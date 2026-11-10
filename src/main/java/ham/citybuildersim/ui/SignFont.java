package ham.citybuildersim.ui;

import java.awt.Font;
import java.io.InputStream;
import java.util.List;

/**
 * Which signs for the city's money the interface's own faces can draw.
 *
 * WHY THIS EXISTS (FM1). The sign a player types for their money ("kr", "€",
 * "₺") is written on every figure of the game, in the words' face (IBM Plex
 * Sans) and in the figures' (IBM Plex Mono). A glyph neither face has is drawn
 * by JavaFX as an empty box, or in whatever face the platform falls back to,
 * and a money that reads "▯2.6M" on every screen is a worse mistake than a
 * founding screen that says no. So the founding row offers only the signs both
 * faces draw (offered()), and Found refuses a typed sign they do not (draws()).
 *
 * THE TEST IS THE FONT FILE'S, NOT THE PLATFORM'S. java.awt.Font reads the same
 * two regular files the interface loads (Palette.Fonts.FOLDER), so the answer
 * is the same on every machine the game is shipped to, and a harness can ask it
 * with no window and no toolkit. java.desktop is in the shipped runtime
 * (Build EXE.bat's MODULES). The bold and medium cuts carry the same glyphs as
 * their regular cut; they are not asked.
 *
 * A face that cannot be read is skipped, not failed: draws() is true when
 * neither can be, because a game that cannot read its own font files is not
 * going to draw anything better with a sign refused.
 */
public final class SignFont {

    private SignFont() { }

    /** The signs the founding row offers before the font is asked, in the order they are offered: the world's common ones, then the Nordic, Polish, Czech and Swiss, then Brazil's. */
    public static final List<String> CANDIDATES = List.of("$", "€", "£", "¥", "₹", "₩", "₽", "₺",
            "₪", "ƒ", "kr", "zł", "Kč", "Fr", "R$");

    /** The two regular faces' files, in the folder Palette.Fonts loads them from: the words' and the figures'. */
    private static final String[] FILES = { "IBMPlexSans-Regular.ttf", "IBMPlexMono-Regular.ttf" };

    private static List<Font> faces;

    /** The faces that could be read, loaded on the first question and kept. */
    private static synchronized List<Font> faces() {
        if (faces == null) {
            List<Font> read = new java.util.ArrayList<>();
            for (String file : FILES) {
                try (InputStream in = SignFont.class.getResourceAsStream(Palette.Fonts.FOLDER + file)) {
                    if (in != null) read.add(Font.createFont(Font.TRUETYPE_FONT, in));
                } catch (Exception | LinkageError e) {
                    System.out.println("SignFont: " + file + " could not be read (" + e + "); it is skipped.");
                }
            }
            faces = List.copyOf(read);
        }
        return faces;
    }

    /** Whether every face that could be read draws every character of the sign (true when none could be read). */
    public static boolean draws(String sign) {
        if (sign == null) return true;
        for (Font face : faces()) {
            if (face.canDisplayUpTo(sign) != -1) return false;
        }
        return true;
    }

    /** CANDIDATES the faces draw, in CANDIDATES' order: the signs the founding row offers. */
    public static List<String> offered() {
        return CANDIDATES.stream().filter(SignFont::draws).toList();
    }
}
