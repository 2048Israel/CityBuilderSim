package ham.citybuildersim;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The player's other saved cities as the Rankings board reads them (spec-leaderboards 5, LB3): slots 1 to GameFiles.SLOT_COUNT of a GameFiles, read only,
 * the autosave (slot 0) left out, each as a Rankings.City at its last save (now()) or at the active city's age (at(month)), the ones that never got that
 * far (stoppedAt(month)), and how many were left off the board and why (left()). A pure model in the *View manner: no JavaFX, no Game, and it never
 * writes a file. A snapshot is immutable, so the screen reads one on a worker thread (refresh) and hands it to the clock's thread.
 *
 * WHY. Each save is its own world: its own money (its last reform may have lopped zeros off it), its own rate, parity and price level, its own history.
 * The board compares them only through the pivot, so each city is handed over in the units of its LAST save, history and header rescaled together, and
 * the pivot divides the unit out (Rankings.savedCell): a reform never moves a rank. A history file is up to 26 MB on the PC, nine of them are 200 MB of
 * numbers, and the board needs a few dozen of the series: so a slot keeps a slim history (HistorySave.slim, CityMeasures.SERIES and the three that make
 * the pivot), never the file, and a snapshot is keyed per slot by both files' size and modified time, so that a refresh reads again only the slot that was
 * saved since. The header supplies what the history does not keep (the money's name, the founding rate, the land and the people by age band) and is
 * decoded by the classes that own each field (WorldEconomy, Denomination, PopulationCohorts, LandManager), never by array index.
 *
 * AT AN AGE. A city at month m has its figures from CityMeasures.at with Extras.UNKNOWN: the history keeps neither the land nor the bands, so area, density
 * and the measures over the bands are absent at an earlier month (never wrong). Its rate, parity and price level are the series at m; where a history
 * did not record the price level (usPrices, from LB3) the money figures are absent for that city rather than guessed.
 */
public final class SavedCities {

    /** The first slot read: slot 0 is the autosave, a copy of a numbered city, and is left out. The last is GameFiles.SLOT_COUNT. */
    public static final int FIRST_SLOT = 1;

    /** The history series a slot keeps: what the measures read, and the three that make the pivot (fxRate and fxParity are among the first). */
    static final Set<String> KEPT = keptSeries();

    private static Set<String> keptSeries() {
        Set<String> kept = new HashSet<>(CityMeasures.SERIES);
        kept.add("fxRate");
        kept.add("fxParity");
        kept.add("usPrices");
        return Set.copyOf(kept);
    }

    /** A saved city that never got as far as an age: its slot, its name and the last month of its history ("stopped at Feb 2031": CityCalendar.formatShort). */
    public record Stopped(int slot, String name, int lastMonth) { }

    /** The saved files left off the board and why, counted: a save from an older build, from a newer one, one that cannot be read, and one with no history. */
    public record LeftOut(int olderBuild, int newerBuild, int damaged, int noHistory) {
        /** All of them. */
        public int total() {
            return olderBuild + newerBuild + damaged + noHistory;
        }
    }

    /** Why a slot is not a city on the board; null for an empty slot and for a city. */
    enum Why { OLDER_BUILD, NEWER_BUILD, DAMAGED, NO_HISTORY }

    /** Both files of a slot as the disk had them: the path and each file's size and modified time (-1 for a file that is not there). */
    record Stamp(Path save, long saveSize, long saveTime, long historySize, long historyTime) { }

    /** What a readable slot keeps: the city as its last save has it, the slim history behind it, and what an earlier month needs of the header. */
    private record Holding(String name, String currency, double foundingRate, HistorySave history, int lastMonth, Rankings.City now) { }

    /** One slot as read: its stamp, and either the city it keeps, or why it is left off, or neither (an empty slot). */
    private record Entry(int slot, Stamp stamp, Holding kept, Why left) { }

    /** The snapshot before any slot was read: no cities, nothing left out. */
    public static final SavedCities NONE = new SavedCities(List.of(), 0);

    private final List<Entry> entries;
    private final int reads;

    private SavedCities(List<Entry> entries, int reads) {
        this.entries = List.copyOf(entries);
        this.reads = reads;
    }

    /* ------------------------------- reading ------------------------------- */

    /** Every slot of the files read afresh: NONE.refresh(files). */
    public static SavedCities read(GameFiles files) {
        return NONE.refresh(files);
    }

    /**
     * A new snapshot of the files, reusing every slot of this one whose save and history files have the size and modified time they had (and the path), so
     * that only the slot saved since is read again. The screen calls it on a worker thread: nothing here touches JavaFX or a Game, and it writes nothing.
     */
    public SavedCities refresh(GameFiles files) {
        List<Entry> next = new ArrayList<>();
        int read = 0;
        for (int slot = FIRST_SLOT; slot <= GameFiles.SLOT_COUNT; slot++) {
            Stamp stamp = stampOf(files, slot);
            Entry old = entryOf(slot);
            if (old != null && old.stamp().equals(stamp)) {
                next.add(old);
                continue;
            }
            if (stamp.saveSize() >= 0) read++;
            next.add(readSafely(files, slot, stamp));
        }
        return new SavedCities(next, read);
    }

    /** How many slots the refresh that made this snapshot parsed from disk (the rest were reused or empty): 0 when nothing was saved since the last. */
    public int reads() {
        return reads;
    }

    private Entry entryOf(int slot) {
        return slot - FIRST_SLOT < entries.size() ? entries.get(slot - FIRST_SLOT) : null;
    }

    private static Stamp stampOf(GameFiles files, int slot) {
        Path save = files.saveFile(slot), history = files.historyFile(slot);
        return new Stamp(save, sizeOf(save), timeOf(save), sizeOf(history), timeOf(history));
    }

    private static long sizeOf(Path file) {
        try {
            return Files.isRegularFile(file) ? Files.size(file) : -1;
        } catch (IOException e) {
            return -1;
        }
    }

    private static long timeOf(Path file) {
        try {
            return Files.isRegularFile(file) ? Files.getLastModifiedTime(file).toMillis() : -1;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * One slot, and whatever goes wrong in it stays in it (LB3's gate review): a slot whose header or history will not decode, or that runs the heap
     * out, is DAMAGED and logged, and the other slots are read as before, so one odd file never hides every saved city.
     */
    private static Entry readSafely(GameFiles files, int slot, Stamp stamp) {
        try {
            return readSlot(files, slot, stamp);
        } catch (RuntimeException | OutOfMemoryError e) {
            GameLog.note("Could not read " + GameFiles.slotLabel(slot) + " for the Rankings board: " + e);
            return left(slot, stamp, Why.DAMAGED);
        }
    }

    /**
     * One slot: the header through GameFiles.readHeader, then the history file read WHOLE (Files.readString, as Game.loadHistory does) and only
     * then parsed, cut to the kept series. Whole first, because a handle held open through a parse of seconds stops the game swapping a new history
     * in over the file on Windows (LB3's gate review: the slot kept its old history beside the new save); readHeader reads its file whole too.
     */
    private static Entry readSlot(GameFiles files, int slot, Stamp stamp) {
        if (stamp.saveSize() < 0) return new Entry(slot, stamp, null, null);
        SaveHeader header = files.readHeader(slot);
        if (header == null) return left(slot, stamp, Why.DAMAGED);
        if (header.isFromNewerBuild()) return left(slot, stamp, Why.NEWER_BUILD);
        if (header.isNoLongerReadable()) return left(slot, stamp, Why.OLDER_BUILD);
        if (stamp.historySize() < 0) return left(slot, stamp, Why.NO_HISTORY);
        HistorySave slim;
        try {
            String json = Files.readString(files.historyFile(slot));
            HistorySave loaded = new Gson().fromJson(json, HistorySave.class);
            slim = loaded == null ? null : loaded.slim(KEPT);
        } catch (IOException | RuntimeException e) {
            GameLog.note("Could not read the history of " + GameFiles.slotLabel(slot) + " (" + files.historyFile(slot) + "): " + e);
            return left(slot, stamp, Why.DAMAGED);
        }
        if (slim == null || slim.months() == 0) return left(slot, stamp, Why.NO_HISTORY);

        String name = header.getCityName();
        String currency = header.getCurrencyPlural() != null ? header.getCurrencyPlural() : Currency.fromCityName(name).plural();
        Denomination unit = new Denomination();
        unit.restore(header.getDenomination());
        double foundingRate = unit.money(ForeignAccounts.OPENING_RATE);
        int last = slim.getMonth().get(slim.months() - 1);
        Rankings.City now = Rankings.cityAt(name, currency, foundingRate, slim, extrasOf(header), last);
        if (now == null) return left(slot, stamp, Why.NO_HISTORY);
        // The price level of the last month, from the series; a history older than LB3 has none, and the header's world economy has the level it ended at.
        if (!Double.isFinite(now.usPrices())) {
            WorldEconomy world = new WorldEconomy();
            world.restore(header.getWorldEconomy());
            now = new Rankings.City(now.name(), now.currency(), now.month(), now.year(), now.fx(), now.parity(), world.getPriceLevel(), now.foundingRate(), now.figures());
        }
        return new Entry(slot, stamp, new Holding(name, currency, foundingRate, slim, last, now), null);
    }

    private static Entry left(int slot, Stamp stamp, Why why) {
        return new Entry(slot, stamp, null, why);
    }

    /** What the history does not keep, from the header: the people in each age band (PopulationCohorts.restore by name) and the city's dry ground in square kilometres (LandManager.km2). */
    private static CityMeasures.Extras extrasOf(SaveHeader header) {
        PopulationCohorts cohorts = new PopulationCohorts();
        cohorts.restore(header.getBandNames(), header.getCohorts());
        double[] bands = new double[AgeBand.values().length];
        for (AgeBand band : AgeBand.values()) bands[band.ordinal()] = cohorts.get(band);
        double area = header.getLandOwned() > 0 ? LandManager.km2(header.getLandOwned()) : Double.NaN;
        return new CityMeasures.Extras(area, bands);
    }

    /* ------------------------------- answering ------------------------------- */

    /** The saved cities at their last saved month, in slot order: their figures from the whole history and the header's land and bands, in the units of their last save. */
    public List<Rankings.Saved> now() {
        List<Rankings.Saved> out = new ArrayList<>();
        for (Entry e : entries) if (e.kept() != null) out.add(new Rankings.Saved(e.slot(), e.kept().now()));
        return out;
    }

    /**
     * The saved cities at game month `month` (D3, the active city's age), in slot order: each read from its history at that month, its rate, parity and price
     * level the series' there, its figures CityMeasures.at with Extras.UNKNOWN. A city that never got to the month, or whose history no longer holds it as a
     * month, is not here (stoppedAt names the first).
     */
    public List<Rankings.Saved> at(int month) {
        List<Rankings.Saved> out = new ArrayList<>();
        for (Entry e : entries) {
            if (e.kept() == null) continue;
            Holding k = e.kept();
            Rankings.City c = Rankings.cityAt(k.name(), k.currency(), k.foundingRate(), k.history(), CityMeasures.Extras.UNKNOWN, month);
            if (c != null) out.add(new Rankings.Saved(e.slot(), c));
        }
        return out;
    }

    /** The saved cities whose history ends before game month `month`: "stopped at Feb 2031" (CityCalendar.formatShort(lastMonth)), no figure and no row. In slot order. */
    public List<Stopped> stoppedAt(int month) {
        List<Stopped> out = new ArrayList<>();
        for (Entry e : entries) if (e.kept() != null && e.kept().lastMonth() < month) out.add(new Stopped(e.slot(), e.kept().name(), e.kept().lastMonth()));
        return out;
    }

    /** The saves left off the board and why, counted; an empty slot is no save and is not counted. */
    public LeftOut left() {
        int older = 0, newer = 0, damaged = 0, none = 0;
        for (Entry e : entries) {
            if (e.left() == null) continue;
            switch (e.left()) {
                case OLDER_BUILD -> older++;
                case NEWER_BUILD -> newer++;
                case DAMAGED -> damaged++;
                case NO_HISTORY -> none++;
            }
        }
        return new LeftOut(older, newer, damaged, none);
    }

    /** The slim history a slot keeps, or null when the slot is not a city on the board: for a harness that holds it to the file's. */
    HistorySave history(int slot) {
        Entry e = entryOf(slot);
        return e == null || e.kept() == null ? null : e.kept().history();
    }
}
