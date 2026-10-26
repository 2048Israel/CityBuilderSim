package ham.citybuildersim;

/**
 * What a long bridge's span costs, in US dollars a metre by kind and by the depth of the water it crosses, and the
 * construction points those dollars are at the bridge's own founding price a point. Pure arithmetic: Game.quoteBuild()
 * prices the span into the order (Game.bridgeWorksFor()), and the points are owed by the order's site before its first
 * bridge finishes, as a highway's works and a foothill pad's are (BuildingsStacks.addPreparation()).
 *
 * WHY. spec-earth2-terrain 6.4 (ET6; D13, D14, D16). A long bridge is an order between two landmasses, its length
 * and the depth of its water found by the model from the Earth's map (BridgeSurvey), and the template cannot hold a
 * price for a length it does not have: the template prices the two landings, the span is billed per order, by length
 * and by depth. As RunWorks does for a viaduct, the dollars are turned into points once, so the money keeps up with
 * the builders' wages and drifts with nothing else, and a bridge of 36 km takes the builders' capacity for years, as
 * the real ones did.
 *
 * THE RATES, from two built structures. In shallow water the span is a viaduct: RunWorks's deck a m2 (US DOT's 2025
 * planning costs) times the deck's width. In deep water the piers get taller, and the Confederation Bridge (12.9 km,
 * an 11 m deck, built 1993-97, its piers standing in water up to about 35 m) is the dearest deck a m2 the game has a
 * figure for, about US$11,800: both rates are the model's own, and the rate between them is linear in depth. A rail
 * bridge costs RAIL_OVER_HIGHWAY times a highway's a metre, and a road bridge, a deck about half as wide, about half
 * of a highway's, so rail is dearer than highway, and highway than road, at every depth.
 */
public final class BridgeWorks {

    private BridgeWorks() { }

    /** The three kinds of long bridge, as buildings.json's "bridge" names them: a highway's, a railway line's and a street's. */
    public enum Kind { HIGHWAY, RAIL, ROAD }

    /**
     * The depth, metres, at and under which the deck costs the base rate: 5, a viaduct over shallows and over fresh water,
     * whose depth the World does not hold (World.depthAt() is 0 on a lake and a river).
     */
    public static final double BASE_DEPTH_M = 5;

    /** The deck a m2 at BASE_DEPTH_M or less, and on fresh water: RunWorks.DECK_USD_PER_M2 (US DOT's 2025 planning costs, $250 a sq ft, about $2,690 a m2). */
    public static final double BASE_USD_PER_M2 = RunWorks.DECK_USD_PER_M2;

    /**
     * The Confederation Bridge's cost as built, Canadian dollars: C$1.3 billion (Wikipedia's figure for the cost as built,
     * about 30% over the C$840 million projected; the C$1.0 billion some sources give, Library and Archives Canada's
     * exhibit, is an earlier estimate and is not used).
     */
    public static final double CONFEDERATION_COST_CAD = 1.3e9;

    /** The Confederation Bridge's length, metres: 12,900 (Prince Edward Island to New Brunswick, over the Northumberland Strait). */
    public static final double CONFEDERATION_LENGTH_M = 12_900;

    /** ...and its deck's width, metres: 11 (two lanes and shoulders). */
    public static final double CONFEDERATION_DECK_M = 11;

    /** ...and the depth its piers stand in at the deepest, metres: 35 (about; the deepest water of the strait's crossing). */
    public static final double CONFEDERATION_DEPTH_M = 35;

    /** Canada's consumer prices from 1997, the year the bridge opened, to 2025: about 1.79 (Statistics Canada, table 18-10-0005). */
    public static final double CAD_CPI_1997_TO_2025 = 1.79;

    /** US dollars a Canadian dollar in 2025: 0.72. */
    public static final double USD_PER_CAD_2025 = 0.72;

    /** The Confederation's deck a m2 in 2025 US dollars, its cost over its deck's area: about $11,800. The rate at CONFEDERATION_DEPTH_M. */
    public static final double CONFEDERATION_USD_PER_M2 = CONFEDERATION_COST_CAD * CAD_CPI_1997_TO_2025 * USD_PER_CAD_2025
            / (CONFEDERATION_LENGTH_M * CONFEDERATION_DECK_M);

    /** A highway bridge's deck, metres: RunWorks.DECK_WIDTH_M (23.2, the Interstate standard's four lanes and shoulders). */
    public static final double HIGHWAY_DECK_WIDTH_M = RunWorks.DECK_WIDTH_M;

    /** A road bridge's deck, metres: two lanes of 3.66 m (12 ft) and two shoulders of 2.44 m (8 ft), the usual rural two-lane section, 12.2 m, the Confederation's 11 m beside it. */
    public static final double ROAD_DECK_WIDTH_M = 2 * 3.66 + 2 * 2.44;

    /**
     * How many times a highway's metre a railway's costs, 1.42: the game's own scale in RunWorks's javadoc - the Freight
     * Line's $2,330 a m2 of its land over the Elevated Highway's $1,640. A rail bridge's metre is a highway bridge's times
     * this, so rail is above highway above road.
     */
    public static final double RAIL_OVER_HIGHWAY = 1.42;

    /** The deck's width a kind of bridge carries, metres: a rail bridge's is a highway's. */
    public static double deckWidthM(Kind kind) {
        return kind == Kind.ROAD ? ROAD_DECK_WIDTH_M : HIGHWAY_DECK_WIDTH_M;
    }

    /**
     * The deck a m2 in water of this depth, US dollars: the base rate at BASE_DEPTH_M or less, the Confederation's at
     * CONFEDERATION_DEPTH_M, linear between and beyond (taller piers in deeper water), never below the base.
     */
    public static double deckUsdPerM2(double depthM) {
        if (!(depthM > BASE_DEPTH_M)) return BASE_USD_PER_M2;
        return BASE_USD_PER_M2 + (CONFEDERATION_USD_PER_M2 - BASE_USD_PER_M2) * (depthM - BASE_DEPTH_M) / (CONFEDERATION_DEPTH_M - BASE_DEPTH_M);
    }

    /** What a metre of the span costs in water of this depth, US dollars: the deck a m2 times the kind's width, a railway's times RAIL_OVER_HIGHWAY. */
    public static double usdPerM(Kind kind, double depthM) {
        double metre = deckUsdPerM2(depthM) * deckWidthM(kind);
        return kind == Kind.RAIL ? metre * RAIL_OVER_HIGHWAY : metre;
    }

    /**
     * The span of a surveyed crossing, US dollars: for each step between two plots of the chain, its length times the rate
     * at the depths of its two ends, half each - a step runs from one plot's centre to the next's, half of either plot -
     * so a landing (depth 0) and a plot of water are priced as the half-plots they are. `stepM[i]` is the step from plot i
     * to plot i + 1 and `depthM` the depth of every plot of the chain, landings at 0: one more plot than steps.
     */
    public static double spanUsd(Kind kind, double[] stepM, double[] depthM) {
        double usd = 0;
        for (int i = 0; i < stepM.length; i++) {
            usd += stepM[i] * (usdPerM(kind, depthM[i]) + usdPerM(kind, depthM[i + 1])) / 2;
        }
        return usd;
    }

    /** ...of a surveyed crossing (BridgeSurvey.Result). 0 for a crossing that is no bridge. */
    public static double spanUsd(Kind kind, BridgeSurvey.Result survey) {
        return survey == null || !survey.ok() ? 0 : spanUsd(kind, survey.stepM(), survey.plotDepthM());
    }

    /**
     * Construction points for `usd` dollars of span: the dollars, in the thousands buildings.json counts in, over what
     * the builders charge a point of the bridge's own work at founding - its template's founding cash cost over its
     * points (BuildingsTemplate.getFoundingCashCost()). 0 for no dollars or a price a point that is not a positive figure.
     */
    public static double points(double usd, double foundingCashPerPoint) {
        if (!(usd > 0) || !(foundingCashPerPoint > 0) || !Double.isFinite(foundingCashPerPoint)) return 0;
        return usd / 1000 / foundingCashPerPoint;
    }
}
