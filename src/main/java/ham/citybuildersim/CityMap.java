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
 * The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, dealt out over each district's tiles with its roads, one drawn building for each the model has, summed up a pyramid for the far view, and written beside the save as a sidecar - what the tile painter paints the city from.
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
 * THE DEAL (deal(), 0.7.64) puts each of a district's buildings and road
 * plots on one of its own 64 tiles, never more than a tile has free plots
 * for: the roads first, ROAD_PIECE plots at a time, then the buildings
 * largest first (BuildingVisual.rankedTypes()), each where its own hash
 * says through a table weighted by the tile's free plots - leaning toward
 * the founding site for what follows people, away from it for farms,
 * utilities, mines, the railway and the car plants - and, its tile full,
 * the nearest tile with room (the farthest for the outer kinds). One more
 * building changes one tile by one while its tile has room. The painter
 * then draws a tile's buildings one for one on their own land and grows
 * its road plots exactly: a city is drawn as what it has, not more, not
 * less (Jerus, 2026-10-07; J3b's homes from people and workplaces from
 * jobs, and the mockup's two world highways, are gone).
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
 * hash of their rectangles (CityLand.stamp(), FORMAT 3).
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

    /** The sidecar's format: 3 since 0.7.67, its land stamp the holdings' rectangles (CityLand.stamp()) where a centre's half-side was; 2 from 0.7.64, when a district's room became its free plots counted plot by plot. An older sidecar is not read, and the map is drawn again once. */
    static final int FORMAT = 3;

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
        /** Its free plots inside its tiles' edge rings, where alone a road is laid, and the road plots it holds: its room for roads (0.7.64). */
        int inner;
        long usedRoad;
        /** Its count of every type, by id. */
        final int[] counts;
        /** Its owned sites of each SITED resource. */
        final int[] sites = new int[SITED.length];
        /** Each tile's free plots, row by row: owned and dry; null until measured or asked for. */
        short[] tileFree;
        /** ...and those inside its edge ring, where alone the painter lays a road. */
        short[] tileInner;
        /** Each tile's sites, by key (TilePainter.Input.siteKey), and their free plots on it, all and inside the edge ring: what a mine standing on one takes of the tile. */
        long[][] tileSiteKey;
        short[][] tileSiteFree, tileSiteInner;
        /** How many of each site-bound type stand on its sites (the rest of its count has none); null when none. */
        int[] sited;
        /** For painting: per SITED resource, pairs of {holding, mines} - how many of each holding's sites here carry one. */
        int[][] holdingMines = new int[SITED.length][];
        /** Bumped whenever its counts change: what its deal is cached against. */
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
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null || types[t].site() != null) continue;
            long want = t < model.length ? model[t] : 0;
            long delta = want - have[t];
            if (delta > 0) touched += place(t, delta);
            else if (delta < 0) touched += remove(t, -delta);
        }
        placeSited(model);
        advanceCursors();
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

    /** Places n more of type t: into the first districts with room for its whole plots in its direction (a road's inside its tiles' edge rings), the rest into the district with the most room left (0.7.64; the outermost before, which can be a district of water holding only sites). */
    private int place(int t, long n) {
        boolean out = types[t].outer(), road = types[t].road() != BuildingVisual.NOT_A_ROAD;
        long each = BuildingVisual.cells(types[t]);
        int touched = 0;
        int i = out ? outerCursor : innerCursor;
        while (n > 0 && i >= 0 && i < districts.size()) {
            District d = districts.get(i);
            long room = each > 0 ? (road ? d.freeRoad() : d.freeCells()) / each : n;
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
            if (types[t] == null || types[t].site() != null || t >= counts.length) continue;
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
       THE DEAL: EACH DISTRICT ONTO ITS OWN TILES, AS FULL AS THEY HOLD
       (0.7.64, batch L2)

       J3b spread a district over the 16 x 16 tiles about it by a hash, so
       a tile could be dealt more than it holds - harmless while the painter
       drew homes from people and could draw fewer; now every building the
       model has is drawn, so the deal keeps every tile to its free plots.
       Roads first, ROAD_PIECE plots at a time, then buildings largest first
       (BuildingVisual.rankedTypes(), the painter's own order), each to the
       tile its hash picks through a table weighted by the tiles' free plots
       - times the core's lean for what follows people, divided by it for
       the outer kinds - or, that tile full, to the nearest tile with room
       for its whole plots (the farthest for the outer kinds), else the one
       with the most room, where it is drawn smaller. A district holds no
       more than its tiles have room for (place(), in whole plots), so every
       building finds a plot; one more changes one tile by one while its
       tile has room.
       ===================================================================== */

    /** How much more of what follows people a tile at the founding site takes, before its district's share is shared out: 6 times more (J3b's star) - its middle a town's size... */
    public static final double CORE_BOOST = 6;

    /** ...falling off over this many plots: 80 (2.4 km, J3b's star). */
    public static final double CORE_RADIUS = 80;

    /** A district's road is dealt to its tiles this many plots at a time: 8, the longest step a road grows (TilePainter.STEP_PAVED + STEP_SPAN) - so a tile's road is at least a run, and an Elevated Highway's 7 plots are one piece, not seven tiles' dots. */
    static final int ROAD_PIECE = TilePainter.STEP_PAVED + TilePainter.STEP_SPAN;

    /** How many districts' deals are kept: 64 - about 1.3 MB, a screen's and its neighbours' many times over. */
    static final int TILE_COUNTS_KEPT = 64;

    /** Bumped whenever free ground is measured again: what the deals are kept against. */
    private long landVersion;

    /** A district's deal: each tile's counts by type id (a mine or well on a site apart), and its road plots by kind [0, gravel, paved, highway]. */
    static final class Dealt {
        long stamp;
        int[][] counts;
        int[][] roads;
        /** Plots of each tile the deal charged and found none for: buildings with no plot at all, roads unlaid (0 while the district has room). */
        int overDealt;
    }

    private final Map<District, Dealt> dealt = new LinkedHashMap<>(TILE_COUNTS_KEPT * 2, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<District, Dealt> e) { return size() > TILE_COUNTS_KEPT; }
    };

    /** Whether a type leans toward the founding site in the deal: everything but roads and the outer kinds - what follows people. */
    static boolean leansToCore(BuildingVisual.Type t) {
        return t != null && t.road() == BuildingVisual.NOT_A_ROAD && !t.outer();
    }

    /** The core's lean at a tile: 1 + CORE_BOOST e^-(r / CORE_RADIUS)^2, r from the founding site to the tile's middle in plots. */
    double coreLean(long tx, long ty) {
        double dx = tx * World.TILE + World.TILE / 2.0 - siteX, dy = ty * World.TILE + World.TILE / 2.0 - siteY;
        double q = (dx * dx + dy * dy) / (CORE_RADIUS * CORE_RADIUS);
        return 1 + CORE_BOOST * Math.exp(-q);
    }

    /** Vose's alias table for weights w, into prob and alias. */
    static void aliasTable(double[] w, double[] prob, int[] alias) {
        int n = w.length;
        double sum = 0;
        for (double v : w) sum += v;
        double[] scaled = new double[n];
        int[] small = new int[n], large = new int[n];
        int ns = 0, nl = 0;
        for (int i = 0; i < n; i++) {
            scaled[i] = sum > 0 ? w[i] * n / sum : 1;
            if (scaled[i] < 1) small[ns++] = i; else large[nl++] = i;
        }
        while (ns > 0 && nl > 0) {
            int s = small[--ns], l = large[--nl];
            prob[s] = scaled[s];
            alias[s] = l;
            scaled[l] = scaled[l] + scaled[s] - 1;
            if (scaled[l] < 1) small[ns++] = l; else large[nl++] = l;
        }
        while (nl > 0) prob[large[--nl]] = 1;
        while (ns > 0) prob[small[--ns]] = 1;
    }

    /** A pick through an alias table by a hash. */
    private static int pick(long h, double[] prob, int[] alias) {
        int i = (int) ((h >>> 32) % prob.length);
        double u = (h & 0xffffffffL) * 0x1.0p-32;
        return u < prob[i] ? i : alias[i];
    }

    /**
     * A district's tiles' counts, [tile][type] (tile = row x 8 + column):
     * every building it holds but its mines and wells on sites, each on one
     * of its own tiles (THE DEAL). Kept for the last TILE_COUNTS_KEPT
     * districts asked.
     */
    public int[][] deal(District d) {
        return dealtOf(d).counts;
    }

    /** ...and their road plots by kind, [tile][0, gravel, paved, highway]. */
    public int[][] roads(District d) {
        return dealtOf(d).roads;
    }

    /** What the deal of a district could not fit: plots charged to no tile (0 while the district holds no more than its tiles have room for). */
    public int overDealt(District d) {
        return dealtOf(d).overDealt;
    }

    private Dealt dealtOf(District d) {
        long stamp = World.mix(World.mix(landVersion ^ 0x5EEDL) ^ d.version);
        Dealt got = dealt.get(d);
        if (got != null && got.stamp == stamp) return got;
        got = dealNow(d);
        got.stamp = stamp;
        dealt.put(d, got);
        return got;
    }

    private Dealt dealNow(District d) {
        Dealt out = new Dealt();
        out.counts = new int[TILES][types.length];
        out.roads = new int[TILES][4];
        short[] free = tileFree(d), inner = d.tileInner;
        int[] spare = new int[TILES], innerSpare = new int[TILES];
        // The sites a mine or well stands on: their plots are the mines', not the tiles' to deal.
        java.util.Set<Long> mined = new java.util.HashSet<>();
        for (int b = -1; b <= 1; b++) {
            for (int a = -1; a <= 1; a++) {
                if (byKey.get(key(d.dx + a, d.dy + b)) == null) continue;
                for (DrawnSite site : siteList(d.dx + a, d.dy + b)) if (site.mine() >= 0) mined.add((site.x0() << 32) | (site.y0() & 0xffffffffL));
            }
        }
        double[] in = new double[TILES], away = new double[TILES], dist = new double[TILES];
        long t0x = (baseDX + d.dx) * TILES_A_SIDE, t0y = (baseDY + d.dy) * TILES_A_SIDE;
        boolean any = false;
        for (int k = 0; k < TILES; k++) {
            long tx = t0x + k % TILES_A_SIDE, ty = t0y + k / TILES_A_SIDE;
            spare[k] = free[k];
            innerSpare[k] = inner == null ? free[k] : inner[k];
            if (d.tileSiteKey != null && d.tileSiteKey[k] != null) {
                for (int a = 0; a < d.tileSiteKey[k].length; a++) {
                    if (!mined.contains(d.tileSiteKey[k][a])) continue;
                    spare[k] -= d.tileSiteFree[k][a];
                    innerSpare[k] -= d.tileSiteInner[k][a];
                }
            }
            spare[k] = Math.max(0, spare[k]);
            innerSpare[k] = Math.max(0, innerSpare[k]);
            double lean = coreLean(tx, ty);
            in[k] = spare[k] * lean;
            away[k] = spare[k] / lean;
            double cx = tx * World.TILE + World.TILE / 2.0 - siteX, cy = ty * World.TILE + World.TILE / 2.0 - siteY;
            dist[k] = cx * cx + cy * cy;
            any |= spare[k] > 0;
        }
        if (!any) { Arrays.fill(in, 1); Arrays.fill(away, 1); }
        double[] probIn = new double[TILES], probAway = new double[TILES];
        int[] aliasIn = new int[TILES], aliasAway = new int[TILES];
        aliasTable(in, probIn, aliasIn);
        aliasTable(away, probAway, aliasAway);
        // The tiles nearest the founding site first; the outer kinds take them the other way round.
        Integer[] byDist = new Integer[TILES];
        for (int k = 0; k < TILES; k++) byDist[k] = k;
        Arrays.sort(byDist, (a, b) -> dist[a] != dist[b] ? Double.compare(dist[a], dist[b]) : Integer.compare(a, b));
        int[] near = new int[TILES], far = new int[TILES];
        for (int k = 0; k < TILES; k++) { near[k] = byDist[k]; far[TILES - 1 - k] = byDist[k]; }
        long dseed = World.mix(seed ^ ((baseDX + d.dx) << 32) ^ ((baseDY + d.dy) & 0xffffffffL));
        // The roads, ROAD_PIECE plots at a time: highways, paved, gravel.
        for (int kind = BuildingVisual.HIGHWAY; kind >= BuildingVisual.GRAVEL; kind--) {
            long plots = 0;
            for (int t = 0; t < types.length; t++) {
                if (types[t] == null || types[t].road() != kind || d.counts[t] <= 0) continue;
                plots += (long) d.counts[t] * BuildingVisual.cells(types[t]);
            }
            long base = World.mix(dseed ^ 0xA0ADL ^ ((long) kind << 40));
            for (long u = 0; plots > 0; u++) {
                // A road is laid only inside a tile's edge ring: a tile takes a piece while its plots there hold it.
                int piece = (int) Math.min(ROAD_PIECE, plots);
                int k = pick(World.mix(base + u), probIn, aliasIn);
                if (innerSpare[k] < piece) k = firstWith(near, innerSpare, piece);
                if (k < 0) {
                    // No tile holds the piece whole: as much of it as each tile holds, nearest first - inside the edge
                    // rings, then on any plot left, where the painter lays what its ring-free plots cannot hold.
                    for (int pass = 0; pass < 2 && piece > 0; pass++) {
                        for (int n : near) {
                            int take = Math.min(piece, pass == 0 ? innerSpare[n] : spare[n]);
                            if (take <= 0) continue;
                            out.roads[n][kind] += take;
                            spare[n] -= take;
                            innerSpare[n] = Math.max(0, innerSpare[n] - take);
                            piece -= take;
                            plots -= take;
                        }
                    }
                    if (piece > 0) { out.overDealt += (int) plots; break; }
                    continue;
                }
                out.roads[k][kind] += piece;
                spare[k] -= piece;
                innerSpare[k] -= piece;
                plots -= piece;
            }
        }
        // The buildings, largest first, type by type and number by number.
        for (int t : BuildingVisual.rankedTypes(types)) {
            int c = d.counts[t] - (d.sited == null ? 0 : d.sited[t]);
            if (c <= 0) continue;
            int need = BuildingVisual.cells(types[t]);
            boolean outer = !leansToCore(types[t]);
            double[] prob = outer ? probAway : probIn;
            int[] alias = outer ? aliasAway : aliasIn, order = outer ? far : near;
            long base = World.mix(dseed ^ ((long) t << 40));
            for (int j = 0; j < c; j++) {
                int k = pick(World.mix(base + j), prob, alias);
                if (spare[k] < need) {
                    int room = firstWith(order, spare, need);
                    k = room >= 0 ? room : roomiest(order, spare);
                }
                int charge = Math.min(need, spare[k]);
                if (charge < 1) out.overDealt++;
                out.counts[k][t]++;
                spare[k] -= Math.max(0, charge);
            }
        }
        return out;
    }

    /** The first tile in `order` with at least `need` plots spare, or -1. */
    private static int firstWith(int[] order, int[] spare, int need) {
        for (int k : order) if (spare[k] >= need) return k;
        return -1;
    }

    /** The tile in `order` with the most plots spare, the first on a tie. */
    private static int roomiest(int[] order, int[] spare) {
        int best = order[0];
        for (int k : order) if (spare[k] > spare[best]) best = k;
        return best;
    }

    /* =====================================================================
       A TILE'S INPUTS FOR THE PAINTER
       ===================================================================== */

    /** The district holding tile (tx, ty), or null. */
    public District districtOfTile(long tx, long ty) {
        return byKey.get(key((int) (Math.floorDiv(tx, TILES_A_SIDE) - baseDX), (int) (Math.floorDiv(ty, TILES_A_SIDE) - baseDY)));
    }

    /** A tile's counts of the model's types from the deal: a copy into out, zeros where no district is. */
    public void tileCounts(long tx, long ty, int[] out) {
        Arrays.fill(out, 0);
        District d = districtOfTile(tx, ty);
        if (d == null) return;
        int[] c = deal(d)[(int) (Math.floorMod(ty, TILES_A_SIDE) * TILES_A_SIDE + Math.floorMod(tx, TILES_A_SIDE))];
        System.arraycopy(c, 0, out, 0, Math.min(c.length, out.length));
    }

    /** A tile's road plots by kind from the deal, into out [0, gravel, paved, highway]. */
    public void tileRoads(long tx, long ty, int[] out) {
        Arrays.fill(out, 0);
        District d = districtOfTile(tx, ty);
        if (d == null) return;
        int[] r = roads(d)[(int) (Math.floorMod(ty, TILES_A_SIDE) * TILES_A_SIDE + Math.floorMod(tx, TILES_A_SIDE))];
        System.arraycopy(r, 0, out, 0, Math.min(r.length, out.length));
    }

    /** Whether a tile has road plots to grow: what gives its neighbours their ports. */
    boolean tileHasRoads(long tx, long ty) {
        District d = districtOfTile(tx, ty);
        if (d == null) return false;
        int[] r = roads(d)[(int) (Math.floorMod(ty, TILES_A_SIDE) * TILES_A_SIDE + Math.floorMod(tx, TILES_A_SIDE))];
        return r[BuildingVisual.GRAVEL] + r[BuildingVisual.PAVED] + r[BuildingVisual.HIGHWAY] > 0;
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
     * what the city owns of it, the model's buildings and road plots the deal
     * gave it, which neighbours have roads, and the sites on it with their
     * states and the mines standing on them. Since 0.7.64 nothing is drawn
     * that the model does not have: J3b's homes from people, workplaces from
     * jobs and roads from frontage, and the mockup's two world highways, are
     * gone.
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

    /** Everything tileInput() fills but the ground, which is in `in` already. */
    private void fillInput(long tx, long ty, TilePainter.Input in) {
        in.seed = seed;
        in.tx = tx;
        in.ty = ty;
        in.types = types;
        tileOwnership(tx, ty, in.owned);
        if (in.counts.length != types.length) in.counts = new int[types.length];
        if (in.model.length != types.length) in.model = new int[types.length];
        tileCounts(tx, ty, in.counts);
        System.arraycopy(in.counts, 0, in.model, 0, types.length);
        tileRoads(tx, ty, in.roadBudget);
        in.neighbourRoads[0] = tileHasRoads(tx, ty - 1);
        in.neighbourRoads[1] = tileHasRoads(tx + 1, ty);
        in.neighbourRoads[2] = tileHasRoads(tx, ty + 1);
        in.neighbourRoads[3] = tileHasRoads(tx - 1, ty);
        in.clearSites();
        long px0 = tx * World.TILE, py0 = ty * World.TILE;
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

    /** Forgets the painted state of every district - the deals and the site lists - after the ground's states moved (what is worked out). */
    public void forgetPainted() {
        dealt.clear();
        siteLists.clear();
        siteListVersion.clear();
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
     * iron and oil sites and a count a type (int32).
     * The stamp is a hash of every other byte, so the same map always writes
     * the same file.
     */
    public byte[] writeSidecar(int month) {
        try {
            ByteArrayOutputStream raw = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(raw);
            out.writeInt(MAGIC);
            out.writeInt(FORMAT);
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
            if (in.readInt() != MAGIC || in.readInt() != FORMAT) return null;
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
            m.purchasesSeen = bought;
            m.centreSeen = land.centreStamp();
            m.sortDistricts();
            m.pyramid = new Pyramid(m);
            return m;
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /** Whether two maps hold the same districts with the same figures, in the same order. */
    public boolean same(CityMap o) {
        if (o == null || o.seed != seed || o.districts.size() != districts.size() || o.types.length != types.length) return false;
        for (int i = 0; i < districts.size(); i++) {
            District a = districts.get(i), b = o.districts.get(i);
            if (a.dx != b.dx || a.dy != b.dy || a.owned != b.owned || a.usedHalf != b.usedHalf || !Arrays.equals(a.counts, b.counts)) return false;
        }
        return true;
    }
}
