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
     * those just beyond its edges, the city's highways and track through it,
     * its buildings' boxes (0.7.88; the counts and road plots dealt to it, its
     * neighbours' roads and the deal's plan before), its sites with their
     * states and mines, and (0.8.1) the freight station's place, whether the
     * railway carries goods and on how many of a yard's tracks wagons stand,
     * and (FX5) the railway's bends that reach it with the plots they replace.
     * Two tiles with one stamp paint the same pixels.
     */
    public static long stamp(TilePainter.Input in) {
        long h = World.mix(in.seed ^ World.mix(in.tx * 0x9E3779B97F4A7C15L ^ in.ty));
        h = World.mix(h ^ packed(in.owned));
        h = World.mix(h ^ packed(in.street));
        h = World.mix(h ^ packed(in.beyond));
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
        // The interchanges' reserves that reach it (batch FR7) - nothing with none.
        for (int r = 0; r < in.reserves * TilePainter.RESERVE_INTS; r++) h = World.mix(h ^ in.reserve[r] * 0x9E3779B97F4A7C15L ^ r);
        // The railway's bends that reach it and the plots they replace (FX5, TrackCurves) - nothing with none: a tile repaints when its bends change.
        for (int k = 0; k < in.bends * TilePainter.Input.BEND_DOUBLES; k++) h = World.mix(h ^ Double.doubleToLongBits(in.bend[k]) * 31 ^ k);
        long curved = packed(in.curved);
        if (in.bends > 0 || curved != 0) h = World.mix(h ^ curved ^ in.bends);
        // The sidings its district's plan laid over it (batch FR7 slice 5) - nothing with none.
        long sidings = packed(in.siding);
        if (sidings != 0) h = World.mix(h ^ sidings);
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
        System.arraycopy(a.owned, 0, b.owned, 0, TilePainter.PLOTS);
        System.arraycopy(a.street, 0, b.street, 0, TilePainter.PLOTS);
        System.arraycopy(a.beyond, 0, b.beyond, 0, a.beyond.length);
        System.arraycopy(a.fixed, 0, b.fixed, 0, TilePainter.PLOTS);
        System.arraycopy(a.fixedBeyond, 0, b.fixedBeyond, 0, a.fixedBeyond.length);
        // ...and the runs' marks, the ramps and 45-degree stretches (0.7.98, batch O14: until then the kept copy the raster
        // reads had none, so no ramp or smooth band was ever drawn).
        System.arraycopy(a.marks, 0, b.marks, 0, TilePainter.PLOTS);
        System.arraycopy(a.siding, 0, b.siding, 0, TilePainter.PLOTS);
        System.arraycopy(a.marksBeyond, 0, b.marksBeyond, 0, a.marksBeyond.length);
        b.stationX = a.stationX;
        b.stationY = a.stationY;
        b.goodsByRail = a.goodsByRail;
        b.wagonTracks = a.wagonTracks;
        b.types = a.types;
        if (b.counts.length != a.counts.length) b.counts = new int[a.counts.length];
        b.clearBuildings();
        for (int k = 0; k < a.buildings; k++) b.addBuilding(a.boxes[2 * k], a.boxes[2 * k + 1]);
        b.clearSites();
        for (int s = 0; s < a.sites; s++) {
            b.addSite(a.siteX0[s], a.siteY0[s], a.siteX1[s], a.siteY1[s], a.siteKind[s], a.siteState[s], a.siteMine[s], a.siteKey[s]);
        }
        // The railway's bends and the plots they replace (FX5).
        b.copyBends(a);
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

    /** A kept painted tile's bytes, from its arrays' lengths: what PAINTED_KEPT of them weigh - since 0.7.88 its plan's streets and runs and its buildings' boxes in, its plots' use, kind, width, role, flags, building and site out; since 0.7.97 each plot's work at sea both ways, and the rings' and pipes' ends in. */
    public static long paintedBytes(TilePainter.Input in, TilePainter.Painted p) {
        long input = 4L * TilePainter.PLOTS + 2L * in.beyond.length + 4L * (in.counts.length + in.boxes.length) + 16 + 4
                + 36L * in.siteX0.length + in.sea.length + 8L * (in.ringX.length + in.ringY.length) + 32L * in.pipeAX.length;
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
        byte[] out = new byte[MapFrame.NODE_PX * MapFrame.NODE_PX];
        World w = World.of(world);
        int stride = (int) Math.min(Integer.MAX_VALUE, plots);
        if (w.isEarth()) w.earthTerrain().farTerrain(x0, y0, stride, MapFrame.NODE_PX, out);
        else w.regionTerrain(x0, y0, stride, MapFrame.NODE_PX, out);
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
                int c = TileRaster.GROUND[g];
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
                        else if (owned && World.dry(g)) {
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
}
