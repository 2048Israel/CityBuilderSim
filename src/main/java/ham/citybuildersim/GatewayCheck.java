package ham.citybuildersim;

/**
 * A city's gateway is the way the world is.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. A STRIP OF LAND RUNNING ONE KNOWN WAY gives the gateway on that
 *      bearing, at the city's radius, with every returned bearing a multiple
 *      of the dial's step.
 *
 *   2. AN ISLAND FOUNDING has no gateway: land only within a small disc round
 *      the site is short of the reach.
 *
 *   3. TWO EQUAL ARMS, east and south, tie; the smaller of the two lone tied
 *      bearings (east) is the gateway.
 *
 *   4. A HALF-PLANE OF LAND WEST ties every westward bearing, and the gateway
 *      is the middle of that wide arc (180), not the first westward bearing.
 *
 *   5. THE THRESHOLD is exact: a strip one cell short of the reach has no
 *      gateway, and a strip that just reaches has one.
 *
 *   6. THE DEFAULT WORLD is deterministic. If a gateway is found its point
 *      lies on the edge and on land; if none is found, the best land run is
 *      really short of the reach.
 *
 *   7. TWELVE REAL GENERATED WORLDS fire the mechanic at least once, so the
 *      rule is not only a fixture's.
 *
 * WHY THIS EXISTS: batch FR0 finds the land's mirror of SeaRoutes' offing,
 * so the highway and railway know which way the world is. A fixture CAUSES
 * each condition rather than waiting for a map to.
 */
public class GatewayCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-78s %s%n", label, ok ? "ok" : "FAIL");
    }

    /** The reach, in cells, that the model's no-gateway rule requires (the model's own constants, restated here for the fixtures). */
    static int reachCells(double radius) {
        int want = (int) Math.ceil((radius + Gateways.GATEWAY_M / World.PLOT_M) / SeaRoutes.CELL);
        return Math.max(1, Math.min(want, Gateways.RAY_CELLS));
    }

    /** The longest run of land cells out from a cell, over every bearing - the harness's own look at the same ground. */
    static int bestRun(Gateways.Ground ground, long ci, long cj) {
        int best = 0;
        for (int deg = 0; deg < 360; deg += Gateways.BEARING_STEP) {
            double r = Math.toRadians(deg), c = Math.cos(r), s = Math.sin(r);
            int run = 0;
            for (int k = 1; k <= Gateways.RAY_CELLS; k++) {
                long i = Math.round(ci + c * k), j = Math.round(cj + s * k);
                if (!ground.land(i, j)) break;
                run = k;
            }
            if (run > best) best = run;
        }
        return best;
    }

    public static void main(String[] args) {
        int cell = SeaRoutes.CELL;
        long sc = 100;                                  // the site's cell, a synthetic world's middle
        long siteX = sc * cell + cell / 2, siteY = sc * cell + cell / 2;
        double radius = 2 * cell;                       // the city's edge, two cells out
        int reach = reachCells(radius);

        /* ============ 1. a strip of land east ============ */
        System.out.println("--- a strip of land east ---");
        long strip = reach + 3L;
        Gateways.Ground east = (ci, cj) -> cj == sc && ci >= sc && ci <= sc + strip;
        Gateways.Gateway g = Gateways.of(east, siteX, siteY, radius);
        check("a strip east gives a gateway", g != null);
        check("...whose bearing is east (0)", g != null && g.bearing() == 0);
        check("...and whose bearing is a multiple of the step",
                g != null && g.bearing() % Gateways.BEARING_STEP == 0);
        double r = Math.toRadians(g == null ? 0 : g.bearing());
        check("...and whose point lies on the city's edge on that bearing",
                g != null && g.x() == Math.round(siteX + Math.cos(r) * radius)
                        && g.y() == Math.round(siteY + Math.sin(r) * radius));
        check("...and is the city's radius out",
                g != null && Math.abs(Math.hypot(g.x() - siteX, g.y() - siteY) - radius) <= 1.0);

        /* ============ 2. an island founding ============ */
        System.out.println("\n--- an island founding ---");
        long islandCells = Math.max(1, reach / 4);
        Gateways.Ground island = (ci, cj) -> {
            long di = ci - sc, dj = cj - sc;
            return di * di + dj * dj <= islandCells * islandCells;
        };
        check("land only within a small disc round the site has no gateway",
                Gateways.of(island, siteX, siteY, radius) == null);

        /* ============ 3. a tie ============ */
        System.out.println("\n--- a tie ---");
        long arm = reach + 2;
        Gateways.Ground tie = (ci, cj) ->
                (cj == sc && ci >= sc && ci <= sc + arm)        // east
                || (ci == sc && cj >= sc && cj <= sc + arm);    // south
        Gateways.Gateway tied = Gateways.of(tie, siteX, siteY, radius);
        check("equal arms east and south keep the smaller lone tied bearing (east)",
                tied != null && tied.bearing() == 0);
        check("...and the tied bearing is a multiple of the step",
                tied != null && tied.bearing() % Gateways.BEARING_STEP == 0);
        double tr = Math.toRadians(tied == null ? 0 : tied.bearing());
        check("...and its point lies on the city's edge on that bearing",
                tied != null && tied.x() == Math.round(siteX + Math.cos(tr) * radius)
                        && tied.y() == Math.round(siteY + Math.sin(tr) * radius));

        /* ============ 4. a half-plane of land west ============ */
        System.out.println("\n--- a half-plane of land west ---");
        Gateways.Ground west = (ci, cj) -> ci <= sc;
        Gateways.Gateway w = Gateways.of(west, siteX, siteY, radius);
        check("every westward bearing runs to the cap, and the gateway is the middle of that arc (180)",
                w != null && w.bearing() == 180);

        /* ============ 5. the threshold ============ */
        System.out.println("\n--- the threshold ---");
        long shortStrip = Math.max(0, reach - 1);
        Gateways.Ground shortOf = (ci, cj) -> cj == sc && ci >= sc && ci <= sc + shortStrip;
        check("a strip one cell short of the reach has no gateway",
                Gateways.of(shortOf, siteX, siteY, radius) == null);
        Gateways.Ground justReaches = (ci, cj) -> cj == sc && ci >= sc && ci <= sc + reach;
        Gateways.Gateway exact = Gateways.of(justReaches, siteX, siteY, radius);
        check("a strip that just reaches has one", exact != null);
        check("...on the east bearing", exact != null && exact.bearing() == 0);

        /* ============ 6. the default world ============ */
        System.out.println("\n--- the default world ---");
        World def = World.of(Founding.DEFAULT_WORLD_SEED);
        long fx = def.foundingX(), fy = def.foundingY();
        Gateways.Gateway da = Gateways.of(def, fx, fy, radius);
        Gateways.Gateway db = Gateways.of(def, fx, fy, radius);
        check("the default world's gateway is deterministic", java.util.Objects.equals(da, db));
        if (da != null) {
            double dr = Math.toRadians(da.bearing());
            check("...its point lies on the city's edge on that bearing",
                    da.x() == Math.round(fx + Math.cos(dr) * radius)
                            && da.y() == Math.round(fy + Math.sin(dr) * radius));
            Gateways.Ground ground = Gateways.ground(def);
            check("...and the cell at its point is land",
                    ground.land(Math.floorDiv(da.x(), cell), Math.floorDiv(da.y(), cell)));
            System.out.printf("  the default world has a gateway: bearing %d, run %d%n",
                    da.bearing(), da.run());
        } else {
            long sci = Math.floorDiv(fx, cell), scj = Math.floorDiv(fy, cell);
            Gateways.Ground ground = Gateways.ground(def);
            int bestDefault = bestRun(ground, sci, scj);
            check("...with none, its best land run is short of the reach", bestDefault < reach);
            System.out.printf("  the default world has no gateway: best land run %d, reach %d%n",
                    bestDefault, reach);
        }

        /* ============ 7. twelve real generated worlds ============ */
        System.out.println("\n--- twelve real generated worlds ---");
        long[] seeds = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 77 };
        int found = 0;
        for (long seed : seeds) {
            World world = World.of(seed);
            if (Gateways.of(world, world.foundingX(), world.foundingY(), radius) != null) found++;
        }
        System.out.printf("  gateway found on %d of %d worlds%n", found, seeds.length);
        check("at least one of the twelve worlds has a gateway", found > 0);

        System.out.println();
        System.out.println(fails == 0 ? "The gateways are found." : fails + " FAILED");
        System.exit(fails);
    }
}
