package ham.citybuildersim;

import java.util.Random;

/**
 * How much of the workforce is off sick this month.
 *
 * WHAT THIS IS NOT: a second population model. Nobody dies here, nobody leaves,
 * and getWorkforce() never moves. The city has exactly as many adults on the
 * first of the month as it did on the last of the previous one, and their names
 * are still on the payroll. What changes is how much work gets done.
 *
 * SO IT IS A FOURTH UTILISATION RATIO, alongside energy, water and roads, and
 * that is the whole design. Those three already exist, already multiply into
 * output, already get carried across a save as a basis, and are already
 * understood by every sector - so sickness costs the codebase one more
 * multiplier rather than a new mechanism. A city short of power, short of water,
 * gridlocked and mid-epidemic is worse off than any one of those alone, which is
 * what multiplying gets you for free.
 *
 * THE EMPLOYER CARRIES IT, per Jerus. Payroll is charged on who is STAFFED, not
 * on who turned up: a mill with fifty people on the books pays fifty wages in a
 * bad flu month and sells four weeks of output in three. That is what makes
 * sickness bite - it is a margin squeeze, not a headcount cut - and it is the
 * reason this multiplies revenue and not rPayroll. It also means the households
 * are untouched: a sick worker still gets paid, still shops, still pays rent.
 *
 * TWO THINGS SET THE RATE
 *
 *   1. COVERAGE, which is the standing state. General-care beds divided by the
 *      people who might need them. A city with none runs at the untreated rate
 *      permanently; a city with enough runs near the floor. Nobody reaches zero
 *      - people get sick in the best-served city on earth.
 *
 *   2. OUTBREAKS, which are the events. Rare, sharp, and they decay. Hospitals
 *      blunt an outbreak rather than preventing it, so a well-covered city still
 *      gets them and still notices - it just survives them.
 *
 * WHY THE OUTBREAK ROLL IS A FUNCTION OF THE MONTH NUMBER. Seeding a fresh
 * Random from the month index means the sequence of outbreaks is fixed for a
 * city and reproducible across a save, WITHOUT the generator's internal state
 * having to be written to the save file and read back. A save carries the
 * severity that is currently decaying, which is a fact about the city, and
 * nothing else. Reload in month 340 and month 340's roll comes out the same way
 * it did the first time - so an outbreak cannot be save-scummed away, which is
 * the same property that made the consecutive-loss streaks worth carrying.
 */
public class Health {

    /* ===================================================================
       THE BASELINE

       Both ends are monthly absence rates for the whole workforce, not
       annual, and both are deliberately survivable. Three percent is roughly
       what a functioning modern economy loses to ordinary illness; eighteen
       is a city with no clinics at all, which is bad enough to be worth
       fixing and not so bad that a young city cannot get off the ground.

       The gap between them - fifteen points of output - is what a full
       healthcare programme is worth, and it is the number to move if
       hospitals turn out to be too cheap or too expensive to be interesting.
       =================================================================== */

    /** Absence when general care covers everybody. */
    public static final double WELL_SERVED_RATE = .03;

    /** Absence with no general care at all. */
    public static final double UNTREATED_RATE = .18;

    /** No month loses more of the workforce than this, outbreak included. */
    public static final double MAX_SICK_RATE = .45;

    /* ===================================================================
       THE DEAD NOBODY BURIED

       A city with no cemetery and no crematorium does not stop having
       funerals; it stops having them somewhere. The backlog Healthcare keeps
       is a share of the population lying unhandled, and it makes the living
       ill - which is both the historical answer and, mechanically, the reason
       death care is urgent rather than decorative. It reaches output through
       the ratio that already exists rather than through a new penalty.

       THE SCALE, because a share-of-population term is easy to get wrong by a
       factor of a hundred. Deaths run about 0.04% of the population a month,
       so a full year with no death care at all leaves roughly 0.5% of the city
       unburied - which at a weight of 20 is a ten-point rise in the sick rate.
       A year of neglect costing a tenth of the city's output is meant to be
       alarming and survivable, and the cap keeps the worst case from
       compounding with an outbreak into a spiral.
       =================================================================== */

    /** Multiplier on the unburied share of the population. */
    public static final double UNBURIED_WEIGHT = 20;

    /** However many are lying about, this is the most it can cost. */
    public static final double MAX_UNBURIED_SICKNESS = .15;

    /* ===================================================================
       OUTBREAKS

       About one every five years, lasting three or four months as the
       severity decays. A new one cannot start while the last is still
       running, so they read as discrete events rather than as noise.
       =================================================================== */

    /** Chance per month that an outbreak begins, when none is running. */
    public static final double OUTBREAK_CHANCE = 1 / 60.0;

    /** Extra absence at the peak of an outbreak, before coverage blunts it. */
    public static final double OUTBREAK_MIN_PEAK = .08;
    public static final double OUTBREAK_MAX_PEAK = .25;

    /** What is left of the peak each following month. */
    public static final double OUTBREAK_DECAY = .55;

    /** Below this the outbreak is over. */
    public static final double OUTBREAK_FLOOR = .005;

    /**
     * How much of an outbreak's peak good coverage can take off.
     *
     * Not 1: a fully covered city still loses 40% of the peak, because a
     * hospital treats an epidemic, it does not prevent one. If this were 1 the
     * correct play would be to build enough beds once and never think about
     * disease again, which is the opposite of interesting.
     */
    public static final double OUTBREAK_MITIGATION = .60;

    /** Fixed, like LandMarket's. The city's outbreaks are its own. */
    private static final long SEED = 411_902_537_009L;

    /* ------------------------------ state ------------------------------ */

    /** What is left of the current outbreak. Zero when the city is well. */
    private double outbreakSeverity;

    /** The month the current outbreak began, for the screen. Zero if none. */
    private int outbreakStarted;

    /** Last month's figures, so the UI and the harness can read them. */
    private double sickRate;
    private double coverage = 1;
    private double baselineRate = WELL_SERVED_RATE;

    /** What the unburied dead are adding this month. Zero in a tidy city. */
    private double unburiedRate;

    /* ---------------------------- the month ---------------------------- */

    /** The old three-argument form: a city with nothing left lying about. */
    public void advanceMonth(double generalCareCapacity, double population, int month) {
        advanceMonth(generalCareCapacity, population, month, 0);
    }

    /**
     * Works out this month's sick rate.
     *
     * @param generalCareCapacity beds the city has finished building AND staffed
     * @param population          everyone general care is on the hook for
     * @param month               the game month, which seeds the outbreak roll
     * @param unburied            the dead the city has nowhere to put
     */
    public void advanceMonth(double generalCareCapacity, double population, int month,
                             double unburied) {

        coverage = coverageOf(generalCareCapacity, population);
        baselineRate = WELL_SERVED_RATE
                + (UNTREATED_RATE - WELL_SERVED_RATE) * (1 - coverage);

        /*
         * Decay first, then roll. In that order a month that ends an outbreak
         * can start the next one, which matters only for the pathological case
         * of back-to-back epidemics - but getting it the other way round means
         * an outbreak rolled this month is immediately decayed by the same
         * month's decay step, so it never appears at full strength at all.
         */
        outbreakSeverity *= OUTBREAK_DECAY;
        if (outbreakSeverity < OUTBREAK_FLOOR) {
            outbreakSeverity = 0;
            outbreakStarted = 0;

            Random roll = new Random(scramble(SEED + month));
            if (roll.nextDouble() < OUTBREAK_CHANCE) {
                double peak = OUTBREAK_MIN_PEAK
                        + roll.nextDouble() * (OUTBREAK_MAX_PEAK - OUTBREAK_MIN_PEAK);
                outbreakSeverity = peak * (1 - OUTBREAK_MITIGATION * coverage);
                outbreakStarted = month;
            }
        }

        unburiedRate = population > 0
                ? Math.min(MAX_UNBURIED_SICKNESS,
                        Math.max(0, unburied) / population * UNBURIED_WEIGHT)
                : 0;

        sickRate = Math.min(MAX_SICK_RATE,
                baselineRate + outbreakSeverity + unburiedRate);
    }

    /**
     * Share of the people general care has to serve that it has room for.
     *
     * A city with nobody in it is fully covered rather than in crisis, which is
     * the guard CareType.populationServed() warns about: dividing by an empty
     * denominator would otherwise found every city mid-plague.
     */
    public static double coverageOf(double capacity, double population) {
        if (population <= 0) return 1;
        return Math.max(0, Math.min(1, capacity / population));
    }

    /* ---------------------------- reading it ---------------------------- */

    /** The share of the workforce that is off sick. */
    public double getSickRate() { return sickRate; }

    /**
     * What sickness leaves of the month's output: 1 when nobody is ill.
     *
     * This is the number the handlers multiply by, and it is deliberately the
     * complement rather than the rate itself, so it reads exactly like
     * energyRatio and waterRatio at every call site.
     */
    public double getWorkRatio() { return 1 - sickRate; }

    /** Beds per person, capped at 1. */
    public double getCoverage() { return coverage; }

    /** What the rate would be with no outbreak running. */
    public double getBaselineRate() { return baselineRate; }

    /** What the current outbreak is adding on top. Zero when the city is well. */
    public double getOutbreakSeverity() { return outbreakSeverity; }

    /** What the unburied dead are adding on top. Zero in a city that buries them. */
    public double getUnburiedRate() { return unburiedRate; }

    public boolean isOutbreak() { return outbreakSeverity > 0; }

    /** The month the running outbreak began, or 0. */
    public int getOutbreakStarted() { return outbreakStarted; }

    /* ------------------------------ saving ------------------------------ */

    /**
     * The state, in order. New fields go on the END - see restore().
     *
     * The coverage and the baseline are derived and could be recomputed, but
     * they are the two numbers the month's report was WRITTEN against, and this
     * codebase has been bitten repeatedly by recomputing a fact about a month
     * from the state that month ended in. Carrying them costs two doubles.
     */
    public double[] getState() {
        return new double[] {
            outbreakSeverity, outbreakStarted, sickRate, coverage, baselineRate,
            unburiedRate
        };
    }

    /**
     * Puts a saved month's health back.
     *
     * Refused whole on a length mismatch, the standing rule: a partially
     * restored Health would hand back a city whose sick rate and outbreak
     * disagreed, which is worse than a city that starts the month well. A save
     * from before this existed has no array at all and lands here as null,
     * which is the same case.
     */
    public boolean restore(double[] state) {
        if (state == null || state.length != getState().length) return false;

        outbreakSeverity = state[0];
        outbreakStarted  = (int) state[1];
        sickRate         = state[2];
        coverage         = state[3];
        baselineRate     = state[4];
        unburiedRate     = state[5];
        return true;
    }

    /** SplitMix64, same as LandMarket's, so adjacent months look unrelated. */
    private static long scramble(long value) {
        long z = value + 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
