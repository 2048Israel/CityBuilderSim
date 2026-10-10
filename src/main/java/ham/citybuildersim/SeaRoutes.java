package ham.citybuildersim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Each sea terminal's route out to the world: found once on a grid of the sea at 480 m, pulled straight, sailed from the terminal's quay out to the offing - past the city's ground, on the bearing with the most open sea - and on into the abyss, where a boat fades - what BoatSchedule's boats sail; pure, kept by the map and never saved.
 *
 * WHY THIS EXISTS (0.7.97, batch O13; the project's spec-roads-and-ports.md
 * 4.1 and 7's S1 and S2, a port of its prototype roads-prototype/islands.py:
 * sea_grid(), astar(), pull(), offing()). O9's boats ran from each quay
 * straight out, away from the founding site, for BoatSchedule.LANE_PLOTS: a
 * lane that crossed the land wherever the coast turned, and a city's ships
 * all queued on one line. A route keeps to the water, comes out of the bay
 * the way a ship would, and leaves the city's waters on the open side.
 *
 * THE GRID. The world's sea in cells of CELL plots (480 m), a cell sea when
 * at least SALT_LEAST of its SAMPLE x SAMPLE samples of the world's ground
 * are salt, read a block of BLOCK x BLOCK cells at a time
 * (World.regionTerrain()); off the world is sea - three quarters of a cell,
 * where the prototype took 95% of its plots, so a channel a ship could use
 * at 480 m is one the grid sees (star O13-5). A terminal stands only where
 * its water OPENS TO THE SEA (opensToSea(): the playtest's first terminals
 * stood on a salt lagoon a sand bar closes, where no ship could come).
 * A cell beside a cell that is not sea is the shore's, which costs SHORE_COST
 * times as much to cross (ships keep off it).
 *
 * A ROUTE. From the cell of the terminal's berth (its quay's end), or the
 * nearest sea cell within NEAR_CELLS:
 *   THE OFFING  the bearing, every BEARING_STEP degrees, with the longest run
 *               of sea cells out from it (up to RAY_CELLS; one that leaves the
 *               world at sea is open water) - the one nearest the way away
 *               from the founding site on a tie - and on it the point the
 *               city's radius plus OFFING_M out (the prototype's rule);
 *   ...IN A BAY when the sea ends sooner on every bearing (a terminal up an
 *               inlet: the playtest's, star O13-5): the sea reachable from
 *               the berth flooded out to that distance, and of the cells
 *               farthest out the one with the longest run of sea on beyond
 *               it, the way away from the site on a tie - so the boats come
 *               out of the inlet the way the water goes;
 *   THE WAY     A* over the sea's cells, eight ways, to the offing (the
 *               flood's own way in a bay; at most EXPANSIONS_MOST cells looked
 *               at), then pulled straight: a point kept only where the
 *               straight line to the next would cross a cell that is not sea;
 *   THE ABYSS   on along the last leg's heading ABYSS_M past the offing, or
 *               as far as the sea goes where land comes sooner (abyssRun()),
 *               the boat fading as it goes (BoatSchedule.Boat.alpha()).
 * Each call's last leg and the abyss are turned by up to SPREAD_DEG either way
 * about the leg's start, by the call's own hash, so boats do not queue on one
 * line (spec 4.1) - where the fan of those lines keeps to the sea; else none.
 * A berth with no sea within reach, or no way found, has the straight lane on
 * its bearing (BoatSchedule.lane() with no sea at all).
 */
public final class SeaRoutes {

    private SeaRoutes() { }

    /** The sea grid's cell, in plots: 16, 480 m (spec 4.1; the prototype's CG). */
    public static final int CELL = 16;

    /** A cell's ground is read at a sample every this many plots: 4, so 4 x 4 samples a cell. */
    public static final int SAMPLE = 4;

    /** A cell is sea when at least this many of its 16 samples are salt: 12, three quarters (star O13-5; the prototype's 95% shut a 300 m narrows). */
    public static final int SALT_LEAST = 12;

    /** A shore cell's cost against an open one: 3 (spec 4.1: "three times the cost one cell from the shore"). */
    public static final double SHORE_COST = 3;

    /** How far past the city's radius the offing lies: 10 km (spec 4.1). */
    public static final double OFFING_M = 10_000;

    /** The bearings tried for the offing, every this many degrees: 5 (spec 4.1). */
    public static final int BEARING_STEP = 5;

    /** The longest run of sea a bearing is measured to, in cells: 400, 192 km (the prototype's). */
    public static final int RAY_CELLS = 400;

    /** How far round a berth its first sea cell is looked for, in cells: 6 (the prototype's nearest_sea()). */
    public static final int NEAR_CELLS = 6;

    /** How far past the offing a boat sails into the abyss, fading: 3 km - "fading over a few km" (spec 4.1); less where land comes sooner (abyssRun()). */
    public static final double ABYSS_M = 3_000;

    /** The most a call's last leg is turned either way, in degrees: 3 - "a few degrees" (spec 4.1). */
    public static final double SPREAD_DEG = 3;

    /** The most cells a route's A* looks at before it takes the straight lane: 250,000 - a way of about 1,000 km through open sea. */
    public static final int EXPANSIONS_MOST = 250_000;

    /** Cells a side of a block of the grid read at once: 64, 1,024 plots (a block a World.regionTerrain() call). */
    static final int BLOCK = 64;

    /** Each terminal's route, in the berths' order (pure in the world, the berths, the site and the radius). */
    public static List<BoatSchedule.Route> of(World world, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius) {
        List<BoatSchedule.Route> out = new ArrayList<>();
        if (berths == null || berths.isEmpty()) return out;
        Grid g = new Grid(world);
        for (BoatSchedule.Berth b : berths) out.add(route(g, b, siteX, siteY, radius));
        return out;
    }

    /* =====================================================================
       THE GRID
       ===================================================================== */

    /** The sea's cells, read a block at a time as asked. */
    static final class Grid {
        final World world;
        final Map<Long, boolean[]> blocks = new HashMap<>();
        /** Each block's cells' openness as worked out: 0 not yet, 1 the shore's, 2 open water. */
        final Map<Long, byte[]> opens = new HashMap<>();
        final byte[] buf = new byte[(BLOCK * CELL / SAMPLE) * (BLOCK * CELL / SAMPLE)];
        int read;

        Grid(World world) { this.world = world; }

        /** Whether cell (ci, cj) is sea: every sample of it salt; off the world, sea. */
        boolean sea(long ci, long cj) {
            long cells = World.SIDE / CELL;
            if (ci < 0 || cj < 0 || ci >= cells || cj >= cells) return true;
            long bi = Math.floorDiv(ci, BLOCK), bj = Math.floorDiv(cj, BLOCK);
            long key = (bi << 32) ^ (bj & 0xffffffffL);
            boolean[] b = blocks.get(key);
            if (b == null) {
                int n = BLOCK * CELL / SAMPLE, per = CELL / SAMPLE;
                world.regionTerrain(bi * BLOCK * CELL, bj * BLOCK * CELL, SAMPLE, n, buf);
                read++;
                int[] salt = new int[BLOCK * BLOCK];
                for (int p = 0; p < n * n; p++) {
                    if (buf[p] != World.SALT) continue;
                    salt[((p / n) / per) * BLOCK + (p % n) / per]++;
                }
                b = new boolean[BLOCK * BLOCK];
                for (int c = 0; c < b.length; c++) b[c] = salt[c] >= SALT_LEAST;
                blocks.put(key, b);
            }
            return b[(int) ((cj - bj * BLOCK) * BLOCK + (ci - bi * BLOCK))];
        }

        /** Whether a sea cell is open water: every cell about it sea too; else it is the shore's. Worked out once a cell. */
        boolean open(long ci, long cj) {
            long bi = Math.floorDiv(ci, BLOCK), bj = Math.floorDiv(cj, BLOCK);
            long key = (bi << 32) ^ (bj & 0xffffffffL);
            byte[] o = opens.computeIfAbsent(key, k -> new byte[BLOCK * BLOCK]);
            int at = (int) ((cj - bj * BLOCK) * BLOCK + (ci - bi * BLOCK));
            if (o[at] == 0) {
                boolean all = true;
                for (int b = -1; b <= 1 && all; b++) for (int a = -1; a <= 1 && all; a++) all = sea(ci + a, cj + b);
                o[at] = (byte) (all ? 2 : 1);
            }
            return o[at] == 2;
        }

        /** Whether the straight line between two cells' middles crosses only sea cells, looked at three times a cell along it (the prototype's clear()). */
        boolean clear(double ai, double aj, double bi, double bj) {
            int n = (int) (Math.max(Math.abs(ai - bi), Math.abs(aj - bj)) * 3) + 1;
            for (int k = 0; k <= n; k++) {
                double t = k / (double) n;
                if (!sea(Math.round(ai + (bi - ai) * t), Math.round(aj + (bj - aj) * t))) return false;
            }
            return true;
        }
    }

    /* =====================================================================
       A ROUTE
       ===================================================================== */

    /** A berth's route (SeaRoutes' rule). */
    static BoatSchedule.Route route(Grid g, BoatSchedule.Berth b, long siteX, long siteY, double radius) {
        double bx = b.x() + 0.5, by = b.y() + 0.5;
        long[] start = nearestSea(g, Math.floorDiv(b.x(), CELL), Math.floorDiv(b.y(), CELL));
        if (start == null) return BoatSchedule.lane(b, siteX, siteY);
        // THE OFFING: the bearing with the longest run of sea, the way away from the founding site on a tie.
        double away = Math.atan2(by - siteY, bx - siteX);
        int bestDeg = -1, bestRun = -1;
        double bestOff = Double.MAX_VALUE;
        for (int deg = 0; deg < 360; deg += BEARING_STEP) {
            int run = run(g, start, deg);
            double off = Math.abs(Math.IEEEremainder(Math.toRadians(deg) - away, 2 * Math.PI));
            if (run > bestRun || (run == bestRun && off < bestOff)) { bestRun = run; bestDeg = deg; bestOff = off; }
        }
        double want = (radius + OFFING_M / World.PLOT_M) / CELL;
        int reach = (int) Math.max(1, Math.min(Math.ceil(want), RAY_CELLS));
        double r = Math.toRadians(bestDeg);
        long oi, oj;
        List<long[]> path;
        if (bestRun >= reach) {
            // Open water ahead: the offing on its bearing; THE WAY: A* over the sea's cells to it.
            oi = Math.round(start[0] + Math.cos(r) * reach);
            oj = Math.round(start[1] + Math.sin(r) * reach);
            path = astar(g, start, new long[] { oi, oj });
        } else {
            // ...IN A BAY: the reachable sea flooded out to the offing's distance, the offing the farthest out with the most sea
            // beyond; the flood's own cheapest way to it.
            path = outOfTheBay(g, start, reach, away);
            if (path == null) return BoatSchedule.lane(b, siteX, siteY);
            long[] o = path.get(path.size() - 1);
            oi = o[0];
            oj = o[1];
            r = Math.atan2(oj - start[1], oi - start[0]);
        }
        List<double[]> pts = new ArrayList<>();
        pts.add(new double[] { bx, by });
        if (path == null) {
            pts.add(new double[] { oi * CELL + CELL / 2.0, oj * CELL + CELL / 2.0 });
        } else {
            for (long[] c : pull(g, path)) {
                double cx = c[0] * CELL + CELL / 2.0, cy = c[1] * CELL + CELL / 2.0;
                if (pts.size() == 1 && c[0] == start[0] && c[1] == start[1] && path.size() > 1) {
                    // The berth's own cell's middle is passed through only when it is not on the way.
                    if (Math.hypot(cx - bx, cy - by) < CELL / 2.0) continue;
                }
                pts.add(new double[] { cx, cy });
            }
        }
        if (pts.size() < 2) pts.add(new double[] { oi * CELL + CELL / 2.0, oj * CELL + CELL / 2.0 });
        // THE ABYSS: on along the last leg's heading, fading.
        double[] a = pts.get(pts.size() - 2), o = pts.get(pts.size() - 1);
        double hx = o[0] - a[0], hy = o[1] - a[1], hl = Math.hypot(hx, hy);
        if (!(hl > 0)) { hx = Math.cos(r); hy = Math.sin(r); hl = 1; }
        double abyss = abyssRun(g, o[0], o[1], hx / hl, hy / hl);
        pts.add(new double[] { o[0] + hx / hl * abyss, o[1] + hy / hl * abyss });
        int n = pts.size(), pivot = n - 3;
        double[] xs = new double[n], ys = new double[n];
        for (int i = 0; i < n; i++) { xs[i] = pts.get(i)[0]; ys[i] = pts.get(i)[1]; }
        // THE SPREAD: the last leg and the abyss turned, where the fan of them keeps to the sea.
        double spread = Math.toRadians(SPREAD_DEG);
        for (double turn : new double[] { -spread, -spread / 2, spread / 2, spread }) {
            if (!tailClear(g, xs, ys, pivot, turn)) { spread = 0; break; }
        }
        return BoatSchedule.Route.through(b, xs, ys, n - 2, pivot, spread);
    }

    /**
     * The way out of a bay (star O13-5): Dijkstra over the sea's cells from
     * `start` (eight ways, a shore cell SHORE_COST times an open one), no
     * cell farther than `reach` cells from it in a straight line, at most
     * EXPANSIONS_MOST looked at; of the cells reached farthest out (in whole
     * cells), the one with the longest run of sea on beyond it on its own
     * bearing, then the one nearest the way away from the founding site
     * (`away`, radians), then the first found; its cheapest way. Null when
     * nothing past the start is reached.
     */
    static List<long[]> outOfTheBay(Grid g, long[] start, int reach, double away) {
        Map<Long, Double> cost = new HashMap<>();
        Map<Long, Long> prev = new HashMap<>();
        List<Long> order = new ArrayList<>();
        PriorityQueue<double[]> open = new PriorityQueue<>((p, q) -> p[0] != q[0] ? Double.compare(p[0], q[0])
                : p[1] != q[1] ? Double.compare(p[1], q[1]) : Double.compare(p[2], q[2]));
        long ks = key(start[0], start[1]);
        cost.put(ks, 0.0);
        prev.put(ks, Long.MIN_VALUE);
        open.add(new double[] { 0, start[0], start[1] });
        int looked = 0;
        while (!open.isEmpty() && looked < EXPANSIONS_MOST) {
            double[] e = open.poll();
            long ci = (long) e[1], cj = (long) e[2], k = key(ci, cj);
            double here = cost.get(k);
            if (e[0] > here + 1e-9) continue;
            looked++;
            order.add(k);
            for (int dj = -1; dj <= 1; dj++) {
                for (int di = -1; di <= 1; di++) {
                    if (di == 0 && dj == 0) continue;
                    long ni = ci + di, nj = cj + dj;
                    if (Math.hypot(ni - start[0], nj - start[1]) > reach || !g.sea(ni, nj)) continue;
                    double c = here + (di != 0 && dj != 0 ? Math.sqrt(2) : 1) * (g.open(ni, nj) ? 1 : SHORE_COST);
                    long nk = key(ni, nj);
                    Double was = cost.get(nk);
                    if (was != null && c >= was) continue;
                    cost.put(nk, c);
                    prev.put(nk, k);
                    open.add(new double[] { c, ni, nj });
                }
            }
        }
        long best = Long.MIN_VALUE;
        int far = -1, run = -1;
        double off = Double.MAX_VALUE;
        for (long k : order) {
            long i = k >> 32, j = (int) k;
            int d = (int) Math.floor(Math.hypot(i - start[0], j - start[1]));
            if (d < far || d == 0) continue;
            int deg = (int) Math.round(Math.toDegrees(Math.atan2(j - start[1], i - start[0])));
            int beyond = d > far || run < RAY_CELLS ? run(g, new long[] { i, j }, ((deg % 360) + 360) % 360) : 0;
            double o = Math.abs(Math.IEEEremainder(Math.atan2(j - start[1], i - start[0]) - away, 2 * Math.PI));
            if (d > far || beyond > run || (beyond == run && o < off)) { best = k; far = d; run = beyond; off = o; }
        }
        if (best == Long.MIN_VALUE) return null;
        List<long[]> out = new ArrayList<>();
        for (long k = best; k != Long.MIN_VALUE; k = prev.get(k)) out.add(new long[] { k >> 32, (int) k });
        java.util.Collections.reverse(out);
        return out;
    }

    /** How far out a berth's water must reach for a terminal to stand on it, in metres: OFFING_M, the offing's own margin past the city (star O13-5). */
    public static final double OPEN_M = OFFING_M;

    /**
     * Whether the water at world plot (x, y) opens to the sea (star O13-5):
     * from its cell, or the nearest sea cell within NEAR_CELLS, the sea's
     * cells reach OPEN_M away in a straight line, eight ways (a breadth-first
     * flood, stopped as soon as one does). A flood that does not is remembered
     * in `shut`, its cells' keys, so the rest of that water is refused at once.
     */
    public static boolean opensToSea(Grid g, long x, long y, java.util.Set<Long> shut) {
        long[] st = nearestSea(g, Math.floorDiv(x, CELL), Math.floorDiv(y, CELL));
        if (st == null) return false;
        long ks = key(st[0], st[1]);
        if (shut.contains(ks)) return false;
        double reach = OPEN_M / World.PLOT_M / CELL;
        java.util.ArrayDeque<long[]> q = new java.util.ArrayDeque<>();
        java.util.Set<Long> seen = new java.util.HashSet<>();
        q.add(st);
        seen.add(ks);
        while (!q.isEmpty() && seen.size() < EXPANSIONS_MOST) {
            long[] c = q.poll();
            if (Math.hypot(c[0] - st[0], c[1] - st[1]) >= reach) return true;
            for (int dj = -1; dj <= 1; dj++) {
                for (int di = -1; di <= 1; di++) {
                    long ni = c[0] + di, nj = c[1] + dj, nk = key(ni, nj);
                    if ((di == 0 && dj == 0) || seen.contains(nk) || !g.sea(ni, nj)) continue;
                    if (shut.contains(nk)) { shut.addAll(seen); return false; }
                    seen.add(nk);
                    q.add(new long[] { ni, nj });
                }
            }
        }
        if (q.isEmpty()) shut.addAll(seen);
        return !q.isEmpty();
    }

    /**
     * How far past the offing (x, y) the abyss runs on along (ux, uy), in
     * plots: ABYSS_M, or the longest of its lengths a third of a cell shorter
     * at a time whose leg keeps to the sea's cells (legOnSea()) - a third of a
     * cell at least, the offing's own cell (it is a sea cell's middle).
     */
    static double abyssRun(Grid g, double x, double y, double ux, double uy) {
        double step = CELL / 3.0;
        for (double d = ABYSS_M / World.PLOT_M; d > step; d -= step) if (legOnSea(g, x, y, x + ux * d, y + uy * d)) return d;
        return step;
    }

    /** Whether the straight leg from (ax, ay) to (bx, by) keeps to the sea's cells, looked at three times a cell along it, both ends too. */
    static boolean legOnSea(Grid g, double ax, double ay, double bx, double by) {
        int n = (int) Math.ceil(Math.hypot(bx - ax, by - ay) / CELL * 3) + 1;
        for (int k = 0; k <= n; k++) {
            double x = ax + (bx - ax) * k / n, y = ay + (by - ay) * k / n;
            if (!g.sea(Math.floorDiv((long) Math.floor(x), CELL), Math.floorDiv((long) Math.floor(y), CELL))) return false;
        }
        return true;
    }

    /** The berth's own cell when it is sea, else the nearest sea cell within NEAR_CELLS (the nearer, then the first in rows), or null. */
    static long[] nearestSea(Grid g, long ci, long cj) {
        if (g.sea(ci, cj)) return new long[] { ci, cj };
        long[] best = null;
        long bestD = Long.MAX_VALUE;
        for (long j = cj - NEAR_CELLS; j <= cj + NEAR_CELLS; j++) {
            for (long i = ci - NEAR_CELLS; i <= ci + NEAR_CELLS; i++) {
                if (!g.sea(i, j)) continue;
                long d = (i - ci) * (i - ci) + (j - cj) * (j - cj);
                if (d < bestD) { bestD = d; best = new long[] { i, j }; }
            }
        }
        return best;
    }

    /** The run of sea cells out from a cell on a bearing, in cells, up to RAY_CELLS; 1,000 more when it leaves the world at sea (the prototype's open water). */
    static int run(Grid g, long[] from, int deg) {
        double r = Math.toRadians(deg), c = Math.cos(r), s = Math.sin(r);
        long cells = World.SIDE / CELL;
        int run = 0;
        for (int k = 1; k <= RAY_CELLS; k++) {
            long i = Math.round(from[0] + c * k), j = Math.round(from[1] + s * k);
            if (i < 0 || j < 0 || i >= cells || j >= cells) return k + 1000;
            if (!g.sea(i, j)) break;
            run = k;
        }
        return run;
    }

    /** The cheapest way over the sea's cells, eight ways, a shore cell SHORE_COST times an open one, Euclidean ahead (the prototype's astar()); null when none, or past EXPANSIONS_MOST. */
    static List<long[]> astar(Grid g, long[] a, long[] b) {
        if (!g.sea(b[0], b[1])) return null;
        Map<Long, Double> cost = new HashMap<>();
        Map<Long, Long> prev = new HashMap<>();
        PriorityQueue<double[]> open = new PriorityQueue<>((p, q) -> p[0] != q[0] ? Double.compare(p[0], q[0])
                : p[1] != q[1] ? Double.compare(p[1], q[1]) : Double.compare(p[2], q[2]));
        long ka = key(a[0], a[1]), kb = key(b[0], b[1]);
        cost.put(ka, 0.0);
        prev.put(ka, Long.MIN_VALUE);
        open.add(new double[] { Math.hypot(a[0] - b[0], a[1] - b[1]), a[0], a[1] });
        int looked = 0;
        boolean found = false;
        while (!open.isEmpty()) {
            double[] e = open.poll();
            long ci = (long) e[1], cj = (long) e[2], k = key(ci, cj);
            if (k == kb) { found = true; break; }
            double here = cost.get(k);
            if (e[0] > here + Math.hypot(ci - b[0], cj - b[1]) + 1e-9) continue;
            if (++looked > EXPANSIONS_MOST) return null;
            for (int dj = -1; dj <= 1; dj++) {
                for (int di = -1; di <= 1; di++) {
                    if (di == 0 && dj == 0) continue;
                    long ni = ci + di, nj = cj + dj;
                    if (!g.sea(ni, nj)) continue;
                    double step = (di != 0 && dj != 0 ? Math.sqrt(2) : 1) * (g.open(ni, nj) ? 1 : SHORE_COST);
                    double c = here + step;
                    long nk = key(ni, nj);
                    Double was = cost.get(nk);
                    if (was != null && c >= was) continue;
                    cost.put(nk, c);
                    prev.put(nk, k);
                    open.add(new double[] { c + Math.hypot(ni - b[0], nj - b[1]), ni, nj });
                }
            }
        }
        if (!found) return null;
        List<long[]> out = new ArrayList<>();
        for (long k = kb; k != Long.MIN_VALUE; k = prev.get(k)) out.add(new long[] { k >> 32, (int) k });
        java.util.Collections.reverse(out);
        return out;
    }

    private static long key(long i, long j) { return (i << 32) | (j & 0xffffffffL); }

    /** String pulling (the prototype's pull()): from each point kept, on to the farthest point the straight line reaches over sea alone. */
    static List<long[]> pull(Grid g, List<long[]> path) {
        List<long[]> out = new ArrayList<>();
        out.add(path.get(0));
        int k = 0;
        while (k < path.size() - 1) {
            int m = path.size() - 1;
            while (m > k + 1 && !g.clear(path.get(k)[0], path.get(k)[1], path.get(m)[0], path.get(m)[1])) m--;
            out.add(path.get(m));
            k = m;
        }
        return out;
    }

    /**
     * Whether the route's last leg and abyss, turned by `turn` radians about
     * the pivot, keep to sea cells: looked at three times a cell along them,
     * past the pivot's own cell and the one beside it (the pivot is the
     * berth when the way runs straight, and a quay's end may share its cell
     * with the shore).
     */
    static boolean tailClear(Grid g, double[] xs, double[] ys, int pivot, double turn) {
        double c = Math.cos(turn), s = Math.sin(turn), px = xs[pivot], py = ys[pivot];
        double ax = px, ay = py;
        for (int i = pivot + 1; i < xs.length; i++) {
            double dx = xs[i] - px, dy = ys[i] - py;
            double bx = px + dx * c - dy * s, by = py + dx * s + dy * c;
            double len = Math.hypot(bx - ax, by - ay);
            int n = (int) Math.ceil(len / CELL * 3) + 1;
            for (int k = 0; k <= n; k++) {
                double x = ax + (bx - ax) * k / n, y = ay + (by - ay) * k / n;
                if (Math.hypot(x - px, y - py) < 1.5 * CELL) continue;
                if (!g.sea(Math.floorDiv((long) Math.floor(x), CELL), Math.floorDiv((long) Math.floor(y), CELL))) return false;
            }
            ax = bx;
            ay = by;
        }
        return true;
    }
}
