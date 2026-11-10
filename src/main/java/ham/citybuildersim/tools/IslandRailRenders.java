package ham.citybuildersim.tools;

import ham.citybuildersim.CityMap;
import ham.citybuildersim.IslandRailCheck;
import ham.citybuildersim.TilePainter;
import ham.citybuildersim.TileRaster;
import ham.citybuildersim.World;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Renders what RI1 did for an island the city's port links, for review: the island's own railway, a rail hub nearest the terminal with
 * its track and yard, beside the home hub's, drawn by the game's own painters, headless.
 *
 *     java -cp "target/classes;<gson.jar>" -Dcbs.earth=data/earth-2.bin ham.citybuildersim.tools.IslandRailRenders [outDir]
 *
 * The city is IslandRailCheck's section 8 (IslandRailCheck.portIsland(): Rotterdam, the island of 648 linked by its General Cargo
 * Terminal, a railway with yards, buildings on the island; built in GameFiles.scratch() folders deleted when the JVM exits - never the
 * real saves) and its map read tile by tile through CityMap.tileInput(), TilePainter.paint() and TileRaster.raster() at 8 px a plot, as
 * the screen reads it. outDir is docs/queue/RI1 unless named; the fixture is skipped, and nothing written, where the Earth file is not
 * beside the classes. It writes:
 *
 *   ri1-a-island-port.png   the window of 4 x 3 tiles (1,024 x 768 px) centred on the island's rail hub: the terminal, the hub, the
 *                           island's track and the yard that stands on it
 *   ri1-b-home.png          the same window centred on the home rail hub, for comparison
 *   ri1-renders.txt         one line a render: its tiles, the hub and terminal plots, the island's rail plots (the net's piecePlots
 *                           for the island's piece) and yards, and the railway plots the window shows
 *
 * WHY (RI1 slice C): the railway on the island is checked by eye. IslandRailCheck holds the hub, the track and the yards; these show
 * the game drawing them. Not a harness: nothing here asserts, and nothing under docs/ but its outDir is written.
 */
public final class IslandRailRenders {

    private IslandRailRenders() { }

    /** Pixels a plot, and the window in tiles: 8 x 32 x 4 by 8 x 32 x 3, 1,024 x 768 (RunRenders'). */
    private static final int PX = 8, ACROSS = 4, DOWN = 3;

    public static void main(String[] args) throws Exception {
        Path outDir = args.length > 0 ? Path.of(args[0]) : Path.of("docs", "queue", "RI1");
        IslandRailCheck.PortIsland pi = IslandRailCheck.portIsland();
        if (pi == null) {
            System.out.println("no island city to draw: EarthCheck's fixture is not beside the classes, or the island has no terminal, piece or railway hub");
            System.exit(0);
        }
        Files.createDirectories(outDir);
        CityMap map = pi.map;
        long[] home = map.runs().rail().hub();
        List<String> lines = new ArrayList<>();
        long[] at = windowAt(pi.railHub);
        int[] railA = new int[1];
        write(outDir, "ri1-a-island-port.png", window(map, at[0], at[1], railA));
        lines.add(describe("ri1-a-island-port.png: the island's piece " + pi.piece, at, pi.railHub, "island rail hub", pi.mid, railA[0])
                + String.format("; the island's rail plots (net piece %d) %,d of the railway's %,d; %d yards on the island %s; island tiles %d",
                        pi.railPiece, map.runs().rail().piecePlots(pi.railPiece), map.runs().rail().plots(), pi.yards.size(), yardList(pi.yards), pi.tiles.size()));
        if (home == null) {
            lines.add("ri1-b-home.png: not written, the railway has no home hub");
        } else {
            long[] atHome = windowAt(home);
            int[] railB = new int[1];
            write(outDir, "ri1-b-home.png", window(map, atHome[0], atHome[1], railB));
            lines.add(describe("ri1-b-home.png: the home piece 0", atHome, home, "home rail hub", null, railB[0])
                    + String.format("; the home piece's rail plots %,d", map.runs().rail().piecePlots(0)));
        }
        Files.writeString(outDir.resolve("ri1-renders.txt"), String.join("\n", lines) + "\n", StandardCharsets.UTF_8);
        for (String s : lines) System.out.println(s);
        System.out.println(outDir.resolve("ri1-renders.txt") + "  " + Files.size(outDir.resolve("ri1-renders.txt")) + " bytes");
        System.exit(0);
    }

    /** The window's top-left tile that centres a plot: the hub's tile is the middle row, and the middle column boundary the nearer to it. */
    private static long[] windowAt(long[] plot) {
        return new long[] { Math.floorDiv(plot[0] + World.TILE / 2, (long) World.TILE) - ACROSS / 2, Math.floorDiv(plot[1], (long) World.TILE) - DOWN / 2 };
    }

    /** One render's line: the tiles, the hub, the terminal (null for none) and whether it lies in the window, and the railway plots the window shows. */
    private static String describe(String what, long[] at, long[] hub, String hubName, long[] mid, int railShown) {
        long x0 = at[0] * World.TILE, y0 = at[1] * World.TILE, x1 = (at[0] + ACROSS) * World.TILE, y1 = (at[1] + DOWN) * World.TILE;
        StringBuilder sb = new StringBuilder(what);
        sb.append(String.format(": tiles (%d, %d) to (%d, %d), plots x %d..%d y %d..%d; %s %s", at[0], at[1], at[0] + ACROSS - 1, at[1] + DOWN - 1, x0, x1 - 1, y0, y1 - 1, hubName, Arrays.toString(hub)));
        if (mid != null) sb.append(String.format("; terminal box middle %s (%s the window)", Arrays.toString(mid), mid[0] >= x0 && mid[0] < x1 && mid[1] >= y0 && mid[1] < y1 ? "in" : "outside"));
        sb.append(String.format("; %,d railway plots drawn in the window", railShown));
        return sb.toString();
    }

    private static String yardList(List<long[]> yards) {
        StringBuilder sb = new StringBuilder("[");
        for (long[] y : yards) sb.append(sb.length() > 1 ? ", " : "").append(String.format("(%d, %d) %d x %d", y[0], y[1], y[2], y[3]));
        return sb.append("]").toString();
    }

    /** A window of ACROSS x DOWN tiles from tile (tx0, ty0), as the screen draws it at PX a plot; railPlots[0] is how many of its plots the painter marks as railway track. */
    private static BufferedImage window(CityMap map, long tx0, long ty0, int[] railPlots) {
        int side = World.TILE * PX;
        BufferedImage img = new BufferedImage(ACROSS * side, DOWN * side, BufferedImage.TYPE_INT_RGB);
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int[] tile = new int[side * side];
        for (int j = 0; j < DOWN; j++) {
            for (int i = 0; i < ACROSS; i++) {
                map.tileInput(tx0 + i, ty0 + j, in);
                TilePainter.paint(in, p);
                for (int k = 0; k < TilePainter.PLOTS; k++) if (p.run[k] == TilePainter.RAIL) railPlots[0]++;
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
