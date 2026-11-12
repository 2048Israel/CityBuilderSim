package ham.citybuildersim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
    /** Implicit masks expand only after the reader has restored all of the map's claim state. */
    private final Map<CityMap.District, MinedBirth> pendingBirths = new HashMap<>();
    private final Map<CityMap.District, int[][]> before = new LinkedHashMap<>();
    private boolean recording;

    private record MinedBirth(int month, int count, long hash) { }

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

    /** No history, explicit rectangles, or one exact FIELD-to-MINED birth reconstructed from the saved current claims. */
    void writeDistrict(DataOutputStream out, CityMap.District d, MaskReader reader) throws IOException {
        Journal j = journals.get(d);
        if (j == null) { CityMap.writeVarint(out, 0); return; }
        if (currentMinedBirth(j, d, reader)) {
            CityMap.writeVarint(out, 2);
            CityMap.writeVarint(out, j.month[0]);
            CityMap.writeVarint(out, j.n);
            out.writeLong(j.hash);
            return;
        }
        CityMap.writeVarint(out, 1);
        List<SiteRectangle> rectangles = rectangles(j);
        CityMap.writeVarint(out, rectangles.size());
        int previous = 0, previousPlot = 0;
        for (SiteRectangle r : rectangles) {
            CityMap.writeVarint(out, r.month - previous);
            if (r.month != previous) previousPlot = 0;
            CityMap.writeVarint(out, (r.plot - previousPlot) << 4 | r.kinds);
            CityMap.writeVarint(out, r.width - 1);
            CityMap.writeVarint(out, r.height - 1);
            previous = r.month;
            previousPlot = r.plot;
        }
    }

    /** A compact predictor is used only after an exact plot-for-plot comparison, never from a count or hash alone. */
    private static boolean currentMinedBirth(Journal j, CityMap.District d, MaskReader reader) {
        if (j.n == 0 || j.month[0] != j.month[j.n - 1]) return false;
        int transition = DistrictPlan.SITE_FIELD << 2 | DistrictPlan.SITE_MINED;
        for (int k = 0; k < j.n; k++) if ((j.rec[k] & 15) != transition) return false;
        byte[] mask = reader.mask(d, Map.of());
        checkMask(mask);
        int k = 0;
        for (int p = 0; p < mask.length; p++) if (mask[p] == DistrictPlan.SITE_MINED) {
            if (k >= j.n || j.rec[k++] != (p << 4 | transition)) return false;
        }
        return k == j.n;
    }

    /** Equal row spans on consecutive rows become one rectangle, without changing any expanded plot transition. */
    private static List<SiteRectangle> rectangles(Journal j) {
        if (j == null) return List.of();
        List<SiteRectangle> out = new ArrayList<>();
        Map<Integer, SiteRectangle> active = new HashMap<>();
        int month = -1;
        for (int k = 0; k < j.n; ) {
            if (j.month[k] != month) { active.clear(); month = j.month[k]; }
            int end = spanEnd(j, k), plot = j.rec[k] >>> 4, width = end - k, kinds = j.rec[k] & 15;
            int key = (plot % CityMap.DISTRICT << 12) | ((width - 1) << 4) | kinds;
            SiteRectangle r = active.get(key);
            if (r != null && r.plot / CityMap.DISTRICT + r.height == plot / CityMap.DISTRICT) r.height++;
            else {
                r = new SiteRectangle(month, plot, width, kinds);
                active.put(key, r);
                out.add(r);
            }
            k = end;
        }
        out.sort(Comparator.comparingInt((SiteRectangle r) -> r.month).thenComparingInt(r -> r.plot));
        return out;
    }

    private static final class SiteRectangle {
        final int month, plot, width, kinds;
        int height = 1;

        SiteRectangle(int month, int plot, int width, int kinds) {
            this.month = month; this.plot = plot; this.width = width; this.kinds = kinds;
        }
    }

    /** Consecutive plots of the same transition and month, stopped at the row's edge. */
    private static int spanEnd(Journal j, int from) {
        int end = from + 1, rowLeft = CityMap.DISTRICT - (j.rec[from] >>> 4) % CityMap.DISTRICT;
        while (end < j.n && end - from < rowLeft && j.month[end] == j.month[from]
                && j.rec[end] == j.rec[from] + ((end - from) << 4)) end++;
        return end;
    }

    /** Reads bounded, ordered transitions; a plot's next before-class must agree with its preceding after-class. */
    void readDistrict(DataInputStream in, CityMap.District d, int savedMonth, int remainingBytes) throws IOException {
        int mode = CityMap.readVarint(in);
        if (mode == 0) { journals.remove(d); pendingBirths.remove(d); return; }
        if (mode == 2) {
            int at = CityMap.readVarint(in), expected = CityMap.readVarint(in);
            long expectedHash = in.readLong();
            if (at < 0 || at > savedMonth || expected < 1 || expected > CityMap.DISTRICT * CityMap.DISTRICT) {
                throw new IOException("site history mined birth");
            }
            journals.remove(d);
            pendingBirths.put(d, new MinedBirth(at, expected, expectedHash));
            return;
        }
        if (mode != 1) throw new IOException("site history encoding");
        int count = CityMap.readVarint(in);
        if (savedMonth < 0 || remainingBytes < 2 || count < 1 || count > (remainingBytes - 2) / 4) {
            throw new IOException("site history record count");
        }
        Journal j = new Journal(Math.min(count, CityMap.DISTRICT));
        byte[] last = new byte[CityMap.DISTRICT * CityMap.DISTRICT];
        Arrays.fill(last, (byte) -1);
        int month = 0, monthFrom = 0, previousPlot = 0;
        long prefixHash = 0;
        for (int k = 0; k < count; k++) {
            int step = CityMap.readVarint(in), code = CityMap.readVarint(in);
            int widthMinusOne = CityMap.readVarint(in), heightMinusOne = CityMap.readVarint(in);
            long plotLong = (long) (step == 0 ? previousPlot : 0) + (code >>> 4);
            int plot = (int) plotLong, was = code >>> 2 & 3, now = code & 3;
            if (step < 0 || step > savedMonth - month || code < 0 || plotLong >= last.length
                    || !kind(was) || !kind(now) || was == now
                    || widthMinusOne < 0 || widthMinusOne >= CityMap.DISTRICT - plot % CityMap.DISTRICT
                    || heightMinusOne < 0 || heightMinusOne >= CityMap.DISTRICT - plot / CityMap.DISTRICT
                    || (k > 0 && step == 0 && plot <= previousPlot)) {
                throw new IOException("site history transition");
            }
            if (k > 0 && step > 0) {
                j.finishMonth(monthFrom, last, prefixHash);
                monthFrom = j.n;
                prefixHash = j.hash;
            }
            month += step;
            previousPlot = plot;
            int width = widthMinusOne + 1, height = heightMinusOne + 1;
            long plots = (long) width * height;
            if ((long) j.n + plots > Integer.MAX_VALUE || j.n - (long) monthFrom + plots > last.length) {
                throw new IOException("site history expanded count");
            }
            for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
                int p = plot + y * CityMap.DISTRICT + x;
                j.append(month, p << 4 | was << 2 | now);
            }
        }
        j.finishMonth(monthFrom, last, prefixHash);
        journals.put(d, j);
        pendingBirths.remove(d);
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
        if (journals.isEmpty() && pendingBirths.isEmpty()) return true;
        for (Map.Entry<CityMap.District, MinedBirth> entry : pendingBirths.entrySet()) {
            byte[] mask = reader.mask(entry.getKey(), Map.of());
            checkMask(mask);
            MinedBirth birth = entry.getValue();
            Journal j = new Journal(Math.min(birth.count, CityMap.DISTRICT));
            for (int p = 0; p < mask.length; p++) if (mask[p] == DistrictPlan.SITE_MINED) {
                if (j.n >= birth.count) return false;
                j.append(birth.month, p << 4 | DistrictPlan.SITE_FIELD << 2 | DistrictPlan.SITE_MINED);
            }
            if (j.n != birth.count || j.hash != birth.hash) return false;
            journals.put(entry.getKey(), j);
        }
        boolean[] checked = new boolean[CityMap.DISTRICT * CityMap.DISTRICT];
        for (Map.Entry<CityMap.District, Journal> entry : journals.entrySet()) {
            if (pendingBirths.containsKey(entry.getKey())) continue;
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
        pendingBirths.clear();
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

        /** Rectangle expansions interleave across rows; restore plot order and validate each transition exactly once. */
        void finishMonth(int from, byte[] last, long prefixHash) throws IOException {
            Arrays.sort(rec, from, n);
            hash = prefixHash;
            int previous = -1;
            for (int k = from; k < n; k++) {
                int plot = rec[k] >>> 4, was = rec[k] >>> 2 & 3, now = rec[k] & 3;
                if (plot <= previous || last[plot] >= 0 && last[plot] != was) throw new IOException("site history before-class");
                previous = plot;
                last[plot] = (byte) now;
                hash = World.mix(hash ^ ((long) month[k] << 32) ^ rec[k]);
            }
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
