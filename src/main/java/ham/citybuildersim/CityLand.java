package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The city's land on the world: whole blocks of a grid lined up with the world - the centre it was founded or converted with, and every purchase since, each a rectangle of blocks - and what all of it holds: its area dry, fresh, sea and forest, counted plot by plot, and the sites and amounts of the seven resources.
 *
 * WHY THIS EXISTS (0.7.57, batch J1b; the project's spec-land.md 2.2). Until
 * then the city's land was one number, the square feet it owned, and the land
 * office sold parcels drawn by a generator seeded with the parcel's id: a
 * size, a price and some iron, and no place. The world (World, 0.7.56) is
 * real ground, so the city's land is a piece of it.
 *
 * ON THE BLOCK GRID SINCE 0.7.67 (batch M3; the project's spec-grid.md). From
 * 0.7.57 to 0.7.66 the land was a square centre and forty lanes of wedges
 * pushed out band by band (LegacyLand keeps that geometry, read-only, to put
 * a saved city on the grid). Jerus, 2026-10-07: the block grid, six offers a
 * side, never rerolled; "the generation should only put what the city has,
 * not more not less". The ground is now whole blocks of LandGrid - squares of
 * 2^k plots lined up with the world's tiles and districts - held as a region
 * quadtree: the CENTRE's blocks (holding 0) and each purchase's rectangle
 * (holdings 1, 2, ... in the order bought). A purchase claims only the plots
 * of its rectangle no holding owns yet, so the tree is the same whatever its
 * history, given its rectangles in order: it is never saved, only its
 * rectangles are, and it is replayed at load (LandGrid.replay()).
 *
 * THE BOOKS ARE THE PLOTS DRAWN (spec-grid star 8; exact at every size, the
 * orchestrator's decision of 2026-10-07). A holding's five areas are its
 * plots counted one by one with World.tileTerrain(), as the map counts them:
 * the centre's when it is drawn, an offer's when it is listed (groundOf(),
 * every plot of its rectangle no holding owns). Nothing is sampled. Its
 * forest's timber is its forest's area x World.FOREST_M3_PER_KM2.
 *
 * A FIELD GOES WHOLE WITH ITS CENTRE (spec-land star 12). Jerus, 2026-10-07:
 * "Yes whole iron fields as one offer, yes that means significant
 * investment." A field - every one of its sites and all its amount - belongs
 * to the holding whose ground holds the field's centre plot, wherever its
 * sites lie. The holdings never share a plot, so every field's centre is on
 * one holding's ground or none, and the world's totals are kept to the tonne.
 * A city converted from a save written by 0.7.58 to 0.7.63 (which held a
 * field site by site) keeps exactly the sites it held of a field it held
 * only part of (a PART FIELD, GridConversion.PartField); that field's other
 * sites go a site at a time to the holding whose ground holds each site's
 * plot (siteHolding()).
 *
 * NOTHING ROLLS INTO THE CENTRE. The centre is the land the city was founded
 * or converted with, fixed; the purchases are kept in the order they were
 * made, which is the order the ground is worked out in (LandManager's
 * depletion). Only a restatement (LandConversion.restate()) - a harness's
 * ground by fiat - draws the centre again, and folds everything into it.
 */
public final class CityLand {

    /** Sides of the city, seen from the founding site: north, east, south and west, in that order - LegacyLand.SIDES (spec-land star 1). */
    public static final int SIDES = LegacyLand.SIDES;

    /** The sides' names, in their order: LegacyLand's. */
    static final String[] SIDE_NAMES = LegacyLand.SIDE_NAMES;

    /** Where a record keeps its whole area, in square kilometres. */
    public static final int TOTAL = 0;

    /** ...its dry ground: what buildings stand on, and what the city's square feet count. */
    public static final int DRY = 1;

    /** ...its fresh water: lakes and the founding river. */
    public static final int FRESH = 2;

    /** ...its sea. */
    public static final int SEA = 3;

    /** ...and its forest, which is dry ground too. */
    public static final int FOREST = 4;

    /** How many areas a record keeps: total, dry, fresh, sea and forest. */
    public static final int AREAS = 5;

    /** How many resources a record keeps the sites and amounts of: Resource's seven, in its order. */
    public static final int KINDS = Resource.values().length;

    /** The centre's holding on the grid: 0; the k-th purchase is holding k. */
    public static final int CENTRE = 0;

    /* =====================================================================
       THE STATE
       ===================================================================== */

    private final long seed;
    private final long siteX, siteY;
    private LandGrid grid = new LandGrid();
    private final List<LandGrid.Fill> centreRects = new ArrayList<>();
    private final double[] centreKm2 = new double[AREAS];
    private final int[] centreSites = new int[KINDS];
    private final double[] centreAmounts = new double[KINDS];
    private long legacyX = -1, legacyY = -1;
    private int legacySites;
    private final List<Purchase> purchases = new ArrayList<>();
    private final List<GridConversion.PartField> parts = new ArrayList<>();
    private final Map<FieldKey, GridConversion.PartField> partIndex = new HashMap<>();
    private double[][] converted;

    /* What the whole holds, kept in step: the centre's and every purchase's, added in acquisition order. */
    private final double[] totalKm2 = new double[AREAS];
    private final long[] totalSites = new long[KINDS];
    private final double[] totalAmounts = new double[KINDS];

    /**
     * One purchase: the offer bought, as it was listed, the month it was
     * bought in and what the treasury paid for it in local money.
     */
    public record Purchase(LandParcel offer, int month, double paidLocal) { }

    /** A field by its kind, the world cell it was drawn in and its index there. */
    record FieldKey(int kind, int cell, int index) { }

    private CityLand(long seed, long siteX, long siteY) {
        this.seed = seed;
        this.siteX = siteX;
        this.siteY = siteY;
    }

    /* =====================================================================
       FOUNDING: A NEW CITY'S CENTRE OF WHOLE BLOCKS
       ===================================================================== */

    /**
     * A city's land at founding (spec-grid 2.1, 3): round the site (x, y),
     * L-infinity rings of whole blocks of the level dryKm2 makes
     * (LandGrid.levelFor() of its plots) round the site's block - each ring's
     * north row west to east, east column, south row, west column - until
     * their dry plots reach dryKm2 rounded up to a whole plot; nothing bought.
     * Its books are its plots counted, so it holds a little more dry ground
     * than asked: the default city 315 plots for 309.7 (+1.7%), 21 blocks of
     * 120 m, 0.3024 km2.
     */
    public static CityLand found(World world, long x, long y, double dryKm2) {
        CityLand land = new CityLand(world.seed(), x, y);
        int level = LandGrid.levelFor(dryKm2 / World.KM2_PER_PLOT);
        long b = 1L << level;
        long target = (long) Math.ceil(dryKm2 / World.KM2_PER_PLOT - GridConversion.WHOLE_PLOT);
        long cbx = Math.floorDiv(x, b), cby = Math.floorDiv(y, b), last = World.SIDE / b - 1;
        long dry = 0;
        byte[] t = new byte[World.TILE * World.TILE];
        outer:
        for (int ring = 0; target > 0; ring++) {
            if (ring > World.SIDE / b) throw new IllegalStateException("the world holds less dry ground than " + dryKm2 + " km2");
            for (long[] p : GridConversion.ringOf(cbx, cby, ring)) {
                if (p[0] < 0 || p[1] < 0 || p[0] > last || p[1] > last) continue;
                dry += dryPlots(world, t, p[0] * b, p[1] * b, b);
                LandGrid.Fill f = new LandGrid.Fill(p[0] * b, p[1] * b, p[0] * b + b, p[1] * b + b, CENTRE);
                land.grid.fill(f.x0(), f.y0(), f.x1(), f.y1(), CENTRE);
                land.centreRects.add(f);
                if (dry >= target) break outer;
            }
        }
        land.drawnBooks(world);
        return land;
    }

    /** The dry plots of a square of `b` plots at (x0, y0), read a tile at a time. */
    private static long dryPlots(World world, byte[] t, long x0, long y0, long b) {
        long n = 0;
        for (long ty = Math.floorDiv(y0, World.TILE); ty <= Math.floorDiv(y0 + b - 1, World.TILE); ty++) {
            for (long tx = Math.floorDiv(x0, World.TILE); tx <= Math.floorDiv(x0 + b - 1, World.TILE); tx++) {
                world.tileTerrain(tx, ty, t);
                long px0 = tx * World.TILE, py0 = ty * World.TILE;
                for (long y = Math.max(y0, py0); y < Math.min(y0 + b, py0 + World.TILE); y++) {
                    for (long x = Math.max(x0, px0); x < Math.min(x0 + b, px0 + World.TILE); x++) {
                        byte c = t[(int) ((y - py0) * World.TILE + (x - px0))];
                        if (c != World.SALT && c != World.FRESH) n++;
                    }
                }
            }
        }
        return n;
    }

    /** The centre's books from its plots drawn (GridConversion.drawnClasses()), and every field whose centre plot is on it, whole. */
    private void drawnBooks(World world) {
        double[] k = GridConversion.km2Of(GridConversion.drawnClasses(world, grid));
        System.arraycopy(k, 0, centreKm2, 0, AREAS);
        Arrays.fill(centreSites, 0);
        Arrays.fill(centreAmounts, 0);
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            for (int cell : cellsUnder(grid.minX(), grid.minY(), grid.maxX() - 1, grid.maxY() - 1)) {
                for (Deposit d : fields(world, cell, r)) {
                    if (grid.owner(d.x(), d.y()) != CENTRE) continue;
                    centreSites[r.ordinal()] += d.sites();
                    centreAmounts[r.ordinal()] += d.amount();
                }
            }
        }
        centreAmounts[Resource.FOREST.ordinal()] = Math.rint(centreKm2[FOREST] * World.FOREST_M3_PER_KM2);
        recount();
    }

    /* =====================================================================
       A CENTRE CONVERTED OR DRAWN AGAIN (LandConversion, GridConversion)
       ===================================================================== */

    /** A city's land whose centre is a converted holding (spec-grid 2.6): its blocks, books, sites, amounts, legacy field and part fields; nothing bought since; `history` the save's purchase records, kept as they were. */
    static CityLand converted(long seed, GridConversion.Result r, double[][] history) {
        CityLand land = new CityLand(seed, r.siteX(), r.siteY());
        land.take(r);
        land.converted = history == null ? new double[0][] : deepCopy(history);
        return land;
    }

    /** Draws the centre again from a conversion's result round the same site - a restatement - and folds every purchase into it: the same object, so the map and the office bound to it see the land drawn again (centreStamp()). */
    void redraw(GridConversion.Result r) {
        if (r.siteX() != siteX || r.siteY() != siteY) throw new IllegalArgumentException("a restatement keeps the site");
        take(r);
    }

    private void take(GridConversion.Result r) {
        grid = LandGrid.replay(r.fills());
        centreRects.clear();
        centreRects.addAll(r.fills());
        for (int a = 0; a < AREAS; a++) centreKm2[a] = r.km2(a);
        for (Resource res : Resource.values()) {
            centreSites[res.ordinal()] = (int) Math.min(Integer.MAX_VALUE, r.sites(res));
            centreAmounts[res.ordinal()] = r.amount(res);
        }
        legacyX = r.legacyX();
        legacyY = r.legacyY();
        legacySites = r.legacySites();
        purchases.clear();
        parts.clear();
        partIndex.clear();
        for (GridConversion.PartField p : r.parts()) addPart(p);
        recount();
    }

    private void addPart(GridConversion.PartField p) {
        parts.add(p);
        partIndex.put(new FieldKey(p.kind().ordinal(), p.cell(), p.index()), p);
    }

    /** Sets what the centre holds of one resource - a restatement keeping the city's, or a fixture's ore (LandManager.restoreSites()). */
    void setCentre(Resource r, int sites, double amount) {
        centreSites[r.ordinal()] = Math.max(0, sites);
        centreAmounts[r.ordinal()] = Math.max(0, amount);
        recount();
    }

    /** Adds the totals up again, in acquisition order. */
    private void recount() {
        System.arraycopy(centreKm2, 0, totalKm2, 0, AREAS);
        for (int k = 0; k < KINDS; k++) {
            totalSites[k] = centreSites[k];
            totalAmounts[k] = centreAmounts[k];
        }
        for (Purchase p : purchases) add(p.offer());
    }

    private void add(LandParcel o) {
        for (int a = 0; a < AREAS; a++) totalKm2[a] += o.getKm2(a);
        for (Resource r : Resource.values()) {
            totalSites[r.ordinal()] += o.getSites(r);
            totalAmounts[r.ordinal()] += o.getAmount(r);
        }
    }

    /**
     * Buys an offer: its rectangle's plots no holding owns are claimed for
     * the next holding (LandGrid.fill()), and the purchase is kept, after
     * every one before it. Its books are as it was listed: its ground could
     * not change while it stood.
     */
    public void extend(LandParcel offer, int month, double paidLocal) {
        grid.fill(offer.getX0(), offer.getY0(), offer.getX1(), offer.getY1(), purchases.size() + 1);
        purchases.add(new Purchase(offer, month, paidLocal));
        add(offer);
    }

    /* =====================================================================
       WHAT IT IS
       ===================================================================== */

    /** The seed of the world it lies on. */
    public long seed()                  { return seed; }

    /** The founding site, as a plot: east from the world's west edge... */
    public long siteX()                 { return siteX; }

    /** ...and south from its north edge. */
    public long siteY()                 { return siteY; }

    /** The ground on the grid: which holding owns each plot. Read it; only this class fills it. */
    public LandGrid grid()              { return grid; }

    /** The city's block level: LandGrid.levelFor() of every plot it owns (spec-grid star 2). */
    public int level()                  { return grid.level(); }

    /** The centre's blocks, as rectangles for holding 0, in the order drawn. */
    public List<LandGrid.Fill> centreRects() { return Collections.unmodifiableList(centreRects); }

    /** One of the centre's areas, in square kilometres (TOTAL, DRY, FRESH, SEA or FOREST). */
    public double centreKm2(int area)   { return centreKm2[area]; }

    /** The centre's sites of a resource. */
    public int centreSites(Resource r)  { return centreSites[r.ordinal()]; }

    /** The centre's amount of a resource, in its unit. */
    public double centreAmount(Resource r) { return centreAmounts[r.ordinal()]; }

    /** A converted centre's legacy iron field: its plot east, -1 when there is none... */
    public long legacyX()               { return legacyX; }

    /** ...south... */
    public long legacyY()               { return legacyY; }

    /** ...and its sites. */
    public int legacySites()            { return legacySites; }

    /** The purchases, in the order they were made. */
    public List<Purchase> purchases()   { return Collections.unmodifiableList(purchases); }

    /** The fields the city holds only part of, a converted 0.7.58-0.7.63 save's (spec-grid 2.6). */
    public List<GridConversion.PartField> partFields() { return Collections.unmodifiableList(parts); }

    /** A converted city's purchase records as its save had them (LegacyLand's 28-wide rows), kept as history; null for a city founded on the grid. */
    public double[][] convertedHistory() { return converted == null ? null : deepCopy(converted); }

    /** One of the whole city's areas, centre and purchases, in square kilometres. */
    public double totalKm2(int area)    { return totalKm2[area]; }

    /** The whole city's sites of a resource. */
    public long totalSites(Resource r)  { return totalSites[r.ordinal()]; }

    /** The whole city's amount of a resource, as listed: O, what conservation counts the city's (spec-land 2.1). */
    public double totalAmount(Resource r) { return totalAmounts[r.ordinal()]; }

    /** What the purchases alone hold of a resource's sites. */
    public long purchasedSites(Resource r) { return totalSites[r.ordinal()] - centreSites[r.ordinal()]; }

    /** ...and of its amount. */
    public double purchasedAmount(Resource r) {
        double sum = 0;
        for (Purchase p : purchases) sum += p.offer().getAmount(r);
        return sum;
    }

    /** What each holding listed of a resource, in acquisition order: the centre first, then each purchase - the order it is worked out in. */
    public double[] amountsInOrder(Resource r) {
        double[] out = new double[1 + purchases.size()];
        out[0] = centreAmounts[r.ordinal()];
        for (int i = 0; i < purchases.size(); i++) out[i + 1] = purchases.get(i).offer().getAmount(r);
        return out;
    }

    /** A side's name: North, East, South or West. */
    public static String sideName(int side) { return SIDE_NAMES[side]; }

    /** The holding owning plot (x, y): 0 the centre, k the k-th purchase, -1 none. */
    public int holdingOf(long x, long y) { return grid.owner(x, y); }

    /** Whether the city owns a plot. */
    public boolean ownsPlot(long x, long y) { return grid.owner(x, y) >= 0; }

    /** Whether the city owns the point (dx, dy) from the site plot's centre: the plot it lies in. */
    public boolean owns(double dx, double dy) {
        return ownsPlot(siteX + (long) Math.floor(dx + 0.5), siteY + (long) Math.floor(dy + 0.5));
    }

    /** The stamp of the centre's blocks: what a map tells a restatement by (CityMap.syncLand()). */
    public long centreStamp() {
        long h = World.mix(seed ^ World.mix(siteX * 0x9E3779B97F4A7C15L ^ siteY));
        for (LandGrid.Fill f : centreRects) h = World.mix(h ^ World.mix(f.x0() * 31 + f.y0()) ^ (f.x1() * 0xC2B2AE3D27D4EB4FL + f.y1()));
        return h;
    }

    /** The stamp of all its ground: the centre's blocks, then every purchase's rectangle, in order (the sidecar's land stamp, spec-grid 2.4). */
    public long stamp() { return stamp(purchases.size()); }

    /** ...of the centre and its first n purchases: the ground a map has measured when it has seen n of them. */
    public long stamp(int n) {
        long h = centreStamp();
        for (int i = 0; i < Math.min(n, purchases.size()); i++) {
            LandParcel o = purchases.get(i).offer();
            h = World.mix(h ^ World.mix(o.getX0() * 31 + o.getY0()) ^ (o.getX1() * 0xC2B2AE3D27D4EB4FL + o.getY1()));
        }
        return World.mix(h ^ n);
    }

    /* =====================================================================
       THE FIELDS ON A PIECE OF GROUND (spec-land star 12; spec-grid 2.6)
       ===================================================================== */

    /** Whether a field is one the city holds only part of. */
    public boolean isPart(Deposit d) { return d.cell() >= 0 && partIndex.containsKey(new FieldKey(d.kind().ordinal(), d.cell(), d.index())); }

    /** The part field record of a field, or null when the city does not hold it in part. */
    GridConversion.PartField partOf(Deposit d) {
        return d.cell() < 0 ? null : partIndex.get(new FieldKey(d.kind().ordinal(), d.cell(), d.index()));
    }

    /**
     * The holding a field's k-th site is the city's by, or -1: a whole
     * field's holding is the one whose ground holds its centre plot; a part
     * field's site is the converted centre's when the save held it, else
     * the holding whose ground holds the site's plot (GridConversion.sitePlot()).
     * A converted centre's legacy field (cell -1) is the centre's.
     */
    public int siteHolding(Deposit d, int k) {
        if (d.cell() < 0) return CENTRE;
        GridConversion.PartField p = partOf(d);
        if (p == null) return grid.owner(d.x(), d.y());
        for (int s : p.owned()) if (s == k) return CENTRE;
        long[] at = GridConversion.sitePlot(d, k);
        int h = grid.owner(at[0], at[1]);
        return h > CENTRE ? h : -1;
    }

    /**
     * What the plots of [x0, x1) x [y0, y1) no holding owns hold of the
     * fields (spec-land star 12): every field whose centre plot is one of
     * them, whole, and each site there of a field the city holds in part
     * (its share, Deposit.siteAmount()) - added into sites and amounts.
     * Forest's timber is the caller's, from the ground's area.
     */
    void fieldsOn(long x0, long y0, long x1, long y1, int[] sites, double[] amounts) {
        World world = World.of(seed);
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            for (int cell : cellsUnder(x0, y0, x1 - 1, y1 - 1)) {
                for (Deposit d : fields(world, cell, r)) {
                    if (d.x() < x0 || d.x() >= x1 || d.y() < y0 || d.y() >= y1) continue;
                    if (isPart(d) || grid.owner(d.x(), d.y()) >= 0) continue;
                    sites[r.ordinal()] += d.sites();
                    amounts[r.ordinal()] += d.amount();
                }
            }
        }
        for (GridConversion.PartField p : parts) {
            Deposit d = fieldOf(world, p);
            if (d == null) continue;
            boolean[] mine = new boolean[d.sites()];
            for (int s : p.owned()) if (s >= 0 && s < mine.length) mine[s] = true;
            for (int k = 0; k < d.sites(); k++) {
                if (mine[k]) continue;
                long[] at = GridConversion.sitePlot(d, k);
                if (at[0] < x0 || at[0] >= x1 || at[1] < y0 || at[1] >= y1 || grid.owner(at[0], at[1]) >= 0) continue;
                sites[p.kind().ordinal()]++;
                amounts[p.kind().ordinal()] += d.siteAmount(k);
            }
        }
    }

    /** The world's field a part field record names, or null. */
    static Deposit fieldOf(World world, GridConversion.PartField p) {
        for (Deposit d : fields(world, p.cell(), p.kind())) if (d.index() == p.index()) return d;
        return null;
    }

    /** The world cells under a box of plots, clipped to the world, row by row. */
    static int[] cellsUnder(double bx0, double by0, double bx1, double by1) {
        long cx0 = Math.max(0, (long) Math.floor(bx0 / World.CELL)), cx1 = Math.min(World.CELLS - 1, (long) Math.floor(bx1 / World.CELL));
        long cy0 = Math.max(0, (long) Math.floor(by0 / World.CELL)), cy1 = Math.min(World.CELLS - 1, (long) Math.floor(by1 / World.CELL));
        if (cx0 > cx1 || cy0 > cy1) return new int[0];
        int[] out = new int[(int) ((cx1 - cx0 + 1) * (cy1 - cy0 + 1))];
        int at = 0;
        for (long cy = cy0; cy <= cy1; cy++) for (long cx = cx0; cx <= cx1; cx++) out[at++] = (int) (cy * World.CELLS + cx);
        return out;
    }

    private record CellKey(long seed, int cell, Resource kind) { }

    /** How many cells' fields are kept: 256 - the nine round a site for every resource, and the cells a large holding reaches. */
    static final int CELLS_KEPT = 256;

    private static final Map<CellKey, List<Deposit>> CELLS = new LinkedHashMap<>(64, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<CellKey, List<Deposit>> eldest) {
            return size() > CELLS_KEPT;
        }
    };

    /** A cell's fields of a resource (World.fieldsInCell()), kept: drawing them places every field again, and the offers of one city ask for the same few cells. */
    static List<Deposit> fields(World world, int cell, Resource r) {
        CellKey key = new CellKey(world.seed(), cell, r);
        List<Deposit> got;
        synchronized (CELLS) { got = CELLS.get(key); }
        if (got == null) {
            got = Collections.unmodifiableList(new ArrayList<>(world.fieldsInCell(cell, r)));
            synchronized (CELLS) { CELLS.put(key, got); }
        }
        return got;
    }

    /* =====================================================================
       THE GROUND AN OFFER HOLDS: EXACT AT EVERY SIZE (spec-grid star 8)

       The orchestrator's decision (2026-10-07): the books stay exact at
       every size, never estimated from samples. An offer's ground is every
       plot of its rectangle no holding owns, counted by its class
       (World.tileTerrain(), as the map and the centre are counted). Three
       things keep that affordable without giving up a plot: a tile the city
       owns wholly is passed over (LandGrid.cover() at level 5); a tile it
       owns none of and the rectangle holds whole is counted once a world
       and kept (TILE_COUNTS_KEPT); and a rectangle of PARALLEL_TILES tiles or
       more is counted over the machine's cores, a sum of whole numbers,
       which is the same in any order - so a listing is the same on every
       machine and every run. The rest - a tile the city owns part of, or the
       rectangle's edge through a tile - is read plot by plot.
       ===================================================================== */

    /** Tiles' counts kept, by world and tile: 65,536 (a few megabytes) - the whole tiles an offer's count reads, which the next city on the same world's ground, or the same city drawn again, reads again. */
    static final int TILE_COUNTS_KEPT = 65_536;

    /** A rectangle of this many tiles or more is counted over the machine's cores: 2,048 (about an eighth of a second's reading on one core). */
    static final int PARALLEL_TILES = 2_048;

    private record TileKey(long seed, long tx, long ty) { }

    private static final Map<TileKey, long[]> TILE_COUNTS = new LinkedHashMap<>(1024, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<TileKey, long[]> eldest) {
            return size() > TILE_COUNTS_KEPT;
        }
    };

    /** A whole tile's plots by World's classes, kept. */
    static long[] tileCounts(World world, long tx, long ty) {
        TileKey key = new TileKey(world.seed(), tx, ty);
        long[] got;
        synchronized (TILE_COUNTS) { got = TILE_COUNTS.get(key); }
        if (got != null) return got;
        byte[] t = new byte[World.TILE * World.TILE];
        world.tileTerrain(tx, ty, t);
        long[] c = new long[5];
        for (byte b : t) c[b]++;
        synchronized (TILE_COUNTS) { TILE_COUNTS.put(key, c); }
        return c;
    }

    /** The five areas, in square kilometres, of the plots of [x0, x1) x [y0, y1) no holding owns, each counted by its class. */
    public double[] groundOf(long x0, long y0, long x1, long y1) {
        return GridConversion.km2Of(unownedClasses(x0, y0, x1, y1));
    }

    /** ...as plots by World's classes (GRASS, FOREST, FRESH, SALT, SAND). */
    long[] unownedClasses(long x0, long y0, long x1, long y1) {
        long[] cls = new long[5];
        x0 = Math.max(0, x0);
        y0 = Math.max(0, y0);
        x1 = Math.min(World.SIDE, x1);
        y1 = Math.min(World.SIDE, y1);
        if (x0 >= x1 || y0 >= y1) return cls;
        World world = World.of(seed);
        world.seaTheta();                          // built on this thread before any other reads it
        long tx0 = Math.floorDiv(x0, World.TILE), tx1 = Math.floorDiv(x1 - 1, World.TILE);
        long ty0 = Math.floorDiv(y0, World.TILE), ty1 = Math.floorDiv(y1 - 1, World.TILE);
        long rows = ty1 - ty0 + 1, tiles = rows * (tx1 - tx0 + 1);
        final long fx0 = x0, fy0 = y0, fx1 = x1, fy1 = y1;
        if (tiles >= PARALLEL_TILES) {
            long[][] byRow = new long[(int) rows][];
            java.util.stream.IntStream.range(0, (int) rows).parallel()
                    .forEach(j -> byRow[j] = rowClasses(world, ty0 + j, tx0, tx1, fx0, fy0, fx1, fy1));
            for (long[] r : byRow) for (int c = 0; c < 5; c++) cls[c] += r[c];
        } else {
            for (long ty = ty0; ty <= ty1; ty++) {
                long[] r = rowClasses(world, ty, tx0, tx1, x0, y0, x1, y1);
                for (int c = 0; c < 5; c++) cls[c] += r[c];
            }
        }
        return cls;
    }

    /** One row of tiles' unowned plots of the rectangle, by class. */
    private long[] rowClasses(World world, long ty, long tx0, long tx1, long x0, long y0, long x1, long y1) {
        long[] cls = new long[5];
        byte[] t = null;
        boolean[] owned = null;
        int n = World.TILE;
        for (long tx = tx0; tx <= tx1; tx++) {
            int cov = grid.cover(5, tx, ty);
            if (cov == LandGrid.ALL) continue;
            long px0 = tx * n, py0 = ty * n;
            boolean whole = px0 >= x0 && py0 >= y0 && px0 + n <= x1 && py0 + n <= y1;
            if (whole && cov == LandGrid.NONE) {
                long[] c = tileCounts(world, tx, ty);
                for (int k = 0; k < 5; k++) cls[k] += c[k];
                continue;
            }
            if (t == null) {
                t = new byte[n * n];
                owned = new boolean[n * n];
            }
            world.tileTerrain(tx, ty, t);
            if (cov == LandGrid.SOME) grid.tileFlags(tx, ty, owned);
            else Arrays.fill(owned, false);
            int i0 = (int) (Math.max(x0, px0) - px0), i1 = (int) (Math.min(x1, px0 + n) - px0);
            int j0 = (int) (Math.max(y0, py0) - py0), j1 = (int) (Math.min(y1, py0 + n) - py0);
            for (int j = j0; j < j1; j++) {
                for (int i = i0; i < i1; i++) {
                    int at = j * n + i;
                    if (!owned[at]) cls[t[at]]++;
                }
            }
        }
        return cls;
    }

    /* =====================================================================
       SAVE AND RESTORE (DataSave's landCentre, landCentreRects, landHoldings,
       landPartFields and landConverted; SAVE_FORMAT 32)
       ===================================================================== */

    /** The centre's record: its five areas, its sites and amounts, the legacy field's plot and sites, and the site's plot - CENTRE_FIELDS doubles. */
    public double[] centreState() {
        double[] s = new double[CENTRE_FIELDS];
        int i = 0;
        for (int a = 0; a < AREAS; a++) s[i++] = centreKm2[a];
        for (int k = 0; k < KINDS; k++) s[i++] = centreSites[k];
        for (int k = 0; k < KINDS; k++) s[i++] = centreAmounts[k];
        s[i++] = legacyX;
        s[i++] = legacyY;
        s[i++] = legacySites;
        s[i++] = siteX;
        s[i] = siteY;
        return s;
    }

    /** How wide the centre's record is: 24 (spec-grid 3, M3; 25 with a half-side before, format 31). */
    public static final int CENTRE_FIELDS = AREAS + 2 * KINDS + 3 + 2;

    /** How wide a centre rectangle's record is: x0, y0, x1, y1 - its plots, half-open. */
    public static final int RECT_FIELDS = 4;

    /** The centre's blocks, a record each: {x0, y0, x1, y1}. */
    public double[][] centreRectsState() {
        double[][] rows = new double[centreRects.size()][];
        for (int i = 0; i < rows.length; i++) {
            LandGrid.Fill f = centreRects.get(i);
            rows[i] = new double[] { f.x0(), f.y0(), f.x1(), f.y1() };
        }
        return rows;
    }

    /** The purchases, one record each in the order they were made (LandParcel.purchaseRow(), 31 wide). */
    public double[][] holdingsState() {
        double[][] rows = new double[purchases.size()][];
        for (int i = 0; i < rows.length; i++) {
            Purchase p = purchases.get(i);
            rows[i] = p.offer().purchaseRow(p.month(), p.paidLocal());
        }
        return rows;
    }

    /** The fields held in part, a record each: {kind, cell, index, then each owned site's index}. */
    public double[][] partFieldsState() {
        double[][] rows = new double[parts.size()][];
        for (int i = 0; i < rows.length; i++) {
            GridConversion.PartField p = parts.get(i);
            double[] r = new double[3 + p.owned().length];
            r[0] = p.kind().ordinal();
            r[1] = p.cell();
            r[2] = p.index();
            for (int k = 0; k < p.owned().length; k++) r[3 + k] = p.owned()[k];
            rows[i] = r;
        }
        return rows;
    }

    /** A converted city's purchase history (convertedHistory()), or null. */
    public double[][] convertedState() { return convertedHistory(); }

    /**
     * A city's land as saved (format 32): its centre's record (CENTRE_FIELDS
     * wide), its blocks, its holdings, its part fields and its history. The
     * grid is replayed from the rectangles - the centre's, then each
     * purchase's - and nothing is counted again: the books are as saved.
     * Null when the centre's record is missing or the wrong width; a record
     * of another width elsewhere is passed over.
     */
    public static CityLand restore(long seed, double[] centre, double[][] rects, double[][] holdings, double[][] partRows,
                                   double[][] history) {
        if (centre == null || centre.length != CENTRE_FIELDS) return null;
        CityLand land = new CityLand(seed, (long) centre[CENTRE_FIELDS - 2], (long) centre[CENTRE_FIELDS - 1]);
        int i = 0;
        for (int a = 0; a < AREAS; a++) land.centreKm2[a] = centre[i++];
        for (int k = 0; k < KINDS; k++) land.centreSites[k] = (int) centre[i++];
        for (int k = 0; k < KINDS; k++) land.centreAmounts[k] = centre[i++];
        land.legacyX = (long) centre[i++];
        land.legacyY = (long) centre[i++];
        land.legacySites = (int) centre[i];
        if (rects != null) {
            for (double[] r : rects) {
                if (r == null || r.length != RECT_FIELDS) continue;
                LandGrid.Fill f = new LandGrid.Fill((long) r[0], (long) r[1], (long) r[2], (long) r[3], CENTRE);
                land.centreRects.add(f);
                land.grid.fill(f.x0(), f.y0(), f.x1(), f.y1(), CENTRE);
            }
        }
        if (holdings != null) {
            for (double[] row : holdings) {
                LandParcel offer = LandParcel.fromPurchaseRow(row);
                if (offer == null) continue;
                land.grid.fill(offer.getX0(), offer.getY0(), offer.getX1(), offer.getY1(), land.purchases.size() + 1);
                land.purchases.add(new Purchase(offer, LandParcel.purchaseMonth(row), LandParcel.purchasePaid(row)));
            }
        }
        if (partRows != null) {
            for (double[] r : partRows) {
                if (r == null || r.length < 3 || r[0] < 0 || r[0] >= KINDS) continue;
                int[] owned = new int[r.length - 3];
                for (int k = 0; k < owned.length; k++) owned[k] = (int) r[3 + k];
                land.addPart(new GridConversion.PartField(Resource.values()[(int) r[0]], (int) r[1], (int) r[2], owned));
            }
        }
        land.converted = history == null ? null : deepCopy(history);
        land.recount();
        return land;
    }

    /** A copy, field for field: restore() of its own records - what the map's draft is drawn on (Game.MapDraft). */
    public CityLand copy() {
        return restore(seed, centreState(), centreRectsState(), holdingsState(), partFieldsState(), convertedState());
    }

    /** Whether two cities' land is the same, field for field. */
    public boolean same(CityLand o) {
        return o != null && seed == o.seed && siteX == o.siteX && siteY == o.siteY
                && Arrays.equals(centreState(), o.centreState()) && Arrays.deepEquals(centreRectsState(), o.centreRectsState())
                && Arrays.deepEquals(holdingsState(), o.holdingsState()) && Arrays.deepEquals(partFieldsState(), o.partFieldsState())
                && Arrays.deepEquals(convertedState(), o.convertedState()) && grid.sameAs(o.grid);
    }

    private static double[][] deepCopy(double[][] rows) {
        double[][] out = new double[rows.length][];
        for (int i = 0; i < rows.length; i++) out[i] = rows[i] == null ? null : rows[i].clone();
        return out;
    }
}
