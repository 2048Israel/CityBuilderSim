package ham.citybuildersim;

import java.util.Arrays;
import java.util.List;

/**
 * The ground a city owns, as a region quadtree over the world's plots: which holding owns each plot, and how much of any block or rectangle of plots is owned.
 *
 * WHY THIS EXISTS (0.7.65, batch M1; the project's spec-grid.md 2.1 and 2.3).
 * The land is moving from spec-land's forty lanes of wedges to a grid of
 * square blocks lined up with the world: a level-k block is 2^k plots a side,
 * block (bx, by) covering plots [bx 2^k, (bx + 1) 2^k) each way, so blocks
 * nest in one another and in the world's tiles (level 5), districts (8) and
 * cells (11). Every piece of ground the city owns - the centre's blocks, each
 * purchase's rectangle - is a union of such squares, and a quadtree holds a
 * union of aligned squares in a few thousand nodes at any size: 3,373 to
 * 3,649 nodes for a city of ten billion people bought evenly, where a raster
 * of its plots would be billions (spec-grid 2.3, measured on the prototype
 * this is ported from). Testing a plot took the lanes 47 to 56 ns; the tree
 * answers in 18 to 49 (GridCheck 7 holds it under OWNER_NS).
 *
 * THE TREE (spec-grid star 7). One root of 2^TOP plots a side, its nodes in
 * int arrays (four children each, a state, a holding). A node is EMPTY (none
 * of it owned), FULL (all of it, by one holding), MIXED (four children), or
 * OWNED (all of it, by more than one holding: its children are kept, so
 * owner() still answers, but cover() says ALL without descending). Four FULL
 * children of one holding merge back into their parent.
 *
 * FILLING CLAIMS ONLY WHAT NO HOLDING OWNS YET. fill() hands a rectangle's
 * unowned plots to a holding and leaves the owned ones as they are, so a
 * coarse offer may cover finer ground the city already owns and buys only
 * the rest. It follows that the tree is the same whatever its history, given
 * the fills in the same order: it is never saved, and replay() rebuilds it at
 * load from the centre's rectangles and then the purchases' (GridCheck 2,
 * node for node).
 *
 * THE MODEL'S SINCE 0.7.67. Batch M1 built the grid pure, with GridOffers
 * beside it and GridCheck holding both; since batch M3 CityLand holds the
 * city's ground on it (spec-grid 3).
 */
public final class LandGrid {

    /** The root's level: 2^20 plots (1,048,576) a side, the least power of two the world's World.SIDE (753,664 plots) fits in. */
    public static final int TOP = 20;

    /** The finest block an offer or a new city's centre is drawn in: level 2, four plots (120 m) a side - 0.0144 km2, the smallest block holding one of spec-land's 100,000 sq ft blocks, Jerus's "one block smallest" (spec-grid star 2). */
    public static final int MIN_LEVEL = 2;

    /** The coarsest: level 15, 2^15 plots (983 km) a side - the largest block the world, 23 x 2^15 plots a side, divides into whole (spec-grid star 1). */
    public static final int MAX_LEVEL = 15;

    /** Blocks of the city's level that fit across a square of its area: six, so each of a side's six places is one or two blocks wide (spec-grid star 2). */
    public static final int FACE_BLOCKS = 6;

    /** A node's state: none of its plots owned... */
    public static final byte EMPTY = 0;

    /** ...all of them, by one holding... */
    public static final byte FULL = 1;

    /** ...some of them: it has four children... */
    public static final byte MIXED = 2;

    /** ...or all of them, by more than one holding: its children kept, so a plot's owner is still found, and cover() says ALL without descending. */
    public static final byte OWNED = 3;

    /** What cover() says of a block: none of its plots owned... */
    public static final int NONE = 0;

    /** ...some of them... */
    public static final int SOME = 1;

    /** ...or all of them. */
    public static final int ALL = 2;

    /** The plots a side of the root: 2^TOP. */
    private static final long ROOT = 1L << TOP;

    /* =====================================================================
       THE STATE
       ===================================================================== */

    /** Each node's four children, the first at 4 x node: the north-west, north-east, south-west and south-east quarters. */
    private int[] kids = new int[4 * 1024];

    /** Each node's state: EMPTY, FULL, MIXED or OWNED. */
    private byte[] state = new byte[1024];

    /** Each FULL node's holding. */
    private int[] holding = new int[1024];

    /** Node slots handed out so far; node 0 is the root. */
    private int used = 1;

    /** Quads of four slots freed by a merge, to be handed out again. */
    private int[] free = new int[256];
    private int freeCount = 0;

    /** Plots owned, by every holding. */
    private long ownedPlots;

    /** The owned ground's bounding box, in plots, half-open; empty while nothing is owned. */
    private long minX = Long.MAX_VALUE, minY = Long.MAX_VALUE, maxX = Long.MIN_VALUE, maxY = Long.MIN_VALUE;

    /** A rectangle of plots, half-open, filled for a holding: what replay() rebuilds the tree from. */
    public record Fill(long x0, long y0, long x1, long y1, int holding) { }

    /** An empty grid: nothing owned. */
    public LandGrid() {
        state[0] = EMPTY;
    }

    /** The grid the fills make, in their order: the centre's rectangles, then each purchase's (spec-grid star 7). */
    public static LandGrid replay(List<Fill> fills) {
        LandGrid g = new LandGrid();
        for (Fill f : fills) g.fill(f.x0(), f.y0(), f.x1(), f.y1(), f.holding());
        return g;
    }

    private int alloc4() {
        if (freeCount > 0) return free[--freeCount];
        int n = used;
        used += 4;
        if (used > state.length) {
            int cap = Math.max(used, state.length * 2);
            state = Arrays.copyOf(state, cap);
            holding = Arrays.copyOf(holding, cap);
            kids = Arrays.copyOf(kids, 4 * cap);
        }
        return n;
    }

    private void release4(int first) {
        if (freeCount == free.length) free = Arrays.copyOf(free, free.length * 2);
        free[freeCount++] = first;
    }

    /* =====================================================================
       OWNING
       ===================================================================== */

    /**
     * Claims every plot of [x0, x1) x [y0, y1) that no holding owns yet, and
     * only those, for holding h (0 or more); plots off the world are left out.
     * Returns the plots claimed.
     */
    public long fill(long x0, long y0, long x1, long y1, int h) {
        x0 = Math.max(0, x0);
        y0 = Math.max(0, y0);
        x1 = Math.min(World.SIDE, x1);
        y1 = Math.min(World.SIDE, y1);
        if (x0 >= x1 || y0 >= y1) return 0;
        long got = fillRec(0, TOP, 0, 0, x0, y0, x1, y1, h);
        ownedPlots += got;
        if (got > 0) {
            minX = Math.min(minX, x0);
            minY = Math.min(minY, y0);
            maxX = Math.max(maxX, x1);
            maxY = Math.max(maxY, y1);
        }
        return got;
    }

    private long fillRec(int node, int level, long bx, long by, long x0, long y0, long x1, long y1, int h) {
        long size = 1L << level;
        if (bx >= x1 || by >= y1 || bx + size <= x0 || by + size <= y0) return 0;
        byte s = state[node];
        if (s == FULL || s == OWNED) return 0;
        boolean inside = bx >= x0 && by >= y0 && bx + size <= x1 && by + size <= y1;
        if (inside && s == EMPTY) {
            state[node] = FULL;
            holding[node] = h;
            return size * size;
        }
        if (s == EMPTY) {
            int k = alloc4();
            for (int q = 0; q < 4; q++) state[k + q] = EMPTY;
            kids[4 * node] = k;
            state[node] = MIXED;
        }
        int k = kids[4 * node];
        long half = size >> 1, got = 0;
        for (int q = 0; q < 4; q++) {
            got += fillRec(k + q, level - 1, bx + (q & 1) * half, by + (q >> 1) * half, x0, y0, x1, y1, h);
        }
        if (state[k] == FULL && state[k + 1] == FULL && state[k + 2] == FULL && state[k + 3] == FULL
                && holding[k] == holding[k + 1] && holding[k] == holding[k + 2] && holding[k] == holding[k + 3]) {
            state[node] = FULL;              // four equal leaves merge
            holding[node] = holding[k];
            release4(k);
        } else if (whole(state[k]) && whole(state[k + 1]) && whole(state[k + 2]) && whole(state[k + 3])) {
            state[node] = OWNED;
        }
        return got;
    }

    private static boolean whole(byte s) { return s == FULL || s == OWNED; }

    /* =====================================================================
       WHAT IS OWNED
       ===================================================================== */

    /** The holding owning plot (x, y), or -1 when none does. */
    public int owner(long x, long y) {
        if ((x | y) < 0 || x >= ROOT || y >= ROOT) return -1;
        int node = 0, level = TOP;
        while (true) {
            byte s = state[node];
            if (s == FULL) return holding[node];
            if (s == EMPTY) return -1;
            level--;
            int q = (int) (((x >> level) & 1) | (((y >> level) & 1) << 1));
            node = kids[4 * node] + q;
        }
    }

    /** NONE, SOME or ALL of the aligned block (bx, by) of `level` (2^level plots a side) is owned. */
    public int cover(int level, long bx, long by) {
        long x = bx << level, y = by << level;
        if ((x | y) < 0 || x >= ROOT || y >= ROOT) return NONE;
        int node = 0, l = TOP;
        while (true) {
            byte s = state[node];
            if (s == FULL || s == OWNED) return ALL;
            if (s == EMPTY) return NONE;
            if (l == level) return SOME;
            l--;
            int q = (int) (((x >> l) & 1) | (((y >> l) & 1) << 1));
            node = kids[4 * node] + q;
        }
    }

    /** The plots of [x0, x1) x [y0, y1) no holding owns. */
    public long unowned(long x0, long y0, long x1, long y1) {
        return (x1 - x0) * (y1 - y0) - owned(x0, y0, x1, y1);
    }

    /** The plots of [x0, x1) x [y0, y1) some holding owns. */
    public long owned(long x0, long y0, long x1, long y1) {
        return ownedRec(0, TOP, 0, 0, x0, y0, x1, y1);
    }

    private long ownedRec(int node, int level, long bx, long by, long x0, long y0, long x1, long y1) {
        long size = 1L << level;
        long ix0 = Math.max(bx, x0), iy0 = Math.max(by, y0), ix1 = Math.min(bx + size, x1), iy1 = Math.min(by + size, y1);
        if (ix0 >= ix1 || iy0 >= iy1) return 0;
        byte s = state[node];
        if (s == EMPTY) return 0;
        if (s == FULL || s == OWNED) return (ix1 - ix0) * (iy1 - iy0);
        int k = kids[4 * node];
        long half = size >> 1, got = 0;
        for (int q = 0; q < 4; q++) got += ownedRec(k + q, level - 1, bx + (q & 1) * half, by + (q >> 1) * half, x0, y0, x1, y1);
        return got;
    }

    /** Fills a tile's (a level-5 block's) World.TILE x World.TILE ownership flags, row by row, from the leaves under it: a FULL leaf sets its square at once. */
    public void tileFlags(long tx, long ty, boolean[] out) {
        Arrays.fill(out, 0, World.TILE * World.TILE, false);
        flagsRec(0, TOP, 0, 0, tx * World.TILE, ty * World.TILE, out);
    }

    private void flagsRec(int node, int level, long bx, long by, long x0, long y0, boolean[] out) {
        long size = 1L << level;
        int t = World.TILE;
        if (bx >= x0 + t || by >= y0 + t || bx + size <= x0 || by + size <= y0) return;
        byte s = state[node];
        if (s == EMPTY) return;
        if (s == FULL) {
            int i0 = (int) Math.max(0, bx - x0), j0 = (int) Math.max(0, by - y0);
            int i1 = (int) Math.min(t, bx + size - x0), j1 = (int) Math.min(t, by + size - y0);
            for (int j = j0; j < j1; j++) Arrays.fill(out, j * t + i0, j * t + i1, true);
            return;
        }
        int k = kids[4 * node];
        long half = size >> 1;
        for (int q = 0; q < 4; q++) flagsRec(k + q, level - 1, bx + (q & 1) * half, by + (q >> 1) * half, x0, y0, out);
    }

    /** Called back for every FULL leaf: its level, its first plot and its holding. */
    public interface Leaf { void at(int level, long x, long y, int holding); }

    /** Calls f back for every FULL leaf, north-west quarter first, down the tree. */
    public void leaves(Leaf f) { leavesRec(0, TOP, 0, 0, f); }

    private void leavesRec(int node, int level, long bx, long by, Leaf f) {
        if (state[node] == FULL) { f.at(level, bx, by, holding[node]); return; }
        if (state[node] == EMPTY) return;
        int k = kids[4 * node];
        long half = 1L << (level - 1);
        for (int q = 0; q < 4; q++) leavesRec(k + q, level - 1, bx + (q & 1) * half, by + (q >> 1) * half, f);
    }

    /** Plots owned, by every holding. */
    public long ownedPlots() { return ownedPlots; }

    /** The owned ground's bounding box, in plots, half-open: its west edge... */
    public long minX() { return minX; }

    /** ...its north edge... */
    public long minY() { return minY; }

    /** ...one past its east edge... */
    public long maxX() { return maxX; }

    /** ...and one past its south edge. */
    public long maxY() { return maxY; }

    /* =====================================================================
       THE CITY'S LEVEL (spec-grid star 2)
       ===================================================================== */

    /**
     * The block level of a city owning `plots` plots of every kind: the
     * largest k at which FACE_BLOCKS blocks fit across a square of that area
     * (2^k at most sqrt(plots) / FACE_BLOCKS), never under MIN_LEVEL or over
     * MAX_LEVEL.
     */
    public static int levelFor(double plots) {
        double s = Math.sqrt(plots) / FACE_BLOCKS;
        int k = 0;
        while ((1L << (k + 1)) <= s && k < MAX_LEVEL) k++;
        return Math.max(MIN_LEVEL, k);
    }

    /** This grid's level: levelFor() of the plots it owns. */
    public int level() { return levelFor(ownedPlots); }

    /* =====================================================================
       THE TREE ITSELF: what GridCheck reads
       ===================================================================== */

    /**
     * A walk of the tree: {live nodes, FULL leaves, OWNED nodes, nodes whose
     * four children are FULL by one holding} - the last is what a merge
     * leaves none of.
     */
    public long[] census() {
        long[] c = new long[4];
        censusRec(0, c);
        return c;
    }

    private void censusRec(int node, long[] c) {
        c[0]++;
        byte s = state[node];
        if (s == FULL) { c[1]++; return; }
        if (s == EMPTY) return;
        if (s == OWNED) c[2]++;
        int k = kids[4 * node];
        if (state[k] == FULL && state[k + 1] == FULL && state[k + 2] == FULL && state[k + 3] == FULL
                && holding[k] == holding[k + 1] && holding[k] == holding[k + 2] && holding[k] == holding[k + 3]) c[3]++;
        for (int q = 0; q < 4; q++) censusRec(k + q, c);
    }

    /** Whether another grid is this one node for node: the same state everywhere, the same holding in every FULL leaf, the same plots owned. */
    public boolean sameAs(LandGrid o) {
        return ownedPlots == o.ownedPlots && sameRec(0, o, 0);
    }

    private boolean sameRec(int node, LandGrid o, int other) {
        byte s = state[node];
        if (s != o.state[other]) return false;
        if (s == FULL) return holding[node] == o.holding[other];
        if (s == EMPTY) return true;
        int k = kids[4 * node], ko = o.kids[4 * other];
        for (int q = 0; q < 4; q++) if (!sameRec(k + q, o, ko + q)) return false;
        return true;
    }
}
