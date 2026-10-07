package ham.citybuildersim;

import java.util.Arrays;
import java.util.List;

/**
 * The world a city is founded on: the same seed makes the same world, and the world is the one the design describes - its sea, lakes and forest, its founding site, its fields and its totals, and the terrain a tile and a region at a time.
 *
 * WHY THIS EXISTS (0.7.56, batch J1a; the project's spec-land.md 2.1 and 3).
 * World is a pure function of a seed, and every later batch stands on it: the
 * land office sells its ground (J1b), the water plants draw on its lakes
 * (J2), the map paints it (J3, J4), the wells pump its oil (K). When this was
 * written nothing in the model read it, so nothing else would have noticed
 * it drifting; a world that was not the same twice, or whose lakes covered
 * the wrong share of the land, would surface now as a land office that
 * disagreed with itself.
 *
 * IT FOUND ONE ALREADY. The design's lake and forest levels (0.695 and 0.595)
 * were percentiles taken at the prototype's samples, which sit on the lattice
 * of every octave of 2^9 plots and under; off the lattice they made 5.0% of the
 * land lakes and 24% forest, not 3.7% and 31%. Section 2 measures the world
 * where a player will see it, on an independent sample.
 *
 * What it has to prove, on the default world and two others (2026's nearest
 * coastal cell has no site, so its search hands on to the next):
 *   1. the same seed gives the same terrain, site, river and fields, and a
 *      world built twice is identical; World.of() shares one; two seeds differ;
 *   2. the sea is SEA_SHARE of the world within a point, land the rest; lakes
 *      are LAKE_SHARE and forest FOREST_SHARE of the land within half a point;
 *   3. the founding site passes its four tests, re-tested here from World's
 *      constants, and stands on dry ground; the river runs from its lake,
 *      fresh water all the way, to the sea, widening as it goes;
 *   4. each cell's fields sum exactly to its total, whole tonnes, and number
 *      its count; ore centred in the sea is rarer than oil, which keeps its
 *      first place;
 *   5. the totals pass is the same every time and takes at most 200 ms
 *      (measured 30); each resource's total is its density x MEAN_SITES x its
 *      amount a site x the land within 3%, and forest's timber is its area;
 *   6. a tile's terrain is the point function's on at least 99.5% of its
 *      plots, at no more than 0.5 ms a tile (measured 0.07); a region at a
 *      stride of one is the point function, and a far region's sea is the
 *      point function's at its pixels within three points.
 */
public class WorldCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** The default world, and two more: 77, and 2026, whose search hands on to a second cell. */
    static final long[] SEEDS = { Founding.DEFAULT_WORLD_SEED, 77, 2026 };

    /** Points in each world's independent sample: 200,000 (a lake share's error about 0.1 point). */
    static final int SAMPLE = 200_000;

    /** The spec's bound on the totals pass, in ms (measured 30). */
    static final double TOTALS_MS = 200;

    /** The spec's bound on a tile, in ms (measured 0.07). */
    static final double TILE_MS = 0.5;

    /** The share of a tile's plots its terrain must agree with the point function on. */
    static final double TILE_AGREES = 0.995;

    /** Cells, neither all sea nor all land, whose fields are drawn to see where ore and oil lie: 300 (about 100,000 iron fields). */
    static final int COASTAL_CELLS = 300;

    public static void main(String[] args) {
        for (long seed : SEEDS) world(seed);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void world(long seed) {
        System.out.println("\n=== the world of seed " + seed + " ===");
        long t0 = System.nanoTime();
        World w = new World(seed);
        double theta = w.seaTheta();
        System.out.printf("built in %.0f ms: the sea's level %.4f; the site at plot (%d, %d) after %d tests in %d cell(s);"
                        + " the river %d points, its lake %.1f plots across%n",
                (System.nanoTime() - t0) / 1e6, theta, w.foundingX(), w.foundingY(), w.siteChecks(), w.siteCells(),
                w.river().points(), 2 * w.river().lakeR());

        /* ---------------------------------------------------------------- 1 */
        System.out.println("--- 1. the same seed, the same world ---");
        World again = new World(seed);
        int cells = World.CELLS * World.CELLS;
        boolean cellsSame = true;
        for (int c = 0; c < cells; c++) {
            cellsSame &= again.cellLandShare(c) == w.cellLandShare(c) && again.cellForestShare(c) == w.cellForestShare(c);
        }
        int fc = World.cellOf(w.foundingX(), w.foundingY());
        boolean fieldsSame = true;
        for (Resource r : Resource.values()) fieldsSame &= again.fieldsInCell(fc, r).equals(w.fieldsInCell(fc, r));
        byte[] a = new byte[World.TILE * World.TILE], b = new byte[World.TILE * World.TILE];
        long ftx = w.foundingX() / World.TILE, fty = w.foundingY() / World.TILE;
        boolean tilesSame = true;
        for (long ty = fty - 2; ty <= fty + 2; ty++) for (long tx = ftx - 2; tx <= ftx + 2; tx++) {
            w.tileTerrain(tx, ty, a);
            again.tileTerrain(tx, ty, b);
            tilesSame &= Arrays.equals(a, b);
        }
        check("built twice: the sea's level to the last bit",
                Double.doubleToLongBits(again.seaTheta()) == Double.doubleToLongBits(theta));
        check("...every cell's land and forest", cellsSame);
        check("...the founding site", again.foundingX() == w.foundingX() && again.foundingY() == w.foundingY());
        check("...the river and its lake", again.river().same(w.river()));
        check("...the founding cell's fields of every resource", fieldsSame);
        check("...the 25 tiles round the site", tilesSame);
        check("...and the world's totals", Arrays.equals(again.computeTotals(), w.computeTotals()));
        check("World.of() hands every asker the same world", World.of(seed) == World.of(seed) && World.of(seed).seed() == seed);
        World other = new World(seed + 1);
        int differ = 0;
        for (long ty = fty - 2; ty <= fty + 2; ty++) for (long tx = ftx - 2; tx <= ftx + 2; tx++) {
            w.tileTerrain(tx, ty, a);
            other.tileTerrain(tx, ty, b);
            for (int i = 0; i < a.length; i++) if (a[i] != b[i]) differ++;
        }
        check("the next seed is another world: its site, its sea's level, its fields",
                (other.foundingX() != w.foundingX() || other.foundingY() != w.foundingY())
                        && other.seaTheta() != theta
                        && !other.fieldsInCell(fc, Resource.IRON).equals(w.fieldsInCell(fc, Resource.IRON)));
        check("...and the ground under this one's site (" + differ + " of 25,600 plots differ)", differ > 0);

        /* ---------------------------------------------------------------- 2 */
        System.out.println("--- 2. the sea, the lakes and the forest, where a player sees them ---");
        long h = World.mix(seed ^ 0x5A3B1EL);
        int sea = 0, land = 0, lake = 0, forest = 0, forestClass = 0;
        for (int i = 0; i < SAMPLE; i++) {
            h = World.mix(h);
            double x = World.unit(h) * World.SIDE;
            h = World.mix(h);
            double y = World.unit(h) * World.SIDE;
            if (w.elevation(x, y) < theta) { sea++; continue; }
            land++;
            if (w.lakeField(x, y) >= World.LAKE_THETA) lake++;
            if (w.forestField(x, y) >= World.FOREST_THETA) forest++;
            if (w.terrainAt((long) x, (long) y) == World.FOREST) forestClass++;
        }
        double seaShare = (double) sea / SAMPLE, lakeShare = (double) lake / land, forestShare = (double) forest / land;
        System.out.printf("   %,d points: sea %.2f%%; of the land, lake %.2f%% and forest %.2f%% (drawn as forest %.2f%%,"
                + " beaches and lakes aside); the sea pass's land %.2f%%%n", SAMPLE, 100 * seaShare, 100 * lakeShare,
                100 * forestShare, 100.0 * forestClass / land, 100 * w.landShare());
        check("the sea is SEA_SHARE of the world, within a point", Math.abs(seaShare - World.SEA_SHARE) <= .01);
        check("...and the sea pass's land the rest, within a point", Math.abs(w.landShare() - (1 - World.SEA_SHARE)) <= .01);
        check("lakes are LAKE_SHARE of the land, within half a point", Math.abs(lakeShare - World.LAKE_SHARE) <= .005);
        check("forest is FOREST_SHARE of the land, within half a point", Math.abs(forestShare - World.FOREST_SHARE) <= .005);

        /* ---------------------------------------------------------------- 3 */
        System.out.println("--- 3. the founding site and its river ---");
        double sx = w.foundingX() + 0.5, sy = w.foundingY() + 0.5;
        boolean dry = dryAt(w, sx, sy);
        for (int q = 0; q < 8; q++) {
            double ang = q * Math.PI / 4;
            dry &= dryAt(w, sx + World.SITE_DRY_PLOTS * StrictMath.cos(ang), sy + World.SITE_DRY_PLOTS * StrictMath.sin(ang));
        }
        int landRing = 0;
        for (int q = 0; q < World.SITE_LAND_SAMPLES; q++) {
            double ang = q * 2 * Math.PI / World.SITE_LAND_SAMPLES, r = World.SITE_LAND_RADII[q % World.SITE_LAND_RADII.length];
            if (w.elevation(sx + r * StrictMath.cos(ang), sy + r * StrictMath.sin(ang)) >= theta) landRing++;
        }
        boolean seaNear = false, seaTooNear = false;
        for (int q = 0; q < 16; q++) {
            double ang = q * Math.PI / 8, c = StrictMath.cos(ang), s = StrictMath.sin(ang);
            seaTooNear |= w.elevation(sx + World.SITE_SEA_NOT_WITHIN * c, sy + World.SITE_SEA_NOT_WITHIN * s) < theta;
            seaNear |= w.elevation(sx + World.SITE_SEA_WITHIN * c, sy + World.SITE_SEA_WITHIN * s) < theta;
        }
        double reach = World.SITE_IRON_KM * 1000 / World.PLOT_M, nearest = Double.MAX_VALUE;
        for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
            for (Deposit d : w.fieldsInCell(fc + dy * World.CELLS + dx, Resource.IRON)) {
                double ex = d.x() + 0.5 - sx, ey = d.y() + 0.5 - sy;
                nearest = Math.min(nearest, Math.sqrt(ex * ex + ey * ey));
            }
        }
        System.out.printf("   %d of %d points round it are land; the nearest iron field %.0f m away%n",
                landRing, World.SITE_LAND_SAMPLES, nearest * World.PLOT_M);
        check("the site is dry ground for SITE_DRY_PLOTS round", dry);
        check("...SITE_LAND_SHARE of the land within 5 km is land",
                landRing >= Math.ceil(World.SITE_LAND_SHARE * World.SITE_LAND_SAMPLES));
        check("...the sea within SITE_SEA_WITHIN plots and not within SITE_SEA_NOT_WITHIN", seaNear && !seaTooNear);
        check("...an iron field within SITE_IRON_KM", nearest <= reach);
        byte here = w.terrainAt(w.foundingX(), w.foundingY());
        check("...and the terrain there is dry", here == World.GRASS || here == World.FOREST || here == World.SAND);
        World.River river = w.river();
        double lx = river.lakeX() - sx, ly = river.lakeY() - sy, lakeAway = Math.sqrt(lx * lx + ly * ly);
        boolean fresh = true;
        for (int k = 0; k + 1 < river.points(); k++) {
            double mx = (river.xs()[k] + river.xs()[k + 1]) / 2, my = (river.ys()[k] + river.ys()[k + 1]) / 2;
            if (w.elevation(mx, my) < theta) continue;   // the mouth, in the sea
            fresh &= w.terrainAt((long) Math.floor(mx), (long) Math.floor(my)) == World.FRESH;
        }
        int last = river.points() - 1;
        check("the river starts at its lake, RIVER_LAKE_NEAR to +SPAN plots from the site",
                river.xs()[0] == river.lakeX() && river.ys()[0] == river.lakeY()
                        && lakeAway >= World.RIVER_LAKE_NEAR && lakeAway <= World.RIVER_LAKE_NEAR + World.RIVER_LAKE_SPAN);
        check("...the lake is fresh water", w.terrainAt((long) Math.floor(river.lakeX()), (long) Math.floor(river.lakeY())) == World.FRESH);
        check("...and so is the river, step by step", fresh);
        check("...widening from RIVER_HALF_WIDTH to + RIVER_WIDENS",
                river.halfWidths()[0] == World.RIVER_HALF_WIDTH
                        && Math.abs(river.halfWidths()[Math.max(0, last - 1)] - (World.RIVER_HALF_WIDTH + World.RIVER_WIDENS)) < 1e-12);
        check("...to the sea", w.elevation(river.xs()[last], river.ys()[last]) < theta);

        /* ---------------------------------------------------------------- 4 */
        System.out.println("--- 4. the fields: a cell's sum to its total ---");
        boolean exact = true, whole = true, counted = true;
        int ironFields = 0, ironSea = 0, oilFields = 0, oilSea = 0, coastal = 0;
        long hc = World.mix(seed ^ 0xF1E1D5L);
        int fieldsSeen = 0;
        // The nine cells round the site, then COASTAL_CELLS cells drawn from the world that are neither all sea nor all land.
        int[] sampled = new int[9 + COASTAL_CELLS];
        for (int k = 0; k < 9; k++) sampled[k] = fc + (k / 3 - 1) * World.CELLS + (k % 3 - 1);
        while (coastal < COASTAL_CELLS) {
            hc = World.mix(hc);
            int cell = (int) Long.remainderUnsigned(hc, cells);
            if (w.cellLandShare(cell) > 0 && w.cellLandShare(cell) < 1) sampled[9 + coastal++] = cell;
        }
        for (int n = 0; n < sampled.length; n++) {
            int cell = sampled[n];
            for (Resource r : Resource.values()) {
                if (!r.inFields()) {
                    counted &= w.fieldsInCell(cell, r).isEmpty() && w.cellFieldCount(cell, r) == 0;
                    continue;
                }
                List<Deposit> fields = w.fieldsInCell(cell, r);
                double sum = 0;
                for (Deposit d : fields) {
                    sum += d.amount();
                    whole &= d.amount() >= 0 && d.amount() == Math.rint(d.amount()) && d.sites() >= 1
                            && d.sites() <= World.MAX_SITES && d.cell() == cell && World.cellOf(d.x(), d.y()) == cell;
                    if (n >= 9 && r == Resource.IRON) { ironFields++; if (w.terrainAt(d.x(), d.y()) == World.SALT) ironSea++; }
                    if (n >= 9 && r == Resource.OIL) { oilFields++; if (w.terrainAt(d.x(), d.y()) == World.SALT) oilSea++; }
                }
                fieldsSeen += fields.size();
                exact &= sum == w.cellTotal(cell, r);
                counted &= fields.size() == w.cellFieldCount(cell, r);
            }
        }
        double ironSeaShare = (double) ironSea / ironFields, oilSeaShare = (double) oilSea / oilFields;
        System.out.printf("   %,d fields in the 9 cells round the site and %d coastal cells; centred in the sea: iron %.2f%%,"
                + " oil %.2f%%%n", fieldsSeen, coastal, 100 * ironSeaShare, 100 * oilSeaShare);
        check("each cell's fields sum exactly to its total", exact);
        check("...whole amounts, 1 to MAX_SITES sites, each in its own cell", whole);
        check("...as many as its count, and forest none (it is terrain)", counted);
        check("ore is drawn off the sea: under a quarter of oil's share centred in it", ironSeaShare < oilSeaShare / 4);

        /* ---------------------------------------------------------------- 5 */
        System.out.println("--- 5. the world's totals ---");
        double[] totals = w.computeTotals();
        double best = Double.MAX_VALUE;
        boolean same = true;
        for (int rep = 0; rep < 3; rep++) {
            long t = System.nanoTime();
            double[] again2 = w.computeTotals();
            best = Math.min(best, (System.nanoTime() - t) / 1e6);
            same &= Arrays.equals(again2, totals);
        }
        double landKm2 = w.landShare() * cells * World.CELL_KM2;
        double richness = World.RICHNESS_MIN + World.RICHNESS_SPAN / 2;
        boolean near = true;
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            double expected = r.fieldsPerKm2() * World.MEAN_SITES * r.amountPerSite() * richness * landKm2;
            double ratio = totals[r.ordinal()] / expected;
            System.out.printf("   %-8s %.4g %s (%.4g a km2 of land), %.4f of its expectation%n", r, totals[r.ordinal()],
                    r.unit() + "s", totals[r.ordinal()] / landKm2, ratio);
            near &= Math.abs(ratio - 1) <= .03;
        }
        double forestKm2 = totals[Resource.FOREST.ordinal()] / World.FOREST_M3_PER_KM2;
        System.out.printf("   FOREST   %.4g cubic metres, %.4g km2 of it: %.2f%% of the land; the pass took %.1f ms (best of 3)%n",
                totals[Resource.FOREST.ordinal()], forestKm2, 100 * forestKm2 / landKm2, best);
        check("the totals pass gives the same totals every time", same);
        check("...in at most 200 ms (measured 30)", best <= TOTALS_MS);
        check("each resource's total is its expectation within 3%", near);
        check("forest's timber is FOREST_M3_PER_KM2 over the forest drawn, within a point",
                Math.abs(forestKm2 / landKm2 - (double) forestClass / land) <= .01);

        /* ---------------------------------------------------------------- 6 */
        System.out.println("--- 6. the terrain a tile and a region at a time ---");
        int agree = 0, plots = 0;
        for (long ty = fty - 3; ty < fty + 3; ty++) for (long tx = ftx - 3; tx < ftx + 3; tx++) {
            w.tileTerrain(tx, ty, a);
            for (int i = 0; i < a.length; i++) {
                plots++;
                if (a[i] == w.terrainAt(tx * World.TILE + i % World.TILE, ty * World.TILE + i / World.TILE)) agree++;
            }
        }
        double tileBest = Double.MAX_VALUE;
        for (int rep = 0; rep < 6; rep++) {   // the first round warms the compiler
            long t = System.nanoTime();
            for (long ty = fty - 10; ty < fty + 10; ty++) for (long tx = ftx - 10; tx < ftx + 10; tx++) w.tileTerrain(tx, ty, a);
            if (rep > 0) tileBest = Math.min(tileBest, (System.nanoTime() - t) / 1e6 / 400);
        }
        System.out.printf("   %,d plots of 36 tiles: %.3f%% agree; a tile in %.3f ms (best of 5 rounds of 400)%n",
                plots, 100.0 * agree / plots, tileBest);
        check("a tile's terrain is the point function's on 99.5% of its plots", agree >= TILE_AGREES * plots);
        check("...at no more than 0.5 ms a tile (measured 0.07)", tileBest <= TILE_MS);
        int n = 64;
        byte[] region = new byte[n * n];
        long x0 = w.foundingX() - n / 2, y0 = w.foundingY() - n / 2;
        w.regionTerrain(x0, y0, 1, n, region);
        int regionAgree = 0;
        for (int i = 0; i < n * n; i++) if (region[i] == w.terrainAt(x0 + i % n, y0 + i / n)) regionAgree++;
        check("a region at a stride of one is the point function", regionAgree >= TILE_AGREES * n * n);
        n = 256;
        region = new byte[n * n];
        boolean farSea = true;
        for (int stride : new int[] { 64, 512 }) {
            x0 = w.foundingX() - (long) stride * n / 2;
            y0 = w.foundingY() - (long) stride * n / 2;
            w.regionTerrain(x0, y0, stride, n, region);
            int regionSea = 0, pointSea = 0;
            for (int i = 0; i < n * n; i++) {
                if (region[i] == World.SALT) regionSea++;
                long px = (long) Math.floor(x0 + (double) (i % n) * stride + stride * 0.5);
                long py = (long) Math.floor(y0 + (double) (i / n) * stride + stride * 0.5);
                if (w.terrainAt(px, py) == World.SALT) pointSea++;
            }
            System.out.printf("   a far region at %d plots a pixel: sea %.2f%%, the point function's %.2f%%%n", stride,
                    100.0 * regionSea / (n * n), 100.0 * pointSea / (n * n));
            farSea &= Math.abs(regionSea - pointSea) <= .03 * n * n;
        }
        check("...and a far region's sea is the point function's within 3 points", farSea);
    }

    /** Test 1's dry ground: land, and not a lake. */
    static boolean dryAt(World w, double x, double y) {
        return w.elevation(x, y) >= w.seaTheta() && w.lakeField(x, y) < World.LAKE_THETA;
    }
}
