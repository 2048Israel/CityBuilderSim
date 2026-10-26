package ham.citybuildersim;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The survey of a long bridge's crossing: the shortest chain of plots from the city's own dry ground on a far landmass
 * to its own buildable ground on a linked one, across water that is FRESH or SHALLOW all the way - found or refused, all
 * or nothing, from the Earth's map read through a small interface (Ground), never from the city's drawn map.
 *
 * WHY THIS EXISTS. spec-earth2-terrain 6.3 (ET6; D13, D14, D16). A bridge is ordered between two places, and the model
 * has to say whether the crossing exists before a dollar is asked for it: a continuous chain of plots, each 8-neighbour
 * of the last (a step 30 m, a diagonal 30 x sqrt(2) m), from an owned dry plot of the far landmass to an owned FLAT or
 * FOOTHILL dry plot of a linked one, every plot between them FRESH or SHALLOW water. A deep SALT plot is a wall, and so
 * is any dry plot that is not a landing. The chain is no longer than MAX_LENGTH_M (36 km, the Hangzhou Bay Bridge's, est.).
 * Nothing is laid until the whole chain exists and the whole order is paid for; there is no half-bridge into the sea.
 * It is a function of the ground alone (Ground), so the harness runs it on a grid of its own making as it runs it on the
 * Earth, and what it finds is what the card shows, what the quote prices and what the saved list keeps.
 *
 * HOW IT SEARCHES. Dijkstra over the plots, from every owned dry plot of the far landmass at once, so that the first
 * landing it reaches is the shortest chain, in a window of the far landmass's owned box widened by SEARCH_FACTOR times the
 * cap (the cost limit is the window's: a chain that long is refused as too long, and one the window cut is "over" it).
 * Costs are whole decimetres (a step 300, a diagonal 424), so ties are exact; a tie is broken toward the heading the chain
 * is already on, so a straight channel gives a straight chain and not a staircase. Terrain is read a 32-plot tile at a
 * time, only where the search goes. The chain is kept as its corner plots, where the heading changes, which is what the
 * saved list holds and the map lays the bridge along.
 */
public final class BridgeSurvey {

    private BridgeSurvey() { }

    /** The longest bridge that can be ordered, metres: 36,000 (est., D13: the Hangzhou Bay Bridge, 36 km, the longest over water). */
    public static final double MAX_LENGTH_M = 36_000;

    /** How far past the cap the search looks, as a multiple of it: 1.25, so a crossing a little over the cap is told its length, and the window past that is "over 45.0 km". */
    public static final double SEARCH_FACTOR = 1.25;

    /** The cost the search is limited to, metres: SEARCH_FACTOR times MAX_LENGTH_M (45 km); also the margin, in plots, the far landmass's owned box is widened by on every side. */
    public static final double SEARCH_LIMIT_M = SEARCH_FACTOR * MAX_LENGTH_M;

    /** A step along a row or a column, in decimetres (a plot is 30 m). */
    static final int ORTHOGONAL_DM = (int) Math.round(World.PLOT_M * 10);

    /** A step along a diagonal, in decimetres: 30 x sqrt(2) m, 424.26 rounded. */
    static final int DIAGONAL_DM = (int) Math.round(World.PLOT_M * 10 * Math.sqrt(2));

    /** The cost limit in decimetres. */
    private static final int LIMIT_DM = (int) Math.round(SEARCH_LIMIT_M * 10);

    /** The margin the window is widened by, in plots. */
    private static final long MARGIN_PLOTS = (long) Math.ceil(SEARCH_LIMIT_M / World.PLOT_M);

    /* =====================================================================
       THE GROUND - what the survey reads
       ===================================================================== */

    /** One tile's plots as the survey reads them: World.TILE x World.TILE, row by row (index py x World.TILE + px). */
    public static final class Tile {
        /** The cover (World.GRASS to World.SHALLOW). */
        public final byte[] cover = new byte[World.TILE * World.TILE];
        /** The relief (World.FLAT to World.VERY_STEEP); read only for owned dry plots. */
        public final byte[] relief = new byte[World.TILE * World.TILE];
        /** The landmass of a dry plot (home's where the data says none); read only for owned dry plots, anything for water. */
        public final int[] landmass = new int[World.TILE * World.TILE];
        /** Whether the city owns the plot. */
        public final boolean[] owned = new boolean[World.TILE * World.TILE];

        /** Clears the tile to open sea, nothing owned. */
        public void clear() {
            Arrays.fill(cover, World.SALT);
            Arrays.fill(relief, World.FLAT);
            Arrays.fill(landmass, 0);
            Arrays.fill(owned, false);
        }
    }

    /** What the survey needs of the world and the city: its home landmass, its linked set, where it owns ground, a tile of terrain and a water plot's depth. */
    public interface Ground {
        /** The home landmass's id. */
        int home();

        /** The landmasses the city's buildings may stand on, ids ascending (Landmasses.closure()). */
        int[] linkedIds();

        /** The tiles the city owns some ground in, as keys (tx << 32 ^ ty & 0xffffffff): see key(). */
        long[] ownedTiles();

        /**
         * Whether a tile's plots can include dry ground of this landmass: false only where the ground knows they cannot (the Earth's
         * landmass layer names no such cell near the tile), so that the search for the far landmass's ground reads no tile that
         * holds none. The answer true is always safe.
         */
        default boolean mayHold(long tx, long ty, int landmass) { return true; }

        /** Fills a tile (clearing it first): cover, ownership, and for an owned dry plot its relief and landmass. */
        void tile(long tx, long ty, Tile out);

        /** The depth, metres, of the water at a plot: 0 on fresh water, which the World holds no depth for. */
        double depthM(long x, long y);
    }

    /** A tile's key as ownedTiles() gives it. */
    public static long key(long tx, long ty) { return (tx << 32) ^ (ty & 0xffffffffL); }

    /* =====================================================================
       THE ANSWER
       ===================================================================== */

    /** Why a crossing is refused, or OK. */
    public enum Why {
        /** A chain was found, no longer than the cap. */
        OK,
        /** Both ends would be on one landmass: the far landmass is the only one the city has linked (or the city owns one). */
        SAME_LANDMASS,
        /** The city owns no dry ground on the far landmass touching water a bridge can cross. */
        NOT_DRY,
        /** The bridgeable water is walled in by deep water: deepM() deep, deepOutM() out from the far shore. */
        DEEP,
        /** Longer than the cap: lengthM() found within the window, or over the window (over()) with no chain found in it. */
        TOO_LONG,
        /** The water ends at ground that is no landing: not owned, steep, or on a landmass the city has not linked. */
        NO_LANDING
    }

    /**
     * A surveyed crossing. For OK, and for TOO_LONG with a chain found: the length, the deepest and mean depth of the
     * water it crosses, the two landing plots and the near landing's landmass, the chain's corner plots (x0, y0, x1, y1,
     * ...: both landings and where the heading changes, from the far shore to the near), and, plot by plot along the whole
     * chain, the step to the next (stepM) and the depth (plotDepthM, the landings 0) - what BridgeWorks prices. For DEEP:
     * the nearest deep plot met, its depth and how far out from the far shore. `over` is a TOO_LONG with no chain found, the
     * window cut the search. plotsExpanded, tiles and millis are for the record of a search.
     */
    public record Result(Why why, int far, double lengthM, boolean over, double deepestM, double meanM, double deepM, double deepOutM,
                         long nearX, long nearY, long farX, long farY, int nearLandmass, long[] corners, double[] stepM, double[] plotDepthM,
                         long plotsExpanded, int tiles, long millis) {

        /** Whether the crossing can be ordered. */
        public boolean ok() { return why == Why.OK; }

        /** The nearest-landing figures are real (OK, or TOO_LONG with a chain found). */
        public boolean found() { return corners != null && corners.length >= 4; }

        static Result refused(Why why, int far) {
            return new Result(why, far, 0, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, null, 0, 0, 0);
        }
    }

    /* =====================================================================
       THE SEARCH
       ===================================================================== */

    // The plot codes the search reads a classified plot by.
    private static final byte WALL = 0, WATER = 1, DEEP = 2, FAR = 3, NEAR = 4;

    /** The direction's step in plots and its cost in decimetres: E, SE, S, SW, W, NW, N, NE (index 8 marks a source). */
    private static final int[] DX = { 1, 1, 0, -1, -1, -1, 0, 1 };
    private static final int[] DY = { 0, 1, 1, 1, 0, -1, -1, -1 };
    private static final byte SOURCE = 8;

    /** One tile of the search: what each plot is, the cost to reach it (decimetres) and the direction it was reached by. */
    private static final class Cell {
        final byte[] code = new byte[World.TILE * World.TILE];
        final int[] cost = new int[World.TILE * World.TILE];
        final byte[] dir = new byte[World.TILE * World.TILE];

        Cell() { Arrays.fill(cost, Integer.MAX_VALUE); }
    }

    /** A binary min-heap of longs. */
    private static final class Heap {
        long[] a = new long[1024];
        int n;

        void push(long v) {
            if (n == a.length) a = Arrays.copyOf(a, n * 2);
            int i = n++;
            while (i > 0) {
                int p = (i - 1) >>> 1;
                if (a[p] <= v) break;
                a[i] = a[p];
                i = p;
            }
            a[i] = v;
        }

        long pop() {
            long top = a[0], v = a[--n];
            int i = 0;
            while (true) {
                int c = 2 * i + 1;
                if (c >= n) break;
                if (c + 1 < n && a[c + 1] < a[c]) c++;
                if (a[c] >= v) break;
                a[i] = a[c];
                i = c;
            }
            a[i] = v;
            return top;
        }
    }

    /**
     * Surveys the crossing from the city's ground on landmass `far` to its buildable ground on a linked landmass other
     * than it. All or nothing: the answer is OK with the shortest chain, or the reason there is none. A pure read of the
     * ground; the same ground gives the same answer.
     */
    public static Result survey(Ground g, int far) {
        long started = System.nanoTime();
        boolean other = false;
        int[] linked = g.linkedIds();
        for (int id : linked) other |= id != far;
        if (!other) return Result.refused(Why.SAME_LANDMASS, far);
        return new Search(g, far, linked).run(started);
    }

    private static final class Search {
        final Ground g;
        final int far;
        final int[] linked;
        final Tile scratch = new Tile();
        final Map<Long, Cell> cells = new HashMap<>();
        long wx0, wy0, wx1, wy1;   // the window, in plots, half-open
        long tilesRead, expanded;
        // The last cell asked for.
        long lastKey = Long.MIN_VALUE;
        Cell last;

        Search(Ground g, int far, int[] linked) {
            this.g = g;
            this.far = far;
            this.linked = linked;
        }

        boolean isLinked(int id) {
            for (int l : linked) if (l == id) return true;
            return false;
        }

        /** What a plot of the scratch tile is, as the search sees it. */
        byte classify(int i) {
            int c = scratch.cover[i];
            if (c == World.FRESH || c == World.SHALLOW) return WATER;
            if (c == World.SALT) return DEEP;
            if (!World.dry(c) || !scratch.owned[i]) return WALL;
            int lm = scratch.landmass[i];
            if (lm == far) return FAR;
            return World.buildable(c, scratch.relief[i]) && isLinked(lm) ? NEAR : WALL;
        }

        Cell cell(long tx, long ty) {
            long k = key(tx, ty);
            if (k == lastKey && last != null) return last;
            Cell c = cells.get(k);
            if (c == null) {
                c = new Cell();
                g.tile(tx, ty, scratch);
                tilesRead++;
                for (int i = 0; i < c.code.length; i++) c.code[i] = classify(i);
                cells.put(k, c);
            }
            lastKey = k;
            last = c;
            return c;
        }

        static int at(long x, long y) {
            return (int) (Math.floorMod(y, (long) World.TILE) * World.TILE + Math.floorMod(x, (long) World.TILE));
        }

        long pack(int cost, long x, long y) {
            return ((long) cost << 42) | ((x - wx0) << 21) | (y - wy0);
        }

        Result run(long started) {
            // The seeds: every owned dry plot of the far landmass, and the box they lie in.
            long minX = Long.MAX_VALUE, minY = Long.MAX_VALUE, maxX = Long.MIN_VALUE, maxY = Long.MIN_VALUE;
            long[] sx = new long[256], sy = new long[256];
            int seeds = 0;
            for (long tk : g.ownedTiles()) {
                long tx = tk >> 32, ty = (int) tk;
                if (!g.mayHold(tx, ty, far)) continue;
                g.tile(tx, ty, scratch);
                tilesRead++;
                for (int i = 0; i < scratch.cover.length; i++) {
                    if (!scratch.owned[i] || !World.dry(scratch.cover[i]) || scratch.landmass[i] != far) continue;
                    long x = tx * World.TILE + i % World.TILE, y = ty * World.TILE + i / World.TILE;
                    if (seeds == sx.length) { sx = Arrays.copyOf(sx, seeds * 2); sy = Arrays.copyOf(sy, seeds * 2); }
                    sx[seeds] = x;
                    sy[seeds] = y;
                    seeds++;
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
            if (seeds == 0) return finish(Result.refused(Why.NOT_DRY, far), started);
            wx0 = minX - MARGIN_PLOTS;
            wy0 = minY - MARGIN_PLOTS;
            wx1 = maxX + 1 + MARGIN_PLOTS;
            wy1 = maxY + 1 + MARGIN_PLOTS;

            Heap heap = new Heap();
            for (int s = 0; s < seeds; s++) {
                Cell c = cell(Math.floorDiv(sx[s], (long) World.TILE), Math.floorDiv(sy[s], (long) World.TILE));
                int i = at(sx[s], sy[s]);
                c.cost[i] = 0;
                c.dir[i] = SOURCE;
                heap.push(pack(0, sx[s], sy[s]));
            }

            boolean cut = false, touched = false;
            long expandedWater = 0;
            // The nearest deep plot the search met in bridgeable water, and, apart, the nearest the far shore itself touches (the sea
            // round the island, told only when no bridgeable water was reached at all).
            int deepCost = Integer.MAX_VALUE, shoreDeepCost = Integer.MAX_VALUE;
            long deepX = 0, deepY = 0, shoreDeepX = 0, shoreDeepY = 0;
            long goal = Long.MIN_VALUE, goalX = 0, goalY = 0;
            final long mask = (1L << 21) - 1;
            while (heap.n > 0) {
                long top = heap.pop();
                int cost = (int) (top >>> 42);
                long x = wx0 + ((top >>> 21) & mask), y = wy0 + (top & mask);
                Cell c = cell(Math.floorDiv(x, (long) World.TILE), Math.floorDiv(y, (long) World.TILE));
                int i = at(x, y);
                if (c.cost[i] != cost) continue;                 // a stale entry: a cheaper way was found since
                byte code = c.code[i];
                if (code == NEAR) { goal = top; goalX = x; goalY = y; break; }
                expanded++;
                byte heading = c.dir[i];
                boolean source = code == FAR;
                if (!source) expandedWater++;
                for (int d = 0; d < 8; d++) {
                    long nx = x + DX[d], ny = y + DY[d];
                    if (nx < wx0 || nx >= wx1 || ny < wy0 || ny >= wy1) { cut = true; continue; }
                    Cell nc = cell(Math.floorDiv(nx, (long) World.TILE), Math.floorDiv(ny, (long) World.TILE));
                    int ni = at(nx, ny);
                    byte ncode = nc.code[ni];
                    if (ncode == WALL || ncode == FAR) continue;
                    int step = (d & 1) == 0 ? ORTHOGONAL_DM : DIAGONAL_DM;
                    int next = cost + step;
                    if (source) touched = true;
                    if (ncode == DEEP) {
                        if (source) {
                            if (next < shoreDeepCost) { shoreDeepCost = next; shoreDeepX = nx; shoreDeepY = ny; }
                        } else if (next < deepCost) { deepCost = next; deepX = nx; deepY = ny; }
                        continue;
                    }
                    if (next > LIMIT_DM) { cut = true; continue; }
                    if (next < nc.cost[ni]) {
                        nc.cost[ni] = next;
                        nc.dir[ni] = (byte) d;
                        heap.push(pack(next, nx, ny));
                    } else if (next == nc.cost[ni] && nc.dir[ni] != d && d == heading) {
                        nc.dir[ni] = (byte) d;                   // a tie: keep the heading the chain is on
                    }
                }
            }

            if (goal != Long.MIN_VALUE) return finish(chain(goalX, goalY), started);
            if (cut) return finish(new Result(Why.TOO_LONG, far, SEARCH_LIMIT_M, true, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, null, 0, 0, 0), started);
            if (deepCost != Integer.MAX_VALUE) {
                return finish(new Result(Why.DEEP, far, 0, false, 0, 0, g.depthM(deepX, deepY), deepCost / 10.0, 0, 0, 0, 0, 0, null, null, null, 0, 0, 0), started);
            }
            // No bridgeable water was reached: if the far shore touches deep water, that is the reason.
            if (expandedWater == 0 && shoreDeepCost != Integer.MAX_VALUE) {
                return finish(new Result(Why.DEEP, far, 0, false, 0, 0, g.depthM(shoreDeepX, shoreDeepY), shoreDeepCost / 10.0, 0, 0, 0, 0, 0, null, null, null, 0, 0, 0), started);
            }
            return finish(Result.refused(touched ? Why.NO_LANDING : Why.NOT_DRY, far), started);
        }

        /** The answer with the record of the search filled in. */
        Result finish(Result r, long started) {
            return new Result(r.why(), r.far(), r.lengthM(), r.over(), r.deepestM(), r.meanM(), r.deepM(), r.deepOutM(),
                    r.nearX(), r.nearY(), r.farX(), r.farY(), r.nearLandmass(), r.corners(), r.stepM(), r.plotDepthM(),
                    expanded, (int) tilesRead, (System.nanoTime() - started) / 1_000_000);
        }

        /** The chain back from the near landing to the far shore it began on: OK, or TOO_LONG when longer than the cap. */
        Result chain(long gx, long gy) {
            int n = 1;
            long x = gx, y = gy;
            while (true) {
                byte dir = cell(Math.floorDiv(x, (long) World.TILE), Math.floorDiv(y, (long) World.TILE)).dir[at(x, y)];
                if (dir == SOURCE) break;
                x -= DX[dir];
                y -= DY[dir];
                n++;
            }
            long[] px = new long[n], py = new long[n];
            x = gx;
            y = gy;
            for (int k = n - 1; k >= 0; k--) {          // far shore first
                px[k] = x;
                py[k] = y;
                byte dir = cell(Math.floorDiv(x, (long) World.TILE), Math.floorDiv(y, (long) World.TILE)).dir[at(x, y)];
                if (dir == SOURCE) break;
                x -= DX[dir];
                y -= DY[dir];
            }
            double[] step = new double[n - 1];
            double[] depth = new double[n];
            double length = 0, deepest = 0, sum = 0;
            int water = 0;
            long[] corners = new long[2 * n];
            int cs = 0;
            corners[cs++] = px[0];
            corners[cs++] = py[0];
            for (int k = 0; k < n; k++) {
                if (k > 0) {
                    boolean diagonal = px[k] != px[k - 1] && py[k] != py[k - 1];
                    step[k - 1] = diagonal ? World.PLOT_M * Math.sqrt(2) : World.PLOT_M;
                    length += step[k - 1];
                }
                if (k > 0 && k < n - 1) {
                    depth[k] = g.depthM(px[k], py[k]);
                    deepest = Math.max(deepest, depth[k]);
                    sum += depth[k];
                    water++;
                    // A corner: the heading changes at this plot.
                    if ((px[k] - px[k - 1]) != (px[k + 1] - px[k]) || (py[k] - py[k - 1]) != (py[k + 1] - py[k])) {
                        corners[cs++] = px[k];
                        corners[cs++] = py[k];
                    }
                }
            }
            corners[cs++] = px[n - 1];
            corners[cs++] = py[n - 1];
            g.tile(Math.floorDiv(gx, (long) World.TILE), Math.floorDiv(gy, (long) World.TILE), scratch);
            tilesRead++;
            int nearLandmass = scratch.landmass[at(gx, gy)];
            Why why = length > MAX_LENGTH_M ? Why.TOO_LONG : Why.OK;
            return new Result(why, far, length, false, deepest, water == 0 ? 0 : sum / water, 0, 0, gx, gy, px[0], py[0], nearLandmass,
                    Arrays.copyOf(corners, cs), step, depth, 0, 0, 0);
        }
    }
}
