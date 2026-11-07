package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;
import ham.citybuildersim.sectors.Mining;

/**
 * Scratch, not in the build (batch FX9): PlanCheck 6's city (MapCheck 1's, played MapCheck.MONTHS months), and at its last month every
 * district whose plan, with the streets on bought ground, keeps an FW11 way clear: the way taken out of the plan's streets, the pieces
 * it joined, and how many of their plots only the plan with FX9's streets has.
 */
public class FX9Probe5 {
    public static void main(String[] args) throws Exception {
        PrintStream log = new PrintStream(new java.io.FileOutputStream(args[0]), true);
        Path root = TempDirs.create("fx9probe5");
        GameFiles files = new GameFiles(root.resolve("city"), root.resolve("city-no-legacy"));
        Game g = new Game(files, LongPlaytest.founding().withWorldSeed(MiningCheck.IRON_SEED));
        PrintStream real = System.out;
        LongPlaytest.out = new PrintStream(java.io.OutputStream.nullOutputStream());
        System.setOut(LongPlaytest.out);
        try {
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            for (int m = 0; m < MapCheck.MONTHS; m++) {
                if (m % 6 == 5) {
                    LongPlaytest.advise(g);
                    LongPlaytest.ensureSchools(g);
                }
                if (m == MapCheck.IRON_AT && g.getLandManager().getIronDeposits() == 0) {
                    MiningCheck.makeRoom(g, 0, true);
                    LongPlaytest.build(g, LongPlaytest.nameOf(g, Mining.IRON_MINE), 1);
                }
                LongPlaytest.run(g, 1);
                if ((m + 1) % MapCheck.DRAWN_EVERY == 0) analyse(g, log);
            }
        } finally {
            System.setOut(real);
        }
    }

    static void analyse(Game g, PrintStream log) throws Exception {
        CityMap map = g.getCityMap();
        int F = DistrictPlan.FRAME, A = DistrictPlan.AREA;
        for (CityMap.District d : map.districts()) {
            DistrictPlan on = DistrictPlan.make(map.planInput(d));
            int[] clear = FX9Probe3.clearPlots();
            DistrictPlan.streetsOnBought = false;
            DistrictPlan off = DistrictPlan.make(map.planInput(d));
            DistrictPlan.streetsOnBought = true;
            log.printf("m%d district (%d,%d): on %d boxes, walls %d (%d plots), layings %d (%d plots); off %d boxes, walls %d%n", g.getMonth(), d.dx, d.dy, on.buildings,
                    on.wallTries, on.wallClear, on.boughtCells, on.boughtStreets, off.buildings, off.wallTries);
            if (on.wallTries == 0) continue;
            boolean[] s = new boolean[A], sOff = new boolean[A], way = new boolean[A];
            for (int p : clear) way[p] = true;
            for (int q = 0; q < A; q++) { s[q] = on.kindAt(q) != DistrictPlan.NONE && !way[q]; sOff[q] = off.kindAt(q) != DistrictPlan.NONE; }
            int[] lab = new int[A];
            java.util.Arrays.fill(lab, -1);
            java.util.List<int[]> pcs = new java.util.ArrayList<>();
            int[] queue = new int[A];
            for (int p0 = 0; p0 < A; p0++) {
                if (!s[p0] || lab[p0] >= 0) continue;
                int id = pcs.size(), h = 0, t = 0, only = 0, x0 = F, y0 = F, x1 = -1, y1 = -1;
                lab[p0] = id;
                queue[t++] = p0;
                while (h < t) {
                    int u = queue[h++], ux = u % F, uy = u / F;
                    if (!sOff[u]) only++;
                    x0 = Math.min(x0, ux); y0 = Math.min(y0, uy); x1 = Math.max(x1, ux); y1 = Math.max(y1, uy);
                    int[] nb = { u - 1, u + 1, u - F, u + F };
                    for (int k = 0; k < 4; k++) {
                        int a = nb[k];
                        if (a < 0 || a >= A || (k < 2 && a / F != uy) || !s[a] || lab[a] >= 0) continue;
                        lab[a] = id;
                        queue[t++] = a;
                    }
                }
                pcs.add(new int[] { t, only, x0, x1, y0, y1 });
            }
            StringBuilder touch = new StringBuilder();
            java.util.Set<Integer> seen = new java.util.TreeSet<>();
            for (int p : clear) {
                int[] nb = { p - 1, p + 1, p - F, p + F };
                for (int a : nb) if (a >= 0 && a < A && s[a]) seen.add(lab[a]);
            }
            for (int id : seen) {
                int[] pc = pcs.get(id);
                touch.append(String.format(" [piece %d: %d plots, %d only FX9's, x %d..%d y %d..%d]", id, pc[0], pc[1], pc[2], pc[3], pc[4], pc[5]));
            }
            StringBuilder w = new StringBuilder();
            for (int p : clear) w.append(' ').append(p % F).append(',').append(p / F).append(sOff[p] ? "(off: street)" : "");
            log.printf("   the way (%d plots):%s%n   without it the streets are %d pieces; the way touches%s%n", clear.length, w, pcs.size(), touch);
        }
    }
}
