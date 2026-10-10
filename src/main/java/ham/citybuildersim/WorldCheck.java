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
 *   3. the founding site passes its three tests (since 0.7.99 no iron is
 *      asked of it: 8 holds that), re-tested here from World's constants,
 *      and stands on dry ground; the river runs from its lake, fresh water
 *      all the way, to the sea, widening as it goes;
 *   4. (since 0.7.99) each pool's fields sum exactly to its cells' totals,
 *      whole tonnes, each listed in the cell its centre is in, FIELD_SCALE to
 *      MAX_SITES sites, numbered from FIELD_INDEX_FROM, a cell's as many as
 *      its count; the old world's fields, which an older save's ground
 *      holds, as they were: each cell's sum exactly to its total; ore
 *      centred in the sea is rarer than oil, which keeps its first place;
 *   5. the totals pass is the same every time and takes at most 200 ms
 *      (measured 30); each resource's total is its density x MEAN_SITES x its
 *      amount a site x the land within 3%, and forest's timber is its area;
 *   6. a tile's terrain is the point function's on at least 99.5% of its
 *      plots, at no more than 0.5 ms a tile (measured 0.07); a region at a
 *      stride of one is the point function, and a far region's sea is the
 *      point function's at its pixels within three points;
 *   7. (0.7.79, batch O3; spec-oil 2.2, 2.7) the sea has a depth: the shelf's
 *      level is the same built twice and under the sea's, a plot is deeper
 *      than nothing exactly where it is sea, and on an independent sample
 *      SHELF_SHARE of the sea is within SHELF_BREAK_M, within half a point;
 *      an oil field's grade is the same every time it is drawn, each grade
 *      is GRADE_SHARES of the fields within two points, and the grades of a
 *      pool's oil add up to its total exactly (a cell's, to 0.7.98) - nothing
 *      stored, no tonne moved;
 *   8. (0.7.99, batch W1; Jerus, 2026-10-08) FEWER AND BIGGER DEPOSITS, on
 *      the default world: every pool holds a tenth of its old count of
 *      fields (FIELD_SCALE, its fraction drawn, at least one), so the world
 *      a tenth of the old world's of every resource; the pools' totals are
 *      the world's to the tonne, and the fields share them exactly (the
 *      sparse resources' whole world summed); a field is FIELD_SCALE of the
 *      old draws, FIELD_SCALE x MEAN_SITES sites on the mean within 3%; the
 *      fields lie in clusters - each one's nearest neighbour as near as the
 *      old world's were, nearer than a tenth as many spread evenly would
 *      lie; a founding asks no iron of its site (seed NO_IRON_SEED: its site
 *      has none of the old world's within SITE_IRON_KM, where the
 *      conversion's search, which still asks it, goes on); and the site
 *      table's first LEGACY_MAX_SITES places are the old one's, so an older
 *      save's sites stand where they stood.
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

    /** The square of cells round the site whose oil fields are graded (section 7): 95 a side, 9,025 cells - some 20,000 fields, a grade's share then within a third of a point (one standard error); 30 a side to 0.7.98, when a cell held ten times the fields (World.FIELD_SCALE). */
    static final int GRADE_CELLS = 95;

    /** A world whose first plot passing a founding's three tests has none of the old world's iron within World.SITE_IRON_KM (section 8): 14, the first such seed from 1 (fixW1-notes.md; 4127, 77 and 2026 found where they did). */
    static final long NO_IRON_SEED = 14;

    /** The square of cells round the site whose iron fields' nearest neighbours are measured (section 8): 6 a side, 36 cells. */
    static final int CLUSTER_CELLS = 6;

    /** How far the shelf's share of an independent sample of the sea may sit from SHELF_SHARE (spec-oil 4: half a point). */
    static final double SHELF_WITHIN = .005;

    /** How far each grade's share of the fields may sit from GRADE_SHARES (spec-oil 4: two points). */
    static final double GRADE_WITHIN = .02;

    public static void main(String[] args) {
        for (long seed : SEEDS) world(seed);
        fewerAndBigger();
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
        for (Resource r : Resource.values()) {
            fieldsSame &= again.fieldsInCell(fc, r).equals(w.fieldsInCell(fc, r))
                    && again.legacyFieldsInCell(fc, r).equals(w.legacyFieldsInCell(fc, r));
            int[] p = World.poolOf(fc, r);
            if (p != null) fieldsSame &= again.poolFields(r, p[0], p[1]).equals(w.poolFields(r, p[0], p[1]));
        }
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
        check("...the founding cell's fields of every resource, its pools' and the old world's", fieldsSame);
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
        int[] ironPool = World.poolOf(fc, Resource.IRON);
        check("the next seed is another world: its site, its sea's level, its fields",
                (other.foundingX() != w.foundingX() || other.foundingY() != w.foundingY())
                        && other.seaTheta() != theta
                        && !other.poolFields(Resource.IRON, ironPool[0], ironPool[1]).equals(w.poolFields(Resource.IRON, ironPool[0], ironPool[1])));
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
        double nearest = Double.MAX_VALUE, nearestOld = Double.MAX_VALUE;
        for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
            for (Deposit d : w.fieldsInCell(fc + dy * World.CELLS + dx, Resource.IRON)) {
                double ex = d.x() + 0.5 - sx, ey = d.y() + 0.5 - sy;
                nearest = Math.min(nearest, Math.sqrt(ex * ex + ey * ey));
            }
            for (Deposit d : w.legacyFieldsInCell(fc + dy * World.CELLS + dx, Resource.IRON)) {
                double ex = d.x() + 0.5 - sx, ey = d.y() + 0.5 - sy;
                nearestOld = Math.min(nearestOld, Math.sqrt(ex * ex + ey * ey));
            }
        }
        System.out.printf("   %d of %d points round it are land; the nearest iron field %s (the old world's %.0f m away)%n",
                landRing, World.SITE_LAND_SAMPLES, nearest == Double.MAX_VALUE ? "none in the nine cells round it"
                        : String.format("%.0f m away", nearest * World.PLOT_M), nearestOld * World.PLOT_M);
        check("the site is dry ground for SITE_DRY_PLOTS round", dry);
        check("...SITE_LAND_SHARE of the land within 5 km is land",
                landRing >= Math.ceil(World.SITE_LAND_SHARE * World.SITE_LAND_SAMPLES));
        check("...the sea within SITE_SEA_WITHIN plots and not within SITE_SEA_NOT_WITHIN", seaNear && !seaTooNear);
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
        System.out.println("--- 4. the fields: a pool's sum to its cells' totals (0.7.99); the old world's, a cell's ---");
        boolean exact = true, whole = true, counted = true, listed = true, oldExact = true, oldWhole = true;
        int ironFields = 0, ironSea = 0, oilFields = 0, oilSea = 0, coastal = 0, pools = 0;
        long hc = World.mix(seed ^ 0xF1E1D5L);
        int fieldsSeen = 0, oldSeen = 0;
        java.util.Set<Long> poolsSeen = new java.util.HashSet<>();
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
                    counted &= w.fieldsInCell(cell, r).isEmpty() && w.cellFieldCount(cell, r) == 0
                            && w.legacyFieldsInCell(cell, r).isEmpty() && w.legacyCellFieldCount(cell, r) == 0;
                    continue;
                }
                List<Deposit> fields = w.fieldsInCell(cell, r);
                for (int k = 0; k < fields.size(); k++) {
                    Deposit d = fields.get(k);
                    whole &= d.amount() >= 0 && d.amount() == Math.rint(d.amount()) && d.sites() >= World.FIELD_SCALE
                            && d.sites() <= World.MAX_SITES && d.cell() == cell && World.cellOf(d.x(), d.y()) == cell
                            && d.index() == World.FIELD_INDEX_FROM + k;
                    if (n >= 9 && r == Resource.IRON) { ironFields++; if (w.terrainAt(d.x(), d.y()) == World.SALT) ironSea++; }
                    if (n >= 9 && r == Resource.OIL) { oilFields++; if (w.terrainAt(d.x(), d.y()) == World.SALT) oilSea++; }
                }
                fieldsSeen += fields.size();
                counted &= fields.size() == w.cellFieldCount(cell, r);
                // The cell's pool, once: its fields add up to its cells' totals, each listed where its centre is.
                int[] p = World.poolOf(cell, r);
                if (poolsSeen.add(((long) p[1] * World.CELLS + p[0]) * 8 + r.ordinal())) {
                    pools++;
                    List<Deposit> mine = w.poolFields(r, p[0], p[1]);
                    double sum = 0, total = 0;
                    for (Deposit d : mine) sum += d.amount();
                    int b = World.poolCells(r);
                    for (int cy = p[1] * b; cy < Math.min(World.CELLS, p[1] * b + b); cy++) {
                        for (int cx = p[0] * b; cx < Math.min(World.CELLS, p[0] * b + b); cx++) total += w.cellTotal(cy * World.CELLS + cx, r);
                    }
                    exact &= sum == total && total == w.poolTotal(r, p[0], p[1]);
                    listed &= mine.size() == w.poolFieldCount(r, p[0], p[1]);
                }
                // The old world's fields of the cell, as they were.
                List<Deposit> old = w.legacyFieldsInCell(cell, r);
                double oldSum = 0;
                for (int k = 0; k < old.size(); k++) {
                    Deposit d = old.get(k);
                    oldSum += d.amount();
                    oldWhole &= d.amount() >= 0 && d.amount() == Math.rint(d.amount()) && d.sites() >= 1
                            && d.sites() <= World.LEGACY_MAX_SITES && d.cell() == cell && World.cellOf(d.x(), d.y()) == cell && d.index() == k;
                }
                oldSeen += old.size();
                oldExact &= oldSum == w.cellTotal(cell, r) && old.size() == w.legacyCellFieldCount(cell, r);
            }
        }
        double ironSeaShare = (double) ironSea / ironFields, oilSeaShare = (double) oilSea / oilFields;
        System.out.printf("   %,d fields (the old world's %,d) in the 9 cells round the site and %d coastal cells, %d pools; centred in"
                + " the sea: iron %.2f%%, oil %.2f%%%n", fieldsSeen, oldSeen, coastal, pools, 100 * ironSeaShare, 100 * oilSeaShare);
        check("each pool's fields sum exactly to its cells' totals (0.7.99)", exact);
        check("...every one listed in the cell its centre is in, the pool's count", listed);
        check("...whole amounts, FIELD_SCALE to MAX_SITES sites, numbered from FIELD_INDEX_FROM", whole);
        check("...a cell's as many as its count, and forest none (it is terrain)", counted);
        check("the old world's fields as they were: each cell's sum exactly to its total", oldExact);
        check("...whole amounts, 1 to LEGACY_MAX_SITES sites, numbered from 0", oldWhole);
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

        /* ---------------------------------------------------------------- 7 */
        System.out.println("--- 7. the sea's depth and the crude's grade (0.7.79) ---");
        double shelf = w.shelfTheta();
        long hs = World.mix(seed ^ 0x5E1FL);
        int seaPoints = 0, onShelf = 0, salt = 0, deep = 0, agreeSalt = 0;
        for (int i = 0; i < SAMPLE; i++) {
            hs = World.mix(hs);
            long x = (long) (World.unit(hs) * World.SIDE);
            hs = World.mix(hs);
            long y = (long) (World.unit(hs) * World.SIDE);
            double depth = w.depthAt(x, y);
            boolean isSalt = w.terrainAt(x, y) == World.SALT;
            if (isSalt) salt++;
            if (depth > 0) deep++;
            if (isSalt == depth > 0) agreeSalt++;
            if (depth > 0) {
                seaPoints++;
                if (depth <= World.SHELF_BREAK_M) onShelf++;
            }
        }
        double shelfShare = (double) onShelf / seaPoints;
        System.out.printf("   the shelf's level %.5f under the sea's %.5f; %,d points: %,d in the sea, %.3f%% of them within %.0f m%n",
                shelf, theta, SAMPLE, seaPoints, 100 * shelfShare, World.SHELF_BREAK_M);
        check("built twice: the shelf's level to the last bit, under the sea's",
                Double.doubleToLongBits(again.shelfTheta()) == Double.doubleToLongBits(shelf) && shelf < theta);
        check("a plot is deeper than nothing exactly where it is sea", agreeSalt == SAMPLE && salt == deep && deep > 0);
        check("SHELF_SHARE of the sea is within SHELF_BREAK_M, within half a point", Math.abs(shelfShare - World.SHELF_SHARE) <= SHELF_WITHIN);
        int[] grades = new int[Deposit.Grade.values().length];
        int graded = 0;
        boolean pure = true, sums = true;
        java.util.Set<Long> gradedPools = new java.util.LinkedHashSet<>();
        long fcx = w.foundingX() / World.CELL, fcy = w.foundingY() / World.CELL;
        for (long cy = fcy - GRADE_CELLS / 2; cy < fcy + GRADE_CELLS / 2; cy++) {
            for (long cx = fcx - GRADE_CELLS / 2; cx < fcx + GRADE_CELLS / 2; cx++) {
                if (cx < 0 || cy < 0 || cx >= World.CELLS || cy >= World.CELLS) continue;
                int cell = (int) (cy * World.CELLS + cx);
                List<Deposit> fields = w.fieldsInCell(cell, Resource.OIL), twice = again.fieldsInCell(cell, Resource.OIL);
                for (int k = 0; k < fields.size(); k++) {
                    Deposit d = fields.get(k);
                    Deposit.Grade g = d.grade();
                    grades[g.ordinal()]++;
                    graded++;
                    pure &= twice.get(k).grade() == g && new Deposit(d.kind(), d.cell(), d.index(), d.x(), d.y(), d.sites(), d.amount()).grade() == g;
                }
                int[] p = World.poolOf(cell, Resource.OIL);
                gradedPools.add(((long) p[1] << 32) | p[0]);
            }
        }
        // A pool's oil by grade adds up to its total (since 0.7.99; a cell's before): grades move no tonne.
        for (long key : gradedPools) {
            int px = (int) (key & 0xffffffffL), py = (int) (key >>> 32);
            double[] byGrade = new double[grades.length];
            for (Deposit d : w.poolFields(Resource.OIL, px, py)) byGrade[d.grade().ordinal()] += d.amount();
            double total = 0;
            for (double v : byGrade) total += v;
            sums &= total == w.poolTotal(Resource.OIL, px, py);
        }
        boolean shares = graded > 0;
        StringBuilder words = new StringBuilder();
        for (Deposit.Grade g : Deposit.Grade.values()) {
            double s = (double) grades[g.ordinal()] / graded;
            shares &= Math.abs(s - Deposit.GRADE_SHARES[g.ordinal()]) <= GRADE_WITHIN;
            words.append(String.format(" %s %.2f%%", g.name().toLowerCase(), 100 * s));
        }
        System.out.printf("   %,d oil fields in the %d x %d cells round the site:%s; %d pools summed by grade%n", graded, GRADE_CELLS, GRADE_CELLS,
                words, gradedPools.size());
        check("an oil field's grade is the same every time it is drawn", pure);
        check("...each grade GRADE_SHARES of the fields, within two points", shares);
        check("...and a pool's oil by grade adds up to its total exactly", sums);
    }

    /** Test 1's dry ground: land, and not a lake. */
    static boolean dryAt(World w, double x, double y) {
        return w.elevation(x, y) >= w.seaTheta() && w.lakeField(x, y) < World.LAKE_THETA;
    }
}
