package ham.citybuildersim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Counts land's plotted ground and redraws a fixture's centre to an area.
 *
 * WHY (HA1): the live ground helpers and setOwnedSqFt fixtures shared the
 * obsolete save converters; keep their drawing here after those loaders go.
 */
final class LandDrawing {
    private LandDrawing() { }

    /** The centre's holding id, before any purchases. */
    static final int CENTRE = 0;
    /** A billionth of a plot absorbs the rounding of plots into square kilometres. */
    static final double WHOLE_PLOT = 1e-9;
    /** A fixture's iron field stands at least a kilometre from its founding site. */
    static final double LEGACY_FIELD_KM = 1;
    /** Draws to find a dry plot for a fixture's iron field. */
    static final int LEGACY_DRAWS = 64;
    /** The deterministic stream used by the fixture's iron field. */
    private static final long LEGACY_STREAM = 0x1E6AC7L;

    /** Whether a hand-set area agrees with the plotted ground to within one plot. */
    static boolean sameGround(double ownedSqFt, double landDrySqFt) {
        return Math.abs(ownedSqFt - landDrySqFt) <= LandManager.sqFt(World.KM2_PER_PLOT) * (1 + 1e-12);
    }

    /** A point's distance from the site in the square grid's metric. */
    static double radius(double dx, double dy) { return Math.max(Math.abs(dx), Math.abs(dy)); }

    /** The plot nearest a field site's centre. */
    public static long[] sitePlot(Deposit d, int k) {
        double[] at = d.siteAt(k);
        return new long[] { (long) Math.floor(d.x() + at[0] + 0.5), (long) Math.floor(d.y() + at[1] + 0.5) };
    }

    /** A centre drawn to a fixture's area, with its books counted from those plots. */
    static final class Result {
        final long siteX, siteY;
        final LandGrid grid = new LandGrid();
        final List<LandGrid.Fill> fills = new ArrayList<>();
        int level;
        final double[] km2 = new double[CityLand.AREAS];
        final double[] relief = new double[CityLand.RELIEF_AREAS];
        final long[] sites = new long[Resource.values().length];
        final double[] amounts = new double[Resource.values().length];
        long legacyX = -1, legacyY = -1;
        int legacySites;
        Result(long x, long y) { siteX = x; siteY = y; }
        long siteX() { return siteX; }
        long siteY() { return siteY; }
        List<LandGrid.Fill> fills() { return fills; }
        double km2(int a) { return km2[a]; }
        double relief(int a) { return relief[a]; }
        long sites(Resource r) { return sites[r.ordinal()]; }
        double amount(Resource r) { return amounts[r.ordinal()]; }
        long legacyX() { return legacyX; }
        long legacyY() { return legacyY; }
        int legacySites() { return legacySites; }
    }

    /** Draws whole dry plots round the same site, preserving the fixture's iron. */
    public static Result draw(World world, long sx, long sy, double dryKm2, int ironSites, double ironTonnes,
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
                    if (out.grid.owner(d.x(), d.y()) != CENTRE) continue;
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
            boolean reachesIt = reach >= LandDrawing.LEGACY_FIELD_KM * 1000 / World.PLOT_M;
            long[] at = LandDrawing.legacyPlot(world, world.seed(), sx, sy, reach,
                    (x, y) -> !reachesIt || out.grid.owner(x, y) == CENTRE);
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
            out.grid.fill(bx * b, by * b, bx * b + b, by * b + b, CENTRE);
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
                if (World.dry(terrain(world, tiles, x, y))) n++;
            }
        }
        return n;
    }

    private static byte terrain(World world, Map<Long, byte[]> tiles, long x, long y) {
        long tx = Math.floorDiv(x, World.TILE), ty = Math.floorDiv(y, World.TILE);
        byte[] t = tile(world, tiles, tx, ty);
        return t[(int) ((y - ty * World.TILE) * World.TILE + (x - tx * World.TILE))];
    }

    /** A tile's relief beside its cover (tile()), kept in `rels` for the walk (ET1). */
    private static byte[] reliefTile(World world, Map<Long, byte[]> rels, long tx, long ty) {
        return rels.computeIfAbsent(tx * World.SIDE + ty, k -> {
            byte[] r = new byte[World.TILE * World.TILE];
            world.tileRelief(tx, ty, r);
            return r;
        });
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
     * FRESH, SALT, SAND, and on Earth since 0.7.127 ICE and DESERT and since
     * ET1 SHALLOW, indexed by the class, World.CLASSES of them), counted one by one from
     * World.tileTerrain(), as the map counts them: a leaf of a tile or more a
     * whole tile at a time, a smaller one plot by plot.
     */
    public static long[] drawnClasses(World world, LandGrid grid) {
        return drawnClasses(world, grid, null);
    }

    /**
     * ...and, when `relief` is given (CityLand.RELIEF_AREAS long), the plots' relief in the same pass (ET1): the
     * dry ground's FLAT to VERY_STEEP and the shallow sea, as plots (CityLand.tally()), beside the classes.
     */
    public static long[] drawnClasses(World world, LandGrid grid, long[] relief) {
        long[] cls = new long[World.CLASSES];
        Map<Long, byte[]> rels = relief == null ? null : new HashMap<>();
        Map<Long, byte[]> tiles = new HashMap<>();
        grid.leaves((level, x, y, h) -> {
            long side = 1L << level;
            long tx0 = Math.floorDiv(x, World.TILE), ty0 = Math.floorDiv(y, World.TILE);
            if (side >= World.TILE) {
                long n = side / World.TILE;
                for (long ty = ty0; ty < ty0 + n; ty++) {
                    for (long tx = tx0; tx < tx0 + n; tx++) {
                        byte[] t = tile(world, tiles, tx, ty);
                        if (rels == null) for (byte c : t) cls[c]++;
                        else {
                            byte[] r = reliefTile(world, rels, tx, ty);
                            for (int at = 0; at < t.length; at++) CityLand.tally(t, r, at, cls, relief, 0);
                        }
                    }
                }
                return;
            }
            byte[] t = tile(world, tiles, tx0, ty0);
            int i0 = (int) (x - tx0 * World.TILE), j0 = (int) (y - ty0 * World.TILE);
            byte[] r = rels == null ? null : reliefTile(world, rels, tx0, ty0);
            for (int j = j0; j < j0 + side; j++) {
                for (int i = i0; i < i0 + side; i++) {
                    int at = j * World.TILE + i;
                    if (r == null) cls[t[at]]++;
                    else CityLand.tally(t, r, at, cls, relief, 0);
                }
            }
        });
        return cls;
    }

    /**
     * Five areas in square kilometres - CityLand.TOTAL, DRY, FRESH, SEA and
     * FOREST - of plots counted by World's classes: the dry ground the
     * classes World.dry() names, the desert's with the grass, and (0.7.127)
     * ice in the whole area only; the sea's area is SALT's and SHALLOW's (ET1:
     * World.salt()). The classes a shorter count leaves off are none.
     */
    public static double[] km2Of(long[] cls) {
        long dry = 0, all = 0;
        for (int c = 0; c < cls.length; c++) {
            all += cls[c];
            if (World.dry(c)) dry += cls[c];
        }
        double[] km2 = new double[CityLand.AREAS];
        km2[CityLand.TOTAL] = all * World.KM2_PER_PLOT;
        km2[CityLand.DRY] = dry * World.KM2_PER_PLOT;
        km2[CityLand.FRESH] = cls[World.FRESH] * World.KM2_PER_PLOT;
        km2[CityLand.SEA] = (cls[World.SALT] + (cls.length > World.SHALLOW ? cls[World.SHALLOW] : 0)) * World.KM2_PER_PLOT;
        km2[CityLand.FOREST] = cls[World.FOREST] * World.KM2_PER_PLOT;
        return km2;
    }

    private static void books(World world, Result out) {
        long[] relief = new long[CityLand.RELIEF_AREAS];
        double[] k = km2Of(drawnClasses(world, out.grid, relief));
        System.arraycopy(k, 0, out.km2, 0, k.length);
        for (int i = 0; i < relief.length; i++) out.relief[i] = relief[i] * World.KM2_PER_PLOT;
    }

    private static void leaves(Result out) {
        out.grid.leaves((level, x, y, h) -> out.fills.add(new LandGrid.Fill(x, y, x + (1L << level), y + (1L << level), h)));
    }

    static long[] legacyPlot(World world, long seed, long sx, long sy, double reach,
                             java.util.function.BiPredicate<Long, Long> onGround) {
        double near = LEGACY_FIELD_KM * 1000 / World.PLOT_M;
        double half = Math.max(reach, near);
        long h = World.mix(seed ^ LEGACY_STREAM);
        long fx = sx + Math.round(near), fy = sy;
        for (int k = 0; k < LEGACY_DRAWS; k++) {
            h = World.mix(h);
            double dx = (2 * World.unit(h) - 1) * half;
            h = World.mix(h);
            double dy = (2 * World.unit(h) - 1) * half;
            if (dx * dx + dy * dy < near * near) continue;
            long x = sx + Math.round(dx), y = sy + Math.round(dy);
            byte c = world.terrainAt(x, y);
            if (World.salt(c) || c == World.FRESH) continue;
            if (!onGround.test(x, y)) continue;
            fx = x;
            fy = y;
            break;
        }
        return new long[] { fx, fy };
    }


    /** Redraws a hand-set area; a normal load never uses this fixture operation. */
    public static void restate(LandManager lm, double drySqFt) {
        CityLand land = lm.land();
        World world = World.of(land.key());
        int ironSites = (int) Math.min(Integer.MAX_VALUE, land.totalSites(Resource.IRON));
        double ironTonnes = land.totalAmount(Resource.IRON);
        // On the old world's fields when the city's centre holds them (0.7.99, CityLand.oldWorldHoldings()), else the world's.
        LandDrawing.Result r = LandDrawing.draw(world, land.siteX(), land.siteY(),
                Math.max(0, LandManager.km2(drySqFt)), ironSites, ironTonnes, 0, land.oldWorldHoldings() > 0);
        land.redraw(r);
        lm.restoreOwnedSqFt(drySqFt);
        lm.getMarket().attach(land);
        // ...the redrawn ground counted by landmass again before the shelf is listed (ET5): a restated centre may now reach another landmass, and the books, the unlinked sums and the offers' split must be of this land (a no-op on a generated world and on a city on one landmass).
        lm.landChanged();
        lm.getMarket().listMissing();
    }
}
