package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * The wages study's save reader (docs/studies/spec-wages.md, section 1.3): a
 * COPY of a save, loaded in a TempDirs folder as slot 10, and its wage ladder
 * printed as the model holds it - every job's wage, its target, its base and
 * multiple, every band's posts and supply, the floor and the cost of living.
 * The save named is only read: its three files are copied, never written.
 *
 *   java ... ham.citybuildersim.SaveLadder <save.json>
 */
public final class SaveLadder {

    public static void main(String[] args) throws Exception {
        Path json = Paths.get(args[0]);
        String stem = json.getFileName().toString().replaceFirst("\\.json$", "");
        Path dir = json.getParent();
        Path root = TempDirs.create("saveladder");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Files.createDirectories(files.savesDirectory());
        Files.copy(json, files.saveFile(10), StandardCopyOption.REPLACE_EXISTING);
        Path history = dir.resolve(stem + "-history.json");
        Path map = dir.resolve(stem + "-map.bin");
        if (Files.exists(history)) Files.copy(history, files.historyFile(10), StandardCopyOption.REPLACE_EXISTING);
        if (Files.exists(map)) Files.copy(map, files.mapFile(10), StandardCopyOption.REPLACE_EXISTING);

        PrintStream real = System.out;
        if (!Boolean.getBoolean("ladder.loud")) System.setOut(new PrintStream(new OutputStream() { @Override public void write(int b) { } }));
        Game g;
        try {
            g = new Game(files);
            g.run();
            g.loadGame(10);
        } finally {
            System.setOut(real);
        }
        LabourMarket lm = g.getLabourMarket();
        PopulationManager pm = g.getPopulationManager();
        System.out.printf("%s: month %d, population %,d, out of work %.1f%%, price index %.4f, cost of living %.4f%n",
                stem, g.getMonth(), pm.getPopulation(), 100 * pm.getUnemploymentRate(),
                g.getPriceIndex().getIndex(), lm.getCostOfLiving());
        System.out.printf("floor: real %.6f, nudge %+.1f%%, cash %.6f; the unit (founding rung x cost of living) %.6f%n",
                lm.getMinimumWage(), 100 * lm.getMinimumWageAdjustment(), lm.cashMinimumWage(),
                LabourMarket.DEFAULT_MINIMUM_WAGE * lm.getCostOfLiving());
        double[] posts = pm.staffablePostsByBand(), supply = pm.supplyByBand(), own = pm.workforceByBand();
        System.out.printf("%-12s %9s %9s %9s %7s %7s %7s %6s%n", "band", "posts", "supply", "own", "tight", "mult", "pinned", "spare");
        for (WageBand b : WageBand.values()) {
            int i = b.ordinal();
            System.out.printf("%-12s %9.1f %9.1f %9.1f %7.3f %7.3f %7s %6.0f%n", b.label(), posts[i], supply[i], own[i],
                    lm.getTightness(b), lm.getBandMultiple(b), lm.isPinned(b), pm.surplusInBand(b));
        }
        long[] jobs = pm.getJobs();
        long[] vacant = pm.getJobVacancy();
        double unit = LabourMarket.DEFAULT_MINIMUM_WAGE * lm.getCostOfLiving();
        System.out.printf("%-20s %6s %6s %10s %10s %10s %8s %8s%n", "job", "posts", "held", "wage", "target", "base", "w/floor", "lic");
        for (JobType j : JobType.values()) {
            int i = j.ordinal();
            System.out.printf("%-20s %6d %6d %10.6f %10.6f %10.6f %8.3f %8.3f%n", j, jobs[i], jobs[i] - vacant[i],
                    lm.getWage(j), lm.targetWageAt(j, lm.getMinimumWage()), lm.baseWage(j),
                    lm.getWage(j) / lm.cashMinimumWage(), lm.getLicenceMultiple(j));
        }
        LongPlaytest.cleanUp(root);
    }
}
