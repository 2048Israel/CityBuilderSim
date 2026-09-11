package ham.citybuildersim;

/**
 * Who the city teaches, what it costs, and why anybody bothers.
 *
 * THE PROBLEM THIS SOLVES
 *
 * Before schools existed, every skilled worker in the game had arrived from
 * somewhere else. The labour market could make a doctor EXPENSIVE and it could
 * not make a doctor, so a city's whole skill mix was a function of how many
 * strangers it had persuaded to move in - and WageBand.arrivalCeiling() means
 * a graduate arrives only where the city is paying over the going rate, and
 * even then out of a world with few to spare. A city of eighty thousand cannot
 * import two hundred doctors at any price. At some size it has to make its own
 * or go without.
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
    /* =====================================================================
       WHAT A COURSE COSTS, IN TODAY'S MONEY

       The table below is in FOUNDING dollars and never moves. The instance
       array beside it is the same table in whatever the city currently calls
       its money, and it is what everything actually charges.

       This was one figure, static, read directly - and it was the thirteenth
       sighting of the redenomination family and the worst-behaved of them,
       because it does not merely misreport. tuitionOf() feeds affordability(),
       which decides who can go to school at all: after a 100:1 reform a
       university place still cost 1.20 in a city where a month's unskilled wage
       had become 0.05, so the whole pipeline stalled and the city stopped
       producing graduates for ever. The city's education FEE revenue was also
       a hundred times too large, which was 29% of the treasury's whole tax take
       on the DenominationCheck fixture.

       Seeded at construction and re-seeded on load like every other money
       constant, and scaled in redenominate() so a reform needs no re-seed.
       ===================================================================== */
    private final double[] tuition = new double[EducationType.values().length];

    { seedConstants(1); }

    public void seedConstants(double unit) {
        for (EducationType t : EducationType.values()) {
            tuition[t.ordinal()] = foundingTuition(t) / (unit > 0 ? unit : 1);
        }
    }

    /** What this city charges for the course today. */
    public double feeFor(EducationType type) {
        return type == null ? 0 : tuition[type.ordinal()];
    }

    /** The same table in founding dollars, which is where the numbers live. */
    public static double foundingTuition(EducationType type) {
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
     * Adults who came out of a course this month, every course counted once:
     * the gross flow out of the student body, where graduates[] is the net
     * movement between bands. HouseholdBalance needs the gross figure - a
     * college that takes in as many as it lets out has a student body that
     * never changes size, and the graduates still leave it with their loans.
     * Saved, because the families read it the month after it happens.
     */
    private double finished;

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

    /*
     * THE PIPELINE (2026-09-06).
     *
     * Everybody part way through an adult course, by course and by months
     * left: inFlight[type][k] is the number of students who graduate in k+1
     * months. Until today there was no such thing - `taking` was computed
     * from the seats and the willing pool and handed straight to the band
     * flow, so a medical school licensed doctors THE MONTH IT OPENED and the
     * "twenty-year loop" this class was built for existed only in its
     * comments. The steady-state throughput was right, which is why
     * EducationCheck passed; what was missing was the wait.
     *
     * This is a STOCK, and it is the second thing in this class a save has to
     * carry: seven years of a medical student's life is not recoverable from
     * a closing balance. A format-17 city loads with nobody in flight and
     * the schools fill from empty, which is what they would have done had
     * they been built the month the save was made.
     */
    private final double[][] inFlight = new double[EducationType.values().length][];

    /** Adults currently studying full time, by the band they came FROM. Derived. */
    private final double[] studying = new double[WageBand.values().length];

    {
        for (EducationType type : EducationType.values()) {
            inFlight[type.ordinal()] = type.isAdult() ? new double[type.months()] : new double[0];
        }
    }

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
        advanceMonth(places, pyramid, people, market, staffedPayroll, schoolUpkeep, 0);
    }

    /**
     * @param attrition the share of adults who left the city or died this
     *                  month, so the students in flight thin at the same rate
     *                  as the workforce they came from
     */
    public void advanceMonth(double[] places, PopulationCohorts pyramid,
                             PopulationManager people, LabourMarket market,
                             double staffedPayroll, double schoolUpkeep,
                             double attrition) {

        java.util.Arrays.fill(graduates, 0);
        java.util.Arrays.fill(licences, 0);
        finished = 0;
        java.util.Arrays.fill(enrolled, 0);
        tuitionCollected = 0;
        citySubsidyPaid = 0;
        payroll = Math.max(0, staffedPayroll);
        upkeep = Math.max(0, schoolUpkeep);

        /*
         * The pipeline moves before anything is measured: this month's
         * graduates leave it, everyone else is a month closer, and a share of
         * them died or moved away. Done even when there are no schools - a
         * city can demolish its university with four cohorts inside it, and
         * they still graduate; the building was the seats, not the students.
         */
        double keep = 1 - Math.max(0, Math.min(1, attrition));
        for (EducationType type : EducationType.values()) {
            if (!type.isAdult()) continue;
            double[] queue = inFlight[type.ordinal()];
            double finishing = queue.length > 0 ? queue[0] * keep : 0;
            for (int k = 0; k + 1 < queue.length; k++) queue[k] = queue[k + 1] * keep;
            if (queue.length > 0) queue[queue.length - 1] = 0;
            if (finishing <= 0) continue;
            // Counted by the rule refreshStudying() counts the body by.
            if (type.requires() != null) finished += finishing;
            if (type.isProfessional()) {
                licences[type.licenses().ordinal()] += finishing;
            } else if (type.produces() != null) {
                graduates[type.produces().ordinal()] += finishing;
                if (type.requires() != null) graduates[type.requires().ordinal()] -= finishing;
            }
        }

        if (places == null || pyramid == null || people == null) {
            refreshStudying();
            return;
        }

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

        refreshStudying();
    }

    /** Recounts who is in a lecture theatre, by the band they came from. */
    private void refreshStudying() {
        java.util.Arrays.fill(studying, 0);
        for (EducationType type : EducationType.values()) {
            if (!type.isAdult() || type.requires() == null) continue;
            studying[type.requires().ordinal()] += studentBody(type);
        }
    }

    /** Everybody part way through this course. */
    public double studentBody(EducationType type) {
        double total = 0;
        for (double cohort : inFlight[type.ordinal()]) total += cohort;
        return total;
    }

    /**
     * Adults out of the labour supply this month because they are studying,
     * by the band they hold NOW (the one they enrolled from). Jerus's call:
     * full-time students do not work. PopulationManager subtracts these from
     * the band's supply, so a city that sends four hundred diploma-holders to
     * college is four hundred workers short for two years - a real cost, and
     * the reason the wait is felt rather than merely recorded.
     */
    public double[] getStudying() { return studying; }

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
        /*
         * The intake: whoever wants to and can, up to the seats the school has
         * FREE. The seats are occupied by the students already in flight, so a
         * full school admits only as many as graduate this month - which is
         * places over course length, the steady-state throughput the first
         * version computed directly and handed out the same month.
         *
         * Nobody graduates here. They join the back of the queue and come out
         * of the front of it type.months() from now, in advanceMonth().
         */
        double[] queue = inFlight[i];
        double occupied = studentBody(type);
        double free = Math.max(0, seats - occupied);
        double intake = Math.min(free, eligible * willing * ENROLMENT_RATE);
        if (intake > 0 && queue.length > 0) {
            queue[queue.length - 1] += intake;
        }

        double body = studentBody(type);
        enrolled[i] = body;
        coverage[i] = cover(seats, Math.max(eligible, 1));
        charge(type, body);
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
        double outOfPocket = feeFor(type) * (1 - tuitionSubsidy);
        if (outOfPocket <= 0) return 1;

        double wage = market == null || from == null
                ? PayTier.UNSKILLED.getMonthlyWage()
                : Math.max(bandWage(market, from), 1e-6);

        double burden = outOfPocket / wage;
        return clamp(1 - burden / MAX_BURDEN);
    }

    /** Bills the month's tuition, split between the household and the treasury. */
    private void charge(EducationType type, double students) {
        double fee = feeFor(type) * Math.max(0, students);
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

    /**
     * What leaves the treasury: staff and buildings. Nothing else.
     *
     * THE SUBSIDY IS NOT A COST. It used to be added here, and that was two
     * mistakes wearing one line. The schools ARE the city, so the "subsidy" is
     * the city declining to bill itself - forgone revenue, not a second cheque
     * - and adding it made getNetCost() read
     *
     *     payroll + upkeep + s*fee - (1-s)*fee
     *
     * where the honest reading is payroll + upkeep - (1-s)*fee. On Jerus's slot
     * 7 that was $17.4M against a true $14.2M, every month, growing with the
     * dial. Healthcare next door has always had the right shape
     * (Healthcare.getGrossCost() is payroll + upkeep) and the two are meant to
     * be read side by side.
     *
     * The second mistake was that the money MOVED. getExpenses() debits this
     * figure from the treasury and nobody was ever credited the subsidy -
     * households pay tuition net of it and are handed nothing back. MoneyAudit
     * could not see it because households sit outside its pool, so a dollar a
     * month left the city and landed nowhere for as long as the line existed.
     * That is the same shape as the deposit-interest leak: conservation and
     * correct attribution are different questions, and only one of them had a
     * harness.
     *
     * The dial still does its work - a higher subsidy means less tuition
     * collected, so the city recovers less of the same cost - and
     * citySubsidyPaid is still measured and still on screen, as what the city
     * forgave rather than what it spent.
     */
    public double getGrossCost() { return payroll + upkeep; }
    public double getPayroll()   { return payroll; }
    public double getUpkeep()    { return upkeep; }

    /** Fees the city waived. Reported, not charged - see getGrossCost(). */
    public double getSubsidy()   { return citySubsidyPaid; }

    /** ...and what comes back from the households. */
    public double getFees()      { return tuitionCollected; }
    public double getNetCost()   { return getGrossCost() - tuitionCollected; }

    public double getCostRecovery() {
        double gross = getGrossCost();
        return gross > 0 ? tuitionCollected / gross : 0;
    }

    public double[] getEverGraduated() { return everGraduated; }

    /** Adults who finished a course this month - the gross flow out of getStudying(). */
    public double getFinished() { return finished; }

    /* ===================================================================
       WHAT THE SCREEN NEEDS TO EXPLAIN AN EMPTY SCHOOL

       READ-ONLY, AND NOTHING BELOW CHANGES A THING. Every one of these is a
       pure function of state this class already holds, exposed because the
       Services screen has to answer "the university is built and empty - why",
       and the answer is one of three gates: no seats, nobody eligible, or
       nobody willing. Recomputing them in the UI would be a second copy of the
       enrolment rule that could quietly disagree with this one.
       =================================================================== */

    /** Everybody part way through this course, cohort by cohort, nearest first. */
    public double[] cohortsInFlight(EducationType type) {
        return inFlight[type.ordinal()].clone();
    }

    /** What a household actually pays for a seat, after the subsidy. */
    public double outOfPocket(EducationType type) {
        return feeFor(type) * (1 - tuitionSubsidy);
    }

    /** How much better off somebody is for doing it - 0 means not worth it. */
    public double studyReturn(EducationType type, LabourMarket market) {
        return returnOn(type, market, type.requires());
    }

    /** What share of the eligible could pay the un-subsidised part. */
    public double studyAffordability(EducationType type, LabourMarket market) {
        return affordability(type, market, type.requires());
    }

    /** The two above, multiplied and capped: who actually enrols. */
    public double willingShare(EducationType type, LabourMarket market) {
        return participation(type, market, type.requires());
    }

    /**
     * The pool this course draws on, before anything else is applied.
     *
     * The same subtraction study() makes: a professional school does not
     * re-teach people who already hold its licence.
     */
    public double eligibleFor(EducationType type, PopulationManager people) {
        WageBand needs = type.requires();
        if (needs == null || people == null) return 0;
        double eligible = people.workforceByBand()[needs.ordinal()];
        if (type.isProfessional()) {
            eligible = Math.max(0, eligible - people.getLicensed(type.licenses()));
        }
        return eligible;
    }

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
        int size = everGraduated.length + 1;
        for (double[] queue : inFlight) size += queue.length;
        size += MONTH_FIELDS + coverage.length + enrolled.length + 1;
        double[] out = new double[size];
        int i = 0;
        out[i++] = tuitionSubsidy;
        for (double v : everGraduated) out[i++] = v;
        // Then every cohort in flight, course by course in enum order, each
        // course's queue in full - so a change to any course length changes
        // the array's length and the restore below refuses it whole.
        for (double[] queue : inFlight) for (double v : queue) out[i++] = v;
        /*
         * ...AND THE MONTH ITSELF, appended 2026-09-09.
         *
         * The class comment above says this is all flow and is rebuilt on the
         * first month back, and that is true - but "the first month back" is one
         * press of Next Month away, and until then a freshly loaded city showed
         * a fully staffed elementary school with nobody in it, 0% covered and a
         * bill of nothing. Finding #13 in claude/simulation-findings.md, filed
         * as "defensible design, bad first impression". Six doubles and two
         * short arrays is a cheap way to stop the screen lying, and it is the
         * same call the sector statements and the residents' statement both
         * made after being caught the same way.
         */
        out[i++] = payroll;
        out[i++] = upkeep;
        out[i++] = citySubsidyPaid;
        out[i++] = tuitionCollected;
        for (double v : coverage) out[i++] = v;
        for (double v : enrolled) out[i++] = v;
        // ...and who finished, appended 2026-09-11: the students' loans leave
        // with them the month after. See HouseholdBalance.setGraduates().
        out[i++] = finished;
        return out;
    }

    /** Scalars appended to the state array on 2026-09-09. See getState(). */
    private static final int MONTH_FIELDS = 4;

    /**
     * Refused whole on a length mismatch, never padded. The usual rule - with
     * one exception: the format-17 shape (the dial and the totals, nobody in
     * flight) is accepted and the schools start empty, because that is what
     * those cities looked like before the pipeline existed.
     */
    public void restore(double[] saved) {
        for (double[] queue : inFlight) java.util.Arrays.fill(queue, 0);
        java.util.Arrays.fill(studying, 0);

        payroll = 0; upkeep = 0; citySubsidyPaid = 0; tuitionCollected = 0;
        finished = 0;
        java.util.Arrays.fill(coverage, 0);
        java.util.Arrays.fill(enrolled, 0);

        int shortForm = everGraduated.length + 1;
        int fullForm = shortForm;
        for (double[] queue : inFlight) fullForm += queue.length;
        int withMonth = fullForm + MONTH_FIELDS + coverage.length + enrolled.length;
        int withFinished = withMonth + 1;

        if (saved == null || (saved.length != shortForm && saved.length != fullForm
                && saved.length != withMonth && saved.length != withFinished)) {
            tuitionSubsidy = DEFAULT_SUBSIDY;
            java.util.Arrays.fill(everGraduated, 0);
            return;
        }
        int i = 0;
        setTuitionSubsidy(saved[i++]);
        for (int b = 0; b < everGraduated.length; b++) everGraduated[b] = saved[i++];
        if (saved.length >= fullForm) {
            for (double[] queue : inFlight) for (int k = 0; k < queue.length; k++) queue[k] = saved[i++];
        }
        if (saved.length >= withMonth) {
            payroll = saved[i++];
            upkeep = saved[i++];
            citySubsidyPaid = saved[i++];
            tuitionCollected = saved[i++];
            for (int k = 0; k < coverage.length; k++) coverage[k] = saved[i++];
            for (int k = 0; k < enrolled.length; k++) enrolled[k] = saved[i++];
        }
        if (saved.length == withFinished) finished = Math.max(0, saved[i]);
        refreshStudying();
    }

    /** Tuition and this month's bill, in the new unit. The subsidy is a share. */
    public void redenominate(double scale) {
        tuitionCollected *= scale;
        citySubsidyPaid  *= scale;
        payroll *= scale;
        upkeep  *= scale;
        // ...and the price list itself. See the note on the tuition table.
        for (int i = 0; i < tuition.length; i++) tuition[i] *= scale;
    }

}
