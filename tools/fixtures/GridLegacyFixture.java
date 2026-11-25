package ham.citybuildersim;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.zip.GZIPOutputStream;

/** Capture genuine published pre-PW5 bytes; never restamp or strip a current save. */
public final class GridLegacyFixture {
    public static void main(String[] args) throws Exception {
        if (args.length != 3 || GameVersion.SAVE_FORMAT != Integer.parseInt(args[1]) || !GameVersion.VERSION.equals(args[2]))
            throw new AssertionError("Use the requested native published format/version");
        Game game = LongPlaytest.probeCity(GameFiles.scratch("pw5-native-pre-grid"), null);
        game.simulateMonths(12);
        if (!game.prepareSaveForChecks(10, "PW5 native pre-grid fixture").write().whole()) throw new AssertionError("Incomplete native save");
        byte[] save = Files.readAllBytes(game.getGameFiles().saveFile(10));
        byte[] history = Files.readAllBytes(game.getGameFiles().historyFile(10));
        var data = JsonParser.parseString(new String(save, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
        var power = data.getAsJsonObject("powerBooks");
        if (power == null || power.has("pendingGridCosts") || power.getAsJsonObject("struck").has("gridCosts"))
            throw new AssertionError("Not a native pre-grid executable");
        Path out = Path.of(args[0]); Files.createDirectories(out);
        String[] names = {"save", "history"}; byte[][] records = {save, history};
        for (int i = 0; i < names.length; i++) {
            try (var zip = new GZIPOutputStream(Files.newOutputStream(out.resolve(names[i] + ".json.gz")))) { zip.write(records[i]); }
            System.out.println(names[i] + " raw SHA256 " + HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(records[i])));
        }
        System.out.println("Native format" + GameVersion.SAVE_FORMAT + " " + GameVersion.VERSION + " month " + game.getMonth());
    }
}
