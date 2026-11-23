package ham.citybuildersim;

import java.nio.file.*;

/** Fixture producer: compile and run only against the shipped format-61 source. */
public final class CardFormat61Fixture {
    public static void main(String[] args) throws Exception {
        if (GameVersion.SAVE_FORMAT != 61) throw new AssertionError("Use the native format-61 build");
        Game g = LongPlaytest.probeCity(GameFiles.scratch("hc1-native-format61"), null);
        g.simulateMonths(12);
        if (!g.prepareSaveForChecks(10, "HC1 native format-61 fixture").write().whole())
            throw new AssertionError("Save did not finish");
        Path out = Path.of(args[0]);
        Files.createDirectories(out);
        Files.copy(g.getGameFiles().saveFile(10), out.resolve("save.json"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(g.getGameFiles().historyFile(10), out.resolve("history.json"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Native format61 " + GameVersion.VERSION + ", month " + g.getMonth());
    }
}
