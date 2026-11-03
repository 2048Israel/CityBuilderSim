package ham.citybuildersim.ui;

import ham.citybuildersim.CameraPath;

/**
 * The replay's camera (TR1, 2026-10-31): the two marked views and the glide between them, the fly out to the whole Earth and back,
 * the load of a move before it runs (CM1), and the smooth hand with its drift - the state of every camera move, frame by frame. The
 * arithmetic is {@link CameraPath}; ui/MapView is the toolkit's half and asks this for one {@link Shot} a frame
 * (MapView.replayCamera()), a view to take whole or a move to ease on, and applies it; ReplayMode owns this, takes the keys and the
 * bar's camera group, and is the month on screen (a glide of views with different months moves time with it, by quick steps).
 *
 * WHY IT EXISTS (TR1): the trailer's camera moves - the glide while the city grows, the pull-out to the whole planet - cannot be
 * made by a hand on the wheel on a screen recording. They are curves the game follows a frame at a time, and the first frame of a
 * glide must not find the map half painted: before a glide starts the camera walks its path once so every tile on it is drawn.
 *
 * <p>WHY THE LOAD (CM1, 2026-11-03): TR1's warm-up walked 25 views of a glide's path, a second a view and eight in all, the start
 * month's map alone, and a recording of a glide or a fly out still showed the screen half black where the move arrived before the
 * worker had. Jerus (2026-11-02): "allow the game to load up so that when it zooms or glides there is no loading, basically it could
 * just say loading 10 seconds, then let you click to run it ... not for the normal game, only while in replaying mode."
 *
 * <p>A move (a glide, a fly out or a fly in) is set up here and then LOADS: it goes through {@link #PLAN_HZ} views a second of its
 * own clock, {@link #planViews()} in all, which MapView paints one after another with no limit on the time, in every month the move
 * crosses, keeping what it painted until the move has run (MapView.loadStep()). The bar says "Loading the move... N s", then
 * "Ready - click to run"; a click on the map or any key runs it, so a screen recording can be started first, and Esc cancels at
 * either point. Only a replay has this camera, so the normal game loads nothing ahead.
 *
 * <p>Everything runs on the FX thread, from MapView's frame. Nothing here touches a node.
 */
final class ReplayCamera {

    /** The views a second of the move's own clock the load walks: 240 - finer than the 165 Hz this PC's screen draws at, so every frame the move runs at lies between two views it loaded. */
    static final double PLAN_HZ = 240;

    /** The longest step of a glide's or fly's own clock, in seconds: 0.1 - a frame held up (a quick step of the map) is a pause in the move, not a jump ahead. */
    static final double MOST_DT_S = 0.1;

    /** The seconds a glide takes when the bar's field says nothing: 10; the field's least and most. */
    static final double GLIDE_S = 10, GLIDE_S_LEAST = 1, GLIDE_S_MOST = 600;

    /** What a frame of the camera asks of the view: a view to take whole, or a move to ease on, or neither; and whether the frames must go on. */
    record Shot(CameraPath.View view, CameraPath.Move move, boolean more) {
        static final Shot IDLE = new Shot(null, null, false);
    }

    private enum Phase { IDLE, LOAD, READY, GLIDE, FLY }

    private static final String LOADING = "Loading the move...";

    private final ReplayMode mode;
    private final UserInterface ui;
    private final CameraPath.Smooth smooth = new CameraPath.Smooth();

    private CameraPath.View markA, markB;
    private double glideSeconds = GLIDE_S;

    private Phase phase = Phase.IDLE;
    private String status;
    // A move (loading, ready or running): a glide or a fly (out or in), from where, to where, how far along (t), over how long.
    private boolean glideKind, flyingOut;
    private CameraPath.View from, to;
    private double t, seconds;
    // The load: when it began, and the seconds-left figure the bar showed last; and which move it is (one more each time a move is set up).
    private long loadStartNanos;
    private int shownLeft, serial;
    // Where the last fly out began, which the fly in goes back to.
    private CameraPath.View flyBack;

    ReplayCamera(ReplayMode mode, UserInterface ui) {
        this.mode = mode;
        this.ui = ui;
    }

    CameraPath.Smooth smooth() { return smooth; }

    /** Whether a move is loading, waits for its click or is in flight: the month does not rest, the hand's input stops it. */
    boolean busy() { return phase != Phase.IDLE; }

    /** Whether a glide or a fly is under way - not loading, not waiting for the click, not idle (CM1: the frames MapView tallies). */
    boolean moving() { return phase == Phase.GLIDE || phase == Phase.FLY; }

    /** Whether a move is loading (CM1: MapView walks its views). */
    boolean loading() { return phase == Phase.LOAD; }

    /** Whether a move is loaded and waits for the click (CM1). */
    boolean ready() { return phase == Phase.READY; }

    /** Which move is set up: it moves on each time one is (MapView drops what it loaded of the move before). */
    int serial() { return serial; }

    /** Whether MapView keeps what it loaded: from the move's load until it has run or been let go (CM1). */
    boolean holdsLoad() { return phase == Phase.LOAD || phase == Phase.READY || phase == Phase.GLIDE || phase == Phase.FLY; }

    /** The phase's name - IDLE, LOAD, READY, GLIDE or FLY (ReplayMoveProbe reads it). */
    String phaseName() { return phase.name(); }

    /** What the bar says of a move ("Loading the move... 7 s", "Ready - click to run", "gliding 40%"), or null. */
    String status() { return status; }

    /** What MapView writes over the map while a move loads or waits for its click, or null (CM1). */
    String overlay() { return phase == Phase.LOAD || phase == Phase.READY ? status : null; }

    /** The recorded month on screen (CM1: the load's month store reads it). */
    int shownMonth() { return mode.shownMonth(); }

    /** Whether the move being loaded or run takes time with it: a glide whose two ends are of different months (a fly keeps the month on screen). */
    boolean movesTime() { return glideKind && from != null && to != null && from.month() != to.month(); }

    /** The month of a plan view put on screen, as the glide does it frame by frame (by quick steps); a fly leaves the month alone (CM1: the load shows each view's month). */
    void showMonthOf(CameraPath.View v) {
        if (glideKind) mode.showMonth(v.month());
    }

    /** The view at a share {@code at} of the move: the glide's, the fly out's or the fly in's curve - the run's and the load's. */
    private CameraPath.View viewAt(double at) {
        if (glideKind) return CameraPath.glide(from, to, at);
        if (flyingOut) return CameraPath.flyOut(from, to.scale(), to.a(), to.b(), at);
        return CameraPath.glide(from, to, at);
    }

    private int planSteps() { return Math.max(1, (int) Math.ceil(seconds * PLAN_HZ)); }

    /** How many views the load walks: PLAN_HZ of them a second of the move, and one more for its end (CM1). */
    int planViews() { return planSteps() + 1; }

    /** The k-th view of the load, k from 0 to planViews() - 1: the move's curve at k of planSteps() of its length (CM1). */
    CameraPath.View planView(int k) { return viewAt(k / (double) planSteps()); }

    /** The view the move begins at (CM1). */
    CameraPath.View startView() { return planView(0); }

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

    /** Sets up the glide from A to B over the bar's seconds: it loads (MapView), then waits for a click. */
    void glide() {
        if (viewOrSay() == null) return;
        if (markA == null || markB == null) {
            mode.say("mark view A (1) and view B (2) first");
            return;
        }
        stopMove();
        smooth.still();
        // The glide starts from A's month: its map is the one the load begins with.
        mode.showMonth(markA.month());
        from = markA;
        to = markB;
        beginLoad(true, false, glideSeconds);
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
        beginLoad(false, true, secs);
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
        beginLoad(false, false, secs);
    }

    /** The move set up (from, to): its load begins - MapView walks the plan's views and reports back (loading(), loadDone(), loadFailed()). */
    private void beginLoad(boolean glide, boolean out, double secs) {
        phase = Phase.LOAD;
        serial++;
        glideKind = glide;
        flyingOut = out;
        t = 0;
        seconds = secs;
        loadStartNanos = System.nanoTime();
        shownLeft = 0;
        status = LOADING;
        ui.replayWake();
    }

    /**
     * MapView's report while it loads (CM1): {@code done} of {@code of} views walked, {@code passed} of them given up. The bar says
     * "Loading the move..." until a second has passed and ten views are walked, then the seconds left at the pace so far, rounded up;
     * a figure up to two seconds above the last shown stays at the last, so the countdown does not jitter upward.
     */
    void loading(int done, int of, int passed) {
        if (phase != Phase.LOAD) return;
        double elapsed = (System.nanoTime() - loadStartNanos) / 1e9;
        if (elapsed < 1 || done < 10) {
            status = LOADING;
            return;
        }
        int left = Math.max(1, (int) Math.ceil(elapsed / done * (of - done)));
        if (shownLeft > 0 && left > shownLeft && left <= shownLeft + 2) left = shownLeft;
        shownLeft = left;
        status = LOADING + " " + left + " s";
    }

    /** MapView has loaded the whole move (CM1): it waits for the click. {@code passed} views could not be loaded whole and are counted. */
    void loadDone(int passed) {
        if (phase != Phase.LOAD) return;
        phase = Phase.READY;
        status = "Ready - click to run" + (passed > 0 ? " (" + passed + (passed == 1 ? " view" : " views") + " could not be loaded whole)" : "");
        ui.replayWake();
    }

    /** MapView could not load the move (CM1): it is let go and the bar says why. */
    void loadFailed(String why) {
        stopMove();
        mode.say(why);
    }

    /** The click (or a key) on a loaded move runs it (CM1); nothing from any other phase. */
    void run() {
        if (phase != Phase.READY) return;
        phase = glideKind ? Phase.GLIDE : Phase.FLY;
        t = 0;
        status = glideKind ? "gliding 0%" : flyingOut ? "flying out 0%" : "flying in 0%";
        ui.replayWake();
    }

    /** A move let go where it is (Esc, a button) - while it loads or waits for its click as in flight; the smooth hand's moves go on. */
    void stop() {
        if (phase == Phase.IDLE) return;
        String was = phase == Phase.LOAD ? "loading the move" : phase == Phase.READY ? "the loaded move was not run" : status;
        stopMove();
        mode.say("stopped: " + was);
    }

    /** A hand on the mouse, the wheel or W A S D takes the camera from a move: it stops where it is. */
    void hand() {
        if (phase != Phase.IDLE) stop();
    }

    /** The map closed (or another screen is up) while a move was loading, waiting or in flight. */
    void mapGone() {
        if (phase != Phase.IDLE) {
            stopMove();
            mode.say("the map closed: the move stopped");
        }
    }

    /** X: every drift and move stopped, a loading or loaded move, a glide or fly with them. */
    void stillAll() {
        stopMove();
        smooth.still();
        mode.say("every drift and move stopped");
    }

    private void stopMove() {
        phase = Phase.IDLE;
        status = null;
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
     * One frame (MapView.replayCamera()): {@code dt} seconds since the last. A move that loads asks for the frames to go on (MapView's
     * loader does the work) and one that waits for its click for none; a glide or a fly advances its clock and returns the view at
     * it (the glide moving the month on screen with it); otherwise the smooth hand takes its step.
     */
    Shot frame(double dt) {
        switch (phase) {
            case LOAD:
                return new Shot(null, null, true);
            case READY:
                return Shot.IDLE;
            case GLIDE:
            case FLY:
                return advance(dt);
            default:
                if (!smooth.moving()) return Shot.IDLE;
                CameraPath.Move m = smooth.step(dt);
                return new Shot(null, m.none() ? null : m, smooth.moving());
        }
    }

    private Shot advance(double dt) {
        t = Math.min(1, t + Math.min(Math.max(0, dt), MOST_DT_S) / seconds);
        CameraPath.View v = viewAt(t);
        if (glideKind) {
            mode.showMonth(v.month());
            status = String.format("gliding %.0f%%", t * 100);
        } else if (flyingOut) {
            status = String.format("flying out %.0f%%", t * 100);
        } else {
            status = String.format("flying in %.0f%%", t * 100);
        }
        if (t >= 1) {
            boolean glided = glideKind;
            stopMove();
            mode.say(glided ? "the glide is done" : flyingOut ? "flown out: I flies back in" : "flown back in");
            return new Shot(v, null, false);
        }
        return new Shot(v, null, true);
    }
}
