package ham.citybuildersim;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Scratch, not in the build: CL1's look at what the order of arrival costs a
 * district's packing. On a copy of a played city (args: saveRoot slot
 * [districts]) it takes the densest districts' plan inputs as the map's jobs
 * gather them, and plans each again with the prototype's DistrictPlan
 * (patch_arrivals.py) in several orders: today's (the counts by band,
 * largest first), the counts as one batch dealt a type at a time, the bands
 * largest first but placed as arrivals (no ladder), and small first. For each:
 * buildings placed and without a place, by band, and the cells opened.
 */
public class PackProbe {

    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]);
        int slot = Integer.parseInt(args[1]);
        int most = args.length > 2 ? Integer.parseInt(args[2]) : 8;
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = new Game(files);
        g.run();
        g.loadGame(slot);
        CityMap m = g.getCityMap();
        List<CityMap.District> ds = new ArrayList<>(m.districts());
        ds.sort((a, b) -> Long.compare(b.usedCells(), a.usedCells()));
        System.out.printf("slot %d, month %d, estate lines %s%n", slot, g.getMonth(), System.getProperty("cl1.estate", "opener"));
        long[][] sum = new long[NAMES.length][3];
        long[] sumArea = new long[NAMES.length];
        for (int k = 0; k < Math.min(most, ds.size()); k++) {
            CityMap.District d = ds.get(k);
            DistrictPlan.Input in = null;
            for (CityMap.Job j : m.planJobs(d)) {
                j.run();
                m.adopt(j);
                if (j instanceof CityMap.PlanJob pj && pj.d == d) in = pj.in;
            }
            if (in == null) continue;
            BuildingVisual.Type[] types = in.types;
            long demand = 0;
            for (int t = 0; t < in.counts.length && t < types.length; t++) {
                if (in.counts[t] <= 0 || types[t] == null || !types[t].drawn()) continue;
                int[] f = BuildingVisual.footprint(types[t]);
                demand += (long) in.counts[t] * f[0] * f[1];
            }
            StringBuilder line = new StringBuilder(String.format("district (%d,%d): owned %,d plots, the buildings' boxes %,d plots (%.0f%%);", d.dx, d.dy, d.owned(), demand, 100.0 * demand / Math.max(1, d.owned())));
            for (int v = 0; v < NAMES.length; v++) {
                in.arrivals = order(v, in.counts, types, d.dx * 31 + d.dy);
                long t0 = System.nanoTime();
                DistrictPlan p = DistrictPlan.make(in);
                double ms = (System.nanoTime() - t0) / 1e6;
                int[] refused = new int[3];
                for (int t = 0; t < p.overflow.length; t++) if (p.overflow[t] > 0 && types[t] != null) refused[band(types[t])] += p.overflow[t];
                sum[v][0] += p.buildings;
                sum[v][1] += refused[0] + refused[1] + refused[2];
                sum[v][2] += p.cellsOpen;
                long area = 0;
                for (int b = 0; b < p.buildings; b++) area += (long) p.bw[b] * p.bh[b];
                int streets = 0;
                for (int q = 0; q < p.street.length; q++) if (p.street[q] != 0) streets++;
                PlanCheck.Figures fg = PlanCheck.figures(p, in);
                String rules = fg.holds(p, in) ? "rules hold" : String.format("RULES FAIL (pieces %d%s, + %d, reach %d, on street %d, surface %s, highway %d, estate %s)",
                        fg.pieces, fg.groundParts ? " ground parts" : "", fg.closestPlus, fg.outOfReach, fg.onStreet, fg.surfaceHolds(p, in), fg.besideHighway, fg.estateHolds());
                sumArea[v] += area;
                line.append(String.format("  %s %d placed (%,d plots), %d/%d/%d without, %d cells (%d homes), %,d street plots, %.0f ms, %s;", NAMES[v], p.buildings, area, refused[0], refused[1], refused[2], p.cellsOpen, p.homesCells, streets, ms, rules));
            }
            System.out.println(line);
        }
        for (int v = 0; v < NAMES.length; v++) System.out.printf("all: %-9s %,d placed (%,d plots of boxes), %,d without a place, %,d cells%n", NAMES[v], sum[v][0], sumArea[v], sum[v][1], sum[v][2]);
    }

    static final String[] NAMES = { "today", "dealt", "bands", "small", "growth" };

    static int band(BuildingVisual.Type t) { return t.outer() ? 2 : t.cls() == BuildingVisual.INDUSTRY ? 1 : 0; }

    /** The counts as arrivals: null (today's plan), one batch dealt a type at a time from a hashed start, the bands largest first, or small first. */
    static int[] order(int v, int[] counts, BuildingVisual.Type[] types, int salt) {
        if (v == 0) return null;
        List<Integer> out = new ArrayList<>();
        int T = Math.min(counts.length, types.length);
        if (v == 4) {
            // Forty months' batches, each a fortieth of every type, dealt a type at a time: a district grown over time.
            int[] left = Arrays.copyOf(counts, T);
            for (int b = 0; b < 40; b++) {
                int[] take = new int[T];
                for (int t = 0; t < T; t++) take[t] = types[t] == null ? 0 : Math.min(left[t], (int) Math.ceil(counts[t] / 40.0));
                int start = Math.floorMod(salt * 7919 + b * 104729, Math.max(1, T));
                boolean any = true;
                while (any) {
                    any = false;
                    for (int j = 0; j < T; j++) {
                        int t = (start + j) % T;
                        if (take[t] > 0) { out.add(t); take[t]--; left[t]--; any = true; }
                    }
                }
            }
        } else if (v == 1) {
            int[] left = Arrays.copyOf(counts, T);
            int start = Math.floorMod(salt * 7919, Math.max(1, T));
            boolean any = true;
            while (any) {
                any = false;
                for (int j = 0; j < T; j++) {
                    int t = (start + j) % T;
                    if (left[t] > 0 && types[t] != null) { out.add(t); left[t]--; any = true; }
                }
            }
        } else {
            Integer[] ts = new Integer[T];
            for (int t = 0; t < T; t++) ts[t] = t;
            boolean large = v == 2;
            Arrays.sort(ts, (a, b) -> {
                if (types[a] == null || types[b] == null) return types[a] == null ? (types[b] == null ? 0 : 1) : -1;
                int ba = band(types[a]), bb = band(types[b]);
                if (ba != bb) return Integer.compare(ba, bb);
                int[] fa = BuildingVisual.footprint(types[a]), fb = BuildingVisual.footprint(types[b]);
                int c = Integer.compare(fa[0] * fa[1], fb[0] * fb[1]);
                return large ? -c : c;
            });
            for (int t : ts) if (types[t] != null) for (int k = 0; k < counts[t]; k++) out.add(t);
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }
}
