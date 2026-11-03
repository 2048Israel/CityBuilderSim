package ham.citybuildersim.ui;

import ham.citybuildersim.CameraPath;
import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.GamePrefs;
import ham.citybuildersim.GlobeFrame;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.Recording;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
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
 * it, how long the move took (a glide: and how long its warm-up took, and whether that reached ReplayCamera.WARM_MOST_S,
 * the cap), and MoveTally.report().
 *
 * NOTHING OF JERUS'S IS TOUCHED. The cities are on GameFiles.scratch() folders, checked to lie under java.io.tmpdir and
 * deleted when the JVM exits; the replay's copy is on its own scratch folder (ReplayCity); the window's preferences are
 * read from the scratch folder, never from %APPDATA%; the only file written outside is the recording folder it is given.
 *
 * THE WINDOW IS OFF EVERY SCREEN unless -Dcbs.probe.onscreen=true: the primary screen's size, undecorated, left of the
 * leftmost screen, so a run does not cover the PC's screen while someone works on it (CM1, 2026-11-03). It still draws
 * every frame; it does take the keyboard's focus when it opens, until another window is clicked.
 *
 * Where JavaFX cannot start - no display, a headless JVM, the jars missing - it says so in one line and exits 0.
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
    private static double cityLat, cityLon;

    /** The recorded months' ends: what goTo() is given. */
    private static int firstMonth, lastMonth;

    /** The private members the probe reaches, found by bind() once the toolkit is up (a build that moved one fails there, naming it). */
    private static Field panKeys, canvasField;
    private static Method replayMode, enter, applyView, requestDraw, replaySettled;

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
        replaySettled = method(MapView.class, "replaySettled");
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
            return null;
        });
        CameraPath.View whole = waitForWhole();
        cityLat = whole.a();
        cityLon = whole.b();
        waitForCanvas();

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
        return new CameraPath.View(true, cityLat, cityLon, kmPx, 0);
    }

    /** The widest view as it stands now (its scale follows the canvas), the whole Earth facing the city. */
    private static CameraPath.View wholeNow() throws Exception {
        CameraPath.View w = fx(() -> ui.expandedMapWhole());
        if (w == null || !w.globe()) throw new IllegalStateException("the expanded map has no globe: the city must be on the Earth");
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
        report.printf("window: full screen %b, the map's canvas %s px%n", fx(() -> stage.isFullScreen()), size);
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

    /** Waits, at most SETTLE_MAX_S, until MapView.replaySettled() has held over two looks 100 ms apart, and prints how long it took. */
    private static void settle(String what) throws Exception {
        long t0 = System.nanoTime();
        int held = 0;
        while (true) {
            Thread.sleep(100);
            double s = (System.nanoTime() - t0) / 1e9;
            boolean idle = fx(() -> (Boolean) call(replaySettled, map));
            held = idle ? held + 1 : 0;
            if (held >= 2) {
                report.printf("  %s settled in %.1f s%n", what, s);
                return;
            }
            if (s >= SETTLE_MAX_S) {
                report.printf("  %s NOT settled after %.0f s; going on%n", what, s);
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

    /**
     * Runs a move: the tally reset in the same FX task that starts it, then polled until the camera is idle (at most MOVE_MAX_S),
     * and printed under its heading. For a glide also how long the warm-up took (until the phase left WARM) and whether that
     * was the cap, ReplayCamera.WARM_MOST_S.
     */
    private static void move(String heading, Runnable start, boolean glide) throws Exception {
        MapView.MoveTally tally = map.moveTally();
        long t0 = System.nanoTime();
        fx(() -> {
            tally.reset();
            start.run();
            return null;
        });
        String phase = fx(() -> cam.phaseName());
        double warmS = glide && !phase.equals("WARM") ? -1 : 0;
        boolean ran = !phase.equals("IDLE");
        boolean timedOut = false;
        while (!phase.equals("IDLE")) {
            Thread.sleep(25);
            double s = (System.nanoTime() - t0) / 1e9;
            phase = fx(() -> cam.phaseName());
            if (glide && warmS == 0 && !phase.equals("WARM")) warmS = s;
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
        report.printf("%n=== %s ===%n", heading);
        if (!ran) report.println("  the camera did not start (it was idle at once: no view marked, or nothing to move)");
        if (timedOut) report.printf("  TIMED OUT: still moving after %.0f s, stopped%n", MOVE_MAX_S);
        report.printf("  took %.1f s%n", took);
        if (glide && warmS > 0) {
            report.printf("  warm-up %.1f s, %s the cap (WARM_MOST_S %.0f s)%n", warmS, warmS >= ReplayCamera.WARM_MOST_S ? "reached" : "short of",
                    ReplayCamera.WARM_MOST_S);
        }
        report.print(fx(() -> tally.report()));
    }

    /* ------------------------------------------ the toolkit and reflection ------------------------------------------ */

    /** Why JavaFX cannot start here, in a few words, or null when it can: the toolkit is started and the answer is whether it came up (UiProbe's). */
    private static String startJavaFx() {
        try {
            if (java.awt.GraphicsEnvironment.isHeadless()) return "this JVM is headless (no display)";
            Platform.setImplicitExit(false);
            CountDownLatch up = new CountDownLatch(1);
            Platform.startup(up::countDown);
            if (!up.await(60, TimeUnit.SECONDS)) return "the toolkit did not come up in 60 s";
            return null;
        } catch (Throwable t) {
            String why = t.getMessage() == null ? "" : ": " + t.getMessage().replaceAll("\\s+", " ").trim();
            return t.getClass().getSimpleName() + why;
        }
    }

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
