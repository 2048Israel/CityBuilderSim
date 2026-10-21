package ham.citybuildersim;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Scratch, not in the build: what CL1's prototype CityMap calls at the end of
 * each month's reconcile() (patch_citymap.py), so a whole run of the playtest
 * can be measured both ways - today's plans (packed from the counts) and the
 * arrival log's (-Dcl1.arrivals=true). Properties: cl1.hook (the folder it
 * writes to), cl1.label, cl1.every (a checkpoint every so many months: every
 * district planned, the ring, the packing, the log's size), cl1.windows
 * (months from which twelve months in a row are planned and the boxes that
 * moved counted), cl1.renders (months a window round the founding site is
 * painted), cl1.window ("w h px"). Nothing in the model reads the map, so the
 * run's figures are the playtest's own.
 */
public final class CL1Hook {
    static final File DIR = new File(System.getProperty("cl1.hook", "."));
    static final String LABEL = System.getProperty("cl1.label", Boolean.getBoolean("cl1.arrivals") ? "arrivals" : "today");
    static final int EVERY = Integer.getInteger("cl1.every", 250);
    static final int[] WINDOWS = ints(System.getProperty("cl1.windows", ""));
    static final int[] RENDERS = ints(System.getProperty("cl1.renders", ""));
    static final int[] WINDOW = ints(System.getProperty("cl1.window", "768 768 1"));
    static final int WINDOW_MONTHS = 12;

    static PrintWriter log;
    static LayoutProbe.Plans last;
    static LayoutProbe.Snapshot lastSnap;
    static int lastSnapMonth = -1;
    static long movedSum, boxesSum, monthsCounted;
    static final java.util.Map<Long, int[]> prev = new java.util.HashMap<>();
    static final java.io.ByteArrayOutputStream recs = new java.io.ByteArrayOutputStream();
    static long nRecs;
    static final java.util.Set<Long> lastCarried = new java.util.HashSet<>();

    static int[] ints(String s) {
        s = s.trim();
        if (s.isEmpty()) return new int[0];
        return Arrays.stream(s.split("[ ,]+")).mapToInt(Integer::parseInt).toArray();
    }

    static boolean in(int[] a, int v) { for (int x : a) if (x == v) return true; return false; }

    static boolean inWindow(int month) {
        for (int w : WINDOWS) if (month >= w && month <= w + WINDOW_MONTHS) return true;
        return false;
    }

    public static void month(CityMap m, int month) {
        try {
            if (log == null) {
                DIR.mkdirs();
                log = new PrintWriter(new FileWriter(new File(DIR, "hook-" + LABEL + ".txt")), true);
            }
            // The log as the sidecar would keep it: each month, each district-type that changed, {district, type, change}.
            int n = 0;
            java.io.ByteArrayOutputStream mo = new java.io.ByteArrayOutputStream();
            for (CityMap.District d : m.districts()) {
                int[] p = prev.computeIfAbsent(LayoutProbe.key(d), k -> new int[d.counts.length]);
                for (int t = 0; t < d.counts.length; t++) {
                    int dv = d.counts[t] - p[t];
                    if (dv == 0) continue;
                    varint(mo, d.index);
                    varint(mo, t);
                    varint(mo, (dv << 1) ^ (dv >> 31));
                    p[t] = d.counts[t];
                    n++;
                }
            }
            varint(recs, n);
            recs.write(mo.toByteArray(), 0, mo.size());
            nRecs += n;
            boolean check = month % EVERY == 0, window = inWindow(month), render = in(RENDERS, month);
            if (!check && !window && !render) { lastSnap = null; return; }
            LayoutProbe.Plans p = LayoutProbe.planAll(m, last);
            LayoutProbe.Snapshot s = LayoutProbe.snapshot(m, p);
            int[] packed = m.packedCounts();
            if (window && lastSnap != null && lastSnapMonth == month - 1) {
                int[] mv = LayoutProbe.moved(lastSnap, s);
                // ...and the moves in districts no carry reached, this month or last (CL1: what the log alone leaves).
                java.util.Set<Long> carried = new java.util.HashSet<>();
                for (CityMap.District d : m.districts()) {
                    int[] c = m.cl1CarryIn(d);
                    boolean any = false;
                    if (c != null) for (int v : c) if (v != 0) any = true;
                    if (any || lastCarried.contains(LayoutProbe.key(d))) carried.add(LayoutProbe.key(d));
                }
                int quiet = 0;
                for (java.util.Map.Entry<Long, long[]> en : s.byDistrict.entrySet()) {
                    if (carried.contains(en.getKey())) continue;
                    long[] a = en.getValue(), b = lastSnap.byDistrict.getOrDefault(en.getKey(), new long[0]);
                    int i = 0, j = 0, same = 0;
                    while (i < a.length && j < b.length) { if (a[i] == b[j]) { same++; i++; j++; } else if (a[i] < b[j]) i++; else j++; }
                    quiet += a.length - same;
                }
                log.printf("m%d no carry: %d moved in %d districts no carry reached (of %d)%n", month, quiet, s.byDistrict.size() - carried.size(), s.byDistrict.size());
                movedSum += mv[0];
                boxesSum += s.total;
                monthsCounted++;
                log.printf("m%d moved %d of %d boxes (%.2f%%) in %d districts (%.1f%% of theirs); %d plans moved, %.1f ms%n",
                        month, mv[0], s.total, 100.0 * mv[0] / Math.max(1, s.total), mv[1], 100.0 * mv[0] / Math.max(1, mv[2]), p.changed, p.changedMs);
            }
            if (check || render) {
                long entries = 0;
                java.io.ByteArrayOutputStream raw = new java.io.ByteArrayOutputStream();
                for (CityMap.District d : m.districts()) {
                    entries += d.nArr;
                    // The log as runs of one event: a varint of the event, a varint of the run.
                    for (int k = 0; k < d.nArr; ) {
                        int e = d.arr[k], r = 1;
                        while (k + r < d.nArr && d.arr[k + r] == e) r++;
                        varint(raw, (e << 1) ^ (e >> 31));
                        varint(raw, r);
                        k += r;
                    }
                }
                java.io.ByteArrayOutputStream def = new java.io.ByteArrayOutputStream();
                try (java.util.zip.DeflaterOutputStream z = new java.util.zip.DeflaterOutputStream(def)) { z.write(raw.toByteArray()); }
                java.io.ByteArrayOutputStream rd = new java.io.ByteArrayOutputStream();
                try (java.util.zip.DeflaterOutputStream z = new java.util.zip.DeflaterOutputStream(rd)) { z.write(recs.toByteArray()); }
                log.printf("m%d records: %d district-type changes, %d bytes, %d deflated%n", month, nRecs, recs.size(), rd.size());
                double[] e = p.each.stream().mapToDouble(Double::doubleValue).sorted().toArray();
                log.printf("m%d %s: %d districts, %d boxes, packed at the edge %d; plans %.0f ms all (median %.2f, slowest %.2f); log %d events, %d bytes as runs, %d deflated; sidecar %d bytes%n",
                        month, LABEL, m.districts().size(), s.total, packed == null ? -1 : packed[0], p.total,
                        e.length == 0 ? 0 : e[e.length / 2], p.max, entries, raw.size(), def.size(), m.writeSidecar(month).length);
                log.printf("m%d ring: %s%n", month, s.ring());
            }
            if (render) {
                CityMap.District d0 = m.districts().get(0);
                DistrictPlan.Input in0 = m.planInputHere(d0);
                long sx = Math.round(in0.hubX + in0.x0), sy = Math.round(in0.hubY + in0.y0);
                int w = WINDOW[0], h = WINDOW[1], px = WINDOW[2];
                RenderCL1.render(m, sx - w / 2, sy - h / 2, w, h, px, new File(DIR, LABEL + "-m" + month + ".png"),
                        String.format("%s, month %d: %d x %d plots (%.1f km a side) round the founding site, %,d buildings drawn", LABEL, month, w, h, w * 0.03, s.total));
            }
            last = p;
            lastSnap = s;
            lastCarried.clear();
            for (CityMap.District d : m.districts()) {
                int[] c = m.cl1CarryIn(d);
                if (c != null) for (int v : c) if (v != 0) { lastCarried.add(LayoutProbe.key(d)); break; }
            }
            lastSnapMonth = month;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void varint(java.io.ByteArrayOutputStream o, int v) {
        while ((v & ~0x7F) != 0) { o.write((v & 0x7F) | 0x80); v >>>= 7; }
        o.write(v);
    }
}
