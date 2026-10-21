package ham.citybuildersim;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scratch, not in the build: CL1's measure of today's map on a copy of a
 * played city (args: saveRoot slot months). It loads the copy (saveRoot holds
 * data/saves/, never the real folder), then prints what the study's budget
 * is measured against: the sidecar's size, write and read; every district's
 * plan, cold and warm; and for each month played on the copy, how many
 * districts were planned again, in how long, and how many buildings' boxes
 * moved - the shuffle Jerus sees - with where each use stands from the
 * founding site (the ring). It writes nothing but the copy's own autosave.
 *
 *   javac -cp "target/classes;<gson.jar>" -d <scratch> docs/studies/city-layout/LayoutProbe.java
 *   java -cp "<scratch>;target/classes;<gson.jar>" ham.citybuildersim.LayoutProbe <copy> 10 12
 */
public class LayoutProbe {

    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]);
        int slot = Integer.parseInt(args[1]);
        int months = args.length > 2 ? Integer.parseInt(args[2]) : 6;
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = new Game(files);
        g.run();
        long t0 = System.nanoTime();
        g.loadGame(slot);
        double loadMs = ms(t0);
        CityMap m = g.getCityMap();
        System.out.printf("slot %d: month %d, population %,d, districts %d, load %.0f ms (whole game)%n",
                slot, g.getMonth(), (long) g.getPopulationManager().getPopulation(), m.districts().size(), loadMs);

        // The sidecar: size, write, read (best of 20 after 5 to warm).
        byte[] side = m.writeSidecar(g.getMonth());
        double[] w = new double[20], r = new double[20];
        for (int k = -5; k < 20; k++) {
            long a = System.nanoTime();
            side = m.writeSidecar(g.getMonth());
            double wm = ms(a);
            a = System.nanoTime();
            CityMap back = CityMap.readSidecar(side, g.getLandManager().getCityLand(), g.getLandManager()::remainingByHolding,
                    g.getMapTypes(), g.getMonth(), m.lastStamp());
            double rm = ms(a);
            if (back == null) throw new IllegalStateException("sidecar did not read back");
            if (k >= 0) { w[k] = wm; r[k] = rm; }
        }
        Arrays.sort(w);
        Arrays.sort(r);
        System.out.printf("sidecar: %,d bytes deflated; write median %.2f ms, read median %.2f ms%n", side.length, w[10], r[10]);

        // Every district's plan: cold (this map, first plans of the run), then warm (fresh maps read from the sidecar).
        Plans cold = planAll(m);
        System.out.printf("plans cold: %s%n", cold);
        Plans best = null;
        for (int k = 0; k < 3; k++) {
            CityMap fresh = CityMap.readSidecar(side, g.getLandManager().getCityLand(), g.getLandManager()::remainingByHolding,
                    g.getMapTypes(), g.getMonth(), m.lastStamp());
            Plans p = planAll(fresh);
            if (best == null || p.total < best.total) best = p;
        }
        System.out.printf("plans warm (best of 3, fresh maps): %s%n", best);

        Plans last = planAll(m);
        Snapshot before = snapshot(m, last);
        System.out.println("ring now: " + before.ring());
        long totalMoved = 0, totalBoxes = 0;
        double replanMs = 0;
        int replanned = 0;
        for (int k = 1; k <= months; k++) {
            long a = System.nanoTime();
            g.simulateMonths(1);
            double monthMs = ms(a);
            m = g.getCityMap();
            Plans p = planAll(m, last);
            Snapshot after = snapshot(m, p);
            int[] mv = moved(before, after);
            System.out.printf("month %d: district-type changes %d, runs laid in %.2f ms%n", g.getMonth(), m.lastTouched, m.lastRunsMs);
            System.out.printf("month %d: %.0f ms the month; %d districts' plans moved, planned in %.1f ms; boxes %,d, moved %,d (%.1f%%), in %d of %d districts; of theirs %.0f%%%n",
                    g.getMonth(), monthMs, p.changed, p.changedMs, after.total, mv[0], 100.0 * mv[0] / Math.max(1, after.total), mv[1], after.byDistrict.size(),
                    100.0 * mv[0] / Math.max(1, mv[2]));
            last = p;
            totalMoved += mv[0];
            totalBoxes += after.total;
            replanMs += p.changedMs;
            replanned += p.changed;
            before = after;
        }
        System.out.printf("over %d months: %.1f%% of boxes moved a month, %.1f districts and %.1f ms of planning a month%n",
                months, 100.0 * totalMoved / Math.max(1, totalBoxes), replanned / (double) months, replanMs / months);
        System.out.println("ring after: " + before.ring());
    }

    static double ms(long t0) { return (System.nanoTime() - t0) / 1e6; }

    /* ---------------------------------------------------------------- planning every district */

    static final class Plans {
        int jobs, districts, changed;
        double changedMs;
        /** Each district's plan as the last job drew it, and that job's stamp: kept here, as the game's PLANS_KEPT (64) would let them go. */
        final Map<Long, CityMap.Drawn> drawn = new HashMap<>();
        final Map<Long, Long> stamps = new HashMap<>();
        final Map<Long, Double> jobMs = new HashMap<>();
        double total, max, pictures;
        List<Double> each = new ArrayList<>();
        @Override public String toString() {
            double[] e = each.stream().mapToDouble(Double::doubleValue).sorted().toArray();
            double med = e.length == 0 ? 0 : e[e.length / 2];
            return String.format("%d plan jobs over %d districts, %.1f ms in all, median %.2f ms, slowest %.2f ms; pictures %.1f ms",
                    jobs, districts, total, med, max, pictures);
        }
    }

    static Plans planAll(CityMap m) { return planAll(m, null); }

    /** Every district planned (those current fetched as they stand); with `last`, which districts' stamps moved since it - what the game plans again: a district's own inputs or its carry in. */
    static Plans planAll(CityMap m, Plans last) {
        Plans out = new Plans();
        for (CityMap.District d : m.districts()) {
            out.districts++;
            if (!m.planned(d)) {
                for (CityMap.Job j : m.planJobs(d)) {
                    j.run();
                    m.adopt(j);
                    if (j instanceof CityMap.PlanJob pj) {
                        out.jobs++;
                        out.total += pj.ms();
                        out.max = Math.max(out.max, pj.ms());
                        out.pictures += pj.pictureMs();
                        out.each.add(pj.ms());
                        out.drawn.put(key(pj.d), pj.result);
                        out.jobMs.merge(key(pj.d), pj.ms(), (a, b) -> b);
                    }
                }
            }
        }
        m.packed();
        for (CityMap.District d : m.districts()) {
            long key = key(d);
            CityMap.Drawn dr = m.planned(d) ? m.drawnOf(d) : out.drawn.get(key);
            if (dr == null) continue;
            out.drawn.put(key, dr);
            out.stamps.put(key, dr.stamp);
            if (last != null && !Long.valueOf(dr.stamp).equals(last.stamps.get(key))) {
                out.changed++;
                out.changedMs += out.jobMs.getOrDefault(key, 0.0);
            }
        }
        return out;
    }

    /* ---------------------------------------------------------------- the boxes, month to month */

    static final class Snapshot {
        final Map<Long, long[]> byDistrict = new HashMap<>();
        long total, packed;
        /** Per class (BuildingVisual's HOME..MINE, then 10 = outer), the sum of distances from the founding site and the count. */
        final double[] dist = new double[11];
        final long[] n = new long[11];
        String ring() {
            String[] names = { "homes", "shops", "offices", "industry", "farms", "utilities", "schools", "health", "safety", "mines", "outer kinds" };
            StringBuilder b = new StringBuilder("mean distance from the founding site, km:");
            for (int c = 0; c < 11; c++) if (n[c] > 0) b.append(String.format(" %s %.2f (%,d)", names[c], dist[c] / n[c] * 0.03, n[c]));
            return b.toString();
        }
    }

    static long key(CityMap.District d) { return ((long) d.dx << 32) ^ (d.dy & 0xFFFFFFFFL); }

    static Snapshot snapshot(CityMap m, Plans p) {
        Snapshot s = new Snapshot();
        BuildingVisual.Type[] types = m.types();
        for (CityMap.District d : m.districts()) {
            CityMap.Drawn dr = p.drawn.get(key(d));
            if (dr == null) continue;
            DistrictPlan.Input in = m.planInputHere(d);
            double siteX = in.hubX + in.x0, siteY = in.hubY + in.y0;
            List<Long> keys = new ArrayList<>();
            for (int k = 0; k < CityMap.TILES; k++) {
                int[] bx = dr.boxes[k];
                if (bx == null) continue;
                long tx = in.x0 + (long) (k % CityMap.TILES_A_SIDE) * World.TILE, ty = in.y0 + (long) (k / CityMap.TILES_A_SIDE) * World.TILE;
                for (int b : bx) {
                    if ((b & CityMap.PACKED_BIT) != 0 && !Boolean.getBoolean("cl1.packedToo")) { s.packed++; continue; }
                    int x = b & 31, y = (b >> 5) & 31, bw = ((b >> 10) & 31) + 1, bh = ((b >> 15) & 31) + 1;
                    int type = b >>> CityMap.BOX_TYPE_SHIFT;
                    long ax = tx + x, ay = ty + y;
                    keys.add(((long) type << 48) | ((ax & 0xFFFFFL) << 24) | (ay & 0xFFFFFFL));
                    BuildingVisual.Type t = type < types.length ? types[type] : null;
                    if (t == null) continue;
                    int c = t.outer() ? 10 : Math.min(9, t.cls());
                    s.dist[c] += Math.hypot(ax + bw / 2.0 - siteX, ay + bh / 2.0 - siteY);
                    s.n[c]++;
                }
            }
            long[] arr = keys.stream().mapToLong(Long::longValue).sorted().toArray();
            s.byDistrict.put(key(d), arr);
            s.total += arr.length;
        }
        return s;
    }

    /** {boxes in `after` with no box of the same type at the same place in `before`, districts with any, boxes in those districts}. */
    static int[] moved(Snapshot before, Snapshot after) {
        int moved = 0, districts = 0, inThem = 0;
        for (Map.Entry<Long, long[]> e : after.byDistrict.entrySet()) {
            long[] a = e.getValue(), b = before.byDistrict.getOrDefault(e.getKey(), new long[0]);
            int i = 0, j = 0, same = 0;
            while (i < a.length && j < b.length) {
                if (a[i] == b[j]) { same++; i++; j++; }
                else if (a[i] < b[j]) i++;
                else j++;
            }
            int mv = a.length - same;
            moved += mv;
            if (mv > 0) { districts++; inThem += a.length; }
        }
        return new int[] { moved, districts, inThem };
    }
}
