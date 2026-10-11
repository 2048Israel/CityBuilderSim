package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key holding the generated world as it was, and the game's founding, land and screen standing on an Earth world.
 *
 * WHY. E2 lays the real Earth's ground on the world's plots, finer than the
 * data by 15 times, and the finer detail is made from the seed: a rule that
 * kept the look of a coast and lost its share of sea would move every city's
 * books, and one that drew the same detail for every seed would make every
 * city founded on a spot the same. So the drawn ground is held to the data
 * cell by 15" cell, and to the seed's detail moving it and nothing more; and
 * because a generated world must reach exactly the code it reached before
 * the key, that is held too (WorldCheck holds the generated world itself,
 * unchanged).
 *
 * THE DATA. EarthCheck's fixture (earth/earth-1-fixture.bin, cut from
 * earth-1.bin by tools/earth/fixture.py) holds the 15" tiles of the study's
 * three windows - Rotterdam, Perth and Paris - and Fiji's four on the 180th
 * meridian, and the 4' bands they lie in; a window here is the world cell
 * about the spot, 61.44 km a side, the study's "61 x 61 km about each spot".
 * Every 1-degree tile it does not hold reads as no sea and no lake, so
 * nothing here is drawn beyond them. The full file's section runs when it is
 * named (-Dcbs.earth=<path>, EarthData.locate()), and says so in one line
 * when it is not.
 *
 * What this has to prove (spec-earth 8, E2):
 *   1. A GENERATED WORLD THROUGH ITS KEY IS THE WORLD IT WAS: World.of() of a
 *      generated key is World.of() of its seed, the same object; a world
 *      made from the key draws the same site, totals, tiles, lines and region
 *      as one made from the seed; an Earth world is kept apart, Earth's.
 *   2. THE SHARES ARE THE DATA'S: on each window, every whole 15" cell's sea
 *      share of the plots drawn is the data's within SHARE_POINTS in the
 *      mean (the study measured 0.39 to 1.49); the windows' fresh water and
 *      forest are printed against the data's.
 *   3. THE SAME KEY, THE SAME GROUND: two worlds made from one key draw every
 *      sampled tile byte for byte, and a plot, a line and a region at a
 *      stride of one are their tile's.
 *   4. ANOTHER SEED MOVES THE DETAIL, NOT THE DATA: a second seed changes at
 *      least SEED_CHANGES of a window's plots (the study 15.7 to 28.6%) and
 *      its water - sea and fresh - within SEED_WATER_POINTS (the study 0.1).
 *   5. A TILE IN NO MORE THAN TILE_MS (the study 0.09 to 0.20 ms), the median
 *      of TILE_ROUNDS rounds over each window's middle; wall clock, so run
 *      under the JVM lock.
 *   6. NO SEAM AT 180 DEGREES: Fiji's window, centred on the meridian, holds
 *      whole cells on both sides of it, and the cells beside the line on
 *      each side are the data's within SHARE_POINTS.
 *   7. THE RULE'S PIECES: every river vertex in a window lies on a fresh (or
 *      sea) plot; every small lake's point does, and the lakes clear of other
 *      water draw their own area within LAKE_AREA; every beach is a dry plot
 *      beside the sea in a 2' cell with bare ground, and Perth has beaches.
 *   8. THE GAME STANDS ON IT: each window's founding site, the snap's, passes
 *      the founding's test 1 within SNAP_PLOTS of the spot; the land a city is
 *      founded with there (LandManager.foundingLand()) holds its dry ground
 *      and, beside a river, its fresh water; a screen of MapCheck's
 *      SCREEN_TILES on Rotterdam, of a square city at the 5B copy's density,
 *      paints and rasters in no more than MapCheck.SCREEN_MS.
 *   9. THE FULL FILE, when named: the cells pass in no more than
 *      CELLS_PASS_MS (the study 0.37 to 0.45 s); the world's land, its cells'
 *      shares added, the land inside its square within LAND_WITHIN - every
 *      4' cell of the globe projected, the antipode's cap past the square
 *      left out; and the widest river EarthTerrain.WIDEST_RIVER_M, its half
 *      within EarthTerrain.WATER_PAD_DEG.
 *
 * Every fixture causes its condition: the windows hold coasts (Rotterdam,
 * Perth), rivers (the Maas and the Seine), small lakes, lakes of 10 km2 and
 * more (Rotterdam's delta), beaches (Perth) and the date line (Fiji).
 */
public class EarthTerrainCheck {

    /** Each whole 15" cell's sea share of the plots drawn is the data's within this, in points, in the mean over a window (spec-earth 8: 2; the study measured 0.39 to 1.49). */
    static final double SHARE_POINTS = 2;

    /** A second seed changes at least this share of a window's plots (spec-earth 8: 10%; the study measured 15.7 to 28.6%). */
    static final double SEED_CHANGES = 0.10;

    /** ...while a window's water, sea and fresh, moves no more than this, in points (spec-earth 8: 0.5; the study measured 0.1). */
    static final double SEED_WATER_POINTS = 0.5;

    /** A tile's ground in no more than this, ms (spec-earth 8: 0.3; the study measured 0.09 to 0.20). */
    static final double TILE_MS = 0.3;

    /** The cells pass in no more than this, ms (spec-earth 8: 1 s; the study measured 0.37 to 0.45 s). */
    static final double CELLS_PASS_MS = 1000;

    /** The window about each spot, in plots a side: a world cell, 61.44 km (spec-earth 3, "61 x 61 km about each spot"). */
    static final int WINDOW = World.CELL;

    /** The tiles a side of each window's middle that are timed: 32, 30.7 km. */
    static final int TIMED_TILES = 32;

    /** Rounds of the timed tiles after one to warm: 3, the median taken (the study's 3 repeats). */
    static final int TILE_ROUNDS = 3;

    /** The seed the windows are drawn with, and the second one section 4 draws them with. */
    static final long SEED = Founding.DEFAULT_WORLD_SEED, SECOND_SEED = Founding.DEFAULT_WORLD_SEED + 1;

    /** How close to the 180th meridian Fiji's cells are held, in degrees: 0.05, twelve 15" cells (5.5 km) each side. */
    static final double SEAM_DEG = 0.05;

    /** The small lakes clear of other water draw their own area within this share of it, over the windows: 10% (★E2: each blob covers its lake's area in expectation, and its fray varies it a few percent; measured in section 7). */
    static final double LAKE_AREA = 0.10;

    /** The world's land on the full file - its cells' shares added - is the land the 4' layers put inside the world's square within this share: half a percent (★E2: sixteen points a 61 km cell, each its 7.4 km 4' cell's, summed over 135,424 cells; measured 0.02%). */
    static final double LAND_WITHIN = 0.005;

    static int fails = 0;
    static final PrintStream out = System.out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** The fixture's windows: name, latitude, longitude. */
    record Spot(String name, double lat, double lon) { }

    public static void main(String[] args) throws Exception {
        out.println("--- the fixture ---");
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE), expect = EarthCheck.resource(EarthCheck.EXPECT);
        check("EarthCheck's fixture and its expected figures are beside the classes (" + EarthCheck.FIXTURE + ")", fixture != null && expect != null);
        if (fixture == null || expect == null) {
            out.println("  a build that copies only src/main/resources/*.json leaves earth/ behind: copy src/main/resources/earth/ beside the classes");
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        EarthData data = EarthData.of(fixture);
        String id = EarthTerrain.use(data);
        Map<String, Spot> spots = new LinkedHashMap<>();
        for (String line : new String(expect, StandardCharsets.UTF_8).split("\n")) {
            String[] f = line.split("\t", -1);
            if (f.length > 4 && f[1].equals("window")) spots.put(f[2], new Spot(f[2], Double.parseDouble(f[3]), Double.parseDouble(f[4])));
        }
        check("the fixture names the four windows: Rotterdam, Perth, Paris and Fiji", spots.keySet().equals(java.util.Set.of("Rotterdam", "Perth", "Paris", "Fiji")));
        double built = timed(EarthTerrain::quantile);
        double mean = 0, sq = 0;
        for (double v : EarthTerrain.quantile()) { mean += v; sq += v * v; }
        mean /= EarthTerrain.QUANTILE_SAMPLES;
        double sd = Math.sqrt(sq / EarthTerrain.QUANTILE_SAMPLES - mean * mean);
        out.printf("      the ranking table: %,d samples of the noise, built in %.0f ms; its mean %.4f, its standard deviation %.4f - the coast's fray%n",
                EarthTerrain.QUANTILE_SAMPLES, built, mean, sd);

        generated(id);
        List<Spot> three = List.of(spots.get("Rotterdam"), spots.get("Perth"), spots.get("Paris"));
        Map<String, Window> windows = new LinkedHashMap<>();
        out.println("\n--- 2. the shares are the data's: each whole 15\" cell's sea within " + SHARE_POINTS + " points in the mean ---");
        for (Spot s : three) {
            Window w = new Window(World.of(World.Key.earth(SEED, s.lat(), s.lon(), id)), data);
            windows.put(s.name(), w);
            report(s.name() + ": the plots' sea share of each whole 15\" cell is the data's within SHARE_POINTS in the mean", w.cells > 0 && w.seaPoints <= SHARE_POINTS,
                    String.format("%.2f points over %,d cells (the study %s); the window %.2f%% sea against the data's %.2f%%; fresh %.2f%% (lakes of 10 km2 and more %.2f%%);"
                            + " forest %.1f%% of the dry ground against the data's %.1f%%",
                            w.seaPoints, w.cells, s.name().equals("Rotterdam") ? "1.49" : s.name().equals("Perth") ? "0.39" : "Paris has no sea",
                            100 * w.share(World.SALT), 100 * w.dataSea, 100 * w.share(World.FRESH), 100 * w.dataLake, 100 * w.forestOfDry(), 100 * w.dataForest));
        }
        twice(three.get(0), id, windows.get("Rotterdam"));
        seeds(three, id, windows);
        tileTime(three, id);
        seam(spots.get("Fiji"), id, data);
        pieces(windows, data);
        stands(three, id);
        full(spots.get("Rotterdam"));

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static double timed(Runnable r) {
        long t0 = System.nanoTime();
        r.run();
        return (System.nanoTime() - t0) / 1e6;
    }

    /* =====================================================================
       A WINDOW: the world cell about a spot, every plot drawn and laid on the data's 15" cells
       ===================================================================== */

    static final class Window {
        final World world;
        final long x0, y0;
        final byte[] plots = new byte[WINDOW * WINDOW];
        final long[] classes = new long[5];
        /** Cells counted: the window's 15" cells, rows from rMin and columns (unwrapped about the spot) from cMin. */
        final long rMin, cMin;
        final int rows, cols;
        final int[] count, salt;
        final boolean[] edge;
        final int cells;
        final double seaPoints, dataSea, dataLake, dataForest;
        final long dryForestData;

        Window(World world, EarthData data) {
            this.world = world;
            EarthProj proj = world.earthTerrain().projection();
            x0 = EarthTerrain.spotPlot() - WINDOW / 2;
            y0 = EarthTerrain.spotPlot() - WINDOW / 2;
            double spotLon = proj.centreLon();
            double[] ll = new double[2];
            // The cell range from the corners, a cell of margin.
            long r0 = Long.MAX_VALUE, r1 = Long.MIN_VALUE, c0 = Long.MAX_VALUE, c1 = Long.MIN_VALUE;
            for (int k = 0; k < 4; k++) {
                proj.latLonOfPlot(x0 + (k & 1) * WINDOW, y0 + (k >> 1) * WINDOW, ll);
                long r = row(ll[0]), c = col(ll[1], spotLon);
                r0 = Math.min(r0, r); r1 = Math.max(r1, r); c0 = Math.min(c0, c); c1 = Math.max(c1, c);
            }
            rMin = r0 - 2;
            cMin = c0 - 2;
            rows = (int) (r1 - r0 + 5);
            cols = (int) (c1 - c0 + 5);
            count = new int[rows * cols];
            salt = new int[rows * cols];
            edge = new boolean[rows * cols];
            double[] coverForest = new double[1];
            long dry = 0;
            double forestData = 0;
            byte[] t = new byte[World.TILE * World.TILE];
            int tiles = WINDOW / World.TILE;
            for (int j = 0; j < tiles; j++) {
                for (int i = 0; i < tiles; i++) {
                    long tx = x0 / World.TILE + i, ty = y0 / World.TILE + j;
                    world.tileTerrain(tx, ty, t);
                    EarthProj.Blend b = proj.blend(tx * World.TILE, ty * World.TILE, World.TILE);
                    for (int p = 0; p < t.length; p++) {
                        int px = i * World.TILE + p % World.TILE, py = j * World.TILE + p / World.TILE;
                        plots[py * WINDOW + px] = t[p];
                        classes[t[p]]++;
                        b.latLon(tx * World.TILE + p % World.TILE + 0.5, ty * World.TILE + p / World.TILE + 0.5, ll);
                        int at = (int) ((row(ll[0]) - rMin) * cols + (col(ll[1], spotLon) - cMin));
                        count[at]++;
                        if (t[p] == World.SALT) salt[at]++;
                        if (px == 0 || py == 0 || px == WINDOW - 1 || py == WINDOW - 1) edge[at] = true;
                        if (t[p] == World.GRASS || t[p] == World.FOREST || t[p] == World.SAND) {
                            dry++;
                            forestData += (data.at("landcover", ll[0], ll[1]) & EarthData.LC_FOREST) / (double) EarthData.SHARE_LEVELS;
                        }
                    }
                }
            }
            dryForestData = dry;
            dataForest = dry > 0 ? forestData / dry : 0;
            int n = 0;
            double diff = 0, sea = 0, lake = 0;
            long all = 0;
            for (int a = 0; a < count.length; a++) {
                if (count[a] == 0) continue;
                long r = rMin + a / cols, c = Math.floorMod(cMin + a % cols, 360L * 240);
                int tile = EarthData.tile(89 - (int) (r / 240), (int) (c / 240) - 180);
                double s = data.cell("sea", tile, (int) (r % 240), (int) (c % 240)) / (double) EarthData.SHARE_LEVELS;
                double l = data.cell("lake", tile, (int) (r % 240), (int) (c % 240)) / (double) EarthData.SHARE_LEVELS;
                sea += s * count[a];
                lake += l * count[a];
                all += count[a];
                if (edge[a]) continue;
                n++;
                diff += Math.abs(salt[a] / (double) count[a] - s);
            }
            cells = n;
            seaPoints = n > 0 ? 100 * diff / n : Double.NaN;
            dataSea = sea / all;
            dataLake = lake / all;
        }

        /** A class's share of the window's plots. */
        double share(byte cls) { return classes[cls] / (double) plots.length; }

        /** Forest's share of the dry ground drawn. */
        double forestOfDry() {
            long dry = classes[World.GRASS] + classes[World.FOREST] + classes[World.SAND];
            return dry > 0 ? classes[World.FOREST] / (double) dry : 0;
        }

        /** The class of window plot (px, py). */
        byte at(int px, int py) { return plots[py * WINDOW + px]; }

        /** The share of a cell's plots of every cell index (count, salt) for the cells within `deg` of a longitude, and on which side. */
        static long row(double lat) { return (long) Math.floor((90 - lat) * 240); }

        static long col(double lon, double about) {
            double l = lon;
            if (l - about > 180) l -= 360;
            else if (l - about < -180) l += 360;
            return (long) Math.floor((l + 180) * 240);
        }
    }

    /* =====================================================================
       1. A GENERATED WORLD THROUGH ITS KEY
       ===================================================================== */

    static void generated(String id) {
        out.println("\n--- 1. a generated world through its key is the world it was ---");
        boolean same = true;
        for (long s : WorldCheck.SEEDS) same &= World.of(World.Key.generated(s)) == World.of(s) && !World.of(s).isEarth() && World.of(s).key().equals(World.Key.generated(s));
        check("World.of(Key.generated(seed)) is World.of(seed), the same generated world, for the default world, 77 and 2026", same);
        World bySeed = new World(SEED), byKey = new World(World.Key.generated(SEED));
        boolean site = bySeed.foundingX() == byKey.foundingX() && bySeed.foundingY() == byKey.foundingY() && bySeed.river().same(byKey.river())
                && Double.compare(bySeed.seaTheta(), byKey.seaTheta()) == 0 && Arrays.equals(bySeed.totals(), byKey.totals());
        check("a world made from the default seed's key has its site, river, sea's level and totals", site);
        byte[] a = new byte[World.TILE * World.TILE], b = new byte[World.TILE * World.TILE];
        boolean tiles = true;
        long tx0 = bySeed.foundingX() / World.TILE - 8, ty0 = bySeed.foundingY() / World.TILE - 8;
        for (int j = 0; j < 16; j++) {
            for (int i = 0; i < 16; i++) {
                bySeed.tileTerrain(tx0 + i, ty0 + j, a);
                byKey.tileTerrain(tx0 + i, ty0 + j, b);
                tiles &= Arrays.equals(a, b);
                bySeed.lineTerrain(tx0 + i, ty0 + j, (i & 1) == 0, j, a);
                byKey.lineTerrain(tx0 + i, ty0 + j, (i & 1) == 0, j, b);
                tiles &= Arrays.equals(Arrays.copyOf(a, World.TILE), Arrays.copyOf(b, World.TILE));
            }
        }
        byte[] ra = new byte[64 * 64], rb = new byte[64 * 64];
        bySeed.regionTerrain(tx0 * World.TILE, ty0 * World.TILE, 37, 64, ra);
        byKey.regionTerrain(tx0 * World.TILE, ty0 * World.TILE, 37, 64, rb);
        check("...and draws the 256 tiles about it, their lines and a far region byte for byte", tiles && Arrays.equals(ra, rb));
        World.Key earth = World.Key.earth(SEED, 51.9, 4.5, id);
        World e = World.of(earth);
        check("an Earth key's world is kept apart: Earth's, its own key, not the seed's generated world", e.isEarth() && e.key().equals(earth)
                && e != World.of(SEED) && World.of(earth) == e && e.seed() == SEED);
        boolean refused = false;
        try {
            e.depthAt(0, 0);
        } catch (UnsupportedOperationException x) {
            refused = true;
        }
        check("...and with no heights, an Earth world refuses the sea's depth rather than make one up", refused);
    }

    /* =====================================================================
       3. THE SAME KEY, THE SAME GROUND
       ===================================================================== */

    static void twice(Spot s, String id, Window w) {
        out.println("\n--- 3. the same key draws the same ground: " + s.name() + " twice, a plot, a line and a region its tile's ---");
        World.Key key = World.Key.earth(SEED, s.lat(), s.lon(), id);
        World one = new World(key), two = new World(key);
        byte[] a = new byte[World.TILE * World.TILE], b = new byte[World.TILE * World.TILE], line = new byte[World.TILE];
        int tiles = WINDOW / World.TILE;
        boolean same = true, plots = true, lines = true, fromWindow = true;
        long sampled = 0;
        for (int j = 0; j < tiles; j += 4) {
            for (int i = 0; i < tiles; i += 4) {
                long tx = w.x0 / World.TILE + i, ty = w.y0 / World.TILE + j;
                one.tileTerrain(tx, ty, a);
                two.tileTerrain(tx, ty, b);
                same &= Arrays.equals(a, b);
                // A line first, drawn alone (the tile not kept yet), then plots, from the tile kept.
                int at = (i + j) % World.TILE;
                two.lineTerrain(tx, ty, (i & 4) == 0, at, line);
                for (int k = 0; k < World.TILE; k++) lines &= line[k] == ((i & 4) == 0 ? a[k * World.TILE + at] : a[at * World.TILE + k]);
                for (int p = 0; p < a.length; p += 37) {
                    long x = tx * World.TILE + p % World.TILE, y = ty * World.TILE + p / World.TILE;
                    plots &= two.terrainAt(x, y) == a[p];
                    fromWindow &= w.at((int) (x - w.x0), (int) (y - w.y0)) == a[p];
                }
                sampled++;
            }
        }
        byte[] region = new byte[96 * 96];
        long rx = w.x0 + 700, ry = w.y0 + 900;
        two.regionTerrain(rx, ry, 1, 96, region);
        boolean reg = true;
        for (int p = 0; p < region.length; p++) reg &= region[p] == w.at((int) (rx - w.x0) + p % 96, (int) (ry - w.y0) + p / 96);
        check("two worlds made from one key draw " + sampled + " tiles of the window byte for byte, as the first world drew them", same && fromWindow);
        check("...and a line drawn alone (lineTerrain()) and a plot (terrainAt()) are their tile's, byte for byte", plots && lines);
        check("...and a region at a stride of one is every plot's own (96 x 96 plots)", reg);
    }

    /* =====================================================================
       4. ANOTHER SEED
       ===================================================================== */

    static void seeds(List<Spot> spots, String id, Map<String, Window> windows) {
        out.println("\n--- 4. another seed moves the detail, not the data: at least " + (int) (100 * SEED_CHANGES) + "% of the plots, the water within "
                + SEED_WATER_POINTS + " points ---");
        for (Spot s : spots) {
            Window a = windows.get(s.name());
            World b = World.of(World.Key.earth(SECOND_SEED, s.lat(), s.lon(), id));
            byte[] t = new byte[World.TILE * World.TILE];
            long changed = 0, waterA = 0, waterB = 0;
            int tiles = WINDOW / World.TILE;
            for (int j = 0; j < tiles; j++) {
                for (int i = 0; i < tiles; i++) {
                    b.tileTerrain(a.x0 / World.TILE + i, a.y0 / World.TILE + j, t);
                    for (int p = 0; p < t.length; p++) {
                        byte was = a.at(i * World.TILE + p % World.TILE, j * World.TILE + p / World.TILE);
                        if (was != t[p]) changed++;
                        if (was == World.SALT || was == World.FRESH) waterA++;
                        if (t[p] == World.SALT || t[p] == World.FRESH) waterB++;
                    }
                }
            }
            double share = changed / (double) a.plots.length, water = 100.0 * Math.abs(waterA - waterB) / a.plots.length;
            report(s.name() + ": seed " + SECOND_SEED + " changes at least SEED_CHANGES of the plots, the water within SEED_WATER_POINTS",
                    share >= SEED_CHANGES && water <= SEED_WATER_POINTS,
                    String.format("%.1f%% changed (the study 15.7-28.6%%); water %.2f%% against %.2f%%, %.3f points", 100 * share,
                            100.0 * waterA / a.plots.length, 100.0 * waterB / a.plots.length, water));
        }
    }

    /* =====================================================================
       5. A TILE'S TIME
       ===================================================================== */

    static void tileTime(List<Spot> spots, String id) {
        out.println("\n--- 5. a tile in no more than " + TILE_MS + " ms: the median of " + TILE_ROUNDS + " rounds over each window's middle " + TIMED_TILES
                + " x " + TIMED_TILES + " tiles ---");
        byte[] t = new byte[World.TILE * World.TILE];
        for (Spot s : spots) {
            World w = World.of(World.Key.earth(SEED, s.lat(), s.lon(), id));
            long tx0 = EarthTerrain.spotPlot() / World.TILE - TIMED_TILES / 2, ty0 = tx0;
            double[] ms = new double[TILE_ROUNDS];
            for (int round = -1; round < TILE_ROUNDS; round++) {
                long t0 = System.nanoTime();
                for (int j = 0; j < TIMED_TILES; j++) for (int i = 0; i < TIMED_TILES; i++) w.tileTerrain(tx0 + i, ty0 + j, t);
                if (round >= 0) ms[round] = (System.nanoTime() - t0) / 1e6 / (TIMED_TILES * TIMED_TILES);
            }
            Arrays.sort(ms);
            double median = ms[TILE_ROUNDS / 2];
            report(s.name() + ": a tile's ground in no more than TILE_MS", median <= TILE_MS,
                    String.format("%.3f ms a tile (rounds %.3f to %.3f; the study %s)", median, ms[0], ms[TILE_ROUNDS - 1],
                            s.name().equals("Rotterdam") ? "0.152-0.202" : "0.090-0.125"));
        }
    }

    /* =====================================================================
       6. NO SEAM AT 180 DEGREES
       ===================================================================== */

    static void seam(Spot fiji, String id, EarthData data) {
        out.println("\n--- 6. no seam at 180 degrees: Fiji's window, centred on the meridian ---");
        Window w = new Window(World.of(World.Key.earth(SEED, fiji.lat(), fiji.lon(), id)), data);
        // A cell's column, unwrapped about the spot's -180: west of the line its longitude is under -180.
        long line = (long) Math.floor((-180 + 180) * 240.0);
        int west = 0, east = 0, near = 0;
        double dWest = 0, dEast = 0;
        long westLand = 0, eastLand = 0, westPlots = 0, eastPlots = 0;
        for (int a = 0; a < w.count.length; a++) {
            if (w.count[a] == 0 || w.edge[a]) continue;
            long c = w.cMin + a % w.cols, r = w.rMin + a / w.cols;
            boolean isWest = c < line;
            if (isWest) { westPlots += w.count[a]; westLand += w.count[a] - w.salt[a]; } else { eastPlots += w.count[a]; eastLand += w.count[a] - w.salt[a]; }
            if (Math.abs(c + 0.5 - line) > SEAM_DEG * 240) continue;
            near++;
            long gc = Math.floorMod(c, 360L * 240);
            int tile = EarthData.tile(89 - (int) (r / 240), (int) (gc / 240) - 180);
            double s = data.cell("sea", tile, (int) (r % 240), (int) (gc % 240)) / (double) EarthData.SHARE_LEVELS;
            double d = Math.abs(w.salt[a] / (double) w.count[a] - s);
            if (isWest) { west++; dWest += d; } else { east++; dEast += d; }
        }
        double pw = west > 0 ? 100 * dWest / west : Double.NaN, pe = east > 0 ? 100 * dEast / east : Double.NaN;
        report("the window holds whole 15\" cells on both sides of the 180th meridian, beside it", west > 0 && east > 0,
                String.format("%,d cells within %.2f degrees: %,d west, %,d east; land %.1f%% west, %.1f%% east", near, SEAM_DEG, west, east,
                        100.0 * westLand / Math.max(1, westPlots), 100.0 * eastLand / Math.max(1, eastPlots)));
        report("...and the cells beside the line are the data's within SHARE_POINTS on each side: no seam", pw <= SHARE_POINTS && pe <= SHARE_POINTS,
                String.format("%.2f points west, %.2f east; the whole window %.2f over %,d cells", pw, pe, w.seaPoints, w.cells));
    }

    /* =====================================================================
       7. THE RULE'S PIECES
       ===================================================================== */

    static void pieces(Map<String, Window> windows, EarthData data) {
        out.println("\n--- 7. the rule's pieces: rivers on their lines, small lakes round their points, beaches where the data has bare ground ---");
        long vertices = 0, onRiver = 0, lakes = 0, atLake = 0;
        double clearArea = 0, clearDrawn = 0;
        int clear = 0;
        long sand = 0, sandOk = 0, perthSand = 0;
        for (Map.Entry<String, Window> e : windows.entrySet()) {
            Window w = e.getValue();
            EarthProj proj = w.world.earthTerrain().projection();
            double[] p = new double[2], ll = new double[2];
            List<Integer> tiles = new ArrayList<>();
            proj.latLonOfPlot(w.x0, w.y0, ll);
            double[] a = ll.clone();
            proj.latLonOfPlot(w.x0 + WINDOW, w.y0 + WINDOW, ll);
            for (int lat = (int) Math.floor(Math.min(a[0], ll[0])); lat <= (int) Math.floor(Math.max(a[0], ll[0])); lat++) {
                for (int lon = (int) Math.floor(Math.min(a[1], ll[1])); lon <= (int) Math.floor(Math.max(a[1], ll[1])); lon++) tiles.add(EarthData.tile(lat, lon));
            }
            int[] ts = tiles.stream().mapToInt(Integer::intValue).toArray();
            for (EarthData.Run run : data.riversIn(ts)) {
                for (int k = 0; k < run.size(); k++) {
                    if (!proj.plotOf(run.lat(k), run.lon(k), p)) continue;
                    int px = (int) Math.floor(p[0] - w.x0), py = (int) Math.floor(p[1] - w.y0);
                    if (px < 1 || py < 1 || px >= WINDOW - 1 || py >= WINDOW - 1) continue;
                    vertices++;
                    byte c = w.at(px, py);
                    if (c == World.FRESH || c == World.SALT) onRiver++;
                }
            }
            // Small lakes: each one's point, and those clear of other water - no sea, lake share or river within its reach.
            for (int t : ts) {
                EarthData.SmallLakes sl = data.smallLakes(t);
                for (int k = 0; k < sl.size(); k++) {
                    if (!proj.plotOf(sl.lat(k), sl.lon(k), p)) continue;
                    double r = Math.sqrt(sl.areaKm2(k) * 1e6 / Math.PI) / World.PLOT_M, reach = r * Math.sqrt(1 + EarthTerrain.BLOB_FRAY / 2) + 1;
                    int px = (int) Math.floor(p[0] - w.x0), py = (int) Math.floor(p[1] - w.y0);
                    if (px - reach < 1 || py - reach < 1 || px + reach >= WINDOW - 1 || py + reach >= WINDOW - 1) continue;
                    lakes++;
                    byte c = w.at(px, py);
                    if (c == World.FRESH || c == World.SALT) atLake++;
                    if (!clearOfWater(w, data, proj, p[0], p[1], reach)) continue;
                    clear++;
                    clearArea += Math.PI * r * r;
                    for (int y = (int) Math.floor(p[1] - reach); y <= (int) Math.ceil(p[1] + reach); y++) {
                        for (int x = (int) Math.floor(p[0] - reach); x <= (int) Math.ceil(p[0] + reach); x++) {
                            if (w.at((int) (x - w.x0), (int) (y - w.y0)) == World.FRESH) clearDrawn++;
                        }
                    }
                }
            }
            for (int py = 1; py < WINDOW - 1; py++) {
                for (int px = 1; px < WINDOW - 1; px++) {
                    if (w.at(px, py) != World.SAND) continue;
                    sand++;
                    if (e.getKey().equals("Perth")) perthSand++;
                    boolean besideSea = false;
                    for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) besideSea |= w.at(px + dx, py + dy) == World.SALT;
                    proj.latLonOfPlot(w.x0 + px + 0.5, w.y0 + py + 0.5, ll);
                    if (besideSea && (data.at("landcover", ll[0], ll[1]) & EarthData.LC_BEACH) != 0) sandOk++;
                }
            }
        }
        report("every river vertex in the windows lies on a fresh (or sea) plot: rivers on their lines", vertices > 0 && onRiver == vertices,
                String.format("%,d of %,d vertices", onRiver, vertices));
        report("every small lake's point in the windows is a fresh (or sea) plot", lakes > 0 && atLake == lakes, String.format("%,d of %,d lakes", atLake, lakes));
        double off = clearArea > 0 ? clearDrawn / clearArea - 1 : Double.NaN;
        report("the small lakes clear of other water draw their own area within LAKE_AREA", clear > 0 && Math.abs(off) <= LAKE_AREA,
                String.format("%,d lakes: %,.0f plots drawn for %,.0f (%+.1f%%)", clear, clearDrawn, clearArea, 100 * off));
        report("every beach is a dry plot beside the sea in a 2' cell with bare ground (LC_BEACH), and Perth has beaches", sand > 0 && sandOk == sand && perthSand > 0,
                String.format("%,d of %,d beach plots; Perth's %,d", sandOk, sand, perthSand));
    }

    /** Whether no sea, lake share or river reaches within `reach` plots of (x, y): the data's sea and lake there nought, and no plot drawn fresh by a river (a river vertex within reach plus its half-width). */
    static boolean clearOfWater(Window w, EarthData data, EarthProj proj, double x, double y, double reach) {
        double[] ll = new double[2];
        for (int q = 0; q < 16; q++) {
            double a = q * Math.PI / 8;
            for (double f : new double[] { 0, 0.5, 1 }) {
                if (!proj.latLonOfPlot(x + f * reach * StrictMath.cos(a), y + f * reach * StrictMath.sin(a), ll)) return false;
                if (data.at("sea", ll[0], ll[1]) > 0 || data.at("lake", ll[0], ll[1]) > 0) return false;
                if (data.at("sea", ll[0] + 0.0042, ll[1]) > 0 || data.at("lake", ll[0] - 0.0042, ll[1]) > 0) return false;
            }
        }
        int t = EarthData.tileAt(ll[0], ll[1]);
        double[] p = new double[2];
        for (EarthData.Run run : data.rivers(t)) {
            for (int k = 0; k < run.size(); k++) {
                if (!proj.plotOf(run.lat(k), run.lon(k), p)) continue;
                double h = run.width(k) / 2.0 / World.PLOT_M + 2;
                if (Math.hypot(p[0] - x, p[1] - y) < reach + h + 60) return false;
            }
        }
        return true;
    }

    /* =====================================================================
       8. THE GAME STANDS ON IT
       ===================================================================== */

    static void stands(List<Spot> spots, String id) {
        out.println("\n--- 8. the game stands on it: the snap, the founding land and the screen ---");
        double dryKm2 = LandManager.km2(LandManager.STARTING_SQ_FT);
        for (Spot s : spots) {
            World.Key key = World.Key.earth(SEED, s.lat(), s.lon(), id);
            World w = World.of(key);
            long sx = w.foundingX(), sy = w.foundingY();
            double off = Math.hypot(sx - EarthTerrain.spotPlot(), sy - EarthTerrain.spotPlot()) * World.PLOT_M;
            boolean test = dryPlot(w, sx, sy);
            for (int q = 0; q < 8; q++) {
                double a = q * Math.PI / 4;
                test &= dryPlot(w, (long) Math.floor(sx + 0.5 + World.SITE_DRY_PLOTS * StrictMath.cos(a)), (long) Math.floor(sy + 0.5 + World.SITE_DRY_PLOTS * StrictMath.sin(a)));
            }
            report(s.name() + ": the founding site, the snap's, is dry with eight dry points SITE_DRY_PLOTS round, within SNAP_PLOTS of the spot",
                    test && off <= EarthTerrain.SNAP_PLOTS * World.PLOT_M,
                    String.format("plot (%d, %d), %.0f m from the spot after %,d plots tested (the study's snap: %s)", sx, sy, off, w.siteChecks(),
                            s.name().equals("Rotterdam") ? "510 m" : "0 m"));
            long t0 = System.nanoTime();
            CityLand land = LandManager.foundingLand(key);
            double ms = (System.nanoTime() - t0) / 1e6;
            boolean dry = land.centreKm2(CityLand.DRY) >= dryKm2 && land.key().equals(key) && land.siteX() == sx && land.siteY() == sy;
            boolean freshWanted = !s.name().equals("Perth");
            boolean fresh = land.centreKm2(CityLand.FRESH) >= LandManager.FOUNDING_FRESH_KM2;
            report(s.name() + ": the land a city is founded with there holds its dry ground" + (freshWanted ? " and, beside its river, its fresh water" : ""),
                    dry && (!freshWanted || fresh),
                    String.format("%.3f km2 dry for %.3f, %.3f km2 fresh for %.3f, %.3f sea, %d blocks; %.0f ms", land.centreKm2(CityLand.DRY), dryKm2,
                            land.centreKm2(CityLand.FRESH), LandManager.FOUNDING_FRESH_KM2, land.centreKm2(CityLand.SEA), land.centreRects().size(), ms));
        }
        screen(spots.get(0), id);
    }

    static boolean dryPlot(World w, long x, long y) {
        byte t = w.terrainAt(x, y);
        return t != World.SALT && t != World.FRESH;
    }

    /** MapCheck 5's screen on Earth: SCREEN_ACROSS x SCREEN_DOWN tiles about the site of a square city at the 5B copy's density, each tile's ground read from the world, painted and rastered. */
    static void screen(Spot s, String id) {
        World.Key key = World.Key.earth(SEED, s.lat(), s.lon(), id);
        World w = World.of(key);
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        double k = MapCheck.TIMES[1];
        long[] c = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(MapCheck.JERUS_COUNTS[t] * k);
        for (BuildingVisual.Type t : types) if (t != null && t.site() != null && t.id() < c.length) c[t.id()] = 0;
        int side = (int) Math.ceil(Math.sqrt(MapCheck.groundKm2(types) * k / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        int capacity = (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        long t0 = System.nanoTime();
        CityMap m = CityMap.square(key, w.foundingX(), w.foundingY(), types, side, capacity, c);
        double built = (System.nanoTime() - t0) / 1e6;
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int[] img = new int[(World.TILE * MapCheck.PX) * (World.TILE * MapCheck.PX)];
        long tx0 = Math.floorDiv(w.foundingX(), World.TILE) - MapCheck.SCREEN_ACROSS / 2, ty0 = Math.floorDiv(w.foundingY(), World.TILE) - MapCheck.SCREEN_DOWN / 2;
        double best = Double.MAX_VALUE, cold = 0;
        long drawn = 0;
        long[] classes = new long[5];
        for (int round = 0; round < MapCheck.WARM_ROUNDS + MapCheck.TIMED_ROUNDS; round++) {
            long n = 0;
            long t1 = System.nanoTime();
            for (int j = 0; j < MapCheck.SCREEN_DOWN; j++) {
                for (int i = 0; i < MapCheck.SCREEN_ACROSS; i++) {
                    m.tileInput(tx0 + i, ty0 + j, in);
                    TilePainter.paint(in, p);
                    TileRaster.raster(in, p, MapCheck.PX, img);
                    n += p.buildings;
                    if (round == 0) for (byte b : in.terrain) classes[b]++;
                }
            }
            double ms = (System.nanoTime() - t1) / 1e6;
            if (round == 0) cold = ms;
            if (round >= MapCheck.WARM_ROUNDS) best = Math.min(best, ms);
            drawn = n;
        }
        long all = Arrays.stream(classes).sum();
        report("the Earth screen: " + MapCheck.SCREEN_TILES + " tiles at Rotterdam's site - ground, plans, paint and raster - in no more than MapCheck.SCREEN_MS",
                best <= MapCheck.SCREEN_MS,
                String.format("%.1f ms (%.3f a tile), %,d buildings; the first time, its plans made, %.0f ms; the square city %d districts a side, laid in %.0f ms;"
                                + " the screen %.0f%% sea, %.0f%% fresh", best, best / MapCheck.SCREEN_TILES, drawn, cold, side, built,
                        100.0 * classes[World.SALT] / all, 100.0 * classes[World.FRESH] / all));
    }

    /* =====================================================================
       9. THE FULL FILE
       ===================================================================== */

    static void full(Spot s) throws Exception {
        out.println("\n--- 9. the full file, when it is named: the cells pass and the planet's land ---");
        Path file = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && !Files.isRegularFile(file)) {
            report("the file -Dcbs.earth names is there", false, named);
            return;
        }
        if (file == null) {
            out.println("earth-1.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the cells pass was not timed");
            return;
        }
        EarthData d = EarthData.open(file);
        String id = EarthTerrain.use(d);
        report("the named file is the one this build reads: EarthData.EARTH_1", id.equals(EarthData.EARTH_1), id);
        World w = World.of(World.Key.earth(SEED, s.lat(), s.lon(), id));
        EarthTerrain e = w.earthTerrain();
        e.cellsPass();
        double cold = e.cellsPassMs();
        e.cellsPass();
        double warm = e.cellsPassMs();
        report("the cells pass in no more than CELLS_PASS_MS, its tiles read from the file the first time", cold <= CELLS_PASS_MS,
                String.format("%.0f ms the first time, %.0f ms again (the study 370-450)", cold, warm));
        double land = 0;
        for (int c = 0; c < World.CELLS * World.CELLS; c++) land += w.cellLandShare(c) * World.CELL_KM2;
        // The land inside the square, independently: every 4' cell of the globe, its area on the sphere, where its centre projects.
        long t0 = System.nanoTime();
        int cpd = d.layer("far.land").cellsPerDegree;
        double globe = 0, inSquare = 0, r2 = EarthProj.RADIUS_M * EarthProj.RADIUS_M / 1e6;
        double[] xy = new double[2];
        EarthProj proj = e.projection();
        for (int b = 0; b < 180; b++) {
            byte[] dry = d.grid("far.land", b), lake = d.grid("far.lake", b);
            for (int r = 0; r < cpd; r++) {
                double lat1 = b - 89 - r / (double) cpd, lat0 = lat1 - 1.0 / cpd, lat = (lat0 + lat1) / 2;
                double area = r2 * StrictMath.toRadians(1.0 / cpd) * (StrictMath.sin(StrictMath.toRadians(lat1)) - StrictMath.sin(StrictMath.toRadians(lat0)));
                for (int c = 0; c < 360 * cpd; c++) {
                    int v = (dry[r * 360 * cpd + c] & 0xFF) + (lake[r * 360 * cpd + c] & 0xFF);
                    if (v == 0) continue;
                    double km2 = v / (double) EarthData.FAR_LEVELS * area;
                    globe += km2;
                    if (proj.plotOf(lat, -180 + (c + 0.5) / cpd, xy) && xy[0] >= 0 && xy[1] >= 0 && xy[0] < World.SIDE && xy[1] < World.SIDE) inSquare += km2;
                }
            }
        }
        double off = land / inSquare - 1;
        report("the world's land, its cells' shares added, is the land inside its square within LAND_WITHIN", Math.abs(off) <= LAND_WITHIN,
                String.format("%,.0f km2 against %,.0f (%+.3f%%); the globe's %,.0f, %.1f%% of it inside the square (the antipode's cap is past it); %.0f ms",
                        land, inSquare, 100 * off, globe, 100 * inSquare / globe, (System.nanoTime() - t0) / 1e6));
        t0 = System.nanoTime();
        int widest = 0;
        for (int t = 0; t < EarthData.TILES; t++) {
            if (d.entry("rivers", t) <= 0) continue;
            for (EarthData.Run r : d.rivers(t)) for (int k = 0; k < r.size(); k++) widest = Math.max(widest, r.width(k));
        }
        report("the widest river in the file is EarthTerrain.WIDEST_RIVER_M, and its half within WATER_PAD_DEG", widest == EarthTerrain.WIDEST_RIVER_M
                        && widest / 2.0 <= EarthTerrain.WATER_PAD_DEG * Math.PI / 180 * EarthProj.RADIUS_M,
                String.format("%,d m; the pad %.1f km; %.0f ms", widest, EarthTerrain.WATER_PAD_DEG * Math.PI / 180 * EarthProj.RADIUS_M / 1000,
                        (System.nanoTime() - t0) / 1e6));
    }
}
