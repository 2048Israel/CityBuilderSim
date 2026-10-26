package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The city's ground counted by landmass, and which landmasses the city's buildings may stand on: its books by landmass
 * (each landmass's owned dry ground by relief and forest, its shore, the holding it was first owned by), the linked set
 * those books are read against, and the unlinked ground a free-ground figure takes off (ET5, 2026-10-25).
 *
 * WHY THIS EXISTS. Until ET5 the city's ground was one pool of square feet with no place in it: ground owned across the
 * water was room for any building, though nothing could be carried or built there. The rule the design settled (the roads
 * study, I2) is that a city builds on its HOME landmass and on the landmasses it has linked, and on another one only a
 * port - so an island bought across a strait is owned, counted and priced as ever, but is no room for anyone until a
 * port stands there and one at home. The pool stays one pool; this class says how much of it is not room (the unlinked
 * landmasses' dry ground), and LandManager's free-ground arithmetic subtracts that behind `anyUnlinked()`, so a city on
 * one landmass - every generated world, and every Earth city whose ground lies on its home landmass - reads the old
 * expressions to the bit.
 *
 * DERIVED, NEVER SAVED. The books by landmass are a pure function of the saved rectangles and the frozen Earth data
 * (World.landmassAt()), recounted at every land change and at load (recount()): a reload is exact by construction, and
 * a later change to the rule that places a plot cannot part the model from a save. Home's own books are the remainder of
 * the land's books once the other landmasses' are taken off. The reader is gated twice, so that a city that owns nothing
 * near another landmass reads no plot at all (plotsRead() stays 0): the 1-degree tiles under its ground, and then each
 * 32-plot tile's window of cells (World.idsNear()).
 *
 * THE LINKED SET (closure()): home, every landmass a join reaches from a linked one (Joins, ET6's bridges), and - once a
 * linked landmass has a port standing - every landmass with one.
 *
 * THE PORTS, SAVED (the second half of ET5, slice B). A building is a template and a count with no place in it, so where
 * the city's sea terminals stand - on which landmass - is kept here, the only thing about the ground that is saved: per
 * port template four lists of landmass ids, oldest first (standing, on site, a stopped shell, and the units leaving by a
 * demolition on site, by the demolition's id), made only when a terminal is ordered off home or the city owns ground off
 * it. Which segment a unit is in is fitted to the buildings' own counts at every month's settle (reconcile()): a
 * completion takes the oldest unit on site to standing, a stop the newest to the shell. A port links a landmass once it
 * STANDS: a site or a shell never does, and the demolition of the one that links is refused (freeToGo()).
 *
 * @author Jerus
 */
public final class Landmasses {

    /** A landmass's name for the landmass the city stands on. */
    public static final String HOME_NAME = "Home";

    /** The plot's area in square feet, as LandManager turns the land's square kilometres into them. */
    static final double SQ_FT_PER_PLOT = World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2;

    /** The plots a margin round the owned ground's box that the 1-degree gate widens it by (it sets the cells' reach, about 124 plots, and some more): 160. */
    static final long GATE_MARGIN_PLOTS = 160;

    /** The spacing, in plots, at which the 1-degree gate samples the edges of a box for its latitudes and longitudes: 256. */
    static final long GATE_STEP_PLOTS = 256;

    /** The degrees the 1-degree gate widens a box's latitudes and longitudes by: 0.05 (twelve cells, past EarthTerrain.LANDMASS_REACH). */
    static final double GATE_MARGIN_DEG = 0.05;

    /** The most 1-degree tiles the gate looks through; a box over more is read by the tiles. */
    static final int GATE_TILES_MOST = 100;

    /** The latitude past which the gate gives up (a box near a pole is not a box in latitude and longitude). */
    static final double GATE_LAT_MOST = 85;

    /** The tiles whose window of cells (World.idsNear()) is remembered before the memory is cleared: 100,000 (about 8 MB). */
    static final int NEAR_KEPT = 100_000;

    /* =====================================================================
       THE JOINS AND THE CLOSURE
       ===================================================================== */

    /**
     * Which landmasses a landmass joins, other than by a port: nothing in ET5, and since ET6 the standing long bridges
     * (Bridges), and any ferry when it comes. A join is read both ways by closure(): a landmass joined to a linked one is linked.
     */
    public interface Joins {
        /** The landmasses this one is joined to (the ids it can reach in one join); empty for none. */
        int[] joinedTo(int id);

        /** No joins: the closure is home and the landmasses ports link. */
        Joins NONE = id -> new int[0];
    }

    /**
     * The landmasses the city's buildings may stand on, home included, ids ascending (ET5 D10): home; every landmass a join
     * reaches from one already in; and, when some landmass in has a port standing, every landmass with one - two ports on
     * two landmasses are the sea route between them (the roads study, I2), but one alone links nothing. Repeated until it
     * stops growing. `standingPorts` maps a landmass to the ports standing on it (those not standing are not in it).
     */
    public static SortedSet<Integer> closure(int home, SortedMap<Integer, Integer> standingPorts, Joins joins) {
        TreeSet<Integer> linked = new TreeSet<>();
        linked.add(home);
        Joins j = joins == null ? Joins.NONE : joins;
        boolean grew = true;
        while (grew) {
            grew = false;
            for (int id : new ArrayList<>(linked)) {
                for (int to : j.joinedTo(id)) grew |= linked.add(to);
            }
            boolean anyPort = false;
            for (int id : linked) anyPort |= standingPorts != null && standingPorts.getOrDefault(id, 0) > 0;
            if (anyPort) {
                for (Map.Entry<Integer, Integer> e : standingPorts.entrySet()) if (e.getValue() > 0) grew |= linked.add(e.getKey());
            }
        }
        return linked;
    }

    /* =====================================================================
       THE BOOKS BY LANDMASS
       ===================================================================== */

    /** One landmass's owned ground, counted in plots. */
    private static final class Books {
        final int id;
        /** Its dry plots by relief (World.FLAT to World.VERY_STEEP). */
        final long[] relief = new long[World.RELIEFS];
        /** Its forest plots (part of its dry ground). */
        long forest;
        /** Its shore: owned flat or foothill dry plots 4-adjacent to owned salt (shorePlots()). */
        long shore;
        /** The lowest holding that owns a dry plot of it (0 the centre, k the k-th purchase). */
        int firstHolding = Integer.MAX_VALUE;
        /** The data's name for it, "" for none. */
        String dataName = "";

        Books(int id) { this.id = id; }

        long dry() {
            long d = 0;
            for (long r : relief) d += r;
            return d;
        }
    }

    /** One landmass as a table lists it: what the city owns of it, in plots. */
    public record Row(int id, String name, boolean home, boolean linked, long dryPlots, long flatPlots, long foothillPlots,
                      long steepPlots, long verySteepPlots, long forestPlots, long shorePlots, int firstHolding) {

        /** Its dry ground in square feet. */
        public double drySqFt() { return dryPlots * SQ_FT_PER_PLOT; }

        /** Its flat and foothill ground, the ground a building may stand on, in square feet. */
        public double buildableSqFt() { return (flatPlots + foothillPlots) * SQ_FT_PER_PLOT; }
    }

    private int home;
    private boolean active;
    /** The landmasses off home that the city owns dry ground on, by id; home's are `homeBooks`. */
    private final TreeMap<Integer, Books> books = new TreeMap<>();
    private Books homeBooks = new Books(0);
    private long plotsRead;
    private int version;
    private SortedSet<Integer> linkedSet = new TreeSet<>();
    private final TreeMap<Integer, String> names = new TreeMap<>();

    /** The ids each tile's window of cells holds (World.idsNear()), remembered for the world they were read on: the data is frozen per world, so what a tile can reach never changes. Filled by recount() and offerSplit(), never by a read. */
    private final java.util.HashMap<Long, int[]> nearKept = new java.util.HashMap<>();
    private World.Key nearKey;

    /** The shore of home's tiles that cannot change any more (the tile and its four neighbours wholly owned), by tile key, kept for the land and the world they were counted on: the next recount adds it without reading the tile. */
    private final java.util.HashMap<Long, Long> shoreSettled = new java.util.HashMap<>();
    private CityLand shoreFor;
    private World.Key shoreKey;

    /** What Landmasses reads of the buildings: Game wires it; a bare one has none and no port is placed. */
    private Stock stock;
    /** The joins other than a port, read by every closure (ET6: the standing long bridges, LandManager wires them); none for a bare one. */
    private Joins joins = Joins.NONE;
    /** Where each port template's units stand, by template id: only for a template with a unit off home (or on a city that owns ground off home). */
    private final TreeMap<Integer, Segments> ports = new TreeMap<>();
    /** The units a demolition on site is taking down, by the demolition's id. */
    private final TreeMap<Integer, Leave> leaving = new TreeMap<>();
    /** The landmass the card chose for a template's next order (ONE order's: cleared by it and at the month's settle); transient, never saved. */
    private final TreeMap<Integer, Integer> chosen = new TreeMap<>();

    /* The unlinked ground, square feet, worked out by refresh(): what LandManager's free-ground arithmetic takes off. */
    private double unlinkedDry, unlinkedForest;
    private final double[] unlinkedRelief = new double[World.RELIEFS];
    private double unlinkedPorts;
    private final List<Integer> unlinkedIds = new ArrayList<>();

    /** A bare one: no landmass, home 0, every plot home's and linked. */
    public Landmasses() {
        linkedSet.add(0);
    }

    /** Back to a bare one (LandManager.reset(): a new city): nothing counted, nothing unlinked. */
    void reset() {
        books.clear();
        names.clear();
        homeBooks = new Books(0);
        home = 0;
        active = false;
        plotsRead = 0;
        version = 0;
        linkedSet = new TreeSet<>();
        linkedSet.add(0);
        nearKept.clear();
        nearKey = null;
        shoreSettled.clear();
        shoreFor = null;
        shoreKey = null;
        unlinkedIds.clear();
        unlinkedDry = 0;
        unlinkedForest = 0;
        java.util.Arrays.fill(unlinkedRelief, 0);
        unlinkedPorts = 0;
        ports.clear();
        leaving.clear();
        chosen.clear();
    }

    /* =====================================================================
       RECOUNT - the books, from the grid and the frozen data
       ===================================================================== */

    /**
     * Counts the city's ground by landmass again (derived, ET5): home is the landmass at the land's site plot; the owned
     * ground is read where it can lie on another landmass, and home's books are what is left of the land's. Nothing is
     * read on a world with no landmasses (a generated world, an earth-1 file), nor when no 1-degree tile under the ground
     * (widened) holds a landmass but home's, nor for a 32-plot tile whose window of cells holds none but home's - so a city
     * that owns nothing near another landmass reads no plot at all (plotsRead()). Call refresh() after.
     */
    void recount(World world, CityLand land) {
        books.clear();
        names.clear();
        homeBooks = new Books(0);
        active = world != null && land != null && world.hasLandmasses();
        home = 0;
        if (land == null) return;
        if (active) {
            home = world.landmassAt(land.siteX(), land.siteY());
            LandGrid grid = land.grid();
            if (grid.ownedPlots() > 0 && !onlyHome(world, grid.minX(), grid.minY(), grid.maxX(), grid.maxY())) readOwned(world, land, grid);
        }
        // Home's books are the land's, less the other landmasses' (the relief rows add up to the dry ground: CityLand's ET1 books).
        long otherForest = 0;
        long[] otherRelief = new long[World.RELIEFS];
        for (Books b : books.values()) {
            otherForest += b.forest;
            for (int r = 0; r < World.RELIEFS; r++) otherRelief[r] += b.relief[r];
        }
        for (int r = 0; r < World.RELIEFS; r++) homeBooks.relief[r] = Math.max(0, plots(land.totalReliefKm2(r)) - otherRelief[r]);
        homeBooks.forest = Math.max(0, plots(land.totalKm2(CityLand.FOREST)) - otherForest);
        homeBooks.firstHolding = 0;
        nameThem(world);
    }

    /** A land area in square kilometres as a whole number of plots. */
    private static long plots(double km2) {
        return Math.max(0, Math.round(km2 / World.KM2_PER_PLOT));
    }

    /** Names the landmasses off home: the data's name as the founding card writes it ("Charlottetown's"), else "Island N", N counting from 2 in the order the city first owned them (the lowest holding, then the lowest id). */
    private void nameThem(World world) {
        List<Books> order = new ArrayList<>(books.values());
        order.sort((a, b) -> a.firstHolding != b.firstHolding ? Integer.compare(a.firstHolding, b.firstHolding) : Integer.compare(a.id, b.id));
        EarthData data = active ? world.earthTerrain().data() : null;
        int n = 2;
        for (Books b : order) {
            EarthData.Landmass l = data == null ? null : data.landmass(b.id);
            b.dataName = l == null ? "" : l.name().trim();
            names.put(b.id, b.dataName.isEmpty() ? "Island " + n : b.dataName + "'s");
            n++;
        }
    }

    /**
     * Whether no landmass but home's (and none at all, 0) can lie under the box of plots [x0, x1) x [y0, y1) or within
     * the cells' reach of it: the 1-degree tiles under the box, widened, hold no other id. False when it cannot say
     * (a box that reaches the planet's edge or a pole, or spreads over more than GATE_TILES_MOST tiles).
     */
    boolean onlyHome(World world, long x0, long y0, long x1, long y1) {
        EarthTerrain e = world.earthTerrain();
        EarthProj proj = e.projection();
        EarthData data = e.data();
        long ax = x0 - GATE_MARGIN_PLOTS, ay = y0 - GATE_MARGIN_PLOTS, bx = x1 + GATE_MARGIN_PLOTS, by = y1 + GATE_MARGIN_PLOTS;
        double[] ll = new double[2];
        double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY, minLon = Double.POSITIVE_INFINITY, maxLon = Double.NEGATIVE_INFINITY;
        double refLon = Double.NaN;
        for (long x = ax; x <= bx; x = x == bx ? bx + 1 : Math.min(bx, x + GATE_STEP_PLOTS)) {
            for (int edge = 0; edge < 2; edge++) {
                if (!proj.latLonOfPlot(x, edge == 0 ? ay : by, ll)) return false;
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
        for (long y = ay; y <= by; y = y == by ? by + 1 : Math.min(by, y + GATE_STEP_PLOTS)) {
            for (int edge = 0; edge < 2; edge++) {
                if (!proj.latLonOfPlot(edge == 0 ? ax : bx, y, ll)) return false;
                double lon = ll[1];
                if (lon - refLon > 180) lon -= 360;
                else if (lon - refLon < -180) lon += 360;
                minLat = Math.min(minLat, ll[0]);
                maxLat = Math.max(maxLat, ll[0]);
                minLon = Math.min(minLon, lon);
                maxLon = Math.max(maxLon, lon);
            }
        }
        if (maxLat > GATE_LAT_MOST || minLat < -GATE_LAT_MOST || maxLon - minLon > 90) return false;
        int lat0 = (int) Math.floor(minLat - GATE_MARGIN_DEG), lat1 = (int) Math.floor(maxLat + GATE_MARGIN_DEG);
        int lon0 = (int) Math.floor(minLon - GATE_MARGIN_DEG), lon1 = (int) Math.floor(maxLon + GATE_MARGIN_DEG);
        if ((long) (lat1 - lat0 + 1) * (lon1 - lon0 + 1) > GATE_TILES_MOST) return false;
        for (int la = lat0; la <= lat1; la++) {
            for (int lo = lon0; lo <= lon1; lo++) {
                int wrapped = Math.floorMod(lo + 180, 360) - 180;
                for (int id : data.landmassIds(EarthData.tile(Math.max(-90, Math.min(89, la)), wrapped))) {
                    if (id != 0 && id != home) return false;
                }
            }
        }
        return true;
    }

    /** Whether every landmass a tile's plots can take is home's (or none): then its plots all read home. */
    private boolean homeOnly(int[] near) {
        for (int id : near) if (id != home) return false;
        return true;
    }

    /** The ids a tile's window of cells holds (World.idsNear()), from the memory of this world's tiles or read once and remembered. */
    private int[] near(World world, long tx, long ty) {
        if (!world.key().equals(nearKey)) {
            nearKept.clear();
            nearKey = world.key();
        }
        long k = (tx << 32) ^ (ty & 0xffffffffL);
        int[] got = nearKept.get(k);
        if (got == null) {
            got = world.idsNear(tx, ty);
            if (nearKept.size() >= NEAR_KEPT) nearKept.clear();
            nearKept.put(k, got);
        }
        return got;
    }

    /** The 32-plot tiles the land owns some of, sorted, each once: as keys (tx << 32 | ty & 0xffffffff), from the grid's leaves. */
    private static long[] ownedTiles(LandGrid grid) {
        TreeSet<Long> tiles = new TreeSet<>();
        grid.leaves((level, x, y, holding) -> {
            long size = 1L << level;
            long tx0 = Math.floorDiv(x, World.TILE), tx1 = Math.floorDiv(x + size - 1, World.TILE);
            long ty0 = Math.floorDiv(y, World.TILE), ty1 = Math.floorDiv(y + size - 1, World.TILE);
            for (long ty = ty0; ty <= ty1; ty++) for (long tx = tx0; tx <= tx1; tx++) tiles.add((tx << 32) ^ (ty & 0xffffffffL));
        });
        long[] out = new long[tiles.size()];
        int i = 0;
        for (long t : tiles) out[i++] = t;
        return out;
    }

    private static long tileX(long key) { return key >> 32; }

    private static long tileY(long key) { return (int) key; }

    /**
     * Reads the owned ground that can lie off home: each owned tile whose window of cells holds another landmass is read
     * plot by plot (its landmass, class, relief, holding), counting the dry plots of every landmass but home's. Then, if
     * the city owns ground off home, the shore of every landmass - home's too - over all its tiles (D10: once there are
     * two landmasses, home's coast test is the shore count).
     */
    private void readOwned(World world, CityLand land, LandGrid grid) {
        long[] tiles = ownedTiles(grid);
        int n = World.TILE * World.TILE;
        byte[] cover = new byte[n], rel = new byte[n];
        int[] ids = new int[n], held = new int[n];
        boolean[] read = new boolean[tiles.length];
        for (int k = 0; k < tiles.length; k++) {
            long tx = tileX(tiles[k]), ty = tileY(tiles[k]);
            if (homeOnly(near(world, tx, ty))) continue;
            read[k] = true;
            load(world, grid, tx, ty, cover, rel, ids, held);
            tally(grid, world, tx, ty, cover, rel, ids, held, true);
        }
        if (books.isEmpty()) {
            // Nothing owned off home: one landmass, whose coast test stays the old one; the shore of the tiles read is not kept.
            homeBooks.shore = 0;
            return;
        }
        // The shore of the tiles not read yet: only a tile with salt water in it, or beside a tile that has some, can hold any - found from the
        // tiles' counts by class, which the land keeps (CityLand.tileCounts()), so an inland tile is not drawn again for it.
        // Those tiles' plots are all home's (their window of cells holds no other landmass), so no landmass is read for them, and the relief only
        // where the tile has steep ground; and a tile whose four neighbours are as wholly owned as it is has a shore that can never change
        // (ownership only grows), kept for the next recount.
        if (shoreFor != land || !world.key().equals(shoreKey)) {
            shoreSettled.clear();
            shoreFor = land;
            shoreKey = world.key();
        }
        boolean[] salty = new boolean[tiles.length];
        long[][] counts = new long[tiles.length][];
        for (int k = 0; k < tiles.length; k++) {
            counts[k] = CityLand.tileCounts(world, tileX(tiles[k]), tileY(tiles[k]));
            salty[k] = counts[k][World.SALT] + counts[k][World.SHALLOW] > 0;
        }
        java.util.Arrays.fill(ids, 0);
        for (int k = 0; k < tiles.length; k++) {
            if (read[k]) continue;
            long tx = tileX(tiles[k]), ty = tileY(tiles[k]);
            if (!(salty[k] || saltyBeside(tiles, salty, tx, ty))) continue;
            boolean settled = grid.cover(5, tx, ty) == LandGrid.ALL && grid.cover(5, tx - 1, ty) == LandGrid.ALL && grid.cover(5, tx + 1, ty) == LandGrid.ALL
                    && grid.cover(5, tx, ty - 1) == LandGrid.ALL && grid.cover(5, tx, ty + 1) == LandGrid.ALL;
            Long kept = settled ? shoreSettled.get(tiles[k]) : null;
            if (kept != null) {
                homeBooks.shore += kept;
                continue;
            }
            long before = homeBooks.shore;
            world.tileTerrain(tx, ty, cover);
            if (counts[k][World.CLASSES + CityLand.RELIEF_STEEP] + counts[k][World.CLASSES + CityLand.RELIEF_VERY_STEEP] > 0) world.tileRelief(tx, ty, rel);
            else java.util.Arrays.fill(rel, (byte) 0);
            grid.tileHoldings(tx, ty, held);
            plotsRead += World.TILE * World.TILE;
            tally(grid, world, tx, ty, cover, rel, ids, held, false);
            if (settled) {
                if (shoreSettled.size() >= NEAR_KEPT) shoreSettled.clear();
                shoreSettled.put(tiles[k], homeBooks.shore - before);
            }
        }
    }

    /** Whether one of the four tiles round (tx, ty) is among the owned tiles and has salt water in it. */
    private static boolean saltyBeside(long[] tiles, boolean[] salty, long tx, long ty) {
        long[][] around = { { tx - 1, ty }, { tx + 1, ty }, { tx, ty - 1 }, { tx, ty + 1 } };
        for (long[] t : around) {
            int at = java.util.Arrays.binarySearch(tiles, (t[0] << 32) ^ (t[1] & 0xffffffffL));
            if (at >= 0 && salty[at]) return true;
        }
        return false;
    }

    /** Reads one tile's plots: cover, relief, landmass and holding. */
    private void load(World world, LandGrid grid, long tx, long ty, byte[] cover, byte[] rel, int[] ids, int[] held) {
        world.tileTerrain(tx, ty, cover);
        world.tileRelief(tx, ty, rel);
        world.tileLandmass(tx, ty, ids);
        grid.tileHoldings(tx, ty, held);
        plotsRead += World.TILE * World.TILE;
    }

    /**
     * Adds a tile's owned ground to the books: the dry plots of each landmass off home (when `dry`: the tile may lie across
     * landmasses) and the shore plots of every landmass. A plot's 0 is home's.
     */
    private void tally(LandGrid grid, World world, long tx, long ty, byte[] cover, byte[] rel, int[] ids, int[] held, boolean dry) {
        int t = World.TILE;
        for (int i = 0; i < t * t; i++) {
            if (held[i] < 0 || !World.dry(cover[i])) continue;
            int id = ids[i] == 0 ? home : ids[i];
            if (dry && id != home) {
                Books b = books.computeIfAbsent(id, Books::new);
                b.relief[rel[i]]++;
                if (cover[i] == World.FOREST) b.forest++;
                if (held[i] < b.firstHolding) b.firstHolding = held[i];
            }
        }
        // The shore: an owned flat or foothill dry plot with an owned salt plot beside it (the map's shoreSpot premise).
        // ...owned salt in the tile, or owned salt across one of its edges beside an owned plot there (a shore plot at the tile's edge).
        boolean salt = false;
        for (int i = 0; i < t * t && !salt; i++) salt = held[i] >= 0 && World.salt(cover[i]);
        if (!salt) salt = saltAcrossEdge(grid, world, tx, ty, held);
        if (!salt) return;
        for (int py = 0; py < t; py++) {
            for (int px = 0; px < t; px++) {
                int i = py * t + px;
                if (held[i] < 0 || !World.dry(cover[i]) || rel[i] > World.FOOTHILL) continue;
                boolean beside = (px > 0 ? ownedSalt(held[i - 1], cover[i - 1]) : edgeSalt(grid, world, tx * t + px - 1, ty * t + py))
                        || (px < t - 1 ? ownedSalt(held[i + 1], cover[i + 1]) : edgeSalt(grid, world, tx * t + px + 1, ty * t + py))
                        || (py > 0 ? ownedSalt(held[i - t], cover[i - t]) : edgeSalt(grid, world, tx * t + px, ty * t + py - 1))
                        || (py < t - 1 ? ownedSalt(held[i + t], cover[i + t]) : edgeSalt(grid, world, tx * t + px, ty * t + py + 1));
                if (!beside) continue;
                int id = ids[i] == 0 ? home : ids[i];
                if (id == home) homeBooks.shore++;
                else books.computeIfAbsent(id, Books::new).shore++;
            }
        }
    }

    private static boolean ownedSalt(int holding, byte cover) {
        return holding >= 0 && World.salt(cover);
    }

    /** Whether any owned plot on the edge of the tile (tx, ty) has owned salt water on the other side of the edge, in the tile beside. */
    private static boolean saltAcrossEdge(LandGrid grid, World world, long tx, long ty, int[] held) {
        int t = World.TILE;
        for (int k = 0; k < t; k++) {
            if ((held[k * t] >= 0 && edgeSalt(grid, world, tx * t - 1, ty * t + k))
                    || (held[k * t + t - 1] >= 0 && edgeSalt(grid, world, tx * t + t, ty * t + k))
                    || (held[k] >= 0 && edgeSalt(grid, world, tx * t + k, ty * t - 1))
                    || (held[(t - 1) * t + k] >= 0 && edgeSalt(grid, world, tx * t + k, ty * t + t))) return true;
        }
        return false;
    }

    /** Whether a plot outside the tile being read is owned salt. */
    private static boolean edgeSalt(LandGrid grid, World world, long x, long y) {
        return grid.owner(x, y) >= 0 && World.salt(world.terrainAt(x, y));
    }

    /* =====================================================================
       OFFERS - the same split of the ground a purchase would add
       ===================================================================== */

    /**
     * Splits an offer's ground by linked and unlinked landmass (derived, ET5): of its unowned plots, the dry ones on a
     * landmass the city has not linked, in square kilometres (LandParcel.getUnlinkedDryKm2()), those of them a building
     * could stand on (LandParcel.getUnlinkedBuildableKm2(): its flat and foothill, or all of it where the offer is all
     * flat), and the landmass with most of it. Zero for an offer of home's ground and on a world with no landmasses; the
     * same gates as recount(). Run again for every offer at every land change and link change (LandManager.landChanged()),
     * so an offer in memory reads as it does after a load.
     */
    void offerSplit(World world, CityLand land, LandParcel p) {
        p.setUnlinked(0, 0, 0);
        if (!active || world == null || land == null || !world.hasLandmasses()) return;
        long x0 = Math.max(0, p.getX0()), y0 = Math.max(0, p.getY0()), x1 = Math.min(World.SIDE, p.getX1()), y1 = Math.min(World.SIDE, p.getY1());
        if (x0 >= x1 || y0 >= y1 || onlyHome(world, x0, y0, x1, y1)) return;
        LandGrid grid = land.grid();
        int t = World.TILE, n = t * t;
        byte[] cover = new byte[n], rel = new byte[n];
        int[] ids = new int[n];
        boolean[] owned = new boolean[n];
        TreeMap<Integer, long[]> counts = new TreeMap<>();
        for (long ty = Math.floorDiv(y0, t); ty <= Math.floorDiv(y1 - 1, t); ty++) {
            for (long tx = Math.floorDiv(x0, t); tx <= Math.floorDiv(x1 - 1, t); tx++) {
                int cov = grid.cover(5, tx, ty);
                if (cov == LandGrid.ALL || homeOnly(near(world, tx, ty))) continue;
                world.tileTerrain(tx, ty, cover);
                world.tileRelief(tx, ty, rel);
                world.tileLandmass(tx, ty, ids);
                if (cov == LandGrid.SOME) grid.tileFlags(tx, ty, owned);
                else java.util.Arrays.fill(owned, false);
                plotsRead += n;
                int i0 = (int) (Math.max(x0, tx * t) - tx * t), i1 = (int) (Math.min(x1, tx * t + t) - tx * t);
                int j0 = (int) (Math.max(y0, ty * t) - ty * t), j1 = (int) (Math.min(y1, ty * t + t) - ty * t);
                for (int j = j0; j < j1; j++) {
                    for (int i = i0; i < i1; i++) {
                        int at = j * t + i;
                        if (owned[at] || !World.dry(cover[at])) continue;
                        int id = ids[at] == 0 ? home : ids[at];
                        if (linked(id)) continue;
                        counts.computeIfAbsent(id, k -> new long[World.RELIEFS])[rel[at]]++;
                    }
                }
            }
        }
        long dry = 0, buildable = 0, most = 0;
        int largest = 0;
        boolean flat = LandMarket.allFlat(p);
        for (Map.Entry<Integer, long[]> e : counts.entrySet()) {
            long d = 0;
            for (long r : e.getValue()) d += r;
            dry += d;
            buildable += flat ? d : e.getValue()[World.FLAT] + e.getValue()[World.FOOTHILL];
            if (d > most) {
                most = d;
                largest = e.getKey();
            }
        }
        p.setUnlinked(dry * World.KM2_PER_PLOT, buildable * World.KM2_PER_PLOT, largest);
    }

    /* =====================================================================
       REFRESH - the linked set and the unlinked sums
       ===================================================================== */

    /**
     * Works out the linked set and the unlinked ground again, eagerly - after a recount, and after anything that moves a
     * port (never in a getter: ReadPathCheck holds that no read fills a cache). version() rises when the linked set moved.
     */
    void refresh() {
        SortedSet<Integer> now = closure(home, standingPorts(), joins);
        if (!now.equals(linkedSet)) {
            linkedSet = now;
            version++;
        }
        unlinkedIds.clear();
        unlinkedDry = 0;
        unlinkedForest = 0;
        java.util.Arrays.fill(unlinkedRelief, 0);
        unlinkedPorts = 0;
        for (Books b : books.values()) {
            if (linked(b.id) || b.dry() == 0) continue;
            unlinkedIds.add(b.id);
            unlinkedDry += b.dry() * SQ_FT_PER_PLOT;
            unlinkedForest += b.forest * SQ_FT_PER_PLOT;
            for (int r = 0; r < World.RELIEFS; r++) unlinkedRelief[r] += b.relief[r] * SQ_FT_PER_PLOT;
            // ...the ground the ports on it stand on is part of the allocated ground and of what it holds (LandManager's linked view takes both off).
            unlinkedPorts += portsGroundSqFt(b.id);
        }
    }

    /* =====================================================================
       THE PORTS - where each terminal stands, and what links the landmasses
       (ET5, slice B, 2026-10-25)

       A sea terminal is ordered ON a landmass: home's, one the city has
       linked, or - the only building that may - an island no port links yet,
       where it takes that island's own room (portRoomSqFt()) and not the
       linked pool's. Where it stands is the one fact about the ground that
       is saved, as four lists per terminal template (see the class header),
       kept only once a unit is off home. A port links once it STANDS; the
       closure() of the standing ports is the linked set, refreshed eagerly
       after every event here (never in a getter). The card's choice of a
       landmass is one order's, transient, and falls back to the rule.
       ===================================================================== */

    /** portLandmass()'s `asked`: the card's choice if there is one and it will do, else the rule. */
    public static final int CHOSEN = -1;

    /** ...the rule alone: the automatic builder and the advice never link an island by choosing it (ET5 D10). */
    public static final int RULE = -2;

    /** portLandmass()'s answer when no landmass will do: the order is NO_COAST. */
    public static final int NO_LANDMASS = -3;

    /** What Landmasses reads of the buildings: Game wires it; a bare one has none, and then no port is placed and every one reads as home. */
    public interface Stock {
        /** The ids of the sea terminals' templates. */
        int[] portTemplates();

        /** Whether the template is one of them (a saved row of any other is passed over). */
        boolean known(int template);

        /** The finished terminals of a template that no demolition has been ordered for. */
        int standing(int template);

        /** ...those under construction. */
        int onSite(int template);

        /** ...those a stopped order left standing as a shell. */
        int shell(int template);

        /** The demolitions on site of a template, each as {the demolition's id, its buildings}. */
        List<int[]> demolitions(int template);

        /** The land one terminal of the template takes, square feet. */
        double landSqFt(int template);

        /** The template's cargo, as Ports.Cargo's ordinal. */
        int cargo(int template);
    }

    /** One template's units by where they stand, each list the landmass ids of its units, oldest first. */
    private static final class Segments {
        final List<Integer> standing = new ArrayList<>(), onSite = new ArrayList<>(), shell = new ArrayList<>();
    }

    /** The units a demolition on site is taking down: the landmass ids, oldest first. */
    private static final class Leave {
        final int demolition, template;
        final List<Integer> landmasses = new ArrayList<>();

        Leave(int demolition, int template) {
            this.demolition = demolition;
            this.template = template;
        }
    }

    /** One template's row as saved: the landmass ids of its standing, on-site and shell units, oldest first. */
    public static final class PortRow {
        public int template;
        public int[] standing, onSite, shell;
        public PortRow() { }
    }

    /** A demolition on site as saved: the landmass ids of the units it is taking down. */
    public static final class Leaving {
        public int demolition;
        public int template;
        public int[] landmasses;
        public Leaving() { }
    }

    /**
     * DataSave's landmasses (ET5): where the terminals stand, for a city with one off its home landmass. Absent on an
     * older save and on every city with no port off its home landmass: every port at home.
     */
    public static final class State {
        public List<PortRow> ports = new ArrayList<>();
        public List<Leaving> leaving = new ArrayList<>();
        public State() { }
    }

    /** Wires the buildings' counts (Game, once). */
    void setStock(Stock stock) { this.stock = stock; }

    /** Wires the joins the closure reads besides the ports (ET6: LandManager's Bridges, once); null, none. */
    void setJoins(Joins joins) { this.joins = joins == null ? Joins.NONE : joins; }

    /** Whether any terminal has a place of its own: a row, or a demolition taking some down. */
    public boolean hasPlacements() { return !ports.isEmpty() || !leaving.isEmpty(); }

    private static void fill(List<Integer> list, int id, int n) {
        for (int i = 0; i < n; i++) list.add(id);
    }

    private static int count(List<Integer> list, int id) {
        int n = 0;
        for (int v : list) if (v == id) n++;
        return n;
    }

    private boolean allHome(List<Integer> list) {
        for (int v : list) if (v != home && v != 0) return false;
        return true;
    }

    /** Fits a list to n units: short, home at the oldest end; long, the newest dropped. */
    private void fit(List<Integer> list, int n) {
        while (list.size() > n) list.remove(list.size() - 1);
        while (list.size() < n) list.add(0, home);
    }

    /** An order of n terminals of a template was placed on a landmass, after the stack took it (`instant`: built at once, no construction). Makes the template's row if it had none. */
    void ordered(int template, int landmass, int n, boolean instant) {
        if (stock == null || n <= 0 || !stock.known(template)) return;
        Segments s = ports.get(template);
        // A template with no row stands at home: an order at home needs none made.
        if (s == null && (landmass == home || landmass == 0)) return;
        if (s == null) {
            s = new Segments();
            fill(s.standing, home, Math.max(0, stock.standing(template) - (instant ? n : 0)));
            fill(s.onSite, home, Math.max(0, stock.onSite(template) - (instant ? 0 : n)));
            fill(s.shell, home, Math.max(0, stock.shell(template)));
            ports.put(template, s);
        }
        fill(instant ? s.standing : s.onSite, landmass == 0 ? home : landmass, n);
    }

    /** A demolition of n of a template's standing units was ordered (id `demolition`), the units' count already off standing: the n newest whose loss unlinks nothing go to leaving. Nothing to do for a template with no row (all home). */
    void demolishing(int template, int n, int demolition) {
        Segments s = ports.get(template);
        if (s == null || n <= 0) return;
        java.util.TreeSet<Integer> take = new java.util.TreeSet<>();
        for (int i : removable(template, true)) if (take.size() < n) take.add(i);
        for (int i = s.standing.size() - 1; i >= 0 && take.size() < n; i--) take.add(i);
        Leave l = new Leave(demolition, template);
        for (int i : take) l.landmasses.add(s.standing.get(i));
        for (int i : take.descendingSet()) s.standing.remove(i);
        leaving.put(demolition, l);
    }

    /** A template's shell was ordered demolished (id `demolition`): its units go to leaving. */
    void shellDemolishing(int template, int demolition) {
        Segments s = ports.get(template);
        if (s == null || s.shell.isEmpty()) return;
        Leave l = new Leave(demolition, template);
        l.landmasses.addAll(s.shell);
        s.shell.clear();
        leaving.put(demolition, l);
    }

    /** A template's shell was put back on site: its units go to the oldest end of on site (they were ordered first). */
    void restarted(int template) {
        Segments s = ports.get(template);
        if (s == null || s.shell.isEmpty()) return;
        s.onSite.addAll(0, s.shell);
        s.shell.clear();
    }

    /** A demolition finished: its units no longer hold their ground. */
    void demolished(int demolition) {
        leaving.remove(demolition);
    }

    /**
     * Fits every template's lists to the buildings' own counts (at the month's settle and at a load), then works the linked
     * set out again: a completion has taken the oldest unit on site to standing, a stop the newest to the shell; a list
     * short of its count is made up with home at its oldest end, one over it loses its newest; a demolition no longer on
     * site is forgotten; a row all home is dropped (an older save and a city that never ordered off home have none).
     */
    void reconcile() {
        if (stock != null) {
            for (java.util.Iterator<Map.Entry<Integer, Segments>> it = ports.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, Segments> e = it.next();
                if (!stock.known(e.getKey())) { it.remove(); continue; }
                fitRow(e.getKey(), e.getValue());
                if (allHome(e.getValue().standing) && allHome(e.getValue().onSite) && allHome(e.getValue().shell)) it.remove();
            }
            for (java.util.Iterator<Leave> it = leaving.values().iterator(); it.hasNext(); ) {
                Leave l = it.next();
                int buildings = -1;
                if (stock.known(l.template)) for (int[] d : stock.demolitions(l.template)) if (d[0] == l.demolition) buildings = d[1];
                if (buildings < 0) { it.remove(); continue; }
                fit(l.landmasses, buildings);
                if (allHome(l.landmasses)) it.remove();
            }
        }
        if (hasPlacements() || !single()) refresh();
    }

    private void fitRow(int template, Segments s) {
        int standing = Math.max(0, stock.standing(template)), onSite = Math.max(0, stock.onSite(template)), shell = Math.max(0, stock.shell(template));
        // The completions: the oldest units on site have finished.
        int done = Math.min(Math.max(0, standing - s.standing.size()), s.onSite.size());
        for (int k = 0; k < done; k++) s.standing.add(s.onSite.remove(0));
        // The stops: the newest units on site past the count have been stopped, to the shell.
        int stopped = Math.min(Math.max(0, s.onSite.size() - onSite), Math.max(0, shell - s.shell.size()));
        if (stopped > 0) {
            List<Integer> moved = new ArrayList<>(s.onSite.subList(s.onSite.size() - stopped, s.onSite.size()));
            for (int k = 0; k < stopped; k++) s.onSite.remove(s.onSite.size() - 1);
            s.shell.addAll(moved);
        }
        fit(s.standing, standing);
        fit(s.onSite, onSite);
        fit(s.shell, shell);
    }

    /** The month's settle (Game.settleConstructionControl()): the choices are forgotten (one order's), the lists fitted. Whether the linked set moved. */
    boolean settle() {
        chosen.clear();
        int before = version;
        reconcile();
        return version != before;
    }

    /** The ports standing on each landmass, by id (a template with no row stands at home): what closure() links by. */
    SortedMap<Integer, Integer> standingPorts() {
        TreeMap<Integer, Integer> out = new TreeMap<>();
        if (stock == null) return out;
        for (int t : stock.portTemplates()) {
            Segments s = ports.get(t);
            if (s == null) {
                int n = stock.standing(t);
                if (n > 0) out.merge(home, n, Integer::sum);
            } else {
                for (int id : s.standing) out.merge(id == 0 ? home : id, 1, Integer::sum);
            }
        }
        return out;
    }

    /** A template's units on a landmass, wherever they are: standing, on site, stopped, or being demolished. */
    private int unitsOn(int template, int id) {
        int at = id == 0 ? home : id;
        Segments s = ports.get(template);
        int n = 0;
        if (s == null) {
            if (at == home) n += Math.max(0, stock.standing(template)) + Math.max(0, stock.onSite(template)) + Math.max(0, stock.shell(template));
        } else {
            n += count(s.standing, at) + count(s.onSite, at) + count(s.shell, at);
            if (at == home) n += count(s.standing, 0) + count(s.onSite, 0) + count(s.shell, 0);
        }
        for (int[] d : stock.demolitions(template)) {
            Leave l = leaving.get(d[0]);
            if (l == null) { if (at == home) n += d[1]; }
            else n += count(l.landmasses, at) + (at == home ? count(l.landmasses, 0) : 0);
        }
        return n;
    }

    /** The standing terminals of a template as landmass ids, oldest first: fitted to the buildings' count unless `raw` (an event's, whose count has already moved). */
    private List<Integer> standingUnits(int template, boolean raw) {
        Segments s = ports.get(template);
        List<Integer> out = new ArrayList<>();
        if (s != null) out.addAll(s.standing);
        if (!raw) fit(out, Math.max(0, stock.standing(template)));
        return out;
    }

    /** The indices of a template's standing units (into the list standingUnits() gives) that may go, newest first: each is judged with the ones taken before it gone, and stays if its going would take a landmass out of the linked set. */
    private List<Integer> removable(int template, boolean raw) {
        List<Integer> units = standingUnits(template, raw);
        TreeMap<Integer, Integer> now = new TreeMap<>(standingPorts());
        SortedSet<Integer> before = closure(home, now, joins);
        List<Integer> out = new ArrayList<>();
        for (int i = units.size() - 1; i >= 0; i--) {
            TreeMap<Integer, Integer> after = new TreeMap<>(now);
            after.merge(units.get(i), -1, Integer::sum);
            if (closure(home, after, joins).containsAll(before)) {
                out.add(i);
                now = after;
            }
        }
        return out;
    }

    /**
     * How many of a template's standing terminals the city may demolish: those whose loss leaves every landmass it links
     * linked (ET5 D11 - unlinking is refused, the builders'-last-depot shape). All of them for a city with no ground off
     * home, and for a template that is not a sea terminal.
     */
    public int freeToGo(int template) {
        if (stock == null || !stock.known(template)) return Integer.MAX_VALUE;
        if (single() && !hasPlacements()) return Math.max(0, stock.standing(template));
        return removable(template, false).size();
    }

    /** The landmasses that would leave the linked set were all of a template's standing terminals gone, named for a sentence ("Island 2"); empty when they link nothing. */
    public String linkedBy(int template) {
        if (stock == null || !stock.known(template)) return "";
        TreeMap<Integer, Integer> now = new TreeMap<>(standingPorts());
        SortedSet<Integer> before = closure(home, now, joins);
        for (int id : standingUnits(template, false)) now.merge(id, -1, Integer::sum);
        SortedSet<Integer> after = closure(home, now, joins);
        List<String> names = new ArrayList<>();
        for (int id : before) if (!after.contains(id)) names.add(name(id));
        return joined(names);
    }

    /** The card's pick of a landmass for a template's next order (ONE order's); portLandmass() uses it if the landmass will do. */
    public void choose(int template, int landmass) { chosen.put(template, landmass); }

    /** The landmass the card picked for a template, NO_LANDMASS for none. */
    public int chosen(int template) { return chosen.getOrDefault(template, NO_LANDMASS); }

    /** An order was placed: its pick is spent. */
    void clearChoice(int template) { chosen.remove(template); }

    /**
     * The landmass an order for a sea terminal stands on (ET5 D10), or NO_LANDMASS when none will do (NO_COAST). `asked`:
     * CHOSEN - the card's pick if there is one and the landmass has shore the city owns, else the rule; RULE - the rule
     * alone: home if the city owns shore on it, else the linked landmass with shore and the most dry ground owned (the
     * lowest id on a tie), never an unlinked one; or a landmass's id, which serves if it has shore. Home for a city that
     * owns nothing off home (its coast test is the old one, Game.hasCoastFor()).
     */
    public int portLandmass(int template, int asked) {
        if (single()) return home;
        if (asked == CHOSEN) {
            Integer c = chosen.get(template);
            if (c != null && hasShore(c)) return c == 0 ? home : c;
            return byRule();
        }
        if (asked == RULE) return byRule();
        return asked >= 0 && hasShore(asked) ? (asked == 0 ? home : asked) : NO_LANDMASS;
    }

    private boolean hasShore(int id) {
        return shorePlots(id) > 0;
    }

    private int byRule() {
        if (shorePlots(home) > 0) return home;
        int best = NO_LANDMASS;
        long most = -1;
        for (Books b : books.values()) {
            if (!linked(b.id) || b.shore <= 0) continue;
            if (b.dry() > most) {
                most = b.dry();
                best = b.id;
            }
        }
        return best;
    }

    /** Whether some landmass the city has linked has shore it owns - the desalination plant's coast (ET5 D9): home's if it has any, else a linked island's. */
    public boolean linkedShore() {
        if (shorePlots(home) > 0) return true;
        for (Books b : books.values()) if (linked(b.id) && b.shore > 0) return true;
        return false;
    }

    /**
     * The landmass a works field centred on plot (x, y) stands on (ET5 D9): the plot's 15" cell resolved as a dry plot's is
     * (World.landmassOfPlot()), so a field centred on a lake plot of an island is on the island and not on home; 0, which
     * reads as home, for a field in the sea (a platform's), which belongs to no landmass. The ONE read of a field's
     * landmass: the model's works gate (LandManager.freeSites()) and the map's site index (CityMap's tallySites()) both
     * ask it, so that no field is drawn as a works site that the model refuses, or the other way about.
     */
    public static int fieldLandmass(World world, long x, long y) {
        if (world.depthAt(x, y) > 0) return 0;
        return world.landmassOfPlot(x, y);
    }

    /** The landmasses the card offers for a sea terminal: those the city owns shore on, home first and then as rows() lists them; empty for a city on one landmass. */
    public List<Row> portChoices() {
        List<Row> out = new ArrayList<>();
        if (single()) return out;
        for (Row r : rows()) if (r.shorePlots() > 0) out.add(r);
        return out;
    }

    /** The ports standing on a landmass, by Ports.Cargo's ordinal (a template with no row stands at home). */
    public int[] portsOn(int id) {
        int at = id == 0 ? home : id;
        int[] out = new int[Ports.Cargo.values().length];
        if (stock == null) return out;
        for (int t : stock.portTemplates()) {
            Segments s = ports.get(t);
            int n = s == null ? (at == home ? Math.max(0, stock.standing(t)) : 0) : count(s.standing, at) + (at == home ? count(s.standing, 0) : 0);
            out[stock.cargo(t)] += n;
        }
        return out;
    }

    /**
     * The foothill an island's terminals are cut into when they stand on more than its flat ground, square feet (the pad
     * a building on foothill is charged, ET2): the rise in max(0, ports - flat) that `sqFt` more would make, with `aheadSqFt`
     * of the same run's ports before it. Priced, not credited to LandManager's prepared ground (ET5-C9).
     */
    public double padSqFt(int id, double sqFt, double aheadSqFt) {
        Books b = books.get(id == 0 ? home : id);
        if (b == null) return 0;
        double flat = b.relief[World.FLAT] * SQ_FT_PER_PLOT;
        double held = portsGroundSqFt(id) + Math.max(0, aheadSqFt);
        return Math.max(0, held + Math.max(0, sqFt) - flat) - Math.max(0, held - flat);
    }

    /**
     * What the map is handed (slice C): home, the linked ids ascending, the standing terminals by template and landmass as
     * triples {template id, landmass id, units} in one array, and a stamp that changes when the linked set does. Null for
     * a city on one landmass - the map then runs as it always did.
     */
    public record Picture(int home, int[] linked, long[] portsByTypeAndLandmass, long stamp) { }

    /** The picture of the linked set and the terminals on it; null while the city owns nothing off home. */
    public Picture picture() {
        if (single()) return null;
        int[] ids = new int[linkedSet.size()];
        int k = 0;
        long stamp = 1125899906842597L * 31 + home;
        for (int id : linkedSet) {
            ids[k++] = id;
            stamp = stamp * 31 + id;
        }
        List<long[]> triples = new ArrayList<>();
        if (stock != null) {
            int[] templates = stock.portTemplates().clone();
            java.util.Arrays.sort(templates);
            for (int t : templates) {
                TreeMap<Integer, Integer> by = new TreeMap<>();
                Segments s = ports.get(t);
                if (s == null) {
                    int n = stock.standing(t);
                    if (n > 0) by.put(home, n);
                } else {
                    for (int id : s.standing) by.merge(id == 0 ? home : id, 1, Integer::sum);
                }
                for (Map.Entry<Integer, Integer> e : by.entrySet()) triples.add(new long[] { t, e.getKey(), e.getValue() });
            }
        }
        long[] flat = new long[triples.size() * 3];
        for (int i = 0; i < triples.size(); i++) System.arraycopy(triples.get(i), 0, flat, i * 3, 3);
        return new Picture(home, ids, flat, stamp);
    }

    /**
     * DataSave's landmasses: where the terminals stand and which demolitions are taking some down; fresh on every call (the autosave's snapshot shares nothing), null when nothing stands off home.
     * A row or a leave that has become all home (a hand demolition of the last unit off home, before the month's settle drops it) is not written: a load drops it
     * (reconcile()), so a save written now reads back as it was written and a resave is the same bytes.
     */
    public State toState() {
        if (ports.isEmpty() && leaving.isEmpty()) return null;
        State s = new State();
        for (Map.Entry<Integer, Segments> e : ports.entrySet()) {
            if (allHome(e.getValue().standing) && allHome(e.getValue().onSite) && allHome(e.getValue().shell)) continue;
            PortRow r = new PortRow();
            r.template = e.getKey();
            r.standing = toArray(e.getValue().standing);
            r.onSite = toArray(e.getValue().onSite);
            r.shell = toArray(e.getValue().shell);
            s.ports.add(r);
        }
        for (Leave l : leaving.values()) {
            if (allHome(l.landmasses)) continue;
            Leaving r = new Leaving();
            r.demolition = l.demolition;
            r.template = l.template;
            r.landmasses = toArray(l.landmasses);
            s.leaving.add(r);
        }
        return s.ports.isEmpty() && s.leaving.isEmpty() ? null : s;
    }

    private static int[] toArray(List<Integer> list) {
        int[] out = new int[list.size()];
        for (int i = 0; i < out.length; i++) out[i] = list.get(i);
        return out;
    }

    private static void into(List<Integer> list, int[] ids) {
        if (ids != null) for (int id : ids) list.add(id);
    }

    /** Puts the placements back as saved; null, none. A row of a template that is no sea terminal is passed over (when the buildings are wired). The caller reconciles and refreshes. */
    void restore(State s) {
        ports.clear();
        leaving.clear();
        chosen.clear();
        if (s == null) return;
        if (s.ports != null) {
            for (PortRow r : s.ports) {
                if (r == null || (stock != null && !stock.known(r.template))) continue;
                Segments seg = new Segments();
                into(seg.standing, r.standing);
                into(seg.onSite, r.onSite);
                into(seg.shell, r.shell);
                ports.put(r.template, seg);
            }
        }
        if (s.leaving != null) {
            for (Leaving r : s.leaving) {
                if (r == null || (stock != null && !stock.known(r.template))) continue;
                Leave l = new Leave(r.demolition, r.template);
                into(l.landmasses, r.landmasses);
                leaving.put(r.demolition, l);
            }
        }
    }

    /* =====================================================================
       READS - plain getters; none fills anything
       ===================================================================== */

    /** The home landmass's id: the landmass at the land's site plot; 0 where there are no landmasses to read. */
    public int home() { return home; }

    /** Whether the city owns dry ground on no landmass but home's: then every figure of the free ground is the old one, to the bit. */
    public boolean single() { return books.isEmpty(); }

    /** Whether the city owns dry ground on a landmass it has not linked. */
    public boolean anyUnlinked() { return !unlinkedIds.isEmpty(); }

    /** Whether a landmass is one the city's buildings may stand on: home, none (0) and the linked set (closure()). */
    public boolean linked(int id) { return id == 0 || id == home || linkedSet.contains(id); }

    /** The ids of the landmasses the city's buildings may stand on, ascending (ET6): home and the linked set (closure()). */
    public int[] linkedIds() {
        java.util.TreeSet<Integer> all = new java.util.TreeSet<>(linkedSet);
        all.add(home);
        int[] out = new int[all.size()];
        int i = 0;
        for (int id : all) out[i++] = id;
        return out;
    }

    /** The ids of the landmasses the city owns dry ground on and has not linked, ascending. */
    public List<Integer> unlinkedIds() { return Collections.unmodifiableList(new ArrayList<>(unlinkedIds)); }

    /** The unlinked landmasses' dry ground, square feet: what the free ground takes off the owned (LandManager). */
    public double unlinkedDrySqFt() { return unlinkedDry; }

    /** ...their ground of one relief (CityLand.RELIEF_FLAT to RELIEF_VERY_STEEP), square feet. */
    public double unlinkedReliefSqFt(int relief) { return relief >= 0 && relief < World.RELIEFS ? unlinkedRelief[relief] : 0; }

    /** ...their forest, square feet (part of their dry ground). */
    public double unlinkedForestSqFt() { return unlinkedForest; }

    /** The ground the ports standing on unlinked landmasses stand on, square feet: part of the city's allocated ground, and part of what an unlinked landmass holds (0 while no port stands, is being built or is being demolished there). */
    public double unlinkedPortsSqFt() { return unlinkedPorts; }

    /** The dry ground of an unlinked landmass nothing stands on, square feet; 0 for a linked one or one the city owns none of. */
    public double unlinkedFreeSqFt(int id) {
        Books b = books.get(id);
        return b == null || linked(id) ? 0 : Math.max(0, b.dry() * SQ_FT_PER_PLOT - portsGroundSqFt(id));
    }

    /** ...all the unlinked landmasses' together. */
    public double unlinkedFreeSqFt() {
        double sum = 0;
        for (int id : unlinkedIds) sum += unlinkedFreeSqFt(id);
        return sum;
    }

    /** The ground the ports standing, being built, stopped or being demolished on a landmass hold, square feet: 0 where there are none. */
    public double portsGroundSqFt(int id) {
        if (stock == null) return 0;
        double sqFt = 0;
        for (int t : stock.portTemplates()) sqFt += unitsOn(t, id) * stock.landSqFt(t);
        return sqFt;
    }

    /** What a port may be built on on a landmass: its flat and foothill ground less the ports' on it, square feet, never below nothing. */
    public double portRoomSqFt(int id) {
        Books b = id == home ? homeBooks : books.get(id);
        return b == null ? 0 : Math.max(0, (b.relief[World.FLAT] + b.relief[World.FOOTHILL]) * SQ_FT_PER_PLOT - portsGroundSqFt(id));
    }

    /** The plots of a landmass the city owns that are shore: flat or foothill dry ground with owned salt water beside it. Counted only while the city owns ground off home; 0 otherwise. */
    public long shorePlots(int id) {
        if (single()) return 0;
        Books b = id == home || id == 0 ? homeBooks : books.get(id);
        return b == null ? 0 : b.shore;
    }

    /** The dry plots of a landmass the city owns (home's: the land's less the others'). */
    public long dryPlots(int id) {
        Books b = id == home || id == 0 ? homeBooks : books.get(id);
        return b == null ? 0 : b.dry();
    }

    /** A landmass's name: "Home", the data's name as the founding card writes it ("Charlottetown's"), else "Island N" (N from 2, in the order the city first owned them). */
    public String name(int id) {
        if (id == home || id == 0) return HOME_NAME;
        String n = names.get(id);
        return n != null ? n : "a landmass";
    }

    /** The unlinked landmasses' names joined for a sentence: "Island 2", "Island 2 and Island 3", "Island 2, Island 3 and Island 4"; empty for none. */
    public String unlinkedNames() {
        List<String> list = new ArrayList<>();
        for (int id : unlinkedIds) list.add(name(id));
        return joined(list);
    }

    /** Names joined for a sentence: "Island 2", "Island 2 and Island 3", "Island 2, Island 3 and Island 4"; empty for none. */
    private static String joined(List<String> list) {
        if (list.isEmpty()) return "";
        if (list.size() == 1) return list.get(0);
        return String.join(", ", list.subList(0, list.size() - 1)) + " and " + list.get(list.size() - 1);
    }

    /** The landmasses the city owns, home first and then in the order it first owned them. */
    public List<Row> rows() {
        List<Row> out = new ArrayList<>();
        out.add(row(homeBooks, true));
        List<Books> others = new ArrayList<>(books.values());
        others.sort((a, b) -> a.firstHolding != b.firstHolding ? Integer.compare(a.firstHolding, b.firstHolding) : Integer.compare(a.id, b.id));
        for (Books b : others) out.add(row(b, false));
        return out;
    }

    private Row row(Books b, boolean isHome) {
        return new Row(isHome ? home : b.id, isHome ? HOME_NAME : name(b.id), isHome, isHome || linked(b.id), b.dry(),
                b.relief[World.FLAT], b.relief[World.FOOTHILL], b.relief[World.STEEP], b.relief[World.VERY_STEEP], b.forest,
                single() ? 0 : b.shore, b.firstHolding);
    }

    /** How many times the linked set has moved: what a cache of anything that depends on it keys on. */
    public int version() { return version; }

    /** How many plots recount() and offerSplit() have read since this was made (cumulative): 0 for a city that owns nothing near another landmass, and for every generated world. */
    public long plotsRead() { return plotsRead; }

    /** Whether the last recount read a world with landmasses: false on a generated world and an earth-1 file. */
    public boolean active() { return active; }
}
