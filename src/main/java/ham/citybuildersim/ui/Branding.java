package ham.citybuildersim.ui;

/**
 * The game's identity on screen: its icon on the window (every size of resources/icons/ handed
 * to the stage, so Windows takes the one it needs for the taskbar and Alt+Tab) and its title art
 * on the start screen.
 *
 * WHY: until BR1 (2026-10-27) the window wore Java's coffee cup, the exe jpackage's default, and
 * the start screen the name as a plain label. Jerus chose both drawings, each of four concepts:
 * the icon is "towers as a chart" (four towers rising like the bars of a chart, each lit in one
 * of the palette's colours, the money line along their roofs), the title art "the ledger's
 * total" (the name over the double rule an account closes on, its top line climbing at the end
 * like a chart). tools/branding/make_icons.py draws the icon and writes the PNGs and icon.ico for
 * Build EXE.bat; make_title.py draws the title art for the store pages from the same proportions
 * as title() below. BrandingCheck holds the icon files to SIZES and ICO_SIZES, and the store art to these proportions. Nothing here touches
 * JavaFX until dress() or title() is called, so a harness can read the lists without the toolkit.
 */
public final class Branding {

    /** The folder on the classpath the icon files are in. */
    public static final String FOLDER = "/icons/";

    /** Every size the PNGs come in; the stage is given them all and the platform picks. */
    public static final int[] SIZES = {16, 20, 24, 32, 40, 48, 64, 128, 256};

    /** The sizes icon.ico carries for the exe: Explorer's and the taskbar's at each display scale, and 256 for large icons. */
    public static final int[] ICO_SIZES = {16, 20, 24, 32, 40, 48, 64, 256};

    /** The exe's icon on the classpath; Build EXE.bat passes the same file from the source tree. */
    public static final String ICO = FOLDER + "icon.ico";

    /*
     * The title art's proportions, in ems of the name's font size, from the baseline and from the
     * name's two ends (tools/branding/make_title.py draws the store art from the same numbers):
     * the rules' thickness, the top rule's and the bottom rule's drop below the baseline, where the
     * top rule leaves the line to climb, the curve's two control points and its end, the straight
     * climb's end, and the dot's radius and ring.
     */
    public static final double RULE = .0467, TOP_DROP = .413, LOW_DROP = .56, BEND_BACK = .2;
    public static final double[] CURVE = {.0667, 0, .133, -.08, .227, -.24};
    public static final double CLIMB_X = .467, CLIMB_Y = -.747, DOT = .06, RING = .0333;

    private Branding() { }

    /** The classpath name of the PNG at n pixels. */
    public static String png(int n) {
        return FOLDER + "icon-" + n + ".png";
    }

    /** Gives the stage every size of the icon; a size missing from the jar is named in the log line, never thrown. */
    public static void dress(javafx.stage.Stage stage) {
        StringBuilder missing = new StringBuilder();
        int given = 0;
        for (int n : SIZES) {
            try (java.io.InputStream in = Branding.class.getResourceAsStream(png(n))) {
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

    /**
     * The title art: the name in bold at the given size, the double rule under it in the money
     * blue, the top rule climbing past the name's end to a ringed dot. The rules follow the
     * label's own width and baseline, so the font the theme gives it decides where they end.
     */
    public static javafx.scene.layout.Pane title(String text, double size) {
        javafx.scene.control.Label name = new javafx.scene.control.Label(text);
        name.setStyle("-fx-font-size: " + size + "px; -fx-font-weight: bold; -fx-text-fill: " + Palette.TEXT_HEAD + ";");
        javafx.scene.paint.Color blue = javafx.scene.paint.Color.web(Palette.MONEY);

        javafx.scene.shape.Line low = new javafx.scene.shape.Line();
        javafx.scene.shape.Path top = new javafx.scene.shape.Path();
        javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(DOT * size);
        for (javafx.scene.shape.Shape s : new javafx.scene.shape.Shape[] {low, top, dot}) {
            s.setStroke(blue);
            s.setStrokeWidth(RULE * size);
            s.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            s.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
            s.setMouseTransparent(true);
        }
        top.setFill(null);
        dot.setStrokeWidth(RING * size);
        dot.setFill(javafx.scene.paint.Color.web(Palette.STAGE));

        javafx.scene.layout.Pane art = new javafx.scene.layout.Pane(name, low, top, dot);
        Runnable lay = () -> {
            double x0 = .03 * size, x1 = name.getWidth(), base = name.getBaselineOffset();
            double yTop = base + TOP_DROP * size, yLow = base + LOW_DROP * size;
            low.setStartX(x0);
            low.setStartY(yLow);
            low.setEndX(x1);
            low.setEndY(yLow);
            double bx = x1 - BEND_BACK * size;
            top.getElements().setAll(
                    new javafx.scene.shape.MoveTo(x0, yTop),
                    new javafx.scene.shape.LineTo(bx, yTop),
                    new javafx.scene.shape.CubicCurveTo(
                            x1 + CURVE[0] * size, yTop + CURVE[1] * size,
                            x1 + CURVE[2] * size, yTop + CURVE[3] * size,
                            x1 + CURVE[4] * size, yTop + CURVE[5] * size),
                    new javafx.scene.shape.LineTo(x1 + CLIMB_X * size, yTop + CLIMB_Y * size));
            dot.setCenterX(x1 + CLIMB_X * size);
            dot.setCenterY(yTop + CLIMB_Y * size);
        };
        name.widthProperty().addListener((o, a, b) -> lay.run());
        name.heightProperty().addListener((o, a, b) -> lay.run());
        lay.run();
        return art;
    }
}
