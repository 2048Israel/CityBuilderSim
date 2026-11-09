package ham.citybuildersim;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The Credits page's sections: the Earth data's attribution, the nations' attribution and the
 * fonts' notice with its licence, each read from the jar as the exe ships it, and the text of each
 * cut into paragraphs for the screen to draw (CR1, 2026-11-09). No JavaFX; the page is
 * UserInterface.showCreditsMenu(), which computes nothing of its own.
 *
 * WHY THIS EXISTS. The two attribution files (tools/earth/ATTRIBUTION-2.txt, tools/world/ATTRIBUTION.txt)
 * said "the Credits page", and the game had none. It is sold, and the licences of what it is made
 * from, CC BY and the SIL Open Font License among them, ask that the credit reach the player, not
 * only a file beside the exe. The page reads the same files the exe ships (pom.xml puts them in the
 * jar as credits/, the fonts' licence is already in fonts/), so there is one source of each text
 * and a page that drifts from the file is not possible. CreditsCheck holds that.
 */
public final class Credits {

    private Credits() { }

    /** One block of the page: its heading, where its text is, the file that ships beside the exe, the line above it, and the text itself. */
    public record Section(String title, String resource, String besideExe, String lead, String text) {
        /** True when this build carries no copy of the text; the page then says where the file is. */
        public boolean missing() { return text == null; }
    }

    /** The Earth data's attribution, in the jar (pom.xml copies tools/earth/ATTRIBUTION-2.txt there). */
    public static final String EARTH = "/credits/earth/ATTRIBUTION-2.txt";

    /** The nations' attribution, in the jar (pom.xml copies tools/world/ATTRIBUTION.txt there). */
    public static final String NATIONS = "/credits/world/ATTRIBUTION.txt";

    /** The fonts' licence in full, the SIL Open Font License 1.1 with IBM's copyright line, as it sits beside the fonts. */
    public static final String FONTS = "/fonts/OFL.txt";

    /** The line above the fonts' licence: what the type is, whose, and the licence it is used under. */
    public static final String FONT_NOTICE = "The type is IBM Plex Sans and IBM Plex Mono, Copyright © 2017 IBM Corp., "
            + "with Reserved Font Name \"Plex\", used under the SIL Open Font License, Version 1.1, which follows in full.";

    /** The page's sections, in order, read from the jar. */
    public static List<Section> sections() {
        return sections(Credits::readResource);
    }

    /** The same through any reader of a classpath path to its text (null for a file this build lacks); a harness's fixture. */
    static List<Section> sections(Function<String, String> reader) {
        List<Section> out = new ArrayList<>();
        out.add(new Section("The Earth", EARTH, "data/earth-2-ATTRIBUTION.txt", null, reader.apply(EARTH)));
        out.add(new Section("The nations", NATIONS, "data/world-ATTRIBUTION.txt", null, reader.apply(NATIONS)));
        out.add(new Section("The fonts", FONTS, null, FONT_NOTICE, reader.apply(FONTS)));
        return out;
    }

    /** A classpath file as UTF-8 text, unchanged but for a leading byte-order mark; null when it is not there or cannot be read. */
    static String readResource(String path) {
        try (InputStream in = Credits.class.getResourceAsStream(path)) {
            if (in == null) return null;
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return text.startsWith("\uFEFF") ? text.substring(1) : text;
        } catch (IOException e) {
            System.out.println("Credits: " + path + " could not be read: " + e.getMessage());
            return null;
        }
    }

    /**
     * The text cut on its blank lines (empty or whitespace), each paragraph keeping its own line breaks,
     * with \r removed and no blank paragraph at either end; empty for null. The screen draws one label
     * for each, since one label holding twenty kilobytes is slow to lay out.
     */
    public static List<String> paragraphs(String text) {
        List<String> out = new ArrayList<>();
        if (text == null) return out;
        StringBuilder paragraph = new StringBuilder();
        for (String line : text.replace("\r", "").split("\n", -1)) {
            if (line.isBlank()) {
                if (paragraph.length() > 0) out.add(paragraph.toString());
                paragraph.setLength(0);
            } else {
                if (paragraph.length() > 0) paragraph.append('\n');
                paragraph.append(line);
            }
        }
        if (paragraph.length() > 0) out.add(paragraph.toString());
        return out;
    }

    /** What the page says for a section this build lacks: where the file is. */
    public static String missingNote(Section s) {
        return s.besideExe() == null
                ? "This build carries no copy of the fonts' licence."
                : "This build carries no copy of it; it ships beside the exe as " + s.besideExe() + ".";
    }
}
