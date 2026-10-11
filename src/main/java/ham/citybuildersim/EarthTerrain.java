package ham.citybuildersim;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The real Earth's ground on the world's plots: a tile, a plot, a line or a region at a time, each world cell's land and forest (the cells pass), the founding site snapped to dry ground, and a point's sea - spec-earth 3's detail below the data, drawn from earth-1 through EarthData and EarthProj.
 *
 * WHY THIS EXISTS (0.7.115, batch E2; the project's spec-earth.md 2, 3 and
 * 8; Jerus's answers of 2026-10-10, "all rec"). E1 built the reader of the
 * real Earth's data and the projection that lays it on the world's square;
 * nothing drew ground from them. The data is coarse: a 15" cell (463 m) of
 * sea and lake shares, a 2' cell (3.7 km) of land cover, so at a new city's
 * zoom it holds two or three samples, and a world drawn straight from it would
 * be squares. This draws the ground the world's way - a plot at a time, 30 m -
 * keeping the data's shares and making only the detail below them from the
 * seed, so the coasts, lakes and rivers are the real ones and the seed decides
 * where within a cell the sea's edge, a patch of forest or a beach falls. The
 * study's prototype of it (EarthWorld) was lost with the workspace on
 * 2026-10-09; this is built from the spec's words.
 *
 * THE RULE (spec-earth 3), a tile at a time (tileTerrain()):
 *   - the tile and a ring of one plot (34 x 34, the ring for the beaches)
 *     read their shares at a 10 x 10 lattice (LATTICE points LATTICE_STEP
 *     plots apart, on the world's own grid of them, so two tiles read a
 *     shared point alike), each point's latitude and longitude from the
 *     tile's four corners (EarthProj.blend()), each share interpolated
 *     between the data's cell centres, and blended between the points;
 *   - each class has its own value noise, octaves of 2^NOISE_HI plots (480 m)
 *     down to 2^NOISE_LO (60 m), each World.OCTAVE_FALLOFF of the one above;
 *     fresh water's, forest's and a small lake's are ranked through one table
 *     of the noise's distribution (QUANTILE_SAMPLES fixed samples), so each
 *     covers in expectation what the data gives it;
 *   - SEA where the sea share plus COAST_FRAY x the coast noise about its
 *     mean, unranked, reaches SEA_LEVEL: the data's half-contour, frayed by a
 *     plot or two;
 *   - FRESH where a river's centreline (GRWL) passes within half its width,
 *     at least RIVER_MIN_HALF; where a small lake (under 10 km2, a point and
 *     an area in the data) lies, drawn as a blob of its own area in
 *     expectation, its edge frayed by BLOB_FRAY of its own noise; or where the
 *     fresh noise ranks under the lake share of the ground that is not sea;
 *   - ICE (0.7.127, batch E7; spec-globe 3.9) where its 2' cell's snow and
 *     ice bit (EarthData.LC_ICE), read 0 or 1 at each cell and interpolated
 *     between the cells' centres as the shares are, plus COVER_FRAY x its
 *     own noise about its mean, unranked, reaches COVER_LEVEL: the bit's
 *     half-contour, frayed as the coast is - land, never dry (World.dry());
 *   - SAND where a dry plot has the sea on one of its eight sides and its 2'
 *     cell's bare ground is 2% of its land or more (EarthData.LC_BEACH);
 *   - FOREST where the forest noise ranks under the forest share of the dry
 *     ground;
 *   - DESERT (0.7.127) where the bare-ground bit (EarthData.LC_DESERT) passes
 *     the same test as ice's, with its own noise: dry ground every rule
 *     treats as grass, only its colour and name its own (spec-globe Q1);
 *   - GRASS the rest. Real land use is ignored: the city starts on
 *     unsettled ground.
 * A plot (terrainAt()) is its tile's, from a kept tile, and a line
 * (lineTerrain()) its tile's byte for byte, drawn alone where the tile is not
 * kept (draw()); a region at a stride (regionTerrain()) classes each pixel's
 * centre from the shares there and the octaves wider than a pixel, the
 * narrower at their mean - World's rule for its far view.
 *
 * THE FAR VIEW (0.7.119, batch E5; spec-earth 3, "Far views"). The map's far
 * nodes (MapTiles.nodeTerrain()) are farTerrain(): the region's pixels, but
 * the rivers stroked from their vectors at least a pixel wide and thinned
 * by width as the view widens (FAR_RIVER_SHARE), the small lakes drawn from
 * their points and areas, and from DITHER_STRIDE the forest dithered by a
 * hash - its vectors read from the 1-degree tiles under the node, either
 * side of the date line alike (nodeTiles()). MapCheck 10 holds it.
 *
 * THE WORLD'S OTHER HALF. The cells pass (cellsPass()) replaces the
 * generated world's sea pass: each world cell's land and forest from
 * CELL_SAMPLES x CELL_SAMPLES points of the 4' layers, which the fields of
 * ore and oil are drawn on as the generated world's are on its cells - since
 * 0.7.117 (batch E3) each point's land weighted by its province's geology
 * (Geology, cellFieldLand()), oil's with a basin's, shelf's or rift's sea. The
 * founding is the snap (spec-earth 2): from the spot, the projection's
 * centre, the nearest plot, ring by ring of one plot, that passes the
 * founding's test 1 (World.SITE_DRY_PLOTS); there is no founding river.
 *
 * WHICH DATA. A world's key names its data by identity (EarthData.identity(),
 * "earth-1:<hash>"); a harness registers its EarthData with use(), and the
 * game's file is found by EarthData.locate() the first time its identity is
 * asked for. Every value is a pure function of the key: the same key draws
 * the same ground on every machine (StrictMath, World's hashes).
 *
 * E2 built it unreachable from the founding screen; since 0.7.121 (batch E4)
 * the screen offers Earth, the snap is the founding card's first question
 * (siteNear()), and the sea has a depth (THE SEA'S DEPTH): a generated world
 * never reaches it. EarthTerrainCheck holds it on the three windows of
 * EarthCheck's fixture and, when it is named, on the full file;
 * EarthFoundingCheck the depth and the card.
 */
public final class EarthTerrain {

    /* =====================================================================
       THE DIALS
       ===================================================================== */

    /** The noise's widest octave, 2^4 plots (480 m): just over the data's 15" cell (463 m), so the detail drawn lives below what the data resolves (spec-earth 3). */
    public static final int NOISE_HI = 4;

    /** ...down to 2^1 plots (60 m), two plots, the finest a plot-centred noise can carry (spec-earth 3). */
    public static final int NOISE_LO = 1;

    /** The sea's share from which the coast is drawn, 0.5: the data's half-contour (spec-earth 3) - and what an Earth world gives as its sea's level (World.seaTheta()), having no heights. */
    public static final double SEA_LEVEL = 0.5;

    /**
     * How far the coast noise frays the coast: 1.0 (spec-earth 3's
     * COAST_FRAY) - sea where share + 1.0 x (noise - 0.5) reaches SEA_LEVEL,
     * the noise unranked, so the coast is the interpolated share's
     * half-contour moved by the noise's spread (its standard deviation 0.127,
     * EarthTerrainCheck's first line) against a share that crosses a 15" cell's 15 plots:
     * a plot or two. Ranked, the same 1.0 scattered a mixed cell's sea across
     * it and drew Rotterdam's cells 2.21 points off the data in the mean
     * against the study's 1.49; unranked, 1.34, and Perth 0.38 against 0.39.
     */
    public static final double COAST_FRAY = 1.0;

    /**
     * How far a small lake's edge is frayed: 1.0 (★E2, the coast's own rule
     * on a lake's disc) - a plot at distance d from a lake of radius r is in
     * it where (d/r)^2 + 1.0 x (rank - 0.5) is under 1, so the blob reaches
     * from 0.71 r to 1.22 r and covers pi r^2, the lake's own area, in
     * expectation (spec-earth 3: "seeded blobs of their own area").
     */
    public static final double BLOB_FRAY = 1.0;

    /** The bit's share from which a plot is ice, or desert (0.7.127, batch E7; spec-globe 3.9): 0.5, its half-contour - each kind's land-cover bit read 0 or 1 at its 2' cell and interpolated between the cells' centres, as the sea's share is between its cells'. */
    public static final double COVER_LEVEL = 0.5;

    /**
     * How far each kind's own noise frays the edge of ice and of the desert
     * (0.7.127, batch E7; spec-globe 3.9: "tested against 1/2 plus its own
     * noise, as the coast is"): COAST_FRAY, 1.0 - ice where the bit's share
     * + 1.0 x (noise - 0.5) reaches COVER_LEVEL, the noise unranked, so the
     * edge is the share's half-contour moved by the noise's spread.
     */
    public static final double COVER_FRAY = COAST_FRAY;

    /** The noise's samples the ranking table holds: 2^18 (spec-earth 3's QUANTILE), at fixed places, one table for every class and every world. */
    public static final int QUANTILE_SAMPLES = 1 << 18;

    /** Plots between the points a tile reads its shares at: 4 (120 m), so a tile's 34 x 34 region holds 10 x 10 of them on the world's own grid of them (spec-earth 3: a 10 x 10 lattice). */
    public static final int LATTICE_STEP = 4;

    /** Points a side of a tile's lattice: TILE / LATTICE_STEP + 2, 10 - the region's ring and its far edge. */
    public static final int LATTICE = World.TILE / LATTICE_STEP + 2;

    /** A river's least half-width, in plots: 0.75, so a 30 m river - half a plot each side of its line - leaves no gaps (spec-earth 3). */
    public static final double RIVER_MIN_HALF = 0.75;

    /** How far the founding snaps from the spot, in plots: 167 (5 km, the founding card's reach, spec-earth 2); a spot with no site within it has none, and E4's screen refuses it. */
    public static final int SNAP_PLOTS = 167;

    /** Points a side of a world cell the cells pass reads: 4, so 16 a cell of the 4' layers (spec-earth 4). */
    public static final int CELL_SAMPLES = 4;

    /** The stride, in plots, from which a region reads the 4' far layers instead of the 15" shares: 256 (7.68 km, a district), about a 4' cell's 7.4 km (★E2: wider pixels would inflate a 15" tile for every few). */
    public static final int FAR_STRIDE = 256;

    /** The stride under which a region draws rivers and small lakes: 16 plots (480 m), the noise's widest octave (★E2): past it a pixel is wider than any detail of the ground's own, and the far view's rivers are stroked from their vectors (E5). */
    public static final int WATER_STRIDE = 16;

    /**
     * How far round a district its rivers are looked for, in degrees of
     * latitude: 0.25 (27.8 km; ★E2) - a river is stored in every 1-degree
     * tile its centreline's box touches, so a district reads the tiles within
     * this of it too, for a river whose width reaches it from beyond; earth-1's
     * widest river, WIDEST_RIVER_M, reaches 20.3 km either side of its line.
     */
    public static final double WATER_PAD_DEG = 0.25;

    /** The widest river in earth-1, in metres: 40,650, measured over all its tiles (EarthTerrainCheck 9 holds it, and WATER_PAD_DEG to it, when the file is named). */
    public static final int WIDEST_RIVER_M = 40_650;

    /** Tiles of ground kept for terrainAt(), lineTerrain() and the snap: 1,024 (1 MB), the snap's 5 km disc and a screen's worth. */
    static final int TILES_KEPT = 1024;

    /** Districts' rivers and small lakes kept, projected: 256. */
    static final int DISTRICTS_KEPT = 256;

    /** 1-degree tiles' rivers and small lakes kept, projected: 64. */
    static final int DATA_TILES_KEPT = 64;

    /* The fields' keys: what makes one class's noise another's. */
    private static final long F_COAST = 0xC0A57L, F_FRESH = 0xF4E54L, F_FOREST = 0xF0E57L, F_BLOB = 0xB10BL, F_ICE = 0x1CE5L, F_DESERT = 0xDE5E7L;

    /** The stream the ranking table's samples are drawn from. */
    private static final long QUANTILE_SALT = 0x0A1E5A3DL;

    /** Each octave's amplitude over the sum of them, octave k at AMP_N[k]. */
    private static final double[] AMP_N = new double[NOISE_HI + 1];

    /** The smoothstep at a plot's centre within octave k's lattice cell, SMOOTH[k][f] for f = 0 .. 2^k - 1. */
    private static final double[][] SMOOTH = new double[NOISE_HI + 1][];

    static {
        double norm = World.norm(NOISE_HI, NOISE_LO);
        for (int k = NOISE_LO; k <= NOISE_HI; k++) AMP_N[k] = World.amp(NOISE_HI, k) / norm;
        for (int k = 0; k <= NOISE_HI; k++) {
            int w = 1 << k;
            SMOOTH[k] = new double[w];
            for (int f = 0; f < w; f++) {
                double t = (f + 0.5) / w;
                SMOOTH[k][f] = t * t * (3 - 2 * t);
            }
        }
    }

    /* =====================================================================
       THE DATA A KEY NAMES
       ===================================================================== */

    /** The readers use() has made available, by their identity: where source() looks for a key's data first. */
    private static final Map<String, EarthData> SOURCES = new HashMap<>();

    /** Makes a reader of Earth's data available to the worlds whose keys name it: returns its identity, the key's `data`. A harness's fixture, or the game's file. */
    public static String use(EarthData data) {
        String id = data.identity();
        synchronized (SOURCES) { SOURCES.put(id, data); }
        return id;
    }

    /** The reader a key's data names: one use() made available, else the file EarthData.locate() finds when its identity is that. */
    static EarthData source(String identity) {
        synchronized (SOURCES) {
            EarthData d = SOURCES.get(identity);
            if (d != null) return d;
            Path p = EarthData.locate();
            if (p != null && Files.isRegularFile(p)) {
                try {
                    EarthData f = EarthData.open(p);
                    if (f.identity().equals(identity)) {
                        SOURCES.put(identity, f);
                        return f;
                    }
                    f.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        throw new IllegalStateException("the Earth's data " + identity + " is not here: name it with -D" + EarthData.PROPERTY
                + "=<path> or put it at " + EarthData.DATA_FILE);
    }

    /* =====================================================================
       ONE EARTH WORLD'S STATE
       ===================================================================== */

    private final World.Key key;
    private final long seed;
    private final EarthData data;
    private final EarthProj proj;
    /** Each field's octave streams: COAST, FRESH, FOREST, BLOB, ICE, DESERT, octave k at [f][k]. */
    private final long[][] os = new long[6][NOISE_HI + 1];
    /** Each noise field's row in os: the coast's, fresh water's, the forest's, the small lakes' blobs', and (0.7.127) the ice's and the desert's edges'. */
    private static final int COAST = 0, FRESH = 1, FOREST = 2, BLOB = 3, ICE = 4, DESERT = 5;

    private volatile boolean built;
    private double[] cellLand, cellForest;
    /** Each resource's field land a cell, by Resource's ordinal (null for forest): cellFieldLand(). */
    private float[][] cellFields;
    private long siteX, siteY;
    private int siteTests;
    private double cellsMs;

    /** The ground of an Earth world's key; the data it names is found here (source()). */
    EarthTerrain(World.Key key) {
        if (!key.earth()) throw new IllegalArgumentException("not an Earth world: " + key);
        this.key = key;
        this.seed = key.seed();
        this.data = source(key.data());
        this.proj = new EarthProj(key.lat(), key.lon());
        long[] fields = { F_COAST, F_FRESH, F_FOREST, F_BLOB, F_ICE, F_DESERT };
        for (int f = 0; f < fields.length; f++) {
            for (int k = NOISE_LO; k <= NOISE_HI; k++) os[f][k] = World.mix(seed ^ fields[f] ^ (k * 0x632BE59BD9B4E019L));
        }
    }

    /** Its key. */
    public World.Key key() { return key; }

    /** The projection it is laid through, centred on the key's spot. */
    public EarthProj projection() { return proj; }

    /** The data it is drawn from. */
    public EarthData data() { return data; }

    /* =====================================================================
       THE RANKING TABLE (spec-earth 3's QUANTILE)
       ===================================================================== */

    private static volatile double[] quantile;

    /** The noise's distribution: QUANTILE_SAMPLES values of it at plot centres hashed from QUANTILE_SALT, sorted - built once, the same everywhere. */
    static double[] quantile() {
        double[] q = quantile;
        if (q != null) return q;
        synchronized (EarthTerrain.class) {
            if (quantile == null) {
                long[] s = new long[NOISE_HI + 1];
                for (int k = NOISE_LO; k <= NOISE_HI; k++) s[k] = World.mix(QUANTILE_SALT ^ (k * 0x632BE59BD9B4E019L));
                double[] v = new double[QUANTILE_SAMPLES];
                for (int i = 0; i < QUANTILE_SAMPLES; i++) {
                    long h = World.mix(QUANTILE_SALT + i * 0x9E3779B97F4A7C15L);
                    long x = h >>> 40, y = World.mix(h) >>> 40;
                    v[i] = noiseAt(s, x + 0.5, y + 0.5, 1);
                }
                Arrays.sort(v);
                quantile = v;
            }
            return quantile;
        }
    }

    /** The noise value under which share p of its samples lie: minus infinity at p of nought or less, plus infinity at one or more. */
    static double below(double p) {
        if (!(p > 0)) return Double.NEGATIVE_INFINITY;
        if (p >= 1) return Double.POSITIVE_INFINITY;
        return quantile()[(int) (p * QUANTILE_SAMPLES)];
    }

    /** ...from a table in hand. */
    private static double below(double[] q, double p) {
        if (!(p > 0)) return Double.NEGATIVE_INFINITY;
        if (p >= 1) return Double.POSITIVE_INFINITY;
        return q[(int) (p * QUANTILE_SAMPLES)];
    }

    /** Whether a plot is sea (spec-earth 3): its sea share plus COAST_FRAY x its coast noise about the noise's mean, 0.5, reaches SEA_LEVEL - the share's half-contour, frayed. */
    static boolean sea(double share, double coastNoise) {
        return share > 0 && share + COAST_FRAY * (coastNoise - 0.5) >= SEA_LEVEL;
    }

    /** Whether a plot is ice, or desert (0.7.127, batch E7; spec-globe 3.9): its kind's bit interpolated there plus COVER_FRAY x its own noise about the noise's mean reaches COVER_LEVEL - the bit's half-contour, frayed as the coast is. */
    static boolean covered(double share, double noise) {
        return share > 0 && share + COVER_FRAY * (noise - 0.5) >= COVER_LEVEL;
    }

    /** One field's noise at a point in plots, its octaves no wider than `stride` at their mean (World.regionTerrain()'s rule): at a stride of one, every octave. */
    static double noiseAt(long[] os, double x, double y, double stride) {
        double v = 0;
        for (int k = NOISE_HI; k >= NOISE_LO; k--) {
            double w = 1L << k;
            v += AMP_N[k] * (w > stride ? World.vnoise(os[k], x / w, y / w) : 0.5);
        }
        return v;
    }

    /* =====================================================================
       READING THE DATA: a layer's cells, a few tiles kept in hand
       ===================================================================== */

    /** One share layer read cell by cell, the last few of its tiles (or bands) in hand: one thread's. */
    private static final class Reader {
        EarthData data;
        EarthData.Layer layer;
        int cpd, cols;
        boolean tiles;
        final int[] ids = new int[4];
        final byte[][] bytes = new byte[4][];
        final int[] uniform = new int[4];
        int next;

        void bind(EarthData d, String name) {
            data = d;
            layer = d.layer(name);
            cpd = layer.cellsPerDegree;
            tiles = layer.kind == EarthData.KIND_TILES;
            cols = tiles ? cpd : 360 * cpd;
            Arrays.fill(ids, -1);
            Arrays.fill(bytes, null);
        }

        /** The value of the cell in global row r (from 90 N) and column c (from 180 W), rows clamped to the globe and columns wrapped round it. */
        int value(long r, long c) {
            long rows = 180L * cpd, all = 360L * cpd;
            if (r < 0) r = 0;
            else if (r >= rows) r = rows - 1;
            c = Math.floorMod(c, all);
            int band = (int) (179 - r / cpd);
            int id = tiles ? band * 360 + (int) (c / cpd) : band;
            int k = slot(id);
            byte[] b = bytes[k];
            if (b == null) return uniform[k];
            return b[(int) ((r % cpd) * cols + (tiles ? c % cpd : c))] & 0xFF;
        }

        private int slot(int id) {
            for (int k = 0; k < ids.length; k++) if (ids[k] == id) return k;
            int k = next;
            next = (next + 1) % ids.length;
            ids[k] = id;
            int e = layer.entries[id];
            if (e <= 0) {
                bytes[k] = null;
                uniform[k] = -e;
            } else {
                bytes[k] = data.raw(layer.name, id);
            }
            return k;
        }

        /** The layer's value at (lat, lon) interpolated between the four cell centres about it, 0 to 255 (bilinear; columns wrap at the date line). */
        double bilinear(double lat, double lon) {
            double g = (90 - lat) * cpd - 0.5, c = (lon + 180) * cpd - 0.5;
            long r0 = (long) Math.floor(g), c0 = (long) Math.floor(c);
            double fr = g - r0, fc = c - c0;
            double top = value(r0, c0) * (1 - fc) + value(r0, c0 + 1) * fc;
            double bottom = value(r0 + 1, c0) * (1 - fc) + value(r0 + 1, c0 + 1) * fc;
            return top * (1 - fr) + bottom * fr;
        }

        /**
         * ...land cover's: the forest level, 0 to 15, interpolated the same
         * way, and (0.7.127, batch E7) its ice and desert bits each read 0 or
         * 1 at its cell and interpolated from the same four cells: out[0] the
         * level, out[1] ice's share, out[2] the desert's. The level is the
         * arithmetic the forest was read with before (bilinearLow(), which
         * this replaced), so the forest drawn is the same to the bit.
         */
        void cover(double lat, double lon, double[] out) {
            double g = (90 - lat) * cpd - 0.5, c = (lon + 180) * cpd - 0.5;
            long r0 = (long) Math.floor(g), c0 = (long) Math.floor(c);
            double fr = g - r0, fc = c - c0;
            int m = EarthData.LC_FOREST;
            int a = value(r0, c0), b = value(r0, c0 + 1), d = value(r0 + 1, c0), e = value(r0 + 1, c0 + 1);
            double top = (a & m) * (1 - fc) + (b & m) * fc;
            double bottom = (d & m) * (1 - fc) + (e & m) * fc;
            out[0] = top * (1 - fr) + bottom * fr;
            int any = a | b | d | e;
            out[1] = (any & EarthData.LC_ICE) == 0 ? 0 : bit(a, b, d, e, EarthData.LC_ICE, fr, fc);
            out[2] = (any & EarthData.LC_DESERT) == 0 ? 0 : bit(a, b, d, e, EarthData.LC_DESERT, fr, fc);
        }

        /** A bit of four cells' values, each 0 or 1, interpolated between their centres. */
        private static double bit(int a, int b, int d, int e, int bit, double fr, double fc) {
            if ((a & b & d & e & bit) != 0) return 1;
            double top = ((a & bit) != 0 ? 1 - fc : 0) + ((b & bit) != 0 ? fc : 0);
            double bottom = ((d & bit) != 0 ? 1 - fc : 0) + ((e & bit) != 0 ? fc : 0);
            return top * (1 - fr) + bottom * fr;
        }

        /** The value of the cell (lat, lon) lies in: a cell holds its north and west edges (EarthData's WHERE A POINT FALLS). */
        int at(double lat, double lon) {
            return value((long) Math.floor((90 - lat) * cpd), (long) Math.floor((lon + 180) * cpd));
        }
    }

    /** One thread's working arrays and readers. */
    private static final class Scratch {
        /** A tile with its ring of one plot each side, RING plots a side (RING_AREA in all); a tile alone, AREA plots. */
        static final int RING = World.TILE + 2, RING_AREA = RING * RING, AREA = World.TILE * World.TILE;
        final double[] coast = new double[RING_AREA], fresh = new double[AREA], forest = new double[AREA], blob = new double[AREA];
        final double[] ice = new double[AREA], desert = new double[AREA];
        final double[] lat = new double[19 * 19];
        final double[] seaL = new double[LATTICE * LATTICE], lakeL = new double[LATTICE * LATTICE], forestL = new double[LATTICE * LATTICE];
        final double[] iceL = new double[LATTICE * LATTICE], desertL = new double[LATTICE * LATTICE];
        final boolean[] sea = new boolean[RING_AREA];
        final double[] ll = new double[2], cv = new double[3];
        int[] segs = new int[64], lakes = new int[16];
        final Reader seaR = new Reader(), lakeR = new Reader(), coverR = new Reader(), farLandR = new Reader(), farLakeR = new Reader();
        EarthData bound;

        void bind(EarthData d) {
            if (bound == d) return;
            seaR.bind(d, "sea");
            lakeR.bind(d, "lake");
            coverR.bind(d, "landcover");
            farLandR.bind(d, "far.land");
            farLakeR.bind(d, "far.lake");
            bound = d;
        }
    }

    /** Each thread's own working arrays and readers, so tiles drawn on several threads at once share none. */
    private static final ThreadLocal<Scratch> SCRATCH = ThreadLocal.withInitial(Scratch::new);

    private Scratch scratch() {
        Scratch s = SCRATCH.get();
        s.bind(data);
        return s;
    }

    /* =====================================================================
       A TILE, OR A LINE OF ONE
       ===================================================================== */

    /**
     * One tile's ground, a byte a plot in rows (out[py x 32 + px]): spec-earth
     * 3's rule (the class header). A tile off the world's square, or wholly
     * off the planet, is sea. 0.05 to 0.1 ms a tile (EarthTerrainCheck 5).
     */
    public void tileTerrain(long tx, long ty, byte[] out) {
        draw(tx, ty, 0, 0, World.TILE, World.TILE, out);
    }

    /**
     * Plots (ox, oy) to (ox + w - 1, oy + h - 1) of tile (tx, ty), row by
     * row (out[(py - oy) x w + px - ox]): each the tile's, byte for byte - the
     * same lattice points from the same corners, the same shares, octaves and
     * sums at each plot - evaluated on these plots and the ring about them
     * alone, so a line costs a part of its tile (lineTerrain()).
     */
    private void draw(long tx, long ty, int ox, int oy, int w, int h, byte[] out) {
        int n = World.TILE;
        if (tx < 0 || ty < 0 || tx >= World.SIDE / n || ty >= World.SIDE / n) {
            Arrays.fill(out, 0, w * h, World.SALT);
            return;
        }
        Scratch s = scratch();
        long x0 = tx * n, y0 = ty * n;
        EarthProj.Blend b = proj.blend(x0, y0, n);
        // The lattice points the plots and their ring blend between: plots x0 - 4 + 4i, y0 - 4 + 4j, each at its centre.
        int i0 = (ox - 1 + LATTICE_STEP) / LATTICE_STEP, i1 = Math.min(LATTICE - 1, (ox + w + LATTICE_STEP) / LATTICE_STEP + 1);
        int j0 = (oy - 1 + LATTICE_STEP) / LATTICE_STEP, j1 = Math.min(LATTICE - 1, (oy + h + LATTICE_STEP) / LATTICE_STEP + 1);
        boolean anySea = false, allSea = true, anyLake = false, anyForest = false, allForest = true;
        boolean anyIce = false, allIce = true, anyDesert = false, allDesert = true;
        double[] ll = s.ll, cv = s.cv;
        for (int j = j0; j <= j1; j++) {
            for (int i = i0; i <= i1; i++) {
                int at = j * LATTICE + i;
                double px = x0 - LATTICE_STEP + LATTICE_STEP * i + 0.5, py = y0 - LATTICE_STEP + LATTICE_STEP * j + 0.5;
                if (!b.latLon(px, py, ll)) {
                    s.seaL[at] = 1;
                    s.lakeL[at] = 0;
                    s.forestL[at] = 0;
                    s.iceL[at] = 0;
                    s.desertL[at] = 0;
                } else {
                    s.seaL[at] = s.seaR.bilinear(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
                    s.lakeL[at] = s.lakeR.bilinear(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
                    s.coverR.cover(ll[0], ll[1], cv);
                    s.forestL[at] = cv[0] / EarthData.SHARE_LEVELS;
                    s.iceL[at] = cv[1];
                    s.desertL[at] = cv[2];
                }
                anySea |= s.seaL[at] > 0;
                allSea &= s.seaL[at] >= 1;
                anyLake |= s.lakeL[at] > 0;
                anyForest |= s.forestL[at] > 0;
                allForest &= s.forestL[at] >= 1;
                anyIce |= s.iceL[at] > 0;
                allIce &= s.iceL[at] >= 1;
                anyDesert |= s.desertL[at] > 0;
                allDesert &= s.desertL[at] >= 1;
            }
        }
        if (allSea) {
            Arrays.fill(out, 0, w * h, World.SALT);
            return;
        }
        double[] q = quantile();
        int rw = w + 2, rh = h + 2;
        // The sea over the plots and their ring: the share's half-contour, frayed by the coast noise.
        if (anySea) {
            Arrays.fill(s.coast, 0, rw * rh, 0);
            for (int k = NOISE_HI; k >= NOISE_LO; k--) octave(s.lat, os[COAST][k], k, x0 + ox - 1, y0 + oy - 1, rw, rh, AMP_N[k], s.coast);
            for (int j = 0; j < rh; j++) {
                for (int i = 0; i < rw; i++) s.sea[j * rw + i] = sea(lattice(s.seaL, ox + i - 1, oy + j - 1), s.coast[j * rw + i]);
            }
        } else {
            Arrays.fill(s.sea, 0, rw * rh, false);
        }
        // The rivers and small lakes reaching the plots.
        long rx0 = x0 + ox, ry0 = y0 + oy;
        Water wt = water(Math.floorDiv(x0, World.DISTRICT), Math.floorDiv(y0, World.DISTRICT));
        int nseg = 0, nlake = 0;
        for (int k = 0; k < wt.segs; k++) {
            if (wt.maxX[k] < rx0 || wt.minX[k] > rx0 + w || wt.maxY[k] < ry0 || wt.minY[k] > ry0 + h) continue;
            if (nseg == s.segs.length) s.segs = Arrays.copyOf(s.segs, 2 * nseg);
            s.segs[nseg++] = k;
        }
        double blobReach = Math.sqrt(1 + BLOB_FRAY / 2);
        for (int k = 0; k < wt.lakes; k++) {
            double reach = wt.lr[k] * blobReach;
            if (wt.lx[k] + reach < rx0 || wt.lx[k] - reach > rx0 + w || wt.ly[k] + reach < ry0 || wt.ly[k] - reach > ry0 + h) continue;
            if (nlake == s.lakes.length) s.lakes = Arrays.copyOf(s.lakes, 2 * nlake);
            s.lakes[nlake++] = k;
        }
        if (anyLake) {
            Arrays.fill(s.fresh, 0, w * h, 0);
            for (int k = NOISE_HI; k >= NOISE_LO; k--) octave(s.lat, os[FRESH][k], k, rx0, ry0, w, h, AMP_N[k], s.fresh);
        }
        if (nlake > 0) {
            Arrays.fill(s.blob, 0, w * h, 0);
            for (int k = NOISE_HI; k >= NOISE_LO; k--) octave(s.lat, os[BLOB][k], k, rx0, ry0, w, h, AMP_N[k], s.blob);
        }
        boolean forestNoise = anyForest && !allForest;
        if (forestNoise) {
            Arrays.fill(s.forest, 0, w * h, 0);
            for (int k = NOISE_HI; k >= NOISE_LO; k--) octave(s.lat, os[FOREST][k], k, rx0, ry0, w, h, AMP_N[k], s.forest);
        }
        // Ice's and the desert's edges (0.7.127): each kind's own noise, where its bit's share is neither nought nor whole.
        boolean iceNoise = anyIce && !allIce, desertNoise = anyDesert && !allDesert;
        if (iceNoise) {
            Arrays.fill(s.ice, 0, w * h, 0);
            for (int k = NOISE_HI; k >= NOISE_LO; k--) octave(s.lat, os[ICE][k], k, rx0, ry0, w, h, AMP_N[k], s.ice);
        }
        if (desertNoise) {
            Arrays.fill(s.desert, 0, w * h, 0);
            for (int k = NOISE_HI; k >= NOISE_LO; k--) octave(s.lat, os[DESERT][k], k, rx0, ry0, w, h, AMP_N[k], s.desert);
        }
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                int i = py * w + px, r = (py + 1) * rw + px + 1;
                if (s.sea[r]) { out[i] = World.SALT; continue; }
                double x = rx0 + px + 0.5, y = ry0 + py + 0.5;
                boolean wet = false;
                for (int k = 0; k < nseg && !wet; k++) wet = onRiver(wt, s.segs[k], x, y);
                for (int k = 0; k < nlake && !wet; k++) wet = inLake(wt, s.lakes[k], x, y, s.blob[i], q);
                if (!wet && anyLake) {
                    double lake = lattice(s.lakeL, ox + px, oy + py);
                    if (lake > 0) wet = s.fresh[i] < below(q, lake / (1 - lattice(s.seaL, ox + px, oy + py)));
                }
                if (wet) { out[i] = World.FRESH; continue; }
                if (allIce || (iceNoise && covered(lattice(s.iceL, ox + px, oy + py), s.ice[i]))) { out[i] = World.ICE; continue; }
                if (anySea && (s.sea[r - rw - 1] || s.sea[r - rw] || s.sea[r - rw + 1] || s.sea[r - 1] || s.sea[r + 1]
                        || s.sea[r + rw - 1] || s.sea[r + rw] || s.sea[r + rw + 1]) && beach(s, b, x, y)) {
                    out[i] = World.SAND;
                    continue;
                }
                boolean forest = allForest || (forestNoise && s.forest[i] < below(q, lattice(s.forestL, ox + px, oy + py)));
                boolean desert = !forest && (allDesert || (desertNoise && covered(lattice(s.desertL, ox + px, oy + py), s.desert[i])));
                out[i] = forest ? World.FOREST : desert ? World.DESERT : World.GRASS;
            }
        }
    }

    /** A share at the tile's plot (px, py) - -1 to 32, the ring included - blended between the lattice's points about it. */
    private static double lattice(double[] at, int px, int py) {
        int gx = px + LATTICE_STEP, gy = py + LATTICE_STEP;
        int i = gx / LATTICE_STEP, j = gy / LATTICE_STEP;
        double u = (gx % LATTICE_STEP) / (double) LATTICE_STEP, v = (gy % LATTICE_STEP) / (double) LATTICE_STEP;
        int i1 = Math.min(LATTICE - 1, i + 1), j1 = Math.min(LATTICE - 1, j + 1);
        double a = at[j * LATTICE + i], bb = at[j * LATTICE + i1], c = at[j1 * LATTICE + i], d = at[j1 * LATTICE + i1];
        return (a * (1 - u) + bb * u) * (1 - v) + (c * (1 - u) + d * u) * v;
    }

    /** Whether the 2' cell under the point (plot coordinates) has bare ground on 2% of its land or more (EarthData.LC_BEACH). */
    private static boolean beach(Scratch s, EarthProj.Blend b, double x, double y) {
        if (!b.latLon(x, y, s.ll)) return false;
        return (s.coverR.at(s.ll[0], s.ll[1]) & EarthData.LC_BEACH) != 0;
    }

    /** Adds `amp` x octave k's value noise at the plot centres of a w x h rectangle from plot (gx0, gy0), row by row: its lattice hashed once, the smoothstep from SMOOTH (World.octaveTile()'s way) - a plot's value the same whatever rectangle holds it. */
    private static void octave(double[] lat, long os, int k, long gx0, long gy0, int w, int h, double amp, double[] acc) {
        long lx0 = gx0 >> k, ly0 = gy0 >> k;
        int nx = (int) (((gx0 + w - 1) >> k) - lx0) + 2, ny = (int) (((gy0 + h - 1) >> k) - ly0) + 2;
        for (int j = 0; j < ny; j++) for (int i = 0; i < nx; i++) lat[j * nx + i] = amp * World.lattice(os, lx0 + i, ly0 + j);
        double[] sm = SMOOTH[k];
        int mask = (1 << k) - 1;
        for (int py = 0; py < h; py++) {
            long gy = gy0 + py;
            int rj = (int) ((gy >> k) - ly0);
            double v = sm[(int) (gy & mask)];
            int row = py * w, r0 = rj * nx, r1 = r0 + nx;
            for (int px = 0; px < w; px++) {
                long gx = gx0 + px;
                int ri = (int) ((gx >> k) - lx0);
                double u = sm[(int) (gx & mask)];
                double a = lat[r0 + ri], b = lat[r0 + ri + 1], c = lat[r1 + ri], d = lat[r1 + ri + 1];
                acc[row + px] += a + (b - a) * u + (c - a) * v + (a - b - c + d) * u * v;
            }
        }
    }

    /** Whether a point lies within a river segment's half-width, interpolated along it. */
    private static boolean onRiver(Water w, int k, double x, double y) {
        double ax = w.ax[k], ay = w.ay[k], vx = w.bx[k] - ax, vy = w.by[k] - ay, l2 = vx * vx + vy * vy;
        double t = l2 > 0 ? ((x - ax) * vx + (y - ay) * vy) / l2 : 0;
        t = t < 0 ? 0 : t > 1 ? 1 : t;
        double dx = x - ax - t * vx, dy = y - ay - t * vy, h = w.ha[k] + (w.hb[k] - w.ha[k]) * t;
        return dx * dx + dy * dy < h * h;
    }

    /** Whether a point is in small lake k's blob, its blob noise there `n` (a stride's noise, at a region's pixel). */
    private static boolean inLake(Water w, int k, double x, double y, double n, double[] q) {
        double dx = x - w.lx[k], dy = y - w.ly[k], r = w.lr[k];
        double t = (dx * dx + dy * dy) / (r * r);
        if (t >= 1 + BLOB_FRAY / 2) return false;
        if (t <= 1 - BLOB_FRAY / 2) return true;
        return n < below(q, 0.5 + (1 - t) / BLOB_FRAY);
    }

    /* =====================================================================
       A PLOT, A LINE, A REGION AND A POINT'S SEA
       ===================================================================== */

    private final Map<Long, byte[]> tiles = new LinkedHashMap<>(256, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, byte[]> eldest) {
            return size() > TILES_KEPT;
        }
    };

    /** A tile's ground, kept (TILES_KEPT): shared, do not write into it. */
    byte[] keptTile(long tx, long ty) {
        long k = (tx << 32) ^ (ty & 0xffffffffL);
        synchronized (tiles) {
            byte[] t = tiles.get(k);
            if (t != null) return t;
        }
        byte[] t = new byte[World.TILE * World.TILE];
        tileTerrain(tx, ty, t);
        synchronized (tiles) { tiles.put(k, t); }
        return t;
    }

    /** One plot's ground: its tile's (tileTerrain()), from a kept tile; sea off the world. */
    public byte terrainAt(long x, long y) {
        if (x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) return World.SALT;
        byte[] t = keptTile(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
        return t[(int) (Math.floorMod(y, World.TILE) * World.TILE + Math.floorMod(x, World.TILE))];
    }

    /** One row (or, `column`, one column) of a tile's ground, its `at`-th: the tile's, byte for byte (World.lineTerrain()'s contract) - from the tile if it is kept, else the line drawn alone (draw()). */
    public void lineTerrain(long tx, long ty, boolean column, int at, byte[] out) {
        int n = World.TILE;
        if (tx < 0 || ty < 0 || tx >= World.SIDE / n || ty >= World.SIDE / n) {
            Arrays.fill(out, 0, n, World.SALT);
            return;
        }
        byte[] t;
        synchronized (tiles) { t = tiles.get((tx << 32) ^ (ty & 0xffffffffL)); }
        if (t != null) {
            for (int i = 0; i < n; i++) out[i] = column ? t[i * n + at] : t[at * n + i];
            return;
        }
        draw(tx, ty, column ? at : 0, column ? 0 : at, column ? 1 : n, column ? n : 1, out);
    }

    /**
     * A region's ground at a stride (World.regionTerrain()'s contract): n x n
     * pixels from plot (x0, y0), each `stride` plots wide. At a stride of one,
     * every plot its tile's. Wider, each pixel classed at its centre: the
     * shares there (the 15" layers, or the 4' far layers from FAR_STRIDE),
     * each noise's octaves wider than a pixel and the rest at their mean;
     * rivers and small lakes under WATER_STRIDE; no beach, a plot wide.
     */
    public void regionTerrain(long x0, long y0, int stride, int n, byte[] out) {
        if (stride <= 1) {
            for (int py = 0; py < n; py++) {
                for (int px = 0; px < n; px++) out[py * n + px] = terrainAt(x0 + px, y0 + py);
            }
            return;
        }
        Scratch s = scratch();
        double[] q = quantile();
        for (int py = 0; py < n; py++) {
            double y = y0 + (double) py * stride + stride * 0.5;
            for (int px = 0; px < n; px++) {
                double x = x0 + (double) px * stride + stride * 0.5;
                out[py * n + px] = pointClass(s, q, x, y, stride);
            }
        }
    }

    /** A point's class at a stride: see regionTerrain(). */
    private byte pointClass(Scratch s, double[] q, double x, double y, int stride) {
        return pointClass(s, q, x, y, stride, true, Double.NaN);
    }

    /**
     * ...with the rivers and small lakes drawn (`water`) or left to the
     * caller, and the forest ranked through its noise or, where `dither` is a
     * number (0 to 1), drawn where it is under the forest's share - the far
     * node's (farTerrain(), 0.7.119).
     */
    private byte pointClass(Scratch s, double[] q, double x, double y, int stride, boolean water, double dither) {
        if (x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) return World.SALT;
        double[] ll = s.ll;
        if (!proj.latLonOfPlot(x, y, ll)) return World.SALT;
        return classAt(s, q, x, y, ll[0], ll[1], stride, water, dither);
    }

    /** ...at a point on the planet whose latitude and longitude are known. */
    private byte classAt(Scratch s, double[] q, double x, double y, double lat, double lon, int stride, boolean water, double dither) {
        double sea, lake, forest, ice, desert;
        if (stride >= FAR_STRIDE) {
            double dry = s.farLandR.at(lat, lon) / (double) EarthData.FAR_LEVELS;
            lake = s.farLakeR.at(lat, lon) / (double) EarthData.FAR_LEVELS;
            sea = Math.max(0, 1 - dry - lake);
            int v = s.coverR.at(lat, lon);
            forest = (v & EarthData.LC_FOREST) / (double) EarthData.SHARE_LEVELS;
            ice = (v & EarthData.LC_ICE) != 0 ? 1 : 0;
            desert = (v & EarthData.LC_DESERT) != 0 ? 1 : 0;
        } else {
            sea = s.seaR.bilinear(lat, lon) / EarthData.SHARE_LEVELS;
            lake = s.lakeR.bilinear(lat, lon) / EarthData.SHARE_LEVELS;
            double[] cv = s.cv;
            s.coverR.cover(lat, lon, cv);
            forest = cv[0] / EarthData.SHARE_LEVELS;
            ice = cv[1];
            desert = cv[2];
        }
        if (sea > 0 && sea(sea, noiseAt(os[COAST], x, y, stride))) return World.SALT;
        if (water && stride < WATER_STRIDE) {
            Water w = water(Math.floorDiv((long) Math.floor(x), World.DISTRICT), Math.floorDiv((long) Math.floor(y), World.DISTRICT));
            for (int k = 0; k < w.segs; k++) {
                if (x < w.minX[k] || x > w.maxX[k] || y < w.minY[k] || y > w.maxY[k]) continue;
                if (onRiver(w, k, x, y)) return World.FRESH;
            }
            double nb = Double.NaN;
            for (int k = 0; k < w.lakes; k++) {
                double dx = x - w.lx[k], dy = y - w.ly[k], r = w.lr[k];
                if (dx * dx + dy * dy >= r * r * (1 + BLOB_FRAY / 2)) continue;
                if (Double.isNaN(nb)) nb = noiseAt(os[BLOB], x, y, stride);
                if (inLake(w, k, x, y, nb, q)) return World.FRESH;
            }
        }
        if (lake > 0 && noiseAt(os[FRESH], x, y, stride) < below(q, lake / (1 - sea))) return World.FRESH;
        if (ice >= 1 || (ice > 0 && covered(ice, noiseAt(os[ICE], x, y, stride)))) return World.ICE;
        if (forest >= 1 || (forest > 0 && (Double.isNaN(dither) ? noiseAt(os[FOREST], x, y, stride) < below(q, forest) : dither < forest))) return World.FOREST;
        if (desert >= 1 || (desert > 0 && covered(desert, noiseAt(os[DESERT], x, y, stride)))) return World.DESERT;
        return World.GRASS;
    }

    /**
     * Whether a point (plot coordinates) is sea, by the coast's rule at the
     * point itself: the sea share there, read through the exact projection,
     * and the coast noise at every octave - what a field drawn off the sea
     * asks (World's deposits). The tile reads the same share through its
     * lattice, so a plot within a hair of the threshold may differ.
     */
    public boolean seaAt(double x, double y) {
        if (x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) return true;
        Scratch s = scratch();
        if (!proj.latLonOfPlot(x, y, s.ll)) return true;
        double sea = s.seaR.bilinear(s.ll[0], s.ll[1]) / EarthData.SHARE_LEVELS;
        if (!(sea > 0)) return false;
        return sea(sea, noiseAt(os[COAST], x, y, 1));
    }

    /* =====================================================================
       RIVERS AND SMALL LAKES, PROJECTED: a 1-degree tile's, and a district's
       ===================================================================== */

    /** Rivers' segments and small lakes in plots: each segment's ends, half-widths and box widened by them, each lake's centre and radius. */
    private static final class Water {
        double[] ax = new double[16], ay = new double[16], bx = new double[16], by = new double[16], ha = new double[16], hb = new double[16];
        double[] minX = new double[16], minY = new double[16], maxX = new double[16], maxY = new double[16];
        double[] lx = new double[4], ly = new double[4], lr = new double[4];
        int segs, lakes;

        void addSeg(double ax0, double ay0, double bx0, double by0, double h0, double h1) {
            if (segs == ax.length) {
                int m = 2 * segs;
                ax = Arrays.copyOf(ax, m); ay = Arrays.copyOf(ay, m); bx = Arrays.copyOf(bx, m); by = Arrays.copyOf(by, m);
                ha = Arrays.copyOf(ha, m); hb = Arrays.copyOf(hb, m);
                minX = Arrays.copyOf(minX, m); minY = Arrays.copyOf(minY, m); maxX = Arrays.copyOf(maxX, m); maxY = Arrays.copyOf(maxY, m);
            }
            double h = Math.max(h0, h1);
            ax[segs] = ax0; ay[segs] = ay0; bx[segs] = bx0; by[segs] = by0; ha[segs] = h0; hb[segs] = h1;
            minX[segs] = Math.min(ax0, bx0) - h; maxX[segs] = Math.max(ax0, bx0) + h;
            minY[segs] = Math.min(ay0, by0) - h; maxY[segs] = Math.max(ay0, by0) + h;
            segs++;
        }

        void addLake(double x, double y, double r) {
            if (lakes == lx.length) {
                int m = 2 * lakes;
                lx = Arrays.copyOf(lx, m); ly = Arrays.copyOf(ly, m); lr = Arrays.copyOf(lr, m);
            }
            lx[lakes] = x; ly[lakes] = y; lr[lakes] = r;
            lakes++;
        }
    }

    /** A district's water where it lies off the world or off the planet: no river and no small lake. */
    private static final Water NO_WATER = new Water();

    private final Map<Integer, Water> dataTiles = new LinkedHashMap<>(64, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Integer, Water> eldest) {
            return size() > DATA_TILES_KEPT;
        }
    };

    private final Map<Long, Water> districts = new LinkedHashMap<>(256, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, Water> eldest) {
            return size() > DISTRICTS_KEPT;
        }
    };

    /** A 1-degree tile's rivers and small lakes through the projection, kept: every run's segments as stored (one crossing the tile's edge is in each tile it touches), each half-width at least RIVER_MIN_HALF. */
    private Water dataTile(int tile) {
        synchronized (dataTiles) {
            Water w = dataTiles.get(tile);
            if (w != null) return w;
        }
        Water w = new Water();
        double[] a = new double[2], b = new double[2];
        for (EarthData.Run run : data.rivers(tile)) {
            boolean have = run.size() > 0 && proj.plotOf(run.lat(0), run.lon(0), a);
            for (int k = 0; k + 1 < run.size(); k++) {
                boolean next = proj.plotOf(run.lat(k + 1), run.lon(k + 1), b);
                if (have && next) {
                    w.addSeg(a[0], a[1], b[0], b[1], halfWidth(run.width(k)), halfWidth(run.width(k + 1)));
                }
                a[0] = b[0];
                a[1] = b[1];
                have = next;
            }
        }
        EarthData.SmallLakes sl = data.smallLakes(tile);
        for (int k = 0; k < sl.size(); k++) {
            if (!proj.plotOf(sl.lat(k), sl.lon(k), a)) continue;
            w.addLake(a[0], a[1], Math.sqrt(sl.areaKm2(k) * 1e6 / Math.PI) / World.PLOT_M);
        }
        synchronized (dataTiles) { dataTiles.put(tile, w); }
        return w;
    }

    /** A river's half-width in plots from its width in metres: at least RIVER_MIN_HALF. */
    private static double halfWidth(int widthM) { return Math.max(RIVER_MIN_HALF, widthM / 2.0 / World.PLOT_M); }

    /** A district's rivers and small lakes, kept: those of the 1-degree tiles within WATER_PAD_DEG of it whose reach touches it. */
    private Water water(long dx, long dy) {
        long key = (dx << 32) ^ (dy & 0xffffffffL);
        synchronized (districts) {
            Water w = districts.get(key);
            if (w != null) return w;
        }
        Water w = districtWater(dx, dy);
        synchronized (districts) { districts.put(key, w); }
        return w;
    }

    private Water districtWater(long dx, long dy) {
        int d = World.DISTRICT;
        long x0 = dx * d, y0 = dy * d;
        if (x0 + d <= 0 || y0 + d <= 0 || x0 >= World.SIDE || y0 >= World.SIDE) return NO_WATER;
        // Its box in latitude and longitude, from 5 x 5 points over it (longitudes unwrapped about the first on the planet).
        double minLat = 90, maxLat = -90, minLon = 0, maxLon = 0, ref = Double.NaN;
        double[] ll = new double[2];
        for (int j = 0; j <= 4; j++) {
            for (int i = 0; i <= 4; i++) {
                if (!proj.latLonOfPlot(x0 + d * i / 4.0, y0 + d * j / 4.0, ll)) continue;
                double lon = ll[1];
                if (Double.isNaN(ref)) { ref = lon; minLon = lon; maxLon = lon; }
                if (lon - ref > 180) lon -= 360;
                else if (lon - ref < -180) lon += 360;
                minLat = Math.min(minLat, ll[0]); maxLat = Math.max(maxLat, ll[0]);
                minLon = Math.min(minLon, lon); maxLon = Math.max(maxLon, lon);
            }
        }
        if (Double.isNaN(ref)) return NO_WATER;
        double[] pole = new double[2];
        boolean north = proj.plotOf(90, 0, pole) && pole[0] >= x0 && pole[0] < x0 + d && pole[1] >= y0 && pole[1] < y0 + d;
        boolean south = proj.plotOf(-90, 0, pole) && pole[0] >= x0 && pole[0] < x0 + d && pole[1] >= y0 && pole[1] < y0 + d;
        if (north) maxLat = 90;
        if (south) minLat = -90;
        double lat0 = minLat - WATER_PAD_DEG, lat1 = maxLat + WATER_PAD_DEG;
        double cos = StrictMath.cos(StrictMath.toRadians(Math.min(89.9, Math.max(Math.abs(lat0), Math.abs(lat1)))));
        double padLon = WATER_PAD_DEG / cos;
        boolean allLon = north || south || lat1 >= 90 || lat0 <= -90 || maxLon - minLon + 2 * padLon >= 360;
        int r0 = (int) Math.max(-90, Math.floor(lat0)), r1 = (int) Math.min(89, Math.floor(lat1));
        long c0 = allLon ? -180 : (long) Math.floor(minLon - padLon), c1 = allLon ? 179 : (long) Math.floor(maxLon + padLon);
        Water out = new Water();
        double ex0 = x0 - 1, ey0 = y0 - 1, ex1 = x0 + d + 1, ey1 = y0 + d + 1;
        double reach = Math.sqrt(1 + BLOB_FRAY / 2);
        for (int lat = r0; lat <= r1; lat++) {
            for (long lon = c0; lon <= c1; lon++) {
                int t = EarthData.tile(lat, (int) (Math.floorMod(lon + 180, 360L) - 180));
                Water tw = dataTile(t);
                for (int k = 0; k < tw.segs; k++) {
                    if (tw.maxX[k] < ex0 || tw.minX[k] > ex1 || tw.maxY[k] < ey0 || tw.minY[k] > ey1) continue;
                    out.addSeg(tw.ax[k], tw.ay[k], tw.bx[k], tw.by[k], tw.ha[k], tw.hb[k]);
                }
                for (int k = 0; k < tw.lakes; k++) {
                    double r = tw.lr[k] * reach;
                    if (tw.lx[k] + r < ex0 || tw.lx[k] - r > ex1 || tw.ly[k] + r < ey0 || tw.ly[k] - r > ey1) continue;
                    out.addLake(tw.lx[k], tw.ly[k], tw.lr[k]);
                }
            }
        }
        return out;
    }

    /* =====================================================================
       THE FAR VIEW'S NODES (0.7.119, batch E5; spec-earth 3, "Far views")
       ===================================================================== */

    /** A far node's forest is dithered by a hash from this stride, in plots: 2^NOISE_HI, 16 (480 m) - where no octave of the forest's noise is wider than a pixel, so the ranked noise stands at its mean and would draw forest wherever its share passes a half and nowhere else (spec-earth 3, "dither forest by a hash"; ★E5). */
    public static final int DITHER_STRIDE = 1 << NOISE_HI;

    /** A river's least half-width on a far node, in pixels: RIVER_MIN_HALF, 0.75 - a far pixel is to the node what a plot is to a tile, so a river is stroked at least 1.5 px wide, past the 0.71 px (half a pixel's diagonal) under which a line at 45 degrees breaks into corners (spec-earth 3, "at least 1 px wide"; ★E5). */
    public static final double FAR_RIVER_HALF_PX = RIVER_MIN_HALF;

    /**
     * The least width, as a share of a far pixel, at which a river is
     * stroked: 30 / 2,500, 0.012 (★E5) - the narrowest river in the data,
     * GRWL's 30 m (spec-earth 1), at the study's far render of 2.5 km a pixel
     * (spec-earth 3, "At 2.5 km a pixel, the North Sea coasts are
     * [recognisable]"). So every river is stroked at 2.5 km a pixel and
     * closer, and wider out only those as wide for the pixel as the
     * narrowest was there: stroked every one at least a pixel wide, the
     * whole world's view at 30.7 km a pixel drew Siberia 45% fresh water
     * against the data's half a percent (measured). A run of a river is
     * stroked whole or not at all, by its vertices' median width, so a river
     * near the line is not broken into dashes.
     */
    public static final double FAR_RIVER_SHARE = 30.0 / 2500;

    /** From WATER_STRIDE a far node's pixels take their latitude and longitude from blocks of this many a side, each blended from its four corners (EarthProj.blend(), the tile's way): 8 - measured, at worst 0.15 of a pixel off the exact at 1,024 plots a pixel and under 0.014 at 256 and closer, for a fifth of the exact inverses' time (closer, each pixel is projected exactly, as the region is). */
    public static final int FAR_BLEND_PX = 8;

    /**
     * From this stride, in plots, a far node places a 1-degree tile's river
     * vertices and small lakes bilinear between the tile's four corners,
     * projected exactly, rather than projecting each: 64 (★E5) - measured at
     * worst 0.07 of a pixel off at 64 plots a pixel (Rotterdam, Perth,
     * Chukotka), and 0.017 from 256; at 1,024 the 147,000 small lakes of
     * Chukotka's node each cost a projection's trigonometry otherwise. A tile
     * past EarthProj.BLEND_MAX_LAT or with a corner past the rim projects
     * each point.
     */
    public static final int FAR_TILE_BLEND_STRIDE = 64;

    /** Points along each edge of a far node its box of 1-degree tiles is found from: 32 - at the widest node (1,024 plots a pixel, 3,932 km a side) one every 123 km, where a straight edge strays from its chord by a fraction of the WATER_PAD_DEG (27.8 km) the box is widened by. */
    static final int NODE_EDGE_POINTS = 32;

    /** The longest river segment in earth-1, in metres on the sphere: 17,915, measured over all its tiles (MapCheck 10 holds it when the file is named). */
    public static final int LONGEST_SEGMENT_M = 17_915;

    /**
     * A river segment with an end this close to the planet's rim is not
     * stroked, in metres: LONGEST_SEGMENT_M^2 / 4R, 12.6 (★E5). The rim is
     * the antipode, drawn as a circle EarthProj.REACH_M from the spot; a
     * segment passing the antipode would join two points on opposite sides
     * of it straight across the world, and one L long can pass it only with
     * both ends within L^2/4R of the rim (an end an angle d from the
     * antipode lies 2R(1 - cos d/2) inside it). What is lost is the ground
     * within about 18 km of the antipode, stretched round the rim.
     */
    public static final double RIM_M = (double) LONGEST_SEGMENT_M * LONGEST_SEGMENT_M / (4 * EarthProj.RADIUS_M);

    /** The far view's dither's stream. */
    private static final long F_DITHER = 0xD17E4L;

    /** Each 1-degree tile's widest river run, by its median width in metres, once a far node has read it: -1 until then. A node too wide for any of a tile's runs skips the tile (FAR_RIVER_SHARE). */
    private volatile int[] widestRun;

    /**
     * A far node's ground (spec-earth 3, "Far views"; MapTiles.nodeTerrain()):
     * n x n pixels from plot (x0, y0), `stride` plots each. At a stride of
     * one, the tiles' plots (regionTerrain()). Wider, each pixel classed at
     * its centre as regionTerrain() classes it - the 15" shares, or the 4'
     * far layers from FAR_STRIDE - but for its water: under WATER_STRIDE the
     * region's pixels exactly, from it each pixel's latitude and longitude
     * blended over FAR_BLEND_PX blocks and (DITHER_STRIDE) its forest drawn
     * where a hash of the pixel falls under the forest's share of the dry
     * ground. Then the rivers stroked from their vectors, each run
     * at least FAR_RIVER_SHARE of a pixel wide at its width and at least
     * FAR_RIVER_HALF_PX a side, and the small lakes drawn from their points
     * and areas (frayed by their own noise where a pixel is narrower than its
     * widest octave), on every pixel that is not sea. The vectors are read
     * from the 1-degree tiles under the node (nodeTiles()), either side of
     * the date line alike. A pure function of the key and the node.
     */
    public void farTerrain(long x0, long y0, int stride, int n, byte[] out) {
        if (stride <= 1) {
            regionTerrain(x0, y0, stride, n, out);
            return;
        }
        Scratch s = scratch();
        double[] q = quantile(), ll = s.ll;
        boolean dither = stride >= DITHER_STRIDE;
        long ds = World.mix(seed ^ F_DITHER ^ (stride * 0x632BE59BD9B4E019L));
        int b = FAR_BLEND_PX;
        for (int by = 0; by < n; by += b) {
            for (int bx = 0; bx < n; bx += b) {
                EarthProj.Blend blend = stride >= WATER_STRIDE ? proj.blend(x0 + (double) bx * stride, y0 + (double) by * stride, (double) b * stride) : null;
                for (int py = by; py < Math.min(n, by + b); py++) {
                    double y = y0 + (double) py * stride + stride * 0.5;
                    long gy = y0 + (long) py * stride;
                    for (int px = bx; px < Math.min(n, bx + b); px++) {
                        double x = x0 + (double) px * stride + stride * 0.5;
                        int at = py * n + px;
                        if (x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE || !(blend != null ? blend.latLon(x, y, ll) : proj.latLonOfPlot(x, y, ll))) {
                            out[at] = World.SALT;
                            continue;
                        }
                        double d = dither ? World.unit(World.mix(ds ^ World.mix((x0 + (long) px * stride) * 0x9E3779B97F4A7C15L ^ gy))) : Double.NaN;
                        out[at] = classAt(s, q, x, y, ll[0], ll[1], stride, false, d);
                    }
                }
            }
        }
        strokeWater(x0, y0, stride, n, out, q);
    }

    /** How many river segments and small lakes the last far node on this thread drew from, and how many 1-degree tiles it read: what a harness prints. */
    static final ThreadLocal<long[]> FAR_COUNTS = ThreadLocal.withInitial(() -> new long[3]);

    /** The rivers and small lakes over a far node's pixels (farTerrain()): fresh water on every pixel that is not sea whose centre a river's stroke or a small lake's blob covers. */
    private void strokeWater(long x0, long y0, int stride, int n, byte[] out, double[] q) {
        double side = (double) stride * n, least = FAR_RIVER_HALF_PX * stride;
        double nx0 = x0 - least, ny0 = y0 - least, nx1 = x0 + side + least, ny1 = y0 + side + least;
        double rim = EarthProj.REACH_M - RIM_M, narrowest = FAR_RIVER_SHARE * stride * World.PLOT_M;
        double grow = Math.sqrt(1 + BLOB_FRAY / 2);
        double[] a = new double[2], b = new double[2];
        int[] widths = new int[64];
        int[] widest = widestRun;
        if (widest == null) {
            widest = new int[EarthData.TILES];
            Arrays.fill(widest, -1);
            widestRun = widest;
        }
        long segs = 0, lakes = 0;
        int[] tiles = nodeTiles(x0, y0, side);
        double[] corners = new double[8];
        for (int t : tiles) {
            boolean blended = stride >= FAR_TILE_BLEND_STRIDE && tileCorners(t, corners, rim);
            if (widest[t] < 0 || widest[t] >= narrowest) {
                int most = 0;
                for (EarthData.Run run : data.rivers(t)) {
                    int m = run.size();
                    if (m < 2) continue;
                    if (widths.length < m) widths = new int[Math.max(m, 2 * widths.length)];
                    for (int k = 0; k < m; k++) widths[k] = run.width(k);
                    Arrays.sort(widths, 0, m);
                    most = Math.max(most, widths[m / 2]);
                    if (widths[m / 2] < narrowest) continue;
                    boolean have = place(run.lat(0), run.lon(0), blended, corners, t, a) && !nearRim(a, rim);
                    for (int k = 0; k + 1 < m; k++) {
                        boolean next = place(run.lat(k + 1), run.lon(k + 1), blended, corners, t, b) && !nearRim(b, rim);
                        if (have && next) {
                            double ha = Math.max(least, run.width(k) / 2.0 / World.PLOT_M), hb = Math.max(least, run.width(k + 1) / 2.0 / World.PLOT_M);
                            double h = Math.max(ha, hb);
                            if (Math.max(a[0], b[0]) + h >= nx0 && Math.min(a[0], b[0]) - h <= nx1 && Math.max(a[1], b[1]) + h >= ny0 && Math.min(a[1], b[1]) - h <= ny1) {
                                stroke(a[0], a[1], b[0], b[1], ha, hb, x0, y0, stride, n, out);
                                segs++;
                            }
                        }
                        a[0] = b[0];
                        a[1] = b[1];
                        have = next;
                    }
                }
                widest[t] = most;
            }
            EarthData.SmallLakes sl = data.smallLakes(t);
            for (int k = 0; k < sl.size(); k++) {
                if (!place(sl.lat(k), sl.lon(k), blended, corners, t, a)) continue;
                double r = Math.sqrt(sl.areaKm2(k) * 1e6 / Math.PI) / World.PLOT_M, reach = r * grow;
                if (a[0] + reach < x0 || a[0] - reach > x0 + side || a[1] + reach < y0 || a[1] - reach > y0 + side) continue;
                blob(a[0], a[1], r, x0, y0, stride, n, out, q);
                lakes++;
            }
        }
        long[] c = FAR_COUNTS.get();
        c[0] = segs;
        c[1] = lakes;
        c[2] = tiles.length;
    }

    /** Tile t's four corners in plots into c (south-west, south-east, north-west, north-east, x then y), projected exactly: false - the tile's points then each projected - where the tile reaches past EarthProj.BLEND_MAX_LAT or a corner is off the planet or within RIM_M of its rim. */
    private boolean tileCorners(int t, double[] c, double rim) {
        int la = EarthData.tileLat0(t), lo = EarthData.tileLon0(t);
        if (Math.max(Math.abs(la), Math.abs(la + 1)) > EarthProj.BLEND_MAX_LAT) return false;
        double[] p = new double[2];
        for (int k = 0; k < 4; k++) {
            if (!proj.plotOf(la + (k >> 1), lo + (k & 1), p) || nearRim(p, rim)) return false;
            c[2 * k] = p[0];
            c[2 * k + 1] = p[1];
        }
        return true;
    }

    /** A point's plot in tile t: bilinear between the tile's corners where `blended` (tileCorners()), else projected; false off the planet. */
    private boolean place(double lat, double lon, boolean blended, double[] c, int t, double[] out) {
        if (!blended) return proj.plotOf(lat, lon, out);
        double v = lat - EarthData.tileLat0(t), u = EarthProj.normaliseLon(lon - EarthData.tileLon0(t));
        double iu = 1 - u, iv = 1 - v;
        out[0] = (c[0] * iu + c[2] * u) * iv + (c[4] * iu + c[6] * u) * v;
        out[1] = (c[1] * iu + c[3] * u) * iv + (c[5] * iu + c[7] * u) * v;
        return true;
    }

    /** Whether a point in plots lies within RIM_M of the rim: `rim` metres or more from the spot. */
    private static boolean nearRim(double[] p, double rim) {
        double mx = EarthProj.metresX(p[0]), my = EarthProj.metresY(p[1]);
        return mx * mx + my * my >= rim * rim;
    }

    /** One river segment stroked onto a far node: every pixel not sea whose centre lies within the segment's half-width there, interpolated along it (onRiver()'s rule). */
    private static void stroke(double ax, double ay, double bx, double by, double ha, double hb, long x0, long y0, int stride, int n, byte[] out) {
        double h = Math.max(ha, hb);
        int i0 = first(Math.min(ax, bx) - h, x0, stride, n), i1 = last(Math.max(ax, bx) + h, x0, stride, n);
        int j0 = first(Math.min(ay, by) - h, y0, stride, n), j1 = last(Math.max(ay, by) + h, y0, stride, n);
        if (i0 > i1 || j0 > j1) return;
        double vx = bx - ax, vy = by - ay, l2 = vx * vx + vy * vy;
        for (int j = j0; j <= j1; j++) {
            double y = y0 + (j + 0.5) * stride;
            for (int i = i0; i <= i1; i++) {
                int at = j * n + i;
                if (out[at] == World.SALT || out[at] == World.FRESH) continue;
                double x = x0 + (i + 0.5) * stride;
                double t = l2 > 0 ? ((x - ax) * vx + (y - ay) * vy) / l2 : 0;
                t = t < 0 ? 0 : t > 1 ? 1 : t;
                double dx = x - ax - t * vx, dy = y - ay - t * vy, w = ha + (hb - ha) * t;
                if (dx * dx + dy * dy < w * w) out[at] = World.FRESH;
            }
        }
    }

    /** One small lake drawn onto a far node: every pixel not sea whose centre its blob holds - inLake()'s rule, its noise at the node's stride (at DITHER_STRIDE and up, at its mean: the lake's disc). */
    private void blob(double lx, double ly, double r, long x0, long y0, int stride, int n, byte[] out, double[] q) {
        double reach = r * Math.sqrt(1 + BLOB_FRAY / 2);
        int i0 = first(lx - reach, x0, stride, n), i1 = last(lx + reach, x0, stride, n);
        int j0 = first(ly - reach, y0, stride, n), j1 = last(ly + reach, y0, stride, n);
        for (int j = j0; j <= j1; j++) {
            double y = y0 + (j + 0.5) * stride;
            for (int i = i0; i <= i1; i++) {
                int at = j * n + i;
                if (out[at] == World.SALT || out[at] == World.FRESH) continue;
                double x = x0 + (i + 0.5) * stride, dx = x - lx, dy = y - ly;
                double t = (dx * dx + dy * dy) / (r * r);
                if (t >= 1 + BLOB_FRAY / 2) continue;
                if (t <= 1 - BLOB_FRAY / 2 || noiseAt(os[BLOB], x, y, stride) < below(q, 0.5 + (1 - t) / BLOB_FRAY)) out[at] = World.FRESH;
            }
        }
    }

    /** The first pixel of a node (from plot `o`, `stride` plots each, n of them) whose centre is at `v` or past it, clamped to the node: n when none is. */
    private static int first(double v, long o, int stride, int n) {
        double i = Math.ceil((v - o) / stride - 0.5);
        return i <= 0 ? 0 : i >= n ? n : (int) i;
    }

    /** ...and the last whose centre is at `v` or before it: -1 when none is. */
    private static int last(double v, long o, int stride, int n) {
        double i = Math.floor((v - o) / stride - 0.5);
        return i < 0 ? -1 : i >= n - 1 ? n - 1 : (int) i;
    }

    /**
     * The 1-degree tiles whose rivers and small lakes may reach a box of
     * plots [x0, x0 + side)^2. First a box of latitude and longitude: the
     * box's edges read at NODE_EDGE_POINTS points each, their longitudes
     * unwrapped point to point round it (so the date line is crossed like
     * any meridian), widened by WATER_PAD_DEG - every longitude where the box
     * holds a pole, or reaches past the planet's rim, where its ground runs
     * to the antipode, its latitudes then out to the antipode's. Then each
     * tile there that holds rivers or small lakes kept only where its own
     * footprint, widened by WATER_PAD_DEG, reaches the box (footprint()).
     */
    int[] nodeTiles(double x0, double y0, double side) {
        double minLat = 90, maxLat = -90, minLon = Double.POSITIVE_INFINITY, maxLon = Double.NEGATIVE_INFINITY;
        double prev = Double.NaN, first = Double.NaN;
        boolean off = false;
        double[] ll = new double[2];
        int m = NODE_EDGE_POINTS;
        for (int e = 0; e < 4; e++) {
            for (int k = 0; k < m; k++) {
                double f = k * side / m;
                double x = e == 0 ? x0 + f : e == 1 ? x0 + side : e == 2 ? x0 + side - f : x0;
                double y = e == 0 ? y0 : e == 1 ? y0 + f : e == 2 ? y0 + side : y0 + side - f;
                if (!proj.latLonOfPlot(x, y, ll)) {
                    off = true;
                    continue;
                }
                double lon = ll[1];
                if (Double.isNaN(prev)) first = lon;
                else lon = prev + (((lon - prev) % 360 + 540) % 360 - 180);
                prev = lon;
                minLat = Math.min(minLat, ll[0]);
                maxLat = Math.max(maxLat, ll[0]);
                minLon = Math.min(minLon, lon);
                maxLon = Math.max(maxLon, lon);
            }
        }
        if (Double.isNaN(prev)) return new int[0];
        double[] pole = new double[2];
        boolean north = proj.plotOf(90, 0, pole) && pole[0] >= x0 && pole[0] < x0 + side && pole[1] >= y0 && pole[1] < y0 + side;
        boolean south = proj.plotOf(-90, 0, pole) && pole[0] >= x0 && pole[0] < x0 + side && pole[1] >= y0 && pole[1] < y0 + side;
        // Round the box and back: a longitude that has wound a whole turn went round a pole.
        double round = prev + (((first - prev) % 360 + 540) % 360 - 180);
        boolean wound = Math.abs(round - first) > 180;
        if (north) maxLat = 90;
        if (south) minLat = -90;
        if (off) {
            double antipode = -proj.centreLat();
            minLat = Math.min(minLat, antipode);
            maxLat = Math.max(maxLat, antipode);
        }
        double lat0 = minLat - WATER_PAD_DEG, lat1 = maxLat + WATER_PAD_DEG;
        double cos = StrictMath.cos(StrictMath.toRadians(Math.min(89.9, Math.max(Math.abs(lat0), Math.abs(lat1)))));
        double padLon = WATER_PAD_DEG / cos;
        boolean allLon = north || south || wound || off || lat1 >= 90 || lat0 <= -90 || maxLon - minLon + 2 * padLon >= 360;
        int r0 = (int) Math.max(-90, Math.floor(lat0)), r1 = (int) Math.min(89, Math.floor(lat1));
        long c0 = allLon ? -180 : (long) Math.floor(minLon - padLon), c1 = allLon ? 179 : (long) Math.floor(maxLon + padLon);
        int[] out = new int[(r1 - r0 + 1) * (int) (c1 - c0 + 1)];
        int k = 0;
        for (int lat = r0; lat <= r1; lat++) {
            for (long lon = c0; lon <= c1; lon++) {
                int t = EarthData.tile(lat, (int) (Math.floorMod(lon + 180, 360L) - 180));
                if (data.entry("rivers", t) <= 0 && data.entry("lakes.small", t) <= 0) continue;
                if (footprint(t, x0, y0, side)) out[k++] = t;
            }
        }
        return Arrays.copyOf(out, k);
    }

    /** Points a side of a 1-degree tile its footprint is read at: 3, its corners, the middles of its edges and its centre. */
    private static final int FOOTPRINT_POINTS = 3;

    /**
     * Whether 1-degree tile t, widened by WATER_PAD_DEG (the reach of its
     * widest river past its edge), may reach a box of plots: its FOOTPRINT_POINTS
     * x FOOTPRINT_POINTS points projected, their box widened by an eighth of
     * its longer side (the most an edge between two of them can bow out),
     * against the box; true where any point is off the planet.
     */
    private boolean footprint(int t, double x0, double y0, double side) {
        double lat0 = EarthData.tileLat0(t) - WATER_PAD_DEG, lat1 = EarthData.tileLat0(t) + 1 + WATER_PAD_DEG;
        double cos = StrictMath.cos(StrictMath.toRadians(Math.min(89.9, Math.max(Math.abs(lat0), Math.abs(lat1)))));
        double pad = Math.min(180, WATER_PAD_DEG / cos);
        double lon0 = EarthData.tileLon0(t) - pad, lon1 = EarthData.tileLon0(t) + 1 + pad;
        double bx0 = Double.POSITIVE_INFINITY, by0 = Double.POSITIVE_INFINITY, bx1 = Double.NEGATIVE_INFINITY, by1 = Double.NEGATIVE_INFINITY;
        double[] p = new double[2];
        int m = FOOTPRINT_POINTS - 1;
        for (int j = 0; j <= m; j++) {
            for (int i = 0; i <= m; i++) {
                if (!proj.plotOf(Math.max(-90, Math.min(90, lat0 + (lat1 - lat0) * j / m)), lon0 + (lon1 - lon0) * i / m, p)) return true;
                bx0 = Math.min(bx0, p[0]); bx1 = Math.max(bx1, p[0]);
                by0 = Math.min(by0, p[1]); by1 = Math.max(by1, p[1]);
            }
        }
        double bow = Math.max(bx1 - bx0, by1 - by0) / 8;
        return bx1 + bow >= x0 && bx0 - bow <= x0 + side && by1 + bow >= y0 && by0 - bow <= y0 + side;
    }

    /* =====================================================================
       THE CELLS PASS AND THE SNAP (the world's build)
       ===================================================================== */

    /** The cells pass and the snap, once (World.build() asks). */
    void build() {
        if (built) return;
        synchronized (this) {
            if (built) return;
            cellsPass();
            snap();
            built = true;
        }
    }

    /* =====================================================================
       THE SEA'S DEPTH (0.7.121, batch E4; spec-earth 6's E4, E2's star 7)

       The generated world's sea has a depth from its elevation field
       (World.depthAt(), 0.7.79): its shallowest SHELF_SHARE is the shelf,
       SHELF_BREAK_M deep at its edge, the depth linear in the elevation from
       the sea's level and on at the same slope. earth-1 ships no heights
       (Jerus's eighth answer: no elevation), and E3 lists oil fields on the
       Earth's shelf, basin and rift seas, so an Earth city's first platform -
       and before it the land wells' count of the dry sites, which asks every
       field's depth - threw.

       THE SAME RULE, WITH THE COAST'S DISTANCE FOR THE ELEVATION IT LACKS
       (★E4-1): the sea nearest the coast is the shallowest. A sea plot's
       depth is SHELF_BREAK_M x its distance from the coast over SHELF_WIDTH_M,
       the distance within which SHELF_SHARE of the Earth's sea lies - the
       generated world's shelf, measured the generated world's way (a share
       of the sea's samples, here SHELF_SAMPLES points even over the sphere)
       on earth-1's own coast; linear from the coast and on at the same slope.
       No figure is added that the generated world does not already use: the
       shelf's share of the sea and its edge's depth (Harris et al. 2014, the
       mean shelf break), and the rest is measured on the data.

       THE COAST is the data's: the centre of the nearest 15" cell whose sea
       share is under SEA_LEVEL - the half-contour the plots' coast is drawn
       on, a plot or two off it (THE RULE above); measured on the plots
       instead, a depth would move by under a metre at this slope. The
       distance is the great circle's, the world's own within 0.3% at
       1,000 km from the spot (EarthProj). A sea plot is at least a plot from
       the coast, so its depth is over nothing, as World.depthAt() promises
       of every SALT plot; past the planet's rim, the farthest anything lies.
       ===================================================================== */

    /**
     * The distance within which SHELF_SHARE of the Earth's sea lies from the
     * coast, metres: 54,167, measured on earth-1 (measureShelfWidthM(): 46,434
     * of SHELF_SAMPLES points the sea's, 54,167.3 m; EarthFoundingCheck holds
     * it to the file) - where a sea plot is World.SHELF_BREAK_M deep, so a
     * platform (sectors.Oil.PLATFORM_MAX_DEPTH_M) stands within 58 km of a
     * coast (★E4-1).
     */
    public static final double SHELF_WIDTH_M = 54_167;

    /** Points the shelf's width is measured over: 2^16, evenly on the sphere by a fixed hash (SHELF_SALT), of which the sea's (about 71%) are measured - the generated world's measure is a share of its sea's samples too (World.SEA_SAMPLES a cell). */
    public static final int SHELF_SAMPLES = 1 << 16;

    /** How far a sample's coast is looked for while the width is measured, metres: 250 km, past which a sample is beyond the shelf by far - over four times its width (the measure is a low quantile; depthAt() itself looks as far as it must). */
    public static final double SHELF_MEASURE_REACH_M = 250_000;

    /** The stream the shelf's sample points are drawn from. */
    private static final long SHELF_SALT = 0x5E1F5A3DL;

    /** The sea layer's levels under which a 15" cell is the coast's land side: the share under SEA_LEVEL, 0 to 7 of 15. */
    static final int COAST_BELOW = (int) Math.ceil(SEA_LEVEL * EarthData.SHARE_LEVELS);

    /** Depths kept, by plot: 4,096 (a field's depth is asked again every time the city's ground changes). */
    static final int DEPTHS_KEPT = 4096;

    private final Map<Long, Double> depths = new LinkedHashMap<>(256, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, Double> eldest) {
            return size() > DEPTHS_KEPT;
        }
    };

    /**
     * The sea's depth at a plot's centre, metres below its level (★E4-1): 0
     * where the plot is not SALT, else World.SHELF_BREAK_M x its distance
     * from the coast (coastDistanceM(), at least a plot) over SHELF_WIDTH_M.
     * World.depthAt() on Earth. Kept by plot (DEPTHS_KEPT).
     */
    public double depthAt(long x, long y) {
        if (terrainAt(x, y) != World.SALT) return 0;
        long k = (x << 32) ^ (y & 0xffffffffL);
        synchronized (depths) {
            Double d = depths.get(k);
            if (d != null) return d;
        }
        double[] ll = new double[2];
        double m = proj.latLonOfPlot(x + 0.5, y + 0.5, ll)
                ? Math.max(World.PLOT_M, coastDistanceM(data, ll[0], ll[1], Double.POSITIVE_INFINITY))
                : StrictMath.PI * EarthProj.RADIUS_M;
        double depth = World.SHELF_BREAK_M * m / SHELF_WIDTH_M;
        synchronized (depths) { depths.put(k, depth); }
        return depth;
    }

    /** ...a point's distance from the coast, metres (see the banner): the great circle to the centre of the nearest 15" cell whose sea share is under SEA_LEVEL. */
    public double coastDistanceM(double lat, double lon) {
        return coastDistanceM(data, lat, lon, Double.POSITIVE_INFINITY);
    }

    /**
     * The great-circle distance from (lat, lon) to the centre of the nearest
     * 15" cell of the data whose sea share is under SEA_LEVEL (COAST_BELOW),
     * metres; infinity when there is none within reachM. Best first over the
     * 1-degree tiles, nearest the point by a bound that never overstates
     * (the angle to the tile's centre less its corners' farthest), a tile of
     * one value read without inflating it, until no tile left can hold a
     * nearer cell.
     */
    static double coastDistanceM(EarthData data, double lat, double lon, double reachM) {
        EarthData.Layer sea = data.layer("sea");
        int cpd = sea.cellsPerDegree;
        double[] p = new double[3];
        unitVector(lat, lon, p);
        double best = Math.min(StrictMath.PI, reachM / EarthProj.RADIUS_M);   // the angle to beat, radians: at most half a turn
        boolean[] seen = new boolean[EarthData.TILES];
        java.util.PriorityQueue<double[]> queue = new java.util.PriorityQueue<>((a, b) -> Double.compare(a[0], b[0]));
        int first = EarthData.tileAt(lat, lon);
        seen[first] = true;
        queue.add(new double[] { 0, first });
        double[] c = new double[3], cosLon = new double[cpd], sinLon = new double[cpd];
        while (!queue.isEmpty()) {
            double[] q = queue.poll();
            if (q[0] >= best) break;
            int t = (int) q[1];
            int lat0 = EarthData.tileLat0(t), lon0 = EarthData.tileLon0(t);
            int e = sea.entries[t];
            if (e > 0 || -e < COAST_BELOW) {
                byte[] g = e > 0 ? data.raw("sea", t) : null;
                for (int j = 0; j < cpd; j++) {
                    double l = StrictMath.toRadians(lon0 + (j + 0.5) / cpd);
                    cosLon[j] = StrictMath.cos(l);
                    sinLon[j] = StrictMath.sin(l);
                }
                double most = StrictMath.cos(best);
                for (int r = 0; r < cpd; r++) {
                    double f = StrictMath.toRadians(lat0 + 1 - (r + 0.5) / cpd);
                    double cf = StrictMath.cos(f), sf = StrictMath.sin(f);
                    for (int j = 0; j < cpd; j++) {
                        if (g != null && (g[r * cpd + j] & 0xFF) >= COAST_BELOW) continue;
                        double dot = cf * (cosLon[j] * p[0] + sinLon[j] * p[1]) + sf * p[2];
                        if (dot > most) most = dot;
                    }
                }
                best = Math.min(best, StrictMath.acos(Math.min(1, most)));
            }
            // The tiles beside it - every one along a polar row, which all meet at the pole.
            int row = (lat0 + 90), col = lon0 + 180;
            for (int dr = -1; dr <= 1; dr++) {
                int rr = row + dr;
                if (rr < 0 || rr >= 180) continue;
                if ((rr == 0 || rr == 179) && dr != 0) {
                    for (int cc = 0; cc < 360; cc++) offer(queue, seen, rr * 360 + cc, p, c, best);
                    continue;
                }
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    offer(queue, seen, rr * 360 + Math.floorMod(col + dc, 360), p, c, best);
                }
            }
        }
        return Double.isInfinite(reachM) || best < reachM / EarthProj.RADIUS_M ? best * EarthProj.RADIUS_M : Double.POSITIVE_INFINITY;
    }

    /** A tile into the queue once, by the least angle any point of it can lie from p: the angle to its centre less the farthest of its corners from that centre. */
    private static void offer(java.util.PriorityQueue<double[]> queue, boolean[] seen, int t, double[] p, double[] c, double best) {
        if (seen[t]) return;
        seen[t] = true;
        int lat0 = EarthData.tileLat0(t), lon0 = EarthData.tileLon0(t);
        unitVector(lat0 + 0.5, lon0 + 0.5, c);
        double far = 0;
        double[] k = new double[3];
        for (int i = 0; i < 4; i++) {
            unitVector(lat0 + (i >> 1), lon0 + (i & 1), k);
            far = Math.max(far, angle(c, k));
        }
        double bound = Math.max(0, angle(p, c) - far - 1e-12);
        if (bound < best) queue.add(new double[] { bound, t });
    }

    /** A point's unit vector on the sphere. */
    private static void unitVector(double lat, double lon, double[] v) {
        double f = StrictMath.toRadians(lat), l = StrictMath.toRadians(lon), cf = StrictMath.cos(f);
        v[0] = cf * StrictMath.cos(l);
        v[1] = cf * StrictMath.sin(l);
        v[2] = StrictMath.sin(f);
    }

    /** The angle between two unit vectors, radians. */
    private static double angle(double[] a, double[] b) {
        return StrictMath.acos(Math.max(-1, Math.min(1, a[0] * b[0] + a[1] * b[1] + a[2] * b[2])));
    }

    /**
     * The shelf's width on a reader's data (★E4-1), metres: SHELF_SAMPLES
     * points evenly on the sphere (a fixed hash: latitude by the arcsine of
     * an even draw, longitude even), the sea's among them - its 15" cell's
     * share at SEA_LEVEL or over - each its coastDistanceM() (out to
     * SHELF_MEASURE_REACH_M), sorted; the distance SHELF_SHARE of them lie
     * within. What SHELF_WIDTH_M is, to the metre it is rounded to.
     */
    public static double measureShelfWidthM(EarthData data) {
        double[] d = new double[SHELF_SAMPLES];
        int n = 0;
        for (int i = 0; i < SHELF_SAMPLES; i++) {
            long h = World.mix(SHELF_SALT + i * 0x9E3779B97F4A7C15L);
            double lat = StrictMath.toDegrees(StrictMath.asin(2 * World.unit(h) - 1));
            double lon = 360 * World.unit(World.mix(h)) - 180;
            if (data.at("sea", lat, lon) < COAST_BELOW) continue;
            d[n++] = coastDistanceM(data, lat, lon, SHELF_MEASURE_REACH_M);
        }
        double[] sea = Arrays.copyOf(d, n);
        Arrays.sort(sea);
        return sea[(int) (World.SHELF_SHARE * n)];
    }

    /** How many of the shelf's samples are the sea's, on a reader's data. */
    public static int shelfSeaSamples(EarthData data) {
        int n = 0;
        for (int i = 0; i < SHELF_SAMPLES; i++) {
            long h = World.mix(SHELF_SALT + i * 0x9E3779B97F4A7C15L);
            double lat = StrictMath.toDegrees(StrictMath.asin(2 * World.unit(h) - 1));
            double lon = 360 * World.unit(World.mix(h)) - 180;
            if (data.at("sea", lat, lon) >= COAST_BELOW) n++;
        }
        return n;
    }

    /**
     * Each world cell's land and forest, as shares of its area (spec-earth
     * 4): CELL_SAMPLES x CELL_SAMPLES points a cell, each read from the 4'
     * far layers - its land the ground that is not sea, dry and lake (the
     * generated world's land holds its lakes too) - and its forest the dry
     * share times land cover's forest level; a point off the planet holds
     * neither. Each cell's points from its four corners, blended
     * (EarthProj.blend()). What the fields are drawn on (World's deposits)
     * and the far views ask; 0.39 to 0.42 s (EarthTerrainCheck 9; the study 0.37 to 0.45).
     *
     * Since 0.7.117 (batch E3) each point also reads its province
     * (Geology): a resource's FIELD LAND is the points' land times the
     * province's multiplier (Geology.multiplier(): w x Z, and oil's
     * petroleum factor), oil's with the sea of a shelf, basin or rift
     * province too - what World.countOf() draws an Earth cell's fields on,
     * so a cell across two provinces takes each its share (spec-earth 4).
     */
    public void cellsPass() {
        long t0 = System.nanoTime();
        int cells = World.CELLS, n = cells * cells, m = CELL_SAMPLES;
        double[] land = new double[n], forest = new double[n];
        Geology geo = geology();
        Resource[] kinds = Resource.values();
        float[][] fields = new float[kinds.length][];
        double[][] mult = new double[kinds.length][];
        for (Resource r : kinds) {
            if (!r.inFields()) continue;
            fields[r.ordinal()] = new float[n];
            mult[r.ordinal()] = new double[geo.rows()];
            for (int row = 0; row < geo.rows(); row++) mult[r.ordinal()][row] = geo.multiplier(r, row);
        }
        boolean[] seaOil = new boolean[geo.rows()];
        for (int row = 0; row < geo.rows(); row++) seaOil[row] = geo.seaHoldsOil(row);
        double[] sum = new double[kinds.length];
        int oil = Resource.OIL.ordinal();
        EarthData.Layer farLand = data.layer("far.land"), farLake = data.layer("far.lake"), cover = data.layer("landcover");
        int fcpd = farLand.cellsPerDegree, ccpd = cover.cellsPerDegree, fcols = 360 * fcpd;
        byte[][] landBands = new byte[180][], lakeBands = new byte[180][];
        int[] landUniform = new int[180], lakeUniform = new int[180];
        boolean[] bandRead = new boolean[180];
        byte[][] covers = new byte[EarthData.TILES][];
        boolean[] coverRead = new boolean[EarthData.TILES];
        double[] ll = new double[2];
        for (int c = 0; c < n; c++) {
            long x0 = (long) (c % cells) * World.CELL, y0 = (long) (c / cells) * World.CELL;
            EarthProj.Blend b = proj.blend(x0, y0, World.CELL);
            double l = 0, f = 0;
            Arrays.fill(sum, 0);
            for (int j = 0; j < m; j++) {
                for (int i = 0; i < m; i++) {
                    if (!b.latLon(x0 + (i + 0.5) * World.CELL / m, y0 + (j + 0.5) * World.CELL / m, ll)) continue;
                    long r = Math.min(180L * fcpd - 1, Math.max(0, (long) Math.floor((90 - ll[0]) * fcpd)));
                    long col = Math.floorMod((long) Math.floor((ll[1] + 180) * fcpd), (long) fcols);
                    int band = (int) (179 - r / fcpd);
                    if (!bandRead[band]) {
                        bandRead[band] = true;
                        int e = farLand.entries[band];
                        if (e <= 0) landUniform[band] = -e; else landBands[band] = data.raw("far.land", band);
                        e = farLake.entries[band];
                        if (e <= 0) lakeUniform[band] = -e; else lakeBands[band] = data.raw("far.lake", band);
                    }
                    int at = (int) ((r % fcpd) * fcols + col);
                    int dry = landBands[band] == null ? landUniform[band] : landBands[band][at] & 0xFF;
                    int wet = lakeBands[band] == null ? lakeUniform[band] : lakeBands[band][at] & 0xFF;
                    double pl = (dry + wet) / (double) EarthData.FAR_LEVELS;
                    l += pl;
                    int row = geo.provinceAt(ll[0], ll[1]);
                    for (int k = 0; k < kinds.length; k++) {
                        if (mult[k] == null) continue;
                        double a = k == oil && seaOil[row] ? pl + Math.max(0, EarthData.FAR_LEVELS - dry - wet) / (double) EarthData.FAR_LEVELS : pl;
                        sum[k] += a * mult[k][row];
                    }
                    if (dry == 0) continue;
                    long cr = Math.min(180L * ccpd - 1, Math.max(0, (long) Math.floor((90 - ll[0]) * ccpd)));
                    long cc = Math.floorMod((long) Math.floor((ll[1] + 180) * ccpd), 360L * ccpd);
                    int tile = (int) ((179 - cr / ccpd) * 360 + cc / ccpd);
                    if (!coverRead[tile]) {
                        coverRead[tile] = true;
                        if (cover.entries[tile] > 0) covers[tile] = data.raw("landcover", tile);
                    }
                    int v = covers[tile] == null ? -Math.min(0, cover.entries[tile]) : covers[tile][(int) ((cr % ccpd) * ccpd + cc % ccpd)] & 0xFF;
                    f += dry / (double) EarthData.FAR_LEVELS * (v & EarthData.LC_FOREST) / EarthData.SHARE_LEVELS;
                }
            }
            land[c] = Math.min(1, l / (m * m));
            forest[c] = Math.min(1, f / (m * m));
            for (int k = 0; k < kinds.length; k++) if (fields[k] != null) fields[k][c] = (float) (sum[k] / (m * m));
        }
        cellLand = land;
        cellForest = forest;
        cellFields = fields;
        cellsMs = (System.nanoTime() - t0) / 1e6;
    }

    /** The geology of its data (Geology.of()): the provinces, their classes and multipliers. */
    public Geology geology() { return Geology.of(data); }

    /**
     * A cell's FIELD LAND for a resource, as a share of its area (0.7.117):
     * its 16 points' land each times its province's Geology.multiplier(),
     * oil's with a shelf's, basin's or rift's sea - what today's density is
     * taken over (World.countOf()). Its land, cellLand(), for forest.
     */
    double cellFieldLand(int cell, Resource r) {
        build();
        float[] f = cellFields[r.ordinal()];
        return f == null ? cellLand[cell] : f[cell];
    }

    /** The province under a point in plot coordinates, its row in the data; 0 off the planet or in none. */
    public int provinceAt(double x, double y) {
        double[] ll = new double[2];
        if (!proj.latLonOfPlot(x, y, ll)) return 0;
        return geology().provinceAt(ll[0], ll[1]);
    }

    /** How long the cells pass took, in ms (the last time it ran). */
    public double cellsPassMs() { return cellsMs; }

    /** A cell's land, as a share of its area: the ground that is not sea (World.cellLandShare()). */
    double cellLand(int cell) { build(); return cellLand[cell]; }

    /** A cell's forest, as a share of its area. */
    double cellForest(int cell) { build(); return cellForest[cell]; }

    /**
     * THE SNAP (spec-earth 2): from the spot's plot - the projection's
     * centre is the corner of four, and this is the one south-east of it,
     * (SIDE/2, SIDE/2) - the nearest plot that passes the founding's test 1,
     * dry ground with eight dry points World.SITE_DRY_PLOTS round it, ring by
     * ring of one plot (a ring the plots k to k + 1 plots off, nearest first,
     * then north to south and west to east), out to SNAP_PLOTS. Dry is the
     * ground as drawn (World.dry()): not sea, not fresh water, rivers
     * included, and since 0.7.127 not ice, which is never a founding site.
     */
    private void snap() {
        long[] s = siteNear();
        siteTests = (int) s[2];
        if (s[0] < 0) throw new IllegalStateException("no dry ground within " + SNAP_PLOTS + " plots of (" + key.lat() + ", " + key.lon() + ") to found a city on");
        siteX = s[0];
        siteY = s[1];
    }

    private volatile long[] site;

    /**
     * The snap's answer without the cells pass (0.7.121, batch E4: the
     * founding card's first question, and Random spot's test): {x, y, plots
     * tested}, x and y -1 when no plot within SNAP_PLOTS passes - a spot in
     * the sea, or on an island too small to found on. Worked out once.
     */
    public long[] siteNear() {
        long[] s = site;
        if (s != null) return s.clone();
        long cx = World.SIDE / 2, cy = World.SIDE / 2;
        int tests = 0;
        List<long[]> ring = new ArrayList<>();
        for (int k = 0; k <= SNAP_PLOTS; k++) {
            ring.clear();
            long lo = (long) k * k, hi = (long) (k + 1) * (k + 1);
            for (int dy = -(k + 1); dy <= k + 1; dy++) {
                for (int dx = -(k + 1); dx <= k + 1; dx++) {
                    long d2 = (long) dx * dx + (long) dy * dy;
                    if (d2 >= lo && d2 < hi) ring.add(new long[] { d2, dy, dx });
                }
            }
            ring.sort((p, o) -> p[0] != o[0] ? Long.compare(p[0], o[0]) : p[1] != o[1] ? Long.compare(p[1], o[1]) : Long.compare(p[2], o[2]));
            for (long[] p : ring) {
                tests++;
                if (siteDry(cx + p[2], cy + p[1])) {
                    site = new long[] { cx + p[2], cy + p[1], tests };
                    return site.clone();
                }
            }
        }
        site = new long[] { -1, -1, tests };
        return site.clone();
    }

    /** The founding's test 1 at plot (px, py): it and eight points World.SITE_DRY_PLOTS round its centre are dry ground. */
    boolean siteDry(long px, long py) {
        if (!dry(px, py)) return false;
        double x = px + 0.5, y = py + 0.5;
        for (int q = 0; q < 8; q++) {
            double a = q * Math.PI / 4;
            if (!dry((long) Math.floor(x + World.SITE_DRY_PLOTS * StrictMath.cos(a)), (long) Math.floor(y + World.SITE_DRY_PLOTS * StrictMath.sin(a)))) return false;
        }
        return true;
    }

    private boolean dry(long x, long y) {
        return World.dry(terrainAt(x, y));
    }

    /** The founding site, as a plot: the snap's. */
    long siteX() { build(); return siteX; }

    /** ...and its row. */
    long siteY() { build(); return siteY; }

    /** How many plots the snap tested. */
    int siteTests() { build(); return siteTests; }

    /** The plot the snap starts from: the spot's, (SIDE/2, SIDE/2). */
    public static long spotPlot() { return World.SIDE / 2; }
}
