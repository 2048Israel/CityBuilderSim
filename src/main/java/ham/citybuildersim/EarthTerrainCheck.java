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
 * The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key holding the generated world as it was, and the game's founding, land and screen standing on an Earth world - and since 0.7.117 (batch E3) its deposits on the Earth's geology (Geology).
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
 * ...and since 0.7.117 (batch E3; spec-earth 4 and 8, runs/spec-materials.md
 * 2.4), the deposits on the Earth's geology:
 *  10. THE CLASSES: the data's provinces are the study's 1,023, the rules'
 *      count STUDY_CLASSES; Geology's hand table holds every one the rules
 *      leave other, once, each row naming the data's province and the rules'
 *      class, and moving it or saying why no class fits; Geology reads it.
 *  11. THE SPOTS: Rotterdam, Perth, Paris and the Yilgarn 150 km east of
 *      Perth lie in the study's provinces and classes; at the study's Z the
 *      table's weights and the petroleum factor give the study's multipliers
 *      to its rounding; the model's are the weight x Z (x the factor); each
 *      world reads its spot's province at its founding site.
 *  12. FIELDS BY CLASS: on a world's pure cells, iron's old-world fields a
 *      land km2 on shields against basins are W_IRON's ratio within SIGMAS,
 *      each class's density the resource's x w x Z, and the fields a city
 *      sees, on pools of pure cells, the same ratio - Perth's world on the
 *      fixture, Rotterdam's on the full file.
 *  13. THE FULL FILE, when named: each Z, measured as Geology defines it, is
 *      Geology's to its rounding, and the types' odds; at them the Earth-wide
 *      type shares are the generated world's; on the rules' classes the land
 *      in each class is the study's within SHARE_WITHIN and limestone in
 *      basins and on shields the study's within LIMESTONE_WITHIN (its Z is
 *      printed against the study's: three of six do not follow from its own
 *      table); then 12 on the whole planet.
 *  14. TOTALS AND TYPES: each Earth world's totals are its cells', to the
 *      tonne; every pool's fields add up to its total, and by type; every
 *      field carries the class under it; cells with no land hold oil where a
 *      basin's, shelf's or rift's sea is, and nothing else; the type draw
 *      gives each class Geology.firstShare() within SIGMAS (iron and copper
 *      flat), on synthetic fields and on those drawn; a generated world has
 *      no geology and draws its types flat (WorldCheck holds its fields byte
 *      for byte).
 *
 * Every fixture causes its condition: the windows hold coasts (Rotterdam,
 * Perth), rivers (the Maas and the Seine), small lakes, lakes of 10 km2 and
 * more (Rotterdam's delta), beaches (Perth) and the date line (Fiji); the
 * fixture's bands cross the Yilgarn shield and basins on three continents,
 * and Rotterdam's the North Sea's basins.
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
    /** Where the harness prints: the console. */
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
        EarthData file = full(spots.get("Rotterdam"));
        classes(data);
        spots(data, id, spots);
        byClass(World.of(World.Key.earth(SEED, spots.get("Perth").lat(), spots.get("Perth").lon(), id)), "the fixture, Perth's world");
        planet(file, spots.get("Rotterdam"));
        types(id, spots);

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

    /** Section 9; returns the full file's reader when it is named, else null (section 13 reads it). */
    static EarthData full(Spot s) throws Exception {
        out.println("\n--- 9. the full file, when it is named: the cells pass and the planet's land ---");
        Path file = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && !Files.isRegularFile(file)) {
            report("the file -Dcbs.earth names is there", false, named);
            return null;
        }
        if (file == null) {
            out.println("earth-1.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the cells pass was not timed");
            return null;
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
        return d;
    }

    /* =====================================================================
       10 TO 14. THE DEPOSITS ON THE EARTH'S GEOLOGY (0.7.117, batch E3;
       spec-earth 4 and 8, runs/spec-materials.md 2.4)
       ===================================================================== */

    /** The study's count of the provinces by geology.py's rules (spec-earth 4): 474 basin, 151 fold belt, 66 shelf, 55 ocean, 38 shield, 20 rift and 219 other, in Geology.Kind's order. */
    static final int[] STUDY_CLASSES = { 55, 38, 151, 20, 66, 474, 219 };

    /** The study's land in each class by the rules, Antarctica left out, in points (spec-earth 4), in Geology.Kind's order: 0.7 ocean, 17.8 shield, 18.9 fold belt, 2.2 rift, 2.8 shelf, 35.1 basin, 22.5 other. */
    static final double[] STUDY_SHARES = { 0.7, 17.8, 18.9, 2.2, 2.8, 35.1, 22.5 };

    /** ...reproduced within this, in points: half a point (★E3: the study's are rounded to a tenth, and its land was its prototype's cells). */
    static final double SHARE_WITHIN = 0.5;

    /** The study's Z on the rules' classes (spec-earth 4), in Resource's order for the six in fields: iron 0.888, oil 0.701, stone 0.751, coal 0.959, copper 0.846, uranium 1.049. */
    static final double[] STUDY_Z = { 0.888, 0.701, 0.751, 0.959, 0.846, 1.049 };

    /** The study's limestone in basins and on shields on the rules' classes (spec-materials 2.4, out/earth-types.txt): .886 and .493. */
    static final double STUDY_LIMESTONE_BASIN = 0.886, STUDY_LIMESTONE_SHIELD = 0.493;

    /** ...reproduced within this: half a point (★E3: the study's are rounded to three places). */
    static final double LIMESTONE_WITHIN = 0.005;

    /** A sampled figure is held to its expectation within this many standard deviations: 3 (★E3). */
    static final double SIGMAS = 3;

    /** A Z on the full file is Geology's constant to its rounding, four decimals: half the last place. */
    static final double Z_ROUNDING = 0.00005;

    /** An odds on the full file is Geology's constant to its rounding, six figures: half the last place, relative. */
    static final double ODDS_ROUNDING = 0.000005;

    /** How far east of Perth the study's shield spot lies, in km: 150 (spec-earth 4, "150 km east of Perth (Yilgarn Block)"). */
    static final double YILGARN_KM = 150;

    /** Synthetic fields drawn for each class to hold the types' odds: 200,000 (one standard deviation about 0.1 point). */
    static final int TYPE_DRAWS = 200_000;

    /** How far about Rotterdam's site the North Sea's cells are looked at for oil at sea, in cells: 8 (490 km), within the fixture's bands. */
    static final int SEA_OIL_CELLS = 8;

    /** One of the study's spots: its place, its province and class, and the multipliers it measured (spec-earth 4). */
    record GeoSpot(String name, double lat, double lon, String province, Geology.Kind kind, double knownMmboe, Map<Resource, Double> study) { }

    /** Section 10: the provinces' classes, the rules' and the hand-checked table over them - on the fixture, which holds the whole geology. */
    static void classes(EarthData data) {
        out.println("\n--- 10. the provinces' classes: geology.py's rules, and the hand-checked table over them ---");
        Geology g = Geology.of(data);
        int[] byRules = new int[Geology.Kind.values().length];
        for (EarthData.Province p : data.provinces()) byRules[g.rulesKindOf(p.row()).ordinal()]++;
        report("the data's provinces are the study's, classed by the rules as the study counts them (STUDY_CLASSES)",
                data.provinces().size() == 1023 && Arrays.equals(byRules, STUDY_CLASSES), String.format("%,d provinces: %s", data.provinces().size(), kinds(byRules)));
        Map<Integer, Geology.Hand> hand = new LinkedHashMap<>();
        boolean once = true;
        for (Geology.Hand h : Geology.TABLE) once &= hand.put(h.row(), h) == null;
        boolean covers = true, names = true, applied = true, reasoned = true;
        int others = 0, stay = 0;
        int[] placed = new int[Geology.Kind.values().length];
        Map<String, Integer> misfires = new java.util.TreeMap<>();
        for (EarthData.Province p : data.provinces()) {
            Geology.Kind rules = g.rulesKindOf(p.row());
            Geology.Hand h = hand.get(p.row());
            if (rules == Geology.Kind.OTHER) {
                others++;
                covers &= h != null;
            }
            if (h == null) {
                applied &= g.kindOf(p.row()) == rules;
                continue;
            }
            names &= h.name().equals(p.name().trim()) && h.code().equals(p.code()) && h.rules() == rules;
            applied &= g.kindOf(p.row()) == h.kind();
            reasoned &= !h.why().isBlank() && (h.kind() != h.rules() || h.why().contains("no class fits"));
            if (rules == Geology.Kind.OTHER) {
                placed[h.kind().ordinal()]++;
                if (h.kind() == Geology.Kind.OTHER) stay++;
            } else {
                misfires.merge(rules.label() + " to " + h.kind().label(), 1, Integer::sum);
            }
        }
        report("every province the rules leave other is in the hand table, once", covers && once && others == STUDY_CLASSES[Geology.Kind.OTHER.ordinal()],
                String.format("%d of %d; the table %d rows", Geology.TABLE.size() - misfires.values().stream().mapToInt(Integer::intValue).sum(), others, Geology.TABLE.size()));
        check("every row of it names the data's province - its row, USGS code and name - and the class the rules gave it", names);
        check("every row moves its province, or keeps an other no class fits, and says why; Geology reads the table's class over the rules'", reasoned && applied);
        out.printf("      the %d the rules leave other: %s, of which %d stay other%n", others, kinds(placed), stay);
        out.printf("      the rules' misfires corrected, %d: %s%n", misfires.values().stream().mapToInt(Integer::intValue).sum(), misfires);
    }

    /** A count by class, in Geology.Kind's order, as words. */
    static String kinds(int[] by) {
        StringBuilder s = new StringBuilder();
        for (Geology.Kind k : Geology.Kind.values()) {
            if (by[k.ordinal()] == 0) continue;
            if (s.length() > 0) s.append(", ");
            s.append(by[k.ordinal()]).append(' ').append(k.label());
        }
        return s.toString();
    }

    /** The study's spots: the three windows' and the shield's, 150 km east of Perth on the Perth world's plots. */
    static List<GeoSpot> geoSpots(Map<String, Spot> spots, String id) {
        Spot r = spots.get("Rotterdam"), p = spots.get("Perth"), f = spots.get("Paris");
        World perth = World.of(World.Key.earth(SEED, p.lat(), p.lon(), id));
        double[] ll = new double[2];
        perth.earthTerrain().projection().latLonOfPlot(EarthTerrain.spotPlot() + YILGARN_KM * 1000 / World.PLOT_M, EarthTerrain.spotPlot(), ll);
        return List.of(
                new GeoSpot("Rotterdam", r.lat(), r.lon(), "Anglo-Dutch Basin", Geology.Kind.BASIN, 12_640, Map.of(Resource.OIL, 2.99, Resource.COAL, 1.92, Resource.IRON, 0.44)),
                new GeoSpot("Perth", p.lat(), p.lon(), "Perth Basin", Geology.Kind.BASIN, 0, Map.of(Resource.OIL, 1.40, Resource.IRON, 0.44)),
                new GeoSpot("Paris", f.lat(), f.lon(), "Anglo-Paris Basin", Geology.Kind.BASIN, 0, Map.of(Resource.OIL, 1.40, Resource.IRON, 0.44)),
                new GeoSpot("150 km east of Perth", ll[0], ll[1], "Yilgarn Block", Geology.Kind.SHIELD, 0, Map.of(Resource.IRON, 3.55, Resource.OIL, 0.04)));
    }

    /** Section 11: the study's spots - their provinces and classes, the study's multipliers at its Z, and the model's at its own. */
    static void spots(EarthData data, String id, Map<String, Spot> spots) {
        out.println("\n--- 11. the study's spots: Rotterdam, Perth, Paris and the Yilgarn shield 150 km east of Perth ---");
        Geology g = Geology.of(data);
        Resource[] six = { Resource.IRON, Resource.OIL, Resource.STONE, Resource.COAL, Resource.COPPER, Resource.URANIUM };
        for (GeoSpot s : geoSpots(spots, id)) {
            int row = g.provinceAt(s.lat(), s.lon());
            EarthData.Province p = data.province(row);
            report(s.name() + ": the province is the study's, of its class" + (s.knownMmboe() > 0 ? ", with its known petroleum" : ""),
                    p != null && p.name().trim().equals(s.province()) && g.kindOf(row) == s.kind() && (s.knownMmboe() == 0 || p.knownMmboe() == s.knownMmboe()),
                    String.format("(%.4f, %.4f): row %d, %s, %s, %,.0f MMBOE", s.lat(), s.lon(), row, p == null ? "none" : p.name(), g.kindOf(row).label(), p == null ? 0 : p.knownMmboe()));
            boolean study = true, model = true;
            StringBuilder ours = new StringBuilder(), theirs = new StringBuilder();
            for (Resource r : six) {
                double pf = r == Resource.OIL ? g.petroleumOf(row) : 1;
                double m = g.multiplier(r, row);
                model &= Math.abs(m - Geology.weight(r, g.kindOf(row)) * Geology.z(r) * pf) <= 1e-12 * Math.max(1, m);
                ours.append(String.format(" %s x%.2f", r.label().toLowerCase(), m));
                Double fig = s.study().get(r);
                if (fig == null) continue;
                double atStudyZ = Geology.weight(r, g.kindOf(row)) * STUDY_Z[r.ordinal()] * pf;
                study &= Math.round(atStudyZ * 100) == Math.round(fig * 100);
                theirs.append(String.format(" %s x%.2f (x%.3f at its Z)", r.label().toLowerCase(), fig, atStudyZ));
            }
            report(s.name() + ": at the study's Z the table's weights and the petroleum factor give the study's multipliers to its rounding", study, theirs.toString().trim());
            report(s.name() + ": the model's multipliers are the weight x Z (x the petroleum factor), Geology's constants", model, "here:" + ours);
        }
        boolean same = true;
        StringBuilder where = new StringBuilder();
        for (String name : List.of("Rotterdam", "Perth", "Paris")) {
            Spot s = spots.get(name);
            World w = World.of(World.Key.earth(SEED, s.lat(), s.lon(), id));
            int row = w.provinceAt(w.foundingX(), w.foundingY());
            same &= row == g.provinceAt(s.lat(), s.lon()) && w.geologyAt(w.foundingX(), w.foundingY()) == g.kindOf(row);
            where.append(String.format(" %s row %d;", name, row));
        }
        report("each world reads its spot's province and class at its founding site (World.provinceAt(), geologyAt())", same, where.toString().trim());
    }

    /**
     * Section 12: iron's fields per land km2 by class, on a world's PURE cells
     * - those whose field land is their land x the weight x Z of the class at
     * their centre for iron and copper both, so all their land is of that class
     * - the old world's counts (what a cell's total is drawn from) and the
     * fields of pools whose cells are all pure of one class.
     */
    static void byClass(World w, String where) {
        out.println("\n--- 12. iron's fields per land km2, shields against basins, by the weight ratio within " + (int) SIGMAS + " standard deviations: " + where + " ---");
        int nk = Geology.Kind.values().length, cells = World.CELLS * World.CELLS;
        Geology.Kind[] pure = new Geology.Kind[cells];
        long[] count = new long[nk];
        double[] km2 = new double[nk];
        int[] pureCells = new int[nk];
        for (int c = 0; c < cells; c++) {
            double land = w.cellLandShare(c);
            if (!(land > 0)) continue;
            long cx = (long) (c % World.CELLS) * World.CELL + World.CELL / 2, cy = (long) (c / World.CELLS) * World.CELL + World.CELL / 2;
            Geology.Kind k = w.geologyAt(cx, cy);
            if (!pureIn(w, c, land, k)) continue;
            pure[c] = k;
            count[k.ordinal()] += w.legacyCellFieldCount(c, Resource.IRON);
            km2[k.ordinal()] += land * World.CELL_KM2;
            pureCells[k.ordinal()]++;
        }
        int s = Geology.Kind.SHIELD.ordinal(), b = Geology.Kind.BASIN.ordinal();
        double ratio = (count[s] / km2[s]) / (count[b] / km2[b]), want = Geology.W_IRON[s] / Geology.W_IRON[b];
        double sigma = Math.sqrt(1.0 / count[s] + 1.0 / count[b]);
        report("the old world's iron fields a land km2 on pure shield cells against pure basin cells: W_IRON's ratio within SIGMAS",
                count[s] > 0 && count[b] > 0 && Math.abs(ratio / want - 1) <= SIGMAS * sigma,
                String.format("%.3f for %.1f (%+.2f%%, sigma %.2f%%): shields %,d fields on %,.0f km2 in %d cells, basins %,d on %,.0f km2 in %d", ratio, want,
                        100 * (ratio / want - 1), 100 * sigma, count[s], km2[s], pureCells[s], count[b], km2[b], pureCells[b]));
        boolean each = true;
        StringBuilder dens = new StringBuilder();
        for (Geology.Kind k : Geology.Kind.values()) {
            int i = k.ordinal();
            if (count[i] == 0 && km2[i] == 0) continue;
            double expect = Resource.IRON.fieldsPerKm2() * Geology.W_IRON[i] * Geology.Z_IRON, got = count[i] / km2[i];
            boolean ok = expect == 0 ? count[i] == 0 : Math.abs(got / expect - 1) <= SIGMAS / Math.sqrt(Math.max(1, expect * km2[i]));
            each &= ok;
            dens.append(String.format(" %s %.4f for %.4f;", k.label(), got, expect));
        }
        report("...and each class's density the resource's own times W_IRON times Z_IRON within SIGMAS", each, dens.toString().trim());
        // The fields a city sees: pools whose cells are all pure of one class.
        long[] fields = new long[nk];
        double[] poolKm2 = new double[nk];
        int side = World.poolsOnSide(Resource.IRON), bc = World.poolCells(Resource.IRON);
        for (int py = 0; py < side; py++) {
            for (int px = 0; px < side; px++) {
                Geology.Kind k = null;
                double land = 0;
                boolean one = true;
                for (int cy = py * bc; cy < Math.min(World.CELLS, py * bc + bc) && one; cy++) {
                    for (int cx = px * bc; cx < Math.min(World.CELLS, px * bc + bc); cx++) {
                        int c = cy * World.CELLS + cx;
                        if (!(w.cellLandShare(c) > 0)) continue;
                        if (pure[c] == null || (k != null && pure[c] != k)) { one = false; break; }
                        k = pure[c];
                        land += w.cellLandShare(c) * World.CELL_KM2;
                    }
                }
                if (!one || k == null) continue;
                fields[k.ordinal()] += w.poolFieldCount(Resource.IRON, px, py);
                poolKm2[k.ordinal()] += land;
            }
        }
        double fr = (fields[s] / poolKm2[s]) / (fields[b] / poolKm2[b]), fsig = Math.sqrt(1.0 / fields[s] + 1.0 / fields[b]);
        report("the fields a city sees, a tenth as many, on pools all of pure shield or pure basin cells: the same ratio within SIGMAS",
                fields[s] > 0 && fields[b] > 0 && Math.abs(fr / want - 1) <= SIGMAS * fsig,
                String.format("%.3f for %.1f (%+.1f%%, sigma %.1f%%): shields %,d fields on %,.0f km2, basins %,d on %,.0f km2", fr, want, 100 * (fr / want - 1), 100 * fsig,
                        fields[s], poolKm2[s], fields[b], poolKm2[b]));
    }

    /** Whether a cell's land is all of class k: its field land for iron and for copper each its land x the weight x Z there (Geology's constants; the field land is kept as a float). */
    static boolean pureIn(World w, int c, double land, Geology.Kind k) {
        for (Resource r : new Resource[] { Resource.IRON, Resource.COPPER }) {
            double want = land * Geology.weight(r, k) * Geology.z(r), got = w.cellFieldShare(c, r);
            if (Math.abs(got - want) > 1e-6 * Math.max(want, 1e-3)) return false;
        }
        return true;
    }

    /** Section 13, on the full file: Z and the odds are its own to their rounding, and the study's figures on the rules' classes; section 12 on the whole planet. */
    static void planet(EarthData file, Spot rotterdam) {
        out.println("\n--- 13. the full file, when it is named: Z and the types' odds are its own, and the study's figures on the rules' classes ---");
        if (file == null) {
            out.println("earth-1.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": Z and the odds were not measured");
            return;
        }
        Geology g = Geology.of(file);
        long t0 = System.nanoTime();
        Geology.Measure m = g.measure();
        double ms = (System.nanoTime() - t0) / 1e6;
        Geology.Kind[] hand = g.kinds(), rules = g.rulesKinds();
        double[] pf = g.petroleum();
        Resource[] six = { Resource.IRON, Resource.OIL, Resource.STONE, Resource.COAL, Resource.COPPER, Resource.URANIUM };
        boolean zs = true;
        StringBuilder z = new StringBuilder(), zr = new StringBuilder();
        for (Resource r : six) {
            double here = m.zOf(r, hand, pf), byRules = m.zOf(r, rules, pf);
            zs &= Math.abs(here - Geology.z(r)) <= Z_ROUNDING;
            z.append(String.format(" %s %.5f (%.4f);", r.label().toLowerCase(), here, Geology.z(r)));
            zr.append(String.format(" %s %.4f (the study %.3f);", r.label().toLowerCase(), byRules, STUDY_Z[r.ordinal()]));
        }
        report("each resource's Z, the land over the land x w north of ANTARCTIC_LAT with the hand table, is Geology's to its rounding", zs,
                z.toString().trim() + String.format(" measured in %.0f ms", ms));
        out.println("      on the rules' classes alone:" + zr + " - iron, copper and uranium's do not follow from the study's own table (fixE3-notes 6)");
        out.printf("      oil's offshore sea - shelves', basins' and rifts' - adds %.1f%% to its land's count (on the rules' classes %.1f%%)%n",
                100 * m.offshoreOil(hand, pf), 100 * m.offshoreOil(rules, pf));
        double[] coalShares = m.fieldShares(Resource.COAL, hand), stoneShares = m.fieldShares(Resource.STONE, hand);
        double coking = Geology.Measure.oddsFor(coalShares, Geology.COKING_RATIO, Deposit.TYPE_SHARES[Deposit.FieldType.COKING.ordinal()]);
        double lime = Geology.Measure.oddsFor(stoneShares, Geology.LIMESTONE_RATIO, Deposit.TYPE_SHARES[Deposit.FieldType.LIMESTONE.ordinal()]);
        report("the types' odds, each the one whose Earth-wide share is the generated world's, are Geology's to their rounding",
                Math.abs(coking / Geology.COKING_ODDS - 1) <= ODDS_ROUNDING && Math.abs(lime / Geology.LIMESTONE_ODDS - 1) <= ODDS_ROUNDING,
                String.format("coking %.7f (%.6f), limestone %.6f (%.5f)", coking, Geology.COKING_ODDS, lime, Geology.LIMESTONE_ODDS));
        double ck = Geology.Measure.shareAt(coalShares, Geology.COKING_RATIO, Geology.COKING_ODDS), ls = Geology.Measure.shareAt(stoneShares, Geology.LIMESTONE_RATIO, Geology.LIMESTONE_ODDS);
        report("...so at Geology's odds the Earth-wide shares are TYPE_SHARES': coking 15%, limestone 70%",
                Math.abs(ck - Deposit.TYPE_SHARES[Deposit.FieldType.COKING.ordinal()]) < 1e-6 && Math.abs(ls - Deposit.TYPE_SHARES[Deposit.FieldType.LIMESTONE.ordinal()]) < 1e-6,
                String.format("coking %.5f%%, limestone %.5f%%; limestone %.3f in basins and on shelves, %.3f in fold belts, %.3f on shields; coking %.3f in fold belts, %.3f on shields",
                        100 * ck, 100 * ls, Geology.lean(Geology.LIMESTONE_ODDS, 2), Geology.lean(Geology.LIMESTONE_ODDS, 0.5), Geology.lean(Geology.LIMESTONE_ODDS, 0.25),
                        Geology.lean(Geology.COKING_ODDS, 3), Geology.lean(Geology.COKING_ODDS, 0.5)));
        double[] by = m.landByKind(rules);
        double all = m.totalLand();
        boolean shares = true;
        StringBuilder sh = new StringBuilder();
        for (Geology.Kind k : Geology.Kind.values()) {
            double pts = 100 * by[k.ordinal()] / all;
            shares &= Math.abs(pts - STUDY_SHARES[k.ordinal()]) <= SHARE_WITHIN;
            sh.append(String.format(" %s %.2f (%.1f);", k.label(), pts, STUDY_SHARES[k.ordinal()]));
        }
        report("on the rules' classes, the land in each class, Antarctica left out, is the study's within SHARE_WITHIN", shares, sh.toString().trim());
        double ruleLime = Geology.Measure.oddsFor(m.fieldShares(Resource.STONE, rules), Geology.LIMESTONE_RATIO, Deposit.TYPE_SHARES[Deposit.FieldType.LIMESTONE.ordinal()]);
        double lb = Geology.lean(ruleLime, Geology.LIMESTONE_RATIO[Geology.Kind.BASIN.ordinal()]), lsh = Geology.lean(ruleLime, Geology.LIMESTONE_RATIO[Geology.Kind.SHIELD.ordinal()]);
        report("...and its odds put limestone in basins and on shields where the study did, within LIMESTONE_WITHIN",
                Math.abs(lb - STUDY_LIMESTONE_BASIN) <= LIMESTONE_WITHIN && Math.abs(lsh - STUDY_LIMESTONE_SHIELD) <= LIMESTONE_WITHIN,
                String.format("%.4f in basins (the study %.3f), %.4f on shields (%.3f)", lb, STUDY_LIMESTONE_BASIN, lsh, STUDY_LIMESTONE_SHIELD));
        double[] withHand = m.landByKind(hand), allIn = m.landAllByKind(hand);
        double allLand = 0;
        for (double v : allIn) allLand += v;
        StringBuilder hs = new StringBuilder();
        for (Geology.Kind k : Geology.Kind.values()) hs.append(String.format(" %s %.1f;", k.label(), 100 * withHand[k.ordinal()] / all));
        out.printf("      with the hand table, Antarctica left out:%s Antarctica in, shield %.1f%% of %,.0f km2%n", hs, 100 * allIn[Geology.Kind.SHIELD.ordinal()] / allLand, allLand);
        byClass(World.of(World.Key.earth(SEED, rotterdam.lat(), rotterdam.lon(), EarthTerrain.use(file))), "the full file, Rotterdam's world");
    }

    /** Section 14: the totals exact on Earth and every field typed by the class under it. */
    static void types(String id, Map<String, Spot> spots) {
        out.println("\n--- 14. on Earth the totals are exact, oil lies at sea in basins, and a field's type leans on the class under it ---");
        Resource[] five = { Resource.IRON, Resource.OIL, Resource.STONE, Resource.COAL, Resource.COPPER };
        boolean sums = true, typed = true, wired = true, totals = true;
        int pools = 0, fieldsSeen = 0;
        int nk = Geology.Kind.values().length;
        long[][] lean = new long[nk][2], coking = new long[nk][2];
        for (String name : List.of("Rotterdam", "Perth")) {
            Spot s = spots.get(name);
            World w = World.of(World.Key.earth(SEED, s.lat(), s.lon(), id));
            double[] t = w.totals();
            for (Resource r : Resource.values()) {
                double sum = 0;
                for (int c = 0; c < World.CELLS * World.CELLS; c++) sum += w.cellTotal(c, r);
                totals &= sum == t[r.ordinal()] && (r == Resource.FOREST || t[r.ordinal()] > 0);
            }
            int fc = World.cellOf(w.foundingX(), w.foundingY());
            for (Resource r : five) {
                int[] home = World.poolOf(fc, r);
                int last = World.poolsOnSide(r) - 1, reach = r == Resource.IRON ? 2 : 1;
                for (int py = Math.max(0, home[1] - reach); py <= Math.min(last, home[1] + reach); py++) {
                    for (int px = Math.max(0, home[0] - reach); px <= Math.min(last, home[0] + reach); px++) {
                        List<Deposit> fields = w.poolFields(r, px, py);
                        double total = 0;
                        double[] byType = new double[r.typesOf().size()];
                        for (Deposit d : fields) {
                            total += d.amount();
                            byType[r.typesOf().indexOf(d.type())] += d.amount();
                            wired &= d.geology() != null && d.geology() == w.geologyAt(d.x(), d.y());
                            if (r == Resource.STONE) lean[d.geology().ordinal()][d.type() == Deposit.FieldType.LIMESTONE ? 0 : 1]++;
                            if (r == Resource.COAL) coking[d.geology().ordinal()][d.type() == Deposit.FieldType.COKING ? 0 : 1]++;
                        }
                        double typeSum = 0;
                        for (double v : byType) typeSum += v;
                        sums &= fields.size() == w.poolFieldCount(r, px, py) && total == w.poolTotal(r, px, py) && typeSum == total;
                        typed &= r.hasTypes() || fields.stream().allMatch(d -> d.type() == Deposit.FieldType.NONE);
                        pools++;
                        fieldsSeen += fields.size();
                    }
                }
            }
        }
        check("each world's totals are its cells' totals added, to the tonne, and every resource has some", totals);
        report("every pool's fields add up to its total exactly, and its fields of each type to it as well; oil's are NONE", sums && typed,
                String.format("%,d pools about Rotterdam's and Perth's sites, %,d fields", pools, fieldsSeen));
        check("every field carries the class of the ground under its centre (World.geologyAt())", wired);
        // Oil at sea: a cell with no land holds oil where its sea is a basin's, shelf's or rift's, and nothing else - the North Sea about
        // Rotterdam, within SEA_OIL_CELLS of its site and with its centre in the fixture's bands there (51 to 53 N; beyond them the fixture reads all sea).
        Spot r = spots.get("Rotterdam");
        World w = World.of(World.Key.earth(SEED, r.lat(), r.lon(), id));
        int seaOil = 0, seaCells = 0, rc = World.cellOf(w.foundingX(), w.foundingY());
        double seaTonnes = 0;
        boolean onlyOil = true, held = true;
        double[] ll = new double[2];
        for (int dy = -SEA_OIL_CELLS; dy <= SEA_OIL_CELLS; dy++) {
            for (int dx = -SEA_OIL_CELLS; dx <= SEA_OIL_CELLS; dx++) {
                int c = rc + dy * World.CELLS + dx;
                long cx = (long) (c % World.CELLS) * World.CELL + World.CELL / 2, cy = (long) (c / World.CELLS) * World.CELL + World.CELL / 2;
                w.earthTerrain().projection().latLonOfPlot(cx, cy, ll);
                if (w.cellLandShare(c) > 0 || ll[0] < 51.2 || ll[0] > 52.8) continue;
                seaCells++;
                for (Resource x : Resource.values()) if (x != Resource.OIL && x.inFields()) onlyOil &= w.cellFieldShare(c, x) == 0 && w.cellTotal(c, x) == 0;
                boolean basinSea = w.geology().seaHoldsOil(w.provinceAt(cx, cy));
                held &= !basinSea || w.cellFieldShare(c, Resource.OIL) > 0;
                if (w.cellFieldShare(c, Resource.OIL) > 0) {
                    seaOil++;
                    seaTonnes += w.cellTotal(c, Resource.OIL);
                }
            }
        }
        report("Rotterdam's North Sea: cells with no land hold oil where they are a basin's, shelf's or rift's sea, and nothing else", seaOil > 0 && seaTonnes > 0 && onlyOil && held,
                String.format("%d of %d cells of sea about the site hold oil, %,.0f Mt", seaOil, seaCells, seaTonnes / 1e6));
        // The odds, on synthetic fields of each class: the draw's share against Geology.firstShare().
        boolean odds = true, flat = true;
        StringBuilder o = new StringBuilder();
        for (Resource x : new Resource[] { Resource.IRON, Resource.COAL, Resource.COPPER, Resource.STONE }) {
            for (Geology.Kind k : Geology.Kind.values()) {
                long first = 0;
                for (int i = 0; i < TYPE_DRAWS; i++) {
                    Deposit d = new Deposit(x, i % (World.CELLS * World.CELLS), World.FIELD_INDEX_FROM + i / (World.CELLS * World.CELLS), 0, 0, World.FIELD_SCALE, 1, k);
                    if (d.type() == x.typesOf().get(0)) first++;
                }
                double p = Geology.firstShare(x, k), got = first / (double) TYPE_DRAWS;
                odds &= Math.abs(got - p) <= SIGMAS * Math.sqrt(p * (1 - p) / TYPE_DRAWS);
                if (x == Resource.IRON || x == Resource.COPPER) flat &= p == Deposit.TYPE_SHARES[x.typesOf().get(0).ordinal()];
                if (x == Resource.STONE || x == Resource.COAL) o.append(String.format(" %s %s %.3f (%.3f);", x == Resource.STONE ? "limestone" : "thermal", k.label(), got, p));
            }
        }
        report("the draw on Earth gives each class its first type's share, Geology.firstShare(), within SIGMAS; iron and copper flat at TYPE_SHARES'", odds && flat,
                String.format("%,d synthetic fields a class:%s", TYPE_DRAWS, o));
        StringBuilder seen = new StringBuilder();
        boolean drawn = true;
        for (Geology.Kind k : Geology.Kind.values()) {
            long[] v = lean[k.ordinal()];
            long n = v[0] + v[1];
            if (n == 0) continue;
            double p = Geology.firstShare(Resource.STONE, k);
            drawn &= Math.abs(v[0] / (double) n - p) <= SIGMAS * Math.sqrt(p * (1 - p) / n) + 1.0 / n;
            seen.append(String.format(" %s %d of %d (%.3f);", k.label(), v[0], n, p));
        }
        for (Geology.Kind k : Geology.Kind.values()) {
            long[] v = coking[k.ordinal()];
            long n = v[0] + v[1];
            if (n == 0) continue;
            double p = 1 - Geology.firstShare(Resource.COAL, k);
            drawn &= Math.abs(v[0] / (double) n - p) <= SIGMAS * Math.sqrt(p * (1 - p) / n) + 1.0 / n;
        }
        report("the stone and coal fields drawn about the two sites: limestone and coking each class's share within SIGMAS", drawn, "limestone:" + seen);
        // A generated world: no geology, its types its 0.7.110 draw.
        World gen = World.of(SEED);
        int gc = World.cellOf(gen.foundingX(), gen.foundingY());
        boolean none = gen.geology() == null && gen.geologyAt(gen.foundingX(), gen.foundingY()) == null && gen.provinceAt(gen.foundingX(), gen.foundingY()) == 0;
        for (Resource x : new Resource[] { Resource.IRON, Resource.STONE, Resource.COAL, Resource.COPPER }) {
            for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                for (Deposit d : gen.fieldsInCell(gc + dy * World.CELLS + dx, x)) {
                    double u = World.unit(World.mix(((long) d.cell() << 24) ^ ((long) d.index() << 4) ^ x.ordinal() ^ Deposit.TYPE_SALT));
                    none &= d.geology() == null && d.type() == (u < Deposit.TYPE_SHARES[x.typesOf().get(0).ordinal()] ? x.typesOf().get(0) : x.typesOf().get(1))
                            && gen.cellFieldShare(d.cell(), x) == gen.cellLandShare(d.cell());
                }
            }
        }
        check("a generated world has no geology: its fields carry none, draw their types flat, and its field land is its land", none);
    }
}
