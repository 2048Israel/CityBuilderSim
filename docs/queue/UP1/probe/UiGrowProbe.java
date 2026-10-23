package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/** Scratch probe (not in the tree): grows LongPlaytest's city, its own loop, timing each model month. */
public final class UiGrowProbe {

    public static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    /** Per-month wall time of LongPlaytest.run(g, 1), nanoseconds, by month index. */
    public static final long[] MONTH_NANOS = new long[20_000];

    private static int stop;

    public static Game found(GameFiles files) {
        PrintStream real = System.out;
        LongPlaytest.out = QUIET;
        System.setOut(QUIET);
        try {
            LongPlaytest.resetTheFloat();
            String spot = System.getProperty("probe.earth");
            Game g = new Game(files, spot == null ? LongPlaytest.founding() : LongPlaytest.earthFounding(spot));
            g.run();
            LongPlaytest.childcareWhenNeeded(g);
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.getCityMap();
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            LongPlaytest.seedMarket(g, 0);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            run(g, 3);
            LongPlaytest.villageBuild(g, "House", 20);
            run(g, 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            run(g, 5);
            LongPlaytest.advise(g);
            run(g, 6);
            LongPlaytest.ensureSchools(g);
            return g;
        } finally {
            System.setOut(real);
        }
    }

    static void run(Game g, int months) {
        for (int i = 0; i < months; i++) {
            int m = g.getMonth();
            long t0 = System.nanoTime();
            LongPlaytest.run(g, 1);
            if (m >= 0 && m < MONTH_NANOS.length) MONTH_NANOS[m] = System.nanoTime() - t0;
        }
    }

    /** LongPlaytest's loop, to a month. */
    public static void play(Game g, int toMonth) {
        PrintStream real = System.out;
        LongPlaytest.out = QUIET;
        System.setOut(QUIET);
        try {
            while (g.getMonth() < toMonth) {
                stop++;
                int skip = switch (stop % 6) {
                    case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120;
                };
                skip = Math.min(skip, LongPlaytest.longestSkip());
                run(g, Math.min(skip, Math.max(1, toMonth - g.getMonth())));
                LongPlaytest.ensureSchools(g);
                LongPlaytest.childcareWhenNeeded(g);
                for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    run(g, 1);
                }
                run(g, 2);
            }
        } finally {
            System.setOut(real);
        }
    }

    /** Mean model month, ms, over [from, to). */
    public static double meanMs(int from, int to) {
        long sum = 0; int n = 0;
        for (int m = Math.max(0, from); m < to && m < MONTH_NANOS.length; m++) {
            if (MONTH_NANOS[m] > 0) { sum += MONTH_NANOS[m]; n++; }
        }
        return n == 0 ? Double.NaN : sum / 1e6 / n;
    }

    public static int buildings(Game g) {
        long n = 0;
        for (long c : g.getMapCounts()) n += c;
        return (int) n;
    }
}
