package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tooltip;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The People page on a scene of its own - no stage, no window - drawn kept and built new, for
 * KeptCheck (study UP4): the harness's hands on the page, since what it reaches is the ui
 * package's own.
 *
 * <p>WHY. A kept page is right only if every month's redraw over last month's nodes ends where
 * a page built new from the same city would; the one way to know is to build both and compare
 * them, node by node and pixel by pixel. The window cannot be used for it: it opens maximised,
 * and the suite runs on the PC beside Jerus's game. A Scene with no stage takes CSS, layout and
 * a snapshot all the same, so the bench lays the page out there, at the menu column's width.
 * The FX toolkit must be up and every call made on its thread; KeptCheck sees to both.
 */
public final class KeptBench {

    /** The width the page is laid out at: the menu column's in a 1600-pixel window (the study's renders). */
    public static final double WIDTH = 1456;

    private final UserInterface ui;
    private final VBox column;
    private final Scene scene;
    private List<Node> kept = List.of();

    /** The bench on a city: the window's object with no stage, its menu column in a scene of its own. */
    public KeptBench(Game game) {
        Palette.Fonts.load();
        ui = new UserInterface(game);
        column = new VBox(10);
        column.setAlignment(Pos.TOP_CENTER);
        column.setPadding(new Insets(8, 0, 14, 0));
        column.setStyle("-fx-background-color: " + Palette.STAGE + ";");
        ui.rootMenu = column;
        ui.currentScreen = "showPopulationInfoMenu";
        scene = new Scene(column, WIDTH, 900);
        ui.applyTheme(scene);
    }

    /** People's page drawn kept from its pool (the first draw makes every node): the draw's own milliseconds. */
    public double drawKept() {
        long t = System.nanoTime();
        Kept.draw(ui.peopleScreen.pool, () -> ui.peopleScreen.drawPage(false));
        return (System.nanoTime() - t) / 1e6;
    }

    /** The nodes the last kept draw made new. */
    public int lastMade() {
        return ui.peopleScreen.pool.lastMade();
    }

    /** The nodes the last kept draw handed back. */
    public int lastKept() {
        return ui.peopleScreen.pool.lastKept();
    }

    /** The page's top node, kept or not: the same object from one kept draw to the next. */
    public Node top() {
        return column.getChildren().isEmpty() ? null : column.getChildren().get(0);
    }

    /**
     * The page built new in place of the kept one (no pool, every node new) and laid out, as every
     * redraw was before UP4: {its draw ms, CSS ms, layout ms}. restoreKept() puts the kept one back.
     */
    public double[] showFresh() throws Exception {
        kept = new ArrayList<>(column.getChildren());
        long t = System.nanoTime();
        ui.peopleScreen.drawPage(false);
        double ms = (System.nanoTime() - t) / 1e6;
        double[] p = passes();
        return new double[]{ms, p[0], p[1]};
    }

    /** The kept page back in the column, as it was, and laid out. */
    public void restoreKept() throws Exception {
        ui.setPage(kept.toArray(new Node[0]));
        passes();
    }

    /**
     * One figure on the page made wrong: the first label whose words hold a digit gets a "9" in
     * front (KeptCheck's proof that its comparison sees one wrong figure, and that the next kept
     * draw sets it right). Returns what it said, or null when the page has no figure.
     */
    public String spoil() {
        return spoil(new ArrayList<>(column.getChildren()));
    }

    private static String spoil(List<Node> nodes) {
        for (Node n : nodes) {
            if (n instanceof Labeled l && l.getText() != null && l.getText().matches(".*\\d.*")) {
                String was = l.getText();
                l.setText("9" + was);
                return was;
            }
            List<Node> inner = new ArrayList<>();
            if (n instanceof Labeled l && l.getGraphic() != null) inner.add(l.getGraphic());
            if (n instanceof Pane p) inner.addAll(p.getChildren());
            if (n instanceof Group g) inner.addAll(g.getChildren());
            String s = spoil(inner);
            if (s != null) return s;
        }
        return null;
    }

    /** A "details" fold opened or closed, as its toggle would (the next draw shows it). */
    public void details(String name, boolean open) {
        if (open) ui.peopleScreen.detailsOpen.add(name);
        else ui.peopleScreen.detailsOpen.remove(name);
    }

    /** The month card's year view on or off. */
    public void yearView(boolean on) {
        ui.peopleScreen.yearView = on;
    }

    private static Method doCSSPass, doLayoutPass;

    /**
     * The scene's CSS and layout, as a frame's pulse runs them (Scene.doCSSPass and doLayoutPass,
     * which touch only what is dirty): {CSS ms, layout ms}. Where the toolkit will not open them,
     * the public applyCss() and layout() instead, which restyle every node and so cost more.
     */
    public double[] passes() throws Exception {
        long a = System.nanoTime();
        boolean pulse = false;
        try {
            if (doCSSPass == null) {
                doCSSPass = Scene.class.getDeclaredMethod("doCSSPass");
                doLayoutPass = Scene.class.getDeclaredMethod("doLayoutPass");
                doCSSPass.setAccessible(true);
                doLayoutPass.setAccessible(true);
            }
            pulse = true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            pulse = false;
        }
        if (pulse) doCSSPass.invoke(scene);
        else column.applyCss();
        long b = System.nanoTime();
        if (pulse) doLayoutPass.invoke(scene);
        else column.layout();
        long c = System.nanoTime();
        return new double[]{(b - a) / 1e6, (c - b) / 1e6};
    }

    /** The page as pixels, ARGB, row by row: the menu column's snapshot, with its width and height first. */
    public int[] pixels() {
        WritableImage img = column.snapshot(null, null);
        int w = (int) img.getWidth(), h = (int) img.getHeight();
        int[] out = new int[2 + w * h];
        out[0] = w;
        out[1] = h;
        img.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), out, 2, w);
        return out;
    }

    /**
     * The page as it is now, a line a node in the order its code made them: the node's class; its
     * words; its inline style; the values code set on it (the styleable ones whose origin is code);
     * whether it has a click, a hover and an action; its tooltip's words; and where layout put it.
     * Two pages that print the same lines show the same thing.
     */
    public List<String> signature() {
        List<String> out = new ArrayList<>();
        for (Node n : column.getChildren()) sign(n, "", out);
        return out;
    }

    static final boolean PRECISE = Boolean.getBoolean("keptbench.precise");

    private static void sign(Node n, String path, List<String> out) {
        StringBuilder b = new StringBuilder(path).append(n.getClass().getSimpleName());
        if (n instanceof Labeled l) b.append(" \"").append(l.getText()).append('"');
        if (n instanceof Text t) b.append(" \"").append(t.getText()).append('"');
        if (n.getStyle() != null && !n.getStyle().isEmpty()) b.append(" style{").append(n.getStyle()).append('}');
        if (!n.isVisible()) b.append(" hidden");
        if (!n.isManaged()) b.append(" unmanaged");
        b.append(codeSet(n));
        b.append(shape(n));
        if (n.getOnMouseClicked() != null) b.append(" click");
        if (n.getOnMouseEntered() != null || n.getOnMouseExited() != null) b.append(" hover");
        if (n instanceof ButtonBase bb && bb.getOnAction() != null) b.append(" action");
        Tooltip tip = Kept.tipOf(n);
        if (tip != null) b.append(" tip{").append(tip.getText()).append('}');
        Bounds at = n.getBoundsInParent();
        b.append(String.format(Locale.ROOT, PRECISE ? " at %.4f,%.4f %.4fx%.4f" : " at %.1f,%.1f %.1fx%.1f", at.getMinX(), at.getMinY(), at.getWidth(), at.getHeight()));
        out.add(b.toString());
        String inner = path + "  ";
        if (n instanceof Labeled l && l.getGraphic() != null) sign(l.getGraphic(), inner + "graphic: ", out);
        if (n instanceof Pane p) for (Node k : p.getChildren()) sign(k, inner, out);
        if (n instanceof Group g) for (Node k : g.getChildren()) sign(k, inner, out);
    }

    /**
     * Every styleable value a node has now, by property, where it is not the property's initial
     * value: what code set and what its style made of it, both pages styled and laid out alike. A
     * value printed with an identity (an effect's object) is compared by its class alone.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String codeSet(Styleable n) {
        StringBuilder b = new StringBuilder();
        for (CssMetaData m : n.getCssMetaData()) {
            if ("-fx-skin".equals(m.getProperty())) continue;
            StyleableProperty p = m.getStyleableProperty(n);
            if (p == null) continue;
            Object v = p.getValue();
            if (java.util.Objects.equals(v, m.getInitialValue(n))) continue;
            b.append(' ').append(m.getProperty()).append('=').append(String.valueOf(v).replaceAll("@[0-9a-f]+", ""));
        }
        return b.toString();
    }

    /** A shape's own geometry, which no style sets. */
    private static String shape(Node n) {
        if (n instanceof Line l) return String.format(Locale.ROOT, " line %.2f,%.2f-%.2f,%.2f", l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
        if (n instanceof Circle c) return String.format(Locale.ROOT, " circle %.2f,%.2f r%.2f", c.getCenterX(), c.getCenterY(), c.getRadius());
        if (n instanceof Arc a) return String.format(Locale.ROOT, " arc %.2f+%.2f", a.getStartAngle(), a.getLength());
        if (n instanceof Rectangle r) return String.format(Locale.ROOT, " rect %.2fx%.2f", r.getWidth(), r.getHeight());
        if (n instanceof Polyline p) return " points" + p.getPoints();
        if (n instanceof SVGPath s) return " svg " + s.getContent();
        return "";
    }
}
