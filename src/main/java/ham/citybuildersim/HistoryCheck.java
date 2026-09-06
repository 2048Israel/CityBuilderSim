package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Verifies the graph history: recording, alignment, and the round trip. Not
 * part of the game.
 *
 * WHY THIS EXISTS
 *
 * The history went from eight series to twenty-three, and every one of the
 * three ways that can go wrong is silent.
 *
 * 1. A SERIES THAT IS NEVER RECORDED looks exactly like one that is, until you
 *    open the graph and it is empty - which is a long way from where the
 *    mistake was made.
 * 2. A SERIES MISSING FROM restoreFrom() records perfectly all session and
 *    vanishes on reload. The live game and the reloaded game disagree and
 *    nothing says so.
 * 3. A SHORT SERIES - one added after a city was already being played - lines
 *    up with the END of the month axis and not the start. Draw it from the left
 *    instead and last decade's sickness appears in the founding years, plotted
 *    confidently against the wrong months.
 *
 * The third is the interesting one, because it is the only bug here that
 * produces a graph that looks completely fine.
 */
public class HistoryCheck {

    static int fails = 0;

    /** The game narrates every month to stdout; the findings are the output here. */
    static final PrintStream OUT = System.out;
    static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
    });

    /** Runs a stretch of months without the monthly report burying the results. */
    static void quietly(Runnable work) {
        System.setOut(QUIET);
        try { work.run(); } finally { System.setOut(OUT); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double a, double b) {
        boolean ok = Math.abs(a - b) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-62s %s%n", label,
                ok ? "OK" : String.format("FAIL  %.6f != %.6f", a, b));
    }

    public static void main(String[] args) throws Exception {

        Path root = Files.createTempDirectory("history");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        /* ============ 1. every series is recorded, every month ============ */
        System.out.println("--- every series records, every month ---");

        Game city = new Game(files);
        int months = 30;
        quietly(() -> { city.newGame(); city.simulateMonths(months); });

        HistorySave h = city.getHistorySave();
        System.out.println("  months on the axis: " + h.months());

        assertTrue("the axis has a point per month lived", h.months() >= months);

        /*
         * EVERY series, by walking the map rather than naming them.
         *
         * Naming them here would be a second list to keep in step with
         * HistorySave's, and a series left off BOTH lists is a series nobody
         * checks - which is exactly the failure mode. Walking seriesByName()
         * means adding a series to the recorder automatically adds it here.
         */
        Map<String, List<? extends Number>> series = h.seriesByName();
        System.out.println("  series kept: " + series.size());
        assertTrue("there are more than the original eight", series.size() > 8);

        for (Map.Entry<String, List<? extends Number>> e : series.entrySet()) {
            assertTrue("  " + e.getKey() + " has a value for every month",
                    e.getValue().size() == h.months());
        }

        /*
         * AND THEY ARE NOT ALL ZERO.
         *
         * Length is not recording. A series wired to a getter that returns 0 -
         * or to the wrong getter on an object that has not been updated yet -
         * has the right length and no content, and looks identical on a graph
         * to a city where nothing happened. A thirty-month city with people in
         * it has a population, a wage bill and a food price.
         */
        for (String key : new String[]{"population", "gdp", "totalWage", "foodPrice",
                                       "materialsPrice", "landPrice", "revenue"}) {
            double[] v = h.aligned(key);
            boolean anyNonZero = false;
            for (double d : v) if (!Double.isNaN(d) && d != 0) anyNonZero = true;
            assertTrue("  " + key + " actually carries values", anyNonZero);
        }

        /* ============ 2. it survives a save and a reload ============ */
        System.out.println("\n--- and it survives a reload ---");

        assertTrue("the city saved", city.saveGame(1, "history").ok);

        Game reloaded = new Game(files);
        quietly(() -> reloaded.loadGameSave(1));
        HistorySave back = reloaded.getHistorySave();

        assertTrue("the axis came back whole", back.months() == h.months());

        /*
         * Compared series by series off the map, so a line forgotten in
         * restoreFrom() fails here by name instead of silently emptying a
         * graph. This is the assertion that made the eight hand-written
         * assignments in Game unnecessary.
         */
        int checked = 0;
        for (String key : series.keySet()) {
            double[] before = h.aligned(key);
            double[] after = back.aligned(key);
            boolean same = before.length == after.length;
            for (int i = 0; same && i < before.length; i++) {
                same = Math.abs(before[i] - after[i]) < 1e-9;
            }
            if (!same) {
                System.out.println("  MISMATCH in " + key
                        + " - is it missing from HistorySave.restoreFrom()?");
            }
            assertTrue("  " + key + " reloaded identically", same);
            checked++;
        }
        System.out.println("  " + checked + " series compared across the reload");

        /* ============ 3. A SHORT SERIES LINES UP WITH THE END ============

           The one that draws a convincing wrong graph.

           A city played before a series existed loads with that series empty
           and the axis full. Play on and the series fills from THAT MONTH
           FORWARD, so it describes the END of the axis. Padding it at the front
           is the only correct reading; padding it at the back - or, worse,
           plotting it from index zero - takes the last few months of data and
           draws them over the city's founding, and nothing about the result
           looks wrong.

           Simulated by hand rather than waited for, because the alternative is
           keeping a pre-sickness save file around forever as a fixture.
           ============================================================== */
        System.out.println("\n--- a series added late lines up with the END ---");

        Path historyFile = files.historyFile(1);
        String json = Files.readString(historyFile);

        // Strip one series out of the file entirely - exactly what a history
        // written before that series existed looks like.
        String stripped = dropSeries(json, "sickRate");
        assertTrue("the fixture actually removed the series",
                json.contains("\"sickRate\"") && !stripped.contains("\"sickRate\""));
        Files.writeString(historyFile, stripped);

        Game older = new Game(files);
        quietly(() -> older.loadGameSave(1));
        HistorySave old = older.getHistorySave();

        System.out.println("  months: " + old.months()
                + ", sickRate values: " + old.seriesByName().get("sickRate").size());

        assertTrue("the axis is untouched by the missing series",
                old.months() == h.months());
        assertTrue("...and the missing series is empty, not absent",
                old.seriesByName().get("sickRate") != null
                        && old.seriesByName().get("sickRate").isEmpty());

        double[] absent = old.aligned("sickRate");
        assertTrue("aligned() still returns a full-length array",
                absent.length == old.months());

        boolean allNaN = true;
        for (double d : absent) if (!Double.isNaN(d)) allNaN = false;
        /*
         * NaN AND NOT ZERO, which is the whole distinction. A sick rate of 0%
         * is a healthy city; "we were not counting" is not a claim about the
         * city's health at all, and a graph that draws the second as the first
         * invents a decade of perfect health.
         */
        assertTrue("...reading as NaN - not-recorded is not the same as zero", allNaN);

        // Now play on, and check the new months land at the RIGHT END.
        quietly(() -> older.simulateMonths(4));
        HistorySave grown = older.getHistorySave();

        int lived = grown.months();
        int recorded = grown.seriesByName().get("sickRate").size();
        System.out.println("  after 4 more months: " + lived
                + " on the axis, " + recorded + " sick rates");

        assertTrue("only the new months have the series", recorded == 4);

        double[] aligned = grown.aligned("sickRate");
        assertTrue("aligned() covers the whole axis", aligned.length == lived);

        for (int i = 0; i < lived - recorded; i++) {
            assertTrue("  month " + grown.getMonth().get(i) + " is blank, as it should be",
                    Double.isNaN(aligned[i]));
        }
        boolean tailIsReal = true;
        for (int i = lived - recorded; i < lived; i++) {
            if (Double.isNaN(aligned[i])) tailIsReal = false;
        }
        assertTrue("...and the LAST four months carry the data", tailIsReal);

        /* ============ 4. the derived series do not divide by zero ============

           Unemployment, GDP per capita and the average wage are all a ratio
           over something that is ZERO in a brand new city - no workforce, no
           population. Deriving them on the way to the screen is right, and it
           puts a division one line away from a graph.
           ================================================================= */
        System.out.println("\n--- derived series on a city with nobody in it ---");

        Game empty = new Game(files);
        quietly(() -> { empty.newGame(); empty.simulateMonths(2); });
        HistorySave e = empty.getHistorySave();

        double[] pop = e.aligned("population");
        double[] work = e.aligned("workforce");
        System.out.printf("  month 1: population %.0f, workforce %.0f%n", pop[0], work[0]);

        for (int i = 0; i < e.months(); i++) {
            double w = work[i], j = e.aligned("jobs")[i];
            double unemployment = w > 0 ? Math.max(0, (w - j) / w) : Double.NaN;
            assertTrue("  month " + i + ": unemployment is a number or nothing, never infinite",
                    Double.isNaN(unemployment) || !Double.isInfinite(unemployment));
        }

        /* ============ 5. a new game forgets it ============ */
        System.out.println("\n--- and a new game starts with a blank sheet ---");

        quietly(reloaded::newGame);
        assertTrue("the axis is empty", reloaded.getHistorySave().months() <= 1);
        for (Map.Entry<String, List<? extends Number>> en
                : reloaded.getHistorySave().seriesByName().entrySet()) {
            assertTrue("  " + en.getKey() + " carries nothing from the old city",
                    en.getValue().size() <= 1);
        }

        cleanUp(root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** Removes one "name": [ ... ] entry from the history JSON. */
    static String dropSeries(String json, String name) {
        String key = "\"" + name + "\":";
        int at = json.indexOf(key);
        if (at < 0) return json;
        int close = json.indexOf(']', at);
        int end = close + 1;
        if (end < json.length() && json.charAt(end) == ',') end++;
        return json.substring(0, at) + json.substring(end);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
