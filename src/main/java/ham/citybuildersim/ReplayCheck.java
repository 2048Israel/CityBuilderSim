package ham.citybuildersim;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Replay of a recorded city (batch TR1, 2026-10-31): the recorder, the throwaway copy, the input log, and what recording costs.
 * Timing harness (Timing.HARNESSES): section 6 judges the recorder's FX-thread cost through Timing.
 *
 * What it has to prove (the numbers are the brief's section 7, docs/queue/TR1.md):
 *   1. a recording writes one month each, and a rewind rewrites from the next: a grown city, "Record this city" on, played 24
 *      months, has 24 month files, the meta and the history, and nothing under saves/ but the autosave's and the slot the check
 *      saved to; loaded back at month 12 (the save carries the switch and the id) and played 3 months with the income tax moved,
 *      the months past 15 are gone, 13 to 15 are rewritten (13's save carries the new rate) and 1 to 12 are byte for byte what
 *      they were; a city founded after that records nothing; and a writer held behind by the backlog never holds the clock;
 *   2. replaying month N gives the figures the city had at N: population, cash, GDP, the price level and the map's building
 *      count, at the first, a middle and the last month; a quick step swaps the map to N's count and nothing else; the
 *      recording's history cut at a month is the history the city had then, series by series, and a cut is right across the
 *      yearly fold and the finished years of trade;
 *   3. nothing sticks: a rate changed on the copy is the recorded one after a step away and back, the copy records nothing and
 *      lives in a folder of its own, and the recording's files and the player's folder are byte for byte unchanged;
 *   4. the input log round-trips - every kind, event for event, the moves closer than MOVE_EVERY_MS dropped, the sittings oldest
 *      first, a file cut mid-record reads back a prefix, a sitting still open reads up to its last flush - and a re-enacted Save,
 *      Load and Quit on the copy writes nothing outside the copy's own folder;
 *   5. (the camera's arithmetic: the next slice's, in this file);
 *   6. recording costs the month only its fill and changes nothing in it: two same cities played 12 months, one recording, agree
 *      to the last digit; the recorder's FX-thread cost per month is within SaveWriter.CLOCK_MS (printed, with the size of a month's
 *      file and of the history, and of a month at deflate level 6 and 1).
 */
public class ReplayCheck {

    static int fails = 0;

    /** The month the section 1 to 4 city is grown to before it is recorded. */
    static final int GROWN = 48;

    /** The month the section 6 cities are grown to. */
    static final int GROWN_SIX = 36;

    /** An assertion's line: Timing.verdict says OK, FAIL, or SOFT with the figure and its budget. */
    static void helper(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-96s %s%n", label, Timing.verdict(ok, label));
    }

    /** What the city shows at a month's end: the figures the replay has to give back. */
    record Figures(long population, double gdp, double cash, double price, long buildings) { }

    static Figures figuresOf(Game g) {
        long buildings = 0;
        for (long n : g.getCityMap().totals()) buildings += n;
        return new Figures(g.getPopulationManager().getPopulation(), g.getEconomyManager().getNationalAccounts().getGdp(),
                g.getCash(), g.getPriceIndex().getIndex(), buildings);
    }

    /** Whether two figures agree, and which differ when they do not (the words for the line). */
    static String parted(Figures got, Figures want) {
        StringBuilder sb = new StringBuilder();
        if (got.population != want.population) sb.append(" population ").append(got.population).append(" vs ").append(want.population);
        if (Double.compare(got.gdp, want.gdp) != 0) sb.append(" gdp ").append(got.gdp).append(" vs ").append(want.gdp);
        if (Double.compare(got.cash, want.cash) != 0) sb.append(" cash ").append(got.cash).append(" vs ").append(want.cash);
        if (Double.compare(got.price, want.price) != 0) sb.append(" price ").append(got.price).append(" vs ").append(want.price);
        if (got.buildings != want.buildings) sb.append(" buildings ").append(got.buildings).append(" vs ").append(want.buildings);
        return sb.toString();
    }

    /** Every file under a folder: its path from the folder, its size and its SHA-256. */
    static Map<String, String> hashes(Path dir) throws Exception {
        Map<String, String> out = new TreeMap<>();
        if (!Files.isDirectory(dir)) return out;
        try (var walk = Files.walk(dir)) {
            for (Path p : (Iterable<Path>) walk.filter(Files::isRegularFile).sorted()::iterator) {
                byte[] bytes = Files.readAllBytes(p);
                byte[] sum = MessageDigest.getInstance("SHA-256").digest(bytes);
                out.put(dir.relativize(p).toString().replace('\\', '/'), bytes.length + ":" + java.util.HexFormat.of().formatHex(sum));
            }
        }
        return out;
    }

    /** The first difference between two maps of hashes, in words, or null when they are the same. */
    static String firstDifference(Map<String, String> before, Map<String, String> after) {
        for (Map.Entry<String, String> e : before.entrySet()) {
            if (!after.containsKey(e.getKey())) return "gone: " + e.getKey();
            if (!after.get(e.getKey()).equals(e.getValue())) return "changed: " + e.getKey();
        }
        for (String k : after.keySet()) if (!before.containsKey(k)) return "new: " + k;
        return null;
    }

    /** Plays one month on the clock's path with the month's printed report silenced: the city prints its reports to the log. */
    static void clockMonth(Game game) {
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try {
            game.toggleNextMonth();
        } finally {
            System.setOut(real);
        }
    }

    static Map<Integer, byte[]> monthBytes(Recording rec) throws IOException {
        Map<Integer, byte[]> out = new TreeMap<>();
        for (int m : rec.months()) out.put(m, Files.readAllBytes(rec.monthFile(m)));
        return out;
    }

    public static void main(String[] args) throws Exception {

        GameFiles files = GameFiles.scratch("replaycheck");
        long grownAt = System.nanoTime();
        Game g = SaveSlotCheck.grownCity(files, GROWN);
        System.out.printf("      fixture: a city grown to month %d in %.1f s, %,d people%n", g.getMonth(),
                (System.nanoTime() - grownAt) / 1e9, g.getPopulationManager().getPopulation());

        /* ======== 1. a recording writes one month each, and a rewind rewrites from the next ======== */
        System.out.println("--- a recording writes one month each, and a rewind rewrites from the next ---");

        helper("fixture: the grown city has a map and is not recording", g.hasCityMap() && !g.isRecording() && g.getRecordingId() == null);
        helper("...and a replay copy is not what it is", !g.isReplayCopy());
        int m0 = g.getMonth();
        g.setRecording(true);
        String id = g.getRecordingId();
        helper("switched on, the city has an id, file-safe, and its folder is under recordings/ beside saves/",
                id != null && id.matches("[a-z0-9-]+-\\d{8}-\\d{6}") && g.isRecording()
                        && g.recordingFolder().equals(files.recordingsDirectory().resolve(id))
                        && files.recordingsDirectory().getParent().equals(files.savesDirectory().getParent())
                        && GameFiles.RECORDINGS_FOLDER.equals(files.recordingsDirectory().getFileName().toString()));
        helper("an id is made from the name's letters and digits, without accents, and a name with none gives city",
                Recording.newId("Medellín", 0).startsWith("medellin-") && Recording.newId("  !! ", 0).startsWith("city-")
                        && Recording.newId("New  York, NY", 0).startsWith("new-york-ny-") && Recording.newId(null, 0).startsWith("city-"));

        Map<Integer, Figures> kept = new TreeMap<>();
        HistorySave historyAt12 = null;
        int monthAt12 = -1;
        boolean everyMonthTurned = true;
        for (int i = 1; i <= 24; i++) {
            clockMonth(g);
            g.awaitSaves();
            everyMonthTurned &= g.getMonth() == m0 + i;
            kept.put(g.getMonth(), figuresOf(g));
            if (i == 12) {
                historyAt12 = g.getHistorySave().snapshot();
                monthAt12 = g.getMonth();
                g.saveGame(ReplayCity.SLOT, "replaycheck");
            }
        }
        helper("fixture: the clock turned the month 24 times", everyMonthTurned && g.getMonth() == m0 + 24 && monthAt12 == m0 + 12);

        Recording rec = Recording.at(g.recordingFolder());
        int[] months = rec.months();
        boolean consecutive = months.length == 24;
        for (int i = 0; consecutive && i < months.length; i++) consecutive = months[i] == m0 + 1 + i;
        helper("24 months played, 24 month files: m0+1 to m0+24, none missing and none twice", consecutive);
        helper("...first and last month are the listing's ends", rec.firstMonth() == m0 + 1 && rec.lastMonth() == m0 + 24);
        helper("...the meta says whose it is: the id, the city, the build, the format, and when it was started",
                rec.meta() != null && id.equals(rec.meta().id()) && g.getCityName().equals(rec.meta().cityName())
                        && GameVersion.VERSION.equals(rec.meta().version()) && rec.meta().saveFormat() == GameVersion.SAVE_FORMAT
                        && rec.meta().startedAt() > 0 && rec.meta().earth() == g.getFounding().getWorldKey().earth());
        helper("...and the recording is listed, with the folder's id", Recording.all(files.recordingsDirectory()).size() == 1
                && Recording.all(files.recordingsDirectory()).get(0).id().equals(id) && Recording.all(files.getDirectory().resolve("nowhere")).isEmpty());
        HistorySave firstHistory = rec.history();
        helper("...and the history is there from the first month (written with it)", firstHistory != null && firstHistory.months() > 0);
        helper("...a month's file has the head, its map and its save: the map and the JSON read back for every month",
                rec.mapOf(m0 + 1) != null && CityMap.stampIn(rec.mapOf(m0 + 1)) != 0 && rec.saveJsonOf(m0 + 5) != null
                        && rec.saveJsonOf(m0 + 5).startsWith("{") && new Gson().fromJson(rec.saveJsonOf(m0 + 5), DataSave.class).getMonth() == m0 + 5
                        && rec.mapOf(m0 + 999) == null && rec.saveJsonOf(m0 + 999) == null);

        g.autosave("replaycheck");
        g.awaitSaves();
        HistorySave autosaved = rec.history();
        helper("the autosave hands the recording its history, from the snapshot it made: now through the last month",
                autosaved != null && autosaved.getMonth().get(autosaved.months() - 1) == m0 + 24);

        Set<String> strays = new TreeSet<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(files.savesDirectory())) {
            for (Path p : ds) {
                String name = p.getFileName().toString();
                if (!name.startsWith("autosave") && !name.startsWith("slot-10")) strays.add(name);
            }
        }
        helper("nothing under saves/ but the autosave's files and slot 10, the slot the check saved to" + (strays.isEmpty() ? "" : " " + strays),
                strays.isEmpty() && !rec.folder().startsWith(files.savesDirectory()));

        Map<Integer, byte[]> before = monthBytes(rec);
        g.loadGameSave(ReplayCity.SLOT);
        helper("loaded the save from m0+12: the city is back there, recording is on again, and it is the same id",
                g.getMonth() == m0 + 12 && g.isRecording() && id.equals(g.getRecordingId()));
        double wasRate = g.getEconomyManager().getTaxPolicy().getIncomeTaxRate();
        g.getEconomyManager().getTaxPolicy().setIncomeTaxRate(wasRate + 0.0123);
        double newRate = g.getEconomyManager().getTaxPolicy().getIncomeTaxRate();
        helper("fixture: the income tax moved off what the recording has", Math.abs(newRate - wasRate) > 1e-9);
        for (int i = 1; i <= 3; i++) {
            clockMonth(g);
            g.awaitSaves();
            kept.put(g.getMonth(), figuresOf(g));
        }
        kept.keySet().removeIf(m -> m > m0 + 15);
        Map<Integer, byte[]> after = monthBytes(rec);
        helper("played 3 months on: the months past m0+15 are gone, m0+1 to m0+15 are what is left",
                after.keySet().equals(new TreeSet<>(before.keySet()).headSet(m0 + 16)) && after.size() == 15);
        boolean untouched = true;
        for (int m = m0 + 1; m <= m0 + 12; m++) untouched &= Arrays.equals(before.get(m), after.get(m));
        helper("...months up to m0+12 are untouched: byte for byte what they were", untouched);
        boolean rewritten = true;
        for (int m = m0 + 13; m <= m0 + 15; m++) rewritten &= !Arrays.equals(before.get(m), after.get(m));
        helper("...m0+13 to m0+15 are rewritten (the city played them again, with another rate)", rewritten);
        DataSave at13 = new Gson().fromJson(rec.saveJsonOf(m0 + 13), DataSave.class);
        DataSave at12 = new Gson().fromJson(rec.saveJsonOf(m0 + 12), DataSave.class);
        helper("...and month m0+13's save carries the new rate, m0+12's the old one",
                Double.compare(at13.getIncomeTaxRate(), newRate) == 0 && Double.compare(at12.getIncomeTaxRate(), wasRate) == 0);
        boolean noTmp;
        try (var listing = Files.list(rec.folder())) {
            noTmp = listing.noneMatch(p -> p.getFileName().toString().endsWith(".tmp"));
        }
        helper("...and no file of the recording is a leftover .tmp: the folder holds month files, .bak copies, the meta, the history", noTmp);

        g.setRecording(false);
        g.awaitSaves();
        HistorySave stopped = rec.history();
        helper("switching off writes the history now: through m0+15, the month the timeline ends on",
                !g.isRecording() && stopped != null && stopped.getMonth().get(stopped.months() - 1) == m0 + 15);
        int filesBefore = rec.months().length;
        clockMonth(g);
        g.awaitSaves();
        helper("...and a month played with it off is not recorded", rec.months().length == filesBefore && g.getMonthsNotRecorded() == 0);

        java.io.PrintStream realOut = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try {
            g.newGame();
        } finally {
            System.setOut(realOut);
        }
        helper("a city founded after that records nothing: no switch, no id", !g.isRecording() && g.getRecordingId() == null && g.recordingFolder() == null);
        clockMonth(g);
        g.awaitSaves();
        helper("...and plays a month without adding to the old city's recording", rec.months().length == filesBefore && Recording.all(files.recordingsDirectory()).size() == 1);

        /* ==== 2. replaying month N gives the figures the city had at N ==== */
        System.out.println("\n--- replaying month N gives the figures the city had at N ---");

        ReplayCity replay = new ReplayCity(rec);
        int[] have = rec.months();
        int first = have[0], middle = have[have.length / 2], last = have[have.length - 1];
        helper("the replay's months are the recording's, and a month it has not is a gap: nearest() finds the closest",
                Arrays.equals(replay.months(), have) && replay.nearest(first - 40) == first && replay.nearest(last + 40) == last
                        && replay.nearest(middle) == middle && replay.shown() == -1 && replay.game() == null);
        helper("a quick step before any load does nothing", !replay.stepQuick(first) && replay.shown() == -1);
        for (int n : new int[] {first, middle, last}) {
            String failure = replay.loadWhole(n);
            Figures got = replay.game() == null ? null : figuresOf(replay.game());
            String parted = got == null ? " no copy" : parted(got, kept.get(n));
            helper("month m0+" + (n - m0) + " loads whole, and its population, GDP, cash, price level and map are the city's then"
                    + (parted.isEmpty() ? "" : " -" + parted), failure == null && replay.shown() == n && replay.wholeMonth() && parted.isEmpty());
            System.out.printf("      a whole load of month m0+%d: %.0f ms%n", n - m0, replay.lastLoadMs());
        }
        helper("the copy is a replay copy that never records, whatever the save it loaded said",
                replay.game().isReplayCopy() && !replay.game().isRecording() && rec.saveJsonOf(last).contains("\"recording\":true"));

        replay.loadWhole(first);
        boolean stepped = replay.stepQuick(middle);
        long mapAfterStep = 0;
        for (long n : replay.game().getCityMap().totals()) mapAfterStep += n;
        helper("a quick step to a middle month swaps the map: its count is that month's, and the copy says it is not whole",
                stepped && replay.shown() == middle && !replay.wholeMonth() && mapAfterStep == kept.get(middle).buildings);
        helper("...and only the map: the people are still the first month's until the month is loaded whole",
                replay.game().getPopulationManager().getPopulation() == kept.get(first).population);
        System.out.printf("      a quick step of the map: %.1f ms%n", replay.lastStepMs());
        helper("a month with no file is not stepped to", !replay.stepQuick(last + 500) && replay.shown() == middle);
        helper("...and the month loaded whole after the step is the middle month's figures",
                replay.loadWhole(middle) == null && parted(figuresOf(replay.game()), kept.get(middle)).isEmpty() && replay.wholeMonth());
        helper("...and a month that is not in the recording is refused in words, the copy as it was",
                replay.loadWhole(last + 500) != null && replay.shown() == middle);

        HistorySave cut = replay.historyAt(monthAt12);
        boolean sameSeries = cut.seriesByName().keySet().equals(historyAt12.seriesByName().keySet()) && cut.getMonth().equals(historyAt12.getMonth())
                && cut.yearlyPoints() == historyAt12.yearlyPoints() && cut.tradeYears() == historyAt12.tradeYears();
        String firstPart = null;
        for (String name : historyAt12.seriesByName().keySet()) {
            if (!Arrays.equals(cut.aligned(name), historyAt12.aligned(name))) { firstPart = name; break; }
        }
        helper("the recording's history cut at m0+12 is the history the city had then: the axis, and all "
                + historyAt12.seriesByName().size() + " series, aligned" + (firstPart == null ? "" : " - parted at " + firstPart),
                sameSeries && firstPart == null && cut.months() == historyAt12.months());
        HistorySave master = replay.master();
        helper("...the master is parsed once and a cut after the last month is a whole snapshot",
                master == replay.master() && replay.historyAt(last + 99).months() == master.months() && replay.historyAt(first - 99).months() == 0
                        && master.getMonth().get(master.months() - 1) == m0 + 15);
        replay.showHistoryAt(monthAt12);
        helper("...and the copy's charts take it: the copy's history ends at the month shown",
                replay.game().getHistorySave().getMonth().get(replay.game().getHistorySave().months() - 1) == monthAt12);

        // A history long enough to have folded years: a cut keeps a folded year (it sits at its last month) or drops it whole,
        // and the finished years of trade are those before the month's year.
        HistorySave longer = new HistorySave();
        int total = HistorySave.MONTHLY_KEPT + 60;
        for (int m = 1; m <= total; m++) {
            final double v = m;
            longer.appendMonth(m, name -> v);
        }
        longer.setTradeByPartner(List.of(new PartnerTrade.Table(2001, null), new PartnerTrade.Table(2002, null),
                new PartnerTrade.Table(2005, null), new PartnerTrade.Table(2010, null)));
        helper("fixture: a history past MONTHLY_KEPT has its oldest five years folded, and all its months", longer.yearlyPoints() == 5 && longer.monthsLived() == total);
        HistorySave among = longer.cutAt(12 * 5 + 3);
        double[] gdpAll = longer.raw("gdp"), gdpAmong = among.raw("gdp");
        boolean prefix = gdpAmong.length == among.months() && among.months() == 8;
        for (int i = 0; prefix && i < gdpAmong.length; i++) prefix = Double.compare(gdpAmong[i], gdpAll[i]) == 0;
        helper("a cut among the months after the folded years: the five years and three months, every series their prefix",
                among.yearlyPoints() == 5 && among.monthsLived() == 63 && among.getMonth().get(among.months() - 1) == 63 && prefix);
        helper("...the finished years of trade are those before the month's year: 2001 and 2002 for a cut in 2005",
                among.tradeYears() == 2 && among.getTradeByPartner().get(1).year() == 2002 && longer.tradeYears() == 4);
        HistorySave inside = longer.cutAt(30);
        helper("a cut inside a folded year drops that year whole: month 30 keeps the two years that ended by it, 2000 and 2001",
                inside.months() == 2 && inside.yearlyPoints() == 2 && inside.monthsLived() == 24 && inside.getMonth().get(1) == 24
                        && inside.raw("cash").length == 2 && inside.tradeYears() == 1 && inside.getTradeByPartner().get(0).year() == 2001);
        HistorySave newest = longer.cutAt(total - 10);
        helper("a cut among the newest months drops them: ten fewer entries, the folded years as they were, every series as long as the axis",
                newest.yearlyPoints() == longer.yearlyPoints() && newest.months() == longer.months() - 10 && newest.raw("cash").length == newest.months()
                        && longer.months() == longer.raw("cash").length);
        helper("...and a cut at the last month is a whole snapshot, the trade years too, and the history it was cut from is as it was",
                longer.cutAt(total).months() == longer.months() && longer.cutAt(total).tradeYears() == 4 && longer.months() == longer.raw("gdp").length
                        && longer.tradeYears() == 4 && longer.monthsLived() == total);

        /* ======================= 3. nothing sticks ======================= */
        System.out.println("\n--- nothing sticks ---");

        Map<String, String> playerBefore = hashes(files.getDirectory());
        Path copyDir = replay.files().getDirectory();
        helper("the copy lives in a folder of its own: not the player's folder, not the recording's",
                !copyDir.startsWith(files.getDirectory()) && !files.getDirectory().startsWith(copyDir) && !copyDir.startsWith(rec.folder())
                        && !playerBefore.isEmpty() && playerBefore.keySet().stream().anyMatch(k -> k.startsWith("recordings/")));
        DataSave recordedMiddle = new Gson().fromJson(rec.saveJsonOf(middle), DataSave.class);
        replay.loadWhole(middle);
        TaxPolicy policy = replay.game().getEconomyManager().getTaxPolicy();
        helper("on the copy at the middle month, the rate is the recorded one",
                Double.compare(policy.getIncomeTaxRate(), recordedMiddle.getIncomeTaxRate()) == 0);
        policy.setIncomeTaxRate(policy.getIncomeTaxRate() + 0.02);
        helper("...a click may change the copy", Math.abs(replay.game().getEconomyManager().getTaxPolicy().getIncomeTaxRate() - recordedMiddle.getIncomeTaxRate()) > 1e-9);
        helper("...a step to the next month loads it whole", replay.loadWhole(middle + 1) == null && replay.shown() == middle + 1);
        helper("...and back: the rate is the recorded one, nothing stuck",
                replay.loadWhole(middle) == null && Double.compare(replay.game().getEconomyManager().getTaxPolicy().getIncomeTaxRate(), recordedMiddle.getIncomeTaxRate()) == 0
                        && parted(figuresOf(replay.game()), kept.get(middle)).isEmpty());
        replay.game().setRecording(true);
        helper("the copy records nothing: switching it on leaves it off", !replay.game().isRecording() && replay.game().isReplayCopy());
        clockMonth(replay.game());
        replay.game().awaitSaves();
        helper("...and a month played on the copy writes no month into the recording",
                Arrays.equals(rec.months(), have));
        replay.loadWhole(middle);
        String hurt = firstDifference(playerBefore, hashes(files.getDirectory()));
        helper("the player's folder (the saves and the recording in it) is byte for byte what it was" + (hurt == null ? "" : " - " + hurt), hurt == null);

        /* ============ 4. the input log round-trips; a re-enacted Save, Load, Quit writes nothing ============ */
        System.out.println("\n--- the input log round-trips, and a re-enacted Save, Load or Quit writes nothing ---");

        Path logs = TempDirs.create("replaylogs");
        long sat = 1_761_900_000_000L;
        List<InputLog.Event> all = new ArrayList<>(), expected = new ArrayList<>();
        InputLog.Writer log = InputLog.open(logs, id, sat);
        helper("a sitting's log is named for the moment it began, in the recording's folder naming",
                log.path().getFileName().toString().equals(Recording.INPUT_PREFIX + sat + Recording.INPUT_SUFFIX));
        long gap = InputLog.MOVE_EVERY_MS;
        InputLog.Event[][] script = {
            {InputLog.Event.move(0, 10.5, 20.25)},                                          // kept: the first
            {InputLog.Event.move(gap / 3, 11, 21)},                                         // dropped: inside the gap
            {InputLog.Event.move(gap, 12.5, 22.5)},                                         // kept: exactly the gap after the last kept
            {InputLog.Event.drag(gap + 4, 13, 23, 1)},                                      // dropped
            {InputLog.Event.drag(3 * gap, 14.5, 24.5, 1)},                                  // kept: drags share the move's clock
            {InputLog.Event.press(3 * gap + 1, 14.5, 24.5, 1, 2, InputLog.SHIFT | InputLog.CTRL)},
            {InputLog.Event.release(4 * gap, 14.5, 24.5, 1, InputLog.ALT | InputLog.META)},
            {InputLog.Event.wheel(4 * gap + 2, 400.25, 300.75, 0, -40, InputLog.CTRL)},
            {InputLog.Event.key(5 * gap, true, InputLog.keyCode("SPACE"), 0, null)},
            {InputLog.Event.key(5 * gap + 1, false, InputLog.keyCode("SPACE"), 0, null)},
            {InputLog.Event.key(6 * gap, true, InputLog.keyCode("A"), InputLog.SHIFT, "A é€")},
            {InputLog.Event.window(7 * gap, 1280, 720)},
            {InputLog.Event.screen(7 * gap + 1, "Budget")},
            {InputLog.Event.mapView(8 * gap, true, 6.25, -75.5, 0.0123456789, 1.5)},
            {InputLog.Event.mapView(8 * gap + 1, false, 1234.5678, -987.125, 3.0, 0.0)},
            {InputLog.Event.clock(9 * gap, 3, true)},
            {InputLog.Event.clock(9 * gap + 1, 0, false)},
            {InputLog.Event.month(10 * gap, 482)},
            {InputLog.Event.move(10 * gap + 2, 5, 6)},                                      // kept: far past the last kept drag
            {InputLog.Event.move(7_200_000, 0.1f, -0.1f)},                                  // kept: two hours on, a long step in time
        };
        // Which of them the writer keeps: the moves and drags inside the gap of the last one kept are the two dropped.
        boolean[] kept2 = {true, false, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        for (int i = 0; i < script.length; i++) {
            all.add(script[i][0]);
            if (kept2[i]) expected.add(script[i][0]);
        }
        for (int i = 0; i < 10; i++) log.add(all.get(i));
        Thread feeder = new Thread(() -> { for (int i = 10; i < all.size(); i++) log.add(all.get(i)); }, "feeder");
        feeder.start();
        feeder.join();
        log.close();
        List<InputLog.Event> got = InputLog.read(log.path());
        helper("every kind written through the writer, from two threads, and closed: read back event for event"
                + (got.equals(expected) ? "" : " - got " + got.size() + " of " + expected.size()), got.equals(expected) && expected.size() == 18);
        boolean kinds = true;
        for (InputLog.Kind k : InputLog.Kind.values()) kinds &= got.stream().anyMatch(e -> e.kind() == k);
        helper("...and the sitting has every kind in it", kinds);
        helper("...the moves and drags closer than MOVE_EVERY_MS to the last one kept are dropped, those apart are kept",
                got.stream().filter(e -> e.kind() == InputLog.Kind.MOVE || e.kind() == InputLog.Kind.DRAG).count() == 5
                        && all.size() - expected.size() == 2);
        helper("...the header names the sitting's start", InputLog.startOf(log.path()) == sat);

        InputLog.Writer earlier = InputLog.open(logs, id, sat - 5_000);
        earlier.add(InputLog.Event.month(0, 1));
        earlier.close();
        List<Path> sittings = InputLog.sittings(logs);
        helper("the sittings of a folder are listed oldest first, and nothing else in it", sittings.size() == 2
                && sittings.get(0).equals(earlier.path()) && sittings.get(1).equals(log.path())
                && InputLog.sittings(logs.resolve("nowhere")).isEmpty());
        helper("...a log with one event reads back that one", InputLog.read(earlier.path()).equals(List.of(InputLog.Event.month(0, 1))));

        // A long sitting, cut mid-record.
        InputLog.Writer longLog = InputLog.open(logs, id, sat + 9_000);
        List<InputLog.Event> longEvents = new ArrayList<>();
        java.util.Random random = new java.util.Random(7);
        for (int i = 0; i < 2_000; i++) {
            InputLog.Event e = (i % 3 == 0) ? InputLog.Event.move(i * 20L, random.nextInt(1920), random.nextInt(1080))
                    : (i % 3 == 1) ? InputLog.Event.press(i * 20L, random.nextInt(1920), random.nextInt(1080), 1 + i % 2, 1, 0)
                    : InputLog.Event.key(i * 20L, i % 2 == 0, InputLog.keyCode("K" + (i % 26)), i % 16, i % 5 == 0 ? "k" + i : null);
            longEvents.add(e);
            longLog.add(e);
        }
        longLog.close();
        byte[] whole = Files.readAllBytes(longLog.path());
        helper("a long sitting reads back whole: 2,000 events, in about " + whole.length / 1024 + " KB", InputLog.read(longLog.path()).equals(longEvents));
        boolean prefixesOnly = true, shortOfAll = true;
        int mostRead = 0, leastRead = Integer.MAX_VALUE;
        // Three cuts through the body, which lose events; two through the gzip's trailer, which lose none (the data is whole before it).
        for (int cutAt : new int[] {whole.length / 4, whole.length / 2, whole.length * 7 / 10, whole.length - 5, whole.length - 11}) {
            Path cutFile = logs.resolve("cut-" + cutAt + ".bin.gz");
            Files.write(cutFile, Arrays.copyOf(whole, cutAt));
            List<InputLog.Event> read = InputLog.read(cutFile);
            boolean body = cutAt < whole.length - 11;
            prefixesOnly &= read.size() <= longEvents.size() && read.equals(longEvents.subList(0, read.size()));
            if (body) {
                shortOfAll &= read.size() < longEvents.size();
                mostRead = Math.max(mostRead, read.size());
                leastRead = Math.min(leastRead, read.size());
            }
        }
        helper("a copy of the file cut mid-record reads back a prefix of what was written: five cuts, the three through the body from "
                + leastRead + " to " + mostRead + " events of 2,000", prefixesOnly && shortOfAll && leastRead > 0);
        Files.write(logs.resolve("stub.bin.gz"), Arrays.copyOf(whole, 6));
        Files.write(logs.resolve("not-a-log.bin.gz"), "hello".getBytes());
        helper("...a file cut inside its header, or not a log at all, or missing, reads as none and throws nothing",
                InputLog.read(logs.resolve("stub.bin.gz")).isEmpty() && InputLog.read(logs.resolve("not-a-log.bin.gz")).isEmpty()
                        && InputLog.read(logs.resolve("missing.bin.gz")).isEmpty());

        // A sitting still open: the sync flush is what a crash leaves.
        InputLog.Writer open = InputLog.open(logs, id, sat + 20_000);
        for (int i = 0; i < 50; i++) open.add(InputLog.Event.month(i * 1_000L, i));
        Thread.sleep(InputLog.FLUSH_MS + 1_200);
        List<InputLog.Event> mid2 = InputLog.read(open.path());
        helper("a sitting still open reads up to its last flush, a crash losing no more than the flush's two seconds: 50 events",
                mid2.size() == 50 && open.written() == 50);
        open.add(InputLog.Event.month(60_000, 60));
        open.close();
        helper("...and closed it has the last one too, and add() after close is ignored", InputLog.read(open.path()).size() == 51
                && closedIgnores(open));

        // The Save, Load and Quit the ghost would re-enact, on the copy.
        replay.loadWhole(middle);
        replay.game().saveGame(ReplayCity.SLOT, "re-enacted");
        replay.game().loadGameSave(ReplayCity.SLOT);
        replay.game().saveForQuit();
        replay.game().awaitSaves();
        helper("a re-enacted Save, Load and Quit on the copy ran: the copy wrote into its own folder",
                Files.isRegularFile(replay.files().saveFile(ReplayCity.SLOT)) && Files.isRegularFile(replay.files().saveFile(GameFiles.AUTOSAVE_SLOT))
                        && replay.game().isReplayCopy() && !replay.game().isRecording());
        String hurt2 = firstDifference(playerBefore, hashes(files.getDirectory()));
        helper("...and the player's folder, with the recording in it, is byte for byte what it was" + (hurt2 == null ? "" : " - " + hurt2), hurt2 == null);
        helper("...and the recording's months are the months it had", Arrays.equals(rec.months(), have));

        /* ---- 5. the camera's arithmetic: the next slice's section (CameraPath) lands here ---- */

        /* ====== 6. recording costs the month only its fill, and changes nothing in it ====== */
        System.out.println("\n--- recording costs the month only its fill, and changes nothing in it ---");

        g = null;
        replay = null;
        GameFiles filesA = GameFiles.scratch("replaya"), filesB = GameFiles.scratch("replayb");
        Game a = SaveSlotCheck.grownCity(filesA, GROWN_SIX);
        Game b = SaveSlotCheck.grownCity(filesB, GROWN_SIX);
        helper("fixture: two cities grown the same, to the last digit",
                a.getMonth() == b.getMonth() && Double.compare(a.getCash(), b.getCash()) == 0
                        && a.getPopulationManager().getPopulation() == b.getPopulationManager().getPopulation()
                        && Double.compare(a.getPriceIndex().getIndex(), b.getPriceIndex().getIndex()) == 0);
        a.setRecording(true);
        List<Double> costs = new ArrayList<>();
        boolean traceHolds = true;
        String parted6 = "";
        int startA = a.getMonth();
        for (int i = 1; i <= 12; i++) {
            a.simulateMonths(1);
            b.simulateMonths(1);
            costs.add(a.lastRecordNanos() / 1e6);
            boolean same = a.getMonth() == b.getMonth()
                    && a.getPopulationManager().getPopulation() == b.getPopulationManager().getPopulation()
                    && Double.compare(a.getCash(), b.getCash()) == 0
                    && Double.compare(a.getEconomyManager().getNationalAccounts().getGdp(), b.getEconomyManager().getNationalAccounts().getGdp()) == 0
                    && Double.compare(a.getPriceIndex().getIndex(), b.getPriceIndex().getIndex()) == 0;
            if (!same && traceHolds) parted6 = " - first parted in month " + a.getMonth();
            traceHolds &= same;
        }
        helper("12 months played by both, one recording: population, cash, GDP and the price level equal in every month" + parted6, traceHolds);
        Recording recA = Recording.at(a.recordingFolder());
        helper("...and the one that records has its 12 months on disk", recA.months().length == 12 && recA.firstMonth() == startA + 1);
        double mean = costs.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
        double most = costs.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
        System.out.printf("      the recorder's FX-thread cost per month (fill, snapshot, hand-over): mean %.2f ms, most %.2f ms, over %d months%n",
                mean, most, costs.size());
        helper("the recorder's part of the month is within SaveWriter.CLOCK_MS, the most of the twelve", Timing.within(most, SaveWriter.CLOCK_MS));
        long monthBytes = 0;
        for (int m : recA.months()) monthBytes += Files.size(recA.monthFile(m));
        long historyBytes = Files.size(recA.folder().resolve(Recording.HISTORY_FILE));
        System.out.printf("      a month's file: mean %.1f KB (deflate level %d); the history: %.1f KB for %d entries%n",
                monthBytes / 12 / 1024.0, Recording.DEFLATE_LEVEL, historyBytes / 1024.0, recA.history().months());
        SaveWriter.Job job = a.prepareSaveForChecks(GameFiles.AUTOSAVE_SLOT, "measure");
        for (int level : new int[] {6, 1}) {
            long best = Long.MAX_VALUE;
            int size = 0;
            for (int rep = 0; rep < 3; rep++) {
                long t0 = System.nanoTime();
                size = Recording.monthBytes(job.data(), job.map(), a.getMonth(), level).length;
                best = Math.min(best, System.nanoTime() - t0);
            }
            System.out.printf("      a month at deflate level %d: %.1f KB, %.1f ms on the writer's thread%n", level, size / 1024.0, best / 1e6);
        }
        int someMonth = recA.lastMonth();
        helper("...and a month's file is smaller than the JSON of the save it holds",
                Files.size(recA.monthFile(someMonth)) < recA.saveJsonOf(someMonth).getBytes(java.nio.charset.StandardCharsets.UTF_8).length);

        // The writer held behind: the clock is never held, and the months not kept are counted.
        b.setRecording(true);
        SaveWriter.shared().hold();
        int played = 10;
        long heldAt = System.nanoTime();
        try {
            for (int i = 0; i < played; i++) clockMonth(b);
        } finally {
            SaveWriter.shared().release();
        }
        double heldMs = (System.nanoTime() - heldAt) / 1e6;
        b.awaitSaves();
        Recording recB = Recording.at(b.recordingFolder());
        System.out.printf("      the writer held for %d months: %d kept, %d not, the clock ran them in %.0f ms%n",
                played, recB.months().length, b.getMonthsNotRecorded(), heldMs);
        helper("a writer held behind never holds the clock: the months over the backlog are counted, not kept, and the rest are on disk",
                b.getMonthsNotRecorded() > 0 && recB.months().length + b.getMonthsNotRecorded() == played
                        && recB.months().length >= 1 && recB.months().length <= Game.RECORD_BACKLOG);

        System.out.printf("%n%s: %d failure(s)%n", ReplayCheck.class.getSimpleName(), fails);
        System.exit(fails == 0 ? 0 : 1);
    }

    /** Whether a closed log ignores an event added to it: nothing more is read from its file. */
    static boolean closedIgnores(InputLog.Writer closed) throws Exception {
        long before = closed.written();
        closed.add(InputLog.Event.month(70_000, 70));
        Thread.sleep(400);
        return closed.written() == before && InputLog.read(closed.path()).size() == 51;
    }
}
