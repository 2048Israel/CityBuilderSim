package ham.citybuildersim.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Scratch probe for the study UP4, never in src/: the pilot (the People page kept) against the
 * page as it was (built afresh each redraw), in ONE JVM on one city, turn and turn about - so the
 * machine's load, which other batches' checks share, falls on both alike. "Afresh" is the kept
 * page's pool forgotten before each redraw: keepMenu then clears the page and draws it from
 * nothing, as clearMenu did for every screen before the pilot (and as it still does for the rest).
 * Per redraw: the draw (with the chrome, which neither changes), the pulse's CSS and layout, the
 * sync to the renderer, and the settle clearMenu runs after a rebuilt page (none after a kept one);
 * then the clock at 50x, kept and afresh in alternating stretches, with frames and the FX thread's
 * CPU by kind. Arguments: month, redraws a turn, turns, live seconds a stretch.
 */
public final class PilotProbe {

    public static void main(String[] args) throws Exception {
        int month = args.length > 0 ? Integer.parseInt(args[0]) : 4000;
        int redraws = args.length > 1 ? Integer.parseInt(args[1]) : 8;
        int turns = args.length > 2 ? Integer.parseInt(args[2]) : 4;
        double live = args.length > 3 ? Double.parseDouble(args[3]) : 4;
        InPlaceProbe.open(month);
        UserInterface ui = InPlaceProbe.ui;
        Kept.Pool pool = ui.peopleScreen.pool;

        // {draw, css, layout, sync, settle, made, kept} per redraw, for each way.
        List<double[]> kept = new ArrayList<>(), fresh = new ArrayList<>();
        InPlaceProbe.fx(() -> {
            ui.peopleScreen.showPopulationInfoMenu();
            InPlaceProbe.scenePulse();
            return null;
        });
        for (int t = 0; t < turns; t++) {
            for (boolean afresh : new boolean[]{false, true}) {
                InPlaceProbe.fx(() -> {
                    for (int i = 0; i < redraws; i++) {
                        InPlaceProbe.g.toggleNextMonth();
                        if (afresh) pool.forget();
                        long a = System.nanoTime();
                        ui.redraw();
                        double draw = (System.nanoTime() - a) / 1e6;
                        double[] p = InPlaceProbe.pulse();
                        double sync = InPlaceProbe.scenePulse();
                        double settle = afresh ? InPlaceProbe.settle() : 0;
                        (afresh ? fresh : kept).add(new double[]{draw, p[0], p[1], sync, settle, pool.lastMade(), pool.lastKept()});
                    }
                    return null;
                });
            }
        }
        InPlaceProbe.out.printf("%nPEOPLE at month %d, %d redraws each way in %d alternating turns (median, ms):%n",
                InPlaceProbe.g.getMonth(), redraws * turns, turns);
        InPlaceProbe.out.println("way        draw    css  layout  sync  settle | total | nodes made  handed back");
        for (boolean afresh : new boolean[]{true, false}) {
            List<double[]> v = afresh ? fresh : kept;
            double[] m = new double[7];
            for (int k = 0; k < 7; k++) {
                List<Double> col = new ArrayList<>();
                for (double[] r : v) col.add(r[k]);
                m[k] = InPlaceProbe.median(col);
            }
            List<Double> tot = new ArrayList<>();
            for (double[] r : v) tot.add(r[0] + r[1] + r[2] + r[3] + r[4]);
            InPlaceProbe.out.printf("%-8s %6.1f %6.1f %6.1f %5.1f %7.1f | %5.1f | %10.0f %12.0f%n", afresh ? "afresh" : "kept",
                    m[0], m[1], m[2], m[3], m[4], InPlaceProbe.median(tot), m[5], m[6]);
        }
        InPlaceProbe.out.println("(the chrome - header, rail, panels, inbox - is rebuilt by both ways: UP2's)");

        // The chrome alone, for its share of either way.
        double[] chrome = InPlaceProbe.fx(() -> {
            List<Double> b = new ArrayList<>(), c = new ArrayList<>(), l = new ArrayList<>(), s = new ArrayList<>();
            for (int i = 0; i < redraws * 2; i++) {
                InPlaceProbe.g.toggleNextMonth();
                long a = System.nanoTime();
                InPlaceProbe.chrome();
                b.add((System.nanoTime() - a) / 1e6);
                double[] p = InPlaceProbe.pulse();
                c.add(p[0]);
                l.add(p[1]);
                s.add(InPlaceProbe.scenePulse());
            }
            return new double[]{InPlaceProbe.median(b), InPlaceProbe.median(c), InPlaceProbe.median(l), InPlaceProbe.median(s)};
        });
        InPlaceProbe.out.printf("chrome alone: build %.1f, css %.1f, layout %.1f, sync %.1f ms%n", chrome[0], chrome[1], chrome[2], chrome[3]);

        if (live > 0) liveAB(ui, pool, live, turns);
        javafx.application.Platform.exit();
        System.exit(0);
    }

    /**
     * The clock at 50x on People, in alternating stretches: kept, afresh, and kept with the count-up
     * held off ("still": the page told no month landed while it was watched, so LIVING HERE is set
     * at once and the month's bars do not grow) - frames and the FX thread's CPU by kind. A first
     * stretch of each, unreported, warms the JIT.
     */
    static void liveAB(UserInterface ui, Kept.Pool pool, double seconds, int turns) throws Exception {
        Field speedIndex = InPlaceProbe.f("speedIndex"), clockRunning = InPlaceProbe.f("clockRunning"),
                pausedBecause = InPlaceProbe.f("pausedBecause");
        Field shown = PeopleScreen.class.getDeclaredField("shownMonth");
        shown.setAccessible(true);
        InPlaceProbe.out.println();
        InPlaceProbe.out.println("LIVE: the clock at 50x on People, " + seconds + " s a stretch after 1 s to settle, alternating");
        InPlaceProbe.out.println("way      months/s  frame p50  p95  max | month  build  pulse  settle  other | busy");
        for (int t = -1; t < turns; t++) {
            final boolean report = t >= 0;
            for (String way : new String[]{"kept", "afresh", "still"}) {
                final boolean afresh = way.equals("afresh"), still = way.equals("still");
                InPlaceProbe.fx(() -> { ui.peopleScreen.showPopulationInfoMenu(); return null; });
                List<Double> frames = Collections.synchronizedList(new ArrayList<>());
                long[] last = {0};
                javafx.animation.AnimationTimer probe = new javafx.animation.AnimationTimer() {
                    @Override public void handle(long now) {
                        if (last[0] != 0) frames.add((now - last[0]) / 1e6);
                        last[0] = now;
                        if (afresh) pool.forget();
                        try {
                            if (still) shown.setInt(ui.peopleScreen, -1);
                            clockRunning.set(ui, true);
                            pausedBecause.set(ui, null);
                            speedIndex.set(ui, 8);
                        } catch (IllegalAccessException e) { throw new RuntimeException(e); }
                    }
                };
                jdk.jfr.Recording rec = new jdk.jfr.Recording();
                rec.enable("jdk.ExecutionSample").withPeriod(java.time.Duration.ofMillis(5)).withStackTrace();
                long fxId = InPlaceProbe.fx(() -> { probe.start(); return Thread.currentThread().getId(); });
                Thread.sleep(1000);
                java.lang.management.ThreadMXBean mx = java.lang.management.ManagementFactory.getThreadMXBean();
                frames.clear();
                int m0 = InPlaceProbe.fx(() -> InPlaceProbe.g.getMonth());
                long cpu0 = mx.getThreadCpuTime(fxId);
                rec.start();
                long w0 = System.nanoTime();
                Thread.sleep((long) (seconds * 1000));
                rec.stop();
                long cpu1 = mx.getThreadCpuTime(fxId);
                double secs = (System.nanoTime() - w0) / 1e9;
                int m1 = InPlaceProbe.fx(() -> { probe.stop(); clockRunning.set(ui, false); return InPlaceProbe.g.getMonth(); });
                java.nio.file.Path jfr = InPlaceProbe.OUT.resolve("pilot-live.jfr");
                rec.dump(jfr);
                rec.close();
                int[] k = InPlaceProbe.liveOf(jfr);
                java.nio.file.Files.deleteIfExists(jfr);
                double busyMs = (cpu1 - cpu0) / 1e6 / secs, all = Math.max(1, k[0]);
                if (!report) continue;
                InPlaceProbe.out.printf("%-8s %8.1f %10.0f %5.0f %5.0f | %5.0f %6.0f %6.0f %7.0f %6.0f | %4.0f%%%n",
                        way, (m1 - m0) / secs,
                        InPlaceProbe.median(frames), InPlaceProbe.pct(frames, .95),
                        frames.isEmpty() ? Double.NaN : Collections.max(frames),
                        busyMs * k[1] / all, busyMs * k[2] / all, busyMs * k[3] / all, busyMs * k[4] / all, busyMs * k[5] / all,
                        busyMs / 10);
            }
        }
    }
}
