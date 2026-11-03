package ham.citybuildersim;
import java.io.*; import java.util.*;
public class ProbeRun {
    public static void main(String[] a) throws Exception {
        java.lang.reflect.Field f = LongPlaytest.class.getDeclaredField("out"); f.setAccessible(true); f.set(null, WealthRun.quiet);
        int end = Integer.parseInt(a[0]); int from = Integer.parseInt(a[1]);
        System.setProperty("playtest.seed", "0");
        GameFiles files = GameFiles.scratch("wealth-probe");
        PrintStream real = System.out; System.setOut(WealthRun.quiet);
        Game g = new Game(files, LongPlaytest.founding()); g.run();
        WealthRun.seed = 0; WealthRun.endMonth = end;
        WealthRun.csv = new PrintWriter(OutputStream.nullOutputStream());
        final double[] tot = new double[3];
        WealthRun.hook = gg -> {
            if (gg.getMonth() >= from) {
                tot[0] += WealthRun.lastResidual;
                tot[1] += WealthRun.lastDS;
                tot[2] += gg.getExchange().getEmigrantsPaid();
            }
            WealthProbe.on = gg.getMonth() + 1 >= from;
        };
        WealthRun.foundAndPlay(g);
        System.setOut(real);
        double probeSum = 0;
        for (double[] v : WealthProbe.moved.values()) probeSum += v[0];
        System.out.printf("months %d-%d: residual %.6g  dS %.6g  probe sum %.6g  emigrants paid %.6g%n", from, end, tot[0], tot[1], probeSum, tot[2]);
        List<Map.Entry<Integer, double[]>> e = new ArrayList<>(WealthProbe.moved.entrySet());
        e.sort((x, y) -> Double.compare(Math.abs(y.getValue()[3]), Math.abs(x.getValue()[3])));
        for (int i = 0; i < Math.min(40, e.size()); i++)
            System.out.printf("L%-6d savings %+.6g  pools %+.6g  together %+.6g%n", e.get(i).getKey(), e.get(i).getValue()[0], e.get(i).getValue()[2], e.get(i).getValue()[3]);
    }
}
