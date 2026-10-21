package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The railway's curves as one geometry, for the drawing and for the trains'
 * routes alike (FX5, 0.8.15): a polyline's corners rounded with arcs of a
 * radius, the arcs the tiles draw and the routes run along, and the yard's
 * ladder as an S that leaves its line and meets its siding tangent at both
 * ends. Pure: points in plots in, points in plots out, no map and no screen.
 *
 * WHY THIS EXISTS. Until FX5 the track was drawn plot by plot, so a corner was
 * an L and a 45-degree turn a notch, and the routes were polylines through
 * plot middles, so each car of a train swung 90 degrees one after another at a
 * corner (Jerus, 2026-10-20: "do trains make 90 degree turns?"); the yard's
 * ladders were one straight pixel line from the line to the siding, 60 degrees
 * off it for the outer sidings, and the route ran along the same straight. The
 * drawing and the routes each had their own idea of where a corner was. They
 * now read this one: CityMap rounds each route with round() and lays each
 * ladder with ladder(), the tiles draw the same bends() and the same
 * ladder(), so a car is on the rail it is drawn on (FX3's rule, one layout
 * read by both, for the curves).
 *
 * A bend carries the net it belongs to (RAILWAY, 0): the highways' 45-degree
 * joints (FX5 slice D) are bends of the other net, drawn by the same arcs.
 */
public final class TrackCurves {

    private TrackCurves() { }

    /** A curve's radius on the line, in plots: 3 (90 m), a few plots at the near view (Jerus, 2026-10-20); real freight curves are 150 to 300 m, wider than a corridor's corner here. */
    public static final double RADIUS = 3;

    /** The routes' sampling along an arc or a ladder, in plots: a quarter, as MapCheck reads the trains' routes. */
    public static final double STEP = 0.25;

    /** A bend's net: the railway's 0 and (FX5 slice D) the highway's 1 - its own numbering, not CityRuns's (whose HIGHWAYS is 0 and RAILWAY 1). */
    public static final int RAILWAY = 0, HIGHWAY = 1;

    /** The turns of a polyline smaller than this, in radians, are straight: a millionth. */
    private static final double STRAIGHT = 1e-6;

    /**
     * One turn of a polyline: its corner point (x, y), the unit headings into
     * and out of it, the radius it is rounded with (the radius asked, less
     * where the legs beside it are short), the tangent length along each leg
     * from the corner to where the arc touches it, and the circle: its centre
     * (cx, cy), the angle from the centre to the arc's start, and the signed
     * sweep - positive for a turn toward the angle's increasing direction.
     * The arc starts on the incoming leg at the corner minus t along (ux, uy)
     * and ends on the outgoing at the corner plus t along (vx, vy).
     */
    public record Bend(int net, double x, double y, double ux, double uy, double vx, double vy,
                       double r, double t, double cx, double cy, double a0, double sweep) {

        /** The bend at corner (x, y) between headings u and v (unit) with the arc's radius r and tangent length t (the radius and the length agree: t = r * tan(turn / 2)); its circle worked out. */
        public static Bend of(int net, double x, double y, double ux, double uy, double vx, double vy, double r, double t) {
            double turn = Math.atan2(ux * vy - uy * vx, ux * vx + uy * vy);
            double sgn = turn < 0 ? -1 : 1;
            double sx = x - ux * t, sy = y - uy * t;
            double cx = sx + sgn * r * -uy, cy = sy + sgn * r * ux;
            return new Bend(net, x, y, ux, uy, vx, vy, r, t, cx, cy, Math.atan2(sy - cy, sx - cx), turn);
        }

        /** Where the arc starts, on the incoming leg. */
        public double startX() { return x - ux * t; }
        public double startY() { return y - uy * t; }

        /** ...and where it ends, on the outgoing leg. */
        public double endX() { return x + vx * t; }
        public double endY() { return y + vy * t; }

        /** The arc's length, in plots. */
        public double length() { return r * Math.abs(sweep); }

        /** The point on the arc a distance s along it from its start, and the heading there: {x, y, heading in radians}. */
        public double[] at(double s) {
            double phi = Math.abs(sweep) < STRAIGHT ? 0 : (sweep < 0 ? -1 : 1) * s / r;
            double a = a0 + phi;
            double h = Math.atan2(uy, ux) + phi;
            return new double[] { cx + r * Math.cos(a), cy + r * Math.sin(a), h };
        }

    }

    /**
     * The bends of a polyline (points xs[i], ys[i]) rounded with radius r:
     * each interior point where the heading turns by more than a millionth
     * of a radian and by less than a half turn is a bend. The arc is tangent
     * to both legs; its tangent length t = r * tan(turn / 2) is cut to half
     * of each leg beside the corner, and the radius shrunk to match, so two
     * bends on one leg never meet. A leg of no length is passed over.
     */
    public static List<Bend> bends(double[] xs, double[] ys, double r) {
        return bends(xs, ys, r, RAILWAY);
    }

    /** ...a bend of net `net` (RAILWAY or HIGHWAY). */
    public static List<Bend> bends(double[] xs, double[] ys, double r, int net) {
        List<Bend> out = new ArrayList<>();
        for (int i = 1; i + 1 < xs.length; i++) {
            Bend b = bendAt(xs, ys, i, r, net);
            if (b != null) out.add(b);
        }
        return out;
    }

    /** The bend at interior point i, or null where the polyline runs on straight there (or doubles back). */
    private static Bend bendAt(double[] xs, double[] ys, int i, double r, int net) {
        double ax = xs[i] - xs[i - 1], ay = ys[i] - ys[i - 1], bx = xs[i + 1] - xs[i], by = ys[i + 1] - ys[i];
        double la = Math.hypot(ax, ay), lb = Math.hypot(bx, by);
        if (la < 1e-9 || lb < 1e-9) return null;
        double ux = ax / la, uy = ay / la, vx = bx / lb, vy = by / lb;
        double turn = Math.abs(Math.atan2(ux * vy - uy * vx, ux * vx + uy * vy));
        if (turn < STRAIGHT || turn > Math.PI - STRAIGHT) return null;
        double tan = Math.tan(turn / 2);
        double t = r * tan, most = Math.min(la, lb) / 2;
        double used = r;
        if (t > most) {
            t = most;
            used = t / tan;
        }
        return Bend.of(net, xs[i], ys[i], ux, uy, vx, vy, used, t);
    }

    /**
     * The polyline with each bend's corner replaced by its arc: {xs, ys}. The
     * ends are kept, and so is every point that is no bend (the legs between
     * two arcs are straight, their two end points); an arc is sampled every
     * STEP of its length, its ends being the tangent points.
     */
    public static double[][] round(double[] xs, double[] ys, double r) {
        List<double[]> pts = new ArrayList<>();
        if (xs.length == 0) return new double[][] { new double[0], new double[0] };
        add(pts, xs[0], ys[0]);
        for (int i = 1; i + 1 < xs.length; i++) {
            Bend b = bendAt(xs, ys, i, r, RAILWAY);
            if (b == null) {
                add(pts, xs[i], ys[i]);
                continue;
            }
            add(pts, b.startX(), b.startY());
            int n = Math.max(1, (int) Math.ceil(b.length() / STEP - 1e-9));
            for (int k = 1; k < n; k++) {
                double[] q = b.at(b.length() * k / n);
                add(pts, q[0], q[1]);
            }
            add(pts, b.endX(), b.endY());
        }
        if (xs.length > 1) add(pts, xs[xs.length - 1], ys[ys.length - 1]);
        double[] ox = new double[pts.size()], oy = new double[pts.size()];
        for (int i = 0; i < ox.length; i++) { ox[i] = pts.get(i)[0]; oy[i] = pts.get(i)[1]; }
        return new double[][] { ox, oy };
    }

    /** Adds the point unless the last one is there (no leg of no length). */
    private static void add(List<double[]> pts, double x, double y) {
        if (!pts.isEmpty()) {
            double[] last = pts.get(pts.size() - 1);
            if (Math.abs(last[0] - x) < 1e-9 && Math.abs(last[1] - y) < 1e-9) return;
        }
        pts.add(new double[] { x, y });
    }

    /** Whether plot (x, y) holds track: how a bend asks the map, or a fixture, about the plots round it. */
    public interface Rail {
        boolean at(int x, int y);
    }

    /** Plots beside one another: north, east, south, west, as TilePainter has them. */
    private static final int[] DX = { 0, 1, 0, -1 }, DY = { -1, 0, 1, 0 };

    /** The direction (0 north, 1 east, 2 south, 3 west) of a unit axis heading, or -1 where it is not one. */
    private static int dir(double ux, double uy) {
        if (Math.abs(ux) < 1e-9 && Math.abs(Math.abs(uy) - 1) < 1e-9) return uy < 0 ? 0 : 2;
        if (Math.abs(uy) < 1e-9 && Math.abs(Math.abs(ux) - 1) < 1e-9) return ux > 0 ? 1 : 3;
        return -1;
    }

    /**
     * The plots a bend replaces, each {x, y}: those whose middle lies on its
     * legs strictly inside the tangent points - the corner's plot among them
     * - and whose only track is the bend's own two legs, exactly two arms of
     * track among their four neighbours (rail.at()), along the legs. A bend
     * at the corner of a 45-degree stretch, or one not on plot middles,
     * replaces none: the arc is drawn over the staircase. A plot with a
     * third arm (a T, a crossing) is not replaced either: it keeps its
     * straights and the arc is drawn over it.
     */
    public static List<int[]> replaced(Bend b, Rail rail) {
        List<int[]> out = new ArrayList<>();
        if (!onGrid(b)) return out;
        int in = dir(b.ux(), b.uy()), outDir = dir(b.vx(), b.vy());
        int back = (in + 2) % 4, toCorner = (outDir + 2) % 4;
        for (int j = 0; j < b.t() - 1e-9; j++) {
            // Along the incoming leg, j plots back from the corner: the corner's arms are the way it came by and the way it goes on.
            int mask = j == 0 ? (1 << back) | (1 << outDir) : (1 << in) | (1 << back);
            addIf(out, rail, (int) Math.floor(b.x() - b.ux() * j), (int) Math.floor(b.y() - b.uy() * j), mask);
            if (j > 0) {
                // ...and j plots on along the outgoing one.
                int m2 = (1 << outDir) | (1 << toCorner);
                addIf(out, rail, (int) Math.floor(b.x() + b.vx() * j), (int) Math.floor(b.y() + b.vy() * j), m2);
            }
        }
        return out;
    }

    /** Whether the bend is on the plot grid: its corner on a plot's middle and both its legs along an axis - a corner of straight track, as replaced() reads. A 45-degree leg, or a corner off the plots' middles, is not. */
    public static boolean onGrid(Bend b) {
        return dir(b.ux(), b.uy()) >= 0 && dir(b.vx(), b.vy()) >= 0
                && Math.abs(b.x() - Math.floor(b.x()) - 0.5) <= 1e-6 && Math.abs(b.y() - Math.floor(b.y()) - 0.5) <= 1e-6;
    }

    /**
     * How far past each tangent point, back along its leg, the last plot the
     * bend may replace reaches, in plots: the straight the tiles draw from the
     * tangent point out to that plot's far edge, so nothing is left between
     * the arc and the next straight plot. 0 where the legs are not on the
     * grid (replaced() names no plot there).
     */
    public static double stub(Bend b) {
        if (!onGrid(b)) return 0;
        int far = Math.max(0, (int) Math.ceil(b.t() - 1e-9) - 1);
        return Math.max(0, far + 0.5 - b.t());
    }

    /** Adds plot (x, y) to out if its track beside it is exactly the arms in `mask`. */
    private static void addIf(List<int[]> out, Rail rail, int x, int y, int mask) {
        int arms = 0;
        for (int d = 0; d < 4; d++) if (rail.at(x + DX[d], y + DY[d])) arms |= 1 << d;
        if (arms == mask) out.add(new int[] { x, y });
    }

    /** The smooth step 3s^2 - 2s^3: 0 at 0 and 1 at 1, level at both. */
    public static double smoothstep(double s) {
        double c = Math.max(0, Math.min(1, s));
        return c * c * (3 - 2 * c);
    }

    /**
     * A yard's ladder from the fan's apex (ax, ay), on the line heading
     * (ux, uy), to its siding's start `along` plots on and `off` plots across
     * (signed, on the unit normal (nx, ny)): an S tangent to the line at the
     * apex and to the siding at the other end, the lateral offset
     * off * smoothstep(s / along) at s plots along. Sampled every STEP of its
     * own length (a steep S is sampled as finely as a gentle one); the first
     * point is the apex and the last the siding's start: {xs, ys}. A ladder
     * with no offset (the middle siding of an odd fan, on the line itself) is
     * straight: its two ends.
     */
    public static double[][] ladder(double ax, double ay, double ux, double uy, double nx, double ny, double along, double off) {
        if (Math.abs(off) < 1e-9) return new double[][] { { ax, ax + ux * along }, { ay, ay + uy * along } };
        return ladder(ax, ay, ux, uy, nx, ny, along, off, STEP);
    }

    /** ...sampled every `step` of its length instead, a straight one too: the tiles draw it a pixel at a time. */
    public static double[][] ladder(double ax, double ay, double ux, double uy, double nx, double ny, double along, double off, double step) {
        List<double[]> pts = new ArrayList<>();
        add(pts, ax, ay);
        int fine = 800;
        double px = ax, py = ay, since = 0;
        for (int k = 1; k <= fine; k++) {
            double s = along * k / fine, l = off * smoothstep(s / along);
            double x = ax + ux * s + nx * l, y = ay + uy * s + ny * l;
            since += Math.hypot(x - px, y - py);
            px = x;
            py = y;
            if (since >= step - 1e-9 || k == fine) {
                add(pts, x, y);
                since = 0;
            }
        }
        double[] ox = new double[pts.size()], oy = new double[pts.size()];
        for (int i = 0; i < ox.length; i++) { ox[i] = pts.get(i)[0]; oy[i] = pts.get(i)[1]; }
        return new double[][] { ox, oy };
    }

    /**
     * The distance from point (px, py) to the bend's arc, where the point lies
     * within the arc's sweep (seen from its centre) - |distance from the
     * centre - r| - else Double.POSITIVE_INFINITY: the pixel helper the raster
     * draws the arc's bed, rails and sleepers by. `along` (length 1, may be
     * null) receives the distance along the arc from its start to the point's
     * foot.
     */
    public static double arcDistance(Bend b, double px, double py, double[] along) {
        double wx = px - b.cx(), wy = py - b.cy();
        double len = Math.hypot(wx, wy);
        if (len < 1e-12) return Double.POSITIVE_INFINITY;
        double sgn = b.sweep() < 0 ? -1 : 1;
        // The angle from the arc's start to the point, in the arc's own direction, 0 to 2 pi.
        double a = (Math.atan2(wy, wx) - b.a0()) * sgn;
        a = a - 2 * Math.PI * Math.floor(a / (2 * Math.PI));
        if (a > Math.abs(b.sweep())) return Double.POSITIVE_INFINITY;
        if (along != null) along[0] = a * b.r();
        return Math.abs(len - b.r());
    }
}
