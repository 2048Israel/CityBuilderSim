package ham.citybuildersim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Scratch, not in the build: CL4's headless renders (FX5's RenderFx5, renamed; FR7's before it) of a played city -
 * FX2's RenderFx2 with its windows centred by name. The city is a long
 * playtest's last month kept by -Dplaytest.keep=<folder> (slot 10 there), so
 * nothing of Jerus's is read or written. Each window is painted by the
 * game's own TileRaster through MapTiles, the world lines and the trains
 * drawn over it as ui/MapView draws them. args: saveRoot slot outDir, then
 * windows "name cx cy w h px t": cx cy plots, or "site 0", "station 0",
 * "hub 0", "siding 0" for the founding site, the freight station's box, the rail
 * net's hub and the first district dealt a plan's siding, offset by the second figure in plots east (cx) and south (cy)
 * when it is "site 300" and so on; t a month fraction, or -1 for the busiest
 * moment. It prints the city's yards and hub to choose windows by.
 *
 *   javac -cp target/classes -d <scratch> docs/queue/CL4/RenderCL4.java
 *   java -Djava.awt.headless=true -Dcbs.earth=<earth-1.bin> -cp "<scratch>;target/classes;<gson.jar>" ham.citybuildersim.RenderCL4 <keep> 10 docs/queue/CL4 \
 *        after-far site 0 site 0 1600 1600 1 -1 ...
 */
public class RenderCL4 {
    static final int CAPTION_H = 40;

    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]);
        int slot = Integer.parseInt(args[1]);
        File outDir = new File(args[2]);
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = new Game(files);
        g.run();
        g.loadGame(slot);
        // -Drender.months=N: play N months after the load, so the map is laid again by this build's runs (a loaded map is the build's that saved it).
        for (int k = 0; k < Integer.getInteger("render.months", 0); k++) g.toggleNextMonth();
        CityMap m = g.getCityMap();
        List<TrainSchedule.Route> routes = m.trainRoutes();
        TrainSchedule s = TrainSchedule.of(3, g.getSectors().freight(), routes);
        CityRuns runs = m.runs();
        System.out.printf("month %d, %d people, %d routes, %s, world lines %s%n", g.getMonth(),
                g.getPopulationManager().getPopulation(), routes.size(), s, m.worldLines());
        System.out.printf("site %s, rail hub %s, station %s, yards:", java.util.Arrays.toString(m.site()),
                java.util.Arrays.toString(runs.rail().hub()), java.util.Arrays.toString(runs.freightStation()));
        for (long[] y : runs.yards()) System.out.print(" " + java.util.Arrays.toString(y));
        System.out.println();
        for (int a = 3; a + 8 < args.length + 1; a += 9) {
            String name = args[a];
            long cx = at(m, runs, args[a + 1], 0) + Long.parseLong(args[a + 2]);
            long cy = at(m, runs, args[a + 3], 1) + Long.parseLong(args[a + 4]);
            int w = Integer.parseInt(args[a + 5]), h = Integer.parseInt(args[a + 6]), px = Integer.parseInt(args[a + 7]);
            double t = Double.parseDouble(args[a + 8]);
            long x0 = cx - w / 2, y0 = cy - h / 2;
            if (t < 0) t = busiest(s, x0, y0, w, h);
            int level = px >= MapFrame.L0_FROM ? MapFrame.L0 : px >= MapFrame.L1_FROM ? MapFrame.L1 : MapFrame.L2;
            int tpx = level == MapFrame.L0 ? (px >= MapFrame.BIG_TILES_FROM ? MapFrame.BIG_TILE_PX : MapFrame.SMALL_TILE_PX)
                    : level == MapFrame.L1 ? MapFrame.MIDDLE_TILE_PX : MapFrame.FAR_TILE_PX;
            render(m, s, x0, y0, w, h, px, tpx, level, t, new File(outDir, name + ".png"),
                    String.format("%s - month %d, %d px a plot, centre %d,%d, t = %.3f: %s", name, g.getMonth(), px, cx, cy, t, words(s, x0, y0, w, h, t)));
        }
    }

    /** A window's centre along axis k (0 x, 1 y): a number, or the founding site's, the freight station's box's middle, or the rail hub's. */
    static long at(CityMap m, CityRuns runs, String what, int k) {
        switch (what) {
            case "site": return m.site()[k];
            case "hub": { long[] h = runs.rail().hub(); return h == null ? m.site()[k] : h[k]; }
            case "station": { long[] y = runs.freightStation(); return y == null ? m.site()[k] : y[k] + y[k + 2] / 2; }
            case "siding": {
                // The middle of the first district, in the map's order, the plans' sidings are dealt to (FR7 slice 5).
                for (CityMap.District d : m.districts()) {
                    if (m.sidingsOf(d) > 0) {
                        return ((k == 0 ? m.baseDX() + d.dx : m.baseDY() + d.dy)) * CityMap.DISTRICT + CityMap.DISTRICT / 2;
                    }
                }
                return m.site()[k];
            }
            default: return Long.parseLong(what);
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

    /** The window's tiles rastered at tpx a plot and scaled to px, the world lines and the trains at t over them, captioned. */
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
        Font labelFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);
        java.awt.FontMetrics fm = g.getFontMetrics(labelFont);
        List<CityMap.WorldLine> wls = m.worldLines();
        double[][] parts = new double[wls.size()][];
        for (int k = 0; k < wls.size(); k++) {
            CityMap.WorldLine line = wls.get(k);
            double r = Math.toRadians(line.way());
            double lx = (line.x() - x0) * px, ly = (line.y() - y0) * px;
            parts[k] = clip(lx, ly, lx + Math.cos(r) * line.reach() * px, ly + Math.sin(r) * line.reach() * px, W, H);
        }
        // The labels as MapView places them since FX5: apart, or one naming both (LandMap.worldLabels()).
        LandMap.WorldLabel[] labels = LandMap.worldLabels(parts, fm.stringWidth(LandMap.WORLD_WORDS), fm.stringWidth(LandMap.WORLD_BOTH_WORDS), fm.getAscent(), W, H);
        for (CityMap.WorldLine line : wls) {
            boolean highway = line.net() == CityRuns.HIGHWAYS;
            double r = Math.toRadians(line.way());
            double lx = (line.x() - x0) * px, ly = (line.y() - y0) * px;
            double ex = lx + Math.cos(r) * line.reach() * px, ey = ly + Math.sin(r) * line.reach() * px;
            g.setColor(colour(highway ? TileRaster.ROAD[BuildingVisual.HIGHWAY] : TileRaster.RAIL_DARK, LandMap.WORLD_ALPHA));
            float wd = (float) (highway ? Math.max(2, 0.86 * px) : Math.max(1.5, 0.52 * px));
            g.setStroke(highway ? new BasicStroke(wd) : new BasicStroke(wd, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] { 6, 5 }, 0));
            g.draw(new java.awt.geom.Line2D.Double(lx, ly, ex, ey));
        }
        g.setFont(labelFont);
        g.setColor(new Color(255, 255, 255, (int) Math.round(255 * LandMap.WORLD_ALPHA)));
        for (LandMap.WorldLabel lab : labels) if (lab != null) g.drawString(lab.words(), (float) lab.x(), (float) lab.y());
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
        String[] lines = caption.split(" \\| ");
        for (int k = 0; k < lines.length; k++) g.drawString(lines[k], 8, H + 16 + 16 * k);
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", file);
        System.out.println("wrote " + file);
    }

    /** The part of the segment (x0, y0)-(x1, y1) inside a w x h view, {x0, y0, x1, y1}; null when none is. */
    static double[] clip(double x0, double y0, double x1, double y1, double w, double h) {
        double t0 = 0, t1 = 1, dx = x1 - x0, dy = y1 - y0;
        double[] p = { -dx, dx, -dy, dy }, q = { x0, w - x0, y0, h - y0 };
        for (int i = 0; i < 4; i++) {
            if (p[i] == 0) { if (q[i] < 0) return null; continue; }
            double t = q[i] / p[i];
            if (p[i] < 0) t0 = Math.max(t0, t); else t1 = Math.min(t1, t);
        }
        if (t0 >= t1) return null;
        return new double[] { x0 + dx * t0, y0 + dy * t0, x0 + dx * t1, y0 + dy * t1 };
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
