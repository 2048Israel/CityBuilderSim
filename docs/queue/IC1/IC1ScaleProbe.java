/*
 * IC1's probe of ScaleCheck section 9 (not part of the game; a study file, not compiled with the tree).
 * It plays section 6's town as ScaleCheck does (the playtest's city on the goods' literals, its rate pinned, to
 * FREE_FIXTURE_MONTHS, its player's look), copies it through ScaleCheck.scale() at K = 1 and K = TRACK_TIMES, and
 * plays both `-Dmonths` months (3). Each month it prints the builders' share, the withheld posts, the unemployed,
 * the jobs and the cash, all per K, and every sector's decision, marking with # each line where the copy and its
 * city differ.
 *   -Dgrown.dir=<dir>  keeps the grown save there, and reuses it if it is there: a town grown on one tree's classes
 *                      can be copied and played on another's (the save format is the same from 0.8.91 to 0.8.93)
 *   -Dcallback=true    calls every laid-off crew back in both copies (BuildingManager.setOfferedShare(null))
 * Run (Git Bash, the data files in JDK_JAVA_OPTIONS), against a tree's classes:
 *   mkdir -p p/ham/citybuildersim && cp docs/queue/IC1/IC1ScaleProbe.java p/ham/citybuildersim/
 *   javac -d pc -cp "target/classes;$(cat target/classpath.txt)" p/ham/citybuildersim/IC1ScaleProbe.java
 *   java -Xmx3g -Dgrown.dir=$(pwd -W)/target/grown -cp "pc;target/classes;$(cat target/classpath.txt)" ham.citybuildersim.IC1ScaleProbe
 * What it showed (IC1's notes section 7): IC1's town (23,102 people at m1,129) parts the same way on main's
 * classes (0.8.91, the crews spoken for) as on IC1's, to the digit, with or without the crews called back.
 */
package ham.citybuildersim;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** IC1 scratch probe (not committed): ScaleCheck 9's city at K=1 and K=1000, each sector's decision and the builders' gate figures, month by month. */
public class IC1ScaleProbe {
    public static void main(String[] a) throws Exception {
        PrintStream out = System.out, real = System.out;
        ScaleCheck.out = out; OrderSearchCheck.out = out;
        PrintStream quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        ScaleCheck.quiet = quiet; OrderSearchCheck.quiet = quiet;
        int months = Integer.getInteger("months", 3);
        Path root = TempDirs.create("ic1scale");
        String kept = System.getProperty("grown.dir");
        Path gr = kept != null ? Paths.get(kept) : root;
        GameFiles grown = new GameFiles(gr.resolve("grown"), gr.resolve("grown-no-legacy"));
        if (kept != null && Files.exists(grown.saveFile(10))) { out.println("grown: the kept save at " + grown.saveFile(10)); } else {
        Game city = new Game(grown, LongPlaytest.founding());
        city.playOnLiterals();
        city.getForeignAccounts().pinRate(city.getForeignAccounts().getRate());
        System.setOut(quiet);
        try { OrderSearchCheck.playtestCity(city, ScaleCheck.FREE_FIXTURE_MONTHS, quiet); } finally { System.setOut(real); }
        ScaleCheck.lookAt(city);
        System.setOut(quiet); try { city.saveGame(10, "probe grown"); } finally { System.setOut(real); }
        out.println("grown at month " + city.getMonth() + ", " + city.getPopulationManager().getPopulation() + " people");
        }
        long[] times = { 1, ScaleCheck.TRACK_TIMES };
        List<List<String>> lines = new ArrayList<>();
        for (int c = 0; c < 2; c++) {
            List<String> l = new ArrayList<>(); lines.add(l);
            GameFiles files = new GameFiles(root.resolve("track" + times[c]), root.resolve("track" + times[c] + "-no-legacy"));
            ScaleCheck.scale(grown.saveFile(10), files.saveFile(10), times[c]);
            Files.copy(grown.historyFile(10), files.historyFile(10));
            Game g = new Game(files);
            g.playOnLiterals();
            System.setOut(quiet); try { g.loadGameSave(10); } finally { System.setOut(real); }
            if (Boolean.getBoolean("callback")) g.getBuildingManager().setOfferedShare(null);
            l.add(state(g, "load", times[c]));
            for (int i = 0; i < months; i++) {
                System.setOut(quiet); try { g.simulateMonths(1); } finally { System.setOut(real); }
                l.add(state(g, "m" + (i + 1), times[c]));
                for (Map.Entry<String, String> e : new TreeMap<>(g.getLastInvestments()).entrySet())
                    l.add("  m" + (i + 1) + " " + e.getKey() + ": " + e.getValue());
            }
        }
        for (int i = 0; i < Math.max(lines.get(0).size(), lines.get(1).size()); i++) {
            String x = i < lines.get(0).size() ? lines.get(0).get(i) : "-", y = i < lines.get(1).size() ? lines.get(1).get(i) : "-";
            out.println((x.equals(y) ? "  = " : "  # ") + "K=1    " + x);
            if (!x.equals(y)) out.println("    K=1000 " + y);
        }
    }

    static String state(Game g, String at, long k) {
        ham.citybuildersim.sectors.Construction b = g.getSectors().construction();
        BuildingManager bm = g.getBuildingManager();
        long wb = bm.getPostsWithheld();
        PopulationManager pm = g.getPopulationManager();
        return String.format(Locale.ROOT, "%s builders offered %.6f need %.6f fill %.6f idle %b | withheld %.3f (builders %.3f) unemployed %.3f labour %.3f jobs %.3f | cash %.6e",
                at, b.getPostsOfferedShare(), b.getCrewsNeeded(), b.getAverageFill(), b.getPostsOfferedShare() <= ham.citybuildersim.sectors.Construction.IDLE_PAYROLL_FLOOR + 1e-9,
                bm.getPostsWithheld() / (double) k, wb / (double) k, pm.getUnemployed() / (double) k, pm.getLabourForce() / (double) k,
                pm.getTotalJobs() / (double) k, g.getCash() / k);
    }
}
