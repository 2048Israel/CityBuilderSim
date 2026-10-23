package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.GamePrefs;
import ham.citybuildersim.UiGrowProbe;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedStackTrace;
import jdk.jfr.consumer.RecordingFile;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * Scratch probe for the study UP4 (docs/studies/screens-in-place.md), never in src/: what each rail
 * screen's monthly redraw costs, what of its page changes from one month to the next, and what the
 * same redraw would cost if the page were kept and only what changed were set on it.
 *
 * WHY. UP1's probe (docs/queue/UP1/probe/) timed the redraws with root.applyCss() and layout(); but
 * applyCss() forces every node under the node it is called on through a CSS update, changed or not,
 * which no pulse does, so its "CSS and layout" was part artefact. This one times the pulse's own
 * passes (Scene.focusCleanup, doCSSPass, doLayoutPass, by reflection: the toolkit is on the class
 * path here, so nothing is sealed), which touch only what is dirty, as the game's frame does. And
 * nothing on today's page is kept, so to say what keeping it would save it builds each month's page
 * off the scene (rootMenu swapped for a detached box for the length of the build), walks it beside
 * the page on screen, and sets on the kept page only what differs (TreePatch). The passes after that
 * are what a kept page would pay, near enough; the build is still paid in full.
 *
 * Modes, per screen: today (the redraw as the clock makes it, and the forced CSS pass clearMenu's
 * settle makes after it), chrome (the header, rail, panels and inbox alone), kept (the patch),
 * census (the page's nodes by kind) and, with -Dprobe.split=1, a JFR split of the build into node
 * making, the screen's own code and the model's reads; with -Dprobe.live=S, the real clock at 50x
 * for S seconds a screen, the FX thread's time by what it was doing.
 */
public final class InPlaceProbe {

    static UserInterface ui;
    static Game g;
    static Stage stage;
    static Scene sc;
    static final Path OUT = Path.of(System.getProperty("probe.out", "."));
    static PrintStream out = System.out;

    static <T> T fx(Callable<T> c) throws Exception {
        FutureTask<T> t = new FutureTask<>(c);
        Platform.runLater(t);
        return t.get(30, TimeUnit.MINUTES);
    }

    record Screen(String name, Runnable show) { }

    static List<Screen> screens() {
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

    static Field f(String name) throws Exception {
        Field x = UserInterface.class.getDeclaredField(name);
        x.setAccessible(true);
        return x;
    }

    static void call(String name) throws Exception {
        Method m = UserInterface.class.getDeclaredMethod(name);
        m.setAccessible(true);
        m.invoke(ui);
    }

    static double ms(long nanos) { return nanos / 1e6; }

    static double median(List<Double> v) {
        if (v.isEmpty()) return Double.NaN;
        List<Double> s = new ArrayList<>(v);
        Collections.sort(s);
        return s.get(s.size() / 2);
    }

    static double pct(List<Double> v, double p) {
        if (v.isEmpty()) return Double.NaN;
        List<Double> s = new ArrayList<>(v);
        Collections.sort(s);
        return s.get(Math.min(s.size() - 1, (int) Math.floor(p * s.size())));
    }

    private static Method focusCleanup, doCSSPass, doLayoutPass;

    /** The pulse's CSS and layout, as the frame runs them: {CSS ms (with the focus clean-up), layout ms}. */
    static double[] pulse() throws Exception {
        if (doCSSPass == null) {
            focusCleanup = Scene.class.getDeclaredMethod("focusCleanup");
            doCSSPass = Scene.class.getDeclaredMethod("doCSSPass");
            doLayoutPass = Scene.class.getDeclaredMethod("doLayoutPass");
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

    private static Object scenePulseListener;
    private static Method scenePulse;

    /** The scene's whole pulse as the frame runs it - the focus clean-up, CSS, layout and the sync to the renderer's nodes: ms. */
    static double scenePulse() throws Exception {
        if (scenePulse == null) {
            Field f = Scene.class.getDeclaredField("scenePulseListener");
            f.setAccessible(true);
            scenePulseListener = f.get(sc);
            scenePulse = scenePulseListener.getClass().getDeclaredMethod("pulse");
            scenePulse.setAccessible(true);
        }
        long a = System.nanoTime();
        scenePulse.invoke(scenePulseListener);
        return ms(System.nanoTime() - a);
    }

    /** A frame on which no month lands: the day's label moved (paintDay), then the scene's pulse. */
    static double idleFrame() throws Exception {
        long a = System.nanoTime();
        call("paintDay");
        scenePulse();
        return ms(System.nanoTime() - a);
    }

    /** What clearMenu's settle does after every redraw (its runLater): a forced CSS pass and a layout over the page's scroller. */
    static double settle() throws Exception {
        javafx.scene.control.ScrollPane scroller = (javafx.scene.control.ScrollPane) f("menuScroller").get(ui);
        long a = System.nanoTime();
        scroller.applyCss();
        scroller.layout();
        return ms(System.nanoTime() - a);
    }

    /** The chrome as clearMenu redraws it: the city panel, the construction panel, the header, the rail, the inbox. */
    static void chrome() throws Exception {
        ui.summaryScreen.refreshCityPanel();
        call("refreshConstructionPanel");
        call("refreshHeader");
        call("refreshTabRail");
        call("refreshInbox");
    }

    /** The page as a fresh build makes it, built off the scene: rootMenu is a detached box for the call. */
    static List<Node> buildDetached() {
        VBox live = ui.rootMenu;
        VBox scratch = new VBox(10);
        ui.rootMenu = scratch;
        try {
            ui.redraw();
        } finally {
            ui.rootMenu = live;
        }
        List<Node> fresh = new ArrayList<>(scratch.getChildren());
        scratch.getChildren().clear();
        return fresh;
    }

    /** The probe's city and window: LongPlaytest's founding grown by its own loop to the month (UP1's UiGrowProbe). */
    static void open(int month) throws Exception {
        out = System.out;
        System.setOut(UiGrowProbe.QUIET);
        Files.createDirectories(OUT);
        Platform.setImplicitExit(false);
        CountDownLatch up = new CountDownLatch(1);
        Platform.startup(up::countDown);
        up.await();
        GameFiles files = GameFiles.scratch("inplace");
        GamePrefs prefs = GamePrefs.load(files);
        prefs.setFullScreen(false);
        prefs.save(files);
        long t0 = System.nanoTime();
        g = fx(() -> UiGrowProbe.found(files));
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
            sc = (Scene) f("scene").get(ui);
            return null;
        });
        fx(() -> { UiGrowProbe.play(g, month); return null; });
        out.printf("month %d grown in %.1f s; people %,d, buildings %,d%n", g.getMonth(),
                (System.nanoTime() - t0) / 1e9, g.getPopulationManager().getPopulation(), UiGrowProbe.buildings(g));
        // Warm the JIT on every screen first, so the first screen measured is not the coldest.
        for (Screen s : screens()) {
            fx(() -> {
                for (int i = 0; i < 4; i++) {
                    s.show().run();
                    pulse();
                    g.toggleNextMonth();
                }
                return null;
            });
        }
    }

    public static void main(String[] args) throws Exception {
        int month = args.length > 0 ? Integer.parseInt(args[0]) : 4000;
        int redraws = args.length > 1 ? Integer.parseInt(args[1]) : 8;
        Set<String> only = args.length > 2 && !args[2].equals("all") ? new HashSet<>(Arrays.asList(args[2].split(","))) : null;
        boolean split = "1".equals(System.getProperty("probe.split"));
        boolean kept = !"0".equals(System.getProperty("probe.kept"));
        double live = Double.parseDouble(System.getProperty("probe.live", "0"));
        open(month);

        out.println();
        out.println("TODAY: the redraw as the clock makes it (median of " + redraws + ", ms; the pulse's own passes)");
        out.println("screen     first |  build    css  layout  sync  settle  idle | chrome alone: build   css  layout | page nodes  labeled  text  region  shape  canvas  control  custom");
        for (Screen s : screens()) {
            if (only != null && !only.contains(s.name())) continue;
            double[] r = fx(() -> {
                ui.showMainMenu();
                pulse();
                long f0 = System.nanoTime();
                s.show().run();
                pulse();
                double first = ms(System.nanoTime() - f0);
                settle();
                List<Double> b = new ArrayList<>(), c = new ArrayList<>(), l = new ArrayList<>(), st = new ArrayList<>(),
                        sy = new ArrayList<>(), idle = new ArrayList<>();
                for (int i = 0; i < redraws; i++) {
                    g.toggleNextMonth();
                    long a0 = System.nanoTime();
                    ui.redraw();
                    long a1 = System.nanoTime();
                    double[] p = pulse();
                    b.add(ms(a1 - a0)); c.add(p[0]); l.add(p[1]);
                    sy.add(scenePulse());
                    st.add(settle());
                }
                for (int i = 0; i < redraws * 2; i++) idle.add(idleFrame());
                List<Double> hb = new ArrayList<>(), hc = new ArrayList<>(), hl = new ArrayList<>();
                for (int i = 0; i < redraws; i++) {
                    g.toggleNextMonth();
                    long a0 = System.nanoTime();
                    chrome();
                    long a1 = System.nanoTime();
                    double[] p = pulse();
                    hb.add(ms(a1 - a0)); hc.add(p[0]); hl.add(p[1]);
                }
                TreePatch.Census k = TreePatch.census(ui.rootMenu.getChildren());
                return new double[]{first, median(b), median(c), median(l), median(st), median(hb), median(hc), median(hl),
                        k.nodes, k.labeled, k.text, k.region, k.shape, k.canvas, k.control, k.custom, median(sy), median(idle)};
            });
            out.printf("%-9s %6.0f | %6.1f %6.1f %6.1f %5.1f %7.1f %5.1f |              %6.1f %5.1f %6.1f | %10.0f %8.0f %5.0f %7.0f %6.0f %7.0f %8.0f %7.0f%n",
                    s.name(), r[0], r[1], r[2], r[3], r[16], r[4], r[17], r[5], r[6], r[7], r[8], r[9], r[10], r[11], r[12], r[13], r[14], r[15]);
        }

        if (kept) {
            out.println();
            out.println("KEPT: the page kept, the month's build made off the scene and only what differs set on it (median of "
                    + redraws + ", ms; counts per month)");
            out.println("screen     build  chrome-pulse  patch  page-css  page-layout | per month: text  style  set  handlers  swapped(nodes)  rows+(nodes)  rows-(nodes)  untouched | words differ");
            for (Screen s : screens()) {
                if (only != null && !only.contains(s.name())) continue;
                Object[] r = fx(() -> {
                    s.show().run();
                    pulse();
                    settle();
                    List<Double> b = new ArrayList<>(), h = new ArrayList<>(), p = new ArrayList<>(), c = new ArrayList<>(),
                            l = new ArrayList<>();
                    TreePatch.Stats sum = new TreePatch.Stats();
                    int mismatch = 0;
                    for (int i = 0; i < redraws; i++) {
                        g.toggleNextMonth();
                        long a0 = System.nanoTime();
                        List<Node> fresh = buildDetached();
                        long a1 = System.nanoTime();
                        // The chrome was rebuilt by clearMenu, as today: its pulse paid here, apart from the page's.
                        double[] hp = pulse();
                        List<String> want = TreePatch.words(fresh);
                        long a2 = System.nanoTime();
                        TreePatch.Stats st = TreePatch.patch(ui.rootMenu, fresh);
                        long a3 = System.nanoTime();
                        double[] pp = pulse();
                        List<String> got = TreePatch.words(ui.rootMenu.getChildren());
                        if (!want.equals(got)) mismatch++;
                        b.add(ms(a1 - a0)); h.add(hp[0] + hp[1]); p.add(ms(a3 - a2)); c.add(pp[0]); l.add(pp[1]);
                        sum.add(st);
                    }
                    return new Object[]{new double[]{median(b), median(h), median(p), median(c), median(l)}, sum, mismatch};
                });
                double[] t = (double[]) r[0];
                TreePatch.Stats st = (TreePatch.Stats) r[1];
                int n = redraws;
                out.printf("%-9s %6.1f %13.1f %6.1f %9.1f %12.1f |       %6.0f %6.0f %4.0f %9.0f %7.0f(%5.0f) %7.0f(%5.0f) %7.0f(%5.0f) %9.0f | %d of %d%n",
                        s.name(), t[0], t[1], t[2], t[3], t[4], st.text / (double) n, st.style / (double) n, st.set / (double) n,
                        st.handlers / (double) n, st.swapped / (double) n, st.swappedNodes / (double) n,
                        st.added / (double) n, st.addedNodes / (double) n, st.removed / (double) n, st.removedNodes / (double) n,
                        st.untouched / (double) n, (int) r[2], n);
                out.println("    pictures kept: " + st.pictures / n + " a month; swapped by class: " + top(st.swappedBy, 8));
                out.println("    changes by card:  " + top(st.byCard, 8));
                out.println("    swaps:            " + top(st.swapDetail, 6));
                out.println("    set by property:  " + top(TreePatch.Stats.SET_BY, 8));
                TreePatch.Stats.SET_BY.clear();
            }
        }

        if (split) {
            out.println();
            out.println("SPLIT: where the build's time goes (JFR, 1 ms samples over " + (redraws * 4) + " redraws a screen)");
            out.println("screen     samples  nodes%  screen-code%  model%  other%");
            for (Screen s : screens()) {
                if (only != null && !only.contains(s.name())) continue;
                Recording rec = new Recording();
                rec.enable("jdk.ExecutionSample").withPeriod(Duration.ofMillis(1)).withStackTrace();
                rec.start();
                fx(() -> {
                    s.show().run();
                    for (int i = 0; i < redraws * 4; i++) {
                        g.toggleNextMonth();
                        ui.redraw();
                        pulse();
                    }
                    return null;
                });
                rec.stop();
                Path jfr = OUT.resolve("split-" + s.name() + ".jfr");
                rec.dump(jfr);
                rec.close();
                int[] k = splitOf(jfr);
                Files.deleteIfExists(jfr);
                double all = Math.max(1, k[0]);
                out.printf("%-9s %8d %7.0f %13.0f %7.0f %7.0f%n", s.name(), k[0], 100 * k[1] / all, 100 * k[2] / all,
                        100 * k[3] / all, 100 * k[4] / all);
                if (TOP) {
                    out.println("    leaf frames:");
                    LEAF.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(25)
                            .forEach(e -> out.printf("      %5.1f%%  %s%n", 100 * e.getValue() / all, e.getKey()));
                    out.println("    our frames, inclusive:");
                    HAM.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(40)
                            .forEach(e -> out.printf("      %5.1f%%  %s%n", 100 * e.getValue() / all, e.getKey()));
                    LEAF.clear();
                    HAM.clear();
                }
            }
        }

        if (live > 0) liveRun(live, only);
        Platform.exit();
        System.exit(0);
    }

    /**
     * The real clock at 50x on each screen for some seconds, the FX thread sampled every millisecond:
     * months a second, frames, and the thread's milliseconds a second by what it was doing - the month,
     * the redraw's build, the pulse (CSS, layout and the sync to the renderer), the settle, the rest.
     */
    static void liveRun(double seconds, Set<String> only) throws Exception {
        Field speedIndex = f("speedIndex"), clockRunning = f("clockRunning"), pausedBecause = f("pausedBecause");
        out.println();
        out.println("LIVE: the clock at 50x, " + seconds + " s a screen after 1 s to settle (the FX thread's CPU ms a second, shared out by 5 ms samples)");
        out.println("screen   months/s  frame p50  p95  max | month  build  pulse  settle  other  | busy");
        for (Screen s : screens()) {
            if (only != null && !only.contains(s.name())) continue;
            fx(() -> { s.show().run(); return null; });
            List<Double> frames = Collections.synchronizedList(new ArrayList<>());
            long[] last = {0};
            javafx.animation.AnimationTimer probe = new javafx.animation.AnimationTimer() {
                @Override public void handle(long now) {
                    if (last[0] != 0) frames.add((now - last[0]) / 1e6);
                    last[0] = now;
                    try {
                        clockRunning.set(ui, true);
                        pausedBecause.set(ui, null);
                        speedIndex.set(ui, 8);
                    } catch (IllegalAccessException e) { throw new RuntimeException(e); }
                }
            };
            Recording rec = new Recording();
            rec.enable("jdk.ExecutionSample").withPeriod(Duration.ofMillis(5)).withStackTrace();
            long fxId = fx(() -> { probe.start(); return Thread.currentThread().getId(); });
            Thread.sleep(1000);   // the clock up to speed, and the first redraws' JIT, before the window measured
            java.lang.management.ThreadMXBean mx = java.lang.management.ManagementFactory.getThreadMXBean();
            frames.clear();
            int m0 = fx(() -> g.getMonth());
            long cpu0 = mx.getThreadCpuTime(fxId);
            rec.start();
            long w0 = System.nanoTime();
            Thread.sleep((long) (seconds * 1000));
            rec.stop();
            long cpu1 = mx.getThreadCpuTime(fxId);
            double secs = (System.nanoTime() - w0) / 1e9;
            int m1 = fx(() -> { probe.stop(); clockRunning.set(ui, false); return g.getMonth(); });
            Path jfr = OUT.resolve("live-" + s.name() + ".jfr");
            rec.dump(jfr);
            rec.close();
            int[] k = liveOf(jfr);
            Files.deleteIfExists(jfr);
            // The FX thread's own CPU time over the window, shared out by the samples' kinds.
            double busyMs = (cpu1 - cpu0) / 1e6 / secs, all = Math.max(1, k[0]);
            out.printf("%-9s %7.1f %10.0f %5.0f %5.0f | %5.0f %6.0f %6.0f %7.0f %6.0f  | %4.0f%%%n", s.name(), (m1 - m0) / secs,
                    median(frames), pct(frames, .95), frames.isEmpty() ? Double.NaN : Collections.max(frames),
                    busyMs * k[1] / all, busyMs * k[2] / all, busyMs * k[3] / all, busyMs * k[4] / all, busyMs * k[5] / all,
                    busyMs / 10);
            if (DEBUG_LIVE) {
                STACKS.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(25)
                        .forEach(e -> out.println("   " + e.getValue() + "  " + e.getKey()));
                STACKS.clear();
            }
        }
    }

    static final boolean DEBUG_LIVE = "1".equals(System.getProperty("probe.debuglive"));
    static final Map<String, Integer> STACKS = new HashMap<>();

    /** {all, month, build, pulse, settle, other} FX-thread samples. */
    static int[] liveOf(Path jfr) throws Exception {
        int[] k = new int[6];
        try (RecordingFile rf = new RecordingFile(jfr)) {
            while (rf.hasMoreEvents()) {
                RecordedEvent e = rf.readEvent();
                if (!e.getEventType().getName().equals("jdk.ExecutionSample")) continue;
                if (e.getThread("sampledThread") == null
                        || !"JavaFX Application Thread".equals(e.getThread("sampledThread").getJavaName())) continue;
                RecordedStackTrace st = e.getStackTrace();
                if (st == null) continue;
                k[0]++;
                int cls = 5;
                if (DEBUG_LIVE) {
                    List<RecordedFrame> fs = st.getFrames();
                    StringBuilder path = new StringBuilder();
                    for (int i = fs.size() - 1, shown = 0; i >= 0 && shown < 9; i--) {
                        String nm = fs.get(i).getMethod().getType().getName();
                        nm = nm.substring(nm.lastIndexOf('.') + 1) + "." + fs.get(i).getMethod().getName();
                        if (i < fs.size() - 6) { path.append(" > ").append(nm); shown++; }
                    }
                    STACKS.merge(path.toString(), 1, Integer::sum);
                }
                for (RecordedFrame fr : st.getFrames()) {
                    String nm = fr.getMethod().getType().getName() + "." + fr.getMethod().getName();
                    if (nm.startsWith("ham.citybuildersim.Game.nextMonth") || nm.startsWith("ham.citybuildersim.Game.toggleNextMonth")) { cls = 1; break; }
                    if (nm.matches("ham\\.citybuildersim\\.ui\\.\\w+\\.show\\w+")) { cls = 2; break; }
                    if (nm.startsWith("javafx.scene.Scene$ScenePulseListener.pulse")) { cls = 3; break; }
                    if (nm.startsWith("ham.citybuildersim.ui.UserInterface.lambda$clearMenu")) { cls = 4; break; }
                }
                k[cls]++;
            }
        }
        return k;
    }

    static final boolean TOP = "1".equals(System.getProperty("probe.top"));
    static final Map<String, Integer> LEAF = new HashMap<>(), HAM = new HashMap<>();

    static String name(RecordedFrame fr) {
        return fr.getMethod().getType().getName() + "." + fr.getMethod().getName();
    }

    static String top(Map<String, Integer> m, int n) {
        StringBuilder b = new StringBuilder();
        m.entrySet().stream().sorted((a, c) -> c.getValue() - a.getValue()).limit(n)
                .forEach(e -> b.append(e.getKey()).append('=').append(e.getValue()).append("  "));
        return b.toString().trim();
    }

    /**
     * The build's FX-thread samples inside UserInterface.redraw (not the pulse): classified by the
     * first frame from the leaf that is JavaFX (making and wiring nodes), the ui package (the
     * screen's own code: its words, its formatting) or the model.
     */
    static int[] splitOf(Path jfr) throws Exception {
        int[] k = new int[5];
        try (RecordingFile rf = new RecordingFile(jfr)) {
            while (rf.hasMoreEvents()) {
                RecordedEvent e = rf.readEvent();
                if (!e.getEventType().getName().equals("jdk.ExecutionSample")) continue;
                if (e.getThread("sampledThread") == null
                        || !"JavaFX Application Thread".equals(e.getThread("sampledThread").getJavaName())) continue;
                RecordedStackTrace st = e.getStackTrace();
                if (st == null) continue;
                boolean inRedraw = false;
                for (RecordedFrame fr : st.getFrames()) {
                    String nm = fr.getMethod().getType().getName() + "." + fr.getMethod().getName();
                    if (nm.equals("ham.citybuildersim.ui.UserInterface.redraw")) { inRedraw = true; break; }
                }
                if (!inRedraw) continue;
                k[0]++;
                if (TOP) {
                    List<RecordedFrame> fs = st.getFrames();
                    String leaf = fs.isEmpty() ? "?" : name(fs.get(0));
                    LEAF.merge(leaf, 1, Integer::sum);
                    Set<String> seen = new HashSet<>();
                    for (RecordedFrame fr : fs) {
                        String nm = name(fr);
                        if (nm.startsWith("ham.") && seen.add(nm)) HAM.merge(nm, 1, Integer::sum);
                    }
                }
                int cls = 4;
                for (RecordedFrame fr : st.getFrames()) {
                    String t = fr.getMethod().getType().getName();
                    if (t.startsWith("javafx.") || t.startsWith("com.sun.javafx.") || t.startsWith("com.sun.prism.")
                            || t.startsWith("com.sun.glass.")) { cls = 1; break; }
                    if (t.startsWith("ham.citybuildersim.ui.")) { cls = 2; break; }
                    if (t.startsWith("ham.citybuildersim.")) { cls = 3; break; }
                }
                k[cls]++;
            }
        }
        return k;
    }
}
