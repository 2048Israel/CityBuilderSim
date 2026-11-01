package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The replay camera's arithmetic without the toolkit (TR1, 2026-10-31): the glide between two marked views, the fly out to the
 * whole Earth and back, and the smooth hand that eases the wheel, W A S D and the drift - what ui/MapView applies to its frame
 * and ReplayCheck 5 holds, as GlobeFrame and MapFrame are for the view they move.
 *
 * WHY IT EXISTS (TR1): Jerus films the game's trailer from a recorded city with a screen recorder, and two of its shots are a
 * camera move - the 0:54-1:35 glide while the city grows, the 2:40-3:12 pull-out from the streets to the whole Earth. A hand on
 * the wheel cannot make either on a recording: a notch is an instant jump of a fifth of the scale, a drag steps with the pointer's
 * jitter. A move there has to be a curve the game follows a frame at a time, eased at both ends, whose scale moves in proportion
 * (a view twice as close covers half the ground, so the same share of the scale per second reads as the same speed) and whose
 * centre crosses the screen at an even pace while it does.
 *
 * <p>A {@link View} is the camera's state for a mark, a glide or a fly: on the globe (an Earth world) a and b are the centre's
 * latitude and longitude and the scale is km a pixel ({@code GlobeFrame.kmPx()}, larger is farther); on the plane (a generated
 * world) a and b are the centre's plot x and y and the scale is pixels a plot ({@code MapFrame.scale()}, larger is closer). The
 * roll is not kept: {@code GlobeFrame.set()} derives it from the centre and the scale, so a glide's roll follows its path.
 */
public final class CameraPath {

    private CameraPath() { }

    /** How many times the view's width doubles in a second of the fly out and back: 1 - so a fly from a street, many doublings from the whole planet, takes longer than one from the region. */
    public static final double FLY_DOUBLINGS_A_SECOND = 1.0;

    /** The time the smooth hand takes to cover most of a move, in seconds: 0.2 - each frame eases off the share 1 - exp(-dt / SMOOTH_S) of the zoom still to do and of the gap between the pan velocity and the keys', so a notch or a key starts and stops gently. */
    public static final double SMOOTH_S = 0.2;

    /** The longest step the smooth hand takes, in seconds: 1/20 - a frame held up (a collection, a load) is a pause in the move, not a jump. */
    public static final double MOST_DT_S = 1.0 / 20;

    /** A notch of the wheel zooms by this much (the map's own, MapFrame.ZOOM_STEP). */
    public static final double ZOOM_STEP = MapFrame.ZOOM_STEP;

    /** Each press of Shift+W, A, S or D adds this many pixels a second to the drift that way: 24 - a still shot's slow constant move, about two seconds to cross 50 px. */
    public static final double DRIFT_STEP_PX = 24;

    /** Each press of R or F adds this to the drift's zoom, the natural log of the magnification a second: 0.06 - a press of R closes in by 6% a second. */
    public static final double DRIFT_ZOOM_STEP = 0.06;

    /** What is left of a zoom below this is let go, so the move ends: 1e-7 of a log, far under a pixel of any view. */
    private static final double REST = 1e-7;

    /** ...and of a velocity with no key asking for it: 0.01 px a second. */
    private static final double REST_V = 0.01;

    /**
     * A camera view: {@code globe} says which kind (see the class's account), {@code a} and {@code b} the centre, {@code scale}
     * how close, {@code month} the recorded month on screen when it was marked or when the glide is at it.
     */
    public record View(boolean globe, double a, double b, double scale, int month) { }

    /** The ease: (1 - cos(pi t)) / 2 of t clamped to [0, 1] - 0 and 1 at the ends, still at both, and symmetric (ease(1 - t) = 1 - ease(t)). */
    public static double ease(double t) {
        double c = Math.max(0, Math.min(1, t));
        return (1 - Math.cos(Math.PI * c)) / 2;
    }

    /**
     * The view a share t of the way from {@code from} to {@code to}, eased. The scale is geometric - the exp of the linear blend of
     * the logs at the eased share, so its middle is the two ends' geometric mean. The centre moves so the ground crosses the
     * screen at an even pace while the scale changes: with r the ratio of the pixels a unit at the end to those at the start (the
     * scale's ratio on the plane, its inverse on the globe) its share along the way is u = (1 - r^-e) / (1 - r^-1) for the eased
     * share e (u = e when r is 1) - a linear blend of x and y on the plane, on the globe the great-circle point at u between the
     * two centres. The month is linear in t, rounded: time moves at a steady pace while the camera eases. The two ends must be
     * of one kind; of two kinds the view stays at {@code from} until t reaches 1.
     */
    public static View glide(View from, View to, double t) {
        double c = Math.max(0, Math.min(1, t));
        int month = (int) Math.round(from.month() + (to.month() - from.month()) * c);
        if (from.globe() != to.globe()) return c >= 1 ? to : new View(from.globe(), from.a(), from.b(), from.scale(), month);
        double e = ease(c);
        double scale = Math.exp(Math.log(from.scale()) * (1 - e) + Math.log(to.scale()) * e);
        double r = from.globe() ? from.scale() / to.scale() : to.scale() / from.scale();
        double u = share(r, e);
        if (!from.globe()) {
            return new View(false, from.a() + (to.a() - from.a()) * u, from.b() + (to.b() - from.b()) * u, scale, month);
        }
        double[] p = slerp(from.a(), from.b(), to.a(), to.b(), u);
        return new View(true, p[0], p[1], scale, month);
    }

    /** The centre's share of the way at the eased share e when the pixels a unit change by the ratio r: (1 - r^-e) / (1 - r^-1), e itself when r is 1. */
    public static double share(double r, double e) {
        if (!(r > 0) || Math.abs(r - 1) < 1e-12) return e;
        return (1 - Math.pow(r, -e)) / (1 - 1 / r);
    }

    /** n + 1 views from t = 0 to t = 1 along the glide, equally spaced in t (n at least 1): what the warm-up walks before a glide starts. */
    public static List<View> path(View from, View to, int n) {
        int steps = Math.max(1, n);
        List<View> out = new ArrayList<>(steps + 1);
        for (int i = 0; i <= steps; i++) out.add(glide(from, to, i / (double) steps));
        return out;
    }

    /**
     * The fly out: the glide from {@code from} to the whole planet facing the city, the widest view - {@code widestScale} (on the
     * globe GlobeFrame.mostKmPx(), on a generated world MapFrame.leastScale()) at the city's lat/lon or the world's middle
     * ({@code cityA}, {@code cityB}), what GlobeFrame.fit() shows. The month stays the one it was.
     */
    public static View flyOut(View from, double widestScale, double cityA, double cityB, double t) {
        return glide(from, new View(from.globe(), cityA, cityB, widestScale, from.month()), t);
    }

    /** How long a fly takes, in seconds: the doublings of the view's width between the two scales over FLY_DOUBLINGS_A_SECOND. Flying back in is the same time. */
    public static double flySeconds(double fromScale, double toScale) {
        return Math.abs(Math.log(toScale / fromScale) / Math.log(2)) / FLY_DOUBLINGS_A_SECOND;
    }

    /* ----------------------------------- the great circle ----------------------------------- */

    private static double[] unit(double lat, double lon) {
        double la = Math.toRadians(lat), lo = Math.toRadians(lon);
        return new double[] { Math.cos(la) * Math.cos(lo), Math.cos(la) * Math.sin(lo), Math.sin(la) };
    }

    /** The point a share u of the way along the great circle from (lat0, lon0) to (lat1, lon1), {lat, lon} in degrees; the first point for a path of no length. */
    static double[] slerp(double lat0, double lon0, double lat1, double lon1, double u) {
        double[] p = unit(lat0, lon0), q = unit(lat1, lon1);
        double dot = Math.max(-1, Math.min(1, p[0] * q[0] + p[1] * q[1] + p[2] * q[2]));
        double w = Math.acos(dot);
        if (w < 1e-12) return new double[] { lat0, lon0 };
        double[] v = new double[3];
        if (Math.PI - w < 1e-9) {
            // Opposite points: any great circle between them will do; the one through a pole (or, at the poles, along the equator).
            double[] axis = Math.abs(p[2]) < 0.9 ? new double[] { 0, 0, 1 } : new double[] { 1, 0, 0 };
            double[] side = { p[1] * axis[2] - p[2] * axis[1], p[2] * axis[0] - p[0] * axis[2], p[0] * axis[1] - p[1] * axis[0] };
            double n = Math.sqrt(side[0] * side[0] + side[1] * side[1] + side[2] * side[2]);
            for (int i = 0; i < 3; i++) v[i] = Math.cos(Math.PI * u) * p[i] + Math.sin(Math.PI * u) * side[i] / n;
        } else {
            double s = Math.sin(w), a = Math.sin((1 - u) * w) / s, b = Math.sin(u * w) / s;
            for (int i = 0; i < 3; i++) v[i] = a * p[i] + b * q[i];
        }
        double lat = Math.toDegrees(Math.asin(Math.max(-1, Math.min(1, v[2]))));
        double lon = Math.toDegrees(Math.atan2(v[1], v[0]));
        return new double[] { lat, lon };
    }

    /* ----------------------------------- the smooth hand ----------------------------------- */

    /** What a step of the smooth hand asks of the view: a zoom factor about the point (sx, sy) (above 1 closer) and pixels to pan; 1 and 0 when nothing moves. */
    public record Move(double zoom, double sx, double sy, double dx, double dy) {
        public boolean none() { return zoom == 1 && dx == 0 && dy == 0; }
    }

    /**
     * What the wheel, W A S D and the drift do in a replay, a step a frame (TR1): a notch adds its zoom to what is still to do about
     * its anchor, and each step eases a share of it off ({@code 1 - exp(-dt / SMOOTH_S)}); the keys set the pan velocity they ask
     * for and the velocity eases toward it by the same share, so a key starts and stops gently; the drift is a constant move
     * added to both until {@link #still()}. A step's time is cut to MOST_DT_S: a frame of a hitch is a pause, not a jump.
     */
    public static final class Smooth {
        private double pending;                     // the zoom still to do, ln of the factor
        private double anchorX, anchorY;            // the wheel's anchor, the last notch's point
        private double centreX, centreY;            // where a zoom with no wheel behind it closes in: the view's middle
        private double keyVx, keyVy;                // the velocity the keys ask for, px a second
        private double vx, vy;                      // the velocity now
        private double driftVx, driftVy, driftZoom; // the constant move: px a second, and ln of the factor a second

        /** Adds {@code notches} (positive closer) of the wheel's zoom still to do about (sx, sy); the last anchor holds. */
        public void wheel(double sx, double sy, double notches) {
            pending += notches * Math.log(ZOOM_STEP);
            anchorX = sx;
            anchorY = sy;
        }

        /** The point a drift's zoom closes in on (the view's middle): a zoom still to do from the wheel keeps its own anchor. */
        public void centre(double sx, double sy) {
            centreX = sx;
            centreY = sy;
        }

        /** The pan velocity the keys ask for, {across, down} px a second the ground moves (MapFrame.keyVelocity()'s); {0, 0} lets go. */
        public void keys(double vx, double vy) {
            keyVx = vx;
            keyVy = vy;
        }

        /** A constant move kept until still(): the ground moves (vx, vy) px a second and the zoom closes by exp(zoomPerSecond) a second (negative opens). */
        public void drift(double vx, double vy, double zoomPerSecond) {
            driftVx = vx;
            driftVy = vy;
            driftZoom = zoomPerSecond;
        }

        /** Stops every move: the drift, the zoom still to do, the keys' velocity and the velocity the hand has. */
        public void still() {
            pending = 0;
            keyVx = keyVy = vx = vy = 0;
            driftVx = driftVy = driftZoom = 0;
        }

        /** The drift now: {px a second across, px a second down, ln of the zoom a second}. */
        public double[] driftNow() { return new double[] { driftVx, driftVy, driftZoom }; }

        /** Whether a drift is set. */
        public boolean drifting() { return driftVx != 0 || driftVy != 0 || driftZoom != 0; }

        /** The pan velocity the hand has now, {across, down} px a second (a harness's). */
        public double[] velocity() { return new double[] { vx, vy }; }

        /** Whether anything is still moving or about to: zoom to do, a velocity, the keys' ask or a drift - the frames go on while so. */
        public boolean moving() {
            return Math.abs(pending) > REST || Math.abs(vx) > REST_V || Math.abs(vy) > REST_V
                    || keyVx != 0 || keyVy != 0 || drifting();
        }

        /** One step of dt seconds (cut to MOST_DT_S): the move to apply to the view. */
        public Move step(double dt) {
            double t = Math.max(0, Math.min(MOST_DT_S, dt));
            double rate = 1 - Math.exp(-t / SMOOTH_S);
            boolean wheeled = pending != 0;
            double take = pending * rate;
            pending -= take;
            if (Math.abs(pending) < REST) pending = 0;      // let go, not added to this step: the steps only ever ease off
            vx += (keyVx - vx) * rate;
            vy += (keyVy - vy) * rate;
            if (Math.abs(vx) < REST_V && keyVx == 0) vx = 0;
            if (Math.abs(vy) < REST_V && keyVy == 0) vy = 0;
            double zoom = Math.exp(take + driftZoom * t);
            double dx = (vx + driftVx) * t, dy = (vy + driftVy) * t;
            if (zoom == 1 && dx == 0 && dy == 0) return new Move(1, 0, 0, 0, 0);
            return new Move(zoom, wheeled ? anchorX : centreX, wheeled ? anchorY : centreY, dx, dy);
        }
    }
}
