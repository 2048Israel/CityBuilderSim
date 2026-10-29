package ham.citybuildersim;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The sea lanes a map draws: every leg of every terminal's route once - a leg that many routes share, once - indexed by the district cells it reaches, so a frame asks for the legs on its screen and touches only those; pure, kept by the map with its routes and never saved.
 *
 * WHY THIS EXISTS (SL1, 2026-10-28; Jerus: "if lots of ship lanes, its not different lanes, its same lane that later
 * diverages if needed"). MapView.boats() looped over every route every frame, tested each against the screen and stroked
 * its whole polyline twice, so a late city's frame paid for every terminal, though SeaRoutes now has most of its
 * terminals share a lane and a screen shows few of them. The routes stay one to a berth (BoatSchedule's boats ride
 * them, one route to count a call against); what is drawn is the lanes, a leg a hundred routes share stroked once, and
 * found the way BoatSchedule finds its routes' boats: by the district cell. A leg's box is its two points', as
 * BoatSchedule.legBoxes() takes it with no spread (the boats' swing past the pivot is theirs, not the lane's). A query
 * keeps a stamp per leg and is for one thread at a time, the screen's.
 */
public final class SeaLanes {

    /**
     * Legs taken in one at a time, each kept once: a leg is not kept when the same one is kept already, nor when it
     * lies on one kept that ends at the same point (a route that joins a lane at a point of its leg goes on along
     * the rest of that leg, which the lane's own leg draws and finds already). SeaRoutes' index of the lanes a berth
     * may join and the drawn lanes both keep their legs so.
     */
    static final class Legs {
        /** How far off a kept leg's line, in plots, a leg's start may lie and be on it: a join point is worked out in doubles. */
        static final double ON_LINE = 1e-6;

        /** The legs kept, four doubles each: x0 y0 x1 y1. */
        double[] pts = new double[64];
        int count;
        /** The starts of the legs kept, by the point they end at (its two coordinates' bits mixed). */
        private final Map<Long, End> ends = new HashMap<>();

        private static final class End {
            final double x, y;
            double[] from = new double[4];
            int n;

            End(double x, double y) { this.x = x; this.y = y; }

            void add(double fx, double fy) {
                if (n + 2 > from.length) from = Arrays.copyOf(from, from.length * 2);
                from[n++] = fx;
                from[n++] = fy;
            }
        }

        /** Takes the leg (ax, ay) to (bx, by) in; false when it is kept already or lies on a kept one ending at (bx, by). */
        boolean add(double ax, double ay, double bx, double by) {
            long k = Double.doubleToLongBits(bx) * 0x9E3779B97F4A7C15L ^ Double.doubleToLongBits(by);
            End e = ends.get(k);
            if (e != null && e.x == bx && e.y == by) {
                for (int i = 0; i < e.n; i += 2) if (lies(e.from[i], e.from[i + 1], bx, by, ax, ay)) return false;
            }
            if ((count + 1) * 4 > pts.length) pts = Arrays.copyOf(pts, pts.length * 2);
            pts[count * 4] = ax;
            pts[count * 4 + 1] = ay;
            pts[count * 4 + 2] = bx;
            pts[count * 4 + 3] = by;
            count++;
            if (e == null) {
                e = new End(bx, by);
                ends.put(k, e);
            }
            if (e.x == bx && e.y == by) e.add(ax, ay);
            return true;
        }

        /** Whether (px, py) lies on the segment (cx, cy) to (bx, by), its two ends too, to within ON_LINE. */
        private static boolean lies(double cx, double cy, double bx, double by, double px, double py) {
            double dx = bx - cx, dy = by - cy, len = Math.hypot(dx, dy);
            if (!(len > 0)) return px == cx && py == cy;
            double along = ((px - cx) * dx + (py - cy) * dy) / len, off = ((px - cx) * dy - (py - cy) * dx) / len;
            return along >= -ON_LINE && along <= len + ON_LINE && Math.abs(off) <= ON_LINE;
        }
    }

    /** The legs in the order met (the routes' order, each route's own order): four doubles each, x0 y0 x1 y1. */
    private final double[] legs;
    private final int count;
    /** How many legs the routes had between them, a shared one every time. */
    private final int routeLegs;
    /** Each district cell's legs, those whose box reaches it. */
    private final Map<Long, int[]> index;
    /** The frame a leg was last looked at in, for a query to meet a leg once, and the frames asked. */
    private final int[] seenAt;
    private int frames;
    private int touched;

    private SeaLanes(double[] legs, int count, int routeLegs, Map<Long, int[]> index) {
        this.legs = legs;
        this.count = count;
        this.routeLegs = routeLegs;
        this.index = index;
        this.seenAt = new int[count];
    }

    /** The lanes of these routes: each of their legs once, the abyss's too. */
    public static SeaLanes of(List<BoatSchedule.Route> routes) {
        Legs kept = new Legs();
        int routeLegs = 0;
        for (BoatSchedule.Route r : routes) {
            double[] xs = r.xs(), ys = r.ys();
            for (int i = 0; i + 1 < xs.length; i++, routeLegs++) kept.add(xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }
        double[] legs = Arrays.copyOf(kept.pts, kept.count * 4);
        int count = kept.count;
        Map<Long, Bin> bins = new HashMap<>();
        for (int l = 0; l < count; l++) {
            long cx0 = Math.floorDiv((long) Math.floor(Math.min(legs[l * 4], legs[l * 4 + 2])), World.DISTRICT);
            long cx1 = Math.floorDiv((long) Math.floor(Math.max(legs[l * 4], legs[l * 4 + 2])), World.DISTRICT);
            long cy0 = Math.floorDiv((long) Math.floor(Math.min(legs[l * 4 + 1], legs[l * 4 + 3])), World.DISTRICT);
            long cy1 = Math.floorDiv((long) Math.floor(Math.max(legs[l * 4 + 1], legs[l * 4 + 3])), World.DISTRICT);
            for (long cx = cx0; cx <= cx1; cx++) for (long cy = cy0; cy <= cy1; cy++) bins.computeIfAbsent(key(cx, cy), k -> new Bin()).add(l);
        }
        Map<Long, int[]> index = new HashMap<>();
        for (Map.Entry<Long, Bin> e : bins.entrySet()) index.put(e.getKey(), Arrays.copyOf(e.getValue().a, e.getValue().n));
        return new SeaLanes(legs, count, routeLegs, index);
    }

    /** A cell's legs while they are gathered. */
    private static final class Bin {
        int[] a = new int[4];
        int n;

        void add(int v) {
            if (n == a.length) a = Arrays.copyOf(a, n * 2);
            a[n++] = v;
        }
    }

    private static long key(long cx, long cy) {
        return (cx << 32) ^ (cy & 0xffffffffL);
    }

    /** How many legs there are, each once. */
    public int legs() { return count; }

    /** How many legs the routes had between them: each route's, a leg shared by many counted for every one. */
    public int routeLegs() { return routeLegs; }

    /** How many legs the last crossing() looked at (those of the district cells it covers, each once). */
    public int touched() { return touched; }

    /** Leg i's points, {x0, y0, x1, y1} in plots. */
    public double[] leg(int i) {
        return new double[] { legs[i * 4], legs[i * 4 + 1], legs[i * 4 + 2], legs[i * 4 + 3] };
    }

    /**
     * The legs whose box meets the rectangle [x0, x1) x [y0, y1) in plots, as the points of each in turn, x0 y0 x1 y1
     * (four doubles a leg, in the order they were met): the district cells the rectangle covers asked, and only the legs
     * in them looked at (touched()) - or, for a rectangle covering more cells than the index holds, the index's own.
     */
    public double[] crossing(long x0, long y0, long x1, long y1) {
        touched = 0;
        int stamp = ++frames;
        long cx0 = Math.floorDiv(x0, World.DISTRICT), cx1 = Math.floorDiv(Math.max(x0, x1 - 1), World.DISTRICT);
        long cy0 = Math.floorDiv(y0, World.DISTRICT), cy1 = Math.floorDiv(Math.max(y0, y1 - 1), World.DISTRICT);
        int[] hit = new int[16];
        int n = 0;
        if ((cx1 - cx0 + 1) * (cy1 - cy0 + 1) > index.size()) {
            for (Map.Entry<Long, int[]> e : index.entrySet()) {
                long cx = e.getKey() >> 32, cy = (int) (long) e.getKey();
                if (cx < cx0 || cx > cx1 || cy < cy0 || cy > cy1) continue;
                for (int l : e.getValue()) {
                    if (seenAt[l] == stamp) continue;
                    seenAt[l] = stamp;
                    touched++;
                    if (!meets(l, x0, y0, x1, y1)) continue;
                    if (n == hit.length) hit = Arrays.copyOf(hit, n * 2);
                    hit[n++] = l;
                }
            }
        } else {
            for (long cx = cx0; cx <= cx1; cx++) {
                for (long cy = cy0; cy <= cy1; cy++) {
                    int[] ls = index.get(key(cx, cy));
                    if (ls == null) continue;
                    for (int l : ls) {
                        if (seenAt[l] == stamp) continue;
                        seenAt[l] = stamp;
                        touched++;
                        if (!meets(l, x0, y0, x1, y1)) continue;
                        if (n == hit.length) hit = Arrays.copyOf(hit, n * 2);
                        hit[n++] = l;
                    }
                }
            }
        }
        Arrays.sort(hit, 0, n);
        double[] out = new double[n * 4];
        for (int k = 0; k < n; k++) System.arraycopy(legs, hit[k] * 4, out, k * 4, 4);
        return out;
    }

    /** Whether leg l's box (inclusive) meets the rectangle, as BoatSchedule.crosses() takes a leg's. */
    private boolean meets(int l, long x0, long y0, long x1, long y1) {
        double ax = legs[l * 4], ay = legs[l * 4 + 1], bx = legs[l * 4 + 2], by = legs[l * 4 + 3];
        return Math.max(ax, bx) >= x0 && Math.min(ax, bx) < x1 && Math.max(ay, by) >= y0 && Math.min(ay, by) < y1;
    }

    @Override public String toString() {
        return "SeaLanes[" + count + " legs of the routes' " + routeLegs + "]";
    }
}
