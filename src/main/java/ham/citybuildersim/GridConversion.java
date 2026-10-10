package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A saved city's land put on the block grid: a format-31 save's lanes snapped to whole blocks with its fields deciding, or an older save's one figure drawn as a centre of blocks round its site to the plot - one converted holding, whose books are the plots drawn.
 *
 * WHY THIS EXISTS (0.7.66, batch M2; the project's spec-grid.md 2.6 and stars
 * 8, 11 and 12). Since batch M3 (0.7.67) the city's land is whole blocks of a grid lined
 * up with the world (LandGrid), and every city saved before then is put on it
 * once, at load. Jerus, 2026-10-07: converted cities snapped to blocks, the
 * books following the map; "the generation should only put what the city
 * has, not more not less". This is that conversion, pure: it reads a save's
 * ground (LegacyLand, or an older save's figure) and returns the converted
 * holding - its blocks, its five areas, its sites and amounts, and the
 * fields it holds in part. Since 0.7.67 (batch M3) LandConversion calls it at
 * load, and restate() draws a figure with fromFigure(). ConversionCheck holds
 * it on copies of the five saves the design was measured on.
 *
 * A FORMAT-31 SAVE (fromLanes(); spec-grid star 11). Its ground is lanes of
 * wedges (LegacyLand), snapped one level finer than the city's offers
 * (snapLevel(): 240 m for Jerus's city, whose offers are 480 m). The points
 * that decide are each field's centre plot, owned by the save or not, and -
 * in a save written from 0.7.58 to 0.7.63, which held a field site by site
 * (sharesFields()) - each site of a field it held only part of, at the plot
 * holding the site's centre (sitePlot()). A block holding points of both kinds
 * splits into its four quarters, down to the plot. Otherwise a block is the
 * city's when a point in it is the city's; not when a point in it is
 * another's; else when the save owned half its plots or more. So no field
 * changes hands, and a field held in part keeps exactly its own sites
 * (PartField). The save's legacy iron field, when it has one, is a point
 * of the city's.
 *
 * AN OLDER SAVE (fromFigure()). Format 30 and before carry one figure of dry
 * ground and a pool of iron. Round the site batch J1b's search finds
 * (LandConversion.site()), rings of blocks of the city's level are taken
 * round the site's block, the last block split into quarters down to the
 * plot, until they hold the save's dry ground to within a plot. The iron is
 * the save's, at least its mines standing and ordered; when the world laid
 * no iron field on the ground drawn, one legacy field stands on it, drawn as
 * J1b draws it (LandConversion.legacyPlot()). Every other resource is the
 * world's fields centred on the ground drawn, whole.
 *
 * THE BOOKS FOLLOW THE MAP (spec-grid star 8). The converted holding's five
 * areas are its drawn plots, counted one by one with World.tileTerrain() as
 * the map counts them (drawnClasses()) - not the save's figures - and its
 * forest's timber follows its forest's area. Its sites and amounts of the
 * resources in fields are the save's, summed in acquisition order exactly as
 * the save's books summed them; what it has taken out (E) is as saved.
 *
 * NO MONEY MOVES (spec-grid star 12). The snap redraws what was bought; it
 * neither buys nor sells, so the ground it adds is not charged and the ground
 * it drops is not refunded. What was paid stays on the save's purchase
 * records (LegacyLand.paidUsd(), paidLocal()).
 */
public final class GridConversion {

    private GridConversion() { }

    /** The holding the converted ground is on the grid: 0, the centre's (purchases made after it are 1, 2, ...). */
    public static final int CONVERTED = 0;

    /** The first build that held a field site by site, each site with the ground holding its own centre: 0.7.58 (batch J1c). */
    static final String SHARED_FROM = "0.7.58";

    /** ...and the last: 0.7.63. 0.7.57 and 0.7.64 on give a field whole to the ground holding its centre (spec-land star 12, batch L). */
    static final String SHARED_TO = "0.7.63";

    /** A billionth of a plot: a figure of ground within it of a whole number of plots is that number - square kilometres are stored as plots x World.KM2_PER_PLOT, which rounds. */
    static final double WHOLE_PLOT = 1e-9;

    /**
     * A field the converted ground holds only part of: its kind, the world
     * cell it was drawn in and its index there (which say which field it is,
     * Deposit), and the sites the save owned, by their index from 0, in
     * order. Its other sites are not the city's.
     */
    public record PartField(Resource kind, int cell, int index, int[] owned) { }

    /** The converted holding: what fromLanes() and fromFigure() return. */
    public static final class Result {
        final long siteX, siteY;
        final LandGrid grid = new LandGrid();
        final List<LandGrid.Fill> fills = new ArrayList<>();
        int level;
        boolean shared;
        final double[] km2 = new double[LegacyLand.AREAS];
        final long[] sites = new long[LegacyLand.KINDS];
        final double[] amounts = new double[LegacyLand.KINDS];
        final double[] extracted = new double[LegacyLand.KINDS];
        final List<PartField> parts = new ArrayList<>();
        long legacyX = -1, legacyY = -1;
        int legacySites;
        int takenForOwn, leftOutForOther, splits, conflicts;

        Result(long siteX, long siteY) {
            this.siteX = siteX;
            this.siteY = siteY;
        }

        /** The founding site it was drawn round, as a plot: east... */
        public long siteX()                 { return siteX; }

        /** ...and south. */
        public long siteY()                 { return siteY; }

        /** The ground on the grid: every plot holding CONVERTED. */
        public LandGrid grid()              { return grid; }

        /** The ground as rectangles to fill for CONVERTED, in order: the grid's leaves, so filling them again rebuilds it node for node. */
        public List<LandGrid.Fill> fills()  { return Collections.unmodifiableList(fills); }

        /** The level it was snapped at (a format-31 save), or the level of its centre's blocks (an older one). */
        public int level()                  { return level; }

        /** Whether the save held its fields site by site (written from 0.7.58 to 0.7.63). */
        public boolean shared()             { return shared; }

        /** One of its five areas, in square kilometres (CityLand.TOTAL, DRY, FRESH, SEA or FOREST): its drawn plots. */
        public double km2(int area)         { return km2[area]; }

        /** Its sites of a resource. */
        public long sites(Resource r)       { return sites[r.ordinal()]; }

        /** Its amount of a resource, in the resource's unit. */
        public double amount(Resource r)    { return amounts[r.ordinal()]; }

        /** What has been taken out of its ground of a resource (E), as saved. */
        public double extracted(Resource r) { return extracted[r.ordinal()]; }

        /** The fields it holds only part of, in the order they were met. */
        public List<PartField> parts()      { return Collections.unmodifiableList(parts); }

        /** Its legacy iron field's plot east, -1 when it has none... */
        public long legacyX()               { return legacyX; }

        /** ...south... */
        public long legacyY()               { return legacyY; }

        /** ...and its sites. */
        public int legacySites()            { return legacySites; }

        /** Blocks the save owned less than half of, taken because one of its fields decided them. */
        public int takenForOwn()            { return takenForOwn; }

        /** Blocks the save owned half or more of, left out because another's field decided them. */
        public int leftOutForOther()        { return leftOutForOther; }

        /** Blocks split into quarters because points of both kinds lay in them. */
        public int splits()                 { return splits; }

        /** Plots holding points of both kinds, which cannot split further: taken, the city's point first. */
        public int conflicts()              { return conflicts; }
    }

    /* =====================================================================
       WHICH SAVES HELD FIELDS SITE BY SITE
       ===================================================================== */

    /** A build's number as a.b.c read a x 1,000,000 + b x 1,000 + c; -1 when it is not three whole numbers. */
    static long buildNumber(String version) {
        if (version == null) return -1;
        String[] p = version.trim().split("\\.");
        if (p.length != 3) return -1;
        try {
            return Long.parseLong(p[0]) * 1_000_000 + Long.parseLong(p[1]) * 1_000 + Long.parseLong(p[2]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Whether a format-31 save written by this build held each field site by site: from SHARED_FROM to SHARED_TO; any other, or one whose build cannot be read, held fields whole. */
    public static boolean sharesFields(String gameVersion) {
        long v = buildNumber(gameVersion);
        return v >= buildNumber(SHARED_FROM) && v <= buildNumber(SHARED_TO);
    }

    /* =====================================================================
       A FORMAT-31 SAVE, SNAPPED (spec-grid star 11)
       ===================================================================== */

    /** The level a format-31 save's ground is snapped at: one finer than its offers' (LandGrid.levelFor() of every plot it owns) - 240 m for Jerus's city, whose offers are 480 m. */
    public static int snapLevel(LegacyLand old) {
        return LandGrid.levelFor(old.totalKm2(CityLand.TOTAL) / World.KM2_PER_PLOT) - 1;
    }

    /** The plot holding a field's k-th site's centre: the plot whose centre is nearest it, as the site is seen from plot centres (LegacyLand.owns()). */
    public static long[] sitePlot(Deposit d, int k) {
        double[] at = d.siteAt(k);
        return new long[] { (long) Math.floor(d.x() + at[0] + 0.5), (long) Math.floor(d.y() + at[1] + 0.5) };
    }

    /**
     * A format-31 save's ground on the grid: `old` its land, `gameVersion` the
     * build that wrote it (which says whether it held fields site by site) and
     * `extracted` what it had taken out of each resource (its depletion
     * record, E; null or short for none).
     */
    public static Result fromLanes(LegacyLand old, String gameVersion, double[] extracted) {
        World world = World.of(old.seed());
        Result out = new Result(old.siteX(), old.siteY());
        out.shared = sharesFields(gameVersion);
        out.level = snapLevel(old);
        long b = 1L << out.level;
        long reach = (long) Math.ceil(old.reach()) + b;
        long last = World.SIDE / b - 1;
        long bx0 = Math.max(0, Math.floorDiv(old.siteX() - reach, b)), bx1 = Math.min(last, Math.floorDiv(old.siteX() + reach, b));
        long by0 = Math.max(0, Math.floorDiv(old.siteY() - reach, b)), by1 = Math.min(last, Math.floorDiv(old.siteY() + reach, b));
        Map<Long, List<long[]>> points = decidingPoints(world, old, out, bx0 * b, by0 * b, (bx1 + 1) * b, (by1 + 1) * b);
        for (long by = by0; by <= by1; by++) {
            for (long bx = bx0; bx <= bx1; bx++) {
                List<long[]> here = points.get(blockKey(bx, by));
                snap(old, out, here == null ? List.of() : here, out.level, bx, by);
            }
        }
        books(world, out);
        for (Resource r : Resource.values()) {
            out.sites[r.ordinal()] = old.totalSites(r);
            out.amounts[r.ordinal()] = r.inFields() ? old.totalAmount(r) : 0;
        }
        out.amounts[Resource.FOREST.ordinal()] = Math.rint(out.km2[CityLand.FOREST] * World.FOREST_M3_PER_KM2);
        for (int k = 0; extracted != null && k < Math.min(extracted.length, LegacyLand.KINDS); k++) out.extracted[k] = extracted[k];
        if (old.legacySites() > 0) {
            out.legacyX = old.legacyX();
            out.legacyY = old.legacyY();
            out.legacySites = old.legacySites();
        }
        leaves(out);
        return out;
    }

    private static long blockKey(long bx, long by) { return bx * World.SIDE + by; }

    /**
     * The points that decide the snap, by the block of out.level holding
     * each: {x, y, 1 the save's or 0 another's}, for every field centred in
     * the box [x0, x1) x [y0, y1) - or, for a field the save held in part, each
     * of its sites - and the save's legacy field. The fields held in part are
     * added to out.parts.
     */
    private static Map<Long, List<long[]>> decidingPoints(World world, LegacyLand old, Result out, long x0, long y0, long x1, long y1) {
        Map<Long, List<long[]>> at = new HashMap<>();
        long b = 1L << out.level, sx = old.siteX(), sy = old.siteY();
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            double pad = out.shared ? Deposit.mostReach(r) + 1 : 0;
            for (int cell : CityLand.cellsUnder(x0 - pad, y0 - pad, x1 - 1 + pad, y1 - 1 + pad)) {
                for (Deposit d : CityLand.legacyFields(world, cell, r)) {
                    boolean centreIn = d.x() >= x0 && d.x() < x1 && d.y() >= y0 && d.y() < y1;
                    if (!out.shared) {
                        if (centreIn) point(at, b, d.x(), d.y(), old.ownsPlot(d.x(), d.y()));
                        continue;
                    }
                    // Site by site: a field none of whose sites the ground reaches is not the save's.
                    int in = 0;
                    boolean[] mine = new boolean[d.sites()];
                    if (LegacyLand.radius(d.x() - sx, d.y() - sy) <= old.reach() + d.reach() + 1) {
                        for (int k = 0; k < d.sites(); k++) {
                            double[] s = d.siteAt(k);
                            mine[k] = old.owns(d.x() + s[0] - sx, d.y() + s[1] - sy);
                            if (mine[k]) in++;
                        }
                    }
                    if (in > 0 && in < d.sites()) {
                        int[] owned = new int[in];
                        int n = 0;
                        for (int k = 0; k < d.sites(); k++) if (mine[k]) owned[n++] = k;
                        out.parts.add(new PartField(r, cell, d.index(), owned));
                        for (int k = 0; k < d.sites(); k++) {
                            long[] p = sitePlot(d, k);
                            if (p[0] >= x0 && p[0] < x1 && p[1] >= y0 && p[1] < y1) point(at, b, p[0], p[1], mine[k]);
                        }
                    } else if (centreIn) {
                        point(at, b, d.x(), d.y(), in == d.sites());
                    }
                }
            }
        }
        if (old.legacySites() > 0 && old.legacyX() >= x0 && old.legacyX() < x1 && old.legacyY() >= y0 && old.legacyY() < y1) {
            point(at, b, old.legacyX(), old.legacyY(), true);
        }
        return at;
    }

    private static void point(Map<Long, List<long[]>> at, long b, long x, long y, boolean own) {
        at.computeIfAbsent(blockKey(Math.floorDiv(x, b), Math.floorDiv(y, b)), k -> new ArrayList<>())
                .add(new long[] { x, y, own ? 1 : 0 });
    }

    /** Snaps one block of `level` holding the points `pts` (spec-grid star 11): split when they are of both kinds, else the city's by its point, not by another's, else by the save's half. */
    private static void snap(LegacyLand old, Result out, List<long[]> pts, int level, long bx, long by) {
        long b = 1L << level, x0 = bx * b, y0 = by * b;
        boolean own = false, not = false;
        for (long[] p : pts) {
            if (p[2] == 1) own = true; else not = true;
        }
        if (own && not && level > 0) {
            out.splits++;
            long half = b >> 1;
            for (int q = 0; q < 4; q++) {
                long cx = 2 * bx + (q & 1), cy = 2 * by + (q >> 1);
                long qx0 = cx * half, qy0 = cy * half;
                List<long[]> sub = new ArrayList<>();
                for (long[] p : pts) if (p[0] >= qx0 && p[0] < qx0 + half && p[1] >= qy0 && p[1] < qy0 + half) sub.add(p);
                snap(old, out, sub, level - 1, cx, cy);
            }
            return;
        }
        if (own && not) out.conflicts++;
        long o = old.ownedPlots(x0, y0, x0 + b, y0 + b);
        if (o == 0 && !own) return;
        boolean most = 2 * o >= b * b;
        if (own && !most) out.takenForOwn++;
        if (not && most && !own) out.leftOutForOther++;
        if (own || (!not && most)) out.grid.fill(x0, y0, x0 + b, y0 + b, CONVERTED);
    }

    /* =====================================================================
       AN OLDER SAVE: A CENTRE OF BLOCKS TO THE PLOT
       ===================================================================== */

    /**
     * An older save's ground on the grid (format 30 and before): round the
     * site (sx, sy) J1b's search found for it, rings of blocks of the city's
     * level, the last split down to the plot, holding dryKm2 of dry ground to
     * within a plot; its iron the save's sites - at least its mines standing
     * and ordered - and tonnes, with a legacy field when the world laid none
     * on the ground drawn; every other resource the world's fields centred on
     * it; nothing taken out yet. The old world's fields (to 0.7.98, World.
     * legacyFieldsInCell()): an older save's ground is the ground it had.
     */
    public static Result fromFigure(World world, long sx, long sy, double dryKm2, int ironSites, double ironTonnes,
                                    int minesCommitted) {
        return fromFigure(world, sx, sy, dryKm2, ironSites, ironTonnes, minesCommitted, true);
    }

    /** ...on the old world's fields when `legacy`, else the world's (0.7.99): what a restatement draws a city founded on 0.7.99 with (LandConversion.restate()). */
    public static Result fromFigure(World world, long sx, long sy, double dryKm2, int ironSites, double ironTonnes,
                                    int minesCommitted, boolean legacy) {
        Result out = new Result(sx, sy);
        out.level = LandGrid.levelFor(dryKm2 / World.KM2_PER_PLOT);
        long b = 1L << out.level;
        long target = (long) Math.ceil(dryKm2 / World.KM2_PER_PLOT - WHOLE_PLOT);
        long cbx = Math.floorDiv(sx, b), cby = Math.floorDiv(sy, b), last = World.SIDE / b - 1;
        Map<Long, byte[]> tiles = new HashMap<>();
        long[] got = { 0 };
        for (int ring = 0; got[0] < target; ring++) {
            if (ring > World.SIDE / b) throw new IllegalStateException("the world holds less dry ground than " + dryKm2 + " km2");
            for (long[] p : ringOf(cbx, cby, ring)) {
                if (p[0] < 0 || p[1] < 0 || p[0] > last || p[1] > last) continue;
                claimDry(world, tiles, out, out.level, p[0], p[1], target, got);
                if (got[0] >= target) break;
            }
        }
        books(world, out);
        boolean worldIron = false;
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            for (int cell : CityLand.cellsUnder(out.grid.minX(), out.grid.minY(), out.grid.maxX() - 1, out.grid.maxY() - 1)) {
                for (Deposit d : legacy ? CityLand.legacyFields(world, cell, r) : CityLand.fields(world, cell, r)) {
                    if (out.grid.owner(d.x(), d.y()) != CONVERTED) continue;
                    if (r == Resource.IRON) {
                        worldIron = true;
                        continue;
                    }
                    out.sites[r.ordinal()] += d.sites();
                    out.amounts[r.ordinal()] += d.amount();
                }
            }
        }
        int iron = Math.max(Math.max(0, ironSites), Math.max(0, minesCommitted));
        out.sites[Resource.IRON.ordinal()] = iron;
        out.amounts[Resource.IRON.ordinal()] = Math.max(0, ironTonnes);
        out.amounts[Resource.FOREST.ordinal()] = Math.rint(out.km2[CityLand.FOREST] * World.FOREST_M3_PER_KM2);
        if (!worldIron && iron > 0) {
            long reach = Math.max(Math.max(sx - out.grid.minX(), out.grid.maxX() - 1 - sx),
                    Math.max(sy - out.grid.minY(), out.grid.maxY() - 1 - sy));
            boolean reachesIt = reach >= LandConversion.LEGACY_FIELD_KM * 1000 / World.PLOT_M;
            long[] at = LandConversion.legacyPlot(world, world.seed(), sx, sy, reach,
                    (x, y) -> !reachesIt || out.grid.owner(x, y) == CONVERTED);
            out.legacyX = at[0];
            out.legacyY = at[1];
            out.legacySites = iron;
        }
        leaves(out);
        return out;
    }

    /** The blocks of one L-infinity ring round (cx, cy), in order: the north row west to east, the east column, the south row east to west, the west column. */
    static List<long[]> ringOf(long cx, long cy, int n) {
        List<long[]> out = new ArrayList<>();
        if (n == 0) {
            out.add(new long[] { cx, cy });
            return out;
        }
        for (long x = cx - n; x <= cx + n; x++) out.add(new long[] { x, cy - n });
        for (long y = cy - n + 1; y <= cy + n; y++) out.add(new long[] { cx + n, y });
        for (long x = cx + n - 1; x >= cx - n; x--) out.add(new long[] { x, cy + n });
        for (long y = cy + n - 1; y > cy - n; y--) out.add(new long[] { cx - n, y });
        return out;
    }

    /** Takes block (bx, by) of `level` whole when its dry plots fit under the target, else its quarters in turn (north-west, north-east, south-west, south-east), down to the plot. */
    private static void claimDry(World world, Map<Long, byte[]> tiles, Result out, int level, long bx, long by, long target, long[] got) {
        if (got[0] >= target) return;
        long b = 1L << level;
        long d = dryIn(world, tiles, bx * b, by * b, b);
        if (got[0] + d <= target || level == 0) {
            out.grid.fill(bx * b, by * b, bx * b + b, by * b + b, CONVERTED);
            got[0] += d;
            return;
        }
        for (int q = 0; q < 4 && got[0] < target; q++) {
            claimDry(world, tiles, out, level - 1, 2 * bx + (q & 1), 2 * by + (q >> 1), target, got);
        }
    }

    private static long dryIn(World world, Map<Long, byte[]> tiles, long x0, long y0, long b) {
        long n = 0;
        for (long y = y0; y < y0 + b; y++) {
            for (long x = x0; x < x0 + b; x++) {
                byte c = terrain(world, tiles, x, y);
                if (c != World.SALT && c != World.FRESH) n++;
            }
        }
        return n;
    }

    private static byte terrain(World world, Map<Long, byte[]> tiles, long x, long y) {
        long tx = Math.floorDiv(x, World.TILE), ty = Math.floorDiv(y, World.TILE);
        byte[] t = tile(world, tiles, tx, ty);
        return t[(int) ((y - ty * World.TILE) * World.TILE + (x - tx * World.TILE))];
    }

    private static byte[] tile(World world, Map<Long, byte[]> tiles, long tx, long ty) {
        return tiles.computeIfAbsent(tx * World.SIDE + ty, k -> {
            byte[] t = new byte[World.TILE * World.TILE];
            world.tileTerrain(tx, ty, t);
            return t;
        });
    }

    /* =====================================================================
       THE BOOKS FOLLOW THE MAP (spec-grid star 8)
       ===================================================================== */

    /**
     * The plots a grid's holdings own, by World's classes (GRASS, FOREST,
     * FRESH, SALT, SAND, indexed by the class), counted one by one from
     * World.tileTerrain(), as the map counts them: a leaf of a tile or more a
     * whole tile at a time, a smaller one plot by plot.
     */
    public static long[] drawnClasses(World world, LandGrid grid) {
        long[] cls = new long[5];
        Map<Long, byte[]> tiles = new HashMap<>();
        grid.leaves((level, x, y, h) -> {
            long side = 1L << level;
            long tx0 = Math.floorDiv(x, World.TILE), ty0 = Math.floorDiv(y, World.TILE);
            if (side >= World.TILE) {
                long n = side / World.TILE;
                for (long ty = ty0; ty < ty0 + n; ty++) {
                    for (long tx = tx0; tx < tx0 + n; tx++) {
                        byte[] t = tile(world, tiles, tx, ty);
                        for (byte c : t) cls[c]++;
                    }
                }
                return;
            }
            byte[] t = tile(world, tiles, tx0, ty0);
            int i0 = (int) (x - tx0 * World.TILE), j0 = (int) (y - ty0 * World.TILE);
            for (int j = j0; j < j0 + side; j++) for (int i = i0; i < i0 + side; i++) cls[t[j * World.TILE + i]]++;
        });
        return cls;
    }

    /** Five areas in square kilometres - CityLand.TOTAL, DRY, FRESH, SEA and FOREST - of plots counted by World's classes. */
    public static double[] km2Of(long[] cls) {
        long dry = cls[World.GRASS] + cls[World.FOREST] + cls[World.SAND];
        long all = dry + cls[World.FRESH] + cls[World.SALT];
        double[] km2 = new double[LegacyLand.AREAS];
        km2[CityLand.TOTAL] = all * World.KM2_PER_PLOT;
        km2[CityLand.DRY] = dry * World.KM2_PER_PLOT;
        km2[CityLand.FRESH] = cls[World.FRESH] * World.KM2_PER_PLOT;
        km2[CityLand.SEA] = cls[World.SALT] * World.KM2_PER_PLOT;
        km2[CityLand.FOREST] = cls[World.FOREST] * World.KM2_PER_PLOT;
        return km2;
    }

    private static void books(World world, Result out) {
        double[] k = km2Of(drawnClasses(world, out.grid));
        System.arraycopy(k, 0, out.km2, 0, k.length);
    }

    private static void leaves(Result out) {
        out.grid.leaves((level, x, y, h) -> out.fills.add(new LandGrid.Fill(x, y, x + (1L << level), y + (1L << level), h)));
    }
}
