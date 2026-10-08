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
 * hash of their rectangles (CityLand.stamp(), FORMAT 3; FORMAT 4 since 0.7.72,
 * the roads following their buildings).
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

    /** The sidecar's format: 4 since 0.7.72, when a district's roads came to follow its buildings (ROADS FOLLOW THEIR BUILDINGS) - an older map, whose districts' roads were placed nearest first, some with none, is drawn again canonically, each district its share; 3 from 0.7.67, its land stamp the holdings' rectangles (CityLand.stamp()) where a centre's half-side was; 2 from 0.7.64, when a district's room became its free plots counted plot by plot. An older sidecar is not read, and the map is drawn again once. */
    static final int FORMAT = 4;

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
        // The buildings first, then the roads, which follow them (0.7.72, ROADS FOLLOW THEIR BUILDINGS).
        roadShare = roadShareOf(model);
        builtIn.clear();
        builtInSet.clear();
        for (int roads = 0; roads < 2; roads++) {
            for (int t = 0; t < types.length; t++) {
                if (types[t] == null || types[t].site() != null || (types[t].road() != BuildingVisual.NOT_A_ROAD) != (roads == 1)) continue;
                long want = t < model.length ? model[t] : 0;
                long delta = want - have[t];
                if (delta > 0) touched += roads == 1 ? placeRoad(t, delta) : place(t, delta);
                else if (delta < 0) touched += remove(t, -delta);
            }
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
            if (types[t] == null || model[t] <= 0) continue;
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

    /** How many districts' deals are kept: 64 - about 1.3 MB, a screen's and its neighbours' many times over. */
    static final int TILE_COUNTS_KEPT = 64;

    /** Bumped whenever free ground is measured again: what the deals are kept against. */
    private long landVersion;

    /** A district's deal: each tile's counts by type id (a mine or well on a site apart), its road plots by kind [0, gravel, paved, highway], and (0.7.72) its railway's plots and the plan's runs on it (Plan.segments). */
    static final class Dealt {
        long stamp;
        int[][] counts;
        int[][] roads;
        int[] rail;
        int[][] plan;
        /** The railway's plots the plan found no way for. */
        long railShort;
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
        // ...and its neighbours' roads: their road tiles meet its own (0.7.72, THE NETWORK).
        for (int dir = 0; dir < 4; dir++) {
            District b = byKey.get(key(d.dx + TilePainter.DX[dir], d.dy + TilePainter.DY[dir]));
            stamp = World.mix(stamp ^ (b == null ? -1 : b.usedRoad));
        }
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
        out.plan = new int[TILES][];
        out.rail = new int[TILES];
        short[] free = tileFree(d), inner = d.tileInner;
        int[] spare = new int[TILES], innerSpare = new int[TILES];
        // The sites a mine or well stands on: their plots are the mines', not the tiles' to deal.
        java.util.Set<Long> mined = new java.util.HashSet<>();
        List<DrawnSite> minedSites = new ArrayList<>();
        for (int b = -1; b <= 1; b++) {
            for (int a = -1; a <= 1; a++) {
                if (byKey.get(key(d.dx + a, d.dy + b)) == null) continue;
                for (DrawnSite site : siteList(d.dx + a, d.dy + b)) {
                    if (site.mine() < 0) continue;
                    mined.add((site.x0() << 32) | (site.y0() & 0xffffffffL));
                    minedSites.add(site);
                }
            }
        }
        double[] in = new double[TILES], away = new double[TILES], dist = new double[TILES], lean = new double[TILES];
        long t0x = (baseDX + d.dx) * TILES_A_SIDE, t0y = (baseDY + d.dy) * TILES_A_SIDE;
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
            lean[k] = coreLean(tx, ty);
            double cx = tx * World.TILE + World.TILE / 2.0 - siteX, cy = ty * World.TILE + World.TILE / 2.0 - siteY;
            dist[k] = cx * cx + cy * cy;
        }
        // The tiles nearest the founding site first; the outer kinds take them the other way round.
        Integer[] byDist = new Integer[TILES];
        for (int k = 0; k < TILES; k++) byDist[k] = k;
        Arrays.sort(byDist, (a, b) -> dist[a] != dist[b] ? Double.compare(dist[a], dist[b]) : Integer.compare(a, b));
        int[] near = new int[TILES], far = new int[TILES];
        for (int k = 0; k < TILES; k++) { near[k] = byDist[k]; far[TILES - 1 - k] = byDist[k]; }
        long dseed = World.mix(seed ^ ((baseDX + d.dx) << 32) ^ ((baseDY + d.dy) & 0xffffffffL));

        // THE NETWORK (0.7.72): the road tiles, the plan's highways and railway lines, the mains and the grid's streets.
        RoadTiles rt = roadTilesOf(d);
        long[] kindPlots = new long[4];
        long track = 0;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null || d.counts[t] <= 0) continue;
            if (types[t].road() != BuildingVisual.NOT_A_ROAD) kindPlots[types[t].road()] += (long) d.counts[t] * BuildingVisual.cells(types[t]);
            else if (types[t].track()) track += (long) d.counts[t] * BuildingVisual.cells(types[t]);
        }
        Plan plan = planOf(d, rt, kindPlots[BuildingVisual.HIGHWAY], track, minedSites, spare);
        out.plan = plan.segments;
        out.railShort = plan.railLeft;
        for (int k = 0; k < TILES; k++) {
            out.roads[k][BuildingVisual.HIGHWAY] = plan.hw[k];
            out.rail[k] = plan.rail[k];
        }
        // A highway the plan could not lay, and every road of a district with no road tile, the grid lays as streets.
        long hwLeft = plan.hwLeft;
        long ordinary = kindPlots[BuildingVisual.GRAVEL] + kindPlots[BuildingVisual.PAVED];
        int[] total = new int[TILES];
        if (rt.n > 0) {
            // Each road tile its main streets first, in the order the tiles were taken: the plan's, its hub and an arm
            // to each road tile it is joined to, in this district and the next.
            long left = ordinary + hwLeft;
            for (int m = 0; m < rt.n && left > 0; m++) {
                int k = rt.order[m];
                int give = (int) Math.min(left, plan.mains[k]);
                total[k] += give;
                left -= give;
            }
            // ...then the grid's streets, in proportion to the tiles' free plots and the core's lean, as the buildings
            // that follow people are dealt, each to its grid and lanes (LANES_TOO); what none has room for, by the
            // same weights.
            double[] w = new double[TILES];
            long[] cap = new long[TILES];
            long spareAll = 0;
            for (int k = 0; k < TILES; k++) if (rt.road[k]) spareAll += spare[k];
            double share = spareAll > 0 ? (ordinary + hwLeft + kindPlots[BuildingVisual.HIGHWAY]) / (double) spareAll : 0;
            for (int k = 0; k < TILES; k++) {
                if (!rt.road[k]) continue;
                w[k] = Math.max(0, spare[k] - total[k] - plan.hw[k] - plan.rail[k]) * lean[k];
                long most = Math.min((long) Math.floor(rt.grid[k] * (1 + LANES_TOO)), (long) Math.ceil(spare[k] * share * ROAD_LEAN_MOST));
                cap[k] = Math.max(0, most - total[k] - plan.hw[k]);
            }
            for (int pass = 0; pass < 2 && left > 0; pass++) {
                long given = shareOut(w, pass == 0 ? cap : null, left, total, rt);
                left -= given;
            }
            // Paved first: the main streets' bridges, then in the order the road tiles were taken - the core's streets
            // and mains paved.
            long paved = kindPlots[BuildingVisual.PAVED], gravel = kindPlots[BuildingVisual.GRAVEL], hw = hwLeft;
            int[] pv = new int[TILES];
            for (int m = 0; m < rt.n; m++) {
                int k = rt.order[m];
                pv[k] = (int) Math.min(Math.min(plan.bridges[k], total[k]), paved);
                paved -= pv[k];
            }
            for (int m = 0; m < rt.n; m++) {
                int k = rt.order[m];
                int p = (int) Math.min(total[k] - pv[k], paved) + pv[k];
                paved -= p - pv[k];
                int g = (int) Math.min(total[k] - p, gravel);
                gravel -= g;
                int h = (int) Math.min(total[k] - p - g, hw);
                hw -= h;
                out.roads[k][BuildingVisual.PAVED] += p;
                out.roads[k][BuildingVisual.GRAVEL] += g;
                out.roads[k][BuildingVisual.HIGHWAY] += h;
            }
            if (paved + gravel + hw > 0) out.overDealt += (int) (paved + gravel + hw);
        } else if (ordinary + hwLeft > 0) {
            // No tile with ground for a road tile: the roads on the roomiest tiles, nearest first, as 0.7.64 dealt them.
            long[] kp = { 0, kindPlots[BuildingVisual.GRAVEL], kindPlots[BuildingVisual.PAVED], hwLeft };
            for (int kind = BuildingVisual.HIGHWAY; kind >= BuildingVisual.GRAVEL; kind--) {
                for (int n : near) {
                    int take = (int) Math.min(kp[kind], Math.max(0, spare[n] - total[n] - plan.rail[n] - plan.hw[n]));
                    out.roads[n][kind] += take;
                    total[n] += take;
                    kp[kind] -= take;
                }
                if (kp[kind] > 0) out.overDealt += (int) kp[kind];
            }
        }
        // What each tile keeps free for its buildings' shapes while another tile has room (TILE_FULL_MOST), and its room
        // short of that.
        int[] soft = new int[TILES];
        for (int k = 0; k < TILES; k++) {
            int keep = (int) Math.floor(spare[k] * (1 - TILE_FULL_MOST));
            spare[k] = Math.max(0, spare[k] - out.roads[k][BuildingVisual.GRAVEL] - out.roads[k][BuildingVisual.PAVED]
                    - out.roads[k][BuildingVisual.HIGHWAY] - out.rail[k]);
            soft[k] = Math.max(0, spare[k] - keep);
        }
        // The buildings, largest first, type by type and number by number - onto the road tiles while the district
        // has any (0.7.72: every building near a road), the rest where there is room.
        boolean anyRoad = rt.n > 0;
        boolean any = false;
        for (int k = 0; k < TILES; k++) {
            boolean ok = !anyRoad || rt.road[k];
            in[k] = ok ? spare[k] * lean[k] : 0;
            away[k] = ok ? spare[k] / lean[k] : 0;
            any |= in[k] > 0;
        }
        if (!any) for (int k = 0; k < TILES; k++) { in[k] = spare[k] * lean[k]; away[k] = spare[k] / lean[k]; any |= in[k] > 0; }
        if (!any) { Arrays.fill(in, 1); Arrays.fill(away, 1); }
        double[] probIn = new double[TILES], probAway = new double[TILES];
        int[] aliasIn = new int[TILES], aliasAway = new int[TILES];
        aliasTable(in, probIn, aliasIn);
        aliasTable(away, probAway, aliasAway);
        int[] nearRoad = onRoadFirst(near, rt), farRoad = onRoadFirst(far, rt);
        int[] yards = yardOrder(plan, minedSites, t0x, t0y);
        for (int t : BuildingVisual.rankedTypes(types)) {
            int c = d.counts[t] - (d.sited == null ? 0 : d.sited[t]);
            if (c <= 0) continue;
            int need = BuildingVisual.cells(types[t]);
            boolean outer = !leansToCore(types[t]);
            double[] prob = outer ? probAway : probIn;
            int[] alias = outer ? aliasAway : aliasIn, order = outer ? farRoad : nearRoad;
            long base = World.mix(dseed ^ ((long) t << 40));
            for (int j = 0; j < c; j++) {
                int k = -1;
                // A rail yard beside its track, on the track's tile nearest a mine (Jerus: "rail terminals prefer to sit near mines").
                if (types[t].terminal()) k = firstWith(yards, spare, need);
                if (k < 0) {
                    // Its hashed tile, or the nearest (farthest) with room short of what it keeps for shapes; then with
                    // any room; then the roomiest.
                    int hashed = pick(World.mix(base + j), prob, alias);
                    k = soft[hashed] >= need ? hashed : firstWith(order, soft, need);
                    if (k < 0) k = spare[hashed] >= need ? hashed : firstWith(order, spare, need);
                    if (k < 0) k = roomiest(order, spare);
                }
                int charge = Math.min(need, spare[k]);
                if (charge < 1) out.overDealt++;
                out.counts[k][t]++;
                spare[k] -= Math.max(0, charge);
                soft[k] = Math.max(0, soft[k] - Math.max(0, charge));
            }
        }
        if (rt.n > 0) moveExcess(out, rt, plan, free, spare);
        return out;
    }

    /**
     * The streets a road tile cannot hold as lines, moved to road tiles that
     * can (0.7.72): a tile's streets are its grid and lanes, which run round
     * its large buildings, not under them - so a tile dealt many is dealt road
     * its lines cannot hold, and the painter laid the rest as a fill beside
     * its roads, plazas of + junctions. A road tile holds about its main
     * streets and its line share (its grid and lanes over its free plots) of
     * the plots its large buildings leave; what it is dealt past that goes, a
     * plot at a time - gravel first, so the paved stay where the order put
     * them - to the road tiles in the order taken that hold less than they
     * could and have plots to spare. The buildings dealt stand as they were.
     */
    private void moveExcess(Dealt out, RoadTiles rt, Plan plan, short[] free, int[] spare) {
        int[] cells = BuildingVisual.cellsById(types);
        long[] over = new long[TILES], under = new long[TILES];
        long excess = 0;
        for (int m = 0; m < rt.n; m++) {
            int k = rt.order[m];
            long large = 0;
            for (int t = 0; t < types.length; t++) {
                if (out.counts[k][t] > 0 && types[t] != null && types[t].drawn() && TilePainter.passOf(types[t], cells[t]) != TilePainter.SMALL) large += (long) out.counts[k][t] * cells[t];
            }
            double share = free[k] > 0 ? rt.grid[k] * (1 + LANES_TOO) / free[k] : 0;
            long open = Math.max(0, free[k] - large - plan.mains[k] - plan.hw[k] - plan.rail[k]);
            long holds = plan.mains[k] + plan.hw[k] + (long) Math.floor(open * share * LINES_HOLD);
            long roads = out.roads[k][BuildingVisual.GRAVEL] + out.roads[k][BuildingVisual.PAVED] + out.roads[k][BuildingVisual.HIGHWAY];
            if (roads > holds) { over[k] = roads - holds; excess += over[k]; }
            else under[k] = Math.min(holds - roads, spare[k]);
        }
        if (excess == 0) return;
        for (int m = 0; m < rt.n && excess > 0; m++) {
            int to = rt.order[m];
            for (int n = rt.n - 1; n >= 0 && under[to] > 0 && excess > 0; n--) {
                int from = rt.order[n];
                if (from == to || over[from] <= 0) continue;
                long move = Math.min(under[to], over[from]);
                for (int kind : new int[] { BuildingVisual.GRAVEL, BuildingVisual.PAVED, BuildingVisual.HIGHWAY }) {
                    long take = Math.min(move, out.roads[from][kind] - (kind == BuildingVisual.HIGHWAY ? plan.hw[from] : 0));
                    if (take <= 0) continue;
                    out.roads[from][kind] -= (int) take;
                    out.roads[to][kind] += (int) take;
                    spare[to] -= (int) take;
                    spare[from] += (int) take;
                    over[from] -= take;
                    under[to] -= take;
                    excess -= take;
                    move -= take;
                }
            }
        }
    }

    /** Tiles in `order`, the road tiles first (each part in its own order). */
    private static int[] onRoadFirst(int[] order, RoadTiles rt) {
        int[] o = new int[order.length];
        int n = 0;
        for (int k : order) if (rt.road[k]) o[n++] = k;
        for (int k : order) if (!rt.road[k]) o[n++] = k;
        return o;
    }

    /** The tiles a rail yard stands on, first choice first: those the plan's track crosses, nearest a mine first (the nearest the track's middle where there is none). */
    private int[] yardOrder(Plan plan, List<DrawnSite> mines, long t0x, long t0y) {
        int n = 0;
        for (int k = 0; k < TILES; k++) if (plan.rail[k] > 0) n++;
        Integer[] ks = new Integer[n];
        double[] key = new double[TILES];
        n = 0;
        for (int k = 0; k < TILES; k++) {
            if (plan.rail[k] <= 0) continue;
            ks[n++] = k;
            double cx = (t0x + k % TILES_A_SIDE) * World.TILE + World.TILE / 2.0, cy = (t0y + k / TILES_A_SIDE) * World.TILE + World.TILE / 2.0;
            double best = Double.MAX_VALUE;
            for (DrawnSite s : mines) {
                double mx = (s.x0() + s.x1()) / 2.0 - cx, my = (s.y0() + s.y1()) / 2.0 - cy;
                best = Math.min(best, mx * mx + my * my);
            }
            if (mines.isEmpty()) {
                double mx = (plan.railX + 0.5) - cx, my = (plan.railY + 0.5) - cy;
                best = mx * mx + my * my;
            }
            key[k] = best;
        }
        Arrays.sort(ks, (a, b) -> key[a] != key[b] ? Double.compare(key[a], key[b]) : Integer.compare(a, b));
        int[] o = new int[n];
        for (int i = 0; i < n; i++) o[i] = ks[i];
        return o;
    }

    /**
     * Shares `amount` road plots over the road tiles by weight w, each to its
     * cap (none with caps null): whole plots by largest remainder, the taken
     * order breaking ties; what the caps leave over is returned unshared. Into
     * total; the plots given.
     */
    private static long shareOut(double[] w, long[] cap, long amount, int[] total, RoadTiles rt) {
        long given = 0;
        boolean[] full = new boolean[TILES];
        for (int round = 0; round < TILES && amount - given > 0; round++) {
            double sum = 0;
            for (int m = 0; m < rt.n; m++) { int k = rt.order[m]; if (!full[k]) sum += w[k]; }
            if (sum <= 0) {
                // No weight left: the taken order.
                for (int m = 0; m < rt.n && amount - given > 0; m++) {
                    int k = rt.order[m];
                    if (full[k]) continue;
                    long take = cap == null ? amount - given : Math.min(amount - given, cap[k]);
                    if (take <= 0) continue;
                    total[k] += (int) take;
                    if (cap != null) cap[k] -= take;
                    given += take;
                }
                break;
            }
            long want = amount - given, round0 = given;
            double[] rem = new double[TILES];
            boolean capped = false;
            for (int m = 0; m < rt.n; m++) {
                int k = rt.order[m];
                if (full[k]) continue;
                double q = want * w[k] / sum;
                long whole = (long) Math.floor(q);
                if (cap != null && whole >= cap[k]) { whole = cap[k]; full[k] = true; capped = true; }
                rem[k] = full[k] ? -1 : q - whole;
                total[k] += (int) whole;
                if (cap != null) cap[k] -= whole;
                given += whole;
            }
            if (!capped) {
                // The remainders, largest first, the taken order on a tie.
                long extra = want - (given - round0);
                Integer[] byRem = new Integer[rt.n];
                for (int m = 0; m < rt.n; m++) byRem[m] = m;
                Arrays.sort(byRem, (a, b) -> rem[rt.order[a]] != rem[rt.order[b]] ? Double.compare(rem[rt.order[b]], rem[rt.order[a]]) : Integer.compare(a, b));
                for (int m = 0; m < rt.n && extra > 0; m++) {
                    int k = rt.order[byRem[m]];
                    if (full[k] || (cap != null && cap[k] <= 0)) continue;
                    total[k]++;
                    if (cap != null) cap[k]--;
                    given++;
                    extra--;
                }
                break;
            }
        }
        return given;
    }

    /* =====================================================================
       THE NETWORK: EACH DISTRICT'S ROAD TILES, HIGHWAYS AND RAILWAY
       (0.7.72, batch N3)

       Jerus, "to make the map generation prettier": highways connect to
       each other, prefer straight lines and curve only when they hit the
       ocean, with T and + junctions used very sparingly; ordinary roads
       "love being a continuous + junction"; all roads connect to one network
       and no road stands alone; every building is near a road; rail connects
       to the rail network and its terminals prefer to sit near mines.

       A district's road plots are laid on its ROAD TILES: a connected set
       grown from the tile nearest the founding site - the tiles a mine or
       well stands on joined first, by the shortest way over owned ground,
       then the rest nearest the founding site first - while the main streets
       joining them (TilePainter's MAINS: the hub and an arm to each road
       neighbour, along the main lines) cost no more than MAINS_SHARE of the
       district's road. A district's first road tile is the one nearest the
       founding site, beside the district before it, whose road tiles the
       main streets cross the shared edge into: one network, district to
       district. The set hangs on the district's road plots and its mines
       alone - never on its buildings - so one more house moves one tile's
       count by one (MapCheck 2), as before.

       THE PLAN lays the district's highways and railway lines plot for plot:
       a highway from the first road tile's hub along its main row, both ways
       (away from the founding site first), so the highways of districts in a
       row lie on one line; where the sea, the city's edge, a mine or water
       wider than it may bridge stops it, it turns and runs on straight, at
       most TURNS_MOST times; both ways spent, it runs across its hub once (the
       one + it makes); it stops short of the district's edge unless it meets
       it on the main line, where the next district's runs on. The railway
       likewise, along its tile's rail column (TilePainter.railX(): the edge
       away from the tile's main, so a yard fits whole beside it) from the
       tile with the most mines, so a rail yard - dealt
       to the track's tile nearest a mine - stands near the mines; it passes
       a highway by as it passes the sea. The plan reads the tiles' ground and
       ownership as the painter does (World.tileTerrain(), the grid's leaves).
       ===================================================================== */

    /** At most this share of a district's road plots joins its road tiles to one another as main streets: a half, so at least the other half is the grid's streets among its buildings (star N3-4). */
    static final double MAINS_SHARE = 0.5;

    /** A tile is taken among the road tiles only with at least this many free plots: a block's at the junction floor, 7 x 7 = 49 - fewer is a sliver at the land's edge a main street would only cross. */
    static final int ROAD_TILE_LEAST = (TilePainter.JUNCTION_APART - 1) * (TilePainter.JUNCTION_APART - 1);

    /** A road tile's streets are dealt, while another road tile has room, no more than its grid's plots on its own dry ground and this share of them more for its lanes: 1.05 - a whole tile's grid is 240 plots on its 4 lines each way, and its lanes, three a segment (TilePainter.LANE_FEET) across a block of LANE_MOST plots, each block's shared by its two sides, 252. */
    static final double LANES_TOO = TilePainter.SEGMENTS * TilePainter.LANE_FEET.length * TilePainter.LANE_MOST / 2.0 / 240;

    /** A road tile's grid and lanes hold this share of the plots the estimate gives them (its grid and lanes over its free plots, of the plots its large buildings leave): 0.66, as measured where the painter filled - on Jerus's city x 1 and x 10,000, 5,534 of 8,448 and 32.9 of 47.4 million (a lane is not laid where it would touch another road, nor a segment a building covers); past it, the painter's fill makes plazas (star N3-8). */
    static final double LINES_HOLD = 0.66;

    /** A tile is dealt buildings, while another tile has room, to no more than this share of its free plots with its roads and track: 0.9 - a tenth kept for the shapes its buildings come in, which no tile at 95% packed whole (MapCheck 6: the dense screen drew 16.7% of its buildings smaller at 0.7.71, every core tile dealt full); the rest of a district's buildings, where it is fuller, as before (star N3-7). */
    static final double TILE_FULL_MOST = 0.9;

    /** A road tile is dealt streets, while another road tile has room, to no more than this many times its district's road over its road tiles' free plots: 1.25 - so the streets lean toward the founding site as the buildings that follow people do, a quarter over the district's share at most, and a crowded tile at the core is not given road its grid and lanes cannot hold (star N3-5). */
    static final double ROAD_LEAN_MOST = 1.25;

    /** How many times a highway or a railway line turns, each way from its hub, where the sea, the city's edge or a mine stops it: 4 at most - four turns the same way go round a square, so a run that needs more is coiling, not going anywhere (star N3-10). */
    static final int TURNS_MOST = 4;

    /** A district's road tiles: the connected set its main streets join, the order they were taken in, and each tile's hub and main street to each edge's crossing, on its own ground. */
    static final class RoadTiles {
        long stamp;
        /** The map's changes() and land version when it was last found current. */
        long changesSeen = -1, landSeen = -1;
        final boolean[] road = new boolean[TILES];
        final int[] order = new int[TILES];
        int n;
        /** Each tile's hub, a plot of the tile (y x 32 + x), or -1: where its main streets meet - the main lines' crossing, or the dry owned plot nearest it. */
        final int[] hub = new int[TILES];
        /** Each tile's main street from its hub to its crossing on each edge (north, east, south, west): its plots from the hub out, ending on the edge; null where none can be laid. */
        final short[][][] arm = new short[TILES][4][];
        /** Each tile's grid plots on its own dry ground: the streets its grid can hold (TilePainter.LINES). */
        final int[] grid = new int[TILES];
    }

    private final Map<District, RoadTiles> roadTiles = new LinkedHashMap<>(TILE_COUNTS_KEPT * 8, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<District, RoadTiles> e) { return size() > TILE_COUNTS_KEPT * 4; }
    };

    /** The tile beside k in direction dir in the same district, or -1. */
    static int besideIn(int k, int dir) {
        int x = k % TILES_A_SIDE + TilePainter.DX[dir], y = k / TILES_A_SIDE + TilePainter.DY[dir];
        return x < 0 || y < 0 || x >= TILES_A_SIDE || y >= TILES_A_SIDE ? -1 : y * TILES_A_SIDE + x;
    }

    /** The district beside d in direction dir and the tile there that k of d faces across their edge, {district or null, tile}; for a tile inside d, {d, the tile}. */
    private Object[] across(District d, int k, int dir) {
        int b = besideIn(k, dir);
        if (b >= 0) return new Object[] { d, b };
        District o = byKey.get(key(d.dx + TilePainter.DX[dir], d.dy + TilePainter.DY[dir]));
        int x = Math.floorMod(k % TILES_A_SIDE + TilePainter.DX[dir], TILES_A_SIDE), y = Math.floorMod(k / TILES_A_SIDE + TilePainter.DY[dir], TILES_A_SIDE);
        return new Object[] { o, y * TILES_A_SIDE + x };
    }

    /** Whether tile k of d and the tile it faces in direction dir are joined by main streets: both road tiles, each with its main street to their shared crossing. */
    boolean joined(District d, RoadTiles rt, int k, int dir) {
        if (!rt.road[k] || rt.arm[k][dir] == null) return false;
        Object[] o = across(d, k, dir);
        District od = (District) o[0];
        int b = (Integer) o[1];
        if (od == null) return false;
        RoadTiles ort = od == d ? rt : roadTilesOf(od);
        return ort.road[b] && ort.arm[b][(dir + 2) % 4] != null;
    }

    /** A road tile's main streets' plots: its hub and its arm to each tile it is joined to (joined()). */
    int mainsOf(District d, RoadTiles rt, int k) {
        if (!rt.road[k]) return 0;
        int n = 1;
        for (int dir = 0; dir < 4; dir++) if (joined(d, rt, k, dir)) n += rt.arm[k][dir].length;
        return n;
    }

    /** A district's road tiles (THE NETWORK), kept against its road plots, its mines and the land. */
    RoadTiles roadTilesOf(District d) {
        // Nothing the map holds has changed since it was last asked: as it was (a screen asks every tile's neighbours).
        RoadTiles seen = roadTiles.get(d);
        if (seen != null && seen.changesSeen == changes && seen.landSeen == landVersion) return seen;
        long budget = d.usedRoad;
        long x0 = (baseDX + d.dx) * DISTRICT, y0 = (baseDY + d.dy) * DISTRICT;
        List<DrawnSite> mines = budget > 0 ? minesNear(d) : new ArrayList<>();
        List<Integer> mineTiles = new ArrayList<>();
        for (DrawnSite s : mines) {
            long cx = (s.x0() + s.x1()) / 2, cy = (s.y0() + s.y1()) / 2;
            if (cx < x0 || cy < y0 || cx >= x0 + DISTRICT || cy >= y0 + DISTRICT) continue;
            int k = (int) ((cy - y0) / World.TILE * TILES_A_SIDE + (cx - x0) / World.TILE);
            if (!mineTiles.contains(k)) mineTiles.add(k);
        }
        long stamp = World.mix(World.mix(landVersion ^ 0x70ADL) ^ budget);
        for (DrawnSite s : mines) stamp = World.mix(stamp ^ s.x0() ^ (s.y0() << 20));
        RoadTiles got = roadTiles.get(d);
        if (got != null && got.stamp == stamp) {
            got.changesSeen = changes;
            got.landSeen = landVersion;
            return got;
        }
        RoadTiles rt = new RoadTiles();
        rt.stamp = stamp;
        rt.changesSeen = changes;
        rt.landSeen = landVersion;
        Arrays.fill(rt.hub, -1);
        roadTiles.put(d, rt);
        if (budget <= 0) return rt;
        short[] free = tileFree(d);
        PlanGround g = new PlanGround(d, mines);
        long tx0 = x0 / World.TILE, ty0 = y0 / World.TILE;
        // Every district's main streets may bridge a river since 0.7.77, gravel as paved (TilePainter.STREET_BRIDGE; Jerus:
        // "gravel road bridge rivers sure") - only a district with paved road until then, so a district of gravel parted
        // at its river drew its streets across it as a network apart.
        for (int k = 0; k < TILES; k++) {
            if (free[k] <= 0) continue;
            rt.hub[k] = hubOf(g, k, tx0, ty0);
            if (rt.hub[k] < 0) continue;
            for (int dir = 0; dir < 4; dir++) rt.arm[k][dir] = armOf(g, k, dir, rt.hub[k], tx0, ty0);
            int bx = (k % TILES_A_SIDE) * World.TILE, by = (k / TILES_A_SIDE) * World.TILE;
            for (int y = TilePainter.STREET_AT; y <= TilePainter.LINES[TilePainter.LINES.length - 1]; y++) {
                for (int x = TilePainter.STREET_AT; x <= TilePainter.LINES[TilePainter.LINES.length - 1]; x++) {
                    boolean line = (x - TilePainter.STREET_AT) % TilePainter.JUNCTION_APART == 0 || (y - TilePainter.STREET_AT) % TilePainter.JUNCTION_APART == 0;
                    if (line && dryAt(g, bx + x, by + y)) rt.grid[k]++;
                }
            }
        }
        double[] lean = new double[TILES];
        for (int k = 0; k < TILES; k++) lean[k] = coreLean(tx0 + k % TILES_A_SIDE, ty0 + k / TILES_A_SIDE);
        int root = -1;
        for (int pass = 0; pass < 2 && root < 0; pass++) {
            for (int k = 0; k < TILES; k++) {
                if (rt.hub[k] < 0 || free[k] < (pass == 0 ? ROAD_TILE_LEAST : 1)) continue;
                if (root < 0 || lean[k] > lean[root]) root = k;
            }
        }
        if (root < 0) return rt;
        double most = budget * MAINS_SHARE;
        long cost = 1;
        take(rt, root);
        // The mines' tiles first, each by the shortest way over tiles whose main streets join.
        for (int m : mineTiles) {
            if (rt.road[m]) continue;
            int[] from = new int[TILES];
            Arrays.fill(from, -2);
            int[] q = new int[TILES];
            int qh = 0, qt = 0;
            for (int k = 0; k < TILES; k++) if (rt.road[k]) { from[k] = -1; q[qt++] = k; }
            while (qh < qt && from[m] == -2) {
                int k = q[qh++];
                for (int dir = 0; dir < 4; dir++) {
                    int b = besideIn(k, dir);
                    if (b < 0 || from[b] != -2 || !armsMeet(rt, k, dir)) continue;
                    from[b] = k;
                    q[qt++] = b;
                }
            }
            if (from[m] == -2) continue;
            List<Integer> path = new ArrayList<>();
            for (int k = m; from[k] != -1; k = from[k]) path.add(0, k);
            long add = 0;
            boolean[] would = rt.road.clone();
            for (int k : path) { add += joinCost(rt, would, k); would[k] = true; }
            if (cost + add > most) continue;
            for (int k : path) take(rt, k);
            cost += add;
        }
        // Then the rest, nearest the founding site first, while the mains cost no more than MAINS_SHARE of the road:
        // a tile whose main streets meet a road tile's; and where none is left, a tile of the district's ground that
        // meets none of them - a piece its land joins to the rest only through the next district (the hub alone).
        while (true) {
            int best = -1;
            boolean joins = false;
            for (int k = 0; k < TILES; k++) {
                if (rt.road[k] || free[k] < ROAD_TILE_LEAST || rt.hub[k] < 0) continue;
                boolean touches = false;
                for (int dir = 0; dir < 4; dir++) {
                    int b = besideIn(k, dir);
                    if (b >= 0 && rt.road[b] && armsMeet(rt, b, (dir + 2) % 4)) touches = true;
                }
                if (touches != joins && best >= 0) { if (!touches) continue; best = -1; }
                if (best < 0 || lean[k] > lean[best]) { best = k; joins = touches; }
            }
            if (best < 0) break;
            long add = joinCost(rt, rt.road, best);
            if (cost + add > most) break;
            take(rt, best);
            cost += add;
        }
        return rt;
    }

    /** The mines and wells on the sites of the districts about d: what no road crosses and the road tiles reach first. */
    List<DrawnSite> minesNear(District d) {
        List<DrawnSite> out = new ArrayList<>();
        for (int b = -1; b <= 1; b++) {
            for (int a = -1; a <= 1; a++) {
                if (byKey.get(key(d.dx + a, d.dy + b)) == null) continue;
                for (DrawnSite site : siteList(d.dx + a, d.dy + b)) if (site.mine() >= 0) out.add(site);
            }
        }
        return out;
    }

    /** Whether tile k's arm toward dir and the arm of the tile there back toward it, both in the district, are both laid. */
    private static boolean armsMeet(RoadTiles rt, int k, int dir) {
        int b = besideIn(k, dir);
        return b >= 0 && rt.arm[k][dir] != null && rt.arm[b][(dir + 2) % 4] != null;
    }

    private static void take(RoadTiles rt, int k) {
        rt.road[k] = true;
        rt.order[rt.n++] = k;
    }

    /** What joining tile k to the road tiles `road` costs in main streets: its hub and both arms to each road neighbour in the district whose arms meet it. */
    private static long joinCost(RoadTiles rt, boolean[] road, int k) {
        long c = 1;
        for (int dir = 0; dir < 4; dir++) {
            int b = besideIn(k, dir);
            if (b >= 0 && road[b] && armsMeet(rt, k, dir)) c += rt.arm[k][dir].length + rt.arm[b][(dir + 2) % 4].length;
        }
        return c;
    }

    /** A plot a main street may take: owned, dry, no mine's. */
    private static boolean dryAt(PlanGround g, int x, int y) {
        int gr = g.ground(x, y);
        return gr >= 0 && gr != World.SALT && gr != World.FRESH && !g.mined(x, y);
    }

    /** Tile k's hub: its main lines' crossing if a main street may take it, else the plot nearest it that can (breadth first over the tile) - inside the tile's edge ring where any can (0.7.72: a hub on the edge had no way in), else on it; -1 for none. A plot of the tile. */
    private int hubOf(PlanGround g, int k, long tx0, long ty0) {
        int bx = (k % TILES_A_SIDE) * World.TILE, by = (k / TILES_A_SIDE) * World.TILE;
        int at = TilePainter.mainY(seed, ty0 + k / TILES_A_SIDE) * World.TILE + TilePainter.mainX(seed, tx0 + k % TILES_A_SIDE);
        boolean[] seen = new boolean[TilePainter.PLOTS];
        int[] q = new int[TilePainter.PLOTS];
        int qh = 0, qt = 0;
        q[qt++] = at;
        seen[at] = true;
        int ring = -1;
        while (qh < qt) {
            int i = q[qh++];
            int ix = i % World.TILE, iy = i / World.TILE;
            if (dryAt(g, bx + ix, by + iy)) {
                if (ix > 0 && iy > 0 && ix < World.TILE - 1 && iy < World.TILE - 1) return i;
                if (ring < 0) ring = i;
            }
            for (int dir = 0; dir < 4; dir++) {
                int x = i % World.TILE + TilePainter.DX[dir], y = i / World.TILE + TilePainter.DY[dir];
                if (x < 0 || y < 0 || x >= World.TILE || y >= World.TILE || seen[y * World.TILE + x]) continue;
                seen[y * World.TILE + x] = true;
                q[qt++] = y * World.TILE + x;
            }
        }
        return ring;
    }

    /**
     * Tile k's main street toward dir: to its crossing on that edge - the
     * plot along the edge nearest the main line where it and the plot across
     * the edge, and since 0.7.72 the plot inside each, are all owned, dry and
     * no mine's, read the same from either
     * side - straight along the main line where it can run so, else the
     * shortest way over the tile's dry ground inside its edge ring (0.7.72:
     * along the edge, a street met the next tile's border where nothing of
     * the next tile's met it), else the shortest that bridges fresh water
     * (bridgedArm()); its plots from the hub out, ending on the edge, or
     * null where there is no crossing or no way. Straight or bridged, its
     * water no wider than TilePainter.STREET_BRIDGE at a stretch, in any
     * district since 0.7.77 (one with paved road before).
     */
    private short[] armOf(PlanGround g, int k, int dir, int hub, long tx0, long ty0) {
        int bx = (k % TILES_A_SIDE) * World.TILE, by = (k / TILES_A_SIDE) * World.TILE;
        boolean ns = dir == 0 || dir == 2;
        int main = ns ? TilePainter.mainX(seed, tx0 + k % TILES_A_SIDE) : TilePainter.mainY(seed, ty0 + k / TILES_A_SIDE);
        int edge = dir == 0 || dir == 3 ? 0 : World.TILE - 1;
        int cross = -1;
        for (int off = 0; off < World.TILE && cross < 0; off++) {
            for (int side = 1; side >= -1 && cross < 0; side -= 2) {
                if (off == 0 && side < 0) continue;
                int along = main + side * off;
                if (along < 0 || along >= World.TILE) continue;
                int x = ns ? along : edge, y = ns ? edge : along;
                // The edge's plot and the one inside it, and the two across - a street enters each side straight, and
                // either tile reads the same four.
                if (!dryAt(g, bx + x, by + y) || !dryAt(g, bx + x - TilePainter.DX[dir], by + y - TilePainter.DY[dir])) continue;
                if (!g.dryAbs(g.x0 + bx + x + TilePainter.DX[dir], g.y0 + by + y + TilePainter.DY[dir])) continue;
                if (!g.dryAbs(g.x0 + bx + x + 2 * TilePainter.DX[dir], g.y0 + by + y + 2 * TilePainter.DY[dir])) continue;
                cross = y * World.TILE + x;
            }
        }
        if (cross < 0) return null;
        int hx = hub % World.TILE, hy = hub / World.TILE, cx = cross % World.TILE, cy = cross / World.TILE;
        // Straight along the main line, from the hub to the edge.
        if ((ns ? hx == cx : hy == cy)) {
            int len = ns ? Math.abs(cy - hy) : Math.abs(cx - hx);
            short[] path = new short[len];
            boolean ok = true;
            int water = 0;
            for (int m = 1; m <= len && ok; m++) {
                int x = hx + TilePainter.DX[dir] * m, y = hy + TilePainter.DY[dir] * m;
                // ...over fresh water on a bridge, where the water is narrow enough (since 0.7.77 gravel's as paved's).
                if (g.ground(bx + x, by + y) == World.FRESH && !g.mined(bx + x, by + y)) {
                    ok = ++water <= TilePainter.STREET_BRIDGE;
                } else {
                    water = 0;
                    ok = dryAt(g, bx + x, by + y);
                }
                path[m - 1] = (short) (y * World.TILE + x);
            }
            if (ok && water == 0) return path;
        }
        // The shortest way over the tile's dry ground, inside its edge ring but for the crossing itself: a main street
        // meets an edge only where it crosses it, so a street that leaves a tile goes on in the next.
        int[] from = new int[TilePainter.PLOTS];
        Arrays.fill(from, -2);
        int[] q = new int[TilePainter.PLOTS];
        int qh = 0, qt = 0;
        q[qt++] = hub;
        from[hub] = -1;
        while (qh < qt && from[cross] == -2) {
            int i = q[qh++];
            for (int d2 = 0; d2 < 4; d2++) {
                int x = i % World.TILE + TilePainter.DX[d2], y = i / World.TILE + TilePainter.DY[d2];
                if (x < 0 || y < 0 || x >= World.TILE || y >= World.TILE) continue;
                int j = y * World.TILE + x;
                if (from[j] != -2 || !dryAt(g, bx + x, by + y)) continue;
                if (j != cross && (x == 0 || y == 0 || x == World.TILE - 1 || y == World.TILE - 1)) continue;
                from[j] = i;
                q[qt++] = j;
            }
        }
        if (from[cross] == -2) return bridgedArm(g, bx, by, hub, cross);
        int len = 0;
        for (int i = cross; i != hub; i = from[i]) len++;
        short[] path = new short[len];
        for (int i = cross, m = len - 1; i != hub; i = from[i], m--) path[m] = (short) i;
        return path;
    }

    /**
     * An arm with no way over dry ground (0.7.72): the shortest way inside the
     * tile's edge ring that crosses fresh water as a main street may - on a
     * bridge of at most TilePainter.STREET_BRIDGE plots at a stretch, paved
     * or since 0.7.77 gravel - so a river through a district parts its
     * streets no more than its straight mains; null for none. Walked plot by
     * plot with the water crossed so far.
     */
    private short[] bridgedArm(PlanGround g, int bx, int by, int hub, int cross) {
        int most = TilePainter.STREET_BRIDGE, states = most + 1;
        int[] from = new int[TilePainter.PLOTS * states];
        Arrays.fill(from, -2);
        int[] q = new int[TilePainter.PLOTS * states];
        int qh = 0, qt = 0;
        q[qt++] = hub * states;
        from[hub * states] = -1;
        int end = -1;
        while (qh < qt && end < 0) {
            int st = q[qh++], i = st / states, run = st % states;
            for (int d2 = 0; d2 < 4 && end < 0; d2++) {
                int x = i % World.TILE + TilePainter.DX[d2], y = i / World.TILE + TilePainter.DY[d2];
                if (x < 0 || y < 0 || x >= World.TILE || y >= World.TILE) continue;
                int j = y * World.TILE + x;
                if (j != cross && (x == 0 || y == 0 || x == World.TILE - 1 || y == World.TILE - 1)) continue;
                int next;
                if (dryAt(g, bx + x, by + y)) next = 0;
                else if (g.ground(bx + x, by + y) == World.FRESH && !g.mined(bx + x, by + y) && run < most) next = run + 1;
                else continue;
                int ns = j * states + next;
                if (from[ns] != -2) continue;
                from[ns] = st;
                q[qt++] = ns;
                if (j == cross) end = ns;
            }
        }
        if (end < 0) return null;
        int len = 0;
        for (int s = end; s / states != hub || from[s] != -1; s = from[s]) len++;
        short[] path = new short[len];
        for (int s = end, m = len - 1; m >= 0; s = from[s], m--) path[m] = (short) (s / states);
        return path;
    }

    /** A district's plan: its highways' and railway's plots, tile by tile, and their runs. */
    static final class Plan {
        final int[] hw = new int[TILES], rail = new int[TILES], mains = new int[TILES], bridges = new int[TILES];
        /** Each tile's runs, five numbers each: x0, y0, x1, y1 in the tile's plots, and the kind (HIGHWAY or TilePainter.RAIL); null for none. */
        final int[][] segments = new int[TILES][];
        long hwLeft, railLeft;
        /** The railway's first plot, in the world's plots. */
        long railX, railY;
    }

    /** The plan's ground, a tile at a time as it is asked for: its terrain, ownership and the mines' sites on it - the district's tiles, and the tiles about it a crossing looks across into. */
    private final class PlanGround {
        final District d;
        final long x0, y0;
        final byte[][] terrain = new byte[TILES][];
        final boolean[][] owned = new boolean[TILES][];
        final byte[] laid = new byte[DISTRICT * DISTRICT];
        final List<DrawnSite> mines;
        final Map<Long, Object[]> outside = new HashMap<>();
        PlanGround(District d, List<DrawnSite> mines) {
            this.d = d;
            this.x0 = (baseDX + d.dx) * DISTRICT;
            this.y0 = (baseDY + d.dy) * DISTRICT;
            this.mines = mines;
        }
        void load(int k) {
            if (terrain[k] != null) return;
            long tx = x0 / World.TILE + k % TILES_A_SIDE, ty = y0 / World.TILE + k / TILES_A_SIDE;
            terrain[k] = new byte[TilePainter.PLOTS];
            owned[k] = new boolean[TilePainter.PLOTS];
            World.of(seed).tileTerrain(tx, ty, terrain[k]);
            tileOwnership(tx, ty, owned[k]);
        }
        /** The ground at district plot (x, y): a World class, or -1 where the city does not own it. */
        int ground(int x, int y) {
            int k = (y / World.TILE) * TILES_A_SIDE + x / World.TILE;
            load(k);
            int i = (y % World.TILE) * World.TILE + x % World.TILE;
            return owned[k][i] ? terrain[k][i] : -1;
        }
        /** Whether the world's plot (ax, ay), in the district or past it, is owned, dry and no mine's - read from its tile as the painter reads it. */
        boolean dryAbs(long ax, long ay) {
            if (ax >= x0 && ay >= y0 && ax < x0 + DISTRICT && ay < y0 + DISTRICT) {
                int gr = ground((int) (ax - x0), (int) (ay - y0));
                return gr >= 0 && gr != World.SALT && gr != World.FRESH && !minedAbs(ax, ay);
            }
            long tx = Math.floorDiv(ax, World.TILE), ty = Math.floorDiv(ay, World.TILE);
            Object[] t = outside.computeIfAbsent((tx << 32) ^ (ty & 0xffffffffL), key -> {
                byte[] ter = new byte[TilePainter.PLOTS];
                boolean[] own = new boolean[TilePainter.PLOTS];
                World.of(seed).tileTerrain(tx, ty, ter);
                tileOwnership(tx, ty, own);
                return new Object[] { ter, own };
            });
            int i = (int) (Math.floorMod(ay, World.TILE) * World.TILE + Math.floorMod(ax, World.TILE));
            byte gr = ((byte[]) t[0])[i];
            return ((boolean[]) t[1])[i] && gr != World.SALT && gr != World.FRESH && !minedAbs(ax, ay);
        }
        boolean minedAbs(long ax, long ay) {
            for (DrawnSite s : mines) if (ax >= s.x0() && ax <= s.x1() && ay >= s.y0() && ay <= s.y1()) return true;
            return false;
        }
        boolean mined(int x, int y) {
            return minedAbs(x0 + x, y0 + y);
        }
        /** Whether a run of `kind` may lay a plot at (x, y): in the district, owned, dry, no mine's, nothing laid - the track may cross a main street. */
        boolean open(int x, int y, int kind) {
            if (x < 0 || y < 0 || x >= DISTRICT || y >= DISTRICT) return false;
            int g = ground(x, y);
            byte l = laid[y * DISTRICT + x];
            boolean free = l == 0 || (kind == TilePainter.RAIL && l == TilePainter.PLAN_MAIN);
            return g >= 0 && g != World.SALT && g != World.FRESH && free && !mined(x, y);
        }
        /** Whether (x, y) is fresh water the city owns, with nothing laid. */
        boolean fresh(int x, int y) {
            if (x < 0 || y < 0 || x >= DISTRICT || y >= DISTRICT) return false;
            return ground(x, y) == World.FRESH && laid[y * DISTRICT + x] == 0 && !mined(x, y);
        }
        /** Whether a run of `kind` going dir may take (x, y): open, and the track over a main street only across it (0.7.72) - never on along the street, every plot of it a crossing. */
        boolean openGoing(int x, int y, int kind, int dir) {
            if (!open(x, y, kind)) return false;
            if (kind != TilePainter.RAIL || laid[y * DISTRICT + x] != TilePainter.PLAN_MAIN) return true;
            int ax = x + TilePainter.DX[dir], ay = y + TilePainter.DY[dir], bx = x - TilePainter.DX[dir], by = y - TilePainter.DY[dir];
            boolean ahead = ax >= 0 && ay >= 0 && ax < DISTRICT && ay < DISTRICT && (laid[ay * DISTRICT + ax] == TilePainter.PLAN_MAIN || laid[ay * DISTRICT + ax] == TilePainter.PLAN_CROSS);
            boolean behind = bx >= 0 && by >= 0 && bx < DISTRICT && by < DISTRICT && (laid[by * DISTRICT + bx] == TilePainter.PLAN_MAIN || laid[by * DISTRICT + bx] == TilePainter.PLAN_CROSS);
            return !ahead && !behind;
        }
        /** Lays a plot of `kind`: the track over a main street makes a crossing. */
        void lay(int x, int y, int kind) {
            int i = y * DISTRICT + x;
            laid[i] = (byte) (kind == TilePainter.RAIL && laid[i] == TilePainter.PLAN_MAIN ? TilePainter.PLAN_CROSS : kind);
        }
    }

    /** A run's head: where it is, which way it goes and how often it has turned. */
    private static final class Head {
        int x, y, dir, turns;
        boolean alive = true;
        Head(int x, int y, int dir) { this.x = x; this.y = y; this.dir = dir; }
    }

    /** The district's plan (THE NETWORK). */
    private Plan planOf(District d, RoadTiles rt, long highway, long track, List<DrawnSite> mines, int[] spare) {
        Plan plan = new Plan();
        plan.hwLeft = highway;
        plan.railLeft = track;
        if (highway <= 0 && track <= 0 && rt.n == 0) return plan;
        PlanGround g = new PlanGround(d, mines);
        long tx0 = g.x0 / World.TILE, ty0 = g.y0 / World.TILE;
        boolean east = (baseDX + d.dx) * DISTRICT + DISTRICT / 2 >= siteX, south = (baseDY + d.dy) * DISTRICT + DISTRICT / 2 >= siteY;
        if (highway > 0 && rt.n > 0) {
            int k = rt.order[0];
            int hx = (k % TILES_A_SIDE) * World.TILE + TilePainter.mainX(seed, tx0 + k % TILES_A_SIDE);
            int hy = (k / TILES_A_SIDE) * World.TILE + TilePainter.mainY(seed, ty0 + k / TILES_A_SIDE);
            plan.hwLeft = run(g, BuildingVisual.HIGHWAY, hx, hy, east ? 1 : 3, highway, true, tx0, ty0);
        }
        // The main streets: each road tile's hub and its arm to each tile it is joined to, here and in the next district.
        for (int m = 0; m < rt.n; m++) {
            int k = rt.order[m];
            int bx = (k % TILES_A_SIDE) * World.TILE, by = (k / TILES_A_SIDE) * World.TILE;
            int hub = rt.hub[k];
            if (g.laid[(by + hub / World.TILE) * DISTRICT + bx + hub % World.TILE] == 0) g.lay(bx + hub % World.TILE, by + hub / World.TILE, TilePainter.PLAN_MAIN);
            for (int dir = 0; dir < 4; dir++) {
                if (!joined(d, rt, k, dir)) continue;
                for (short i : rt.arm[k][dir]) {
                    int x = bx + i % World.TILE, y = by + i / World.TILE;
                    if (g.laid[y * DISTRICT + x] == 0) g.lay(x, y, TilePainter.PLAN_MAIN);
                }
            }
        }
        if (track > 0) {
            // The railway's tile: the one with the most mines' sites, the nearer the founding site on a tie; else the first road tile, else the roomiest.
            int[] minesOn = new int[TILES];
            for (DrawnSite s : mines) {
                long cx = (s.x0() + s.x1()) / 2 - g.x0, cy = (s.y0() + s.y1()) / 2 - g.y0;
                if (cx >= 0 && cy >= 0 && cx < DISTRICT && cy < DISTRICT) minesOn[(int) (cy / World.TILE) * TILES_A_SIDE + (int) (cx / World.TILE)]++;
            }
            // The tiles to start from, first choice first: those with the most mines' sites (the road tiles first among
            // equals, in the order taken), then the road tiles, then the rest by their room.
            Integer[] cand = new Integer[TILES];
            int[] rank = new int[TILES];
            Arrays.fill(rank, TILES);
            for (int m = 0; m < rt.n; m++) rank[rt.order[m]] = m;
            for (int t = 0; t < TILES; t++) cand[t] = t;
            Arrays.sort(cand, (a, b) -> minesOn[a] != minesOn[b] ? Integer.compare(minesOn[b], minesOn[a])
                    : rank[a] != rank[b] ? Integer.compare(rank[a], rank[b]) : spare[a] != spare[b] ? Integer.compare(spare[b], spare[a]) : Integer.compare(a, b));
            int rx = -1, ry = -1;
            for (int c = 0; c < TILES && ry < 0; c++) {
                int k = cand[c];
                if (spare[k] <= 0 && minesOn[k] == 0) continue;
                int x = (k % TILES_A_SIDE) * World.TILE + TilePainter.railX(seed, tx0 + k % TILES_A_SIDE), base = (k / TILES_A_SIDE) * World.TILE;
                for (int dy = 0; dy < World.TILE && ry < 0; dy++) {
                    for (int side = -1; side <= 1 && ry < 0; side += 2) {
                        int y = base + World.TILE / 2 + side * dy;
                        if (y >= base && y < base + World.TILE && g.open(x, y, TilePainter.RAIL)) { rx = x; ry = y; }
                    }
                }
            }
            if (ry >= 0) {
                plan.railX = g.x0 + rx;
                plan.railY = g.y0 + ry;
                plan.railLeft = run(g, TilePainter.RAIL, rx, ry, south ? 2 : 0, track, true, tx0, ty0);
                // Track the line could not lay: branches off the line laid, from its plots in turn, each a run across
                // it - so every plot of the city's track is drawn, on one railway.
                for (int pass = 0; pass < 2 && plan.railLeft > 0; pass++) {
                    for (int y = 0; y < DISTRICT && plan.railLeft > 0; y++) {
                        for (int x = 0; x < DISTRICT && plan.railLeft > 0; x++) {
                            byte l = g.laid[y * DISTRICT + x];
                            if (l != TilePainter.RAIL && l != TilePainter.PLAN_CROSS) continue;
                            boolean ns = (y > 0 && isTrack(g.laid[(y - 1) * DISTRICT + x])) || (y + 1 < DISTRICT && isTrack(g.laid[(y + 1) * DISTRICT + x]));
                            // Off a north-south line east and west; off an east-west one north and south (on the second pass, any way).
                            for (int dir = 0; dir < 4 && plan.railLeft > 0; dir++) {
                                if (pass == 0 && (dir == 0 || dir == 2) == ns) continue;
                                int nx = x + TilePainter.DX[dir], ny = y + TilePainter.DY[dir];
                                if (!g.open(nx, ny, TilePainter.RAIL) || atEdge(nx, ny)) continue;
                                plan.railLeft = branch(g, nx, ny, dir, plan.railLeft, tx0, ty0);
                            }
                        }
                    }
                }
            }
        }
        // Each tile's runs: east-west first, two plots or more; then north-south, the plots left.
        for (int k = 0; k < TILES; k++) {
            int bx = (k % TILES_A_SIDE) * World.TILE, by = (k / TILES_A_SIDE) * World.TILE;
            int[] segs = new int[0];
            boolean[] used = new boolean[TilePainter.PLOTS];
            for (int y = 0; y < World.TILE; y++) {
                for (int x = 0; x < World.TILE; ) {
                    byte kind = g.laid[(by + y) * DISTRICT + bx + x];
                    if (kind == 0) { x++; continue; }
                    int e = x;
                    while (e + 1 < World.TILE && g.laid[(by + y) * DISTRICT + bx + e + 1] == kind) e++;
                    if (e > x) {
                        segs = Arrays.copyOf(segs, segs.length + 5);
                        segs[segs.length - 5] = x; segs[segs.length - 4] = y; segs[segs.length - 3] = e; segs[segs.length - 2] = y; segs[segs.length - 1] = kind;
                        for (int m = x; m <= e; m++) used[y * World.TILE + m] = true;
                    }
                    x = e + 1;
                }
            }
            for (int x = 0; x < World.TILE; x++) {
                for (int y = 0; y < World.TILE; ) {
                    byte kind = g.laid[(by + y) * DISTRICT + bx + x];
                    if (kind == 0 || used[y * World.TILE + x]) { y++; continue; }
                    int e = y;
                    while (e + 1 < World.TILE && g.laid[(by + e + 1) * DISTRICT + bx + x] == kind && !used[(e + 1) * World.TILE + x]) e++;
                    segs = Arrays.copyOf(segs, segs.length + 5);
                    segs[segs.length - 5] = x; segs[segs.length - 4] = y; segs[segs.length - 3] = x; segs[segs.length - 2] = e; segs[segs.length - 1] = kind;
                    y = e + 1;
                }
            }
            for (int y = 0; y < World.TILE; y++) {
                for (int x = 0; x < World.TILE; x++) {
                    byte kind = g.laid[(by + y) * DISTRICT + bx + x];
                    if (kind == BuildingVisual.HIGHWAY) plan.hw[k]++;
                    else if (kind == TilePainter.RAIL || kind == TilePainter.PLAN_CROSS) plan.rail[k]++;
                    else if (kind == TilePainter.PLAN_MAIN) {
                        plan.mains[k]++;
                        if (g.ground(bx + x, by + y) == World.FRESH) plan.bridges[k]++;
                    }
                }
            }
            if (segs.length > 0) plan.segments[k] = segs;
        }
        return plan;
    }

    /**
     * Lays a run of `kind` (a highway, or TilePainter.RAIL) from district plot
     * (sx, sy): two heads, first `dir` and then the opposite way, a plot each
     * in turn; a head blocked by the sea, the city's edge or a mine turns
     * (left or right, the way open, toward the district's middle on a tie) at
     * most TURNS_MOST times, crosses fresh water up to the kind's bridge, and
     * stops short of the district's edge unless it meets it on its tile's
     * line (a main line for a highway, the rail column or row for the track), which the next
     * district's runs on. Both heads spent, two more across the start once
     * (`cross`). The plots left unlaid.
     */
    private long run(PlanGround g, int kind, int sx, int sy, int dir, long budget, boolean cross, long tx0, long ty0) {
        if (budget <= 0 || !g.open(sx, sy, kind)) return budget;
        g.lay(sx, sy, kind);
        budget--;
        int bridgeMost = kind == TilePainter.RAIL ? TilePainter.RAIL_BRIDGE : TilePainter.MAX_BRIDGE[BuildingVisual.HIGHWAY];
        for (int round = 0; round < (cross ? 2 : 1) && budget > 0; round++) {
            int d0 = round == 0 ? dir : (dir + 1) % 4;
            Head[] heads = { new Head(sx, sy, d0), new Head(sx, sy, (d0 + 2) % 4) };
            int turn = 0;
            while (budget > 0 && (heads[0].alive || heads[1].alive)) {
                Head h = heads[turn];
                turn = 1 - turn;
                if (!h.alive) continue;
                int nx = h.x + TilePainter.DX[h.dir], ny = h.y + TilePainter.DY[h.dir];
                // The district's edge stops it, but on its tile's line, which the next district's run goes on along.
                if (!inside(nx, ny) || (atEdge(nx, ny) && !onLine(kind, nx, ny, h.dir, tx0, ty0))) { h.alive = false; continue; }
                {
                    if (g.openGoing(nx, ny, kind, h.dir)) {
                        g.lay(nx, ny, kind);
                        budget--;
                        h.x = nx;
                        h.y = ny;
                        continue;
                    }
                    if (g.fresh(nx, ny)) {
                        // A bridge: the water's width along the way, and the dry plot past it.
                        int w = 0;
                        while (w <= bridgeMost && g.fresh(nx + TilePainter.DX[h.dir] * w, ny + TilePainter.DY[h.dir] * w)) w++;
                        int lx = nx + TilePainter.DX[h.dir] * w, ly = ny + TilePainter.DY[h.dir] * w;
                        if (w <= bridgeMost && w + 1 <= budget && g.openGoing(lx, ly, kind, h.dir) && (onLine(kind, lx, ly, h.dir, tx0, ty0) || !atEdge(lx, ly))) {
                            for (int m = 0; m <= w; m++) g.lay(nx + TilePainter.DX[h.dir] * m, ny + TilePainter.DY[h.dir] * m, kind);
                            budget -= w + 1;
                            h.x = lx;
                            h.y = ly;
                            continue;
                        }
                    }
                }
                // Blocked by the sea, the city's edge, a mine or a run laid: turn, the way that is open, toward the
                // district's middle on a tie.
                if (h.turns >= TURNS_MOST) { h.alive = false; continue; }
                int a = (h.dir + 1) % 4, b = (h.dir + 3) % 4;
                boolean oa = g.openGoing(h.x + TilePainter.DX[a], h.y + TilePainter.DY[a], kind, a) && !atEdge(h.x + TilePainter.DX[a], h.y + TilePainter.DY[a]);
                boolean ob = g.openGoing(h.x + TilePainter.DX[b], h.y + TilePainter.DY[b], kind, b) && !atEdge(h.x + TilePainter.DX[b], h.y + TilePainter.DY[b]);
                if (!oa && !ob) { h.alive = false; continue; }
                int pick;
                if (oa && ob) {
                    double da = Math.abs(h.x + TilePainter.DX[a] * 8 - DISTRICT / 2.0) + Math.abs(h.y + TilePainter.DY[a] * 8 - DISTRICT / 2.0);
                    double db = Math.abs(h.x + TilePainter.DX[b] * 8 - DISTRICT / 2.0) + Math.abs(h.y + TilePainter.DY[b] * 8 - DISTRICT / 2.0);
                    pick = da <= db ? a : b;
                } else pick = oa ? a : b;
                h.dir = pick;
                h.turns++;
            }
        }
        return budget;
    }

    /** Whether a plan plot is track. */
    private static boolean isTrack(byte l) {
        return l == TilePainter.RAIL || l == TilePainter.PLAN_CROSS;
    }

    /** A branch of track from district plot (x, y), open and beside the line, going dir: straight on while the ground is open and track is left, short of the district's edge and of any other track but the line it leaves. The plots left unlaid. */
    private long branch(PlanGround g, int x, int y, int dir, long budget, long tx0, long ty0) {
        int cx = x, cy = y;
        while (budget > 0 && g.openGoing(cx, cy, TilePainter.RAIL, dir) && !atEdge(cx, cy)) {
            // Touching no track but the plot it comes from.
            boolean clear = true;
            for (int k = 0; k < 4; k++) {
                int ax = cx + TilePainter.DX[k], ay = cy + TilePainter.DY[k];
                if (ax == cx - TilePainter.DX[dir] && ay == cy - TilePainter.DY[dir]) continue;
                if (inside(ax, ay) && isTrack(g.laid[ay * DISTRICT + ax])) clear = false;
            }
            if (!clear) break;
            g.lay(cx, cy, TilePainter.RAIL);
            budget--;
            cx += TilePainter.DX[dir];
            cy += TilePainter.DY[dir];
        }
        return budget;
    }

    /** Whether district plot (x, y) is inside the district. */
    private static boolean inside(int x, int y) {
        return x >= 0 && y >= 0 && x < DISTRICT && y < DISTRICT;
    }

    /** Whether district plot (x, y) is on the district's edge ring. */
    private static boolean atEdge(int x, int y) {
        return x <= 0 || y <= 0 || x >= DISTRICT - 1 || y >= DISTRICT - 1;
    }

    /** Whether a run of `kind` going `dir` at district plot (x, y) is on its tile's line, where it may meet the district's edge: a highway on the main line it runs along, the track on its rail column or row (TilePainter.railX(), railY()). */
    private boolean onLine(int kind, int x, int y, int dir, long tx0, long ty0) {
        if (!inside(x, y)) return false;
        boolean ns = dir == 0 || dir == 2;
        int at = ns ? x % World.TILE : y % World.TILE;
        if (kind == TilePainter.RAIL) return at == (ns ? TilePainter.railX(seed, tx0 + x / World.TILE) : TilePainter.railY(seed, ty0 + y / World.TILE));
        return at == (ns ? TilePainter.mainX(seed, tx0 + x / World.TILE) : TilePainter.mainY(seed, ty0 + y / World.TILE));
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

    /** Whether a tile is one of its district's road tiles (0.7.72, THE NETWORK): what its neighbours' main streets run to the port for. */
    boolean tileHasRoads(long tx, long ty) {
        District d = districtOfTile(tx, ty);
        if (d == null) return false;
        return roadTilesOf(d).road[(int) (Math.floorMod(ty, TILES_A_SIDE) * TILES_A_SIDE + Math.floorMod(tx, TILES_A_SIDE))];
    }

    /** A tile's railway plots from the deal (0.7.72). */
    public int tileRail(long tx, long ty) {
        District d = districtOfTile(tx, ty);
        if (d == null) return 0;
        return dealtOf(d).rail[(int) (Math.floorMod(ty, TILES_A_SIDE) * TILES_A_SIDE + Math.floorMod(tx, TILES_A_SIDE))];
    }

    /** The railway's plots a district's plan found no way for (0.7.72). */
    public long railShort(District d) {
        return dealtOf(d).railShort;
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
        // The plan's highways and railway through it (0.7.72).
        in.clearPlans();
        District pd = districtOfTile(tx, ty);
        if (pd != null) {
            int[] segs = dealtOf(pd).plan[(int) (Math.floorMod(ty, TILES_A_SIDE) * TILES_A_SIDE + Math.floorMod(tx, TILES_A_SIDE))];
            if (segs != null) for (int s = 0; s + 4 < segs.length; s += 5) in.addPlan(segs[s], segs[s + 1], segs[s + 2], segs[s + 3], segs[s + 4]);
        }
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
