package ham.citybuildersim;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, each district's street plan kept for the painter with what it cannot hold carried to the next, the city's highways and railway laid on corridors across it, one drawn building for each the model has, summed up a pyramid for the far view, and written beside the save as a sidecar - what the tile painter paints the city from.
 *
 * WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.5). The
 * model counts buildings by type and nothing else: a city of ten billion has
 * 279 million of them and no places. Jerus's mockup placed every building on
 * a 30 m plot as it grew; stored that way a 10B city is gigabytes. So the map
 * keeps counts by type in districts of 256 x 256 plots - 29,929 of them at
 * 10B, 10.8 MB - and paints a tile from its district's counts when the screen
 * needs it (TilePainter), so its cost follows the screen, never the
 * population. NOTHING IN THE MODEL READS IT.
 *
 * A DISTRICT holds its free plots - owned and dry, a resource's sites among
 * them (a mine or well takes its own site's; the painter builds on a site
 * last) - counted plot by plot on each of its tiles since 0.7.64 (32 x 32
 * samples before), recounted in every district a purchase touches; the
 * ground its buildings use (a type's land / 9,687.5 sq ft a plot) and the
 * whole plots they are drawn on (BuildingVisual.cells(), roads by their own
 * plots); its owned iron and oil sites; and its count of every type.
 *
 * EACH MONTH, ONCE CONSTRUCTION COMPLETES (reconcile(), star 12): each type's
 * change is placed - new buildings into the first district with room for
 * their whole plots,
 * districts tried nearest the founding site first, but farms, utilities,
 * mines and wells, the railway and the car plants farthest first; removals in
 * reverse - and nothing placed ever moves. Running totals and a cursor each
 * way keep a month's work to the districts it changes: measured 0.76 ms a
 * month at 10B in the design. Mines and wells are the exception the design
 * names: they stand on their resource's sites, the first in acquisition order
 * (the centre, then each purchase) not worked out, so they go to the
 * districts holding those sites.
 *
 * CANONICALLY (canonical()), for a save with no sidecar, a lost one or one
 * that does not match: each type in proportion in every district, inner
 * first, the remainders by largest remainder - exact. The picture shifts
 * once.
 *
 * THE DRAWN PLANS (0.7.88, batch RD2; the deal of 0.7.64 to 0.7.87 before
 * them, which dealt a district's buildings and road plots onto its tiles
 * for each to grow its own roads): each district is painted from its
 * street plan (DistrictPlan) - its streets with the model's road as their
 * surface, a box for every building - kept for the last PLANS_KEPT districts
 * asked and made away from the screen's thread; what a district's plan
 * cannot hold goes to the next district in the map's order, and what none
 * can is packed at the city's edge without a street (R7). A city is drawn
 * as what it has, not more, not less (Jerus, 2026-10-07). Since 0.7.89 the
 * order is cut into bands of CHAIN_BAND districts (THE CHAIN'S BANDS), so a
 * screen at the edge of a city of ten billion plans one band, not the city;
 * a city of CHAIN_BAND districts or fewer is one band, drawn as before.
 *
 * THE RUNS (0.7.89, batch RD3; spec-roads-and-ports.md 2.7, 2.8): the
 * city's Elevated Highways and railway are laid city-wide, month by month,
 * on corridors from the founding site's lines (CityRuns) - never moved, the
 * newest end taken first - and each district's plan is drawn round them.
 *
 * THE PYRAMID sums ten classes, used plots and owned plots two by two up to
 * one node, built when the map is, each district's ancestors updated as it
 * changes: what the far view draws (batch J4).
 *
 * THE SIDECAR (slot-NN-map.bin, writeSidecar()/readSidecar()): one record a
 * district, deflated, its header the magic, the format, the world's seed, the
 * month, a stamp of the content (also the save's mapStamp), the land it was
 * drawn on and the type count. A map saved and read back writes the same
 * bytes.
 *
 * ON THE BLOCK GRID SINCE 0.7.67 (batch M3; the project's spec-grid.md 2.4).
 * The land's ownership reads are the grid's: whether a district or a tile is
 * owned is LandGrid.cover() at its level (8 or 5), a tile's plots are the
 * grid's leaves under it (tileFlags()), the land's reach is its owned
 * bounding box, a field's or a site's holding is the one owning its plot
 * (CityLand.siteHolding()), and a purchase recounts exactly the districts
 * under its rectangle. The sidecar's land stamp is the holdings' count and a
 * hash of their rectangles (CityLand.stamp(), FORMAT 3; FORMAT 4 since 0.7.72,
 * the roads following their buildings; FORMAT 5 since 0.7.88, the city's
 * highway and railway runs after the districts - 0.7.88 wrote none, and a
 * sidecar with none has them laid from its counts as it is read - an older
 * one still read).
 */
public final class CityMap {

    /* =====================================================================
       THE GEOMETRY
       ===================================================================== */

    /** Plots on a district's side: World.DISTRICT, 256 (7.68 km). */
    public static final int DISTRICT = World.DISTRICT;

    /** Tiles on a district's side: 8. */
    public static final int TILES_A_SIDE = DISTRICT / World.TILE;

    /** Tiles in a district: 64. */
    public static final int TILES = TILES_A_SIDE * TILES_A_SIDE;

    /** Half square feet in a plot: 9,687.5 sq ft twice, so a district's room and use are whole numbers and a month's change is exact however it is added up. */
    static final long HALF_SQ_FT_PER_PLOT = Math.round(2 * BuildingVisual.SQ_FT_PER_PLOT);

    /** A district with less room than this many whole plots is passed by the cursors: one, the least a building is drawn on (0.7.64; half a plot of ground before). */
    static final int MIN_ROOM = 1;

    /** The resources a district counts its owned sites of, and mines and wells stand on: iron and oil (spec-land 2.5). */
    static final Resource[] SITED = { Resource.IRON, Resource.OIL };

    /** How many districts' lists of sites are kept: 64. */
    static final int SITE_LISTS_KEPT = 64;

    /** Above this many districts under the land's box, a canonical build looks at the ground coarsely first and measures only districts with land in or beside them: 1,024 (a box 246 km across). */
    static final int COARSE_ABOVE = 1024;

    /** The coarse look's stride, in plots: 32, a sample a tile. */
    static final int COARSE_STRIDE = 32;

    /** The sidecar's magic: "CMAP". */
    static final int MAGIC = 0x434D4150;

    /** The sidecar's format: 5 since 0.7.88, the city's highway and railway runs after the districts (spec-roads-and-ports.md 2.9: "city-wide state in the sidecar (FORMAT 5)"; 0.7.88 wrote a count of none, 0.7.89 the runs it lays) - a FORMAT 4 sidecar, or a FORMAT 5 one with none, has its runs laid from its counts as it is read (OLDEST_READ), its districts as they were; 4 since 0.7.72, when a district's roads came to follow its buildings (ROADS FOLLOW THEIR BUILDINGS) - an older map, whose districts' roads were placed nearest first, some with none, is drawn again canonically, each district its share; 3 from 0.7.67, its land stamp the holdings' rectangles (CityLand.stamp()) where a centre's half-side was; 2 from 0.7.64, when a district's room became its free plots counted plot by plot. A sidecar older than OLDEST_READ is not read, and the map is drawn again once. */
    static final int FORMAT = 5;

    /** The oldest sidecar read: FORMAT 4 (0.7.72 to 0.7.87) - its districts are placed as 0.7.88 places them; it has no runs, which are laid from its counts as it is read. */
    static final int OLDEST_READ = 4;

    /** Where the stamp sits in the sidecar's raw bytes: after the magic, the format, the seed and the month. */
    static final int STAMP_AT = 4 + 4 + 8 + 4;

    /* =====================================================================
       A DISTRICT
       ===================================================================== */

    /** One district: where it is, its free plots, the ground and the plots used, its owned sites, its counts by type. */
    public static final class District {
        /** Its column and row, in districts from the founding site's. */
        public final int dx, dy;
        final long key;
        final double order;
        /** Its free plots: owned and dry, counted plot by plot (0.7.64; from 32 x 32 samples before) - or, for a harness's square city, given. */
        int owned;
        /** The ground its buildings use, in half square feet: each type's count x its land, kept exactly. */
        long usedHalf;
        /** The whole plots its buildings and roads are drawn on (BuildingVisual.cells()), a mine or well on its site taking its own: what its room is kept in (0.7.64). */
        long usedCells;
        /** Its free plots inside its tiles' edge rings, where alone the painter laid a road until 0.7.87, and the road plots it holds: its room for roads, by which its counts are still placed (0.7.64). */
        int inner;
        long usedRoad;
        /** Its count of every type, by id. */
        final int[] counts;
        /** Its owned sites of each SITED resource. */
        final int[] sites = new int[SITED.length];
        /** Each tile's free plots, row by row: owned and dry; null until measured or asked for. */
        short[] tileFree;
        /** ...and those inside its edge ring, where alone the painter laid a road until 0.7.87 (the counts' room for roads still measures it). */
        short[] tileInner;
        /** Each tile's sites, by key (TilePainter.Input.siteKey), and their free plots on it, all and inside the edge ring: what a mine standing on one takes of the tile. */
        long[][] tileSiteKey;
        short[][] tileSiteFree, tileSiteInner;
        /** How many of each site-bound type stand on its sites (the rest of its count has none); null when none. */
        int[] sited;
        /** For painting: per SITED resource, pairs of {holding, mines} - how many of each holding's sites here carry one. */
        int[][] holdingMines = new int[SITED.length][];
        /** Bumped whenever its counts change: what its sites' list is kept against (its deal's until 0.7.87). */
        long version;
        /** Its place in the map's order, nearest first. */
        int index;

        District(int dx, int dy, int types, double order) {
            this.dx = dx;
            this.dy = dy;
            this.key = key(dx, dy);
            this.order = order;
            this.counts = new int[types];
        }

        /** Its free plots (owned and dry, its sites' plots among them). */
        public int owned()       { return owned; }
        /** The plots of ground its buildings use: each type's count x its land in plots. */
        public double used()     { return usedHalf / (double) HALF_SQ_FT_PER_PLOT; }
        /** The whole plots its buildings and roads are drawn on. */
        public long usedCells()  { return usedCells; }
        public int count(int t)  { return t < counts.length ? counts[t] : 0; }
        public int sites(Resource r) {
            for (int k = 0; k < SITED.length; k++) if (SITED[k] == r) return sites[k];
            return 0;
        }
        /** Its room in whole plots, never below nothing (0.7.64; in ground, half square feet, before). */
        long freeCells()         { return Math.max(0, owned - usedCells); }
        /** ...and for roads: its whole room, no more than its plots inside the edge rings less its road. */
        long freeRoad()          { return Math.min(freeCells(), Math.max(0, inner - usedRoad)); }
    }

    static long key(int dx, int dy) { return ((long) dx << 32) | (dy & 0xffffffffL); }

    /* =====================================================================
       THE STATE
       ===================================================================== */

    private final long seed;
    private final long siteX, siteY;
    private final long baseDX, baseDY;
    private final BuildingVisual.Type[] types;
    private CityLand land;
    private Function<Resource, double[]> remaining;

    private final List<District> districts = new ArrayList<>();
    private final Map<Long, District> byKey = new HashMap<>();
    private final long[] have;
    private final int[] lo, hi;
    private int innerCursor, outerCursor;
    private int purchasesSeen;
    private long centreSeen;
    private boolean measured = true;
    private Pyramid pyramid;
    private SiteIndex siteIndex;
    private boolean sitedPlaced;

    private CityMap(long seed, long siteX, long siteY, BuildingVisual.Type[] types) {
        this.seed = seed;
        this.siteX = siteX;
        this.siteY = siteY;
        this.baseDX = Math.floorDiv(siteX, DISTRICT);
        this.baseDY = Math.floorDiv(siteY, DISTRICT);
        this.types = types;
        this.have = new long[types.length];
        this.lo = new int[types.length];
        this.hi = new int[types.length];
    }

    /** The world's seed. */
    public long seed()                     { return seed; }

    /** The types it counts, by id. */
    public BuildingVisual.Type[] types()   { return types; }

    /** Its districts, nearest the founding site first. */
    public List<District> districts()      { return java.util.Collections.unmodifiableList(districts); }

    /** The district at (dx, dy) from the founding site's, or null. */
    public District district(int dx, int dy) { return byKey.get(key(dx, dy)); }

    /** The pyramid. */
    public Pyramid pyramid()               { return pyramid; }

    /** Whether its districts were measured on the ground (false for a harness's square city, whose capacities were given). */
    public boolean measured()              { return measured; }

    /** Every type's count over all districts. */
    public long[] totals() {
        long[] out = new long[types.length];
        for (District d : districts) for (int t = 0; t < types.length; t++) out[t] += d.counts[t];
        return out;
    }

    /** The land it was drawn on. */
    public CityLand land() { return land; }

    /** The founding site's district's column, absolute. */
    long baseDX() { return baseDX; }

    /** ...and row. */
    long baseDY() { return baseDY; }

    /* =====================================================================
       BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE
       ===================================================================== */

    /**
     * A map drawn canonically (spec-land 2.5): the land's districts measured,
     * then each type in proportion in every district, inner first, the
     * remainders by largest remainder; mines and wells on their sites.
     *
     * @param land       the city's land
     * @param remaining  what remains of a resource in each holding, in acquisition order (LandManager.remainingByHolding)
     * @param types      the types by id (BuildingVisual.table())
     * @param counts     the model's count of every type, by id
     */
    public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining,
                                    BuildingVisual.Type[] types, long[] counts) {
        CityMap m = new CityMap(land.seed(), land.siteX(), land.siteY(), types);
        m.land = land;
        m.remaining = remaining;
        m.measureAll();
        m.allocate(counts);
        m.layRuns(counts);
        return m;
    }

    /**
     * A HARNESS'S SQUARE CITY (the design's scale case): side x side districts
     * round the founding site, every one owned and holding `capacity` plots
     * - a city of any size at one density without measuring its ground; a
     * district's tiles are counted plot by plot only when it is dealt. Its
     * land is a centre of one rectangle, the square of districts (whole
     * blocks of the grid: districts are level-8 blocks), its books given as
     * none, as a harness's record may give them; its sites are the world's
     * under it; nothing in it worked.
     */
    static CityMap square(long seed, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity,
                          long[] counts) {
        CityMap m = new CityMap(seed, siteX, siteY, types);
        int h = side / 2;
        double[] centre = new double[CityLand.CENTRE_FIELDS];
        long x0 = (m.baseDX - h) * DISTRICT, x1 = (m.baseDX - h + side) * DISTRICT;
        long y0 = (m.baseDY - h) * DISTRICT, y1 = (m.baseDY - h + side) * DISTRICT;
        centre[CityLand.CENTRE_FIELDS - 5] = -1;
        centre[CityLand.CENTRE_FIELDS - 4] = -1;
        centre[CityLand.CENTRE_FIELDS - 2] = siteX;
        centre[CityLand.CENTRE_FIELDS - 1] = siteY;
        m.land = CityLand.restore(seed, centre, new double[][] { { x0, y0, x1, y1 } }, null, null, null);
        m.remaining = r -> m.land.amountsInOrder(r);
        m.measured = false;
        for (int dy = -h; dy < side - h; dy++) {
            for (int dx = -h; dx < side - h; dx++) {
                District d = m.newDistrict(dx, dy);
                d.owned = capacity;
                d.inner = capacity;
            }
        }
        m.sortDistricts();
        m.purchasesSeen = 0;
        m.centreSeen = m.land.centreStamp();
        m.siteIndex();
        m.allocate(counts);
        m.layRuns(counts);
        return m;
    }

    private District newDistrict(int dx, int dy) {
        long cx = (baseDX + dx) * DISTRICT + DISTRICT / 2, cy = (baseDY + dy) * DISTRICT + DISTRICT / 2;
        double ox = cx - siteX, oy = cy - siteY;
        District d = new District(dx, dy, types.length, ox * ox + oy * oy);
        districts.add(d);
        d.index = districts.size() - 1;
        byKey.put(d.key, d);
        return d;
    }

    private void sortDistricts() {
        districts.sort(DISTRICT_ORDER);
        for (int i = 0; i < districts.size(); i++) districts.get(i).index = i;
        rangeTypes();
        innerCursor = 0;
        outerCursor = districts.size() - 1;
        advanceCursors();
    }

    /** Each type's lowest and highest district index holding it. */
    private void rangeTypes() {
        Arrays.fill(lo, Integer.MAX_VALUE);
        Arrays.fill(hi, -1);
        for (int i = 0; i < districts.size(); i++) {
            int[] c = districts.get(i).counts;
            for (int t = 0; t < types.length; t++) {
                if (c[t] > 0) {
                    if (i < lo[t]) lo[t] = i;
                    hi[t] = i;
                }
            }
        }
    }

    private void advanceCursors() {
        while (innerCursor < districts.size() && districts.get(innerCursor).freeCells() < MIN_ROOM) innerCursor++;
        while (outerCursor >= 0 && districts.get(outerCursor).freeCells() < MIN_ROOM) outerCursor--;
    }

    /* =====================================================================
       MEASURING THE GROUND: OWNED DRY PLOTS
       ===================================================================== */

    /** Every district the land's owned box reaches, measured; the coarse look first when there are many. */
    private void measureAll() {
        LandGrid g = land.grid();
        boolean any = g.ownedPlots() > 0;
        long dx0 = (any ? Math.floorDiv(g.minX(), DISTRICT) : baseDX) - baseDX;
        long dx1 = (any ? Math.floorDiv(g.maxX() - 1, DISTRICT) : baseDX) - baseDX;
        long dy0 = (any ? Math.floorDiv(g.minY(), DISTRICT) : baseDY) - baseDY;
        long dy1 = (any ? Math.floorDiv(g.maxY() - 1, DISTRICT) : baseDY) - baseDY;
        int nx = (int) (dx1 - dx0 + 1), ny = (int) (dy1 - dy0 + 1);
        boolean[] maybe = null;
        if ((long) nx * ny > COARSE_ABOVE) maybe = coarseLand((int) dx0, (int) dy0, nx, ny);
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                int dx = (int) (dx0 + i), dy = (int) (dy0 + j);
                int own = ownership(dx, dy);
                if (own == NONE || (maybe != null && !maybe[j * nx + i])) continue;
                measureNew(dx, dy, own);
            }
        }
        if (byKey.get(key(0, 0)) == null) {
            District d = newDistrict(0, 0);
            measure(d, ownership(0, 0));
        }
        sortDistricts();
        purchasesSeen = land.purchases().size();
        centreSeen = land.centreStamp();
        siteIndex();
    }

    /** Which districts have land in or beside them, by a sample a tile (COARSE_STRIDE): a district all sea there, with all-sea neighbours, is taken as sea and not measured. */
    private boolean[] coarseLand(int dx0, int dy0, int nx, int ny) {
        int per = DISTRICT / COARSE_STRIDE, block = 16;
        boolean[] land = new boolean[nx * ny];
        byte[] buf = new byte[block * per * block * per];
        World world = World.of(seed);
        for (int bj = 0; bj < ny; bj += block) {
            for (int bi = 0; bi < nx; bi += block) {
                int n = block * per;
                world.regionTerrain((baseDX + dx0 + bi) * DISTRICT, (baseDY + dy0 + bj) * DISTRICT, COARSE_STRIDE, n, buf);
                for (int p = 0; p < n * n; p++) {
                    if (buf[p] == World.SALT) continue;
                    int i = bi + (p % n) / per, j = bj + (p / n) / per;
                    if (i < nx && j < ny) land[j * nx + i] = true;
                }
            }
        }
        boolean[] out = land.clone();
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                if (!land[j * nx + i]) continue;
                for (int b = -1; b <= 1; b++) for (int a = -1; a <= 1; a++) {
                    int x = i + a, y = j + b;
                    if (x >= 0 && y >= 0 && x < nx && y < ny) out[y * nx + x] = true;
                }
            }
        }
        return out;
    }

    static final int NONE = 0, SOME = 1, ALL = 2;

    /** Whether the city owns none, some or all of a district: the grid's cover of it, a level-8 block (spec-grid 2.4; by its radii and lanes until 0.7.66). */
    int ownership(int dx, int dy) {
        return cover(land.grid().cover(8, baseDX + dx, baseDY + dy));
    }

    /** LandGrid's NONE, SOME or ALL as this class's. */
    private static int cover(int c) {
        return c == LandGrid.ALL ? ALL : c == LandGrid.SOME ? SOME : NONE;
    }

    /** The land's owned box from the site, in plots, inclusive {dx0, dy0, dx1, dy1}: its reach each way (an empty land, the site's plot). */
    double[] ownedBox() {
        LandGrid g = land.grid();
        if (g.ownedPlots() == 0) return new double[] { 0, 0, 0, 0 };
        return new double[] { g.minX() - siteX, g.minY() - siteY, g.maxX() - 1 - siteX, g.maxY() - 1 - siteY };
    }

    /**
     * Counts a district's free plots, tile by tile and plot by plot (0.7.64):
     * owned and dry - the ground the model builds on, a resource's sites with
     * it (a mine stands on its site; another building takes a site's plot only
     * when its tile has no other) - and each site's plots on each tile, which
     * a mine standing on it takes. So a district's room is exactly the ground
     * its tiles can draw buildings and roads on. 64 tiles' ground read from
     * the world (World.tileTerrain()), about 8 ms a district (L2's scratch
     * L2Cap); J3's 32 x 32 samples could put more on a tile than it held.
     */
    private void measure(District d, int own) {
        d.tileFree = new short[TILES];
        d.tileInner = new short[TILES];
        d.tileSiteKey = new long[TILES][];
        d.tileSiteFree = new short[TILES][];
        d.tileSiteInner = new short[TILES][];
        d.owned = 0;
        if (own == NONE) return;
        long x0 = (baseDX + d.dx) * DISTRICT, y0 = (baseDY + d.dy) * DISTRICT;
        List<long[]> squares = siteSquares(x0, y0, x0 + DISTRICT - 1, y0 + DISTRICT - 1);
        byte[] ground = new byte[TilePainter.PLOTS];
        boolean[] owns = new boolean[TilePainter.PLOTS], field = new boolean[TilePainter.PLOTS];
        World world = World.of(seed);
        int total = 0;
        for (int k = 0; k < TILES; k++) {
            long tx = (baseDX + d.dx) * TILES_A_SIDE + k % TILES_A_SIDE, ty = (baseDY + d.dy) * TILES_A_SIDE + k / TILES_A_SIDE;
            tileOwnership(tx, ty, owns);
            boolean any = false;
            for (boolean b : owns) if (b) { any = true; break; }
            if (!any) continue;
            world.tileTerrain(tx, ty, ground);
            Arrays.fill(field, false);
            long px0 = tx * World.TILE, py0 = ty * World.TILE;
            List<long[]> here = new ArrayList<>();
            for (long[] q : squares) {
                if (q[2] < px0 || q[0] >= px0 + World.TILE || q[3] < py0 || q[1] >= py0 + World.TILE) continue;
                // A site's plots here, owned and dry, all and inside the edge ring; the first site holding a plot has it (the painter's rule).
                int all = 0, in = 0;
                for (long y = Math.max(q[1], py0); y <= Math.min(q[3], py0 + World.TILE - 1); y++) {
                    for (long x = Math.max(q[0], px0); x <= Math.min(q[2], px0 + World.TILE - 1); x++) {
                        int i = (int) ((y - py0) * World.TILE + (x - px0));
                        if (field[i]) continue;
                        field[i] = true;
                        byte g = ground[i];
                        if (!owns[i] || g == World.SALT || g == World.FRESH) continue;
                        all++;
                        if (x - px0 >= 1 && y - py0 >= 1 && x - px0 < World.TILE - 1 && y - py0 < World.TILE - 1) in++;
                    }
                }
                if (all > 0) here.add(new long[] { (q[0] << 32) | (q[1] & 0xffffffffL), all, in });
            }
            int n = 0, inner = 0;
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                byte g = ground[i];
                if (!owns[i] || g == World.SALT || g == World.FRESH) continue;
                n++;
                int x = i % World.TILE, y = i / World.TILE;
                if (x >= 1 && y >= 1 && x < World.TILE - 1 && y < World.TILE - 1) inner++;
            }
            d.tileFree[k] = (short) n;
            d.tileInner[k] = (short) inner;
            d.tileSiteKey[k] = new long[here.size()];
            d.tileSiteFree[k] = new short[here.size()];
            d.tileSiteInner[k] = new short[here.size()];
            for (int a = 0; a < here.size(); a++) {
                d.tileSiteKey[k][a] = here.get(a)[0];
                d.tileSiteFree[k][a] = (short) here.get(a)[1];
                d.tileSiteInner[k][a] = (short) here.get(a)[2];
            }
            total += n;
        }
        d.owned = total;
        int inner = 0;
        for (int k = 0; k < TILES; k++) inner += d.tileInner[k];
        d.inner = inner;
    }

    /** Every site's square, {x0, y0, x1, y1} in plots inclusive, of every resource in fields, owned or not, that reaches into a box of plots: the plots the painter keeps as fields. */
    private List<long[]> siteSquares(long bx0, long by0, long bx1, long by1) {
        List<long[]> out = new ArrayList<>();
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            double w = Deposit.siteWidth(r);
            for (Deposit f : fieldsNear(r, bx0 - w, by0 - w, bx1 + w, by1 + w)) {
                for (int k = 0; k < f.sites(); k++) {
                    double[] at = f.siteAt(k);
                    double cx = f.x() + at[0], cy = f.y() + at[1];
                    long sx0 = Math.round(cx - w / 2), sy0 = Math.round(cy - w / 2);
                    long sx1 = Math.max(sx0, Math.round(cx + w / 2) - 1), sy1 = Math.max(sy0, Math.round(cy + w / 2) - 1);
                    if (sx1 < bx0 || sx0 > bx1 || sy1 < by0 || sy0 > by1) continue;
                    out.add(new long[] { sx0, sy0, sx1, sy1 });
                }
            }
        }
        return out;
    }

    /** A district not on the map yet, measured, and kept only when it holds free ground: a district of water joins the map only when a site in it needs one (tallySites()). */
    private District measureNew(int dx, int dy, int own) {
        District d = newDistrict(dx, dy);
        measure(d, own);
        if (d.owned > 0) return d;
        districts.remove(districts.size() - 1);
        byKey.remove(d.key);
        return null;
    }

    /** A district's tiles' free plots (a copy), counted if they were not: for a harness and a probe. */
    public short[] tileFreeOf(District d) { return tileFree(d).clone(); }

    /** A district's tiles' free plots, counted if they were not (after a sidecar is read, or for a harness's square city); its room stands as it was. */
    short[] tileFree(District d) {
        if (d.tileFree == null || d.tileInner == null || d.tileSiteKey == null) {
            int owned = d.owned, inner = d.inner;
            measure(d, ownership(d.dx, d.dy));
            d.owned = owned;
            d.inner = inner;
        }
        return d.tileFree;
    }

    /* =====================================================================
       THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN
       ===================================================================== */

    /**
     * Catches the map up with its land: every district a new purchase
     * touches recounted (new ones added); true when the land was drawn
     * again (a restatement, a conversion), which the caller answers with a
     * canonical map.
     */
    boolean syncLand() {
        if (!measured) return false;
        List<CityLand.Purchase> bought = land.purchases();
        if (land.centreStamp() != centreSeen || bought.size() < purchasesSeen) return true;
        if (bought.size() == purchasesSeen) return false;
        // Every new purchase's rectangle: exactly the districts under it are recounted (spec-grid 2.4).
        java.util.Set<Long> touched = new java.util.LinkedHashSet<>();
        int firstNew = purchasesSeen + 1;
        double nbx0 = Double.MAX_VALUE, nby0 = Double.MAX_VALUE, nbx1 = -Double.MAX_VALUE, nby1 = -Double.MAX_VALUE;
        for (int k = purchasesSeen; k < bought.size(); k++) {
            LandParcel o = bought.get(k).offer();
            if (o.getX1() <= o.getX0() || o.getY1() <= o.getY0()) continue;
            long ax0 = Math.floorDiv(o.getX0(), DISTRICT) - baseDX, ax1 = Math.floorDiv(o.getX1() - 1, DISTRICT) - baseDX;
            long ay0 = Math.floorDiv(o.getY0(), DISTRICT) - baseDY, ay1 = Math.floorDiv(o.getY1() - 1, DISTRICT) - baseDY;
            for (long dy = ay0; dy <= ay1; dy++) for (long dx = ax0; dx <= ax1; dx++) touched.add(key((int) dx, (int) dy));
            nbx0 = Math.min(nbx0, o.getX0() - siteX); nbx1 = Math.max(nbx1, o.getX1() - 1 - siteX);
            nby0 = Math.min(nby0, o.getY0() - siteY); nby1 = Math.max(nby1, o.getY1() - 1 - siteY);
        }
        purchasesSeen = bought.size();
        landVersion++;
        changes++;
        for (long k : touched) {
            int dx = (int) (k >> 32), dy = (int) k;
            int own = ownership(dx, dy);
            District d = byKey.get(k);
            if (d == null) {
                if (own != NONE) measureNew(dx, dy, own);
                continue;
            }
            measure(d, own);
            d.version++;
        }
        sortDistricts();
        if (siteIndex != null && nbx0 <= nbx1) tallySites(siteIndex, firstNew, nbx0 - 1, nby0 - 1, nbx1 + 1, nby1 + 1);
        if (pyramid != null) pyramid = new Pyramid(this);
        return false;
    }

    /* =====================================================================
       PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION
       ===================================================================== */

    /**
     * THE MONTH (spec-land 2.5, star 12): the land caught up, then each
     * type's change placed - new buildings into the first district with room
     * (nearest first; farms, utilities, mines and wells, the railway and the
     * car plants farthest first), removals in reverse - and the mines and
     * wells put on their sites. When the land was drawn again it answers
     * false and places nothing: the caller draws the map again canonically.
     * (How many district-types it touched is lastTouched.)
     */
    public boolean reconcile(long[] model) {
        if (syncLand()) return false;
        int touched = 0;
        // The buildings first, then the roads, which follow them (0.7.72, ROADS FOLLOW THEIR BUILDINGS).
        roadShare = roadShareOf(model);
        builtIn.clear();
        builtInSet.clear();
        for (int roads = 0; roads < 2; roads++) {
            for (int t = 0; t < types.length; t++) {
                // ...not the buildings at sea (0.7.91): on none of the city's dry ground, so in no district (BuildingVisual.of()'s sea flag).
                if (types[t] == null || types[t].sea() || types[t].site() != null || (types[t].road() != BuildingVisual.NOT_A_ROAD) != (roads == 1)) continue;
                long want = t < model.length ? model[t] : 0;
                long delta = want - have[t];
                if (delta > 0) touched += roads == 1 ? placeRoad(t, delta) : place(t, delta);
                else if (delta < 0) touched += remove(t, -delta);
            }
        }
        placeSited(model);
        advanceCursors();
        // The highways and railway to the month's counts (0.7.89): grown from their newest end, or taken back from it.
        long r0 = System.nanoTime();
        layRuns(model);
        lastRunsMs = (System.nanoTime() - r0) / 1e6;
        lastTouched = touched;
        // What is worked out moved: the sites drawn are drawn again (their states), the deals stand.
        int[][] states = new int[Resource.values().length][];
        for (Resource r : Resource.values()) if (r.inFields()) states[r.ordinal()] = holdingStates(r);
        if (!Arrays.deepEquals(states, lastStates)) {
            siteLists.clear();
            siteListVersion.clear();
            lastStates = states;
            changes++;
        }
        return true;
    }

    /** How many changes the map has taken since it was drawn - a district's count, a purchase measured, a site's state: what the view stamps its tiles again on (MapTiles). Only grows. */
    private long changes;

    /** How many changes the map has taken since it was drawn: when it moves, a view stamps the tiles it shows again (batch J4). */
    public long changes() { return changes; }

    /**
     * A map drawn away from the screen's thread, on a copy of the land
     * (Game.MapDraft, batch J4), bound to the city's own land and what
     * remains of its resources before it is kept: the copy was the land as
     * it stood, field for field, so every district, site and count drawn on
     * it stands; a purchase made since is the next reconcile()'s to measure.
     */
    void rebind(CityLand live, Function<Resource, double[]> liveRemaining) {
        this.land = live;
        this.remaining = liveRemaining;
        changes++;
    }

    /** Each resource's holdings' states at the last month: when they move, the sites are drawn again. */
    private int[][] lastStates;

    /** How many district-types the last month's change touched. */
    int lastTouched;

    /** How long the last month's runs took to lay, in ms (0.7.89): a harness's. */
    double lastRunsMs;

    /* ---------------------------------------------------------------------
       ROADS FOLLOW THEIR BUILDINGS (0.7.72, batch N3)

       Jerus: "every building is near a road". 0.7.64 placed a road as it
       placed a house, into the first district with room for it: a district
       a road's 46 plots no longer fitted went on taking houses, so the
       playtest's districts ran from 30% road to none at all (month 4,000:
       three districts, 34,000 plots of buildings, no road). Now a building
       goes only where its district keeps room for the road its buildings
       need at the city's share (roadShare: the model's road plots over its
       other plots this month), and the month's new roads go, one by one, to
       the district whose buildings lack road the most.
       --------------------------------------------------------------------- */

    /** The city's road plots for each plot of its other buildings, this month: its roads' whole plots over everything else's (0 with no road). */
    private double roadShare;

    /** The model's road plots over its other whole plots. */
    double roadShareOf(long[] model) {
        double road = 0, other = 0;
        for (int t = 0; t < types.length && t < model.length; t++) {
            if (types[t] == null || model[t] <= 0 || types[t].sea()) continue;
            double c = model[t] * (double) BuildingVisual.cells(types[t]);
            if (types[t].road() != BuildingVisual.NOT_A_ROAD) road += c; else other += c;
        }
        return other > 0 ? road / other : 0;
    }

    /** How many buildings of `each` whole plots district d has room for, keeping room for the road they and its buildings need at the city's share: k with k x each plus the road short of share(d's other plots + k x each) no more than its free plots. */
    long buildingRoom(District d, long each) {
        long free = d.freeCells();
        if (each <= 0) return Long.MAX_VALUE;
        double other = d.usedCells - d.usedRoad;
        double k = Math.min(free / (double) each, (free + d.usedRoad - roadShare * other) / (each * (1 + roadShare)));
        return Math.max(0, (long) Math.floor(k + 1e-9));
    }

    /** Whether district d has room for one more of type t, as the month places it (MapCheck 2 asks). */
    boolean hasRoomFor(District d, int t) {
        long each = BuildingVisual.cells(types[t]);
        return types[t].road() != BuildingVisual.NOT_A_ROAD ? d.freeRoad() >= each : buildingRoom(d, each) >= 1;
    }

    /** The road a district's buildings lack, in plots, at the city's share: share x its other plots less its road plots. */
    double roadLacking(District d) {
        return roadShare * (d.usedCells - d.usedRoad) - d.usedRoad;
    }

    /** The districts the month's new buildings went into (ROADS FOLLOW THEIR BUILDINGS): where its new roads are looked for, so a month's work stays with the districts it changes. */
    private final List<District> builtIn = new ArrayList<>();
    private final java.util.Set<District> builtInSet = new java.util.HashSet<>();

    /** Places n more roads of type t, one at a time: each into the district, of those the month's buildings went into, whose buildings lack road the most and that has room for it inside its tiles' edge rings, the nearer on a tie; the rest as a building is placed. */
    private int placeRoad(int t, long n) {
        long each = BuildingVisual.cells(types[t]);
        java.util.PriorityQueue<double[]> lacking = new java.util.PriorityQueue<>((a, b) -> a[0] != b[0] ? Double.compare(b[0], a[0]) : Double.compare(a[1], b[1]));
        for (District d : builtIn) {
            double lack = roadLacking(d);
            if (lack > 0 && d.freeRoad() >= each) lacking.add(new double[] { lack, d.index });
        }
        int touched = 0;
        while (n > 0 && !lacking.isEmpty()) {
            double[] top = lacking.poll();
            District d = districts.get((int) top[1]);
            if (d.freeRoad() < each) continue;
            add(d, d.index, t, 1);
            n--;
            touched++;
            double lack = roadLacking(d);
            if (lack > 0 && d.freeRoad() >= each) lacking.add(new double[] { lack, d.index });
        }
        return n > 0 ? touched + place(t, n) : touched;
    }

    /** Places n more of type t: into the first districts with room for its whole plots in its direction (a road's inside its tiles' edge rings; since 0.7.72 a building's keeping room for its road, buildingRoom()), the rest into the district with the most room left (0.7.64; the outermost before, which can be a district of water holding only sites). */
    private int place(int t, long n) {
        boolean out = types[t].outer(), road = types[t].road() != BuildingVisual.NOT_A_ROAD;
        long each = BuildingVisual.cells(types[t]);
        int touched = 0;
        int i = out ? outerCursor : innerCursor;
        while (n > 0 && i >= 0 && i < districts.size()) {
            District d = districts.get(i);
            long room = each > 0 ? (road ? d.freeRoad() / each : buildingRoom(d, each)) : n;
            long k = Math.min(n, room);
            if (k > 0) {
                add(d, i, t, k);
                n -= k;
                touched++;
            }
            if (n > 0) i += out ? -1 : 1;
        }
        if (n > 0) {
            District most = roomiest();
            add(most, most.index, t, n);
            touched++;
        }
        return touched;
    }

    /** The district with the most room left, the nearer on a tie: where what no district has room for goes, to be drawn smaller there. */
    District roomiest() {
        District best = districts.isEmpty() ? home() : districts.get(0);
        for (District d : districts) if (d.freeCells() > best.freeCells()) best = d;
        return best;
    }

    /** Takes n of type t away: an inner type from the outermost district holding it in, an outer type from the innermost out. */
    private int remove(int t, long n) {
        boolean out = types[t].outer();
        int touched = 0;
        if (hi[t] < 0) return 0;
        int i = out ? lo[t] : hi[t];
        while (n > 0 && i >= 0 && i < districts.size()) {
            District d = districts.get(i);
            int k = (int) Math.min(n, d.counts[t]);
            if (k > 0) {
                add(d, i, t, -k);
                n -= k;
                touched++;
            }
            if (n > 0) i += out ? 1 : -1;
        }
        // The range shrinks to the districts still holding it.
        while (lo[t] <= hi[t] && lo[t] < districts.size() && districts.get(lo[t]).counts[t] == 0) lo[t]++;
        while (hi[t] >= lo[t] && hi[t] >= 0 && districts.get(hi[t]).counts[t] == 0) hi[t]--;
        if (lo[t] > hi[t]) { lo[t] = Integer.MAX_VALUE; hi[t] = -1; }
        return touched;
    }

    /** Adds k (negative to take away) of type t to the district at index i, keeping its ground and plots used, the running total, the type's range, the cursors and the pyramid. */
    private void add(District d, int i, int t, long k) {
        add(d, i, t, k, false);
    }

    /** ...a mine or well on a site (sited) taking its whole plots too: its site is its ground, the land the model gives it. */
    private void add(District d, int i, int t, long k, boolean sited) {
        d.counts[t] += (int) k;
        have[t] += k;
        if (k > 0 && types[t].road() == BuildingVisual.NOT_A_ROAD && builtInSet.add(d)) builtIn.add(d);
        d.usedHalf += k * 2 * types[t].sqFt();
        d.usedCells += k * BuildingVisual.cells(types[t]);
        if (types[t].road() != BuildingVisual.NOT_A_ROAD) d.usedRoad += k * BuildingVisual.cells(types[t]);
        d.version++;
        changes++;
        if (k > 0) {
            if (i < lo[t]) lo[t] = i;
            if (i > hi[t]) hi[t] = i;
        } else {
            if (i < innerCursor) innerCursor = i;
            if (i > outerCursor) outerCursor = i;
        }
        if (pyramid != null) pyramid.add(d, t, k);
    }

    /** The ground a district's buildings use, in half square feet: each type's count x its land, twice. */
    long usedOf(District d) {
        long u = 0;
        for (int t = 0; t < types.length; t++) if (d.counts[t] != 0 && types[t] != null) u += d.counts[t] * 2 * types[t].sqFt();
        return u;
    }

    /** The road plots a district holds. */
    long roadOf(District d) {
        long u = 0;
        for (int t = 0; t < types.length; t++) {
            if (d.counts[t] != 0 && types[t] != null && types[t].road() != BuildingVisual.NOT_A_ROAD) u += d.counts[t] * (long) BuildingVisual.cells(types[t]);
        }
        return u;
    }

    /** The whole plots a district's buildings and roads are drawn on, a mine or well on its site taking its own. */
    long cellsOf(District d) {
        long u = 0;
        for (int t = 0; t < types.length; t++) if (d.counts[t] != 0 && types[t] != null) u += d.counts[t] * (long) BuildingVisual.cells(types[t]);
        return u;
    }

    /**
     * THE CANONICAL ALLOCATION (spec-land 2.5): the mines and wells on their
     * sites first (0.7.64), then the city's footprint F, in the whole plots
     * it is drawn on, shared inner first - each district its share of F,
     * min(its room, what is left) / F - and each type in that proportion in
     * every district, rounded down, its remainder to the districts with the
     * largest remainders (the nearer on a tie) that have room for one - the
     * largest kinds first - and the rest to the district with the most room
     * left. Exact.
     */
    private void allocate(long[] counts) {
        int n = districts.size();
        // The mines and wells first, on their sites: their ground is theirs before the rest is shared (0.7.64).
        placeSited(counts);
        long[] used = new long[n];
        for (int i = 0; i < n; i++) used[i] = cellsOf(districts.get(i));
        double total = 0;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null || types[t].site() != null || types[t].sea() || t >= counts.length) continue;
            total += counts[t] * (double) BuildingVisual.cells(types[t]);
        }
        double[] share = new double[n];
        double left = total;
        for (int i = 0; i < n && left > 0; i++) {
            double take = Math.min(Math.max(0, districts.get(i).owned - used[i]), left);
            share[i] = total > 0 ? take / total : 0;
            left -= take;
        }
        if (n > 0 && total > 0 && left > 0) share[n - 1] += left / total;
        if (n > 0 && total <= 0) share[0] = 1;
        long[] keys = new long[n];
        // The roads, then the largest kinds first, so a district's room left for a kind's remainders is what those
        // before it left.
        int[] ranked = BuildingVisual.rankedTypes(types);
        int[] order = new int[types.length];
        int no = 0;
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].road() != BuildingVisual.NOT_A_ROAD) order[no++] = t;
        // ...the railway's lines with them (0.7.72: drawn as track, not ranked as buildings).
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].track()) order[no++] = t;
        for (int t : ranked) order[no++] = t;
        for (int oi = 0; oi < no; oi++) {
            int t = order[oi];
            if (types[t] == null || types[t].site() != null || t >= counts.length || counts[t] <= 0) continue;
            long c = counts[t], given = 0, each = BuildingVisual.cells(types[t]);
            int m = 0;
            for (int i = 0; i < n; i++) {
                if (share[i] <= 0) continue;
                double q = c * share[i];
                long whole = (long) Math.floor(q);
                double rem = q - whole;
                if (whole > 0) districts.get(i).counts[t] += (int) whole;
                used[i] += whole * each;
                given += whole;
                // Largest remainder first, then the nearer district: the remainder in the high bits.
                keys[m++] = ((long) ((1 - rem) * (1L << 30)) << 32) | i;
            }
            long extra = c - given;
            if (extra > 0) {
                // ...to the districts with the largest remainders that have room for one (0.7.64: a district is never
                // given more than its plots by a remainder), the rest to the one with the most room left.
                Arrays.sort(keys, 0, m);
                for (int k = 0; k < m && extra > 0; k++) {
                    int i = (int) (keys[k] & 0xffffffffL);
                    if (used[i] + each > districts.get(i).owned) continue;
                    districts.get(i).counts[t]++;
                    used[i] += each;
                    extra--;
                }
                while (extra > 0) {
                    int best = 0;
                    for (int i = 1; i < n; i++) if (districts.get(i).owned - used[i] > districts.get(best).owned - used[best]) best = i;
                    districts.get(best).counts[t]++;
                    used[best] += each;
                    extra--;
                }
            }
            have[t] = c;
        }
        for (District d : districts) {
            d.usedHalf = usedOf(d);
            d.usedCells = cellsOf(d);
            d.usedRoad = roadOf(d);
            d.version++;
        }
        rangeTypes();
        innerCursor = 0;
        outerCursor = districts.size() - 1;
        advanceCursors();
        pyramid = new Pyramid(this);
    }

    /* =====================================================================
       MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5)

       Every owned site of a SITED resource, by holding - the centre, then
       each purchase - and by district. A site's holding is its field's: the
       one holding the field's centre, which owns the field whole wherever
       its sites lie (CityLand, THE FIELDS IN A PIECE OF GROUND; from 0.7.58
       to 0.7.63 the one holding the site's own centre). The mines and wells of a type go to
       the first sites in acquisition order whose holding is not worked out,
       then the worked-out ones in the same order; within a holding, the
       districts nearest first. More of them than sites are drawn in the
       founding site's district with no site under them.
       ===================================================================== */

    /** Every owned site of the SITED resources: per resource and holding, the districts holding them, nearest first, and how many in each. */
    static final class SiteIndex {
        /** [resource][holding]: the districts, and the sites in each. */
        District[][][] where;
        int[][][] how;
        /** The tallies they are read from: [resource] then holding, district to sites. */
        final List<List<Map<District, int[]>>> tally = new ArrayList<>();
    }

    /** The holding a plot (dx, dy) from the site lies in: 0 the centre, k the k-th purchase, -1 none - the grid's owner (spec-grid 2.4). */
    int holdingOf(long dx, long dy) {
        return land.holdingOf(siteX + dx, siteY + dy);
    }

    /** The fields of a resource that reach into a box of plots: the world's, and a converted centre's legacy iron field. */
    private List<Deposit> fieldsNear(Resource r, double bx0, double by0, double bx1, double by1) {
        World world = World.of(seed);
        List<Deposit> out = new ArrayList<>();
        double reach = Deposit.mostReach(r);
        for (int cell : CityLand.cellsUnder(bx0 - reach, by0 - reach, bx1 + reach, by1 + reach)) {
            for (Deposit d : CityLand.fields(world, cell, r)) {
                double o = d.reach();
                if (d.x() + o < bx0 || d.x() - o > bx1 || d.y() + o < by0 || d.y() - o > by1) continue;
                out.add(d);
            }
        }
        if (r == Resource.IRON && land.legacySites() > 0 && land.legacyX() >= 0) {
            Deposit legacy = legacyField();
            double o = legacy.reach();
            if (!(legacy.x() + o < bx0 || legacy.x() - o > bx1 || legacy.y() + o < by0 || legacy.y() - o > by1)) out.add(legacy);
        }
        return out;
    }

    /** A converted centre's legacy iron field, as a field of its sites at its plot (cell -1). */
    Deposit legacyField() {
        int n = Math.min(World.MAX_SITES, land.legacySites());
        return new Deposit(Resource.IRON, -1, 0, land.legacyX(), land.legacyY(), n, land.centreAmount(Resource.IRON));
    }

    /**
     * The site index: every owned site of the SITED resources by holding and
     * district, built with the map and extended by each purchase (a new
     * holding's sites are those of the fields centred on its ground, wherever
     * they lie). A district holding sites but no plot of dry ground is
     * added, wet.
     */
    SiteIndex siteIndex() {
        if (siteIndex == null) {
            siteIndex = new SiteIndex();
            double[] box = ownedBox();
            for (District d : districts) Arrays.fill(d.sites, 0);
            tallySites(siteIndex, 0, box[0], box[1], box[2], box[3]);
        }
        return siteIndex;
    }

    /**
     * Adds to the index the sites of holdings `from` on that lie in a box of
     * plots from the site, and lays its lists out again: a new index object,
     * so the walks lay themselves out again too.
     */
    private void tallySites(SiteIndex ix, int from, double bx0, double by0, double bx1, double by1) {
        int holdings = 1 + land.purchases().size();
        boolean added = false;
        // A purchase's fields go whole (0.7.64), so their sites can lie past
        // the districts under its rectangle, which syncLand() recounted: the
        // others they lie in are drawn again too.
        java.util.Set<District> redraw = from > 0 ? new java.util.LinkedHashSet<>() : null;
        for (int k = 0; k < SITED.length; k++) {
            if (ix.tally.size() <= k) ix.tally.add(new ArrayList<>());
            List<Map<District, int[]>> m = ix.tally.get(k);
            while (m.size() < holdings) m.add(new HashMap<>());
            Resource r = SITED[k];
            if (!r.inFields()) continue;
            for (Deposit f : fieldsNear(r, siteX + bx0, siteY + by0, siteX + bx1, siteY + by1)) {
                double fx = f.x() - siteX, fy = f.y() - siteY;
                boolean legacy = f.cell() < 0;
                if (legacy && from > 0) continue;
                for (int s = 0; s < f.sites(); s++) {
                    // The site's holding: its field's, whole - the one owning the field's centre plot (0.7.64) - or, for
                    // a field a converted city holds in part, the site's own (CityLand.siteHolding(), 0.7.67).
                    int h = land.siteHolding(f, s);
                    if (h < from) continue;
                    double[] at = f.siteAt(s);
                    double sx = fx + at[0], sy = fy + at[1];
                    int ddx = (int) (Math.floorDiv(siteX + (long) Math.floor(sx), DISTRICT) - baseDX);
                    int ddy = (int) (Math.floorDiv(siteY + (long) Math.floor(sy), DISTRICT) - baseDY);
                    District d = byKey.get(key(ddx, ddy));
                    if (d == null) {
                        d = newDistrict(ddx, ddy);
                        d.tileFree = new short[TILES];
                        d.tileInner = new short[TILES];
                        added = true;
                    }
                    d.sites[k]++;
                    m.get(h).computeIfAbsent(d, x -> new int[1])[0]++;
                    if (redraw != null) redraw.add(d);
                }
            }
        }
        if (redraw != null) for (District d : redraw) d.version++;
        if (added) {
            sortDistricts();
            if (pyramid != null) pyramid = new Pyramid(this);
        }
        SiteIndex out = new SiteIndex();
        out.tally.addAll(ix.tally);
        out.where = new District[SITED.length][holdings][];
        out.how = new int[SITED.length][holdings][];
        for (int k = 0; k < SITED.length; k++) {
            for (int h = 0; h < holdings; h++) {
                Map<District, int[]> m = out.tally.get(k).get(h);
                List<District> where = new ArrayList<>(m.keySet());
                where.sort(DISTRICT_ORDER);
                out.where[k][h] = where.toArray(new District[0]);
                out.how[k][h] = new int[where.size()];
                for (int i = 0; i < where.size(); i++) out.how[k][h][i] = m.get(where.get(i))[0];
            }
        }
        siteIndex = out;
    }

    /** The owned sites of a SITED resource on the map. */
    public long ownedSites(Resource r) {
        siteIndex();
        int k = siteSlot(r);
        long n = 0;
        if (k >= 0) for (District d : districts) n += d.sites[k];
        return n;
    }

    /** Nearest the founding site first, then north to south, west to east. */
    static final Comparator<District> DISTRICT_ORDER =
            Comparator.comparingDouble((District d) -> d.order).thenComparingInt(d -> d.dy).thenComparingInt(d -> d.dx);

    /** A holding's state for a resource from what remains in it: UNWORKED, WORKING or WORKED_OUT (one that listed none, unworked). */
    static int holdingState(double listed, double left) {
        if (listed <= 0) return TilePainter.UNWORKED;
        if (left <= 0) return TilePainter.WORKED_OUT;
        return left < listed ? TilePainter.WORKING : TilePainter.UNWORKED;
    }

    /** Each holding's state for a resource, the centre first. */
    int[] holdingStates(Resource r) {
        double[] listed = land.amountsInOrder(r);
        double[] left = remaining != null ? remaining.apply(r) : listed;
        int[] out = new int[listed.length];
        for (int h = 0; h < listed.length; h++) out[h] = holdingState(listed[h], h < left.length ? left[h] : listed[h]);
        return out;
    }

    /** One SITED resource's sites in the order mines and wells take them, and how far its type's count has filled them. */
    private static final class Walk {
        SiteIndex ix;
        int[] states;
        int type;
        District[] at = new District[0];
        int[] holding = new int[0], sites = new int[0];
        int length, pos, partial;
        long placed, overflow;
    }

    private final Walk[] walks = new Walk[SITED.length];

    /** The type standing on a SITED resource's sites: the first with it as its site, or -1. */
    private int typeOn(Resource r) {
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].site() == r) return t;
        return -1;
    }

    /**
     * Puts each SITED resource's mines or wells on its sites (spec-land 2.4,
     * 2.5): the first sites in acquisition order whose holding is not worked
     * out, then the worked-out ones, a holding's districts nearest first;
     * more than there are sites stand in the founding site's district on
     * none. Incremental: a month's change fills or empties the walk from
     * where it stood; the land or a holding's state changing lays it out
     * again. A second type on the same resource stands on no site.
     */
    private void placeSited(long[] model) {
        for (int k = 0; k < SITED.length; k++) {
            Resource r = SITED[k];
            int t = typeOn(r);
            if (t < 0) continue;
            long n = t < model.length ? Math.max(0, model[t]) : 0;
            Walk w = walks[k];
            if (n == 0 && (w == null ? have[t] == 0 : w.placed + w.overflow == 0)) continue;
            SiteIndex ix = siteIndex();
            int[] states = holdingStates(r);
            if (w == null || w.ix != ix || !Arrays.equals(w.states, states) || w.type != t) {
                clearType(t, k);
                w = layWalk(ix, states, k, t);
                walks[k] = w;
            }
            fill(w, k, n);
        }
        // Any further type on a SITED resource: in the founding site's district, on no site, as any building.
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null || types[t].site() == null || typeOn(types[t].site()) == t) continue;
            long delta = (t < model.length ? model[t] : 0) - have[t];
            if (delta != 0) add(home(), indexOf(home()), t, delta);
        }
        sitedPlaced = true;
    }

    /** The mines and wells stood on their sites (placeSited()), once, before anything reads a district's counts less them: a plan's inputs and its stamp (0.7.88), as the sites' lists do. */
    private void sitedNow() {
        if (!sitedPlaced && !districts.isEmpty()) placeSited(totals());
    }

    private District home() {
        District d = byKey.get(key(0, 0));
        if (d == null) {
            d = newDistrict(0, 0);
            d.tileFree = new short[TILES];
            d.tileInner = new short[TILES];
            sortDistricts();
            if (pyramid != null) pyramid = new Pyramid(this);
        }
        return d;
    }

    private static int indexOf(District d) { return d.index; }

    /** Where a resource sits among SITED, or -1. */
    static int siteSlot(Resource r) {
        for (int k = 0; k < SITED.length; k++) if (SITED[k] == r) return k;
        return -1;
    }

    /** Takes every one of type t off the map, its sites' marks with it. */
    private void clearType(int t, int k) {
        if (hi[t] < 0) return;
        for (int i = Math.max(0, lo[t]); i <= hi[t] && i < districts.size(); i++) {
            District d = districts.get(i);
            int sited = d.sited == null ? 0 : d.sited[t];
            if (d.counts[t] - sited != 0) add(d, i, t, -(d.counts[t] - sited));
            if (sited != 0) add(d, i, t, -sited, true);
            if (d.sited != null) d.sited[t] = 0;
            d.holdingMines[k] = null;
        }
        lo[t] = Integer.MAX_VALUE;
        hi[t] = -1;
    }

    /** The walk's order: holdings not worked out in acquisition order, then the worked-out; within each, its districts nearest first. */
    private Walk layWalk(SiteIndex ix, int[] states, int k, int t) {
        Walk w = new Walk();
        w.ix = ix;
        w.states = states;
        w.type = t;
        int holdings = ix.where[k].length, size = 0;
        for (int h = 0; h < holdings; h++) size += ix.where[k][h].length;
        w.at = new District[size];
        w.holding = new int[size];
        w.sites = new int[size];
        for (int pass = 0; pass < 2; pass++) {
            for (int h = 0; h < holdings; h++) {
                boolean out = h < states.length && states[h] == TilePainter.WORKED_OUT;
                if (out != (pass == 1)) continue;
                for (int i = 0; i < ix.where[k][h].length; i++) {
                    w.at[w.length] = ix.where[k][h][i];
                    w.holding[w.length] = h;
                    w.sites[w.length] = ix.how[k][h][i];
                    w.length++;
                }
            }
        }
        return w;
    }

    /** Fills or empties a walk to n: on sites from where it stood, past the last site in the founding site's district. */
    private void fill(Walk w, int k, long n) {
        int t = w.type;
        while (w.placed + w.overflow < n) {
            if (w.pos >= w.length) {
                long rest = n - w.placed - w.overflow;
                District h = home();
                add(h, indexOf(h), t, rest);
                w.overflow += rest;
                break;
            }
            District d = w.at[w.pos];
            int take = (int) Math.min(w.sites[w.pos] - w.partial, n - w.placed - w.overflow);
            mark(d, k, t, w.holding[w.pos], take);
            w.placed += take;
            w.partial += take;
            if (w.partial >= w.sites[w.pos]) { w.pos++; w.partial = 0; }
        }
        while (w.placed + w.overflow > n) {
            if (w.overflow > 0) {
                long off = Math.min(w.overflow, w.placed + w.overflow - n);
                District h = home();
                add(h, indexOf(h), t, -off);
                w.overflow -= off;
                continue;
            }
            if (w.partial == 0) { w.pos--; w.partial = w.sites[w.pos]; }
            District d = w.at[w.pos];
            int take = (int) Math.min(w.partial, w.placed - n);
            mark(d, k, t, w.holding[w.pos], -take);
            w.placed -= take;
            w.partial -= take;
        }
    }

    /** m more (or fewer) of type t on a district's sites of one holding. */
    private void mark(District d, int k, int t, int holding, int m) {
        if (m == 0) return;
        if (d.sited == null) d.sited = new int[types.length];
        d.sited[t] += m;
        int[] pairs = d.holdingMines[k];
        int at = -1;
        if (pairs != null) for (int a = 0; a < pairs.length; a += 2) if (pairs[a] == holding) at = a;
        if (at < 0) {
            pairs = pairs == null ? new int[2] : Arrays.copyOf(pairs, pairs.length + 2);
            at = pairs.length - 2;
            pairs[at] = holding;
        }
        pairs[at + 1] += m;
        d.holdingMines[k] = pairs;
        add(d, indexOf(d), t, m, true);
    }

    /* =====================================================================
       THE DRAWN PLANS: EVERY DISTRICT FROM ITS STREET PLAN (0.7.88, batch
       RD2; the project's spec-roads-and-ports.md 2.6, 2.9 and R7)

       0.7.64 to 0.7.87 DEALT a district's buildings and road plots onto its
       64 tiles - its road onto a few ROAD TILES - and each tile grew its
       roads from what it was dealt, as lanes and a fill beside them inside
       an edge ring: the maze, the tiles with no road and the walls of
       buildings along every tile edge in Jerus's screenshot (spec 1). Since
       0.7.88 a district is painted from its PLAN (DistrictPlan): its
       streets - one network, + junctions on the lattice, the model's road
       as their surface - and a box for every building. A plan is packed as
       the painter reads it (Drawn: each tile's street codes, its buildings'
       boxes) and kept, PLANS_KEPT of them, against a stamp of everything it
       is drawn from (ownStamp()); made away from the screen's thread
       (planJobs(), Job.run() on any thread, adopt() back here) or, for a
       harness or a probe, here (drawnOf()).

       R7, WHAT A DISTRICT CANNOT HOLD. The buildings a plan finds no place
       for are CARRIED to the next district in the map's order
       (DISTRICT_ORDER: nearest the founding site first, the order the month
       fills districts in), which plans them with its own: a chain whose
       every link is kept (Link: its own stamp, what came in, what went on),
       so a month that changes a district plans again only it and those
       after it whose carry moved. What the last district cannot hold, the
       city has no room for anywhere: it is PACKED at the city's edge
       (PackJob) - into the free ground the plans of its EDGE districts
       leave (a district with a neighbour off the map), spread over their
       tiles, the farthest from the founding site first, without a street -
       and counted in the map's legend. A district inside the edge is drawn
       from itself and the districts before it alone; an edge district
       waits for them all.
       ===================================================================== */

    /** How many districts' plans are kept, packed for the painter (Drawn): 64, as the deals and road tiles were (spec 2.9) - each tile's street rows once a pattern and four bytes a building (RD1's plan was 140 KB as DistrictPlan holds it); counted in the view's budget (MapTiles.budgetBytes()). */
    public static final int PLANS_KEPT = 64;

    /** A packed box's flag (Drawn.boxes): drawn without a street, R7's packing at the city's edge. */
    public static final int PACKED_BIT = 1 << 20;

    /** Where a Drawn box's type id starts: above the box's 20 bits and PACKED_BIT, eleven bits for ids under 2,048 (buildings.json's ids are under 128). */
    public static final int BOX_TYPE_SHIFT = 21;

    /** A district's plan as the painter reads it (0.7.88): each tile's street codes and its buildings' boxes, and the figures a harness reads. */
    public static final class Drawn {
        /** Its link's stamp: its own inputs (ownStamp()) and what was carried to it. */
        long stamp;
        /** Each tile's street codes (TilePainter's S_ codes), null where it has none, packed by row (pack()): a byte for each of its 32 rows saying which of its patterns that row is, then the patterns, 32 codes each - a tile's streets run along its rows and columns, so a handful of patterns draw it, not a byte a plot. */
        final byte[][] tiles = new byte[TILES][];
        /** The frame's east column and south row: the first column and row of the districts east and south of it, where its edge cells' last arterials run (null where it lays none). */
        byte[] east, south;
        /** Each tile's buildings, an int each: the box - x, y, w - 1 and h - 1 in the tile's plots, five bits each, PACKED_BIT when packed without a street - and the type id from BOX_TYPE_SHIFT. */
        final int[][] boxes = new int[TILES][];
        /** Buildings its plan placed, and what it could not hold (carried on, R7). */
        int placed, refused;
        /** Its streets' surface in half plots by kind, [0, gravel, paved, highway], the plots of street it lays and how many of them are tracks: what its own road is drawn as. */
        final long[] halves = new long[4];
        int streets, tracks;
        /** Its pieces joined to the streets of the districts before it, those no way over its ground joins, and its one-sided seams surfaced (DistrictPlan's figures). */
        int joinsOut, partedOut, seamsSurfaced;
        public int joinsOut() { return joinsOut; }
        public int partedOut() { return partedOut; }
        public int seamsSurfaced() { return seamsSurfaced; }

        /** Road it has no street for (the plan's surplus), in plots, and by kind [0, gravel, paved, highway]. */
        double surplus;
        final double[] leftover = new double[4];

        /** The bytes it holds, about: what the view's budget counts. */
        long bytes() {
            long b = 64 + 2L * DistrictPlan.FRAME;
            for (byte[] t : tiles) if (t != null) b += 16 + t.length;
            for (int[] x : boxes) if (x != null) b += 16 + 4L * x.length;
            return b;
        }

        /** Its plan's buildings, by type id: the boxes counted. */
        public long[] buildingsByType(int types) {
            long[] n = new long[types];
            for (int[] x : boxes) if (x != null) for (int b : x) n[b >>> BOX_TYPE_SHIFT]++;
            return n;
        }

        /** The street code its plan lays on plot (x, y) of its frame: the district's 256 x 256 and its east column and south row (x or y = DISTRICT); 0 for none. */
        public byte codeAt(int x, int y) {
            if (x == DISTRICT) return east == null ? 0 : east[y];
            if (y == DISTRICT) return south == null ? 0 : south[x];
            byte[] t = tiles[(y / World.TILE) * TILES_A_SIDE + x / World.TILE];
            return t == null ? 0 : codeIn(t, x % World.TILE, y % World.TILE);
        }

        /** A packed tile's code at plot (x, y) of it. */
        static byte codeIn(byte[] t, int x, int y) { return t[World.TILE + (t[y] & 0xff) * World.TILE + x]; }

        /** A packed tile's codes, a byte a plot row by row, into out. */
        static void unpack(byte[] t, byte[] out) {
            for (int y = 0; y < World.TILE; y++) System.arraycopy(t, World.TILE + (t[y] & 0xff) * World.TILE, out, y * World.TILE, World.TILE);
        }

        /** A tile's codes (a byte a plot row by row) packed by row: each row's pattern once, in the order first met. */
        static byte[] pack(byte[] plots) {
            int n = World.TILE, rows = 0;
            int[] which = new int[n], first = new int[n];
            for (int y = 0; y < n; y++) {
                int r = 0;
                while (r < rows && !Arrays.equals(plots, y * n, y * n + n, plots, first[r] * n, first[r] * n + n)) r++;
                if (r == rows) first[rows++] = y;
                which[y] = r;
            }
            byte[] out = new byte[n + rows * n];
            for (int y = 0; y < n; y++) out[y] = (byte) which[y];
            for (int r = 0; r < rows; r++) System.arraycopy(plots, first[r] * n, out, n + r * n, n);
            return out;
        }

        /** Its surface in plots of kind (BuildingVisual's GRAVEL to HIGHWAY). */
        public double surface(int kind) { return halves[kind] / 2.0; }
        /** The plots of street it lays, and those of them tracks. */
        public int streets() { return streets; }
        public int tracks() { return tracks; }
        /** Buildings its plan placed, and refused (carried on). */
        public int placed() { return placed; }
        public int refused() { return refused; }
        /** Road it has no street for, in plots; of a kind. */
        public double surplus() { return surplus; }
        public double leftover(int kind) { return leftover[kind]; }
    }

    /** A plan's street code as the painter reads it (TilePainter's S_ codes): DistrictPlan's kind and width, its role - an arterial, a boulevard's, or a street (a cell's, along a cut, the join's) - and a bridge. */
    static byte paintCode(short code) {
        if (code == 0) return 0;
        int kind = code & DistrictPlan.KIND_MASK, width = (code >> DistrictPlan.WIDTH_SHIFT) & 3, role = (code >> DistrictPlan.ROLE_SHIFT) & 7;
        int r = role == DistrictPlan.ROLE_ARTERIAL ? TilePainter.S_ARTERIAL : role == DistrictPlan.ROLE_BOULEVARD ? TilePainter.S_BOULEVARD : 0;
        int b = kind | (width << TilePainter.S_WIDTH_SHIFT) | (r << TilePainter.S_ROLE_SHIFT);
        if ((code & DistrictPlan.BRIDGE) != 0) b |= TilePainter.S_BRIDGE;
        return (byte) b;
    }

    /** A plan packed for the painter, with its stamp. */
    static Drawn drawnOf(DistrictPlan p, long stamp) {
        Drawn out = new Drawn();
        out.stamp = stamp;
        int f = DistrictPlan.FRAME;
        byte[][] raw = new byte[TILES][];
        for (int y = 0; y < f; y++) {
            for (int x = 0; x < f; x++) {
                short code = p.street[y * f + x];
                if (code == 0) continue;
                int kind = code & DistrictPlan.KIND_MASK;
                byte b = paintCode(code);
                if (x == DISTRICT || y == DISTRICT) {
                    if (x == DISTRICT) {
                        if (out.east == null) out.east = new byte[f];
                        out.east[y] = b;
                    }
                    if (y == DISTRICT) {
                        if (out.south == null) out.south = new byte[f];
                        out.south[x] = b;
                    }
                } else {
                    int k = (y / World.TILE) * TILES_A_SIDE + x / World.TILE;
                    if (raw[k] == null) raw[k] = new byte[TilePainter.PLOTS];
                    raw[k][(y % World.TILE) * World.TILE + x % World.TILE] = b;
                }
                // Its own road as drawn: every street it lays, its surface by kind (a seam's and a street's beneath a highway are none).
                if (kind == DistrictPlan.UNDER || kind == DistrictPlan.SEAM) continue;
                out.streets++;
                int halves = ((code >> DistrictPlan.WIDTH_SHIFT) & 3);
                if (kind == DistrictPlan.TRACK) out.tracks++;
                else if (kind == DistrictPlan.GRAVEL) out.halves[BuildingVisual.GRAVEL] += halves;
                else if (kind == DistrictPlan.PAVED) out.halves[BuildingVisual.PAVED] += halves;
                else if (kind == DistrictPlan.HIGHWAY) out.halves[BuildingVisual.HIGHWAY] += halves;
            }
        }
        for (int k = 0; k < TILES; k++) if (raw[k] != null) out.tiles[k] = Drawn.pack(raw[k]);
        int[] n = new int[TILES];
        for (int b = 0; b < p.buildings; b++) n[(p.by[b] / World.TILE) * TILES_A_SIDE + p.bx[b] / World.TILE]++;
        for (int k = 0; k < TILES; k++) if (n[k] > 0) out.boxes[k] = new int[n[k]];
        Arrays.fill(n, 0);
        for (int b = 0; b < p.buildings; b++) {
            int k = (p.by[b] / World.TILE) * TILES_A_SIDE + p.bx[b] / World.TILE;
            out.boxes[k][n[k]++] = box(p.bx[b] % World.TILE, p.by[b] % World.TILE, p.bw[b], p.bh[b], false) | (p.btype[b] << BOX_TYPE_SHIFT);
        }
        out.placed = p.buildings;
        out.refused = p.overflowCount();
        out.surplus = p.surplus;
        System.arraycopy(p.leftover, 0, out.leftover, 0, 4);
        out.joinsOut = p.joinsOut;
        out.partedOut = p.partedOut;
        out.seamsSurfaced = p.seamsSurfaced;
        return out;
    }

    /** A box packed into an int: x, y, w - 1 and h - 1 at five bits each, PACKED_BIT when packed without a street. */
    static int box(int x, int y, int w, int h, boolean packed) {
        return x | (y << 5) | ((w - 1) << 10) | ((h - 1) << 15) | (packed ? PACKED_BIT : 0);
    }

    /** A district's link in the chain (R7): the stamp of its own inputs, what was carried to it and what it carried on (by type id, null for none); for an edge district, its plan's free ground (a bit a plot of the district), where the city's packed buildings may stand; its frame's edges' street codes (margins), which tell the districts after it which of their seams it leaves unlaid and where their streets join its; and the districts about it before it in the map's order, with the stamps of their links its plan read (readNeighbours()). Kept for every district planned, never dropped. */
    static final class Link {
        final long own, serial;
        final int[] in, out;
        final long[] free;
        final byte[][] margins;
        final District[] earlier;
        final long[] read;
        /** Its plan's road with no street to carry it (DistrictPlan.surplus), in plots: what the legend counts (star RD2-4); by kind [0, gravel, paved, highway]. */
        final double surplus;
        double[] leftover;
        Link(long own, int[] in, int[] out, long[] free, byte[][] margins, District[] earlier, long[] read, double surplus, long serial) {
            this.own = own; this.in = in; this.out = out; this.free = free; this.margins = margins;
            this.earlier = earlier; this.read = read; this.surplus = surplus; this.serial = serial;
        }
        /** Its plan's stamp: its own inputs and what came in. */
        long stamp() { return stampOf(own, in); }
    }

    /** A plan's stamp: its own inputs' and the carry in's. */
    static long stampOf(long own, int[] in) {
        long h = World.mix(own ^ 0x4C494E4BL);
        if (in != null) for (int t = 0; t < in.length; t++) if (in[t] != 0) h = World.mix(h ^ ((long) t << 32) ^ in[t]);
        return h;
    }

    /** A plan frame's edges' street codes (Link.margins): its west column (x 0) and north row (y 0), its east column (x 256, the next district's first) and south row (y 256), each 257 long; and M_PARTED, a byte along them with bit M_WEST to M_SOUTH set where that edge's street is parted from the network (DistrictPlan.parted). */
    static final int M_WEST = 0, M_NORTH = 1, M_EAST = 2, M_SOUTH = 3, M_PARTED = 4;

    /** A plan's frame edges' street codes (M_WEST to M_SOUTH) and which of them are parted (M_PARTED). */
    static byte[][] marginsOf(DistrictPlan p) {
        int f = DistrictPlan.FRAME;
        byte[][] m = new byte[5][f];
        int[] at = new int[4];
        for (int i = 0; i < f; i++) {
            at[M_WEST] = i * f;
            at[M_NORTH] = i;
            at[M_EAST] = i * f + DISTRICT;
            at[M_SOUTH] = DISTRICT * f + i;
            for (int k = 0; k < 4; k++) {
                m[k][i] = paintCode(p.street[at[k]]);
                if (p.parted[at[k]]) m[M_PARTED][i] |= (byte) (1 << k);
            }
        }
        return m;
    }

    /** The eight districts about one, by offset {dx, dy}: north, east, south, west, then north-west, north-east, south-east, south-west - the order of a frame's seam parts (DistrictPlan.Input.surfaces). */
    static final int[][] AROUND = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 }, { -1, -1 }, { 1, -1 }, { 1, 1 }, { -1, 1 } };

    /** Each corner seam part's districts sharing it besides its own, as AROUND's directions: north-west, north-east, south-east, south-west. */
    static final int[][] CORNER_DIRS = { { 3, 0, 4 }, { 1, 0, 5 }, { 1, 2, 6 }, { 3, 2, 7 } };

    /** The districts about d (AROUND's order) that come before it in the map's order, in its band (0.7.89; CHAIN_BAND), null for the rest: whose plans its plan reads - which of its seams they leave unlaid, and where its streets join theirs. */
    District[] earlierOf(District d) {
        District[] out = new District[AROUND.length];
        for (int k = 0; k < AROUND.length; k++) {
            District od = byKey.get(key(d.dx + AROUND[k][0], d.dy + AROUND[k][1]));
            if (od != null && DISTRICT_ORDER.compare(od, d) < 0 && band(od.index) == band(d.index)) out[k] = od;
        }
        return out;
    }

    /** Where district d's frame plot (x, y) lies in the frame of its neighbour at offset (ox, oy), read from that plan's margins - a plot of its frame's edge - or 0 when it is not on them. */
    static byte marginAt(byte[][] m, int x, int y, int ox, int oy) {
        int k = marginOf(x, y, ox, oy);
        return k < 0 ? 0 : m[k >> 16][k & 0xffff];
    }

    /** ...whether the street there is parted from the network (M_PARTED); false where it is not on them. */
    static boolean partedAt(byte[][] m, int x, int y, int ox, int oy) {
        int k = marginOf(x, y, ox, oy);
        return k >= 0 && (m[M_PARTED][k & 0xffff] & (1 << (k >> 16))) != 0;
    }

    /** ...which margin and place: the margin (M_WEST to M_SOUTH) << 16 | the index along it, or -1. */
    private static int marginOf(int x, int y, int ox, int oy) {
        int e = DISTRICT, sx = x - ox * e, sy = y - oy * e;
        if (sx < 0 || sy < 0 || sx > e || sy > e) return -1;
        if (sx == 0) return M_WEST << 16 | sy;
        if (sx == e) return M_EAST << 16 | sy;
        if (sy == 0) return M_NORTH << 16 | sx;
        if (sy == e) return M_SOUTH << 16 | sx;
        return -1;
    }

    /**
     * What district d's plan reads of the plans before it (0.7.88), from
     * their frames' edges (margins, AROUND's order; null for a district after
     * it or none): ONE-SIDED SEAMS, its seams no district before it sharing
     * them lays a street on (in.seamOpen) - an edge's the district beyond it,
     * which surfaces it; a corner's up to three - so the first district that
     * lays a seam the surfacing one leaves surfaces it, once; and ACROSS
     * DISTRICTS, every plot of its frame's edge a district before it lays a
     * street on the network on (in.anchor) - and PARTED, those it lays a
     * street on its own ground parted from the network (in.partedAnchor),
     * which this plan joins where its ground lets it.
     */
    static void readNeighbours(District d, District[] earlier, byte[][][] margins, DistrictPlan.Input in) {
        int e = DISTRICT;
        for (int k = 0; k < AROUND.length; k++) {
            if (earlier[k] == null || margins[k] == null) continue;
            byte[][] m = margins[k];
            int ox = AROUND[k][0], oy = AROUND[k][1];
            // The plots of d's frame this neighbour's frame shares: an edge's line, or a corner's plot.
            for (int i = 0; i <= e; i++) {
                int x, y;
                if (k == 0) { x = i; y = 0; } else if (k == 1) { x = e; y = i; } else if (k == 2) { x = i; y = e; } else if (k == 3) { x = 0; y = i; }
                else { if (i > 0) break; x = ox < 0 ? 0 : e; y = oy < 0 ? 0 : e; }
                byte code = marginAt(m, x, y, ox, oy);
                if ((code & TilePainter.S_KIND) == DistrictPlan.NONE) continue;
                if (partedAt(m, x, y, ox, oy)) in.partedAnchor[y * DistrictPlan.FRAME + x] = true;
                else in.anchor[y * DistrictPlan.FRAME + x] = true;
            }
        }
        // A plot one district before it has on the network is on it.
        for (int p = 0; p < DistrictPlan.AREA; p++) if (in.anchor[p]) in.partedAnchor[p] = false;
        // The seams it does not surface: open where every district before it sharing the plot leaves it without a street - an
        // edge's the one beyond it, which surfaces it; a corner's up to three, of which the first surfaces it and, where that one
        // leaves it, the first that lays it does (so a corner is surfaced once).
        for (int k = 0; k < AROUND.length; k++) {
            int[] dirs = k < 4 ? new int[] { k } : CORNER_DIRS[k - 4];
            boolean any = false;
            for (int c : dirs) any |= earlier[c] != null;
            if (!any) continue;
            if (k < 4) {
                if (margins[k] == null) continue;
                int ox = AROUND[k][0], oy = AROUND[k][1];
                for (int i = 1; i < e; i++) {
                    int x = k == 1 ? e : k == 3 ? 0 : i, y = k == 0 ? 0 : k == 2 ? e : i;
                    open(in, x, y, marginAt(margins[k], x, y, ox, oy));
                }
            } else {
                int x = k == 4 || k == 7 ? 0 : e, y = k == 4 || k == 5 ? 0 : e;
                boolean laid = false, read = true;
                for (int c : dirs) {
                    if (earlier[c] == null) continue;
                    if (margins[c] == null) { read = false; continue; }
                    laid |= (marginAt(margins[c], x, y, AROUND[c][0], AROUND[c][1]) & TilePainter.S_KIND) != DistrictPlan.NONE;
                }
                if (read && !laid) in.seamOpen[y * DistrictPlan.FRAME + x] = true;
            }
        }
    }

    private static void open(DistrictPlan.Input in, int x, int y, byte code) {
        if ((code & TilePainter.S_KIND) == DistrictPlan.NONE) in.seamOpen[y * DistrictPlan.FRAME + x] = true;
    }

    /** What the city has no room for, packed at its edge (R7): each edge district's packed boxes by tile, an int each as Drawn.boxes; how many were packed, how many drawn smaller than their own land, and how many found no ground at all. */
    static final class Pack {
        long stamp;
        final Map<District, int[][]> boxes = new HashMap<>();
        int packed, smaller, none;
    }

    private final Map<District, Drawn> drawn = new LinkedHashMap<>(PLANS_KEPT * 2, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<District, Drawn> e) { return size() > PLANS_KEPT; }
    };
    private final Map<District, Link> links = new HashMap<>();
    private final Map<District, PlanJob> pending = new HashMap<>();
    /** Each band's packing at its edge, and the job making it. */
    private final Map<Integer, Pack> packs = new HashMap<>();
    private final Map<Integer, PackJob> packings = new HashMap<>();

    /**
     * THE CHAIN'S BANDS (0.7.89, batch RD3). A district's plan reads the
     * plans before it - what they carry on, their frames' edges - and an edge
     * district's waits for the packing, so a screen at the edge of a city of
     * ten billion would plan every district (RD2's estimate: minutes on the
     * worker) and keep a link for each. The map's order is cut into bands of
     * CHAIN_BAND districts, nearest the founding site first: what a district
     * cannot hold is carried on within its band, its plan reads its
     * neighbours before it in its band, and what the band's last district
     * cannot hold is packed at the band's own edge districts (none: counted).
     * A city of CHAIN_BAND districts or fewer - Jerus's has 93 - is one band,
     * drawn as 0.7.88 drew it.
     */
    public static final int CHAIN_BAND = 128;

    /** The bands whose links are kept, the last used: 16 (2,048 districts' links, about 4 MB) - a band let go is planned again when a screen asks for it. */
    public static final int BANDS_KEPT = 16;

    /** The band of the district at index i in the map's order. */
    static int band(int i) { return i / CHAIN_BAND; }

    /** Bands in the map's order. */
    int bands() { return (districts.size() + CHAIN_BAND - 1) / CHAIN_BAND; }

    /** Each band's links checked at an epoch (bandEpoch): those of its districts up to bandValidTo (an index in the map's order) hold, and (when all do) the stamp of the packing they make. */
    private long[] bandEpoch = new long[0], bandPackWanted = new long[0];
    private int[] bandValidTo = new int[0];
    private int bandsSized = -1;

    /** The bands used, the last last: those past BANDS_KEPT are let go (forgetBand()). */
    private final LinkedHashMap<Integer, Boolean> bandsUsed = new LinkedHashMap<>(BANDS_KEPT * 2, 0.75f, true);

    /** Jobs handed out so far: each job's serial, which keeps a later job's link over an earlier one's adopted after it. */
    private long jobsHanded;

    /** What the plans hang on, cheaply: the map's changes, and the land's purchases (a purchase owns ground at once, before the month measures it; the land drawn again draws a new map). */
    private long planEpoch() {
        return World.mix(World.mix(changes ^ 0x91A7L) ^ land.purchases().size());
    }

    /** Whether district d is at the city's edge: a neighbour of its eight is not on the map - where what the city has no room for is packed (R7). */
    boolean isEdge(District d) {
        for (int b = -1; b <= 1; b++) for (int a = -1; a <= 1; a++) if ((a != 0 || b != 0) && byKey.get(key(d.dx + a, d.dy + b)) == null) return true;
        return false;
    }

    /**
     * A stamp of everything district d's own plan is drawn from but what is
     * carried to it: its buildings and road by type (its mines and wells on
     * sites apart), the ground the city owns over its frame (ownership only
     * grows, and a restatement draws the map again, so the count of owned
     * plots tells), the sites and mines about it, the city's highway and
     * railway runs through its frame (0.7.89) and its terminals they draw as
     * yards, and which neighbours are on the map (whose seams it surfaces).
     */
    long ownStamp(District d) {
        sitedNow();
        long h = World.mix(seed ^ d.key * 0x9E3779B97F4A7C15L);
        long x0 = (baseDX + d.dx) * DISTRICT, y0 = (baseDY + d.dy) * DISTRICT;
        h = World.mix(h ^ land.grid().owned(x0, y0, x0 + DistrictPlan.FRAME, y0 + DistrictPlan.FRAME));
        for (int t = 0; t < types.length; t++) {
            int c = d.counts[t] - (d.sited == null ? 0 : d.sited[t]);
            if (c != 0) h = World.mix(h ^ ((long) t << 32) ^ c);
        }
        long fx1 = x0 + DistrictPlan.FRAME - 1, fy1 = y0 + DistrictPlan.FRAME - 1;
        for (int b = -1; b <= 1; b++) {
            for (int a = -1; a <= 1; a++) {
                boolean on = byKey.get(key(d.dx + a, d.dy + b)) != null;
                h = World.mix(h ^ (on ? 0x0A + 3 * a + b : 0x0FF));
                if (!on) continue;
                for (DrawnSite s : siteList(d.dx + a, d.dy + b)) {
                    if (s.x1() < x0 || s.y1() < y0 || s.x0() > fx1 || s.y0() > fy1) continue;
                    h = World.mix(h ^ (s.x0() * 0x9E3779B97F4A7C15L) ^ (s.y0() << 20) ^ ((long) s.mine() << 50) ^ s.state());
                }
            }
        }
        // The city's runs over its frame (0.7.89), and its Rail Terminals the runs draw as yards.
        h = World.mix(h ^ runs.frameHash(x0, y0, DistrictPlan.FRAME, DistrictPlan.FRAME));
        h = World.mix(h ^ yardsOf(d));
        return h;
    }

    /** The bands' arrays sized to the districts, every band to be checked again. */
    private void sizeBands() {
        if (bandsSized == districts.size()) return;
        int n = bands();
        bandEpoch = new long[n];
        Arrays.fill(bandEpoch, Long.MIN_VALUE);
        bandPackWanted = new long[n];
        bandValidTo = new int[n];
        bandsSized = districts.size();
    }

    /** Every band to be checked again (a plan adopted). */
    private void bandsStale() {
        sizeBands();
        Arrays.fill(bandEpoch, Long.MIN_VALUE);
    }

    /** Band b's links walked in the map's order at the current epoch: its bandValidTo is the first whose own stamp moved, whose carry in is not the one before's carry on (none for the band's first), or (an edge district) that kept no free ground; and when all hold, the stamp of the packing they make. */
    private void checkBand(int b) {
        sitedNow();
        sizeBands();
        long e = planEpoch();
        if (b >= bandEpoch.length || e == bandEpoch[b]) return;
        bandEpoch[b] = e;
        int start = b * CHAIN_BAND, end = Math.min(districts.size(), start + CHAIN_BAND), valid = start;
        int[] carry = null;
        long ps = World.mix(0x5041434BL);
        for (int i = start; i < end; i++) {
            District d = districts.get(i);
            Link l = links.get(d);
            if (l == null || !Arrays.equals(l.in, carry) || l.own != ownStamp(d) || !readHolds(l)) break;
            boolean edge = isEdge(d);
            if (edge && l.free == null) break;
            if (edge) ps = World.mix(ps ^ l.stamp());
            carry = l.out;
            valid = i + 1;
        }
        bandValidTo[b] = valid;
        bandPackWanted[b] = valid == end ? stampOf(ps, carry) : 0;
    }

    /** Marks band b used, letting go of the bands used longest ago past BANDS_KEPT. */
    private void useBand(int b) {
        bandsUsed.put(b, Boolean.TRUE);
        while (bandsUsed.size() > BANDS_KEPT) {
            int old = bandsUsed.keySet().iterator().next();
            bandsUsed.remove(old);
            forgetBand(old);
        }
    }

    /** Lets go of band b's links, plans and packing: drawn again when a screen asks for it. */
    private void forgetBand(int b) {
        int start = b * CHAIN_BAND, end = Math.min(districts.size(), start + CHAIN_BAND);
        for (int i = start; i < end; i++) {
            District d = districts.get(i);
            links.remove(d);
            drawn.remove(d);
        }
        packs.remove(b);
        sizeBands();
        if (b < bandEpoch.length) bandEpoch[b] = Long.MIN_VALUE;
    }

    /** Whether every band's links hold and its packing is current: the whole city drawn. */
    private boolean allDrawn() {
        for (int b = 0; b < bands(); b++) {
            checkBand(b);
            Pack p = packs.get(b);
            if (bandValidTo[b] != Math.min(districts.size(), (b + 1) * CHAIN_BAND) || p == null || p.stamp != bandPackWanted[b]) return false;
        }
        return true;
    }

    /** District d's band's packing of what the band cannot hold, by tile, when current; null for none. */
    private int[][] packedIn(District d) {
        if (d == null) return null;
        int b = band(d.index);
        sizeBands();
        if (b >= bandPackWanted.length) return null;
        Pack p = packs.get(b);
        return p != null && p.stamp == bandPackWanted[b] ? p.boxes.get(d) : null;
    }

    /** Whether the plans a link read its neighbours' edges from are those standing: each such district's link the one it read (they are before it in the map's order, walked first). */
    private boolean readHolds(Link l) {
        if (l.earlier == null) return true;
        for (int k = 0; k < l.earlier.length; k++) {
            District s = l.earlier[k];
            if (s == null) continue;
            Link sl = links.get(s);
            if (sl == null || l.read == null || sl.stamp() != l.read[k]) return false;
        }
        return true;
    }

    /** Whether district d's plan is drawn and current, and every link before it in its band holds; for an edge district, its band's packing too: its tiles paint without planning. The screen's thread. */
    public boolean planned(District d) {
        if (d == null) return true;
        int b = band(d.index);
        checkBand(b);
        if (d.index >= bandValidTo[b]) return false;
        Drawn got = drawn.get(d);
        if (got == null || got.stamp != links.get(d).stamp()) return false;
        if (!isEdge(d)) return true;
        Pack p = packs.get(b);
        return p != null && p.stamp == bandPackWanted[b];
    }

    /** A job to run away from the screen's thread (0.7.88): run() on any one thread, in the order handed out, then adopt() on the map's. */
    public abstract static class Job {
        final CityMap map;
        /** The epoch it was handed out at (planEpoch()), and its place among every job the map handed out: a later job's inputs are the newer. */
        final long epoch, serial;
        private boolean done;
        Job(CityMap map, long epoch) { this.map = map; this.epoch = epoch; this.serial = ++map.jobsHanded; }
        /** Does the work: any thread, the jobs handed out in their order on one; safe to call twice. */
        public final synchronized void run() {
            if (done) return;
            work();
            done = true;
        }
        abstract void work();
        /** Whether it has run. */
        public final synchronized boolean done() { return done; }
    }

    /** One district's plan: its inputs gathered on the map's thread, its ground read and its plan drawn by run(), with what the job before it carried on (R7). */
    public static final class PlanJob extends Job {
        final District d;
        final DistrictPlan.Input in;
        final long own;
        final PlanJob after;
        final int[] given;
        final boolean edge;
        /** The districts about it before it in the map's order (earlierOf()), and each one's link or job: whose frames' edges it reads (readNeighbours()). */
        final District[] earlier;
        final Object[] sources;
        int[] carryIn, carryOut;
        Drawn result;
        long[] free, read;
        byte[][] margins;
        double ms;

        PlanJob(CityMap map, District d, DistrictPlan.Input in, long own, PlanJob after, int[] given, boolean edge,
                District[] earlier, Object[] sources, long epoch) {
            super(map, epoch);
            this.d = d; this.in = in; this.own = own; this.after = after; this.given = given; this.edge = edge;
            this.earlier = earlier; this.sources = sources;
        }

        @Override void work() {
            long t0 = System.nanoTime();
            if (after != null) after.run();
            int[] c = after != null ? after.carryOut : given;
            readGround(in);
            if (c != null) for (int t = 0; t < c.length && t < in.counts.length; t++) in.counts[t] += c[t];
            // What it reads of the districts before it about it (readNeighbours()), and the stamps of the plans it read.
            byte[][][] m = new byte[AROUND.length][][];
            read = new long[AROUND.length];
            for (int k = 0; k < AROUND.length; k++) {
                Object src = sources[k];
                if (src instanceof PlanJob j) {
                    j.run();
                    m[k] = j.margins;
                    read[k] = stampOf(j.own, j.carryIn);
                } else if (src instanceof Link l) {
                    m[k] = l.margins;
                    read[k] = l.stamp();
                }
            }
            readNeighbours(d, earlier, m, in);
            DistrictPlan p = DistrictPlan.make(in);
            carryIn = c;
            carryOut = p.overflowCount() > 0 ? p.overflow.clone() : null;
            result = drawnOf(p, stampOf(own, c));
            margins = marginsOf(p);
            if (edge) free = freeGround(in, p);
            ms = (System.nanoTime() - t0) / 1e6;
        }

        /** How long it took, in ms: a harness's figure. */
        public double ms() { return ms; }
    }

    /** A band's packing at its edge (R7; the city's, in a city of one band): what its last link carried on, into its edge districts' free ground - their links', or the jobs' that make them. */
    public static final class PackJob extends Job {
        long stamp;
        int band;
        final District[] edges;
        final Object[] from;
        final Object last;
        final BuildingVisual.Type[] types;
        final long[] tileOrder;
        Pack result;

        PackJob(CityMap map, District[] edges, Object[] from, Object last, long[] tileOrder, long epoch) {
            super(map, epoch);
            this.edges = edges; this.from = from; this.last = last; this.types = map.types; this.tileOrder = tileOrder;
        }

        @Override void work() {
            // Each edge district's free ground, copied (the packing takes plots of it), and the stamp its links make - as
            // checkBand() walks them, nearest first - with the carry the band's last district let go.
            long[][] free = new long[edges.length][];
            long[] stamps = new long[edges.length];
            for (int i = 0; i < edges.length; i++) {
                if (from[i] instanceof PlanJob j) {
                    j.run();
                    free[i] = j.free == null ? null : j.free.clone();
                    stamps[i] = stampOf(j.own, j.carryIn);
                } else {
                    Link l = (Link) from[i];
                    free[i] = l == null || l.free == null ? null : l.free.clone();
                    stamps[i] = l == null ? 0 : l.stamp();
                }
            }
            int[] p;
            if (last instanceof PlanJob j) { j.run(); p = j.carryOut; }
            else p = last == null ? null : ((Link) last).out;
            long ps = World.mix(0x5041434BL);
            for (int i = edges.length - 1; i >= 0; i--) ps = World.mix(ps ^ stamps[i]);
            stamp = stampOf(ps, p);
            result = packAtEdge(p, types, edges, free, tileOrder);
            result.stamp = stamp;
        }
    }

    /**
     * The jobs that make district d's plan current (R7: every district before
     * it in the map's order whose link does not hold, each after the one
     * before it), and, for an edge district, the city's packing after them:
     * to run in this order on one thread, then adopt(). A job already handed
     * out at this epoch is not handed out again. Empty when it is current.
     * The screen's thread.
     */
    public List<Job> planJobs(District d) {
        List<Job> out = new ArrayList<>();
        if (d == null || planned(d)) return out;
        useBand(band(d.index));
        chainTo(d.index, out);
        if (isEdge(d)) packAfter(band(d.index), out);
        return out;
    }

    /** The plan jobs up to the district at index i from its band's first, into out; the last job of the chain to it (handed out now or before), or null when its link holds. */
    private PlanJob chainTo(int i, List<Job> out) {
        int b = band(i), start = b * CHAIN_BAND;
        checkBand(b);
        long e = planEpoch();
        District di = districts.get(i);
        if (i < bandValidTo[b]) {
            Drawn got = drawn.get(di);
            if (got != null && got.stamp == links.get(di).stamp()) return null;
            PlanJob p = pending.get(di);
            if (p != null && p.epoch == e) return p;
            // Its link holds: only its plan was let go - drawn again with the carry its link came in with.
            District[] near = earlierOf(di);
            PlanJob j = new PlanJob(this, di, planInputHere(di), links.get(di).own, null, links.get(di).in, isEdge(di), near, sources(near), e);
            pending.put(di, j);
            out.add(j);
            return j;
        }
        PlanJob prev = null;
        for (int k = bandValidTo[b]; k <= i; k++) {
            District dk = districts.get(k);
            PlanJob p = pending.get(dk);
            if (p != null && p.epoch == e) { prev = p; continue; }
            int[] given = prev == null && k > start ? links.get(districts.get(k - 1)).out : null;
            District[] near = earlierOf(dk);
            PlanJob j = new PlanJob(this, dk, planInputHere(dk), ownStamp(dk), prev, given, isEdge(dk), near, sources(near), e);
            pending.put(dk, j);
            out.add(j);
            prev = j;
        }
        return prev;
    }

    /** Each earlier district's plan as a job reads it: its job handed out at this epoch, else its link (it is before the job's district in the map's order, so one or the other holds). */
    private Object[] sources(District[] earlier) {
        Object[] out = new Object[earlier.length];
        for (int k = 0; k < earlier.length; k++) {
            District s = earlier[k];
            if (s == null) continue;
            PlanJob p = pending.get(s);
            out[k] = p != null && p.epoch == planEpoch() ? p : links.get(s);
        }
        return out;
    }

    /** Band b's packing at its edge, after the chain to its last district, into out - unless it is current or handed out at this epoch. */
    private void packAfter(int b, List<Job> out) {
        checkBand(b);
        long e = planEpoch();
        int start = b * CHAIN_BAND, end = Math.min(districts.size(), start + CHAIN_BAND);
        Pack cur = packs.get(b);
        if (bandValidTo[b] == end && cur != null && cur.stamp == bandPackWanted[b]) return;
        PackJob was = packings.get(b);
        if (was != null && was.epoch == e) return;
        PlanJob tail = end <= start ? null : chainTo(end - 1, out);
        // Every edge district of the band, the farthest from the founding site first: its link's free ground, or its job's.
        List<District> edges = new ArrayList<>();
        for (int i = end - 1; i >= start; i--) if (isEdge(districts.get(i))) edges.add(districts.get(i));
        Object[] from = new Object[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            District d = edges.get(i);
            PlanJob p = pending.get(d);
            from[i] = p != null && p.epoch == e ? p : links.get(d);
        }
        Object last = end <= start ? null : tail != null ? tail : links.get(districts.get(end - 1));
        PackJob j = new PackJob(this, edges.toArray(new District[0]), from, last, tileOrder(edges), e);
        j.band = b;
        packings.put(b, j);
        out.add(j);
    }

    /** The edge districts' tiles in the order the packing spreads over them: each a district's index in `edges` and the tile, the farthest tile from the founding site first. */
    private long[] tileOrder(List<District> edges) {
        int n = edges.size() * TILES;
        long[] keys = new long[n];
        double[] dist = new double[n];
        Integer[] idx = new Integer[n];
        for (int i = 0; i < edges.size(); i++) {
            District d = edges.get(i);
            for (int k = 0; k < TILES; k++) {
                int at = i * TILES + k;
                double cx = ((baseDX + d.dx) * TILES_A_SIDE + k % TILES_A_SIDE) * World.TILE + World.TILE / 2.0 - siteX;
                double cy = ((baseDY + d.dy) * TILES_A_SIDE + k / TILES_A_SIDE) * World.TILE + World.TILE / 2.0 - siteY;
                dist[at] = cx * cx + cy * cy;
                keys[at] = ((long) i << 32) | k;
                idx[at] = at;
            }
        }
        Arrays.sort(idx, (a, b) -> dist[a] != dist[b] ? Double.compare(dist[b], dist[a]) : Long.compare(keys[a], keys[b]));
        long[] out = new long[n];
        for (int i = 0; i < n; i++) out[i] = keys[idx[i]];
        return out;
    }

    /** Keeps a job's work: a plan's link and its packed plan, the city's packing. False when it is another map's, or has not run. The screen's thread. */
    public boolean adopt(Job j) {
        if (j == null || j.map != this || !j.done()) return false;
        if (j instanceof PlanJob p) {
            if (pending.get(p.d) == p) pending.remove(p.d);
            Link was = links.get(p.d);
            if (was != null && was.serial > p.serial) return true;
            Link l = new Link(p.own, p.carryIn, p.carryOut, p.free, p.margins, p.earlier, p.read, p.result.surplus, p.serial);
            l.leftover = p.result.leftover.clone();
            links.put(p.d, l);
            drawn.put(p.d, p.result);
            bandsStale();
            return true;
        }
        PackJob k = (PackJob) j;
        if (packings.get(k.band) == k) packings.remove(k.band);
        checkBand(k.band);
        // Kept when every link of its band holds and they are the ones it packed into.
        int end = Math.min(districts.size(), (k.band + 1) * CHAIN_BAND);
        if (k.band < bandValidTo.length && bandValidTo[k.band] == end && k.stamp == bandPackWanted[k.band]) packs.put(k.band, k.result);
        return true;
    }

    /** District d's plan for the painter, made here when it is not current - every district before it in the map's order first where its link does not hold, and for an edge district the city's packing - on this thread: a harness's, a probe's, or the view's once its jobs have run. Null for no district. */
    Drawn drawnOf(District d) {
        if (d == null) return null;
        // Twice at most: an edge district's packing plans every district, which may let its own plan go from the cache.
        for (int round = 0; round < 2; round++) {
            for (Job j : planJobs(d)) {
                j.run();
                adopt(j);
            }
            Drawn got = drawn.get(d);
            if (got != null && planned(d)) return got;
        }
        return drawn.get(d);
    }

    /** Every band's packing at its edge, made here when it is not current (a harness's): the whole city drawn. */
    void packed() {
        for (int b = 0; b < bands(); b++) {
            List<Job> out = new ArrayList<>();
            packAfter(b, out);
            for (Job j : out) { j.run(); adopt(j); }
        }
    }

    /** The bands' packings summed: {packed, smaller, none}; null when one is not current. */
    private int[] packSums() {
        if (!allDrawn()) return null;
        int[] out = new int[3];
        for (int b = 0; b < bands(); b++) {
            Pack p = packs.get(b);
            out[0] += p.packed;
            out[1] += p.smaller;
            out[2] += p.none;
        }
        return out;
    }

    /** How many buildings the city has no room for in any district's plan, packed at its edge (R7; since 0.7.89 at its band's edge) - and how many of them drawn smaller than their own land or with no ground at all: {packed, smaller, none}. The legend's words read it. */
    public int[] packedCounts() {
        packed();
        int[] p = packSums();
        return p == null ? new int[3] : p;
    }

    /** ...the same, or null when the packing is not current: the screen's thread, which never plans. */
    public int[] packedCountsIfDrawn() {
        return packSums();
    }

    /** The legend's figures once every plan and the packing are drawn (never planned here: the screen's thread): {buildings packed at the city's edge, plots of road its streets do not carry (star RD2-4) - since 0.7.89 with the highway plots the runs could not lay -, plots of track the runs could not lay}; null until they are. */
    public long[] legendFiguresIfDrawn() {
        int[] p = packSums();
        if (p == null) return null;
        double surplus = runs.highwayShort();
        for (District d : districts) surplus += links.get(d).surplus;
        return new long[] { p[0], Math.round(surplus), runs.railShort() };
    }

    /** The road the plans have no street for, by kind [0, gravel, paved, highway], over every district (made now if need be), and (0.7.89) the Elevated Highway plots the runs could not lay: a harness's - with the surface drawn, the model's road kind by kind. */
    public double[] leftoverByKind() {
        packed();
        double[] out = new double[4];
        for (District d : districts) {
            Link l = links.get(d);
            if (l != null && l.leftover != null) for (int k = 0; k < 4; k++) out[k] += l.leftover[k];
        }
        out[BuildingVisual.HIGHWAY] += runs.highwayShort();
        return out;
    }

    /** ...the same, made now if need be: a harness's. */
    public long[] legendFigures() {
        packed();
        return legendFiguresIfDrawn();
    }

    /** District d's plan as drawn (made now if need be): a harness's and a probe's. */
    public Drawn drawn(District d) { return drawnOf(d); }

    /** What was carried to district d and what it carried on (R7), by type id, null for none - its link's, once planned. */
    public int[][] carried(District d) {
        drawnOf(d);
        Link l = links.get(d);
        return l == null ? new int[2][] : new int[][] { l.in, l.out };
    }

    /** The links kept (0.7.89: a band's let go past BANDS_KEPT): a harness's. */
    int linksKept() { return links.size(); }

    /** The bytes the kept plans hold: what MapTiles' budget counts. */
    public long plansBytes() {
        long b = 0;
        for (Drawn x : drawn.values()) b += x.bytes();
        return b;
    }

    /**
     * A plan's free ground (R7), for an edge district: a bit a plot of the
     * district's 256 x 256, set where the city owns dry ground no street,
     * building, highway or its verge (H5), track or mine's site takes - a
     * field's plots among it, built on last as the plan builds on them.
     */
    static long[] freeGround(DistrictPlan.Input in, DistrictPlan p) {
        int f = DistrictPlan.FRAME, words = DISTRICT / 64;
        long[] out = new long[DISTRICT * words];
        boolean[] taken = new boolean[DistrictPlan.AREA];
        for (int b = 0; b < p.buildings; b++) {
            for (int y = p.by[b]; y < p.by[b] + p.bh[b]; y++) for (int x = p.bx[b]; x < p.bx[b] + p.bw[b]; x++) taken[y * f + x] = true;
        }
        for (int y = 0; y < DISTRICT; y++) {
            for (int x = 0; x < DISTRICT; x++) {
                int q = y * f + x;
                byte t = in.terrain[q];
                if (!in.owned[q] || !(t == World.GRASS || t == World.FOREST || t == World.SAND)) continue;
                if (taken[q] || p.street[q] != 0 || in.fixed[q] != 0 || in.site[q] == DistrictPlan.SITE_MINED) continue;
                boolean verge = false;
                for (int dy = -1; dy <= 1 && !verge; dy++) {
                    for (int dx = -1; dx <= 1 && !verge; dx++) {
                        int ax = x + dx, ay = y + dy;
                        if (ax >= 0 && ay >= 0 && ax < f && ay < f && in.fixed[ay * f + ax] == DistrictPlan.FIXED_HIGHWAY) verge = true;
                    }
                }
                if (verge) continue;
                out[y * words + (x >>> 6)] |= 1L << (x & 63);
            }
        }
        return out;
    }

    /**
     * PACKED AT THE CITY'S EDGE (R7; spec 2.6: "a city with no room anywhere
     * packs them at its edge, without a street, and the map's legend says how
     * many"). What the last district carried on, largest first (the plans'
     * own order), each into the edge districts' free ground - on a tile's
     * interior, off its arterial lines, so it stands on one tile and blocks
     * no street the cells may yet lay - the tiles in turn from the farthest
     * from the founding site, each building on the next tile that holds it
     * whole from the one the last took: spread thin over the city's edge,
     * not a wall of them in one place (star RD2-2). One no free box holds
     * whole is drawn as the largest square any tile still has (0.7.64's rule
     * for a building no box held), at least a plot; with none left at all it
     * is counted, not drawn.
     */
    static Pack packAtEdge(int[] carry, BuildingVisual.Type[] types, District[] edges, long[][] free, long[] tileOrder) {
        Pack out = new Pack();
        if (carry == null) return out;
        int words = DISTRICT / 64;
        int[] ranked = BuildingVisual.rankedTypes(types);
        int nt = tileOrder.length;
        // Shapes each tile is known to hold no box of: free ground only shrinks.
        int[][] failW = new int[nt][], failH = new int[nt][];
        int[] fails = new int[nt];
        List<Integer>[][] boxes = new List[edges.length][TILES];
        int at = 0;
        for (int t : ranked) {
            if (t >= carry.length || carry[t] <= 0) continue;
            int[] fp = BuildingVisual.footprint(types[t]);
            for (int j = 0; j < carry[t]; j++) {
                out.packed++;
                int[] got = null;
                for (int m = 0; m < nt && got == null; m++) {
                    int ti = (at + m) % nt;
                    got = fitTile(free, tileOrder[ti], fp[0], fp[1], failW, failH, fails, ti);
                    if (got != null) at = (ti + 1) % nt;
                }
                if (got == null) {
                    // Smaller: the largest square some tile still holds, at least a plot.
                    for (int s = Math.min(INTERIOR_MOST, Math.max(fp[0], fp[1]) - 1); s >= 1 && got == null; s--) {
                        int w = Math.min(fp[0], s), h = Math.min(fp[1], s);
                        for (int m = 0; m < nt && got == null; m++) {
                            int ti = (at + m) % nt;
                            got = fitTile(free, tileOrder[ti], w, h, failW, failH, fails, ti);
                            if (got != null) at = (ti + 1) % nt;
                        }
                    }
                    if (got != null) out.smaller++;
                }
                if (got == null) { out.none++; continue; }
                int di = got[0], k = got[1];
                if (boxes[di][k] == null) boxes[di][k] = new ArrayList<>();
                boxes[di][k].add(box(got[2], got[3], got[4], got[5], true) | (t << BOX_TYPE_SHIFT));
                // Its plots taken.
                int x0 = (k % TILES_A_SIDE) * World.TILE + got[2], y0 = (k / TILES_A_SIDE) * World.TILE + got[3];
                for (int y = y0; y < y0 + got[5]; y++) for (int x = x0; x < x0 + got[4]; x++) free[di][y * words + (x >>> 6)] &= ~(1L << (x & 63));
            }
        }
        for (int i = 0; i < edges.length; i++) {
            int[][] per = null;
            for (int k = 0; k < TILES; k++) {
                if (boxes[i][k] == null) continue;
                if (per == null) per = new int[TILES][];
                per[k] = new int[boxes[i][k].size()];
                for (int b = 0; b < per[k].length; b++) per[k][b] = boxes[i][k].get(b);
            }
            if (per != null) out.boxes.put(edges[i], per);
        }
        return out;
    }

    /** A tile's interior holds a box no larger than this a side: 31 plots, off its arterial lines. */
    static final int INTERIOR_MOST = World.TILE - 1;

    /** The first spot, in rows, on tile key (edge index << 32 | tile) of the free ground where a w x h box - or h x w - fits inside the tile's interior: {edge index, tile, x, y, w, h} in the tile's plots, or null (and the shape remembered as not held). */
    private static int[] fitTile(long[][] free, long key, int w, int h, int[][] failW, int[][] failH, int[] fails, int ti) {
        for (int f = 0; f < fails[ti]; f++) {
            int a = failW[ti][f], b = failH[ti][f];
            if ((w >= a && h >= b) || (w >= b && h >= a)) return null;
        }
        int di = (int) (key >>> 32), k = (int) key, words = DISTRICT / 64;
        int bx = (k % TILES_A_SIDE) * World.TILE, by = (k / TILES_A_SIDE) * World.TILE;
        long[] g = free[di];
        if (g != null) {
            for (int turn = 0; turn < 2; turn++) {
                int ww = turn == 0 ? w : h, hh = turn == 0 ? h : w;
                if (turn == 1 && w == h) break;
                if (ww > INTERIOR_MOST || hh > INTERIOR_MOST) continue;
                for (int y = 1; y + hh <= World.TILE; y++) {
                    for (int x = 1; x + ww <= World.TILE; x++) {
                        boolean fits = true;
                        for (int yy = y; yy < y + hh && fits; yy++) {
                            int row = (by + yy) * words;
                            for (int xx = x; xx < x + ww && fits; xx++) {
                                int ax = bx + xx;
                                fits = (g[row + (ax >>> 6)] & (1L << (ax & 63))) != 0;
                            }
                        }
                        if (fits) return new int[] { di, k, x, y, ww, hh };
                    }
                }
            }
        }
        if (failW[ti] == null) { failW[ti] = new int[4]; failH[ti] = new int[4]; }
        if (fails[ti] == failW[ti].length) { failW[ti] = Arrays.copyOf(failW[ti], fails[ti] * 2); failH[ti] = Arrays.copyOf(failH[ti], fails[ti] * 2); }
        failW[ti][fails[ti]] = w;
        failH[ti][fails[ti]] = h;
        fails[ti]++;
        return null;
    }

    /* =====================================================================
       THE RUNS: THE CITY'S HIGHWAYS AND RAILWAY ON CORRIDORS (0.7.89, batch
       RD3; the project's spec-roads-and-ports.md 2.7 and 2.8)

       From 0.7.72 to 0.7.88 each district laid its own highways and track
       from its first road tile's hub (THE NETWORK, its road tiles, hubs and
       main streets worked out since 0.7.88 for that alone): a city's six
       highways were six stubs, each crossing itself, and at 0.7.88 a short
       highway floated inside a block of Jerus's city. Since 0.7.89 they are
       the city's RUNS (CityRuns): laid city-wide, month by month, on
       corridors from the founding site's lines - the railway's from the
       city's mine nearest the founding site - straight by preference, never
       moved, the newest end taken first; kept here and in the sidecar
       (FORMAT 5). Each district's plan is drawn round them (their plots
       fixed, a verge either side of a highway: H5) and the painter draws them
       plot for plot. A Rail Terminal is the railway's YARD, on its track in
       the cell nearest a mine; one the runs find no place for is its
       district's plan's to draw. What the runs cannot lay - no ground left
       for a corridor - is counted, and the legend says so.
       ===================================================================== */

    /** Bumped whenever free ground is measured again (a purchase): what a run waiting on the ground is tried again on. */
    private long landVersion;

    /** The city's highways and railway (spec 2.7, 2.8). */
    private CityRuns runs = new CityRuns();

    /** The city's runs. */
    public CityRuns runs() { return runs; }

    /** Terrain lines (a tile's row or column, World.lineTerrain()) the runs' ground keeps: 4,096, about 460 KB - a corridor's way ahead and back, at any size. */
    static final int RUN_LINES_KEPT = 4096;

    /** ...whole tiles, for a 45-degree way: 256. */
    static final int RUN_TILES_KEPT = 256;

    /**
     * The ground the runs read (CityRuns.Ground): a plot's World class where
     * the city owns it, -1 where it does not - the terrain a tile's row or
     * column at a time (World.lineTerrain(), byte for byte the painter's
     * tileTerrain()), a 45-degree way a whole tile at a time, ownership a tile
     * at a time from the grid (tileOwnership()).
     */
    private final class RunGround implements CityRuns.Ground {
        private final Map<Long, byte[]> lines = new LinkedHashMap<>(RUN_LINES_KEPT * 2, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<Long, byte[]> e) { return size() > RUN_LINES_KEPT; }
        };
        private final Map<Long, byte[]> tiles = new LinkedHashMap<>(RUN_TILES_KEPT * 2, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<Long, byte[]> e) { return size() > RUN_TILES_KEPT; }
        };
        private final Map<Long, boolean[]> owned = new LinkedHashMap<>(RUN_TILES_KEPT * 2, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<Long, boolean[]> e) { return size() > RUN_TILES_KEPT; }
        };
        private long ownedAt = Long.MIN_VALUE, lastLineKey = Long.MIN_VALUE, lastOwnKey = Long.MIN_VALUE;
        private byte[] lastLine;
        private boolean[] lastOwn;
        private int lastCover;
        private final boolean[] ALL_OWNED = new boolean[0], NONE_OWNED = new boolean[0];

        @Override public int at(long x, long y, int dir) {
            if (x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE) return -1;
            long tx = x >> 5, ty = y >> 5;
            int ix = (int) (x & (World.TILE - 1)), iy = (int) (y & (World.TILE - 1));
            if (!ownedAt(tx, ty, ix, iy)) return -1;
            long tk = (tx << 20) | ty;
            if (dir < 0 || (dir & 1) == 1) {
                byte[] t = tiles.get(tk);
                if (t == null) {
                    t = new byte[TilePainter.PLOTS];
                    World.of(seed).tileTerrain(tx, ty, t);
                    tiles.put(tk, t);
                }
                return t[iy * World.TILE + ix];
            }
            boolean col = dir == 2 || dir == 6;
            int at = col ? ix : iy;
            long key = (tk << 6) | ((long) at << 1) | (col ? 1 : 0);
            if (key != lastLineKey) {
                byte[] t = tiles.get(tk);
                byte[] line = lines.get(key);
                if (line == null) {
                    line = new byte[World.TILE];
                    if (t != null) {
                        for (int i = 0; i < World.TILE; i++) line[i] = t[col ? i * World.TILE + at : at * World.TILE + i];
                    } else World.of(seed).lineTerrain(tx, ty, col, at, line);
                    lines.put(key, line);
                }
                lastLineKey = key;
                lastLine = line;
            }
            return lastLine[col ? iy : ix];
        }

        /** Whether the city owns plot (ix, iy) of tile (tx, ty): the grid's cover of the tile, its leaves where it owns some. */
        private boolean ownedAt(long tx, long ty, int ix, int iy) {
            long v = version();
            if (v != ownedAt) { owned.clear(); ownedAt = v; lastOwnKey = Long.MIN_VALUE; }
            long tk = (tx << 20) | ty;
            if (tk != lastOwnKey) {
                boolean[] o = owned.get(tk);
                if (o == null) {
                    int c = cover(land.grid().cover(5, tx, ty));
                    if (c == ALL) o = ALL_OWNED;
                    else if (c == NONE) o = NONE_OWNED;
                    else {
                        o = new boolean[TilePainter.PLOTS];
                        land.grid().tileFlags(tx, ty, o);
                    }
                    owned.put(tk, o);
                }
                lastOwnKey = tk;
                lastOwn = o;
            }
            if (lastOwn == ALL_OWNED) return true;
            if (lastOwn == NONE_OWNED) return false;
            return lastOwn[iy * World.TILE + ix];
        }

        @Override public long version() {
            return World.mix(landVersion ^ ((long) land.purchases().size() << 32) ^ land.centreStamp());
        }

        @Override public long[] box() {
            LandGrid g = land.grid();
            if (g.ownedPlots() == 0) return new long[] { siteX, siteY, siteX, siteY };
            return new long[] { g.minX(), g.minY(), g.maxX() - 1, g.maxY() - 1 };
        }
    }

    private final RunGround runGround = new RunGround();

    /** The ground the runs read: a harness's, to lay runs of its own on the city's ground. */
    CityRuns.Ground runGround() { return runGround; }

    /** The founding site, {x, y} in plots. */
    long[] site() { return new long[] { siteX, siteY }; }

    /**
     * Lays the runs to the model's counts (CityRuns.layTo()): its Elevated
     * Highways' plots, its track's, its Rail Terminals as yards - the
     * highways from the founding site, the railway from the city's mine
     * nearest it (the founding site with none). True, and the map's changes
     * bumped, when they moved.
     */
    boolean layRuns(long[] model) {
        long highway = 0, track = 0;
        int yards = 0, yw = 0, yh = 0;
        for (int t = 0; t < types.length && t < model.length; t++) {
            if (types[t] == null || model[t] <= 0) continue;
            if (types[t].road() == BuildingVisual.HIGHWAY) highway += model[t] * BuildingVisual.cells(types[t]);
            else if (types[t].track()) track += model[t] * BuildingVisual.cells(types[t]);
            else if (types[t].terminal()) {
                yards += (int) Math.min(Integer.MAX_VALUE, model[t]);
                int[] fp = BuildingVisual.footprint(types[t]);
                yw = fp[0];
                yh = fp[1];
            }
        }
        CityRuns.Net rail = runs.rail();
        boolean needMines = (track > 0 || yards > 0) && (!rail.started || yards != runs.yards().size());
        List<long[]> mines = needMines ? minedSites() : new ArrayList<>();
        long rx = siteX, ry = siteY;
        double best = Double.MAX_VALUE;
        for (long[] s : mines) {
            double cx = (s[0] + s[2] + 1) / 2.0, cy = (s[1] + s[3] + 1) / 2.0, d = (cx - siteX) * (cx - siteX) + (cy - siteY) * (cy - siteY);
            if (d < best) { best = d; rx = (long) Math.floor(cx); ry = (long) Math.floor(cy); }
        }
        boolean moved = runs.layTo(highway, track, yards, yw, yh, runGround, siteX, siteY, rx, ry, mines);
        if (moved) {
            changes++;
            yardShare = null;
        }
        return moved;
    }

    /** The city's mines and wells standing on their sites, each {x0, y0, x1, y1} inclusive: where the railway starts and its yards are drawn near. */
    List<long[]> minedSites() {
        sitedNow();
        List<long[]> out = new ArrayList<>();
        for (District d : districts) {
            if (d.sited == null) continue;
            boolean any = false;
            for (int c : d.sited) any |= c > 0;
            if (!any) continue;
            for (DrawnSite s : siteList(d.dx, d.dy)) if (s.mine() >= 0) out.add(new long[] { s.x0(), s.y0(), s.x1(), s.y1() });
        }
        return out;
    }

    /** Each district's Rail Terminals the runs draw as yards, by its index: the yards dealt to the districts holding terminals in the map's order, so the rest are their plans' (kept against the runs' version). */
    private int[] yardShare;
    private long yardShareAt = Long.MIN_VALUE;

    /** How many of district d's Rail Terminals are the runs' yards. */
    int yardsOf(District d) {
        long at = World.mix(runs.version() ^ changes);
        if (yardShare == null || yardShareAt != at || yardShare.length != districts.size()) {
            yardShare = new int[districts.size()];
            yardShareAt = at;
            int left = runs.yards().size();
            for (int i = 0; i < districts.size() && left > 0; i++) {
                District o = districts.get(i);
                int n = 0;
                for (int t = 0; t < types.length && t < o.counts.length; t++) if (types[t] != null && types[t].terminal()) n += o.counts[t];
                int take = Math.min(n, left);
                yardShare[i] = take;
                left -= take;
            }
        }
        return d.index < yardShare.length ? yardShare[d.index] : 0;
    }

    /** The runs' frame for district d's plan: their plots over its 257 x 257 (DistrictPlan's codes), a hash of them returned. */
    long runsInto(District d, byte[] fixed) {
        long x0 = (baseDX + d.dx) * DISTRICT, y0 = (baseDY + d.dy) * DISTRICT;
        int f = DistrictPlan.FRAME;
        long h = runs.fill(x0, y0, f, f, fixed, null, true);
        for (int i = 0; i < f * f; i++) {
            byte b = fixed[i];
            if (b == CityRuns.F_RAIL_OVER) fixed[i] = DistrictPlan.FIXED_HIGHWAY;
            else if (b == CityRuns.F_YARD) fixed[i] = DistrictPlan.FIXED_YARD;
        }
        return h;
    }

    /* =====================================================================
       A TILE'S INPUTS FOR THE PAINTER
       ===================================================================== */

    /** The district holding tile (tx, ty), or null. */
    public District districtOfTile(long tx, long ty) {
        return byKey.get(key((int) (Math.floorDiv(tx, TILES_A_SIDE) - baseDX), (int) (Math.floorDiv(ty, TILES_A_SIDE) - baseDY)));
    }

    /** The tile in district d at its column and row of tiles (tile = row x 8 + column), from world tile (tx, ty). */
    static int tileIn(long tx, long ty) {
        return (int) (Math.floorMod(ty, TILES_A_SIDE) * TILES_A_SIDE + Math.floorMod(tx, TILES_A_SIDE));
    }

    /** A tile's buildings by type id as its district's plan draws them (0.7.88; the deal's until 0.7.87), packed ones among them: into out, zeros where no district is. */
    public void tileCounts(long tx, long ty, int[] out) {
        Arrays.fill(out, 0);
        District d = districtOfTile(tx, ty);
        if (d == null) return;
        Drawn dr = drawnOf(d);
        int k = tileIn(tx, ty);
        if (dr != null && dr.boxes[k] != null) for (int b : dr.boxes[k]) if (b >>> BOX_TYPE_SHIFT < out.length) out[b >>> BOX_TYPE_SHIFT]++;
        int[][] pk = packedIn(d);
        if (pk != null && pk[k] != null) for (int b : pk[k]) if (b >>> BOX_TYPE_SHIFT < out.length) out[b >>> BOX_TYPE_SHIFT]++;
    }

    /** Which of a tile's plots the city owns: a tile owned wholly or not at all in one test (the grid's cover at level 5), the rest from the grid's leaves under it (spec-grid 2.4). */
    public void tileOwnership(long tx, long ty, boolean[] out) {
        int own = cover(land.grid().cover(5, tx, ty));
        if (own != SOME) {
            Arrays.fill(out, 0, TilePainter.PLOTS, own == ALL);
            return;
        }
        land.grid().tileFlags(tx, ty, out);
    }

    /**
     * Everything the painter needs for tile (tx, ty), into in: its ground,
     * what the city owns of it, and from its district's plan (0.7.88; the
     * deal's counts and road plots until 0.7.87) the streets through it and
     * every building's box on it, the city's highways and railway through it,
     * and the sites on it with their states and the mines standing on them.
     * Its plan is made here when it is not current (drawnOf()): the view asks
     * only for a tile whose plans are (tileReady()). Since 0.7.64 nothing is
     * drawn that the model does not have.
     */
    public void tileInput(long tx, long ty, TilePainter.Input in) {
        World.of(seed).tileTerrain(tx, ty, in.terrain);
        fillInput(tx, ty, in);
    }

    /** ...with the tile's ground given (a view keeps it: MapTiles.terrain(), batch J4) rather than read from the world. */
    public void tileInput(long tx, long ty, TilePainter.Input in, byte[] terrain) {
        System.arraycopy(terrain, 0, in.terrain, 0, TilePainter.PLOTS);
        fillInput(tx, ty, in);
    }

    /** The districts a tile's picture is drawn from (0.7.88): those of the tiles about it - its own, the ones whose plans lay the street on its first column or row (west, north, north-west) and those its edges look across to (the streets and runs just beyond it). */
    public List<District> tileDistricts(long tx, long ty) {
        List<District> out = new ArrayList<>(4);
        for (int b = -1; b <= 1; b++) {
            for (int a = -1; a <= 1; a++) {
                District d = districtOfTile(tx + a, ty + b);
                if (d != null && !out.contains(d)) out.add(d);
            }
        }
        return out;
    }

    /** Whether tile (tx, ty) paints without planning: every plan it is drawn from current (planned()). The screen's thread. */
    public boolean tileReady(long tx, long ty) {
        for (District d : tileDistricts(tx, ty)) if (!planned(d)) return false;
        return true;
    }

    /** The jobs that make tile (tx, ty)'s plans current (planJobs()), in order; empty when they are. The screen's thread. */
    public List<Job> tileJobs(long tx, long ty) {
        List<Job> out = new ArrayList<>();
        for (District d : tileDistricts(tx, ty)) out.addAll(planJobs(d));
        return out;
    }

    /**
     * The street a plan draws on world plot (x, y) (TilePainter's S_ codes):
     * the district's own; on its first column or row the district west or
     * north of it (or north-west, at its corner) lays the same plot as its
     * frame's margin - a SEAM (DistrictPlan.SEAMS) where the other surfaces
     * it - so the surfaced code wins; a seam laid by a district that does not
     * surface it, with no surfaced street under it, is a track (star RD1-1).
     */
    byte streetAt(long x, long y) {
        return streetAt(x, y, new HashMap<>());
    }

    /** ...the plans looked up through `seen`, by district key (null for none), which it fills. */
    private byte streetAt(long x, long y, Map<Long, Drawn> seen) {
        long dxl = Math.floorDiv(x, DISTRICT), dyl = Math.floorDiv(y, DISTRICT);
        int lx = (int) (x - dxl * DISTRICT), ly = (int) (y - dyl * DISTRICT);
        int ddx = (int) (dxl - baseDX), ddy = (int) (dyl - baseDY);
        byte best = 0;
        boolean seam = false;
        for (int c = 0; c < 4; c++) {
            int ox = c == 1 || c == 3 ? -1 : 0, oy = c >= 2 ? -1 : 0;
            if ((ox < 0 && lx != 0) || (oy < 0 && ly != 0)) continue;
            long dk = key(ddx + ox, ddy + oy);
            Drawn dr;
            if (seen.containsKey(dk)) dr = seen.get(dk);
            else {
                District d = byKey.get(dk);
                dr = d == null ? null : drawnOf(d);
                seen.put(dk, dr);
            }
            if (dr == null) continue;
            byte b;
            if (ox == 0 && oy == 0) b = dr.codeAt(lx, ly);
            else if (oy == 0) b = dr.east == null ? 0 : dr.east[ly];
            else if (ox == 0) b = dr.south == null ? 0 : dr.south[lx];
            else b = dr.east == null ? 0 : dr.east[DISTRICT];
            int kind = b & TilePainter.S_KIND;
            if (kind == DistrictPlan.NONE) continue;
            if (kind == DistrictPlan.SEAM) { seam = true; if (best == 0) best = b; continue; }
            if (kind == DistrictPlan.UNDER) { if (best == 0 || (best & TilePainter.S_KIND) == DistrictPlan.SEAM) best = b; continue; }
            return b;
        }
        if (seam && (best & TilePainter.S_KIND) == DistrictPlan.SEAM) return (byte) ((best & ~TilePainter.S_KIND & ~(3 << TilePainter.S_WIDTH_SHIFT)) | DistrictPlan.TRACK);
        return best;
    }

    /** Everything tileInput() fills but the ground, which is in `in` already. */
    private void fillInput(long tx, long ty, TilePainter.Input in) {
        in.seed = seed;
        in.tx = tx;
        in.ty = ty;
        in.types = types;
        tileOwnership(tx, ty, in.owned);
        if (in.counts.length != types.length) in.counts = new int[types.length];
        in.clearBuildings();
        java.util.Arrays.fill(in.street, (byte) 0);
        java.util.Arrays.fill(in.fixed, (byte) 0);
        District d = districtOfTile(tx, ty);
        int k = tileIn(tx, ty);
        long px0 = tx * World.TILE, py0 = ty * World.TILE;
        // Its streets: its district's plan's, and on its first column and row its neighbours' where they lay them.
        Map<Long, Drawn> seen = new HashMap<>();
        Drawn own = d == null ? null : drawnOf(d);
        if (d != null) seen.put(d.key, own);
        boolean firstCol = Math.floorMod(tx, TILES_A_SIDE) == 0, firstRow = Math.floorMod(ty, TILES_A_SIDE) == 0;
        if (own != null && own.tiles[k] != null) Drawn.unpack(own.tiles[k], in.street);
        if (firstCol || firstRow || own == null) {
            for (int y = 0; y < World.TILE; y++) {
                for (int x = 0; x < World.TILE; x++) {
                    if (!(firstCol && x == 0) && !(firstRow && y == 0) && own != null) continue;
                    in.street[y * World.TILE + x] = streetAt(px0 + x, py0 + y, seen);
                }
            }
        }
        // ...and just beyond its edges, north, east, south and west.
        for (int m = 0; m < World.TILE; m++) {
            in.beyond[m] = streetAt(px0 + m, py0 - 1, seen);
            in.beyond[World.TILE + m] = streetAt(px0 + World.TILE, py0 + m, seen);
            in.beyond[2 * World.TILE + m] = streetAt(px0 + m, py0 + World.TILE, seen);
            in.beyond[3 * World.TILE + m] = streetAt(px0 - 1, py0 + m, seen);
        }
        // The city's highways and track through it and the ring about it (0.7.89: the city's runs; THE NETWORK's plan's until
        // 0.7.88), their ramps and 45-degree stretches; its yards drawn over their track.
        int rw = World.TILE + 2;
        byte[] ringFixed = new byte[rw * rw], ringMarks = new byte[rw * rw];
        {
            runs.fill(px0 - 1, py0 - 1, rw, rw, ringFixed, ringMarks, false);
            for (int y = 0; y < World.TILE; y++) {
                for (int x = 0; x < World.TILE; x++) {
                    in.fixed[y * World.TILE + x] = ringFixed[(y + 1) * rw + x + 1];
                    in.marks[y * World.TILE + x] = ringMarks[(y + 1) * rw + x + 1];
                }
            }
            for (int m = 0; m < World.TILE; m++) {
                in.fixedBeyond[m] = ringFixed[m + 1];
                in.fixedBeyond[World.TILE + m] = ringFixed[(m + 1) * rw + rw - 1];
                in.fixedBeyond[2 * World.TILE + m] = ringFixed[(rw - 1) * rw + m + 1];
                in.fixedBeyond[3 * World.TILE + m] = ringFixed[(m + 1) * rw];
                in.marksBeyond[m] = ringMarks[m + 1];
                in.marksBeyond[World.TILE + m] = ringMarks[(m + 1) * rw + rw - 1];
                in.marksBeyond[2 * World.TILE + m] = ringMarks[(rw - 1) * rw + m + 1];
                in.marksBeyond[3 * World.TILE + m] = ringMarks[(m + 1) * rw];
            }
        }
        // Its buildings: its yards (0.7.89), its plan's boxes, and those packed at the city's edge (R7).
        int boxBits = (1 << BOX_TYPE_SHIFT) - 1;
        int terminal = -1;
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].terminal()) { terminal = t; break; }
        for (long[] yd : runs.yards()) {
            if (terminal < 0 || yd[0] < px0 || yd[1] < py0 || yd[0] >= px0 + World.TILE || yd[1] >= py0 + World.TILE) continue;
            in.addBuilding(box((int) (yd[0] - px0), (int) (yd[1] - py0), (int) yd[2], (int) yd[3], false), terminal);
        }
        if (own != null && own.boxes[k] != null) for (int b : own.boxes[k]) in.addBuilding(b & boxBits, b >>> BOX_TYPE_SHIFT);
        int[][] pk = packedIn(d);
        if (pk != null && pk[k] != null) for (int b : pk[k]) in.addBuilding(b & boxBits, b >>> BOX_TYPE_SHIFT);
        in.clearSites();
        int ddx = (int) (Math.floorDiv(tx, TILES_A_SIDE) - baseDX), ddy = (int) (Math.floorDiv(ty, TILES_A_SIDE) - baseDY);
        for (int b = -1; b <= 1; b++) {
            for (int a = -1; a <= 1; a++) {
                for (DrawnSite s : siteList(ddx + a, ddy + b)) {
                    if (s.x1 < px0 || s.x0 >= px0 + World.TILE || s.y1 < py0 || s.y0 >= py0 + World.TILE) continue;
                    in.addSite((int) (s.x0 - px0), (int) (s.y0 - py0), (int) (s.x1 - px0), (int) (s.y1 - py0),
                            s.kind, s.state, s.mine, (s.x0 << 32) | (s.y0 & 0xffffffffL));
                }
            }
        }
    }

    /** One site as the map draws it: its square of plots (inclusive), resource, state and the mine on it (-1 for none). */
    record DrawnSite(long x0, long y0, long x1, long y1, int kind, int state, int mine) { }

    private final Map<Long, List<DrawnSite>> siteLists = new LinkedHashMap<>(SITE_LISTS_KEPT * 2, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, List<DrawnSite>> e) { return size() > SITE_LISTS_KEPT; }
    };
    private final Map<Long, Long> siteListVersion = new HashMap<>();

    /**
     * The sites whose centres lie in district (dx, dy) - every resource's,
     * owned or not - each with its holding's state (its field's holding, the
     * one holding the field's centre, since 0.7.64) and, for the SITED
     * resources, the mine on it: in each holding, the first of its sites here
     * (in field order) carry the mines the district holds from that holding.
     */
    List<DrawnSite> siteList(int dx, int dy) {
        long k = key(dx, dy);
        District d = byKey.get(k);
        long version = d == null ? -1 : d.version;
        List<DrawnSite> got = siteLists.get(k);
        Long seen = siteListVersion.get(k);
        if (got != null && seen != null && seen == version) return got;
        if (d != null && !sitedPlaced) placeSited(totals());
        long x0 = (baseDX + dx) * DISTRICT, y0 = (baseDY + dy) * DISTRICT;
        List<DrawnSite> out = new ArrayList<>();
        Map<Resource, int[]> states = new HashMap<>();
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            int slot = siteSlot(r);
            int[][] pairs = d == null || slot < 0 ? null : new int[][] { d.holdingMines[slot] };
            int[] left = new int[1 + land.purchases().size()];
            if (pairs != null && pairs[0] != null) for (int a = 0; a + 1 < pairs[0].length; a += 2) left[pairs[0][a]] += pairs[0][a + 1];
            int mineType = -1;
            for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].site() == r) { mineType = t; break; }
            double w = Deposit.siteWidth(r);
            for (Deposit f : fieldsNear(r, x0, y0, x0 + DISTRICT - 1, y0 + DISTRICT - 1)) {
                boolean legacy = f.cell() < 0;
                for (int s = 0; s < f.sites(); s++) {
                    double[] at = f.siteAt(s);
                    double cx = f.x() + at[0], cy = f.y() + at[1];
                    if (Math.floorDiv((long) Math.floor(cx), DISTRICT) != baseDX + dx
                            || Math.floorDiv((long) Math.floor(cy), DISTRICT) != baseDY + dy) continue;
                    int h = land.siteHolding(f, s);
                    int state = TilePainter.UNOWNED;
                    if (h >= 0) {
                        int[] st = states.computeIfAbsent(r, this::holdingStates);
                        state = h < st.length ? st[h] : TilePainter.UNWORKED;
                    }
                    int mine = -1;
                    if (h >= 0 && h < left.length && left[h] > 0 && mineType >= 0) {
                        mine = mineType;
                        left[h]--;
                    }
                    long sx0 = Math.round(cx - w / 2), sy0 = Math.round(cy - w / 2);
                    long sx1 = Math.round(cx + w / 2) - 1, sy1 = Math.round(cy + w / 2) - 1;
                    out.add(new DrawnSite(sx0, sy0, Math.max(sx0, sx1), Math.max(sy0, sy1), r.ordinal(), state, mine));
                }
            }
        }
        siteLists.put(k, out);
        siteListVersion.put(k, version);
        siteListVersion.keySet().retainAll(siteLists.keySet());
        return out;
    }

    /** Forgets the painted state of every district - the plans, their links, the packing and the site lists - after the ground's states moved (what is worked out). The runs stand: they are laid, not painted. */
    public void forgetPainted() {
        drawn.clear();
        links.clear();
        pending.clear();
        packs.clear();
        packings.clear();
        bandsUsed.clear();
        bandsStale();
        siteLists.clear();
        siteListVersion.clear();
    }

    /* =====================================================================
       THE DISTRICT PLAN'S INPUTS (0.7.87, batch RD1; the project's
       spec-roads-and-ports.md 2.9)

       What DistrictPlan draws a district from: the ground and what the city
       owns of it over the plan's frame (the district and the first column
       and row of its east and south neighbours), its buildings by type - its
       mines and wells on sites apart, which stand on them - and its road
       plots by kind, the resource sites, which seams it surfaces (the
       district first in the map's order does: DISTRICT_ORDER), and the
       city's highway and railway plots through it: since 0.7.89 the city's
       runs' (THE RUNS), every Elevated Highway plot theirs and none a
       street's surface - what they cannot lay is counted - where 0.7.88's
       NETWORK left the plots it did not lay to the streets' surface.
       Since 0.7.88 the painter draws from these plans (THE DRAWN PLANS),
       each with what the districts before it could not hold (R7).
       ===================================================================== */

    /** District d's own plan, drawn now from planInput(d) - its own buildings alone, none carried to it (R7): not kept (THE DRAWN PLANS keep the painter's). */
    DistrictPlan plan(District d) {
        return DistrictPlan.make(planInput(d));
    }

    /** The inputs of district d's plan (DistrictPlan.Input), its ground read from the world. */
    DistrictPlan.Input planInput(District d) {
        DistrictPlan.Input in = planInputHere(d);
        readGround(in);
        return in;
    }

    /** The ground over a plan's frame, a World class a plot, read from the world: any thread (World is safe), so a plan's job reads it away from the screen's (0.7.88). */
    static void readGround(DistrictPlan.Input in) {
        int f = DistrictPlan.FRAME;
        byte[] ter = new byte[TilePainter.PLOTS];
        int tiles = (f + World.TILE - 1) / World.TILE;
        World w = World.of(in.seed);
        for (int j = 0; j < tiles; j++) {
            for (int i = 0; i < tiles; i++) {
                long tx = Math.floorDiv(in.x0, World.TILE) + i, ty = Math.floorDiv(in.y0, World.TILE) + j;
                w.tileTerrain(tx, ty, ter);
                for (int y = 0; y < World.TILE && j * World.TILE + y < f; y++) {
                    System.arraycopy(ter, y * World.TILE, in.terrain, (j * World.TILE + y) * f + i * World.TILE, Math.min(World.TILE, f - i * World.TILE));
                }
            }
        }
    }

    /** ...everything but the ground: what the map's own thread reads (the land, the counts, the sites, the runs). */
    DistrictPlan.Input planInputHere(District d) {
        sitedNow();
        DistrictPlan.Input in = new DistrictPlan.Input();
        int f = DistrictPlan.FRAME;
        long x0 = (baseDX + d.dx) * DISTRICT, y0 = (baseDY + d.dy) * DISTRICT;
        in.seed = seed;
        in.x0 = x0;
        in.y0 = y0;
        in.hubX = siteX - x0;
        in.hubY = siteY - y0;
        in.types = types;
        // What the city owns, tile by tile over the frame (its ground is readGround()'s).
        boolean[] own = new boolean[TilePainter.PLOTS];
        int tiles = (f + World.TILE - 1) / World.TILE;
        for (int j = 0; j < tiles; j++) {
            for (int i = 0; i < tiles; i++) {
                long tx = Math.floorDiv(x0, World.TILE) + i, ty = Math.floorDiv(y0, World.TILE) + j;
                tileOwnership(tx, ty, own);
                for (int y = 0; y < World.TILE && j * World.TILE + y < f; y++) {
                    System.arraycopy(own, y * World.TILE, in.owned, (j * World.TILE + y) * f + i * World.TILE, Math.min(World.TILE, f - i * World.TILE));
                }
            }
        }
        // Its buildings, its mines and wells on sites apart; its road plots by kind.
        in.counts = new int[types.length];
        long[] kind = new long[4];
        for (int t = 0; t < types.length && t < d.counts.length; t++) {
            if (types[t] == null || d.counts[t] <= 0) continue;
            if (types[t].road() != BuildingVisual.NOT_A_ROAD) { kind[types[t].road()] += (long) d.counts[t] * BuildingVisual.cells(types[t]); continue; }
            if (!types[t].drawn()) continue;
            in.counts[t] = Math.max(0, d.counts[t] - (d.sited == null ? 0 : d.sited[t]));
        }
        // ...less its Rail Terminals the runs draw as yards (0.7.89).
        int yards = yardsOf(d);
        for (int t = 0; t < types.length && yards > 0 && t < in.counts.length; t++) {
            if (types[t] == null || !types[t].terminal()) continue;
            int take = Math.min(yards, in.counts[t]);
            in.counts[t] -= take;
            yards -= take;
        }
        // The city's highways and railway through its frame (0.7.89: the city's runs; THE NETWORK's plan's until 0.7.88), its yards
        // among them. The model's Elevated Highways are the runs' plots, none a street's surface: what the runs could not lay the
        // map counts (THE RUNS).
        runsInto(d, in.fixed);
        in.gravel = kind[BuildingVisual.GRAVEL];
        in.paved = kind[BuildingVisual.PAVED];
        in.highway = 0;
        // The resource sites over the frame: a mine's or well's its own, the rest fields; the district's own mines reached.
        for (int b = -1; b <= 1; b++) {
            for (int a = -1; a <= 1; a++) {
                if (byKey.get(key(d.dx + a, d.dy + b)) == null) continue;
                for (DrawnSite s : siteList(d.dx + a, d.dy + b)) {
                    long sx0 = Math.max(s.x0(), x0), sy0 = Math.max(s.y0(), y0), sx1 = Math.min(s.x1(), x0 + f - 1), sy1 = Math.min(s.y1(), y0 + f - 1);
                    if (sx0 > sx1 || sy0 > sy1) continue;
                    byte mark = s.mine() >= 0 ? DistrictPlan.SITE_MINED : DistrictPlan.SITE_FIELD;
                    for (long y = sy0; y <= sy1; y++) {
                        for (long x = sx0; x <= sx1; x++) {
                            int p = (int) ((y - y0) * f + (x - x0));
                            if (in.site[p] != DistrictPlan.SITE_MINED) in.site[p] = mark;
                        }
                    }
                    if (a == 0 && b == 0 && s.mine() >= 0) in.mines.add(new int[] { (int) (sx0 - x0), (int) (sy0 - y0), (int) (sx1 - x0), (int) (sy1 - y0) });
                }
            }
        }
        // The seams it surfaces: north, east, south, west, then the corners north-west, north-east, south-east, south-west.
        int[][][] others = { { { 0, -1 } }, { { 1, 0 } }, { { 0, 1 } }, { { -1, 0 } },
                { { -1, 0 }, { 0, -1 }, { -1, -1 } }, { { 1, 0 }, { 0, -1 }, { 1, -1 } },
                { { 1, 0 }, { 0, 1 }, { 1, 1 } }, { { -1, 0 }, { 0, 1 }, { -1, 1 } } };
        for (int k = 0; k < others.length; k++) {
            boolean first = true;
            for (int[] o : others[k]) {
                District od = byKey.get(key(d.dx + o[0], d.dy + o[1]));
                if (od != null && DISTRICT_ORDER.compare(od, d) < 0) first = false;
            }
            in.surfaces[k] = first;
        }
        in.root = d.index % CHAIN_BAND == 0;
        return in;
    }

    /* =====================================================================
       THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2
       ===================================================================== */

    /** How many numbers a node sums: the ten classes, the ground used (in half square feet, exact) and the owned dry plots. */
    public static final int NODE_WIDTH = BuildingVisual.CLASSES + 2;

    /** Where a node keeps the ground used, in half square feet. */
    public static final int NODE_USED = BuildingVisual.CLASSES;

    /** ...and its owned dry plots. */
    public static final int NODE_OWNED = BuildingVisual.CLASSES + 1;

    /**
     * The districts summed two by two, level by level, to one node - level 0
     * is the districts themselves, each level above a grid of nodes over the
     * districts' box, a node NODE_WIDTH numbers. Built when the map is drawn
     * or its land changes; a district's change goes up its ancestors only.
     */
    public static final class Pyramid {
        private final CityMap map;
        private final int minX, minY;
        /** Each level's grid: its width, height and nodes (NODE_WIDTH numbers each, row by row). */
        private final int[] widths, heights;
        private final long[][] nodes;
        private final int count;

        Pyramid(CityMap map) {
            this.map = map;
            int mx = Integer.MAX_VALUE, my = Integer.MAX_VALUE, xx = Integer.MIN_VALUE, xy = Integer.MIN_VALUE;
            for (District d : map.districts) {
                mx = Math.min(mx, d.dx); my = Math.min(my, d.dy);
                xx = Math.max(xx, d.dx); xy = Math.max(xy, d.dy);
            }
            if (mx == Integer.MAX_VALUE) { mx = my = xx = xy = 0; }
            minX = mx;
            minY = my;
            int w = xx - mx + 1, h = xy - my + 1;
            List<int[]> sizes = new ArrayList<>();
            while (w > 1 || h > 1) {
                w = (w + 1) / 2;
                h = (h + 1) / 2;
                sizes.add(new int[] { w, h });
            }
            widths = new int[sizes.size()];
            heights = new int[sizes.size()];
            nodes = new long[sizes.size()][];
            for (int l = 0; l < sizes.size(); l++) {
                widths[l] = sizes.get(l)[0];
                heights[l] = sizes.get(l)[1];
                nodes[l] = new long[widths[l] * heights[l] * NODE_WIDTH];
            }
            for (District d : map.districts) {
                long[] v = vector(d);
                int x = d.dx - minX, y = d.dy - minY;
                for (int l = 0; l < nodes.length; l++) {
                    x >>= 1;
                    y >>= 1;
                    int at = (y * widths[l] + x) * NODE_WIDTH;
                    for (int c = 0; c < NODE_WIDTH; c++) nodes[l][at + c] += v[c];
                }
            }
            int n = 0;
            for (int l = 0; l < nodes.length; l++) {
                for (int at = 0; at < nodes[l].length; at += NODE_WIDTH) {
                    boolean any = false;
                    for (int c = 0; c < NODE_WIDTH && !any; c++) any = nodes[l][at + c] != 0;
                    if (any || l == nodes.length - 1) n++;
                }
            }
            count = n;
        }

        /** A district's numbers: its buildings by class, its ground used (half square feet) and its owned dry plots. */
        long[] vector(District d) {
            long[] v = new long[NODE_WIDTH];
            for (int t = 0; t < map.types.length; t++) {
                BuildingVisual.Type ty = map.types[t];
                if (d.counts[t] == 0 || ty == null || !ty.drawn()) continue;
                v[ty.cls()] += d.counts[t];
            }
            v[NODE_USED] = d.usedHalf;
            v[NODE_OWNED] = d.owned;
            return v;
        }

        /** k more (or fewer) of type t in a district: its ancestors take them. */
        void add(District d, int t, long k) {
            BuildingVisual.Type ty = map.types[t];
            int cls = ty != null && ty.drawn() ? ty.cls() : -1;
            long used = ty == null ? 0 : k * 2 * ty.sqFt();
            int x = d.dx - minX, y = d.dy - minY;
            if (x < 0 || y < 0) return;
            for (int l = 0; l < nodes.length; l++) {
                x >>= 1;
                y >>= 1;
                if (x >= widths[l] || y >= heights[l]) return;
                int at = (y * widths[l] + x) * NODE_WIDTH;
                if (cls >= 0) nodes[l][at + cls] += k;
                nodes[l][at + NODE_USED] += used;
            }
        }

        /** How many levels above the districts. */
        public int levels() { return nodes.length; }

        /** How many nodes above the districts hold anything (and the top). */
        public int nodes() { return count; }

        /** The top node: the whole city's numbers. */
        public long[] root() {
            if (nodes.length == 0) {
                long[] v = new long[NODE_WIDTH];
                for (District d : map.districts) {
                    long[] w = vector(d);
                    for (int c = 0; c < NODE_WIDTH; c++) v[c] += w[c];
                }
                return v;
            }
            return Arrays.copyOf(nodes[nodes.length - 1], NODE_WIDTH);
        }

        /** The node at a level (1 up) covering district (dx, dy), or null. */
        public long[] node(int level, int dx, int dy) {
            if (level <= 0 || level > nodes.length) return null;
            int x = (dx - minX) >> level, y = (dy - minY) >> level, l = level - 1;
            if (x < 0 || y < 0 || x >= widths[l] || y >= heights[l]) return null;
            int at = (y * widths[l] + x) * NODE_WIDTH;
            return Arrays.copyOfRange(nodes[l], at, at + NODE_WIDTH);
        }
    }

    /* =====================================================================
       THE SIDECAR (spec-land 2.5)
       ===================================================================== */

    /**
     * The map as its sidecar's bytes, deflated: the header (magic, format,
     * seed, month, stamp, the founding site, the stamp of the land's rectangles
     * it was drawn on (CityLand.stamp(), 0.7.67) and its purchases, the type count, the district count), then a
     * record a district in its order - dx and dy (int16), its free plots and
     * those inside its tiles' edge rings (int32), plots used (double), owned
     * iron and oil sites and a count a type (int32) - and since FORMAT 5 the
     * city's highway and railway runs (int32 their count: 0 until batch RD3
     * lays them, which adds their records).
     * The stamp is a hash of every other byte, so the same map always writes
     * the same file.
     */
    public byte[] writeSidecar(int month) {
        return writeSidecar(month, FORMAT);
    }

    /** ...in a given format, FORMAT or OLDEST_READ (a harness writes the older one to read it back). */
    byte[] writeSidecar(int month, int format) {
        return writeSidecar(month, format, true);
    }

    /** ...and with its runs or (a harness's, as 0.7.88 wrote FORMAT 5) a count of none. */
    byte[] writeSidecar(int month, int format, boolean withRuns) {
        try {
            ByteArrayOutputStream raw = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(raw);
            out.writeInt(MAGIC);
            out.writeInt(format);
            out.writeLong(seed);
            out.writeInt(month);
            out.writeLong(0);                       // the stamp, below
            out.writeLong(siteX);
            out.writeLong(siteY);
            out.writeLong(land.stamp(purchasesSeen));
            out.writeInt(purchasesSeen);
            out.writeInt(types.length);
            out.writeInt(districts.size());
            for (District d : districts) {
                out.writeShort(d.dx);
                out.writeShort(d.dy);
                out.writeInt(d.owned);
                out.writeInt(d.inner);
                out.writeDouble(d.used());
                for (int k = 0; k < SITED.length; k++) out.writeInt(d.sites[k]);
                for (int t = 0; t < types.length; t++) out.writeInt(d.counts[t]);
            }
            // The runs (FORMAT 5): their count - 0 for none - and since 0.7.89 (batch RD3) the highways' and the railway's.
            if (format >= 5) {
                int n = withRuns ? runs.runs() : 0;
                out.writeInt(n);
                if (n > 0) runs.write(out);
            }
            out.flush();
            byte[] bytes = raw.toByteArray();
            long stamp = stampOf(bytes);
            lastStamp = stamp;
            for (int b = 0; b < 8; b++) bytes[STAMP_AT + b] = (byte) (stamp >>> (56 - 8 * b));
            ByteArrayOutputStream z = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
            try (DeflaterOutputStream d = new DeflaterOutputStream(z, deflater)) {
                d.write(bytes);
            } finally {
                deflater.end();
            }
            return z.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("the map would not write", e);
        }
    }

    private long lastStamp;

    /** The stamp of the sidecar last written: what the save carries as mapStamp. */
    public long lastStamp() { return lastStamp; }

    /** The stamp of a sidecar's raw bytes: SplitMix64 folded over them, the stamp's own eight bytes read as zero. */
    static long stampOf(byte[] raw) {
        long h = World.mix(MAGIC);
        for (int i = 0; i < raw.length; i += 8) {
            long w = 0;
            for (int b = 0; b < 8; b++) {
                int at = i + b;
                long v = at < raw.length && (at < STAMP_AT || at >= STAMP_AT + 8) ? raw[at] & 0xffL : 0;
                w = (w << 8) | v;
            }
            h = World.mix(h ^ w);
        }
        return h;
    }

    /** The stamp a sidecar carries, or 0 when it is not one. */
    public static long stampIn(byte[] deflated) {
        byte[] raw = inflate(deflated);
        if (raw == null || raw.length < STAMP_AT + 8) return 0;
        long s = 0;
        for (int b = 0; b < 8; b++) s = (s << 8) | (raw[STAMP_AT + b] & 0xffL);
        return s;
    }

    private static byte[] inflate(byte[] deflated) {
        try (InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(deflated))) {
            return in.readAllBytes();
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /**
     * A map read back from its sidecar, or null when it is not one, is not
     * this city's (another seed, month or stamp, or land drawn otherwise), or
     * does not add up. Its mines are put back on their sites, which gives the
     * counts the file holds when the ground's states are those it was saved
     * with.
     */
    public static CityMap readSidecar(byte[] deflated, CityLand land, Function<Resource, double[]> remaining,
                                      BuildingVisual.Type[] types, int month, long stamp) {
        byte[] raw = inflate(deflated);
        if (raw == null || raw.length < STAMP_AT + 8) return null;
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(raw));
            if (in.readInt() != MAGIC) return null;
            int format = in.readInt();
            if (format < OLDEST_READ || format > FORMAT) return null;
            long seed = in.readLong();
            int savedMonth = in.readInt();
            long savedStamp = in.readLong();
            if (seed != land.seed() || savedMonth != month || savedStamp != stamp || stampOf(raw) != stamp) return null;
            long sx = in.readLong(), sy = in.readLong();
            long landStamp = in.readLong();
            int bought = in.readInt();
            if (sx != land.siteX() || sy != land.siteY() || bought != land.purchases().size() || landStamp != land.stamp(bought)) return null;
            int nTypes = in.readInt(), n = in.readInt();
            if (nTypes > types.length || n < 0) return null;
            CityMap m = new CityMap(seed, sx, sy, types);
            m.land = land;
            m.remaining = remaining;
            for (int i = 0; i < n; i++) {
                int dx = in.readShort(), dy = in.readShort();
                District d = m.newDistrict(dx, dy);
                d.owned = in.readInt();
                d.inner = in.readInt();
                double used = in.readDouble();
                d.tileFree = null;
                for (int k = 0; k < SITED.length; k++) d.sites[k] = in.readInt();
                for (int t = 0; t < nTypes; t++) {
                    d.counts[t] = in.readInt();
                    m.have[t] += d.counts[t];
                }
                d.usedHalf = m.usedOf(d);
                d.usedCells = m.cellsOf(d);
                d.usedRoad = m.roadOf(d);
                if (used != d.used()) return null;
            }
            // The runs: read back; none written before 0.7.89 (batch RD3), nor by a FORMAT 4 map - laid now from its counts.
            int nRuns = format >= 5 ? in.readInt() : 0;
            if (nRuns < 0) return null;
            if (nRuns > 0) {
                CityRuns r = CityRuns.read(in);
                if (r == null) return null;
                m.runs = r;
            }
            m.purchasesSeen = bought;
            m.centreSeen = land.centreStamp();
            m.sortDistricts();
            m.pyramid = new Pyramid(m);
            if (nRuns == 0) m.layRuns(m.have);
            return m;
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /** Whether two maps hold the same districts with the same figures, in the same order, and (0.7.89) the same runs. */
    public boolean same(CityMap o) {
        return sameDistricts(o) && runs.same(o.runs);
    }

    /** Whether two maps hold the same districts with the same figures, in the same order. */
    public boolean sameDistricts(CityMap o) {
        if (o == null || o.seed != seed || o.districts.size() != districts.size() || o.types.length != types.length) return false;
        for (int i = 0; i < districts.size(); i++) {
            District a = districts.get(i), b = o.districts.get(i);
            if (a.dx != b.dx || a.dy != b.dy || a.owned != b.owned || a.usedHalf != b.usedHalf || !Arrays.equals(a.counts, b.counts)) return false;
        }
        return true;
    }
}
