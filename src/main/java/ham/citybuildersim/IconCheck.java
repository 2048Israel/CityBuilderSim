package ham.citybuildersim;

import ham.citybuildersim.ui.AppIcon;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The game's icon is in the jar at every size it is given, and the exe's .ico carries the sizes it
 * should (BR1).
 *
 * WHY THIS EXISTS. The window's icon (ui/AppIcon) is loaded from the classpath and a size that is
 * missing is only a log line, never an error, so a regeneration by tools/icon/make_icons.py that
 * dropped a size, or a file left out of the jar, would go unseen until someone looked at a taskbar.
 * The .ico is read by jpackage alone (Build EXE.bat), which no harness runs. No JavaFX is needed:
 * the files are read as bytes.
 *
 * What it has to prove:
 *
 *   1. EVERY PNG IS THERE, AT ITS SIZE. Each of AppIcon.SIZES is on the classpath as a PNG whose
 *      header gives that width and height, with an alpha channel (the tile's corners are round).
 *   2. THE .ICO CARRIES ITS SIZES. icon.ico is an icon file listing exactly AppIcon.ICO_SIZES, each
 *      entry a PNG whose own header agrees with the directory, inside the file.
 *   3. THE EXE IS BUILT WITH IT, AND THE DRAWING IS KEPT. Build EXE.bat passes --icon with a file
 *      that exists, and the two SVG masters are beside the PNGs.
 */
public class IconCheck {

    private static int fails;

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    static byte[] resource(String name) {
        try (InputStream in = IconCheck.class.getResourceAsStream(name)) {
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

    public static void main(String[] args) throws Exception {
        System.out.println("=== IconCheck: the game's icon is in the jar at every size, and the exe's .ico carries its sizes (BR1) ===");

        System.out.println("--- every PNG is there, at its size ---");
        for (int n : AppIcon.SIZES) {
            int[] head = pngHeader(resource(AppIcon.png(n)), 0);
            check(AppIcon.png(n) + " is a PNG on the classpath", head != null, null);
            if (head == null) continue;
            check(AppIcon.png(n) + " is " + n + " by " + n, head[0] == n && head[1] == n, head[0] + "x" + head[1]);
            check(AppIcon.png(n) + " has an alpha channel", head[2] == 6 || head[2] == 4, "colour type " + head[2]);
        }

        System.out.println("--- the .ico carries its sizes ---");
        byte[] ico = resource(AppIcon.ICO);
        check(AppIcon.ICO + " is on the classpath", ico != null && ico.length > 6, null);
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
            for (int n : AppIcon.ICO_SIZES) want.add(n);
            check("it lists exactly AppIcon.ICO_SIZES", listed.equals(want), "lists " + listed + ", want " + want);
        }

        System.out.println("--- the exe is built with it, and the drawing is kept ---");
        Path bat = Path.of("Build EXE.bat");
        if (!Files.exists(bat)) {
            System.out.println("  (no Build EXE.bat in " + Path.of("").toAbsolutePath() + ": not run from the repository, skipped)");
        } else {
            String text = new String(Files.readAllBytes(bat), StandardCharsets.UTF_8);
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("--icon \"([^\"]+)\"").matcher(text);
            boolean found = m.find();
            check("Build EXE.bat passes --icon", found, null);
            if (found) {
                Path icon = Path.of(m.group(1).replace('\\', '/'));
                check("the file it passes exists", Files.exists(icon), icon.toString());
                check("and is the classpath's icon.ico", icon.endsWith(Path.of("src/main/resources" + AppIcon.ICO)), icon.toString());
            }
        }
        for (String svg : new String[] {"icon.svg", "icon-small.svg"}) {
            byte[] b = resource(AppIcon.FOLDER + svg);
            check(svg + " is beside the PNGs", b != null && new String(b, StandardCharsets.UTF_8).contains("<svg"), null);
        }

        System.out.println(fails == 0 ? "IconCheck: all ok" : "IconCheck: " + fails + " FAILED");
        System.exit(fails);
    }
}
