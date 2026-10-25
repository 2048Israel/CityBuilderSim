package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again only when what it is painted from has changed, a few painted tiles kept for the hover and a change of level, the per-tile path from inputs to pixels, and the far view's node pixels from the world's ground, the districts' pictures and shares, and the city's runs and edge.
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
 * (the world's ground, which World keeps safe) and, since 0.7.88, the
 * district plans' jobs (CityMap.Job: their inputs gathered on the FX thread,
 * kept there by CityMap.adopt()) run on the view's worker: a tile is painted
 * only once ready() says its plans are drawn, and jobs() hands out what
 * draws them. Since 0.7.128 (batch MM1) a plan's job draws its district's
 * picture with it (DistrictPicture), which the far nodes read; a node drawn
 * without one it wanted says so (nodePixels()'s count, wanted()), and the
 * view asks CityMap.pictureJobs() for them while picturesFit().
 */
public final class MapTiles {

    /** Tiles' ground kept: 2,048 (spec-land 2.6), a kilobyte each - a far screen of NEAR_TILES_MOST and its margin. */
    public static final int TERRAIN_KEPT = 2048;

    /** Painted tiles kept, with their inputs: 1, the hover's (star RD2-5; 64 until 0.7.87) - about 31 KB each on MapCheck 7's densest screen, x 10,000 (the painter's arrays grow to 512). Since 0.7.88 a tile paints from its district plans in about 0.1 ms, so a frame paints a tile again rather than keep it, and the 48 MB holds the plans instead (CityMap.PLANS_KEPT). */
    public static final int PAINTED_KEPT = 1;

    /** What every cache of the view together may hold, in MB: 48 (spec-land 2.6). */
    public static final double BUDGET_MB = 48;

    /** How much the far view's highway line is lightened toward white against the dark ground (spec-freight 4.8): 40%. */
    public static final double FAR_HIGHWAY_LIGHTEN = 0.4;

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
            World.of(map.worldKey()).tileTerrain(tx, ty, t);
            terrain.put(k, t);
        }
        return t;
    }

    /** Whether tile (tx, ty) paints without planning (0.7.88): the district plans it is drawn from are drawn (CityMap.tileReady()). */
    public boolean ready(long tx, long ty) {
        return map.tileReady(tx, ty);
    }

    /** The jobs that draw tile (tx, ty)'s plans, to run in order on one thread away from the screen's and then keep (CityMap.adopt()); none handed out twice. */
    public java.util.List<CityMap.Job> jobs(long tx, long ty) {
        return map.tileJobs(tx, ty);
    }

    /** A tile's inputs into `in` (its ground from the cache), and their stamp - its plans made here if they are not drawn (a harness's way; the view asks ready() first). */
    public long input(long tx, long ty, TilePainter.Input in) {
        map.tileInput(tx, ty, in, terrain(tx, ty));
        stamps++;
        return stamp(in);
    }

    /**
     * A stamp of everything a tile is painted from but its ground, which is
     * the world's and never changes: what is owned, its plan's streets and
     * those just beyond its edges (and, since FX5, on the ring of REACH plots
     * about it), the city's highways and track through it,
     * its buildings' boxes (0.7.88; the counts and road plots dealt to it, its
     * neighbours' roads and the deal's plan before), its sites with their
     * states and mines, and (0.8.1) the freight station's place, whether the
     * railway carries goods and on how many of a yard's tracks wagons stand,
     * and (FX5) the railway's bends that reach it with the plots they replace,
     * and (FX5 Y2) the yards that reach it, whole.
     * Two tiles with one stamp paint the same pixels.
     */
    public static long stamp(TilePainter.Input in) {
        long h = World.mix(in.seed ^ World.mix(in.tx * 0x9E3779B97F4A7C15L ^ in.ty));
        h = World.mix(h ^ packed(in.owned));
        h = World.mix(h ^ packed(in.street));
        h = World.mix(h ^ packed(in.beyond));
        // The ring of streets about it (FX5): a crossing on its corner is read whole - nothing with none.
        long ring = packed(in.ring);
        if (ring != 0) h = World.mix(h ^ ring);
        h = World.mix(h ^ packed(in.fixed));
        h = World.mix(h ^ packed(in.fixedBeyond));
        h = World.mix(h ^ in.stationX);
        h = World.mix(h ^ in.stationY);
        h = World.mix(h ^ (in.goodsByRail ? 1 : 0) ^ in.wagonTracks);
        h = World.mix(h ^ in.buildings);
        for (int b = 0; b < 2 * in.buildings; b++) h = World.mix(h ^ in.boxes[b]);
        h = World.mix(h ^ in.sites);
        for (int s = 0; s < in.sites; s++) {
            h = World.mix(h ^ ((long) in.siteX0[s] << 40 ^ (long) in.siteY0[s] << 30 ^ (long) in.siteX1[s] << 20 ^ (long) in.siteY1[s] << 10));
            h = World.mix(h ^ ((long) in.siteKind[s] << 40 ^ (long) in.siteState[s] << 32 ^ (in.siteMine[s] + 1)));
        }
        // The yards that reach it, whole (FX5 Y2): the box, the type, the layout of the fan and whether it is the station - nothing with none.
        for (int k = 0; k < in.yards * TilePainter.Input.YARD_INTS; k++) h = World.mix(h ^ in.yard[k] * 0x9E3779B97F4A7C15L ^ (k + 1));
        // The interchanges' reserves that reach it (batch FR7) - nothing with none.
        for (int r = 0; r < in.reserves * TilePainter.RESERVE_INTS; r++) h = World.mix(h ^ in.reserve[r] * 0x9E3779B97F4A7C15L ^ r);
        // The railway's bends that reach it and the plots they replace (FX5, TrackCurves) - nothing with none: a tile repaints when its bends change.
        for (int k = 0; k < in.bends * TilePainter.Input.BEND_DOUBLES; k++) h = World.mix(h ^ Double.doubleToLongBits(in.bend[k]) * 31 ^ k);
        long curved = packed(in.curved);
        if (in.bends > 0 || curved != 0) h = World.mix(h ^ curved ^ in.bends);
        // The sidings its district's plan laid over it (batch FR7 slice 5) - nothing with none.
        long sidings = packed(in.siding);
        if (sidings != 0) h = World.mix(h ^ sidings);
        // The avenues about it and the origin they run out from (batch CL4) - nothing with none: a tile repaints when an avenue plot beside it is laid.
        if (in.avenueMarks != 0) {
            h = World.mix(h ^ packed(in.avenueRing) ^ in.avenueMarks);
            h = World.mix(h ^ ((long) in.avenueOx << 32) ^ (in.avenueOy & 0xffffffffL));
        }
        // The curves about it (batch CL4, slice D) - nothing with none: a tile repaints when a curve beside it is laid, and when one it holds moves.
        if (in.curves != 0 || in.curveMarks != 0) {
            h = World.mix(h ^ packed(in.curveRing) ^ in.curveMarks ^ ((long) in.curves << 40));
            for (int k = 0; k < in.curveLen; k++) h = World.mix(h ^ Double.doubleToLongBits(in.curveGeom[k]) * 31 ^ k);
        }
        // The works at sea (0.7.97): the quays', jackets' and wells' plots, the rings and the pipes - nothing with none.
        long sea = packed(in.sea);
        if (sea != 0) h = World.mix(h ^ sea);
        for (int r = 0; r < in.rings; r++) h = World.mix(h ^ Double.doubleToLongBits(in.ringX[r]) * 31 ^ Double.doubleToLongBits(in.ringY[r]));
        for (int q = 0; q < in.pipes; q++) {
            h = World.mix(h ^ Double.doubleToLongBits(in.pipeAX[q]) * 31 ^ Double.doubleToLongBits(in.pipeAY[q]));
            h = World.mix(h ^ Double.doubleToLongBits(in.pipeBX[q]) * 31 ^ Double.doubleToLongBits(in.pipeBY[q]));
        }
        return h;
    }

    /** Bytes a plot folded into one number, eight a word. */
    private static long packed(byte[] codes) {
        long h = 0;
        for (int i = 0; i < codes.length; i += 8) {
            long w = 0;
            for (int b = 0; b < 8 && i + b < codes.length; b++) w |= (codes[i + b] & 0xffL) << (8 * b);
            if (w != 0) h = World.mix(h ^ w ^ i);
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
        System.arraycopy(a.relief, 0, b.relief, 0, TilePainter.PLOTS);
        System.arraycopy(a.owned, 0, b.owned, 0, TilePainter.PLOTS);
        System.arraycopy(a.street, 0, b.street, 0, TilePainter.PLOTS);
        System.arraycopy(a.beyond, 0, b.beyond, 0, a.beyond.length);
        System.arraycopy(a.ring, 0, b.ring, 0, a.ring.length);
        System.arraycopy(a.fixed, 0, b.fixed, 0, TilePainter.PLOTS);
        System.arraycopy(a.fixedBeyond, 0, b.fixedBeyond, 0, a.fixedBeyond.length);
        // ...and the runs' marks, the ramps and 45-degree stretches (0.7.98, batch O14: until then the kept copy the raster
        // reads had none, so no ramp or smooth band was ever drawn).
        System.arraycopy(a.marks, 0, b.marks, 0, TilePainter.PLOTS);
        System.arraycopy(a.siding, 0, b.siding, 0, TilePainter.PLOTS);
        System.arraycopy(a.marksBeyond, 0, b.marksBeyond, 0, a.marksBeyond.length);
        // The avenues about it (batch CL4): the marks and the origin, as the kept copy the raster reads must have them.
        b.clearAvenues();
        if (a.avenueMarks != 0) {
            System.arraycopy(a.avenue, 0, b.avenue, 0, TilePainter.PLOTS);
            System.arraycopy(a.avenueRing, 0, b.avenueRing, 0, a.avenueRing.length);
            b.avenueMarks = a.avenueMarks;
            b.avenueOx = a.avenueOx;
            b.avenueOy = a.avenueOy;
        }
        // The curves about it (batch CL4, slice D): the marks and the geometry, as the kept copy the raster reads must have them.
        b.copyCurves(a);
        b.stationX = a.stationX;
        b.stationY = a.stationY;
        b.goodsByRail = a.goodsByRail;
        b.wagonTracks = a.wagonTracks;
        b.types = a.types;
        if (b.counts.length != a.counts.length) b.counts = new int[a.counts.length];
        // ...each as it stands, a yard's part on a tile it is not counted on still a part, and the counts as they are (FX5 Y2).
        b.copyBuildings(a);
        // The yards that reach it, whole (FX5 Y2).
        b.copyYards(a);
        b.clearSites();
        for (int s = 0; s < a.sites; s++) {
            b.addSite(a.siteX0[s], a.siteY0[s], a.siteX1[s], a.siteY1[s], a.siteKind[s], a.siteState[s], a.siteMine[s], a.siteKey[s]);
        }
        // The railway's bends and the plots they replace (FX5).
        b.copyBends(a);
        // The interchanges' reserves that reach it (batch FR7), whose loops the raster draws from the kept copy too (CL4: left behind
        // until 0.8.19, so the view, which rasters a kept tile from this copy, drew no loop though the stamp folded them).
        b.clearReserves();
        for (int r = 0; r < a.reserves; r++) {
            int o = r * TilePainter.RESERVE_INTS;
            b.addReserve(a.reserve[o], a.reserve[o + 1], a.reserve[o + 2], a.reserve[o + 3], a.reserve[o + 4], a.reserve[o + 5], a.reserve[o + 6], a.reserve[o + 7]);
        }
        // The works at sea (0.7.97).
        b.clearSea();
        System.arraycopy(a.sea, 0, b.sea, 0, TilePainter.PLOTS);
        for (int r = 0; r < a.rings; r++) b.addRing(a.ringX[r], a.ringY[r]);
        for (int q = 0; q < a.pipes; q++) b.addPipe(a.pipeAX[q], a.pipeAY[q], a.pipeBX[q], a.pipeBY[q]);
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

    /** A kept painted tile's bytes, from its arrays' lengths: what PAINTED_KEPT of them weigh - since 0.7.88 its plan's streets and runs and its buildings' boxes in, its plots' use, kind, width, role, flags, building and site out; since 0.7.97 each plot's work at sea both ways, and the rings' and pipes' ends in; since CL4 the avenues' marks, on the tile and its ring, and the curves' (their marks on the tile and its ring, their geometry). */
    public static long paintedBytes(TilePainter.Input in, TilePainter.Painted p) {
        long input = 4L * TilePainter.PLOTS + 2L * in.beyond.length + in.ring.length + 4L * (in.counts.length + in.boxes.length + in.yard.length) + 16 + 4
                + 36L * in.siteX0.length + in.sea.length + 8L * (in.ringX.length + in.ringY.length) + 32L * in.pipeAX.length
                + in.avenue.length + in.avenueRing.length + in.curve.length + in.curveRing.length + 8L * in.curveLen;
        long picture = 6L * TilePainter.PLOTS + 2L * 2 * TilePainter.PLOTS + 29L * p.bx.length + p.sea.length;
        return input + picture;
    }

    /**
     * Every cache of the view together, in bytes, for a view this size: each
     * level's images at four bytes a pixel (L0 at both widths, L1, L2), the
     * far nodes (MapFrame.nodesAtMost()), the tiles' ground and PAINTED_KEPT
     * painted tiles of `paintedBytes` each - and since 0.7.88 the district
     * plans the tiles are painted from, CityMap.PLANS_KEPT of `planBytes`
     * each, and since 0.7.128 the far nodes' district pictures,
     * CityMap.PICTURES_KEPT of DistrictPicture.BYTES.
     */
    public static double budgetBytes(MapFrame f, double paintedBytes, double planBytes) {
        return budgetBytes(f, paintedBytes) + CityMap.PLANS_KEPT * planBytes + (double) CityMap.PICTURES_KEPT * DistrictPicture.BYTES;
    }

    /** ...without the plans: what the view's own caches hold. */
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
        return nodeTerrain(World.Key.generated(seed), x0, y0, plots);
    }

    /**
     * ...on the world a key names (0.7.115; the map's: CityMap.worldKey()).
     * A generated world's is its region at the node's stride
     * (World.regionTerrain()); since 0.7.119 (batch E5) an Earth world's is
     * its far node (EarthTerrain.farTerrain(), spec-earth 3's "Far views"):
     * its rivers stroked from their vectors at least a pixel wide, its small
     * lakes drawn from their points and areas, the shares - the 4' far layers
     * from EarthTerrain.FAR_STRIDE - read at each pixel's centre, and its
     * forest dithered by a hash.
     */
    public static byte[] nodeTerrain(World.Key world, long x0, long y0, long plots) {
        return nodeTerrain(world, x0, y0, plots, null);
    }

    /**
     * ...and, where `relief` is not null (MapFrame.NODE_PX squared bytes), the node's relief beside it (ET1
     * slice D; spec-earth2-terrain 9.3): each pixel's World relief (FLAT, FOOTHILL, STEEP, VERY_STEEP) as the
     * far rule draws it at the node's stride (EarthTerrain.farTerrain()), FLAT where its cover is not dry
     * ground and everywhere on a generated world. The cover is byte for byte what it is without.
     */
    public static byte[] nodeTerrain(World.Key world, long x0, long y0, long plots, byte[] relief) {
        byte[] out = new byte[MapFrame.NODE_PX * MapFrame.NODE_PX];
        World w = World.of(world);
        int stride = (int) Math.min(Integer.MAX_VALUE, plots);
        if (w.isEarth()) w.earthTerrain().farTerrain(x0, y0, stride, MapFrame.NODE_PX, out, relief);
        else {
            w.regionTerrain(x0, y0, stride, MapFrame.NODE_PX, out);
            if (relief != null) java.util.Arrays.fill(relief, 0, MapFrame.NODE_PX * MapFrame.NODE_PX, World.FLAT);
        }
        return out;
    }

    /* ----- the far nodes from the district pictures (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) ----- */

    /** A district is drawn from its picture (DistrictPicture) on a far node where it is at least this many of the node's pixels across: 4 (the study's 8.2) - a node's pixel is shown 1 to 2 screen pixels wide (MapFrame.nodePlots()), so 4 to 8 on screen (star MM1-4); under it its pixels are dithered by its shares (DistrictPicture.shares()). */
    public static final int PICTURE_FROM_PX = 4;

    /** The city's edge over a far node's pixels: white at 0.55, the view's edge's (MapView's EDGE_ALPHA, since 0.7.61), on the inner ring of the pixels drawn as the city's and on any holding ground of it smaller than a pixel - at least a pixel wide at every scale, where the view's own edge, runs along the block lines, drops a run under a pixel long. */
    public static final int EDGE = 0xffffffff;

    /** ...its white's alpha: 0.55, the view's own edge's since 0.7.61 - ui/MapView's EDGE_ALPHA reads this one since 0.7.128, so the two are one dial. */
    public static final double EDGE_ALPHA = 0.55;

    /** The districts the last nodePixels() drew at PICTURE_FROM_PX or more with no current picture: what the view asks the pictures of (CityMap.pictureJobs()). */
    private final java.util.Set<CityMap.District> wanted = new java.util.LinkedHashSet<>();

    /** Which of the last nodePixels()'s pixels it drew as the city's edge, and as the far skeleton's - a highway, the railway, a yard or a sea terminal's dot: a harness's. */
    private boolean[] lastEdge = new boolean[0], lastRun = new boolean[0];

    public java.util.Set<CityMap.District> wanted() { return wanted; }
    public boolean[] lastEdge() { return lastEdge; }
    public boolean[] lastRun() { return lastRun; }

    /**
     * A far node's pixels from its ground (0.7.128, batch MM1; spec-globe
     * 8.2): each sample's terrain colour, dimmed where the city does not own
     * it (TileRaster's UNOWNED_DIM); over the city's districts what stands
     * there - where a district is PICTURE_FROM_PX of the node's pixels across
     * or more, its picture's pixel under the sample (a GROUND pixel the
     * node's own ground), and where it is smaller, or its picture is not
     * drawn yet, its owned dry ground dithered by its shares of each kind
     * (DistrictPicture.shares(), a fixed hash a pixel); then the city's
     * highways and railway (CityRuns' stretches) on every pixel they cross -
     * since 0.8.1 the highways lightened (FAR_HIGHWAY_LIGHTEN), the railway
     * RAIL_DARK, and each yard and sea terminal a 2 x 2 dot in TRANSPORT's
     * fill (dot()) - and its edge - the inner ring of the pixels drawn as
     * its (their samples owned), and a pixel wherever it holds ground
     * smaller than one - so the
     * city keeps its shape however small. On Earth (0.7.119) past the planet's
     * rim - the antipode, EarthProj.REACH_M from the spot - the void
     * (spec-earth 2: "beyond it is drawn as the void"), which the ground
     * there reads as sea. Returns how many districts it drew without the
     * picture it would sample (wanted()); a district one
     * tint, until 0.7.127.
     */
    public int nodePixels(long x0, long y0, long plots, byte[] ground, int[] out) {
        return nodePixels(x0, y0, plots, ground, null, out);
    }

    /**
     * ...with the node's relief (nodeTerrain()'s, ET1 slice D): each pixel's ground in TileRaster.colour()'s
     * - the three greys on open dry ground, forest shaded toward them - where `relief` is not null; null, and a
     * generated world's (all FLAT), are the pixels as they were before.
     */
    public int nodePixels(long x0, long y0, long plots, byte[] ground, byte[] relief, int[] out) {
        CityLand land = map.land();
        LandGrid grid = land == null ? null : land.grid();
        int n = MapFrame.NODE_PX, w = n + 2;
        boolean earth = map.worldKey().earth();
        boolean pictures = World.DISTRICT / plots >= PICTURE_FROM_PX;
        int level = Long.numberOfTrailingZeros(plots);
        boolean aligned = Long.bitCount(plots) == 1 && Math.floorMod(x0, plots) == 0 && Math.floorMod(y0, plots) == 0;
        wanted.clear();
        // The city's ground a pixel at a time, a ring of pixels round the node among them: how much of the pixel it owns (NONE,
        // SOME or ALL), and whether it owns the pixel's sample - the pixel drawn as its.
        byte[] cover = new byte[w * w];
        boolean[] city = new boolean[w * w];
        for (int j = -1; j <= n; j++) {
            for (int i = -1; i <= n; i++) {
                int cv = LandGrid.NONE, at = (j + 1) * w + i + 1;
                if (grid != null) {
                    long ax = x0 + i * plots, ay = y0 + j * plots;
                    if (aligned) cv = grid.cover(level, Math.floorDiv(ax, plots), Math.floorDiv(ay, plots));
                    else {
                        long o = grid.owned(ax, ay, ax + plots, ay + plots);
                        cv = o == 0 ? LandGrid.NONE : o == plots * plots ? LandGrid.ALL : LandGrid.SOME;
                    }
                }
                cover[at] = (byte) cv;
                city[at] = cv == LandGrid.ALL || (cv == LandGrid.SOME && land.ownsPlot(x0 + i * plots + plots / 2, y0 + j * plots + plots / 2));
            }
        }
        Map<CityMap.District, DistrictPicture> pics = new java.util.HashMap<>();
        Map<CityMap.District, double[]> shares = new java.util.HashMap<>();
        BuildingVisual.Type[] types = map.types();
        long lastKey = Long.MIN_VALUE;
        CityMap.District d = null;
        long dx0 = 0, dy0 = 0;
        for (int j = 0; j < n; j++) {
            long y = y0 + j * plots + plots / 2;
            for (int i = 0; i < n; i++) {
                long x = x0 + i * plots + plots / 2;
                int k = j * n + i;
                if (earth && !EarthProj.onPlanet(EarthProj.metresX(x0 + (i + 0.5) * plots), EarthProj.metresY(y0 + (j + 0.5) * plots))) {
                    out[k] = TileRaster.VOID;
                    continue;
                }
                byte g = ground[k];
                int c = relief == null ? TileRaster.GROUND[g] : TileRaster.colour(g, relief[k]);
                int cv = cover[(j + 1) * w + i + 1];
                boolean owned = city[(j + 1) * w + i + 1];
                if (!owned) c = TileRaster.blend(c, TileRaster.VOID, TileRaster.UNOWNED_DIM);
                if (cv != LandGrid.NONE) {
                    long ddx = Math.floorDiv(x, World.DISTRICT), ddy = Math.floorDiv(y, World.DISTRICT), key = (ddx << 32) ^ (ddy & 0xffffffffL);
                    if (key != lastKey) {
                        lastKey = key;
                        d = map.districtOfTile(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
                        dx0 = ddx * World.DISTRICT;
                        dy0 = ddy * World.DISTRICT;
                    }
                    if (d != null && d.usedCells() > 0) {
                        int code = DistrictPicture.GROUND;
                        DistrictPicture pic = null;
                        if (pictures) {
                            pic = pics.get(d);
                            if (pic == null && !pics.containsKey(d)) {
                                pic = map.pictureIfCurrent(d);
                                pics.put(d, pic);
                                if (pic == null) wanted.add(d);
                            }
                        }
                        if (pic != null) code = pic.codeAtPlot((int) (x - dx0), (int) (y - dy0));
                        else if (owned && (relief == null ? World.dry(g) : World.buildable(g, relief[k]))) {
                            double[] s = shares.get(d);
                            if (s == null) {
                                s = DistrictPicture.shares(d, types);
                                shares.put(d, s);
                            }
                            code = DistrictPicture.pick(s, World.unit(World.mix(World.mix(map.seed() ^ 0x444954484552L ^ level
                                    ^ Math.floorDiv(x, plots) * 0x9E3779B97F4A7C15L) ^ Math.floorDiv(y, plots) * 0xC2B2AE3D27D4EB4FL)));
                        }
                        if (code != DistrictPicture.GROUND) c = DistrictPicture.colour(code);
                    }
                }
                out[k] = c;
            }
        }
        // The city's highways and railway, a pixel wide at least wherever they run, and (0.8.1) its yards' and sea terminals' dots.
        if (lastRun.length != n * n) { lastRun = new boolean[n * n]; lastEdge = new boolean[n * n]; }
        java.util.Arrays.fill(lastRun, false);
        java.util.Arrays.fill(lastEdge, false);
        CityRuns runs = map.runs();
        if (runs != null) {
            int highway = TileRaster.blend(TileRaster.ROAD[BuildingVisual.HIGHWAY], 0xffffffff, FAR_HIGHWAY_LIGHTEN);
            // The highways only once they reach a street (CityRuns.highwaysReach(), FX2), as the near view draws them.
            if (runs.highwaysReach()) stroke(runs.highways(), highway, x0, y0, plots, out);
            stroke(runs.rail(), TileRaster.RAIL_DARK, x0, y0, plots, out);
            for (long[] yard : runs.yards()) dot(yard[0] + yard[2] / 2.0, yard[1] + yard[3] / 2.0, x0, y0, plots,
                    BuildingVisual.FILL[BuildingVisual.TRANSPORT], out);
            for (CityShore.Work work : map.shore().works()) {
                if (work.quay() > 0) dot(work.x0() + work.w() / 2.0, work.y0() + work.h() / 2.0, x0, y0, plots,
                        BuildingVisual.FILL[BuildingVisual.TRANSPORT], out);
            }
        }
        // The city's edge: each of its pixels beside one that is not - the inner ring of its shape as drawn - and each pixel
        // holding some of its ground with none of the eight about it its own: ground smaller than a pixel, drawn a pixel.
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                int at = (j + 1) * w + i + 1;
                boolean edge = city[at] ? !city[at - 1] || !city[at + 1] || !city[at - w] || !city[at + w]
                        : cover[at] == LandGrid.SOME && !city[at - 1] && !city[at + 1] && !city[at - w] && !city[at + w]
                                && !city[at - w - 1] && !city[at - w + 1] && !city[at + w - 1] && !city[at + w + 1];
                if (edge) {
                    out[j * n + i] = TileRaster.blend(out[j * n + i], EDGE, EDGE_ALPHA);
                    lastEdge[j * n + i] = true;
                }
            }
        }
        return wanted.size();
    }

    /**
     * A net's stretches over a far node's pixels in a colour: every pixel
     * holding a plot of a stretch - each step's, from the plot it starts from
     * (its arm's start, or the last of the stretch before) to its last, and a
     * 45-degree step's corner (CityRuns.onStretch()) - so a run is at least a
     * pixel wide at every scale. Walked a pixel at a time, not a plot: from
     * one plot to the first that leaves its pixel's column or row.
     */
    private void stroke(CityRuns.Net net, int colour, long x0, long y0, long plots, int[] out) {
        int n = MapFrame.NODE_PX;
        long xa = x0 - 1, xb = x0 + n * plots, ya = y0 - 1, yb = y0 + n * plots;
        for (long[] s : net.stretches()) {
            int h = (int) s[2];
            long steps = s[3], sx = s[0], sy = s[1];
            int dx = CityRuns.DX[h], dy = CityRuns.DY[h];
            boolean corner = dx != 0 && dy != 0;
            // The steps whose plots lie within the node, a plot round it.
            long lo = 0, hi = steps;
            if (dx > 0) { lo = Math.max(lo, xa - sx); hi = Math.min(hi, xb - sx); }
            else if (dx < 0) { lo = Math.max(lo, sx - xb); hi = Math.min(hi, sx - xa); }
            else if (sx < xa || sx > xb) continue;
            if (dy > 0) { lo = Math.max(lo, ya - sy); hi = Math.min(hi, yb - sy + 1); }
            else if (dy < 0) { lo = Math.max(lo, sy - yb - 1); hi = Math.min(hi, sy - ya); }
            else if (sy < ya || sy > yb) continue;
            for (long k = lo; k <= hi; ) {
                long x = sx + (long) dx * k, y = sy + (long) dy * k;
                mark(x, y, x0, y0, plots, colour, out);
                if (corner && k >= 1) mark(x, y - dy, x0, y0, plots, colour, out);
                // The next step whose plot leaves this pixel's column or row.
                long next = Long.MAX_VALUE;
                long px = Math.floorDiv(x - x0, plots), py = Math.floorDiv(y - y0, plots);
                if (dx > 0) next = Math.min(next, x0 + (px + 1) * plots - x);
                else if (dx < 0) next = Math.min(next, x - (x0 + px * plots) + 1);
                if (dy > 0) next = Math.min(next, y0 + (py + 1) * plots - y);
                else if (dy < 0) next = Math.min(next, y - (y0 + py * plots) + 1);
                if (next == Long.MAX_VALUE) next = hi - k + 1;
                // A corner of a 45-degree step lies in the row before its plot's: the step that crosses a row is visited itself.
                k += Math.max(1, next);
            }
        }
    }

    /** Plot (x, y)'s pixel on the node, when it is on it, in a run's colour. */
    private void mark(long x, long y, long x0, long y0, long plots, int colour, int[] out) {
        int n = MapFrame.NODE_PX;
        long p = Math.floorDiv(x - x0, plots), q = Math.floorDiv(y - y0, plots);
        if (p < 0 || q < 0 || p >= n || q >= n) return;
        int k = (int) (q * n + p);
        out[k] = colour;
        lastRun[k] = true;
    }

    /** A yard's or a sea terminal's 2 x 2 pixel dot at its box's middle, in TRANSPORT's fill (spec-freight 4.8). */
    private void dot(double x, double y, long x0, long y0, long plots, int colour, int[] out) {
        int n = MapFrame.NODE_PX;
        long p = Math.floorDiv((long) Math.floor(x - x0), plots), q = Math.floorDiv((long) Math.floor(y - y0), plots);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 2; i++) {
                long pp = p + i, qq = q + j;
                if (pp < 0 || qq < 0 || pp >= n || qq >= n) continue;
                int k = (int) (qq * n + pp);
                out[k] = colour;
                lastRun[k] = true;
            }
        }
    }

    /**
     * Whether a far view asks for the pictures of the built districts it
     * shows (0.7.128): while they are no more than CityMap.PICTURES_KEPT and
     * lie in no more of the map's bands than it keeps the links of
     * (CityMap.BANDS_KEPT), so what it asks for is kept while it is shown;
     * past either - a city of thousands of districts seen wide - every
     * district it shows is dithered by its shares (star MM1-2).
     */
    public boolean picturesFit(MapFrame f) {
        double vx0 = f.plotX(0), vy0 = f.plotY(0), vx1 = f.plotX(f.width()), vy1 = f.plotY(f.height());
        int built = 0;
        java.util.Set<Integer> bands = new java.util.HashSet<>();
        for (CityMap.District d : map.districts()) {
            if (d.usedCells() <= 0) continue;
            double ax = (map.baseDX() + d.dx) * (double) World.DISTRICT, ay = (map.baseDY() + d.dy) * (double) World.DISTRICT;
            if (ax + World.DISTRICT <= vx0 || ay + World.DISTRICT <= vy0 || ax >= vx1 || ay >= vy1) continue;
            built++;
            bands.add(CityMap.bandOf(d));
            if (built > CityMap.PICTURES_KEPT || bands.size() > CityMap.BANDS_KEPT) return false;
        }
        return true;
    }

    /* ----- a far node kept: redrawn only where it moved (batch UP3) ----- */

    /** KeptNode's district index at a pixel with no district (no city ground there, or ground no district holds), and at one past the planet's rim on Earth, which nodePixels() draws the void. */
    private static final int NO_DISTRICT = -1, VOID_PIXEL = -2;

    /** nodeKey()'s fixed marks: a district whose picture is not current, the land's key where the city has no land, the runs' where it has none. */
    private static final long NO_PICTURE = 0x4E4F504943L, NO_LAND = 0x4C414E44L, NO_RUNS = 0x4E4F52554E53L;

    /**
     * What a far node was last drawn from and its layers, for one node (batch
     * UP3, docs/queue/UP3.md; the minimap's stamp is one - the expanded map's
     * far view has too many nodes to keep layers for, and asks nodeKey()
     * instead): nodePixels(KeptNode, ...) redraws only what moved since, and
     * composes the same pixels nodePixels() would. It is called with the same
     * x0, y0, plots and ground (the same array) from call to call; another
     * redraws everything. The counters are what a harness reads.
     *
     * Three layers, each redone when its key moved. The LAND: the city's
     * cover and ownership a pixel at a time, which district each pixel lies in
     * (the node's districts listed in the order nodePixels() first meets them)
     * and the city's edge - redone when what is owned under the node and its
     * ring of a pixel round it changes, or a district is added. The DISTRICTS:
     * each pixel's colour before the runs and the edge, repainted only for the
     * districts whose version, built plots, owned plots or picture moved. The
     * RUNS: the highways, railway, yards and sea terminals' dots a pixel at a
     * time - redone when the runs or the shore's works change.
     */
    public static final class KeptNode {
        private CityMap map;
        private CityLand land;
        private LandGrid grid;
        private long x0, y0, plots, landKey, runsKey;
        private byte[] ground, relief;
        private boolean runsKept;
        /** The land layer: the city's cover of each pixel and a ring about the node, whether it owns the pixel's sample, whether the pixel is its edge, and each pixel's district (an index into districts, or NO_DISTRICT, or VOID_PIXEL). Null before the first call. */
        private byte[] cover;
        private boolean[] city, edge;
        private int[] district;
        private CityMap.District[] districts = new CityMap.District[0];
        /** The district layer: each pixel's colour before the runs and the edge, and each district's key as it was drawn. */
        private int[] base;
        private long[] dVersion, dUsed;
        private int[] dOwned;
        private DistrictPicture[] dPic;
        /** The runs layer: the colour of each pixel the runs cross, and which they cross. */
        private int[] runColour;
        private boolean[] runMask;
        private long lands, runsDrawn, repainted, calls;

        /** Times its land layer was drawn. */
        public long lands() { return lands; }
        /** Times its runs layer was drawn. */
        public long runsDrawn() { return runsDrawn; }
        /** Districts whose pixels were repainted, summed over the calls. */
        public long districtsRepainted() { return repainted; }
        /** Times nodePixels() was called with it. */
        public long calls() { return calls; }
        /** How many districts it lists now: those it met at a pixel of the city's ground. */
        public int districts() { return districts.length; }
    }

    /**
     * A far node's pixels as nodePixels(x0, y0, plots, ground, out) gives
     * them - `out`, wanted(), lastEdge(), lastRun() and the return all equal -
     * redoing only what moved since `keep` was last drawn (batch UP3): the land
     * layer if its key moved (or keep is new, or for another node), the
     * districts' keys and the repaint of those that moved, the runs layer if
     * its key moved; then `out` composed from them every call, the order
     * nodePixels() writes them in - the district pass's colour, the runs over
     * it, the edge blended over both. It is an optimisation and not a new look:
     * MapCheck 18 holds it equal to nodePixels(), which stays as it was.
     */
    public int nodePixels(KeptNode keep, long x0, long y0, long plots, byte[] ground, int[] out) {
        return nodePixels(keep, x0, y0, plots, ground, null, out);
    }

    /** ...with the node's relief (nodeTerrain()'s; null as before), equal to nodePixels(x0, y0, plots, ground, relief, out) as the plain one is. */
    public int nodePixels(KeptNode keep, long x0, long y0, long plots, byte[] ground, byte[] relief, int[] out) {
        int n = MapFrame.NODE_PX;
        CityLand land = map.land();
        LandGrid grid = land == null ? null : land.grid();
        boolean pictures = World.DISTRICT / plots >= PICTURE_FROM_PX;
        keep.calls++;
        boolean fresh = keep.district == null || keep.map != map || keep.x0 != x0 || keep.y0 != y0 || keep.plots != plots || keep.ground != ground || keep.relief != relief;
        long landKey = landKey(x0, y0, plots);
        boolean landMoved = fresh || keep.land != land || keep.grid != grid || keep.landKey != landKey;
        if (fresh) {
            keep.map = map;
            keep.x0 = x0;
            keep.y0 = y0;
            keep.plots = plots;
            keep.ground = ground;
            keep.relief = relief;
            keep.runsKept = false;
        }
        if (landMoved) {
            drawLand(keep, land, grid);
            keep.land = land;
            keep.grid = grid;
            keep.landKey = landKey;
            keep.lands++;
        }
        // The districts: each one's key as it stands, against the key it was last drawn at; wanted() in the order nodePixels() meets them.
        CityMap.District[] ds = keep.districts;
        boolean[] dirty = new boolean[ds.length];
        boolean any = landMoved;
        wanted.clear();
        for (int q = 0; q < ds.length; q++) {
            CityMap.District d = ds[q];
            long used = d.usedCells();
            DistrictPicture pic = null;
            if (pictures && used > 0) {
                pic = map.pictureIfCurrent(d);
                if (pic == null) wanted.add(d);
            }
            if (landMoved || d.version != keep.dVersion[q] || used != keep.dUsed[q] || d.owned() != keep.dOwned[q] || pic != keep.dPic[q]) {
                dirty[q] = true;
                any = true;
                keep.repainted++;
                keep.dVersion[q] = d.version;
                keep.dUsed[q] = used;
                keep.dOwned[q] = d.owned();
                keep.dPic[q] = pic;
            }
        }
        if (any) repaint(keep, dirty, landMoved);
        if (lastRun.length != n * n) { lastRun = new boolean[n * n]; lastEdge = new boolean[n * n]; }
        long runsKey = runsKey();
        if (!keep.runsKept || keep.runsKey != runsKey) {
            drawRuns(keep);
            keep.runsKey = runsKey;
            keep.runsKept = true;
            keep.runsDrawn++;
        }
        // Composed in the order nodePixels() writes: the district pass, the runs over it, the edge over both.
        System.arraycopy(keep.runMask, 0, lastRun, 0, n * n);
        System.arraycopy(keep.edge, 0, lastEdge, 0, n * n);
        for (int k = 0; k < n * n; k++) {
            int c = keep.base[k];
            if (keep.runMask[k]) c = keep.runColour[k];
            if (keep.edge[k]) c = TileRaster.blend(c, EDGE, EDGE_ALPHA);
            out[k] = c;
        }
        return wanted.size();
    }

    /**
     * The land layer of a kept node: nodePixels()'s first loop (the city's
     * cover a pixel at a time and a ring round the node, and whether it owns
     * each pixel's sample) and last (the edge), and which district each pixel
     * lies in, met in nodePixels()'s order - every district at a pixel of the
     * city's ground, built or not, the pixels past the Earth's rim none of
     * them. Its sizes are allocated on the first draw. The expressions are
     * nodePixels()'s: the two must move together (MapCheck 18 holds them equal).
     */
    private void drawLand(KeptNode keep, CityLand land, LandGrid grid) {
        int n = MapFrame.NODE_PX, w = n + 2;
        long x0 = keep.x0, y0 = keep.y0, plots = keep.plots;
        boolean earth = map.worldKey().earth();
        int level = Long.numberOfTrailingZeros(plots);
        boolean aligned = Long.bitCount(plots) == 1 && Math.floorMod(x0, plots) == 0 && Math.floorMod(y0, plots) == 0;
        if (keep.district == null) {
            keep.cover = new byte[w * w];
            keep.city = new boolean[w * w];
            keep.edge = new boolean[n * n];
            keep.district = new int[n * n];
            keep.base = new int[n * n];
            keep.runColour = new int[n * n];
            keep.runMask = new boolean[n * n];
        }
        byte[] cover = keep.cover;
        boolean[] city = keep.city;
        for (int j = -1; j <= n; j++) {
            for (int i = -1; i <= n; i++) {
                int cv = LandGrid.NONE, at = (j + 1) * w + i + 1;
                if (grid != null) {
                    long ax = x0 + i * plots, ay = y0 + j * plots;
                    if (aligned) cv = grid.cover(level, Math.floorDiv(ax, plots), Math.floorDiv(ay, plots));
                    else {
                        long o = grid.owned(ax, ay, ax + plots, ay + plots);
                        cv = o == 0 ? LandGrid.NONE : o == plots * plots ? LandGrid.ALL : LandGrid.SOME;
                    }
                }
                cover[at] = (byte) cv;
                city[at] = cv == LandGrid.ALL || (cv == LandGrid.SOME && land.ownsPlot(x0 + i * plots + plots / 2, y0 + j * plots + plots / 2));
            }
        }
        // Which district each pixel lies in: nodePixels()'s second loop's lookups, the districts listed as it first meets them.
        java.util.List<CityMap.District> met = new java.util.ArrayList<>();
        Map<CityMap.District, Integer> index = new java.util.HashMap<>();
        long lastKey = Long.MIN_VALUE;
        int di = NO_DISTRICT;
        for (int j = 0; j < n; j++) {
            long y = y0 + j * plots + plots / 2;
            for (int i = 0; i < n; i++) {
                long x = x0 + i * plots + plots / 2;
                int k = j * n + i;
                if (earth && !EarthProj.onPlanet(EarthProj.metresX(x0 + (i + 0.5) * plots), EarthProj.metresY(y0 + (j + 0.5) * plots))) {
                    keep.district[k] = VOID_PIXEL;
                    continue;
                }
                int cv = cover[(j + 1) * w + i + 1];
                if (cv == LandGrid.NONE) {
                    keep.district[k] = NO_DISTRICT;
                    continue;
                }
                long ddx = Math.floorDiv(x, World.DISTRICT), ddy = Math.floorDiv(y, World.DISTRICT), key = (ddx << 32) ^ (ddy & 0xffffffffL);
                if (key != lastKey) {
                    lastKey = key;
                    CityMap.District d = map.districtOfTile(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
                    if (d == null) di = NO_DISTRICT;
                    else {
                        Integer q = index.get(d);
                        if (q == null) {
                            q = met.size();
                            index.put(d, q);
                            met.add(d);
                        }
                        di = q;
                    }
                }
                keep.district[k] = di;
            }
        }
        keep.districts = met.toArray(new CityMap.District[0]);
        int nd = keep.districts.length;
        keep.dVersion = new long[nd];
        keep.dUsed = new long[nd];
        keep.dOwned = new int[nd];
        keep.dPic = new DistrictPicture[nd];
        // The city's edge, nodePixels()'s last loop's test.
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                int a = (j + 1) * w + i + 1;
                keep.edge[j * n + i] = city[a] ? !city[a - 1] || !city[a + 1] || !city[a - w] || !city[a + w]
                        : cover[a] == LandGrid.SOME && !city[a - 1] && !city[a + 1] && !city[a - w] && !city[a + w]
                                && !city[a - w - 1] && !city[a - w + 1] && !city[a + w - 1] && !city[a + w + 1];
            }
        }
    }

    /**
     * The district layer of a kept node: each pixel's colour before the runs
     * and the edge - the terrain's, dimmed where the city does not own it, a
     * built district's picture's pixel or its shares' dither over it - for the
     * pixels of the districts marked dirty, or every pixel (`all`, after a
     * land redraw). The colour rule is nodePixels()'s, expression for
     * expression, and the two must move together (MapCheck 18 holds them equal).
     */
    private void repaint(KeptNode keep, boolean[] dirty, boolean all) {
        int n = MapFrame.NODE_PX, w = n + 2;
        long x0 = keep.x0, y0 = keep.y0, plots = keep.plots;
        int level = Long.numberOfTrailingZeros(plots);
        BuildingVisual.Type[] types = map.types();
        double[][] shares = new double[dirty.length][];
        for (int j = 0; j < n; j++) {
            long y = y0 + j * plots + plots / 2;
            for (int i = 0; i < n; i++) {
                int k = j * n + i, di = keep.district[k];
                if (di == VOID_PIXEL) {
                    if (all) keep.base[k] = TileRaster.VOID;
                    continue;
                }
                if (!all && !(di >= 0 && dirty[di])) continue;
                long x = x0 + i * plots + plots / 2;
                byte g = keep.ground[k];
                int c = keep.relief == null ? TileRaster.GROUND[g] : TileRaster.colour(g, keep.relief[k]);
                boolean owned = keep.city[(j + 1) * w + i + 1];
                if (!owned) c = TileRaster.blend(c, TileRaster.VOID, TileRaster.UNOWNED_DIM);
                if (di >= 0 && keep.dUsed[di] > 0) {
                    long dx0 = Math.floorDiv(x, World.DISTRICT) * World.DISTRICT, dy0 = Math.floorDiv(y, World.DISTRICT) * World.DISTRICT;
                    int code = DistrictPicture.GROUND;
                    DistrictPicture pic = keep.dPic[di];
                    if (pic != null) code = pic.codeAtPlot((int) (x - dx0), (int) (y - dy0));
                    else if (owned && (keep.relief == null ? World.dry(g) : World.buildable(g, keep.relief[k]))) {
                        double[] s = shares[di];
                        if (s == null) {
                            s = DistrictPicture.shares(keep.districts[di], types);
                            shares[di] = s;
                        }
                        code = DistrictPicture.pick(s, World.unit(World.mix(World.mix(map.seed() ^ 0x444954484552L ^ level
                                ^ Math.floorDiv(x, plots) * 0x9E3779B97F4A7C15L) ^ Math.floorDiv(y, plots) * 0xC2B2AE3D27D4EB4FL)));
                    }
                    if (code != DistrictPicture.GROUND) c = DistrictPicture.colour(code);
                }
                keep.base[k] = c;
            }
        }
    }

    /** The runs layer of a kept node: nodePixels()'s runs block, drawn into the node's own colours and its mask the pixels they cross (lastRun after it). */
    private void drawRuns(KeptNode keep) {
        int n = MapFrame.NODE_PX;
        long x0 = keep.x0, y0 = keep.y0, plots = keep.plots;
        int[] out = keep.runColour;
        java.util.Arrays.fill(lastRun, false);
        CityRuns runs = map.runs();
        if (runs != null) {
            int highway = TileRaster.blend(TileRaster.ROAD[BuildingVisual.HIGHWAY], 0xffffffff, FAR_HIGHWAY_LIGHTEN);
            if (runs.highwaysReach()) stroke(runs.highways(), highway, x0, y0, plots, out);
            stroke(runs.rail(), TileRaster.RAIL_DARK, x0, y0, plots, out);
            for (long[] yard : runs.yards()) dot(yard[0] + yard[2] / 2.0, yard[1] + yard[3] / 2.0, x0, y0, plots,
                    BuildingVisual.FILL[BuildingVisual.TRANSPORT], out);
            for (CityShore.Work work : map.shore().works()) {
                if (work.quay() > 0) dot(work.x0() + work.w() / 2.0, work.y0() + work.h() / 2.0, x0, y0, plots,
                        BuildingVisual.FILL[BuildingVisual.TRANSPORT], out);
            }
        }
        System.arraycopy(lastRun, 0, keep.runMask, 0, n * n);
    }

    /**
     * The land key of the node at (x0, y0) of `plots` a pixel: how many plots
     * the city owns in its box and the ring of a pixel round it - [x0 - plots,
     * x0 + (NODE_PX + 1) plots) each way - folded with the map's district
     * count (a new district changes districtOfTile()). Ownership only grows
     * (as CityMap.ownStamp() argues: a restatement draws the map again), so
     * the count moves whenever what is owned there does. One LandGrid.owned()
     * over a box: a walk of the grid's blocks the box cuts, not a pixel's
     * worth of queries. A fixed key where the city has no land.
     */
    private long landKey(long x0, long y0, long plots) {
        CityLand land = map.land();
        LandGrid grid = land == null ? null : land.grid();
        if (grid == null) return NO_LAND;
        int n = MapFrame.NODE_PX;
        long owned = grid.owned(x0 - plots, y0 - plots, x0 + (n + 1) * plots, y0 + (n + 1) * plots);
        return World.mix(World.mix(NO_LAND ^ owned) ^ map.districts().size());
    }

    /** The runs key: whether the city has runs, their version (CityRuns' stamp of everything laid), whether the highways reach a street, and the shore's works - each one's box and whether it has a quay - in order. What nodePixels()'s runs block draws from. */
    private long runsKey() {
        CityRuns runs = map.runs();
        if (runs == null) return NO_RUNS;
        long h = World.mix(NO_RUNS ^ runs.version());
        h = World.mix(h ^ (runs.highwaysReach() ? 1L : 0L));
        for (CityShore.Work work : map.shore().works()) {
            h = World.mix(h ^ work.x0() * 0x9E3779B97F4A7C15L ^ work.y0() ^ ((long) work.w() << 40) ^ ((long) work.h() << 48)
                    ^ (work.quay() > 0 ? 1L << 62 : 0L));
        }
        return h;
    }

    /**
     * A hash of everything nodePixels() draws the node at (x0, y0) from but
     * its ground, which is the world's and fixed for the node (batch UP3; the
     * expanded map's far view keeps this and an image a node, redrawing a node
     * when its key moved): the land key, the runs key, and each district whose
     * square overlaps the node's box [x0, x0 + NODE_PX plots) each way - its
     * place, version, built plots, owned plots and, where the node draws
     * pictures, the stamp of its current picture (a fixed mark for none).
     * Equal keys, equal pixels. The districts' hashes are summed, so the key
     * does not depend on the order they are met in, and none is sorted or
     * listed: they are found by map.district() for each column and row the
     * box spans when that is no more lookups than the map has districts, else
     * by one pass over map.districts() (a node of 1,024 plots a pixel spans
     * 512 x 512 squares, the 5B copy has 15,000 districts). The cost is the
     * grid's one box count, the runs' shore works and those lookups - about
     * a thousand at 5B's 64 plots a pixel, against the 16,384 pixels
     * nodePixels() computes - plus pictureIfCurrent() for each built district
     * where the node draws pictures, as nodePixels() asks.
     */
    public long nodeKey(long x0, long y0, long plots) {
        boolean pictures = World.DISTRICT / plots >= PICTURE_FROM_PX;
        long span = MapFrame.NODE_PX * plots, bdx = map.baseDX(), bdy = map.baseDY();
        long fx0 = Math.floorDiv(x0, World.DISTRICT), fx1 = Math.floorDiv(x0 + span - 1, World.DISTRICT);
        long fy0 = Math.floorDiv(y0, World.DISTRICT), fy1 = Math.floorDiv(y0 + span - 1, World.DISTRICT);
        long sum = 0;
        if ((fx1 - fx0 + 1) * (fy1 - fy0 + 1) <= map.districts().size()) {
            for (long fy = fy0; fy <= fy1; fy++) {
                for (long fx = fx0; fx <= fx1; fx++) {
                    CityMap.District d = map.district((int) (fx - bdx), (int) (fy - bdy));
                    if (d != null) sum += districtKey(d, pictures);
                }
            }
        } else {
            for (CityMap.District d : map.districts()) {
                long fx = bdx + d.dx, fy = bdy + d.dy;
                if (fx >= fx0 && fx <= fx1 && fy >= fy0 && fy <= fy1) sum += districtKey(d, pictures);
            }
        }
        long h = World.mix(landKey(x0, y0, plots) ^ 0x4B4559L);
        h = World.mix(h ^ runsKey());
        return World.mix(h ^ sum);
    }

    /** One district's part of nodeKey(): its place, version, built and owned plots, and (`pictures`) its current picture's stamp or NO_PICTURE. */
    private long districtKey(CityMap.District d, boolean pictures) {
        long used = d.usedCells();
        long h = World.mix(((long) d.dx << 32) ^ (d.dy & 0xffffffffL) ^ 0x44495354L);
        h = World.mix(h ^ d.version);
        h = World.mix(h ^ used * 0x9E3779B97F4A7C15L ^ d.owned());
        if (pictures && used > 0) {
            DistrictPicture pic = map.pictureIfCurrent(d);
            h = World.mix(h ^ (pic == null ? NO_PICTURE : pic.stamp));
        }
        return h;
    }
}
