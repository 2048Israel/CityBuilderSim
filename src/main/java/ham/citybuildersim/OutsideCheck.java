package ham.citybuildersim;

import java.util.Locale;

/**
 * The people outside the families: the out of work, the students, the
 * unhoused and the orphans (2026-09-11).
 *
 * Jerus: "we are to add a new household structure called unemployed... these
 * will just sum up by age the unemployed, or unhoused... and they will have
 * their own cashflow and stuff." Twenty questions answered; this is every one
 * of the answers that can be caused and measured, each with the cause set
 * by the fixture rather than stood next to. See
 * claude/the-people-the-books-left-out.md.
 */
public class OutsideCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-72s %12.4f  expected %12.4f  %s%n", label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate t(Game g, String name) {
        for (BuildingsTemplate b : g.getBuildingManager().getTemplates()) {
            if (b.getName().equals(name)) return b;
        }
        throw new IllegalArgumentException("no template " + name);
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.CANADA);
        int tiers = PayTier.values().length;
        int unskilled = PayTier.UNSKILLED.ordinal();
        int skilled = PayTier.SKILLED.ordinal();

        /* ============ 1. a student is not unemployed ============ */
        System.out.println("--- the students come out of the count ---");
        {
            PopulationManager pm = new PopulationManager();
            pm.setWagesPerType();
            int[] posts = new int[JobType.values().length];
            posts[JobType.NO_DIPLOMA.ordinal()] = 50;
            pm.updateJobs(posts);
            pm.applyPopulation(200, 100);
            double[] studying = new double[WageBand.values().length];
            studying[WageBand.NONE.ordinal()] = 10;
            pm.setStudying(studying);
            // The cause: a hundred adults, ten of them in a lecture theatre, fifty posts.
            check("the labour force is the workforce less the students", pm.getLabourForce(), 90, 1e-9);
            check("...and the unemployed are the labour force less the filled posts",
                    pm.getUnemployed(), 40, 0);
            check("...at a rate over the labour force, not the workforce",
                    pm.getUnemploymentRate(), 40.0 / 90, 1e-9);
        }

        /* ============ 2. the families are the people who work ============ */
        System.out.println("\n--- families from working adults, and the children nobody took ---");
        {
            PopulationCohorts c = new PopulationCohorts();
            double[] pyr = new double[AgeBand.values().length + 3];
            for (AgeBand b : AgeBand.values()) pyr[b.ordinal()] = 18_000 * PopulationCohorts.equilibriumShare(b);
            c.restore(pyr);
            double[] jobs = new double[tiers];
            jobs[unskilled] = 700; jobs[skilled] = 300;

            FamilyModel all = new FamilyModel();
            all.rebuild(c, jobs);
            FamilyModel workers = new FamilyModel();
            double outside = 1_500;
            workers.rebuild(c, jobs, outside);

            double adults = 0;
            for (FamilyStructure s : FamilyStructure.values()) adults += workers.totalOf(s) * s.membersOf(AgeBand.ADULT);
            check("the families hold every adult but the ones outside them",
                    adults, c.get(AgeBand.ADULT) - outside, 1e-6);
            check("...and the builder records who it left out", workers.getOutsideAdults(), outside, 1e-9);

            double placedBabies = 0, placedTeens = 0;
            for (FamilyStructure s : FamilyStructure.values()) {
                placedBabies += all.totalOf(s) * s.membersOf(AgeBand.BABY);
                placedTeens += all.totalOf(s) * s.membersOf(AgeBand.TEEN);
            }
            check("the orphans are exactly the babies no family took",
                    all.getOrphans(AgeBand.BABY), c.get(AgeBand.BABY) - placedBabies, 1e-6);
            check("...and the teens", all.getOrphans(AgeBand.TEEN), c.get(AgeBand.TEEN) - placedTeens, 1e-6);
            assertTrue("in an equilibrium city at full employment, there are some (Jerus: \"they're the orphans\")",
                    all.getOrphansTotal() > 100);
            check("no adult is ever an orphan", all.getOrphans(AgeBand.ADULT), 0, 0);
        }

        /* ============ 3. the out of work share doors with their own kind ============ */
        System.out.println("\n--- each with its own kind ---");
        {
            PopulationCohorts c = new PopulationCohorts();
            double[] pyr = new double[AgeBand.values().length + 3];
            pyr[AgeBand.ADULT.ordinal()] = 1_000;
            c.restore(pyr);
            double[] jobs = new double[tiers];
            jobs[unskilled] = 800;
            FamilyModel f = new FamilyModel();
            f.rebuild(c, jobs, 200);
            f.setSeekers(200, 0);
            double workingSingles = f.totalOf(FamilyStructure.SINGLE_ADULT);

            // The cause: enough one-person doors for the families and nobody else.
            int[] doors = new int[3];
            doors[1] = (int) Math.floor(f.totalHouseholds());
            double left = f.house(doors);
            f.squeezeUnplaced(left);
            f.noteUnplaced(f.house(doors));
            f.shareSeekersByAffordability(new double[] { 0, 0 });

            /*
             * The families here are two hundred couples and four hundred single
             * adults; the couples crowd into two hundred one-person doors, and
             * the singles and the out of work queue for the other four hundred
             * together, pro rata - a landlord lets the flat, not the payslip -
             * so two hundred are left over, two thirds of them working singles
             * and a third of them out of work.
             */
            check("fixture: two hundred could not be housed alone", left, 200, 1e-6);
            double singlesLeft = left * workingSingles / (workingSingles + 200);
            double seekersLeft = left * 200 / (workingSingles + 200);
            check("the working singles share only for their own unplaced (a common valve would leave 150)",
                    f.totalOf(FamilyStructure.SINGLE_ADULT),
                    workingSingles - 5 * Math.min(singlesLeft / 4, workingSingles / 5), 1e-6);
            check("...and the out of work share only for theirs",
                    f.getSeekersSharing(FamilyModel.Seeker.UNEMPLOYED),
                    5 * Math.min(seekersLeft / 4, 200.0 / 5), 1e-6);
            double share = f.seekerDoorShare(FamilyModel.Seeker.UNEMPLOYED);
            assertTrue("one of them pays less than a whole door", share < 1);
            check("every household that wants a door is housed, doubled up, or outside",
                    f.homesNeeded() + f.getDoubledUpHouseholds(), f.householdsSeekingDoors(), 1e-6);
        }

        /* ============ 4. EI, on the inflow ============ */
        System.out.println("\n--- EI: jobs lost, the ring, pro rata, the arrivals ---");
        {
            Unemployment u = new Unemployment();
            double wage = 3.460;
            double[] wages = new double[tiers];
            wages[unskilled] = wage; wages[skilled] = 4.500;
            double[] filled = new double[tiers];
            double[] posts = new double[tiers];
            filled[unskilled] = 900; filled[skilled] = 100;
            posts[unskilled] = 900; posts[skilled] = 100;

            // Month one: nobody's history is known.
            u.advanceMonth(0, filled, posts, wages, wage, 0, .55);
            check("a founding city with nobody out of work draws nothing", u.getBenefitsPaid(), 0, 1e-12);

            // The cause: a hundred skilled posts close, and the people in them are out.
            double[] filled2 = filled.clone(), posts2 = posts.clone();
            filled2[skilled] = 0; posts2[skilled] = 0;
            double pool = 100 * (1 - AgeBand.ADULT.monthlyOutflowRate() * 0);   // nobody else moves
            u.advanceMonth(pool, filled2, posts2, wages, wage, 0, .55);
            check("the posts that disappeared are the jobs lost", u.getJobsLost(), 100, 1e-9);
            check("...and every one of them is on EI", u.onEi(), 100, 1e-9);
            check("...at 55% of the wage they lost", u.getBenefitsPaid(), 100 * .55 * 4.500, 1e-9);

            // Posts that stay while their workers leave the city lose nobody a job.
            double[] filled3 = filled2.clone();
            filled3[unskilled] = 850;
            Unemployment v = new Unemployment();
            v.advanceMonth(0, filled, posts, wages, wage, 0, .55);
            v.advanceMonth(0, filled3, posts, wages, wage, 0, .55);
            check("a post left empty by somebody leaving the city loses nobody a job", v.getJobsLost(), 0, 0);

            // Twelve months later, with nothing else moving, the claim ends.
            for (int m = 0; m < 11; m++) {
                double left = u.getPool() * (1 - AgeBand.ADULT.monthlyOutflowRate());
                u.advanceMonth(left, filled2, posts2, wages, wage, 0, .55);
            }
            assertTrue("eleven months on, the claim is still being paid", u.onEi() > 90);
            double before = u.onEi();
            u.advanceMonth(u.getPool() * (1 - AgeBand.ADULT.monthlyOutflowRate()), filled2, posts2, wages, wage, 0, .55);
            check("...and in the thirteenth month it drops off", u.onEi(), 0, 1e-9);
            check("...into the off-EI group, whole", u.getDroppedOffEi(), before * (1 - AgeBand.ADULT.monthlyOutflowRate()), 1e-6);
            check("nobody past the twelfth month is paid", u.getBenefitsPaid(), 0, 1e-12);

            // Pro rata: a hundred in the pool, a hundred arrivals, fifty openings.
            Unemployment w = new Unemployment();
            double[] f0 = new double[tiers]; f0[unskilled] = 500;
            double[] p0 = new double[tiers]; p0[unskilled] = 600;
            w.advanceMonth(100, f0, p0, wages, wage, 0, .55);
            w.noteArrivals(100);
            double[] f1 = f0.clone(); f1[unskilled] = 550;
            double expectedPool = 100 - 25 + 75 - 100 * AgeBand.ADULT.monthlyOutflowRate();
            w.advanceMonth(expectedPool, f1, p0, wages, wage, 0, .55);
            check("fifty openings go half to the pool...", w.getLocalHires(), 25, 1e-9);
            check("...and half to the arrivals", w.getArrivalHires(), 25, 1e-9);
            check("the arrivals who did not get one are on EI (Jerus: \"same as locals\")",
                    w.getCohort(0), 75, 1e-9);
            check("...at the unskilled rate", w.getBenefitsPaid() - w.getCohort(1) * .55 * wage,
                    75 * .55 * wage, 1e-6);
            check("...and nobody entered the pool by a door the flows do not name", w.getEntrants(), 0, 1e-6);

            // The cap: an elite wage is insured at 1.66 unskilled wages, no more.
            Unemployment x = new Unemployment();
            double[] wx = new double[tiers]; wx[PayTier.ELITE.ordinal()] = 15.600; wx[unskilled] = wage;
            double[] fx = new double[tiers]; fx[PayTier.ELITE.ordinal()] = 10;
            x.advanceMonth(0, fx, fx.clone(), wx, wage, 0, .55);
            x.advanceMonth(10, new double[tiers], new double[tiers], wx, wage, 0, .55);
            check("an elite claim is capped at the maximum insurable earnings",
                    x.getBenefitsPaid(), 10 * .55 * wage * Unemployment.MAX_INSURABLE_MULTIPLE, 1e-9);

            // Evictions: forty could not pay, a quarter leave, the rest have no home.
            Unemployment e = new Unemployment();
            e.advanceMonth(100, f0, p0, wages, wage, 0, .55);
            for (int m = 0; m < 12; m++) e.advanceMonth(100, f0, p0, wages, wage, 0, .55);
            assertTrue("fixture: the pool is off EI", e.getOffEi() > 99);
            e.noteEvicted(40);
            double leaving = e.takeEvicted();
            check("a quarter of the evicted leave the city", leaving, 10, 1e-9);
            check("...and the rest have no home", e.getUnhoused(), 30, 1e-9);
            check("...and are no longer looking for a door", e.getHoused(), e.getPool() - 30, 1e-9);
        }

        /* ============ 5. the books ============ */
        System.out.println("\n--- the ledgers outside the families ---");
        {
            HouseholdBalance bal = new HouseholdBalance();
            double[] count = new double[1];
            bal.setOutsideCensus(c -> {
                if (c instanceof UnemployedHousehold u) {
                    return switch (u.status()) { case ON_EI -> 50; case OFF_EI -> count[0]; case UNHOUSED -> 0; };
                }
                if (c instanceof StudentHousehold) return 20;
                if (c instanceof OrphanHousehold o) return o.band() == AgeBand.BABY ? 10 : 0;
                return 0;
            });
            bal.setRentShares(c -> c instanceof OrphanHousehold ? 0 : c instanceof StudentHousehold ? 2 : 1);
            int R = HouseholdBalance.ROWS;
            double[] pay = new double[R];
            pay[HouseholdAccounts.UNEMPLOYED] = 50 * 1.9;      // the EI bill
            pay[HouseholdAccounts.STUDENTS] = 20 * .5;          // the grants
            double[] fees = new double[R];
            double[] bought = new double[R];

            // The cause: forty off EI with nothing saved and a rent of one.
            count[0] = 40;
            bal.advanceMonth((s, tier) -> 0, pay, 1.0, fees, bought, .3, .05, 1);
            UnemployedHousehold on = bal.unemployed(UnemployedHousehold.Status.ON_EI);
            UnemployedHousehold off = bal.unemployed(UnemployedHousehold.Status.OFF_EI);
            check("the EI bill goes to those on EI", on.disposable(), 1.9, 1e-9);
            check("...and nobody past it shares it", off.disposable(), 0, 1e-12);
            check("an off-EI household that cannot pay its rent is evicted by the share unpaid",
                    bal.getEvicted(), 40, 1e-9);

            // A student's rent is two, the grant a half, and a new student arrives
            // with a month and a half of the grant saved.
            StudentHousehold st = bal.students();
            double shortAfterSavings = (2 - .5) - .5 * HouseholdBalance.OPENING_BUFFER_MONTHS;
            check("a student is paid the grant", st.disposable(), .5, 1e-9);
            assertTrue("...cannot run out: nothing unfunded (Jerus: \"can't run out, for now\")", st.unfunded() == 0);
            check("...because the student loan covered what the savings could not", st.studentBorrowed(),
                    shortAfterSavings, 1e-9);
            check("...and it is a student loan, not the bank's credit", st.debt(), 0, 0);
            check("the treasury lent it", bal.totalStudentBorrowed(), 20 * shortAfterSavings, 1e-9);

            OrphanHousehold baby = bal.orphans(AgeBand.BABY);
            check("an orphan has no income", baby.disposable(), 0, 0);
            assertTrue("...and goes without", baby.planned() == 0 && baby.subsistence() > 0);

            // A graduate carries the loan into a family and repays a 114th of it.
            HouseholdBalance grad = new HouseholdBalance();
            double[] students = { 10 };
            double[] singles = { 0 };
            grad.setOutsideCensus(c -> c instanceof StudentHousehold ? students[0] : 0);
            double[] payG = new double[R];
            payG[HouseholdAccounts.STUDENTS] = 0;
            grad.advanceMonth((s, tier) -> 0, payG, .1, new double[R], new double[R], .3, .05, 1);
            double owed = grad.students().studentDebt();
            assertTrue("fixture: the students owe something", owed > 0);
            students[0] = 0;
            singles[0] = 10;
            double[] payW = new double[R];
            payW[unskilled] = 10 * 3.460;
            grad.advanceMonth((s, tier) -> s == FamilyStructure.SINGLE_ADULT && tier == PayTier.UNSKILLED ? singles[0] : 0,
                    payW, .1, new double[R], new double[R], .3, .05, 1);
            Household family = grad.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
            check("the graduates repay a 114th of what they brought", family.studentRepaid(), owed / 114, 1e-9);
            check("...and owe the rest", family.studentDebt(), owed - owed / 114, 1e-9);

            // A college that never changes size: ten students, ten working singles,
            // and five of the students finish. On the net rule nothing moved.
            HouseholdBalance college = new HouseholdBalance();
            college.setOutsideCensus(c -> c instanceof StudentHousehold ? 10 : 0);
            java.util.function.ToDoubleBiFunction<FamilyStructure, PayTier> ten =
                    (s, tier) -> s == FamilyStructure.SINGLE_ADULT && tier == PayTier.UNSKILLED ? 10 : 0;
            college.advanceMonth(ten, payW, .1, new double[R], new double[R], .3, .05, 1);
            double each = college.students().studentDebt();
            assertTrue("fixture: the students owe something", each > 0);
            Household worker = college.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
            assertTrue("fixture: the workers owe nothing", worker.studentDebt() == 0);
            college.setGraduates(5);
            college.advanceMonth(ten, payW, .1, new double[R], new double[R], .3, .05, 1);
            check("five graduates leave a student body that did not change size", college.getLastGraduated(), 5, 0);
            check("...with their loans, to the working households", worker.studentDebt() + worker.studentRepaid(),
                    each * 5 / 10, 1e-9);
            check("...who start repaying them", worker.studentRepaid(), each * 5 / 10 / 114, 1e-9);
            check("the freshers in their places owe nothing yet: the students' loan is halved, plus this month's",
                    college.students().studentDebt(), each / 2 + college.students().studentBorrowed(), 1e-9);
            check("nothing written off: a graduate is not a leaver", college.getStudentDebtTakenAway(), 0, 0);
            college.advanceMonth(ten, payW, .1, new double[R], new double[R], .3, .05, 1);
            check("...and it happens once", college.getLastGraduated(), 0, 0);
        }

        /* ============ 6. health ============ */
        System.out.println("\n--- the unhoused and the orphans ---");
        {
            Health h = new Health();
            h.advanceMonth(1_000, 1_000, 7, 0, 0, 0);
            double housed = h.getSickRate();
            Health hu = new Health();
            hu.advanceMonth(1_000, 1_000, 7, 0, 0, .10);
            check("a tenth of the city unhoused adds their extra sickness over the baseline",
                    hu.getSickRate() - housed, .10 * hu.getBaselineRate() * (Unemployment.UNHOUSED_SICKNESS - 1), 1e-12);

            double[] factors = { 1, 1, 1, 1, 1 };
            double[] uncared = Healthcare.mortalityFactors(0, 0, 0);
            double[] inBand = { 100, 100, 100, 100, 100 };
            double[] unhoused = { 0, 0, 0, 10, 0 };
            double[] orphans = { 20, 0, 0, 0, 0 };
            double[] out = Unemployment.blendMortality(factors, uncared, inBand, unhoused, orphans);
            check("a tenth of the adults unhoused die 3.7 times as fast",
                    out[AgeBand.ADULT.ordinal()], .9 + .1 * Unemployment.UNHOUSED_MORTALITY, 1e-12);
            check("a fifth of the babies orphaned die at the rate of a baby nobody cares for",
                    out[AgeBand.BABY.ordinal()], .8 + .2 * uncared[AgeBand.BABY.ordinal()], 1e-12);
            assertTrue("...which is faster than any cared-for baby", uncared[AgeBand.BABY.ordinal()] > 1);
            check("a band with nobody outside is untouched", out[AgeBand.CHILD.ordinal()], 1, 0);
        }

        /* ============ 7. a real city: the treasury and the books agree ============ */
        System.out.println("\n--- a city: EI, the premium and the books ---");
        {
            Game g = new Game(GameFiles.scratch("outsidecheck"));
            quietly(() -> {
                g.run();
                g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 100_000_000L);
                // More homes than posts, so the city has people out of work,
                // and one mill whose closing takes posts that are filled.
                g.buildStack(t(g, "House"), 400, false);
                g.buildStack(t(g, "Small Grocery Store"), 2, false);
                g.buildStack(t(g, "Textile Mill"), 2, false);
                g.buildStack(t(g, "Coal Power Plant"), 1, false);
                g.buildStack(t(g, "Water Treatment Plant"), 1, false);
                g.buildStack(t(g, "Paved Road"), 4, false);
                g.buildStack(t(g, "Walk-in Clinic"), 1, false);
                g.simulateMonths(60);
            });
            EconomyManager e = g.getEconomyManager();
            Unemployment u = g.getUnemployment();
            check("the treasury pays the EI the ring says", e.getEiBenefits(), u.getBenefitsPaid(), 1e-9);
            check("the premium is the dial times the staffed wage bill",
                    e.getEiPremiums(), e.getTaxPolicy().getEiPremiumRate()
                            * g.getPopulationManager().getTotalWage(), 1e-6);
            check("the pool is the labour market's", u.getPool(), g.getPopulationManager().getUnemployed(), 1e-6);

            FamilyModel f = g.getFamilies();
            double adults = 0;
            for (FamilyStructure s : FamilyStructure.values()) adults += f.totalOf(s) * s.membersOf(AgeBand.ADULT);
            check("the families hold the adults who are not outside them",
                    adults + f.getOutsideAdults(), g.getCohorts().get(AgeBand.ADULT), 1e-6);

            /*
             * THE CAUSE, in two steps. A founding city has more posts than
             * people, so nobody is out of work and no closing can put anybody
             * on EI. So: half as many adults again arrive at once, which fills
             * every post and leaves a pool; then the business with the most
             * filled posts closes, and its posts go with it.
             */
            double[] pyramid = g.getCohorts().toSaveArray();
            pyramid[AgeBand.ADULT.ordinal()] *= 1.5;
            g.getCohorts().restore(pyramid);
            quietly(() -> g.simulateMonths(2));
            BuildingsTemplate biggest = null;
            int biggestJobs = 0;
            for (BuildingsTemplate b : g.getBuildingManager().getTemplates()) {
                if (b.getSector() == null || b.getTotalJobs() <= 0) continue;
                int jobs = b.getTotalJobs() * g.getBuildingManager().getQuantity(b.getId());
                if (jobs > biggestJobs) { biggestJobs = jobs; biggest = b; }
            }
            final BuildingsTemplate closing = biggest;
            double filledBefore = g.getPopulationManager().getJobsFilled();
            System.out.printf("   before: %,d posts, %,d filled, %,.0f out of work; closing every %s (%,d posts)%n",
                    g.getPopulationManager().getTotalJobs(), g.getPopulationManager().getJobsFilled(), u.getPool(),
                    closing == null ? "-" : closing.getName(), biggestJobs);
            assertTrue("fixture: the city has more workers than posts", u.getPool() > 0 && closing != null);
            quietly(() -> {
                if (closing != null) g.getBuildingManager().retire(closing, g.getBuildingManager().getQuantity(closing.getId()));
                g.simulateMonths(1);
            });
            System.out.printf("   after: %,d posts, %,d filled, %,.0f out of work, %,.0f jobs lost, %,.0f on EI%n",
                    g.getPopulationManager().getTotalJobs(), g.getPopulationManager().getJobsFilled(), u.getPool(),
                    u.getJobsLost(), u.onEi());
            assertTrue("fixture: the closing cost filled posts",
                    g.getPopulationManager().getJobsFilled() < filledBefore);
            assertTrue("...and put people on EI", u.getJobsLost() > 0 && u.onEi() > 0);
            check("...and the treasury pays what the ring says", e.getEiBenefits(), u.getBenefitsPaid(), 1e-9);
            double before = e.getEiBenefits();
            assertTrue("fixture: there is EI to pay", before > 0);
            quietly(() -> g.simulateMonths(1));
            HouseholdAccounts books = g.getHouseholds();
            check("the out of work were paid last month's EI", books.getRowBenefits(HouseholdAccounts.UNEMPLOYED),
                    before, 1e-9);
            MoneyAudit.Result r = g.getLastMoneyAudit();
            assertTrue("and the month passes the money audit", r.relative() < 1e-6);
        }

        /* ============ 8. a city with a college: the students' money, and a save ============ */
        System.out.println("\n--- a city with a college: grants, loans, tuition, and a save ---");
        try {
            java.nio.file.Path root = java.nio.file.Files.createTempDirectory("outsidecheck");
            GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
            Game g = new Game(files);
            double[] worstAudit = new double[1];
            double[] lentEver = new double[1];
            double[] repaidEver = new double[1];
            double[] graduatedEver = new double[1];
            double[] graduatesMissed = new double[1];
            double[] finishedBefore = new double[1];
            quietly(() -> {
                g.newGame();
                g.getGovernmentInvestor().spend(-900_000_000);
                g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
                String[][] plan = {
                    {"Low-Rise Apartments", "120"}, {"General Hospital", "3"}, {"Small Grocery Store", "14"},
                    {"Coal Power Plant", "4"}, {"Water Treatment Plant", "4"}, {"Paved Road", "60"},
                    {"Construction Depot", "6"}, {"Elementary School", "14"}, {"Middle School", "14"},
                    {"High School", "10"}, {"Community College", "6"}, {"University", "3"},
                    // Policed since the prisons (2026-09-11): with none, the
                    // thefts hand the students enough to never need a loan,
                    // and this fixture is about the loans.
                    {"Police Station", "1"}};
                for (String[] b : plan) g.buildStack(t(g, b[0]), Integer.parseInt(b[1]), true);
                g.getEducation().setTuitionSubsidy(.8);
                for (int m = 0; m < 180; m++) {
                    // The households are struck at the top of the month and the
                    // schools run in the middle of it, so the census carries out
                    // the ones who finished the month before last: the count the
                    // families were built on last month is the one they left.
                    double finished = finishedBefore[0];
                    finishedBefore[0] = g.getEducation().getFinished();
                    double studentsBefore = g.getHouseholdBalance().students().households();
                    g.simulateMonths(1);
                    double carried = g.getHouseholdBalance().getLastGraduated();
                    graduatedEver[0] += carried;
                    if (m > 0) graduatesMissed[0] = Math.max(graduatesMissed[0],
                            Math.abs(carried - Math.min(finished, studentsBefore)));
                    MoneyAudit.Result r = g.getLastMoneyAudit();
                    worstAudit[0] = Math.max(worstAudit[0], r.relative());
                    lentEver[0] += g.getStudentLoansLent();
                    repaidEver[0] += g.getStudentLoansRepaid();
                }
            });
            FamilyModel f = g.getFamilies();
            EconomyManager e = g.getEconomyManager();
            HouseholdAccounts books = g.getHouseholds();
            double students = f.getSeekers(FamilyModel.Seeker.STUDENT);
            HouseholdBalance hb = g.getHouseholdBalance();
            double onStudents = hb.students().studentDebt() * hb.students().households();
            System.out.printf("   %,.0f students, %,.0f graduated; lent %,.0f, repaid %,.0f over fifteen years;"
                            + " %,.0f owed now, %,.0f of it by the working families%n",
                    students, graduatedEver[0], lentEver[0], repaidEver[0], hb.totalStudentDebt(),
                    hb.totalStudentDebt() - onStudents);
            assertTrue("fixture: the college has students", students > 10);
            check("the grant is the dial times the unskilled wage, for every student",
                    e.getStudentGrants(), students * e.getTaxPolicy().getStudentGrantShare()
                            * g.getPopulationManager().getWagesPerType()[JobType.NO_DIPLOMA.ordinal()], 1e-6);
            check("the students pay the tuition (Jerus: \"students pay it\")",
                    books.getRowTuition(HouseholdAccounts.STUDENTS), books.getTuition(), 1e-9);
            check("...and no family does", books.getRowTuition(PayTier.UNSKILLED.ordinal()), 0, 0);
            assertTrue("the treasury lent student loans", lentEver[0] > 0);
            check("every month, the students the census saw finish left with their loans",
                    graduatesMissed[0], 0, 1e-9);
            assertTrue("fixture: people graduated", graduatedEver[0] > 100);
            assertTrue("...so the working families carry student loans", hb.totalStudentDebt() - onStudents > 0);
            assertTrue("...and graduates have repaid some of them", repaidEver[0] > 0);
            check("a student is never cut off", g.getHouseholdBalance().students().unfunded(), 0, 0);
            assertTrue("every month passed the money audit", worstAudit[0] < 1e-6);

            double owed = g.getHouseholdBalance().totalStudentDebt();
            double pool = g.getUnemployment().getPool();
            double onEi = g.getUnemployment().onEi();
            double eiRate = e.getTaxPolicy().getEiBenefitRate();
            assertTrue("the city saved", g.saveGame(1, "outside").ok);
            Game back = new Game(files);
            quietly(() -> back.loadGameSave(1));
            check("the student loans came back", back.getHouseholdBalance().totalStudentDebt(), owed, 1e-6);
            check("...and the pool", back.getUnemployment().getPool(), pool, 1e-9);
            check("...and who was on EI", back.getUnemployment().onEi(), onEi, 1e-9);
            check("...and the EI dial", back.getEconomyManager().getTaxPolicy().getEiBenefitRate(), eiRate, 0);
            check("...and the orphans", back.getFamilies().getOrphansTotal(), f.getOrphansTotal(), 1e-9);
            check("...and who finished a course, which the families read next",
                    back.getEducation().getFinished(), g.getEducation().getFinished(), 0);
            assertTrue("fixture: somebody finished that month", g.getEducation().getFinished() > 0);
        } catch (java.io.IOException ex) {
            assertTrue("a temporary directory for the save: " + ex.getMessage(), false);
        }

        System.out.println();
        System.out.println(fails == 0 ? "The people outside the families are on the books." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
