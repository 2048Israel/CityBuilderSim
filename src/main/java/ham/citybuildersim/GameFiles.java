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
    public Path saveFile()      { return directory.resolve(SAVE_FILE); }
    public Path historyFile()   { return directory.resolve(HISTORY_FILE); }

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

        if (legacyDirectory == null
                || !Files.isDirectory(legacyDirectory)
                || legacyDirectory.equals(directory)) {
            return copied;
        }

        for (String name : new String[] { SAVE_FILE, HISTORY_FILE }) {
            Path from = legacyDirectory.resolve(name);
            Path to = directory.resolve(name);
            try {
                if (Files.isRegularFile(from) && !Files.exists(to)) {
                    Files.createDirectories(directory);
                    Files.copy(from, to);
                    copied.add(name);
                }
            } catch (IOException e) {
                // Not fatal. A failed migration means the player starts fresh in
                // the new folder with the old one still intact behind them,
                // which is recoverable; stopping the game over it is not.
                System.out.println("Could not bring " + name
                        + " over from the old save folder: " + e.getMessage());
            }
        }

        return copied;
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
