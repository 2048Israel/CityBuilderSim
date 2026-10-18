package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The city's two gateways - the highway's and the railway's - as one point on
 * the city's edge: the bearing, every SeaRoutes.BEARING_STEP degrees, with
 * the longest run of land out from the founding site, and the point at the
 * city's radius on it; pure, found once by the map and kept in its runs
 * (CityRuns.gateway(), saved in the sidecar).
 *
 * WHY THIS EXISTS (batch FR0; spec-freight-and-infrastructure.md 4.1,
 * decision D13). Today a run stops where the city's owned ground stops and
 * nothing says which way the world is, so the line that carries the city's
 * trade to the world does not reach the world. The sea already has the
 * mirror of this - SeaRoutes finds the offing, the bearing with the longest
 * run of open sea from a berth. This is the land's offing, from the founding
 * site itself; on the Earth it is the bearing toward the nearest larger
 * place, along land (4.1). The rail gateway shares the highway's bearing.
 */
public final class Gateways {

    private Gateways() { }

    /** The bearings tried for a gateway, every this many degrees: 5 (spec 4.1; SeaRoutes.BEARING_STEP). */
    public static final int BEARING_STEP = SeaRoutes.BEARING_STEP;

    /** The longest run of land a bearing is measured to, in cells: 400, 192 km (SeaRoutes.RAY_CELLS; the prototype's). */
    public static final int RAY_CELLS = SeaRoutes.RAY_CELLS;

    /** How far the Earth's rule looks for a larger place, in cells: the offing's 400, 192 km (SeaRoutes.RAY_CELLS); only the land on the place's own bearing is read, and only out to the reach. */
    public static final int PLACE_CELLS = SeaRoutes.RAY_CELLS;

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

    /** The gateway on the world's ground at the founding site with a city of this radius, in plots: on the Earth, the nearest larger place's way; elsewhere the land rule. */
    public static Gateway of(World world, long siteX, long siteY, double radius) {
        return of(ground(world), siteX, siteY, radius, largerPlaces(world, siteX, siteY));
    }

    /**
     * The gateway on a ground: the bearing, every BEARING_STEP degrees, with
     * the longest run of land cells out from the founding site's cell,
     * measured the way SeaRoutes.run() measures sea (x = cos, y = sin, up to
     * RAY_CELLS). A tie keeps the middle of one of the widest consecutive
     * arcs of tied bearings, chosen by the founding site's hash
     * (World.mix() of its two plots), so a half-plane of land to the west
     * gives 180 rather than the first westward bearing, and a full tie is one
     * arc whose middle is the hash's pick of all the bearings; a lone best
     * bearing keeps itself. Null when the best run does not reach GATEWAY_M
     * past the city's radius - an island founding has no gateway. The point
     * is the city's radius out from the site on the bearing, in world plots.
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
        int reach = reachCells(radius);
        if (bestRun < reach) return null;
        int best = tieBearing(runs, bestRun, siteX, siteY);
        double r = Math.toRadians(best);
        return new Gateway(best,
                Math.round(siteX + Math.cos(r) * radius),
                Math.round(siteY + Math.sin(r) * radius),
                bestRun);
    }

    /**
     * The Earth's gateway on a ground: the first candidate place in `larger`
     * (nearest the site first) whose bearing - atan2 of its offset, x = cos
     * and y = sin as run() reads it, rounded to the nearest BEARING_STEP and
     * mod 360 - has a land run reaching the reach wins, on that bearing. With
     * no such place, or an empty or null list, the land rule (the other of).
     */
    public static Gateway of(Ground ground, long siteX, long siteY, double radius, List<long[]> larger) {
        if (larger == null || larger.isEmpty()) return of(ground, siteX, siteY, radius);
        long ci = Math.floorDiv(siteX, SeaRoutes.CELL), cj = Math.floorDiv(siteY, SeaRoutes.CELL);
        int reach = reachCells(radius);
        for (long[] p : larger) {
            double dx = p[0] - siteX, dy = p[1] - siteY;
            int step = (int) Math.round(Math.toDegrees(Math.atan2(dy, dx)) / BEARING_STEP);
            int deg = Math.floorMod(step * BEARING_STEP, 360);
            int run = run(ground, ci, cj, deg, reach);
            if (run < reach) continue;
            double r = Math.toRadians(deg);
            return new Gateway(deg,
                    Math.round(siteX + Math.cos(r) * radius),
                    Math.round(siteY + Math.sin(r) * radius),
                    run);
        }
        return of(ground, siteX, siteY, radius);
    }

    /**
     * The candidate larger places for the Earth's rule, nearest the site
     * first (ties by the data's own order): empty unless the world is the
     * Earth; else every place of its EarthData with a population strictly
     * larger than the site's namesake's (all of them when it has none) whose
     * plot maps and lies within PLACE_CELLS * SeaRoutes.CELL plots of the site.
     */
    public static List<long[]> largerPlaces(World world, long siteX, long siteY) {
        if (!world.isEarth()) return List.of();
        EarthTerrain e = world.earthTerrain();
        double[] ll = new double[2];
        if (!e.projection().latLonOfPlot(siteX + 0.5, siteY + 0.5, ll)) return List.of();
        EarthData.Place namesake = LandMap.nearestPlace(e.data(), ll[0], ll[1]);
        double limit = (double) PLACE_CELLS * SeaRoutes.CELL;
        double[] out = new double[2];
        List<double[]> found = new ArrayList<>();
        for (EarthData.Place p : e.data().places()) {
            if (namesake != null && p.population() <= namesake.population()) continue;
            if (!e.projection().plotOf(p.lat(), p.lon(), out)) continue;
            double dx = out[0] - siteX, dy = out[1] - siteY;
            double d = Math.hypot(dx, dy);
            if (d > limit) continue;
            found.add(new double[] { d, Math.round(out[0]), Math.round(out[1]) });
        }
        found.sort((p, q) -> Double.compare(p[0], q[0]));
        List<long[]> places = new ArrayList<>(found.size());
        for (double[] p : found) places.add(new long[] { (long) p[1], (long) p[2] });
        return places;
    }

    /** The reach, in cells, that the no-gateway rule requires of a land run for a city of this radius: at least one, and never past RAY_CELLS. */
    public static int reachCells(double radius) {
        int want = (int) Math.ceil((radius + GATEWAY_M / World.PLOT_M) / SeaRoutes.CELL);
        return Math.max(1, Math.min(want, RAY_CELLS));
    }

    /**
     * The tied bearing among the widest arcs of tied bearings: the middle of
     * one chosen by the founding site's hash, floorMod by the number of
     * widest arcs. A full tie (every bearing tied) is one arc whose middle is
     * the hash's pick of all the bearings. A single tied bearing is its own
     * arc, so a unique best keeps itself.
     */
    private static int tieBearing(int[] runs, int bestRun, long siteX, long siteY) {
        int n = runs.length;
        boolean[] tied = new boolean[n];
        boolean all = true;
        for (int i = 0; i < n; i++) {
            tied[i] = runs[i] == bestRun;
            all &= tied[i];
        }
        long hash = World.mix(siteX * 0x9E3779B97F4A7C15L ^ siteY);
        if (all) return Math.floorMod(hash, n) * BEARING_STEP;
        int widest = 0;
        List<Integer> middles = new ArrayList<>();
        for (int start = 0; start < n; start++) {
            if (!tied[start] || tied[(start - 1 + n) % n]) continue;
            int len = 1;
            while (len < n && tied[(start + len) % n]) len++;
            int mid = Math.floorMod(start + (len - 1) / 2, n) * BEARING_STEP;
            if (len > widest) { widest = len; middles.clear(); middles.add(mid); }
            else if (len == widest) middles.add(mid);
        }
        return middles.get(Math.floorMod(hash, middles.size()));
    }

    /** The run of land cells out from a cell on a bearing, in cells, up to RAY_CELLS; the world's edge, or any non-land cell, ends it. */
    private static int run(Ground ground, long ci, long cj, int deg) {
        return run(ground, ci, cj, deg, RAY_CELLS);
    }

    /** ...up to `most` cells. */
    private static int run(Ground ground, long ci, long cj, int deg, int most) {
        double r = Math.toRadians(deg), c = Math.cos(r), s = Math.sin(r);
        int run = 0;
        for (int k = 1; k <= most; k++) {
            long i = Math.round(ci + c * k), j = Math.round(cj + s * k);
            if (!ground.land(i, j)) break;
            run = k;
        }
        return run;
    }
}
