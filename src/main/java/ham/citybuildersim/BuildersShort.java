package ham.citybuildersim;

/**
 * The builders short (FC1): what an order pays a point of work as the builders run out of points for the sites - a
 * premium that rises with the months of work queued at the pace the builders leave the sites - and when a foreign
 * contractor takes the orders instead. Pure arithmetic: BuildingManager reads it for every price (orderPriceMultiplier(),
 * nonMaterialPricePerPoint()), Game for the contractor's month (foreignCrewsOffer()), and the screens and the harness
 * call these functions and never copy them.
 *
 * WHY. Jerus's 2026-11-01 report on his own city: "in a sufficiently big city, on a specific sequence of events, a
 * city could end up, where there are no crews to build, they are all repairing. and it gets stuck, no way to fix it".
 * The month's repairs take the builders' points before the sites (Game.getBuildingOutput()), and when they take them
 * all nothing advances, the builders' own depot waits in the same queue for points the repairs never leave
 * (Construction.plan(): "the repairs take every crew it has"), and the price of a point did not read the queue, so the
 * orders kept coming at the old price. Now the price reads it - every order is dearer for the work already queued - and
 * when the sites are left with none, a foreign contractor builds the builders' own depots first and the city climbs out.
 *
 * AN ORDER'S OWN BUILDINGS (BA1). With no cap on orders (BusinessInvestment.MAX_CONCURRENT_ORDERS and MAX_ORDER_MONTHS
 * went), an order of six hundred homes would take years of the queue at the price before it, every home at the price of
 * the first. Now each of its buildings is priced on the queue the ones before it leave, as each order of a run already is
 * (Game.buildRunInvoice()): an order of n pays the mean of the premium over the n queues it stands in
 * (meanPremium(), multiplier()). One building is priced exactly as FC1 priced it.
 */
public final class BuildersShort {

    private BuildersShort() { }

    /**
     * Months of work queued at the sites' pace at or under which a point carries no premium: the backlog the builders
     * themselves expand at (BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING); three city-months in four in the
     * 16-seed ensemble sit under it (median 2.0 months, p75 9.2, p90 20.9, p99 72).
     */
    public static final double KNEE_MONTHS = BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING;

    /** Months past the knee at which the premium is half its ceiling. */
    public static final double HALF_MONTHS = 24;

    /** The most the premium adds to the price of a point's work, as a share of it: what an order pays when the builders have no points left for sites. */
    public static final double CEILING = 0.8;

    /** The share of the builders' month (every post offered) under which what the repairs leave the sites counts as none, and the foreign contractor takes the orders. */
    public static final double FOREIGN_FLOOR = 0.05;

    /** The contractor's price for a point, a multiple of the city's price before any premium: above the ceiling's 1 + CEILING, so no city prefers it. */
    public static final double FOREIGN_PRICE = 2.0;

    /** The contractor's month as a share of the builders' month, every post offered; never less than the points to build the builders' largest depot, so a depot can be built in a month. */
    public static final double FOREIGN_CAPACITY_SHARE = 0.25;

    /**
     * The months of work queued at the pace the sites get: queue over sites. Infinite when the sites get no points at
     * all, even with nothing queued; nothing when the queue is empty and the sites have some. A NaN reads as nothing.
     */
    public static double monthsQueued(double queuePoints, double sitesAtEveryPost) {
        double queue = Double.isNaN(queuePoints) ? 0 : queuePoints;
        double sites = Double.isNaN(sitesAtEveryPost) ? 0 : sitesAtEveryPost;
        if (!(sites > 0)) return Double.POSITIVE_INFINITY;
        if (!(queue > 0)) return 0;
        return queue / sites;
    }

    /**
     * What a point's work carries over its price at that many months queued, as a share of it: none at or under
     * KNEE_MONTHS, then CEILING * x^2 / (1 + x^2) for x the months past the knee over HALF_MONTHS - no slope at the
     * knee, half the ceiling at HALF_MONTHS past it, the ceiling itself with the sites at nothing, never above it.
     */
    public static double premium(double months) {
        if (Double.isNaN(months) || months <= KNEE_MONTHS) return 0;
        if (Double.isInfinite(months)) return CEILING;
        double x = (months - KNEE_MONTHS) / HALF_MONTHS;
        // x^2 / (1 + x^2) as 1 / (1 + 1/x^2): the same curve, and the ceiling rather than NaN where x^2 overflows.
        return Math.min(CEILING, CEILING / (1 + 1 / (x * x)));
    }

    /**
     * Whether the foreign contractor takes the orders: the builders' points for sites, every post offered, are under
     * FOREIGN_FLOOR of their whole month, every post offered. A city whose builders have no month at all (the
     * founding's first) is not trapped: nobody's repairs took anything.
     */
    public static boolean foreignTakesOrders(double sitesAtEveryPost, double outputAtEveryPost) {
        return outputAtEveryPost > 0 && sitesAtEveryPost < FOREIGN_FLOOR * outputAtEveryPost;
    }

    /** What an order's non-material price is multiplied by: the contractor's price while it takes orders, else one and the premium at that many months queued. */
    public static double multiplier(double months, boolean foreign) {
        return foreign ? FOREIGN_PRICE : 1 + premium(months);
    }

    /** The largest order whose premium is the exact mean over its buildings (meanPremium()); a larger one reads the curve's integral, the same figure to the precision meanPremium() states. */
    public static final long EXACT_SUM_MOST = 64;

    /**
     * The mean premium over an order of {@code n} buildings, each priced on the queue the ones before it leave (BA1): the
     * building i (from 0) stands in {@code fromMonths + i * stepMonths} months queued, where {@code stepMonths} is one
     * building's points over the sites' month. Exactly premium(fromMonths) for one building, for no step (zero, negative
     * or NaN: nothing leaves the queue between them) and for a queue already endless (then CEILING).
     *
     * Up to EXACT_SUM_MOST buildings the sum is exact. Past it, the midpoint integral of the curve from half a step
     * before the first building to half a step after the last, over n steps: the antiderivative of premium() is nothing
     * up to the knee and CEILING * HALF_MONTHS * (x - atan x) past it, x the months past the knee over HALF_MONTHS
     * (continuous at the knee, where the curve has no slope). MEASURED against the exact sum (orders of 65 to 100,000,
     * queues of nothing to 2,000 months): within a part in a million of the premium for a step under a tenth of a month
     * (a home at a city's pace), within 3e-5 up to two months, 3.4e-4 at 22 months (a materials plant behind a founding
     * city's yard), 2.8e-3 at a hundred - a premium that never passes CEILING (0.8). Never above CEILING, never below
     * nothing, never NaN.
     */
    public static double meanPremium(double fromMonths, double stepMonths, long n) {
        if (n <= 1 || Double.isNaN(fromMonths) || Double.isNaN(stepMonths) || !(stepMonths > 0)
                || Double.isInfinite(fromMonths)) {
            return premium(fromMonths);
        }
        // ...an endless step puts every building after the first at the ceiling: nothing to integrate.
        if (Double.isInfinite(stepMonths)) return clamp((premium(fromMonths) + (n - 1) * CEILING) / n);
        if (n <= EXACT_SUM_MOST) {
            double sum = 0;
            for (long i = 0; i < n; i++) sum += premium(fromMonths + i * stepMonths);
            return clamp(sum / n);
        }
        double top = fromMonths + (n - 0.5) * stepMonths;
        if (Double.isInfinite(top)) return CEILING;
        double area = antiderivative(top) - antiderivative(fromMonths - 0.5 * stepMonths);
        return clamp(area / (n * stepMonths));
    }

    /** The area under premium() from the knee to that many months queued; nothing at or under the knee. */
    private static double antiderivative(double months) {
        if (!(months > KNEE_MONTHS)) return 0;
        double x = (months - KNEE_MONTHS) / HALF_MONTHS;
        return CEILING * HALF_MONTHS * (x - Math.atan(x));
    }

    private static double clamp(double premium) {
        return Double.isNaN(premium) ? 0 : Math.max(0, Math.min(CEILING, premium));
    }

    /** ...an order of {@code n} buildings' multiplier (BA1): the contractor's price while it takes orders, else one and meanPremium(); multiplier(fromMonths, foreign) exactly for one. */
    public static double multiplier(double fromMonths, double stepMonths, long n, boolean foreign) {
        return foreign ? FOREIGN_PRICE : 1 + meanPremium(fromMonths, stepMonths, n);
    }

    /** The contractor's month, in points: FOREIGN_CAPACITY_SHARE of the builders' month, every post offered, and never less than the builders' largest depot, so one can be built in a month. Never negative. */
    public static double foreignCapacity(double outputAtEveryPost, double largestDepotPoints) {
        return Math.max(0, Math.max(FOREIGN_CAPACITY_SHARE * outputAtEveryPost, largestDepotPoints));
    }
}
