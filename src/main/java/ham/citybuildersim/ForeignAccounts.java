package ham.citybuildersim;

/**
 * The city's dealings with the rest of the world: the balance of payments, the
 * reserve position, and the exchange rate.
 *
 * ==================== WHY THIS IS NEARLY EMPTY ====================
 *
 * Almost nothing here is calculated. The balance of payments is read straight
 * off MoneyAudit, which has been computing it since the day it was written
 * without anybody noticing - it tracks every flow crossing the city's edge and
 * reconciles them to the cent every month.
 *
 * What was missing was not the arithmetic but the BOUNDARY. "Outside the pools"
 * bundled households, who are domestic and simply not modelled as a pool,
 * together with the rest of the world. Tag those apart - see MoneyAudit.Scope -
 * and the foreign subset of a list that already balances IS the balance of
 * payments. This class is the running total of it.
 *
 * That is the same move the bank rewrite kept making: find the identity that is
 * already there rather than inventing a parallel mechanism that can drift from
 * it. There is no second set of books here to disagree with the first.
 *
 * ==================== PHASE ONE OF FOUR ====================
 *
 * Jerus's plan, in order:
 *
 *   1. THIS. The accounting only. Split the boundary, count the reserve, show
 *      it on a screen. The exchange rate exists and is pinned at 1.00, and
 *      NOTHING BEHAVES DIFFERENTLY - the point is to prove the split is honest
 *      against MoneyAudit before anything depends on it.
 *   2. The rate moves, on reserve adequacy. Import prices and export revenues
 *      start converting through it, and wages get a cost-of-living drift.
 *   3. Foreign debt: USD instruments beside the existing ones. This is what
 *      closes the circular-capital hole, because the treasury finally has a
 *      lender that is not its own bank.
 *   4. Capital flows: the carry trade in, sudden stops out, straight into the
 *      bank's deposits.
 *
 * ALL FOUR ARE BUILT, and the list is kept as the plan it was. The rate
 * floats (repriceCurrency(), THE FLOAT), held as the city chooses since XR2
 * (THE REGIMES: Float, Lean, Band, Peg); the treasury borrows in
 * dollars (WHAT THE CITY OWES ABROAD, and DebtManager's foreign paper); the
 * hot money and the carry trade are CapitalFlows.
 *
 * See claude/foreign-exchange-design.md.
 *
 * ==================== WHAT A RESERVE IS, TODAY ====================
 *
 * TWO NUMBERS, which were one number until the split (TWO NUMBERS THAT WERE
 * ONE NUMBER, below).
 *
 * THE VAULT is a holding: the US dollars the treasury chose to buy and has
 * not yet sold. The treasury buying and selling moves it (buyReserves(),
 * sellReserves()), since 0.7.2 the central bank selling it to defend the
 * currency (A DEFENCE THAT SPENDS, at repriceCurrency()), and since 0.7.6
 * the land office paid out of it when the player says so
 * (spendReservesOnLand()); it cannot go below zero, and it is KEPT IN DOLLARS -
 * the US$ value of its eight balances (getReservesUsd(), THE VAULT IN EIGHT) - so everything local about it, what it is worth, what can be
 * sold, the import cover and the net position, is those dollars at today's
 * rate. When the currency moves the dollars stay put, and the move in their
 * local value is booked beside the vault as a revaluation (revalueVault()),
 * in the dollar debt's shape: not cash, and not an audit flow. A new city
 * opens with the founders' dollars in it - US$25M on the default founding
 * since 0.7.10, US$1B before (Game's THE FOUNDING RESERVE) - and it is
 * spent only when the D$ would fall (A RESERVE DEFENDS A CURRENCY, at
 * repriceCurrency()): the central bank sells its dollars against the
 * month's deficit, and since XR1 the sale lifts the rate at its real size.
 * Under a band or a peg (XR2, THE REGIMES) the vault holds the rate instead:
 * it sells the month's deficit at the weak edge or the peg, and buys the
 * month's surplus at the strong edge (THE STRONG EDGE).
 *
 * THE RECORD is the cumulative balance: every month's balance of payments,
 * added up since founding - what the city has earned abroad less what it has
 * spent there. A MEASUREMENT, not a pot of money. The dollars a mill earns
 * exporting steel land in the mill's cash, and nothing takes them away and
 * puts them somewhere else; so it is deliberately not one of MoneyAudit's
 * pools, because adding the same dollar to a pool as well as to the sector
 * that earned it would create money - and that is why a city with a surplus
 * can still have an empty vault. It CAN go negative, and that is not a bug: a
 * young city importing its construction materials and exporting nothing is
 * exactly that, and the number saying so is the point. balanceFromFlows() is
 * its identity.
 *
 * THE HISTORY, because each step replaced the one before. In phase one the
 * reserve WAS the record - a measurement that could go negative, "living on
 * foreign credit that phase three has not modelled yet" - and it was to
 * become a real holding once the treasury could buy and sell. When it could,
 * the one field held both, and the conflation surfaced three times before it
 * was split. The vault that came out of the split was a LOCAL figure at the
 * price paid, so a hundredfold fall in the currency turned a US$1B vault into
 * US$10M and took its import cover down with it - until 2026-09-21, when it
 * was put in the money it actually is (THE VAULT IS HELD IN DOLLARS, in the
 * banner TWO NUMBERS THAT WERE ONE NUMBER below).
 */
public class ForeignAccounts {

    /* ------------------------------- the rate ------------------------------- */

    /**
     * Local currency per US dollar.
     *
     * PINNED AT ONE FOR PHASE ONE, and every conversion in the game goes through
     * toLocal()/toUsd() rather than assuming parity, so phase two moves the rate
     * in one place instead of hunting for multiplications that were never
     * written down.
     */
    public static final double OPENING_RATE = 1.00;

    /* ====================== WHAT MOVES THE RATE ======================
     *
     * Jerus: "foreign exchange isnt just how much reserves but also the trade
     * volume, so if deficit is small or large that also comes into play."
     *
     * Right, and the measurement showed why. Read as a stock, two very different
     * cities look alike; read against the flows they do not:
     *
     *   a residential city   deficit is 89% of its imports   catastrophic
     *   an industrial city   deficit is  4% of its trade     noise
     *
     * So the balance is read against what the city is, not as a stock (since
     * XR1, against a year of its GDP: THE FLOAT, below), and the RESERVES are
     * the buffer: the vault leans against a fall (A RESERVE DEFENDS A
     * CURRENCY) and, once it is thin, costs the money a premium (RISK_PREMIUM).
     * The rule itself is THE FLOAT; what it replaced is WHAT XR1 RETIRED.
     */

    /** Cover at which reserves fully absorb the month's deficit. */
    public static final double COMFORTABLE_COVER = 6;

    /** The most of the month's deficit deep reserves can absorb. */
    public static final double MAX_ABSORPTION = .85;

    /*
     * THE RATE CANNOT LEAVE THIS RANGE, WHATEVER THE ARITHMETIC SAYS - and
     * since 0.7.3 the range is nowhere a city goes.
     *
     * UNCAPPED, and these are now a numeric guard rather than a policy.
     *
     * They were .40 and 4.0, and the 4.0 was doing real damage: a bug in the
     * balance of payments drove the rate into it and it sat there for three
     * centuries looking like a considered outcome. A bound that a healthy model
     * never reaches costs nothing and a bound that a broken model rests against
     * HIDES the break - the ceiling was the reason nobody noticed that the
     * bank's whole funding cost was being declared as interest paid abroad.
     *
     * Jerus: "i think we should uncap the currency". So they are wide enough
     * that reaching one is itself the finding: a hundredfold move in either
     * direction is a currency that has genuinely collapsed or genuinely
     * quadrupled, and either way the run should be looked at rather than
     * quietly clipped. What actually holds the rate now is relative PPP, which
     * is a moving anchor and does not need a fence.
     *
     * AND NOT EVEN A HUNDREDFOLD (0.7.3). Jerus, on the guard of 100: "Better
     * to have it exceed otherwise one can just ignore once at 100." The year
     * book's acts 23 to 26 were a currency pinned at its guard: imports
     * stopped getting dearer and the player could stop caring - a bound a
     * broken city RESTS against is the same lie the 4.0 told, one floor up.
     * So the two are numerical guards
     * now and nothing else - against a rate run to nothing or to infinity, a
     * billion either way - and the reform scaling below stays, so they move
     * with the unit. (They do not catch a NaN: Math.min and Math.max hand one
     * straight through, and a rate that is not a number is a bug upstream to
     * be found, not clamped.) A currency that reaches a thousand local
     * dollars to one of theirs is a currency the city reforms - Denomination
     * is the tool for the numbers - and LongPlaytest still counts the months
     * at the guard, which should read zero.
     */
    /** The least a US dollar can cost in local money: a numerical guard against a rate run to nothing, never a price the game expects to see (.01 until 0.7.3). */
    public static final double MIN_RATE = 1e-9;
    /** ...and the most: a numerical guard against a rate run to infinity, never a price the game expects to see (100 until 0.7.3) - a city whose currency reaches a thousand reforms it. */
    public static final double MAX_RATE = 1e9;

    /**
     * The same three, in TODAY's money.
     *
     * All three are quoted in local dollars per US dollar, so all three are
     * PRICES and a currency reform divides them. Leaving the guards alone would
     * have been the subtler half of the same bug as parity: after lopping two
     * zeros a rate of 0.021 sat a hair above the floor of 0.01 it had until
     * 0.7.3, so a city whose currency then strengthened a little would find
     * it pinned - not by any economic force but by a bound left behind in the
     * old unit. The same shape
     * as MAX_SETTABLE on an indexed wage floor, and as MATERIAL_MONTHS before
     * that.
     */
    private double minRate = MIN_RATE;
    private double maxRate = MAX_RATE;
    private double parityBase = OPENING_RATE;

    public double getMinRate() { return minRate; }
    public double getMaxRate() { return maxRate; }

    /** Re-seeds the money CONSTANTS at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        minTrade   = MIN_TRADE / unit;
        minRate    = MIN_RATE / unit;
        maxRate    = MAX_RATE / unit;
        parityBase = OPENING_RATE / unit;
    }

    /**
     * The rate at which a basket costs the same at home and abroad.
     *
     * WAS THE CONSTANT 1.00, which is ABSOLUTE purchasing power parity pinned
     * to the founding year - a claim that the world's prices never move, which
     * was true only because nothing in the game moved them. Now that the world
     * has its own inflation (see WorldEconomy) the honest anchor is RELATIVE
     * PPP: the rate should go to wherever local prices stand against world
     * prices.
     *
     *     parity = local price level / world price level
     *
     * A city whose prices rise faster than the world's has a currency that
     * should weaken by the difference, and one that holds its prices while the
     * world inflates has a currency that should strengthen. That is the whole
     * of relative PPP and it is the first thing in this class that gives the
     * player a reason to care about their own inflation rate beyond how it
     * feels: it is what their currency is worth.
     *
     * Told to it each month by Game, because ForeignAccounts has no business
     * knowing what a shelf price is.
     */
    public static final double OPENING_PARITY = 1.00;

    /** Save slot 18. Not derivable: the local level it was struck from is a stock. */
    private double parity = OPENING_PARITY;

    private double localInflation, worldInflation;

    /**
     * The two inflation rates, and the level ratio parity is struck from.
     * The inflations move nothing here directly - the drift they were struck
     * for is deleted (WHAT XR1 RETIRED) - and are kept for the screens; Game
     * hands the same two to the real rate differential. Since XR1 the level
     * is the basket with rent capped (PriceIndex.getParityLevel()), and parity's
     * trend, not the month's level, is what the rate follows (THE FLOAT).
     *
     * @param localLevel the city's basket vs founding
     * @param worldLevel the world's, likewise
     * @param localRate  the city's annual inflation
     * @param worldRate  the world's
     */
    public void setParity(double localLevel, double worldLevel,
                          double localRate, double worldRate) {
        /* -------------------------------------------------------------------
           PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY.

           This was `parity = localLevel / worldLevel`, which is right about
           the SHAPE of relative PPP and silently wrong about its units. The two
           price levels are indices - both 1.00 at founding - so their ratio is
           a pure number, and treating that pure number as an exchange rate only
           works because the founding rate happens to be 1.00 as well. Nobody
           had written that assumption down and nothing depended on it until a
           currency reform came along.

           What a hundred-to-one reform then did: every price in the city
           divided, the exchange rate divided from 2.46 to 0.0246 - and parity
           did not, because a ratio of two indices cannot notice a change of
           units. So the level-PPP term in repriceCurrency() spent the next
           decade dragging the rate back up toward 1.00, which is to say back to
           its pre-reform value in the OLD money. Measured: the reformed city's
           currency fell a hundredfold, its price index went to 8,867 against
           the unreformed city's 3.39, and the rate ended pinned at its ceiling.

           So the ratio is anchored to a RATE. parityBase is what the currency
           was worth at founding, carried in today's unit, and it is
           redenominated with every other price. At founding it is 1.00 and this
           line means exactly what it meant before.
           ------------------------------------------------------------------- */
        if (localLevel > 0 && worldLevel > 0) parity = parityBase * localLevel / worldLevel;
        localInflation = localRate;
        worldInflation = worldRate;
    }

    /** How far the city's inflation is running above the world's. */
    public double inflationGap() { return localInflation - worldInflation; }

    /** The two inflations the month was handed - the halves of the real rate differential, for the forces page. */
    public double getLocalInflation() { return localInflation; }
    public double getWorldInflation() { return worldInflation; }

    public double getParity() { return parity; }

    private double rate = OPENING_RATE;
    private double lastAbsorption;

    /* ================ WHAT XR1 RETIRED (2026-10-21) ================
     *
     * The rule that stood here from 0.7.2 to 0.8.18 moved the rate a little
     * every month by a push and two pulls. Four forms of it, in the order they
     * came, and what the study measured (docs/studies/spec-exchange-rates.md,
     * section 1) of the whole:
     *
     *   THE LEVEL FORM pulled the rate to localLevel / worldLevel; it survived,
     *     slowed, as the PULL to parity at REVERSION .004 a month.
     *   THE DRIFT FORM (deleted in 0.7.2) moved the rate by the whole
     *     inflation differential every month; with imports repricing one for
     *     one it fed inflation through the currency (the 0.6.9 year book: prices
     *     399x, the currency at its guard).
     *   RATE_PULL's PUSH on the real gap (4 per unit of gap, at DRIFT_SPEED
     *     .02, less the vault's share, times the openness), with the trade
     *     balance's own push beside it (DEAD_BAND .05, quiet for SETTLING_MONTHS).
     *   THE UIP LEVEL (0.7.42): the push read the gap less EXPECTED_REVERSION
     *     .15 of the distance from parity, so a held gap settled at e^(-gap /
     *     .15); and THE ANCHORED DRIFT (0.7.42): the credible part of expected
     *     inflation against the world's, a twelfth a month.
     *
     * What they did, measured on slot 3 and the playtest: the D$ moved 1.2% a
     * year (a float moves 8-12%, a currency board under 1%), in runs of up to 78
     * months one way and 242 in slot 1; a pull of k toward a parity growing g a
     * month settles at rate/parity = k / (k + g), 0.70-0.80 for 333 years of the
     * playtest, a lag that never closes; parity was 41% to 80% rent; and the
     * treasury's purchases sat outside the balance while a fuller vault took
     * more of every fall, so buying US$205M of dollars left the D$ 7.7% dearer
     * two years on. `git show d2869a5:src/main/java/ham/citybuildersim/ForeignAccounts.java`
     * has the full text.
     */

    /** Below this much trade a month, the exchange rate is not a real price. */
    public static final double MIN_TRADE = 50;

    /**
     * The same floor, in TODAY's money.
     *
     * A MONEY THRESHOLD IN A RATIO'S CLOTHING. The old trade pressure was
     * scale-free - a deficit as a share of trade - so a currency reform should
     * not have touched it, and it did: the guard underneath it is fifty
     * DOLLARS of trade, and after lopping two zeros a city trading $6,000 a
     * month was trading $60 and fell straight through it. Pressure went to
     * exactly zero, the trade term stopped defending the currency at all, and
     * the two cities parted company in the first month. (gate() reads the
     * floor now, as the pressure did.)
     *
     * Fourth sighting of this exact shape: MATERIAL_MONTHS against a stock that
     * could not reach it, MAX_SETTABLE against an indexed wage floor, MIN_RATE
     * against a reformed exchange rate, and now this. A bound is in units, and
     * a bound whose units nobody wrote down is a bound that will be wrong
     * eventually.
     */
    private double minTrade = MIN_TRADE;

    /**
     * How much of it the reserve absorbs.
     *
     * A city with six months of import cover takes almost none of the hit; one
     * with nothing takes all of it. Only dollars in the vault can absorb
     * anything - an empty vault has nothing to spend, whatever the city owes or
     * is owed abroad.
     *
     * The vault's CAPACITY - what it COULD do, which the screens show:
     * repriceCurrency() sells this share of the month's deficit, since
     * 2026-09-21 only in a month the D$ would fall, and what the sale
     * actually met is getLastAbsorption(), which is less when the vault runs
     * short.
     */
    public double absorption() {
        if (getReservesUsd() <= 0) return 0;
        double cover = importCover();
        if (cover == Double.MAX_VALUE) return MAX_ABSORPTION;
        return MAX_ABSORPTION * Math.max(0, Math.min(1, cover / COMFORTABLE_COVER));
    }

    /**
     * The real rate gap the month was handed (Game.realRateDifferential()): the
     * city's real rate less the world's, a year - the dial less the inflation
     * it expects, against the world's base rate less its realised inflation.
     * THE FLOAT reads it through gapLevelFor().
     */
    private double realRateDifferential;

    /** @param differential the city's real rate less the world's real rate, annual; not a number reads as none */
    public void setRealRateDifferential(double differential) {
        this.realRateDifferential = Double.isFinite(differential) ? differential : 0;
    }

    /** The city's real rate less the world's, as the month was handed it. */
    public double getRealRateDifferential() { return realRateDifferential; }

    /* ================ THE FLOAT (XR1, 2026-10-21) ================
     *
     * Jerus, 2026-10-21: "the current one is wierd". The study found why (WHAT
     * XR1 RETIRED, above; docs/studies/spec-exchange-rates.md, sections 0 and
     * 1): the D$ glided a tenth as far as a real currency moves and lagged a
     * moving parity for ever, it sat 20-34% dearer than a parity that was mostly
     * rent, and buying dollars made it dearer. What replaced it follows Jerus's
     * calls of 2026-10-21: star 1, the market like a real float; star 2, parity
     * on the basket with rent capped at a third (PriceIndex.RENT_CAP); star 7,
     * interventions move the rate at the real size; star 8, the real gap's
     * effect "whatever real life does".
     *
     * Every month, in logs:
     *
     *   ln rate = trend + g x (gap reached + balance reached + cover reached + market)
     *             + intervention felt
     *
     *   trend         parity's level and slope (Holt), followed one for one: no lag.
     *   gap reached   where the real rate gap puts the D$ (gapLevelFor()): a part
     *                 at once, the rest building, so the peak comes in a year or two.
     *   balance       the overall balance against a year's GDP (balanceTarget()).
     *   cover         the vault's shortfall from THIN_COVER months of imports
     *                 (coverTarget()), its premium; both reached at MARKET_SPEED.
     *   market        the D$'s own swing: seeded, fading (MARKET_SIGMA).
     *   felt          the treasury's and the defence's dollars, at the real size.
     *   g             gate(): a city that hardly trades has no market price to move.
     *
     * Each piece is a LEVEL, not a speed, so nothing lags a moving target, and
     * each is a line of its own on the screens (getMoveTrend() and the rest).
     */

    /** How much of a month's jump in parity the trend takes at once: Holt's level, so a steady inflation gap passes into the rate one for one and a single month's spike moves it a sixth of itself. */
    public static final double TREND_LEVEL = 1 / 6.0;

    /** ...and how fast its slope learns, a twenty-fourth of the level's change a month: about two years. */
    public static final double TREND_SLOPE = 1 / 24.0;

    /** How many years the market expects a real rate gap to last, so a sustained point of gap holds the D$ about 2.5% dearer (or cheaper, when negative). */
    public static final double PERSISTENCE = 2.5;

    /** The most a real rate gap can move the level either way, a soft cap (tanh): markets do not believe a seventeen-point gap lasts. */
    public static final double GAP_CAP = .35;

    /** The share of a change in the gap's level that the rate shows in its month: Kearns and Manners' 1.5% a point on the day (R16). */
    public static final double GAP_IMPACT = .6;

    /** ...and the rest builds with this half-life, in months, so the peak comes within one to three years: delayed overshooting (R15). */
    public static final double GAP_BUILD_HALF_LIFE = 9;

    /** How far a year's overall balance, against a year's GDP, moves the level: a deficit of a tenth of GDP cheapens the D$ 5%. */
    public static final double BALANCE_WEIGHT = .5;

    /** ...at most this far either way. */
    public static final double BALANCE_CAP = .20;

    /** An empty vault's discount on the D$, falling to nothing at THIN_COVER months of imports, which is its safe cover: one dial, one meaning (the oldest rule of thumb, R31). */
    public static final double RISK_PREMIUM = .10;

    /** How much of a change in the balance's and the cover's levels reaches the rate in its month. */
    public static final double MARKET_SPEED = .5;

    /** The market's own swing a month, about 8% a year: a real float's (Jerus, star 1). */
    public static final double MARKET_SIGMA = .022;

    /** ...fading by half in this many months, the half-life of real currencies' gaps from parity (Rogoff, R1). */
    public static final double MARKET_HALF_LIFE = 42;

    /** The openness, trade over GDP, at which the market and the fundamentals reach the rate in full; a less open city feels them in proportion. */
    public static final double OPEN_ENOUGH = .10;

    /** How far a purchase of dollars moves the rate: 2% per 1% of a year's GDP, a sale the other way (the IMF's 1.7-2.0%, R33; Jerus, star 7). */
    public static final double INTERVENTION_IMPACT = 2.0;

    /** ...fading by half in this many months. */
    public static final double INTERVENTION_HALF_LIFE = 6;

    /**
     * The float's state, every figure a log and all of it saved (slots 45-70):
     * parity's trend (a level and a slope, from the first reprice), the real
     * gap's level and how much of it the rate shows, the balance's and the
     * cover's levels reached, the market's swing and the seed and draws that
     * make it a path, the interventions still felt, a year's GDP as a rolling
     * mean, and the month's moves by force with the contributions they were
     * struck from. A flow cannot be reconstructed from the state a month ended
     * in, so the last month's moves ride the save (the seventh, XR2's, with the
     * regimes' state, slots 71 on).
     */
    private double trendLevel, trendSlope;
    private boolean trendStarted;
    private double gapTarget, gapReached;
    private double balanceReached, coverReached;
    private double market;
    private double interventionFelt;
    private long marketSeed;
    private boolean marketSeeded;
    private long marketDraws;
    private double gdpTrailing;
    /** The five contributions last month (trend, gap, balance, cover, intervention): this month's move by force is the change from them. */
    private final double[] lastContrib = new double[5];
    /** This month's move by force (trend, gap, balance, cover, intervention, market, held); they add to the month's log move. The seventh, XR2's, is the hold of a peg or a band's edge: exactly 0 under Lean and Float. */
    private final double[] moves = new double[7];
    /** The month's fall against parity's trend before the defence leaned on it (repriceCurrency(), step 7). */
    private double lastWouldBeMove;
    /**
     * What the treasury has dealt since the last reprice, local money, a
     * purchase of dollars positive: the player deals between months, and the
     * next month's startMonth() clears boughtThisMonth and soldThisMonth
     * before any reprice reads them. Saved (slot 70): a purchase pressed
     * before a save moves the rate after the load.
     */
    private double dealtSinceReprice;

    /** Where a real rate gap puts the D$ against parity's trend, in logs: negative is dearer (the rate is D$ per US$, so a lower rate is a dearer D$), the gap's sign the other way round, and never past GAP_CAP either way. Pure. */
    public static double gapLevelFor(double gap) {
        return -GAP_CAP * Math.tanh(PERSISTENCE * gap / GAP_CAP);
    }

    /**
     * Where the overall balance puts the D$ in logs, positive weaker: minus
     * BALANCE_WEIGHT times the year's current plus financial account over the
     * year's GDP, at most BALANCE_CAP either way - nothing before a GDP is known.
     * The reserve transactions stay out of both, as they stay out of the
     * accounts: the vault meets a deficit by selling, which the felt carries.
     * Pure.
     */
    public double balanceTarget() {
        if (!(gdpTrailing > 0)) return 0;
        double raw = -BALANCE_WEIGHT * (currentTrailing + financialTrailing) / gdpTrailing;
        return Math.max(-BALANCE_CAP, Math.min(BALANCE_CAP, raw));
    }

    /** Where the vault's cover puts the D$ in logs, positive weaker: RISK_PREMIUM times how far the cover falls short of THIN_COVER months of imports - the whole premium on an empty vault, none with no import bill to cover. Pure. */
    public double coverTarget() {
        return RISK_PREMIUM * Math.max(0, 1 - importCover() / THIN_COVER);
    }

    /** How much of the market and the fundamentals reaches the rate: nothing while the city trades no more than minTrade a month, else its openness over OPEN_ENOUGH, at most 1. Pure. */
    public double gate() {
        if (exportsTrailing + importsTrailing + financialGrossTrailing <= minTrade) return 0;
        return Math.min(1, openness / OPEN_ENOUGH);
    }

    /** Parity's trend in logs: where the trend stands once started, parity's own log before the first reprice. */
    private double trendLog() {
        return trendStarted ? trendLevel : (parity > 0 ? Math.log(parity) : 0);
    }

    /** Parity's trend as a rate, local money per US dollar: the Holt level of parity, followed one for one; parity itself before the first reprice. Pure. */
    public double getParityTrend() { return Math.exp(trendLog()); }

    /** The trend's slope, a log a month. */
    public double getTrendSlope() { return trendSlope; }

    /** The real rate gap's level the market has priced, in logs (gapLevelFor() of the gap it was handed). */
    public double getGapTarget() { return gapTarget; }

    /** ...and how much of it the rate shows so far: GAP_IMPACT of each change at once, the rest building. */
    public double getGapReached() { return gapReached; }

    /** The balance's level as reached, in logs. */
    public double getBalanceReached() { return balanceReached; }

    /** The cover's premium as reached, in logs. */
    public double getCoverReached() { return coverReached; }

    /** The market's own swing, in logs, ungated; the rate carries gate() of it. */
    public double getMarket() { return market; }

    /** What the interventions still felt put on the rate, in logs, positive weaker: the treasury's purchases and the defence's sales, fading by half in INTERVENTION_HALF_LIFE months. */
    public double getInterventionFelt() { return interventionFelt; }

    /** Where the fundamentals put the D$ now, local money per US dollar: the trend and gate() of the real gap's, the balance's and the cover's levels reached and the world's (WN4: the dollar cycle, the risk-off and the terms of trade) - the rate without the market and the interventions. Pure. */
    public double getFairLevel() {
        return Math.exp(trendLog() + gate() * (gapReached + balanceReached + coverReached + worldReached));
    }

    /** How far the rate sits from the fundamentals' level: positive is weaker than it. Pure. */
    public double deviationFromFair() {
        double fair = getFairLevel();
        return fair > 0 ? rate / fair - 1 : 0;
    }

    /** Last month's move from parity's trend, in logs, positive weaker. */
    public double getMoveTrend() { return moves[0]; }
    /** ...from the real rate gap. */
    public double getMoveGap() { return moves[1]; }
    /** ...from the balance. */
    public double getMoveBalance() { return moves[2]; }
    /** ...from the vault's cover. */
    public double getMoveCover() { return moves[3]; }
    /** ...from interventions, the treasury's and the defence's. */
    public double getMoveIntervention() { return moves[4]; }
    /** ...from the market: what is left of the shadow rate's move once the five and the world's are taken, so the seven and the hold always add to the month's move, through a pin or a guard. Under Lean and Float the shadow is the rate. */
    public double getMoveMarket() { return moves[5]; }
    /** ...from the world (WN4): the dollar's own swing against every money, money running to safety, and the prices of what the city sells abroad - gate() times the change in the world's level (THE WORLD REACHES THE D$). Exactly 0 where no world is handed. */
    public double getMoveWorld() { return moveWorld; }
    /** ...from the hold (XR2): the part of the move the vault's hold of a peg or a band's edge made, and in a month a peg gave way the peg giving way. 0 under Lean and Float. */
    public double getMoveHeld() { return moves[6]; }

    /** Last month's move in the rate, in logs, positive weaker: the seven moves added (the six and the hold, and the world's). */
    public double getLastMove() {
        double sum = 0;
        for (double m : moves) sum += m;
        return sum + moveWorld;
    }

    /** The month's fall before the defence leaned on it: the move of the market and the fundamentals alone, parity's trend and the interventions left out - positive is a fall the vault, if it holds dollars, sells against (repriceCurrency(), step 7). */
    public double getLastWouldBeMove() { return lastWouldBeMove; }

    /** The seed the market's path is drawn from (52 bits, so it survives a double in the save). */
    public long getMarketSeed() { return marketSeed; }

    /** Whether the market is drawn at all: a city is seeded at founding (Game.buildWorld()); a bare ForeignAccounts has a quiet market. */
    public boolean isMarketSeeded() { return marketSeeded; }

    /** How many draws the path has taken: one a month the rate is not pinned. */
    public long getMarketDraws() { return marketDraws; }

    /** A year of the city's output as a rolling monthly mean, local money, the figure the balance and the interventions are weighed against. */
    public double getGdpTrailing() { return gdpTrailing; }

    /**
     * Seeds the market: one seed, one path (the nations study's D9). Cut to 52
     * bits so the save carries it as a double exactly, and the path restarts.
     */
    public void setMarketSeed(long seed) {
        marketSeed = seed & ((1L << 52) - 1);
        marketSeeded = true;
        marketDraws = 0;
        pegDraws = 0;   // ...and the peg's chance restarts beside it (XR2)
    }

    /**
     * Quiets the market, beside pinRate() and for the same reason: FOR FIXTURES
     * THAT MEASURE SOMETHING ELSE. A fixture about the real gap's level or the
     * defence's arithmetic cannot have a swing of 2% a month underneath it. A
     * new ForeignAccounts is quiet until it is seeded; Game seeds every city
     * at founding.
     */
    public void quietMarket() {
        marketSeeded = false;
        market = 0;
    }

    /** The market's next normal draw, a function of the seed and the draws taken - the same seed, the same path, however the months are grouped. */
    private double draw() {
        return new java.util.Random(mix(marketSeed, marketDraws++)).nextGaussian();
    }

    /** SplitMix64's finaliser over seed x golden-gamma + draws: neighbouring draws are unrelated seeds. */
    private static long mix(long seed, long draws) {
        long z = seed * 0x9E3779B97F4A7C15L + draws;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /* ================ THE WORLD REACHES THE D$ (WN4, 2026-10-30; XR3 folded in) ================
     *
     * Once the nations' moneys move month by month (WorldMoneys; docs/studies/
     * spec-exchange-rates.md, 3.5 and 3.7), the D$ against the US$ carries what
     * they carry, as one more level beside the fundamentals and gated with them
     * (a city that hardly trades has no market price to move):
     *
     *   world = cycle + riskOffLoading() x riskOff + terms of trade reached
     *   ln rate = trend + g x (gap + balance + cover + market + world) + felt
     *
     *   cycle         the dollar's, with a loading of 1: when the dollar rises
     *                 against everything it rises against the D$.
     *   risk-off      money runs to safety about once in eight years; the D$'s
     *                 loading is its traits' (riskOffLoading()): a small open
     *                 money +10% weaker, less with a deep vault and a surplus,
     *                 more with hot money and debt abroad. A city with six months
     *                 of cover and a surplus is a haven and rises, as the Swiss
     *                 franc does. A fall is a fall: the vault leans against it
     *                 and a peg feels it as pressure, with nothing special here.
     *   terms of trade  the city's own, from its exports by good (Game's THE
     *                 WORLD MONEYS IN THE MONTH hands, each January, the
     *                 export-weighted ln of the world prices of what it sells
     *                 against their normal, 2025's): dearer exports, a dearer D$,
     *                 by TOT_PASS, the level reached a third in each of the
     *                 year's first TOT_MONTHS reprices. A LEVEL, NOT A SUM OF
     *                 CHANGES: summed, a change weighted by the year before's
     *                 export values (which hold that year's prices) is biased,
     *                 because the prices revert - a good that was dear has a big
     *                 weight and then falls - and on the playtest's city the sum
     *                 only climbed, +0.09 to +0.69 in a century, the D$ weaker
     *                 every January and prices +8.7% a year against main's 3.7%
     *                 (WN4's review, 2026-10-31). A level of reverting prices
     *                 reverts, and is bounded by them.
     *
     * WHERE THERE IS NO WORLD every figure here is zero and the D$ is main's, to
     * the bit. Saved: slots 95-104. THE SLOTS AFTER 104 ARE THE VAULT'S (WN4
     * slice C, THE VAULT IN EIGHT): 105 to 141, and 142 to 146 what it earned (XR4, THE VAULT EARNS).
     */

    /** A small open money's loading on the risk-off: +10% weaker in a risk-off of size one (the study's 3.5; the Swiss franc's and the yen's are the other way, WorldMoneys.FRANC and YEN). */
    public static final double RISK_OFF_BASE = .10;

    /** The months of import cover at which the vault's pull on the loading is whole: six, twice THIN_COVER (est.). */
    public static final double HAVEN_COVER = 6;

    /** The most a deep vault takes off the loading (est.: with a surplus, six months of cover turns a small open money into a haven). */
    public static final double HAVEN_PULL = .10;

    /** What a year in surplus (the current plus financial account over the trailing year) takes off the loading (est.): six months of cover and a surplus read -0.05, the Swiss franc's. */
    public static final double SURPLUS_PULL = .05;

    /** How much hot money and debt abroad add to the loading, per year's GDP of them (est.: a fifth of a year's GDP adds ten points). */
    public static final double EXPOSURE_WEIGHT = .5;

    /** The least the loading can be: the yen's, the most a haven rises (WorldMoneys.YEN). */
    public static final double RISK_OFF_LOADING_MIN = -.08;

    /** The most the loading can be (est.: the emerging floats' is .20). */
    public static final double RISK_OFF_LOADING_MAX = .30;

    /** What the world is this month, handed before the reprice (setWorld()): the dollar cycle, the risk-off level, and the city's exposure (the hot money held and the debt abroad), local money. Saved. */
    private double worldCycle, worldRiskOff, worldExposure;
    /** The city's own terms of trade reached (a log, positive weaker), the piece each of the next totLeft reprices adds, and how many of them are left. Saved. */
    private double totReached, totStep;
    private int totLeft;
    /** The world's level as the last reprice struck it (cycle, the loading x risk-off, terms of trade; a log, positive weaker, ungated), gate() of it as last month's contribution, and the move it made this month. Saved. */
    private double worldReached, lastWorldContrib, moveWorld;
    /** The risk-off loading the last reprice used. Saved. */
    private double worldLoading;

    /**
     * What the world is this month: handed before repriceCurrency(), by Game, from the world's moneys. The dollar cycle and the risk-off level are
     * logs as WorldMoneys has them (positive: the dollar is strong, money runs to safety); exposureLocal is the hot money held here and the city's
     * debt abroad, local money. Handed, not read off getForeignDebt(): that is struck after the reprice and not saved, so a reloaded city would
     * reprice its first month on no debt (ReloadCheck, WN4: the loading 0.11 reloaded against 0.30 live). Zeros (or never called) is no world,
     * and the D$ is then exactly what it was without one. A figure that is no number reads 0.
     */
    public void setWorld(double cycle, double riskOff, double exposureLocal) {
        worldCycle = Double.isFinite(cycle) ? cycle : 0;
        worldRiskOff = Double.isFinite(riskOff) ? riskOff : 0;
        worldExposure = Double.isFinite(exposureLocal) ? Math.max(0, exposureLocal) : 0;
    }

    /**
     * The city's terms of trade in a January: exportPriceLevel is the export-weighted ln of the world prices of what it sells, each against its
     * normal (Game's, from PartnerTrade's exports by good over the year that finished and the nations' good prices this year, 1 being 2025's).
     * Dearer exports make a dearer D$, a lower rate: the term's level is minus TOT_PASS times it, reached a third in each of the next TOT_MONTHS
     * reprices (WorldMoneys' pace for a nation's money). What the last January had not yet reached comes at once, so no news is lost; a level
     * that is no number is no news. A level, not a sum of changes: see THE WORLD REACHES THE D$.
     */
    public void stepTermsOfTrade(double exportPriceLevel) {
        if (!Double.isFinite(exportPriceLevel)) return;
        totReached += totStep * totLeft;
        totStep = (-WorldMoneys.TOT_PASS * exportPriceLevel - totReached) / WorldMoneys.TOT_MONTHS;
        totLeft = WorldMoneys.TOT_MONTHS;
    }

    /**
     * The D$'s loading on the risk-off from the city's traits now: RISK_OFF_BASE, less HAVEN_PULL for a vault of up to HAVEN_COVER months of imports,
     * less SURPLUS_PULL for a trailing year in surplus (current plus financial account), plus EXPOSURE_WEIGHT of the hot money and debt abroad as a
     * share of a year's GDP; at most RISK_OFF_LOADING_MAX and at least RISK_OFF_LOADING_MIN. RISK_OFF_BASE while there is no GDP yet. Pure.
     */
    public double riskOffLoading() {
        if (!(gdpTrailing > 0)) return RISK_OFF_BASE;
        double loading = RISK_OFF_BASE - HAVEN_PULL * Math.min(importCover() / HAVEN_COVER, 1)
                - (currentTrailing + financialTrailing > 0 ? SURPLUS_PULL : 0)
                + EXPOSURE_WEIGHT * worldExposure / (12 * gdpTrailing);
        return Math.max(RISK_OFF_LOADING_MIN, Math.min(RISK_OFF_LOADING_MAX, loading));
    }

    /** The dollar cycle as last handed, a log. */
    public double getWorldCycle() { return worldCycle; }

    /** The risk-off level as last handed. */
    public double getWorldRiskOff() { return worldRiskOff; }

    /** The risk-off loading the last reprice used (riskOffLoading() as it stood then). */
    public double getWorldLoading() { return worldLoading; }

    /** The city's own terms of trade reached, a log, positive weaker. */
    public double getTermsOfTradeReached() { return totReached; }

    /** The world's level as the last reprice struck it, in logs, positive weaker, before the gate. */
    public double getWorldLevel() { return worldReached; }

    /* ==================== A RESERVE DEFENDS A CURRENCY ====================
     *
     * Jerus, 2026-09-21: "a reserve defends a currency; it does not hold one
     * down." So the central bank sells dollars only in a month the D$ would
     * FALL (repriceCurrency(), step 7), never in a month it would rise. It was
     * damped both ways until then, and a deep vault muted the only two forces
     * that pull a currency back from a spiral - the surplus a weak currency
     * earns, and the policy rate: of the ensemble's eight seeds three
     * reproduced the year book that started the batch (prices 141x and 190x
     * founding, the currency at its guard), against a median 1.60x once it
     * leaned on the way down only. Leaning against a rise is BUYING dollars,
     * which is a decision, not something a vault does by standing there.
     *
     * A DEFENCE THAT SPENDS (0.7.2). Jerus, on the founding reserve: "1B is in
     * usd in the reserve ... since 99% players wont add to reserves". The
     * central bank SELLS:
     *
     *     dollarsSold = absorption() x deficitUsd, at most what the vault holds
     *
     * deficitUsd is the month's OWN net outflow (monthDeficitUsd()), not a
     * trailing sum: a central bank sells what is asked of it this month, and a
     * month with no deficit sells nothing. getLastAbsorption() is the share of
     * the deficit the sale met - the capacity with a full vault, less as the
     * cover runs low, nothing on an empty one.
     *
     * SINCE XR1 THE SALE MOVES THE RATE AT ITS REAL SIZE, through the channel
     * the treasury's own purchases use (INTERVENTION_IMPACT): the dollars it
     * sells lift the D$, and fade. Until then the sale only damped a push by
     * its share of the deficit - which is why a bigger vault took more of every
     * fall and buying dollars left the D$ dearer (WHAT XR1 RETIRED).
     *
     * THE OTHER SIDE IS THE CENTRAL BANK'S EQUITY (CentralBank, THE DEFENCE):
     * the vault's dollars leave here, the central bank's equity falls by their
     * local price, and M0 does not move - a capital transaction against the
     * world, not money destroyed; no pool moves, so the audit declares nothing.
     * lifetimeIntervention falls by the local money exactly as a treasury sale
     * does. The treasury's own sellReserves() is not this and is untouched.
     */

    /**
     * The month's own net outflow, in dollars at the month's rate: its current
     * plus financial account when that is negative, and nothing when it is
     * not. The reserve transactions are not in it - they are the financing
     * item, not the balance (MoneyAudit, Scope.RESERVE).
     */
    public double monthDeficitUsd() {
        double balance = currentAccount() + financialAccount();
        return balance < 0 && rate > 0 ? -balance / rate : 0;
    }

    /** The most the central bank sells against a month's deficit: the vault's capacity times the deficit, at most the vault; nothing on a month with no deficit. What it does sell is what takes back absorption() of the month's fall (repriceCurrency(), step 7). */
    private double dollarsFor(double deficitUsd) {
        double held = getReservesUsd();
        if (!(deficitUsd > 0) || !(held > 0)) return 0;
        return Math.min(absorption() * deficitUsd, held);
    }

    /**
     * The sale itself, at the month's rate - before the reprice moves it, so
     * the dollars sold are never revalued (revalueVault() strikes the move on
     * what is left). Selling everything empties the vault exactly.
     */
    private void sellToDefend(double usd) {
        if (!(usd > 0) || rate <= 0) { defenceUsd = defenceLocal = 0; return; }
        defenceUsd = usd;
        defenceLocal = usd * rate;
        vaultRemove(usd);
        defenceUsdLifetime += usd;
        lifetimeIntervention -= defenceLocal;
    }

    /**
     * THE MONTH'S DEFENCE: the dollars the central bank sold at the last
     * reprice and the local money they fetched, at that month's rate, and
     * the dollars sold since founding. Struck at the reprice and not cleared
     * at the top of the month, so the screens show the month that closed
     * between two presses; saved (slots 25-27), because a flow a screen shows
     * cannot be rebuilt from the state the month left. An older save reads
     * none sold.
     */
    private double defenceUsd, defenceLocal, defenceUsdLifetime;

    /** Dollars sold defending the currency at the last reprice. */
    public double getDefenceUsd()         { return defenceUsd; }
    /** ...and the local money they fetched, which is what the central bank's equity fell by (CentralBank.dollarsSold()). */
    public double getDefenceLocal()       { return defenceLocal; }
    /** Dollars sold defending the currency since founding. */
    public double getDefenceUsdLifetime() { return defenceUsdLifetime; }

    /** The share of the month's deficit the vault met - the realised absorption, 0 on a month nothing was sold. */
    public double getLastAbsorption() { return lastAbsorption; }

    /**
     * Holds the exchange rate still.
     *
     * FOR FIXTURES THAT MEASURE SOMETHING ELSE. MiningCheck asks what a foundry
     * earns on imported scrap against local ore; BankCheck asks what a bank does
     * with its capital. Neither is a question about the currency, and a rate
     * drifting underneath them changes every world price in the city while they
     * are trying to hold one thing constant - MiningCheck went red because scrap
     * had moved from $400 a tonne to $420 while it was looking away.
     *
     * A fixture that has not been checked for what else it is measuring is not
     * evidence, and this is the lever that stops the currency being one of those
     * things.
     */
    public void pinRate(double fixed) {
        this.rate = Math.max(minRate, Math.min(maxRate, fixed));
        this.pinned = true;
    }

    private boolean pinned;

    /** True while the rate is being held for a measurement. */
    public boolean isPinned() { return pinned; }

    /**
     * Moves the rate on the month just taken: THE FLOAT, in nine steps.
     *
     * A pinned rate does not move and is not defended by a sale: the promise
     * is the treasury's, and nothing is sold - and nothing is drawn, so a pin
     * does not shift the market's path. Nor does a pin touch the regime's
     * state (XR2, THE REGIMES): the edge buys nothing and no peg breaks.
     *
     * Steps 1-6 are the same under every regime; step 7 is the regime's own
     * (LEAN is the lean against a fall, as it has always been; FLOAT leans on
     * nothing; BAND and PEG hold the rate where they say), and steps 8-9 close
     * the month on the rate it set. See THE REGIMES.
     */
    public void repriceCurrency() {
        if (pinned) {
            defenceUsd = defenceLocal = 0;
            edgeBoughtUsd = edgeBoughtLocal = 0;
            brokeThisMonth = false;
            java.util.Arrays.fill(moves, 0);
            moveWorld = 0;
            lastWouldBeMove = 0;
            dealtSinceReprice = 0;   // ...and a pinned month's dealing is the promise's, not the market's
            return;
        }
        // The month's regime events are the month's (XR2): cleared as it opens.
        brokeThisMonth = false;
        edgeBoughtUsd = edgeBoughtLocal = 0;
        double prevLog = Math.log(rate);
        // ...and where the SHADOW stood last month: the rate itself, unless last
        // month's reprice held the rate away from it (a peg in force, a band at
        // an edge), when it is the float's own rule the held rate was set beside.
        // Under Lean the two are the same number, bit for bit.
        double prevShadowLog = heldLastMonth ? lastShadowLog : prevLog;

        // 1. PARITY'S TREND, Holt's level and slope on ln(parity): the first
        // reprice opens it on parity, so a founding starts on its own level.
        double lnParity = parity > 0 ? Math.log(parity) : trendLog();
        if (!trendStarted) {
            trendLevel = lnParity;
            trendSlope = 0;
            trendStarted = true;
        } else {
            double before = trendLevel;
            trendLevel = TREND_LEVEL * lnParity + (1 - TREND_LEVEL) * (trendLevel + trendSlope);
            trendSlope = TREND_SLOPE * (trendLevel - before) + (1 - TREND_SLOPE) * trendSlope;
        }

        // 2. THE REAL RATE GAP: GAP_IMPACT of a change in its level shows at
        // once and the rest builds. No started flag - both open at nothing, so a
        // founding's gap is news like any other.
        double target = gapLevelFor(realRateDifferential);
        gapReached += GAP_IMPACT * (target - gapTarget);
        gapTarget = target;
        gapReached += (1 - Math.pow(.5, 1 / GAP_BUILD_HALF_LIFE)) * (gapTarget - gapReached);

        // 3. THE BALANCE AND THE COVER, levels the market reaches at MARKET_SPEED.
        balanceReached += MARKET_SPEED * (balanceTarget() - balanceReached);
        coverReached += MARKET_SPEED * (coverTarget() - coverReached);

        // 4. THE MARKET, its own swing: an AR(1) drawn from the seed. Nothing is
        // drawn while it is quiet, so a fixture's rate has nothing underneath it.
        market = Math.pow(.5, 1 / MARKET_HALF_LIFE) * market + (marketSeeded ? MARKET_SIGMA * draw() : 0);

        // 5. WHAT INTERVENTIONS STILL FEEL, fading, plus what the treasury has
        // dealt since the last reprice (buyReserves(), sellReserves()): a
        // purchase of dollars cheapens the D$. Since the last reprice and not
        // this month, because the player deals BETWEEN months, before the
        // next month's startMonth() clears the month's bookkeeping. The
        // founders' dollars are an endowment, forgotten at founding
        // (forgetDealing()), so they never count.
        interventionFelt *= Math.pow(.5, 1 / INTERVENTION_HALF_LIFE);
        // ...and what a peg's breaking still puts on the rate (XR2): it fades as
        // the market's swings do, and is nothing, so 0 stays 0, until a peg breaks.
        breakFelt *= Math.pow(.5, 1 / MARKET_HALF_LIFE);
        double annual = 12 * gdpTrailing;
        if (annual > 0) interventionFelt += INTERVENTION_IMPACT * dealtSinceReprice / annual;
        dealtSinceReprice = 0;

        // 5b. THE WORLD (WN4, THE WORLD REACHES THE D$): the terms of trade reach
        // their term a piece a month, and the world's level is the dollar cycle, the
        // risk-off at the loading this city's traits give it, and that term. All
        // zero where no world was handed, and then nothing below moves by a bit.
        if (totLeft > 0) {
            totReached += totStep;
            totLeft--;
        }
        worldLoading = riskOffLoading();
        double world = worldCycle + worldLoading * worldRiskOff + totReached;
        worldReached = world;

        // 6. THE MONTH'S LOG RATE.
        double g = gate();
        double fundamentals = gapReached + balanceReached + coverReached + market;
        double x = trendLevel + g * (fundamentals + world) + interventionFelt + breakFelt;

        /*
         * 7. LEAN AGAINST A FALL (A RESERVE DEFENDS A CURRENCY): the fall the
         * market and the fundamentals would make this month - not parity's
         * trend, and not the fading of what was sold before. A bank that
         * leaned against parity would spend its vault holding up a currency
         * its own prices are cheapening; one that leaned against its own
         * support fading would sell again every month to hold where it had
         * got to, which is a peg, and lose the vault.
         *
         * It takes back absorption() of that fall, the vault's capacity by its
         * cover, as the old defence damped a push by it, and sells only the
         * dollars that takes at INTERVENTION_IMPACT - at most absorption() x
         * the month's deficit, the most anyone came to the window for, and at
         * most the vault. The support then fades at INTERVENTION_HALF_LIFE: a
         * fall is spread over months, and the fundamentals have the last word.
         * Measured on MonetaryCheck's town at a held 3% (US$25M of founders'
         * dollars against a town of 357): sized to the whole deficit (slice
         * 1's first cut) the sales held the D$ 26% over its fair level by
         * month 26 and emptied the vault by month 42, and the fall that
         * followed fed it 45% a year; leaning against its own fading as well,
         * 25% over by month 36 and the vault at 1.3 months by 60, the D$ then
         * 80% weaker in two years. A rise is never defended: nothing is bought.
         */
        /*
         * ...THAT IS THE LEAN REGIME, and it is exactly what the city did
         * before there were regimes (XR2 added the other three beside it, THE
         * REGIMES): the one difference is that the fall is struck from where
         * the SHADOW stood last month, which is the rate itself unless last
         * month held the rate away from it - so after a peg or a band is left
         * for Lean, the release of the hold is not a new fall to sell against.
         */
        double deficitUsd = monthDeficitUsd();
        // The month's own balance in dollars, at the month's rate before it moves
        // (a surplus positive): what a peg meets, and a band's edge.
        double balanceUsd = rate > 0 ? (currentAccount() + financialAccount()) / rate : 0;
        double fall = x - prevShadowLog - (trendLevel - lastContrib[0]) - (interventionFelt - lastContrib[4]);
        lastWouldBeMove = fall;
        double shadow;
        if (regime == Regime.LEAN) {
            double sold = 0;
            if (fall > 0 && annual > 0 && rate > 0) {
                double need = absorption() * fall * annual / INTERVENTION_IMPACT / rate;
                sold = Math.min(need, dollarsFor(deficitUsd));
            }
            sellToDefend(sold);
            lastAbsorption = defenceUsd > 0 && deficitUsd > 0 ? defenceUsd / deficitUsd : 0;
            if (defenceLocal > 0) {
                interventionFelt -= INTERVENTION_IMPACT * defenceLocal / annual;
                x = trendLevel + g * (fundamentals + world) + interventionFelt + breakFelt;
            }

            // 8. THE RATE, inside the numerical guards.
            rate = clampRate(Math.exp(x));
            shadow = rate;
            doubtShare = lastHazard = lastPegGap = 0;
        } else if (regime == Regime.FLOAT) {
            // Nothing is sold and nothing leaned on: the rule's rate, as it falls.
            sellToDefend(0);
            lastAbsorption = 0;
            rate = clampRate(Math.exp(x));
            shadow = rate;
            doubtShare = lastHazard = lastPegGap = 0;
        } else if (regime == Regime.BAND) {
            sellToDefend(0);
            lastAbsorption = 0;
            // The centre crawls first, so the edges are read where it has got to.
            bandCentre *= Math.exp(bandSlope / 12);
            shadow = clampRate(Math.exp(x));
            double weak = bandWeakEdge();
            double strong = bandStrongEdge();
            if (shadow > weak) {
                // Past the weak edge: the vault sells the month's whole deficit and the
                // rate is held at the edge - unless paying it would take the vault under
                // its floor, when the band gives way for the month (the regime stays).
                double need = Math.max(0, -balanceUsd);
                if (getReservesUsd() - need < vaultFloorUsd()) {
                    rate = shadow;
                } else {
                    sellToDefend(need);
                    rate = clampRate(weak);
                }
            } else if (shadow < strong) {
                // Past the strong edge: the vault buys the month's surplus (THE STRONG EDGE).
                edgeBuy(Math.max(0, balanceUsd));
                rate = clampRate(strong);
            } else {
                rate = shadow;
            }
            doubtShare = lastHazard = lastPegGap = 0;
        } else {
            // PEG. The shadow is where the float's rule would have the D$; its gap
            // from the peg is the pressure, and the pressure is the chance of a break.
            sellToDefend(0);
            lastAbsorption = 0;
            if (!(pegRate > 0)) pegRate = rate;
            shadow = clampRate(Math.exp(x));
            double gap = Math.log(shadow / pegRate);
            double h = hazard(Math.max(0, gap));
            BreakCause cause = BreakCause.NONE;
            // (a) DOUBT: a draw from a stream of its own, so the market's path is the same
            // pegged or not. A quiet (unseeded) market never draws and never doubts.
            if (marketSeeded) {
                double u = new java.util.Random(mix(marketSeed ^ PEG_SALT, pegDraws++)).nextDouble();
                if (u < h) cause = BreakCause.DOUBT;
            }
            // (b) THE VAULT: paying the month's deficit at the peg would take it under its floor.
            if (cause == BreakCause.NONE && balanceUsd < 0 && getReservesUsd() + balanceUsd < vaultFloorUsd()) {
                cause = BreakCause.VAULT;
            }
            lastHazard = h;
            lastPegGap = gap;
            if (cause == BreakCause.NONE) {
                // (c) HELD: the vault meets the month's whole balance at the peg.
                if (balanceUsd < 0) sellToDefend(-balanceUsd); else edgeBuy(balanceUsd);
                rate = clampRate(pegRate);
                doubtShare = gap > 0 ? h : 0;
            } else {
                // THE BREAK: the rate lands at the shadow plus OVERSHOOT of the gap again
                // (a gap the other way is no overshoot), and fades like the market's swings.
                double overshoot = OVERSHOOT * Math.max(0, gap);
                breakFelt += overshoot;
                rate = clampRate(Math.exp(x + overshoot));
                regime = Regime.FLOAT;
                brokeThisMonth = true;
                breaks++;
                lastBreakCause = cause;
                lastBreakJump = Math.log(rate) - prevLog;
                lastBreakShadow = shadow;
                shadow = rate;   // it floats now
                doubtShare = 0;
            }
        }

        // 9. THE MONTH'S MOVES BY FORCE, from the contributions: the market's
        // line is what is left of the SHADOW's move, so the six (the world's,
        // WN4, is the sixth beside the five, and the market the remainder) add to
        // it, and the hold (what a peg or an edge made of the rate beside the
        // shadow, and in a break month the peg giving way) is the last: all of
        // them add to the month's move. Under Lean and Float the shadow is the
        // rate, and the hold is exactly nothing; with no world the world's line is.
        double[] c = { trendLevel, g * gapReached, g * balanceReached, g * coverReached, interventionFelt };
        double sum = 0;
        for (int k = 0; k < 5; k++) {
            moves[k] = c[k] - lastContrib[k];
            sum += moves[k];
            lastContrib[k] = c[k];
        }
        double worldContrib = g * world;
        moveWorld = worldContrib - lastWorldContrib;
        lastWorldContrib = worldContrib;
        moves[5] = Math.log(shadow) - prevShadowLog - sum - moveWorld;
        moves[6] = (Math.log(rate) - prevLog) - (Math.log(shadow) - prevShadowLog);
        lastShadowLog = Math.log(shadow);
        heldLastMonth = (regime == Regime.BAND || regime == Regime.PEG) && rate != shadow;
    }

    /** A rate inside the numerical guards. */
    private double clampRate(double r) { return Math.max(minRate, Math.min(maxRate, r)); }

    /** How far the currency sits from parity. Negative is stronger than parity. */
    public double deviationFromParity() { return parity > 0 ? rate / parity - 1 : 0; }

    /** Local currency per US dollar. */
    public double getRate() { return rate; }

    /** What a USD amount is worth in the city's own money. */
    public double toLocal(double usd) { return usd * rate; }

    /** ...and the other way. */
    public double toUsd(double local) { return rate > 0 ? local / rate : 0; }

    /* ================ THE REGIMES (XR2, 2026-10-22) ================
     *
     * Jerus, 2026-10-21 (docs/studies/spec-exchange-rates.md, 3.6; his calls
     * star 3, 4, 5, 6 and 13): the city chooses what its D$ is held to. Four
     * regimes, one reprice (repriceCurrency(), steps 7 to 9):
     *
     *   LEAN   the default, and what the city did before there were regimes:
     *          the float's rate, a fall damped by the vault's sales (XR1,
     *          step 7). Every number it computes is the number it computed
     *          before XR2 - the new terms below are exactly nothing under it.
     *   FLOAT  the float's rate, and the vault does nothing.
     *   BAND   the rate floats inside centre x (1 -/+ width), the centre
     *          crawling at the slope; at the weak edge the vault sells the
     *          month's deficit, at the strong edge it buys the month's surplus.
     *   PEG    the rate is held at the peg, and the vault meets the month's whole
     *          balance at it. The rate is D$ per US$, so a HIGHER rate is a
     *          WEAKER D$ - the weak edge is the upper one.
     *
     * THE SHADOW RATE is the float's rule run beside every regime: where the D$
     * would be. Under Lean and Float it is the rate itself; under a band or a
     * peg it is the rate the hold is set against, and its gap from the peg (or
     * the band's centre) is the pressure, shown (currencyGap()).
     *
     * HOW A PEG BREAKS (star 4): the vault cannot pay the month's deficit at
     * the peg without falling under VAULT_FLOOR months of imports (Krugman's
     * first generation), or by chance, hazard(gap) a month, which rises with the
     * shadow's gap from the peg (Obstfeld's second: doubt itself breaks a peg).
     * The rate lands at the shadow plus OVERSHOOT of the gap again (breakFelt,
     * fading as the market's swings do), the regime becomes Float, and
     * Expectations loses credibility (pegBroke()). The chance is drawn from a
     * stream of its own (pegDraws) so the market's path is the same pegged or
     * not, and a quiet (unseeded) ForeignAccounts never breaks by chance.
     *
     * THE STRONG EDGE (star 6): the vault takes the dollars at once, at the
     * month's rate before the reprice (as sellToDefend() sells), and the local
     * price is settled at the top of the next month by its payer, inside the
     * audit's window (Game.settleTheEdge()): by default the central bank, in
     * new money (CentralBank.buyDollarsForVault(), M0 rises as the SNB's did
     * 2011-2015), or the treasury, out of its cash, as a reserve purchase of
     * its own (noteEdgePaidByTreasury()). Neither is dealing the next reprice
     * feels: the edge's purchase IS the hold.
     *
     * Leaving a regime keeps its settings in the fields, so a re-entry can show
     * them; they act only in force.
     */

    /** The four ways a city can hold its D$ (XR2; the study's 3.6). */
    public enum Regime {
        FLOAT("Float"), LEAN("Lean against falls"), BAND("Band"), PEG("Peg to the US$");
        public final String words;
        Regime(String words) { this.words = words; }
    }

    /** Who pays the local price of the dollars the vault buys at the strong edge (star 6). */
    public enum EdgePayer {
        CENTRAL_BANK("The central bank, in new money"), TREASURY("The treasury, out of its cash");
        public final String words;
        EdgePayer(String words) { this.words = words; }
    }

    /** Why a peg broke: the vault could not pay the month's deficit, or doubt itself did it (star 4). */
    public enum BreakCause {
        NONE("none"), VAULT("the vault ran short"), DOUBT("doubt");
        public final String words;
        BreakCause(String words) { this.words = words; }
    }

    /** Where a band opens, this share either side of its centre: the study's board 2 (est.; Singapore's band is thought to be about 2% either side, R36). */
    public static final double BAND_WIDTH = .03;

    /** The widest a band can be set, a share either side of the centre. */
    public static final double MAX_BAND_WIDTH = .25;

    /** The steepest a band's centre can crawl, a log a year, either way. */
    public static final double MAX_BAND_SLOPE = .25;

    /** Months of imports the vault must keep under a peg or a band: under it the hold gives way (the usual rule of thumb is three months of cover; the study's prototype breaks at one). */
    public static final double VAULT_FLOOR = 1;

    /** A broken peg lands at the shadow plus this share of the gap from the peg again, and fades as the market's swings do (RatesModel, the study's prototype). */
    public static final double OVERSHOOT = .25;

    /** A peg's chance of breaking by doubt in a month at no gap at all (0.1%). */
    public static final double BREAK_BASE = .001;

    /** ...rising by this much for each BREAK_GAP the shadow sits past the peg beyond the first BREAK_GAP (3% a month at a 20% gap). */
    public static final double BREAK_SLOPE = .03;

    /** ...the gap, a log, under which doubt adds nothing to BREAK_BASE (10%). */
    public static final double BREAK_GAP = .10;

    /** The salt that makes the peg's draws a stream of their own (odd). */
    private static final long PEG_SALT = 0x5DEECE66D1B54A33L;

    /** A peg's chance of breaking in a month by doubt, given the log of the shadow over the peg (pass it at least 0): BREAK_BASE, plus BREAK_SLOPE for each BREAK_GAP past BREAK_GAP. Pure. */
    public static double hazard(double gap) {
        return BREAK_BASE + BREAK_SLOPE * Math.max(0, gap - BREAK_GAP) / BREAK_GAP;
    }

    /** The regime in force. A new city is on Lean, the regime it has always had. */
    private Regime regime = Regime.LEAN;
    /** Who pays at the strong edge. */
    private EdgePayer edgePayer = EdgePayer.CENTRAL_BANK;
    /** The band: its centre now (D$ per US$), the log a year the centre crawls, and the share either side the edges sit. Kept when the regime is left. */
    private double bandCentre, bandSlope, bandWidth = BAND_WIDTH;
    /** The peg, D$ per US$. Kept when the regime is left. */
    private double pegRate;
    /** What a break put on the rate and has not yet faded, in logs, positive weaker: the overshoot (repriceCurrency(), step 6). Saved. */
    private double breakFelt;
    /** The log of the shadow rate at the last reprice, and whether that reprice held the rate away from it (a peg in force, a band at an edge). Saved. */
    private double lastShadowLog;
    private boolean heldLastMonth;
    /** How many draws the peg's chance has taken: a stream apart from the market's. Saved. */
    private long pegDraws;
    /** The month's break chance as the last reprice left it (what the hot money takes of itself home the next month), the last reprice's hazard, and the shadow's log gap from the peg then. Saved. */
    private double doubtShare, lastHazard, lastPegGap;
    /** The strong edge's dollars the vault took at the last reprice and their local price at the rate before it: the month's, cleared as the next reprice opens. Saved. */
    private double edgeBoughtUsd, edgeBoughtLocal;
    /** ...and since founding. Saved. */
    private double edgeBoughtUsdLifetime;
    /** The local price still owed for the dollars the edge bought, and who owes it as it stood when they were bought: settled at the top of the next month (Game.settleTheEdge()). Saved: a purchase pressed before a save is paid after the load. */
    private double edgeOwedLocal;
    private EdgePayer edgeOwedPayer = EdgePayer.CENTRAL_BANK;
    /** Pegs broken since founding, and the last one's cause, jump (the log move of the rate that month) and shadow rate; whether the last reprice broke one. Saved. */
    private int breaks;
    private BreakCause lastBreakCause = BreakCause.NONE;
    private double lastBreakJump, lastBreakShadow;
    private boolean brokeThisMonth;

    /**
     * Chooses the regime. Entering a peg pegs to the rate of the day; entering a
     * band centres it on the rate of the day, flat, at the board's width (unless
     * it is the band already). Leaving a regime keeps its settings in the fields
     * so a re-entry can show them, but they act only in force - all but the
     * peg's doubt, which acts the month after it was struck (Game hands it to
     * the hot money before the reprice), so leaving a peg clears it (FX8,
     * 2026-10-22): until then a peg left between two presses sent the hot money
     * home at the dead peg's hazard for one more month, against the "0 under
     * every other regime" Game says.
     */
    public void setRegime(Regime r) {
        if (r == null || r == regime) return;
        if (r == Regime.PEG) {
            pegRate = rate;
        } else if (r == Regime.BAND) {
            bandCentre = rate;
            bandSlope = 0;
            bandWidth = BAND_WIDTH;
        }
        if (regime == Regime.PEG) doubtShare = 0;
        regime = r;
    }

    /** A devaluation (a higher peg, a weaker D$) or a revaluation, under a peg; nothing under another regime. The rate follows at the next reprice. */
    public void repeg(double newPeg) {
        if (regime != Regime.PEG || !(newPeg > 0) || !Double.isFinite(newPeg)) return;
        pegRate = clampRate(newPeg);
    }

    /** Sets the band: its centre (D$ per US$), the log a year it crawls and the share either side the edges sit, each inside its limits. The rate follows at the next reprice. */
    public void setBand(double centre, double slopeAYear, double width) {
        if (centre > 0 && Double.isFinite(centre)) bandCentre = clampRate(centre);
        if (Double.isFinite(slopeAYear)) bandSlope = Math.max(-MAX_BAND_SLOPE, Math.min(MAX_BAND_SLOPE, slopeAYear));
        if (Double.isFinite(width)) bandWidth = Math.max(0, Math.min(MAX_BAND_WIDTH, width));
    }

    /** Chooses who pays for the dollars the vault buys at the strong edge. */
    public void setEdgePayer(EdgePayer p) {
        if (p != null) edgePayer = p;
    }

    public Regime getRegime()         { return regime; }
    public EdgePayer getEdgePayer()   { return edgePayer; }
    public double getPegRate()        { return pegRate; }
    public double getBandCentre()     { return bandCentre; }
    /** The log a year the band's centre crawls. */
    public double getBandSlope()      { return bandSlope; }
    /** The share either side of the centre the edges sit. */
    public double getBandWidth()      { return bandWidth; }
    /** The band's weak edge, D$ per US$ (a higher rate is a weaker D$): the centre and its width. */
    public double bandWeakEdge()      { return bandCentre * (1 + bandWidth); }
    /** ...and its strong edge. */
    public double bandStrongEdge()    { return bandCentre * (1 - bandWidth); }

    /**
     * Where the float's rule put the D$ at the last reprice, D$ per US$: the
     * rate itself unless that reprice held the rate away from it (a peg in
     * force, a band at an edge). Pure.
     */
    public double getShadowRate() { return heldLastMonth ? Math.exp(lastShadowLog) : rate; }

    /** Whether the last reprice held the rate away from the shadow: a peg in force, a band at an edge. */
    public boolean isHeld() { return heldLastMonth; }

    /**
     * How far the D$ sits past its target, a share, positive weaker: the
     * shadow over the peg under a peg, over the band's centre under a band, and
     * nothing under Lean and Float, which have no target to aim at (a call for
     * Jerus whether a float should aim at the real rate). What the autopilot's
     * currency aim sets the dial against (DebtManager.currencyRate()). Pure.
     */
    public double currencyGap() {
        if (regime == Regime.PEG && pegRate > 0) return getShadowRate() / pegRate - 1;
        if (regime == Regime.BAND && bandCentre > 0) return getShadowRate() / bandCentre - 1;
        return 0;
    }

    /** A peg's chance of breaking by doubt at the last reprice (0 outside a peg). */
    public double getLastHazard()     { return lastHazard; }
    /** ...and the log of the shadow over the peg then. */
    public double getLastPegGap()     { return lastPegGap; }
    /** The share of the hot money that takes itself home in the coming month: the break chance the last reprice left, when the peg held with the shadow past it (Game hands it to CapitalFlows.setDoubt()). */
    public double getDoubtShare()     { return doubtShare; }
    /** Whether the last reprice broke a peg. */
    public boolean brokeThisMonth()   { return brokeThisMonth; }
    /** Pegs broken since founding. */
    public int getBreaks()            { return breaks; }
    public BreakCause getLastBreakCause() { return lastBreakCause; }
    /** The last break's jump in the rate, in logs, that month. */
    public double getLastBreakJump()  { return lastBreakJump; }
    /** ...and where the shadow stood then, D$ per US$. */
    public double getLastBreakShadow() { return lastBreakShadow; }

    /** The dollars the hold must not let the vault fall under: VAULT_FLOOR months of the trailing import bill at the rate; nothing with no imports. Pure. */
    public double vaultFloorUsd() {
        return importsTrailing > 0 && rate > 0 ? VAULT_FLOOR * importsTrailing / rate : 0;
    }

    /** Where a peg that broke now would land, D$ per US$: the shadow plus OVERSHOOT of its gap again (as the break itself does it); the rate itself outside a peg. Pure. */
    public double breakLanding() {
        if (regime != Regime.PEG || !(pegRate > 0)) return rate;
        double shadow = getShadowRate();
        return shadow * Math.exp(OVERSHOOT * Math.max(0, Math.log(shadow / pegRate)));
    }

    /** How many months the vault lasts at the last reprice's sales, to its floor, under a peg or a band that sold; infinity when it sold nothing. Pure. */
    public double monthsToFloor() {
        if ((regime == Regime.PEG || regime == Regime.BAND) && defenceUsd > 0) {
            return (getReservesUsd() - vaultFloorUsd()) / defenceUsd;
        }
        return Double.POSITIVE_INFINITY;
    }

    /** What a purchase of dollars for this much local money would put on the float's log rate (positive weaker; a sale the other way), at the real size: the interventions' channel. 0 with no GDP. Pure. */
    public double dealEffect(double localPurchase) {
        return gdpTrailing > 0 ? INTERVENTION_IMPACT * localPurchase / (12 * gdpTrailing) : 0;
    }

    /** The dollars the strong edge bought at the last reprice, and their local price at the rate before it. */
    public double getEdgeBoughtUsd()      { return edgeBoughtUsd; }
    public double getEdgeBoughtLocal()    { return edgeBoughtLocal; }
    /** ...since founding, in dollars. */
    public double getEdgeBoughtUsdLifetime() { return edgeBoughtUsdLifetime; }
    /** The local price still owed for them, and by whom: settled at the top of the next month. */
    public double getEdgeOwedLocal()      { return edgeOwedLocal; }
    public EdgePayer getEdgeOwedPayer()   { return edgeOwedPayer; }

    /**
     * The vault takes these dollars at once, at the month's rate before the
     * reprice moves it (as sellToDefend() sells, so revalueVault() stays
     * clean), and the local price is owed to its payer at the top of the next
     * month. Not dealing the next reprice feels: the purchase is the hold.
     */
    private void edgeBuy(double usd) {
        if (!(usd > 0) || !(rate > 0)) return;
        vaultAdd(usd);
        edgeBoughtUsd = usd;
        edgeBoughtLocal = usd * rate;
        edgeOwedLocal += edgeBoughtLocal;
        edgeOwedPayer = edgePayer;
        edgeBoughtUsdLifetime += usd;
    }

    /**
     * Hands over what is owed for the dollars the edge bought, and clears it:
     * called once, at the top of the month inside the audit's window. Read
     * getEdgeOwedPayer() first for who owes it.
     */
    public double takeEdgeOwed() {
        double owed = edgeOwedLocal;
        edgeOwedLocal = 0;
        return owed;
    }

    /**
     * The treasury paid this much of what the edge owed, out of its cash: a
     * reserve purchase of its own, so it joins the month's purchases (and
     * MoneyAudit's "- treasury BoughtReserves" declares it) and the lifetime
     * dealing. NOT the vault, which has the dollars already, and NOT
     * dealtSinceReprice: the edge's purchase is the hold itself, never felt as
     * an intervention.
     */
    public void noteEdgePaidByTreasury(double local) {
        if (!(local > 0)) return;
        boughtThisMonth += local;
        lifetimeIntervention += local;
    }

    /**
     * The central bank paid this much of what the edge owed, in new money
     * (CentralBank.buyDollarsForVault()): the lifetime dealing alone, the
     * mirror of the defence's sales, which it has counted since 0.7.2 (FX8,
     * 2026-10-22; before, the record and Trade's "net bought into the vault"
     * left out every dollar a peg or a band bought on the central bank's
     * money). NOT the month's purchases, which are the treasury's (MoneyAudit
     * declares this as "- centralbank DollarsBoughtAbroad"), NOT the vault,
     * and NOT dealtSinceReprice.
     */
    public void noteEdgePaidByCentralBank(double local) {
        if (!(local > 0)) return;
        lifetimeIntervention += local;
    }

    /* --------------------------- the month's account --------------------------- */

    private double exports;
    private double imports;
    private double foreignInterest;
    private double financialIn;
    private double financialOut;

    /* ------------------------------ the position ------------------------------ */

    /* =======================================================================
       TWO NUMBERS THAT WERE ONE NUMBER
       =======================================================================

       This field used to be both of the things below at once, and the
       conflation surfaced three separate times before it was split:

         - the import-cover figure read 0 for any city whose cumulative
           position was negative (most of them) and 43,892 months for the
           mainline playtest. Neither said anything about anything.
         - the sudden-stop window was gated on that cover, and shut on a
           perfectly healthy city.
         - selling reserves raised cash and the stock came straight back,
           because the same transaction moved "the reserves" twice.

       Each was patched where it showed. Jerus, after the third: "i think youll
       have to split it before continuing, cause its still the case." He is
       right, and each patch was treating a symptom of one wrong idea.

       THE VAULT. What the treasury actually holds in foreign money and could
       spend this afternoon. It cannot go below zero, because you cannot spend
       currency you do not have, and the only things that move it are the
       treasury buying and selling, since 0.7.2 the central bank selling it
       in a currency's defence, and since 0.7.6 land paid for out of it.
       Export earnings do NOT land here - they land
       with the firms that earned them, which is why a country with a trade
       surplus can still run out of reserves. This is what import cover is
       measured against and what a currency defence is fought with.

       THE VAULT IS HELD IN DOLLARS (2026-09-21).

       Jerus, reading his own city's year book - prices 399x founding in 25
       years, the currency at its 100x guard: "i think we should make it so
       that of the 3.5B you start with, 1B is in usd in the reserve ... i think
       that greatly helps, since 99% players wont add to reserves most
       probably cause they have no clue."

       Checking that first found the vault in the wrong currency. It was a
       LOCAL figure at the price paid - buyReserves() added the local cash
       spent, and getReservesUsd() divided by today's rate - so when the
       currency fell a hundredfold a US$1B vault read US$10M, and import
       cover, local over local, shrank a hundredfold exactly when it was
       needed. The dollar DEBT on this same class was revalued every month.
       Dollar debts got dearer when the currency fell and dollar reserves did
       not get more valuable, and a reserve is the one thing that is supposed
       to gain when your currency falls.

       So the stock of truth is the dollars. Everything local - getReserves(),
       what can be sold, the cover, the net position - is those dollars at
       today's rate, and the move from one rate to the next is booked as a
       revaluation line beside the vault (revalueVault()), in the debt's shape.
       ======================================================================= */

    /* =======================================================================
       THE VAULT IN EIGHT (WN4, 2026-10-30; spec-nations 5.2-5.4)

       Jerus, D6: "Own prices, vault in 8". The vault is no longer one figure:
       it keeps a BALANCE IN EACH OF THE EIGHT MONEYS the IMF's COFER names
       (WorldMoneys.EIGHT: the US$, euro, yen, pound, yuan, the Canadian and
       Australian dollars and the Swiss franc), each in its own units, and
       the one US$ figure the vault was kept in since 2026-09-21 became their SUM at
       the day's cross rates: getReservesUsd(). Every reader of the vault (the
       cover, the defence, the floor, the screens) goes on reading that sum.

       WHAT GOES IN, by the RESERVE MIX (D20, taken on Jerus's "do rec"): ALL_USD
       by default, so a city that never touches it plays exactly as before
       (every site's arithmetic on the US$ balance is what it was on the one
       field, to the bit); MATCH_TRADE, the city's trade by money; WORLD_MIX,
       COFER's shares; CHOSEN, eight shares the player sets. Every dollar that
       comes in goes in by the mix - the treasury's purchases, the strong edge's
       and a peg's buys - but the founders' dollars, which are dollars (Game
       names the US$). A money with no rate this month takes no share and its
       share goes to the US$.

       WHAT COMES OUT leaves the LARGEST holding first, by US$ worth, then the
       next: the defence, a band's or a peg's sales, the treasury's sales and
       the land office. Only the player's own sale can name a money
       (sellReserves(local, code)).

       THE CROSS RATES come from Game at the top of the month, right after the
       moneys step (setMoneyRates()), before anything buys or sells; handing them
       REVALUES THE VAULT ACROSS THE MONEYS: crossUsd gains native x (new - old)
       for each, the US$ the vault gained or lost because the eight moved
       against each other. At the month's end revalueVault() books the whole
       month's move in local money exactly - the dollars at the rate's move, plus
       the cross move at the rate the vault was last valued at - and the screens
       read the cross move and the money that made it (getLastVaultCrossUsd(),
       getLastVaultCrossDriver()). A purchase in the month is at the month's
       rates and adds nothing to it.
       ======================================================================= */

    /** The share of the world's reserves each of the eight holds, IMF COFER 2026Q2 [R23], in WorldMoneys.EIGHT's order, normalised when used: the US$ 56.7%, euro 20.6%, yen 5.0%, pound 4.5%, yuan 2.1%, Canadian dollar 2.7%, Australian dollar 2.1%, franc 0.2% (est.: the study gives the four small ones together as about 9.5%). */
    public static final double[] COFER_SHARES = {.567, .206, .050, .045, .021, .027, .021, .002};

    /** How many moneys the vault keeps a balance in. */
    public static final int MONEYS = 8;

    /** The money's name as a sentence has it ("the euro"), in WorldMoneys.EIGHT's order. */
    public static final String[] MONEY_NAME = {"the US dollar", "the euro", "the yen", "the pound", "the yuan", "the Canadian dollar", "the Australian dollar", "the Swiss franc"};

    /** ...and what a purchase of it is ("euros"), for a button's words. */
    public static final String[] MONEY_PLURAL = {"US dollars", "euros", "yen", "pounds", "yuan", "Canadian dollars", "Australian dollars", "Swiss francs"};

    /** ...and the mark a figure in it is written with ("€"), in the eight's order: the same marks the trade screen prints (CD8: the decision log names the money a piece is sold in by it). */
    public static final String[] MONEY_MARK = {"US$", "€", "¥", "£", "CN¥", "C$", "A$", "CHF "};

    /** Which of the eight a code is, or -1 for a code that is none of them (or null). */
    public static int moneyIndex(String code) { return code == null ? -1 : WorldMoneys.EIGHT.indexOf(code); }

    /** Where a dollar that comes into the vault goes: ALL_USD, today's behaviour and the default; MATCH_TRADE, the city's trade by money; WORLD_MIX, COFER's shares; CHOSEN, the player's eight. */
    public enum Mix {
        ALL_USD("All US$", ""), MATCH_TRADE("Match our trade", " in our trade's moneys"), WORLD_MIX("The world's mix", " in the world's mix"),
        CHOSEN("Choose", " in the chosen mix");
        /** The chip's words, and what a decision's line adds when a purchase went in by it ("" for the default). */
        public final String words, inWords;
        Mix(String words, String inWords) {
            this.words = words;
            this.inWords = inWords;
        }
    }

    /** What the vault holds of each money, in that money's own units. */
    private final double[] vault = new double[MONEYS];
    /** US$ to the unit of each, as Game handed them (1 for the US$, 0 until handed), and whether this month has a rate for it. */
    private final double[] usdPer = new double[MONEYS];
    private final boolean[] rated = new boolean[MONEYS];
    private Mix mix = Mix.ALL_USD;
    /** The player's eight shares under CHOSEN, and the city's trade by money under MATCH_TRADE (Game hands it once a month): each sums to 1, the trade's to 0 when there is none. */
    private final double[] chosen = new double[MONEYS];
    private final double[] tradeShares = new double[MONEYS];
    /** The month's move of the vault across the moneys in US$, whether revalueVault() has booked it, and the money that moved it most (its index, its move against the US$ as a fraction, the US$ it moved). */
    private double crossUsd;
    private boolean crossBooked = true;
    private int crossDriver = -1;
    private double crossDriverMove, crossDriverUsd;

    {
        usdPer[0] = 1;
        rated[0] = true;
        chosen[0] = 1;
    }

    /** What the vault holds of one money, in its own units; 0 for an index that is none of the eight. */
    public double getHolding(int k) { return k < 0 || k >= MONEYS ? 0 : vault[k]; }

    /** ...and what that is in US$ at the day's cross rate (the last one handed for a money that has none this month). */
    public double getHoldingUsd(int k) {
        if (k < 0 || k >= MONEYS || vault[k] == 0) return 0;
        return vault[k] * usdPer[k];
    }

    /**
     * The reserve position in dollars - the stock itself: the eight balances at the day's cross rates, summed. The one figure every reader
     * of the vault goes on reading.
     *
     * Since 2026-09-21 this is the figure the vault is KEPT in, not a translation of it: the money is foreign, so its own unit is the one
     * that does not move when the currency does. See THE VAULT IS HELD IN DOLLARS, in the banner TWO NUMBERS THAT WERE ONE NUMBER, and
     * THE VAULT IN EIGHT.
     */
    public double getReservesUsd() {
        double total = vault[0];
        for (int k = 1; k < MONEYS; k++) total += getHoldingUsd(k);
        return total;
    }

    /** US$ to the unit of a money as Game last handed it; NaN for one it has not been handed (and for an index that is none of the eight). */
    public double getMoneyUsd(int k) { return k < 0 || k >= MONEYS || !(usdPer[k] > 0) ? Double.NaN : usdPer[k]; }

    /** Whether the money has a rate this month, so a purchase can go into it: always the US$; the others once Game has handed a rate (a loaded city's are the rates it was saved with). */
    public boolean hasRate(int k) { return k >= 0 && k < MONEYS && rated[k]; }

    /** Hands the eight's US$ per unit, in WorldMoneys.EIGHT's order (the US$'s entry is read as 1): the top of the month, right after the moneys step. A NaN or a rate that is not above zero leaves the money without a rate this month. Revalues the vault across the moneys (crossUsd). */
    public void setMoneyRates(double[] usdPerUnit) {
        if (usdPerUnit == null) return;
        if (crossBooked) {
            crossUsd = 0;
            crossDriver = -1;
            crossDriverMove = crossDriverUsd = 0;
            crossBooked = false;
            // ...and a new month's interest starts from nothing: a month the world does not hand it (earnVaultInterest()) reads 0.
            clearInterest();
        }
        for (int k = 1; k < MONEYS && k < usdPerUnit.length; k++) {
            double now = usdPerUnit[k];
            if (!(now > 0) || !Double.isFinite(now)) {
                rated[k] = false;
                continue;
            }
            double was = usdPer[k];
            if (was > 0 && vault[k] != 0) {
                double move = vault[k] * (now - was);
                crossUsd += move;
                if (Math.abs(move) > crossDriverUsd) {
                    crossDriver = k;
                    crossDriverMove = now / was - 1;
                    crossDriverUsd = Math.abs(move);
                }
            }
            usdPer[k] = now;
            rated[k] = true;
        }
    }

    /** The mix a dollar coming into the vault goes in by. */
    public Mix getReserveMix() { return mix; }

    public void setReserveMix(Mix to) { mix = to == null ? Mix.ALL_USD : to; }

    /** The player's eight shares under CHOSEN (a copy; sums to 1). */
    public double[] getChosenShares() { return chosen.clone(); }

    /** Sets the eight shares under CHOSEN, normalised to sum to 1: a negative or NaN share reads 0, and all zero means all US$. */
    public void setChosenShares(double[] shares) { normaliseInto(chosen, shares, true); }

    /** Moves one money's chosen share by this many points (0.05 is five): the rest give or take it in proportion; with nothing else held, the freed points go to the US$ (to the euro when it is the US$ that was moved). */
    public void shiftChosen(int k, double points) {
        if (k < 0 || k >= MONEYS || !Double.isFinite(points) || points == 0) return;
        double was = chosen[k], now = Math.max(0, Math.min(1, was + points));
        if (now == was) return;
        double others = 1 - was;
        double[] next = new double[MONEYS];
        next[k] = now;
        if (others > 1e-12) {
            double scale = (1 - now) / others;
            for (int j = 0; j < MONEYS; j++) if (j != k) next[j] = chosen[j] * scale;
        } else {
            next[k == 0 ? 1 : 0] = 1 - now;
        }
        setChosenShares(next);
    }

    /** The city's trade by money, the eight's shares Game handed (a copy; sums to 1, or all 0 where the city has traded nothing). */
    public double[] getTradeShares() { return tradeShares.clone(); }

    /** Game's once a month, right after the moneys step: the city's trade by money under MATCH_TRADE, normalised; all zero reads all US$. */
    public void setTradeShares(double[] shares) { normaliseInto(tradeShares, shares, false); }

    /** Fills a share array from a source: finite positive entries kept, the rest 0, scaled to sum to 1; with none, all zero - or the US$ alone for the player's. */
    private static void normaliseInto(double[] into, double[] source, boolean usdIfNone) {
        double sum = 0;
        for (int k = 0; k < MONEYS; k++) {
            double v = source != null && k < source.length && Double.isFinite(source[k]) && source[k] > 0 ? source[k] : 0;
            into[k] = v;
            sum += v;
        }
        if (sum > 0) {
            // Shares already at 100% (to rounding) are left as they are, so a save restored and normalised again reads back to the bit.
            if (Math.abs(sum - 1) > 1e-12) for (int k = 0; k < MONEYS; k++) into[k] /= sum;
        } else if (usdIfNone) {
            into[0] = 1;
        }
    }

    /** The shares a dollar coming in is split by now (sums to 1): the mix's, less the moneys with no rate this month, whose share goes to the US$; all US$ when nothing is left. A new array. */
    public double[] mixShares() {
        double[] w = new double[MONEYS];
        switch (mix) {
            case MATCH_TRADE -> System.arraycopy(tradeShares, 0, w, 0, MONEYS);
            case WORLD_MIX -> System.arraycopy(COFER_SHARES, 0, w, 0, MONEYS);
            case CHOSEN -> System.arraycopy(chosen, 0, w, 0, MONEYS);
            default -> w[0] = 1;
        }
        for (int k = 1; k < MONEYS; k++) {
            if (rated[k]) continue;
            w[0] += w[k];
            w[k] = 0;
        }
        double sum = 0;
        for (double v : w) sum += v;
        if (!(sum > 0)) {
            java.util.Arrays.fill(w, 0);
            w[0] = 1;
            return w;
        }
        for (int k = 0; k < MONEYS; k++) w[k] /= sum;
        return w;
    }

    /** Puts these dollars into the vault by the mix. All US$ is the old vault's "+=", to the bit. */
    private void vaultAdd(double usd) {
        if (mix == Mix.ALL_USD) {
            vault[0] += usd;
            return;
        }
        double[] share = mixShares();
        for (int k = 0; k < MONEYS; k++) if (share[k] > 0) vault[k] += usd * share[k] / usdPer[k];
    }

    /** Puts these dollars into one money's balance; a code that is none of the eight goes by the mix, and a money with no rate this month goes to the US$. */
    private void vaultAddInto(double usd, String code) {
        int k = moneyIndex(code);
        if (k < 0) {
            vaultAdd(usd);
            return;
        }
        if (!rated[k]) k = 0;
        vault[k] += usd / usdPer[k];
    }

    /**
     * Takes these dollars out of the vault from the largest holding by US$ worth, then the next (a tie leaves the one earlier in the eight's
     * order first). A sum that is the whole vault or more empties it exactly, as the old field's did.
     */
    private void vaultRemove(double usd) {
        if (usd >= getReservesUsd()) {
            java.util.Arrays.fill(vault, 0);
            return;
        }
        double left = usd;
        boolean[] drained = new boolean[MONEYS];
        while (left > 0) {
            int at = -1;
            double most = 0;
            for (int k = 0; k < MONEYS; k++) {
                double worth = drained[k] ? 0 : getHoldingUsd(k);
                if (worth > most) {
                    most = worth;
                    at = k;
                }
            }
            if (at < 0) return;
            if (left >= most) {
                vault[at] = 0;
                left -= most;
                drained[at] = true;
            } else {
                vault[at] = Math.max(0, vault[at] - left / usdPer[at]);
                left = 0;
            }
        }
    }

    /** The month's move of the vault across the moneys, in US$: what it gained or lost because the eight moved against each other (the cross rates handed at the top of the month; a purchase after them adds nothing). Kept past revalueVault() until the next month's rates are handed. */
    public double getLastVaultCrossUsd() { return crossUsd; }

    /** The money that moved it most, and how far it moved against the US$ in the month: a fraction of the US$ a unit costs, so -0.091 is a euro down 9.1% against the dollar. */
    public record CrossDriver(String code, int index, double move) { }

    /** ...or null when nothing moved it. */
    public CrossDriver getLastVaultCrossDriver() {
        return crossDriver < 1 || crossDriver >= MONEYS || crossUsd == 0 ? null
                : new CrossDriver(WorldMoneys.EIGHT.get(crossDriver), crossDriver, crossDriverMove);
    }

    /* -----------------------------------------------------------------------
       THE VAULT EARNS (XR4, 2026-10-31; spec-exchange-rates 3.8)

       Jerus, calls 9 and 11: the vault earns each money's rate, as Canada's
       Exchange Fund Account earns for its government - the central bank study
       said "the vault earns nothing", and a reserve manager's balance in the
       euro earns what the euro's central bank pays, in euros.

       THE INTEREST STAYS IN THE VAULT (the call taken, recommended): it is
       reinvested in the money that paid it, compounding month on month, as a
       reserve manager reinvests its coupons, and it reaches the city only when
       the vault sells, as every other dollar of it does. So no D$ is made for
       it, no audit pool moves, it is not on the balance of payments' current
       account (it never meets the D$ market) - and the central bank's equity
       reads it because it reads the vault whole. It is booked at the month's
       cross rates, so it adds nothing to the month's cross move, as a purchase
       at the month's rates adds nothing; the month's revaluation then reads the
       grown vault, as it reads one that bought.
       ----------------------------------------------------------------------- */

    /** What the vault earned this month and since founding, in US$ at the month's cross rates (negative where a bank's rate was below zero). */
    private double interestUsd, interestUsdLifetime;
    /** The money that earned (or lost) the most this month by US$ (an index, -1 for none) and the US$ it did so, signed. */
    private int interestDriver = -1;
    private double interestDriverUsd;
    /** The US$ the vault had in the moneys that earned this month, before the interest: what the month's yield is struck on. */
    private double interestBaseUsd;

    /**
     * The month's interest on the vault's eight balances, each at its central bank's rate (annual, a fraction; WorldMoneys.EIGHT's order):
     * balance x rate / 12 in the money's own units, kept in the vault. A negative rate takes from its balance, never below zero; a money with
     * no rate this month (hasRate()) or no finite policy rate earns nothing. Game hands it once a month, right after setMoneyRates() and before
     * anything buys or sells. A call replaces the month's figures; a month it is not called (no world) reads 0 from the next setMoneyRates().
     */
    public void earnVaultInterest(double[] annualRates) {
        clearInterest();
        if (annualRates == null) return;
        for (int k = 0; k < MONEYS; k++) {
            if (!rated[k] || k >= annualRates.length || !Double.isFinite(annualRates[k]) || !(vault[k] > 0)) continue;
            double before = vault[k] * usdPer[k];
            double earned = Math.max(-vault[k], vault[k] * annualRates[k] / 12);
            interestBaseUsd += before;
            if (earned == 0) continue;
            vault[k] += earned;
            double usd = earned * usdPer[k];
            interestUsd += usd;
            if (Math.abs(usd) > Math.abs(interestDriverUsd)) {
                interestDriver = k;
                interestDriverUsd = usd;
            }
        }
        interestUsdLifetime += interestUsd;
    }

    private void clearInterest() {
        interestUsd = interestDriverUsd = interestBaseUsd = 0;
        interestDriver = -1;
    }

    /** What the vault earned this month, in US$ at the month's cross rates (0 in a month the world did not hand it). Negative where the banks' rates were below zero. */
    public double getVaultInterestUsd() { return interestUsd; }

    /** ...and since founding. */
    public double getVaultInterestUsdLifetime() { return interestUsdLifetime; }

    /** The month's interest in local money, at this month's rate as getReserves() converts. */
    public double getVaultInterest() { return interestUsd * rate; }

    /** The money that earned (or lost) the most this month by US$, as an index into WorldMoneys.EIGHT; -1 where nothing was earned. */
    public int getVaultInterestDriver() { return interestDriver; }

    /** ...and the US$ it earned, signed (0 where there is no driver). */
    public double getVaultInterestDriverUsd() { return interestDriverUsd; }

    /** The vault's US$-weighted mean rate this month, a fraction a year: the month's interest times twelve over the US$ the earning balances were worth; 0 for an empty vault or a month nothing was earned. */
    public double getVaultYield() { return interestBaseUsd > 0 ? 12 * interestUsd / interestBaseUsd : 0; }

    /**
     * THE RECORD. Every month's balance of payments, added up since founding.
     *
     * Not a stock of anything - nobody can spend it - but the answer to "has
     * this city earned its living from the world or lived off it", which is a
     * different and equally real question. Negative means the city has taken
     * more from the world than it has sent out, cumulatively. It can be as
     * negative as it likes; that is not a bug and not a crisis, it is a
     * description.
     */
    private double cumulativeBalance;
    private double forgiven;

    /* Trailing means, for anything the rate is decided on. See balanceTarget(). */
    private double exportsTrailing;
    private double currentTrailing;
    /**
     * The financial account, trailing, and the gross of it - what crossed in
     * either direction - so the rate can be struck on the OVERALL balance, and
     * gate() can tell a city that trades from one that does not. Since
     * 2026-09-10; see balanceTarget().
     */
    private double financialTrailing;
    private double financialGrossTrailing;

    /*
     * SINCE FOUNDING. Cheap to keep, and the only way to say anything about a
     * city's trade rather than about the month it happens to be in - one month's
     * exports depend on whether a mill was staffed that week.
     *
     * They also give the cumulative balance a checkable definition: it is
     * exactly lifetime exports, less imports, less interest paid abroad, plus
     * capital received. ForeignCheck asserts that, which is what makes the
     * stock and the flows one set of books rather than two. (Not the vault,
     * which is bought and sold rather than accumulated - see
     * balanceFromFlows().)
     */
    private double lifetimeExports;
    private double lifetimeImports;
    private double lifetimeInterest;
    private double lifetimeFinancial;
    private double importsTrailing;
    private int monthsOfHistory;

    /**
     * How many months the trailing figures are averaged over: the import bill
     * the cover is measured against, and the balances the float reads.
     *
     * Measured against a TRAILING AVERAGE of imports rather than last month's,
     * so a single month with no construction going on does not report the city
     * as spectacularly well covered. A rolling mean rather than a window of
     * months (see takeMonth()), so once it has settled each new month counts
     * for one part in this many.
     */
    public static final double COVER_WINDOW = 12;

    /**
     * Months of imports the reserve would cover.
     *
     * THE HEADLINE NUMBER, and the one the vault's damping of the rate is
     * struck off (absorption()). It is the standard yardstick for reserve
     * adequacy - three months is the usual rule of thumb - and it is far more
     * legible than the raw figure, because "forty million" means nothing
     * without knowing what the city spends abroad.
     */
    public double importCover() {
        // An empty vault is no cover - never negative cover, and not endless
        // cover on a city with no import bill yet.
        if (getReservesUsd() <= 0) return 0;
        if (importsTrailing <= 0) return Double.MAX_VALUE;
        /*
         * The vault at TODAY's rate over the import bill in today's money, so
         * a falling currency moves both halves together and the cover does not
         * move with it. Imports are dollar goods; so is the vault. It used to
         * be the vault at the price paid over today's bill, and fell exactly
         * as fast as the currency did.
         */
        return getReservesUsd() * rate / importsTrailing;
    }

    /** The trailing monthly import bill the cover is measured against. */
    public double monthlyImports() { return importsTrailing; }

    /** ...and the trailing figures the rate is decided on. */
    public double monthlyExports()       { return exportsTrailing; }
    public double monthlyCurrentAccount(){ return currentTrailing; }
    /** The financial account, trailing: positive is money coming in. See balanceTarget(). */
    public double monthlyFinancialAccount(){ return financialTrailing; }

    /**
     * How much of the economy actually crosses the border.
     *
     * Trade against GDP: how much the economy cares about the rate (since
     * XR1, gate() scales the market and the fundamentals by it up to
     * OPEN_ENOUGH). It was the missing half of the old pressure signal: a
     * deficit as a share of trade says how BAD an imbalance is, not how
     * much the economy cares, and a city with no exports sat at the maximum
     * reading for ever - its deficit 100% of its trade whether that trade was
     * a tenth of the economy or all of it - and the currency fell at a fixed
     * rate until it hit the floor.
     *
     * Measured: a residential city pinned at 0.95 pressure for twenty years,
     * devaluing 8x while its trade was a fifth of its output.
     */
    private double openness;

    public double getOpenness() { return openness; }

    public void takeMonth(MoneyAudit.Result month) {
        takeMonth(month, 0);
    }

    /**
     * Takes the month off the audit that has just been struck.
     *
     * Fed the Result rather than the Game, because the Result is the thing that
     * has already been reconciled - reading the underlying getters a second time
     * would be a second definition of the same month, and this codebase has been
     * caught by that four times.
     *
     * @param monthlyGdp the city's output, for the openness weighting above.
     */
    public void takeMonth(MoneyAudit.Result month, double monthlyGdp) {
        if (month == null) return;
        monthCounted = true;
        if (monthlyGdp > 0) {
            double trade = exportsTrailing + importsTrailing;
            openness = Math.max(0, Math.min(1, trade / monthlyGdp));
        }

        exports = month.tradeIn;
        imports = month.tradeOut;
        foreignInterest = month.incomeOut - month.incomeIn;
        financialIn = month.financialIn;
        financialOut = month.financialOut;
        lastValuation = month.valuationChange();

        /*
         * VALUATION CHANGES ARE NOT RESERVE FLOWS. A failed bank's foreign
         * creditors absorbing its hole - until 0.7.14; since then a
         * resolution wiping out the world's shares in it - leaves the city
         * better off against the world without anybody having earned a
         * dollar, so it improves the position and does not fill the vault.
         * Left in, the reserve swung from
         * -$88M to +$50M on bank resolutions alone and said nothing at all about
         * whether the city was selling more than it was buying.
         */
        cumulativeBalance += month.currentAccount() + month.financialAccount();
        forgiven += month.valuationChange();

        lifetimeExports += month.tradeIn;
        lifetimeImports += month.tradeOut;
        lifetimeInterest += month.incomeOut - month.incomeIn;
        lifetimeFinancial += month.financialAccount();

        // A rolling mean, kept as a mean rather than as a window of months: one
        // number, no array to carry in the save, and it settles within a year.
        double weight = Math.min(monthsOfHistory, COVER_WINDOW - 1);
        // ...and the output, the same mean (XR1): what the balance and the
        // interventions are weighed against. A city that has not reported a
        // GDP yet opens the mean on its first.
        if (monthlyGdp > 0) gdpTrailing = gdpTrailing > 0 ? (gdpTrailing * weight + monthlyGdp) / (weight + 1) : monthlyGdp;
        importsTrailing = (importsTrailing * weight + tradeImports()) / (weight + 1);
        exportsTrailing = (exportsTrailing * weight + exports) / (weight + 1);
        currentTrailing = (currentTrailing * weight + currentAccount()) / (weight + 1);
        financialTrailing = (financialTrailing * weight + month.financialAccount()) / (weight + 1);
        financialGrossTrailing = (financialGrossTrailing * weight
                + Math.abs(month.financialIn) + Math.abs(month.financialOut)) / (weight + 1);
        monthsOfHistory++;
    }

    /* ------------------------------- reading ------------------------------- */

    /* =======================================================================
       WHAT THE CITY OWES ABROAD
       =======================================================================

       READ, NOT ACCUMULATED, and that is deliberate. Every other stock on this
       class is built up flow by flow because there is nowhere else for it to
       live; this one has a definitive source - the bonds themselves - so it is
       taken from them each month rather than tracked in parallel. Two records
       of the same debt is two records that can disagree, and the one that is
       merely convenient is always the one that is wrong.

       THE REVALUATION IS NOT A CASH FLOW. A currency that falls 10% makes the
       city's dollar debt 10% dearer without anybody paying a cent, so it does
       not appear in MoneyAudit's inflows or outflows at all - it would break
       the identity, and rightly, because no money moved. It changes the
       POSITION, which is what a valuation change is.

       Computed off the USD stock and the rate change rather than off the
       month-on-month local value, because that local value also moves when the
       city borrows or repays, and a figure captioned "what the currency did to
       your debt" must not include what the treasury did to it.
       ======================================================================= */

    private double foreignDebt;         // local value of USD paper outstanding
    private double foreignDebtUsd;      // ...and in the dollars it is owed in
    private double lastDebtRate = OPENING_RATE;
    private double lastRevaluation;
    private double lifetimeRevaluation;

    /**
     * @param usdOutstanding principal still owed abroad, in dollars
     * @param rateNow        local currency per USD, this month
     */
    public void takeForeignDebt(double usdOutstanding, double rateNow) {
        if (rateNow <= 0) return;
        lastRevaluation = usdOutstanding * (rateNow - lastDebtRate);
        lifetimeRevaluation += lastRevaluation;
        lastDebtRate = rateNow;
        foreignDebtUsd = usdOutstanding;
        foreignDebt = usdOutstanding * rateNow;
    }

    /** What the city owes abroad, in local money at today's rate. */
    public double getForeignDebt()    { return foreignDebt; }
    public double getForeignDebtUsd() { return foreignDebtUsd; }

    /** What the currency did to that debt this month. Positive means dearer. */
    public double getLastRevaluation()     { return lastRevaluation; }
    public double getLifetimeRevaluation() { return lifetimeRevaluation; }

    /**
     * Claims abroad less what is owed abroad: the city's net position.
     *
     * The figure a country is actually judged on. A city with deep reserves and
     * deeper dollar debt is not rich, and the reserve line on its own says it
     * is. Both halves in local money at today's rate: the vault's dollars and
     * the debt's, which is what makes a falling currency move them together.
     */
    public double netForeignPosition() { return getReserves() - foreignDebt; }

    /** Goods and services sold abroad. */
    public double getExports() { return exports; }

    /** Goods bought abroad, interest excluded. */
    public double tradeImports() { return imports; }

    /** Interest paid to foreign creditors. */
    public double getForeignInterest() { return foreignInterest; }

    /** Exports less imports. The visible balance. */
    public double tradeBalance() { return exports - imports; }

    /** ...less what was paid to foreign lenders. */
    public double currentAccount() { return tradeBalance() - foreignInterest; }

    public double getFinancialIn()  { return financialIn; }
    public double getFinancialOut() { return financialOut; }
    public double financialAccount() { return financialIn - financialOut; }

    /** The month's change in the city's foreign position. */
    public double balance() { return currentAccount() + financialAccount(); }

    /**
     * What the treasury holds in foreign money, and could spend today - in
     * local money, at today's rate. The dollars are getReservesUsd().
     */
    public double getReserves() { return getReservesUsd() * rate; }

    /** Every month's balance of payments since founding, added up. */
    public double getCumulativeBalance() { return cumulativeBalance; }

    /**
     * Whether the city owes the world more than it holds there.
     *
     * On the NET POSITION, not on the cumulative balance. A city can have run a
     * cumulative deficit for thirty years and owe nobody anything - it simply
     * bought more than it sold - while a city with a fat cumulative surplus and
     * a mountain of dollar bonds is the one actually in trouble.
     */
    public boolean inDeficit() { return netForeignPosition() < 0; }

    /** Claims the world has written off, cumulatively. Not part of the reserve. */
    public double getForgiven() { return forgiven; }

    /**
     * Debt the city refused to pay, and its creditors will not see again.
     *
     * It improves the position without a dollar being earned, which is exactly
     * what the valuation line is for - and exactly why a default flatters every
     * ratio for a year and costs the city the window for five.
     */
    public void forgiveDebt(double localAmount) {
        if (localAmount <= 0) return;
        forgiven += localAmount;
        repudiated += localAmount;
    }

    private double repudiated;

    /** What the city has walked away from, since founding. */
    public double getRepudiated() { return repudiated; }

    /* ==================== BUYING AND SELLING THE RESERVE ====================
     *
     * The treasury can add to the city's foreign position by buying currency
     * with local money, and draw it down by selling. Both are REAL: local cash
     * leaves the city to buy foreign money and arrives when it is sold, so both
     * cross the audit's edge and are declared on the financial account.
     *
     * Which makes reserve policy a fiscal decision rather than a free lever.
     * Stacking reserves competes with building a school out of the same dollar,
     * and defending the currency by selling them is money the player watches
     * leave - including in the case where the defence fails anyway, which is the
     * classic way a country loses its reserves and its peg in the same year.
     */

    private double boughtThisMonth;
    private double soldThisMonth;

    /**
     * Clears the month's intervention. Called at the top of the month - so
     * a purchase made between two presses, as the player makes one, is
     * cleared here before any reprice has read it; the rate reads what was
     * dealt since the last reprice instead (dealtSinceReprice), which this
     * leaves alone.
     */
    public void startMonth() {
        boughtThisMonth = 0;
        soldThisMonth = 0;
    }

    /**
     * Forgets what has been dealt since the last reprice: the founders'
     * dollars (Game.buildWorld()), an endowment the market never saw bought,
     * and a fixture's vault, set up as a holding rather than a purchase under
     * test. The vault and the books keep the purchase; only the rate forgets it.
     */
    public void forgetDealing() { dealtSinceReprice = 0; }

    /** What the treasury has dealt since the last reprice, local money, a purchase of dollars positive: what the next reprice feels (THE FLOAT, step 5). */
    public double getDealtSinceReprice() { return dealtSinceReprice; }

    /**
     * Local currency spent buying foreign money. Raises the stock.
     *
     * BOOKED AGAINST lifetimeIntervention, NOT lifetimeFinancial, and that is
     * the fix rather than a tidy-up. An intervention used to be counted as a
     * financial-account flow in two places at once: here, directly, and again
     * in takeMonth() via financialAccount(). The two cancelled, so buying
     * reserves cost the treasury real cash and built nothing at all.
     *
     * THE DOLLARS ARE WHAT IS KEPT, at the rate of the day (2026-09-21); the
     * two bookkeeping figures stay in local money, because the local cash is
     * what crossed the audit's edge and what the treasury gave up for them.
     */
    public void buyReserves(double localAmount) { buyReserves(localAmount, null); }

    /**
     * ...into one money of the eight, the player's named purchase (WN4, THE VAULT IN EIGHT): a code that is none of them, or null, goes by
     * the mix; a money with no rate this month, to the US$. The founders' dollars are bought into "USD", whatever the mix.
     */
    public void buyReserves(double localAmount, String code) {
        if (localAmount <= 0 || rate <= 0) return;
        vaultAddInto(localAmount / rate, code);
        boughtThisMonth += localAmount;
        dealtSinceReprice += localAmount;
        lifetimeIntervention += localAmount;
    }

    /**
     * Foreign money sold for local currency. Lowers the stock.
     *
     * THE CLAMP IS THE POINT, and it is why this returns a figure rather than
     * void: you cannot sell what you do not hold. A city with $6,000 of
     * reserves that asks to sell $10,000 sells $6,000, and the remaining
     * $4,000 has to come from somewhere that actually has it - taxes, a
     * domestic bond, or borrowing abroad. Jerus: "if you wanna sell more then
     * youd have to borrow or something". That is exactly right, and it is what
     * a finance ministry with empty reserves has to do.
     *
     * The clamp was always here. What was missing is that it MEANT anything:
     * the same sale was added back to the stock by takeMonth() a moment later,
     * so the reserves never actually ran out and the ceiling never bound.
     *
     * The clamp is against what the dollars fetch TODAY (2026-09-21), which is
     * the whole point of holding them in dollars: sold after the currency has
     * halved, the same dollars raise twice the local cash they cost. Selling
     * everything empties the vault exactly rather than leaving the rounding of
     * a division behind in it.
     *
     * @return what was actually sold, which is never more than the city had
     */
    public double sellReserves(double localAmount) {
        double held = sellableReserves();
        double sold = Math.max(0, Math.min(localAmount, held));
        if (sold <= 0 || rate <= 0) return 0;
        if (sold >= held) java.util.Arrays.fill(vault, 0);
        else vaultRemove(sold / rate);
        soldThisMonth += sold;
        dealtSinceReprice -= sold;
        lifetimeIntervention -= sold;
        return sold;
    }

    /**
     * ...out of one money of the eight, the player's named sale (WN4, THE VAULT IN EIGHT): the clamp is what that money alone holds at
     * today's rate, and no other money gives up anything. A code that is none of the eight sells from the largest, as sellReserves(local) does.
     */
    public double sellReserves(double localAmount, String code) {
        int k = moneyIndex(code);
        if (k < 0) return sellReserves(localAmount);
        double held = sellableReserves(code);
        double sold = Math.max(0, Math.min(localAmount, held));
        if (sold <= 0 || rate <= 0) return 0;
        vault[k] = sold >= held ? 0 : Math.max(0, vault[k] - sold / rate / usdPer[k]);
        soldThisMonth += sold;
        dealtSinceReprice -= sold;
        lifetimeIntervention -= sold;
        return sold;
    }

    /** The most the treasury could sell right now, in local money at today's rate. */
    public double sellableReserves() { return Math.max(0, getReserves()); }

    /** ...of one money of the eight alone (0 for a code that is none of them). */
    public double sellableReserves(String code) { return Math.max(0, getHoldingUsd(moneyIndex(code)) * rate); }

    /* ================== THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) ==================

       Jerus: "when you buy land, make it so that it costs USD not domestic
       currency, and basically how it would work is a little toggle at the top
       to choose, when you buy land, to use up your USD reserves or to convert
       cash into usd exactly to buy the land, and the default is that you
       convert." The seller is the world, and these are the two ways the world
       is paid (Game.buyLandParcel() chooses, off Game.isLandPaidFromVault()):

       CONVERTING, the default: the treasury buys exactly the dollars the
       parcel costs at today's rate and hands them straight to the seller, in
       one movement - buyAndSpendDollarsForLand(). The local cash leaves the
       city (Game pays it through TreasuryLine.LAND, the land line it always
       went out on); the vault ends where it began. The two intervention
       figures a reserve purchase moves - boughtThisMonth and
       lifetimeIntervention - net to nothing on it, because the dollars were
       bought and spent in the same breath, and boughtThisMonth is the only
       way a treasury purchase of dollars touches the rate (the interventions
       felt, THE FLOAT): a reserve transaction is the financing item
       (MoneyAudit, Scope.RESERVE), so the balance and monthDeficitUsd() read
       neither. Converting for land therefore puts no push on the rate - where
       a reserve purchase of the same dollars cheapens the D$ at the real size
       (INTERVENTION_IMPACT) - and the other difference is that the reserve
       purchase leaves the dollars in the vault, where absorption() can see
       them. ForeignCheck asserts both halves.

       FROM THE VAULT: the dollars leave the vault and no local money moves
       - spendReservesOnLand(), a capital transaction like the defence's
       (sellToDefend()): the vault falls, lifetimeIntervention falls by their
       local price as a sale's would (the vault is still "what the treasury
       chose to buy and has not yet sold"), no pool moves and the audit
       declares nothing. A vault too short for the parcel spends what it has
       and the caller converts the rest.

       THE MONTH IS THE BUDGET'S, press to press: what the land cost in
       dollars accumulates as it is bought and is struck by strikeLandMonth()
       where the government's books strike the land's local cost
       (Game.strikeGovernmentBooks()), so the Exchange page and the Government
       tab's land line read the same month as the budget's "Land bought".
       ========================================================================= */

    /** The month in progress: dollars paid for land, and the part of them that came out of the vault. */
    private double landUsdOpen, landVaultUsdOpen;
    /**
     * ...and their local price on the day, all of it and the vault's part: the
     * part of the budget's land line bought abroad (the rest of that line is
     * plots bought back from businesses, in local money), and the part of it
     * no cash paid.
     */
    private double landLocalOpen, landVaultLocalOpen;
    /** The month struck - what the screens show between two presses. Saved (slots 31-32, 35-36). */
    private double landUsd, landVaultUsd, landLocal, landVaultLocal;
    /** ...and since founding (slots 33-34). */
    private double landUsdLifetime, landVaultUsdLifetime;

    /**
     * Buys exactly these dollars at today's rate and pays them to the land's
     * seller, in one movement: the vault is untouched, and nothing a reserve
     * purchase would leave behind is left (see THE LAND OFFICE IS PAID IN
     * DOLLARS). The caller pays the local cash.
     *
     * @return the local money the dollars cost today
     */
    public double buyAndSpendDollarsForLand(double usd) {
        if (!(usd > 0) || !Double.isFinite(usd) || rate <= 0) return 0;
        landUsdOpen += usd;
        landUsdLifetime += usd;
        landLocalOpen += usd * rate;
        return usd * rate;
    }

    /**
     * Pays the land's seller out of the vault: at most what it holds, and
     * everything it holds empties it exactly. No local money moves.
     *
     * @return the dollars actually paid out of the vault; the caller converts
     *         whatever this falls short of
     */
    public double spendReservesOnLand(double usd) {
        double held = getReservesUsd();
        if (!(usd > 0) || !Double.isFinite(usd) || held <= 0 || rate <= 0) return 0;
        double spent = Math.min(usd, held);
        vaultRemove(spent);
        lifetimeIntervention -= spent * rate;
        landUsdOpen += spent;
        landVaultUsdOpen += spent;
        landLocalOpen += spent * rate;
        landVaultLocalOpen += spent * rate;
        landUsdLifetime += spent;
        landVaultUsdLifetime += spent;
        return spent;
    }

    /** Strikes the land's month where the government's books strike its local cost. */
    public void strikeLandMonth() {
        landUsd = landUsdOpen;
        landVaultUsd = landVaultUsdOpen;
        landLocal = landLocalOpen;
        landVaultLocal = landVaultLocalOpen;
        landUsdOpen = landVaultUsdOpen = landLocalOpen = landVaultLocalOpen = 0;
    }

    /** Dollars paid for land in the month last struck, both ways. */
    public double getLandUsdThisMonth()          { return landUsd; }
    /** ...of which out of the vault. */
    public double getLandUsdFromVaultThisMonth() { return landVaultUsd; }
    /** What the month's land from abroad cost in local money on the day, both ways - the budget's land line less the buybacks. */
    public double getLandLocalThisMonth()          { return landLocal; }
    /** ...and the vault's part of it - the part of the budget's land line no cash paid. */
    public double getLandLocalFromVaultThisMonth() { return landVaultLocal; }
    /** Dollars paid for land since founding, both ways. */
    public double getLandUsdLifetime()           { return landUsdLifetime; }
    /** ...of which out of the vault. */
    public double getLandUsdFromVaultLifetime()  { return landVaultUsdLifetime; }
    /** Dollars paid for land since the last strike - the month in progress. */
    public double getLandUsdPending()            { return landUsdOpen; }

    /* ------------------- what the currency did to the vault -------------------
     *
     * THE DEBT'S SHAPE, mirrored rather than reinvented: the dollar stock
     * times the move in the rate since the vault was last valued, struck once
     * a month after the reprice, beside takeForeignDebt(). Positive when the
     * currency fell and the same dollars are worth more at home.
     *
     * NOT CASH AND NOT AN AUDIT FLOW. Nobody can spend it until the dollars
     * are sold, and a sale is the only thing that crosses the audit's edge -
     * at that day's rate, through sellReserves(). MoneyAudit never sees this
     * line, exactly as it never sees the debt's.
     *
     * Struck off the rate the vault was LAST VALUED at rather than off the
     * change in its local value, for the reason the debt's is: the local value
     * also moves when the treasury buys or sells, and a figure captioned "what
     * the currency did to the vault" must not include what the treasury did.
     * Within a month the rate does not move - only the reprice moves it - so
     * every purchase and sale happens at the rate the vault was last valued
     * at and none of them leaks into the line.
     */
    private double lastVaultRate = OPENING_RATE;
    private double lastVaultRevaluation;

    /**
     * Values the vault at today's rate. Called once a month, after the reprice.
     *
     * THE WHOLE MONTH'S MOVE IN LOCAL MONEY, exactly (WN4): the vault's dollars at the rate's move, plus the cross move the eight made
     * against each other (the US$ the vault gained or lost, booked at the rate the vault was last valued at) - V1 r1 - (V1 - cross) r0.
     * The cross term is added only when there is one to book, so an all-US$ vault's figure is the old one to the bit.
     */
    public void revalueVault() {
        if (rate <= 0) return;
        double moved = getReservesUsd() * (rate - lastVaultRate);
        if (!crossBooked && crossUsd != 0) moved += crossUsd * lastVaultRate;
        crossBooked = true;
        lastVaultRevaluation = moved;
        lastVaultRate = rate;
    }

    /** What the currency did to the vault this month, in local money. Positive means worth more. */
    public double getLastVaultRevaluation() { return lastVaultRevaluation; }

    public double getBoughtThisMonth() { return boughtThisMonth; }
    public double getSoldThisMonth()   { return soldThisMonth; }

    public double getLifetimeExports()   { return lifetimeExports; }
    public double getLifetimeImports()   { return lifetimeImports; }
    public double getLifetimeInterest()  { return lifetimeInterest; }
    public double getLifetimeFinancial() { return lifetimeFinancial; }

    /**
     * The cumulative balance, rebuilt from the flows that made it.
     *
     * The stock-equals-flows identity, asserted every month by ForeignCheck.
     * NOTE what it is an identity ON: the cumulative BALANCE, not the vault.
     * The vault is not an accumulation of flows at all - it is what the
     * treasury chose to buy and has not yet sold, which is a decision rather
     * than a residual. That is exactly the distinction this class spent three
     * bugs failing to make.
     */
    public double balanceFromFlows() {
        return lifetimeExports - lifetimeImports - lifetimeInterest + lifetimeFinancial;
    }

    /**
     * What has been bought into the vault less what has been taken out of it,
     * since founding, in the local money each cost or fetched: the treasury's
     * purchases and sales, the defence's sales (0.7.2), the land office's
     * dollars, and the strong edge's purchases by whichever paid for them, the
     * treasury out of its cash or the central bank in new money (FX8,
     * 2026-10-22: until then the record said "what the treasury has bought and
     * sold on its own account" and left the central bank's purchases out,
     * though it counted the central bank's sales).
     *
     * Kept separate from lifetimeFinancial because an intervention is not a
     * financial-account flow - it is the financing item below the line. Adding
     * it here rather than there is what lets balanceFromFlows() still equal the
     * cumulative balance while takeMonth() leaves the stock alone.
     */
    private double lifetimeIntervention;

    public double getLifetimeIntervention() { return lifetimeIntervention; }

    /** This month's part of that. */
    public double valuationChange() { return lastValuation; }

    private double lastValuation;

    /* ================ WHAT THE TRADE TAB READS (0.7.35) ================
     *
     * Pure reads for the Trade tab's redesign (the project's
     * spec-trade-0734.md): nothing here is read by the month (but THIN_COVER,
     * since XR1), and nothing here writes. The tab drew several of these as
     * its own arithmetic - the cover a purchase would buy, three parity
     * verdicts for one rate - and the screens' rule is that a figure is a
     * model read. The float's own reads (gate(), balanceTarget(),
     * coverTarget(), getFairLevel(), the six moves) are under THE FLOAT.
     */

    /**
     * Whether the month's flows are the month's: false only after a founding
     * or a load of an older save.
     *
     * The month's flows - exports, imports, the income and the capital
     * across the edge and the valuation change, struck by takeMonth() and
     * live until the next one, and the treasury's purchases and sales,
     * booked as they are made and cleared by startMonth() - were not saved
     * until 0.7.46, so a city loaded between two months read all of them as
     * nothing while its businesses' saved books said it sold hundreds of
     * millions abroad. The Trade tab reads this and says "not counted yet
     * since the load" until the month turns, rather than printing those
     * zeros as the month's figures (0.7.30's rule for a word the month has
     * not said yet). Since 0.7.46 the save carries the flows (slots 37-44,
     * the Trade spec's D4; A2 in the model fixes) and a load of it counts
     * the month; a founding, or a save from before them, still has none.
     * Not saved itself: the slots' presence is the flag.
     */
    private boolean monthCounted;

    /** True once the month's flows are the month's - a month taken since the founding, or a load of a save that carried them - and not nothing. */
    public boolean isMonthCounted() { return monthCounted; }

    /** Exports less imports since founding: the record on trade alone, without the income or the capital. */
    public double getLifetimeTradeBalance() { return lifetimeExports - lifetimeImports; }

    /**
     * How far from parity, either side, the rate reads as a watch - amber on
     * the Trade tab, the drawer's THE CURRENCY row and the header's rate
     * line, which until 0.7.35 judged one rate three ways (the spec's B9):
     * the drawer's own bands, kept. Since 0.7.38 the drawer's TRADE "vs
     * parity" line too, which read amber past 15% until then.
     */
    public static final double PARITY_WATCH = .25;

    /** ...and how far reads as far: red on all four. */
    public static final double PARITY_FAR = .50;

    /** The verdict on a deviation from parity (deviationFromParity()), either side: 0 near, 1 at PARITY_WATCH or more, 2 at PARITY_FAR or more. */
    public static int parityLevel(double deviation) {
        double far = Math.abs(deviation);
        if (!(far < PARITY_FAR)) return Double.isNaN(far) ? 0 : 2;
        return far >= PARITY_WATCH ? 1 : 0;
    }

    /**
     * Months of import cover under which the world prices a currency for a
     * crisis rather than on its trade balance - the Trade tab's red line and
     * its alert, three months by the usual rule of thumb - and since XR1 the
     * cover at which the risk premium is gone (coverTarget(), RISK_PREMIUM).
     * The defence reads COMFORTABLE_COVER (absorption()).
     */
    public static final double THIN_COVER = 3;

    /** The verdict on months of cover: 2 under THIN_COVER, 1 under COMFORTABLE_COVER, 0 at or over it. */
    public static int coverLevel(double months) {
        if (!(months >= THIN_COVER)) return 2;
        return months < COMFORTABLE_COVER ? 1 : 0;
    }

    /**
     * The cover the vault would give with this much local money's worth of
     * dollars bought into it at today's rate (negative: sold out of it) -
     * the Exchange card's "what it would do". importCover()'s rule: an empty
     * vault is no cover, and no import bill is endless cover.
     */
    public double coverWith(double localChange) {
        double after = getReserves() + localChange;
        if (after <= 0) return 0;
        if (importsTrailing <= 0) return Double.MAX_VALUE;
        return after / importsTrailing;
    }

    /** What it would cost in local money, at today's rate, to bring the vault up to this many months of the trailing import bill; nothing when it is there already. */
    public double toCover(double months) {
        return Math.max(0, months * importsTrailing - getReserves());
    }

    /* ------------------------------- carrying ------------------------------- */

    /*
     * A STOCK, so it is saved. Everything else here is this month's flow and is
     * restruck on the first tick - except the trailing import bill, which is an
     * average of months that have gone and cannot be recovered from the month
     * the save was taken in.
     *
     * That was the first four slots. The array has grown since, and the
     * comments inside it say why most of the later ones are carried - some of
     * them flows after all, the ones a screen shows before the first tick.
     */
    public double[] toSaveArray() {
        return new double[] { cumulativeBalance, importsTrailing, monthsOfHistory, rate, forgiven,
                lifetimeExports, lifetimeImports, lifetimeInterest, lifetimeFinancial,
                exportsTrailing, currentTrailing,
                /*
                 * AND THE THREE THAT LOOK DERIVED BUT ARE NOT RECOVERABLE.
                 *
                 * openness, the pressure and the absorption are all restruck at
                 * the end of a month, from figures that include the month that
                 * just ran. A city loaded on the first of the month has not had
                 * one yet, so leaving them out meant the trade panel opened at
                 * 0% pressure on a city running a chronic deficit, and the rate
                 * about to move told the player nothing was pushing it.
                 *
                 * A flow cannot be reconstructed from the state a month ended
                 * in - the ninth or tenth time this file has been the one to
                 * say so.
                 *
                 * SLOT 12 WAS THE PRESSURE, retired by XR1: it is written as 0
                 * and read by nobody, and the slots after it keep their places.
                 * The month's moves by force (slots 62-67) are what the screens
                 * show of it now.
                 */
                openness, 0, lastAbsorption,
                /*
                 * AND THE RATE THE DEBT WAS LAST VALUED AT.
                 *
                 * Without it a reload compares this month's rate against 1.00
                 * and books the city's entire currency history as one month's
                 * revaluation. The lifetime figure goes with it for the same
                 * reason every other lifetime total is carried.
                 */
                lastDebtRate, lifetimeRevaluation, repudiated, lifetimeIntervention, parity,
                /*
                 * THE VAULT, saved separately since the two were split. Slot 19,
                 * so an older save restores its single number into the
                 * cumulative balance (slot 0) and starts the vault empty - which
                 * is the right reading of a save written when the two were one
                 * number and only the balance behaviour was ever real.
                 *
                 * STILL THE LOCAL VALUE, at the moment of saving, now that the
                 * vault is kept in dollars (2026-09-21): slot 19 keeps its
                 * meaning, so an older build reading this save sees exactly
                 * the figure it always saw. The dollars ride the end, slot 22.
                 */
                getReserves(),
                /*
                 * The financial account's two trailing figures, slots 20 and
                 * 21, since the rate was struck on the overall balance.
                 * Absent from an older save: both start at zero and settle
                 * within a year, as the current one always did.
                 */
                financialTrailing, financialGrossTrailing,
                /*
                 * THE VAULT IN DOLLARS, slot 22 (2026-09-21) - the stock of
                 * truth. An older save has no slot 22 and its vault comes back
                 * from slot 19 at the rate it was saved at (slot 3), which is
                 * the only rate it has: the same dollars it held that day.
                 */
                getReservesUsd(),
                /*
                 * ...AND WHAT THE CURRENCY DID TO IT THIS MONTH, slot 23. A
                 * flow, and the Exchange page shows it beside the vault, so a
                 * reload that dropped it would open the page on a month in
                 * which the currency had done nothing. An older save reads as
                 * nothing done until its first month turns. The rate the vault
                 * was last valued at is NOT saved: it is struck right after the
                 * reprice, nothing moves the rate between that and a save, so
                 * it is always the rate in slot 3.
                 */
                lastVaultRevaluation,
                /*
                 * ...AND WHAT IT DID TO THE DEBT, slot 24 (2026-09-21). The
                 * same flow on the other side of the position: struck by
                 * takeForeignDebt() after the reprice, and shown by the trade
                 * and finance pages beside the dollars owed - which read
                 * nothing on a freshly loaded city until the month turned. An
                 * older save has no slot 24 and reads as nothing done until
                 * its first month, which is what it always read. The rate the
                 * debt was last valued at is slot 14.
                 */
                lastRevaluation,
                /*
                 * ...AND THE DEFENCE, slots 25-27 (0.7.2): the dollars the
                 * central bank sold at the last reprice, the local money they
                 * fetched, and the dollars sold since founding. The first two
                 * are the month's flow the Exchange page and the forces page
                 * show between two presses, and would read nothing after a
                 * reload until the month turned; the third is a lifetime total,
                 * carried like every other. An older save has none of the
                 * three and reads as a vault that has never been spent.
                 */
                defenceUsd, defenceLocal, defenceUsdLifetime,
                /*
                 * ...AND WHAT THE REPRICE WAS HANDED, slots 28-30 (0.7.2): the
                 * real rate differential and the two inflations it was struck
                 * on. Restruck every month before the reprice, so no month
                 * reads them stale - but the forces page shows the real-rate
                 * term between two presses, and since the term stopped being
                 * the dial less a constant a reload that dropped them showed a
                 * city paying nothing over the world in real terms until the
                 * month turned. An older save reads nothing, as it always did.
                 */
                realRateDifferential, localInflation, worldInflation,
                /*
                 * ...AND THE LAND'S DOLLARS, slots 31-34 (0.7.6): the month
                 * struck - both ways, and out of the vault - and the same
                 * since founding. The month is the one the Exchange page and
                 * the Government tab show between two presses. The month in
                 * progress is NOT carried: the budget's own land line
                 * (LandManager's month) is cleared on load, and the dollars
                 * follow it so the two cannot disagree. An older save reads
                 * as land never bought in dollars.
                 */
                landUsd, landVaultUsd, landUsdLifetime, landVaultUsdLifetime,
                // ...and the vault's part at its local price, slot 35, and
                // all of it, slot 36: the Government tab splits the budget's
                // land line with them.
                landVaultLocal, landLocal,
                /*
                 * ...AND THE MONTH'S FLOWS THEMSELVES, slots 37-44 (0.7.46;
                 * the Trade spec's D4, A2 in the model fixes): what takeMonth()
                 * struck and what the treasury bought and sold of the dollars,
                 * so a freshly loaded Trade tab reads the month the live city
                 * read rather than "not counted yet". Nothing in the next
                 * month reads them before takeMonth() strikes them again, and
                 * startMonth() clears the two purchases, so the month played
                 * after a load is the one played without it. An older save
                 * has none of the eight and counts no month until one turns.
                 */
                exports, imports, foreignInterest, financialIn, financialOut, lastValuation,
                boughtThisMonth, soldThisMonth,
                /*
                 * ...AND THE FLOAT, slots 45-70 (XR1; THE FLOAT): parity's
                 * trend (level, slope, started), the real gap's level and what
                 * the rate shows of it, the balance's and the cover's levels
                 * reached, the market's swing and the interventions still felt,
                 * the market's seed (52 bits, so it is exact as a double), whether
                 * it is seeded and how many draws it has taken, the last month's
                 * five contributions and six moves, a year's GDP, the fall the
                 * month would have made before the defence, and (slot 70) what
                 * the treasury dealt since the last reprice. Every one is a
                 * level or a flow the month cannot restrike from the state it
                 * ended in: the trend and the market are paths, and a reload
                 * that dropped them would open the currency on a different one.
                 */
                trendLevel, trendSlope, trendStarted ? 1 : 0,
                gapTarget, gapReached, balanceReached, coverReached, market, interventionFelt,
                (double) marketSeed, marketSeeded ? 1 : 0, (double) marketDraws,
                lastContrib[0], lastContrib[1], lastContrib[2], lastContrib[3], lastContrib[4],
                moves[0], moves[1], moves[2], moves[3], moves[4], moves[5],
                gdpTrailing, lastWouldBeMove,
                dealtSinceReprice,
                /*
                 * ...AND THE REGIMES, slots 71-94 (XR2; THE REGIMES): the regime
                 * and who pays at the strong edge (ordinals), the band (centre,
                 * slope, width) and the peg, what a break still puts on the
                 * rate, the shadow's log and whether the last reprice held the
                 * rate away from it, the peg's draws, the break chance the hot
                 * money will take of itself home and the hazard and gap that
                 * made it, the local price still owed for the edge's dollars
                 * and who owes it (a purchase pressed before a save is paid
                 * after the load), the edge's dollars the month bought and
                 * their price and the lifetime, the pegs broken and the last
                 * break's cause, jump and shadow, whether the last reprice
                 * broke one, and the seventh move, the hold. Each is a level
                 * or a flow the month cannot restrike from the state it ended
                 * in. An older save reads Lean, nothing owed, nothing held.
                 */
                regime.ordinal(), edgePayer.ordinal(),
                bandCentre, bandSlope, bandWidth, pegRate,
                breakFelt, lastShadowLog, heldLastMonth ? 1 : 0, (double) pegDraws,
                doubtShare, lastHazard, lastPegGap,
                edgeOwedLocal, edgeOwedPayer.ordinal(),
                edgeBoughtUsd, edgeBoughtLocal, edgeBoughtUsdLifetime,
                breaks, lastBreakCause.ordinal(), lastBreakJump, lastBreakShadow,
                brokeThisMonth ? 1 : 0,
                moves[6],
                /*
                 * ...AND THE WORLD, slots 95-104 (WN4; THE WORLD REACHES THE D$):
                 * the dollar cycle, the risk-off and the hot money Game handed
                 * for the month, the terms of trade reached with the piece each
                 * of the reprices still to come adds and how many are left, the
                 * world's level as the last reprice struck it, last month's
                 * contribution (this month's move is the change from it), the
                 * move it made, and the loading the risk-off was weighed at.
                 * Each is a level or a flow the month cannot restrike from the
                 * state it ended in. An older save has no world, and the D$ is
                 * as it was.
                 */
                worldCycle, worldRiskOff, worldExposure,
                totReached, totStep, totLeft,
                worldReached, lastWorldContrib, moveWorld,
                worldLoading,
                /*
                 * ...AND THE VAULT IN EIGHT, slots 105-141 (WN4, THE VAULT IN
                 * EIGHT): the balance in each of the eight in its own units
                 * (105-112), the US$ to the unit of each as Game last handed it,
                 * 0 for a money never handed (113-120), the reserve mix's ordinal
                 * (121), the player's eight chosen shares (122-129) and the city's
                 * trade by money Game handed (130-137), the month's cross move in
                 * US$ (138), whether revalueVault() still has it to book (139: 1
                 * is yes), and the money that moved it most as an index, -1 for
                 * none (140), with its move against the US$ (141). Slot 22 stays
                 * the total, so an older build reads the same vault; a save that
                 * has none of these reads it as all US$, as it was. The cross
                 * move is a flow the month cannot restrike from the state it ended
                 * in, and the balances are the stock.
                 */
                vault[0], vault[1], vault[2], vault[3], vault[4], vault[5], vault[6], vault[7],
                usdPer[0], usdPer[1], usdPer[2], usdPer[3], usdPer[4], usdPer[5], usdPer[6], usdPer[7],
                mix.ordinal(),
                chosen[0], chosen[1], chosen[2], chosen[3], chosen[4], chosen[5], chosen[6], chosen[7],
                tradeShares[0], tradeShares[1], tradeShares[2], tradeShares[3], tradeShares[4], tradeShares[5], tradeShares[6], tradeShares[7],
                crossUsd, crossBooked ? 0 : 1, crossDriver, crossDriverMove,
                /*
                 * ...AND WHAT THE VAULT EARNED, slots 142-146 (XR4, THE VAULT
                 * EARNS): the month's interest in US$ (142), since founding
                 * (143), the money that earned most as an index, -1 for none
                 * (144), the US$ it earned (145) and the US$ the earning
                 * balances were worth before it, which the month's yield is
                 * struck on (146). The balances are slots 105-112, interest
                 * included; these are the flow the month cannot restrike from
                 * the state it ended in, and a screen reads them the month after.
                 * A save that has none of them reads nothing earned.
                 */
                interestUsd, interestUsdLifetime, interestDriver, interestDriverUsd, interestBaseUsd };
    }

    public void restore(double[] saved) {
        /*
         * FROM NOTHING, NOT FROM THE FOUNDING. buildWorld() founds every new
         * city with the founders' dollars in the vault, and the load path runs
         * buildWorld() before it gets here - so a save that carried no vault,
         * or no foreign accounts at all, would otherwise come back holding
         * the default founding's dollars (US$25M since 0.7.10) it never had.
         * Whatever the array does not say is what a city that never had it
         * holds: nothing.
         */
        reset();
        if (saved == null || saved.length < 4) return;   // refused whole
        cumulativeBalance = saved[0];
        importsTrailing = saved[1];
        monthsOfHistory = (int) Math.round(saved[2]);
        rate = saved[3] > 0 ? saved[3] : OPENING_RATE;
        if (saved.length > 4) forgiven = saved[4];
        if (saved.length > 8) {
            lifetimeExports = saved[5];
            lifetimeImports = saved[6];
            lifetimeInterest = saved[7];
            lifetimeFinancial = saved[8];
        }
        if (saved.length > 10) {
            exportsTrailing = saved[9];
            currentTrailing = saved[10];
        }
        if (saved.length > 13) {
            openness = saved[11];
            // slot 12 was the pressure, retired by XR1: written as 0, read by nobody
            lastAbsorption = saved[13];
        }
        if (saved.length > 15) {
            lastDebtRate = saved[14] > 0 ? saved[14] : rate;
            lifetimeRevaluation = saved[15];
        }
        if (saved.length > 16) repudiated = saved[16];
        if (saved.length > 17) lifetimeIntervention = saved[17];
        if (saved.length > 18) parity = saved[18] > 0 ? saved[18] : OPENING_PARITY;
        if (saved.length > 21) {
            financialTrailing = saved[20];
            financialGrossTrailing = Math.max(0, saved[21]);
        }
        /*
         * THE DOLLARS, from slot 22 when the save has them. An older save
         * kept only the local value (slot 19), and that comes back at the rate
         * it was saved at (slot 3) - the only rate it has, and the one those
         * dollars were worth that day. Before slot 19 there was no vault.
         */
        if (saved.length > 22)      vault[0] = Math.max(0, saved[22]);
        else if (saved.length > 19) vault[0] = Math.max(0, saved[19]) / rate;
        lastVaultRate = rate;
        if (saved.length > 23) lastVaultRevaluation = saved[23];
        if (saved.length > 24) lastRevaluation = saved[24];
        if (saved.length > 27) {
            defenceUsd = Math.max(0, saved[25]);
            defenceLocal = Math.max(0, saved[26]);
            defenceUsdLifetime = Math.max(0, saved[27]);
        }
        if (saved.length > 30) {
            realRateDifferential = Double.isFinite(saved[28]) ? saved[28] : 0;
            localInflation = Double.isFinite(saved[29]) ? saved[29] : 0;
            worldInflation = Double.isFinite(saved[30]) ? saved[30] : 0;
        }
        if (saved.length > 34) {
            landUsd = Math.max(0, saved[31]);
            landVaultUsd = Math.max(0, saved[32]);
            landUsdLifetime = Math.max(0, saved[33]);
            landVaultUsdLifetime = Math.max(0, saved[34]);
        }
        if (saved.length > 36) {
            landVaultLocal = Math.max(0, saved[35]);
            landLocal = Math.max(0, saved[36]);
        }
        // The month's flows (0.7.46, A2): a save that carried them counts the month.
        if (saved.length > 44) {
            exports = saved[37];
            imports = saved[38];
            foreignInterest = saved[39];
            financialIn = saved[40];
            financialOut = saved[41];
            lastValuation = saved[42];
            boughtThisMonth = saved[43];
            soldThisMonth = saved[44];
            monthCounted = true;
        }
        // The float (XR1, slots 45-70); a save from before it has none and its
        // trend opens on the next reprice, its market unseeded until Game seeds it.
        if (saved.length > 69) {
            trendLevel = saved[45];
            trendSlope = saved[46];
            trendStarted = saved[47] > .5;
            gapTarget = saved[48];
            gapReached = saved[49];
            balanceReached = saved[50];
            coverReached = saved[51];
            market = saved[52];
            interventionFelt = saved[53];
            marketSeed = (long) saved[54];
            marketSeeded = saved[55] > .5;
            marketDraws = Math.max(0, (long) saved[56]);
            for (int k = 0; k < 5; k++) lastContrib[k] = saved[57 + k];
            for (int k = 0; k < 6; k++) moves[k] = saved[62 + k];
            gdpTrailing = Math.max(0, saved[68]);
            lastWouldBeMove = saved[69];
        }
        if (saved.length > 70) dealtSinceReprice = saved[70];
        // The regimes (XR2, slots 71-94); a save from before them is on Lean with
        // nothing owed and nothing held, which is what the city was.
        if (saved.length > 94) {
            // An ordinal that is not one reads the default (Lean, the central bank, no cause), never the nearest.
            regime = Regime.values()[ordinalOr(saved[71], Regime.values().length, Regime.LEAN.ordinal())];
            edgePayer = EdgePayer.values()[ordinalOr(saved[72], EdgePayer.values().length, EdgePayer.CENTRAL_BANK.ordinal())];
            bandCentre = Math.max(0, saved[73]);
            bandSlope = Math.max(-MAX_BAND_SLOPE, Math.min(MAX_BAND_SLOPE, saved[74]));
            bandWidth = Math.max(0, Math.min(MAX_BAND_WIDTH, saved[75]));
            pegRate = Math.max(0, saved[76]);
            breakFelt = saved[77];
            lastShadowLog = saved[78];
            heldLastMonth = saved[79] > .5;
            pegDraws = Math.max(0, (long) saved[80]);
            doubtShare = Math.max(0, Math.min(1, saved[81]));
            lastHazard = Math.max(0, saved[82]);
            lastPegGap = saved[83];
            edgeOwedLocal = Math.max(0, saved[84]);
            edgeOwedPayer = EdgePayer.values()[ordinalOr(saved[85], EdgePayer.values().length, EdgePayer.CENTRAL_BANK.ordinal())];
            edgeBoughtUsd = Math.max(0, saved[86]);
            edgeBoughtLocal = Math.max(0, saved[87]);
            edgeBoughtUsdLifetime = Math.max(0, saved[88]);
            breaks = Math.max(0, (int) Math.round(saved[89]));
            lastBreakCause = BreakCause.values()[ordinalOr(saved[90], BreakCause.values().length, BreakCause.NONE.ordinal())];
            lastBreakJump = saved[91];
            lastBreakShadow = Math.max(0, saved[92]);
            brokeThisMonth = saved[93] > .5;
            moves[6] = saved[94];
        }
        // The world (WN4, slots 95-104); a save from before it has no world handed, nothing
        // reached and nothing moved by it, which is what the D$ was.
        if (saved.length > 104) {
            worldCycle = saved[95];
            worldRiskOff = saved[96];
            worldExposure = Math.max(0, saved[97]);
            totReached = saved[98];
            totStep = saved[99];
            totLeft = Math.max(0, Math.min(WorldMoneys.TOT_MONTHS, (int) Math.round(saved[100])));
            worldReached = saved[101];
            lastWorldContrib = saved[102];
            moveWorld = saved[103];
            worldLoading = saved[104];
        }
        // The vault in eight (WN4, slots 105-141); a save from before it has the one US$ balance of slot 22, all US$, as it was.
        if (saved.length > 141) {
            for (int k = 0; k < MONEYS; k++) {
                double rateK = saved[113 + k];
                usdPer[k] = k == 0 ? 1 : Double.isFinite(rateK) && rateK > 0 ? rateK : 0;
                rated[k] = usdPer[k] > 0;
                // A balance in a money with no rate to value it at cannot be kept: it reads empty.
                vault[k] = rated[k] && Double.isFinite(saved[105 + k]) ? Math.max(0, saved[105 + k]) : 0;
            }
            mix = Mix.values()[ordinalOr(saved[121], Mix.values().length, Mix.ALL_USD.ordinal())];
            setChosenShares(java.util.Arrays.copyOfRange(saved, 122, 130));
            setTradeShares(java.util.Arrays.copyOfRange(saved, 130, 138));
            crossUsd = Double.isFinite(saved[138]) ? saved[138] : 0;
            crossBooked = !(saved[139] > .5);
            crossDriver = ordinalOr(saved[140], MONEYS, -1);
            crossDriverMove = crossDriver > 0 && Double.isFinite(saved[141]) ? saved[141] : 0;
            crossDriverUsd = 0;
        }
        // What the vault earned (XR4, slots 142-146); a save from before it has earned nothing.
        if (saved.length > 146) {
            interestUsd = Double.isFinite(saved[142]) ? saved[142] : 0;
            interestUsdLifetime = Double.isFinite(saved[143]) ? saved[143] : 0;
            interestDriver = ordinalOr(saved[144], MONEYS, -1);
            interestDriverUsd = interestDriver >= 0 && Double.isFinite(saved[145]) ? saved[145] : 0;
            interestBaseUsd = Double.isFinite(saved[146]) ? Math.max(0, saved[146]) : 0;
        }
    }

    /** A saved ordinal, or the fallback's when it is not a whole number inside 0 to length - 1. */
    private static int ordinalOr(double saved, int length, int fallback) {
        long k = Math.round(saved);
        return Double.isFinite(saved) && k >= 0 && k < length ? (int) k : fallback;
    }

    public void reset() {
        rate = OPENING_RATE;
        pinned = false;
        // The vault in eight (WN4): empty, all US$ (the default mix), no money but the dollar rated, nothing moved across the moneys.
        java.util.Arrays.fill(vault, 0);
        java.util.Arrays.fill(usdPer, 0);
        java.util.Arrays.fill(rated, false);
        usdPer[0] = 1;
        rated[0] = true;
        mix = Mix.ALL_USD;
        java.util.Arrays.fill(chosen, 0);
        chosen[0] = 1;
        java.util.Arrays.fill(tradeShares, 0);
        crossUsd = 0;
        crossBooked = true;
        crossDriver = -1;
        crossDriverMove = crossDriverUsd = 0;
        // ...and nothing earned (XR4).
        clearInterest();
        interestUsdLifetime = 0;
        lastVaultRate = OPENING_RATE;
        lastVaultRevaluation = 0;
        cumulativeBalance = 0;
        forgiven = 0;
        exportsTrailing = 0;
        currentTrailing = 0;
        financialTrailing = financialGrossTrailing = 0;
        lifetimeExports = lifetimeImports = lifetimeInterest = lifetimeFinancial = 0;
        lastValuation = 0;
        importsTrailing = 0;
        monthsOfHistory = 0;
        exports = imports = foreignInterest = 0;
        financialIn = financialOut = 0;
        boughtThisMonth = soldThisMonth = 0;
        monthCounted = false;
        openness = 0;
        lastAbsorption = 0;
        foreignDebt = foreignDebtUsd = 0;
        lastDebtRate = OPENING_RATE;
        lastRevaluation = lifetimeRevaluation = 0;
        parity = OPENING_PARITY;
        repudiated = 0;
        lifetimeIntervention = 0;
        defenceUsd = defenceLocal = defenceUsdLifetime = 0;
        landUsdOpen = landVaultUsdOpen = landUsd = landVaultUsd = 0;
        landLocalOpen = landVaultLocalOpen = landLocal = landVaultLocal = 0;
        landUsdLifetime = landVaultUsdLifetime = 0;
        /*
         * ...AND THE THREE THE MONTH RE-STRIKES (0.7.1). The rate differential
         * and the two inflation rates are set every month before the currency
         * is repriced (Game.nextMonth(): setParity(), setRealRateDifferential()),
         * so no month ever read them stale - but a new city opened from a
         * finished one carried the finished one's until its first month, and
         * every screen that reads them (the forces page, the landing) showed
         * the old city's inflation gap on the new city's first morning. Saved
         * since 0.7.2 (slots 28-30), so a reload restores them over this.
         */
        realRateDifferential = 0;
        localInflation = worldInflation = 0;
        // ...and the float, at its founding values (XR1): a trend still to start,
        // every level and the market at nothing, no seed.
        trendLevel = trendSlope = 0;
        trendStarted = false;
        gapTarget = gapReached = 0;
        balanceReached = coverReached = 0;
        market = interventionFelt = 0;
        marketSeed = marketDraws = 0;
        marketSeeded = false;
        gdpTrailing = 0;
        java.util.Arrays.fill(lastContrib, 0);
        java.util.Arrays.fill(moves, 0);
        lastWouldBeMove = 0;
        dealtSinceReprice = 0;
        // ...and the world (WN4): none handed, nothing reached, nothing moved by it.
        worldCycle = worldRiskOff = worldExposure = 0;
        totReached = totStep = 0;
        totLeft = 0;
        worldReached = lastWorldContrib = moveWorld = worldLoading = 0;
        // ...and the regimes (XR2): Lean, who pays the central bank, no band, no
        // peg, nothing owed, nothing held, nothing broken.
        regime = Regime.LEAN;
        edgePayer = EdgePayer.CENTRAL_BANK;
        bandCentre = bandSlope = pegRate = 0;
        bandWidth = BAND_WIDTH;
        breakFelt = lastShadowLog = 0;
        heldLastMonth = false;
        pegDraws = 0;
        doubtShare = lastHazard = lastPegGap = 0;
        edgeBoughtUsd = edgeBoughtLocal = edgeBoughtUsdLifetime = 0;
        edgeOwedLocal = 0;
        edgeOwedPayer = EdgePayer.CENTRAL_BANK;
        breaks = 0;
        lastBreakCause = BreakCause.NONE;
        lastBreakJump = lastBreakShadow = 0;
        brokeThisMonth = false;
    }

    /**
     * Every figure in the city's foreign accounts, in the new unit.
     *
     * THE EXCHANGE RATE IS A PRICE AND IT MOVES. It is local dollars per US
     * dollar, so lopping two zeros off the local dollar divides it by a
     * hundred: a rate of 300 becomes 3. That single line is what keeps every
     * foreign price in the game honest without touching one of them - food at
     * $0.20 abroad is still $0.20 abroad, and what it costs here falls by the
     * same hundred as everything else.
     *
     * WHAT DOES NOT MOVE: foreignDebtUsd, because it is owed in somebody else's
     * money and no domestic reform can reach it (foreignDebt, its local
     * translation, moves because the RATE moved). The vault's dollars likewise
     * (2026-09-21): its local value is those dollars at the rate, and the rate
     * has already been divided - scaling both would divide it twice; so too
     * the dollars the defence sold (0.7.2) and the dollars paid for land
     * (0.7.6), while the local money the one fetched and the other cost
     * moves. Nor openness, absorption, the inflation rates or the real rate
     * differential - all ratios - nor any of the float's levels bar the trend:
     * each is a log of a ratio, and the trend is a log of a rate, which a
     * reform shifts by the log of its scale (trendLevel, and last month's
     * trend contribution beside it, so the reform is not a move).
     *
     * The parity moves because it is a rate, not a ratio: it is the level the
     * exchange rate reverts to, quoted in the same units as the rate itself.
     */
    public void redenominate(double scale) {
        rate     *= scale;
        minTrade *= scale;
        minRate  *= scale;
        maxRate  *= scale;
        parityBase *= scale;
        parity   *= scale;
        lastDebtRate *= scale;
        lastVaultRate *= scale;

        cumulativeBalance *= scale;
        forgiven          *= scale;
        foreignDebt       *= scale;

        exports         *= scale;
        imports         *= scale;
        foreignInterest *= scale;
        financialIn     *= scale;
        financialOut    *= scale;

        exportsTrailing *= scale;
        importsTrailing *= scale;
        currentTrailing *= scale;
        financialTrailing *= scale;
        financialGrossTrailing *= scale;

        lifetimeExports   *= scale;
        lifetimeImports   *= scale;
        lifetimeInterest  *= scale;
        lifetimeFinancial *= scale;
        lifetimeRevaluation *= scale;
        lifetimeIntervention *= scale;

        lastRevaluation *= scale;
        lastVaultRevaluation *= scale;   // (the vault's interest is US$, and stays like the vault's dollars; its local figure is read at the rate)
        lastValuation   *= scale;
        repudiated      *= scale;
        boughtThisMonth *= scale;
        soldThisMonth   *= scale;
        dealtSinceReprice *= scale;
        defenceLocal    *= scale;   // the dollars do not move; what they fetched is local money
        landLocalOpen      *= scale;   // ...and the same for what the land cost here (0.7.6)
        landVaultLocalOpen *= scale;
        landLocal          *= scale;
        landVaultLocal     *= scale;
        // The float (XR1): the trend is a log of a rate and moves with it; the GDP
        // it weighs the balance against is local money. Every other level is a
        // log of a ratio and stays.
        trendLevel += Math.log(scale);
        lastContrib[0] += Math.log(scale);
        worldExposure *= scale;   // (WN4) local money; the world's level is a log against the US$ and stays
        gdpTrailing *= scale;
        // The regimes (XR2): every rate and local figure moves like the rate; the
        // shadow's log shifts by the log of the scale, as the trend's does, so
        // the reform is not a move. The dollars, the logs and the shares stay.
        bandCentre *= scale;
        pegRate *= scale;
        lastBreakShadow *= scale;
        edgeOwedLocal *= scale;
        edgeBoughtLocal *= scale;
        lastShadowLog += Math.log(scale);
    }

}
