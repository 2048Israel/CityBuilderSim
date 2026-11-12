package ham.citybuildersim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * The resource sites' plot classes as they change each month, kept sparsely by district for the plans' historical ground.
 *
 * WHY (DM2): a plan replayed its old buildings around today's mined sites, so a mine first worked at month 100 barred its
 * ground before month 100 too. Counts alone cannot date that ground. The map captures the site claims before their first
 * mutation, compares the finished month's masks, and keeps only actual plot transitions; a canonical city's unchanged sites
 * need no records. Nothing here plans a district or depends on when the player first looks at it.
 */
final class MapSiteHistory {

    /** Reads a district's own 256 by 256 mask; an override replaces its site runs, and a null override means absent before. */
    @FunctionalInterface
    interface MaskReader {
        byte[] mask(CityMap.District district, Map<CityMap.District, int[][]> overrides);
    }

    private final Map<CityMap.District, Journal> journals = new HashMap<>();
    private final Map<CityMap.District, int[][]> before = new LinkedHashMap<>();
    private boolean recording;

    /** Starts the month's source capture; canonical construction outside this interval has no history to record. */
    void begin() {
        if (recording) throw new IllegalStateException("site history already recording");
        before.clear();
        recording = true;
    }

    /** Whether site claims are being captured for a reconciled month. */
    boolean isRecording() { return recording; }

    /** A district's claims before its first mutation this month, including mutations that are later undone. */
    void changed(CityMap.District d) {
        if (!recording || before.containsKey(d)) return;
        int[][] runs = d.siteRuns.clone();
        for (int k = 0; k < runs.length; k++) if (runs[k] != null) runs[k] = runs[k].clone();
        before.put(d, runs);
    }

    /** A new district had no mask before this month; subsequent claim writes keep that fact. */
    void added(CityMap.District d) {
        if (recording) before.put(d, null);
    }

    /** Compares finished masks only near changed claims, recording the month's net transitions in plot order. */
    void end(BiFunction<Integer, Integer, CityMap.District> lookup, MaskReader reader, int month) {
        if (!recording) return;
        try {
            if (month < 0) throw new IllegalArgumentException("negative site history month");
            Set<CityMap.District> touched = new LinkedHashSet<>();
            for (CityMap.District d : before.keySet()) {
                for (int y = -1; y <= 1; y++) {
                    for (int x = -1; x <= 1; x++) {
                        CityMap.District near = neighbour(d, x, y, lookup);
                        if (near != null) touched.add(near);
                    }
                }
            }
            for (CityMap.District d : touched) {
                byte[] was = reader.mask(d, before), now = reader.mask(d, Map.of());
                checkMask(was);
                checkMask(now);
                if (Arrays.equals(was, now)) continue;
                Journal j = journals.computeIfAbsent(d, key -> new Journal());
                j.record(month, was, now);
                if (j.n == 0) journals.remove(d);
            }
        } finally {
            before.clear();
            recording = false;
        }
    }

    /** Rejects a renderer's malformed mask before it becomes saved history. */
    private static void checkMask(byte[] mask) {
        if (mask == null || mask.length != CityMap.DISTRICT * CityMap.DISTRICT) {
            throw new IllegalArgumentException("site history mask size");
        }
        for (byte kind : mask) if (!kind(kind)) throw new IllegalArgumentException("site history mask kind");
    }

    /** The history's stamp, zero for a district without any transitions. */
    long hash(CityMap.District d) {
        Journal j = journals.get(d);
        return j == null ? 0 : j.hash;
    }

    /** The own district and its east/south seams, transformed to the plan's 257 by 257 frame, in month and plot order. */
    void frameInto(CityMap.District d, DistrictPlan.Input in,
                   BiFunction<Integer, Integer, CityMap.District> lookup) {
        Journal[] from = { journals.get(d), journals.get(neighbour(d, 1, 0, lookup)),
                journals.get(neighbour(d, 0, 1, lookup)), journals.get(neighbour(d, 1, 1, lookup)) };
        int n = 0;
        for (int part = 0; part < from.length; part++) {
            Journal j = from[part];
            if (j == null) continue;
            for (int k = 0; k < j.n; k++) if (framePlot(j.rec[k] >>> 4, part) >= 0) n++;
        }
        if (n == 0) {
            in.siteLogMonth = in.siteLog = null;
            return;
        }
        long[] records = new long[n];
        n = 0;
        for (int part = 0; part < from.length; part++) {
            Journal j = from[part];
            if (j == null) continue;
            for (int k = 0; k < j.n; k++) {
                int p = framePlot(j.rec[k] >>> 4, part);
                if (p < 0) continue;
                records[n++] = ((long) j.month[k] << 32) | ((p << 4) | (j.rec[k] & 15));
            }
        }
        Arrays.sort(records);
        in.siteLogMonth = new int[n];
        in.siteLog = new int[n];
        for (int k = 0; k < n; k++) {
            in.siteLogMonth[k] = (int) (records[k] >>> 32);
            in.siteLog[k] = (int) records[k];
        }
    }

    /** Own plots, east's first column, south's first row and the south-east's corner alone belong to this frame. */
    private static int framePlot(int plot, int part) {
        int side = CityMap.DISTRICT, x = plot % side, y = plot / side;
        return switch (part) {
            case 0 -> y * DistrictPlan.FRAME + x;
            case 1 -> x == 0 ? y * DistrictPlan.FRAME + side : -1;
            case 2 -> y == 0 ? side * DistrictPlan.FRAME + x : -1;
            default -> plot == 0 ? side * DistrictPlan.FRAME + side : -1;
        };
    }

    /** A neighbour without wrapping the district coordinates at the integer limits. */
    private static CityMap.District neighbour(CityMap.District d, int x, int y,
                                              BiFunction<Integer, Integer, CityMap.District> lookup) {
        long dx = (long) d.dx + x, dy = (long) d.dy + y;
        return dx == (int) dx && dy == (int) dy ? lookup.apply((int) dx, (int) dy) : null;
    }

    /** The sidecar's count, month deltas and packed plot/class transitions for one district. */
    void writeDistrict(DataOutputStream out, CityMap.District d) throws IOException {
        Journal j = journals.get(d);
        CityMap.writeVarint(out, j == null ? 0 : j.n);
        if (j == null) return;
        int previous = 0;
        for (int k = 0; k < j.n; k++) {
            CityMap.writeVarint(out, j.month[k] - previous);
            CityMap.writeVarint(out, j.rec[k]);
            previous = j.month[k];
        }
    }

    /** Reads bounded, ordered transitions; a plot's next before-class must agree with its preceding after-class. */
    void readDistrict(DataInputStream in, CityMap.District d, int savedMonth, int remainingBytes) throws IOException {
        int count = CityMap.readVarint(in);
        if (savedMonth < 0 || remainingBytes < 1 || count < 0 || count > (remainingBytes - 1) / 2) {
            throw new IOException("site history record count");
        }
        if (count == 0) {
            journals.remove(d);
            return;
        }
        Journal j = new Journal(count);
        byte[] last = new byte[CityMap.DISTRICT * CityMap.DISTRICT];
        Arrays.fill(last, (byte) -1);
        int month = 0, previousPlot = -1;
        for (int k = 0; k < count; k++) {
            int step = CityMap.readVarint(in), rec = CityMap.readVarint(in), plot = rec >>> 4;
            int was = rec >>> 2 & 3, now = rec & 3;
            if (step < 0 || step > savedMonth - month || rec < 0 || plot >= last.length
                    || !kind(was) || !kind(now) || was == now
                    || (step == 0 && plot <= previousPlot) || (last[plot] >= 0 && last[plot] != was)) {
                throw new IOException("site history transition");
            }
            month += step;
            previousPlot = plot;
            last[plot] = (byte) now;
            j.append(month, rec);
        }
        journals.put(d, j);
    }

    /** Whether two districts have exactly the same source history, independently of array capacity. */
    boolean same(CityMap.District a, MapSiteHistory other, CityMap.District b) {
        Journal left = journals.get(a), right = other.journals.get(b);
        int n = left == null ? 0 : left.n;
        if (n != (right == null ? 0 : right.n)) return false;
        return n == 0 || Arrays.equals(left.month, 0, n, right.month, 0, n)
                && Arrays.equals(left.rec, 0, n, right.rec, 0, n);
    }

    /** A loaded journal must end on the actual claims; only districts with transitions need a mask read. */
    boolean matchesCurrent(MaskReader reader) {
        if (journals.isEmpty()) return true;
        boolean[] checked = new boolean[CityMap.DISTRICT * CityMap.DISTRICT];
        for (Map.Entry<CityMap.District, Journal> entry : journals.entrySet()) {
            byte[] now = reader.mask(entry.getKey(), Map.of());
            checkMask(now);
            Arrays.fill(checked, false);
            Journal j = entry.getValue();
            for (int k = j.n - 1; k >= 0; k--) {
                int plot = j.rec[k] >>> 4;
                if (checked[plot]) continue;
                if (now[plot] != (j.rec[k] & 3)) return false;
                checked[plot] = true;
            }
        }
        return true;
    }

    /** The only source classes a transition may name. */
    private static boolean kind(int kind) {
        return kind == 0 || kind == DistrictPlan.SITE_FIELD || kind == DistrictPlan.SITE_MINED;
    }

    /** One district's ordered plot transitions, with capacity kept between months. */
    private static final class Journal {
        int[] month, rec;
        int n;
        long hash;

        Journal() { this(16); }

        Journal(int capacity) {
            month = new int[capacity];
            rec = new int[capacity];
        }

        void append(int at, int record) {
            if (n == month.length) {
                int capacity = Math.max(16, n * 2);
                month = Arrays.copyOf(month, capacity);
                rec = Arrays.copyOf(rec, capacity);
            }
            month[n] = at;
            rec[n++] = record;
            hash = World.mix(hash ^ ((long) at << 32) ^ record);
        }

        /** A repeated reconciliation in the same month merges into that month's original before-classes. */
        void record(int at, byte[] was, byte[] now) {
            if (n > 0 && month[n - 1] > at) throw new IllegalArgumentException("site history month fell");
            int from = n;
            while (from > 0 && month[from - 1] == at) from--;
            int[] prior = from == n ? null : Arrays.copyOfRange(rec, from, n);
            if (prior != null) {
                n = from;
                hash = 0;
                for (int k = 0; k < n; k++) hash = World.mix(hash ^ ((long) month[k] << 32) ^ rec[k]);
            }
            int old = 0;
            for (int p = 0; p < was.length; p++) {
                int first = was[p], after = now[p];
                if (prior != null && old < prior.length && prior[old] >>> 4 == p) {
                    int previous = prior[old++];
                    if ((previous & 3) != first) throw new IllegalStateException("site history before-class changed");
                    first = previous >>> 2 & 3;
                }
                if (first != after) append(at, p << 4 | first << 2 | after);
            }
        }
    }
}
