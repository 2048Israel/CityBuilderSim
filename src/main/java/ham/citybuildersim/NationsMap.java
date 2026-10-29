package ham.citybuildersim;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.ToDoubleFunction;

/**
 * The nations on the map, without the toolkit: the borders simplified and cut to a view, each nation's dot and the name written on its land, what lies
 * under a point, and the card a click opens - everything ui/MapView draws the nations by, as arithmetic a harness can hold (GlobeCheck 9 to 12 and
 * MapCheck 19 hold it), as LandMap and GlobeFrame hold the land office's and the globe's.
 *
 * WHY THIS EXISTS (batch WN2; docs/studies/spec-nations.md 6.1 and 8, board 1). The map had no nations and no borders; WN0 baked Natural Earth's lines
 * and a nation for every 15" cell into world-1.bin and WN1 gave each nation a year, and Jerus asked for "a dot at each nation's node, its area by
 * output", "a toggle on the map ui", "a map button to ... toggle borders", "each nation stats on their land ... canada would show gdp and ...
 * population" and "clicking would show more stats based on the leaderboard". The map must not get slower (MP1 made it fast), so nothing here walks the
 * file's vertices or its 15" cells in a frame: the lines are kept once at five levels of detail (LOD_KM), each with the cap that holds it, so a frame
 * culls to the lines whose cap meets the view and projects only the vertices a pixel could tell apart; the labels are tried on a coarse grid of
 * owners, a byte read where ownerAt() on 15" cells would inflate tiles. Nothing in the month reads this; it is the screen's.
 *
 * THE LEVELS OF DETAIL. A line's vertex has a significance, the km it stands off the chord of the Douglas-Peucker range it was chosen to split,
 * made no greater than its parent's, so a level keeps the vertices whose significance exceeds its tolerance and each level's vertices are a subset of
 * the one before (never more), every vertex a level drops within its tolerance of the segment that spans it. The distance is measured in three
 * dimensions, from the vertex to the chord between the two kept vertices on the sphere of RADIUS_M, not on a flat approximation: the screen draws the
 * straight chord between two projected vertices, and an orthographic projection is a plain projection of the three-dimensional point, so the
 * chord's three-dimensional miss bounds what the eye sees; a 2,000 km stretch of the 49th parallel, straight in latitude and longitude, bows 120 km
 * off its chord on the globe, and a flat measure would have dropped every vertex along it. A frame takes the coarsest level whose tolerance is no more
 * than LOD_PX of a pixel at the view's km a pixel.
 *
 * THE COARSE GRID is the owner layer at COARSE_PER_DEGREE cells a degree: each cell takes the owner of the 15" cell at its centre (the fine cell
 * CENTRE of the cell's FINE_PER_COARSE along each side), and a tile with one owner fills its cells with it. It is deterministic and it is a sample, not
 * a vote; the land's edge is where it is least exact, and the sea of a coastal tile that is one nation's belongs to that nation, as in the owner
 * layer. A label is tried on it; a click reads the exact ownerAt().
 *
 * ONE THREAD. Built once per world file (of(), on the map's worker thread), it is immutable but for three caches - the globe's borders for the last
 * view, the lines crossing the last box of the city's plane, and the last year's figures and ranks - which are used from the screen's thread alone.
 * It keeps the WorldData it was built from for the exact ownerAt(), so the caller keeps that open.
 */
public final class NationsMap {

    /* =====================================================================
       THE DIALS
       ===================================================================== */

    /** The levels of detail of a line, as the tolerance in km each keeps its vertices within of the file's own: 0 is the file's, then 1, 4, 16 and 64 km. */
    public static final double[] LOD_KM = { 0, 1, 4, 16, 64 };

    /** A frame takes the coarsest level whose tolerance is no more than this many pixels at the view's km a pixel: half a pixel, under which nobody sees the difference. */
    public static final double LOD_PX = 0.5;

    /** The coarse owner grid's cells a degree: 8 (2,880 x 1,440 bytes), a cell 30 of the owner layer's 15" cells across, 14 km at the equator. */
    public static final int COARSE_PER_DEGREE = 8;

    /** A stretch of a line more than this many pixels off the canvas is dropped, and the run is cut there: 8, so a dash pattern does not start on the canvas's own edge. */
    public static final double DRAW_MARGIN_PX = 8;

    /** The slack added to a view's cap and a line's, radians: 0.005 (32 km), so a line the chord of its own vertices only grazes is not culled. */
    static final double CAP_SLACK_RAD = 0.005;

    /** A dot's radius is this many pixels... (the board's: 1.6) */
    public static final double DOT_BASE_PX = 1.6;

    /** ...plus this many for each root of a trillion US$ of output (1.9)... */
    public static final double DOT_PER_ROOT_PX = 1.9;

    /** ...and never less than this (2.5), so the smallest nation is still a dot to point at... */
    public static final double DOT_MIN_PX = 2.5;

    /** ...and never more than this (11), so the largest does not cover its neighbours. */
    public static final double DOT_MAX_PX = 11;

    /** A label's second line, the figures, stands this many pixels under the first, the name (the board's 12 px for its 11 px name and 9.5 px figures). */
    public static final double LABEL_LINE_PX = 12;

    /** A label's box is the wider line's width by this many pixels: 26, the two lines and the room round them. */
    public static final double LABEL_H_PX = 26;

    /** A label's box starts this many pixels above the name's baseline (the 11 px name's height), so the box is the name, the figures and what hangs below. */
    public static final double LABEL_ASCENT_PX = 11;

    /** A nation is written on only if its coarse area, in the view's pixels, is at least this many times the label's box: 2. */
    public static final double LABEL_AREA_TIMES = 2;

    /** A label's box is tried at a grid of this many points across... */
    public static final int LABEL_SAMPLES_X = 5;

    /** ...by this many down: 3, fifteen points. */
    public static final int LABEL_SAMPLES_Y = 3;

    /** At least this share of the box's points must lie on the nation's own coarse cells: 0.6 (nine of fifteen). */
    public static final double LABEL_OWN_SHARE = 0.6;

    /** A click or a hover lands on a dot within its radius and this many pixels more: 3. */
    public static final double HIT_SLOP_PX = 3;

    /** The last line of a nation's hover, which says what a click does (the board's). */
    public static final String CLICK_FOR_CARD = "Click for its card";

    /** The legend's words for a border line (the board's). */
    public static final String BORDER_WORDS = "A border";

    /** The legend's words for a nation's dot (the board's wording, the dot's area by output). */
    public static final String NATION_WORDS = "A nation, by its economy";

    /** A border line's colour, ARGB: white, drawn at BORDER_ALPHA and dashed. */
    public static final int BORDER_ARGB = 0xffffffff;

    /** A border line's opacity: 0.7. */
    public static final double BORDER_ALPHA = 0.7;

    /** A border line's dash, in pixels drawn and pixels left: 4 and 3 (the board's). */
    public static final double[] BORDER_DASH_PX = { 4, 3 };

    /** A nation's dot, ARGB: the board's pale blue-white (e6edf3)... */
    public static final int DOT_ARGB = 0xffe6edf3;

    /** ...at this opacity: 0.85. */
    public static final double DOT_ALPHA = 0.85;

    /** ...with this edge, ARGB: the map's dark (0b1118), so a dot reads on ice and on sea alike. */
    public static final int DOT_EDGE_ARGB = 0xff0b1118;

    /** A globe view's borders - the cull, the level, the projection and the cut - take no more than this, ms of the main thread's CPU (the wall clock where the JVM's CPU clock is coarse): 3 - measured 0.96 ms on the PC's slowest 1,389 x 868 view (the North Atlantic at 10 km a pixel), least of ten rounds, wall clock, 2026-10-28 (GlobeCheck 9); three times that, rounded up. */
    static final double BORDERS_MS = 3;

    /** A view's dots and labels - the projection, the tests and the placing - take no more than this, ms of the main thread's CPU (or the wall clock): 0.4 - measured 0.104 ms on the PC's slower view (the North Atlantic at 10 km a pixel), least of ten rounds, wall clock, 2026-10-28 (GlobeCheck 10); three times that, rounded up. */
    static final double MARKS_MS = 0.4;

    /** Building the map from the world file - the levels, the caps and the coarse grid, which inflates every blob tile once - takes no more than this, ms of the wall clock, on the worker thread: 600 - measured 147 to 189 ms on the PC, the first build of a cold JVM, 2026-10-28 (GlobeCheck 9); three times the slowest, rounded up. */
    static final double BUILD_MS = 600;

    /** The 15" cells along a coarse cell's side. */
    private static final int FINE_PER_COARSE = WorldData.OWNER_CELLS / COARSE_PER_DEGREE;

    /** The 15" cell, along a coarse cell's side, whose owner the coarse cell takes: the middle one (15 of 0 to 29). */
    private static final int CENTRE = FINE_PER_COARSE / 2;

    /** The sphere's radius, km. */
    private static final double R_KM = EarthProj.RADIUS_M / 1000;

    private static final MathContext THREE = new MathContext(3, RoundingMode.HALF_UP);
    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000), HUNDRED = BigDecimal.valueOf(100);

    /* =====================================================================
       WHAT THE MAP KEEPS
       ===================================================================== */

    private final WorldData file;
    private final int n;                                  // the nations, 1..n
    private final String[] iso3, names, region;           // by number: a nation's iso3 and region, and a nation's or an area's name
    private final double[] nodeLat, nodeLon, labelLat, labelLon;
    private final int lineCount;
    private final double[][] lat, lon;                    // [level][vertex]: the lines' vertices in degrees, a line's run from start[level][line]
    private final int[][] start;                          // [level][line + 1]
    private final double[] capX, capY, capZ, capR, capCos, capSin;   // a line's cap: unit centre, radius in radians, and the radius's cosine and sine
    private final byte[] coarse;                          // the coarse owner grid, rows from the north
    private final double[] areaKm2;                       // by owner: the coarse area, km2

    private final Cut cut = new Cut();
    private GlobeFrame cachedView;
    private Runs cachedRuns;
    private double[] crossKey;
    private int[] crossLines;
    private Table table;

    /** The map of a world file: the lines at their levels, the caps, the coarse owner grid and the areas. The caller keeps the file open. */
    public static NationsMap of(WorldData file) {
        return new NationsMap(file);
    }

    private NationsMap(WorldData file) {
        this.file = file;
        n = file.nationCount();
        int all = file.nations().size();
        iso3 = new String[n + 1];
        region = new String[n + 1];
        names = new String[all + 1];
        nodeLat = new double[n + 1];
        nodeLon = new double[n + 1];
        labelLat = new double[n + 1];
        labelLon = new double[n + 1];
        for (WorldData.Nation x : file.nations()) {
            int k = x.number();
            names[k] = x.name();
            if (k > n) continue;
            iso3[k] = x.iso3();
            region[k] = x.region();
            nodeLat[k] = x.nodeLat();
            nodeLon[k] = x.nodeLon();
            labelLat[k] = x.labelLat();
            labelLon[k] = x.labelLon();
        }
        List<WorldData.Border> lines = file.lines();
        lineCount = lines.size();
        int levels = LOD_KM.length;
        lat = new double[levels][];
        lon = new double[levels][];
        start = new int[levels][lineCount + 1];
        int total = 0, longest = 0;
        for (WorldData.Border b : lines) {
            total += b.lat().length;
            longest = Math.max(longest, b.lat().length);
        }
        lat[0] = new double[total];
        lon[0] = new double[total];
        double[] sig = new double[total];
        capX = new double[lineCount];
        capY = new double[lineCount];
        capZ = new double[lineCount];
        capR = new double[lineCount];
        capCos = new double[lineCount];
        capSin = new double[lineCount];
        double[] ux = new double[longest], uy = new double[longest], uz = new double[longest];
        int[] stackA = new int[longest + 1], stackB = new int[longest + 1];
        double[] stackE = new double[longest + 1];
        int at = 0;
        for (int i = 0; i < lineCount; i++) {
            WorldData.Border b = lines.get(i);
            int len = b.lat().length;
            start[0][i] = at;
            for (int k = 0; k < len; k++) {
                double la = b.latDeg(k), lo = b.lonDeg(k);
                lat[0][at + k] = la;
                lon[0][at + k] = lo;
                double p = StrictMath.toRadians(la), q = StrictMath.toRadians(lo), c = StrictMath.cos(p);
                ux[k] = R_KM * c * StrictMath.cos(q);
                uy[k] = R_KM * c * StrictMath.sin(q);
                uz[k] = R_KM * StrictMath.sin(p);
            }
            significance(ux, uy, uz, len, sig, at, stackA, stackB, stackE);
            cap(i, ux, uy, uz, len);
            at += len;
        }
        start[0][lineCount] = at;
        for (int l = 1; l < levels; l++) {
            int kept = 0;
            for (int k = 0; k < total; k++) if (sig[k] > LOD_KM[l]) kept++;
            lat[l] = new double[kept];
            lon[l] = new double[kept];
            int put = 0;
            for (int i = 0; i < lineCount; i++) {
                start[l][i] = put;
                for (int k = start[0][i]; k < start[0][i + 1]; k++) {
                    if (sig[k] > LOD_KM[l]) {
                        lat[l][put] = lat[0][k];
                        lon[l][put] = lon[0][k];
                        put++;
                    }
                }
            }
            start[l][lineCount] = put;
        }
        coarse = new byte[360 * COARSE_PER_DEGREE * 180 * COARSE_PER_DEGREE];
        areaKm2 = new double[WorldData.MAX_OWNERS + 1];
        fillCoarse();
    }

    /**
     * Each vertex's significance into sig[off ..]: the endpoints infinite; the others the km the vertex stands, in three dimensions, off the chord
     * of the range it is the farthest of, no more than its parent's. Ranges are split depth first, off an explicit stack.
     */
    private static void significance(double[] x, double[] y, double[] z, int len, double[] sig, int off, int[] stackA, int[] stackB, double[] stackE) {
        sig[off] = sig[off + len - 1] = Double.POSITIVE_INFINITY;
        for (int k = 1; k < len - 1; k++) sig[off + k] = 0;
        int top = 0;
        if (len > 2) {
            stackA[0] = 0;
            stackB[0] = len - 1;
            stackE[0] = Double.POSITIVE_INFINITY;
            top = 1;
        }
        while (top > 0) {
            top--;
            int a = stackA[top], b = stackB[top];
            double parent = stackE[top];
            if (b - a < 2) continue;
            double abx = x[b] - x[a], aby = y[b] - y[a], abz = z[b] - z[a], ab2 = abx * abx + aby * aby + abz * abz;
            int far = -1;
            double most = -1;
            for (int k = a + 1; k < b; k++) {
                double px = x[k] - x[a], py = y[k] - y[a], pz = z[k] - z[a];
                double t = ab2 > 0 ? Math.max(0, Math.min(1, (px * abx + py * aby + pz * abz) / ab2)) : 0;
                double dx = px - t * abx, dy = py - t * aby, dz = pz - t * abz;
                double d2 = dx * dx + dy * dy + dz * dz;
                if (d2 > most) {
                    most = d2;
                    far = k;
                }
            }
            double e = Math.min(Math.sqrt(most), parent);
            sig[off + far] = e;
            stackA[top] = a;
            stackB[top] = far;
            stackE[top++] = e;
            stackA[top] = far;
            stackB[top] = b;
            stackE[top++] = e;
        }
    }

    /** A line's cap: the unit vector of its vertices' mean (its first vertex's if they cancel), and the largest angle from it to a vertex. */
    private void cap(int line, double[] x, double[] y, double[] z, int len) {
        double sx = 0, sy = 0, sz = 0;
        for (int k = 0; k < len; k++) {
            sx += x[k];
            sy += y[k];
            sz += z[k];
        }
        double norm = Math.sqrt(sx * sx + sy * sy + sz * sz);
        if (norm < 1e-6 * len * R_KM) {
            sx = x[0];
            sy = y[0];
            sz = z[0];
            norm = Math.sqrt(sx * sx + sy * sy + sz * sz);
        }
        double cx = sx / norm, cy = sy / norm, cz = sz / norm, least = 1;
        for (int k = 0; k < len; k++) least = Math.min(least, (cx * x[k] + cy * y[k] + cz * z[k]) / R_KM);
        double r = Math.acos(Math.max(-1, Math.min(1, least)));
        capX[line] = cx;
        capY[line] = cy;
        capZ[line] = cz;
        capR[line] = r;
        capCos[line] = Math.cos(r);
        capSin[line] = Math.sin(r);
    }

    /** The coarse grid from the owner layer a tile at a time, and each owner's area from it. */
    private void fillCoarse() {
        int cols = 360 * COARSE_PER_DEGREE;
        for (int lat0 = -90; lat0 < 90; lat0++) {
            int row0 = (89 - lat0) * COARSE_PER_DEGREE;
            for (int lon0 = -180; lon0 < 180; lon0++) {
                int tile = WorldData.tile(lat0, lon0), col0 = (lon0 + 180) * COARSE_PER_DEGREE;
                int one = file.ownerUniform(tile);
                byte[] fine = one >= 0 ? null : file.ownerTile(tile);
                for (int i = 0; i < COARSE_PER_DEGREE; i++) {
                    for (int j = 0; j < COARSE_PER_DEGREE; j++) {
                        coarse[(row0 + i) * cols + col0 + j] = fine == null ? (byte) one
                                : fine[(i * FINE_PER_COARSE + CENTRE) * WorldData.OWNER_CELLS + j * FINE_PER_COARSE + CENTRE];
                    }
                }
            }
        }
        double cell = StrictMath.toRadians(1.0 / COARSE_PER_DEGREE);
        for (int row = 0; row < 180 * COARSE_PER_DEGREE; row++) {
            double north = StrictMath.toRadians(90 - row / (double) COARSE_PER_DEGREE), south = StrictMath.toRadians(90 - (row + 1) / (double) COARSE_PER_DEGREE);
            double area = R_KM * R_KM * cell * (StrictMath.sin(north) - StrictMath.sin(south));
            for (int col = 0; col < cols; col++) areaKm2[coarse[row * cols + col] & 0xFF] += area;
        }
    }

    /** How many nations the map holds (the file's N). */
    public int count() { return n; }

    /** How many border lines the file holds. */
    public int lineCount() { return lineCount; }

    /** A nation's ISO code, 1..count(). */
    public String iso3(int nation) { return iso3[nation]; }

    /** The number of the nation with that ISO code; 0 if there is none. */
    public int numberOf(String code) {
        for (int k = 1; k <= n; k++) if (iso3[k].equals(code)) return k;
        return 0;
    }

    /** The nation's or the area's name for an owner number; null for 0 (no one) and for a number the file does not have. */
    public String ownerName(int owner) {
        return owner < 1 || owner >= names.length ? null : names[owner];
    }

    /** A nation's largest place, where its dot stands. */
    public double nodeLat(int nation) { return nodeLat[nation]; }
    public double nodeLon(int nation) { return nodeLon[nation]; }

    /** Natural Earth's label point of a nation, where its name is written. */
    public double labelLat(int nation) { return labelLat[nation]; }
    public double labelLon(int nation) { return labelLon[nation]; }

    /** An owner's area on the coarse grid, km2. */
    public double areaKm2(int owner) { return areaKm2[owner]; }

    /** The owner of the coarse cell holding a point, 0 for no one: a byte read (the cell holds its north and west edges, as the owner layer's does). */
    int coarseOwner(double atLat, double atLon) {
        int rows = 180 * COARSE_PER_DEGREE, cols = 360 * COARSE_PER_DEGREE;
        int row = Math.max(0, Math.min(rows - 1, (int) Math.floor((90 - atLat) * COARSE_PER_DEGREE)));
        double wrapped = (atLon + 180) % 360;
        if (wrapped < 0) wrapped += 360;
        int col = (int) Math.floor(wrapped * COARSE_PER_DEGREE) % cols;
        return coarse[row * cols + col] & 0xFF;
    }

    /** The exact owner of a point (the owner layer's 15" cell): a nation's or an area's number, 0 for no one. */
    public int ownerAt(double atLat, double atLon) {
        return file.ownerAt(atLat, atLon);
    }

    /** The level a view at this km a pixel takes: the coarsest whose tolerance is no more than LOD_PX of a pixel. */
    static int levelFor(double kmPx) {
        int best = 0;
        for (int l = 1; l < LOD_KM.length; l++) if (LOD_KM[l] <= LOD_PX * kmPx) best = l;
        return best;
    }

    /** How many vertices a line keeps at a level. */
    int vertices(int line, int level) { return start[level][line + 1] - start[level][line]; }

    /** A line's vertex k at a level, degrees. */
    double lat(int line, int level, int k) { return lat[level][start[level][line] + k]; }
    double lon(int line, int level, int k) { return lon[level][start[level][line] + k]; }

    /** Whether borders and nations are drawn on a world: only the real Earth's (a generated world has no nations; its partners wait for WN3). */
    public static boolean drawsOn(World.Key key) {
        return key != null && key.earth();
    }

    /* =====================================================================
       THE BORDERS - cut to the view
       ===================================================================== */

    /** Runs of vertices in pixels: run k is xs, ys from starts[k] to starts[k + 1]; the counts a harness reads beside them. */
    public static final class Runs {
        public final double[] xs, ys;
        public final int[] starts;
        /** The lines in the file, the lines whose cap met the view, and the level of detail drawn. */
        public final int lines, considered, level;

        Runs(double[] xs, double[] ys, int[] starts, int lines, int considered, int level) {
            this.xs = xs;
            this.ys = ys;
            this.starts = starts;
            this.lines = lines;
            this.considered = considered;
            this.level = level;
        }

        public int runs() { return starts.length - 1; }

        public int vertices() { return xs.length; }
    }

    /**
     * The borders in a globe view (the PICTURE and GROUND tiers; in the PLANE the screen draws planeBorders(), and this gives nothing): the lines whose
     * cap meets the view's, at the level the view's km a pixel takes, each vertex through the ortho's forward() to pixels as GlobeFrame.at() does. A
     * vertex on the far side ends a run; the stretches more than DRAW_MARGIN_PX off the canvas are cut away, so every vertex lies within that of it. The
     * last view's runs are kept: the same view again gives the same object.
     */
    public Runs borders(GlobeFrame v) {
        if (v.tier() == GlobeFrame.PLANE) return new Runs(new double[0], new double[0], new int[] { 0 }, lineCount, 0, 0);
        if (cachedRuns != null && v.sameView(cachedView)) return cachedRuns;
        Runs r = bordersAt(v, levelFor(v.kmPx()));
        cachedView = v.copy(v.width(), v.height(), v.kmPx());
        cachedRuns = r;
        return r;
    }

    /** The same at a level of the caller's choice, and not kept (a harness prints what the file's own would draw). */
    Runs bordersAt(GlobeFrame v, int level) {
        EarthProj.Ortho o = v.ortho();
        double w = v.width(), h = v.height(), m = v.kmPx() * 1000;
        double viewR = viewRadius(v) + CAP_SLACK_RAD;
        double cosV = Math.cos(viewR), sinV = Math.sin(viewR);
        double p = StrictMath.toRadians(o.centreLat()), q = StrictMath.toRadians(o.centreLon());
        double vx = StrictMath.cos(p) * StrictMath.cos(q), vy = StrictMath.cos(p) * StrictMath.sin(q), vz = StrictMath.sin(p);
        cut.reset(w, h);
        double[] xy = new double[2];
        double[] la = lat[level], lo = lon[level];
        int[] from = start[level];
        int considered = 0;
        for (int i = 0; i < lineCount; i++) {
            double reach = viewR + capR[i] + CAP_SLACK_RAD;
            if (reach < Math.PI && vx * capX[i] + vy * capY[i] + vz * capZ[i] < cosV * capCos[i] - sinV * capSin[i]) continue;
            considered++;
            boolean have = false;
            double px = 0, py = 0;
            for (int k = from[i]; k < from[i + 1]; k++) {
                if (!o.forward(la[k], lo[k], xy)) {
                    have = false;
                    cut.gap();
                    continue;
                }
                double x = w / 2 + xy[0] / m, y = h / 2 - xy[1] / m;
                if (have) cut.segment(px, py, x, y);
                px = x;
                py = y;
                have = true;
            }
            cut.gap();
        }
        return cut.finish(lineCount, considered, level);
    }

    /** The angle from a view's centre to the farthest point it shows, radians: its corner's if all four are on the planet, a quarter turn once one is off it. */
    private static double viewRadius(GlobeFrame v) {
        double far = Math.hypot(v.width() / 2, v.height() / 2) * v.kmPx() * 1000;
        return far >= EarthProj.RADIUS_M ? Math.PI / 2 : Math.asin(far / EarthProj.RADIUS_M);
    }

    /* =====================================================================
       THE BORDERS ON THE CITY'S PLANE - only where one crosses the city's ground
       ===================================================================== */

    /**
     * The lines, by index, with a segment meeting a box of the plane's plots (the caller passes the box of the city's land and its offers): the
     * lines whose cap meets the box's, tested at the file's own level in plot coordinates. The last box's answer is kept against the box and the
     * plane's spot, which move only on a purchase.
     */
    public int[] crossing(EarthProj plane, double x0, double y0, double x1, double y1) {
        double[] key = { x0, y0, x1, y1, plane.centreLat(), plane.centreLon() };
        if (crossLines != null && Arrays.equals(key, crossKey)) return crossLines;
        double lox = Math.min(x0, x1), hix = Math.max(x0, x1), loy = Math.min(y0, y1), hiy = Math.max(y0, y1);
        double[] ll = new double[2];
        double[][] probes = { { (lox + hix) / 2, (loy + hiy) / 2 }, { lox, loy }, { hix, loy }, { lox, hiy }, { hix, hiy },
                { (lox + hix) / 2, loy }, { (lox + hix) / 2, hiy }, { lox, (loy + hiy) / 2 }, { hix, (loy + hiy) / 2 } };
        double cx = 0, cy = 0, cz = 0, boxR = 0;
        boolean capped = plane.latLonOfPlot(probes[0][0], probes[0][1], ll);
        if (capped) {
            double p = StrictMath.toRadians(ll[0]), q = StrictMath.toRadians(ll[1]);
            cx = StrictMath.cos(p) * StrictMath.cos(q);
            cy = StrictMath.cos(p) * StrictMath.sin(q);
            cz = StrictMath.sin(p);
            for (int k = 1; k < probes.length && capped; k++) {
                if (!plane.latLonOfPlot(probes[k][0], probes[k][1], ll)) {
                    capped = false;
                    break;
                }
                double pp = StrictMath.toRadians(ll[0]), qq = StrictMath.toRadians(ll[1]);
                double dot = cx * StrictMath.cos(pp) * StrictMath.cos(qq) + cy * StrictMath.cos(pp) * StrictMath.sin(qq) + cz * StrictMath.sin(pp);
                boxR = Math.max(boxR, Math.acos(Math.max(-1, Math.min(1, dot))));
            }
        }
        double cosB = Math.cos(boxR + CAP_SLACK_RAD), sinB = Math.sin(boxR + CAP_SLACK_RAD);
        int[] found = new int[16];
        int count = 0;
        double[] xy = new double[2];
        for (int i = 0; i < lineCount; i++) {
            if (capped && boxR + capR[i] + 2 * CAP_SLACK_RAD < Math.PI
                    && cx * capX[i] + cy * capY[i] + cz * capZ[i] < cosB * capCos[i] - sinB * capSin[i]) continue;
            boolean have = false, hit = false;
            double px = 0, py = 0;
            for (int k = start[0][i]; k < start[0][i + 1] && !hit; k++) {
                if (!plane.plotOf(lat[0][k], lon[0][k], xy)) {
                    have = false;
                    continue;
                }
                if (have && segmentMeets(px, py, xy[0], xy[1], lox, loy, hix, hiy)) hit = true;
                px = xy[0];
                py = xy[1];
                have = true;
            }
            if (hit) {
                if (count == found.length) found = Arrays.copyOf(found, count * 2);
                found[count++] = i;
            }
        }
        crossKey = key;
        crossLines = Arrays.copyOf(found, count);
        return crossLines;
    }

    /** Whether the segment (ax, ay)-(bx, by) meets the box (Liang-Barsky, boundary included). */
    static boolean segmentMeets(double ax, double ay, double bx, double by, double x0, double y0, double x1, double y1) {
        double dx = bx - ax, dy = by - ay, t0 = 0, t1 = 1;
        double[] p = { -dx, dx, -dy, dy }, q = { ax - x0, x1 - ax, ay - y0, y1 - ay };
        for (int k = 0; k < 4; k++) {
            if (p[k] == 0) {
                if (q[k] < 0) return false;
            } else {
                double t = q[k] / p[k];
                if (p[k] < 0) t0 = Math.max(t0, t);
                else t1 = Math.min(t1, t);
                if (t0 > t1) return false;
            }
        }
        return true;
    }

    /**
     * Those lines on the plane's screen: each vertex through plotOf() and the frame's screenX() and screenY(), at the level a pixel of
     * PLOT_M / 1000 / scale() km takes, cut to the canvas as the globe's are. Not kept: it is a handful of lines.
     */
    public Runs planeBorders(EarthProj plane, MapFrame f, int[] lines) {
        int level = levelFor(World.PLOT_M / 1000 / f.scale());
        cut.reset(f.width(), f.height());
        double[] xy = new double[2];
        double[] la = lat[level], lo = lon[level];
        for (int i : lines) {
            boolean have = false;
            double px = 0, py = 0;
            for (int k = start[level][i]; k < start[level][i + 1]; k++) {
                if (!plane.plotOf(la[k], lo[k], xy)) {
                    have = false;
                    cut.gap();
                    continue;
                }
                double x = f.screenX(xy[0]), y = f.screenY(xy[1]);
                if (have) cut.segment(px, py, x, y);
                px = x;
                py = y;
                have = true;
            }
            cut.gap();
        }
        return cut.finish(lineCount, lines.length, level);
    }

    /**
     * Collects runs of pixels, each segment clipped to the canvas widened by DRAW_MARGIN_PX (Liang-Barsky): a segment wholly outside is dropped and
     * the run it was in is cut there; a segment that leaves ends at the margin, one that enters starts a run at it. Reused from frame to frame.
     */
    private static final class Cut {
        private double[] xs = new double[4096], ys = new double[4096];
        private int[] starts = new int[512];
        private int points, runs;
        private double xmin, ymin, xmax, ymax;
        private boolean open;

        void reset(double w, double h) {
            xmin = -DRAW_MARGIN_PX;
            ymin = -DRAW_MARGIN_PX;
            xmax = w + DRAW_MARGIN_PX;
            ymax = h + DRAW_MARGIN_PX;
            points = 0;
            runs = 0;
            open = false;
        }

        /** The run in progress ends (a vertex was on the far side, or the line did). */
        void gap() { open = false; }

        void segment(double ax, double ay, double bx, double by) {
            double dx = bx - ax, dy = by - ay, t0 = 0, t1 = 1;
            for (int k = 0; k < 4; k++) {
                double p = k == 0 ? -dx : k == 1 ? dx : k == 2 ? -dy : dy;
                double q = k == 0 ? ax - xmin : k == 1 ? xmax - ax : k == 2 ? ay - ymin : ymax - ay;
                if (p == 0) {
                    if (q < 0) {
                        open = false;
                        return;
                    }
                } else {
                    double t = q / p;
                    if (p < 0) t0 = Math.max(t0, t);
                    else t1 = Math.min(t1, t);
                    if (t0 > t1) {
                        open = false;
                        return;
                    }
                }
            }
            if (!(open && t0 == 0)) {
                if (runs + 2 > starts.length) starts = Arrays.copyOf(starts, starts.length * 2);
                starts[runs++] = points;
                add(t0 == 0 ? ax : ax + t0 * dx, t0 == 0 ? ay : ay + t0 * dy);
            }
            add(t1 == 1 ? bx : ax + t1 * dx, t1 == 1 ? by : ay + t1 * dy);
            open = t1 == 1;
        }

        private void add(double x, double y) {
            if (points == xs.length) {
                xs = Arrays.copyOf(xs, points * 2);
                ys = Arrays.copyOf(ys, points * 2);
            }
            xs[points] = x;
            ys[points++] = y;
        }

        Runs finish(int lines, int considered, int level) {
            int[] s = Arrays.copyOf(starts, runs + 1);
            s[runs] = points;
            return new Runs(Arrays.copyOf(xs, points), Arrays.copyOf(ys, points), s, lines, considered, level);
        }
    }

    /* =====================================================================
       THE NATIONS' DOTS AND LABELS - on the globe
       ===================================================================== */

    /**
     * A nation's dot on the screen, centre (x, y) and radius r, and where its name is written: (lx, ly) the centre and baseline of the name, the
     * figures under it, drawn only if `labelled` (lx and ly are NaN if not). name and figures are the words, "Canada" and "US$2.24T . 41.5M".
     */
    public record Mark(int nation, double x, double y, double r, boolean labelled, double lx, double ly, String name, String figures) { }

    /** A dot's radius from a nation's output, real US$ of 2015: DOT_BASE_PX plus DOT_PER_ROOT_PX a root of a trillion, between DOT_MIN_PX and DOT_MAX_PX. */
    static double radius(double output) {
        double trillions = output > 0 ? output / 1e12 : 0;
        return Math.min(DOT_MAX_PX, Math.max(DOT_MIN_PX, DOT_BASE_PX + DOT_PER_ROOT_PX * Math.sqrt(trillions)));
    }

    /**
     * The dots and labels of a globe view (none in the PLANE tier), the nations largest output first (ties by number), for each nation whose node is
     * on the visible side and within the canvas and its radius. A label is written (`labelled`) only where the nation's ground in view holds it: its
     * label point on the visible side and its box inside the canvas; the nation's coarse area, in the view's pixels, at least LABEL_AREA_TIMES the
     * box; at least LABEL_OWN_SHARE of a LABEL_SAMPLES_X by LABEL_SAMPLES_Y grid of the box's points, through the ortho's inverse onto the coarse
     * grid, the nation's own; and the box overlapping no label placed before it. A nation that fails keeps its dot. The widths are the screen's
     * font measures of the name and of the figures.
     */
    public List<Mark> marks(GlobeFrame v, Nations nations, int year, ToDoubleFunction<String> nameW, ToDoubleFunction<String> figuresW) {
        if (v.tier() == GlobeFrame.PLANE) return List.of();
        Table t = table(nations, year);
        EarthProj.Ortho o = v.ortho();
        double w = v.width(), h = v.height(), m = v.kmPx() * 1000, pixelKm2 = v.kmPx() * v.kmPx();
        double[] xy = new double[2], ll = new double[2];
        List<Mark> out = new ArrayList<>();
        double[][] placed = new double[t.order.length][];
        int placedCount = 0;
        for (int nation : t.order) {
            if (nation > n || !o.forward(nodeLat[nation], nodeLon[nation], xy)) continue;
            double x = w / 2 + xy[0] / m, y = h / 2 - xy[1] / m, r = radius(t.output[nation - 1]);
            if (x < -r || x > w + r || y < -r || y > h + r) continue;
            String name = t.names[nation - 1], figures = t.figures[nation - 1];
            boolean labelled = false;
            double lx = Double.NaN, ly = Double.NaN;
            if (o.forward(labelLat[nation], labelLon[nation], xy)) {
                double width = Math.max(nameW.applyAsDouble(name), figuresW.applyAsDouble(figures));
                double bx = w / 2 + xy[0] / m, by = h / 2 - xy[1] / m;
                double[] box = { bx - width / 2, by - LABEL_ASCENT_PX, bx + width / 2, by - LABEL_ASCENT_PX + LABEL_H_PX };
                if (box[0] >= 0 && box[2] <= w && box[1] >= 0 && box[3] <= h
                        && areaKm2[nation] / pixelKm2 >= LABEL_AREA_TIMES * width * LABEL_H_PX
                        && ownShare(o, w, h, m, box, nation, xy, ll) >= LABEL_OWN_SHARE - 1e-9
                        && !overlaps(placed, placedCount, box)) {
                    labelled = true;
                    lx = bx;
                    ly = by;
                    placed[placedCount++] = box;
                }
            }
            out.add(new Mark(nation, x, y, r, labelled, lx, ly, name, figures));
        }
        return out;
    }

    /** The share of a box's LABEL_SAMPLES_X by LABEL_SAMPLES_Y points (the middles of equal cells) that the ortho's inverse puts on the nation's coarse cells. */
    private double ownShare(EarthProj.Ortho o, double w, double h, double m, double[] box, int nation, double[] xy, double[] ll) {
        int own = 0;
        for (int j = 0; j < LABEL_SAMPLES_Y; j++) {
            double sy = box[1] + (j + 0.5) / LABEL_SAMPLES_Y * (box[3] - box[1]);
            for (int i = 0; i < LABEL_SAMPLES_X; i++) {
                double sx = box[0] + (i + 0.5) / LABEL_SAMPLES_X * (box[2] - box[0]);
                if (o.inverse((sx - w / 2) * m, (h / 2 - sy) * m, ll) && coarseOwner(ll[0], ll[1]) == nation) own++;
            }
        }
        return own / (double) (LABEL_SAMPLES_X * LABEL_SAMPLES_Y);
    }

    private static boolean overlaps(double[][] placed, int count, double[] box) {
        for (int k = 0; k < count; k++) {
            double[] b = placed[k];
            if (box[0] < b[2] && b[0] < box[2] && box[1] < b[3] && b[1] < box[3]) return true;
        }
        return false;
    }

    /** A labelled mark's box on the screen, {x0, y0, x1, y1}: the wider of the two lines across, centred on the label point, LABEL_H_PX tall from LABEL_ASCENT_PX above the name's baseline. */
    public static double[] labelBox(Mark mark, ToDoubleFunction<String> nameW, ToDoubleFunction<String> figuresW) {
        double width = Math.max(nameW.applyAsDouble(mark.name()), figuresW.applyAsDouble(mark.figures()));
        return new double[] { mark.lx() - width / 2, mark.ly() - LABEL_ASCENT_PX, mark.lx() + width / 2, mark.ly() - LABEL_ASCENT_PX + LABEL_H_PX };
    }

    /**
     * The dot under a screen point: the mark whose radius and HIT_SLOP_PX it falls within (the nearest centre wins, then the smaller number); null
     * if none, or if there are no marks. The screen asks it alone to tell a dot from the land round it (the hover's words, the click's cursor).
     */
    public Mark dotAt(List<Mark> marks, double sx, double sy) {
        Mark best = null;
        double bestD = Double.MAX_VALUE;
        if (marks != null) {
            for (Mark k : marks) {
                double dx = sx - k.x(), dy = sy - k.y(), d2 = dx * dx + dy * dy, reach = k.r() + HIT_SLOP_PX;
                if (d2 <= reach * reach && (d2 < bestD || (d2 == bestD && k.nation() < best.nation()))) {
                    best = k;
                    bestD = d2;
                }
            }
        }
        return best;
    }

    /**
     * The nation under a screen point: the dot whose radius and HIT_SLOP_PX it falls within (the nearest centre wins, then the smaller number),
     * else the exact owner under the point (the ortho's inverse onto ownerAt()) when that is a nation, else 0.
     */
    public int nationAt(GlobeFrame v, List<Mark> marks, double sx, double sy) {
        Mark dot = dotAt(marks, sx, sy);
        if (dot != null) return dot.nation();
        double[] ll = v.latLonAt(sx, sy);
        if (ll == null) return 0;
        int owner = file.ownerAt(ll[0], ll[1]);
        return owner >= 1 && owner <= n ? owner : 0;
    }

    /* =====================================================================
       THE HOVER AND THE CARD
       ===================================================================== */

    /** The distance row's words: km from the city to the nation's node, along the great circle, whole km. */
    private String distanceWords(int nation, double cityLat, double cityLon) {
        long km = Math.round(EarthProj.distanceM(cityLat, cityLon, nodeLat[nation], nodeLon[nation]) / 1000);
        return String.format(Locale.US, "%,d km from the city", km);
    }

    /** What hovering a nation says: its name, "GDP US$2.24T . 41.5M people", "1,583 km from the city", and CLICK_FOR_CARD. */
    public List<String> hoverWords(Nations nations, int nation, int year, double cityLat, double cityLon) {
        Nations.Year y = nations.year(nation, year);
        return List.of(nations.name(nation), "GDP " + usd(y.output()) + " · " + people(y.people()) + " people", distanceWords(nation, cityLat, cityLon), CLICK_FOR_CARD);
    }

    /** One line of the card: its label, its value and the rank beside it ("9th of 196"; empty where it has none). */
    public record Row(String label, String value, String rank) { }

    /** A nation's card: its name, its World Bank region (empty for none), its distance from the city and the rows. */
    public record Card(String name, String region, String distance, List<Row> rows) { }

    /** The GDP row's label: the file's output is real, US$ of 2015. */
    public static final String GDP_WORDS = "GDP, US$ of 2015";

    /**
     * A nation's card for a year: its output, people and output a head, each with its rank among the nations; this year's growth and prices; its money
     * and rate, and, when dollarsAUsd (the game dollar's price in US$, D$ a US$) is finite and positive, the money in the game's dollars. A figure the
     * file marks a projection carries " ◐" and the game's estimate " ○"; a head takes the lower of its two figures' marks.
     */
    public Card card(Nations nations, int nation, int year, double cityLat, double cityLon, double dollarsAUsd) {
        Table t = table(nations, year);
        Nations.Year y = nations.year(nation, year);
        int i = nation - 1;
        String of = " of " + nations.count();
        List<Row> rows = new ArrayList<>();
        rows.add(new Row(GDP_WORDS, usd(y.output()) + markWords(y.outputMark()), ordinal(t.rankOutput[i]) + of));
        rows.add(new Row("People", people(y.people()) + markWords(y.peopleMark()), ordinal(t.rankPeople[i]) + of));
        rows.add(new Row("GDP a head", "US$" + sig3(y.outputPerHead()) + markWords(Math.min(y.outputMark(), y.peopleMark())), ordinal(t.rankHead[i]) + of));
        double growth = Math.expm1(nations.growth(nation, year)), prices = y.inflation();
        rows.add(new Row("This year", Double.isNaN(growth) && Double.isNaN(prices) ? "-" : percent(growth, true) + " real · " + percent(prices, false) + " prices", ""));
        rows.add(new Row("Its money", moneyWords(y, dollarsAUsd), ""));
        return new Card(nations.name(nation), region[nation] == null ? "" : region[nation], distanceWords(nation, cityLat, cityLon), List.copyOf(rows));
    }

    /** A share as a percent to one decimal ("-" for NaN), with a sign if asked: "+1.6%". A rounded zero has no minus. */
    private static String percent(double share, boolean signed) {
        if (Double.isNaN(share)) return "-";
        double pct = Math.round(share * 1000) / 10.0;
        return String.format(Locale.US, signed ? "%+.1f%%" : "%.1f%%", pct == 0 ? 0.0 : pct);
    }

    /** "CAD 1.37 a US$ . CAD 1 = D$0.86", the second part only if the game dollar's price is known; for the dollar itself "the US dollar . US$1 = D$1.16". */
    private static String moneyWords(Nations.Year y, double dollarsAUsd) {
        boolean known = dollarsAUsd > 0 && !Double.isInfinite(dollarsAUsd);
        if (Nations.DOLLAR.equals(y.money())) return "the US dollar" + (known ? " · US$1 = D$" + sig3(dollarsAUsd) : "");
        if (!(y.rateToUsd() > 0) || Double.isInfinite(y.rateToUsd())) return y.money() == null ? "-" : y.money();
        return y.money() + " " + sig3(y.rateToUsd()) + " a US$" + (known ? " · " + y.money() + " 1 = D$" + sig3(dollarsAUsd / y.rateToUsd()) : "");
    }

    private static String markWords(int mark) {
        return mark == WorldData.MARK_PROJECTED ? " ◐" : mark == WorldData.MARK_ESTIMATE ? " ○" : "";
    }

    /** 1st, 2nd, 3rd, 4th ... 11th, 12th, 13th ... 21st, 101st, 111th. */
    public static String ordinal(int k) {
        int hundreds = k % 100, units = k % 10;
        return k + (hundreds >= 11 && hundreds <= 13 ? "th" : units == 1 ? "st" : units == 2 ? "nd" : units == 3 ? "rd" : "th");
    }

    /* =====================================================================
       THE WORDS
       ===================================================================== */

    /**
     * US$ as words: "US$2.24T" from a trillion (two decimals), "US$580B", "US$58.1B" and "US$5.81B" from a billion (three significant figures, a
     * trailing ".0" dropped), the same in M from a million, and below that "US$" and a whole number with its thousands separated. "-" for NaN.
     */
    public static String usd(double v) {
        if (Double.isNaN(v)) return "-";
        if (v >= 1e12) return "US$" + String.format(Locale.US, "%.2f", v / 1e12) + "T";
        String s;
        if (v >= 1e9) return (s = three(v / 1e9)) != null ? "US$" + s + "B" : "US$1.00T";
        if (v >= 1e6) return (s = three(v / 1e6)) != null ? "US$" + s + "M" : "US$1.00B";
        long whole = Math.round(v);
        return whole >= 1_000_000 ? "US$1.00M" : "US$" + String.format(Locale.US, "%,d", whole);
    }

    /** People as words: "1.43B", "41.5M", "390k", "57k" (three significant figures, a trailing ".0" dropped), below a thousand the whole number. "-" for NaN. */
    public static String people(double v) {
        if (Double.isNaN(v)) return "-";
        String s;
        if (v >= 1e9) return (s = three(v / 1e9)) != null ? s + "B" : String.format(Locale.US, "%,d", Math.round(v / 1e9)) + "B";
        if (v >= 1e6) return (s = three(v / 1e6)) != null ? s + "M" : "1.00B";
        if (v >= 1e3) return (s = three(v / 1e3)) != null ? s + "k" : "1.00M";
        long whole = Math.round(v);
        return whole >= 1000 ? "1.00k" : String.valueOf(whole);
    }

    /** A number of [1, 1000) to three significant figures with a trailing ".0" dropped; null if it rounds up to 1000. */
    private static String three(double x) {
        BigDecimal r = new BigDecimal(x).round(THREE);
        if (r.compareTo(THOUSAND) >= 0) return null;
        String s = r.setScale(r.compareTo(BigDecimal.TEN) < 0 ? 2 : r.compareTo(HUNDRED) < 0 ? 1 : 0, RoundingMode.HALF_UP).toPlainString();
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }

    /** A positive figure to three significant figures: thousands separated from 1,000 ("54,300"), a trailing zero kept from 1 up ("1.30") and dropped under it ("0.86"). "-" for the rest. */
    static String sig3(double v) {
        if (!(v > 0) || Double.isInfinite(v)) return "-";
        BigDecimal r = new BigDecimal(v).round(THREE);
        if (r.compareTo(THOUSAND) >= 0) return String.format(Locale.US, "%,d", r.toBigInteger());
        if (r.compareTo(BigDecimal.ONE) < 0) return r.stripTrailingZeros().toPlainString();
        return r.setScale(r.compareTo(BigDecimal.TEN) < 0 ? 2 : r.compareTo(HUNDRED) < 0 ? 1 : 0, RoundingMode.HALF_UP).toPlainString();
    }

    /* =====================================================================
       A YEAR'S FIGURES AND RANKS - computed once for a Nations and a year
       ===================================================================== */

    /** The figures of a year for all the nations, and each one's rank: the count of nations strictly larger, plus one (ties share it). */
    private static final class Table {
        final Nations nations;
        final int year;
        final double[] output, people, head;
        final int[] rankOutput, rankPeople, rankHead;
        final int[] order;                                // the nations' numbers, largest output first, ties by number
        final String[] names, figures;

        Table(Nations nations, int year) {
            this.nations = nations;
            this.year = year;
            int count = nations.count();
            output = new double[count];
            people = new double[count];
            head = new double[count];
            names = new String[count];
            figures = new String[count];
            for (int i = 0; i < count; i++) {
                Nations.Year y = nations.year(i + 1, year);
                output[i] = y.output();
                people[i] = y.people();
                head[i] = y.outputPerHead();
                names[i] = nations.name(i + 1);
                figures[i] = usd(y.output()) + " · " + people(y.people());
            }
            rankOutput = ranks(output);
            rankPeople = ranks(people);
            rankHead = ranks(head);
            Integer[] by = new Integer[count];
            for (int i = 0; i < count; i++) by[i] = i + 1;
            Arrays.sort(by, (a, b) -> {
                int c = Double.compare(key(output[b - 1]), key(output[a - 1]));
                return c != 0 ? c : Integer.compare(a, b);
            });
            order = new int[count];
            for (int i = 0; i < count; i++) order[i] = by[i];
        }

        /** A figure as it ranks: NaN last. */
        private static double key(double v) { return Double.isNaN(v) ? Double.NEGATIVE_INFINITY : v; }

        private static int[] ranks(double[] v) {
            Integer[] by = new Integer[v.length];
            for (int i = 0; i < v.length; i++) by[i] = i;
            Arrays.sort(by, (a, b) -> Double.compare(key(v[b]), key(v[a])));
            int[] rank = new int[v.length];
            for (int k = 0; k < by.length; k++) rank[by[k]] = k > 0 && key(v[by[k]]) == key(v[by[k - 1]]) ? rank[by[k - 1]] : k + 1;
            return rank;
        }
    }

    private Table table(Nations nations, int year) {
        if (table == null || table.nations != nations || table.year != year) table = new Table(nations, year);
        return table;
    }
}
