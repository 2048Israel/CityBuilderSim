package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The city's hydro sites: where a river and a drop lie on the ground it owns - the places a Hydro Station can be ordered
 * (HY1, 2026-10-26; docs/queue/HY1-notes.md 1.1).
 *
 * WHY THIS EXISTS. Canada's power was mostly hydro from 1900, and the game had only coal and wind: nothing in it could
 * tell a river from a lake, or read a drop. The Earth has the rivers (GRWL's centrelines, each vertex with its width in
 * metres: EarthData.rivers()) and the slope of the land (ET1: each plot's relief class), but no heights on land
 * (spec-earth E8 ruled them out), so a river's own fall, which is what a real head is, is not in the data. Its banks
 * are. At every plot along a river the rise of the ground on each side is added up out to REACH plots from the water,
 * each plot at its class's least grade, and the head is the lower bank's - a dam backs the river up no higher than its
 * lower abutment. The flow comes from the width by hydraulic geometry. A generated world, and an earth-1 file, has no
 * relief: no drop anywhere, no site, and the card says why.
 *
 * ONE SITE A CELL, THE BEST POINT. The world is cut into CELL squares (128 plots, 3.84 km); a cell's site is its best
 * point on any river - the most mean power, a tie to the lower y and then the lower x - and it is the city's when the
 * plot at that point is the city's, as a field goes whole with its centre. A site is defined on the world, not on the
 * ground owned (a cell's best reads every river sample in the cell, owned or not), so buying more of a river never
 * merges two sites into one and never lowers what the city had. A site holds as many stations as its flow allows
 * (slots()), and counts only on a landmass the city has linked (refresh(): ET5's rule for works).
 *
 * DERIVED, NEVER SAVED. As Landmasses' books are: a pure function of the saved rectangles and the frozen Earth data,
 * worked out again at every land change and at load (LandManager.landChanged()); no save field, no format bump.
 * recount() reads nothing on a world with no relief, and on Earth only the 1-degree tiles round the cells the city owns
 * ground in. The ground's reads are World.reliefAt()'s, kept a tile at a time.
 *
 * THE CORE, scan(), takes a relief and the river's segments and nothing of the Earth, so HydroCheck can lay a valley on
 * synthetic ground and prove each rule; the Earth and the city are only where the segments and the wanted cells come from.
 *
 * @author Jerus
 */
public final class HydroSites {

    /** How far each bank is read from the water, in plots: 10 (300 m), a valley's side as a dam's abutment reaches it. */
    public static final int REACH = 10;

    /** The world's site cells, in plots a side: 128 (3.84 km, four tiles), aligned to the world's plot grid, where a river holds one site. */
    public static final long CELL = 128;

    /** The least head a site needs, metres: 10 - under it there is no drop, and the sample is no candidate. */
    public static final double HEAD_MIN_M = 10;

    /** The least grade (rise over run) of each relief class, indexed FLAT, FOOTHILL, STEEP, VERY_STEEP: 0, 0.10, 0.30 and 0.60, the edges World's classes are cut at (ET1; LandMap.GRADE_WORDS says them), so a bank reads each plot at the least it can be. */
    static final double[] LEAST_GRADE = { 0, 0.10, 0.30, 0.60 };

    /** The widest river read as flowing water, metres: 550, the width HG_COEFF gives Niagara's mean flow (about 5,800 m3/s) - GRWL keeps reservoirs and lake-like reaches a kilometre and more wide (Shawinigan's best, 1,088 m), which at their own width read as many times their river's flow. */
    public static final double WIDTH_CAP_M = 550;

    /** Hydraulic geometry's coefficient, w = 7.2 Q^0.5 (a width in metres from the mean flow in cubic metres a second): Moody and Troutman 2002, quoted from memory, not re-read. */
    public static final double HG_COEFF = 7.2;

    /** Water to wire, the share of the water's power that reaches the grid: 0.85. */
    public static final double EFFICIENCY = 0.85;

    /** Gravity, metres a second squared. */
    public static final double G = 9.81;

    /** The hours of a month the game's power figures are made at: 730 (the Wind Farm's arithmetic: kW x 730 h, over the unit). */
    public static final double HOURS_A_MONTH = 730;

    /** The kilowatt-hours one unit of electricity is a month: 720 (derived from the Steel Foundry's documented 450 kWh a tonne; the Wind Farm's comment in BuildingManager). */
    public static final double KWH_PER_UNIT = 720;

    /** A sample's reading is widened by this many degrees when the 1-degree tiles round a cell are found: 0.02 (2.2 km), past the curve of a cell's edge between the points it is found from. */
    static final double TILE_PAD_DEG = 0.02;

    /** A segment whose box spans more than this many cells is kept without asking each: 16. */
    static final int SEGMENT_CELLS_ASKED = 16;

    /* =====================================================================
       THE PHYSICS - the flow, the power and a bank's rise
       ===================================================================== */

    /** The mean flow of a river of this width, cubic metres a second: (min(width, WIDTH_CAP_M) / HG_COEFF) squared. */
    public static double dischargeM3s(double widthM) {
        double w = Math.min(Math.max(0, widthM), WIDTH_CAP_M) / HG_COEFF;
        return w * w;
    }

    /** The mean power of a river of this width falling this far, kW: EFFICIENCY x G x Q x H (water's thousand kilograms a cubic metre makes the kilowatts). */
    public static double meanKw(double widthM, double headM) {
        return EFFICIENCY * G * dischargeM3s(widthM) * Math.max(0, headM);
    }

    /** The least grade of a relief class (FLAT 0 to VERY_STEEP 0.60); a byte that is no class reads FLAT. */
    public static double leastGrade(byte relief) {
        return relief >= 0 && relief < LEAST_GRADE.length ? LEAST_GRADE[relief] : 0;
    }

    /** The kilowatt-hours a month's worth of a mean power makes, in the game's electricity units: kW x HOURS_A_MONTH / KWH_PER_UNIT. */
    public static double unitsOfKw(double kw) {
        return kw * HOURS_A_MONTH / KWH_PER_UNIT;
    }

    /** One plot's relief, a World.FLAT to World.VERY_STEEP: World.reliefAt() for the city, a lambda for a fixture. A plot that is not dry reads FLAT. */
    public interface Relief {
        byte at(long x, long y);
    }

    /** The cells a read is asked for, by their column and row (a plot's is Math.floorDiv(plot, CELL)). */
    public interface Cells {
        boolean has(long cx, long cy);
    }

    /**
     * The rise of one bank, metres: for k = 1 to REACH the plot at the point p, out along the unit direction (nx, ny)
     * by halfWidthPlots + k - 0.5 plots (the centre of the kth plot past the water's edge), floored to plot coordinates,
     * adds World.PLOT_M x its class's least grade.
     */
    public static double bankRiseM(Relief relief, double px, double py, double nx, double ny, double halfWidthPlots) {
        double rise = 0;
        for (int k = 1; k <= REACH; k++) {
            double d = halfWidthPlots + k - 0.5;
            long x = (long) Math.floor(px + nx * d), y = (long) Math.floor(py + ny * d);
            rise += World.PLOT_M * leastGrade(relief.at(x, y));
        }
        return rise;
    }

    /** The head at a point, metres: the lower bank's rise, and nothing under HEAD_MIN_M. */
    public static double headM(double leftRiseM, double rightRiseM) {
        double h = Math.min(leftRiseM, rightRiseM);
        return h >= HEAD_MIN_M ? h : 0;
    }

    /* =====================================================================
       THE CORE - a river's segments and a relief, one site a cell
       ===================================================================== */

    /** A river segment in plots, its ends and the river's true width at each, metres (EarthData.Run.width(), not EarthTerrain's half-widths, clamped at 0.75 plots). */
    public record Segment(double ax, double ay, double bx, double by, double widthA, double widthB) { }

    /** A site: the dam's plot, the river's width and the head there, the mean power, and the landmass of its bank (0 where the world has none: it reads as home). */
    public record Site(long x, long y, double widthM, double headM, double meanKw, int landmass) {

        /** What it makes a month, in the game's electricity units. */
        public double units() { return unitsOfKw(meanKw); }

        /** How many stations of this many units each it holds: floor(its units over theirs); 0 for a station of nothing. */
        public int slots(double stationUnits) {
            return stationUnits > 0 ? (int) Math.floor(units() / stationUnits) : 0;
        }
    }

    /** Whether one sample is better than another: the most mean power, then the lower y, then the lower x, then the greater head and width - a total order, so no tie depends on the order the segments are listed in. */
    private static final Comparator<Site> BEST = (a, b) -> {
        int c = Double.compare(b.meanKw(), a.meanKw());
        if (c == 0) c = Long.compare(a.y(), b.y());
        if (c == 0) c = Long.compare(a.x(), b.x());
        if (c == 0) c = Double.compare(b.headM(), a.headM());
        if (c == 0) c = Double.compare(b.widthM(), a.widthM());
        return c;
    };

    private static long key(long cx, long cy) {
        return (cx << 32) ^ (cy & 0xffffffffL);
    }

    /**
     * Each wanted cell's site, by the cells' rows and then columns: its best sample over every river segment given. Each
     * segment is walked a plot at a time (the width between its ends interpolated), and at each sample both banks are read
     * (bankRiseM(), out along the segment's unit normal) and the head is the lower's; a sample with no drop (under
     * HEAD_MIN_M) is no candidate, so a cell whose best point has none has no site. The landmass is left 0.
     * The segments are the river's whole, owned or not: a cell's best reads every sample in it.
     */
    public static List<Site> scan(Relief relief, List<Segment> segments, Cells wanted) {
        Map<Long, Site> best = new HashMap<>();
        for (Segment s : segments) {
            double dx = s.bx() - s.ax(), dy = s.by() - s.ay();
            double len = Math.hypot(dx, dy);
            if (!(len > 1e-9)) continue;
            double nx = -dy / len, ny = dx / len;
            int n = Math.max(1, (int) Math.ceil(len));
            for (int i = 0; i < n; i++) {
                double t = (i + 0.5) / n;
                double px = s.ax() + dx * t, py = s.ay() + dy * t;
                long x = (long) Math.floor(px), y = (long) Math.floor(py);
                long cx = Math.floorDiv(x, CELL), cy = Math.floorDiv(y, CELL);
                if (!wanted.has(cx, cy)) continue;
                double width = s.widthA() + (s.widthB() - s.widthA()) * t;
                double half = width / 2 / World.PLOT_M;
                double head = headM(bankRiseM(relief, px, py, nx, ny, half), bankRiseM(relief, px, py, -nx, -ny, half));
                if (!(head > 0)) continue;
                double kw = meanKw(width, head);
                if (!(kw > 0)) continue;
                Site s0 = new Site(x, y, width, head, kw, 0);
                best.merge(key(cx, cy), s0, (a, b) -> BEST.compare(a, b) <= 0 ? a : b);
            }
        }
        List<Map.Entry<Long, Site>> rows = new ArrayList<>(best.entrySet());
        rows.sort(Comparator.<Map.Entry<Long, Site>>comparingLong(e -> Math.floorDiv(e.getValue().y(), CELL))
                .thenComparingLong(e -> Math.floorDiv(e.getValue().x(), CELL)));
        List<Site> out = new ArrayList<>(rows.size());
        for (Map.Entry<Long, Site> e : rows) out.add(e.getValue());
        return out;
    }

    /* =====================================================================
       THE CITY'S SITES - recount() and refresh()
       ===================================================================== */

    /** Whether the city owns a plot: CityLand.ownsPlot() for the city, a lambda for a fixture. */
    public interface Owns {
        boolean at(long x, long y);
    }

    /** The sites whose dam's plot is owned: a site is the city's when the plot at its best point is, as a field goes whole with its centre. */
    public static List<Site> keepOwned(List<Site> sites, Owns owns) {
        List<Site> mine = new ArrayList<>();
        for (Site s : sites) if (owns.at(s.x(), s.y())) mine.add(s);
        return mine;
    }

    /** The sites on ground the city owns (their dams' plots are its), on every landmass. */
    private List<Site> owned = List.of();

    /** ...and those on a landmass the city has linked: the ones a station can be ordered against. */
    private List<Site> linked = List.of();

    /** The Earth's rivers last read, kept while the tiles they are read from stay the same: the key (world, tiles) and the segments through the projection. */
    private String readKey;
    private List<Segment> readSegments = List.of();

    /** How many river segments the last read looked at (those that meet a wanted cell), 0 where nothing was read: HydroCheck's "a generated world reads nothing at all". */
    private int segmentsRead;

    /** A bare one: no sites. */
    public HydroSites() { }

    /**
     * Works the city's sites out again (derived, never read by a getter): empty when the world is not Earth, the Earth data
     * has no relief (a generated world, an earth-1 file) or the land owns no plot. Otherwise the rivers of the 1-degree
     * tiles round each cell the city owns ground in are read once, the core run over those cells, and the sites kept whose
     * dam plot the land owns, each given the landmass of the dry ground beside its dam. Call refresh() after.
     */
    void recount(World world, CityLand land) {
        owned = List.of();
        linked = List.of();
        segmentsRead = 0;
        if (world == null || land == null || !world.isEarth() || !world.earthTerrain().data().hasRelief()) return;
        LandGrid grid = land.grid();
        if (grid.ownedPlots() <= 0) return;
        Set<Long> cells = new HashSet<>();
        for (long cy = Math.floorDiv(grid.minY(), CELL); cy <= Math.floorDiv(grid.maxY() - 1, CELL); cy++) {
            for (long cx = Math.floorDiv(grid.minX(), CELL); cx <= Math.floorDiv(grid.maxX() - 1, CELL); cx++) {
                if (grid.owned(cx * CELL, cy * CELL, (cx + 1) * CELL, (cy + 1) * CELL) > 0) cells.add(key(cx, cy));
            }
        }
        owned = List.copyOf(keepOwned(read(world, cells), land::ownsPlot));
        linked = owned;
    }

    /**
     * The sites a harness reads off the world itself, with no city (so every cell in the box is wanted and every site is
     * kept): the sites of the cells that meet the box of plots [x0, x1) x [y0, y1), each with its landmass. Empty on a world
     * with no relief. A fresh reader's, nothing of the city's is touched.
     */
    public static List<Site> sitesOnWorld(World world, long x0, long y0, long x1, long y1) {
        if (world == null || !world.isEarth() || !world.earthTerrain().data().hasRelief()) return List.of();
        Set<Long> cells = new HashSet<>();
        for (long cy = Math.floorDiv(y0, CELL); cy <= Math.floorDiv(y1 - 1, CELL); cy++) {
            for (long cx = Math.floorDiv(x0, CELL); cx <= Math.floorDiv(x1 - 1, CELL); cx++) cells.add(key(cx, cy));
        }
        return new HydroSites().read(world, cells);
    }

    /** The sites of these cells, from the Earth's rivers and relief, each given its landmass. */
    private List<Site> read(World world, Set<Long> cells) {
        EarthTerrain earth = world.earthTerrain();
        Cells wanted = (cx, cy) -> cells.contains(key(cx, cy));
        List<Segment> segments = rivers(world, earth, cells);
        segmentsRead = segments.size();
        List<Site> found = scan(world::reliefAt, segments, wanted);
        List<Site> out = new ArrayList<>(found.size());
        for (Site s : found) out.add(new Site(s.x(), s.y(), s.widthM(), s.headM(), s.meanKw(), landmassBeside(world, s)));
        return out;
    }

    /** The landmass of the first dry plot beside a dam, nearest first (rings of plots, the lower y then x on a ring): 0 where there is none, which reads as home. */
    static int landmassBeside(World world, Site s) {
        int reach = REACH + (int) Math.ceil(s.widthM() / 2 / World.PLOT_M) + 1;
        for (int r = 0; r <= reach; r++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dx = -r; dx <= r; dx++) {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) != r) continue;
                    int id = world.landmassAt(s.x() + dx, s.y() + dy);
                    if (id != 0) return id;
                }
            }
        }
        return 0;
    }

    /**
     * The rivers near these cells as segments in plots with their true widths: the 1-degree tiles that cover each cell
     * (widened by REACH and TILE_PAD_DEG), read with EarthData.riversIn() (each segment once), each vertex through the
     * projection - a segment whose end does not project is left out - and kept when its box meets a wanted cell.
     * The last read is kept while the tiles are the same, so a purchase that moves no tile reads no file.
     */
    private List<Segment> rivers(World world, EarthTerrain earth, Set<Long> cells) {
        EarthProj proj = earth.projection();
        EarthData data = earth.data();
        TreeSet<Integer> tiles = new TreeSet<>();
        for (long k : cells) tilesOfCell(proj, k >> 32, (int) k, tiles);
        if (tiles.isEmpty()) return List.of();
        String id = world.key() + "|" + tiles;
        List<Segment> all;
        if (id.equals(readKey)) {
            all = readSegments;
        } else {
            int[] t = new int[tiles.size()];
            int n = 0;
            for (int tile : tiles) t[n++] = tile;
            all = project(proj, data.riversIn(t));
            readKey = id;
            readSegments = all;
        }
        List<Segment> out = new ArrayList<>();
        for (Segment s : all) if (touches(s, cells)) out.add(s);
        return out;
    }

    /** The river runs' consecutive vertices as segments in plots, widths in metres as stored; a segment with an end that does not project is left out. */
    private static List<Segment> project(EarthProj proj, List<EarthData.Run> runs) {
        List<Segment> out = new ArrayList<>();
        double[] a = new double[2], b = new double[2];
        for (EarthData.Run run : runs) {
            boolean have = run.size() > 0 && proj.plotOf(run.lat(0), run.lon(0), a);
            for (int k = 0; k + 1 < run.size(); k++) {
                boolean next = proj.plotOf(run.lat(k + 1), run.lon(k + 1), b);
                if (have && next) out.add(new Segment(a[0], a[1], b[0], b[1], run.width(k), run.width(k + 1)));
                a[0] = b[0];
                a[1] = b[1];
                have = next;
            }
        }
        return out;
    }

    /** Whether a segment's box (a plot widened) meets a wanted cell: asked a cell at a time for a short one, assumed for a long one. */
    private static boolean touches(Segment s, Set<Long> cells) {
        long x0 = Math.floorDiv((long) Math.floor(Math.min(s.ax(), s.bx())) - 1, CELL), x1 = Math.floorDiv((long) Math.floor(Math.max(s.ax(), s.bx())) + 1, CELL);
        long y0 = Math.floorDiv((long) Math.floor(Math.min(s.ay(), s.by())) - 1, CELL), y1 = Math.floorDiv((long) Math.floor(Math.max(s.ay(), s.by())) + 1, CELL);
        if ((x1 - x0 + 1) * (y1 - y0 + 1) > SEGMENT_CELLS_ASKED) return true;
        for (long cy = y0; cy <= y1; cy++) for (long cx = x0; cx <= x1; cx++) if (cells.contains(key(cx, cy))) return true;
        return false;
    }

    /**
     * Adds the 1-degree tiles that cover a cell widened by REACH plots to `out`: its corners and edge midpoints through
     * the projection, the longitudes unwrapped about the first (as EarthTerrain.districtWater() finds its tiles), the box
     * padded by TILE_PAD_DEG. Nothing is added when a point does not project.
     */
    private static void tilesOfCell(EarthProj proj, long cx, long cy, Set<Integer> out) {
        double x0 = cx * CELL - REACH, y0 = cy * CELL - REACH, d = CELL + 2.0 * REACH;
        double minLat = 90, maxLat = -90, minLon = 0, maxLon = 0, ref = Double.NaN;
        double[] ll = new double[2];
        for (int j = 0; j <= 2; j++) {
            for (int i = 0; i <= 2; i++) {
                if (!proj.latLonOfPlot(x0 + d * i / 2.0, y0 + d * j / 2.0, ll)) return;
                double lon = ll[1];
                if (Double.isNaN(ref)) { ref = lon; minLon = lon; maxLon = lon; }
                if (lon - ref > 180) lon -= 360;
                else if (lon - ref < -180) lon += 360;
                minLat = Math.min(minLat, ll[0]);
                maxLat = Math.max(maxLat, ll[0]);
                minLon = Math.min(minLon, lon);
                maxLon = Math.max(maxLon, lon);
            }
        }
        int r0 = (int) Math.max(-90, Math.floor(minLat - TILE_PAD_DEG)), r1 = (int) Math.min(89, Math.floor(maxLat + TILE_PAD_DEG));
        long c0 = (long) Math.floor(minLon - TILE_PAD_DEG), c1 = (long) Math.floor(maxLon + TILE_PAD_DEG);
        for (int lat = r0; lat <= r1; lat++) {
            for (long lon = c0; lon <= c1; lon++) out.add(EarthData.tile(lat, (int) (Math.floorMod(lon + 180, 360L) - 180)));
        }
    }

    /** Works the linked sites out again: the owned ones whose landmass is linked (Landmasses.linked(): home, 0 and what a port or a join reaches). Eager, after recount() and wherever the linked set moves. */
    void refresh(Landmasses lms) {
        if (owned.isEmpty()) {
            linked = owned;
            return;
        }
        List<Site> now = new ArrayList<>();
        for (Site s : owned) if (lms == null || lms.linked(s.landmass())) now.add(s);
        linked = List.copyOf(now);
    }

    /** Forgets everything, the rivers kept too (LandManager's reset: a new city, a load): no sites until recount(). */
    void reset() {
        owned = List.of();
        linked = List.of();
        readKey = null;
        readSegments = List.of();
        segmentsRead = 0;
    }

    /** Harnesses only: puts these sites in as the city's, owned and linked by fiat (as CityLand.setCentreRelief sets a fixture's hills). A land change works them out again. */
    void setForTest(List<Site> sites) {
        owned = List.copyOf(sites);
        linked = owned;
    }

    /* =====================================================================
       READS - plain getters; none fills anything
       ===================================================================== */

    /** The sites the city owns the dams of, on every landmass, by their cells' rows and columns. */
    public List<Site> sites() { return owned; }

    /** The sites on a linked landmass: where a station can be ordered. */
    public List<Site> linked() { return linked; }

    /** How many stations of this many units each the linked sites hold in all: each site's floor(units / stationUnits), summed. */
    public int slots(double stationUnits) {
        int slots = 0;
        for (Site s : linked) slots += s.slots(stationUnits);
        return slots;
    }

    /** The linked site with the most mean power, or null for none: what the card names. */
    public Site best() {
        Site best = null;
        for (Site s : linked) if (best == null || BEST.compare(s, best) < 0) best = s;
        return best;
    }

    /** How many river segments the last read looked at (0 where nothing was read). */
    public int segmentsRead() { return segmentsRead; }
}
