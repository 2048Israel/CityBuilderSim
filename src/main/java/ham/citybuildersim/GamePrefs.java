package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * How the player likes the window, kept between runs.
 *
 * WHY THIS IS NOT IN THE SAVE FILE. Graphs and reports live in DataSave, which
 * is right for them - they are settings ABOUT a city and a different city can
 * reasonably want different ones. Whether the game runs full screen is a fact
 * about the monitor it is running on, and filing it per city would mean loading
 * slot 3 changed the shape of the window. So it lives beside the saves rather
 * than inside one, and every city on this machine shares it.
 *
 * WHY NOT java.util.prefs. That would work and takes about four lines, but on
 * Windows it writes into the registry, where a player cannot find it, cannot
 * back it up with the rest of the game folder, and cannot delete it when it
 * goes wrong. Everything else this game keeps is a JSON file in one folder, and
 * a settings file nobody can locate is a support problem waiting to happen.
 *
 * NOTHING HERE IS LOAD-BEARING. A missing, empty or corrupt file is not an
 * error - it is a player who has never changed a setting, and the defaults are
 * the answer. That is why every method swallows and logs rather than throwing:
 * a game that will not start because it could not read a preference is a worse
 * game than one that starts windowed.
 *
 * @author Jerus
 */
public final class GamePrefs {

    public static final String FILE = "settings.json";

    /**
     * Full screen by default.
     *
     * Jerus: "when you play the game its full scren, like not top white bar
     * with game name and not bottom windows bar." A city builder is a screen
     * you sit in front of for an hour, and the title bar and taskbar are two
     * strips of somebody else's chrome around it. F11 gets you out.
     */
    private boolean fullScreen = true;

    public boolean isFullScreen()            { return fullScreen; }
    public void setFullScreen(boolean value) { this.fullScreen = value; }

    /**
     * Which of the two the city panel is showing.
     *
     * Jerus: "i think you can switch from summary and dashboard, like at a
     * switch, so uh both?" - so it is a preference rather than a mode the game
     * decides, and it belongs here rather than in a save: it is how the player
     * likes to read, not something true about a particular city.
     *
     * DASHBOARD BY DEFAULT, because that is what the panel already was and a
     * setting should not change what an existing player sees the first time
     * they launch after it lands.
     */
    private boolean panelDashboard = true;

    public boolean isPanelDashboard()            { return panelDashboard; }
    public void setPanelDashboard(boolean value) { this.panelDashboard = value; }

    /* ===================================================================
       THE FILE
       =================================================================== */

    private static Path fileIn(GameFiles files) {
        return files.getDirectory().resolve(FILE);
    }

    /** Whatever is on disk, or the defaults. Never null, never throws. */
    public static GamePrefs load(GameFiles files) {
        try {
            Path file = fileIn(files);
            if (!Files.isRegularFile(file)) return new GamePrefs();

            GamePrefs read = new com.google.gson.Gson()
                    .fromJson(Files.readString(file), GamePrefs.class);
            return read == null ? new GamePrefs() : read;

        } catch (Exception e) {
            // A note rather than a crash: see the class comment. The player
            // gets the defaults and a line in the log saying why.
            GameLog.note("Could not read " + FILE + ": " + e);
            return new GamePrefs();
        }
    }

    /**
     * Writes them back, through the same atomic replace the saves use.
     *
     * @return true if it landed - the caller may want to say so, and may
     *         equally not care, which is why this reports rather than throws
     */
    public boolean save(GameFiles files) {
        try {
            return files.write(fileIn(files),
                    new com.google.gson.Gson().toJson(this)).ok;
        } catch (Exception e) {
            GameLog.note("Could not write " + FILE + ": " + e);
            return false;
        }
    }
}
