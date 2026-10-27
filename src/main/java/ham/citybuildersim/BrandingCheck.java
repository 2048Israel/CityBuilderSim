package ham.citybuildersim;

import ham.citybuildersim.ui.Branding;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The game's icon is in the jar at every size it is given, the exe's .ico carries the sizes it
 * should, and the store's title art is drawn to the start screen's proportions (BR1).
 *
 * WHY THIS EXISTS. The window's icon (ui/Branding.dress) is loaded from the classpath and a size
 * that is missing is only a log line, never an error, so a regeneration by
 * tools/branding/make_icons.py that dropped a size, or a file left out of the jar, would go unseen
 * until someone looked at a taskbar. The .ico is read by jpackage alone (Build EXE.bat), which no
 * harness runs. And the title art is drawn twice, by the start screen in JavaFX (Branding.title)
 * and for the store pages by tools/branding/make_title.py, from proportions each keeps: a change
 * to one would leave the store's logo unlike the game's. No JavaFX is needed: files are read as
 * bytes and text.
 *
 * What it has to prove:
 *
 *   1. EVERY PNG IS THERE, AT ITS SIZE. Each of Branding.SIZES is on the classpath as a PNG whose
 *      header gives that width and height, with an alpha channel (the tile's corners are round).
 *   2. THE .ICO CARRIES ITS SIZES. icon.ico is an icon file listing exactly Branding.ICO_SIZES,
 *      each entry a PNG whose own header agrees with the directory, inside the file.
 *   3. THE EXE IS BUILT WITH IT, AND THE DRAWING IS KEPT. Build EXE.bat passes --icon with a file
 *      that exists, and the two SVG masters are beside the PNGs.
 *   4. THE TITLE ART IS THE START SCREEN'S. make_title.py's proportions are Branding's, number for
 *      number, and branding/ holds the title SVG and its PNGs. Run from the repository only (it
 *      reads files there); elsewhere the section says so and is skipped.
 */
public class BrandingCheck {

    private static int fails;

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    static byte[] resource(String name) {
        try (InputStream in = BrandingCheck.class.getResourceAsStream(name)) {
            return in == null ? null : in.readAllBytes();
        } catch (Exception e) {
            return null;
        }
    }

    /** {width, height, colour type} from a PNG's header, or null if the bytes are not a PNG. */
    static int[] pngHeader(byte[] b, int at) {
        byte[] sig = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1a, '\n'};
        if (b == null || b.length < at + 26 || !Arrays.equals(Arrays.copyOfRange(b, at, at + 8), sig)) return null;
        ByteBuffer h = ByteBuffer.wrap(b, at + 16, 10).order(ByteOrder.BIG_ENDIAN);
        int w = h.getInt(), ht = h.getInt();
        h.get();   // bit depth
        return new int[] {w, ht, h.get() & 0xff};
    }

    /** The numbers a Python line "A, B = 1, 2" or "C = [1, 2]" assigns, in order, or null if the line is not there. */
    static double[] pythonNumbers(String source, String names) {
        Matcher m = Pattern.compile("(?m)^" + Pattern.quote(names) + "\\s*=\\s*\\[?([-0-9., ]+)\\]?\\s*$").matcher(source);
        if (!m.find()) return null;
        return Arrays.stream(m.group(1).split(",")).map(String::trim).filter(s -> !s.isEmpty()).mapToDouble(Double::parseDouble).toArray();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== BrandingCheck: the icon at every size, the exe's .ico, and the store's title art drawn as the start screen's (BR1) ===");

        System.out.println("--- every PNG is there, at its size ---");
        for (int n : Branding.SIZES) {
            int[] head = pngHeader(resource(Branding.png(n)), 0);
            check(Branding.png(n) + " is a PNG on the classpath", head != null, null);
            if (head == null) continue;
            check(Branding.png(n) + " is " + n + " by " + n, head[0] == n && head[1] == n, head[0] + "x" + head[1]);
            check(Branding.png(n) + " has an alpha channel", head[2] == 6 || head[2] == 4, "colour type " + head[2]);
        }

        System.out.println("--- the .ico carries its sizes ---");
        byte[] ico = resource(Branding.ICO);
        check(Branding.ICO + " is on the classpath", ico != null && ico.length > 6, null);
        if (ico != null && ico.length > 6) {
            ByteBuffer d = ByteBuffer.wrap(ico).order(ByteOrder.LITTLE_ENDIAN);
            int reserved = d.getShort() & 0xffff, type = d.getShort() & 0xffff, count = d.getShort() & 0xffff;
            check("it is an icon file (reserved 0, type 1)", reserved == 0 && type == 1, reserved + ", " + type);
            List<Integer> listed = new ArrayList<>();
            for (int i = 0; i < count && 6 + 16 * (i + 1) <= ico.length; i++) {
                int w = ico[6 + 16 * i] & 0xff, h = ico[7 + 16 * i] & 0xff;
                ByteBuffer e = ByteBuffer.wrap(ico, 6 + 16 * i + 8, 8).order(ByteOrder.LITTLE_ENDIAN);
                long size = e.getInt() & 0xffffffffL, offset = e.getInt() & 0xffffffffL;
                int side = w == 0 ? 256 : w;
                listed.add(side);
                boolean inside = offset + size <= ico.length;
                check("entry " + side + " lies inside the file", inside, offset + " + " + size + " of " + ico.length);
                int[] head = inside ? pngHeader(ico, (int) offset) : null;
                check("entry " + side + " is a PNG of " + side + " by " + side, head != null && head[0] == side && head[1] == side
                        && (h == 0 ? 256 : h) == side, head == null ? "not a PNG" : head[0] + "x" + head[1]);
            }
            List<Integer> want = new ArrayList<>();
            for (int n : Branding.ICO_SIZES) want.add(n);
            check("it lists exactly Branding.ICO_SIZES", listed.equals(want), "lists " + listed + ", want " + want);
        }

        System.out.println("--- the exe is built with it, and the drawing is kept ---");
        Path bat = Path.of("Build EXE.bat");
        boolean inRepository = Files.exists(bat);
        if (!inRepository) {
            System.out.println("  (no Build EXE.bat in " + Path.of("").toAbsolutePath() + ": not run from the repository, skipped)");
        } else {
            String text = new String(Files.readAllBytes(bat), StandardCharsets.UTF_8);
            Matcher m = Pattern.compile("--icon \"([^\"]+)\"").matcher(text);
            boolean found = m.find();
            check("Build EXE.bat passes --icon", found, null);
            if (found) {
                Path icon = Path.of(m.group(1).replace('\\', '/'));
                check("the file it passes exists", Files.exists(icon), icon.toString());
                check("and is the classpath's icon.ico", icon.endsWith(Path.of("src/main/resources" + Branding.ICO)), icon.toString());
            }
        }
        for (String svg : new String[] {"icon.svg", "icon-small.svg"}) {
            byte[] b = resource(Branding.FOLDER + svg);
            check(svg + " is beside the PNGs", b != null && new String(b, StandardCharsets.UTF_8).contains("<svg"), null);
        }

        System.out.println("--- the title art is the start screen's ---");
        Path tool = Path.of("tools", "branding", "make_title.py");
        if (!inRepository || !Files.exists(tool)) {
            System.out.println("  (not run from the repository, skipped)");
        } else {
            String py = new String(Files.readAllBytes(tool), StandardCharsets.UTF_8);
            Object[][] pairs = {
                {"RULE, TOP_DROP, LOW_DROP, BEND_BACK", new double[] {Branding.RULE, Branding.TOP_DROP, Branding.LOW_DROP, Branding.BEND_BACK}},
                {"CURVE", Branding.CURVE},
                {"CLIMB_X, CLIMB_Y, DOT, RING", new double[] {Branding.CLIMB_X, Branding.CLIMB_Y, Branding.DOT, Branding.RING}},
            };
            for (Object[] p : pairs) {
                double[] there = pythonNumbers(py, (String) p[0]), here = (double[]) p[1];
                check("make_title.py's " + p[0] + " are Branding's", there != null && Arrays.equals(there, here),
                        (there == null ? "no such line" : Arrays.toString(there)) + " against " + Arrays.toString(here));
            }
            double[] widths = pythonNumbers(py, "TITLE_WIDTHS");
            check("make_title.py names the widths it writes", widths != null && widths.length > 0, null);
            Path svg = Path.of("branding", "title.svg");
            check("branding/title.svg is there, the font inside it", Files.exists(svg)
                    && new String(Files.readAllBytes(svg), StandardCharsets.UTF_8).contains("data:font/ttf;base64,"), svg.toString());
            if (widths != null) {
                for (double wd : widths) {
                    Path png = Path.of("branding", "title-" + (int) wd + ".png");
                    int[] head = Files.exists(png) ? pngHeader(Files.readAllBytes(png), 0) : null;
                    check(png + " is a PNG " + (int) wd + " wide, with an alpha channel",
                            head != null && head[0] == (int) wd && head[2] == 6, head == null ? "missing" : head[0] + "x" + head[1]);
                }
            }
        }

        System.out.println(fails == 0 ? "BrandingCheck: all ok" : "BrandingCheck: " + fails + " FAILED");
        System.exit(fails);
    }
}
