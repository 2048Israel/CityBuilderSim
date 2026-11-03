package ham.citybuildersim;
import java.io.*; import java.util.*;
/** The wealth study's leak hunt: each month the households' gap beside every line the audit named, long format. */
public class LinesRun {
    static PrintWriter w;
    public static void main(String[] a) throws Exception {
        java.lang.reflect.Field f = LongPlaytest.class.getDeclaredField("out"); f.setAccessible(true); f.set(null, WealthRun.quiet);
        int end = Integer.parseInt(a[0]);
        w = new PrintWriter(new BufferedWriter(new FileWriter(a[1])));
        System.setProperty("playtest.seed", "0");
        GameFiles files = GameFiles.scratch("wealth-peek");
        PrintStream real = System.out; System.setOut(WealthRun.quiet);
        Game g = new Game(files, LongPlaytest.founding()); g.run();
        WealthRun.seed = 0; WealthRun.endMonth = end;
        WealthRun.csv = new PrintWriter(OutputStream.nullOutputStream());
        WealthRun.monthly = new PrintWriter(OutputStream.nullOutputStream()) {
            @Override public void println(String s) { }
        };
        // hook: after each month, dump the detail
        Thread t = null;
        WealthRun.hook = gg -> {
            MoneyAudit.Result r = gg.getLastMoneyAudit();
            for (String line : r.detail.split("\n")) {
                String s = line.trim();
                if (!(s.startsWith("+") || s.startsWith("-"))) continue;
                int cut = s.lastIndexOf(' ');
                String label = s.substring(0, cut).trim().replace(',', ';');
                String num = s.substring(cut + 1).replace(",", "");
                w.println(gg.getMonth() + "," + label + "," + num);
            }
            w.println(gg.getMonth() + ",=R," + WealthRun.lastResidual);
        };
        WealthRun.foundAndPlay(g);
        System.setOut(real);
        w.close();
    }
}
