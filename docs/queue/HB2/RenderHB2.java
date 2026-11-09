import ham.citybuildersim.BoatSchedule;
import ham.citybuildersim.BuildingCatalog;
import ham.citybuildersim.BuildingVisual;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.CityMap;
import ham.citybuildersim.CityShore;
import ham.citybuildersim.EarthData;
import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.MapFrame;
import ham.citybuildersim.Ports;
import ham.citybuildersim.ShipShapes;
import ham.citybuildersim.TilePainter;
import ham.citybuildersim.TileRaster;
import ham.citybuildersim.World;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

/**
 * Renders HB2's three ideas for Jerus's pick, headless: the quay showing its stock, rail sidings on the quay, and a near-view crane
 * moving a box - each in three looks, drawn over HB1's settled yard (b) by the game's own painters and colours.
 *
 *     java -cp "target/classes;<gson.jar>" -Dcbs.earth=<earth-2.bin> docs/queue/HB2/RenderHB2.java [outDir]
 *
 * A single-file program (no class is written): it lives beside its pictures, not in the game's source. outDir is docs/queue/HB2 unless
 * named; -Drun.months=N plays the city N months (2,400 unless named). One city is founded at Bergen (LongPlaytest.earthCity(), in a
 * GameFiles.scratch() folder deleted when the JVM exits - never the real saves). It writes hb2-1a/1b/1c (the stock), hb2-2a/2b/2c (the
 * rail), hb2-3-crane.png and hb2-3a/3b/3c.gif (the crane), and README.md with what each shows and what each costs to draw.
 *
 * WHY (HB2): each idea is put to Jerus with renders before it is built. tools.HarbourRenders made HB1's; its yard, wall and boat
 * painters are private, so the ones these pictures need are copied here and given a level, a siding and a crane. Not a harness:
 * nothing here asserts.
 */
public final class RenderHB2 {

    private RenderHB2() { }

    /** Pixels a plot the pictures are drawn at (the tile raster's own near width, MapFrame.BIG_TILE_PX), and a specimen cell's width in plots. */
    static final int PX = 8, CW = 20;

    /** North, east, south, west: CityShore.DX and DY, which are package-private. */
    static final int[] DX = { 0, 1, 0, -1 }, DY = { -1, 0, 1, 0 };

    /** A yard's ground as HB1's (b) drew it, and the colours the new parts are drawn in: the game's own where it has one. */
    static final int GROUND = mix(0xff2e3640, TileRaster.YARD_GROUND, 0.32), EDGE = 0xff4a5866, DARK = 0xff2b2f36, SHADE = 0xff161b21,
            LIGHT = mix(TileRaster.STEEL, DARK, 0.3), ORANGE = 0xffff9a2e, STOP = 0xffa8442f, SEA = 0xff4f87b9, LAND = 0xff8aa07a;

    /** The levels a sheet shows a yard at, and the frames and hundredths of a second a frame of a crane's loop. */
    static final double[] LEVELS = { 0.12, 0.5, 0.92 };
    static final int FRAMES = 40, FRAME_CS = 7;

    enum Kind { CONTAINER, DRY_BULK, LIQUID, GENERAL, TANKS }

    /** A window on the world: the plot at its top left and the pixels a plot. */
    record Frame(long x0, long y0, int px) { }

    /** A berth, its work and the boat docked at it. */
    record Docked(CityShore.Work work, BoatSchedule.Berth berth, BoatSchedule.Boat boat) { }

    /** What a terminal is drawn with: the stock's look ('0' HB1's yard as settled, else a, b or c), the rail's and the crane's ('0' none). */
    record Look(char stock, char rail, char crane) { }

    /** The part of a box its stock may stand on, in the box's own pixels, and the apron left clear at its sea end. */
    record Zone(int u0, int v0, int u1, int v1, int apron) {
        int W() { return u1 - u0; }
        int H() { return v1 - v0; }
    }

    static BuildingVisual.Type[] TYPES;
    static final Map<Integer, BuildingsTemplate> TEMPLATES = new HashMap<>();
    static final int[] BOX = new int[60];
    /** Shapes filled since it was last set to nothing: what a look costs, counted. */
    static long shapes;

    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "true");
        Path file = EarthData.locate();
        if (file == null || !Files.isRegularFile(file)) {
            System.out.println("no earth-2 file: -Dcbs.earth=<path>, or put it at " + EarthData.DATA_FILE);
            return;
        }
        System.setProperty(EarthData.PROPERTY, file.toString());
        Path outDir = args.length > 0 ? Path.of(args[0]) : Path.of("docs", "queue", "HB2");
        Files.createDirectories(outDir);
        int months = Integer.getInteger("run.months", 2400);
        PrintStream silent = new PrintStream(new OutputStream() { @Override public void write(int x) { } });
        for (BuildingsTemplate t : new BuildingCatalog().load()) TEMPLATES.put(t.getId(), t);
        BoatSchedule.Boat any = new BoatSchedule.Boat(new BoatSchedule.Call(Ports.Cargo.CONTAINER, Ports.Ship.FEEDER, true, 0, 0), 0, 0, true, true, 0, 1);
        for (int k = 0; k < BOX.length; k++) BOX[k] = ShipShapes.boxColour(any, k);

        long t0 = System.currentTimeMillis();
        Game g = LongPlaytest.earthCity("bergen", months, GameFiles.scratch("hb2renders-bergen"), silent);
        long seconds = (System.currentTimeMillis() - t0) / 1000;
        System.out.printf("a Bergen city played %d months in %d s%n", g.getMonth(), seconds);
        CityMap map = g.getCityMap();
        TYPES = map.types();
        CityShore shore = map.shore();

        List<List<BuildingVisual.Type>> rows = new ArrayList<>();
        for (String[] names : new String[][] { { "Container Terminal" }, { "Bulk Terminal" }, { "Tanker Terminal" }, { "General Cargo Terminal", "Bulk Berth" } }) {
            List<BuildingVisual.Type> row = new ArrayList<>();
            for (String n : names) { BuildingVisual.Type t = named(n); if (t != null) row.add(t); }
            if (!row.isEmpty()) rows.add(row);
        }

        // The city's own harbour, for the band under each sheet: the window with the most terminals, as the screen draws it.
        List<Docked> docked = docked(g, map);
        long[] crop = crop(shore.works(), 88, 44);
        BufferedImage base = null;
        long tx0 = 0, ty0 = 0;
        int across = 0, down = 0;
        if (crop != null) {
            tx0 = Math.floorDiv(crop[0], World.TILE);
            ty0 = Math.floorDiv(crop[1], World.TILE);
            across = (int) (Math.floorDiv(crop[0] + crop[2] - 1, World.TILE) - tx0) + 1;
            down = (int) (Math.floorDiv(crop[1] + crop[3] - 1, World.TILE) - ty0) + 1;
            base = tiles(map, tx0, ty0, across, down, PX);
        }
        final BufferedImage baseF = base;
        final long fx = tx0 * World.TILE, fy = ty0 * World.TILE;

        List<String> readme = new ArrayList<>();
        readme.add("# HB2 renders: the harbour's stock, its rail and a crane");
        readme.add("");
        readme.add("Written by `docs/queue/HB2/RenderHB2.java` (a single-file program; its header says how it is run): a Bergen city on the real Earth"
                + " played " + g.getMonth() + " months in " + seconds + " s in a scratch folder, and specimen terminals on a plain shore, each at its real box"
                + " size with its quay wall and the ship its schedule brings. Everything is drawn at 8 px a plot, the tile raster's near width; a sheet"
                + " shows the specimens doubled (the closest zoom, " + MapFrame.MOST_PX_A_PLOT + " px a plot) and the city's own harbour at 8 and at 4 px a plot (the 8 px"
                + " picture halved: the detail starts at " + MapFrame.DETAIL_FROM + " px a plot, where the scale bar first reads 500 m). All of it is drawn over HB1's"
                + " settled yard (b). Not a harness; nothing in it asserts.");
        readme.add("");

        /* ----- 1: the stock ----- */
        String[][] stock = {
            { "a", "hb2-1a-fills.png", "1a - the yard fills and empties",
              "Boxes, heaps, crates and full tanks are there in the number the level gives; an empty slot shows its painted marking, an empty heap its pad." },
            { "b", "hb2-1b-rises.png", "1b - the stacks rise",
              "The yard is always laid out in full; the level is its height: a low stack dim and flat, a high one bright with a shadow, a heap more rings, a tank a lighter roof." },
            { "c", "hb2-1c-gauge.png", "1c - a gauge on the box",
              "HB1's yard as settled, never changing, and a bar along the land side of the box filled to the level in the cargo's colour." } };
        readme.add("## 1. The quay shows its stock");
        readme.add("");
        for (String[] s : stock) {
            Look look = new Look(s[0].charAt(0), '0', '0');
            BufferedImage ctx = baseF == null ? null : context(baseF, fx, fy, crop, shore, docked, look, 0);
            write(outDir, s[1], sheet("HB2 " + s[2], List.of(s[3]), new String[] { "low (12%)", "half (50%)", "full (92%)" }, rows, look, LEVELS,
                    new boolean[3], ctx, "each terminal at a level of its own"));
            readme.add("- `" + s[1] + "` - " + s[2].substring(5) + ". " + s[3]);
        }
        readme.add("");

        /* ----- 2: the rail ----- */
        String[][] rail = {
            { "a", "hb2-2a-quay-tracks.png", "2a - tracks on the quay",
              "Two tracks along the apron behind the wall (one at a small berth), the wagons under the ship's side; the yard gives up its two plots nearest the water." },
            { "b", "hb2-2b-back-sidings.png", "2b - sidings behind the yard",
              "A fan of three sidings along the land side closing to one line, the rail yard's ladder in small (one track at a small berth); the apron stays clear." },
            { "c", "hb2-2c-side-spur.png", "2c - a spur down the side",
              "One track in from the land side down the edge of the box to a buffer short of the wall: the siding a mine or a works has today." } };
        readme.add("## 2. Rail sidings on the quay");
        readme.add("");
        readme.add("Each sheet draws the tracks inside the terminal's box alone. The line from the railway to the box is the placement's work (rail leaning toward a port) and is not drawn: the railway has no port on it today, so the city's band shows sidings no line reaches yet.");
        readme.add("");
        for (String[] s : rail) {
            Look look = new Look('0', s[0].charAt(0), '0');
            BufferedImage ctx = baseF == null ? null : context(baseF, fx, fy, crop, shore, docked, look, 0);
            write(outDir, s[1], sheet("HB2 " + s[2], List.of(s[3]), new String[] { "the sidings empty", "a train in" }, rows, look, new double[] { 0.5, 0.5 },
                    new boolean[] { false, true }, ctx, "a train at every other terminal"));
            readme.add("- `" + s[1] + "` - " + s[2].substring(5) + ". " + s[3]);
        }
        readme.add("");

        /* ----- 3: the crane ----- */
        String[][] crane = {
            { "a", "hb2-3a-line.gif", "3a - the pixel line",
              "His words: one light line a berth from the ship across the wall, and an orange box sliding along it to the wagon and the hook coming back bare." },
            { "b", "hb2-3b-gantry.gif", "3b - a gantry on the wall",
              "HB1's quay crane (four legs and a boom out over the ship), standing still; the box rides its boom." },
            { "c", "hb2-3c-travelling.gif", "3c - a gantry that travels",
              "The same crane walking a plot along the wall between moves, to work the ship bay by bay." } };
        readme.add("## 3. A crane at the near view");
        readme.add("");
        readme.add("Drawn with 2a's tracks under it, so the box goes between ship and wagon. A dry bulk berth's crane carries a grab of ore, not a box;"
                + " a tanker's has a loading arm that does not move. A loop is two moves in " + String.format("%.1f", FRAMES * FRAME_CS / 100.0) + " s; the game's boat lies at its quay "
                + String.format("%.1f", BoatSchedule.BERTH_SECONDS) + " s of a " + (int) BoatSchedule.MONTH_SECONDS + " s month (BoatSchedule.BERTH_SECONDS), so a call is one or two moves.");
        readme.add("");
        List<BufferedImage> strip = new ArrayList<>();
        for (String[] s : crane) {
            Look look = new Look('0', 'a', s[0].charAt(0));
            List<BufferedImage> frames = new ArrayList<>();
            for (int k = 0; k < FRAMES; k++) frames.add(craneFrame("HB2 " + s[2], rows, look, (k + 0.5) / FRAMES));
            gif(outDir.resolve(s[1]), frames, FRAME_CS);
            System.out.println(outDir.resolve(s[1]) + "  " + Files.size(outDir.resolve(s[1])) + " bytes");
            readme.add("- `" + s[1] + "` - " + s[2].substring(5) + ". " + s[3]);
        }
        write(outDir, "hb2-3-crane.png", craneStrip(crane, rows, baseF, fx, fy, crop, shore, docked));
        readme.add("- `hb2-3-crane.png` - the three as stills: four moments of a move a row (out, half way, set down, the hook coming back), and each in the city.");
        readme.add("");

        /* ----- what each costs ----- */
        readme.add("## What each costs to draw");
        readme.add("");
        readme.add("Measured here, in Java2D on the PC with other work running, the least of 40 rounds: an estimate to set the looks beside one another, not the"
                + " game's canvas. `MapView.FRAME_MS` is 8 ms of tile painting a frame; `MapCheck.BOAT_FRAME_MS` is 0.5 ms a frame for the boats.");
        readme.add("");
        List<CityShore.Work> works = shore.works();
        if (baseF != null) {
            final long ftx = tx0, fty = ty0;
            final int fa = across, fd = down;
            readme.add(String.format("- the game's own tile, painted and rastered (TilePainter.paint, TileRaster.raster): %.2f ms a tile at 8 px a plot, %.2f at 4,"
                    + " over the %d tiles of the band's window.", tileMs(map, ftx, fty, fa, fd, 8), tileMs(map, ftx, fty, fa, fd, 4), fa * fd));
        }
        readme.add("- the city's " + works.size() + " shore works drawn once (a stock or a rail look is still: it is painted when the level or the rail changes, with the tile, not every frame):");
        for (char v : new char[] { '0', 'a', 'b', 'c' }) {
            double[] c = cost(() -> { for (int i = 0; i < works.size(); i++) one(works.get(i), new Look(v, '0', '0'), level(i), false, null, 0); });
            readme.add(String.format("  - stock %s: %,d shapes, %.3f ms", v == '0' ? "as HB1 settled it" : "1" + v, (long) c[0], c[1]));
        }
        for (char v : new char[] { 'a', 'b', 'c' }) {
            double[] c = cost(() -> { for (int i = 0; i < works.size(); i++) one(works.get(i), new Look('0', v, '0'), 0.5, true, null, 0); });
            readme.add(String.format("  - rail 2%s, a train in at each: %,d shapes, %.3f ms", v, (long) c[0], c[1]));
        }
        readme.add("- a frame of the city's " + docked.size() + " berths, every one with a boat in (the crowded case):");
        double[] boats = cost(() -> { for (Docked d : docked) one(d.work(), null, 0, false, d, 0.3); });
        readme.add(String.format("  - the boats alone, for scale: %,d shapes, %.3f ms", (long) boats[0], boats[1]));
        for (char v : new char[] { 'a', 'b', 'c' }) {
            double[] c = cost(() -> { for (Docked d : docked) one(d.work(), new Look('0', '0', v), 0, false, d, 0.3); });
            readme.add(String.format("  - crane 3%s, with nothing else: %,d shapes, %.3f ms (%.0f%% of the boats')", v, (long) c[0], c[1], boats[1] > 0 ? 100 * c[1] / boats[1] : 0));
        }
        readme.add("");
        Files.writeString(outDir.resolve("README.md"), String.join("\n", readme) + "\n", StandardCharsets.UTF_8);
        for (String line : readme) if (line.startsWith("- ") || line.startsWith("  - ")) System.out.println(line);
        System.exit(0);
    }

    /** The berthed type a building of this name is, or null. */
    static BuildingVisual.Type named(String name) {
        for (BuildingVisual.Type t : TYPES) {
            if (t != null && t.berth() >= 0 && TEMPLATES.get(t.id()) != null && TEMPLATES.get(t.id()).getName().equals(name)) return t;
        }
        return null;
    }

    /** The level the city's i-th work is shown at: spread over the range, the same every run. */
    static double level(int i) {
        double x = i * 0.618034 + 0.3;
        return 0.1 + 0.84 * (x - Math.floor(x));
    }

    /* ----- the city: its tiles, its berths and their boats (as tools.HarbourRenders reads them) ----- */

    /** A window of across x down tiles from tile (tx0, ty0), as the screen draws it at px a plot. */
    static BufferedImage tiles(CityMap map, long tx0, long ty0, int across, int down, int px) {
        int side = World.TILE * px;
        BufferedImage img = new BufferedImage(across * side, down * side, BufferedImage.TYPE_INT_RGB);
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int[] tile = new int[side * side];
        for (int j = 0; j < down; j++) {
            for (int i = 0; i < across; i++) {
                map.tileInput(tx0 + i, ty0 + j, in);
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, px, tile);
                for (int y = 0; y < side; y++) for (int x = 0; x < side; x++) img.setRGB(i * side + x, j * side + y, tile[y * side + x] & 0xffffff);
            }
        }
        return img;
    }

    /** The milliseconds a tile of the window takes to paint and raster at px a plot: the least of 5 rounds' means after 2. */
    static double tileMs(CityMap map, long tx0, long ty0, int across, int down, int px) {
        int side = World.TILE * px;
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int[] tile = new int[side * side];
        double best = Double.MAX_VALUE;
        for (int round = 0; round < 7; round++) {
            long a = System.nanoTime();
            for (int j = 0; j < down; j++) for (int i = 0; i < across; i++) {
                map.tileInput(tx0 + i, ty0 + j, in);
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, px, tile);
            }
            double ms = (System.nanoTime() - a) / 1e6 / (across * down);
            if (round >= 2) best = Math.min(best, ms);
        }
        return best;
    }

    /** The ship a terminal's boats are: its first class (Ports.shipsOf()), else the first of its cargo. */
    static Ports.Ship shipFor(BuildingsTemplate t, Ports.Cargo cargo) {
        List<Ports.Ship> ships = t == null ? List.of() : Ports.shipsOf(t);
        if (!ships.isEmpty()) return ships.get(0);
        for (Ports.Ship s : Ports.Ship.values()) if (s.cargo() == cargo) return s;
        throw new IllegalStateException("no ship for " + cargo);
    }

    /** A laden call of `ship` coming in on route i, docked at the berth's point as the schedule places it, bow out. */
    static BoatSchedule.Boat bowOut(BoatSchedule.Berth b, Ports.Ship ship, int i) {
        BoatSchedule.Call call = new BoatSchedule.Call(b.cargo(), ship, true, i, i * 0.173);
        double heading = b.side() >= 0 ? Math.atan2(DY[b.side()], DX[b.side()]) : 0;
        return new BoatSchedule.Boat(call, b.x() + 0.5, b.y() + 0.5, true, true, heading, 1);
    }

    /** The city's berths, each with its work and a boat docked at it, every berth occupied at once, laid by BoatSchedule.berthed(). */
    static List<Docked> docked(Game g, CityMap map) {
        CityShore shore = map.shore();
        List<BoatSchedule.Berth> berths = shore.berths(TYPES);
        List<CityShore.Work> mine = new ArrayList<>();
        for (CityShore.Work w : shore.works()) {
            if (w.berthed() && w.type() >= 0 && w.type() < TYPES.length && TYPES[w.type()] != null && TYPES[w.type()].berth() >= 0) mine.add(w);
        }
        if (mine.size() != berths.size()) throw new IllegalStateException(mine.size() + " berthed works but " + berths.size() + " berths");
        List<Ports.Ship> standing = Ports.bulkShipsStanding(g.getBuildingManager());
        Ports.Ship bulk = standing.isEmpty() ? Ports.Ship.HANDYSIZE : standing.get(0);
        List<BoatSchedule.Route> routes = new ArrayList<>();
        List<BoatSchedule.Boat> bowOut = new ArrayList<>();
        for (int i = 0; i < berths.size(); i++) {
            BoatSchedule.Berth b = berths.get(i);
            Ports.Ship ship = b.cargo() == Ports.Cargo.DRY_BULK ? bulk : shipFor(TEMPLATES.get(mine.get(i).type()), b.cargo());
            routes.add(new BoatSchedule.Route(b, b.x(), b.y()));
            bowOut.add(bowOut(b, ship, i));
        }
        List<BoatSchedule.Boat> laid = BoatSchedule.berthed(bowOut, routes, PX, shore);
        List<Docked> out = new ArrayList<>();
        for (int i = 0; i < berths.size(); i++) out.add(new Docked(mine.get(i), berths.get(i), laid.get(i)));
        return out;
    }

    /** The window of w x h plots holding the most of the city's terminals, {x0, y0, w, h}, or null for a city with none. */
    static long[] crop(List<CityShore.Work> works, int w, int h) {
        CityShore.Work best = null;
        int most = -1;
        for (CityShore.Work a : works) {
            if (!a.berthed()) continue;
            int n = 0;
            for (CityShore.Work b : works) if (near(a, b, w, h)) n += b.berthed() ? 2 : 1;
            if (n > most) { most = n; best = a; }
        }
        if (best == null) return null;
        long lx = Long.MAX_VALUE, ly = Long.MAX_VALUE, hx = Long.MIN_VALUE, hy = Long.MIN_VALUE;
        for (CityShore.Work b : works) {
            if (!near(best, b, w, h)) continue;
            lx = Math.min(lx, b.x0()); ly = Math.min(ly, b.y0());
            hx = Math.max(hx, b.x0() + b.w()); hy = Math.max(hy, b.y0() + b.h());
        }
        return new long[] { (lx + hx) / 2 - w / 2, (ly + hy) / 2 - h / 2, w, h };
    }

    static boolean near(CityShore.Work a, CityShore.Work b, int w, int h) {
        return Math.abs((a.x0() + a.w() / 2) - (b.x0() + b.w() / 2)) <= w / 2 - 10 && Math.abs((a.y0() + a.h() / 2) - (b.y0() + b.h() / 2)) <= h / 2 - 8;
    }

    /* ----- the drawing ----- */

    static Color col(int argb) { return new Color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255); }

    static Color col(int argb, double alpha) {
        return new Color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, (int) Math.round(255 * Math.max(0, Math.min(1, alpha))));
    }

    /** t of b over a, per channel. */
    static int mix(int a, int b, double t) {
        int r = (int) Math.round(((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int g = (int) Math.round(((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int bl = (int) Math.round((a & 255) * (1 - t) + (b & 255) * t);
        return 0xff000000 | r << 16 | g << 8 | bl;
    }

    static void aa(Graphics2D g, boolean on) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, on ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    static double clamp(double x) { return Math.max(0, Math.min(1, x)); }

    static double ease(double x) { x = clamp(x); return x * x * (3 - 2 * x); }

    /** The harbour's quays as MapView.harbourQuay() draws them: filled QUAY_DECK and, from TileRaster.EDGE_FROM px a plot, edged QUAY_EDGE. */
    static void walls(Graphics2D g, Frame f, List<CityShore.Wall> walls) {
        aa(g, false);
        g.setColor(col(TileRaster.QUAY_DECK));
        for (CityShore.Wall w : walls) g.fillRect((int) ((w.x0() - f.x0) * f.px), (int) ((w.y0() - f.y0) * f.px), w.w() * f.px, w.h() * f.px);
        if (f.px < TileRaster.EDGE_FROM) return;
        g.setColor(col(TileRaster.QUAY_EDGE));
        for (CityShore.Wall w : walls) g.drawRect((int) ((w.x0() - f.x0) * f.px), (int) ((w.y0() - f.y0) * f.px), w.w() * f.px - 1, w.h() * f.px - 1);
    }

    /** A boat as MapView.boats() draws it: hull, deck marks (a box ship's boxes in their colours), bridge, and the hull's outline. */
    static void boat(Graphics2D g, Frame f, BoatSchedule.Boat b) {
        aa(g, true);
        int c = ShipShapes.colour(b);
        g.setColor(col(b.loaded() ? ShipShapes.HULL_LADEN : ShipShapes.HULL_EMPTY, b.alpha()));
        double[][] hull = ShipShapes.hull(b, f.px);
        g.fill(poly(f, hull));
        double[][][] deck = ShipShapes.deck(b, f.px);
        boolean boxes = b.call().cargo() == Ports.Cargo.CONTAINER;
        for (int k = 0; k < deck.length; k++) {
            g.setColor(boxes ? col(ShipShapes.boxColour(b, k), b.alpha()) : col(c, 0.6 * b.alpha()));
            g.fill(poly(f, deck[k]));
        }
        g.setColor(col(ShipShapes.BRIDGE, 0.85 * b.alpha()));
        g.fill(poly(f, ShipShapes.bridge(b, f.px)));
        g.setColor(col(c, b.alpha()));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(poly(f, hull));
        shapes += deck.length + 3;
    }

    static Path2D poly(Frame f, double[][] pts) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < pts[0].length; i++) {
            double x = (pts[0][i] - f.x0) * f.px, y = (pts[1][i] - f.y0) * f.px;
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        return p;
    }

    /**
     * A work's box in its own frame, in pixels: u along the sea side (left to right as the sea is faced), v from the land side (0) out to
     * the sea side (D) and on over the wall. X() and Y() turn a point of it to the picture, so a yard is drawn once for the four sides.
     */
    static final class Local {
        final int px, ax, ay, dx, dy, L, D, quay;
        final double bx, by, ox, oy;

        Local(CityShore.Work w, Frame f) {
            px = f.px;
            int s = w.side();
            dx = DX[s];
            dy = DY[s];
            ax = -dy;
            ay = dx;
            boolean ns = s % 2 == 0;
            L = (ns ? w.w() : w.h()) * px;
            D = (ns ? w.h() : w.w()) * px;
            quay = w.quay();
            long wx, wy;
            switch (s) {
                case 0: wx = w.x0(); wy = w.y0() + w.h(); break;
                case 1: wx = w.x0(); wy = w.y0(); break;
                case 2: wx = w.x0() + w.w(); wy = w.y0(); break;
                default: wx = w.x0() + w.w(); wy = w.y0() + w.h();
            }
            ox = wx;
            oy = wy;
            bx = (wx - f.x0()) * px;
            by = (wy - f.y0()) * px;
        }

        double X(double u, double v) { return bx + ax * u + dx * v; }

        double Y(double u, double v) { return by + ay * u + dy * v; }

        /** The u and the v of a point given in world plots. */
        double uOf(double wx, double wy) { return ((wx - ox) * ax + (wy - oy) * ay) * px; }

        double vOf(double wx, double wy) { return ((wx - ox) * dx + (wy - oy) * dy) * px; }

        void rect(Graphics2D g, double u0, double v0, double u1, double v1, int argb) { rect(g, u0, v0, u1, v1, 0, argb); }

        /** ...moved `off` pixels right and down on the picture, whichever way the box faces: a shadow. */
        void rect(Graphics2D g, double u0, double v0, double u1, double v1, int off, int argb) {
            quad(g, new double[] { u0, u1, u1, u0 }, new double[] { v0, v0, v1, v1 }, off, argb);
        }

        void quad(Graphics2D g, double[] us, double[] vs, int off, int argb) {
            Path2D.Double p = new Path2D.Double();
            for (int i = 0; i < us.length; i++) {
                double x = X(us[i], vs[i]) + off, y = Y(us[i], vs[i]) + off;
                if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
            }
            p.closePath();
            aa(g, false);
            g.setColor(col(argb));
            g.fill(p);
            shapes++;
        }

        /** A rectangle with a 1 px edge. */
        void framed(Graphics2D g, double u0, double v0, double u1, double v1, int fill, int edge) {
            rect(g, u0, v0, u1, v1, edge);
            rect(g, u0 + 1, v0 + 1, u1 - 1, v1 - 1, fill);
        }

        void ellipse(Graphics2D g, double cu, double cv, double ru, double rv, int argb) {
            double ex = Math.abs(ax) * ru + Math.abs(dx) * rv, ey = Math.abs(ay) * ru + Math.abs(dy) * rv;
            double cx = X(cu, cv), cy = Y(cu, cv);
            aa(g, true);
            g.setColor(col(argb));
            g.fill(new Ellipse2D.Double(cx - ex, cy - ey, 2 * ex, 2 * ey));
            shapes++;
        }
    }

    /** What a work's box is drawn as, or null for a work these pictures leave as it is. */
    static Kind kindOf(CityShore.Work w) {
        if (w.type() < 0 || w.type() >= TYPES.length || TYPES[w.type()] == null) return null;
        BuildingVisual.Type t = TYPES[w.type()];
        if (t.berth() >= 0) {
            switch (Ports.Cargo.values()[t.berth()]) {
                case CONTAINER: return Kind.CONTAINER;
                case DRY_BULK: return Kind.DRY_BULK;
                case LIQUID: return Kind.LIQUID;
                default: return Kind.GENERAL;
            }
        }
        return t.shore() ? Kind.TANKS : null;
    }

    /* ----- 1: the yards, at a level ----- */

    /**
     * A yard drawn over its box: HB1's (b) for look.stock '0', else that yard at `level` (0 to 1) in the look's way; with look.rail, its
     * sidings (and a train on them), the stock keeping off their ground. `lead` is how far, in pixels, the line that reaches the box is drawn.
     */
    static void yard(Graphics2D g, CityShore.Work w, Frame f, Kind kind, Look look, double level, boolean train, int lead) {
        Local q = new Local(w, f);
        Random r = new Random(w.x0() * 7919L + w.y0() * 104_729L + w.type());
        q.framed(g, 0, 0, q.L, q.D, GROUND, EDGE);
        boolean port = kind != Kind.TANKS, big = q.D >= 10 * q.px;
        char rail = port ? look.rail() : '0', mode = port ? look.stock() : '0';
        Zone z;
        switch (rail) {
            case 'a': z = new Zone(0, 0, q.L, q.D - (big ? 16 : 9), 3); break;
            case 'b': z = new Zone(0, big ? 21 : 7, q.L, q.D, 10); break;
            case 'c': z = new Zone(0, 0, q.L - 9, q.D, 10); break;
            default: z = new Zone(0, 0, q.L, q.D, 10);
        }
        switch (kind) {
            case CONTAINER: containers(g, q, z, mode, level, r); break;
            case DRY_BULK: heaps(g, q, z, mode, level, r); break;
            case LIQUID:
            case TANKS: tanks(g, q, z, mode, level); break;
            default: crates(g, q, z, mode, level, r);
        }
        if (mode == 'c') gauge(g, q, kind, level);
        if (rail != '0') rails(g, q, kind, rail, big, train, lead);
    }

    /** Rows of small coloured boxes in blocks with lanes between, an apron left by the quay. */
    static void containers(Graphics2D g, Local q, Zone z, char mode, double level, Random r) {
        int margin = 4, back = 5, boxU = 5, boxV = 3, rows = 3, lane = 4, blockV = rows * (boxV + 1) - 1;
        int k = r.nextInt(1000);
        List<int[]> slots = new ArrayList<>();
        for (int v = z.v0() + back; v + blockV <= z.v1() - z.apron(); v += blockV + lane) {
            for (int row = 0; row < rows; row++) {
                int slot = 0;
                for (int u = z.u0() + margin; u + boxU <= z.u1() - margin; u += boxU + 1, slot++) {
                    if (slot % 7 == 6) continue;                                     // a lane across the rows
                    slots.add(new int[] { u, v + row * (boxV + 1), r.nextInt(14) == 0 ? 0 : 1 });
                }
            }
        }
        int n = slots.size();
        if (mode == 'a') {
            // The yard fills from the land side toward the quay, its front ragged: each slot's turn is its place and a little chance.
            double[] turn = new double[n];
            for (int i = 0; i < n; i++) turn[i] = 0.72 * i / Math.max(1, n) + 0.28 * r.nextDouble();
            double[] sorted = turn.clone();
            java.util.Arrays.sort(sorted);
            int filled = (int) Math.round(level * n);
            double cut = filled >= n ? 2 : sorted[filled];
            for (int i = 0; i < n; i++) {
                int[] s = slots.get(i);
                q.rect(g, s[0], s[1], s[0] + boxU, s[1] + boxV, turn[i] < cut ? BOX[k++ % BOX.length] : mix(GROUND, 0xffffffff, 0.07));
            }
            return;
        }
        if (mode == 'b') {
            // Every slot is taken; the level is how high it is stacked, one to four.
            int[] tier = new int[n];
            for (int i = 0; i < n; i++) tier[i] = Math.max(1, Math.min(4, (int) Math.ceil(level * 4 + (r.nextDouble() - 0.5) * 1.4)));
            for (int i = 0; i < n; i++) {
                int[] s = slots.get(i);
                if (s[2] == 1 && tier[i] >= 3) q.rect(g, s[0], s[1], s[0] + boxU, s[1] + boxV, tier[i] - 2, SHADE);
            }
            double[] dim = { 0.62, 0.4, 0.16, 0 };
            for (int i = 0; i < n; i++) {
                int[] s = slots.get(i);
                if (s[2] == 0) continue;
                int c = BOX[k++ % BOX.length];
                q.rect(g, s[0], s[1], s[0] + boxU, s[1] + boxV, mix(c, 0xff20262d, dim[tier[i] - 1]));
                if (tier[i] == 4) q.rect(g, s[0], s[1], s[0] + boxU, s[1] + 1, mix(c, 0xffffffff, 0.45));
            }
            return;
        }
        for (int[] s : slots) if (s[2] == 1) q.rect(g, s[0], s[1], s[0] + boxU, s[1] + boxV, BOX[k++ % BOX.length]);
    }

    /** Two or three heaps, ochre or grey, each a darker ring and a lighter crown. */
    static void heaps(Graphics2D g, Local q, Zone z, char mode, double level, Random r) {
        int W = z.W(), H = z.H(), n = W >= 12 * q.px ? 3 : 2, flip = r.nextInt(2);
        for (int i = 0; i < n; i++) {
            double cu = z.u0() + W * (i + 0.5) / n + (r.nextDouble() - 0.5) * 4, cv = z.v0() + H * 0.46 + (r.nextDouble() - 0.5) * Math.min(8, H * 0.12);
            double ru = Math.min(W / (2.0 * n) - 3, H * 0.3) * (0.85 + 0.15 * r.nextDouble()), rv = ru * 0.8;
            int c = (i + flip) % 2 == 0 ? TileRaster.ORE_HEAP : TileRaster.WORKED_GREY;
            double part = 0.8 + 0.4 * r.nextDouble();
            if (mode == 'a') {
                // The pad is always there; the heap on it has the area the level gives.
                q.ellipse(g, cu, cv, ru, rv, mix(GROUND, 0xff000000, 0.25));
                double s = Math.sqrt(clamp(level * part));
                if (s * ru < 1.5) continue;
                q.ellipse(g, cu, cv, ru * s, rv * s, mix(c, 0xff000000, 0.3));
                q.ellipse(g, cu - 1, cv - 1, ru * s * 0.82, rv * s * 0.82, c);
                if (s * ru >= 4) q.ellipse(g, cu - ru * s * 0.2, cv - rv * s * 0.2, ru * s * 0.35, rv * s * 0.35, mix(c, 0xffffffff, 0.25));
            } else if (mode == 'b') {
                // The heap keeps its ground; the level is its height, a ring a quarter.
                q.ellipse(g, cu, cv, ru, rv, mix(c, 0xff000000, 0.5));
                if (level > 0.25) q.ellipse(g, cu - 1, cv - 1, ru * 0.76, rv * 0.76, mix(c, 0xff000000, 0.28));
                if (level > 0.5) q.ellipse(g, cu - 2, cv - 2, ru * 0.52, rv * 0.52, c);
                if (level > 0.75) q.ellipse(g, cu - 3, cv - 3, ru * 0.26, rv * 0.26, mix(c, 0xffffffff, 0.35));
            } else {
                q.ellipse(g, cu, cv, ru, rv, mix(c, 0xff000000, 0.3));
                q.ellipse(g, cu - 1, cv - 1, ru * 0.82, rv * 0.82, c);
                q.ellipse(g, cu - ru * 0.2, cv - rv * 0.2, ru * 0.35, rv * 0.35, mix(c, 0xffffffff, 0.25));
            }
        }
    }

    /** Round tanks on a grid, each a rim, a roof and a hatch. */
    static void tanks(Graphics2D g, Local q, Zone z, char mode, double level) {
        int W = z.W(), H = z.H(), pitch = 3 * q.px;
        int cols = Math.max(1, (W - 4) / pitch), rows = Math.max(1, (H - 4) / pitch), n = cols * rows;
        double rad = Math.min(pitch * 0.42, Math.min(W, H) / 2.0 - 2);
        double u0 = z.u0() + (W - cols * pitch) / 2.0 + pitch / 2.0, v0 = z.v0() + (H - rows * pitch) / 2.0 + pitch / 2.0;
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                double cu = u0 + i * pitch, cv = v0 + j * pitch;
                // a: so many tanks full, the rest empty; b: every tank at the level. A floating roof rides high and light on a full tank.
                double fill = mode == 'a' ? clamp(level * n - (j * cols + i)) : mode == 'b' ? level : -1;
                q.ellipse(g, cu, cv, rad, rad, 0xff7f8ca6);
                q.ellipse(g, cu, cv, rad - 1, rad - 1, fill < 0 ? mix(0xff2a3540, 0xff7f8ca6, 0.4) : mix(0xff1d252d, 0xffa6b6d2, 0.06 + 0.86 * fill));
                if (fill < 0 || fill > 0.5) q.ellipse(g, cu, cv, 2, 2, 0xff7f8ca6);
            }
        }
    }

    /** One shed along the land side, stacked crates in groups between it and the apron. */
    static void crates(Graphics2D g, Local q, Zone z, char mode, double level, Random r) {
        int margin = 4, crate = 4, apron = Math.min(8, z.apron());
        double shedV = Math.min(14, z.H() * 0.28);
        shedRoof(g, q, z.u0() + margin, z.v0() + 4, z.u1() - margin, z.v0() + 4 + shedV);
        int[] palette = { 0xff7b4b3a, mix(TileRaster.ORE_HEAP, 0xff2e3640, 0.35), mix(TileRaster.STEEL, 0xff2e3640, 0.5), mix(0xff7b4b3a, TileRaster.ORE_HEAP, 0.5) };
        double vTop = z.v0() + 4 + shedV + 4;
        List<double[]> groups = new ArrayList<>();
        for (int u = z.u0() + margin + 2; u + 3 * (crate + 1) <= z.u1() - margin; u += 3 * (crate + 1) + 3 + r.nextInt(3)) {
            for (double v = vTop; v + 2 * (crate + 1) <= z.v1() - apron; v += 2 * (crate + 1) + 2) groups.add(new double[] { u, v, 2 + r.nextInt(2) });
        }
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) order.add(i);
        Collections.shuffle(order, r);
        int shown = mode == 'a' ? (int) Math.round(level * groups.size()) : groups.size();
        for (int at = 0; at < order.size(); at++) {
            double[] gr = groups.get(order.get(at));
            for (int i = 0; i < gr[2]; i++) for (int j = 0; j < 2; j++) {
                int c = palette[r.nextInt(palette.length)];
                double u = gr[0] + i * (crate + 1), v = gr[1] + j * (crate + 1);
                if (at >= shown) { q.rect(g, u, v, u + crate, v + crate, mix(GROUND, 0xffffffff, 0.07)); continue; }
                if (mode == 'b') {
                    if (level > 0.6) q.rect(g, u, v, u + crate, v + crate, 1, SHADE);
                    c = mix(c, 0xff20262d, 0.55 * (1 - level));
                }
                q.rect(g, u, v, u + crate, v + crate, c);
            }
        }
    }

    static void shedRoof(Graphics2D g, Local q, double u0, double v0, double u1, double v1) {
        int roof = mix(TileRaster.STEEL, 0xff2e3640, 0.55);
        q.framed(g, u0, v0, u1, v1, roof, mix(roof, 0xff000000, 0.45));
        double mid = Math.floor((v0 + v1) / 2);
        q.rect(g, u0 + 2, mid, u1 - 2, mid + 1, mix(roof, 0xffffffff, 0.3));
    }

    /** 1c's bar along the land side of the box: dark, filled to the level in the cargo's colour. */
    static void gauge(Graphics2D g, Local q, Kind kind, double level) {
        int c = kind == Kind.CONTAINER ? ShipShapes.BOXES : kind == Kind.DRY_BULK ? TileRaster.ORE_HEAP : kind == Kind.GENERAL ? 0xffb98a6a : ShipShapes.IMPORT;
        q.rect(g, 2, 1, q.L - 2, 5, SHADE);
        q.rect(g, 3, 2, 3 + (q.L - 6) * clamp(level), 4, c);
    }

    /* ----- 2: the rail ----- */

    /** A terminal's sidings in the look's way, as the game draws track at 8 px a plot (ballast, sleepers, two steel rails), and a train on them. */
    static void rails(Graphics2D g, Local q, Kind kind, char rail, boolean big, boolean train, int lead) {
        if (rail == 'a') {
            // Along the apron, just behind the wall: the outer track under the ship's side, and at a large terminal a second joining it.
            int v1 = q.D - 5, v2 = q.D - 12;
            if (big) {
                diag(g, q, 6, v1, 2 * q.px + 6, v2);
                trackU(g, q, 2 * q.px + 6, q.L - 3, v2);
                stop(g, q, q.L - 3, v2, true);
            }
            trackU(g, q, -lead, q.L - 3, v1);
            stop(g, q, q.L - 3, v1, true);
            if (train) {
                trainU(g, q, kind, big ? 3 * q.px : 2, q.L - 5, v1, true);
                if (big) trainU(g, q, kind, q.L / 2, q.L - 5, v2, false);
            }
        } else if (rail == 'b') {
            // Behind the stock, along the land side: three tracks closing to the middle one over two plots.
            if (!big) {
                trackU(g, q, -lead, q.L - 3, 4);
                stop(g, q, q.L - 3, 4, true);
                if (train) trainU(g, q, kind, 2, q.L - 5, 4, true);
                return;
            }
            int[] vs = { 4, 11, 18 };
            int ladder = 2 * q.px;
            for (int i = 0; i < 3; i += 2) diag(g, q, 6, vs[1], ladder + 6, vs[i]);
            for (int i = 0; i < 3; i++) {
                trackU(g, q, i == 1 ? -lead : ladder + 6, q.L - 3, vs[i]);
                stop(g, q, q.L - 3, vs[i], true);
            }
            if (train) {
                trainU(g, q, kind, ladder + 10, q.L - 5, vs[0], true);
                trainU(g, q, kind, q.L / 2, q.L - 5, vs[2], false);
            }
        } else {
            // In from the land side down the edge of the box, to a buffer short of the wall.
            int u = q.L - 5, end = q.D - 10;
            trackV(g, q, u, -lead, end);
            stop(g, q, u, end, false);
            if (train) trainV(g, q, kind, u, end - 2, 3);
        }
    }

    /** A track along the quay from uA to uB, its middle at v: the bed, a sleeper every third pixel, the two rails. */
    static void trackU(Graphics2D g, Local q, int uA, int uB, int v) {
        q.rect(g, uA, v - 2, uB, v + 3, TileRaster.BALLAST);
        for (int u = uA + 1; u + 1 <= uB; u += 3) q.rect(g, u, v - 2, u + 1, v + 3, TileRaster.SLEEPER);
        q.rect(g, uA, v - 1, uB, v, TileRaster.STEEL);
        q.rect(g, uA, v + 1, uB, v + 2, TileRaster.STEEL);
    }

    /** ...and one across it, from the land side toward the sea. */
    static void trackV(Graphics2D g, Local q, int u, int vA, int vB) {
        q.rect(g, u - 2, vA, u + 3, vB, TileRaster.BALLAST);
        for (int v = vA + 1; v + 1 <= vB; v += 3) q.rect(g, u - 2, v, u + 3, v + 1, TileRaster.SLEEPER);
        q.rect(g, u - 1, vA, u, vB, TileRaster.STEEL);
        q.rect(g, u + 1, vA, u + 2, vB, TileRaster.STEEL);
    }

    /** The turnout between two tracks along the quay. */
    static void diag(Graphics2D g, Local q, double uA, double vA, double uB, double vB) {
        q.quad(g, new double[] { uA, uB, uB, uA }, new double[] { vA - 2, vB - 2, vB + 3, vA + 3 }, 0, TileRaster.BALLAST);
        q.quad(g, new double[] { uA, uB, uB, uA }, new double[] { vA - 1, vB - 1, vB, vA }, 0, TileRaster.STEEL);
        q.quad(g, new double[] { uA, uB, uB, uA }, new double[] { vA + 1, vB + 1, vB + 2, vA + 2 }, 0, TileRaster.STEEL);
    }

    /** A buffer stop at a track's end. */
    static void stop(Graphics2D g, Local q, int u, int v, boolean along) {
        if (along) q.rect(g, u, v - 2, u + 1, v + 3, STOP); else q.rect(g, u - 2, v, u + 3, v + 1, STOP);
    }

    /** A wagon's body and its mark by what the terminal handles, as TrainShapes colours a full train: boxes white, the rest the import's blue. */
    static int[] wagon(Kind kind) {
        int body = kind == Kind.CONTAINER ? ShipShapes.BOXES : ShipShapes.IMPORT;
        int mark = kind == Kind.DRY_BULK ? TileRaster.ORE_HEAP : kind == Kind.LIQUID ? mix(body, 0xffffffff, 0.4) : kind == Kind.GENERAL ? mix(body, 0xff000000, 0.3) : 0;
        return new int[] { body, mark };
    }

    /** A train standing on a track along the quay: a locomotive (if asked) and a wagon every 4 px (TileRaster.WAGON_LENGTH, half a plot). */
    static void trainU(Graphics2D g, Local q, Kind kind, int uA, int uB, int v, boolean loco) {
        int[] c = wagon(kind);
        int u = uA;
        if (loco) { q.rect(g, u, v - 1, u + 5, v + 2, DARK); u += 6; }
        for (; u + 3 <= uB; u += 4) {
            q.rect(g, u, v - 1, u + 3, v + 2, c[0]);
            if (c[1] != 0) q.rect(g, u + (kind == Kind.DRY_BULK ? 1 : 0), v, u + (kind == Kind.DRY_BULK ? 2 : 3), v + 1, c[1]);
        }
    }

    /** ...and on a track across it, the wagons from the buffer back toward the land, the locomotive last. */
    static void trainV(Graphics2D g, Local q, Kind kind, int u, int vTop, int vLow) {
        int[] c = wagon(kind);
        int v = vTop;
        for (; v - 3 >= vLow + 6; v -= 4) {
            q.rect(g, u - 1, v - 3, u + 2, v, c[0]);
            if (c[1] != 0) q.rect(g, u, v - 3 + (kind == Kind.DRY_BULK ? 1 : 0), u + 1, v - (kind == Kind.DRY_BULK ? 1 : 0), c[1]);
        }
        q.rect(g, u - 1, v - 5, u + 2, v, DARK);
    }

    /* ----- 3: the crane ----- */

    /** Where a crane's hook is at phase p of a move, and whether it carries: {v, 1 or 0}. Out to `to` with the load, a pause, back bare. */
    static double[] hook(double p, double from, double to) {
        if (p < 0.42) return new double[] { from + (to - from) * ease(p / 0.42), 1 };
        if (p < 0.50) return new double[] { to, 1 };
        if (p < 0.92) return new double[] { to + (from - to) * ease((p - 0.50) / 0.42), 0 };
        return new double[] { from, 0 };
    }

    /**
     * A terminal's cranes about its boat at time t of the loop (0 to 1, two moves): one at a small berth, two at a large one, the
     * first unloading the ship and the second loading it. a: a light line and the box on it; b: HB1's gantry, standing; c: the gantry
     * walking a plot along the wall between its moves. A tanker's is a loading arm, still.
     */
    static void cranes(Graphics2D g, Docked d, Frame f, char crane, double t, int nth) {
        CityShore.Work w = d.work();
        if (crane == '0' || w.quay() != CityShore.HARBOUR_QUAY_PLOTS) return;     // a pier of its own has no wall to stand on
        Local q = new Local(w, f);
        Ports.Cargo cargo = d.berth().cargo();
        int wall = q.quay * q.px;
        double c = q.uOf(d.boat().x(), d.boat().y()), vShip = q.vOf(d.boat().x(), d.boat().y()), vDrop = q.D - 4;
        if (cargo == Ports.Cargo.LIQUID) {
            for (int k = -1; k <= 1; k += 2) {
                double u = Math.round(c + k * 1.5 * q.px);
                q.rect(g, u, q.D + wall - 4, u + 1, vShip, LIGHT);
                q.rect(g, u - 1, q.D + wall - 6, u + 2, q.D + wall - 3, DARK);
            }
            return;
        }
        int n = q.L >= 12 * q.px ? 2 : 1;
        int load = cargo == Ports.Cargo.DRY_BULK ? TileRaster.ORE_HEAP : ORANGE;
        for (int i = 0; i < n; i++) {
            double base = c + (n == 2 ? (i == 0 ? -2.0 : 2.0) * q.px : 0);
            double tt = (t + nth * 0.37 + i * 0.25) % 1.0 * 2, p = tt % 1;
            int move = (int) tt;
            double u = base;
            if (crane == 'c') {
                double a = base - 0.5 * q.px, b = base + 0.5 * q.px, s = p < 0.55 ? 0 : ease((p - 0.55) / 0.4);
                u = move == 0 ? a + (b - a) * s : b + (a - b) * s;
            }
            u = Math.round(Math.max(8, Math.min(q.L - 9, u)));
            double[] h = i == 0 ? hook(p, vShip, vDrop) : hook(p, vDrop, vShip);
            double v = Math.round(h[0]);
            if (crane == 'a') {
                q.rect(g, u, vDrop - 2, u + 1, vShip + 3, LIGHT);
                q.rect(g, u - 1, vDrop - 3, u + 2, vDrop - 2, TileRaster.STEEL);
                q.rect(g, u - 1, vShip + 3, u + 2, vShip + 4, TileRaster.STEEL);
            } else {
                gantry(g, q, u);
            }
            if (h[1] > 0) {
                q.rect(g, u - 2, v - 2, u + 3, v + 3, DARK);
                q.rect(g, u - 1, v - 1, u + 2, v + 2, load);
            } else {
                q.rect(g, u - 1, v, u + 2, v + 1, TileRaster.STEEL);
            }
        }
    }

    /**
     * One gantry crane seen from above, as HB1's (c) drew it: its portal across the wall - four legs and the beams between them - and
     * its boom, a dark girder out over the water to the ship with a short back-reach over the box.
     */
    static void gantry(Graphics2D g, Local q, double u) {
        int wall = q.quay * q.px, v0 = q.D, bv0 = v0 + 3, bv1 = v0 + wall - 3;
        q.rect(g, u - 7, bv0, u + 8, bv1, DARK);
        q.rect(g, u - 5, bv0 + 2, u + 6, bv1 - 2, TileRaster.QUAY_DECK);
        q.rect(g, u - 7, bv0, u - 4, bv0 + 3, LIGHT);
        q.rect(g, u + 5, bv0, u + 8, bv0 + 3, LIGHT);
        q.rect(g, u - 7, bv1 - 3, u - 4, bv1, LIGHT);
        q.rect(g, u + 5, bv1 - 3, u + 8, bv1, LIGHT);
        q.rect(g, u - 1, v0 - 7, u + 2, v0 + wall + 11, DARK);
        q.rect(g, u - 2, v0 + wall + 8, u + 3, v0 + wall + 11, LIGHT);
    }

    /* ----- the pictures ----- */

    /** One work drawn alone into a scratch picture, for the count and the clock: its yard in the look, or (with d) its boat and cranes. */
    static final BufferedImage SCRATCH = new BufferedImage(48 * PX, 48 * PX, BufferedImage.TYPE_INT_RGB);

    static void one(CityShore.Work w, Look look, double level, boolean train, Docked d, double t) {
        Graphics2D g = SCRATCH.createGraphics();
        Frame f = new Frame(w.x0() - 14, w.y0() - 14, PX);
        Kind kind = kindOf(w);
        if (d == null) { if (kind != null) yard(g, w, f, kind, look, level, train, 0); }
        else if (look == null) boat(g, f, d.boat());
        else cranes(g, d, f, look.crane(), t, 0);
        g.dispose();
    }

    /** What a drawing comes to: {shapes, ms}, the least of 40 rounds after 30. */
    static double[] cost(Runnable draw) {
        for (int i = 0; i < 30; i++) draw.run();
        double best = Double.MAX_VALUE;
        long n = 0;
        for (int i = 0; i < 40; i++) {
            shapes = 0;
            long a = System.nanoTime();
            draw.run();
            best = Math.min(best, (System.nanoTime() - a) / 1e6);
            n = shapes;
        }
        return new double[] { n, best };
    }

    /** A specimen cell at 8 px a plot: the row's terminals side by side on a plain shore, the sea to the north, each with its wall and its ship. */
    static BufferedImage cell(List<BuildingVisual.Type> row, int ch, Look look, double level, boolean train, double t) {
        Frame f = new Frame(0, 0, PX);
        BufferedImage img = new BufferedImage(CW * PX, ch * PX, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        aa(g, false);
        g.setColor(col(SEA));
        g.fillRect(0, 0, CW * PX, 4 * PX);
        g.setColor(col(LAND));
        g.fillRect(0, 4 * PX, CW * PX, (ch - 4) * PX);
        int total = 3 * (row.size() - 1);
        for (BuildingVisual.Type t0 : row) total += BuildingVisual.footprint(t0)[0];
        long x = (CW - total) / 2;
        List<Docked> docked = new ArrayList<>();
        for (int i = 0; i < row.size(); i++) {
            BuildingVisual.Type ty = row.get(i);
            int[] fp = BuildingVisual.footprint(ty);
            Ports.Cargo cargo = Ports.Cargo.values()[ty.berth()];
            CityShore.Work w = new CityShore.Work(x, 6, fp[0], fp[1], ty.id(), 0, CityShore.HARBOUR_QUAY_PLOTS);
            walls(g, f, List.of(new CityShore.Wall(x, 6 - CityShore.HARBOUR_QUAY_PLOTS, fp[0], CityShore.HARBOUR_QUAY_PLOTS, 0)));
            yard(g, w, f, kindOf(w), look, level, train, 2 * PX);
            double[] at = w.berth();
            BoatSchedule.Berth b = new BoatSchedule.Berth(cargo, (long) Math.floor(at[0]), (long) Math.floor(at[1]), 0);
            docked.add(new Docked(w, b, BoatSchedule.alongside(bowOut(b, shipFor(TEMPLATES.get(ty.id()), cargo), i), 0, PX)));
            x += fp[0] + 3;
        }
        for (Docked d : docked) boat(g, f, d.boat());
        for (int i = 0; i < docked.size(); i++) cranes(g, docked.get(i), f, look.crane(), t, i);
        g.dispose();
        return img;
    }

    /** A row's height in plots: 4 of sea, the wall's 2, its deepest box and 3 of land. */
    static int rowPlots(List<BuildingVisual.Type> row) {
        int h = 1;
        for (BuildingVisual.Type t : row) h = Math.max(h, BuildingVisual.footprint(t)[1]);
        return h + 9;
    }

    /** The city's own harbour in a look, at 8 px a plot: the game's tiles, the walls, each yard at a level of its own, the boats, the cranes. */
    static BufferedImage context(BufferedImage base, long fx, long fy, long[] crop, CityShore shore, List<Docked> docked, Look look, double t) {
        BufferedImage img = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.drawImage(base, 0, 0, null);
        Frame f = new Frame(fx, fy, PX);
        walls(g, f, shore.walls());
        List<CityShore.Work> works = shore.works();
        for (int i = 0; i < works.size(); i++) {
            Kind kind = kindOf(works.get(i));
            if (kind != null) yard(g, works.get(i), f, kind, look, level(i), i % 2 == 0, 0);
        }
        for (Docked d : docked) boat(g, f, d.boat());
        for (int i = 0; i < docked.size(); i++) cranes(g, docked.get(i), f, look.crane(), t, i);
        g.dispose();
        return img.getSubimage((int) (crop[0] - fx) * PX, (int) (crop[1] - fy) * PX, (int) crop[2] * PX, (int) crop[3] * PX);
    }

    /** A picture at half its size, each pixel the mean of four: the 8 px drawing as it sits at 4 px a plot. */
    static BufferedImage half(BufferedImage src) {
        int w = src.getWidth() / 2, h = src.getHeight() / 2;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int r = 0, g = 0, b = 0;
            for (int k = 0; k < 4; k++) {
                int c = src.getRGB(2 * x + (k & 1), 2 * y + (k >> 1));
                r += (c >> 16) & 255; g += (c >> 8) & 255; b += c & 255;
            }
            out.setRGB(x, y, (r / 4) << 16 | (g / 4) << 8 | b / 4);
        }
        return out;
    }

    static void text(Graphics2D g, String s, int x, int y, int size, boolean bold, int argb) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, size));
        g.setColor(col(argb));
        g.drawString(s, x, y);
    }

    /** The lines broken at spaces to fit `width` pixels at the size. */
    static List<String> wrapped(List<String> lines, int width, int size) {
        java.awt.FontMetrics fm = SCRATCH.createGraphics().getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, size));
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            StringBuilder sb = new StringBuilder();
            for (String word : line.split(" ")) {
                if (sb.length() > 0 && fm.stringWidth(sb + " " + word) > width) { out.add(sb.toString()); sb.setLength(0); }
                sb.append(sb.length() > 0 ? " " : "").append(word);
            }
            out.add(sb.toString());
        }
        return out;
    }

    /** A picture drawn at `times` its size, pixel for pixel. */
    static void big(Graphics2D g, BufferedImage img, int x, int y, int times) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(img, x, y, img.getWidth() * times, img.getHeight() * times, null);
    }

    /** A row's label: each terminal's name, its box and its ship. */
    static void rowLabel(Graphics2D g, List<BuildingVisual.Type> row, int x, int y) {
        for (BuildingVisual.Type t : row) {
            BuildingsTemplate tpl = TEMPLATES.get(t.id());
            int[] fp = BuildingVisual.footprint(t);
            Ports.Cargo cargo = Ports.Cargo.values()[t.berth()];
            text(g, tpl.getName(), x, y, 13, true, 0xffffffff);
            text(g, fp[0] + " x " + fp[1] + " plots, " + cargo.label().toLowerCase(), x, y + 16, 12, false, 0xffb9c4cf);
            text(g, shipFor(tpl, cargo).label(), x, y + 31, 12, false, 0xffb9c4cf);
            y += 60;
        }
    }

    /**
     * A look's sheet: a column a state (a level, or a train in or not), a row a kind of terminal, the specimens doubled; under them the
     * city's own harbour in the look at 8 px a plot and at 4.
     */
    static BufferedImage sheet(String title, List<String> notes, String[] heads, List<List<BuildingVisual.Type>> rows, Look look, double[] levels,
            boolean[] trains, BufferedImage ctx, String ctxNote) {
        int label = 176, cellW = CW * PX * 2, gap = 6;
        int width = Math.max(label + heads.length * (cellW + gap) + 10, ctx == null ? 0 : 16 + ctx.getWidth() + 16 + ctx.getWidth() / 2 + 16);
        notes = wrapped(notes, width - 28, 13);
        int head = 40 + notes.size() * 18 + 26;
        int height = head;
        for (List<BuildingVisual.Type> row : rows) height += rowPlots(row) * PX * 2 + gap;
        if (ctx != null) height += 44 + ctx.getHeight() + 16;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(col(TileRaster.VOID));
        g.fillRect(0, 0, width, height);
        text(g, title, 14, 28, 19, true, 0xffffffff);
        for (int i = 0; i < notes.size(); i++) text(g, notes.get(i), 14, 50 + i * 18, 13, false, 0xffd6dde4);
        for (int c = 0; c < heads.length; c++) text(g, heads[c], label + c * (cellW + gap) + 4, head - 8, 14, true, 0xffffffff);
        int y = head;
        for (List<BuildingVisual.Type> row : rows) {
            int ch = rowPlots(row);
            rowLabel(g, row, 14, y + 24);
            for (int c = 0; c < heads.length; c++) big(g, cell(row, ch, look, levels[c], trains[c], 0.2), label + c * (cellW + gap), y, 2);
            y += ch * PX * 2 + gap;
        }
        if (ctx != null) {
            text(g, "In a Bergen city at 8 px a plot (" + ctxNote + ")", 16, y + 28, 13, true, 0xffffffff);
            g.drawImage(ctx, 16, y + 40, null);
            text(g, "...and at 4 px a plot, where the detail starts", 16 + ctx.getWidth() + 16, y + 28, 13, true, 0xffffffff);
            g.drawImage(half(ctx), 16 + ctx.getWidth() + 16, y + 40, null);
        }
        g.dispose();
        return img;
    }

    /** The plots of a cell, from the sea in, a crane's pictures show: the ship, the wall, the tracks and the first stacks. */
    static final int QUAY_ROWS = 13;

    static BufferedImage quay(BufferedImage cell) { return cell.getSubimage(0, 0, cell.getWidth(), Math.min(cell.getHeight(), QUAY_ROWS * PX)); }

    /** A frame of a crane look's loop: the container terminal and the small berths doubled, and the container quay at its true 8 px and at 4. */
    static BufferedImage craneFrame(String title, List<List<BuildingVisual.Type>> rows, Look look, double t) {
        List<BuildingVisual.Type> first = rows.get(0), last = rows.get(rows.size() - 1);
        int cellW = CW * PX * 2, h1 = QUAY_ROWS * PX * 2, h2 = rowPlots(last) * PX * 2;
        BufferedImage img = new BufferedImage(8 + cellW + 8 + cellW + 8, 30 + Math.max(h1 + 22 + QUAY_ROWS * PX, h2) + 8, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(col(TileRaster.VOID));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        text(g, title, 10, 21, 15, true, 0xffffffff);
        BufferedImage quay = quay(cell(first, rowPlots(first), look, 0.5, true, t)), b = cell(last, rowPlots(last), look, 0.5, true, t);
        big(g, quay, 8, 30, 2);
        big(g, b, 16 + cellW, 30, 2);
        int y = 30 + h1 + 22;
        text(g, "the map's 8 px a plot", 8, y - 5, 11, false, 0xffd6dde4);
        g.drawImage(quay, 8, y, null);
        text(g, "4 px a plot", 8 + quay.getWidth() + 12, y - 5, 11, false, 0xffd6dde4);
        g.drawImage(half(quay), 8 + quay.getWidth() + 12, y, null);
        g.dispose();
        return img;
    }

    /** The three crane looks as stills: a row a look, four moments of a move of the container terminal's first crane, and the look in the city. */
    static BufferedImage craneStrip(String[][] crane, List<List<BuildingVisual.Type>> rows, BufferedImage base, long fx, long fy, long[] crop, CityShore shore,
            List<Docked> docked) {
        List<BuildingVisual.Type> first = rows.get(0);
        double[] at = { 0.05, 0.12, 0.225, 0.36 };
        String[] heads = { "the box leaves the ship", "half way", "set down on the wagon", "the hook goes back bare" };
        int ch = rowPlots(first), cellW = CW * PX * 2, cellH = QUAY_ROWS * PX * 2, gap = 6, head = 62, band = base == null ? 0 : 44 * PX + 46;
        int width = 14 + at.length * (cellW + gap) + 8;
        BufferedImage img = new BufferedImage(width, head + crane.length * (cellH + 30 + band) + 8, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(col(TileRaster.VOID));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        text(g, "HB2 3 - a crane at the near view: the three looks, as stills", 14, 28, 19, true, 0xffffffff);
        for (int c = 0; c < at.length; c++) text(g, heads[c], 14 + c * (cellW + gap) + 4, head - 8, 14, true, 0xffffffff);
        int y = head;
        for (String[] s : crane) {
            Look look = new Look('0', 'a', s[0].charAt(0));
            text(g, s[2] + ": " + s[3].replace("His words: one", "one"), 14, y + 18, 13, true, 0xffffffff);
            for (int c = 0; c < at.length; c++) big(g, quay(cell(first, ch, look, 0.5, true, at[c])), 14 + c * (cellW + gap), y + 26, 2);
            y += cellH + 30;
            if (base != null) {
                BufferedImage ctx = context(base, fx, fy, crop, shore, docked, look, 0.12);
                text(g, "in a Bergen city, 8 px a plot, a boat at every berth", 14, y + 16, 12, false, 0xffd6dde4);
                g.drawImage(ctx, 14, y + 24, null);
                text(g, "at 4 px a plot", 14 + ctx.getWidth() + 14, y + 16, 12, false, 0xffd6dde4);
                g.drawImage(half(ctx), 14 + ctx.getWidth() + 14, y + 24, null);
                y += band;
            }
        }
        g.dispose();
        return img;
    }

    /* ----- the files ----- */

    static void write(Path dir, String name, BufferedImage img) throws IOException {
        Path out = dir.resolve(name);
        ImageIO.write(img, "png", out.toFile());
        System.out.println(out + "  " + img.getWidth() + " x " + img.getHeight() + "  " + Files.size(out) + " bytes");
    }

    /** The frames as a looping GIF, `cs` hundredths of a second each, on one palette of their 256 commonest colours (the rest to the nearest). */
    static void gif(Path file, List<BufferedImage> frames, int cs) throws IOException {
        Map<Integer, int[]> count = new HashMap<>();
        for (BufferedImage im : frames) {
            for (int y = 0; y < im.getHeight(); y++) for (int x = 0; x < im.getWidth(); x++) count.computeIfAbsent(im.getRGB(x, y) & 0xfcfcfc, k -> new int[1])[0]++;
        }
        List<Map.Entry<Integer, int[]>> byUse = new ArrayList<>(count.entrySet());
        byUse.sort((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0]));
        int n = Math.min(256, byUse.size());
        byte[] r = new byte[256], gr = new byte[256], b = new byte[256];
        int[] pal = new int[n];
        for (int i = 0; i < n; i++) {
            pal[i] = byUse.get(i).getKey();
            r[i] = (byte) (pal[i] >> 16);
            gr[i] = (byte) (pal[i] >> 8);
            b[i] = (byte) pal[i];
        }
        IndexColorModel icm = new IndexColorModel(8, 256, r, gr, b);
        Map<Integer, Integer> index = new HashMap<>();
        for (int i = 0; i < n; i++) index.put(pal[i], i);
        ImageWriter wr = ImageIO.getImageWritersBySuffix("gif").next();
        Files.deleteIfExists(file);
        try (ImageOutputStream os = ImageIO.createImageOutputStream(file.toFile())) {
            wr.setOutput(os);
            wr.prepareWriteSequence(null);
            for (BufferedImage im : frames) {
                BufferedImage ix = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, icm);
                for (int y = 0; y < im.getHeight(); y++) for (int x = 0; x < im.getWidth(); x++) {
                    int c = im.getRGB(x, y) & 0xfcfcfc;
                    Integer at = index.get(c);
                    if (at == null) {
                        int best = 0;
                        long least = Long.MAX_VALUE;
                        for (int i = 0; i < n; i++) {
                            long dr = ((pal[i] >> 16) & 255) - ((c >> 16) & 255), dg = ((pal[i] >> 8) & 255) - ((c >> 8) & 255), db = (pal[i] & 255) - (c & 255);
                            long dist = dr * dr + dg * dg + db * db;
                            if (dist < least) { least = dist; best = i; }
                        }
                        at = best;
                        index.put(c, at);
                    }
                    ix.getRaster().setSample(x, y, 0, at);
                }
                IIOMetadata md = wr.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(ix), null);
                String fmt = md.getNativeMetadataFormatName();
                IIOMetadataNode root = (IIOMetadataNode) md.getAsTree(fmt);
                IIOMetadataNode gce = child(root, "GraphicControlExtension");
                gce.setAttribute("disposalMethod", "none");
                gce.setAttribute("userInputFlag", "FALSE");
                gce.setAttribute("transparentColorFlag", "FALSE");
                gce.setAttribute("delayTime", Integer.toString(cs));
                gce.setAttribute("transparentColorIndex", "0");
                IIOMetadataNode app = new IIOMetadataNode("ApplicationExtension");
                app.setAttribute("applicationID", "NETSCAPE");
                app.setAttribute("authenticationCode", "2.0");
                app.setUserObject(new byte[] { 1, 0, 0 });
                child(root, "ApplicationExtensions").appendChild(app);
                /* The palette goes in by hand: left to the writer, the frame's indices were written against the default web palette
                   (the stream's own table) and every colour came out blue and green. */
                child(root, "ImageDescriptor").setAttribute("interlaceFlag", "FALSE");
                IIOMetadataNode table = child(root, "LocalColorTable");
                while (table.hasChildNodes()) table.removeChild(table.getFirstChild());
                table.setAttribute("sizeOfLocalColorTable", "256");
                table.setAttribute("sortFlag", "FALSE");
                for (int i = 0; i < 256; i++) {
                    IIOMetadataNode entry = new IIOMetadataNode("ColorTableEntry");
                    entry.setAttribute("index", Integer.toString(i));
                    entry.setAttribute("red", Integer.toString(r[i] & 255));
                    entry.setAttribute("green", Integer.toString(gr[i] & 255));
                    entry.setAttribute("blue", Integer.toString(b[i] & 255));
                    table.appendChild(entry);
                }
                md.setFromTree(fmt, root);
                wr.writeToSequence(new IIOImage(ix, null, md), null);
            }
            wr.endWriteSequence();
        }
        wr.dispose();
    }

    static IIOMetadataNode child(IIOMetadataNode root, String name) {
        for (int i = 0; i < root.getLength(); i++) if (root.item(i).getNodeName().equals(name)) return (IIOMetadataNode) root.item(i);
        IIOMetadataNode node = new IIOMetadataNode(name);
        root.appendChild(node);
        return node;
    }
}
