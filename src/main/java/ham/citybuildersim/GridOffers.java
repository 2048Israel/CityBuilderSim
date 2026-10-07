package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The city's offers on the block grid: on each of its four sides six places, left to right facing out, and in each place one standing offer - a rectangle of whole blocks against the city's edge - or none while that side has no room for it.
 *
 * WHY THIS EXISTS (0.7.65, batch M1; the project's spec-grid.md 2.2). The
 * land office of spec-land sold bands of forty wedge-shaped lanes, so an
 * offer was a curved sliver whose ground had to be sampled, and a lane bought
 * often grew a finger out of the city. On the grid an offer is a rectangle
 * of LandGrid blocks: its ground is whole plots, and the next offer in the
 * same place is the innermost free ground of its lane, so the city fills
 * out. Jerus, 2026-10-07: six offers a side, never rerolled.
 *
 * SIDE AND PLACE (spec-grid star 3). A block is seen from the founding
 * site's plot centre, as LegacyLand.sideLane() sees a point: its side by the
 * larger of |dx| and |dy| (ties to North or South), and its place by its
 * lane coordinate t (across over out, -1 to 1, left to right facing out),
 * the place's lane holding t from -1 + 2j / PLACES to -1 + 2(j + 1) / PLACES.
 *
 * THE FRONTIER of a side at a level: its quarter's blocks that are not
 * wholly owned and touch owned ground - partly owned, or with an owned plot
 * across an edge. A block is taken when it meets a standing offer.
 *
 * LISTING A PLACE (spec-grid star 4):
 *   1. the seed: the free frontier block in the place's lane nearest the
 *      site, ties to the lane's middle; with none, the side's free frontier
 *      block nearest the lane's middle; with none at the city's level, the
 *      same one level finer (never under LandGrid.MIN_LEVEL); with none at
 *      all, the place waits empty and is tried again after every purchase;
 *   2. the rectangle: w = round(sqrt(P) / PLACES / 2^k) blocks across (P the
 *      plots owned, k the level), at least one and at most a sixth of the
 *      side's frontier when that is one or more, and DEPTH_OVER_WIDTH x w
 *      deep (star 5), its columns centred on the seed, the odd one toward
 *      the lane's middle;
 *   3. the clip: while it meets a standing offer, the columns on that
 *      offer's side of the seed go, or the rows from that offer's out; then
 *      outer rows and columns the city wholly owns are trimmed off, and it is
 *      held to 2:1 either way.
 * Its ground is the rectangle's unowned plots (LandGrid.unowned()), so a
 * coarse offer takes in the finer steps beside it and every gap can be
 * offered. What the ground holds and what it costs are LandMarket's since
 * 0.7.67 (batch M3; spec-grid 3): it lists each place through list() and
 * stands the offer here.
 *
 * BUYING claims exactly the rectangle's unowned plots and lists the place's
 * next offer; the other places stand as they were, because standing
 * rectangles never meet (GridCheck 5, after each of 600 purchases).
 */
public final class GridOffers {

    /** Sides of the city: North, East, South and West, in CityLand's order. */
    public static final int SIDES = CityLand.SIDES;

    /** Places on a side, each with one offer standing: six, left to right facing out (Jerus, 2026-10-07). */
    public static final int PLACES = 6;

    /** An offer's rows deep over its blocks across: two, the long side outward - the largest six a side allow at 2:1 (spec-grid star 5). */
    public static final int DEPTH_OVER_WIDTH = 2;

    /** How many times a listing may clip against the standing offers: 4,096, far past the 24 there are, so a loop that never settles stops. */
    static final int CLIP_TRIES = 4096;

    /** Each side's outward step, {dx, dy}: North up, East right, South down, West left (y runs south). */
    static final int[][] OUT = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };

    /** Each side's step across, left to right facing out. */
    static final int[][] ACROSS = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    /**
     * One standing offer's ground: its side and place, the level of its
     * blocks, and its rectangle of plots, [x0, x1) x [y0, y1), whole blocks.
     */
    public record Rect(int side, int place, int level, long x0, long y0, long x1, long y1) {
        /** Its blocks across its side. */
        public long across() { return (OUT[side][0] == 0 ? x1 - x0 : y1 - y0) >> level; }

        /** Its blocks deep, outward. */
        public long deep() { return (OUT[side][0] == 0 ? y1 - y0 : x1 - x0) >> level; }

        /** Whether it shares a plot with [ax0, ax1) x [ay0, ay1). */
        public boolean meets(long ax0, long ay0, long ax1, long ay1) {
            return x0 < ax1 && ax0 < x1 && y0 < ay1 && ay0 < y1;
        }

        /** Whether it shares a plot with another. */
        public boolean meets(Rect o) { return meets(o.x0, o.y0, o.x1, o.y1); }
    }

    /** A frontier block: its place in the grid, its distance out and its lane coordinate seen from the site, and whether a standing offer meets it. */
    record Front(long bx, long by, double r, double t, boolean taken) { }

    private final LandGrid grid;
    private final long sx, sy;
    private final Rect[] standing = new Rect[SIDES * PLACES];
    private int clipFailures;

    /** The offers round ground held on a grid, seen from the founding site (sx, sy); none standing yet. */
    public GridOffers(LandGrid grid, long sx, long sy) {
        this.grid = grid;
        this.sx = sx;
        this.sy = sy;
    }

    /** The grid the offers stand round. */
    public LandGrid grid() { return grid; }

    /** The offer standing in a place, or null while it waits. */
    public Rect offer(int side, int place) { return standing[side * PLACES + place]; }

    /** Stands an offer in a place, or empties it with null - an offer read back from a save. */
    public void stand(int side, int place, Rect r) { standing[side * PLACES + place] = r; }

    /** Listings whose clip did not settle and so were left empty; none ever has (spec-grid 2.2, 4,533 purchases). */
    public int clipFailures() { return clipFailures; }

    /* =====================================================================
       SIDE AND PLACE (spec-grid star 3)
       ===================================================================== */

    /** The side a point (dx, dy) from the site lies on: LegacyLand.sideLane()'s rule - the larger of |dx| and |dy|, ties to North or South. */
    public static int sideOf(double dx, double dy) {
        double ax = Math.abs(dx), ay = Math.abs(dy);
        if (ay >= ax) return dy < 0 ? 0 : 2;
        return dx > 0 ? 1 : 3;
    }

    /** Where place j's lane begins in the lane coordinate, -1 to 1 (j = PLACES gives 1). */
    static double laneT0(int j) { return -1 + j * 2.0 / PLACES; }

    /** The middle of place j's lane. */
    static double rayT(int j) { return (laneT0(j) + laneT0(j + 1)) / 2; }

    /** Whether lane coordinate t lies in place j's lane; the last lane holds t = 1. */
    static boolean inLane(double t, int j) {
        return t >= laneT0(j) && (t < laneT0(j + 1) || (j == PLACES - 1 && t <= 1));
    }

    /** The side and place a point (dx, dy) from the site's plot centre lies in, as side x PLACES + place: its side by sideOf(), its place by its lane coordinate, as the frontier reads a block's (0.7.67, what the map's pick names; the site itself, North's middle). */
    public static int sidePlace(double dx, double dy) {
        int s = sideOf(dx, dy);
        double r = dx * OUT[s][0] + dy * OUT[s][1], across = dx * ACROSS[s][0] + dy * ACROSS[s][1];
        double t = r > 0 ? across / r : 0;
        int j = 0;
        while (j < PLACES - 1 && !inLane(t, j)) j++;
        return s * PLACES + j;
    }

    /** {dx, dy} of a block's centre from the site's plot centre, in plots. */
    double[] centreOf(int level, long bx, long by) {
        double b = 1L << level;
        return new double[] { (bx + 0.5) * b - (sx + 0.5), (by + 0.5) * b - (sy + 0.5) };
    }

    /* =====================================================================
       THE FRONTIER
       ===================================================================== */

    /** Side s's frontier at `level`: the blocks of its quarter, on the world, that are not wholly owned and touch owned ground; row by row from the north-west. */
    List<Front> frontier(int s, int level) {
        List<Front> out = new ArrayList<>();
        if (grid.ownedPlots() == 0) return out;
        long b = 1L << level;
        long cx0 = Math.floorDiv(grid.minX(), b) - 1, cy0 = Math.floorDiv(grid.minY(), b) - 1;
        long cx1 = Math.floorDiv(grid.maxX() - 1, b) + 1, cy1 = Math.floorDiv(grid.maxY() - 1, b) + 1;
        long last = World.SIDE / b - 1;
        int[] o = OUT[s], a = ACROSS[s];
        for (long by = Math.max(0, cy0); by <= Math.min(last, cy1); by++) {
            for (long bx = Math.max(0, cx0); bx <= Math.min(last, cx1); bx++) {
                double[] c = centreOf(level, bx, by);
                if (sideOf(c[0], c[1]) != s) continue;
                int cov = grid.cover(level, bx, by);
                if (cov == LandGrid.ALL) continue;
                if (cov == LandGrid.NONE && !touches(level, bx, by)) continue;
                double r = c[0] * o[0] + c[1] * o[1];
                double across = c[0] * a[0] + c[1] * a[1];
                out.add(new Front(bx, by, r, across / r, takenRect(bx * b, by * b, bx * b + b, by * b + b)));
            }
        }
        return out;
    }

    /** Whether an unowned block has an owned plot across one of its four edges. */
    boolean touches(int level, long bx, long by) {
        long b = 1L << level, x0 = bx * b, y0 = by * b;
        return grid.unowned(x0, y0 - 1, x0 + b, y0) < b || grid.unowned(x0, y0 + b, x0 + b, y0 + b + 1) < b
                || grid.unowned(x0 - 1, y0, x0, y0 + b) < b || grid.unowned(x0 + b, y0, x0 + b + 1, y0 + b) < b;
    }

    /** Whether a standing offer meets the rectangle of plots [x0, x1) x [y0, y1). */
    boolean takenRect(long x0, long y0, long x1, long y1) {
        for (Rect o : standing) if (o != null && o.meets(x0, y0, x1, y1)) return true;
        return false;
    }

    /**
     * The seed of place (s, j) among a side's frontier at `level`: the free
     * block in its lane nearest the site, ties to the lane's middle; else
     * the side's free block nearest the lane's middle, ties to the nearer;
     * null when the side has none free. The keys are lexicographic - a block
     * is a few blocks out and off the ray, under a thousand - and the first
     * met in frontier order wins a tie.
     */
    Front seedOf(List<Front> f, int s, int j, int level) {
        long b = 1L << level;
        double tj = rayT(j);
        Front seed = null;
        double best = Double.MAX_VALUE;
        for (Front x : f) {
            if (x.taken()) continue;
            boolean in = inLane(x.t(), j);
            double lateral = Math.abs(x.t() - tj) * x.r() / b;      // blocks off the ray at the block's own distance
            double key = in ? Math.floor(x.r() / b) * 1e3 + lateral
                            : 1e12 + Math.abs(x.t() - tj) * 1e6 + x.r() / b;
            if (key < best) { best = key; seed = x; }
        }
        return seed;
    }

    /* =====================================================================
       LISTING A PLACE (spec-grid star 4)
       ===================================================================== */

    /** The offer place (s, j) would list now, against the offers standing; null when the side has no room for it. Nothing is stood. */
    public Rect list(int s, int j) {
        int level = grid.level();
        List<Front> f = frontier(s, level);
        Front seed = seedOf(f, s, j, level);
        int floor = Math.max(LandGrid.MIN_LEVEL, level - 1);
        while (seed == null && level > floor) {
            level--;
            f = frontier(s, level);
            seed = seedOf(f, s, j, level);
        }
        if (seed == null) return null;
        long b = 1L << level;
        double tj = rayT(j);
        int w = Math.max(1, Math.min(Math.max(1, f.size() / PLACES), (int) Math.round(Math.sqrt(grid.ownedPlots()) / PLACES / b)));
        int d = DEPTH_OVER_WIDTH * w;
        int[] o = OUT[s], a = ACROSS[s];
        long as = seed.bx() * a[0] + seed.by() * a[1], os = seed.bx() * o[0] + seed.by() * o[1];
        long left = (w - 1) / 2, right = w - 1 - left;
        if (seed.t() > tj && right > left) { long tt = left; left = right; right = tt; }
        long la0 = as - left, la1 = as + right;      // the columns across, inclusive, in the side's own block coordinates
        long lo0 = os, lo1 = os + d - 1;            // the rows outward, inclusive
        // the world's edge: no column or row past it (the seed is on the world)
        long[] wl = toLocal(s, level, 0, 0, World.SIDE, World.SIDE);
        la0 = Math.max(la0, wl[0]);
        la1 = Math.min(la1, wl[1]);
        lo1 = Math.min(lo1, wl[3]);
        // the clip against the standing offers
        boolean settled = false;
        for (int tries = 0; tries < CLIP_TRIES; tries++) {
            long[] r = toWorld(s, level, la0, la1, lo0, lo1);
            Rect hit = null;
            for (Rect x : standing) if (x != null && x.meets(r[0], r[1], r[2], r[3])) { hit = x; break; }
            if (hit == null) { settled = true; break; }
            long[] hl = toLocal(s, level, hit.x0(), hit.y0(), hit.x1(), hit.y1());
            if (hl[1] < as) la0 = Math.max(la0, hl[1] + 1);
            else if (hl[0] > as) la1 = Math.min(la1, hl[0] - 1);
            else if (hl[2] > lo0) lo1 = Math.min(lo1, hl[2] - 1);
            else break;                              // it meets the seed itself, which a free seed cannot
        }
        if (!settled) { clipFailures++; return null; }
        // trim columns and rows wholly owned, from the outside in, never the seed's
        while (la0 < as && lineOwned(s, level, la0, la0, lo0, lo1)) la0++;
        while (la1 > as && lineOwned(s, level, la1, la1, lo0, lo1)) la1--;
        while (lo1 > lo0 && lineOwned(s, level, la0, la1, lo1, lo1)) lo1--;
        // held to 2:1 either way
        long cols = la1 - la0 + 1;
        if (lo1 - lo0 + 1 > 2 * cols) lo1 = lo0 + 2 * cols - 1;
        long rows = lo1 - lo0 + 1;
        while (la1 - la0 + 1 > 2 * rows) { if (as - la0 > la1 - as) la0++; else la1--; }
        long[] r = toWorld(s, level, la0, la1, lo0, lo1);
        return new Rect(s, j, level, r[0], r[1], r[2], r[3]);
    }

    /** Whether every block of a rectangle in side s's block coordinates is wholly owned. */
    boolean lineOwned(int s, int level, long la0, long la1, long lo0, long lo1) {
        long[] r = toWorld(s, level, la0, la1, lo0, lo1);
        return grid.unowned(r[0], r[1], r[2], r[3]) == 0;
    }

    /** A rectangle in side s's block coordinates (columns la0 to la1, rows lo0 to lo1, inclusive) as plots {x0, y0, x1, y1}, half-open. */
    static long[] toWorld(int s, int level, long la0, long la1, long lo0, long lo1) {
        int[] o = OUT[s], a = ACROSS[s];
        long minX = Long.MAX_VALUE, minY = Long.MAX_VALUE, maxX = Long.MIN_VALUE, maxY = Long.MIN_VALUE;
        for (long la : new long[] { la0, la1 }) for (long lo : new long[] { lo0, lo1 }) {
            long bx = la * a[0] + lo * o[0], by = la * a[1] + lo * o[1];
            minX = Math.min(minX, bx); maxX = Math.max(maxX, bx); minY = Math.min(minY, by); maxY = Math.max(maxY, by);
        }
        long b = 1L << level;
        return new long[] { minX * b, minY * b, (maxX + 1) * b, (maxY + 1) * b };
    }

    /** A rectangle of plots as the blocks of `level` it touches, in side s's block coordinates: {first column, last column, first row, last row}, inclusive. */
    static long[] toLocal(int s, int level, long x0, long y0, long x1, long y1) {
        int[] o = OUT[s], a = ACROSS[s];
        long b = 1L << level;
        long bx0 = Math.floorDiv(x0, b), by0 = Math.floorDiv(y0, b), bx1 = Math.floorDiv(x1 - 1, b), by1 = Math.floorDiv(y1 - 1, b);
        long minA = Long.MAX_VALUE, maxA = Long.MIN_VALUE, minO = Long.MAX_VALUE, maxO = Long.MIN_VALUE;
        for (long bx : new long[] { bx0, bx1 }) for (long by : new long[] { by0, by1 }) {
            long la = bx * a[0] + by * a[1], lo = bx * o[0] + by * o[1];
            minA = Math.min(minA, la); maxA = Math.max(maxA, la); minO = Math.min(minO, lo); maxO = Math.max(maxO, lo);
        }
        return new long[] { minA, maxA, minO, maxO };
    }

    /* =====================================================================
       STANDING AND BUYING
       ===================================================================== */

    /** Lists every place that waits empty, North's first to West's last. */
    public void listMissing() {
        for (int i = 0; i < standing.length; i++) if (standing[i] == null) standing[i] = list(i / PLACES, i % PLACES);
    }

    /** Lists place (s, j)'s next offer, then every place still waiting: what follows a purchase in it. */
    public void relist(int s, int j) {
        standing[s * PLACES + j] = list(s, j);
        listMissing();
    }

    /**
     * Buys the offer standing in place (s, j) for holding h: its rectangle's
     * unowned plots are claimed on the grid, and the place lists its next.
     * Returns the offer bought, or null when the place waits empty.
     */
    public Rect buy(int s, int j, int h) {
        Rect x = standing[s * PLACES + j];
        if (x == null) return null;
        standing[s * PLACES + j] = null;
        grid.fill(x.x0(), x.y0(), x.x1(), x.y1(), h);
        relist(s, j);
        return x;
    }
}
