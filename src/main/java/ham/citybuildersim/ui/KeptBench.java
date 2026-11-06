package ham.citybuildersim.ui;

import ham.citybuildersim.Auction;
import ham.citybuildersim.BuildAdvice;
import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.ClockPace;
import ham.citybuildersim.FreightView;
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
import javafx.scene.layout.HBox;
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
 * Since KP2 Infrastructure, Services and the land office too, as {@link Page}s: one small shape for the three, each drawn
 * kept from its screen's pool and built new from an empty one, with the hands on what reshapes them, on the fare dial and
 * City History's filter box (the controls) and on the nodes a pointer styles (the hover).
 * Since KP3 the Build screen (the Overview and every category, with the hands on a ring, an order dialled by a card's
 * stepper, a stat card pinned and automatic building), the pages a build is stopped on, the funding and fell-short pages,
 * and the construction page (its tabs, a building picked to demolish, the find box), as Pages of the same shape.
 * Since KP4 the Sectors tab: the list and a business's five pages in both views as Pages, with the hands on a business, a page,
 * the view, Show more, Freight's divisions, the folds and a statement's notes and columns, the pictures the pages hold (the refinery's,
 * a materials industry's, a leg's bar), the figures that count up and the waterfall and ring that grow and sweep, the oil reserve's
 * dials, and the card, the door and the crumb the pointer rests on.
 * Since UP7 the drawer's pinned bar and the construction tab too, each a box made once on a scene of its own and drawn by the real
 * refreshDrawerBar() and refreshConstructionTab() (kept, and built new), and the header's speed reading, its pace swapped for one on a
 * clock the harness moves by hand.
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

    /** The drawer's bar (the drawer's width) and the construction tab (its own width, the window's height), on scenes of their own (UP7). */
    public static final double BAR_WIDTH = 290, BAR_HEIGHT = 40;
    private final Scene barScene, tabScene;
    private List<Node> keptBar = List.of(), keptTab = List.of();
    private Kept.Pool[] keptFramePools;

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
        // ...and the drawer's bar and the construction tab (UserInterface.drawerBarBox() and constructionTabBox()).
        ui.drawerBar = UserInterface.drawerBarBox();
        ui.constructionTab = UserInterface.constructionTabBox();
        barScene = new Scene(ui.drawerBar, BAR_WIDTH, BAR_HEIGHT);
        tabScene = new Scene(ui.constructionTab, UserInterface.CONSTRUCTION_TAB, RAIL_HEIGHT);
        ui.applyTheme(barScene);
        ui.applyTheme(tabScene);
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
        ensureWindow();
    }

    /** The window a page reads: the stand-in scroller its height follows, and the stack a full screen goes in. */
    private void ensureWindow() {
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
        settlePasses();
        // A scroller works out its bars when it is laid out, and a bar that comes or goes narrows or widens everything in it: a kept scroller whose page
        // has grown or shrunk by hundreds of pixels since it was last settled was left a layout behind a new one (its page 13 px too wide, or too
        // narrow). Such a scroller is laid out again, and the page settled again (KP5, slice D).
        boolean kicked = false;
        for (ScrollPane sp : scrollersOnPage()) {
            double h = sp.getContent() == null ? 0 : sp.getContent().getLayoutBounds().getHeight();
            Object was = sp.getProperties().put(SETTLED_HEIGHT, h);
            if (was instanceof Double d && Math.abs(d - h) > 400) {
                sp.requestLayout();
                kicked = true;
            }
        }
        if (kicked) settlePasses();
    }

    /** The key a scroller keeps the height its content had when the page was last settled, in its properties. */
    private static final String SETTLED_HEIGHT = "keptbench.settledHeight";

    private List<ScrollPane> scrollersOnPage() {
        List<ScrollPane> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof ScrollPane sp) out.add(sp);
        });
        return out;
    }

    private void settlePasses() throws Exception {
        int was = signature().hashCode();
        int quiet = 0;
        for (int i = 0; i < SETTLE_PASSES; i++) {
            passes();
            int now = signature().hashCode();
            // Two quiet passes in a row (KP5, slice D): the scroller under a frame takes its height from the frame's, and a frame whose
            // height moves a pass after its children stop moving was read as settled at the first quiet pass - a first draw, and now
            // and then a month, then differed by a few pixels in the frame's column from a page that had been through one pass more.
            if (now == was) {
                if (++quiet >= QUIET_PASSES) return;
            } else {
                quiet = 0;
            }
            was = now;
        }
    }

    /** The most layout passes settle() runs, and the quiet ones in a row it takes to stop. */
    private static final int SETTLE_PASSES = 10, QUIET_PASSES = 2;

    /** One figure on City History made wrong: the first label whose words hold a digit gets a "9" in front (see spoil()). */
    public String spoilHistory() {
        return spoil(new ArrayList<>(column.getChildren()));
    }

    /**
     * Every animation a draw started on the page run to its end, as a pulse would have: a scene with no window has none. Its
     * on-finished handler runs too (a pulse that reaches the end calls it; a jump and a stop do not): the glow a built tag flashes
     * is taken off by it, and a count-up's last word set. The page is laid out again after this, not before (see KeptCheck.againstNew).
     */
    public void finishPageAnimations() {
        each(column, KeptBench::finishOne);
    }

    private static void finishOne(Node n) {
        javafx.animation.Animation a = Kept.animationOf(n);
        if (a != null && a.getStatus() != javafx.animation.Animation.Status.STOPPED) {
            a.jumpTo(a.getTotalDuration());
            a.stop();
            if (a.getOnFinished() != null) a.getOnFinished().handle(new javafx.event.ActionEvent());
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
        javafx.event.Event.fireEvent(target, mouse(type, x, y, button, clicks));
    }

    private static javafx.scene.input.MouseEvent mouse(javafx.event.EventType<javafx.scene.input.MouseEvent> type, double x, double y,
                                                       javafx.scene.input.MouseButton button, int clicks) {
        return new javafx.scene.input.MouseEvent(type, x, y, x, y, button, clicks,
                false, false, false, false, button == javafx.scene.input.MouseButton.PRIMARY, false, false,
                false, false, true, null);
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

    /* =====================================================================
       INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2): the three pages kept and built new,
       and the hands on what reshapes them, the controls on them and the pointer over them
       ===================================================================== */

    /**
     * One of the three pages on the bench: the name the window shows it under, the screen's own pool, how it is
     * drawn from a pool (what the screen's show method works out first, then the page's code as a kept draw) and how
     * it goes back to the page it opens on. Drawn from the screen's pool it is the kept page; from an empty one, the
     * page built new, as every redraw was before KP2.
     */
    public final class Page {
        private final String name, screen;
        private final java.util.function.Supplier<Kept.Pool> pool;
        private final java.util.function.ToDoubleFunction<Kept.Pool> draw;
        private final Runnable home;

        private Page(String name, String screen, java.util.function.Supplier<Kept.Pool> pool,
                     java.util.function.ToDoubleFunction<Kept.Pool> draw, Runnable home) {
            this.name = name;
            this.screen = screen;
            this.pool = pool;
            this.draw = draw;
            this.home = home;
        }

        /** What the harness's lines call the page. */
        public String name() {
            return name;
        }
    }

    /** The draw's own milliseconds, for a page's code run as a kept draw from this pool. */
    private double timed(Kept.Pool pool, Runnable page) {
        long t = System.nanoTime();
        Kept.draw(pool, page);
        return (System.nanoTime() - t) / 1e6;
    }

    /** Infrastructure's tab: the page `infraPage` (and on Freight `freightDivision`) names, over its fold state. */
    public Page infrastructure() {
        InfrastructureScreen s = ui.infrastructureScreen;
        return new Page("Infrastructure", "showInfrastructureMenu", () -> s.pool, pool -> {
            List<ham.citybuildersim.CityNeeds.Need> all = ham.citybuildersim.CityNeeds.measure(ui.game, SummaryScreen.WORDS);
            return timed(pool, () -> s.drawPage(all));
        }, () -> {
            s.infraPage = "Roads";
            s.freightDivision = "All";
            s.detailsOpen.clear();
            s.forget();
        });
    }

    /** Services' tab: the sub-page `serviceArea` and `servicePage` name, over its fold state. */
    public Page services() {
        ServicesScreen s = ui.servicesScreen;
        return new Page("Services", "showServicesStatsMenu", () -> s.pool, pool -> {
            List<ham.citybuildersim.CityNeeds.Need> all = ham.citybuildersim.CityNeeds.measure(ui.game, SummaryScreen.WORDS);
            List<ServicesScreen.Event> events = s.events();
            return timed(pool, () -> s.drawPage(all, events));
        }, () -> {
            s.serviceArea = ServicesScreen.SERVICE_AREA_HOME;
            s.servicePage = ServicesScreen.SERVICE_HOME;
            s.detailsOpen.clear();
            s.forget();
        });
    }

    /** The land office: its side and place, the best-N count and its fold as the screen has them; the map is the screen's own. */
    public Page land() {
        LandScreen s = ui.landScreen;
        return new Page("the land office", "showLandMenu", () -> s.pool, pool -> {
            // What showLandMenu() works out before its draw.
            s.ensureMap();
            s.noteShelf();
            s.chooseSide();
            LandScreen.Next next = s.next();
            return timed(pool, () -> s.drawOffice(next));
        }, () -> {
            s.forget();
            s.nextCount = 5;
            s.detailsOpen = false;
        });
    }

    /** The land office's funding page for these plots (Game.landNeedsFunding() holds for them). */
    public Page landFunding(List<Integer> ids) {
        LandScreen s = ui.landScreen;
        return new Page("the funding page", "showLandFunding", () -> s.pool, pool -> {
            s.ensureMap();
            return timed(pool, () -> s.drawFunding(ids));
        }, s::forget);
    }

    /** The page for when the city fell short: `bought` of `asked` plots, the loan `paper` taken (null: the vault's dollars). */
    public Page landFellShort(String paper, int bought, int asked) {
        LandScreen s = ui.landScreen;
        return new Page(paper == null ? "the fell-short page (the vault's dollars)" : "the fell-short page (a " + paper + ")", "showLandFellShort", () -> s.pool, pool -> {
            s.ensureMap();
            return timed(pool, () -> s.drawFellShort(paper, bought, asked));
        }, s::forget);
    }

    /** A page drawn kept from its screen's own pool (an arrival from another screen forgets it first, as keepMenu does): the draw's own milliseconds. */
    public double drawKept(Page p) {
        ensureWindow();
        if (!p.screen.equals(ui.currentScreen)) p.pool.get().forget();
        ui.currentScreen = p.screen;
        return p.draw.applyAsDouble(p.pool.get());
    }

    /** A page back on the page it opens on, nothing kept: the next draw makes every node. */
    public void home(Page p) {
        p.home.run();
    }

    /**
     * What one kept draw set on the nodes it handed back (KP2b): how many times a style, a text, a graphic, a padding or a preferred width
     * was written on a node that was on the page before the draw (`count`), those written from a file the caller names as not yet mended
     * (`ignored`, not in the count), and the sources of the first, most first (each once, with how often and the first node it wrote:
     * "x14 Pieces.java:132 < Kept.java:61 style: Column .style [...] -> [...]").
     */
    public record Sets(int count, int ignored, List<String> first) { }

    /**
     * One more kept draw of a page, the city unchanged (no month between), with a listener on every node of the page before it: on its style;
     * on a label's or a text's words and a label's graphic; on a region's padding and preferred width. Each listener counts and reads its
     * property again, so the next write is seen too: a value set to what it already is sets nothing (a String property compares by value),
     * and a value set to another and back, or an Insets made again (an object property compares by reference), is counted at each write.
     * The page is drawn and laid out before this is called, and no CSS or layout pass runs inside it (a pass writes properties of its own).
     * It does not see what moves no property: a size or a margin set to what it is (Region.setPrefSize, HBox.setMargin) asks its parent for a
     * layout all the same, whatever it is given.
     * The listeners are taken off before it returns. A write whose nearest line of the game's code is in one of the files `unmended` names
     * is counted apart: a screen outside the sweep's list, named by the check that asks, with its reason.
     */
    public Sets redrawSets(Page p, java.util.Set<String> unmended) {
        return redrawSets(column, () -> drawKept(p), unmended);
    }

    /**
     * ...the same for any root and any draw (UP7): the listeners go on `root` and everything under it (a label, a box and its children), `draw`
     * runs - a screen's refresh, the header's, the speed's repaint - and they come off. redrawSets(Page, ...) is this over the page's column.
     */
    public Sets redrawSets(Node root, Runnable draw, java.util.Set<String> unmended) {
        int[] count = {0, 0};
        java.util.Map<String, Object[]> sources = new java.util.LinkedHashMap<>();
        List<Runnable> off = new ArrayList<>();
        each(root, n -> {
            watchSets(n, "style", n.styleProperty(), count, sources, unmended, off);
            if (n instanceof Labeled l) {
                watchSets(n, "text", l.textProperty(), count, sources, unmended, off);
                watchSets(n, "graphic", l.graphicProperty(), count, sources, unmended, off);
            } else if (n instanceof Text t) {
                watchSets(n, "text", t.textProperty(), count, sources, unmended, off);
            }
            if (n instanceof Region r) {
                watchSets(n, "padding", r.paddingProperty(), count, sources, unmended, off);
                watchSets(n, "prefWidth", r.prefWidthProperty(), count, sources, unmended, off);
            }
        });
        try {
            draw.run();
        } finally {
            for (Runnable r : off) r.run();
        }
        List<String> first = new ArrayList<>();
        sources.entrySet().stream().sorted((a, b) -> (int) b.getValue()[0] - (int) a.getValue()[0]).limit(SETS_NAMED)
                .forEach(e -> first.add("x" + e.getValue()[0] + " " + e.getKey() + ": " + e.getValue()[1]));
        return new Sets(count[0], count[1], first);
    }

    /** ...with nothing unmended. */
    public Sets redrawSets(Page p) {
        return redrawSets(p, java.util.Set.of());
    }

    /** The most sources redrawSets names. */
    private static final int SETS_NAMED = Integer.getInteger("keptbench.named", 12);

    private static void watchSets(Node n, String what, javafx.beans.value.ObservableValue<?> property, int[] count,
                                  java.util.Map<String, Object[]> sources, java.util.Set<String> unmended, List<Runnable> off) {
        Object[] was = {property.getValue()};
        javafx.beans.InvalidationListener l = o -> {
            Object now = property.getValue();
            String[] at2 = whereSet();
            String where = at2[0];
            if (unmended.contains(at2[1])) {
                count[1]++;
            } else {
                count[0]++;
                Object[] at = sources.computeIfAbsent(where + " " + what, k -> new Object[]{0, describeSet(n) + " ." + what + differ(was[0], now)});
                at[0] = (int) at[0] + 1;
            }
            was[0] = now;
        };
        property.addListener(l);
        off.add(() -> property.removeListener(l));
    }

    /**
     * Where the write that is being counted comes from: {the two nearest lines of the game's own code on the stack that are not the bench's,
     * "Pieces.java:132 < Kept.java:61"; the file of the nearest that is not Kept's, the plumbing every make and every label goes through}.
     */
    private static String[] whereSet() {
        StringBuilder b = new StringBuilder();
        String owner = "";
        int found = 0;
        for (StackTraceElement e : new Throwable().getStackTrace()) {
            if (!e.getClassName().startsWith("ham.citybuildersim") || e.getClassName().startsWith("ham.citybuildersim.ui.KeptBench")) continue;
            if (owner.isEmpty() && !e.getClassName().equals("ham.citybuildersim.ui.Kept")) owner = e.getFileName();
            if (found < 2) b.append(found++ == 0 ? "" : " < ").append(e.getFileName()).append(':').append(e.getLineNumber());
            if (found == 2 && !owner.isEmpty()) break;
        }
        return new String[]{b.length() == 0 ? "(no line of the game's)" : b.toString(), owner};
    }

    /** What changed between two values, as the report writes it: from where they part, each cut. A graphic says only what it was and is. */
    private static String differ(Object was, Object now) {
        String a = String.valueOf(was), b = String.valueOf(now);
        if (was instanceof Node || now instanceof Node) return " " + (was == null ? "none" : was.getClass().getSimpleName()) + " -> "
                + (now == null ? "none" : now.getClass().getSimpleName());
        // From a little before the place they part: a style built on another shows its tail, a text its words.
        int start = 0;
        while (start < a.length() && start < b.length() && a.charAt(start) == b.charAt(start)) start++;
        int at = Math.max(0, start - 12);
        String x = a.substring(at), y = b.substring(at);
        return " [" + (at > 0 ? "..." : "") + (x.length() > 70 ? x.substring(0, 70) + "..." : x) + "] -> ["
                + (at > 0 ? "..." : "") + (y.length() > 70 ? y.substring(0, 70) + "..." : y) + "]";
    }

    /** A node as the report of redrawSets names it: its class, its words (cut) and its first style class. */
    private static String describeSet(Node n) {
        StringBuilder b = new StringBuilder(n.getClass().getSimpleName());
        String words = n instanceof Labeled l ? l.getText() : n instanceof Text t ? t.getText() : null;
        if (words != null) b.append(" \"").append(words.length() > 24 ? words.substring(0, 24) + "..." : words).append('"');
        if (!n.getStyleClass().isEmpty()) b.append(" .").append(n.getStyleClass().get(0));
        return b.toString();
    }

    /**
     * A berth kind's row, as the Ports page draws it (SectorScreen.berthRow in the scope berthScope() names), drawn kept with two berths
     * standing and then with none, and the same row built new with none (KP2b): the signature of each, the kept row's and the new one's,
     * with where layout put them. The city has no Ports page with a kind gone to none, so the rows are drawn from a berth made by hand; the
     * code is the page's own.
     */
    public List<List<String>> berthRowGoesToNone() throws Exception {
        ensureWindow();
        SectorScreen s = ui.sectorScreen;
        FreightView.Quote quoted = new FreightView.Quote(FreightView.Unit.PER_TONNE, 4.2, true, java.util.OptionalDouble.of(.5), java.util.OptionalDouble.of(.7));
        FreightView.Quote unquoted = new FreightView.Quote(FreightView.Unit.PER_TONNE, 0, false, java.util.OptionalDouble.empty(), java.util.OptionalDouble.empty());
        FreightView.Berth standing = new FreightView.Berth(ham.citybuildersim.Ports.Cargo.CONTAINER, 2, 1200, 1200, 1000, .83, 0, 0, 3, 10, .4, 40, 60, quoted);
        FreightView.Berth none = new FreightView.Berth(ham.citybuildersim.Ports.Cargo.CONTAINER, 0, 0, 0, 0, 0, 0, 0, 0, 0, .4, 0, 0, unquoted);
        Kept.Pool pool = new Kept.Pool();
        List<List<String>> out = new ArrayList<>();
        for (Object[] draw : new Object[][]{{pool, standing, false}, {pool, none, true}, {new Kept.Pool(), none, true}}) {
            Node[] row = new Node[1];
            FreightView.Berth b = (FreightView.Berth) draw[1];
            Kept.draw((Kept.Pool) draw[0], () -> row[0] = Kept.scope(SectorScreen.berthScope(b), () -> s.berthRow(b)));
            ui.setPage(row[0]);
            passes();
            if ((boolean) draw[2]) out.add(signature());
        }
        return out;
    }

    /** The nodes the last kept draw of the page made new and handed back: {made, kept}. */
    public int[] counts(Page p) {
        Kept.Pool pool = p.pool.get();
        return new int[]{pool.lastMade(), pool.lastKept()};
    }

    /** The page's top nodes, kept or not: the same objects from one kept draw to the next. */
    public List<Node> pageTop() {
        return new ArrayList<>(column.getChildren());
    }

    /** What the kept page held when a page was built new in its place: its top nodes, and what every box under it held. */
    private List<Node> keptPage = List.of();
    private java.util.Map<Pane, List<Node>> keptBoxes = new java.util.IdentityHashMap<>();

    /**
     * The page built new in place of the kept one (an empty pool, every node new) and laid out, as every redraw was
     * before KP2: {its draw ms, CSS ms, layout ms}. What the page shares with the kept one - the land office's map - is
     * taken into the new page, so every box under the kept page is noted for restoreKeptPage() to put its children back.
     */
    public double[] showFresh(Page p) throws Exception {
        ensureWindow();
        ui.currentScreen = p.screen;
        keptPage = new ArrayList<>(column.getChildren());
        keptBoxes = new java.util.IdentityHashMap<>();
        for (Node n : keptPage) boxesOf(n, keptBoxes);
        double ms = p.draw.applyAsDouble(new Kept.Pool());
        double[] pass = passes();
        return new double[]{ms, pass[0], pass[1]};
    }

    /** The kept page back in the column, every box holding what it held, and laid out. */
    public void restoreKeptPage() throws Exception {
        ui.setPage(keptPage.toArray(new Node[0]));
        for (java.util.Map.Entry<Pane, List<Node>> e : keptBoxes.entrySet()) Kept.set(e.getKey().getChildren(), e.getValue());
        passes();
        release();
    }

    private final WritableImage tiny = new WritableImage(1, 1);
    private final javafx.scene.SnapshotParameters tinyView = new javafx.scene.SnapshotParameters();

    /**
     * What a scene with no window never lets go: a scene is synchronised to its renderer by a pulse, and the nodes that changed wait in its dirty list
     * (strongly held) until one comes - so every page built new and taken off the scene again stayed in memory, about 5 MB a page, until the next
     * snapshot (KP5, slice D: the Bank's reshaping alone built 200 and took the 2 GB a harness JVM has). A snapshot of one pixel does the
     * synchronising and costs nothing to draw.
     */
    public void release() {
        tinyView.setViewport(new javafx.geometry.Rectangle2D(0, 0, 1, 1));
        column.snapshot(tinyView, tiny);
    }

    /** Every box (Pane) under a node and the children it holds now. */
    private static void boxesOf(Node n, java.util.Map<Pane, List<Node>> out) {
        if (n instanceof Pane p) out.put(p, new ArrayList<>(p.getChildren()));
        if (n instanceof Labeled l && l.getGraphic() != null) boxesOf(l.getGraphic(), out);
        if (n instanceof ScrollPane sp && sp.getContent() != null) boxesOf(sp.getContent(), out);
        if (n instanceof Group g) for (Node k : new ArrayList<>(g.getChildren())) boxesOf(k, out);
        if (n instanceof Region r && !(n instanceof Control)) for (Node k : new ArrayList<>(r.getChildrenUnmodifiable())) boxesOf(k, out);
    }

    /** One figure on the page made wrong (see spoil()). */
    public String spoilPage() {
        return spoil(new ArrayList<>(column.getChildren()));
    }

    /** A fold's key put in a set or taken out of it. */
    private static void fold(java.util.Set<String> open, String key, boolean on) {
        if (on) open.add(key);
        else open.remove(key);
    }

    /* ----- the hands on what reshapes the pages ----- */

    /** Infrastructure on one of its pages (and on Freight, a division), as its strip would (the show method normalises the two; the draw does not). */
    public void infraOpen(String page, String division) {
        ui.infrastructureScreen.infraPage = page;
        ui.infrastructureScreen.freightDivision = division;
    }

    /** One of Infrastructure's folds ("roads", "transit", "railway", "freight") opened or closed. */
    public void infraFold(String name, boolean open) {
        fold(ui.infrastructureScreen.detailsOpen, name, open);
    }

    /** Every sub-page of Services as {system, page}, in the strips' order. */
    public static List<String[]> servicesPages() {
        List<String[]> out = new ArrayList<>();
        for (ServicesScreen.ServiceArea area : ServicesScreen.serviceAreas()) {
            for (String page : area.pages()) out.add(new String[]{area.name(), page});
        }
        return out;
    }

    /** Services on one sub-page, as the two strips would. */
    public void servicesOpen(String area, String page) {
        ui.servicesScreen.serviceArea = area;
        ui.servicesScreen.servicePage = page;
    }

    /** One of Services' folds ("health:sick", "books:safety"...) opened or closed. */
    public void servicesFold(String key, boolean open) {
        fold(ui.servicesScreen.detailsOpen, key, open);
    }

    /** The land office's side (0 north to 3 west) picked, as its chip or the map would: no place on it yet. */
    public void landSide(int side) {
        ui.landScreen.side = side;
        ui.landScreen.place = -1;
    }

    /** The offer at a place (0 to 5) on the side shown picked, as its row would. */
    public void landPlace(int place) {
        ui.landScreen.place = place;
    }

    /** The side the office shows. */
    public int landSide() {
        return ui.landScreen.side;
    }

    /** How many offers "Buy the best N" is set to buy (the draw holds it inside what is listed). */
    public void landCount(int n) {
        ui.landScreen.nextCount = n;
    }

    /** The office's "details" fold opened or closed. */
    public void landDetails(boolean open) {
        ui.landScreen.detailsOpen = open;
    }

    /** How the office pays: from the vault (true) or by converting cash (false), as its toggle sets it. */
    public void landPay(boolean fromVault) {
        ui.game.setLandPaidFromVault(fromVault);
    }

    /** Whether the office pays from the vault. */
    public boolean landPaidFromVault() {
        return ui.game.isLandPaidFromVault();
    }

    /** How many offers are listed. */
    public int landListed() {
        return ui.game.landShelf().size();
    }

    /** The first count of the best offers the city cannot pay for as it stands, or null when it can pay for the whole shelf. */
    public List<Integer> landFundingIds() {
        int listed = ui.game.landShelf().size();
        for (int n = 1; n <= listed; n++) {
            List<Integer> ids = ui.game.nextLandParcels(n);
            if (ui.game.landNeedsFunding(ids)) return ids;
        }
        return null;
    }

    /** The best offer bought through the model, as its row's Buy would (the receipt shows until the month turns); why not, or null when it was. */
    public String landBuy() {
        List<Integer> ids = ui.game.nextLandParcels(1);
        if (ids.isEmpty()) return "nothing is listed";
        if (ui.game.landNeedsFunding(ids)) return "the city cannot pay for the best offer";
        return ui.game.buyLandParcels(ids) == 1 ? null : "the best offer was not bought";
    }

    /**
     * Offers bought, the best first through the model (Game.buyLandParcels(), as a row's Buy), until the city cannot pay for the
     * best one as it stands, so that the funding page exists: how many it took, or -1 when the limit came first or nothing was left.
     */
    public int landBuyUntilShort(int limit) {
        for (int i = 0; i < limit; i++) {
            List<Integer> ids = ui.game.nextLandParcels(1);
            if (ids.isEmpty()) return -1;
            if (ui.game.landNeedsFunding(ids)) return i;
            if (ui.game.buyLandParcels(ids) != 1) return -1;
        }
        return -1;
    }

    /** How many sectors wait on ground (the office's waiting card lists them). */
    public int landWaiting() {
        return ui.landScreen.waitingNames().size();
    }

    /** Whether the land office holds a receipt now. */
    public boolean landReceipt() {
        String r = ui.game.getLastLandReceipt();
        return r != null && !r.isEmpty();
    }

    /** Whether the city owns ground on a second landmass, so that the office lists its ground by landmass (a table kept since KP4: SectorScreen.oilTable, whose rows are scopes). */
    public boolean landLandmasses() {
        return ui.landScreen.landmassesTable() != null;
    }

    /* ----- the controls (the rule: a control keeps the player's hand) ----- */

    /** Infrastructure shown, as the rail's tab or a month landing shows it: the real show method (the chrome, then the page kept). */
    public void showInfrastructure() {
        ensureWindow();
        ui.infrastructureScreen.showInfrastructureMenu();
    }

    /** The first slider on the page - Transit's fare - or null. */
    public javafx.scene.control.Slider slider() {
        return first(javafx.scene.control.Slider.class);
    }

    /** Whether the player holds a node (Kept.held). */
    public static boolean held(Node n) {
        return Kept.held(n);
    }

    /** The reading beside a slider, as the latest draw made it (the label the slider's listener writes). */
    public static Label readingOf(javafx.scene.control.Slider s) {
        for (Object v : s.getProperties().values()) if (v instanceof Label l) return l;
        return null;
    }

    /** What the dial's ladder says its thumb shows, as the latest draw built it (Ladder.shown()). */
    public static double shownBy(javafx.scene.control.Slider s) {
        for (Object v : s.getProperties().values()) if (v instanceof Ladder l) return l.shown();
        return Double.NaN;
    }

    /** A primary press on a node, as the pointer's. */
    public static void press(Node n) {
        fireMouse(n, javafx.scene.input.MouseEvent.MOUSE_PRESSED, 5, 5, javafx.scene.input.MouseButton.PRIMARY, 1);
    }

    /** ...and the release (the node's own release handler runs: for the fare dial, it stages the thumb and redraws the screen). */
    public static void release(Node n) {
        fireMouse(n, javafx.scene.input.MouseEvent.MOUSE_RELEASED, 5, 5, javafx.scene.input.MouseButton.PRIMARY, 1);
    }

    /** The staged fare, or NaN when none is. */
    public double stagedFare() {
        return ui.policyScreen.isStaged("fare") ? ui.policyScreen.staged("fare", Double.NaN) : Double.NaN;
    }

    /** The fare put back as it is, as the card's "Leave it as it is" does (it drops the proposal). */
    public void unstageFare() {
        ui.policyScreen.unstage("fare");
    }

    /** The fare staged at a value, as a release on the dial stages it. */
    public void stageFare(double value) {
        ui.policyScreen.stage("fare", value);
    }

    /** The filter box of City History as it is now, or null when the page has none. */
    private javafx.scene.control.TextField filter() {
        return first(javafx.scene.control.TextField.class);
    }

    /**
     * Words typed into City History's filter box and the box given the focus: how it got it - "requestFocus" when the
     * scene took it, "setFocused by reflection" when this scene has no window to give it (a scene with no stage leaves
     * isFocused() false), or why it could not be given.
     */
    public String historyFilterFocus(String text) throws ReflectiveOperationException {
        javafx.scene.control.TextField box = filter();
        if (box == null) return "the page has no filter box";
        box.setText(text);
        box.requestFocus();
        String how = "requestFocus";
        if (!box.isFocused()) {
            Method m = Node.class.getDeclaredMethod("setFocused", boolean.class);
            m.setAccessible(true);
            m.invoke(box, true);
            how = box.isFocused() ? "setFocused by reflection" : "the box would not take the focus";
        }
        // The caret at the end of what was typed, as typing leaves it (taking the focus may have moved it).
        box.positionCaret(text.length());
        return how;
    }

    /** The focus taken off City History's filter box, as another click would. */
    public void historyFilterBlur() throws ReflectiveOperationException {
        javafx.scene.control.TextField box = filter();
        if (box == null) return;
        Method m = Node.class.getDeclaredMethod("setFocused", boolean.class);
        m.setAccessible(true);
        m.invoke(box, false);
    }

    /** City History's filter box: the node, its text, its caret and whether it has the focus. */
    public Object[] historyFilterState() {
        javafx.scene.control.TextField box = filter();
        return box == null ? null : new Object[]{box, box.getText(), box.getCaretPosition(), box.isFocused()};
    }

    /** Kept.text on City History's filter box: the text it holds after. */
    public String historyFilterSet(String text) {
        javafx.scene.control.TextField box = filter();
        Kept.text(box, text);
        return box.getText();
    }

    /* =====================================================================
       BUILD AND THE CONSTRUCTION PAGE (KP3): the Build screen's pages and the construction page, kept and built new,
       the hands on what reshapes them, the controls on them and the pointer over them
       ===================================================================== */

    /** The pages the Build tab has: the Overview, then the categories in the strip's order. */
    public static List<String> buildPages() {
        List<String> out = new ArrayList<>();
        out.add(BuildAdvice.OVERVIEW);
        for (BuildAdvice.Category c : BuildAdvice.categories()) out.add(c.name());
        return out;
    }

    /** Whether a Build page is one of the city's five (rings, and a card on each), else the Overview or a market category. */
    public static boolean buildIsCity(String name) {
        BuildAdvice.Category c = BuildAdvice.category(name);
        return c != null && c.cityBuilds();
    }

    /** The rings a city category has, by their labels in the page's order (none for the Overview and the market's). */
    public static List<String> buildRings(String category) {
        List<String> out = new ArrayList<>();
        for (BuildAdvice.Measure m : BuildAdvice.measuresOf(category)) out.add(m.label());
        return out;
    }

    /**
     * The Build screen as BuildScreen has it: whichever page `buildCategory` names, the Overview or a category, set up as its
     * show method sets it up and drawn by its draw; a page other than the one the pool last drew is the arrival that forgets
     * the pool, as keepBuild() makes it (BuildScreen.arrival(): a page built new has a pool of its own and forgets nothing).
     * It opens on `start` with `orders` (a building's name to the count dialled up on its card), the stat cards of `pins` (building names)
     * pinned open, and no ring picked. The shared state a page leaves on the screen (the order bar, the keyboard's cards) is the last
     * page drawn's: a kept draw sets it right before a stepper is used.
     */
    public Page build(String name, String start, java.util.Map<String, Integer> orders) {
        return build(name, start, orders, java.util.Set.of());
    }

    /** ...with the stat cards of these buildings pinned open. */
    public Page build(String name, String start, java.util.Map<String, Integer> orders, java.util.Set<String> pins) {
        BuildScreen s = ui.buildScreen;
        return new Page(name, "handleAllBuildingMenus", () -> s.pool, pool -> {
            String at = BuildAdvice.category(s.buildCategory) == null ? BuildAdvice.OVERVIEW : s.buildCategory;
            Runnable draw = s.drawOf(at);
            if (pool == s.pool) s.arrival(at);
            return timed(pool, draw);
        }, () -> {
            s.forget();
            s.buildCategory = start;
            s.measurePicked.clear();
            s.orderQty.clear();
            s.orderQty.putAll(orders);
            s.pinnedStats.clear();
            s.pinnedStats.addAll(pins);
        });
    }

    /** The Build screen on a page, as a tile or the strip's chip opens it (the next draw is the arrival). */
    public void buildOpen(String name) {
        ui.buildScreen.buildCategory = name;
    }

    /** The page the Build screen is on. */
    public String buildPage() {
        return ui.buildScreen.buildCategory;
    }

    /** The ring at a place on a city category picked, as its card's click does (the click then draws the page again). */
    public void buildRing(String category, int i) {
        ui.buildScreen.measurePicked.put(category, BuildAdvice.measuresOf(category).get(i));
    }

    /** Automatic building turned on or off through the model, as its switch does. */
    public void buildAuto(boolean on) {
        ui.game.getAutoBuilder().setOn(on, ui.game.getDecisions());
    }

    /** Whether automatic building is on. */
    public boolean buildAuto() {
        return ui.game.getAutoBuilder().isOn();
    }

    /** The names of the buildings with a card on the page as the last draw laid it, in the page's order (the keyboard's list). */
    public List<String> buildCardNames() {
        List<String> out = new ArrayList<>();
        for (BuildScreen.PageCard c : ui.buildScreen.pageCards) out.add(c.template().getName());
        return out;
    }

    /** The card nodes on the page now, in the order the page laid them out. */
    private List<javafx.scene.layout.StackPane> cardNodes() {
        List<javafx.scene.layout.StackPane> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof javafx.scene.layout.StackPane sp && sp.getPrefWidth() == BuildScreen.NEED_CARD) out.add(sp);
        });
        return out;
    }

    /** The first node of a class under a root that passes a test, or null. */
    private static <T extends Node> T find(Node root, Class<T> type, java.util.function.Predicate<T> test) {
        List<T> found = new ArrayList<>();
        each(root, n -> {
            if (found.isEmpty() && type.isInstance(n) && test.test(type.cast(n))) found.add(type.cast(n));
        });
        return found.isEmpty() ? null : found.get(0);
    }

    /** How many cards the page now shows. */
    public int buildCards() {
        return cardNodes().size();
    }

    /** A press on a card's stepper ("+", "+10", "+100", "−" or "↺"), as the pointer's: its order repriced and the order bar refilled in place, outside any draw. False when there is no such card or button. */
    public boolean buildStep(int card, String glyph) {
        List<javafx.scene.layout.StackPane> cards = cardNodes();
        if (card >= cards.size()) return false;
        javafx.scene.control.Button b = find(cards.get(card), javafx.scene.control.Button.class, x -> glyph.equals(x.getText()));
        if (b == null) return false;
        b.fire();
        return true;
    }

    /** The count a building has dialled up on its card. */
    public int buildOrdered(String building) {
        return ui.buildScreen.orderQty.getOrDefault(building, 0);
    }

    /** The count on the face of a card's stepper, as the page shows it now; null when the page has no such card. */
    public String buildCount(int card) {
        List<javafx.scene.layout.StackPane> cards = cardNodes();
        if (card >= cards.size()) return null;
        Label l = find(cards.get(card), Label.class, x -> x.getText() != null && x.getText().matches("[0-9]+") && x.getMinWidth() == 30);
        return l == null ? null : l.getText();
    }

    /** Whether the page shows a label with exactly these words (the order bar's line of the order, "3 × Walk-in Clinic"). */
    public boolean buildBarShows(String words) {
        return find(column, Label.class, x -> words.equals(x.getText())) != null;
    }

    /** A click on a card's (i), as the pointer's: its stat card pinned open, or let go. False when the page has no such card. */
    public boolean buildPin(int card) {
        List<javafx.scene.layout.StackPane> cards = cardNodes();
        if (card >= cards.size()) return false;
        Label info = find(cards.get(card), Label.class, x -> "i".equals(x.getText()) && x.getOnMouseClicked() != null);
        if (info == null) return false;
        fireMouse(info, javafx.scene.input.MouseEvent.MOUSE_CLICKED, 5, 5, javafx.scene.input.MouseButton.PRIMARY, 1);
        return true;
    }

    /** How many times the order bar has been filled since the screen was made (BuildScreen.barFills). */
    public int buildBarFills() {
        return ui.buildScreen.barFills;
    }

    /** Whether the stat card of a building is pinned open. */
    public boolean buildPinned(String building) {
        return ui.buildScreen.pinnedStats.contains(building);
    }

    /**
     * The pointer over the first card's (i): its stat cover shown by the (i)'s own mouse-entered, the page drawn kept again
     * (`redraw`) and the cover looked at; then the pointer gone with its mouse-exited run, the page drawn again; then the pointer
     * gone without one (a pointer that left while the page was being drawn is told nothing) and the page drawn again. Null when
     * card, (i) and cover were the same nodes each time and the cover was shown while the pointer was over the (i) and hidden
     * once it was gone; else what differed. ABSENT when the page has no card.
     */
    public String hoverCover(Runnable redraw) throws Exception {
        List<javafx.scene.layout.StackPane> cards = cardNodes();
        if (cards.isEmpty()) return ABSENT;
        javafx.scene.layout.StackPane card = cards.get(0);
        Label info = find(card, Label.class, x -> "i".equals(x.getText()) && x.getOnMouseEntered() != null);
        Node cover = card.getChildren().size() > 1 ? card.getChildren().get(1) : null;
        if (info == null || cover == null) return "the first card has no (i) and cover to hover";
        if (cover.isVisible()) return "the cover was shown before the pointer came (a pinned stat card)";
        hover(info, true);
        info.getOnMouseEntered().handle(mouse(javafx.scene.input.MouseEvent.MOUSE_ENTERED, 5, 5, javafx.scene.input.MouseButton.NONE, 0));
        if (!cover.isVisible()) return "the (i)'s own mouse-entered did not show the cover";
        redraw.run();
        passes();
        if (cardNodes().isEmpty() || cardNodes().get(0) != card) return "the card under the pointer was handed away or made new by the redraw";
        if (card.getChildren().get(1) != cover || card.getChildren().get(2) != info) return "the cover or the (i) was made new by the redraw";
        if (!cover.isVisible()) return "the pointer over the (i) and the draw hid the cover";
        hover(info, false);
        info.getOnMouseExited().handle(mouse(javafx.scene.input.MouseEvent.MOUSE_EXITED, 5, 5, javafx.scene.input.MouseButton.NONE, 0));
        redraw.run();
        passes();
        if (cover.isVisible()) return "the pointer gone with a mouse-exited, the draw left the cover shown";
        hover(info, true);
        info.getOnMouseEntered().handle(mouse(javafx.scene.input.MouseEvent.MOUSE_ENTERED, 5, 5, javafx.scene.input.MouseButton.NONE, 0));
        hover(info, false);
        redraw.run();
        passes();
        if (card.getChildren().get(1) != cover) return "the cover was made new by the redraw";
        if (cover.isVisible()) return "the pointer gone with no mouse-exited, the draw left the cover shown";
        return null;
    }

    /** The Build screen shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showBuild() {
        ensureWindow();
        BuildScreen s = ui.buildScreen;
        BuildAdvice.Category c = BuildAdvice.category(s.buildCategory);
        if (c == null) s.showOverview();
        else if (c.cityBuilds()) s.showCityCategory(c);
        else s.showMarketCategory(c);
    }

    /** The stopped pages' kinds, by the way the model refuses an order. */
    public static final List<String> STOPPED = List.of("deposit", "coast", "river", "bridge", "licence", "land");

    private static Game.BuildResult refusalOf(String kind) {
        return switch (kind) {
            case "deposit" -> Game.BuildResult.NO_DEPOSIT;
            case "coast" -> Game.BuildResult.NO_COAST;
            case "river" -> Game.BuildResult.NO_RIVER;
            case "bridge" -> Game.BuildResult.NO_BRIDGE;
            case "licence" -> Game.BuildResult.NO_LICENCE;
            case "land" -> Game.BuildResult.NO_LAND;
            default -> throw new IllegalArgumentException(kind);
        };
    }

    /** Whether a building is of the kind that can meet this refusal at all (it names the page): a mine for ore, a plant or terminal for the sea... */
    private boolean namesRefusal(String kind, BuildingsTemplate t) {
        return switch (kind) {
            case "deposit" -> Game.siteOf(t) != null;
            case "coast" -> ham.citybuildersim.Ports.isPort(t) || t.getName().equals("Desalination Plant");
            case "river" -> ham.citybuildersim.BuildCard.hydro(ui.game, t) != null;
            case "bridge" -> t.isBridge();
            case "licence" -> t.getRequiresLicence() != null;
            default -> true;
        };
    }

    /**
     * A building and a count the model refuses for this reason (Game.buildRunStop(), the check an order passes before money),
     * found by trying every building at a few counts: {the building, the count, true}. Where the city has none it is a building
     * that merely names the page (a mine for the deposit, a plant for the coast...) at a count of one: {the building, 1, false};
     * null when no building names it either.
     */
    public Object[] stoppedFor(String kind) {
        Game g = ui.game;
        Game.BuildResult want = refusalOf(kind);
        BuildingsTemplate named = null;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t == null) continue;
            if (named == null && namesRefusal(kind, t)) named = t;
            for (int q : new int[]{1, 100, 10_000}) {
                if (g.buildRunStop(BuildScreen.runOf(t, q)) == want) return new Object[]{t, q, true};
            }
        }
        return named == null ? null : new Object[]{named, 1, false};
    }

    /** The page a build is stopped on, for this kind (STOPPED), this building and count, as it is drawn when the order came from the building's own category. */
    public Page buildStopped(String kind, BuildingsTemplate t, int quantity) {
        BuildScreen s = ui.buildScreen;
        BuildAdvice.Category from = BuildAdvice.categoryOf(t);
        String title = from == null ? BuildAdvice.OVERVIEW : from.name();
        java.util.EnumSet<BuildingType> types = from == null ? java.util.EnumSet.noneOf(BuildingType.class) : from.types();
        String screen = switch (kind) {
            case "deposit" -> "showNoDepositMenu";
            case "coast" -> "showNoCoastMenu";
            case "river" -> "showNoRiverMenu";
            case "bridge" -> "showNoBridgeMenu";
            case "licence" -> "showNoLicenceMenu";
            case "land" -> "showNoLandMenu";
            default -> throw new IllegalArgumentException(kind);
        };
        return new Page("the no-" + kind + " page (" + quantity + " x " + t.getName() + ")", screen, () -> s.pool, pool -> timed(pool, switch (kind) {
            case "deposit" -> () -> s.drawNoDeposit(t, quantity, title, types);
            case "coast" -> () -> s.drawNoCoast(t, quantity, title, types);
            case "river" -> () -> s.drawNoRiver(t, quantity, title, types);
            case "bridge" -> () -> s.drawNoBridge(t, quantity, title, types);
            case "licence" -> () -> s.drawNoLicence(t, quantity, title, types);
            default -> () -> s.drawNoLand(t, quantity, title, types);
        }), s::forget);
    }

    /**
     * A run the city cannot pay for, for the funding page: the first building and count (a few, the smallest first) such that
     * the model lets some of it ahead and the part it funds costs more than the cash; {the building, the count}, or null when
     * the city can pay for every one of them.
     */
    public Object[] fundingRun() {
        Game g = ui.game;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t == null) continue;
            for (int q : new int[]{1, 10, 100, 1_000, 10_000}) {
                java.util.LinkedHashMap<BuildingsTemplate, Integer> run = BuildScreen.runOf(t, q);
                int ahead = g.buildRunAhead(run);
                if (ahead > 0 && g.buildFundingGap(BuildScreen.firstOf(run, ahead)) > 0) return new Object[]{t, q};
            }
        }
        return null;
    }

    /** The build funding page for a run of one order (the run must have a gap: fundingRun() finds one). */
    public Page buildFunding(BuildingsTemplate t, int quantity) {
        BuildScreen s = ui.buildScreen;
        BuildAdvice.Category from = BuildAdvice.categoryOf(t);
        BuildScreen.RunFrom where = new BuildScreen.RunFrom(from == null ? BuildAdvice.OVERVIEW : from.name(),
                from == null ? java.util.EnumSet.noneOf(BuildingType.class) : from.types(), true);
        return new Page("the build funding page (" + quantity + " x " + t.getName() + ")", "showBuildFunding", () -> s.pool, pool -> {
            s.fundChips.noteFocus();
            return timed(pool, () -> s.drawFunding(BuildScreen.runOf(t, quantity), where));
        }, () -> {
            s.forget();
            s.fundCap = null;
        });
    }

    /** The page for when the loan went through and an order was still not built: `built` of one building and `left` of another, the loan `paper` ("20-year bond"). */
    public Page buildFellShort(BuildingsTemplate built, int builtCount, BuildingsTemplate left, int leftCount, String paper) {
        BuildScreen s = ui.buildScreen;
        BuildAdvice.Category from = BuildAdvice.categoryOf(left);
        BuildScreen.RunFrom where = new BuildScreen.RunFrom(from == null ? BuildAdvice.OVERVIEW : from.name(),
                from == null ? java.util.EnumSet.noneOf(BuildingType.class) : from.types(), true);
        return new Page("the build fell-short page (" + (builtCount > 0 ? builtCount + " x " + built.getName() + " built, " : "none built, ")
                + leftCount + " x " + left.getName() + " left)", "showBuildFellShort", () -> s.pool, pool -> timed(pool, () -> s.drawFellShort(
                builtCount > 0 ? BuildScreen.runOf(built, builtCount) : new java.util.LinkedHashMap<>(),
                BuildScreen.runOf(left, leftCount), where, paper)), s::forget);
    }

    /* ----- the construction page ----- */

    /** The construction page, on the tab the screen is on; it opens on Sites with nothing picked and the find box empty. */
    public Page construction() {
        return construction("Sites");
    }

    /** ...opening on a tab ("Sites", "Timeline" or "Demolish"), with nothing picked and the find box empty. */
    public Page construction(String opensOn) {
        ConstructionScreen s = ui.constructionScreen;
        return new Page(opensOn.equals("Sites") ? "the construction page" : "the construction page, " + opensOn + " tab", ConstructionScreen.SCREEN,
                () -> s.pool, pool -> timed(pool, s::drawPage), () -> {
            s.forget();
            s.tab = opensOn;
            s.focus = null;
            s.pick = null;
            s.pickShell = false;
            s.count = 1;
            s.filter = "";
        });
    }

    /** The construction page on a tab ("Sites", "Timeline" or "Demolish"), as its tab chip opens it (the next draw is the page). */
    public void constructionTab(String tab) {
        ui.constructionScreen.tab = tab;
        ui.constructionScreen.focus = null;
    }

    /** A building on the Demolish tab picked, as its row's click picks it, and how many of it to bring down. */
    public void constructionPick(String name, boolean shell, int count) {
        ConstructionScreen s = ui.constructionScreen;
        s.pick = name;
        s.pickShell = shell;
        s.count = count;
    }

    /** The screen's own find filter set, as a change that is not the player's typing (the box is then set by the next draw, never while it has the focus). */
    public void constructionFilter(String filter) {
        ui.constructionScreen.filter = filter;
    }

    /** The first building the city may demolish (buyOut false) or must buy out first (buyOut true) and has standing, by name; null when it has none. */
    public String constructionPickable(boolean buyOut) {
        Game g = ui.game;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t == null || g.demolishable(t) <= 0) continue;
            if (buyOut ? g.isBuyOutable(t) && !g.isCitysToDemolish(t) : g.isCitysToDemolish(t)) return t.getName();
        }
        return null;
    }

    /** The construction page shown through its real show method, on its tab (the chrome, then the page kept). */
    public void showConstruction() {
        ensureWindow();
        ui.constructionScreen.show();
    }

    /** How many sites the city has on (the rows of the Sites tab and the bars of the timeline). */
    public int sitesOn() {
        return ui.game.getBuildingManager().siteKeysOnSite().size();
    }

    /* ----- the first text field on a page: the yield box on the Overview, the find box on Demolish ----- */

    /** Text typed into the page's first text field and the box given the focus (see historyFilterFocus()): how it got the focus. */
    public String fieldFocus(String text) throws ReflectiveOperationException {
        return historyFilterFocus(text);
    }

    /** The focus taken off the page's first text field. */
    public void fieldBlur() throws ReflectiveOperationException {
        historyFilterBlur();
    }

    /** The page's first text field: the node, its text, its caret and whether it has the focus; null when it has none. */
    public Object[] fieldState() {
        return historyFilterState();
    }

    /** Text typed into the page's first text field as keys would, its listener running (outside a draw); the text it holds after. */
    public String fieldType(String text) {
        javafx.scene.control.TextField box = filter();
        box.setText(text);
        return box.getText();
    }

    /** Kept.text on the page's first text field: the text it holds after. */
    public String fieldSet(String text) {
        return historyFilterSet(text);
    }

    /* ----- the pointer over what a hover styles ----- */

    /** What hoverKept() says when the page has no node of the kind asked for. */
    public static final String ABSENT = "absent";

    /**
     * The first node of a kind on the page, or null: "rowGoes" (City History's row that moves the chart), "door"
     * (Pieces.door: words and a "›"), "limitCell" (a vitals cell that is a door), "chartCardHead" (a chart card's name,
     * a click on it opens the chart), "landRow" (an offer's row), "oreCard" (the land office's ore card), "doorCard"
     * (Services' card made a door), and since KP3 Build's "jobTile" (the Overview's tile for a city category), "marketTile" (its row's tile for a market
     * category) and "ringCard" (a small ring card that is not the one picked, on a category with many rings: Education's), and since KP4 the
     * Sectors list's "sectorCard" (a business's card), since KP5 the money screens' "bankCard", "rungName", "financeCard", "bookRow" and "holdingRow",
     * and since KP6 "ringKeyRow", "bridgeRow", "rankRow", "mirrorGround" and "policyRow".
     */
    public Node hoverTarget(String kind) {
        List<Node> found = new ArrayList<>();
        each(column, n -> {
            if (!found.isEmpty() || n.getOnMouseEntered() == null || n.getOnMouseExited() == null) return;
            String style = n.getStyle() == null ? "" : n.getStyle();
            boolean it = switch (kind) {
                case "rowGoes" -> n instanceof Pane && style.contains("-fx-padding: 4 6 4 6; -fx-background-radius: 6");
                case "door" -> n instanceof Label l && l.getText() != null && l.getText().endsWith(" ›") && style.contains("-fx-cursor: hand");
                case "limitCell" -> n instanceof VBox && n.getOnMouseClicked() != null && style.contains("-fx-padding: 0 14 0 14");
                case "chartCardHead" -> n instanceof Label && n.getParent() instanceof Pieces.CardHead h && h.name == n;
                case "landRow" -> n instanceof Pane && style.contains("-fx-padding: 0 6 0 6; -fx-background-radius: 4");
                case "oreCard" -> n instanceof Pane && Kept.tipOf(n) != null && Kept.tipOf(n).getText().startsWith("Mines are built under Build");
                case "doorCard" -> n instanceof VBox && n.getOnMouseClicked() != null && style.contains("-fx-background-radius: 8")
                        && style.contains("-fx-cursor: hand");
                case "jobTile" -> n instanceof VBox && n.getOnMouseClicked() != null && style.contains("-fx-border-width: 3 1 1 1");
                case "marketTile" -> n instanceof HBox && n.getOnMouseClicked() != null && style.contains("-fx-padding: 8 8 8 8; -fx-background-color: " + Palette.PANEL);
                case "ringCard" -> n instanceof HBox && n.getOnMouseClicked() != null && style.contains("-fx-padding: 8 8 8 8; -fx-background-radius: 8")
                        && style.contains("-fx-background-color: " + Palette.RAISED);
                case "sectorCard" -> n instanceof VBox && n.getOnMouseClicked() != null
                        && style.contains("-fx-padding: 12; -fx-cursor: hand; -fx-background-color: " + Palette.RAISED);
                // KP5: the Bank's door cards (BankScreen.doorCard), a rung's name on the rate ladder (a door with " ›", in a row of the ladder),
                // Finances' area and issuer cards and the book's rows, and the fund's holding, issuer and result rows.
                case "bankCard" -> n instanceof VBox && n.getOnMouseClicked() != null
                        && style.contains("-fx-padding: 12; -fx-background-color: " + Palette.RAISED) && style.contains("-fx-cursor: hand");
                case "rungName" -> n instanceof Label l && l.getText() != null && l.getText().endsWith(" ›") && style.contains("-fx-cursor: hand")
                        && l.getParent() instanceof HBox named && named.getParent() instanceof HBox row && row.getMinHeight() == Pieces.RUNG_ROW;
                case "financeCard" -> n instanceof VBox && n.getOnMouseClicked() != null
                        && style.contains("-fx-padding: 10 12 12 12; -fx-background-color: " + Palette.RAISED) && style.contains("-fx-cursor: hand");
                case "bookRow" -> n instanceof HBox && n.getOnMouseClicked() != null
                        && style.contains("-fx-padding: 2 4 2 4; -fx-background-radius: 4; -fx-cursor: hand");
                case "holdingRow" -> n instanceof HBox && n.getOnMouseClicked() != null
                        && style.contains("-fx-background-radius: 6; -fx-cursor: hand");
                // KP6: the key rows of the Government tab's split ring (Pieces.ringKey), its bridge's rows (Pieces.bridgeRow), the rows of its ranked bars
                // (Pieces.rankBars, which carry their line's key), the grounds of Trade's mirror rows (Pieces.MirrorRows), and the tall rows of Policy that
                // are clicked (a payer's, a course's, a zone's).
                case "ringKeyRow" -> n instanceof HBox && n.getOnMouseClicked() != null
                        && style.contains("-fx-padding: 1 4 1 4; -fx-background-radius: 4; -fx-cursor: hand");
                case "bridgeRow" -> n instanceof HBox && n.getOnMouseClicked() != null && style.startsWith("-fx-background-radius: 4; -fx-cursor: hand");
                case "rankRow" -> n instanceof HBox && n.getOnMouseClicked() != null && n.getProperties().containsKey(Pieces.RANK_KEY);
                case "mirrorGround" -> n instanceof Region && !(n instanceof Pane) && n.getCursor() == javafx.scene.Cursor.HAND
                        && n.getOnMouseClicked() != null && style.startsWith("-fx-background-radius: 4;");
                case "policyRow" -> n instanceof HBox && n.getOnMouseClicked() != null && !n.getProperties().containsKey(Pieces.RANK_KEY)
                        && style.contains("-fx-padding: 4 6 4 6; -fx-background-radius: 6;") && style.contains("-fx-cursor: hand");
                default -> throw new IllegalArgumentException(kind);
            };
            if (it) found.add(n);
        });
        return found.isEmpty() ? null : found.get(0);
    }

    /**
     * The pointer put over the first node of a kind, the page drawn kept again (`redraw`), and the node looked at; then the
     * pointer taken off and the page drawn again. Null when each time the node was the same node, in the look its own
     * handler gives (read its style, run the handler, read it again: unchanged) and the two looks differ; else what
     * differed. ABSENT when the page has no such node.
     */
    public String hoverKept(String kind, Runnable redraw) throws Exception {
        Node n = hoverTarget(kind);
        if (n == null) return ABSENT;
        hover(n, true);
        redraw.run();
        passes();
        if (hoverTarget(kind) != n) return "the node under the pointer was handed away or made new by the redraw";
        String over = n.getStyle();
        n.getOnMouseEntered().handle(mouse(javafx.scene.input.MouseEvent.MOUSE_ENTERED, 5, 5, javafx.scene.input.MouseButton.NONE, 0));
        if (!java.util.Objects.equals(over, n.getStyle())) return "under the pointer the draw left " + clip(over) + " where its own mouse-entered sets " + clip(n.getStyle());
        hover(n, false);
        redraw.run();
        passes();
        if (hoverTarget(kind) != n) return "the node the pointer left was handed away or made new by the redraw";
        String rest = n.getStyle();
        n.getOnMouseExited().handle(mouse(javafx.scene.input.MouseEvent.MOUSE_EXITED, 5, 5, javafx.scene.input.MouseButton.NONE, 0));
        if (!java.util.Objects.equals(rest, n.getStyle())) return "off the pointer the draw left " + clip(rest) + " where its own mouse-exited sets " + clip(n.getStyle());
        if (java.util.Objects.equals(over, rest)) return "the hover changes nothing in the node's style, so there is no look to keep";
        return null;
    }

    /**
     * The pointer put over the crumb on a business's page ("Sectors ›", whose hover is its tooltip), the page drawn kept again
     * (`redraw`), and the crumb looked at; then the pointer taken off and the page drawn again. Null when each time the crumb was the
     * same node and its tooltip the same one, with the same words, still under the pointer in the first case and not in the second;
     * else what differed. ABSENT when the page has no crumb.
     */
    public String hoverCrumb(Runnable redraw) throws Exception {
        Node crumb = crumb();
        if (crumb == null) return ABSENT;
        Tooltip tip = Kept.tipOf(crumb);
        if (tip == null) return "the crumb has no tooltip to keep";
        String words = tip.getText();
        hover(crumb, true);
        redraw.run();
        passes();
        if (crumb() != crumb) return "the crumb under the pointer was handed away or made new by the redraw";
        if (!crumb.isHover()) return "the pointer over the crumb was lost by the redraw";
        if (Kept.tipOf(crumb) != tip) return "the crumb's tooltip was made new by the redraw";
        if (!java.util.Objects.equals(words, tip.getText())) return "the crumb's tooltip's words changed to " + clip(tip.getText());
        hover(crumb, false);
        redraw.run();
        passes();
        if (crumb() != crumb) return "the crumb the pointer left was handed away or made new by the redraw";
        if (crumb.isHover()) return "the pointer gone, the redraw put it back over the crumb";
        if (Kept.tipOf(crumb) != tip) return "the crumb's tooltip was made new by the redraw";
        return null;
    }

    /** The crumb on a business's page: the label that goes back to the list. */
    private Node crumb() {
        return find(column, Label.class, l -> "Sectors".equals(l.getText()) && l.getGraphic() != null && Kept.tipOf(l) != null);
    }

    private static String clip(String s) {
        return s == null ? "null" : s.length() > 120 ? s.substring(0, 120) + "..." : s;
    }

    /* =====================================================================
       THE BANK (KP5): the Overview and the six pages behind it, in both views, kept and built new, the hands on what
       reshapes them (a page and view picked, a line or a fold opened, the waterfall's year) and the real show method
       ===================================================================== */

    /** The Bank tab on its Overview: nothing opened, the waterfall on this month, the Summary view. */
    public Page bank() {
        return bank("the Bank Overview", null, null, false);
    }

    /**
     * The Bank tab as a Page, as the screen draws it from its pool (the Overview when `area` is null, else the page `page` names
     * behind it, in the Summary or the Statement view - which only Profit and Balance sheet have): it opens with nothing folded
     * open and the waterfall on this month.
     */
    public Page bank(String name, String area, String page, boolean statement) {
        BankScreen s = ui.bankScreen;
        return new Page(name, "showBankMenu", () -> s.pool, pool -> timed(pool, s::drawPage), () -> {
            s.forget();
            s.openLines.clear();
            s.profitYear = false;
            bankOpen(area, page, statement);
        });
    }

    /** The six pages behind the Overview, in the chip strip's order. */
    public static List<String> bankPages() {
        return List.of(BankScreen.BANK_PAGE_NAMES);
    }

    /** The keys of the six pages' "details" folds, in the pages' order. */
    public static List<String> bankFolds() {
        return List.of("profit:statement", "sheet:statement", "lending:tables", "lending:bonds", "funding:lines", "capital:lines");
    }

    /** The screen on its Overview (`area` null), or on a page behind it in a view, as a chip or the switch would put it (the next draw shows it). */
    public void bankOpen(String area, String page, boolean statement) {
        BankScreen s = ui.bankScreen;
        s.bankArea = area == null ? null : BankScreen.BANK_PAGES;
        s.bankPage = page == null ? BankScreen.BANK_HOME : page;
        ui.sectorScreen.statementView = statement;
    }

    /** A fold ("details"), a statement line or a borrower's sectors opened or closed, by its key, as a click on it would. */
    public void bankLine(String key, boolean open) {
        fold(ui.bankScreen.openLines, key, open);
    }

    /** The Profit page's waterfall on the last twelve months or on this month, as its chip would put it. */
    public void bankYear(boolean year) {
        ui.bankScreen.profitYear = year;
    }

    /** The Bank tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showBank() {
        ensureWindow();
        ui.bankScreen.showBankMenu();
    }

    /** The Bank tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. */
    public void forgetBank() {
        ui.bankScreen.forget();
    }

    /* =====================================================================
       FINANCES (KP5): the hub and the six areas' pages, kept and built new, the hands on what reshapes them (an area and a
       page picked, a line's page and Close it now opened for a piece and closed, a fold, what is typed in the boxes) and the
       real show method
       ===================================================================== */

    /** The Finances tab on its hub: nothing opened, nothing folded open. */
    public Page finances() {
        return finances("the Finances hub", null, null);
    }

    /**
     * The Finances tab as a Page, as the screen draws it from its pool (the hub when `area` is null, else the area and the page `page`
     * names, or its first page when `page` is null): it opens with nothing folded open, no line's page and no Close it now open, the
     * boxes empty.
     */
    public Page finances(String name, String area, String page) {
        FinancesScreen s = ui.financesScreen;
        return new Page(name, "showFinanceMenu", () -> s.pool, pool -> timed(pool, s::drawPage), () -> {
            s.forget();
            s.open.clear();
            s.lineOpen = null;
            s.closing = null;
            s.closeFromLine = false;
            s.closeFaceText = "";
            s.closeMaxText = "";
            s.closeFaceField = null;
            s.closeMaxField = null;
            financesOpen(area, page);
        });
    }

    /** The six areas, in the hub's order. */
    public static List<String> financeAreas() {
        return List.of(FinancesScreen.AREAS);
    }

    /** An area's pages, in its chip strip's order (the book's one page, Money's one, the fund's four). */
    public static List<String> financePages(String area) {
        return List.of(FinancesScreen.pagesOf(area));
    }

    /** The keys of the "details" folds on the area's pages: the bond market's two (every bond as tables, a bond's bids and asks). */
    public static List<String> financeFolds() {
        return List.of("bonds:grids", "bond:levels");
    }

    /**
     * The screen on its hub (`area` null), or on an area and a page as a chip or a door would put it (the next draw shows it): the
     * fund on no security, no line's page and no Close it now open.
     */
    public void financesOpen(String area, String page) {
        FinancesScreen s = ui.financesScreen;
        s.financeArea = area;
        s.financePage = page == null ? FinancesScreen.FINANCE_HOME : page;
        s.fundScreen.security = null;
    }

    /** The pieces of paper on the city's books, as the book lists them (the desk sorts them by when they fall due). */
    public List<ham.citybuildersim.Debt> financesPieces() {
        return new ArrayList<>(ui.game.getDebtManager().getDebt());
    }

    /** A piece's own page opened under The book, as its desk row would (the next draw shows it); null puts the book back. */
    public void financesLine(ham.citybuildersim.Debt piece) {
        FinancesScreen s = ui.financesScreen;
        s.financeArea = "The book";
        s.fundScreen.security = null;
        if (piece == null) {
            s.lineOpen = null;
            s.financePage = FinancesScreen.BOOK_PAGES[0];
        } else {
            s.lineOpen = piece;
            s.closeFromLine = false;
            s.financePage = FinancesScreen.LINE_PAGE;
        }
    }

    /**
     * Close it now opened on a piece, as its Close it now button would (the next draw shows it): its two boxes empty, and the back
     * door to the line's page when that was open on the same piece.
     */
    public void financesClose(ham.citybuildersim.Debt piece) {
        FinancesScreen s = ui.financesScreen;
        s.closeFromLine = s.lineOpenNow() && s.lineOpen == piece;
        s.closing = piece;
        s.closeFaceText = "";
        s.closeMaxText = "";
        s.financeArea = "The book";
        s.fundScreen.security = null;
        s.financePage = FinancesScreen.CLOSE_PAGE;
    }

    /** What is typed in Close it now's two boxes (1 the face to buy back, 2 the most to pay), as the page keeps it: the next draw puts it in the box. */
    public void financesTyped(int which, String text) {
        if (which == 1) ui.financesScreen.closeFaceText = text;
        else ui.financesScreen.closeMaxText = text;
    }

    /** Close it now's two boxes as the last draw made them (null before one has drawn them). */
    public javafx.scene.control.TextField[] financesCloseBoxes() {
        return new javafx.scene.control.TextField[] {ui.financesScreen.closeFaceField, ui.financesScreen.closeMaxField};
    }

    /** A "details" fold on a Finances page opened or closed, by its key, as a click on it would. */
    public void financesFold(String key, boolean open) {
        fold(ui.financesScreen.open, key, open);
    }

    /** The Finances tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showFinances() {
        ensureWindow();
        ui.financesScreen.showFinanceMenu();
    }

    /** The Finances tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. */
    public void forgetFinances() {
        ui.financesScreen.forget();
    }

    /* ----- Borrow, the bond market's book, the city's fund (KP5, slice C) ----- */

    /** The kinds of paper Borrow offers, by key, in its tiles' order. */
    public static List<String> borrowKinds() {
        List<String> out = new ArrayList<>();
        for (FinancesScreen.Instrument kit : FinancesScreen.INSTRUMENTS) out.add(kit.key());
        return out;
    }

    /** The terms a kind is sold at (a bill's in months, a bond's in years). */
    public static List<Integer> borrowTerms(String kind) {
        List<Integer> out = new ArrayList<>();
        for (int t : FinancesScreen.instrument(kind).terms()) out.add(t);
        return out;
    }

    /** The eight moneys the paper abroad may be asked in, by code. */
    public static List<String> borrowMoneys() {
        return new ArrayList<>(ham.citybuildersim.WorldMoneys.EIGHT);
    }

    /**
     * Borrow's choices as its tiles, columns, chips and switches would set them (the next draw shows them): the kind of paper, its term,
     * the money it is asked in (abroad), the ask (0: none), whether a bond is asked callable and whether the dollars are held; the
     * typed box empty and its refusal gone.
     */
    public void financesBorrow(String kind, int term, String money, double ask, boolean callable, boolean hold) {
        FinancesScreen s = ui.financesScreen;
        s.borrowType = kind;
        s.borrowTerm = term;
        s.borrowMoney = money;
        s.borrowAsk = ask;
        s.borrowCallable = callable;
        s.borrowHold = hold;
        s.askTyped = null;
        s.askRefused = null;
    }

    /** What is typed in Borrow's ask box and not yet set, over the ask as it stands (null: nothing typed), and what the box said of an entry it could not read. */
    public void financesAskTyped(String typed, String refused) {
        FinancesScreen s = ui.financesScreen;
        s.askTyped = typed;
        s.askTypedOver = s.borrowAsk;
        s.askRefused = refused;
    }

    /** Borrow's ask box as the last draw made it (null before one has). */
    public javafx.scene.control.TextField financesAskBox() {
        return ui.financesScreen.askField;
    }

    /** The highest yield the sale will take, as its chips would set it. */
    public void financesCap(boolean fill, double value) {
        ui.financesScreen.borrowCap = fill ? Auction.Cap.FILL : Auction.Cap.at(value);
    }

    /** The numbers of the businesses' bonds outstanding, the largest first (the bond market's book names one). */
    public List<Integer> bondIds() {
        List<ham.citybuildersim.CorporateBond> bonds = new ArrayList<>(ui.game.getBondMarket().getBonds());
        bonds.sort((a, b) -> Double.compare(b.face(), a.face()));
        List<Integer> out = new ArrayList<>();
        for (ham.citybuildersim.CorporateBond b : bonds) out.add(b.id());
        return out;
    }

    /** A bond's book opened, as a click on its issuer's card would (the next draw shows it). */
    public void financesBondBook(int id) {
        FinancesScreen s = ui.financesScreen;
        s.bookBondId = id;
        s.financeArea = "The bond market";
        s.financePage = "A bond's book";
        s.fundScreen.security = null;
    }

    /** The securities the fund's Search lists with nothing typed, by lot key (the shares by value, then the bonds): the pages it may open. */
    public List<String> fundSecurities() {
        List<String> out = new ArrayList<>();
        for (FundScreen.Found f : FundScreen.found(ui.game, "", "All")) if (!out.contains(f.key())) out.add(f.key());
        for (FundScreen.Holding h : FundScreen.holdings(ui.game)) if (!out.contains(h.key())) out.add(h.key());
        return out;
    }

    /** The lots the fund holds, by key. */
    public List<String> fundHoldings() {
        List<String> out = new ArrayList<>();
        for (FundScreen.Holding h : FundScreen.holdings(ui.game)) out.add(h.key());
        return out;
    }

    /** The fund on one of its four pages (the next draw shows it): no security open. */
    public void fundPage(String page) {
        FinancesScreen s = ui.financesScreen;
        s.financeArea = FundScreen.AREA;
        s.financePage = page;
        s.fundScreen.security = null;
    }

    /** A security's page opened by its lot key, as a row would (the next draw shows it), from the page named; the ticket for it fresh. */
    public void fundOpen(String key, String from) {
        FinancesScreen s = ui.financesScreen;
        FundScreen f = s.fundScreen;
        s.financeArea = FundScreen.AREA;
        s.financePage = from;
        f.cameFrom = from;
        f.security = key;
        f.resetTicket(key);
    }

    /** The ticket on the security open set as its chips, steps and press would: buy or sell, by amount or by quantity, the figure, the price's kind and steps, and whether it is at its review. */
    public void fundTicket(boolean buy, boolean byAmount, double amount, String priceKind, int steps, boolean review) {
        FundScreen f = ui.financesScreen.fundScreen;
        f.buy = buy;
        f.byAmount = byAmount;
        f.amount = amount;
        f.priceKind = priceKind;
        f.steps = steps;
        f.review = review;
    }

    /** The ticket's price kinds, in the chips' words: fair, bid, ask, last and the player's own. */
    public static List<String> fundPriceKinds() {
        return List.of(FundScreen.FAIR, FundScreen.BID, FundScreen.ASK, FundScreen.LAST, FundScreen.OWN);
    }

    /** Search's words and chip, as the box and the chips would set them (the next draw shows them). */
    public void fundSearch(String query, String filter) {
        FundScreen f = ui.financesScreen.fundScreen;
        f.query = query;
        f.filter = filter;
    }

    /** Search's chips. */
    public static List<String> fundFilters() {
        return List.of(FundScreen.FILTERS);
    }

    /** Activity's chip and how many months it shows. */
    public void fundActivity(String group, int months) {
        FundScreen f = ui.financesScreen.fundScreen;
        f.group = group;
        f.activityMonths = months;
    }

    /** Activity's chips. */
    public static List<String> fundGroups() {
        return List.of(FundScreen.GROUPS);
    }

    /** The fund's search box as the last draw made it (null before one has). */
    public javafx.scene.control.TextField fundSearchBox() {
        return ui.financesScreen.fundScreen.box;
    }

    /** A fold or an opened row of the fund, by its key ("fund:closed", "fund:life", "fund:issuer:...", "fund:small:...", "fund:act:..."), as a click on it would. */
    public void fundFold(String key, boolean open) {
        fold(ui.financesScreen.fundScreen.open, key, open);
    }

    /** The keys of the fund's folds and opened rows that exist before anything is held: the closed lots' and the Rules & cash page's. */
    public static List<String> fundFolds() {
        return List.of("fund:closed", "fund:life");
    }

    /** What the fund's PAY IN, DRAW OUT asks for, as its steps would set it. */
    public void fundAsk(double ask) {
        ui.financesScreen.fundAsk = ask;
    }

    /**
     * Whether the fund's tab was on show before the next draw, as the show method tells it (FinancesScreen sets it): only then does the
     * worth count on and a holding's payment pop when the month has moved, and (UserInterface.countsUp()) at a speed a figure can follow.
     */
    public void fundOnShow(boolean here) {
        ui.financesScreen.fundScreen.here = here;
    }

    /** What the fund has opened so far (folds, grouped lines, bond issuers), for the harness to open every one of them. */
    public List<String> fundOpenRows() {
        return new ArrayList<>(ui.financesScreen.fundScreen.open);
    }

    /* ----- the foreign default menu and the debt result menu (KP5, slice C) ----- */

    /** The foreign default menu: the page that asks twice before the city walks away from what it owes abroad. */
    public Page foreignDefault() {
        FinancesScreen s = ui.financesScreen;
        return new Page("the foreign default menu", "showForeignDefaultMenu", () -> s.pool, pool -> timed(pool, s::drawForeignDefault), s::forget);
    }

    /** What the last booking the bench made quoted and said: the receipt's two inputs. */
    private ham.citybuildersim.DebtQuote bookedQuote;
    private String bookedSummary, bookedMoney;
    private boolean bookedForeign;

    /**
     * Paper sold as the Borrow page's own button sells it, through the same calls (the quote struck on the city as it stands, then the
     * booking the quote's card hands it to): the receipt's inputs are kept for the debt result menu to draw. Returns what the booking
     * said. `foreign` sells in `money` abroad, else at home at the default highest yield.
     */
    public String financesBook(String kind, int term, double ask, boolean foreign, String money) {
        FinancesScreen s = ui.financesScreen;
        Game g = ui.game;
        FinancesScreen.Instrument kit = FinancesScreen.instrument(kind);
        Auction.Cap cap = Auction.Cap.DEFAULT;
        bookedQuote = foreign ? g.quoteForeign(kit.key(), money, ask, term, kit.rounding())
                : g.quoteDebt(kit.key(), ask, term, kit.rounding(), cap);
        bookedSummary = foreign ? g.handleForeignLogic(kit.key(), money, ask, term, kit.rounding(), false)
                : s.executeDebtLogic(kit.key(), ask, term, kit.rounding(), cap);
        bookedForeign = foreign;
        bookedMoney = money;
        return bookedSummary;
    }

    /** The receipt for nothing issued: no quote, the booking's words only (null: the page's own "Nothing issued."). */
    public void financesBookedNothing(String summary) {
        bookedQuote = null;
        bookedSummary = summary;
        bookedForeign = false;
        bookedMoney = null;
    }

    /** The debt result menu, drawn on what financesBook() last quoted and said. */
    public Page debtResult() {
        FinancesScreen s = ui.financesScreen;
        return new Page("the debt result menu", "showDebtResultMenu", () -> s.pool,
                pool -> timed(pool, () -> s.drawDebtResult(bookedQuote, bookedForeign, bookedMoney, bookedSummary)), s::forget);
    }

    /* =====================================================================
       THE MONEY SCREENS' HANDS (KP5, slice D): what KeptCheck's sections 55 to 68 reach that the slices' hands did not - the
       scroller a chip lands at the top of, paper sold of every kind, the boxes given the focus and the chips pressed under
       it, the pictures and how often they draw, the animations a month starts on the page, and the nodes a pointer rests on
       ===================================================================== */

    /** The page's scroller (a Bank, Finances or fund page is a frame over a scroller), or null. */
    public ScrollPane pageScroller() {
        for (Node n : column.getChildren()) if (n instanceof ScrollPane sp) return sp;
        return null;
    }

    /** A chip of the Bank tab picked, as a click on it would: "Overview" or one of the six pages, at its top (BankScreen.pick). */
    public void bankPick(String chip) {
        ui.bankScreen.pick(chip);
    }

    /** The Bank's Summary | Statement switch picked, as a click on it would (BankScreen.pickView). */
    public void bankPickView(boolean statement) {
        ui.bankScreen.pickView(statement);
    }

    /** A chip of the Finances tab picked, as a click on it would: the area's page at its top (FinancesScreen.open, no anchor). */
    public void financesPick(String area, String page) {
        ui.financesScreen.open(area, page, null);
    }

    /**
     * Paper sold at home as the Borrow page's own button sells it (the same executeDebtLogic call, at the default highest yield):
     * what the booking said. A callable bond is `callable` on a Term. Nothing is kept for the debt result menu (financesBook does that).
     */
    public String financesSell(String kind, int term, double ask, boolean callable) {
        FinancesScreen.Instrument kit = FinancesScreen.instrument(kind);
        return ui.financesScreen.executeDebtLogic(kit.key(), ask, term, kit.rounding(), Auction.Cap.DEFAULT, callable);
    }

    /* ----- the boxes, given the focus and pressed under ----- */

    /** Every text field on the page, in the page's order. */
    public List<javafx.scene.control.TextField> textFields() {
        List<javafx.scene.control.TextField> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof javafx.scene.control.TextField f) out.add(f);
        });
        return out;
    }

    /** The yield box of a cap's chips ("type a yield, 4.5": the rollover's on the hub, Borrow's at home) on the page now, or null. */
    public javafx.scene.control.TextField yieldBox() {
        return find(column, javafx.scene.control.TextField.class, t -> t.getPromptText() != null && t.getPromptText().startsWith("type a yield"));
    }

    /**
     * Words typed into a box and the box given the focus (see historyFilterFocus()): how it got it - "requestFocus" when the scene
     * took it, "setFocused by reflection" when this scene has no window to give it, or why it could not be given.
     */
    public static String boxFocus(javafx.scene.control.TextField box, String text) throws ReflectiveOperationException {
        if (box == null) return "the page has no such box";
        box.setText(text);
        box.requestFocus();
        String how = "requestFocus";
        if (!box.isFocused()) {
            Method m = Node.class.getDeclaredMethod("setFocused", boolean.class);
            m.setAccessible(true);
            m.invoke(box, true);
            how = box.isFocused() ? "setFocused by reflection" : "the box would not take the focus";
        }
        // The caret at the end of what was typed, as typing leaves it (taking the focus may have moved it).
        box.positionCaret(text.length());
        return how;
    }

    /** The focus taken off a box, as another click would. */
    public static void boxBlur(javafx.scene.control.TextField box) throws ReflectiveOperationException {
        Method m = Node.class.getDeclaredMethod("setFocused", boolean.class);
        m.setAccessible(true);
        m.invoke(box, false);
    }

    /** A box: the node, its text, its caret and whether it has the focus. */
    public static Object[] boxState(javafx.scene.control.TextField box) {
        return box == null ? null : new Object[]{box, box.getText(), box.getCaretPosition(), box.isFocused()};
    }

    /** Text typed into a box as keys would, its listener running (outside a draw); the text it holds after. */
    public static String boxType(javafx.scene.control.TextField box, String text) {
        box.setText(text);
        return box.getText();
    }

    /** Kept.text on a box: the text it holds after. */
    public static String boxSet(javafx.scene.control.TextField box, String text) {
        Kept.text(box, text);
        return box.getText();
    }

    /**
     * A chip pressed on the page, as the pointer would press it: the first button or label with exactly these words that can be
     * pressed (a chip strip's Button is fired, a step chip's Label is clicked). False when the page has none.
     */
    public boolean pressChip(String words) {
        Labeled chip = find(column, Labeled.class, l -> words.equals(l.getText()) && (l instanceof ButtonBase || l.getOnMouseClicked() != null));
        if (chip == null) return false;
        if (chip instanceof ButtonBase b) b.fire();
        else fireMouse(chip, javafx.scene.input.MouseEvent.MOUSE_CLICKED, 5, 5, javafx.scene.input.MouseButton.PRIMARY, 1);
        return true;
    }

    /** The words of the first chip or step on the page that pass a test (its label), or null. */
    public String chipWith(java.util.function.Predicate<String> test) {
        Labeled chip = find(column, Labeled.class, l -> l.getText() != null && test.test(l.getText())
                && (l instanceof ButtonBase || l.getOnMouseClicked() != null));
        return chip == null ? null : chip.getText();
    }

    /**
     * Every foldable row of the fund's page clicked open, as the pointer would (a holding's bond issuer "▸ ...", the "and N more
     * under ..." row, an activity line that groups): how many were opened. A click redraws the tab through its real show method.
     */
    public int fundUnfold() {
        int opened = 0;
        for (int i = 0; i < 40; i++) {
            Labeled row = find(column, Labeled.class, l -> l.getText() != null && (l.getText().startsWith("▸ ")
                    || (l.getText().startsWith("and ") && l.getText().contains(" more under "))));
            if (row == null) break;
            fireMouse(row, javafx.scene.input.MouseEvent.MOUSE_CLICKED, 5, 5, javafx.scene.input.MouseButton.PRIMARY, 1);
            opened++;
        }
        return opened;
    }

    /** Every fold and row the fund has opened closed again (its page drawn on the next draw). */
    public void fundFoldsClosed() {
        ui.financesScreen.fundScreen.open.clear();
    }

    /* ----- the pictures, and how often they draw ----- */

    /** The kinds of picture KeptCheck's section 64 watches. */
    public static List<String> pictureKinds() {
        return List.of("CurveChart", "DeskCurve", "DeskBars", "PathChart", "BuybackLadder", "AuctionChart", "Columns", "RankBar", "TimeChart");
    }

    private static boolean isPicture(String kind, Node n) {
        return switch (kind) {
            case "CurveChart" -> n instanceof FinancesScreen.CurveChart;
            case "DeskCurve" -> n instanceof FinancesScreen.DeskCurve;
            case "DeskBars" -> n instanceof FinancesScreen.DeskBars;
            case "PathChart" -> n instanceof FinancesScreen.PathChart;
            case "BuybackLadder" -> n instanceof FinancesScreen.BuybackLadder;
            case "AuctionChart" -> n instanceof FinancesScreen.AuctionChart;
            case "Columns" -> n instanceof Pieces.Columns;
            case "RankBar" -> n instanceof Pieces.RankBar;
            case "TimeChart" -> n instanceof TimeChart;
            default -> throw new IllegalArgumentException(kind);
        };
    }

    /** Every picture of a kind on the page now, in the page's order. */
    public List<Pane> pictures(String kind) {
        List<Pane> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof Pane p && isPicture(kind, n)) out.add(p);
        });
        return out;
    }

    private final java.util.Map<Pane, int[]> repaintCounts = new java.util.IdentityHashMap<>();

    /**
     * How many times a picture has drawn itself since the bench first asked about it (0 at that first asking): a chart counts its
     * frames (TimeChart.paints); a picture that clears its children and builds them again when it draws (a Drawn, the ladder, the
     * columns) counts those clearings, which the bench listens to from the first asking on; a bar that keeps its parts
     * (RankBar) counts nothing, and layoutRequested() is its draw.
     */
    public int repaints(Pane picture) {
        if (picture instanceof TimeChart t) return t.paints;
        int[] c = repaintCounts.get(picture);
        if (c == null) {
            int[] counter = new int[1];
            picture.getChildren().addListener((javafx.collections.ListChangeListener<Node>) change -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        counter[0]++;
                        break;
                    }
                }
            });
            repaintCounts.put(picture, counter);
            return 0;
        }
        return c[0];
    }

    /** What a bar was last told (RankBar): its figures, for comparing one month with the next; the picture's signature for any other. */
    public static String figuresOf(Pane picture) {
        if (picture instanceof Pieces.RankBar r) return r.amount + "," + r.pos + "," + r.neg;
        return pictureSignature(picture);
    }

    /** Why a picture is waiting for a layout: itself, or which of its parts, and the figures it was last told. */
    public static String layoutReason(Pane picture) {
        StringBuilder b = new StringBuilder(picture.isNeedsLayout() ? "itself" : "not itself");
        for (Node k : picture.getChildrenUnmodifiable()) if (k instanceof javafx.scene.Parent p && p.isNeedsLayout()) b.append(", ").append(k.getClass().getSimpleName());
        return b + "; told " + figuresOf(picture) + "; managed " + picture.isManaged() + ", visible " + picture.isVisible() + ", parent " + (picture.getParent() == null ? null : picture.getParent().getClass().getSimpleName());
    }

    /** Whether a picture has asked to be laid out again (a bar told a figure that moved: RankBar.set). */
    public static boolean layoutRequested(Pane picture) {
        return picture.isNeedsLayout();
    }

    /**
     * What a picture and everything in it show now, a line a node: two pictures with the same lines draw the same. A chart that paints on
     * a canvas (TimeChart) is also its pixels, which no node of it records.
     */
    public static String pictureSignature(Node picture) {
        List<String> out = new ArrayList<>();
        preciseNow = true;
        try {
            sign(picture, "", out);
        } finally {
            preciseNow = false;
        }
        if (picture instanceof TimeChart) out.add("pixels " + java.util.Arrays.hashCode(pixelsOf(picture)));
        return String.join("\n", out);
    }

    /* ----- the animations a month starts ----- */

    /** Every animation a kept draw has played (Kept.play) on a node of the page: the last on each node. */
    public List<javafx.animation.Animation> pageAnimations() {
        List<javafx.animation.Animation> out = new ArrayList<>();
        each(column, n -> {
            javafx.animation.Animation a = Kept.animationOf(n);
            if (a != null) out.add(a);
        });
        return out;
    }

    /** Whether any animation of the page is running. */
    public boolean animating() {
        for (javafx.animation.Animation a : pageAnimations()) if (a.getStatus() != javafx.animation.Animation.Status.STOPPED) return true;
        return false;
    }

    /** How many nodes of the page that were popped are not at rest (a scale other than 1). */
    public int notAtRest() {
        int[] n = {0};
        each(column, node -> {
            if (Kept.animationOf(node) != null && (node.getScaleX() != 1 || node.getScaleY() != 1)) n[0]++;
        });
        return n[0];
    }

    /** The PROFIT figure on the page (the Overview's scorecard, or a page's status strip): the node under the word, or null. */
    public Node bankProfitFigure() {
        VBox cell = find(column, VBox.class, v -> v.getChildren().size() > 1 && saysProfit(v.getChildren().get(0)));
        return cell == null ? null : cell.getChildren().get(1);
    }

    private static boolean saysProfit(Node first) {
        if (first instanceof Label l) return l.getText() != null && l.getText().startsWith("PROFIT");
        return first instanceof HBox row && !row.getChildren().isEmpty() && row.getChildren().get(0) instanceof Label l
                && l.getText() != null && l.getText().startsWith("PROFIT");
    }

    /** The Overview's capital gauge: the bar in the card that opens "CAPITAL", or null. */
    public Pieces.SegmentBar bankGauge() {
        VBox card = find(column, VBox.class, v -> !v.getChildren().isEmpty() && v.getChildren().get(0) instanceof Label l
                && l.getText() != null && l.getText().startsWith("CAPITAL"));
        return card == null ? null : find(card, Pieces.SegmentBar.class, b -> true);
    }

    /**
     * Whether the capital gauge would grow on the month that has just landed, by the screen's own reading: the measure that binds is
     * the risk-weighted one, it is finite, and the ratio of the month before (the history's last two) differs from this month's.
     */
    public boolean bankGaugeMoved() {
        ham.citybuildersim.Bank bank = ui.game.getBank();
        if (bank.leverageBinds()) return false;
        double[] g = gaugeNumbers();
        double now = g[0], before = g[1], scale = g[2];
        if (!Double.isFinite(now) || !(now > 0)) return false;
        return Double.isFinite(before) && Math.abs(Math.min(before, scale) - Math.min(now, scale)) > 0;
    }

    /** The gauge's figure now, the risk-weighted ratio of the month before it (the history's second last), and the scale the bar is drawn on: for a failure to say what it saw. */
    public double[] gaugeNumbers() {
        double now = ui.bankScreen.gauge().value();
        double[] s = ui.game.getHistorySave().aligned("bankCapitalRatio");
        double before = Double.NaN;
        int seen = 0;
        for (int i = s.length - 1; i >= 0 && seen < 2; i--) {
            if (!Double.isFinite(s[i])) continue;
            if (++seen == 2) before = s[i];
        }
        return new double[]{now, before, ui.bankScreen.gauge().top() * Pieces.BAND_SCALE};
    }

    /** The numbers of the businesses' bonds outstanding, the ones that fall due last first (the largest first among those): books that outlast a long section. */
    public List<Integer> bondIdsLasting() {
        List<ham.citybuildersim.CorporateBond> bonds = new ArrayList<>(ui.game.getBondMarket().getBonds());
        bonds.sort((a, b) -> a.maturityMonth() != b.maturityMonth() ? Integer.compare(b.maturityMonth(), a.maturityMonth()) : Double.compare(b.face(), a.face()));
        List<Integer> out = new ArrayList<>();
        for (ham.citybuildersim.CorporateBond b : bonds) out.add(b.id());
        return out;
    }

    /** The fund's worth as the hero says it (the page's one 28-point figure), or null. */
    public String fundWorth() {
        Label l = find(column, Label.class, x -> x.getStyle() != null && x.getStyle().contains("-fx-font-size: 28.0px"));
        return l == null ? null : l.getText();
    }

    /** The yield box's text for a cap, as a page built new shows it (CapChips.yieldText): 0.045 is "4.500" (KP2b). */
    public static String yieldText(Auction.Cap cap) {
        return CapChips.yieldText(cap);
    }

    /** The cap a typed yield names, as Enter in the yield box sets it (CapChips.fromWords, in per cent): "4.5" is the cap at 4.5% (KP2b). */
    public static Auction.Cap yieldCapOf(String typed) {
        return Auction.Cap.at(CapChips.fromWords(typed));
    }

    /** The highest yield Borrow's sale will take, as the page keeps it (KP2b). */
    public Auction.Cap borrowCap() {
        return ui.financesScreen.borrowCap;
    }

    /** The highest yield the sale will take put back to what Borrow opens with (fair and the tail). */
    public void financesCapDefault() {
        ui.financesScreen.borrowCap = Auction.Cap.DEFAULT;
    }

    /** How many holdings the fund's page says paid something in the month just landed ("paid +D$26k this month"). */
    public int paidLines() {
        int[] n = {0};
        each(column, node -> {
            if (node instanceof Label l && l.getText() != null && l.getText().startsWith("paid +")) n[0]++;
        });
        return n[0];
    }

    /** The Bank tab forgotten and the Finances tab forgotten, as another city opened would leave them. */
    public void forgetMoney() {
        ui.bankScreen.forget();
        ui.financesScreen.forget();
    }

    /* =====================================================================
       SECTORS (KP4): the list and a business's five pages, kept and built new, the hands on what reshapes them, the
       pictures they hold (the refinery's, a materials industry's, a leg's bar), the count-up the clock allows and the
       pointer over a card, a door and the crumb
       ===================================================================== */

    /** The list the Sectors tab opens on: folded to its first row (Show more), nothing opened, the Summary view. */
    public Page sectors() {
        return sectors("the Sectors list", null, SectorScreen.SECTOR_HOME, false);
    }

    /**
     * The Sectors tab as a Page, as the screen draws it from its pool (the list when `key` is null, else the business and its page, in the
     * Summary or the Statement view): it opens with nothing folded open or noted, the statements' two columns on and Freight on All.
     */
    public Page sectors(String name, String key, String page, boolean statement) {
        SectorScreen s = ui.sectorScreen;
        return new Page(name, "showSectorMenu", () -> s.pool, pool -> timed(pool, s::drawPage), () -> {
            s.forget();
            s.sectorsExpanded = false;
            s.detailsOpen.clear();
            for (ham.citybuildersim.Sector each : ui.game.getSectors().all()) s.linesOpen(each).clear();
            s.showChange = true;
            s.showShare = true;
            s.freightDivision = ham.citybuildersim.FreightView.Division.ALL;
            sectorsOpen(key, page, statement);
        });
    }

    /** The five pages of a business, in the strip's order. */
    public static List<String> sectorPages() {
        return List.of(SectorScreen.SECTOR_PAGES);
    }

    /** Freight's divisions by the words on their chips: All, Haulage, Rail, Ports. */
    public static List<String> freightDivisions() {
        List<String> out = new ArrayList<>();
        for (ham.citybuildersim.FreightView.Division d : ham.citybuildersim.FreightView.Division.values()) out.add(d.label());
        return out;
    }

    /** The screen on the list (`key` null), or on a business's page in a view, as a card's click, a chip or the switch would put it (the next draw shows it). */
    public void sectorsOpen(String key, String page, boolean statement) {
        SectorScreen s = ui.sectorScreen;
        s.openSector = key == null ? null : ui.game.getSectors().byKey(key);
        s.sectorPage = page;
        s.statementView = statement;
    }

    /** The list's cards open to their second row (Show more) or folded to the first (Show less). */
    public void sectorsMore(boolean more) {
        ui.sectorScreen.sectorsExpanded = more;
    }

    /** Freight's Operations page on one of its divisions, by the words on its chip. */
    public void sectorsDivision(String label) {
        for (ham.citybuildersim.FreightView.Division d : ham.citybuildersim.FreightView.Division.values()) {
            if (d.label().equals(label)) ui.sectorScreen.freightDivision = d;
        }
    }

    /**
     * One of the pages' folds opened or closed: "every" (the fold of every line, on a business's Operations page), "foods" (the goods
     * a column folds into one row, on the shops' and the kitchens'), "book" (the order book, on the Balance sheet's owners card).
     */
    public void sectorsFold(String kind, String key, boolean open) {
        String name = switch (kind) {
            case "every" -> key + ":every";
            case "foods" -> key + ":foods";
            case "book" -> "book:" + ham.citybuildersim.Equity.indexOf(key);
            default -> throw new IllegalArgumentException(kind);
        };
        fold(ui.sectorScreen.detailsOpen, name, open);
    }

    /** Every note of a business's four statements opened or closed, as the toolbar's "open every note" would. */
    public void sectorsNotes(String key, boolean open) {
        java.util.Set<String> notes = ui.sectorScreen.linesOpen(ui.game.getSectors().byKey(key));
        for (String table : new String[]{SectorScreen.INCOME, SectorScreen.SHEET, SectorScreen.EQUITY, SectorScreen.CASH}) {
            for (int n = 0; n <= 24; n++) fold(notes, StatementView.noteKey(table, n), open);
        }
    }

    /** One note of one statement of a business opened or closed, as a click on its line would. */
    public void sectorsNote(String key, String table, int note, boolean open) {
        fold(ui.sectorScreen.linesOpen(ui.game.getSectors().byKey(key)), StatementView.noteKey(table, note), open);
    }

    /**
     * The first note of a business's income statement a click opens on lines this month, or 0 when none does: its line drawn
     * (shown, and not nil in the statement's columns) and its note not empty, the statement's own test for a caret. A note
     * with nothing in it this month draws no caret and opens nothing (the revenue note of a business with one named part,
     * Construction's on 0.9.19), so a fold check that chose it would cause nothing.
     */
    public int sectorsNoteOf(String key) {
        ham.citybuildersim.Sector s = ui.game.getSectors().byKey(key);
        ham.citybuildersim.SectorBooks books = ui.game.getSectorBooks();
        ham.citybuildersim.SectorStatements.Table t = ui.sectorScreen.incomeTable(s, books.get(s), books.previous(s));
        boolean millions = ham.citybuildersim.SectorStatements.inMillions(t);
        StatementView.Columns c = ui.sectorScreen.columns("of revenue");
        java.util.function.IntFunction<Node> notes = ui.sectorScreen.incomeNotes(s, t, millions);
        for (ham.citybuildersim.SectorStatements.Row r : t.rows()) {
            if (r.note() <= 0 || !r.shown() || StatementView.nil(r, t, c, millions)) continue;
            Node detail = notes.apply(r.note());
            if (detail == null || detail instanceof Pane p && p.getChildren().isEmpty()) continue;
            return r.note();
        }
        return 0;
    }

    /** The statements' change and common-size columns on or off, as the toolbar's two chips would. */
    public void sectorsColumns(boolean change, boolean share) {
        ui.sectorScreen.showChange = change;
        ui.sectorScreen.showShare = share;
    }

    /** The Sectors tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showSectors() {
        ensureWindow();
        ui.sectorScreen.showSectorMenu();
    }

    /** The Sectors tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. */
    public void forgetSectors() {
        ui.sectorScreen.forget();
    }

    /* ----- the pictures ----- */

    /** The refinery's picture on the page, or null. */
    public Pane refineryPicture() {
        return first(SectorScreen.RefineryPicture.class);
    }

    /** A materials industry's picture on the page, or null. */
    public Pane industryPane() {
        return first(SectorScreen.IndustryPane.class);
    }

    /** Every leg's bar on the page. */
    public List<Pane> legBars() {
        List<Pane> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof SectorScreen.LegBar bar) out.add(bar);
        });
        return out;
    }

    /** What a picture last painted, by identity (null before a first paint): another object only where the month's view moved or the width did. */
    public static Object painted(Node picture) {
        if (picture instanceof SectorScreen.RefineryPicture r) return r.painted;
        if (picture instanceof SectorScreen.IndustryPane i) return i.painted;
        return null;
    }

    /** What a picture (or a bar) holds now, in its order. */
    public static List<Node> partsOf(Pane picture) {
        return new ArrayList<>(picture.getChildren());
    }

    /* ----- the count-up and the sweep ----- */

    /** The KEPT figure on the page now: the list's first cell, or the open business's. */
    public Label sectorsKept() {
        VBox cell = find(column, VBox.class, v -> v.getChildren().size() > 1 && v.getChildren().get(1) instanceof Label
                && saysKept(v.getChildren().get(0)));
        return cell == null ? null : (Label) cell.getChildren().get(1);
    }

    /** Whether a label says KEPT, or "KEPT  ›" (the cell that is a door says its name and a chevron). */
    private static boolean isKept(Label l) {
        return l.getText() != null && (l.getText().equals("KEPT") || l.getText().startsWith("KEPT  "));
    }

    /** Whether a cell's first node is its "KEPT" label, or the row that holds it beside an (i). */
    private static boolean saysKept(Node first) {
        if (first instanceof Label l) return isKept(l);
        return first instanceof HBox row && !row.getChildren().isEmpty() && row.getChildren().get(0) instanceof Label l && isKept(l);
    }

    /** The net income each card on the list shows, in the order the grid has them: its figure's label. */
    public List<Label> sectorsCardFigures() {
        List<Label> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof VBox card && card.getOnMouseClicked() != null && card.getStyle() != null
                    && card.getStyle().contains("-fx-padding: 12; -fx-cursor: hand; -fx-background-color: " + Palette.RAISED)
                    && !card.getChildren().isEmpty() && card.getChildren().get(0) instanceof HBox top && top.getChildren().size() > 2
                    && top.getChildren().get(2) instanceof VBox right && !right.getChildren().isEmpty()
                    && right.getChildren().get(0) instanceof Label figure) out.add(figure);
        });
        return out;
    }

    /**
     * The words the figures read this month, from the model as the screen reads it, with last month's beside: {the list's KEPT, then
     * the open business's KEPT}, each as {last month's, this month's}, and the cards' as {last, this} in the card order. A business's is
     * null when none is open (`key` null).
     */
    public String[][] sectorsWords(String key) {
        ham.citybuildersim.SectorBooks books = ui.game.getSectorBooks();
        List<ham.citybuildersim.Sector> order = ui.sectorScreen.marketOrder();
        SectorScreen.ListFigures l = SectorScreen.listFigures(books, order);
        List<String[]> out = new ArrayList<>();
        out.add(new String[]{SectorScreen.m(l.before()) + "/mo", SectorScreen.m(l.kept()) + "/mo"});
        if (key == null) {
            out.add(null);
        } else {
            ham.citybuildersim.Sector s = ui.game.getSectors().byKey(key);
            out.add(new String[]{SectorScreen.m(books.previous(s).netIncome()), SectorScreen.m(books.get(s).netIncome())});
        }
        for (ham.citybuildersim.Sector s : order) {
            out.add(new String[]{SectorScreen.m(books.previous(s).netIncome()) + "/mo", SectorScreen.m(books.get(s).netIncome()) + "/mo"});
        }
        return out.toArray(new String[0][]);
    }

    /** Whether a count-up is running on a figure now (one was started and has not finished or been stopped). */
    public static boolean counting(Node figure) {
        javafx.animation.Animation a = Kept.animationOf(figure);
        return a != null && a.getStatus() != javafx.animation.Animation.Status.STOPPED;
    }

    /** The count-up last started on a figure: the animation object, or null. */
    public static javafx.animation.Animation countOf(Node figure) {
        return Kept.animationOf(figure);
    }

    /** Whether the page has an income waterfall. */
    public boolean hasFall() {
        return first(Pieces.Waterfall.class) != null;
    }

    /** The growth last started on the page's income waterfall (a new month that animates starts one), or null. */
    public javafx.animation.Animation fallGrowth() {
        Pieces.Waterfall fall = first(Pieces.Waterfall.class);
        return fall == null ? null : Kept.animationOf(fall);
    }

    /** The sweep last started on a ring of the page (the plant's), or null. */
    public javafx.animation.Animation ringSweep() {
        List<javafx.animation.Animation> found = new ArrayList<>();
        each(column, n -> {
            if (n instanceof javafx.scene.shape.Arc a && Kept.animationOf(a) != null) found.add(Kept.animationOf(a));
        });
        return found.isEmpty() ? null : found.get(0);
    }

    /** Whether the page has a ring with an arc to sweep (a plant that runs). */
    public boolean hasRing() {
        boolean[] has = {false};
        each(column, n -> {
            if (n instanceof javafx.scene.shape.Arc) has[0] = true;
        });
        return has[0];
    }

    /* ----- the controls (the two dials of the oil reserve) ----- */

    /** Every slider on the page, in the page's order: the oil reserve's FILL dial first, then its RELEASE dial. */
    public List<javafx.scene.control.Slider> sliders() {
        List<javafx.scene.control.Slider> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof javafx.scene.control.Slider s) out.add(s);
        });
        return out;
    }

    /** What a policy dial has staged under its key ("reserveFill", "reserveRelease"), or NaN when nothing is. */
    public double staged(String key) {
        return ui.policyScreen.isStaged(key) ? ui.policyScreen.staged(key, Double.NaN) : Double.NaN;
    }

    /** A policy dial's staged value dropped. */
    public void unstage(String key) {
        ui.policyScreen.unstage(key);
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

    /** Whether a picture's signature is being written (KeptCheck 64): to four decimals, so that a bar that moved by a hair is told from one that did not. */
    private static boolean preciseNow;

    private static void sign(Node n, String path, List<String> out) {
        sign(n, path, out, false);
    }

    /**
     * One node and what is under it. `unlaid` is true inside a node that is hidden and unmanaged: nobody lays it out and nobody sees it, so
     * where it sits is what it was last laid out at (a kept fold's detail, closed after it was open) or its default (the same fold built
     * new), and neither is the page's. Its words, style, handlers and values are still compared; only its place and size are not (KP5, slice D).
     */
    private static void sign(Node n, String path, List<String> out, boolean unlaid) {
        unlaid |= !n.isVisible() && !n.isManaged();
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
        if (unlaid) {
            b.append(" (not laid out)");
        } else {
            Bounds at = n.getBoundsInParent();
            b.append(String.format(Locale.ROOT, PRECISE || preciseNow ? " at %.4f,%.4f %.4fx%.4f" : " at %.1f,%.1f %.1fx%.1f", at.getMinX(), at.getMinY(), at.getWidth(), at.getHeight()));
        }
        out.add(b.toString());
        String inner = path + "  ";
        if (n instanceof Labeled l && l.getGraphic() != null) sign(l.getGraphic(), inner + "graphic: ", out, unlaid);
        // A Pane's children, and those of the game's own pictures that are a Region and no Pane (the header's tile).
        if (n instanceof Region r && !(n instanceof Control)) for (Node k : r.getChildrenUnmodifiable()) sign(k, inner, out, unlaid);
        if (n instanceof ScrollPane sp && sp.getContent() != null) sign(sp.getContent(), inner + "content: ", out, unlaid);
        if (n instanceof Group g) for (Node k : g.getChildren()) sign(k, inner, out, unlaid);
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
        // KP6: a kept path (Trade's river ribbons, a Sector's curve) is handed back with last month's elements unless the draw sets them.
        if (n instanceof javafx.scene.shape.Path p) return " path" + pathOf(p);
        return "";
    }

    /** A path's elements as figures: each element's kind and its end point (and a curve's controls). */
    private static String pathOf(javafx.scene.shape.Path p) {
        StringBuilder b = new StringBuilder();
        for (javafx.scene.shape.PathElement e : p.getElements()) {
            if (e instanceof javafx.scene.shape.MoveTo m) b.append(String.format(Locale.ROOT, " M%.2f,%.2f", m.getX(), m.getY()));
            else if (e instanceof javafx.scene.shape.LineTo l) b.append(String.format(Locale.ROOT, " L%.2f,%.2f", l.getX(), l.getY()));
            else if (e instanceof javafx.scene.shape.CubicCurveTo c) b.append(String.format(Locale.ROOT, " C%.2f,%.2f %.2f,%.2f %.2f,%.2f",
                    c.getControlX1(), c.getControlY1(), c.getControlX2(), c.getControlY2(), c.getX(), c.getY()));
            else if (e instanceof javafx.scene.shape.QuadCurveTo q) b.append(String.format(Locale.ROOT, " Q%.2f,%.2f %.2f,%.2f", q.getControlX(), q.getControlY(), q.getX(), q.getY()));
            else if (e instanceof javafx.scene.shape.HLineTo h) b.append(String.format(Locale.ROOT, " H%.2f", h.getX()));
            else if (e instanceof javafx.scene.shape.VLineTo v) b.append(String.format(Locale.ROOT, " V%.2f", v.getY()));
            else if (e instanceof javafx.scene.shape.ClosePath) b.append(" Z");
            else b.append(' ').append(e.getClass().getSimpleName());
        }
        return b.toString();
    }

    /* =====================================================================
       TRADE & THE WORLD (KP6, slice B): the six pages and a nation's page behind Partners, kept and built new, the hands on
       what reshapes them (a page or a nation picked, the span, the good, a fold or a line opened, the river and the goods'
       toggles, the exchange's ask) and the real show method
       ===================================================================== */

    /** The Trade tab on its Overview: nothing folded open, the steps, goods rather than businesses, the exchange buying and asking nothing. */
    public Page trade() {
        return trade("the Trade Overview", TradeScreen.OVERVIEW);
    }

    /**
     * The Trade tab as a Page, as the screen draws it from its pool: the page `page` names (one of tradePages()), and on Partners the
     * nation tradeNationPick() names - it opens with nothing folded open, the steps, goods, the month's table and the exchange buying.
     */
    public Page trade(String name, String page) {
        TradeScreen s = ui.tradeScreen;
        return new Page(name, "showForeignMenu", () -> s.pool, pool -> timed(pool, s::drawPage), () -> {
            s.forget();
            s.openLines.clear();
            s.showRiver = false;
            s.byBusiness = false;
            s.tradeBuying = true;
            s.tradeExchange = 0;
            s.tradeMoney = null;
            s.partners.nation = 0;
            s.partners.span = ham.citybuildersim.PartnersView.Span.MONTH;
            s.partners.good = null;
            tradeOpen(page);
        });
    }

    /** A nation's page behind Partners (the number from 1: tradePartners() names some), as the table's link would open it. */
    public Page tradeNation(String name, int nation) {
        Page p = trade(name, TradeScreen.PARTNERS);
        Runnable home = p.home;
        return new Page(name, p.screen, p.pool, p.draw, () -> {
            home.run();
            tradeNationPick(nation);
        });
    }

    /** The six pages, in the strip's order. */
    public static List<String> tradePages() {
        return List.of(TradeScreen.PAGES);
    }

    /** The keys of the "details" folds and "more" lines a page of the tab has, outermost first (a fold inside a fold needs the one it is in open). */
    public static List<String> tradeFolds(String page) {
        return switch (page) {
            case TradeScreen.MONTH    -> List.of("month:ledger");
            case TradeScreen.GOODS    -> List.of("goods:all", "goods:prices", "goods:band", "band:all");
            case TradeScreen.PARTNERS -> List.of("partners:all", "partners:goods");
            case TradeScreen.CURRENCY -> List.of("currency:rule");
            case TradeScreen.RESERVES -> List.of("reserves:eight", "reserves:claims");
            default                   -> List.of();
        };
    }

    /** The key of the fold of the rest of a nation's goods on its page. */
    public static String tradeNationFold(int nation) {
        return "partners:goods:" + nation;
    }

    /** The screen on a page, as a chip would put it (the next draw shows it): a nation's page only by tradeNationPick(). */
    public void tradeOpen(String page) {
        TradeScreen s = ui.tradeScreen;
        s.tradeArea = null;
        s.tradePage = page;
        s.partners.nation = 0;
    }

    /** The screen on a nation's page behind Partners, as a row's link would put it (the next draw shows it); 0 is the table. */
    public void tradeNationPick(int nation) {
        TradeScreen s = ui.tradeScreen;
        s.tradePage = TradeScreen.PARTNERS;
        s.partners.nation = nation;
    }

    /** The nations the month's table ranks first, at most `limit`, by number: the ones a row, a strip card or a link opens. */
    public List<Integer> tradePartners(int limit) {
        ham.citybuildersim.PartnersView.Table table = ham.citybuildersim.PartnersView.table(ui.game, ham.citybuildersim.PartnersView.Span.MONTH);
        List<Integer> out = new ArrayList<>();
        if (table != null) for (ham.citybuildersim.PartnersView.Row r : table.rows()) if (out.size() < limit) out.add(r.nation());
        return out;
    }

    /** The goods the Partners board offers, those the city sold first. */
    public List<ham.citybuildersim.Good> tradeGoods() {
        ham.citybuildersim.PartnersView.Goods goods = ham.citybuildersim.PartnersView.goods(ui.game);
        List<ham.citybuildersim.Good> out = new ArrayList<>(goods.sold());
        out.addAll(goods.others());
        return out;
    }

    /** The Partners board of a good, as its chip would pick it. */
    public void tradeGood(ham.citybuildersim.Good good) {
        ui.tradeScreen.partners.good = good;
    }

    /** The Partners table of the year so far or of the month, as its chip would pick it. */
    public void tradeSpan(boolean year) {
        ui.tradeScreen.partners.span = year ? ham.citybuildersim.PartnersView.Span.YEAR : ham.citybuildersim.PartnersView.Span.MONTH;
    }

    /** A fold, or a "more" line, opened or closed by its key, as a click on it would. */
    public void tradeLine(String key, boolean open) {
        fold(ui.tradeScreen.openLines, key, open);
    }

    /** The Month page on the river or on the steps, as its chip would put it. */
    public void tradeRiver(boolean river) {
        ui.tradeScreen.showRiver = river;
    }

    /** What we trade by business or by good, as its chip would put it. */
    public void tradeByBusiness(boolean business) {
        ui.tradeScreen.byBusiness = business;
    }

    /** The exchange buying or selling, as its toggle would put it (an ask the other way cleared). */
    public void tradeBuying(boolean buying) {
        TradeScreen s = ui.tradeScreen;
        if (buying != s.tradeBuying) {
            s.tradeExchange = 0;
            s.tradeMoney = null;
        }
        s.tradeBuying = buying;
    }

    /** The exchange asked for this much (thousands of the city's money; the card keeps it to what there is to deal). */
    public void tradeAsk(double thousands) {
        ui.tradeScreen.tradeExchange = thousands;
    }

    /** The exchange dealing in one of the eight moneys by its code (null: the mix when buying, the largest holding when selling). */
    public void tradeMoney(String code) {
        ui.tradeScreen.tradeMoney = code;
    }

    /** A door of the tab to a page and a card on it (TradeScreen.open(page, target)): the real open, which draws the screen through its show method. */
    public void tradeDoor(String page, String target) {
        ensureWindow();
        ui.tradeScreen.open(page, target);
    }

    /** The Trade tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showTrade() {
        ensureWindow();
        ui.tradeScreen.showForeignMenu();
    }

    /** The Trade tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. */
    public void forgetTrade() {
        ui.tradeScreen.forget();
    }

    /* =====================================================================
       THE GOVERNMENT TAB (KP6, slice A): its four pages, kept and built new, the hands on what reshapes them (a page picked, a
       fold or a line opened, a door's target) and the real show method
       ===================================================================== */

    /** The Government tab on its Overview: nothing folded open, no line opened. */
    public Page government() {
        return government("the Government Overview", "Overview");
    }

    /**
     * The Government tab as a Page, as the screen draws it from its pool, on the page `page` names (its first page when null): it
     * opens with nothing folded open and no line opened.
     */
    public Page government(String name, String page) {
        GovernmentScreen s = ui.governmentScreen;
        return new Page(name, "showGovernmentMenu", () -> s.pool, pool -> timed(pool, s::drawPage), () -> {
            s.forget();
            s.open.clear();
            s.govPage = page == null ? GovernmentScreen.GOV_PAGES[0] : page;
        });
    }

    /** The tab's four pages, in the chip strip's order. */
    public static List<String> governmentPages() {
        return List.of(GovernmentScreen.GOV_PAGES);
    }

    /** The keys of the folds the pages hold: the treasury's month, the paper, the output's months recorded, and the bridge's two columns' "N more". */
    public static List<String> governmentFolds() {
        return List.of("treasury", "debt", "output", "bridge:0", "bridge:1");
    }

    /** The lines of Revenue or Spending that open into who pays (the other pages have none). */
    public static List<String> governmentLines(String page) {
        return switch (page) {
            case "Revenue" -> List.of("Business tax", "Sales tax", "Wage tax", "Property tax", "Pension contributions",
                    "Healthcare fees", "School fees", "Mortgage insurance premiums", "Transfer from the fund");
            case "Spending" -> List.of("Healthcare", "Education", "Pensions", "Interest on the city's paper", "Land bought",
                    "Mortgage insurance claims", "Food assistance");
            default -> List.of();
        };
    }

    /** The screen on the page `page` names, as a chip would put it (the next draw shows it). */
    public void governmentOpen(String page) {
        ui.governmentScreen.govPage = page;
    }

    /** A fold ("details" or a bridge column's "N more"), by its key, opened or closed, as a click on it would. */
    public void governmentFold(String key, boolean open) {
        fold(ui.governmentScreen.open, key, open);
    }

    /** A line of Revenue or Spending opened or closed into who pays, by its name, as a click on its row would. */
    public void governmentLine(String page, String line, boolean open) {
        fold(ui.governmentScreen.open, GovernmentScreen.lineKey(page, line), open);
    }

    /** A chip of the Government tab picked, as a click on it would: the page, at its top (GovernmentScreen.open, no line). */
    public void governmentPick(String page) {
        ui.governmentScreen.open(page, null);
    }

    /** A door of the tab used, as its click would: the page opened with `line` opened and scrolled to (a budget line's name, or the Overview's BRIDGE or BALANCE). */
    public void governmentDoor(String page, String line) {
        ui.governmentScreen.open(page, line);
    }

    /** The Government tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showGovernment() {
        ensureWindow();
        ui.governmentScreen.showGovernmentMenu();
    }

    /** The Government tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. */
    public void forgetGovernment() {
        ui.governmentScreen.forget();
    }

    /* =====================================================================
       POLICY AND ZONING (KP6, slice C): the hub and every page of the five areas, kept and built new, the hands on what reshapes them (an
       area and page picked, a fold or a line opened, a zone's page, a change staged), the controls on them and the real show method
       ===================================================================== */

    /** The Policy tab on its hub: nothing opened, nothing staged. */
    public Page policy() {
        return policy("the Policy hub", null, null);
    }

    /**
     * The Policy tab as a Page, as the screen draws it from its pool (the hub when `area` is null, else the area and the page `page`
     * names, or the area's first page when `page` is null; a zone's page of Zoning is "zone:" and its sector's key): it opens with
     * nothing folded open and nothing staged. Zoning's pages are drawn from the same pool.
     */
    public Page policy(String name, String area, String page) {
        PolicyScreen s = ui.policyScreen;
        return new Page(name, "showPolicyMenu", () -> s.pool, pool -> timed(pool, s::drawPage), () -> {
            s.forget();
            s.openLines.clear();
            s.dropProposal();
            policyOpen(area, page);
        });
    }

    /** Every fixed page of the five areas as {area, page}, in the tabs' order (Zoning's two; its zones' pages are policyZone()'s). */
    public static List<String[]> policyPages() {
        List<String[]> out = new ArrayList<>();
        for (String area : PolicyScreen.AREAS) {
            for (String page : PolicyScreen.pagesOf(area)) out.add(new String[]{area, page});
        }
        return out;
    }

    /** The page of the zone of a sector, as the area's tabs name it. */
    public static String policyZonePage(String sector) {
        return ZoningScreen.zonePage(sector);
    }

    /** Every sector's key, in the registry's order: each has a zone's page, zoned or not. */
    public static List<String> policySectors() {
        return List.of(ham.citybuildersim.Sectors.KEYS);
    }

    /** Policy on an area and page (`area` null: the hub), as a door or a tab would put it; the staged set dropped (the next draw shows it). */
    public void policyOpen(String area, String page) {
        PolicyScreen s = ui.policyScreen;
        s.policyArea = area;
        s.policyPage = area == null ? PolicyScreen.POLICY_HOME : page == null ? PolicyScreen.pagesOf(area)[0] : page;
        s.dropProposal();
    }

    /** A door of the tab pressed (PolicyScreen.open): the area and page at their top, through the real show method. */
    public void policyPick(String area, String page) {
        ensureWindow();
        ui.currentScreen = "showPolicyMenu";
        ui.policyScreen.open(area, page);
    }

    /** The hub's door pressed (PolicyScreen.openHub). */
    public void policyPickHub() {
        ensureWindow();
        ui.currentScreen = "showPolicyMenu";
        ui.policyScreen.openHub();
    }

    /** A fold, a payer's own dial, a course's own price or the folded zero payers (by its key) opened or closed, as a click on it would. */
    public void policyLine(String key, boolean open) {
        fold(ui.policyScreen.openLines, key, open);
    }

    /** The keys of the lines open now: what a page's folds hold. */
    public java.util.Set<String> policyLines() {
        return new java.util.TreeSet<>(ui.policyScreen.openLines);
    }

    /** A change staged under a lever's key, as a release on its dial stages it (the next draw shows it). */
    public void policyStage(String key, double value) {
        ui.policyScreen.stage(key, value);
    }

    /** A staged change taken back, as the tray's × does. */
    public void policyUnstage(String key) {
        ui.policyScreen.unstage(key);
    }

    /** Everything staged dropped, as "Discard" or "Leave it as it is" does. */
    public void policyDrop() {
        ui.policyScreen.dropProposal();
    }

    /** What is staged now, by key, in the order it was staged. */
    public java.util.Map<String, Double> policyStaged() {
        return new java.util.LinkedHashMap<>(ui.policyScreen.policyStaged);
    }

    /** The keys of the dials the page on show has registered, in the order drawn: what the tray can name and apply. */
    public List<String> policyLevers() {
        return new ArrayList<>(ui.policyScreen.policyLevers.keySet());
    }

    /** The tray under the page, or null with nothing staged to show: the page's third top node. */
    public Node policyTray() {
        List<Node> top = column.getChildren();
        return top.size() > 2 ? top.get(2) : null;
    }

    /** Every slider on the page, in the page's order (the dials': the first is the first card's). */
    public List<javafx.scene.control.Slider> policySliders() {
        List<javafx.scene.control.Slider> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof javafx.scene.control.Slider s) out.add(s);
        });
        return out;
    }

    /** How many "→" arrows the page shows: one a dial card's effect that has moved, so a card drawn at its thumb shows its after. */
    public int policyArrows() {
        int[] n = {0};
        each(column, node -> {
            if (node instanceof Label l && "→".equals(l.getText())) n[0]++;
        });
        return n[0];
    }

    /** The cards of the page that are doors - a card's ground, a hand and a click: the hub's area cards, a tax's card on Taxes (KP2b); not the vitals strip's cells (Pieces.limitCell), which have no ground. */
    public List<VBox> policyDoors() {
        List<VBox> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof VBox v && v.getOnMouseClicked() != null && v.getStyle().contains("-fx-cursor: hand") && v.getStyle().contains("-fx-background-radius: 8")) out.add(v);
        });
        return out;
    }

    /** What HOW STRICT's line says for a thumb at a place on the dial (KP2b): the words under the dial at that step, as the screen's own function makes them. */
    public static String strictLine(Game g, double thumb) {
        return PolicyScreen.strictWords(ham.citybuildersim.PolicyPreview.ruleAt(g, PolicyScreen.strictAt(thumb)));
    }

    /** Whether a label on the page says exactly these words (KP2b). */
    public boolean pageSays(String words) {
        return find(column, Label.class, l -> words.equals(l.getText())) != null;
    }

    /** The key a zone's tax dial stages under, as Policy's Taxes rows have it: the same dial, the sector's own offset (KP2b). */
    public static String policyZoneTaxKey(ham.citybuildersim.Zoning.Tax tax, String sector) {
        return ZoningScreen.taxKey(tax, sector);
    }

    /** A sector zoned through the model (a share of the dry ground, at the going price), as the zone's page applies it; false while zoning is shut. */
    public boolean policyZone(String sector, double share) {
        ui.game.setZone(sector, ham.citybuildersim.Zoning.Size.SHARE, share, ham.citybuildersim.Zoning.MAX_PRICE_SHARE);
        return ui.game.getZoning().zone(sector) != null;
    }

    /** A zone taken off through the model. */
    public void policyUnzone(String sector) {
        ui.game.removeZone(sector);
    }

    /** The sectors with a zone now. */
    public List<String> policyZoned() {
        List<String> out = new ArrayList<>();
        for (ham.citybuildersim.Zoning.Zone z : ui.game.getZoning().zones()) out.add(z.sector());
        return out;
    }

    /** The Policy tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showPolicy() {
        ensureWindow();
        ui.policyScreen.showPolicyMenu();
    }

    /** The Policy tab forgotten, as another city opened would leave it: nothing kept. */
    public void forgetPolicy() {
        ui.policyScreen.forget();
    }

    /* =====================================================================
       THE HANDS THE KP6 HARNESS ADDS (KP6, slice D): what Trade counts up, and the width of its charts
       ===================================================================== */

    /** The head word of a limit cell (Pieces.limitCell): its first child's words, whether that is a label or a row that starts with one. */
    private static String headOf(VBox cell) {
        if (cell.getChildren().isEmpty()) return "";
        Node first = cell.getChildren().get(0);
        if (first instanceof Label l) return l.getText() == null ? "" : l.getText();
        if (first instanceof HBox row && !row.getChildren().isEmpty() && row.getChildren().get(0) instanceof Label l) return l.getText() == null ? "" : l.getText();
        return "";
    }

    /** The two figures of Trade's strip that count up from last month's when a month lands: SOLD ABROAD's and BOUGHT ABROAD's, in that order (empty when the page has no such cells). */
    public List<Label> tradeFigures() {
        List<Label> out = new ArrayList<>();
        for (String head : List.of("SOLD ABROAD", "BOUGHT ABROAD")) {
            VBox cell = find(column, VBox.class, v -> v.getChildren().size() > 1 && v.getChildren().get(1) instanceof Label && headOf(v).startsWith(head));
            if (cell != null) out.add((Label) cell.getChildren().get(1));
        }
        return out;
    }

    /**
     * The words the strip's two counted figures read, from the model as the screen reads them (TradeScreen.countFrom): {SOLD ABROAD, BOUGHT ABROAD},
     * each {last month's, this month's}; last month's is null while History holds fewer than two points.
     */
    public String[][] tradeWords() {
        ham.citybuildersim.ForeignAccounts fx = ui.game.getForeignAccounts();
        ham.citybuildersim.HistorySave h = ui.game.getHistorySave();
        String[][] out = new String[2][];
        String[] series = {"exportsAbroad", "importsAbroad"};
        double[] now = {fx.getExports(), fx.tradeImports()};
        for (int i = 0; i < 2; i++) {
            double[] s = h.aligned(series[i]);
            String before = s.length < 2 || !Double.isFinite(s[s.length - 2]) ? null : ui.tradeScreen.d(s[s.length - 2]);
            out[i] = new String[]{before, ui.tradeScreen.d(now[i])};
        }
        return out;
    }

    /** The mirror-row pictures on the page (the bars of what the city bought and sold abroad), in the page's order: the ones that grow out of their axis when a month lands. */
    public List<Pieces.MirrorRows> tradeBars() {
        List<Pieces.MirrorRows> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof Pieces.MirrorRows m) out.add(m);
        });
        return out;
    }

    /** Every tooltip on the page by the node that carries it, by identity: Kept.tip keeps a node's own tooltip from draw to draw, Tooltip.install on a draw's node makes a new one each time. */
    public java.util.Map<Node, Tooltip> tooltips() {
        java.util.Map<Node, Tooltip> out = new java.util.IdentityHashMap<>();
        each(column, n -> {
            Tooltip t = Kept.tipOf(n);
            if (t != null) out.put(n, t);
        });
        return out;
    }

    /** Every node on the page, by identity. */
    public java.util.Set<Node> nodesOnPage() {
        java.util.Set<Node> out = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        each(column, out::add);
        return out;
    }

    /** The keys of a tax's payers that show a row with a dial of their own (a payer's fold is "payer:" and its key), in the ranking's order: the first is the row at the top. */
    public List<String> policyPayers(String tax) {
        List<String> out = new ArrayList<>();
        for (PolicyScreen.Payer p : ui.policyScreen.payersOf(tax, ui.game.getEconomyManager().getTaxPolicy())) {
            if (p.lever() != null && !PolicyScreen.paidNothing(p)) out.add(p.key());
        }
        return out;
    }

    /** What WHO GOES WITHOUT (Pieces.whoGoesWithout) says now: {its figure's words, whether the door to the shops is "drawn" or "not drawn"}. */
    public String[] hunger() {
        Pieces.Hunger h = Pieces.hunger(ui.game);
        return new String[]{h.figure(), h.shortLarger() ? "drawn" : "not drawn"};
    }

    /** Every time chart on the page with the width of the box it sits in: {the chart's width, the box's width}, in the page's order. */
    public List<double[]> chartWidths() {
        List<double[]> out = new ArrayList<>();
        each(column, n -> {
            if (n instanceof TimeChart c) out.add(new double[]{c.getWidth(), c.getParent() instanceof Region box ? box.getWidth() : Double.NaN});
        });
        return out;
    }

    /* =====================================================================
       THE CLOCK ON EVERY RAIL SCREEN (KP7, slice D): the window's count of screens rebuilt in place, what the clock does after
       a month lands, the condition that moves the count, and the show hands of the two rail tabs the bench had none for
       ===================================================================== */

    /** How many draws rebuilt the screen on show in place - the same screen drawn again with its page emptied - since the window was made (UserInterface.rebuiltInPlace). */
    public int rebuiltInPlace() {
        return ui.rebuiltInPlace;
    }

    /** What the clock does after a month lands: the screen on show drawn again by its own redraw (UserInterface.redraw(), the call the clock's drawPaced() makes). */
    public void clockRedraw() {
        ui.redraw();
    }

    /** The name the screen on show gives the window: the one its show method passes to clearMenu() or keepMenu(). */
    public String screenShowing() {
        return ui.currentScreen;
    }

    /**
     * The condition that moves rebuiltInPlace, caused: a page drawn the old way (clearMenu() and a page put in the menu column) twice
     * under one name, then People shown through its real show method, an arrival from that name. {the moves the two old-way draws
     * made together - one, the second being the rebuild and the first an arrival - and the moves the arrival made - none}.
     */
    public int[] rebuildProbe() {
        ensureWindow();
        int start = ui.rebuiltInPlace;
        for (int i = 0; i < 2; i++) {
            ui.clearMenu("keptcheck-probe", () -> { });
            ui.rootMenu.getChildren().add(new Label("a page drawn the old way"));
        }
        int pair = ui.rebuiltInPlace - start;
        ui.peopleScreen.showPopulationInfoMenu();
        return new int[]{pair, ui.rebuiltInPlace - start - pair};
    }

    /** The pool of the screen on show, by the name it gives the window, or null for a screen this does not know (a page drawn the old way, a menu). */
    private Kept.Pool poolOnShow() {
        return switch (ui.currentScreen) {
            case "handleAllBuildingMenus" -> ui.buildScreen.pool;
            case ConstructionScreen.SCREEN -> ui.constructionScreen.pool;
            case "showLandMenu" -> ui.landScreen.pool;
            case "showPopulationInfoMenu" -> ui.peopleScreen.pool;
            case "showServicesStatsMenu" -> ui.servicesScreen.pool;
            case "showInfrastructureMenu" -> ui.infrastructureScreen.pool;
            case "showSectorMenu" -> ui.sectorScreen.pool;
            case "showGovernmentMenu" -> ui.governmentScreen.pool;
            case "showFinanceMenu" -> ui.financesScreen.pool;
            case "showBankMenu" -> ui.bankScreen.pool;
            case "showForeignMenu" -> ui.tradeScreen.pool;
            case "showPolicyMenu" -> ui.policyScreen.pool;
            case "showHistoryMenu" -> ui.historyScreen.pool;
            case "showHouseholdMenu" -> ui.peopleScreen.moneyPool;
            case "showRankingsMenu" -> ui.rankingsScreen.pool;
            default -> null;
        };
    }

    /**
     * The nodes the last kept draw of the screen on show made new and handed back: {made, kept}, or null where the screen on show draws
     * through no pool this knows. After the clock's redraw, kept above nought is the proof that the redraw ran the screen's page over its own nodes.
     */
    public int[] drawnOnShow() {
        Kept.Pool pool = poolOnShow();
        return pool == null ? null : new int[]{pool.lastMade(), pool.lastKept()};
    }

    /** The land office shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showLand() {
        ensureWindow();
        ui.landScreen.showLandMenu();
    }

    /** The Services tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showServices() {
        ensureWindow();
        ui.servicesScreen.showServicesStatsMenu();
    }

    /** The page `p` under a name of the harness's own, with `more` run after its home as well: the harness's state on top of what the page opens on (a cell open, the measures column unfolded). */
    public Page withHome(Page p, String name, Runnable more) {
        return new Page(name, p.screen, p.pool, p.draw, () -> {
            p.home.run();
            more.run();
        });
    }

    /**
     * The pointer put over the node `target` finds, the page drawn kept again (`redraw`), and the node looked at; then the pointer taken off
     * and the page drawn again (the same as hoverKept(), for a node the kinds there do not name). Null when each time the node was the same
     * node, in the look its own handler gives, and the two looks differ; else what differed. ABSENT when the page has no such node.
     */
    private String hoverLook(java.util.function.Supplier<Node> target, Runnable redraw) throws Exception {
        Node n = target.get();
        if (n == null) return ABSENT;
        hover(n, true);
        redraw.run();
        passes();
        if (target.get() != n) return "the node under the pointer was handed away or made new by the redraw";
        String over = n.getStyle();
        n.getOnMouseEntered().handle(mouse(javafx.scene.input.MouseEvent.MOUSE_ENTERED, 5, 5, javafx.scene.input.MouseButton.NONE, 0));
        if (!java.util.Objects.equals(over, n.getStyle())) return "under the pointer the draw left " + clip(over) + " where its own mouse-entered sets " + clip(n.getStyle());
        hover(n, false);
        redraw.run();
        passes();
        if (target.get() != n) return "the node the pointer left was handed away or made new by the redraw";
        String rest = n.getStyle();
        n.getOnMouseExited().handle(mouse(javafx.scene.input.MouseEvent.MOUSE_EXITED, 5, 5, javafx.scene.input.MouseButton.NONE, 0));
        if (!java.util.Objects.equals(rest, n.getStyle())) return "off the pointer the draw left " + clip(rest) + " where its own mouse-exited sets " + clip(n.getStyle());
        if (java.util.Objects.equals(over, rest)) return "the hover changes nothing in the node's style, so there is no look to keep";
        return null;
    }

    /**
     * The page's canvases as one picture, in the order the page holds them, one under another ({width, height, then the pixels},
     * KeptCheck.alike()'s form; the empty part of a narrower canvas's row is nought): what no signature reads - the Rankings
     * table's tiles and its rank line. A canvas is a few hundred pixels tall where the page is thousands, so this can be taken at
     * every choice; a snapshot of the whole page at each of the Rankings tab's choices wore out the renderer's texture pool on its
     * long list, and every snapshot after it failed (a NullPointerException in D3DTexture on the render thread, the run of 2026-11-06).
     */
    public int[] canvasPixels() {
        List<javafx.scene.canvas.Canvas> found = new ArrayList<>();
        each(column, n -> {
            if (n instanceof javafx.scene.canvas.Canvas c && c.isVisible() && c.getWidth() >= 1 && c.getHeight() >= 1) found.add(c);
        });
        List<int[]> shots = new ArrayList<>();
        int w = 0, h = 0;
        for (javafx.scene.canvas.Canvas c : found) {
            WritableImage img = c.snapshot(null, null);
            int cw = (int) img.getWidth(), ch = (int) img.getHeight();
            int[] px = new int[cw * ch];
            img.getPixelReader().getPixels(0, 0, cw, ch, PixelFormat.getIntArgbInstance(), px, 0, cw);
            shots.add(new int[]{cw, ch});
            shots.add(px);
            w = Math.max(w, cw);
            h += ch;
        }
        int[] out = new int[2 + w * h];
        out[0] = w;
        out[1] = h;
        int row = 0;
        for (int i = 0; i < shots.size(); i += 2) {
            int cw = shots.get(i)[0], ch = shots.get(i)[1];
            int[] px = shots.get(i + 1);
            for (int y = 0; y < ch; y++) System.arraycopy(px, y * cw, out, 2 + (row + y) * w, cw);
            row += ch;
        }
        return out;
    }

    /** The first measure line on the Rankings tab's measures column that is not the one shown (the shown one is lit and has no hover): an HBox with a mouse-entered and a mouse-exited. */
    private Node rankingsLine() {
        return find(column, HBox.class, h -> h.getOnMouseEntered() != null && h.getOnMouseExited() != null && h.getOnMouseClicked() != null
                && h.getStyle() != null && h.getStyle().contains("-fx-padding: 3 4 3 8; -fx-cursor: hand;"));
    }

    /** The Rankings tab's measure line under the pointer: hoverKept()'s test, on the first line that is not lit (see hoverLook()). ABSENT when the page has none. */
    public String hoverRankingsLine(Runnable redraw) throws Exception {
        return hoverLook(this::rankingsLine, redraw);
    }

    /** The table's picture (the rows are painted on canvases, with one region under them for the row the pointer is over), or null. */
    private Pane rankingsRows() {
        return find(column, Pane.class, p -> "RowsPicture".equals(p.getClass().getSimpleName()));
    }

    /**
     * The pointer moved over the first row of the Rankings table that lights under it, the page drawn kept again (`redraw`): the picture is the
     * same node and the same row is lit, with no mouse-moved sent (a pointer that has not moved sends none); then the pointer gone with no
     * mouse-exited (a page torn down swallows the exits) and the page drawn again: no row is lit. Null when so; else what differed. ABSENT
     * when the page has no table.
     */
    public String hoverRankingsRows(Runnable redraw) throws Exception {
        Pane rows = rankingsRows();
        if (rows == null) return ABSENT;
        Region lit = find(rows, Region.class, r -> r != rows && r.isMouseTransparent() && !r.isManaged());
        if (lit == null) return "the table's picture has no region for the lit row";
        hover(rows, true);
        double at = -1;
        int count = (int) Math.round(rows.getPrefHeight() / RankingsScreen.ROW_H);
        for (int i = 0; i < count && at < 0; i++) {
            double y = (i + .5) * RankingsScreen.ROW_H;
            fireMouse(rows, javafx.scene.input.MouseEvent.MOUSE_MOVED, 40, y, javafx.scene.input.MouseButton.NONE, 0);
            if (lit.isVisible()) at = lit.getLayoutY();
        }
        if (at < 0) {
            hover(rows, false);
            return "no row of the table lights under the pointer in this city (" + count + " rows), so there is no look to keep";
        }
        redraw.run();
        passes();
        if (rankingsRows() != rows) return "the table's picture under the pointer was handed away or made new by the redraw";
        if (!lit.isVisible()) return "under a still pointer the draw left the row unlit";
        if (lit.getLayoutY() != at) return "under a still pointer the draw lit the row at " + lit.getLayoutY() + " where the pointer is, at " + at;
        hover(rows, false);
        redraw.run();
        passes();
        if (rankingsRows() != rows) return "the table's picture the pointer left was handed away or made new by the redraw";
        if (lit.isVisible()) return "the pointer gone, the draw left a row lit";
        return null;
    }

    /**
     * The table's picture drawn kept once more, the city unchanged (KP2b): how many times its cursor was written by the draw, and the cursor after
     * it. A cursor set by code is reset by Kept when a node is handed back and set again by the draw, a change at each; one in the picture's style
     * is the style's, which Kept leaves alone. Null when the draw wrote it none and it is the hand; else what differed. ABSENT without a table.
     */
    public String rankingsRowsCursor(Runnable redraw) throws Exception {
        Pane rows = rankingsRows();
        if (rows == null) return ABSENT;
        passes();
        if (rows.getCursor() != javafx.scene.Cursor.HAND) return "the table's picture has not the hand before the draw (" + rows.getCursor() + ")";
        int[] writes = {0};
        javafx.beans.InvalidationListener on = o -> {
            writes[0]++;
            rows.getCursor();
        };
        rows.cursorProperty().addListener(on);
        try {
            redraw.run();
        } finally {
            rows.cursorProperty().removeListener(on);
        }
        passes();
        if (rankingsRows() != rows) return "the table's picture was handed away or made new by the redraw";
        if (writes[0] != 0) return "the draw wrote the table's cursor " + writes[0] + " times";
        if (rows.getCursor() != javafx.scene.Cursor.HAND) return "after the draw the table's cursor is " + rows.getCursor() + ", not the hand";
        return null;
    }

    /** The scroller the table's picture sits in, or null. */
    private ScrollPane rankingsScroller(Pane rows) {
        for (Node n = rows; n != null; n = n.getParent()) if (n instanceof ScrollPane s) return s;
        return null;
    }

    /**
     * The pointer over a row of the Rankings table while the page scrolls under it (KP2b): a wheel scrolls the page, the rows move under the
     * pointer, and JavaFX sends the table no mouse-moved (it re-picks at a pulse and sends entered and exited to what changed). Each row is tried
     * first for whether the band lights under a pointer on it (the city's own and the open row are raised already, a gap is no row); then the
     * pointer is put on a row that lights, a ScrollEvent is fired at the table's canvas (the scroller moves the page as a wheel does), and then the
     * page is drawn again as a month landing draws it: after each, the band is lit if the row now under the pointer lights, and on that row, and
     * not left on the row the pointer was over before the scroll. Null when so; else what differed. ABSENT without a table.
     */
    public String hoverRankingsScroll(Runnable redraw) throws Exception {
        Pane rows = rankingsRows();
        if (rows == null) return ABSENT;
        Region lit = find(rows, Region.class, r -> r != rows && r.isMouseTransparent() && !r.isManaged());
        if (lit == null) return "the table's picture has no region for the lit row";
        ScrollPane scroller = rankingsScroller(rows);
        javafx.scene.canvas.Canvas canvas = find(rows, javafx.scene.canvas.Canvas.class, c -> true);
        if (scroller == null || canvas == null) return "the table's picture has no scroller or no canvas to scroll through";
        passes();
        hover(rows, true);
        double vvalue = scroller.getVvalue();
        try {
            int count = (int) Math.round(rows.getPrefHeight() / RankingsScreen.ROW_H);
            boolean[] lights = new boolean[count];
            int first = -1;
            for (int i = 0; i < count; i++) {
                fireMouse(rows, javafx.scene.input.MouseEvent.MOUSE_MOVED, 40, rows.localToScene(0, (i + .5) * RankingsScreen.ROW_H).getY(),
                        javafx.scene.input.MouseButton.NONE, 0);
                lights[i] = lit.isVisible();
                if (lights[i] && first < 0) first = i;
            }
            if (first < 0) return "no row of the table lights under the pointer in this city (" + count + " rows), so there is no look to keep";
            // The pointer on that row, at its place in the scene (the synthetic event takes its coordinates as the scene's).
            double sceneY = rows.localToScene(0, (first + .5) * RankingsScreen.ROW_H).getY();
            fireMouse(rows, javafx.scene.input.MouseEvent.MOUSE_MOVED, 40, sceneY, javafx.scene.input.MouseButton.NONE, 0);
            double before = lit.getLayoutY();
            if (!lit.isVisible() || before != first * RankingsScreen.ROW_H) return "the fixture's premise failed: the pointer on row " + first + " did not light it";
            double top = rows.localToScene(0, 0).getY();
            // The wheel: a ScrollEvent through the canvas, bubbling to the scroller, which moves the page by the delta (down: the rows go up).
            double by = -4 * RankingsScreen.ROW_H;
            javafx.event.Event.fireEvent(canvas, new javafx.scene.input.ScrollEvent(javafx.scene.input.ScrollEvent.SCROLL, 40, sceneY, 40, sceneY,
                    false, false, false, false, true, false, 0, by, 0, by, javafx.scene.input.ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                    javafx.scene.input.ScrollEvent.VerticalTextScrollUnits.NONE, 0, 0, null));
            passes();
            double moved = top - rows.localToScene(0, 0).getY();
            if (moved < 2 * RankingsScreen.ROW_H) return "the fixture's premise failed: the wheel moved the rows by " + moved + " pixels (it was " + -by + ")";
            String scrolled = bandNow(rows, lit, sceneY, lights, before);
            redraw.run();
            passes();
            if (rankingsRows() != rows) return "the table's picture was handed away or made new by the redraw";
            String landed = bandNow(rows, lit, sceneY, lights, before);
            if (scrolled == null && landed == null) return null;
            return (scrolled == null ? "" : "after the wheel, with no month landed: " + scrolled)
                    + (scrolled != null && landed != null ? "; " : "") + (landed == null ? "" : "a month landing after the wheel: " + landed);
        } finally {
            hover(rows, false);
            scroller.setVvalue(vvalue);
            redraw.run();
            passes();
        }
    }

    /** The band against the row under a pointer at this place in the scene: null when it is lit on that row (or not lit, where that row does not light), else the difference. */
    private static String bandNow(Pane rows, Region lit, double sceneY, boolean[] lights, double before) {
        int at = (int) Math.floor(rows.sceneToLocal(0, sceneY).getY() / RankingsScreen.ROW_H);
        boolean should = at >= 0 && at < lights.length && lights[at];
        if (!should) return lit.isVisible() ? "the band is on the row at " + lit.getLayoutY() + ", and the row under the pointer (" + at + ") does not light" : null;
        if (!lit.isVisible()) return "the band is not lit, and the row under the pointer (" + at + ") lights";
        if (lit.getLayoutY() == before && before != at * RankingsScreen.ROW_H) {
            return "the band stayed on the row the pointer was over before the scroll (at " + before + "; the pointer is on row " + at + ")";
        }
        return lit.getLayoutY() == at * RankingsScreen.ROW_H ? null : "the band is on the row at " + lit.getLayoutY() + ", the pointer is on row " + at;
    }

    /* =====================================================================
       THE RANKINGS TAB (KP7, slice B): the tab as it opens, kept and built new, the hands on what reshapes it (a board, the money, the
       prices and the conversion, Now / Your age, a measure, a filter and a view, a pin, a place's drawer), the read of the saved
       cities and the real show method
       ===================================================================== */

    /**
     * The Rankings tab as a Page, as the screen draws it from its pool: it opens on GDP and the full list, Real world 2025 in the real
     * US dollar, per year and at this month's rate, the first six measures pinned, nothing folded open and no drawer. The saved cities
     * the screen has read stay (they are the folder's, not the page's).
     */
    public Page rankings() {
        RankingsScreen s = ui.rankingsScreen;
        return new Page("the Rankings tab", "showRankingsMenu", () -> s.pool, pool -> timed(pool, s::drawPage), () -> {
            s.forget();
            s.settings = ham.citybuildersim.Rankings.Settings.OPENING;
            s.measure = RankingsScreen.OPENS_ON;
            s.aroundYou = false;
            s.filter = RankingsScreen.Filter.ALL;
            s.pinned.clear();
            s.pinned.addAll(RankingsScreen.PINNED_AT_FIRST);
            s.drawer = null;
            s.browseAll = false;
            s.atAge = false;
        });
    }

    /** The measures of the file, by their key, in the file's order: the ones the tab can show. */
    public static List<String> rankingsMeasures() {
        List<String> out = new ArrayList<>();
        for (ham.citybuildersim.WorldMeasures.Measure m : ham.citybuildersim.WorldMeasures.load().measures()) out.add(m.key());
        return out;
    }

    /** Whether the measure ranks on Real world 2025 against real places (false: only the player's cities rank on it). */
    public static boolean rankingsSourced(String key) {
        ham.citybuildersim.WorldMeasures.Measure m = ham.citybuildersim.WorldMeasures.load().measure(key);
        return m != null && m.sourced();
    }

    /** The boards, by name: Real world 2025 and Your world. */
    public static List<String> rankingsBoards() {
        return enumNames(ham.citybuildersim.Rankings.Board.values());
    }

    /** The moneys, by name: the city's, the game's US dollar and the real one. */
    public static List<String> rankingsMoneys() {
        return enumNames(ham.citybuildersim.Rankings.Money.values());
    }

    /** The prices, by name: per year and static. */
    public static List<String> rankingsPrices() {
        return enumNames(ham.citybuildersim.Rankings.Prices.values());
    }

    /** The conversions, by name: this month's rate, constant 2015 and PPP. */
    public static List<String> rankingsConverts() {
        return enumNames(ham.citybuildersim.Rankings.Convert.values());
    }

    /** The filters of the table, by name: All, Nations, Cities, Your size and Your cities. */
    public static List<String> rankingsFilters() {
        return enumNames(RankingsScreen.Filter.values());
    }

    private static List<String> enumNames(Enum<?>[] values) {
        List<String> out = new ArrayList<>();
        for (Enum<?> e : values) out.add(e.name());
        return out;
    }

    /** Whether the city has nations of its own, so that Your world is a board it can show (else the screen draws Real world 2025 in its place). */
    public boolean rankingsHasNations() {
        return ui.game.getNations() != null;
    }

    /** The measure shown, by its key, as a line on the measures column or a pinned rank on the card would show it. */
    public void rankingsMeasure(String key) {
        ui.rankingsScreen.measure = key;
    }

    /** The board picked, by name, as its chip would (the money, the prices and the conversion as they are). */
    public void rankingsBoard(String board) {
        RankingsScreen s = ui.rankingsScreen;
        s.settings = new ham.citybuildersim.Rankings.Settings(ham.citybuildersim.Rankings.Board.valueOf(board), s.settings.money(), s.settings.prices(), s.settings.convert());
    }

    /** The money picked, by name, as its chip would. */
    public void rankingsMoney(String money) {
        RankingsScreen s = ui.rankingsScreen;
        s.settings = new ham.citybuildersim.Rankings.Settings(s.settings.board(), ham.citybuildersim.Rankings.Money.valueOf(money), s.settings.prices(), s.settings.convert());
    }

    /** The prices picked, by name, as its chip would (they move only the saved cities' rows, and are locked in other cases). */
    public void rankingsPrice(String prices) {
        RankingsScreen s = ui.rankingsScreen;
        s.settings = new ham.citybuildersim.Rankings.Settings(s.settings.board(), s.settings.money(), ham.citybuildersim.Rankings.Prices.valueOf(prices), s.settings.convert());
    }

    /** The conversion picked, by name, as its chip would. */
    public void rankingsConvert(String convert) {
        RankingsScreen s = ui.rankingsScreen;
        s.settings = new ham.citybuildersim.Rankings.Settings(s.settings.board(), s.settings.money(), s.settings.prices(), ham.citybuildersim.Rankings.Convert.valueOf(convert));
    }

    /** The filter picked, by name, as its chip would. */
    public void rankingsFilter(String filter) {
        ui.rankingsScreen.filter = RankingsScreen.Filter.valueOf(filter);
    }

    /** Full list or Around you, as its chip would put it. */
    public void rankingsAround(boolean around) {
        ui.rankingsScreen.aroundYou = around;
    }

    /** Your cities at Now (false) or Your age (true), as its chip would put it. */
    public void rankingsAtAge(boolean atAge) {
        ui.rankingsScreen.atAge = atAge;
    }

    /** The measures column folded to the pinned and the group shown (false) or opened on every group (true), as "Browse all" would put it. */
    public void rankingsBrowseAll(boolean all) {
        ui.rankingsScreen.browseAll = all;
    }

    /** A measure pinned or unpinned, by its key, as the pin on its line would. */
    public void rankingsPin(String key, boolean on) {
        if (on) ui.rankingsScreen.pinned.add(key);
        else ui.rankingsScreen.pinned.remove(key);
    }

    /** A place's drawer opened, by its id (a nation's or a region's in the file, "city", or a saved city's "saved:" and its slot), as its row or a neighbour on the card would; null closes it. */
    public void rankingsDrawer(String place) {
        ui.rankingsScreen.drawer = place;
    }

    /** The ids a drawer can be opened on: the city, the file's first nation and first region, and the saved cities the screen has read. */
    public List<String> rankingsDrawers() {
        List<String> out = new ArrayList<>();
        out.add(ham.citybuildersim.Rankings.PLACE_CITY);
        ham.citybuildersim.WorldMeasures.Place nation = null, region = null;
        for (ham.citybuildersim.WorldMeasures.Place p : ham.citybuildersim.WorldMeasures.load().places()) {
            if (p.kind() == ham.citybuildersim.WorldMeasures.Kind.NATION && nation == null) nation = p;
            if (p.kind() != ham.citybuildersim.WorldMeasures.Kind.NATION && region == null) region = p;
        }
        if (nation != null) out.add(nation.id());
        if (region != null) out.add(region.id());
        ham.citybuildersim.SavedCities saved = ui.rankingsScreen.saved;
        if (saved != null) for (ham.citybuildersim.Rankings.Saved sv : saved.now()) out.add(sv.id());
        return out;
    }

    /**
     * The city saved into slot 10 of the folder it plays in, so that the Your cities board has a row to read. Refused (false) for the player's own
     * save folder: only a scratch folder (GameFiles.scratch) is ever written, and only its slot 10.
     */
    public boolean rankingsSaveSlot10() {
        ham.citybuildersim.GameFiles files = ui.game.getGameFiles();
        if (files.getDirectory().equals(ham.citybuildersim.GameFiles.defaultDirectory())) return false;
        return ui.game.saveGame(10, "KP7 bench").ok;
    }

    /** A read of the saved cities started on the screen's worker, as an entry to the tab starts one: its answer comes back through the FX thread (see rankingsReadLanded()). */
    public void rankingsRead() {
        ui.rankingsScreen.readSaved();
    }

    /** Whether a read has landed: none is on the worker and the screen holds a snapshot. Asked from the FX thread, between sleeps of the caller's, never waited for on it. */
    public boolean rankingsReadLanded() {
        RankingsScreen s = ui.rankingsScreen;
        return !s.reading && s.saved != null;
    }

    /** How many saved cities the screen's snapshot puts on the board (0 before the first read has landed). */
    public int rankingsSavedOnBoard() {
        ham.citybuildersim.SavedCities saved = ui.rankingsScreen.saved;
        return saved == null ? 0 : saved.now().size();
    }

    /** The snapshot of the saved cities let go, as a tab not yet entered has it ("Your cities (reading...)" and no saved row). */
    public void rankingsUnread() {
        ui.rankingsScreen.saved = null;
    }

    /** The Rankings tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showRankings() {
        ensureWindow();
        ui.rankingsScreen.showRankingsMenu();
    }

    /** The Rankings tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. */
    public void forgetRankings() {
        ui.rankingsScreen.forget();
    }

    /* =====================================================================
       HOUSEHOLD MONEY (KP7, slice A): People's second page, kept and built new, the hands on what reshapes it (the view, a cell of
       the grid opened by its key, the city's month's statement), the page laid out narrower than the books sit beside the grid at,
       and the real show method
       ===================================================================== */

    /** Household money as it opens: no cell open, the view "One household", the city's month's statement folded. */
    public Page householdMoney() {
        return householdMoney("Household money");
    }

    /** ...under a name of the harness's own. */
    public Page householdMoney(String name) {
        PeopleScreen s = ui.peopleScreen;
        return new Page(name, "showHouseholdMenu", () -> s.moneyPool, pool -> timed(pool, s::drawHousehold), () -> {
            s.forget();
            s.openCell = "";
            s.householdPerFamily = true;
            s.revealOpened = false;
            s.detailsOpen.remove("statement");
            householdColumnWidth(0);
        });
    }

    /** The page's two views, in the strip's order. */
    public static List<String> householdViews() {
        return List.of("One household", "All of them");
    }

    /** The view picked, as the strip's chip would put it (the next draw shows it). */
    public void householdView(String view) {
        ui.peopleScreen.householdPerFamily = "One household".equals(view);
    }

    /**
     * The cells of the grid that can be opened on the city as it is, by the key the grid opens them under, in the grid's order: a
     * family's cell with anybody in it ("tier:SHAPE", the tier's number), then the rows below the rule ("OUT:" and the ledger's key -
     * the retired, the out of work, the students, the orphans, the prison).
     */
    public List<String> householdCells() {
        ham.citybuildersim.HouseholdAccounts hh = ui.game.getHouseholds();
        ham.citybuildersim.HouseholdBalance bal = ui.game.getHouseholdBalance();
        ham.citybuildersim.FamilyModel families = ui.game.getFamilies();
        List<String> out = new ArrayList<>();
        for (ham.citybuildersim.FamilyStructure shape : ham.citybuildersim.FamilyStructure.values()) {
            if (shape.isRetired()) continue;
            for (int t = 0; t < ham.citybuildersim.HouseholdAccounts.RETIRED; t++) {
                if (hh.statementFor(families, shape, ham.citybuildersim.PayTier.values()[t]).households() >= .5) out.add(t + ":" + shape.name());
            }
        }
        for (ham.citybuildersim.Household h : ui.peopleScreen.belowTheRule(bal, families)) out.add("OUT:" + h.key());
        return out;
    }

    /** A cell of the grid opened by its key (see householdCells()), or none ("" - the quiet books), as a click on it would (the next draw shows it). */
    public void householdCell(String key) {
        ui.peopleScreen.openCell = key;
        ui.peopleScreen.revealOpened = !key.isEmpty();
    }

    /** The cell the grid has open now: a draw closes one whose households have gone, so this is the answer after a draw. */
    public String householdOpenCell() {
        return ui.peopleScreen.openCell;
    }

    /** The city's month's statement ("the month as a statement") opened or closed, as a click on its toggle would. */
    public void householdStatement(boolean open) {
        fold(ui.peopleScreen.detailsOpen, "statement", open);
    }

    /**
     * The menu column laid out `width` wide (its side padding takes the rest of WIDTH) or, for 0 or less, at WIDTH again: the books
     * sit beside the grid from PeopleScreen.BESIDE and stack under it below that. The next pass lays the page out at it.
     */
    public void householdColumnWidth(double width) {
        double side = width <= 0 ? 0 : Math.max(0, (WIDTH - width) / 2);
        column.setPadding(new Insets(8, side, 14, side));
    }

    /** Whether the open cell's books sit beside the grid on the page as laid out, or null when the page has no books pane. */
    public Boolean householdBeside() {
        List<PeopleScreen.Books> found = new ArrayList<>();
        each(column, n -> {
            if (n instanceof PeopleScreen.Books b) found.add(b);
        });
        return found.isEmpty() ? null : found.get(0).beside(found.get(0).getWidth());
    }

    /** Household money shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). */
    public void showHousehold() {
        ensureWindow();
        ui.peopleScreen.showHouseholdMenu();
    }

    /** Household money forgotten, as another city opened would leave it: nothing kept (People's page too, which shares the screen's forget()). */
    public void forgetHousehold() {
        ui.peopleScreen.forget();
    }

    /* =====================================================================
       THE DRAWER'S BAR, THE CONSTRUCTION TAB AND THE SPEED'S READING (UP7)

       The bar and the tab are two boxes made once (UserInterface.drawerBarBox() and constructionTabBox()), each on a scene of its
       own, drawn by the real refreshDrawerBar() and refreshConstructionTab(inCity, open) - kept, or with every node new (the
       pools swapped for empty ones). The speed's reading is the header's label, put in a known state by swapping the pace
       the window reads for one KeptCheck drives on a clock it moves by hand.
       ===================================================================== */

    /**
     * The bar and the tab drawn kept, from their pools (the first draw makes every node): the draws' own milliseconds. The
     * construction panel is set open or folded as the tab's click leaves it, and the tab is drawn for a city on show or a game menu.
     */
    public double drawFrameKept(boolean inCity, boolean open) {
        ui.prefs.setConstructionOpen(open);
        long t = System.nanoTime();
        ui.refreshDrawerBar();
        ui.refreshConstructionTab(inCity, open);
        return (System.nanoTime() - t) / 1e6;
    }

    /** The drawer pinned or not, as its pin's click leaves it (the bar's look follows at the next draw). */
    public void framePinned(boolean pinned) {
        ui.prefs.setDrawerPinned(pinned);
    }

    /** Whether the drawer is pinned now. */
    public boolean framePinned() {
        return ui.prefs.isDrawerPinned();
    }

    /** Whether the construction panel is open now, as the tab's click leaves it. */
    public boolean frameOpen() {
        return ui.prefs.isConstructionOpen();
    }

    /** The nodes the last draws made new and handed back: {bar made, kept, tab made, kept}. */
    public int[] frameCounts() {
        return new int[]{ui.barPool.lastMade(), ui.barPool.lastKept(), ui.tabPool.lastMade(), ui.tabPool.lastKept()};
    }

    /** The bar's boxed nodes and the tab's, in order: the same objects from one kept draw to the next. */
    public List<Node> frameKids() {
        List<Node> out = new ArrayList<>(ui.drawerBar.getChildren());
        out.addAll(ui.constructionTab.getChildren());
        return out;
    }

    /** The drawer's bar, made once. */
    public Node bar() {
        return ui.drawerBar;
    }

    /** The construction tab, made once. */
    public Node tab() {
        return ui.constructionTab;
    }

    /** The bar's pin and its close, in order. */
    public Label pinLabel() {
        return (Label) ui.drawerBar.getChildren().get(0);
    }

    public Label closeLabel() {
        return (Label) ui.drawerBar.getChildren().get(1);
    }

    /** The tab's count (the kinds of building on site). */
    public Label tabCount() {
        return (Label) ui.constructionTab.getChildren().get(2);
    }

    /**
     * The bar and the tab built new in place of the kept ones - the pools swapped for empty ones, so every node is made again, as every
     * redraw did before UP7 - and laid out. restoreKeptFrame() puts the kept nodes and pools back.
     */
    public void showFreshFrame(boolean inCity, boolean open) throws Exception {
        keptBar = new ArrayList<>(ui.drawerBar.getChildren());
        keptTab = new ArrayList<>(ui.constructionTab.getChildren());
        keptFramePools = new Kept.Pool[]{ui.barPool, ui.tabPool};
        ui.barPool = new Kept.Pool();
        ui.tabPool = new Kept.Pool();
        ui.refreshDrawerBar();
        ui.refreshConstructionTab(inCity, open);
        framePasses();
    }

    /** The kept nodes and pools back where they were, and laid out. */
    public void restoreKeptFrame() throws Exception {
        ui.barPool = keptFramePools[0];
        ui.tabPool = keptFramePools[1];
        ui.drawerBar.getChildren().setAll(keptBar);
        ui.constructionTab.getChildren().setAll(keptTab);
        framePasses();
    }

    /** Both scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed. */
    public double[] framePasses() throws Exception {
        double[] a = passes(barScene, ui.drawerBar), b = passes(tabScene, ui.constructionTab);
        return new double[]{a[0] + b[0], a[1] + b[1]};
    }

    /** The bar's signature, then the tab's, a line a node (see signature()); each box itself is signed too: its style, its tooltip, its handlers. */
    public List<String> frameSignature() {
        List<String> out = new ArrayList<>();
        out.add("--- bar ---");
        sign(ui.drawerBar, "", out);
        out.add("--- tab ---");
        sign(ui.constructionTab, "", out);
        return out;
    }

    /** The bar's pixels and the tab's, each as pixels() gives them: {bar, tab}. */
    public int[][] framePixels() {
        return new int[][]{pixelsOf(ui.drawerBar), pixelsOf(ui.constructionTab)};
    }

    /** The tab's count made wrong by hand: the first label with a digit gets a "9" in front (see spoil()); what it said, or null. */
    public String spoilFrame() {
        return spoil(new ArrayList<>(ui.constructionTab.getChildren()));
    }

    /**
     * A second draw of the bar and then of the tab, the city unchanged, each with a listener on every node of it (redrawSets): what
     * they wrote. The draws are laid out before this is called.
     */
    public Sets[] frameSets(boolean inCity, boolean open) {
        Sets bar = redrawSets(ui.drawerBar, ui::refreshDrawerBar, java.util.Set.of());
        Sets tab = redrawSets(ui.constructionTab, () -> ui.refreshConstructionTab(inCity, open), java.util.Set.of());
        return new Sets[]{bar, tab};
    }

    /** The header's pace swapped for this one: a ClockPace on a clock KeptCheck moves by hand puts the speed's reading in a known state. */
    public void pace(ClockPace pace) {
        ui.pace = pace;
    }

    /** The speed ladder's rungs, in times real speed, lowest first: the index speed(int) takes. */
    public static double[] speedRungs() {
        return UserInterface.SPEEDS.clone();
    }

    /** A rung's words as the header writes them ("0.25×", "50×"). */
    public static String speedWords(int rung) {
        return UserInterface.speedLabel(rung);
    }

    /** The header's speed reading, kept: the same label from one draw to the next. */
    public Label speedLabel() {
        return ui.speedReading;
    }

    /** The one width the reading holds, as the window measures it. */
    public double speedWidth() {
        return ui.speedWidth();
    }

    /** The reading repainted in place, as the clock does between redraws. */
    public void paintSpeed() {
        ui.paintSpeed();
    }

    /** A string's width in the reading's own font (the figures' face, 11 px), as the window measures the reading's widest text. */
    public static double speedTextWidth(String text) {
        return TimeChart.textWidth(text, Palette.Fonts.monoFont(11));
    }

    /** Whether the reading's words are cut short: the text its skin draws is not the text it was given. Laid out first. */
    public boolean speedClipped() {
        Label label = ui.speedReading;
        return !(label.lookup(".text") instanceof Text drawn) || !drawn.getText().equals(label.getText());
    }

    /**
     * A label made as the reading was before UP7 - min width 34, the same style, this text - laid out alone on a scene of its own, as a
     * row lays it out: {its width, its preferred height}.
     */
    public double[] speedOldWay(String text) throws Exception {
        Label old = new Label(text);
        old.setMinWidth(34);
        old.setAlignment(Pos.CENTER);
        old.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 11px;"
                + " -fx-text-fill: " + Palette.TEXT_HEAD + ";");
        HBox row = new HBox(old);
        Scene sc = new Scene(row, 200, 40);
        ui.applyTheme(sc);
        passes(sc, row);
        return new double[]{old.getWidth(), old.prefHeight(-1)};
    }

    /** The reading's preferred height, as laid out. */
    public double speedHeight() {
        return ui.speedReading.prefHeight(-1);
    }

    /** What the reading and the nodes round it wrote while `draw` ran, by redrawSets (the header's second draw, or paintSpeed()). */
    public Sets speedSets(Runnable draw) {
        return redrawSets(ui.speedReading, draw, java.util.Set.of());
    }

    /** The x of the header's i-th child, as laid out. */
    public double headerChildX(int i) {
        return ui.header.getChildren().get(i).getBoundsInParent().getMinX();
    }
}
