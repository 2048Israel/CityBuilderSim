package ham.citybuildersim;

import java.io.PrintStream;
import java.lang.reflect.Field;

/**
 * Scratch, not in the build (batch FX9): at month probe.at, district (probe.dx, probe.dy)'s own plan with the streets on bought
 * ground on and off; the street network's pieces with the plots only the on-plan has (laid by FX9), and the way FW11 keeps clear.
 */
public class FX9Probe3 {
    static PrintStream log;
    static final int AT = Integer.getInteger("probe.at", 1208), DX = Integer.getInteger("probe.dx", -1), DY = Integer.getInteger("probe.dy", -1);

    public static void main(String[] args) throws Exception {
        log = new PrintStream(new java.io.FileOutputStream(args[0]), true);
        LongPlaytest.probeHook = FX9Probe3::month;
        LongPlaytest.main(new String[0]);
    }

    static void month(Game g) {
        if (g.getMonth() != AT) return;
        try {
            CityMap m = g.getCityMap();
            CityMap.District d = null;
            for (CityMap.District e : m.districts()) if (e.dx == DX && e.dy == DY) d = e;
            DistrictPlan.Input in = m.planInput(d);
            DistrictPlan.streetsOnBought = true;
            DistrictPlan on = DistrictPlan.make(in);
            int[] clear = clearPlots();
            DistrictPlan.streetsOnBought = false;
            DistrictPlan off = DistrictPlan.make(m.planInput(d));
            DistrictPlan.streetsOnBought = true;
            int F = DistrictPlan.FRAME, A = DistrictPlan.AREA;
            boolean[] sOn = new boolean[A], sOff = new boolean[A];
            for (int q = 0; q < A; q++) { sOn[q] = on.kindAt(q) != DistrictPlan.NONE; sOff[q] = off.kindAt(q) != DistrictPlan.NONE; }
            int[] lab = new int[A];
            java.util.Arrays.fill(lab, -1);
            java.util.List<int[]> pieces = new java.util.ArrayList<>();
            int[] q = new int[A];
            for (int p0 = 0; p0 < A; p0++) {
                if (!sOn[p0] || lab[p0] >= 0) continue;
                int id = pieces.size(), h = 0, t = 0, onlyOn = 0, bought = 0, x0 = F, y0 = F, x1 = -1, y1 = -1;
                boolean edge = false;
                lab[p0] = id;
                q[t++] = p0;
                while (h < t) {
                    int u = q[h++], ux = u % F, uy = u / F;
                    if (!sOff[u]) onlyOn++;
                    if (in.boughtAt != null && in.boughtAt[u] > 258) bought++;
                    x0 = Math.min(x0, ux); y0 = Math.min(y0, uy); x1 = Math.max(x1, ux); y1 = Math.max(y1, uy);
                    if (ux == 0 || uy == 0 || ux == F - 1 || uy == F - 1) edge = true;
                    int[] nb = { u - 1, u + 1, u - F, u + F };
                    for (int k = 0; k < 4; k++) {
                        int a = nb[k];
                        if (a < 0 || a >= A || (k < 2 && a / F != uy) || !sOn[a] || lab[a] >= 0) continue;
                        lab[a] = id;
                        q[t++] = a;
                    }
                }
                pieces.add(new int[] { t, onlyOn, bought, x0, y0, x1, y1, edge ? 1 : 0 });
            }
            int main = 0;
            for (int i = 1; i < pieces.size(); i++) if (pieces.get(i)[0] > pieces.get(main)[0]) main = i;
            int onlyOnAll = 0, onlyOffAll = 0;
            for (int p = 0; p < A; p++) { if (sOn[p] && !sOff[p]) onlyOnAll++; if (sOff[p] && !sOn[p]) onlyOffAll++; }
            log.printf("m%d district (%d,%d): on %d boxes, walls %d tries %d plots, bought layings %d (%d plots); off %d boxes, walls %d; street plots only on %d, only off %d; %d pieces on%n",
                    AT, DX, DY, on.buildings, on.wallTries, on.wallClear, on.boughtCells, on.boughtStreets, off.buildings, off.wallTries, onlyOnAll, onlyOffAll, pieces.size());
            for (int i = 0; i < pieces.size(); i++) {
                int[] pc = pieces.get(i);
                log.printf("   piece %d%s: %d plots, %d only on (FX9's), %d on ground bought after the start, box x %d..%d y %d..%d, frame edge %s%n", i, i == main ? " (main)" : "",
                        pc[0], pc[1], pc[2], pc[3], pc[5], pc[4], pc[6], pc[7] == 1);
            }
            StringBuilder sb = new StringBuilder();
            for (int p : clear) sb.append(' ').append(p % F).append(',').append(p / F).append(in.boughtAt != null ? "@" + in.boughtAt[p] : "").append(sOn[p] ? "S" : "").append(lab[p] >= 0 ? "#" + lab[p] : "");
            log.printf("   the way kept clear (%d plots):%s%n", clear.length, sb);
        } catch (Throwable t) {
            t.printStackTrace(log);
        }
    }

    /** The builder's kept-clear plots after the last make() on this thread (clearAt[0..nClear)), by reflection. */
    static int[] clearPlots() throws Exception {
        Field f = DistrictPlan.class.getDeclaredField("BUILDERS");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<java.lang.ref.SoftReference<Object>> tl = (ThreadLocal<java.lang.ref.SoftReference<Object>>) f.get(null);
        Object b = tl.get().get();
        Field ca = b.getClass().getDeclaredField("clearAt"), nc = b.getClass().getDeclaredField("nClear");
        ca.setAccessible(true);
        nc.setAccessible(true);
        int n = nc.getInt(b);
        return java.util.Arrays.copyOf((int[]) ca.get(b), n);
    }
}
