package ham.citybuildersim;

import java.nio.file.*;

/** Fixture producer: compile and run only against the shipped format-60 source. */
public final class CardFormat60Fixture {
    public static void main(String[] args) throws Exception {
        if (GameVersion.SAVE_FORMAT != 60) throw new AssertionError("Use the native format-60 build");
        Game g = LongPlaytest.probeCity(GameFiles.scratch("hc1-native-format60"), null);
        g.simulateMonths(12);
        if (!g.prepareSaveForChecks(10, "HC1 native format-60 fixture").write().whole())
            throw new AssertionError("Save did not finish");
        Path out = Path.of(args[0]);
        Files.createDirectories(out);
        Files.copy(g.getGameFiles().saveFile(10), out.resolve("save.json"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(g.getGameFiles().historyFile(10), out.resolve("history.json"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Native format60 " + GameVersion.VERSION + ", month " + g.getMonth());
    }
}
