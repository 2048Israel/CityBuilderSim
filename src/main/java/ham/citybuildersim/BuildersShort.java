package ham.citybuildersim;

/**
 * The builders short (FC1): what an order pays a point of work as the builders run out of points for the sites - a
 * premium that rises with the months of work queued at the pace the builders leave the sites - and, since FC2, the
 * foreign crews always on offer beside them: their price for a point, the months past which the city's overflow goes
 * to them, their month and what the city's spare points sell for abroad. Pure arithmetic: BuildingManager reads it for
 * every price (orderPriceMultiplier(), nonMaterialPricePerPoint(), foreignPricePerPoint()), Game for the contractor's
 * month (foreignCrewsOffer()), and the screens and the harnesses call these functions and never copy them.
 *
 * WHY. Jerus's 2026-11-01 report on his own city: "in a sufficiently big city, on a specific sequence of events, a
 * city could end up, where there are no crews to build, they are all repairing. and it gets stuck, no way to fix it".
 * The month's repairs take the builders' points before the sites (Game.getBuildingOutput()), and when they take them
 * all nothing advances, the builders' own depot waits in the same queue for points the repairs never leave
 * (Construction.plan(): "the repairs take every crew it has"), and the price of a point did not read the queue, so the
 * orders kept coming at the old price. Now the price reads it - every order is dearer for the work already queued.
 *
 * WHY THE CONTRACTOR IS ALWAYS ON OFFER (FC2). FC1's contractor came only to a rescue: under FOREIGN_FLOOR (5%) of
 * the builders' month left for the sites, at FOREIGN_PRICE (2.0) the city's price, a quarter of the month
 * (FOREIGN_CAPACITY_SHARE), with no exchange rate in it. PW5's test town (2026-11-22) had its builders spend every
 * point on upkeep a little over the floor, and nothing came. Jerus, 2026-11-22: "make it a imported/exported good, if
 * currency is strong, it could be cheaper than regular construction, so make it so that the base cost is still in
 * domestic currency but the excess is in foreign currency". So a foreign point costs p + E x (p the city's own base
 * price, x an excess in the world's money at the world's price level, E the D$ the goods are struck at): an order's
 * work goes abroad only where the queue's premium is over E x / p, which is nothing up to the knee, so the city's
 * builders are never priced out; a strong D$ lowers the threshold and a weak one raises it, never past
 * premium(KNEE_MONTHS + SURE_MONTHS) (Jerus: "at a certain premium they do go for it"). The overflow past it is the
 * contractor's, whose month grows with the work so that the backlog settles at the threshold, at an excess that rises
 * with the month he takes (a supply curve, under CEILING); and the builders sell abroad the points no site took
 * while their queue is under the knee, at the world's base and their own excess (docs/queue/FC2.md).
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

    /* ===== THE FOREIGN CREWS, ALWAYS ON OFFER (FC2, 2026-11-22) =====
       A point the foreign crews build costs the city p (1 + r): p its own base price for a point (BuildingManager.
       basePricePerPoint()), r = E x / p, x = FOREIGN_EXCESS of the world's base price for the same work (the work's
       price at founding wages and level, carried at the world's level since: BuildingManager.worldPricePerPoint() is
       E times it). r is capped at premium(KNEE_MONTHS + SURE_MONTHS) at the curve's first point and rises with the
       points they take a month (excessAt()); the queue's work past thresholdMonths(r) at the city's own pace is theirs.
       A point the city's builders sell abroad fetches the world's base and the city's own excess, converted, capped the
       same way (exportShare()). */

    /**
     * The foreign crews' excess over a local builder's price for the same work at parity, as a share of the base, at
     * the curve's first point: 15%. Flying crews in adds up to a tenth of a whole job's cost (RLB, Hawaii 2014-15),
     * about a sixth of its work beside its material; an expatriate costs two to five times a local post (Mercer,
     * SHRM); a mobilisation bid past a tenth of a contract is held back (MoDOT 618); the World Bank's margin of
     * preference that lets a domestic contractor's bid compete with a foreign one on works is 7.5% (docs/queue/FC2-notes.md).
     */
    public static final double FOREIGN_EXCESS = 0.15;

    /**
     * The months of backlog past KNEE_MONTHS at which the overflow goes abroad whatever the currency (N, Jerus's
     * "at a certain premium they do go for it"): a year, the time an owner takes to tender work abroad and have the
     * winner start (REV-FC2, its own reason). A road resurfacing contract takes 270 days in Korea from the bidders'
     * documents to the winner starting work, among the quickest, and years in the slowest (Bosio and Djankov, World
     * Bank, 2020-02-12, from the Contracting with the Government study); FIDIC's Red Book lets the work commence up to 42
     * days after the letter of acceptance (1999, sub-clause 8.1). An owner who would wait a year past the builders' own
     * backlog has had the time to bring a foreign firm in, at whatever it asks. The cap it gives, premium(KNEE_MONTHS +
     * 12) = 0.16, a fifth of CEILING, is barely over FOREIGN_EXCESS: a D$ about 6% under par already sits at it, so from
     * par to the weakest D$ the threshold moves only from 20.5 months to 21 - a weak currency barely raises it, while a
     * strong one lowers it as far as the knee (docs/queue/FC2-notes.md).
     */
    public static final double SURE_MONTHS = 12;

    /**
     * The foreign crews' month, as a multiple of the builders' month at every post, at which their excess is half way
     * from its first point to CEILING: twice the city's own trade. Post-disaster demand surge, where the work is a
     * fraction to a few times the local trade, raised reconstruction costs 10-40% after Katrina and 20-50% after
     * earthquakes (Olsen and Porter, USGS 2011; WCEE 2012); this curve gives +22% at a quarter of the city's month,
     * +37% at one and +54% at three, and never passes CEILING, the most the city's own queue asks.
     */
    public static final double SUPPLY_HALF = 2.0;

    /** The cap on the foreign crews' excess share at the curve's first point: the premium at SURE_MONTHS past the knee. */
    public static double capShare() { return premium(KNEE_MONTHS + SURE_MONTHS); }

    /**
     * The foreign crews' excess share at the curve's first point: E x / p, x FOREIGN_EXCESS of the world's base price
     * for the work, so FOREIGN_EXCESS times {@code worldPricePerPoint} (E times that base, in the city's money) over
     * {@code basePricePerPoint} (p), never over capShare() and never under nothing. capShare() with no base price to
     * read (nothing standing and nothing on site: the threshold stays reachable); a NaN reads as that.
     */
    public static double excessShare(double worldPricePerPoint, double basePricePerPoint) {
        double r = FOREIGN_EXCESS * worldPricePerPoint / basePricePerPoint;
        if (!(basePricePerPoint > 0) || Double.isNaN(r)) return capShare();
        return Math.max(0, Math.min(capShare(), r));
    }

    /**
     * ...and the city's own excess when its builders sell a point abroad, as a share of the world's base, by the same
     * rule turned round: FOREIGN_EXCESS of the city's base price p in the city's money, over E times the world's base,
     * capped at capShare(); capShare() with no world price to read.
     */
    public static double exportShare(double worldPricePerPoint, double basePricePerPoint) {
        double r = FOREIGN_EXCESS * basePricePerPoint / worldPricePerPoint;
        if (!(worldPricePerPoint > 0) || Double.isNaN(r)) return capShare();
        return Math.max(0, Math.min(capShare(), r));
    }

    /**
     * The months queued at the city's own pace past which an order's work goes abroad: where premium() reaches the
     * share {@code r} - KNEE_MONTHS + HALF_MONTHS sqrt(r / (CEILING - r)), premium() turned round. KNEE_MONTHS for a
     * share of nothing; never under it, whatever the currency, and never past KNEE_MONTHS + SURE_MONTHS for a share
     * excessShare() gives.
     */
    public static double thresholdMonths(double r) {
        if (!(r > 0)) return KNEE_MONTHS;
        if (r >= CEILING) return Double.POSITIVE_INFINITY;
        return KNEE_MONTHS + HALF_MONTHS * Math.sqrt(r / (CEILING - r));
    }

    /**
     * The queue's work past the threshold, in points: what the city's own builders hold over {@code thresholdMonths}
     * of their month for the sites ({@code sitesAtEveryPost}), nothing under it. All of it with no month for the sites.
     */
    public static double overflow(double queueAtHome, double sitesAtEveryPost, double thresholdMonths) {
        double queue = Double.isNaN(queueAtHome) ? 0 : Math.max(0, queueAtHome);
        if (!(sitesAtEveryPost > 0)) return queue;
        return Math.max(0, queue - thresholdMonths * sitesAtEveryPost);
    }

    /**
     * The foreign crews' month, in points, for {@code work} on their book: enough crews to build it in the threshold's
     * months - so the work abroad waits no longer than the work at home at the threshold, and the backlog read at both
     * paces settles there - and never under the builders' largest depot ({@code floorPoints}), so a depot is built in a
     * month (FC1's rule); never more than the work, never negative.
     */
    public static double foreignMonth(double work, double thresholdMonths, double floorPoints) {
        if (!(work > 0)) return 0;
        double month = thresholdMonths > 0 && Double.isFinite(thresholdMonths) ? work / thresholdMonths : work;
        return Math.min(work, Math.max(month, Math.max(0, floorPoints)));
    }

    /**
     * The foreign crews' excess share at {@code foreignMonth} points a month (the supply curve): {@code firstShare} (the
     * curve's first point, excessShare()) and the rest of the way to CEILING times m / (1 + m), m their month over
     * SUPPLY_HALF of the builders' month at every post ({@code buildersMonth}). Rising with their month, never past
     * CEILING; firstShare exactly at nothing; CEILING for a month with no builders' month to read it against.
     */
    public static double excessAt(double firstShare, double foreignMonth, double buildersMonth) {
        double first = Math.max(0, Math.min(CEILING, Double.isNaN(firstShare) ? 0 : firstShare));
        if (!(foreignMonth > 0)) return first;
        if (!(buildersMonth > 0)) return CEILING;
        double m = foreignMonth / (SUPPLY_HALF * buildersMonth);
        if (Double.isInfinite(m)) return CEILING;
        return Math.min(CEILING, first + (CEILING - first) * m / (1 + m));
    }

    /**
     * How much of an order of {@code n} buildings (at least one) stands within {@code thresholdMonths}, in buildings
     * (REV-FC2, the second review): the building i (from 0) takes the months {@code fromMonths + i * stepMonths} to the
     * next step queued, and its work past the threshold goes abroad point for point, so the building the threshold falls
     * in is split - its points within it at home, the rest abroad. meanShare() prices the part at home at the premium and
     * the rest at the foreign crews' share, and the order hands the rest to the crews when it is billed
     * (BuildingManager.abroadOfOrder()), one rule for both. WHY: FC2's whole buildings kept a building that began within
     * the threshold at home however far its points ran past, and PW5's Substation, 132,000 points, ordered so, held the
     * queue at home years past the threshold, every order after it abroad until the city's builders had worked it down.
     * All of it with no step or an endless threshold, none from a queue at or past it; a NaN queue reads as nothing.
     */
    public static double homeUnits(double fromMonths, double stepMonths, long n, double thresholdMonths) {
        long count = Math.max(1, n);
        double from = Double.isNaN(fromMonths) ? 0 : fromMonths;
        if (!(from < thresholdMonths)) return 0;
        if (!(stepMonths > 0) || thresholdMonths == Double.POSITIVE_INFINITY) return count;
        return Math.min(count, (thresholdMonths - from) / stepMonths);
    }

    /**
     * The mean share an order of {@code n} buildings carries over its base price with the foreign crews on offer: the
     * building i (from 0) stands in {@code fromMonths + i * stepMonths} months queued (meanPremium()); its work within
     * {@code thresholdMonths} is the city's own, at premium(), and past it abroad, at {@code foreignShare} (homeUnits():
     * the building the threshold falls in is split between them).
     * The first k at home are meanPremium(fromMonths, stepMonths, k), exact or the integral as it says. With no step
     * (zero, negative or NaN) every building stands where the first does; with a queue already endless all are abroad,
     * unless the threshold is endless too (no foreign crews at all: every building at home, FC1's meanPremium()).
     */
    public static double meanShare(double fromMonths, double stepMonths, long n, double thresholdMonths, double foreignShare) {
        long count = Math.max(1, n);
        double abroad = Math.max(0, Math.min(CEILING, Double.isNaN(foreignShare) ? 0 : foreignShare));
        double from = Double.isNaN(fromMonths) ? 0 : fromMonths;
        double home = homeUnits(fromMonths, stepMonths, n, thresholdMonths);
        if (home >= count) return meanPremium(from, stepMonths, count);
        if (!(home > 0)) return abroad;
        // ...the whole buildings at home at BA1's premium each, the one the threshold splits at its own for its part at home.
        long whole = (long) Math.floor(home);
        double part = home - whole;
        double atHome = (whole > 0 ? whole * meanPremium(from, stepMonths, whole) : 0)
                + (part > 0 ? part * premium(from + whole * stepMonths) : 0);
        return clamp((atHome + (count - home) * abroad) / count);
    }

    /** ...and the multiple an order's non-material price is struck at: one and meanShare(). */
    public static double multiplier(double fromMonths, double stepMonths, long n, double thresholdMonths, double foreignShare) {
        return 1 + meanShare(fromMonths, stepMonths, n, thresholdMonths, foreignShare);
    }
}
