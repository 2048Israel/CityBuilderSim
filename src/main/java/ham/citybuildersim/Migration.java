package ham.citybuildersim;

/**
 * Why people move to this city, and the much narrower question of why they leave.
 *
 * This class is what replaced `min(housing, jobs * 2.25)`. That expression was
 * the population: derived fresh every month from nothing, with no memory, so a
 * finished tower filled instantly and a demolished one erased its residents. It
 * is now a TARGET that migration chases, and the chasing is what gives the city
 * inertia.
 *
 * ==================== JERUS'S SPEC ====================
 *
 *   "make it so its city housing space and current jobs available. If a city is
 *    full but has jobs, they'll still move in."
 *
 *   "homes pull too, but not as much as jobs. Crowding slows but jobs is still
 *    the main factor."
 *
 *   "they only leave if the respective job tier cashflow is declining for 12
 *    months straight or is zero."
 *
 * ======================================================
 *
 * THE PULL is a weighted target, jobs three parts to housing one:
 *
 *   jobs  -> every post supports RESIDENTS_PER_JOB people, worker and household
 *   homes -> the people the city's residential buildings comfortably hold
 *
 * Weighting them rather than taking the smaller of the two is the whole change
 * Jerus asked for. A minimum makes housing a GATE - build one house too few and
 * the city stops dead no matter how many jobs are going begging. A weighted sum
 * makes it a PULL: a city with jobs and no houses still attracts people, they
 * just arrive into a shortage. Which is what actually happens, and what the
 * flatshare model in FamilyModel was built to absorb.
 *
 * Housing gets a say twice, and the second is the interesting one: it also
 * DAMPS. A crowded city keeps attracting people, more slowly, until there is
 * physically nowhere left to put them - and that point is asked of FamilyModel
 * rather than typed in here, because it has to be the same line as the one where
 * the squeeze runs out of valves. See crowdingFactor().
 *
 * THE PUSH is deliberately hard to trigger. A negative gap does NOT empty a
 * city; a city with more people than jobs is a city with unemployment, not a
 * city with an exodus. People only leave when a pay tier has been shrinking for
 * a solid year or has stopped paying anything at all - which means a bust takes
 * a year to start and then years to play out, and a bad month is just a bad
 * month. That asymmetry is the point of the twelve-month gate.
 */
public class Migration {

    /* ------------------------------- the dials ------------------------------- */

    /* =====================================================================
       HOW MANY RESIDENTS A JOB SUPPORTS

       This used to be the constant 2.25, inherited from Jerus's original
       `totalJobs * (1 + adultPercent) * 1.5` with adultPercent at .5. Nobody
       ever typed 2.25; it was 1.5 x 1.5, and what it MEANT was buried.

       What it meant, reconstructed: with half the city working, two residents
       staff one job exactly. At exactly two the city would have precisely enough
       workers for precisely the jobs - no unemployment, every adult employed,
       and a labour market with no slack at all, oscillating between fully
       staffed and one person short. 2.25 is 12.5% more workers than posts, which
       is about 11% unemployment.

       So 2.25 = 1.125 / 0.5, and written that way it derives itself.

       WHY IT HAD TO STOP BEING A CONSTANT. The workforce is no longer half the
       city - it is the actual adults, around 57.5% in a settled one. The same
       2.25 then supplies 1.294 workers per post rather than 1.125, and the
       number that meant "11% unemployment" quietly came to mean 23%. It drifted
       without anyone touching it, because the thing underneath it moved.

       Now it follows the age structure. A city that ages has fewer workers per
       resident and therefore needs more residents per job, and the target moves
       with it instead of disagreeing with it in silence. At a 50% adult share
       this returns exactly 2.25 - Jerus's number, reconstructed rather than
       replaced.
       ===================================================================== */

    /** Workers per post the city aims to have, over and above one each. */
    public static final double TARGET_LABOUR_SLACK = .125;

    /**
     * Below this the arithmetic stops meaning anything - a city that is 10%
     * adults would demand ten residents per job and grow without limit. Nothing
     * plausible comes near it; it is here so nothing implausible can.
     */
    public static final double MIN_ADULT_SHARE = .25;

    public static double residentsPerJob(double adultShare) {
        double share = adultShare > 0 ? adultShare
                : PopulationCohorts.equilibriumShare(AgeBand.ADULT);
        return (1 + TARGET_LABOUR_SLACK) / Math.max(MIN_ADULT_SHARE, share);
    }

    /** Jobs are the main factor, per Jerus. Homes pull, but less. */
    public static final double JOB_WEIGHT  = .75;
    public static final double HOME_WEIGHT = .25;

    /* ===================================================================
       WHY A CITY WITH GOOD SENIOR CARE IS WORTH MOVING TO

       Jerus's, and it is the answer to a problem the model created for
       itself. Seniors are pure burden here: they draw a pension, occupy a
       home, need the most expensive care in the game and work not at all. An
       ageing pyramid was therefore something to be endured, and the only
       rational play was to hope your city stayed young - which is not a game,
       it is a wait.

       So senior care buys something. At full coverage the city is 30% more
       attractive to everybody, not just to the old: places that look after
       their parents are places people are willing to raise children. It is a
       multiplier on the TARGET rather than on the arrival rate, because the
       claim is about how big a city these conditions support, not how fast it
       fills - the same distinction JOB_WEIGHT and ARRIVAL_RATE already make.
       =================================================================== */

    /** How much more attractive full senior coverage makes the city. */
    public static final double SENIOR_CARE_PULL = .30;

    /** The multiplier on the target, given senior-care coverage. */
    public static double seniorCarePull(double seniorCoverage) {
        return 1 + SENIOR_CARE_PULL * Math.max(0, Math.min(1, seniorCoverage));
    }

    /**
     * How much of the gap closes each month.
     *
     * Arrivals are fast and departures are slow, which is how cities actually
     * behave - a boom fills in months and a bust empties over years. At 15% a
     * month a city closes most of a gap inside half a year; at 5% a shrinking
     * one takes about a year to shed a quarter of the surplus, and that is on
     * top of the twelve months it takes to qualify at all.
     */
    public static final double ARRIVAL_RATE   = .15;
    public static final double DEPARTURE_RATE = .05;

    /** Consecutive months of falling wages before a tier's people give up. */
    public static final int DECLINE_MONTHS = 12;

    /* ------------------------------ what it carries ------------------------------ */

    private static final int TIERS = PayTier.values().length;

    /**
     * A rolling year of each tier's wage bill, oldest at index 0.
     *
     * SAVED, and it has to be. A streak is a flow, and this codebase has been
     * caught four times now by the same thing: you cannot reconstruct a flow from
     * the state a month ended in. A reloaded city that forgot its history would
     * have every tier's streak reset to zero and could not shed a single person
     * for a year, which is exactly the kind of quiet difference between a saved
     * game and a played one that this project keeps hunting down.
     */
    private final double[][] history = new double[TIERS][DECLINE_MONTHS];
    private final int[] decliningStreak = new int[TIERS];
    private int monthsRecorded;

    /* Last month's working, purely so the screen can show it. */
    private double lastTarget;
    private double lastArrivals;
    private double lastDepartures;
    private double lastCrowding = 1;
    private double lastDecliningShare;
    private double lastSeniorPull = 1;
    private double lastResidentsPerJob = residentsPerJob(0);

    /* ------------------------------- reading ------------------------------- */

    public double getLastTarget()         { return lastTarget; }
    /** What senior care multiplied the target by. 1 when there is none. */
    public double getLastSeniorPull()     { return lastSeniorPull; }
    public double getLastArrivals()       { return lastArrivals; }
    public double getLastDepartures()     { return lastDepartures; }
    public double getLastCrowding()       { return lastCrowding; }
    public double getLastDecliningShare() { return lastDecliningShare; }
    public double getLastResidentsPerJob() { return lastResidentsPerJob; }
    public double getLastNet()            { return lastArrivals - lastDepartures; }

    public int getDecliningStreak(PayTier tier) {
        return decliningStreak[tier.ordinal()];
    }

    /** True once there is a full year of history to judge a streak against. */
    public boolean hasFullHistory() {
        return monthsRecorded >= DECLINE_MONTHS;
    }

    /**
     * True if this tier's people are entitled to leave.
     *
     * Two ways in, both Jerus's: a full year of consecutive decline, or a tier
     * that used to pay something and now pays nothing. The second is not
     * redundant - a plant that closes takes its tier to zero in one month, and
     * waiting a further year to notice would be absurd.
     */
    public boolean isDeclining(PayTier tier) {
        if (!hasFullHistory()) return false;
        int t = tier.ordinal();
        if (decliningStreak[t] >= DECLINE_MONTHS) return true;
        return newest(t) <= 0 && oldest(t) > 0;
    }

    private double newest(int t) { return history[t][DECLINE_MONTHS - 1]; }
    private double oldest(int t) { return history[t][0]; }

    /* ------------------------------- recording ------------------------------- */

    /**
     * Files this month's wage bill per tier and updates every streak.
     *
     * Called once a month, before the migration is worked out, so the decision
     * sees the month it is deciding about.
     */
    public void recordWages(double[] wagePerTier) {
        if (wagePerTier == null || wagePerTier.length != TIERS) {
            return;   // refused whole, per the standing rule on state arrays
        }

        for (int t = 0; t < TIERS; t++) {
            double previous = newest(t);

            System.arraycopy(history[t], 1, history[t], 0, DECLINE_MONTHS - 1);
            history[t][DECLINE_MONTHS - 1] = wagePerTier[t];

            // A streak needs something to have declined FROM, so month one of a
            // tier's existence is not a decline - it is an arrival.
            if (monthsRecorded > 0 && wagePerTier[t] < previous) {
                decliningStreak[t]++;
            } else {
                decliningStreak[t] = 0;
            }
        }
        monthsRecorded++;
    }

    /**
     * How much of the city's payroll sits in tiers whose people may leave.
     *
     * Weighted by what each tier was worth a YEAR AGO rather than today, and
     * that is the load-bearing detail. FamilyModel reassigns households to tiers
     * every month from the current job mix, so the month a tier's jobs vanish its
     * households vanish with them - weighting by today's payroll would give a
     * collapsed tier a weight of zero and let a city lose its entire steel
     * industry without losing a single resident. The people who leave are the
     * people who were there before it collapsed, so the weight is what the tier
     * was worth before it collapsed.
     */
    public double decliningShare() {
        if (!hasFullHistory()) return 0;

        double thenTotal = 0;
        for (int t = 0; t < TIERS; t++) thenTotal += oldest(t);
        if (thenTotal <= 0) return 0;

        double share = 0;
        for (PayTier tier : PayTier.values()) {
            if (isDeclining(tier)) share += oldest(tier.ordinal()) / thenTotal;
        }
        return Math.min(1, Math.max(0, share));
    }

    /**
     * How far a tier has fallen over the year, 0 to 1.
     *
     * The gate says WHETHER a trade is dying; this says HOW BADLY, and both are
     * needed. A tier that has slipped 3% across twelve months and one that has
     * shut its doors both pass the gate, and treating them the same would empty a
     * city over a rounding error.
     */
    public double severity(PayTier tier) {
        if (!isDeclining(tier)) return 0;
        int t = tier.ordinal();
        double then = oldest(t);
        if (then <= 0) return 0;
        return Math.max(0, Math.min(1, 1 - newest(t) / then));
    }

    /**
     * The share of the city's payroll that has actually been destroyed, as
     * opposed to merely sitting in a tier that qualifies.
     *
     * This is what departures are charged against: each tier's weight a year ago
     * times how much of it has gone. A city where one small trade has slipped
     * loses almost nobody; a city whose main employer has closed loses people
     * steadily.
     */
    public double decliningPressure() {
        if (!hasFullHistory()) return 0;

        double thenTotal = 0;
        for (int t = 0; t < TIERS; t++) thenTotal += oldest(t);
        if (thenTotal <= 0) return 0;

        double pressure = 0;
        for (PayTier tier : PayTier.values()) {
            pressure += oldest(tier.ordinal()) / thenTotal * severity(tier);
        }
        return Math.min(1, Math.max(0, pressure));
    }

    /* ------------------------------- deciding ------------------------------- */

    /**
     * How crowded the city is, as a multiplier on arrivals: 1 is room to spare,
     * 0 is physically full.
     *
     * The zero point is asked of FamilyModel, not chosen here. It is the fewest
     * homes the current households could crowd into once every single adult has
     * taken a flatshare and every doubling the squeeze allows has happened -
     * below that line somebody would have nowhere to go, and nobody is homeless
     * in this model. So arrivals reach zero at exactly the same point the
     * crowding valves run out, and PopulationCheck asserts they agree.
     *
     * Between one-home-each and that floor it falls off linearly. There is no
     * defence of linear beyond its being the shape that makes the endpoints mean
     * what they say; the endpoints are the part that matters.
     */
    public double crowdingFactor(int homes, FamilyModel families) {
        double comfortable = families.totalHouseholds();
        if (comfortable <= 0) return 1;               // nobody here yet

        double floor = families.minimumHomesTolerable();
        if (homes >= comfortable) return 1;
        if (homes <= floor) return 0;

        return (homes - floor) / (comfortable - floor);
    }

    /**
     * The month's net migration: positive is people arriving.
     *
     * @param population       who lives here now
     * @param totalJobs        posts the city's buildings offer
     * @param householdCapacity people its residential buildings comfortably hold
     * @param homes            front doors, which is a different number
     * @param families         the household mix, for the crowding floor
     */
    public double monthlyNet(int population, int totalJobs, int householdCapacity,
                             int homes, FamilyModel families, double adultShare) {
        return monthlyNet(population, totalJobs, householdCapacity, homes,
                families, adultShare, 0);
    }

    /**
     * The same, with the draw good senior care adds.
     *
     * @param seniorCoverage places in senior care against the seniors who need
     *                       them; 0 in a city that has built none, which is
     *                       exactly how every city behaved before this existed
     */
    public double monthlyNet(int population, int totalJobs, int householdCapacity,
                             int homes, FamilyModel families, double adultShare,
                             double seniorCoverage) {

        lastResidentsPerJob = residentsPerJob(adultShare);
        double jobTarget  = totalJobs * lastResidentsPerJob;
        double homeTarget = householdCapacity;

        lastSeniorPull = seniorCarePull(seniorCoverage);
        lastTarget = (JOB_WEIGHT * jobTarget + HOME_WEIGHT * homeTarget) * lastSeniorPull;
        lastArrivals = 0;
        lastDepartures = 0;
        lastCrowding = 1;
        lastDecliningShare = 0;

        double gap = lastTarget - population;
        lastCrowding = crowdingFactor(homes, families);

        /*
         * GROSS FLOWS, NOT NET. The two directions are computed independently
         * and netted at the end, and that is a deliberate correction rather than
         * how it was first written.
         *
         * The first version made departures the NEGATIVE BRANCH of arrivals -
         * people could only leave a city that was already oversized. Measured
         * over four thousand months: 53,528 people moved in and NOBODY EVER
         * LEFT. The decline gate opened fifteen times and never once mattered,
         * because a growing city never satisfied the other half of the
         * condition. An entire mechanic existed only in the harness.
         *
         * Both things happen at once in a real city, and they have to here: a
         * town whose steelworks has closed loses steelworkers even while its
         * shops are hiring. The cost is that a city can now bleed people while
         * its headline number still rises, which is exactly what a place hollowing
         * out looks like from the inside.
         */
        lastArrivals = gap > 0 ? gap * ARRIVAL_RATE * lastCrowding : 0;

        /*
         * And the push. A city with more people than jobs is NOT by itself a
         * reason for anybody to leave - that is unemployment, not an exodus.
         * Somebody only goes when the tier they earn in has been shrinking for a
         * full year or has stopped paying at all, and then in proportion to how
         * much of it has actually gone.
         */
        lastDecliningShare = decliningShare();
        lastDepartures = population * decliningPressure() * DEPARTURE_RATE;

        // Never evacuate. A month that would remove more people than live here
        // is arithmetic going wrong, not a city emptying.
        lastDepartures = Math.min(lastDepartures, population);

        return lastArrivals - lastDepartures;
    }

    /* ------------------------------- saving ------------------------------- */

    public double[] toSaveArray() {
        double[] out = new double[TIERS * DECLINE_MONTHS + TIERS + 1];
        int i = 0;
        for (double[] row : history) {
            for (double v : row) out[i++] = v;
        }
        for (int s : decliningStreak) out[i++] = s;
        out[i] = monthsRecorded;
        return out;
    }

    public void restore(double[] saved) {
        int expected = TIERS * DECLINE_MONTHS + TIERS + 1;
        if (saved == null || saved.length != expected) {
            return;   // refused whole rather than half-read
        }
        int i = 0;
        for (double[] row : history) {
            for (int m = 0; m < row.length; m++) row[m] = saved[i++];
        }
        for (int t = 0; t < TIERS; t++) decliningStreak[t] = (int) saved[i++];
        monthsRecorded = (int) saved[i];
    }

    public void reset() {
        for (double[] row : history) java.util.Arrays.fill(row, 0);
        java.util.Arrays.fill(decliningStreak, 0);
        monthsRecorded = 0;
        lastTarget = 0;
        lastArrivals = 0;
        lastDepartures = 0;
        lastCrowding = 1;
        lastDecliningShare = 0;
        lastSeniorPull = 1;
        lastResidentsPerJob = residentsPerJob(0);
    }
}
