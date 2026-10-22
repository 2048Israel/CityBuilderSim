package ham.citybuildersim;

import java.util.Arrays;

/**
 * A street's smooth line from the plots it is laid on, and the plots that line's band reaches: pure - plots in, points in plots out, no plan and no screen.
 *
 * WHY THIS EXISTS (batch CL4, slice C; Jerus, 2026-10-20: "having some slanted roads and curves"; the study spec-city-layout 5.2).
 * A street the ground cuts a cell along (DistrictPlan, WHERE THE GROUND CUTS A CELL: the plots of ROLE_SHORE that join a dead end to
 * its neighbour) is a path of plots that follows the cut, and drawn plot by plot - a bar across each - it is a staircase of crosses.
 * FX5 rounded the railway's corners as one geometry that the routes and the tiles both read (TrackCurves); the shore's streets are
 * given the same: the middles of the path's plots are simplified (Douglas-Peucker, FIT), the corners of what is left rounded with
 * TrackCurves at CURVE_RADIUS, and the plan and the painter both call this class, so the plot a building may not stand on (the band
 * the line draws reaches it) is the plot the painter draws the band across, the one geometry read by both - FX3's rule, again.
 *
 * COORDINATES. A frame's plot (x, y) is the square [x, x + 1] by [y, y + 1] in plots and its middle (x + 0.5, y + 0.5), as
 * TrackCurves has it; a plot's index in a frame of side `frame` is y * frame + x.
 */
public final class StreetCurves {

    private StreetCurves() { }

    /** The radius the corners of a street along a cut are rounded with, in plots: 2 (60 m) - the railway's is 3 (TrackCurves.RADIUS), a street's turn at the near view is tighter; where a leg is shorter than twice the tangent TrackCurves cuts the radius to fit. */
    public static final double CURVE_RADIUS = 2;

    /** How far the simplified line of a path may lie from the middles of its plots, in plots: three quarters (Douglas-Peucker) - more than a 45-degree staircase's corners lie from the line through its steps (sqrt 2 / 2, 0.71), so a staircase along a diagonal shore is one straight line, as an avenue is, where a half kept every corner (24 of the test district's 25 corners cut short, the line jagged; CL4's review). */
    public static final double FIT = 0.75;

    /** The half width of the band a street's curve is drawn with, in plots: a half, the widest surface a street has (DistrictPlan.FULL, a whole plot) - a plot whose square lies nearer the line than this is one the band reaches, as the fourth plot of an avenue's 2 x 2 block is. */
    public static final double BAND_HALF = 0.5;

    /** The margin a distance must be inside BAND_HALF to count, so a plot beside a line along a plot's middles (exactly BAND_HALF away) is not reached, whatever the last bit of the arithmetic says. */
    static final double EDGE = 1e-9;

    /**
     * The simplified polyline through the middles of the first n plots of `plots` (frame indices of side `frame`), in order: {xs, ys}.
     * Douglas-Peucker with FIT: the first and the last plot's middle are kept, and each plot's middle lies within FIT of the line. The
     * farthest point of a stretch is the first such, so the same plots give the same line on any machine.
     */
    public static double[][] fit(int[] plots, int n, int frame) {
        double[] px = new double[n], py = new double[n];
        for (int i = 0; i < n; i++) {
            px[i] = plots[i] % frame + 0.5;
            py[i] = plots[i] / frame + 0.5;
        }
        if (n <= 2) return new double[][] { px, py };
        boolean[] keep = new boolean[n];
        keep[0] = true;
        keep[n - 1] = true;
        int[] stack = new int[4 * n];
        int sp = 0;
        stack[sp++] = 0;
        stack[sp++] = n - 1;
        while (sp > 0) {
            int hi = stack[--sp], lo = stack[--sp];
            int far = -1;
            double worst = FIT;
            for (int i = lo + 1; i < hi; i++) {
                double d = segmentDistance(px[i], py[i], px[lo], py[lo], px[hi], py[hi]);
                if (d > worst) { worst = d; far = i; }
            }
            if (far < 0) continue;
            keep[far] = true;
            stack[sp++] = lo;
            stack[sp++] = far;
            stack[sp++] = far;
            stack[sp++] = hi;
        }
        int m = 0;
        for (int i = 0; i < n; i++) if (keep[i]) m++;
        double[] vx = new double[m], vy = new double[m];
        m = 0;
        for (int i = 0; i < n; i++) {
            if (!keep[i]) continue;
            vx[m] = px[i];
            vy[m] = py[i];
            m++;
        }
        return new double[][] { vx, vy };
    }

    /** The line of a simplified path (fit()): its corners rounded with TrackCurves at CURVE_RADIUS, the arcs sampled every TrackCurves.STEP of their length: {xs, ys}, from the first vertex to the last. */
    public static double[][] line(double[] vx, double[] vy) {
        return TrackCurves.round(vx, vy, CURVE_RADIUS);
    }

    /**
     * The plots the band of a line reaches, sorted, each once, as indices in a frame of side `frame` (plots off the frame are left
     * out): those whose square lies nearer the polyline (xs, ys) than BAND_HALF. A line along the middles of plots reaches only
     * those plots; one that cuts across their corners reaches the plot on the other side of the cut as well.
     */
    public static int[] reached(double[] xs, double[] ys, int frame) {
        int[] out = new int[32];
        int n = 0;
        int last = Math.max(xs.length - 1, 1);
        for (int i = 0; i < last; i++) {
            double ax = xs[i], ay = ys[i], bx = xs.length > 1 ? xs[i + 1] : ax, by = xs.length > 1 ? ys[i + 1] : ay;
            int x0 = Math.max(0, (int) Math.floor(Math.min(ax, bx) - BAND_HALF)), x1 = Math.min(frame - 1, (int) Math.floor(Math.max(ax, bx) + BAND_HALF));
            int y0 = Math.max(0, (int) Math.floor(Math.min(ay, by) - BAND_HALF)), y1 = Math.min(frame - 1, (int) Math.floor(Math.max(ay, by) + BAND_HALF));
            for (int y = y0; y <= y1; y++) {
                for (int x = x0; x <= x1; x++) {
                    if (squareDistance(ax, ay, bx, by, x, y) >= BAND_HALF - EDGE) continue;
                    if (n == out.length) out = Arrays.copyOf(out, n * 2);
                    out[n++] = y * frame + x;
                }
            }
        }
        Arrays.sort(out, 0, n);
        int m = 0;
        for (int i = 0; i < n; i++) if (m == 0 || out[m - 1] != out[i]) out[m++] = out[i];
        return Arrays.copyOf(out, m);
    }

    /** The distance from point (px, py) to the segment (ax, ay)-(bx, by). */
    static double segmentDistance(double px, double py, double ax, double ay, double bx, double by) {
        double dx = bx - ax, dy = by - ay, len2 = dx * dx + dy * dy;
        double t = len2 < 1e-18 ? 0 : Math.max(0, Math.min(1, ((px - ax) * dx + (py - ay) * dy) / len2));
        double qx = ax + t * dx - px, qy = ay + t * dy - py;
        return Math.sqrt(qx * qx + qy * qy);
    }

    /** The distance from point (px, py) to the closed square of plot (x, y): 0 inside it. */
    static double pointSquare(double px, double py, int x, int y) {
        double dx = Math.max(Math.max(x - px, 0), px - (x + 1)), dy = Math.max(Math.max(y - py, 0), py - (y + 1));
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** The distance from segment (ax, ay)-(bx, by) to the closed square of plot (x, y): 0 where they meet, else the least of the ends' distances to the square and the square's corners' to the segment (both convex, so one of those is the nearest pair). */
    static double squareDistance(double ax, double ay, double bx, double by, int x, int y) {
        if (meets(ax, ay, bx, by, x, y)) return 0;
        double d = Math.min(pointSquare(ax, ay, x, y), pointSquare(bx, by, x, y));
        for (int c = 0; c < 4; c++) d = Math.min(d, segmentDistance(x + (c & 1), y + (c >> 1), ax, ay, bx, by));
        return d;
    }

    /** Whether the segment (ax, ay)-(bx, by) meets the closed square of plot (x, y) (Liang-Barsky). */
    static boolean meets(double ax, double ay, double bx, double by, int x, int y) {
        double t0 = 0, t1 = 1, dx = bx - ax, dy = by - ay;
        double[] p = { -dx, dx, -dy, dy };
        double[] q = { ax - x, x + 1 - ax, ay - y, y + 1 - ay };
        for (int i = 0; i < 4; i++) {
            if (p[i] == 0) {
                if (q[i] < 0) return false;
                continue;
            }
            double r = q[i] / p[i];
            if (p[i] < 0) { if (r > t1) return false; if (r > t0) t0 = r; }
            else { if (r < t0) return false; if (r < t1) t1 = r; }
        }
        return t0 <= t1;
    }
}
