package ham.citybuildersim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Scratch, not in the build: FR1's headless renders. MapCheck's yard city
 * (yardCity(), the railway carrying boxes, wagons on 60% of the sidings)
 * painted by the game's own TileRaster through MapTiles, with its trains
 * drawn over it in Java2D from TrainSchedule and TrainShapes - the shapes
 * ui/MapView fills - and the railway's world line as MapView strokes it.
 * Writes PNGs into args[0]. Never touches saves.
 *
 *   javac -cp target/classes -d <scratch> docs/queue/FR1-notes/RenderTrains.java
 *   java -cp "<scratch>;target/classes;<gson.jar>" ham.citybuildersim.RenderTrains docs/queue/FR1-notes
 */
public class RenderTrains {
    static final int CAPTION_H = 40;

    public static void main(String[] args) throws Exception {
        File outDir = new File(args.length > 0 ? args[0] : ".");
        PrintStream real = System.out;
        MapCheck.out = new PrintStream(OutputStream.nullOutputStream());
        MapCheck.Squares sq = new MapCheck.Squares();
        CityMap m = MapCheck.yardCity(sq);
        m.railPicture(new CityMap.RailPicture(true, 0.6));
        CityRuns r = m.runs();
        List<TrainSchedule.Route> routes = m.trainRoutes();
        long[] st = r.freightStation();
        CityMap.WorldLine line = null;
        for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.RAILWAY) line = l;
        real.printf("yard city: %d yards, station %s, %d routes, world line %s%n", r.yards().size(),
                st == null ? "none" : java.util.Arrays.toString(st), routes.size(), line);
        if (st == null || line == null || routes.isEmpty()) return;

        // A month of every kind: ore in, steel out, boxes both ways, fuel in.
        int kinds = Ports.Cargo.values().length;
        double[] in = new double[kinds], out = new double[kinds];
        in[Ports.Cargo.DRY_BULK.ordinal()] = 10 * TrainSchedule.UNIT_TONNES;
        in[Ports.Cargo.LIQUID.ordinal()] = 3 * TrainSchedule.UNIT_TONNES;
        out[Ports.Cargo.GENERAL.ordinal()] = 8 * TrainSchedule.MIXED_TONNES;
        in[Ports.Cargo.CONTAINER.ordinal()] = 6 * TrainSchedule.Kind.INTERMODAL.tonnes();
        TrainSchedule s = TrainSchedule.of(3, in, out, routes);
        real.println(s + ", boxes " + s.boxes());

        // (a) the freight station at 8 px a plot, the moment the most trains are in it or near it.
        int px = MapFrame.BIG_TILE_PX, w = 112, h = 75;
        long cx = st[0] + st[2] / 2, cy = st[1] + st[3] / 2;
        long x0 = cx - w / 2, y0 = cy - h / 2;
        double t = busiest(s, x0, y0, w, h, 2);
        render(m, s, line, x0, y0, w, h, px, MapFrame.L0, t, new File(outDir, "fr1-station.png"),
                String.format("FR1 - the freight station at 8 px a plot, t = %.3f of the month: %s | MapCheck yardCity, the railway carrying boxes; %d trains of a month of every kind",
                        t, words(s, x0, y0, w, h, t), s.callCount()));

        // (b) the same at 16 px, closer: the wagons' marks.
        int w2 = 56, h2 = 38;
        long x2 = cx - w2 / 2, y2 = cy - h2 / 2;
        double t2 = busiest(s, x2, y2, w2, h2, 1);
        render(m, s, line, x2, y2, w2, h2, 2 * px, MapFrame.L0, t2, new File(outDir, "fr1-station-close.png"),
                String.format("FR1 - the freight station at 16 px a plot, t = %.3f: %s | hoppers' heaps, tanks' barrels, vans' roofs, two boxes a flat; a locomotive at each end", t2,
                        words(s, x2, y2, w2, h2, t2)));

        // (c) the gateway: where the line leaves the city's ground, a train fading in along the world line.
        long gx = Math.round(line.x()), gy = Math.round(line.y());
        long x3 = gx - w / 2, y3 = gy - h / 2;
        double t3 = busiest(s, x3, y3, w, h, 1);
        render(m, s, line, x3, y3, w, h, px, MapFrame.L0, t3, new File(outDir, "fr1-gateway.png"),
                String.format("FR1 - the rail gateway at 8 px a plot, t = %.3f: %s | each vehicle fades in over the world line's last %.0f plots", t3,
                        words(s, x3, y3, w, h, t3), TrainSchedule.WORLD_PLOTS));

        // (d) the middle view: 2 px a plot, trains as dashes along the line.
        int w4 = 448, h4 = 300, px4 = 2;
        long x4 = (long) ((cx + line.x()) / 2) - w4 / 2, y4 = (long) ((cy + line.y()) / 2) - h4 / 2;
        double t4 = busiest(s, x4, y4, w4, h4, 1);
        render(m, s, line, x4, y4, w4, h4, px4, MapFrame.L1, t4, new File(outDir, "fr1-middle-view.png"),
                String.format("FR1 - the middle view (L1) at 2 px a plot, t = %.3f: %s | a train is a dash of its colour: blue in full, orange out full, grey empty, white boxes", t4,
                        words(s, x4, y4, w4, h4, t4)));
    }

    /** The t, on a fine grid of the month, with the most trains in the window (`want` of them standing, if any moment has). */
    static double busiest(TrainSchedule s, long x0, long y0, int w, int h, int want) {
        double best = 0.5;
        int bestScore = -1;
        for (int k = 0; k < 2000; k++) {
            double t = (k + 0.5) / 2000;
            List<TrainSchedule.Train> on = s.frame(x0, y0, x0 + w, y0 + h, t);
            int standing = 0;
            for (TrainSchedule.Train tr : on) if (tr.standing()) standing++;
            int score = on.size() * 10 + Math.min(want, standing) * 25;
            if (score > bestScore) { bestScore = score; best = t; }
        }
        return best;
    }

    static String words(TrainSchedule s, long x0, long y0, int w, int h, double t) {
        StringBuilder b = new StringBuilder();
        for (TrainSchedule.Train tr : s.frame(x0, y0, x0 + w, y0 + h, t)) {
            if (b.length() > 0) b.append(", ");
            b.append(tr.call().kind().label()).append(tr.call().kind() == TrainSchedule.Kind.INTERMODAL ? ", boxes both ways" : tr.call().inbound() ? ", an import" : ", an export")
                    .append(tr.standing() ? " standing" : tr.loaded() ? " running full" : " running empty");
        }
        return b.length() == 0 ? "no train" : b.toString();
    }

    /** The window's tiles at px a plot, the world line and the trains at t over them, captioned. */
    static void render(CityMap m, TrainSchedule s, CityMap.WorldLine line, long x0, long y0, int w, int h, int px, int level, double t,
                       File file, String caption) throws Exception {
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        int tpx = World.TILE * px;
        int[] buf = new int[tpx * tpx];
        int W = w * px, H = h * px;
        BufferedImage img = new BufferedImage(W, H + CAPTION_H, BufferedImage.TYPE_INT_ARGB);
        long tx0 = Math.floorDiv(x0, World.TILE), ty0 = Math.floorDiv(y0, World.TILE);
        long tx1 = Math.floorDiv(x0 + w - 1, World.TILE), ty1 = Math.floorDiv(y0 + h - 1, World.TILE);
        for (long ty = ty0; ty <= ty1; ty++) {
            for (long tx = tx0; tx <= tx1; tx++) {
                tiles.pixels(tx, ty, level, px, in, buf);
                long ox = (tx * World.TILE - x0) * px, oy = (ty * World.TILE - y0) * px;
                for (int j = 0; j < tpx; j++) {
                    long yy = oy + j;
                    if (yy < 0 || yy >= H) continue;
                    for (int i = 0; i < tpx; i++) {
                        long xx = ox + i;
                        if (xx < 0 || xx >= W) continue;
                        img.setRGB((int) xx, (int) yy, buf[j * tpx + i]);
                    }
                }
            }
        }
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(0, 0, W, H);
        // The railway's world line, as MapView strokes it: dashed RAIL_DARK at WORLD_LINE_ALPHA, from where it leaves the ground.
        double r = Math.toRadians(line.bearing());
        double lx = (line.x() - x0) * px, ly = (line.y() - y0) * px;
        g.setColor(colour(TileRaster.RAIL_DARK, 0.45));
        g.setStroke(new BasicStroke((float) Math.max(1.5, 0.52 * px), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] { 6, 5 }, 0));
        g.draw(new java.awt.geom.Line2D.Double(lx, ly, lx + Math.cos(r) * 4000, ly + Math.sin(r) * 4000));
        // The trains, as MapView.trains() draws them.
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
