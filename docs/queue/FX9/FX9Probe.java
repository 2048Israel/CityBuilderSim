package ham.citybuildersim;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Scratch, not in the build (batch FX9): the default playtest with every district's own plan drawn each month from
 * probe.from to probe.to, with the streets on bought ground on and off; the moves between months counted, and at a move
 * the inputs differenced and swapped part by part from the month before. Like FX7Probe it needs a copy of LongPlaytest.java with a
 * hook: `static java.util.function.Consumer<Game> probeHook;` after `static boolean layoutOn;`, and
 * `if (probeHook != null) probeHook.accept(g);` after `if (layoutOn) layoutMonth(g);` (FX9Moves, FX9Probe3 and FX9Probe4 use it too).
 *   java -cp "<out>;<classes>;<jars>" ham.citybuildersim.FX9Probe <log.txt>
 */
public class FX9Probe {
    static PrintStream log;
    static final int FROM = Integer.getInteger("probe.from", 1200), TO = Integer.getInteger("probe.to", 1214);
    static final Map<Integer, DistrictPlan.Input> prevIn = new HashMap<>();
    static final Map<Integer, long[]> prevOn = new HashMap<>(), prevOff = new HashMap<>();

    public static void main(String[] args) throws Exception {
        log = new PrintStream(new java.io.FileOutputStream(args[0]), true);
        LongPlaytest.probeHook = FX9Probe::month;
        LongPlaytest.main(new String[0]);
    }

    static DistrictPlan make(DistrictPlan.Input in, boolean on) {
        DistrictPlan.streetsOnBought = on;
        try {
            return DistrictPlan.make(in);
        } finally {
            DistrictPlan.streetsOnBought = true;
        }
    }

    static long[] boxes(DistrictPlan p) {
        long[] k = new long[p.buildings];
        for (int i = 0; i < p.buildings; i++) k[i] = ((long) p.btype[i] << 40) | ((long) p.bx[i] << 20) | p.by[i];
        Arrays.sort(k);
        return k;
    }

    /** The watch's rule: per type, the boxes gone less the fall in that type's count. */
    static int moved(long[] a, long[] b, int nt) {
        int[] ca = new int[nt], cb = new int[nt], gone = new int[nt];
        for (long k : a) ca[(int) (k >>> 40)]++;
        for (long k : b) cb[(int) (k >>> 40)]++;
        for (long k : a) if (Arrays.binarySearch(b, k) < 0) gone[(int) (k >>> 40)]++;
        int n = 0;
        for (int t = 0; t < nt; t++) n += Math.max(0, gone[t] - Math.max(0, ca[t] - cb[t]));
        return n;
    }

    static void month(Game g) {
        int month = g.getMonth();
        if (month < FROM || month > TO) return;
        try {
            CityMap m = g.getCityMap();
            int nt = m.types().length;
            for (CityMap.District d : m.districts()) {
                DistrictPlan.Input in = m.planInput(d);
                DistrictPlan p = make(in, true), q = make(m.planInput(d), false);
                long[] b = boxes(p), bo = boxes(q);
                int key = d.index;
                if (prevOn.containsKey(key)) {
                    int mv = moved(prevOn.get(key), b, nt), mvOff = moved(prevOff.get(key), bo, nt);
                    if (mv > 0 || mvOff > 0 || p.wallTries > 0 || q.wallTries > 0)
                        log.printf("m%d district (%d,%d): %d boxes on (%d moved, walls %d tries %d plots), %d off (%d moved, walls %d tries %d plots); bought-ground layings %d, street plots %d; last log record m%d type %d change %d%n", month, d.dx, d.dy,
                                b.length, mv, p.wallTries, p.wallClear, bo.length, mvOff, q.wallTries, q.wallClear, p.boughtCells, p.boughtStreets,
                                in.logMonth[in.logMonth.length - 1], in.logType[in.logType.length - 1], in.logChange[in.logChange.length - 1]);
                    if (mv > 0) diagnose(m, d, prevIn.get(key), prevOn.get(key), nt, month);
                }
                prevIn.put(key, in);
                prevOn.put(key, b);
                prevOff.put(key, bo);
            }
        } catch (Throwable t) {
            t.printStackTrace(log);
        }
    }

    static int startMonth(DistrictPlan.Input in) {
        int n = in.logMonth.length, fb = Math.max(0, Math.min(in.firstBatch, n));
        return fb > 0 ? in.logMonth[fb - 1] : (n > 0 ? in.logMonth[0] - 1 : -1);
    }

    static void diagnose(CityMap m, CityMap.District d, DistrictPlan.Input prev, long[] prevBoxes, int nt, int month) {
        DistrictPlan.Input cur = m.planInput(d);
        int ownP = 0, ownC = 0, both = 0, dateDiff = 0, newOwned = 0, newMin = Integer.MAX_VALUE, newMax = Integer.MIN_VALUE, lost = 0;
        StringBuilder sample = new StringBuilder();
        for (int p = 0; p < DistrictPlan.AREA; p++) {
            if (prev.owned[p]) ownP++;
            if (cur.owned[p]) ownC++;
            if (prev.owned[p] && cur.owned[p]) {
                both++;
                int a = prev.boughtAt == null ? 0 : prev.boughtAt[p], c = cur.boughtAt == null ? 0 : cur.boughtAt[p];
                if (a != c) {
                    dateDiff++;
                    if (sample.length() < 300) sample.append(' ').append(p % DistrictPlan.FRAME).append(',').append(p / DistrictPlan.FRAME).append(':').append(a).append("->").append(c);
                }
            } else if (cur.owned[p]) {
                newOwned++;
                int c = cur.boughtAt == null ? 0 : cur.boughtAt[p];
                newMin = Math.min(newMin, c);
                newMax = Math.max(newMax, c);
            } else if (prev.owned[p]) {
                lost++;
            }
        }
        log.printf("   inputs: firstBatch %d -> %d, log %d -> %d, start month %d -> %d, carry %s -> %s, fixedLog %d -> %d, owned %d -> %d (both %d, new %d dated %d..%d, lost %d), boughtAt %s -> %s, plots owned in both whose date differs %d:%s%n",
                prev.firstBatch, cur.firstBatch, prev.logMonth.length, cur.logMonth.length, startMonth(prev), startMonth(cur),
                prev.carry == null ? "null" : String.valueOf(prev.carry.length), cur.carry == null ? "null" : String.valueOf(cur.carry.length),
                prev.fixedLog == null ? 0 : prev.fixedLog.length, cur.fixedLog == null ? 0 : cur.fixedLog.length, ownP, ownC, both, newOwned, newMin, newMax, lost,
                prev.boughtAt == null ? "null" : "dated", cur.boughtAt == null ? "null" : "dated", dateDiff, sample);
        // The log's first difference: the records both have in common, record by record.
        int n = Math.min(prev.logMonth.length, cur.logMonth.length), first = -1;
        for (int i = 0; i < n && first < 0; i++) if (prev.logMonth[i] != cur.logMonth[i] || prev.logType[i] != cur.logType[i] || prev.logChange[i] != cur.logChange[i]) first = i;
        log.printf("   log: the first record that differs %d (of %d in common)%s; the months of the records cur adds: %s%n", first, n,
                first < 0 ? "" : String.format(" (m%d vs m%d)", prev.logMonth[first], cur.logMonth[first]),
                cur.logMonth.length > n ? cur.logMonth[n] + ".." + cur.logMonth[cur.logMonth.length - 1] : "none");
        // Swaps: one part of cur from prev at a time, the moves of each against prev's plan.
        String[] parts = { "ground (owned, boughtAt)", "dates only (boughtAt where owned in both)", "the runs (fixed, fixedLog, fixedLogMonth)", "the log and counts", "streets off both months" };
        for (int s = 0; s < parts.length; s++) {
            DistrictPlan.Input c = m.planInput(d);
            boolean on = true;
            if (s == 0) {
                System.arraycopy(prev.owned, 0, c.owned, 0, DistrictPlan.AREA);
                c.boughtAt = prev.boughtAt == null ? null : prev.boughtAt.clone();
            } else if (s == 1) {
                if (c.boughtAt != null && prev.boughtAt != null) for (int p = 0; p < DistrictPlan.AREA; p++) if (prev.owned[p] && c.owned[p]) c.boughtAt[p] = prev.boughtAt[p];
            } else if (s == 2) {
                System.arraycopy(prev.fixed, 0, c.fixed, 0, DistrictPlan.AREA);
                c.fixedLog = prev.fixedLog == null ? null : prev.fixedLog.clone();
                c.fixedLogMonth = prev.fixedLogMonth == null ? null : prev.fixedLogMonth.clone();
            } else if (s == 3) {
                c.logMonth = prev.logMonth.clone();
                c.logType = prev.logType.clone();
                c.logChange = prev.logChange.clone();
                c.firstBatch = prev.firstBatch;
                c.counts = prev.counts.clone();
            } else {
                on = false;
            }
            long[] b = boxes(make(c, on));
            long[] base = on ? prevBoxes : boxes(make(prev, false));
            log.printf("   swap %s: %d moved%n", parts[s], moved(base, b, nt));
        }
    }
}
