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
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
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
 * package's own. Since UP2 the window's chrome too: the header at the window's width and the rail
 * at its height, each on a scene of its own, drawn by the real refreshHeader() and
 * refreshTabRail() - kept, or with every node new (the pools swapped for empty ones, as a rebuild
 * made them).
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

    /** The window's width in KeptCheck's pictures: the header is laid out across it. */
    public static final double HEADER_WIDTH = 1600;

    /** The window's height: the rail is laid out down it. */
    public static final double RAIL_HEIGHT = 900;

    private final Scene headerScene, railScene;
    private List<Node> keptHeader = List.of(), keptRail = List.of();
    private Kept.Pool[] keptPools;

    /** The three panels' scenes (UP2, slice 2b): the city panel's 290 wide, the construction panel's 280, the inbox list's. */
    public static final double PANEL_HEIGHT = 900;
    private final Scene cityScene, sitesScene, listScene;
    private List<Node> keptCity = List.of(), keptSites = List.of(), keptList = List.of();
    private Kept.Pool[] keptPanelPools;

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
        // The chrome's containers, made as start() makes them (UserInterface.headerBox() and railBox()).
        ui.header = UserInterface.headerBox();
        ui.tabRail = UserInterface.railBox();
        headerScene = new Scene(ui.header, HEADER_WIDTH, Palette.HEADER);
        railScene = new Scene(ui.tabRail, Palette.RAIL, RAIL_HEIGHT);
        ui.applyTheme(headerScene);
        ui.applyTheme(railScene);
        // ...and the three panels' (UserInterface.cityPanelBox(), constructionPanelBox() and inboxCornerBox()).
        ui.cityPanel = UserInterface.cityPanelBox();
        ui.constructionPanel = UserInterface.constructionPanelBox();
        ui.inboxCorner = UserInterface.inboxCornerBox();
        cityScene = new Scene(ui.cityPanel, 290, PANEL_HEIGHT);
        sitesScene = new Scene(ui.constructionPanel, 280, PANEL_HEIGHT);
        listScene = new Scene(ui.inboxCorner, UserInterface.INBOX_WIDTH + 30, 520);
        ui.applyTheme(cityScene);
        ui.applyTheme(sitesScene);
        ui.applyTheme(listScene);
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
            if (n instanceof ScrollPane sp && sp.getContent() != null) inner.add(sp.getContent());
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

    /* =====================================================================
       THE CHROME (UP2): the header and the rail, kept and built new
       ===================================================================== */

    /**
     * The header and the rail drawn kept, from their pools (the first draw makes every node): the
     * draw's own milliseconds. The real refreshHeader() and refreshTabRail(), in the order the
     * window draws them.
     */
    public double drawChromeKept() {
        long t = System.nanoTime();
        ui.refreshHeader();
        ui.refreshTabRail();
        double ms = (System.nanoTime() - t) / 1e6;
        keepMiniMapOff();
        return ms;
    }

    /**
     * The minimap's node hidden (never removed: it stays in the rail where the rail puts it). In a
     * scene it starts its own pump, which draws a map of the city; the bench has no use for that,
     * and a node out of view returns from its tick at once.
     */
    private void keepMiniMapOff() {
        Node mini = ui.miniMapNode();
        if (mini != null && mini.isVisible()) mini.setVisible(false);
    }

    /** The envelope drawn on its own, as a notice read elsewhere draws it (refreshInboxButton()). */
    public double drawEnvelope() {
        long t = System.nanoTime();
        ui.refreshInboxButton();
        return (System.nanoTime() - t) / 1e6;
    }

    /** The nodes the last draws of each pool made new and handed back: {header made, kept, envelope made, kept, rail made, kept}. */
    public int[] chromeCounts() {
        return new int[]{ui.headerPool.lastMade(), ui.headerPool.lastKept(), ui.inboxPool.lastMade(), ui.inboxPool.lastKept(),
                ui.railPool.lastMade(), ui.railPool.lastKept()};
    }

    /** The header's top nodes and the rail's, in order: the same objects from one kept draw to the next. */
    public List<Node> chromeTops() {
        List<Node> out = new ArrayList<>(ui.header.getChildren());
        out.addAll(ui.tabRail.getChildren());
        return out;
    }

    /**
     * The chrome built new in place of the kept one - the pools swapped for empty ones, so every node
     * is made again, as every redraw did before UP2 - and laid out: {its draw ms, CSS ms, layout ms}.
     * restoreKeptChrome() puts the kept nodes and pools back.
     */
    public double[] showFreshChrome() throws Exception {
        keptHeader = new ArrayList<>(ui.header.getChildren());
        keptRail = new ArrayList<>(ui.tabRail.getChildren());
        keptPools = new Kept.Pool[]{ui.headerPool, ui.inboxPool, ui.railPool};
        ui.headerPool = new Kept.Pool();
        ui.inboxPool = new Kept.Pool();
        ui.railPool = new Kept.Pool();
        long t = System.nanoTime();
        ui.refreshHeader();
        ui.refreshTabRail();
        double ms = (System.nanoTime() - t) / 1e6;
        double[] p = chromePasses();
        return new double[]{ms, p[0], p[1]};
    }

    /** The kept chrome back where it was, with its pools, and laid out. The rail's minimap is never taken out. */
    public void restoreKeptChrome() throws Exception {
        ui.headerPool = keptPools[0];
        ui.inboxPool = keptPools[1];
        ui.railPool = keptPools[2];
        ui.header.getChildren().setAll(keptHeader);
        Node mini = ui.miniMapNode();
        ui.tabRail.getChildren().retainAll(mini);
        int at = keptRail.indexOf(mini);
        ui.tabRail.getChildren().addAll(0, keptRail.subList(0, at));
        ui.tabRail.getChildren().addAll(keptRail.subList(at + 1, keptRail.size()));
        chromePasses();
    }

    /**
     * Both chrome scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed.
     * The animations a draw started (the month figure's pop) are run to their ends first: a scene with
     * no window has no pulses to run them, and a chrome built new starts none.
     */
    public double[] chromePasses() throws Exception {
        finishAnimations(ui.header);
        finishAnimations(ui.tabRail);
        double[] a = passes(headerScene, ui.header), b = passes(railScene, ui.tabRail);
        return new double[]{a[0] + b[0], a[1] + b[1]};
    }

    private static void finishAnimations(Node n) {
        javafx.animation.Animation a = Kept.animationOf(n);
        if (a != null && a.getStatus() != javafx.animation.Animation.Status.STOPPED) {
            a.jumpTo(a.getTotalDuration());
            a.stop();
        }
        if (n instanceof Region r && !(n instanceof Control)) for (Node k : r.getChildrenUnmodifiable()) finishAnimations(k);
    }

    /** The header's signature, then the rail's, a line a node (see signature()). */
    public List<String> chromeSignature() {
        List<String> out = new ArrayList<>();
        out.add("--- header ---");
        for (Node n : ui.header.getChildren()) sign(n, "", out);
        out.add("--- rail ---");
        for (Node n : ui.tabRail.getChildren()) sign(n, "", out);
        return out;
    }

    /** The header's pixels and the rail's, each as pixels() gives them: {header, rail}. */
    public int[][] chromePixels() {
        return new int[][]{pixelsOf(ui.header), pixelsOf(ui.tabRail)};
    }

    private static int[] pixelsOf(Node root) {
        WritableImage img = root.snapshot(null, null);
        int w = (int) img.getWidth(), h = (int) img.getHeight();
        int[] out = new int[2 + w * h];
        out[0] = w;
        out[1] = h;
        img.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), out, 2, w);
        return out;
    }

    /** One figure in the header made wrong: the first label with a digit gets a "9" in front (see spoil()). */
    public String spoilHeader() {
        return spoil(new ArrayList<>(ui.header.getChildren()));
    }

    /** The clock as the header shows it: running or stopped, and the line saying why it stopped itself (null for none). */
    public void clock(boolean running, String pausedBecause) {
        ui.clockRunning = running;
        ui.pausedBecause = pausedBecause;
    }

    /** The speed's rung (an index into the ladder, 0 to 8). */
    public void speed(int rung) {
        ui.speedIndex = rung;
    }

    /** The screen on show, by the name its clearMenu() call gives: the rail lights its tab; a game menu hides the rail and the clock. */
    public void screen(String name) {
        ui.currentScreen = name;
    }

    /** A node of the header's that can take the focus (the play button), given it; whether the scene has it. */
    public boolean focusPlay() {
        Node b = firstButton(ui.header);
        if (b == null) return false;
        b.requestFocus();
        return headerScene.getFocusOwner() == b;
    }

    private static Node firstButton(Node n) {
        if (n instanceof ButtonBase) return n;
        if (n instanceof ScrollPane sp && sp.getContent() != null) return firstButton(sp.getContent());
        if (n instanceof Region r && !(n instanceof Control)) {
            for (Node k : r.getChildrenUnmodifiable()) {
                Node b = firstButton(k);
                if (b != null) return b;
            }
        }
        return null;
    }

    /** Whether the focus is on a node inside the header (the header itself is the scene's root here, where the draw puts the focus). */
    public boolean headerHasFocus() {
        Node owner = headerScene.getFocusOwner();
        if (owner == ui.header) return false;
        for (Node n = owner; n != null; n = n.getParent()) if (n == ui.header) return true;
        return false;
    }

    /**
     * A draw of one pool inside a draw of another (the header's draw reaches the envelope's) gives each its
     * own nodes: the outer's three are handed back to it at the next round, the inner's one to the inner
     * pool - drawn on its own, as a notice read elsewhere draws the envelope - and nothing is made new.
     */
    public static boolean nestedDrawsKeepEach() {
        Kept.Pool outer = new Kept.Pool(), inner = new Kept.Pool();
        Node[] first = new Node[4], second = new Node[4];
        for (int round = 0; round < 2; round++) {
            Node[] into = round == 0 ? first : second;
            Kept.draw(outer, () -> {
                into[0] = Kept.label("a");
                Kept.draw(inner, () -> into[1] = Kept.label("b"));
                into[2] = Kept.label("c");
            });
            Kept.draw(inner, () -> into[3] = Kept.label("b"));
        }
        for (int i = 0; i < first.length; i++) if (first[i] == null || first[i] != second[i]) return false;
        return first[1] == first[3] && outer.lastMade() == 0 && inner.lastMade() == 0 && !Kept.drawing();
    }

    /* =====================================================================
       THE THREE PANELS (UP2, slice 2b): the city panel, the construction panel, the inbox's list
       ===================================================================== */

    /** The three drawn kept, from their pools (the first draw makes every node): the draws' own milliseconds. */
    public double drawPanelsKept() {
        long t = System.nanoTime();
        ui.summaryScreen.refreshCityPanel();
        ui.refreshConstructionPanel();
        ui.refreshInboxList();
        return (System.nanoTime() - t) / 1e6;
    }

    /** The nodes the last draws of the three made new and handed back: {city made, kept, construction made, kept, list made, kept}. */
    public int[] panelCounts() {
        return new int[]{ui.cityPool.lastMade(), ui.cityPool.lastKept(), ui.sitesPool.lastMade(), ui.sitesPool.lastKept(),
                ui.listPool.lastMade(), ui.listPool.lastKept()};
    }

    /** The panels' top nodes, in order: the same objects from one kept draw to the next. */
    public List<Node> panelTops() {
        List<Node> out = new ArrayList<>(ui.cityPanel.getChildren());
        out.addAll(ui.constructionPanel.getChildren());
        out.addAll(ui.inboxCorner.getChildren());
        return out;
    }

    /**
     * The three built new in place of the kept ones - their pools swapped for empty ones, so every
     * node is made again, as every redraw did before UP2 - and laid out: {draw ms, CSS ms, layout ms}.
     * restoreKeptPanels() puts the kept nodes and pools back.
     */
    public double[] showFreshPanels() throws Exception {
        keptCity = new ArrayList<>(ui.cityPanel.getChildren());
        keptSites = new ArrayList<>(ui.constructionPanel.getChildren());
        keptList = new ArrayList<>(ui.inboxCorner.getChildren());
        keptPanelPools = new Kept.Pool[]{ui.cityPool, ui.sitesPool, ui.listPool};
        ui.cityPool = new Kept.Pool();
        ui.sitesPool = new Kept.Pool();
        ui.listPool = new Kept.Pool();
        long t = System.nanoTime();
        ui.summaryScreen.refreshCityPanel();
        ui.refreshConstructionPanel();
        ui.refreshInboxList();
        double ms = (System.nanoTime() - t) / 1e6;
        double[] p = panelPasses();
        return new double[]{ms, p[0], p[1]};
    }

    /** The kept panels back where they were, with their pools, and laid out. */
    public void restoreKeptPanels() throws Exception {
        ui.cityPool = keptPanelPools[0];
        ui.sitesPool = keptPanelPools[1];
        ui.listPool = keptPanelPools[2];
        ui.cityPanel.getChildren().setAll(keptCity);
        ui.constructionPanel.getChildren().setAll(keptSites);
        ui.inboxCorner.getChildren().setAll(keptList);
        panelPasses();
    }

    /** The three scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed. */
    public double[] panelPasses() throws Exception {
        double[] a = passes(cityScene, ui.cityPanel), b = passes(sitesScene, ui.constructionPanel), c = passes(listScene, ui.inboxCorner);
        return new double[]{a[0] + b[0] + c[0], a[1] + b[1] + c[1]};
    }

    /** The three panels' signatures, one after the other, a line a node (see signature()). */
    public List<String> panelSignature() {
        List<String> out = new ArrayList<>();
        out.add("--- city panel ---");
        for (Node n : ui.cityPanel.getChildren()) sign(n, "", out);
        out.add("--- construction panel ---");
        for (Node n : ui.constructionPanel.getChildren()) sign(n, "", out);
        out.add("--- inbox list " + (ui.inboxCorner.isVisible() ? "shown" : "hidden") + " ---");
        for (Node n : ui.inboxCorner.getChildren()) sign(n, "", out);
        return out;
    }

    /** The three panels' pixels, each as pixels() gives them: {city, construction, inbox list}. */
    public int[][] panelPixels() {
        return new int[][]{pixelsOf(ui.cityPanel), pixelsOf(ui.constructionPanel), pixelsOf(ui.inboxCorner)};
    }

    /** One figure in the city panel made wrong: the first label with a digit gets a "9" in front (see spoil()). */
    public String spoilPanel() {
        return spoil(new ArrayList<>(ui.cityPanel.getChildren()));
    }

    /** The city panel's mode: Dashboard or Summary, and whether the header's chip opened it on NEEDS YOU (which draws Summary). */
    public void cityMode(boolean dashboard, boolean needsView) {
        ui.prefs.setPanelDashboard(dashboard);
        ui.summaryScreen.needsView = needsView;
    }

    /** One dashboard section opened or folded, as its header's click would. */
    public void section(String key, boolean open) {
        if (open) ui.summaryScreen.panelOpen.add(key);
        else ui.summaryScreen.panelOpen.remove(key);
    }

    /** "Open all" or "close all", as the dashboard's links do. */
    public void sectionsAll(boolean open) {
        if (open) ui.summaryScreen.panelOpen.addAll(java.util.Arrays.asList(SummaryScreen.PANEL_SECTIONS));
        else ui.summaryScreen.panelOpen.clear();
    }

    /** The drawer open or shut (the header's chip), pinned or not: the chip's look follows, the panel's nodes do not. */
    public void drawer(boolean open, boolean pinned) {
        ui.drawerOpen = open;
        ui.prefs.setDrawerPinned(pinned);
    }

    /** The inbox's list dropped down or put away, and which notice is unfolded ("" for none). */
    public void inboxList(boolean open, String expandedKey) {
        ui.inboxOpen = open;
        ui.inboxExpanded = expandedKey;
    }

    /** The first node of the city panel with a hover look given by code (a summary row), or null. */
    public Node firstHoverInCityPanel() {
        for (Node n : ui.cityPanel.getChildren()) {
            Node h = firstHover(n);
            if (h != null) return h;
        }
        return null;
    }

    private static Node firstHover(Node n) {
        if (n.getOnMouseEntered() != null) return n;
        List<Node> inner = new ArrayList<>();
        if (n instanceof Region r && !(n instanceof Control)) inner.addAll(r.getChildrenUnmodifiable());
        if (n instanceof ScrollPane sp && sp.getContent() != null) inner.add(sp.getContent());
        for (Node k : inner) {
            Node h = firstHover(k);
            if (h != null) return h;
        }
        return null;
    }

    /** The inbox list's action button (an unfolded notice's), given the focus; whether the scene has it. */
    public boolean focusInboxButton() {
        Node b = firstButton(ui.inboxCorner);
        if (b == null) return false;
        b.requestFocus();
        return listScene.getFocusOwner() == b;
    }

    /** Whether the focus is on a node inside the inbox list (the box itself is the scene's root here, where the draw puts the focus). */
    public boolean inboxHasFocus() {
        Node owner = listScene.getFocusOwner();
        if (owner == ui.inboxCorner) return false;
        for (Node n = owner; n != null; n = n.getParent()) if (n == ui.inboxCorner) return true;
        return false;
    }

    /** The city panel's scroller (kept: the same object from one draw to the next). */
    public ScrollPane cityScroller() {
        for (Node n : ui.cityPanel.getChildren()) if (n instanceof ScrollPane sp) return sp;
        return null;
    }

    /** Every kept node of the chrome let go, as anotherCity() lets them go: the next draw makes them all new. */
    public void forgetChrome() {
        ui.forgetChrome();
    }

    /** The header's i-th child (the clock, the money block, the row of tiles, the rating and inbox). */
    public Node headerChild(int i) {
        return ui.header.getChildren().get(i);
    }

    /** The rail's i-th child (a destination's button, or the gap, the minimap, Menu). */
    public Node railChild(int i) {
        return ui.tabRail.getChildren().get(i);
    }

    /** The pointer put over a node, or taken off it, as the scene's own mouse handling does (Node.setHover). */
    public static void hover(Node n, boolean on) throws ReflectiveOperationException {
        Method m = Node.class.getDeclaredMethod("setHover", boolean.class);
        m.setAccessible(true);
        m.invoke(n, on);
    }

    private static Method doCSSPass, doLayoutPass;

    /**
     * The scene's CSS and layout, as a frame's pulse runs them (Scene.doCSSPass and doLayoutPass,
     * which touch only what is dirty): {CSS ms, layout ms}. Where the toolkit will not open them,
     * the public applyCss() and layout() instead, which restyle every node and so cost more.
     */
    public double[] passes() throws Exception {
        return passes(scene, column);
    }

    private static double[] passes(Scene sc, javafx.scene.Parent root) throws Exception {
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
        if (pulse) doCSSPass.invoke(sc);
        else root.applyCss();
        long b = System.nanoTime();
        if (pulse) doLayoutPass.invoke(sc);
        else root.layout();
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
        if (n instanceof Control c && c.getTooltip() != null) b.append(" ctip{").append(c.getTooltip().getText()).append('}');
        Bounds at = n.getBoundsInParent();
        b.append(String.format(Locale.ROOT, PRECISE ? " at %.4f,%.4f %.4fx%.4f" : " at %.1f,%.1f %.1fx%.1f", at.getMinX(), at.getMinY(), at.getWidth(), at.getHeight()));
        out.add(b.toString());
        String inner = path + "  ";
        if (n instanceof Labeled l && l.getGraphic() != null) sign(l.getGraphic(), inner + "graphic: ", out);
        // A Pane's children, and those of the game's own pictures that are a Region and no Pane (the header's tile).
        if (n instanceof Region r && !(n instanceof Control)) for (Node k : r.getChildrenUnmodifiable()) sign(k, inner, out);
        if (n instanceof ScrollPane sp && sp.getContent() != null) sign(sp.getContent(), inner + "content: ", out);
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
