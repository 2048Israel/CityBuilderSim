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
import java.util.Random;

/**
 * The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key holding the generated world as it was, and the game's founding, land and screen standing on an Earth world - and since 0.7.117 (batch E3) its deposits on the Earth's geology (Geology), and since 0.7.127 (batch E7) its ice and desert.
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
 * THE DATA. EarthCheck's fixture (earth/earth-2-fixture.bin, cut from
 * earth-2.bin by tools/earth/fixture.py) holds the 15" tiles of the study's
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
 *      CELLS_PASS_MS of the main thread's CPU, the wall clock printed beside
 *      it (batch EC1; the study 0.37 to 0.45 s); the world's land, its cells'
 *      shares added, the land inside its square within LAND_WITHIN - every
 *      4' cell of the globe projected, the antipode's cap past the square
 *      left out; and the widest river EarthTerrain.WIDEST_RIVER_M, its half
 *      within EarthTerrain.WATER_PAD_DEG.
 *
 * ...and since 0.7.117 (batch E3; spec-earth 4 and 8,
 * docs/studies/spec-materials.md 2.4), the deposits on the Earth's geology:
 *  10. THE CLASSES: the data's provinces are the study's 1,023, the rules'
 *      count STUDY_CLASSES; Geology's hand table holds every one the rules
 *      leave other, once, each row naming the data's province and the rules'
 *      class, and moving it or saying why no class fits; Geology reads it.
 *  11. THE SPOTS: Rotterdam, Perth, Paris and the Yilgarn 150 km east of
 *      Perth lie in the study's provinces and classes; at the study's Z the
 *      table's weights and the petroleum factor give the study's multipliers
 *      to its rounding; each world reads its spot's province at its founding
 *      site. Since 0.8.12 (batch EG1) the model's multipliers are EG1's, no
 *      longer the weight x Z x the factor: oil's weight is OIL_ALLOWANCE x its
 *      class's W_OIL plus its code's known petroleum a km2 over the Earth's
 *      mean, iron's a shield's four or IRON_ALLOWANCE of the class's (a
 *      recorded district's at its point), each x Z - worked out in the check
 *      from the constants and the code's known petroleum and area; E3's
 *      lines at the study's Z stay as the study's history.
 *  12. FIELDS BY CLASS: on a world's pure cells, iron's old-world fields a
 *      land km2 on shields against basins are the ratio of iron's weights
 *      within SIGMAS, each class's density the resource's x its iron weight x
 *      Z, and the fields a city sees, on pools of pure cells, the same ratio
 *      - Perth's world on the fixture, Rotterdam's on the full file. Since
 *      0.8.12 (batch EG1) iron's weight is a shield's W_IRON, else
 *      IRON_ALLOWANCE of the class's (Geology.ironWeight()), so the ratio is
 *      80, not E3's 8; the cells with a point in a recorded district are
 *      left out of the pure ones, counted and printed; the city's fields are
 *      a thirtieth as many (FIELD_SCALE x EARTH_FIELD_SCALE), not a tenth.
 *  13. THE FULL FILE, when named: each Z, measured as Geology defines it
 *      (since EG1 Measure.zOf(r, g): iron's by shield and district, oil's by
 *      known petroleum), is Geology's to its rounding, and the types' odds;
 *      E3's Z replayed with E3's weights and petroleum factor are E3's
 *      (E3_Z) to E3_Z_WITHIN; the land in the districts is printed; at the
 *      odds the Earth-wide type shares are the generated world's; on the
 *      rules' classes the land in each class is the study's within
 *      SHARE_WITHIN and limestone in basins and on shields the study's
 *      within LIMESTONE_WITHIN (its Z is printed against the study's: three
 *      of six do not follow from its own table); then 12 on the whole planet.
 *  14. TOTALS AND TYPES: each Earth world's totals are its cells', to the
 *      tonne; every pool's fields add up to its total, and by type; every
 *      field carries the class under it; cells with no land hold oil where a
 *      basin's, shelf's or rift's sea is, and nothing else; the type draw
 *      gives each class Geology.firstShare() within SIGMAS (iron and copper
 *      flat), on synthetic fields and on those drawn; a generated world has
 *      no geology and draws its types flat (WorldCheck holds its fields byte
 *      for byte).
 *
 * ...and since 0.7.127 (batch E7; docs/studies/spec-globe.md 3.9 and 4's
 * E7 row), ice and desert as kinds of ground:
 *  15. World.dry() names the dry classes, the desert among them and ice not;
 *      the books count a desert's plots as a grass's and ice's in the whole
 *      area only, and a count of the five alone as before; the runs, and a
 *      district's plan - PlanCheck's test district with its grass made
 *      desert is the same plan, with half its grass ice no building stands
 *      on the ice; an Earth save from before ICE_FORMAT is refused in the ice
 *      words, since 0.8.12 (batch EG1) one from ICE_FORMAT to before
 *      DEPOSITS_FORMAT in the deposits words, one at DEPOSITS_FORMAT not
 *      (the general floor stays below it, so a generated save of the floor's
 *      format loads and an Earth one is refused), a generated one loads. On the full file, each kind's share of two
 *      1-degree cells' ground (ICE_CELLS, DESERT_CELLS) is the data's within
 *      COVER_POINTS, and its edge frayed both ways about the bit's
 *      half-contour and never past the cells that agree; the founding card's
 *      ice is the site's plot's - Greenland's sheet refused on its own plot,
 *      a margin site on grass in an ice-bit cell foundable, a spot on the ice
 *      snapped off it; a holding's tile of ice its whole area only, the
 *      Sahara's site desert and dry; and the far nodes draw both.
 *
 * ...and since ET1 (2026-10-24; docs/studies/spec-earth2-terrain.md 6.1,
 * 12's 6) the shallow sea:
 *  16. World.SHALLOW is the byte after DESERT, World.salt() is SALT and
 *      SHALLOW alone, the books count it as sea and the runs as not dry, its
 *      colour, words and legend entry are its own. The shallow share of a
 *      window's sea drawn is the data's, sea-weighted, within SHALLOW_POINTS
 *      (Rotterdam all shallow, Perth and Halifax part), the tile rule and the
 *      point rule split the sea alike, the Halifax Narrows is shallow and
 *      Bedford Basin's middle deep, a tile costs no more than TILE_MS; a
 *      generated world draws none (D19) and neither does an earth-1 file;
 *      on the full file four coasts' shares are the data's.
 *
 * ...and since ET1's second slice (2026-10-24; docs/studies/spec-earth2-terrain.md
 * 3.1) the relief plane:
 *  17. World's relief classes FLAT, FOOTHILL, STEEP and VERY_STEEP, and the
 *      two rules that read them (buildable(), workable()); a generated world
 *      and an earth-1 file are FLAT everywhere. On each fixture window - said
 *      to hold slope or not: Rotterdam holds none, so it proves only that none
 *      is drawn - and, with the file named, six of the full file's hills
 *      (Medellin, Everest, the Upper Engadin, Hong Kong, La Paz, Kathmandu),
 *      the share of each class among the dry plots is the data's
 *      land-weighted share within RELIEF_POINTS (scaled to the window's
 *      ground); water, the shallow sea, fresh water and ice carry FLAT; forest
 *      keeps its cover on a slope; Medellin's valley floor is FLAT and the
 *      hills about it steep; Everest's ice is FLAT and the rock about it very
 *      steep. The cover is untouched, the readers agree, the classes nest and lie
 *      where the data has them, the tile rule and the point rule split alike
 *      within RELIEF_COPIES_POINTS, a tile and its relief cost no more than
 *      TILE_MS. On the full file: the far summary of a 4' block is the mean of
 *      its 256 cells, far nodes at four strides, the globe's camera and the
 *      picture's draw the relief within SCREEN_MS and FRAME_MS.
 *
 * ...and since ET5 (2026-10-25; the ET5 design, D2 to D4) the landmass of a plot:
 *  18. A TILE'S LANDMASS in no more than LANDMASS_TILE_MS: each round a world
 *      of its own (another seed, the same data), the cover's tiles kept as a map
 *      keeps them, then the landmass of each tile drawn beside it - the data's
 *      cell at every dry plot's blend point, and for a dry plot on a cell with
 *      no land the nearest land cell's (LandmassCheck holds the rule itself).
 *
 * Every fixture causes its condition: the windows hold coasts (Rotterdam,
 * Perth), rivers (the Maas and the Seine), small lakes, lakes of 10 km2 and
 * more (Rotterdam's delta), beaches (Perth) and the date line (Fiji); the
 * fixture's bands cross the Yilgarn shield and basins on three continents,
 * and Rotterdam's the North Sea's basins; ice and desert's cells and spots
 * (section 15) were each picked on the full file for holding the condition
 * - an edge in the cell, a site the bit's rule and the plot's split - and
 * the section says so where one no longer does.
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

    /** A tile's landmass (ET5) in no more than this, ms, its cover kept: the data's cell at each of its 1,024 plots' blend points, the nearest land cell's for a dry plot on a cell with none (the median of TILE_ROUNDS rounds over each window's middle TIMED_TILES x TIMED_TILES tiles). */
    static final double LANDMASS_TILE_MS = 0.3;

    /** The cells pass in no more than this, ms of the main thread's CPU since batch EC1 (spec-earth 8: 1 s; the study measured 0.37 to 0.45 s on the wall clock). */
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
        out.printf("%-100s %s  %s%n", label, Timing.verdict(ok), detail);
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
                            100 * (w.share(World.SALT) + w.share(World.SHALLOW)), 100 * w.dataSea, 100 * w.share(World.FRESH), 100 * w.dataLake, 100 * w.forestOfDry(), 100 * w.dataForest));
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
        iceAndDesert(file, windows, id);
        shallowSea(file, windows, data, id);
        reliefPlane(file, spots, data, id);
        landmassTime(three, id);

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
        final long[] classes = new long[World.CLASSES];
        /** Cells counted: the window's 15" cells, rows from rMin and columns (unwrapped about the spot) from cMin. */
        final long rMin, cMin;
        final int rows, cols;
        final int[] count, salt;
        final boolean[] edge;
        final int cells;
        final double seaPoints, dataSea, dataLake, dataForest;
        /** The shallow share of the window's sea in the data (ET1): its cells' shallow levels weighted by each cell's sea and the plots laid on it; NaN where the file has no shallow layer or the window no sea. */
        final double dataShallow;
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
                        if (World.salt(t[p])) salt[at]++;
                        if (px == 0 || py == 0 || px == WINDOW - 1 || py == WINDOW - 1) edge[at] = true;
                        if (World.dry(t[p])) {
                            dry++;
                            forestData += (data.at("landcover", ll[0], ll[1]) & EarthData.LC_FOREST) / (double) EarthData.SHARE_LEVELS;
                        }
                    }
                }
            }
            dryForestData = dry;
            dataForest = dry > 0 ? forestData / dry : 0;
            int n = 0;
            double diff = 0, sea = 0, lake = 0, shallowSea = 0;
            long all = 0;
            for (int a = 0; a < count.length; a++) {
                if (count[a] == 0) continue;
                long r = rMin + a / cols, c = Math.floorMod(cMin + a % cols, 360L * 240);
                int tile = EarthData.tile(89 - (int) (r / 240), (int) (c / 240) - 180);
                double s = data.cell("sea", tile, (int) (r % 240), (int) (c % 240)) / (double) EarthData.SHARE_LEVELS;
                double l = data.cell("lake", tile, (int) (r % 240), (int) (c % 240)) / (double) EarthData.SHARE_LEVELS;
                sea += s * count[a];
                lake += l * count[a];
                if (data.hasRelief()) shallowSea += s * count[a] * data.cell("shallow", tile, (int) (r % 240), (int) (c % 240)) / (double) EarthData.SHARE_LEVELS;
                all += count[a];
                if (edge[a]) continue;
                n++;
                diff += Math.abs(salt[a] / (double) count[a] - s);
            }
            cells = n;
            seaPoints = n > 0 ? 100 * diff / n : Double.NaN;
            dataSea = sea / all;
            dataLake = lake / all;
            dataShallow = data.hasRelief() && sea > 0 ? shallowSea / sea : Double.NaN;
        }

        /** The shallow share of the sea drawn: SHALLOW plots over all the sea's, NaN where there is none. */
        double drawnShallow() {
            long sea = classes[World.SALT] + classes[World.SHALLOW];
            return sea > 0 ? classes[World.SHALLOW] / (double) sea : Double.NaN;
        }

        /** A class's share of the window's plots. */
        double share(byte cls) { return classes[cls] / (double) plots.length; }

        /** Forest's share of the dry ground drawn. */
        double forestOfDry() {
            long dry = 0;
            for (int c = 0; c < World.CLASSES; c++) if (World.dry(c)) dry += classes[c];
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
            e.shelfTheta();
        } catch (UnsupportedOperationException x) {
            refused = true;
        }
        // (Until 0.7.121 it refused the sea's depth too; E4 gave one by the coast's distance, ET1 the data's - EarthFoundingCheck 5 holds it.)
        check("...and with no heights, an Earth world refuses the elevation field's shelf rather than make one up, and gives the"
                + " sea's depth from the data instead (0.7.121; ET1)", refused && e.depthAt(0, 0) > 0);
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
                        if (World.salt(was) || was == World.FRESH) waterA++;
                        if (World.salt(t[p]) || t[p] == World.FRESH) waterB++;
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
            report(s.name() + ": a tile's ground in no more than TILE_MS", Timing.within(median, TILE_MS),
                    String.format("%.3f ms a tile (rounds %.3f to %.3f; the study %s)", median, ms[0], ms[TILE_ROUNDS - 1],
                            s.name().equals("Rotterdam") ? "0.152-0.202" : "0.090-0.125"));
        }
    }

    /* =====================================================================
       18. A TILE'S LANDMASS (ET5)
       ===================================================================== */

    static void landmassTime(List<Spot> spots, String id) {
        out.println("\n--- 18. a tile's landmass in no more than " + LANDMASS_TILE_MS + " ms: the median of " + TILE_ROUNDS + " rounds over each window's middle " + TIMED_TILES
                + " x " + TIMED_TILES + " tiles, its cover kept ---");
        int[] ids = new int[World.TILE * World.TILE];
        for (Spot s : spots) {
            long tx0 = EarthTerrain.spotPlot() / World.TILE - TIMED_TILES / 2, ty0 = tx0;
            double[] ms = new double[TILE_ROUNDS];
            for (int round = -1; round < TILE_ROUNDS; round++) {
                // A world of its own each round (another seed, the same data), so no tile of the landmass is kept; the cover's tiles first, kept as a map keeps them.
                World w = World.of(World.Key.earth(SEED + 2000 + round, s.lat(), s.lon(), id));
                for (int j = 0; j < TIMED_TILES; j++) for (int i = 0; i < TIMED_TILES; i++) w.terrainAt((tx0 + i) * World.TILE, (ty0 + j) * World.TILE);
                long t0 = System.nanoTime();
                for (int j = 0; j < TIMED_TILES; j++) for (int i = 0; i < TIMED_TILES; i++) w.tileLandmass(tx0 + i, ty0 + j, ids);
                if (round >= 0) ms[round] = (System.nanoTime() - t0) / 1e6 / (TIMED_TILES * TIMED_TILES);
            }
            // The premise: the tiles timed hold land, or the figure is the cost of drawing nothing.
            World w = World.of(World.Key.earth(SEED + 2000, s.lat(), s.lon(), id));
            long land = 0, noLandCell = 0;
            // ...and, for a coast, dry plots on a cell the layer calls no land: they take the nearest land cell's id (a search of the cells round, the costly part of the reader), and a window of nothing else takes the lookup alone.
            byte[] cover = new byte[World.TILE * World.TILE];
            double[] ll = new double[2];
            EarthData data = w.earthTerrain().data();
            for (int j = 0; j < TIMED_TILES; j++) {
                for (int i = 0; i < TIMED_TILES; i++) {
                    long tx = tx0 + i, ty = ty0 + j;
                    w.tileLandmass(tx, ty, ids);
                    w.tileTerrain(tx, ty, cover);
                    EarthProj.Blend b = w.earthTerrain().projection().blend(tx * World.TILE, ty * World.TILE, World.TILE);
                    for (int k = 0; k < ids.length; k++) {
                        if (ids[k] != 0) land++;
                        if (World.dry(cover[k]) && b.latLon(tx * World.TILE + k % World.TILE + 0.5, ty * World.TILE + k / World.TILE + 0.5, ll) && data.landmassAt(ll[0], ll[1]) == 0) noLandCell++;
                    }
                }
            }
            Arrays.sort(ms);
            double median = ms[TILE_ROUNDS / 2];
            boolean coast = s.name().equals("Rotterdam") || s.name().equals("Perth");
            report(s.name() + ": a tile's landmass in no more than LANDMASS_TILE_MS, over tiles that hold land" + (coast ? " and dry plots on a cell with none" : ""),
                    land > 0 && (!coast || noLandCell > 0) && Timing.within(median, LANDMASS_TILE_MS),
                    String.format("%.3f ms a tile (rounds %.3f to %.3f), %,d land plots in the %d tiles, %,d of them dry on a cell with no land", median, ms[0], ms[TILE_ROUNDS - 1], land, TIMED_TILES * TIMED_TILES, noLandCell));
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
                    if (c == World.FRESH || World.salt(c)) onRiver++;
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
                    if (c == World.FRESH || World.salt(c)) atLake++;
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
                    for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) besideSea |= World.salt(w.at(px + dx, py + dy));
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
        return World.dry(w.terrainAt(x, y));
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
        long[] classes = new long[World.CLASSES];
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
                Timing.within(best, MapCheck.SCREEN_MS),
                String.format("%.1f ms (%.3f a tile), %,d buildings; the first time, its plans made, %.0f ms; the square city %d districts a side, laid in %.0f ms;"
                                + " the screen %.0f%% sea, %.0f%% fresh", best, best / MapCheck.SCREEN_TILES, drawn, cold, side, built,
                        100.0 * (classes[World.SALT] + classes[World.SHALLOW]) / all, 100.0 * classes[World.FRESH] / all));
    }

    /* =====================================================================
       9. THE FULL FILE
       ===================================================================== */

    /*
     * THE CELLS PASS IS TIMED ON THE MAIN THREAD'S CPU (batch EC1, 2026-10-11;
     * Jerus's A28 of 2026-10-10: "time it as thread CPU (not a loosening)",
     * MapCheck's month bound since 0.7.100). On the wall clock the pass's
     * first time failed often in a full suite on the two-core cloud machine
     * (1,085 ms in the gate of 0.7.126, 1,125 and 1,212 in I6's suites) and
     * passed alone (491 to 652 ms), before and after I6 alike: the harnesses
     * before it push earth-2.bin's pages out of the cache, and its first
     * reads wait on the disk (docs/queue/EC1.md). The design's bound is on
     * the pass's own work, which is its thread's CPU - a blocked read is
     * none, the kernel's copy of the bytes is - and the pass is all on the
     * calling thread: EarthTerrain.cellsPass() is a plain loop, and
     * EarthData reads and inflates its tiles where it is asked, with no
     * stream, executor or thread of its own in EarthTerrain, EarthData,
     * EarthProj or Geology. CELLS_PASS_MS stays 1 s, and the wall clock is
     * printed beside it, so a slow disk still shows. Where the JVM cannot
     * measure a thread's CPU it is timed on the wall clock, and the output
     * and the label say so (MapCheck.CPU_TIMED, which turns it on).
     */
    /** The clock the cells pass's bound reads, in ns: the main thread's CPU, or the wall clock where MapCheck.CPU_TIMED is false. */
    static long passClock() {
        return MapCheck.CPU_TIMED ? MapCheck.THREADS.getCurrentThreadCpuTime() : System.nanoTime();
    }

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
            out.println("earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the cells pass was not timed");
            return null;
        }
        EarthData d = EarthData.open(file);
        String id = EarthTerrain.use(d);
        report("the named file is the one this build reads: EarthData.EARTH_2", id.equals(EarthData.EARTH_2), id);
        World w = World.of(World.Key.earth(SEED, s.lat(), s.lon(), id));
        EarthTerrain e = w.earthTerrain();
        long w0 = System.nanoTime(), c0 = passClock();
        e.cellsPass();
        double cold = (passClock() - c0) / 1e6, coldWall = (System.nanoTime() - w0) / 1e6;
        w0 = System.nanoTime();
        c0 = passClock();
        e.cellsPass();
        double warm = (passClock() - c0) / 1e6, warmWall = (System.nanoTime() - w0) / 1e6;
        String clock = MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock (this JVM cannot read a thread's CPU finely enough)";
        report("the cells pass in no more than CELLS_PASS_MS of " + (MapCheck.CPU_TIMED ? "the main thread's CPU" : "the wall clock")
                        + ", its tiles read from the file the first time", Timing.within(cold, CELLS_PASS_MS),
                String.format("%.0f ms of %s the first time (%.0f on the wall clock), %.0f again (%.0f on the wall clock); the study 370-450 on the wall clock",
                        cold, clock, coldWall, warm, warmWall));
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
       spec-earth 4 and 8, docs/studies/spec-materials.md 2.4)
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

    /** E3's Z on the hand table (0.7.117, before EG1), in Resource's order for the six in fields: iron 0.7219, oil 0.6033, stone 0.6915, coal 0.7884, copper 0.7322, uranium 0.7518. */
    static final double[] E3_Z = { 0.7219, 0.6033, 0.6915, 0.7884, 0.7322, 0.7518 };

    /** ...replayed on today's measure within this: a place and a half of the last (the Sydney patch, EG1, moves a little land, and copper's 0.73226 is E3's 0.7322 and a bit). */
    static final double E3_Z_WITHIN = 0.0001;

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

    /**
     * A resource's multiplier for a province by EG1's rules (0.8.12; spec-earth2-terrain 8.5), worked out here from Geology's constants and
     * the province's USGS code, not by asking Geology's tables: oil's weight is OIL_ALLOWANCE x its class's W_OIL plus the code's known
     * petroleum a km2 (Geology.known() over Geology.area()) over the Earth's mean (Geology.knownMean()); iron's a shield's W_IRON, else
     * IRON_ALLOWANCE x its class's; every other field's its class's; each x Z. With a finite point, iron inside a recorded district is the
     * greater of that and IRON_DISTRICT_WEIGHT x Z_IRON (Geology.districtAt()); NaN for the province's alone.
     */
    static double eg1Multiplier(Geology g, EarthData.Province p, Resource r, double lat, double lon) {
        Geology.Kind k = g.kindOf(p.row());
        double w;
        if (r == Resource.OIL) {
            double area = g.area(p.code()), mean = g.knownMean();
            w = Geology.OIL_ALLOWANCE * Geology.W_OIL[k.ordinal()] + (area > 0 && mean > 0 ? g.known(p.code()) / area / mean : 0);
        } else if (r == Resource.IRON) {
            w = k == Geology.Kind.SHIELD ? Geology.W_IRON[k.ordinal()] : Geology.IRON_ALLOWANCE * Geology.W_IRON[k.ordinal()];
        } else {
            w = Geology.weight(r, k);
        }
        double m = w * Geology.z(r);
        if (r == Resource.IRON && !Double.isNaN(lat) && g.districtAt(lat, lon) >= 0) m = Math.max(m, Geology.IRON_DISTRICT_WEIGHT * Geology.Z_IRON);
        return m;
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
            if (p == null) continue;
            boolean study = true, model = true;
            StringBuilder ours = new StringBuilder(), theirs = new StringBuilder();
            for (Resource r : six) {
                // E3's formula, the study's: the class's weight x the petroleum factor (E3's, Geology.petroleumOf(): the file's own known petroleum) x the study's Z.
                double pf = r == Resource.OIL ? g.petroleumOf(row) : 1;
                double m = g.multiplier(r, row);
                // EG1's (0.8.12): worked out here from the constants and the code's known petroleum and area, at the spot's point.
                double here = eg1Multiplier(g, p, r, s.lat(), s.lon());
                model &= Math.abs(m - eg1Multiplier(g, p, r, Double.NaN, Double.NaN)) <= 1e-12 * Math.max(1, m)
                        && Math.abs(g.multiplierAt(r, row, s.lat(), s.lon()) - here) <= 1e-12 * Math.max(1, here);
                ours.append(String.format(" %s x%.2f", r.label().toLowerCase(), m));
                Double fig = s.study().get(r);
                if (fig == null) continue;
                double atStudyZ = Geology.weight(r, g.kindOf(row)) * STUDY_Z[r.ordinal()] * pf;
                study &= Math.round(atStudyZ * 100) == Math.round(fig * 100);
                theirs.append(String.format(" %s x%.2f (x%.3f at its Z)", r.label().toLowerCase(), fig, atStudyZ));
            }
            report(s.name() + ": at the study's Z the table's weights and the petroleum factor give the study's multipliers to its rounding", study, theirs.toString().trim());
            double known = g.known(p.code()), area = g.area(p.code());
            report(s.name() + ": the model's multipliers are EG1's: the weight x Z, oil's by its code's known petroleum a km2 over the Earth's plus the allowance, iron's a shield's or an allowance, at the point",
                    model && (s.knownMmboe() == 0 || known == p.knownMmboe()) && area > 0,
                    String.format("here:%s; oil's code %s: %,.0f MMBOE over %,.0f km2, the Earth's %.5f a km2; %s", ours, p.code(), known, area, g.knownMean(),
                            g.districtAt(s.lat(), s.lon()) >= 0 ? "in an iron district" : "in no iron district"));
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
     * - those with no point in a recorded iron district (EG1) whose field land
     * is their land x the weight x Z of the class at their centre for iron
     * and copper both, so all their land is of that class - the old world's
     * counts (what a cell's total is drawn from) and the fields of pools
     * whose cells are all pure of one class. Since 0.8.12 (batch EG1) iron's
     * weight is Geology.ironWeight(): a shield's four, else IRON_ALLOWANCE of
     * the class's, so shields against basins is 80, not E3's 8; a cell with a
     * point in a district is left out and counted, because its iron is more
     * than its class's.
     */
    static void byClass(World w, String where) {
        out.println("\n--- 12. iron's fields per land km2, shields against basins, by the weight ratio within " + (int) SIGMAS + " standard deviations: " + where + " ---");
        int nk = Geology.Kind.values().length, cells = World.CELLS * World.CELLS;
        Geology.Kind[] pure = new Geology.Kind[cells];
        long[] count = new long[nk];
        double[] km2 = new double[nk];
        int[] pureCells = new int[nk];
        int landCells = 0, districtCells = 0;
        for (int c = 0; c < cells; c++) {
            double land = w.cellLandShare(c);
            if (!(land > 0)) continue;
            landCells++;
            long cx = (long) (c % World.CELLS) * World.CELL + World.CELL / 2, cy = (long) (c / World.CELLS) * World.CELL + World.CELL / 2;
            Geology.Kind k = w.geologyAt(cx, cy);
            if (districtPointIn(w, c)) { districtCells++; continue; }
            if (!pureIn(w, c, land, k)) continue;
            pure[c] = k;
            count[k.ordinal()] += w.legacyCellFieldCount(c, Resource.IRON);
            km2[k.ordinal()] += land * World.CELL_KM2;
            pureCells[k.ordinal()]++;
        }
        int s = Geology.Kind.SHIELD.ordinal(), b = Geology.Kind.BASIN.ordinal();
        double ratio = (count[s] / km2[s]) / (count[b] / km2[b]), want = Geology.ironWeight(Geology.Kind.SHIELD) / Geology.ironWeight(Geology.Kind.BASIN);
        double sigma = Math.sqrt(1.0 / count[s] + 1.0 / count[b]);
        report("the old world's iron fields a land km2 on pure shield cells against pure basin cells: the ratio of EG1's iron weights within SIGMAS",
                count[s] > 0 && count[b] > 0 && Math.abs(ratio / want - 1) <= SIGMAS * sigma,
                String.format("%.3f for %.1f (%+.2f%%, sigma %.2f%%): shields %,d fields on %,.0f km2 in %d cells, basins %,d on %,.0f km2 in %d;"
                                + " %,d of the %,d land cells hold a point in a recorded iron district and are left out", ratio, want,
                        100 * (ratio / want - 1), 100 * sigma, count[s], km2[s], pureCells[s], count[b], km2[b], pureCells[b], districtCells, landCells));
        boolean each = true;
        StringBuilder dens = new StringBuilder();
        for (Geology.Kind k : Geology.Kind.values()) {
            int i = k.ordinal();
            if (count[i] == 0 && km2[i] == 0) continue;
            double expect = Resource.IRON.fieldsPerKm2() * Geology.ironWeight(k) * Geology.Z_IRON, got = count[i] / km2[i];
            boolean ok = expect == 0 ? count[i] == 0 : Math.abs(got / expect - 1) <= SIGMAS / Math.sqrt(Math.max(1, expect * km2[i]));
            each &= ok;
            dens.append(String.format(" %s %.4f for %.4f;", k.label(), got, expect));
        }
        report("...and each class's density the resource's own times its iron weight (EG1) times Z_IRON within SIGMAS", each, dens.toString().trim());
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
        report("the fields a city sees, a thirtieth as many on Earth (FIELD_SCALE x EARTH_FIELD_SCALE), on pools all of pure shield or pure basin cells: the same ratio within SIGMAS",
                fields[s] > 0 && fields[b] > 0 && Math.abs(fr / want - 1) <= SIGMAS * fsig,
                String.format("%.3f for %.1f (%+.1f%%, sigma %.1f%%): shields %,d fields on %,.0f km2, basins %,d on %,.0f km2", fr, want, 100 * (fr / want - 1), 100 * fsig,
                        fields[s], poolKm2[s], fields[b], poolKm2[b]));
    }

    /** Whether a cell's land is all of class k: its field land for iron and for copper each its land x the weight x Z there (Geology's constants, iron's the EG1 weight; the field land is kept as a float). */
    static boolean pureIn(World w, int c, double land, Geology.Kind k) {
        for (Resource r : new Resource[] { Resource.IRON, Resource.COPPER }) {
            double want = land * (r == Resource.IRON ? Geology.ironWeight(k) : Geology.weight(r, k)) * Geology.z(r), got = w.cellFieldShare(c, r);
            if (Math.abs(got - want) > 1e-6 * Math.max(want, 1e-3)) return false;
        }
        return true;
    }

    /** Whether any of a cell's CELL_SAMPLES x CELL_SAMPLES points - the ones the cells pass reads - lies in a recorded iron district (Geology.districtAt(), EG1). */
    static boolean districtPointIn(World w, int c) {
        EarthTerrain e = w.earthTerrain();
        Geology g = e.geology();
        int m = EarthTerrain.CELL_SAMPLES;
        long x0 = (long) (c % World.CELLS) * World.CELL, y0 = (long) (c / World.CELLS) * World.CELL;
        EarthProj.Blend b = e.projection().blend(x0, y0, World.CELL);
        double[] ll = new double[2];
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < m; i++) {
                if (b.latLon(x0 + (i + 0.5) * World.CELL / m, y0 + (j + 0.5) * World.CELL / m, ll) && g.districtAt(ll[0], ll[1]) >= 0) return true;
            }
        }
        return false;
    }

    /** Section 13, on the full file: Z and the odds are its own to their rounding, and the study's figures on the rules' classes; section 12 on the whole planet. */
    static void planet(EarthData file, Spot rotterdam) {
        out.println("\n--- 13. the full file, when it is named: Z and the types' odds are its own, and the study's figures on the rules' classes ---");
        if (file == null) {
            out.println("earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": Z and the odds were not measured");
            return;
        }
        Geology g = Geology.of(file);
        long t0 = System.nanoTime();
        Geology.Measure m = g.measure();
        double ms = (System.nanoTime() - t0) / 1e6;
        Geology.Kind[] hand = g.kinds(), rules = g.rulesKinds();
        double[] pf = g.petroleum();
        Resource[] six = { Resource.IRON, Resource.OIL, Resource.STONE, Resource.COAL, Resource.COPPER, Resource.URANIUM };
        boolean zs = true, e3 = true;
        StringBuilder z = new StringBuilder(), zr = new StringBuilder(), z3 = new StringBuilder();
        for (Resource r : six) {
            double here = m.zOf(r, g), byRules = m.zOf(r, rules, pf), replay = m.zOf(r, hand, pf);
            zs &= Math.abs(here - Geology.z(r)) <= Z_ROUNDING;
            e3 &= Math.abs(replay - E3_Z[r.ordinal()]) <= E3_Z_WITHIN;
            z.append(String.format(" %s %.5f (%.4f);", r.label().toLowerCase(), here, Geology.z(r)));
            zr.append(String.format(" %s %.4f (the study %.3f);", r.label().toLowerCase(), byRules, STUDY_Z[r.ordinal()]));
            z3.append(String.format(" %s %.5f (E3 %.4f);", r.label().toLowerCase(), replay, E3_Z[r.ordinal()]));
        }
        report("each resource's Z, the land over the land x its weight (EG1's: iron's by shield and district, oil's by known petroleum) north of ANTARCTIC_LAT, is Geology's to its rounding", zs,
                z.toString().trim() + String.format(" measured in %.0f ms", ms));
        report("E3's Z, replayed with E3's weights and petroleum factor (Measure.zOf(r, kinds, petroleum)), are E3's to E3_Z_WITHIN: the Sydney patch moves a little land", e3, z3.toString().trim());
        out.println("      on the rules' classes alone:" + zr + " - iron, copper and uranium's do not follow from the study's own table (fixE3-notes 6)");
        out.printf("      oil's offshore sea - shelves', basins' and rifts' - adds %.1f%% to its land's count with EG1's weights (E3's on the hand table %.1f%%, on the rules' classes %.1f%%)%n",
                100 * m.offshoreOil(g), 100 * m.offshoreOil(hand, pf), 100 * m.offshoreOil(rules, pf));
        double districts = m.districtLand();
        report("the recorded iron districts hold land, which iron's weight and Z count at IRON_DISTRICT_WEIGHT", districts > 0 && !Geology.DISTRICTS.isEmpty(),
                String.format("%,.0f km2 of land in %d districts, %.3f%% of the %,.0f km2 north of ANTARCTIC_LAT", districts, Geology.DISTRICTS.size(), 100 * districts / m.totalLand(), m.totalLand()));
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

    /* =====================================================================
       15. ICE AND DESERT AS KINDS OF GROUND (0.7.127, batch E7; the
       project's spec-globe.md 3.9, and 4's E7 row)
       ===================================================================== */

    /** Each kind's share of a 1-degree cell's ground drawn is the data's within this, in points: SHARE_POINTS, the bound the sea's share of a 15" cell is held to (spec-earth 8); E7 measured 0.02 to 0.34. */
    static final double COVER_POINTS = SHARE_POINTS;

    /** The 1-degree cells each kind is held on, {lat0, lon0}, on the full file: ice at Vatnajokull and on south Greenland's margin, the desert at the Sahel's edge and on the Ustyurt by the Aral Sea - each a cell with its kind's bit on a fifth to four fifths of its land, so it has an edge to draw (★E7). */
    static final int[][] ICE_CELLS = { { 64, -17 }, { 61, -46 } }, DESERT_CELLS = { { 16, 0 }, { 44, 56 } };

    /** Greenland's sheet's middle, EarthFoundingCheck 3's spot on ice. */
    static final double[] SHEET = { 72, -40 };

    /** A spot on south Greenland's ice margin whose snapped site lies on grass in a 2' cell with the ice bit - the bit's rule (E4's) refused it, the plot's does not (found by E7's probe, the default seed; moved about 9 km from 61.41, -45.53 by ET2, whose snap on buildable ground lands elsewhere from that spot - the premise is the same). */
    static final double[] MARGIN = { 61.48, -45.68 };

    /** A spot on the ice there whose site the snap finds off it, on dry ground over a kilometre away (E7's probe). */
    static final double[] OFF_THE_ICE = { 61.42, -45.62 };

    /** The Sahara's spot, EarthFoundingCheck 3's desert. */
    static final double[] SAHARA = { 23, 10 };

    static void iceAndDesert(EarthData file, Map<String, Window> windows, String id) {
        out.println("\n--- 15. ice and desert as kinds of ground: ice never dry, the desert dry as grass is, each kind's share the data's, its edge frayed, the card's ice the plot's ---");
        boolean rule = World.dry(World.GRASS) && World.dry(World.FOREST) && World.dry(World.SAND) && World.dry(World.DESERT)
                && !World.dry(World.FRESH) && !World.dry(World.SALT) && !World.dry(World.ICE) && !World.dry(-1) && !World.dry(World.CLASSES);
        check("World.dry(): grass, forest, the beach and the desert are dry ground; fresh water, the sea and ice are not, nor what is not a class", rule);
        check("...ICE and DESERT take the two bytes after the five, World.CLASSES in all, each with its colour and its own name on the hover",
                World.ICE == 5 && World.DESERT == 6 && World.CLASSES == World.SHALLOW + 1 && TileRaster.GROUND.length == World.CLASSES
                        && LandMap.groundWords(World.ICE).equals("Ice") && LandMap.groundWords(World.DESERT).equals("Desert"));
        // The books (GridConversion.km2Of(), what CityLand counts every holding with).
        long n = 1000;
        double[] grass = GridConversion.km2Of(only(World.GRASS, n)), desert = GridConversion.km2Of(only(World.DESERT, n)), ice = GridConversion.km2Of(only(World.ICE, n));
        check("the books: a desert's plots count as a grass's in all five areas, dry ground", Arrays.equals(grass, desert) && desert[CityLand.DRY] == n * World.KM2_PER_PLOT);
        check("...and ice's in the whole area only - never dry, fresh, sea or forest", ice[CityLand.TOTAL] == n * World.KM2_PER_PLOT
                && ice[CityLand.DRY] == 0 && ice[CityLand.FRESH] == 0 && ice[CityLand.SEA] == 0 && ice[CityLand.FOREST] == 0);
        long[] five = { 11, 22, 33, 44, 55 }, seven = Arrays.copyOf(five, World.CLASSES);
        check("...and a count of the five classes alone, as the generated world's are made, reads as it did", Arrays.equals(GridConversion.km2Of(five), GridConversion.km2Of(seven))
                && GridConversion.km2Of(five)[CityLand.DRY] == (11 + 22 + 55) * World.KM2_PER_PLOT);
        check("the runs (CityRuns): the desert is dry ground where grass is, ice is not, and off the ground is not", CityRuns.dry(World.DESERT) == CityRuns.dry(World.GRASS)
                && CityRuns.dry(World.DESERT) && !CityRuns.dry(World.ICE) && !CityRuns.dry(-1));
        // A district's plan (DistrictPlan): PlanCheck's test district, its grass made desert, then half of it ice.
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        DistrictPlan.Input g = PlanCheck.testDistrict(types, 0, false), d = PlanCheck.testDistrict(types, 0, false), ic = PlanCheck.testDistrict(types, 0, false);
        int swapped = 0, iced = 0;
        for (int p = 0; p < DistrictPlan.AREA; p++) {
            if (d.terrain[p] == World.GRASS) { d.terrain[p] = World.DESERT; swapped++; }
            if (ic.terrain[p] == World.GRASS && p % DistrictPlan.FRAME < DistrictPlan.FRAME / 2) { ic.terrain[p] = World.ICE; iced++; }
        }
        DistrictPlan pg = DistrictPlan.make(g), pd = DistrictPlan.make(d), pi = DistrictPlan.make(ic);
        boolean same = pg.buildings == pd.buildings && Arrays.equals(pg.bx, pd.bx) && Arrays.equals(pg.by, pd.by) && Arrays.equals(pg.bw, pd.bw)
                && Arrays.equals(pg.bh, pd.bh) && Arrays.equals(pg.btype, pd.btype) && Arrays.equals(pg.street, pd.street) && Arrays.equals(pg.overflow, pd.overflow);
        report("a district's plan: PlanCheck's test district with its grass made desert is the same plan, every building and street", same && swapped > 0 && pg.buildings > 0,
                String.format("%,d plots of grass made desert; %d buildings", swapped, pg.buildings));
        int onIce = 0, iceHalf = 0;
        for (int b = 0; b < pi.buildings; b++) {
            for (int y = pi.by[b]; y < pi.by[b] + pi.bh[b]; y++) for (int x = pi.bx[b]; x < pi.bx[b] + pi.bw[b]; x++) if (ic.terrain[y * DistrictPlan.FRAME + x] == World.ICE) onIce++;
            if (pi.bx[b] < DistrictPlan.FRAME / 2) iceHalf++;
        }
        report("...and with the grass of its west half ice, no building stands on a plot of ice", onIce == 0 && iced > 0 && pi.buildings > 0,
                String.format("%,d plots of grass made ice; %d buildings, %d of them west of the middle on what ground is left there (%d before)", iced, pi.buildings, iceHalf, westOf(pg)));
        // The save (GameVersion's format 39): an Earth city saved before ice is refused, a generated one loads.
        Spot r = new Spot("Rotterdam", 51.90625, 4.48125);
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String earthSave = "{\"worldKind\":\"EARTH\",\"earthLat\":" + r.lat() + ",\"earthLon\":" + r.lon() + ",\"earthData\":\"" + id + "\",\"saveFormat\":";
        DataSave before = gson.fromJson(earthSave + (EarthSite.ICE_FORMAT - 1) + "}", DataSave.class), after = gson.fromJson(earthSave + EarthSite.ICE_FORMAT + "}", DataSave.class);
        DataSave beforeDeposits = gson.fromJson(earthSave + (EarthSite.DEPOSITS_FORMAT - 1) + "}", DataSave.class), atDeposits = gson.fromJson(earthSave + EarthSite.DEPOSITS_FORMAT + "}", DataSave.class);
        DataSave atSlope = gson.fromJson(earthSave + EarthSite.SLOPE_FORMAT + "}", DataSave.class), atLandmass = gson.fromJson(earthSave + EarthSite.LANDMASS_FORMAT + "}", DataSave.class);
        DataSave generated = gson.fromJson("{\"saveFormat\":" + GameVersion.OLDEST_LOADABLE_FORMAT + "}", DataSave.class);
        // Four floors for an Earth save since ET5: below ICE_FORMAT the ice words; from ICE_FORMAT to below DEPOSITS_FORMAT the deposits words;
        // from DEPOSITS_FORMAT to below SLOPE_FORMAT (the founding site snapped to buildable ground, ET2) the slope words; from SLOPE_FORMAT to below
        // LANDMASS_FORMAT (the ground counted by landmass, ET5) the landmass words; at LANDMASS_FORMAT none.
        check("the saves: an Earth city saved before ice was a kind of ground (EarthSite.ICE_FORMAT) is refused in words saying so; from there to before"
                        + " its fields were drawn on the geology (DEPOSITS_FORMAT, EG1) in the deposits words; from there to before its founding site was snapped to"
                        + " buildable ground (SLOPE_FORMAT, ET2) in the slope words; from there to before its ground was counted by landmass (LANDMASS_FORMAT, ET5)"
                        + " in the landmass words; one saved at LANDMASS_FORMAT is not refused",
                EarthSite.BEFORE_ICE_WORDS.equals(before.earthProblem())
                        && EarthSite.BEFORE_DEPOSITS_WORDS.equals(after.earthProblem()) && EarthSite.BEFORE_DEPOSITS_WORDS.equals(beforeDeposits.earthProblem())
                        && EarthSite.BEFORE_SLOPE_WORDS.equals(atDeposits.earthProblem()) && EarthSite.BEFORE_LANDMASS_WORDS.equals(atSlope.earthProblem()) && atLandmass.earthProblem() == null
                        && !EarthSite.BEFORE_ICE_WORDS.equals(EarthSite.BEFORE_DEPOSITS_WORDS) && !EarthSite.BEFORE_DEPOSITS_WORDS.equals(EarthSite.BEFORE_SLOPE_WORDS)
                        && !EarthSite.BEFORE_SLOPE_WORDS.equals(EarthSite.BEFORE_LANDMASS_WORDS)
                        && GameVersion.SAVE_FORMAT >= EarthSite.LANDMASS_FORMAT && EarthSite.LANDMASS_FORMAT > EarthSite.SLOPE_FORMAT && EarthSite.SLOPE_FORMAT > EarthSite.DEPOSITS_FORMAT
                        && EarthSite.DEPOSITS_FORMAT > EarthSite.ICE_FORMAT && EarthSite.ICE_FORMAT > EarthSite.EARTH_FORMAT);
        // Since 0.8.5 (FR2) the floor, 40, is past the ice format: an Earth save from before ice is refused by the floor as well.
        check("...and a generated city's save at the floor, its ground as it was, still loads: no Earth problem; and an Earth save"
                        + " before ice is refused, by its words below the floor or by the floor past it",
                generated.earthProblem() == null && !GameVersion.isNoLongerReadable(GameVersion.OLDEST_LOADABLE_FORMAT)
                        && (GameVersion.OLDEST_LOADABLE_FORMAT < EarthSite.ICE_FORMAT || GameVersion.isNoLongerReadable(EarthSite.ICE_FORMAT - 1)));
        // The Earth's floors are Earth-only: the general floor stays below the newest, so an Earth save of the floor's format is refused in the slope words (ET2; the deposits words, EG1's, before it) while a generated one loads.
        DataSave earthAtFloor = gson.fromJson(earthSave + GameVersion.OLDEST_LOADABLE_FORMAT + "}", DataSave.class);
        check("...and the slope floor is the Earth's alone: an Earth save at the general floor is refused in the slope words while the floor is below"
                        + " SLOPE_FORMAT, a generated save of that format loads",
                generated.earthProblem() == null && (GameVersion.OLDEST_LOADABLE_FORMAT >= EarthSite.LANDMASS_FORMAT
                        ? earthAtFloor.earthProblem() == null
                        : GameVersion.OLDEST_LOADABLE_FORMAT >= EarthSite.SLOPE_FORMAT
                        ? EarthSite.BEFORE_LANDMASS_WORDS.equals(earthAtFloor.earthProblem()) : EarthSite.BEFORE_SLOPE_WORDS.equals(earthAtFloor.earthProblem())));
        // ...and the landmass floor (ET5) is the Earth's alone too: a generated save of the format before it loads, and the general floor stays below it.
        DataSave generatedBeforeLandmass = gson.fromJson("{\"saveFormat\":" + (EarthSite.LANDMASS_FORMAT - 1) + "}", DataSave.class);
        check("...and the landmass floor is the Earth's alone: a generated save of the format before LANDMASS_FORMAT has no Earth problem, and is refused only by the general floor, once that has passed it",
                generatedBeforeLandmass.earthProblem() == null
                        && GameVersion.isNoLongerReadable(EarthSite.LANDMASS_FORMAT - 1) == (GameVersion.OLDEST_LOADABLE_FORMAT >= EarthSite.LANDMASS_FORMAT));
        long fixtureIce = 0, fixtureDesert = 0;
        for (Window w : windows.values()) { fixtureIce += w.classes[World.ICE]; fixtureDesert += w.classes[World.DESERT]; }
        out.printf("      the fixture's three windows: %,d plots of ice and %,d of desert%n", fixtureIce, fixtureDesert);
        if (file == null) {
            out.println("earth-2.bin not named: the shares, edges and cards of ice and desert, which the fixture's windows hold none of, were not tried");
            return;
        }
        // Each kind's share of a 1-degree cell, and its edge.
        for (int[] c : ICE_CELLS) cover(file, true, c[0], c[1]);
        for (int[] c : DESERT_CELLS) cover(file, false, c[0], c[1]);
        // The founding card's ice is the plot's.
        World.Key sheetKey = World.Key.earth(SEED, SHEET[0], SHEET[1], EarthData.EARTH_2);
        EarthSite.Card sheet = EarthSite.measure(sheetKey);
        World sw = World.of(sheetKey);
        byte sp = sw.terrainAt(EarthTerrain.spotPlot(), EarthTerrain.spotPlot());
        report("the card: Greenland's sheet has no dry ground within 5 km, so no site; the card is the spot's own plot's, ice, refused on ice - its ice the plot's",
                sw.earthTerrain().siteNear()[0] < 0 && sp == World.ICE && sheet.hasSite() && sheet.ice() && sw.terrainAt(sheet.siteX(), sheet.siteY()) == World.ICE
                        && EarthSite.ICE_WORDS.equals(sheet.refusal()) && EarthSite.ICE_WORDS.equals(EarthSite.foundingProblem(sheetKey)) && adds(sheet),
                String.format("%.0f%% ice, %.0f%% dry within 5 km", 100 * sheet.iceShare(), 100 * sheet.dryShare()));
        World.Key marginKey = World.Key.earth(SEED, MARGIN[0], MARGIN[1], EarthData.EARTH_2);
        EarthSite.Card margin = EarthSite.measure(marginKey);
        World mw = World.of(marginKey);
        double[] ll = new double[2];
        mw.earthTerrain().projection().latLonOfPlot(margin.siteX() + 0.5, margin.siteY() + 0.5, ll);
        boolean bit = (file.at("landcover", ll[0], ll[1]) & EarthData.LC_ICE) != 0;
        byte mp = mw.terrainAt(margin.siteX(), margin.siteY());
        report("...on south Greenland's margin, a site on grass in a 2' cell with the ice bit (the bit's rule refused it): not on ice, as its plot is not, and foundable",
                margin.hasSite() && bit && mp != World.ICE && World.dry(mp) && !margin.ice() && margin.foundable() && EarthSite.foundingProblem(marginKey) == null && adds(margin),
                String.format("the site %s, its cell's ice bit %s; %.1f%% ice, %.1f%% dry within 5 km", LandMap.groundWords(mp), bit, 100 * margin.iceShare(), 100 * margin.dryShare()));
        World.Key offKey = World.Key.earth(SEED, OFF_THE_ICE[0], OFF_THE_ICE[1], EarthData.EARTH_2);
        EarthSite.Card off = EarthSite.measure(offKey);
        World ow = World.of(offKey);
        byte op = ow.terrainAt(EarthTerrain.spotPlot(), EarthTerrain.spotPlot());
        report("...and a spot on the ice there: the snap finds its site on dry ground off the ice, never on it, and the card agrees",
                op == World.ICE && off.hasSite() && World.dry(ow.terrainAt(off.siteX(), off.siteY())) && !off.ice() && off.foundable()
                        && ow.foundingX() == off.siteX() && ow.foundingY() == off.siteY() && off.snappedM() >= 1000 && adds(off),
                String.format("the site %.0f m off, on %s; %.1f%% ice within 5 km", off.snappedM(), LandMap.groundWords(ow.terrainAt(off.siteX(), off.siteY())), 100 * off.iceShare()));
        // The books on a real holding: a city founded at the margin, an unowned tile of ice and one of grass beside it.
        CityLand land = CityLand.found(mw, mw.foundingX(), mw.foundingY(), 1.0);
        long[] iceTile = tileOf(mw, land, World.ICE), grassTile = tileOf(mw, land, World.GRASS);
        double[] iceKm2 = iceTile == null ? null : land.groundOf(iceTile[0], iceTile[1], iceTile[0] + World.TILE, iceTile[1] + World.TILE);
        double tileKm2 = World.TILE * World.TILE * World.KM2_PER_PLOT;
        report("...and a holding's books there: a tile of ice offered is its whole area and no dry ground, fresh, sea or forest", iceKm2 != null && grassTile != null
                        && iceKm2[CityLand.TOTAL] == tileKm2 && iceKm2[CityLand.DRY] == 0 && iceKm2[CityLand.FOREST] == 0 && iceKm2[CityLand.FRESH] == 0 && iceKm2[CityLand.SEA] == 0
                        && land.centreKm2(CityLand.DRY) >= 1.0 - World.KM2_PER_PLOT,
                iceTile == null ? "no tile all ice within reach" : String.format("the tile %.4f km2; the city's centre %.3f km2 dry, %.3f in all", iceKm2[CityLand.TOTAL], land.centreKm2(CityLand.DRY), land.centreKm2(CityLand.TOTAL)));
        // The desert: the card's warning, the site's plot dry ground, the far view's pixels.
        World.Key saharaKey = World.Key.earth(SEED, SAHARA[0], SAHARA[1], EarthData.EARTH_2);
        EarthSite.Card sahara = EarthSite.measure(saharaKey);
        World hw = World.of(saharaKey);
        byte hp = hw.terrainAt(sahara.siteX(), sahara.siteY());
        CityLand dune = CityLand.found(hw, hw.foundingX(), hw.foundingY(), 1.0);
        long[] desertTile = tileOf(hw, dune, World.DESERT);
        double[] desertKm2 = desertTile == null ? null : dune.groundOf(desertTile[0], desertTile[1], desertTile[0] + World.TILE, desertTile[1] + World.TILE);
        report("the Sahara: the site a plot of desert, dry ground the snap founds on, the card warns of a desert, and a tile of it is all dry ground on the books",
                hp == World.DESERT && sahara.foundable() && sahara.desert() && adds(sahara) && desertKm2 != null && desertKm2[CityLand.DRY] == tileKm2 && desertKm2[CityLand.TOTAL] == tileKm2,
                String.format("the site %s; %.1f%% dry within 5 km", LandMap.groundWords(hp), 100 * sahara.dryShare()));
        String[] sheetFar = new String[1], saharaFar = new String[1];
        boolean farOk = far(sheetKey, World.ICE, sheetFar) & far(saharaKey, World.DESERT, saharaFar);
        report("the far view draws them: far nodes on Greenland's sheet ice, on the Sahara desert - all of it at WATER_STRIDE and FAR_STRIDE / 4, the most of it from the 4' layers at FAR_STRIDE",
                farOk, "the sheet " + sheetFar[0] + "; the Sahara " + saharaFar[0]);
    }

    /* =====================================================================
       16. THE SHALLOW SEA (ET1, spec-earth2-terrain 6.1, D11 and 12's 6)
       ===================================================================== */

    /** The shallow share of a window's sea drawn is the data's within this, in points: COVER_POINTS, the bound ice's and the desert's share of a cell is held to (the sea-weighted share of cells whose coast the fray moves a plot or two). */
    static final double SHALLOW_POINTS = COVER_POINTS;

    /** The tile rule (draw()) and the point rule (classAt()) split the sea alike on all but this share of the plots both call sea, in points: half a point (the lattice blends the shares between points 120 m apart, the point rule reads them where the plot is, so a plot within a hair of the noise's threshold may differ). */
    static final double TWO_COPIES_POINTS = 0.5;

    /** How far round a spot the Narrows' and the Basin's plots are all held to their class, in plots: 5 (150 m, a Narrows is a few hundred metres across). */
    static final int SPOT_REACH = 5;

    /** The Halifax Narrows between Bedford Basin and the harbour, and Bedford Basin's middle (the fixture holds the Narrows' tiles: tools/earth/fixture.py NARROWS). */
    static final double[] NARROWS = { 44.67, -63.59 }, BASIN = { 44.70, -63.64 };

    static void shallowSea(EarthData file, Map<String, Window> windows, EarthData fixture, String id) throws Exception {
        out.println("\n--- 16. the shallow sea: sea no deeper than 30 m, sea to every rule, drawn where the data's share beats its noise, weighted by the sea ---");
        boolean cls = World.SHALLOW == 7 && World.CLASSES == World.SHALLOW + 1 && World.salt(World.SALT) && World.salt(World.SHALLOW)
                && !World.salt(World.FRESH) && !World.salt(World.GRASS) && !World.salt(World.ICE) && !World.salt(World.SAND) && !World.salt(-1) && !World.salt(World.CLASSES)
                && !World.dry(World.SHALLOW) && EarthData.SHALLOW_M == 30;
        check("World.SHALLOW is the byte after DESERT, 7, and World.salt() is SALT and SHALLOW alone: not fresh water, not ice, not what is not a class; never dry", cls);
        int lighter = TileRaster.GROUND[World.SHALLOW];
        check("...with its own colour 0x4f87b5, lighter than the deep sea's and darker than the lakes', and its own words on the hover and in the legend",
                TileRaster.GROUND.length == World.CLASSES && lighter == 0xff4f87b5 && lighter != TileRaster.GROUND[World.SALT] && lighter != TileRaster.GROUND[World.FRESH]
                        && luma(lighter) > luma(TileRaster.GROUND[World.SALT]) && luma(lighter) < luma(TileRaster.GROUND[World.FRESH])
                        && LandMap.groundWords(World.SHALLOW).equals(LandMap.SHALLOW_WORDS) && LandMap.SHALLOW_WORDS.startsWith("Shallow sea")
                        && LandMap.legend(GlobeFrame.PICTURE).stream().anyMatch(en -> en.words().equals(LandMap.SHALLOW_WORDS) && en.colour() == lighter));
        long n = 1000;
        double[] shallow = GridConversion.km2Of(only(World.SHALLOW, n)), salt = GridConversion.km2Of(only(World.SALT, n));
        check("the books: a shallow sea's plots count as a sea's in the sea area and the whole, never dry, fresh or forest", Arrays.equals(shallow, salt)
                && shallow[CityLand.SEA] == n * World.KM2_PER_PLOT && shallow[CityLand.TOTAL] == n * World.KM2_PER_PLOT && shallow[CityLand.DRY] == 0
                && shallow[CityLand.FRESH] == 0 && shallow[CityLand.FOREST] == 0);
        check("the runs (CityRuns): the shallow sea is not dry ground, as the sea is not", !CityRuns.dry(World.SHALLOW) && CityRuns.dry(World.SHALLOW) == CityRuns.dry(World.SALT));
        // The share: the fixture's windows with a sea (Rotterdam's North Sea, Perth's shelf), and Halifax's, where the strait and the harbour are shallow and the Basin deep.
        Window hal = new Window(World.of(World.Key.earth(SEED, NARROWS[0], NARROWS[1], id)), fixture);
        Window[] sea = { windows.get("Rotterdam"), windows.get("Perth"), hal };
        String[] names = { "Rotterdam", "Perth", "Halifax" };
        boolean mixed = false;
        for (int k = 0; k < sea.length; k++) {
            Window w = sea[k];
            double drawn = w.drawnShallow(), want = w.dataShallow, points = 100 * Math.abs(drawn - want);
            mixed |= want > 0.05 && want < 0.95;
            report(names[k] + ": the shallow share of the window's sea drawn is the data's, sea-weighted, within SHALLOW_POINTS",
                    want >= 0 && points <= SHALLOW_POINTS, String.format("%.2f%% drawn against %.2f%% (%.2f points), of %,d plots of sea", 100 * drawn, 100 * want, points,
                            w.classes[World.SALT] + w.classes[World.SHALLOW]));
        }
        check("...and a window holds both kinds of sea, so the share is no mere all or nothing (a share between 5% and 95% in the data)", mixed);
        // The rule's two copies: the tile's (draw()) and the point's (classAt(), the regions' and the far views'), on the plots both call sea.
        byte[] t = new byte[World.TILE * World.TILE];
        for (int k = 1; k < sea.length; k++) {
            Window w = sea[k];
            EarthTerrain e = w.world.earthTerrain();
            long sampled = 0, both = 0, differ = 0;
            for (long ty = w.y0 / World.TILE; ty < (w.y0 + WINDOW) / World.TILE; ty++) {
                for (long tx = w.x0 / World.TILE; tx < (w.x0 + WINDOW) / World.TILE; tx++) {
                    w.world.tileTerrain(tx, ty, t);
                    for (int p = 0; p < t.length; p += 7) {
                        if (!World.salt(t[p])) continue;
                        sampled++;
                        byte q = e.pointTerrain(tx * World.TILE + p % World.TILE, ty * World.TILE + p / World.TILE);
                        if (!World.salt(q)) continue;
                        both++;
                        if (q != t[p]) differ++;
                    }
                }
            }
            double points = 100.0 * differ / Math.max(1, both);
            report(names[k] + ": the tile rule and the point rule split the sea alike, SALT or SHALLOW, on all but TWO_COPIES_POINTS of the plots both call sea",
                    both >= 0.99 * sampled && points <= TWO_COPIES_POINTS && w.classes[World.SHALLOW] > 0 && w.classes[World.SALT] > 0,
                    String.format("%,d of %,d sampled sea plots, %,d differ (%.3f points)", both, sampled, differ, points));
        }
        // The Narrows is shallow and Bedford Basin deep, plot by plot round the spot.
        World hw = hal.world;
        double[] p = new double[2];
        EarthProj proj = hw.earthTerrain().projection();
        proj.plotOf(NARROWS[0], NARROWS[1], p);
        long nx = (long) Math.floor(p[0]), ny = (long) Math.floor(p[1]);
        proj.plotOf(BASIN[0], BASIN[1], p);
        long bx = (long) Math.floor(p[0]), by = (long) Math.floor(p[1]);
        int narrowsSea = 0, narrowsShallow = 0, basinSea = 0, basinDeep = 0;
        for (int dy = -SPOT_REACH; dy <= SPOT_REACH; dy++) {
            for (int dx = -SPOT_REACH; dx <= SPOT_REACH; dx++) {
                byte a = hw.terrainAt(nx + dx, ny + dy), b = hw.terrainAt(bx + dx, by + dy);
                if (World.salt(a)) { narrowsSea++; if (a == World.SHALLOW) narrowsShallow++; }
                if (World.salt(b)) { basinSea++; if (b == World.SALT) basinDeep++; }
            }
        }
        report("Halifax: the Narrows (44.67 N, 63.59 W) is shallow sea and Bedford Basin's middle (44.70 N, 63.64 W) deep, every sea plot within SPOT_REACH of each",
                hw.terrainAt(nx, ny) == World.SHALLOW && hw.terrainAt(bx, by) == World.SALT && narrowsSea > 0 && narrowsShallow == narrowsSea && basinSea > 0 && basinDeep == basinSea,
                String.format("the Narrows %d of %d sea plots shallow, the Basin %d of %d deep", narrowsShallow, narrowsSea, basinDeep, basinSea));
        // No fringe of deep sea along a shallow coast (the study's 12, 6): a sea plot whose 3 x 3 cells of the data hold sea only in cells that are all
        // shallow (shallow level 15) is shallow, however much land the cells about it hold - read unweighted, a coast cell's land would pull the share down.
        EarthProj.Blend blend;
        long qualifying = 0, atCoast = 0, deep = 0;
        double[] ll = new double[2];
        for (long ty = hal.y0 / World.TILE; ty < (hal.y0 + WINDOW) / World.TILE; ty++) {
            for (long tx = hal.x0 / World.TILE; tx < (hal.x0 + WINDOW) / World.TILE; tx++) {
                hw.tileTerrain(tx, ty, t);
                blend = proj.blend(tx * World.TILE, ty * World.TILE, World.TILE);
                for (int q = 0; q < t.length; q += 11) {
                    if (!World.salt(t[q]) || !blend.latLon(tx * World.TILE + q % World.TILE + 0.5, ty * World.TILE + q / World.TILE + 0.5, ll)) continue;
                    boolean allShallow = true, land = false;
                    for (int dj = -1; dj <= 1; dj++) {
                        for (int di = -1; di <= 1; di++) {
                            double la = ll[0] + dj / 240.0, lo = ll[1] + di / 240.0;
                            int seaLevel = fixture.at("sea", la, lo);
                            if (seaLevel == 0) land = true;
                            else if (fixture.at("shallow", la, lo) != EarthData.SHARE_LEVELS) allShallow = false;
                        }
                    }
                    if (!allShallow) continue;
                    qualifying++;
                    if (land) atCoast++;
                    if (t[q] == World.SALT) deep++;
                }
            }
        }
        report("Halifax: no fringe of deep sea along a shallow coast - a sea plot whose cells' sea is all shallow is shallow, with land in those cells too (the sea-weighted share)",
                qualifying > 1000 && atCoast > 100 && deep <= qualifying / 1000, String.format("%,d such plots sampled, %,d with land in their cells, %,d of them deep", qualifying, atCoast, deep));
        // The cost: tiles about a coast holding shallow and deep, the median of rounds, within TILE_MS.
        long tx0 = EarthTerrain.spotPlot() / World.TILE - TIMED_TILES / 2, ty0 = tx0;
        double[] ms = new double[TILE_ROUNDS];
        for (int round = -1; round < TILE_ROUNDS; round++) {
            long t0 = System.nanoTime();
            for (int j = 0; j < TIMED_TILES; j++) for (int i = 0; i < TIMED_TILES; i++) hw.tileTerrain(tx0 + i, ty0 + j, t);
            if (round >= 0) ms[round] = (System.nanoTime() - t0) / 1e6 / (TIMED_TILES * TIMED_TILES);
        }
        Arrays.sort(ms);
        report("Halifax: a tile's ground, its shallow sea drawn, in no more than TILE_MS", Timing.within(ms[TILE_ROUNDS / 2], TILE_MS),
                String.format("%.3f ms a tile (rounds %.3f to %.3f)", ms[TILE_ROUNDS / 2], ms[0], ms[TILE_ROUNDS - 1]));
        // A generated world has none (D19): its far view of the whole world holds sea and no SHALLOW, and so do the tiles about its founding site.
        World g = World.of(SEED);
        byte[] far = new byte[64 * 64];
        g.regionTerrain(0, 0, (int) (World.SIDE / 64), 64, far);
        int farSea = 0, farShallow = 0, siteSea = 0, siteShallow = 0;
        for (byte b : far) { if (b == World.SALT) farSea++; if (b == World.SHALLOW) farShallow++; }
        long cx = g.foundingX() / World.TILE, cy = g.foundingY() / World.TILE;
        for (long ty = cy - 24; ty < cy + 24; ty++) {
            for (long tx = cx - 24; tx < cx + 24; tx++) {
                g.tileTerrain(tx, ty, t);
                for (byte b : t) { if (b == World.SALT) siteSea++; if (b == World.SHALLOW) siteShallow++; }
            }
        }
        report("a generated world draws no shallow sea (D19): its whole far view holds sea and no SHALLOW, nor do the tiles about its founding site",
                farSea > 0 && farShallow == 0 && siteShallow == 0, String.format("%,d sea pixels of 4,096; %,d sea plots about the site", farSea, siteSea));
        if (file == null) {
            out.println("earth-2.bin not named: the full file's shallow shares, and earth-1's refusal to draw any, were not tried");
            return;
        }
        // The full file: the shallow share of a 61 km window of the Earth's sea at four coasts; and an earth-1 file, which has no layer, draws none.
        for (double[] at : SHELVES) {
            Window w = new Window(World.of(World.Key.earth(SEED, at[0], at[1], EarthData.EARTH_2)), file);
            double drawn = w.drawnShallow(), want = w.dataShallow, points = 100 * Math.abs(drawn - want);
            report(String.format("the full file at %.2f, %.2f: the shallow share of the window's sea drawn is the data's within SHALLOW_POINTS", at[0], at[1]),
                    want >= 0 && points <= SHALLOW_POINTS && w.classes[World.SHALLOW] > 0, String.format("%.2f%% drawn against %.2f%% (%.2f points), of %,d plots of sea",
                            100 * drawn, 100 * want, points, w.classes[World.SALT] + w.classes[World.SHALLOW]));
        }
        Path one = EarthData.locate().resolveSibling("earth-1.bin");
        if (!Files.isRegularFile(one)) {
            out.println("earth-1.bin is not beside the named file: that a file with no shallow layer draws none was not tried");
            return;
        }
        EarthData old = EarthData.open(one);
        String oldId = EarthTerrain.use(old);
        World ow = World.of(World.Key.earth(SEED, 51.90625, 4.48125, oldId));
        int oldSea = 0, oldShallow = 0;
        long oc = EarthTerrain.spotPlot() / World.TILE;
        for (long ty = oc - 16; ty < oc + 16; ty++) {
            for (long tx = oc - 16; tx < oc + 16; tx++) {
                ow.tileTerrain(tx, ty, t);
                for (byte b : t) { if (b == World.SALT) oldSea++; if (b == World.SHALLOW) oldShallow++; }
            }
        }
        boolean refused = false;
        for (long x = EarthTerrain.spotPlot() - 3000; x < EarthTerrain.spotPlot() && !refused; x += 50) {
            if (!World.salt(ow.terrainAt(x, EarthTerrain.spotPlot()))) continue;
            try { ow.depthAt(x, EarthTerrain.spotPlot()); } catch (IllegalStateException e) { refused = true; }
        }
        check("an earth-1 file, which has no shallow layer, draws no SHALLOW where it draws the sea (" + oldSea + " plots), and gives no depth it does not have",
                !old.hasRelief() && oldSea > 0 && oldShallow == 0 && refused);
    }

    /* =====================================================================
       17. THE RELIEF PLANE (ET1, spec-earth2-terrain 3.1, D4, D5 and D19)
       ===================================================================== */

    /** Each relief class's share of a window's dry ground drawn is the data's within this, in points, for a window of RELIEF_REF_PLOTS dry plots: SHARE_POINTS, section 15's bound - and more for a window with less dry ground, by the root of the ratio (see tolerance()). */
    static final double RELIEF_POINTS = SHARE_POINTS;

    /** The dry plots a 61 km window of land holds: 2,048 squared, 4,194,304 - the size RELIEF_POINTS is the bound for. A slope is drawn as patches a kilometre or two across (EarthTerrain.RELIEF_SHIFT), so a window holds as many independent patches as it holds ground, and the share it draws strays from the data's by a spread that grows as the root of the window shrinks: measured, 0.6 to 1.9 points at 61 km on the full file's six hills (0 to 0.8 where a window holds little slope), and 2.6 on Fiji's 0.56 million dry plots. */
    static final long RELIEF_REF_PLOTS = (long) WINDOW * WINDOW;

    /** The relief's two copies (the tile's drawRelief() and the point's reliefOf()) give a dry plot the same class on all but this share of the plots both draw dry, in points: 1 (the lattice blends the shares between points 120 m apart where the point rule reads them where the plot is, so a plot within a hair of a threshold may differ; measured 0.4). */
    static final double RELIEF_COPIES_POINTS = 1.0;

    /** A far node's relief shares are the data's cells' at its dry pixels within this, in points: twice RELIEF_POINTS (a node's 16,384 pixels are a sample, a pixel a hash's draw against the share), where it holds at least FAR_DRY_LEAST dry pixels. */
    static final double FAR_POINTS = 2 * RELIEF_POINTS;
    static final int FAR_DRY_LEAST = 2000;

    /** Medellin's valley floor, the centre of the city (the study's 2.3: 98% flat in a 3 x 3 of 15" cells, 1,482 m), and the middle of the fixture's window about it (tools/earth/fixture.py MEDELLIN, one 1-degree tile). */
    static final double[] FLOOR = { 6.25, -75.57 }, VALLEY = { 6.25, -75.58 };

    /** Everest's summit area: snow and ice on all its high ground (the study's 12, 7), the rock between its glaciers the only dry land. */
    static final double[] SUMMIT = { 27.99, 86.93 };

    /** The full file's hilly spots, {lat, lon}: Medellin, Everest's, the Upper Engadin, Hong Kong (a steep coast), La Paz and Kathmandu - their classes in the study's table of 2.3. */
    static final double[][] HILLS = { VALLEY, SUMMIT, { 46.7, 10.3 }, { 22.3, 114.2 }, { -16.5, -68.1 }, { 27.7, 85.3 } };
    static final String[] HILL_NAMES = { "Medellin", "Everest", "the Upper Engadin", "Hong Kong", "La Paz", "Kathmandu" };

    /** How far round the floor's spot the plots are all held FLAT, in plots: 5 (150 m); and the share of the plots within FLOOR_WIDE (17: 510 m) that are flat, at least. */
    static final int FLOOR_REACH = 5, FLOOR_WIDE = 17;
    static final double FLOOR_FLAT = 0.95;

    /**
     * A window of plots - the world cell's middle, side x side of them - drawn (cover and relief) and laid on the data's slope:
     * its dry plots counted by relief against what the data's cells say of them (each plot's own 15" cell, EarthData.slope()
     * decoded: the plot's expected share of each class), and the relief a plot that is not dry ground carries counted apart.
     */
    static final class ReliefWindow {
        final World world;
        final EarthData data;
        final long x0, y0;
        final int side;
        final byte[] cover, relief;
        final long[] drawn = new long[World.RELIEFS], classes = new long[World.CLASSES];
        final double[] want = new double[World.RELIEFS];
        long dry, lost;
        /** Dry plots of each cover with a relief above FLAT, by cover class. */
        final long[] sloped = new long[World.CLASSES];

        ReliefWindow(World world, EarthData data, int side) {
            this.world = world;
            this.data = data;
            this.side = side;
            x0 = EarthTerrain.spotPlot() - side / 2;
            y0 = x0;
            cover = new byte[side * side];
            relief = new byte[side * side];
            EarthProj proj = world.earthTerrain().projection();
            byte[] t = new byte[World.TILE * World.TILE], r = new byte[World.TILE * World.TILE];
            double[] ll = new double[2];
            int tiles = side / World.TILE;
            for (int j = 0; j < tiles; j++) {
                for (int i = 0; i < tiles; i++) {
                    long tx = x0 / World.TILE + i, ty = y0 / World.TILE + j;
                    world.tileTerrain(tx, ty, t);
                    world.tileRelief(tx, ty, r);
                    EarthProj.Blend b = proj.blend(tx * World.TILE, ty * World.TILE, World.TILE);
                    for (int p = 0; p < t.length; p++) {
                        int at = (j * World.TILE + p / World.TILE) * side + i * World.TILE + p % World.TILE;
                        cover[at] = t[p];
                        relief[at] = r[p];
                        classes[t[p]]++;
                        if (!World.dry(t[p])) {
                            if (r[p] != World.FLAT) lost++;
                            continue;
                        }
                        dry++;
                        drawn[r[p]]++;
                        if (r[p] != World.FLAT) sloped[t[p]]++;
                        b.latLon(tx * World.TILE + p % World.TILE + 0.5, ty * World.TILE + p / World.TILE + 0.5, ll);
                        int s = data.slope(ll[0], ll[1]);
                        double f = EarthData.slopeFoothill(s), st = EarthData.slopeSteep(s), v = EarthData.slopeVerySteep(s);
                        want[0] += 1 - f;
                        want[1] += f - st;
                        want[2] += st - v;
                        want[3] += v;
                    }
                }
            }
        }

        double drawnShare(int k) { return dry > 0 ? drawn[k] / (double) dry : 0; }

        double wantShare(int k) { return dry > 0 ? want[k] / dry : 0; }

        /** The largest gap between a class's share drawn and the data's, in points. */
        double points() {
            double worst = 0;
            for (int k = 0; k < World.RELIEFS; k++) worst = Math.max(worst, 100 * Math.abs(drawnShare(k) - wantShare(k)));
            return worst;
        }

        /** The bound on points(): RELIEF_POINTS, and past it by the root of the window's dry ground against RELIEF_REF_PLOTS. */
        double tolerance() { return RELIEF_POINTS * Math.sqrt(Math.max(1.0, RELIEF_REF_PLOTS / (double) Math.max(1, dry))); }

        /** Whether the data holds slope under the window: at least 5% of its dry ground at or above foothill. */
        boolean hilly() { return wantShare(0) < 0.95; }

        /** Whether it holds steep ground: at least 5% of its dry ground at or above steep. */
        boolean steep() { return wantShare(2) + wantShare(3) >= 0.05; }

        String shares(boolean data) {
            return String.format("%.1f/%.1f/%.1f/%.1f%%", 100 * (data ? wantShare(0) : drawnShare(0)), 100 * (data ? wantShare(1) : drawnShare(1)),
                    100 * (data ? wantShare(2) : drawnShare(2)), 100 * (data ? wantShare(3) : drawnShare(3)));
        }
    }

    /** The relief about a point (its lat, lon), plots dx, dy of it, for a window's world: counted by class over the dry plots within `reach`. */
    static long[] reliefAbout(World w, double lat, double lon, int reach) {
        double[] p = new double[2];
        w.earthTerrain().projection().plotOf(lat, lon, p);
        long cx = (long) Math.floor(p[0]), cy = (long) Math.floor(p[1]);
        long[] n = new long[World.RELIEFS];
        for (int dy = -reach; dy <= reach; dy++) {
            for (int dx = -reach; dx <= reach; dx++) {
                if (World.dry(w.terrainAt(cx + dx, cy + dy))) n[w.reliefAt(cx + dx, cy + dy)]++;
            }
        }
        return n;
    }

    static void reliefPlane(EarthData file, Map<String, Spot> spots, EarthData fixture, String id) throws Exception {
        out.println("\n--- 17. the relief plane: four classes of slope beside the cover, drawn from the data's shares of the land, none on water or ice, the cover untouched ---");
        // The classes and the two rules that read them (no model rule calls either yet: ET2's).
        boolean classes = World.FLAT == 0 && World.FOOTHILL == 1 && World.STEEP == 2 && World.VERY_STEEP == 3 && World.RELIEFS == 4;
        check("World's relief classes are FLAT 0, FOOTHILL 1, STEEP 2 and VERY_STEEP 3, RELIEFS 4 in all: a second plane, its own bytes", classes);
        boolean rules = true, work = true;
        for (int c = -1; c <= World.CLASSES; c++) {
            for (int r = -1; r <= World.RELIEFS; r++) {
                boolean real = c >= 0 && c < World.CLASSES && r >= 0 && r < World.RELIEFS;
                rules &= World.buildable(c, r) == (real && World.dry(c) && r <= World.FOOTHILL);
            }
            boolean land = c >= 0 && c < World.CLASSES && c != World.SALT && c != World.SHALLOW && c != World.FRESH;
            work &= World.workable(c) == land;
        }
        check("World.buildable(cover, relief) is dry ground on a FLAT or FOOTHILL plot: no sea, fresh water or ice, and no STEEP or VERY_STEEP ground, forest or not", rules
                && World.buildable(World.FOREST, World.FOOTHILL) && !World.buildable(World.FOREST, World.STEEP) && !World.buildable(World.ICE, World.FLAT));
        check("World.workable(cover) is any land, ice and the beach among it: not the sea (SALT, SHALLOW), not fresh water, not what is not a cover", work
                && World.workable(World.ICE) && World.workable(World.SAND) && !World.workable(World.SHALLOW) && !World.workable(World.FRESH));
        // A generated world is flat everywhere (D19), and answers without drawing a noise.
        World g = World.of(SEED);
        byte[] t = new byte[World.TILE * World.TILE], r = new byte[World.TILE * World.TILE], region = new byte[64 * 64];
        long cx = g.foundingX() / World.TILE, cy = g.foundingY() / World.TILE;
        boolean flat = true, ground = true;
        long landG = 0;
        for (long ty = cy - 16; ty < cy + 16; ty++) {
            for (long tx = cx - 16; tx < cx + 16; tx++) {
                g.tileTerrain(tx, ty, t);
                Arrays.fill(r, (byte) 9);
                g.tileRelief(tx, ty, r);
                byte[] again = new byte[t.length];
                g.tileTerrain(tx, ty, again);
                ground &= Arrays.equals(t, again);
                for (int p = 0; p < r.length; p++) {
                    flat &= r[p] == World.FLAT && g.reliefAt(tx * World.TILE + p % World.TILE, ty * World.TILE + p / World.TILE) == World.FLAT;
                    if (World.dry(t[p])) landG++;
                }
            }
        }
        Arrays.fill(region, (byte) 9);
        g.regionRelief(0, 0, (int) (World.SIDE / 64), 64, region);
        for (byte b : region) flat &= b == World.FLAT;
        flat &= g.reliefAt(-5, 7) == World.FLAT && g.reliefAt(World.SIDE, 0) == World.FLAT;
        report("a generated world's relief is FLAT everywhere (D19): every plot of the 32 x 32 tiles about its founding site, a far region, off the world - and asking for it changes none of its ground",
                flat && ground && landG > 0, String.format("%,d dry plots about the site", landG));
        if (fixture == null) return;
        // The windows the relief is drawn on: the fixture's, each said to hold slope or not.
        List<String> names = new ArrayList<>();
        List<ReliefWindow> ws = new ArrayList<>();
        for (String n : List.of("Rotterdam", "Perth", "Paris", "Fiji")) {
            Spot s = spots.get(n);
            names.add(n);
            ws.add(new ReliefWindow(World.of(World.Key.earth(SEED, s.lat(), s.lon(), id)), fixture, WINDOW));
        }
        names.add("Halifax");
        ws.add(new ReliefWindow(World.of(World.Key.earth(SEED, NARROWS[0], NARROWS[1], id)), fixture, WINDOW));
        names.add("Medellin's valley (30 km)");
        ws.add(new ReliefWindow(World.of(World.Key.earth(SEED, VALLEY[0], VALLEY[1], id)), fixture, WINDOW / 2));
        reliefWindows("the fixture", names, ws, true);
        // The floor of the valley and the hillsides about it, on the fixture's tile.
        World valley = ws.get(ws.size() - 1).world;
        long[] floor = reliefAbout(valley, FLOOR[0], FLOOR[1], FLOOR_REACH), wide = reliefAbout(valley, FLOOR[0], FLOOR[1], FLOOR_WIDE), hills = reliefAbout(valley, FLOOR[0], FLOOR[1], 300);
        long floorAll = Arrays.stream(floor).sum(), wideAll = Arrays.stream(wide).sum(), hillsAll = Arrays.stream(hills).sum();
        report("Medellin's valley floor (6.25 N, 75.57 W) reads FLAT: every dry plot within FLOOR_REACH of it, nearly all within FLOOR_WIDE, while the hills about it (9 km) are steep",
                floorAll > 0 && floor[World.FLAT] == floorAll && wide[World.FLAT] >= FLOOR_FLAT * wideAll && hills[World.STEEP] + hills[World.VERY_STEEP] > hillsAll / 20,
                String.format("%d of %d plots flat within %d; %d of %d within %d; the hills %d/%d/%d/%d", floor[0], floorAll, FLOOR_REACH, wide[0], wideAll, FLOOR_WIDE,
                        hills[0], hills[1], hills[2], hills[3]));
        if (file != null) {
            names.clear();
            ws.clear();
            for (int k = 0; k < HILLS.length; k++) {
                names.add(HILL_NAMES[k]);
                ws.add(new ReliefWindow(World.of(World.Key.earth(SEED, HILLS[k][0], HILLS[k][1], EarthData.EARTH_2)), file, WINDOW));
            }
            reliefWindows("the full file", names, ws, false);
            World top = ws.get(1).world;
            long[] summit = reliefAbout(top, SUMMIT[0], SUMMIT[1], 300), core = reliefAbout(top, SUMMIT[0], SUMMIT[1], 33);
            ReliefWindow everest = ws.get(1);
            double[] sp = new double[2];
            top.earthTerrain().projection().plotOf(SUMMIT[0], SUMMIT[1], sp);
            long sx = (long) Math.floor(sp[0]), sy = (long) Math.floor(sp[1]), iceCore = 0, iceSloped = 0;
            for (int dy = -33; dy <= 33; dy++) {
                for (int dx = -33; dx <= 33; dx++) {
                    if (top.terrainAt(sx + dx, sy + dy) != World.ICE) continue;
                    iceCore++;
                    if (top.reliefAt(sx + dx, sy + dy) != World.FLAT) iceSloped++;
                }
            }
            report("Everest: the summit's own ground, a kilometre round, is ice and FLAT in the plane (D4: snow wins over slope), and the rock about it, 9 km round, has VERY_STEEP and STEEP plots among its non-ice land",
                    iceCore > 4000 && iceSloped == 0 && Arrays.stream(core).sum() == 0 && summit[World.VERY_STEEP] > 0 && summit[World.STEEP] > 0 && everest.lost == 0,
                    String.format("%,d plots of ice within 1 km, none with relief; 9 km round, the dry plots %d/%d/%d/%d; %,d plots of ice in the window against %,d dry", iceCore,
                            summit[0], summit[1], summit[2], summit[3], everest.classes[World.ICE], everest.dry));
            long[] full = reliefAbout(ws.get(0).world, FLOOR[0], FLOOR[1], FLOOR_REACH), fullWide = reliefAbout(ws.get(0).world, FLOOR[0], FLOOR[1], FLOOR_WIDE);
            report("...and Medellin's valley floor reads FLAT on the full file too, every dry plot within FLOOR_REACH and nearly all within FLOOR_WIDE",
                    Arrays.stream(full).sum() > 0 && full[World.FLAT] == Arrays.stream(full).sum() && fullWide[World.FLAT] >= FLOOR_FLAT * Arrays.stream(fullWide).sum(),
                    String.format("%d of %d plots flat within %d; %d of %d within %d", full[0], Arrays.stream(full).sum(), FLOOR_REACH, fullWide[0], Arrays.stream(fullWide).sum(), FLOOR_WIDE));
        }
        reliefRule(fixture, file, id);
        reliefFar(file);
        // An earth-1 file has no slope layer: every plot FLAT, its cover as it was.
        if (file == null) {
            out.println("earth-2.bin not named: the full file's hills, Everest's summit, the far views' relief at the stride of the globe and the earth-1 file's flatness were not tried");
            return;
        }
        Path one = EarthData.locate().resolveSibling("earth-1.bin");
        if (!Files.isRegularFile(one)) {
            out.println("earth-1.bin is not beside the named file: that a file with no slope layer draws every plot flat was not tried");
            return;
        }
        EarthData old = EarthData.open(one);
        String oldId = EarthTerrain.use(old);
        World ow = World.of(World.Key.earth(SEED, 46.7, 10.3, oldId));
        long oc = EarthTerrain.spotPlot() / World.TILE;
        boolean allFlat = true;
        long oldDry = 0;
        for (long ty = oc - 8; ty < oc + 8; ty++) {
            for (long tx = oc - 8; tx < oc + 8; tx++) {
                ow.tileTerrain(tx, ty, t);
                Arrays.fill(r, (byte) 9);
                ow.tileRelief(tx, ty, r);
                for (int p = 0; p < r.length; p++) {
                    allFlat &= r[p] == World.FLAT;
                    if (World.dry(t[p])) oldDry++;
                }
            }
        }
        byte[] oldFar = new byte[MapFrame.NODE_PX * MapFrame.NODE_PX], oldRelief = new byte[oldFar.length];
        Arrays.fill(oldRelief, (byte) 9);
        ow.earthTerrain().farTerrain(EarthTerrain.spotPlot() - 64 * 64, EarthTerrain.spotPlot() - 64 * 64, 64, MapFrame.NODE_PX, oldFar, oldRelief);
        for (byte b : oldRelief) allFlat &= b == World.FLAT;
        check("an earth-1 file, which has no slope layer, draws every plot FLAT (" + oldDry + " dry plots in the Alps, a far node over them too): none of the Alps' relief reaches an old save",
                !old.hasRelief() && oldDry > 0 && allFlat);
    }

    /** The share check on each window, the checks that hold of all of them, and which hold slope. */
    static void reliefWindows(String where, List<String> names, List<ReliefWindow> ws, boolean fixtureWindows) {
        int hilly = 0, steep = 0;
        long water = 0, ice = 0, fresh = 0, shallow = 0, forestOnSlope = 0, lost = 0;
        for (int k = 0; k < ws.size(); k++) {
            ReliefWindow w = ws.get(k);
            double points = w.points(), tol = w.tolerance();
            String says = w.hilly() ? (w.steep() ? "holds steep ground" : "holds foothill only") : "holds no slope, so it proves only that none is drawn";
            report(names.get(k) + ": the relief of the dry ground drawn is the data's land-weighted shares, flat/foothill/steep/very steep, within RELIEF_POINTS (scaled to the window's ground)",
                    w.dry > 0 && points <= tol && (w.want[1] + w.want[2] + w.want[3] > 0 || w.drawn[0] == w.dry),
                    String.format("%s against the data's %s: %.2f points of %.2f, of %,d dry plots (%s)", w.shares(false), w.shares(true), points, tol, w.dry, says));
            if (w.hilly()) hilly++;
            if (w.steep()) steep++;
            water += w.classes[World.SALT];
            shallow += w.classes[World.SHALLOW];
            fresh += w.classes[World.FRESH];
            ice += w.classes[World.ICE];
            forestOnSlope += w.sloped[World.FOREST];
            lost += w.lost;
        }
        check(where + "'s windows hold slope for the rule to be tried on: " + hilly + " of " + ws.size() + " hold foothill and " + steep + " steep ground in the data (those that hold none prove only that none is drawn)",
                fixtureWindows ? hilly >= 5 && steep >= 2 : hilly >= 5 && steep >= 5);
        report(where + ": water carries no relief - the deep sea, the shallow sea, fresh water and ice alike are FLAT on every plot of every window",
                lost == 0 && fresh > 0 && (fixtureWindows ? water > 0 && shallow > 0 : water + shallow > 0 && ice > 0),
                String.format("%,d plots of sea, %,d of shallow sea, %,d of fresh water and %,d of ice, none with relief", water, shallow, fresh, ice));
        report(where + ": forest keeps its cover on a slope (D5): " + String.format("%,d", forestOnSlope) + " plots of forest are foothill, steep or very steep, and are forest still",
                forestOnSlope > 0, "the cover is the one tileTerrain() draws");
    }

    /** The rule's own: the cover untouched, the readers one another's, the classes nested within the data, the point rule the tile's, a tile's time. */
    static void reliefRule(EarthData fixture, EarthData file, String id) throws Exception {
        EarthData d = file != null ? file : fixture;
        String did = file != null ? EarthData.EARTH_2 : id;
        World w = World.of(World.Key.earth(SEED, file != null ? HILLS[0][0] : VALLEY[0], file != null ? HILLS[0][1] : VALLEY[1], did));
        EarthTerrain e = w.earthTerrain();
        EarthProj proj = e.projection();
        long c = EarthTerrain.spotPlot();
        // 1. The cover untouched: every reader of the relief leaves tileTerrain() as it was, and a far node's cover is the same with the relief asked for as without.
        byte[] before = new byte[World.TILE * World.TILE], after = new byte[before.length], rel = new byte[before.length];
        boolean same = true, agree = true;
        long sloped = 0;
        for (long ty = c / World.TILE - 6; ty < c / World.TILE + 6; ty++) {
            for (long tx = c / World.TILE - 6; tx < c / World.TILE + 6; tx++) {
                w.tileTerrain(tx, ty, before);
                w.tileRelief(tx, ty, rel);
                e.reliefAt(tx * World.TILE, ty * World.TILE);
                e.regionRelief(tx * World.TILE, ty * World.TILE, 1, 4, new byte[16]);
                w.tileTerrain(tx, ty, after);
                same &= Arrays.equals(before, after);
                for (int p = 0; p < rel.length; p += 5) {
                    long x = tx * World.TILE + p % World.TILE, y = ty * World.TILE + p / World.TILE;
                    agree &= w.reliefAt(x, y) == rel[p];
                    if (rel[p] != World.FLAT) sloped++;
                }
            }
        }
        int n = MapFrame.NODE_PX;
        boolean farSame = true, farFlat = true;
        long farSloped = 0, farDry = 0;
        for (int stride : new int[] { 2, 4, 16, 64, 256 }) {
            long x0 = c - (long) n * stride / 2;
            byte[] plain = new byte[n * n], with = new byte[n * n], relief = new byte[n * n], region = new byte[n * n];
            Arrays.fill(relief, (byte) 9);
            e.farTerrain(x0, x0, stride, n, plain);
            e.farTerrain(x0, x0, stride, n, with, relief);
            farSame &= Arrays.equals(plain, with);
            Arrays.fill(region, (byte) 9);
            e.regionRelief(x0, x0, stride, n, region);
            for (int i = 0; i < relief.length; i++) {
                if (!World.dry(with[i])) farFlat &= relief[i] == World.FLAT;
                else farDry++;
                if (relief[i] != World.FLAT) farSloped++;
                farFlat &= relief[i] >= 0 && relief[i] < World.RELIEFS && region[i] >= 0 && region[i] < World.RELIEFS;
            }
        }
        report("the cover is untouched: tileTerrain() is as it was after every reader of the relief has run on its tile, and a far node's cover is the same byte for byte with the relief asked for as without",
                same && farSame && sloped > 0, String.format("%,d plots with relief in 144 tiles; %,d of %,d dry far pixels at five strides", sloped, farSloped, farDry));
        report("the readers agree: reliefAt() is tileRelief() plot by plot; far nodes and regions give every pixel a relief class, and none to a pixel that is not dry ground",
                agree && farFlat && farSloped > 0, "");
        // 2. The classes nest: the shares at a point keep their order - steep within foothill, very steep within steep, in 0 to 1 - on the 15" cells' blend and on the far blocks'.
        Random rnd = new Random(17);
        double[] ll = new double[2];
        boolean ordered = true;
        long points = 0, withSlope = 0;
        for (int i = 0; i < 20_000; i++) {
            double x = c + (rnd.nextDouble() - 0.5) * 3000, y = c + (rnd.nextDouble() - 0.5) * 3000;
            if (!proj.latLonOfPlot(x, y, ll)) continue;
            for (boolean far : new boolean[] { false, true }) {
                double[] s = e.reliefShares(ll[0], ll[1], far);
                ordered &= s[0] >= s[1] && s[1] >= s[2] && s[2] >= 0 && s[0] <= 1;
                points++;
                if (s[0] > 0) withSlope++;
            }
        }
        report("the classes nest: at every point the share at or above foothill is no less than the steep share and that no less than the very steep, in 0 to 1 - on the cells' blend and on the far blocks'",
                ordered && withSlope > 0, String.format("%,d readings, %,d with slope", points, withSlope));
        // ...and the drawn classes lie where the data has them: a STEEP plot only within a cell of some steep ground, a VERY_STEEP plot of some very steep.
        long checked = 0, bad = 0, varied = 0;
        byte[] cov = new byte[World.TILE * World.TILE];
        for (long ty = c / World.TILE - 12; ty < c / World.TILE + 12; ty++) {
            for (long tx = c / World.TILE - 12; tx < c / World.TILE + 12; tx++) {
                w.tileTerrain(tx, ty, cov);
                w.tileRelief(tx, ty, rel);
                EarthProj.Blend b = proj.blend(tx * World.TILE, ty * World.TILE, World.TILE);
                for (int p = 0; p < rel.length; p += 7) {
                    if (rel[p] == World.FLAT) continue;
                    b.latLon(tx * World.TILE + p % World.TILE + 0.5, ty * World.TILE + p / World.TILE + 0.5, ll);
                    boolean foot = false, st = false, very = false;
                    for (int dj = -1; dj <= 1; dj++) {
                        for (int di = -1; di <= 1; di++) {
                            int s = d.slope(ll[0] + dj / 240.0, ll[1] + di / 240.0);
                            foot |= EarthData.slopeFoothill(s) > 0;
                            st |= EarthData.slopeSteep(s) > 0;
                            very |= EarthData.slopeVerySteep(s) > 0;
                        }
                    }
                    checked++;
                    if (!foot || (rel[p] >= World.STEEP && !st) || (rel[p] == World.VERY_STEEP && !very)) bad++;
                    if (rel[p] >= World.STEEP) varied++;
                }
            }
        }
        report("...and the drawn classes lie where the data has them: a foothill plot beside a cell with foothill ground, a steep one beside steep, a very steep one beside very steep, in the 3 x 3 cells about it",
                checked > 0 && bad == 0 && varied > 0, String.format("%,d sampled plots with relief, %,d out of place, %,d steep or more", checked, bad, varied));
        // 3. The two copies: the tile's and the point's agree on all but RELIEF_COPIES_POINTS of the dry plots.
        long both = 0, differ = 0;
        for (long ty = c / World.TILE - 12; ty < c / World.TILE + 12; ty++) {
            for (long tx = c / World.TILE - 12; tx < c / World.TILE + 12; tx++) {
                w.tileTerrain(tx, ty, cov);
                w.tileRelief(tx, ty, rel);
                for (int p = 0; p < rel.length; p += 3) {
                    if (!World.dry(cov[p])) continue;
                    both++;
                    if (e.pointRelief(tx * World.TILE + p % World.TILE, ty * World.TILE + p / World.TILE, cov[p]) != rel[p]) differ++;
                }
            }
        }
        double pts = 100.0 * differ / Math.max(1, both);
        report("the tile rule and the point rule give a dry plot the same relief on all but RELIEF_COPIES_POINTS of the plots",
                both > 0 && pts <= RELIEF_COPIES_POINTS, String.format("%,d of %,d sampled dry plots differ (%.3f points)", differ, both, pts));
        // 4. The cost of a tile: each round a world of its own (another seed, the same data), so no tile is kept - the cover's tiles first, kept as a map keeps them, then the relief alone.
        long tx0 = c / World.TILE - TIMED_TILES / 2, ty0 = tx0;
        double[] coverMs = new double[TILE_ROUNDS], reliefMs = new double[TILE_ROUNDS];
        byte[] tile = new byte[World.TILE * World.TILE];
        for (int round = -1; round < TILE_ROUNDS; round++) {
            World t0w = World.of(World.Key.earth(SEED + 1000 + round, file != null ? HILLS[0][0] : VALLEY[0], file != null ? HILLS[0][1] : VALLEY[1], did));
            long t0 = System.nanoTime();
            for (int j = 0; j < TIMED_TILES; j++) for (int i = 0; i < TIMED_TILES; i++) t0w.terrainAt((tx0 + i) * World.TILE, (ty0 + j) * World.TILE);
            long t1 = System.nanoTime();
            for (int j = 0; j < TIMED_TILES; j++) for (int i = 0; i < TIMED_TILES; i++) t0w.tileRelief(tx0 + i, ty0 + j, tile);
            long t2 = System.nanoTime();
            if (round >= 0) {
                coverMs[round] = (t1 - t0) / 1e6 / (TIMED_TILES * TIMED_TILES);
                reliefMs[round] = (t2 - t1) / 1e6 / (TIMED_TILES * TIMED_TILES);
            }
        }
        Arrays.sort(coverMs);
        Arrays.sort(reliefMs);
        double both3 = coverMs[TILE_ROUNDS / 2] + reliefMs[TILE_ROUNDS / 2];
        report("a hilly tile's ground and its relief together in no more than TILE_MS (the median of " + TILE_ROUNDS + " rounds over " + TIMED_TILES + " x " + TIMED_TILES
                        + " tiles about Medellin)", Timing.within(both3, TILE_MS),
                String.format("%.3f ms a tile: %.3f for the cover and %.3f for the relief beside it (cover kept; rounds %.3f to %.3f)", both3, coverMs[TILE_ROUNDS / 2],
                        reliefMs[TILE_ROUNDS / 2], reliefMs[0], reliefMs[TILE_ROUNDS - 1]));
    }

    /** The far views' relief: a far node at a stride, the globe's camera, the picture's, their cost against SCREEN_MS and FRAME_MS. */
    static void reliefFar(EarthData file) throws Exception {
        if (file == null) return;
        long c = EarthTerrain.spotPlot();
        int n = MapFrame.NODE_PX;
        World w = World.of(World.Key.earth(SEED, HILLS[0][0], HILLS[0][1], EarthData.EARTH_2));
        EarthTerrain e = w.earthTerrain();
        // The far summary: a 4' block's shares are the land-weighted mean of its 256 cells'.
        Random rnd = new Random(5);
        double worst = 0;
        int blocks = 0;
        for (double[] at : new double[][] { HILLS[0], HILLS[1], HILLS[2], HILLS[3], HILLS[4], HILLS[5], { 46.5, 10.2 }, { 35.7, 139.7 }, { 0.5, -150.5 } }) {
            for (int k = 0; k < 4; k++) {
                double lat = at[0] + (rnd.nextDouble() - 0.5) * 0.4, lon = at[1] + (rnd.nextDouble() - 0.5) * 0.4;
                long gb = (long) Math.floor((90 - lat) * 240) / 16 * 16, cb = (long) Math.floor((lon + 180) * 240) / 16 * 16;
                double land = 0, f = 0, s = 0, v = 0;
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        double la = 90 - (gb + i + 0.5) / 240, lo = -180 + (cb + j + 0.5) / 240;
                        double wgt = Math.max(0, EarthData.SHARE_LEVELS - file.at("sea", la, lo) - file.at("lake", la, lo));
                        int b = file.slope(la, lo);
                        land += wgt;
                        f += wgt * EarthData.slopeFoothill(b);
                        s += wgt * Math.min(EarthData.slopeFoothill(b), EarthData.slopeSteep(b));
                        v += wgt * Math.min(Math.min(EarthData.slopeFoothill(b), EarthData.slopeSteep(b)), EarthData.slopeVerySteep(b));
                    }
                }
                double[] got = e.reliefShares(90 - (gb + 8) / 240.0, -180 + (cb + 8) / 240.0, true);
                if (land > 0) worst = Math.max(worst, Math.max(Math.abs(got[0] - f / land), Math.max(Math.abs(got[1] - s / land), Math.abs(got[2] - v / land))));
                else worst = Math.max(worst, got[0] + got[1] + got[2]);
                blocks++;
            }
        }
        report("the far summary: a 4' block's shares are the land-weighted mean of its 256 cells' (within a level of a byte, 1/255), at " + blocks + " blocks over the hills, a city and the open Pacific",
                worst <= 1.0 / 255 + 1e-9, String.format("the worst %.4f", worst));
        // A far node at each stride over the hills: its relief shares are the data's cells' at its dry pixels; its cost, cold and warm, against SCREEN_MS.
        EarthData fresh = EarthData.open(EarthData.locate());
        String freshId = EarthTerrain.use(fresh);
        EarthTerrain ce = new EarthTerrain(World.Key.earth(SEED, HILLS[0][0], HILLS[0][1], freshId));
        double[] ll = new double[2];
        EarthProj proj = e.projection();
        for (int stride : new int[] { 16, 64, 256, 1024 }) {
            long x0 = c - (long) n * stride / 2;
            byte[] plain = new byte[n * n], relief = new byte[n * n];
            double plainMs = Double.MAX_VALUE, withMs = Double.MAX_VALUE;
            long t0 = System.nanoTime();
            ce.farTerrain(x0, x0, stride, n, plain, relief);
            double cold = (System.nanoTime() - t0) / 1e6;
            for (int round = 0; round < 3; round++) {
                t0 = System.nanoTime();
                e.farTerrain(x0, x0, stride, n, plain);
                plainMs = Math.min(plainMs, (System.nanoTime() - t0) / 1e6);
                t0 = System.nanoTime();
                e.farTerrain(x0, x0, stride, n, plain, relief);
                withMs = Math.min(withMs, (System.nanoTime() - t0) / 1e6);
            }
            long[] drawn = new long[World.RELIEFS];
            double[] want = new double[World.RELIEFS];
            long dry = 0;
            for (int py = 0; py < n; py++) {
                for (int px = 0; px < n; px++) {
                    int i = py * n + px;
                    if (!World.dry(plain[i])) continue;
                    double x = x0 + (px + 0.5) * stride, y = x0 + (py + 0.5) * stride;
                    if (!proj.latLonOfPlot(x, y, ll)) continue;
                    dry++;
                    drawn[relief[i]]++;
                    int b = file.slope(ll[0], ll[1]);
                    double f = EarthData.slopeFoothill(b), s = EarthData.slopeSteep(b), v = EarthData.slopeVerySteep(b);
                    want[0] += 1 - f;
                    want[1] += f - s;
                    want[2] += s - v;
                    want[3] += v;
                }
            }
            double points = 0;
            for (int k = 0; k < World.RELIEFS; k++) points = Math.max(points, 100 * Math.abs(drawn[k] - want[k]) / Math.max(1, dry));
            report("a far node over Medellin at " + stride + " plots a pixel: its relief shares are the cells' within FAR_POINTS (a sample of its pixels), and it is drawn in no more than SCREEN_MS",
                    dry >= FAR_DRY_LEAST && points <= FAR_POINTS && Timing.within(withMs, MapCheck.SCREEN_MS),
                    String.format("%.1f/%.1f/%.1f/%.1f%% against %.1f/%.1f/%.1f/%.1f%% of %,d dry pixels, %.1f points; %.1f ms with the relief, %.1f without, %.0f ms the first time over fresh data",
                            100.0 * drawn[0] / Math.max(1, dry), 100.0 * drawn[1] / Math.max(1, dry), 100.0 * drawn[2] / Math.max(1, dry), 100.0 * drawn[3] / Math.max(1, dry),
                            100 * want[0] / Math.max(1, dry), 100 * want[1] / Math.max(1, dry), 100 * want[2] / Math.max(1, dry), 100 * want[3] / Math.max(1, dry), dry, points,
                            withMs, plainMs, cold));
        }
        // The globe's camera at the handoff, against the plane's node: the same shares; its piece in no more than FRAME_MS.
        GlobeFrame f = new GlobeFrame(n, n, HILLS[0][0], HILLS[0][1]);
        f.set(HILLS[0][0], HILLS[0][1], GlobeFrame.HANDOFF_KM);
        byte[] gc = new byte[n * n], gr = new byte[n * n], pc = new byte[n * n], pr = new byte[n * n];
        long x0 = c - (long) n * GlobeFrame.HANDOFF_PLOTS / 2;
        double gMs = Double.MAX_VALUE;
        for (int round = 0; round < 3; round++) {
            long t0 = System.nanoTime();
            e.farTerrain(e.globe(f.ortho(), f.kmPx(), f.width(), f.height(), 0, 0, n, n), gc, gr);
            gMs = Math.min(gMs, (System.nanoTime() - t0) / 1e6);
        }
        e.farTerrain(x0, x0, GlobeFrame.HANDOFF_PLOTS, n, pc, pr);
        e.farTerrain(x0, x0, GlobeFrame.HANDOFF_PLOTS, n, pc, pr);
        long gDry = 0, pDry = 0;
        double[] g = new double[World.RELIEFS], p = new double[World.RELIEFS];
        boolean flat = true;
        for (int i = 0; i < gr.length; i++) {
            if (World.dry(gc[i])) { gDry++; g[gr[i]]++; } else flat &= gr[i] == World.FLAT;
            if (World.dry(pc[i])) { pDry++; p[pr[i]]++; }
        }
        double apart = 0;
        for (int k = 0; k < World.RELIEFS; k++) apart = Math.max(apart, 100 * Math.abs(g[k] / Math.max(1, gDry) - p[k] / Math.max(1, pDry)));
        report("the globe's camera at the handoff draws the plane's relief shares: its dry pixels within RELIEF_POINTS x 2 of the plane's node over the same ground, none on water, and a piece is drawn in no more than FRAME_MS",
                gDry > 0 && pDry > 0 && flat && apart <= 2 * RELIEF_POINTS * 2 && Timing.within(gMs, GlobeCheck.FRAME_MS),
                String.format("%.1f/%.1f/%.1f/%.1f%% on the globe's %,d dry pixels against %.1f/%.1f/%.1f/%.1f%% on the plane's %,d (%.1f points); %.1f ms", 100 * g[0] / Math.max(1, gDry), 100 * g[1] / Math.max(1, gDry),
                        100 * g[2] / Math.max(1, gDry), 100 * g[3] / Math.max(1, gDry), gDry, 100 * p[0] / Math.max(1, pDry), 100 * p[1] / Math.max(1, pDry), 100 * p[2] / Math.max(1, pDry),
                        100 * p[3] / Math.max(1, pDry), pDry, apart, gMs));
        // The picture's camera: a band of the planet at 4', cover as without, and the cost of a band's relief over fresh data (the picture is made once and never saved).
        EarthData again = EarthData.open(EarthData.locate());
        String againId = EarthTerrain.use(again);
        EarthTerrain pe = new EarthTerrain(World.Key.earth(0, 0, 0, againId));
        int perDeg = 60 / GlobePicture.ARCMIN, bandRows = GlobePicture.BAND_DEG * perDeg, cols = 360 * perDeg;
        byte[] bandPlain = new byte[cols * bandRows], bandWith = new byte[bandPlain.length], bandRelief = new byte[bandPlain.length];
        int row0 = (90 - 50) / GlobePicture.BAND_DEG * bandRows;   // the band 50 N to 40 N: the Alps, the Pyrenees, the Pamirs' foot
        long t0 = System.nanoTime();
        pe.farTerrain(pe.grid(GlobePicture.ARCMIN, row0, bandRows), bandPlain);
        double plainBand = (System.nanoTime() - t0) / 1e6;
        t0 = System.nanoTime();
        pe.farTerrain(pe.grid(GlobePicture.ARCMIN, row0, bandRows), bandWith, bandRelief);
        double withBand = (System.nanoTime() - t0) / 1e6;
        long bandDry = 0, bandSloped = 0;
        boolean bandFlat = true;
        for (int i = 0; i < bandRelief.length; i++) {
            if (World.dry(bandWith[i])) { bandDry++; if (bandRelief[i] != World.FLAT) bandSloped++; } else bandFlat &= bandRelief[i] == World.FLAT;
        }
        long bandTiles = 0, planetTiles = 0;
        for (int t = 0; t < EarthData.TILES; t++) {
            if (file.entry(EarthData.SLOPE, t) <= 0) continue;
            planetTiles++;
            if (EarthData.tileLat0(t) >= 40 && EarthData.tileLat0(t) < 50) bandTiles++;
        }
        double perTile = (withBand - plainBand) / Math.max(1, bandTiles);
        report("the picture's camera (a band of 10 degrees at 4'): the cover is the same byte for byte, the relief is none on water, and the band's relief costs a first sight of its slope tiles only",
                Arrays.equals(bandPlain, bandWith) && bandFlat && bandSloped > 0 && bandTiles > 0,
                String.format("%,d of %,d dry texels with relief; %.0f ms for the band's cover, the relief adding %.0f over fresh data for its %,d tiles with slope (%.2f ms each: the planet's %,d about %.1f s of one thread, once)",
                        bandSloped, bandDry, plainBand, withBand - plainBand, bandTiles, perTile, planetTiles, perTile * planetTiles / 1000));
    }

    /** Four coasts of the full file, {lat, lon}, each a 61 km window with sea of both depths: the North Sea at the Flemish banks, the shelf off Perth, the Texas shelf off Galveston and the Gulf off Ras Tanura. */
    static final double[][] SHELVES = { { 52.0, 3.0 }, { -32.0, 115.4 }, { 28.5, -94.0 }, { 27.0, 50.5 } };

    /** A colour's luma, the plain sum of its channels. */
    static int luma(int c) { return ((c >> 16) & 255) + ((c >> 8) & 255) + (c & 255); }

    /** A count of n plots of one class, World.CLASSES long. */
    static long[] only(byte cls, long n) {
        long[] c = new long[World.CLASSES];
        c[cls] = n;
        return c;
    }

    /** A card's plots add up: dry, fresh, sea and ice. */
    static boolean adds(EarthSite.Card c) {
        return c.dry() + c.fresh() + c.sea() + c.icePlots() == c.plots() && c.plots() > 0;
    }

    /** How many of a plan's buildings stand west of its district's middle. */
    static int westOf(DistrictPlan p) {
        int k = 0;
        for (int b = 0; b < p.buildings; b++) if (p.bx[b] < DistrictPlan.FRAME / 2) k++;
        return k;
    }

    /** The nearest whole tile to a city's founding site, ring by ring of tiles out to 48 (46 km), that the city owns no plot of and whose every plot is of one class: its north-west plot, or null. */
    static long[] tileOf(World w, CityLand land, byte cls) {
        long tx0 = Math.floorDiv(land.siteX(), World.TILE), ty0 = Math.floorDiv(land.siteY(), World.TILE);
        byte[] t = new byte[World.TILE * World.TILE];
        for (int r = 1; r <= 48; r++) {
            for (long ty = ty0 - r; ty <= ty0 + r; ty++) {
                for (long tx = tx0 - r; tx <= tx0 + r; tx++) {
                    if (Math.max(Math.abs(tx - tx0), Math.abs(ty - ty0)) != r) continue;
                    w.tileTerrain(tx, ty, t);
                    boolean all = true;
                    for (int i = 0; i < t.length && all; i++) all = t[i] == cls && !land.ownsPlot(tx * World.TILE + i % World.TILE, ty * World.TILE + i / World.TILE);
                    if (all) return new long[] { tx * World.TILE, ty * World.TILE };
                }
            }
        }
        return null;
    }

    /** Whether the far nodes about a world's spot draw a class: every pixel of it at WATER_STRIDE and FAR_STRIDE / 4 (61 and 246 km a node), and at FAR_STRIDE (983 km, which reaches past the sheet's coast) more of it than of any other; each node's share of it in words. */
    static boolean far(World.Key key, byte cls, String[] words) {
        boolean ok = true;
        StringBuilder w = new StringBuilder();
        for (long plots : new long[] { EarthTerrain.WATER_STRIDE, EarthTerrain.FAR_STRIDE / 4, EarthTerrain.FAR_STRIDE }) {
            long half = plots * MapFrame.NODE_PX / 2;
            byte[] node = MapTiles.nodeTerrain(key, EarthTerrain.spotPlot() - half, EarthTerrain.spotPlot() - half, plots);
            long[] c = new long[World.CLASSES];
            for (byte b : node) c[b]++;
            boolean most = true;
            for (int k = 0; k < World.CLASSES; k++) if (k != cls && c[k] >= c[cls]) most = false;
            ok &= plots < EarthTerrain.FAR_STRIDE ? c[cls] == node.length : most;
            w.append(String.format("%s%.1f%% at %d", w.length() == 0 ? "" : ", ", 100.0 * c[cls] / node.length, plots));
        }
        words[0] = w.toString();
        return ok;
    }

    /**
     * One kind's 1-degree cell on the full file, on a world centred there:
     * every plot whose centre lies in the cell drawn, and among those the
     * kind could take (ice any ground not water; the desert grass or desert,
     * which forest and the beach come before), its share drawn against the
     * share whose 2' cell carries its bit; then its edge - the plots where
     * the bit's share interpolated between the cells' centres lies strictly
     * between 0 and 1, where the noise may fray it - drawn against the
     * share's half-contour unfrayed: frayed both ways, and never past it.
     */
    static void cover(EarthData d, boolean ice, int lat0, int lon0) {
        long t0 = System.nanoTime();
        World w = World.of(World.Key.earth(SEED, lat0 + 0.5, lon0 + 0.5, EarthData.EARTH_2));
        EarthProj proj = w.earthTerrain().projection();
        double[] p = new double[2];
        long x0 = Long.MAX_VALUE, x1 = Long.MIN_VALUE, y0 = Long.MAX_VALUE, y1 = Long.MIN_VALUE;
        for (int k = 0; k <= 16; k++) {
            for (int e = 0; e < 4; e++) {
                double la = e < 2 ? lat0 + k / 16.0 : lat0 + (e - 2), lo = e < 2 ? lon0 + e : lon0 + k / 16.0;
                proj.plotOf(la, lo, p);
                x0 = Math.min(x0, (long) Math.floor(p[0])); x1 = Math.max(x1, (long) Math.ceil(p[0]));
                y0 = Math.min(y0, (long) Math.floor(p[1])); y1 = Math.max(y1, (long) Math.ceil(p[1]));
            }
        }
        int bit = ice ? EarthData.LC_ICE : EarthData.LC_DESERT;
        byte kind = ice ? World.ICE : World.DESERT;
        Cover c = new Cover(d);
        long ground = 0, drawn = 0, data = 0, band = 0, inward = 0, outward = 0, flipped = 0;
        double[] dist = new double[1 << 16];
        int nd = 0;
        byte[] t = new byte[World.TILE * World.TILE];
        double[] ll = new double[2], q = new double[2];
        for (long ty = Math.floorDiv(y0, World.TILE); ty <= Math.floorDiv(y1, World.TILE); ty++) {
            for (long tx = Math.floorDiv(x0, World.TILE); tx <= Math.floorDiv(x1, World.TILE); tx++) {
                w.tileTerrain(tx, ty, t);
                EarthProj.Blend b = proj.blend(tx * World.TILE, ty * World.TILE, World.TILE);
                for (int i = 0; i < t.length; i++) {
                    double px = tx * World.TILE + i % World.TILE + 0.5, py = ty * World.TILE + i / World.TILE + 0.5;
                    if (!b.latLon(px, py, ll) || ll[0] < lat0 || ll[0] >= lat0 + 1 || ll[1] < lon0 || ll[1] >= lon0 + 1) continue;
                    byte g = t[i];
                    if (ice ? !(!World.salt(g) && g != World.FRESH) : !(g == World.GRASS || g == World.DESERT)) continue;
                    ground++;
                    boolean is = g == kind;
                    if (is) drawn++;
                    if ((c.at(ll[0], ll[1]) & bit) != 0) data++;
                    double s = c.share(ll[0], ll[1], bit);
                    if (s <= 0 || s >= 1) {
                        if (is == (s <= 0)) flipped++;
                        continue;
                    }
                    band++;
                    if ((s >= EarthTerrain.COVER_LEVEL) == is) continue;
                    if (is) outward++; else inward++;
                    b.latLon(px + 1, py, q); double sx1 = c.share(q[0], q[1], bit);
                    b.latLon(px - 1, py, q); double sx0 = c.share(q[0], q[1], bit);
                    b.latLon(px, py + 1, q); double sy1 = c.share(q[0], q[1], bit);
                    b.latLon(px, py - 1, q); double sy0 = c.share(q[0], q[1], bit);
                    double gr = Math.hypot((sx1 - sx0) / 2, (sy1 - sy0) / 2);
                    if (gr > 0) {
                        if (nd == dist.length) dist = Arrays.copyOf(dist, 2 * nd);
                        dist[nd++] = Math.abs(s - EarthTerrain.COVER_LEVEL) / gr;
                    }
                }
            }
        }
        Arrays.sort(dist, 0, nd);
        double mean = 0;
        for (int k = 0; k < nd; k++) mean += dist[k];
        mean /= Math.max(1, nd);
        String what = (ice ? "ice" : "the desert") + String.format(" at %d,%d", lat0, lon0);
        double points = 100.0 * Math.abs(drawn - data) / Math.max(1, ground);
        report(what + ": its share of the 1-degree cell's ground drawn is the data's within COVER_POINTS", ground > 0 && points <= COVER_POINTS && data > 0 && data < ground,
                String.format("%.2f%% drawn against %.2f%% (%.2f points), of %,d plots%s", 100.0 * drawn / ground, 100.0 * data / ground, points, ground, ice ? "" : " of grass or desert"));
        report("...its edge frayed both ways about the bit's half-contour, and never past the cells that agree", inward > 0 && outward > 0 && flipped == 0,
                String.format("%,d plots within a cell of the edge (%.1f%%), %,d drawn its way past the contour and %,d short of it (%.1f%%); a frayed plot %.1f plots from it in the mean,"
                                + " %.1f the median, %.1f at 95%%, %.1f at most; %,d past the cells; %.1f s", band, 100.0 * band / ground, outward, inward, 100.0 * (inward + outward) / Math.max(1, band),
                        mean, nd > 0 ? dist[nd / 2] : 0, nd > 0 ? dist[(int) (nd * 0.95)] : 0, nd > 0 ? dist[nd - 1] : 0, flipped, (System.nanoTime() - t0) / 1e9));
    }

    /** Land cover's 2' cells read from the file's grids, kept by tile: a cell's value, and a bit's share between the cells' centres as EarthTerrain reads it. */
    static final class Cover {
        final EarthData d;
        final int cpd;
        final Map<Integer, byte[]> grids = new java.util.HashMap<>();

        Cover(EarthData d) {
            this.d = d;
            this.cpd = d.layer("landcover").cellsPerDegree;
        }

        /** The value of the cell in global row r (from 90 N) and column c (from 180 W). */
        int cell(long r, long c) {
            r = Math.max(0, Math.min(180L * cpd - 1, r));
            c = Math.floorMod(c, 360L * cpd);
            int tile = (int) ((179 - r / cpd) * 360 + c / cpd);
            byte[] g = grids.computeIfAbsent(tile, k -> d.grid("landcover", k));
            return g[(int) ((r % cpd) * cpd + c % cpd)] & 0xFF;
        }

        /** The value of the cell a point lies in. */
        int at(double lat, double lon) {
            return cell((long) Math.floor((90 - lat) * cpd), (long) Math.floor((lon + 180) * cpd));
        }

        /** A bit read 0 or 1 at each cell and interpolated between the four cells' centres about a point. */
        double share(double lat, double lon, int bit) {
            double g = (90 - lat) * cpd - 0.5, c = (lon + 180) * cpd - 0.5;
            long r0 = (long) Math.floor(g), c0 = (long) Math.floor(c);
            double fr = g - r0, fc = c - c0;
            double a = (cell(r0, c0) & bit) != 0 ? 1 : 0, b = (cell(r0, c0 + 1) & bit) != 0 ? 1 : 0;
            double e = (cell(r0 + 1, c0) & bit) != 0 ? 1 : 0, f = (cell(r0 + 1, c0 + 1) & bit) != 0 ? 1 : 0;
            return (a * (1 - fc) + b * fc) * (1 - fr) + (e * (1 - fc) + f * fc) * fr;
        }
    }
}
