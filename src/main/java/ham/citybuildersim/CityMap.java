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
 * The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, each district's street plan kept for the painter with what it cannot hold carried to the next, the city's highways and railway laid on corridors across it, one drawn building for each the model has, summed up a pyramid, each built district's picture drawn with its plan for the far view (0.7.128), and written beside the save as a sidecar - what the tile painter paints the city from.
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
 * plots); its owned sites of each SITED resource's types of field (iron's
 * and oil's until 0.8.0: SLOTS); and its count of every type.
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
 * districts holding those sites - since 0.8.0 every works on a SITED
 * resource, by the type of its field and on as many sites as it takes
 * (THE WORKS ON THEIR SITES BY TYPE).
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
 * changes: batch J4's for the far view, which since 0.7.128 draws from the
 * district pictures instead (MapCheck holds the pyramid's sums).
 *
 * THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md
 * 8.2): each built district's 32 x 32 picture (DistrictPicture), drawn by
 * its plan's job and kept by a stamp of the plots it is drawn from, so a
 * month redraws only the districts whose plots it moved - what the far
 * nodes draw from (MapTiles.nodePixels()), the minimap's too (0.7.134).
 * Never saved: drawn again from the plans after a load.
 *
 * THE SIDECAR (slot-NN-map.bin, writeSidecar()/readSidecar()): one record a
 * district, deflated, its header the magic, the format, the world's seed (on
 * the real Earth since 0.7.121 its whole key folded, worldId()), the
 * month, a stamp of the content (also the save's mapStamp), the land it was
 * drawn on and the type count. A map saved and read back writes the same
 * bytes. Since FORMAT 9 (batch CL3) it carries each district's LOG after the
 * rest: the changes to its counts month by month, a record {month, type,
 * change} for each type a month moved, kept in the order they came so its plan
 * can place its buildings in that order and leave them where they stood. A
 * flow cannot be got back from the counts a month ended in, so it is saved;
 * a log that does not add up to the district's counts makes the sidecar
 * unread (the map is drawn again canonically), and a sidecar of FORMAT 8 or
 * older, which has none, starts each log as its counts, one batch at the
 * save's month - drawn as before.
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
 * one still read; FORMAT 6 since 0.7.97, its works on the shore after the
 * runs, laid from its counts when a FORMAT 5 sidecar is read; FORMAT 7
 * since 0.8.0, a district's sites by slot; FORMAT 8 since 0.8.1, the
 * gateway's bearing after the shore, a FORMAT 7 sidecar's found again at the
 * next month's runs; FORMAT 10 since FX5, each railway yard's footprint beside
 * its box in the runs - a yard is laid up to a unit train's length along its
 * line, so its box is not the size it was asked for - an older sidecar reading
 * each as its box's own width and height).
 *
 * AT SEA AND ON THE SHORE (0.7.97, batch O13; docs/studies/spec-oil.md 2.12,
 * spec-roads-and-ports.md 2.8 and 4): THE CAMPUS deals the refinery's units
 * to the district holding the most of them, THE SHORE lays the city's
 * terminals and tank farms at the water city-wide (CityShore), THE OIL AT
 * SEA draws the platforms and pipes the game hands it, and THE SEA ROUTES
 * finds each terminal's way out to the world (SeaRoutes) for the boats.
 * THE TRAINS' ROUTES (0.8.2, batch FR1; spec-freight 4.4) lay the trains'
 * way from the railway's world line onto each yard's sidings, and THE
 * TRUCKS' ROUTES (0.8.8, batch FR5; spec-freight 4.7) the trucks' along the
 * highways to their ramps and along each district's access roads to a gate.
 *
 * THE MATERIALS (0.8.0, batch I14; spec-materials 5's I14 row): THE WORKS
 * ON THEIR SITES BY TYPE stands every mine, quarry and leach plant on its
 * resource's sites of its type, a works on as many as it takes; EVERY
 * INDUSTRY'S CAMPUS gathers each processing industry's works as THE CAMPUS
 * does the refinery's; and ZONES BY DISTRICT picks the districts a zone's
 * tint covers.
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

    /** The share of a district's owned plots its buildings' boxes may take: 0.68 (CL3), a full plan's boxes over its ground on Jerus's densest districts (the CL1 study, section 5.3) - so a district is full where its plan is, and what spills is rare. */
    static final double PLAN_FILL = 0.68;

    /** A district's empty log: the one array every district without records shares. */
    private static final int[] NO_LOG = new int[0];

    /** The resources a district counts its owned sites of, and mines and wells stand on: iron and oil (spec-land 2.5); since 0.8.0 (batch I14) coal, stone and copper after them, every resource a works on dry ground stands on. */
    static final Resource[] SITED = { Resource.IRON, Resource.OIL, Resource.COAL, Resource.STONE, Resource.COPPER };

    /** The site slots (0.8.0, batch I14): each SITED resource's types of field in Resource.typesOf()'s order - iron's hematite and magnetite, oil's one, coal's thermal and coking, stone's limestone and hard rock, copper's sulphide and oxide - what a district counts its sites by, and a works of a type stands on. */
    static final int SLOTS;

    /** ...each slot's resource... */
    static final Resource[] SLOT_RESOURCE;

    /** ...and its type of field. */
    static final Deposit.FieldType[] SLOT_TYPE;

    static {
        List<Resource> rs = new ArrayList<>();
        List<Deposit.FieldType> ts = new ArrayList<>();
        for (Resource r : SITED) for (Deposit.FieldType f : r.typesOf()) { rs.add(r); ts.add(f); }
        SLOTS = rs.size();
        SLOT_RESOURCE = rs.toArray(new Resource[0]);
        SLOT_TYPE = ts.toArray(new Deposit.FieldType[0]);
    }

    /** The sites a district the sidecar wrote before FORMAT 7: iron's and oil's, SITED's until 0.8.0. */
    static final int OLD_SITED = 2;

    /** How many districts' lists of sites are kept: 64. */
    static final int SITE_LISTS_KEPT = 64;

    /** Above this many districts under the land's box, a canonical build looks at the ground coarsely first and measures only districts with land in or beside them: 1,024 (a box 246 km across). */
    static final int COARSE_ABOVE = 1024;

    /** The coarse look's stride, in plots: 32, a sample a tile. */
    static final int COARSE_STRIDE = 32;

    /** The sidecar's magic: "CMAP". */
    static final int MAGIC = 0x434D4150;

    /** The sidecar's format: 10 since FX5 (batch Y1), each railway yard's box in the runs followed by the footprint it was laid for (CityRuns.yardAsked(): a yard is laid YARD_LONG along its line or shorter where the ground allows, so its box is no longer the size it was asked for; a sidecar of FORMAT 9 or older reads each yard's footprint as its box's own width and height, as it was laid then); 9 since CL3 (batch CL3), each district's log after the gateway's bearing (the changes to its counts month by month, in the order they came: what its plan places its buildings by; a sidecar of FORMAT 8 or older has none, and each district's log then starts as its counts, one batch at the save's month); 8 since 0.8.1 (batch FR0), the gateway's bearing after the works on the shore (CityRuns.gateway(): a FORMAT 7 sidecar's is found again at the next month's runs); 7 since 0.8.0 (batch I14), a district's owned sites by slot (SLOTS: each SITED resource's types of field) where it wrote iron's and oil's - an older sidecar's put on their first slots until the index counts them again; 6 since 0.7.97, the city's works on its shore after the runs (CityShore: each terminal's and tank farm's box, never moved) - a FORMAT 5 sidecar has them laid from its counts as it is read, its districts and runs as they were; 5 from 0.7.88, the city's highway and railway runs after the districts (spec-roads-and-ports.md 2.9: "city-wide state in the sidecar (FORMAT 5)"; 0.7.88 wrote a count of none, 0.7.89 the runs it lays) - a FORMAT 4 sidecar, or a FORMAT 5 one with none, has its runs laid from its counts as it is read (OLDEST_READ), its districts as they were; 4 since 0.7.72, when a district's roads came to follow its buildings (ROADS FOLLOW THEIR BUILDINGS) - an older map, whose districts' roads were placed nearest first, some with none, is drawn again canonically, each district its share; 3 from 0.7.67, its land stamp the holdings' rectangles (CityLand.stamp()) where a centre's half-side was; 2 from 0.7.64, when a district's room became its free plots counted plot by plot. A sidecar older than OLDEST_READ is not read, and the map is drawn again once. */
    static final int FORMAT = 10;

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
        /** Its owned sites of each slot (SLOTS; of each SITED resource until 0.8.0). */
        final int[] sites = new int[SLOTS];
        /** Each tile's free plots, row by row: owned and dry; null until measured or asked for. */
        short[] tileFree;
        /** ...and those inside its edge ring, where alone the painter laid a road until 0.7.87 (the counts' room for roads still measures it). */
        short[] tileInner;
        /** Each tile's sites, by key (TilePainter.Input.siteKey), and their free plots on it, all and inside the edge ring: what a mine standing on one takes of the tile. */
        long[][] tileSiteKey;
        short[][] tileSiteFree, tileSiteInner;
        /** How many of each site-bound type stand on sites, each counted in the district of its first site (the rest of its count has none); null when none. */
        int[] sited;
        /** For painting (0.8.0, batch I14; pairs of {holding, mines} by resource before): per slot, the works on its sites here in the walk's order, triples of {holding, type, sites} - which type each holding's sites here carry, a works of twenty sites twenty of them. */
        int[][] siteRuns = new int[SLOTS][];
        /** Bumped whenever its counts change: what its sites' list is kept against (its deal's until 0.7.87). */
        long version;
        /** Its place in the map's order, nearest first. */
        int index;

        /**
         * ITS LOG (batch CL3): the changes to its counts, month by month, in the order they came - a record {month, type,
         * change} (+ come, - gone) for each type a month moved, the types in id order, months never falling, one record
         * to a type and month. What its plan places its buildings by, so a building stays where it was put; saved in the
         * sidecar (FORMAT 9) because a flow cannot be got back from the counts a month ended in.
         */
        int[] logMonth = NO_LOG, logType = NO_LOG, logChange = NO_LOG;
        /** How many records the log holds. */
        int logN;
        /** How many of its first records are one batch, placed in today's order (a canonical map's deal; a sidecar's from before FORMAT 9). */
        int firstBatch;
        /** Its counts as last logged; null before the first (the same as none). */
        int[] logged;
        /** Whether its counts may have moved since they were logged: set wherever they change (a new district is dirty), cleared by logChanges(). */
        boolean logDirty = true;
        /** A hash of its log, kept with it: what its plan's stamp folds, so a plan is drawn again when its log moved and its counts did not. */
        long logHash;

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
            int n = 0;
            for (int k = 0; k < SLOTS; k++) if (SLOT_RESOURCE[k] == r) n += sites[k];
            return n;
        }
        /** Its room in whole plots: PLAN_FILL of its owned plots, what its plan can draw (CL3; every owned plot before). */
        long room()              { return (long) Math.floor(owned * PLAN_FILL); }
        /** Its room left in whole plots, never below nothing (0.7.64; in ground, half square feet, before). */
        long freeCells()         { return Math.max(0, room() - usedCells); }
        /** ...and for roads: its whole room, no more than its plots inside the edge rings less its road. */
        long freeRoad()          { return Math.min(freeCells(), Math.max(0, inner - usedRoad)); }

        /** How many records its log holds. */
        public int logSize()     { return logN; }

        /**
         * Adds a change of type t, made in a month, to its log: a record at the end, or - when the month's records after the
         * first batch already hold the type - into that one (one record to a type and month; gone when the two cancel). The
         * month never falls: one before the log's last is the last's.
         */
        void logAdd(int month, int t, int change) {
            if (change == 0) return;
            if (logN > 0 && month < logMonth[logN - 1]) month = logMonth[logN - 1];
            for (int k = logN - 1; k >= firstBatch && k >= 0 && logMonth[k] == month; k--) {
                if (logType[k] != t) continue;
                logChange[k] += change;
                if (logChange[k] == 0) {
                    int tail = logN - k - 1;
                    System.arraycopy(logMonth, k + 1, logMonth, k, tail);
                    System.arraycopy(logType, k + 1, logType, k, tail);
                    System.arraycopy(logChange, k + 1, logChange, k, tail);
                    logN--;
                }
                rehash();
                return;
            }
            if (logN == logMonth.length) {
                int n = Math.max(8, logN * 2);
                logMonth = Arrays.copyOf(logMonth, n);
                logType = Arrays.copyOf(logType, n);
                logChange = Arrays.copyOf(logChange, n);
            }
            logMonth[logN] = month;
            logType[logN] = t;
            logChange[logN] = change;
            logN++;
            logHash = fold(logHash, month, t, change);
        }

        /** Its log's hash worked out again from its records. */
        void rehash() {
            long h = 0;
            for (int k = 0; k < logN; k++) h = fold(h, logMonth[k], logType[k], logChange[k]);
            logHash = h;
        }

        private static long fold(long h, int month, int t, int change) {
            h = World.mix(h + month * 0x9E3779B97F4A7C15L);
            return World.mix(h ^ ((long) t << 32) ^ (change & 0xffffffffL));
        }

        /** Whether its log and b's are the same: the count of records, the first batch and every record. */
        boolean sameLog(District b) {
            if (logN != b.logN || firstBatch != b.firstBatch) return false;
            for (int k = 0; k < logN; k++) {
                if (logMonth[k] != b.logMonth[k] || logType[k] != b.logType[k] || logChange[k] != b.logChange[k]) return false;
            }
            return true;
        }
    }

    static long key(int dx, int dy) { return ((long) dx << 32) | (dy & 0xffffffffL); }

    /* =====================================================================
       THE STATE
       ===================================================================== */

    private final long seed;
    /** The world it is drawn on (0.7.115, batch E2): its land's key, whose ground every tile and plan reads. */
    private final World.Key worldKey;
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
    /** The month of its last change (CL3): what its districts' logs date their records by; 0 for a map drawn canonically with no month given, a harness's square city. */
    private int month;

    private CityMap(World.Key worldKey, long siteX, long siteY, BuildingVisual.Type[] types) {
        this.seed = worldKey.seed();
        this.worldKey = worldKey;
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

    /** The month of its last change (CL3): the month its districts' newest log records are dated. */
    public int month()                     { return month; }

    /** The world's key (0.7.115): World.of(worldKey()) is the ground it is drawn on. */
    public World.Key worldKey()            { return worldKey; }

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
        return canonical(land, remaining, types, counts, 0);
    }

    /**
     * ...in a given month (CL3): the month its districts' logs date the deal by, each log one batch (firstBatch) of
     * its counts as the deal gave them.
     */
    public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining,
                                    BuildingVisual.Type[] types, long[] counts, int month) {
        CityMap m = new CityMap(land.key(), land.siteX(), land.siteY(), types);
        m.month = month;
        m.land = land;
        m.remaining = remaining;
        m.measureAll();
        m.allocate(counts);
        m.layRuns(counts);
        m.layShore(counts);
        m.logChanges();
        m.batchLogs();
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
        return square(World.Key.generated(seed), siteX, siteY, types, side, capacity, counts);
    }

    /** ...on the world a key names (0.7.115): EarthTerrainCheck's screen on Earth. */
    static CityMap square(World.Key world, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity,
                          long[] counts) {
        CityMap m = new CityMap(world, siteX, siteY, types);
        int h = side / 2;
        double[] centre = new double[CityLand.CENTRE_FIELDS];
        long x0 = (m.baseDX - h) * DISTRICT, x1 = (m.baseDX - h + side) * DISTRICT;
        long y0 = (m.baseDY - h) * DISTRICT, y1 = (m.baseDY - h + side) * DISTRICT;
        centre[CityLand.CENTRE_FIELDS - 5] = -1;
        centre[CityLand.CENTRE_FIELDS - 4] = -1;
        centre[CityLand.CENTRE_FIELDS - 2] = siteX;
        centre[CityLand.CENTRE_FIELDS - 1] = siteY;
        m.land = CityLand.restore(world, centre, new double[][] { { x0, y0, x1, y1 } }, null, null, null);
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
        m.layShore(counts);
        m.logChanges();
        m.batchLogs();
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
        World world = World.of(worldKey);
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
        World world = World.of(worldKey);
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
                        if (!owns[i] || !World.dry(ground[i])) continue;
                        all++;
                        if (x - px0 >= 1 && y - py0 >= 1 && x - px0 < World.TILE - 1 && y - py0 < World.TILE - 1) in++;
                    }
                }
                if (all > 0) here.add(new long[] { (q[0] << 32) | (q[1] & 0xffffffffL), all, in });
            }
            int n = 0, inner = 0;
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                if (!owns[i] || !World.dry(ground[i])) continue;
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
     * (How many district-types it touched is lastTouched.) Every district's
     * change is logged in the month given (logChanges(), CL3); this form
     * counts on from the last month, a harness's.
     */
    public boolean reconcile(long[] model) {
        return reconcile(model, month + 1);
    }

    /** ...in a given month, the game's: what the districts' logs date the month's changes by. */
    public boolean reconcile(long[] model, int month) {
        if (syncLand()) return false;
        this.month = month;
        nPassed = 0;
        int touched = 0;
        // The buildings first, then the roads, which follow them (0.7.72, ROADS FOLLOW THEIR BUILDINGS).
        roadShare = roadShareOf(model);
        builtIn.clear();
        builtInSet.clear();
        for (int roads = 0; roads < 2; roads++) {
            for (int t = 0; t < types.length; t++) {
                // ...not the buildings at sea (0.7.91): on none of the city's dry ground, so in no district (BuildingVisual.of()'s sea flag).
                if (types[t] == null || types[t].sea() || onSites(t) || (types[t].road() != BuildingVisual.NOT_A_ROAD) != (roads == 1)) continue;
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
        // ...and its works on the shore to the month's counts (0.7.97): laid at the water, or the newest taken.
        layShore(model);
        // The month's changes to every district's counts, into their logs (CL3), now that everything that moves them has.
        logChanges();
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

    /**
     * THE LOG (CL3): into the log of every district whose counts may have moved (logDirty), a record {month, type, change}
     * for each type whose count is not what was last logged, the types in id order, at the map's month; then what was
     * logged is its counts. A purchase's recount moves no count, so it logs none; the canonical deal, a placement or
     * removal, the campus gathered, the mines and wells put on their sites and the runs' and the shore's all do, and are
     * records here, in one place.
     */
    void logChanges() {
        logChanges(false);
    }

    /** ...of every district, dirty or not (all): what a sidecar is written after, so a count moved by hand (a harness's) is a record too and what is written always adds up. */
    void logChanges(boolean all) {
        for (District d : districts) {
            if (!all && !d.logDirty) continue;
            d.logDirty = false;
            int[] was = d.logged;
            boolean any = false;
            for (int t = 0; t < d.counts.length; t++) {
                int c = d.counts[t];
                if (c != 0) any = true;
                int w = was == null ? 0 : was[t];
                if (c != w) d.logAdd(month, t, c - w);
            }
            if (was != null) System.arraycopy(d.counts, 0, was, 0, d.counts.length);
            else if (any) d.logged = d.counts.clone();
        }
    }

    /** Every district's whole log so far one batch, placed in today's order: after a canonical deal, a square city's or a sidecar of FORMAT 8 or older read. */
    private void batchLogs() {
        for (District d : districts) d.firstBatch = d.logN;
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
        lackN = 0;
        for (District d : builtIn) {
            double lack = roadLacking(d);
            if (lack > 0 && d.freeRoad() >= each) lackAdd(lack, d.index);
        }
        // Made a heap at once (each parent sifted down, the last first): the same first entry at every poll as entries
        // pushed one by one gave - the order is strict - in about a sixth of the steps for a month's two thousand.
        for (int k = (lackN >>> 1) - 1; k >= 0; k--) lackDown(k);
        int touched = 0;
        while (n > 0 && lackN > 0) {
            District d = districts.get(lackPoll());
            if (d.freeRoad() < each) continue;
            add(d, d.index, t, 1);
            n--;
            touched++;
            double lack = roadLacking(d);
            if (lack > 0 && d.freeRoad() >= each) lackPush(lack, d.index);
        }
        return n > 0 ? touched + place(t, n) : touched;
    }

    /*
     * THE LACKING HEAP (0.7.94, batch RD7): placeRoad()'s districts, the one
     * whose buildings lack road the most first, the lower index on a tie -
     * the order the PriorityQueue it replaces kept, entry for entry (no two
     * entries tie: a district is in it once). Its own heap of two arrays, so
     * a month makes no entry objects, and the JDK's queue code, which the
     * city's runs and others share with their own orders, is not compiled
     * for one order and given up for another in the middle of a month.
     */
    private double[] lackOf = new double[64];
    private int[] lackAt = new int[64];
    private int lackN;

    /** Whether heap entry i comes before entry j: more lacking, then the lower index. */
    private boolean lackFirst(int i, int j) {
        double a = lackOf[i], b = lackOf[j];
        return a != b ? Double.compare(b, a) < 0 : lackAt[i] < lackAt[j];
    }

    /** An entry added at the end, the heap not kept (placeRoad() makes it a heap after). */
    private void lackAdd(double lack, int index) {
        if (lackN == lackOf.length) { lackOf = Arrays.copyOf(lackOf, lackN * 2); lackAt = Arrays.copyOf(lackAt, lackN * 2); }
        lackOf[lackN] = lack;
        lackAt[lackN] = index;
        lackN++;
    }

    private void lackPush(double lack, int index) {
        lackAdd(lack, index);
        int k = lackN - 1;
        while (k > 0) {
            int up = (k - 1) >>> 1;
            if (!lackFirst(k, up)) break;
            lackSwap(k, up);
            k = up;
        }
    }

    /** The first entry's district index, taken off the heap. */
    private int lackPoll() {
        int top = lackAt[0];
        lackN--;
        if (lackN > 0) {
            lackOf[0] = lackOf[lackN];
            lackAt[0] = lackAt[lackN];
            lackDown(0);
        }
        return top;
    }

    /** Entry k sifted down to its place below. */
    private void lackDown(int k) {
        while (true) {
            int l = 2 * k + 1, r = l + 1, m = k;
            if (l < lackN && lackFirst(l, m)) m = l;
            if (r < lackN && lackFirst(r, m)) m = r;
            if (m == k) return;
            lackSwap(k, m);
            k = m;
        }
    }

    private void lackSwap(int i, int j) {
        double a = lackOf[i]; lackOf[i] = lackOf[j]; lackOf[j] = a;
        int b = lackAt[i]; lackAt[i] = lackAt[j]; lackAt[j] = b;
    }

    /** Places n more of type t: into the first districts with room for its whole plots in its direction (a road's inside its tiles' edge rings; since 0.7.72 a building's keeping room for its road, buildingRoom()), the rest into the district with the most room left (0.7.64; the outermost before, which can be a district of water holding only sites). */
    private int place(int t, long n) {
        boolean out = types[t].outer(), road = types[t].road() != BuildingVisual.NOT_A_ROAD;
        long each = BuildingVisual.cells(types[t]);
        int touched = 0;
        // THE CAMPUS (0.7.97): a refinery unit first into the district holding the most of the refinery's ground, while it has
        // room - and since 0.8.0 each processing industry's works into its own campus (BuildingVisual.Type.campusGroup()).
        if (types[t].campusGroup() > BuildingVisual.NO_GROUP) {
            District c = campusDistrict(types[t].campusGroup());
            long k = c == null ? 0 : Math.min(n, buildingRoom(c, each));
            if (k > 0) {
                add(c, c.index, t, k);
                n -= k;
                touched++;
            }
            if (n <= 0) return touched;
        }
        int i = out ? outerCursor : innerCursor, kind = (out ? 2 : 0) | (road ? 1 : 0), first = -2;
        if (each > 0) i = passed(kind, each, i);
        while (n > 0 && i >= 0 && i < districts.size()) {
            District d = districts.get(i);
            long room = each > 0 ? (road ? d.freeRoad() / each : buildingRoom(d, each)) : n;
            long k = Math.min(n, room);
            if (k > 0) {
                if (first == -2) first = i;
                add(d, i, t, k);
                n -= k;
                touched++;
            }
            if (n > 0) i += out ? -1 : 1;
        }
        if (each > 0) notePassed(kind, each, first != -2 ? first : i);
        if (n > 0) {
            District most = roomiest();
            add(most, most.index, t, n);
            touched++;
        }
        return touched;
    }

    /*
     * WALKS PASSED (0.7.94, batch RD7). Within a month's change a district's
     * room only shrinks while buildings are added (roadShare stands for the
     * month), and a district with no room for a size has none for a larger
     * one (buildingRoom() and freeRoad() / size fall as the size grows). So
     * the districts a walk of place() found no room in - from the cursor to
     * the first with room, or to the end - have none for a walk of the same
     * kind (inward or outward, a road or not) of that size or larger, from a
     * cursor that has not moved back: it starts past them. Forgotten at each
     * month and whenever anything is taken away (add() with k < 0, which
     * moves the cursors back too). The same districts take the same counts;
     * at 10 billion the month's first change walked about 38,000 districts.
     */
    private long[] passedSize = new long[32];
    private int[] passedTo = new int[32], passedKind = new int[32];
    private int nPassed;

    /** Where a walk of `kind` for buildings of `each` plots from cursor i may start (WALKS PASSED). */
    private int passed(int kind, long each, int i) {
        boolean out = (kind & 2) != 0;
        for (int k = 0; k < nPassed; k++) {
            if (passedKind[k] != kind || passedSize[k] > each) continue;
            i = out ? Math.min(i, passedTo[k]) : Math.max(i, passedTo[k]);
        }
        return i;
    }

    /** A walk of `kind` for `each` plots found no room before district index `to` (WALKS PASSED). */
    private void notePassed(int kind, long each, int to) {
        boolean out = (kind & 2) != 0;
        for (int k = 0; k < nPassed; k++) {
            if (passedKind[k] != kind || passedSize[k] != each) continue;
            passedTo[k] = out ? Math.min(passedTo[k], to) : Math.max(passedTo[k], to);
            return;
        }
        if (nPassed == passedSize.length) {
            passedSize = Arrays.copyOf(passedSize, nPassed * 2);
            passedTo = Arrays.copyOf(passedTo, nPassed * 2);
            passedKind = Arrays.copyOf(passedKind, nPassed * 2);
        }
        passedSize[nPassed] = each;
        passedTo[nPassed] = to;
        passedKind[nPassed] = kind;
        nPassed++;
    }

    /*
     * THE CAMPUS (0.7.97, batch O13; docs/studies/spec-oil.md 2.12, the
     * research's Q11: "the refinery is drawn as a campus"). The refinery's
     * units - its crude units and conversion units
     * (BuildingVisual.Type.campus()) - were placed as any industry, into the
     * first district with room, so Jerus's sixteen stood in five districts. Now
     * a unit goes first to the district already holding the most of the
     * refinery's ground, while it has room (place()), and the district's plan
     * packs them together, first of its industry (DistrictPlan's THE CAMPUS);
     * drawn canonically, the whole refinery goes to the first district with
     * room for all of it, before the rest is shared (allocate()). What no
     * campus district has room for goes as any industry. Nothing placed moves,
     * but once: a map read from a sidecar older than FORMAT 6 (0.7.96's and
     * before) has its refinery gathered into its campus district as it is read,
     * while it has room (gatherCampus(), star O13-1) - the picture shifts once,
     * as FORMAT 4's did.
     */

    /*
     * EVERY INDUSTRY'S CAMPUS (0.8.0, batch I14; spec-materials 5's I14 row:
     * "the works as O13's campus"). The refinery's rule, for each group of
     * BuildingVisual's - the refinery's units, then Iron & Steel's,
     * Cement & Concrete's, Copper Refining's and Wood Products' works: a
     * works first into the district holding the most of its group's ground
     * while it has room, the plan packing its group in touching cells; drawn
     * canonically, a group whole into the first district with room for all
     * of it - the refinery's into the roomiest when none has, as before, and
     * a works group shared as any industry, so a city of ten billion is not
     * poured into one district. Iron & Steel's arc furnaces and mini-mills,
     * on every map before, are drawn in its colours but not gathered
     * (BuildingVisual.NOT_GATHERED), and only the refinery is gathered from
     * an older sidecar (gatherCampus()): nothing else placed moves.
     */

    /** The most districts a works group's units may stand across, in the map's order, and still have a campus a new one goes to first: 64 (0.8.0) - past it, a city whose works of a group stand in every district (a city of billions) places them as any industry, and a month does not read every district for the one holding the most. The refinery's has no bound, as before. */
    static final int CAMPUS_REACH = 64;

    /** Each group's types, by id: worked out once a table. */
    private int[][] campusTypes;

    private int[] campusTypes(int g) {
        if (campusTypes == null) {
            int[][] all = new int[BuildingVisual.GROUPS][];
            for (int q = 0; q < BuildingVisual.GROUPS; q++) {
                int n = 0;
                for (BuildingVisual.Type ty : types) if (ty != null && ty.campusGroup() == q && q > BuildingVisual.NO_GROUP) n++;
                int[] out = new int[n];
                n = 0;
                for (int u = 0; u < types.length; u++) if (types[u] != null && types[u].campusGroup() == q && q > BuildingVisual.NO_GROUP) out[n++] = u;
                all[q] = out;
            }
            campusTypes = all;
        }
        return campusTypes[g];
    }

    /** The refinery's unit types, by id. */
    private int[] campusTypes() {
        return campusTypes(BuildingVisual.REFINERY);
    }

    /** The refinery's ground in district d, in the whole plots its units are drawn on. */
    long campusCells(District d) {
        return campusCells(d, BuildingVisual.REFINERY);
    }

    /** ...a group's (0.8.0). */
    long campusCells(District d, int g) {
        long c = 0;
        int[] cells = BuildingVisual.cellsById(types);
        for (int u : campusTypes(g)) c += (long) d.counts[u] * cells[u];
        return c;
    }

    /** The district holding the most of the refinery's ground, the nearer on a tie; null when it has none. Looks only between the districts holding a unit (lo, hi). */
    District campusDistrict() {
        return campusDistrict(BuildingVisual.REFINERY);
    }

    /** ...of a group's (0.8.0). */
    District campusDistrict(int g) {
        int from = Integer.MAX_VALUE, to = -1;
        for (int u : campusTypes(g)) {
            if (hi[u] < 0) continue;
            from = Math.min(from, lo[u]);
            to = Math.max(to, hi[u]);
        }
        // ...a works group spread over more than CAMPUS_REACH districts has no campus: it is placed as any industry (0.8.0).
        if (g != BuildingVisual.REFINERY && to - from + 1 > CAMPUS_REACH) return null;
        District best = null;
        long most = 0;
        for (int i = Math.max(0, from); i <= to && i < districts.size(); i++) {
            long c = campusCells(districts.get(i), g);
            if (c > most) { most = c; best = districts.get(i); }
        }
        return best;
    }

    /** The refinery's units in other districts moved into its campus district while it has room, the largest kinds first, then by district in the map's order (a sidecar older than FORMAT 6, read). How many moved. */
    int gatherCampus() {
        District c = campusDistrict();
        if (c == null) return 0;
        int moved = 0;
        int[] kinds = campusTypes().clone();
        Integer[] boxed = new Integer[kinds.length];
        for (int k = 0; k < kinds.length; k++) boxed[k] = kinds[k];
        Arrays.sort(boxed, (a, b) -> BuildingVisual.cells(types[a]) != BuildingVisual.cells(types[b])
                ? Integer.compare(BuildingVisual.cells(types[b]), BuildingVisual.cells(types[a])) : Integer.compare(a, b));
        for (int u : boxed) {
            long each = BuildingVisual.cells(types[u]);
            for (int i = 0; i < districts.size(); i++) {
                District d = districts.get(i);
                if (d == c || d.counts[u] <= 0) continue;
                long k = Math.min(d.counts[u], buildingRoom(c, each));
                if (k <= 0) break;
                add(d, i, u, -k);
                add(c, c.index, u, k);
                moved += (int) k;
            }
        }
        if (moved > 0) rangeTypes();
        return moved;
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
        d.logDirty = true;
        changes++;
        if (k > 0) {
            if (i < lo[t]) lo[t] = i;
            if (i > hi[t]) hi[t] = i;
        } else {
            if (i < innerCursor) innerCursor = i;
            if (i > outerCursor) outerCursor = i;
            // Room grew: what the month's walks passed is passed no longer (WALKS PASSED).
            nPassed = 0;
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
        // THE CAMPUS (0.7.97): the refinery whole, into the first district with room for all of it (the roomiest when none
        // has), before the rest is shared - as the mines' ground is theirs first; since 0.8.0 each works group too, shared as
        // any industry when no district has room for it whole (EVERY INDUSTRY'S CAMPUS).
        boolean[] gathered = new boolean[types.length];
        for (int g = BuildingVisual.REFINERY; g < BuildingVisual.GROUPS; g++) {
            long campus = 0;
            for (int u : campusTypes(g)) if (u < counts.length && counts[u] > 0) campus += counts[u] * (long) BuildingVisual.cells(types[u]);
            if (campus <= 0 || n <= 0) continue;
            int at = -1;
            for (int i = 0; i < n && at < 0; i++) if (districts.get(i).room() - used[i] >= campus) at = i;
            if (at < 0 && g != BuildingVisual.REFINERY) continue;
            if (at < 0) {
                at = 0;
                for (int i = 1; i < n; i++) if (districts.get(i).room() - used[i] > districts.get(at).room() - used[at]) at = i;
            }
            for (int u : campusTypes(g)) {
                gathered[u] = true;
                if (u >= counts.length || counts[u] <= 0) continue;
                districts.get(at).counts[u] += (int) counts[u];
                have[u] = counts[u];
            }
            used[at] += campus;
        }
        double total = 0;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null || onSites(t) || types[t].sea() || gathered[t] || t >= counts.length) continue;
            total += counts[t] * (double) BuildingVisual.cells(types[t]);
        }
        double[] share = new double[n];
        double left = total;
        for (int i = 0; i < n && left > 0; i++) {
            double take = Math.min(Math.max(0, districts.get(i).room() - used[i]), left);
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
            if (types[t] == null || onSites(t) || gathered[t] || t >= counts.length || counts[t] <= 0) continue;
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
                    if (used[i] + each > districts.get(i).room()) continue;
                    districts.get(i).counts[t]++;
                    used[i] += each;
                    extra--;
                }
                while (extra > 0) {
                    int best = 0;
                    for (int i = 1; i < n; i++) if (districts.get(i).room() - used[i] > districts.get(best).room() - used[best]) best = i;
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
            // ...every district's counts were dealt here, directly: all are to be logged.
            d.logDirty = true;
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

    /** The fields of a resource that reach into a box of plots: the world's as the city sees them (CityLand.fieldsIn(): its old world's ground's own, 0.7.99), and a converted centre's legacy iron field. */
    private List<Deposit> fieldsNear(Resource r, double bx0, double by0, double bx1, double by1) {
        List<Deposit> out = new ArrayList<>();
        double reach = Deposit.mostReach(r);
        for (int cell : CityLand.cellsUnder(bx0 - reach, by0 - reach, bx1 + reach, by1 + reach)) {
            for (Deposit d : land.fieldsIn(cell, r)) {
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
        int n = Math.min(World.LEGACY_MAX_SITES, land.legacySites());
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
        for (int k = 0; k < SLOTS; k++) {
            if (ix.tally.size() <= k) ix.tally.add(new ArrayList<>());
            List<Map<District, int[]>> m = ix.tally.get(k);
            while (m.size() < holdings) m.add(new HashMap<>());
        }
        for (Resource r : SITED) {
            if (!r.inFields()) continue;
            for (Deposit f : fieldsNear(r, siteX + bx0, siteY + by0, siteX + bx1, siteY + by1)) {
                // ...each field's sites in the slot of its type (0.8.0): a hematite field's for the hematite mines.
                int k = slotOf(r, f.type());
                if (k < 0) continue;
                List<Map<District, int[]>> m = ix.tally.get(k);
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
        out.where = new District[SLOTS][holdings][];
        out.how = new int[SLOTS][holdings][];
        for (int k = 0; k < SLOTS; k++) {
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
        return ownedSites(r, null);
    }

    /** ...of one type of its fields (0.8.0, batch I14), or of all of them (null): what a works of that type may stand on. */
    public long ownedSites(Resource r, Deposit.FieldType type) {
        siteIndex();
        long n = 0;
        for (int k = 0; k < SLOTS; k++) {
            if (SLOT_RESOURCE[k] != r || (type != null && SLOT_TYPE[k] != type)) continue;
            for (District d : districts) n += d.sites[k];
        }
        return n;
    }

    /** Whether type t stands on its resource's sites (placeSited()): a site of a SITED resource, and sites to take - any other building, a site on none, placed as any building is, so the districts always sum to the counts. */
    private boolean onSites(int t) {
        return types[t] != null && types[t].site() != null && types[t].sites() > 0 && siteSlot(types[t].site()) >= 0;
    }

    /** The slot of a resource's fields of a type, or -1 (not SITED, or a type not the resource's). */
    static int slotOf(Resource r, Deposit.FieldType type) {
        for (int k = 0; k < SLOTS; k++) if (SLOT_RESOURCE[k] == r && SLOT_TYPE[k] == type) return k;
        return -1;
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

    /*
     * THE WORKS ON THEIR SITES BY TYPE (0.8.0, batch I14; spec-materials 5's
     * I14 row: "pits by sites"). Until 0.7.143 one type stood on a resource's
     * sites - the first in the table with it as its site - and every other
     * (a Large Open-Pit Mine, the magnetite mines) stood in the founding
     * site's district on none, drawn as one plain mine; coal's, stone's and
     * copper's first types were placed nowhere (the latent "districts do not
     * sum" of fixI9-notes' star 10). Now each SITED resource's types are
     * claims on its slots (SLOTS: its fields by type), laid as LandManager.
     * fits() packs them - the typed before those on ANY of its fields, the
     * larger first, then by id; ANY onto the first type's sites first - each
     * works on as many sites as its template takes (a Large Open-Pit Mine on
     * twenty, counted once, in the district of its first), a works for which
     * its slots have no whole room left in the founding site's district on
     * none. A month that moves only the last claim on its slots fills or
     * empties it from where it stood; any other move, or the land or a
     * holding's state moving, lays the resource again.
     */

    /** One slot's sites in the order works take them (layWalk()), and how far its claims have filled them. */
    private static final class Walk {
        District[] at = new District[0];
        int[] holding = new int[0], sites = new int[0];
        int length, slot;
        /** The next free site: its entry, and the sites of that entry taken. */
        int pos, partial;
        /** Its sites, and those taken. */
        long total, taken;

        long free() { return total - taken; }
    }

    /** One SITED resource's works on its sites: its slots' walks, laid against an index and its holdings' states, and each claim's works on each slot and on none. */
    private static final class Lay {
        SiteIndex ix;
        int[] states;
        /** By the resource's own slots, in SLOTS' order. */
        Walk[] walks;
        /** By claim (claims()): its works on each of the resource's slots, on none, and its count at the last month. */
        long[][] on;
        long[] over, count;
    }

    private final Lay[] lays = new Lay[SITED.length];

    /** Each SITED resource's claims (the types standing on its sites) in the order they are laid: worked out once a table. */
    private int[][] claims;

    /** A SITED resource's claims: its typed works first, by their type's slot, then those on ANY of its fields; within each the larger first (BuildingVisual.Type.sites()), then by id - first fit decreasing, as LandManager.fits() packs them. */
    int[] claims(int k) {
        if (claims == null) {
            int[][] all = new int[SITED.length][];
            for (int q = 0; q < SITED.length; q++) {
                Resource r = SITED[q];
                List<Integer> ts = new ArrayList<>();
                for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].site() == r && types[t].sites() > 0) ts.add(t);
                ts.sort((a, b) -> {
                    int sa = claimSlot(a), sb = claimSlot(b);
                    if (sa != sb) return Integer.compare(sa, sb);
                    if (types[a].sites() != types[b].sites()) return Integer.compare(types[b].sites(), types[a].sites());
                    return Integer.compare(a, b);
                });
                all[q] = ts.stream().mapToInt(Integer::intValue).toArray();
            }
            claims = all;
        }
        return claims[k];
    }

    /** A claim's slot to sort by: its type's, or past every slot for ANY (or a type not its resource's). */
    private int claimSlot(int t) {
        int s = types[t].field() == null ? -1 : slotOf(types[t].site(), types[t].field());
        return s < 0 ? SLOTS : s;
    }

    /** The resource's own slots (indexes into Lay.walks) a claim stands on, in order: its type's alone, or every one for ANY. */
    private int[] slotsOf(int k, int t) {
        Resource r = SITED[k];
        int first = slotOf(r, r.typesOf().get(0)), n = r.typesOf().size();
        int s = types[t].field() == null ? -1 : slotOf(r, types[t].field());
        if (s >= 0) return new int[] { s - first };
        int[] all = new int[n];
        for (int j = 0; j < n; j++) all[j] = j;
        return all;
    }

    /** Whether claim c is the last laid on each of its slots: no later claim stands on any of them. */
    private boolean lastOnItsSlots(int k, int c) {
        int[] cl = claims(k);
        int[] mine = slotsOf(k, cl[c]);
        for (int d = c + 1; d < cl.length; d++) {
            for (int s : slotsOf(k, cl[d])) for (int m : mine) if (s == m) return false;
        }
        return true;
    }

    /**
     * Puts each SITED resource's works on its sites (spec-land 2.4, 2.5;
     * since 0.8.0 every type of them, by the type of its field and on as
     * many sites as it takes): in each slot the first sites in acquisition
     * order whose holding is not worked out, then the worked-out ones, a
     * holding's districts nearest first; those with no room in the founding
     * site's district on none. Incremental where only the last claim on its
     * slots moved (THE WORKS ON THEIR SITES BY TYPE).
     */
    private void placeSited(long[] model) {
        for (int k = 0; k < SITED.length; k++) {
            int[] cl = claims(k);
            if (cl.length == 0) continue;
            Lay lay = lays[k];
            long[] want = new long[cl.length];
            boolean any = false;
            for (int c = 0; c < cl.length; c++) {
                want[c] = cl[c] < model.length ? Math.max(0, model[cl[c]]) : 0;
                any |= want[c] > 0 || have[cl[c]] != 0;
            }
            if (lay == null && !any) continue;
            SiteIndex ix = siteIndex();
            int[] states = holdingStates(SITED[k]);
            if (lay == null || lay.ix != ix || !Arrays.equals(lay.states, states)) {
                relay(k, ix, states, want);
                continue;
            }
            int moved = -1, how = 0;
            for (int c = 0; c < cl.length; c++) if (want[c] != lay.count[c]) { moved = c; how++; }
            if (how == 0) continue;
            if (how == 1 && lastOnItsSlots(k, moved)) restand(k, lay, moved, want[moved]);
            else relay(k, ix, states, want);
        }
        sitedPlaced = true;
    }

    /** Lays a resource's works on its sites again: every one of its types taken off the map, then each claim in order on its slots' walks from their first site. */
    private void relay(int k, SiteIndex ix, int[] states, long[] want) {
        int[] cl = claims(k);
        Resource r = SITED[k];
        int first = slotOf(r, r.typesOf().get(0)), n = r.typesOf().size();
        Lay old = lays[k];
        if (old != null) for (Walk w : old.walks) for (int e = 0; e < w.length; e++) w.at[e].siteRuns[w.slot] = null;
        for (int t : cl) clearType(t);
        Lay lay = new Lay();
        lay.ix = ix;
        lay.states = states;
        lay.walks = new Walk[n];
        for (int j = 0; j < n; j++) lay.walks[j] = layWalk(ix, states, first + j);
        lay.on = new long[cl.length][n];
        lay.over = new long[cl.length];
        lay.count = new long[cl.length];
        lays[k] = lay;
        for (int c = 0; c < cl.length; c++) restand(k, lay, c, want[c]);
    }

    /** One claim to a new count: more filled onto its slots in order from where they stand, then on none; fewer taken off what stands on none first, then off its slots from the last back. */
    private void restand(int k, Lay lay, int c, long n) {
        int t = claims(k)[c];
        int each = Math.max(1, types[t].sites());
        int[] slots = slotsOf(k, t);
        long delta = n - lay.count[c];
        if (delta > 0) {
            for (int j : slots) {
                if (delta <= 0) break;
                long q = fill(lay.walks[j], t, each, delta);
                lay.on[c][j] += q;
                delta -= q;
            }
            if (delta > 0) {
                District h = home();
                add(h, indexOf(h), t, delta);
                lay.over[c] += delta;
            }
        } else if (delta < 0) {
            long off = -delta;
            long o = Math.min(off, lay.over[c]);
            if (o > 0) {
                District h = home();
                add(h, indexOf(h), t, -o);
                lay.over[c] -= o;
                off -= o;
            }
            for (int s = slots.length - 1; s >= 0 && off > 0; s--) {
                int j = slots[s];
                long q = Math.min(off, lay.on[c][j]);
                empty(lay.walks[j], t, each, q);
                lay.on[c][j] -= q;
                off -= q;
            }
        }
        lay.count[c] = n;
    }

    /** The walk's order: holdings not worked out in acquisition order, then the worked-out; within each, its districts nearest first. */
    private Walk layWalk(SiteIndex ix, int[] states, int k) {
        Walk w = new Walk();
        w.slot = k;
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
                    w.total += w.sites[w.length];
                    w.length++;
                }
            }
        }
        return w;
    }

    /** Up to n works of type t, `each` sites apiece, onto a walk from its next free site - only while a whole one has room - each counted in the district of its first site. How many stood. */
    private long fill(Walk w, int t, int each, long n) {
        long k = Math.min(n, w.free() / each);
        long stood = k;
        int owed = 0;
        while (k > 0 || owed > 0) {
            if (w.partial >= w.sites[w.pos]) { w.pos++; w.partial = 0; continue; }
            District d = w.at[w.pos];
            int h = w.holding[w.pos], avail = w.sites[w.pos] - w.partial;
            if (owed > 0) {
                // ...the rest of a works begun in an entry before.
                int take = Math.min(owed, avail);
                markRun(d, w.slot, h, t, take);
                w.partial += take;
                w.taken += take;
                owed -= take;
                continue;
            }
            long whole = Math.min(k, avail / each);
            if (whole > 0) {
                markRun(d, w.slot, h, t, (int) (whole * each));
                count(d, t, whole);
                w.partial += (int) (whole * each);
                w.taken += whole * each;
                k -= whole;
                continue;
            }
            // ...one begun here, on what the entry has left, and owed the rest from the next.
            markRun(d, w.slot, h, t, avail);
            count(d, t, 1);
            w.partial += avail;
            w.taken += avail;
            owed = each - avail;
            k--;
        }
        return stood;
    }

    /** n works of type t, `each` sites apiece, taken back off a walk from its last taken site: the last claim on it, so they are the ones there. */
    private void empty(Walk w, int t, int each, long n) {
        while (n > 0) {
            int left = each;
            while (left > 0) {
                if (w.partial == 0) { w.pos--; w.partial = w.sites[w.pos]; }
                District d = w.at[w.pos];
                int h = w.holding[w.pos];
                if (left == each && w.partial >= each) {
                    // ...as many whole ones as end in this entry at once.
                    long whole = Math.min(n, w.partial / each);
                    markRun(d, w.slot, h, t, (int) -(whole * each));
                    count(d, t, -whole);
                    w.partial -= (int) (whole * each);
                    w.taken -= whole * each;
                    n -= whole;
                    left = 0;
                    break;
                }
                int take = Math.min(left, w.partial);
                markRun(d, w.slot, h, t, -take);
                w.partial -= take;
                w.taken -= take;
                left -= take;
                // ...a works is counted where its first site is: the last of its sites taken back.
                if (left == 0) { count(d, t, -1); n--; }
            }
        }
    }

    /** m more (or fewer) sites of a district's slot, of one holding, under a works of type t: its painting's runs. */
    private void markRun(District d, int slot, int holding, int t, int m) {
        if (m == 0) return;
        int[] runs = d.siteRuns[slot];
        int at = -1;
        if (runs != null) for (int a = runs.length - 3; a >= 0 && at < 0; a -= 3) if (runs[a] == holding) at = a;
        if (at < 0 || runs[at + 1] != t) {
            runs = runs == null ? new int[3] : Arrays.copyOf(runs, runs.length + 3);
            at = runs.length - 3;
            runs[at] = holding;
            runs[at + 1] = t;
        }
        runs[at + 2] += m;
        if (runs[at + 2] <= 0) {
            int[] less = new int[runs.length - 3];
            System.arraycopy(runs, 0, less, 0, at);
            System.arraycopy(runs, at + 3, less, at, runs.length - at - 3);
            runs = less.length == 0 ? null : less;
        }
        d.siteRuns[slot] = runs;
        d.version++;
        changes++;
    }

    /** q more (or fewer) works of type t counted on sites in a district. */
    private void count(District d, int t, long q) {
        if (q == 0) return;
        if (d.sited == null) d.sited = new int[types.length];
        d.sited[t] += (int) q;
        add(d, indexOf(d), t, q, true);
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

    /** Takes every one of type t off the map (its sites' runs are its resource's lay's to clear: relay()). */
    private void clearType(int t) {
        if (hi[t] < 0) return;
        for (int i = Math.max(0, lo[t]); i <= hi[t] && i < districts.size(); i++) {
            District d = districts.get(i);
            int sited = d.sited == null ? 0 : d.sited[t];
            if (d.counts[t] - sited != 0) add(d, i, t, -(d.counts[t] - sited));
            if (sited != 0) add(d, i, t, -sited, true);
            if (d.sited != null) d.sited[t] = 0;
        }
        lo[t] = Integer.MAX_VALUE;
        hi[t] = -1;
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

    /** A siding plot of a Drawn (Drawn.siding) whose bit this is: the siding's first, beside the works' box, where its loading shed stands (batch FR7 slice 5). The plot is below it: a frame has 66,049 of them. */
    static final int SIDING_END_BIT = 1 << 24;

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
        /** The plots its plan's sidings run over (batch FR7 slice 5, D14), each a plot of its frame (y x FRAME + x) with SIDING_END_BIT set on the first of a siding, the loading shed's; null for none. */
        int[] siding;
        /** Its plan's access roads (batch FR7, D17) as the trucks drive them (FR5, slice B): each a way of plots of its frame (y x FRAME + x), the road's end first and its gate last, 4-connected over the plots the roads run over (DistrictPlan.accessAt); null for none. A road the plan laid but that cannot be traced, or that already stood on one (ACCESS_HAD), has none. */
        int[][] access;
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
            if (siding != null) b += 16 + 4L * siding.length;
            if (access != null) {
                b += 16;
                for (int[] a : access) b += 16 + 4L * a.length;
            }
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
        int r = role == DistrictPlan.ROLE_ARTERIAL ? TilePainter.S_ARTERIAL : role == DistrictPlan.ROLE_BOULEVARD ? TilePainter.S_BOULEVARD
                : role == DistrictPlan.ROLE_ACCESS ? TilePainter.S_ACCESS : 0;
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
        if (p.siding != null) {
            int count = 0;
            for (byte s : p.siding) if (s != 0) count++;
            out.siding = new int[count];
            count = 0;
            for (int q = 0; q < p.siding.length; q++) if (p.siding[q] != 0) out.siding[count++] = q | (p.siding[q] == DistrictPlan.SIDING_END ? SIDING_END_BIT : 0);
        }
        out.access = accessWays(p);
        out.placed = p.buildings;
        out.refused = p.overflowCount();
        out.surplus = p.surplus;
        System.arraycopy(p.leftover, 0, out.leftover, 0, 4);
        out.joinsOut = p.joinsOut;
        out.partedOut = p.partedOut;
        out.seamsSurfaced = p.seamsSurfaced;
        return out;
    }

    /**
     * A plan's access roads as ways of plots (FR5, slice B; Drawn.access):
     * for each road the plan laid (DistrictPlan.accessRoads, those that have
     * a gate and an end - a work that already stood on one has neither) the
     * shortest 4-connected way over the plots the roads run over
     * (accessAt) from its end plot to its gate plot, as frame indices (y x
     * FRAME + x), the end first and the gate last. A road no way joins is
     * left out. Null when the plan laid none.
     */
    static int[][] accessWays(DistrictPlan p) {
        if (p.accessAt == null || p.accessRoads.isEmpty()) return null;
        int f = DistrictPlan.FRAME;
        List<int[]> out = new ArrayList<>();
        int[] prev = null, queue = null;
        for (int[] r : p.accessRoads) {
            if (r[4] < 0 || r[5] < 0 || r[6] < 0 || r[7] < 0) continue;
            int gate = r[5] * f + r[4], end = r[7] * f + r[6];
            if (gate < 0 || gate >= f * f || end < 0 || end >= f * f || !p.accessAt[gate] || !p.accessAt[end]) continue;
            if (prev == null) { prev = new int[f * f]; queue = new int[f * f]; }
            Arrays.fill(prev, -2);
            // Breadth first from the gate, each plot remembering the one it was reached from: read from the end, prev leads back to the gate.
            int head = 0, tail = 0;
            queue[tail++] = gate;
            prev[gate] = -1;
            while (head < tail && prev[end] == -2) {
                int a = queue[head++], x = a % f, y = a / f;
                for (int dir = 0; dir < 4; dir++) {
                    if (dir == 0 && y == 0 || dir == 1 && x == f - 1 || dir == 2 && y == f - 1 || dir == 3 && x == 0) continue;
                    int b = dir == 0 ? a - f : dir == 1 ? a + 1 : dir == 2 ? a + f : a - 1;
                    if (!p.accessAt[b] || prev[b] != -2) continue;
                    prev[b] = a;
                    queue[tail++] = b;
                }
            }
            if (prev[end] == -2) continue;
            int n = 0;
            for (int a = end; a >= 0; a = prev[a]) n++;
            int[] way = new int[n];
            int k = 0;
            for (int a = end; a >= 0; a = prev[a]) way[k++] = a;
            out.add(way);
        }
        return out.isEmpty() ? null : out.toArray(new int[0][]);
    }

    /** A box packed into an int: x, y, w - 1 and h - 1 at five bits each, PACKED_BIT when packed without a street. */
    static int box(int x, int y, int w, int h, boolean packed) {
        return x | (y << 5) | ((w - 1) << 10) | ((h - 1) << 15) | (packed ? PACKED_BIT : 0);
    }

    /** A district's link in the chain (R7): the stamp of its own inputs, what was carried to it and what it carried on ({month, code} pairs in the order they came, since CL3 - the month each came in, so the spill keeps its time; code >= 0 an arrival of that type id, code < 0 a departure, ~k, of the building the k-th pair of the same list brought: the list only grows at its end; null for none); for an edge district, its plan's free ground (a bit a plot of the district), where the city's packed buildings may stand; its frame's edges' street codes (margins), which tell the districts after it which of their seams it leaves unlaid and where their streets join its; and the districts about it before it in the map's order, with the stamps of their links its plan read (readNeighbours()). Kept for every district planned, never dropped. */
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
        /** Its plan's picture's stamp (0.7.128, batch MM1; DistrictPicture.stampOf()): the picture kept for it is current when it bears this. */
        long picture;
        Link(long own, int[] in, int[] out, long[] free, byte[][] margins, District[] earlier, long[] read, double surplus, long serial) {
            this.own = own; this.in = in; this.out = out; this.free = free; this.margins = margins;
            this.earlier = earlier; this.read = read; this.surplus = surplus; this.serial = serial;
        }
        /** Its plan's stamp: its own inputs and what came in. */
        long stamp() { return stampOf(own, in); }
    }

    /** A plan's stamp: its own inputs' and the carry in's, {month, code} pairs (CL3): the list's length and every entry folded, zeros included, in order - so two different lists never stamp alike, a list of none and no list neither. */
    static long stampOf(long own, int[] in) {
        long h = World.mix(own ^ 0x4C494E4BL);
        if (in != null) {
            h = World.mix(h ^ 0x4C454E00L ^ ((long) in.length << 32));
            for (int k = 0; k < in.length; k++) h = World.mix(h ^ ((long) k << 32) ^ (in[k] & 0xffffffffL));
        }
        return h;
    }

    /**
     * A carry's {month, code} pairs as counts by type (CL3): the arrivals (code >= 0, a type id) less the departures (code
     * < 0, ~k: the building the k-th pair brought, so the type of that pair) - what the packing at the edge and the legend
     * count by; null for none. A departure whose arrival is missing, comes later or has gone already is ignored.
     */
    static int[] countsOf(int[] pairs, int types) {
        if (pairs == null) return null;
        int[] out = new int[types];
        boolean[] gone = null;
        for (int k = 0; 2 * k + 1 < pairs.length; k++) {
            int code = pairs[2 * k + 1];
            if (code >= 0) {
                if (code < types) out[code]++;
                continue;
            }
            int a = ~code;
            if (a >= k) continue;
            int t = pairs[2 * a + 1];
            if (t < 0 || t >= types) continue;
            if (gone == null) gone = new boolean[pairs.length / 2];
            if (gone[a]) continue;
            gone[a] = true;
            out[t]--;
        }
        return out;
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
     * sites apart) and the log they came in (CL3), the ground the city owns over its frame (ownership only
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
        // ...and its log (CL3): the order its buildings came in places them, so a log that moved moves its plan, its counts moved or not.
        if (d.logN > 0) h = World.mix(h ^ d.logHash ^ ((long) d.firstBatch << 40) ^ d.logN);
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
        // The city's runs over its frame (0.7.89), and its Rail Terminals (and since FR7 Rail Depots) the runs draw as yards.
        h = World.mix(h ^ runs.frameHash(x0, y0, DistrictPlan.FRAME, DistrictPlan.FRAME));
        h = World.mix(h ^ yardsOf(d));
        // ...and its share of the Rail Spurs the runs' sidings left (batch FR7 slice 5, D14); nothing with none.
        int sidings = sidingsOf(d);
        if (sidings != 0) h = World.mix(h ^ 0x53494449L ^ ((long) sidings << 40));
        // ...and the works on the shore over its frame and those of its own the shore holds (0.7.97); nothing with none.
        if (!shore.isEmpty()) {
            h = World.mix(h ^ shore.frameHash(x0, y0, DistrictPlan.FRAME, DistrictPlan.FRAME));
            for (int t : shoreTypes()) { int s = shoreOf(d, t); if (s != 0) h = World.mix(h ^ ((long) t << 40) ^ s); }
        }
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
        /** What was carried to it and what it carries on: {month, code} pairs in the order they came (CL3; an arrival, or the departure ~k of the k-th pair's, DistrictPlan.Input.carry), null for none. */
        int[] carryIn, carryOut;
        Drawn result;
        long[] free, read;
        byte[][] margins;
        double ms;
        /** Its picture's (0.7.128, batch MM1): the boxes the runs and the shore stand on its ground (extrasIn()), the stamp of the picture kept for it when it was handed out (0 for none), its plan's plots' stamp (DistrictPicture.stampOf()), and what run() drew - null when the kept one is of the same plots. */
        final int[] extras;
        final long keptPicture;
        long pictureStamp;
        DistrictPicture picture;
        double pictureMs;

        PlanJob(CityMap map, District d, DistrictPlan.Input in, long own, PlanJob after, int[] given, boolean edge,
                District[] earlier, Object[] sources, long epoch) {
            super(map, epoch);
            this.d = d; this.in = in; this.own = own; this.after = after; this.given = given; this.edge = edge;
            this.earlier = earlier; this.sources = sources;
            this.extras = map.extrasIn(d);
            DistrictPicture kept = map.pictures.get(d);
            this.keptPicture = kept == null ? 0 : kept.stamp;
        }

        @Override void work() {
            long t0 = System.nanoTime();
            if (after != null) after.run();
            int[] c = after != null ? after.carryOut : given;
            readGround(in, map.worldKey);
            // What the district before it could not place, as {month, code} pairs, arrivals and departures: the plan merges it into its own events by month (CL3; counts added into its own before).
            in.carry = c;
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
            carryOut = p.overflowLog != null && p.overflowLog.length > 0 ? p.overflowLog.clone() : null;
            result = drawnOf(p, stampOf(own, c));
            margins = marginsOf(p);
            if (edge) free = freeGround(in, p);
            ms = (System.nanoTime() - t0) / 1e6;
            // Its picture (0.7.128, batch MM1), from the plan and the ground just read - unless the one kept is of the same plots.
            long t1 = System.nanoTime();
            byte[] plots = DistrictPicture.plotCodes(in, p, map.types, extras);
            pictureStamp = DistrictPicture.stampOf(plots, map.seed, in.x0, in.y0);
            if (pictureStamp != keptPicture) picture = DistrictPicture.of(plots, pictureStamp, map.seed, in.x0, in.y0);
            pictureMs = (System.nanoTime() - t1) / 1e6;
        }

        /** How long it took, in ms: a harness's figure (its plan's; its picture's apart, pictureMs()). */
        public double ms() { return ms; }

        /** How long its picture took, in ms - its plots, and its pixels when it drew them: a harness's figure. */
        public double pictureMs() { return pictureMs; }

        /** Whether it drew a picture (the one kept was not of its plots): a harness's figure. */
        public boolean drewPicture() { return picture != null; }
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
            result = packAtEdge(countsOf(p, types.length), types, edges, free, tileOrder);
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
            l.picture = p.pictureStamp;
            links.put(p.d, l);
            drawn.put(p.d, p.result);
            if (p.picture != null) {
                pictures.put(p.d, p.picture);
                picturesKept++;
            }
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

    /** The legend's figures once every plan and the packing are drawn (never planned here: the screen's thread): {buildings packed at the city's edge, plots of road its streets do not carry (star RD2-4) - since 0.7.89 with the highway plots the runs could not lay -, plots of track more than the line uses (FR7 slice 3: the model's track the main line, its sidings and its branches do not take; slice 5: less a dealt spur's plots each, dealtSpurTrack(), which the plans' sidings use)}; null until they are. */
    public long[] legendFiguresIfDrawn() {
        int[] p = packSums();
        if (p == null) return null;
        double surplus = runs.highwayUndrawn();
        for (District d : districts) surplus += links.get(d).surplus;
        return new long[] { p[0], Math.round(surplus), Math.max(0, runs.trackSurplus() - dealtSpurTrack()) };
    }

    /** The track plots of the Rail Spurs dealt to the plans' works (sidingsOf()), a spur's share of the model's spur track each: used by a siding the plans lay, so no longer the legend's "track more than the line uses" (batch FR7 slice 5). */
    long dealtSpurTrack() {
        long dealt = sidingsDealt();
        return spurCount <= 0 ? 0 : dealt * spurTrack / spurCount;
    }

    /** The road the plans have no street for, by kind [0, gravel, paved, highway], over every district (made now if need be), and (0.7.89) the Elevated Highway plots the runs could not lay: a harness's - with the surface drawn, the model's road kind by kind. */
    public double[] leftoverByKind() {
        packed();
        double[] out = new double[4];
        for (District d : districts) {
            Link l = links.get(d);
            if (l != null && l.leftover != null) for (int k = 0; k < 4; k++) out[k] += l.leftover[k];
        }
        out[BuildingVisual.HIGHWAY] += runs.highwayUndrawn();
        return out;
    }

    /** ...the same, made now if need be: a harness's. */
    public long[] legendFigures() {
        packed();
        return legendFiguresIfDrawn();
    }

    /** District d's plan as drawn (made now if need be): a harness's and a probe's. */
    public Drawn drawn(District d) { return drawnOf(d); }

    /** What was carried to district d and what it carried on (R7), counted by type id, null for none - its link's, once planned. (The link holds {month, code} pairs since CL3; countsOf() counts the arrivals less the departures.) */
    public int[][] carried(District d) {
        drawnOf(d);
        Link l = links.get(d);
        return l == null ? new int[2][] : new int[][] { countsOf(l.in, types.length), countsOf(l.out, types.length) };
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
                if (!in.owned[q] || !World.dry(in.terrain[q])) continue;
                if (taken[q] || p.street[q] != 0 || in.fixed[q] != 0 || in.site[q] == DistrictPlan.SITE_MINED || p.siding != null && p.siding[q] != 0) continue;
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
       THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md
       8.2)

       Each built district's 32 x 32 picture for the far views
       (DistrictPicture): drawn by its PlanJob from the plan it has just
       drawn - on the map view's worker, "city-map", or a harness's thread -
       and kept here by a stamp of the plots it is drawn from
       (DistrictPicture.stampOf(), star MM1-6), so a plan drawn again the
       same draws no picture and a month redraws only the districts whose
       plots it moved. Kept PICTURES_KEPT at most, the last
       used; never saved (star MM1-1): drawn again from the plans after a
       load. The far nodes read them (MapTiles.nodePixels()) and hand out
       the jobs that draw those they lack (pictureJobs()).
       ===================================================================== */

    /** How many districts' pictures are kept, the last used: 128 (star MM1-2) - a far view of up to 128 built districts (Jerus's city has 93) in DistrictPicture.BYTES each, 0.14 MB, within the view's 48 MB with the rest (MapTiles.budgetBytes(), 0.34 MB under it before them); a view of more asks for none (MapTiles.picturesFit()). */
    public static final int PICTURES_KEPT = 128;

    private final Map<District, DistrictPicture> pictures = new LinkedHashMap<>(PICTURES_KEPT * 2, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<District, DistrictPicture> e) { return size() > PICTURES_KEPT; }
    };

    /** How many pictures have been kept since the map was drawn, each drawn for plots the one kept before it was not: what the far view repaints a waiting node on, and a harness counts a month's redraws by. */
    private long picturesKept;

    /** The boxes the runs and the shore stand on district d's ground, as the painter draws them over its plan (fillInput()): its yards as Rail Terminals and its terminals and tank farms, each {x, y, w, h, type id} in the district's plots, the box's first plot in it. The map's thread. */
    int[] extrasIn(District d) {
        long x0 = (baseDX + d.dx) * DISTRICT, y0 = (baseDY + d.dy) * DISTRICT;
        List<int[]> boxes = new ArrayList<>();
        List<long[]> laid = runs.yards();
        for (int yi = 0; yi < laid.size(); yi++) {
            long[] yd = laid.get(yi);
            int terminal = yardTypeOf(yi);
            if (terminal < 0 || yd[0] < x0 || yd[1] < y0 || yd[0] >= x0 + DISTRICT || yd[1] >= y0 + DISTRICT) continue;
            boxes.add(new int[] { (int) (yd[0] - x0), (int) (yd[1] - y0), (int) yd[2], (int) yd[3], terminal });
        }
        for (CityShore.Work wk : shore.works()) {
            if (wk.x0() < x0 || wk.y0() < y0 || wk.x0() >= x0 + DISTRICT || wk.y0() >= y0 + DISTRICT) continue;
            boxes.add(new int[] { (int) (wk.x0() - x0), (int) (wk.y0() - y0), wk.w(), wk.h(), wk.type() });
        }
        int[] out = new int[5 * boxes.size()];
        for (int i = 0; i < boxes.size(); i++) System.arraycopy(boxes.get(i), 0, out, 5 * i, 5);
        return out;
    }

    /** District d's picture when it is its plan's, the plan current - every link before it in its band holding - else null: the screen's thread, which never plans. */
    public DistrictPicture pictureIfCurrent(District d) {
        if (d == null) return null;
        DistrictPicture pic = pictures.get(d);
        if (pic == null) return null;
        int b = band(d.index);
        checkBand(b);
        if (d.index >= bandValidTo[b]) return null;
        Link l = links.get(d);
        return l != null && pic.stamp == l.picture ? pic : null;
    }

    /**
     * The jobs that draw district d's picture: its plan's chain, as
     * planJobs() hands it out but for its band's packing at the edge, which
     * a picture does not draw (star MM1-3); or, where its plan is current
     * and its picture is not kept (let go past PICTURES_KEPT), its plan
     * drawn again for it. To run in order on one thread, then adopt(). Empty
     * when its picture is current. The screen's thread.
     */
    public List<Job> pictureJobs(District d) {
        List<Job> out = new ArrayList<>();
        if (d == null || pictureIfCurrent(d) != null) return out;
        useBand(band(d.index));
        if (chainTo(d.index, out) != null) return out;
        PlanJob p = pending.get(d);
        long e = planEpoch();
        if (p != null && p.epoch == e) return out;
        Link l = links.get(d);
        if (l == null) return out;
        District[] near = earlierOf(d);
        PlanJob j = new PlanJob(this, d, planInputHere(d), l.own, null, l.in, isEdge(d), near, sources(near), e);
        pending.put(d, j);
        out.add(j);
        return out;
    }

    /** District d's picture, drawn here when it is not current (pictureJobs() run and kept on this thread): a harness's and a probe's. */
    public DistrictPicture picture(District d) {
        for (int round = 0; round < 2; round++) {
            DistrictPicture p = pictureIfCurrent(d);
            if (p != null) return p;
            for (Job j : pictureJobs(d)) {
                j.run();
                adopt(j);
            }
        }
        return pictureIfCurrent(d);
    }

    /** How many pictures have been kept since the map was drawn (picturesKept). */
    public long picturesKept() { return picturesKept; }

    /** The pictures kept now: what MapTiles' budget counts PICTURES_KEPT of, DistrictPicture.BYTES each. */
    public int picturesHeld() { return pictures.size(); }

    /** The band district d's links are kept in (CHAIN_BAND districts of the map's order a band): what a far view counts the bands it would plan by (MapTiles.picturesFit()). */
    public static int bandOf(District d) { return band(d.index); }

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
       for a corridor - is counted, and the legend says so. Since 0.8.1
       (batch FR0) each net's spine lies toward the city's GATEWAY
       (Gateways: found once, at the first highway, track or yard, and kept
       in the sidecar, FORMAT 8), and worldLines() carries each net on from
       the arm that meets the city's edge on dry ground toward the world.
       Since FR7 slice 3 (2026-10-18) the railway is laid only on a train's
       way: its main line (the gate arm), a siding into a bulk mined site for
       each Rail Spur, a branch to a further yard beside the largest group of
       mines - and the track plots the model has past those are not drawn but
       counted, "plots of track more than the line uses" (CityRuns, THE
       RAILWAY'S WAYS; Jerus: "why all the other rails they are dead ends").
       A Rail Depot is a small yard of its own size and sidings; trainRoutes()
       runs a bulk unit train straight to each siding. Works the district
       plans place are not served by the runs: the plans are drawn round the
       runs, and a siding laid after them would move the plans' own ground.
       Since FR7 slice 5 (2026-10-19) they are served inside the plans: the
       spurs the runs' sidings left are dealt (sidingsOf()) to the districts
       the main line crosses that hold bulk works, and each plan lays a
       siding to its largest, after its buildings stand (DistrictPlan
       sidings()); the trains of those sidings are not drawn (trainRoutes()
       needs the plans' geometry, which it does not draw).
       ===================================================================== */

    /** Bumped whenever free ground is measured again (a purchase): what a run waiting on the ground is tried again on. */
    private long landVersion;

    /** The city's highways and railway (spec 2.7, 2.8). */
    private CityRuns runs = guarded(new CityRuns());

    /** The runs given the works an interchange's reserve may not take (batch FR7, InterchangeGround.Guard): the mines on their sites and the shore's works. */
    private CityRuns guarded(CityRuns r) {
        r.setReserveGuard(new InterchangeGround.Guard() {
            @Override public boolean blocked(long x0, long y0, long x1, long y1) { return reserveBlocked(x0, y0, x1, y1); }

            // Any change of the map - a building placed, a site worked, a work laid on the shore - may move a mine or a terminal.
            @Override public long version() { return changes; }
        });
        return r;
    }

    /** Whether a mine on its site or a work on the shore stands in the box (x0, y0) to (x1, y1), inclusive: ground an interchange's reserve may not take. */
    private boolean reserveBlocked(long x0, long y0, long x1, long y1) {
        for (CityShore.Work w : shore.works()) {
            if (w.x0() <= x1 && w.y0() <= y1 && w.x0() + w.w() > x0 && w.y0() + w.h() > y0) return true;
        }
        for (long[] s : minedSites()) if (s[0] <= x1 && s[1] <= y1 && s[2] >= x0 && s[3] >= y0) return true;
        return false;
    }

    /** The city's runs. */
    public CityRuns runs() { return runs; }

    /** The gateway's bearing, as CityRuns keeps it: GATEWAY_UNKNOWN until it is looked for, NO_GATEWAY where the land has none. */
    public int gateway() { return runs.gateway(); }

    /**
     * A net's line out to the world (spec-freight 4.1): the highway's or the
     * railway's run, from the first plot past the city's ground, faded to the
     * edge of the view and labelled "to the world".
     *
     * @param net     CityRuns.HIGHWAYS or CityRuns.RAILWAY
     * @param x       the point's x in plots where the line leaves the city's ground
     * @param y       the point's y in plots where the line leaves the city's ground
     * @param bearing the gateway's bearing in degrees
     * @param arm     the index in its net's arms of the arm the line leaves the city's ground from (FX2)
     * @param way     the degrees the line runs out along (x = cos, y = sin): the gateway's bearing, or the arm's heading times 45
     * @param reach   the plots of dry ground along way from (x, y), at most WORLD_REACH
     */
    public record WorldLine(int net, double x, double y, int bearing, int arm, int way, double reach) { }

    /** How far ahead of a track's end the city's edge may lie for its line to the world to start there, in plots: 12, twice CityRuns.LOOK - an arm waits LOOK short of the edge, a 45-degree arm sooner (FX2). */
    public static final int WORLD_GAP = 2 * CityRuns.LOOK;

    /** How far the world line is drawn at most, in plots: 1,024 (30.7 km), four districts; it stops sooner at the first plot that is not dry ground (FX2). */
    public static final int WORLD_REACH = 4 * DISTRICT;

    /** How finely a way out is read, in plots: a quarter, as MapCheck reads the trains' routes (FX2). */
    public static final double WORLD_STEP = 0.25;

    /** worldLines()'s last answer, kept against the map's changes: recomputed when a purchase or a month's runs move changes(). */
    private List<WorldLine> worldLinesCache;
    private long worldLinesAt = Long.MIN_VALUE;

    /**
     * The world lines (spec-freight 4.1): for each net whose gateway is found
     * and which has started, the line to the world that worldLine() finds -
     * where an arm's track ends at the city's edge on dry ground, and the way
     * out over dry ground from there; none for a net with no such arm. FR0
     * walked the gate arm on to the first plot the city did not own, across
     * the city's own sea, and Jerus saw the line and a train over open sea
     * in a V (FX2). Each time it is found it sets the runs' main arm to the
     * railway line's arm (CityRuns.mainArm()): the main line, the freight
     * station and the trains' way run from there. Kept against changes().
     */
    public List<WorldLine> worldLines() {
        if (worldLinesCache != null && worldLinesAt == changes) return worldLinesCache;
        List<WorldLine> out = new ArrayList<>();
        int gateway = runs.gateway();
        WorldLine railLine = null;
        if (gateway >= 0) {
            worldLine(runs.highways(), gateway, out);
            railLine = worldLine(runs.rail(), gateway, out);
        }
        runs.setMainArm(railLine == null ? -1 : railLine.arm());
        worldLinesCache = out;
        worldLinesAt = changes;
        return out;
    }

    /** One way out of the city from an arm at its edge: the score it is tried by, the arm, 0 for the gateway's bearing or 1 for the arm's heading, the degrees, and the edge plot. */
    private record WayOut(double score, int arm, int which, int way, long ex, long ey) { }

    /**
     * Adds net n's line to the world to out and returns it; null (none added) for a net that has not started, for the
     * highways while they reach no street (CityRuns.highwaysReach(), FX2: they are not drawn), or that has no arm to
     * start it from. An arm with steps is read from its end along its heading up to WORLD_GAP plots: an owned
     * plot that is not dry ground ends the read (the arm is at the water, no edge), the first plot the city does not
     * own is the arm's edge plot where it is dry ground (else none), and no unowned plot in reach means the arm is not
     * at the edge. An arm at the edge offers up to two ways out over the land beyond - the gateway's bearing, when it
     * lies within 90 degrees of the arm's heading, scored 1, and the arm's own heading, scored by how near it lies to
     * the bearing - tried best score first (then the lower arm, then the bearing before the heading); the first that
     * runs TrainSchedule.WORLD_PLOTS plots over dry ground wins, and its line starts in the middle of the edge plot
     * and runs on over dry ground (dryRun()) up to WORLD_REACH.
     */
    private WorldLine worldLine(CityRuns.Net n, int gateway, List<WorldLine> out) {
        if (!n.started || (n.kind == CityRuns.HIGHWAYS && !runs.highwaysReach())) return null;
        World w = World.of(worldKey);
        List<WayOut> offers = new ArrayList<>();
        for (int i = 0; i < n.arms.size(); i++) {
            CityRuns.Arm a = n.arms.get(i);
            if (a.steps <= 0) continue;
            // The railway's branches and sidings end at a yard and a works, never at the world (FR7 slice 3): only its spine meets it.
            if (n.kind == CityRuns.RAILWAY && a.corridor >= CityRuns.BRANCH) continue;
            int dx = CityRuns.DX[a.head], dy = CityRuns.DY[a.head];
            long ex = 0, ey = 0;
            boolean edge = false;
            for (int s = 1; s <= WORLD_GAP; s++) {
                long px = a.x + (long) dx * s, py = a.y + (long) dy * s;
                int g = runGround.at(px, py, -1);
                if (g == -1) {
                    edge = World.dry(w.terrainAt(px, py));
                    ex = px;
                    ey = py;
                    break;
                }
                if (!World.dry(g)) break;
            }
            if (!edge) continue;
            // The angle between the heading and the bearing, folded to 0 to 180 so that two arms' scores tie exactly.
            int heading = a.head * 45, apart = Math.floorMod(heading - gateway, 360);
            if (apart > 180) apart = 360 - apart;
            if (apart < 90) offers.add(new WayOut(1, i, 0, gateway, ex, ey));
            offers.add(new WayOut(Math.cos(Math.toRadians(apart)), i, 1, heading, ex, ey));
        }
        offers.sort((p, q) -> p.score() != q.score() ? Double.compare(q.score(), p.score())
                : p.arm() != q.arm() ? Integer.compare(p.arm(), q.arm()) : Integer.compare(p.which(), q.which()));
        for (WayOut o : offers) {
            double x = o.ex() + 0.5, y = o.ey() + 0.5;
            if (dryRun(w, x, y, o.way(), TrainSchedule.WORLD_PLOTS) < TrainSchedule.WORLD_PLOTS) continue;
            WorldLine line = new WorldLine(n.kind, x, y, gateway, o.arm(), o.way(), dryRun(w, x, y, o.way(), WORLD_REACH));
            out.add(line);
            return line;
        }
        return null;
    }

    /** How far from (x, y) the way out along deg degrees runs over dry ground, in plots: the distance to the first point, read every WORLD_STEP, that lies on a plot that is not World.dry(); `most` where none does. */
    private static double dryRun(World w, double x, double y, int deg, double most) {
        double c = Math.cos(Math.toRadians(deg)), s = Math.sin(Math.toRadians(deg));
        for (int i = 0; i * WORLD_STEP <= most; i++) {
            double t = i * WORLD_STEP;
            if (!World.dry(w.terrainAt((long) Math.floor(x + c * t), (long) Math.floor(y + s * t)))) return t;
        }
        return most;
    }

    /* =====================================================================
       THE TRAINS' ROUTES (0.8.2, batch FR1; spec-freight 4.4, D16)
       ===================================================================== */

    /** trainRoutes()'s last answer, kept against the map's changes. */
    private List<TrainSchedule.Route> trainRoutesCache;
    private long trainRoutesAt = Long.MIN_VALUE;

    /** A bend of the routes the tiles draw (FX5), with whether it lies on double track (its corner plot's M_DOUBLE). */
    private record DrawnBend(TrackCurves.Bend bend, boolean doubled) { }

    /** The railway's bends the tiles draw, by the tiles they reach (bendKey()), made with the routes and kept with them. */
    private Map<Long, List<DrawnBend>> trainBends = new HashMap<>();

    /** A tile's key in trainBends. */
    private static long bendKey(long tx, long ty) { return tx * 2_000_003L + ty; }

    /**
     * The trains' routes (TrainSchedule.Route): for each yard the railway's
     * way reaches, from TrainSchedule.WORLD_PLOTS out along the railway's
     * world line (along line.way(), over dry ground: FX2), in to the main
     * arm's end (the arm the line leaves the city's ground from) and along the
     * main line and the corridors (CityRuns.wayTo()) to where the way enters the
     * yard's box,
     * then through its ladder onto each of its TileRaster.YARD_TRACKS sidings
     * to the fan's far end, as TileRaster.drawYard() lays the fan: one route a
     * yard and siding. Since FX3 (0.8.4) the fan is TileRaster.yardLayout()'s,
     * read from the box's track (yardRail()), so a way that crosses a yard
     * along another line than its fan's is brought along it to the fan's
     * throat, and no siding is laid across a drawn one. A yard the way enters
     * on a 45-degree stretch has one route, to its anchor. Since FR7 (slice 3)
     * a Rail Depot's yard has TileRaster.DEPOT_TRACKS sidings, not a Terminal's
     * YARD_TRACKS, and each of the works' sidings (CityRuns.sidings()) has one
     * more route, TrainSchedule.Route.toWorks(): the world line, the main line
     * and straight into the siding's end, no yard - a bulk unit train's alone.
     * None without the railway's world line. Since FX5 (0.8.15) each route is
     * rounded at its turns (TrackCurves.round(), TrackCurves.RADIUS plots, the
     * world line's part ending where the rounded route leaves its first
     * straight) and each siding's ladder is TrackCurves.ladder()'s S, so no
     * train swings on the spot; the turns found, those on the railway's own
     * plots, are the bends the tiles draw (Input.bends), kept with the routes.
     * The map's alone: nothing in the model reads it.
     * Kept against changes().
     */
    public List<TrainSchedule.Route> trainRoutes() {
        if (trainRoutesCache != null && trainRoutesAt == changes) return trainRoutesCache;
        List<TrainSchedule.Route> out = new ArrayList<>();
        List<TrackCurves.Bend> found = new ArrayList<>();
        WorldLine line = null;
        for (WorldLine l : worldLines()) if (l.net() == CityRuns.RAILWAY) line = l;
        if (line != null) {
            double r = Math.toRadians(line.way());
            double fx = line.x() + Math.cos(r) * TrainSchedule.WORLD_PLOTS, fy = line.y() + Math.sin(r) * TrainSchedule.WORLD_PLOTS;
            long[] station = runs.freightStation();
            List<long[]> yards = runs.yards();
            for (int i = 0; i < yards.size(); i++) {
                long[] yard = yards.get(i);
                double[][] way = runs.wayTo(yard);
                if (way == null) continue;
                boolean isStation = station != null && Arrays.equals(station, yard);
                int ty = yardTypeOf(i);
                trainRoutesInto(out, i, isStation, yard, way, fx, fy, line.x(), line.y(), yardRail(runs, yard),
                        ty < 0 ? TileRaster.YARD_TRACKS : TileRaster.tracksOf(types[ty]), found);
            }
            // A bulk unit train runs from the world line along the main line straight into a works' siding, no yard (FR7 slice 3).
            for (long[] siding : runs.sidings()) {
                double[][] way = runs.wayToPlot(siding[3], siding[4]);
                if (way == null) continue;
                List<double[]> pts = new ArrayList<>();
                pts.add(new double[] { fx, fy });
                pts.add(new double[] { line.x(), line.y() });
                for (int k = 0; k < way[0].length; k++) addNew(pts, way[0][k], way[1][k]);
                out.add(routeToWorks(pts, found));
            }
        }
        trainBends = drawnBends(found);
        trainRoutesCache = out;
        trainRoutesAt = changes;
        return out;
    }

    /**
     * The bends the routes make (TrackCurves.bends() of each route's points
     * before it is rounded) that the tiles draw (FX5), by the tiles they reach:
     * those whose corner and both tangent points lie on plots the runs lay
     * track on (a yard's box counts where its own track is: yardRail()), each
     * once however many routes make it, with whether its corner plot is double
     * track. The turn at the world line's join, out beyond the city's track,
     * is the routes' alone: no track is drawn there.
     */
    private Map<Long, List<DrawnBend>> drawnBends(List<TrackCurves.Bend> found) {
        Map<Long, List<DrawnBend>> byTile = new HashMap<>();
        java.util.Set<List<Long>> seen = new java.util.HashSet<>();
        List<long[]> yards = runs.yards();
        boolean[][] yardRails = new boolean[yards.size()][];
        for (TrackCurves.Bend b : found) {
            if (!seen.add(java.util.Arrays.asList(Math.round(b.x() * 1e6), Math.round(b.y() * 1e6), Math.round(b.ux() * 1e6), Math.round(b.uy() * 1e6),
                    Math.round(b.vx() * 1e6), Math.round(b.vy() * 1e6), Math.round(b.r() * 1e6)))) continue;
            if (!railPlot(yards, yardRails, (long) Math.floor(b.x()), (long) Math.floor(b.y()))
                    || !railPlot(yards, yardRails, (long) Math.floor(b.startX()), (long) Math.floor(b.startY()))
                    || !railPlot(yards, yardRails, (long) Math.floor(b.endX()), (long) Math.floor(b.endY()))) continue;
            byte[] f = new byte[1], mk = new byte[1];
            runs.fill((long) Math.floor(b.x()), (long) Math.floor(b.y()), 1, 1, f, mk, false);
            DrawnBend d = new DrawnBend(b, (mk[0] & TilePainter.M_DOUBLE) != 0);
            // The tiles its path and its bed may touch: its corner, its tangent points and the corner's plot, a plot beyond.
            double x0 = Math.min(b.x(), Math.min(b.startX(), b.endX())) - 1, x1 = Math.max(b.x(), Math.max(b.startX(), b.endX())) + 1;
            double y0 = Math.min(b.y(), Math.min(b.startY(), b.endY())) - 1, y1 = Math.max(b.y(), Math.max(b.startY(), b.endY())) + 1;
            for (long ty = Math.floorDiv((long) Math.floor(y0), World.TILE); ty <= Math.floorDiv((long) Math.floor(y1), World.TILE); ty++) {
                for (long tx = Math.floorDiv((long) Math.floor(x0), World.TILE); tx <= Math.floorDiv((long) Math.floor(x1), World.TILE); tx++) {
                    byTile.computeIfAbsent(bendKey(tx, ty), k -> new ArrayList<>()).add(d);
                }
            }
        }
        return byTile;
    }

    /** Whether the runs lay track on plot (x, y): track, track bridging a highway, or the track in a yard's box (yardRails caches each yard's, by index). */
    private boolean railPlot(List<long[]> yards, boolean[][] yardRails, long x, long y) {
        byte f = runs.at(x, y);
        if (f == CityRuns.F_RAIL || f == CityRuns.F_RAIL_OVER) return true;
        if (f != CityRuns.F_YARD) return false;
        for (int i = 0; i < yards.size(); i++) {
            long[] yd = yards.get(i);
            if (x < yd[0] || y < yd[1] || x >= yd[0] + yd[2] || y >= yd[1] + yd[3]) continue;
            if (yardRails[i] == null) yardRails[i] = yardRail(runs, yd);
            return yardRails[i][(int) ((y - yd[1]) * yd[2] + (x - yd[0]))];
        }
        return false;
    }

    /** The yard layouts (TileRaster.yardLayout() of yardRail()) as the map's changes left them: valid at `at`, one per yard of the runs, made when first asked. */
    private record YardLayouts(long at, int[][] by) { }

    private volatile YardLayouts yardLayouts;

    /**
     * Where yard yi's fan lies in its box (FX5 Y2): TileRaster.yardLayout() of the box's track as the runs lay it (yardRail()), the layout
     * trainRoutesInto() lays the routes on and the tiles draw the yard from. Kept per yard against the map's changes, so a tile paints
     * without running the runs over the yard's box again.
     */
    int[] yardLayoutOf(int yi) {
        List<long[]> yards = runs.yards();
        YardLayouts c = yardLayouts;
        if (c == null || c.at() != changes || c.by().length != yards.size()) {
            c = new YardLayouts(changes, new int[yards.size()][]);
            yardLayouts = c;
        }
        int[] got = c.by()[yi];
        if (got == null) {
            long[] yd = yards.get(yi);
            got = TileRaster.yardLayout(yardRail(runs, yd), (int) yd[2], (int) yd[3]);
            c.by()[yi] = got;
        }
        return got;
    }

    /** The track in a yard's box, plot by plot (row by row, yard[2] across and yard[3] down): where the runs lay the railway, as the tile the yard is drawn on carries it (TileRaster.yardLayout()'s input; FX3, 0.8.4). */
    static boolean[] yardRail(CityRuns runs, long[] yard) {
        int w = (int) yard[2], h = (int) yard[3];
        byte[] fixed = new byte[w * h];
        runs.fill(yard[0], yard[1], w, h, fixed, null, false);
        boolean[] rail = new boolean[w * h];
        for (int k = 0; k < rail.length; k++) rail[k] = fixed[k] == CityRuns.F_RAIL;
        return rail;
    }

    /**
     * Adds one yard's routes to out: the world line's part (far end, then
     * where it leaves the city's ground), the way to the box's edge, and the
     * ladder onto each siding. Since FX3 (0.8.4) the fan is laid from
     * TileRaster.yardLayout() of the box's track (rail, as yardRail() reads
     * it), as drawYard() lays it: the way is followed from the box's edge to
     * where it first stands on the fan's line (the interchange, where another
     * line crosses the box; a way that ends short of it, its anchor on the
     * crossing track, is carried straight on over the track to it), along the
     * line to the nearer end of the fan's span, and up the ladder onto each
     * siding. Without rail (null) the fan lies along the leg the way enters
     * the box on, as before; a way that never reaches the line has one route,
     * to its anchor.
     */
    static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way,
                                        double fx, double fy, double wx, double wy, boolean[] rail) {
        trainRoutesInto(out, yardAt, station, yard, way, fx, fy, wx, wy, rail, TileRaster.YARD_TRACKS);
    }

    /** ...with the yard's own number of sidings (TileRaster.tracksOf(): a Rail Depot's three, FR7 slice 3), one route to each. */
    static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way,
                                        double fx, double fy, double wx, double wy, boolean[] rail, int tracks) {
        trainRoutesInto(out, yardAt, station, yard, way, fx, fy, wx, wy, rail, tracks, null);
    }

    /**
     * ...and (FX5) the bends of the routes' points before they are rounded
     * (TrackCurves.bends()) added to bendsOut, where it is not null: the
     * turns the tiles draw. Every route is rounded (rounded()), and each
     * siding's ladder is TrackCurves.ladder()'s S, as TileRaster.drawYard()
     * draws it.
     */
    static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way,
                                        double fx, double fy, double wx, double wy, boolean[] rail, int tracks,
                                        List<TrackCurves.Bend> bendsOut) {
        double[] xs = way[0], ys = way[1];
        int n = xs.length;
        double bx0 = yard[0], by0 = yard[1], bx1 = yard[0] + yard[2], by1 = yard[1] + yard[3];
        // The last point of the way outside the box: the way enters it on the stretch after.
        int j = n - 1;
        while (j >= 0 && xs[j] >= bx0 && xs[j] < bx1 && ys[j] >= by0 && ys[j] < by1) j--;
        if (j < 0 || j == n - 1) return;
        List<double[]> pts = new ArrayList<>();
        pts.add(new double[] { fx, fy });
        pts.add(new double[] { wx, wy });
        for (int i = 0; i <= j; i++) pts.add(new double[] { xs[i], ys[i] });
        double ux = Math.signum(xs[j + 1] - xs[j]), uy = Math.signum(ys[j + 1] - ys[j]);
        if (ux != 0 && uy != 0) {
            // A 45-degree way in: on to the anchor, no siding.
            for (int i = j + 1; i < n; i++) pts.add(new double[] { xs[i], ys[i] });
            out.add(route(yardAt, station, -1, pts, bendsOut));
            return;
        }
        // Where the way crosses the box's edge.
        double ex = ux != 0 ? (ux > 0 ? bx0 : bx1) : xs[j], ey = ux != 0 ? ys[j] : (uy > 0 ? by0 : by1);
        // The fan: the yard's layout from its track (FX3), else along the leg the way comes in on. Its line in world plots, and its span along the line.
        int[] layout = rail == null ? null : TileRaster.yardLayout(rail, (int) yard[2], (int) yard[3]);
        if (layout != null && layout[1] < 0) layout = null;
        boolean ew = layout != null ? layout[0] == 1 : ux != 0;
        double lineAt = layout != null ? (ew ? by0 : bx0) + layout[1] + 0.5 : ux != 0 ? ys[j] : xs[j];
        double s0 = ew ? bx0 : by0, s1 = ew ? bx1 : by1;
        if (layout != null) {
            s1 = s0 + layout[3];
            s0 += layout[2];
        }
        double across0 = ew ? by0 : bx0, across1 = ew ? by1 : bx1;
        // On from the box's edge along the way to where it first stands on the line: at a point of it, or where one of its legs crosses.
        pts.add(new double[] { ex, ey });
        double[] q = null;
        double[] prev = { ex, ey };
        List<double[]> inside = new ArrayList<>();
        for (int i = j + 1; i < n && q == null; i++) {
            double[] cur = { xs[i], ys[i] };
            double pa = (ew ? prev[1] : prev[0]) - lineAt, pb = (ew ? cur[1] : cur[0]) - lineAt;
            if (Math.abs(pa) < 1e-9) q = prev;
            else if (Math.abs(pb) < 1e-9) q = cur;
            else if (pa * pb < 0) {
                double t = pa / (pa - pb);
                q = new double[] { prev[0] + t * (cur[0] - prev[0]), prev[1] + t * (cur[1] - prev[1]) };
            } else {
                inside.add(cur);
                prev = cur;
            }
        }
        if (q == null && layout != null) {
            // The way ends short of the line (its anchor stands on the crossing track, beside the line): carried straight on over the box's track to the line, if the track reaches it.
            double[] last = { xs[n - 1], ys[n - 1] };
            double dx = Math.signum(last[0] - xs[n - 2]), dy = Math.signum(last[1] - ys[n - 2]);
            double gap = ew ? lineAt - last[1] : lineAt - last[0];
            if ((ew ? dx == 0 && dy != 0 : dy == 0 && dx != 0) && gap * (ew ? dy : dx) > 0) {
                long px = (long) Math.floor(last[0]), py = (long) Math.floor(last[1]);
                long toX = ew ? px : (long) Math.floor(lineAt), toY = ew ? (long) Math.floor(lineAt) : py;
                boolean track = true;
                while (track && (px != toX || py != toY)) {
                    px += (long) dx;
                    py += (long) dy;
                    long cx = px - (long) bx0, cy = py - (long) by0;
                    track = cx >= 0 && cy >= 0 && cx < yard[2] && cy < yard[3] && rail[(int) (cy * yard[2] + cx)];
                }
                if (track) q = ew ? new double[] { last[0], lineAt } : new double[] { lineAt, last[1] };
            }
        }
        if (q == null) {
            // The way never stands on the fan's line: on to the anchor, no siding.
            for (int i = j + 1; i < n; i++) pts.add(new double[] { xs[i], ys[i] });
            out.add(route(yardAt, station, -1, pts, bendsOut));
            return;
        }
        pts.addAll(inside);
        addNew(pts, q[0], q[1]);
        // Along the line to the nearer end of the span: the fan's apex, where the ladder begins.
        boolean fromStart = (ew ? q[0] : q[1]) <= (s0 + s1) / 2.0;
        double apex = fromStart ? s0 : s1, u = fromStart ? 1 : -1;
        addNew(pts, ew ? apex : lineAt, ew ? lineAt : apex);
        int k = tracks;
        double[] at = new double[k];
        for (int i = 0; i < k; i++) at[i] = lineAt + (i - (k - 1) / 2.0) * TileRaster.YARD_TRACK_APART;
        // The fan's whole bed inside the box, as the painter keeps it.
        double lo = at[0] - TileRaster.BED / 2, hi = at[k - 1] + TileRaster.BED / 2, shift = lo < across0 ? across0 - lo : hi > across1 ? across1 - hi : 0;
        double b = (fromStart ? s1 : s0) - u * TileRaster.YARD_LADDER;
        // The way up to the fan's apex is rounded once for all the sidings; each siding's ladder is TrackCurves.ladder()'s S, as the yard is drawn (FX5).
        Rounded base = rounded(pts, bendsOut);
        for (int s = 0; s < k; s++) {
            double off = at[s] + shift - lineAt;
            double[][] lad = TrackCurves.ladder(ew ? apex : lineAt, ew ? lineAt : apex, ew ? u : 0, ew ? 0 : u, ew ? 0 : 1, ew ? 1 : 0, TileRaster.YARD_LADDER, off);
            int m = base.xs().length, l = lad[0].length;
            double[] rx = new double[m + l], ry = new double[m + l];
            System.arraycopy(base.xs(), 0, rx, 0, m);
            System.arraycopy(base.ys(), 0, ry, 0, m);
            // The apex is the base's last point and the ladder's first: the ladder follows, then the siding on to the fan's far end.
            int w = m;
            for (int qi = 1; qi < l; qi++) { rx[w] = lad[0][qi]; ry[w] = lad[1][qi]; w++; }
            rx = Arrays.copyOf(rx, w + 1);
            ry = Arrays.copyOf(ry, w + 1);
            rx[w] = ew ? b : lineAt + off;
            ry[w] = ew ? lineAt + off : b;
            out.add(TrainSchedule.Route.through(yardAt, station, s, rx, ry, base.fadeTo()));
        }
    }

    /** A polyline's points rounded by TrackCurves (RADIUS), with the world line's part: how far along the rounded polyline its first straight lasts. */
    private record Rounded(double[] xs, double[] ys, double fadeTo) { }

    /**
     * The route's points rounded (FX5): every turn replaced by its arc of
     * TrackCurves.RADIUS plots, less where the legs beside it are short; the
     * turns found are added to bendsOut where it is not null. fadeTo is the
     * world line's part, the along-distance of the point where the rounded
     * route leaves the first leg's straight: the leg's length, less the
     * tangent length of the turn at its far end, where there is one.
     */
    private static Rounded rounded(List<double[]> pts, List<TrackCurves.Bend> bendsOut) {
        double[] xs = new double[pts.size()], ys = new double[pts.size()];
        for (int i = 0; i < pts.size(); i++) { xs[i] = pts.get(i)[0]; ys[i] = pts.get(i)[1]; }
        double fade = Math.hypot(xs[1] - xs[0], ys[1] - ys[0]);
        List<TrackCurves.Bend> bends = TrackCurves.bends(xs, ys, TrackCurves.RADIUS);
        for (TrackCurves.Bend b : bends) if (b.x() == xs[1] && b.y() == ys[1]) fade -= b.t();
        if (bendsOut != null) bendsOut.addAll(bends);
        double[][] r = TrackCurves.round(xs, ys, TrackCurves.RADIUS);
        return new Rounded(r[0], r[1], fade);
    }

    /** Adds the point (x, y) to pts unless the last one is there (no leg of no length). */
    private static void addNew(List<double[]> pts, double x, double y) {
        double[] last = pts.get(pts.size() - 1);
        if (Math.abs(last[0] - x) < 1e-9 && Math.abs(last[1] - y) < 1e-9) return;
        pts.add(new double[] { x, y });
    }

    /** A route through points, rounded at every turn (FX5), the world line's part its first leg's straight. */
    private static TrainSchedule.Route route(int yard, boolean station, int siding, List<double[]> pts, List<TrackCurves.Bend> bendsOut) {
        Rounded r = rounded(pts, bendsOut);
        return TrainSchedule.Route.through(yard, station, siding, r.xs(), r.ys(), r.fadeTo());
    }

    /** ...to a works' siding (FR7 slice 3): through the points, rounded, the world line's part its first leg's straight, no yard; a bulk unit train's alone. */
    private static TrainSchedule.Route routeToWorks(List<double[]> pts, List<TrackCurves.Bend> bendsOut) {
        Rounded r = rounded(pts, bendsOut);
        return TrainSchedule.Route.toWorks(r.xs(), r.ys(), r.fadeTo());
    }

    /* =====================================================================
       THE TRUCKS' ROUTES (0.8.8, batch FR5; spec-freight 4.7, D21)

       Marks run between the depots, yards, ports and works along the
       highway and the access roads. A truck is a schedule entry, as a train
       and a boat are (TruckSchedule): this finds the ways it drives and
       nothing else. Two kinds: the highways' (truckRoutes()), from the world
       line out at the city's edge in along the highways to each of their
       ramps, found as the trains' are (CityRuns.wayOn() on the highways net);
       and each drawn district's access roads (accessRoutesIn()), from the
       road's end on the street grid to the gate of the work it serves,
       traced when the plan is packed (accessWays(), Drawn.access) and never
       by drawing a plan here. Nothing in the model reads them.
       ===================================================================== */


    /** The least a highway route runs along the highways, in plots, past the world line's own: shorter is the arm the line leaves from, where the trucks have no road to drive. */
    public static final double TRUCK_ROUTE_LEAST = 4;

    /** The most highway routes kept, 40: one to each ramp, the arms' order, a city of many arms is cut here (a route is a lane of trucks, and the legend would not read more). */
    public static final int TRUCK_ROUTES_MOST = 40;

    /** How far an access road's trucks fade in off the street grid, in plots: 3. */
    public static final double TRUCK_ACCESS_FADE = 3;

    /** truckRoutes()'s last answer, kept against the map's changes. */
    private List<TruckSchedule.Route> truckRoutesCache;
    private long truckRoutesAt = Long.MIN_VALUE;

    /**
     * The trucks' highway routes (TruckSchedule.Route, world true): for each
     * ramp the highways have (CityRuns.highwayRamps(): their arms' ends),
     * from TruckSchedule.WORLD_PLOTS out along the highways' world line (along
     * line.way(), over dry ground), in to where the line leaves the city's
     * ground, to the end of the arm it leaves from and along the highways to
     * the ramp (CityRuns.highwayWayTo(): wayOn() on the highways net, as the
     * railway's wayToPlot() is), points in plots, each a plot's middle; fadeTo
     * is the world part's length. A ramp the way does not reach, one that runs
     * under TRUCK_ROUTE_LEAST plots along the highways (the arm the line leaves
     * from) and a second route to the same plot are left out, and no more than
     * TRUCK_ROUTES_MOST are kept. None without the highways' world line, which
     * exists only while they reach a street (CityRuns.highwaysReach()). The
     * map's alone: nothing in the model reads it. Kept against changes().
     */
    public List<TruckSchedule.Route> truckRoutes() {
        if (truckRoutesCache != null && truckRoutesAt == changes) return truckRoutesCache;
        List<TruckSchedule.Route> out = new ArrayList<>();
        WorldLine line = null;
        for (WorldLine l : worldLines()) if (l.net() == CityRuns.HIGHWAYS) line = l;
        if (line != null) {
            double r = Math.toRadians(line.way());
            double fx = line.x() + Math.cos(r) * TruckSchedule.WORLD_PLOTS, fy = line.y() + Math.sin(r) * TruckSchedule.WORLD_PLOTS;
            java.util.Set<String> ends = new java.util.HashSet<>();
            for (long[] ramp : runs.highwayRamps()) {
                if (out.size() >= TRUCK_ROUTES_MOST) break;
                double[][] way = runs.highwayWayTo(line.arm(), ramp[0], ramp[1]);
                if (way == null || way[0].length == 0) continue;
                double along = 0;
                for (int k = 1; k < way[0].length; k++) along += Math.hypot(way[0][k] - way[0][k - 1], way[1][k] - way[1][k - 1]);
                if (along < TRUCK_ROUTE_LEAST || !ends.add(ramp[0] + "," + ramp[1])) continue;
                List<double[]> pts = new ArrayList<>();
                pts.add(new double[] { fx, fy });
                pts.add(new double[] { line.x(), line.y() });
                for (int k = 0; k < way[0].length; k++) addNew(pts, way[0][k], way[1][k]);
                out.add(truckRoute(true, pts, Math.hypot(line.x() - fx, line.y() - fy)));
            }
        }
        truckRoutesCache = out;
        truckRoutesAt = changes;
        return out;
    }

    /** A truck route through points, fadeTo plots faded in. */
    private static TruckSchedule.Route truckRoute(boolean world, List<double[]> pts, double fadeTo) {
        double[] xs = new double[pts.size()], ys = new double[pts.size()];
        for (int i = 0; i < pts.size(); i++) { xs[i] = pts.get(i)[0]; ys[i] = pts.get(i)[1]; }
        return TruckSchedule.Route.through(world, xs, ys, fadeTo);
    }

    /** One district's access roads' routes, with the stable key it is told apart by: the district's own, the same every call. */
    public record DistrictRoutes(long key, List<TruckSchedule.Route> routes) { }

    /** A district's routes as last made, and what they were made against: its Drawn's stamp and its frame's origin in world plots. */
    private static final class AccessRoutes {
        long stamp, x0, y0;
        DistrictRoutes routes;
    }

    private final Map<Long, AccessRoutes> accessRoutesCache = new LinkedHashMap<>(PLANS_KEPT * 4, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, AccessRoutes> e) { return size() > 2 * PLANS_KEPT; }
    };

    /**
     * The access roads' routes (TruckSchedule.Route, world false) of the
     * districts already drawn whose frame meets the box (x0, y0) to (x1, y1),
     * in world plots, the corners inclusive: for each district its roads,
     * each from the road's end on the street grid through the corners of its
     * plots (a straight run kept to its ends) to the gate of the work it
     * serves, fadeTo TRUCK_ACCESS_FADE; a district with no road, or whose
     * plan is not drawn and current, is left out. A plan is never drawn here
     * (the screen's thread): the roads were traced when it was packed
     * (Drawn.access). Each district's routes are kept against its Drawn's
     * stamp, so a call with no change answers the same lists. The key is the
     * district's own, the same every call.
     */
    public List<DistrictRoutes> accessRoutesIn(long x0, long y0, long x1, long y1) {
        List<DistrictRoutes> out = new ArrayList<>();
        if (x1 < x0 || y1 < y0 || districts.isEmpty()) return out;
        // The frames reach DISTRICT plots past their origin (257 plots): a box meets the frame of every district from the one before its corner.
        long cx0 = Math.floorDiv(x0 - 1, DISTRICT) - baseDX, cx1 = Math.floorDiv(x1, DISTRICT) - baseDX;
        long cy0 = Math.floorDiv(y0 - 1, DISTRICT) - baseDY, cy1 = Math.floorDiv(y1, DISTRICT) - baseDY;
        if ((cx1 - cx0 + 1) * (cy1 - cy0 + 1) <= districts.size()) {
            for (long dy = cy0; dy <= cy1; dy++) {
                for (long dx = cx0; dx <= cx1; dx++) {
                    District d = byKey.get(key((int) dx, (int) dy));
                    if (d != null) accessRoutesOf(d, out);
                }
            }
        } else {
            for (District d : districts) if (d.dx >= cx0 && d.dx <= cx1 && d.dy >= cy0 && d.dy <= cy1) accessRoutesOf(d, out);
        }
        return out;
    }

    /** Adds district d's access routes to out when its plan is drawn and current and it has any. */
    private void accessRoutesOf(District d, List<DistrictRoutes> out) {
        int b = band(d.index);
        checkBand(b);
        if (d.index >= bandValidTo[b]) return;
        Drawn got = drawn.get(d);
        Link l = links.get(d);
        if (got == null || l == null || got.stamp != l.stamp()) return;
        long ox = (baseDX + d.dx) * DISTRICT, oy = (baseDY + d.dy) * DISTRICT;
        AccessRoutes ar = accessRoutesCache.get(d.key);
        if (ar == null || ar.stamp != got.stamp || ar.x0 != ox || ar.y0 != oy) {
            ar = new AccessRoutes();
            ar.stamp = got.stamp;
            ar.x0 = ox;
            ar.y0 = oy;
            List<TruckSchedule.Route> routes = new ArrayList<>();
            if (got.access != null) for (int[] way : got.access) accessRouteInto(routes, way, ox, oy);
            ar.routes = routes.isEmpty() ? null : new DistrictRoutes(d.key, java.util.Collections.unmodifiableList(routes));
            accessRoutesCache.put(d.key, ar);
        }
        if (ar.routes != null) out.add(ar.routes);
    }

    /** Adds the route of one access road (Drawn.access: frame indices, the end first) to out: its plots' middles in world plots from the frame's origin (ox, oy), a straight run kept to its two ends. A way of one plot makes none. */
    private static void accessRouteInto(List<TruckSchedule.Route> out, int[] way, long ox, long oy) {
        if (way.length < 2) return;
        int f = DistrictPlan.FRAME;
        List<double[]> pts = new ArrayList<>();
        for (int i = 0; i < way.length; i++) {
            if (i > 0 && i < way.length - 1) {
                // Dropped when it lies on the straight line between its neighbours.
                int a = way[i - 1], b = way[i], c = way[i + 1];
                if ((b % f) - (a % f) == (c % f) - (b % f) && (b / f) - (a / f) == (c / f) - (b / f)) continue;
            }
            pts.add(new double[] { ox + way[i] % f + 0.5, oy + way[i] / f + 0.5 });
        }
        out.add(truckRoute(false, pts, TRUCK_ACCESS_FADE));
    }

    /**
     * How many works the city has that need an access road (batch FR7, D17):
     * the railway's yards the runs draw, the shore's berthed works (sea
     * terminals) and the Truck Depots among its buildings - read from what the
     * map holds, no plan drawn - and at least 1. The painter gives each drawn
     * district's roads their part of the city's trucks as its roads over this
     * count. A work whose plan finds it no road still counts; the count is
     * the works the plans are told of, not the roads laid.
     */
    public int accessRoadCount() {
        if (accessRoadCountAt == changes) return accessRoadCountCache;
        long n = runs.yards().size();
        for (CityShore.Work w : shore.works()) if (w.berthed()) n++;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null || types[t].category() != BuildingType.HAULAGE) continue;
            for (District d : districts) n += Math.max(0, d.counts[t]);
        }
        accessRoadCountCache = (int) Math.max(1, Math.min(Integer.MAX_VALUE, n));
        accessRoadCountAt = changes;
        return accessRoadCountCache;
    }

    /** accessRoadCount()'s last answer, kept against the map's changes. */
    private int accessRoadCountCache;
    private long accessRoadCountAt = Long.MIN_VALUE;

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
            // A work on the shore and the plot round it, and a quay, are not the runs' ground (0.7.97): no run crosses a terminal.
            if (!shore.isEmpty() && shore.blocks(x, y)) return -1;
            long tk = (tx << 20) | ty;
            if (dir < 0 || (dir & 1) == 1) {
                byte[] t = tiles.get(tk);
                if (t == null) {
                    t = new byte[TilePainter.PLOTS];
                    World.of(worldKey).tileTerrain(tx, ty, t);
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
                    } else World.of(worldKey).lineTerrain(tx, ty, col, at, line);
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
            return World.mix(landVersion ^ ((long) land.purchases().size() << 32) ^ land.centreStamp() ^ shore.version() * 0x9E3779B97F4A7C15L);
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

    /** Whether the counts have any Freight Line, whose track makes the main line double. */
    private boolean hasFreightLine(long[] counts) {
        for (int t = 0; t < types.length && t < counts.length; t++) {
            if (counts[t] > 0 && BuildingVisual.freightLine(types[t])) return true;
        }
        return false;
    }

    /** Up to this many districts the map weighs its mined sites each month for the railway's sidings and further yards: 1,000 (Jerus's city has 93). A city of billions has 14,000 districts and 200,000 mined sites, and its month's change must take 5 ms (MapCheck): past it the railway has its main line and its yards by the nearest-mine rule alone, as before FR7 slice 3 (CityRuns THE RAILWAY'S WAYS). */
    public static final int WAYS_WORKS_DISTRICTS = 1000;

    /**
     * Lays the runs to the model's counts (CityRuns.layTo()): its Elevated
     * Highways' plots, its track's, its Rail Terminals and Rail Depots as
     * yards, each asked for its type's footprint (FR7 slice 3: Terminals first, then Depots,
     * so a depot added never moves a terminal's yard) and laid as long as a unit
     * train along its line where the ground allows (FX5 Y1, CityRuns.YARD_LONG), its Rail Spurs as
     * sidings into the bulk mined sites - the highways from the founding
     * site, the railway from the city's mine nearest it (the founding site
     * with none). True, and the map's changes bumped, when they moved.
     */
    boolean layRuns(long[] model) {
        long highway = 0, track = 0, spurTrackNow = 0;
        int spurs = 0;
        for (int t = 0; t < types.length && t < model.length; t++) {
            if (types[t] == null || model[t] <= 0) continue;
            if (types[t].road() == BuildingVisual.HIGHWAY) highway += model[t] * BuildingVisual.cells(types[t]);
            else if (types[t].track()) {
                track += model[t] * BuildingVisual.cells(types[t]);
                if (types[t].siding()) {
                    spurs += (int) Math.min(Integer.MAX_VALUE - spurs, model[t]);
                    spurTrackNow += model[t] * BuildingVisual.cells(types[t]);
                }
            }
        }
        spurCount = spurs;
        spurTrack = spurTrackNow;
        int[] seq = yardSequenceOf(model);
        List<int[]> yardSizes = new java.util.AbstractList<>() {
            @Override public int[] get(int i) { return footprintOf(seq[i]); }
            @Override public int size() { return seq.length; }
        };
        int yards = seq.length;
        // The mined sites, each month while the city is small enough to weigh them for the railway's sidings and further yards;
        // past WAYS_WORKS_DISTRICTS only as before FR7, when the railway starts or its yards change.
        boolean ways = districts.size() <= WAYS_WORKS_DISTRICTS;
        boolean needMines = (track > 0 || yards > 0) && (ways || !runs.rail().started || yards != runs.yards().size());
        List<long[]> mines, works = new ArrayList<>();
        if (needMines && ways) {
            refreshMines();
            mines = minesKept;
            works = worksKept;
        } else {
            mines = needMines ? minedSites() : new ArrayList<>();
        }
        long rx = siteX, ry = siteY;
        double best = Double.MAX_VALUE;
        for (long[] s : mines) {
            double cx = (s[0] + s[2] + 1) / 2.0, cy = (s[1] + s[3] + 1) / 2.0, d = (cx - siteX) * (cx - siteX) + (cy - siteY) * (cy - siteY);
            if (d < best) { best = d; rx = (long) Math.floor(cx); ry = (long) Math.floor(cy); }
        }
        if (runs.gateway() == CityRuns.GATEWAY_UNKNOWN && (highway > 0 || track > 0 || yards > 0)) {
            // Found from the founding site alone (a radius of none: the land must run GATEWAY_M from the site), so a map drawn
            // canonically at the city's size today finds the gateway the month-by-month map found at its first highway, track or yard.
            Gateways.Gateway g = Gateways.of(World.of(worldKey), siteX, siteY, 0);
            runs.setGateway(g == null ? CityRuns.NO_GATEWAY : g.bearing());
        }
        runs.setDoubleMain(hasFreightLine(model));
        boolean moved = runs.layTo(highway, track, yardSizes, spurs, runGround, siteX, siteY, rx, ry, mines, works);
        if (moved) {
            changes++;
            yardShare = null;
        }
        return moved;
    }

    /**
     * The type of each yard the model has, in the order the runs lay them
     * (FR7 slice 3): every Rail Terminal (by type index), then every Rail
     * Depot - each yard is asked for its type's footprint (laid YARD_LONG along its line where the ground
     * allows, FX5 Y1) and the painter draws it as that type. `counts` are the model's.
     */
    private int[] yardSequence(long[] counts) {
        long total = 0;
        for (int t = 0; t < types.length && t < counts.length; t++) if (types[t] != null && types[t].terminal()) total += Math.max(0, counts[t]);
        int[] seq = new int[(int) Math.min(Integer.MAX_VALUE - 8, total)];
        int at = 0;
        for (int pass = 0; pass < 2; pass++) {
            for (int t = 0; t < types.length && t < counts.length; t++) {
                if (types[t] == null || !types[t].terminal() || BuildingVisual.depot(types[t]) != (pass == 1)) continue;
                for (long k = 0; k < counts[t] && at < seq.length; k++) seq[at++] = t;
            }
        }
        return seq;
    }

    /** yardSequence() of a set of counts, the last kept against the terminal types' counts in it (a month and a tile ask for it again and again; a city of billions has thousands of yards). */
    private int[] yardSeqCache;
    private long yardSeqKey = Long.MIN_VALUE;

    private int[] yardSequenceOf(long[] counts) {
        long key = 0x59415244L;
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].terminal()) key = World.mix(key ^ (t < counts.length ? counts[t] : 0) ^ ((long) t << 40));
        if (yardSeqCache == null || key != yardSeqKey) {
            yardSeqCache = yardSequence(counts);
            yardSeqKey = key;
        }
        return yardSeqCache;
    }

    private int[] haveSequence() {
        return yardSequenceOf(have);
    }

    /** A type's footprint, {across, down} in whole plots (BuildingVisual.footprint()), kept: the size a yard is asked for, every yard every month (FX5 Y1: laid so long along its line as the ground allows, CityRuns.YARD_LONG, it keeps this size as its ask). */
    private int[] footprintOf(int t) {
        if (footprints == null || footprints.length != types.length) footprints = new int[types.length][];
        if (footprints[t] == null) footprints[t] = BuildingVisual.footprint(types[t]);
        return footprints[t];
    }

    private int[][] footprints;

    /** The type index of the runs' yard i: the model's sequence (yardSequence() of the map's counts), the first terminal type past it; -1 with no terminal type. */
    int yardTypeOf(int i) {
        int[] seq = haveSequence();
        if (i >= 0 && i < seq.length) return seq[i];
        return seq.length > 0 ? seq[0] : firstTerminal();
    }

    /** The first Rail Terminal type's index, or -1. */
    private int firstTerminal() {
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].terminal()) return t;
        return -1;
    }

    /** The city's mines and wells standing on their sites, each {x0, y0, x1, y1} inclusive: where the railway starts and its yards are drawn near. */
    List<long[]> minedSites() {
        List<long[]> all = new ArrayList<>();
        scanMinedSites(all, null);
        return all;
    }

    /** ...and (FR7 slice 3) of those, the BULK works' (BuildingVisual.Type.bulk(): a mine, a quarry - not a well): the works a siding is laid into and a further yard stands beside. */
    List<long[]> bulkSites() {
        List<long[]> bulk = new ArrayList<>();
        scanMinedSites(null, bulk);
        return bulk;
    }

    /** The mined sites into `all` and the bulk works' among them into `bulk` (either null to leave out): one pass over the districts' site lists. */
    private void scanMinedSites(List<long[]> all, List<long[]> bulk) {
        sitedNow();
        for (District d : districts) {
            boolean any = false;
            for (int[] rs : d.siteRuns) any |= rs != null;
            if (!any) continue;
            for (DrawnSite s : siteList(d.dx, d.dy)) {
                if (s.mine() < 0) continue;
                long[] box = { s.x0(), s.y0(), s.x1(), s.y1() };
                if (all != null) all.add(box);
                if (bulk != null && types[s.mine()] != null && types[s.mine()].bulk()) bulk.add(box);
            }
        }
    }

    /**
     * layRuns()'s mined sites and bulk works, kept against a digest of the
     * districts' mines (their site runs): a city of billions has some two
     * hundred thousand sites, and walking every district's site list costs
     * seconds the month's runs must not pay for the same sites twice.
     */
    private List<long[]> minesKept, worksKept;
    private long minesKey = Long.MIN_VALUE;

    private void refreshMines() {
        sitedNow();
        long key = World.mix(0x4D494E45L ^ districts.size());
        for (District d : districts) {
            for (int[] rs : d.siteRuns) if (rs != null) key = World.mix(key ^ Arrays.hashCode(rs) ^ d.key * 31);
        }
        if (minesKept != null && key == minesKey) return;
        minesKept = new ArrayList<>();
        worksKept = new ArrayList<>();
        scanMinedSites(minesKept, worksKept);
        minesKey = key;
    }

    /** Each district's Rail Terminals and Depots the runs draw as yards, by its index and then the terminal type's place among the terminal types (terminalTypes()): the yards of each type dealt to the districts holding that type in the map's order, so the rest are their plans' (kept against the runs' version). */
    private int[][] yardShare;
    private long yardShareAt = Long.MIN_VALUE;

    /** The terminal types' indices (BuildingVisual.Type.terminal()), in type order; worked out once (the types are the map's for life). */
    private int[] terminalTypes() {
        if (terminalTypesKept != null) return terminalTypesKept;
        int n = 0;
        for (BuildingVisual.Type t : types) if (t != null && t.terminal()) n++;
        int[] out = new int[n];
        n = 0;
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].terminal()) out[n++] = t;
        terminalTypesKept = out;
        return out;
    }

    private int[] terminalTypesKept;

    /** How many of district d's buildings of terminal type t are the runs' yards. */
    int yardsOf(District d, int t) {
        long at = World.mix(runs.version() ^ changes);
        int[] tt = terminalTypes();
        if (yardShare == null || yardShareAt != at || yardShare.length != districts.size()) {
            yardShare = new int[districts.size()][tt.length];
            yardShareAt = at;
            // The runs' yards of each type, from the sequence: the first of them laid (those the ground had no place for are the last).
            int[] seq = haveSequence();
            int laid = runs.yards().size();
            int[] left = new int[tt.length];
            for (int i = 0; i < seq.length && i < laid; i++) for (int k = 0; k < tt.length; k++) if (tt[k] == seq[i]) left[k]++;
            for (int k = 0; k < tt.length; k++) {
                for (int i = 0; i < districts.size() && left[k] > 0; i++) {
                    District o = districts.get(i);
                    int n = tt[k] < o.counts.length ? o.counts[tt[k]] : 0;
                    int take = Math.min(n, left[k]);
                    yardShare[i][k] = take;
                    left[k] -= take;
                }
            }
        }
        if (d.index >= yardShare.length) return 0;
        for (int k = 0; k < tt.length; k++) if (tt[k] == t) return yardShare[d.index][k];
        return 0;
    }

    /** How many of district d's Rail Terminals and Depots are the runs' yards. */
    int yardsOf(District d) {
        int n = 0;
        for (int t : terminalTypes()) n += yardsOf(d, t);
        return n;
    }

    /* ----- the Rail Spurs the runs' sidings left: dealt to the plans' works (batch FR7 slice 5, D14) ----- */

    /** The Rail Spurs the model has and the track plots they are, as layRuns() last saw them: the runs lay a siding into a mined site for each spur they can; the rest are dealt (sidingsOf()). */
    private int spurCount;
    private long spurTrack;

    /** Each district's share of the spurs, by its index, how many are dealt in all, and what they were kept against (the runs' version, the map's changes and the spurs). */
    private int[] sidingShare;
    private long sidingsDealt;
    private long sidingShareAt = Long.MIN_VALUE;

    /** The Rail Spurs the runs' sidings did not take: the model's less the sidings the runs laid into mined sites. */
    int spursLeft() {
        return Math.max(0, spurCount - runs.sidingsLaid());
    }

    /** How many Rail Spurs are dealt to the districts' plans in all (sidingsOf()). */
    long sidingsDealt() {
        sidingsOf(null);
        return sidingsDealt;
    }

    /**
     * How many sidings district d's plan lays (batch FR7 slice 5, D14): its
     * share of the Rail Spurs the runs' sidings left (spursLeft()). They are
     * dealt to the districts whose frames the main line crosses - a plan reads
     * its own frame alone, so a district the line only comes within a district
     * of could not draw one (CityRuns.mainLineCells()) - that hold bulk works
     * their plans place (BuildingVisual.Type.bulk(), a mine standing on its
     * site apart), the type of the largest footprint first (cells()), each in
     * the districts' order, as yardsOf() deals the yards: a district added
     * after never reorders the rest. A pure function of the counts and the runs,
     * kept against the runs' version and the map's changes; nothing is saved.
     * Past WAYS_WORKS_DISTRICTS districts none, as the runs lay none.
     */
    int sidingsOf(District d) {
        long at = World.mix(runs.version() ^ changes ^ ((long) spurCount << 20) ^ 0x53494449L);
        if (sidingShare == null || sidingShareAt != at || sidingShare.length != districts.size()) {
            sidingShare = new int[districts.size()];
            sidingsDealt = 0;
            sidingShareAt = at;
            dealSidings(sidingShare);
        }
        return d == null || d.index >= sidingShare.length ? 0 : sidingShare[d.index];
    }

    private void dealSidings(int[] share) {
        if (districts.size() > WAYS_WORKS_DISTRICTS || !runs.rail().started) return;
        int left = spursLeft();
        if (left == 0) return;
        java.util.Set<Long> line = runs.mainLineCells(DISTRICT);
        if (line.isEmpty()) return;
        List<Integer> bulk = new ArrayList<>();
        for (int t = 0; t < types.length; t++) {
            BuildingVisual.Type ty = types[t];
            if (ty != null && ty.bulk() && ty.drawn() && !ty.terminal() && !ty.shore()) bulk.add(t);
        }
        bulk.sort((a, b) -> BuildingVisual.cells(types[a]) != BuildingVisual.cells(types[b])
                ? Integer.compare(BuildingVisual.cells(types[b]), BuildingVisual.cells(types[a])) : Integer.compare(a, b));
        for (int t : bulk) {
            for (District d : districts) {
                if (left == 0) return;
                if (!line.contains(((long) (baseDX + d.dx) << 32) ^ ((baseDY + d.dy) & 0xffffffffL))) continue;
                int n = t < d.counts.length ? d.counts[t] - (d.sited == null ? 0 : d.sited[t]) : 0;
                if (n <= 0) continue;
                int take = Math.min(n, left);
                share[d.index] += take;
                sidingsDealt += take;
                left -= take;
            }
        }
    }

    /** The runs' frame for district d's plan: their plots over its 257 x 257 (DistrictPlan's codes), a hash of them returned. */
    long runsInto(District d, byte[] fixed) {
        return runsInto(d, fixed, null);
    }

    /** ...and (batch FR7) the frame's highway plots that carry a ramp into `ramp` (null to leave them): where an access road may end. */
    long runsInto(District d, byte[] fixed, boolean[] ramp) {
        long x0 = (baseDX + d.dx) * DISTRICT, y0 = (baseDY + d.dy) * DISTRICT;
        int f = DistrictPlan.FRAME;
        byte[] marks = ramp == null ? null : new byte[f * f];
        long h = runs.fill(x0, y0, f, f, fixed, marks, true);
        for (int i = 0; i < f * f; i++) {
            byte b = fixed[i];
            if (marks != null && (marks[i] & CityRuns.M_RAMP) != 0 && (b == CityRuns.F_HIGHWAY || b == CityRuns.F_RAIL_OVER)) ramp[i] = true;
            if (b == CityRuns.F_RAIL_OVER) fixed[i] = DistrictPlan.FIXED_HIGHWAY;
            else if (b == CityRuns.F_YARD) fixed[i] = DistrictPlan.FIXED_YARD;
            else if (b == CityRuns.F_RESERVE) fixed[i] = DistrictPlan.FIXED_RESERVE;
        }
        return h;
    }

    /**
     * The works of the city that need an access road and stand in district d's
     * frame (batch FR7, D17): each Rail Terminal and Rail Depot the runs draw as a yard, each
     * sea terminal on the shore (a tank farm has no berth and none), as {x, y,
     * w, h} in the frame's plots, off the edge row and column the districts
     * share. A sea terminal stands inside one cell's interior. (FX5 Y1) A yard
     * is laid up to YARD_LONG plots along its line and may run across a district's
     * edge: it is the work of the one district that holds more of its plots
     * (workIn()), cut at its frame, so it is given one access road, not two.
     */
    void worksInto(District d, DistrictPlan.Input in) {
        long x0 = (baseDX + d.dx) * DISTRICT, y0 = (baseDY + d.dy) * DISTRICT;
        for (long[] yd : runs.yards()) {
            int[] w = workIn(yd, x0, y0);
            if (w != null) in.works.add(w);
        }
        for (CityShore.Work wk : shore.works()) if (wk.berthed()) addWork(in, wk.x0() - x0, wk.y0() - y0, wk.w(), wk.h());
    }

    /**
     * A yard's box as the district frame whose first plot is (x0, y0) holds it, {x, y, w, h} in the frame's plots, or null when another
     * frame holds more of it. A frame's own plots are its first plot + 1 to + DISTRICT - 1 (the first and the last are the edge row and
     * column two frames share); the box is cut at them, so a yard across an edge is one frame's work, whole to its plan (the other
     * frame has it only as FIXED_YARD ground, from CityRuns.fill()). A yard inside one frame is that frame's, as it was.
     */
    static int[] workIn(long[] yard, long x0, long y0) {
        if (!holdsMost(yard[0], yard[2], x0) || !holdsMost(yard[1], yard[3], y0)) return null;
        long fx = Math.max(yard[0], x0 + 1), fy = Math.max(yard[1], y0 + 1);
        long lx = Math.min(yard[0] + yard[2], x0 + DISTRICT), ly = Math.min(yard[1] + yard[3], y0 + DISTRICT);
        return new int[] { (int) (fx - x0), (int) (fy - y0), (int) (lx - fx), (int) (ly - fy) };
    }

    /** Whether the frame whose first plot is f holds more of the n plots from p than any other frame does; a tie to the first. */
    private static boolean holdsMost(long p, long n, long f) {
        long best = 0, bestFrame = Long.MIN_VALUE;
        for (long k = Math.floorDiv(p, DISTRICT); k <= Math.floorDiv(p + n - 1, DISTRICT); k++) {
            long held = Math.min(p + n, k * DISTRICT + DISTRICT) - Math.max(p, k * DISTRICT + 1);
            if (held > best) { best = held; bestFrame = k * DISTRICT; }
        }
        return best > 0 && f == bestFrame;
    }

    private static void addWork(DistrictPlan.Input in, long x, long y, long w, long h) {
        if (x >= 1 && y >= 1 && x + w <= DISTRICT && y + h <= DISTRICT) in.works.add(new int[] { (int) x, (int) y, (int) w, (int) h });
    }

    /* =====================================================================
       THE SHORE: TERMINALS AND TANK FARMS AT THE WATER (0.7.97, batch O13;
       spec-roads-and-ports.md 2.8, docs/studies/spec-oil.md 2.12)

       The city's sea terminals and tank farms (BuildingVisual.Type.shore())
       are laid city-wide on its shore, as its railway's yards are on its
       track: CityShore holds them and says the rule; this finds each one's
       place (shoreSpot()), lays them to the month's counts (layShore()) and
       writes them in the sidecar (FORMAT 6). Each district's plan is drawn
       round them - their boxes fixed as a yard's ground, the runs kept off
       them (RunGround) - and the ones its own counts stand for are taken out
       of its plan (shoreOf()), as its yards are. What no shore holds is its
       district's plan's to draw, counted (shoreShort()), and the search
       waits for the ground to change.
       ===================================================================== */

    /** The city's works on its shore. */
    private CityShore shore = new CityShore();

    /** The city's works on its shore (0.7.97). */
    public CityShore shore() { return shore; }

    /** The shore's types, by id: worked out once a table. */
    private int[] shoreTypes;

    int[] shoreTypes() {
        if (shoreTypes == null) {
            int n = 0;
            for (BuildingVisual.Type ty : types) if (ty != null && ty.shore() && ty.drawn()) n++;
            int[] out = new int[n];
            n = 0;
            for (int u = 0; u < types.length; u++) if (types[u] != null && types[u].shore() && types[u].drawn()) out[n++] = u;
            shoreTypes = out;
        }
        return shoreTypes;
    }

    /** The ground's key a search came up short at: none is made again until the ground moves (a purchase) or a work is taken. */
    private long shoreShortAt = Long.MIN_VALUE;

    /** How many works the model has that the shore holds no place for (drawn by their districts' plans). */
    private int shoreShort;

    /** How many of the model's terminals and tank farms the shore has no place for: their districts' plans draw them. */
    public int shoreShort() { return shoreShort; }

    /** The ground's key, without the shore's own works: what a search that came up short waits on. */
    private long groundKey() {
        return World.mix(landVersion ^ ((long) land.purchases().size() << 32) ^ land.centreStamp());
    }

    /**
     * Lays the shore to the model's counts: one fewer of a type takes its
     * newest work; one more is laid where shoreSpot() finds it, the first
     * types first. True, and the map's changes bumped, when a work moved.
     */
    boolean layShore(long[] model) {
        int[] st = shoreTypes();
        if (st.length == 0 || (shore.isEmpty() && !anyOf(model, st))) {
            shoreShort = 0;
            return false;
        }
        boolean moved = false;
        for (int t : st) {
            long want = t < model.length ? Math.max(0, model[t]) : 0;
            while (shore.count(t) > want && shore.removeNewest(t)) {
                moved = true;
                shoreShortAt = Long.MIN_VALUE;
            }
        }
        int missing = 0;
        List<long[]> mines = null;
        for (int t : st) {
            long want = t < model.length ? Math.max(0, model[t]) : 0;
            int have = shore.count(t);
            if (have >= want) continue;
            if (shoreShortAt == groundKey()) { missing += (int) (want - have); continue; }
            if (mines == null) mines = minedSites();
            while (have < want) {
                CityShore.Work w = shoreSpot(t, mines);
                if (w == null) {
                    missing += (int) (want - have);
                    shoreShortAt = groundKey();
                    break;
                }
                shore.add(w);
                have++;
                moved = true;
            }
        }
        shoreShort = missing;
        if (moved) {
            changes++;
            shoreShare = null;
        }
        return moved;
    }

    private static boolean anyOf(long[] model, int[] ids) {
        for (int t : ids) if (t < model.length && model[t] > 0) return true;
        return false;
    }

    /**
     * Where the next work of type t goes (CityShore's rule), or null: the
     * districts in the map's order, each one's tiles nearest the founding
     * site first; on each tile with owned salt water and owned dry ground,
     * its shore plots in rows, each side the sea lies on, the box either way
     * round with the plot at the middle of its sea side. A spot within REACH
     * of its cell's ring, off the sea side, is taken at once; else the
     * district's first spot, once its tiles are looked at.
     */
    CityShore.Work shoreSpot(int t, List<long[]> mines) {
        int[] fp = BuildingVisual.footprint(types[t]);
        int quay = types[t].berth() >= 0 ? CityShore.QUAY_PLOTS[types[t].berth()] : 0;
        int m = World.TILE, ww = World.TILE + 2 * m, tile = World.TILE;
        byte[] fixed = new byte[ww * ww];
        int[] ground = new int[ww * ww];
        int scanned = 0;
        Integer[] order = new Integer[TILES];
        // A terminal's water must open to the sea (SeaRoutes.opensToSea(), star O13-5): the routes' own grid, and the water found
        // shut, kept for the search.
        SeaRoutes.Grid sea = quay > 0 ? new SeaRoutes.Grid(World.of(worldKey)) : null;
        java.util.Set<Long> shut = new java.util.HashSet<>();
        for (District d : districts) {
            long tx0 = (baseDX + d.dx) * TILES_A_SIDE, ty0 = (baseDY + d.dy) * TILES_A_SIDE;
            for (int k = 0; k < TILES; k++) order[k] = k;
            final long fx0 = tx0, fy0 = ty0;
            Arrays.sort(order, Comparator.comparingDouble((Integer k) -> {
                double cx = ((fx0 + k % TILES_A_SIDE) + 0.5) * tile - siteX, cy = ((fy0 + k / TILES_A_SIDE) + 0.5) * tile - siteY;
                return cx * cx + cy * cy;
            }).thenComparingInt(k -> k));
            CityShore.Work far = null;
            for (int k : order) {
                long tx = tx0 + k % TILES_A_SIDE, ty = ty0 + k / TILES_A_SIDE;
                if (cover(land.grid().cover(5, tx, ty)) == NONE) continue;
                if (++scanned > CityShore.SEARCH_TILES_MOST) return far;
                boolean wet = false, dry = false;
                for (int i = 0; i < World.TILE * World.TILE && !(wet && dry); i++) {
                    int g = runGround.at(tx * tile + i % tile, ty * tile + i / tile, -1);
                    if (g == World.SALT) wet = true;
                    else if (CityRuns.dry(g)) dry = true;
                }
                if (!wet || !dry) continue;
                long wx0 = tx * tile - m, wy0 = ty * tile - m;
                runs.fill(wx0, wy0, ww, ww, fixed, null, true);
                for (int j = 0; j < ww * ww; j++) ground[j] = runGround.at(wx0 + j % ww, wy0 + j / ww, -1);
                for (long[] s : mines) {
                    for (long y = Math.max(s[1], wy0); y <= Math.min(s[3], wy0 + ww - 1); y++) {
                        for (long x = Math.max(s[0], wx0); x <= Math.min(s[2], wx0 + ww - 1); x++) ground[(int) ((y - wy0) * ww + (x - wx0))] = -2;
                    }
                }
                for (int iy = 0; iy < tile; iy++) {
                    for (int ix = 0; ix < tile; ix++) {
                        int j = (iy + m) * ww + ix + m;
                        if (!CityRuns.dry(ground[j])) continue;
                        for (int side = 0; side < 4; side++) {
                            if (ground[j + CityShore.DX[side] + CityShore.DY[side] * ww] != World.SALT) continue;
                            for (int turn = 0; turn < (fp[0] == fp[1] ? 1 : 2); turn++) {
                                int w = turn == 0 ? fp[0] : fp[1], h = turn == 0 ? fp[1] : fp[0];
                                long sx = tx * tile + ix, sy = ty * tile + iy;
                                long bx0 = side == 1 ? sx - (w - 1) : side == 3 ? sx : sx - w / 2;
                                long by0 = side == 2 ? sy - (h - 1) : side == 0 ? sy : sy - h / 2;
                                CityShore.Work c = new CityShore.Work(bx0, by0, w, h, t, side, quay);
                                if (!shoreFits(c, ground, fixed, wx0, wy0, ww)) continue;
                                if (sea != null) {
                                    long[] e = c.quayPlot(quay + 1);
                                    if (!SeaRoutes.opensToSea(sea, e[0], e[1], shut)) continue;
                                }
                                if (nearRing(c)) return c;
                                if (far == null) far = c;
                            }
                        }
                    }
                }
            }
            if (far != null) return far;
        }
        return null;
    }

    /** Whether a work fits where c puts it, on the window's ground and runs from (wx0, wy0), ww a side: its box in one cell's interior on owned dry ground no run, mine or other work takes, no highway beside it, and its quay all owned salt water. */
    private static boolean shoreFits(CityShore.Work c, int[] ground, byte[] fixed, long wx0, long wy0, int ww) {
        long cx = Math.floorDiv(c.x0(), World.TILE) * World.TILE, cy = Math.floorDiv(c.y0(), World.TILE) * World.TILE;
        if (c.x0() - cx < 1 || c.y0() - cy < 1 || c.x0() + c.w() > cx + World.TILE || c.y0() + c.h() > cy + World.TILE) return false;
        for (long y = c.y0() - CityShore.CLEAR; y < c.y0() + c.h() + CityShore.CLEAR; y++) {
            for (long x = c.x0() - CityShore.CLEAR; x < c.x0() + c.w() + CityShore.CLEAR; x++) {
                if (x < wx0 || y < wy0 || x >= wx0 + ww || y >= wy0 + ww) return false;
                int j = (int) ((y - wy0) * ww + (x - wx0));
                byte f = fixed[j];
                if (f == CityRuns.F_HIGHWAY || f == CityRuns.F_RAIL_OVER) return false;
                if (!c.holds(x, y)) continue;
                if (f != 0 || !CityRuns.dry(ground[j])) return false;
            }
        }
        for (int q = 1; q <= c.quay(); q++) {
            long[] at = c.quayPlot(q);
            if (at[0] < wx0 || at[1] < wy0 || at[0] >= wx0 + ww || at[1] >= wy0 + ww) return false;
            if (ground[(int) ((at[1] - wy0) * ww + (at[0] - wx0))] != World.SALT) return false;
        }
        return true;
    }

    /** Whether a work's box lies within TilePainter.REACH of its cell's ring on a side away from the sea: where an arterial runs when the cell is open. */
    static boolean nearRing(CityShore.Work c) {
        long cx = Math.floorDiv(c.x0(), World.TILE) * World.TILE, cy = Math.floorDiv(c.y0(), World.TILE) * World.TILE;
        int r = TilePainter.REACH;
        if (c.side() != 0 && c.y0() - cy <= r) return true;
        if (c.side() != 1 && cx + World.TILE - (c.x0() + c.w() - 1) <= r) return true;
        if (c.side() != 2 && cy + World.TILE - (c.y0() + c.h() - 1) <= r) return true;
        return c.side() != 3 && c.x0() - cx <= r;
    }

    /** Each district's works the shore holds, by its index and the shore's type: the works dealt to the districts holding their type in the map's order, so the rest are their plans' (kept against the shore and the counts). */
    private int[][] shoreShare;
    private long shoreShareAt = Long.MIN_VALUE;

    /** How many of district d's buildings of type t the shore holds. */
    int shoreOf(District d, int t) {
        int[] st = shoreTypes();
        long at = World.mix(shore.version() ^ changes * 0x9E3779B97F4A7C15L);
        if (shoreShare == null || shoreShareAt != at || shoreShare.length != districts.size()) {
            shoreShare = new int[districts.size()][st.length];
            shoreShareAt = at;
            for (int s = 0; s < st.length; s++) {
                int left = shore.count(st[s]);
                for (int i = 0; i < districts.size() && left > 0; i++) {
                    int take = Math.min(districts.get(i).counts[st[s]], left);
                    shoreShare[i][s] = take;
                    left -= take;
                }
            }
        }
        for (int s = 0; s < st.length; s++) if (st[s] == t) return d.index < shoreShare.length ? shoreShare[d.index][s] : 0;
        return 0;
    }

    /* =====================================================================
       THE OIL AT SEA (0.7.97, batch O13; docs/studies/spec-oil.md 2.12, the
       research's 3.3)

       The model's platforms and pipelines are on none of the city's dry
       ground (BuildingVisual.Type.sea()), so no district holds them: the game
       hands the map where they stand (Game's mapAtSea(), from sectors.Oil's
       records) and each tile draws what crosses it - a jacket on its field,
       its wells on their sea sites, its 500 m ring, a pipe from its field
       toward the founding site as far as its kilometres standing reach.
       ===================================================================== */

    /** A platform as the map draws it: its jacket's middle in plots, its wells, and the plots its wells stand on (each x << 32 | y). */
    public record Jacket(double x, double y, int wells, List<Long> wellPlots) { }

    /** A pipe as the map draws it: from its field's middle (x0, y0) toward the founding site, to (x1, y1), in plots - its kilometres standing. */
    public record Pipe(double x0, double y0, double x1, double y1) { }

    /** The railway as the map draws its yard: whether it carries goods, and the share of the track's wagon sets the fleet has standing. */
    public record RailPicture(boolean goodsByRail, double wagonShare) { }

    private RailPicture railPicture = new RailPicture(false, 0);

    /** The railway's picture, as the game last handed it. */
    public RailPicture railPicture() { return railPicture; }

    /** The whole wagon tracks a share of the fleet gives: its share of YARD_TRACKS, rounded to a whole track. */
    static int wagonTracksOf(double share) {
        return (int) Math.round(clamp(share, 0, 1) * TileRaster.YARD_TRACKS);
    }

    /** The railway's picture handed over by the game (Game.mapRail()); a change of goods or of the whole wagon tracks restamps the tiles. */
    public void railPicture(RailPicture r) {
        RailPicture now = r == null ? new RailPicture(false, 0) : r;
        if (now.equals(railPicture)) return;
        boolean goods = railPicture.goodsByRail();
        int tracks = wagonTracksOf(railPicture.wagonShare());
        railPicture = now;
        if (now.goodsByRail() != goods || wagonTracksOf(now.wagonShare()) != tracks) changes++;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    /** The freight station's yard, {x0, y0, w, h} in world plots: the yard nearest the rail main arm's end (spec 4.2; CityRuns.freightStation()); null for none. */
    public long[] freightStation() {
        worldLines();   // the main arm the station is nearest to is found by the lines (FX2); cached, so it costs nothing twice
        return runs.freightStation();
    }

    /** The oil at sea: its jackets and pipes. */
    public record AtSea(List<Jacket> jackets, List<Pipe> pipes) {
        /** None. */
        public static final AtSea NONE = new AtSea(List.of(), List.of());
    }

    private AtSea atSea = AtSea.NONE;

    /** The oil at sea, as the game last handed it. */
    public AtSea atSea() { return atSea; }

    /** The oil at sea handed over by the game (Game.mapAtSea()); a change restamps the tiles it crosses. */
    public void atSea(AtSea a) {
        AtSea now = a == null ? AtSea.NONE : a;
        if (now.equals(atSea)) return;
        atSea = now;
        changes++;
    }

    /** The oil at sea over tile (px0, py0)'s plots, into its inputs: the jackets' and wells' plots, the rings and pipes that may cross it. */
    private void atSeaInto(long px0, long py0, TilePainter.Input in) {
        if (atSea.jackets().isEmpty() && atSea.pipes().isEmpty()) return;
        int tile = World.TILE;
        double r = TilePainter.RING_PLOTS + 1;
        for (Jacket j : atSea.jackets()) {
            long jx0 = Math.round(j.x()) - TilePainter.JACKET_PLOTS / 2, jy0 = Math.round(j.y()) - TilePainter.JACKET_PLOTS / 2;
            for (long y = jy0; y < jy0 + TilePainter.JACKET_PLOTS; y++) {
                for (long x = jx0; x < jx0 + TilePainter.JACKET_PLOTS; x++) {
                    if (x >= px0 && y >= py0 && x < px0 + tile && y < py0 + tile) in.sea[(int) ((y - py0) * tile + (x - px0))] = TilePainter.JACKET;
                }
            }
            for (long w : j.wellPlots()) {
                long x = w >> 32, y = (int) w;
                if (x >= px0 && y >= py0 && x < px0 + tile && y < py0 + tile && in.sea[(int) ((y - py0) * tile + (x - px0))] == 0) {
                    in.sea[(int) ((y - py0) * tile + (x - px0))] = TilePainter.WELL;
                }
            }
            if (j.x() + r >= px0 && j.y() + r >= py0 && j.x() - r < px0 + tile && j.y() - r < py0 + tile) in.addRing(j.x() - px0, j.y() - py0);
        }
        for (Pipe p : atSea.pipes()) {
            if (Math.max(p.x0(), p.x1()) < px0 || Math.max(p.y0(), p.y1()) < py0 || Math.min(p.x0(), p.x1()) >= px0 + tile
                    || Math.min(p.y0(), p.y1()) >= py0 + tile) continue;
            in.addPipe(p.x0() - px0, p.y0() - py0, p.x1() - px0, p.y1() - py0);
        }
    }

    /* =====================================================================
       THE SEA ROUTES (0.7.97, batch O13; spec-roads-and-ports.md 4.1)

       Each terminal's route out to the world, found on the sea's grid and
       pulled straight (SeaRoutes) from its quay's end: kept, not saved, and
       found again when the shore's works or the city's ground move - away
       from the screen's thread (RoutesJob), as a district's plan is.
       ===================================================================== */

    /** The routes found, and the key of what they were found from. */
    private List<BoatSchedule.Route> routes;
    private long routesKey = Long.MIN_VALUE;

    /** The city's radius for the offing, in plots: the farthest corner of its owned box from the founding site. */
    double cityRadius() {
        double[] b = ownedBox();
        double most = 0;
        for (double x : new double[] { b[0], b[2] + 1 }) for (double y : new double[] { b[1], b[3] + 1 }) most = Math.max(most, Math.hypot(x, y));
        return most;
    }

    /** The key the routes are found against: the berths, the founding site and the city's radius. */
    private long routesKeyNow() {
        long h = World.mix(seed ^ siteX * 31 ^ siteY);
        for (BoatSchedule.Berth b : shore.berths(types)) h = World.mix(h ^ b.x() * 0x9E3779B97F4A7C15L ^ b.y() ^ ((long) b.cargo().ordinal() << 56));
        return World.mix(h ^ Double.doubleToLongBits(cityRadius()));
    }

    /** The routes now, found here when they are not (a harness's way; the view hands a RoutesJob to its worker). */
    public List<BoatSchedule.Route> seaRoutes() {
        long k = routesKeyNow();
        if (routes == null || routesKey != k) {
            routes = SeaRoutes.of(World.of(worldKey), shore.berths(types), siteX, siteY, cityRadius());
            routesKey = k;
        }
        return routes;
    }

    /** The routes when they are found and current, else null: the screen's thread, which never finds them itself. */
    public List<BoatSchedule.Route> seaRoutesIfFound() {
        return routes != null && routesKey == routesKeyNow() ? routes : null;
    }

    /** The berths' routes found away from the screen's thread: what they are found from, copied here. */
    public static final class RoutesJob implements Runnable {
        final long key, seed, siteX, siteY;
        /** The world the routes are found on (0.7.115). */
        final World.Key world;
        final List<BoatSchedule.Berth> berths;
        final double radius;
        private volatile List<BoatSchedule.Route> found;

        RoutesJob(long key, World.Key world, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius) {
            this.key = key;
            this.seed = world.seed();
            this.world = world;
            this.berths = List.copyOf(berths);
            this.siteX = siteX;
            this.siteY = siteY;
            this.radius = radius;
        }

        @Override public void run() {
            if (found == null) found = SeaRoutes.of(World.of(world), berths, siteX, siteY, radius);
        }

        /** Whether it has run. */
        public boolean done() { return found != null; }
    }

    /** The job that finds the routes now, or null when they are current. */
    public RoutesJob routesJob() {
        long k = routesKeyNow();
        if (routes != null && routesKey == k) return null;
        return new RoutesJob(k, worldKey, shore.berths(types), siteX, siteY, cityRadius());
    }

    /** A job run: its routes kept when they are still the city's. True when kept. */
    public boolean adoptRoutes(RoutesJob j) {
        if (j == null || !j.done() || j.key != routesKeyNow()) return false;
        routes = j.found;
        routesKey = j.key;
        return true;
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
        World.of(worldKey).tileTerrain(tx, ty, in.terrain);
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
        java.util.Arrays.fill(in.siding, (byte) 0);
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
        // ...and the ring about it (FX5): the street on every plot within REACH of the tile, corners too, so a crossing on the tile's corner
        // is read whole. The tile's own plots are `street` and the plots just across its edges `beyond`; streetAt() gives the rest.
        int reach = TilePainter.REACH, tile = World.TILE;
        for (int y = -reach; y < tile + reach; y++) {
            boolean rowIn = y >= 0 && y < tile;
            for (int x = -reach; x < tile + reach; x++) {
                byte code;
                if (rowIn && x >= 0 && x < tile) code = in.street[y * tile + x];
                else if (y == -1 && x >= 0 && x < tile) code = in.beyond[x];
                else if (y == tile && x >= 0 && x < tile) code = in.beyond[2 * tile + x];
                else if (x == -1 && rowIn) code = in.beyond[3 * tile + y];
                else if (x == tile && rowIn) code = in.beyond[tile + y];
                else code = streetAt(px0 + x, py0 + y, seen);
                in.ring[TilePainter.ringIndex(x, y)] = code;
            }
        }
        // The city's highways and track through it and the ring about it (0.7.89: the city's runs; THE NETWORK's plan's until
        // 0.7.88), their ramps and 45-degree stretches; its yards drawn over their track.
        int rw = World.TILE + 2;
        byte[] ringFixed = new byte[rw * rw], ringMarks = new byte[rw * rw];
        {
            // The main line's marks run from where the railway meets the world, so its arm is found first (FX2); cached.
            worldLines();
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
        // The sidings its district's plan laid (batch FR7 slice 5, D14): on the tile, and as the runs' track is, just beyond its edges. A
        // siding keeps SIDING_EDGE plots from its frame's edge, so the tile's own district has every one the tile and its ring can see.
        if (own != null && own.siding != null) {
            int t = World.TILE, f = DistrictPlan.FRAME;
            int lx0 = (int) Math.floorMod(tx, TILES_A_SIDE) * t, ly0 = (int) Math.floorMod(ty, TILES_A_SIDE) * t;
            for (int e : own.siding) {
                int q = e & (SIDING_END_BIT - 1), x = q % f - lx0, y = q / f - ly0;
                boolean end = (e & SIDING_END_BIT) != 0;
                if (x >= 0 && y >= 0 && x < t && y < t) {
                    in.siding[y * t + x] = end ? TilePainter.SIDING_END : TilePainter.SIDING_PLOT;
                    if (end) in.marks[y * t + x] |= (byte) TilePainter.M_SHED;
                    continue;
                }
                int at = y == -1 && x >= 0 && x < t ? x : x == t && y >= 0 && y < t ? t + y : y == t && x >= 0 && x < t ? 2 * t + x : x == -1 && y >= 0 && y < t ? 3 * t + y : -1;
                if (at >= 0 && in.fixedBeyond[at] == 0) in.fixedBeyond[at] = TilePainter.RAIL;
            }
        }
        // The railway's bends that reach it (FX5), in the tile's plots, and the plots they replace: found with the trains' routes, which round the
        // same turns, so a car is on the rail it is drawn on.
        in.clearBends();
        trainRoutes();
        List<DrawnBend> bendsHere = trainBends.get(bendKey(tx, ty));
        if (bendsHere != null) {
            for (DrawnBend db : bendsHere) {
                TrackCurves.Bend b = db.bend();
                in.addBend(b.net(), b.x() - px0, b.y() - py0, b.ux(), b.uy(), b.vx(), b.vy(), b.r(), b.t(), db.doubled());
            }
            in.markCurved();
        }
        // The interchanges' reserves that reach it (batch FR7): the ground their loops are drawn on, once the highways are drawn at all.
        in.clearReserves();
        if (runs.highwaysReach()) {
            for (InterchangeGround.Site s : runs.reservesIn(px0, py0, World.TILE, World.TILE)) {
                if (!s.got()) continue;
                in.addReserve(s.kind(), s.stub(), (int) (s.x() - px0), (int) (s.y() - py0), (int) (s.x0() - px0), (int) (s.y0() - py0), s.w(), s.h());
            }
        }
        // The railway's picture over the tile: the freight station's yard, and whether its tracks have wagons standing.
        long[] station = freightStation();
        in.stationX = station == null ? Long.MIN_VALUE : station[0];
        in.stationY = station == null ? Long.MIN_VALUE : station[1];
        in.goodsByRail = railPicture.goodsByRail();
        in.wagonTracks = wagonTracksOf(railPicture.wagonShare());
        // Its buildings: its yards (0.7.89), its plan's boxes, and those packed at the city's edge (R7). (FX5 Y2) A yard is a building on
        // every tile its box reaches, its part there (the box clipped to the tile: a yard a unit train long spans two or three), counted
        // once, on the tile holding its first plot; and the tile is given the yard whole, its box in the tile's plots and the layout the
        // routes lay (yardLayoutOf()), which the raster draws clipped to the tile.
        int boxBits = (1 << BOX_TYPE_SHIFT) - 1;
        List<long[]> laidYards = runs.yards();
        in.clearYards();
        for (int yi = 0; yi < laidYards.size(); yi++) {
            long[] yd = laidYards.get(yi);
            if (yd[0] >= px0 + World.TILE || yd[1] >= py0 + World.TILE || yd[0] + yd[2] <= px0 || yd[1] + yd[3] <= py0) continue;
            int terminal = yardTypeOf(yi);
            if (terminal < 0) continue;
            int cx0 = (int) Math.max(0, yd[0] - px0), cy0 = (int) Math.max(0, yd[1] - py0);
            int cx1 = (int) Math.min(World.TILE, yd[0] + yd[2] - px0), cy1 = (int) Math.min(World.TILE, yd[1] + yd[3] - py0);
            int part = box(cx0, cy0, cx1 - cx0, cy1 - cy0, false);
            if (yd[0] >= px0 && yd[1] >= py0) in.addBuilding(part, terminal);
            else in.addBuildingPart(part, terminal);
            in.addYard((int) (yd[0] - px0), (int) (yd[1] - py0), (int) yd[2], (int) yd[3], terminal, yardLayoutOf(yi),
                    station != null && yd[0] == station[0] && yd[1] == station[1]);
        }
        if (own != null && own.boxes[k] != null) for (int b : own.boxes[k]) in.addBuilding(b & boxBits, b >>> BOX_TYPE_SHIFT);
        int[][] pk = packedIn(d);
        if (pk != null && pk[k] != null) for (int b : pk[k]) in.addBuilding(b & boxBits, b >>> BOX_TYPE_SHIFT);
        // The works on the shore (0.7.97): each box on the tile holding it (inside one cell's interior, as a yard), its quay's plots on the water.
        in.clearSea();
        for (CityShore.Work wk : shore.works()) {
            if (wk.x0() >= px0 && wk.y0() >= py0 && wk.x0() < px0 + World.TILE && wk.y0() < py0 + World.TILE) {
                in.addBuilding(box((int) (wk.x0() - px0), (int) (wk.y0() - py0), wk.w(), wk.h(), false), wk.type());
            }
            for (int q = 1; q <= wk.quay(); q++) {
                long[] at = wk.quayPlot(q);
                if (at[0] >= px0 && at[1] >= py0 && at[0] < px0 + World.TILE && at[1] < py0 + World.TILE) in.sea[(int) ((at[1] - py0) * World.TILE + (at[0] - px0))] = TilePainter.QUAY;
            }
        }
        atSeaInto(px0, py0, in);
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
     * (in field order) carry the mines the district holds from that holding -
     * since 0.8.0 each slot's own, in the walk's order, each works on as many
     * sites as it takes (District.siteRuns).
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
        // Each slot's works here, by holding, in the walk's order (0.8.0): {type, sites} pairs popped site by site.
        Map<Long, java.util.ArrayDeque<int[]>> runs = new HashMap<>();
        if (d != null) {
            for (int q = 0; q < SLOTS; q++) {
                int[] rs = d.siteRuns[q];
                if (rs == null) continue;
                for (int a = 0; a + 2 < rs.length; a += 3) {
                    runs.computeIfAbsent(((long) q << 32) | rs[a], x -> new java.util.ArrayDeque<>()).add(new int[] { rs[a + 1], rs[a + 2] });
                }
            }
        }
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            double w = Deposit.siteWidth(r);
            for (Deposit f : fieldsNear(r, x0, y0, x0 + DISTRICT - 1, y0 + DISTRICT - 1)) {
                boolean legacy = f.cell() < 0;
                int slot = slotOf(r, f.type());
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
                    java.util.ArrayDeque<int[]> q = h >= 0 && slot >= 0 ? runs.get(((long) slot << 32) | h) : null;
                    if (q != null && !q.isEmpty()) {
                        int[] head = q.peek();
                        mine = head[0];
                        if (--head[1] <= 0) q.poll();
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
        pictures.clear();
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
        readGround(in, worldKey);
        return in;
    }

    /** The ground over a plan's frame, a World class a plot, read from the world the key names (0.7.115; the input's seed's until then): any thread (World is safe), so a plan's job reads it away from the screen's (0.7.88). */
    static void readGround(DistrictPlan.Input in, World.Key world) {
        int f = DistrictPlan.FRAME;
        byte[] ter = new byte[TilePainter.PLOTS];
        int tiles = (f + World.TILE - 1) / World.TILE;
        World w = World.of(world);
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
        // ...and the month the city bought each plot (FX6, THE GROUND BOUGHT in DistrictPlan): from the grid's holdings, the
        // purchases dated; left null when the frame holds none of a purchase's ground.
        List<CityLand.Purchase> buys = land.purchases();
        int[] held = buys.isEmpty() ? null : new int[TilePainter.PLOTS];
        int tiles = (f + World.TILE - 1) / World.TILE;
        for (int j = 0; j < tiles; j++) {
            for (int i = 0; i < tiles; i++) {
                long tx = Math.floorDiv(x0, World.TILE) + i, ty = Math.floorDiv(y0, World.TILE) + j;
                tileOwnership(tx, ty, own);
                for (int y = 0; y < World.TILE && j * World.TILE + y < f; y++) {
                    System.arraycopy(own, y * World.TILE, in.owned, (j * World.TILE + y) * f + i * World.TILE, Math.min(World.TILE, f - i * World.TILE));
                }
                if (held == null || land.grid().cover(5, tx, ty) == LandGrid.NONE) continue;
                land.grid().tileHoldings(tx, ty, held);
                for (int y = 0; y < World.TILE && j * World.TILE + y < f; y++) {
                    for (int x = 0; x < World.TILE && i * World.TILE + x < f; x++) {
                        int h = held[y * World.TILE + x];
                        if (h < 1 || h > buys.size()) continue;
                        if (in.boughtAt == null) in.boughtAt = new int[f * f];
                        in.boughtAt[(j * World.TILE + y) * f + i * World.TILE + x] = buys.get(h - 1).month();
                    }
                }
            }
        }
        // The log its buildings came in (CL3): its first logN records, copied, and how many of them are one batch.
        in.logMonth = Arrays.copyOf(d.logMonth, d.logN);
        in.logType = Arrays.copyOf(d.logType, d.logN);
        in.logChange = Arrays.copyOf(d.logChange, d.logN);
        in.firstBatch = d.firstBatch;
        // Its buildings, its mines and wells on sites apart; its road plots by kind.
        in.counts = new int[types.length];
        long[] kind = new long[4];
        for (int t = 0; t < types.length && t < d.counts.length; t++) {
            if (types[t] == null || d.counts[t] <= 0) continue;
            if (types[t].road() != BuildingVisual.NOT_A_ROAD) { kind[types[t].road()] += (long) d.counts[t] * BuildingVisual.cells(types[t]); continue; }
            if (!types[t].drawn()) continue;
            in.counts[t] = Math.max(0, d.counts[t] - (d.sited == null ? 0 : d.sited[t]));
        }
        // ...less its Rail Terminals and Rail Depots the runs draw as yards (0.7.89; since FR7 slice 3 each of its own type).
        for (int t = 0; t < types.length && t < in.counts.length; t++) {
            if (types[t] == null || !types[t].terminal()) continue;
            in.counts[t] -= Math.min(yardsOf(d, t), in.counts[t]);
        }
        // ...and its terminals and tank farms the shore holds (0.7.97).
        if (!shore.isEmpty()) for (int t : shoreTypes()) if (t < in.counts.length) in.counts[t] = Math.max(0, in.counts[t] - shoreOf(d, t));
        // The city's highways and railway through its frame (0.7.89: the city's runs; THE NETWORK's plan's until 0.7.88), its yards
        // among them. The model's Elevated Highways are the runs' plots, none a street's surface: what the runs could not lay the
        // map counts (THE RUNS).
        if (runs.highways().started) in.ramp = new boolean[f * f];
        runsInto(d, in.fixed, in.ramp);
        // The works that need an access road (batch FR7): its yards and sea terminals.
        worksInto(d, in);
        // ...and its share of the Rail Spurs the runs' sidings left: a siding to each of that many of the bulk works its plan places (batch FR7 slice 5, D14).
        in.sidings = sidingsOf(d);
        if (spurCount > 0) in.sidingPlots = (int) Math.min(Integer.MAX_VALUE, spurTrack / spurCount);
        // The works on the shore over its frame (0.7.97): fixed as a yard's ground is - no street across, no building on it.
        if (!shore.isEmpty()) shore.fill(x0, y0, f, f, in.fixed, DistrictPlan.FIXED_YARD);
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
       ZONES BY DISTRICT (0.8.0, batch I14; docs/studies/spec-zoning.md 4:
       "the map tints a zone's districts ... Zones are quantities, not places
       (star Z1), so the map picks where to draw them; the map draws from the
       model and never the other way")

       A zone is ground held for one sector, an amount with no place. The
       map picks its districts: the zones in Zoning's order, each taking
       first the districts holding its sector's buildings - the most of
       their ground first, the map's order on a tie - then, while what it
       has taken covers less than its ground, the rest of the districts with
       free ground in the map's order, from the outside in for a sector of
       outer buildings (a mine, a farm, Forestry's) and from the inside out
       for any other. A district one zone takes is no other's. Nothing in
       the model reads it.
       ===================================================================== */

    /** A zone as the map tints it: its sector and the ground it covers, in square feet - what it holds free and what its sector has built in it (LandManager.heldBy() and usedBy()). */
    public record ZoneArea(String sector, double sqFt) { }

    /** A district a zone's tint covers: its box's first plot, {x, y} in the world, a district (DISTRICT plots) a side, and the zone's index in the list given. */
    public record Tinted(long x0, long y0, int zone) { }

    /** Each zone's districts (ZONES BY DISTRICT), in the map's order: none with no zone. */
    public List<Tinted> zoneTint(List<ZoneArea> zones) {
        List<Tinted> out = new ArrayList<>();
        int n = districts.size();
        if (zones == null || zones.isEmpty() || n == 0) return out;
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        for (int z = 0; z < zones.size(); z++) {
            ZoneArea a = zones.get(z);
            if (a == null || a.sector() == null || !(a.sqFt() > 0)) continue;
            boolean outer = false;
            List<Integer> mine = new ArrayList<>();
            for (int t = 0; t < types.length; t++) {
                if (types[t] == null || !a.sector().equals(types[t].sector())) continue;
                mine.add(t);
                outer |= types[t].outer();
            }
            double want = a.sqFt() / BuildingVisual.SQ_FT_PER_PLOT, got = 0;
            // ...the districts holding its sector's buildings, the most of their ground first.
            double[] ground = new double[n];
            List<Integer> holding = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                District d = districts.get(i);
                for (int t : mine) ground[i] += (double) d.counts[t] * types[t].sqFt();
                if (ground[i] > 0 && zoneOf[i] < 0 && d.owned > 0) holding.add(i);
            }
            holding.sort((x, y) -> ground[x] != ground[y] ? Double.compare(ground[y], ground[x]) : Integer.compare(x, y));
            for (int i : holding) {
                if (got >= want) break;
                zoneOf[i] = z;
                got += districts.get(i).owned;
            }
            // ...then the rest with free ground, in the map's order.
            for (int k = 0; k < n && got < want; k++) {
                int i = outer ? n - 1 - k : k;
                if (zoneOf[i] >= 0 || districts.get(i).owned <= 0) continue;
                zoneOf[i] = z;
                got += districts.get(i).owned;
            }
        }
        for (int i = 0; i < n; i++) {
            if (zoneOf[i] < 0) continue;
            District d = districts.get(i);
            out.add(new Tinted((baseDX + d.dx) * DISTRICT, (baseDY + d.dy) * DISTRICT, zoneOf[i]));
        }
        return out;
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
     * the world - its seed, or since 0.7.121 an Earth world's key folded,
     * worldId() - month, stamp, the founding site, the stamp of the land's rectangles
     * it was drawn on (CityLand.stamp(), 0.7.67) and its purchases, the type count, the district count), then a
     * record a district in its order - dx and dy (int16), its free plots and
     * those inside its tiles' edge rings (int32), plots used (double), owned
     * iron and oil sites (since FORMAT 7, 0.8.0, its owned sites of each slot,
     * SLOTS) and a count a type (int32) - and since FORMAT 5 the
     * city's highway and railway runs (int32 their count: 0 until batch RD3
     * lays them, which adds their records), and since FORMAT 6 (0.7.97) its
     * works on the shore (CityShore.write()), and since FORMAT 8 (0.8.1) the
     * gateway's bearing (int32, CityRuns.gateway()), and since FORMAT 9
     * (batch CL3) each district's log in the map's order: its record count
     * and its first batch (int32 each), then each record as unsigned varints -
     * the month as its step from the record before (the first from 0), the
     * type, the change zig-zagged. A map's log is brought up to its counts
     * before it is written (logChanges()), so what is written adds up.
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
        // The logs up to the counts first (CL3), every district's: a works put on its site since the month's reconcile moved a count, and a log that does not add up is not read back.
        if (format >= 9) logChanges(true);
        try {
            ByteArrayOutputStream raw = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(raw);
            out.writeInt(MAGIC);
            out.writeInt(format);
            out.writeLong(worldId(worldKey));
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
                // ...its sites by slot (FORMAT 7, 0.8.0), or as an older sidecar wrote them, iron's and oil's.
                if (format >= 7) for (int k = 0; k < SLOTS; k++) out.writeInt(d.sites[k]);
                else { out.writeInt(d.sites(Resource.IRON)); out.writeInt(d.sites(Resource.OIL)); }
                for (int t = 0; t < types.length; t++) out.writeInt(d.counts[t]);
            }
            // The runs (FORMAT 5): their count - 0 for none - and since 0.7.89 (batch RD3) the highways' and the railway's.
            if (format >= 5) {
                int n = withRuns ? runs.runs() : 0;
                out.writeInt(n);
                if (n > 0) runs.write(out, format);
            }
            // The works on the shore (FORMAT 6, 0.7.97): their count, then each one's box, type, side and quay.
            if (format >= 6) shore.write(out);
            // The gateway's bearing (FORMAT 8, 0.8.1): after the shore, so an older reader stops at what it knows.
            if (format >= 8) out.writeInt(runs.gateway());
            // Each district's log (FORMAT 9, CL3), in the map's order: its record count and first batch, then the records as varints.
            if (format >= 9) {
                for (District d : districts) {
                    out.writeInt(d.logN);
                    out.writeInt(d.firstBatch);
                    int before = 0;
                    for (int k = 0; k < d.logN; k++) {
                        writeVarint(out, d.logMonth[k] - before);
                        before = d.logMonth[k];
                        writeVarint(out, d.logType[k]);
                        writeVarint(out, (d.logChange[k] << 1) ^ (d.logChange[k] >> 31));
                    }
                }
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

    /** An unsigned varint (CL3): seven bits a byte, the low first, the high bit set where more follow. */
    static void writeVarint(DataOutputStream out, int v) throws IOException {
        while ((v & ~0x7F) != 0) {
            out.writeByte((v & 0x7F) | 0x80);
            v >>>= 7;
        }
        out.writeByte(v);
    }

    /** ...read back; an IOException for one more than five bytes long. */
    static int readVarint(DataInputStream in) throws IOException {
        int v = 0;
        for (int shift = 0; shift < 35; shift += 7) {
            int b = in.readUnsignedByte();
            v |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) return v;
        }
        throw new IOException("a varint of more than five bytes");
    }

    /**
     * The world a sidecar names in its header (0.7.121, batch E4): a generated
     * world's seed, as every sidecar has carried it, so theirs are the bytes
     * they were; an Earth world's whole key - its seed, spot and data -
     * folded into one long (SplitMix64, as the stamp is), so a map drawn on
     * one Earth is never read back onto another, nor onto the generated world
     * of the same seed (E2's section 7: the key was not kept).
     */
    static long worldId(World.Key key) {
        if (!key.earth()) return key.seed();
        long h = World.mix(key.seed() ^ 0xEA27L);
        h = World.mix(h ^ Double.doubleToLongBits(key.lat()));
        h = World.mix(h ^ Double.doubleToLongBits(key.lon()));
        return World.mix(h ^ key.data().hashCode());
    }

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
     * with. Since FORMAT 9 (CL3) the districts' logs are read back, and a
     * district's that does not add up to its counts, type by type, makes it
     * null (the map is drawn again); an older sidecar's districts each start
     * a log of their counts, one batch at the sidecar's month.
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
            long world = in.readLong();
            int savedMonth = in.readInt();
            long savedStamp = in.readLong();
            if (world != worldId(land.key()) || savedMonth != month || savedStamp != stamp || stampOf(raw) != stamp) return null;
            long sx = in.readLong(), sy = in.readLong();
            long landStamp = in.readLong();
            int bought = in.readInt();
            if (sx != land.siteX() || sy != land.siteY() || bought != land.purchases().size() || landStamp != land.stamp(bought)) return null;
            int nTypes = in.readInt(), n = in.readInt();
            if (nTypes > types.length || n < 0) return null;
            CityMap m = new CityMap(land.key(), sx, sy, types);
            m.month = savedMonth;
            m.land = land;
            m.remaining = remaining;
            for (int i = 0; i < n; i++) {
                int dx = in.readShort(), dy = in.readShort();
                District d = m.newDistrict(dx, dy);
                d.owned = in.readInt();
                d.inner = in.readInt();
                double used = in.readDouble();
                d.tileFree = null;
                if (format >= 7) for (int k = 0; k < SLOTS; k++) d.sites[k] = in.readInt();
                else {
                    // ...an older sidecar's iron and oil (0.8.0): on their first slots until the index counts them by type.
                    d.sites[slotOf(Resource.IRON, Resource.IRON.typesOf().get(0))] = in.readInt();
                    d.sites[slotOf(Resource.OIL, Resource.OIL.typesOf().get(0))] = in.readInt();
                }
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
                CityRuns r = CityRuns.read(in, format);
                if (r == null) return null;
                m.runs = m.guarded(r);
            }
            // The works on the shore: read back since FORMAT 6 (0.7.97); a FORMAT 5 map's laid now from its counts.
            if (format >= 6) {
                CityShore s = CityShore.read(in);
                if (s == null) return null;
                m.shore = s;
            }
            // The gateway's bearing (FORMAT 8, 0.8.1): into the runs, whether they were read or none; an older sidecar leaves it unknown.
            if (format >= 8) m.runs.setGateway(in.readInt());
            // Each district's log (FORMAT 9, CL3), in the order it was written - the districts are not sorted yet: it must add up, type by type, to the district's counts, or the map is drawn again.
            if (format >= 9) {
                int[] sums = new int[types.length];
                for (District d : m.districts) {
                    int logN = in.readInt(), firstBatch = in.readInt();
                    if (logN < 0 || firstBatch < 0 || firstBatch > logN || logN > raw.length) return null;
                    d.logMonth = logN == 0 ? NO_LOG : new int[logN];
                    d.logType = logN == 0 ? NO_LOG : new int[logN];
                    d.logChange = logN == 0 ? NO_LOG : new int[logN];
                    Arrays.fill(sums, 0);
                    int at = 0;
                    for (int k = 0; k < logN; k++) {
                        int step = readVarint(in), t = readVarint(in), z = readVarint(in);
                        if (step < 0 || t < 0 || t >= types.length) return null;
                        at += step;
                        d.logMonth[k] = at;
                        d.logType[k] = t;
                        d.logChange[k] = (z >>> 1) ^ -(z & 1);
                        sums[t] += d.logChange[k];
                    }
                    if (!Arrays.equals(sums, d.counts)) return null;
                    d.logN = logN;
                    d.firstBatch = firstBatch;
                    d.rehash();
                    boolean any = false;
                    for (int c : d.counts) if (c != 0) { any = true; break; }
                    d.logged = any ? d.counts.clone() : null;
                    d.logDirty = false;
                }
            }
            m.runs.setDoubleMain(m.hasFreightLine(m.have));
            m.purchasesSeen = bought;
            m.centreSeen = land.centreStamp();
            m.sortDistricts();
            m.pyramid = new Pyramid(m);
            // The interchanges' reserves are the runs and the ground's, found when asked and not saved (batch FR7).
            m.runs.layReserves(m.runGround);
            if (nRuns == 0) m.layRuns(m.have);
            if (format < 6) {
                // 0.7.96's map and before (0.7.97): the refinery gathered into its campus once, the shore laid from the counts.
                m.roadShare = m.roadShareOf(m.have);
                m.gatherCampus();
                m.layShore(m.have);
            }
            // A sidecar of FORMAT 8 or older has no logs (CL3): each district's starts as its counts, one batch at the save's month, once all the reader does is done.
            if (format < 9) {
                m.logChanges();
                m.batchLogs();
            }
            return m;
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /** Whether two maps hold the same districts with the same figures, in the same order, and (0.7.89) the same runs, and (0.7.97) the same works on the shore. */
    public boolean same(CityMap o) {
        return sameDistricts(o) && runs.same(o.runs) && shore.same(o.shore);
    }

    /** Whether two maps hold the same districts with the same figures, in the same order, and (CL3) the same logs: the count of records, the first batch and every record. */
    public boolean sameDistricts(CityMap o) {
        return sameDistricts(o, true);
    }

    /** ...with or without their logs: the districts of a map read from a sidecar of FORMAT 8 or older, whose logs begin at its month, are the same as the map's own without them. */
    public boolean sameDistricts(CityMap o, boolean withLogs) {
        if (o == null || o.seed != seed || o.districts.size() != districts.size() || o.types.length != types.length) return false;
        for (int i = 0; i < districts.size(); i++) {
            District a = districts.get(i), b = o.districts.get(i);
            if (a.dx != b.dx || a.dy != b.dy || a.owned != b.owned || a.usedHalf != b.usedHalf || !Arrays.equals(a.counts, b.counts)) return false;
            if (withLogs && !a.sameLog(b)) return false;
        }
        return true;
    }
}
