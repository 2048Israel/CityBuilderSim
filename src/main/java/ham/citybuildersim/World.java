package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and river, and the fields of the seven resources - every one of them a pure function of one seed.
 *
 * WHY THIS EXISTS (0.7.56, batch J1a; the project's spec-land.md 2.1). The
 * land office sold parcels drawn by a generator seeded with the parcel's id:
 * a plot had a size, a price and some iron, and no place. Jerus's map mockup
 * (city-map.html) painted a city on real ground - a coast, a lake, a river,
 * forest, deposits - and asked for the land office to sell that ground. This
 * is that ground at the size of a world, so a city can grow from a village to
 * ten billion people without reaching an edge. The model reads it since
 * 0.7.57: the city's land is a piece of it (CityLand, batch J1b), its lakes
 * and river hold the water plants to what they yield (J2), the map paints it
 * (J3, J4) and the wells lift its oil (K). At 0.7.56 nothing read it, and the
 * default playtest was byte for byte 0.7.55's.
 *
 * THE GRID (spec-land star 4). A plot is 30 m; a tile 32 plots (0.96 km); a
 * district 256 (7.68 km); a world cell 2,048 (61.44 km) - each nested in the
 * next by a power of two. The world is 368 x 368 cells, 511.2 million km2,
 * the Earth's 510.1 million within 0.2%. A plot's coordinates run 0 to
 * 753,663 east and south from the north-west corner; beyond them is sea.
 *
 * THE SAME ON EVERY MACHINE. Every value comes from SplitMix64 (mix(), the
 * finaliser LandMarket's scramble uses) of the seed, the field, the octave
 * and the lattice point, and from IEEE arithmetic, which Java does the same
 * everywhere; every sine, cosine, power, logarithm and exponential is
 * StrictMath's, which is bit-for-bit the same on every JVM where Math's may
 * differ by an ulp. A world built twice, or on Jerus's PC, is the same world.
 *
 * BUILT ON FIRST USE. new World(seed) and World.of(seed) cost nothing; the
 * first question asked of a world runs the sea pass (the sea's level, and
 * since 0.7.79 its shelf's, from four samples a cell, the land and forest in
 * every cell), the founding
 * site's search and the river - about half a second - and nothing is stored
 * that the seed cannot give back. World.of() keeps the last few worlds asked
 * for, so the screens and the land office share one.
 *
 * FINER WHERE IT MATTERS. The cells carry the totals and the far view; near
 * the city every function is evaluated per plot (terrainAt()), a tile at a
 * time (tileTerrain(), each octave's lattice once a tile) or a region at a
 * stride (regionTerrain(), the octaves narrower than a pixel replaced by
 * their mean), and a cell's fields are drawn only when asked for.
 */
public final class World {

    /* =====================================================================
       THE GRID
       ===================================================================== */

    /** A plot's side, in metres: 30, the map's smallest square and the mockup's cell. */
    public static final double PLOT_M = 30;

    /** A plot's area in square kilometres: 30 m squared, 0.0009. */
    public static final double KM2_PER_PLOT = PLOT_M * PLOT_M / 1e6;

    /** Plots on a tile's side: 32, 0.96 km - the unit the map paints (spec-land 2.6). */
    public static final int TILE = 32;

    /** Plots on a district's side: 256, 7.68 km - the unit the map stores (spec-land 2.5). */
    public static final int DISTRICT = 256;

    /** Plots on a world cell's side: 2,048, 61.44 km - the unit the world's totals and far view are counted in, so plot, tile, district and cell nest by powers of two (spec-land star 4). */
    public static final int CELL = 2048;

    /** World cells on the world's side: 368, so the world is 511.2 million km2, within 0.2% of the Earth's 510.1 million. */
    public static final int CELLS = 368;

    /** Plots on the world's side: 2,048 x 368, 753,664 (22,610 km). */
    public static final long SIDE = (long) CELL * CELLS;

    /** A world cell's area in square kilometres: 61.44 km squared, 3,774.87. */
    public static final double CELL_KM2 = (CELL * PLOT_M / 1000) * (CELL * PLOT_M / 1000);

    /* ---------------------------------------------------- the terrain's classes */

    /** Open ground: dry and buildable. */
    public static final byte GRASS = 0;

    /** Forest: dry and buildable, and standing timber (Resource.FOREST). */
    public static final byte FOREST = 1;

    /** Fresh water: a lake, or the founding river. */
    public static final byte FRESH = 2;

    /** Salt water: the sea, and everything beyond the world's edge. */
    public static final byte SALT = 3;

    /** Beach: dry land within BEACH_BAND of the sea's level. */
    public static final byte SAND = 4;

    /* =====================================================================
       THE TERRAIN: VALUE NOISE IN OCTAVES (spec-land 2.1)

       Each field is value noise - a hashed value at every lattice point,
       smoothstep-interpolated - summed over octaves of wavelength 2^hi down
       to 2^lo plots, each octave OCTAVE_FALLOFF times the amplitude of the
       one above, and divided by the amplitudes' sum, so a field runs 0 to 1
       with its mean at 0.5. Elevation is continents and coasts; the lake and
       forest fields are read only on land.
       ===================================================================== */

    /** Each octave's amplitude against the one above it: 0.55, a little rougher than classic fractal noise's half (the mockup's terrain, scaled to plots). */
    public static final double OCTAVE_FALLOFF = 0.55;

    /** Elevation's coarse octaves run from 2^16 plots (1,966 km): continents and oceans. */
    public static final int ELEV_COARSE_HI = 16;

    /** ...down to 2^9 plots (15 km): coasts and bays. */
    public static final int ELEV_COARSE_LO = 9;

    /** Elevation's fine octaves run from 2^8 plots (7.7 km)... */
    public static final int ELEV_FINE_HI = 8;

    /** ...down to 2^4 plots (480 m): headlands, coves and islets. */
    public static final int ELEV_FINE_LO = 4;

    /** The coarse octaves' share of the elevation: 0.85, so the coast is the continents' and the fine octaves only fray it. */
    public static final double ELEV_COARSE_WEIGHT = 0.85;

    /** The fine octaves' share: the rest, 0.15. */
    public static final double ELEV_FINE_WEIGHT = 0.15;

    /** The lake field's octaves run from 2^10 plots (30.7 km)... */
    public static final int LAKE_HI = 10;

    /** ...down to 2^5 plots (960 m). */
    public static final int LAKE_LO = 5;

    /** The forest field's octaves run from 2^8 plots (7.7 km)... */
    public static final int FOREST_HI = 8;

    /** ...down to 2^5 plots (960 m). */
    public static final int FOREST_LO = 5;

    /** The share of the world that is sea: 71%, the Earth's. The sea's level is this percentile of SEA_SAMPLES elevation samples a cell, so each world finds its own (0.546 to 0.571 over five seeds, measured). */
    public static final double SEA_SHARE = 0.71;

    /**
     * Elevation samples a world cell the sea's level is found from: 4, one at
     * a hashed point in each quarter of the cell (541,696 in all). Not at the
     * quarter points themselves, as the prototype took them: those are
     * lattice points of every octave of 2^9 plots and under, where value noise
     * is a bare lattice value and not a blend, so percentiles taken there are
     * not the world's - its sea came out 71.0 to 71.3% off the lattice, and
     * its lake and forest levels gave 5.0% lakes and 24% forest (measured, five
     * seeds, a million points each). Jittered, the sea is 70.95 to 71.07%.
     */
    public static final int SEA_SAMPLES = 4;

    /** Lakes' share of land: 3.7%, of the Earth's non-glaciated land (Verpoorter 2014). LAKE_THETA is its percentile; WorldCheck holds every world to it. */
    public static final double LAKE_SHARE = 0.037;

    /**
     * The lake field's level from which ground is a lake: 0.7092, the 96.3rd
     * percentile of the field over land (LAKE_SHARE) - measured 0.7088 to
     * 0.7095 over five seeds, a million points each, off the lattice; a
     * constant, so no world needs a pass for it. (The design's 0.695 was the
     * percentile at the prototype's lattice-bound samples, and makes 5.0% of
     * land lakes; see SEA_SAMPLES.)
     */
    public static final double LAKE_THETA = 0.7092;

    /** Forest's share of land: 31% (FAO 2020). FOREST_THETA is its percentile; WorldCheck holds every world to it. */
    public static final double FOREST_SHARE = 0.31;

    /** The forest field's level from which land is forest: 0.5676, the 69th percentile of the field over land (FOREST_SHARE), measured 0.5672 to 0.5679 as LAKE_THETA was (the design's 0.595 makes 24% of land forest). */
    public static final double FOREST_THETA = 0.5676;

    /** How far above the sea's level, in the elevation field, land is beach: 0.0015. */
    public static final double BEACH_BAND = 0.0015;

    /* ----- the sea's depth (0.7.79, batch O3; runs/spec-oil.md 2.7) -----
     * The generated world had a sea and no depth; a platform stands only on
     * shallow sea (O10), so the sea gets one, from the same elevation field:
     * the shallowest SHELF_SHARE of the sea's samples are the continental
     * shelf, its edge SHELF_BREAK_M deep, and depthAt() runs linearly in the
     * elevation from the sea's level to that edge and on below it. Found in
     * the sea pass from its own sorted samples (shelfTheta()), so a world
     * from any save has one and nothing is stored; the prototype
     * (scratch-oil's ShelfProto) measured the shelf's level 0.528-0.557 over
     * three seeds. GEBCO's bathymetry can replace it (spec-earth). */

    /** The continental shelf's share of the sea: 8.86% (est., the shelf's share of the ocean's area, Harris et al. 2014 - spec-oil 2.7 and 6, to confirm). */
    public static final double SHELF_SHARE = 0.0886;

    /** The depth of the shelf's edge, in metres: 140 (est., the mean shelf break - spec-oil 2.7 and 6, to confirm); depthAt() is this at shelfTheta(). */
    public static final double SHELF_BREAK_M = 140;

    /* =====================================================================
       THE FOUNDING SITE (spec-land 2.1)

       Take the cells that are 25 to 75% land, start at the nearest to the
       world's middle, and spiral out from its centre; the first plot that
       passes the tests is the site. A cell whose spiral finds none hands
       on to the next nearest (the prototype stopped at the first cell; seed
       2026 needs a second, WorldCheck). Since 0.7.99 (batch W1) a founding
       asks the first three - dry ground, land round it, a coast - and no
       longer the fourth, iron near the site: a city may start with no
       deposit at all, and imports what it needs. The search an older
       save's land is converted on (searchSites()) keeps all four, on the
       fields the world had then, so it finds the site it always found.
       ===================================================================== */

    /** The least land, in quarters of a cell's SEA_SAMPLES, a cell needs to be searched for a site: 1, 25%. */
    public static final int SITE_CELL_LAND_MIN = 1;

    /** The most: 3, 75% - a coast, not open sea or an interior. */
    public static final int SITE_CELL_LAND_MAX = 3;

    /** The spiral's step: 17 plots (510 m) between rings, and the ring's points about that far apart (six a ring per ring). */
    public static final int SITE_STEP = 17;

    /** Rings in a cell's spiral: 70, out to 1,173 plots (35 km), past the cell's own half-width of 1,024. */
    public static final int SITE_RINGS = 70;

    /** Test 1: the site and eight points this far round it are dry ground, in plots: 20 (600 m), room for a new city's centre - since 0.7.67 rings of 120 m blocks round the site's own, on the default world 21 of them, 0.30 km2, within 360 m of it each way (a 0.28 km2 square, half-side 264 m, until 0.7.66). */
    public static final int SITE_DRY_PLOTS = 20;

    /** Test 2: of SITE_LAND_SAMPLES points within 5 km, at least this share are land: 60%. */
    public static final double SITE_LAND_SHARE = 0.6;

    /** ...over 48 points, at 1.5, 3 and 5 km in turn round the compass. */
    public static final int SITE_LAND_SAMPLES = 48;

    /** ...at these radii, in plots: 50, 100 and 167 (1.5, 3 and 5 km). */
    static final int[] SITE_LAND_RADII = { 50, 100, 167 };

    /** Test 3: no sea within this many plots in 16 directions: 45 (1.35 km). */
    public static final int SITE_SEA_NOT_WITHIN = 45;

    /** ...but sea within this many in at least one: 70 (2.1 km) - a coast with room for a town. */
    public static final int SITE_SEA_WITHIN = 70;

    /**
     * Test 4 (spec-land star), the conversion's alone since 0.7.99: an iron
     * field's centre within this many km of the old world's fields
     * (legacyFieldsInCell()) - the mockup put deposits near the site "so the
     * first mines come early"; at IRON's 0.2 fields a km2 it passed about 92%
     * of the time. A founding no longer asks it (batch W1; Jerus, 2026-10-08:
     * "a city doesnt require a deposit"): only searchSites(), where an older
     * save's land is put back on the site it had.
     */
    public static final double SITE_IRON_KM = 2;

    /* =====================================================================
       THE FOUNDING LAKE AND RIVER (the mockup's random walk, in plots)

       A lake on the far side of the site from the sea, a walk from it past
       the site to the sea, widening as it goes. Rebuilt from the seed when a
       world is built (0.3 to 0.6 ms); elsewhere fresh water is lakes.
       ===================================================================== */

    /** Directions the sea is looked for in, round the site: 32. */
    static final int RIVER_SEA_DIRECTIONS = 32;

    /** ...out to this many plots, from SITE_SEA_NOT_WITHIN: 100 (3 km). */
    static final int RIVER_SEA_REACH = 100;

    /** ...in steps of this many plots: 5. */
    static final int RIVER_SEA_STEP = 5;

    /** Tries at a lake whose shore is dry: 32. */
    static final int RIVER_LAKE_TRIES = 32;

    /** The lake's bearing from the site: opposite the sea, give or take half of this many radians: 1.7. */
    static final double RIVER_LAKE_FAN = 1.7;

    /** The lake's centre this many plots from the site, at the least: 64 (1.9 km)... */
    static final double RIVER_LAKE_NEAR = 64;

    /** ...plus up to this many: 26, so 64 to 90 plots (1.9 to 2.7 km). */
    static final double RIVER_LAKE_SPAN = 26;

    /** The lake's radius in plots, at the least: 11 (330 m)... */
    static final double LAKE_R_MIN = 11;

    /** ...plus up to this many: 6, so 11 to 17 plots. */
    static final double LAKE_R_SPAN = 6;

    /** The lake's shore is tested this many radii out: 1.6... */
    static final double LAKE_SHORE = 1.6;

    /** ...for ground this far above the sea's level: 0.003, two beaches. */
    static final double LAKE_SHORE_RISE = 0.003;

    /** The river passes the site at least this many plots to one side: 8 (240 m)... */
    static final double RIVER_PASS_MIN = 8;

    /** ...plus up to this many: 12, so 8 to 20 plots. */
    static final double RIVER_PASS_SPAN = 12;

    /** Within this many plots of its passing point the river turns for the sea: 10. */
    static final double RIVER_PASS_REACHED = 10;

    /** Plots the river moves a step: 3 (90 m). */
    static final double RIVER_STEP = 3;

    /** Its heading wanders by up to half this many radians a step: 0.75... */
    static final double RIVER_WANDER = 0.75;

    /** ...and turns this share of the way to its target a step: 0.25. */
    static final double RIVER_STEER = 0.25;

    /** The most steps it takes: 500 (45 km). */
    static final int RIVER_MAX_STEPS = 500;

    /** It ends where the elevation is this far under the sea's level: 0.002. */
    static final double RIVER_MOUTH = 0.002;

    /** Its half-width at the lake, in plots: 1 (a 60 m river)... */
    static final double RIVER_HALF_WIDTH = 1.0;

    /** ...growing by this many plots to the sea: 0.95, so 1.95 at the mouth (117 m). */
    static final double RIVER_WIDENS = 0.95;

    /* =====================================================================
       THE DEPOSITS (spec-land 2.1; the densities and sizes are Resource's)

       A Poisson count of fields a world cell, its mean the resource's
       density times the cell's land. A field's sites are
       floor((1-u)^(-1/1.5)), capped at 512: most fields one or two sites, a
       few huge. A cell's total is drawn WITHOUT drawing a field - its count
       times the mean sites times the amount a site times a richness - so the
       world's totals cost one pass over the cells; its fields come lazily
       from the cell's own stream and share that total by sites, the last
       taking the remainder. Amounts are whole tonnes (or cubic metres), so
       every sum is exact in a double: the world's largest, its iron, is
       1.1e15, under 2^53 (9.0e15).

       THAT IS THE OLD WORLD'S FIELDS (to 0.7.98), kept as
       legacyFieldsInCell(): an older save's ground holds them (CityLand,
       THE FIELDS ON A PIECE OF GROUND). The count and the total of a cell
       are still drawn so, and still make the world's totals to the tonne;
       since 0.7.99 the fields a city sees are drawn from them as FEWER AND
       BIGGER DEPOSITS, below.
       ===================================================================== */

    /** How heavy the tail of a field's size is: 1.5, so P(sites >= k) = k^-1.5 - most small, a few huge. */
    public static final double FIELD_TAIL = 1.5;

    /** The most sites one of the old world's fields holds: 512 (the cap of the tail's draw, sites()). */
    public static final int LEGACY_MAX_SITES = 512;

    /** The mean sites one of the old world's fields: the sum of k^-1.5 for k = 1 to LEGACY_MAX_SITES, 2.524 - exact for the capped tail, since P(sites >= k) = k^-1.5. */
    public static final double MEAN_SITES = meanSites();

    /** A cell's richness, its total against its count's mean, at the least: 0.6... */
    public static final double RICHNESS_MIN = 0.6;

    /** ...plus up to this: 0.8, so 0.6 to 1.4, with a mean of one. */
    public static final double RICHNESS_SPAN = 0.8;

    /** Times a field centred in the sea is drawn again: 8, since ore lies under land. Oil keeps its first place - offshore oil is real. */
    public static final int SEA_REDRAWS = 8;

    /** Above this mean a cell's count is drawn as a rounded normal rather than by inversion: 40. */
    static final double POISSON_NORMAL_ABOVE = 40;

    /** Standing timber a square kilometre of forest, in cubic metres: 13,700 (FAO 2020: 557 billion m3 on 4.06 billion hectares). */
    public static final double FOREST_M3_PER_KM2 = 13_700;

    /* =====================================================================
       FEWER AND BIGGER DEPOSITS (0.7.99, batch W1; Jerus, 2026-10-08: "the
       world has too many deposits, a city doesnt require a deposit, you can
       just make a deposit deeper or in very big clusters"; asked to choose,
       a tenth as many fields, each ten times bigger, clustered, and the
       world's totals of each resource as they were)

       THE TONNES DO NOT MOVE. Every cell's count and total are drawn as the
       old world drew them (countOf(), totalOf()), so computeTotals() - what
       every saved city stores as W - is the same to the tonne. The fields are
       drawn a POOL at a time: a square of poolCells(r) cells, sized so a pool
       of land holds about POOL_CLUSTERS clusters. The pool's total, its cells'
       totals added, is shared among its fields exactly as a cell's was -
       whole tonnes, the last taking the remainder - so the world's fields
       still add up to W.

       A TENTH AS MANY, EACH TEN TIMES WIDER. A pool of N of the old world's
       fields holds N / FIELD_SCALE fields (the fraction drawn, at least one
       when N is not nought), and each of them is FIELD_SCALE of the old
       fields' sites added (★W1-1: wider, not deeper: the same tonnes a site,
       so a mine or a well lasts as long as it did and its ground costs what
       it did a site; the world keeps its sites, and a field has ten times
       as many). So a field holds ten times the tonnes on the mean, the most
       FIELD_SCALE x LEGACY_MAX_SITES sites, the least FIELD_SCALE.

       IN CLUSTERS. A pool's fields lie in about one cluster for every
       CLUSTER_FIELDS of them, each cluster's centre in one of the pool's
       cells drawn as its share of the old fields (so on land as the old
       fields were, and the world's ore off the sea), its fields anywhere in a
       disc of clusterKm(r) round it: a disc holding CLUSTER_FIELDS fields at
       the old world's density (★W1-2). Where there are fields they lie as
       thickly as they did everywhere, and a tenth of the land holds them.
       A field takes the richness of the cell its cluster stands in, as a
       cell's fields did (its share of the pool's total by its sites times
       that richness). Ore is drawn off the sea as the old fields were; oil
       keeps its place, offshore fields and all.

       A FIELD IS LISTED IN THE CELL ITS CENTRE IS IN, as before, so the
       land office, the map and the city find it by its cell: a cell's fields
       are the fields of every pool whose clusters reach it, in the pools'
       order and then the order drawn, numbered from FIELD_INDEX_FROM - never
       an old world's field's number, so the two can stand in one city.
       Pools are drawn once and kept (POOLS_KEPT).
       ===================================================================== */

    /** How many of the old world's fields one field stands for: 10 (Jerus, 2026-10-08: "a tenth as many fields, each ten times bigger", the world's totals as they were). */
    public static final int FIELD_SCALE = 10;

    /** The most sites one field holds: FIELD_SCALE of the old world's largest, 5,120. */
    public static final int MAX_SITES = FIELD_SCALE * LEGACY_MAX_SITES;

    /** The fields a cluster holds on the mean: 10 (★W1-2, est.: Jerus's "very big clusters" - ten fields a cluster, as a field is ten of the old). */
    public static final int CLUSTER_FIELDS = 10;

    /** The clusters a pool of land holds on the mean, at the least: 10 (★W1-2, est.): enough that a pool's clusters lie where the draws put them, not one a pool on a lattice - a pool a quarter land still holds two or three. */
    public static final int POOL_CLUSTERS = 10;

    /** The first number a field is listed under in its cell: 65,536, past any old world's field's (a cell held at most 868 of them on the default world, iron's). */
    public static final int FIELD_INDEX_FROM = 1 << 16;

    /** Pools kept, by resource and place: 256 (an iron pool is about 300 fields, some 15 KB). */
    static final int POOLS_KEPT = 256;

    /** The stream a pool is drawn from. */
    private static final long POOL_SALT = 0x5EB0C1A5L;

    /* =====================================================================
       THE WORLD, ITS STATE
       ===================================================================== */

    /** How many worlds World.of() keeps: 4, the city's and a few the founding screen looked at (about 0.3 MB each). */
    static final int WORLDS_KEPT = 4;

    private static final Map<Long, World> KEPT = new LinkedHashMap<>(8, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, World> eldest) {
            return size() > WORLDS_KEPT;
        }
    };

    private final long seed;
    private volatile boolean built;

    private double seaTheta;
    /** The shelf's edge in the elevation field (0.7.79): see shelfTheta(). */
    private double shelfTheta;
    /** Each cell's land and forest, in quarters (of its SEA_SAMPLES). */
    private byte[] landQuarters, forestQuarters;
    private long fx, fy;
    private int siteChecks, siteCells;
    private double[] riverX, riverY, riverW;
    private double lakeX, lakeY, lakeR;
    private double riverBox0x, riverBox0y, riverBox1x, riverBox1y;

    /** A world from its seed. Costs nothing: it is built the first time it is asked anything. */
    public World(long seed) {
        this.seed = seed;
    }

    /** The world a seed makes, shared: the same object for the same seed while it is among the last WORLDS_KEPT asked for. */
    public static World of(long seed) {
        synchronized (KEPT) {
            return KEPT.computeIfAbsent(seed, World::new);
        }
    }

    /** The seed it was made from. */
    public long seed() { return seed; }

    /** The sea pass, the site and the river, once. */
    private void build() {
        if (built) return;
        synchronized (this) {
            if (built) return;
            seaPass();
            found();
            walkRiver();
            built = true;
        }
    }

    /* =====================================================================
       THE NOISE
       ===================================================================== */

    /** SplitMix64's finaliser - LandMarket's scramble: every value in the world is this of the seed and a place. */
    static long mix(long z) {
        z += 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    /** A hash as a number in [0, 1), from its top 53 bits. */
    static double unit(long h) { return (h >>> 11) * 0x1.0p-53; }

    /** The value at one lattice point of one octave. */
    static double lattice(long s, long ix, long iy) {
        return unit(mix(s ^ (ix * 0x9E3779B97F4A7C15L) ^ (iy * 0xC2B2AE3D27D4EB4FL)));
    }

    /** Value noise at (x, y) in lattice units, smoothstep-interpolated. */
    static double vnoise(long s, double x, double y) {
        long ix = (long) Math.floor(x), iy = (long) Math.floor(y);
        double xf = x - ix, yf = y - iy;
        double u = xf * xf * (3 - 2 * xf), v = yf * yf * (3 - 2 * yf);
        double a = lattice(s, ix, iy), b = lattice(s, ix + 1, iy), c = lattice(s, ix, iy + 1), d = lattice(s, ix + 1, iy + 1);
        return a + (b - a) * u + (c - a) * v + (a - b - c + d) * u * v;
    }

    /* The fields' keys: what makes one field's octaves another's. */
    private static final long F_ELEV = 0x1111, F_ELEV_FINE = F_ELEV ^ 0x55, F_LAKE = 0x2222, F_FOREST = 0x3333;

    /** OCTAVE_FALLOFF to the power n, by repeated multiplication - the order fbm() has always summed in. */
    private static final double[] AMP = new double[ELEV_COARSE_HI + 1];
    static {
        AMP[0] = 1;
        for (int n = 1; n < AMP.length; n++) AMP[n] = AMP[n - 1] * OCTAVE_FALLOFF;
    }

    /** An octave's amplitude under a field's top octave. */
    static double amp(int hi, int k) { return AMP[hi - k]; }

    /** The sum of a field's amplitudes, top octave first. */
    static double norm(int hi, int lo) {
        double n = 0;
        for (int k = hi; k >= lo; k--) n += amp(hi, k);
        return n;
    }

    private static final double NORM_COARSE = norm(ELEV_COARSE_HI, ELEV_COARSE_LO), NORM_FINE = norm(ELEV_FINE_HI, ELEV_FINE_LO),
            NORM_LAKE = norm(LAKE_HI, LAKE_LO), NORM_FOREST = norm(FOREST_HI, FOREST_LO);

    /** One octave's stream. */
    private long oct(long field, int k) { return mix(seed ^ field ^ (k * 0x632BE59BD9B4E019L)); }

    /** A field at (x, y), in plots: octaves hi to lo, normalised to 0..1. */
    double fbm(long field, double x, double y, int hi, int lo) {
        double s = 0, norm = 0;
        for (int k = hi; k >= lo; k--) {
            double w = (double) (1L << k);
            double a = amp(hi, k);
            s += a * vnoise(oct(field, k), x / w, y / w);
            norm += a;
        }
        return s / norm;
    }

    /** The elevation at a point, in plots: below seaTheta() is sea. */
    double elevation(double x, double y) {
        return ELEV_COARSE_WEIGHT * fbm(F_ELEV, x, y, ELEV_COARSE_HI, ELEV_COARSE_LO)
                + ELEV_FINE_WEIGHT * fbm(F_ELEV_FINE, x, y, ELEV_FINE_HI, ELEV_FINE_LO);
    }

    /** The lake field at a point: LAKE_THETA and over, on land, is a lake. */
    double lakeField(double x, double y) { return fbm(F_LAKE, x, y, LAKE_HI, LAKE_LO); }

    /** The forest field at a point: FOREST_THETA and over, on dry land, is forest. */
    double forestField(double x, double y) { return fbm(F_FOREST, x, y, FOREST_HI, FOREST_LO); }

    /* =====================================================================
       THE SEA PASS: the sea's level, and each cell's land and forest
       ===================================================================== */

    private void seaPass() {
        int n = CELLS * CELLS;
        double[] e = new double[n * SEA_SAMPLES], xs = new double[n * SEA_SAMPLES], ys = new double[n * SEA_SAMPLES];
        for (int c = 0, k = 0; c < n; c++) for (int q = 0; q < SEA_SAMPLES; q++, k++) {
            long h = mix(seed ^ mix(((long) c << 2 | q) ^ 0x5EA5EA5EL));
            xs[k] = (c % CELLS + 0.5 * (q & 1) + 0.5 * unit(h)) * CELL;
            ys[k] = (c / CELLS + 0.5 * (q >> 1) + 0.5 * unit(mix(h))) * CELL;
            e[k] = elevation(xs[k], ys[k]);
        }
        double[] sorted = e.clone();
        Arrays.sort(sorted);
        seaTheta = sorted[(int) (SEA_SHARE * sorted.length)];
        // ...and the shelf's edge, from the same samples: the shallowest SHELF_SHARE of the sea's lie at it and above (0.7.79).
        int seaN = (int) (SEA_SHARE * sorted.length);
        shelfTheta = sorted[(int) (seaN - SHELF_SHARE * seaN)];
        landQuarters = new byte[n];
        forestQuarters = new byte[n];
        for (int c = 0; c < n; c++) {
            int land = 0, forest = 0;
            for (int q = 0; q < SEA_SAMPLES; q++) {
                int k = c * SEA_SAMPLES + q;
                double v = e[k];
                if (v < seaTheta) continue;
                land++;
                if (v >= seaTheta + BEACH_BAND && lakeField(xs[k], ys[k]) < LAKE_THETA && forestField(xs[k], ys[k]) >= FOREST_THETA) forest++;
            }
            landQuarters[c] = (byte) land;
            forestQuarters[c] = (byte) forest;
        }
    }

    /** The sea's level: the elevation SEA_SHARE of the world's samples lie under. */
    public double seaTheta() { build(); return seaTheta; }

    /** The continental shelf's edge in the elevation field (0.7.79): the level the shallowest SHELF_SHARE of the sea's samples lie at and above, so under seaTheta(). */
    public double shelfTheta() { build(); return shelfTheta; }

    /**
     * The sea's depth at a plot's centre, in metres below its level (0.7.79,
     * batch O3; spec-oil 2.7): 0 where the plot is not sea (land, beach, a
     * lake or the river - on the world terrainAt() is SALT exactly where it
     * is over 0), SHELF_BREAK_M at shelfTheta(), and linear in the
     * elevation from the sea's level to the shelf's edge and on at the same
     * slope below it. Off the world, what the elevation there says. Pure; an
     * elevation's cost, about half a microsecond.
     */
    public double depthAt(long x, long y) {
        build();
        double e = elevation(x + 0.5, y + 0.5);
        return e < seaTheta ? SHELF_BREAK_M * (seaTheta - e) / (seaTheta - shelfTheta) : 0;
    }

    /** A cell's land, as a share of its samples: 0, 0.25, 0.5, 0.75 or 1. */
    public double cellLandShare(int cell) { build(); return landQuarters[cell] / (double) SEA_SAMPLES; }

    /** A cell's forest, as a share of its samples. */
    public double cellForestShare(int cell) { build(); return forestQuarters[cell] / (double) SEA_SAMPLES; }

    /** The world's land, as a share of every cell's samples: 29%, by the sea's level's definition. */
    public double landShare() {
        build();
        long land = 0;
        for (byte b : landQuarters) land += b;
        return land / (double) (landQuarters.length * SEA_SAMPLES);
    }

    /** The world cell a plot is in, as row x CELLS + column; -1 off the world. */
    public static int cellOf(long x, long y) {
        if (x < 0 || y < 0 || x >= SIDE || y >= SIDE) return -1;
        return (int) (y / CELL) * CELLS + (int) (x / CELL);
    }

    /* =====================================================================
       THE FOUNDING SITE
       ===================================================================== */

    private void found() {
        int mid = CELLS / 2, n = CELLS * CELLS, count = 0;
        long[] order = new long[n];
        for (int c = 0; c < n; c++) {
            if (landQuarters[c] < SITE_CELL_LAND_MIN || landQuarters[c] > SITE_CELL_LAND_MAX) continue;
            long dx = c % CELLS - mid, dy = c / CELLS - mid;
            order[count++] = ((dx * dx + dy * dy) << 20) | c;   // nearest first, then the lower index
        }
        order = Arrays.copyOf(order, count);
        Arrays.sort(order);
        for (long key : order) {
            int c = (int) (key & 0xFFFFF);
            siteCells++;
            double ox = (c % CELLS + 0.5) * CELL, oy = (c / CELLS + 0.5) * CELL;
            for (int ring = 0; ring < SITE_RINGS; ring++) {
                int pts = Math.max(1, ring * 6);
                for (int p = 0; p < pts; p++) {
                    double a = 2 * Math.PI * p / pts, r = ring * (double) SITE_STEP;
                    long px = Math.round(ox + r * StrictMath.cos(a)), py = Math.round(oy + r * StrictMath.sin(a));
                    if (siteOk(px, py)) {
                        fx = px;
                        fy = py;
                        return;
                    }
                }
            }
        }
        throw new IllegalStateException("world " + seed + " has no coast to found a city on");
    }

    /** Dry ground at a point: land, and not a lake. */
    private boolean dryAt(double x, double y) { return elevation(x, y) >= seaTheta && lakeField(x, y) < LAKE_THETA; }

    /** A founding's tests, cheapest first, at the centre of plot (px, py) - the first three since 0.7.99 (no iron) - counted in siteChecks(). */
    private boolean siteOk(long px, long py) {
        siteChecks++;
        return siteTests(px, py, null);
    }

    /**
     * ...and uncounted: tests 1 to 3, and with `iron` (the old world's iron
     * fields by cell, filled as they are asked for) the fourth, for the
     * conversion's search (searchSites()).
     */
    private boolean siteTests(long px, long py, Map<Integer, List<Deposit>> iron) {
        double x = px + 0.5, y = py + 0.5;
        if (!dryAt(x, y)) return false;
        for (int q = 0; q < 8; q++) {
            double a = q * Math.PI / 4;
            if (!dryAt(x + SITE_DRY_PLOTS * StrictMath.cos(a), y + SITE_DRY_PLOTS * StrictMath.sin(a))) return false;
        }
        int land = 0;
        for (int q = 0; q < SITE_LAND_SAMPLES; q++) {
            double a = q * 2 * Math.PI / SITE_LAND_SAMPLES, r = SITE_LAND_RADII[q % SITE_LAND_RADII.length];
            if (elevation(x + r * StrictMath.cos(a), y + r * StrictMath.sin(a)) >= seaTheta) land++;
        }
        if (land < siteLandMin()) return false;
        boolean sea = false;
        for (int q = 0; q < 16; q++) {
            double a = q * Math.PI / 8, c = StrictMath.cos(a), s = StrictMath.sin(a);
            if (elevation(x + SITE_SEA_NOT_WITHIN * c, y + SITE_SEA_NOT_WITHIN * s) < seaTheta) return false;
            if (!sea && elevation(x + SITE_SEA_WITHIN * c, y + SITE_SEA_WITHIN * s) < seaTheta) sea = true;
        }
        if (!sea) return false;
        if (iron == null) return true;
        double reach = SITE_IRON_KM * 1000 / PLOT_M;
        for (long cy = (long) Math.floor((y - reach) / CELL); cy <= (long) Math.floor((y + reach) / CELL); cy++) {
            for (long cx = (long) Math.floor((x - reach) / CELL); cx <= (long) Math.floor((x + reach) / CELL); cx++) {
                if (cx < 0 || cy < 0 || cx >= CELLS || cy >= CELLS) continue;
                int cell = (int) (cy * CELLS + cx);
                for (Deposit d : iron.computeIfAbsent(cell, k -> legacyFieldsOf(k, Resource.IRON))) {
                    double dx = d.x() + 0.5 - x, dy = d.y() + 0.5 - y;
                    if (dx * dx + dy * dy <= reach * reach) return true;
                }
            }
        }
        return false;
    }

    /**
     * THE FOUNDING SEARCH AGAIN, A CELL AT A TIME (0.7.57, batch J1b): in
     * each of the nearest `cells` coastal cells, in the founding search's
     * order, the first plot of its spiral that passes the four tests is
     * offered to `accept`, and the first site it takes is returned as {x, y}
     * - or null, when it takes none. What LandConversion puts an older
     * city's land on, with a fifth test of its own (spec-land 2.9). Counts
     * nothing in siteChecks() or siteCells(), which are the founding's.
     * Since 0.7.99 its fourth test, iron within SITE_IRON_KM, reads the old
     * world's fields (legacyFieldsInCell()), and a founding no longer asks
     * it: an older save is converted on the site it always was, and the
     * founding site is no longer always the first offered.
     */
    public long[] searchSites(int cells, java.util.function.Predicate<long[]> accept) {
        build();
        int mid = CELLS / 2, n = CELLS * CELLS, count = 0;
        long[] order = new long[n];
        for (int c = 0; c < n; c++) {
            if (landQuarters[c] < SITE_CELL_LAND_MIN || landQuarters[c] > SITE_CELL_LAND_MAX) continue;
            long dx = c % CELLS - mid, dy = c / CELLS - mid;
            order[count++] = ((dx * dx + dy * dy) << 20) | c;
        }
        order = Arrays.copyOf(order, count);
        Arrays.sort(order);
        Map<Integer, List<Deposit>> iron = new HashMap<>();
        int tried = 0;
        for (long key : order) {
            if (tried++ >= cells) break;
            int c = (int) (key & 0xFFFFF);
            double ox = (c % CELLS + 0.5) * CELL, oy = (c / CELLS + 0.5) * CELL;
            search:
            for (int ring = 0; ring < SITE_RINGS; ring++) {
                int pts = Math.max(1, ring * 6);
                for (int p = 0; p < pts; p++) {
                    double a = 2 * Math.PI * p / pts, r = ring * (double) SITE_STEP;
                    long px = Math.round(ox + r * StrictMath.cos(a)), py = Math.round(oy + r * StrictMath.sin(a));
                    if (siteTests(px, py, iron)) {
                        long[] site = { px, py };
                        if (accept.test(site)) return site;
                        break search;
                    }
                }
            }
        }
        return null;
    }

    /** Test 2's least count of land samples: SITE_LAND_SHARE of SITE_LAND_SAMPLES, rounded up - 29 of 48. */
    static int siteLandMin() { return (int) Math.ceil(SITE_LAND_SHARE * SITE_LAND_SAMPLES); }

    /** The founding site, as a plot: east from the world's west edge. */
    public long foundingX() { build(); return fx; }

    /** ...and south from its north edge. */
    public long foundingY() { build(); return fy; }

    /** How many plots the founding search tested - the cost of finding the site. */
    public int siteChecks() { build(); return siteChecks; }

    /** ...and in how many cells: one, unless the nearest coastal cell had no site. */
    public int siteCells()  { build(); return siteCells; }

    /* =====================================================================
       THE FOUNDING LAKE AND RIVER
       ===================================================================== */

    private void walkRiver() {
        double sx = fx + 0.5, sy = fy + 0.5;
        long r = mix(seed ^ 0x5bd1e995L);
        // The sea's direction: the nearest sea sample round the site.
        double seaAng = 0, bestD = Double.MAX_VALUE;
        for (int q = 0; q < RIVER_SEA_DIRECTIONS; q++) {
            double a = q * 2 * Math.PI / RIVER_SEA_DIRECTIONS, c = StrictMath.cos(a), s = StrictMath.sin(a);
            for (int d = SITE_SEA_NOT_WITHIN; d <= RIVER_SEA_REACH; d += RIVER_SEA_STEP) {
                if (elevation(sx + d * c, sy + d * s) < seaTheta) {
                    if (d < bestD) { bestD = d; seaAng = a; }
                    break;
                }
            }
        }
        // The lake: opposite the sea, its shore dry.
        for (int tries = 0; tries < RIVER_LAKE_TRIES; tries++) {
            r = mix(r); double a = seaAng + Math.PI + (unit(r) - 0.5) * RIVER_LAKE_FAN;
            r = mix(r); double d = RIVER_LAKE_NEAR + unit(r) * RIVER_LAKE_SPAN;
            r = mix(r); lakeR = LAKE_R_MIN + unit(r) * LAKE_R_SPAN;
            lakeX = sx + StrictMath.cos(a) * d;
            lakeY = sy + StrictMath.sin(a) * d;
            boolean dry = true;
            for (int q = 0; q < 8 && dry; q++) {
                double b = q * Math.PI / 4;
                dry = elevation(lakeX + lakeR * LAKE_SHORE * StrictMath.cos(b), lakeY + lakeR * LAKE_SHORE * StrictMath.sin(b))
                        >= seaTheta + LAKE_SHORE_RISE;
            }
            if (dry) break;
        }
        // The walk: from the lake to a point beside the site, then to the sea.
        r = mix(r); int side = unit(r) < 0.5 ? -1 : 1;
        r = mix(r); double off = RIVER_PASS_MIN + unit(r) * RIVER_PASS_SPAN;
        double passX = sx - StrictMath.sin(seaAng) * side * off, passY = sy + StrictMath.cos(seaAng) * side * off;
        List<double[]> pts = new ArrayList<>();
        double rx = lakeX, ry = lakeY, heading = StrictMath.atan2(passY - ry, passX - rx);
        boolean passed = false;
        for (int k = 0; k < RIVER_MAX_STEPS; k++) {
            pts.add(new double[] { rx, ry });
            if (k > 3 && elevation(rx, ry) < seaTheta - RIVER_MOUTH) break;
            double px = passX - rx, py = passY - ry;
            if (!passed && px * px + py * py < RIVER_PASS_REACHED * RIVER_PASS_REACHED) passed = true;
            double target = passed ? seaAng : StrictMath.atan2(passY - ry, passX - rx);
            r = mix(r); heading += (unit(r) - 0.5) * RIVER_WANDER;
            double dA = target - heading;
            dA = StrictMath.atan2(StrictMath.sin(dA), StrictMath.cos(dA));
            heading += dA * RIVER_STEER;
            rx += StrictMath.cos(heading) * RIVER_STEP;
            ry += StrictMath.sin(heading) * RIVER_STEP;
        }
        int n = pts.size();
        riverX = new double[n];
        riverY = new double[n];
        riverW = new double[n];
        double x0 = Double.MAX_VALUE, y0 = Double.MAX_VALUE, x1 = -Double.MAX_VALUE, y1 = -Double.MAX_VALUE;
        for (int k = 0; k < n; k++) {
            riverX[k] = pts.get(k)[0];
            riverY[k] = pts.get(k)[1];
            riverW[k] = RIVER_HALF_WIDTH + RIVER_WIDENS * k / Math.max(1, n - 2);
            x0 = Math.min(x0, riverX[k]); y0 = Math.min(y0, riverY[k]);
            x1 = Math.max(x1, riverX[k]); y1 = Math.max(y1, riverY[k]);
        }
        double margin = RIVER_HALF_WIDTH + RIVER_WIDENS + RIVER_STEP;
        riverBox0x = x0 - margin; riverBox0y = y0 - margin;
        riverBox1x = x1 + margin; riverBox1y = y1 + margin;
    }

    /** The founding river and its lake, in plots: the walk's points and its half-width at each, and the lake's centre and radius. */
    public record River(double[] xs, double[] ys, double[] halfWidths, double lakeX, double lakeY, double lakeR) {

        /** How many points the walk has. */
        public int points() { return xs.length; }

        /** Whether two rivers are the same walk to the last bit. */
        public boolean same(River o) {
            return Arrays.equals(xs, o.xs) && Arrays.equals(ys, o.ys) && Arrays.equals(halfWidths, o.halfWidths)
                    && Double.compare(lakeX, o.lakeX) == 0 && Double.compare(lakeY, o.lakeY) == 0
                    && Double.compare(lakeR, o.lakeR) == 0;
        }
    }

    /** The founding river and lake (copies: nothing a caller does moves them). */
    public River river() {
        build();
        return new River(riverX.clone(), riverY.clone(), riverW.clone(), lakeX, lakeY, lakeR);
    }

    /** Whether a point is in the founding river. */
    private boolean inRiver(double x, double y) {
        if (x < riverBox0x || x > riverBox1x || y < riverBox0y || y > riverBox1y) return false;
        for (int k = 0; k + 1 < riverX.length; k++) {
            if (onSegment(k, x, y)) return true;
        }
        return false;
    }

    /** Whether a point is within the river's half-width of its k-th step. */
    private boolean onSegment(int k, double x, double y) {
        double ax = riverX[k], ay = riverY[k], vx = riverX[k + 1] - ax, vy = riverY[k + 1] - ay, l2 = vx * vx + vy * vy;
        double t = l2 > 0 ? ((x - ax) * vx + (y - ay) * vy) / l2 : 0;
        t = t < 0 ? 0 : t > 1 ? 1 : t;
        double dx = x - ax - t * vx, dy = y - ay - t * vy;
        return dx * dx + dy * dy < riverW[k] * riverW[k];
    }

    /** Whether a point is in the founding lake: within its radius (the mockup's halo past it never reaches the lake's level). */
    private boolean inLake(double x, double y) {
        double dx = x - lakeX, dy = y - lakeY;
        return dx * dx + dy * dy <= lakeR * lakeR;
    }

    /* =====================================================================
       THE TERRAIN, A PLOT, A TILE OR A REGION AT A TIME
       ===================================================================== */

    /** The class of a point's ground, from its fields: SALT, FRESH, SAND, FOREST or GRASS. */
    private byte classOf(double x, double y, double e, double lake, double forest) {
        if (e < seaTheta) return SALT;
        if (lake >= LAKE_THETA || inLake(x, y) || inRiver(x, y)) return FRESH;
        if (e < seaTheta + BEACH_BAND) return SAND;
        return forest >= FOREST_THETA ? FOREST : GRASS;
    }

    /** One plot's ground, at its centre: GRASS, FOREST, FRESH, SALT or SAND; SALT off the world. 650 to 930 ns. */
    public byte terrainAt(long x, long y) {
        build();
        if (x < 0 || y < 0 || x >= SIDE || y >= SIDE) return SALT;
        double px = x + 0.5, py = y + 0.5;
        double e = elevation(px, py);
        if (e < seaTheta) return SALT;
        double lake = lakeField(px, py);
        double forest = lake >= LAKE_THETA || e < seaTheta + BEACH_BAND ? 0 : forestField(px, py);
        return classOf(px, py, e, lake, forest);
    }

    /** The smoothstep at a plot's centre within an octave's lattice cell, for every wavelength 2^k: SMOOTH[k][f] for f = 0 .. 2^k - 1. */
    private static final double[][] SMOOTH = new double[ELEV_COARSE_HI + 1][];
    static {
        for (int k = 0; k <= ELEV_COARSE_HI; k++) {
            int w = 1 << k;
            SMOOTH[k] = new double[w];
            for (int f = 0; f < w; f++) {
                double t = (f + 0.5) / w;
                SMOOTH[k][f] = t * t * (3 - 2 * t);
            }
        }
    }

    /** One thread's working arrays for a tile. */
    private static final class TileScratch {
        final double[] e = new double[TILE * TILE], lake = new double[TILE * TILE], forest = new double[TILE * TILE];
        final double[] lat = new double[64];
        int[] segs = new int[64];
    }

    private static final ThreadLocal<TileScratch> SCRATCH = ThreadLocal.withInitial(TileScratch::new);

    /**
     * One tile's ground, a byte a plot in rows (out[py x 32 + px]): what
     * terrainAt() gives each of its plots, on 99.9 to 100% of them, five times
     * faster - each fine octave's lattice is hashed once a tile, and the coarse
     * octaves (2^9 plots and over) are interpolated between the tile's
     * corners. A tile off the world is sea. 65 to 70 us a tile.
     *
     * @param tx the tile's column, plots / TILE
     * @param ty the tile's row
     * @param out TILE x TILE bytes
     */
    public void tileTerrain(long tx, long ty, byte[] out) {
        build();
        if (tx < 0 || ty < 0 || tx >= SIDE / TILE || ty >= SIDE / TILE) {
            Arrays.fill(out, 0, TILE * TILE, SALT);
            return;
        }
        TileScratch s = SCRATCH.get();
        long x0 = tx * TILE, y0 = ty * TILE;
        double[] eAcc = s.e, lAcc = s.lake, fAcc = s.forest;
        // Elevation: the coarse octaves at the corners, bilinear between; the fine ones a plot at a time.
        double c00 = fbm(F_ELEV, x0, y0, ELEV_COARSE_HI, ELEV_COARSE_LO), c10 = fbm(F_ELEV, x0 + TILE, y0, ELEV_COARSE_HI, ELEV_COARSE_LO);
        double c01 = fbm(F_ELEV, x0, y0 + TILE, ELEV_COARSE_HI, ELEV_COARSE_LO), c11 = fbm(F_ELEV, x0 + TILE, y0 + TILE, ELEV_COARSE_HI, ELEV_COARSE_LO);
        bilinear(c00, c10, c01, c11, ELEV_COARSE_WEIGHT, eAcc);
        for (int k = ELEV_FINE_HI; k >= ELEV_FINE_LO; k--) {
            octaveTile(s.lat, oct(F_ELEV_FINE, k), k, x0, y0, ELEV_FINE_WEIGHT * amp(ELEV_FINE_HI, k) / NORM_FINE, eAcc);
        }
        // Lakes: 2^10 to 2^8 at the corners, 2^7 to 2^5 a plot at a time.
        int lakeCorner = LAKE_HI - 2;
        double l00 = part(F_LAKE, x0, y0, LAKE_HI, lakeCorner) / NORM_LAKE, l10 = part(F_LAKE, x0 + TILE, y0, LAKE_HI, lakeCorner) / NORM_LAKE;
        double l01 = part(F_LAKE, x0, y0 + TILE, LAKE_HI, lakeCorner) / NORM_LAKE, l11 = part(F_LAKE, x0 + TILE, y0 + TILE, LAKE_HI, lakeCorner) / NORM_LAKE;
        bilinear(l00, l10, l01, l11, 1, lAcc);
        for (int k = lakeCorner - 1; k >= LAKE_LO; k--) octaveTile(s.lat, oct(F_LAKE, k), k, x0, y0, amp(LAKE_HI, k) / NORM_LAKE, lAcc);
        Arrays.fill(fAcc, 0);
        for (int k = FOREST_HI; k >= FOREST_LO; k--) octaveTile(s.lat, oct(F_FOREST, k), k, x0, y0, amp(FOREST_HI, k) / NORM_FOREST, fAcc);
        // The river's steps within reach of this tile, once.
        int nseg = 0;
        if (!(x0 + TILE < riverBox0x || x0 > riverBox1x || y0 + TILE < riverBox0y || y0 > riverBox1y)) {
            if (s.segs.length < riverX.length) s.segs = new int[riverX.length];
            double reach = RIVER_HALF_WIDTH + RIVER_WIDENS;
            for (int k = 0; k + 1 < riverX.length; k++) {
                double mx0 = Math.min(riverX[k], riverX[k + 1]) - reach, mx1 = Math.max(riverX[k], riverX[k + 1]) + reach;
                double my0 = Math.min(riverY[k], riverY[k + 1]) - reach, my1 = Math.max(riverY[k], riverY[k + 1]) + reach;
                if (mx1 >= x0 && mx0 <= x0 + TILE && my1 >= y0 && my0 <= y0 + TILE) s.segs[nseg++] = k;
            }
        }
        double lakeReach = lakeR + TILE;
        boolean lakeNear = Math.abs(x0 + TILE / 2.0 - lakeX) < lakeReach && Math.abs(y0 + TILE / 2.0 - lakeY) < lakeReach;
        for (int i = 0; i < TILE * TILE; i++) {
            double e = eAcc[i];
            if (e < seaTheta) { out[i] = SALT; continue; }
            double x = x0 + (i % TILE) + 0.5, y = y0 + (i / TILE) + 0.5;
            boolean wet = lAcc[i] >= LAKE_THETA || (lakeNear && inLake(x, y));
            for (int q = 0; q < nseg && !wet; q++) wet = onSegment(s.segs[q], x, y);
            if (wet) { out[i] = FRESH; continue; }
            if (e < seaTheta + BEACH_BAND) { out[i] = SAND; continue; }
            out[i] = fAcc[i] >= FOREST_THETA ? FOREST : GRASS;
        }
    }

    /**
     * One row (or, `column`, one column) of a tile's ground, its `at`-th: the
     * TILE plots tileTerrain() gives that line, byte for byte - the same
     * corners, octaves and sums in the same order, evaluated on the line's
     * plots alone (0.7.89, batch RD3: the city map's highway and railway runs
     * read the ground along their corridors a line at a time, about a tenth of
     * a tile's cost). out[i] is the line's i-th plot, west to east (north to
     * south). A tile off the world is sea.
     */
    public void lineTerrain(long tx, long ty, boolean column, int at, byte[] out) {
        build();
        if (tx < 0 || ty < 0 || tx >= SIDE / TILE || ty >= SIDE / TILE) {
            Arrays.fill(out, 0, TILE, SALT);
            return;
        }
        LineScratch s = LINE_SCRATCH.get();
        long x0 = tx * TILE, y0 = ty * TILE;
        double[] eAcc = s.e, lAcc = s.lake, fAcc = s.forest;
        // The tile's corners (the elevation's coarse octaves and the lake's), each kept a while: the lines of a corridor run
        // through tiles side by side, which share two corners, and a tile's other lines all four.
        double[] cv;
        cv = s.corner(this, x0, y0);
        double c00 = cv[0], l00 = cv[1];
        cv = s.corner(this, x0 + TILE, y0);
        double c10 = cv[0], l10 = cv[1];
        cv = s.corner(this, x0, y0 + TILE);
        double c01 = cv[0], l01 = cv[1];
        cv = s.corner(this, x0 + TILE, y0 + TILE);
        double c11 = cv[0], l11 = cv[1];
        bilinearLine(c00, c10, c01, c11, ELEV_COARSE_WEIGHT, column, at, eAcc);
        for (int k = ELEV_FINE_HI; k >= ELEV_FINE_LO; k--) {
            octaveLine(s.lat, oct(F_ELEV_FINE, k), k, x0, y0, ELEV_FINE_WEIGHT * amp(ELEV_FINE_HI, k) / NORM_FINE, column, at, eAcc);
        }
        int lakeCorner = LAKE_HI - 2;
        bilinearLine(l00, l10, l01, l11, 1, column, at, lAcc);
        for (int k = lakeCorner - 1; k >= LAKE_LO; k--) octaveLine(s.lat, oct(F_LAKE, k), k, x0, y0, amp(LAKE_HI, k) / NORM_LAKE, column, at, lAcc);
        Arrays.fill(fAcc, 0);
        for (int k = FOREST_HI; k >= FOREST_LO; k--) octaveLine(s.lat, oct(F_FOREST, k), k, x0, y0, amp(FOREST_HI, k) / NORM_FOREST, column, at, fAcc);
        int nseg = 0;
        if (!(x0 + TILE < riverBox0x || x0 > riverBox1x || y0 + TILE < riverBox0y || y0 > riverBox1y)) {
            if (s.segs.length < riverX.length) s.segs = new int[riverX.length];
            double reach = RIVER_HALF_WIDTH + RIVER_WIDENS;
            for (int k = 0; k + 1 < riverX.length; k++) {
                double mx0 = Math.min(riverX[k], riverX[k + 1]) - reach, mx1 = Math.max(riverX[k], riverX[k + 1]) + reach;
                double my0 = Math.min(riverY[k], riverY[k + 1]) - reach, my1 = Math.max(riverY[k], riverY[k + 1]) + reach;
                if (mx1 >= x0 && mx0 <= x0 + TILE && my1 >= y0 && my0 <= y0 + TILE) s.segs[nseg++] = k;
            }
        }
        double lakeReach = lakeR + TILE;
        boolean lakeNear = Math.abs(x0 + TILE / 2.0 - lakeX) < lakeReach && Math.abs(y0 + TILE / 2.0 - lakeY) < lakeReach;
        for (int i = 0; i < TILE; i++) {
            double e = eAcc[i];
            if (e < seaTheta) { out[i] = SALT; continue; }
            int px = column ? at : i, py = column ? i : at;
            double x = x0 + px + 0.5, y = y0 + py + 0.5;
            boolean wet = lAcc[i] >= LAKE_THETA || (lakeNear && inLake(x, y));
            for (int q = 0; q < nseg && !wet; q++) wet = onSegment(s.segs[q], x, y);
            if (wet) { out[i] = FRESH; continue; }
            if (e < seaTheta + BEACH_BAND) { out[i] = SAND; continue; }
            out[i] = fAcc[i] >= FOREST_THETA ? FOREST : GRASS;
        }
    }

    /** One thread's working arrays for a line, and the tile corners it met last (their elevation's coarse octaves and lake's, as tileTerrain() reads them). */
    private static final class LineScratch {
        final double[] e = new double[TILE], lake = new double[TILE], forest = new double[TILE];
        final double[] lat = new double[64];
        int[] segs = new int[64];
        static final int CORNERS = 64;
        final long[] cornerX = new long[CORNERS], cornerY = new long[CORNERS], cornerSeed = new long[CORNERS];
        final boolean[] cornerSet = new boolean[CORNERS];
        final double[][] cornerV = new double[CORNERS][2];

        /** A tile corner's {elevation's coarse octaves, lake's corner octaves}, as tileTerrain()'s c00 and l00 at (x, y): kept by place and world. */
        double[] corner(World w, long x, long y) {
            int h = (int) ((World.mix(x * 0x9E3779B97F4A7C15L ^ y) >>> 58) & (CORNERS - 1));
            if (cornerSet[h] && cornerX[h] == x && cornerY[h] == y && cornerSeed[h] == w.seed) return cornerV[h];
            cornerV[h][0] = w.fbm(F_ELEV, x, y, ELEV_COARSE_HI, ELEV_COARSE_LO);
            cornerV[h][1] = w.part(F_LAKE, x, y, LAKE_HI, LAKE_HI - 2) / NORM_LAKE;
            cornerX[h] = x; cornerY[h] = y; cornerSeed[h] = w.seed; cornerSet[h] = true;
            return cornerV[h];
        }
    }

    private static final ThreadLocal<LineScratch> LINE_SCRATCH = ThreadLocal.withInitial(LineScratch::new);

    /** bilinear() on one line of the tile's plots: acc[i] is the line's i-th plot's value. */
    private static void bilinearLine(double c00, double c10, double c01, double c11, double scale, boolean column, int at, double[] acc) {
        for (int i = 0; i < TILE; i++) {
            int px = column ? at : i, py = column ? i : at;
            double v = (py + 0.5) / TILE;
            double u = (px + 0.5) / TILE;
            acc[i] = scale * (c00 + (c10 - c00) * u + (c01 - c00) * v + (c00 - c10 - c01 + c11) * u * v);
        }
    }

    /** octaveTile() on one line of the tile's plots: the same lattice, the same sum at each of its plots. */
    private static void octaveLine(double[] lat, long os, int k, long x0, long y0, double amp, boolean column, int at, double[] acc) {
        int w = 1 << k, n = TILE / w + 2;
        long lx0 = x0 >> k, ly0 = y0 >> k;
        for (int j = 0; j < n; j++) for (int i = 0; i < n; i++) lat[j * n + i] = amp * lattice(os, lx0 + i, ly0 + j);
        double[] sm = SMOOTH[k];
        int mask = w - 1;
        for (int i = 0; i < TILE; i++) {
            int px = column ? at : i, py = column ? i : at;
            long gy = y0 + py;
            int rj = (int) ((gy >> k) - ly0);
            double v = sm[(int) (gy & mask)];
            int r0 = rj * n, r1 = r0 + n;
            long gx = x0 + px;
            int ri = (int) ((gx >> k) - lx0);
            double u = sm[(int) (gx & mask)];
            double a = lat[r0 + ri], b = lat[r0 + ri + 1], c = lat[r1 + ri], d = lat[r1 + ri + 1];
            acc[i] += a + (b - a) * u + (c - a) * v + (a - b - c + d) * u * v;
        }
    }

    /** Sets acc to scale x the bilinear blend of four corner values at each plot's centre. */
    private static void bilinear(double c00, double c10, double c01, double c11, double scale, double[] acc) {
        for (int py = 0; py < TILE; py++) {
            double v = (py + 0.5) / TILE;
            for (int px = 0; px < TILE; px++) {
                double u = (px + 0.5) / TILE;
                acc[py * TILE + px] = scale * (c00 + (c10 - c00) * u + (c01 - c00) * v + (c00 - c10 - c01 + c11) * u * v);
            }
        }
    }

    /** Adds amp x one octave's value noise at a tile's 32 x 32 plot centres: its lattice hashed once, the smoothstep from SMOOTH. */
    private static void octaveTile(double[] lat, long os, int k, long x0, long y0, double amp, double[] acc) {
        int w = 1 << k, n = TILE / w + 2;
        long lx0 = x0 >> k, ly0 = y0 >> k;
        for (int j = 0; j < n; j++) for (int i = 0; i < n; i++) lat[j * n + i] = amp * lattice(os, lx0 + i, ly0 + j);
        double[] sm = SMOOTH[k];
        int mask = w - 1;
        for (int py = 0; py < TILE; py++) {
            long gy = y0 + py;
            int rj = (int) ((gy >> k) - ly0);
            double v = sm[(int) (gy & mask)];
            int row = py * TILE, r0 = rj * n, r1 = r0 + n;
            for (int px = 0; px < TILE; px++) {
                long gx = x0 + px;
                int ri = (int) ((gx >> k) - lx0);
                double u = sm[(int) (gx & mask)];
                double a = lat[r0 + ri], b = lat[r0 + ri + 1], c = lat[r1 + ri], d = lat[r1 + ri + 1];
                acc[row + px] += a + (b - a) * u + (c - a) * v + (a - b - c + d) * u * v;
            }
        }
    }

    /** Octaves hi to lo of a field at a point, at the amplitudes fbm() gives them under the field's top octave, not normalised. */
    private double part(long field, double x, double y, int top, int lo) {
        double s = 0;
        for (int k = top; k >= lo; k--) {
            double w = (double) (1L << k);
            s += amp(top, k) * vnoise(oct(field, k), x / w, y / w);
        }
        return s;
    }

    /**
     * A region's ground at a stride, for the far view (spec-land 2.6, L2): n x
     * n pixels from plot (x0, y0), each `stride` plots wide, classed at its
     * centre (out[py x n + px]). Every octave at least a pixel wide is
     * evaluated, a lattice once a region; an octave narrower than a pixel
     * would only alias, and stands at its mean, 0.5, so the far view's sea
     * is the near view's (the prototype dropped them, and its far views ran
     * low). At a stride of one it is terrainAt() for every plot.
     */
    public void regionTerrain(long x0, long y0, int stride, int n, byte[] out) {
        build();
        double[] e = new double[n * n], lake = new double[n * n], forest = new double[n * n];
        for (int k = ELEV_COARSE_HI; k >= ELEV_COARSE_LO; k--) {
            regionOctave(F_ELEV, k, x0, y0, stride, n, ELEV_COARSE_WEIGHT * amp(ELEV_COARSE_HI, k) / NORM_COARSE, e);
        }
        for (int k = ELEV_FINE_HI; k >= ELEV_FINE_LO; k--) {
            regionOctave(F_ELEV_FINE, k, x0, y0, stride, n, ELEV_FINE_WEIGHT * amp(ELEV_FINE_HI, k) / NORM_FINE, e);
        }
        for (int k = LAKE_HI; k >= LAKE_LO; k--) regionOctave(F_LAKE, k, x0, y0, stride, n, amp(LAKE_HI, k) / NORM_LAKE, lake);
        for (int k = FOREST_HI; k >= FOREST_LO; k--) regionOctave(F_FOREST, k, x0, y0, stride, n, amp(FOREST_HI, k) / NORM_FOREST, forest);
        for (int py = 0; py < n; py++) {
            double y = y0 + (double) py * stride + stride * 0.5;
            for (int px = 0; px < n; px++) {
                double x = x0 + (double) px * stride + stride * 0.5;
                int i = py * n + px;
                out[i] = x < 0 || y < 0 || x >= SIDE || y >= SIDE ? SALT : classOf(x, y, e[i], lake[i], forest[i]);
            }
        }
    }

    /** Adds amp x one octave over a region's pixel centres, or amp x its mean, 0.5, when the octave is no wider than a pixel. */
    private void regionOctave(long field, int k, long x0, long y0, int stride, int n, double amp, double[] acc) {
        if ((1L << k) <= stride) {
            double mean = amp * 0.5;
            for (int i = 0; i < n * n; i++) acc[i] += mean;
            return;
        }
        long os = oct(field, k);
        double wl = 1L << k;
        long lx0 = (long) Math.floor(x0 / wl), lx1 = (long) Math.floor((x0 + (double) stride * n) / wl) + 1;
        long ly0 = (long) Math.floor(y0 / wl), ly1 = (long) Math.floor((y0 + (double) stride * n) / wl) + 1;
        int nx = (int) (lx1 - lx0 + 1), ny = (int) (ly1 - ly0 + 1);
        double[] lat = new double[nx * ny];
        for (int j = 0; j < ny; j++) for (int i = 0; i < nx; i++) lat[j * nx + i] = amp * lattice(os, lx0 + i, ly0 + j);
        for (int py = 0; py < n; py++) {
            double gy = (y0 + (double) py * stride + stride * 0.5) / wl;
            long iy = (long) Math.floor(gy);
            double yf = gy - iy, v = yf * yf * (3 - 2 * yf);
            int rj = (int) (iy - ly0);
            for (int px = 0; px < n; px++) {
                double gx = (x0 + (double) px * stride + stride * 0.5) / wl;
                long ix = (long) Math.floor(gx);
                double xf = gx - ix, u = xf * xf * (3 - 2 * xf);
                int ri = (int) (ix - lx0);
                double a = lat[rj * nx + ri], b = lat[rj * nx + ri + 1], c = lat[(rj + 1) * nx + ri], d = lat[(rj + 1) * nx + ri + 1];
                acc[py * n + px] += a + (b - a) * u + (c - a) * v + (a - b - c + d) * u * v;
            }
        }
    }

    /* =====================================================================
       THE DEPOSITS
       ===================================================================== */

    /** The mean of the capped tail: P(sites >= k) summed over k = 1 .. LEGACY_MAX_SITES. */
    private static double meanSites() {
        double m = 0;
        for (int k = 1; k <= LEGACY_MAX_SITES; k++) m += StrictMath.pow(k, -FIELD_TAIL);
        return m;
    }

    /** One of the old world's fields' sites from a uniform draw: floor((1-u)^(-1/FIELD_TAIL)), at most LEGACY_MAX_SITES - and since 0.7.99 each of the FIELD_SCALE draws a field's sites add up. */
    static int sites(double u) {
        return (int) Math.min(LEGACY_MAX_SITES, Math.floor(StrictMath.pow(1 - u, -1 / FIELD_TAIL)));
    }

    /** A Poisson count from a hash: by inversion for a small mean, a rounded normal above POISSON_NORMAL_ABOVE. */
    static long poisson(long h, double m) {
        if (!(m > 0)) return 0;
        double u = unit(h);
        if (m > POISSON_NORMAL_ABOVE) {
            double u2 = unit(mix(h));
            double z = StrictMath.sqrt(-2 * StrictMath.log(Math.max(1e-300, u))) * StrictMath.cos(2 * Math.PI * u2);
            return Math.max(0, Math.round(m + StrictMath.sqrt(m) * z));
        }
        double p = StrictMath.exp(-m), f = p;
        long n = 0;
        while (u > f && n < 10_000) {
            n++;
            p *= m / n;
            f += p;
        }
        return n;
    }

    /** A cell's stream for a resource. */
    private long cellHash(int cell, Resource r) {
        return mix(seed ^ mix(((long) cell << 8) | r.ordinal()) ^ 0xDEADBEEFL);
    }

    /** A cell's land, in square kilometres: its land share times its area. */
    private double landKm2(int cell) { return landQuarters[cell] / (double) SEA_SAMPLES * CELL_KM2; }

    /** How many fields of a resource a cell lists (fieldsInCell(); since 0.7.99 the fewer and bigger ones). None of forest. */
    public long cellFieldCount(int cell, Resource r) {
        build();
        return fieldsOf(cell, r).size();
    }

    /** ...and how many of the old world's it held (to 0.7.98): a Poisson count, its mean the resource's density times the cell's land - what its total is drawn from, and what legacyFieldsInCell() lists. */
    public long legacyCellFieldCount(int cell, Resource r) {
        build();
        return countOf(cell, r);
    }

    private long countOf(int cell, Resource r) {
        if (!r.inFields()) return 0;
        return poisson(cellHash(cell, r), r.fieldsPerKm2() * landKm2(cell));
    }

    /**
     * What a cell holds of a resource, in whole tonnes (forest, whole cubic
     * metres): its old count x MEAN_SITES x the amount a site x the cell's
     * richness, drawn without drawing a field. Forest: its forest area x
     * FOREST_M3_PER_KM2. The old world's fields of the cell share it; since
     * 0.7.99 it is the cell's part of its pool's total, which the pool's
     * fields share (poolTotal()), wherever in the pool they lie.
     */
    public double cellTotal(int cell, Resource r) {
        build();
        return totalOf(cell, r);
    }

    private double totalOf(int cell, Resource r) {
        if (!r.inFields()) {
            return Math.rint(forestQuarters[cell] / (double) SEA_SAMPLES * CELL_KM2 * FOREST_M3_PER_KM2);
        }
        long h = cellHash(cell, r);
        long n = poisson(h, r.fieldsPerKm2() * landKm2(cell));
        double rich = RICHNESS_MIN + RICHNESS_SPAN * unit(mix(h ^ 0x77));
        return Math.rint(n * MEAN_SITES * r.amountPerSite() * rich);
    }

    /** A cell's richness for a resource, RICHNESS_MIN to + RICHNESS_SPAN: the draw its total is struck at (totalOf()), and since 0.7.99 the richness of a cluster standing in it. */
    private double richnessOf(int cell, Resource r) {
        return RICHNESS_MIN + RICHNESS_SPAN * unit(mix(cellHash(cell, r) ^ 0x77));
    }

    /**
     * A cell's fields of a resource as the old world drew them (to 0.7.98),
     * from the cell's own stream: each one's centre (drawn again, up to
     * SEA_REDRAWS times, while it is in the sea - but oil's), its sites, and
     * its share of cellTotal() by sites, rounded so the shares sum to the
     * total exactly, numbered from 0. Empty for forest. What an older save's
     * ground holds (CityLand.fieldsIn()), and the conversion's.
     */
    public List<Deposit> legacyFieldsInCell(int cell, Resource r) {
        build();
        return legacyFieldsOf(cell, r);
    }

    private List<Deposit> legacyFieldsOf(int cell, Resource r) {
        if (!r.inFields()) return List.of();
        int n = (int) countOf(cell, r);
        if (n == 0) return List.of();
        double total = totalOf(cell, r);
        long h = mix(cellHash(cell, r) ^ 0x1234);
        long cx = (long) (cell % CELLS) * CELL, cy = (long) (cell / CELLS) * CELL;
        long[] xs = new long[n], ys = new long[n];
        int[] s = new int[n];
        long raw = 0;
        for (int i = 0; i < n; i++) {
            h = mix(h); xs[i] = cx + (long) (unit(h) * CELL);
            h = mix(h); ys[i] = cy + (long) (unit(h) * CELL);
            if (r != Resource.OIL) {
                for (int again = 0; again < SEA_REDRAWS && elevation(xs[i] + 0.5, ys[i] + 0.5) < seaTheta; again++) {
                    h = mix(h); xs[i] = cx + (long) (unit(h) * CELL);
                    h = mix(h); ys[i] = cy + (long) (unit(h) * CELL);
                }
            }
            h = mix(h); s[i] = sites(unit(h));
            raw += s[i];
        }
        List<Deposit> out = new ArrayList<>(n);
        long cum = 0;
        double before = 0;
        for (int i = 0; i < n; i++) {
            cum += s[i];
            double upTo = i == n - 1 ? total : Math.rint(total * cum / raw);
            out.add(new Deposit(r, cell, i, xs[i], ys[i], s[i], upTo - before));
            before = upTo;
        }
        return out;
    }

    /* =====================================================================
       FEWER AND BIGGER DEPOSITS: THE POOLS AND THEIR CLUSTERS (0.7.99; the
       banner above, under THE DEPOSITS' constants)
       ===================================================================== */

    /** Each resource's pool side in cells, in Resource's order (poolCells()). */
    private static final int[] POOL_SIDE = new int[Resource.values().length];
    static {
        for (Resource r : Resource.values()) {
            POOL_SIDE[r.ordinal()] = r.inFields()
                    ? (int) Math.max(1, Math.ceil(Math.sqrt((double) POOL_CLUSTERS * CLUSTER_FIELDS * FIELD_SCALE
                            / (r.fieldsPerKm2() * CELL_KM2))))
                    : 0;
        }
    }

    /**
     * The side of a resource's pool, in cells (0.7.99): the fewest whose
     * square of land holds POOL_CLUSTERS clusters on the mean, POOL_CLUSTERS
     * x CLUSTER_FIELDS x FIELD_SCALE of the old world's fields - iron 2, oil
     * 4, stone 6, coal 12, copper 24, uranium 37. 0 for forest.
     */
    public static int poolCells(Resource r) { return POOL_SIDE[r.ordinal()]; }

    /** A resource's pools on the world's side: CELLS over poolCells(), rounded up (the last a part pool); 0 for forest. */
    public static int poolsOnSide(Resource r) {
        int b = poolCells(r);
        return b == 0 ? 0 : (CELLS + b - 1) / b;
    }

    /**
     * A cluster's radius for a resource, in km (0.7.99): the disc that holds
     * CLUSTER_FIELDS fields at the old world's density, sqrt(CLUSTER_FIELDS /
     * (pi x its fields a km2)) - iron 4.0, oil 12.6, stone 17.8, coal 39.9,
     * copper 79.8, uranium 126. 0 for forest.
     */
    public static double clusterKm(Resource r) {
        return r.inFields() ? Math.sqrt(CLUSTER_FIELDS / (Math.PI * r.fieldsPerKm2())) : 0;
    }

    /** A pool's fields as drawn: each one's centre plot, sites and amount, in the order drawn; the pool's total and its old count. */
    private static final class Pool {
        final long[] xs, ys;
        final int[] sites;
        final double[] amounts;
        final double total;
        final long old;

        Pool(long[] xs, long[] ys, int[] sites, double[] amounts, double total, long old) {
            this.xs = xs;
            this.ys = ys;
            this.sites = sites;
            this.amounts = amounts;
            this.total = total;
            this.old = old;
        }
    }

    private static final Pool NO_POOL = new Pool(new long[0], new long[0], new int[0], new double[0], 0, 0);

    /** The pools drawn, kept: by resource and place, the last POOLS_KEPT asked for. */
    private final Map<Long, Pool> pools = new LinkedHashMap<>(64, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, Pool> eldest) {
            return size() > POOLS_KEPT;
        }
    };

    /** A pool, drawn once and kept: its place in pools (px east, py south, each 0 to poolsOnSide() - 1). */
    private Pool pool(Resource r, int px, int py) {
        long key = (((long) py * CELLS + px) << 8) | r.ordinal();
        synchronized (pools) {
            Pool p = pools.get(key);
            if (p != null) return p;
        }
        Pool p = drawPool(r, px, py);
        synchronized (pools) {
            pools.put(key, p);
        }
        return p;
    }

    /** A pool's stream for a resource. */
    private long poolHash(Resource r, int px, int py) {
        return mix(seed ^ mix((((long) py * CELLS + px) << 8) | r.ordinal()) ^ POOL_SALT);
    }

    /** `count` over `per`, its fraction a draw (u under it adds one), at least one when `count` is not nought: how many fields N old ones make, and how many clusters those fields lie in. */
    static int atLeastOne(long count, int per, double u) {
        if (count <= 0) return 0;
        long whole = count / per, part = count % per;
        return (int) Math.max(1, whole + (u < (double) part / per ? 1 : 0));
    }

    /**
     * Draws a pool (0.7.99): its cells' old counts and totals, row by row;
     * its fields, N / FIELD_SCALE of the old count's N; its clusters, one for
     * every CLUSTER_FIELDS fields, each centred in a cell drawn as its share
     * of the old fields (off the sea, but oil's, as the old fields were) with
     * that cell's richness; each field in a cluster drawn as evenly as the
     * cluster's disc allows (drawn again off the sea up to SEA_REDRAWS times,
     * but oil's), its sites FIELD_SCALE of the old draws added; and the
     * pool's total shared by sites x richness, whole tonnes, the last taking
     * the remainder - so the pool's fields sum to its cells' totals exactly.
     */
    private Pool drawPool(Resource r, int px, int py) {
        int b = poolCells(r);
        int cx0 = px * b, cy0 = py * b, cx1 = Math.min(CELLS, cx0 + b), cy1 = Math.min(CELLS, cy0 + b);
        int nc = Math.max(0, cx1 - cx0) * Math.max(0, cy1 - cy0);
        int[] cellAt = new int[nc];
        long[] upTo = new long[nc];
        long old = 0;
        double total = 0;
        int k = 0;
        for (int cy = cy0; cy < cy1; cy++) {
            for (int cx = cx0; cx < cx1; cx++, k++) {
                int c = cy * CELLS + cx;
                cellAt[k] = c;
                if (landQuarters[c] > 0) {
                    old += countOf(c, r);
                    total += totalOf(c, r);
                }
                upTo[k] = old;
            }
        }
        if (old == 0) return NO_POOL;
        long h = poolHash(r, px, py);
        h = mix(h);
        int n = atLeastOne(old, FIELD_SCALE, unit(h));
        h = mix(h);
        int clusters = atLeastOne(n, CLUSTER_FIELDS, unit(h));
        // The clusters: each centred in a cell drawn as its share of the old fields, with that cell's richness.
        long[] ccx = new long[clusters], ccy = new long[clusters];
        double[] rich = new double[clusters];
        for (int c = 0; c < clusters; c++) {
            h = mix(h);
            long pick = Math.min(old - 1, (long) (unit(h) * old));
            int lo = 0, hi = nc - 1;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (upTo[mid] > pick) hi = mid; else lo = mid + 1;
            }
            int cell = cellAt[lo];
            long ox = (long) (cell % CELLS) * CELL, oy = (long) (cell / CELLS) * CELL;
            h = mix(h); ccx[c] = ox + (long) (unit(h) * CELL);
            h = mix(h); ccy[c] = oy + (long) (unit(h) * CELL);
            if (r != Resource.OIL) {
                for (int again = 0; again < SEA_REDRAWS && elevation(ccx[c] + 0.5, ccy[c] + 0.5) < seaTheta; again++) {
                    h = mix(h); ccx[c] = ox + (long) (unit(h) * CELL);
                    h = mix(h); ccy[c] = oy + (long) (unit(h) * CELL);
                }
            }
            rich[c] = richnessOf(cell, r);
        }
        // The fields: each anywhere in its cluster's disc, its sites FIELD_SCALE of the old draws.
        double radius = clusterKm(r) * 1000 / PLOT_M;
        long[] xs = new long[n], ys = new long[n];
        int[] s = new int[n];
        double[] weight = new double[n];
        double all = 0;
        for (int i = 0; i < n; i++) {
            h = mix(h);
            int c = (int) Math.min(clusters - 1, (long) (unit(h) * clusters));
            for (int tries = 0; ; tries++) {
                h = mix(h);
                double rr = radius * Math.sqrt(unit(h));
                h = mix(h);
                double a = 2 * Math.PI * unit(h);
                xs[i] = Math.max(0, Math.min(SIDE - 1, ccx[c] + Math.round(rr * StrictMath.cos(a))));
                ys[i] = Math.max(0, Math.min(SIDE - 1, ccy[c] + Math.round(rr * StrictMath.sin(a))));
                if (r == Resource.OIL || tries >= SEA_REDRAWS || elevation(xs[i] + 0.5, ys[i] + 0.5) >= seaTheta) break;
            }
            int sum = 0;
            for (int j = 0; j < FIELD_SCALE; j++) {
                h = mix(h);
                sum += sites(unit(h));
            }
            s[i] = sum;
            weight[i] = sum * rich[c];
            all += weight[i];
        }
        // The pool's total shared by weight: whole tonnes, the last the remainder, so the fields sum to it exactly.
        double[] amounts = new double[n];
        double cum = 0, before = 0;
        for (int i = 0; i < n; i++) {
            cum += weight[i];
            double to = i == n - 1 ? total : Math.rint(total * cum / all);
            amounts[i] = to - before;
            before = to;
        }
        return new Pool(xs, ys, s, amounts, total, old);
    }

    /** The pool holding a cell, as {px, py}, for a resource; null for forest or off the world. */
    public static int[] poolOf(int cell, Resource r) {
        int b = poolCells(r);
        if (b == 0 || cell < 0 || cell >= CELLS * CELLS) return null;
        return new int[] { (cell % CELLS) / b, (cell / CELLS) / b };
    }

    /** What a pool shares among its fields: its cells' totals added (cellTotal()), whole tonnes, row by row - without drawing a field. */
    public double poolTotal(Resource r, int px, int py) {
        build();
        int b = poolCells(r);
        double total = 0;
        for (int cy = py * b; cy < Math.min(CELLS, py * b + b); cy++) {
            for (int cx = px * b; cx < Math.min(CELLS, px * b + b); cx++) {
                int c = cy * CELLS + cx;
                if (landQuarters[c] > 0) total += totalOf(c, r);
            }
        }
        return total;
    }

    /** How many fields a pool holds (fewer and bigger, 0.7.99) - computed from its cells' old counts without drawing them. */
    public long poolFieldCount(Resource r, int px, int py) {
        build();
        int b = poolCells(r);
        if (b == 0) return 0;
        long old = poolLegacyCount(r, px, py);
        long h = mix(poolHash(r, px, py));
        return atLeastOne(old, FIELD_SCALE, unit(h));
    }

    /** ...and how many of the old world's fields its cells held: their old counts added. */
    public long poolLegacyCount(Resource r, int px, int py) {
        build();
        int b = poolCells(r);
        if (b == 0) return 0;
        long old = 0;
        for (int cy = py * b; cy < Math.min(CELLS, py * b + b); cy++) {
            for (int cx = px * b; cx < Math.min(CELLS, px * b + b); cx++) {
                int c = cy * CELLS + cx;
                if (landQuarters[c] > 0) old += countOf(c, r);
            }
        }
        return old;
    }

    /** The fields of a resource the whole world holds (0.7.99): every pool's, from their old counts, without drawing one. */
    public long fieldCount(Resource r) {
        long n = 0;
        for (int py = 0; py < poolsOnSide(r); py++) for (int px = 0; px < poolsOnSide(r); px++) n += poolFieldCount(r, px, py);
        return n;
    }

    /** ...and the old world's: every cell's old count. */
    public long legacyFieldCount(Resource r) {
        build();
        long n = 0;
        for (int c = 0; c < CELLS * CELLS; c++) if (landQuarters[c] > 0) n += countOf(c, r);
        return n;
    }

    /** A pool's fields, each as the cell its centre is in lists it (fieldsInCell()), in the order the pool drew them; one not found there is left out. */
    public List<Deposit> poolFields(Resource r, int px, int py) {
        build();
        Pool p = pool(r, px, py);
        Map<Integer, List<Deposit>> listed = new HashMap<>();
        List<Deposit> out = new ArrayList<>(p.xs.length);
        for (int i = 0; i < p.xs.length; i++) {
            for (Deposit d : listed.computeIfAbsent(cellOf(p.xs[i], p.ys[i]), c -> fieldsOf(c, r))) {
                if (d.x() == p.xs[i] && d.y() == p.ys[i] && d.sites() == p.sites[i] && d.amount() == p.amounts[i]) {
                    out.add(d);
                    break;
                }
            }
        }
        return out;
    }

    /**
     * A cell's fields of a resource (since 0.7.99, FEWER AND BIGGER
     * DEPOSITS): every field of every pool whose clusters reach the cell
     * whose centre plot is in it, pool by pool (north to south, west to
     * east) in the order each was drawn, numbered from FIELD_INDEX_FROM.
     * Empty for forest.
     */
    public List<Deposit> fieldsInCell(int cell, Resource r) {
        build();
        return fieldsOf(cell, r);
    }

    private List<Deposit> fieldsOf(int cell, Resource r) {
        if (!r.inFields() || cell < 0 || cell >= CELLS * CELLS) return List.of();
        int b = poolCells(r), last = poolsOnSide(r) - 1;
        long x0 = (long) (cell % CELLS) * CELL, y0 = (long) (cell / CELLS) * CELL;
        long reach = (long) Math.ceil(clusterKm(r) * 1000 / PLOT_M) + 1;
        int px0 = (int) Math.max(0, Math.floorDiv(Math.floorDiv(x0 - reach, CELL), b));
        int px1 = (int) Math.min(last, Math.floorDiv(Math.floorDiv(x0 + CELL - 1 + reach, CELL), b));
        int py0 = (int) Math.max(0, Math.floorDiv(Math.floorDiv(y0 - reach, CELL), b));
        int py1 = (int) Math.min(last, Math.floorDiv(Math.floorDiv(y0 + CELL - 1 + reach, CELL), b));
        List<Deposit> out = new ArrayList<>();
        for (int py = py0; py <= py1; py++) {
            for (int px = px0; px <= px1; px++) {
                Pool p = pool(r, px, py);
                for (int i = 0; i < p.xs.length; i++) {
                    if (cellOf(p.xs[i], p.ys[i]) != cell) continue;
                    out.add(new Deposit(r, cell, FIELD_INDEX_FROM + out.size(), p.xs[i], p.ys[i], p.sites[i], p.amounts[i]));
                }
            }
        }
        return out;
    }

    /** The totals once computed (0.7.57): what every city founded on this world stores. */
    private double[] totals;

    /**
     * The world's totals, computed once and kept (0.7.57): computeTotals()'s
     * figures, a copy - what a city founded or converted on this world
     * stores, so a load needs no pass (spec-land 2.1).
     */
    public double[] totals() {
        synchronized (this) {
            if (totals == null) totals = computeTotals();
            return totals.clone();
        }
    }

    /**
     * The world's totals, a resource at a time in Resource's order: every
     * cell's cellTotal(), summed in cell order - one pass over 135,424 cells,
     * about 30 ms, the same every time. Whole tonnes, so exact.
     */
    public double[] computeTotals() {
        build();
        Resource[] kinds = Resource.values();
        double[] totals = new double[kinds.length];
        for (int c = 0; c < CELLS * CELLS; c++) {
            if (landQuarters[c] == 0) continue;
            for (Resource r : kinds) totals[r.ordinal()] += totalOf(c, r);
        }
        return totals;
    }
}
