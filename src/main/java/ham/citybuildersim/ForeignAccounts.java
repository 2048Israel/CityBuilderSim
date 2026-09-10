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
 * See claude/foreign-exchange-design.md.
 *
 * ==================== WHAT A RESERVE IS, TODAY ====================
 *
 * A MEASUREMENT, not a pot of money, and that distinction is what keeps phase
 * one honest. The dollars a mill earns exporting steel land in the mill's cash,
 * exactly as they did yesterday; nothing was taken away and put somewhere else.
 * The reserve is the CUMULATIVE NET FOREIGN POSITION - what the city has earned
 * abroad less what it has spent there - and it is deliberately not one of
 * MoneyAudit's pools, because adding the same dollar to a pool as well as to the
 * sector that earned it would create money.
 *
 * In phase two, when the treasury can actually buy and sell foreign currency to
 * defend a rate, it becomes a real holding and the flows change. Not yet.
 *
 * A NEGATIVE reserve is not a bug. It means the city has bought more abroad than
 * it has sold, and is living on foreign credit that phase three has not modelled
 * yet. A young city importing its construction materials and exporting nothing
 * is exactly that, and the number saying so is the point.
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

    /** The rate cannot leave this range, whatever the arithmetic says. */
    /*
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
     */
    public static final double MIN_RATE = .01;
    public static final double MAX_RATE = 100.0;

    /**
     * The same three, in TODAY's money.
     *
     * All three are quoted in local dollars per US dollar, so all three are
     * PRICES and a currency reform divides them. Leaving the guards alone would
     * have been the subtler half of the same bug as parity: after lopping two
     * zeros a rate of 0.021 sits a hair above a floor of 0.01, so a city whose
     * currency then weakened a little would find it pinned - not by any
     * economic force but by a bound left behind in the old unit. The same shape
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

    /** Where the currency is pulled back towards. */
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

    private double parity = OPENING_PARITY;

    /** @param localLevel the city's basket vs founding, worldLevel the world's */
    /** Save slot 19. Not derivable: the local level it was struck from is a stock. */
    private double localInflation, worldInflation;

    /**
     * The two inflation rates the drift is struck from, and the level ratio
     * kept for the screen.
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
         * The reserve transactions stay out, as before: the vault absorbs
         * pressure through absorption(), which is the same idea from the
         * other side, and counting them here as well would take it twice.
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
     * with nothing takes all of it. Only a SURPLUS position can absorb anything
     * - a city already in net debt to the world has nothing to spend.
     */
    public double absorption() {
        if (reserves <= 0) return 0;
        double cover = importCover();
        if (cover == Double.MAX_VALUE) return MAX_ABSORPTION;
        return MAX_ABSORPTION * Math.max(0, Math.min(1, cover / COMFORTABLE_COVER));
    }

    /**
     * How far the city's own rate is above the world's, and what that is worth
     * to the currency.
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
     */
    public static final double RATE_PULL = 6.0;

    /** Most of the pressure a rate differential alone can produce. */
    public static final double MAX_RATE_PRESSURE = .8;

    private double rateDifferential;

    /** @param differential the city's rate less the world's, annual */
    public void setRateDifferential(double differential) {
        this.rateDifferential = differential;
    }

    public double getRateDifferential() { return rateDifferential; }

    /** Negative when the city pays over the odds: a rate advantage is support. */
    public double ratePressure() {
        double raw = -rateDifferential * RATE_PULL;
        return Math.max(-MAX_RATE_PRESSURE, Math.min(MAX_RATE_PRESSURE, raw));
    }

    /** The pressure that actually reaches the rate this month. */
    public double effectivePressure() {
        lastPressure = pressure() + ratePressure();
        lastAbsorption = absorption();
        return lastPressure * (1 - lastAbsorption) * openness;
    }

    public double getLastPressure()   { return lastPressure; }
    public double getLastAbsorption() { return lastAbsorption; }

    /**
     * Moves the rate on the month just taken.
     *
     * Proportional rather than additive, so a 2% move is 2% whether the currency
     * stands at one or at four - an additive drift would be trivial when the
     * rate is high and catastrophic when it is low.
     */
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

    public void repriceCurrency() {
        if (pinned) return;

        double push = effectivePressure();
        rate *= (1 + push * DRIFT_SPEED);

        // ...and the long-run pull. Applied every month, pressure or none, so a
        // city whose trade has come back into balance drifts home rather than
        // staying wherever the last crisis left it.
        /*
         * RELATIVE PPP AS A DRIFT, NOT AS A LEVEL.
         *
         * The first version pulled the rate toward parity = localLevel /
         * worldLevel. That is the LEVEL form of relative PPP and it is
         * unbounded over a long run: the playtest's city becomes self-
         * sufficient in food, so its own prices decouple from the world's, and
         * after 333 years of world inflation the level ratio said the currency
         * should be worth ten thousand times more than it started. Measured at
         * every band tried - even 0.2-1.5% a year gave a world 16x dearer
         * against local prices that had FALLEN, and the rate went to 0.05.
         *
         * That is not wrong, it is just what a level comparison does over three
         * centuries, and no exchange rate on earth has ever obeyed one. Real
         * rates deviate from PPP for decades at a time; what PPP actually
         * predicts well is the DIRECTION of drift from an inflation
         * differential.
         *
         *     rate drifts by (local inflation - world inflation)
         *
         * A city inflating faster than the world sees its currency fall by the
         * difference; a city holding its prices while the world inflates sees
         * it rise. Same economics, no accumulating level term, and it is the
         * form that makes the player's own inflation rate the thing that
         * decides what their money is worth - which is the whole reason the
         * policy rate is about to become a dial.
         */
        rate *= 1 + (localInflation - worldInflation) / 12;
        /*
         * ...AND STILL PULLED TOWARD PPP, because the drift alone has no
         * restoring force. Removing this to stop a level comparison running
         * away left nothing at all holding the rate, and a city with a
         * persistent trade surplus rode its own pressure term straight to the
         * floor - which is what a bound with nothing behind it looks like.
         *
         * Both terms are needed and they do different jobs: the drift says
         * which way the rate should be going, and this says where it should
         * end up.
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
       treasury buying and selling. Export earnings do NOT land here - they land
       with the firms that earned them, which is why a country with a trade
       surplus can still run out of reserves. This is what import cover is
       measured against and what a currency defence is fought with.
       ======================================================================= */
    private double reserves;

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
     * They also give the reserve a checkable definition: it is exactly
     * lifetime exports, less imports, less interest paid abroad, plus capital
     * received. ForeignCheck asserts that, which is what makes the stock and the
     * flows one set of books rather than two.
     */
    private double lifetimeExports;
    private double lifetimeImports;
    private double lifetimeInterest;
    private double lifetimeFinancial;
    private double importsTrailing;
    private int monthsOfHistory;

    /**
     * Months of imports the reserve would cover.
     *
     * THE HEADLINE NUMBER, and the one the exchange rate will be driven off in
     * phase two. It is the standard yardstick for reserve adequacy - three
     * months is the usual rule of thumb - and it is far more legible than the
     * raw figure, because "forty million" means nothing without knowing what the
     * city spends abroad.
     *
     * Measured against a TRAILING AVERAGE of imports rather than last month's,
     * so a single month with no construction going on does not report the city
     * as spectacularly well covered.
     */
    public static final double COVER_WINDOW = 12;

    public double importCover() {
        // A city in net debt to the world has no cover, not negative cover.
        if (reserves <= 0) return 0;
        if (importsTrailing <= 0) return Double.MAX_VALUE;
        return reserves / importsTrailing;
    }

    /** The trailing monthly import bill the cover is measured against. */
    public double monthlyImports() { return importsTrailing; }

    /** ...and the trailing figures the rate is decided on. */
    public double monthlyExports()       { return exportsTrailing; }
    public double monthlyCurrentAccount(){ return currentTrailing; }
    /** The financial account, trailing: positive is money coming in. See pressure(). */
    public double monthlyFinancialAccount(){ return financialTrailing; }

    /**
     * Takes the month off the audit that has just been struck.
     *
     * Fed the Result rather than the Game, because the Result is the thing that
     * has already been reconciled - reading the underlying getters a second time
     * would be a second definition of the same month, and this codebase has been
     * caught by that four times.
     */
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

    /** @param monthlyGdp the city's output, for the openness weighting above. */
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
     * The reserve position in dollars.
     *
     * The stock is carried in local money because every flow that built it was,
     * but what it BUYS is foreign - and a player deciding whether to convert
     * some of it back into spendable cash needs the figure in the money it is
     * actually held in.
     */
    public double getReservesUsd() { return rate > 0 ? reserves / rate : 0; }

    /**
     * Claims abroad less what is owed abroad: the city's net position.
     *
     * The figure a country is actually judged on. A city with deep reserves and
     * deeper dollar debt is not rich, and the reserve line on its own says it
     * is.
     */
    public double netForeignPosition() { return reserves - foreignDebt; }

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

    /** What the city has earned abroad, less what it has spent there. */
    /** What the treasury holds in foreign money, and could spend today. */
    public double getReserves() { return reserves; }

    /** Every month's balance of payments since founding, added up. */
    public double getCumulativeBalance() { return cumulativeBalance; }

    /** True when the city owes the world more than it has ever earned from it. */
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
     */
    public void buyReserves(double localAmount) {
        if (localAmount <= 0) return;
        reserves += localAmount;
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
     * @return what was actually sold, which is never more than the city had
     */
    public double sellReserves(double localAmount) {
        double sold = Math.max(0, Math.min(localAmount, Math.max(0, reserves)));
        if (sold <= 0) return 0;
        reserves -= sold;
        soldThisMonth += sold;
        lifetimeIntervention -= sold;
        return sold;
    }

    /** The most the treasury could sell right now. */
    public double sellableReserves() { return Math.max(0, reserves); }

    public double getBoughtThisMonth() { return boughtThisMonth; }
    public double getSoldThisMonth()   { return soldThisMonth; }

    public double getLifetimeExports()   { return lifetimeExports; }
    public double getLifetimeImports()   { return lifetimeImports; }
    public double getLifetimeInterest()  { return lifetimeInterest; }
    public double getLifetimeFinancial() { return lifetimeFinancial; }

    /**
     * The reserve, rebuilt from the flows that made it.
     *
     * Should equal getReserves() exactly. If it ever does not, the stock and the
     * flows have stopped being the same set of books.
     */
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
     * it here rather than there is what lets reservesFromFlows() still equal the
     * stock while takeMonth() leaves the stock alone.
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
                 * THE VAULT, saved separately since the two were split. Slot 18,
                 * so an older save restores its single number into the
                 * cumulative balance (slot 0) and starts the vault empty - which
                 * is the right reading of a save written when the two were one
                 * number and only the balance behaviour was ever real.
                 */
                reserves,
                /*
                 * The financial account's two trailing figures, slots 20 and
                 * 21, since the pressure was struck on the overall balance.
                 * Absent from an older save: both start at zero and settle
                 * within a year, as the current one always did.
                 */
                financialTrailing, financialGrossTrailing };
    }

    public void restore(double[] saved) {
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
        if (saved.length > 19) reserves = Math.max(0, saved[19]);
        if (saved.length > 21) {
            financialTrailing = saved[20];
            financialGrossTrailing = Math.max(0, saved[21]);
        }
    }

    public void reset() {
        rate = OPENING_RATE;
        pinned = false;
        reserves = 0;
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
     * translation, moves because the RATE moved). Nor openness, absorption,
     * pressure, the inflation rates or the rate differential - all ratios.
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

        reserves          *= scale;
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
        lastValuation   *= scale;
        repudiated      *= scale;
        boughtThisMonth *= scale;
        soldThisMonth   *= scale;
    }

}
