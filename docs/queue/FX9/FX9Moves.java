package ham.citybuildersim;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Scratch, not in the build (batch FX9): the default playtest with every district's own plan drawn every month from probe.from to
 * probe.to with the streets on bought ground on (play's way); the boxes that moved between months counted by the layout watch's
 * rule, and at each move the same pair drawn with the streets off (FX6's way, main's plans), to tell FX9's moves from main's.
 */
public class FX9Moves {
    static PrintStream log;
    static final int FROM = Integer.getInteger("probe.from", 2), TO = Integer.getInteger("probe.to", Integer.MAX_VALUE);
    static final Map<Integer, DistrictPlan.Input> prevIn = new HashMap<>();
    static final Map<Integer, long[]> prevOn = new HashMap<>();
    static long pairs, boxes, movedOn, movedOff, pairsOn, pairsOff, pairsBoth, nanos, layingDistrictMonths;
    static int last;
    static final boolean PRIMARY = !Boolean.getBoolean("probe.off");

    public static void main(String[] args) throws Exception {
        log = new PrintStream(new java.io.FileOutputStream(args[0]), true);
        LongPlaytest.probeHook = FX9Moves::month;
        try {
            LongPlaytest.main(new String[0]);
        } finally {
            summary();
        }
    }

    static void summary() {
        log.printf("SUMMARY m%d..m%d: %,d district month pairs, %,d boxes compared; primary (streets " + (PRIMARY ? "on" : "off") + "): %,d moved in %d pairs; at those pairs the other way: %,d moved in %d pairs (%d pairs moved both ways);"
                + " %,d district months with a laying on bought ground; planning took %.0f s%n", FROM, last, pairs, boxes, movedOn, pairsOn, movedOff, pairsOff, pairsBoth, layingDistrictMonths, nanos / 1e9);
    }

    static DistrictPlan make(DistrictPlan.Input in, boolean on) {
        DistrictPlan.streetsOnBought = on;
        try {
            return DistrictPlan.make(in);
        } finally {
            DistrictPlan.streetsOnBought = true;
        }
    }

    static void month(Game g) {
        int month = g.getMonth();
        if (month < FROM - 1 || month > TO) return;
        last = month;
        long t0 = System.nanoTime();
        try {
            CityMap m = g.getCityMap();
            int nt = m.types().length;
            for (CityMap.District d : m.districts()) {
                DistrictPlan.Input in = m.planInput(d);
                DistrictPlan p = make(in, PRIMARY);
                if (p.boughtCells > 0) layingDistrictMonths++;
                long[] b = FX9Probe.boxes(p);
                int key = d.index;
                DistrictPlan.Input pin = prevIn.get(key);
                if (pin != null && month >= FROM) {
                    pairs++;
                    boxes += b.length;
                    int mv = FX9Probe.moved(prevOn.get(key), b, nt);
                    if (mv > 0) {
                        int mvOff = FX9Probe.moved(FX9Probe.boxes(make(pin, !PRIMARY)), FX9Probe.boxes(make(m.planInput(d), !PRIMARY)), nt);
                        movedOn += mv;
                        pairsOn++;
                        movedOff += mvOff;
                        if (mvOff > 0) { pairsOff++; pairsBoth++; }
                        log.printf("m%d district (%d,%d): %d of %d boxes moved (walls %d), %d the other way; layings on bought ground %d%n", month, d.dx, d.dy, mv, b.length,
                                p.wallTries, mvOff, p.boughtCells);
                    }
                }
                prevIn.put(key, in);
                prevOn.put(key, b);
            }
        } catch (Throwable t) {
            t.printStackTrace(log);
        }
        nanos += System.nanoTime() - t0;
        if (month % 100 == 0) summary();
    }
}
