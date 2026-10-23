package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.GamePrefs;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.TempDirs;
import javafx.application.Platform;
import javafx.stage.Stage;
import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedStackTrace;
import jdk.jfr.consumer.RecordedThread;
import jdk.jfr.consumer.RecordingFile;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * What the window costs the JavaFX thread as a city ages, measured: it grows
 * the playtest's city to the months it is given, opens the real window on it,
 * and times every rail screen, the real clock at 50x and the save. A main()
 * run by hand on the PC; it is not in AllChecks, because it opens a window for
 * a minute or so and the cloud has no display.
 *
 * WHY IT EXISTS (UP1, 2026-10-22; docs/queue/UP1.md)
 *
 * Jerus: the game gets sluggish after long play. The plan that looked into it
 * wrote three scratch sources that were never in the tree - UiCostProbe,
 * UiGrowProbe and JfrCallers (docs/queue/UP1/probe/) - and found the autosave
 * on the FX thread the largest cost, 31% of it at month 4,000 at 50x, growing
 * with every month played. UP1 took the write off the clock; UP2 and UP3 go on
 * from what this prints, so the probe is kept where a later batch finds it and
 * measures against it, instead of being rewritten from a note each time.
 *
 * HOW IT IS RUN, from the clone's root on the PC (Git Bash):
 *
 *     java -XX:FlightRecorderOptions:stackdepth=512 -Dprobe.out=DIR
 *         -cp "target/classes;$(cat target/classpath.txt)"
 *         ham.citybuildersim.ui.UiProbe 1000,2000,3000,4000 3
 *
 * The arguments are the months to grow the city to, comma separated (it stops
 * at each, measures, and grows on), and the seconds the clock runs on each
 * screen (3 if left out). The classpath is the build's (target/classpath.txt,
 * which batch-check.sh writes from the pom, holds gson and the JavaFX base,
 * graphics and controls jars with their -win natives; on another platform the
 * pom's own jars are the ones to name); stackdepth=512 is what lets a sample's
 * whole stack reach down to the callers a share is counted from (a truncated
 * stack is counted and printed). Options:
 *
 *   -Dprobe.out=DIR     where the JFR recordings (ui-MONTH.jfr) are kept; unset,
 *                       a TempDirs folder, which is deleted when the JVM ends
 *                       (the repository is never written)
 *   -Dprobe.earth=SPOT  grow the city on the real Earth round a spot, as
 *                       LongPlaytest --earth does; needs -Dcbs.earth=PATH-TO-earth-1.bin
 *
 * and, without a city or a window, the callers of a method in a recording
 * (JfrCallers): {@code UiProbe --callers file.jfr ham.citybuildersim.Game.nextMonth [rows]}
 * prints the ham.* frames above each FX-thread sample that has the method in
 * its stack, most common first.
 *
 * WHAT IT PRINTS, for each month it stops at (the shapes of
 * docs/queue/UP1/default-m1000-4000.txt, so the two read side by side):
 *
 *   - the city (people, buildings, history months, heap);
 *   - the save as UP1 made it: what g.autosave() costs the FX thread (the fill
 *     and the snapshot - what the clock pays), how long the writer then takes
 *     to have the files whole (g.awaitSaves(), on the save-writer thread), the
 *     three files' sizes, and what a numbered save (g.saveGame(3), written on
 *     the FX thread, as the player's Save still does) costs; then the load;
 *   - 24 months as the clock runs them (toggleNextMonth): median and max;
 *   - each rail screen's first build, and its redraw as the clock makes it:
 *     the draw, then the pulse's own CSS and layout passes (Scene.doCSSPass
 *     and doLayoutPass, which touch only what is dirty - UP2: until then this
 *     forced applyCss() over every node, which no pulse does, and could not
 *     see what a kept page saves; the study's section 9); and the chrome alone
 *     (the header, rail, city panel, construction panel and inbox drawn again
 *     after a month, then the same passes);
 *   - the real clock at 50x on each screen: months a second, frame p50, p95
 *     and max, while JFR samples the FX thread; at month 4,000 or later the
 *     p95 is held to its screen's figure in FRAME_P95 (People's FRAME_P95_MS)
 *     through Timing, and the verdict printed on its line;
 *   - the FX thread's samples, by inclusive share (the clock, Game.nextMonth,
 *     Game.autosave, the chrome, the history series, the charts, the map, the
 *     pulse), the threads sampled (the save-writer thread appears once the
 *     writer has written) and the frames of our own code where the thread
 *     spent its time.
 *
 * THE FLOOR. Since UP1 an autosave comes at most every SaveWriter.FLOOR_SECONDS
 * of real time. The probe's own g.autosave() resets that floor, so the 24
 * clock months that follow it never write one, and the live phase opens with
 * one autosave of its own (a real minute at 50x holds one or two). The
 * printed lines say so; that is the game's behaviour, not the probe's.
 *
 * NOTHING OF JERUS'S IS TOUCHED. The city is on a GameFiles.scratch() folder,
 * and every file the window writes - GamePrefs' preferences, the saves, their
 * maps and histories - goes through game.getGameFiles(), so the real
 * %APPDATA% folder is never reached (the probe checks that its folder lies
 * under java.io.tmpdir and refuses to go on if it does not). The scratch
 * folders are deleted when the JVM exits; numbered save 3 here is that
 * folder's, not a player's.
 *
 * Where JavaFX cannot start - no display, a headless JVM, the jars missing -
 * it says so in one line and exits 0.
 */
public final class UiProbe {

    private UiProbe() { }

    /**
     * Where the probe's own lines go: the console, as it was when the class
     * loaded. The game narrates every month it plays (a screenful a month, nine
     * thousand lines in a short run), so once the probe starts, System.out goes
     * nowhere and only these lines are printed.
     */
    private static final PrintStream report = System.out;

    private static UserInterface ui;
    private static Game g;
    private static Stage stage;
    private static Path out;

    /**
     * The 95th-percentile frame on People at 50x at month 4,000 the clock must keep under, ms
     * (UP2: 49 before it, with the redraw paced, the months budgeted and the chrome kept).
     */
    public static final double FRAME_P95_MS = 34;

    /**
     * Every rail screen's 95th-percentile frame at 50x at month 4,000, ms: its figure before UP2
     * (People's is FRAME_P95_MS), which the clock must not exceed since.
     */
    public static final Map<String, Double> FRAME_P95 = Map.ofEntries(
            Map.entry("Build", 70.0), Map.entry("Land", 70.0), Map.entry("People", FRAME_P95_MS),
            Map.entry("Services", 70.0), Map.entry("Infra", 70.0), Map.entry("Sectors", 70.0),
            Map.entry("Govt", 70.0), Map.entry("Finances", 70.0), Map.entry("Bank", 70.0),
            Map.entry("Trade", 70.0), Map.entry("Policy", 70.0), Map.entry("History", 70.0));

    /** The month from which FRAME_P95 is asserted: the city it was measured on. */
    static final int FRAME_P95_FROM = 4000;

    private static Method focusCleanup, doCSSPass, doLayoutPass;

    /**
     * The pulse's CSS and layout, as a frame runs them (Scene.focusCleanup and doCSSPass, then
     * doLayoutPass; by reflection, since the toolkit is on the class path): {CSS ms, layout ms}.
     */
    static double[] pulse(javafx.scene.Scene sc) throws Exception {
        if (doCSSPass == null) {
            focusCleanup = javafx.scene.Scene.class.getDeclaredMethod("focusCleanup");
            doCSSPass = javafx.scene.Scene.class.getDeclaredMethod("doCSSPass");
            doLayoutPass = javafx.scene.Scene.class.getDeclaredMethod("doLayoutPass");
            focusCleanup.setAccessible(true);
            doCSSPass.setAccessible(true);
            doLayoutPass.setAccessible(true);
        }
        long a = System.nanoTime();
        focusCleanup.invoke(sc);
        doCSSPass.invoke(sc);
        long b = System.nanoTime();
        doLayoutPass.invoke(sc);
        long c = System.nanoTime();
        return new double[]{ms(b - a), ms(c - b)};
    }

    /** The chrome drawn again, as every redraw draws it: the header, the rail, the city panel, the construction panel and the inbox. */
    private static void chrome() throws Exception {
        call("refreshHeader");
        call("refreshTabRail");
        ui.summaryScreen.refreshCityPanel();
        call("refreshConstructionPanel");
        call("refreshInbox");
    }

    /** The window's pump: runs on the FX thread and waits for the answer. */
    private static <T> T fx(Callable<T> c) throws Exception {
        FutureTask<T> t = new FutureTask<>(c);
        Platform.runLater(t);
        return t.get();
    }

    private record Screen(String name, Runnable show) { }

    /** The twelve rail screens, in rail order. */
    private static List<Screen> screens() {
        return List.of(
                new Screen("Build", ui.buildScreen::showBuildMenu),
                new Screen("Land", ui.landScreen::showLandMenu),
                new Screen("People", ui.peopleScreen::showPopulationInfoMenu),
                new Screen("Services", ui.servicesScreen::showServicesStatsMenu),
                new Screen("Infra", ui.infrastructureScreen::showInfrastructureMenu),
                new Screen("Sectors", ui.sectorScreen::showSectorMenu),
                new Screen("Govt", ui.governmentScreen::showGovernmentMenu),
                new Screen("Finances", ui.financesScreen::showFinanceMenu),
                new Screen("Bank", ui.bankScreen::showBankMenu),
                new Screen("Trade", ui.tradeScreen::showForeignMenu),
                new Screen("Policy", ui.policyScreen::showPolicyMenu),
                new Screen("History", ui.historyScreen::showHistoryMenu));
    }

    private static Field field(String name) throws Exception {
        Field x = UserInterface.class.getDeclaredField(name);
        x.setAccessible(true);
        return x;
    }

    private static void call(String name) throws Exception {
        Method m = UserInterface.class.getDeclaredMethod(name);
        m.setAccessible(true);
        m.invoke(ui);
    }

    /** Waits, on the FX thread as the game does, for the save writer to have every file whole. */
    private static void settle() throws Exception {
        fx(() -> { g.awaitSaves(); return null; });
    }

    private static double ms(long nanos) { return nanos / 1e6; }

    private static double median(List<Double> v) {
        if (v.isEmpty()) return Double.NaN;
        List<Double> s = new ArrayList<>(v);
        Collections.sort(s);
        return s.get(s.size() / 2);
    }

    private static double pct(List<Double> v, double p) {
        if (v.isEmpty()) return Double.NaN;
        List<Double> s = new ArrayList<>(v);
        Collections.sort(s);
        return s.get(Math.min(s.size() - 1, (int) Math.floor(p * s.size())));
    }

    private static double megabytes(Path p) {
        try { return Files.size(p) / 1e6; } catch (Exception e) { return -1; }
    }

    private static long modified(Path p) {
        try { return Files.getLastModifiedTime(p).toMillis(); } catch (Exception e) { return -1; }
    }

    /** Why JavaFX cannot start here, in a few words, or null when it can: the toolkit is started and the answer is whether it came up. */
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

    public static void main(String[] args) throws Exception {

        if (args.length > 0 && args[0].equals("--callers")) {
            callers(args);
            return;
        }
        if (args.length == 0) {
            report.println("UiProbe: months[,months...] [seconds per screen, default 3]"
                    + " | --callers file.jfr method-prefix [rows]; see the class comment.");
            return;
        }

        String cannot = startJavaFx();
        if (cannot != null) {
            report.println("UiProbe: JavaFX cannot start here (" + cannot + "); nothing was measured.");
            System.exit(0);
        }

        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            run(args);
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static void run(String[] args) throws Exception {

        int[] checkpoints = Arrays.stream(args[0].split(",")).mapToInt(Integer::parseInt).toArray();
        double liveSeconds = args.length > 1 ? Double.parseDouble(args[1]) : 3;
        String spot = System.getProperty("probe.earth");
        String outProperty = System.getProperty("probe.out");
        out = outProperty == null ? TempDirs.create("uiprobe") : Path.of(outProperty);
        Files.createDirectories(out);

        GameFiles files = GameFiles.scratch("uiprobe");
        Path tmp = Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
        if (!files.getDirectory().toAbsolutePath().normalize().startsWith(tmp)) {
            report.println("UiProbe: refusing to run, its save folder " + files.getDirectory()
                    + " is not under " + tmp + ".");
            return;
        }
        // The window reads its preferences from this folder, never from %APPDATA% (GamePrefs.fileIn).
        GamePrefs prefs = GamePrefs.load(files);
        prefs.setFullScreen(false);
        prefs.save(files);

        long t0 = System.nanoTime();
        g = fx(() -> LongPlaytest.probeCity(files, spot));
        report.printf("founded in %.1f s%s%n", (System.nanoTime() - t0) / 1e9, spot == null ? "" : " (Earth: " + spot + ")");

        fx(() -> {
            ui = new UserInterface(g);
            stage = new Stage();
            ui.start(stage);
            stage.setFullScreen(false);
            stage.setMaximized(false);
            stage.setWidth(1600);
            stage.setHeight(900);
            call("anotherCity");
            call("openCity");
            return null;
        });
        Field speedIndex = field("speedIndex"), clockRunning = field("clockRunning"), pausedBecause = field("pausedBecause");
        Field sceneField = field("scene");

        int lastGrowth = g.getMonth();
        for (int cp : checkpoints) {
            long g0 = System.nanoTime();
            fx(() -> { LongPlaytest.probePlay(g, cp); return null; });
            double growS = (System.nanoTime() - g0) / 1e9;
            int at = g.getMonth();
            report.printf("%n=== month %d (grown in %.1f s; model month mean %.1f ms over the last stretch) ===%n",
                    at, growS, LongPlaytest.probeMonthMs(lastGrowth, at));
            lastGrowth = at;
            report.printf("people %,d, buildings %,d, history months %d, districts %d, decisions %d, heap used %d MB%n",
                    g.getPopulationManager().getPopulation(), buildings(),
                    g.getHistorySave().months(), g.hasCityMap() ? g.getCityMap().districts().size() : -1,
                    g.getDecisions().size(),
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20);

            saveAndLoad(files);

            // ---- the month as the clock runs it (toggleNextMonth), 24 of them
            Path autosaveFile = files.saveFile(GameFiles.AUTOSAVE_SLOT);
            settle();
            long stampBefore = modified(autosaveFile);
            List<Double> monthMs = fx(() -> {
                List<Double> v = new ArrayList<>();
                for (int i = 0; i < 24; i++) {
                    long a = System.nanoTime();
                    g.toggleNextMonth();
                    v.add(ms(System.nanoTime() - a));
                }
                return v;
            });
            settle();
            boolean wrote = modified(autosaveFile) != stampBefore;
            report.printf("clock month: median %.1f ms, max %.0f ms (%s)%n", median(monthMs), Collections.max(monthMs),
                    wrote ? "an autosave's month is in it: the fill and the snapshot, the write is off the clock"
                            : "no autosave in it: the " + (int) ham.citybuildersim.SaveWriter.FLOOR_SECONDS
                                    + " s floor holds the due month back, the probe's own autosave was seconds ago");

            // ---- each screen: first build, then redraws as the clock makes them
            report.println("screen     first |  draw    css  layout  redraw | chrome alone: draw   css  layout   all"
                    + "   (median of 8, ms; the pulse's own passes)");
            for (Screen s : screens()) {
                try {
                    javafx.scene.Scene sc = fx(() -> (javafx.scene.Scene) sceneField.get(ui));
                    double first = fx(() -> {
                        long a = System.nanoTime();
                        s.show().run();
                        pulse(sc);
                        return ms(System.nanoTime() - a);
                    });
                    List<Double> draw = new ArrayList<>(), css = new ArrayList<>(), lay = new ArrayList<>(),
                            whole = new ArrayList<>(), cDraw = new ArrayList<>(), cCss = new ArrayList<>(),
                            cLay = new ArrayList<>(), cWhole = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        // Each its own task, so the settle a rebuilt page posts runs between them, as on the clock.
                        fx(() -> {
                            g.toggleNextMonth();
                            pulse(sc);
                            long b0 = System.nanoTime();
                            ui.redraw();
                            long b1 = System.nanoTime();
                            double[] p = pulse(sc);
                            draw.add(ms(b1 - b0));
                            css.add(p[0]);
                            lay.add(p[1]);
                            whole.add(ms(b1 - b0) + p[0] + p[1]);
                            return null;
                        });
                        fx(() -> {
                            g.toggleNextMonth();
                            pulse(sc);
                            long c0 = System.nanoTime();
                            chrome();
                            long c1 = System.nanoTime();
                            double[] p = pulse(sc);
                            cDraw.add(ms(c1 - c0));
                            cCss.add(p[0]);
                            cLay.add(p[1]);
                            cWhole.add(ms(c1 - c0) + p[0] + p[1]);
                            return null;
                        });
                    }
                    report.printf("%-9s %6.0f | %5.1f %6.1f %6.1f %7.1f |              %5.1f %5.1f %6.1f %5.1f%n",
                            s.name(), first, median(draw), median(css), median(lay), median(whole),
                            median(cDraw), median(cCss), median(cLay), median(cWhole));
                } catch (Exception e) {
                    report.println(s.name() + " FAILED " + e);
                }
            }

            // ---- live: the real clock at 50x, each screen for a few seconds, the FX thread profiled
            Recording rec = new Recording(Configuration.getConfiguration("profile"));
            rec.start();
            // A real minute at 50x holds one or two autosaves under the floor; this one opens the window's.
            fx(() -> { g.autosave("probe live"); return null; });
            report.println("live 50x   months/s  frame p50  p95   max (ms)   (opens with one autosave, the floor then holds the next 30 s)");
            for (Screen s : screens()) {
                fx(() -> { s.show().run(); return null; });
                FrameTimer probe = new FrameTimer(clockRunning, pausedBecause, speedIndex);
                List<Double> frames = probe.frames;
                int m0 = fx(() -> { probe.start(); return g.getMonth(); });
                long w0 = System.nanoTime();
                Thread.sleep((long) (liveSeconds * 1000));
                int m1 = fx(() -> { probe.stop(); clockRunning.set(ui, false); return g.getMonth(); });
                double secs = (System.nanoTime() - w0) / 1e9;
                double p95 = pct(frames, .95);
                Double budget = FRAME_P95.get(s.name());
                String verdict = at < FRAME_P95_FROM || budget == null ? ""
                        : String.format("   p95 within %.0f: %s", budget,
                                ham.citybuildersim.Timing.verdict(ham.citybuildersim.Timing.within(p95, budget)));
                report.printf("%-9s %9.1f %10.0f %5.0f %6.0f%s%n", s.name(), (m1 - m0) / secs,
                        median(frames), p95, frames.isEmpty() ? Double.NaN : Collections.max(frames), verdict);
            }
            settle();
            rec.stop();
            Path jfr = out.resolve("ui-" + at + ".jfr");
            rec.dump(jfr);
            rec.close();
            report.println("JFR " + jfr);
            analyse(jfr);
        }
        report.println("\nmodel month mean by stretch (ms):");
        for (int a = 0; a < g.getMonth(); a += 500) {
            int to = Math.min(a + 500, g.getMonth());
            double mean = LongPlaytest.probeMonthMs(a, to);   // NaN where the months were stepped by the probe, not played
            if (!Double.isNaN(mean)) report.printf("  %d-%d: %.1f%n", a, to, mean);
        }
        settle();
        Platform.exit();
    }

    /**
     * One frame's length at a time, and the clock kept running at 50x: the
     * window's own AnimationTimer would stop for a menu or a dialog, so the
     * probe sets its three fields on every frame. A named class, not an
     * anonymous one, so that UiProbe itself loads without JavaFX on the
     * classpath and can say so in a line.
     */
    private static final class FrameTimer extends javafx.animation.AnimationTimer {

        final List<Double> frames = Collections.synchronizedList(new ArrayList<>());
        private final Field clockRunning, pausedBecause, speedIndex;
        private long last;

        FrameTimer(Field clockRunning, Field pausedBecause, Field speedIndex) {
            this.clockRunning = clockRunning;
            this.pausedBecause = pausedBecause;
            this.speedIndex = speedIndex;
        }

        @Override public void handle(long now) {
            if (last != 0) frames.add((now - last) / 1e6);
            last = now;
            try {
                clockRunning.set(ui, true);
                pausedBecause.set(ui, null);
                speedIndex.set(ui, 8);   // SPEEDS[8], 50x
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The save as UP1 made it, then the load. g.autosave() on the FX thread is
     * what the clock pays (the fill and the snapshot); g.awaitSaves() after it
     * is the writer's thread finishing the three files; g.saveGame(3) is a
     * numbered save, filled and written on the FX thread as the player's Save
     * button still does. Each is timed twice and the second reported, the
     * first being the JIT's.
     */
    private static void saveAndLoad(GameFiles files) throws Exception {
        double autosaveMs = 0, writeMs = 0, numberedMs = 0;
        for (int k = 0; k < 2; k++) {
            double[] r = fx(() -> {
                long a = System.nanoTime();
                g.autosave("probe");
                long b = System.nanoTime();
                g.awaitSaves();
                long c = System.nanoTime();
                return new double[]{ms(b - a), ms(c - b)};
            });
            autosaveMs = r[0];
            writeMs = r[1];
        }
        for (int k = 0; k < 2; k++) {
            long a = System.nanoTime();
            fx(() -> g.saveGame(3, "probe"));
            numberedMs = ms(System.nanoTime() - a);
        }
        report.printf("autosave on the clock %.0f ms (fill and snapshot), written by the writer in %.0f ms more;"
                        + " save %.1f MB, history %.1f MB, map %.1f MB%n",
                autosaveMs, writeMs, megabytes(files.saveFile(GameFiles.AUTOSAVE_SLOT)),
                megabytes(files.historyFile(GameFiles.AUTOSAVE_SLOT)), megabytes(files.mapFile(GameFiles.AUTOSAVE_SLOT)));
        report.printf("numbered save %.0f ms on the FX thread (slot 3 of the scratch folder; the Save button)%s%n", numberedMs,
                g.getLastSaveResult().ok ? "" : " - FAILED: " + g.getLastSaveResult().error);

        long l0 = System.nanoTime();
        Game back = new Game(files);
        back.loadGameSave(GameFiles.AUTOSAVE_SLOT);
        report.printf("load %.0f ms (month %d)%n", ms(System.nanoTime() - l0), back.getMonth());
    }

    private static int buildings() {
        long n = 0;
        for (long c : g.getMapCounts()) n += c;
        return (int) n;
    }

    /* ===================== reading the recording ===================== */

    /**
     * The prefixes of "type.method" an FX-thread sample is counted under, if
     * any frame of its stack starts with one: inclusive shares, so a sample
     * counts once for each key it is under.
     */
    private static final String[] KEYS = {
            "ham.citybuildersim.ui.UserInterface$",            // the clock and other timers (anonymous)
            "ham.citybuildersim.Game.nextMonth",
            "ham.citybuildersim.Game.autosave",                 // the autosave as the clock pays it (UP1)
            "ham.citybuildersim.Game.prepareSave",              // ...its fill, in a numbered save too
            "ham.citybuildersim.HistorySave.snapshot",          // ...and the history's copy
            "ham.citybuildersim.Game.reconcileMap",
            "ham.citybuildersim.ui.UserInterface.clearMenu",
            "ham.citybuildersim.ui.UserInterface.refreshHeader",
            "ham.citybuildersim.ui.UserInterface.refreshConstructionPanel",
            "ham.citybuildersim.ui.UserInterface.refreshInbox",
            "ham.citybuildersim.ui.UserInterface.refreshTabRail",
            "ham.citybuildersim.ui.SummaryScreen.refreshCityPanel",
            "ham.citybuildersim.HistorySave.aligned",
            "ham.citybuildersim.HistorySave.raw",
            "ham.citybuildersim.HistorySave.seriesByName",
            "ham.citybuildersim.YearBook.",
            "ham.citybuildersim.ChartModel.",
            "ham.citybuildersim.ui.TimeChart.setData",
            "ham.citybuildersim.ui.TimeChart.draw",
            "ham.citybuildersim.ui.TimeChart.",
            "ham.citybuildersim.ui.MiniMap.tick",
            "ham.citybuildersim.ui.MapView.tick",
            "ham.citybuildersim.MapTiles.",
            "ham.citybuildersim.CityMap.",
            "ham.citybuildersim.MiniMapFrame.",
            "javafx.scene.Scene$ScenePulseListener.pulse",      // the pulse
            "javafx.scene.Parent.layout",
            "javafx.scene.Node.processCSS",
            "javafx.scene.Node.applyCss",
            "javafx.scene.Parent$",                              // children-list changes
            "javafx.scene.text.",
            "com.sun.javafx.text.",
            "javafx.scene.canvas.",
    };

    private static final String FX_THREAD = "JavaFX Application Thread";

    private static void analyse(Path jfr) throws Exception {
        Map<String, Integer> incl = new LinkedHashMap<>();
        for (String k : KEYS) incl.put(k, 0);
        Map<String, Integer> show = new TreeMap<>();
        Map<String, Integer> topHam = new HashMap<>();
        Map<String, Integer> threads = new HashMap<>();
        int fxSamples = 0, truncated = 0;
        double gcMs = 0;
        int gcN = 0;
        try (RecordingFile rf = new RecordingFile(jfr)) {
            while (rf.hasMoreEvents()) {
                RecordedEvent e = rf.readEvent();
                String type = e.getEventType().getName();
                if (type.equals("jdk.GarbageCollection")) { gcMs += e.getDuration().toNanos() / 1e6; gcN++; continue; }
                if (!type.equals("jdk.ExecutionSample")) continue;
                RecordedThread t = e.getThread("sampledThread");
                String tn = t == null ? "?" : t.getJavaName();
                threads.merge(tn, 1, Integer::sum);
                if (!FX_THREAD.equals(tn)) continue;
                fxSamples++;
                RecordedStackTrace st = e.getStackTrace();
                if (st == null) continue;
                if (st.isTruncated()) truncated++;
                Set<String> seen = new HashSet<>();
                String firstHam = null;
                for (RecordedFrame fr : st.getFrames()) {
                    String name = fr.getMethod().getType().getName() + "." + fr.getMethod().getName();
                    if (firstHam == null && name.startsWith("ham.")) firstHam = name;
                    for (String k : KEYS) if (name.startsWith(k) && seen.add(k)) incl.merge(k, 1, Integer::sum);
                    if (name.matches("ham\\.citybuildersim\\.ui\\.\\w+\\.show\\w+") && seen.add(name)) show.merge(name, 1, Integer::sum);
                }
                if (firstHam != null) topHam.merge(firstHam, 1, Integer::sum);
            }
        }
        report.printf("JFR: FX-thread samples %d (truncated %d); GC %d pauses, %.0f ms total%n", fxSamples, truncated, gcN, gcMs);
        report.print("threads:");
        threads.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(6)
                .forEach(x -> report.printf(" %s=%d", x.getKey(), x.getValue()));
        report.println();
        report.printf("save-writer samples: %d%n", threads.getOrDefault("save-writer", 0));
        final int all = Math.max(1, fxSamples);
        report.println("inclusive share of FX-thread samples:");
        // Every key, a zero too: "Game.autosave 0.0%" is a figure the next batch reads.
        incl.forEach((k, v) -> report.printf("  %5.1f%%  %s%n", 100.0 * v / all, k));
        show.forEach((k, v) -> report.printf("  %5.1f%%  %s%n", 100.0 * v / all, k));
        report.println("top ham frames (self within our code):");
        topHam.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(14)
                .forEach(x -> report.printf("  %5.1f%%  %s%n", 100.0 * x.getValue() / all, x.getKey()));
    }

    /**
     * {@code --callers file.jfr method-prefix [rows]}: for the FX-thread
     * samples with a method starting with the prefix in their stack, the ham.*
     * frames from there up (six at most), most common path first. This was
     * UP1's JfrCallers.
     */
    private static void callers(String[] a) throws Exception {
        if (a.length < 3) {
            report.println("UiProbe --callers file.jfr method-prefix [rows]");
            return;
        }
        String target = a[2];
        Map<String, Integer> paths = new HashMap<>();
        int hits = 0, fxSamples = 0;
        try (RecordingFile rf = new RecordingFile(Path.of(a[1]))) {
            while (rf.hasMoreEvents()) {
                RecordedEvent e = rf.readEvent();
                if (!e.getEventType().getName().equals("jdk.ExecutionSample")) continue;
                RecordedThread t = e.getThread("sampledThread");
                if (t == null || !FX_THREAD.equals(t.getJavaName())) continue;
                fxSamples++;
                List<RecordedFrame> fr = e.getStackTrace().getFrames();
                for (int i = 0; i < fr.size(); i++) {
                    String n = fr.get(i).getMethod().getType().getName() + "." + fr.get(i).getMethod().getName();
                    if (!n.startsWith(target)) continue;
                    hits++;
                    StringBuilder sb = new StringBuilder();
                    int k = 0;
                    for (int j = i; j < fr.size() && k < 6; j++) {
                        String m = fr.get(j).getMethod().getType().getName() + "." + fr.get(j).getMethod().getName();
                        if (!m.startsWith("ham.")) continue;
                        sb.append(k == 0 ? "" : " <- ").append(m.replace("ham.citybuildersim.", ""));
                        k++;
                    }
                    paths.merge(sb.toString(), 1, Integer::sum);
                    break;
                }
            }
        }
        report.printf("%s: %d of %d FX samples%n", target, hits, fxSamples);
        final int all = Math.max(1, fxSamples);
        paths.entrySet().stream().sorted((x, y) -> y.getValue() - x.getValue()).limit(a.length > 3 ? Integer.parseInt(a[3]) : 6)
                .forEach(x -> report.printf("  %4.1f%%  %s%n", 100.0 * x.getValue() / all, x.getKey()));
    }
}
