package ham.citybuildersim;

/**
 * Hot money: what comes in chasing a spread, and what happens when it leaves.
 *
 * THE CARRY TRADE. A city paying 9% when the world pays 2% is an offer, and
 * money takes it. Foreign savers move in, fund the city's credit system, and
 * everything gets cheaper and easier - more deposits means more capacity means
 * more lending means more building. None of it was earned by selling anything.
 *
 * AND THE SUDDEN STOP, which is the same sentence read backwards. The money is
 * not owed to anybody and has no maturity, so nothing has to go wrong for it to
 * leave; the spread merely has to stop being worth the risk. When it goes it
 * takes the bank's funding with it, which is the one thing a bank cannot
 * replace quickly, and the credit boom unwinds into whatever was built on it.
 * Mexico 1994, Thailand and Korea 1997, and every emerging market since.
 *
 * WHY THE TWO CHANNELS ARE ONE CHANNEL HERE, because the design called for
 * "both, split by where the yield is" and this looks like only half of it:
 *
 * In this game the city's bonds are bought BY ITS OWN BANK - that is the whole
 * of the circular-capital story, and it is still true. So a foreigner who wants
 * the city's bond yield and a foreigner who wants the bank's deposit rate are
 * funding the same balance sheet one step apart, and giving them two separate
 * pipes would be modelling the same money twice. Both yields therefore compete
 * to decide HOW MUCH comes - the money goes where the return is, and the return
 * is the better of the two - and all of it arrives as funding for the bank that
 * holds both. The split is reported, because the player should see which offer
 * is pulling; it is not two flows, because there is one balance sheet.
 *
 * @author Jerus
 */
public class CapitalFlows {

    /* =======================================================================
       HOW MUCH WANTS TO COME
       ======================================================================= */

    /**
     * Foreign money held, per point of excess return, as a multiple of a year's
     * output.
     *
     * The spread is measured NET OF THE COUNTRY PREMIUM, which is what makes
     * this a carry trade rather than a free lunch. A city paying 9% while the
     * world pays 2% has a seven-point gap, but if six of those points are what
     * the world charges this city for its own risk, only one point is a reason
     * to come. That is exactly the calculation a carry trader does, and it is
     * why the money leaves a country whose risk has caught up with its rate
     * without the rate having moved at all.
     */
    public static final double APPETITE = 3.0;

    /** Excess return above which appetite stops growing. Beyond this it is a warning. */
    public static final double MAX_SPREAD = .06;

    /** How much of the gap to its target the stock closes in a month, coming in. */
    public static final double ARRIVAL_SPEED = .08;

    /** ...and going out, which is faster, because leaving is always faster. */
    public static final double DEPARTURE_SPEED = .20;

    /**
     * Below this much foreign money, the flow is not worth modelling.
     *
     * MONEY, in FOUNDING dollars, so it is seeded rather than read directly -
     * the fourteenth member of the redenomination family and the last one a
     * reformed city could feel. A hundred-to-one reform turns $3.01 of hot money
     * into $0.03, which is under a flat $1 floor, so the reformed city's foreign
     * money was written off entirely while its unreformed twin kept it - and the
     * bank's deposit book, its funding cost, its foreign borrowing and the
     * balance of payments all followed. A threshold in absolute money is a
     * threshold that means something different after a reform.
     */
    public static final double MIN_STOCK = 1;

    /** MIN_STOCK in today's money. See seedConstants(). */
    private double minStock = MIN_STOCK;

    /**
     * Puts the money constants into the city's current unit.
     *
     * Called at construction (unit 1), on load, and kept current through a
     * reform by redenominate() - the same three doors every other seeded
     * constant in this codebase goes through.
     */
    public void seedConstants(double unit) {
        minStock = MIN_STOCK / (unit > 0 ? unit : 1);
    }

    /**
     * Months of output below which the hot money is too small to break anything.
     *
     * A city with a few thousand dollars of foreign funding and no reserves is
     * not one bad month from a banking crisis, and treating it as one is what
     * made the first version fire on arrival.
     *
     * AND IT HAS TO BE REACHABLE, which is not a design point but an arithmetic
     * one, and it was wrong. The largest stock this class can ever hold is
     * MAX_SPREAD x APPETITE x 12 months of output - at the old constants,
     * 1.08 months. The threshold was three. So "material" was unreachable by
     * construction, fragility could never be true, and every run in the harness
     * and the playtest alike sailed through every shock unharmed. The mechanic
     * read as calm because it was unreachable, which is the most convincing way
     * for something to be broken.
     *
     * A ceiling and a floor set from the same two constants have to be checked
     * against each other. CapitalFlowCheck asserts the relation now, so the
     * next person to tune APPETITE finds out immediately.
     */
    public static final double MATERIAL_MONTHS = .5;

    /** The largest position these constants can ever produce, in months of output. */
    public static double maxStockInMonthsOfOutput() {
        return MAX_SPREAD * APPETITE * 12;
    }

    /* =======================================================================
       AND WHAT MAKES IT GO
       ======================================================================= */

    /**
     * Reserves needed to back the hot money, as a share of it.
     *
     * MEASURED AGAINST WHAT CAN RUN, NOT AGAINST IMPORTS - and the first
     * version got this wrong in a way worth keeping written down. Gating the
     * panic on import cover meant a city that had never bought a reserve in its
     * life was permanently in crisis: the playtest suffered 110 sudden stops in
     * 333 years, every one of them reading "reserves cover 0.0 months of
     * imports", and hot money could never accumulate at all because it was
     * always fleeing a city it had never arrived in.
     *
     * Thin reserves do not make a country untouchable. They make it VULNERABLE,
     * and the ratio that says so is reserves against short-term external
     * liabilities - the Guidotti-Greenspan rule, which is exactly this. A city
     * with no hot money needs no reserves and cannot have a run; a city that
     * has let a mountain of it build up needs a mountain of reserves behind it,
     * and if it did not buy them in the good years then the good years are what
     * it will lose.
     *
     * Which is the answer to "should the player be able to do anything about
     * it": the war chest, and nothing else.
     */
    public static final double PANIC_BACKING = .25;

    /** A twelve-month fall in the currency past this reads as a run. */
    public static final double PANIC_DEPRECIATION = .12;

    /** How long a break lasts before money will look at the city again. */
    public static final int PANIC_MONTHS = 18;

    /**
     * The share that leaves each month while confidence is broken.
     *
     * PAINFUL, NEVER FATAL - Jerus's call, and this constant is where it lives.
     * A third a month empties the position in about six months, which is fast
     * enough to break a credit boom and slow enough that a bank with any
     * capital at all can shed its book instead of failing outright. A real
     * sudden stop is faster than this; a real sudden stop also ends countries,
     * and a 300-year run should not be decided by one bad decade.
     */
    public static final double PANIC_EXIT = .33;

    /* ----------------------------------- state ----------------------------------- */

    private double stock;
    private double target;
    private double spread;
    private double arrived;          // this month, gross
    private double departed;         // this month, gross
    private int panicUntil = -1;
    private String panicReason = "";
    private double depositShare = .5;
    private double lifetimeArrived, lifetimeDeparted;
    private double peakStock, peakSpread;
    private int stopsSuffered;

    /* --------------------------------- the month --------------------------------- */

    /**
     * @param depositRate    what the bank pays savers, annual
     * @param cityRate       what the city pays on its paper, annual
     * @param worldRate      the world's own price of money
     * @param countryPremium what the world charges this city for its risk
     * @param monthlyGdp     the size of the thing the money is coming to
     * @param reserves       what the treasury holds abroad, in local money
     * @param yearlyRateMove how far the currency has fallen over a year, as a share
     * @param bankStressed   true if the bank is insolvent or has stopped lending
     * @param defaulted      true if the city has defaulted abroad recently
     * @param month          the game month
     */
    /* =====================================================================
       WHAT THE MONEY WOULD DO AT A DIFFERENT PRICE.

       A pure forecast: no state moves, nothing is recorded. It exists so the
       BANK can ask "if I paid savers more, how much more would turn up?" before
       deciding what to pay - see Bank.chooseDepositRate().

       It is the same arithmetic takeMonth() uses, lifted out and made
       side-effect-free, rather than a second model of the same thing. A
       forecast that disagrees with the mechanic it forecasts is worse than no
       forecast, and this codebase has been caught by a re-derived line more
       than once.

       Panic is deliberately NOT modelled here. A bank deciding its rate for a
       normal month should not be handed a number that assumes a run; and a bank
       in a run has fundToCover()'s own guards in front of it.
       ===================================================================== */
    public double stockAt(double depositRate, double cityRate, double worldRate,
                          double countryPremium, double monthlyGdp) {
        double best = Math.max(depositRate, cityRate);
        double wouldSpread = Math.max(0, Math.min(MAX_SPREAD, best - worldRate - countryPremium));
        return wouldSpread * APPETITE * Math.max(0, monthlyGdp) * 12;
    }

    /** How much of that gap actually arrives in the first month. */
    public double arrivalsAt(double depositRate, double cityRate, double worldRate,
                             double countryPremium, double monthlyGdp) {
        double wouldBe = stockAt(depositRate, cityRate, worldRate, countryPremium, monthlyGdp);
        return Math.max(0, (wouldBe - stock) * ARRIVAL_SPEED);
    }

    public void takeMonth(double depositRate, double cityRate, double worldRate,
                          double countryPremium, double monthlyGdp,
                          double reserves, double yearlyRateMove,
                          boolean bankStressed, boolean defaulted, int month) {

        arrived = 0;
        departed = 0;

        /* ------------------------- is anybody panicking ------------------------- */

        /*
         * FRAGILITY IS NOT A CRISIS. A SHOCK ON TOP OF IT IS.
         *
         * The backing ratio on its own fired the moment the first dollar
         * arrived in a city with no reserves - which is every young city - so
         * hot money spent 333 years fleeing a place it had never managed to
         * arrive in: 110 stops, nothing ever accumulated, and the whole
         * mechanic reduced to a flicker.
         *
         * That is not how it works and it is not a good game. Countries with
         * thin reserves do not have a crisis every month; they build up a
         * position for years and then a shock arrives and finds them with
         * nothing behind it. Thin reserves decide whether a bad month becomes a
         * run, not whether the money comes at all.
         *
         * So: FRAGILE is a state - a material position with little behind it -
         * and a SHOCK is an event. A run needs both. Which is what makes the
         * war chest worth buying: it does not stop the shock, it stops the
         * shock from becoming the thing that ends your decade.
         *
         * A default is the exception, because a city that has already refused
         * to pay does not need to be fragile for money to leave.
         */
        boolean material = stock > Math.max(minStock, monthlyGdp * MATERIAL_MONTHS);
        boolean fragile = material && Math.max(0, reserves) < stock * PANIC_BACKING;

        String shock = null;
        if (defaulted) {
            shock = "the city defaulted abroad";
        } else if (yearlyRateMove > PANIC_DEPRECIATION) {
            shock = String.format("the currency fell %.0f%% in a year", yearlyRateMove * 100);
        } else if (bankStressed) {
            shock = "the bank stopped lending";
        }

        String breaking = null;
        if (shock != null && (fragile || defaulted)) {
            breaking = fragile
                    ? String.format("%s, and only %.0f%% of the money that can leave is "
                            + "backed by reserves",
                            shock, stock > 0 ? Math.max(0, reserves) / stock * 100 : 0)
                    : shock;
        }

        if (breaking != null && month > panicUntil) {
            /*
             * A NEW break, not a continuing one. Counted, because a mechanic
             * that has never fired in a real run looks exactly like one that
             * does not exist - and this project has shipped one of those before.
             */
            if (panicUntil < 0 || month > panicUntil) stopsSuffered++;
            panicReason = breaking;
        }
        if (breaking != null) panicUntil = month + PANIC_MONTHS;

        boolean panicking = month <= panicUntil;

        /* --------------------------- what it is worth --------------------------- */

        double best = Math.max(depositRate, cityRate);
        spread = Math.max(0, Math.min(MAX_SPREAD, best - worldRate - countryPremium));

        /*
         * WHICH OFFER IS PULLING. Reported rather than acted on - see the class
         * comment for why there is one pipe - but it is the difference between
         * "savers are being paid well here" and "the government is paying up",
         * and those are different stories about the same city.
         */
        double dEdge = Math.max(0, depositRate - worldRate);
        double bEdge = Math.max(0, cityRate - worldRate);
        depositShare = (dEdge + bEdge) > 0 ? dEdge / (dEdge + bEdge) : .5;

        target = panicking ? 0 : spread * APPETITE * Math.max(0, monthlyGdp) * 12;

        /* ------------------------------ and it moves ------------------------------ */

        if (panicking && stock > 0) {
            departed = Math.min(stock, stock * PANIC_EXIT);
        } else if (target > stock) {
            arrived = (target - stock) * ARRIVAL_SPEED;
        } else {
            departed = Math.min(stock, (stock - target) * DEPARTURE_SPEED);
        }

        stock = Math.max(0, stock + arrived - departed);
        if (stock < minStock && target <= 0) {
            departed += stock;
            stock = 0;
        }
        lifetimeArrived += arrived;
        lifetimeDeparted += departed;

        /*
         * THE PEAK, not the end state - H2 in the design queue, and K1 beside
         * it. A mechanic read only at month 4,001 looks dead in any city that
         * has since grown out of it, and "it never fired" and "it fired and
         * finished" are the same reading.
         */
        if (stock > peakStock) peakStock = stock;
        if (spread > peakSpread) peakSpread = spread;
    }

    /* --------------------------------- reading --------------------------------- */

    /** Foreign money currently funding the city, in local money. */
    public double getStock() { return stock; }

    /** What would be here if it had all arrived. */
    public double getTarget() { return target; }

    /** The excess return that is pulling it, net of what the risk costs. */
    public double getSpread() { return spread; }

    public double getArrived()  { return arrived; }
    public double getDeparted() { return departed; }

    /** The month's flow, positive when money is coming in. */
    public double netFlow() { return arrived - departed; }

    /** Share of the pull that is the bank's deposit rate rather than the city's paper. */
    public double getDepositShare() { return depositShare; }

    public boolean isStopped()      { return panicUntil >= 0 && lastMonth <= panicUntil; }
    public String  getStopReason()  { return panicReason; }
    public int     getStopsSuffered() { return stopsSuffered; }

    /** The most that was ever here, and the widest the gap ever got. */
    public double getPeakStock()  { return peakStock; }
    public double getPeakSpread() { return peakSpread; }

    public double getLifetimeArrived()  { return lifetimeArrived; }
    public double getLifetimeDeparted() { return lifetimeDeparted; }

    /** Told the month, so isStopped() can be asked outside takeMonth(). */
    private int lastMonth;
    public void setMonth(int month) { this.lastMonth = month; }

    /** Months of the stop still to run, or 0. */
    public int stopMonthsLeft() { return Math.max(0, panicUntil - lastMonth); }

    /* --------------------------------- carrying --------------------------------- */

    public double[] toSaveArray() {
        return new double[] { stock, panicUntil, depositShare,
                lifetimeArrived, lifetimeDeparted, stopsSuffered, lastMonth };
    }

    public void restore(double[] saved) {
        if (saved == null || saved.length < 2) return;
        stock = Math.max(0, saved[0]);
        panicUntil = (int) Math.round(saved[1]);
        if (saved.length > 2) depositShare = saved[2];
        if (saved.length > 5) {
            lifetimeArrived = saved[3];
            lifetimeDeparted = saved[4];
            stopsSuffered = (int) Math.round(saved[5]);
        }
        if (saved.length > 6) lastMonth = (int) Math.round(saved[6]);
    }

    public void reset() {
        stock = target = spread = arrived = departed = 0;
        panicUntil = -1;
        panicReason = "";
        depositShare = .5;
        lifetimeArrived = lifetimeDeparted = 0;
        peakStock = peakSpread = 0;
        stopsSuffered = 0;
        lastMonth = 0;
    }

    /** Hot money, in the new unit. The spread is a rate and does not move. */
    public void redenominate(double scale) {
        stock  *= scale;
        target *= scale;
        arrived  *= scale;
        departed *= scale;
        lifetimeArrived  *= scale;
        lifetimeDeparted *= scale;
        peakStock *= scale;
        // ...and the floor the stock is measured against, or a reform would
        // move the threshold without moving the thing being thresholded.
        minStock *= scale;
    }

}
