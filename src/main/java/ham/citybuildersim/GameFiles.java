package ham.citybuildersim;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Where the game keeps its files, and how it writes them.
 *
 * WHY THIS EXISTS AT ALL
 *
 * The save path used to be a literal - Path.of(userHome, "YourGame", "save.json")
 * - written out FOUR separate times: in DataSave, in HistorySave, in
 * Game.loadGame() and in Game.loadHistory(). Four copies of one fact is three
 * chances to change it in three places and forget the fourth, and the failure
 * mode is the nastiest kind: the game saves happily to one folder and loads an
 * older file from another, so a player loses hours and nothing anywhere reports
 * an error.
 *
 * WHY NOT user.home/YourGame
 *
 * "YourGame" is a tutorial placeholder, and dropping a folder straight into
 * someone's home directory is the kind of thing people notice and resent. More
 * practically, Steam Cloud syncs from a fixed set of known roots - %APPDATA%
 * among them - and a bare folder in the home root is not one of them, so saves
 * kept there could never be synced.
 *
 * So: %APPDATA%\CityBuilderSim on Windows, and the equivalent convention on the
 * other two platforms, because getting this right once is cheaper than
 * discovering it after players have saves worth keeping.
 *
 * THE OLD FOLDER IS NOT DELETED
 *
 * migrateLegacy() COPIES the old files across and leaves the originals exactly
 * where they were. A migration that moves files is a migration that can destroy
 * a save if it half-runs, and the disk cost of leaving a few kilobytes behind is
 * nothing against the cost of being wrong.
 *
 * WRITING
 *
 * write() never writes over a good save. It writes a .tmp beside the target,
 * copies the existing save to .bak, and only then swaps the .tmp into place -
 * atomically where the filesystem supports it. If the disk is full or the
 * process dies mid-write, the worst case is a stray .tmp file and a save that is
 * one autosave old, instead of a truncated file that loads as a corrupt city.
 */
public final class GameFiles {

    public static final String APP_NAME = "CityBuilderSim";

    public static final String SAVE_FILE = "save.json";
    public static final String HISTORY_FILE = "history.json";

    /* --------------------------------- slots ---------------------------------
     *
     * Ten numbered slots and one autosave, each with its own save and its own
     * history. The history has to be per slot: it is every month that city ever
     * lived, so a single shared history file would draw slot 3's graph using
     * slot 7's past.
     *
     * The autosave is slot 0 and is not writable from the save menu, which is
     * the whole point of it - a player cannot accidentally spend their autosave
     * on the city they were about to abandon.
     * ------------------------------------------------------------------------ */

    public static final int SLOT_COUNT = 10;
    public static final int AUTOSAVE_SLOT = 0;
    public static final String SAVES_FOLDER = "saves";

    /** The folder the game used before this class existed. */
    static final String LEGACY_FOLDER = "YourGame";

    private final Path directory;
    private final Path legacyDirectory;

    public GameFiles() {
        this(defaultDirectory(), defaultLegacyDirectory());
    }

    /** For tests, which must never touch the real user's save folder. */
    GameFiles(Path directory, Path legacyDirectory) {
        this.directory = directory;
        this.legacyDirectory = legacyDirectory;
    }

    /* ------------------------------ locations ------------------------------ */

    public Path getDirectory()  { return directory; }

    /** The pre-slot single save. Still read on migration; never written now. */
    public Path legacyFlatSave()    { return directory.resolve(SAVE_FILE); }
    public Path legacyFlatHistory() { return directory.resolve(HISTORY_FILE); }

    public Path savesDirectory() { return directory.resolve(SAVES_FOLDER); }

    public Path saveFile(int slot)    { return savesDirectory().resolve(stem(slot) + ".json"); }
    public Path historyFile(int slot) { return savesDirectory().resolve(stem(slot) + "-history.json"); }

    /**
     * Zero-padded, so the folder sorts the way a person reads it and slot 10
     * does not land between 1 and 2.
     */
    private static String stem(int slot) {
        return (slot == AUTOSAVE_SLOT) ? "autosave" : String.format("slot-%02d", slot);
    }

    public static boolean isValidSlot(int slot) {
        return slot >= AUTOSAVE_SLOT && slot <= SLOT_COUNT;
    }

    /** "Autosave", "Slot 1"... - the label, before anything is known about the city. */
    public static String slotLabel(int slot) {
        return (slot == AUTOSAVE_SLOT) ? "Autosave" : "Slot " + slot;
    }

    public static Path defaultDirectory() {
        return resolveDirectory(
                System.getProperty("os.name"),
                System.getenv("APPDATA"),
                System.getenv("XDG_DATA_HOME"),
                System.getProperty("user.home"));
    }

    public static Path defaultLegacyDirectory() {
        return Path.of(System.getProperty("user.home"), LEGACY_FOLDER);
    }

    /**
     * The convention for each platform, as a pure function of its inputs.
     *
     * Kept free of System.getenv and System.getProperty on purpose: a directory
     * chooser that reads the real environment can only be tested on the machine
     * it happens to be running on, which is exactly the machine where a mistake
     * would not show up.
     */
    static Path resolveDirectory(String osName, String appData,
                                 String xdgDataHome, String userHome) {

        String os = (osName == null) ? "" : osName.toLowerCase(Locale.ROOT);
        String home = notBlank(userHome) ? userHome : ".";

        if (os.contains("win")) {
            // %APPDATA% is Roaming, which is where saves conventionally live and
            // which Steam Cloud can sync. The fallback is only for the odd
            // environment that has the variable stripped.
            return notBlank(appData)
                    ? Path.of(appData, APP_NAME)
                    : Path.of(home, "AppData", "Roaming", APP_NAME);
        }

        if (os.contains("mac") || os.contains("darwin")) {
            return Path.of(home, "Library", "Application Support", APP_NAME);
        }

        // Linux and anything else: the XDG base directory spec.
        return notBlank(xdgDataHome)
                ? Path.of(xdgDataHome, APP_NAME)
                : Path.of(home, ".local", "share", APP_NAME);
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    /* ------------------------------ migration ------------------------------ */

    /**
     * Brings saves over from the old folder. Returns what it copied, so the
     * caller can say so rather than moving a player's game silently.
     *
     * Deliberately refuses to overwrite anything already in the new location: if
     * both exist, the new one is the one the player has been playing, and the
     * old one is a fossil.
     */
    public List<String> migrateLegacy() {

        List<String> copied = new ArrayList<>();

        /*
         * Two hops, oldest first, and both land in slot 1 because that is the
         * only place a single save can sensibly go.
         *
         * Every step copies and refuses to overwrite. A player who has already
         * used slot 1 keeps what is in it; the old file stays where it is and
         * can be recovered by hand if it ever mattered.
         */

        // 1. user.home/YourGame -> the app folder's flat save (the pre-slot shape)
        if (legacyDirectory != null
                && Files.isDirectory(legacyDirectory)
                && !legacyDirectory.equals(directory)) {

            copyIfAbsent(legacyDirectory.resolve(SAVE_FILE), legacyFlatSave(),
                    SAVE_FILE + " (old folder)", copied);
            copyIfAbsent(legacyDirectory.resolve(HISTORY_FILE), legacyFlatHistory(),
                    HISTORY_FILE + " (old folder)", copied);
        }

        // 2. the flat save -> slot 1
        copyIfAbsent(legacyFlatSave(), saveFile(1), "save into Slot 1", copied);
        copyIfAbsent(legacyFlatHistory(), historyFile(1), "history into Slot 1", copied);

        return copied;
    }

    private void copyIfAbsent(Path from, Path to, String description,
                              List<String> copied) {
        try {
            if (Files.isRegularFile(from) && !Files.exists(to)) {
                Files.createDirectories(to.getParent());
                Files.copy(from, to);
                copied.add(description);
            }
        } catch (IOException e) {
            // Not fatal. A failed migration means the player starts fresh with
            // the old file still intact behind them, which is recoverable;
            // stopping the game over it is not.
            System.out.println("Could not bring " + description
                    + " over: " + e.getMessage());
        }
    }

    /* ------------------------------- reading ------------------------------- */

    /**
     * Reads just enough of a slot to label it, or null if the slot is empty.
     *
     * Never throws. A corrupt or half-written file in one slot must not stop the
     * menu drawing the other ten - it reads as empty, which is what the player
     * can act on anyway.
     */
    public SaveHeader readHeader(int slot) {

        Path file = saveFile(slot);
        if (!Files.isRegularFile(file)) return null;

        try {
            return new com.google.gson.Gson()
                    .fromJson(Files.readString(file), SaveHeader.class);
        } catch (IOException | RuntimeException e) {
            // Not a crash, and not silence either. A corrupt file in one slot
            // must not stop the other ten drawing - but it does go in the log,
            // because "the menu says Empty and it is not empty" is exactly the
            // kind of thing a player cannot diagnose and a log line can.
            GameLog.note("Could not read " + slotLabel(slot) + " ("
                    + saveFile(slot) + "): " + e);
            return null;
        }
    }

    /** No file at all. NOT the same as a file that cannot be read - see below. */
    public boolean slotIsEmpty(int slot) {
        return !Files.isRegularFile(saveFile(slot));
    }

    /**
     * True when a slot holds a file the game cannot make sense of.
     *
     * This distinction is the whole bug it was written for. readHeader() fails
     * safely on a truncated or corrupt save and returns null, so the menu drew
     * the slot as "Empty" - but slotIsEmpty() asks whether the FILE exists, and
     * it does, so the Load button stayed enabled. The player clicked Load on a
     * slot labelled Empty and the game did nothing whatsoever: no message, no
     * error, no load, because the JsonSyntaxException went to a console that
     * does not exist.
     *
     * Empty and unreadable are different things and the player is entitled to
     * know which one they are looking at.
     */
    public boolean slotIsUnreadable(int slot) {
        return !slotIsEmpty(slot) && readHeader(slot) == null;
    }

    /** Loadable: something is there, and it can be read. */
    public boolean slotIsLoadable(int slot) {
        SaveHeader header = readHeader(slot);
        return header != null && !header.isFromNewerBuild();
    }

    /* ------------------------------- writing ------------------------------- */

    /**
     * What a write attempt did. Returned rather than thrown, because every
     * caller here has to tell the player something either way, and an exception
     * that gets caught and turned into a println is just a Result with extra
     * steps.
     */
    public static final class Result {

        public final boolean ok;
        public final Path file;
        public final String error;

        private Result(boolean ok, Path file, String error) {
            this.ok = ok;
            this.file = file;
            this.error = error;
        }

        static Result succeeded(Path file) { return new Result(true, file, null); }

        /** For a failure that is not an exception - see DataSave.saveGame(). */
        static Result failed(Path file, String reason) {
            return new Result(false, file, reason);
        }

        static Result failed(Path file, Throwable cause) {
            String detail = (cause.getMessage() == null)
                    ? cause.getClass().getSimpleName()
                    : cause.getMessage();
            return new Result(false, file, detail);
        }

        /** One line, fit to show a player. */
        public String message() {
            return ok ? "Saved to " + file
                      : "Could not save to " + file + " - " + error;
        }
    }

    public Result write(Path file, String contents) {

        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");

        try {
            Files.createDirectories(file.getParent());

            // Everything lands in the temporary file first. If this is the call
            // that runs out of disk, the existing save has not been touched.
            Files.writeString(tmp, contents);

            if (Files.exists(file)) {
                Files.copy(file, file.resolveSibling(file.getFileName() + ".bak"),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            try {
                Files.move(tmp, file,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                // Some filesystems (and moves across devices) cannot do it
                // atomically. A plain replace is still better than writing the
                // target directly, because the content is already known good.
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }

            return Result.succeeded(file);

        } catch (IOException | RuntimeException e) {
            deleteQuietly(tmp);
            return Result.failed(file, e);
        }
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // A leftover .tmp is untidy, not harmful. Nothing reads it.
        }
    }
}
