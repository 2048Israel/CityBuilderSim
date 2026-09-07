package ham.citybuildersim;

/**
 * What labour costs, and why it costs that.
 *
 * WHY THIS EXISTS
 *
 * Labour was the last thing in the game that was not a market. Iron prices off
 * scarcity, land prices off scarcity, the city's borrowing rate prices off its
 * own books - and a wage was one of six constants in PayTier, copied into
 * PopulationManager once at startup and never moved again. A city that could
 * not staff a hospital paid its doctors exactly what a city with a queue of
 * them paid.
 *
 * That is the same complaint the backlog already makes about rent, one system
 * over and worse, because labour is an input to everything.
 *
 * THE MODEL
 *
 *     base   = minimumWage x ratio[type]
 *     tight  = posts / qualified workers
 *     target = base x clamp(tight ^ ELASTICITY, MIN_MULTIPLE, MAX_MULTIPLE)
 *     wage  += (target - wage) x ADJUST_RATE
 *     wage   = max(wage, minimumWage)
 *
 * Four things in there are load-bearing and none of them is the exponent.
 *
 * THE MINIMUM WAGE IS THE BASE. Jerus: "make the base a modifiable min wage".
 * It is not merely a floor under the lowest band - the whole ladder is a
 * multiple of it, so raising it lifts every wage in the city and the player has
 * one dial that moves the entire labour cost of the economy. The multiples are
 * READ OFF PayTier rather than invented (see ratioOf), which gives the property
 * that matters: at the default minimum wage this market pays exactly what the
 * game paid before it existed. Every balance measurement already taken survives.
 *
 * IT IS CLAMPED. A purely relative price has no absolute level, and one
 * unfilled post in a city with no doctors is infinite scarcity. Same reasoning
 * as the credit spread stopping at eight points.
 *
 * IT IS DAMPED. People take months to move, so the supply response lags - and a
 * lagged negative feedback loop oscillates rather than settles. That is the
 * cobweb cycle, and undamped it produces wage swings that read as a bug rather
 * than as economics. ADJUST_RATE is what stops the city hunting.
 *
 * AND THE FLOOR IS A POLICY, WHICH IS THE INTERESTING PART. Without one:
 * unemployment drives wages down, low wages drive people out, a smaller city
 * needs fewer workers, and it spirals. The minimum wage stops that - and
 * because the unskilled base IS the minimum wage, an unskilled surplus cannot
 * be priced away at all. The adjustment has to happen in QUANTITY instead, which
 * is exactly what a binding minimum wage does in the real world and exactly
 * what Migration's surplus departures now model. The player sets the number and
 * lives with which of the two costs they would rather pay.
 */
public class LabourMarket {

    /* ===================================================================
       THE DIAL
       =================================================================== */

    /**
     * Where the minimum wage starts, and therefore what the whole ladder is
     * anchored to on month one.
     *
     * Deliberately the unskilled PayTier wage, so a new city's payroll is
     * penny-identical to what it was before this class existed.
     */
    public static final double DEFAULT_MINIMUM_WAGE = PayTier.UNSKILLED.getMonthlyWage();

    /** Bounds on the dial, so the screen cannot ask for a negative wage. */
    public static final double MIN_SETTABLE = .200;
    public static final double MAX_SETTABLE = 4.000;

    private double minimumWage = DEFAULT_MINIMUM_WAGE;

    /* ===================================================================
       THE CURVE
       =================================================================== */

    /**
     * How hard a shortage pushes the wage.
     *
     * The square root: twice as many posts as workers is about 1.4x the wage,
     * four times is 2x. Gentler than linear on purpose - a wage that doubles
     * the month a hospital opens is not a labour market, it is a step function
     * with a delay.
     */
    public static final double ELASTICITY = .5;

    /**
     * How far above base a wage can climb.
     *
     * Jerus's call, and it is a real choice about what kind of game this is:
     * at 4x a rich city can always buy its way out of a shortage, so labour is
     * a cost line rather than a wall. Shortages hurt the budget instead of
     * stopping the city, which is the forgiving end of the range - and for a
     * game whose failure states are already mostly financial, consistent.
     */
    public static final double MAX_MULTIPLE = 4.0;

    /** ...and how far below, before the minimum wage catches it anyway. */
    public static final double MIN_MULTIPLE = .70;

    /**
     * How much of the gap to its target a wage closes each month.
     *
     * Jerus: "just supply vs demand but lagged". At .12 a wage covers about
     * three quarters of a step change in a year and effectively all of it in
     * two, which is roughly how long it takes a person to notice a labour
     * shortage, retrain or move for it, and arrive.
     */
    public static final double ADJUST_RATE = .12;

    /**
     * How far from its floor a wage counts as PINNED.
     *
     * Migration reads this: a band whose wage has fallen as far as it is allowed
     * to go and which still has more workers than posts is a band the price
     * mechanism has given up on, and the surplus leaves instead. Without a
     * tolerance the test is an equality on a damped double, which is never true.
     */
    public static final double PINNED_TOLERANCE = .02;

    private final double[] wage = new double[JobType.values().length];

    /**
     * Scarcity per BAND, not per job type.
     *
     * Because supply is per band. Workers are fungible inside a band - see
     * WageBand - and things that substitute freely for each other clear at one
     * price. So the whole UNIVERSITY band shares a multiplier, while its job
     * types still pay differently because their BASES differ: a doctor's base
     * is 8.0 and a policy officer's is 6.0, and a 1.4x university-wide shortage
     * lifts both by the same proportion.
     */
    private final double[] tightness = new double[WageBand.values().length];

    /*
     * THE LICENCE PREMIUM (2026-09-06).
     *
     * Scarcity is priced per band because workers are fungible inside a band -
     * except where they are not. A doctor's post can only be filled by a
     * licence holder, and until today an empty doctor's post moved the whole
     * university band's wage and nothing else: the one price that should
     * have been screaming for a medical school was a band average. Worse,
     * two other mechanisms read that price - the share of arriving graduates
     * who already hold a licence (Migration) and the return on studying for
     * one (Education) - and neither could ever respond to a doctor shortage,
     * because there was no doctor price to respond to.
     *
     * So a gated job carries a second multiplier of its own, on top of its
     * band's: posts over licence holders, same elasticity, never below one
     * (a glut of doctors earns the band wage; the band's multiple handles
     * that), and the two together never exceed MAX_MULTIPLE, which is Jerus's
     * ceiling on what any wage can reach.
     */
    private final double[] licenceTightness = new double[JobType.values().length];
    private final double[] licenceMultiple  = new double[JobType.values().length];
    private final double[] bandMultiple     = new double[WageBand.values().length];

    /** True for a job that only a licence holder can fill - see EducationType. */
    public static boolean isGated(JobType job) {
        for (EducationType type : EducationType.values()) {
            if (type.isProfessional() && type.licenses() == job) return true;
        }
        return false;
    }

    public LabourMarket() {
        resetToBase();
    }

    /* ===================================================================
       THE LADDER
       =================================================================== */

    /**
     * What this job pays relative to the unskilled floor.
     *
     * DERIVED FROM PayTier, NOT TYPED OUT. PayTier is already "the one
     * definition" of the wage ladder and says so in its own comment; a second
     * table of multiples here would be right the day it was written and wrong
     * the first time somebody rebalanced a tier. Deriving it also gives the
     * neutrality property: minimumWage x ratio reproduces PayTier exactly when
     * the dial is at its default.
     */
    public static double ratioOf(JobType job) {
        double floor = PayTier.UNSKILLED.getMonthlyWage();
        return floor > 0 ? PayTier.wageOf(job) / floor : 1;
    }

    /** What this job would pay in a city with exactly enough people for it. */
    public double baseWage(JobType job) {
        return minimumWage * ratioOf(job);
    }

    /* ===================================================================
       THE MONTH
       =================================================================== */

    /**
     * Re-prices every job type against how hard it is to staff.
     *
     * @param posts             positions that exist, per JobType
     * @param qualifiedSupply   workers who could hold that post, per JobType -
     *                          from PopulationManager, which knows the skill
     *                          composition and how it cascades down the bands
     */
    public void advanceMonth(double[] bandPosts, double[] bandSupply) {
        advanceMonth(bandPosts, bandSupply, null, null);
    }

    /**
     * @param jobPosts      posts per job type, for the licence premium; null to price bands only
     * @param licensedHeads licence holders per job type, likewise
     */
    public void advanceMonth(double[] bandPosts, double[] bandSupply,
                             int[] jobPosts, double[] licensedHeads) {

        double[] multiple = new double[WageBand.values().length];

        java.util.Arrays.fill(licenceMultiple, 1);
        java.util.Arrays.fill(licenceTightness, 0);
        if (jobPosts != null && licensedHeads != null) {
            for (JobType job : JobType.values()) {
                if (!isGated(job)) continue;
                int j = job.ordinal();
                double open = j < jobPosts.length ? jobPosts[j] : 0;
                double held = j < licensedHeads.length ? licensedHeads[j] : 0;
                if (open <= 0) continue;
                licenceTightness[j] = open / Math.max(held, 1);
                licenceMultiple[j] = clamp(Math.pow(licenceTightness[j], ELASTICITY), 1, MAX_MULTIPLE);
            }
        }

        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();
            double open = bandPosts == null ? 0 : bandPosts[b];
            double able = bandSupply == null ? 0 : bandSupply[b];

            tightness[b] = open <= 0 ? 0 : open / Math.max(able, 1);

            /*
             * A band nobody employs has no market and drifts to the bottom of
             * its range rather than to zero: the wage is what the city WOULD
             * have to pay, and it pays nobody, so the figure is harmless either
             * way - but a zero on the People screen beside a job that simply
             * does not exist here reads as a bug.
             */
            multiple[b] = open <= 0
                    ? MIN_MULTIPLE
                    : clamp(Math.pow(tightness[b], ELASTICITY), MIN_MULTIPLE, MAX_MULTIPLE);
        }

        System.arraycopy(multiple, 0, bandMultiple, 0, multiple.length);

        for (JobType job : JobType.values()) {
            int i = job.ordinal();
            double combined = Math.min(MAX_MULTIPLE,
                    multiple[WageBand.of(job).ordinal()] * licenceMultiple[i]);
            double target = baseWage(job) * combined;

            wage[i] += (target - wage[i]) * ADJUST_RATE;

            // Nobody is paid under the minimum, whatever the band's own floor
            // works out to. For the unskilled band the two are the same number,
            // which is why an unskilled surplus cannot be priced away at all.
            wage[i] = Math.max(wage[i], minimumWage);
        }
    }

    /**
     * True when this job's wage has fallen as far as the market will let it and
     * there is still nowhere for those workers to go.
     *
     * The signal Migration turns into departures. See the class comment: a
     * binding floor converts a price adjustment into a quantity one.
     */
    public boolean isPinned(WageBand band) {
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) != band) continue;
            double floor = Math.max(baseWage(job) * MIN_MULTIPLE, minimumWage);
            // One job in the band is enough to read it: they all share a
            // multiplier, so they reach their floors together.
            return wage[job.ordinal()] <= floor * (1 + PINNED_TOLERANCE);
        }
        return false;
    }

    /** Puts every wage back on its base. New game, and the load fallback. */
    public final void resetToBase() {
        for (JobType job : JobType.values()) {
            wage[job.ordinal()] = baseWage(job);
        }
        java.util.Arrays.fill(tightness, 1);
    }

    private static double clamp(double v, double lo, double hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    /* ===================================================================
       READING AND SETTING
       =================================================================== */

    public double[] getWages()          { return wage; }
    public double getWage(JobType job)  { return wage[job.ordinal()]; }
    public double getTightness(WageBand band) { return tightness[band.ordinal()]; }
    /** Posts over licence holders for a gated job; 0 for an ungated one or one with no posts. */
    public double getLicenceTightness(JobType job) { return licenceTightness[job.ordinal()]; }
    /** The licence premium's target multiple this month, 1 when there is none. */
    public double getLicenceMultiple(JobType job)  { return licenceMultiple[job.ordinal()]; }
    /** The band's own multiple this month, without any licence premium on top. */
    public double getBandMultiple(WageBand band)   { return bandMultiple[band.ordinal()]; }

    /**
     * The premium the BAND is paying, read off an ungated job in it.
     *
     * Migration used to read "any job in the band" for this, and the first
     * university job in enum order is UNIV_DOCTOR - so once doctors carried a
     * premium of their own, the whole graduate band would have looked dear.
     */
    /**
     * What a licensed profession is paid OVER ITS OWN BAND, as actually paid.
     *
     * The band premium answers "are graduates dear here"; this answers "are
     * DOCTORS dear here", which is a different question with a different
     * answer - a city can be drowning in graduates and unable to staff a
     * hospital, and before this the two were one number.
     *
     * Realised rather than target, deliberately. getLicenceMultiple() is where
     * this month's scarcity is aiming; this is where the wage has actually got
     * to, after ADJUST_RATE. Migration reads THIS one, because a migration
     * model driven by an undamped signal oscillates for exactly the reason the
     * damping exists.
     *
     * Never below 1: a profession is not made cheap by its band being dear.
     */
    public double licencePremium(JobType job) {
        if (!isGated(job)) return 1;
        double band = bandPremium(WageBand.of(job));
        return band > 0 ? Math.max(1, premium(job) / band) : 1;
    }

    public double bandPremium(WageBand band) {
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) == band && !isGated(job)) return premium(job);
        }
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) == band) return premium(job);
        }
        return 1;
    }
    public double getMinimumWage()      { return minimumWage; }

    /** How far above its base a job is paying - 1.00 is the going rate. */
    public double premium(JobType job) {
        double base = baseWage(job);
        return base > 0 ? wage[job.ordinal()] / base : 1;
    }

    /**
     * Moves the dial. Every wage in the city re-anchors to the new floor.
     *
     * The existing wages are NOT rescaled here. They walk to their new targets
     * at ADJUST_RATE like everything else, so a minimum wage rise arrives over a
     * year rather than on the turn it is signed - which is both truer and the
     * only version a player can see happening.
     */
    public void setMinimumWage(double value) {
        minimumWage = clamp(value, MIN_SETTABLE, MAX_SETTABLE);
    }

    /* ===================================================================
       SAVE AND RESTORE

       A flow cannot be reconstructed from the state a month ended in, and
       neither can a damped one. The wage a city is paying is the result of
       every month of scarcity it has been through - recompute it from today's
       posts and workers and you get the TARGET, not the wage, and a reloaded
       city would jump to a number the live one was still walking toward.
       =================================================================== */

    public double[] state() {
        double[] out = new double[wage.length + 1];
        out[0] = minimumWage;
        System.arraycopy(wage, 0, out, 1, wage.length);
        return out;
    }

    /**
     * Refused whole on a length mismatch, never padded - the same rule the
     * health and healthcare arrays follow. A wage array from a build with a
     * different number of job types cannot be read positionally, and half a
     * wage table is worse than none: resetToBase() gives a coherent city
     * paying the going rate, which is exactly what every city before this
     * class was doing.
     */
    public void restore(double[] saved) {
        if (saved == null || saved.length != wage.length + 1) {
            resetToBase();
            return;
        }
        minimumWage = clamp(saved[0], MIN_SETTABLE, MAX_SETTABLE);
        System.arraycopy(saved, 1, wage, 0, wage.length);
    }
}
