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

import ham.citybuildersim.ui.FollowBench;
import ham.citybuildersim.ui.FollowRules;
import ham.citybuildersim.ui.ReplayMoveProbe;

/**
 * Replay of a recorded city (batch TR1, 2026-10-31): the recorder, the throwaway copy, the input log, and what recording costs;
 * and Follow (batch TR2, 2026-11-09): the timeline of the input logs and the player that plays it, with TR1's four review fixes.
 * Timing harness (Timing.HARNESSES): section 6 judges the recorder's FX-thread cost through Timing.
 *
 * What it has to prove (the numbers are the brief's section 7, docs/queue/TR1.md):
 *   1. a recording writes one month each, and a rewind rewrites from the next: a grown city, "Record this city" on, played 24
 *      months, has 24 month files, the meta and the history, and nothing under saves/ but the autosave's and the slot the check
 *      saved to; loaded back at month 12 (the save carries the switch and the id) and played 3 months with the property tax moved (R0: the income tax is shut until 1917, so the open dial),
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
 *      5d. a replay's camera move loads first and then runs (CM1): in the real window, off screen, the normal game draws its map with
 *      no camera, no loaded move and nothing pinned; a glide from the street to the whole map reaches Ready with every plan view
 *      loaded, holds its pins while it waits and runs on a click; a glide across months does the same and runs on a key; while
 *      they run no frame asks the worker, draws unpainted ground or lets a picture go, over at least L0 and FAR for the first, and
 *      afterwards nothing is pinned or held; and Esc while a glide loads leaves the camera idle with nothing held (ReplayMoveProbe.check;
 *      skipped where JavaFX cannot start, as in the cloud);
 *   6. recording costs the month only its fill and changes nothing in it: two same cities played 12 months, one recording, agree
 *      to the last digit; the recorder's FX-thread cost per month is within SaveWriter.CLOCK_MS (printed, with the size of a month's
 *      file and of the history, and of a month at deflate level 6 and 1);
 *   7. TR1's review (TR2): a log writer whose write failed takes no more events and lets go of its file (it can be deleted); a save
 *      whose recording id is not the shape Recording.newId writes ("../escape", a drive path) gives no folder, recording off, and
 *      records nothing outside recordings/; a first write after a load that fails leaves no month of the old timeline past it, and
 *      one that cannot clear the old months writes nothing and ends the recording on the next month; a month that autosaves is
 *      filled once, and the recording's month is the autosave's save but for its name; a recording's tasks queued behind a first write
 *      that fails (the writer held: the months after it, the history an autosave hands over, the history switching off writes) write
 *      nothing beside the old months, and a new timeline (switched off and on) is not ended by the failure of the one before;
 *   8. the timeline (InputTimeline, FollowPlayer): two sittings with a rewind give a segment each (and a third where the recording
 *      holds the older one's later months), startOf finds a month's segment and marker, a skipped month the marker past it, and
 *      none a month no log reaches; a sitting that loads an earlier save is cut into runs; stateAt gives the screen, the mouse, the
 *      window, the map and the buttons; a gap of more than IDLE_GAP_MS plays in IDLE_KEEP_MS with idle skipping on and in full with
 *      it off; crossing a segment switches sittings; stopping and rejoining at now() lands where it stopped;
 *   9. Follow into a headless copy: a real recording and a sitting logged with the writer, played by FollowPlayer into a Target over
 *      a ReplayCity that loads each month whole as the log turns it: the copy is at each logged month in order, the screens arrive
 *      in logged order at their logged times divided by the speed (to the frame), joining mid-month loads the month and catches
 *      its input up flagged, and ends on the screen playing from the month's start reaches; nothing in the recording is written;
 *  10. Follow in the real window: the rules (FollowRules) drop what a copy must not do again - anything logged on a game menu (Save,
 *      Load, New city, Settings and Quit live there) or while the copy is on one, anything under a dialog, a click on a node that
 *      opens a menu or moves the clock, Space, Left, Right and F11, Esc and P where they would open the menu - and keep the
 *      rest; then a sitting logged with the writer over a copy of the recording is played into the window by frames: the copy
 *      is at each logged month as its marker passes, each click on a rail tab takes it to that tab's screen before the SCREEN
 *      marker comes and the screens arrive in order, the click on the Menu button is dropped and Follow stays on through every
 *      press, the ghost sits at the logged pointer, a stop and a join in mid-month reaches the screen and month that a run
 *      from the month's start reaches, a viewer's press ends Follow and his move does only past the grace and 8 px, and leaving
 *      the replay leaves the copy and the player's folder as they were (FollowBench.check and followCheck; the window part is
 *      skipped where JavaFX cannot start); a press dragged off a button disarms it and a release there does not fire it, one dragged
 *      back on fires it, a key still down when Follow stops is released once, and the game menus are the model's own list.
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

    /** Every file under a folder: its path from the folder, its size and its SHA-256 (public since TR2: FollowBench asks it in the window). */
    public static Map<String, String> hashes(Path dir) throws Exception {
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
    public static String firstDifference(Map<String, String> before, Map<String, String> after) {
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
        double wasRate = g.getEconomyManager().getTaxPolicy().getPropertyTaxRate();
        g.getEconomyManager().getTaxPolicy().setPropertyTaxRate(wasRate + 0.0123);
        double newRate = g.getEconomyManager().getTaxPolicy().getPropertyTaxRate();
        helper("fixture: the property tax moved off what the recording has", Math.abs(newRate - wasRate) > 1e-9);
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
                Double.compare(at13.getPropertyTaxRate(), newRate) == 0 && Double.compare(at12.getPropertyTaxRate(), wasRate) == 0);
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
        int y0 = CityCalendar.EPOCH_YEAR;   // the calendar's first year: the tables are its 2nd, 3rd, 6th and 11th (EP1)
        longer.setTradeByPartner(List.of(new PartnerTrade.Table(y0 + 1, null), new PartnerTrade.Table(y0 + 2, null),
                new PartnerTrade.Table(y0 + 5, null), new PartnerTrade.Table(y0 + 10, null)));
        helper("fixture: a history past MONTHLY_KEPT has its oldest five years folded, and all its months", longer.yearlyPoints() == 5 && longer.monthsLived() == total);
        HistorySave among = longer.cutAt(12 * 5 + 3);
        double[] gdpAll = longer.raw("gdp"), gdpAmong = among.raw("gdp");
        boolean prefix = gdpAmong.length == among.months() && among.months() == 8;
        for (int i = 0; prefix && i < gdpAmong.length; i++) prefix = Double.compare(gdpAmong[i], gdpAll[i]) == 0;
        helper("a cut among the months after the folded years: the five years and three months, every series their prefix",
                among.yearlyPoints() == 5 && among.monthsLived() == 63 && among.getMonth().get(among.months() - 1) == 63 && prefix);
        helper("...the finished years of trade are those before the month's year: " + (y0 + 1) + " and " + (y0 + 2) + " for a cut in " + (y0 + 5),
                among.tradeYears() == 2 && among.getTradeByPartner().get(1).year() == y0 + 2 && longer.tradeYears() == 4);
        HistorySave inside = longer.cutAt(30);
        helper("a cut inside a folded year drops that year whole: month 30 keeps the two years that ended by it, " + y0 + " and " + (y0 + 1),
                inside.months() == 2 && inside.yearlyPoints() == 2 && inside.monthsLived() == 24 && inside.getMonth().get(1) == 24
                        && inside.raw("cash").length == 2 && inside.tradeYears() == 1 && inside.getTradeByPartner().get(0).year() == y0 + 1);
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
                Double.compare(policy.getPropertyTaxRate(), recordedMiddle.getPropertyTaxRate()) == 0);
        policy.setPropertyTaxRate(policy.getPropertyTaxRate() + 0.02);
        helper("...a click may change the copy", Math.abs(replay.game().getEconomyManager().getTaxPolicy().getPropertyTaxRate() - recordedMiddle.getPropertyTaxRate()) > 1e-9);
        helper("...a step to the next month loads it whole", replay.loadWhole(middle + 1) == null && replay.shown() == middle + 1);
        helper("...and back: the rate is the recorded one, nothing stuck",
                replay.loadWhole(middle) == null && Double.compare(replay.game().getEconomyManager().getTaxPolicy().getPropertyTaxRate(), recordedMiddle.getPropertyTaxRate()) == 0
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

        /* ====== 5d. a replay's camera move loads first, then runs with nothing unpainted and nothing asked (CM1) ====== */
        System.out.println("\n--- 5d. a replay move loads first, then runs with nothing unpainted and nothing asked (CM1) ---");
        double glideSeconds = 2;        // short, to keep the suite quick: ReplayMoveProbe's main() measures the ten-second moves
        ReplayMoveProbe.Checked cm = ReplayMoveProbe.check(rec.folder(), GameFiles.scratch("replaywindow"), glideSeconds);
        if (cm.cannot() != null) {
            System.out.println("      skipped: " + cm.cannot());
        } else {
            ReplayMoveProbe.Normal nm = cm.normal();
            System.out.printf("      the %s window: normal game drew %d frames; camera %b, loaded move %b, caches pinned %d%n",
                    cm.globe() ? "Earth" : "generated-world", nm.frames(), nm.camera(), nm.moveHeld(), nm.cachesPinned());
            helper("the normal game draws its map (at least 30 frames) with no camera, no loaded move and nothing pinned",
                    nm.frames() >= 30 && !nm.camera() && !nm.moveHeld() && nm.cachesPinned() == 0);
            movedChecks(cm.zoomOut(), glideSeconds, "click", true);
            movedChecks(cm.months(), glideSeconds, "key", false);
            ReplayMoveProbe.Cancelled esc = cm.escape();
            System.out.printf("      Esc while loading: was loading %b, taken %b, camera %s after, move held %b, caches pinned %d%n",
                    esc.wasLoading(), esc.taken(), esc.phaseAfter(), esc.heldAfter(), esc.pinnedAfter());
            helper("Esc while a glide loads leaves the camera idle, no move held and nothing pinned (it was loading when Esc came)",
                    esc.wasLoading() && esc.taken() && "IDLE".equals(esc.phaseAfter()) && !esc.heldAfter() && esc.pinnedAfter() == 0);
        }

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

        long sevenAt = System.nanoTime();
        reviewChecks(a, b, filesA, filesB);
        long eightAt = System.nanoTime();
        timelineChecks();
        long nineAt = System.nanoTime();
        followChecks(rec, files);
        long tenAt = System.nanoTime();
        followRulesChecks();
        followWindowChecks(rec);
        System.out.printf("      sections 7, 8, 9 and 10 took %.1f s, %.1f s, %.1f s and %.1f s%n", (eightAt - sevenAt) / 1e9, (nineAt - eightAt) / 1e9,
                (tenAt - nineAt) / 1e9, (System.nanoTime() - tenAt) / 1e9);

        System.out.printf("%n%s: %d failure(s)%n", ReplayCheck.class.getSimpleName(), fails);
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * Section 5d's assertions for one glide, loaded and run in the window (ReplayMoveProbe.check()): it reached Ready with every plan
     * view loaded, held its pins while it waited, ran on the click or the key; no frame of its run asked the worker, drew unpainted
     * ground or let a picture go, and it ran frame by frame to its end: at least ten frames a second of the glide, since the camera
     * steps a tenth of a second at most (over L0 and FAR where {@code acrossLevels}: a glide that never left the street proves
     * nothing; a floor tied to the frame rate would go red on a busy PC); afterwards nothing is pinned or held.
     */
    static void movedChecks(ReplayMoveProbe.Moved m, double glideSeconds, String ranBy, boolean acrossLevels) {
        System.out.printf("      %s glide: %s in %.1f s (%d plan views, %d passed); ran by a %s in %.1f s, %d frames over %s, unpainted %d, worker asks %d, pictures let go %d, off the plan %d; "
                        + "afterwards held %b (let go in %.2f s), pinned %d%n",
                m.name(), m.ready() ? "ready" : "NOT ready", m.loadSeconds(), m.planViews(), m.passed(), m.ranBy(), m.runSeconds(), m.frames(),
                m.levelFrames(), m.unpaintedFrames(), m.asks(), m.evicted(), m.offPlan(), m.heldAfter(), m.letGoAfterS(), m.pinnedAfter());
        String what = "the " + m.name() + " glide";
        helper(what + " loads and reaches Ready with every plan view loaded (none passed), and holds its pins while it waits (the move held, all six caches pinned)",
                m.ready() && m.passed() == 0 && m.heldWhileReady() && m.pinnedWhileReady() == 6);
        helper("...and runs on " + (ranBy.equals("click") ? "a click on the map" : "a plain key") + ", which leaves Ready for the glide",
                m.ranBy().equals(ranBy) && m.taken() && "GLIDE".equals(m.phaseAfterTrigger()));
        boolean frames = m.frames() >= glideSeconds * 10;
        boolean levels = acrossLevels ? m.levelFrames().containsKey("L0") && m.levelFrames().containsKey("FAR") : !m.levelFrames().isEmpty();
        helper("...no frame of its run asked the worker, drew unpainted ground or let a picture go (asks " + m.asks() + ", unpainted frames "
                + m.unpaintedFrames() + ", let go " + m.evicted() + ")", m.ready() && m.asks() == 0 && m.unpaintedFrames() == 0 && m.evicted() == 0);
        helper("...and every frame of its run showed a view its load had painted, whatever the frames' clock (off the plan " + m.offPlan() + " of "
                + m.frames() + " frames)", m.ready() && m.frames() > 0 && m.offPlan() == 0);
        helper("...and it ran frame by frame to its end, at least " + (int) (glideSeconds * 10) + " frames" + (acrossLevels ? " over at least L0 and FAR" : "") + " (" + m.frames() + ")", frames && levels);
        helper("...after it, no move is held and nothing is pinned", m.ready() && !m.heldAfter() && m.pinnedAfter() == 0);
    }

    /** Whether a closed log ignores an event added to it: nothing more is read from its file. */
    static boolean closedIgnores(InputLog.Writer closed) throws Exception {
        long before = closed.written();
        closed.add(InputLog.Event.month(70_000, 70));
        Thread.sleep(400);
        return closed.written() == before && InputLog.read(closed.path()).size() == 51;
    }

    /* ============ 7. TR1's review: a failed log write, an id from a save, a failed first write, a month that autosaves ============ */

    /** A body that may throw: what {@link #printed} runs. */
    interface Body {
        void run() throws Exception;
    }

    /** What a body prints to the log, as a string (and not to the log). */
    static String printed(Body body) throws Exception {
        java.io.PrintStream real = System.out;
        java.io.ByteArrayOutputStream captured = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(captured, true, java.nio.charset.StandardCharsets.UTF_8));
        try {
            body.run();
        } finally {
            System.setOut(real);
        }
        return captured.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    /** What a task's write returns, with what it prints kept out of the log. */
    static GameFiles.Result printedResult(java.util.function.Supplier<GameFiles.Result> write) throws Exception {
        GameFiles.Result[] out = new GameFiles.Result[1];
        printed(() -> out[0] = write.get());
        return out[0];
    }

    /** Waits up to {@code ms} for a condition, polling; whether it came true. */
    static boolean awaitTrue(java.util.function.BooleanSupplier condition, long ms) throws InterruptedException {
        long until = System.nanoTime() + ms * 1_000_000L;
        while (!condition.getAsBoolean()) {
            if (System.nanoTime() > until) return false;
            Thread.sleep(10);
        }
        return true;
    }

    static int occurrences(String text, String part) {
        int n = 0;
        for (int i = text.indexOf(part); i >= 0; i = text.indexOf(part, i + part.length())) n++;
        return n;
    }

    /** {@code a} and {@code b}: the section 6 cities, both recording. */
    static void reviewChecks(Game a, Game b, GameFiles filesA, GameFiles filesB) throws Exception {
        System.out.println("\n--- 7. TR1's review: a failed log write, an id from a save, a failed first write, a month that autosaves ---");

        // (1) The input log after a failed write: it stops taking events and lets go of its file.
        Path logs = TempDirs.create("replaylog7");
        InputLog.Writer failing = InputLog.open(logs, "tr2-20261109-120000", 1_762_000_000_000L);
        failing.add(InputLog.Event.month(0, 1));
        helper("fixture: a log has written its first event", awaitTrue(() -> failing.written() == 1, 3_000));
        boolean[] ended = {false};
        printed(() -> {
            failing.failNextDrainForChecks();
            failing.add(InputLog.Event.month(10, 2));
            ended[0] = awaitTrue(failing::stopped, 3_000);
        });
        helper("a log whose write failed has ended its thread", ended[0]);
        failing.add(InputLog.Event.month(20, 3));
        helper("...takes no more events: what is added after the failure is not queued, and nothing more is written",
                failing.queued() == 0 && failing.written() == 1);
        long closing = System.nanoTime();
        failing.close();
        helper("...closes at once, not after the wait for a thread that has gone", Timing.within((System.nanoTime() - closing) / 1e6, InputLog.CLOSE_WAIT_MS / 2.0));
        boolean deleted;
        try {
            deleted = Files.deleteIfExists(failing.path());
        } catch (IOException e) {
            deleted = false;
            System.out.println("      the failed log's file could not be deleted: " + e);
        }
        helper("...and its file can be deleted: the failing path closed it (on Windows an open file cannot be)", deleted && !Files.exists(failing.path()));

        // (2) A recording id read from a save is the shape the recorder writes, or no recording at all.
        String escapeAbsolute = logs.resolve("elsewhere").toAbsolutePath().toString();
        helper("an id is exactly the shape newId writes: its own ids are accepted, a path, a drive, a dot or a stray character is not",
                Recording.isId(Recording.newId("Medellín", 0)) && Recording.isId(Recording.newId("  !! ", 0))
                        && Recording.isId(Recording.newId("x".repeat(90), 0)) && Recording.isId("city-20261031-142355")
                        && !Recording.isId(null) && !Recording.isId("") && !Recording.isId("../escape") && !Recording.isId("..")
                        && !Recording.isId("a/b-20261031-142355") && !Recording.isId("a\\b-20261031-142355")
                        && !Recording.isId("C:\\temp\\x-20261031-142355") && !Recording.isId(escapeAbsolute)
                        && !Recording.isId("City-20261031-142355") && !Recording.isId("x-20261031-142355 ") && !Recording.isId("x-20261031-142355\n")
                        && !Recording.isId("x-20261031-142355/..") && !Recording.isId("-20261031-142355") && !Recording.isId("a--b-20261031-142355")
                        && !Recording.isId("x".repeat(Recording.ID_NAME_LENGTH + 1) + "-20261031-142355") && !Recording.isId("x-2026103-142355"));
        helper("fixture: the city is recording, under an id of that shape", a.isRecording() && Recording.isId(a.getRecordingId()));
        a.awaitSaves();
        Map<String, String> recordingsBefore = hashes(filesA.recordingsDirectory());
        helper("...with a recording on disk to be left alone", !recordingsBefore.isEmpty());
        a.saveGame(ReplayCity.SLOT, "tr2 id");
        Path slotFile = filesA.saveFile(ReplayCity.SLOT);
        Path outside = TempDirs.create("replayoutside");
        for (String bad : new String[] {"../escape", outside.toAbsolutePath().toString()}) {
            boolean relative = bad.startsWith("..");
            com.google.gson.JsonObject save = com.google.gson.JsonParser.parseString(Files.readString(slotFile)).getAsJsonObject();
            helper("fixture: the save says it is recording, and now names a folder " + (relative ? "above recordings/" : "elsewhere"),
                    save.get("recording").getAsBoolean() && save.has("recordingId"));
            save.addProperty("recordingId", bad);
            Files.writeString(slotFile, save.toString());
            String said = printed(() -> a.loadGameSave(ReplayCity.SLOT));
            helper("a save whose recording id is " + (relative ? "\"" + bad + "\"" : "an absolute path")
                            + " loads, with no folder, recording off, and the log saying so once",
                    a.getLoadFailure() == null && a.recordingFolder() == null && a.getRecordingId() == null && !a.isRecording()
                            && occurrences(said, "is not one this game writes") == 1);
            printed(() -> clockMonth(a));
            a.awaitSaves();
            String hurt = firstDifference(recordingsBefore, hashes(filesA.recordingsDirectory()));
            boolean nothingOutside = relative ? !Files.exists(filesA.recordingsDirectory().resolve(bad).normalize()) : true;
            try (var listing = Files.list(outside)) {
                nothingOutside &= listing.findAny().isEmpty();
            }
            helper("...and a month played writes nothing outside recordings/ and nothing in the one there" + (hurt == null ? "" : " - " + hurt),
                    nothingOutside && hurt == null);
        }
        a.setRecording(true);
        helper("...and switching recording on again gives the city an id of its own, of the shape", a.isRecording() && Recording.isId(a.getRecordingId()));

        // (3) The first write after a load: a failure leaves one timeline.
        Path folder = TempDirs.create("replayold").resolve("old-20261109-120000");
        DataSave small = new DataSave();
        for (int m = 1; m <= 8; m++) Recording.monthTask(filesA, folder, null, m, small, null, false, null).write();
        Recording old = Recording.at(folder);
        helper("fixture: an old timeline of months 1 to 8", Arrays.equals(old.months(), new int[] {1, 2, 3, 4, 5, 6, 7, 8}));
        DataSave nan = new DataSave();
        nan.setCash(Double.NaN);
        java.util.concurrent.atomic.AtomicBoolean broken = new java.util.concurrent.atomic.AtomicBoolean();
        GameFiles.Result lost = printedResult(() -> Recording.monthTask(filesA, folder, null, 5, nan, null, true, () -> broken.set(true)).write());
        helper("a first write after a load, at month 5, that fails (a number the file cannot hold) leaves no month of the old timeline past it: months 1 to 4 and none from 5",
                !lost.ok && !broken.get() && Arrays.equals(old.months(), new int[] {1, 2, 3, 4}));
        for (int m = 5; m <= 8; m++) Recording.monthTask(filesA, folder, null, m, small, null, false, null).write();
        Files.delete(old.monthFile(7));
        Files.createDirectories(old.monthFile(7));
        Files.writeString(old.monthFile(7).resolve("keep"), "a month that cannot be deleted");
        GameFiles.Result stuck = printedResult(() -> Recording.monthTask(filesA, folder, null, 5, small, null, true, () -> broken.set(true)).write());
        helper("...and one that cannot clear the old months (month 7 cannot be deleted) writes nothing and tells the caller: the flag is set, month 5 is not written, the old months that could go have gone",
                !stuck.ok && broken.get() && !Files.exists(old.monthFile(5)) && !Files.exists(old.monthFile(6)) && !Files.exists(old.monthFile(8)));
        helper("...and in the city: a first write after a load that cannot clear the old months writes nothing, and the next month ends the recording, saying so",
                tripFirstWrite(a));

        // (3b) One recording, one timeline: what is queued behind a failing first write, and what a new timeline does after it.
        String behind = queuedBehindBrokenFirstWrite(a, 4, false);
        helper("...and the months queued behind that failing first write while the writer is held (the first and 3 more) are not written beside the old ones, nor is the history an autosave hands over:"
                + " the history file is as it was, and the next month ends the recording" + behind, behind.isEmpty());
        String behindOff = queuedBehindBrokenFirstWrite(a, 2, true);
        helper("...and so is the history that switching the recording off writes, behind a first month and one more" + behindOff, behindOff.isEmpty());
        String newer = newTimelineAfterBrokenOne(a);
        helper("...and a recording switched off and on again behind a timeline whose first write fails is a new timeline: that failure, which comes after, does not end it,"
                + " and its months are written" + newer, newer.isEmpty());

        // (4) A month that autosaves is filled once.
        long[] tick = {0};
        b.useSaveClockForChecks(() -> tick[0] += 60_000_000_000L);
        b.autosave("tr2 reset");
        b.awaitSaves();
        int sharedBefore = b.recordedFromAutosave();
        for (int i = 0; i < Game.AUTOSAVE_MONTHS; i++) {
            clockMonth(b);
            b.awaitSaves();
        }
        b.awaitSaves();
        int autosaveMonth = b.getMonth();
        Recording recB = Recording.at(b.recordingFolder());
        SaveHeader autoHeader = filesB.readHeader(GameFiles.AUTOSAVE_SLOT);
        String recordedJson = recB.saveJsonOf(autosaveMonth), previousJson = recB.saveJsonOf(autosaveMonth - 1);
        helper("a recorded city that plays " + Game.AUTOSAVE_MONTHS + " months after an autosave autosaves in the last, and that month's recording is the autosave's fill, taken once"
                        + " (shared " + (b.recordedFromAutosave() - sharedBefore) + " time(s); the autosave is month " + (autoHeader == null ? "?" : autoHeader.getMonth()) + " of " + autosaveMonth
                        + ", named " + (autoHeader == null ? "?" : autoHeader.getSlotName()) + "; " + b.getMonthsNotRecorded() + " months left out of the recording)",
                b.recordedFromAutosave() == sharedBefore + 1 && autoHeader != null && autoHeader.getMonth() == autosaveMonth
                        && autoHeader.getSlotName().equals("Autosave - month " + autosaveMonth) && recordedJson != null && previousJson != null);
        if (recordedJson == null || previousJson == null) return;
        com.google.gson.JsonObject autosaved = com.google.gson.JsonParser.parseString(Files.readString(filesB.saveFile(GameFiles.AUTOSAVE_SLOT))).getAsJsonObject();
        com.google.gson.JsonObject recorded = com.google.gson.JsonParser.parseString(recordedJson).getAsJsonObject();
        String recordedName = recorded.get("slotName").getAsString();
        String previousName = com.google.gson.JsonParser.parseString(previousJson).getAsJsonObject().get("slotName").getAsString();
        autosaved.remove("slotName");
        recorded.remove("slotName");
        helper("...the recording's save of that month is the autosave's but for the name it carries, which stays \"" + Game.RECORDING_NAME + "\" as in the other months",
                autosaved.equals(recorded) && recordedName.equals(Game.RECORDING_NAME) && previousName.equals(Game.RECORDING_NAME));
        byte[] autoMap = Files.isRegularFile(filesB.mapFile(GameFiles.AUTOSAVE_SLOT)) ? Files.readAllBytes(filesB.mapFile(GameFiles.AUTOSAVE_SLOT)) : null;
        helper("...and so is its map, byte for byte", autoMap != null && Arrays.equals(autoMap, recB.mapOf(autosaveMonth)));
    }

    /**
     * The city's own first write after a load: a month written, a month past the next one made undeletable (a directory with a file
     * in it), recording switched off and on as a load does; the first month then cannot clear the old timeline, nothing of it is
     * written, and the month after sees the flag, ends the recording and says so.
     */
    static boolean tripFirstWrite(Game city) throws Exception {
        printed(() -> clockMonth(city));
        city.awaitSaves();
        int first = city.getMonth();
        Recording rec = Recording.at(city.recordingFolder());
        Path trap = rec.monthFile(first + 5);
        Files.createDirectories(trap);
        Files.writeString(trap.resolve("keep"), "a month that cannot be deleted");
        city.setRecording(false);
        city.awaitSaves();
        city.setRecording(true);
        printed(() -> {
            city.toggleNextMonth();
            city.awaitSaves();
        });
        String said = printed(() -> {
            city.toggleNextMonth();
            city.awaitSaves();
        });
        return !city.isRecording() && said.contains("Recording ended") && Files.isDirectory(trap)
                && Files.isRegularFile(rec.monthFile(first)) && !Files.exists(rec.monthFile(first + 1)) && !Files.exists(rec.monthFile(first + 2));
    }

    /** The bytes of a file, or null where there is none. */
    static byte[] bytesOrNull(Path file) throws IOException {
        return Files.isRegularFile(file) ? Files.readAllBytes(file) : null;
    }

    /** A month made undeletable at {@code month}: a directory with a file in it stands where the month's file goes. */
    static Path trapMonth(Recording rec, int month) throws IOException {
        Path trap = rec.monthFile(month);
        Files.createDirectories(trap);
        Files.writeString(trap.resolve("keep"), "a month that cannot be deleted");
        return trap;
    }

    /** The traps trapMonth made, taken away, so the next fixture's first write is not stopped by an older one. */
    static void removeTraps(Recording rec) throws IOException {
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(rec.folder(), Recording.MONTH_PREFIX + "*")) {
            for (Path entry : entries) {
                if (!Files.isDirectory(entry)) continue;
                try (java.util.stream.Stream<Path> walk = Files.walk(entry)) {
                    for (Path p : walk.sorted(java.util.Comparator.reverseOrder()).toList()) Files.delete(p);
                }
            }
        }
    }

    /**
     * TR2's review, the tasks queued behind a first write that fails. A month written, one past the months to come made
     * undeletable, recording off and on (a new timeline), the writer held: {@code months} months are played (the first is the
     * timeline's first write, which cannot clear the old months), then either an autosave hands the recording its history or
     * the recording is switched off, which writes it. The writer let go, none of those months is written beside the old ones and
     * the history file is as it was; when the recording is still on, the next month ends it and says so. What went wrong, or "".
     */
    static String queuedBehindBrokenFirstWrite(Game city, int months, boolean switchOff) throws Exception {
        Recording rec = Recording.at(city.recordingFolder());
        removeTraps(rec);
        city.setRecording(true);
        clockMonth(city);
        city.awaitSaves();
        int base = city.getMonth();
        Path trap = trapMonth(rec, base + months + 2);
        city.setRecording(false);
        city.awaitSaves();
        city.setRecording(true);
        Path historyFile = rec.folder().resolve(Recording.HISTORY_FILE);
        byte[] historyBefore = bytesOrNull(historyFile);
        String problems = "";
        try {
            printed(() -> {
                SaveWriter.shared().hold();
                try {
                    for (int i = 0; i < months; i++) clockMonth(city);
                    if (switchOff) city.setRecording(false);
                    else city.autosave("tr2 queued");
                } finally {
                    SaveWriter.shared().release();
                }
                city.awaitSaves();
            });
            for (int i = 1; i <= months; i++) {
                if (Files.exists(rec.monthFile(base + i))) problems += " month " + (base + i) + " was written beside the old timeline;";
            }
            if (!Arrays.equals(historyBefore, bytesOrNull(historyFile))) problems += " the history file was rewritten;";
            if (!Files.isDirectory(trap)) problems += " the month that cannot be deleted is gone;";
            if (!switchOff) {
                String said = printed(() -> {
                    city.toggleNextMonth();
                    city.awaitSaves();
                });
                if (city.isRecording() || !said.contains("Recording ended")) problems += " the next month did not end the recording;";
                if (Files.exists(rec.monthFile(city.getMonth()))) problems += " the next month was written;";
            }
        } finally {
            SaveWriter.shared().release();
            removeTraps(rec);
        }
        return problems;
    }

    /**
     * TR2's review, a timeline whose first write fails and a new one after it. A month written, the next made undeletable (so the
     * first month of the timeline cannot clear it), recording off and on, the writer held: that first month and one more are
     * played, the recording is switched off and on (the next timeline) and two more months are played; the writer let go, the
     * failure of the first timeline comes after the second began. The second timeline's months are written and the next month,
     * after the failure, does not end its recording. What went wrong, or "".
     */
    static String newTimelineAfterBrokenOne(Game city) throws Exception {
        Recording rec = Recording.at(city.recordingFolder());
        removeTraps(rec);
        city.setRecording(true);
        clockMonth(city);
        city.awaitSaves();
        int base = city.getMonth();
        Path trap = trapMonth(rec, base + 2);
        city.setRecording(false);
        city.awaitSaves();
        city.setRecording(true);
        String problems = "";
        try {
            printed(() -> {
                SaveWriter.shared().hold();
                try {
                    clockMonth(city);
                    clockMonth(city);
                    city.setRecording(false);
                    city.setRecording(true);
                    clockMonth(city);
                    clockMonth(city);
                } finally {
                    SaveWriter.shared().release();
                }
                city.awaitSaves();
            });
            String said = printed(() -> {
                city.toggleNextMonth();
                city.awaitSaves();
            });
            if (!city.isRecording() || said.contains("Recording ended")) problems += " the new timeline's recording was ended;";
            for (int i = 3; i <= 5; i++) {
                if (!Files.isRegularFile(rec.monthFile(base + i))) problems += " month " + (base + i) + " of the new timeline is not on disk;";
            }
            if (Files.isRegularFile(rec.monthFile(base + 1))) problems += " the first timeline's first month was written;";
            if (!Files.isDirectory(trap)) problems += " the month that cannot be deleted is gone;";
        } finally {
            SaveWriter.shared().release();
            removeTraps(rec);
        }
        return problems;
    }

    /* ============ 8. the timeline ============ */

    /** What a Follow target was handed, in order; with a copy it loads each month whole, as Follow does in the window (sections 8 and 9). */
    static final class Tape implements FollowPlayer.Target {
        final ReplayCity copy;
        final List<String> calls = new ArrayList<>();
        final List<InputLog.Event> events = new ArrayList<>();
        final List<Boolean> catching = new ArrayList<>();
        final List<Integer> months = new ArrayList<>();
        final List<String> screens = new ArrayList<>();
        final List<Double> screenTimes = new ArrayList<>();
        double elapsed;
        String screen;
        boolean loaded = true;

        Tape(ReplayCity copy) { this.copy = copy; }

        @Override public void loadMonth(int month) {
            months.add(month);
            calls.add("load " + month);
            if (copy != null) loaded &= copy.loadWhole(month) == null && copy.game().getMonth() == month;
        }

        @Override public void event(InputLog.Event e, boolean catchingUp) {
            events.add(e);
            catching.add(catchingUp);
            calls.add((catchingUp ? "catch " : "play ") + e.kind() + (e.words() == null ? "" : " " + e.words()));
            if (e.kind() == InputLog.Kind.SCREEN) {
                screen = e.words();
                screens.add(e.words());
                screenTimes.add(elapsed);
            }
        }

        long count(InputLog.Kind kind) { return events.stream().filter(e -> e.kind() == kind).count(); }
    }

    /** Plays the timeline out in frames of {@code frameMs}; the tape's clock is the frames' end. */
    static void playOut(FollowPlayer player, Tape tape, double frameMs) {
        for (int guard = 0; !player.isDone() && guard < 2_000_000; guard++) {
            tape.elapsed += frameMs;
            player.advance(frameMs);
        }
    }

    static int[] months(int from, int to) {
        return java.util.stream.IntStream.rangeClosed(from, to).toArray();
    }

    static void timelineChecks() {
        System.out.println("\n--- 8. the timeline: which sitting holds a month, where Follow starts, what the player had, idle ---");

        // S1 plays months 100 to 200, a second each, twelve events a month; S2, later, loads month 149 and plays 150 to 180.
        List<InputLog.Event> s1 = new ArrayList<>(), s2 = new ArrayList<>();
        s1.add(InputLog.Event.window(0, 1280, 720));
        for (int m = 100; m <= 200; m++) {
            long t = (m - 100) * 1_000L;
            s1.add(InputLog.Event.month(t, m));
            s1.add(InputLog.Event.screen(t + 100, "S1 " + m));
            for (int k = 0; k < 10; k++) s1.add(InputLog.Event.move(t + 200 + k * 30L, m, k));
        }
        s2.add(InputLog.Event.window(0, 1920, 1080));
        for (int m = 150; m <= 180; m++) {
            long t = (m - 150) * 1_000L;
            s2.add(InputLog.Event.month(t, m));
            s2.add(InputLog.Event.screen(t + 100, "S2 " + m));
            if (m == 150) {
                s2.add(InputLog.Event.mapView(t + 200, true, 1, 2, 3.5, 0));
                s2.add(InputLog.Event.move(t + 500, 300, 300));
                s2.add(InputLog.Event.press(t + 600, 300, 300, 1, 1, 0));
                s2.add(InputLog.Event.move(t + 700, 301, 301));
                s2.add(InputLog.Event.release(t + 800, 301, 301, 1, 0));
                s2.add(InputLog.Event.mapView(t + 900, true, 1, 2, 0, 0));
            } else {
                s2.add(InputLog.Event.move(t + 500, m, m));
            }
        }
        // Given newest first: the timeline orders the sittings itself.
        List<InputTimeline.Sitting> sittings = List.of(new InputTimeline.Sitting(2_000, s2), new InputTimeline.Sitting(1_000, s1));
        InputTimeline tl = InputTimeline.of(sittings, months(100, 180));
        List<InputTimeline.Segment> segments = tl.segments();
        helper("a later sitting that rewound into the earlier's months makes two segments: the earlier for 100 to 149, the later for 150 to 180",
                segments.size() == 2 && segments.get(0).equals(new InputTimeline.Segment(0, 1_000, 100, 149, 0, 50_000))
                        && segments.get(1).equals(new InputTimeline.Segment(1, 2_000, 150, 180, 0, 30_500)));
        InputTimeline.Moment at100 = tl.startOf(100), at149 = tl.startOf(149), at150 = tl.startOf(150), at180 = tl.startOf(180);
        helper("startOf: a month before the rewind is in the earlier sitting's segment at its marker's millisecond, a rewound one in the later's",
                at100 != null && at149 != null && at150 != null && at180 != null && at100.segment() == 0 && at100.ms() == 0
                        && at149.segment() == 0 && at149.ms() == 49_000 && at150.segment() == 1 && at150.ms() == 0
                        && at180.segment() == 1 && at180.ms() == 30_000);
        helper("...and monthAt gives the month back for each", at100 != null && tl.monthAt(at100) == 100 && tl.monthAt(at149) == 149
                && tl.monthAt(at150) == 150 && tl.monthAt(at180) == 180 && tl.monthAt(null) == -1);
        helper("...a month the recording does not hold, or no log reaches, is null: 99, the older sitting's 181 and 200 (the rewind deleted them), 1000",
                tl.startOf(99) == null && tl.startOf(181) == null && tl.startOf(200) == null && tl.startOf(1_000) == null);
        InputTimeline kept = InputTimeline.of(sittings, months(100, 200));
        List<InputTimeline.Segment> keptSegments = kept.segments();
        helper("a recording that still holds the older sitting's 181 to 200 gets them from it, as a third segment",
                keptSegments.size() == 3 && keptSegments.get(2).sitting() == 0 && keptSegments.get(2).firstMonth() == 181
                        && keptSegments.get(2).lastMonth() == 200 && keptSegments.get(2).msFrom() == 81_000 && kept.startOf(190).segment() == 2);

        // A skip jumps over months: the marker past it.
        InputTimeline jumped = InputTimeline.of(List.of(new InputTimeline.Sitting(3_000, List.of(
                InputLog.Event.month(0, 300), InputLog.Event.move(100, 1, 1), InputLog.Event.month(10_000, 310)))), months(300, 310));
        helper("a month a skip jumped over starts at the first marker past it: 305 at the millisecond of 310's, and the month there is 310",
                jumped.segments().size() == 1 && jumped.startOf(305) != null && jumped.startOf(305).ms() == 10_000
                        && jumped.monthAt(jumped.startOf(305)) == 310 && jumped.startOf(300).ms() == 0 && jumped.monthAt(jumped.startOf(300)) == 300);

        // A sitting that loads an earlier save: its markers go back, and the sitting is cut there into runs.
        InputTimeline cut = InputTimeline.of(List.of(new InputTimeline.Sitting(4_000, List.of(
                InputLog.Event.month(0, 10), InputLog.Event.month(1_000, 11), InputLog.Event.month(2_000, 12), InputLog.Event.month(3_000, 13),
                InputLog.Event.month(4_000, 11), InputLog.Event.month(5_000, 12)))), months(10, 12));
        List<InputTimeline.Segment> cutSegments = cut.segments();
        helper("a sitting whose markers go back (a Load) is cut there: month 10 from its first run, 11 and 12 from the second, the 13 the rewind deleted nobody's",
                cutSegments.size() == 2 && cutSegments.get(0).equals(new InputTimeline.Segment(0, 4_000, 10, 10, 0, 1_000))
                        && cutSegments.get(1).equals(new InputTimeline.Segment(0, 4_000, 11, 12, 4_000, 5_000))
                        && cut.startOf(11).ms() == 4_000 && cut.startOf(13) == null);
        Tape cutTape = new Tape(null);
        playOut(new FollowPlayer(cut, cutTape), cutTape, 100);
        helper("...and played, it loads 10, 11, 12 and stops", cutTape.months.equals(List.of(10, 11, 12)));

        // What the player had at a moment.
        InputTimeline.State inMonth = tl.stateAt(tl.momentAt(1, 700));
        helper("stateAt, 700 ms into month 150: the screen open, the last mouse position, the window the sitting began with, the map open at its view, the button held",
                "S2 150".equals(inMonth.screen()) && inMonth.mouseKnown() && inMonth.x() == 301 && inMonth.y() == 301
                        && inMonth.windowWidth() == 1920 && inMonth.windowHeight() == 1080 && inMonth.mapOpen()
                        && inMonth.mapView().globe() && inMonth.mapView().scale() == 3.5 && inMonth.down(1) && !inMonth.down(3));
        InputTimeline.State later = tl.stateAt(tl.momentAt(1, 950));
        helper("...at 950 ms: the button let go, and the map's view with a scale of 0 is closed",
                !later.down(1) && !later.mapOpen() && later.mapView() != null && later.x() == 301);
        InputTimeline.State beforeAny = tl.stateAt(tl.startOfSegment(1));
        helper("...at the segment's start, only what came before and at its marker: the window, no screen, no mouse, no map",
                beforeAny.windowWidth() == 1920 && beforeAny.screen() == null && !beforeAny.mouseKnown() && beforeAny.mapView() == null && !beforeAny.mapOpen());
        InputTimeline.State late = tl.stateAt(tl.momentAt(0, 49_999));
        helper("...and far into a long sitting (hundreds of events on, past the saved states): the last screen, the last move, the first window",
                "S1 149".equals(late.screen()) && late.x() == 149 && late.y() == 9 && late.windowWidth() == 1280 && late.windowHeight() == 720);
        helper("...a moment that is not in the timeline has no state, no month", tl.stateAt(null).screen() == null && !tl.stateAt(null).mouseKnown()
                && tl.monthAt(new InputTimeline.Moment(9, 0, 0)) == -1);

        // The input events are the player's; the markers are not.
        boolean inputs = true;
        for (InputLog.Kind k : InputLog.Kind.values()) {
            boolean input = k == InputLog.Kind.MOVE || k == InputLog.Kind.DRAG || k == InputLog.Kind.PRESS || k == InputLog.Kind.RELEASE
                    || k == InputLog.Kind.WHEEL || k == InputLog.Kind.KEY_DOWN || k == InputLog.Kind.KEY_UP;
            inputs &= InputTimeline.isInput(k) == input;
        }
        helper("the input events are MOVE, DRAG, PRESS, RELEASE, WHEEL and the keys; WINDOW, SCREEN, MAP_VIEW, CLOCK and MONTH are markers", inputs);

        // Playing across the rewind: the older sitting up to 149, the newer from its marker for 150.
        Tape across = new Tape(null);
        FollowPlayer acrossPlayer = new FollowPlayer(tl, across);
        acrossPlayer.setSpeed(4);
        acrossPlayer.start(tl.first());
        playOut(acrossPlayer, across, 250);
        List<Integer> expectedMonths = new ArrayList<>();
        for (int m = 100; m <= 180; m++) expectedMonths.add(m);
        boolean screensRight = across.screens.size() == 81;
        for (int i = 0; screensRight && i < across.screens.size(); i++) screensRight = across.screens.get(i).equals((i < 50 ? "S1 " : "S2 ") + (100 + i));
        helper("played across the rewind, the months load in order, 100 to 149 from the older sitting and 150 to 180 from the newer, and the older's 150 to 200 never play",
                across.months.equals(expectedMonths) && screensRight && acrossPlayer.isDone() && !acrossPlayer.isRunning() && acrossPlayer.month() == 180);

        // Idle: a gap of more than IDLE_GAP_MS plays in IDLE_KEEP_MS.
        InputTimeline idle = InputTimeline.of(List.of(new InputTimeline.Sitting(5_000, List.of(
                InputLog.Event.month(0, 1), InputLog.Event.move(0, 1, 1), InputLog.Event.screen(30_000, "mid-gap"), InputLog.Event.move(60_000, 2, 2),
                InputLog.Event.month(61_000, 2), InputLog.Event.move(62_000, 3, 3)))), months(1, 2));
        Tape skipping = new Tape(null);
        FollowPlayer skipper = new FollowPlayer(idle, skipping);
        skipper.start(idle.first());
        skipper.advance(InputTimeline.IDLE_KEEP_MS - 1);
        boolean before = skipping.count(InputLog.Kind.MOVE) == 1 && skipping.screens.equals(List.of("mid-gap"));
        skipper.advance(1);
        helper("with idle skipping on (the default), a 60 s gap between input plays in IDLE_KEEP_MS: the next input is not there a millisecond short, and is there at it; a marker in the gap comes in proportion",
                before && skipping.count(InputLog.Kind.MOVE) == 2 && skipper.skipIdle());
        Tape full = new Tape(null);
        FollowPlayer fuller = new FollowPlayer(idle, full);
        fuller.setSkipIdle(false);
        fuller.start(idle.first());
        fuller.advance(59_999);
        boolean short1 = full.count(InputLog.Kind.MOVE) == 1;
        fuller.advance(1);
        helper("...with it off, the gap plays in full", short1 && full.count(InputLog.Kind.MOVE) == 2 && !fuller.skipIdle());
        Tape quick = new Tape(null);
        FollowPlayer quicker = new FollowPlayer(idle, quick);
        quicker.setSpeed(4);
        quicker.start(idle.first());
        quicker.advance(InputTimeline.IDLE_KEEP_MS / 4.0 - 1);
        boolean short4 = quick.count(InputLog.Kind.MOVE) == 1;
        quicker.advance(1);
        helper("...and at speed 4 the skipped gap takes a quarter of that", short4 && quick.count(InputLog.Kind.MOVE) == 2 && quicker.speed() == 4);
        InputTimeline edge = InputTimeline.of(List.of(new InputTimeline.Sitting(6_000, List.of(
                InputLog.Event.month(0, 1), InputLog.Event.move(0, 1, 1), InputLog.Event.move(InputTimeline.IDLE_GAP_MS, 2, 2),
                InputLog.Event.move(2 * InputTimeline.IDLE_GAP_MS + 1, 3, 3)))), months(1, 1));
        Tape edgeTape = new Tape(null);
        FollowPlayer edger = new FollowPlayer(edge, edgeTape);
        edger.start(edge.first());
        edger.advance(InputTimeline.IDLE_GAP_MS);
        boolean atGap = edgeTape.count(InputLog.Kind.MOVE) == 2;
        edger.advance(InputTimeline.IDLE_KEEP_MS - 1);
        boolean inGap = edgeTape.count(InputLog.Kind.MOVE) == 2;
        edger.advance(1);
        helper("a gap of exactly IDLE_GAP_MS plays in full, one millisecond more is idle",
                atGap && inGap && edgeTape.count(InputLog.Kind.MOVE) == 3 && edger.isDone());

        // Stop and rejoin at the same moment.
        Tape first = new Tape(null);
        FollowPlayer player = new FollowPlayer(tl, first);
        player.start(tl.startOf(100));
        player.advance(10_500);
        InputTimeline.Moment stoppedAt = player.now();
        int deliveredWhenStopped = first.events.size();
        player.stop();
        player.advance(5_000);
        helper("a stopped player delivers nothing more", first.events.size() == deliveredWhenStopped && !player.isRunning() && player.month() == 110);
        Tape second = new Tape(null);
        FollowPlayer rejoined = new FollowPlayer(tl, second);
        rejoined.start(stoppedAt);
        boolean allCaught = !second.catching.isEmpty();
        for (boolean c : second.catching) allCaught &= c;
        helper("...and rejoined at now(), the month loads and its input up to the moment catches up, and the player is where the first stopped",
                second.months.equals(List.of(110)) && allCaught && second.events.size() == 11 && stoppedAt.equals(rejoined.now())
                        && second.screens.equals(List.of("S1 110")));
        helper("an empty timeline is done at once, plays nothing, and starts nowhere",
                playsNothing(InputTimeline.of(List.of(), months(1, 5))) && playsNothing(InputTimeline.of(sittings, new int[0])));
    }

    static boolean playsNothing(InputTimeline empty) {
        Tape tape = new Tape(null);
        FollowPlayer player = new FollowPlayer(empty, tape);
        player.advance(1_000);
        return empty.isEmpty() && empty.first() == null && player.isDone() && tape.calls.isEmpty() && player.now() == null
                && empty.startOf(1) == null && empty.segments().isEmpty();
    }

    /* ============ 9. Follow into a headless copy ============ */

    static void followChecks(Recording rec, GameFiles files) throws Exception {
        System.out.println("\n--- 9. Follow into a headless copy: the copy is loaded at each logged month, the screens come in order, a join mid-month catches up ---");

        int[] have = rec.months();
        int[] logged = Arrays.copyOfRange(have, 3, 7);
        long month = 4_000, startedAt = 1_790_000_000_000L;
        // A sitting of four months, four seconds each: input, two screens, a click.
        List<InputLog.Event> script = new ArrayList<>();
        script.add(InputLog.Event.window(0, 1280, 720));
        List<String> screensLogged = new ArrayList<>();
        List<Long> screenAtMs = new ArrayList<>();
        for (int i = 0; i < logged.length; i++) {
            long t = i * month;
            script.add(InputLog.Event.month(t, logged[i]));
            script.add(InputLog.Event.move(t + 200, 100 + i, 200));
            script.add(InputLog.Event.screen(t + 500, "Screen A" + i));
            script.add(InputLog.Event.move(t + 700, 300, 400 + i));
            script.add(InputLog.Event.press(t + 1_000, 300, 400 + i, 1, 1, 0));
            script.add(InputLog.Event.release(t + 1_050, 300, 400 + i, 1, 0));
            script.add(InputLog.Event.screen(t + 1_500, "Screen B" + i));
            screensLogged.add("Screen A" + i);
            screensLogged.add("Screen B" + i);
            screenAtMs.add(t + 500);
            screenAtMs.add(t + 1_500);
        }
        InputLog.Writer log = InputLog.open(rec.folder(), rec.id(), startedAt);
        for (InputLog.Event e : script) log.add(e);
        log.close();
        helper("fixture: a sitting of " + logged.length + " months and " + script.size() + " events logged beside the recording's months",
                InputLog.read(log.path()).equals(script) && InputLog.sittings(rec.folder()).contains(log.path()));

        Map<String, String> recordingBefore = hashes(rec.folder());
        Map<String, String> playerBefore = hashes(files.getDirectory());
        InputTimeline tl = InputTimeline.of(rec.folder());
        helper("the timeline of the recording's folder holds the one sitting, for the months it logged, and none for the recording's other months",
                tl.segments().size() == 1 && tl.segments().get(0).firstMonth() == logged[0] && tl.segments().get(0).lastMonth() == logged[logged.length - 1]
                        && tl.startOf(have[0]) == null && tl.startOf(have[have.length - 1]) == null && tl.startOf(logged[2]) != null);
        helper("...and the state at the first month's marker is the window the sitting opened with",
                tl.stateAt(tl.startOf(logged[0])).windowWidth() == 1280 && tl.stateAt(tl.startOf(logged[0])).screen() == null
                        && "Screen B1".equals(tl.stateAt(tl.startOf(logged[2])).screen()));

        ReplayCity copy = new ReplayCity(rec);
        for (double speed : new double[] {1, 2}) {
            Tape tape = new Tape(copy);
            FollowPlayer player = new FollowPlayer(tl, tape);
            player.setSpeed(speed);
            player.start(tl.startOf(logged[0]));
            double frame = 16;
            playOut(player, tape, frame);
            List<Integer> loadedMonths = new ArrayList<>();
            for (int m : logged) loadedMonths.add(m);
            helper("at speed " + speed + ", Follow loads the copy at each logged month in order, and the copy's month is the marker's each time (" + tape.months + ")",
                    tape.months.equals(loadedMonths) && tape.loaded && copy.shown() == logged[logged.length - 1] && copy.game().getMonth() == logged[logged.length - 1]);
            boolean timed = tape.screens.equals(screensLogged);
            double worst = 0;
            for (int i = 0; timed && i < tape.screenTimes.size(); i++) {
                double late = tape.screenTimes.get(i) - screenAtMs.get(i) / speed;
                worst = Math.max(worst, late);
                timed &= late >= 0 && late <= frame;
            }
            helper("...the screens arrive in the order logged, each in the frame its logged time divided by the speed falls in (the most late " + Math.round(worst * 10) / 10.0 + " ms of a " + (int) frame + " ms frame)",
                    timed && tape.screens.size() == 2 * logged.length && player.isDone());
            boolean live = true;
            for (int i = 0; live && i + 1 < tape.calls.size(); i++) live &= !tape.calls.get(i).startsWith("catch");
            helper("...and nothing played live is flagged as catching up, and a month's load comes before the input after its marker",
                    live && tape.catching.stream().noneMatch(c -> c) && tape.calls.get(0).equals("load " + logged[0]) && tape.calls.get(1).startsWith("play MOVE"));
        }

        // Joining mid-month: 1,200 ms into the third logged month, after screen A and the click.
        long joinMs = 2 * month + 1_200;
        InputTimeline.Moment join = tl.momentAt(0, joinMs);
        Tape joined = new Tape(copy);
        FollowPlayer joiner = new FollowPlayer(tl, joined);
        joiner.start(join);
        List<String> expectedCatchUp = new ArrayList<>();
        expectedCatchUp.add("load " + logged[2]);
        for (String s : new String[] {"MOVE", "SCREEN Screen A2", "MOVE", "PRESS", "RELEASE"}) expectedCatchUp.add("catch " + s);
        helper("joining mid-month loads that month into the copy, then plays its input from just after its marker up to the moment, all flagged as catching up",
                joined.calls.equals(expectedCatchUp) && joined.loaded && copy.game().getMonth() == logged[2] && joined.catching.stream().allMatch(c -> c));
        Tape fromStart = new Tape(copy);
        FollowPlayer starter = new FollowPlayer(tl, fromStart);
        starter.start(tl.startOf(logged[2]));
        starter.advance(joinMs - 2 * month);
        boolean sameEvents = fromStart.events.equals(joined.events);
        helper("...and the screen it ends on, " + joined.screen + ", is the one playing from the month's start reaches at the same moment, with the same events",
                joined.screen != null && joined.screen.equals(fromStart.screen) && sameEvents && fromStart.months.equals(joined.months)
                        && fromStart.catching.stream().noneMatch(c -> c) && joiner.now().equals(starter.now()));
        joiner.advance(1_000);
        helper("...and the live play after the join carries on to the month's next screen, " + joined.screen,
                "Screen B2".equals(joined.screen) && joined.catching.get(joined.catching.size() - 1) == false && joiner.month() == logged[2]);
        Tape atMarker = new Tape(copy);
        new FollowPlayer(tl, atMarker).start(tl.startOf(logged[1]));
        helper("starting exactly at a month's marker loads it and catches up on nothing", atMarker.calls.equals(List.of("load " + logged[1])));

        String hurtRecording = firstDifference(recordingBefore, hashes(rec.folder()));
        String hurtPlayer = firstDifference(playerBefore, hashes(files.getDirectory()));
        helper("following writes nothing in the recording's folder, and nothing in the player's" + (hurtRecording == null ? "" : " - " + hurtRecording)
                + (hurtPlayer == null ? "" : " - " + hurtPlayer), hurtRecording == null && hurtPlayer == null && Arrays.equals(rec.months(), have));
    }

    /* ============ 10. Follow in the window ============ */

    static void followRulesChecks() {
        System.out.println("\n--- 10. Follow in the window: the rules drop a re-enacted Save, Load and Quit, the clock and the menu keys; then a sitting is played into the real window ---");
        FollowRules.Verdict keep = FollowRules.Verdict.KEEP;
        String city = "showPopulationInfoMenu";
        InputLog.Event move = InputLog.Event.move(1_000, 120, 340), press = InputLog.Event.press(1_020, 120, 340, 1, 1, 0), release = InputLog.Event.release(1_080, 120, 340, 1, 0);
        InputLog.Event wheel = InputLog.Event.wheel(1_100, 120, 340, 0, -80, 0);
        InputLog.Event typed = InputLog.Event.key(1_120, true, InputLog.keyCode("Enter"), 0, null);
        InputLog.Event typedUp = InputLog.Event.key(1_140, false, InputLog.keyCode("Enter"), 0, null);
        List<InputLog.Event> inputs = List.of(move, press, release, wheel, typed, typedUp);

        // The screens the model takes for a game menu: its own list (UserInterface.GAME_MENUS), not a copy of it here (TR2's review).
        List<String> gameMenus = FollowRules.gameMenus();
        StringBuilder escaped = new StringBuilder();
        for (String menu : gameMenus) {
            for (InputLog.Event e : inputs) {
                FollowRules.Verdict v = FollowRules.judge(e, menu, city, false, false, false, false);
                if (v != FollowRules.Verdict.DROP_LOGGED_MENU) escaped.append(' ').append(menu).append('/').append(e.kind()).append('=').append(v);
            }
        }
        helper("a move, press, release, wheel and key logged while the player was on a game menu (" + gameMenus.size() + " of them) are dropped as logged on a menu"
                + escaped, escaped.length() == 0);
        helper("...and the screens Save (the saving menu), Load (the load menu) and Quit (the main menu, which has New city and Settings too) are on are among the game menus",
                gameMenus.containsAll(List.of("showSavingMenu", "showLoadMenu", "showMainMenu")));
        helper("...so a re-enacted Save, Load and Quit never reaches the copy: their buttons are not marked, and the verdict holds with the point on no marked node and the copy on a city screen",
                FollowRules.judge(press, "showSavingMenu", city, false, false, false, false) == FollowRules.Verdict.DROP_LOGGED_MENU
                        && FollowRules.judge(release, "showLoadMenu", city, false, false, false, false) == FollowRules.Verdict.DROP_LOGGED_MENU
                        && FollowRules.judge(press, "showMainMenu", city, false, false, false, false) == FollowRules.Verdict.DROP_LOGGED_MENU
                        && FollowRules.judge(typed, "showSavingMenu", city, false, true, false, false) == FollowRules.Verdict.DROP_LOGGED_MENU);

        StringBuilder onMenu = new StringBuilder();
        for (String menu : gameMenus) {
            for (InputLog.Event e : inputs) {
                FollowRules.Verdict v = FollowRules.judge(e, city, menu, false, false, false, false);
                if (v != FollowRules.Verdict.DROP_COPY_MENU) onMenu.append(' ').append(menu).append('/').append(e.kind()).append('=').append(v);
            }
        }
        helper("with the copy on a game menu, every input logged on a city screen is dropped as the copy's menu" + onMenu, onMenu.length() == 0);
        helper("a press, a release and a key are dropped while a dialog (Quit's) is up on the copy",
                FollowRules.judge(press, city, city, true, false, false, false) == FollowRules.Verdict.DROP_DIALOG
                        && FollowRules.judge(release, city, city, true, false, false, false) == FollowRules.Verdict.DROP_DIALOG
                        && FollowRules.judge(typed, city, city, true, false, false, false) == FollowRules.Verdict.DROP_DIALOG);
        helper("a press and a release on a node marked as the ghost never clicks (the rail's Menu, the clock) are dropped, and a move over it is kept",
                FollowRules.judge(press, city, city, false, false, false, true) == FollowRules.Verdict.DROP_NODE
                        && FollowRules.judge(release, city, city, false, false, false, true) == FollowRules.Verdict.DROP_NODE
                        && FollowRules.judge(move, city, city, false, false, false, true) == keep);

        int space = InputLog.keyCode("Space"), left = InputLog.keyCode("Left"), right = InputLog.keyCode("Right"), f11 = InputLog.keyCode("F11");
        boolean clock = true;
        for (int code : new int[] {space, left, right}) {
            clock &= FollowRules.judge(InputLog.Event.key(1_200, true, code, 0, null), city, city, false, false, false, false) == FollowRules.Verdict.DROP_CLOCK_KEY;
            clock &= FollowRules.judge(InputLog.Event.key(1_210, false, code, 0, null), city, city, false, false, false, false) == FollowRules.Verdict.DROP_CLOCK_KEY;
        }
        helper("Space, Left and Right, down and up, are dropped as the clock's keys", clock);
        helper("...but not in a text field, where Space types a space; and F11 is dropped as the window's key, typing or not",
                FollowRules.judge(InputLog.Event.key(1_220, true, space, 0, " "), city, city, false, true, false, false) == keep
                        && FollowRules.judge(InputLog.Event.key(1_230, true, f11, 0, null), city, city, false, false, false, false) == FollowRules.Verdict.DROP_WINDOW_KEY
                        && FollowRules.judge(InputLog.Event.key(1_240, true, f11, 0, null), city, city, false, true, false, false) == FollowRules.Verdict.DROP_WINDOW_KEY);

        InputLog.Event esc = InputLog.Event.key(1_300, true, InputLog.keyCode("Esc"), 0, null), p = InputLog.Event.key(1_310, true, InputLog.keyCode("P"), 0, "p");
        helper("Esc is dropped where it would open the game menu, typing or not, and kept where it closes something else",
                FollowRules.judge(esc, city, city, false, false, true, false) == FollowRules.Verdict.DROP_MENU_KEY
                        && FollowRules.judge(esc, city, city, false, true, true, false) == FollowRules.Verdict.DROP_MENU_KEY
                        && FollowRules.judge(esc, city, city, false, false, false, false) == keep);
        helper("P is dropped where it would open the game menu, and kept where it would not and in a text field",
                FollowRules.judge(p, city, city, false, false, true, false) == FollowRules.Verdict.DROP_MENU_KEY
                        && FollowRules.judge(p, city, city, false, false, false, false) == keep
                        && FollowRules.judge(p, city, city, false, true, true, false) == keep);

        boolean kept = true;
        for (InputLog.Event e : inputs) {
            kept &= FollowRules.judge(e, city, city, false, false, false, false) == keep;
            kept &= FollowRules.judge(e, null, null, false, false, false, false) == keep;
        }
        helper("a plain move, press, release, wheel and key on a city screen are kept, and so is any input where the screens are not known", kept);
        boolean markers = true;
        for (InputLog.Event e : List.of(InputLog.Event.screen(1_400, "showSavingMenu"), InputLog.Event.window(1_410, 1280, 720), InputLog.Event.month(1_420, 12),
                InputLog.Event.clock(1_430, 1, true), InputLog.Event.mapView(1_440, false, 1, 2, 3, 0))) {
            markers &= FollowRules.judge(e, "showSavingMenu", "showMainMenu", true, false, true, true) == keep;
        }
        helper("the log's markers (screen, window, month, clock, map) are kept whatever the screens: they are no input", markers);
    }

    /**
     * Section 10 in the real window, off screen: FollowBench's findings on the Reenactor and on a sitting followed in a copy of
     * {@code rec}, each printed as a line. Skipped, and said so, where JavaFX cannot start.
     */
    static void followWindowChecks(Recording rec) throws Exception {
        String cannot = FollowBench.startJavaFx();
        if (cannot != null) {
            System.out.println("      skipped in the window: " + cannot);
            return;
        }
        GameFiles windowFiles = GameFiles.scratch("followwindow");
        Game city = FollowBench.fx(() -> LongPlaytest.probeCity(windowFiles, null));
        long t0 = System.nanoTime();
        List<String[]> reenacted = FollowBench.fx(() -> FollowBench.check(city));
        for (String[] f : reenacted) helper("in the window: " + f[0], "true".equals(f[1]));
        List<String[]> followed = FollowBench.fx(() -> FollowBench.followCheck(city, rec.folder()));
        for (String[] f : followed) helper("in the window: " + f[0], "true".equals(f[1]));
        System.out.printf("      %d findings in the window, %.1f s%n", reenacted.size() + followed.size(), (System.nanoTime() - t0) / 1e9);
    }
}
