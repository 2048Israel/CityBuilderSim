package ham.citybuildersim.tools;

import ham.citybuildersim.BoatSchedule;
import ham.citybuildersim.BuildingCatalog;
import ham.citybuildersim.BuildingVisual;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.CityLand;
import ham.citybuildersim.CityMap;
import ham.citybuildersim.CityShore;
import ham.citybuildersim.EarthData;
import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.Ports;
import ham.citybuildersim.ShipShapes;
import ham.citybuildersim.TilePainter;
import ham.citybuildersim.TileRaster;
import ham.citybuildersim.World;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Renders a Bergen city's harbour for review, headless: HB1's placement before and after, and three ways to draw a terminal's box
 * under the quay walls and the docked boats HB1 draws from the detail zoom, by the game's own painters.
 *
 *     java -cp "target/classes;<gson.jar>" ham.citybuildersim.tools.HarbourRenders [outDir]
 *
 * outDir is docs/queue/HB1 unless named. The earth file is -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); -Drun.months=N
 * plays each city N months (2,400 unless named). Two cities are founded at Bergen (LongPlaytest.earthCity(), each in a
 * GameFiles.scratch() folder deleted when the JVM exits - never the real saves), the first with CityMap.bundleHarbour false (MF1's
 * order), the second with it true (HB1's), and each map is read tile by tile through CityMap.tileInput(), TilePainter.paint() and
 * TileRaster.raster(), as the screen reads it. It writes:
 *
 *   hb1-0-mf1.png        the city's largest harbour, MF1's order, at 2 px a plot: the window round all its shore works plus 2 tiles
 *                        of margin, at most 2,048 px a side
 *   hb1-0-bundled.png    the same, HB1's order
 *   hb1-a-boxes.png      from the bundled city, the window of up to 6 x 4 tiles (1,536 x 1,024 px, 8 px a plot) holding the most
 *                        shore works: the game's tiles, the quay walls (CityShore.walls()), a boat docked alongside at each berth
 *                        (laid by BoatSchedule.berthed(): alongside where it fits, else stern-to, else as the schedule places it); each terminal's box as the game draws it now
 *   hb1-b-yard.png       ...the same window, each box an open cargo yard by what its terminal handles: rows of boxes, heaps,
 *                        round tanks, crates and a shed
 *   hb1-c-cranes.png     ...the same window, each terminal's box with a long transit shed set back from the wall and, on the
 *                        wall, 2 or 3 quay cranes with their booms out over the ship
 *   hb1-x-4px.png        an extra: the same window as a, at 4 px a plot (just past MapFrame.DETAIL_FROM; boats are true size there since HB1 slice E)
 *   hb1-d-kinds.png      an extra: a specimen sheet, every kind of terminal and a tank farm (a row each) in the drawings a, b and c (a
 *                        column each) on a plain shore, for the kinds the Bergen window lacks
 *   README.md            one line a picture, the runs (months, seconds, works a harbour) and what the pictures show of HB1's drawing
 *
 * WHY (HB1): the harbour's look is Jerus's call, put to him with renders. Not a harness: nothing here asserts, and nothing under
 * docs/ but its outDir is written. CityMap.bundleHarbour is a public test switch, set for each city's play and put back.
 */
public final class HarbourRenders {

    private HarbourRenders() { }

    /** Pixels a plot in the detail pictures, and their window in tiles: 8 x 32 x 6 by 8 x 32 x 4, 1,536 x 1,024. */
    private static final int PX = 8, ACROSS = 6, DOWN = 4;

    /** Pixels a plot in the harbour pictures, the margin round the works in tiles, and the most tiles a side: 2 x 32 x 32 = 2,048 px. */
    private static final int HARBOUR_PX = 2, MARGIN_TILES = 2, TILES_MOST = 32;

    /** How far past a box's sea side, in plots, a window must hold a work for it to count whole: its quay and the boat alongside. */
    private static final int SEAWARD = 10;

    /** North, east, south, west: CityShore.DX and DY, which are package-private. */
    private static final int[] DX = { 0, 1, 0, -1 }, DY = { -1, 0, 1, 0 };

    /** What a terminal's box is drawn as: a kind of cargo yard. */
    private enum Kind { CONTAINER, DRY_BULK, LIQUID, GENERAL, TANKS }

    /** A world, a window on it: the plot at its top left and the pixels a plot. */
    private record Frame(long x0, long y0, int px) { }

    /** A harbour as CityShore.harbours() makes it: its first work's shore plot, its landmass and its works. */
    private static final class Harbour {
        long x, y;
        int landmass;
        final List<CityShore.Work> works = new ArrayList<>();
    }

    /** A world plot. */
    private record Plot(long x, long y) { }

    /** The places BoatSchedule.berthed() gives a docked boat: alongside its quay, stern-to it, or as the schedule placed it, centred on the berth point. */
    private static final int ALONGSIDE = 0, STERN_TO = 1, AS_PLACED = 2;

    /** A berth, the work it belongs to, the boat docked at it as it is drawn, and its place (ALONGSIDE, STERN_TO or AS_PLACED). */
    private record Docked(CityShore.Work work, BoatSchedule.Berth berth, BoatSchedule.Boat boat, int place) {
        Docked(CityShore.Work work, BoatSchedule.Berth berth, BoatSchedule.Boat boat) { this(work, berth, boat, ALONGSIDE); }

        /** Whether it lies alongside or stern-to, not as the schedule placed it. */
        boolean laid() { return place != AS_PLACED; }

        String name() { return place == ALONGSIDE ? "alongside" : place == STERN_TO ? "stern-to" : "as placed"; }
    }

    /** What one city's run came to: for the README. */
    private record Run(String label, int months, long seconds, List<Harbour> harbours, String overview, List<String> audit) { }

    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "true");
        Path file = EarthData.locate();
        if (file == null || !Files.isRegularFile(file)) {
            System.out.println("no earth-2 file: -Dcbs.earth=<path>, or put it at " + EarthData.DATA_FILE);
            return;
        }
        System.setProperty(EarthData.PROPERTY, file.toString());
        Path outDir = args.length > 0 ? Path.of(args[0]) : Path.of("docs", "queue", "HB1");
        Files.createDirectories(outDir);
        int months = Integer.getInteger("run.months", 2400);
        PrintStream silent = new PrintStream(new OutputStream() { @Override public void write(int x) { } });
        Map<Integer, BuildingsTemplate> templates = new HashMap<>();
        for (BuildingsTemplate t : new BuildingCatalog().load()) templates.put(t.getId(), t);

        // Two cities, one JVM: MF1's order first, then HB1's. The switch is set for the whole of each city's play and its picture.
        Run mf1;
        Run bundled;
        Game g;
        try {
            CityMap.bundleHarbour = false;
            Game first = play("mf1", months, silent);
            mf1 = harbourPicture(first, "mf1", outDir, "hb1-0-mf1.png", templates);
            first = null;
            CityMap.bundleHarbour = true;
            g = play("bundled", months, silent);
            bundled = harbourPicture(g, "bundled", outDir, "hb1-0-bundled.png", templates);
        } finally {
            CityMap.bundleHarbour = true;
        }
        CityMap map = g.getCityMap();
        BuildingVisual.Type[] types = map.types();
        CityShore shore = map.shore();

        // The detail window: whole tiles, up to ACROSS x DOWN, with the most shore works.
        long[] at = bestWindow(shore.works());
        List<String> readme = new ArrayList<>();
        readme.add("# HB1 harbour renders");
        readme.add("");
        readme.add("Written by `ham.citybuildersim.tools.HarbourRenders`: a Bergen city on the real Earth (earth-2) played twice in one JVM, once with"
                + " `CityMap.bundleHarbour` false (MF1's order) and once true (HB1's), drawn by the game's own painters. Not a harness; nothing in it asserts.");
        readme.add("");
        readme.add("## The pictures");
        readme.add("");
        readme.add("- `hb1-0-mf1.png` - the MF1 city's largest harbour at 2 px a plot: the window round all its shore works plus 2 tiles of margin" + mf1.overview);
        readme.add("- `hb1-0-bundled.png` - the same for the HB1 city" + bundled.overview);
        List<String> typeLines = new ArrayList<>();
        BufferedImage sheet = kindsSheet(map, templates, typeLines);
        if (sheet != null) {
            write(outDir, "hb1-d-kinds.png", sheet);
            readme.add("- `hb1-d-kinds.png` - an extra, not asked for, because a Bergen city may hold no terminal of some kind: a specimen sheet of every kind of terminal (a row each, with a tank farm), on a plain straight shore with the sea to the north, each at its real box size, with its quay wall and boat; column a the box as the game draws it, b the yard, c the shed and cranes. The sea and the land are plain, not the game's tiles. "
                    + String.join("; ", typeLines) + ".");
        }
        if (at == null) {
            readme.add("- no detail pictures: the bundled city holds no shore work.");
        } else {
            int whole = 0, part = 0;
            for (CityShore.Work w : shore.works()) {
                if (inside(w, at[0], at[1], SEAWARD)) whole++;
                if (meets(w, at[0], at[1])) part++;
            }
            List<Docked> docked = docked(g, map, templates, PX);
            long wx0 = at[0] * World.TILE, wy0 = at[1] * World.TILE;
            System.out.printf("detail window: tiles (%d, %d) to (%d, %d), %d works wholly in it, %d touching it, %d boats docked in the city%n",
                    at[0], at[1], at[0] + ACROSS - 1, at[1] + DOWN - 1, whole, part, docked.size());
            BufferedImage base = tiles(map, at[0], at[1], ACROSS, DOWN, PX);
            String where = String.format("the window of %d x %d tiles (%,d x %,d px, 8 px a plot) from tile (%d, %d) with the most shore works: %d wholly in it and %d touching it",
                    ACROSS, DOWN, ACROSS * World.TILE * PX, DOWN * World.TILE * PX, at[0], at[1], whole, part);
            for (char v : new char[] { 'a', 'b', 'c' }) {
                BufferedImage img = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D gr = img.createGraphics();
                gr.drawImage(base, 0, 0, null);
                paint(gr, new Frame(wx0, wy0, PX), shore, types, docked, v);
                gr.dispose();
                String name = v == 'a' ? "hb1-a-boxes.png" : v == 'b' ? "hb1-b-yard.png" : "hb1-c-cranes.png";
                write(outDir, name, img);
            }
            // The same window just past DETAIL_FROM, where boats are true size too since slice E: the rule at the other end of the zoom.
            int near = 4;
            BufferedImage nearImg = tiles(map, at[0], at[1], ACROSS, DOWN, near);
            Graphics2D gb = nearImg.createGraphics();
            paint(gb, new Frame(wx0, wy0, near), shore, types, docked(g, map, templates, near), 'a');
            gb.dispose();
            write(outDir, "hb1-x-4px.png", nearImg);
            readme.add("- `hb1-a-boxes.png` - " + where + ". The game's tiles, the quay walls (QUAY_DECK, edged QUAY_EDGE) and a boat docked at each berth, laid by BoatSchedule.berthed() (alongside its quay where it fits, else stern-to it, else as the schedule places it, centred on its berth), true size, each terminal's box as the game draws it now.");
            readme.add("- `hb1-b-yard.png` - the same window; each terminal's box painted as an open cargo yard by what it handles: container terminals rows of small coloured boxes (ShipShapes.boxColour's six), dry bulk two or three heaps in ochre or grey, general cargo stacked crates and one shed, a tank farm or reserve round tanks.");
            readme.add("- `hb1-c-cranes.png` - the same window; each terminal's box under a long transit shed set back from the wall, and on the wall 2 or 3 quay cranes (a dark gantry on the wall, its boom out over the ship); a tank farm has no quay and is drawn with its tanks as in b. A crane's boom reaches about a plot past the wall over the water.");
            readme.add("- `hb1-x-4px.png` - an extra, not asked for: the same window as a, at 4 px a plot (768 x 512), just past MapFrame.DETAIL_FROM, where boats are true size too (ShipShapes.TRUE_FROM is DETAIL_FROM since slice E; they were drawn 1.6 times true there before) and are laid by BoatSchedule.berthed() as at 8; the game's tiles, walls and boats, boxes as the game draws them.");
            readme.add("");
            String bulkNow = Ports.bulkShipsStanding(g.getBuildingManager()).get(0).label();
            readme.add("The boat at each berth is the ship the game's schedule would bring there, laden, coming in, every berth occupied at once (the crowded case, more than the game's months show): at a dry bulk berth the first of Ports.bulkShipsStanding() (a " + bulkNow + " in this city), at any other the first class of its terminal's Ports.shipsOf() (a Container Terminal a feeder, a General Cargo Terminal a general cargo ship). The boats are laid in the order of their made-up arrivals, one after another.");
            readme.add("");
            readme.add("## What the drawing shows (HB1's own: walls, boats alongside)");
            readme.add("");
            for (String line : bundled.audit) readme.add("- the HB1 city, " + line);
            readme.add("- the MF1 city, for comparison, " + mf1.audit.get(0));
        }
        readme.add("");
        readme.add("## The runs");
        readme.add("");
        for (Run r : new Run[] { mf1, bundled }) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < r.harbours.size(); i++) {
                Harbour h = r.harbours.get(i);
                sb.append(i == 0 ? "" : ", ").append(h.works.size());
            }
            int all = 0;
            for (Harbour h : r.harbours) all += h.works.size();
            readme.add(String.format("- %s: Bergen played %d months in %d s (asked for %d); %d shore works in %d harbour%s: %s%s.", r.label, r.months, r.seconds, months, all,
                    r.harbours.size(), r.harbours.size() == 1 ? "" : "s", sb, r.harbours.isEmpty() ? "none" : " works (the largest drawn)"));
        }
        Files.writeString(outDir.resolve("README.md"), String.join("\n", readme) + "\n", StandardCharsets.UTF_8);
        System.out.println(outDir.resolve("README.md") + "  " + Files.size(outDir.resolve("README.md")) + " bytes");
        System.exit(0);
    }

    /* ----- the cities ----- */

    /** The seconds each city took to play, by its label: one number in an array. */
    private static final Map<String, long[]> TIMES = new HashMap<>();

    /** A Bergen city played `months` months in a scratch folder. */
    private static Game play(String label, int months, PrintStream silent) {
        long t0 = System.currentTimeMillis();
        Game g = LongPlaytest.earthCity("bergen", months, GameFiles.scratch("harbourrenders-bergen-" + label), silent);
        long s = (System.currentTimeMillis() - t0) / 1000;
        TIMES.put(label, new long[] { s });
        System.out.printf("a Bergen city (%s) played %d months in %d s%n", label, g.getMonth(), s);
        return g;
    }

    /** The city's harbours, its largest drawn at 2 px a plot to `name`, and what its drawing shows. */
    private static Run harbourPicture(Game g, String label, Path outDir, String name, Map<Integer, BuildingsTemplate> templates) throws IOException {
        int played = g.getMonth();
        CityMap map = g.getCityMap();
        CityLand land = g.getCityLand();
        List<Harbour> hs = harbours(map.shore(), World.of(land.key()));
        for (int i = 0; i < hs.size(); i++) {
            Harbour h = hs.get(i);
            int berthed = 0;
            for (CityShore.Work w : h.works) if (w.berthed()) berthed++;
            System.out.printf("%s: harbour %d at shore plot (%d, %d), landmass %d: %d works, %d with a quay; %s; %s%n", label, i + 1, h.x, h.y, h.landmass, h.works.size(), berthed,
                    spacing(h), kinds(h, templates));
            for (CityShore.Work w : h.works) System.out.printf("    work at (%d, %d) %d x %d, sea on side %d, type %d%n", w.x0(), w.y0(), w.w(), w.h(), w.side(), w.type());
        }
        List<String> audit = new ArrayList<>(audit(g, map, templates, PX));
        audit.addAll(audit(g, map, templates, 4.0));
        String overview;
        if (hs.isEmpty()) {
            overview = " - the city holds no shore work, so there is no picture";
            System.out.println(label + ": no shore work, no overview");
            return new Run(label, played, TIMES.get(label)[0], hs, overview, audit);
        }
        Harbour big = hs.get(0);
        for (Harbour h : hs) if (h.works.size() > big.works.size()) big = h;
        long minX = Long.MAX_VALUE, minY = Long.MAX_VALUE, maxX = Long.MIN_VALUE, maxY = Long.MIN_VALUE;
        for (CityShore.Work w : big.works) {
            minX = Math.min(minX, w.x0());
            minY = Math.min(minY, w.y0());
            maxX = Math.max(maxX, w.x0() + w.w() - 1);
            maxY = Math.max(maxY, w.y0() + w.h() - 1);
        }
        long tx0 = Math.floorDiv(minX, (long) World.TILE) - MARGIN_TILES, tx1 = Math.floorDiv(maxX, (long) World.TILE) + MARGIN_TILES;
        long ty0 = Math.floorDiv(minY, (long) World.TILE) - MARGIN_TILES, ty1 = Math.floorDiv(maxY, (long) World.TILE) + MARGIN_TILES;
        boolean cut = tx1 - tx0 + 1 > TILES_MOST || ty1 - ty0 + 1 > TILES_MOST;
        if (tx1 - tx0 + 1 > TILES_MOST) { long mid = (tx0 + tx1) / 2; tx0 = mid - TILES_MOST / 2; tx1 = tx0 + TILES_MOST - 1; }
        if (ty1 - ty0 + 1 > TILES_MOST) { long mid = (ty0 + ty1) / 2; ty0 = mid - TILES_MOST / 2; ty1 = ty0 + TILES_MOST - 1; }
        int across = (int) (tx1 - tx0 + 1), down = (int) (ty1 - ty0 + 1);
        write(outDir, name, tiles(map, tx0, ty0, across, down, HARBOUR_PX));
        int berthed = 0;
        for (CityShore.Work w : big.works) if (w.berthed()) berthed++;
        overview = String.format(": harbour of %d shore works (%d with a quay; %s) over %d x %d plots, %s, tiles (%d, %d) to (%d, %d), %d x %d px%s",
                big.works.size(), berthed, kinds(big, templates), maxX - minX + 1, maxY - minY + 1, spacing(big), tx0, ty0, tx1, ty1,
                across * World.TILE * HARBOUR_PX, down * World.TILE * HARBOUR_PX, cut ? " (cut to 32 tiles a side, round the middle of its works)" : "");
        return new Run(label, played, TIMES.get(label)[0], hs, overview, audit);
    }

    /** How near a harbour's works lie: the distance from each work's shore plot to its nearest neighbour's, in plots, mean and greatest. */
    private static String spacing(Harbour h) {
        if (h.works.size() < 2) return "a single work";
        double sum = 0, most = 0;
        for (CityShore.Work a : h.works) {
            double nearest = Double.MAX_VALUE;
            long[] p = a.shorePlot();
            for (CityShore.Work b : h.works) {
                if (b == a) continue;
                long[] q = b.shorePlot();
                nearest = Math.min(nearest, Math.hypot(p[0] - q[0], p[1] - q[1]));
            }
            sum += nearest;
            most = Math.max(most, nearest);
        }
        return String.format("nearest neighbour %.1f plots mean, %.1f greatest", sum / h.works.size(), most);
    }

    /** A harbour's works by the name of their building, in the order first laid: "6 Bulk Berth, 3 General Cargo Terminal". */
    private static String kinds(Harbour h, Map<Integer, BuildingsTemplate> templates) {
        Map<String, Integer> n = new java.util.LinkedHashMap<>();
        for (CityShore.Work w : h.works) {
            BuildingsTemplate t = templates.get(w.type());
            n.merge(t == null ? "type " + w.type() : t.getName(), 1, Integer::sum);
        }
        List<String> out = new ArrayList<>();
        for (Map.Entry<String, Integer> e : n.entrySet()) out.add(e.getValue() + " " + e.getKey());
        return String.join(", ", out);
    }

    /** CityShore.harbours() asked of what it returns: each work, in the order laid, in the first harbour on its landmass within the reach of that harbour's first work. */
    private static List<Harbour> harbours(CityShore shore, World world) {
        long reachSq = (long) CityShore.HARBOUR_REACH * CityShore.HARBOUR_REACH;
        List<Harbour> open = new ArrayList<>();
        for (CityShore.Work w : shore.works()) {
            long[] s = w.shorePlot();
            int lm = world.landmassAt(s[0], s[1]);
            Harbour in = null;
            for (Harbour h : open) {
                if (h.landmass != lm) continue;
                long dx = s[0] - h.x, dy = s[1] - h.y;
                if (dx * dx + dy * dy <= reachSq) { in = h; break; }
            }
            if (in == null) { in = new Harbour(); in.x = s[0]; in.y = s[1]; in.landmass = lm; open.add(in); }
            in.works.add(w);
        }
        return open;
    }

    /* ----- the window ----- */

    private static long tileOf(long plot) { return Math.floorDiv(plot, (long) World.TILE); }

    /** Whether the work's box, and `seaward` plots past its sea side, lie wholly in the window of ACROSS x DOWN tiles from tile (tx, ty). */
    private static boolean inside(CityShore.Work w, long tx, long ty, int seaward) {
        long x0 = w.x0(), y0 = w.y0(), x1 = w.x0() + w.w(), y1 = w.y0() + w.h();
        if (w.side() == 0) y0 -= seaward; else if (w.side() == 1) x1 += seaward; else if (w.side() == 2) y1 += seaward; else x0 -= seaward;
        return x0 >= tx * World.TILE && y0 >= ty * World.TILE && x1 <= (tx + ACROSS) * World.TILE && y1 <= (ty + DOWN) * World.TILE;
    }

    /** Whether the work's box meets the window. */
    private static boolean meets(CityShore.Work w, long tx, long ty) {
        return w.x0() < (tx + ACROSS) * World.TILE && w.x0() + w.w() > tx * World.TILE && w.y0() < (ty + DOWN) * World.TILE && w.y0() + w.h() > ty * World.TILE;
    }

    /**
     * The top-left tile of the window of ACROSS x DOWN tiles with the most works wholly in it (box and seaward run), then the most touching
     * it, then the middle of its whole works nearest its own middle; null with no work.
     */
    private static long[] bestWindow(List<CityShore.Work> works) {
        long best = -1;
        double bestOff = Double.MAX_VALUE;
        long[] at = null;
        for (CityShore.Work w : works) {
            for (int ox = 0; ox < ACROSS; ox++) {
                for (int oy = 0; oy < DOWN; oy++) {
                    long tx = tileOf(w.x0()) - ox, ty = tileOf(w.y0()) - oy;
                    long score = 0;
                    double cx = 0, cy = 0;
                    int n = 0;
                    for (CityShore.Work k : works) {
                        boolean whole = inside(k, tx, ty, SEAWARD);
                        score += (whole ? 1000 : 0) + (meets(k, tx, ty) ? 1 : 0);
                        if (whole) { cx += k.x0() + k.w() / 2.0; cy += k.y0() + k.h() / 2.0; n++; }
                    }
                    double off = n == 0 ? 0 : Math.hypot(cx / n - (tx + ACROSS / 2.0) * World.TILE, cy / n - (ty + DOWN / 2.0) * World.TILE);
                    if (score > best || (score == best && off < bestOff)) { best = score; bestOff = off; at = new long[] { tx, ty }; }
                }
            }
        }
        return at;
    }

    /** A window of across x down tiles from tile (tx0, ty0), as the screen draws it at px a plot. */
    private static BufferedImage tiles(CityMap map, long tx0, long ty0, int across, int down, int px) {
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

    /* ----- the berths and their boats ----- */

    /** The ship a terminal's boats are: its first class (Ports.shipsOf()), else the first of its cargo. */
    private static Ports.Ship shipFor(BuildingsTemplate t, Ports.Cargo cargo) {
        List<Ports.Ship> ships = t == null ? List.of() : Ports.shipsOf(t);
        if (!ships.isEmpty()) return ships.get(0);
        for (Ports.Ship s : Ports.Ship.values()) if (s.cargo() == cargo) return s;
        throw new IllegalStateException("no ship for " + cargo);
    }

    /**
     * The city's berths in CityShore.berths(types)'s order, each with its work (the berthed works whose type has a berth, in the order
     * laid) and its boat: a laden call of the ship the game's schedule would bring (a dry bulk berth's the first of Ports.bulkShipsStanding(),
     * another cargo's the first class of its terminal's Ports.shipsOf()), docked at the berth point, every berth occupied at once, the arrivals
     * made up in the berths' order and the boats laid at `scale` by BoatSchedule.berthed() on the city's shore.
     */
    private static List<Docked> docked(Game g, CityMap map, Map<Integer, BuildingsTemplate> templates, double scale) {
        BuildingVisual.Type[] types = map.types();
        CityShore shore = map.shore();
        List<BoatSchedule.Berth> berths = shore.berths(types);
        List<CityShore.Work> mine = new ArrayList<>();
        for (CityShore.Work w : shore.works()) {
            if (w.berthed() && w.type() >= 0 && w.type() < types.length && types[w.type()] != null && types[w.type()].berth() >= 0) mine.add(w);
        }
        if (mine.size() != berths.size()) throw new IllegalStateException(mine.size() + " berthed works but " + berths.size() + " berths");
        Ports.Ship bulk = Ports.bulkShipsStanding(g.getBuildingManager()).get(0);
        List<BoatSchedule.Route> routes = new ArrayList<>();
        List<BoatSchedule.Boat> bowOut = new ArrayList<>();
        for (int i = 0; i < berths.size(); i++) {
            BoatSchedule.Berth b = berths.get(i);
            CityShore.Work w = mine.get(i);
            double[] at = w.berth();
            if ((long) Math.floor(at[0]) != b.x() || (long) Math.floor(at[1]) != b.y()) System.out.println("warning: berth " + i + " is not its work's berth point");
            Ports.Ship ship = b.cargo() == Ports.Cargo.DRY_BULK ? bulk : shipFor(templates.get(w.type()), b.cargo());
            routes.add(new BoatSchedule.Route(b, b.x(), b.y()));
            bowOut.add(bowOut(b, ship, i));
        }
        List<BoatSchedule.Boat> laid = BoatSchedule.berthed(bowOut, routes, scale, shore);
        List<Docked> out = new ArrayList<>();
        for (int i = 0; i < berths.size(); i++) {
            BoatSchedule.Boat l = laid.get(i), raw = bowOut.get(i);
            int place = l.equals(raw) ? AS_PLACED : l.equals(BoatSchedule.alongside(raw, berths.get(i).side(), scale)) ? ALONGSIDE : STERN_TO;
            out.add(new Docked(mine.get(i), berths.get(i), l, place));
        }
        return out;
    }

    /** A laden call of `ship` coming in on route i, docked at the berth's point (+0.5, +0.5) as the schedule places it, bow out (heading out to sea, the way the berth's side points); i varies a box ship's boxes' colours and is its arrival's order. */
    private static BoatSchedule.Boat bowOut(BoatSchedule.Berth b, Ports.Ship ship, int i) {
        BoatSchedule.Call call = new BoatSchedule.Call(b.cargo(), ship, true, i, i * 0.173);
        double heading = b.side() >= 0 ? Math.atan2(DY[b.side()], DX[b.side()]) : 0;
        return new BoatSchedule.Boat(call, b.x() + 0.5, b.y() + 0.5, true, true, heading, 1);
    }

    /** ...and a lone boat of the specimen sheet put alongside at `scale` (BoatSchedule.alongside(): what berthed() gives a boat with nothing near it). */
    private static BoatSchedule.Boat dock(BoatSchedule.Berth b, Ports.Ship ship, int i, double scale) {
        return BoatSchedule.alongside(bowOut(b, ship, i), b.side(), scale);
    }

    /* ----- the drawing ----- */

    private static Color col(int argb) { return new Color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255); }

    private static Color col(int argb, double alpha) {
        return new Color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, (int) Math.round(255 * Math.max(0, Math.min(1, alpha))));
    }

    /** t of b over a, per channel. */
    private static int mix(int a, int b, double t) {
        int r = (int) Math.round(((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int g = (int) Math.round(((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int bl = (int) Math.round((a & 255) * (1 - t) + (b & 255) * t);
        return 0xff000000 | r << 16 | g << 8 | bl;
    }

    private static void aa(Graphics2D g, boolean on) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, on ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /** The window's overlay: the quay walls, then by variant (a: nothing; b: the yards; c: the sheds), the boats, and for c the cranes. */
    private static void paint(Graphics2D g, Frame f, CityShore shore, BuildingVisual.Type[] types, List<Docked> docked, char variant) {
        walls(g, f, shore.walls());
        if (variant == 'b') {
            for (CityShore.Work k : shore.works()) {
                Kind kind = kindOf(k, types);
                if (kind != null) yard(g, k, f, kind);
            }
        }
        if (variant == 'c') {
            for (CityShore.Work k : shore.works()) {
                Kind kind = kindOf(k, types);
                if (kind == Kind.TANKS) yard(g, k, f, kind);
                else if (kind != null && k.berthed()) shed(g, new Local(k, f));
            }
        }
        for (Docked d : docked) boat(g, f, d.boat(), f.px());
        if (variant == 'c') {
            for (Docked d : docked) cranes(g, d, f);
        }
    }

    /** The harbour's quays as MapView.harbourQuay() draws them: the walls filled QUAY_DECK and, from TileRaster.EDGE_FROM px a plot, edged QUAY_EDGE. */
    private static void walls(Graphics2D g, Frame f, List<CityShore.Wall> walls) {
        aa(g, false);
        g.setColor(col(TileRaster.QUAY_DECK));
        for (CityShore.Wall w : walls) g.fillRect((int) ((w.x0() - f.x0) * f.px), (int) ((w.y0() - f.y0) * f.px), w.w() * f.px, w.h() * f.px);
        if (f.px < TileRaster.EDGE_FROM) return;
        g.setColor(col(TileRaster.QUAY_EDGE));
        for (CityShore.Wall w : walls) g.drawRect((int) ((w.x0() - f.x0) * f.px), (int) ((w.y0() - f.y0) * f.px), w.w() * f.px - 1, w.h() * f.px - 1);
    }

    /** A boat as MapView.boats() draws it above LEAST_PX: hull, deck marks (a box ship's boxes in their colours), bridge, and the hull's outline. */
    private static void boat(Graphics2D g, Frame f, BoatSchedule.Boat b, double scale) {
        aa(g, true);
        int c = ShipShapes.colour(b);
        g.setColor(col(b.loaded() ? ShipShapes.HULL_LADEN : ShipShapes.HULL_EMPTY, b.alpha()));
        double[][] hull = ShipShapes.hull(b, scale);
        g.fill(poly(f, hull));
        double[][][] deck = ShipShapes.deck(b, scale);
        boolean boxes = b.call().cargo() == Ports.Cargo.CONTAINER;
        for (int k = 0; k < deck.length; k++) {
            g.setColor(boxes ? col(ShipShapes.boxColour(b, k), b.alpha()) : col(c, 0.6 * b.alpha()));
            g.fill(poly(f, deck[k]));
        }
        g.setColor(col(ShipShapes.BRIDGE, 0.85 * b.alpha()));
        g.fill(poly(f, ShipShapes.bridge(b, scale)));
        g.setColor(col(c, b.alpha()));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(poly(f, hull));
    }

    private static Path2D poly(Frame f, double[][] pts) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < pts[0].length; i++) {
            double x = (pts[0][i] - f.x0) * f.px, y = (pts[1][i] - f.y0) * f.px;
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        return p;
    }

    /**
     * A work's box in its own frame, in pixels: u along the sea side (left to right as the sea is faced), v from the land side (0) out to the
     * sea side (D). X() and Y() turn a point of it to the picture, so a yard is drawn once for the four sides the sea can be on.
     */
    private static final class Local {
        final int px, ax, ay, dx, dy, L, D, quay;
        final double bx, by, ox, oy;
        final long x0, y0;

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
            x0 = f.x0();
            y0 = f.y0();
            bx = (wx - f.x0()) * px;
            by = (wy - f.y0()) * px;
        }

        double X(double u, double v) { return bx + ax * u + dx * v; }

        double Y(double u, double v) { return by + ay * u + dy * v; }

        /** The u of a point given in world plots. */
        double uOf(double wx, double wy) { return ((wx - ox) * ax + (wy - oy) * ay) * px; }

        void rect(Graphics2D g, double u0, double v0, double u1, double v1, int argb) {
            Path2D.Double p = new Path2D.Double();
            p.moveTo(X(u0, v0), Y(u0, v0));
            p.lineTo(X(u1, v0), Y(u1, v0));
            p.lineTo(X(u1, v1), Y(u1, v1));
            p.lineTo(X(u0, v1), Y(u0, v1));
            p.closePath();
            aa(g, false);
            g.setColor(col(argb));
            g.fill(p);
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
        }
    }

    /** What a work's box is drawn as, or null for a work this tool leaves as it is. */
    private static Kind kindOf(CityShore.Work w, BuildingVisual.Type[] types) {
        if (w.type() < 0 || w.type() >= types.length || types[w.type()] == null) return null;
        BuildingVisual.Type t = types[w.type()];
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

    /* ----- b: the yards ----- */

    /** A yard's ground: the terminal's slate warmed a little towards the rail yard's ballast. */
    private static final int GROUND = mix(0xff2e3640, TileRaster.YARD_GROUND, 0.32);

    /** A yard drawn over its box. */
    private static void yard(Graphics2D g, CityShore.Work w, Frame f, Kind kind) {
        Local q = new Local(w, f);
        Random r = new Random(w.x0() * 7919L + w.y0() * 104_729L + w.type());
        q.framed(g, 0, 0, q.L, q.D, GROUND, 0xff4a5866);
        switch (kind) {
            case CONTAINER: containers(g, q, r); break;
            case DRY_BULK: heaps(g, q, r); break;
            case LIQUID:
            case TANKS: tanks(g, q); break;
            default: crates(g, q, r);
        }
    }

    /** The six colours of a box ship's boxes, from ShipShapes.boxColour(). */
    private static int boxColour(int k) {
        BoatSchedule.Boat any = new BoatSchedule.Boat(new BoatSchedule.Call(Ports.Cargo.CONTAINER, Ports.Ship.FEEDER, true, 0, 0), 0, 0, true, true, 0, 1);
        return ShipShapes.boxColour(any, k);
    }

    /** Rows of small coloured boxes in blocks with lanes between, an apron left by the quay. */
    private static void containers(Graphics2D g, Local q, Random r) {
        int margin = 4, back = 5, apron = 10, boxU = 5, boxV = 3, rows = 3, lane = 4, blockV = rows * (boxV + 1) - 1;
        int k = r.nextInt(1000);
        for (int v = back; v + blockV <= q.D - apron; v += blockV + lane) {
            for (int row = 0; row < rows; row++) {
                int slot = 0;
                for (int u = margin; u + boxU <= q.L - margin; u += boxU + 1, slot++) {
                    if (slot % 7 == 6 || r.nextInt(14) == 0) continue;
                    int vv = v + row * (boxV + 1);
                    q.rect(g, u, vv, u + boxU, vv + boxV, boxColour(k++));
                }
            }
        }
    }

    /** Two or three heaps, ochre or grey, each a darker ring and a lighter crown. */
    private static void heaps(Graphics2D g, Local q, Random r) {
        int n = q.L >= 12 * q.px ? 3 : 2, flip = r.nextInt(2);
        for (int i = 0; i < n; i++) {
            double cu = q.L * (i + 0.5) / n + (r.nextDouble() - 0.5) * 4, cv = q.D * 0.46 + (r.nextDouble() - 0.5) * 8;
            double ru = Math.min(q.L / (2.0 * n) - 3, q.D * 0.3) * (0.85 + 0.15 * r.nextDouble()), rv = ru * 0.8;
            int c = (i + flip) % 2 == 0 ? TileRaster.ORE_HEAP : TileRaster.WORKED_GREY;
            q.ellipse(g, cu, cv, ru, rv, mix(c, 0xff000000, 0.3));
            q.ellipse(g, cu - 1, cv - 1, ru * 0.82, rv * 0.82, c);
            q.ellipse(g, cu - ru * 0.2, cv - rv * 0.2, ru * 0.35, rv * 0.35, mix(c, 0xffffffff, 0.25));
        }
    }

    /** Round tanks on a grid, each a rim, a roof and a hatch. */
    private static void tanks(Graphics2D g, Local q) {
        int pitch = 3 * q.px;
        int cols = Math.max(1, (q.L - 4) / pitch), rows = Math.max(1, (q.D - 4) / pitch);
        double r = Math.min(pitch * 0.42, Math.min(q.L, q.D) / 2.0 - 2);
        double u0 = (q.L - cols * pitch) / 2.0 + pitch / 2.0, v0 = (q.D - rows * pitch) / 2.0 + pitch / 2.0;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                double cu = u0 + i * pitch, cv = v0 + j * pitch;
                q.ellipse(g, cu, cv, r, r, 0xff7f8ca6);
                q.ellipse(g, cu, cv, r - 1, r - 1, mix(0xff2a3540, 0xff7f8ca6, 0.4));
                q.ellipse(g, cu, cv, 2, 2, 0xff7f8ca6);
            }
        }
    }

    /** One shed along the land side, stacked crates in groups between it and the apron. */
    private static void crates(Graphics2D g, Local q, Random r) {
        int margin = 4;
        double shedV = Math.min(14, q.D * 0.28);
        shedRoof(g, q, margin, 4, q.L - margin, 4 + shedV);
        int[] palette = { 0xff7b4b3a, mix(TileRaster.ORE_HEAP, 0xff2e3640, 0.35), mix(TileRaster.STEEL, 0xff2e3640, 0.5), mix(0xff7b4b3a, TileRaster.ORE_HEAP, 0.5) };
        int crate = 4, apron = 8;
        double vTop = 4 + shedV + 4;
        for (int u = margin + 2; u + 3 * (crate + 1) <= q.L - margin; u += 3 * (crate + 1) + 3 + r.nextInt(3)) {
            for (double v = vTop; v + 2 * (crate + 1) <= q.D - apron; v += 2 * (crate + 1) + 2) {
                int cols = 2 + r.nextInt(2);
                for (int i = 0; i < cols; i++) for (int j = 0; j < 2; j++) {
                    q.rect(g, u + i * (crate + 1), v + j * (crate + 1), u + i * (crate + 1) + crate, v + j * (crate + 1) + crate, palette[r.nextInt(palette.length)]);
                }
            }
        }
    }

    private static void shedRoof(Graphics2D g, Local q, double u0, double v0, double u1, double v1) {
        int roof = mix(TileRaster.STEEL, 0xff2e3640, 0.55);
        q.framed(g, u0, v0, u1, v1, roof, mix(roof, 0xff000000, 0.45));
        double mid = Math.floor((v0 + v1) / 2);
        q.rect(g, u0 + 2, mid, u1 - 2, mid + 1, mix(roof, 0xffffffff, 0.3));
    }

    /* ----- c: the sheds and the cranes ----- */

    /** A long transit shed along the sea side of the box, set back 1.5 plots from the wall. */
    private static void shed(Graphics2D g, Local q) {
        double depth = Math.min(2 * q.px, q.D * 0.35), v1 = q.D - 1.5 * q.px, v0 = Math.max(3, v1 - depth);
        shedRoof(g, q, q.L * 0.06, v0, q.L * 0.94, v1);
    }

    /** A terminal's 2 or 3 quay cranes on its wall, about its boat. */
    private static void cranes(Graphics2D g, Docked d, Frame f) {
        CityShore.Work w = d.work();
        if (w.quay() != CityShore.HARBOUR_QUAY_PLOTS) return;     // a pier of its own has no wall to stand on
        Local q = new Local(w, f);
        int n = q.L >= 12 * q.px ? 3 : 2;
        double c = q.uOf(d.boat().x(), d.boat().y());
        double apart = Math.max(14, Math.min(3.2 * q.px, (q.L - 24.0) / (n == 3 ? 2 : 1)));
        for (int i = 0; i < n; i++) {
            double u = c + (i - (n - 1) / 2.0) * apart;
            u = Math.max(8, Math.min(q.L - 8, u));
            crane(g, q, Math.round(u));
        }
    }

    /**
     * One gantry crane, seen from above: its portal across the wall - four legs and the beams between them, dark - and its boom, a dark
     * girder running out over the water to the ship (and a short back-reach over the box), with a light cap at its end.
     */
    private static void crane(Graphics2D g, Local q, double u) {
        int wall = q.quay * q.px, v0 = q.D;
        int frame = 0xff2b2f36, light = mix(TileRaster.STEEL, 0xff2b2f36, 0.35);
        int bv0 = v0 + 3, bv1 = v0 + wall - 3;
        q.rect(g, u - 7, bv0, u + 7, bv1, frame);                    // the portal: beams and legs
        q.rect(g, u - 5, bv0 + 2, u + 5, bv1 - 2, TileRaster.QUAY_DECK);   // seen through between the legs
        q.rect(g, u - 7, bv0, u - 4, bv0 + 3, light);                  // the legs' feet, landward
        q.rect(g, u + 4, bv0, u + 7, bv0 + 3, light);
        q.rect(g, u - 7, bv1 - 3, u - 4, bv1, light);                  // ...and seaward
        q.rect(g, u + 4, bv1 - 3, u + 7, bv1, light);
        q.rect(g, u - 1, v0 - 5, u + 2, v0 + wall + 11, frame);       // the boom: back-reach over the box, outreach over the ship
        q.rect(g, u - 2, v0 + wall + 8, u + 3, v0 + wall + 11, light); // its cap
        q.rect(g, u - 3, v0 + wall - 2, u + 4, v0 + wall + 1, frame);  // the trolley head at the wall's face
    }

    /* ----- the specimen sheet ----- */

    /** The sea and the land of the specimen sheet: plain and muted, not the game's tiles. */
    private static final int SEA = 0xff4f87b9, LAND = 0xff8aa07a;

    /**
     * A sheet of every kind of terminal (and one tank farm), a row each, in the three drawings (a, b, c across): each box at its real size
     * (BuildingVisual.footprint()) on a straight shore with the sea to the north, with its quay wall and its boat alongside. Null when
     * the city's types have none. `lines` gets one line a row: name, cargo, box and ship.
     */
    private static BufferedImage kindsSheet(CityMap map, Map<Integer, BuildingsTemplate> templates, List<String> lines) {
        BuildingVisual.Type[] types = map.types();
        List<BuildingVisual.Type> rows = new ArrayList<>();
        for (BuildingVisual.Type t : types) if (t != null && t.berth() >= 0) rows.add(t);
        for (BuildingVisual.Type t : types) {
            if (t != null && t.shore() && t.berth() < 0 && templates.get(t.id()) != null && templates.get(t.id()).getName().equals("Tank Farm")) { rows.add(t); break; }
        }
        if (rows.isEmpty()) return null;
        int maxW = 10, maxH = 1;
        for (BuildingVisual.Type t : rows) {
            int[] fp = BuildingVisual.footprint(t);
            maxW = Math.max(maxW, fp[0]);
            maxH = Math.max(maxH, fp[1]);
        }
        int cw = maxW + 4, ch = maxH + 7, label = 24, head = 3;
        Frame f = new Frame(-label, -head, PX);
        BufferedImage img = new BufferedImage((label + 3 * cw) * PX, (head + rows.size() * ch) * PX, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        aa(g, false);
        g.setColor(col(TileRaster.VOID));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 11));
        String[] heads = { "a  the box as the game draws it", "b  an open cargo yard", "c  shed and quay cranes" };
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        for (int c = 0; c < 3; c++) { g.setColor(Color.WHITE); g.drawString(heads[c], (label + c * cw) * PX + 6, 16); }
        for (int r = 0; r < rows.size(); r++) {
            BuildingVisual.Type t = rows.get(r);
            BuildingsTemplate tpl = templates.get(t.id());
            int[] fp = BuildingVisual.footprint(t);
            boolean berthed = t.berth() >= 0;
            Ports.Cargo cargo = berthed ? Ports.Cargo.values()[t.berth()] : null;
            Ports.Ship ship = berthed ? shipFor(tpl, cargo) : null;
            String name = tpl == null ? "type " + t.id() : tpl.getName();
            lines.add(name + (berthed ? " (" + cargo.label() + ", box " + fp[0] + " x " + fp[1] + " plots, " + ship.label() + ")" : " (no quay, box " + fp[0] + " x " + fp[1] + " plots)"));
            g.setColor(Color.WHITE);
            g.drawString(name, 6, (head + r * ch + 8) * PX);
            g.drawString(fp[0] + " x " + fp[1] + " plots", 6, (head + r * ch + 8) * PX + 14);
            if (berthed) g.drawString(ship.label(), 6, (head + r * ch + 8) * PX + 28);
            for (int c = 0; c < 3; c++) {
                long cx = (long) c * cw, cy = (long) r * ch, x0 = cx + (cw - fp[0]) / 2, y0 = cy + 6;
                aa(g, false);
                g.setColor(col(SEA));
                g.fillRect((int) ((cx - f.x0()) * PX), (int) ((cy - f.y0()) * PX), cw * PX - 1, 4 * PX);
                g.setColor(col(LAND));
                g.fillRect((int) ((cx - f.x0()) * PX), (int) ((cy + 4 - f.y0()) * PX), cw * PX - 1, (ch - 4) * PX - 1);
                CityShore.Work w = new CityShore.Work(x0, y0, fp[0], fp[1], t.id(), 0, berthed ? CityShore.HARBOUR_QUAY_PLOTS : 0);
                if (berthed) walls(g, f, List.of(new CityShore.Wall(x0, y0 - CityShore.HARBOUR_QUAY_PLOTS, fp[0], CityShore.HARBOUR_QUAY_PLOTS, 0)));
                Local q = new Local(w, f);
                q.framed(g, 0, 0, q.L, q.D, BuildingVisual.TERMINAL_FILL, BuildingVisual.TERMINAL_EDGE);
                Kind kind = kindOf(w, types);
                if (c == 1 && kind != null) yard(g, w, f, kind);
                if (c == 2 && kind != null) { if (kind == Kind.TANKS) yard(g, w, f, kind); else shed(g, q); }
                if (!berthed) continue;
                double[] at = w.berth();
                BoatSchedule.Berth b = new BoatSchedule.Berth(cargo, (long) Math.floor(at[0]), (long) Math.floor(at[1]), 0);
                BoatSchedule.Boat boat = dock(b, ship, r * 3 + c, PX);
                boat(g, f, boat, PX);
                if (c == 2) cranes(g, new Docked(w, b, boat), f);
            }
        }
        g.dispose();
        return img;
    }

    /* ----- what the drawing shows ----- */

    /**
     * What a city's boats and walls come to at `scale` px a plot, after BoatSchedule.berthed(): how many boats lie alongside, how many stern-to
     * and how many are as the schedule placed them (centred on the berth point); how many pairs of boats go deeper into one another than
     * BoatSchedule.CROSS_TOLERANCE (overlap()) - among those laid alongside or stern-to, where the rule says none, and with a boat as placed;
     * and how many boats lie across a box (its own or another's) or across a wall (a boat alongside lies against its own wall's face and
     * touches it), the same two ways, the first few of each named by their berths; at true size also wallLines().
     */
    private static List<String> audit(Game g, CityMap map, Map<Integer, BuildingsTemplate> templates, double scale) {
        List<String> out = new ArrayList<>();
        CityShore shore = map.shore();
        List<Docked> ds = docked(g, map, templates, scale);
        List<CityShore.Work> works = shore.works();
        List<CityShore.Wall> walls = shore.walls();
        int[] places = new int[3];
        int pairsLaid = 0, pairsPlaced = 0, boxLaid = 0, boxPlaced = 0, boxOwnPlaced = 0, wallLaid = 0, wallPlaced = 0;
        List<String> pairs = new ArrayList<>(), boxes = new ArrayList<>(), across = new ArrayList<>();
        List<Area> hulls = new ArrayList<>();
        for (Docked d : ds) {
            hulls.add(new Area(poly(new Frame(0, 0, 1), ShipShapes.hull(d.boat(), scale))));
            places[d.place()]++;
        }
        for (int i = 0; i < ds.size(); i++) {
            Docked a = ds.get(i);
            String me = String.format("(%d, %d)", a.berth().x(), a.berth().y());
            for (int j = i + 1; j < ds.size(); j++) {
                if (!overlap(hulls.get(i), hulls.get(j))) continue;
                boolean both = a.laid() && ds.get(j).laid();
                if (both) pairsLaid++; else pairsPlaced++;
                if (pairs.size() < 4) pairs.add(String.format("%s and (%d, %d), %s%s", me, ds.get(j).berth().x(), ds.get(j).berth().y(), cargoPair(a, ds.get(j)),
                        both ? ", both laid alongside or stern-to" : ""));
            }
            for (CityShore.Work k : works) {
                if (!overlap(hulls.get(i), new Area(new Rectangle2D.Double(k.x0(), k.y0(), k.w(), k.h())))) continue;
                if (a.laid()) boxLaid++; else { boxPlaced++; if (k == a.work()) boxOwnPlaced++; }
                if (boxes.size() < 4) boxes.add(String.format("%s over the box at (%d, %d)%s, %s", me, k.x0(), k.y0(), k == a.work() ? " (its own)" : "", a.name()));
            }
            for (CityShore.Wall k : walls) {
                if (!overlap(hulls.get(i), new Area(new Rectangle2D.Double(k.x0(), k.y0(), k.w(), k.h())))) continue;
                if (a.laid()) wallLaid++; else wallPlaced++;
                if (across.size() < 4) across.add(String.format("%s over the wall at (%d, %d) %d x %d, %s", me, k.x0(), k.y0(), k.w(), k.h(), a.name()));
            }
        }
        out.add(String.format("at %.1f px a plot (%s): %d boats docked: %d alongside, %d stern-to, %d as placed; pairs of boats overlapping: %d among those alongside or stern-to (expected 0), %d with a boat as placed;"
                        + " boats alongside or stern-to across a box or a wall: %d and %d (expected 0); boats as placed across a box: %d (%d across its own), across a wall: %d%s%s%s",
                scale, scale > PX - 1 ? "true size" : "just past DETAIL_FROM, true size since slice E", ds.size(), places[0], places[1], places[2], pairsLaid, pairsPlaced,
                boxLaid, wallLaid, boxPlaced, boxOwnPlaced, wallPlaced, listed(pairs), listed(boxes), listed(across)));
        if (scale > PX - 1) out.addAll(wallLines(shore));
        return out;
    }

    private static String cargoPair(Docked a, Docked b) {
        return a.berth().cargo().label() + " / " + b.berth().cargo().label() + ", sides " + a.berth().side() + " / " + b.berth().side();
    }

    private static String listed(List<String> l) {
        return l.isEmpty() ? "" : " [berths " + String.join("; ", l) + "]";
    }

    /** Whether two shapes go deeper into one another than BoatSchedule.CROSS_TOLERANCE: the narrowest width of the part they share (thickness()) is more than it. */
    private static boolean overlap(Area a, Area b) {
        Area c = new Area(a);
        c.intersect(b);
        return !c.isEmpty() && thickness(c) > BoatSchedule.CROSS_TOLERANCE + 1e-9;
    }

    /** The narrowest width of a convex area (the part two convex shapes share): across each of its edges, the spread of its points along the edge's normal, the least. */
    private static double thickness(Area c) {
        List<double[]> pts = new ArrayList<>();
        double[] seg = new double[6];
        for (java.awt.geom.PathIterator it = c.getPathIterator(null); !it.isDone(); it.next()) {
            int type = it.currentSegment(seg);
            if (type == java.awt.geom.PathIterator.SEG_MOVETO || type == java.awt.geom.PathIterator.SEG_LINETO) pts.add(new double[] { seg[0], seg[1] });
        }
        double least = Double.MAX_VALUE;
        for (int i = 0; i < pts.size(); i++) {
            double[] p = pts.get(i), q = pts.get((i + 1) % pts.size());
            double nx = q[1] - p[1], ny = p[0] - q[0], len = Math.hypot(nx, ny);
            if (len < 1e-12) continue;
            double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
            for (double[] r : pts) {
                double d = (r[0] * nx + r[1] * ny) / len;
                lo = Math.min(lo, d);
                hi = Math.max(hi, d);
            }
            least = Math.min(least, hi - lo);
        }
        return least == Double.MAX_VALUE ? 0 : least;
    }

    /**
     * The walls against the quays they stand for. A work with a HARBOUR_QUAY_PLOTS quay has a strip along its box's whole sea side, that
     * deep (the plots shorePlot() + k x the sea's direction, for the box's whole side): every plot of every strip should be in a wall, no
     * wall plot should lie on a box, and the wall plots that are no work's strip are the joins across a gap.
     */
    private static List<String> wallLines(CityShore shore) {
        Set<Plot> strip = new HashSet<>();
        int pier = 0;
        for (CityShore.Work w : shore.works()) {
            if (!w.berthed()) continue;
            if (w.quay() != CityShore.HARBOUR_QUAY_PLOTS) { pier++; continue; }
            int s = w.side();
            boolean ns = s % 2 == 0;
            int along = ns ? w.w() : w.h();
            for (int i = 0; i < along; i++) {
                long ex = s == 0 || s == 2 ? w.x0() + i : s == 1 ? w.x0() + w.w() - 1 : w.x0();
                long ey = s == 1 || s == 3 ? w.y0() + i : s == 0 ? w.y0() : w.y0() + w.h() - 1;
                for (int k = 1; k <= w.quay(); k++) strip.add(new Plot(ex + (long) DX[s] * k, ey + (long) DY[s] * k));
            }
        }
        long outside = 0, cut = 0, wallPlots = 0, onBox = 0, joins = 0;
        for (Plot p : strip) {
            boolean in = false, boxed = false;
            for (CityShore.Wall w : shore.walls()) if (w.holds(p.x(), p.y())) { in = true; break; }
            if (!in) for (CityShore.Work k : shore.works()) if (k.holds(p.x(), p.y())) { boxed = true; break; }
            if (!in && boxed) cut++;
            else if (!in) outside++;
        }
        for (CityShore.Wall w : shore.walls()) {
            for (long x = w.x0(); x < w.x0() + w.w(); x++) {
                for (long y = w.y0(); y < w.y0() + w.h(); y++) {
                    wallPlots++;
                    if (!strip.contains(new Plot(x, y))) joins++;
                    for (CityShore.Work k : shore.works()) if (k.holds(x, y)) { onBox++; break; }
                }
            }
        }
        return List.of(String.format("walls: %d of %,d plots; of the %,d plots of the works' %d-plot strips, %,d lie outside every wall (and %,d more are cut out of it where another work's box stands on the strip); %,d wall plots are no work's strip (joins across a gap of up to %d plots); %,d wall plots lie on a box; %d works with a quay of another length (a pier) have no wall",
                shore.walls().size(), wallPlots, strip.size(), CityShore.HARBOUR_QUAY_PLOTS, outside, cut, joins, CityShore.WALL_JOIN_GAP, onBox, pier));
    }

    private static void write(Path dir, String name, BufferedImage img) throws IOException {
        Path to = dir.resolve(name);
        javax.imageio.ImageIO.write(img, "png", to.toFile());
        System.out.printf("%s  %d x %d  %,d bytes%n", to, img.getWidth(), img.getHeight(), Files.size(to));
    }
}
