package ham.citybuildersim.ui;

/**
 * The game's icon on its window: the PNGs in resources/icons/, every size handed to the stage, so
 * Windows takes the one it needs for the title bar, the taskbar and Alt+Tab.
 *
 * WHY: until BR1 (2026-10-27) the window wore Java's coffee cup and the exe jpackage's default.
 * The drawing is Jerus's pick of four, "towers as a chart": four towers rising like the bars of a
 * chart, each lit in one of the palette's colours, the money line along their roofs.
 * tools/icon/make_icons.py draws it, renders it at SIZES and packs ICO_SIZES of them into icon.ico
 * for Build EXE.bat; IconCheck holds the files to these lists. Nothing here touches JavaFX until
 * dress() is called, so a harness can read the lists without the toolkit.
 */
public final class AppIcon {

    /** The folder on the classpath the icon files are in. */
    public static final String FOLDER = "/icons/";

    /** Every size the PNGs come in; the stage is given them all and the platform picks. */
    public static final int[] SIZES = {16, 20, 24, 32, 40, 48, 64, 128, 256};

    /** The sizes icon.ico carries for the exe: Explorer's and the taskbar's at each display scale, and 256 for large icons. */
    public static final int[] ICO_SIZES = {16, 20, 24, 32, 40, 48, 64, 256};

    /** The exe's icon on the classpath; Build EXE.bat passes the same file from the source tree. */
    public static final String ICO = FOLDER + "icon.ico";

    private AppIcon() { }

    /** The classpath name of the PNG at n pixels. */
    public static String png(int n) {
        return FOLDER + "icon-" + n + ".png";
    }

    /** Gives the stage every size of the icon; a size missing from the jar is named in the log line, never thrown. */
    public static void dress(javafx.stage.Stage stage) {
        StringBuilder missing = new StringBuilder();
        int given = 0;
        for (int n : SIZES) {
            try (java.io.InputStream in = AppIcon.class.getResourceAsStream(png(n))) {
                if (in == null) {
                    missing.append(' ').append(n);
                    continue;
                }
                javafx.scene.image.Image image = new javafx.scene.image.Image(in);
                if (image.isError()) {
                    missing.append(' ').append(n).append(" (did not load)");
                } else {
                    stage.getIcons().add(image);
                    given++;
                }
            } catch (Throwable e) {
                missing.append(' ').append(n).append(" (").append(e).append(')');
            }
        }
        System.out.println("Icon: " + given + " of " + SIZES.length + " sizes"
                + (missing.length() == 0 ? "" : ", missing" + missing));
    }
}
