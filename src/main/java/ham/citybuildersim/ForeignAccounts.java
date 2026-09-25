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
 * floats (repriceCurrency(), WHAT MOVES THE RATE); the treasury borrows in
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
 * currency (A DEFENCE THAT SPENDS, at effectivePressure()), and since 0.7.6
 * the land office paid out of it when the player says so
 * (spendReservesOnLand()); it cannot go below zero, and it is KEPT IN DOLLARS -
 * reservesUsd - so everything local about it, what it is worth, what can be
 * sold, the import cover and the net position, is those dollars at today's
 * rate. When the currency moves the dollars stay put, and the move in their
 * local value is booked beside the vault as a revaluation (revalueVault()),
 * in the dollar debt's shape: not cash, and not an audit flow. A new city
 * opens with the founders' dollars in it - US$25M on the default founding
 * since 0.7.10, US$1B before (Game's THE FOUNDING RESERVE) - and it is
 * spent only when the push would weaken the currency (A RESERVE DEFENDS A
 * CURRENCY, at effectivePressure()): the central bank sells its dollars
 * against the month's deficit, and what they meet of it is what damps the
 * push.
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
     * So PRESSURE is the current account as a share of what the city trades, and
     * RESERVES are the buffer that decides how much of it reaches the rate. Deep
     * cover absorbs a bad year; shallow cover means it lands immediately; no
     * cover at all and the defence is over. That is how a managed float actually
     * behaves, and it gives long quiet stretches with sharp adjustments at the
     * edges rather than a rate that twitches every month.
     */

    /** Deficits smaller than this share of trade are noise, and are ignored. */
    public static final double DEAD_BAND = .05;

    /** Cover at which reserves fully absorb the month's pressure. */
    public static final double COMFORTABLE_COVER = 6;

    /** The most of the pressure deep reserves can absorb. */
    public static final double MAX_ABSORPTION = .85;

    /**
     * How much of a month's pressure passes into the rate.
     *
     * Small on purpose. At the residential city's 89% deficit this is about
     * 1.6% a month - roughly 20% a year, which is a currency in visible trouble
     * without being a currency that has ceased to exist. A 4% deficit inside the
     * dead band moves nothing at all.
     */
    public static final double DRIFT_SPEED = .02;

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

    /*
     * ==================== AND A PULL BACK TOWARDS PARITY ====================
     *
     * WHY THIS IS NEEDED, and it is not a fudge.
     *
     * The pressure signal is the current account as a share of trade, and both
     * of those are local-currency figures that scale with the exchange rate by
     * exactly the same factor. So the RATIO IS SCALE-INVARIANT: moving the rate
     * does not, by itself, change the number driving the rate. It only bites
     * through VOLUMES - imports get dear and people buy fewer.
     *
     * That works where volumes respond. It does not where they do not: a mine
     * sells whatever it digs at the world price, so an industrial city's export
     * volume is indifferent to the currency, its surplus never closes, and the
     * rate drifts in one direction for as long as the run lasts. Measured over
     * 4,000 months - which is 333 YEARS - it appreciated all the way to the
     * floor, quartering the ore price from $0.30 to $0.07 and closing mines.
     *
     * A long-run anchor fixes it and is better economics than leaving it out.
     * Real exchange rates mean-revert towards purchasing power parity, with a
     * half-life measured in years, and the pull is what stops small persistent
     * pressures compounding into absurdity over a century. Equilibrium is where
     * the pressure drift and the pull balance, which is a rate that reflects a
     * city's trade position without being a function of how long it has existed.
     */

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
     * Since 0.7.2 the inflations move nothing here directly - the drift they
     * were struck for is deleted (THE DRIFT THAT WAS DELETED) - and are kept
     * for the screens; Game hands the same two to the real rate differential.
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

    /** How hard. A half-life of roughly fifteen years. */
    public static final double REVERSION = .004;

    private double rate = OPENING_RATE;
    private double lastPressure;
    private double lastAbsorption;

    /**
     * The month's depreciation pressure: the current-account deficit as a share
     * of everything the city trades. Positive means the currency should weaken.
     */
    public double pressure() {
        /*
         * ON TRAILING FIGURES, and that is not a refinement.
         *
         * Measured on a single month, one construction order looks like a
         * currency crisis: the industrial city's current account swung from
         * +$874 to -$9,311 to +$3,246 in three consecutive years, and the rate
         * chased every one of them into the cap. A month says whether a mill was
         * staffed; a year says whether the city earns its living.
         */
        if (monthsOfHistory < SETTLING_MONTHS) return 0;

        /*
         * ON THE OVERALL BALANCE, NOT THE TRADE BALANCE (2026-09-10).
         *
         * This was the current account over the trade volume, which is the
         * whole pressure on a currency only in a city where nothing else
         * crosses the border. Money crosses it: a foreign bond the treasury
         * sells, a foreign shareholder's capital, and - the reason this
         * changed - the sectors' own savings going abroad for the world's
         * rate (OutwardInvestment). A surplus that is recycled into foreign
         * assets is not a surplus of demand for the currency; it is a
         * surplus met by an outflow, and the rate has to see both halves or
         * it appreciates on a demand that is not there. Measured before
         * this: every steel economy in the game appreciated at ten percent
         * a year until its exporters were dead, because nothing it earned
         * abroad ever went back.
         *
         * The reserve transactions stay out, as before: the vault meets
         * pressure by selling (effectivePressure(), since 0.7.2), which is the
         * same idea from the other side, and counting them here as well would
         * take it twice.
         * The volume grows by the gross of what crossed, so a month of pure
         * capital flow with no trade reads as a full push and not as a
         * division by nothing.
         */
        double volume = exportsTrailing + importsTrailing + financialGrossTrailing;
        if (volume <= minTrade) return 0;

        double raw = -(currentTrailing + financialTrailing) / volume;
        raw = Math.max(-1, Math.min(1, raw));
        if (Math.abs(raw) < DEAD_BAND) return 0;
        return raw - Math.signum(raw) * DEAD_BAND;
    }

    /**
     * Months before the rate is allowed to move at all.
     *
     * A city that has been trading for three months has no trend to read, and
     * its first construction order is its entire balance of payments. Without
     * this the currency had already fallen a third before the city had a
     * thousand people in it.
     */
    public static final int SETTLING_MONTHS = 24;

    /** Below this much trade a month, the exchange rate is not a real price. */
    public static final double MIN_TRADE = 50;

    /**
     * The same floor, in TODAY's money.
     *
     * A MONEY THRESHOLD IN A RATIO'S CLOTHING. pressure() is scale-free - a
     * deficit as a share of trade - so a currency reform should not touch it,
     * and it did: the guard underneath it is fifty DOLLARS of trade, and after
     * lopping two zeros a city trading $6,000 a month was trading $60 and fell
     * straight through it. Pressure went to exactly zero, the trade term
     * stopped defending the currency at all, and the two cities parted company
     * in the first month.
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
     * effectivePressure() sells this share of the month's deficit, since
     * 2026-09-21 only against a push that would weaken the currency, and
     * since 0.7.2 what actually damps the push is what the sale met
     * (getLastAbsorption()), which is less when the vault runs short.
     */
    public double absorption() {
        if (reservesUsd <= 0) return 0;
        double cover = importCover();
        if (cover == Double.MAX_VALUE) return MAX_ABSORPTION;
        return MAX_ABSORPTION * Math.max(0, Math.min(1, cover / COMFORTABLE_COVER));
    }

    /**
     * How far the city's own rate is above the world's - in real terms since
     * 0.7.2 - and what that is worth to the currency.
     *
     * THE CHANNEL THAT MAKES THE RATE A LEVER. Until this existed, pressure()
     * read the trade balance and nothing else, so a rate rise could not defend
     * a currency - which is the single most important thing a rate rise does in
     * an open economy and the whole reason the lever was deferred to this
     * phase. Money goes where it is paid better; a city paying five points over
     * the world is a city people buy currency to lend to.
     *
     * SIGNED AGAINST THE TRADE TERM. pressure() is DEPRECIATION pressure, so a
     * rate advantage has to come out negative: paying more than the world
     * supports the currency. Getting this backwards would make every rate rise
     * a devaluation, which is the sort of thing that passes review and fails a
     * playtest three hundred years later.
     *
     * Damped by the same openness the trade term is, because a closed economy's
     * rate is not a price anybody outside is trading on.
     *
     * ================ THE REAL RATE, NOT THE NOMINAL (0.7.2) ================
     *
     * Jerus: "i think we need to uncap the rate... but if we do... what
     * happens to everyone?"
     *
     * Until 0.7.2 the differential was NOMINAL - the policy rate less the
     * world's base rate - and capped at MAX_RATE_PRESSURE .8, so it saturated
     * 13.3 points over the world while the drift beside it (repriceCurrency(),
     * THE DRIFT THAT WAS DELETED) moved the rate by the whole inflation
     * differential, unbounded. A city inflating at 40% saw its currency fall
     * 40% a year by rule whatever its central bank did, and a dial past 17%
     * bought nothing. That was the ring the 0.6.9 year book closed.
     *
     * Now it is uncovered interest parity on the REAL rates: money moves to
     * the higher real return, so what Game hands setRealRateDifferential() is
     *
     *     (policy - the city's inflation) - (world base - the world's inflation)
     *
     * - the same two inflations setParity() receives. A city inflating at 40%
     * with its dial at 25% pays -15 real and is pushed down by the outflow
     * that implies; the same city at 45% is supported. A spiral needs an
     * actual outflow to continue, and a credible real rate stops it.
     *
     * ================== FOUR, NOT SIX (0.7.2, measured) ==================
     *
     * Six was struck against the NOMINAL gap under the .8 cap, where it
     * bought thirteen points of support and nothing past them. Uncapped and
     * on the real gap it is the loop's gain instead: a city whose dial sits
     * still while its prices rise sees its currency fall RATE_PULL x
     * DRIFT_SPEED x 12 times the gap a year (times the openness) - 1.44 at
     * six, half as fast again as the drift it replaced, which fell one for
     * one - and import prices follow it. Measured at six on the eight default
     * seeds, whose player looks in every few decades: seed 2's dial sat at
     * 0.08% while a local shock took inflation from 2% to 15% in ten months;
     * the real gap ran to -118 points, the rate to its guard and prices to
     * 115x founding, until the player's rule took the dial to 82% and the
     * same term, turned round, appreciated the currency 8% a month into 54%
     * deflation. At four the gain is 0.96 at full openness, under the
     * drift's one, and that episode did not recur (the seed swung 1.37x); a
     * credible real rate buys two thirds of the support it did, and with the
     * rule on the dial (the autopilot) neither setting spirals. Once the
     * household ledger's empty cells were fixed (0.7.2's gate) seed 2 took
     * another path and swings 2.65x on a different episode, with no month at
     * the guard: a bank failure at month 809, the vault sold down from month
     * 811 and empty by month 850, the dollar at 1.87 by month 980 and prices
     * at 2.47x by month 988, back to 1.16 by the end. The one retune the
     * batch allowed itself; Jerus's number to settle.
     */
    public static final double RATE_PULL = 4.0;

    /** A numerical guard, not a mechanic: it binds only at a real gap of MAX_RATE_PRESSURE / RATE_PULL - a hundred points, a currency in collapse rather than a policy (it was .8 until 0.7.2, a cap at 13 points that was the mechanic). */
    public static final double MAX_RATE_PRESSURE = 4.0;

    private double realRateDifferential;

    /** @param differential the city's real rate less the world's real rate, annual - see THE REAL RATE, NOT THE NOMINAL */
    public void setRealRateDifferential(double differential) {
        this.realRateDifferential = Double.isFinite(differential) ? differential : 0;
    }

    /** The city's real rate less the world's, as the month was handed it. */
    public double getRealRateDifferential() { return realRateDifferential; }

    /** Negative when the city pays over the odds in real terms: a real rate advantage is support. */
    public double ratePressure() {
        double raw = -realRateDifferential * RATE_PULL;
        return Math.max(-MAX_RATE_PRESSURE, Math.min(MAX_RATE_PRESSURE, raw));
    }

    /**
     * The pressure that actually reaches the rate this month.
     *
     * ==================== A RESERVE DEFENDS A CURRENCY ====================
     *
     * Jerus, 2026-09-21: "a reserve defends a currency; it does not hold one
     * down." So the vault absorbs a push only when the push would WEAKEN the
     * currency - the total, trade term plus the rate's support, positive. A
     * push the other way, appreciation from a surplus or the support of a
     * policy rate paid over the world's, passes through undamped.
     *
     * IT WAS DAMPED BOTH WAYS, and nobody could see it while the vault was
     * small. Nothing about a stock of dollars resists a currency rising: a
     * central bank leans against appreciation by BUYING dollars, which is a
     * decision, not something its vault does by standing there. Damped both
     * ways, a deep vault muted the only two forces that pull a currency back
     * from an inflation spiral - the surplus a weak currency earns, and the
     * policy rate - and left the drift, which carries the rate up with local
     * prices and import prices up with the rate. The old vault was held at
     * the price paid, so its cover fell as the currency fell and the damping
     * let go when it mattered: a stabiliser by accident, gone the day the
     * vault was kept in dollars. The founding reserve then gave every young
     * city hundreds of months of cover and the 0.85 ceiling for decades.
     *
     * Measured over the ensemble's eight seeds, founding reserve in: damped
     * both ways, three of the eight reproduced the year book that started the
     * batch - prices 141x and 190x founding with the currency at its guard,
     * and 11x - and the median swing of the price level went from 1.63x to
     * 3.80x. Damped on the way down only: median 1.60x, none past 2.81x
     * (the pristine runs' worst was 7.13x), and the dearest dollar 1.65.
     *
     * lastAbsorption is what was APPLIED - nothing on a month the currency was
     * pushed up - so the screens and the save say what the vault did, not
     * what it could have done. absorption() is still the vault's capacity.
     * Since 0.7.2 it is the REALISED figure: what the dollars actually sold
     * met of the month's deficit (A DEFENCE THAT SPENDS, below).
     *
     * ================== A DEFENCE THAT SPENDS (0.7.2) ==================
     *
     * Jerus, on the founding reserve: "1B is in usd in the reserve ... since
     * 99% players wont add to reserves" - and the-central-bank.md section 8:
     * the defence is what the founding US$1B is for. Until 0.7.2 absorption()
     * was applied to a weakening push and not a dollar left the vault: a
     * capacity that cost nothing, which is why the default player never saw
     * the vault work and why it could sit at 0.85 for fifty years. Now the
     * central bank SELLS:
     *
     *     dollarsSold = absorption() x deficitUsd, at most what the vault holds
     *
     * deficitUsd is the month's OWN net outflow - its current plus financial
     * account off the audit the month just struck, when negative, in dollars
     * at the month's rate (monthDeficitUsd()) - and not the trailing sum
     * pressure() reads. The trailing window says which way the currency is
     * being pushed; the month says how many dollars were actually wanted, and
     * a central bank sells what is asked of it this month, not a year's
     * average of it. What damps the push is the REALISED absorption,
     * dollarsSold / deficitUsd: a full vault damps exactly what absorption()
     * says, one running low damps less each month as its cover falls, and an
     * empty one damps nothing at all.
     *
     * A PUSH TO WEAKEN ON A MONTH WITH NO DEFICIT sells nothing and damps
     * nothing - the trailing window remembering last year's deficit, or the
     * real rate pushing on its own. That is the honest reading: nobody came
     * to the window for dollars this month, so there was nothing to meet, and
     * the whole push reaches the rate.
     *
     * A RISE IS NEVER DEFENDED: nothing is bought. Leaning against
     * appreciation is buying dollars, which is a decision (batch E, if Jerus
     * wants it), not something a vault does by standing there.
     *
     * THE OTHER SIDE IS THE CENTRAL BANK'S EQUITY (CentralBank, THE
     * DEFENCE): the vault's dollars leave here, the central bank's equity
     * falls by their local price, and M0 does not move - a capital
     * transaction against the world, not money destroyed; no pool moves, so
     * the audit declares nothing. lifetimeIntervention falls by the local
     * money exactly as a treasury sale does - the vault is still "what the
     * treasury chose to buy and has not yet sold". The treasury's own
     * sellReserves() is not this and is untouched.
     *
     * The month's own call, from repriceCurrency(), and it RECORDS AND SELLS:
     * lastPressure, lastAbsorption and the month's sale are written here. A
     * screen asks previewPressure(), the same arithmetic with nothing written
     * and nothing sold.
     */
    public double effectivePressure() {
        lastPressure = pressure() + ratePressure();
        double deficitUsd = monthDeficitUsd();
        // Both struck before the sale: absorption() reads the vault the sale empties.
        lastAbsorption = realisedFor(lastPressure, deficitUsd);
        sellToDefend(dollarsFor(lastPressure, deficitUsd));
        return damped(lastPressure, lastAbsorption);
    }

    /**
     * The same push, computed and not recorded - for a screen.
     *
     * WHY IT EXISTS (2026-09-21). The trade page's "forces on the rate" asked
     * effectivePressure() for the figure it prints, and effectivePressure()
     * writes lastPressure and lastAbsorption - the two readings the month
     * records for the save and for the same page's own lines. So opening the
     * page between two months rewrote the month's record from the screen, the
     * shape ReadPathCheck exists to stop: reading the city must not change the
     * city. This is the arithmetic without the two writes; the month's caller,
     * repriceCurrency(), still records them - and since 0.7.2 sells the vault's
     * dollars, which a screen doing would be a great deal worse than a write.
     */
    public double previewPressure() {
        double total = pressure() + ratePressure();
        return damped(total, realisedFor(total, monthDeficitUsd()));
    }

    /**
     * ...and the readings it is made of, on the same figures and recorded
     * nowhere (0.7.1): the imbalance and the real rate together, and what the
     * vault would take of it - since 0.7.2 the share of this month's deficit
     * the dollars it would sell would meet, and the dollars themselves. The
     * trade page's forces are one reading - the next reprice, on today's
     * figures - and it printed the month's RECORDED pressure and absorption
     * (getLastPressure(), getLastAbsorption()) beside this preview's push,
     * which are a month apart once the month's accounts have moved.
     */
    public double previewRawPressure() { return pressure() + ratePressure(); }

    public double previewAbsorption()  { return realisedFor(previewRawPressure(), monthDeficitUsd()); }

    public double previewDefenceUsd()  { return dollarsFor(previewRawPressure(), monthDeficitUsd()); }

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

    /** What the central bank sells against a push of this size on this deficit: the vault's capacity times the deficit, at most the vault; nothing against a rise, nothing on a month with no deficit. */
    private double dollarsFor(double total, double deficitUsd) {
        if (!(total > 0) || !(deficitUsd > 0) || !(reservesUsd > 0)) return 0;
        return Math.min(absorption() * deficitUsd, reservesUsd);
    }

    /** ...and the share of the deficit it meets, which is what damps the push: the capacity when the vault covers the sale, less when it cannot. */
    private double realisedFor(double total, double deficitUsd) {
        double sold = dollarsFor(total, deficitUsd);
        if (sold <= 0) return 0;
        return sold < absorption() * deficitUsd ? sold / deficitUsd : absorption();
    }

    /** The push that reaches the rate, once the vault and the openness have had their share. */
    private double damped(double total, double absorbed) {
        return total * (1 - absorbed) * openness;
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
        reservesUsd = usd >= reservesUsd ? 0 : reservesUsd - usd;
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

    public double getLastPressure()   { return lastPressure; }
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
     * Moves the rate on the month just taken.
     *
     * Proportional rather than additive, so a 2% move is 2% whether the currency
     * stands at one or at four - an additive drift would be trivial when the
     * rate is high and catastrophic when it is low.
     *
     * TWO TERMS SINCE 0.7.2: the month's push - the trade balance and the real
     * rate, less what the vault's sale met of it - and the pull back to
     * parity. A pinned rate is not defended by a sale: the promise is the
     * treasury's, and nothing is sold.
     */
    public void repriceCurrency() {
        if (pinned) { defenceUsd = defenceLocal = 0; return; }

        double push = effectivePressure();
        rate *= (1 + push * DRIFT_SPEED);

        /*
         * ================ THE DRIFT THAT WAS DELETED (0.7.2) ================
         *
         * Three forms of relative PPP have stood here, and the history is the
         * reason for the one that is left.
         *
         * THE LEVEL FORM pulled the rate toward localLevel / worldLevel. It is
         * unbounded over a long run: the playtest's city became self-sufficient
         * in food, its prices decoupled from the world's, and after 333 years
         * of world inflation the level ratio said the currency should be worth
         * ten thousand times more than it started - the rate went to 0.05. It
         * survives, bounded, as the PULL below: parity is the level form,
         * applied at REVERSION, a half-life of fifteen years.
         *
         * THE DRIFT FORM moved the rate every month by the whole inflation
         * differential, rate *= 1 + (local - world) / 12: "the direction of
         * drift from an inflation differential", which is what PPP predicts
         * well. And it was the ring. With imports repricing through the rate
         * one for one, wages chasing the whole index and the shelf cost-plus,
         * a gain of one on this link meant a city whose prices rose 40% a year
         * saw its currency fall 40% a year by rule, whatever its central bank
         * did - while the support a high rate gave it was capped at 13 points
         * and the dial at 25%. Jerus's 0.6.9 year book is that loop run for
         * 306 months: prices 399x, the currency at its guard
         * (a-reserve-defends-a-currency.md section 1).
         *
         * NEITHER SURVIVES AS A DIRECT TERM. The inflation differential still
         * reaches the currency - through the pull toward parity, which rises
         * with local prices, and through the trade balance a dear or cheap
         * currency earns (pressure()) - but not by rule, month by month. The
         * monthly drift comes from the capital account now: ratePressure() on
         * the REAL rate, so a spiral needs an actual outflow to continue and a
         * credible real rate stops it (THE REAL RATE, NOT THE NOMINAL).
         */

        // ...and the long-run pull. Applied every month, pressure or none, so a
        // city whose trade has come back into balance drifts home rather than
        // staying wherever the last crisis left it.
        /*
         * STILL PULLED TOWARD PPP, because the push alone has no restoring
         * force: with nothing holding it, a city with a persistent trade
         * surplus rode its own pressure term straight to the floor - which is
         * what a bound with nothing behind it looks like. The push says which
         * way the rate is being moved this month, and this says where it
         * should end up.
         */
        rate += (parity - rate) * REVERSION;

        rate = Math.max(minRate, Math.min(maxRate, rate));
    }

    /** How far the currency sits from parity. Negative is stronger than parity. */
    public double deviationFromParity() { return parity > 0 ? rate / parity - 1 : 0; }

    /** Local currency per US dollar. */
    public double getRate() { return rate; }

    /** What a USD amount is worth in the city's own money. */
    public double toLocal(double usd) { return usd * rate; }

    /** ...and the other way. */
    public double toUsd(double local) { return rate > 0 ? local / rate : 0; }

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
    private double reservesUsd;

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

    /* Trailing means, for anything the rate is decided on. See pressure(). */
    private double exportsTrailing;
    private double currentTrailing;
    /**
     * The financial account, trailing, and the gross of it - what crossed in
     * either direction - so the pressure can be struck on the OVERALL balance
     * over everything that crossed. Since 2026-09-10; see pressure().
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
     * the cover is measured against, and the balances the pressure reads.
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
        if (reservesUsd <= 0) return 0;
        if (importsTrailing <= 0) return Double.MAX_VALUE;
        /*
         * The vault at TODAY's rate over the import bill in today's money, so
         * a falling currency moves both halves together and the cover does not
         * move with it. Imports are dollar goods; so is the vault. It used to
         * be the vault at the price paid over today's bill, and fell exactly
         * as fast as the currency did.
         */
        return reservesUsd * rate / importsTrailing;
    }

    /** The trailing monthly import bill the cover is measured against. */
    public double monthlyImports() { return importsTrailing; }

    /** ...and the trailing figures the rate is decided on. */
    public double monthlyExports()       { return exportsTrailing; }
    public double monthlyCurrentAccount(){ return currentTrailing; }
    /** The financial account, trailing: positive is money coming in. See pressure(). */
    public double monthlyFinancialAccount(){ return financialTrailing; }

    /**
     * How much of the economy actually crosses the border.
     *
     * Trade against GDP, and it is the missing half of the pressure signal. The
     * deficit-as-a-share-of-trade ratio says how BAD an imbalance is; this says
     * how much the economy cares. Without it, a city with no exports sits at the
     * maximum reading for ever - its deficit is 100% of its trade whether that
     * trade is a tenth of the economy or all of it - and the currency falls at a
     * fixed rate until it hits the floor, which is a mechanic with no gradient
     * in it.
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
         * creditors absorbing its hole leaves the city better off against the
         * world without anybody having earned a dollar, so it improves the
         * position and does not fill the vault. Left in, the reserve swung from
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
     * The reserve position in dollars - the stock itself.
     *
     * Since 2026-09-21 this is the figure the vault is KEPT in, not a
     * translation of it: the money is foreign, so its own unit is the one that
     * does not move when the currency does. See THE VAULT IS HELD IN DOLLARS,
     * in the banner TWO NUMBERS THAT WERE ONE NUMBER.
     */
    public double getReservesUsd() { return reservesUsd; }

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
    public double getReserves() { return reservesUsd * rate; }

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

    /** Clears the month's intervention. Called at the top of the month. */
    public void startMonth() {
        boughtThisMonth = 0;
        soldThisMonth = 0;
    }

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
    public void buyReserves(double localAmount) {
        if (localAmount <= 0 || rate <= 0) return;
        reservesUsd += localAmount / rate;
        boughtThisMonth += localAmount;
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
        reservesUsd = sold >= held ? 0 : Math.max(0, reservesUsd - sold / rate);
        soldThisMonth += sold;
        lifetimeIntervention -= sold;
        return sold;
    }

    /** The most the treasury could sell right now, in local money at today's rate. */
    public double sellableReserves() { return Math.max(0, getReserves()); }

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
       bought and spent in the same breath, and they are the only way a
       treasury purchase of dollars touches the currency: a reserve
       transaction is the financing item (MoneyAudit, Scope.RESERVE), so
       pressure() and monthDeficitUsd() read neither, and buying dollars puts
       no push on the rate here. Converting for land therefore puts exactly
       the push a reserve purchase of the same dollars would - none - and the
       only difference between the two is that the reserve purchase leaves the
       dollars in the vault, where absorption() can see them. ForeignCheck
       asserts both halves.

       FROM THE VAULT: the dollars leave reservesUsd and no local money moves
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
        if (!(usd > 0) || !Double.isFinite(usd) || reservesUsd <= 0 || rate <= 0) return 0;
        double spent = Math.min(usd, reservesUsd);
        reservesUsd = spent >= reservesUsd ? 0 : reservesUsd - spent;
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

    /** Values the vault at today's rate. Called once a month, after the reprice. */
    public void revalueVault() {
        if (rate <= 0) return;
        lastVaultRevaluation = reservesUsd * (rate - lastVaultRate);
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
     * What the treasury has bought and sold on its own account, since founding.
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
                 */
                openness, lastPressure, lastAbsorption,
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
                 * 21, since the pressure was struck on the overall balance.
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
                reservesUsd,
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
                landVaultLocal, landLocal };
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
            lastPressure = saved[12];
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
        if (saved.length > 22)      reservesUsd = Math.max(0, saved[22]);
        else if (saved.length > 19) reservesUsd = Math.max(0, saved[19]) / rate;
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
    }

    public void reset() {
        rate = OPENING_RATE;
        pinned = false;
        reservesUsd = 0;
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
        openness = 0;
        lastPressure = lastAbsorption = 0;
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
     * moves. Nor openness, absorption, pressure, the inflation rates or the
     * real rate differential - all ratios.
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
        lastVaultRevaluation *= scale;
        lastValuation   *= scale;
        repudiated      *= scale;
        boughtThisMonth *= scale;
        soldThisMonth   *= scale;
        defenceLocal    *= scale;   // the dollars do not move; what they fetched is local money
        landLocalOpen      *= scale;   // ...and the same for what the land cost here (0.7.6)
        landVaultLocalOpen *= scale;
        landLocal          *= scale;
        landVaultLocal     *= scale;
    }

}
