package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The block grid (0.7.65, batch M1): the city's owned ground as a quadtree, every plot's owner exact against a raster, and its offers six a side - apart, against its edge and whole blocks at 2:1 after every one of 600 purchases, a notch offered before ground farther out, and a plot's owner found in time.
 *
 * WHY THIS EXISTS (the project's spec-grid.md 3, batch M1). LandGrid and
 * GridOffers are ported from the prototype the design was measured on, and
 * since batch M3 (0.7.67) every plot the city owns, every offer the land
 * office lists and every building the map draws stands on them. A tree that
 * misplaced one plot, or two offers that met, would surface there as books
 * that disagreed with the map, so they are held here first, on their own.
 *
 * What it has to prove:
 *   1. fill() and owner() are a brute-force raster's on FIXTURES fixtures -
 *      the plots each fill claims, every plot's owner, unowned() on any
 *      rectangle, cover() at every level, a tile's flags and the leaves -
 *      including fixtures at the world's edges and across the root's
 *      quarters; four equal leaves merge; cover() is exact for a MIXED block
 *      and for an OWNED one (wholly owned by more than one holding);
 *   2. replay() of the same fills rebuilds the tree node for node, and a
 *      replay with one holding changed does not;
 *   3. the city's level is the largest at which FACE_BLOCKS blocks fit across
 *      its area, from MIN_LEVEL to MAX_LEVEL; a block's side is
 *      LegacyLand.sideLane()'s; every lane coordinate lies in one place;
 *   4. a new default city, its centre in blocks of 120 m as the prototype
 *      drew it, lists its first offers one block across and
 *      DEPTH_OVER_WIDTH deep, four or five a side (spec-grid 1.5), and all
 *      24 stand by the 12th purchase bought evenly;
 *   5. over CITY_PURCHASES purchases bought evenly, after every one: no two
 *      standing offers meet, every one touches the city, each is whole
 *      blocks of its level at no more than 2:1, the other places stand
 *      unchanged, and a purchase claims exactly its rectangle's unowned
 *      plots; the city replayed from its rectangles is the same tree;
 *   6. a notch - free ground in a place's lane nearer the site than its
 *      edge - is seeded before the ground farther out, and with none the
 *      place lists straight out;
 *   7. owner() answers within OWNER_NS on the city of 5, a median of
 *      TIMING_RUNS runs.
 */
public class GridCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-66s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Fixtures of fills checked against a raster: 200 (spec-grid 3, M1). */
    static final int FIXTURES = 200;

    /** Plots across a fixture's window: 128, 2^7, so its fills merge up to level 7 and unaligned windows straddle every block line under it. */
    static final int WINDOW = 128;

    /** Purchases the city of section 5 is bought to, evenly: 600 (spec-grid 3, M1) - past the 344 that bring the default world's city to Jerus's old city's size. */
    static final int CITY_PURCHASES = 600;

    /** The purchase by which a new city's every place stands, bought evenly: the 12th, the latest spec-grid 2.2 measured on three worlds and two ways of buying (4 to 12). */
    static final int ALL_LISTED_BY = 12;

    /** The most owner() may take a plot, in nanoseconds: 100 (spec-grid 3, M1; measured 18 to 49 on two shared cores). */
    static final double OWNER_NS = 100;

    /** Runs owner()'s timing takes the median of: seven, as the spec's benchmarks did. */
    static final int TIMING_RUNS = 7;

    /** Random plots owner() is timed on in each run: 2,000,000, as the spec's benchmark. */
    static final int TIMED_PLOTS = 2_000_000;

    public static void main(String[] args) {
        rasterFixtures();
        levelSideAndPlace();
        World w = World.of(Founding.DEFAULT_WORLD_SEED);
        newCity(w);
        City city = boughtEvenly(w);
        notch(w);
        timing(city);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1 AND 2. THE TREE AGAINST A RASTER, AND REPLAYED
       ===================================================================== */

    /** A fixture's raster: the window's plots, each its holding or -1, filled first come first served, nothing off the world. */
    static final class Raster {
        final long ox, oy;
        final int[] own = new int[WINDOW * WINDOW];
        Raster(long ox, long oy) { this.ox = ox; this.oy = oy; Arrays.fill(own, -1); }

        static boolean onWorld(long x, long y) { return x >= 0 && y >= 0 && x < World.SIDE && y < World.SIDE; }

        boolean inWindow(long x, long y) { return x >= ox && y >= oy && x < ox + WINDOW && y < oy + WINDOW; }

        int at(long x, long y) { return inWindow(x, y) ? own[(int) ((y - oy) * WINDOW + (x - ox))] : -1; }

        long fill(long x0, long y0, long x1, long y1, int h) {
            long got = 0;
            for (long y = y0; y < y1; y++) for (long x = x0; x < x1; x++) {
                if (!onWorld(x, y) || !inWindow(x, y)) continue;
                int i = (int) ((y - oy) * WINDOW + (x - ox));
                if (own[i] < 0) { own[i] = h; got++; }
            }
            return got;
        }

        /** {owned plots, plots of more than one holding's} in [x0, x1) x [y0, y1); outside the window nothing is owned. */
        long[] count(long x0, long y0, long x1, long y1) {
            long n = 0;
            int first = -2;
            boolean several = false;
            for (long y = Math.max(y0, oy); y < Math.min(y1, oy + WINDOW); y++) for (long x = Math.max(x0, ox); x < Math.min(x1, ox + WINDOW); x++) {
                int h = at(x, y);
                if (h < 0) continue;
                n++;
                if (first == -2) first = h; else if (h != first) several = true;
            }
            return new long[] { n, several ? 1 : 0 };
        }
    }

    static void rasterFixtures() {
        System.out.println("--- 1. the tree against a raster: " + FIXTURES + " fixtures ---");
        long fills = 0, claimedOk = 0, plots = 0, plotsOk = 0, rects = 0, rectsOk = 0, blocks = 0, blocksOk = 0;
        long tiles = 0, tilesOk = 0, leafOk = 0, someSeen = 0, ownedSeen = 0, severalAll = 0, unmerged = 0, bboxOk = 0, totalsOk = 0;
        long replayed = 0, replayedSame = 0;
        boolean changedDiffers = false;
        int[] where = new int[5];
        for (int f = 0; f < FIXTURES; f++) {
            Random rnd = new Random(f);
            long ox, oy;
            switch (f % 5) {
                case 0:  ox = (long) rnd.nextInt((int) (World.SIDE / WINDOW)) * WINDOW; oy = (long) rnd.nextInt((int) (World.SIDE / WINDOW)) * WINDOW; break;
                case 1:  ox = WINDOW + rnd.nextInt((int) World.SIDE - 2 * WINDOW); oy = WINDOW + rnd.nextInt((int) World.SIDE - 2 * WINDOW); break;
                case 2:  ox = -WINDOW / 4; oy = rnd.nextBoolean() ? -WINDOW / 4 : rnd.nextInt((int) World.SIDE - WINDOW); break;   // the world's west and north edges
                case 3:  ox = World.SIDE - WINDOW / 2; oy = rnd.nextBoolean() ? World.SIDE - WINDOW / 2 : rnd.nextInt((int) World.SIDE - WINDOW); break;   // its east and south
                default: ox = (1L << (LandGrid.TOP - 1)) - WINDOW / 2 - rnd.nextInt(8); oy = (1L << (LandGrid.TOP - 1)) - WINDOW / 2 - rnd.nextInt(8); break;   // across the root's quarters
            }
            where[f % 5]++;
            LandGrid g = new LandGrid();
            Raster r = new Raster(ox, oy);
            List<LandGrid.Fill> log = new ArrayList<>();
            int n = 4 + rnd.nextInt(28);
            for (int k = 0; k < n; k++) {
                int h = rnd.nextInt(4);
                int kind = rnd.nextInt(4);
                List<long[]> rs = new ArrayList<>();
                if (kind == 0) {                       // any rectangle in the window
                    long x0 = ox + rnd.nextInt(WINDOW), y0 = oy + rnd.nextInt(WINDOW);
                    rs.add(new long[] { x0, y0, x0 + 1 + rnd.nextInt((int) (ox + WINDOW - x0)), y0 + 1 + rnd.nextInt((int) (oy + WINDOW - y0)) });
                } else {                               // an aligned block, whole (1), its four quarters one by one (2), or its two halves (3)
                    int lv = rnd.nextInt(7);
                    long b = 1L << lv;
                    long bx0 = Math.floorDiv(ox + b - 1, b), by0 = Math.floorDiv(oy + b - 1, b), across = (ox + WINDOW) / b - bx0;
                    if (across <= 0) continue;
                    long x0 = (bx0 + rnd.nextInt((int) across)) * b, y0 = (by0 + rnd.nextInt((int) Math.max(1, (oy + WINDOW) / b - by0))) * b;
                    if (kind == 1 || lv == 0) rs.add(new long[] { x0, y0, x0 + b, y0 + b });
                    else if (kind == 2) for (int q = 0; q < 4; q++) rs.add(new long[] { x0 + (q & 1) * b / 2, y0 + (q >> 1) * b / 2, x0 + (q & 1) * b / 2 + b / 2, y0 + (q >> 1) * b / 2 + b / 2 });
                    else { rs.add(new long[] { x0, y0, x0 + b / 2, y0 + b }); rs.add(new long[] { x0 + b / 2, y0, x0 + b, y0 + b }); }
                }
                for (int i = 0; i < rs.size(); i++) {
                    long[] q = rs.get(i);
                    int hh = kind == 3 && i == 1 ? (h + 1) % 4 : h;
                    long got = g.fill(q[0], q[1], q[2], q[3], hh);
                    long want = r.fill(q[0], q[1], q[2], q[3], hh);
                    log.add(new LandGrid.Fill(q[0], q[1], q[2], q[3], hh));
                    fills++;
                    if (got == want) claimedOk++;
                }
            }
            // every plot of the window, and a margin of two round it
            for (long y = oy - 2; y < oy + WINDOW + 2; y++) for (long x = ox - 2; x < ox + WINDOW + 2; x++) {
                plots++;
                if (g.owner(x, y) == r.at(x, y)) plotsOk++;
            }
            // unowned() on rectangles in and round the window
            for (int k = 0; k < 64; k++) {
                long x0 = ox - 8 + rnd.nextInt(WINDOW + 8), y0 = oy - 8 + rnd.nextInt(WINDOW + 8);
                long x1 = x0 + 1 + rnd.nextInt(WINDOW), y1 = y0 + 1 + rnd.nextInt(WINDOW);
                rects++;
                if (g.unowned(x0, y0, x1, y1) == (x1 - x0) * (y1 - y0) - r.count(x0, y0, x1, y1)[0]) rectsOk++;
            }
            // cover() at levels 0 to 8, every block meeting the window
            for (int lv = 0; lv <= 8; lv++) {
                long b = 1L << lv;
                for (long by = Math.floorDiv(oy, b); by <= Math.floorDiv(oy + WINDOW - 1, b); by++)
                    for (long bx = Math.floorDiv(ox, b); bx <= Math.floorDiv(ox + WINDOW - 1, b); bx++) {
                        long[] c = r.count(bx * b, by * b, bx * b + b, by * b + b);
                        int want = c[0] == 0 ? LandGrid.NONE : c[0] == b * b ? LandGrid.ALL : LandGrid.SOME;
                        int got = g.cover(lv, bx, by);
                        blocks++;
                        if (got == want) blocksOk++;
                        if (want == LandGrid.SOME) someSeen++;
                        if (want == LandGrid.ALL && c[1] == 1) severalAll++;
                    }
            }
            // a tile's flags, every tile meeting the window
            boolean[] flags = new boolean[World.TILE * World.TILE];
            for (long ty = Math.floorDiv(oy, World.TILE); ty <= Math.floorDiv(oy + WINDOW - 1, World.TILE); ty++)
                for (long tx = Math.floorDiv(ox, World.TILE); tx <= Math.floorDiv(ox + WINDOW - 1, World.TILE); tx++) {
                    g.tileFlags(tx, ty, flags);
                    boolean ok = true;
                    for (int i = 0; i < flags.length; i++) ok &= flags[i] == (r.at(tx * World.TILE + (i % World.TILE), ty * World.TILE + (i / World.TILE)) >= 0);
                    tiles++;
                    if (ok) tilesOk++;
                }
            // the leaves: each the raster's holding on every plot, together every owned plot
            long[] area = { 0 };
            boolean[] leavesRight = { true };
            g.leaves((lv, x, y, h) -> {
                long b = 1L << lv;
                area[0] += b * b;
                for (long yy = y; yy < y + b; yy++) for (long xx = x; xx < x + b; xx++) leavesRight[0] &= r.at(xx, yy) == h;
            });
            long rasterOwned = r.count(ox, oy, ox + WINDOW, oy + WINDOW)[0];
            if (leavesRight[0] && area[0] == rasterOwned) leafOk++;
            if (g.ownedPlots() == rasterOwned) totalsOk++;
            long mx = Long.MAX_VALUE, my = Long.MAX_VALUE, Mx = Long.MIN_VALUE, My = Long.MIN_VALUE;
            for (long y = oy; y < oy + WINDOW; y++) for (long x = ox; x < ox + WINDOW; x++) if (r.at(x, y) >= 0) {
                mx = Math.min(mx, x); my = Math.min(my, y); Mx = Math.max(Mx, x + 1); My = Math.max(My, y + 1);
            }
            if (g.minX() == mx && g.minY() == my && g.maxX() == Mx && g.maxY() == My) bboxOk++;
            long[] census = g.census();
            ownedSeen += census[2];
            unmerged += census[3];
            // 2: replayed
            replayed++;
            if (LandGrid.replay(log).sameAs(g)) replayedSame++;
            if (!changedDiffers) {
                for (int i = 0; i < log.size(); i++) {
                    LandGrid probe = new LandGrid();
                    long got = 0;
                    for (int k = 0; k <= i; k++) got = probe.fill(log.get(k).x0(), log.get(k).y0(), log.get(k).x1(), log.get(k).y1(), log.get(k).holding());
                    if (got == 0) continue;
                    List<LandGrid.Fill> changed = new ArrayList<>(log);
                    LandGrid.Fill c = log.get(i);
                    changed.set(i, new LandGrid.Fill(c.x0(), c.y0(), c.x1(), c.y1(), c.holding() + 1));
                    changedDiffers = !LandGrid.replay(changed).sameAs(g);
                    break;
                }
            }
        }
        System.out.printf("fixtures: %d in the open, %d unaligned, %d at the west or north edge, %d at the east or south, %d across the root's quarters; %d fills%n",
                where[0], where[1], where[2], where[3], where[4], fills);
        check("every fill claims exactly the plots no holding owned (" + fills + ")", claimedOk == fills);
        check("owner() of every plot is the raster's, and -1 round it (" + plots + ")", plotsOk == plots);
        check("unowned() of any rectangle is the raster's (" + rects + ")", rectsOk == rects);
        check("cover() of every block, levels 0 to 8, is NONE, SOME or ALL exactly", blocksOk == blocks);
        check("...the fixtures cause MIXED blocks (" + someSeen + " SOME)", someSeen > 0);
        check("...and blocks wholly owned by more than one holding (" + severalAll + ")", severalAll > 0);
        check("...which the tree holds as OWNED nodes (" + ownedSeen + ")", ownedSeen > 0);
        check("a tile's flags are the raster's (" + tiles + " tiles)", tilesOk == tiles);
        check("the leaves are the raster's holdings, and every owned plot", leafOk == FIXTURES);
        check("the plots owned and their bounding box are the raster's", totalsOk == FIXTURES && bboxOk == FIXTURES);
        check("no node is left with four FULL children of one holding", unmerged == 0);

        // four equal leaves merge, and an OWNED block
        LandGrid m = new LandGrid();
        long b6 = 1L << 6, x6 = 5_000 * b6, y6 = 5_000 * b6, h6 = b6 / 2;
        for (int q = 0; q < 4; q++) m.fill(x6 + (q & 1) * h6, y6 + (q >> 1) * h6, x6 + (q & 1) * h6 + h6, y6 + (q >> 1) * h6 + h6, 3);
        List<long[]> leaves = new ArrayList<>();
        m.leaves((lv, x, y, h) -> leaves.add(new long[] { lv, x, y, h }));
        check("four quarters filled one by one for one holding merge into one leaf",
                leaves.size() == 1 && leaves.get(0)[0] == 6 && leaves.get(0)[1] == x6 && leaves.get(0)[2] == y6 && leaves.get(0)[3] == 3);
        LandGrid o = new LandGrid();
        for (int q = 0; q < 4; q++) o.fill(x6 + (q & 1) * h6, y6 + (q >> 1) * h6, x6 + (q & 1) * h6 + h6, y6 + (q >> 1) * h6 + h6, q == 3 ? 4 : 3);
        long[] oc = o.census();
        check("...for two holdings they stay four, under an OWNED node", oc[1] == 4 && oc[2] == 1);
        check("...which cover() says is ALL, its parent SOME", o.cover(6, x6 / b6, y6 / b6) == LandGrid.ALL && o.cover(7, x6 / b6 / 2, y6 / b6 / 2) == LandGrid.SOME);
        check("...and owner() still tells its holdings apart", o.owner(x6, y6) == 3 && o.owner(x6 + b6 - 1, y6 + b6 - 1) == 4);
        LandGrid s = new LandGrid();
        s.fill(x6, y6, x6 + b6, y6 + b6, 2);
        LandGrid hole = new LandGrid();
        hole.fill(x6, y6, x6 + b6 - 1, y6 + b6, 2);
        hole.fill(x6 + b6 - 1, y6, x6 + b6, y6 + b6 - 1, 2);
        boolean some = true;
        for (int lv = 1; lv <= LandGrid.TOP; lv++) some &= hole.cover(lv, (x6 + b6 - 1) >> lv, (y6 + b6 - 1) >> lv) == LandGrid.SOME;
        check("a block missing one plot is SOME at every level over the plot", some && hole.cover(0, x6 + b6 - 1, y6 + b6 - 1) == LandGrid.NONE);
        check("...and the same block whole is one FULL leaf, ALL", s.census()[1] == 1 && s.cover(6, x6 / b6, y6 / b6) == LandGrid.ALL);

        System.out.println("--- 2. replayed, the tree node for node ---");
        check("each fixture's fills replayed make the same tree, node for node (" + replayedSame + " of " + replayed + ")", replayedSame == replayed);
        check("...and with one holding changed, a different one", changedDiffers);
    }

    /* =====================================================================
       3. THE LEVEL, THE SIDE AND THE PLACE
       ===================================================================== */

    static void levelSideAndPlace() {
        System.out.println("--- 3. the city's level, a block's side and its place ---");
        boolean exact = true, under = true;
        for (int k = LandGrid.MIN_LEVEL; k <= LandGrid.MAX_LEVEL; k++) {
            double edge = (double) LandGrid.FACE_BLOCKS * (1L << k);
            exact &= LandGrid.levelFor(edge * edge) == k;
            if (k > LandGrid.MIN_LEVEL) under &= LandGrid.levelFor(edge * edge - 1) == k - 1;
        }
        check("FACE_BLOCKS blocks of 2^k plots across a square of its area: level k", exact);
        check("...a plot fewer: level k - 1", under);
        check("never under MIN_LEVEL (one plot, a new city's 310)", LandGrid.levelFor(1) == LandGrid.MIN_LEVEL
                && LandGrid.levelFor(LandManager.km2(LandManager.STARTING_SQ_FT) / World.KM2_PER_PLOT) == LandGrid.MIN_LEVEL);
        check("never over MAX_LEVEL (the whole world)", LandGrid.levelFor((double) World.SIDE * World.SIDE) == LandGrid.MAX_LEVEL);
        Random rnd = new Random(1);
        boolean sides = true;
        for (int i = 0; i < 100_000; i++) {
            double dx = rnd.nextInt(2001) - 1000 + (rnd.nextBoolean() ? 0.5 : 0), dy = rnd.nextInt(2001) - 1000 + (rnd.nextBoolean() ? 0.5 : 0);
            if (dx == 0 && dy == 0) continue;
            sides &= GridOffers.sideOf(dx, dy) == LegacyLand.sideLane(dx, dy) / LegacyLand.LANES;
        }
        check("a point's side is the lanes' (LegacyLand.sideLane()), diagonals to North or South", sides
                && GridOffers.sideOf(-3, -3) == 0 && GridOffers.sideOf(3, -3) == 0 && GridOffers.sideOf(3, 3) == 2 && GridOffers.sideOf(-3, 3) == 2);
        boolean one = true;
        for (int i = 0; i <= 12_000; i++) {
            double t = -1 + i / 6_000.0;
            int in = 0;
            for (int j = 0; j < GridOffers.PLACES; j++) if (GridOffers.inLane(t, j)) in++;
            one &= in == 1;
        }
        for (int j = 0; j <= GridOffers.PLACES; j++) {
            int in = 0;
            for (int p = 0; p < GridOffers.PLACES; p++) if (GridOffers.inLane(GridOffers.laneT0(j), p)) in++;
            one &= in == 1;
        }
        check("every lane coordinate from -1 to 1 lies in exactly one of the six places", one);
    }

    /* =====================================================================
       4. A NEW CITY'S FIRST OFFERS
       ===================================================================== */

    /** A city on the grid as the harness builds it: its grid, its offers, and every fill in order. */
    static final class City {
        final LandGrid grid = new LandGrid();
        final GridOffers offers;
        final List<LandGrid.Fill> fills = new ArrayList<>();
        final long sx, sy;
        int holdings = 1;
        City(long sx, long sy) { this.sx = sx; this.sy = sy; offers = new GridOffers(grid, sx, sy); }
    }

    /**
     * A new city's centre on the grid, as the prototype the design was
     * measured on drew it (spec-grid 1.5): blocks of the founding level in
     * L-infinity rings round the site's block - each ring its north row west
     * to east, its east column, its south row east to west, its west column
     * - until they hold dryKm2 of dry ground. Since batch M3 (0.7.67) the
     * model's founding is CityLand.found(), and section 4 holds it to this,
     * block for block.
     */
    static City found(World w, double dryKm2) {
        City c = new City(w.foundingX(), w.foundingY());
        int level = LandGrid.levelFor(dryKm2 / World.KM2_PER_PLOT);
        long b = 1L << level;
        long target = (long) Math.ceil(dryKm2 / World.KM2_PER_PLOT - 1e-9);
        long cbx = Math.floorDiv(c.sx, b), cby = Math.floorDiv(c.sy, b);
        long dry = 0;
        Map<Long, byte[]> tiles = new HashMap<>();
        outer:
        for (int ring = 0; ; ring++) {
            for (long[] p : ringOf(cbx, cby, ring)) {
                for (long y = p[1] * b; y < p[1] * b + b; y++) for (long x = p[0] * b; x < p[0] * b + b; x++) {
                    byte t = terrain(w, tiles, x, y);
                    if (t != World.SALT && t != World.FRESH) dry++;
                }
                LandGrid.Fill f = new LandGrid.Fill(p[0] * b, p[1] * b, p[0] * b + b, p[1] * b + b, 0);
                c.grid.fill(f.x0(), f.y0(), f.x1(), f.y1(), 0);
                c.fills.add(f);
                if (dry >= target) break outer;
            }
        }
        return c;
    }

    static byte terrain(World w, Map<Long, byte[]> tiles, long x, long y) {
        long tx = Math.floorDiv(x, World.TILE), ty = Math.floorDiv(y, World.TILE);
        byte[] t = tiles.computeIfAbsent(tx * 1_000_003L + ty, k -> {
            byte[] out = new byte[World.TILE * World.TILE];
            w.tileTerrain(tx, ty, out);
            return out;
        });
        return t[(int) ((y - ty * World.TILE) * World.TILE + (x - tx * World.TILE))];
    }

    /** The blocks of one L-infinity ring round (cx, cy), in order: the north row west to east, the east column, the south row east to west, the west column. */
    static List<long[]> ringOf(long cx, long cy, int n) {
        List<long[]> out = new ArrayList<>();
        if (n == 0) { out.add(new long[] { cx, cy }); return out; }
        for (long x = cx - n; x <= cx + n; x++) out.add(new long[] { x, cy - n });
        for (long y = cy - n + 1; y <= cy + n; y++) out.add(new long[] { cx + n, y });
        for (long x = cx + n - 1; x >= cx - n; x--) out.add(new long[] { x, cy + n });
        for (long y = cy + n - 1; y > cy - n; y--) out.add(new long[] { cx - n, y });
        return out;
    }

    /** Buys round the four sides in turn, on each the offer standing nearest the site (its rectangle's middle, L-infinity); a side with none is passed. Returns the place bought, or null. */
    static GridOffers.Rect buyEvenly(City c, int turn) {
        int s = turn % GridOffers.SIDES, best = -1;
        double bd = Double.MAX_VALUE;
        for (int j = 0; j < GridOffers.PLACES; j++) {
            GridOffers.Rect o = c.offers.offer(s, j);
            if (o == null) continue;
            double d = Math.max(Math.abs((o.x0() + o.x1()) / 2.0 - c.sx - 0.5), Math.abs((o.y0() + o.y1()) / 2.0 - c.sy - 0.5));
            if (d < bd) { bd = d; best = j; }
        }
        if (best < 0) return null;
        GridOffers.Rect o = c.offers.offer(s, best);
        int h = c.holdings++;
        c.fills.add(new LandGrid.Fill(o.x0(), o.y0(), o.x1(), o.y1(), h));
        return c.offers.buy(s, best, h);
    }

    static int standing(City c) {
        int n = 0;
        for (int s = 0; s < GridOffers.SIDES; s++) for (int j = 0; j < GridOffers.PLACES; j++) if (c.offers.offer(s, j) != null) n++;
        return n;
    }

    static void newCity(World w) {
        System.out.println("--- 4. a new default city's first offers ---");
        double dry = LandManager.km2(LandManager.STARTING_SQ_FT);
        City c = found(w, dry);
        c.offers.listMissing();
        boolean blocks = true;
        int[] perSide = new int[GridOffers.SIDES];
        for (int s = 0; s < GridOffers.SIDES; s++) for (int j = 0; j < GridOffers.PLACES; j++) {
            GridOffers.Rect o = c.offers.offer(s, j);
            if (o == null) continue;
            perSide[s]++;
            blocks &= o.level() == LandGrid.MIN_LEVEL && o.across() == 1 && o.deep() == GridOffers.DEPTH_OVER_WIDTH
                    && c.grid.unowned(o.x0(), o.y0(), o.x1(), o.y1()) == (o.x1() - o.x0()) * (o.y1() - o.y0());
        }
        long km = 1L << LandGrid.MIN_LEVEL;
        System.out.printf("founded in %d blocks of %d m: %.4f km2; %d offers standing (North %d, East %d, South %d, West %d), each %.4f km2%n",
                c.fills.size(), (long) (km * World.PLOT_M), c.grid.ownedPlots() * World.KM2_PER_PLOT, standing(c), perSide[0], perSide[1], perSide[2], perSide[3],
                km * km * GridOffers.DEPTH_OVER_WIDTH * World.KM2_PER_PLOT);
        check("its centre is blocks of MIN_LEVEL, 120 m", c.grid.level() == LandGrid.MIN_LEVEL && c.fills.get(0).x1() - c.fills.get(0).x0() == km);
        CityLand model = CityLand.found(w, w.foundingX(), w.foundingY(), dry);
        check("the model founds this centre, block for block (CityLand.found(), 0.7.67)",
                model.centreRects().equals(c.fills) && model.grid().sameAs(c.grid));
        check("every first offer is one block across and DEPTH_OVER_WIDTH deep, all unowned", blocks);
        boolean fourOrFive = true;
        for (int n : perSide) fourOrFive &= n == 4 || n == 5;
        check("four or five fit on each edge (spec-grid 1.5)", fourOrFive);
        int allBy = -1, bought = 0;
        for (int turn = 0; bought < ALL_LISTED_BY && allBy < 0; turn++) {
            if (buyEvenly(c, turn) == null) continue;
            bought++;
            if (standing(c) == GridOffers.SIDES * GridOffers.PLACES) allBy = bought;
        }
        System.out.println("bought evenly, all 24 first stand after purchase " + allBy);
        check("all 24 places stand by the " + ALL_LISTED_BY + "th purchase bought evenly", allBy > 0);
    }

    /* =====================================================================
       5. A CITY BOUGHT EVENLY, CHECKED AFTER EVERY PURCHASE
       ===================================================================== */

    static City boughtEvenly(World w) {
        System.out.println("--- 5. " + CITY_PURCHASES + " purchases bought evenly, checked after every one ---");
        long t0 = System.nanoTime();
        City c = found(w, LandManager.km2(LandManager.STARTING_SQ_FT));
        c.offers.listMissing();
        int bought = 0, turn = 0, meet = 0, apart = 0, whole = 0, aspect = 0, moved = 0, claimed = 0, bare = 0, waited = 0, notches = 0, out = 0;
        long checked = 0;
        int topLevel = 0;
        boolean[] levels = new boolean[LandGrid.MAX_LEVEL + 1];
        levels[c.grid.level()] = true;
        while (bought < CITY_PURCHASES) {
            GridOffers.Rect[] before = new GridOffers.Rect[GridOffers.SIDES * GridOffers.PLACES];
            for (int i = 0; i < before.length; i++) before[i] = c.offers.offer(i / GridOffers.PLACES, i % GridOffers.PLACES);
            int s = turn % GridOffers.SIDES;
            GridOffers.Rect pick = null;
            double bd = Double.MAX_VALUE;
            for (int j = 0; j < GridOffers.PLACES; j++) {
                GridOffers.Rect o = c.offers.offer(s, j);
                if (o == null) continue;
                double d = Math.max(Math.abs((o.x0() + o.x1()) / 2.0 - c.sx - 0.5), Math.abs((o.y0() + o.y1()) / 2.0 - c.sy - 0.5));
                if (d < bd) { bd = d; pick = o; }
            }
            turn++;
            if (pick == null) continue;
            long free = c.grid.unowned(pick.x0(), pick.y0(), pick.x1(), pick.y1()), had = c.grid.ownedPlots();
            GridOffers.Rect got = buyEvenly(c, turn - 1);
            bought++;
            levels[c.grid.level()] = true;
            if (got != pick || c.grid.ownedPlots() - had != free || c.grid.unowned(pick.x0(), pick.y0(), pick.x1(), pick.y1()) != 0) claimed++;
            // the place's next: a notch when its inner edge lies nearer the site than the bought one's outer edge
            GridOffers.Rect next = c.offers.offer(pick.side(), pick.place());
            if (next != null && reach(c, next, true) < reach(c, pick, false)) notches++; else if (next != null) out++;
            GridOffers.Rect[] now = new GridOffers.Rect[before.length];
            for (int i = 0; i < now.length; i++) now[i] = c.offers.offer(i / GridOffers.PLACES, i % GridOffers.PLACES);
            for (int i = 0; i < now.length; i++) {
                if (i != pick.side() * GridOffers.PLACES + pick.place() && before[i] != null && !before[i].equals(now[i])) moved++;
                GridOffers.Rect a = now[i];
                if (a == null) { waited++; continue; }
                checked++;
                topLevel = Math.max(topLevel, a.level());
                for (int k = i + 1; k < now.length; k++) if (now[k] != null && a.meets(now[k])) meet++;
                if (!touches(c.grid, a)) apart++;
                long b = 1L << a.level();
                if (a.level() < LandGrid.MIN_LEVEL || a.level() > LandGrid.MAX_LEVEL || a.x0() % b != 0 || a.y0() % b != 0 || a.x1() % b != 0 || a.y1() % b != 0
                        || a.x0() < 0 || a.y0() < 0 || a.x1() > World.SIDE || a.y1() > World.SIDE) whole++;
                if (Math.max(a.across(), a.deep()) > 2 * Math.min(a.across(), a.deep())) aspect++;
                if (c.grid.unowned(a.x0(), a.y0(), a.x1(), a.y1()) == 0) bare++;
            }
        }
        long[] census = c.grid.census();
        System.out.printf("%d purchases in %.1f s: owned %,.1f km2, level %d (blocks of %,.0f m; the coarsest offer level %d); %d nodes, %d leaves; %d offers checked, %d places waiting summed over the purchases; the next in the place bought a notch %d times, straight out %d%n",
                bought, (System.nanoTime() - t0) / 1e9, c.grid.ownedPlots() * World.KM2_PER_PLOT, c.grid.level(), (1L << c.grid.level()) * World.PLOT_M, topLevel,
                census[0], census[1], checked, waited, notches, out);
        check("after every purchase no two standing offers meet", meet == 0);
        check("...every one touches the city", apart == 0);
        check("...every one is whole blocks of its level, on the world", whole == 0);
        check("...none is over 2:1", aspect == 0);
        check("...every one holds ground the city does not own", bare == 0);
        check("...the other places stand as they were", moved == 0);
        check("...and a purchase claims exactly its rectangle's unowned plots", claimed == 0);
        check("no listing failed to clip", c.offers.clipFailures() == 0);
        int district = Integer.numberOfTrailingZeros(World.DISTRICT);
        boolean every = true;
        for (int k = LandGrid.MIN_LEVEL; k <= c.grid.level(); k++) every &= levels[k];
        check("the city grows past the district's level (" + district + "), through every level under it", c.grid.level() > district && every);
        check("its centre's blocks and its purchases replayed: the same tree, node for node", LandGrid.replay(c.fills).sameAs(c.grid));
        return c;
    }

    /** How far out an offer reaches from the site, in plots: its inner edge's distance, or its outer edge's. */
    static double reach(City c, GridOffers.Rect a, boolean inner) {
        double sx = c.sx + 0.5, sy = c.sy + 0.5;
        switch (a.side()) {
            case 0:  return inner ? sy - a.y1() : sy - a.y0();
            case 1:  return inner ? a.x0() - sx : a.x1() - sx;
            case 2:  return inner ? a.y0() - sy : a.y1() - sy;
            default: return inner ? sx - a.x1() : sx - a.x0();
        }
    }

    /** Whether an offer's rectangle holds, or has across one of its edges, a plot the city owns. */
    static boolean touches(LandGrid g, GridOffers.Rect a) {
        long w = a.x1() - a.x0(), h = a.y1() - a.y0();
        long inside = w * h - g.unowned(a.x0(), a.y0(), a.x1(), a.y1());
        long beside = (w - g.unowned(a.x0(), a.y0() - 1, a.x1(), a.y0())) + (w - g.unowned(a.x0(), a.y1(), a.x1(), a.y1() + 1))
                + (h - g.unowned(a.x0() - 1, a.y0(), a.x0(), a.y1())) + (h - g.unowned(a.x1(), a.y0(), a.x1() + 1, a.y1()));
        return inside > 0 || beside > 0;
    }

    /* =====================================================================
       6. A NOTCH FIRST
       ===================================================================== */

    static void notch(World w) {
        System.out.println("--- 6. a notch is offered before ground farther out ---");
        long sx = w.foundingX(), sy = w.foundingY();
        int level = LandGrid.MIN_LEVEL;
        long b = 1L << level, cbx = Math.floorDiv(sx, b), cby = Math.floorDiv(sy, b);
        int half = 4;                           // a square of 9 x 9 blocks, 1,296 plots: under the (6 x 8)^2 = 2,304 where the level becomes 3
        int place = 2;
        // the north row's block in place 2's lane, nearest the lane's middle
        GridOffers geometry = new GridOffers(new LandGrid(), sx, sy);
        long notchBx = Long.MIN_VALUE, notchBy = cby - half;
        double bestOff = Double.MAX_VALUE;
        for (long bx = cbx - half; bx <= cbx + half; bx++) {
            double[] c = geometry.centreOf(level, bx, notchBy);
            if (GridOffers.sideOf(c[0], c[1]) != 0) continue;
            double t = c[0] / -c[1];
            if (GridOffers.inLane(t, place) && Math.abs(t - GridOffers.rayT(place)) < bestOff) { bestOff = Math.abs(t - GridOffers.rayT(place)); notchBx = bx; }
        }
        LandGrid full = new LandGrid(), notched = new LandGrid();
        for (long by = cby - half; by <= cby + half; by++) for (long bx = cbx - half; bx <= cbx + half; bx++) {
            full.fill(bx * b, by * b, bx * b + b, by * b + b, 0);
            if (bx != notchBx || by != notchBy) notched.fill(bx * b, by * b, bx * b + b, by * b + b, 0);
        }
        GridOffers.Rect straight = new GridOffers(full, sx, sy).list(0, place);
        GridOffers.Rect first = new GridOffers(notched, sx, sy).list(0, place);
        System.out.printf("a %d x %d square of %d m blocks, level %d; North %d's notch at block (%d, %d); listed %s, without it %s%n",
                2 * half + 1, 2 * half + 1, (long) (b * World.PLOT_M), notched.level(), place + 1, notchBx, notchBy, first, straight);
        check("the fixture stays at MIN_LEVEL and its notch is in the place's lane", notched.level() == LandGrid.MIN_LEVEL && notchBx != Long.MIN_VALUE);
        check("with a notch, the place's offer takes in the notch", first != null && first.meets(notchBx * b, notchBy * b, notchBx * b + b, notchBy * b + b));
        check("...its inner edge the notch's, nearer than the square's edge", first != null && first.y1() == notchBy * b + b);
        double[] in = straight == null ? null : geometry.centreOf(level, straight.x0() / b, straight.y1() / b - 1);
        check("without one, the place lists straight out from the square's edge", straight != null && straight.y1() == (cby - half) * b
                && GridOffers.sideOf(in[0], in[1]) == 0 && GridOffers.inLane(in[0] / -in[1], place));
    }

    /* =====================================================================
       7. OWNER() IN TIME
       ===================================================================== */

    static void timing(City c) {
        System.out.println("--- 7. a plot's owner, in time ---");
        LandGrid g = c.grid;
        Random rnd = new Random(1);
        long[] xs = new long[TIMED_PLOTS], ys = new long[TIMED_PLOTS];
        for (int i = 0; i < TIMED_PLOTS; i++) {
            xs[i] = g.minX() + (long) (rnd.nextDouble() * (g.maxX() - g.minX()));
            ys[i] = g.minY() + (long) (rnd.nextDouble() * (g.maxY() - g.minY()));
        }
        double[] ns = new double[TIMING_RUNS];
        long sink = 0;
        for (int r = 0; r < TIMING_RUNS; r++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < TIMED_PLOTS; i++) sink += g.owner(xs[i], ys[i]);
            ns[r] = (System.nanoTime() - t0) / (double) TIMED_PLOTS;
        }
        Arrays.sort(ns);
        System.out.printf("owner() of random plots in the city's box: median %.1f ns (least %.1f, most %.1f) over %d runs [%d]%n",
                ns[TIMING_RUNS / 2], ns[0], ns[TIMING_RUNS - 1], TIMING_RUNS, sink & 1);
        check("owner() answers within OWNER_NS (" + (int) OWNER_NS + " ns), the median of " + TIMING_RUNS + " runs", ns[TIMING_RUNS / 2] <= OWNER_NS);
    }
}
