package ham.citybuildersim;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * The throwaway copy of a recorded city that replay shows (TR1, 2026-10-31): a Game of its own, in a scratch folder, that loads
 * any recorded month and is marked a replay copy (Game.markReplayCopy) so that it never records, and the recording and the
 * player's folder are never written. The month on screen is loaded in full ({@link #loadWhole}), so every screen shows exactly
 * that month; while the time bar plays, only the map's sidecar is swapped ({@link #stepQuick}), and the caller loads in full when
 * it comes to rest. No JavaFX, and everything runs on the calling thread - the FX thread in the game, never a worker: the
 * copy's Game is read by the screens as it loads.
 *
 * WHY IT EXISTS (TR1): Jerus films the game's trailer from a recording, as often as he likes, and clicks around the city in
 * navigation mode; "nothing sticks" - whatever a click changes in the copy is gone the next time a month is loaded, and moving
 * in time reloads from the recording. A full load is 330 to 800 ms on a 360-month Earth city, too slow for play at 1 to 16
 * months a second; the map is the part that moves, and it is tens of KB.
 *
 * The copy's save slot is {@link #SLOT}, in its own folder (GameFiles.scratch), never the player's: loadWhole writes the month's
 * JSON and map there and loads them as a Load does, so the same code that reads a player's save reads the recording's.
 */
public final class ReplayCity {

    /** The slot the copy loads the month from, in its own throwaway folder (GameFiles.scratch): never the player's folder. */
    public static final int SLOT = 10;

    private final Recording recording;
    private final GameFiles files;
    private Game game;
    private HistorySave master;
    private boolean masterRead;
    private int shown = -1;
    private boolean whole;
    private double lastLoadMs;
    private double lastStepMs;

    /** A replay of {@code recording}: the copy's folder is made now, the copy itself on the first load. */
    public ReplayCity(Recording recording) {
        this.recording = recording;
        this.files = GameFiles.scratch("replay");
    }

    public Recording recording() { return recording; }

    /** The copy, or null before the first load. */
    public Game game() { return game; }

    /** The copy's own folder: where loadWhole writes (for a check that nothing else is). */
    GameFiles files() { return files; }

    /** The months the recording holds, ascending. */
    public int[] months() { return recording.months(); }

    /** The month on screen, or -1 before the first. */
    public int shown() { return shown; }

    /** Whether the month on screen was loaded in full (false after a quick step, which swapped only the map). */
    public boolean wholeMonth() { return whole; }

    /** How long the last loadWhole took, in milliseconds. */
    public double lastLoadMs() { return lastLoadMs; }

    /** How long the last stepQuick took, in milliseconds. */
    public double lastStepMs() { return lastStepMs; }

    /**
     * Loads a recorded month in full: its save and map written to the copy's folder as slot {@link #SLOT} (the history file
     * deleted), loaded as the Load menu loads a save, then the recording's history cut at the month. The copy is the same Game
     * object load after load, built on the first (and marked a replay copy before it loads anything). Returns the copy's load
     * failure, or null when the month is on screen.
     */
    public String loadWhole(int month) {
        long began = System.nanoTime();
        String json = recording.saveJsonOf(month);
        if (json == null) return "Month " + month + " is not in this recording.";
        byte[] map = recording.mapOf(month);
        try {
            Files.createDirectories(files.savesDirectory());
            Files.write(files.saveFile(SLOT), json.getBytes(StandardCharsets.UTF_8));
            if (map != null) Files.write(files.mapFile(SLOT), map);
            else Files.deleteIfExists(files.mapFile(SLOT));
            Files.deleteIfExists(files.historyFile(SLOT));
        } catch (IOException e) {
            return "The month could not be put in the replay's folder: " + e.getMessage();
        }
        if (game == null) {
            game = new Game(files);
            game.markReplayCopy();
        }
        game.loadGameSave(SLOT);
        String failure = game.getLoadFailure();
        if (failure != null) return failure;
        game.replayHistory(historyAt(month));
        shown = month;
        whole = true;
        lastLoadMs = (System.nanoTime() - began) / 1e6;
        return null;
    }

    /**
     * Swaps only the map to a recorded month's: false, and nothing changed, when the month has no map, there is no copy yet,
     * or the land is not the one the map was drawn on (a purchase between) - the caller then loads the month whole.
     */
    public boolean stepQuick(int month) {
        if (game == null) return false;
        long began = System.nanoTime();
        byte[] map = recording.mapOf(month);
        if (map == null || !game.replayMap(map, month)) return false;
        shown = month;
        whole = false;
        lastStepMs = (System.nanoTime() - began) / 1e6;
        return true;
    }

    /** The recording's history, parsed once; null when the recording has none. */
    public HistorySave master() {
        if (!masterRead) {
            master = recording.history();
            masterRead = true;
        }
        return master;
    }

    /** The history as it stood at {@code month} (HistorySave.cutAt); an empty history when the recording has none. */
    public HistorySave historyAt(int month) {
        HistorySave all = master();
        return all == null ? new HistorySave() : all.cutAt(month);
    }

    /** Puts the history at {@code month} in the copy's charts: what a quick step leaves to its caller, because the cut is a copy of the whole history. */
    public void showHistoryAt(int month) {
        if (game != null) game.replayHistory(historyAt(month));
    }

    /** The recorded month nearest {@code month} (the earlier of two equally near), or -1 for a recording with none: a month the recorder could not keep is a gap. */
    public int nearest(int month) {
        int best = -1;
        for (int m : recording.months()) {
            if (best < 0 || Math.abs(m - month) < Math.abs(best - month)) best = m;
        }
        return best;
    }
}
