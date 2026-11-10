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
 *     base   = goingRate x ratio[type] x costOfLiving
 *     tight  = posts / qualified workers
 *     target = base x clamp(tight ^ ELASTICITY, MIN_MULTIPLE, MAX_MULTIPLE)
 *     wage  += (target - wage) x ADJUST_RATE
 *     wage   = max(wage, cashMinimumWage())     // the floor in today's money, which isPinned() reads too (0.7.47)
 *
 * Four things in there are load-bearing and none of them is the exponent.
 *
 * THE GOING RATE IS THE BASE (WG1, 2026-11-10). It was "THE MINIMUM WAGE IS THE
 * BASE" from the day this class was written - Jerus: "make the base a modifiable
 * min wage" - and the whole ladder was a multiple of the floor, so raising the
 * floor lifted every wage in the city and 94% of workers were paid exactly the
 * floor (spec-wages 2.2). Jerus, 2026-11-02: "yes a real minimum wage". The
 * ladder is now anchored on the GOING RATE: what an unskilled post pays where
 * there are exactly enough workers, ladder.anchor(), in founding money and
 * lifted by the cost of living as every base is. The multiples are still READ
 * OFF PayTier rather than invented (see ratioOf), which still gives the
 * property that matters: at the going rate this market pays exactly what the
 * game paid before it existed, so every balance measurement already taken
 * survives. The floor is not the anchor; it starts at DEFAULT_FLOOR_SHARE of it.
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
 * needs fewer workers, and it spirals. The minimum wage stops that. Until WG1
 * (2026-11-10) the unskilled base WAS the minimum wage, so an unskilled surplus
 * could not be priced away at all; the floor is now a real minimum, a figure of
 * its own that only lifts the wages UNDER it (wage = max(wage, cash floor)), and
 * the unskilled wage can fall to the floor and no further. A floor over what the
 * market would pay still turns a price adjustment into a QUANTITY one: the
 * surplus cannot be priced away, so it is unemployed or it leaves, which is
 * exactly what a binding minimum wage does in the real world and exactly what
 * Migration's surplus departures model. The player sets the number and lives
 * with which of the two costs they would rather pay.
 *
 * THE FLOOR'S TIE (WG1). The floor is set in founding money and lifted by the
 * cost of living (PRICES, the default and the only rule before WG1); or struck
 * each month as a share of the average wage (AVERAGE_WAGE, the OECD's way of
 * reading a minimum, spec-wages 3.2); or fixed as a cash figure that inflation
 * wears down (NOTHING). The tie is saved with the market.
 */
public class LabourMarket {

    /** Where the player's change to the wage floor is written (DecisionLog, 0.7.23); null for one no city holds. Never saved: the city wires it. */
    private transient DecisionLog decisions;

    /** Wires this to its city's decision log (Game.buildWorld()). */
    public void recordTo(DecisionLog log) { decisions = log; }

    private void decided(String kind, String label) {
        if (decisions != null) decisions.record(kind, label);
    }

    /* ===================================================================
       THE DIAL
       =================================================================== */

    /**
     * The going rate on today's ladder, in founding money, for a reader with no city: what an unskilled post pays where there are exactly enough
     * workers, and what the ladder is anchored on (a city's market reads getGoingRate(), its own ladder's anchor()).
     */
    public static final double GOING_RATE = PayTier.UNSKILLED.getMonthlyWage();

    /**
     * Where a new city's floor starts, as a share of the going rate: Nova Scotia's legal minimum, $17.00 an hour since 1 October 2026, over the
     * material-handling labourer's Job Bank median, $20.00, which the unskilled rung is (spec-wages C1, Jerus 2026-11-02: "0.85, Nova Scotia's").
     */
    public static final double DEFAULT_FLOOR_SHARE = .85;

    /**
     * Where the minimum wage starts on today's ladder. It is no longer what the ladder is anchored to (WG1, 2026-11-10: the going rate is), so a
     * new city's payroll is still penny-identical to what it was before this class existed, and its floor sits under the unskilled wage.
     */
    public static final double DEFAULT_MINIMUM_WAGE = GOING_RATE * DEFAULT_FLOOR_SHARE;

    /** How far above the floor a wage counts as near it: the reach the spillover evidence gives the next few dollars, an estimate (spec-wages 3.6). */
    public static final double NEAR_FLOOR = .10;

    /** The floor's share of the average wage when it is tied to it: the middle of the OECD's 37-55% (28 of the 33 nations in the world file), an estimate (spec-wages 3.2). */
    public static final double DEFAULT_TIE_SHARE = .45;

    /** The lowest share of the average wage a tie to it can ask: under the world file's lowest minimum-to-average ratio (.17, the United States), so even that nation can be copied. */
    public static final double MIN_TIE_SHARE = .15;

    /**
     * The highest share of the average wage a tie to it can ask: over the world file's highest minimum-to-average ratio (.69, Costa Rica), so every nation can be copied and none
     * exceeded, and under 1, where the floor is last month's average itself and lifts every wage under it each month - a floor struck at share s of an average that includes it
     * settles at most s of the mean of the wages over it.
     */
    public static final double MAX_TIE_SHARE = .70;

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

    /**
     * The going rate in the market's current unit (founding money): the ladder's anchor, what every base is a multiple of. Kept exactly as
     * minSettable is - the ladder's at setLadder(), re-seeded at a unit by seedConstants(), carried across a reform by redenominate() - and
     * never saved: the city says its ladder, and the unit is the city's.
     */
    private double goingRate = GOING_RATE;

    /** The going rate, in founding money at the market's current unit: what an unskilled post pays where there are exactly enough workers. */
    public double getGoingRate() { return goingRate; }

    /** The going rate in TODAY's money: the founding figure lifted by the cost of living, as every base is. */
    public double cashGoingRate() { return goingRate * costOfLiving; }

    public double getMinSettable() { return minSettable; }
    public double getMaxSettable() { return maxSettable; }

    /**
     * The wage ladder this market prices on (R0d): today's, PayTier's, in every city but one founded the 1900 way, whose market pays 1901's (WageLadder).
     * Never saved - the city says, from its saved founding (Game.layTheLadder()) - and set before the saved wages and floor are read back, so
     * the dial's bounds are the ladder's when the floor is clamped to them.
     */
    private transient WageLadder ladder = WageLadder.TODAY;

    /** The ladder this market prices on (R0d). */
    public WageLadder getLadder() { return ladder; }

    /**
     * Puts this market on a ladder (R0d): the ladder and the dial's bounds, which are multiples of its anchor (today's are exactly MIN_SETTABLE and
     * MAX_SETTABLE). Never the minimum wage or the wages, so the load path can call it over restored state; a new city's founding is foundOn().
     */
    public void setLadder(WageLadder l) {
        ladder = l == null ? WageLadder.TODAY : l;
        minSettable = lowestSettable();
        maxSettable = highestSettable();
        goingRate = ladder.anchor();
    }

    /** The dial's bounds on this market's ladder at the founding unit: the constants on today's, multiples of the 1901 anchor on 1901's. */
    private double lowestSettable()  { return ladder.isHistorical() ? ladder.anchor() * MIN_SETTABLE_SHARE : MIN_SETTABLE; }
    private double highestSettable() { return ladder.isHistorical() ? ladder.anchor() * MAX_SETTABLE_MULTIPLE : MAX_SETTABLE; }

    /**
     * A city founded on a ladder (R0d): its bounds, its minimum wage at DEFAULT_FLOOR_SHARE of the ladder's anchor (WG1; the anchor itself until
     * then) - the field, not setMinimumWage(), which would log a decision nobody made - and every wage on its base. Founding only; a load
     * restores the floor and the wages and calls setLadder().
     */
    public void foundOn(WageLadder l) {
        setLadder(l);
        minimumWage = ladder.anchor() * DEFAULT_FLOOR_SHARE;
        resetToBase();
    }

    private double minimumWage = DEFAULT_MINIMUM_WAGE;

    /**
     * Whether the minimum wage is law yet (R0): false in a city before the date Canada first had one (PolicyDates, MINIMUM_WAGE, 1918),
     * and then the floor binds no wage - the max() in advanceMonth() and isPinned()'s floor are skipped. Only that. The floor is not the
     * ladder's anchor (WG1, 2026-11-10: baseWage() is goingRate x ratio x costOfLiving; until then the dial WAS the anchor, so it could not
     * be zeroed), so the base wages, the curve and everything else stand: before 1918 the wages find their own levels above and below the
     * going rate. Game pushes it with the policy's month (the lever's opening month is the city's, and a law find may move it); true for a
     * labour market no city holds, which behaves as it always did. Never saved: the city says.
     */
    private boolean floorBinding = true;

    /** Whether the floor binds (see floorBinding). */
    public boolean isFloorBinding() { return floorBinding; }

    /** Game's push, with the policy's month: whether the minimum wage is law in this city yet. */
    public void setFloorBinding(boolean binding) { floorBinding = binding; }

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
     * THE CURVE ITSELF, in one place (0.7.18): the multiple a band's wage
     * aims at when its posts stand at this tightness against its supply.
     * advanceMonth() prices every band on it, and the allocator reads it
     * (PopulationManager.fillByBand()) to know which posts pay best - two
     * copies of this line would be the same curve until somebody tuned one.
     */
    public static double multipleAt(double tightness) {
        return clamp(Math.pow(tightness, ELASTICITY), MIN_MULTIPLE, MAX_MULTIPLE);
    }

    /**
     * ...and the curve read backwards: the tightness at which a band's wage
     * aims at this multiple of its base, clamps aside. A band tied with
     * another at one wage holds the workers that put it there
     * (PopulationManager.fillByBand()).
     */
    public static double tightnessAt(double multiple) {
        return Math.pow(multiple, 1 / ELASTICITY);
    }

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
     * neutrality property: goingRate x ratio reproduces PayTier exactly (WG1,
     * 2026-11-10; until then it was minimumWage x ratio at the dial's default).
     *
     * TODAY'S LADDER (R0d): for a reader with no city. A city's market reads
     * ratio(job), which is its own ladder's: this one, the expression
     * unchanged, unless the city was founded the 1900 way.
     */
    public static double ratioOf(JobType job) {
        return WageLadder.TODAY.ratio(job);
    }

    /** What this job pays relative to the unskilled floor on this market's ladder (R0d): ratioOf() on today's, the census's ratios on 1901's. */
    public double ratio(JobType job) {
        return ladder.ratio(job);
    }

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
     * ALL of the price move now, reached over about two years - see
     * COST_OF_LIVING_PASS_THROUGH and DRIFT_PER_MONTH below, and since 0.7.42
     * half of it from what people expect once the basket is based (HALF WHAT
     * PEOPLE EXPECT, HALF THE CHASE, further down). It was a third,
     * reached over about a year and deliberately not full, so that a
     * devaluation cost something rather than being a unit change; the two
     * dials say why that compounded, and why it is the lag rather than a
     * share withheld for ever that makes a price rise hurt. LabourCheck's
     * wagesAgainstTheIndex() holds the recurrence to the index month by
     * month (2026-09-21).
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
     *
     * The whole speed until the basket is based; after that the chase takes
     * (1 - EXPECTED_SHARE) of it - a forty-eighth - and the rest of a wage's
     * indexing is what people expect (0.7.42, HALF WHAT PEOPLE EXPECT, HALF
     * THE CHASE). The lag in steady inflation is still two years.
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
        double cash = cashMinimumWage();
        livingTarget = 1 + (priceIndex - 1) * COST_OF_LIVING_PASS_THROUGH;
        costOfLiving += (livingTarget - costOfLiving) * DRIFT_PER_MONTH;
        holdTheCashFloor(cash);
    }

    /** A floor tied to nothing (WG1) is a cash figure: after the cost of living moves, the real floor is whatever keeps `cash` its cash. */
    private void holdTheCashFloor(double cash) {
        if (floorTie == FloorTie.NOTHING) minimumWage = clamp(floorForCash(cash), minSettable, maxSettable);
    }

    /* ----------------- HALF WHAT PEOPLE EXPECT, HALF THE CHASE (0.7.42) -----------------

       Once the basket is based a wage is indexed two ways at once, half each:
       to what people EXPECT prices to do this month (Expectations), and to
       the chase above, a forty-eighth of the gap a month instead of a
       twenty-fourth. The recurrence, with omega = EXPECTED_SHARE:

           C <- C x (1 + expected a month)^omega x (target / C)^((1 - omega) / 24)

       NEVER BOTH IN FULL. In steady inflation, expected equal to actual, the
       wage grows omega x inflation from the first factor and (1 - omega) x
       inflation from the second (the gap it holds is two years of inflation,
       as before) - inflation in all, so wages lag the index by the same two
       years and pass it through one for one. A one-off level step with
       expectations unmoved passes a forty-eighth a month, not a twenty-fourth,
       and still all of it in the end. A wage overshoots the index only when
       people expect more than happens - in a disinflation, where it is the
       cost of disinflating. Measured in the prototype under the autopilot:
       no overshoot, 2.13% a year (spec-inflation.md, section 2.2).

       The tightness curve is untouched: it is the level Phillips curve, which
       becomes persistent inflation only once expectations come loose (star 1).
       ---------------------------------------------------------------------- */

    /** The share of a wage's monthly indexing taken from expected inflation; the rest chases the index at the old speed's share. One half: never both in full. */
    public static final double EXPECTED_SHARE = .5;

    /**
     * The cost of living, once the basket is based: half from what people
     * expect, half the chase (see HALF WHAT PEOPLE EXPECT, HALF THE CHASE).
     * Game calls the one-argument form before the basket is based.
     *
     * @param priceIndex      what a household's basket costs now against founding
     * @param expectedMonthly expected inflation as a month's growth (Expectations.monthlyExpected())
     */
    public void updateCostOfLiving(double priceIndex, double expectedMonthly) {
        if (priceIndex <= 0 || !(costOfLiving > 0)) return;
        double cash = cashMinimumWage();
        livingTarget = 1 + (priceIndex - 1) * COST_OF_LIVING_PASS_THROUGH;
        costOfLiving *= Math.pow(1 + expectedMonthly, EXPECTED_SHARE)
                * Math.pow(livingTarget / costOfLiving, (1 - EXPECTED_SHARE) * DRIFT_PER_MONTH);
        holdTheCashFloor(cash);
    }

    /** What wages have been lifted by, chasing the cost of living. */
    public double getCostOfLiving() { return costOfLiving; }

    /** Where they are heading. */
    public double getLivingTarget() { return livingTarget; }

    public void setCostOfLiving(double value) {
        if (value > 0) this.costOfLiving = value;
    }

    /** What this job would pay in a city with exactly enough people for it. */
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
         * The right end is that the base was ALREADY real. It is multiplied
         * by costOfLiving here, so a figure of 0.80 in founding money is already
         * worth 0.80 in founding money whatever prices do - the only place it
         * was nominal was the hard clamp in advanceMonth(), which is one line
         * and is now indexed too. Nothing else had to change, and the player's
         * ± % sits on top of a floor that was real all along.
         *
         * (WG1, 2026-11-10: the figure multiplied here is the going rate, not
         * the floor; the floor's own indexation is cashMinimumWage(), once.)
         */
        return goingRate * ratio(job) * costOfLiving;
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
                             long[] jobPosts, double[] licensedHeads) {

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
                    : multipleAt(tightness[b]);
        }

        System.arraycopy(multiple, 0, bandMultiple, 0, multiple.length);

        for (JobType job : JobType.values()) {
            int i = job.ordinal();
            double combined = Math.min(MAX_MULTIPLE,
                    multiple[WageBand.of(job).ordinal()] * licenceMultiple[i]);
            double target = baseWage(job) * combined;

            wage[i] += (target - wage[i]) * ADJUST_RATE;

            // Nobody is paid under the minimum, whatever the band's own floor
            // works out to. The unskilled band's own floor is .70 of the going
            // rate, so a floor over that (the default is .85 of it) is where an
            // unskilled surplus cannot be priced away any further (WG1; until
            // then the floor was the unskilled base itself).
            /*
             * INDEXED, because this is the one place the floor was nominal.
             * baseWage() multiplies the floor by costOfLiving, so the floor is
             * real everywhere else; a bare `minimumWage` here is a legislated
             * minimum that inflation quietly removes. See cashMinimumWage().
             */
            // ...from the date the law exists (R0): before it nobody is bound to pay the floor.
            if (floorBinding) wage[i] = Math.max(wage[i], cashMinimumWage());
        }
    }

    /**
     * True when this job's wage has fallen as far as the market will let it and
     * there is still nowhere for those workers to go.
     *
     * The signal Migration turns into departures. See the class comment: a
     * binding floor converts a price adjustment into a quantity one.
     *
     * AGAINST THE FLOOR IN TODAY'S MONEY (B8, 0.7.47), the one advanceMonth()
     * holds every wage at: cashMinimumWage(). Until then it read the founding
     * figure, so in a city whose cost of living had risen a band held at the
     * indexed floor - city600's Diploma at 3.6934 against a cash floor of
     * 3.6882, with 803 people spare - read as one that could still get cheaper.
     */
    public boolean isPinned(WageBand band) {
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) != band) continue;
            // The same floor advanceMonth() holds the wage at: without the law (R0) that is the band's own, and nothing else.
            double floor = floorBinding ? Math.max(baseWage(job) * MIN_MULTIPLE, cashMinimumWage()) : baseWage(job) * MIN_MULTIPLE;
            // One job in the band is enough to read it: they all share a
            // multiplier, so they reach their floors together.
            return wage[job.ordinal()] <= floor * (1 + PINNED_TOLERANCE);
        }
        return false;
    }

    /**
     * Whether the floor is what this job is paid (WG1): the law exists and the wage is within PINNED_TOLERANCE of the cash floor, at or under
     * it times (1 + PINNED_TOLERANCE). The floor was every unskilled wage's base until WG1, so the Wages page had no one to count.
     */
    public boolean isPaidTheFloor(JobType job) {
        return floorBinding && wage[job.ordinal()] <= cashMinimumWage() * (1 + PINNED_TOLERANCE);
    }

    /** Whether any job of the band is paid the floor (see isPaidTheFloor(JobType)). */
    public boolean isPaidTheFloor(WageBand band) {
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) == band && isPaidTheFloor(job)) return true;
        }
        return false;
    }

    /**
     * Whether this job is paid a little over the floor (WG1): the law exists, the job is not paid the floor, and its wage is at or under the
     * cash floor times (1 + NEAR_FLOOR) - the wages a rise in the floor is likely to reach next (spec-wages 3.6).
     */
    public boolean isNearTheFloor(JobType job) {
        return floorBinding && !isPaidTheFloor(job) && wage[job.ordinal()] <= cashMinimumWage() * (1 + NEAR_FLOOR);
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

    /**
     * The premium the BAND is paying, read off an ungated job in it.
     *
     * Migration used to read "any job in the band" for this, and the first
     * university job in enum order is UNIV_DOCTOR - so once doctors carried a
     * premium of their own, the whole graduate band would have looked dear.
     */
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
     * Moves the dial. The wages under the new floor are lifted to it (WG1,
     * 2026-11-10; until then every wage in the city re-anchored to the new floor,
     * because the floor was the ladder's anchor): it is only a floor now, and
     * the going rate every base is a multiple of does not move.
     *
     * The existing wages are NOT rescaled here. A wage over the floor walks to
     * its target at ADJUST_RATE like everything else, and one under it is held
     * at it by the next month's max(), so the floor binds from the next month.
     * Under a tie to the average wage the share moves to match, so the one dial
     * works under every tie.
     *
     * TAKES THE REAL FLOOR, IN FOUNDING MONEY, and stores it as given, held to
     * the settable bounds: the same figure getMinimumWage() reads, and what
     * cashMinimumWage() lifts by the cost of living and the player's nudge
     * into today's money. (This comment said until 0.7.36 that it took a cash
     * figure and set the real base to match - true of an older version, when
     * reindexing from a base overwrote a cash figure on the next tick and
     * LabourCheck caught it as "doubling the floor moved the unskilled wage".
     * The code has stored the founding figure since; the Policy tab's floor
     * dial moves in it and reads in today's money through cashAt().)
     */
    public void setMinimumWage(double value) {
        double was = minimumWage;
        minimumWage = clamp(value, minSettable, maxSettable);
        if (floorTie == FloorTie.AVERAGE_WAGE && Double.isFinite(lastAverageWage) && lastAverageWage > 0) {
            tieShare = clamp(cashAt(minimumWage) / lastAverageWage, MIN_TIE_SHARE, MAX_TIE_SHARE);
        }
        if (DecisionLog.moved(was, minimumWage)) {
            decided(DecisionLog.PROMISE, "Wage floor to " + DecisionLog.money(minimumWage) + " a month");
        }
    }

    /* ------------------- WHAT THE POLICY TAB ASKS (0.7.36) -------------------
     * The floor dial's reading and its preview, by this class's own rules:
     * the dial is set in founding money and read in today's (the Policy
     * spec's D6, B7 - the tab printed the founding figure as a wage).
     */

    /** What a real floor of `floor`, in founding money, comes to in today's money: cashMinimumWage()'s rule at another setting. Pure. */
    public double cashAt(double floor) {
        return floor * costOfLiving * (1 + minimumWageAdjustment);
    }

    /** ...and back: the founding figure whose cash floor is `cash` today - what the Policy tab's dial, read in today's money, sets. */
    public double floorForCash(double cash) {
        double per = costOfLiving * (1 + minimumWageAdjustment);
        return per > 0 ? cash / per : cash;
    }

    /**
     * Where a job's wage heads with the real floor at `floor`: advanceMonth()'s
     * target - its base times this month's band and licence multiples, held to
     * MAX_MULTIPLE - and never under the floor in today's money at that setting
     * (once the law exists: advanceMonth() holds the wage by the same test).
     * What the wage walks to "once wages have walked there", at ADJUST_RATE a
     * month; at today's floor it is today's target, which the wage itself may
     * not have reached. The base is the going rate's, whatever the floor (WG1,
     * 2026-11-10; the floor was the base until then): the floor is only in the
     * max(). Pure.
     */
    public double targetWageAt(JobType job, double floor) {
        double base = goingRate * ratio(job) * costOfLiving;
        double combined = Math.min(MAX_MULTIPLE,
                bandMultiple[WageBand.of(job).ordinal()] * licenceMultiple[job.ordinal()]);
        double target = base * combined;
        return floorBinding ? Math.max(target, cashAt(floor)) : target;
    }

    /** The best-paid job somebody in this band can hold (0.7.36; the Policy tab's own until then): what a course's fee is weighed against. */
    public double bestWageIn(WageBand band) {
        double best = 0;
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) == band) best = Math.max(best, wage[job.ordinal()]);
        }
        return best;
    }

    /** Sets the real floor, in founding money. The same dial as setMinimumWage. */
    public void setMinimumWageBase(double value) {
        setMinimumWage(value);
    }

    /* ===================================================================
       THE FLOOR'S TIE (WG1, 2026-11-10; spec-wages 3.2)

       What the floor keeps pace with, when nothing is moving it by hand:
         PRICES        the real floor holds as prices move - cashMinimumWage()
                       lifts it by the cost of living. The rule before WG1.
         AVERAGE_WAGE  the real floor is struck each month as a share of last
                       month's average wage (followAverageWage()).
         NOTHING       a cash figure: the real floor falls as prices rise and
                       rises as they fall, so inflation wears it down.
       =================================================================== */

    /** What the floor keeps pace with. */
    public enum FloorTie {
        PRICES("prices"), AVERAGE_WAGE("the average wage"), NOTHING("nothing");

        private final String label;
        FloorTie(String label) { this.label = label; }

        /** The tie in words, as the Policy tab and the decision log name it. */
        public String label() { return label; }
    }

    private FloorTie floorTie = FloorTie.PRICES;

    /** The floor's share of the average wage when it is tied to it (AVERAGE_WAGE); kept, unused, under another tie. */
    private double tieShare = DEFAULT_TIE_SHARE;

    /**
     * The average wage the tie last read, in today's money (0 before any month has said one, and for a nobody-employed month). Game says it every month before the
     * wages are walked; it is saved (WS1) so that a dial move before the first month after a load still reads the share against it, and scaled by a reform like the wages.
     */
    private double lastAverageWage;

    /** What the floor keeps pace with. */
    public FloorTie getFloorTie() { return floorTie; }

    /** The floor's share of the average wage when it is tied to it. */
    public double getTieShare() { return tieShare; }

    /**
     * Ties the floor (WG1): the tie, and its share of the average wage (held to MIN_TIE_SHARE..MAX_TIE_SHARE; the share means something under
     * AVERAGE_WAGE only). A null tie reads PRICES. It does not move the floor itself this month - the tie strikes it as the month turns
     * (followAverageWage(), updateCostOfLiving()). Logs one decision when either moved.
     */
    public void setFloorTie(FloorTie tie, double share) {
        FloorTie t = tie == null ? FloorTie.PRICES : tie;
        double s = clamp(share, MIN_TIE_SHARE, MAX_TIE_SHARE);
        boolean moved = t != floorTie || DecisionLog.moved(tieShare, s);
        floorTie = t;
        tieShare = s;
        if (!moved) return;
        switch (t) {
            case AVERAGE_WAGE -> decided(DecisionLog.PROMISE, "Wage floor tied to the average wage at " + DecisionLog.pct(tieShare));
            case NOTHING -> decided(DecisionLog.PROMISE, "Wage floor tied to nothing: a cash figure");
            default -> decided(DecisionLog.PROMISE, "Wage floor tied to prices");
        }
    }

    /**
     * Game's monthly word on the average wage (WG1), the wage bill over the posts filled, before the wages are walked. Stored for
     * setMinimumWage(); and under AVERAGE_WAGE, when the average is finite and above nothing, the real floor is struck at tieShare of it,
     * held to the settable bounds (a floor the tie would take outside them is held at the bound).
     */
    public void followAverageWage(double averageWage) {
        lastAverageWage = averageWage;
        if (floorTie == FloorTie.AVERAGE_WAGE && Double.isFinite(averageWage) && averageWage > 0) {
            minimumWage = clamp(floorForCash(tieShare * averageWage), minSettable, maxSettable);
        }
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
        int bands = WageBand.values().length;
        double[] out = new double[wage.length + 3 + diagnosticsLength() + 3];
        out[0] = minimumWage;
        System.arraycopy(wage, 0, out, 1, wage.length);
        // The real floor and the player's nudge, appended so an older save
        // restores its cash figure and starts with the base at default.
        out[wage.length + 1] = minimumWage;
        out[wage.length + 2] = minimumWageAdjustment;
        /*
         * ...AND THE DIAGNOSTICS THE PEOPLE SCREEN PRINTS. tightness,
         * bandMultiple, licenceMultiple and livingTarget are all struck in
         * advanceMonth(), which the load path deliberately does not run, and
         * until 2026-09-10 none of them was carried either - so for one month
         * after every load the tightness column read a flat 1.00 down the
         * table, the multiples 0, and the cost-of-living target 1.0. That is
         * the column that says which bands are short. The wages themselves
         * were always fine; it was the explanation beside them that reset.
         */
        int i = wage.length + 3;
        System.arraycopy(tightness, 0, out, i, bands);           i += bands;
        System.arraycopy(bandMultiple, 0, out, i, bands);        i += bands;
        System.arraycopy(licenceMultiple, 0, out, i, wage.length); i += wage.length;
        out[i] = livingTarget;
        // The floor's tie and its share (WG1), appended after the diagnostics: an older array restores PRICES at the default share.
        out[i + 1] = floorTie.ordinal();
        out[i + 2] = tieShare;
        // The average wage the tie last read (WS1), appended after the tie and its share: an older array restores 0, as before. Never NaN in a save.
        out[i + 3] = Double.isFinite(lastAverageWage) ? lastAverageWage : 0;
        return out;
    }

    private int diagnosticsLength() {
        return WageBand.values().length * 2 + wage.length + 1;
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
        // The diagnostics, when the save carries them; the defaults otherwise,
        // which is what an older save was already reading.
        if (saved.length >= wage.length + 3 + diagnosticsLength()) {
            int bands = WageBand.values().length;
            int i = wage.length + 3;
            System.arraycopy(saved, i, tightness, 0, bands);           i += bands;
            System.arraycopy(saved, i, bandMultiple, 0, bands);        i += bands;
            System.arraycopy(saved, i, licenceMultiple, 0, wage.length); i += wage.length;
            if (saved[i] > 0) livingTarget = saved[i];
        }
        // The floor's tie and its share (WG1), when the save carries them; PRICES at the default share otherwise.
        if (saved.length >= wage.length + 3 + diagnosticsLength() + 2) {
            int at = wage.length + 3 + diagnosticsLength();
            FloorTie[] ties = FloorTie.values();
            int ordinal = (int) Math.round(saved[at]);
            floorTie = ordinal >= 0 && ordinal < ties.length ? ties[ordinal] : FloorTie.PRICES;
            tieShare = Double.isFinite(saved[at + 1]) ? clamp(saved[at + 1], MIN_TIE_SHARE, MAX_TIE_SHARE) : DEFAULT_TIE_SHARE;
        } else {
            floorTie = FloorTie.PRICES;
            tieShare = DEFAULT_TIE_SHARE;
        }
        // The average wage the tie last read (WS1), when the save carries it; 0 otherwise, which is what an older save was already reading.
        int averageAt = wage.length + 3 + diagnosticsLength() + 2;
        lastAverageWage = saved.length > averageAt && Double.isFinite(saved[averageAt]) && saved[averageAt] > 0 ? saved[averageAt] : 0;
    }

    /**
     * Wages and the floor, in the new unit. See Denomination.
     *
     * costOfLiving and livingTarget are INDICES - ratios against a base year -
     * so they do not move, and moving them would be the bug. The same reform
     * that halves every price halves the base it is measured against. The
     * average wage the tie last read is cash in today's money, like the wages,
     * so it moves with them (WS1): setMinimumWage() in a reform month divides a
     * new-unit floor by it.
     */
    public void redenominate(double scale) {
        lastAverageWage *= scale;
        minimumWage *= scale;
        goingRate *= scale;
        minSettable *= scale;
        maxSettable *= scale;
        for (int i = 0; i < wage.length; i++) wage[i] *= scale;
    }


    /** Re-seeds the money CONSTANTS at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        minSettable = lowestSettable() / unit;
        maxSettable = highestSettable() / unit;
        goingRate = ladder.anchor() / unit;
    }

}
