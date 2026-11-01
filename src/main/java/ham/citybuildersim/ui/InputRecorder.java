package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import ham.citybuildersim.InputLog;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * The window's input kept for the replay's ghost (TR1, 2026-10-31): while "Record this city" is on, the mouse and the keys
 * of the sitting - and the markers beside them: the window's size, the screen open, the expanded map's view, the clock and
 * the month - go to an {@link InputLog.Writer} in the recording's folder, each with its time in milliseconds since the
 * sitting's log was opened. The log's format, its writer and its reader are InputLog's; this is the screen's half: what to
 * listen to and when.
 *
 * <p>Active exactly while the city on screen is recording and no replay is on. The events come from filters on the scene
 * (a filter runs before any control, so a control that consumes an event does not hide it from the log); the markers are
 * polled once a frame from a timer of its own, cheap reads that are logged only when they change. Everything here runs on
 * the FX thread; the writer's own thread is the only one that touches the file, and it is handed events and nothing else.
 *
 * WHY IT EXISTS (TR1): the ghost cursor of the replay's second half (TR2) shows where the player was and re-enacts what he
 * pressed, and it can only play back what was kept from the start - an input log started after the fact has nothing in it.
 * Kept in the recording's folder beside the months, per sitting, and closed (flushed) before Quit's System.exit and when the
 * window closes, so a sitting's end is on disk.
 */
final class InputRecorder {

    /** The smallest change of a width, a height or a map's centre that is worth a marker: below it the polled value is the same view. */
    private static final double MARKER_EPSILON = 1e-6;

    private final UserInterface ui;
    private InputLog.Writer writer;
    /** The game and the recording the open writer is of: another city, or another id, closes it and opens the next. */
    private Game openFor;
    private String openId;
    /** When the log was opened (System.nanoTime()): every event's ms is the time since. */
    private long openedNanos;
    /** The last move or drag kept, so the filters do not build the events the writer would drop (InputLog.MOVE_EVERY_MS). */
    private long lastMoveMs = Long.MIN_VALUE;

    // The markers as last logged: a new value is logged, the same is not.
    private double markedW = Double.NaN, markedH = Double.NaN;
    private String markedScreen;
    private double[] markedView;
    private int markedSpeed = -1;
    private int markedRunning = -1;
    private int markedMonth = Integer.MIN_VALUE;

    private final AnimationTimer poll = new AnimationTimer() {
        @Override public void handle(long now) { tick(); }
    };

    InputRecorder(UserInterface ui) {
        this.ui = ui;
    }

    /** Listens to the scene's mouse and keys, and starts the markers' timer. Once, from start(). */
    void install(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> move(e, false));
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> move(e, true));
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (writer == null) return;
            writer.add(InputLog.Event.press(ms(), e.getSceneX(), e.getSceneY(), button(e.getButton()), e.getClickCount(), mods(e.isShiftDown(), e.isControlDown(), e.isAltDown(), e.isMetaDown())));
        });
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (writer == null) return;
            writer.add(InputLog.Event.release(ms(), e.getSceneX(), e.getSceneY(), button(e.getButton()), mods(e.isShiftDown(), e.isControlDown(), e.isAltDown(), e.isMetaDown())));
        });
        scene.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (writer == null) return;
            writer.add(InputLog.Event.wheel(ms(), e.getSceneX(), e.getSceneY(), e.getDeltaX(), e.getDeltaY(), mods(e.isShiftDown(), e.isControlDown(), e.isAltDown(), e.isMetaDown())));
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> key(e, true));
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> key(e, false));
        poll.start();
    }

    /** Flushes and closes the sitting's log, if one is open: before Quit's System.exit, and when the window closes. */
    void close() {
        InputLog.Writer w = writer;
        writer = null;
        openFor = null;
        openId = null;
        if (w != null) w.close();
    }

    /** Whether the log is being written now. */
    boolean isOpen() { return writer != null; }

    private long ms() {
        return (System.nanoTime() - openedNanos) / 1_000_000L;
    }

    private void move(MouseEvent e, boolean dragging) {
        if (writer == null) return;
        long ms = ms();
        if (lastMoveMs != Long.MIN_VALUE && ms - lastMoveMs < InputLog.MOVE_EVERY_MS) return;
        lastMoveMs = ms;
        writer.add(dragging ? InputLog.Event.drag(ms, e.getSceneX(), e.getSceneY(), button(e.getButton()))
                : InputLog.Event.move(ms, e.getSceneX(), e.getSceneY()));
    }

    private void key(KeyEvent e, boolean down) {
        if (writer == null) return;
        String text = e.getText();
        writer.add(InputLog.Event.key(ms(), down, InputLog.keyCode(e.getCode().getName()),
                mods(e.isShiftDown(), e.isControlDown(), e.isAltDown(), e.isMetaDown()), text == null || text.isEmpty() ? null : text));
    }

    /** The log's button numbers: 1 primary, 2 middle, 3 secondary, 0 none. */
    private static int button(MouseButton b) {
        return switch (b) {
            case PRIMARY -> 1;
            case MIDDLE -> 2;
            case SECONDARY -> 3;
            default -> 0;
        };
    }

    private static int mods(boolean shift, boolean ctrl, boolean alt, boolean meta) {
        return (shift ? InputLog.SHIFT : 0) | (ctrl ? InputLog.CTRL : 0) | (alt ? InputLog.ALT : 0) | (meta ? InputLog.META : 0);
    }

    /* ------------------------------- the markers ------------------------------- */

    /** Whether the city on screen is being recorded and no replay is on. */
    private boolean active() {
        Game g = ui.game;
        return g != null && g.isRecording() && !g.isReplayCopy() && !ui.replayOn() && g.recordingFolder() != null;
    }

    /** One frame: the log opened, kept or closed for the city on screen, and the markers that changed since the last frame. */
    private void tick() {
        if (!active()) {
            if (writer != null) close();
            return;
        }
        Game g = ui.game;
        if (writer != null && (openFor != g || !g.getRecordingId().equals(openId))) close();
        if (writer == null) open(g);
        if (writer == null) return;

        Scene scene = ui.replayScene();
        if (scene != null && (differs(scene.getWidth(), markedW) || differs(scene.getHeight(), markedH))) {
            markedW = scene.getWidth();
            markedH = scene.getHeight();
            writer.add(InputLog.Event.window(ms(), markedW, markedH));
        }
        String screen = ui.currentScreen;
        if (screen != null && !screen.equals(markedScreen)) {
            markedScreen = screen;
            writer.add(InputLog.Event.screen(ms(), screen));
        }
        double[] view = ui.expandedMapView();
        if (view != null && viewMoved(view, markedView)) {
            markedView = view;
            writer.add(InputLog.Event.mapView(ms(), view[0] != 0, view[1], view[2], view[3], view[4]));
        } else if (view == null) {
            markedView = null;
        }
        int running = ui.clockRunning ? 1 : 0;
        if (ui.speedIndex != markedSpeed || running != markedRunning) {
            markedSpeed = ui.speedIndex;
            markedRunning = running;
            writer.add(InputLog.Event.clock(ms(), markedSpeed, running == 1));
        }
        int month = g.getMonth();
        if (month != markedMonth) {
            markedMonth = month;
            writer.add(InputLog.Event.month(ms(), month));
        }
    }

    /** Opens a log for the city's recording: the markers forgotten, so the new log starts with the window, the screen, the view, the clock and the month. */
    private void open(Game g) {
        long now = System.currentTimeMillis();
        writer = InputLog.open(g.recordingFolder(), g.getRecordingId(), now);
        openFor = g;
        openId = g.getRecordingId();
        openedNanos = System.nanoTime();
        lastMoveMs = Long.MIN_VALUE;
        markedW = markedH = Double.NaN;
        markedScreen = null;
        markedView = null;
        markedSpeed = markedRunning = -1;
        markedMonth = Integer.MIN_VALUE;
        System.out.println("Recording: the input log is written to " + writer.path());
    }

    private static boolean differs(double a, double b) {
        return Double.isNaN(b) || Math.abs(a - b) > 0.5;
    }

    /** Whether the map's view {globe, a, b, scale, roll} is another than the last one logged. */
    private static boolean viewMoved(double[] now, double[] was) {
        if (was == null) return true;
        for (int i = 0; i < now.length; i++) {
            if (Math.abs(now[i] - was[i]) > MARKER_EPSILON * Math.max(1, Math.abs(was[i]))) return true;
        }
        return false;
    }
}
