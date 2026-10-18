package ham.citybuildersim;

/**
 * The city's two gateways - the highway's and the railway's - as one point on
 * the city's edge: the bearing, every SeaRoutes.BEARING_STEP degrees, with
 * the longest run of land out from the founding site, and the point at the
 * city's radius on it; pure, kept by the map and never saved.
 *
 * WHY THIS EXISTS (batch FR0; spec-freight-and-infrastructure.md 4.1,
 * decision D13). Today a run stops where the city's owned ground stops and
 * nothing says which way the world is, so the line that carries the city's
 * trade to the world does not reach the world. The sea already has the
 * mirror of this - SeaRoutes finds the offing, the bearing with the longest
 * run of open sea from a berth. This is the land's offing, from the founding
 * site itself; the rail gateway shares the highway's bearing (4.1).
 */
public final class Gateways {

    private Gateways() { }

    /** The bearings tried for a gateway, every this many degrees: 5 (spec 4.1; SeaRoutes.BEARING_STEP). */
    public static final int BEARING_STEP = SeaRoutes.BEARING_STEP;

    /** The longest run of land a bearing is measured to, in cells: 400, 192 km (SeaRoutes.RAY_CELLS; the prototype's). */
    public static final int RAY_CELLS = SeaRoutes.RAY_CELLS;

    /** How far past the city's radius the land must run for a gateway, in metres: 10 km, the offing's own margin (SeaRoutes.OFFING_M), so an island founding with less has no gateway (spec 4.1). */
    public static final double GATEWAY_M = SeaRoutes.OFFING_M;

    /** A ground's cells of SeaRoutes.CELL plots: true where the cell is land, so a harness fixture can cause each condition. */
    @FunctionalInterface
    public interface Ground {
        boolean land(long ci, long cj);
    }

    /** A gateway: the bearing, the point on the city's edge in world plots, and the run of land cells that won it. */
    public record Gateway(int bearing, long x, long y, int run) { }

    /* =====================================================================
       THE GROUND
       ===================================================================== */

    /** The world's ground as a Ground: land is a cell that is not sea in SeaRoutes' grid (off the world is sea, so the world's edge ends a land run). */
    public static Ground ground(World world) {
        SeaRoutes.Grid sea = new SeaRoutes.Grid(world);
        return (ci, cj) -> !sea.sea(ci, cj);
    }

    /* =====================================================================
       A GATEWAY
       ===================================================================== */

    /** The gateway on the world's ground at the founding site with a city of this radius, in plots. */
    public static Gateway of(World world, long siteX, long siteY, double radius) {
        return of(ground(world), siteX, siteY, radius);
    }

    /**
     * The gateway on a ground: the bearing, every BEARING_STEP degrees, with
     * the longest run of land cells out from the founding site's cell,
     * measured the way SeaRoutes.run() measures sea (x = cos, y = sin, up to
     * RAY_CELLS). A tie keeps the middle bearing of the widest consecutive
     * arc of tied bearings, so a half-plane of land to the west gives 180
     * rather than the first westward bearing; arcs of the same width keep the
     * smaller middle, and a full tie keeps 0. Null when the best run does not
     * reach GATEWAY_M past the city's radius - an island founding has no
     * gateway. The point is the city's radius out from the site on the
     * bearing, in world plots.
     */
    public static Gateway of(Ground ground, long siteX, long siteY, double radius) {
        long ci = Math.floorDiv(siteX, SeaRoutes.CELL), cj = Math.floorDiv(siteY, SeaRoutes.CELL);
        int steps = 360 / BEARING_STEP;
        int[] runs = new int[steps];
        int bestRun = -1;
        for (int deg = 0; deg < 360; deg += BEARING_STEP) {
            int run = run(ground, ci, cj, deg);
            runs[deg / BEARING_STEP] = run;
            if (run > bestRun) bestRun = run;
        }
        int want = (int) Math.ceil((radius + GATEWAY_M / World.PLOT_M) / SeaRoutes.CELL);
        int reach = Math.max(1, Math.min(want, RAY_CELLS));
        if (bestRun < reach) return null;
        int best = tieBearing(runs, bestRun);
        double r = Math.toRadians(best);
        return new Gateway(best,
                Math.round(siteX + Math.cos(r) * radius),
                Math.round(siteY + Math.sin(r) * radius),
                bestRun);
    }

    /**
     * The tied bearing: the middle of the longest consecutive run of tied
     * bearings round the dial (a whole dial's middle is taken as 0), and of
     * equal runs the smaller middle. A single tied bearing is its own arc,
     * so a unique best keeps itself.
     */
    private static int tieBearing(int[] runs, int bestRun) {
        int n = runs.length;
        boolean[] tied = new boolean[n];
        boolean all = true;
        for (int i = 0; i < n; i++) {
            tied[i] = runs[i] == bestRun;
            all &= tied[i];
        }
        if (all) return 0;
        int widest = 0, middle = 0;
        for (int start = 0; start < n; start++) {
            if (!tied[start]) continue;
            int len = 1;
            while (len < n && tied[(start + len) % n]) len++;
            int mid = Math.floorMod(start + (len - 1) / 2, n) * BEARING_STEP;
            if (len > widest || (len == widest && mid < middle)) { widest = len; middle = mid; }
        }
        return middle;
    }

    /** The run of land cells out from a cell on a bearing, in cells, up to RAY_CELLS; the world's edge, or any non-land cell, ends it. */
    private static int run(Ground ground, long ci, long cj, int deg) {
        double r = Math.toRadians(deg), c = Math.cos(r), s = Math.sin(r);
        int run = 0;
        for (int k = 1; k <= RAY_CELLS; k++) {
            long i = Math.round(ci + c * k), j = Math.round(cj + s * k);
            if (!ground.land(i, j)) break;
            run = k;
        }
        return run;
    }
}
