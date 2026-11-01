package ham.citybuildersim;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

/**
 * IC1's second ScaleCheck probe (SK1, folded into IC1, 2026-11-01): section 9's two copies of a kept town, K = 1 and
 * K = ScaleCheck.TRACK_TIMES, every public numeric getter of each sector (-Dsectors=true) and of the Game objects named
 * in -Dobjects (getBank() for "bank"), the copy's over the city's, at the load or after -Dmonths, with every sector held
 * (-Dheld=true) and the treasury's automatic rescue off. A '#' marks a figure that is neither K times the city's nor
 * the city's own. It found Freight's carried.<stream> shares loaded K times over (the copy hauled K squared tonnes).
 *
 *   javac -d pc -cp "target/classes;$(cat target/classpath.txt)" docs/queue/IC1/IC1BankProbe.java
 *   java -Xmx3g -Dmonths=0 -Dsectors=true -Dobjects=bank -Dgrown.dir=<a folder whose grown/ holds slot 10>
 *        -cp "pc;target/classes;$(cat target/classpath.txt)" ham.citybuildersim.IC1BankProbe
 */
public class IC1BankProbe {
    public static void main(String[] a) throws Exception {
        PrintStream out = System.out, real = System.out;
        PrintStream quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        ScaleCheck.out = out; ScaleCheck.quiet = quiet;
        Path root = TempDirs.create("ic1bank");
        Path gr = Paths.get(System.getProperty("grown.dir"));
        GameFiles grown = new GameFiles(gr.resolve("grown"), gr.resolve("grown-no-legacy"));
        long[] times = { 1, ScaleCheck.TRACK_TIMES };
        String[] targets = System.getProperty("objects", "bank").split(",");
        List<Map<String, Double>> seen = new ArrayList<>();
        for (int c = 0; c < 2; c++) {
            long k = times[c];
            GameFiles files = new GameFiles(root.resolve("t" + k), root.resolve("t" + k + "-no-legacy"));
            ScaleCheck.scale(grown.saveFile(10), files.saveFile(10), k);
            Files.copy(grown.historyFile(10), files.historyFile(10));
            Game g = new Game(files);
            g.playOnLiterals();
            System.setOut(quiet); try { g.loadGameSave(10); } finally { System.setOut(real); }
            if (Boolean.getBoolean("held")) for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            g.getFund().setRescueMode(TreasuryFund.RescueMode.BUTTON);
            System.setOut(quiet); try { g.simulateMonths(Integer.getInteger("months", 0)); } finally { System.setOut(real); }
            Map<String, Double> m = new TreeMap<>();
            m.put("game.branchesClosed", (double) g.getBranchesClosed());
            m.put("game.banks", (double) g.getBuildingManager().countByName("Commercial Bank"));
            if (Boolean.getBoolean("sectors")) for (Sector sec : g.getSectors().all()) {
                for (Method mt : sec.getClass().getMethods()) {
                    if (mt.getParameterCount() != 0 || Modifier.isStatic(mt.getModifiers()) || mt.getName().equals("hashCode")) continue;
                    Class<?> r = mt.getReturnType();
                    if (r != double.class && r != long.class && r != int.class) continue;
                    try { m.put(sec.key() + "." + mt.getName(), ((Number) mt.invoke(sec)).doubleValue()); } catch (Throwable e) { }
                }
            }
            for (String t : targets) {
                if (t.isEmpty()) continue;
                Object o = Game.class.getMethod("get" + Character.toUpperCase(t.charAt(0)) + t.substring(1)).invoke(g);
                for (Method mt : o.getClass().getMethods()) {
                    if (mt.getParameterCount() != 0 || Modifier.isStatic(mt.getModifiers())) continue;
                    Class<?> r = mt.getReturnType();
                    if (r != double.class && r != long.class && r != int.class) continue;
                    try { m.put(t + "." + mt.getName(), ((Number) mt.invoke(o)).doubleValue()); } catch (Throwable e) { }
                }
            }
            seen.add(m);
        }
        long k = times[1];
        for (String n : seen.get(0).keySet()) {
            double x = seen.get(0).get(n), y = seen.get(1).getOrDefault(n, Double.NaN);
            if (x == 0 && y == 0) continue;
            double ratio = y / x;
            boolean ok = Math.abs(ratio - 1) < 1e-3 || Math.abs(ratio / k - 1) < 1e-3;
            out.printf(Locale.ROOT, "%s %-50s city %16.6g copy %16.6g ratio %12.6g%n", ok ? " " : "#", n, x, y, ratio);
        }
    }
}
