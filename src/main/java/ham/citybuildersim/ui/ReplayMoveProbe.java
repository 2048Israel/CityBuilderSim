package ham.citybuildersim.ui;

import ham.citybuildersim.CameraPath;
import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.GamePrefs;
import ham.citybuildersim.GlobeFrame;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.Recording;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * What a replay's camera moves show unpainted, measured: it opens the real window full screen on a recorded city in replay
 * mode, runs the seven moves a trailer makes (the fly out and back, the glide from the street to the whole Earth and back, a
 * glide within a month and one across months, the first again with the caches warm) and prints, for each, the tally MapView
 * keeps of every frame (MapView.MoveTally): the cells drawn as nothing, from a stand-in or from an older month, the frames
 * drawn at half resolution, the worker's asks, the pictures let go and the frame gaps. A main() run by hand on the PC; it
 * is not in AllChecks, because it opens a window and needs the Earth's file, which the cloud has neither of.
 *
 * WHY IT EXISTS (CM1, 2026-11-02): Jerus films his trailer from a recorded city in replay mode and "half the screen is
 * black while you zoom out well". The batch loads a whole move before it runs it; this measures first, with the camera as the
 * trailer uses it, so the batch can say which cause makes the black frames (tiles not drawn in time, the worker behind,
 * pictures let go for want of room, a stand-in showing, a half-resolution draw) and so that its last check has numbers to
 * hold itself to.
 *
 * HOW IT IS RUN, from the clone's root on the PC (Git Bash), on the Earth's and the nations' files:
 *
 *     java -Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin
 *         -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin
 *         -cp "target/classes;$(cat target/classpath.txt)"
 *         ham.citybuildersim.ui.ReplayMoveProbe RECORDING_FOLDER [grow to month, default 360] [months recorded, default 120]
 *
 * RECORDING_FOLDER is a recording as the game keeps it (recording.json and month-*.bin). If it holds one, the probe replays
 * it; if not, it founds a city on the Earth at Andorra (42.507, 1.522), plays it to the month it is given, records the months
 * after it (a dozen at a time, the writer given time to catch up, so none is left out) and copies the recording there - to be
 * kept for the next run, and for a run on another build, whose moves are then the same city's.
 *
 * WHAT IT PRINTS: the recording, the window's canvas, and for each move its heading, how long the view took to settle before
 * it, how long the move took to load (CM1: the views it walked, the Ready line's words, the heap after it) and to run,
 * and MoveTally.report() for the run (the load's frames are not counted: the map is veiled while it loads).
 *
 * NOTHING OF JERUS'S IS TOUCHED. The cities are on GameFiles.scratch() folders, checked to lie under java.io.tmpdir and
 * deleted when the JVM exits; the replay's copy is on its own scratch folder (ReplayCity); the window's preferences are
 * read from the scratch folder, never from %APPDATA%; the only file written outside is the recording folder it is given.
 *
 * THE WINDOW IS OFF EVERY SCREEN unless -Dcbs.probe.onscreen=true: by default the primary screen's size, undecorated, left of the
 * leftmost screen, so a run does not cover the PC's screen while someone works on it (CM1, 2026-11-03). It still draws
 * every frame; it does take the keyboard's focus when it opens, until another window is clicked.
 * With -Dcbs.probe.film=true the main() measure uses a 1920 x 1080 cinematic canvas, even on a smaller desktop (CM2).
 * -Dcbs.probe.moves=M6 runs only that move; a comma-separated list selects several. -Dcbs.replay.stepProfile=true logs
 * each month's read, decode, whole load or prepared switch to stderr, with the move's LOAD/RUN boundaries.
 *
 * Where JavaFX cannot start - no display, a headless JVM, the jars missing - it says so in one line and exits 0.
 *
 * check() is the public entry ReplayCheck 5d runs (CM1): the same window off screen on a generated world's recording, a short glide
 * loaded and run by a click, one across months run by a key, and Esc while one loads, returned as figures (Checked) for it to assert on.
 */
public final class ReplayMoveProbe {

    private ReplayMoveProbe() { }

    /** Where the probe's own lines go: the console, as it was when the class loaded; the game's narration is silenced once the probe starts (UiProbe's way). */
    private static final PrintStream report = System.out;

    /** The spot the city is founded at when there is no recording: Andorra, on the Earth. */
    private static final String SPOT = "42.507,1.522";

    /** The months a city is grown to, and the months recorded after it, when the arguments say nothing. */
    private static final int GROW_TO = 360, RECORDED = 120;

    /** The months played between two waits for the recording's writer. */
    private static final int RECORD_CHUNK = 12;

    /** The most a view is waited for to settle, and a move to end, in seconds. */
    private static final double SETTLE_MAX_S = 20, MOVE_MAX_S = 120;

    /** The seconds a glide takes. */
    private static final double GLIDE_S = 10;

    /** Whether the window is full screen on the PC's screen (-Dcbs.probe.onscreen=true) rather than the same size off every screen. */
    private static final boolean ON_SCREEN = Boolean.getBoolean("cbs.probe.onscreen");

    private static UserInterface ui;
    private static Stage stage;
    private static ReplayMode mode;
    private static ReplayCamera cam;
    private static MapView map;
    private static Canvas canvas;
    /** The city's centre: latitude and longitude on the Earth; on a generated world (plane) the plot x and y. */
    private static double cityLat, cityLon;

    /** Whether the window's map is a plane (a generated world) rather than the globe: views are then in plots and px a plot, not degrees and km a pixel. */
    private static boolean plane;

    /** Whether the probe's own waiting lines are said: not while the harness runs check() (it prints the figures itself). */
    private static boolean silent;

    /** The recorded months' ends: what goTo() is given. */
    private static int firstMonth, lastMonth;

    /** The private members the probe reaches, found by bind() once the toolkit is up (a build that moved one fails there, naming it). */
    private static Field panKeys, canvasField;
    private static Method replayMode, enter, applyView, requestDraw;

    /** The window moved off every screen at the primary screen's size, then made visible: drawn as full screen would be, seen by nobody. */
    private static void offScreen(Stage s) {
        Rectangle2D b = Screen.getPrimary().getBounds();
        double left = Screen.getScreens().stream().mapToDouble(x -> x.getBounds().getMinX()).min().orElse(0);
        s.setFullScreen(false);
        s.setMaximized(false);
        s.setWidth(b.getWidth());
        s.setHeight(b.getHeight());
        s.setX(left - b.getWidth() - 200);
        s.setY(b.getMinY());
        s.setOpacity(1);
    }

    private static void bind() {
        panKeys = field(UserInterface.class, "panKeys");
        canvasField = field(MapView.class, "canvas");
        replayMode = method(UserInterface.class, "replayMode");
        enter = method(ReplayMode.class, "enter", Recording.class);
        applyView = method(MapView.class, "applyCameraView", CameraPath.View.class);
        requestDraw = method(MapView.class, "requestDraw");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            report.println("ReplayMoveProbe: RECORDING_FOLDER [grow to month, default " + GROW_TO + "] [months recorded, default "
                    + RECORDED + "]; see the class comment.");
            return;
        }
        String cannot = startJavaFx();
        if (cannot != null) {
            report.println("ReplayMoveProbe: JavaFX cannot start here (" + cannot + "); nothing was measured.");
            System.exit(0);
        }
        try {
            bind();
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            run(args);
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static void run(String[] args) throws Exception {
        Path folder = Path.of(args[0]).toAbsolutePath().normalize();
        int growTo = args.length > 1 ? Integer.parseInt(args[1]) : GROW_TO;
        int recorded = args.length > 2 ? Integer.parseInt(args[2]) : RECORDED;

        // 2. the recording, and a game for the window to start on
        Game start;
        if (Files.isRegularFile(folder.resolve(Recording.META_FILE))) {
            report.println("recording: " + folder + " is there; replaying it");
            GameFiles files = scratch("cm1window");
            if (files == null) return;
            start = fx(() -> found(files));
        } else {
            start = record(folder, growTo, recorded);
            if (start == null) return;
        }
        Recording rec = Recording.at(folder);
        int[] months = rec.months();
        if (rec.meta() == null || months.length == 0) throw new IllegalStateException("no months in the recording at " + folder);
        firstMonth = months[0];
        lastMonth = months[months.length - 1];
        report.printf("recording %s: %d months, %d to %d%n", rec.id(), months.length, firstMonth, lastMonth);

        // 3. the window, full screen, in the replay with the expanded map open
        Game game = start;
        GamePrefs prefs = GamePrefs.load(game.getGameFiles());
        prefs.setFullScreen(false);
        prefs.save(game.getGameFiles());
        fx(() -> {
            ui = new UserInterface(game);
            stage = new Stage();
            if (!ON_SCREEN) {
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setOpacity(0);
            }
            ui.start(stage);
            if (ON_SCREEN) stage.setFullScreen(true);
            else offScreen(stage);
            mode = (ReplayMode) call(replayMode, ui);
            call(enter, mode, rec);
            if (!ui.replayOn()) throw new IllegalStateException("the replay did not open on " + folder + " (the window's list says why)");
            ui.replayOpenMap();
            map = (MapView) get(panKeys, ui);
            cam = ui.replayCamera();
            cam.setGlideSeconds(GLIDE_S);
            canvas = (Canvas) get(canvasField, map);
            if (Boolean.getBoolean("cbs.probe.film")) {
                call(method(ReplayMode.class, "setCinematic", boolean.class), mode, true);
                stage.setWidth(ReplayMode.FILM_W);
                stage.setHeight(ReplayMode.FILM_H);
                if (!ON_SCREEN) {
                    double left = Screen.getScreens().stream().mapToDouble(s -> s.getBounds().getMinX()).min().orElse(0);
                    stage.setX(left - stage.getWidth() - 200);
                }
            }
            return null;
        });
        CameraPath.View whole = waitForWhole();
        cityLat = whole.a();
        cityLon = whole.b();
        waitForCanvas();
        if (Boolean.getBoolean("cbs.probe.film") && fx(() -> canvas.getWidth() != ReplayMode.FILM_W || canvas.getHeight() != ReplayMode.FILM_H)) {
            throw new IllegalStateException("the film probe needs a 1920 x 1080 canvas");
        }
        if (Boolean.getBoolean("cbs.probe.film") && !ON_SCREEN && fx(() -> Screen.getScreens().stream().anyMatch(s ->
                s.getBounds().intersects(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())))) {
            throw new IllegalStateException("the film probe must stay off every screen");
        }

        double street = GlobeFrame.leastKmPx();
        double handoff = GlobeFrame.HANDOFF_KM;
        double eastDegrees = 12 / (111.32 * Math.cos(Math.toRadians(cityLat)));

        // 5. the moves
        goTo(lastMonth);
        take(at(street));
        settle("street view");
        move("M1 fly out, the street to the whole Earth", cam::flyOut, false);
        move("M2 fly in, straight after M1", cam::flyIn, false);
        zoomOutGlide("M3 zoom-out glide, the street to the whole Earth, " + (int) GLIDE_S + " s");

        goTo(lastMonth);
        take(at(street));
        mark(false);
        take(wholeNow());
        mark(true);
        settle("whole Earth");
        move("M4 zoom-in glide, the whole Earth to the street, " + (int) GLIDE_S + " s", cam::glide, true);

        goTo(lastMonth);
        take(new CameraPath.View(true, cityLat, cityLon + eastDegrees, 4 * street, 0));
        mark(false);
        take(at(4 * street));
        mark(true);
        settle("view A");
        move("M5 glide within a month, 12 km east at " + String.format("%.4f", 4 * street) + " km a pixel", cam::glide, true);

        goTo(lastMonth);
        take(at(handoff / 16));
        mark(false);
        goTo(firstMonth);
        take(at(handoff / 8));
        mark(true);
        settle("view A");
        move("M6 glide across months, " + firstMonth + " to " + lastMonth + ", the handoff over 8 to over 16", cam::glide, true);

        zoomOutGlide("M7 = M3 again, the caches warm from the moves before");

        // 6. close
        report.printf("%nheap used %d MB%n", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20);
    }

    /* ------------------------------------------ the recording ------------------------------------------ */

    /** The city founded on the Earth at SPOT, on a FX-thread call; a friendlier line when the Earth's file is not there. */
    private static Game found(GameFiles files) {
        try {
            return LongPlaytest.probeCity(files, SPOT);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("the city could not be founded on the Earth at " + SPOT + " (" + e.getMessage()
                    + "): -Dcbs.earth=PATH-TO-earth-2.bin is needed", e);
        }
    }

    /** A scratch save folder, checked to lie under java.io.tmpdir (UiProbe's rule); null, after saying so, when it does not. */
    private static GameFiles scratch(String label) {
        GameFiles files = GameFiles.scratch(label);
        Path tmp = Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
        if (!files.getDirectory().toAbsolutePath().normalize().startsWith(tmp)) {
            report.println("ReplayMoveProbe: refusing to run, its save folder " + files.getDirectory() + " is not under " + tmp + ".");
            return null;
        }
        return files;
    }

    /** Founds a city, grows it to growTo, records `recorded` months more and copies the recording to `folder`; the grown city, or null if the scratch folder was refused. */
    private static Game record(Path folder, int growTo, int recorded) throws Exception {
        GameFiles files = scratch("cm1probe");
        if (files == null) return null;
        long t0 = System.nanoTime();
        Game g = fx(() -> found(files));
        report.printf("founded in %.1f s (Earth: %s)%n", (System.nanoTime() - t0) / 1e9, SPOT);
        long t1 = System.nanoTime();
        fx(() -> {
            LongPlaytest.probePlay(g, growTo);
            return null;
        });
        report.printf("grown to month %d in %.1f s, %,d people%n", g.getMonth(), (System.nanoTime() - t1) / 1e9,
                g.getPopulationManager().getPopulation());
        long t2 = System.nanoTime();
        int target = growTo + recorded;
        fx(() -> {
            g.setRecording(true);
            while (g.getMonth() < target) {
                LongPlaytest.probePlay(g, Math.min(target, g.getMonth() + RECORD_CHUNK));
                g.awaitSaves();
            }
            g.setRecording(false);
            g.awaitSaves();
            return null;
        });
        Path from = g.recordingFolder();
        if (from == null || !Files.isDirectory(from)) throw new IllegalStateException("the city wrote no recording");
        copyTree(from, folder);
        int kept = Recording.at(folder).months().length;
        report.printf("recorded to month %d in %.1f s, %,d people; %d months kept, %d left out; copied to %s%n", g.getMonth(),
                (System.nanoTime() - t2) / 1e9, g.getPopulationManager().getPopulation(), kept, g.getMonthsNotRecorded(), folder);
        return g;
    }

    private static void copyTree(Path from, Path to) throws IOException {
        try (Stream<Path> walk = Files.walk(from)) {
            for (Path p : (Iterable<Path>) walk::iterator) {
                Path target = to.resolve(from.relativize(p).toString());
                if (Files.isDirectory(p)) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    /* ------------------------------------------ views and waiting ------------------------------------------ */

    /** A view on the globe at the city's centre, `kmPx` km a pixel (its month is the camera's to put on: mark() takes the month on screen). */
    private static CameraPath.View at(double kmPx) {
        return new CameraPath.View(!plane, cityLat, cityLon, kmPx, 0);
    }

    /** The widest view as it stands now (its scale follows the canvas), the whole Earth facing the city. */
    private static CameraPath.View wholeNow() throws Exception {
        CameraPath.View w = fx(() -> ui.expandedMapWhole());
        if (w == null || (!plane && !w.globe())) throw new IllegalStateException("the expanded map has no globe: the city must be on the Earth");
        return w;
    }

    /** Waits for the expanded map's globe to exist, and returns the whole-Earth view (its centre is the city's). */
    private static CameraPath.View waitForWhole() throws Exception {
        long t0 = System.nanoTime();
        while ((System.nanoTime() - t0) / 1e9 < 30) {
            CameraPath.View w = fx(() -> ui.expandedMapWhole());
            if (w != null && w.globe()) return w;
            Thread.sleep(200);
        }
        throw new IllegalStateException("the expanded map showed no globe in 30 s: is the city on the Earth (-Dcbs.earth)?");
    }

    /** Waits for the canvas to keep one size over three looks (the full screen takes a moment), and prints it. */
    private static void waitForCanvas() throws Exception {
        String size = "";
        int same = 0;
        for (int i = 0; i < 75 && same < 3; i++) {
            Thread.sleep(200);
            double[] wh = fx(() -> new double[] { canvas.getWidth(), canvas.getHeight() });
            String now = wh[0] + " x " + wh[1];
            same = now.equals(size) && wh[0] > 100 ? same + 1 : 0;
            size = now;
        }
        tell("window: full screen %b, the map's canvas %s px%n", fx(() -> stage.isFullScreen()), size);
    }

    /** Puts the recorded month nearest `month` on screen, as the camera does. */
    private static void goTo(int month) throws Exception {
        fx(() -> {
            mode.showMonth(month);
            return null;
        });
    }

    /** The expanded map's view taken whole and drawn (what a glide's or a fly's frame does). */
    private static void take(CameraPath.View v) throws Exception {
        fx(() -> {
            call(applyView, map, v);
            call(requestDraw, map);
            return null;
        });
    }

    private static void mark(boolean a) throws Exception {
        fx(() -> {
            cam.mark(a);
            return null;
        });
    }

    /** One of the probe's waiting lines, unless check() runs it. */
    private static void tell(String format, Object... arguments) {
        if (!silent) report.printf(format, arguments);
    }

    /** Waits, at most SETTLE_MAX_S, until MapView.viewSettled() has held over two looks 100 ms apart, and prints how long it took. */
    private static void settle(String what) throws Exception {
        long t0 = System.nanoTime();
        int held = 0;
        while (true) {
            Thread.sleep(100);
            double s = (System.nanoTime() - t0) / 1e9;
            boolean idle = fx(() -> map.viewSettled());
            held = idle ? held + 1 : 0;
            if (held >= 2) {
                tell("  %s settled in %.1f s%n", what, s);
                return;
            }
            if (s >= SETTLE_MAX_S) {
                tell("  %s NOT settled after %.0f s; going on%n", what, s);
                return;
            }
        }
    }

    /* ------------------------------------------ the moves ------------------------------------------ */

    /** M3 and M7: the street at the city marked A, the whole Earth B, the street taken again and settled, then the glide. */
    private static void zoomOutGlide(String heading) throws Exception {
        goTo(lastMonth);
        take(at(GlobeFrame.leastKmPx()));
        settle("street view");
        mark(true);
        take(wholeNow());
        mark(false);
        take(at(GlobeFrame.leastKmPx()));
        settle("street view");
        move(heading, cam::glide, true);
    }

    /** The most a move's load is waited for, in seconds, before the probe stops it and says so. */
    private static final double LOAD_MAX_S = 900;

    /**
     * Runs a move: started, its load waited for (at most LOAD_MAX_S) and printed, then run as a click runs it (the tally reset in the
     * same FX task), polled until the camera is idle (at most MOVE_MAX_S), and printed under its heading. A move that does not reach
     * Ready runs nothing.
     */
    private static void move(String heading, Runnable start, boolean glide) throws Exception {
        String selected = System.getProperty("cbs.probe.moves");
        if (selected != null && !java.util.Arrays.asList(selected.split(",")).contains(heading.substring(0, 2))) return;
        MapView.MoveTally tally = map.moveTally();
        if (Boolean.getBoolean("cbs.replay.stepProfile")) System.err.println(heading + " LOAD");
        long t0 = System.nanoTime();
        fx(() -> {
            start.run();
            return null;
        });
        String phase = fx(() -> cam.phaseName());
        while (phase.equals("LOAD")) {
            Thread.sleep(25);
            phase = fx(() -> cam.phaseName());
            if ((System.nanoTime() - t0) / 1e9 >= LOAD_MAX_S) {
                fx(() -> {
                    cam.stop();
                    return null;
                });
                phase = "LOAD TIMED OUT";
                break;
            }
        }
        double loadS = (System.nanoTime() - t0) / 1e9;
        String words = fx(() -> cam.overlay());
        int views = fx(() -> cam.planViews());
        long heap = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20;
        report.printf("%n=== %s ===%n", heading);
        if (!phase.equals("READY")) {
            report.printf("  the move did not reach Ready: %s after %.1f s (%d plan views); nothing run%n", phase, loadS, views);
            return;
        }
        report.printf("  loaded in %.1f s, %d plan views, heap %d MB: %s%n", loadS, views, heap, words);
        if (Boolean.getBoolean("cbs.replay.stepProfile")) System.err.println(heading + " RUN");
        t0 = System.nanoTime();
        fx(() -> {
            tally.reset();
            cam.run();
            return null;
        });
        phase = fx(() -> cam.phaseName());
        boolean ran = !phase.equals("IDLE");
        boolean timedOut = false;
        while (!phase.equals("IDLE")) {
            Thread.sleep(25);
            double s = (System.nanoTime() - t0) / 1e9;
            phase = fx(() -> cam.phaseName());
            if (s >= MOVE_MAX_S) {
                timedOut = true;
                fx(() -> {
                    cam.stop();
                    return null;
                });
                break;
            }
        }
        double took = (System.nanoTime() - t0) / 1e9;
        if (!ran) report.println("  the camera did not start (it was idle at once: no view marked, or nothing to move)");
        if (timedOut) report.printf("  TIMED OUT: still moving after %.0f s, stopped%n", MOVE_MAX_S);
        report.printf("  took %.1f s%n", took);
        report.print(fx(() -> tally.report()));
    }

    /* ------------------------------------------ the check ReplayCheck runs (5d) ------------------------------------------ */

    /** How long a move's load is waited for in check(), in seconds, before it is stopped and said not to have reached Ready. */
    private static final double CHECK_LOAD_MAX_S = 300;

    /** The scale of the "street" on a plane in check(), in pixels a plot: a level-0 view with the big tiles (MapFrame.BIG_TILES_FROM is 6). */
    private static final double PLANE_STREET_PX = 8;

    /** The frames the normal game's map is asked to draw before the replay is entered, and the most the wait for them takes, in seconds. */
    private static final int NORMAL_FRAMES = 30;
    private static final double NORMAL_MAX_S = 30;

    /** The normal game, before the replay is entered: the map's frames drawn, and whether a camera, a loaded move or a pinned cache exists. */
    public record Normal(long frames, boolean camera, boolean moveHeld, int cachesPinned) { }

    /**
     * One glide, loaded and run: how long the load took and how many plan views it walked, whether it reached Ready and how many views
     * it passed; while it waited, whether the map held the move and how many of the six caches were pinned; how it was run (the click
     * or the key), whether the key was taken and the phase at once after; then the run's tally (frames, frames with anything
     * unpainted, the worker's asks, the pictures let go, and the frames by where they drew) and, after the run, whether the map still
     * held the move and how many caches were pinned (and the seconds it took to let go, -1 if it did not in 3 s); last, the frames
     * whose asks came out below nothing (FB7: none can).
     */
    public record Moved(String name, boolean ready, double loadSeconds, int planViews, int passed, boolean heldWhileReady,
                        int pinnedWhileReady, String ranBy, boolean taken, String phaseAfterTrigger, double runSeconds,
                        long frames, long unpaintedFrames, long asks, long evicted, Map<String, Long> levelFrames,
                        boolean heldAfter, int pinnedAfter, double letGoAfterS, long offPlan, int preparedMonths, int preparedContexts,
                        long asksBelowNothing) { }

    /**
     * FB7's fixture for the count of asks the glides are held to, on a Worker built as the map's own (MapView.Worker, one thread)
     * whose queue stops the thread after it takes a job and before it begins it: the moment getTaskCount(), the old count, counts
     * the job nowhere, and which a loaded machine makes long. The readings, of getTaskCount() and of Worker.asked(): with one job
     * handed in and the thread not yet taking it (queued), with the thread holding it unbegun (inTransit), and after one more job is
     * handed in during that moment (another); and whether the Worker ended when let go.
     */
    public record Transit(long taskCountQueued, long taskCountInTransit, long askedQueued, long askedInTransit, long askedAnother,
                          boolean ended) { }

    /** Causes the moment Transit names, by latches and no clock, and reads both counts across it. Needs no JavaFX. */
    public static Transit inTransit() throws Exception {
        CountDownLatch atTake = new CountDownLatch(1), mayTake = new CountDownLatch(1), taken = new CountDownLatch(1), mayBegin = new CountDownLatch(1);
        java.util.concurrent.atomic.AtomicInteger takes = new java.util.concurrent.atomic.AtomicInteger();
        java.util.concurrent.LinkedBlockingQueue<Runnable> queue = new java.util.concurrent.LinkedBlockingQueue<>() {
            @Override
            public Runnable take() throws InterruptedException {
                if (takes.getAndIncrement() > 0) return super.take();
                // The thread's first take, after its first job (run as the job it was made with, never queued) has ended.
                atTake.countDown();
                mayTake.await();
                Runnable job = super.take();
                taken.countDown();
                mayBegin.await();
                return job;
            }
        };
        MapView.Worker worker = new MapView.Worker(queue, r -> {
            Thread t = new Thread(r, "transit-fixture");
            t.setDaemon(true);
            return t;
        });
        try {
            worker.execute(() -> { });
            if (!atTake.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("the fixture's worker never came to take a job");
            worker.execute(() -> { });
            long countQueued = worker.getTaskCount(), askedQueued = worker.asked();
            mayTake.countDown();
            if (!taken.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("the fixture's worker never took its job");
            long countInTransit = worker.getTaskCount(), askedInTransit = worker.asked();
            worker.execute(() -> { });
            long askedAnother = worker.asked();
            mayBegin.countDown();
            worker.shutdown();
            boolean ended = worker.awaitTermination(10, TimeUnit.SECONDS);
            return new Transit(countQueued, countInTransit, askedQueued, askedInTransit, askedAnother, ended);
        } finally {
            mayTake.countDown();
            mayBegin.countDown();
            worker.shutdownNow();
        }
    }

    /** Escape pressed while a glide loads: whether it was loading, whether the key was taken, and where the camera and the map were left. */
    public record Cancelled(boolean wasLoading, boolean taken, String phaseAfter, boolean heldAfter, int pinnedAfter) { }

    /** Leaving a Ready move, measured in the same FX call before a detached canvas could tick again (CM2). */
    public record Left(boolean ready, int preparedBefore, boolean replayAfter, boolean heldAfter, int pinnedAfter,
                       int preparedAfter, long chartPoints, long chartMissed) { }

    /**
     * What check() measured. {@code cannot} is non-null, and nothing else, when JavaFX cannot start here; {@code globe} whether the
     * window's map was the Earth's (else a generated world's plane).
     */
    public record Checked(String cannot, boolean globe, Normal normal, Moved zoomOut, Moved months, Cancelled escape, Left left,
                          Map<String, Boolean> followups) { }

    /**
     * CM1's check, run by ReplayCheck 5d (2026-11-03): the window off screen on a game of its own (windowFiles), first as the normal
     * game draws its map, then in the replay of the recording at recordingFolder with the expanded map open, where it loads and runs
     * a zoom-out glide (the street to the whole map, by a click on the map), a glide across the recording's months (by a key) and a
     * glide cancelled with Escape while it loads, each {@code glideSeconds} long. The window is closed at the end; the toolkit is left
     * up. Nothing is printed and the game's narration is silenced; main() is the long measure of the same, ten seconds a move.
     */
    public static Checked check(Path recordingFolder, GameFiles windowFiles, double glideSeconds) throws Exception {
        String cannot = startJavaFx();
        if (cannot != null) return new Checked(cannot, false, null, null, null, null, null, null);
        PrintStream was = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        silent = true;
        Map<String, Boolean> followups = new LinkedHashMap<>();
        String cleanup = "check cleanup turns replay off and unregisters both pumps before the timed section";
        followups.put(cleanup, false);
        try {
            bind();
            return measureAll(recordingFolder, windowFiles, glideSeconds, followups);
        } finally {
            silent = false;
            System.setOut(was);
            try {
                fx(() -> {
                    if (mode != null) mode.leave();
                    if (mode != null) ((AnimationTimer) get(field(ReplayMode.class, "timer"), mode)).stop();
                    Object mini = ui == null ? null : get(field(UserInterface.class, "miniMap"), ui);
                    if (stage != null && stage.getScene() != null) stage.getScene().setRoot(new javafx.scene.Group());
                    if (ui != null) {
                        AnimationTimer clock = (AnimationTimer) get(field(UserInterface.class, "clock"), ui);
                        if (clock != null) clock.stop();
                    }
                    stopPump(map);
                    stopPump(mini);
                    // A late worker adoption still asks for a frame; a detached canvas must not register its pump again.
                    if (map != null) call(requestDraw, map);
                    followups.put(cleanup,
                            ui != null && !ui.replayOn() && map != null && !(Boolean) get(field(MapView.class, "running"), map)
                                    && canvas.getScene() == null && mini != null && ((MiniMap) mini).node().getScene() == null
                                    && !(Boolean) get(field(MiniMap.class, "running"), mini));
                    if (stage != null) stage.close();
                    return null;
                });
            } catch (Exception ignored) {
                // the window was not up
            }
            ui = null;
            stage = null;
            mode = null;
            cam = null;
            map = null;
            canvas = null;
            plane = false;
        }
    }

    private static Checked measureAll(Path folder, GameFiles windowFiles, double glideSeconds, Map<String, Boolean> followups) throws Exception {
        Recording rec = Recording.at(folder);
        int[] ms = rec.months();
        if (rec.meta() == null || ms.length < 2) throw new IllegalStateException("fewer than two months in the recording at " + folder);
        firstMonth = ms[0];
        lastMonth = ms[ms.length - 1];

        // The window: off every screen, on a city of its own. It is the normal game first.
        Game game = LongPlaytest.probeCity(windowFiles, null);
        GamePrefs prefs = GamePrefs.load(windowFiles);
        prefs.setFullScreen(false);
        prefs.save(windowFiles);
        fx(() -> {
            ui = new UserInterface(game);
            stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setOpacity(0);
            ui.start(stage);
            offScreen(stage);
            ui.replayAnotherCity();
            ui.replayOpenCity();
            ui.openMapOn(null);
            return null;
        });
        map = waitForMap();
        canvas = (Canvas) get(canvasField, map);
        waitForCanvas();
        long t0 = System.nanoTime();
        while (fx(() -> map.framesDrawn()) < NORMAL_FRAMES && (System.nanoTime() - t0) / 1e9 < NORMAL_MAX_S) {
            fx(() -> {
                call(requestDraw, map);
                return null;
            });
            Thread.sleep(20);
        }
        Normal normal = fx(() -> new Normal(map.framesDrawn(), ui.replayCamera() != null, map.moveHeld(), map.cachesPinned()));

        // The replay, with the expanded map open.
        fx(() -> {
            mode = (ReplayMode) call(replayMode, ui);
            call(enter, mode, rec);
            if (!ui.replayOn()) throw new IllegalStateException("the replay did not open on " + folder);
            ui.replayOpenMap();
            return null;
        });
        t0 = System.nanoTime();
        CameraPath.View whole = null;
        while (whole == null && (System.nanoTime() - t0) / 1e9 < 30) {
            Thread.sleep(200);
            whole = fx(() -> ui.expandedMapWhole());
        }
        if (whole == null) throw new IllegalStateException("the replay's expanded map did not open in 30 s");
        plane = !whole.globe();
        fx(() -> {
            map = (MapView) get(panKeys, ui);
            canvas = (Canvas) get(canvasField, map);
            cam = ui.replayCamera();
            cam.setGlideSeconds(glideSeconds);
            return null;
        });
        waitForCanvas();
        if (plane) {
            CameraPath.View opening = fx(() -> ui.expandedMapCameraView());
            cityLat = opening.a();
            cityLon = opening.b();
        } else {
            cityLat = whole.a();
            cityLon = whole.b();
        }
        double street = plane ? PLANE_STREET_PX : GlobeFrame.leastKmPx();
        double handoff = GlobeFrame.HANDOFF_KM;
        double first = plane ? PLANE_STREET_PX / 2 : handoff / 8, last = plane ? PLANE_STREET_PX : handoff / 16;

        // The zoom-out glide, by a click: the street at the last month marked A, the whole map B (ReplayMoveProbe's M3).
        goTo(lastMonth);
        take(at(street));
        settle("street view");
        mark(true);
        take(wholeNow());
        mark(false);
        take(at(street));
        settle("street view");
        Moved zoomOut = measureGlide("zoom-out", false);

        // The glide across months, by a key: the first month wide, the last month near (M6).
        markMonths(first, last);
        Moved months = measureGlide("across months", true);

        // Escape while a glide loads.
        markMonths(first, last);
        Cancelled escape = cancelWhileLoading();
        reviewFollowups(followups);
        Left left = leaveWhileReady();
        // Leave one more replay active: check()'s finally, rather than leaveWhileReady(), must clean it up.
        fx(() -> { call(enter, mode, rec); return null; });
        return new Checked(null, !plane, normal, zoomOut, months, escape, left, followups);
    }

    /** Stop and unregister a harness window's pump now; a closed stage alone does not unregister AnimationTimer (CM1b). */
    private static void stopPump(Object owner) throws Exception {
        if (owner == null) return;
        ((AnimationTimer) get(field(owner.getClass(), "pump"), owner)).stop();
        field(owner.getClass(), "running").setBoolean(owner, false);
    }

    /** Cause the review's retained-store, half-view input, stalled-job and heap cases on the real replay window (CM1b). */
    @SuppressWarnings("unchecked")
    private static void reviewFollowups(Map<String, Boolean> results) throws Exception {
        for (KeyCode key : new KeyCode[] { KeyCode.ESCAPE, KeyCode.X }) {
            fx(() -> { cam.glide(); return null; });
            long began = System.nanoTime();
            while (fx(() -> cam.loading()) && (System.nanoTime() - began) / 1e9 < CHECK_LOAD_MAX_S) Thread.sleep(25);
            results.put(key + " while Ready releases the store, pins and prepared months in the same FX call", fx(() -> {
                boolean ready = cam.ready() && map.moveHeld() && cam.moveCity().preparedMonths() >= 2;
                ham.citybuildersim.ReplayCity city = cam.moveCity();
                boolean taken = mode.key(press(key), false);
                boolean released = ready && taken && !cam.busy() && !map.moveHeld() && map.cachesPinned() == 0 && city.preparedMonths() == 0;
                finishFixtureLoad();
                return released;
            }));
        }
        results.put("all other keys are consumed during a load without changing its marks, month, camera or replay", fx(() -> {
            startFixtureLoad();
            CameraPath.View middle = cam.planView(cam.planViews() / 2);
            cam.showMonthOf(middle);
            call(applyView, map, middle);
            CameraPath.View a = cam.markA(), b = cam.markB();
            int serial = cam.serial(), month = mode.shownMonth();
            boolean taken = true;
            for (KeyCode k : new KeyCode[] { KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.O, KeyCode.I, KeyCode.LEFT, KeyCode.RIGHT,
                    KeyCode.SPACE, KeyCode.C, KeyCode.L, KeyCode.W, KeyCode.X, KeyCode.F11, KeyCode.SHIFT }) {
                taken &= mode.key(press(k), false);
            }
            boolean unchanged = taken && cam.loading() && cam.serial() == serial && mode.shownMonth() == month
                    && java.util.Objects.equals(a, cam.markA()) && java.util.Objects.equals(b, cam.markB());
            finishFixtureLoad();
            field(ReplayCamera.class, "markA").set(cam, a);
            field(ReplayCamera.class, "markB").set(cam, b);
            return unchanged;
        }));
        results.put("a map lost at the middle of loading restores the move's starting month and view before detaching", fx(() -> {
            startFixtureLoad();
            CameraPath.View start = cam.startView(), middle = cam.planView(cam.planViews() - 1);
            cam.showMonthOf(middle);
            call(method(MapView.class, "keepUp"), map);
            call(applyView, map, middle);
            boolean changed = mode.shownMonth() != start.month();
            map.collapse(false);
            GlobeFrame ground = (GlobeFrame) get(field(MapView.class, "globe"), map);
            ham.citybuildersim.MapFrame flat = (ham.citybuildersim.MapFrame) get(field(MapView.class, "big"), map);
            CameraPath.View restored = ground != null
                    ? new CameraPath.View(true, ground.centreLat(), ground.centreLon(), ground.kmPx(), 0)
                    : new CameraPath.View(false, flat.centreX(), flat.centreY(), flat.scale(), 0);
            boolean released = !cam.busy() && !map.moveHeld() && map.cachesPinned() == 0 && mode.shownMonth() == start.month();
            finishFixtureLoad();
            ui.replayOpenMap();
            call(applyView, map, start);
            return changed && released && restored.a() == start.a() && restored.b() == start.b() && restored.scale() == start.scale();
        }));
        results.put("passing a missing cell and a district picture remembers both on later views", fx(() -> {
            startFixtureLoad();
            Object m = get(field(MapView.class, "move"), map);
            Object globeWas = get(field(MapView.class, "globe"), map);
            ham.citybuildersim.MapFrame f = (ham.citybuildersim.MapFrame) get(field(MapView.class, "big"), map);
            field(MapView.class, "globe").set(map, null);
            f.set(0, 0, 1);
            Object pictures = call(method(MapView.class, "picturesFor", int.class, int.class), map, f.level(), ham.citybuildersim.MapFrame.tilePx(f.level(), f.scale()));
            ((java.util.Map<?, ?>) pictures).clear();
            java.util.Set<ham.citybuildersim.CityMap.District> asked = (java.util.Set<ham.citybuildersim.CityMap.District>) get(field(MapView.class, "picturesAsked"), map);
            asked.add(ui.game.getCityMap().districts().get(0));
            Method settled = method(MapView.class, "loadSettled", m.getClass());
            boolean missing = !(Boolean) call(settled, map, m);
            call(method(MapView.class, "passView", m.getClass()), map, m);
            boolean remembered = (Boolean) call(settled, map, m);
            asked.clear();
            // The FAR and globe branches must remember cells absent even from the request sets.
            ((java.util.Set<?>) get(field(MapView.class, "nodesAsked"), map)).clear();
            ((java.util.Set<?>) get(field(MapView.class, "chartsAsked"), map)).clear();
            ((java.util.Map<?, ?>) get(field(MapView.class, "nodes"), map)).clear();
            f.set(0, 0, f.leastScale());
            boolean missingNodes = f.level() == ham.citybuildersim.MapFrame.FAR && !(Boolean) call(settled, map, m);
            call(method(MapView.class, "passView", m.getClass()), map, m);
            results.put("unasked FAR nodes are remembered when a view is passed", missingNodes && (Boolean) call(settled, map, m));
            GlobeFrame chartView = new GlobeFrame(canvas.getWidth(), canvas.getHeight(), 42.507, 1.522);
            chartView.set(42.507, 1.522, (GlobeFrame.HANDOFF_KM + GlobeFrame.PICTURE_FROM_KM) / 2);
            field(MapView.class, "globe").set(map, chartView);
            ((java.util.Map<?, ?>) get(field(MapView.class, "charts"), map)).clear();
            boolean missingCharts = !(Boolean) call(settled, map, m);
            call(method(MapView.class, "passView", m.getClass()), map, m);
            results.put("unasked globe chart cells are remembered when a view is passed", missingCharts && (Boolean) call(settled, map, m));
            field(MapView.class, "globe").set(map, globeWas);
            finishFixtureLoad();
            return missing && remembered;
        }));

        // A real WORKER job throws on a missing world. Its failure must be adopted on FX and never asked again by paintNodes().
        long[] key = fx(() -> {
            startFixtureLoad();
            long plots = 1, nx = 123, ny = 456;
            long k = (Long) call(method(MapView.class, "nodeKey", long.class, long.class, long.class), null, plots, nx, ny);
            long[] wanted = { plots, nx, nx, ny, ny };
            field(MapView.class, "nodesWanted").set(map, wanted);
            ((java.util.Set<Long>) get(field(MapView.class, "nodesAsked"), map)).add(k);
            call(method(MapView.class, "askNodes", java.util.List.class, long.class, long.class, ham.citybuildersim.World.Key.class, long[].class),
                    map, new java.util.ArrayList<>(java.util.List.of(new long[] { nx, ny, k })), plots, (long) ham.citybuildersim.MapFrame.NODE_PX, null, wanted);
            return new long[] { k, nx, ny };
        });
        MapView.Worker worker = MapView.WORKER;
        worker.submit(() -> {}).get(10, TimeUnit.SECONDS);
        results.put("a failed worker job is given up on FX and later paints never ask it again", fx(() -> {
            Object m = get(field(MapView.class, "move"), map);
            boolean failed = m != null && ((java.util.Set<Long>) get(field(m.getClass(), "stuckNodes"), m)).contains(key[0]);
            long tasks = worker.asked();
            for (int i = 0; i < 3; i++) call(method(MapView.class, "paintNodes", long.class, long.class, long.class, long.class, long.class, long.class, long.class, ham.citybuildersim.MapFrame.class),
                    map, 1L, key[1], key[1], key[2], key[2], System.nanoTime() + 1_000_000_000L, ui.game.getCityMap().picturesKept(), new ham.citybuildersim.MapFrame(100, 100));
            finishFixtureLoad();
            return failed && tasks == worker.asked();
        }));
        CountDownLatch busy = new CountDownLatch(1), release = new CountDownLatch(1);
        worker.submit(() -> { busy.countDown(); try { release.await(10, TimeUnit.SECONDS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } });
        busy.await(10, TimeUnit.SECONDS);
        try {
            results.put("the overall load cap fires while the worker is busy and the card names its dial", fx(() -> {
                startFixtureLoad();
                Object m = get(field(MapView.class, "move"), map);
                field(m.getClass(), "started").setLong(m, System.nanoTime() - (long) ((MapView.LOAD_MAX_S + 1) * 1e9));
                call(method(MapView.class, "loadStep", ReplayCamera.class), map, cam);
                String words = (String) get(field(ReplayMode.class, "note"), mode);
                return !cam.busy() && !map.moveHeld() && map.cachesPinned() == 0 && words.contains((int) MapView.LOAD_MAX_S + " s load limit");
            }));
        } finally { release.countDown(); }
        long most = 1_000_000, start = (long) Math.ceil(most * (1 + MapView.LOAD_HEAP_SHARE) / 2);
        MapView.LoadMemory memory = new MapView.LoadMemory(start, most);
        int[] collections = { 0 };
        boolean baselineFits = start > MapView.LOAD_HEAP_SHARE * most && !memory.over(() -> start, () -> collections[0]++);
        boolean growthFits = !memory.over(() -> memory.limit, () -> collections[0]++);
        boolean over = memory.over(() -> memory.limit + 1, () -> collections[0]++);
        for (int i = 0; i < 5; i++) over &= memory.over(() -> memory.limit + 1, () -> collections[0]++);
        results.put("a city starting above the old heap threshold can load and excess growth collects at most once", baselineFits && growthFits && over && collections[0] == 1);
        MapView.LoadMemory reclaimed = new MapView.LoadMemory(0, most);
        long[] used = { reclaimed.limit + 1 };
        int[] reclaimedCollections = { 0 };
        boolean recovered = !reclaimed.over(() -> used[0], () -> { reclaimedCollections[0]++; used[0] = 0; });
        used[0] = reclaimed.limit + 1;
        results.put("a collection that recovers room is still the load's only collection", recovered
                && reclaimed.over(() -> used[0], () -> reclaimedCollections[0]++) && reclaimedCollections[0] == 1);
    }

    private static KeyEvent press(KeyCode key) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key, false, false, false, false);
    }

    private static void startFixtureLoad() {
        cam.glide();
        cam.showMonthOf(cam.startView());
        call(method(MapView.class, "keepUp"), map);
        call(applyView, map, cam.startView());
        call(method(MapView.class, "beginMove", ReplayCamera.class), map, cam);
    }

    /** Restore the fixture even when the cancellation under test is broken, so a later finding has its own premise. */
    private static void finishFixtureLoad() {
        cam.stop();
        map.cancelMove(cam);
    }

    /** M6's marks: the last month at `last` is B, the first month at `first` is A, and the view is settled on A. */
    private static void markMonths(double first, double last) throws Exception {
        goTo(lastMonth);
        take(at(last));
        mark(false);
        goTo(firstMonth);
        take(at(first));
        mark(true);
        settle("view A");
    }

    /** Waits for the expanded map to exist on the window (the normal game's) and returns its view. */
    private static MapView waitForMap() throws Exception {
        long t0 = System.nanoTime();
        while ((System.nanoTime() - t0) / 1e9 < 30) {
            MapView m = fx(() -> (MapView) get(panKeys, ui));
            if (m != null) return m;
            Thread.sleep(200);
        }
        throw new IllegalStateException("the window showed no map in 30 s");
    }

    /**
     * The glide set up from the marks: loaded (at most CHECK_LOAD_MAX_S), held while it waits, run by a click on the canvas's middle or
     * by a plain key as the window sends it to ReplayMode.key(), and polled until the camera is idle; then what the map still held.
     */
    private static Moved measureGlide(String name, boolean byKey) throws Exception {
        MapView.MoveTally tally = map.moveTally();
        long t0 = System.nanoTime();
        fx(() -> {
            cam.glide();
            return null;
        });
        String phase = fx(() -> cam.phaseName());
        while (phase.equals("LOAD")) {
            Thread.sleep(25);
            phase = fx(() -> cam.phaseName());
            if ((System.nanoTime() - t0) / 1e9 >= CHECK_LOAD_MAX_S) {
                fx(() -> {
                    cam.stop();
                    return null;
                });
                phase = "LOAD TIMED OUT";
            }
        }
        double loadS = (System.nanoTime() - t0) / 1e9;
        int views = fx(() -> cam.planViews());
        String words = fx(() -> cam.overlay());
        boolean ready = phase.equals("READY");
        int passed = 0;
        if (words != null) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\((\\d+) views? could not").matcher(words);
            if (m.find()) passed = Integer.parseInt(m.group(1));
        }
        String by = byKey ? "key" : "click";
        if (!ready) return new Moved(name, false, loadS, views, passed, false, 0, by, false, phase, 0, 0, 0, 0, 0, Map.of(), false, 0, -1, 0, 0, 0, 0);
        Thread.sleep(300);      // it waits: nothing runs it, nothing lets go of what it loaded
        boolean held = fx(() -> map.moveHeld() && cam.ready());
        int pinned = fx(() -> map.cachesPinned());
        int preparedMonths = fx(() -> cam.moveCity().preparedMonths());
        int preparedContexts = fx(() -> cam.moveCity().preparedContexts());

        // Run it as the window does: the click is fired at the canvas, the key goes to ReplayMode.key() as UserInterface's filter sends it.
        double cx = canvas.getWidth() / 2, cy = canvas.getHeight() / 2;
        Object[] ran = fx(() -> {
            tally.reset();
            boolean taken = true;
            if (byKey) {
                taken = mode.key(new KeyEvent(KeyEvent.KEY_PRESSED, "z", "z", KeyCode.Z, false, false, false, false), false);
            } else {
                canvas.fireEvent(new MouseEvent(MouseEvent.MOUSE_PRESSED, cx, cy, cx, cy, MouseButton.PRIMARY, 1, false, false, false, false,
                        true, false, false, true, false, false, null));
            }
            return new Object[] { taken, cam.phaseName() };
        });
        boolean taken = (Boolean) ran[0];
        String after = (String) ran[1];
        long t1 = System.nanoTime();
        phase = after;
        while (!phase.equals("IDLE")) {
            Thread.sleep(25);
            phase = fx(() -> cam.phaseName());
            if ((System.nanoTime() - t1) / 1e9 >= MOVE_MAX_S) {
                fx(() -> {
                    cam.stop();
                    return null;
                });
                break;
            }
        }
        double runS = (System.nanoTime() - t1) / 1e9;
        Object[] figures = fx(() -> {
            Map<String, Long> levels = new LinkedHashMap<>();
            for (Map.Entry<String, long[]> e : tally.byWhere.entrySet()) levels.put(e.getKey(), e.getValue()[0]);
            return new Object[] { tally.frames, tally.unpaintedFrames, tally.asks, tally.evicted, levels, tally.offPlan, tally.asksBelowNothing };
        });
        // The map lets go of the move on its next frame (endMove()); give it a few seconds to have one.
        long t2 = System.nanoTime();
        boolean heldAfter = fx(() -> map.moveHeld());
        while (heldAfter && (System.nanoTime() - t2) / 1e9 < 3) {
            Thread.sleep(50);
            heldAfter = fx(() -> map.moveHeld());
        }
        double letGo = heldAfter ? -1 : (System.nanoTime() - t2) / 1e9;
        int pinnedAfter = fx(() -> map.cachesPinned());
        @SuppressWarnings("unchecked")
        Map<String, Long> levels = (Map<String, Long>) figures[4];
        return new Moved(name, true, loadS, views, passed, held, pinned, by, taken, after, runS, (Long) figures[0], (Long) figures[1],
                (Long) figures[2], (Long) figures[3], levels, heldAfter, pinnedAfter, letGo, (Long) figures[5], preparedMonths, preparedContexts,
                (Long) figures[6]);
    }

    /** Test the actual chartNodes range while a real move is retained, then leave without yielding another FX frame. */
    private static Left leaveWhileReady() throws Exception {
        fx(() -> { cam.glide(); return null; });
        long began = System.nanoTime();
        while (fx(() -> cam.loading()) && (System.nanoTime() - began) / 1e9 < CHECK_LOAD_MAX_S) Thread.sleep(25);
        return fx(() -> {
            boolean ready = cam.ready() && map.moveHeld();
            ham.citybuildersim.ReplayCity city = cam.moveCity();
            int preparedBefore = city.preparedMonths();
            GlobeFrame view = new GlobeFrame(ReplayMode.FILM_W, ReplayMode.FILM_H, 42.507, 1.522);
            view.set(42.507, 1.522, (GlobeFrame.HANDOFF_KM + GlobeFrame.PICTURE_FROM_KM) / 2);
            long[] range = (long[]) call(method(MapView.class, "chartNodes", GlobeFrame.class), map, view);
            double step = ((Number) get(field(MapView.class, "NODE_SAMPLE_PX"), null)).doubleValue();
            long points = 0, missed = 0;
            double[] xy = new double[2];
            for (double y = .5; y < view.height(); y += step) for (double x = .5; x < view.width(); x += step) {
                if (!view.onPlanet(x, y) || view.drawsHold(x, y)) continue;
                points++;
                if (!view.chartAt(x, y, xy)) { missed++; continue; }
                long nx = (long) Math.floor(xy[0] / ham.citybuildersim.MapFrame.NODE_PX);
                long ny = (long) Math.floor(xy[1] / ham.citybuildersim.MapFrame.NODE_PX);
                if (range == null || range[0] != ham.citybuildersim.MapFrame.nodePlotsAt(view.planeScale()) || range[1] != view.chart().id()
                        || nx < range[2] || nx > range[3] || ny < range[4] || ny > range[5]) missed++;
            }
            mode.leave();
            return new Left(ready, preparedBefore, ui.replayOn(), map.moveHeld(), map.cachesPinned(), city.preparedMonths(), points, missed);
        });
    }

    /** A glide set up from the marks and, once the map has begun to load it, Escape sent to ReplayMode.key(); then where it all stands. */
    private static Cancelled cancelWhileLoading() throws Exception {
        fx(() -> {
            cam.glide();
            return null;
        });
        long t0 = System.nanoTime();
        while (!fx(() -> cam.loading() && map.moveHeld()) && (System.nanoTime() - t0) / 1e9 < 10) Thread.sleep(10);
        Object[] pressed = fx(() -> {
            boolean loading = cam.loading();
            boolean taken = mode.key(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false), false);
            return new Object[] { loading, taken, cam.phaseName() };
        });
        long t1 = System.nanoTime();
        boolean held = fx(() -> map.moveHeld());
        while (held && (System.nanoTime() - t1) / 1e9 < 5) {
            Thread.sleep(50);
            held = fx(() -> map.moveHeld());
        }
        int pinned = fx(() -> map.cachesPinned());
        return new Cancelled((Boolean) pressed[0], (Boolean) pressed[1], (String) pressed[2], held, pinned);
    }

    /* ------------------------------------------ the toolkit and reflection ------------------------------------------ */

    /** Why JavaFX cannot start here, in a few words, or null when it is up: the one start, FollowBench's (GB5). */
    private static String startJavaFx() { return FollowBench.startJavaFx(); }

    /** Runs on the FX thread and waits for the answer; an exception in it comes back here. */
    private static <T> T fx(Callable<T> c) throws Exception {
        FutureTask<T> t = new FutureTask<>(c);
        Platform.runLater(t);
        return t.get();
    }

    private static Field field(Class<?> c, String name) {
        try {
            Field f = c.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("ReplayMoveProbe: " + c.getSimpleName() + "." + name + " (a field) is not there any more; the probe needs updating", e);
        }
    }

    private static Method method(Class<?> c, String name, Class<?>... parameters) {
        try {
            Method m = c.getDeclaredMethod(name, parameters);
            m.setAccessible(true);
            return m;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("ReplayMoveProbe: " + c.getSimpleName() + "." + name + " (a method) is not there any more; the probe needs updating", e);
        }
    }

    private static Object get(Field f, Object on) {
        try {
            return f.get(on);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("ReplayMoveProbe: " + f.getDeclaringClass().getSimpleName() + "." + f.getName() + " could not be read", e);
        }
    }

    private static Object call(Method m, Object on, Object... arguments) {
        try {
            return m.invoke(on, arguments);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("ReplayMoveProbe: " + m.getDeclaringClass().getSimpleName() + "." + m.getName() + " threw " + e.getCause(), e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("ReplayMoveProbe: " + m.getDeclaringClass().getSimpleName() + "." + m.getName() + " could not be called", e);
        }
    }
}
