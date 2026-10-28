package ham.citybuildersim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Scratch, not in the build: FR9's headless renders of the trucks' whole trips. The city is TruckRouteCheck's TRIPS CITY
 * (accessCity(types, TRIPS_MORE_DEPOTS, TRIPS_HIGHWAYS): 3 districts with trips, 12 roads, 8 ending at a ramp and 4 with a street leg),
 * every district drawn first and then CityMap.tripsDrawn() asked, its tiles painted by the game's own TileRaster through MapTiles,
 * the trucks over them in Java2D exactly as ui/MapView.trucks() draws them: one TruckSchedule a district with trips
 * (TruckSchedule.of(month, loads, trips, shares, key)), its shares the catchment's (TruckSchedule.sharesByTrips()) of tonnes given by hand
 * (100 t a load for every sector but Freight, as TruckRouteCheck section 9 does), each truck filled with TruckShapes.body(truck, px a plot)
 * in TruckShapes.colour() at the truck's alpha, and the highways' world line stroked as MapView strokes it. The ordinary month is 500
 * import, 250 export and 250 home loads (N = 1); the busy one 30,000, 15,000 and 15,000 (N = 100). Writes five PNGs into args[0]:
 *
 *   fr9-trip.png      one world trip followed whole, its truck at 8 moments of its visit (the truck drawn about 4 times its size, to be seen)
 *   fr9-ramp.png      the ramp where that trip leaves the highway for the streets, every truck at one moment, 8 px a plot, 4x insets under it
 *   fr9-street.png    its street leg from the ramp to the access road's end and down to the gate, 8 px a plot, 4x insets under it
 *   fr9-home.png      a home trip from one works' gate to another's, its truck at 4 moments, 8 px a plot (the truck drawn about 4 times its size)
 *   fr9-near-far.png  the near view's far end (MapFrame.L0_FROM, 3.2 px a plot), the busy month, every truck of every schedule, 4x insets under it
 *
 * Pictures 2, 3 and 5 are the game's own size (a truck is 4 px long at 8 px a plot and drawn no shorter than TruckShapes.MIN_PX), so
 * each has insets of the same moment at 4 times the scale, boxed on the picture with the letter the inset carries. A picture over about
 * 300 KB is written with a 256-colour palette (the tiles' gravel is noise, which a PNG does not pack).
 * Never touches saves, never makes a Game.
 *
 *   javac -cp "target/classes;$(cat target/classpath.txt)" -d <scratch> docs/queue/FR9/RenderTrips.java
 *   java -Djava.awt.headless=true -Dcbs.earth=<path to data/earth-2.bin> -cp "<scratch>;target/classes;$(cat target/classpath.txt)" ham.citybuildersim.RenderTrips docs/queue/FR9
 */
public class RenderTrips {

    /** The ordinary month's loads (import, export, home): 1,000 in all, so N is 1. */
    static final double[] MONTH = { 500, 250, 250 };

    /** The busy month's: 60,000 loads, N = 100 (a month is never drawn with more than 1,000 trucks). */
    static final double[] BUSY = { 30000, 15000, 15000 };

    static final int MONTH_NUMBER = 3;

    /** The truck tonnes of the city's loads by hand, as TruckRouteCheck section 9: 100 t a load for every sector but Freight. */
    static final double TONNES = 100;

    /** How many times larger than the game draws it a single truck is shown where one is followed (pictures 1 and 4): its length in pixels. */
    static final double BIG_TRUCK_PX = 19;

    /** The inset's side in pixels and how many times the picture's scale it is drawn at. */
    static final int INSET_PX = 360, INSET_ZOOM = 4;

    static final Color INK = new Color(0x16222c), PAPER = new Color(0xe8e8e8);
    static final Color GREEN = new Color(120, 255, 170), CYAN = new Color(120, 220, 255), RED = new Color(255, 130, 120), WHITE = Color.WHITE;

    /** One truck drawn: its place, which route it is on, its district, how far along that route it is (plots), and its call. */
    record Shown(TruckSchedule.Truck truck, TruckSchedule.Route route, CityMap.District district, double d) { }

    /** The city's schedules for one month of loads: one a district with trips, made as MapView makes them. */
    static final class Scene {
        final CityMap m;
        final double[] loads;
        final String label;
        final List<CityMap.DistrictTrips> trips;
        final Map<Long, double[]> shares;
        final Map<Long, TruckSchedule> schedules = new LinkedHashMap<>();

        Scene(CityMap m, List<CityMap.DistrictTrips> trips, Map<Long, double[]> shares, double[] loads, String label) {
            this.m = m;
            this.trips = trips;
            this.shares = shares;
            this.loads = loads;
            this.label = label;
            for (CityMap.DistrictTrips dt : trips) {
                schedules.put(dt.key(), TruckSchedule.of(MONTH_NUMBER, loads, dt.trips(), shares.getOrDefault(dt.key(), new double[TruckSchedule.Load.values().length]), dt.key()));
            }
        }

        int stands() {
            int n = 1;
            for (TruckSchedule s : schedules.values()) n = Math.max(n, s.stands());
            return n;
        }

        int callCount() {
            int n = 0;
            for (TruckSchedule s : schedules.values()) n += s.callCount();
            return n;
        }

        /** The trucks over the window [x0, x0 + w) x [y0, y0 + h) at t whose middle lies in it, as MapView.trucks() frames them (a truck's length past the edge). */
        List<Shown> shown(long x0, long y0, int w, int h, double px, double t) {
            double margin = Math.max(TruckShapes.LENGTH, TruckShapes.MIN_PX / px);
            long bx0 = (long) Math.floor(x0 - margin), by0 = (long) Math.floor(y0 - margin);
            long bx1 = (long) Math.ceil(x0 + w + margin), by1 = (long) Math.ceil(y0 + h + margin);
            List<Shown> out = new ArrayList<>();
            for (CityMap.DistrictTrips dt : trips) {
                TruckSchedule s = schedules.get(dt.key());
                if (s.callCount() == 0 || !s.crosses(bx0, by0, bx1, by1)) continue;
                for (TruckSchedule.Truck tk : s.frame(bx0, by0, bx1, by1, t)) {
                    if (tk.x() >= x0 && tk.x() < x0 + w && tk.y() >= y0 && tk.y() < y0 + h) {
                        TruckSchedule.Route r = s.routes().get(tk.call().route());
                        out.add(new Shown(tk, r, dt.district(), along(tk.call(), r, t)));
                    }
                }
            }
            return out;
        }
    }

    /** How far along its route a call's truck is at t, in plots (TruckSchedule.position()'s own arithmetic), NaN when it is not on the road. */
    static double along(TruckSchedule.Call call, TruckSchedule.Route route, double t) {
        double leg = route.legSeconds() / TruckSchedule.MONTH_SECONDS, dwell = TruckSchedule.DWELL_SECONDS / TruckSchedule.MONTH_SECONDS, a = call.arrives(), len = route.length();
        if (t >= a - leg && t < a) return len * (1 - (a - t) / leg);
        if (t >= a && t <= a + dwell) return len;
        if (t > a + dwell && t <= a + dwell + leg) return len * (1 - (t - a - dwell) / leg);
        return Double.NaN;
    }

    /** 0 running in, 1 standing at the gate, 2 running out. */
    static int phase(TruckSchedule.Call call, double t) {
        double dwell = TruckSchedule.DWELL_SECONDS / TruckSchedule.MONTH_SECONDS;
        return t < call.arrives() ? 0 : t <= call.arrives() + dwell ? 1 : 2;
    }

    /** One trip analysed: where its parts are, as distances along it. */
    static final class Trip {
        CityMap.DistrictTrips dt;
        int index;
        TruckSchedule.Route r;
        int ramp = -1, at = -1;
        double dLine, dRamp, dEnd, dGate;
        double[] end;

        String name() { return String.format("district (%d,%d), trip %d", dt.district().dx, dt.district().dy, index); }
    }

    static Trip analyse(CityMap m, CityMap.DistrictTrips dt, int index) {
        Trip t = new Trip();
        t.dt = dt;
        t.index = index;
        t.r = dt.trips().get(index);
        TruckSchedule.Route r = t.r;
        int n = r.xs().length;
        t.dGate = r.length();
        t.dLine = n > 1 ? r.along()[1] : 0;
        if (!r.home()) t.ramp = TruckRouteCheck.rampIndex(m, r);
        t.end = roadEnd(m, dt.district(), r.xs()[n - 1], r.ys()[n - 1]);
        if (t.end != null) {
            for (int i = Math.max(t.ramp, 0); i < n && t.at < 0; i++) if (Math.abs(r.xs()[i] - t.end[0]) < 1e-9 && Math.abs(r.ys()[i] - t.end[1]) < 1e-9) t.at = i;
        }
        if (t.ramp >= 0) t.dRamp = r.along()[t.ramp];
        if (t.at >= 0) t.dEnd = r.along()[t.at];
        return t;
    }

    /** The end on the street grid of the access road whose gate is (gx, gy), in a district's plan: {x, y} in world plots, a plot's middle; null for none. */
    static double[] roadEnd(CityMap m, CityMap.District d, double gx, double gy) {
        CityMap.Drawn dr = m.drawn(d);
        if (dr.access == null) return null;
        long[] o = TruckRouteCheck.origin(m, d);
        int f = DistrictPlan.FRAME;
        for (int[] way : dr.access) {
            int g = way[way.length - 1], e = way[0];
            if (way.length < 2 || Math.abs(o[0] + g % f + 0.5 - gx) > 1e-9 || Math.abs(o[1] + g / f + 0.5 - gy) > 1e-9) continue;
            return new double[] { o[0] + e % f + 0.5, o[1] + e / f + 0.5 };
        }
        return null;
    }

    // ------------------------------------------------------------------------------------------------------------------------ the picture

    static MapTiles tiles;
    static final TilePainter.Input PAINT = new TilePainter.Input();

    /** A window over the city painted: its tiles, its world lines; open for the trucks and marks, then finish()ed with a caption. */
    static final class View {
        final CityMap m;
        final long x0, y0;
        final int w, h, W, H;
        final double px;
        final BufferedImage img;
        final Graphics2D g;
        final List<Rectangle2D> used = new ArrayList<>();

        View(CityMap m, long x0, long y0, int w, int h, double px) {
            this.m = m;
            this.x0 = x0;
            this.y0 = y0;
            this.w = w;
            this.h = h;
            this.px = px;
            W = (int) Math.round(w * px);
            H = (int) Math.round(h * px);
            img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
            g = img.createGraphics();
        }

        double sx(double x) { return (x - x0) * px; }
        double sy(double y) { return (y - y0) * px; }
    }

    /** The window's tiles at px a plot painted by the game's own raster, and the world lines over them. */
    static View open(CityMap m, long x0, long y0, int w, int h, double px) {
        if (tiles == null) tiles = new MapTiles(m);
        int level = MapFrame.levelOf(px);
        int tpx = MapFrame.tilePx(level, px);
        int tside = World.TILE * tpx;
        int[] buf = new int[tside * tside];
        BufferedImage tilesImg = new BufferedImage(w * tpx, h * tpx, BufferedImage.TYPE_INT_ARGB);
        long tx0 = Math.floorDiv(x0, World.TILE), ty0 = Math.floorDiv(y0, World.TILE);
        long tx1 = Math.floorDiv(x0 + w - 1, World.TILE), ty1 = Math.floorDiv(y0 + h - 1, World.TILE);
        for (long ty = ty0; ty <= ty1; ty++) {
            for (long tx = tx0; tx <= tx1; tx++) {
                tiles.pixels(tx, ty, level, tpx, PAINT, buf);
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
        View v = new View(m, x0, y0, w, h, px);
        Graphics2D g = v.g;
        boolean exact = Math.abs(px - tpx) < 1e-9 || px / tpx >= 2 && Math.abs(px / tpx - Math.rint(px / tpx)) < 1e-9;
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, exact ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR : RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(tilesImg, 0, 0, v.W, v.H, null);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setClip(0, 0, v.W, v.H);
        // The world lines, as MapView strokes them: the highways' solid, the railway's dashed.
        Font labelFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);
        for (CityMap.WorldLine line : m.worldLines()) {
            boolean highway = line.net() == CityRuns.HIGHWAYS;
            double r = Math.toRadians(line.way());
            double lx = v.sx(line.x()), ly = v.sy(line.y());
            double ex = lx + Math.cos(r) * line.reach() * px, ey = ly + Math.sin(r) * line.reach() * px;
            g.setColor(colour(highway ? TileRaster.ROAD[BuildingVisual.HIGHWAY] : TileRaster.RAIL_DARK, LandMap.WORLD_ALPHA));
            float wd = (float) (highway ? Math.max(2, 0.86 * px) : Math.max(1.5, 0.52 * px));
            g.setStroke(highway ? new BasicStroke(wd) : new BasicStroke(wd, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] { 6, 5 }, 0));
            g.draw(new Line2D.Double(lx, ly, ex, ey));
            double[] cut = clip(lx, ly, ex, ey, v.W, v.H);
            if (cut == null) continue;
            FontMetrics fm = g.getFontMetrics(labelFont);
            double[] at = LandMap.worldLabelAt(cut[0], cut[1], cut[2], cut[3], fm.stringWidth(LandMap.WORLD_WORDS), fm.getAscent(), v.W, v.H);
            if (at == null) continue;
            g.setFont(labelFont);
            g.setColor(new Color(255, 255, 255, (int) Math.round(255 * LandMap.WORLD_ALPHA)));
            g.drawString(LandMap.WORLD_WORDS, (float) at[0], (float) at[1]);
        }
        return v;
    }

    /** The trucks of `on`, each filled as MapView.fillTrucks() fills it. */
    static void trucks(View v, List<Shown> on) {
        for (Shown sh : on) fill(v, TruckShapes.body(sh.truck(), v.px), sh.truck());
    }

    /** One truck drawn about BIG_TRUCK_PX long (its shape kept), to be seen where a single truck is followed. */
    static void truckBig(View v, TruckSchedule.Truck tk) {
        fill(v, TruckShapes.body(tk, v.px * TruckShapes.MIN_PX / BIG_TRUCK_PX), tk);
    }

    static void fill(View v, double[][] pts, TruckSchedule.Truck tk) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < pts[0].length; i++) {
            double x = v.sx(pts[0][i]), y = v.sy(pts[1][i]);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        Color c = colour(TruckShapes.colour(tk), tk.alpha());
        v.g.setColor(c);
        v.g.fill(p);
        v.g.setStroke(new BasicStroke(0.8f));
        v.g.setColor(new Color(0, 0, 0, (int) Math.round(120 * tk.alpha())));
        v.g.draw(p);
    }

    /** A route stroked thin and faint over the tiles (a dark under-stroke so it reads on light ground and dark). */
    static void stroke(View v, TruckSchedule.Route r, Color c, float width) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < r.xs().length; i++) {
            double x = v.sx(r.xs()[i]), y = v.sy(r.ys()[i]);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        v.g.setStroke(new BasicStroke(width + 1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        v.g.setColor(new Color(0, 0, 0, 90));
        v.g.draw(p);
        v.g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        v.g.setColor(c);
        v.g.draw(p);
    }

    static void ring(View v, double x, double y, double r, Color c) {
        double sx = v.sx(x), sy = v.sy(y);
        v.g.setStroke(new BasicStroke(3.4f));
        v.g.setColor(new Color(0, 0, 0, 160));
        v.g.draw(new Ellipse2D.Double(sx - r, sy - r, 2 * r, 2 * r));
        v.g.setStroke(new BasicStroke(1.8f));
        v.g.setColor(c);
        v.g.draw(new Ellipse2D.Double(sx - r, sy - r, 2 * r, 2 * r));
    }

    /** A diamond at a plot, with its word placed beside it when it has one. */
    static void mark(View v, double x, double y, String word, Color c) {
        double sx = v.sx(x), sy = v.sy(y), r = 6.5;
        Path2D.Double d = new Path2D.Double();
        d.moveTo(sx, sy - r); d.lineTo(sx + r, sy); d.lineTo(sx, sy + r); d.lineTo(sx - r, sy); d.closePath();
        v.g.setStroke(new BasicStroke(3.4f));
        v.g.setColor(new Color(0, 0, 0, 170));
        v.g.draw(d);
        v.g.setStroke(new BasicStroke(1.8f));
        v.g.setColor(c);
        v.g.draw(d);
        if (word != null) label(v, word, sx, sy, 0, 1, 14, c, false);
    }

    /**
     * A word near a point, placed by trying a few offsets (along the normal (nx, ny), either side, then straight out) for the first
     * whose box lies inside the picture and clears the words placed already; a line joins it to the point when it is not close.
     */
    static void label(View v, String text, double sx, double sy, double nx, double ny, double gap, Color tint, boolean bold) {
        Graphics2D g = v.g;
        g.setFont(new Font(Font.SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, 13));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text), th = fm.getAscent() + 3;
        List<double[]> tries = new ArrayList<>();
        for (double mult : new double[] { 1, 2.2, 3.6, 5.5 }) {
            tries.add(new double[] { nx * gap * mult, ny * gap * mult });
            tries.add(new double[] { -nx * gap * mult, -ny * gap * mult });
            tries.add(new double[] { gap * mult, 0 });
            tries.add(new double[] { -gap * mult, 0 });
            tries.add(new double[] { 0, gap * mult });
            tries.add(new double[] { 0, -gap * mult });
        }
        Rectangle2D pick = null;
        for (double[] t : tries) {
            double ax = sx + t[0], ay = sy + t[1];
            // The box: the text left of its anchor when the offset points left, right of it when right, below or above it when mostly vertical.
            boolean vertical = Math.abs(t[1]) > Math.abs(t[0]);
            double bx = vertical ? ax - tw / 2.0 : t[0] < 0 ? ax - tw - 4 : ax + 2;
            double by = vertical ? (t[1] < 0 ? ay - th - 2 : ay + 3) : ay - th / 2.0;
            Rectangle2D box = new Rectangle2D.Double(bx - 2, by, tw + 8, th + 2);
            if (box.getMinX() < 2 || box.getMinY() < 2 || box.getMaxX() > v.W - 2 || box.getMaxY() > v.H - 2) continue;
            boolean clash = false;
            for (Rectangle2D u : v.used) clash |= u.intersects(box);
            if (clash) continue;
            pick = box;
            break;
        }
        if (pick == null) pick = new Rectangle2D.Double(Math.max(2, Math.min(v.W - tw - 10, sx + 4)), Math.max(2, Math.min(v.H - th - 6, sy + 4)), tw + 8, th + 2);
        v.used.add(pick);
        double cx = pick.getCenterX(), cy = pick.getCenterY();
        if (Math.hypot(cx - sx, cy - sy) > gap + tw / 2.0 + 6) {
            g.setStroke(new BasicStroke(1.2f));
            g.setColor(new Color(0, 0, 0, 150));
            g.draw(new Line2D.Double(sx, sy, cx, cy));
        }
        g.setColor(new Color(10, 16, 22, 205));
        g.fill(new RoundRectangle2D.Double(pick.getMinX(), pick.getMinY(), pick.getWidth(), pick.getHeight(), 6, 6));
        g.setColor(tint);
        g.drawString(text, (float) (pick.getMinX() + 4), (float) (pick.getMinY() + fm.getAscent()));
    }

    /** A tag with its top-left corner at (x, y): a dark box with a word. */
    static void tag(Graphics2D g, String text, double x, double y, Color tint) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(10, 16, 22, 215));
        g.fill(new RoundRectangle2D.Double(x, y, fm.stringWidth(text) + 10, fm.getAscent() + 6, 6, 6));
        g.setColor(tint);
        g.drawString(text, (float) (x + 5), (float) (y + fm.getAscent() + 1));
    }

    // ------------------------------------------------------------------------------------------------------------------------ the insets

    /** A place an inset is of: the centre in plots, and what it is. */
    record Spot(double x, double y, String name) { }

    /** An inset of the same moment, INSET_ZOOM times the picture's scale, over `spot`; its box is drawn on the picture. */
    static BufferedImage inset(Scene s, View main, double tm, Spot spot, String letter) {
        double pxIn = main.px * INSET_ZOOM;
        int n = (int) Math.ceil(INSET_PX / pxIn);
        long ix0 = Math.round(spot.x() - n / 2.0), iy0 = Math.round(spot.y() - n / 2.0);
        View iv = open(main.m, ix0, iy0, n, n, pxIn);
        List<Shown> on = s.shown(ix0, iy0, n, n, pxIn, tm);
        trucks(iv, on);
        iv.g.setClip(null);
        iv.g.setStroke(new BasicStroke(1.5f));
        iv.g.setColor(new Color(255, 255, 255, 200));
        iv.g.draw(new Rectangle2D.Double(0.75, 0.75, iv.W - 1.5, iv.H - 1.5));
        tag(iv.g, letter + "  " + spot.name() + "  (" + on.size() + " trucks, " + INSET_ZOOM + "x)", 6, 6, WHITE);
        iv.g.dispose();
        // The box on the picture.
        Graphics2D g = main.g;
        g.setClip(null);
        g.setStroke(new BasicStroke(3.2f));
        g.setColor(new Color(0, 0, 0, 160));
        double bx = main.sx(ix0), by = main.sy(iy0), bw = n * main.px;
        g.draw(new Rectangle2D.Double(bx, by, bw, bw));
        g.setStroke(new BasicStroke(1.6f));
        g.setColor(WHITE);
        g.draw(new Rectangle2D.Double(bx, by, bw, bw));
        tag(g, letter, bx + 2, by - 22 < 0 ? by + bw + 2 : by - 22, WHITE);
        return iv.img;
    }

    /**
     * Up to `count` places worth an inset among the trucks shown, each the middle of a truck that is one of the kind asked (the kinds taken in turn),
     * with the most trucks about it, `apart` plots or more from the places chosen already.
     */
    static List<Spot> spots(List<Shown> on, List<Spot> chosen, int count, double about, double apart, List<Predicate<Shown>> kinds, List<String> names) {
        List<Spot> out = new ArrayList<>();
        for (int k = 0; out.size() < count && k < kinds.size() * 3; k++) {
            Predicate<Shown> kind = kinds.get(k % kinds.size());
            Shown best = null;
            int bestN = -1;
            for (Shown c : on) {
                if (!kind.test(c)) continue;
                boolean near = false;
                for (Spot sp : chosen) near |= Math.max(Math.abs(sp.x() - c.truck().x()), Math.abs(sp.y() - c.truck().y())) < apart;
                for (Spot sp : out) near |= Math.max(Math.abs(sp.x() - c.truck().x()), Math.abs(sp.y() - c.truck().y())) < apart;
                if (near) continue;
                int n = 0;
                for (Shown o : on) if (Math.max(Math.abs(o.truck().x() - c.truck().x()), Math.abs(o.truck().y() - c.truck().y())) <= about) n++;
                if (n > bestN) { bestN = n; best = c; }
            }
            if (best != null) out.add(new Spot(best.truck().x(), best.truck().y(), names.get(k % kinds.size())));
        }
        return out;
    }

    // ------------------------------------------------------------------------------------------------------------------------ the caption

    static List<String> wrap(String text, FontMetrics fm, int width) {
        List<String> out = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String next = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(next) > width && line.length() > 0) {
                out.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(next);
            }
        }
        if (line.length() > 0) out.add(line.toString());
        return out;
    }

    /** The picture with its insets in a row under it and a caption strip under them (the text wrapped, then the colours' key), written to `file`. */
    static void finish(View v, List<BufferedImage> insets, File file, String caption) throws Exception {
        v.g.dispose();
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D pg = probe.createGraphics();
        pg.setFont(font);
        FontMetrics fm = pg.getFontMetrics();
        List<String> lines = new ArrayList<>();
        for (String para : caption.split("\n")) lines.addAll(wrap(para, fm, v.W - 20));
        pg.dispose();
        int insetH = 0;
        for (BufferedImage i : insets) insetH = Math.max(insetH, i.getHeight());
        int rowH = insets.isEmpty() ? 0 : insetH + 8;
        int capH = 10 + 16 * lines.size() + 30;
        BufferedImage out = new BufferedImage(v.W, v.H + rowH + capH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(v.img, 0, 0, null);
        g.setColor(INK);
        g.fillRect(0, v.H, v.W, rowH + capH);
        int x = 0;
        for (BufferedImage i : insets) {
            g.drawImage(i, x, v.H + 4, null);
            x += i.getWidth() + 6;
        }
        int top = v.H + rowH;
        g.setFont(font);
        g.setColor(PAPER);
        for (int k = 0; k < lines.size(); k++) g.drawString(lines.get(k), 10, top + 20 + 16 * k);
        // The key: the colours a truck is drawn in.
        int y = top + capH - 14;
        x = 10;
        String[] words = { "import, loaded in", "export, loaded out", "empty", "bought at home, loaded" };
        int[] cols = { ShipShapes.IMPORT, ShipShapes.EXPORT, ShipShapes.EMPTY, TruckShapes.HOME };
        for (int k = 0; k < words.length; k++) {
            g.setColor(colour(cols[k], 1));
            g.fill(new RoundRectangle2D.Double(x, y - 9, 22, 10, 4, 4));
            g.setColor(PAPER);
            g.drawString(words[k], x + 28, y);
            x += 28 + fm.stringWidth(words[k]) + 22;
        }
        g.dispose();
        write(out, file);
    }

    /** The PNG: as it is, or with a 256-colour palette when that comes to more than about 300 KB. */
    static void write(BufferedImage img, File file) throws Exception {
        javax.imageio.ImageIO.write(img, "png", file);
        String how = "full colour";
        if (file.length() > 290_000) {
            javax.imageio.ImageIO.write(quantize(img, 256), "png", file);
            how = "256 colours";
        }
        if (file.length() > 300_000) {
            javax.imageio.ImageIO.write(quantize(img, 128), "png", file);
            how = "128 colours";
        }
        System.out.printf("wrote %s: %d x %d, %,d bytes (%s)%n", file.getName(), img.getWidth(), img.getHeight(), file.length(), how);
    }

    /** The picture in 256 colours: the truck and key colours kept exactly, the rest by median cut. */
    static BufferedImage quantize(BufferedImage src, int colours) {
        int w = src.getWidth(), h = src.getHeight();
        int[] px = src.getRGB(0, 0, w, h, null, 0, w);
        Map<Integer, Integer> hist = new HashMap<>();
        for (int p : px) hist.merge(p & 0xffffff, 1, Integer::sum);
        int[] reserved = { ShipShapes.IMPORT, ShipShapes.EXPORT, ShipShapes.EMPTY, TruckShapes.HOME, 0xffffff, 0x000000, INK.getRGB(), PAPER.getRGB() };
        List<int[]> pal = new ArrayList<>();
        for (int c : reserved) pal.add(new int[] { (c >> 16) & 255, (c >> 8) & 255, c & 255 });
        int[] cols = new int[hist.size()], cnt = new int[hist.size()];
        int k = 0;
        for (Map.Entry<Integer, Integer> e : hist.entrySet()) { cols[k] = e.getKey(); cnt[k++] = e.getValue(); }
        List<int[]> boxes = new ArrayList<>();
        int[] all = new int[cols.length];
        for (int i = 0; i < all.length; i++) all[i] = i;
        boxes.add(all);
        while (boxes.size() < colours - reserved.length) {
            int bi = -1;
            double bs = -1;
            int bc = 0;
            for (int i = 0; i < boxes.size(); i++) {
                int[] b = boxes.get(i);
                if (b.length < 2) continue;
                int[] lo = { 255, 255, 255 }, hi = { 0, 0, 0 };
                long total = 0;
                for (int idx : b) {
                    total += cnt[idx];
                    for (int c = 0; c < 3; c++) { int v = (cols[idx] >> (16 - 8 * c)) & 255; lo[c] = Math.min(lo[c], v); hi[c] = Math.max(hi[c], v); }
                }
                for (int c = 0; c < 3; c++) {
                    double score = (hi[c] - lo[c]) * Math.sqrt((double) total);
                    if (score > bs) { bs = score; bi = i; bc = c; }
                }
            }
            if (bi < 0 || bs <= 0) break;
            int[] b = boxes.remove(bi);
            final int ch = bc;
            Integer[] sorted = new Integer[b.length];
            for (int i = 0; i < b.length; i++) sorted[i] = b[i];
            Arrays.sort(sorted, (p, q) -> Integer.compare((cols[p] >> (16 - 8 * ch)) & 255, (cols[q] >> (16 - 8 * ch)) & 255));
            long total = 0;
            for (int idx : b) total += cnt[idx];
            long run = 0;
            int cut = 1;
            for (int i = 0; i < sorted.length; i++) { run += cnt[sorted[i]]; if (run * 2 >= total) { cut = Math.max(1, Math.min(sorted.length - 1, i + 1)); break; } }
            int[] a = new int[cut], c2 = new int[sorted.length - cut];
            for (int i = 0; i < cut; i++) a[i] = sorted[i];
            for (int i = cut; i < sorted.length; i++) c2[i - cut] = sorted[i];
            boxes.add(a);
            boxes.add(c2);
        }
        for (int[] b : boxes) {
            long r = 0, g = 0, bl = 0, total = 0;
            for (int idx : b) { r += (long) ((cols[idx] >> 16) & 255) * cnt[idx]; g += (long) ((cols[idx] >> 8) & 255) * cnt[idx]; bl += (long) (cols[idx] & 255) * cnt[idx]; total += cnt[idx]; }
            pal.add(new int[] { (int) (r / total), (int) (g / total), (int) (bl / total) });
        }
        byte[] rr = new byte[256], gg = new byte[256], bb = new byte[256];
        for (int i = 0; i < pal.size() && i < 256; i++) { rr[i] = (byte) pal.get(i)[0]; gg[i] = (byte) pal.get(i)[1]; bb[i] = (byte) pal.get(i)[2]; }
        IndexColorModel icm = new IndexColorModel(8, 256, rr, gg, bb);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED, icm);
        byte[] data = ((DataBufferByte) out.getRaster().getDataBuffer()).getData();
        Map<Integer, Byte> nearest = new HashMap<>();
        for (int i = 0; i < px.length; i++) {
            int c = px[i] & 0xffffff;
            Byte got = nearest.get(c);
            if (got == null) {
                int best = 0;
                long bd = Long.MAX_VALUE;
                for (int j = 0; j < pal.size() && j < 256; j++) {
                    long dr = ((c >> 16) & 255) - pal.get(j)[0], dg = ((c >> 8) & 255) - pal.get(j)[1], db = (c & 255) - pal.get(j)[2];
                    long d = dr * dr * 2 + dg * dg * 4 + db * db * 3;
                    if (d < bd) { bd = d; best = j; }
                }
                got = (byte) best;
                nearest.put(c, got);
            }
            data[i] = got;
        }
        return out;
    }

    // ------------------------------------------------------------------------------------------------------------------------ the counts

    /** The trucks in view in words: by load, by trip, loaded or empty, running in, standing at a gate, running out. */
    static String words(List<Shown> on, double t) {
        int[] by = new int[TruckSchedule.Load.values().length];
        int full = 0, empty = 0, in = 0, standing = 0, out = 0, world = 0, home = 0;
        for (Shown sh : on) {
            TruckSchedule.Truck tk = sh.truck();
            by[tk.call().load().ordinal()]++;
            if (tk.loaded()) full++; else empty++;
            switch (phase(tk.call(), t)) { case 0 -> in++; case 1 -> standing++; default -> out++; }
            if (sh.route().home()) home++; else world++;
        }
        return String.format("%d trucks in view: %d import (blue in, grey out), %d export (grey in, orange out), %d home (violet in, grey out); %d on world trips and %d on home trips; "
                + "%d loaded and %d empty; %d running in, %d standing at a gate, %d running out.",
                on.size(), by[0], by[1], by[2], world, home, full, empty, in, standing, out);
    }

    /** Whether a truck is on a plot the highways cover. */
    static boolean onHighway(CityMap m, TruckSchedule.Truck tk) {
        return CityRuns.covered(m.runs().highways(), (long) Math.floor(tk.x()), (long) Math.floor(tk.y()));
    }

    /** Whether a truck is on the street grid or an access road, off the highways, and shown whole (not fading in). */
    static boolean offHighway(CityMap m, Shown sh) {
        return sh.truck().alpha() >= 1 && !onHighway(m, sh.truck()) && TruckRouteCheck.isStreet(m, (long) Math.floor(sh.truck().x()), (long) Math.floor(sh.truck().y()));
    }

    static final Map<TruckSchedule.Route, Double> RAMP_AT = new IdentityHashMap<>();

    /** Where a world route leaves the highways, in plots along it (its ramp); routes the harness never analysed are read once. */
    static double rampAt(CityMap m, TruckSchedule.Route r) {
        Double got = RAMP_AT.get(r);
        if (got == null) {
            int i = r.home() ? -1 : TruckRouteCheck.rampIndex(m, r);
            got = i < 0 ? Double.NaN : r.along()[i];
            RAMP_AT.put(r, got);
        }
        return got;
    }

    static int offTotal = 0, pastRampOnHighway = 0, shownTotal = 0;

    /**
     * Counts the trucks of `on` that are off every road the plans and the highways draw (the world part of a world trip, before its arm's end, is exempt)
     * and those of a world trip past its ramp that are on the highways again.
     */
    static int offRoad(CityMap m, List<Shown> on, String what) {
        int off = 0;
        for (Shown sh : on) {
            shownTotal++;
            TruckSchedule.Truck tk = sh.truck();
            TruckSchedule.Route r = sh.route();
            if (!r.home()) {
                double ramp = rampAt(m, r);
                if (!Double.isNaN(ramp) && sh.d > ramp + 0.6 && onHighway(m, tk)) {
                    pastRampOnHighway++;
                    System.out.printf("      PAST ITS RAMP BUT ON A HIGHWAY (%s): truck at (%.2f, %.2f), %.1f plots along its %.0f, ramp at %.0f%n", what, tk.x(), tk.y(), sh.d, r.length(), ramp);
                }
            }
            if (!r.home() && r.xs().length > 2 && sh.d < r.along()[2] - 1e-6) continue;
            long px = (long) Math.floor(tk.x()), py = (long) Math.floor(tk.y());
            boolean road = CityRuns.covered(m.runs().highways(), px, py) || TruckRouteCheck.isStreet(m, px, py);
            if (!road) {
                off++;
                System.out.printf("      OFF THE ROAD (%s): %s truck at (%.2f, %.2f), plot (%d, %d), %.1f plots along its %.0f, alpha %.2f%n", what, r.home() ? "home" : "world", tk.x(), tk.y(), px, py, sh.d, r.length(), tk.alpha());
            }
        }
        return off;
    }

    static String monthWords(Scene s) {
        double[] l = s.loads;
        return String.format("%s: %,.0f import, %,.0f export and %,.0f home loads (%,.0f in all), N = %d, the legend would read \"Trucks: one per %s\"; %,d trucks drawn in the month, on %d districts' trips",
                s.label, l[0], l[1], l[2], l[0] + l[1] + l[2], s.stands(), s.stands() <= 1 ? "load" : String.format("%,d loads", s.stands()), s.callCount(), s.trips.size());
    }

    /**
     * The t, on a fine grid of the month, whose window shows the most trucks of the most colours going both ways, with `weight` more for each (up to 8) of
     * the trucks that `focus` picks: {t, score}.
     */
    static double[] busiest(Scene s, long x0, long y0, int w, int h, double px, Predicate<Shown> focus, double weight) {
        double best = 0.5, bestScore = -1;
        for (int k = 0; k < 1200; k++) {
            double t = (k + 0.5) / 1200;
            List<Shown> on = s.shown(x0, y0, w, h, px, t);
            Set<Integer> colours = new HashSet<>();
            boolean in = false, out = false;
            int focused = 0;
            for (Shown sh : on) {
                colours.add(TruckShapes.colour(sh.truck()));
                in |= phase(sh.truck().call(), t) == 0;
                out |= phase(sh.truck().call(), t) == 2;
                if (focus != null && focus.test(sh)) focused++;
            }
            double score = Math.min(on.size(), 60) * 6 + 40 * colours.size() + (in && out ? 40 : 0) + weight * Math.min(focused, 8);
            if (score > bestScore) { bestScore = score; best = t; }
        }
        return new double[] { best, bestScore };
    }

    // ------------------------------------------------------------------------------------------------------------------------ the renders

    public static void main(String[] args) throws Exception {
        File outDir = new File(args.length > 0 ? args[0] : ".");
        long t0 = System.nanoTime();
        PrintStream real = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        BuildingVisual.Type[] types = TruckRouteCheck.types();
        CityMap m = TruckRouteCheck.accessCity(types, TruckRouteCheck.TRIPS_MORE_DEPOTS, TruckRouteCheck.TRIPS_HIGHWAYS);
        for (CityMap.District d : m.districts()) m.drawn(d);
        List<CityMap.DistrictTrips> trips = m.tripsDrawn();
        System.setOut(real);
        CityMap.WorldLine line = TruckRouteCheck.highwayLine(m);

        // The tonnes by hand (TruckRouteCheck section 9): 100 t of each load for each sector but Freight, so the shares are real catchments.
        java.util.function.Function<String, double[]> by100 = s -> s.equals("Freight") ? new double[3] : new double[] { TONNES, TONNES, TONNES };
        long[] totals = m.totals();
        Set<String> standing = new HashSet<>();
        for (int t = 0; t < m.types().length; t++) {
            if (totals[t] > 0 && m.types()[t] != null && !m.types()[t].sector().isEmpty() && !m.types()[t].sector().equals("Freight")) standing.add(m.types()[t].sector());
        }
        double[] cityTonnes = { TONNES * standing.size(), TONNES * standing.size(), TONNES * standing.size() };
        double[][] per = TruckSchedule.perBuilding(by100, m.types(), totals);
        Map<Long, double[]> shares = TruckSchedule.sharesByTrips(trips, m::district, per, cityTonnes);

        int worldTrips = 0, homeTrips = 0;
        for (CityMap.DistrictTrips dt : trips) for (TruckSchedule.Route r : dt.trips()) { if (r.home()) homeTrips++; else worldTrips++; }
        int[] rc = m.tripRoadCounts();
        real.printf("trips city: site %s, %d districts, %d with trips; %d roads (%d end at a ramp, %d with a street leg, %d no ramp reached); %d world trips and %d home trips; world line %s%n",
                Arrays.toString(m.site()), m.districts().size(), trips.size(), rc[0], rc[1], rc[2], rc[3], worldTrips, homeTrips, line);
        for (CityMap.DistrictTrips dt : trips) {
            double[] s = shares.get(dt.key());
            real.printf("  district (%d,%d): %d trips, shares of imports/exports/home %.3f / %.3f / %.3f%n", dt.district().dx, dt.district().dy, dt.trips().size(), s[0], s[1], s[2]);
        }
        Scene month = new Scene(m, trips, shares, MONTH, "an ordinary month");
        Scene busy = new Scene(m, trips, shares, BUSY, "a busy month");
        real.printf("ordinary month: %,d trucks, N = %d; busy month: %,d trucks, N = %d%n", month.callCount(), month.stands(), busy.callCount(), busy.stands());

        // The trips: the world trip that runs farthest from its ramp (its street leg and its access road, both non-empty), and the home trip with the most points.
        Trip street = null, home = null;
        for (CityMap.DistrictTrips dt : trips) {
            for (int i = 0; i < dt.trips().size(); i++) {
                Trip t = analyse(m, dt, i);
                if (!t.r.home()) {
                    real.printf("  %s: %d points, %.0f plots, ramp at %.0f (%s), road's end at %.0f (%s), street leg %.0f plots, access road %.0f plots%n", t.name(), t.r.xs().length, t.r.length(), t.dRamp,
                            t.ramp < 0 ? "none" : String.format("%.1f,%.1f", t.r.xs()[t.ramp], t.r.ys()[t.ramp]), t.dEnd, t.end == null ? "none" : String.format("%.1f,%.1f", t.end[0], t.end[1]), t.dEnd - t.dRamp, t.dGate - t.dEnd);
                }
                if (t.r.home()) {
                    if (home == null || t.r.xs().length > home.r.xs().length || t.r.xs().length == home.r.xs().length && t.r.length() > home.r.length()) home = t;
                } else if (t.ramp >= 2 && t.at > t.ramp) {
                    if (street == null || t.dGate - t.dRamp > street.dGate - street.dRamp) street = t;
                }
            }
        }
        if (street == null || home == null) throw new IllegalStateException("the trips city has no world trip with a street leg or no home trip");
        real.printf("world trip picked: %s, %d points, %.0f plots: line's point at %.0f, ramp (point %d) at %.0f = (%.1f, %.1f), road's end (point %d) at %.0f = (%.1f, %.1f), gate at %.0f = (%.1f, %.1f)%n",
                street.name(), street.r.xs().length, street.r.length(), street.dLine, street.ramp, street.dRamp, street.r.xs()[street.ramp], street.r.ys()[street.ramp], street.at, street.dEnd,
                street.end[0], street.end[1], street.dGate, street.r.xs()[street.r.xs().length - 1], street.r.ys()[street.r.xs().length - 1]);
        real.printf("home trip picked: %s, %d points, %.0f plots, from (%.1f, %.1f) to (%.1f, %.1f)%n", home.name(), home.r.xs().length, home.r.length(),
                home.r.xs()[0], home.r.ys()[0], home.r.xs()[home.r.xs().length - 1], home.r.ys()[home.r.xs().length - 1]);

        trip(m, month, street, new File(outDir, "fr9-trip.png"));
        ramp(m, month, street, new File(outDir, "fr9-ramp.png"));
        streetLeg(m, month, street, new File(outDir, "fr9-street.png"));
        home(m, month, home, new File(outDir, "fr9-home.png"));
        nearFar(m, busy, new File(outDir, "fr9-near-far.png"));
        real.printf("trucks in the five pictures: %d; off a drawn road: %d; past their ramp and back on a highway: %d%n", shownTotal, offTotal, pastRampOnHighway);
        sweep(m, month, real);
        sweep(m, busy, real);
        real.printf("total %.1f s%n", (System.nanoTime() - t0) / 1e9);
    }

    /**
     * Not a picture: every truck of every schedule at 600 moments of the month, each checked against the roads (on a highway plot or a street the plans draw, from
     * a world trip's arm's end on), a world trip's trucks past its ramp (none on a highway plot again), and where a truck is not shown whole (alpha under 1:
     * only over a world trip's world part, or the first plots out of a gate), and every trip's ends (a gate; a home trip's start too).
     */
    static void sweep(CityMap m, Scene s, PrintStream out) {
        double ax = Double.MAX_VALUE, ay = Double.MAX_VALUE, bx = -Double.MAX_VALUE, by = -Double.MAX_VALUE;
        for (CityMap.DistrictTrips dt : s.trips) {
            for (TruckSchedule.Route r : dt.trips()) {
                double[] b = TruckSchedule.box(r);
                ax = Math.min(ax, b[0]); ay = Math.min(ay, b[1]); bx = Math.max(bx, b[2]); by = Math.max(by, b[3]);
            }
        }
        long x0 = (long) Math.floor(ax) - 40, y0 = (long) Math.floor(ay) - 40;
        int w = (int) (Math.ceil(bx) + 40 - x0), h = (int) (Math.ceil(by) + 40 - y0);
        List<int[]> works = TruckRouteCheck.worldWorks(m);
        int endsBad = 0, startsBad = 0, routes = 0;
        for (CityMap.DistrictTrips dt : s.trips) {
            for (TruckSchedule.Route r : dt.trips()) {
                routes++;
                int n = r.xs().length;
                if (!TruckRouteCheck.atGate(works, r.xs()[n - 1], r.ys()[n - 1])) endsBad++;
                if (r.home() && !TruckRouteCheck.atGate(works, r.xs()[0], r.ys()[0])) startsBad++;
            }
        }
        int frames = 0, trucks = 0, off = 0, past = 0, fadeBad = 0, standing = 0, standingAway = 0, maxAtOnce = 0;
        for (int k = 0; k < 600; k++) {
            double t = (k + 0.5) / 600;
            List<Shown> on = s.shown(x0, y0, w, h, 3.2, t);
            frames++;
            maxAtOnce = Math.max(maxAtOnce, on.size());
            for (Shown sh : on) {
                trucks++;
                TruckSchedule.Truck tk = sh.truck();
                TruckSchedule.Route r = sh.route();
                if (!r.home()) {
                    double ramp = rampAt(m, r);
                    if (!Double.isNaN(ramp) && sh.d > ramp + 0.6 && onHighway(m, tk)) past++;
                }
                boolean worldPart = !r.home() && r.xs().length > 2 && sh.d < r.along()[2] - 1e-6;
                if (!worldPart) {
                    long px = (long) Math.floor(tk.x()), py = (long) Math.floor(tk.y());
                    if (!(CityRuns.covered(m.runs().highways(), px, py) || TruckRouteCheck.isStreet(m, px, py))) off++;
                }
                if (tk.alpha() < 1 && !(r.home() ? sh.d < r.fadeTo() : sh.d < r.along()[1] + 1e-6)) fadeBad++;
                if (phase(tk.call(), t) == 1) {
                    standing++;
                    if (Math.abs(sh.d - r.length()) > 1e-6) standingAway++;
                }
            }
        }
        out.printf("sweep of %s (%d routes, %d moments, up to %d trucks at once, %,d truck-frames): off a drawn road %d; past their ramp and on a highway again %d; "
                + "fading in anywhere but a world part or a gate %d; standing off their gate %d of %,d; trips not ending at a gate %d; home trips not starting at one %d%n",
                s.label, routes, frames, maxAtOnce, trucks, off, past, fadeBad, standingAway, standing, endsBad, startsBad);
    }

    /** (1) One world trip followed whole, its truck at 8 moments of its visit. */
    static void trip(CityMap m, Scene s, Trip t, File file) throws Exception {
        TruckSchedule.Route r = t.r;
        double[] b = TruckSchedule.box(r);
        int margin = 22;
        long x0 = (long) Math.floor(b[0]) - margin, y0 = (long) Math.floor(b[1]) - margin;
        int w = (int) (Math.ceil(b[2]) + margin - x0), h = (int) (Math.ceil(b[3]) + margin - y0);
        double px = Math.min(1600.0 / w, 1450.0 / h);
        View v = open(m, x0, y0, w, h, px);
        stroke(v, r, new Color(255, 225, 60, 235), 1.6f);

        double a = 0.5, leg = r.legSeconds() / TruckSchedule.MONTH_SECONDS, dwell = TruckSchedule.DWELL_SECONDS / TruckSchedule.MONTH_SECONDS, len = r.length();
        TruckSchedule.Call call = new TruckSchedule.Call(TruckSchedule.Load.IMPORT, 0, a);
        double hw1 = t.dLine + 0.35 * (t.dRamp - t.dLine), hw2 = t.dLine + 0.7 * (t.dRamp - t.dLine);
        double st1 = t.dRamp + 0.35 * (t.dEnd - t.dRamp), st2 = t.dRamp + 0.7 * (t.dEnd - t.dRamp);
        // {word, phase (0 running in, 1 at the gate, 2 running out), distance along}
        Object[][] moments = {
            { "on the world line, fading in", 0, 24.0 },
            { "on the highway", 0, hw1 },
            { "at the ramp: off the highway", 0, t.dRamp },
            { "on the street leg", 0, st1 },
            { "road's end: down the access road", 0, t.dEnd },
            { "at the gate, loaded as it came", 1, len },
            { "back out: street leg, empty", 2, st2 },
            { "back out: highway, empty", 2, hw2 },
        };
        StringBuilder ts = new StringBuilder();
        List<Shown> shown = new ArrayList<>();
        // The marks first, so the trucks and their rings lie over them: the diamonds at the line's point, the ramp, the road's end and the gate.
        mark(v, r.xs()[1], r.ys()[1], "the line's point: the city's edge", WHITE);
        mark(v, r.xs()[t.ramp], r.ys()[t.ramp], null, GREEN);
        mark(v, t.end[0], t.end[1], null, CYAN);
        mark(v, r.xs()[r.xs().length - 1], r.ys()[r.xs().length - 1], null, RED);
        for (int k = 0; k < moments.length; k++) {
            int ph = (Integer) moments[k][1];
            double d = ((Number) moments[k][2]).doubleValue();
            double tm = ph == 0 ? a - leg * (1 - d / len) : ph == 1 ? a + dwell * 0.25 : a + dwell + leg * (1 - d / len);
            TruckSchedule.Truck tk = TruckSchedule.position(call, r, tm);
            if (tk == null) throw new IllegalStateException("moment " + (k + 1) + " is off the road");
            shown.add(new Shown(tk, r, t.dt.district(), d));
            truckBig(v, tk);
            ring(v, tk.x(), tk.y(), 15, WHITE);
            double nx = -Math.sin(tk.heading()), ny = Math.cos(tk.heading());
            double side = k < 5 ? 1 : -1;
            label(v, (k + 1) + "  " + moments[k][0], v.sx(tk.x()), v.sy(tk.y()), nx * side, ny * side, 26, WHITE, false);
            ts.append(String.format("%d: t = %.3f, %.0f plots along, alpha %.2f, %s; ", k + 1, tm, d, tk.alpha(), tk.loaded() ? "loaded" : "empty"));
        }
        int off = offRoad(m, shown, "trip");
        offTotal += off;
        String caption = String.format("FR9 - ONE WORLD TRIP FOLLOWED WHOLE. %s: from the highways' world line %.0f plots out (its far end, top left), in over the line's point (white diamond: the city's edge), "
                + "%.0f plots along the highways to a ramp (green diamond), %.0f plots of street (%d segments) to an access road's end (cyan), %.0f plots down the road to the works' gate (red) - %.0f plots in all, "
                + "run in %.1f s, 2 s standing at the gate, run out again. Drawn at %.2f px a plot (%d x %d plots); the yellow line is the trip's route. "
                + "One truck of an IMPORT at 8 moments of its visit (it reaches the gate at t = %.2f of the month): loaded blue coming in, at the gate, grey going out. "
                + "THE TRUCK IS DRAWN ABOUT %.0f PX LONG, 4 times the game's size (%.1f px at this scale, where TruckShapes.MIN_PX keeps it %.0f), so that it can be told; it fades in over the world line's first %.0f plots, "
                + "and the same road takes it back, so it never stops or turns inside the city. All 8 on a drawn road: %s.\n%s\n%s",
                t.name(), TruckSchedule.WORLD_PLOTS, t.dRamp - t.dLine, t.dEnd - t.dRamp, t.at - t.ramp, t.dGate - t.dEnd, len, r.legSeconds(), v.px, w, h, a,
                BIG_TRUCK_PX, Math.max(TruckShapes.MIN_PX, TruckShapes.LENGTH * v.px), TruckShapes.MIN_PX, TruckSchedule.WORLD_PLOTS,
                off == 0 ? "yes, every one" : off + " are not", ts, monthWords(s));
        finish(v, List.of(), file, caption);
    }

    /** (2) The ramp where the trip leaves the highway for the streets, every truck of every schedule at one moment, 8 px a plot. */
    static void ramp(CityMap m, Scene s, Trip t, File file) throws Exception {
        double px = MapFrame.BIG_TILE_PX;
        int w = 140, h = 90;
        double rx = t.r.xs()[t.ramp], ry = t.r.ys()[t.ramp];
        long x0 = Math.round(rx - w / 2.0), y0 = Math.round(ry - h / 2.0);
        // The moment that has the most trucks on the streets about the ramp as well as on the highway.
        double tm = busiest(s, x0, y0, w, h, px, sh -> offHighway(m, sh) && Math.abs(sh.truck().x() - rx) < 40 && Math.abs(sh.truck().y() - ry) < 40, 70)[0];
        View v = open(m, x0, y0, w, h, px);
        stroke(v, t.r, new Color(255, 225, 60, 150), 1.2f);
        List<Shown> on = s.shown(x0, y0, w, h, px, tm);
        trucks(v, on);
        ring(v, rx, ry, 16, GREEN);
        label(v, "ramp: trips leave the highway for the streets", v.sx(rx), v.sy(ry), 1, 0, 22, GREEN, true);
        // The insets: the ramp, then the streets' and the highway's busiest places.
        List<Spot> fixed = List.of(new Spot(rx, ry, "the ramp"));
        int count = (v.W + 6) / (INSET_PX + 6);
        List<Spot> all = new ArrayList<>(fixed);
        all.addAll(spots(on, fixed, count - fixed.size(), 5, 14, List.<Predicate<Shown>>of(sh -> offHighway(m, sh), sh -> onHighway(m, sh.truck())), List.of("on the streets", "on the highway")));
        List<BufferedImage> ins = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) ins.add(inset(s, v, tm, all.get(i), String.valueOf((char) ('A' + i))));
        int off = offRoad(m, on, "ramp");
        offTotal += off;
        String caption = String.format("FR9 - THE RAMP at world plot (%.1f, %.1f), where %s leaves the highway (the highway runs down from the north; this trip turns east along the street, the roads of the district to the west leave west). "
                + "%.0f px a plot, t = %.3f of the month, window %d x %d plots from plot %d,%d; the yellow line is that one trip's route. %s All on a drawn road (highway, street or access road): %s. "
                + "Insets A-%c: the same moment at %dx (%.0f px a plot), boxed on the picture.\n%s",
                rx, ry, t.name(), px, tm, w, h, x0, y0, words(on, tm), off == 0 ? "yes, every one" : off + " are not", (char) ('A' + all.size() - 1), INSET_ZOOM, px * INSET_ZOOM, monthWords(s));
        finish(v, ins, file, caption);
    }

    /** (3) The street leg from the ramp to the access road's end and down to the gate, every truck at one moment, 8 px a plot. */
    static void streetLeg(CityMap m, Scene s, Trip t, File file) throws Exception {
        double px = MapFrame.BIG_TILE_PX;
        TruckSchedule.Route r = t.r;
        double[] b = TruckSchedule.box(cut(r, t.ramp));
        int margin = 12;
        long x0 = (long) Math.floor(b[0]) - margin, y0 = (long) Math.floor(b[1]) - margin;
        int w = (int) (Math.ceil(b[2]) + margin - x0), h = (int) (Math.ceil(b[3]) + margin - y0);
        double tm = busiest(s, x0, y0, w, h, px, sh -> offHighway(m, sh), 30)[0];
        View v = open(m, x0, y0, w, h, px);
        stroke(v, cut(r, t.ramp), new Color(255, 225, 60, 130), 1.2f);
        List<Shown> on = s.shown(x0, y0, w, h, px, tm);
        trucks(v, on);
        mark(v, r.xs()[t.ramp], r.ys()[t.ramp], "ramp", GREEN);
        mark(v, t.end[0], t.end[1], "access road's end", CYAN);
        mark(v, r.xs()[r.xs().length - 1], r.ys()[r.xs().length - 1], "gate", RED);
        List<Spot> fixed = List.of(new Spot(t.end[0], t.end[1], "the road's end"), new Spot(r.xs()[r.xs().length - 1], r.ys()[r.xs().length - 1] + 3, "the gate"));
        int count = Math.min(5, (v.W + 6) / (INSET_PX + 6));
        List<Spot> all = new ArrayList<>(fixed);
        all.addAll(spots(on, fixed, count - fixed.size(), 5, 14, List.<Predicate<Shown>>of(sh -> offHighway(m, sh)), List.of("on the street leg")));
        List<BufferedImage> ins = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) ins.add(inset(s, v, tm, all.get(i), String.valueOf((char) ('A' + i))));
        int off = offRoad(m, on, "street");
        offTotal += off;
        String caption = String.format("FR9 - THE STREET LEG of %s: from the ramp at (%.1f, %.1f) %.0f plots along the street (%d segments) to the access road's end at (%.1f, %.1f), then %.0f plots down the road to the gate at (%.1f, %.1f). "
                + "%.0f px a plot, t = %.3f of the month, window %d x %d plots from plot %d,%d; the yellow line is the trip's route from the ramp. %s All on a drawn road: %s. "
                + "Insets A-%c: the same moment at %dx (%.0f px a plot), boxed on the picture.\n%s",
                t.name(), r.xs()[t.ramp], r.ys()[t.ramp], t.dEnd - t.dRamp, t.at - t.ramp, t.end[0], t.end[1], t.dGate - t.dEnd, r.xs()[r.xs().length - 1], r.ys()[r.xs().length - 1],
                px, tm, w, h, x0, y0, words(on, tm), off == 0 ? "yes, every one" : off + " are not", (char) ('A' + all.size() - 1), INSET_ZOOM, px * INSET_ZOOM, monthWords(s));
        finish(v, ins, file, caption);
    }

    /** (4) A home trip from one works' gate to another's, its route faint, its truck at 4 moments, 8 px a plot. */
    static void home(CityMap m, Scene s, Trip t, File file) throws Exception {
        double px = MapFrame.BIG_TILE_PX;
        TruckSchedule.Route r = t.r;
        double[] b = TruckSchedule.box(r);
        int margin = 14;
        long x0 = (long) Math.floor(b[0]) - margin, y0 = (long) Math.floor(b[1]) - margin;
        int w = Math.max(110, (int) (Math.ceil(b[2]) + margin - x0)), h = Math.max(60, (int) (Math.ceil(b[3]) + margin - y0));
        View v = open(m, x0, y0, w, h, px);
        stroke(v, r, new Color(255, 225, 60, 235), 1.6f);
        int n = r.xs().length;
        double[] from = { r.xs()[0], r.ys()[0] }, to = { r.xs()[n - 1], r.ys()[n - 1] };
        mark(v, from[0], from[1], null, RED);
        mark(v, to[0], to[1], null, RED);
        double a = 0.5, leg = r.legSeconds() / TruckSchedule.MONTH_SECONDS, dwell = TruckSchedule.DWELL_SECONDS / TruckSchedule.MONTH_SECONDS, len = r.length();
        TruckSchedule.Call call = new TruckSchedule.Call(TruckSchedule.Load.HOME, 0, a);
        Object[][] moments = {
            { "out of the partner's gate, fading in", 0, Math.min(1.5, len) },
            { "on its way, loaded", 0, 0.62 * len },
            { "at this works' gate, loaded as it came", 1, len },
            { "back out, empty", 2, 0.35 * len },
        };
        List<Shown> shown = new ArrayList<>();
        StringBuilder ts = new StringBuilder();
        for (int k = 0; k < moments.length; k++) {
            int ph = (Integer) moments[k][1];
            double d = ((Number) moments[k][2]).doubleValue();
            double tm = ph == 0 ? a - leg * (1 - d / len) : ph == 1 ? a + dwell * 0.25 : a + dwell + leg * (1 - d / len);
            TruckSchedule.Truck tk = TruckSchedule.position(call, r, tm);
            truckBig(v, tk);
            shown.add(new Shown(tk, r, t.dt.district(), d));
            ring(v, tk.x(), tk.y(), 15, WHITE);
            double nx = -Math.sin(tk.heading()), ny = Math.cos(tk.heading());
            label(v, (k + 1) + "  " + moments[k][0], v.sx(tk.x()), v.sy(tk.y()), nx * (k < 2 ? 1 : -1), ny * (k < 2 ? 1 : -1), 26, WHITE, false);
            ts.append(String.format("%d: t = %.3f, %.1f plots along, alpha %.2f, %s; ", k + 1, tm, d, tk.alpha(), tk.loaded() ? "loaded" : "empty"));
        }
        int off = offRoad(m, shown, "home");
        offTotal += off;
        String caption = String.format("FR9 - A HOME TRIP (tonnes bought at home, violet): %s, %.0f plots over %d points, from the gate of one works at (%.1f, %.1f) out along its access road to the street, along the streets, "
                + "and in along the other works' access road to its gate at (%.1f, %.1f) (red diamonds; the yellow line is the route). "
                + "%.0f px a plot (%d x %d plots from plot %d,%d). One truck at 4 moments of its visit (it reaches the gate at t = %.2f): fading in over the first %.0f plots out of the partner's gate, "
                + "loaded violet in, standing at the gate, grey and empty going back out. THE TRUCK IS DRAWN ABOUT %.0f PX LONG, 4 times the game's size (%.1f px at this scale). All 4 on a drawn road: %s.\n%s\n%s",
                t.name(), len, n, from[0], from[1], to[0], to[1], px, w, h, x0, y0, a, r.fadeTo(), BIG_TRUCK_PX, Math.max(TruckShapes.MIN_PX, TruckShapes.LENGTH * px),
                off == 0 ? "yes, every one" : off + " are not", ts, monthWords(s));
        finish(v, List.of(), file, caption);
    }

    /** (5) The near view's far end, MapFrame.L0_FROM px a plot, every truck of every schedule at one moment, the busy month. */
    static void nearFar(CityMap m, Scene s, File file) throws Exception {
        double px = MapFrame.L0_FROM;
        double ax = Double.MAX_VALUE, ay = Double.MAX_VALUE, bx = -Double.MAX_VALUE, by = -Double.MAX_VALUE;
        for (CityMap.DistrictTrips dt : s.trips) {
            for (TruckSchedule.Route r : dt.trips()) {
                double[] b = TruckSchedule.box(r);
                ax = Math.min(ax, b[0]); ay = Math.min(ay, b[1]); bx = Math.max(bx, b[2]); by = Math.max(by, b[3]);
            }
        }
        int margin = 10;
        long x0 = (long) Math.floor(ax) - margin, y0 = (long) Math.floor(ay) - margin;
        int w = (int) (Math.ceil(bx) + margin - x0), h = (int) (Math.ceil(by) + margin - y0);
        double tm = busiest(s, x0, y0, w, h, px, sh -> sh.route().home() || offHighway(m, sh), 20)[0];
        View v = open(m, x0, y0, w, h, px);
        List<Shown> on = s.shown(x0, y0, w, h, px, tm);
        trucks(v, on);
        int count = Math.min(4, (v.W + 6) / (INSET_PX + 6));
        List<Spot> all = spots(on, List.of(), count, 12, 30,
                List.<Predicate<Shown>>of(sh -> sh.route().home(), sh -> offHighway(m, sh) && !sh.route().home(), sh -> onHighway(m, sh.truck()), sh -> sh.truck().alpha() < 1 && !sh.route().home()),
                List.of("home trips", "on the streets", "on the highway", "fading in on the world line"));
        List<BufferedImage> ins = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) ins.add(inset(s, v, tm, all.get(i), String.valueOf((char) ('A' + i))));
        int off = offRoad(m, on, "near-far");
        offTotal += off;
        String caption = String.format("FR9 - THE NEAR VIEW'S FAR END: %.1f px a plot (MapFrame.L0_FROM), where trucks are drawn at TruckShapes.MIN_PX = %.0f px long, their shape kept (a true-size truck is %.1f px here). "
                + "Every truck of every district's schedule in the BUSY month, t = %.3f; the window holds all of the trips city's trips, %d x %d plots from plot %d,%d. %s All on a drawn road: %s. "
                + "Insets A-%c: the same moment at %dx (%.1f px a plot), boxed on the picture.\n%s",
                px, TruckShapes.MIN_PX, TruckShapes.LENGTH * px, tm, w, h, x0, y0, words(on, tm), off == 0 ? "yes, every one" : off + " are not", (char) ('A' + all.size() - 1), INSET_ZOOM, px * INSET_ZOOM, monthWords(s));
        finish(v, ins, file, caption);
    }

    /** The route from point `from` to its end, as a route (for its box and its stroke). */
    static TruckSchedule.Route cut(TruckSchedule.Route r, int from) {
        return TruckSchedule.Route.through(true, Arrays.copyOfRange(r.xs(), from, r.xs().length), Arrays.copyOfRange(r.ys(), from, r.ys().length), 0);
    }

    // ------------------------------------------------------------------------------------------------------------------------ helpers

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

    static Color colour(int argb, double alpha) {
        return new Color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, (int) Math.round(255 * Math.max(0, Math.min(1, alpha))));
    }
}
