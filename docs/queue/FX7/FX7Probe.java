package ham.citybuildersim;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * Scratch, not in the build (batch FX7): the default playtest with district (0,0)'s own plan drawn every month from
 * probe.from (1001) on, the moves between months counted, and at each move the plan's inputs differenced and (with
 * -Dprobe.swap=true) swapped part by part from the month before - which part's swap leaves no move is the cause; the
 * boxes under each run newly laid, the m1001 works and the runs later under them, and the plan's time. It needs a
 * copy of LongPlaytest.java with a hook: a field `static java.util.function.Consumer<Game> probeHook;` after
 * `static boolean layoutOn;`, and `if (probeHook != null) probeHook.accept(g);` after `if (layoutOn) layoutMonth(g);`,
 * compiled with this file ahead of target/classes:
 *   java -cp "<hooked>;target/classes;<gson.jar>" ham.citybuildersim.FX7Probe <out.txt>
 */
public class FX7Probe {
    static PrintStream log;
    static int from = Integer.getInteger("probe.from", 1001);
    static int every = Integer.getInteger("probe.every", 1);
    static DistrictPlan.Input prevIn;
    static long[] prevBoxes;
    static int[] prevCount;
    static int prevMonth;
    static long[] oldBoxes;
    static int swaps; static long makeNs, makeMax; static int makeN;
    static DistrictPlan oldPlan, prevPlan;
    static byte[] fixed0;
    static int[] firstFixed; static byte[] firstKind;

    /** The boxes of plan p overlapping a plot set by `on`, by type name. */
    static String overlapping(Game g, DistrictPlan p, boolean[] on, int[] count) {
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (int i = 0; i < p.buildings; i++) {
            boolean hit = false;
            for (int y = p.by[i]; y < p.by[i] + p.bh[i] && !hit; y++)
                for (int x = p.bx[i]; x < p.bx[i] + p.bw[i] && !hit; x++)
                    if (x >= 0 && y >= 0 && x < DistrictPlan.FRAME && y < DistrictPlan.FRAME && on[y * DistrictPlan.FRAME + x]) hit = true;
            if (hit) { n++; sb.append(' ').append(LongPlaytest.nameOf(g, p.btype[i])).append('@').append(p.bx[i]).append(',').append(p.by[i]); }
        }
        count[0] = n;
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        log = new PrintStream(new java.io.FileOutputStream(args[0]), true);
        LongPlaytest.probeHook = FX7Probe::month;
        LongPlaytest.main(new String[0]);
    }

    static CityMap.District d00(CityMap m) {
        for (CityMap.District d : m.districts()) if (d.dx == 0 && d.dy == 0) return d;
        return null;
    }

    static long[] boxes(DistrictPlan p) {
        long[] k = new long[p.buildings];
        for (int i = 0; i < p.buildings; i++) k[i] = ((long) p.btype[i] << 40) | ((long) p.bx[i] << 20) | p.by[i];
        Arrays.sort(k);
        return k;
    }

    static int[] byType(long[] b, int nt) {
        int[] c = new int[nt];
        for (long k : b) c[(int) (k >>> 40)]++;
        return c;
    }

    /** Moves from a to b, by type: gone - max(0, count drop). */
    static int[] moves(long[] a, long[] b, int nt) {
        int[] gone = new int[nt];
        for (long k : a) if (Arrays.binarySearch(b, k) < 0) gone[(int) (k >>> 40)]++;
        int[] ca = byType(a, nt), cb = byType(b, nt);
        int[] mv = new int[nt];
        for (int t = 0; t < nt; t++) mv[t] = Math.max(0, gone[t] - Math.max(0, ca[t] - cb[t]));
        return mv;
    }

    static int sum(int[] a) { int s = 0; for (int x : a) s += x; return s; }

    static void month(Game g) {
        int month = g.getMonth();
        if (month < from || (month - from) % every != 0) return;
        try {
            CityMap m = g.getCityMap();
            CityMap.District d = d00(m);
            if (d == null) return;
            DistrictPlan.Input in = m.planInput(d);
            long mk0 = System.nanoTime(); DistrictPlan p = DistrictPlan.make(in); long mk = System.nanoTime() - mk0; makeNs += mk; makeN++; makeMax = Math.max(makeMax, mk); if (month == 4000) log.printf("make: mean %.1f ms, max %.1f ms over %d months; frame records %d%n", makeNs / 1e6 / makeN, makeMax / 1e6, makeN, in.fixedLog == null ? 0 : in.fixedLog.length);
            long[] b = boxes(p);
            int nt = m.types().length;
            int fixedN = 0, hw = 0, rail = 0, yard = 0, res = 0, own = 0;
            for (int i = 0; i < in.fixed.length; i++) {
                byte f = in.fixed[i];
                if (f != 0) fixedN++;
                if (f == DistrictPlan.FIXED_HIGHWAY) hw++;
                else if (f == DistrictPlan.FIXED_RAIL) rail++;
                else if (f == DistrictPlan.FIXED_YARD) yard++;
                else if (f == DistrictPlan.FIXED_RESERVE) res++;
                if (in.owned[i]) own++;
            }
            if (oldBoxes == null) {
                oldBoxes = b;
                log.printf("m%d: district (0,0) index %d, %d boxes, carried %s%n", month, d.index, b.length, Arrays.toString(m.carried(d) == null ? null : m.carried(d)[0]));
            }
            int oldStand = 0;
            for (long k : oldBoxes) if (Arrays.binarySearch(b, k) >= 0) oldStand++;
            if (month % 100 == 0 || month == from) {
                log.printf("m%d: boxes %d, old standing %d of %d, fixed %d (hw %d rail %d yard %d res %d), owned %d, log %d, works %d, sidings %d, overflow %d, bought %s%n",
                        month, b.length, oldStand, oldBoxes.length, fixedN, hw, rail, yard, res, own, in.logMonth.length, in.works.size(), in.sidings,
                        sum(p.overflow), in.boughtAt == null ? "none" : "dated");
            }
            if (prevBoxes != null && prevMonth == month - every) {
                int[] mv = moves(prevBoxes, b, nt);
                int total = sum(mv);
                if (total > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int t = 0; t < nt; t++) if (mv[t] > 0) sb.append(' ').append(LongPlaytest.nameOf(g, t)).append('=').append(mv[t]);
                    log.printf("MOVE m%d->m%d: %d moved (of %d), old standing %d:%s%n", prevMonth, month, total, prevBoxes.length, oldStand, sb);
                    diffInputs(prevIn, in);
                    if (Boolean.getBoolean("probe.swap") && swaps++ < 40) swapParts(prevIn, in, prevBoxes, nt);
                }
            }
            DistrictPlan.Input before = prevIn; prevIn = in;
            if (oldPlan == null) {
                oldPlan = p;
                fixed0 = in.fixed.clone();
                firstFixed = new int[in.fixed.length]; firstKind = new byte[in.fixed.length];
            } else {
                boolean[] fresh = new boolean[in.fixed.length];
                int nf = 0;
                for (int i = 0; i < in.fixed.length; i++) {
                    if (in.fixed[i] != 0 && fixed0[i] == 0 && firstFixed[i] == 0) { firstFixed[i] = month; firstKind[i] = in.fixed[i]; }
                    if (in.fixed[i] != 0 && before != null && before.fixed[i] == 0) { fresh[i] = true; nf++; }
                }
                if (nf > 0 && prevPlan != null) {
                    int[] c = new int[1];
                    String s = overlapping(g, prevPlan, fresh, c);
                    log.printf("   m%d: %d plots newly fixed; last month's boxes on them: %d:%s%n", month, nf, c[0], s.length() > 200 ? s.substring(0, 200) : s);
                    byte[] rf = new byte[in.fixed.length];
                    m.runsInto(d, rf);
                    int ry = 0, rh = 0;
                    for (byte bb : rf) { if (bb == DistrictPlan.FIXED_YARD) ry++; if (bb == DistrictPlan.FIXED_HIGHWAY) rh++; }
                    StringBuilder ys = new StringBuilder();
                    for (long[] yy : m.runs().yards()) ys.append(java.util.Arrays.toString(yy));
                    log.printf("      runs alone: yard %d highway %d; reach %b; runs yards %s; frame x0 %d y0 %d; rail hub %s%n", ry, rh, m.runs().highwaysReach(), ys,
                            in.x0, in.y0, java.util.Arrays.toString(m.runs().rail().hub()));
                }
            }
            if (month == 4000) {
                boolean[] ever = new boolean[in.fixed.length];
                for (int i = 0; i < ever.length; i++) ever[i] = firstFixed[i] > 0;
                int[] c = new int[1];
                String s = overlapping(g, oldPlan, ever, c);
                log.printf("m1001's boxes on ground a run took later: %d of %d:%s%n", c[0], oldPlan.buildings, s);
                StringBuilder ob = new StringBuilder();
                for (int i = 0; i < oldPlan.buildings; i++) ob.append(' ').append(LongPlaytest.nameOf(g, oldPlan.btype[i])).append('@').append(oldPlan.bx[i]).append(',').append(oldPlan.by[i]).append('/').append(oldPlan.bw[i]).append('x').append(oldPlan.bh[i]);
                log.println("m1001's boxes:" + ob);
                for (int i = 0; i < oldPlan.buildings; i++) {
                    String nm = LongPlaytest.nameOf(g, oldPlan.btype[i]);
                    if (nm == null || !(nm.contains("Bottling") || nm.contains("Snack") || nm.contains("Vehicle") || nm.contains("Fabrication") || nm.contains("Meat"))) continue;
                    java.util.TreeMap<String, Integer> under = new java.util.TreeMap<>();
                    for (int y = oldPlan.by[i]; y < oldPlan.by[i] + oldPlan.bh[i]; y++)
                        for (int x = oldPlan.bx[i]; x < oldPlan.bx[i] + oldPlan.bw[i]; x++) {
                            int q = y * DistrictPlan.FRAME + x;
                            if (firstFixed[q] > 0) under.merge("m" + firstFixed[q] + "/kind" + firstKind[q], 1, Integer::sum);
                        }
                    log.printf("old works %s@%d,%d %dx%d: runs later under it %s%n", nm, oldPlan.bx[i], oldPlan.by[i], oldPlan.bw[i], oldPlan.bh[i], under);
                }
                log.printf("kinds: highway %d rail %d yard %d reserve %d%n", DistrictPlan.FIXED_HIGHWAY, DistrictPlan.FIXED_RAIL, DistrictPlan.FIXED_YARD, DistrictPlan.FIXED_RESERVE);
            }
            prevPlan = p;
            prevBoxes = b;
            prevMonth = month;
        } catch (RuntimeException e) {
            log.println("m" + month + " threw " + e);
            e.printStackTrace(log);
        }
    }

    static void diffInputs(DistrictPlan.Input a, DistrictPlan.Input b) {
        StringBuilder sb = new StringBuilder("   differs:");
        if (!Arrays.equals(a.fixed, b.fixed)) {
            int add = 0, gone = 0, chg = 0;
            for (int i = 0; i < a.fixed.length; i++) {
                if (a.fixed[i] == b.fixed[i]) continue;
                if (a.fixed[i] == 0) add++; else if (b.fixed[i] == 0) gone++; else chg++;
            }
            sb.append(String.format(" fixed(+%d -%d ~%d)", add, gone, chg));
        }
        if (!Arrays.equals(a.ramp, b.ramp)) sb.append(" ramp");
        if (a.works.size() != b.works.size() || !same(a.works, b.works)) sb.append(" works(" + a.works.size() + "->" + b.works.size() + ")");
        if (a.sidings != b.sidings) sb.append(" sidings(" + a.sidings + "->" + b.sidings + ")");
        if (a.sidingPlots != b.sidingPlots) sb.append(" sidingPlots");
        if (!Arrays.equals(a.owned, b.owned)) sb.append(" owned");
        if (!Arrays.equals(a.boughtAt, b.boughtAt)) sb.append(" boughtAt");
        if (!Arrays.equals(a.terrain, b.terrain)) sb.append(" terrain");
        if (!Arrays.equals(a.site, b.site)) sb.append(" site");
        if (!same(a.mines, b.mines)) sb.append(" mines");
        if (!Arrays.equals(a.counts, b.counts)) {
            sb.append(" counts[");
            for (int t = 0; t < Math.max(a.counts.length, b.counts.length); t++) {
                int x = t < a.counts.length ? a.counts[t] : 0, y = t < b.counts.length ? b.counts[t] : 0;
                if (x != y) sb.append(t).append(':').append(x).append("->").append(y).append(' ');
            }
            sb.append(']');
        }
        sb.append(" log(" + a.logMonth.length + "->" + b.logMonth.length + ")");
        boolean prefix = b.logMonth.length >= a.logMonth.length;
        for (int i = 0; prefix && i < a.logMonth.length; i++) {
            if (a.logMonth[i] != b.logMonth[i] || a.logType[i] != b.logType[i] || a.logChange[i] != b.logChange[i]) prefix = false;
        }
        if (!prefix) sb.append(" LOG-NOT-PREFIX");
        if (a.firstBatch != b.firstBatch) sb.append(" firstBatch");
        if (a.gravel != b.gravel || a.paved != b.paved) sb.append(" road");
        if (!Arrays.equals(a.surfaces, b.surfaces) || a.root != b.root) sb.append(" surfaces/root");
        log.println(sb);
    }

    static boolean same(java.util.List<int[]> a, java.util.List<int[]> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) if (!Arrays.equals(a.get(i), b.get(i))) return false;
        return true;
    }

    static DistrictPlan.Input copy(DistrictPlan.Input s) {
        DistrictPlan.Input c = new DistrictPlan.Input();
        c.seed = s.seed; c.x0 = s.x0; c.y0 = s.y0; c.hubX = s.hubX; c.hubY = s.hubY;
        System.arraycopy(s.terrain, 0, c.terrain, 0, s.terrain.length);
        System.arraycopy(s.owned, 0, c.owned, 0, s.owned.length);
        c.boughtAt = s.boughtAt == null ? null : s.boughtAt.clone();
        System.arraycopy(s.fixed, 0, c.fixed, 0, s.fixed.length);
        System.arraycopy(s.site, 0, c.site, 0, s.site.length);
        for (int[] x : s.mines) c.mines.add(x.clone());
        for (int[] x : s.works) c.works.add(x.clone());
        c.ramp = s.ramp == null ? null : s.ramp.clone();
        c.sidings = s.sidings; c.sidingPlots = s.sidingPlots;
        c.types = s.types;
        c.counts = s.counts.clone();
        c.gravel = s.gravel; c.paved = s.paved; c.highway = s.highway;
        System.arraycopy(s.surfaces, 0, c.surfaces, 0, s.surfaces.length);
        System.arraycopy(s.seamOpen, 0, c.seamOpen, 0, s.seamOpen.length);
        System.arraycopy(s.anchor, 0, c.anchor, 0, s.anchor.length);
        System.arraycopy(s.partedAnchor, 0, c.partedAnchor, 0, s.partedAnchor.length);
        c.root = s.root; c.asPrototype = s.asPrototype; c.bandsApart = s.bandsApart; c.combOnly = s.combOnly; c.latticeOnly = s.latticeOnly;
        c.hashes = s.hashes;
        c.logMonth = s.logMonth.clone(); c.logType = s.logType.clone(); c.logChange = s.logChange.clone();
        c.firstBatch = s.firstBatch;
        c.carry = s.carry == null ? null : s.carry.clone();
        return c;
    }

    /** cur with one part from prev at a time; the moves of each against prev's plan. */
    static void swapParts(DistrictPlan.Input prev, DistrictPlan.Input cur, long[] prevBoxes, int nt) {
        String[] parts = { "runs(fixed,ramp,works,sidings)", "ground(owned,boughtAt)", "terrain", "log", "counts", "sites", "road", "log+counts" };
        StringBuilder sb = new StringBuilder("   swap prev's part into cur -> moves:");
        for (int k = 0; k < parts.length; k++) {
            DistrictPlan.Input h = copy(cur);
            switch (k) {
                case 0 -> { System.arraycopy(prev.fixed, 0, h.fixed, 0, h.fixed.length); h.ramp = prev.ramp == null ? null : prev.ramp.clone();
                            h.works.clear(); for (int[] x : prev.works) h.works.add(x.clone()); h.sidings = prev.sidings; h.sidingPlots = prev.sidingPlots; }
                case 1 -> { System.arraycopy(prev.owned, 0, h.owned, 0, h.owned.length); h.boughtAt = prev.boughtAt == null ? null : prev.boughtAt.clone(); }
                case 2 -> System.arraycopy(prev.terrain, 0, h.terrain, 0, h.terrain.length);
                case 3 -> { h.logMonth = prev.logMonth.clone(); h.logType = prev.logType.clone(); h.logChange = prev.logChange.clone(); h.firstBatch = prev.firstBatch; }
                case 4 -> h.counts = prev.counts.clone();
                case 5 -> { System.arraycopy(prev.site, 0, h.site, 0, h.site.length); h.mines.clear(); for (int[] x : prev.mines) h.mines.add(x.clone()); }
                case 6 -> { h.gravel = prev.gravel; h.paved = prev.paved; h.highway = prev.highway; }
                case 7 -> { h.logMonth = prev.logMonth.clone(); h.logType = prev.logType.clone(); h.logChange = prev.logChange.clone(); h.firstBatch = prev.firstBatch; h.counts = prev.counts.clone(); }
                default -> { }
            }
            long[] hb = boxes(DistrictPlan.make(h));
            sb.append(' ').append(parts[k]).append('=').append(sum(moves(prevBoxes, hb, nt)));
        }
        log.println(sb);
    }
}
