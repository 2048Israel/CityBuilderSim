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
import java.util.WeakHashMap;

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
 *   - SHALLOW (ET1, spec-earth2-terrain 6.1) for a sea plot where the shallow
 *     sea's share of the sea there - earth-2's shallow layer, interpolated
 *     between the 15" cells' centres WEIGHTED BY EACH CELL'S SEA, so a coast
 *     grows no fringe of deep sea - ranks over the shallow noise (its own,
 *     ranked as the forest's is, so it covers in expectation what the data
 *     gives it); the rest of the sea is SALT. Only on data that has the
 *     layer (EarthData.hasRelief()): an earth-1 file draws none;
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
 * THE CAMERA (0.7.132, batch E8; docs/studies/spec-globe.md 3.5). The far
 * rule reaches the planet through a camera (Camera): where a pixel is, where
 * a river's vertex lands and which tiles it reads. The city's plane's is the
 * far node, byte for byte as before (plane()); a latitude-longitude grid's
 * makes the planet's picture (grid(), GlobePicture); the globe's,
 * orthographic round a view centre with a roll, draws GlobeFrame's GROUND
 * tier (globe()). On the globe the forest is dithered from a fixed
 * latitude-longitude grid at the pixel's scale (globeDither()), so it moves
 * with the ground as the globe turns. GlobeCheck holds them.
 *
 * THE RELIEF (ET1, 2026-10-24; docs/studies/spec-earth2-terrain.md 3.1). On
 * earth-2's data a plot carries a second value beside its cover: how steep
 * its ground is - World.FLAT, FOOTHILL, STEEP or VERY_STEEP (a grade of under
 * 10%, to 30%, to 60%, and over), read with reliefAt(), tileRelief() and
 * regionRelief() and, through a camera, farTerrain(camera, cover, relief).
 * It is drawn as the forest is - the data's shares of the land at the plot
 * (the slope layer, a 15" cell holding the shares at or above each grade),
 * ranked over a noise of its own, one noise for the three so the classes
 * nest - on the plots whose cover is dry ground, and nowhere else: water and
 * ICE carry FLAT, and forest keeps its cover on any slope. It never touches
 * the cover; a generated world and an earth-1 file are FLAT throughout.
 * THE RELIEF below has the rule, its two copies and the far view's.
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
 * Since 0.8.12 (batch EG1; spec-earth2-terrain 8.5) the pass reads the point
 * through PointWeights, so iron's weight is the point's (a recorded iron
 * district, Geology.multiplierAt()) and World can draw a cluster's centre among
 * a cell's 16 points by the same weights (pointFieldWeights()) and keep a
 * centre or a field only on ground that holds its resource (fieldGround()):
 * a cluster stays in its province's own cells.
 *
 * WHICH DATA. A world's key names its data by identity (EarthData.identity(),
 * "earth-2:<hash>"); a harness registers its EarthData with use(), and the
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

    /** How far past the spot the nearest buildable ground is looked for when the snap finds none (ET2, spec-earth2-terrain 3.4): 1,667 plots (50 km); past it the founding card says there is none. */
    public static final int BEYOND_PLOTS = 1667;

    /** The step of that search (ET2): a lattice of spots 8 plots (240 m) apart, each tried by the founding's test 1, so the nearest it reports is within a lattice step of the true nearest - cheap where the snap's ring of one plot is not, over a disc 100 times the snap's. */
    public static final int BEYOND_STEP = 8;

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

    /**
     * How much wider the relief's noise is than the cover noises' (ET1, spec-earth2-terrain 3.1): its octaves are
     * theirs scaled by 2^RELIEF_SHIFT, 2^3 plots (240 m) down to 2^6 (1.9 km) at 2, so a slope is drawn as a
     * connected patch of a few hundred metres to a kilometre or two - a hillside, not the forest's speckle, whose
     * widest patch is 480 m - and the data's 15" cell (463 m), which holds the share, holds two or three of them.
     * Wider (3: to 3.8 km) the share a 61 km window draws strays from the data's by more than the cover's
     * tolerance, the realised share of a noise having as few independent patches in a window as the window
     * holds (EarthTerrainCheck 17 prints the spread); narrower, the slope speckles. The same table ranks it as
     * the cover noises: a value noise's distribution does not depend on its scale.
     */
    public static final int RELIEF_SHIFT = 2;

    /** The relief noise's lattice scale as a divisor of plot coordinates: 2^RELIEF_SHIFT, 4 (the noise is the cover noises' at x / 4, y / 4). */
    private static final double RELIEF_SCALE = 1 << RELIEF_SHIFT;

    /** Tiles of relief kept for reliefAt() and tileRelief(): 1,024 (1 MB), a tile of ground's partner (TILES_KEPT). */
    static final int RELIEF_KEPT = 1024;

    /** Cells a side of the block a far pixel reads the slope of from FAR_STRIDE: 16 (4', 7.4 km), the 4' far layers' cell - 15 of them a side of a 1-degree tile, 225 a tile. */
    static final int FAR_BLOCK = 16;

    /* The fields' keys: what makes one class's noise another's. */
    private static final long F_COAST = 0xC0A57L, F_FRESH = 0xF4E54L, F_FOREST = 0xF0E57L, F_BLOB = 0xB10BL, F_ICE = 0x1CE5L, F_DESERT = 0xDE5E7L, F_SHALLOW = 0x5A110L, F_RELIEF = 0x4E11EFL;

    /** The stream a far pixel's relief dither is drawn from: the camera's dither (the forest's) remixed with it, for the two must not be one number, or the steep ground would be the forested. */
    private static final long F_RELIEF_DITHER = 0x4E11D17L;

    /** A share layer's levels squared, 15 x 15: what bilinearProduct() of two of them is over to be a share of the area (ET1). */
    private static final double SHARE_AREA = (double) EarthData.SHARE_LEVELS * EarthData.SHARE_LEVELS;

    /** The stream the ranking table's samples are drawn from. */
    private static final long QUANTILE_SALT = 0x0A1E5A3DL;

    /** Each octave's amplitude over the sum of them, octave k at AMP_N[k]. */
    private static final double[] AMP_N = new double[NOISE_HI + 1];

    /** A slope byte's shares of the land at or above foothill, steep and very steep, by byte (ET1): EarthData's decoders, the steep share held within the foothill share and the very steep within the steep - the file promises both (FORMAT-2.txt), and the order of the classes drawn from them rests on it. */
    private static final double[] SLOPE_F = new double[256], SLOPE_S = new double[256], SLOPE_V = new double[256];

    /** The smoothstep at a plot's centre within octave k's lattice cell, SMOOTH[k][f] for f = 0 .. 2^k - 1 - up to the relief's widest octave, NOISE_HI + RELIEF_SHIFT (ET1). */
    private static final double[][] SMOOTH = new double[NOISE_HI + RELIEF_SHIFT + 1][];

    static {
        double norm = World.norm(NOISE_HI, NOISE_LO);
        for (int k = NOISE_LO; k <= NOISE_HI; k++) AMP_N[k] = World.amp(NOISE_HI, k) / norm;
        for (int k = 0; k <= NOISE_HI + RELIEF_SHIFT; k++) {
            int w = 1 << k;
            SMOOTH[k] = new double[w];
            for (int f = 0; f < w; f++) {
                double t = (f + 0.5) / w;
                SMOOTH[k][f] = t * t * (3 - 2 * t);
            }
        }
        for (int b = 0; b < 256; b++) {
            SLOPE_F[b] = EarthData.slopeFoothill(b);
            SLOPE_S[b] = Math.min(SLOPE_F[b], EarthData.slopeSteep(b));
            SLOPE_V[b] = Math.min(SLOPE_S[b], EarthData.slopeVerySteep(b));
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
    /** Whether its data has the slope layer (EarthData.hasRelief()): without it every plot's relief is FLAT. */
    private final boolean relief;
    /** Each field's octave streams: COAST, FRESH, FOREST, BLOB, ICE, DESERT, SHALLOW, RELIEF, octave k at [f][k] (the relief's octave k is the lattice 2^(k + RELIEF_SHIFT) wide). */
    private final long[][] os = new long[8][NOISE_HI + 1];
    /** Each noise field's row in os: the coast's, fresh water's, the forest's, the small lakes' blobs', (0.7.127) the ice's and the desert's edges', and (ET1) the shallow sea's and the relief's. */
    private static final int COAST = 0, FRESH = 1, FOREST = 2, BLOB = 3, ICE = 4, DESERT = 5, SHALLOW = 6, RELIEF = 7;

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
        this.relief = data.hasRelief();
        long[] fields = { F_COAST, F_FRESH, F_FOREST, F_BLOB, F_ICE, F_DESERT, F_SHALLOW, F_RELIEF };
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

    /* ----- the sphere's noise (MS3): the same fields on the planet, not on the city's plane ----- */

    /**
     * The gain on each octave's deviation from its mean in the sphere's noise: sqrt(35 / 26), 1.160 (MS3). 3D value
     * noise's spread is the 2D's times sqrt(26 / 35): a lattice value's weight in the smoothstep blend has the mean
     * square E[1 - 2w + 2w^2] = 26 / 35 along an axis, and the 2D blend takes it twice (a square of it) where the 3D
     * takes it three times (a cube), so the sphere's noise scaled by this has the plane's spread and the ranking table
     * (quantile()) and the frays' rules (COAST_FRAY and the rest) hold for it unchanged. GlobeCheck 2d measures it.
     */
    static final double SPHERE_GAIN = Math.sqrt(35.0 / 26);

    /** The third lattice coordinate's hash constant, beside World.lattice()'s two: an odd number the other two are not. */
    private static final long LATTICE_Z = 0x165667B19E3779F9L;

    /** The value at one lattice point of one octave of the sphere's noise: World.lattice()'s hash with a third coordinate. */
    private static double lattice3(long s, long ix, long iy, long iz) {
        return World.unit(World.mix(s ^ (ix * 0x9E3779B97F4A7C15L) ^ (iy * 0xC2B2AE3D27D4EB4FL) ^ (iz * LATTICE_Z)));
    }

    /** 3D value noise at (x, y, z) in lattice units, smoothstep-interpolated along each axis (World.vnoise()'s, a dimension more). */
    private static double vnoise3(long s, double x, double y, double z) {
        long ix = (long) Math.floor(x), iy = (long) Math.floor(y), iz = (long) Math.floor(z);
        double xf = x - ix, yf = y - iy, zf = z - iz;
        double u = xf * xf * (3 - 2 * xf), v = yf * yf * (3 - 2 * yf), w = zf * zf * (3 - 2 * zf);
        double x00 = lattice3(s, ix, iy, iz), x10 = lattice3(s, ix + 1, iy, iz), x01 = lattice3(s, ix, iy + 1, iz), x11 = lattice3(s, ix + 1, iy + 1, iz);
        double y00 = lattice3(s, ix, iy, iz + 1), y10 = lattice3(s, ix + 1, iy, iz + 1), y01 = lattice3(s, ix, iy + 1, iz + 1), y11 = lattice3(s, ix + 1, iy + 1, iz + 1);
        double a0 = x00 + (x10 - x00) * u, a1 = x01 + (x11 - x01) * u, b0 = y00 + (y10 - y00) * u, b1 = y01 + (y11 - y01) * u;
        double c0 = a0 + (a1 - a0) * v, c1 = b0 + (b1 - b0) * v;
        return c0 + (c1 - c0) * w;
    }

    /**
     * One field's noise at a point of the planet (MS3), noiseAt()'s octaves on the sphere: the point (x, y, z) in plots
     * (the point's unit vector times RADIUS_M / PLOT_M), each octave k 3D value noise at the point over 2^k at AMP_N[k],
     * each octave's deviation from 0.5 scaled by SPHERE_GAIN, and, as noiseAt() has it, an octave no wider than `stride`
     * at its mean, 0.5. The planet's noise, not a view's: it depends on the point, the field's streams and the stride,
     * not on the projection the point was found by, so a pixel read from two charts reads the same value.
     */
    static double sphereNoise(long[] os, double x, double y, double z, double stride) {
        double v = 0;
        for (int k = NOISE_HI; k >= NOISE_LO; k--) {
            double w = 1L << k;
            v += AMP_N[k] * (w > stride ? 0.5 + SPHERE_GAIN * (vnoise3(os[k], x / w, y / w, z / w) - 0.5) : 0.5);
        }
        return v;
    }

    /** Two independent noises mixed at share t of the second (0 to 1) with the spread held at the plane's: 0.5 + ((1 - t) (plane - 0.5) + t (sphere - 0.5)) / sqrt((1 - t)^2 + t^2) - the plane's alone at 0, the sphere's alone at 1. */
    static double blendNoise(double plane, double sphere, double t) {
        return 0.5 + ((1 - t) * (plane - 0.5) + t * (sphere - 0.5)) / Math.sqrt((1 - t) * (1 - t) + t * t);
    }

    /** A cover's noise at a point: the plane's noiseAt() at (x, y), unless a chart is drawing on this thread (the scratch's), when the sphere's at the pixel it is aimed at, blended in from the plane's near the hand-over. */
    private static double noise(Scratch s, long[] os, double x, double y, double stride) {
        return s.chart == null ? noiseAt(os, x, y, stride) : s.chart.noise(os, 1, stride);
    }

    /** ...the relief's, at the point over RELIEF_SCALE and the stride over it: noiseAt()'s arithmetic as the relief has always read it, unless a chart is drawing. */
    private static double reliefNoise(Scratch s, long[] os, double x, double y, double stride) {
        return s.chart == null ? noiseAt(os, x / RELIEF_SCALE, y / RELIEF_SCALE, stride / RELIEF_SCALE) : s.chart.noise(os, RELIEF_SCALE, stride);
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
         * ...the product of this layer's level and `sea`'s at each cell, both
         * 0 to 15, interpolated the same way, so 0 to 15 x 15 (ET1): the
         * shallow sea's share of a cell's AREA when this is the shallow layer
         * and `sea` the sea layer (the two lie on one grid of 15" cells).
         * Over bilinear() of the sea, the shallow share of the sea there,
         * weighted by the sea share of each cell about the point, so a coast's
         * land cells (nought sea, nought shallow) pull no share down and
         * draw no fringe of deep sea along the shore (the study's 12, 6).
         */
        double bilinearProduct(Reader sea, double lat, double lon) {
            double g = (90 - lat) * cpd - 0.5, c = (lon + 180) * cpd - 0.5;
            long r0 = (long) Math.floor(g), c0 = (long) Math.floor(c);
            double fr = g - r0, fc = c - c0;
            double top = value(r0, c0) * sea.value(r0, c0) * (1 - fc) + value(r0, c0 + 1) * sea.value(r0, c0 + 1) * fc;
            double bottom = value(r0 + 1, c0) * sea.value(r0 + 1, c0) * (1 - fc) + value(r0 + 1, c0 + 1) * sea.value(r0 + 1, c0 + 1) * fc;
            return top * (1 - fr) + bottom * fr;
        }

        /**
         * ...the slope layer's (ET1, spec-earth2-terrain 3.1): the shares of the LAND
         * at a point at or above foothill, steep and very steep into out[0..2], 0 to 1,
         * each interpolated between the four cell centres about it WEIGHTED BY EACH
         * CELL'S LAND (1 - sea - lake of the cells `sea` and `lake` read, on the same
         * grid of 15" cells): a cell's byte is the share of its own land, so a cell of
         * sea or lake - which holds 0 - must pull no share down, or every steep coast
         * would draw a flat strip along the shore (the shallow sea's lesson, and
         * bilinearProduct()'s). The shares keep the file's order, steep within
         * foothill and very steep within steep, in every blend; 0 where the four
         * cells hold no land. Ice and water are the cover's to say: the slope under
         * them is read and left.
         */
        void shares(Reader sea, Reader lake, double lat, double lon, double[] out) {
            double g = (90 - lat) * cpd - 0.5, c = (lon + 180) * cpd - 0.5;
            long r0 = (long) Math.floor(g), c0 = (long) Math.floor(c);
            double fr = g - r0, fc = c - c0;
            double land = 0, f = 0, s = 0, v = 0;
            for (int k = 0; k < 4; k++) {
                long r = r0 + (k >> 1), cc = c0 + (k & 1);
                double w = ((k & 1) == 0 ? 1 - fc : fc) * ((k >> 1) == 0 ? 1 - fr : fr);
                if (w == 0) continue;
                int b = value(r, cc);
                double a = w * Math.max(0, EarthData.SHARE_LEVELS - sea.value(r, cc) - lake.value(r, cc));
                if (a == 0) continue;
                land += a;
                f += a * SLOPE_F[b];
                s += a * SLOPE_S[b];
                v += a * SLOPE_V[b];
            }
            if (land > 0) {
                out[0] = f / land;
                out[1] = s / land;
                out[2] = v / land;
            } else {
                out[0] = out[1] = out[2] = 0;
            }
        }

        /**
         * ...land cover's: the forest level, 0 to 15, interpolated the same
         * way, and (0.7.127, batch E7) its ice and desert bits each read 0 or
         * 1 at its cell and interpolated from the same four cells: out[0] the
         * level, out[1] ice's share, out[2] the desert's. The level is the
         * arithmetic the forest was read with before (the old bilinearLow,
         * which this replaced), so the forest drawn is the same to the bit.
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
        /** The shallow sea's share of the area at each lattice point (ET1): over seaL, the shallow share of the sea there. */
        final double[] shalL = new double[LATTICE * LATTICE];
        final double[] shal = new double[AREA];
        /** The relief's noise over a tile (ET1), and the slope shares at the lattice's points: at or above foothill, steep, very steep. */
        final double[] rel = new double[AREA], relF = new double[LATTICE * LATTICE], relS = new double[LATTICE * LATTICE], relV = new double[LATTICE * LATTICE];
        final double[] sh = new double[3];
        final boolean[] sea = new boolean[RING_AREA];
        final double[] ll = new double[2], cv = new double[3];
        /** The chart camera this thread is drawing (farTerrain(Camera)), or null: every noise read goes through the sphere at its aimed pixel while it is set (noise()). */
        Chart chart;
        int[] segs = new int[64], lakes = new int[16];
        final Reader seaR = new Reader(), lakeR = new Reader(), coverR = new Reader(), farLandR = new Reader(), farLakeR = new Reader(), shalR = new Reader(), slopeR = new Reader();
        EarthData bound;
        /** Whether the data bound has earth-2's layers (EarthData.hasRelief()): an earth-1 file draws no SHALLOW and no relief. */
        boolean shallow;
        /** The far shares of the bound data's slope (FarShares), the last 1-degree tile read from them and its number: one thread's, so a far view's neighbouring pixels take the lock once a tile. */
        FarShares far;
        int farTile = -1;
        byte[] farBlocks;

        void bind(EarthData d) {
            if (bound == d) return;
            seaR.bind(d, "sea");
            lakeR.bind(d, "lake");
            coverR.bind(d, "landcover");
            farLandR.bind(d, "far.land");
            farLakeR.bind(d, "far.lake");
            shallow = d.hasRelief();
            if (shallow) {
                shalR.bind(d, "shallow");
                slopeR.bind(d, EarthData.SLOPE);
                if (slopeR.cpd != seaR.cpd || slopeR.cpd != lakeR.cpd) {
                    throw new IllegalStateException("the slope layer is not on the sea's and the lakes' grid of cells (" + slopeR.cpd + " a degree against " + seaR.cpd + "): " + d.identity());
                }
            }
            far = null;
            farTile = -1;
            farBlocks = null;
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
        boolean anyIce = false, allIce = true, anyDesert = false, allDesert = true, anyShal = false;
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
                    s.shalL[at] = 0;
                } else {
                    s.seaL[at] = s.seaR.bilinear(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
                    s.lakeL[at] = s.lakeR.bilinear(ll[0], ll[1]) / EarthData.SHARE_LEVELS;
                    s.coverR.cover(ll[0], ll[1], cv);
                    s.forestL[at] = cv[0] / EarthData.SHARE_LEVELS;
                    s.iceL[at] = cv[1];
                    s.desertL[at] = cv[2];
                    // The shallow sea's share of the area (ET1), read only where there is sea and the data has the layer.
                    s.shalL[at] = s.shallow && s.seaL[at] > 0 ? s.shalR.bilinearProduct(s.seaR, ll[0], ll[1]) / SHARE_AREA : 0;
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
                anyShal |= s.shalL[at] > 0;
            }
        }
        if (allSea) {
            // All sea: SALT, or SHALLOW where the shallow share beats its noise (ET1) - the tile is the sea alone.
            if (!anyShal) {
                Arrays.fill(out, 0, w * h, World.SALT);
                return;
            }
            double[] q = quantile();
            Arrays.fill(s.shal, 0, w * h, 0);
            for (int k = NOISE_HI; k >= NOISE_LO; k--) octave(s.lat, os[SHALLOW][k], k, x0 + ox, y0 + oy, w, h, AMP_N[k], s.shal);
            for (int py = 0; py < h; py++) {
                for (int px = 0; px < w; px++) {
                    int i = py * w + px;
                    out[i] = s.shal[i] < below(q, shallowShare(s, ox + px, oy + py)) ? World.SHALLOW : World.SALT;
                }
            }
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
        // The shallow sea's detail (ET1): its own noise, ranked as the forest's is, where a plot may be sea and some of the sea is shallow.
        boolean shalNoise = anyShal && anySea;
        if (shalNoise) {
            Arrays.fill(s.shal, 0, w * h, 0);
            for (int k = NOISE_HI; k >= NOISE_LO; k--) octave(s.lat, os[SHALLOW][k], k, rx0, ry0, w, h, AMP_N[k], s.shal);
        }
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                int i = py * w + px, r = (py + 1) * rw + px + 1;
                if (s.sea[r]) {
                    out[i] = shalNoise && s.shal[i] < below(q, shallowShare(s, ox + px, oy + py)) ? World.SHALLOW : World.SALT;
                    continue;
                }
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

    /**
     * The shallow share of the sea at the tile's plot (px, py) - -1 to 32 (ET1): the
     * lattice's blend of the shallow sea's area over its blend of the sea's, so the
     * share is weighted by the sea share (no fringe of deep sea along a coast, the
     * study's 12, 6; the mockup's shal * sw / sw). 0 where the blend holds no sea.
     */
    private static double shallowShare(Scratch s, int px, int py) {
        double sea = lattice(s.seaL, px, py);
        return sea > 0 ? lattice(s.shalL, px, py) / sea : 0;
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
        if (sea > 0 && sea(sea, noise(s, os[COAST], x, y, stride))) return s.shallow && shallowAt(s, q, x, y, lat, lon, sea, stride) ? World.SHALLOW : World.SALT;
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
                if (Double.isNaN(nb)) nb = noise(s, os[BLOB], x, y, stride);
                if (inLake(w, k, x, y, nb, q)) return World.FRESH;
            }
        }
        if (lake > 0 && noise(s, os[FRESH], x, y, stride) < below(q, lake / (1 - sea))) return World.FRESH;
        if (ice >= 1 || (ice > 0 && covered(ice, noise(s, os[ICE], x, y, stride)))) return World.ICE;
        if (forest >= 1 || (forest > 0 && (Double.isNaN(dither) ? noise(s, os[FOREST], x, y, stride) < below(q, forest) : dither < forest))) return World.FOREST;
        if (desert >= 1 || (desert > 0 && covered(desert, noise(s, os[DESERT], x, y, stride)))) return World.DESERT;
        return World.GRASS;
    }

    /**
     * A plot's class by the point rule (classAt(), the regions' and the far
     * views') at its centre, at a stride of one, rather than its tile's
     * (draw()): the rule's two copies, which agree but for the lattice's blend
     * of the shares (a plot within a hair of a threshold may differ). What a
     * harness holds the one to the other with.
     */
    byte pointTerrain(long x, long y) {
        return pointClass(scratch(), quantile(), x + 0.5, y + 0.5, 1);
    }

    /**
     * Whether a point of the sea is SHALLOW (ET1), the tile rule's other copy
     * (draw()): the shallow share of the sea there, interpolated between the
     * 15" cells' centres weighted by each cell's sea (Reader.bilinearProduct()
     * over `sea`, the sea share bilinear() gave), beats the shallow noise
     * ranked as the forest's is. From FAR_STRIDE, where `sea` is the 4' far
     * layers' and a pixel is wider than a 15" cell, the shallow level of the
     * 15" cell the point lies in (0 where the cell holds no sea, so the sea
     * layer is not read: a far view makes millions of these, and each tile
     * of a layer read is a blob inflated).
     */
    private boolean shallowAt(Scratch s, double[] q, double x, double y, double lat, double lon, double sea, int stride) {
        double share = stride >= FAR_STRIDE
                ? s.shalR.at(lat, lon) / (double) EarthData.SHARE_LEVELS
                : s.shalR.bilinearProduct(s.seaR, lat, lon) / SHARE_AREA / sea;
        return share > 0 && noise(s, os[SHALLOW], x, y, stride) < below(q, share);
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
       THE RELIEF: a second plane beside the cover (ET1, spec-earth2-terrain 3.1)

       A plot carries two values: its cover (terrainAt(): GRASS to SHALLOW) and
       its relief (reliefAt(): FLAT, FOOTHILL, STEEP, VERY_STEEP). The cover is
       never touched by it: tileTerrain() gives the classes it gave before, byte
       for byte, and a harness holds that. The relief is drawn from earth-2's slope
       layer (EarthData.slope()), a byte a 15" cell holding the shares of the
       cell's LAND at or above foothill (a 10% grade), steep (30%) and very steep
       (60%).

       THE RULE, the forest's own. F, S and V are those shares at the plot -
       interpolated between the four cell centres about it, WEIGHTED BY EACH
       CELL'S LAND (Reader.shares()), a tile's at the lattice's points and blended
       between them as the forest's are - and r is one noise of the relief's own
       (its field key F_RELIEF, octaves RELIEF_SHIFT wider than the cover's: the
       dial says why), ranked through the same table (below()), so each class
       covers in expectation what the data gives it:
         VERY_STEEP where r < below(V), else STEEP where r < below(S), else
         FOOTHILL where r < below(F), else FLAT.
       One noise for the three, so the classes nest as the shares do (V <= S <= F
       by the file's promise and the blend's weights): a very steep patch is the
       core of a steep one, and a steep one of a foothill - a slope grades away
       into the plain instead of speckling.

       WHAT HAS NONE. Relief is drawn only on a plot whose cover is dry ground
       (World.dry(): grass, forest, the beach, the desert); water, the shallow sea
       and ICE carry FLAT, meaning "none" (D4: snow wins over slope, and water has
       no relief). Forest keeps its cover on any slope (D5): the plot is FOREST
       and, say, STEEP. An earth-1 file has no slope layer: every plot is FLAT, and
       so is every plot of a generated world (D19), which never reaches this.

       TWO COPIES, as the cover's: drawRelief() for a tile (a lattice, the
       tile's octaves a rectangle at once) and reliefOf() for a point (the regions',
       the far views', a stride at a time), which agree but for the lattice's blend
       of the shares (a plot within a hair of a threshold may differ).

       THE FAR VIEW. From FAR_STRIDE a pixel is wider than a 15" cell and a far
       view spans thousands of 1-degree tiles, each a 57 KB blob of the slope
       layer to inflate (about 0.15 ms): read cell by cell, a globe's frame would
       inflate thousands of them every frame and the LRU, which holds a hundred and
       seventy, would never keep them. So the far shares are summarised, once a
       tile and kept (FarShares): the land-weighted shares in each 4' block, 225 a
       tile, and a pixel reads the block it lies in - the far layers' own scale.
       From DITHER_STRIDE a camera's pixels are dithered by a hash against the
       shares, as the forest's are (the camera's dither, remixed so that steep
       ground is not forested ground), for under it the ranked noise keeps its
       widest octaves and over it a pixel holds the whole of them.
       ===================================================================== */

    private final Map<Long, byte[]> reliefTiles = new LinkedHashMap<>(256, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, byte[]> eldest) {
            return size() > RELIEF_KEPT;
        }
    };

    /** A tile's relief, kept (RELIEF_KEPT): shared, do not write into it. The tile's cover is drawn first, kept as it is (keptTile()). */
    byte[] keptRelief(long tx, long ty) {
        long k = (tx << 32) ^ (ty & 0xffffffffL);
        synchronized (reliefTiles) {
            byte[] t = reliefTiles.get(k);
            if (t != null) return t;
        }
        byte[] t = new byte[World.TILE * World.TILE];
        drawRelief(tx, ty, keptTile(tx, ty), t);
        synchronized (reliefTiles) { reliefTiles.put(k, t); }
        return t;
    }

    /** One plot's relief: its tile's (tileRelief()), from a kept tile; FLAT off the world, and for data with no slope layer. World.reliefAt() on Earth. */
    public byte reliefAt(long x, long y) {
        if (!relief || x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) return World.FLAT;
        byte[] t = keptRelief(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
        return t[(int) (Math.floorMod(y, World.TILE) * World.TILE + Math.floorMod(x, World.TILE))];
    }

    /**
     * One tile's relief, a byte a plot in rows (out[py x 32 + px], as tileTerrain()'s cover): the rule
     * above, on the plots whose cover tileTerrain() draws dry. A tile off the world's square is FLAT, and
     * so is every tile of data with no slope layer. Nothing is drawn where no plot of the tile is dry or
     * no cell about it holds a slope (EarthTerrainCheck 17 times it).
     */
    public void tileRelief(long tx, long ty, byte[] out) {
        int n = World.TILE;
        if (!relief || tx < 0 || ty < 0 || tx >= World.SIDE / n || ty >= World.SIDE / n) {
            Arrays.fill(out, 0, n * n, World.FLAT);
            return;
        }
        System.arraycopy(keptRelief(tx, ty), 0, out, 0, n * n);
    }

    /**
     * A tile's relief given its cover (the rule's tile copy): FLAT but on dry plots, and on those the
     * nested classes over the relief noise. The lattice of shares is read only where the tile holds
     * dry ground, the noise only where some lattice point holds a slope.
     */
    private void drawRelief(long tx, long ty, byte[] cover, byte[] out) {
        int n = World.TILE, area = n * n;
        Arrays.fill(out, 0, area, World.FLAT);
        if (!relief || tx < 0 || ty < 0 || tx >= World.SIDE / n || ty >= World.SIDE / n) return;
        boolean land = false;
        for (int i = 0; i < area && !land; i++) land = World.dry(cover[i]);
        if (!land) return;
        Scratch s = scratch();
        long x0 = tx * n, y0 = ty * n;
        EarthProj.Blend b = proj.blend(x0, y0, n);
        double[] ll = s.ll, sh = s.sh;
        boolean any = false;
        // The lattice points the plots blend between (the cover's: plots x0 - 4 + 4i, y0 - 4 + 4j); the ring's, i = 0, no plot reads.
        for (int j = 1; j < LATTICE; j++) {
            for (int i = 1; i < LATTICE; i++) {
                int at = j * LATTICE + i;
                double px = x0 - LATTICE_STEP + LATTICE_STEP * i + 0.5, py = y0 - LATTICE_STEP + LATTICE_STEP * j + 0.5;
                if (b.latLon(px, py, ll)) {
                    s.slopeR.shares(s.seaR, s.lakeR, ll[0], ll[1], sh);
                    s.relF[at] = sh[0];
                    s.relS[at] = sh[1];
                    s.relV[at] = sh[2];
                } else {
                    s.relF[at] = s.relS[at] = s.relV[at] = 0;
                }
                any |= s.relF[at] > 0;
            }
        }
        if (!any) return;
        double[] q = quantile();
        Arrays.fill(s.rel, 0, area, 0);
        for (int k = NOISE_HI; k >= NOISE_LO; k--) octave(s.lat, os[RELIEF][k], k + RELIEF_SHIFT, x0, y0, n, n, AMP_N[k], s.rel);
        for (int py = 0; py < n; py++) {
            for (int px = 0; px < n; px++) {
                int i = py * n + px;
                if (!World.dry(cover[i])) continue;
                double f = lattice(s.relF, px, py);
                if (!(f > 0)) continue;
                double r = s.rel[i];
                if (!(r < below(q, f))) continue;
                double st = lattice(s.relS, px, py);
                if (!(r < below(q, st))) { out[i] = World.FOOTHILL; continue; }
                double v = lattice(s.relV, px, py);
                out[i] = r < below(q, v) ? World.VERY_STEEP : World.STEEP;
            }
        }
    }

    /* =====================================================================
       THE LANDMASS OF A PLOT (ET5, 2026-10-25; the ET5 design, D2 to D4)

       A plot's landmass is the id of the landmass layer's 15" cell it lies in
       (EarthData.landmassGrid()), the cell read at the plot's centre through
       the tile's blend, as the cover and the relief are drawn (proj.blend(),
       32 plots a side): so the tile rules agree with each other and with the
       founding card, which reads the same call at the snapped site.

       WATER HAS NONE. A sea, shallow or fresh plot reads 0, and so does a plot
       the layer cannot place. A DRY plot (and ice, which the card has always
       read a landmass for) on a cell the layer calls no land - a coast the
       game draws dry where the data says half water, a lake's shore or islet,
       the dry rest of a river-cut cell, a fixture's tile not held - takes the
       NEAREST land cell's id (the bake's own naming rule, tools/earth/
       landmass.py: the least row and column distance squared, so no islet of
       a few plots becomes a landmass), within LANDMASS_REACH cells either way,
       a tie going to the lowest id; none in reach reads 0, which the city
       reads as home. Integer arithmetic only, resolved once a cell (kept).

       Kept a tile at a time beside the relief (keptLandmass()), never inside
       tileTerrain() or tileRelief(), whose bytes and cost it leaves alone.
       ===================================================================== */

    /** How many 15" cells either way (east-west and north-south, about 3.7 km north-south) a dry plot on a cell the layer calls no land looks for the nearest land cell: 8. Past it the plot reads 0, which the city reads as home. */
    public static final int LANDMASS_REACH = 8;

    /** Cells whose nearest land cell is kept (resolvedCell()) before the memory is cleared: 65,536. */
    static final int RESOLVED_KEPT = 65_536;

    private final Map<Long, Integer> resolved = new HashMap<>();

    private final Map<Long, int[]> landmassTiles = new LinkedHashMap<>(256, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, int[]> eldest) {
            return size() > RELIEF_KEPT;
        }
    };

    /** Whether the data has the landmass layer (EarthData.hasRelief()): without it every plot's landmass is 0. */
    public boolean hasLandmasses() { return relief; }

    /** Whether a plot of this cover class has a landmass: dry ground and ice, never water. */
    static boolean onLand(byte cover) { return World.dry(cover) || cover == World.ICE; }

    /**
     * The landmass a plot of the layer's cell (row g, column c) belongs to when it is dry: the cell's own id, or - for a
     * cell with no land - the nearest land cell's within LANDMASS_REACH (least dr squared + dc squared, the lowest id on a
     * tie), or 0 for none. Pure on the data; the answer is kept a cell (RESOLVED_KEPT).
     */
    int resolvedCell(long g, long c) {
        int own = data.landmassOfCell(g, c);
        if (own != 0) return own;
        int cpd = data.landmassCellsPerDegree();
        long wrap = 360L * cpd;
        long key = g * wrap + Math.floorMod(c, wrap);
        synchronized (resolved) {
            Integer hit = resolved.get(key);
            if (hit != null) return hit;
        }
        int best = 0;
        long bestD = Long.MAX_VALUE;
        int gridTile = -1;
        EarthData.LandmassGrid grid = null;
        for (int dr = -LANDMASS_REACH; dr <= LANDMASS_REACH; dr++) {
            long r = g + dr;
            if (r < 0 || r >= 180L * cpd) continue;
            for (int dc = -LANDMASS_REACH; dc <= LANDMASS_REACH; dc++) {
                long d = (long) dr * dr + (long) dc * dc;
                if (d > bestD || d == 0) continue;
                long cc = Math.floorMod(c + dc, wrap);
                int t = data.landmassTileOf(r, cc);
                if (t != gridTile) {
                    grid = data.landmassGrid(t);
                    gridTile = t;
                }
                int id = grid.id((int) (r % cpd), (int) (cc % cpd));
                if (id == 0) continue;
                if (d < bestD || id < best) {
                    bestD = d;
                    best = id;
                }
            }
        }
        synchronized (resolved) {
            if (resolved.size() >= RESOLVED_KEPT) resolved.clear();
            resolved.put(key, best);
        }
        return best;
    }

    /** A tile's landmass, kept (RELIEF_KEPT): shared, do not write into it. The tile's cover is drawn first, kept as it is (keptTile()). */
    int[] keptLandmass(long tx, long ty) {
        long k = (tx << 32) ^ (ty & 0xffffffffL);
        synchronized (landmassTiles) {
            int[] t = landmassTiles.get(k);
            if (t != null) return t;
        }
        int[] t = new int[World.TILE * World.TILE];
        drawLandmass(tx, ty, keptTile(tx, ty), t);
        synchronized (landmassTiles) { landmassTiles.put(k, t); }
        return t;
    }

    /** A tile's landmass given its cover: 0 on water and where the layer places nothing (see the section above), the plot's landmass on the rest. */
    private void drawLandmass(long tx, long ty, byte[] cover, int[] out) {
        int n = World.TILE;
        Arrays.fill(out, 0, n * n, 0);
        if (!relief || tx < 0 || ty < 0 || tx >= World.SIDE / n || ty >= World.SIDE / n) return;
        boolean land = false;
        for (int i = 0; i < n * n && !land; i++) land = onLand(cover[i]);
        if (!land) return;
        long x0 = tx * n, y0 = ty * n;
        EarthProj.Blend b = proj.blend(x0, y0, n);
        double[] ll = new double[2];
        int cpd = data.landmassCellsPerDegree();
        int gridTile = -1;
        EarthData.LandmassGrid grid = null;
        for (int py = 0; py < n; py++) {
            for (int px = 0; px < n; px++) {
                int i = py * n + px;
                if (!onLand(cover[i]) || !b.latLon(x0 + px + 0.5, y0 + py + 0.5, ll)) continue;
                long g = data.landmassRow(ll[0]), c = data.landmassCol(ll[1]);
                int t = data.landmassTileOf(g, c);
                if (t != gridTile) {
                    grid = data.landmassGrid(t);
                    gridTile = t;
                }
                int id = grid.id((int) (g % cpd), (int) (c % cpd));
                out[i] = id != 0 ? id : resolvedCell(g, c);
            }
        }
    }

    /**
     * One tile's landmass, an id a plot in rows (out[py x 32 + px], as tileTerrain()'s cover): 0 on water, off the
     * world's square and for data with no landmass layer. See the section above.
     */
    public void tileLandmass(long tx, long ty, int[] out) {
        int n = World.TILE;
        if (!relief || tx < 0 || ty < 0 || tx >= World.SIDE / n || ty >= World.SIDE / n) {
            Arrays.fill(out, 0, n * n, 0);
            return;
        }
        System.arraycopy(keptLandmass(tx, ty), 0, out, 0, n * n);
    }

    /** One plot's landmass: its tile's (tileLandmass()), from a kept tile; 0 off the world and for data with no landmass layer. World.landmassAt() on Earth. */
    public int landmassAt(long x, long y) {
        if (!relief || x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) return 0;
        int[] t = keptLandmass(Math.floorDiv(x, World.TILE), Math.floorDiv(y, World.TILE));
        return t[(int) (Math.floorMod(y, World.TILE) * World.TILE + Math.floorMod(x, World.TILE))];
    }

    /**
     * A plot's landmass by its 15" cell alone, whether the plot is dry or water: the cell's own id, or the nearest land
     * cell's within LANDMASS_REACH (resolvedCell(), the rule landmassAt() gives a dry plot), read at the plot's blend point
     * as the tile is drawn. What a field centred on a lake plot of an island is on (landmassAt() gives a water plot 0, which
     * the city reads as home). Not kept: asked of a few fields' centres (LandManager's works gate), never of a tile's plots.
     */
    public int landmassOfPlot(long x, long y) {
        if (!relief || x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) return 0;
        int n = World.TILE;
        long x0 = Math.floorDiv(x, n) * n, y0 = Math.floorDiv(y, n) * n;
        EarthProj.Blend b = proj.blend(x0, y0, n);
        double[] ll = new double[2];
        if (!b.latLon(x + 0.5, y + 0.5, ll)) return 0;
        return resolvedCell(data.landmassRow(ll[0]), data.landmassCol(ll[1]));
    }

    /**
     * The landmass ids a tile's plots can take, the non-zero ones ascending: those of the land cells under the tile's blend
     * box widened by LANDMASS_REACH cells every way. A pure read of a window of cells - no plot of the tile is drawn - and a
     * superset of what landmassAt() gives any plot of the tile, which is why a tile whose ids are all one landmass can be
     * counted without reading it (Landmasses.recount()). Empty off the world and for data with no landmass layer.
     */
    int[] idsNear(long tx, long ty) {
        int n = World.TILE;
        if (!relief || tx < 0 || ty < 0 || tx >= World.SIDE / n || ty >= World.SIDE / n) return new int[0];
        long x0 = tx * n, y0 = ty * n;
        EarthProj.Blend b = proj.blend(x0, y0, n);
        double[] ll = new double[2];
        double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY, minLon = Double.POSITIVE_INFINITY, maxLon = Double.NEGATIVE_INFINITY;
        double refLon = Double.NaN;
        double[] at = { 0.5, n / 2.0, n - 0.5 };
        for (double fy : at) {
            for (double fx : at) {
                if (!b.latLon(x0 + fx, y0 + fy, ll)) continue;
                double lon = ll[1];
                if (Double.isNaN(refLon)) refLon = lon;
                else if (lon - refLon > 180) lon -= 360;
                else if (lon - refLon < -180) lon += 360;
                minLat = Math.min(minLat, ll[0]);
                maxLat = Math.max(maxLat, ll[0]);
                minLon = Math.min(minLon, lon);
                maxLon = Math.max(maxLon, lon);
            }
        }
        if (Double.isNaN(refLon)) return new int[0];
        int cpd = data.landmassCellsPerDegree();
        long wrap = 360L * cpd;
        long gTop = data.landmassRow(maxLat) - LANDMASS_REACH, gBottom = data.landmassRow(minLat) + LANDMASS_REACH;
        long cLeft = data.landmassCol(minLon);
        long span = Math.floorMod(data.landmassCol(maxLon) - cLeft, wrap);
        long cRight = cLeft + Math.min(span, wrap) + LANDMASS_REACH;
        cLeft -= LANDMASS_REACH;
        int[] ids = new int[8];
        int count = 0;
        int gridTile = -1;
        EarthData.LandmassGrid grid = null;
        for (long g = Math.max(0, gTop); g <= Math.min(180L * cpd - 1, gBottom); g++) {
            for (long c = cLeft; c <= cRight; c++) {
                long cc = Math.floorMod(c, wrap);
                int t = data.landmassTileOf(g, cc);
                if (t != gridTile) {
                    grid = data.landmassGrid(t);
                    gridTile = t;
                }
                int id = grid.id((int) (g % cpd), (int) (cc % cpd));
                if (id == 0) continue;
                boolean seen = false;
                for (int k = 0; k < count && !seen; k++) seen = ids[k] == id;
                if (seen) continue;
                if (count == ids.length) ids = Arrays.copyOf(ids, count * 2);
                ids[count++] = id;
            }
        }
        int[] out = Arrays.copyOf(ids, count);
        Arrays.sort(out);
        return out;
    }

    /**
     * A point's relief at a stride, the rule's point copy (the regions' and the far views'): the shares
     * at the point (Reader.shares(), a 15" cell's own land-weighted blend; from FAR_STRIDE the 4' block's
     * - FarShares) against the relief noise at the point and its octaves wider than a pixel, the
     * narrower at their mean - or, where `dither` is a number (the forest's rule: from DITHER_STRIDE, a
     * camera's dither), against a hash of it, remixed: a pixel that wide has no octave of the noise
     * to itself, and a globe's or a grid's camera has no plot coordinates to read the noise at (its x() and y() are
     * nought). The caller has found the point's cover dry ground; (lat, lon) is the point's place on the planet.
     */
    private byte reliefOf(Scratch s, double[] q, double x, double y, double lat, double lon, int stride, double dither) {
        double[] sh = s.sh;
        if (stride >= FAR_STRIDE) farShares(s, lat, lon, sh);
        else s.slopeR.shares(s.seaR, s.lakeR, lat, lon, sh);
        double f = sh[0];
        if (!(f > 0)) return World.FLAT;
        double r, lf, ls, lv;
        if (!Double.isNaN(dither)) {
            r = World.unit(World.mix(Double.doubleToLongBits(dither) ^ F_RELIEF_DITHER));
            lf = f;
            ls = sh[1];
            lv = sh[2];
        } else {
            r = reliefNoise(s, os[RELIEF], x, y, stride);
            lf = below(q, f);
            ls = below(q, sh[1]);
            lv = below(q, sh[2]);
        }
        return r < lv ? World.VERY_STEEP : r < ls ? World.STEEP : r < lf ? World.FOOTHILL : World.FLAT;
    }

    /**
     * A plot's relief by the point rule at its centre, at a stride of one, given its cover: the rule's
     * other copy (drawRelief() is the tile's) for a plot of dry cover, FLAT for any other. What a harness
     * holds the one to the other with, on plots whose cover both copies agree on.
     */
    byte pointRelief(long x, long y, byte cover) {
        if (!relief || !World.dry(cover)) return World.FLAT;
        Scratch s = scratch();
        double px = x + 0.5, py = y + 0.5;
        if (!proj.latLonOfPlot(px, py, s.ll)) return World.FLAT;
        return reliefOf(s, quantile(), px, py, s.ll[0], s.ll[1], 1, Double.NaN);
    }

    /**
     * The shares the tile and point rules draw against at a point (ET1): of the land there, at or above
     * foothill, at or above steep and very steep (Reader.shares(): the four 15" cells' about it, weighted
     * by each cell's land), or - `far` - the 4' block's it lies in (FarShares). {0, 0, 0} for data with
     * no slope layer. What a harness holds the order of the classes, and the far summary, to.
     */
    double[] reliefShares(double lat, double lon, boolean far) {
        double[] out = new double[3];
        if (!relief) return out;
        Scratch s = scratch();
        if (far) farShares(s, lat, lon, out);
        else s.slopeR.shares(s.seaR, s.lakeR, lat, lon, out);
        return out;
    }

    /**
     * A region's relief at a stride (World.regionRelief()'s contract), as regionTerrain() lays its cover:
     * n x n pixels from plot (x0, y0), each `stride` plots wide, FLAT where the pixel's cover is not dry
     * ground. At a stride of one every plot's own (reliefAt()). Wider, each pixel's cover is drawn as
     * regionTerrain() draws it, and on dry ground the relief by the point rule at its centre - from
     * DITHER_STRIDE against the plane's dither, the far node's own, so a region and a far node over the
     * same ground draw the same relief on the pixels whose cover agrees.
     */
    public void regionRelief(long x0, long y0, int stride, int n, byte[] out) {
        if (!relief) {
            Arrays.fill(out, 0, n * n, World.FLAT);
            return;
        }
        if (stride <= 1) {
            for (int py = 0; py < n; py++) {
                for (int px = 0; px < n; px++) out[py * n + px] = reliefAt(x0 + px, y0 + py);
            }
            return;
        }
        Scratch s = scratch();
        double[] q = quantile(), ll = s.ll;
        Camera plane = plane(x0, y0, stride, n);
        for (int py = 0; py < n; py++) {
            double y = y0 + (double) py * stride + stride * 0.5;
            for (int px = 0; px < n; px++) {
                double x = x0 + (double) px * stride + stride * 0.5;
                byte r = World.FLAT;
                if (x >= 0 && y >= 0 && x < World.SIDE && y < World.SIDE && proj.latLonOfPlot(x, y, ll)) {
                    double lat = ll[0], lon = ll[1];
                    if (World.dry(classAt(s, q, x, y, lat, lon, stride, true, Double.NaN))) r = reliefOf(s, q, x, y, lat, lon, stride, plane.dither(px, py, lat, lon));
                }
                out[py * n + px] = r;
            }
        }
    }

    /** The far shares of the slope at a point into out[0..2] (the 4' block it lies in, FarShares), the last tile read kept in hand. */
    private void farShares(Scratch s, double lat, double lon, double[] out) {
        int cpd = s.slopeR.cpd;
        long g = Math.min(180L * cpd - 1, Math.max(0, (long) Math.floor((90 - lat) * cpd)));
        long c = Math.floorMod((long) Math.floor((lon + 180) * cpd), 360L * cpd);
        int tile = (int) ((179 - g / cpd) * 360 + c / cpd);
        if (tile != s.farTile) {
            if (s.far == null) s.far = FarShares.of(data);
            s.farBlocks = s.far.blocks(data, tile);
            s.farTile = tile;
        }
        byte[] bl = s.farBlocks;
        if (bl == null) {
            out[0] = out[1] = out[2] = 0;
            return;
        }
        int per = cpd / FAR_BLOCK, plane = per * per;
        int at = (int) (g % cpd) / FAR_BLOCK * per + (int) (c % cpd) / FAR_BLOCK;
        out[0] = (bl[at] & 0xFF) / 255.0;
        out[1] = (bl[plane + at] & 0xFF) / 255.0;
        out[2] = (bl[2 * plane + at] & 0xFF) / 255.0;
    }

    /**
     * The slope's shares summarised for the far views (ET1): per 1-degree tile, built the first time a far
     * pixel lies in it and kept, the land-weighted shares at or above foothill, steep and very steep of
     * each FAR_BLOCK x FAR_BLOCK block of 15" cells (4', 225 blocks a tile), a byte each, in three planes
     * (foothill, steep, very steep). A tile with no slope at all is kept as nothing. One for each data
     * (of()), shared by every world drawn from it and every thread; the cost of a tile is its slope blob's
     * inflation (about 0.15 ms) and a pass over its cells, so a far view's first sight of a country costs
     * its tiles and every later one nothing. At most 675 bytes for each of the planet's 14,520 tiles
     * with land.
     */
    private static final class FarShares {
        private static final Map<EarthData, FarShares> OF = new WeakHashMap<>();
        /** A tile built and found flat or without land, kept in place of an array. */
        private static final byte[] NONE = new byte[0];
        private final byte[][] tiles = new byte[EarthData.TILES][];

        static FarShares of(EarthData d) {
            synchronized (OF) { return OF.computeIfAbsent(d, k -> new FarShares()); }
        }

        /** Tile t's blocks, three planes of (cpd / FAR_BLOCK)^2; null where the tile has no slope to speak of. */
        byte[] blocks(EarthData d, int t) {
            synchronized (this) {
                byte[] b = tiles[t];
                if (b != null) return b == NONE ? null : b;
            }
            byte[] b = build(d, t);
            synchronized (this) {
                if (tiles[t] == null) tiles[t] = b == null ? NONE : b;
            }
            return b;
        }

        private static byte[] build(EarthData d, int t) {
            EarthData.Layer slope = d.layer(EarthData.SLOPE), sea = d.layer("sea"), lake = d.layer("lake");
            int cpd = slope.cellsPerDegree;
            if (sea.cellsPerDegree != cpd || lake.cellsPerDegree != cpd || cpd % FAR_BLOCK != 0) {
                throw new IllegalStateException("the slope layer is not on the sea's and the lakes' grid of 15\" cells, or not in blocks of " + FAR_BLOCK + ": " + d.identity());
            }
            byte[] sl = d.raw(EarthData.SLOPE, t), se = d.raw("sea", t), lk = d.raw("lake", t);
            int slU = sl == null ? -d.entry(EarthData.SLOPE, t) : 0, seU = se == null ? -d.entry("sea", t) : 0, lkU = lk == null ? -d.entry("lake", t) : 0;
            if (sl == null && slU == 0) return null;
            int per = cpd / FAR_BLOCK, plane = per * per;
            double[] land = new double[plane], f = new double[plane], st = new double[plane], v = new double[plane];
            for (int r = 0; r < cpd; r++) {
                int row = r / FAR_BLOCK * per;
                for (int c = 0; c < cpd; c++) {
                    int i = r * cpd + c;
                    int w = EarthData.SHARE_LEVELS - (se == null ? seU : se[i] & 0xFF) - (lk == null ? lkU : lk[i] & 0xFF);
                    if (w <= 0) continue;
                    int b = sl == null ? slU : sl[i] & 0xFF, at = row + c / FAR_BLOCK;
                    land[at] += w;
                    f[at] += w * SLOPE_F[b];
                    st[at] += w * SLOPE_S[b];
                    v[at] += w * SLOPE_V[b];
                }
            }
            byte[] out = new byte[3 * plane];
            boolean any = false;
            for (int at = 0; at < plane; at++) {
                if (!(land[at] > 0)) continue;
                out[at] = (byte) Math.round(255 * f[at] / land[at]);
                out[plane + at] = (byte) Math.round(255 * st[at] / land[at]);
                out[2 * plane + at] = (byte) Math.round(255 * v[at] / land[at]);
                any |= out[at] != 0;
            }
            return any ? out : null;
        }
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
     * A tile's rivers as a far node draws them: the runs of two vertices or more
     * whose median width is `least` or more, in the order EarthData.rivers() gives
     * them, each with its median, and `most`, the largest median over all the
     * tile's runs of two vertices or more, drawn or not. A set made at `least`
     * holds every run of median `least` or more, so for any narrowest of `least`
     * or more it holds every run a node would draw, in the same order, and the
     * runs it skips are those the node skips; `most` does not depend on the
     * narrowest. Only the runs that pass are kept, never a tile's narrow ones.
     */
    private static final class FarRivers {
        final double least;
        final List<EarthData.Run> runs;
        final int[] medians;
        final int most;

        FarRivers(double least, List<EarthData.Run> runs, int[] medians, int most) {
            this.least = least; this.runs = runs; this.medians = medians; this.most = most;
        }
    }

    /**
     * Each 1-degree tile's FarRivers, once a far node has read it: null until then, and made again (not kept) for a node whose narrowest is
     * under the kept one's `least`. Made with the terrain (a reference a tile, about half a megabyte), so no thread ever sees a second array.
     * The far nodes are drawn on the map's workers as well as the screen's thread: an entry is immutable (final fields, its list built before
     * it), so a thread sees a whole entry or none, and two threads building the same tile's only cost a parse.
     */
    private final FarRivers[] farRivers = new FarRivers[EarthData.TILES];

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
     * the date line alike. A pure function of the key and the node: since
     * 0.7.132 (batch E8) the far rule through the city's plane's camera
     * (plane()), byte for byte what it drew before (GlobeCheck 4).
     */
    public void farTerrain(long x0, long y0, int stride, int n, byte[] out) {
        farTerrain(x0, y0, stride, n, out, null);
    }

    /**
     * ...and, where `relief` is not null, its relief beside it (ET1, spec-earth2-terrain 3.1): n x n
     * bytes laid as `out` is, FLAT where the pixel's cover is not dry ground - the cover is the same
     * byte for byte as without it. At a stride of one every plot's own (regionRelief()). A far node's
     * relief costs the slope of the 4' blocks under it (FarShares) the first time it is seen.
     */
    public void farTerrain(long x0, long y0, int stride, int n, byte[] out, byte[] relief) {
        if (stride <= 1) {
            regionTerrain(x0, y0, stride, n, out);
            if (relief != null) regionRelief(x0, y0, stride, n, relief);
            return;
        }
        farTerrain(plane(x0, y0, stride, n), out, relief);
    }

    /**
     * The far rule through a camera (0.7.132, batch E8; spec-globe 3.5): the
     * camera's w x h pixels, each classed at its centre on the planet as a
     * far node's are (classAt(), the camera's stride picking the layers and
     * the octaves at their mean, its dither the forest's), then the rivers
     * and small lakes stroked from their vectors through the camera, each run
     * at least FAR_RIVER_SHARE of a pixel wide and FAR_RIVER_HALF_PX a side.
     * A pixel off the planet, or off the plane's square, is sea; the screen
     * draws the void or space there (the camera says where).
     */
    public void farTerrain(Camera c, byte[] out) {
        farTerrain(c, out, null);
    }

    /**
     * ...and, where `relief` is not null, the relief of its pixels beside the cover (ET1,
     * spec-earth2-terrain 3.1; w x h bytes laid as `out` is): on each pixel whose cover is dry ground, once the
     * rivers and small lakes are stroked, the nested classes (reliefOf()) from the 15" cells' shares to
     * FAR_STRIDE and the 4' blocks' (FarShares) from it, ranked through the relief noise where the camera
     * has no dither (under DITHER_STRIDE) and drawn against a hash of its dither (the forest's, remixed)
     * where it has; FLAT on every other pixel. The cover is byte for byte what it is without.
     */
    public void farTerrain(Camera c, byte[] out, byte[] relief) {
        Scratch s = scratch();
        s.chart = c instanceof Chart ? (Chart) c : null;
        try {
            farTerrain(c, out, relief, s);
        } finally {
            s.chart = null;
        }
    }

    /** ...on this thread's scratch, whose chart (if the camera is one) is set. */
    private void farTerrain(Camera c, byte[] out, byte[] relief, Scratch s) {
        double[] q = quantile(), ll = s.ll;
        boolean rel = relief != null && this.relief;
        int b = FAR_BLEND_PX, w = c.w, h = c.h;
        for (int by = 0; by < h; by += b) {
            for (int bx = 0; bx < w; bx += b) {
                for (int py = by; py < Math.min(h, by + b); py++) {
                    for (int px = bx; px < Math.min(w, bx + b); px++) {
                        int at = py * w + px;
                        if (!c.latLon(px, py, ll)) {
                            out[at] = World.SALT;
                            if (relief != null) relief[at] = World.FLAT;
                            continue;
                        }
                        double lat = ll[0], lon = ll[1];
                        double d = c.dither(px, py, lat, lon);
                        out[at] = classAt(s, q, c.x(px), c.y(py), lat, lon, c.stride, false, d);
                        if (relief != null) relief[at] = rel && World.dry(out[at]) ? reliefOf(s, q, c.x(px), c.y(py), lat, lon, c.stride, d) : World.FLAT;
                    }
                }
            }
        }
        strokeWater(c, out, q);
        // The rivers and small lakes are stroked over the land: the pixels they took are fresh water now, and have none.
        if (rel) {
            for (int i = 0; i < w * h; i++) if (relief[i] != World.FLAT && !World.dry(out[i])) relief[i] = World.FLAT;
        }
    }

    /** How many river segments and small lakes the last far node on this thread drew from, and how many 1-degree tiles it read: what a harness prints. */
    static final ThreadLocal<long[]> FAR_COUNTS = ThreadLocal.withInitial(() -> new long[3]);

    /** The rivers and small lakes over a camera's pixels (farTerrain()): fresh water on every pixel that is not sea whose centre a river's stroke or a small lake's blob covers. */
    private void strokeWater(Camera c, byte[] out, double[] q) {
        double sideX = c.step * c.w, sideY = c.step * c.h, least = FAR_RIVER_HALF_PX * c.step;
        double nx0 = c.ox - least, ny0 = c.oy - least, nx1 = c.ox + sideX + least, ny1 = c.oy + sideY + least;
        double narrowest = FAR_RIVER_SHARE * c.step * c.unitM;
        double grow = Math.sqrt(1 + BLOB_FRAY / 2);
        double[] a = new double[2], b = new double[2];
        int[] widths = new int[64];
        int[] widest = widestRun;
        if (widest == null) {
            widest = new int[EarthData.TILES];
            Arrays.fill(widest, -1);
            widestRun = widest;
        }
        FarRivers[] kept = farRivers;
        long segs = 0, lakes = 0;
        int[] tiles = c.tiles();
        for (int t : tiles) {
            c.tile(t);
            if (widest[t] < 0 || widest[t] >= narrowest) {
                FarRivers fr = kept[t];
                if (fr == null || fr.least > narrowest) {
                    List<EarthData.Run> all = data.rivers(t), keep = new ArrayList<>();
                    int[] med = new int[all.size()];
                    int most = 0, n = 0;
                    for (EarthData.Run run : all) {
                        int m = run.size();
                        if (m < 2) continue;
                        if (widths.length < m) widths = new int[Math.max(m, 2 * widths.length)];
                        for (int k = 0; k < m; k++) widths[k] = run.width(k);
                        Arrays.sort(widths, 0, m);
                        int median = widths[m / 2];
                        most = Math.max(most, median);
                        if (median < narrowest) continue;
                        keep.add(run);
                        med[n++] = median;
                    }
                    fr = new FarRivers(narrowest, keep, Arrays.copyOf(med, n), most);
                    kept[t] = fr;
                }
                for (int i = 0; i < fr.runs.size(); i++) {
                    if (fr.medians[i] < narrowest) continue;
                    EarthData.Run run = fr.runs.get(i);
                    int m = run.size();
                    boolean have = c.place(run.lat(0), run.lon(0), a, true);
                    for (int k = 0; k + 1 < m; k++) {
                        boolean next = c.place(run.lat(k + 1), run.lon(k + 1), b, true);
                        if (have && next) {
                            if (c.wrap > 0) b[0] = unwrapped(b[0], a[0], c.wrap);
                            double ha = Math.max(least, run.width(k) / 2.0 / c.unitM), hb = Math.max(least, run.width(k + 1) / 2.0 / c.unitM);
                            double h = Math.max(ha, hb), xs = c.xScale(0.5 * (run.lat(k) + run.lat(k + 1))), hx = h / xs;
                            for (double sh : c.shifts(Math.min(a[0], b[0]) - hx, Math.max(a[0], b[0]) + hx)) {
                                double ax = a[0] + sh, bx = b[0] + sh;
                                if (Math.max(ax, bx) + hx >= nx0 && Math.min(ax, bx) - hx <= nx1 && Math.max(a[1], b[1]) + h >= ny0 && Math.min(a[1], b[1]) - h <= ny1) {
                                    stroke(ax, a[1], bx, b[1], ha, hb, c, xs, out);
                                    segs++;
                                }
                            }
                        }
                        a[0] = b[0];
                        a[1] = b[1];
                        have = next;
                    }
                }
                widest[t] = fr.most;
            }
            EarthData.SmallLakes sl = data.smallLakes(t);
            for (int k = 0; k < sl.size(); k++) {
                if (!c.place(sl.lat(k), sl.lon(k), a, false)) continue;
                double r = Math.sqrt(sl.areaKm2(k) * 1e6 / Math.PI) / c.unitM, reach = r * grow;
                double xs = c.xScale(sl.lat(k)), rx = reach / xs;
                for (double sh : c.shifts(a[0] - rx, a[0] + rx)) {
                    double lx = a[0] + sh;
                    if (lx + rx < c.ox || lx - rx > c.ox + sideX || a[1] + reach < c.oy || a[1] - reach > c.oy + sideY) continue;
                    blob(lx, a[1], r, c, xs, out, q);
                    lakes++;
                }
            }
        }
        long[] counts = FAR_COUNTS.get();
        counts[0] = segs;
        counts[1] = lakes;
        counts[2] = tiles.length;
    }

    /** A wrapped coordinate moved by whole turns to within half a turn of a reference: a river's next vertex across the grid's date line. */
    private static double unwrapped(double v, double ref, double wrap) {
        if (v - ref > wrap / 2) return v - wrap;
        if (ref - v > wrap / 2) return v + wrap;
        return v;
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

    /**
     * One river segment stroked onto a camera's pixels: every pixel not sea
     * whose centre lies within the segment's half-width there, interpolated
     * along it (onRiver()'s rule) - the camera's x units xs of its y units
     * on the ground (the grid's cos lat; 1, and the arithmetic as before, on
     * the plane and the globe).
     */
    private static void stroke(double ax, double ay, double bx, double by, double ha, double hb, Camera c, double xs, byte[] out) {
        double h = Math.max(ha, hb), hx = h / xs;
        int i0 = first(Math.min(ax, bx) - hx, c.ox, c.step, c.w), i1 = last(Math.max(ax, bx) + hx, c.ox, c.step, c.w);
        int j0 = first(Math.min(ay, by) - h, c.oy, c.step, c.h), j1 = last(Math.max(ay, by) + h, c.oy, c.step, c.h);
        if (i0 > i1 || j0 > j1) return;
        double vx = (bx - ax) * xs, vy = by - ay, l2 = vx * vx + vy * vy;
        for (int j = j0; j <= j1; j++) {
            double y = c.oy + (j + 0.5) * c.step;
            for (int i = i0; i <= i1; i++) {
                int at = j * c.w + i;
                if (World.salt(out[at]) || out[at] == World.FRESH) continue;
                double x = c.ox + (i + 0.5) * c.step, ex = (x - ax) * xs;
                double t = l2 > 0 ? (ex * vx + (y - ay) * vy) / l2 : 0;
                t = t < 0 ? 0 : t > 1 ? 1 : t;
                double dx = ex - t * vx, dy = y - ay - t * vy, w = ha + (hb - ha) * t;
                if (dx * dx + dy * dy < w * w) out[at] = World.FRESH;
            }
        }
    }

    /** One small lake drawn onto a camera's pixels: every pixel not sea whose centre its blob holds - inLake()'s rule, its noise at the camera's stride (at DITHER_STRIDE and up, at its mean: the lake's disc) - x scaled as stroke()'s. */
    private void blob(double lx, double ly, double r, Camera c, double xs, byte[] out, double[] q) {
        Scratch s = scratch();
        double reach = r * Math.sqrt(1 + BLOB_FRAY / 2), rx = reach / xs;
        int i0 = first(lx - rx, c.ox, c.step, c.w), i1 = last(lx + rx, c.ox, c.step, c.w);
        int j0 = first(ly - reach, c.oy, c.step, c.h), j1 = last(ly + reach, c.oy, c.step, c.h);
        for (int j = j0; j <= j1; j++) {
            double y = c.oy + (j + 0.5) * c.step;
            for (int i = i0; i <= i1; i++) {
                int at = j * c.w + i;
                if (World.salt(out[at]) || out[at] == World.FRESH) continue;
                double x = c.ox + (i + 0.5) * c.step, dx = (x - lx) * xs, dy = y - ly;
                double t = (dx * dx + dy * dy) / (r * r);
                if (t >= 1 + BLOB_FRAY / 2) continue;
                if (t <= 1 - BLOB_FRAY / 2 || (c.aim(i, j) && noise(s, os[BLOB], x, y, c.stride) < below(q, 0.5 + (1 - t) / BLOB_FRAY))) out[at] = World.FRESH;
            }
        }
    }

    /** The first pixel of a run (from `o`, `step` units each, n of them) whose centre is at `v` or past it, clamped to the run: n when none is. */
    private static int first(double v, double o, double step, int n) {
        double i = Math.ceil((v - o) / step - 0.5);
        return i <= 0 ? 0 : i >= n ? n : (int) i;
    }

    /** ...and the last whose centre is at `v` or before it: -1 when none is. */
    private static int last(double v, double o, double step, int n) {
        double i = Math.floor((v - o) / step - 0.5);
        return i < 0 ? -1 : i >= n - 1 ? n - 1 : (int) i;
    }

    /* ----- the camera: where the far rule's pixels lie on the planet (0.7.132, batch E8; spec-globe 3.5) ----- */

    /**
     * Where the far rule's pixels lie on the planet, and where a river's
     * vertex lands among them (0.7.132, batch E8; docs/studies/spec-globe.md
     * 3.5). Two things tied farTerrain() to the city's plane: where a pixel
     * is (the projection, or its FAR_BLEND_PX blend) and where a vertex lands
     * with which 1-degree tiles a node reads (place(), nodeTiles()). Both are
     * a camera's, and there are four:
     *   - the city's plane (plane()): a far node, byte for byte as before;
     *   - a latitude-longitude grid (grid()): the planet's picture, a texel
     *     a pixel (GlobePicture);
     *   - the globe round a view centre with a roll (globe()): GlobeFrame's
     *     GROUND tier, a piece of the screen;
     *   - a chart about a fixed anchor (chart(), MS3): a node of the near
     *     view far from the city, true in shape, its noise the planet's.
     * A camera's pixels are w x h, pixel (i, j) centred at
     * (ox + (i + 0.5) step, oy + (j + 0.5) step) in its own units - plots on
     * the plane, texels on the grid, the screen's pixels on the globe - a
     * unit unitM metres of ground down and xScale(lat) x unitM across.
     * stride is the rule's: a pixel's ground in plots, which picks the layers
     * the shares are read from and the noise's octaves at their mean - on
     * the globe never under DITHER_STRIDE, where every octave is at its mean
     * and a pixel's place in plots no longer matters.
     */
    public abstract static class Camera {
        final int w, h, stride;
        final double ox, oy, step, unitM;
        /** Units round the planet across, where the camera's x wraps (the grid's columns at the date line); 0 where it does not. */
        final double wrap;

        Camera(int w, int h, int stride, double ox, double oy, double step, double unitM, double wrap) {
            this.w = w;
            this.h = h;
            this.stride = stride;
            this.ox = ox;
            this.oy = oy;
            this.step = step;
            this.unitM = unitM;
            this.wrap = wrap;
        }

        /** Its pixels across and down. */
        public int width() { return w; }
        public int height() { return h; }

        /** The rule's stride: a pixel's ground, in plots. */
        public int stride() { return stride; }

        /** Metres of ground a pixel stands for, down. */
        public double pixelM() { return step * unitM; }

        /** Pixel (px, py)'s centre on the planet into ll; false off the planet (or off the plane's square), where it is drawn as sea. */
        abstract boolean latLon(int px, int py, double[] ll);

        /** Where pixel column px and row py stand for the rule's noise, in plots. */
        abstract double x(int px);
        abstract double y(int py);

        /** The dither at pixel (px, py), whose centre is (lat, lon): a number in [0, 1), or NaN where the forest is ranked through its noise. */
        abstract double dither(int px, int py, double lat, double lon);

        /** The 1-degree tiles whose rivers and small lakes may reach its pixels. */
        abstract int[] tiles();

        /** Ready to place tile t's vertices and small lakes. */
        void tile(int t) { }

        /** Ready for the noise at pixel (px, py) when a rule reads it away from latLon() (a small lake's fray); false where the pixel is not on the planet. Nothing to do but on a chart. */
        boolean aim(int px, int py) { return true; }

        /** A point of tile t's - a river's vertex (`river`) or a small lake's - in the camera's units into xy; false where it is not drawn. */
        abstract boolean place(double lat, double lon, double[] xy, boolean river);

        /** A unit's ground across over a unit's ground down, at a latitude: 1 but on the grid. */
        double xScale(double lat) { return 1; }

        /** The one shift a camera whose x does not wrap draws a shape at: none, the shape where it is. */
        private static final double[] NO_SHIFT = { 0 };

        /** The whole turns a shape spanning x0 to x1 is drawn at: none but where x wraps (the grid's). */
        double[] shifts(double x0, double x1) {
            if (wrap <= 0) return NO_SHIFT;
            double lo = ox, hi = ox + step * w;
            int k0 = (int) Math.ceil((lo - x1) / wrap), k1 = (int) Math.floor((hi - x0) / wrap);
            if (k1 < k0) return new double[0];
            double[] s = new double[k1 - k0 + 1];
            for (int k = k0; k <= k1; k++) s[k - k0] = k * wrap;
            return s;
        }
    }

    /** The city's plane's camera: a far node of n x n pixels from plot (x0, y0), stride plots each (MapTiles.nodeTerrain()). */
    public Camera plane(long x0, long y0, int stride, int n) {
        return new Plane(x0, y0, stride, n);
    }

    /** The city's plane: the far node as it was drawn before the camera, the same arithmetic in the same order. */
    private final class Plane extends Camera {
        private final long x0, y0;
        private final long ds;
        private final double rim = EarthProj.REACH_M - RIM_M;
        private final double[] corners = new double[8];
        private boolean blended;
        private int tile = -1;
        private EarthProj.Blend blend;
        private int blendX = -1, blendY = -1;

        Plane(long x0, long y0, int stride, int n) {
            super(n, n, stride, x0, y0, stride, World.PLOT_M, 0);
            this.x0 = x0;
            this.y0 = y0;
            this.ds = World.mix(seed ^ F_DITHER ^ (stride * 0x632BE59BD9B4E019L));
        }

        @Override double x(int px) { return x0 + (double) px * stride + stride * 0.5; }
        @Override double y(int py) { return y0 + (double) py * stride + stride * 0.5; }

        @Override boolean latLon(int px, int py, double[] ll) {
            double x = x(px), y = y(py);
            if (x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) return false;
            if (stride < WATER_STRIDE) return proj.latLonOfPlot(x, y, ll);
            int b = FAR_BLEND_PX, bx = px / b * b, by = py / b * b;
            if (bx != blendX || by != blendY) {
                blend = proj.blend(x0 + (double) bx * stride, y0 + (double) by * stride, (double) b * stride);
                blendX = bx;
                blendY = by;
            }
            return blend.latLon(x, y, ll);
        }

        @Override double dither(int px, int py, double lat, double lon) {
            if (stride < DITHER_STRIDE) return Double.NaN;
            long gy = y0 + (long) py * stride;
            return World.unit(World.mix(ds ^ World.mix((x0 + (long) px * stride) * 0x9E3779B97F4A7C15L ^ gy)));
        }

        @Override int[] tiles() { return nodeTiles(x0, y0, (double) stride * w); }

        @Override void tile(int t) {
            tile = t;
            blended = stride >= FAR_TILE_BLEND_STRIDE && tileCorners(t, corners, rim);
        }

        @Override boolean place(double lat, double lon, double[] xy, boolean river) {
            return EarthTerrain.this.place(lat, lon, blended, corners, tile, xy) && !(river && nearRim(xy, rim));
        }
    }

    /**
     * The globe's dither cell (spec-globe 3.5: "the dither's hash comes from
     * a fixed latitude-longitude grid at the pixel's scale, so the forest
     * does not shimmer as the globe turns"): rows of GLOBE_DITHER_DEG x 2^level
     * degrees, and in each row cells as many times as wide in longitude as
     * the least power of two that keeps them as wide on the ground as tall -
     * so a cell is the same ground whichever way the globe looks, about
     * square, and never narrower than it is tall.
     */
    public static final double GLOBE_DITHER_DEG = 4.0 / 60;

    /** The latitudes past which a dither cell is 2^m times as wide in longitude as tall, m = 1, 2, ...: where cos lat falls to 2^-m. */
    private static final double[] DITHER_WIDENS = new double[30];

    static {
        for (int m = 1; m <= DITHER_WIDENS.length; m++) DITHER_WIDENS[m - 1] = StrictMath.toDegrees(StrictMath.acos(StrictMath.scalb(1.0, -m)));
    }

    /** The globe's dither's stream (the far node's F_DITHER is the plane's, by plot and the world's seed). */
    private static final long F_GLOBE_DITHER = 0x610BED17L;

    /** The dither level of a pixel `degrees` of latitude tall: the least whose cell, GLOBE_DITHER_DEG x 2^level, is no shorter than the pixel. */
    public static int ditherLevel(double degrees) {
        return (int) Math.ceil(Math.log(degrees / GLOBE_DITHER_DEG) / Math.log(2) - 1e-9);
    }

    /** The globe's dither at (lat, lon) at a level: its cell's hash, a number in [0, 1) - a function of the point and the level alone, not of the view, the city or its seed. */
    public static double globeDither(int level, double lat, double lon) {
        double cell = StrictMath.scalb(GLOBE_DITHER_DEG, level);
        long r = (long) Math.floor((lat + 90) / cell);
        double mid = Math.abs(-90 + (r + 0.5) * cell);
        int m = 0;
        while (m < DITHER_WIDENS.length && mid > DITHER_WIDENS[m]) m++;
        double across = Math.min(360, StrictMath.scalb(cell, m));
        long col = (long) Math.floor((EarthProj.normaliseLon(lon) + 180) / across);
        long hsh = World.mix(F_GLOBE_DITHER ^ (level * 0x632BE59BD9B4E019L));
        hsh = World.mix(hsh ^ r);
        hsh = World.mix(hsh ^ (col * 0x9E3779B97F4A7C15L));
        return World.unit(hsh);
    }

    /** How many metres of ground a degree of latitude is on the sphere: pi R / 180. */
    public static final double M_A_DEGREE = Math.PI * EarthProj.RADIUS_M / 180;

    /** A latitude-longitude grid's camera: rows row0 to row0 + rows - 1 of the planet at `arcmin` minutes a texel, every column (360 x 60 / arcmin), row 0 at the north pole - the planet's picture (GlobePicture). */
    public Camera grid(int arcmin, int row0, int rows) {
        return new Grid(arcmin, row0, rows);
    }

    /**
     * The planet on a latitude-longitude grid: a pixel a texel, its centre
     * the texel's; the rule's stride at least FAR_STRIDE, so each pixel reads
     * the 4' far layers at its centre - at 4' its own cell of them (★E8); a
     * vertex at its column and row, the columns wrapping at the date line; a
     * column cos lat as wide on the ground as a row is tall (xScale()).
     */
    private final class Grid extends Camera {
        private final double perDeg;
        private final int level;

        Grid(int arcmin, int row0, int rows) {
            super(360 * 60 / arcmin, rows, Math.max(FAR_STRIDE, (int) Math.round(arcmin / 60.0 * M_A_DEGREE / World.PLOT_M)),
                    0, row0, 1, arcmin / 60.0 * M_A_DEGREE, 360 * 60 / arcmin);
            if (arcmin <= 0 || 60 % arcmin != 0) throw new IllegalArgumentException("a texel of " + arcmin + "' does not divide a degree");
            this.perDeg = 60.0 / arcmin;
            this.level = ditherLevel(arcmin / 60.0);
        }

        @Override double x(int px) { return 0; }
        @Override double y(int py) { return 0; }

        @Override boolean latLon(int px, int py, double[] ll) {
            ll[0] = 90 - (oy + py + 0.5) / perDeg;
            ll[1] = (px + 0.5) / perDeg - 180;
            return true;
        }

        @Override double dither(int px, int py, double lat, double lon) { return globeDither(level, lat, lon); }

        @Override int[] tiles() {
            double north = 90 - oy / perDeg + WATER_PAD_DEG, south = 90 - (oy + h) / perDeg - WATER_PAD_DEG;
            int r0 = (int) Math.max(-90, Math.floor(south)), r1 = (int) Math.min(89, Math.floor(north));
            int[] out = new int[(r1 - r0 + 1) * 360];
            int k = 0;
            for (int lat = r0; lat <= r1; lat++) {
                for (int lon = -180; lon < 180; lon++) {
                    int t = EarthData.tile(lat, lon);
                    if (data.entry("rivers", t) > 0 || data.entry("lakes.small", t) > 0) out[k++] = t;
                }
            }
            return Arrays.copyOf(out, k);
        }

        @Override boolean place(double lat, double lon, double[] xy, boolean river) {
            xy[0] = (EarthProj.normaliseLon(lon) + 180) * perDeg;
            xy[1] = (90 - lat) * perDeg;
            return true;
        }

        @Override double xScale(double lat) { return Math.max(1e-6, StrictMath.cos(StrictMath.toRadians(lat))); }
    }

    /**
     * The globe's camera: pixels sx0 to sx0 + w - 1 across and sy0 to
     * sy0 + h - 1 down of a view viewW x viewH pixels at kmPx km a pixel,
     * orthographic round its centre with its roll (EarthProj.Ortho) -
     * GlobeFrame's GROUND tier, a piece at a time. Its stride is a pixel's
     * ground in plots, at least DITHER_STRIDE (GlobeFrame's handoff).
     */
    public Camera globe(EarthProj.Ortho view, double kmPx, double viewW, double viewH, int sx0, int sy0, int w, int h) {
        return new Globe(view, kmPx, viewW, viewH, sx0, sy0, w, h);
    }

    /** The globe round a view centre with a roll: a pixel's centre through the orthographic inverse, a vertex through the forward (hidden on the far side), the forest dithered by globeDither() at the pixel's scale. */
    private class Globe extends Camera {
        final EarthProj.Ortho view;
        final double viewW, viewH, m;
        private final int level;

        Globe(EarthProj.Ortho view, double kmPx, double viewW, double viewH, int sx0, int sy0, int w, int h) {
            this(view, kmPx * 1000, Math.max(DITHER_STRIDE, (int) Math.round(kmPx * 1000 / World.PLOT_M)), viewW, viewH, sx0, sy0, w, h);
        }

        /** ...a pixel `m` metres, its rule's stride given (a chart's is not raised to DITHER_STRIDE), the first pixel at (ox, oy). */
        Globe(EarthProj.Ortho view, double m, int stride, double viewW, double viewH, double ox, double oy, int w, int h) {
            super(w, h, stride, ox, oy, 1, m, 0);
            this.view = view;
            this.viewW = viewW;
            this.viewH = viewH;
            this.m = m;
            this.level = ditherLevel(m / M_A_DEGREE);
        }

        @Override double x(int px) { return 0; }
        @Override double y(int py) { return 0; }

        @Override boolean latLon(int px, int py, double[] ll) {
            double sx = ox + px + 0.5, sy = oy + py + 0.5;
            return view.inverse((sx - viewW / 2) * m, (viewH / 2 - sy) * m, ll);
        }

        @Override double dither(int px, int py, double lat, double lon) { return globeDither(level, lat, lon); }

        @Override int[] tiles() { return globeTiles(view, m, viewW, viewH, ox, oy, w, h); }

        @Override boolean place(double lat, double lon, double[] xy, boolean river) {
            if (!view.forward(lat, lon, xy)) return false;
            xy[0] = viewW / 2 + xy[0] / m;
            xy[1] = viewH / 2 - xy[1] / m;
            return true;
        }
    }

    /**
     * A chart's camera (MS3; GlobeFrame.chart()): node (nx, ny) of an anchor's
     * chart - MapFrame.NODE_PX square pixels of `plots` x World.PLOT_M metres
     * each, pixel (i, j)'s centre at (nx NODE_PX + i + 0.5) such pixels east
     * and (ny NODE_PX + j + 0.5) south of the anchor on its orthographic
     * plane, north up. True in shape where the city's plane is not: the plane
     * is Lambert's equal-area projection, whose shape distortion
     * 2 / (1 + cos c) at an angle c from the city stretches the plane's noise
     * into streaks along the line to the city (GlobeCheck 2d), and a chart is
     * the globe's own camera about a point near the pixels, whose shape is
     * true to a few hundredths over the ground a view covers.
     *
     * It is the Globe camera of a view of size 0 x 0 whose first pixel is the
     * node's, but for three things: its stride is `plots` (not raised to
     * DITHER_STRIDE), so the cover is ranked through its noise and the
     * relief too (dither() is NaN); every noise read is the sphere's
     * (sphereNoise()) at the pixel's unit vector, planet-fixed, so a pixel
     * read from two charts reads the same noise; and, within `bandRad` past
     * the angle `fromUp` names from the city, the sphere's noise is blended
     * in (blendNoise()) from the plane's at the pixel's own plot, so the
     * patches carry across the hand-over and change over the band with no
     * line anywhere. Past the band the plane's plot is never worked out.
     */
    private final class Chart extends Globe {
        private final double cosLat0, sinLat0, cosLon0, sinLon0;
        private final double cityX, cityY, cityZ, eastX, eastY, northX, northY, northZ;
        private final double fromUp, c0, bandRad, cosEnd;
        private double t, sx, sy, sz, planeX, planeY;

        Chart(EarthProj.Ortho anchor, int plots, long nx, long ny, double fromUp, double bandRad) {
            super(anchor, plots * World.PLOT_M, plots, 0, 0, (double) nx * MapFrame.NODE_PX, (double) ny * MapFrame.NODE_PX, MapFrame.NODE_PX, MapFrame.NODE_PX);
            double p = StrictMath.toRadians(anchor.centreLat()), l = StrictMath.toRadians(anchor.centreLon());
            cosLat0 = StrictMath.cos(p);
            sinLat0 = StrictMath.sin(p);
            cosLon0 = StrictMath.cos(l);
            sinLon0 = StrictMath.sin(l);
            // The city's frame, planet-fixed: its unit vector, and its east and north (EarthProj.forward()'s).
            double cp = StrictMath.toRadians(proj.centreLat()), cl = StrictMath.toRadians(proj.centreLon());
            double cosP = StrictMath.cos(cp), sinP = StrictMath.sin(cp), cosL = StrictMath.cos(cl), sinL = StrictMath.sin(cl);
            cityX = cosP * cosL;
            cityY = cosP * sinL;
            cityZ = sinP;
            eastX = -sinL;
            eastY = cosL;
            northX = -sinP * cosL;
            northY = -sinP * sinL;
            northZ = cosP;
            this.fromUp = fromUp;
            this.c0 = StrictMath.acos(Math.max(-1, Math.min(1, fromUp)));
            this.bandRad = bandRad;
            this.cosEnd = c0 + bandRad < Math.PI ? StrictMath.cos(c0 + bandRad) : -2;
        }

        /** Aims the noise at the pixel: its point on the planet, planet-fixed, in plots; t, the share of the sphere's noise it reads (0 to 1, by its angle from the city); and, where t is under 1, its plot on the city's plane. False off the disc. */
        @Override boolean aim(int px, int py) {
            double e = (ox + px + 0.5) * m / EarthProj.RADIUS_M, n = -(oy + py + 0.5) * m / EarthProj.RADIUS_M, s2 = e * e + n * n;
            if (s2 > 1) return false;
            double a = Math.sqrt(1 - s2);
            // The anchor's frame (Ortho.inverse()'s: px = a cos lat0 - n sin lat0, py = e, pz = a sin lat0 + n cos lat0) turned about the axis to the planet's.
            double fx = a * cosLat0 - n * sinLat0, fy = e, ux = fx * cosLon0 - fy * sinLon0, uy = fx * sinLon0 + fy * cosLon0, uz = a * sinLat0 + n * cosLat0;
            double rPlots = EarthProj.RADIUS_M / World.PLOT_M;
            sx = ux * rPlots;
            sy = uy * rPlots;
            sz = uz * rPlots;
            double up = ux * cityX + uy * cityY + uz * cityZ;
            if (up >= fromUp) t = 0;
            else if (up <= cosEnd || !(1 + up > 0)) t = 1;
            else t = (StrictMath.acos(up) - c0) / bandRad;
            if (t < 1) {
                double k = EarthProj.RADIUS_M * Math.sqrt(2 / (1 + up));
                planeX = EarthProj.plotX(k * (ux * eastX + uy * eastY));
                planeY = EarthProj.plotY(k * (ux * northX + uy * northY + uz * northZ));
            }
            return true;
        }

        /** The aimed pixel's noise in the field `os` at `scale` (1, or the relief's RELIEF_SCALE) and a stride: the sphere's, blended in from the plane's at its plot within the band. */
        double noise(long[] os, double scale, double stride) {
            double st = stride / scale;
            if (t >= 1) return sphereNoise(os, sx / scale, sy / scale, sz / scale, st);
            double plane = noiseAt(os, planeX / scale, planeY / scale, st);
            return t <= 0 ? plane : blendNoise(plane, sphereNoise(os, sx / scale, sy / scale, sz / scale, st), t);
        }

        @Override boolean latLon(int px, int py, double[] ll) {
            if (!super.latLon(px, py, ll)) return false;
            return aim(px, py);
        }

        @Override double x(int px) { return planeX; }
        @Override double y(int py) { return planeY; }

        @Override double dither(int px, int py, double lat, double lon) { return Double.NaN; }
    }

    /**
     * The camera of node (nx, ny) of the chart about an anchor (MS3): the
     * far rule through the planet's own orthographic camera, true in shape,
     * `plots` plots a pixel, MapFrame.NODE_PX pixels a side. From `fromUp`
     * (the cosine of the angle from the city, GlobeFrame.handoverUp()) the
     * sphere's noise is blended in over `bandRad` radians from the plane's;
     * past the band the pixel is the sphere's alone. farTerrain(Camera, out,
     * relief) draws it; everything the rule reads of the planet is the
     * Globe camera's but the noise.
     */
    public Camera chart(EarthProj.Ortho anchor, int plots, long nx, long ny, double fromUp, double bandRad) {
        return new Chart(anchor, plots, nx, ny, fromUp, bandRad);
    }

    /**
     * The 1-degree tiles whose rivers and small lakes may reach a piece of
     * the globe's screen: (NODE_EDGE_POINTS + 1)^2 points over the piece -
     * those past the disc taken at its limb - give a box of latitude and its
     * arc of longitude (the widest gap between them left out), widened by
     * WATER_PAD_DEG and the widest step between neighbouring points; every
     * longitude where a pole is in the piece.
     */
    private int[] globeTiles(EarthProj.Ortho view, double m, double viewW, double viewH, double sx0, double sy0, int w, int h) {
        int g = NODE_EDGE_POINTS;
        double[][] pts = new double[(g + 1) * (g + 1)][];
        double[] ll = new double[2];
        double minLat = 90, maxLat = -90, gap = 0;
        double limb = EarthProj.RADIUS_M * (1 - 1e-9);
        for (int j = 0; j <= g; j++) {
            for (int i = 0; i <= g; i++) {
                double x = (sx0 + (double) w * i / g - viewW / 2) * m, y = (viewH / 2 - sy0 - (double) h * j / g) * m;
                double r = Math.sqrt(x * x + y * y);
                if (r > limb) {
                    x *= limb / r;
                    y *= limb / r;
                }
                view.inverse(x, y, ll);
                pts[j * (g + 1) + i] = new double[] { ll[0], ll[1] };
                minLat = Math.min(minLat, ll[0]);
                maxLat = Math.max(maxLat, ll[0]);
            }
        }
        // The widest step between neighbouring points, in degrees on the sphere: how far the ground between them may reach past the box.
        for (int j = 0; j <= g; j++) {
            for (int i = 0; i <= g; i++) {
                double[] p = pts[j * (g + 1) + i];
                if (i < g) gap = Math.max(gap, EarthProj.distanceM(p[0], p[1], pts[j * (g + 1) + i + 1][0], pts[j * (g + 1) + i + 1][1]));
                if (j < g) gap = Math.max(gap, EarthProj.distanceM(p[0], p[1], pts[(j + 1) * (g + 1) + i][0], pts[(j + 1) * (g + 1) + i][1]));
            }
        }
        double pad = WATER_PAD_DEG + gap / M_A_DEGREE;
        double[] xy = new double[2];
        boolean north = view.forward(90, 0, xy) && inPiece(xy, m, viewW, viewH, sx0, sy0, w, h);
        boolean south = view.forward(-90, 0, xy) && inPiece(xy, m, viewW, viewH, sx0, sy0, w, h);
        double lat0 = (south ? -90 : minLat) - pad, lat1 = (north ? 90 : maxLat) + pad;
        // The arc of longitude the points hold: the circle less its widest gap between them.
        double[] lons = new double[pts.length];
        for (int k = 0; k < pts.length; k++) lons[k] = pts[k][1];
        Arrays.sort(lons);
        double widest = lons[0] + 360 - lons[lons.length - 1], from = lons[0];
        for (int k = 1; k < lons.length; k++) {
            if (lons[k] - lons[k - 1] > widest) {
                widest = lons[k] - lons[k - 1];
                from = lons[k];
            }
        }
        double span = 360 - widest;
        double cos = StrictMath.cos(StrictMath.toRadians(Math.min(89.9, Math.max(Math.abs(lat0), Math.abs(lat1)))));
        double padLon = pad / cos;
        boolean allLon = north || south || lat1 >= 90 || lat0 <= -90 || span + 2 * padLon >= 360;
        int r0 = (int) Math.max(-90, Math.floor(lat0)), r1 = (int) Math.min(89, Math.floor(lat1));
        long c0 = allLon ? -180 : (long) Math.floor(from - padLon), c1 = allLon ? 179 : (long) Math.floor(from + span + padLon);
        int[] out = new int[(r1 - r0 + 1) * (int) (c1 - c0 + 1)];
        int k = 0;
        for (int lat = r0; lat <= r1; lat++) {
            for (long lon = c0; lon <= c1; lon++) {
                int t = EarthData.tile(lat, (int) (Math.floorMod(lon + 180, 360L) - 180));
                if (data.entry("rivers", t) > 0 || data.entry("lakes.small", t) > 0) out[k++] = t;
            }
        }
        return Arrays.copyOf(out, k);
    }

    /** Whether a point of the view's plane (metres) lies on the piece's pixels. */
    private static boolean inPiece(double[] xy, double m, double viewW, double viewH, double sx0, double sy0, int w, int h) {
        double sx = viewW / 2 + xy[0] / m, sy = viewH / 2 - xy[1] / m;
        return sx >= sx0 && sx <= sx0 + w && sy >= sy0 && sy <= sy0 + h;
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
       THE SEA'S DEPTH (0.7.121, batch E4; ET1, 2026-10-24: spec-earth2-terrain 7)

       The generated world's sea has a depth from its elevation field
       (World.depthAt(), 0.7.79). The real Earth's sea has its own: earth-2's
       depth layer, the mean of the sea in each 1' cell (1.85 km) from GEBCO,
       in a code a byte (EarthData.depthM()).

       WHY THE GUESS RETIRED. earth-1 shipped no heights, so E4 (★E4-1)
       guessed: SHELF_BREAK_M x a plot's distance from the coast over
       SHELF_WIDTH_M, 54,167 m, the distance within which the generated
       world's share of the sea lies - linear from the coast and on at the
       same slope. GEBCO showed it wrong both ways (the study's 7): the North
       Sea 40 and 80 km off Rotterdam is 27 and 34 m deep, not 69 and 150, so
       a broad shelf came out three to nine times too deep and no platform
       stood past 58 km where the real sea is 20 to 40 m; off Valparaiso the
       sea is 656 m deep 20 km out, not 44, and a platform could stand on 2.4
       km of water. Jerus decided (D17, 2026-10-12) the real depth replaces
       it; the shelf's width, its measure over the Earth's sea and the
       coast-distance search that fed them went with it.

       THE DEPTH A PLOT HAS is the layer's at the plot's centre, at least a
       metre (the file's promise: a sea plot is never shallower than 1 m in
       the game, because World.depthAt() over 0 is how the land office tells
       a sea field from a dry one); 0 where the plot is not the sea's (not
       SALT or SHALLOW). Past the planet's rim, where the layer has no
       ground, the deepest it says. The shallow sea (World.SHALLOW, a 15"
       cell's share of its sea under 30 m) and this depth (a 1' cell's mean)
       are two readings of GEBCO and need not agree at a plot: a SHALLOW plot
       may be 34 m deep, and a SALT one 12.
       ===================================================================== */

    /** The sea layer's levels under which a 15" cell is the coast's land side: the share under SEA_LEVEL, 0 to 7 of 15 - the land of the landmass layer (tools/earth/FORMAT-2.txt). */
    static final int COAST_BELOW = (int) Math.ceil(SEA_LEVEL * EarthData.SHARE_LEVELS);

    /** The shallowest a sea plot is, metres: 1 (FORMAT-2: "a sea plot is never shallower than 1 m in the game"), so World.depthAt() over 0 is still how a sea plot is told from land. */
    public static final double SEA_LEAST_DEPTH_M = 1;

    /** Depths kept, by plot: 4,096 (a field's depth is asked again every time the city's ground changes). */
    static final int DEPTHS_KEPT = 4096;

    private final Map<Long, Double> depths = new LinkedHashMap<>(256, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, Double> eldest) {
            return size() > DEPTHS_KEPT;
        }
    };

    /**
     * The sea's depth at a plot's centre, metres below its level (ET1, the
     * study's 7): 0 where the plot is not the sea's (World.salt()), else the
     * depth layer's metres there (EarthData.depthM()), at least
     * SEA_LEAST_DEPTH_M, and the layer's deepest, EarthData.DEPTH_DEEPEST_M,
     * past the planet's rim. World.depthAt() on Earth. Kept by plot
     * (DEPTHS_KEPT). A reader without the layer (an earth-1 file) has no depth
     * to give: it throws, naming the file that has.
     */
    public double depthAt(long x, long y) {
        if (!World.salt(terrainAt(x, y))) return 0;
        long k = (x << 32) ^ (y & 0xffffffffL);
        synchronized (depths) {
            Double d = depths.get(k);
            if (d != null) return d;
        }
        if (!data.hasRelief()) throw new IllegalStateException("this data has no depth layer; earth-2.bin does (" + data.identity() + ")");
        double[] ll = new double[2];
        double depth = proj.latLonOfPlot(x + 0.5, y + 0.5, ll) ? depthOf(ll[0], ll[1]) : EarthData.DEPTH_DEEPEST_M;
        synchronized (depths) { depths.put(k, depth); }
        return depth;
    }

    /**
     * The depth layer's metres at a point of the planet (ET1 slice D; the globe's hover, which has a point and no plot): what
     * depthAt() gives the plot there if it is sea - at least SEA_LEAST_DEPTH_M - whether or not the point is sea. Needs
     * data with the depth layer (an earth-1 file throws).
     */
    public double depthOf(double lat, double lon) {
        if (!data.hasRelief()) throw new IllegalStateException("this data has no depth layer; earth-2.bin does (" + data.identity() + ")");
        return Math.max(SEA_LEAST_DEPTH_M, data.depthM(lat, lon));
    }

    /**
     * Each world cell's land and forest, as shares of its area (spec-earth
     * 4): CELL_SAMPLES x CELL_SAMPLES points a cell, each read from the 4'
     * far layers - its land the ground that is not sea, dry and lake (the
     * generated world's land holds its lakes too) - and its forest the dry
     * share times land cover's forest level; a point off the planet holds
     * neither. Each cell's points from its four corners, blended
     * (EarthProj.blend()). What the fields are drawn on (World's deposits)
     * and the far views ask; 0.39 to 0.42 s on the wall clock when it was
     * written (the study 0.37 to 0.45). EarthTerrainCheck 9 holds it to
     * CELLS_PASS_MS of the main thread's CPU since batch EC1 (0.42 to 0.55 s
     * measured, the first time, the wall clock printed beside it).
     *
     * Since 0.7.117 (batch E3) each point also reads its province
     * (Geology): a resource's FIELD LAND is the points' land times the
     * province's multiplier (Geology.multiplier(): the weight x Z), oil's
     * with the sea of a shelf, basin or rift province too - what
     * World.countOf() draws an Earth cell's fields on, so a cell across two
     * provinces takes each its share (spec-earth 4). Since 0.8.12 (batch EG1)
     * iron's multiplier is the point's (Geology.multiplierAt(): a recorded
     * iron district lifts it), and the per-point arithmetic is
     * PointWeights.of(), which pointFieldWeights() reads too, so the two
     * cannot disagree.
     */
    public void cellsPass() {
        long t0 = System.nanoTime();
        int cells = World.CELLS, n = cells * cells, m = CELL_SAMPLES;
        double[] land = new double[n], forest = new double[n];
        Geology geo = geology();
        Resource[] kinds = Resource.values();
        float[][] fields = new float[kinds.length][];
        for (Resource r : kinds) if (r.inFields()) fields[r.ordinal()] = new float[n];
        PointWeights pw = pointWeights();
        double[] sum = new double[kinds.length];
        EarthData.Layer cover = data.layer("landcover");
        int ccpd = cover.cellsPerDegree;
        FarReader far = new FarReader();
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
                    far.read(ll[0], ll[1]);
                    int dry = far.dry, wet = far.wet;
                    double pl = (dry + wet) / (double) EarthData.FAR_LEVELS;
                    l += pl;
                    int row = geo.provinceAt(ll[0], ll[1]);
                    for (int k = 0; k < kinds.length; k++) {
                        if (fields[k] == null) continue;
                        sum[k] += pw.of(k, row, ll[0], ll[1], pl, dry + wet);
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

    /* =====================================================================
       FIELD LAND AT A POINT (0.8.12, batch EG1; spec-earth2-terrain 8.5)

       The cells pass sums, for each of a cell's 16 points, the point's land
       (and for oil the sea of a shelf, basin or rift) times the resource's
       multiplier there. EG1 draws a cluster's centre among those 16 points by
       the same weights, and keeps a centre and a field only on ground that
       holds the resource (fieldGround()), so the arithmetic of one point is
       written once, PointWeights.of(), and the pass and pointFieldWeights()
       both call it; the reading of the far layers is written once too,
       FarReader.read().
       ===================================================================== */

    /** Reads the 4' far layers at a point, keeping the bands it has inflated (a band of a layer is a row of degrees all round the planet): the cells pass's reader, and pointFieldWeights()'s - dry and wet, 0 to FAR_LEVELS each. */
    private final class FarReader {
        final EarthData.Layer farLand = data.layer("far.land"), farLake = data.layer("far.lake");
        final int fcpd = farLand.cellsPerDegree, fcols = 360 * fcpd;
        final byte[][] landBands = new byte[180][], lakeBands = new byte[180][];
        final int[] landUniform = new int[180], lakeUniform = new int[180];
        final boolean[] bandRead = new boolean[180];
        int dry, wet;

        /** The point's dry land and its lakes, in the layers' levels, left in dry and wet. */
        void read(double lat, double lon) {
            long r = Math.min(180L * fcpd - 1, Math.max(0, (long) Math.floor((90 - lat) * fcpd)));
            long col = Math.floorMod((long) Math.floor((lon + 180) * fcpd), (long) fcols);
            int band = (int) (179 - r / fcpd);
            if (!bandRead[band]) {
                bandRead[band] = true;
                int e = farLand.entries[band];
                if (e <= 0) landUniform[band] = -e; else landBands[band] = data.raw("far.land", band);
                e = farLake.entries[band];
                if (e <= 0) lakeUniform[band] = -e; else lakeBands[band] = data.raw("far.lake", band);
            }
            int at = (int) ((r % fcpd) * fcols + col);
            dry = landBands[band] == null ? landUniform[band] : landBands[band][at] & 0xFF;
            wet = lakeBands[band] == null ? lakeUniform[band] : lakeBands[band][at] & 0xFF;
        }
    }

    /** What a point contributes to each resource's field land: its land share (and oil's sea share on a province whose sea holds oil) times the multiplier there. Made once a world (pointWeights()). */
    private static final class PointWeights {
        private final Geology geo;
        private final double[][] mult;
        private final boolean[] seaOil;
        private final int oil = Resource.OIL.ordinal(), iron = Resource.IRON.ordinal();

        PointWeights(Geology geo) {
            this.geo = geo;
            Resource[] kinds = Resource.values();
            mult = new double[kinds.length][];
            for (Resource r : kinds) {
                if (!r.inFields()) continue;
                mult[r.ordinal()] = new double[geo.rows()];
                for (int row = 0; row < geo.rows(); row++) mult[r.ordinal()][row] = geo.multiplier(r, row);
            }
            seaOil = new boolean[geo.rows()];
            for (int row = 0; row < geo.rows(); row++) seaOil[row] = geo.seaHoldsOil(row);
        }

        /** Resource k's weight at a point in province `row`: `pl` the point's land share, `levels` its dry and lake levels added (FAR_LEVELS less them is the sea). */
        double of(int k, int row, double lat, double lon, double pl, int levels) {
            double a = k == oil && seaOil[row] ? pl + Math.max(0, EarthData.FAR_LEVELS - levels) / (double) EarthData.FAR_LEVELS : pl;
            return a * (k == iron ? geo.multiplierAt(Resource.IRON, row, lat, lon) : mult[k][row]);
        }
    }

    private volatile PointWeights pointWeights;

    private PointWeights pointWeights() {
        PointWeights p = pointWeights;
        if (p == null) {
            synchronized (this) {
                if (pointWeights == null) pointWeights = new PointWeights(geology());
                p = pointWeights;
            }
        }
        return p;
    }

    /**
     * The weights a cell's CELL_SAMPLES x CELL_SAMPLES points carry for a
     * resource (EG1), in the cells pass's order - rows from the north, each
     * from the west, j x CELL_SAMPLES + i - each the point's land (oil's with
     * a sea that holds it) times the multiplier there, nought for a point off
     * the planet: their mean is cellFieldLand(), to its float rounding. What
     * World draws a cluster's centre by, so it stands where the cell's
     * province holds the resource.
     */
    double[] pointFieldWeights(int cell, Resource r) {
        int m = CELL_SAMPLES, cells = World.CELLS;
        double[] out = new double[m * m];
        if (!r.inFields() || cell < 0 || cell >= cells * cells) return out;
        Geology geo = geology();
        PointWeights pw = pointWeights();
        FarReader far = new FarReader();
        long x0 = (long) (cell % cells) * World.CELL, y0 = (long) (cell / cells) * World.CELL;
        EarthProj.Blend b = proj.blend(x0, y0, World.CELL);
        double[] ll = new double[2];
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < m; i++) {
                if (!b.latLon(x0 + (i + 0.5) * World.CELL / m, y0 + (j + 0.5) * World.CELL / m, ll)) continue;
                far.read(ll[0], ll[1]);
                out[j * m + i] = pw.of(r.ordinal(), geo.provinceAt(ll[0], ll[1]), ll[0], ll[1], (far.dry + far.wet) / (double) EarthData.FAR_LEVELS, far.dry + far.wet);
            }
        }
        return out;
    }

    /**
     * Whether a point (plot coordinates) is ground a resource's fields lie
     * on (EG1): the province under it (Geology.provinceAt(), the patches in)
     * gives the resource a multiplier above nought there (Geology.multiplierAt(),
     * a district's for iron), and the point is land - or, for oil, the sea of
     * a province whose sea holds oil. A point off the planet holds none. What
     * World asks before it keeps a cluster's centre or a field's.
     */
    boolean fieldGround(Resource r, double x, double y) {
        double[] ll = scratch().ll;
        if (!proj.latLonOfPlot(x, y, ll)) return false;
        Geology geo = geology();
        double lat = ll[0], lon = ll[1];
        int row = geo.provinceAt(lat, lon);
        if (!(geo.multiplierAt(r, row, lat, lon) > 0)) return false;
        return !seaAt(x, y) || (r == Resource.OIL && geo.seaHoldsOil(row));
    }

    /** The geology of its data (Geology.of()): the provinces, their classes and multipliers. */
    public Geology geology() { return Geology.of(data); }

    /**
     * A cell's FIELD LAND for a resource, as a share of its area (0.7.117):
     * its 16 points' land each times the multiplier there (Geology.multiplier(),
     * and since EG1 iron's at the point, Geology.multiplierAt()), oil's with a
     * shelf's, basin's or rift's sea - what today's density is taken over
     * (World.countOf()). Its land, cellLand(), for forest.
     */
    double cellFieldLand(int cell, Resource r) {
        build();
        float[] f = cellFields[r.ordinal()];
        return f == null ? cellLand[cell] : f[cell];
    }

    /** The province under a point in plot coordinates, its row in the data; 0 off the planet or in none. Geology.provinceAt()'s, so a patch (EG1) is in. */
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
     * buildable ground with eight buildable points World.SITE_DRY_PLOTS round
     * it, ring by ring of one plot (a ring the plots k to k + 1 plots off,
     * nearest first, then north to south and west to east), out to
     * SNAP_PLOTS. Buildable is World.buildable() of the ground as drawn and
     * its relief (ET2, spec-earth2-terrain 3.4): dry - not sea, not fresh
     * water, rivers included, and since 0.7.127 not ice, which is never a
     * founding site - and flat or foothill, so no city is founded where no
     * building may stand.
     */
    private void snap() {
        long[] s = siteNear();
        siteTests = (int) s[2];
        if (s[0] < 0) throw new IllegalStateException("no buildable ground within " + SNAP_PLOTS + " plots of (" + key.lat() + ", " + key.lon() + ") to found a city on");
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
                if (siteBuildable(cx + p[2], cy + p[1])) {
                    site = new long[] { cx + p[2], cy + p[1], tests };
                    return site.clone();
                }
            }
        }
        site = new long[] { -1, -1, tests };
        return site.clone();
    }

    /** The founding's test 1 at plot (px, py) (ET2: buildable ground, spec-earth2-terrain 3.4): it and eight points World.SITE_DRY_PLOTS round its centre are ground a building may stand on - dry, and flat or foothill (World.buildable()). */
    boolean siteBuildable(long px, long py) {
        if (!buildable(px, py)) return false;
        double x = px + 0.5, y = py + 0.5;
        for (int q = 0; q < 8; q++) {
            double a = q * Math.PI / 4;
            if (!buildable((long) Math.floor(x + World.SITE_DRY_PLOTS * StrictMath.cos(a)), (long) Math.floor(y + World.SITE_DRY_PLOTS * StrictMath.sin(a)))) return false;
        }
        return true;
    }

    /** Whether plot (x, y) is ground a building may stand on: its cover is dry, and its relief - read only where it is - flat or foothill. */
    private boolean buildable(long x, long y) {
        byte c = terrainAt(x, y);
        return World.dry(c) && World.buildable(c, reliefAt(x, y));
    }

    private volatile long[] snapGround, beyond;

    /**
     * What the snap's disc holds when it found no site (ET2): {dry plots, and of them the steep and very steep} within
     * SNAP_PLOTS of the spot, counted plot by plot from the tiles drawn for them - what tells a spot among steep slopes
     * from a spot in the sea or on an island too small (the card's words, EarthSite.noSiteWords()). Worked out once.
     */
    long[] snapGround() {
        long[] g = snapGround;
        if (g != null) return g.clone();
        long cx = World.SIDE / 2, cy = World.SIDE / 2, r = SNAP_PLOTS, dry = 0, steep = 0;
        int n = World.TILE;
        for (long ty = Math.floorDiv(cy - r, n); ty <= Math.floorDiv(cy + r, n); ty++) {
            for (long tx = Math.floorDiv(cx - r, n); tx <= Math.floorDiv(cx + r, n); tx++) {
                byte[] t = keptTile(tx, ty), rt = null;
                for (int py = 0; py < n; py++) {
                    long dy = ty * n + py - cy;
                    if (dy * dy > r * r) continue;
                    for (int px = 0; px < n; px++) {
                        long dx = tx * n + px - cx;
                        if (dx * dx + dy * dy > r * r || !World.dry(t[py * n + px])) continue;
                        dry++;
                        if (rt == null) rt = keptRelief(tx, ty);
                        if (rt[py * n + px] >= World.STEEP) steep++;
                    }
                }
            }
        }
        snapGround = new long[] { dry, steep };
        return snapGround.clone();
    }

    /**
     * The nearest buildable ground past a snap that found none (ET2, spec-earth2-terrain 3.4): {x, y} of the nearest
     * spot that passes the founding's test 1 (siteBuildable()) on a lattice of BEYOND_STEP plots from the spot, rings of
     * the lattice - the ring k the lattice steps k to k + 1 off, nearest first, then north to south and west to east -
     * from the snap's reach out to BEYOND_PLOTS; {-1, -1} for none within it. Worked out once, and only for a card that
     * asks (a spot among steep slopes: EarthSite.noSiteWords()).
     */
    long[] siteBeyond() {
        long[] b = beyond;
        if (b != null) return b.clone();
        long cx = World.SIDE / 2, cy = World.SIDE / 2;
        int first = (SNAP_PLOTS + BEYOND_STEP - 1) / BEYOND_STEP, last = BEYOND_PLOTS / BEYOND_STEP;
        List<long[]> ring = new ArrayList<>();
        for (int k = first; k <= last; k++) {
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
                long x = cx + p[2] * BEYOND_STEP, y = cy + p[1] * BEYOND_STEP;
                if (x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) continue;
                if (siteBuildable(x, y)) {
                    beyond = new long[] { x, y };
                    return beyond.clone();
                }
            }
        }
        beyond = new long[] { -1, -1 };
        return beyond.clone();
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
