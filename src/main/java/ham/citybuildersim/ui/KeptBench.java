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
import javafx.scene.control.Label;
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
 * made them). Since KP1 City History too, on the same scene: its page drawn kept and built new, the
 * hands on what reshapes it, the pointer on its big chart, and the real show methods for what the clock decides.
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

    /* =====================================================================
       CITY HISTORY (KP1): the page kept and built new, and the hands on its choices
       ===================================================================== */

    private List<Node> keptHistory = List.of();
    private Kept.Pool keptHistoryPool;
    /** What a page built new took from the charts the kept page holds: their pools, their chips, and where they sat. */
    private Kept.Pool[] keptChipPools;
    private List<List<Node>> keptLegend = new ArrayList<>(), keptRanges = new ArrayList<>();
    private javafx.scene.Parent[] chartParents;
    private int[] chartAt;

    /**
     * City History's stage: the screen's name (its scroller is filed under it), the first-visit preset
     * (showHistoryMenu() hands it over, and the bench draws the page itself), the window's scroller the
     * page's height reads, and a stack for the chart's full screen to go in.
     */
    private void historyStage() {
        historyEnsure();
        ui.currentScreen = "showHistoryMenu";
    }

    /** ...without naming the screen on show: the real show methods name it themselves, and arriving is not being there. */
    private void historyEnsure() {
        if (!ui.historyScreen.historySeeded) {
            ui.historyScreen.historySeeded = true;
            ui.historyScreen.historyPicked.addAll(java.util.Arrays.asList(HistoryScreen.PRESETS[0].keys()));
        }
        if (ui.menuScroller == null) {
            ScrollPane window = new ScrollPane();
            window.resize(WIDTH, 900);
            ui.menuScroller = window;
        }
        if (ui.windowStack == null) ui.windowStack = new javafx.scene.layout.StackPane();
    }

    /** City History drawn kept from its pool (the first draw makes every node): the draw's own milliseconds. */
    public double drawHistoryKept() {
        historyStage();
        long t = System.nanoTime();
        Kept.draw(ui.historyScreen.pool, () -> ui.historyScreen.drawPage(false));
        return (System.nanoTime() - t) / 1e6;
    }

    /** The nodes the last kept draw of City History made new and handed back: {made, kept}. */
    public int[] historyCounts() {
        return new int[]{ui.historyScreen.pool.lastMade(), ui.historyScreen.pool.lastKept()};
    }

    /** The page's top nodes (the frame and its scroller), kept or not: the same objects from one kept draw to the next. */
    public List<Node> historyTop() {
        return new ArrayList<>(column.getChildren());
    }

    /** The charts the page holds, for scene and parent checks: the big chart, then the two pins' (those made). */
    public List<Node> historyCharts() {
        List<Node> out = new ArrayList<>();
        for (TimeChart c : ui.historyScreen.charts()) if (c != null) out.add(c);
        return out;
    }

    /**
     * City History built new in place of the kept page - its pool and the big chart's chips' pool swapped for
     * empty ones, so every node is made again, as every redraw was before KP1 - and laid out: {its draw ms,
     * CSS ms, layout ms}. The charts are the screen's own and shared by both pages, so a page built new takes
     * them off the kept one; restoreKeptHistory() puts them back.
     */
    public double[] showFreshHistory() throws Exception {
        historyStage();
        HistoryScreen screen = ui.historyScreen;
        TimeChart[] charts = screen.charts();
        keptHistory = new ArrayList<>(column.getChildren());
        keptHistoryPool = screen.pool;
        keptChipPools = new Kept.Pool[charts.length];
        keptLegend = new ArrayList<>();
        keptRanges = new ArrayList<>();
        chartParents = new javafx.scene.Parent[charts.length];
        chartAt = new int[charts.length];
        for (int i = 0; i < charts.length; i++) {
            TimeChart c = charts[i];
            if (c == null) {
                keptLegend.add(List.of());
                keptRanges.add(List.of());
                continue;
            }
            chartParents[i] = c.getParent();
            chartAt[i] = chartParents[i] == null ? -1 : chartParents[i].getChildrenUnmodifiable().indexOf(c);
            keptChipPools[i] = c.chipPool;
            keptLegend.add(new ArrayList<>(c.legend.getChildren()));
            keptRanges.add(new ArrayList<>(c.ranges.getChildren()));
            c.chipPool = new Kept.Pool();
        }
        screen.pool = new Kept.Pool();
        long t = System.nanoTime();
        Kept.draw(screen.pool, () -> screen.drawPage(false));
        double ms = (System.nanoTime() - t) / 1e6;
        double[] p = passes();
        return new double[]{ms, p[0], p[1]};
    }

    /** The kept page back in the column with its pool, the charts back in the boxes that held them, and laid out. */
    public void restoreKeptHistory() throws Exception {
        HistoryScreen screen = ui.historyScreen;
        screen.pool = keptHistoryPool;
        TimeChart[] charts = screen.charts();
        ui.setPage(keptHistory.toArray(new Node[0]));
        for (int i = 0; i < charts.length; i++) {
            TimeChart c = charts[i];
            if (c == null || keptChipPools[i] == null) continue;
            c.chipPool = keptChipPools[i];
            Kept.set(c.legend.getChildren(), keptLegend.get(i));
            Kept.set(c.ranges.getChildren(), keptRanges.get(i));
            if (chartParents[i] instanceof Pane box && c.getParent() != box) {
                box.getChildren().add(Math.max(0, Math.min(chartAt[i], box.getChildren().size())), c);
            }
        }
        passes();
    }

    /**
     * The page laid out again, as the next frames would, until a pass moves nothing (at most SETTLE_PASSES): a wrapped
     * label's height settles over a pass or two, and the scroller's under the frame a pass after that.
     */
    public void settle() throws Exception {
        int was = signature().hashCode();
        for (int i = 0; i < SETTLE_PASSES; i++) {
            passes();
            int now = signature().hashCode();
            if (now == was) return;
            was = now;
        }
    }

    /** The most layout passes settle() runs. */
    private static final int SETTLE_PASSES = 8;

    /** One figure on City History made wrong: the first label whose words hold a digit gets a "9" in front (see spoil()). */
    public String spoilHistory() {
        return spoil(new ArrayList<>(column.getChildren()));
    }

    /** Every animation a draw started on the page run to its end, as a pulse would have: a scene with no window has none. */
    public void finishPageAnimations() {
        each(column, KeptBench::finishOne);
    }

    private static void finishOne(Node n) {
        javafx.animation.Animation a = Kept.animationOf(n);
        if (a != null && a.getStatus() != javafx.animation.Animation.Status.STOPPED) {
            a.jumpTo(a.getTotalDuration());
            a.stop();
        }
    }

    /** `f` on a node and everything under it, through the scroller's content and a label's graphic too. */
    private static void each(Node n, java.util.function.Consumer<Node> f) {
        f.accept(n);
        if (n instanceof Labeled l && l.getGraphic() != null) each(l.getGraphic(), f);
        if (n instanceof ScrollPane sp && sp.getContent() != null) each(sp.getContent(), f);
        if (n instanceof Group g) for (Node k : new ArrayList<>(g.getChildren())) each(k, f);
        if (n instanceof Region r && !(n instanceof Control)) for (Node k : new ArrayList<>(r.getChildrenUnmodifiable())) each(k, f);
    }

    private int graphicMoves;

    /** Every graphic of a label on the page now watched for entering or leaving the scene: how many are watched. */
    public int watchGraphics() {
        int[] n = {0};
        each(column, node -> {
            if (node instanceof Labeled l && l.getGraphic() != null) {
                l.getGraphic().sceneProperty().addListener((o, was, now) -> graphicMoves++);
                n[0]++;
            }
        });
        return n[0];
    }

    /** How many times a watched graphic left or entered the scene since this was last asked. */
    public int graphicMoves() {
        int moves = graphicMoves;
        graphicMoves = 0;
        return moves;
    }

    /** The first node of this class under the page, or null. */
    private <T extends Node> T first(Class<T> type) {
        List<T> found = new ArrayList<>();
        each(column, n -> {
            if (found.isEmpty() && type.isInstance(n)) found.add(type.cast(n));
        });
        return found.isEmpty() ? null : found.get(0);
    }

    /* ----- the hands on what reshapes the page ----- */

    /** A line ticked or unticked, as its chip would (the next draw shows it). */
    public void historyPick(String key, boolean on) {
        if (on) ui.historyScreen.historyPicked.add(key);
        else ui.historyScreen.historyPicked.remove(key);
        ui.historyScreen.groupOpen.put(ui.historyScreen.traceFor(key).group(), true);
    }

    /** Exactly these lines drawn, none hidden (what "clear all" and a preset leave). */
    public void historyPickOnly(String... keys) {
        ui.historyScreen.historyPicked.clear();
        ui.historyScreen.historyPicked.addAll(java.util.Arrays.asList(keys));
        ui.historyScreen.hiddenLines.clear();
    }

    /** How many presets there are. */
    public static int historyPresets() {
        return HistoryScreen.PRESETS.length;
    }

    /** The i-th preset pressed, as its chip would (the lines it names, none hidden). */
    public void historyPreset(int i) {
        historyPickOnly(HistoryScreen.PRESETS[i].keys());
    }

    /** The picker's group (by name) a line is in. */
    public String historyGroupOf(String key) {
        return ui.historyScreen.traceFor(key).group();
    }

    /** A picker group opened or closed, as its heading would. */
    public void historyGroup(String group, boolean open) {
        ui.historyScreen.groupOpen.put(group, open);
    }

    /** Typed into the picker's filter box, as a key would (it refills the groups in place); false when the page has no box. */
    public boolean historyType(String text) {
        javafx.scene.control.TextField box = first(javafx.scene.control.TextField.class);
        if (box == null) return false;
        box.setText(text);
        return true;
    }

    /** A fold of the page opened or closed ("hard times", "goods"), as its toggle would. */
    public void historyFold(String key, boolean open) {
        if (open) ui.historyScreen.folds.add(key);
        else ui.historyScreen.folds.remove(key);
    }

    /** The big chart on a log scale or off, as the chip would; why it cannot be drawn, or null when it can. */
    public String historyLog(boolean on) {
        ui.historyScreen.historyLog = on;
        return ui.historyScreen.logRefusal(ui.game.getHistorySave());
    }

    /** Real GDP's layers on or off, as the chip would. */
    public void historyLayers(boolean on) {
        ui.historyScreen.gdpLayers = on;
    }

    /** The legend chip i of the big chart clicked, as the pointer would (it hides or shows the line); false when it has none. */
    public boolean historyLegendClick(int i) {
        TimeChart big = ui.historyScreen.charts()[0];
        if (big == null || i < 0 || i >= big.legend.getChildren().size()) return false;
        Node chip = big.legend.getChildren().get(i);
        fireMouse(chip, javafx.scene.input.MouseEvent.MOUSE_CLICKED, 5, 5, javafx.scene.input.MouseButton.PRIMARY, 1);
        return true;
    }

    /** The big chart's legend chips, as they are now. */
    public List<Node> historyLegend() {
        TimeChart big = ui.historyScreen.charts()[0];
        return big == null ? List.of() : new ArrayList<>(big.legend.getChildren());
    }

    /** The tooltip installed on a node (the object, to see it is the same one from month to month), or null. */
    public static Object tooltipOf(Node n) {
        return Kept.tipOf(n);
    }

    /** The big chart's range buttons, as they are now. */
    public List<Node> historyRanges() {
        TimeChart big = ui.historyScreen.charts()[0];
        return big == null ? List.of() : new ArrayList<>(big.ranges.getChildren());
    }

    /** How many lines the legend has switched off. */
    public int historyHidden() {
        return ui.historyScreen.hiddenLines.size();
    }

    /** The chart's window moved to these months, as a drag or an episode's row would. */
    public void historyWindow(double from, double to) {
        ui.historyScreen.chartWindow.setWindow(from, to);
    }

    /** The chart's window on one of its ranges (a number of months, as ChartModel.RANGES gives them, or ChartModel.ALL), as a range button would. */
    public void historyRange(int months) {
        ui.historyScreen.chartWindow.showRange(months);
    }

    /** The window as it is: {first month shown, last month shown}. */
    public int[] historyWindow() {
        return new int[]{ui.historyScreen.chartWindow.firstMonthShown(), ui.historyScreen.chartWindow.lastMonthShown()};
    }

    /** The history's ends: {first month, last month}. */
    public int[] historyBounds() {
        List<Integer> axis = ui.game.getHistorySave().getMonth();
        return new int[]{axis.get(0), axis.get(axis.size() - 1)};
    }

    /** The first named episode's span with a year either side, as moveChartTo(episode) would put the window, or null when the city has none. */
    public double[] historyEpisodeSpan() {
        List<ham.citybuildersim.YearBook.Episode> episodes = ham.citybuildersim.ChartModel.episodes(ui.game.getHistorySave());
        if (episodes.isEmpty()) return null;
        ham.citybuildersim.YearBook.Episode e = episodes.get(0);
        return new double[]{e.fromMonth() - 12, e.toMonth() + 12};
    }

    /** A line pinned, as its chip would (it writes the city's scratch preferences). */
    public void historyPin(String key) {
        ui.historyScreen.pin(key);
    }

    /** A pin taken off, as its "unpin" would. */
    public void historyUnpin(boolean left) {
        ui.historyScreen.unpin(left);
    }

    /** The line on a pin. */
    public String historyPinned(boolean left) {
        return ui.historyScreen.pinned(left);
    }

    /** The chart's full screen on or off, as its button would (the page is drawn by the caller). */
    public void historyFull(boolean on) {
        ui.historyScreen.setChartFull(on, false);
    }

    /** The year book's card shown (with this said) or put away (null), without writing a book. */
    public void historyBook(String said) {
        ui.historyScreen.bookExportSaid = said;
    }

    /* ----- the pointer on the big chart ----- */

    /** The pointer moved over the big chart's plot, as the toolkit would: a MOUSE_MOVED fired on the canvas; whether the chart now shows its crosshair. */
    public boolean historyHoverPlot() {
        TimeChart big = ui.historyScreen.charts()[0];
        if (big == null) return false;
        fireMouse(big.plot, javafx.scene.input.MouseEvent.MOUSE_MOVED, big.plot.getWidth() / 2, big.plot.getHeight() / 2,
                javafx.scene.input.MouseButton.NONE, 0);
        return big.hovering();
    }

    /** Whether the big chart shows its crosshair. */
    public boolean historyHovering() {
        TimeChart big = ui.historyScreen.charts()[0];
        return big != null && big.hovering();
    }

    private static void fireMouse(Node target, javafx.event.EventType<javafx.scene.input.MouseEvent> type, double x, double y,
                                  javafx.scene.input.MouseButton button, int clicks) {
        javafx.event.Event.fireEvent(target, new javafx.scene.input.MouseEvent(type, x, y, x, y, button, clicks,
                false, false, false, false, button == javafx.scene.input.MouseButton.PRIMARY, false, false,
                false, false, true, null));
    }

    /* ----- the real show methods, for what the clock decides (the count-up) ----- */

    /** The speed ladder, as the clock steps along it. */
    public static double[] speedLadder() {
        return UserInterface.SPEEDS.clone();
    }

    /** The fastest speed a figure still counts up at. */
    public static double countUpTop() {
        return UserInterface.COUNT_UP_TOP;
    }

    /** People shown, as the rail's tab or a month landing shows it: the real show method (the chrome, then the page kept). */
    public void showPeople() {
        ui.peopleScreen.showPopulationInfoMenu();
    }

    /** City History shown, as the rail's tab or a month landing shows it. */
    public void showHistory() {
        historyEnsure();
        ui.historyScreen.showHistoryMenu();
    }

    /** People forgotten, as another city opened would leave it: nothing kept, nothing just landed. */
    public void forgetPeople() {
        ui.peopleScreen.forget();
    }

    /** City History forgotten, as another city opened would leave it. */
    public void forgetHistory() {
        ui.historyScreen.forget();
    }

    /** The count-up last started on LIVING HERE's figure on People, or null. */
    public javafx.animation.Animation peopleCount() {
        Label figure = livingHere();
        return figure == null ? null : Kept.animationOf(figure);
    }

    /** The count-up last started on the left pin's figure on City History, or null. */
    public javafx.animation.Animation pinCount() {
        Pieces.CardHead head = first(Pieces.CardHead.class);
        return head == null ? null : Kept.animationOf(head.figure);
    }

    private Label livingHere() {
        List<Label> found = new ArrayList<>();
        each(column, n -> {
            if (found.isEmpty() && n instanceof VBox v && v.getChildren().size() > 1
                    && v.getChildren().get(0) instanceof Label what && what.getText() != null
                    && what.getText().startsWith("LIVING HERE") && v.getChildren().get(1) instanceof Label figure) found.add(figure);
        });
        return found.isEmpty() ? null : found.get(0);
    }

    /** Whether last month's headcount differs from this month's, as LIVING HERE's count-up reads them (it counts nothing from the same figure). */
    public boolean headcountMoved() {
        List<Long> pop = ui.game.getHistorySave().getPopulation();
        return pop.size() >= 2 && pop.get(pop.size() - 2) != ui.game.getPopulationManager().getPopulation();
    }

    /** The line on the left pin set directly (the preferences are not written): a line that moves most months, for the count-up. */
    public void historyPinLeft(String key) {
        ui.prefs.setPinnedLeft(key);
    }

    /** The left pin's figure on the page now, and the figure the model gives it now: they differ when a month has landed that the page has not drawn. */
    public String[] pinFigures() {
        Pieces.CardHead head = first(Pieces.CardHead.class);
        return new String[]{head == null ? null : head.figure.getText(),
                ui.historyScreen.pinHead(ui.game.getHistorySave(), true).figure()};
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

    /** Whether a signature writes a node's place and size to four decimals instead of one (-Dkeptbench.precise=true). */
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
