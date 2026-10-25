package ham.citybuildersim.tools;

import ham.citybuildersim.BuildingManager;
import ham.citybuildersim.EarthData;
import ham.citybuildersim.Game;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.LandClassCheck;
import ham.citybuildersim.LandClassView;
import ham.citybuildersim.LandManager;
import ham.citybuildersim.LandMarket;
import ham.citybuildersim.LandParcel;
import ham.citybuildersim.LongPlaytest;
import ham.citybuildersim.Sector;
import ham.citybuildersim.SectorBooks;
import ham.citybuildersim.SectorStatements;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Renders what ET3 says about the classes of ground, for review: the land office's prices and offers by class and note 11
 * of the sector statements, in the words the screens use (LandClassView), for cities played on the real Earth.
 *
 *     java -Dcbs.earth=<earth-2.bin> -cp "target/classes;<gson.jar>" ham.citybuildersim.tools.LandClassRenders [earth-2.bin [outDir [name...]]]
 *
 * The file is the argument, else -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); outDir is docs/queue/ET3
 * unless named; names (render-a, render-b, render-c) pick which to write, all three otherwise. -Dlandclass.months=N plays
 * Medellin and Rotterdam N months (2,000 unless named, SlopeRenders' length, at which the land office's offers have
 * brought a city in the hills steep ground) and -Dlandclass.early=N plays the early Medellin render A also shows
 * (120, when the going price is low and foothill sits at the base). A city is founded as the playtest founds one
 * (LongPlaytest.earthCity(): a GameFiles.scratch() folder deleted when the JVM exits - never the real saves). It writes:
 *
 *   et3-a-land-office.txt   LandClassView.officeLines() and every offer on the shelf's offerLine(), Medellin at the later
 *                           month and at the early one
 *   et3-b-note-11.txt       note 11 (LandClassView.landNoteLines()) for each sector of Medellin that holds any land by class,
 *                           with the sheet's Land line it adds up to, and says whether the run has a works sector among them
 *                           (the playtest builds no mine on the steep ground); then the same for LandClassCheck.hillyCity(), a
 *                           fixture of houses on flat and foothill and iron mines on steep ground, which has one
 *   et3-c-rotterdam.txt     the same lines for Rotterdam's city, which has no foothill and no steep ground: no class line
 *                           in the office, no note 11
 *
 * WHY (ET3 slice C): the screens are checked by eye, and the words are written in one place so that they can be read
 * here without JavaFX. Not a harness: nothing here asserts (LandClassCheck 7 does), and nothing under docs/ but its
 * outDir is written.
 */
public final class LandClassRenders {

    private LandClassRenders() { }

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.CANADA);
        Path file = args.length > 0 ? Path.of(args[0]) : EarthData.locate();
        if (file == null || !Files.isRegularFile(file)) {
            System.out.println("no earth-2 file: name it, or -Dcbs.earth=<path>, or put it at " + EarthData.DATA_FILE);
            return;
        }
        System.setProperty(EarthData.PROPERTY, file.toString());
        Path outDir = args.length > 1 ? Path.of(args[1]) : Path.of("docs", "queue", "ET3");
        java.util.Set<String> only = new java.util.HashSet<>();
        for (int k = 2; k < args.length; k++) only.add(args[k]);
        Files.createDirectories(outDir);
        boolean a = only.isEmpty() || only.contains("render-a"), b = only.isEmpty() || only.contains("render-b"),
                c = only.isEmpty() || only.contains("render-c");
        int months = Integer.getInteger("landclass.months", 2000), early = Integer.getInteger("landclass.early", 120);
        PrintStream silent = new PrintStream(new OutputStream() { @Override public void write(int x) { } });
        if (a || b) {
            Game g = play("medellin", months, silent);
            if (a) {
                List<String> text = new ArrayList<>();
                text.add("ET3 render A - the land office's prices and offers by class (LandClassView.officeLines(), offerLine())");
                text.add("");
                office(text, g, "Medellin, month " + g.getMonth());
                text.add("");
                office(text, play("medellin", early, silent), "Medellin, month " + early + " (the going price low, foothill at the base)");
                writeLf(outDir.resolve("et3-a-land-office.txt"), text);
            }
            if (b) {
                List<String> text = new ArrayList<>();
                text.add("ET3 render B - note 11 of the sector balance sheets, a sector's land by the ground it stands on (LandClassView.landNoteLines())");
                text.add("");
                notes(text, g, "Medellin, month " + g.getMonth());
                text.add("");
                notes(text, LandClassCheck.hillyCity("landclassrenders-hilly"), "LandClassCheck's hilly fixture: 100 houses and 2 iron mines on flat, foothill and steep ground, the going price four preparations over the base");
                writeLf(outDir.resolve("et3-b-note-11.txt"), text);
            }
        }
        if (c) {
            Game g = play("rotterdam", months, silent);
            List<String> text = new ArrayList<>();
            text.add("ET3 render C - the same lines for Rotterdam's city, which stands on flat ground only");
            text.add("");
            office(text, g, "Rotterdam, month " + g.getMonth());
            text.add("");
            notes(text, g, "Rotterdam, month " + g.getMonth());
            writeLf(outDir.resolve("et3-c-rotterdam.txt"), text);
        }
        System.exit(0);
    }

    /** Writes a render's lines with LF endings, as the tree stores text (Files.write() would use the PC's CRLF). */
    private static void writeLf(Path file, List<String> text) throws IOException {
        Files.writeString(file, String.join("\n", text) + "\n");
    }

    private static Game play(String spot, int months, PrintStream silent) {
        long t0 = System.currentTimeMillis();
        Game g = LongPlaytest.earthCity(spot, months, GameFiles.scratch("landclassrenders-" + spot), silent);
        LandManager lm = g.getLandManager();
        System.out.printf("a %s city played %d months in %d s: %.3f km2 owned, flat %.3f, foothill %.3f, steep %.3f%n", spot, g.getMonth(),
                (System.currentTimeMillis() - t0) / 1000, LandManager.km2(lm.getOwnedSqFt()), LandManager.km2(lm.getFlatSqFt()),
                LandManager.km2(lm.getFoothillSqFt()), LandManager.km2(lm.getSlopeSqFt()));
        return g;
    }

    /** A city's ground, the office's prices by class and every offer on the shelf with its ground. */
    private static void office(List<String> text, Game g, String title) {
        LandManager lm = g.getLandManager();
        LandMarket m = lm.getMarket();
        String mark = g.getCurrency().qualifiedSymbol();
        text.add("== " + title + " ==");
        text.add(String.format("The city owns %s: flat %s, foothill %s, steep and very steep %s (%s of it worked).",
                LandManager.areaWords(lm.getOwnedSqFt()), LandManager.areaWords(lm.getFlatSqFt()), LandManager.areaWords(lm.getFoothillSqFt()),
                LandManager.areaWords(lm.getSlopeSqFt()), LandManager.areaWords(lm.getWorksOnSlopeSqFt())));
        text.add("The going price a m2: " + LandClassView.local(mark, lm.getPricePerSqFt()) + " flat; the base price "
                + LandClassView.local(mark, m.getBasePricePerSqFt()) + "; a foothill m2's preparation " + LandClassView.local(mark, lm.getPreparationPerSqFt()) + ".");
        text.add("");
        List<String> lines = LandClassView.officeLines(g);
        text.add("officeLines() - " + (lines.isEmpty() ? "none: no foothill and no steep ground owned or on the shelf" : lines.size() + " lines") + ":");
        for (String line : lines) text.add("  " + line);
        text.add("");
        List<LandParcel> shelf = g.landShelf();
        text.add("The shelf, " + shelf.size() + " offers, each with offerLine():");
        for (LandParcel p : shelf) {
            String line = LandClassView.offerLine(p, m);
            text.add(String.format("  %-22s %s dry, listed %s%s", p.where(), LandManager.areaWords(p.getSizeSqFt()),
                    LandClassView.usd0(p.getPriceUsd()), line.isEmpty() ? " - all flat, no line" : ":"));
            if (!line.isEmpty()) text.add("      " + line);
        }
    }

    /** Note 11 for every sector that holds any land by class, services and works, with the sheet's Land line it adds up to. */
    private static void notes(List<String> text, Game g, String title) {
        SectorBooks books = g.getSectorBooks();
        BuildingManager bm = g.getBuildingManager();
        String mark = g.getCurrency().qualifiedSymbol();
        text.add("== " + title + " ==");
        int shown = 0, works = 0;
        for (Sector s : g.getSectors().all()) {
            SectorBooks.SectorMonth now = books.get(s);
            if (now == null) continue;
            List<String> lines = LandClassView.landNoteLines(now, mark);
            if (lines.isEmpty()) continue;
            shown++;
            boolean isWorks = bm.getWorksLandSqFtBySector(s.key(), Game::standsAnywhere) > 0;
            if (isWorks) works++;
            SectorStatements.Table sheet = SectorStatements.sheet(now, books.previous(s), books.debt(s.key()), books.debtBefore(s.key()));
            text.add(s.label() + (isWorks ? " (with works)" : " (buildings only)") + " - the sheet's Land " + LandClassView.marked(mark, LandClassView.money(sheet.now(SectorStatements.LAND)))
                    + ", note " + SectorStatements.LAND_NOTE + ":");
            for (String line : lines) text.add("  " + line);
        }
        if (shown == 0) {
            text.add("No sector's books carry its land by class: the city owns no foothill and no steep ground, so the sheet's Land is one line with no note 11.");
        } else {
            text.add("");
            text.add(shown + " sectors hold land by class; " + (works == 0 ? "none of them has works: this run has no works sector holding land by class."
                    : works + (works == 1 ? " of them has" : " of them have") + " works."));
        }
    }
}
