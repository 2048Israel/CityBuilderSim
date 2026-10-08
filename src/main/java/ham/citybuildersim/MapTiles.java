package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again only when what it is painted from has changed, a few painted tiles kept for the hover and a change of level, the per-tile path from inputs to pixels, and the far view's node pixels from the world's ground and the districts' counts.
 *
 * WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6). The
 * view holds its images (ui/MapView: JavaFX's WritableImage, one cache a
 * level); what it paints them from is arithmetic, and kept here so MapCheck
 * can time the path a frame takes and see that a tile whose counts did not
 * change keeps its picture. A month changes a few districts; a district's
 * change moves its own tiles (CityMap's deal since 0.7.64; up to half a
 * district into its neighbours before); a purchase changes what is owned.
 * Rather than track which, the view stamps every tile it shows when the map
 * or the land moved (stamp()), and repaints only those whose stamp changed -
 * the spec's "a district change repaints only the tiles whose counts
 * changed, and a purchase repaints the districts it touched", as one rule.
 *
 * NOT THREAD-SAFE: the FX thread's alone, as CityMap is. Only nodeTerrain()
 * (the world's ground, which World keeps safe) runs on the view's worker.
 */
public final class MapTiles {

    /** Tiles' ground kept: 2,048 (spec-land 2.6), a kilobyte each - a far screen of NEAR_TILES_MOST and its margin. */
    public static final int TERRAIN_KEPT = 2048;

    /** Painted tiles kept, with their inputs: 64 (star) - measured at up to about 36 KB each on MapCheck 7's densest screen, x 10,000 (the painter's arrays grow to 512), so the design's 1,024 would be 36 MB; the hover asks for one and a change of level for a screen's worth, which a repaint (0.1 to 0.2 ms) serves. */
    public static final int PAINTED_KEPT = 64;

    /** What every cache of the view together may hold, in MB: 48 (spec-land 2.6). */
    public static final double BUDGET_MB = 48;

    /** The view the budget is sized for, in pixels: the land office's expanded map in a 1,389 x 868 window - 1,345 across inside the pane's padding, 806 down under its head (UserInterface's chart pane, 0.7.23). */
    public static final int DESIGN_W = 1345, DESIGN_H = 806;

    /** A tile's painted bytes, kept: its inputs, the picture and the stamp it was painted at. */
    private static final class Kept {
        final TilePainter.Input in = new TilePainter.Input();
        final TilePainter.Painted p = new TilePainter.Painted();
        long stamp;
    }

    private final CityMap map;
    private final Map<Long, byte[]> terrain = new LinkedHashMap<>(TERRAIN_KEPT * 2, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, byte[]> e) { return size() > TERRAIN_KEPT; }
    };
    private final Map<Long, Kept> painted = new LinkedHashMap<>(PAINTED_KEPT * 2, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, Kept> e) { return size() > PAINTED_KEPT; }
    };

    /** How many tiles have been painted and how many stamps taken: what a harness reads. */
    private long paints, stamps;

    public MapTiles(CityMap map) { this.map = map; }

    /** The map these tiles are painted from. */
    public CityMap map() { return map; }

    public long paints() { return paints; }
    public long stamps() { return stamps; }

    /** What a view's pictures are current against: the map's changes (CityMap.changes()), and the land's purchases and centre - a purchase between months owns ground at once (a tile's ownership is the live land's) that the map measures only at the month's end. The land's rectangles' stamp since 0.7.67 (CityLand.stamp(); the centre's half-side before). */
    public static long version(CityMap map, CityLand land) {
        long h = World.mix(map.changes() ^ 0x5EED0A11L);
        h = World.mix(h ^ land.purchases().size());
        return World.mix(h ^ land.stamp());
    }

    /** A tile's key: its column and row, the world's tiles numbering under 2^20 each way. */
    public static long key(long tx, long ty) { return (tx << 24) ^ (ty & 0xffffffL); }

    /** A tile's ground, from the world the first time and kept. */
    public byte[] terrain(long tx, long ty) {
        long k = key(tx, ty);
        byte[] t = terrain.get(k);
        if (t == null) {
            t = new byte[TilePainter.PLOTS];
            World.of(map.seed()).tileTerrain(tx, ty, t);
            terrain.put(k, t);
        }
        return t;
    }

    /** A tile's inputs into `in` (its ground from the cache), and their stamp. */
    public long input(long tx, long ty, TilePainter.Input in) {
        map.tileInput(tx, ty, in, terrain(tx, ty));
        stamps++;
        return stamp(in);
    }

    /**
     * A stamp of everything a tile is painted from but its ground, which is
     * the world's and never changes: what is owned, the buildings and road
     * plots dealt to it, its neighbours' roads, the plan's runs through it
     * (0.7.72) and its sites with their states and mines. Two tiles with one
     * stamp paint the same pixels.
     */
    public static long stamp(TilePainter.Input in) {
        long h = World.mix(in.seed ^ World.mix(in.tx * 0x9E3779B97F4A7C15L ^ in.ty));
        h = World.mix(h ^ packed(in.owned));
        for (int c : in.counts) h = World.mix(h ^ c);
        for (int b : in.roadBudget) h = World.mix(h ^ b);
        for (boolean b : in.neighbourRoads) h = World.mix(h ^ (b ? 1 : 2));
        // The plan's runs through it: its main streets, highways and track (0.7.72).
        h = World.mix(h ^ in.plans);
        for (int k = 0; k < in.plans; k++) {
            h = World.mix(h ^ ((long) in.planX0[k] << 40 ^ (long) in.planY0[k] << 30 ^ (long) in.planX1[k] << 20 ^ (long) in.planY1[k] << 10 ^ in.planKind[k]));
        }
        h = World.mix(h ^ in.sites);
        for (int s = 0; s < in.sites; s++) {
            h = World.mix(h ^ ((long) in.siteX0[s] << 40 ^ (long) in.siteY0[s] << 30 ^ (long) in.siteX1[s] << 20 ^ (long) in.siteY1[s] << 10));
            h = World.mix(h ^ ((long) in.siteKind[s] << 40 ^ (long) in.siteState[s] << 32 ^ (in.siteMine[s] + 1)));
        }
        return h;
    }

    /** A plot-by-plot flag folded into one number, sixty-four plots a word. */
    private static long packed(boolean[] flags) {
        long h = 0;
        for (int i = 0; i < flags.length; i += 64) {
            long w = 0;
            for (int b = 0; b < 64 && i + b < flags.length; b++) if (flags[i + b]) w |= 1L << b;
            h = World.mix(h ^ w ^ i);
        }
        return h;
    }

    /**
     * A tile painted from `in` (whose stamp is `stamp`): the kept picture
     * when it was painted at that stamp, else painted now and kept. The
     * returned inputs and picture are the cache's: read them, do not keep them.
     */
    public TilePainter.Painted painted(long tx, long ty, TilePainter.Input in, long stamp) {
        long k = key(tx, ty);
        Kept kept = painted.get(k);
        if (kept != null && kept.stamp == stamp) return kept.p;
        if (kept == null) kept = new Kept();
        copy(in, kept.in);
        TilePainter.paint(kept.in, kept.p);
        kept.stamp = stamp;
        painted.put(k, kept);
        paints++;
        return kept.p;
    }

    /** The inputs a kept picture was painted from, or null when the tile is not kept. */
    public TilePainter.Input paintedInput(long tx, long ty) {
        Kept kept = painted.get(key(tx, ty));
        return kept == null ? null : kept.in;
    }

    /** A copy of a tile's inputs, the arrays reused. */
    static void copy(TilePainter.Input a, TilePainter.Input b) {
        b.seed = a.seed;
        b.tx = a.tx;
        b.ty = a.ty;
        System.arraycopy(a.terrain, 0, b.terrain, 0, TilePainter.PLOTS);
        System.arraycopy(a.owned, 0, b.owned, 0, TilePainter.PLOTS);
        b.counts = a.counts.clone();
        b.model = a.model.clone();
        b.types = a.types;
        System.arraycopy(a.roadBudget, 0, b.roadBudget, 0, a.roadBudget.length);
        System.arraycopy(a.neighbourRoads, 0, b.neighbourRoads, 0, a.neighbourRoads.length);
        b.clearSites();
        for (int s = 0; s < a.sites; s++) {
            b.addSite(a.siteX0[s], a.siteY0[s], a.siteX1[s], a.siteY1[s], a.siteKind[s], a.siteState[s], a.siteMine[s], a.siteKey[s]);
        }
    }

    /**
     * The per-tile path a frame takes: the tile's inputs (its ground kept),
     * painted - or the kept picture when its stamp has not moved - and
     * rastered at a level's width into `out` (TileRaster.raster() at L0, its
     * blocks at L1 and L2, gravel hidden at L2). The stamp, for the image's
     * own record.
     */
    public long pixels(long tx, long ty, int level, int px, TilePainter.Input in, int[] out) {
        long stamp = input(tx, ty, in);
        TilePainter.Painted p = painted(tx, ty, in, stamp);
        TilePainter.Input painted = paintedInput(tx, ty);
        raster(painted == null ? in : painted, p, level, px, out);
        return stamp;
    }

    /** A painted tile rastered as a level draws it. */
    public static void raster(TilePainter.Input in, TilePainter.Painted p, int level, int px, int[] out) {
        if (level == MapFrame.L0) TileRaster.raster(in, p, px, out);
        else TileRaster.blocks(in, p, px, MapFrame.blockOf(level), level == MapFrame.L1, out);
    }

    /** Pixels in a tile's image at a width. */
    public static int tileImagePixels(int px) { return World.TILE * px * World.TILE * px; }

    /* =====================================================================
       THE BUDGET (spec-land 2.6: every cache within 48 MB)
       ===================================================================== */

    /**
     * The tiles a level's image cache keeps for a view this size (star): one
     * screen at the level's least scale with a tile of margin round it
     * (MapFrame.tilesAt()) - at L0 from L0_FROM at SMALL_TILE_PX and from
     * BIG_TILES_FROM at BIG_TILE_PX, at L1 from L1_FROM - and at L2
     * NEAR_TILES_MOST, past which the far nodes draw. Fewer and a screen
     * would paint its own tiles out of the cache as it drew them.
     */
    public static long tilesKept(MapFrame f, int level, int px) {
        switch (level) {
            case MapFrame.L0: return f.tilesAt(px >= MapFrame.BIG_TILE_PX ? MapFrame.BIG_TILES_FROM : MapFrame.L0_FROM);
            case MapFrame.L1: return f.tilesAt(MapFrame.L1_FROM);
            default:          return MapFrame.NEAR_TILES_MOST;
        }
    }

    /** A kept painted tile's bytes, from its arrays' lengths: what PAINTED_KEPT of them weigh. */
    public static long paintedBytes(TilePainter.Input in, TilePainter.Painted p) {
        long input = 2L * TilePainter.PLOTS + 4L * (in.counts.length + in.model.length) + 16 + 4
                + 36L * in.siteX0.length + 17L * in.planX0.length;
        long picture = 4L * TilePainter.PLOTS + 2L * 2 * TilePainter.PLOTS + 28L * p.bx.length;
        return input + picture;
    }

    /**
     * Every cache of the view together, in bytes, for a view this size: each
     * level's images at four bytes a pixel (L0 at both widths, L1, L2), the
     * far nodes (MapFrame.nodesAtMost()), the tiles' ground and PAINTED_KEPT
     * painted tiles of `paintedBytes` each.
     */
    public static double budgetBytes(MapFrame f, double paintedBytes) {
        double bytes = 0;
        bytes += 4.0 * tilesKept(f, MapFrame.L0, MapFrame.SMALL_TILE_PX) * tileImagePixels(MapFrame.SMALL_TILE_PX);
        bytes += 4.0 * tilesKept(f, MapFrame.L0, MapFrame.BIG_TILE_PX) * tileImagePixels(MapFrame.BIG_TILE_PX);
        bytes += 4.0 * tilesKept(f, MapFrame.L1, MapFrame.MIDDLE_TILE_PX) * tileImagePixels(MapFrame.MIDDLE_TILE_PX);
        bytes += 4.0 * tilesKept(f, MapFrame.L2, MapFrame.FAR_TILE_PX) * tileImagePixels(MapFrame.FAR_TILE_PX);
        bytes += 4.0 * f.nodesAtMost() * MapFrame.NODE_PX * MapFrame.NODE_PX;
        bytes += (double) TERRAIN_KEPT * TilePainter.PLOTS;
        bytes += PAINTED_KEPT * paintedBytes;
        return bytes;
    }

    /* =====================================================================
       THE FAR NODES
       ===================================================================== */

    /** A far node's ground: MapFrame.NODE_PX squared samples a `plots` apart from (x0, y0), a World class each. Thread-safe: the worker's. */
    public static byte[] nodeTerrain(long seed, long x0, long y0, long plots) {
        byte[] out = new byte[MapFrame.NODE_PX * MapFrame.NODE_PX];
        World.of(seed).regionTerrain(x0, y0, (int) Math.min(Integer.MAX_VALUE, plots), MapFrame.NODE_PX, out);
        return out;
    }

    /**
     * A far node's pixels from its ground: each sample's terrain colour,
     * dimmed where the city does not own it (TileRaster's UNOWNED_DIM), and
     * where it owns dry ground its district's tint over it - the class its
     * buildings take the most ground of, at TileRaster's BLOCK_ALPHA by the
     * share of the district's dry ground they use (BLOCK_FILLS), homes as
     * flats where the district is densest - the mockup's far blocks, a
     * district a block.
     */
    public void nodePixels(long x0, long y0, long plots, byte[] ground, int[] out) {
        CityLand land = map.land();
        int n = MapFrame.NODE_PX;
        Map<CityMap.District, Integer> tints = new java.util.HashMap<>();
        for (int j = 0; j < n; j++) {
            long y = y0 + j * plots + plots / 2;
            for (int i = 0; i < n; i++) {
                long x = x0 + i * plots + plots / 2;
                byte g = ground[j * n + i];
                int c = TileRaster.GROUND[g];
                boolean owned = land != null && land.ownsPlot(x, y);
                if (!owned) {
                    out[j * n + i] = TileRaster.blend(c, TileRaster.VOID, TileRaster.UNOWNED_DIM);
                    continue;
                }
                if (g != World.SALT && g != World.FRESH) {
                    CityMap.District d = map.districtOfTile(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
                    if (d != null) {
                        Integer tint = tints.get(d);
                        if (tint == null) {
                            tint = tintOf(d);
                            tints.put(d, tint);
                        }
                        if (tint != 0) c = TileRaster.blend(c, tint | 0xff000000, ((tint >>> 24) & 255) / 255.0);
                    }
                }
                out[j * n + i] = c;
            }
        }
    }

    /** A district's tint for the far nodes: its colour with its opacity in the top byte, 0 for none built. */
    int tintOf(CityMap.District d) {
        if (d.owned() <= 0) return 0;
        BuildingVisual.Type[] types = map.types();
        double[] ground = new double[BuildingVisual.CLASSES];
        double flats = 0;
        for (int t = 0; t < types.length; t++) {
            int c = d.count(t);
            if (c <= 0 || types[t] == null || !types[t].drawn()) continue;
            double sq = (double) c * types[t].sqFt();
            ground[types[t].cls()] += sq;
            if (types[t].flats()) flats += sq;
        }
        int best = BuildingVisual.HOME;
        for (int k = 1; k < BuildingVisual.CLASSES; k++) if (ground[k] > ground[best]) best = k;
        if (!(ground[best] > 0)) return 0;
        double f = Math.min(1, d.used() / d.owned());
        int lvl = f < TileRaster.BLOCK_FILLS[0] ? 0 : f < TileRaster.BLOCK_FILLS[1] ? 1 : f < TileRaster.BLOCK_FILLS[2] ? 2 : 3;
        int colour = best == BuildingVisual.HOME
                ? (lvl == 3 || flats * 2 >= ground[BuildingVisual.HOME] ? BuildingVisual.FLATS_FILL : BuildingVisual.FILL[BuildingVisual.HOME])
                : BuildingVisual.FILL[best];
        return (colour & 0xffffff) | ((int) Math.round(TileRaster.BLOCK_ALPHA[lvl] * 255) << 24);
    }
}
