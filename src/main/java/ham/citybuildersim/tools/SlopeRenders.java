package ham.citybuildersim.tools;

import ham.citybuildersim.BuildingVisual;
import ham.citybuildersim.CityLand;
import ham.citybuildersim.CityMap;
import ham.citybuildersim.EarthData;
import ham.citybuildersim.EarthSite;
import ham.citybuildersim.Founding;
import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.TilePainter;
import ham.citybuildersim.TileRaster;
import ham.citybuildersim.World;
import ham.citybuildersim.ui.FoundingScreen;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Renders what ET2 did to the map and the founding card, for review: a city played on the real Earth and drawn by the
 * game's own painters, headless, and the card's words for three spots.
 *
 *     java -cp "target/classes;<gson.jar>;<javafx jars>" ham.citybuildersim.tools.SlopeRenders [earth-2.bin [outDir [name...]]]
 *
 * The file is the argument, else -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); outDir is docs/queue/ET2
 * unless named; names (render-a, render-b, render-c) pick which to write, all three otherwise. -Dslope.months=N plays
 * the city N months (2,000 unless named: SlopeCheck 9's run, the length at which its land office's offers have
 * brought it steep ground). The city is founded at Medellin (LongPlaytest.earthCity(): the playtest's own play, in a
 * GameFiles.scratch() folder that is deleted when the JVM exits - never the real saves) and its map read tile by tile
 * through CityMap.tileInput(), TilePainter.paint() and TileRaster.raster(), as the screen reads it. It writes, each
 * under about a megabyte:
 *
 *   et2-a-medellin-city.png    the city at plot zoom, 4 px a plot, over the window of 8 x 6 tiles (1,024 x 768 px)
 *                              with the most buildings among those that hold steep ground and buildings on foothill:
 *                              streets and buildings on the flat and the foothill, none on the steep greys
 *   et2-b-medellin-works.png   the window round the most works on steep ground (a mine or a quarry standing where no
 *                              building may) - not written when the run has none
 *   et2-c-refusal.txt          the founding card's words (FoundingScreen's, in its order) for Medellin's site (the spot itself and one the snap moves),
 *                              for a spot in the Andes at -13.2, -72.5, for Everest's slopes and for its summit
 *
 * The render-c card needs JavaFX on the classpath (FoundingScreen is the screen's class and reads its colours); the
 * other two do not.
 *
 * WHY (ET2 slice E): the map is checked by eye. SlopeCheck holds the counts - no building on steep ground, a works
 * on it, the preparation charged - and these show the game drawing a city where the ground tilts. Not a harness:
 * nothing here asserts, and nothing under docs/ but its outDir is written.
 */
public final class SlopeRenders {

    private SlopeRenders() { }

    /** Pixels a plot is drawn at, and the window in tiles: 4 x 32 x 8 by 4 x 32 x 6, 1,024 x 768 - inside the review's 800 x 600 to 1,200 x 900. */
    private static final int PX = 4, ACROSS = 8, DOWN = 6;

    /** Where the founding cards are measured: Medellin's spot (SlopeCheck's), a spot in the hills south of it that the snap moves 1.4 km, a spot in the Andes slice C probed, Everest's slopes (SlopeCheck's) and its summit (EarthTerrainCheck's). */
    private static final double[][] SPOTS = { { 6.25, -75.57 }, { 6.12, -75.57 }, { -13.2, -72.5 }, { 27.60, 86.70 }, { 27.9881, 86.9250 } };
    private static final String[] SPOT_NAMES = { "Medellin", "the hills south of Medellin, a spot the snap moves", "the Andes at -13.2, -72.5", "Everest's southern slopes", "Everest's summit" };

    public static void main(String[] args) throws IOException {
        Path file = args.length > 0 ? Path.of(args[0]) : EarthData.locate();
        if (file == null || !Files.isRegularFile(file)) {
            System.out.println("no earth-2 file: name it, or -Dcbs.earth=<path>, or put it at " + EarthData.DATA_FILE);
            return;
        }
        System.setProperty(EarthData.PROPERTY, file.toString());
        Path outDir = args.length > 1 ? Path.of(args[1]) : Path.of("docs", "queue", "ET2");
        java.util.Set<String> only = new java.util.HashSet<>();
        for (int k = 2; k < args.length; k++) only.add(args[k]);
        Files.createDirectories(outDir);
        boolean a = only.isEmpty() || only.contains("render-a"), b = only.isEmpty() || only.contains("render-b"),
                c = only.isEmpty() || only.contains("render-c");
        if (c) writeCards(outDir.resolve("et2-c-refusal.txt"));
        if (a || b) writeCity(outDir, a, b);
        System.exit(0);
    }

    /* ----- the city ----- */

    /** What one tile of the map holds, counted for choosing a window: all in plots or buildings. */
    private static final class Count {
        int buildings, steepDry, steepOwned, foothillBuilt, onSteep, plain;
        void add(Count o) { buildings += o.buildings; steepDry += o.steepDry; steepOwned += o.steepOwned; foothillBuilt += o.foothillBuilt; onSteep += o.onSteep; plain += o.plain; }
    }

    private static void writeCity(Path outDir, boolean a, boolean b) throws IOException {
        int months = Integer.getInteger("slope.months", 2000);
        PrintStream silent = new PrintStream(new OutputStream() { @Override public void write(int x) { } });
        long t0 = System.currentTimeMillis();
        Game g = LongPlaytest.earthCity("medellin", months, GameFiles.scratch("sloperenders-medellin"), silent);
        LandManager lm = g.getLandManager();
        System.out.printf("a Medellin city played %d months in %d s: %.3f km2 owned, flat %.3f, foothill %.3f, steep %.3f; %.3f km2 built on, %.3f of it by works, %.3f of that on steep ground; %.4f km2 of foothill prepared%n",
                g.getMonth(), (System.currentTimeMillis() - t0) / 1000, LandManager.km2(lm.getOwnedSqFt()), LandManager.km2(lm.getFlatSqFt()),
                LandManager.km2(lm.getFoothillSqFt()), LandManager.km2(lm.getSlopeSqFt()), LandManager.km2(lm.getAllocatedSqFt()),
                LandManager.km2(lm.getWorksSqFt()), LandManager.km2(lm.getWorksOnSlopeSqFt()), LandManager.km2(lm.getPreparedSqFt()));
        CityMap map = g.getCityMap();
        CityLand land = g.getCityLand();
        BuildingVisual.Type[] types = map.types();
        long baseX = Math.floorDiv(land.siteX(), (long) CityMap.DISTRICT), baseY = Math.floorDiv(land.siteY(), (long) CityMap.DISTRICT);

        // Every tile of every district the map holds: counted once, and what stands on steep ground and on foothill tallied by sector.
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        Map<Long, Count> counts = new HashMap<>();
        Map<String, int[]> onSteepBy = new TreeMap<>(), onFootBy = new TreeMap<>();
        long streetPlots = 0, streetPlotsOnSteep = 0, buildingPlots = 0, buildingPlotsOnSteep = 0;
        for (CityMap.District d : map.districts()) {
            for (int k = 0; k < CityMap.TILES_A_SIDE * CityMap.TILES_A_SIDE; k++) {
                long tx = (baseX + d.dx) * CityMap.TILES_A_SIDE + k % CityMap.TILES_A_SIDE, ty = (baseY + d.dy) * CityMap.TILES_A_SIDE + k / CityMap.TILES_A_SIDE;
                map.tileInput(tx, ty, in);
                TilePainter.paint(in, p);
                Count n = new Count();
                for (int i = 0; i < TilePainter.PLOTS; i++) {
                    boolean steep = World.dry(in.terrain[i]) && in.relief[i] >= World.STEEP;
                    byte use = p.use[i];
                    if (use == TilePainter.ROAD || use == TilePainter.RAIL) { streetPlots++; if (steep) streetPlotsOnSteep++; }
                    else if (use == TilePainter.BUILDING) { buildingPlots++; if (steep) buildingPlotsOnSteep++; }
                    if (!steep) continue;
                    n.steepDry++;
                    if (in.owned[i]) n.steepOwned++;
                }
                for (int i = 0; i < p.buildings; i++) {
                    int steep = 0, foot = 0;
                    for (int y = p.by[i]; y < p.by[i] + p.bh[i]; y++) {
                        for (int x = p.bx[i]; x < p.bx[i] + p.bw[i]; x++) {
                            int q = y * World.TILE + x;
                            if (q < 0 || q >= TilePainter.PLOTS) continue;
                            if (in.relief[q] >= World.STEEP) steep++; else if (in.relief[q] == World.FOOTHILL) foot++;
                        }
                    }
                    n.buildings++;
                    String sector = p.btype[i] >= 0 && p.btype[i] < types.length && types[p.btype[i]] != null ? String.valueOf(types[p.btype[i]].sector()) : "?";
                    if (steep > 0) { n.onSteep++; onSteepBy.computeIfAbsent(sector, s -> new int[1])[0]++; } else n.plain++;
                    if (foot > 0) { n.foothillBuilt++; onFootBy.computeIfAbsent(sector, s -> new int[1])[0]++; }
                }
                if (n.buildings > 0 || n.steepOwned > 0) counts.put(key(tx, ty), n);
            }
        }
        System.out.printf("the whole map as painted: %,d street, highway and rail plots, %,d of them on steep ground; %,d building plots, %,d of them on steep ground%n",
                streetPlots, streetPlotsOnSteep, buildingPlots, buildingPlotsOnSteep);
        System.out.println("buildings standing on steep ground, by sector: " + tally(onSteepBy) + "; on foothill: " + tally(onFootBy));

        Count best = null, bestWorks = null;
        long[] at = null, atWorks = null;
        long minX = Long.MAX_VALUE, maxX = Long.MIN_VALUE, minY = Long.MAX_VALUE, maxY = Long.MIN_VALUE;
        for (long k : counts.keySet()) {
            long kx = (int) (k >> 32), ky = (int) k;
            minX = Math.min(minX, kx); maxX = Math.max(maxX, kx); minY = Math.min(minY, ky); maxY = Math.max(maxY, ky);
        }
        for (long wx = minX - ACROSS + 1; wx <= maxX; wx++) {
            for (long wy = minY - DOWN + 1; wy <= maxY; wy++) {
                Count w = new Count();
                for (int j = 0; j < DOWN; j++) for (int i = 0; i < ACROSS; i++) { Count c = counts.get(key(wx + i, wy + j)); if (c != null) w.add(c); }
                // The picture a: the most buildings where steep ground is on show and buildings stand on foothill; the next best of these where none does.
                if (best == null || scoreOf(w) > scoreOf(best)) { best = w; at = new long[] { wx, wy }; }
                if (w.onSteep > 0 && (bestWorks == null || w.onSteep * 100_000L + w.buildings > bestWorks.onSteep * 100_000L + bestWorks.buildings)) { bestWorks = w; atWorks = new long[] { wx, wy }; }
            }
        }
        if (a) {
            if (best == null) System.out.println("render-a: the map holds no buildings");
            else {
                System.out.printf("render-a: window at tiles (%d, %d): %d buildings, %d of them on foothill, %,d steep plots on show (%.1f%% of the window), %,d of them owned, %d buildings on steep ground%n",
                        at[0], at[1], best.buildings, best.foothillBuilt, best.steepDry, 100.0 * best.steepDry / (ACROSS * DOWN * TilePainter.PLOTS), best.steepOwned, best.onSteep);
                write(outDir, "et2-a-medellin-city.png", window(map, at[0], at[1]));
            }
        }
        if (b) {
            if (bestWorks == null) System.out.println("render-b: no building of the run stands on steep ground, so no works picture is written");
            else {
                System.out.printf("render-b: window at tiles (%d, %d): %d buildings, %d on steep ground (works), %d on foothill%n",
                        atWorks[0], atWorks[1], bestWorks.buildings, bestWorks.onSteep, bestWorks.foothillBuilt);
                write(outDir, "et2-b-medellin-works.png", window(map, atWorks[0], atWorks[1]));
            }
        }
    }

    /**
     * A window's worth as picture a: a city to look at with ground of its own that nothing stands on in it - its
     * buildings (to a ceiling: a window all city shows no steep ground) and, a plot for a building and a half, the
     * steep ground it owns - where buildings stand on foothill; else its buildings.
     */
    private static long scoreOf(Count w) {
        return w.foothillBuilt > 0 ? Math.min(w.buildings, CEILING) + 3L * w.steepOwned / 2 : w.buildings;
    }

    /** The buildings after which a window is not the better for more. */
    private static final int CEILING = 2_500;
    private static String tally(Map<String, int[]> by) {
        if (by.isEmpty()) return "none";
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, int[]> e : by.entrySet()) parts.add(e.getKey() + " " + e.getValue()[0]);
        return String.join(", ", parts);
    }

    private static long key(long tx, long ty) { return (tx << 32) | (ty & 0xffffffffL); }

    /** A window of ACROSS x DOWN tiles from tile (tx0, ty0), as the screen draws it: CityMap.tileInput() with the city's own ownership, streets and buildings, painted and rastered at PX a plot. */
    private static BufferedImage window(CityMap map, long tx0, long ty0) {
        int side = World.TILE * PX;
        BufferedImage img = new BufferedImage(ACROSS * side, DOWN * side, BufferedImage.TYPE_INT_RGB);
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int[] tile = new int[side * side];
        for (int j = 0; j < DOWN; j++) {
            for (int i = 0; i < ACROSS; i++) {
                map.tileInput(tx0 + i, ty0 + j, in);
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, PX, tile);
                for (int y = 0; y < side; y++) for (int x = 0; x < side; x++) img.setRGB(i * side + x, j * side + y, tile[y * side + x] & 0xffffff);
            }
        }
        return img;
    }

    private static void write(Path dir, String name, BufferedImage img) throws IOException {
        Path to = dir.resolve(name);
        javax.imageio.ImageIO.write(img, "png", to.toFile());
        System.out.printf("%s  %d x %d  %,d bytes%n", to, img.getWidth(), img.getHeight(), Files.size(to));
    }

    /* ----- the founding card ----- */

    /** The card's words for each spot, as FoundingScreen.fillCard() lays them: the refusal where there is no site or the site is refused, else the site's lines in order. */
    private static void writeCards(Path to) throws IOException {
        List<String> out = new ArrayList<>();
        out.add("ET2 slice E: the founding card's words (FoundingScreen), measured on earth-2.bin by EarthSite.measure(), at each spot.");
        out.add("A refusal is the card's last line in the bad colour; a site's lines are those the screen draws in this order.");
        for (int i = 0; i < SPOTS.length; i++) {
            double lat = SPOTS[i][0], lon = SPOTS[i][1];
            out.add("");
            out.add("== " + SPOT_NAMES[i] + " (" + EarthSite.latLonWords(lat, lon) + ")");
            World.Key key = World.Key.earth(Founding.DEFAULT_WORLD_SEED, lat, lon, EarthData.EARTH_2);
            EarthSite.Card c = EarthSite.measure(key);
            String where = SPOT_NAMES[i] + " · " + EarthSite.latLonWords(lat, lon);
            try {
                if (!c.hasSite()) {
                    out.add(where);
                    out.add(c.refusal());
                } else {
                    out.add(FoundingScreen.siteWords(c));
                    if (c.refusal() != null) out.add(c.refusal());
                    else {
                        out.add(FoundingScreen.groundWords(c));
                        out.add(FoundingScreen.waterWords(c));
                        out.add(FoundingScreen.geologyWords(c));
                        out.add(FoundingScreen.ironWords(c));
                        out.add(EarthSite.reliefWords(c));
                        out.add(EarthSite.shallowWords(c));
                        String landmass = EarthSite.landmassWords(c);
                        if (landmass != null) out.add(landmass);
                        for (String w : c.warnings()) out.add(w);
                        out.add(FoundingScreen.seedWords(Founding.DEFAULT_WORLD_SEED));
                    }
                }
            } catch (LinkageError e) {
                out.add("(FoundingScreen could not be loaded - JavaFX is not on the classpath: " + e + ")");
            }
            if (c.hasSite()) out.add(String.format(Locale.ROOT, "[site plot (%d, %d), %.0f m from the spot; foundable %s]", c.siteX(), c.siteY(), c.snappedM(), c.foundable()));
        }
        Files.write(to, out, java.nio.charset.StandardCharsets.UTF_8);
        System.out.printf("%s  %,d bytes%n", to, Files.size(to));
        for (String l : out) System.out.println("   " + l);
    }
}
