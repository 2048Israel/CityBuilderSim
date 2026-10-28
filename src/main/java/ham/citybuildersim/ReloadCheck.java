package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ham.citybuildersim.sectors.Construction;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Plays a city, saves it, loads the save into a second game, runs both a month
 * and compares what each would save: a reloaded city has to play its next
 * month as the city that kept running. Not part of the game.
 *
 * WHY THIS EXISTS. 0.7.46 (A1) cut the series that differ a month after a
 * reload from 62 of 226 to 12 of 229, and the residual was left (todo-archive,
 * star 2). Nothing compared a reloaded city with its running twin past the
 * moment of the load: LongPlaytest's roundTrip() compares at the load only,
 * and SaveFileCheck 14 plays fixture cities on, not a played city's whole
 * save. FW6 measured it with a scratch tool - the probe city saved at a month,
 * loaded into a second Game, both run a month, both saved, the two saves'
 * JSON compared leaf by leaf. Every history series matched for 24 months; the
 * save did not: each sector book's stockRevalued and stockCounted (the month
 * after a load counts no revaluation), Construction's drawnImported and
 * drawnLocal, and the lastInvestment notes (the first investment pass after a
 * load weighs a different candidate, because the markets' month tally is not
 * saved). This is that tool as a check, so the next change that lets a city
 * play differently after a load is a red line and not a month nobody compared.
 *
 * WHAT IT PROVES, on the probe city (Danzik on the flag's preset, played by
 * LongPlaytest.probePlay) saved at about months 30, 120, 360 and 1200, each time
 * loaded into a second Game, with both run a month and then another:
 *
 *   1. EVERY HISTORY SERIES of the reloaded city is the running city's, a
 *      month on and two months on.
 *   2. ...AND EVERY FIGURE IN ITS SAVE, to the cent, a month on and two on
 *      (the map sidecar's stamp among them since CC1-M).
 *   3. THE CAUSES, ONE BY ONE, so a red section 2 says why: (a) every
 *      sector's stock revaluation is counted in the reload's first month and
 *      equals the live city's; (b) Construction's materials drawn at home and
 *      imported; (c) every market's month tally right after the load, and the
 *      investment notes a month on; (d) each sector book's shares right after
 *      the load; (e) the price index's rent and food weights right after the
 *      load; (f) a time skip reports what the running city's skip reports.
 *   4. THE REAL EARTH: sections 1 and 2 at one checkpoint (about month 120)
 *      on LongPlaytest.probeCity(files, "rotterdam"), where earth-2.bin is
 *      named; a line saying it did not run, never a FAIL, where it is not.
 *
 * Every fixture is asserted: the history holds more than a hundred series and
 * the save more than ten thousand numbers (so the comparison is not empty),
 * and each cause is shown at work in the live city before its parity is.
 *
 * WHY FOUR CHECKPOINTS AND NOT THREE: a cause shows only in the states that
 * exercise it. The investment notes, for one, came out the same after a reload
 * at months 35 and 365 of this city and different at 123 and 1205 (Heavy
 * Industry weighing an Electric Arc Furnace on the live city's tally and a
 * Blast Furnace on an empty one), so a harness with the first two alone would
 * have passed a bug it was written to find. The months are the probe's own
 * (probePlay's skips are fixed by its stop count), so they do not move unless
 * LongPlaytest does; a fixture line says each cause was seen at work.
 *
 * @author Jerus
 */
public class ReloadCheck {

    static int fails = 0;

    /** The months the live city is saved at; LongPlaytest.probePlay overshoots by a few, so a point reads g.getMonth(). */
    static final int[] CHECKPOINTS = { 30, 120, 360, 1200 };

    /** The month the Earth city is saved at (section 4). */
    static final int EARTH_CHECKPOINT = 120;

    /** Where section 4's city is founded: the real Earth round Rotterdam, as LongPlaytest --earth rotterdam plays it. */
    static final String EARTH_SPOT = "rotterdam";

    /** A time skip of section 3f: two months, so it is a skip (the game autosaves before one), and two are run on each city. */
    static final int SKIP_MONTHS = 2;

    /** Two numbers are the same when they differ by no more than a cent... */
    static final double CENT = 0.005;

    /** ...and by no more than a billionth of the larger (or of 1, for the tiny). */
    static final double RELATIVE = 1e-9;

    /**
     * The keys no two saves of one city share, whatever it did: the slot's name and the time it was written. Skipped wherever
     * they stand in the tree. The map sidecar's stamp (mapStamp) was exempt too until batch CC1-M, because the first month after a
     * load re-laid the railway; it is compared since, like every figure: a map that moves after a load is a red line here (and in
     * GatewayCheck 21, which causes it on a coast).
     */
    static final Set<String> EXEMPT = Set.of("slotName", "savedAt");

    /** How many differing paths a failing line lists, each with both values; the rest is counted. */
    static final int SHOWN = 12;

    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    /* ----------------------------- the findings ----------------------------- */

    /**
     * What a comparison found: how many things differ, in how many kinds of place, and the first SHOWN of them, each
     * with both values. A thing's kind is what is left of its name once the one it belongs to is taken off: a path with
     * its indexes masked (.sectorBooks[*].stockCounted), or the words after the colon of "Retail: stockCounted".
     */
    static class Findings {
        int differing;
        final List<String> first = new ArrayList<>();
        final Map<String, Integer> kinds = new LinkedHashMap<>();

        void differ(String what, String live, String reloaded) {
            differing++;
            int colon = what.indexOf(": ");
            kinds.merge(colon >= 0 ? what.substring(colon + 2) : what.replaceAll("\\[\\d+\\]", "[*]"), 1, Integer::sum);
            if (first.size() < SHOWN) first.add(what + "  live " + live + "  reloaded " + reloaded);
        }

        /** The kinds, the first few with how many of each: what a failing line says before the list. */
        String kindsLine() {
            StringBuilder s = new StringBuilder(differing + " differ, in " + kinds.size() + (kinds.size() == 1 ? " kind: " : " kinds: "));
            int n = 0;
            for (Map.Entry<String, Integer> e : kinds.entrySet()) {
                if (n++ == 4) { s.append(" (+").append(kinds.size() - 4).append(" more)"); break; }
                s.append(n == 1 ? "" : ", ").append(e.getKey()).append(" x").append(e.getValue());
            }
            return s.toString();
        }
    }

    /** Two saves' files compared leaf by leaf: how many numbers and how many series (arrays of numbers) were compared. */
    static final class Compared extends Findings {
        int numbers, series;
    }

    /** One cause (section 3): what was compared, and what the live city showed the cause at work in (the fixture). */
    static final class Cause extends Findings {
        int compared;
        final Map<String, Integer> saw = new LinkedHashMap<>();

        void saw(String what, int n) { saw.merge(what, n, Integer::sum); }

        void compare(String what, double live, double reloaded) {
            compared++;
            if (!same(live, reloaded)) differ(what, num(live), num(reloaded));
        }

        void compare(String what, String live, String reloaded) {
            compared++;
            if (!java.util.Objects.equals(live, reloaded)) differ(what, String.valueOf(live), String.valueOf(reloaded));
        }
    }

    /** What one checkpoint found: the city saved at {@code month}, reloaded, and both played on. */
    static final class Point {
        final String city;
        final int month;
        /** Why the checkpoint could not be compared (a save or a load that failed), or null. */
        String problem;
        Compared history1, history2, save1, save2;
        final Cause revalued = new Cause(), drawn = new Cause(), tally = new Cause(), notes = new Cause(),
                shares = new Cause(), weights = new Cause(), skipped = new Cause();
        int skips, skipsWithOutbreak;

        Point(String city, int month) { this.city = city; this.month = month; }
    }

    /* --------------------------------- the numbers --------------------------------- */

    static boolean same(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return Double.isNaN(a) && Double.isNaN(b);
        if (a == b) return true;
        double d = Math.abs(a - b);
        return d <= CENT && d <= RELATIVE * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static String num(double v) { return Double.isNaN(v) ? "NaN" : String.valueOf(v); }

    /* ------------------------------- the two files ------------------------------- */

    static JsonElement read(Path file) {
        try {
            return Files.isRegularFile(file) ? JsonParser.parseString(Files.readString(file)) : null;
        } catch (IOException e) {
            return null;
        }
    }

    /** A number, or the strings Gson writes for the ones JSON has no word for. */
    static boolean isNumeric(JsonElement e) {
        if (e == null || !e.isJsonPrimitive()) return false;
        if (e.getAsJsonPrimitive().isNumber()) return true;
        String s = e.getAsString();
        return e.getAsJsonPrimitive().isString() && (s.equals("NaN") || s.equals("Infinity") || s.equals("-Infinity"));
    }

    static double valueOf(JsonElement e) { return e.getAsJsonPrimitive().isNumber() ? e.getAsDouble() : Double.parseDouble(e.getAsString()); }

    static String show(JsonElement e) {
        if (e == null) return "(absent)";
        String s = e.toString();
        return s.length() > 60 ? s.substring(0, 60) + "..." : s;
    }

    /** The live city's file against the reloaded city's, key by key and leaf by leaf; null (a file not there) is itself a difference. */
    static Compared compare(Path live, Path reloaded) {
        Compared c = new Compared();
        JsonElement a = read(live), b = read(reloaded);
        if (a == null || b == null) c.differ(live.getFileName() + " / " + reloaded.getFileName(), a == null ? "(not there)" : "read", b == null ? "(not there)" : "read");
        else walk("", a, b, c);
        return c;
    }

    static void walk(String path, JsonElement a, JsonElement b, Compared c) {
        if (a == null || b == null) {
            if (a != b) c.differ(path, show(a), show(b));
        } else if (a.isJsonObject() && b.isJsonObject()) {
            JsonObject x = a.getAsJsonObject(), y = b.getAsJsonObject();
            Set<String> keys = new TreeSet<>(x.keySet());
            keys.addAll(y.keySet());
            for (String k : keys) {
                if (!EXEMPT.contains(k)) walk(path + "." + k, x.get(k), y.get(k), c);
            }
        } else if (a.isJsonArray() && b.isJsonArray()) {
            JsonArray x = a.getAsJsonArray(), y = b.getAsJsonArray();
            if (x.size() > 0 && isNumeric(x.get(0))) c.series++;
            if (x.size() != y.size()) c.differ(path + ".length", String.valueOf(x.size()), String.valueOf(y.size()));
            for (int i = 0; i < Math.min(x.size(), y.size()); i++) walk(path + "[" + i + "]", x.get(i), y.get(i), c);
        } else if (isNumeric(a) && isNumeric(b)) {
            c.numbers++;
            if (!same(valueOf(a), valueOf(b))) c.differ(path, a.getAsString(), b.getAsString());
        } else if (!a.equals(b)) {
            c.differ(path, show(a), show(b));
        }
    }

    /* ------------------------------ one checkpoint ------------------------------ */

    /**
     * The city {@code g} saved where it stands and loaded into a second game, observed right after the load (the
     * markets' tally, the shares, the price index's weights), then both run a month (the sector books, Construction,
     * the investment notes), saved and compared, a month more and compared again, then two time skips of each.
     * The game's narration is silenced throughout; nothing here asserts - section by section the report does.
     */
    static Point reload(GameFiles files, Game g, String city) {
        Point p = new Point(city, g.getMonth());
        PrintStream real = System.out;
        System.setOut(QUIET);
        try {
            if (!g.saveGame(1, "reloadcheck m" + p.month).ok) { p.problem = "the save of the live city failed"; return p; }
            Game back = new Game(files);
            back.loadGameSave(1);
            if (back.getLoadFailure() != null) { p.problem = "the load failed: " + back.getLoadFailure(); return p; }

            observeTheLoad(p, g, back);
            g.simulateMonths(1);
            back.simulateMonths(1);
            observeAMonthOn(p, g, back);
            Compared[] monthOn = saveAndCompare(files, g, back, "a month on");
            p.history1 = monthOn[0];
            p.save1 = monthOn[1];
            g.simulateMonths(1);
            back.simulateMonths(1);
            Compared[] twoOn = saveAndCompare(files, g, back, "two months on");
            p.history2 = twoOn[0];
            p.save2 = twoOn[1];
            skips(p, g, back);
        } finally {
            System.setOut(real);
        }
        return p;
    }

    /** Both cities saved (the live one to slot 2, the reloaded to slot 3) and their files compared: {the history's, the save's}. */
    private static Compared[] saveAndCompare(GameFiles files, Game g, Game back, String when) {
        boolean ok = g.saveGame(2, "live " + when).ok & back.saveGame(3, "reloaded " + when).ok;
        Compared[] both = { compare(files.historyFile(2), files.historyFile(3)), compare(files.saveFile(2), files.saveFile(3)) };
        if (!ok) for (Compared c : both) c.differ("saving " + when, "a save failed", "a save failed");
        return both;
    }

    /**
     * Right after the load, before any month: (c) every market's tally of the month just closed - the goods imported,
     * exported, filled at home, offered and bid for; the trades list and the month's draws have no getter that is a
     * figure (getTrades() is the fills, monthsDraws() is package-private), so they are not compared - (d) each sector
     * book's shares, this month's and last month's, and (e) the price index's two weights.
     */
    private static void observeTheLoad(Point p, Game g, Game back) {
        for (GoodsMarket live : g.getMarkets().all()) {
            GoodsMarket re = back.getMarkets().get(live.good());
            String good = String.valueOf(live.good());
            p.tally.saw("markets that imported", live.getImported() > 0 ? 1 : 0);
            p.tally.saw("markets that exported", live.getExported() > 0 ? 1 : 0);
            p.tally.compare(good + ": imported", live.getImported(), re == null ? Double.NaN : re.getImported());
            p.tally.compare(good + ": exported", live.getExported(), re == null ? Double.NaN : re.getExported());
            p.tally.compare(good + ": filled at home", live.getLocalFilled(), re == null ? Double.NaN : re.getLocalFilled());
            p.tally.compare(good + ": offered", live.getOffered(), re == null ? Double.NaN : re.getOffered());
            p.tally.compare(good + ": bid", live.getBid(), re == null ? Double.NaN : re.getBid());
        }
        for (Sector s : g.getSectors().all()) {
            String key = s.key();
            p.shares.saw("sectors with shares this month", g.getSectorBooks().shares(key) != null ? 1 : 0);
            p.shares.saw("sectors with shares last month", g.getSectorBooks().sharesBefore(key) != null ? 1 : 0);
            compareShares(p.shares, key + " this month", g.getSectorBooks().shares(key), back.getSectorBooks().shares(key));
            compareShares(p.shares, key + " last month", g.getSectorBooks().sharesBefore(key), back.getSectorBooks().sharesBefore(key));
        }
        p.weights.saw("weights above zero", g.getPriceIndex().getRentWeight() > 0 && g.getPriceIndex().getFoodWeight() > 0 ? 1 : 0);
        p.weights.compare("the price index: rent weight", g.getPriceIndex().getRentWeight(), back.getPriceIndex().getRentWeight());
        p.weights.compare("the price index: food weight", g.getPriceIndex().getFoodWeight(), back.getPriceIndex().getFoodWeight());
    }

    private static void compareShares(Cause c, String what, SectorBooks.Shares live, SectorBooks.Shares re) {
        if (live == null || re == null) {
            c.compared++;
            if (live != re) c.differ(what + ": shares", live == null ? "not counted" : live.toString(), re == null ? "not counted" : re.toString());
            return;
        }
        c.compare(what + ": shares", live.shares(), re.shares());
        c.compare(what + ": outstanding", live.outstanding(), re.outstanding());
        c.compare(what + ": price", live.price(), re.price());
        c.compare(what + ": fair value", live.fair(), re.fair());
        c.compare(what + ": dividend", live.dividend(), re.dividend());
        c.compare(what + ": dividend a year", live.dividendYear(), re.dividendYear());
        c.compare(what + ": traded", String.valueOf(live.traded()), String.valueOf(re.traded()));
    }

    /**
     * A month on: (a) each sector book's stock revaluation and whether it was counted, (b) Construction's materials
     * drawn at home and imported in the month last struck, and (c) the investment notes the investors left.
     */
    private static void observeAMonthOn(Point p, Game g, Game back) {
        for (Sector s : g.getSectors().all()) {
            SectorBooks.SectorMonth live = g.getSectorBooks().get(s.key()), re = back.getSectorBooks().get(s.key());
            if (live == null || re == null) {
                p.revalued.compared++;
                if (live != re) p.revalued.differ(s.key() + ": its month", live == null ? "no month" : "a month", re == null ? "no month" : "a month");
                continue;
            }
            p.revalued.saw("sectors that counted a revaluation", live.stockCounted() ? 1 : 0);
            p.revalued.saw("...of them one not zero", live.stockCounted() && live.stockRevalued() != 0 ? 1 : 0);
            p.revalued.compare(s.key() + ": stockCounted", String.valueOf(live.stockCounted()), String.valueOf(re.stockCounted()));
            p.revalued.compare(s.key() + ": stockRevalued", SectorStatements.stockRevalued(live), SectorStatements.stockRevalued(re));
        }
        Construction live = g.getSectors().construction(), re = back.getSectors().construction();
        p.drawn.saw("crews that drew materials", live.getDrawnLocal() + live.getDrawnImported() > 0 ? 1 : 0);
        p.drawn.compare("Construction: isDrawnKnown", String.valueOf(live.isDrawnKnown()), String.valueOf(re.isDrawnKnown()));
        p.drawn.compare("Construction: drawn at home", live.getDrawnLocal(), re.getDrawnLocal());
        p.drawn.compare("Construction: drawn imported", live.getDrawnImported(), re.getDrawnImported());

        Map<String, String> liveNotes = g.getLastInvestments(), reNotes = back.getLastInvestments();
        p.notes.saw("investment notes the live city left", liveNotes.size());
        Set<String> keys = new TreeSet<>(liveNotes.keySet());
        keys.addAll(reNotes.keySet());
        for (String k : keys) p.notes.compare(k + ": lastInvestment", liveNotes.get(k), reNotes.get(k));
    }

    /**
     * (f) Two skips run on each city from the same month, and the second report's health figures compared. Whether a skip
     * had an outbreak is read off the reloaded city's reports, not the live city's (which carry every skip it has made):
     * its first report is its first skip's alone, and its second skip had one when the count rose, or fell and is not
     * zero (a report that starts afresh). Equal counts are read as none.
     */
    private static void skips(Point p, Game g, Game back) {
        g.simulateMonths(SKIP_MONTHS);
        back.simulateMonths(SKIP_MONTHS);
        int first = outbreakMonths(back.getSkipReport());
        g.simulateMonths(SKIP_MONTHS);
        back.simulateMonths(SKIP_MONTHS);
        int second = outbreakMonths(back.getSkipReport());
        TimeSkipReport live = g.getSkipReport(), re = back.getSkipReport();
        p.skips = 2;
        p.skipsWithOutbreak = (first > 0 ? 1 : 0) + ((second < first ? second > 0 : second > first) ? 1 : 0);
        p.skipped.compare("the report: outbreaks", live.getOutbreaks(), re.getOutbreaks());
        p.skipped.compare("the report: months in outbreak", live.getMonthsInOutbreak(), re.getMonthsInOutbreak());
        p.skipped.compare("the report: months sick", live.getMonthsSick(), re.getMonthsSick());
        p.skipped.compare("the report: worst work ratio", live.getWorstWorkRatio(), re.getWorstWorkRatio());
        p.skipped.compare("the report: peak unburied", live.getPeakUnburied(), re.getPeakUnburied());
    }

    private static int outbreakMonths(TimeSkipReport report) { return report.getOutbreaks() + report.getMonthsInOutbreak(); }

    /* ----------------------------------- the report ----------------------------------- */

    /** One labelled assertion, printed either way; a failing one lists, under its line, what differs. */
    static void check(String label, boolean ok, String detail, List<String> more) {
        if (!ok) fails++;
        System.out.printf("%-112s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
        if (!ok && more != null) for (String line : more) System.out.println("      " + line);
    }

    static void check(String label, boolean ok, String detail) { check(label, ok, detail, null); }

    /** An assertion that nothing differs: the first difference on its line, the others (to SHOWN) under it, the rest counted. */
    static void nothingDiffers(String label, Findings f, String what) {
        boolean ok = f.differing == 0;
        List<String> more = new ArrayList<>(f.first);
        if (f.differing > f.first.size()) more.add("... and " + (f.differing - f.first.size()) + " more");
        check(label, ok, ok ? what : f.kindsLine(), more);
    }

    static String seen(Map<String, Integer> saw) {
        StringBuilder s = new StringBuilder();
        saw.forEach((k, v) -> s.append(s.length() == 0 ? "" : ", ").append(v).append(" ").append(k));
        return s.toString();
    }

    static Map<String, Integer> sawTogether(List<Point> points, Function<Point, Cause> cause) {
        Map<String, Integer> all = new LinkedHashMap<>();
        for (Point p : points) if (p.problem == null) cause.apply(p).saw.forEach((k, v) -> all.merge(k, v, Integer::sum));
        return all;
    }

    static boolean allAbove0(Map<String, Integer> saw) {
        return !saw.isEmpty() && saw.values().stream().allMatch(v -> v > 0);
    }

    static String when(Point p, int monthsOn) { return p.city + " month " + p.month + ", reloaded and " + (monthsOn == 1 ? "a month on" : "two months on"); }

    /** Sections 1 and 4's history lines: the fixture line first, then each checkpoint a month on and two months on. */
    static void history(List<Point> points) {
        int fewest = Integer.MAX_VALUE;
        for (Point p : points) if (p.problem == null) fewest = Math.min(fewest, Math.min(p.history1.series, p.history2.series));
        check("fixture: the history holds more than a hundred series with the month's entry, at every checkpoint",
                fewest != Integer.MAX_VALUE && fewest > 100, "fewest " + fewest);
        for (Point p : points) {
            if (p.problem != null) {
                check(p.city + " month " + p.month + ": the city was saved and loaded", false, p.problem);
                continue;
            }
            for (int on = 1; on <= 2; on++) {
                Compared c = on == 1 ? p.history1 : p.history2;
                nothingDiffers(when(p, on) + ": every history figure is the live city's", c, c.numbers + " numbers in " + c.series + " series, the same");
            }
        }
    }

    /** Sections 2 and 4's whole-save lines. */
    static void whole(List<Point> points) {
        int fewest = Integer.MAX_VALUE;
        for (Point p : points) if (p.problem == null) fewest = Math.min(fewest, Math.min(p.save1.numbers, p.save2.numbers));
        check("fixture: the save holds over 10,000 numbers, at every checkpoint", fewest != Integer.MAX_VALUE && fewest > 10_000, "fewest " + fewest);
        for (Point p : points) {
            if (p.problem != null) continue;
            for (int on = 1; on <= 2; on++) {
                Compared c = on == 1 ? p.save1 : p.save2;
                nothingDiffers(when(p, on) + ": every figure in the save is the live city's, to the cent", c, c.numbers + " numbers the same");
            }
        }
    }

    static void section3(List<Point> points) {
        System.out.println("--- 3. the causes, one by one ---");
        cause(points, p -> p.revalued, "3a", "a month on", "every sector's stock revaluation is counted in the reload's first month and equals the live city's");
        cause(points, p -> p.drawn, "3b", "a month on", "Construction's materials drawn at home and imported equal the live city's");
        cause(points, p -> p.tally, "3c, the tally", "right after the load",
                "every market's month tally (imported, exported, filled at home, offered, bid) is the live city's");
        cause(points, p -> p.notes, "3c, the notes", "a month on", "each sector's lastInvestment note is the live city's");
        cause(points, p -> p.shares, "3d", "right after the load", "each sector book's shares month, this and last, is the live city's");
        cause(points, p -> p.weights, "3e", "right after the load", "the price index's rent and food weights are the live city's");
        int skips = 0, withOutbreak = 0;
        for (Point p : points) if (p.problem == null) { skips += p.skips; withOutbreak += p.skipsWithOutbreak; }
        check("fixture 3f: time skips were run on both cities from the same month", skips > 0, skips + " skips, " + withOutbreak + " with an outbreak (read off the reloaded city's reports)");
        for (Point p : points) {
            if (p.problem != null) continue;
            String over = p.skipsWithOutbreak == 0 ? "no outbreak in either of the " + p.skips + " skips, so only the sick months and the ratios were tried"
                    : p.skipsWithOutbreak + " of " + p.skips + " skips with an outbreak";
            nothingDiffers("3f. " + p.city + " month " + p.month + ", the second time skip after the load reports what the running city's does (" + over
                    + "): outbreaks, months in outbreak, months sick, worst work ratio, peak unburied", p.skipped, p.skipped.compared + " figures the same");
        }
    }

    /** One cause: the fixture line (the live city showed it at work, in every way named), then its parity at each checkpoint. */
    static void cause(List<Point> points, Function<Point, Cause> which, String id, String when, String label) {
        Map<String, Integer> saw = sawTogether(points, which);
        check("fixture " + id + ": the live city exercised it", allAbove0(saw), seen(saw));
        for (Point p : points) {
            if (p.problem != null) continue;
            Cause c = which.apply(p);
            nothingDiffers(id + ". " + p.city + " month " + p.month + ", " + when + ": " + label, c, c.compared + " compared, the same");
        }
    }

    /* ------------------------------------ the Earth ------------------------------------ */

    /** Whether section 4 can run: earth-2.bin named (-Dcbs.earth, or at data/earth-2.bin) and the game reading it; says so in one line where not. */
    static boolean earthHere() {
        Path p = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && (p == null || !Files.isRegularFile(p))) {
            check("the file -D" + EarthData.PROPERTY + " names is there", false, named);
            return false;
        }
        if (p == null) {
            System.out.println("\nearth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": section 4, a city on the real Earth, was not run");
            return false;
        }
        EarthSite.Data game = EarthSite.gameData();
        if (!game.here() || !game.reader().identity().equals(EarthData.EARTH_2)) {
            System.out.println("\nthe game's Earth is not earth-2 (" + game.words() + "): section 4 was not run");
            return false;
        }
        return true;
    }

    /* -------------------------------------- the run -------------------------------------- */

    public static void main(String[] args) {
        long started = System.nanoTime();
        List<Point> points = new ArrayList<>();
        Game g = LongPlaytest.probeCity(GameFiles.scratch("reloadcheck"), null);
        GameFiles files = g.getGameFiles();
        for (int target : CHECKPOINTS) {
            long t0 = System.nanoTime();
            LongPlaytest.probePlay(g, target);
            points.add(reload(files, g, "Danzik"));
            System.out.printf("  played, saved, reloaded and compared at month %d (%.0f s)%n", points.get(points.size() - 1).month, (System.nanoTime() - t0) / 1e9);
        }
        Point earth = null;
        if (earthHere()) {
            GameFiles earthFiles = GameFiles.scratch("reloadcheck-earth");
            Game e = LongPlaytest.probeCity(earthFiles, EARTH_SPOT);
            LongPlaytest.probePlay(e, EARTH_CHECKPOINT);
            earth = reload(earthFiles, e, "Rotterdam");
            System.out.println("  the Earth city played, saved, reloaded and compared at month " + earth.month);
        }

        System.out.println("--- 1. a played city, reloaded, plays its next month as the one that kept running: every history series ---");
        history(points);
        System.out.println("--- 2. ...and every figure in its save, to the cent ---");
        whole(points);
        section3(points);
        if (earth != null) {
            System.out.println("--- 4. on the real Earth ---");
            history(List.of(earth));
            whole(List.of(earth));
        }

        System.out.printf("%n%.0f s%n", (System.nanoTime() - started) / 1e9);
        System.out.println(fails == 0 ? "All checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
