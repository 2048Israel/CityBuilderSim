package ham.citybuildersim.ui;

import ham.citybuildersim.CameraPath;
import java.util.List;

/**
 * The replay's camera (TR1, 2026-10-31): the two marked views and the glide between them, the fly out to the whole Earth and back,
 * the warm-up before a glide, and the smooth hand with its drift - the state of every camera move, frame by frame. The arithmetic
 * is {@link CameraPath}; ui/MapView is the toolkit's half and asks this for one {@link Shot} a frame (MapView.replayCamera()), a
 * view to take whole or a move to ease on, and applies it; ReplayMode owns this, takes the keys and the bar's camera group, and
 * is the month on screen (a glide of views with different months moves time with it, by quick steps).
 *
 * WHY IT EXISTS (TR1): the trailer's camera moves - the glide while the city grows, the pull-out to the whole planet - cannot be
 * made by a hand on the wheel on a screen recording. They are curves the game follows a frame at a time, and the first frame of a
 * glide must not find the map half painted: before a glide starts the camera walks its path once so every tile on it is drawn.
 *
 * <p>The warm-up walks {@link #WARM_VIEWS} views of the path a frame each, staying on one until MapView says its painting has
 * nothing left to do and nothing asked of the worker is outstanding (or {@link #WARM_VIEW_S} has passed), then returns to A and
 * starts. It warms the start month's map only: a glide across months swaps the map as it goes, each quick step keeping the
 * pictures that did not change (MapView.staleAll()), so what the glide shows of a later month is painted as it comes.
 *
 * <p>Everything runs on the FX thread, from MapView's frame. Nothing here touches a node.
 */
final class ReplayCamera {

    /** How many steps of the glide's path the warm-up walks: 24, so 25 views from A to B. */
    static final int WARM_VIEWS = 24;

    /** The most the warm-up stays on one view, in seconds: 1 - a view whose last tile never comes (a far node the worker cannot ground) does not hold the rest. */
    static final double WARM_VIEW_S = 1.0;

    /** The most the whole warm-up takes, in seconds: 8 - then the glide starts with what is painted. */
    static final double WARM_MOST_S = 8;

    /** The longest step of a glide's or fly's own clock, in seconds: 0.1 - a frame held up (a quick step of the map) is a pause in the move, not a jump ahead. */
    static final double MOST_DT_S = 0.1;

    /** The seconds a glide takes when the bar's field says nothing: 10; the field's least and most. */
    static final double GLIDE_S = 10, GLIDE_S_LEAST = 1, GLIDE_S_MOST = 600;

    /** What a frame of the camera asks of the view: a view to take whole, or a move to ease on, or neither; and whether the frames must go on. */
    record Shot(CameraPath.View view, CameraPath.Move move, boolean more) {
        static final Shot IDLE = new Shot(null, null, false);
    }

    private enum Phase { IDLE, WARM, GLIDE, FLY }

    private final ReplayMode mode;
    private final UserInterface ui;
    private final CameraPath.Smooth smooth = new CameraPath.Smooth();

    private CameraPath.View markA, markB;
    private double glideSeconds = GLIDE_S;

    private Phase phase = Phase.IDLE;
    private String status;
    // The warm-up.
    private List<CameraPath.View> path;
    private int warmAt;
    private boolean warmApplied;
    private double warmS, warmViewS;
    // A glide or a fly: from where, to where, how far along (t), over how long.
    private CameraPath.View from, to;
    private boolean flyingOut;
    private double t, seconds;
    // Where the last fly out began, which the fly in goes back to.
    private CameraPath.View flyBack;

    ReplayCamera(ReplayMode mode, UserInterface ui) {
        this.mode = mode;
        this.ui = ui;
    }

    CameraPath.Smooth smooth() { return smooth; }

    /** Whether a warm-up, a glide or a fly is in flight: the month does not rest, the hand's input stops it. */
    boolean busy() { return phase != Phase.IDLE; }

    /** Whether a glide or a fly is under way - not the warm-up before a glide, not idle (CM1: the frames MapView tallies). */
    boolean moving() { return phase == Phase.GLIDE || phase == Phase.FLY; }

    /** The phase's name - IDLE, WARM, GLIDE or FLY (ReplayMoveProbe reads it). */
    String phaseName() { return phase.name(); }

    /** What the bar says of a move in flight ("warming up the glide 7 of 25"), or null. */
    String status() { return status; }

    CameraPath.View markA() { return markA; }

    CameraPath.View markB() { return markB; }

    double glideSeconds() { return glideSeconds; }

    void setGlideSeconds(double s) {
        glideSeconds = Math.max(GLIDE_S_LEAST, Math.min(GLIDE_S_MOST, Double.isFinite(s) ? s : GLIDE_S));
    }

    /** Back to nothing marked, nothing in flight, no drift: a replay opened or left. */
    void reset() {
        stopMove();
        smooth.still();
        markA = markB = flyBack = null;
        glideSeconds = GLIDE_S;
    }

    /** The marks as the bar's tooltip says them: "month 123, ..." or "not marked". */
    static String words(CameraPath.View v) {
        if (v == null) return "not marked";
        return "month " + v.month() + (v.globe()
                ? String.format(" · %.2f°, %.2f° · %s km a pixel", v.a(), v.b(), shortScale(v.scale()))
                : String.format(" · plot %.0f, %.0f · %s px a plot", v.a(), v.b(), shortScale(v.scale())));
    }

    private static String shortScale(double s) {
        return s >= 100 ? String.format("%.0f", s) : s >= 1 ? String.format("%.1f", s) : String.format("%.3g", s);
    }

    /* ------------------------------------ the keys and the buttons ------------------------------------ */

    /** The expanded map's view with the month on screen, or null with the bar told the map is not open. */
    private CameraPath.View viewOrSay() {
        CameraPath.View v = ui.expandedMapCameraView();
        if (v == null) {
            mode.say("open the map first (M)");
            return null;
        }
        return new CameraPath.View(v.globe(), v.a(), v.b(), v.scale(), mode.shownMonth());
    }

    /** Marks the expanded map's view (and the month on screen) as A or B. */
    void mark(boolean a) {
        CameraPath.View v = viewOrSay();
        if (v == null) return;
        if (a) markA = v; else markB = v;
        mode.say("view " + (a ? "A" : "B") + " marked: " + words(v));
    }

    /** Starts the warm-up and then the glide from A to B over the bar's seconds. */
    void glide() {
        if (viewOrSay() == null) return;
        if (markA == null || markB == null) {
            mode.say("mark view A (1) and view B (2) first");
            return;
        }
        stopMove();
        smooth.still();
        // The glide starts from A's month: its map is the one the warm-up paints.
        mode.showMonth(markA.month());
        path = CameraPath.path(markA, markB, WARM_VIEWS);
        warmAt = 0;
        warmApplied = false;
        warmS = warmViewS = 0;
        phase = Phase.WARM;
        status = "warming up the glide";
        ui.replayWake();
    }

    /** Flies out from the view on show to the whole Earth (or, on a generated world, the whole world), over the time the doublings of its width take. */
    void flyOut() {
        CameraPath.View now = viewOrSay();
        CameraPath.View whole = ui.expandedMapWhole();
        if (now == null || whole == null) return;
        double secs = CameraPath.flySeconds(now.scale(), whole.scale());
        if (secs < 0.05) {
            mode.say("already the whole " + (now.globe() ? "Earth" : "world"));
            return;
        }
        stopMove();
        smooth.still();
        flyBack = now;
        from = now;
        to = new CameraPath.View(whole.globe(), whole.a(), whole.b(), whole.scale(), now.month());
        begin(true, secs, "flying out");
    }

    /** Flies back in to where the last fly out began. */
    void flyIn() {
        CameraPath.View now = viewOrSay();
        if (now == null) return;
        if (flyBack == null) {
            mode.say("fly out first (O): there is nowhere to fly back to");
            return;
        }
        double secs = CameraPath.flySeconds(now.scale(), flyBack.scale());
        if (secs < 0.05) {
            mode.say("already there");
            return;
        }
        stopMove();
        smooth.still();
        from = now;
        to = flyBack;
        begin(false, secs, "flying in");
    }

    private void begin(boolean out, double secs, String words) {
        phase = Phase.FLY;
        flyingOut = out;
        t = 0;
        seconds = secs;
        status = words;
        ui.replayWake();
    }

    /** A glide, fly or warm-up let go where it is (Esc, a button); the smooth hand's moves go on. */
    void stop() {
        if (phase == Phase.IDLE) return;
        String was = status;
        stopMove();
        mode.say("stopped: " + was);
    }

    /** A hand on the mouse, the wheel or W A S D takes the camera from a glide or fly: it stops where it is. */
    void hand() {
        if (phase != Phase.IDLE) stop();
    }

    /** The map closed (or another screen is up) while a move was in flight. */
    void mapGone() {
        if (phase != Phase.IDLE) {
            stopMove();
            mode.say("the map closed: the move stopped");
        }
    }

    /** X: every drift and move stopped, a glide or fly with them. */
    void stillAll() {
        stopMove();
        smooth.still();
        mode.say("every drift and move stopped");
    }

    private void stopMove() {
        phase = Phase.IDLE;
        status = null;
        path = null;
    }

    /** Adds to the drift: px a second across and down (the ground's, as W A S D move it) and ln of the zoom a second. */
    void driftBy(double across, double down, double zoom) {
        double[] d = smooth.driftNow();
        smooth.drift(clean(d[0] + across), clean(d[1] + down), clean(d[2] + zoom));
        ui.replayWake();
    }

    private static double clean(double v) { return Math.abs(v) < 1e-9 ? 0 : v; }

    /** The drift as the bar reads it ("drift up 24 · left 48 px a second · in 6% a second"), or null when there is none. */
    String driftReading() {
        if (!smooth.drifting()) return null;
        double[] d = smooth.driftNow();
        StringBuilder sb = new StringBuilder("drift");
        // The ground moving down is the view moving up (W): the words are the view's.
        if (d[1] != 0) sb.append(d[1] > 0 ? " up " : " down ").append(Math.round(Math.abs(d[1])));
        if (d[0] != 0) sb.append(d[0] > 0 ? " left " : " right ").append(Math.round(Math.abs(d[0])));
        if (d[0] != 0 || d[1] != 0) sb.append(" px a second");
        if (d[2] != 0) sb.append(" · ").append(d[2] > 0 ? "in " : "out ").append(String.format("%.0f%% a second", 100 * (Math.exp(Math.abs(d[2])) - 1)));
        return sb.toString();
    }

    /* ------------------------------------------ the frame ------------------------------------------ */

    /**
     * One frame (MapView.replayCamera()): {@code dt} seconds since the last, {@code settled} asked (only by the warm-up) whether the
     * view as it stands has nothing left to paint. A warm-up walks its path; a glide or a fly advances its clock and returns the view at it (the glide
     * moving the month on screen with it); otherwise the smooth hand takes its step.
     */
    Shot frame(double dt, java.util.function.BooleanSupplier settled) {
        switch (phase) {
            case WARM:
                return warm(dt, settled);
            case GLIDE:
            case FLY:
                return advance(dt);
            default:
                if (!smooth.moving()) return Shot.IDLE;
                CameraPath.Move m = smooth.step(dt);
                return new Shot(null, m.none() ? null : m, smooth.moving());
        }
    }

    private Shot warm(double dt, java.util.function.BooleanSupplier settled) {
        warmS += dt;
        if (warmS >= WARM_MOST_S || warmAt >= path.size()) return startGlide();
        if (!warmApplied) {
            // A view is taken, and this frame paints it; the next frame asks whether that painting is done.
            warmApplied = true;
            warmViewS = 0;
            status = "warming up the glide " + (warmAt + 1) + " of " + path.size();
            return new Shot(path.get(warmAt), null, true);
        }
        warmViewS += dt;
        if (warmViewS >= WARM_VIEW_S || settled.getAsBoolean()) {
            warmAt++;
            warmApplied = false;
        }
        return new Shot(null, null, true);
    }

    private Shot startGlide() {
        phase = Phase.GLIDE;
        from = markA;
        to = markB;
        t = 0;
        seconds = glideSeconds;
        status = "gliding";
        path = null;
        return new Shot(markA, null, true);
    }

    private Shot advance(double dt) {
        t = Math.min(1, t + Math.min(Math.max(0, dt), MOST_DT_S) / seconds);
        CameraPath.View v;
        if (phase == Phase.GLIDE) {
            v = CameraPath.glide(markA, markB, t);
            mode.showMonth(v.month());
            status = String.format("gliding %.0f%%", t * 100);
        } else if (flyingOut) {
            v = CameraPath.flyOut(from, to.scale(), to.a(), to.b(), t);
            status = String.format("flying out %.0f%%", t * 100);
        } else {
            v = CameraPath.glide(from, to, t);
            status = String.format("flying in %.0f%%", t * 100);
        }
        if (t >= 1) {
            boolean glided = phase == Phase.GLIDE;
            stopMove();
            mode.say(glided ? "the glide is done" : flyingOut ? "flown out: I flies back in" : "flown back in");
            return new Shot(v, null, false);
        }
        return new Shot(v, null, true);
    }
}
