package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The city's land as spec-land drew it, read-only: a square centre round the founding site and forty lanes of wedges pushed out band by band - the geometry CityLand sells along, and the ground a format-31 save holds.
 *
 * WHY THIS EXISTS (0.7.66, batch M2; the project's spec-grid.md 2.6 and 3).
 * The land moved onto a grid of square blocks (LandGrid, GridOffers): since
 * batch M3 (0.7.67) CityLand holds rectangles, and the lanes are gone from play.
 * But every save written from 0.7.57 on holds its ground as lanes - the
 * centre's half-side, forty frontiers and each purchase's band - and to put
 * that ground on the grid (GridConversion) something must still say which
 * plots it owned. So the lane geometry moved here, whole and unchanged, and
 * CityLand delegates to it (radius(), sideLane(), point(), bandKm2(),
 * outerRadius(), owns(), inCentre(), inBand()): nothing it computes moved by
 * a bit (ConversionCheck 1). Since 0.7.67 (batch M3) CityLand is the grid's
 * and sells along no lanes; this is the lanes' only reader, and J1b's
 * centre profile is kept here for its site search (LandConversion.site()).
 *
 * THE GEOMETRY (spec-land star 1). Every point is seen from the founding
 * site's plot centre: its side is N, E, S or W by the larger of |x| and |y|
 * (y runs south), its radius r = max(|x|, |y|), and its lane
 * floor((t + 1) x 5), where t is x/|y| or y/|x| read across the side, so lane
 * 0 is the left-hand one facing out. The city owns a point when r is within
 * the centre's half-side, or within its lane's frontier. A band of a lane
 * from r1 to r2 holds LANE_SHARE x (r2^2 - r1^2) plots, so forty equal lanes
 * make a square with straight frontiers.
 *
 * THE OLD GROUND (restore()). A format-31 save's three land records -
 * landCentre, landLanes and landPurchases - read as CityLand.restore() read
 * them until 0.7.66, field for field and with the same bounds an offer's
 * record was read with (LandParcel), into this class's own reader of the
 * purchase record, so batch M3 changed LandParcel and every save written
 * before it still reads. Its totals are added up in acquisition order, as CityLand.recount()
 * adds them, so they are the save's to the bit.
 */
public final class LegacyLand {

    /** Sides of the city, seen from the founding site: north, east, south and west, in that order - the larger of |x| and |y| says which (spec-land star 1). */
    public static final int SIDES = 4;

    /** Lanes on a side: ten wedges fanning out from the site, each with one offer standing (spec-land star 1). */
    public static final int LANES = 10;

    /** The sides' names, in their order. */
    static final String[] SIDE_NAMES = { "North", "East", "South", "West" };

    /** A lane's share of its side's r squared: a tenth - a side within radius r holds r^2 plots, so a band of a lane from r1 to r2 holds this x (r2^2 - r1^2). */
    public static final double LANE_SHARE = 1.0 / LANES;

    /** Where a record keeps its whole area, in square kilometres: CityLand.TOTAL, DRY, FRESH, SEA and FOREST, in that order. */
    public static final int AREAS = 5;

    /** How many resources a record keeps the sites and amounts of: Resource's seven, in its order. */
    public static final int KINDS = Resource.values().length;

    /** How wide a format-31 centre's record is: its half-side, its five areas, its sites, its amounts, the legacy field's plot and sites, and the site's plot - 25 (CityLand.CENTRE_FIELDS until 0.7.66; 24, without the half-side, since). */
    public static final int CENTRE_FIELDS = 1 + AREAS + 2 * KINDS + 3 + 2;

    /** How wide a format-31 purchase's record is: side, lane, r1, r2, the month, the price, what was paid here, the five areas, the sites, the amounts, and the offer's id and month listed - 28 (LandParcel.PURCHASE_FIELDS until 0.7.66; 31, a rectangle in place of the band, since). */
    public static final int PURCHASE_FIELDS = 7 + AREAS + 2 * KINDS + 2;

    /* =====================================================================
       THE GEOMETRY (spec-land star 1)
       ===================================================================== */

    /** A point's radius from the site: the larger of |dx| and |dy|, in plots. */
    public static double radius(double dx, double dy) { return Math.max(Math.abs(dx), Math.abs(dy)); }

    /** A point's side and lane, as side x LANES + lane: the side by the larger of |dx| and |dy| (dy south), the lane floor((t + 1) x LANES / 2) across it, lane 0 on the left facing out. */
    public static int sideLane(double dx, double dy) {
        double ax = Math.abs(dx), ay = Math.abs(dy), t;
        int side;
        if (ay >= ax) {
            if (dy < 0) { side = 0; t = dx / ay; }
            else        { side = 2; t = ay > 0 ? -dx / ay : 0; }
        } else {
            if (dx > 0) { side = 1; t = dy / ax; }
            else        { side = 3; t = -dy / ax; }
        }
        int lane = (int) Math.floor((t + 1) * LANES / 2);
        return side * LANES + Math.max(0, Math.min(LANES - 1, lane));
    }

    /** The point at radius r and lane coordinate t (-1 to 1, left to right facing out) on a side, as {dx, dy} from the site. */
    static double[] point(int side, double r, double t) {
        switch (side) {
            case 0:  return new double[] { t * r, -r };
            case 1:  return new double[] { r, t * r };
            case 2:  return new double[] { -t * r, r };
            default: return new double[] { -r, -t * r };
        }
    }

    /** What a band of a lane from r1 to r2 holds, in square kilometres: LANE_SHARE x (r2^2 - r1^2) plots. */
    public static double bandKm2(double r1, double r2) {
        return LANE_SHARE * (r2 * r2 - r1 * r1) * World.KM2_PER_PLOT;
    }

    /** The outer radius of a band of km2 square kilometres starting at r1: sqrt(r1^2 + km2 / (LANE_SHARE x a plot)). */
    public static double outerRadius(double r1, double km2) {
        return Math.sqrt(r1 * r1 + km2 / (LANE_SHARE * World.KM2_PER_PLOT));
    }

    /** Whether ground of centre half-side `half` and these lane frontiers (SIDES x LANES, side by side) owns the point (dx, dy) from its site: within the centre, or within its lane's frontier. */
    static boolean owns(double half, double[] frontier, double dx, double dy) {
        double r = radius(dx, dy);
        return r <= half || r <= frontier[sideLane(dx, dy)];
    }

    /** Whether a field is the centre's: its centre within L-infinity radius `half` of (x, y). */
    static boolean inCentre(Deposit d, long x, long y, double half) {
        return radius(d.x() - x, d.y() - y) <= half;
    }

    /** Whether a field is the band's of a lane from r1 to r2, seen from (x, y): its centre on that side and lane with r1 < r <= r2. */
    static boolean inBand(Deposit d, long x, long y, int side, int lane, double r1, double r2) {
        double dx = d.x() - x, dy = d.y() - y, r = radius(dx, dy);
        return r > r1 && r <= r2 && sideLane(dx, dy) == side * LANES + lane;
    }

    /* =====================================================================
       THE OLD GROUND: A FORMAT-31 SAVE'S CENTRE, LANES AND PURCHASES
       ===================================================================== */

    /**
     * One purchase as its record holds it: the band bought (side, lane, r1,
     * r2), the month bought, its price in thousands of US dollars, what the
     * treasury paid in local money, its five areas, its sites and amounts in
     * Resource's order, and the offer's id and month listed.
     */
    public record Bought(int side, int lane, double r1, double r2, int month, double priceUsd, double paidLocal,
                         double[] km2, int[] sites, double[] amounts, int id, int listedMonth) { }

    private final long seed, siteX, siteY;
    private final double centreHalf;
    private final double[] centreKm2 = new double[AREAS];
    private final int[] centreSites = new int[KINDS];
    private final double[] centreAmounts = new double[KINDS];
    private final long legacyX, legacyY;
    private final int legacySites;
    private final double[] frontier = new double[SIDES * LANES];
    private final List<Bought> bought = new ArrayList<>();
    private final double[] totalKm2 = new double[AREAS];
    private final long[] totalSites = new long[KINDS];
    private final double[] totalAmounts = new double[KINDS];

    private LegacyLand(long seed, double[] centre, double[] lanes) {
        this.seed = seed;
        int i = 0;
        centreHalf = centre[i++];
        for (int a = 0; a < AREAS; a++) centreKm2[a] = centre[i++];
        for (int k = 0; k < KINDS; k++) centreSites[k] = (int) centre[i++];
        for (int k = 0; k < KINDS; k++) centreAmounts[k] = centre[i++];
        legacyX = (long) centre[i++];
        legacyY = (long) centre[i++];
        legacySites = (int) centre[i++];
        siteX = (long) centre[i++];
        siteY = (long) centre[i];
        if (lanes != null && lanes.length == SIDES * LANES) System.arraycopy(lanes, 0, frontier, 0, SIDES * LANES);
        else java.util.Arrays.fill(frontier, centreHalf);
    }

    /**
     * A save's land as it holds it, on the world of `seed`: its centre's
     * record (CENTRE_FIELDS wide), its forty frontiers and its purchases' records
     * (PURCHASE_FIELDS wide; a record of another width is passed over, as
     * CityLand.restore() passes it). Null when the centre's record is missing
     * or the wrong width.
     */
    public static LegacyLand restore(long seed, double[] centre, double[] lanes, double[][] purchases) {
        if (centre == null || centre.length != CENTRE_FIELDS) return null;
        LegacyLand land = new LegacyLand(seed, centre, lanes);
        if (purchases != null) {
            for (double[] row : purchases) {
                Bought b = read(row);
                if (b != null) land.bought.add(b);
            }
        }
        land.recount();
        return land;
    }

    /** A purchase's record, read with the bounds LandParcel's constructor puts on an offer (nothing below zero); null when it is the wrong width. */
    static Bought read(double[] row) {
        if (row == null || row.length != PURCHASE_FIELDS) return null;
        double[] km2 = new double[AREAS];
        int[] sites = new int[KINDS];
        double[] amounts = new double[KINDS];
        int i = 7;
        for (int a = 0; a < AREAS; a++) km2[a] = Math.max(0, row[i++]);
        for (int k = 0; k < KINDS; k++) sites[k] = Math.max(0, (int) row[i++]);
        for (int k = 0; k < KINDS; k++) amounts[k] = Math.max(0, row[i++]);
        return new Bought((int) row[0], (int) row[1], row[2], row[3], (int) row[4], Math.max(0, row[5]), row[6],
                km2, sites, amounts, (int) row[i], (int) row[i + 1]);
    }

    /** Adds the totals up in acquisition order, the centre first: CityLand.recount()'s order, so the sums are the save's to the bit. */
    private void recount() {
        System.arraycopy(centreKm2, 0, totalKm2, 0, AREAS);
        for (int k = 0; k < KINDS; k++) {
            totalSites[k] = centreSites[k];
            totalAmounts[k] = centreAmounts[k];
        }
        for (Bought b : bought) {
            for (int a = 0; a < AREAS; a++) totalKm2[a] += b.km2()[a];
            for (int k = 0; k < KINDS; k++) {
                totalSites[k] += b.sites()[k];
                totalAmounts[k] += b.amounts()[k];
            }
        }
    }

    /** The seed of the world it lies on. */
    public long seed()                   { return seed; }

    /** The founding site, as a plot: east from the world's west edge... */
    public long siteX()                  { return siteX; }

    /** ...and south from its north edge. */
    public long siteY()                  { return siteY; }

    /** The centre's half-side, in plots from the site's centre. */
    public double centreHalf()           { return centreHalf; }

    /** One of the centre's areas, in square kilometres. */
    public double centreKm2(int area)    { return centreKm2[area]; }

    /** The centre's sites of a resource. */
    public int centreSites(Resource r)   { return centreSites[r.ordinal()]; }

    /** The centre's amount of a resource, in its unit. */
    public double centreAmount(Resource r) { return centreAmounts[r.ordinal()]; }

    /** A converted centre's legacy iron field: its plot east, -1 when there is none... */
    public long legacyX()                { return legacyX; }

    /** ...south... */
    public long legacyY()                { return legacyY; }

    /** ...and its sites. */
    public int legacySites()             { return legacySites; }

    /** A lane's frontier: how far out the save owned it, in plots from the site. */
    public double frontier(int side, int lane) { return frontier[side * LANES + lane]; }

    /** The purchases, in the order they were made. */
    public List<Bought> purchases()      { return Collections.unmodifiableList(bought); }

    /** One of the whole ground's areas, centre and purchases, in square kilometres, as the save's books have it. */
    public double totalKm2(int area)     { return totalKm2[area]; }

    /** The whole ground's sites of a resource. */
    public long totalSites(Resource r)   { return totalSites[r.ordinal()]; }

    /** The whole ground's amount of a resource, as listed. */
    public double totalAmount(Resource r) { return totalAmounts[r.ordinal()]; }

    /** How far out the ground reaches anywhere, in plots from the site: the centre's half-side or the farthest frontier. */
    public double reach() {
        double r = centreHalf;
        for (double f : frontier) r = Math.max(r, f);
        return r;
    }

    /** How far out the ground reaches everywhere: the centre's half-side or the nearest frontier, whichever is farther - every point within it is owned. */
    public double reachEverywhere() {
        double r = Double.MAX_VALUE;
        for (double f : frontier) r = Math.min(r, f);
        return Math.max(centreHalf, r);
    }

    /** Whether the ground owns the point (dx, dy) from the site: CityLand.owns()'s test. */
    public boolean owns(double dx, double dy) { return owns(centreHalf, frontier, dx, dy); }

    /** Whether the ground owns a plot, at its centre. */
    public boolean ownsPlot(long x, long y)   { return owns(x - siteX, y - siteY); }

    /**
     * How many plots of [x0, x1) x [y0, y1) the ground owns, each at its
     * centre: all of them when the farthest plot is within reachEverywhere(),
     * none when the nearest is past reach(), else counted plot by plot.
     */
    public long ownedPlots(long x0, long y0, long x1, long y1) {
        long dx0 = x0 - siteX, dx1 = x1 - 1 - siteX, dy0 = y0 - siteY, dy1 = y1 - 1 - siteY;
        double far = Math.max(Math.max(Math.abs(dx0), Math.abs(dx1)), Math.max(Math.abs(dy0), Math.abs(dy1)));
        double near = Math.max(dx0 > 0 ? dx0 : dx1 < 0 ? -dx1 : 0, dy0 > 0 ? dy0 : dy1 < 0 ? -dy1 : 0);
        if (far <= reachEverywhere()) return (x1 - x0) * (y1 - y0);
        if (near > reach()) return 0;
        long n = 0;
        for (long y = y0; y < y1; y++) for (long x = x0; x < x1; x++) if (ownsPlot(x, y)) n++;
        return n;
    }

    /** What the purchases cost, in thousands of US dollars as listed. */
    public double paidUsd() {
        double s = 0;
        for (Bought b : bought) s += b.priceUsd();
        return s;
    }

    /** ...and what the treasury paid for them, in local money. */
    public double paidLocal() {
        double s = 0;
        for (Bought b : bought) s += b.paidLocal();
        return s;
    }

    /* =====================================================================
       J1B'S CENTRE, KEPT FOR ITS SITE SEARCH (moved from CityLand, 0.7.67)

       An older save (format 30 or before) is put on the world at the site
       J1b's search finds (LandConversion.site()), which tests the square that
       would hold the city's dry ground by this profile; so the profile stays,
       unchanged, though no centre is drawn from it since the block grid.
       ===================================================================== */

    /**
     * Samples across a centre's profile from the site to twice the radius
     * its dry ground would need if all of it were dry: 1,024 - the stride is
     * one plot (every plot counted, a tile at a time) up to about 236 km2 of
     * dry ground, and coarser past it, so a profile is about a million
     * samples at any size (the design's 2,048 across its whole reach).
     */
    public static final int PROFILE_SPAN = 1024;

    /* -----------------------------------------------------------------
       THE CENTRE: A PROFILE RING BY RING ROUND THE SITE

       Dry ground is counted ring by ring (L-infinity) out from the site at a
       stride, and the square stops in the ring where the running sum reaches
       the target, the ring's area shared out in proportion: so the centre
       holds the dry ground asked for exactly, and its other areas are the
       same share of their rings. At a stride of one plot the profile reads
       every plot a tile at a time (World.tileTerrain()); past PROFILE_SPAN it
       reads plot centres at the stride (terrainAt()). A profile is kept per
       site and stride and grown outward as it is asked for more, and every
       ring it counts is the same however far it has been grown, so the
       centre is the same whoever asks first.
       ----------------------------------------------------------------- */

    /** A centre's half-side and its five areas. */
    record Centre(double half, double[] km2) { }

    /** The centre round (x, y) that holds dryKm2 of dry ground. */
    static Centre sizeCentre(World world, long x, long y, double dryKm2) {
        double[] km2 = new double[AREAS];
        if (!(dryKm2 > 0)) return new Centre(0, km2);
        int stride = strideFor(dryKm2);
        Profile p = profile(world, x, y, stride);
        double cell = (double) stride * stride * World.KM2_PER_PLOT;
        int rings = (int) Math.ceil(2 * idealHalf(dryKm2) / stride) + 2;
        while (true) {
            synchronized (p) {
                p.reach(rings);
                double dry = 0, fresh = 0, sea = 0, forest = 0;
                for (int k = 0; k < p.rings; k++) {
                    double d = p.dry[k] * cell;
                    if (d > 0 && dry + d >= dryKm2) {
                        double f = (dryKm2 - dry) / d;
                        double lo = k == 0 ? 0 : (k - 0.5) * stride, hi = (k + 0.5) * stride;
                        double half = Math.sqrt(lo * lo + f * (hi * hi - lo * lo));
                        km2[CityLand.TOTAL] = 4 * half * half * World.KM2_PER_PLOT;
                        km2[CityLand.DRY] = dryKm2;
                        km2[CityLand.FRESH] = fresh + f * p.fresh[k] * cell;
                        km2[CityLand.SEA] = sea + f * p.sea[k] * cell;
                        km2[CityLand.FOREST] = forest + f * p.forest[k] * cell;
                        return new Centre(half, km2);
                    }
                    dry += d;
                    fresh += p.fresh[k] * cell;
                    sea += p.sea[k] * cell;
                    forest += p.forest[k] * cell;
                }
                if ((double) p.rings * stride > World.SIDE) {
                    throw new IllegalStateException("the world holds less dry ground than " + dryKm2 + " km2");
                }
                rings = p.rings * 2;
            }
        }
    }

    /**
     * What lies within L-infinity radius `radius` of (x, y), on the profile
     * a centre of dryKm2 is sized from: its five areas, the ring the radius
     * falls in shared out by area. What LandConversion's tests read.
     */
    static double[] within(World world, long x, long y, double dryKm2, double radius) {
        int stride = strideFor(dryKm2);
        Profile p = profile(world, x, y, stride);
        double cell = (double) stride * stride * World.KM2_PER_PLOT;
        int last = (int) Math.floor(radius / stride + 0.5);
        double[] out = new double[AREAS];
        synchronized (p) {
            p.reach(last + 1);
            for (int k = 0; k <= last; k++) {
                double lo = k == 0 ? 0 : (k - 0.5) * stride, hi = (k + 0.5) * stride;
                double f = radius >= hi ? 1 : Math.max(0, (radius * radius - lo * lo) / (hi * hi - lo * lo));
                out[CityLand.DRY] += f * p.dry[k] * cell;
                out[CityLand.FRESH] += f * p.fresh[k] * cell;
                out[CityLand.SEA] += f * p.sea[k] * cell;
                out[CityLand.FOREST] += f * p.forest[k] * cell;
            }
        }
        out[CityLand.TOTAL] = 4 * radius * radius * World.KM2_PER_PLOT;
        return out;
    }

    /** The half-side a square of dryKm2 would have if all of it were dry, in plots. */
    static double idealHalf(double dryKm2) { return Math.sqrt(dryKm2 / World.KM2_PER_PLOT) / 2; }

    /** The profile's stride for a centre of dryKm2: one plot until twice the ideal radius passes PROFILE_SPAN / 2 plots, and then as many as keep PROFILE_SPAN samples across that radius. */
    static int strideFor(double dryKm2) {
        return (int) Math.max(1, Math.ceil(4 * idealHalf(dryKm2) / PROFILE_SPAN));
    }

    /** The ground round one site at one stride: samples of each kind ring by ring. */
    private static final class Profile {
        final World world;
        final long x, y;
        final int stride;
        long[] dry = new long[0], fresh = new long[0], sea = new long[0], forest = new long[0];
        int rings;

        Profile(World world, long x, long y, int stride) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.stride = stride;
        }

        /** Counts out to at least `want` rings, doubling. */
        void reach(int want) {
            if (want <= rings) return;
            int to = Math.max(want, rings * 2);
            dry = Arrays.copyOf(dry, to);
            fresh = Arrays.copyOf(fresh, to);
            sea = Arrays.copyOf(sea, to);
            forest = Arrays.copyOf(forest, to);
            if (stride == 1) countTiles(rings, to); else countPoints(rings, to);
            rings = to;
        }

        private void tally(byte c, int ring) {
            if (c == World.SALT) sea[ring]++;
            else if (c == World.FRESH) fresh[ring]++;
            else {
                dry[ring]++;
                if (c == World.FOREST) forest[ring]++;
            }
        }

        /** Every plot with a ring from `from` to `to` - 1, a tile at a time. */
        private void countTiles(int from, int to) {
            long reach = to - 1;
            long tx0 = Math.floorDiv(x - reach, World.TILE), tx1 = Math.floorDiv(x + reach, World.TILE);
            long ty0 = Math.floorDiv(y - reach, World.TILE), ty1 = Math.floorDiv(y + reach, World.TILE);
            byte[] t = new byte[World.TILE * World.TILE];
            for (long ty = ty0; ty <= ty1; ty++) {
                for (long tx = tx0; tx <= tx1; tx++) {
                    long px0 = tx * World.TILE, py0 = ty * World.TILE;
                    long far = Math.max(Math.max(Math.abs(px0 - x), Math.abs(px0 + World.TILE - 1 - x)),
                            Math.max(Math.abs(py0 - y), Math.abs(py0 + World.TILE - 1 - y)));
                    if (far < from) continue;
                    world.tileTerrain(tx, ty, t);
                    for (int i = 0; i < t.length; i++) {
                        long ring = Math.max(Math.abs(px0 + i % World.TILE - x), Math.abs(py0 + i / World.TILE - y));
                        if (ring >= from && ring < to) tally(t[i], (int) ring);
                    }
                }
            }
        }

        /** Every sample at the stride with a ring from `from` to `to` - 1, a plot's centre at a time. */
        private void countPoints(int from, int to) {
            int reach = to - 1;
            for (int j = -reach; j <= reach; j++) {
                int aj = Math.abs(j);
                for (int i = -reach; i <= reach; i++) {
                    if (aj < from && Math.abs(i) < from) {
                        i = from - 1;       // skip the counted middle of the row
                        continue;
                    }
                    tally(world.terrainAt(x + (long) i * stride, y + (long) j * stride), Math.max(aj, Math.abs(i)));
                }
            }
        }
    }

    private record ProfileKey(long seed, long x, long y, int stride) { }

    /** How many profiles are kept: 8, the city's and a few a conversion tried. */
    static final int PROFILES_KEPT = 8;

    private static final Map<ProfileKey, Profile> PROFILES = new LinkedHashMap<>(16, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<ProfileKey, Profile> eldest) {
            return size() > PROFILES_KEPT;
        }
    };

    private static Profile profile(World world, long x, long y, int stride) {
        synchronized (PROFILES) {
            return PROFILES.computeIfAbsent(new ProfileKey(world.seed(), x, y, stride),
                    k -> new Profile(world, x, y, stride));
        }
    }
}
