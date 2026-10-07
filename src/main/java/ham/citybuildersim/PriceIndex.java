package ham.citybuildersim;

/**
 * What a month costs a household, against what it cost at founding.
 *
 * WHY THIS EXISTS. LabourMarket.updateCostOfLiving() was being handed the
 * EXCHANGE RATE as a stand-in for a price index - a placeholder written in
 * phase 2 and flagged as one, on the reasoning that the world's own prices do
 * not move so the rate is the only thing changing what an import costs. That
 * was true while imports were the only price in the game that moved. It stopped
 * being true the moment the shelf price became cost-plus, and it was never true
 * of rent, which follows the unskilled wage and therefore follows wages
 * chasing... the exchange rate. A loop with a placeholder in it.
 *
 * A REAL INDEX IS A FIXED BASKET, PRICED REPEATEDLY. That is the whole idea and
 * it is the part people get wrong: you do not re-weight as spending shifts,
 * because then a household that switched to cheaper food would show no
 * inflation while eating worse. The basket is fixed - after SETTLING_MONTHS
 * of real shopping, not at founding (see there) - and priced every month
 * afterwards, for REBASE_MONTHS; then it is struck again on what the city
 * spends by then and CHAINED to the old one, so the level runs on unbroken
 * (0.7.43; for good until then - see THE BASKET IS CHAINED below).
 *
 * THE BASKET IS MEASURED, NOT INVENTED. The weights come from what this game's
 * households actually spent - over the trailing twelve months since 0.7.43,
 * in the base month alone until then - rather than from a constant somebody
 * picked. A city whose families spend two thirds of their money on food has
 * a food-weighted index, and it should, because that is whose cost of living
 * this is.
 *
 * @author Jerus
 */
public class PriceIndex {

    /** Months of index kept, so a year-on-year rate can be struck. */
    public static final int WINDOW = 13;

    /** Below this the basket is not worth pricing - a city with no shops. */
    public static final double MIN_BASE = 1e-9;

    /**
     * Months of real shopping before the basket is fixed.
     *
     * A CPI's base period has to be a period the city actually lived through
     * normally, and a city three months old is not that. Based too early the
     * weights came out 4% food and 96% rent - because a founding city has
     * hundreds of homes paying rent and a handful of customers - and a basket
     * that is 96% rent is a basket that does not notice food.
     *
     * That is not a cosmetic error. The shelf price went from 0.30 to 0.54 in
     * the run that produced those weights and the index recorded 0.0%
     * inflation, so wages never chased anything, so nothing in the domestic
     * economy absorbed the cost of imports - and the whole adjustment fell on
     * the currency, which ran to its 4.0 ceiling and stayed there. A mis-based
     * index does not make the numbers slightly wrong; it makes a different
     * economy.
     */
    public static final int SETTLING_MONTHS = 24;

    /* ======================================================================
       WHAT IS IN THE BASKET (0.7.43; the project's spec-inflation.md 2.8)

       Groceries and rent until 0.7.43: two prices, and neither of them read
       anybody's money, while the two that did - a meal out and a piece over
       a luxury counter - were not in the index at all. Five components now,
       each priced every month against its price at the link:

           index = link x sum over k of weight_k x price_k / base_k

       the shelf price, the average rent actually paid, a meal
       (Restaurants.getSellPrice()), a luxury piece (LuxuryRetail
       .getSellPrice()), and the services a household is billed for - a fee
       index of its own over the clinic's fee for a general visit, the
       tuition for a seat, the fare for a ride and the bank's account fee,
       each weighted by its share of what the households paid in fees. A
       component with no price this month holds its last one.

       THE WEIGHTS are the trailing twelve months of what the households
       spent on each (groceries and rent off the sectors' statements, meals
       and luxury off the households' purchases, the fee lines off their
       books), and luxury's is capped at LUXURY_WEIGHT_CAP with the rest
       spread pro rata over the others: honest weights made one research
       city's index 63% a watch, which is an import at the exchange rate
       times a margin, and not a cost of living.
       ====================================================================== */

    /** The components, in the order every array here keeps them. */
    public static final int GROCERIES = 0, RENT = 1, MEALS = 2, LUXURY = 3, SERVICES = 4;

    /** How many there are. */
    public static final int COMPONENTS = 5;

    /** Their names, for the year book and the screens. */
    public static final String[] COMPONENT_NAMES = { "groceries", "rent", "meals", "luxury", "services" };

    /** The fee lines inside SERVICES, in the order the fee arrays here keep them. */
    public static final int HEALTH_FEE = 0, TUITION = 1, FARE = 2, ACCOUNT_FEE = 3;

    /** How many there are. */
    public static final int FEE_LINES = 4;

    /** Months of spending a basket's weights are struck on: the trailing year. A one-month rebase was measured to give food a weight of nothing in a crash month. */
    public static final int WEIGHT_MONTHS = 12;

    /** Months between one basket and the next: ten years. */
    public static final int REBASE_MONTHS = 120;

    /** The most of the basket luxury may be, the excess spread pro rata over the rest: 15%. */
    public static final double LUXURY_WEIGHT_CAP = .15;

    private int shoppingMonths;

    /** The two-component basket a save from before 0.7.43 carried: read once, to strike the level the first chain-link starts from. */
    private double baseFood, baseRent;
    private double foodWeight = .5, rentWeight = .5;
    private boolean based;

    /** Each component's price at the link, its weight, its last price seen, and its price over its base this month. */
    private final double[] base = new double[COMPONENTS];
    private final double[] weight = new double[COMPONENTS];
    private final double[] last = new double[COMPONENTS];
    private final double[] relative = { 1, 1, 1, 1, 1 };

    /** The level the basket was linked at - 1 for a city's first - and the month it was. */
    private double link = 1;
    private int linkedAt;

    /** True on a save from before 0.7.43 until its first month links the basket. Saved as the marker's absence. */
    private boolean linkPending;

    /**
     * Each component's own level at the link, chained (0.7.45): what its
     * relative is multiplied by to give its price against founding, unbroken
     * across every link (getComponentLevel()). Bookkeeping for City History's
     * lines - nothing in the index reads it.
     */
    private final double[] componentLink = { 1, 1, 1, 1, 1 };

    /** Each fee line's price at the link, its share of the fees then, and its last price seen. */
    private final double[] feeBase = new double[FEE_LINES];
    private final double[] feeShare = new double[FEE_LINES];
    private final double[] feeLast = new double[FEE_LINES];

    /** The trailing year of what the households spent on each component, and on each fee line, as rings; how many months they hold. */
    private final double[][] spendRing = new double[WEIGHT_MONTHS][COMPONENTS];
    private final double[][] feeRing = new double[WEIGHT_MONTHS][FEE_LINES];
    private int ringMonths;

    private double index = 1.0;
    private final double[] history = new double[WINDOW];
    private int monthsSeen;

    /* ======================================================================
       THE HIGH AND LOW WATER MARKS

       Jerus, 2026-09-14, after the price index turned out to be reported
       nowhere except at month four thousand: "something as well that stores
       the highest price index and lowest".

       WHY A CITY NEEDS THEM. Every price figure this project has ever quoted
       is the level on the last month of the run, and the level does not sit
       still: measured across sixteen seeds, cities that FINISH between 0.84
       and 1.26 times founding prices peak at a median of 1.62 and as high as
       3.78 on the way, and one seed swung +298% inside ten years. A seed that
       ends at 0.98 spent a century near 2.0 and nothing anywhere said so.

       An endpoint is one sample of a path that swings sixty percent. These two
       numbers are the path's shape in the only two readings that survive not
       having been there - and they are the cheapest possible instrument, being
       two comparisons a month.

       CARRIED, because they are a record and not a derivation. Nothing in the
       state a month ended in can reconstruct what the level was in month 2,950.
       See claude/the-cities-that-empty-out.md.
       ====================================================================== */

    private double peak = 1.0, trough = 1.0;
    private int peakMonth, troughMonth;

    /**
     * Prices the basket for the month: groceries and rent alone, the two
     * components the index had until 0.7.43 - meals, luxury and services
     * unpriced and unspent on. For a caller with nothing else to say.
     *
     * @param shelfPrice what a unit costs in the shops
     * @param rentPrice  what a home costs to rent
     * @param foodSpend  what households spent on food this month
     * @param rentSpend  what they paid in rent
     * @param month      the city's month, so the marks can say WHEN
     */
    public void takeMonth(double shelfPrice, double rentPrice,
                          double foodSpend, double rentSpend, int month) {
        takeMonth(new double[] { shelfPrice, rentPrice, 0, 0, 0 },
                new double[] { foodSpend, rentSpend, 0, 0, 0 },
                new double[FEE_LINES], new double[FEE_LINES], month);
    }

    /**
     * Prices the basket for the month.
     *
     * @param prices    each component's price this month, in COMPONENTS order;
     *                  SERVICES is not read - its price is the fee index
     *                  struck here from `fees`
     * @param spends    what the households spent on each this month; SERVICES
     *                  is not read - it is the sum of `feeSpends`
     * @param fees      each fee line's price this month, in FEE_LINES order
     * @param feeSpends what the households paid on each fee line this month
     * @param month     the city's month, so the marks can say WHEN
     */
    public void takeMonth(double[] prices, double[] spends, double[] fees, double[] feeSpends, int month) {

        double shelfPrice = prices[GROCERIES], rentPrice = prices[RENT];
        if (shelfPrice <= MIN_BASE || rentPrice <= MIN_BASE) return;

        // The month's prices, each held at its last where it has none.
        for (int k = 0; k < COMPONENTS; k++) {
            if (k != SERVICES && prices[k] > MIN_BASE && Double.isFinite(prices[k])) last[k] = prices[k];
        }
        for (int j = 0; j < FEE_LINES; j++) {
            if (fees[j] > MIN_BASE && Double.isFinite(fees[j])) feeLast[j] = fees[j];
        }
        // ...and the month's spending, into the trailing year.
        double[] spent = spendRing[ringMonths % WEIGHT_MONTHS];
        double[] feesSpent = feeRing[ringMonths % WEIGHT_MONTHS];
        double services = 0;
        for (int j = 0; j < FEE_LINES; j++) {
            feesSpent[j] = Math.max(0, feeSpends[j]);
            services += feesSpent[j];
        }
        for (int k = 0; k < COMPONENTS; k++) spent[k] = k == SERVICES ? services : Math.max(0, spends[k]);
        ringMonths++;

        if (!based) {
            /*
             * THE MONTH THE BASKET IS FIXED. Not month one - a city one month
             * old has no households, no rent and no sales, and basing an index
             * on that would divide the whole game by a rounding error. The
             * base is the SETTLING_MONTHS-th month with real spending in it
             * (see there; it was the first until the settling went in), and
             * everything afterwards is measured against what a family paid
             * then.
             */
            /*
             * BOTH HALVES, NOT EITHER. Requiring only a non-zero total based
             * the basket on the first month with rent in it - which is before
             * the first month with SALES in it - and produced a basket of 0%
             * food and 100% rent.
             *
             * That is not merely a bad weighting, it is a closed loop with no
             * input: rent follows the unskilled wage, the wage follows the cost
             * of living, the cost of living follows the index, and the index
             * was rent. Nothing outside it could move it, so it sat at exactly
             * 1.000 with 0.0% inflation for four thousand months while the
             * shelf price went from 0.30 to 0.54 in plain sight.
             *
             * A basket has to be based on a month the city actually shopped in.
             * The weights are the trailing year's (0.7.43), and a component
             * nobody has bought yet - no kitchens, no counters - is in it at
             * nothing until the next basket.
             */
            double food = Math.max(0, spends[GROCERIES]);
            double rent = Math.max(0, spends[RENT]);
            if (food <= MIN_BASE || rent <= MIN_BASE) return;
            if (++shoppingMonths < SETTLING_MONTHS) return;
            based = true;
            relink(month, 1.0);
            peak = trough = 1.0;
            peakMonth = troughMonth = month;
        } else if (linkPending) {
            /*
             * THE FIRST MONTH OF A SAVE FROM BEFORE 0.7.43: the two-component
             * basket it carried strikes this month's level, and the new
             * basket is linked at it - so the level runs on unbroken, and the
             * thirteen-month ring keeps the year's rate across the link. The
             * weights are the trailing year's, which on such a save is this
             * month alone.
             */
            double level = foodWeight * (baseFood > MIN_BASE ? shelfPrice / baseFood : 1)
                    + rentWeight * (baseRent > MIN_BASE ? rentPrice / baseRent : 1);
            relink(month, level > MIN_BASE && Double.isFinite(level) ? level : index);
        } else if (month - linkedAt >= REBASE_MONTHS) {
            // A new basket every REBASE_MONTHS, linked at the level the old
            // one strikes this month (THE BASKET IS CHAINED).
            relink(month, levelOnThisBasket());
        }

        index = levelOnThisBasket();

        /*
         * ...and the marks, which only mean anything once the basket is based.
         * Before that the index is a placeholder 1.0 and recording it would
         * stamp both marks on a city that has not shopped yet.
         */
        if (index > peak)   { peak = index;   peakMonth = month; }
        if (index < trough) { trough = index; troughMonth = month; }

        // A ring of the last thirteen readings; the oldest is a year ago.
        history[monthsSeen % WINDOW] = index;
        monthsSeen++;
    }

    /* ======================================================================
       THE BASKET IS CHAINED (0.7.43)

       A fixed basket is right for a decade and wrong for a century: a city
       whose families came to eat out half their meals is not measured by
       what it ate at founding. So the basket is struck again every
       REBASE_MONTHS on what was spent over the trailing year, each component
       based at its price that month, and the level the old basket reached is
       carried as the LINK: the new basket opens at exactly that level, so the
       index never jumps, and the ring of thirteen readings behind the year's
       rate is the ring of the level, so the rate does not jump either. A
       save from before 0.7.43 is linked the same way on its first month.
       ====================================================================== */

    /** The level this month on the basket in force: the link times each component's price over its base, weighted. */
    private double levelOnThisBasket() {
        double sum = 0, weights = 0;
        for (int k = 0; k < COMPONENTS; k++) {
            double price = k == SERVICES ? feeLevel() : last[k];
            relative[k] = base[k] > MIN_BASE ? price / base[k] : 1;
            if (!(weight[k] > 0)) continue;
            sum += weight[k] * relative[k];
            weights += weight[k];
        }
        return weights > 0 ? link * sum / weights : link;
    }

    /** The services price: each fee line's price over its price at the link, weighted by its share of the fees then - 1 at the link, and with no fees, for ever. */
    private double feeLevel() {
        double sum = 0, shares = 0;
        for (int j = 0; j < FEE_LINES; j++) {
            if (!(feeShare[j] > 0) || !(feeBase[j] > MIN_BASE)) continue;
            sum += feeShare[j] * feeLast[j] / feeBase[j];
            shares += feeShare[j];
        }
        return shares > 0 ? sum / shares : 1;
    }

    /**
     * Strikes a new basket: the weights from the trailing year's spending
     * (this month's alone with no year yet), luxury's capped, each component
     * based at its price now - and the link, the level the basket opens at.
     */
    private void relink(int month, double level) {
        /*
         * EACH COMPONENT'S OWN LEVEL RUNS ON (0.7.45): its level on the old
         * basket this month - its link times its price over its old base -
         * is where it opens on the new one, read before anything below moves
         * a base. A component the old basket did not price enters at the
         * level the basket is linked at; a save from before 0.7.43 links its
         * groceries and rent from the two-component basket it carried, and
         * the rest at the link. Bookkeeping: nothing in the index reads it.
         */
        for (int k = 0; k < COMPONENTS; k++) {
            double price = k == SERVICES ? feeLevel() : last[k];
            componentLink[k] = based && !linkPending && base[k] > MIN_BASE && price > MIN_BASE
                    ? componentLink[k] * price / base[k] : level;
        }
        if (linkPending) {
            if (baseFood > MIN_BASE && last[GROCERIES] > MIN_BASE) componentLink[GROCERIES] = last[GROCERIES] / baseFood;
            if (baseRent > MIN_BASE && last[RENT] > MIN_BASE) componentLink[RENT] = last[RENT] / baseRent;
        }
        double[] spend = new double[COMPONENTS];
        double[] feeSpend = new double[FEE_LINES];
        int months = Math.min(ringMonths, WEIGHT_MONTHS);
        for (int m = 0; m < months; m++) {
            for (int k = 0; k < COMPONENTS; k++) spend[k] += spendRing[m][k];
            for (int j = 0; j < FEE_LINES; j++) feeSpend[j] += feeRing[m][j];
        }
        // The fee lines first: a line with no price is in the services basket at nothing.
        double fees = 0;
        for (int j = 0; j < FEE_LINES; j++) {
            if (!(feeLast[j] > MIN_BASE)) feeSpend[j] = 0;
            fees += feeSpend[j];
        }
        for (int j = 0; j < FEE_LINES; j++) {
            feeShare[j] = fees > 0 ? feeSpend[j] / fees : 0;
            feeBase[j] = feeLast[j];
        }
        // ...then the components: one with no price is in the basket at nothing.
        spend[SERVICES] = fees;
        for (int k = 0; k < COMPONENTS; k++) {
            if (k != SERVICES && !(last[k] > MIN_BASE)) spend[k] = 0;
            spend[k] = Math.max(0, spend[k]);
        }
        double others = 0;
        for (int k = 0; k < COMPONENTS; k++) if (k != LUXURY) others += spend[k];
        if (spend[LUXURY] > LUXURY_WEIGHT_CAP / (1 - LUXURY_WEIGHT_CAP) * others) {
            spend[LUXURY] = LUXURY_WEIGHT_CAP / (1 - LUXURY_WEIGHT_CAP) * others;
        }
        double total = others + spend[LUXURY];
        for (int k = 0; k < COMPONENTS; k++) {
            weight[k] = total > MIN_BASE ? spend[k] / total : (k == GROCERIES ? 1 : 0);
            base[k] = k == SERVICES ? 1 : last[k];
        }
        foodWeight = weight[GROCERIES];
        rentWeight = weight[RENT];
        baseFood = base[GROCERIES];
        baseRent = base[RENT];
        link = level;
        linkedAt = month;
        linkPending = false;
    }

    /** The dearest the basket has ever been, against founding. */
    public double getPeak()      { return peak; }
    /** ...and the month it happened. */
    public int getPeakMonth()    { return peakMonth; }
    /** The cheapest it has ever been. */
    public double getTrough()    { return trough; }
    public int getTroughMonth()  { return troughMonth; }

    /**
     * Peak over trough - how far the level has travelled, in one number.
     *
     * The reading that says whether an endpoint is worth quoting. A city at
     * 1.02 that has never left 0.95-1.10 and a city at 1.02 that went to 3.78
     * and came back are the same number and not the same place to live.
     */
    public double swing() { return trough > MIN_BASE ? peak / trough : 1; }

    /** The basket now, against the basket at founding. 1.0 is no change. */
    public double getIndex() { return index; }

    public boolean isBased() { return based; }

    /** How the basket is split: groceries and rent, as the basket in force weighs them - fixed at a link, re-weighted only at the next (0.7.43; never until then). */
    public double getFoodWeight() { return foodWeight; }
    public double getRentWeight() { return rentWeight; }

    /** One component's weight in the basket in force (COMPONENTS order). */
    public double getWeight(int component) { return weight[component]; }

    /** One component's price this month over its price at the link: 1 at the link, and for a component not in the basket. */
    public double getRelative(int component) { return relative[component]; }

    /** One component's price at the link (the services' fee index: 1). */
    public double getBase(int component) { return base[component]; }

    /** The level the basket in force was linked at: 1 for a city's first basket. */
    public double getLink() { return link; }

    /** The month the basket in force was linked: the base month, or the last chain-link. */
    public int getLinkedMonth() { return linkedAt; }

    /** One fee line's share of the services price (FEE_LINES order). */
    public double getFeeShare(int line) { return feeShare[line]; }

    /** True on a save from before 0.7.43 until its first month links the five-component basket. */
    public boolean isLinkPending() { return linkPending; }

    /**
     * One component's own price level against founding, chained across every
     * link (0.7.45; COMPONENTS order): its level at the link times its
     * relative - so a basket struck again does not reset it to 1, as the
     * relative does. 1 before the basket is based. Pure.
     */
    public double getComponentLevel(int component) { return componentLink[component] * relative[component]; }

    /** True when the basket in force had luxury's weight held at LUXURY_WEIGHT_CAP - what the households spent on it was more (0.7.45). Pure. */
    public boolean isLuxuryCapped() { return weight[LUXURY] > 0 && weight[LUXURY] >= LUXURY_WEIGHT_CAP * (1 - 1e-9); }

    /**
     * Inflation over the last twelve months.
     *
     * YEAR ON YEAR, NOT MONTH ON MONTH, and it matters here more than most
     * places: a single month in this game contains a construction order, a
     * harvest and a shipping bill, and the month-on-month rate is mostly those.
     * A year is the shortest window in which the number means "prices are
     * rising" rather than "something happened in March".
     */
    public double inflation() {
        if (monthsSeen <= WINDOW) return 0;
        double thenIdx = history[monthsSeen % WINDOW];
        if (thenIdx <= MIN_BASE) return 0;
        return index / thenIdx - 1;
    }

    /** True once there is a year of readings and the rate means anything. */
    public boolean hasRate() { return monthsSeen > WINDOW; }

    /**
     * How many more months before hasRate() (0.7.20): the settling months
     * still to come, then the readings after the basket is fixed - for the
     * header, which says when the first rate comes rather than printing a
     * placeholder as though it were a reading. A month with no shopping in
     * it does not count towards the settling, so before the shops open this
     * is the least it can be. Reads; changes nothing.
     */
    public int monthsUntilRate() {
        if (hasRate()) return 0;
        if (!based) return Math.max(0, SETTLING_MONTHS - shoppingMonths) + WINDOW;
        return WINDOW + 1 - monthsSeen;
    }

    /* -------------------------------- carrying -------------------------------- */

    /** The slots a save carried before the chained basket (0.7.43): the two-component basket, the ring, the marks and the settling count. */
    public static final int SLOTS_BEFORE_CHAIN = 5 + WINDOW + 5;

    /** What follows them in a save from 0.7.43 on, so an older array - which ends there - is told apart from one that goes on. */
    public static final double CHAIN_MARKER = 743;

    /** The chained basket's slots behind the marker as 0.7.43 wrote them: the link, its month, the pending flag, the bases, weights and last prices, the fee lines', and the trailing year. */
    private static final int CHAIN_TAIL_0743 = 4 + COMPONENTS * 3 + FEE_LINES * 3 + 1 + WEIGHT_MONTHS * (COMPONENTS + FEE_LINES);

    public double[] toSaveArray() {
        int tail = CHAIN_TAIL_0743 + COMPONENTS;
        double[] out = new double[SLOTS_BEFORE_CHAIN + tail];
        out[0] = based ? 1 : 0;
        out[1] = baseFood;
        out[2] = baseRent;
        out[3] = foodWeight;
        out[4] = monthsSeen;
        System.arraycopy(history, 0, out, 5, WINDOW);
        out[5 + WINDOW]     = peak;
        out[5 + WINDOW + 1] = peakMonth;
        out[5 + WINDOW + 2] = trough;
        out[5 + WINDOW + 3] = troughMonth;
        /*
         * THE SETTLING COUNT, AT THE END (0.7.12, round 2). It used to ride
         * in the based flag - irrelevant once based, and a save taken before
         * basing restarted the settling period. That made a city saved in its
         * first two years base its index two years later than the city it was
         * saved from, and the reload did not replay: the index, and every wage
         * indexed to it, parted a few months on. At the end so an older save,
         * which is shorter, still reads; it restarts the count as it always did.
         */
        out[5 + WINDOW + 4] = shoppingMonths;
        /*
         * ...AND THE CHAINED BASKET, BEHIND A MARKER (0.7.43): the link and
         * its month, whether it is still to come, each component's base,
         * weight and last price, each fee line's, and the trailing year of
         * spending its next weights are struck on - a flow, carried.
         */
        int i = SLOTS_BEFORE_CHAIN;
        out[i++] = CHAIN_MARKER;
        out[i++] = link;
        out[i++] = linkedAt;
        out[i++] = linkPending ? 1 : 0;
        for (int k = 0; k < COMPONENTS; k++) { out[i++] = base[k]; out[i++] = weight[k]; out[i++] = last[k]; }
        for (int j = 0; j < FEE_LINES; j++) { out[i++] = feeBase[j]; out[i++] = feeShare[j]; out[i++] = feeLast[j]; }
        out[i++] = ringMonths;
        for (int m = 0; m < WEIGHT_MONTHS; m++) {
            for (int k = 0; k < COMPONENTS; k++) out[i++] = spendRing[m][k];
            for (int j = 0; j < FEE_LINES; j++) out[i++] = feeRing[m][j];
        }
        // ...and each component's chained level at the link (0.7.45), after the ring.
        for (int k = 0; k < COMPONENTS; k++) out[i++] = componentLink[k];
        return out;
    }

    public void restore(double[] saved) {
        if (saved == null || saved.length < 5 + WINDOW) return;
        based = saved[0] > .5;
        baseFood = saved[1];
        baseRent = saved[2];
        foodWeight = saved[3];
        rentWeight = 1 - foodWeight;
        monthsSeen = (int) Math.round(saved[4]);
        System.arraycopy(saved, 5, history, 0, WINDOW);
        /*
         * The index itself is restruck on the first tick from prices the city
         * still has - but the HISTORY cannot be, and without it a reloaded city
         * reports zero inflation for a year however fast prices are moving. A
         * flow cannot be reconstructed from the state a month ended in.
         */
        if (monthsSeen > 0) index = history[(monthsSeen - 1) % WINDOW];

        /*
         * THE CHAINED BASKET (0.7.43), or - on an older save, which has no
         * marker - the two-component basket it carried, linked on the first
         * month it plays (linkPending): the five components priced from then
         * on, the level unbroken.
         */
        restoreChain(saved);

        /*
         * A save from before the marks existed has no tail. Opening them on the
         * restored index rather than on 1.0 is the honest answer: the city's
         * real high and low are unknowable from that save, and claiming it had
         * never been anywhere but today's level would be a made-up record.
         */
        if (saved.length < 5 + WINDOW + 4) {
            peak = trough = index;
            peakMonth = troughMonth = 0;
            return;
        }
        peak        = saved[5 + WINDOW];
        peakMonth   = (int) Math.round(saved[5 + WINDOW + 1]);
        trough      = saved[5 + WINDOW + 2];
        troughMonth = (int) Math.round(saved[5 + WINDOW + 3]);
        shoppingMonths = saved.length > 5 + WINDOW + 4
                ? (int) Math.round(saved[5 + WINDOW + 4]) : 0;
    }

    /** The chained basket behind the marker, or a link pending on an older save; the array is read whole or not at all. */
    private void restoreChain(double[] saved) {
        clearChain();
        boolean withLevels = saved.length == SLOTS_BEFORE_CHAIN + CHAIN_TAIL_0743 + COMPONENTS;
        if ((saved.length != SLOTS_BEFORE_CHAIN + CHAIN_TAIL_0743 && !withLevels)
                || saved[SLOTS_BEFORE_CHAIN] != CHAIN_MARKER) {
            linkPending = based;
            return;
        }
        int i = SLOTS_BEFORE_CHAIN + 1;
        link = saved[i++];
        linkedAt = (int) Math.round(saved[i++]);
        linkPending = saved[i++] > .5;
        for (int k = 0; k < COMPONENTS; k++) { base[k] = saved[i++]; weight[k] = saved[i++]; last[k] = saved[i++]; }
        for (int j = 0; j < FEE_LINES; j++) { feeBase[j] = saved[i++]; feeShare[j] = saved[i++]; feeLast[j] = saved[i++]; }
        ringMonths = (int) Math.round(saved[i++]);
        for (int m = 0; m < WEIGHT_MONTHS; m++) {
            for (int k = 0; k < COMPONENTS; k++) spendRing[m][k] = saved[i++];
            for (int j = 0; j < FEE_LINES; j++) feeRing[m][j] = saved[i++];
        }
        // Each component's chained level (0.7.45); a save from 0.7.43 or 0.7.44
        // has none, and its components run on from the level of the link.
        for (int k = 0; k < COMPONENTS; k++) {
            double level = withLevels ? saved[i++] : link;
            componentLink[k] = level > 0 && Double.isFinite(level) ? level : link;
        }
        // The relatives are this month's and are struck by the next; on the
        // basket in force they read as last struck.
        for (int k = 0; k < COMPONENTS; k++) {
            double price = k == SERVICES ? feeLevel() : last[k];
            relative[k] = base[k] > MIN_BASE ? price / base[k] : 1;
        }
    }

    /** Nothing linked, nothing spent: a basket still to be struck. */
    private void clearChain() {
        link = 1;
        linkedAt = 0;
        linkPending = false;
        java.util.Arrays.fill(base, 0);
        java.util.Arrays.fill(weight, 0);
        java.util.Arrays.fill(last, 0);
        java.util.Arrays.fill(relative, 1);
        java.util.Arrays.fill(feeBase, 0);
        java.util.Arrays.fill(feeShare, 0);
        java.util.Arrays.fill(feeLast, 0);
        java.util.Arrays.fill(componentLink, 1);
        for (double[] row : spendRing) java.util.Arrays.fill(row, 0);
        for (double[] row : feeRing) java.util.Arrays.fill(row, 0);
        ringMonths = 0;
    }

    public void reset() {
        peak = trough = 1.0;
        peakMonth = troughMonth = 0;
        based = false;
        baseFood = baseRent = 0;
        foodWeight = rentWeight = .5;
        index = 1.0;
        monthsSeen = 0;
        shoppingMonths = 0;
        java.util.Arrays.fill(history, 0);
        clearChain();
    }

    /** The basket's base prices, in the new unit.
     *
     * THE INDEX ITSELF MUST NOT MOVE. It is a ratio of today's basket to the
     * base year's, and a reform divides both. Scaling the bases and leaving the
     * index alone is what keeps that true - and getting it backwards would put
     * a hundredfold step in the city's inflation rate on the month of the
     * reform, which is the one number a currency reform must never touch.
     *
     * Every base and last price in money moves (0.7.43): the four components'
     * and the four fee lines'. The services' base is its fee index, 1 - a
     * ratio, which stays; the spending behind the weights is money and moves
     * with them, which leaves every weight it strikes where it was.
     */
    public void redenominate(double scale) {
        baseFood *= scale;
        baseRent *= scale;
        for (int k = 0; k < COMPONENTS; k++) {
            if (k == SERVICES) continue;
            base[k] *= scale;
            last[k] *= scale;
        }
        for (int j = 0; j < FEE_LINES; j++) {
            feeBase[j] *= scale;
            feeLast[j] *= scale;
        }
        for (double[] row : spendRing) for (int k = 0; k < COMPONENTS; k++) row[k] *= scale;
        for (double[] row : feeRing) for (int j = 0; j < FEE_LINES; j++) row[j] *= scale;
        // The marks are ratios like the index, and move for the same reason it
        // does: they do not. A reform divides the basket and its base together.
    }

}
