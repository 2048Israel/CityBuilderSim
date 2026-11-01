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
 *   5. the camera's arithmetic (CameraPath): a glide's ends are exact on the globe and the plane, its middle scale the ends'
 *      geometric mean, its ease symmetric, monotone and still at both ends, the centre's share the formula's (2/3 at the middle
 *      for a ratio of 4, the eased share when the scales match, halfway on the equator), its month linear; the fly out ends at
 *      the widest scale on the city and takes as long as the doublings say; the smooth hand's five notches sum to ZOOM_STEP^5
 *      and ease out, a held key's velocity rises and falls without a jump, a hitch is a pause, a drift moves until still();
 *      5b. the graphs behind the city (ReplayGraphs): the recording's months in order, scaled to its highest, a year of GDP;
 *      5c. a quick step leaves most of the map's pictures as they were: a month on, fewer than half the tiles in view have another
 *      stamp, those that have the same stamp paint the same pixels in both maps, and the new map's tiles read the ground carried over;
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

    /** The middle of a map's districts in plots {x, y}: where a view of the city looks. */
    static double[] districtsMiddle(CityMap map) {
        double sx = 0, sy = 0;
        int n = 0;
        for (CityMap.District d : map.districts()) {
            sx += (map.baseDX() + d.dx + 0.5) * (double) World.DISTRICT;
            sy += (map.baseDY() + d.dy + 0.5) * (double) World.DISTRICT;
            n++;
        }
        return n == 0 ? new double[] {World.SIDE / 2.0, World.SIDE / 2.0} : new double[] {sx / n, sy / n};
    }

    /** The stamps of the tiles in a view, their district plans drawn first as the view's worker draws them (the jobs run and kept here). */
    static long[] stampsOf(MapTiles tiles, CityMap map, List<long[]> view) {
        TilePainter.Input in = new TilePainter.Input();
        long[] out = new long[view.size()];
        for (int i = 0; i < out.length; i++) {
            long tx = view.get(i)[0], ty = view.get(i)[1];
            for (CityMap.Job j : tiles.jobs(tx, ty)) {
                j.run();
                map.adopt(j);
            }
            out[i] = tiles.input(tx, ty, in);
        }
        return out;
    }

    /** Whether two figures agree to {@code tol}, absolutely under 1 and relatively over it. */
    static boolean near(double a, double b, double tol) {
        return Math.abs(a - b) <= tol * Math.max(1, Math.abs(b));
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

        /* ====== 5. the camera's arithmetic ====== */
        System.out.println("\n--- 5. the camera's arithmetic ---");

        CameraPath.View globeA = new CameraPath.View(true, 45, 10, 2.0, 12), globeB = new CameraPath.View(true, -10, 80, 0.05, 36);
        CameraPath.View planeA = new CameraPath.View(false, 100_000, 120_000, 0.5, 10), planeB = new CameraPath.View(false, 300_000, 80_000, 8.0, 50);
        for (CameraPath.View[] ends : new CameraPath.View[][] {{globeA, globeB}, {planeA, planeB}}) {
            String kind = ends[0].globe() ? "globe" : "plane";
            CameraPath.View g0 = CameraPath.glide(ends[0], ends[1], 0), g1 = CameraPath.glide(ends[0], ends[1], 1);
            helper("on the " + kind + " a glide at t = 0 is its start and at t = 1 its end: the centre, the scale and the month",
                    near(g0.a(), ends[0].a(), 1e-9) && near(g0.b(), ends[0].b(), 1e-9) && near(g0.scale(), ends[0].scale(), 1e-9) && g0.month() == ends[0].month()
                            && near(g1.a(), ends[1].a(), 1e-9) && near(g1.b(), ends[1].b(), 1e-9) && near(g1.scale(), ends[1].scale(), 1e-9) && g1.month() == ends[1].month());
            CameraPath.View mid = CameraPath.glide(ends[0], ends[1], 0.5);
            helper("...its middle scale is the geometric mean of the ends (to 1e-9)",
                    Math.abs(mid.scale() / Math.sqrt(ends[0].scale() * ends[1].scale()) - 1) < 1e-9);
            boolean steady = true;
            double was = Double.NaN;
            for (int i = 0; i <= 100; i++) {
                double s = CameraPath.glide(ends[0], ends[1], i / 100.0).scale();
                if (!Double.isNaN(was) && (ends[0].scale() < ends[1].scale() ? s < was - 1e-12 : s > was + 1e-12)) steady = false;
                was = s;
            }
            helper("...and the scale only ever goes one way from the start to the end", steady);
        }
        boolean symmetric = true, monotone = true;
        double eased = -1;
        for (int i = 0; i <= 1000; i++) {
            double t = i / 1000.0;
            if (Math.abs(CameraPath.ease(1 - t) - (1 - CameraPath.ease(t))) > 1e-12) symmetric = false;
            if (CameraPath.ease(t) < eased) monotone = false;
            eased = CameraPath.ease(t);
        }
        double slope = 1e-7;
        helper("the ease is 0 and 1 at the ends, symmetric about the middle, monotone, and still at both ends (a slope under 1e-6 at 0 and 1)",
                CameraPath.ease(0) == 0 && CameraPath.ease(1) == 1 && near(CameraPath.ease(0.5), 0.5, 1e-15) && symmetric && monotone
                        && CameraPath.ease(slope) / slope < 1e-6 && (1 - CameraPath.ease(1 - slope)) / slope < 1e-6
                        && CameraPath.ease(-3) == 0 && CameraPath.ease(7) == 1);
        double formula = (1 - Math.pow(4, -0.5)) / (1 - 1.0 / 4);
        CameraPath.View planeFour = new CameraPath.View(false, 0, 0, 1, 0), planeFar = new CameraPath.View(false, 300, 90, 4, 0);
        CameraPath.View midFour = CameraPath.glide(planeFour, planeFar, 0.5);
        helper("the centre's share at the middle for a ratio of 4 is the formula's own, (1 - r^-e) / (1 - r^-1), 2/3",
                near(CameraPath.share(4, 0.5), formula, 1e-12) && near(formula, 2.0 / 3, 1e-12) && near(midFour.a(), 300 * formula, 1e-9) && near(midFour.b(), 90 * formula, 1e-9));
        CameraPath.View sameScale = CameraPath.glide(new CameraPath.View(false, 0, 0, 2, 0), new CameraPath.View(false, 300, 90, 2, 0), 0.3);
        helper("...and the share is the eased share itself when the scales match",
                near(sameScale.a(), 300 * CameraPath.ease(0.3), 1e-9) && near(sameScale.b(), 90 * CameraPath.ease(0.3), 1e-9));
        CameraPath.View onEquator = CameraPath.glide(new CameraPath.View(true, 0, 0, 3, 0), new CameraPath.View(true, 0, 20, 3, 0), 0.5);
        helper("on the globe the middle of a glide between two points on the equator 20 degrees apart is on the equator, halfway",
                near(onEquator.a(), 0, 1e-9) && near(onEquator.b(), 10, 1e-9));
        CameraPath.View closing = CameraPath.glide(new CameraPath.View(true, 0, 0, 4, 0), new CameraPath.View(true, 0, 20, 1, 0), 0.5);
        helper("...closing in, the globe's share is the same formula on the inverse ratio: two thirds of the way",
                near(closing.a(), 0, 1e-9) && near(closing.b(), 20 * formula, 1e-9));
        CameraPath.View stay = CameraPath.glide(new CameraPath.View(true, 33, 44, 3, 5), new CameraPath.View(true, 33, 44, 1, 5), 0.37);
        helper("...a path of no length stays at the point at every step", near(stay.a(), 33, 1e-9) && near(stay.b(), 44, 1e-9));
        CameraPath.View monthMid = CameraPath.glide(new CameraPath.View(false, 0, 0, 1, 12), new CameraPath.View(false, 0, 0, 5, 36), 0.25);
        helper("a glide's month is linear in t, not eased: a quarter of the way from 12 to 36 is 18, and the ends are the marks'",
                monthMid.month() == 18 && CameraPath.glide(planeA, planeB, 0).month() == 10 && CameraPath.glide(planeA, planeB, 1).month() == 50
                        && CameraPath.path(planeA, planeB, 24).size() == 25 && CameraPath.path(planeA, planeB, 24).get(12).month() == 30
                        && CameraPath.path(planeA, planeB, 24).get(24).equals(CameraPath.glide(planeA, planeB, 1)));
        helper("a glide between a globe's view and a plane's stays at its start until the end (the kinds cannot blend)",
                CameraPath.glide(globeA, planeB, 0.5).globe() && CameraPath.glide(globeA, planeB, 1).equals(planeB));

        double widest = 17.7, cityLat = 6.2, cityLon = -75.6;
        CameraPath.View fromStreet = new CameraPath.View(true, 6.25, -75.57, 0.01, 77);
        CameraPath.View farthest = CameraPath.flyOut(fromStreet, widest, cityLat, cityLon, 1);
        boolean grows = true;
        double scaleBefore = 0;
        for (int i = 0; i <= 200; i++) {
            double s = CameraPath.flyOut(fromStreet, widest, cityLat, cityLon, i / 200.0).scale();
            if (s < scaleBefore - 1e-12) grows = false;
            scaleBefore = s;
        }
        helper("the fly out at t = 1 is exactly the widest scale and the city's centre, its scale grows all the way, and the month stays",
                near(farthest.scale(), widest, 1e-12) && near(farthest.a(), cityLat, 1e-9) && near(farthest.b(), cityLon, 1e-9) && grows && farthest.month() == 77);
        helper("the time a fly takes is the doublings of the view's width over the dial: a ratio squared takes twice as long, in as out",
                near(CameraPath.flySeconds(1, 16), 2 * CameraPath.flySeconds(1, 4), 1e-12) && near(CameraPath.flySeconds(1, 4), 2 / CameraPath.FLY_DOUBLINGS_A_SECOND, 1e-12)
                        && near(CameraPath.flySeconds(4, 1), CameraPath.flySeconds(1, 4), 1e-12) && CameraPath.flySeconds(5, 5) == 0
                        && CameraPath.flySeconds(0.01, widest) > CameraPath.flySeconds(2, widest));

        // The smooth hand: five notches in, then steps of 1/60 s until it rests (the brief's 2 s leaves exp(-10) of it to do, 5e-5; the rest takes 3 s).
        CameraPath.Smooth hand = new CameraPath.Smooth();
        hand.wheel(300, 200, 5);
        double product = 1, previous = Double.POSITIVE_INFINITY;
        boolean easesOut = true, anchored = true;
        int steps = 0;
        CameraPath.Move firstStep = null;
        while (hand.moving() && steps < 1200) {
            CameraPath.Move m = hand.step(1.0 / 60);
            if (firstStep == null) firstStep = m;
            if (m.zoom() > previous + 1e-15 || m.zoom() < 1) easesOut = false;
            if (!m.none() && (m.sx() != 300 || m.sy() != 200)) anchored = false;
            previous = m.zoom();
            product *= m.zoom();
            steps++;
        }
        helper("five notches in: the product of the steps' zooms is ZOOM_STEP^5 to 1e-6, and the hand rests after " + steps + " steps",
                Math.abs(product / Math.pow(CameraPath.ZOOM_STEP, 5) - 1) < 1e-6 && !hand.moving() && hand.step(1.0 / 60).none());
        helper("...every step zooms no more than the one before (it eases out), the first the most, about the point the wheel was at",
                easesOut && anchored && firstStep.zoom() > 1.05 && firstStep.zoom() < Math.pow(CameraPath.ZOOM_STEP, 5));
        CameraPath.Smooth outHand = new CameraPath.Smooth();
        outHand.wheel(10, 10, -2);
        double outProduct = 1;
        for (int i = 0; i < 600 && outHand.moving(); i++) outProduct *= outHand.step(1.0 / 60).zoom();
        helper("two notches out are ZOOM_STEP^-2 in the same way", Math.abs(outProduct * Math.pow(CameraPath.ZOOM_STEP, 2) - 1) < 1e-6);

        CameraPath.Smooth keyHand = new CameraPath.Smooth();
        double target = 600, biggest = 0, lastV = 0;
        boolean rises = true, falls = true;
        keyHand.keys(target, 0);
        for (int i = 0; i < 60; i++) {
            keyHand.step(1.0 / 60);
            double v = keyHand.velocity()[0];
            biggest = Math.max(biggest, Math.abs(v - lastV));
            if (v < lastV) rises = false;
            lastV = v;
        }
        double reached = lastV;
        keyHand.keys(0, 0);
        for (int i = 0; i < 60; i++) {
            keyHand.step(1.0 / 60);
            double v = keyHand.velocity()[0];
            biggest = Math.max(biggest, Math.abs(v - lastV));
            if (v > lastV) falls = false;
            lastV = v;
        }
        helper("a held key's velocity rises toward its target and, let go, falls to nothing, no step jumping more than MOST_DT_S / SMOOTH_S of the target",
                rises && falls && reached > 0.99 * target && reached < target && lastV < 0.01 * target
                        && biggest <= CameraPath.MOST_DT_S / CameraPath.SMOOTH_S * target + 1e-9);
        CameraPath.Smooth slow = new CameraPath.Smooth(), quick = new CameraPath.Smooth();
        for (CameraPath.Smooth s : new CameraPath.Smooth[] {slow, quick}) {
            s.wheel(1, 2, 3);
            s.keys(600, -300);
        }
        CameraPath.Move hitch = slow.step(1.0), ordinary = quick.step(CameraPath.MOST_DT_S);
        helper("a frame of a one-second hitch moves the view no more than a MOST_DT_S step does: a pause, not a jump",
                hitch.equals(ordinary) && Math.abs(hitch.dx()) <= 600 * CameraPath.MOST_DT_S && slow.step(-1).none());
        CameraPath.Smooth driftHand = new CameraPath.Smooth();
        driftHand.centre(640, 360);
        driftHand.drift(CameraPath.DRIFT_STEP_PX, 0, CameraPath.DRIFT_ZOOM_STEP);
        double across = 0, zoomed = 1;
        boolean centred = true;
        for (int i = 0; i < 60; i++) {
            CameraPath.Move m = driftHand.step(1.0 / 60);
            across += m.dx();
            zoomed *= m.zoom();
            if (m.sx() != 640 || m.sy() != 360) centred = false;
        }
        helper("a drift moves the ground DRIFT_STEP_PX in a second and closes in by exp(DRIFT_ZOOM_STEP) about the view's middle, until still()",
                near(across, CameraPath.DRIFT_STEP_PX, 1e-9) && near(zoomed, Math.exp(CameraPath.DRIFT_ZOOM_STEP), 1e-9) && centred
                        && driftHand.moving() && driftHand.drifting());
        driftHand.still();
        helper("...and still() stops it, and the zoom still to do and the keys' velocity with it",
                !driftHand.moving() && !driftHand.drifting() && driftHand.step(1.0 / 60).none());

        System.out.println("\n--- 5b. the graphs behind the city ---");
        ReplayGraphs.Line people = ReplayGraphs.of(master, "population", first, last, false);
        ReplayGraphs.Line output = ReplayGraphs.of(master, "gdp", first, last, true);
        boolean ascending = true, within = true;
        for (int i = 0; i < people.months().length; i++) {
            if (i > 0 && people.months()[i] <= people.months()[i - 1]) ascending = false;
            if (people.months()[i] < first || people.months()[i] > last) within = false;
        }
        helper("the population line holds the recording's months in order, from its first month to its last",
                people.months().length > 1 && ascending && within && people.months()[0] == first && people.months()[people.months().length - 1] == last);
        helper("...it draws nothing before the first month, all of it at the last, and the head is the last point",
                people.drawn(first - 1) == 0 && people.drawn(last) == people.months().length && people.head(last) == people.values()[people.values().length - 1]
                        && people.drawn(middle) > 0 && people.drawn(middle) < people.months().length);
        double top = 0;
        boolean fits = true;
        for (int i = 0; i < people.values().length; i++) {
            top = Math.max(top, people.values()[i]);
            if (people.share(i) > 1 + 1e-12 || people.share(i) < 0) fits = false;
        }
        helper("...scaled to the whole recording: its highest point is 1 and none is above it, so a line rises as the months play",
                fits && top == people.max() && top > 0 && near(people.share(people.drawn(first) - 1), people.values()[0] / top, 1e-12)
                        && people.share(people.drawn(first) - 1) < 1);
        double[] gdpRaw = master.aligned("gdp");
        int axisAt = master.getMonth().indexOf(last);
        double yearSum = 0;
        for (int j = axisAt - ReplayGraphs.YEAR + 1; axisAt >= 0 && j <= axisAt; j++) yearSum += gdpRaw[j];
        helper("GDP is a year of output, twelve times the mean of the last twelve months, as the header quotes it",
                axisAt >= ReplayGraphs.YEAR && output.months().length == people.months().length && near(output.head(last), yearSum, 1e-9 * Math.max(1, yearSum)));
        helper("a month's place across the recording runs 0 at the first to 1 at the last, and a recording of one month is at 0",
                ReplayGraphs.across(first, first, last) == 0 && ReplayGraphs.across(last, first, last) == 1
                        && near(ReplayGraphs.across((first + last) / 2, first, last), 0.5, 0.5 / (last - first) + 1e-12)
                        && ReplayGraphs.across(5, 5, 5) == 0 && ReplayGraphs.of(null, "gdp", first, last, true).months().length == 0);

        System.out.println("\n--- 5c. a quick step leaves most of the map's pictures as they were ---");
        // The pop-in's premise (ui/MapView.staleAll()): a step to the next month changes few tiles' inputs, so the pictures the view
        // keeps are repainted only where their stamp moved. The view is the plane at 4 px a plot over the city's districts.
        replay.loadWhole(first);
        CityMap mapBefore = replay.game().getCityMap();
        double[] middleOf = districtsMiddle(mapBefore);
        MapFrame eye = new MapFrame(1345, 806);
        eye.set(middleOf[0], middleOf[1], 4.0);
        List<long[]> inView = new ArrayList<>();
        for (long ty = eye.tileY0(); ty <= eye.tileY1(); ty++) for (long tx = eye.tileX0(); tx <= eye.tileX1(); tx++) inView.add(new long[] {tx, ty});
        MapTiles tilesBefore = new MapTiles(mapBefore);
        long[] stampsBefore = stampsOf(tilesBefore, mapBefore, inView);
        boolean stepped2 = replay.stepQuick(have[1]);
        CityMap mapAfter = replay.game().getCityMap();
        MapTiles tilesAfter = new MapTiles(mapAfter, tilesBefore);
        long[] stampsAfter = stampsOf(tilesAfter, mapAfter, inView);
        int repainted = 0;
        for (int i = 0; i < stampsAfter.length; i++) if (stampsAfter[i] != stampsBefore[i]) repainted++;
        System.out.printf("      one month on, %d of %d tiles in view have another stamp (repainted); the rest keep their pictures%n", repainted, inView.size());
        helper("a quick step to the next month is another map of the same world, and its tiles read the same ground the first read",
                stepped2 && mapAfter != mapBefore && mapAfter.worldKey().equals(mapBefore.worldKey()) && inView.size() > 8
                        && Arrays.equals(tilesAfter.terrain(inView.get(0)[0], inView.get(0)[1]), new MapTiles(mapAfter).terrain(inView.get(0)[0], inView.get(0)[1])));
        helper("...and fewer than half the tiles in view have another stamp: the pictures kept are repainted only where their inputs moved", repainted * 2 < inView.size());
        int[] pixelsA = new int[MapTiles.tileImagePixels(MapFrame.SMALL_TILE_PX)], pixelsB = new int[pixelsA.length];
        TilePainter.Input scratch = new TilePainter.Input();
        int sameStamp = 0, agree = 0;
        for (int i = 0; i < inView.size() && sameStamp < 40; i++) {
            if (stampsAfter[i] != stampsBefore[i]) continue;
            sameStamp++;
            long tx = inView.get(i)[0], ty = inView.get(i)[1];
            tilesBefore.pixels(tx, ty, MapFrame.L0, MapFrame.SMALL_TILE_PX, scratch, pixelsA);
            tilesAfter.pixels(tx, ty, MapFrame.L0, MapFrame.SMALL_TILE_PX, scratch, pixelsB);
            if (Arrays.equals(pixelsA, pixelsB)) agree++;
        }
        helper("...a tile whose stamp is the same paints the same pixels in both maps (" + sameStamp + " of them painted and compared), so a picture held against it is the new map's",
                sameStamp > 0 && agree == sameStamp);

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
