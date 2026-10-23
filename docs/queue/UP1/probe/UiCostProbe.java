package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.GamePrefs;
import ham.citybuildersim.UiGrowProbe;
import javafx.application.Platform;
import javafx.stage.Stage;
import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import jdk.jfr.consumer.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/** Scratch probe (not in the tree): what the interface costs on the FX thread as a city ages. */
public final class UiCostProbe {

    static UserInterface ui;
    static Game g;
    static Stage stage;
    static final Path OUT = Path.of(System.getProperty("probe.out", "."));

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
        List<Double> s = new ArrayList<>(v); Collections.sort(s);
        return s.get(s.size() / 2);
    }

    static double pct(List<Double> v, double p) {
        if (v.isEmpty()) return Double.NaN;
        List<Double> s = new ArrayList<>(v); Collections.sort(s);
        return s.get(Math.min(s.size() - 1, (int) Math.floor(p * s.size())));
    }

    public static void main(String[] args) throws Exception {
        int[] checkpoints = Arrays.stream(args[0].split(",")).mapToInt(Integer::parseInt).toArray();
        double liveSeconds = args.length > 1 ? Double.parseDouble(args[1]) : 3;
        Files.createDirectories(OUT);
        Platform.setImplicitExit(false);
        CountDownLatch up = new CountDownLatch(1);
        Platform.startup(up::countDown);
        up.await();

        GameFiles files = GameFiles.scratch("uicost");
        GamePrefs prefs = GamePrefs.load(files);
        prefs.setFullScreen(false);
        prefs.save(files);

        long t0 = System.nanoTime();
        g = fx(() -> UiGrowProbe.found(files));
        System.out.printf("founded in %.1f s%n", (System.nanoTime() - t0) / 1e9);

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
        Field speedIndex = f("speedIndex"), clockRunning = f("clockRunning"), pausedBecause = f("pausedBecause");
        Field sceneF = f("scene");

        int lastGrowth = g.getMonth();
        for (int cp : checkpoints) {
            long g0 = System.nanoTime();
            fx(() -> { UiGrowProbe.play(g, cp); return null; });
            double growS = (System.nanoTime() - g0) / 1e9;
            int at = g.getMonth();
            System.out.printf("%n=== month %d (grown in %.1f s; model month mean %.1f ms over the last stretch) ===%n",
                    at, growS, UiGrowProbe.meanMs(lastGrowth, at));
            lastGrowth = at;
            System.out.printf("people %,d, buildings %,d, history months %d, districts %d, decisions %d, heap used %d MB%n",
                    g.getPopulationManager().getPopulation(), UiGrowProbe.buildings(g),
                    g.getHistorySave().months(), g.hasCityMap() ? g.getCityMap().districts().size() : -1,
                    g.getDecisions().size(),
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20);

            // ---- the save, as the autosave writes it, on the FX thread
            for (int k = 0; k < 2; k++) {
                long s0 = System.nanoTime();
                fx(() -> g.saveGame(GameFiles.AUTOSAVE_SLOT, "probe"));
                double saveMs = ms(System.nanoTime() - s0);
                if (k == 1) System.out.printf("save %.0f ms; save %.1f MB, history %.1f MB, map %.1f MB%n", saveMs,
                        size(files.saveFile(GameFiles.AUTOSAVE_SLOT)), size(files.historyFile(GameFiles.AUTOSAVE_SLOT)),
                        size(files.mapFile(GameFiles.AUTOSAVE_SLOT)));
            }
            long l0 = System.nanoTime();
            Game back = new Game(files);
            back.loadGameSave(GameFiles.AUTOSAVE_SLOT);
            System.out.printf("load %.0f ms (month %d)%n", ms(System.nanoTime() - l0), back.getMonth());
            back = null;

            // ---- the month as the clock runs it (toggleNextMonth), 24 of them: the autosave lands in one
            List<Double> monthMs = fx(() -> {
                List<Double> v = new ArrayList<>();
                for (int i = 0; i < 24; i++) {
                    long a = System.nanoTime();
                    g.toggleNextMonth();
                    v.add(ms(System.nanoTime() - a));
                }
                return v;
            });
            System.out.printf("clock month: median %.1f ms, max %.0f ms (the autosave's)%n", median(monthMs),
                    Collections.max(monthMs));

            // ---- each screen: first build, then redraws as the clock makes them
            System.out.println("screen     first  build(med) css+layout(med)  build(max)");
            for (Screen s : screens()) {
                try {
                    double[] r = fx(() -> {
                        javafx.scene.Scene sc = (javafx.scene.Scene) sceneF.get(ui);
                        long a = System.nanoTime();
                        s.show().run();
                        sc.getRoot().applyCss();
                        sc.getRoot().layout();
                        double first = ms(System.nanoTime() - a);
                        List<Double> build = new ArrayList<>(), lay = new ArrayList<>();
                        for (int i = 0; i < 8; i++) {
                            g.toggleNextMonth();
                            long b0 = System.nanoTime();
                            ui.redraw();
                            long b1 = System.nanoTime();
                            sc.getRoot().applyCss();
                            sc.getRoot().layout();
                            long b2 = System.nanoTime();
                            build.add(ms(b1 - b0));
                            lay.add(ms(b2 - b1));
                        }
                        return new double[]{first, median(build), median(lay), Collections.max(build)};
                    });
                    System.out.printf("%-9s %6.0f %10.1f %14.1f %11.1f%n", s.name(), r[0], r[1], r[2], r[3]);
                } catch (Exception e) {
                    System.out.println(s.name() + " FAILED " + e);
                }
            }

            // ---- live: the real clock at 50x, each screen for a few seconds, the FX thread profiled
            Recording rec = new Recording(Configuration.getConfiguration("profile"));
            rec.start();
            System.out.println("live 50x   months/s  frame p50  p95   max (ms)");
            for (Screen s : screens()) {
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
                int m0 = fx(() -> { probe.start(); return g.getMonth(); });
                long w0 = System.nanoTime();
                Thread.sleep((long) (liveSeconds * 1000));
                int m1 = fx(() -> { probe.stop(); clockRunning.set(ui, false); return g.getMonth(); });
                double secs = (System.nanoTime() - w0) / 1e9;
                System.out.printf("%-9s %9.1f %10.0f %5.0f %6.0f%n", s.name(), (m1 - m0) / secs,
                        median(frames), pct(frames, .95), frames.isEmpty() ? Double.NaN : Collections.max(frames));
            }
            rec.stop();
            Path jfr = OUT.resolve("ui-" + at + ".jfr");
            rec.dump(jfr);
            rec.close();
            analyse(jfr);
        }
        System.out.println("\nmodel month mean by stretch (ms):");
        for (int a = 0; a < g.getMonth(); a += 500) System.out.printf("  %d-%d: %.1f%n", a, a + 500, UiGrowProbe.meanMs(a, a + 500));
        Platform.exit();
        System.exit(0);
    }

    static double size(Path p) {
        try { return Files.size(p) / 1e6; } catch (Exception e) { return -1; }
    }

    static final String[] KEYS = {
            "ham.citybuildersim.ui.UserInterface$",            // the clock and other timers (anonymous)
            "ham.citybuildersim.Game.nextMonth",
            "ham.citybuildersim.Game.autosave",
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
            "ham.citybuildersim.ui.ChartModel.",
            "ham.citybuildersim.ChartModel.",
            "ham.citybuildersim.ui.TimeChart.setData",
            "ham.citybuildersim.ui.TimeChart.draw",
            "ham.citybuildersim.ui.TimeChart.",
            "ham.citybuildersim.ui.MiniMap.tick",
            "ham.citybuildersim.ui.MapView.tick",
            "ham.citybuildersim.MapTiles.",
            "ham.citybuildersim.CityMap.",
            "ham.citybuildersim.MiniMapFrame.",
            "javafx.scene.Scene$ScenePulseListener.pulse",
            "javafx.scene.Parent.layout",
            "javafx.scene.Node.processCSS",
            "javafx.scene.Node.applyCss",
            "javafx.scene.Parent$",                          // children-list changes
            "javafx.scene.text.",
            "com.sun.javafx.text.",
            "javafx.scene.canvas.",
    };

    static void analyse(Path jfr) throws Exception {
        Map<String, Integer> incl = new LinkedHashMap<>();
        for (String k : KEYS) incl.put(k, 0);
        Map<String, Integer> show = new TreeMap<>();
        Map<String, Integer> topHam = new HashMap<>();
        Map<String, Integer> threads = new HashMap<>();
        int fxSamples = 0, truncated = 0;
        double gcMs = 0; int gcN = 0;
        try (RecordingFile rf = new RecordingFile(jfr)) {
            while (rf.hasMoreEvents()) {
                RecordedEvent e = rf.readEvent();
                String type = e.getEventType().getName();
                if (type.equals("jdk.GarbageCollection")) { gcMs += e.getDuration().toNanos() / 1e6; gcN++; continue; }
                if (!type.equals("jdk.ExecutionSample")) continue;
                RecordedThread t = e.getThread("sampledThread");
                String tn = t == null ? "?" : t.getJavaName();
                threads.merge(tn, 1, Integer::sum);
                if (!"JavaFX Application Thread".equals(tn)) continue;
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
        System.out.printf("JFR: FX-thread samples %d (truncated %d); GC %d pauses, %.0f ms total%n", fxSamples, truncated, gcN, gcMs);
        System.out.print("threads:");
        threads.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(6)
                .forEach(x -> System.out.printf(" %s=%d", x.getKey(), x.getValue()));
        System.out.println();
        final int all = Math.max(1, fxSamples);
        System.out.println("inclusive share of FX-thread samples:");
        incl.forEach((k, v) -> { if (v > 0) System.out.printf("  %5.1f%%  %s%n", 100.0 * v / all, k); });
        show.forEach((k, v) -> System.out.printf("  %5.1f%%  %s%n", 100.0 * v / all, k));
        System.out.println("top ham frames (self within our code):");
        topHam.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(14)
                .forEach(x -> System.out.printf("  %5.1f%%  %s%n", 100.0 * x.getValue() / all, x.getKey()));
    }
}
