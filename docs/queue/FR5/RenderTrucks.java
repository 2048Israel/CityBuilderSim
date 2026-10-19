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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Scratch, not in the build: FR5 slice D's headless renders of the trucks. The
 * city is TruckRouteCheck's access city (MapCheck's, a city with highways that
 * reach a street and districts with access roads to yards and Truck Depots),
 * its tiles painted by the game's own TileRaster through MapTiles, the trucks
 * over them in Java2D from the schedules' shapes exactly as ui/MapView.trucks()
 * draws them: the highways' schedule (share 1, MapView.HIGHWAYS_SALT) and each
 * drawn district's (its routes' part of accessRoadCount(), its key as salt),
 * each truck filled with TruckShapes.body(truck, px a plot) in TruckShapes.colour()
 * at the truck's alpha, and the highways' world line as MapView strokes it.
 * Writes PNGs into args[0]. Never touches saves, never makes a Game.
 *
 *   javac -cp <classes> -d <scratch> docs/queue/FR5/RenderTrucks.java
 *   java -Djava.awt.headless=true -cp "<scratch>;<classes>;<gson.jar>" ham.citybuildersim.RenderTrucks docs/queue/FR5
 */
public class RenderTrucks {
    static final int CAPTION_H = 104;

    /** The ordinary month's loads: 1,000 in all, so N is 1 (TruckSchedule.MOST_A_MONTH is the most drawn one for one). */
    static final double[] MONTH = { 500, 250, 250 };

    /** The big city's month: 60,000 loads, N = 100 (60,000 / 100 = 600 trucks; a month never draws more than 1,000 whatever N, so a big city's roads are no denser than the ordinary month's, only read for more loads). */
    static final double[] BIG = { 30000, 15000, 15000 };

    static final int MONTH_NUMBER = 3;

    /** One truck drawn: its place and which schedule and route it came from. */
    record Shown(TruckSchedule.Truck truck, TruckSchedule.Route route, boolean highway, CityMap.District district) { }

    /** The city's schedules for one month of loads: the highways' and each drawn district's, made as MapView makes them. */
    static final class Scene {
        final CityMap m;
        final double[] loads;
        final String label;
        final TruckSchedule highways;
        final Map<Long, TruckSchedule> districts = new HashMap<>();
        final Map<Long, CityMap.District> byKey = new HashMap<>();

        Scene(CityMap m, double[] loads, String label) {
            this.m = m;
            this.loads = loads;
            this.label = label;
            highways = TruckSchedule.of(MONTH_NUMBER, loads, m.truckRoutes(), 1, HIGHWAYS_SALT);
            for (CityMap.District d : m.districts()) byKey.put(d.key, d);
        }

        int stands() {
            int n = highways.stands();
            for (TruckSchedule s : districts.values()) n = Math.max(n, s.stands());
            return Math.max(1, n);
        }

        /** The trucks over the window [x0, x0 + w) x [y0, y0 + h) at t, whose middle lies in it, as MapView.trucks() frames them (a truck's length past the edge). */
        List<Shown> shown(long x0, long y0, int w, int h, double px, double t) {
            double margin = Math.max(TruckShapes.LENGTH, TruckShapes.MIN_PX / px);
            long bx0 = (long) Math.floor(x0 - margin), by0 = (long) Math.floor(y0 - margin);
            long bx1 = (long) Math.ceil(x0 + w + margin), by1 = (long) Math.ceil(y0 + h + margin);
            List<Shown> out = new ArrayList<>();
            if (!m.truckRoutes().isEmpty() && highways.callCount() > 0 && highways.crosses(bx0, by0, bx1, by1)) {
                for (TruckSchedule.Truck tk : highways.frame(bx0, by0, bx1, by1, t)) {
                    if (inside(tk, x0, y0, w, h)) out.add(new Shown(tk, highways.routes().get(tk.call().route()), true, null));
                }
            }
            for (CityMap.DistrictRoutes dr : m.accessRoutesIn(bx0, by0, bx1, by1)) {
                TruckSchedule s = districts.get(dr.key());
                if (s == null) {
                    double share = dr.routes().size() / (double) m.accessRoadCount();
                    s = TruckSchedule.of(MONTH_NUMBER, loads, dr.routes(), share, dr.key());
                    districts.put(dr.key(), s);
                }
                if (s.callCount() == 0 || !s.crosses(bx0, by0, bx1, by1)) continue;
                for (TruckSchedule.Truck tk : s.frame(bx0, by0, bx1, by1, t)) {
                    if (inside(tk, x0, y0, w, h)) out.add(new Shown(tk, s.routes().get(tk.call().route()), false, byKey.get(dr.key())));
                }
            }
            return out;
        }

        static boolean inside(TruckSchedule.Truck tk, long x0, long y0, int w, int h) {
            return tk.x() >= x0 && tk.x() < x0 + w && tk.y() >= y0 && tk.y() < y0 + h;
        }
    }

    /** MapView.HIGHWAYS_SALT (package-private in ui): the highways' schedule's salt. */
    static final long HIGHWAYS_SALT = 0x7472_7563_6b73_0001L;

    public static void main(String[] args) throws Exception {
        File outDir = new File(args.length > 0 ? args[0] : ".");
        PrintStream real = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        BuildingVisual.Type[] types = TruckRouteCheck.types();
        CityMap m = TruckRouteCheck.accessCity(types);
        // The painter's Drawn plans, drawn before the access roads are asked for (they are never drawn by asking).
        for (CityMap.District d : m.districts()) m.drawn(d);
        System.setOut(real);
        CityRuns runs = m.runs();
        CityMap.WorldLine line = null;
        for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.HIGHWAYS) line = l;
        List<TruckSchedule.Route> hw = m.truckRoutes();
        List<CityMap.DistrictRoutes> access = allAccess(m);
        real.printf("access city: site %s, %d districts, %d highway routes, %d ramps, highway hub %s, world line %s, accessRoadCount %d%n",
                java.util.Arrays.toString(m.site()), m.districts().size(), hw.size(), runs.highwayRamps().size(),
                java.util.Arrays.toString(runs.highways().hub()), line, m.accessRoadCount());
        for (CityMap.DistrictRoutes dr : access) {
            for (TruckSchedule.Route r : dr.routes()) real.printf("  access route of district %d: %d points, %.0f plots, (%.1f,%.1f) to (%.1f,%.1f)%n", dr.key(),
                    r.xs().length, r.length(), r.xs()[0], r.ys()[0], r.xs()[r.xs().length - 1], r.ys()[r.xs().length - 1]);
        }
        if (line == null || hw.isEmpty() || access.isEmpty()) return;

        Scene month = new Scene(m, MONTH, String.format("a month of %,.0f import, %,.0f export and %,.0f home loads", MONTH[0], MONTH[1], MONTH[2]));
        Scene big = new Scene(m, BIG, String.format("a big city's month of %,.0f import, %,.0f export and %,.0f home loads", BIG[0], BIG[1], BIG[2]));
        int px8 = MapFrame.BIG_TILE_PX, w = 112, h = 75;
        real.printf("month: %s, %d highway trucks; big: %s, %d highway trucks%n", month.highways, month.highways.callCount(), big.highways, big.highways.callCount());

        // (a) a stretch of highway near a ramp: the route and the window (centred 35 plots in from the ramp, along the route) with the most trucks both ways.
        long[] hwWin = null;
        double hwT = 0, hwScore = -1;
        for (TruckSchedule.Route r : hw) {
            double[] c = pointAlong(r, Math.max(0, r.length() - 35));
            long x0 = Math.round(c[0]) - w / 2, y0 = Math.round(c[1]) - h / 2;
            double[] best = busiest(month, x0, y0, w, h, px8, HIGHWAYS_ONLY);
            if (best[1] > hwScore) { hwScore = best[1]; hwT = best[0]; hwWin = new long[] { x0, y0 }; }
        }
        render(month, hwWin[0], hwWin[1], w, h, px8, hwT, new File(outDir, "fr5-highway.png"),
                "FR5 - a stretch of highway near a ramp at 8 px a plot, t = %.3f of the month", "trucks both ways on the highways' routes: loaded imports blue, loaded exports orange, empty trucks grey");

        // (b) an access road from its end to its gate: the shortest road whose whole route fits the window, the window centred on its box.
        long[] acWin = null;
        double acT = 0, acScore = -1;
        for (CityMap.DistrictRoutes dr : access) {
            for (TruckSchedule.Route r : dr.routes()) {
                double[] b = TruckSchedule.box(r);
                if (b[2] - b[0] > w - 12 || b[3] - b[1] > h - 12) continue;
                long x0 = Math.round((b[0] + b[2]) / 2) - w / 2, y0 = Math.round((b[1] + b[3]) / 2) - h / 2;
                double[] best = busiest(month, x0, y0, w, h, px8, WITH_HOME);
                if (best[1] > acScore) { acScore = best[1]; acT = best[0]; acWin = new long[] { x0, y0 }; }
            }
        }
        if (acWin == null) {
            // No whole road fits: the last 35 plots of the first road, its gate.
            TruckSchedule.Route r = access.get(0).routes().get(0);
            double[] c = pointAlong(r, Math.max(0, r.length() - 35));
            acWin = new long[] { Math.round(c[0]) - w / 2, Math.round(c[1]) - h / 2 };
            acT = busiest(month, acWin[0], acWin[1], w, h, px8, WITH_HOME)[0];
        }
        render(month, acWin[0], acWin[1], w, h, px8, acT, new File(outDir, "fr5-access.png"),
                "FR5 - an access road from its end on the street grid to its gate at 8 px a plot, t = %.3f of the month", "violet = a tonne bought at home (works to works), the rest as on the highways");

        // (c) the gateway: the window over the first leg of a highway route, the world part (64 plots) fading in along the world line.
        TruckSchedule.Route g0 = hw.get(0);
        double[] gc = pointAlong(g0, TruckSchedule.WORLD_PLOTS * 0.55);
        int gh = 112;
        long gx0 = Math.round(gc[0]) - w / 2, gy0 = Math.round(gc[1]) - gh / 2;
        double gt = busiest(month, gx0, gy0, w, gh, px8, HIGHWAYS_ONLY)[0];
        render(month, gx0, gy0, w, gh, px8, gt, new File(outDir, "fr5-gateway.png"),
                "FR5 - the highway gateway at 8 px a plot, t = %.3f of the month", String.format("each truck fades in over the world line's first %.0f plots (alpha = distance / fadeTo), full by the line's point", TruckSchedule.WORLD_PLOTS));

        // (d) the near view's far end: L0_FROM px a plot, the trucks drawn at MIN_PX long. Centred between the highway hub and the access roads.
        double px3 = MapFrame.L0_FROM;
        int fw = 280, fh = 190;
        long[] hub = runs.highways().hub();
        double[] a0 = TruckSchedule.box(access.get(0).routes().get(0));
        long fcx = Math.round((hub[0] + (a0[0] + a0[2]) / 2) / 2), fcy = Math.round((hub[1] + (a0[1] + a0[3]) / 2) / 2);
        long fx0 = fcx - fw / 2, fy0 = fcy - fh / 2;
        double ft = busiest(month, fx0, fy0, fw, fh, px3, ALL)[0];
        render(month, fx0, fy0, fw, fh, px3, ft, new File(outDir, "fr5-near-far.png"),
                "FR5 - the near view's far end, L0_FROM = 3.2 px a plot, t = %.3f of the month", String.format("a true-size truck is %.1f px long here; drawn at least TruckShapes.MIN_PX = %.0f px, its shape kept", TruckShapes.LENGTH * px3, TruckShapes.MIN_PX));

        // (e) the big city's month on the highway: the busiest stretch of the spine every route shares (the first route's way in from the world line), N > 1.
        TruckSchedule.Route spine = hw.get(0);
        long[] busyWin = null;
        double busyT = 0, busyScore = -1;
        for (double d = TruckSchedule.WORLD_PLOTS + 10; d <= Math.min(spine.length() - 40, 500); d += 20) {
            double[] c = pointAlong(spine, d);
            long x0 = Math.round(c[0]) - w / 2, y0 = Math.round(c[1]) - h / 2;
            double[] best = busiest(big, x0, y0, w, h, px8, HIGHWAYS_ONLY);
            if (best[1] > busyScore) { busyScore = best[1]; busyT = best[0]; busyWin = new long[] { x0, y0 }; }
        }
        render(big, busyWin[0], busyWin[1], w, h, px8, busyT, new File(outDir, "fr5-busy.png"),
                "FR5 - the big city's month on the highways' shared stretch at 8 px a plot, t = %.3f of the month", "every truck stands for N loads (the legend's words below), and a month is never drawn with more than 1,000 trucks");
    }

    /** Every drawn district's access routes, over the whole city's ground. */
    static List<CityMap.DistrictRoutes> allAccess(CityMap m) {
        long[] lo = { Long.MAX_VALUE, Long.MAX_VALUE }, hi = { Long.MIN_VALUE, Long.MIN_VALUE };
        for (CityMap.District d : m.districts()) {
            long[] o = TruckRouteCheck.origin(m, d);
            lo[0] = Math.min(lo[0], o[0]); lo[1] = Math.min(lo[1], o[1]);
            hi[0] = Math.max(hi[0], o[0] + CityMap.DISTRICT); hi[1] = Math.max(hi[1], o[1] + CityMap.DISTRICT);
        }
        return m.accessRoutesIn(lo[0] - 10, lo[1] - 10, hi[0] + 10, hi[1] + 10);
    }

    /** The point `d` plots along a route, {x, y}. */
    static double[] pointAlong(TruckSchedule.Route r, double d) {
        int last = r.xs().length - 1;
        if (last < 1) return new double[] { r.xs()[0], r.ys()[0] };
        d = Math.max(0, Math.min(r.length(), d));
        int lo = 0;
        while (lo < last - 1 && r.along()[lo + 1] <= d) lo++;
        double seg = r.along()[lo + 1] - r.along()[lo], u = seg > 0 ? (d - r.along()[lo]) / seg : 0;
        return new double[] { r.xs()[lo] + (r.xs()[lo + 1] - r.xs()[lo]) * u, r.ys()[lo] + (r.ys()[lo + 1] - r.ys()[lo]) * u };
    }

    /** Whether a truck is on its way in (before it reaches its gate), at t. */
    static boolean inbound(TruckSchedule.Truck tk, double t) { return t < tk.call().arrives(); }

    static boolean outbound(TruckSchedule.Truck tk, double t) {
        return t > tk.call().arrives() + TruckSchedule.DWELL_SECONDS / TruckSchedule.MONTH_SECONDS;
    }

    /** What busiest() counts: every truck, the highways' alone, or every truck with a bonus for a home load among them. */
    static final int ALL = 0, HIGHWAYS_ONLY = 1, WITH_HOME = 2;

    /**
     * The t, on a fine grid of the month, whose window shows the most trucks of the most colours going both ways (and, for WITH_HOME, some home load
     * among them): {t, score}.
     */
    static double[] busiest(Scene s, long x0, long y0, int w, int h, double px, int focus) {
        double best = 0.5, bestScore = -1;
        for (int k = 0; k < 2000; k++) {
            double t = (k + 0.5) / 2000;
            List<Shown> on = s.shown(x0, y0, w, h, px, t);
            if (focus == HIGHWAYS_ONLY) on.removeIf(sh -> !sh.highway());
            Set<Integer> colours = new HashSet<>();
            boolean in = false, out = false, hm = false;
            for (Shown sh : on) {
                colours.add(TruckShapes.colour(sh.truck()));
                in |= inbound(sh.truck(), t);
                out |= outbound(sh.truck(), t);
                hm |= sh.truck().call().load() == TruckSchedule.Load.HOME;
            }
            double score = on.size() * 10 + 40 * colours.size() + (in && out ? 40 : 0) + (focus == WITH_HOME && hm ? 60 : 0);
            if (score > bestScore) { bestScore = score; best = t; }
        }
        return new double[] { best, bestScore };
    }

    /** The counts in words: trucks by load, running full or empty, and how many stand at a gate. */
    static String words(List<Shown> on, double t) {
        int[] by = new int[TruckSchedule.Load.values().length];
        int full = 0, empty = 0, standing = 0, in = 0, out = 0;
        for (Shown sh : on) {
            by[sh.truck().call().load().ordinal()]++;
            if (sh.truck().loaded()) full++; else empty++;
            if (inbound(sh.truck(), t)) in++; else if (outbound(sh.truck(), t)) out++; else standing++;
        }
        int hwy = 0;
        for (Shown sh : on) if (sh.highway()) hwy++;
        return String.format("%d trucks in view (%d on the highways, %d on access roads): %d import, %d export, %d home | %d loaded and %d empty; %d running in, %d running out, %d standing at a gate",
                on.size(), hwy, on.size() - hwy, by[0], by[1], by[2], full, empty, in, out, standing);
    }

    /** The trucks' lines of the legend as MapView.trucksWords() words them: "Trucks: one per load" for one, "Trucks: one per 1,200 loads" for more. */
    static String trucksWords(int loads) {
        return String.format("Trucks: one per %s", loads <= 1 ? "load" : String.format("%,d loads", loads));
    }

    /** Off-road check of the trucks drawn: how many lie off the road they drive (the world part of a highway route, before its line's point, is exempt). */
    static int offRoad(CityMap m, List<Shown> on) {
        int off = 0;
        for (Shown sh : on) {
            TruckSchedule.Truck tk = sh.truck();
            long px = (long) Math.floor(tk.x()), py = (long) Math.floor(tk.y());
            boolean bad;
            if (sh.highway()) {
                bad = tk.alpha() >= 1 && !CityRuns.covered(m.runs().highways(), px, py);
            } else {
                long[] o = TruckRouteCheck.origin(m, sh.district());
                bad = TruckRouteCheck.notAccess(m.drawn(sh.district()), px - o[0], py - o[1]);
            }
            if (bad) {
                off++;
                System.out.printf("      off its road: %s truck at (%.2f, %.2f), plot (%d, %d), alpha %.2f, heading %.2f%n", sh.highway() ? "highway" : "access", tk.x(), tk.y(), px, py, tk.alpha(), tk.heading());
            }
        }
        return off;
    }

    /** The window's tiles at px a plot, the highways' world line and the trucks at t over them, captioned. */
    static void render(Scene s, long x0, long y0, int w, int h, double px, double t, File file, String titleFormat, String what) throws Exception {
        CityMap m = s.m;
        int level = MapFrame.levelOf(px);
        int tpx = MapFrame.tilePx(level, px);
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        int tside = World.TILE * tpx;
        int[] buf = new int[tside * tside];
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
        int W = (int) Math.round(w * px), H = (int) Math.round(h * px);
        BufferedImage img = new BufferedImage(W, H + CAPTION_H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(tilesImg, 0, 0, W, H, null);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(0, 0, W, H);
        // The world lines, as MapView strokes them (RenderFr7's): the highways' solid, the railway's dashed.
        Font labelFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);
        for (CityMap.WorldLine line : m.worldLines()) {
            boolean highway = line.net() == CityRuns.HIGHWAYS;
            double r = Math.toRadians(line.way());
            double lx = (line.x() - x0) * px, ly = (line.y() - y0) * px;
            double ex = lx + Math.cos(r) * line.reach() * px, ey = ly + Math.sin(r) * line.reach() * px;
            g.setColor(colour(highway ? TileRaster.ROAD[BuildingVisual.HIGHWAY] : TileRaster.RAIL_DARK, LandMap.WORLD_ALPHA));
            float wd = (float) (highway ? Math.max(2, 0.86 * px) : Math.max(1.5, 0.52 * px));
            g.setStroke(highway ? new BasicStroke(wd) : new BasicStroke(wd, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] { 6, 5 }, 0));
            g.draw(new java.awt.geom.Line2D.Double(lx, ly, ex, ey));
            double[] cut = clip(lx, ly, ex, ey, W, H);
            if (cut == null) continue;
            java.awt.FontMetrics fm = g.getFontMetrics(labelFont);
            double[] at = LandMap.worldLabelAt(cut[0], cut[1], cut[2], cut[3], fm.stringWidth(LandMap.WORLD_WORDS), fm.getAscent(), W, H);
            if (at == null) continue;
            g.setFont(labelFont);
            g.setColor(new Color(255, 255, 255, (int) Math.round(255 * LandMap.WORLD_ALPHA)));
            g.drawString(LandMap.WORLD_WORDS, (float) at[0], (float) at[1]);
        }
        // The trucks, as MapView.fillTrucks() fills them.
        List<Shown> on = s.shown(x0, y0, w, h, px, t);
        for (Shown sh : on) {
            TruckSchedule.Truck tk = sh.truck();
            fill(g, TruckShapes.body(tk, px), x0, y0, px, colour(TruckShapes.colour(tk), tk.alpha()));
        }
        g.setClip(null);
        g.setColor(new Color(0x16222c));
        g.fillRect(0, H, W, CAPTION_H);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.setColor(new Color(0xe8e8e8));
        int n = s.stands();
        String caption = String.format(titleFormat, t) + " | " + what
                + " | " + words(on, t)
                + " | " + s.label + " = " + String.format("%,.0f", s.loads[0] + s.loads[1] + s.loads[2]) + " loads, N = " + n + ", legend: \"" + trucksWords(n) + "\""
                + " | window " + w + " x " + h + " plots from plot " + x0 + "," + y0 + " (the access city: TruckRouteCheck.accessCity())";
        String[] lines = caption.split(" \\| ");
        for (int k = 0; k < lines.length; k++) g.drawString(lines[k], 8, H + 16 + 16 * k);
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", file);
        Set<String> gates = new HashSet<>();
        int standing = 0;
        for (Shown sh : on) {
            if (!inbound(sh.truck(), t) && !outbound(sh.truck(), t)) { standing++; gates.add(String.format("%.2f,%.2f", sh.truck().x(), sh.truck().y())); }
        }
        System.out.printf("wrote %s: %d trucks, %d off their road, %d standing at %d distinct points, N = %d, t = %.3f%n", file, on.size(), offRoad(m, on), standing, gates.size(), n, t);
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

    static void fill(Graphics2D g, double[][] pts, long x0, long y0, double px, Color c) {
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
