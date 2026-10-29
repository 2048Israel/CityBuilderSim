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
 *
 * SHARED LANES (SL1, 2026-10-28; Jerus: "if lots of ship lanes, its not
 * different lanes, its same lane that later diverages if needed"; "late
 * cities thats probably the thing that slows down stuff the most"). Every
 * terminal used to find its own way from scratch - the 72 bearings, an A* of
 * up to EXPANSIONS_MOST cells - and every berth laid found them all again, so
 * a late city's routes were its slowest thing, and its map drew a lane for
 * each terminal side by side. The berths are routed now in the order the
 * shore laid them, each against the routes before it:
 *   JOIN        a Dijkstra over the sea's cells from the berth's start (eight
 *               ways, a shore cell SHORE_COST times an open one, as the way
 *               out of a bay), no cell farther than JOIN_CELLS from it, stops
 *               at the first cell a joinable lane covers - a leg of a route
 *               from its berth up to its offing, sampled three times a cell
 *               (Lanes). A lane is joinable when it was found by a search or
 *               itself joined, and its offing lies at least (reach - 1) cells
 *               from this berth's start, so every route's offing stays the
 *               city's radius plus OFFING_M out from its berth;
 *   THE ROUTE   then the berth, the way to the lane pulled straight, the join
 *               point (the point on the covering leg nearest the cell's
 *               middle) and the lane's own points after that leg, the same
 *               doubles (BoatSchedule's boats ride them), its offing, pivot and
 *               spread the lane's (a join on the lane's last leg makes the join
 *               point the pivot, and the spread is looked at again from it);
 *   ELSE        the berth is searched, as above, and is a lane for those after.
 * A route depends on the routes before it alone, so those found already for
 * the first berths are kept when berths are added (extend()). The map finds
 * them at the radius rounded up to a multiple of REFIND_M (band()). And a
 * search whose straight line from the berth's cell to the offing keeps to the
 * sea takes the two points as THE WAY, which is what pull() would make of any
 * way the A* found between them: the A* of a way off the bearings that are
 * multiples of 45 degrees took 50 ms and tens of thousands of cells for it.
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

    /** How far by water a terminal looks for a lane to join, in metres: OFFING_M, the offing's own margin (SL1, 2026-10-28). */
    public static final double JOIN_M = OFFING_M;

    /** ...in cells of CELL plots, rounded up: 21 (no cell of a join's search is farther from the berth's start in a straight line). */
    public static final int JOIN_CELLS = (int) Math.ceil(JOIN_M / World.PLOT_M / CELL);

    /** How much the city's radius may grow before the routes are found again, in metres: OFFING_M / 2, 5 km - they are found at the radius rounded up to a multiple of it (band(); SL1, 2026-10-28). */
    public static final double REFIND_M = OFFING_M / 2;

    /** Cells a side of a block of the grid read at once: 64, 1,024 plots (a block a World.regionTerrain() call). */
    static final int BLOCK = 64;

    /** Each terminal's route, in the berths' order, each against those before it (pure in the world, the berths, the site and the radius). */
    public static List<BoatSchedule.Route> of(World world, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius) {
        return find(world, berths, siteX, siteY, radius).routes();
    }

    /**
     * The routes found, and how: how many berths were searched (route(): the 72 bearings and an A*, a straight lane
     * where the sea gave no way) and how many joined a lane (join()), of the berths this call routed; for each of
     * the routes, `joinAt`: the index among its points of the join point it was joined at (the lane's own points
     * follow it), or -1 for a route searched or kept.
     */
    public record Found(List<BoatSchedule.Route> routes, int searched, int joined, int[] joinAt) { }

    /** ...every berth's route, with how each was found. */
    public static Found find(World world, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius) {
        return extend(world, List.of(), berths, siteX, siteY, radius);
    }

    /**
     * The routes of `berths` given those already found for its first berths (`kept`, the same routes of() or an
     * earlier extend() made, for the same world, site and radius): the rest routed onto them, equal point for point to
     * of() on the whole list. The kept routes are in the result as they are, the same objects.
     */
    public static Found extend(World world, List<BoatSchedule.Route> kept, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius) {
        if (berths == null) berths = List.of();
        if (berths.isEmpty() && kept.isEmpty()) return new Found(List.of(), 0, 0, new int[0]);
        return extend(new Grid(world), kept, berths, siteX, siteY, radius);
    }

    /** ...on a grid given (a harness's, from a mask of the sea). */
    static Found find(Grid g, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius) {
        return extend(g, List.of(), berths, siteX, siteY, radius);
    }

    /** ...on a grid given. */
    static Found extend(Grid g, List<BoatSchedule.Route> kept, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius) {
        if (kept.size() > berths.size()) throw new IllegalArgumentException("more routes kept than berths: " + kept.size() + " > " + berths.size());
        Router r = new Router(g, siteX, siteY, radius, berths.size());
        for (int i = 0; i < kept.size(); i++) {
            if (!kept.get(i).berth().equals(berths.get(i))) throw new IllegalArgumentException("kept route " + i + " is not of berth " + i);
            r.keep(kept.get(i));
        }
        for (int i = kept.size(); i < berths.size(); i++) r.next(berths.get(i));
        return new Found(java.util.Collections.unmodifiableList(r.lanes.routes), r.searched, r.joined, r.joinAt);
    }

    /** The radius the routes are found at, in plots: `radius` rounded up to a multiple of REFIND_M, never below it (so the offing stays at least the radius plus OFFING_M out from the berth). */
    public static double band(double radius) {
        double q = REFIND_M / World.PLOT_M, b = Math.ceil(radius / q) * q;
        return b < radius ? b + q : b;
    }

    /** The cells of the sea as a fixture says them (a harness's way to cause each condition): true where the cell is sea. */
    @FunctionalInterface
    interface Sea {
        boolean at(long ci, long cj);
    }

    /** A grid over a mask of the sea: what `sea` answers, nothing read from the world (off the mask is whatever it answers). */
    static Grid grid(Sea sea) {
        return new Grid(sea);
    }

    /* =====================================================================
       THE GRID
       ===================================================================== */

    /** The sea's cells, read a block at a time as asked. */
    static final class Grid {
        final World world;
        /** A fixture's mask of the sea (SL1), or null: the world's ground is read. */
        final Sea mask;
        final Map<Long, boolean[]> blocks = new HashMap<>();
        /** Each block's cells' openness as worked out: 0 not yet, 1 the shore's, 2 open water. */
        final Map<Long, byte[]> opens = new HashMap<>();
        final byte[] buf;
        int read;

        Grid(World world) { this.world = world; this.mask = null; this.buf = new byte[(BLOCK * CELL / SAMPLE) * (BLOCK * CELL / SAMPLE)]; }

        Grid(Sea mask) { this.world = null; this.mask = mask; this.buf = null; }

        /** Whether cell (ci, cj) is sea: at least SALT_LEAST of its samples salt; off the world, sea. */
        boolean sea(long ci, long cj) {
            if (mask != null) return mask.at(ci, cj);
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
                    if (!World.salt(buf[p])) continue;
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

    /** How far out the offing lies from a berth's start cell for a city of this radius, in cells: the radius plus OFFING_M, at least 1, at most RAY_CELLS. */
    static int reachOf(double radius) {
        double want = (radius + OFFING_M / World.PLOT_M) / CELL;
        return (int) Math.max(1, Math.min(Math.ceil(want), RAY_CELLS));
    }

    /** A berth's route, searched (SeaRoutes' rule). */
    static BoatSchedule.Route route(Grid g, BoatSchedule.Berth b, long siteX, long siteY, double radius) {
        return route(g, b, nearestSea(g, Math.floorDiv(b.x(), CELL), Math.floorDiv(b.y(), CELL)), siteX, siteY, radius);
    }

    /** ...from its start cell, which nearestSea() found (null for none: the straight lane). */
    static BoatSchedule.Route route(Grid g, BoatSchedule.Berth b, long[] start, long siteX, long siteY, double radius) {
        double bx = b.x() + 0.5, by = b.y() + 0.5;
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
        int reach = reachOf(radius);
        double r = Math.toRadians(bestDeg);
        long oi, oj;
        List<long[]> path;
        if (bestRun >= reach) {
            // Open water ahead: the offing on its bearing; THE WAY: A* over the sea's cells to it.
            oi = Math.round(start[0] + Math.cos(r) * reach);
            oj = Math.round(start[1] + Math.sin(r) * reach);
            // SL1: when the straight line from the start to the offing keeps to the sea, pull() would take whatever way the A*
            // found for those two points alone (its first test is that very line), and a way of 400 cells off the bearings
            // that are a multiple of 45 degrees takes the A* tens of thousands of cells and 50 ms: so the two points are the way.
            long[] goal = { oi, oj };
            boolean straight = (oi != start[0] || oj != start[1]) && g.clear(start[0], start[1], oi, oj);
            path = straight ? List.of(start, goal) : astar(g, start, goal);
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
            List<long[]> pulled = pull(g, path);
            for (int k = 0; k < pulled.size(); k++) {
                long[] c = pulled.get(k);
                double cx = c[0] * CELL + CELL / 2.0, cy = c[1] * CELL + CELL / 2.0;
                if (pts.size() == 1 && c[0] == start[0] && c[1] == start[1] && path.size() > 1) {
                    // The berth's own cell's middle is passed through only when it is not on the way - and only when the
                    // berth's own straight leg to the next keeps to the sea (FR3, 2026-10-19): pull() clears the line from
                    // the cell's middle, and the leg from the berth beside it can graze a headland further out (MapCheck 9,
                    // a Bulk Berth at the shore's end, a cell of land 30 cells out).
                    long[] next = k + 1 < pulled.size() ? pulled.get(k + 1) : null;
                    if (Math.hypot(cx - bx, cy - by) < CELL / 2.0
                            && (next == null || legOnSea(g, bx, by, next[0] * CELL + CELL / 2.0, next[1] * CELL + CELL / 2.0))) continue;
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

    /* =====================================================================
       SHARED LANES (SL1, 2026-10-28)
       ===================================================================== */

    /** The routes of one call, in the berths' order: each joins a lane of those before it (join()) or is searched (route()). */
    private static final class Router {
        /** A join's scratch window: JOIN_CELLS either way of the berth's start cell. */
        static final int SIDE = 2 * JOIN_CELLS + 1;

        final Grid g;
        final long siteX, siteY;
        final double radius;
        final int reach;
        final Lanes lanes = new Lanes();
        final int[] joinAt;
        int searched, joined;

        /** The window's cells: the cost reached, the cell it was reached from, and the join they were last reached in (a stamp, so nothing is cleared). */
        private final double[] cost = new double[SIDE * SIDE];
        private final int[] prev = new int[SIDE * SIDE], mark = new int[SIDE * SIDE];
        private int gen;
        /** The cells reached and not yet looked at, a binary heap by cost, then by cell. */
        private double[] heapCost = new double[256];
        private int[] heapAt = new int[256];
        private int heapN;

        Router(Grid g, long siteX, long siteY, double radius, int berths) {
            this.g = g;
            this.siteX = siteX;
            this.siteY = siteY;
            this.radius = radius;
            this.reach = reachOf(radius);
            this.joinAt = new int[berths];
            java.util.Arrays.fill(joinAt, -1);
        }

        /** A route found already, for the next berth: it is a lane for those after. */
        void keep(BoatSchedule.Route r) {
            lanes.add(r);
        }

        /** The next berth's route: joined to a lane when one is in reach, else searched. */
        void next(BoatSchedule.Berth b) {
            long[] start = nearestSea(g, Math.floorDiv(b.x(), CELL), Math.floorDiv(b.y(), CELL));
            int at = lanes.routes.size();
            BoatSchedule.Route r = start == null || lanes.count == 0 ? null : join(at, b, start);
            if (r == null) {
                searched++;
                r = route(g, b, start, siteX, siteY, radius);
            } else {
                joined++;
            }
            lanes.add(r);
        }

        /**
         * The berth's route by a lane (SL1): a Dijkstra over the sea's cells from `start` (eight ways, a shore cell
         * SHORE_COST times an open one, as outOfTheBay() costs them), no cell farther than JOIN_CELLS from it in a
         * straight line, at most EXPANSIONS_MOST looked at, stopping at the first one looked at that a lane the berth
         * may join covers (Lanes.covering()); then assemble(). Null when no lane is reached.
         */
        private BoatSchedule.Route join(int at, BoatSchedule.Berth b, long[] start) {
            gen++;
            heapN = 0;
            int centre = JOIN_CELLS * SIDE + JOIN_CELLS;
            cost[centre] = 0;
            prev[centre] = -1;
            mark[centre] = gen;
            push(0, centre);
            int hit = -1, looked = 0;
            int[] cover = null;
            while (heapN > 0 && looked < EXPANSIONS_MOST) {
                double c = heapCost[0];
                int idx = heapAt[0];
                pop();
                if (c > cost[idx] + 1e-9) continue;
                looked++;
                int li = idx % SIDE, lj = idx / SIDE;
                cover = lanes.covering(start[0] + li - JOIN_CELLS, start[1] + lj - JOIN_CELLS, start, reach);
                if (cover != null) { hit = idx; break; }
                for (int dj = -1; dj <= 1; dj++) {
                    for (int di = -1; di <= 1; di++) {
                        if (di == 0 && dj == 0) continue;
                        int ni = li + di, nj = lj + dj;
                        if (ni < 0 || nj < 0 || ni >= SIDE || nj >= SIDE) continue;
                        int ox = ni - JOIN_CELLS, oy = nj - JOIN_CELLS;
                        if (ox * ox + oy * oy > JOIN_CELLS * JOIN_CELLS) continue;
                        long gi = start[0] + ox, gj = start[1] + oy;
                        if (!g.sea(gi, gj)) continue;
                        double nc = c + (di != 0 && dj != 0 ? Math.sqrt(2) : 1) * (g.open(gi, gj) ? 1 : SHORE_COST);
                        int nidx = nj * SIDE + ni;
                        if (mark[nidx] == gen && nc >= cost[nidx]) continue;
                        mark[nidx] = gen;
                        cost[nidx] = nc;
                        prev[nidx] = idx;
                        push(nc, nidx);
                    }
                }
            }
            if (hit < 0) return null;
            List<long[]> path = new ArrayList<>();
            for (int k = hit; k >= 0; k = prev[k]) path.add(new long[] { start[0] + k % SIDE - JOIN_CELLS, start[1] + k / SIDE - JOIN_CELLS });
            java.util.Collections.reverse(path);
            return assemble(at, b, start, path, cover);
        }

        /**
         * The joined route: the berth, the way to the covering cell pulled straight (pull(), with route()'s rule for the
         * berth's own cell and its FR3 check of the berth's first leg), the join point, then the lane's own points after
         * the covering leg, copied. Its offing and pivot are the lane's, shifted; its spread the lane's, looked at again
         * (tailClear()) when the join point is the pivot, a join on the lane's last leg.
         */
        private BoatSchedule.Route assemble(int at, BoatSchedule.Berth b, long[] start, List<long[]> path, int[] cover) {
            BoatSchedule.Route q = lanes.routes.get(cover[0]);
            int leg = cover[1];
            double[] qx = q.xs(), qy = q.ys();
            double bx = b.x() + 0.5, by = b.y() + 0.5;
            long[] hitCell = path.get(path.size() - 1);
            double[] jp = joinPoint(g, q, leg, hitCell[0], hitCell[1]);
            List<double[]> pts = new ArrayList<>();
            pts.add(new double[] { bx, by });
            List<long[]> pulled = pull(g, path);
            for (int k = 0; k < pulled.size(); k++) {
                long[] c = pulled.get(k);
                double cx = c[0] * CELL + CELL / 2.0, cy = c[1] * CELL + CELL / 2.0;
                if (pts.size() == 1 && c[0] == start[0] && c[1] == start[1]) {
                    double nx = jp[0], ny = jp[1];
                    if (k + 1 < pulled.size()) { nx = pulled.get(k + 1)[0] * CELL + CELL / 2.0; ny = pulled.get(k + 1)[1] * CELL + CELL / 2.0; }
                    if (Math.hypot(cx - bx, cy - by) < CELL / 2.0 && legOnSea(g, bx, by, nx, ny)) continue;
                }
                pts.add(new double[] { cx, cy });
            }
            // The join point, once: it may be where the way ends already, or the lane's next point.
            double[] last = pts.get(pts.size() - 1);
            int joinIdx;
            if (last[0] == jp[0] && last[1] == jp[1]) {
                joinIdx = pts.size() - 1;
            } else if (qx[leg + 1] == jp[0] && qy[leg + 1] == jp[1]) {
                joinIdx = pts.size();
            } else {
                joinIdx = pts.size();
                pts.add(jp);
            }
            last = pts.get(pts.size() - 1);
            int first = leg + 1;
            if (last[0] == qx[first] && last[1] == qy[first]) first++;
            int base = pts.size();
            for (int m = first; m < qx.length; m++) pts.add(new double[] { qx[m], qy[m] });
            // The lane's points keep their order and their doubles: a point m of it is at base + m - first, and the one before the first copied (the lane's own next point after the covering leg, the way's last) at base - 1.
            int offing = (q.offing() >= first ? base + q.offing() - first : base - 1);
            boolean pivotMoves = q.pivot() < leg + 1;
            int pivot = pivotMoves ? joinIdx : (q.pivot() >= first ? base + q.pivot() - first : base - 1);
            int n = pts.size();
            double[] xs = new double[n], ys = new double[n];
            for (int i = 0; i < n; i++) { xs[i] = pts.get(i)[0]; ys[i] = pts.get(i)[1]; }
            double spread = q.spread();
            if (pivotMoves && spread > 0) {
                for (double turn : new double[] { -spread, -spread / 2, spread / 2, spread }) {
                    if (!tailClear(g, xs, ys, pivot, turn)) { spread = 0; break; }
                }
            }
            joinAt[at] = joinIdx;
            return BoatSchedule.Route.through(b, xs, ys, offing, pivot, spread);
        }

        private boolean before(int a, int b) {
            return heapCost[a] < heapCost[b] || (heapCost[a] == heapCost[b] && heapAt[a] < heapAt[b]);
        }

        private void push(double c, int at) {
            if (heapN == heapAt.length) {
                heapCost = java.util.Arrays.copyOf(heapCost, heapN * 2);
                heapAt = java.util.Arrays.copyOf(heapAt, heapN * 2);
            }
            int i = heapN++;
            heapCost[i] = c;
            heapAt[i] = at;
            while (i > 0) {
                int p = (i - 1) >>> 1;
                if (!before(i, p)) break;
                swap(i, p);
                i = p;
            }
        }

        /** Takes the heap's first away. */
        private void pop() {
            heapN--;
            if (heapN == 0) return;
            heapCost[0] = heapCost[heapN];
            heapAt[0] = heapAt[heapN];
            int i = 0;
            while (true) {
                int l = 2 * i + 1, r = l + 1, m = i;
                if (l < heapN && before(l, m)) m = l;
                if (r < heapN && before(r, m)) m = r;
                if (m == i) break;
                swap(i, m);
                i = m;
            }
        }

        private void swap(int a, int b) {
            double c = heapCost[a];
            heapCost[a] = heapCost[b];
            heapCost[b] = c;
            int t = heapAt[a];
            heapAt[a] = heapAt[b];
            heapAt[b] = t;
        }
    }

    /**
     * The lanes a berth may join: the legs of the routes found by a search or joined (a straight lane, two points, is
     * never one), each from its berth up to its offing and never the abyss's, indexed by the sea cell each passes
     * through, sampled three times a cell as legOnSea() looks. A leg is indexed once however many routes share it - the
     * same four doubles, or a part of one that ends where it does, as a joiner has - under the first route that has it.
     */
    private static final class Lanes {
        /** The routes taken in, in order (all of them, whether they are lanes or not). */
        final List<BoatSchedule.Route> routes = new ArrayList<>();
        /** How many of them are lanes. */
        int count;
        /** Each cell's legs: route << 24 | leg, in the order taken in. */
        private final Map<Long, long[]> cells = new HashMap<>();
        /** The legs indexed, each once (SeaLanes.Legs: a leg kept already, or lying on one that ends where it does, is not indexed again). */
        private final SeaLanes.Legs kept = new SeaLanes.Legs();

        /** The next route taken in: indexed when it is a lane. */
        void add(BoatSchedule.Route r) {
            int ri = routes.size();
            routes.add(r);
            if (r.xs().length < 3) return;
            count++;
            double[] xs = r.xs(), ys = r.ys();
            for (int i = 0; i < r.offing(); i++) {
                if (!kept.add(xs[i], ys[i], xs[i + 1], ys[i + 1])) continue;
                long entry = ((long) ri << 24) | i;
                int n = samples(xs[i], ys[i], xs[i + 1], ys[i + 1]);
                long lastI = Long.MIN_VALUE, lastJ = Long.MIN_VALUE;
                for (int k = 0; k <= n; k++) {
                    long ci = cellOf(xs[i] + (xs[i + 1] - xs[i]) * k / n), cj = cellOf(ys[i] + (ys[i + 1] - ys[i]) * k / n);
                    if (ci == lastI && cj == lastJ) continue;
                    lastI = ci;
                    lastJ = cj;
                    long[] now = cells.get(key(ci, cj));
                    if (now == null) {
                        cells.put(key(ci, cj), new long[] { entry });
                    } else if (now[now.length - 1] != entry) {
                        long[] more = java.util.Arrays.copyOf(now, now.length + 1);
                        more[now.length] = entry;
                        cells.put(key(ci, cj), more);
                    }
                }
            }
        }

        /** The samples along a leg, three a cell as legOnSea() takes them (the last index; there is one more). */
        static int samples(double ax, double ay, double bx, double by) {
            return (int) Math.ceil(Math.hypot(bx - ax, by - ay) / CELL * 3) + 1;
        }

        static long cellOf(double v) {
            return Math.floorDiv((long) Math.floor(v), CELL);
        }

        /**
         * The lane covering cell (ci, cj) that a berth whose start cell is `start` may join, as {route, leg}, or null:
         * the first in the order taken in whose offing lies at least (reach - 1) cells from the start.
         */
        int[] covering(long ci, long cj, long[] start, int reach) {
            long[] es = cells.get(key(ci, cj));
            if (es == null) return null;
            for (long e : es) {
                int ri = (int) (e >>> 24);
                BoatSchedule.Route q = routes.get(ri);
                double ox = q.xs()[q.offing()] / CELL - 0.5, oy = q.ys()[q.offing()] / CELL - 0.5;
                if (Math.hypot(ox - start[0], oy - start[1]) >= reach - 1) return new int[] { ri, (int) (e & 0xFFFFFF) };
            }
            return null;
        }
    }

    /**
     * Where a berth's way meets leg `leg` of lane q, which covers cell (ci, cj): the point on the leg nearest the
     * cell's middle; or, where the straight way from the middle to it would leave the sea (the point lies in the
     * next cell over), the leg's own sample in the cell nearest the middle.
     */
    private static double[] joinPoint(Grid g, BoatSchedule.Route q, int leg, long ci, long cj) {
        double ax = q.xs()[leg], ay = q.ys()[leg], bx = q.xs()[leg + 1], by = q.ys()[leg + 1];
        double mx = ci * CELL + CELL / 2.0, my = cj * CELL + CELL / 2.0;
        double dx = bx - ax, dy = by - ay, l2 = dx * dx + dy * dy;
        double t = l2 > 0 ? Math.max(0, Math.min(1, ((mx - ax) * dx + (my - ay) * dy) / l2)) : 0;
        double jx = ax + dx * t, jy = ay + dy * t;
        if (legOnSea(g, mx, my, jx, jy)) return new double[] { jx, jy };
        int n = Lanes.samples(ax, ay, bx, by);
        double best = Double.MAX_VALUE;
        for (int k = 0; k <= n; k++) {
            double x = ax + dx * k / n, y = ay + dy * k / n, d = Math.hypot(x - mx, y - my);
            if (Lanes.cellOf(x) == ci && Lanes.cellOf(y) == cj && d < best) { best = d; jx = x; jy = y; }
        }
        return new double[] { jx, jy };
    }
}
