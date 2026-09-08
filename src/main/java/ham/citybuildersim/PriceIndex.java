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
 * inflation while eating worse. The basket is fixed at founding and priced
 * every month afterwards.
 *
 * THE BASKET IS MEASURED, NOT INVENTED. The weights come from what this game's
 * households actually spent in the month the index was based - food against
 * rent - rather than from a constant somebody picked. A city whose families
 * spend two thirds of their money on food has a food-weighted index, and it
 * should, because that is whose cost of living this is.
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

    private int shoppingMonths;

    private double baseFood, baseRent;
    private double foodWeight = .5, rentWeight = .5;
    private boolean based;

    private double index = 1.0;
    private final double[] history = new double[WINDOW];
    private int monthsSeen;

    /**
     * Prices the basket for the month.
     *
     * @param shelfPrice what a unit costs in the shops
     * @param rentPrice  what a home costs to rent
     * @param foodSpend  what households spent on food this month
     * @param rentSpend  what they paid in rent
     */
    public void takeMonth(double shelfPrice, double rentPrice,
                          double foodSpend, double rentSpend) {

        if (shelfPrice <= MIN_BASE || rentPrice <= MIN_BASE) return;

        if (!based) {
            /*
             * THE MONTH THE BASKET IS FIXED. Not month one - a city one month
             * old has no households, no rent and no sales, and basing an index
             * on that would divide the whole game by a rounding error. The
             * first month with real spending in it is the base, and everything
             * afterwards is measured against what a family paid then.
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
             */
            double food = Math.max(0, foodSpend);
            double rent = Math.max(0, rentSpend);
            if (food <= MIN_BASE || rent <= MIN_BASE) return;
            if (++shoppingMonths < SETTLING_MONTHS) return;
            double total = food + rent;
            baseFood = shelfPrice;
            baseRent = rentPrice;
            foodWeight = food / total;
            rentWeight = 1 - foodWeight;
            based = true;
            index = 1.0;
        }

        index = foodWeight * (shelfPrice / baseFood)
              + rentWeight * (rentPrice / baseRent);

        // A ring of the last thirteen readings; the oldest is a year ago.
        history[monthsSeen % WINDOW] = index;
        monthsSeen++;
    }

    /** The basket now, against the basket at founding. 1.0 is no change. */
    public double getIndex() { return index; }

    public boolean isBased() { return based; }

    /** How the basket is split. Fixed at the base month, never re-weighted. */
    public double getFoodWeight() { return foodWeight; }
    public double getRentWeight() { return rentWeight; }

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

    /* -------------------------------- carrying -------------------------------- */

    public double[] toSaveArray() {
        double[] out = new double[5 + WINDOW];
        out[0] = based ? 1 : 0;
        out[1] = baseFood;
        out[2] = baseRent;
        out[3] = foodWeight;
        out[4] = monthsSeen;
        // shoppingMonths rides in the based flag: once based it is irrelevant,
        // and a save taken before basing restarts the settling period, which is
        // the right answer for a city that has not shopped for two years yet.
        System.arraycopy(history, 0, out, 5, WINDOW);
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
    }

    public void reset() {
        based = false;
        baseFood = baseRent = 0;
        foodWeight = rentWeight = .5;
        index = 1.0;
        monthsSeen = 0;
        shoppingMonths = 0;
        java.util.Arrays.fill(history, 0);
    }

    /** The basket's base prices, in the new unit.
     *
     * THE INDEX ITSELF MUST NOT MOVE. It is a ratio of today's basket to the
     * base year's, and a reform divides both. Scaling the bases and leaving the
     * index alone is what keeps that true - and getting it backwards would put
     * a hundredfold step in the city's inflation rate on the month of the
     * reform, which is the one number a currency reform must never touch.
     */
    public void redenominate(double scale) {
        baseFood *= scale;
        baseRent *= scale;
    }

}
