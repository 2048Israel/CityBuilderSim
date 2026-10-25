package ham.citybuildersim;

/**
 * What it takes to prepare a foothill site for a building, as builders' work: the earth a pad is cut and filled in
 * and the wall where it meets the slope above, priced from published unit rates and turned into construction
 * points at the builders' own price for a House. Pure arithmetic: LandManager measures how much foothill an order
 * has to prepare (foothillToPrepareSqFt()), Game.quoteBuild() prices it into the quote, and the points are owed by
 * the order's site like any work (BuildingsStacks.addPreparation()).
 *
 * WHY. spec-earth2-terrain 4.1 sets the shape - earth moved a g x W / 4 a m2 of pad, one wall the height of the
 * pad's drop - and leaves the rates and the class's grade to this batch (ET2). The rule it gave is that no
 * coefficient is laid on top: a foothill pad costs what its earthworks cost. Written as dollars, though, the figure
 * would have drifted from the game's own prices, which are founding-dollar figures in buildings.json struck at the
 * expected price level and with the builders' wages (BuildingManager.nonMaterialCost()); so the work is turned into
 * points once, against the House's founding price a point, and the money for those points is the builders' price
 * for house work today. It keeps up with wages as every building's labour does, and drifts with nothing else.
 */
public final class SitePreparation {

    private SitePreparation() { }

    /**
     * The foothill class's mean grade (10% to 30%), 19%: fitted here from the study's measured class shares by an
     * exponential fall of land with grade between the class edges - 17.9% to 19.7% at every one of nine sites
     * (Bogota, Medellin, La Paz, Hong Kong, Wellington, Denver, Rio, Kathmandu, and NASADEM's 55-60 N tiles); the
     * study's per-tile histograms were not kept in the repository.
     */
    public static final double MEAN_FOOTHILL_GRADE = 0.19;

    /** The width of a pad across the contours, metres: one plot (30 m). */
    public static final double PAD_WIDTH_M = 30;

    /** Earth moved a m2 of pad, m3: balanced cut and fill on a pad W wide on a grade g moves g x W / 4 (spec 4.1), 1.425. */
    public static final double EARTH_M3_PER_M2 = MEAN_FOOTHILL_GRADE * PAD_WIDTH_M / 4;

    /** Wall face a m2 of pad, m2: one wall the height of the pad's drop, g m2 of face per m2 of pad (spec 4.1). */
    public static final double WALL_M2_PER_M2 = MEAN_FOOTHILL_GRADE;

    /**
     * US dollars a m3 of earth, balanced cut and fill with compaction and no haul off site: US$15-30 a cubic yard
     * (about $20-39 a m3) is the commonest 2026 benchmark (snaptrude.com/blog/cut-fill-earthwork-calculator-site-grading,
     * March 2026), with contractor guides lower ($6.50-14 a yd3); 20, the low end of the benchmark.
     */
    public static final double EARTH_USD_PER_M3 = 20;

    /**
     * US dollars a m2 of retaining wall, face area: a segmental block wall is US$15-35 a sq ft in national guides
     * (fixr.com/costs/retaining-wall-building) and $70-140 for an engineered wall with geogrid in San Diego
     * (installitdirect.com, 2025); about $40 a sq ft, between them, 430 a m2.
     */
    public static final double WALL_USD_PER_M2 = 430;

    /** US dollars to prepare a m2 of foothill: its earth and its wall at their rates (about $110). */
    public static final double USD_PER_M2 = EARTH_M3_PER_M2 * EARTH_USD_PER_M3 + WALL_M2_PER_M2 * WALL_USD_PER_M2;

    /**
     * Construction points to prepare a m2 of foothill: its dollars, in the thousands buildings.json counts in, over
     * what the builders charge a point of house work at founding - the House's founding cash cost over its points,
     * in the same thousands (BuildingsTemplate.getFoundingCashCost()). 0 where that price is not a positive figure.
     */
    public static double pointsPerM2(double houseFoundingCashPerPoint) {
        return houseFoundingCashPerPoint > 0 && Double.isFinite(houseFoundingCashPerPoint)
                ? USD_PER_M2 / 1000 / houseFoundingCashPerPoint : 0;
    }

    /** ...and for `sqFt` square feet of foothill; 0 for none. */
    public static double points(double sqFt, double houseFoundingCashPerPoint) {
        return sqFt > 0 ? sqFt * LandManager.SQ_M_PER_SQ_FT * pointsPerM2(houseFoundingCashPerPoint) : 0;
    }
}
