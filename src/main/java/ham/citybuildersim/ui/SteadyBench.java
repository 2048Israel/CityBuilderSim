package ham.citybuildersim.ui;

import ham.citybuildersim.Game;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The real window on a city, page by page, measured for UiSteadyCheck (UX1): every node on the scene, where it
 * sits, and where every scroller stands, before and after a redraw.
 *
 * <p>WHY. Jerus, 2026-11-27, on the interface rework: "the ui work watches out for scrolling issues, and issues
 * were the screen flickers or expands without stopping". KeptCheck holds each kept page against a page built new,
 * on a scene of its own; nothing held the window itself - the chrome, the page and the panels together, as the
 * player has them - to the plain law that a redraw with nothing changed changes nothing on screen. This bench
 * walks the picture tool's pages ({@link PagePictures#pages}), so the harness and the pictures see the same window.
 *
 * <p>The window is the game's own (UserInterface.start), at 1920x1080, on a stage drawn transparent and off the
 * screen as FollowBench's is, so the suite shows nothing on the player's desktop.
 */
public final class SteadyBench {

    /** A node that moves less than this between two measures has not moved (px): a layout's rounding. */
    public static final double STILL = 0.5;

    /** How long a page is left after it opens before it is measured: its arrival's animations (a dial's arc, a waterfall's bars; the longest, 1.1 s) run their course (ms). */
    public static final int ARRIVE_MS = 1500;

    /** A page scroller holds more than its window when its content is this much taller (px), as the pictures take it. */
    public static final double SCROLLS = 40;

    private final Game g;
    private UserInterface ui;
    private Stage stage;
    private List<PagePictures.Page> walk;

    /** Why JavaFX cannot start here, or null when it is up (FollowBench's one start, GB5). */
    public static String startJavaFx() {
        return FollowBench.startJavaFx();
    }

    /** The game's window on this city, as it lands after a load. */
    public SteadyBench(Game g) throws Exception {
        this.g = g;
        FollowBench.fx(() -> {
            ui = new UserInterface(g);
            stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setOpacity(0);
            ui.start(stage);
            stage.setFullScreen(false);
            stage.setMaximized(false);
            stage.setX(-5000);
            stage.setY(0);
            stage.setWidth(PagePictures.WIDTH);
            stage.setHeight(PagePictures.HEIGHT);
            PagePictures.call(ui, "anotherCity");
            PagePictures.call(ui, "openCity");
            walk = PagePictures.pages(ui, g);
            return null;
        });
        settle();
    }

    /** The window's size as the stage has it (a headless screen may give less than asked). */
    public String size() throws Exception {
        return FollowBench.fx(() -> (int) stage.getWidth() + "x" + (int) stage.getHeight());
    }

    /** The pages of the walk, by name ("012-build-industry"). */
    public List<String> pages() {
        List<String> names = new ArrayList<>();
        for (PagePictures.Page p : walk) names.add(p.name());
        return names;
    }

    /** A page opened as its rail button and chip open it, and given its layout. */
    public void open(String page) throws Exception {
        for (PagePictures.Page p : walk) {
            if (p.name().equals(page)) {
                FollowBench.fx(() -> { p.open().run(); return null; });
                Thread.sleep(ARRIVE_MS);
                settle();
                return;
            }
        }
        throw new IllegalArgumentException("no page " + page);
    }

    /** The open page drawn again, in place, with nothing changed: the call the clock makes after a month. */
    public void redraw() throws Exception {
        FollowBench.fx(() -> { ui.redraw(); return null; });
        settle();
    }

    /** A month played and the page redrawn, as the clock does it (UserInterface's clock: toggleNextMonth, then the redraw). */
    public void month() throws Exception {
        FollowBench.fx(() -> { g.toggleNextMonth(); ui.redraw(); return null; });
        settle();
    }

    /** Two pulses and their layout: what a redraw, or a scroll set, has asked of the next frames, done. */
    private void settle() throws Exception {
        for (int i = 0; i < 3; i++) {
            Thread.sleep(40);
            FollowBench.fx(() -> {
                Scene sc = stage.getScene();
                if (sc != null) {
                    sc.getRoot().applyCss();
                    sc.getRoot().layout();
                }
                return null;
            });
        }
    }

    /** The page's own scroller set to a fraction of its run; its place then, in pixels from its top, or -1 when it does not scroll. */
    public double placePage(double fraction) throws Exception {
        double at = FollowBench.fx(() -> {
            ScrollPane sp = PagePictures.pageScroller(stage.getScene().getRoot());
            if (sp == null || run(sp) < SCROLLS) return -1.0;
            sp.setVvalue(sp.getVmin() + fraction * (sp.getVmax() - sp.getVmin()));
            return 0.0;
        });
        if (at < 0) return -1;
        settle();
        return pageHold()[0];
    }

    /** The page scroller's place and how far it can move, in pixels from its top; {-1, 0} when there is no page scroller. */
    public double[] pageHold() throws Exception {
        return FollowBench.fx(() -> {
            ScrollPane sp = PagePictures.pageScroller(stage.getScene().getRoot());
            return sp == null ? new double[] {-1, 0} : new double[] {place(sp), run(sp)};
        });
    }

    /** How far a scroller can move: its content's height over its window's (px). */
    static double run(ScrollPane sp) {
        double content = sp.getContent() == null ? 0 : sp.getContent().getLayoutBounds().getHeight();
        return Math.max(0, content - sp.getViewportBounds().getHeight());
    }

    /** A scroller's place, in pixels from its top. */
    static double place(ScrollPane sp) {
        double span = sp.getVmax() - sp.getVmin();
        return span <= 0 ? 0 : (sp.getVvalue() - sp.getVmin()) / span * run(sp);
    }

    /* ----- the measure ----- */

    /** The window at one moment: every node on the scene with its bounds there, every scroller's place, the page's size. */
    public static final class Shape {
        final Map<Node, double[]> nodes = new IdentityHashMap<>();
        final Map<ScrollPane, Double> places = new IdentityHashMap<>();
        /** The page scroller's content (width, height), or NaN with no page scroller. */
        public double pageWidth = Double.NaN, pageHeight = Double.NaN;
        /** The window's root, and the window (width, height). */
        public double rootWidth, rootHeight, windowWidth, windowHeight;

        /** How many nodes are on the scene. */
        public int count() {
            return nodes.size();
        }
    }

    /** The window as it is now. */
    public Shape shape() throws Exception {
        return FollowBench.fx(() -> measure(stage.getScene()));
    }

    /** Every node under a scene's root, and its scrollers (package-level, for the bench's own fixtures). */
    static Shape measure(Scene sc) {
        Shape s = new Shape();
        walk(sc.getRoot(), s);
        ScrollPane page = PagePictures.pageScroller(sc.getRoot());
        if (page != null && page.getContent() != null) {
            Bounds b = page.getContent().getLayoutBounds();
            s.pageWidth = b.getWidth();
            s.pageHeight = b.getHeight();
        }
        s.rootWidth = sc.getRoot().getLayoutBounds().getWidth();
        s.rootHeight = sc.getRoot().getLayoutBounds().getHeight();
        s.windowWidth = sc.getWidth();
        s.windowHeight = sc.getHeight();
        return s;
    }

    private static void walk(Node n, Shape s) {
        if (!n.isVisible()) return;
        Bounds b = n.localToScene(n.getLayoutBounds());
        s.nodes.put(n, new double[] {b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight()});
        if (n instanceof ScrollPane sp) s.places.put(sp, place(sp));
        if (n instanceof Parent p) for (Node c : p.getChildrenUnmodifiable()) walk(c, s);
    }

    /** What changed from one measure to the next: nodes moved or resized, made, let go, scrollers moved. */
    public static final class Diff {
        public final List<String> moved = new ArrayList<>(), made = new ArrayList<>(), lost = new ArrayList<>(), scrolled = new ArrayList<>();

        /** The first few of a list, for a FAIL line: "Label "1,888" +12.0,+0.0; VBox ..." */
        public static String few(List<String> l) {
            return String.join("; ", l.subList(0, Math.min(3, l.size()))) + (l.size() > 3 ? "; and " + (l.size() - 3) + " more" : "");
        }
    }

    /** From a to b: every node on both that moved or changed size, every node on one only, every scroller on both whose place moved. */
    public static Diff compare(Shape a, Shape b) {
        Diff d = new Diff();
        for (Map.Entry<Node, double[]> e : a.nodes.entrySet()) {
            double[] after = b.nodes.get(e.getKey());
            if (after == null) {
                d.lost.add(name(e.getKey()));
                continue;
            }
            double[] before = e.getValue();
            for (int i = 0; i < 4; i++) {
                if (Math.abs(after[i] - before[i]) > STILL) {
                    d.moved.add(name(e.getKey()) + String.format(" at %.1f,%.1f %.1fx%.1f now %.1f,%.1f %.1fx%.1f",
                            before[0], before[1], before[2], before[3], after[0], after[1], after[2], after[3]));
                    break;
                }
            }
        }
        for (Node n : b.nodes.keySet()) if (!a.nodes.containsKey(n)) d.made.add(name(n));
        for (Map.Entry<ScrollPane, Double> e : a.places.entrySet()) {
            Double after = b.places.get(e.getKey());
            if (after != null && Math.abs(after - e.getValue()) > STILL) {
                d.scrolled.add(name(e.getKey()) + String.format(" from %.1f to %.1f px", e.getValue(), after));
            }
        }
        return d;
    }

    /** A node as a FAIL line names it: its class, its text if it has one, its style class. */
    static String name(Node n) {
        String text = n instanceof Labeled l && l.getText() != null && !l.getText().isBlank()
                ? " \"" + (l.getText().length() > 24 ? l.getText().substring(0, 24) + "..." : l.getText()) + "\"" : "";
        String parent = n.getParent() == null ? "" : " in " + n.getParent().getClass().getSimpleName();
        return n.getClass().getSimpleName() + text + parent;
    }

    /* ----- the fixtures: each fault caused, and seen ----- */

    /**
     * The measure's own proof, on a scene of its own: a page of labels in a scroller, measured, then each fault
     * the harness looks for caused once - a label put in another's place (made and lost), a box made taller
     * (moved: everything under it), the scroller moved - and measured again. Returns {made, lost, moved, scrolled,
     * grew}, each the count of what the diff saw; a still redraw of the same page is {0, 0, 0, 0, 0}.
     */
    public static int[][] fixtures() throws Exception {
        return FollowBench.fx(() -> {
            VBox page = new VBox(4);
            for (int i = 0; i < 60; i++) page.getChildren().add(new Label("line " + i));
            ScrollPane sp = new ScrollPane(page);
            Scene sc = new Scene(sp, 400, 300);
            sc.getRoot().applyCss();
            sc.getRoot().layout();
            Shape before = measure(sc);
            Shape still = measure(sc);
            page.getChildren().set(10, new Label("line 10"));
            Region spacer = (Region) page.getChildren().get(20);
            spacer.setMinHeight(80);
            sp.setVvalue(0.5);
            sc.getRoot().applyCss();
            sc.getRoot().layout();
            Shape after = measure(sc);
            Diff none = compare(before, still), seen = compare(before, after);
            return new int[][] {
                    {none.made.size(), none.lost.size(), none.moved.size(), none.scrolled.size(), still.pageHeight > before.pageHeight ? 1 : 0},
                    {seen.made.size(), seen.lost.size(), seen.moved.size(), seen.scrolled.size(), after.pageHeight > before.pageHeight ? 1 : 0}};
        });
    }

    /** The stage put away. */
    public void close() throws Exception {
        FollowBench.fx(() -> { stage.close(); return null; });
    }
}
