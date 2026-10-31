# KeptBench.java - 684 lines · 61 methods · 5 constants · interface

`ham/citybuildersim/ui/KeptBench.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The People page on a scene of its own - no stage, no window - drawn kept and built new, for
> KeptCheck (study UP4): the harness's hands on the page, since what it reaches is the ui
> package's own. Since UP2 the window's chrome too: the header at the window's width and the rail
> at its height, each on a scene of its own, drawn by the real refreshHeader() and
> refreshTabRail() - kept, or with every node new (the pools swapped for empty ones, as a rebuild
> made them).
> 
> <p>WHY. A kept page is right only if every month's redraw over last month's nodes ends where
> a page built new from the same city would; the one way to know is to build both and compare
> them, node by node and pixel by pixel. The window cannot be used for it: it opens maximised,
> and the suite runs on the PC beside Jerus's game. A Scene with no stage takes CSS, layout and
> a snapshot all the same, so the bench lays the page out there, at the menu column's width.
> The FX toolkit must be up and every call made on its thread; KeptCheck sees to both.

**Uses:** [Kept](Kept.md) (24), [UserInterface](UserInterface.md) (8), [Palette](Palette.md) (4), [Game](Game.md) (1), [SummaryScreen](SummaryScreen.md) (1)

**Used by (1):** [KeptCheck](KeptCheck.md)

## Sections

| line | section |
|---:|---|
| 188 | THE CHROME (UP2): the header and the rail, kept and built new |
| 386 | THE THREE PANELS (UP2, slice 2b): the city panel, the construction panel, the inbox's list |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 55 | `KeptBench.WIDTH` | `1456` | The width the page is laid out at: the menu column's in a 1600-pixel window (the study's renders). |
| 63 | `KeptBench.HEADER_WIDTH` | `1600` | The window's width in KeptCheck's pictures: the header is laid out across it. |
| 66 | `KeptBench.RAIL_HEIGHT` | `900` | The window's height: the rail is laid out down it. |
| 73 | `KeptBench.PANEL_HEIGHT` | `900` | The three panels' scenes (UP2, slice 2b): the city panel's 290 wide, the construction panel's 280, the inbox list's. |
| 627 | `KeptBench.PRECISE` | `Boolean.getBoolean("keptbench.precise")` | Whether a signature writes a node's place and size to four decimals instead of one (-Dkeptbench.precise=true). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private final UserInterface ui` |  |
| 58 | `private final VBox column` |  |
| 59 | `private final Scene scene` |  |
| 60 | `private List<Node> kept` |  |
| 68 | `private final Scene headerScene, railScene` |  |
| 69 | `private List<Node> keptHeader` |  |
| 70 | `private Kept.Pool[] keptPools` |  |
| 74 | `private final Scene cityScene, sitesScene, listScene` |  |
| 75 | `private List<Node> keptCity` |  |
| 76 | `private Kept.Pool[] keptPanelPools` |  |
| 569 | `private static Method doCSSPass, doLayoutPass` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 633 | **type** `public final class KeptBench` | The People page on a scene of its own - no stage, no window - drawn kept and built new, for KeptCheck (study UP4): the harness's hands on the page, since what it reaches is the ui package's own. |
| 79 | 29 | `public KeptBench(Game game)` | The bench on a city: the window's object with no stage, its menu column in a scene of its own. |
| 110 | 5 | `public double drawKept()` | People's page drawn kept from its pool (the first draw makes every node): the draw's own milliseconds. |
| 117 | 3 | `public int lastMade()` | The nodes the last kept draw made new. |
| 122 | 3 | `public int lastKept()` | The nodes the last kept draw handed back. |
| 127 | 3 | `public Node top()` | The page's top node, kept or not: the same object from one kept draw to the next. |
| 135 | 8 | `public double[] showFresh() throws Exception` | The page built new in place of the kept one (no pool, every node new) and laid out, as every redraw was before UP4: {its draw ms, CSS ms, layout ms}. |
| 145 | 4 | `public void restoreKept() throws Exception` | The kept page back in the column, as it was, and laid out. |
| 155 | 3 | `public String spoil()` | One figure on the page made wrong: the first label whose words hold a digit gets a "9" in front (KeptCheck's proof that its comparison sees one wrong figure, and that the next kept draw sets it right). |
| 159 | 17 | `private static String spoil(List<Node> nodes)` |  |
| 178 | 4 | `public void details(String name, boolean open)` | A "details" fold opened or closed, as its toggle would (the next draw shows it). |
| 184 | 3 | `public void yearView(boolean on)` | The month card's year view on or off. |

### THE CHROME (UP2): the header and the rail, kept and built new (lines 188-385)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 8 | `public double drawChromeKept()` | The header and the rail drawn kept, from their pools (the first draw makes every node): the draw's own milliseconds. |
| 211 | 4 | `private void keepMiniMapOff()` | The minimap's node hidden (never removed: it stays in the rail where the rail puts it). |
| 217 | 5 | `public double drawEnvelope()` | The envelope drawn on its own, as a notice read elsewhere draws it (refreshInboxButton()). |
| 224 | 4 | `public int[] chromeCounts()` | The nodes the last draws of each pool made new and handed back: {header made, kept, envelope made, kept, rail made, kept}. |
| 230 | 5 | `public List<Node> chromeTops()` | The header's top nodes and the rail's, in order: the same objects from one kept draw to the next. |
| 241 | 14 | `public double[] showFreshChrome() throws Exception` | The chrome built new in place of the kept one - the pools swapped for empty ones, so every node is made again, as every redraw did before UP2 - and laid out: {its draw ms, CSS ms, layout ms}. |
| 257 | 12 | `public void restoreKeptChrome() throws Exception` | The kept chrome back where it was, with its pools, and laid out. |
| 275 | 6 | `public double[] chromePasses() throws Exception` | Both chrome scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed. |
| 282 | 8 | `private static void finishAnimations(Node n)` |  |
| 292 | 8 | `public List<String> chromeSignature()` | The header's signature, then the rail's, a line a node (see signature()). |
| 302 | 3 | `public int[][] chromePixels()` | The header's pixels and the rail's, each as pixels() gives them: {header, rail}. |
| 306 | 9 | `private static int[] pixelsOf(Node root)` |  |
| 317 | 3 | `public String spoilHeader()` | One figure in the header made wrong: the first label with a digit gets a "9" in front (see spoil()). |
| 322 | 4 | `public void clock(boolean running, String pausedBecause)` | The clock as the header shows it: running or stopped, and the line saying why it stopped itself (null for none). |
| 328 | 3 | `public void speed(int rung)` | The speed's rung (an index into the ladder, 0 to 8). |
| 333 | 3 | `public void screen(String name)` | The screen on show, by the name its clearMenu() call gives: the rail lights its tab; a game menu hides the rail and the clock. |
| 338 | 6 | `public boolean focusPlay()` | A node of the header's that can take the focus (the play button), given it; whether the scene has it. |
| 345 | 11 | `private static Node firstButton(Node n)` |  |
| 358 | 6 | `public boolean headerHasFocus()` | Whether the focus is on a node inside the header (the header itself is the scene's root here, where the draw puts the focus). |
| 370 | 15 | `public static boolean nestedDrawsKeepEach()` | A draw of one pool inside a draw of another (the header's draw reaches the envelope's) gives each its own nodes: the outer's three are handed back to it at the next round, the inner's one to the inner pool - drawn on ... |

### THE THREE PANELS (UP2, slice 2b): the city panel, the construction panel, the inbox's list (lines 386-684)

| line | len | member | says |
|---:|---:|---|---|
| 391 | 7 | `public double drawPanelsKept()` | The three drawn kept, from their pools (the first draw makes every node): the draws' own milliseconds. |
| 400 | 4 | `public int[] panelCounts()` | The nodes the last draws of the three made new and handed back: {city made, kept, construction made, kept, list made, kept}. |
| 406 | 6 | `public List<Node> panelTops()` | The panels' top nodes, in order: the same objects from one kept draw to the next. |
| 418 | 16 | `public double[] showFreshPanels() throws Exception` | The three built new in place of the kept ones - their pools swapped for empty ones, so every node is made again, as every redraw did before UP2 - and laid out: {draw ms, CSS ms, layout ms}. |
| 436 | 9 | `public void restoreKeptPanels() throws Exception` | The kept panels back where they were, with their pools, and laid out. |
| 447 | 4 | `public double[] panelPasses() throws Exception` | The three scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed. |
| 453 | 10 | `public List<String> panelSignature()` | The three panels' signatures, one after the other, a line a node (see signature()). |
| 465 | 3 | `public int[][] panelPixels()` | The three panels' pixels, each as pixels() gives them: {city, construction, inbox list}. |
| 470 | 3 | `public String spoilPanel()` | One figure in the city panel made wrong: the first label with a digit gets a "9" in front (see spoil()). |
| 475 | 4 | `public void cityMode(boolean dashboard, boolean needsView)` | The city panel's mode: Dashboard or Summary, and whether the header's chip opened it on NEEDS YOU (which draws Summary). |
| 481 | 4 | `public void section(String key, boolean open)` | One dashboard section opened or folded, as its header's click would. |
| 487 | 4 | `public void sectionsAll(boolean open)` | "Open all" or "close all", as the dashboard's links do. |
| 493 | 4 | `public void drawer(boolean open, boolean pinned)` | The drawer open or shut (the header's chip), pinned or not: the chip's look follows, the panel's nodes do not. |
| 499 | 4 | `public void inboxList(boolean open, String expandedKey)` | The inbox's list dropped down or put away, and which notice is unfolded ("" for none). |
| 505 | 7 | `public Node firstHoverInCityPanel()` | The first node of the city panel with a hover look given by code (a summary row), or null. |
| 513 | 11 | `private static Node firstHover(Node n)` |  |
| 526 | 6 | `public boolean focusInboxButton()` | The inbox list's action button (an unfolded notice's), given the focus; whether the scene has it. |
| 534 | 6 | `public boolean inboxHasFocus()` | Whether the focus is on a node inside the inbox list (the box itself is the scene's root here, where the draw puts the focus). |
| 542 | 4 | `public ScrollPane cityScroller()` | The city panel's scroller (kept: the same object from one draw to the next). |
| 548 | 3 | `public void forgetChrome()` | Every kept node of the chrome let go, as anotherCity() lets them go: the next draw makes them all new. |
| 553 | 3 | `public Node headerChild(int i)` | The header's i-th child (the clock, the money block, the row of tiles, the rating and inbox). |
| 558 | 3 | `public Node railChild(int i)` | The rail's i-th child (a destination's button, or the gap, the minimap, Menu). |
| 563 | 5 | `public static void hover(Node n, boolean on) throws ReflectiveOperationException` | The pointer put over a node, or taken off it, as the scene's own mouse handling does (Node.setHover). |
| 576 | 3 | `public double[] passes() throws Exception` | The scene's CSS and layout, as a frame's pulse runs them (Scene.doCSSPass and doLayoutPass, which touch only what is dirty): {CSS ms, layout ms}. |
| 580 | 22 | `private static double[] passes(Scene sc, javafx.scene.Parent root) throws Exception` |  |
| 604 | 9 | `public int[] pixels()` | The page as pixels, ARGB, row by row: the menu column's snapshot, with its width and height first. |
| 620 | 5 | `public List<String> signature()` | The page as it is now, a line a node in the order its code made them: the node's class; its words; its inline style; the values code set on it (the styleable ones whose origin is code); whether it has a click, a hover... |
| 629 | 25 | `private static void sign(Node n, String path, List<String> out)` |  |
| 661 | 12 | `private static String codeSet(Styleable n)` | Every styleable value a node has now, by property, where it is not the property's initial value: what code set and what its style made of it, both pages styled and laid out alike. |
| 675 | 9 | `private static String shape(Node n)` | A shape's own geometry, which no style sets. |

