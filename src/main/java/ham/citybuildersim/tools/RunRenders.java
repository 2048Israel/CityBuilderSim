package ham.citybuildersim.tools;

import ham.citybuildersim.CityLand;
import ham.citybuildersim.CityMap;
import ham.citybuildersim.EarthData;
import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.RunWorksCheck;
import ham.citybuildersim.TilePainter;
import ham.citybuildersim.TileRaster;
import ham.citybuildersim.World;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders what ET4 slice C did to the map, for review: where a city's highways and railway run across steep ground, a
 * viaduct, and across very steep ground, a tunnel, drawn by the game's own painters, headless.
 *
 *     java -cp "target/classes;<gson.jar>" ham.citybuildersim.tools.RunRenders [earth-2.bin [outDir]]
 *
 * The file is the argument, else -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); outDir is docs/queue/ET4
 * unless named. -Drun.months=N plays the city N months (2,000 unless named: SlopeRenders' length). The city is founded
 * at Medellin (LongPlaytest.earthCity(), in a GameFiles.scratch() folder deleted when the JVM exits - never the real
 * saves) and its map read tile by tile through CityMap.tileInput(), TilePainter.paint() and TileRaster.raster() at 8 px
 * a plot, as the screen reads it. It writes:
 *
 *   et4-a-medellin-viaduct.png   the window of 4 x 3 tiles (1,024 x 768 px) with the most plots of the runs on steep
 *                                ground, a bridge over dry land (TilePainter.Painted.bridge off the water); when the run
 *                                has none, et4-a-viaduct.txt says so
 *   et4-b-medellin-tunnel.png    the window with the most plots of the runs on very steep ground (Painted.tunnel), and
 *                                RunWorksCheck's fixture tile as et4-c-fixture.png (256 x 256 px: an east-west highway and a
 *                                north-south track each across flat, steep, very steep and flat ground); when the run has
 *                                no tunnel, et4-b-tunnel.txt says so and the fixture tile is written as
 *                                et4-b-fixture-tunnel.png instead, the picture of a tunnel
 *   README.md                    one line a picture
 *
 * WHY (ET4 slice C): the map is checked by eye. RunWorksCheck holds the flags and the pixels; these show the game
 * drawing a city's runs where the ground tilts. Not a harness: nothing here asserts, and nothing under docs/ but its
 * outDir is written.
 */
public final class RunRenders {

    private RunRenders() { }

    /** Pixels a plot, and the window in tiles: 8 x 32 x 4 by 8 x 32 x 3, 1,024 x 768. */
    private static final int PX = 8, ACROSS = 4, DOWN = 3;

    /** What one tile of the map holds of the runs: their plots, and those on steep and on very steep ground. */
    private static final class Count {
        int runs, viaducts, tunnels;
        void add(Count o) { runs += o.runs; viaducts += o.viaducts; tunnels += o.tunnels; }
    }

    public static void main(String[] args) throws IOException {
        Path file = args.length > 0 ? Path.of(args[0]) : EarthData.locate();
        if (file == null || !Files.isRegularFile(file)) {
            System.out.println("no earth-2 file: name it, or -Dcbs.earth=<path>, or put it at " + EarthData.DATA_FILE);
            return;
        }
        System.setProperty(EarthData.PROPERTY, file.toString());
        Path outDir = args.length > 1 ? Path.of(args[1]) : Path.of("docs", "queue", "ET4");
        Files.createDirectories(outDir);
        List<String> readme = new ArrayList<>();
        readme.add("# ET4 slice C renders");
        readme.add("");
        readme.add("Written by `ham.citybuildersim.tools.RunRenders` (a Medellin city on the real Earth, earth-2, played "
                + Integer.getInteger("run.months", 2000) + " months, drawn at 8 px a plot by the game's own painters). Not a harness.");
        readme.add("");

        // The fixture tile first: the harness's own, always.
        TilePainter.Input fin = RunWorksCheck.mapTile();
        TilePainter.Painted fp = new TilePainter.Painted();
        TilePainter.paint(fin, fp);
        int[] ftile = new int[World.TILE * PX * World.TILE * PX];
        TileRaster.raster(fin, fp, PX, ftile);
        BufferedImage fixture = new BufferedImage(World.TILE * PX, World.TILE * PX, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < World.TILE * PX; y++) for (int x = 0; x < World.TILE * PX; x++) fixture.setRGB(x, y, ftile[y * World.TILE * PX + x] & 0xffffff);
        // (written below: as the tunnel picture where the city has none, else as a picture of its own)

        PrintStream silent = new PrintStream(new OutputStream() { @Override public void write(int x) { } });
        int months = Integer.getInteger("run.months", 2000);
        long t0 = System.currentTimeMillis();
        Game g = LongPlaytest.earthCity("medellin", months, GameFiles.scratch("runrenders-medellin"), silent);
        System.out.printf("a Medellin city played %d months in %d s%n", g.getMonth(), (System.currentTimeMillis() - t0) / 1000);
        CityMap map = g.getCityMap();
        CityLand land = g.getCityLand();
        long baseX = Math.floorDiv(land.siteX(), (long) CityMap.DISTRICT), baseY = Math.floorDiv(land.siteY(), (long) CityMap.DISTRICT);

        // Every tile of every district the map holds: the runs' plots counted, by the ground they lie on.
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        Map<Long, Count> counts = new HashMap<>();
        long runPlots = 0, onFoothill = 0, onSteep = 0, onVery = 0, railOnSteep = 0, railOnVery = 0;
        for (CityMap.District d : map.districts()) {
            for (int k = 0; k < CityMap.TILES_A_SIDE * CityMap.TILES_A_SIDE; k++) {
                long tx = (baseX + d.dx) * CityMap.TILES_A_SIDE + k % CityMap.TILES_A_SIDE, ty = (baseY + d.dy) * CityMap.TILES_A_SIDE + k / CityMap.TILES_A_SIDE;
                map.tileInput(tx, ty, in);
                TilePainter.paint(in, p);
                Count n = new Count();
                for (int i = 0; i < TilePainter.PLOTS; i++) {
                    if (p.run[i] == 0) continue;
                    n.runs++;
                    runPlots++;
                    if (in.relief[i] == World.FOOTHILL) onFoothill++;
                    boolean viaduct = p.bridge[i] && in.terrain[i] != World.FRESH;
                    if (viaduct) { n.viaducts++; onSteep++; if (p.run[i] == TilePainter.RAIL) railOnSteep++; }
                    if (p.tunnel[i]) { n.tunnels++; onVery++; if (p.run[i] == TilePainter.RAIL) railOnVery++; }
                }
                if (n.runs > 0) counts.put(key(tx, ty), n);
            }
        }
        System.out.printf("the runs as painted: %,d plots, %,d on foothill, %,d on steep ground (viaducts; %,d of them railway), %,d on very steep (tunnels; %,d railway)%n",
                runPlots, onFoothill, onSteep, railOnSteep, onVery, railOnVery);

        long[] atA = best(counts, false), atB = best(counts, true);
        if (atA == null) {
            Files.writeString(outDir.resolve("et4-a-viaduct.txt"), String.format("No plot of the runs of a Medellin city played %d months lies on steep ground (%,d run plots, %,d on foothill, %,d on very steep): there is no viaduct to draw. The fixture tile (et4-c-fixture.png, or et4-b-fixture-tunnel.png) shows one.\n",
                    g.getMonth(), runPlots, onFoothill, onVery));
            System.out.println("render a: no viaduct in this run");
            readme.add("- `et4-a-viaduct.txt` - the run had no plot of the runs on steep ground; see the fixture picture.");
        } else {
            System.out.printf("render a: window at tiles (%d, %d)%n", atA[0], atA[1]);
            write(outDir, "et4-a-medellin-viaduct.png", window(map, atA[0], atA[1]));
            readme.add("- `et4-a-medellin-viaduct.png` - the window of 4 x 3 tiles (1,024 x 768 px, 8 px a plot) of the Medellin city with the most plots of its highways and railway on steep ground: each drawn as a viaduct (a deck, the highway cased in the deck's colour).");
        }
        if (atB == null) {
            Files.writeString(outDir.resolve("et4-b-tunnel.txt"), String.format("No plot of the runs of a Medellin city played %d months lies on very steep ground (%,d run plots, %,d on foothill, %,d on steep): there is no tunnel to draw in the city. The harness's fixture tile is drawn instead, as et4-b-fixture-tunnel.png.\n",
                    g.getMonth(), runPlots, onFoothill, onSteep));
            System.out.println("render b: no tunnel in this run; the fixture tile written as et4-b-fixture-tunnel.png");
            write(outDir, "et4-b-fixture-tunnel.png", fixture);
            readme.add("- `et4-b-tunnel.txt` - the run had no plot of the runs on very steep ground.");
            readme.add("- `et4-b-fixture-tunnel.png` - RunWorksCheck's fixture tile at 8 px (256 x 256): a highway along the top and a track down the left, each across flat, steep, very steep and flat ground - the viaduct on the steep stretch, the tunnel on the very steep with its dashed line and a portal at each end.");
        } else {
            System.out.printf("render b: window at tiles (%d, %d)%n", atB[0], atB[1]);
            write(outDir, "et4-b-medellin-tunnel.png", window(map, atB[0], atB[1]));
            readme.add("- `et4-b-medellin-tunnel.png` - the window of 4 x 3 tiles (1,024 x 768 px, 8 px a plot) with the most plots of the runs on very steep ground: each drawn as a tunnel (the ground, the run's dashed line, a dark portal band at each end).");
            write(outDir, "et4-c-fixture.png", fixture);
            readme.add("- `et4-c-fixture.png` - RunWorksCheck's fixture tile at 8 px (256 x 256): a highway along the top and a track down the left, each across flat, steep, very steep and flat ground.");
        }

        Files.writeString(outDir.resolve("README.md"), String.join("\n", readme) + "\n", java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(outDir.resolve("README.md") + "  " + Files.size(outDir.resolve("README.md")) + " bytes");
        System.exit(0);
    }

    /**
     * The window's top-left tile with the most plots of the runs on steep ground (tunnels false) or on very steep (true),
     * a window being ACROSS x DOWN tiles; null where no tile has any.
     */
    private static long[] best(Map<Long, Count> counts, boolean tunnels) {
        long bestScore = 0;
        long[] at = null;
        for (long k : counts.keySet()) {
            long kx = (int) (k >> 32), ky = (int) k;
            if ((tunnels ? counts.get(k).tunnels : counts.get(k).viaducts) == 0) continue;
            // Windows that hold this tile; the tile itself is kept off the window's edge where it can be.
            for (int ox = 0; ox < ACROSS; ox++) {
                for (int oy = 0; oy < DOWN; oy++) {
                    long wx = kx - ox, wy = ky - oy;
                    Count w = new Count();
                    for (int j = 0; j < DOWN; j++) for (int i = 0; i < ACROSS; i++) { Count c = counts.get(key(wx + i, wy + j)); if (c != null) w.add(c); }
                    // Both counted, the one asked for first: a window with the other as well is the better picture.
                    long score = (tunnels ? w.tunnels : w.viaducts) * 1_000_000L + (tunnels ? w.viaducts : w.tunnels) * 1_000L + w.runs;
                    if (score > bestScore) { bestScore = score; at = new long[] { wx, wy }; }
                }
            }
        }
        return at;
    }

    private static long key(long tx, long ty) { return (tx << 32) | (ty & 0xffffffffL); }

    /** A window of ACROSS x DOWN tiles from tile (tx0, ty0), as the screen draws it at PX a plot. */
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
}
