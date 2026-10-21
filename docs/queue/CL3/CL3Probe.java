package ham.citybuildersim;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Scratch, not in the build (batch CL3): what CL3's notes measure on a copy of
 * a played city, on whichever build is first on the class path - 0.8.13's
 * plans from the counts, or CL3's from the log. Every district planned through
 * the map's own chain (LayoutProbe's planAll), then: each use's mean distance
 * from the founding site (the ring); the houses standing in a cell that holds
 * a mine or a mine's site or touches one (Jerus's "homes dont like being
 * placed near mines"); and how full a plan is - its boxes' plots over the
 * district's owned plots, for the districts whose plan could not place all it
 * was given (what PLAN_FILL is measured on). It reads the copy and never
 * writes it.
 *
 *   java -cp "<scratch>;<classes>;<gson.jar>" ham.citybuildersim.CL3Probe <copy root> 10 [months to play]
 */
public class CL3Probe {
    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]);
        int slot = Integer.parseInt(args[1]);
        int play = args.length > 2 ? Integer.parseInt(args[2]) : 0;
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        Game g;
        try {
            g = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            g.run();
            g.loadGame(slot);
            for (int k = 0; k < play; k++) g.toggleNextMonth();
        } finally {
            System.setOut(real);
        }
        CityMap m = g.getCityMap();
        long t0 = System.nanoTime();
        for (CityMap.District d : m.districts()) {
            if (m.planned(d)) continue;
            for (CityMap.Job j : m.planJobs(d)) { j.run(); m.adopt(j); }
        }
        m.packed();
        double planMs = (System.nanoTime() - t0) / 1e6;
        BuildingVisual.Type[] types = m.types();
        double[] dist = new double[3];
        long[] n = new long[3];
        long homes = 0, homesByMine = 0, packed = 0;
        List<double[]> fills = new ArrayList<>();
        for (CityMap.District d : m.districts()) {
            CityMap.Drawn dr = m.drawnOf(d);
            if (dr == null) continue;
            DistrictPlan.Input in = m.planInputHere(d);
            double sx = in.hubX + in.x0, sy = in.hubY + in.y0;
            // The cells holding a site plot or a mine's box, and those touching one, side or corner.
            boolean[] mined = new boolean[DistrictPlan.CELLS];
            for (int p = 0; p < in.site.length; p++) {
                if (in.site[p] == 0) continue;
                int x = p % DistrictPlan.FRAME, y = p / DistrictPlan.FRAME;
                if (x < 1 || y < 1 || x > DistrictPlan.SIDE - 1 || y > DistrictPlan.SIDE - 1) continue;
                mined[(y - 1) / DistrictPlan.CELL * DistrictPlan.CELLS_A_SIDE + (x - 1) / DistrictPlan.CELL] = true;
            }
            for (int[] b : in.mines) {
                for (int y = Math.max(1, b[1]); y <= Math.min(DistrictPlan.SIDE - 1, b[3]); y += 8) {
                    for (int x = Math.max(1, b[0]); x <= Math.min(DistrictPlan.SIDE - 1, b[2]); x += 8) {
                        mined[(y - 1) / DistrictPlan.CELL * DistrictPlan.CELLS_A_SIDE + (x - 1) / DistrictPlan.CELL] = true;
                    }
                }
            }
            boolean[] near = new boolean[DistrictPlan.CELLS];
            for (int c = 0; c < DistrictPlan.CELLS; c++) {
                if (!mined[c]) continue;
                int ci = c % DistrictPlan.CELLS_A_SIDE, cj = c / DistrictPlan.CELLS_A_SIDE;
                for (int b = -1; b <= 1; b++) for (int a = -1; a <= 1; a++) {
                    int i = ci + a, j = cj + b;
                    if (i >= 0 && j >= 0 && i < DistrictPlan.CELLS_A_SIDE && j < DistrictPlan.CELLS_A_SIDE) near[j * DistrictPlan.CELLS_A_SIDE + i] = true;
                }
            }
            long plots = 0, cells = 0;
            for (int k = 0; k < CityMap.TILES; k++) {
                int[] bx = dr.boxes[k];
                if (bx == null) continue;
                long tx = (long) (k % CityMap.TILES_A_SIDE) * World.TILE, ty = (long) (k / CityMap.TILES_A_SIDE) * World.TILE;
                for (int b : bx) {
                    if ((b & CityMap.PACKED_BIT) != 0) { packed++; continue; }
                    int x = b & 31, y = (b >> 5) & 31, bw = ((b >> 10) & 31) + 1, bh = ((b >> 15) & 31) + 1;
                    int type = b >>> CityMap.BOX_TYPE_SHIFT;
                    BuildingVisual.Type t = type < types.length ? types[type] : null;
                    if (t == null) continue;
                    long fx = tx + x, fy = ty + y;
                    plots += (long) bw * bh;
                    cells += BuildingVisual.cells(t);
                    int band = t.outer() ? 2 : t.cls() == BuildingVisual.INDUSTRY ? 1 : 0;
                    dist[band] += Math.hypot(in.x0 + fx + bw / 2.0 - sx, in.y0 + fy + bh / 2.0 - sy) * 0.03;
                    n[band]++;
                    if (band == 0) {
                        homes++;
                        if (fx >= 1 && fy >= 1 && fx < DistrictPlan.SIDE && fy < DistrictPlan.SIDE
                                && near[(int) ((fy - 1) / DistrictPlan.CELL) * DistrictPlan.CELLS_A_SIDE + (int) ((fx - 1) / DistrictPlan.CELL)]) homesByMine++;
                    }
                }
            }
            // ...and the same in the map's own measure (usedCells: the plots its buildings and roads are drawn on): the placed buildings' and the district's roads'.
            long road = 0;
            for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].road() != BuildingVisual.NOT_A_ROAD) road += (long) d.count(t) * BuildingVisual.cells(types[t]);
            if (dr.refused > 0 && d.owned() > 0) fills.add(new double[] { plots / (double) d.owned(), (cells + road) / (double) d.owned(), d.usedCells() / (double) d.owned() });
        }
        fills.sort((a, b) -> Double.compare(b[0], a[0]));
        System.out.printf("month %d, %d districts, every plan in %.0f ms%n", g.getMonth(), m.districts().size(), planMs);
        System.out.printf("ring, mean km from the founding site: homes %.2f (%d), industry %.2f (%d), outer kinds %.2f (%d); packed at the edge %d%n",
                n[0] > 0 ? dist[0] / n[0] : 0, n[0], n[1] > 0 ? dist[1] / n[1] : 0, n[1], n[2] > 0 ? dist[2] / n[2] : 0, n[2], packed);
        System.out.printf("homes-band buildings in or beside a mine's cell: %d of %d%n", homesByMine, homes);
        System.out.printf("plans that could not place all they were given: %d; their boxes over their owned plots:", fills.size());
        for (int i = 0; i < Math.min(12, fills.size()); i++) System.out.printf(" %.3f (cells %.3f, given %.3f)", fills.get(i)[0], fills.get(i)[1], fills.get(i)[2]);
        if (!fills.isEmpty()) {
            double[] f = fills.stream().mapToDouble(a -> a[0]).sorted().toArray();
            System.out.printf("; median %.3f%n", f[f.length / 2]);
        } else System.out.println();
    }
}
