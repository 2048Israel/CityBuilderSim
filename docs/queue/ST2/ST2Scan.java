package ham.citybuildersim;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

/**
 * ST2's scan of ScaleCheck section 6's town (2026-11-01): the playtest's town, played once and one month at a time by
 * OrderSearchCheck.playtestCity()'s opening and rhythm, and at each month asked for, ScaleCheck's sections 6 and 9 read on
 * a copy of it loaded from its save - the fixture month a property, sections 1 to 5, 7 and 8 skipped (they do not read it).
 *
 * WHY: HD2's and EM2's scans replayed the town from month 0 for each month, 15 s a month. The town at month M is the same
 * whichever month playtestCity() was asked to stop at (its rhythm only cuts its last burst short, and LongPlaytest.run(g, n)
 * is n single months), so one trunk serves every month; a copy loaded from the trunk's save is the city section 6 reads.
 * Fidelity is read twice: the probe at 1124 against ScaleCheck's own output, and the trunk at its end against
 * OrderSearchCheck.playtestCity() played fresh to the same month (-Dst2.reference=true, a second JVM).
 *
 * Usage: ST2Scan <from> <to> <every> [more months] -> one line a month scanned on stdout; the trunk's figures every
 * month to st2.csv (-Dst2.csv=path). Each line: the month, the people at the look's end and a year before, the places,
 * the fails' count and each fail's section and label.
 */
public class ST2Scan {

    static Game g;
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());
    static PrintStream real;
    static final TreeSet<Integer> SCAN = new TreeSet<>();
    static Path root;
    static GameFiles trunkFiles;
    static PrintWriter csv;

    public static void main(String[] a) throws Exception {
        Locale.setDefault(Locale.CANADA);
        real = System.out;
        if (Boolean.getBoolean("st2.reference")) { reference(Integer.parseInt(a[0])); return; }
        int from = Integer.parseInt(a[0]), to = Integer.parseInt(a[1]), every = Integer.parseInt(a[2]);
        for (int m = from; m <= to; m += every) SCAN.add(m);
        for (int i = 3; i < a.length; i++) SCAN.add(Integer.parseInt(a[i]));
        int end = SCAN.last();
        csv = new PrintWriter(Files.newBufferedWriter(Path.of(System.getProperty("st2.csv", "st2.csv"))), true);
        csv.println("month,people,places,homes,unemployed,cash");
        root = TempDirs.create("st2scan");
        trunkFiles = new GameFiles(root.resolve("trunk"), root.resolve("trunk-no-legacy"));
        g = new Game(trunkFiles, LongPlaytest.founding());
        g.playOnLiterals();
        g.getForeignAccounts().pinRate(g.getForeignAccounts().getRate());
        long t0 = System.nanoTime();
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = QUIET;
        System.setOut(QUIET);
        try {
            // OrderSearchCheck.playtestCity(), its opening, step for step
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            steps(3);
            LongPlaytest.villageBuild(g, "House", 20);
            steps(4);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            steps(5);
            LongPlaytest.advise(g);
            steps(6);
            LongPlaytest.ensureSchools(g);
            // ...and playtestRhythm(), to the last month scanned
            int stop = 0;
            while (g.getMonth() < end) {
                stop++;
                int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                steps(Math.min(skip, end - g.getMonth()));
                if (g.getMonth() >= end) break;
                LongPlaytest.ensureSchools(g);
                for (int move = 0; move < LongPlaytest.movesPerLook() && g.getMonth() < end; move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    steps(1);
                }
                steps(Math.min(2, end - g.getMonth()));
            }
        } finally {
            LongPlaytest.out = was;
            System.setOut(real);
        }
        real.printf("trunk m%d: people %d places %d cash %.10e (%.0f s)%n", g.getMonth(), g.getPopulationManager().getPopulation(),
                g.getHouseholdCapacity(), g.getCash(), (System.nanoTime() - t0) / 1e9);
        csv.close();
        ScaleCheck.cleanUp(root);
    }

    /** OrderSearchCheck.playtestCity() itself to the month, on section 6's setup: the trunk's end must equal it to the digit. */
    static void reference(int month) throws Exception {
        Path r = TempDirs.create("st2ref");
        Game city = new Game(new GameFiles(r.resolve("grown"), r.resolve("grown-no-legacy")), LongPlaytest.founding());
        city.playOnLiterals();
        city.getForeignAccounts().pinRate(city.getForeignAccounts().getRate());
        OrderSearchCheck.playtestCity(city, month, QUIET);
        real.printf("reference m%d: people %d places %d cash %.10e%n", city.getMonth(), city.getPopulationManager().getPopulation(),
                city.getHouseholdCapacity(), city.getCash());
    }

    /** n single months, as LongPlaytest.run(g, n) plays them; a month that did not advance ends the burst, as run() returns there. */
    static void steps(int n) throws Exception {
        for (int i = 0; i < n; i++) {
            int before = g.getMonth();
            LongPlaytest.run(g, 1);
            if (g.getMonth() == before) return;
            csv.printf("%d,%d,%d,%d,%d,%.2f%n", g.getMonth(), g.getPopulationManager().getPopulation(), g.getHouseholdCapacity(),
                    g.getBuildingManager().getTotalHomes(), g.getPopulationManager().getUnemployed(), g.getCash());
            if (SCAN.contains(g.getMonth())) probe();
        }
    }

    /** Sections 6 and 9 of ScaleCheck.run(), statement for statement, on a copy of the trunk at this month. */
    static void probe() throws Exception {
        int m = g.getMonth();
        long t = System.nanoTime();
        Path pr = root.resolve("m" + m);
        // -Dst2.live=true: the last month is read on the trunk itself, as ScaleCheck reads its live city (nothing plays on after it)
        boolean live = Boolean.getBoolean("st2.live") && m == SCAN.last();
        GameFiles grown = live ? trunkFiles : new GameFiles(pr.resolve("grown"), pr.resolve("grown-no-legacy"));
        Game city = live ? g : new Game(grown);
        if (!live) {
            g.saveGame(10, "st2 trunk");
            Files.createDirectories(grown.savesDirectory());
            Files.copy(trunkFiles.saveFile(10), grown.saveFile(10), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(trunkFiles.historyFile(10), grown.historyFile(10), StandardCopyOption.REPLACE_EXISTING);
            if (Files.exists(trunkFiles.mapFile(10)))
                Files.copy(trunkFiles.mapFile(10), grown.mapFile(10), StandardCopyOption.REPLACE_EXISTING);
            city.playOnLiterals();
            city.loadGameSave(10);
            if (city.getLoadFailure() != null) { real.printf("m%d: the copy did not load: %s%n", m, city.getLoadFailure()); return; }
        }

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream sOut = ScaleCheck.out, sQuiet = ScaleCheck.quiet, sysOut = System.out, lpOut = LongPlaytest.out;
        ScaleCheck.out = new PrintStream(buf, true, StandardCharsets.UTF_8);
        ScaleCheck.quiet = QUIET;
        ScaleCheck.fails = 0;
        long popGrown, yearBefore, placesGrown;
        int monthGrown;
        try {
            // section 6, as ScaleCheck.run() has it from playtestCity() on
            ScaleCheck.lookAt(city);
            ScaleCheck.quietly(() -> city.saveGame(10, "scalecheck grown"));
            popGrown = city.getPopulationManager().getPopulation();
            yearBefore = city.getHistorySave().getPopulation().get(city.getHistorySave().months() - 13);
            placesGrown = city.getHouseholdCapacity();
            monthGrown = city.getMonth();
            ScaleCheck.out.printf("      month %d, %,d people in %,d household places%n", city.getMonth(), popGrown, city.getHouseholdCapacity());
            ScaleCheck.assertTrue("fixture: the city is growing - more people than a year before", popGrown > yearBefore);
            BuildingsTemplate home = city.getBuildingManager().getTemplateByName("House");
            ScaleCheck.assertTrue("fixture: ...with ground free for a home", home != null
                    && city.getLandManager().getAvailableSqFt() >= home.getLandSqFt());
            ScaleCheck.Original original = new ScaleCheck.Original(city);
            ScaleCheck.assertTrue("fixture: ...and its own rents price no migrant out in the months its copies are read against"
                    + " (the affordability pull 1 in all " + ScaleCheck.FREE_MONTHS + ")", original.calm());
            for (double target : ScaleCheck.FREE_PEOPLE) ScaleCheck.free(pr, grown, popGrown, target, original);
            // section 9
            ScaleCheck.tracks(pr, grown);
        } finally {
            ScaleCheck.out.flush();
            ScaleCheck.out = sOut;
            ScaleCheck.quiet = sQuiet;
            System.setOut(sysOut);
            LongPlaytest.out = lpOut;
        }
        // the fails, each with its section
        String section = "6";
        List<String> failed = new ArrayList<>();
        String built = "";
        double k = 1;
        java.util.regex.Matcher km;
        for (String line : buf.toString(StandardCharsets.UTF_8).split("\\R")) {
            if (line.startsWith("--- the sectors free, 5,")) section = "5bn";
            else if (line.startsWith("--- the sectors free, 10,")) section = "10bn";
            else if (line.startsWith("--- a copy")) section = "9";
            if ((km = java.util.regex.Pattern.compile("\\(K = ([0-9,]+)\\)").matcher(line)).find())
                k = Double.parseDouble(km.group(1).replace(",", ""));
            if ((km = java.util.regex.Pattern.compile("orders: ([0-9,]+) sized \\(the largest needed ([0-9,]+).*?, ([0-9,]+) judged").matcher(line)).find())
                built += String.format(" %s{sized %s, largest %.2f/K, judged %s}", section, km.group(1),
                        Double.parseDouble(km.group(2).replace(",", "")) / k, km.group(3));
            if (line.contains("built per K")) {
                int i = line.indexOf('('), j = line.indexOf(')');
                if (i >= 0 && j > i) built += " " + section + "[" + line.substring(i + 1, j) + "]";
            }
            if (line.trim().endsWith("FAIL") || line.contains(" FAIL ")) {
                String label = line.replaceAll("\\s+FAIL.*$", "").trim();
                failed.add(section + ":" + (label.length() > 60 ? label.substring(0, 60) : label));
            }
        }
        if (Boolean.getBoolean("st2.full")) Files.writeString(Path.of(System.getProperty("st2.dir", ".")).resolve("st2-m" + m + ".txt"), buf.toString(StandardCharsets.UTF_8));
        real.printf("m%d: %d at m%d (%d a year before), %d places; %d FAIL%s |%s (%.1f s)%n", m, popGrown, monthGrown, yearBefore,
                placesGrown, failed.size(), failed.isEmpty() ? "" : " " + String.join(" | ", failed), built,
                (System.nanoTime() - t) / 1e9);
        ScaleCheck.cleanUp(pr);
    }
}
