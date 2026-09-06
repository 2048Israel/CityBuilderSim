package ham.citybuildersim;

/**
 * Who the city teaches, what it costs, and why anybody bothers.
 *
 * THE PROBLEM THIS SOLVES
 *
 * Before schools existed, every skilled worker in the game had arrived from
 * somewhere else. The labour market could make a doctor EXPENSIVE and it could
 * not make a doctor, so a city's whole skill mix was a function of how many
 * strangers it had persuaded to move in - and WageBand.worldShare() means the
 * wider world only ever has about six graduates for every hundred people. A
 * city of eighty thousand cannot import two hundred doctors at any price. At
 * some size it has to make its own or go without.
 *
 * WHAT MAKES A PERSON GET EDUCATED
 *
 * Three things, and they answer three different questions. Jerus picked all
 * three, and the interesting part is that each one fails differently:
 *
 *   CAN THEY GO?     Places. A school seats so many, and a course lasts so
 *                    long, so a university of 2,000 seats and a four-year
 *                    degree graduates forty a month. This is the cap.
 *
 *   WOULD THEY?      The return. Nobody spends four years to earn what they
 *                    already earn, so enrolment follows the gap between what
 *                    the next band pays and what this one does - which the
 *                    labour market is already computing every month for its
 *                    own reasons.
 *
 *   CAN THEY AFFORD IT?  Tuition against a wage. This is the one that bites,
 *                    and it is the reason the subsidy dial exists: at no
 *                    subsidy a university education costs a diploma-holder half
 *                    their monthly income and almost nobody goes, so a city of
 *                    poor unskilled workers CANNOT EDUCATE ITS WAY OUT. That is
 *                    a real trap with three different fixes - build schools,
 *                    raise wages, or pay the tuition - and it is the most
 *                    interesting thing on this page.
 *
 * TWO KINDS OF OUTPUT
 *
 * The basic ladder and the colleges raise a person's BAND, which is a level.
 * The four professional schools raise nobody's level; they license a job. See
 * EducationType - a city with no medical school has graduates and no doctors.
 *
 * THE PIPELINE IS ONLY AS WIDE AS ITS NARROWEST STAGE
 *
 * Elementary and middle school serve CHILD, high school serves TEEN, and a
 * child who never went to middle school does not turn up at high school. So the
 * diploma rate is the MINIMUM of the three coverages and not their average: a
 * city with elementary places for everybody and one high school produces as
 * many diplomas as the high school can seat, and the People screen can say
 * which stage is the bottleneck.
 */
public class Education {

    /* ===================================================================
       THE DIAL
       =================================================================== */

    /**
     * What share of tuition the city pays.
     *
     * Jerus's choice, and it is the whole policy in one number. At 0 the
     * household pays it all and only the top pay tiers can attend; at 1 it is
     * free at the point of use and the education line becomes one of the
     * largest on the budget. The default is most-of-the-way-public, which is
     * what makes a starting city able to educate anybody at all.
     */
    public static final double DEFAULT_SUBSIDY = .60;

    private double tuitionSubsidy = DEFAULT_SUBSIDY;

    /**
     * Tuition per student per month, before any subsidy.
     *
     * A PRICE, not a cost recovery. What it costs the city to run a school is
     * the building's upkeep plus its payroll, which the treasury pays whether
     * anybody enrols or not; this is what a seat is sold for, and the gap
     * between the two is the point of a public education system.
     *
     * Calibrated against the wages of the people who would pay it. University
     * tuition is 0.80 against a diploma wage of 1.500 - fifty-three per cent of
     * a month's pay unsubsidised, which stops almost everybody, and twenty-one
     * per cent at the default subsidy, which stops almost nobody. The dial has
     * to move something or it is not a decision.
     */
    public static double tuitionOf(EducationType type) {
        switch (type) {
            case ELEMENTARY:
            case MIDDLE:      return .10;
            case HIGH:        return .18;
            case COLLEGE:     return .55;
            case UNIVERSITY:  return 1.20;
            case MEDICAL:
            case LAW:
            case BUSINESS:
            case ENGINEERING: return 2.60;
            default:          return 0;
        }
    }

    /**
     * The share of a month's wage above which nobody enrols.
     *
     * Not a cliff: enrolment falls linearly from everyone at zero burden to
     * nobody at this one, and the ramp between is the difference between a
     * policy that is felt and a policy that is a switch.
     *
     * SIXTY PER CENT AND NOT THIRTY-FIVE, measured in. At thirty-five the
     * tuition that households could bear was so small it recovered about one
     * per cent of what the schools cost, and the subsidy dial changed
     * attendance enormously while changing the budget by a rounding error -
     * which is half a decision. Education is a thing families commit real money
     * to for years because it pays back for decades, and the number has to say
     * that or the trade is not a trade.
     */
    public static final double MAX_BURDEN = .60;

    /* ===================================================================
       THE CURVE
       =================================================================== */

    /**
     * How hard the pay gap pulls people into a classroom.
     *
     * Above one, so a doubling of the return more than doubles enrolment.
     * Nobody studies for four years to earn what they already earn, and the
     * shape has to say that: at a return of 1.0 this contributes nothing at all.
     */
    public static final double RETURN_ELASTICITY = 1.3;

    /** However good the return, this share of the eligible is the most that go. */
    public static final double MAX_PARTICIPATION = .90;

    /**
     * What fraction of the willing eligible pool starts a course in any month.
     *
     * MEASURED IN, AFTER THE FIRST VERSION EMPTIED A BAND IN ONE MONTH. Without
     * it, enrolment was `eligible x willing` - ninety per cent of every
     * diploma-holder in the city walking into a lecture theatre the month the
     * college opened. The diploma band went from 1,390 people to 28 and stayed
     * there, because adult study was consuming a STOCK at the rate a FLOW
     * should be consumed.
     *
     * A person spends about five years of their working life in a position to
     * consider going back to study, so a sixtieth of them start in any given
     * month. That also makes the school's own capacity the binding constraint
     * in a city large enough to fill it, which is the whole point of building
     * one.
     */
    public static final double ENROLMENT_RATE = 1 / 60.0;

    /**
     * Ages 6-10 out of the CHILD band's 6-13.
     *
     * Elementary and middle school both serve CHILD, so the band has to be
     * split between them or one of the two is always trivially covered. The
     * split is the real one - four years then three.
     */
    public static final double ELEMENTARY_SHARE = 4 / 7.0;

    /* ===================================================================
       STATE
       =================================================================== */

    private final double[] graduates = new double[WageBand.values().length];
    private final double[] licences = new double[JobType.values().length];
    private final double[] coverage = new double[EducationType.values().length];
    private final double[] enrolled = new double[EducationType.values().length];

    private double tuitionCollected;
    private double citySubsidyPaid;
    private double payroll;
    private double upkeep;

    /**
     * Everyone the city has ever put through school, by band.
     *
     * A STOCK, and the only number here that a save has to carry for its own
     * sake - the rest is this month's flow and is rebuilt every month. It earns
     * its place by being the answer to "is the education system working", which
     * a monthly graduation count cannot give: forty a month is either
     * impressive or pitiful depending on how long it has been going.
     */
    private final double[] everGraduated = new double[WageBand.values().length];

    /* ===================================================================
       THE MONTH
       =================================================================== */

    /**
     * Teaches everybody who can, will, and can afford to go.
     *
     * @param places        staffed capacity per EducationType - staffed, because
     *                      a school with no teachers teaches nobody, exactly as
     *                      a hospital with no doctors treats nobody
     * @param staffedPayroll what the city owes the schools' staff this month
     * @param schoolUpkeep   and their buildings' upkeep
     */
    public void advanceMonth(double[] places, PopulationCohorts pyramid,
                             PopulationManager people, LabourMarket market,
                             double staffedPayroll, double schoolUpkeep) {

        java.util.Arrays.fill(graduates, 0);
        java.util.Arrays.fill(licences, 0);
        java.util.Arrays.fill(enrolled, 0);
        tuitionCollected = 0;
        citySubsidyPaid = 0;
        payroll = Math.max(0, staffedPayroll);
        upkeep = Math.max(0, schoolUpkeep);

        if (places == null || pyramid == null || people == null) return;

        /* ------------------------- the basic ladder ------------------------- */
        double children = pyramid.get(AgeBand.CHILD);
        double teens = pyramid.get(AgeBand.TEEN);

        coverage[EducationType.ELEMENTARY.ordinal()] =
                cover(places[EducationType.ELEMENTARY.ordinal()], children * ELEMENTARY_SHARE);
        coverage[EducationType.MIDDLE.ordinal()] =
                cover(places[EducationType.MIDDLE.ordinal()], children * (1 - ELEMENTARY_SHARE));
        coverage[EducationType.HIGH.ordinal()] =
                cover(places[EducationType.HIGH.ordinal()], teens);

        /*
         * THE MINIMUM, not the average and not the product.
         *
         * The average would let a city paper over a missing high school with
         * spare primary places, which is the one thing a pipeline cannot do.
         * The product is arguably truer - each stage loses people - but three
         * coverages of 80% would then yield 51%, which reads as broken rather
         * than as attrition. The bottleneck is both the honest answer and the
         * one a player can act on, because it names a building.
         */
        double basic = Math.min(coverage[EducationType.ELEMENTARY.ordinal()],
                Math.min(coverage[EducationType.MIDDLE.ordinal()],
                         coverage[EducationType.HIGH.ordinal()]));

        /*
         * Teens age out at a steady rate, and the ones who were in school leave
         * with a diploma. No cohort tracking: the pyramid already knows how many
         * teens there are and how long they stay, and a second copy of that
         * clock in here is a second thing to keep in step with it.
         */
        double leavingSchool = teens / (double) AgeBand.TEEN.spanMonths();
        double afford = affordability(EducationType.HIGH, market, WageBand.NONE);
        double newDiplomas = leavingSchool * basic * afford;

        /*
         * A NET FLOW, not a count of graduates.
         *
         * Somebody who finishes a degree LEAVES the band they were in - the
         * city gains a graduate and loses a diploma-holder, it does not gain
         * both. Emitting the movement as +1 at the destination and -1 at the
         * source means PopulationManager can apply it without knowing anything
         * about who trains whom, and there is no second copy of the ladder to
         * keep in step with EducationType.requires().
         *
         * A school leaver's source is NONE, which PopulationManager does not
         * store at all - the unskilled band is whatever is left of the
         * workforce - so that entry is written and harmlessly ignored.
         */
        graduates[WageBand.DIPLOMA.ordinal()] += newDiplomas;
        graduates[WageBand.NONE.ordinal()] -= newDiplomas;
        /*
         * Recorded as well as charged. The first version billed the two junior
         * stages and never wrote down how many were in them, so the People
         * screen showed an elementary school with a hundred per cent coverage
         * and nobody in it - which reads as a bug in the schools rather than a
         * bug in the reporting, and is the harder of the two to find.
         */
        enrolled[EducationType.ELEMENTARY.ordinal()] = Math.min(
                places[EducationType.ELEMENTARY.ordinal()], children * ELEMENTARY_SHARE);
        enrolled[EducationType.MIDDLE.ordinal()] = Math.min(
                places[EducationType.MIDDLE.ordinal()], children * (1 - ELEMENTARY_SHARE));
        enrolled[EducationType.HIGH.ordinal()] =
                Math.min(places[EducationType.HIGH.ordinal()], teens * basic);

        charge(EducationType.ELEMENTARY, enrolled[EducationType.ELEMENTARY.ordinal()]);
        charge(EducationType.MIDDLE, enrolled[EducationType.MIDDLE.ordinal()]);
        charge(EducationType.HIGH, enrolled[EducationType.HIGH.ordinal()]);

        /* ------------------------- adult study ------------------------- */
        double[] workforceByBand = people.workforceByBand();

        study(EducationType.COLLEGE, places, workforceByBand, market, people);
        study(EducationType.UNIVERSITY, places, workforceByBand, market, people);

        for (EducationType type : EducationType.values()) {
            if (type.isProfessional()) study(type, places, workforceByBand, market, people);
        }

        // The running total counts people GAINED at each level, so the negative
        // half of the flow is not a graduation and does not belong in it.
        for (int b = 0; b < graduates.length; b++) {
            if (graduates[b] > 0) everGraduated[b] += graduates[b];
        }
    }

    /**
     * One course, for adults who already have what it asks for.
     *
     * The three gates in order: how many seats turn over, how many people are
     * eligible, and what share of those both want it and can pay. Whichever is
     * smallest is the answer, which is what makes a school in the wrong city
     * simply idle rather than broken.
     */
    private void study(EducationType type, double[] places, double[] workforceByBand,
                       LabourMarket market, PopulationManager people) {

        int i = type.ordinal();
        double seats = places[i];
        if (seats <= 0) return;

        WageBand needs = type.requires();
        double eligible = needs == null ? 0 : workforceByBand[needs.ordinal()];

        /*
         * ALREADY LICENSED PEOPLE DO NOT GO BACK. A professional school's pool
         * is graduates who do not already hold that licence - otherwise a city
         * with a medical school would keep re-teaching the same doctors and the
         * count would run away from the workforce that contains them.
         */
        if (type.isProfessional()) {
            eligible = Math.max(0, eligible - people.getLicensed(type.licenses()));
        }

        double willing = participation(type, market, needs);

        /*
         * TWO DIFFERENT NUMBERS, and conflating them was the first version's
         * bug. `taking` is a FLOW - people finishing this month - and it is
         * capped by how fast the seats turn over. `studentBody` is a STOCK -
         * everybody currently part way through - and it is what tuition is
         * charged on and what the seats are actually occupied by.
         *
         * They differ by the length of the course, which is exactly the lag
         * this whole mechanic is about: a four-year degree means four cohorts
         * are in the building for every one that leaves it.
         */
        double seatsPerMonth = seats / (double) type.months();
        double taking = Math.min(seatsPerMonth, eligible * willing * ENROLMENT_RATE);
        if (taking <= 0) return;

        double studentBody = Math.min(seats, taking * type.months());

        enrolled[i] = studentBody;
        coverage[i] = cover(seats, Math.max(eligible, 1));
        charge(type, studentBody);

        if (type.isProfessional()) {
            // Raises nobody's band - a medical student was already a graduate.
            licences[type.licenses().ordinal()] += taking;
        } else if (type.produces() != null) {
            graduates[type.produces().ordinal()] += taking;
            if (needs != null) graduates[needs.ordinal()] -= taking;
        }
    }

    /**
     * What share of the eligible actually enrol: the return, times the money.
     *
     * Multiplied rather than averaged, because either one being zero is a
     * complete answer on its own. A degree that pays no more than the job you
     * have is not worth doing at any price, and a degree you cannot pay for is
     * not worth doing at any salary.
     */
    private double participation(EducationType type, LabourMarket market, WageBand from) {
        return MAX_PARTICIPATION
                * returnOn(type, market, from)
                * affordability(type, market, from);
    }

    /**
     * How much better off somebody is for having done it.
     *
     * Read off the LIVE wages, so a city short of engineers is a city whose
     * people are choosing engineering - the labour market's scarcity signal
     * arriving in the classroom without anything having to connect them
     * deliberately. At parity it returns zero: nobody studies for four years to
     * stand still.
     */
    private double returnOn(EducationType type, LabourMarket market, WageBand from) {
        if (market == null || from == null) return .5;

        /*
         * WHAT THEY EARN WITHOUT THE LICENCE, and this is the whole difference
         * between the two kinds of school.
         *
         * A medical student is already a university graduate, so their "before"
         * is the best UNGATED university job - research, policy - and not the
         * doctor's salary they are studying to be allowed to earn. The first
         * version compared the band's best wage against the doctor's wage, and
         * the band's best wage IS the doctor's wage, so the return came out at
         * exactly 1.00, nobody ever enrolled, and a medical school produced
         * zero doctors in three hundred and sixty months while costing $78M.
         */
        double now = type.isProfessional()
                ? ungatedWage(market, from)
                : bandWage(market, from);
        double after = type.isProfessional()
                ? market.getWage(type.licenses())
                : bandWage(market, type.produces());

        if (now <= 0 || after <= now) return 0;
        double ratio = after / now;
        // Normalised so a 2x return is full participation and anything beyond
        // it is capped: past a point the answer is already yes.
        return Math.min(1, Math.pow(ratio - 1, RETURN_ELASTICITY));
    }

    /** The best-paid job in a band, which is what a student is aiming at. */
    private double bandWage(LabourMarket market, WageBand band) {
        if (band == null) return 0;
        double best = 0;
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) == band) best = Math.max(best, market.getWage(job));
        }
        return best;
    }

    /** The best a graduate can earn WITHOUT a professional licence. */
    private double ungatedWage(LabourMarket market, WageBand band) {
        if (band == null) return 0;
        double best = 0;
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) != band) continue;
            if (PopulationManager.isGated(job)) continue;
            best = Math.max(best, market.getWage(job));
        }
        // A band where every job is gated has no unlicensed fallback, so the
        // comparison is against the band below - which is what such a person
        // would actually be doing.
        return best > 0 ? best : bandWage(market,
                band.ordinal() > 0 ? WageBand.values()[band.ordinal() - 1] : band);
    }

    /**
     * What share of people could pay the un-subsidised part out of a month's pay.
     *
     * Falls linearly from everybody at no burden to nobody at MAX_BURDEN. This
     * is the poverty trap, stated: an unskilled worker earning the minimum wage
     * cannot fund a university place out of it, so a city whose workforce is all
     * unskilled produces no graduates however many universities it builds - and
     * the way out is the subsidy dial, or higher wages, or both.
     */
    private double affordability(EducationType type, LabourMarket market, WageBand from) {
        double outOfPocket = tuitionOf(type) * (1 - tuitionSubsidy);
        if (outOfPocket <= 0) return 1;

        double wage = market == null || from == null
                ? PayTier.UNSKILLED.getMonthlyWage()
                : Math.max(bandWage(market, from), 1e-6);

        double burden = outOfPocket / wage;
        return clamp(1 - burden / MAX_BURDEN);
    }

    /** Bills the month's tuition, split between the household and the treasury. */
    private void charge(EducationType type, double students) {
        double fee = tuitionOf(type) * Math.max(0, students);
        citySubsidyPaid += fee * tuitionSubsidy;
        tuitionCollected += fee * (1 - tuitionSubsidy);
    }

    private static double cover(double have, double need) {
        if (need <= 0) return have > 0 ? 1 : 0;
        return clamp(have / need);
    }

    private static double clamp(double v) {
        return v < 0 ? 0 : (v > 1 ? 1 : v);
    }

    /* ===================================================================
       READING
       =================================================================== */

    /**
     * The month's movement between bands: positive where people arrived,
     * negative where they left. See advanceMonth - it is a flow, not a count.
     */
    public double[] getGraduates() { return graduates; }

    /** People who became able to hold a gated job this month. */
    public double[] getLicences() { return licences; }

    public double getCoverage(EducationType type) { return coverage[type.ordinal()]; }
    public double getEnrolled(EducationType type) { return enrolled[type.ordinal()]; }
    public double getTuitionSubsidy() { return tuitionSubsidy; }

    public void setTuitionSubsidy(double value) {
        tuitionSubsidy = value < 0 ? 0 : (value > 1 ? 1 : value);
    }

    /** What leaves the treasury: staff, buildings, and the city's share of fees. */
    public double getGrossCost() { return payroll + upkeep + citySubsidyPaid; }
    public double getPayroll()   { return payroll; }
    public double getUpkeep()    { return upkeep; }
    public double getSubsidy()   { return citySubsidyPaid; }

    /** ...and what comes back from the households. */
    public double getFees()      { return tuitionCollected; }
    public double getNetCost()   { return getGrossCost() - tuitionCollected; }

    public double getCostRecovery() {
        double gross = getGrossCost();
        return gross > 0 ? tuitionCollected / gross : 0;
    }

    public double[] getEverGraduated() { return everGraduated; }

    /**
     * Which stage of the basic ladder is holding the rest up.
     *
     * The whole reason coverage is a minimum rather than an average: the answer
     * to "why is my diploma rate low" is a building, and this names it.
     */
    public EducationType basicBottleneck() {
        EducationType worst = EducationType.ELEMENTARY;
        for (EducationType type : EducationType.values()) {
            if (!type.isBasic()) continue;
            if (coverage[type.ordinal()] < coverage[worst.ordinal()]) worst = type;
        }
        return worst;
    }

    public double basicCoverage() {
        return coverage[basicBottleneck().ordinal()];
    }

    /* ===================================================================
       SAVE AND RESTORE

       Only the running total and the dial. Everything else here is this
       month's flow and is rebuilt from the buildings and the pyramid on the
       first month back - which is the test for what belongs in a save, and
       most of this class fails it.
       =================================================================== */

    public double[] getState() {
        double[] out = new double[everGraduated.length + 1];
        out[0] = tuitionSubsidy;
        System.arraycopy(everGraduated, 0, out, 1, everGraduated.length);
        return out;
    }

    /** Refused whole on a length mismatch, never padded. The usual rule. */
    public void restore(double[] saved) {
        if (saved == null || saved.length != everGraduated.length + 1) {
            tuitionSubsidy = DEFAULT_SUBSIDY;
            java.util.Arrays.fill(everGraduated, 0);
            return;
        }
        setTuitionSubsidy(saved[0]);
        System.arraycopy(saved, 1, everGraduated, 0, everGraduated.length);
    }
}
