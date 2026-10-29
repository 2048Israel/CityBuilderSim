package ham.citybuildersim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Scratch, not in the build (MF1 item 6): the rail of a kept city, drawn by the game's own painters (TileRaster through MapTiles)
 * with the trains over it as ui/MapView draws them (RenderFx5's way, trimmed). The save is read from a copy in this JVM's temp
 * folder, never written back. args: saveFolder slot outDir, then windows "name cx cy w h px t": the centre in world plots, the
 * size in plots, px a plot, t a month fraction (or -1 for the busiest moment). -Drender.months=N plays N months after the load.
 *
 *   java -Djava.awt.headless=true -Dcbs.earth=<earth-2.bin> -cp "<scratch>;<classes>;<jars>" ham.citybuildersim.Render6 <folder> 8 docs/queue/MF1 6-rail-slot8-after 376878 377060 100 50 12 -1
 *
 * Or, in place of "saveFolder slot", `fixture <spurs>/<bulkWorks>` for MapCheck 8c's iron city with that many Rail Spurs and bulk works
 * (Probe6.fixture(), dry bulk railed so the trains run): the kind of place his shot 7 shows. 6-rail-before/after.png are
 *   Render6 fixture 30/300 docs/queue/MF1 6-rail-after 359635 371545 90 120 10 -1
 * run once on the untouched classes (before) and once on MF1's (after); the slot 8 and slot 7 pairs are his copies' (slot 7 at
 * 376815 374935, 60 x 40 plots, 16 px, t = 0.45).
 */
public class Render6 {
    static final int CAPTION_H = 40;

    public static void main(String[] args) throws Exception {
        boolean fixture = args[0].equals("fixture");
        File outDir = new File(args[2]);
        CityMap m;
        TrainSchedule s;
        int month;
        if (fixture) {
            // args: fixture <spurs>/<bulkWorks> outDir, then the windows: MapCheck 8c's iron city (Probe6.fixture()) with dry bulk railed.
            String[] sb = args[1].split("/");
            m = Probe6.fixture(Integer.parseInt(sb[0]), Integer.parseInt(sb[1]));
            m.packed();
            double[] in = new double[Ports.Cargo.values().length], out = new double[in.length];
            in[Ports.Cargo.DRY_BULK.ordinal()] = 1_200_000;
            out[Ports.Cargo.DRY_BULK.ordinal()] = 1_200_000;
            month = 0;
            s = TrainSchedule.of(3, in, out, m.trainRoutes());
        } else {
            m = load(Path.of(args[0]), Integer.parseInt(args[1]));
            month = lastMonth;
            s = TrainSchedule.of(3, lastGame.getSectors().freight(), m.trainRoutes());
        }
        List<TrainSchedule.Route> routes = m.trainRoutes();
        System.out.printf("month %d, %d routes, %s%n", month, routes.size(), s);
        for (int a = 3; a + 7 < args.length + 1; a += 8) {
            String name = args[a];
            long cx = Long.parseLong(args[a + 1]), cy = Long.parseLong(args[a + 2]);
            int w = Integer.parseInt(args[a + 3]), h = Integer.parseInt(args[a + 4]), px = Integer.parseInt(args[a + 5]);
            double t = Double.parseDouble(args[a + 6]);
            long x0 = cx - w / 2, y0 = cy - h / 2;
            if (t < 0) t = busiest(s, x0, y0, w, h);
            int level = px >= MapFrame.L0_FROM ? MapFrame.L0 : px >= MapFrame.L1_FROM ? MapFrame.L1 : MapFrame.L2;
            int tpx = level == MapFrame.L0 ? (px >= MapFrame.BIG_TILES_FROM ? MapFrame.BIG_TILE_PX : MapFrame.SMALL_TILE_PX)
                    : level == MapFrame.L1 ? MapFrame.MIDDLE_TILE_PX : MapFrame.FAR_TILE_PX;
            render(m, s, x0, y0, w, h, px, tpx, level, t, new File(outDir, name + ".png"),
                    String.format("%s - month %d, %d px a plot, centre %d,%d, t = %.3f: %s", name, month, px, cx, cy, t, words(s, x0, y0, w, h, t)));
        }
    }

    static Game lastGame;
    static int lastMonth;

    /** The save in `src` as slot `slot`, read from a copy in this JVM's temp folder, and -Drender.months played after it. */
    static CityMap load(Path src, int slot) throws Exception {
        Path root = TempDirs.create("render6");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Files.createDirectories(files.savesDirectory());
        for (String suffix : new String[] { ".json", "-history.json", "-map.bin" }) {
            Path f = src.resolve(String.format("slot-%02d%s", slot, suffix));
            if (Files.exists(f)) Files.copy(f, files.savesDirectory().resolve(f.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        }
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try {
            Game g = new Game(files);
            g.loadGameSave(slot);
            for (int k = 0; k < Integer.getInteger("render.months", 0); k++) g.toggleNextMonth();
            lastGame = g;
            lastMonth = g.getMonth();
            return g.getCityMap();
        } finally {
            System.setOut(real);
        }
    }

    static double busiest(TrainSchedule s, long x0, long y0, int w, int h) {
        double best = 0.5;
        int bestScore = -1;
        for (int k = 0; k < 2000; k++) {
            double t = (k + 0.5) / 2000;
            int score = s.frame(x0, y0, x0 + w, y0 + h, t).size();
            if (score > bestScore) { bestScore = score; best = t; }
        }
        return best;
    }

    static String words(TrainSchedule s, long x0, long y0, int w, int h, double t) {
        List<TrainSchedule.Train> on = s.frame(x0, y0, x0 + w, y0 + h, t);
        return on.isEmpty() ? "no train" : on.size() + " train(s)";
    }

    static void render(CityMap m, TrainSchedule s, long x0, long y0, int w, int h, int px, int tpx, int level, double t,
                       File file, String caption) throws Exception {
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        int tside = World.TILE * tpx;
        int[] buf = new int[tside * tside];
        int W = w * px, H = h * px;
        BufferedImage tilesImg = new BufferedImage(w * tpx, h * tpx, BufferedImage.TYPE_INT_ARGB);
        long tx0 = Math.floorDiv(x0, World.TILE), ty0 = Math.floorDiv(y0, World.TILE);
        long tx1 = Math.floorDiv(x0 + w - 1, World.TILE), ty1 = Math.floorDiv(y0 + h - 1, World.TILE);
        for (long ty = ty0; ty <= ty1; ty++) {
            for (long tx = tx0; tx <= tx1; tx++) {
                tiles.pixels(tx, ty, level, tpx, in, buf);
                long ox = (tx * World.TILE - x0) * tpx, oy = (ty * World.TILE - y0) * tpx;
                for (int j = 0; j < tside; j++) {
                    long yy = oy + j;
                    if (yy < 0 || yy >= h * tpx) continue;
                    for (int i = 0; i < tside; i++) {
                        long xx = ox + i;
                        if (xx < 0 || xx >= w * tpx) continue;
                        tilesImg.setRGB((int) xx, (int) yy, buf[j * tside + i]);
                    }
                }
            }
        }
        BufferedImage img = new BufferedImage(W, H + CAPTION_H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(tilesImg, 0, 0, W, H, null);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(0, 0, W, H);
        List<TrainSchedule.Route> routes = s.routes();
        for (TrainSchedule.Train tr : s.frame(x0, y0, x0 + w, y0 + h, t)) {
            List<TrainSchedule.Car> cars = TrainSchedule.cars(tr, routes.get(tr.call().route()));
            int c = TrainShapes.colour(tr);
            if (px < TrainShapes.CARS_FROM) {
                g.setStroke(new BasicStroke((float) Math.max(2, TrainShapes.WIDTH * px), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 0; i + 1 < cars.size(); i++) {
                    TrainSchedule.Car a = cars.get(i), b = cars.get(i + 1);
                    g.setColor(colour(c, Math.min(a.alpha(), b.alpha())));
                    g.draw(new java.awt.geom.Line2D.Double((a.x() - x0) * px, (a.y() - y0) * px, (b.x() - x0) * px, (b.y() - y0) * px));
                }
                continue;
            }
            int mc = TrainShapes.markColour(tr);
            for (TrainSchedule.Car car : cars) {
                fill(g, TrainShapes.body(car), x0, y0, px, colour(car.loco() ? TrainShapes.LOCO : c, car.alpha()));
                if (px < TrainShapes.MARKS_FROM) continue;
                double[][] mark = TrainShapes.mark(car, tr);
                if (mark != null) fill(g, mark, x0, y0, px, colour(mc, car.alpha()));
            }
        }
        g.setClip(null);
        g.setColor(new Color(0x16222c));
        g.fillRect(0, H, W, CAPTION_H);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.setColor(new Color(0xe8e8e8));
        String[] lines = caption.split(" [|] ");
        for (int k = 0; k < lines.length; k++) g.drawString(lines[k], 8, H + 16 + 16 * k);
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", file);
        System.out.println("wrote " + file);
    }

    static void fill(Graphics2D g, double[][] pts, long x0, long y0, int px, Color c) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < pts[0].length; i++) {
            double x = (pts[0][i] - x0) * px, y = (pts[1][i] - y0) * px;
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        g.setColor(c);
        g.fill(p);
    }

    static Color colour(int argb, double alpha) {
        return new Color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, (int) Math.round(255 * Math.max(0, Math.min(1, alpha))));
    }
}
