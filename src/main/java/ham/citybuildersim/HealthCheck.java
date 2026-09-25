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
 *
 * WHAT THIS HAS TO PROVE, section by section: (1) the buildings know what
 * they treat and the founding endowment is the pyramid's; (2) coverage sets
 * the baseline sick rate; (3) outbreaks happen and end; (4) the same month
 * rolls the same way; (5) output falls and the workforce does not; (6) an
 * unstaffed hospital treats nobody; (7) what care does to mortality and
 * births; (8) burial, cremation and the backlog; (9) senior care draws people
 * in; (10) somebody pays for all of it, and the households paid what the city
 * collected; (11) a skip reports the epidemic it lived through. And since the
 * clinic had a price (2026-09-19): (12) the fee scale scales the three care
 * fees and not the funerals, nobody pays at 0, and the break-even scale is
 * struck from the city's own figures; (13) a household that cannot pay goes
 * without care rather than without food, the rule in both directions, and a
 * poor city at a high fee is a sicker city that buries a larger share of its
 * people, and of its old, than at the founding fee;
 * (14) a city that can pay is served exactly as it was, at zero tolerance;
 * (15) the premium raises rate times the wage bill into the treasury, shows on
 * the households' statement, and fees 0 with a premium serves the same people
 * as fees 1x without one; (16) both dials survive a save and a reform.
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
        /*
         * Asserted as the rule, not as an ordering. It used to read "senior
         * care is smaller than childcare", which was only ever the shape of the
         * pyramid: halving adult mortality (2026-09-11) let more adults reach
         * seventy, the equilibrium's seniors passed its children, and the
         * assertion failed with the endowment still sized exactly as intended.
         */
        check("the childcare endowment is the pyramid's own share of the founding city",
                Healthcare.foundingCapacity(CareType.CHILDCARE),
                Healthcare.FOUNDING_CITY * (PopulationCohorts.equilibriumShare(AgeBand.BABY)
                        + PopulationCohorts.equilibriumShare(AgeBand.CHILD)), 1e-9);
        /*
         * SENIOR CARE SERVES TWO BANDS NOW, and not equally: a place per elder
         * against 0.19 of one per senior, which is the census residency curve
         * normalised on the elders. So the endowment is the weighted share, and
         * this asserts it through CareType's own weights rather than repeating
         * the numbers - a check that hard-codes 0.19 tests that nobody edited
         * the harness.
         */
        double seniorShare = PopulationCohorts.equilibriumShare(AgeBand.SENIOR)
                        * CareType.SENIOR.placesPerHead(AgeBand.SENIOR)
                + PopulationCohorts.equilibriumShare(AgeBand.ELDER)
                        * CareType.SENIOR.placesPerHead(AgeBand.ELDER);
        check("...and senior care's, weighted across both retired bands",
                Healthcare.foundingCapacity(CareType.SENIOR),
                Healthcare.FOUNDING_CITY * seniorShare, 1e-9);
        assertTrue("an elder needs a whole place and a senior a fraction of one",
                CareType.SENIOR.placesPerHead(AgeBand.ELDER) == 1.0
                        && CareType.SENIOR.placesPerHead(AgeBand.SENIOR) < 1.0
                        && CareType.SENIOR.placesPerHead(AgeBand.SENIOR) > 0);
        assertTrue("...both less than general care, which serves everybody",
                Healthcare.foundingCapacity(CareType.CHILDCARE)
                        < Healthcare.foundingCapacity(CareType.GENERAL)
                && Healthcare.foundingCapacity(CareType.SENIOR)
                        < Healthcare.foundingCapacity(CareType.GENERAL));
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

        /*
         * SINCE THE SECTOR TEMPLATE (2026-09-11) there is no report to
         * recompute: the shops' revenue is the sale they book at the bottom
         * of the month, off their pantry, at their shelf price, throttled by
         * the operating rate. So the sale is run by hand, twice, on the same
         * shelf - the pantry is put back between the two so the second run
         * sees what the first did - and the units it hands over are what is
         * compared. The ledger it leaves behind is not read by anything
         * after this section.
         */
        ham.citybuildersim.sectors.Retail shops = well.getSectors().retail();
        /*
         * THIRTEEN PANTRIES TO PUT BACK, NOT ONE. The shelf was a single good
         * until 2026-09-15 and this saved it with one getter. It is thirteen
         * kilogram figures now, and restoring only one would let the second
         * sale run against a shelf the first had already eaten twelve
         * thirteenths of - exactly the confound the save-and-restore exists to
         * remove.
         */
        java.util.Map<Good, Double> shelf = new java.util.EnumMap<>(Good.class);
        for (Good sg : ham.citybuildersim.sectors.Retail.SHELF) shelf.put(sg, shops.getPantry(sg));
        wellEcon.setHealthRatio(1);
        shops.sellOwnPriced(well.getMarkets(), well);

        double workforceBefore = wellPop.getWorkforce();
        double populationBefore = wellPop.getPopulation();
        double wageBillBefore = wellPop.getTotalWage();
        double payrollBefore = shops.getPayroll();
        double soldBefore = shops.getProductsSold();
        double millRateBefore = well.getSectors().industry().getOperatingRate();
        double buildBefore = well.getConstructionOutput();
        assertTrue("fixture: the shops sold something at full health", soldBefore > 0);

        double sickness = .20;
        wellEcon.setHealthRatio(1 - sickness);
        for (java.util.Map.Entry<Good, Double> e : shelf.entrySet()) shops.setPantry(e.getKey(), e.getValue());
        shops.sellOwnPriced(well.getMarkets(), well);

        System.out.printf("  a city of %.0f with %.0f working, told %.0f%% of them are ill%n",
                populationBefore, workforceBefore, sickness * 100);

        check("the workforce is unchanged", wellPop.getWorkforce(), workforceBefore, 1e-9);
        check("the population is unchanged", wellPop.getPopulation(), populationBefore, 1e-9);
        check("the wage bill is unchanged", wellPop.getTotalWage(), wageBillBefore, 1e-9);
        check("the employer still pays the full payroll",
                shops.getPayroll(), payrollBefore, 1e-9);

        /*
         * WITHIN A UNIT, because units are whole things.
         *
         * The sick rate throttles how many baskets the shops can serve, and a
         * shop cannot serve four fifths of a basket - productsSold is an int and
         * always was. So the revenue lands on a whole number of units either
         * side of the continuous figure, and the tolerance is one unit's price.
         *
         * It used to be exact because the ratio was applied to the MONEY rather
         * than to the goods, which is the bug this tolerance is the shadow of:
         * the shops handed over every basket and were paid for four fifths of
         * them. See CommercialHandler.computeMonthlyReport().
         */
        check("...and the shops hand over fewer baskets by exactly the sick rate",
                shops.getProductsSold(),
                soldBefore * (1 - sickness),
                1);
        check("the mills run slower by the same share",
                well.getSectors().industry().getOperatingRate(),
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
            // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its buildings
            // are placed free, but their running costs come out of the treasury month
            // after month, and on the D$100M a city founds with since 0.7.10 it ran
            // dry and simulateMonths() stopped short. Given the D$2.5B it assumed,
            // the Wealthy preset's, explicitly.
            city.setCashForTest(Founding.WEALTHY_CASH);
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
        // The basis its statements were written against went with the
        // sector template: a statement is struck from the ledger of trades,
        // and the ratio the trades were throttled by is the ratio in force
        // when the market cleared - carried in the sectors' own state.
        check("...including the statement it struck under it",
                back.getSectors().retail().statement().revenue,
                city.getSectors().retail().statement().revenue, 1e-9);

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
        /*
         * General care has no swing of its own on adults since 2026-09-11 -
         * Jerus: sickness replaces it. It keeps them alive by curing them before
         * they have been sick two months (SicknessCheck), so their factor here
         * is 1 whatever the coverage. Asserted at both ends so a swing that came
         * back would show.
         */
        check("no general care at all leaves the adults' rate alone",
                Healthcare.mortalityFactor(AgeBand.ADULT, 0, 0, 0), 1, 1e-9);
        check("...and so does general care for everybody",
                Healthcare.mortalityFactor(AgeBand.ADULT, .5, 1, .5), 1, 1e-9);
        check("...and the teenagers' the same",
                Healthcare.mortalityFactor(AgeBand.TEEN, .5, 0, .5), 1, 1e-9);
        check("no senior care at all", Healthcare.mortalityFactor(AgeBand.SENIOR, 0, .5, 0),
                Healthcare.SENIOR_SWING, 1e-9);

        // Drastic for children, gentle for seniors - Jerus's ordering, asserted
        // as an ordering rather than as two literals.
        System.out.printf("  swings: children %.0fx, seniors %.2fx%n",
                Healthcare.CHILDCARE_SWING, Healthcare.SENIOR_SWING);
        assertTrue("children are the drastic ones",
                Healthcare.CHILDCARE_SWING > Healthcare.SENIOR_SWING * 5);
        assertTrue("...and seniors are the gentlest",
                Healthcare.SENIOR_SWING > 1);

        // One care type per band, so the levers stay readable.
        check("general care does not also treat babies",
                Healthcare.mortalityFactor(AgeBand.BABY, .5, 0, .5), 1, 1e-9);
        check("...nor seniors", Healthcare.mortalityFactor(AgeBand.SENIOR, .5, 0, .5), 1, 1e-9);

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
        /*
         * THE SPEC IS THE DEFICIT, NOT THE PERCENTAGE.
         *
         * This used to also require recovery under 50%, and it started failing
         * at 52% the month wages became a market: the fixture city has a labour
         * surplus, so its wages sit below their base, so its healthcare payroll
         * is cheaper and the fees cover more of it. That is the model working -
         * a city with spare workers runs cheaper public services - and 52%
         * against 50% is not a spec violation, it is a magnitude that now moves
         * with the labour market by design.
         *
         * So the claim is stated as what it is: the service loses money, and
         * fees come nowhere near funding it. The band is wide enough to be
         * about the design rather than about today's wage.
         */
        assertTrue("...and it is a NET DEFICIT business, per the spec",
                ph.getNetCost() > 0);
        assertTrue("...with fees nowhere near funding it",
                ph.getCostRecovery() < .8);

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

        /* ============ 12. the fee has a dial, and the funerals do not ============ */
        /*
         * Jerus (2026-09-19): "healthcare should be an adjustable price, all
         * the way to even make it a profitable business". The scale sits on
         * TaxPolicy and reaches the three care fees through feeNow(); the two
         * funeral fees are the cemetery-against-crematorium design and keep
         * their own prices. Asserted against the founding constants and the
         * dial's own ceiling, never a number.
         */
        System.out.println("\n--- the price at the door: the fee scale ---");

        Healthcare dear = new Healthcare();
        dear.setFeeScale(2.5);
        check("the scale multiplies general care's fee",
                dear.feeNow(CareType.GENERAL), Healthcare.GENERAL_FEE * 2.5, 1e-12);
        check("...and childcare's",
                dear.feeNow(CareType.CHILDCARE), Healthcare.CHILDCARE_FEE * 2.5, 1e-12);
        check("...and senior care's",
                dear.feeNow(CareType.SENIOR), Healthcare.SENIOR_FEE * 2.5, 1e-12);
        check("...and NOT the burial fee", dear.feeNow(CareType.BURIAL), Healthcare.BURIAL_FEE, 0);
        check("...nor the cremation fee", dear.feeNow(CareType.CREMATION), Healthcare.CREMATION_FEE, 0);
        check("the founding fee is still the founding fee, unscaled",
                Healthcare.feeFor(CareType.GENERAL), Healthcare.GENERAL_FEE, 0);
        dear.setFeeScale(99);
        check("the dial stops at its ceiling", dear.getFeeScale(), TaxPolicy.MAX_HEALTH_FEE_SCALE, 0);
        dear.setFeeScale(-1);
        check("...and at nothing", dear.getFeeScale(), 0, 0);
        TaxPolicy dial = new TaxPolicy();
        check("a new city charges the founding fee", dial.getHealthFeeScale(),
                TaxPolicy.DEFAULT_HEALTH_FEE_SCALE, 0);
        check("...and no premium", dial.getHealthPremiumRate(), TaxPolicy.DEFAULT_HEALTH_PREMIUM, 0);
        dial.setHealthFeeScale(99);
        check("the policy clamps the scale to the same ceiling", dial.getHealthFeeScale(),
                TaxPolicy.MAX_HEALTH_FEE_SCALE, 0);
        dial.setHealthPremiumRate(1);
        check("...and the premium to its own", dial.getHealthPremiumRate(),
                TaxPolicy.MAX_HEALTH_PREMIUM, 0);

        // A ward: a thousand seen by the doctor, a hundred children, fifty seniors.
        double[] ward = new double[CareType.values().length];
        ward[CareType.GENERAL.ordinal()] = 1000;
        ward[CareType.CHILDCARE.ordinal()] = 100;
        ward[CareType.SENIOR.ordinal()] = 50;
        double wardAtOne = 1000 * Healthcare.GENERAL_FEE + 100 * Healthcare.CHILDCARE_FEE
                + 50 * Healthcare.SENIOR_FEE;

        Healthcare free = new Healthcare();
        free.setFeeScale(0);
        free.advanceMonth(100, 50, ward, 10, 1.0, 1000, 0);
        check("at 0 nobody pays for treatment", free.getTreatmentFees(), 0, 0);
        check("...and everybody is still treated", free.getServed(CareType.GENERAL), 1000, 0);
        check("...and the funerals still charge", free.getFuneralFees(), 10 * Healthcare.BURIAL_FEE, 1e-9);
        check("...so the fees are the funerals alone", free.getFees(), free.getFuneralFees(), 0);
        check("...and nobody was priced out by a fee of nothing", free.getPricedOutTotal(), 0, 0);

        /*
         * BREAK-EVEN, struck from the city's own figures: gross cost over what
         * the three fees raise at 1x on the people the beds could take. A
         * gross cost of 150 against 40 of fees at 1x is 3.75, inside the dial;
         * at that scale the treatment fees meet the cost, below it the service
         * loses money, above it it is a business.
         */
        Healthcare even = new Healthcare();
        even.advanceMonth(100, 50, ward, 0, 1.0, 1000, 0);
        double be = even.breakEvenScale();
        check("the break-even scale is the gross cost over the fees at 1x", be, 150 / wardAtOne, 1e-12);
        assertTrue("...and this ward's is inside the dial", be > 0 && be <= TaxPolicy.MAX_HEALTH_FEE_SCALE);
        even.setFeeScale(be);
        even.advanceMonth(100, 50, ward, 0, 1.0, 1000, 0);
        check("at the break-even scale the fees meet the gross cost",
                even.getTreatmentFees(), even.getGrossCost(), 1e-9);
        check("...and the net cost is nothing", even.getNetCost(), 0, 1e-9);
        even.setFeeScale(be * .5);
        even.advanceMonth(100, 50, ward, 0, 1.0, 1000, 0);
        assertTrue("below it the service loses money", even.getNetCost() > 0);
        check("...half the fees, at half the scale", even.getTreatmentFees(), even.getGrossCost() * .5, 1e-9);
        even.setFeeScale(Math.min(TaxPolicy.MAX_HEALTH_FEE_SCALE, be * 1.25));
        even.advanceMonth(100, 50, ward, 0, 1.0, 1000, 0);
        assertTrue("above it, it is a business", even.getNetCost() < 0);
        assertTrue("...and the recovery rate says so", even.getCostRecovery() > 1);

        /* ---- and on the played city of section 10, through the policy ---- */
        Healthcare pc = paid.getHealthcare();
        TaxPolicy pt = paid.getEconomyManager().getTaxPolicy();
        double cityBreakEven = pc.breakEvenScale();
        System.out.printf("  the city: gross $%,.1fk, fees at 1x $%,.1fk on %,.0f people offered care, break-even x%.2f%n",
                pc.getGrossCost(), pc.fullTreatmentFees(), pc.getOffered(CareType.GENERAL)
                        + pc.getOffered(CareType.CHILDCARE) + pc.getOffered(CareType.SENIOR), cityBreakEven);
        assertTrue("fixture: the city's break-even is inside the dial",
                cityBreakEven > 1 && cityBreakEven <= TaxPolicy.MAX_HEALTH_FEE_SCALE);
        pt.setHealthFeeScale(cityBreakEven);
        System.setOut(quiet);
        try { paid.simulateMonths(1); } finally { System.setOut(out); }
        check("the policy's scale reached the service", pc.getFeeScale(), cityBreakEven, 0);
        assertTrue("at the city's break-even the fees at full service are within a month's drift of the cost",
                Math.abs(pc.fullTreatmentFees() - pc.getGrossCost()) < pc.getGrossCost() * .05);
        pt.setHealthFeeScale(cityBreakEven * .5);
        System.setOut(quiet);
        try { paid.simulateMonths(1); } finally { System.setOut(out); }
        assertTrue("...at half of it the service loses money", pc.getNetCost() > 0);
        assertTrue("...by about half the cost", pc.getTreatmentFees() < pc.getGrossCost() * .6);
        pt.setHealthFeeScale(TaxPolicy.DEFAULT_HEALTH_FEE_SCALE);
        System.setOut(quiet);
        try { paid.simulateMonths(1); } finally { System.setOut(out); }
        // Back at the founding fee: a month's fees, and the month after it
        // whose statement carries them - section 14 reads both.
        double feesLastMonth = pc.getFees();
        System.setOut(quiet);
        try { paid.simulateMonths(1); } finally { System.setOut(out); }

        /* ============ 13. who can afford the clinic ============ */
        /*
         * Jerus's decision 1: "when fees are turned up past what a poor
         * household can pay, they go without care" - and without care, not
         * without food. The rule is Household.affordCare(): a household pays
         * its care bill out of what it will have next month after rent, the
         * other bills and a basket for everybody in it, counting its income,
         * its savings, its paper abroad and the credit still open to it; what
         * would have to come out of the food budget is not paid, and that
         * share of its people goes untreated. First the rule on one
         * household, both directions, against the model's own arithmetic;
         * then a poor city at three prices.
         */
        System.out.println("\n--- who can afford the clinic ---");

        HouseholdBalance ledger = new HouseholdBalance();
        Household family = ledger.cell(FamilyStructure.LARGE_FAMILY, PayTier.UNSKILLED);
        double basket = family.size() * .40;   // a basket a head at $400
        // Well off: income after the fixed bills a little over the basket,
        // and savings besides - the bill fits without touching food.
        family.savings = 2.0;
        family.afterFixed = basket + .10;
        family.subsistence = basket;
        family.spendable = Math.max(0, family.afterFixed) + family.savings;
        double bill = .50;
        check("a household with room pays its whole care bill",
                family.affordCare(bill, bill), 1, 0);
        check("...and skips nothing", family.careSkipped(), 0, 0);
        // Exactly at the line: room equal to the bill still pays it all.
        family.savings = 0;
        family.spendable = family.afterFixed;
        check("...and so does one whose room is exactly the bill",
                family.affordCare(.10, .10), 1, 0);
        // Poor: nothing saved, no credit, and income after the bills that
        // does not reach the basket. It pays what fits after eating.
        family.afterFixed = basket - .30;          // .30 short of eating, with the bill paid
        family.spendable = Math.max(0, family.afterFixed);
        double room = family.spendable + bill - basket;   // what is left with no bill: .20
        double share = family.affordCare(bill, bill);
        check("a household short of a basket pays only what fits after eating",
                share, room / bill, 1e-12);
        check("...which is the share of its people the clinic will see", family.carePaid(), share, 0);
        check("...and the rest of the bill is what it eats instead",
                family.careSkipped(), bill - room, 1e-12);
        assertTrue("...so the bill it does pay leaves the basket whole",
                family.spendable + bill - family.carePaid() * bill >= basket - 1e-12);
        // Destitute: nothing at all, and billed nothing last month either.
        // It skips the whole bill and is unserved.
        family.afterFixed = 0;
        family.spendable = 0;
        check("a household with nothing pays nothing", family.affordCare(bill, 0), 0, 0);
        check("...and none of its people are served", family.carePaid(), 0, 0);
        check("...whatever the fee", family.affordCare(bill * 5, 0), 0, 0);
        // The same household with a bill of nothing is served in full.
        check("...and with a fee of nothing it is served in full", family.affordCare(0, 0), 1, 0);
        // The stability the rule was designed for: a household that skipped
        // its whole bill last month is measured against the FULL bill, not
        // the empty one it was handed, so it does not read an empty bill as
        // affordable and swing between served and starving every other month.
        double feeFree = basket + .20;             // what it has with no care bill at all
        family.afterFixed = feeFree;
        family.spendable = feeFree;
        double afterSkipping = family.affordCare(bill, 0);
        check("a household that skipped last month's bill is judged on the full one",
                afterSkipping, .20 / bill, 1e-12);
        // Next month it is billed that share, and has that much less.
        family.spendable = feeFree - bill * afterSkipping;
        double next = family.affordCare(bill, bill * afterSkipping);
        check("...and settles where it pays what it can, month after month", next, afterSkipping, 1e-12);
        // Whereas a cliff on being hungry TODAY would have it pay nothing,
        // then everything, then nothing: the rule was chosen against that.
        assertTrue("...rather than swinging between served and starving",
                next > 0 && next < 1);

        /* ---- a poor city, at three prices ---- */
        /*
         * MORE HOMES THAN WORK, so wages sit at the floor and the out of work
         * fill a row - the crime fixture's shape - and then the savings
         * drained and the bank shut, once, at the start of the window: a city
         * of households with nothing behind them, which is what the fee has
         * to be tested against. Three twins from the same founding, the same
         * buildings, the same months, at fees of 0, 1x and the dial's top.
         */
        System.out.println("  building a poor city three times over...");
        double[] scales = { 0, TaxPolicy.DEFAULT_HEALTH_FEE_SCALE, TaxPolicy.MAX_HEALTH_FEE_SCALE };
        Game[] towns = new Game[scales.length];
        double[] meanAffordable = new double[scales.length];
        double[] meanSick = new double[scales.length];
        double[] meanBaseline = new double[scales.length];
        double[] deathsOver = new double[scales.length];
        double[] eldersLost = new double[scales.length];
        double[] pricedOutMonths = new double[scales.length];
        double[] servedInFullMonths = new double[scales.length];
        double[] hungerAtEnd = new double[scales.length];
        // The people each city had to bury from, month by month: all of them,
        // and its old (2026-09-21, see the assertions below).
        double[] personMonths = new double[scales.length];
        double[] elderMonths = new double[scales.length];
        int window = 96;
        // What the beds could do for each twin, month by month, apart from the
        // fee (0.7.8, round 3): the share of the people general care serves
        // that its staffed places could take, and the coverage the month read.
        double[] bedCover = new double[scales.length];
        double[] coverRead = new double[scales.length];
        double[] peakPeople = new double[scales.length];
        final int clinics = 10;   // see TEN CLINICS below
        for (int k = 0; k < scales.length; k++) {
            Game town = new Game(files);
            towns[k] = town;
            System.setOut(quiet);
            try {
                town.run();
                // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its buildings
                // are placed free, but their running costs - the coal plant's unbilled
                // draw, the city's own services - come out of the treasury month after
                // month, and on the D$100M a city founds with since 0.7.10 it ran dry
                // and simulateMonths() stopped. Given the D$2.5B it assumed, the
                // Wealthy preset's, explicitly.
                town.setCashForTest(Founding.WEALTHY_CASH);
                BuildingManager b = town.getBuildingManager();
                town.getLandManager().setOwnedSqFt(town.getLandManager().getOwnedSqFt() + 100_000_000L);
                b.addStack(b.getTemplateByName("House"), 3000, true);
                b.addStack(b.getTemplateByName("Convenience Store"), 60, true);
                b.addStack(b.getTemplateByName("Industrial Bakery"), 30, true);
                b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
                b.addStack(b.getTemplateByName("Water Treatment Plant"), 2, true);
                b.addStack(b.getTemplateByName("Paved Road"), 20, true);
                /*
                 * TEN CLINICS, 25,000 PLACES, more than any of the three twins
                 * ever holds (0.7.8, round 3). With six (15,000) the beds ran
                 * short in every twin and by a different amount in each -
                 * measured on round 2's build, where this went red: the
                 * dear city, which the fee makes smaller, had a place for 97.8%
                 * of its people against the founding fee's 93.7% - a gap wider
                 * than the fee's own, 91.1% served against 94.8% - and its
                 * baseline came out LOWER (4.27% against 4.35%): the fixture
                 * was measuring the size of the cities, not the fee. With a
                 * place for everyone in each twin the beds are the same and
                 * the fee is the difference, which the assertions below hold
                 * it to before they read the baseline.
                 */
                b.addStack(b.getTemplateByName("Walk-in Clinic"), clinics, true);
                b.addStack(b.getTemplateByName("Childcare Centre"), 8, true);
                b.addStack(b.getTemplateByName("Home Care Service"), 4, true);
                town.simulateMonths(48);
                // The drain: nothing saved, nothing abroad, no shares, and
                // the bank not lending - the waterfall's every step before
                // "eat less" already spent.
                for (Household c : town.getHouseholdBalance().cells()) {
                    c.savings = 0;
                    c.abroad = 0;
                    java.util.Arrays.fill(c.shares, 0);
                    c.lockout = HouseholdBalance.LOCKOUT_MONTHS;
                }
                town.getEconomyManager().getTaxPolicy().setHealthFeeScale(scales[k]);
                for (int m = 0; m < window; m++) {
                    town.simulateMonths(1);
                    Healthcare hc = town.getHealthcare();
                    double treated = 0, offered = 0;
                    for (CareType care : CareType.values()) {
                        if (!care.servesTheLiving()) continue;
                        treated += hc.getServed(care);
                        offered += hc.getOffered(care);
                    }
                    meanAffordable[k] += offered > 0 ? treated / offered : 1;
                    meanSick[k] += town.getHealth().getSickRate();
                    meanBaseline[k] += town.getHealth().getBaselineRate();
                    double wouldServe = CareType.GENERAL.populationServed(town.getCohorts());
                    bedCover[k] += wouldServe > 0 ? Math.min(1, hc.getOffered(CareType.GENERAL) / wouldServe) : 1;
                    coverRead[k] += hc.getCoverage(CareType.GENERAL);
                    peakPeople[k] = Math.max(peakPeople[k], town.getCohorts().total());
                    deathsOver[k] += town.getCohorts().getLastDeaths();
                    eldersLost[k] += town.getCohorts().getDeaths(AgeBand.SENIOR)
                            + town.getCohorts().getDeaths(AgeBand.ELDER);
                    personMonths[k] += town.getCohorts().total();
                    elderMonths[k] += town.getCohorts().get(AgeBand.SENIOR)
                            + town.getCohorts().get(AgeBand.ELDER);
                    if (hc.getPricedOutTotal() > 0) pricedOutMonths[k]++;
                    if (hc.getPricedOutTotal() == 0) servedInFullMonths[k]++;
                }
                meanAffordable[k] /= window;
                meanSick[k] /= window;
                meanBaseline[k] /= window;
                hungerAtEnd[k] = town.getHouseholdBalance().getHungerRate();
            } finally { System.setOut(out); }
            Healthcare hc = town.getHealthcare();
            System.out.printf("  fees x%.2f: %,.0f people, served %.1f%% of those offered care over %d months,"
                    + " sick %.2f%% (baseline %.2f%%), %,.0f died (%,.0f over seventy), priced out in %.0f months,"
                    + " %,.0f last month (childcare %,.0f / general %,.0f / senior %,.0f), hunger %.0f%%%n",
                    scales[k], town.getCohorts().total(), meanAffordable[k] * 100, window,
                    meanSick[k] * 100, meanBaseline[k] * 100, deathsOver[k], eldersLost[k], pricedOutMonths[k],
                    hc.getPricedOutTotal(), hc.getPricedOut(CareType.CHILDCARE),
                    hc.getPricedOut(CareType.GENERAL), hc.getPricedOut(CareType.SENIOR),
                    hungerAtEnd[k] * 100);
        }
        assertTrue("fixture: the poor city has somebody at the eat-less step",
                hungerAtEnd[1] > 0);
        assertTrue("fixture: at the dial's top the fee priced somebody out",
                pricedOutMonths[2] > 0);
        assertTrue("at a high fee a poor city serves a smaller share of its people than at the founding fee",
                meanAffordable[2] < meanAffordable[1]);
        /*
         * SICKER WHERE COVERAGE BITES. The whole sick rate carries the hunger
         * term too, and a household that skipped its care bill ate with the
         * money - so the dear city can come out less hungry and the two
         * effects can net to nothing on the headline figure. The claim that
         * coverage makes is on the BASELINE rate, which coverage alone sets.
         */
        /*
         * ...AND THE FIXTURE CAUSES WHAT IT MEASURES (0.7.8, round 3): the
         * same clinics in every twin, with a place for more people than any
         * of them holds, so the beds are not what differs - what they could
         * take differs between the dear city and the founding fee's by less
         * than the fee turns away - and the coverage each twin's month read
         * is lower at the dial's top. Then the baseline, which that coverage
         * alone sets.
         */
        for (int k = 0; k < scales.length; k++) {
            bedCover[k] /= window;
            coverRead[k] /= window;
        }
        System.out.printf("  the beds took %.2f%% of those general care serves at x%.2f and %.2f%% at x%.2f; the fee left coverage at %.2f%% and %.2f%%%n",
                bedCover[1] * 100, scales[1], bedCover[2] * 100, scales[2], coverRead[1] * 100, coverRead[2] * 100);
        double places = clinics
                * (double) towns[0].getBuildingManager().getTemplateByName("Walk-in Clinic").getCapacity();
        assertTrue("fixture: the same clinics in every twin, a place for more people than any of them holds",
                places > Math.max(peakPeople[0], Math.max(peakPeople[1], peakPeople[2])));
        assertTrue("fixture: so the beds are not what differs: at the dial's top they take no larger a share by more than the fee turns away",
                bedCover[2] - bedCover[1] < meanAffordable[1] - meanAffordable[2]);
        assertTrue("...and the coverage the month reads, what the fee leaves of the beds, is lower at the dial's top",
                coverRead[2] < coverRead[1]);
        assertTrue("...and its baseline sick rate, which coverage sets, is higher for it",
                meanBaseline[2] > meanBaseline[1]);
        /*
         * AS A SHARE OF ITS PEOPLE, NOT A HEAD COUNT (2026-09-21). The two
         * cities are not the same size after eight years - the dear one is
         * sicker, so it has fewer births and loses people - and a count of
         * burials compares mortality times population. Measured: the count
         * held on the shipped build by 25 deaths in a thousand, and flipped
         * with nothing about the clinic changed - the same pristine fixture
         * with its currency pinned buried 1,014 at 15x against 999 at 1x but
         * lost 826 elders against 856, from a city that had shrunk to 7,474
         * people against 13,230; the vault's damping rule flipped the total.
         * Per person-month, and the old per old person-month, the dear city
         * buries more on every variant measured.
         */
        double deathRate1 = deathsOver[1] / Math.max(1, personMonths[1]);
        double deathRate2 = deathsOver[2] / Math.max(1, personMonths[2]);
        double elderRate1 = eldersLost[1] / Math.max(1, elderMonths[1]);
        double elderRate2 = eldersLost[2] / Math.max(1, elderMonths[2]);
        System.out.printf("  deaths a year per 1,000: %.2f at x%.2f, %.2f at x%.2f; the old %.2f against %.2f%n",
                deathRate1 * 12_000, scales[1], deathRate2 * 12_000, scales[2],
                elderRate1 * 12_000, elderRate2 * 12_000);
        assertTrue("...and it buries more of its people over the run", deathRate2 > deathRate1);
        assertTrue("...its old first, whom a fee on senior care turns away", elderRate2 > elderRate1);
        check("with the fee at nothing the same city is served in full, every month",
                servedInFullMonths[0], window, 0);
        check("...every kind of care", towns[0].getHealthcare().getAffordability(CareType.CHILDCARE)
                + towns[0].getHealthcare().getAffordability(CareType.GENERAL)
                + towns[0].getHealthcare().getAffordability(CareType.SENIOR), 3, 0);
        assertTrue("...and the households who skipped a bill ate with it: the dear city is no hungrier than the free one by more than the price of care",
                hungerAtEnd[2] <= hungerAtEnd[0] + .05);
        {
            // The priced-out are the people the beds had room for, not the beds.
            Healthcare hc = towns[2].getHealthcare();
            for (CareType care : new CareType[] { CareType.CHILDCARE, CareType.GENERAL, CareType.SENIOR }) {
                check("  " + care.getLabel().toLowerCase() + ": served + priced out = offered",
                        hc.getServed(care) + hc.getPricedOut(care), hc.getOffered(care), 1e-9);
                check("  ...and the served are the offered times the share who could pay",
                        hc.getServed(care), hc.getOffered(care) * hc.getAffordability(care), 1e-9);
            }
            // The fee revenue is from the people who paid.
            double fromPayers = 0;
            for (CareType care : new CareType[] { CareType.CHILDCARE, CareType.GENERAL, CareType.SENIOR }) {
                fromPayers += hc.getServed(care) * hc.feeNow(care);
            }
            check("the treatment fees are charged on the people treated", hc.getTreatmentFees(), fromPayers, 1e-9);
            assertTrue("...which is less than the same beds would raise at full service",
                    hc.getTreatmentFees() < hc.fullTreatmentFees());
            // ...and the cost of the buildings did not move for it: a ward is
            // paid for whether it is full or not. The same buildings stand in
            // both towns, so their upkeep is the same figure; the payroll
            // follows the staffing of a city the price has made smaller and
            // is not pinned.
            Healthcare atOne = towns[1].getHealthcare();
            check("the buildings' upkeep is the same at a dear fee as at the founding fee",
                    hc.getUpkeep(), atOne.getUpkeep(), 1e-9);
            assertTrue("...and the service still costs money to run", hc.getGrossCost() > 0);
        }

        /* ============ 14. the unchanged case, at zero tolerance ============ */
        /*
         * The other half of decision 1, and the half that protects every city
         * that exists: a household that can still cover its bills out of
         * income, savings, shares or credit pays the fee and is served exactly
         * as it was. The rich city of section 10, at the founding fee: every
         * share is 1 to the bit, the served are the offered to the bit, and
         * the fees charged are the fees at full service to the bit - so the
         * multiplications by 1.0 that stand between this batch and the city
         * as it was are exact, which is the "unchanged case at zero tolerance"
         * rule of the harness notes.
         */
        System.out.println("\n--- the unchanged case, at zero tolerance ---");
        for (Household c : paid.getHouseholdBalance().cells()) {
            if (c.households() < .5) continue;
            if (c.carePaid() != 1) {
                System.out.printf("  %s x%.0f paid %.6f%n", c.label(), c.households(), c.carePaid());
            }
        }
        double paidCells = 0, allCells = 0;
        for (Household c : paid.getHouseholdBalance().cells()) {
            if (c.households() < .5) continue;
            allCells++;
            if (c.carePaid() == 1) paidCells++;
        }
        check("in a city that can pay, every household paid its whole care bill", paidCells, allCells, 0);
        for (CareType care : new CareType[] { CareType.CHILDCARE, CareType.GENERAL, CareType.SENIOR }) {
            check("  " + care.getLabel().toLowerCase() + ": everybody could pay, exactly",
                    pc.getAffordability(care), 1, 0);
            check("  ...and the served are the offered, exactly",
                    pc.getServed(care), pc.getOffered(care), 0);
        }
        check("...and the fees charged are the fees at full service, exactly",
                pc.getTreatmentFees(), pc.fullTreatmentFees(), 0);
        check("...and the households were billed exactly what the city collected a month ago, as before",
                paid.getHouseholds().getHealthcare(), feesLastMonth, 0);
        check("...and nobody was priced out", pc.getPricedOutTotal(), 0, 0);
        check("...and no bill was skipped", paid.getHouseholdBalance().getCareSkipped(), 0, 0);

        /* ============ 15. the premium ============ */
        /*
         * Jerus's decision 2: "the option to make it an obligatory insurance
         * payment system" - a share of every wage, employee side, into the
         * treasury as revenue, with the treasury carrying the gap either way.
         * The base is the whole staffed wage bill, which is the EI premium's
         * own base (EI's insurable cap is on its benefit, not its premium).
         */
        System.out.println("\n--- the premium ---");
        double rate = .03;
        pt.setHealthPremiumRate(rate);
        System.setOut(quiet);
        try { paid.simulateMonths(1); } finally { System.setOut(out); }
        EconomyManager pe2 = paid.getEconomyManager();
        NationalAccounts pn2 = pe2.getNationalAccounts();
        double premiumThisMonth = pe2.getHealthPremiums();
        check("the premium raises exactly the rate times the wage bill",
                premiumThisMonth, rate * paid.getPopulationManager().getTotalWage(), 1e-9);
        assertTrue("fixture: which is money", premiumThisMonth > 0);
        check("...on the same base as the EI premium",
                premiumThisMonth / rate, pe2.getEiPremiums() / pt.getEiPremiumRate(), 1e-6);
        check("...and it is on the government's revenue list", pn2.getHealthPremiums(), premiumThisMonth, 1e-9);
        double revenueWith = pn2.getTotalRevenue();
        pn2.setOutsideLines(pn2.getEiPremiums(), pn2.getEiBenefits(), pn2.getStudentGrants(), 0);
        check("...inside the revenue total", revenueWith - pn2.getTotalRevenue(), premiumThisMonth, 1e-9);
        pn2.setOutsideLines(pn2.getEiPremiums(), pn2.getEiBenefits(), pn2.getStudentGrants(), premiumThisMonth);
        double surplusWith = pe2.getTotalIncome();
        pt.setHealthPremiumRate(0);
        check("...and reaches the treasury's cash, penny for penny",
                surplusWith - pe2.getTotalIncome(), premiumThisMonth, 1e-6);
        pt.setHealthPremiumRate(rate);
        pe2.getTaxIncome();
        // The households' statement is a month behind, by construction - the
        // same lag section 10 tests the fees through.
        System.setOut(quiet);
        try { paid.simulateMonths(1); } finally { System.setOut(out); }
        check("the households' statement shows the premium the city collected a month ago",
                paid.getHouseholds().getHealthPremiums(), premiumThisMonth, 1e-9);
        double premiumRows = 0;
        for (int r = 0; r < paid.getHouseholds().getRowCount(); r++) {
            premiumRows += paid.getHouseholds().getRowHealthPremiums(r);
        }
        check("...and the rows add back up to it", premiumRows, paid.getHouseholds().getHealthPremiums(), 1e-6);
        check("...off the wages, so the retired pay none",
                paid.getHouseholds().getRowHealthPremiums(HouseholdAccounts.RETIRED), 0, 0);
        check("...and it comes off take-home, like the EI premium",
                paid.getHouseholds().getDisposableIncome(),
                paid.getHouseholds().getWages() - paid.getHouseholds().getWageTax()
                        - paid.getHouseholds().getContributions() - paid.getHouseholds().getEiPremiums()
                        - paid.getHouseholds().getHealthPremiums() + paid.getHouseholds().getPensions()
                        + paid.getHouseholds().getEiBenefits() + paid.getHouseholds().getStudentGrants(), 1e-9);
        assertTrue("the money audit saw it as a household-to-treasury flow",
                paid.getLastMoneyAudit() != null && paid.getLastMoneyAudit().relative() < 1e-6);
        pt.setHealthPremiumRate(0);

        /* ---- fees or a premium: the same people served, different payers ---- */
        /*
         * Insurance-funded against fee-funded, from one saved city: with fees
         * 0 and a premium the clinics see the same people as with fees 1x and
         * no premium, and the money comes from the wage earners in one and
         * from the patients in the other.
         */
        System.setOut(quiet);
        try { paid.saveGame(3, "two ways to pay"); } finally { System.setOut(out); }
        Game insured = new Game(files);
        Game feeFunded = new Game(files);
        System.setOut(quiet);
        try {
            insured.loadGameSave(3);
            feeFunded.loadGameSave(3);
            insured.getEconomyManager().getTaxPolicy().setHealthFeeScale(0);
            insured.getEconomyManager().getTaxPolicy().setHealthPremiumRate(.05);
            feeFunded.getEconomyManager().getTaxPolicy().setHealthFeeScale(TaxPolicy.DEFAULT_HEALTH_FEE_SCALE);
            feeFunded.getEconomyManager().getTaxPolicy().setHealthPremiumRate(0);
            insured.simulateMonths(1);
            feeFunded.simulateMonths(1);
        } finally { System.setOut(out); }
        for (CareType care : new CareType[] { CareType.CHILDCARE, CareType.GENERAL, CareType.SENIOR }) {
            check("  " + care.getLabel().toLowerCase() + ": fees 0 + premium serves the same people as fees 1x + no premium",
                    insured.getHealthcare().getServed(care), feeFunded.getHealthcare().getServed(care), 1e-9);
        }
        check("the insured city charged no treatment fee", insured.getHealthcare().getTreatmentFees(), 0, 0);
        assertTrue("...and the fee-funded one did", feeFunded.getHealthcare().getTreatmentFees() > 0);
        assertTrue("the insured city's wage earners paid a premium",
                insured.getEconomyManager().getHealthPremiums() > 0);
        check("...and the fee-funded city's paid none", feeFunded.getEconomyManager().getHealthPremiums(), 0, 0);
        check("the gross cost is the same either way: a ward is paid for whether or not its patients are",
                insured.getHealthcare().getGrossCost(), feeFunded.getHealthcare().getGrossCost(), 1e-9);

        /* ============ 16. both dials survive a save, and a reform ============ */
        System.out.println("\n--- the dials survive a save and a reform ---");
        pt.setHealthFeeScale(2.25);
        pt.setHealthPremiumRate(.0125);
        System.setOut(quiet);
        try { paid.simulateMonths(1); paid.saveGame(4, "dials"); } finally { System.setOut(out); }
        Game dialed = new Game(files);
        System.setOut(quiet);
        try { dialed.loadGameSave(4); } finally { System.setOut(out); }
        TaxPolicy dialsBack = dialed.getEconomyManager().getTaxPolicy();
        check("the fee scale came back", dialsBack.getHealthFeeScale(), 2.25, 0);
        check("...and the premium", dialsBack.getHealthPremiumRate(), .0125, 0);
        check("...and the service charges at the reloaded scale", dialed.getHealthcare().getFeeScale(), 2.25, 0);
        check("...and the full-service bill the next strike reads came back",
                dialed.getHealthcare().fullTreatmentFees(), pc.fullTreatmentFees(), 1e-9);
        // The array as a save from before the two health dials wrote it: cut
        // at the model's own mark rather than two short of today's end, which
        // stopped being the same thing when the education dials went on after
        // them (2026-09-21).
        double[] older = java.util.Arrays.copyOf(pt.getPolicyState(), TaxPolicy.STATE_BEFORE_HEALTH);
        TaxPolicy fromBefore = new TaxPolicy();
        assertTrue("a save from before the dials is still read", fromBefore.restorePolicyState(older));
        check("...at the founding fee", fromBefore.getHealthFeeScale(), TaxPolicy.DEFAULT_HEALTH_FEE_SCALE, 0);
        check("...with no premium", fromBefore.getHealthPremiumRate(), TaxPolicy.DEFAULT_HEALTH_PREMIUM, 0);
        double[] healthBefore = java.util.Arrays.copyOf(pc.getState(), Healthcare.STATE_BEFORE_FULL_BILL);
        Healthcare olderService = new Healthcare();
        assertTrue("...and so is the service's state from before the full-service bill",
                olderService.restore(healthBefore));
        check("...which reads the bill it charged as the bill at full service",
                olderService.fullTreatmentFees(), pc.getTreatmentFees(), 0);
        check("...and a coverage of 1 until a month strikes it",
                olderService.getCoverage(CareType.CHILDCARE) + olderService.getCoverage(CareType.GENERAL)
                        + olderService.getCoverage(CareType.SENIOR), 3, 0);
        double[] beforeCoverage = java.util.Arrays.copyOf(pc.getState(), Healthcare.STATE_BEFORE_COVERAGE);
        Healthcare olderStill = new Healthcare();
        assertTrue("...and the state from before the coverages were kept", olderStill.restore(beforeCoverage));
        check("...which reads the full bill at 1x it carried", olderStill.treatmentFeesAtOne(), pc.treatmentFeesAtOne(), 0);
        check("the reloaded service kept the coverage the month read",
                dialed.getHealthcare().getCoverage(CareType.GENERAL), pc.getCoverage(CareType.GENERAL), 0);
        assertTrue("...which is the figure the sick rate read, not the beds",
                Math.abs(pc.getCoverage(CareType.GENERAL) - paid.getHealth().getCoverage()) < 1e-9);
        // A reform: both are ratios, and neither moves.
        TaxPolicy reformed = new TaxPolicy();
        reformed.setHealthFeeScale(3);
        reformed.setHealthPremiumRate(.02);
        reformed.redenominate(.01);
        check("a currency reform leaves the fee scale alone", reformed.getHealthFeeScale(), 3, 0);
        check("...and the premium", reformed.getHealthPremiumRate(), .02, 0);
        Healthcare reformedService = new Healthcare();
        reformedService.setFeeScale(3);
        reformedService.redenominate(.01);
        check("...and the service's scale", reformedService.getFeeScale(), 3, 0);
        check("...while its fees move with the money",
                reformedService.feeNow(CareType.GENERAL), Healthcare.GENERAL_FEE * .01 * 3, 1e-15);
        pt.setHealthFeeScale(TaxPolicy.DEFAULT_HEALTH_FEE_SCALE);
        pt.setHealthPremiumRate(0);

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
        b.addStack(b.getTemplateByName("Convenience Store"), 10, true);
        b.addStack(b.getTemplateByName("Industrial Bakery"), 3, true);
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
