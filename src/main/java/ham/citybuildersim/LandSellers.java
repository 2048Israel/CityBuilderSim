package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.LongAdder;

/**
 * The nation that sells each plot of a real-Earth world's ground, and an offer's unowned ground split by its sellers. A pure model of the world file's
 * owner layer and the Earth's coasts; the land office prices each part at its seller's land factor (LandFactors) and pays it to that nation (WN5).
 *
 * WHY THIS EXISTS. Every offer of land was priced by one formula for the whole world and bought from nobody. Jerus: "Borders baked in; land bought from
 * its nation" (spec-nations 6): an offer at Windsor across the Detroit River is the United States' to sell, an offer on the city's own bank Canada's, and
 * one that straddles the river is split, each part priced and paid to its own nation. The world file's owner layer (WorldData.ownerAt()) says who owns a
 * 15" cell; the plot's cell is read at the plot's own blend point, as the map and the terrain read it (EarthTerrain.landmassOfPlot()).
 *
 * THE RULES. A plot's owner is a nation (it sells), an area (Antarctica, Western Sahara, Kosovo: its seller where the file names one) or no one; an area
 * with no seller and no one are the host's to sell, the host being the nation the city stands in, by WorldMarket.Base.hostOf()'s own rule (so host() is
 * WorldMarket.host() for the same site). The territorial sea (D22, the Law of the Sea's 12 nautical miles, TERRITORIAL_KM): a sea plot owned by a foreign
 * nation is its owner's only when a land cell of that nation lies within TERRITORIAL_KM of the plot's cell; beyond, it is the host's.
 *
 * WHAT IT DOES NOT DO. The owner layer gives every sea cell of a tile the owner of its nearest owned cell (FORMAT.txt), and that is taken as "the coast's
 * nation": a sea cell the layer gave the host stays the host's even within 12 nautical miles of a foreign coast, and a sea tile with no owner at all
 * (owner 0) is the host's even next to another tile's foreign coast. It reads no border line, only cells. A generated world has no borders: on() is null.
 *
 * THE SPLIT does not read a plot it need not. The sellers an offer can touch are found from the owner cells under its latitude and longitude box
 * (a tile uniform to one owner is one read); an offer wholly one seller's is counted as the land office counts it today (CityLand.unownedCounts()), and
 * only the tiles a border or a foreign coast crosses are read plot by plot (plotsRead()).
 */
public final class LandSellers {

    /** The Law of the Sea's territorial sea (R25, D22): 12 nautical miles of 1.852 km, in kilometres. */
    public static final double TERRITORIAL_KM = 12 * 1.852;

    /** How many answers to "is there a land cell of that nation within TERRITORIAL_KM of this cell" are kept: 4,096, a few hundred kilobytes. */
    static final int REACH_KEPT = 4_096;

    /** How many whole owner tiles' answers to "do all its owners sell through one nation" are kept: 8,192 (a tile of 57,600 cells is read once). */
    static final int TILES_KEPT = 8_192;

    /** More than one seller: sellerIn()'s answer when the cells it reads sell through two nations or more, or when it cannot tell. */
    static final int MANY = -1;

    private static final int NOTHING = -2;
    private static final int CPD = WorldData.OWNER_CELLS;
    private static final long ROWS = 180L * CPD, COLS = 360L * CPD;

    private final World world;
    private final WorldData file;
    private final LandFactors factors;
    private final EarthTerrain earth;
    private final EarthProj proj;
    private final EarthData data;
    private final int n;
    private final int host;
    private final int[] areaSeller;
    private final int[] seller = new int[WorldData.MAX_OWNERS + 1];
    private final double[] nodeLat, nodeLon;
    private final double radius, cellKm;
    private final boolean coasts;
    private final LongAdder plotsRead = new LongAdder();

    private final Map<Long, Boolean> reach = new LinkedHashMap<>(256, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, Boolean> eldest) { return size() > REACH_KEPT; }
    };
    private final Map<Integer, Integer> wholeTiles = new LinkedHashMap<>(256, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) { return size() > TILES_KEPT; }
    };

    private LandSellers(World world, WorldData file, LandFactors factors) {
        this.world = world;
        this.file = file;
        this.factors = factors;
        this.earth = world.earthTerrain();
        this.proj = earth.projection();
        this.data = earth.data();
        this.n = file.nationCount();
        double r;
        try {
            r = file.dial("EARTH_RADIUS_KM");
        } catch (IllegalArgumentException e) {
            r = 6371;
        }
        this.radius = r;
        this.cellKm = radius * Math.PI / 180 / CPD;
        nodeLat = new double[n];
        nodeLon = new double[n];
        for (int i = 0; i < n; i++) {
            nodeLat[i] = file.nation(i + 1).nodeLat();
            nodeLon[i] = file.nation(i + 1).nodeLon();
        }
        areaSeller = new int[file.nations().size() + 1];
        for (int number = n + 1; number <= file.nations().size(); number++) {
            String name = file.nation(number).seller();
            int s = name == null ? 0 : file.numberOf(name);
            areaSeller[number] = s >= 1 && s <= n ? s : 0;
        }
        this.host = hostOf(world.key().lat(), world.key().lon());
        for (int o = 0; o < seller.length; o++) seller[o] = sellerOfOwner(o);
        this.coasts = earth.hasLandmasses() && data.landmassCellsPerDegree() == CPD;
    }

    /**
     * The sellers of a real-Earth world's ground, or null where the world is a generated one (it has no borders: every offer is the host's, at factor 1,
     * and the land office does as it did). `factors` is what the parts are priced by; it is handed back by factors(), and read by nothing here.
     */
    public static LandSellers on(World world, WorldData file, LandFactors factors) {
        if (world == null || file == null || !world.isEarth()) return null;
        return new LandSellers(world, file, factors);
    }

    /* =====================================================================
       THE HOST, AND WHO SELLS AN OWNER'S GROUND
       ===================================================================== */

    /** The nation the city stands in, 1-based: WorldMarket.Base.hostOf()'s rule at the world key's spot. */
    public int host() { return host; }

    /** The land factors the parts are priced by. */
    public LandFactors factors() { return factors; }

    /** The world file the owners are read from. */
    public WorldData file() { return file; }

    /** Whether these sellers are of the world a key names: the land office drops sellers whose world is not its land's (LandMarket.attach()). */
    public boolean standsOn(World.Key key) { return world.key().equals(key); }

    /** How many plots split() has read one by one since it was made: a tile it counted whole adds none. */
    public long plotsRead() { return plotsRead.sum(); }

    /** The great circle distance in km between two points (WorldMarket.Base.gc()'s). */
    private double gc(double lat1, double lon1, double lat2, double lon2) {
        double p1 = StrictMath.toRadians(lat1), p2 = StrictMath.toRadians(lat2);
        double dp = p2 - p1, dl = StrictMath.toRadians(lon2 - lon1);
        double s = StrictMath.sin(dp / 2), t = StrictMath.sin(dl / 2);
        double a = s * s + StrictMath.cos(p1) * StrictMath.cos(p2) * t * t;
        return 2 * radius * StrictMath.asin(Math.min(1, StrictMath.sqrt(a)));
    }

    /** The nation whose node is nearest a point, 1-based (WorldMarket.Base.nearest()'s, ties to the lower number). */
    private int nearest(double la, double lo) {
        int best = 0;
        double bestKm = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            double km = gc(la, lo, nodeLat[i], nodeLon[i]);
            if (km < bestKm) {
                bestKm = km;
                best = i;
            }
        }
        return best + 1;
    }

    /** The host of a site: the owner of the point when that is a nation, the nation that sells an area's land when it is one, else the nearest node (WorldMarket.Base.hostOf()'s rule). */
    private int hostOf(double la, double lo) {
        int owner;
        try {
            owner = file.ownerAt(la, lo);
        } catch (RuntimeException e) {
            owner = 0;
        }
        if (owner >= 1 && owner <= n) return owner;
        if (owner > n && owner < areaSeller.length && areaSeller[owner] > 0) return areaSeller[owner];
        return nearest(la, lo);
    }

    /** The nation that sells the ground an owner owns: a nation itself, an area its seller where the file names one, anyone else (no one, an area with none) the host. */
    public int sellerOfOwner(int owner) {
        if (owner >= 1 && owner <= n) return owner;
        if (owner > n && owner < areaSeller.length && areaSeller[owner] > 0) return areaSeller[owner];
        return host;
    }

    /* =====================================================================
       ONE PLOT'S SELLER
       ===================================================================== */

    /** The nation that sells a plot of the world, plot coordinates: its owner's seller, and the host's where a foreign nation's sea is further than TERRITORIAL_KM from that nation's land. The host off the planet. */
    public int sellerAt(long x, long y) {
        long t = World.TILE;
        EarthProj.Blend b = proj.blend(Math.floorDiv(x, t) * t, Math.floorDiv(y, t) * t, t);
        return plotSeller(b, x, y, world.terrainAt(x, y), new double[2]);
    }

    private int plotSeller(EarthProj.Blend b, long x, long y, int cover, double[] ll) {
        if (!b.latLon(x + 0.5, y + 0.5, ll)) return host;
        int s = sellerOfOwner(file.ownerAt(ll[0], ll[1]));
        if (s != host && coasts && World.salt(cover) && !coastWithin(ll[0], ll[1], s)) return host;
        return s;
    }

    /* =====================================================================
       THE TERRITORIAL SEA - a land cell of the nation within TERRITORIAL_KM
       ===================================================================== */

    private static long rowOf(double lat) {
        return Math.min(ROWS - 1, Math.max(0, (long) StrictMath.floor((90 - lat) * CPD)));
    }

    private static long colOf(double lon) {
        double wrapped = (lon + 180) % 360;
        if (wrapped < 0) wrapped += 360;
        return (long) StrictMath.floor(wrapped * CPD) % COLS;
    }

    private static double cellLat(long row) { return 90 - (row + 0.5) / CPD; }

    private static double cellLon(long col) { return (col + 0.5) / CPD - 180; }

    /** Whether the nation has a land cell (the landmass layer's) within TERRITORIAL_KM of the cell holding a point, centre to centre; kept by cell and nation. */
    private boolean coastWithin(double lat, double lon, int s) {
        long g = rowOf(lat), c = colOf(lon);
        long key = ((g * COLS + c) << 9) | s;
        synchronized (reach) {
            Boolean hit = reach.get(key);
            if (hit != null) return hit;
        }
        boolean found = searchCoast(g, c, s);
        synchronized (reach) {
            reach.put(key, found);
        }
        return found;
    }

    /** The window of cells round (g, c) that TERRITORIAL_KM can reach, read a degree tile at a time; true at the first land cell of the nation inside the radius. */
    private boolean searchCoast(long g, long c, int s) {
        double lat0 = cellLat(g), lon0 = cellLon(c);
        long rows = (long) Math.ceil(TERRITORIAL_KM / cellKm);
        long cols = Math.min(COLS / 2, (long) Math.ceil(TERRITORIAL_KM / (cellKm * Math.max(1e-3, Math.cos(Math.toRadians(lat0))))));
        long g0 = Math.max(0, g - rows), g1 = Math.min(ROWS - 1, g + rows), c0 = c - cols, c1 = c + cols;
        for (long dr = g0 / CPD; dr <= g1 / CPD; dr++) {
            long ga = Math.max(g0, dr * CPD), gb = Math.min(g1, dr * CPD + CPD - 1);
            for (long dc = Math.floorDiv(c0, CPD); dc <= Math.floorDiv(c1, CPD); dc++) {
                long ca = Math.max(c0, dc * CPD), cb = Math.min(c1, dc * CPD + CPD - 1);
                long colBase = Math.floorMod(dc, 360L) * CPD;
                int tile = WorldData.tile(89 - (int) dr, (int) Math.floorMod(dc, 360L) - 180);
                EarthData.LandmassGrid land = data.landmassGrid(tile);
                int uniform = file.ownerUniform(tile);
                byte[] owners = uniform >= 0 ? null : file.ownerTile(tile);
                for (long row = ga; row <= gb; row++) {
                    int r = (int) (row - dr * CPD);
                    for (long col = ca; col <= cb; col++) {
                        int k = (int) (col - dc * CPD);
                        if (land.id(r, k) == 0) continue;
                        int owner = owners == null ? uniform : owners[r * CPD + k] & 0xFF;
                        if (seller[owner] != s) continue;
                        if (gc(lat0, lon0, cellLat(row), cellLon(colBase + k)) <= TERRITORIAL_KM) return true;
                    }
                }
            }
        }
        return false;
    }

    /* =====================================================================
       THE SELLERS A BOX OF GROUND CAN HAVE
       ===================================================================== */

    /**
     * The one nation every owner under a box of 15" cells sells through, or MANY. The box is rows g0..g1 from the north and columns c0..c1 east from the
     * date line, c counted on (it may run below 0 or past the last column: the planet wraps). Read a degree tile at a time: a uniform tile is one owner,
     * a whole blob tile one answer kept, a part of one its bytes, stopping at the second nation.
     */
    private int sellerIn(long g0, long g1, long c0, long c1) {
        g0 = Math.max(0, g0);
        g1 = Math.min(ROWS - 1, g1);
        if (c1 - c0 + 1 >= COLS) {
            c0 = 0;
            c1 = COLS - 1;
        }
        int only = NOTHING;
        for (long dr = g0 / CPD; dr <= g1 / CPD; dr++) {
            int r0 = (int) (Math.max(g0, dr * CPD) - dr * CPD), r1 = (int) (Math.min(g1, dr * CPD + CPD - 1) - dr * CPD);
            for (long dc = Math.floorDiv(c0, CPD); dc <= Math.floorDiv(c1, CPD); dc++) {
                int k0 = (int) (Math.max(c0, dc * CPD) - dc * CPD), k1 = (int) (Math.min(c1, dc * CPD + CPD - 1) - dc * CPD);
                int tile = WorldData.tile(89 - (int) dr, (int) Math.floorMod(dc, 360L) - 180);
                int s = sellerInTile(tile, r0, r1, k0, k1);
                if (s == MANY || (only != NOTHING && only != s)) return MANY;
                only = s;
            }
        }
        return only == NOTHING ? MANY : only;
    }

    /** ...within one degree tile: rows r0..r1 by columns k0..k1 of its 240 x 240 cells. */
    private int sellerInTile(int tile, int r0, int r1, int k0, int k1) {
        int uniform = file.ownerUniform(tile);
        if (uniform >= 0) return seller[uniform];
        boolean whole = r0 == 0 && r1 == CPD - 1 && k0 == 0 && k1 == CPD - 1;
        if (whole) {
            synchronized (wholeTiles) {
                Integer hit = wholeTiles.get(tile);
                if (hit != null) return hit;
            }
        }
        byte[] owners = file.ownerTile(tile);
        int only = NOTHING;
        scan:
        for (int r = r0; r <= r1; r++) {
            for (int k = k0; k <= k1; k++) {
                int s = seller[owners[r * CPD + k] & 0xFF];
                if (only != NOTHING && only != s) {
                    only = MANY;
                    break scan;
                }
                only = s;
            }
        }
        if (whole) {
            synchronized (wholeTiles) {
                wholeTiles.put(tile, only);
            }
        }
        return only;
    }

    /** A box of latitude and longitude (longitudes counted on, from the first corner, so a box across the date line is one run), grown a cell each way for the rounding of the blend, read by sellerIn(). */
    private int sellerInBox(double latMin, double latMax, double lonMin, double lonMax) {
        long g0 = rowOf(latMax) - 1, g1 = rowOf(latMin) + 1;
        long c0 = (long) StrictMath.floor((lonMin + 180) * CPD) - 1, c1 = (long) StrictMath.floor((lonMax + 180) * CPD) + 1;
        return sellerIn(g0, g1, c0, c1);
    }

    /** What the corners of some squares of plots cover on the sphere: min latitude, max latitude, min longitude, max longitude (counted on), and whether it is good to read a box by. */
    private static final class Span {
        double latMin = Double.POSITIVE_INFINITY, latMax = Double.NEGATIVE_INFINITY, lonMin = Double.POSITIVE_INFINITY, lonMax = Double.NEGATIVE_INFINITY;
        double ref = Double.NaN;
        boolean sound = true;

        /** Adds the four corners of the square of `side` plots at (x0, y0), through its blend; unsound where the blend is exact (off the planet in part, or near a pole). */
        void add(EarthProj proj, long x0, long y0, long side) {
            EarthProj.Blend b = proj.blend(x0, y0, side);
            if (b.exact()) {
                sound = false;
                return;
            }
            double[] ll = new double[2];
            for (int corner = 0; corner < 4; corner++) {
                if (!b.latLon(x0 + (corner & 1) * side, y0 + (corner >> 1) * side, ll)) {
                    sound = false;
                    return;
                }
                double lon = ll[1];
                if (Double.isNaN(ref)) ref = lon;
                else if (lon - ref > 180) lon -= 360;
                else if (lon - ref < -180) lon += 360;
                latMin = Math.min(latMin, ll[0]);
                latMax = Math.max(latMax, ll[0]);
                lonMin = Math.min(lonMin, lon);
                lonMax = Math.max(lonMax, lon);
            }
        }

        /** Whether the span is one a box can stand for: sound, and not so wide in longitude that the unwrapping could be wrong. */
        boolean usable() { return sound && !Double.isNaN(ref) && lonMax - lonMin < 90; }
    }

    /** The one seller of a rectangle of plots, from the boxes of the tiles along its edge (the interior of a smooth projection lies inside what its edge does); MANY where it cannot tell. */
    private int sellerOfRect(long x0, long y0, long x1, long y1) {
        long t = World.TILE;
        long tx0 = Math.floorDiv(x0, t), tx1 = Math.floorDiv(x1 - 1, t), ty0 = Math.floorDiv(y0, t), ty1 = Math.floorDiv(y1 - 1, t);
        Span span = new Span();
        for (long tx = tx0; tx <= tx1; tx++) {
            span.add(proj, tx * t, ty0 * t, t);
            if (ty1 != ty0) span.add(proj, tx * t, ty1 * t, t);
        }
        for (long ty = ty0 + 1; ty < ty1; ty++) {
            span.add(proj, tx0 * t, ty * t, t);
            if (tx1 != tx0) span.add(proj, tx1 * t, ty * t, t);
        }
        return span.usable() ? sellerInBox(span.latMin, span.latMax, span.lonMin, span.lonMax) : MANY;
    }

    /** The one seller of the ground of a tile, MANY where its box crosses a border or cannot be told. */
    private int sellerOfTile(long px0, long py0) {
        Span span = new Span();
        span.add(proj, px0, py0, World.TILE);
        return span.usable() ? sellerInBox(span.latMin, span.latMax, span.lonMin, span.lonMax) : MANY;
    }

    /* =====================================================================
       THE SPLIT OF AN OFFER'S GROUND
       ===================================================================== */

    /**
     * One seller's part of an offer: the plots no holding owns that it sells, counted by class and by relief as CityLand.unownedCounts() counts them (World.CLASSES
     * of class, then RELIEF_AREAS more), and the sites and amounts of the fields standing there (CityLand.KINDS each). km2() and relief() read the counts as the
     * offer's figures; a seller's parts add up to the offer's whole.
     */
    public record Part(int seller, long[] counts, int[] sites, double[] amounts) {

        /** The five areas, in square kilometres (GridConversion.km2Of()). */
        public double[] km2() { return GridConversion.km2Of(Arrays.copyOf(counts, World.CLASSES)); }

        /** The dry ground by relief and the shallow sea, in square kilometres (CityLand.RELIEF_FLAT to RELIEF_SHALLOW). */
        public double[] relief() { return CityLand.reliefKm2(counts, World.CLASSES); }

        /** The plots the part holds: the class counts summed. */
        long plots() {
            long sum = 0;
            for (int c = 0; c < World.CLASSES; c++) sum += counts[c];
            return sum;
        }

        boolean empty() {
            if (plots() > 0) return false;
            for (int s : sites) if (s > 0) return false;
            return true;
        }
    }

    /**
     * The unowned ground of [x0, x1) x [y0, y1) and its fields, split by the nation that sells each plot: the parts by plots, most first (ties to the lower
     * nation), those with no ground and no field left out; a rectangle with no unowned ground is one empty part for the host. `seaKm2` is the rectangle's
     * unowned sea, which the caller has counted already: a rectangle every owner of which sells through one nation, the host or one with no sea in it, is
     * one part counted as today and no plot is read. Otherwise tile by tile, as Landmasses.offerSplit() reads: a tile the city owns whole is passed over, a
     * tile one nation sells (the host, or with no sea in it) is counted whole, and any other is read plot by plot. A field goes to the seller of its centre
     * plot, each site of a part field to the seller of its own plot.
     */
    public List<Part> split(CityLand land, long x0, long y0, long x1, long y1, double seaKm2) {
        x0 = Math.max(0, x0);
        y0 = Math.max(0, y0);
        x1 = Math.min(World.SIDE, x1);
        y1 = Math.min(World.SIDE, y1);
        if (x0 >= x1 || y0 >= y1) return List.of(empty(host));
        int only = sellerOfRect(x0, y0, x1, y1);
        if (only != MANY && (only == host || !(seaKm2 > 0))) {
            int[] sites = new int[CityLand.KINDS];
            double[] amounts = new double[CityLand.KINDS];
            land.fieldsOn(x0, y0, x1, y1, sites, amounts);
            Part whole = new Part(only, land.unownedCounts(x0, y0, x1, y1), sites, amounts);
            return whole.empty() ? List.of(empty(host)) : List.of(whole);
        }
        TreeMap<Integer, long[]> counts = new TreeMap<>();
        LandGrid grid = land.grid();
        int t = World.TILE;
        byte[] cover = new byte[t * t], rel = new byte[t * t];
        boolean[] owned = new boolean[t * t];
        double[] ll = new double[2];
        for (long ty = Math.floorDiv(y0, t); ty <= Math.floorDiv(y1 - 1, t); ty++) {
            for (long tx = Math.floorDiv(x0, t); tx <= Math.floorDiv(x1 - 1, t); tx++) {
                int cov = grid.cover(5, tx, ty);
                if (cov == LandGrid.ALL) continue;
                long px0 = tx * t, py0 = ty * t;
                long cx0 = Math.max(x0, px0), cy0 = Math.max(y0, py0), cx1 = Math.min(x1, px0 + t), cy1 = Math.min(y1, py0 + t);
                EarthProj.Blend b = proj.blend(px0, py0, t);
                int one = sellerOfTile(px0, py0);
                if (one != MANY && (one == host || !hasSea(tx, ty))) {
                    add(counts, one, land.unownedCounts(cx0, cy0, cx1, cy1));
                    continue;
                }
                world.tileTerrain(tx, ty, cover);
                world.tileRelief(tx, ty, rel);
                if (cov == LandGrid.SOME) grid.tileFlags(tx, ty, owned);
                else Arrays.fill(owned, false);
                plotsRead.add((long) t * t);
                for (long y = cy0; y < cy1; y++) {
                    for (long x = cx0; x < cx1; x++) {
                        int at = (int) ((y - py0) * t + (x - px0));
                        if (owned[at]) continue;
                        long[] c = counts.computeIfAbsent(plotSeller(b, x, y, cover[at], ll), k -> new long[CityLand.WIDE]);
                        CityLand.tally(cover, rel, at, c, c, World.CLASSES);
                    }
                }
            }
        }
        return withFields(land, x0, y0, x1, y1, counts);
    }

    /** Whether a whole tile of the world holds any sea (its kept count: SALT and SHALLOW). */
    private boolean hasSea(long tx, long ty) {
        long[] c = CityLand.tileCounts(world, tx, ty);
        return c[World.SALT] + c[World.SHALLOW] > 0;
    }

    private static void add(TreeMap<Integer, long[]> counts, int seller, long[] more) {
        long[] c = counts.computeIfAbsent(seller, k -> new long[CityLand.WIDE]);
        for (int i = 0; i < c.length; i++) c[i] += more[i];
    }

    /** The parts, each with the fields whose centre or site plot it sells (a field's seller is found by plot, remembered for the call), in the order split() says. */
    private List<Part> withFields(CityLand land, long x0, long y0, long x1, long y1, TreeMap<Integer, long[]> counts) {
        Map<Long, Integer> sold = new HashMap<>();
        java.util.function.LongBinaryOperator who = (x, y) -> sold.computeIfAbsent(x * World.SIDE + y, k -> sellerAt(x, y));
        TreeSet<Integer> sellers = new TreeSet<>(counts.keySet());
        land.fieldsOn(x0, y0, x1, y1, (x, y) -> {
            sellers.add((int) who.applyAsLong(x, y));
            return false;
        }, new int[CityLand.KINDS], new double[CityLand.KINDS]);
        List<Part> parts = new ArrayList<>();
        for (int s : sellers) {
            int[] sites = new int[CityLand.KINDS];
            double[] amounts = new double[CityLand.KINDS];
            land.fieldsOn(x0, y0, x1, y1, (x, y) -> who.applyAsLong(x, y) == s, sites, amounts);
            Part p = new Part(s, counts.getOrDefault(s, new long[CityLand.WIDE]), sites, amounts);
            if (!p.empty()) parts.add(p);
        }
        if (parts.isEmpty()) return List.of(empty(host));
        parts.sort((a, b) -> a.plots() != b.plots() ? Long.compare(b.plots(), a.plots()) : Integer.compare(a.seller(), b.seller()));
        return parts;
    }

    private static Part empty(int seller) {
        return new Part(seller, new long[CityLand.WIDE], new int[CityLand.KINDS], new double[CityLand.KINDS]);
    }
}
