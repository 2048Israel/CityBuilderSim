package ham.citybuildersim;

import java.nio.file.*;

/** Run alone against unedited shipped 0.9.196 classes to produce the RES3 refusal pair. */
public final class HousingFormat64Fixture {
    public static void main(String[] args) throws Exception {
        if (GameVersion.class.getField("SAVE_FORMAT").getInt(null) != 64
                || !GameVersion.class.getField("VERSION").get(null).equals("0.9.196"))
            throw new AssertionError("Use the shipped 0.9.196 format-64 build");
        Game g = LongPlaytest.probeCity(GameFiles.scratch("res3-native-format64"), null);
        g.simulateMonths(12);
        if (!g.prepareSaveForChecks(10, "RES3 native format-64 fixture").write().whole())
            throw new AssertionError("Native save/history did not finish");
        Path out = Path.of(args[0]);
        Files.createDirectories(out);
        Files.copy(g.getGameFiles().saveFile(10), out.resolve("save.json"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(g.getGameFiles().historyFile(10), out.resolve("history.json"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Native format64 " + GameVersion.VERSION + ", month " + g.getMonth());
    }
}
