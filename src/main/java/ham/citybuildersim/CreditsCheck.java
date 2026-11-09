package ham.citybuildersim;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The Credits page shows every attribution file the exe ships, in full, and the fonts' notice and
 * licence, and says where a file is when this build lacks it (CR1, 2026-11-09).
 *
 * WHY THIS EXISTS. The game is sold, and the licences of its data (CC BY among them) and of its
 * type (the SIL Open Font License) ask that the credit reach the player. The page reads the files
 * from the jar, which pom.xml copies from tools/; Build EXE.bat copies the same files beside the
 * exe. A third attribution file added to the batch file and not to the page, a page cut down from
 * its file, or a font added without its licence would each leave a credit unshown without any
 * error. Files are read from the working directory (the repository), as BrandingCheck reads
 * them; elsewhere those sections say so and are skipped.
 *
 * What it has to prove:
 *
 *   1. EVERY FILE THE EXE SHIPS AS A CREDIT IS ON THE PAGE, IN FULL. Each ATTRIBUTION copy in
 *      Build EXE.bat has a section whose text is the file under tools/, exactly, and whose
 *      besideExe is the copy's destination.
 *   2. THE FONTS' NOTICE AND LICENCE. Every font in resources/fonts/ is IBM Plex, OFL.txt carries
 *      IBM's copyright and the licence's name, the notice says both, and the fonts' section is
 *      the notice over the licence in full.
 *   3. A BUILD WITHOUT A FILE SAYS WHERE THE FILE IS. A section whose text is null is missing(),
 *      the others are not, and the note names the file beside the exe.
 *   4. THE PARAGRAPHS KEEP THE WHOLE TEXT. Cut on blank lines, no non-blank line is lost or
 *      moved; a blank run is one cut and a \r goes.
 *   5. EACH SHIPPED FILE NAMES THE PAGE. Both files say "Credits page", where the game shows them.
 */
public class CreditsCheck {

    private static int fails;

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    static String read(Path p) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    /** The text's non-blank lines, in order, with any \r gone. */
    static List<String> lines(String text) {
        List<String> out = new ArrayList<>();
        for (String line : text.replace("\r", "").split("\n", -1)) if (!line.isBlank()) out.add(line);
        return out;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== CreditsCheck: the Credits page carries every attribution the exe ships and the fonts' licence, in full (CR1) ===");

        Path bat = Path.of("Build EXE.bat");
        Path fonts = Path.of("src", "main", "resources", "fonts");
        boolean inRepository = Files.exists(bat) && Files.isDirectory(fonts);
        if (!inRepository) System.out.println("  (no Build EXE.bat in " + Path.of("").toAbsolutePath() + ": not run from the repository, the file sections are skipped)");
        List<Credits.Section> sections = Credits.sections();
        final String mark = "\u00a9";

        System.out.println("--- every file the exe ships as a credit is on the page, in full ---");
        if (inRepository) {
            Matcher m = Pattern.compile("(?im)^\\s*copy\\s+(?:/\\w+\\s+)*\"([^\"]*ATTRIBUTION[^\"]*\\.txt)\"\\s+\"([^\"]+)\"").matcher(read(bat));
            int found = 0;
            List<String[]> copies = new ArrayList<>();
            while (m.find()) copies.add(new String[] {m.group(1).replace('\\', '/'), m.group(2).replace('\\', '/')});
            check("Build EXE.bat copies at least two ATTRIBUTION files beside the exe", copies.size() >= 2, copies.size() + " found");
            for (String[] c : copies) {
                String source = c[0], dest = c[1].substring(c[1].lastIndexOf("/data/") + 1);
                String resource = "/credits/" + (source.startsWith("tools/") ? source.substring("tools/".length()) : source);
                Credits.Section s = null;
                for (Credits.Section each : sections) if (each.resource().equals(resource)) s = each;
                check(source + " has a section on the page, reading " + resource, s != null, null);
                if (s == null) continue;
                check(source + "'s section is not missing", !s.missing(), s.title());
                if (!s.missing()) {
                    String file = read(Path.of(source));
                    check(source + "'s section shows the file exactly", s.text().equals(file),
                            s.text().length() + " characters on the page, " + file.length() + " in the file");
                }
                check(source + "'s section names " + dest + " as the file beside the exe", dest.equals(s.besideExe()), String.valueOf(s.besideExe()));
                found++;
            }
            check("every one of them was matched to a section", found == copies.size(), found + " of " + copies.size());
        } else {
            System.out.println("  (skipped)");
        }

        System.out.println("--- the fonts' notice and licence ---");
        Credits.Section fontsSection = null;
        for (Credits.Section s : sections) if (s.resource().equals(Credits.FONTS)) fontsSection = s;
        check("the page has a fonts section reading " + Credits.FONTS, fontsSection != null, null);
        check("the notice says 2017 IBM Corp.", Credits.FONT_NOTICE.contains("2017 IBM Corp."), null);
        check("the notice names the SIL Open Font License, Version 1.1", Credits.FONT_NOTICE.contains("SIL Open Font License, Version 1.1"), null);
        if (fontsSection != null) {
            check("the fonts' section leads with the notice", Credits.FONT_NOTICE.equals(fontsSection.lead()), String.valueOf(fontsSection.lead()));
            check("the fonts' section is not missing", !fontsSection.missing(), null);
        }
        if (inRepository) {
            int count = 0;
            List<String> strangers = new ArrayList<>();
            try (Stream<Path> files = Files.list(fonts)) {
                for (Path f : (Iterable<Path>) files::iterator) {
                    String name = f.getFileName().toString();
                    if (!name.endsWith(".ttf") && !name.endsWith(".otf")) continue;
                    count++;
                    if (!name.startsWith("IBMPlex")) strangers.add(name);
                }
            }
            check("there is at least one font in resources/fonts/", count >= 1, count + " found");
            check("every font there is IBM Plex, whose licence the page carries", strangers.isEmpty(), strangers.toString());
            Path ofl = fonts.resolve("OFL.txt");
            check("OFL.txt is beside the fonts", Files.exists(ofl), null);
            if (Files.exists(ofl)) {
                String licence = read(ofl);
                check("OFL.txt carries Copyright " + mark + " 2017 IBM Corp.", licence.contains("Copyright " + mark + " 2017 IBM Corp."), null);
                check("OFL.txt names the SIL Open Font License, Version 1.1", licence.contains("SIL Open Font License, Version 1.1"), null);
                if (fontsSection != null && !fontsSection.missing()) {
                    check("the fonts' section shows OFL.txt exactly", fontsSection.text().equals(licence),
                            fontsSection.text().length() + " characters on the page, " + licence.length() + " in the file");
                }
            }
        }

        System.out.println("--- a build without a file says where the file is ---");
        List<Credits.Section> without = Credits.sections(path -> path.equals(Credits.NATIONS) ? null : Credits.readResource(path));
        for (Credits.Section s : without) {
            boolean gone = s.resource().equals(Credits.NATIONS);
            check(s.title() + (gone ? " is missing when its file is not read" : " is not missing"), s.missing() == gone, null);
            if (gone) {
                String note = Credits.missingNote(s);
                check("and its note names " + s.besideExe(), s.besideExe() != null && note.contains(s.besideExe()), note);
            }
        }

        System.out.println("--- the paragraphs keep the whole text ---");
        for (Credits.Section s : sections) {
            if (s.missing()) {
                check(s.title() + " has no text here, so no paragraphs to hold", Credits.paragraphs(s.text()).isEmpty(), null);
                continue;
            }
            List<String> paragraphs = Credits.paragraphs(s.text());
            List<String> kept = new ArrayList<>();
            boolean blankParagraph = false;
            for (String p : paragraphs) {
                kept.addAll(lines(p));
                if (p.isBlank() || p.contains("\r")) blankParagraph = true;
            }
            check(s.title() + " loses no non-blank line and moves none", kept.equals(lines(s.text())),
                    kept.size() + " lines in " + paragraphs.size() + " paragraphs, " + lines(s.text()).size() + " in the text");
            check(s.title() + " has no blank paragraph and no \\r in one", !blankParagraph, null);
        }
        List<String> fixture = Credits.paragraphs("a\nb\n\n\nc\r\n");
        check("\"a\\nb\\n\\n\\nc\\r\\n\" is [\"a\\nb\", \"c\"]", fixture.equals(List.of("a\nb", "c")), fixture.toString());
        check("a null text has no paragraphs", Credits.paragraphs(null).isEmpty(), null);

        System.out.println("--- each shipped file names the page ---");
        if (inRepository) {
            for (String file : new String[] {"tools/earth/ATTRIBUTION-2.txt", "tools/world/ATTRIBUTION.txt"}) {
                check(file + " says \"Credits page\"", Files.exists(Path.of(file)) && read(Path.of(file)).contains("Credits page"), null);
            }
        } else {
            System.out.println("  (skipped)");
        }

        System.out.println(fails == 0 ? "CreditsCheck: all ok" : "CreditsCheck: " + fails + " FAILED");
        System.exit(fails);
    }
}
