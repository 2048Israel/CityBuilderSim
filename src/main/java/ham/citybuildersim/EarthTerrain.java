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
 *   - SAND where a dry plot has the sea on one of its eight sides and its 2'
 *     cell's bare ground is 2% of its land or more (EarthData.LC_BEACH);
 *   - FOREST where the forest noise ranks under the forest share of the dry
 *     ground; GRASS the rest. Real land use is ignored: the city starts on
 *     unsettled ground.
 * A plot (terrainAt()) is its tile's, from a kept tile, and a line
 * (lineTerrain()) its tile's byte for byte, drawn alone where the tile is not
 * kept (draw()); a region at a stride (regionTerrain()) classes each pixel's
 * centre from the shares there and the octaves wider than a pixel, the
 * narrower at their mean - World's rule for its far view.
 *
 * THE WORLD'S OTHER HALF. The cells pass (cellsPass()) replaces the
 * generated world's sea pass: each world cell's land and forest from
 * CELL_SAMPLES x CELL_SAMPLES points of the 4' layers, which the fields of
 * ore and oil are drawn on as the generated world's are on its cells. The
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
 * E2 builds it unreachable from the founding screen (E4 offers Earth): a
 * generated world never reaches it. EarthTerrainCheck holds it on the three
 * windows of EarthCheck's fixture and, when it is named, on the full file.
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
    private static final long F_COAST = 0xC0A57L, F_FRESH = 0xF4E54L, F_FOREST = 0xF0E57L, F_BLOB = 0xB10BL;

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
    /** Each field's octave streams: COAST, FRESH, FOREST, BLOB, octave k at [f][k]. */
    private final long[][] os = new long[4][NOISE_HI + 1];
    /** Each noise field's row in os: the coast's, fresh water's, the forest's and the small lakes' blobs'. */
    private static final int COAST = 0, FRESH = 1, FOREST = 2, BLOB = 3;

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
        long[] fields = { F_COAST, F_FRESH, F_FOREST, F_BLOB };
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

        /** ...the forest level of land cover, 0 to 15, interpolated the same way. */
        double bilinearLow(double lat, double lon) {
            double g = (90 - lat) * cpd - 0.5, c = (lon + 180) * cpd - 0.5;
            long r0 = (long) Math.floor(g), c0 = (long) Math.floor(c);
            double fr = g - r0, fc = c - c0;
            int m = EarthData.LC_FOREST;
            double top = (value(r0, c0) & m) * (1 - fc) + (value(r0, c0 + 1) & m) * fc;
            double bottom = (value(r0 + 1, c0) & m) * (1 - fc) + (value(r0 + 1, c0 + 1) & m) * fc;
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
        final double[] lat = new double[19 * 19];
        final double[] seaL = new double[LATTICE * LATTICE], lakeL = new double[LATTICE * LATTICE], forestL = new double[LATTICE * LATTICE];
        final boolean[] sea = new boolean[RING_AREA];
        final double[] ll = new double[2];
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
        double[] ll = s.ll;
        for (int j = j0; j <= j1; j++) {
            for (int i = i0; i <= i1; i++) {
                int at = j * LATTICE + i;
                double px = x0 - LATTICE_STEP + LATTICE_STEP * i + 0.5, py = y0 - LATTICE_STEP + LATTICE_STEP * j + 0.5;
                if (!b.latLon(px, py, ll)) {
                    s.seaL[at] = 1;
                    s.lakeL[at] = 0;
                    s.forestL[at] = 0;
                } else {
                    s.seaL[at] = s.seaR.bilinear(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
                    s.lakeL[at] = s.lakeR.bilinear(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
                    s.forestL[at] = s.coverR.bilinearLow(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
                }
                anySea |= s.seaL[at] > 0;
                allSea &= s.seaL[at] >= 1;
                anyLake |= s.lakeL[at] > 0;
                anyForest |= s.forestL[at] > 0;
                allForest &= s.forestL[at] >= 1;
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
                if (anySea && (s.sea[r - rw - 1] || s.sea[r - rw] || s.sea[r - rw + 1] || s.sea[r - 1] || s.sea[r + 1]
                        || s.sea[r + rw - 1] || s.sea[r + rw] || s.sea[r + rw + 1]) && beach(s, b, x, y)) {
                    out[i] = World.SAND;
                    continue;
                }
                boolean forest = allForest || (forestNoise && s.forest[i] < below(q, lattice(s.forestL, ox + px, oy + py)));
                out[i] = forest ? World.FOREST : World.GRASS;
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
        if (x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) return World.SALT;
        double[] ll = s.ll;
        if (!proj.latLonOfPlot(x, y, ll)) return World.SALT;
        double sea, lake, forest;
        if (stride >= FAR_STRIDE) {
            double dry = s.farLandR.at(ll[0], ll[1]) / (double) EarthData.FAR_LEVELS;
            lake = s.farLakeR.at(ll[0], ll[1]) / (double) EarthData.FAR_LEVELS;
            sea = Math.max(0, 1 - dry - lake);
            forest = (s.coverR.at(ll[0], ll[1]) & EarthData.LC_FOREST) / (double) EarthData.SHARE_LEVELS;
        } else {
            sea = s.seaR.bilinear(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
            lake = s.lakeR.bilinear(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
            forest = s.coverR.bilinearLow(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
        }
        if (sea > 0 && sea(sea, noiseAt(os[COAST], x, y, stride))) return World.SALT;
        if (stride < WATER_STRIDE) {
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
        if (forest >= 1 || (forest > 0 && noiseAt(os[FOREST], x, y, stride) < below(q, forest))) return World.FOREST;
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
     * ground as drawn: not sea and not fresh water, rivers included.
     */
    private void snap() {
        long cx = World.SIDE / 2, cy = World.SIDE / 2;
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
                siteTests++;
                if (siteDry(cx + p[2], cy + p[1])) {
                    siteX = cx + p[2];
                    siteY = cy + p[1];
                    return;
                }
            }
        }
        throw new IllegalStateException("no dry ground within " + SNAP_PLOTS + " plots of (" + key.lat() + ", " + key.lon() + ") to found a city on");
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
        byte t = terrainAt(x, y);
        return t != World.SALT && t != World.FRESH;
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
