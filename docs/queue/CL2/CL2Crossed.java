package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;

/** Scratch: his city's open homes cells - how many a highway, railway, yard or reserve crosses, and the layouts of the rest. */
public class CL2Crossed {
    public static void main(String[] args) throws Exception {
        GameFiles files = new GameFiles(Path.of(args[0]).resolve("data"), Path.of(args[0]).resolve("no-legacy"));
        PrintStream real = System.out;
        System.setOut(PlanCheck.QUIET);
        Game g;
        try { g = new Game(files); g.run(); g.loadGame(10); } finally { System.setOut(real); }
        CityMap m = g.getCityMap();
        int homes = 0, crossed = 0;
        int[] free = new int[3], hashAll = new int[3];
        int n = DistrictPlan.FRAME;
        for (CityMap.District d : m.districts()) {
            DistrictPlan.Input in = m.planInput(d);
            DistrictPlan p = DistrictPlan.make(in);
            for (int c = 0; c < DistrictPlan.CELLS; c++) {
                if (p.cellKind[c] != DistrictPlan.HOMES) continue;
                homes++;
                int ci = c % 8, cj = c / 8, x0 = DistrictPlan.CELL * ci + 1, y0 = DistrictPlan.CELL * cj + 1;
                boolean x = false;
                for (int y = Math.max(0, y0 - 1); y <= Math.min(n - 1, y0 + DistrictPlan.INTERIOR) && !x; y++)
                    for (int xx = Math.max(0, x0 - 1); xx <= Math.min(n - 1, x0 + DistrictPlan.INTERIOR); xx++) if (in.fixed[y * n + xx] != 0) { x = true; break; }
                hashAll[DistrictPlan.layoutOf(in.seed, in.x0 + (long) DistrictPlan.CELL * ci, in.y0 + (long) DistrictPlan.CELL * cj, p.cellSquare[c])]++;
                if (x) crossed++; else free[p.cellLayout[c]]++;
            }
        }
        System.out.printf("homes cells %d, crossed %d (%.0f%%); uncrossed by layout today/dropped/brick %d/%d/%d; the hash alone over all %d/%d/%d%n",
                homes, crossed, 100.0 * crossed / homes, free[0], free[1], free[2], hashAll[0], hashAll[1], hashAll[2]);
    }
}
