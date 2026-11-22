package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A native format-60 city and history, refused before either file or a running city is changed.
 * WHY. Restamping a current save or testing the floor predicate alone does not exercise the actual old shape.
 * The two compressed resources are captured once on main 01e6c2d4b (0.9.188, format 60), without JSON edits.
 */
final class Format60SaveFixture {
    private static final Gson JSON = new Gson();
    private static final String SAVE = "format60-save.json.gz", HISTORY = "format60-history.json.gz";

    private static void quietly(Runnable work) {
        PrintStream prior = System.out;
        try (PrintStream quiet = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(quiet);
            try { work.run(); } finally { System.setOut(prior); }
        }
    }

    private static void unpack(String name, Path destination) throws IOException {
        try (var resource = Format60SaveFixture.class.getResourceAsStream("/checks/" + name)) {
            if (resource == null) throw new IOException("missing native format-60 fixture: " + name);
            try (var gzip = new GZIPInputStream(resource)) { Files.copy(gzip, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING); }
        }
    }

    private static String state(Game game) {
        JsonObject data = JSON.toJsonTree(game.prepareSaveForChecks(10, "format60 refusal state").data()).getAsJsonObject();
        data.remove("savedAt");
        return data + "\n" + JSON.toJson(game.getHistorySave().snapshot());
    }

    /** Shared by the save, substation and real-read harnesses; no save slot outside the scratch folder is touched. */
    static boolean checks(boolean readPaths) {
        try {
            GameFiles files = GameFiles.scratch("format60-refusal");
            Game live = new Game(files);
            quietly(() -> { live.newGame(); live.simulateMonths(3); });
            if (!live.prepareSaveForChecks(10, "current twin before format60 refusal").write().whole()) return false;
            Game running = new Game(files);
            quietly(() -> running.loadGameSave(10));
            if (running.getLoadFailure() != null || running.getMonth() <= 1) return false;
            if (readPaths) quietly(() -> ReadPathCheck.readEverything(running));
            BuildingManager buildings = running.getBuildingManager();
            HistorySave history = running.getHistorySave();
            String beforeCity = state(running);

            unpack(SAVE, files.saveFile(10));
            unpack(HISTORY, files.historyFile(10));
            byte[] beforeSave = Files.readAllBytes(files.saveFile(10)), beforeHistory = Files.readAllBytes(files.historyFile(10));
            JsonObject data = JsonParser.parseString(Files.readString(files.saveFile(10))).getAsJsonObject();
            JsonObject oldHistory = JsonParser.parseString(Files.readString(files.historyFile(10))).getAsJsonObject();
            JsonObject power = data.getAsJsonObject("powerBooks");
            boolean nativeShape = data.get("saveFormat").getAsInt() == 60 && "0.9.188".equals(data.get("gameVersion").getAsString())
                    && data.get("month").getAsInt() == 5 && oldHistory.getAsJsonArray("month").size() == 4 && power != null
                    && !power.has("pendingGridCosts") && !power.getAsJsonObject("struck").has("gridCosts");
            System.out.println((nativeShape ? "  OK   " : "  FAIL ") + "native format-60 city and history exist without the post-60 grid fields");
            Game blank = new Game(files);
            quietly(() -> blank.loadGameSave(10));
            boolean refused = SaveFileCheck.refusedAtTheFloor(files, 10, blank, beforeSave)
                    && Arrays.equals(beforeHistory, Files.readAllBytes(files.historyFile(10)));
            System.out.println((refused ? "  OK   " : "  FAIL ") + "the native format-60 load is refused, naming its build and preserving both files");
            quietly(() -> running.loadGameSave(10));
            if (readPaths) quietly(() -> ReadPathCheck.readEverything(running));
            SaveHeader header = files.readHeader(10);
            boolean intact = header != null && GameVersion.noLongerReadable(header.getGameVersion()).equals(running.getLoadFailure())
                    && running.getBuildingManager() == buildings && running.getHistorySave() == history && beforeCity.equals(state(running))
                    && Arrays.equals(beforeSave, Files.readAllBytes(files.saveFile(10)))
                    && Arrays.equals(beforeHistory, Files.readAllBytes(files.historyFile(10)));
            System.out.println((intact ? "  OK   " : "  FAIL ") + "refusing format60 into a running loaded twin preserves its full saved state, history and both files through reads");
            return nativeShape && refused && intact;
        } catch (Exception e) {
            System.out.println("  FAIL native format-60 refusal fixture: " + e);
            return false;
        }
    }

    /** Capture only on the actual old executable; never create an old fixture by relabelling a new save. */
    public static void main(String[] args) throws Exception {
        if (args.length != 1 || GameVersion.SAVE_FORMAT != 60 || !"0.9.188".equals(GameVersion.VERSION))
            throw new IllegalStateException("capture requires unmodified 0.9.188/format60 classes and an output directory");
        Game game = new Game(GameFiles.scratch("native-format60-capture"));
        quietly(() -> {
            game.newGame();
            game.setCashForTest(1_000_000_000 * PriceLevel.FACTOR);
            game.getLandManager().setOwnedSqFt(game.getLandManager().getOwnedSqFt() + 400_000_000L);
            if (game.buildStack(game.getBuildingManager().getTemplateByName("House"), 20, true) != Game.BuildResult.SUCCESS
                    || game.buildStack(game.getBuildingManager().getTemplateByName("Small Grocery Store"), 1, true) != Game.BuildResult.SUCCESS)
                throw new AssertionError("native format60 fixture buildings refused");
            game.simulateMonths(4);
        });
        if (!game.prepareSaveForChecks(10, "Native 0.9.188 format60 refusal fixture").write().whole()) throw new AssertionError("native capture incomplete");
        Path destination = Path.of(args[0]); Files.createDirectories(destination);
        Path[] input = {game.getGameFiles().saveFile(10), game.getGameFiles().historyFile(10)};
        String[] names = {SAVE, HISTORY};
        for (int i = 0; i < input.length; i++) try (var gzip = new GZIPOutputStream(Files.newOutputStream(destination.resolve(names[i])))) {
            Files.copy(input[i], gzip);
        }
        System.out.println("Captured native format60 save and history at month " + game.getMonth());
    }
}
