package ham.citybuildersim;

import java.io.PrintStream;

/**
 * Scratch, not in the build (batch FX9): district (probe.dx, probe.dy)'s own plan at months probe.a and probe.b, streets on and off,
 * drawn as text over the window x probe.x0..x1, y probe.y0..y1: '#' a street both ways, '+' only with FX9's streets, '-' only
 * without, 'o' a box (on), ',' owned ground bought after month probe.since, '.' owned ground, ' ' not owned, 'W' the way FW11 kept clear.
 */
public class FX9Probe4 {
    static PrintStream log;
    static final int A = Integer.getInteger("probe.a", 1189), B = Integer.getInteger("probe.b", 1190), DX = Integer.getInteger("probe.dx", -1), DY = Integer.getInteger("probe.dy", -1);
    static final int X0 = Integer.getInteger("probe.x0", 200), X1 = Integer.getInteger("probe.x1", 256), Y0 = Integer.getInteger("probe.y0", 190), Y1 = Integer.getInteger("probe.y1", 256);
    static final int SINCE = Integer.getInteger("probe.since", 1000);

    public static void main(String[] args) throws Exception {
        log = new PrintStream(new java.io.FileOutputStream(args[0]), true);
        LongPlaytest.probeHook = FX9Probe4::month;
        LongPlaytest.main(new String[0]);
    }

    static void month(Game g) {
        int month = g.getMonth();
        if (month != A && month != B) return;
        try {
            CityMap m = g.getCityMap();
            CityMap.District d = null;
            for (CityMap.District e : m.districts()) if (e.dx == DX && e.dy == DY) d = e;
            DistrictPlan.Input in = m.planInput(d);
            DistrictPlan on = DistrictPlan.make(in);
            int[] clear = FX9Probe3.clearPlots();
            DistrictPlan.streetsOnBought = false;
            DistrictPlan off = DistrictPlan.make(m.planInput(d));
            DistrictPlan.streetsOnBought = true;
            int F = DistrictPlan.FRAME, AR = DistrictPlan.AREA;
            boolean[] box = new boolean[AR], way = new boolean[AR];
            for (int i = 0; i < on.buildings; i++) for (int y = on.by[i]; y < on.by[i] + on.bh[i]; y++) for (int x = on.bx[i]; x < on.bx[i] + on.bw[i]; x++) box[y * F + x] = true;
            for (int p : clear) way[p] = true;
            log.printf("m%d district (%d,%d): on %d boxes (walls %d, %d plots), off %d boxes; x %d..%d across, y %d..%d down; the last log record m%d type %d change %d%n", month, DX, DY,
                    on.buildings, on.wallTries, on.wallClear, off.buildings, X0, X1, Y0, Y1, in.logMonth[in.logMonth.length - 1], in.logType[in.logType.length - 1], in.logChange[in.logChange.length - 1]);
            for (int y = Y0; y <= Y1; y++) {
                StringBuilder sb = new StringBuilder(String.format("%4d ", y));
                for (int x = X0; x <= X1; x++) {
                    int p = y * F + x;
                    boolean a = on.kindAt(p) != DistrictPlan.NONE, b = off.kindAt(p) != DistrictPlan.NONE;
                    char c;
                    if (way[p]) c = 'W';
                    else if (a && b) c = '#';
                    else if (a) c = '+';
                    else if (b) c = box[p] ? 'x' : '-';
                    else if (box[p]) c = 'o';
                    else if (in.owned[p]) c = in.boughtAt != null && in.boughtAt[p] > SINCE ? ',' : '.';
                    else c = ' ';
                    sb.append(c);
                }
                log.println(sb);
            }
        } catch (Throwable t) {
            t.printStackTrace(log);
        }
    }
}
