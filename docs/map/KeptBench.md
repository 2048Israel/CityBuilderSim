# KeptBench.java - 1,122 lines · 117 methods · 6 constants · interface

`ham/citybuildersim/ui/KeptBench.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The People page on a scene of its own - no stage, no window - drawn kept and built new, for
> KeptCheck (study UP4): the harness's hands on the page, since what it reaches is the ui
> package's own. Since UP2 the window's chrome too: the header at the window's width and the rail
> at its height, each on a scene of its own, drawn by the real refreshHeader() and
> refreshTabRail() - kept, or with every node new (the pools swapped for empty ones, as a rebuild
> made them). Since KP1 City History too, on the same scene: its page drawn kept and built new, the
> hands on what reshapes it, the pointer on its big chart, and the real show methods for what the clock decides.
> 
> <p>WHY. A kept page is right only if every month's redraw over last month's nodes ends where
> a page built new from the same city would; the one way to know is to build both and compare
> them, node by node and pixel by pixel. The window cannot be used for it: it opens maximised,
> and the suite runs on the PC beside Jerus's game. A Scene with no stage takes CSS, layout and
> a snapshot all the same, so the bench lays the page out there, at the menu column's width.
> The FX toolkit must be up and every call made on its thread; KeptCheck sees to both.

**Uses:** [Kept](Kept.md) (37), [UserInterface](UserInterface.md) (10), [TimeChart](TimeChart.md) (10), [HistoryScreen](HistoryScreen.md) (5), [Palette](Palette.md) (4), [Pieces](Pieces.md) (4), [YearBook](YearBook.md) (2), [Game](Game.md) (1), [SummaryScreen](SummaryScreen.md) (1), [ChartModel](ChartModel.md) (1)

**Used by (1):** [KeptCheck](KeptCheck.md)

## Sections

| line | section |
|---:|---|
| 190 | THE CHROME (UP2): the header and the rail, kept and built new |
| 388 | THE THREE PANELS (UP2, slice 2b): the city panel, the construction panel, the inbox's list |
| 571 | CITY HISTORY (KP1): the page kept and built new, and the hands on its choices |
| 764 | · the hands on what reshapes the page |
| 910 | · the pointer on the big chart |
| 934 | · the real show methods, for what the clock decides (the count-up) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `KeptBench.WIDTH` | `1456` | The width the page is laid out at: the menu column's in a 1600-pixel window (the study's renders). |
| 65 | `KeptBench.HEADER_WIDTH` | `1600` | The window's width in KeptCheck's pictures: the header is laid out across it. |
| 68 | `KeptBench.RAIL_HEIGHT` | `900` | The window's height: the rail is laid out down it. |
| 75 | `KeptBench.PANEL_HEIGHT` | `900` | The three panels' scenes (UP2, slice 2b): the city panel's 290 wide, the construction panel's 280, the inbox list's. |
| 705 | `KeptBench.SETTLE_PASSES` | `8` | The most layout passes settle() runs. |
| 1065 | `KeptBench.PRECISE` | `Boolean.getBoolean("keptbench.precise")` | Whether a signature writes a node's place and size to four decimals instead of one (-Dkeptbench.precise=true). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 59 | `private final UserInterface ui` |  |
| 60 | `private final VBox column` |  |
| 61 | `private final Scene scene` |  |
| 62 | `private List<Node> kept` |  |
| 70 | `private final Scene headerScene, railScene` |  |
| 71 | `private List<Node> keptHeader` |  |
| 72 | `private Kept.Pool[] keptPools` |  |
| 76 | `private final Scene cityScene, sitesScene, listScene` |  |
| 77 | `private List<Node> keptCity` |  |
| 78 | `private Kept.Pool[] keptPanelPools` |  |
| 575 | `private List<Node> keptHistory` |  |
| 576 | `private Kept.Pool keptHistoryPool` |  |
| 578 | `private Kept.Pool[] keptChipPools` | What a page built new took from the charts the kept page holds: their pools, their chips, and where they sat. |
| 579 | `private List<List<Node>> keptLegend` |  |
| 580 | `private javafx.scene.Parent[] chartParents` |  |
| 581 | `private int[] chartAt` |  |
| 734 | `private int graphicMoves` |  |
| 1007 | `private static Method doCSSPass, doLayoutPass` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 1069 | **type** `public final class KeptBench` | The People page on a scene of its own - no stage, no window - drawn kept and built new, for KeptCheck (study UP4): the harness's hands on the page, since what it reaches is the ui package's own. |
| 81 | 29 | `public KeptBench(Game game)` | The bench on a city: the window's object with no stage, its menu column in a scene of its own. |
| 112 | 5 | `public double drawKept()` | People's page drawn kept from its pool (the first draw makes every node): the draw's own milliseconds. |
| 119 | 3 | `public int lastMade()` | The nodes the last kept draw made new. |
| 124 | 3 | `public int lastKept()` | The nodes the last kept draw handed back. |
| 129 | 3 | `public Node top()` | The page's top node, kept or not: the same object from one kept draw to the next. |
| 137 | 8 | `public double[] showFresh() throws Exception` | The page built new in place of the kept one (no pool, every node new) and laid out, as every redraw was before UP4: {its draw ms, CSS ms, layout ms}. |
| 147 | 4 | `public void restoreKept() throws Exception` | The kept page back in the column, as it was, and laid out. |
| 157 | 3 | `public String spoil()` | One figure on the page made wrong: the first label whose words hold a digit gets a "9" in front (KeptCheck's proof that its comparison sees one wrong figure, and that the next kept draw sets it right). |
| 161 | 17 | `private static String spoil(List<Node> nodes)` |  |
| 180 | 4 | `public void details(String name, boolean open)` | A "details" fold opened or closed, as its toggle would (the next draw shows it). |
| 186 | 3 | `public void yearView(boolean on)` | The month card's year view on or off. |

### THE CHROME (UP2): the header and the rail, kept and built new (lines 190-387)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 8 | `public double drawChromeKept()` | The header and the rail drawn kept, from their pools (the first draw makes every node): the draw's own milliseconds. |
| 213 | 4 | `private void keepMiniMapOff()` | The minimap's node hidden (never removed: it stays in the rail where the rail puts it). |
| 219 | 5 | `public double drawEnvelope()` | The envelope drawn on its own, as a notice read elsewhere draws it (refreshInboxButton()). |
| 226 | 4 | `public int[] chromeCounts()` | The nodes the last draws of each pool made new and handed back: {header made, kept, envelope made, kept, rail made, kept}. |
| 232 | 5 | `public List<Node> chromeTops()` | The header's top nodes and the rail's, in order: the same objects from one kept draw to the next. |
| 243 | 14 | `public double[] showFreshChrome() throws Exception` | The chrome built new in place of the kept one - the pools swapped for empty ones, so every node is made again, as every redraw did before UP2 - and laid out: {its draw ms, CSS ms, layout ms}. |
| 259 | 12 | `public void restoreKeptChrome() throws Exception` | The kept chrome back where it was, with its pools, and laid out. |
| 277 | 6 | `public double[] chromePasses() throws Exception` | Both chrome scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed. |
| 284 | 8 | `private static void finishAnimations(Node n)` |  |
| 294 | 8 | `public List<String> chromeSignature()` | The header's signature, then the rail's, a line a node (see signature()). |
| 304 | 3 | `public int[][] chromePixels()` | The header's pixels and the rail's, each as pixels() gives them: {header, rail}. |
| 308 | 9 | `private static int[] pixelsOf(Node root)` |  |
| 319 | 3 | `public String spoilHeader()` | One figure in the header made wrong: the first label with a digit gets a "9" in front (see spoil()). |
| 324 | 4 | `public void clock(boolean running, String pausedBecause)` | The clock as the header shows it: running or stopped, and the line saying why it stopped itself (null for none). |
| 330 | 3 | `public void speed(int rung)` | The speed's rung (an index into the ladder, 0 to 8). |
| 335 | 3 | `public void screen(String name)` | The screen on show, by the name its clearMenu() call gives: the rail lights its tab; a game menu hides the rail and the clock. |
| 340 | 6 | `public boolean focusPlay()` | A node of the header's that can take the focus (the play button), given it; whether the scene has it. |
| 347 | 11 | `private static Node firstButton(Node n)` |  |
| 360 | 6 | `public boolean headerHasFocus()` | Whether the focus is on a node inside the header (the header itself is the scene's root here, where the draw puts the focus). |
| 372 | 15 | `public static boolean nestedDrawsKeepEach()` | A draw of one pool inside a draw of another (the header's draw reaches the envelope's) gives each its own nodes: the outer's three are handed back to it at the next round, the inner's one to the inner pool - drawn on ... |

### THE THREE PANELS (UP2, slice 2b): the city panel, the construction panel, the inbox's list (lines 388-570)

| line | len | member | says |
|---:|---:|---|---|
| 393 | 7 | `public double drawPanelsKept()` | The three drawn kept, from their pools (the first draw makes every node): the draws' own milliseconds. |
| 402 | 4 | `public int[] panelCounts()` | The nodes the last draws of the three made new and handed back: {city made, kept, construction made, kept, list made, kept}. |
| 408 | 6 | `public List<Node> panelTops()` | The panels' top nodes, in order: the same objects from one kept draw to the next. |
| 420 | 16 | `public double[] showFreshPanels() throws Exception` | The three built new in place of the kept ones - their pools swapped for empty ones, so every node is made again, as every redraw did before UP2 - and laid out: {draw ms, CSS ms, layout ms}. |
| 438 | 9 | `public void restoreKeptPanels() throws Exception` | The kept panels back where they were, with their pools, and laid out. |
| 449 | 4 | `public double[] panelPasses() throws Exception` | The three scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed. |
| 455 | 10 | `public List<String> panelSignature()` | The three panels' signatures, one after the other, a line a node (see signature()). |
| 467 | 3 | `public int[][] panelPixels()` | The three panels' pixels, each as pixels() gives them: {city, construction, inbox list}. |
| 472 | 3 | `public String spoilPanel()` | One figure in the city panel made wrong: the first label with a digit gets a "9" in front (see spoil()). |
| 477 | 4 | `public void cityMode(boolean dashboard, boolean needsView)` | The city panel's mode: Dashboard or Summary, and whether the header's chip opened it on NEEDS YOU (which draws Summary). |
| 483 | 4 | `public void section(String key, boolean open)` | One dashboard section opened or folded, as its header's click would. |
| 489 | 4 | `public void sectionsAll(boolean open)` | "Open all" or "close all", as the dashboard's links do. |
| 495 | 4 | `public void drawer(boolean open, boolean pinned)` | The drawer open or shut (the header's chip), pinned or not: the chip's look follows, the panel's nodes do not. |
| 501 | 4 | `public void inboxList(boolean open, String expandedKey)` | The inbox's list dropped down or put away, and which notice is unfolded ("" for none). |
| 507 | 7 | `public Node firstHoverInCityPanel()` | The first node of the city panel with a hover look given by code (a summary row), or null. |
| 515 | 11 | `private static Node firstHover(Node n)` |  |
| 528 | 6 | `public boolean focusInboxButton()` | The inbox list's action button (an unfolded notice's), given the focus; whether the scene has it. |
| 536 | 6 | `public boolean inboxHasFocus()` | Whether the focus is on a node inside the inbox list (the box itself is the scene's root here, where the draw puts the focus). |
| 544 | 4 | `public ScrollPane cityScroller()` | The city panel's scroller (kept: the same object from one draw to the next). |
| 550 | 3 | `public void forgetChrome()` | Every kept node of the chrome let go, as anotherCity() lets them go: the next draw makes them all new. |
| 555 | 3 | `public Node headerChild(int i)` | The header's i-th child (the clock, the money block, the row of tiles, the rating and inbox). |
| 560 | 3 | `public Node railChild(int i)` | The rail's i-th child (a destination's button, or the gap, the minimap, Menu). |
| 565 | 5 | `public static void hover(Node n, boolean on) throws ReflectiveOperationException` | The pointer put over a node, or taken off it, as the scene's own mouse handling does (Node.setHover). |

### CITY HISTORY (KP1): the page kept and built new, and the hands on its choices (lines 571-763)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 4 | `private void historyStage()` | City History's stage: the screen's name (its scroller is filed under it), the first-visit preset (showHistoryMenu() hands it over, and the bench draws the page itself), the window's scroller the page's height reads, a... |
| 594 | 12 | `private void historyEnsure()` | ...without naming the screen on show: the real show methods name it themselves, and arriving is not being there. |
| 608 | 6 | `public double drawHistoryKept()` | City History drawn kept from its pool (the first draw makes every node): the draw's own milliseconds. |
| 616 | 3 | `public int[] historyCounts()` | The nodes the last kept draw of City History made new and handed back: {made, kept}. |
| 621 | 3 | `public List<Node> historyTop()` | The page's top nodes (the frame and its scroller), kept or not: the same objects from one kept draw to the next. |
| 626 | 5 | `public List<Node> historyCharts()` | The charts the page holds, for scene and parent checks: the big chart, then the two pins' (those made). |
| 638 | 32 | `public double[] showFreshHistory() throws Exception` | City History built new in place of the kept page - its pool and the big chart's chips' pool swapped for empty ones, so every node is made again, as every redraw was before KP1 - and laid out: {its draw ms, CSS ms, lay... |
| 672 | 17 | `public void restoreKeptHistory() throws Exception` | The kept page back in the column with its pool, the charts back in the boxes that held them, and laid out. |
| 694 | 9 | `public void settle() throws Exception` | The page laid out again, as the next frames would, until a pass moves nothing (at most SETTLE_PASSES): a wrapped label's height settles over a pass or two, and the scroller's under the frame a pass after that. |
| 708 | 3 | `public String spoilHistory()` | One figure on City History made wrong: the first label whose words hold a digit gets a "9" in front (see spoil()). |
| 713 | 3 | `public void finishPageAnimations()` | Every animation a draw started on the page run to its end, as a pulse would have: a scene with no window has none. |
| 717 | 7 | `private static void finishOne(Node n)` |  |
| 726 | 7 | `private static void each(Node n, java.util.function.Consumer<Node> f)` | `f` on a node and everything under it, through the scroller's content and a label's graphic too. |
| 737 | 10 | `public int watchGraphics()` | Every graphic of a label on the page now watched for entering or leaving the scene: how many are watched. |
| 749 | 5 | `public int graphicMoves()` | How many times a watched graphic left or entered the scene since this was last asked. |
| 756 | 7 | `private<T extends Node> T first(Class<T> type)` | The first node of this class under the page, or null. |

### the hands on what reshapes the page (lines 764-909)

| line | len | member | says |
|---:|---:|---|---|
| 767 | 5 | `public void historyPick(String key, boolean on)` | A line ticked or unticked, as its chip would (the next draw shows it). |
| 774 | 5 | `public void historyPickOnly(String...keys)` | Exactly these lines drawn, none hidden (what "clear all" and a preset leave). |
| 781 | 3 | `public static int historyPresets()` | How many presets there are. |
| 786 | 3 | `public void historyPreset(int i)` | The i-th preset pressed, as its chip would (the lines it names, none hidden). |
| 791 | 3 | `public String historyGroupOf(String key)` | The picker's group (by name) a line is in. |
| 796 | 3 | `public void historyGroup(String group, boolean open)` | A picker group opened or closed, as its heading would. |
| 801 | 6 | `public boolean historyType(String text)` | Typed into the picker's filter box, as a key would (it refills the groups in place); false when the page has no box. |
| 809 | 4 | `public void historyFold(String key, boolean open)` | A fold of the page opened or closed ("hard times", "goods"), as its toggle would. |
| 815 | 4 | `public String historyLog(boolean on)` | The big chart on a log scale or off, as the chip would; why it cannot be drawn, or null when it can. |
| 821 | 3 | `public void historyLayers(boolean on)` | Real GDP's layers on or off, as the chip would. |
| 826 | 7 | `public boolean historyLegendClick(int i)` | The legend chip i of the big chart clicked, as the pointer would (it hides or shows the line); false when it has none. |
| 835 | 4 | `public List<Node> historyLegend()` | The big chart's legend chips, as they are now. |
| 841 | 3 | `public static Object tooltipOf(Node n)` | The tooltip installed on a node (the object, to see it is the same one from month to month), or null. |
| 846 | 4 | `public List<Node> historyRanges()` | The big chart's range buttons, as they are now. |
| 852 | 3 | `public int historyHidden()` | How many lines the legend has switched off. |
| 857 | 3 | `public void historyWindow(double from, double to)` | The chart's window moved to these months, as a drag or an episode's row would. |
| 862 | 3 | `public void historyRange(int months)` | The chart's window on one of its ranges (a number of months, as ChartModel.RANGES gives them, or ChartModel.ALL), as a range button would. |
| 867 | 3 | `public int[] historyWindow()` | The window as it is: {first month shown, last month shown}. |
| 872 | 4 | `public int[] historyBounds()` | The history's ends: {first month, last month}. |
| 878 | 6 | `public double[] historyEpisodeSpan()` | The first named episode's span with a year either side, as moveChartTo(episode) would put the window, or null when the city has none. |
| 886 | 3 | `public void historyPin(String key)` | A line pinned, as its chip would (it writes the city's scratch preferences). |
| 891 | 3 | `public void historyUnpin(boolean left)` | A pin taken off, as its "unpin" would. |
| 896 | 3 | `public String historyPinned(boolean left)` | The line on a pin. |
| 901 | 3 | `public void historyFull(boolean on)` | The chart's full screen on or off, as its button would (the page is drawn by the caller). |
| 906 | 3 | `public void historyBook(String said)` | The year book's card shown (with this said) or put away (null), without writing a book. |

### the pointer on the big chart (lines 910-933)

| line | len | member | says |
|---:|---:|---|---|
| 913 | 7 | `public boolean historyHoverPlot()` | The pointer moved over the big chart's plot, as the toolkit would: a MOUSE_MOVED fired on the canvas; whether the chart now shows its crosshair. |
| 922 | 4 | `public boolean historyHovering()` | Whether the big chart shows its crosshair. |
| 927 | 6 | `private static void fireMouse(Node target, javafx.event.EventType<javafx.scene.input.MouseEvent> type, double x, double y, java...` |  |

### the real show methods, for what the clock decides (the count-up) (lines 934-1122)

| line | len | member | says |
|---:|---:|---|---|
| 937 | 3 | `public static double[] speedLadder()` | The speed ladder, as the clock steps along it. |
| 942 | 3 | `public static double countUpTop()` | The fastest speed a figure still counts up at. |
| 947 | 3 | `public void showPeople()` | People shown, as the rail's tab or a month landing shows it: the real show method (the chrome, then the page kept). |
| 952 | 4 | `public void showHistory()` | City History shown, as the rail's tab or a month landing shows it. |
| 958 | 3 | `public void forgetPeople()` | People forgotten, as another city opened would leave it: nothing kept, nothing just landed. |
| 963 | 3 | `public void forgetHistory()` | City History forgotten, as another city opened would leave it. |
| 968 | 4 | `public javafx.animation.Animation peopleCount()` | The count-up last started on LIVING HERE's figure on People, or null. |
| 974 | 4 | `public javafx.animation.Animation pinCount()` | The count-up last started on the left pin's figure on City History, or null. |
| 979 | 9 | `private Label livingHere()` |  |
| 990 | 4 | `public boolean headcountMoved()` | Whether last month's headcount differs from this month's, as LIVING HERE's count-up reads them (it counts nothing from the same figure). |
| 996 | 3 | `public void historyPinLeft(String key)` | The line on the left pin set directly (the preferences are not written): a line that moves most months, for the count-up. |
| 1001 | 5 | `public String[] pinFigures()` | The left pin's figure on the page now, and the figure the model gives it now: they differ when a month has landed that the page has not drawn. |
| 1014 | 3 | `public double[] passes() throws Exception` | The scene's CSS and layout, as a frame's pulse runs them (Scene.doCSSPass and doLayoutPass, which touch only what is dirty): {CSS ms, layout ms}. |
| 1018 | 22 | `private static double[] passes(Scene sc, javafx.scene.Parent root) throws Exception` |  |
| 1042 | 9 | `public int[] pixels()` | The page as pixels, ARGB, row by row: the menu column's snapshot, with its width and height first. |
| 1058 | 5 | `public List<String> signature()` | The page as it is now, a line a node in the order its code made them: the node's class; its words; its inline style; the values code set on it (the styleable ones whose origin is code); whether it has a click, a hover... |
| 1067 | 25 | `private static void sign(Node n, String path, List<String> out)` |  |
| 1099 | 12 | `private static String codeSet(Styleable n)` | Every styleable value a node has now, by property, where it is not the property's initial value: what code set and what its style made of it, both pages styled and laid out alike. |
| 1113 | 9 | `private static String shape(Node n)` | A shape's own geometry, which no style sets. |

