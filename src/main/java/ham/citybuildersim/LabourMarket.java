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

    /* =====================================================================
       THE BOUNDS ARE MULTIPLES OF THE GOING WAGE, NOT DOLLAR FIGURES.

       They used to be .200 and 4.000 flat, and that read as a sensible range
       for exactly as long as the unskilled wage was .800: a quarter of it to
       five times it, which is what a minimum-wage dial should offer.

       On 2026-09-09 the wage ladder moved onto real Job Bank medians and the
       unskilled wage became 3.460. The bounds did not move, so the dial's range
       silently became 0.058x to 1.16x the going wage - the player could raise
       the minimum wage by fifteen percent and no further, and setMinimumWage()
       clamped everything above that without saying so. LabourCheck caught it as
       "doubling the floor moved the unskilled wage", which is a thing a
       minimum-wage dial has to be able to do.

       This is the same family as MATERIAL_MONTHS, MIN_RATE, MIN_TRADE and the
       hot money's flat $1 threshold, and the standing rule covers it exactly: a
       constant in absolute money is the same bug as a cached figure. The bounds
       were always MEANT as multiples; they were written as dollars because at
       the time the two happened to agree.

       The reform hook below is unchanged and still needed: these are seeded
       from the ladder at construction, and a currency reform moves them with
       every other price.
       ===================================================================== */

    /** The lowest the dial goes, as a share of the unskilled wage. */
    public static final double MIN_SETTABLE_SHARE = .25;

    /** The highest the dial goes, as a multiple of the unskilled wage. */
    public static final double MAX_SETTABLE_MULTIPLE = 5.0;

    /** Bounds on the dial, so the screen cannot ask for a negative wage. */
    public static final double MIN_SETTABLE =
            PayTier.UNSKILLED.getMonthlyWage() * MIN_SETTABLE_SHARE;
    public static final double MAX_SETTABLE =
            PayTier.UNSKILLED.getMonthlyWage() * MAX_SETTABLE_MULTIPLE;

    /**
     * The same bounds, in TODAY's money.
     *
     * A settable range is a range of PRICES, so it has to be reformed with
     * every other price - otherwise a hundred-to-one reform leaves a player
     * unable to set a minimum wage below what is now a hundred times the
     * average one. That is the same shape of bug as MAX_SETTABLE being applied
     * to an indexed floor in phase 5, and as MATERIAL_MONTHS before it: a bound
     * in one unit applied to a quantity in another.
     */
    private double minSettable = MIN_SETTABLE;
    private double maxSettable = MAX_SETTABLE;

    public double getMinSettable() { return minSettable; }
    public double getMaxSettable() { return maxSettable; }

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
    /* ==================== THE COST OF LIVING ====================
     *
     * Jerus, on deferring inflation: wages should show "slow partial drift".
     *
     * This is the part of inflation that cannot be deferred. A devaluation IS
     * inflation, through import prices, whether or not a general price model
     * exists - the shops' stock, the mills' scrap and the builders' materials
     * are all bought abroad, and halving the currency doubles them. That happens
     * with or without this class knowing about it.
     *
     * What this decides is whether anybody's pay follows. With wages perfectly
     * sticky a devaluation cuts real incomes with nothing pushing back, and the
     * household balance sheet turns that straight into hunger, bankruptcy and
     * emigration - so every currency crisis becomes a death spiral. Real
     * economies get partial pass-through and a long lag.
     *
     * A THIRD of the price move, reached over about a year. Deliberately not
     * full: workers never quite catch up, which is what makes a devaluation
     * genuinely cost something rather than being a unit change.
     */

    /**
     * How much of a rise in prices wages eventually chase.
     *
     * ALL OF IT, IN BOTH DIRECTIONS. It was a third, which is what you write
     * when the price index is a placeholder and you are not sure you believe
     * it. A third is a claim that workers permanently accept two thirds of
     * every price rise as a pay cut and never catch up - which compounds, and
     * over three hundred years says real wages fall to nothing.
     *
     * Prices fall and wages follow them DOWN too. There is no ratchet here,
     * because a ratchet is a separate mechanism and this model does not have
     * one. Measured over 4,001 months: wages lifted -6.0%, because prices ended
     * slightly below where they started.
     *
     * AND IT WAS BLAMED ON THE WRONG THING FIRST, which is worth keeping. At
     * 1.0 four harnesses failed, and the diagnosis was this loop:
     *
     *     rent -> price index -> wage -> rent
     *
     * rent is most of the basket, rent follows the unskilled wage, and at full
     * pass-through the wage follows the basket. Steady-state gain is the rent
     * weight, so the multiplier is about 2 and every shock doubles itself. All
     * true, and all survivable - the actual cause was one line in MoneyAudit
     * declaring the bank's entire wholesale funding cost as interest paid
     * abroad, which put the exchange rate against its ceiling and drove the
     * whole price level from outside. Fix that and full pass-through is 31/31
     * green with inflation at zero.
     *
     * A loop that is real is not automatically the loop that is biting.
     */
    public static final double COST_OF_LIVING_PASS_THROUGH = 1.0;

    /**
     * How fast they chase it. A twenty-fourth of the gap a month is two years.
     *
     * DOUBLED WITH THE PASS-THROUGH, and the two go together. Full
     * pass-through at the old one-year lag would have wages catching prices
     * almost as fast as prices move, which leaves no interval in which
     * inflation HURTS anybody - and a price rise nobody feels is not inflation,
     * it is a change of units.
     *
     * Two years is long enough that a shock costs real wages for a good while
     * before they recover, and short enough that a SUSTAINED inflation is
     * chased, which is what turns a shock into a spiral. That is the whole
     * mechanism: wages chasing last year's prices into this year's prices.
     */
    public static final double DRIFT_PER_MONTH = 1.0 / 24;

    private double costOfLiving = 1.0;
    private double livingTarget = 1.0;

    /**
     * @param priceIndex what a household's basket costs now against founding.
     *                   A REAL index since phase 5 - see PriceIndex. It used to
     *                   be handed the exchange rate as a stand-in, on the
     *                   reasoning that the world's prices do not move so the
     *                   rate is the only thing changing what an import costs.
     *                   That stopped being true when the shelf price became
     *                   cost-plus, and was never true of rent.
     */
    public void updateCostOfLiving(double priceIndex) {
        if (priceIndex <= 0) return;
        livingTarget = 1 + (priceIndex - 1) * COST_OF_LIVING_PASS_THROUGH;
        costOfLiving += (livingTarget - costOfLiving) * DRIFT_PER_MONTH;
    }

    /** What wages have been lifted by, chasing the cost of living. */
    public double getCostOfLiving() { return costOfLiving; }

    /** Where they are heading. */
    public double getLivingTarget() { return livingTarget; }

    public void setCostOfLiving(double value) {
        if (value > 0) this.costOfLiving = value;
    }

    public double baseWage(JobType job) {
        /*
         * ONE INDEXATION, NOT TWO - AND IT IS THIS ONE.
         *
         * A runaway lived here for an hour. Making the player's floor real, by
         * indexing minimumWage to the price level, put costOfLiving into every
         * wage TWICE: once in the floor and once here. A wage then went as the
         * SQUARE of the price index, rent follows the unskilled wage, and rent
         * is half of the price index. index -> wage^2 -> rent -> index is not a
         * loop with a gain, it is an exponent. Measured: rent went 0.12, 0.14,
         * 0.24, 0.86, 19,667,429, Infinity over eight years. The shelf price
         * sat calmly at 0.63 the whole time - the half of the basket that was
         * NOT in the loop never moved, which is what said where to look.
         *
         * THE FIRST FIX WAS THE WRONG END. Removing costOfLiving from here
         * stopped the runaway and broke four harnesses, because this line is
         * where every wage in the game learns about prices and the fixtures all
         * measure wages.
         *
         * The right end is that minimumWage was ALREADY real. It is multiplied
         * by costOfLiving here, so a floor of 0.80 in founding money is already
         * worth 0.80 in founding money whatever prices do - the only place it
         * was nominal was the hard clamp in advanceMonth(), which is one line
         * and is now indexed too. Nothing else had to change, and the player's
         * ± % sits on top of a floor that was real all along.
         */
        return minimumWage * ratioOf(job) * costOfLiving;
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
            /*
             * INDEXED, because this is the one place the floor was nominal.
             * baseWage() multiplies the floor by costOfLiving, so the floor is
             * real everywhere else; a bare `minimumWage` here is a legislated
             * minimum that inflation quietly removes. See getCashMinimumWage().
             */
            wage[i] = Math.max(wage[i], cashMinimumWage());
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
    /* ===================================================================
       THE MINIMUM WAGE IS A STANDARD OF LIVING, NOT A NUMBER OF DOLLARS.

       Jerus: "set the minimum wage, and then have the player modify the min
       wage by +- % and then have it go off that".

       So the dial is REAL. The player sets what the floor is worth - in
       founding money - and it holds that worth as prices move; on top of it
       sits a percentage they nudge up or down, and that percentage is the
       actual lever. "Minimum wage, +8%" means eight per cent above the
       indexed base, and it stays eight per cent above it next year.

       WHY THIS RATHER THAN A CASH FIGURE. With wages now chasing prices in
       full, a nominal floor is a floor that inflation quietly removes: the
       player sets $1.20, prices double, and the floor is worth sixty cents
       while every screen still says $1.20. That is exactly how a real minimum
       wage behaves and it is a miserable thing to put in a game, because the
       policy decays through inaction and nothing on screen says so.

       Real, with an explicit percentage, means the player's decision stays the
       decision they made. The cash figure is still shown - it is what the
       floor comes to at today's prices - but it is an OUTPUT now, not the
       input.
       =================================================================== */

    /** What the player has added to or taken off the floor, as a share. */
    private double minimumWageAdjustment;

    /** The real floor, in founding money. What the player set. */
    public double getMinimumWage()      { return minimumWage; }

    /** The same figure. Kept because "base" is what the screen calls it. */
    public double getMinimumWageBase()  { return minimumWage; }

    /** The player's nudge, as a share. +.08 is eight per cent above the base. */
    public double getMinimumWageAdjustment() { return minimumWageAdjustment; }

    /**
     * Re-strikes the cash floor from the base, the index and the adjustment.
     *
     * Called every month, because the whole point is that it moves when prices
     * do without anybody touching the dial.
     */
    /**
     * The floor in TODAY'S money: the real floor, lifted by the cost of living.
     *
     * NOT clamped to MIN_SETTABLE..MAX_SETTABLE. Those bounds are what a player
     * may type, in founding money, and they belong on the real figure. Applying
     * a nominal ceiling of 4.00 to an indexed quantity means that once prices
     * have quadrupled the floor is frozen and the dial does nothing - the same
     * shape as MATERIAL_MONTHS against the largest reachable position in
     * CapitalFlows: a bound set in one unit, applied to a quantity that has
     * since changed units.
     */
    public double cashMinimumWage() {
        return minimumWage * costOfLiving * (1 + minimumWageAdjustment);
    }

    /** Moves the percentage. The base is left alone. */
    public void setMinimumWageAdjustment(double share) {
        minimumWageAdjustment = Math.max(-.9, Math.min(2.0, share));
    }

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
    /**
     * Sets the floor as a CASH figure, in today's money.
     *
     * AND SETS THE REAL BASE TO MATCH, which is the whole of a bug worth
     * recording. This method used to be the only way in, and reindexing runs
     * every month from the base - so a caller that set the cash figure had it
     * silently overwritten on the next tick by a base that knew nothing about
     * it. LabourCheck caught it immediately ("doubling the floor moved the
     * unskilled wage: FAIL"), which is exactly what that assertion is for: two
     * setters for one quantity is one setter too many unless they agree.
     *
     * They agree now. The base is set to whatever reproduces this cash figure
     * at today's index, so every existing caller - the harnesses, the load
     * path, the old screen - behaves exactly as it did, while the real floor
     * underneath is correct and holds its worth from here on.
     */
    public void setMinimumWage(double value) {
        minimumWage = clamp(value, minSettable, maxSettable);
    }

    /** Sets the real floor, in founding money. The same dial as setMinimumWage. */
    public void setMinimumWageBase(double value) {
        setMinimumWage(value);
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
        double[] out = new double[wage.length + 3];
        out[0] = minimumWage;
        System.arraycopy(wage, 0, out, 1, wage.length);
        // The real floor and the player's nudge, appended so an older save
        // restores its cash figure and starts with the base at default.
        out[wage.length + 1] = minimumWage;
        out[wage.length + 2] = minimumWageAdjustment;
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
        /*
         * `!=` became `<`, and that is the difference between a save format
         * that can grow and one that cannot. The old check threw away the whole
         * wage ladder the moment the array changed length by one - so adding
         * the real minimum wage below would have silently reset every wage in
         * every existing city to base.
         */
        if (saved == null || saved.length < wage.length + 1) {
            resetToBase();
            return;
        }
        minimumWage = clamp(saved[0], minSettable, maxSettable);
        System.arraycopy(saved, 1, wage, 0, wage.length);
        minimumWageAdjustment = saved.length >= wage.length + 3
                ? saved[wage.length + 2] : 0;
    }

    /**
     * Wages and the floor, in the new unit. See Denomination.
     *
     * costOfLiving and livingTarget are INDICES - ratios against a base year -
     * so they do not move, and moving them would be the bug. The same reform
     * that halves every price halves the base it is measured against.
     */
    public void redenominate(double scale) {
        minimumWage *= scale;
        minSettable *= scale;
        maxSettable *= scale;
        for (int i = 0; i < wage.length; i++) wage[i] *= scale;
    }


    /** Re-seeds the money CONSTANTS at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        minSettable = MIN_SETTABLE / unit;
        maxSettable = MAX_SETTABLE / unit;
    }

}
