# KeptBench.java - 5,603 lines · 586 methods · 13 constants · interface

`ham/citybuildersim/ui/KeptBench.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> The People page on a scene of its own - no stage, no window - drawn kept and built new, for
> KeptCheck (study UP4): the harness's hands on the page, since what it reaches is the ui
> package's own. Since UP2 the window's chrome too: the header at the window's width and the rail
> at its height, each on a scene of its own, drawn by the real refreshHeader() and
> refreshTabRail() - kept, or with every node new (the pools swapped for empty ones, as a rebuild
> made them). Since KP1 City History too, on the same scene: its page drawn kept and built new, the
> hands on what reshapes it, the pointer on its big chart, and the real show methods for what the clock decides.
> Since KP2 Infrastructure, Services and the land office too, as {@link Page}s: one small shape for the three, each drawn
> kept from its screen's pool and built new from an empty one, with the hands on what reshapes them, on the fare dial and
> City History's filter box (the controls) and on the nodes a pointer styles (the hover).
> Since KP3 the Build screen (the Overview and every category, with the hands on a ring, an order dialled by a card's
> stepper, a stat card pinned and automatic building), the pages a build is stopped on, the funding and fell-short pages,
> and the construction page (its tabs, a building picked to demolish, the find box), as Pages of the same shape.
> Since KP4 the Sectors tab: the list and a business's five pages in both views as Pages, with the hands on a business, a page,
> the view, Show more, Freight's divisions, the folds and a statement's notes and columns, the pictures the pages hold (the refinery's,
> a materials industry's, a leg's bar), the figures that count up and the waterfall and ring that grow and sweep, the oil reserve's
> dials, and the card, the door and the crumb the pointer rests on.
> Since UP7 the drawer's pinned bar and the construction tab too, each a box made once on a scene of its own and drawn by the real
> refreshDrawerBar() and refreshConstructionTab() (kept, and built new), and the header's speed reading, its pace swapped for one on a
> clock the harness moves by hand.
> 
> <p>WHY. A kept page is right only if every month's redraw over last month's nodes ends where
> a page built new from the same city would; the one way to know is to build both and compare
> them, node by node and pixel by pixel. The window cannot be used for it: it opens maximised,
> and the suite runs on the PC beside Jerus's game. A Scene with no stage takes CSS, layout and
> a snapshot all the same, so the bench lays the page out there, at the menu column's width.
> The FX toolkit must be up and every call made on its thread; KeptCheck sees to both.

**Uses:** [Kept](Kept.md) (134), [Pieces](Pieces.md) (51), [FinancesScreen](FinancesScreen.md) (49), [Palette](Palette.md) (32), [FundScreen](FundScreen.md) (32), [Game](Game.md) (30), [BuildScreen](BuildScreen.md) (26), [UserInterface](UserInterface.md) (25), [BuildingsTemplate](BuildingsTemplate.md) (25), [SectorScreen](SectorScreen.md) (23), [RankingsScreen](RankingsScreen.md) (23), [BuildAdvice](BuildAdvice.md) (21), [TradeScreen](TradeScreen.md) (19), [TimeChart](TimeChart.md) (17), [FreightView](FreightView.md) (17), [Rankings](Rankings.md) (15), [ResearchScreen](ResearchScreen.md) (13), [Auction](Auction.md) (12), [PolicyScreen](PolicyScreen.md) (12), [Buyback](Buyback.md) (12), [PartnersView](PartnersView.md) (9), [WorldMeasures](WorldMeasures.md) (9), [PartnersPage](PartnersPage.md) (8), [HistoryScreen](HistoryScreen.md) (7), [GovernmentScreen](GovernmentScreen.md) (7), [ServicesScreen](ServicesScreen.md) (6), [DebtQuote](DebtQuote.md) (5), [BankScreen](BankScreen.md) (5), [Sector](Sector.md) (5), [SavedCities](SavedCities.md) (5)... and 42 more

**Used by (3):** [KeptCheck](KeptCheck.md), [MoneyWordsCheck](MoneyWordsCheck.md), [ShortSellingCheck](ShortSellingCheck.md)

## Sections

| line | section |
|---:|---|
| 227 | THE CHROME (UP2): the header and the rail, kept and built new |
| 428 | THE THREE PANELS (UP2, slice 2b): the city panel, the construction panel, the inbox's list |
| 614 | CITY HISTORY (KP1): the page kept and built new, and the hands on its choices |
| 861 | · the hands on what reshapes the page |
| 1007 | · the pointer on the big chart |
| 1036 | · the real show methods, for what the clock decides (the count-up) |
| 1109 | INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2): the three pages kept and built new, |
| 1646 | · the hands on what reshapes the pages |
| 1768 | · the controls (the rule: a control keeps the player's hand) |
| 1872 | BUILD AND THE CONSTRUCTION PAGE (KP3): the Build screen's pages and the construction page, kept and built new, |
| 2331 | · the construction page |
| 2393 | · the first text field on a page: the yield box on the Overview, the find box on Demolish |
| 2422 | · the pointer over what a hover styles |
| 2550 | THE BANK (KP5): the Overview and the six pages behind it, in both views, kept and built new, the hands on what |
| 2614 | FINANCES (KP5): the hub and the six areas' pages, kept and built new, the hands on what reshapes them (an area and a |
| 2734 | · Borrow, the bond market's book, the city's fund (KP5, slice C) |
| 2816 | · the fund's words, read without a window (LF1c): a harness reads them on a played city |
| 2950 | · the foreign default menu and the debt result menu (KP5, slice C) |
| 2997 | THE MONEY SCREENS' HANDS (KP5, slice D): what KeptCheck's sections 55 to 68 reach that the slices' hands did not - the |
| 3033 | · the boxes, given the focus and pressed under |
| 3133 | · the pictures, and how often they draw |
| 3226 | · the animations a month starts |
| 3348 | SECTORS (KP4): the list and a business's five pages, kept and built new, the hands on what reshapes them, the |
| 3475 | · the pictures |
| 3508 | · the count-up and the sweep |
| 3604 | · the controls (the two dials of the oil reserve) |
| 3777 | TRADE & THE WORLD (KP6, slice B): the six pages and a nation's page behind Partners, kept and built new, the hands on |
| 3940 | THE GOVERNMENT TAB (KP6, slice A): its four pages, kept and built new, the hands on what reshapes them (a page picked, a |
| 4020 | POLICY AND ZONING (KP6, slice C): the hub and every page of the five areas, kept and built new, the hands on what res... |
| 4198 | THE RESEARCH AREA AND CITY HISTORY'S FLAGS (R5, slice B1): the pure helpers the Found page and the History flags are ... |
| 4203 | · the Overview, Funding and the schools' lines (R3b, slice B) |
| 4327 | THE HANDS THE KP6 HARNESS ADDS (KP6, slice D): what Trade counts up, and the width of its charts |
| 4418 | THE CLOCK ON EVERY RAIL SCREEN (KP7, slice D): the window's count of screens rebuilt in place, what the clock does after |
| 4727 | THE RANKINGS TAB (KP7, slice B): the tab as it opens, kept and built new, the hands on what reshapes it (a board, the... |
| 4934 | HOUSEHOLD MONEY (KP7, slice A): People's second page, kept and built new, the hands on what reshapes it (the view, a ... |
| 5033 | THE DRAWER'S BAR, THE CONSTRUCTION TAB AND THE SPEED'S READING (UP7) |
| 5241 | FB2: THE CAUSAL FIXTURES OF THE REPAIRS (KL1, KS1-UI, BF1-K) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 79 | `KeptBench.WIDTH` | `1456` | The width the page is laid out at: the menu column's in a 1600-pixel window (the study's renders). |
| 87 | `KeptBench.HEADER_WIDTH` | `1600` | The window's width in KeptCheck's pictures: the header is laid out across it. |
| 90 | `KeptBench.RAIL_HEIGHT` | `900` | The window's height: the rail is laid out down it. |
| 97 | `KeptBench.PANEL_HEIGHT` | `900` | The three panels' scenes (UP2, slice 2b): the city panel's 290 wide, the construction panel's 280, the inbox list's. |
| 103 | `KeptBench.BAR_WIDTH` | `290, BAR_HEIGHT = 40` | The drawer's bar (the drawer's width) and the construction tab (its own width, the window's height), on scenes of their own (UP7). |
| 768 | `KeptBench.SETTLED_HEIGHT` | `"keptbench.settledHeight"` | The key a scroller keeps the height its content had when the page was last settled, in its properties. |
| 797 | `KeptBench.SETTLE_PASSES` | `10, QUIET_PASSES = 2` | The most layout passes settle() runs, and the quiet ones in a row it takes to stop. |
| 1466 | `KeptBench.SETS_NAMED` | `Integer.getInteger("keptbench.named", 12)` | The most sources redrawSets names. |
| 2137 | `KeptBench.STOPPED` | `List.of("deposit", "coast", "river", "bridge", "licence", "land")` | The stopped pages' kinds, by the way the model refuses an order. |
| 2291 | `KeptBench.FUNDING_CAP_TITLE` | `"THE HIGHEST YIELD YOU WILL TAKE"` | The title over the funding page's cap chips, as BuildScreen.shortAnswers() writes it. |
| 2425 | `KeptBench.ABSENT` | `"absent"` | What hoverKept() says when the page has no node of the kind asked for. |
| 3683 | `KeptBench.PRECISE` | `Boolean.getBoolean("keptbench.precise")` | Whether a signature writes a node's place and size to four decimals instead of one (-Dkeptbench.precise=true). |
| 5538 | `KeptBench.RECURRING_LINES` | `{ "Raised by issuing paper", "Principal repaid", "Interest that was no cash",...` | KS1-Gov: the rows a scope parks are the ones last asked for. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `private final UserInterface ui` |  |
| 82 | `private final VBox column` |  |
| 83 | `private final Scene scene` |  |
| 84 | `private List<Node> kept` |  |
| 92 | `private final Scene headerScene, railScene` |  |
| 93 | `private List<Node> keptHeader` |  |
| 94 | `private Kept.Pool[] keptPools` |  |
| 98 | `private final Scene cityScene, sitesScene, listScene` |  |
| 99 | `private List<Node> keptCity` |  |
| 100 | `private Kept.Pool[] keptPanelPools` |  |
| 104 | `private final Scene barScene, tabScene` |  |
| 105 | `private List<Node> keptBar` |  |
| 106 | `private Kept.Pool[] keptFramePools` |  |
| 618 | `private List<Node> keptHistory` |  |
| 619 | `private Kept.Pool keptHistoryPool` |  |
| 621 | `private Kept.Pool[] keptChipPools` | What a page built new took from the charts the kept page holds: their pools, their chips, and where they sat. |
| 622 | `private List<List<Node>> keptLegend` |  |
| 623 | `private javafx.scene.Parent[] chartParents` |  |
| 624 | `private int[] chartAt` |  |
| 831 | `private int graphicMoves` |  |
| 1121 | `private final String name, screen` |  |
| 1122 | `private final java.util.function.Supplier<Kept.Pool> pool` |  |
| 1123 | `private final java.util.function.ToDoubleFunction<Kept.Pool> draw` |  |
| 1124 | `private final Runnable home` |  |
| 1579 | `private List<Node> keptPage` | What the kept page held when a page was built new in its place: its top nodes, and what every box under it held. |
| 1580 | `private java.util.Map<Pane, List<Node>> keptBoxes` |  |
| 1608 | `private final WritableImage tiny` |  |
| 1609 | `private final javafx.scene.SnapshotParameters tinyView` |  |
| 2959 | `private ham.citybuildersim.DebtQuote bookedQuote` | What the last booking the bench made quoted and said: the receipt's two inputs. |
| 2960 | `private String bookedSummary, bookedMoney` |  |
| 2961 | `private boolean bookedForeign` |  |
| 3165 | `private final java.util.Map<Pane, int[]> repaintCounts` | A picture owns its counter; observing one must not keep it and its retired page alive. |
| 3625 | `private static Method doCSSPass, doLayoutPass` |  |
| 3686 | `private static boolean preciseNow` | Whether a picture's signature is being written (KeptCheck 64): to four decimals, so that a bar that moved by a hair is told from one that did not. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 76 | 5528 | **type** `public final class KeptBench` | The People page on a scene of its own - no stage, no window - drawn kept and built new, for KeptCheck (study UP4): the harness's hands on the page, since what it reaches is the ui package's own. |
| 109 | 36 | `public KeptBench(Game game)` | The bench on a city: the window's object with no stage, its menu column in a scene of its own. |
| 147 | 5 | `public double drawKept()` | People's page drawn kept from its pool (the first draw makes every node): the draw's own milliseconds. |
| 154 | 3 | `public int lastMade()` | The nodes the last kept draw made new. |
| 159 | 3 | `public int lastKept()` | The nodes the last kept draw handed back. |
| 164 | 3 | `public Node top()` | The page's top node, kept or not: the same object from one kept draw to the next. |
| 172 | 8 | `public double[] showFresh() throws Exception` | The page built new in place of the kept one (no pool, every node new) and laid out, as every redraw was before UP4: {its draw ms, CSS ms, layout ms}. |
| 182 | 6 | `public void restoreKept() throws Exception` | The kept page back in the column, as it was, and laid out. |
| 194 | 3 | `public String spoil()` | One figure on the page made wrong: the first label whose words hold a digit gets a "9" in front (KeptCheck's proof that its comparison sees one wrong figure, and that the next kept draw sets it right). |
| 198 | 17 | `private static String spoil(List<Node> nodes)` |  |
| 217 | 4 | `public void details(String name, boolean open)` | A "details" fold opened or closed, as its toggle would (the next draw shows it). |
| 223 | 3 | `public void yearView(boolean on)` | The month card's year view on or off. |

### THE CHROME (UP2): the header and the rail, kept and built new (lines 227-427)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 8 | `public double drawChromeKept()` | The header and the rail drawn kept, from their pools (the first draw makes every node): the draw's own milliseconds. |
| 250 | 4 | `private void keepMiniMapOff()` | The minimap's node hidden (never removed: it stays in the rail where the rail puts it). |
| 256 | 5 | `public double drawEnvelope()` | The envelope drawn on its own, as a notice read elsewhere draws it (refreshInboxButton()). |
| 263 | 4 | `public int[] chromeCounts()` | The nodes the last draws of each pool made new and handed back: {header made, kept, envelope made, kept, rail made, kept}. |
| 269 | 5 | `public List<Node> chromeTops()` | The header's top nodes and the rail's, in order: the same objects from one kept draw to the next. |
| 280 | 14 | `public double[] showFreshChrome() throws Exception` | The chrome built new in place of the kept one - the pools swapped for empty ones, so every node is made again, as every redraw did before UP2 - and laid out: {its draw ms, CSS ms, layout ms}. |
| 296 | 15 | `public void restoreKeptChrome() throws Exception` | The kept chrome back where it was, with its pools, and laid out. |
| 317 | 6 | `public double[] chromePasses() throws Exception` | Both chrome scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed. |
| 324 | 8 | `private static void finishAnimations(Node n)` |  |
| 334 | 8 | `public List<String> chromeSignature()` | The header's signature, then the rail's, a line a node (see signature()). |
| 344 | 3 | `public int[][] chromePixels()` | The header's pixels and the rail's, each as pixels() gives them: {header, rail}. |
| 348 | 9 | `private static int[] pixelsOf(Node root)` |  |
| 359 | 3 | `public String spoilHeader()` | One figure in the header made wrong: the first label with a digit gets a "9" in front (see spoil()). |
| 364 | 4 | `public void clock(boolean running, String pausedBecause)` | The clock as the header shows it: running or stopped, and the line saying why it stopped itself (null for none). |
| 370 | 3 | `public void speed(int rung)` | The speed's rung (an index into the ladder, 0 to 8). |
| 375 | 3 | `public void screen(String name)` | The screen on show, by the name its clearMenu() call gives: the rail lights its tab; a game menu hides the rail and the clock. |
| 380 | 6 | `public boolean focusPlay()` | A node of the header's that can take the focus (the play button), given it; whether the scene has it. |
| 387 | 11 | `private static Node firstButton(Node n)` |  |
| 400 | 6 | `public boolean headerHasFocus()` | Whether the focus is on a node inside the header (the header itself is the scene's root here, where the draw puts the focus). |
| 412 | 15 | `public static boolean nestedDrawsKeepEach()` | A draw of one pool inside a draw of another (the header's draw reaches the envelope's) gives each its own nodes: the outer's three are handed back to it at the next round, the inner's one to the inner pool - drawn on ... |

### THE THREE PANELS (UP2, slice 2b): the city panel, the construction panel, the inbox's list (lines 428-613)

| line | len | member | says |
|---:|---:|---|---|
| 433 | 7 | `public double drawPanelsKept()` | The three drawn kept, from their pools (the first draw makes every node): the draws' own milliseconds. |
| 442 | 4 | `public int[] panelCounts()` | The nodes the last draws of the three made new and handed back: {city made, kept, construction made, kept, list made, kept}. |
| 448 | 6 | `public List<Node> panelTops()` | The panels' top nodes, in order: the same objects from one kept draw to the next. |
| 460 | 16 | `public double[] showFreshPanels() throws Exception` | The three built new in place of the kept ones - their pools swapped for empty ones, so every node is made again, as every redraw did before UP2 - and laid out: {draw ms, CSS ms, layout ms}. |
| 478 | 12 | `public void restoreKeptPanels() throws Exception` | The kept panels back where they were, with their pools, and laid out. |
| 492 | 4 | `public double[] panelPasses() throws Exception` | The three scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed. |
| 498 | 10 | `public List<String> panelSignature()` | The three panels' signatures, one after the other, a line a node (see signature()). |
| 510 | 3 | `public int[][] panelPixels()` | The three panels' pixels, each as pixels() gives them: {city, construction, inbox list}. |
| 515 | 3 | `public String spoilPanel()` | One figure in the city panel made wrong: the first label with a digit gets a "9" in front (see spoil()). |
| 520 | 4 | `public void cityMode(boolean dashboard, boolean needsView)` | The city panel's mode: Dashboard or Summary, and whether the header's chip opened it on NEEDS YOU (which draws Summary). |
| 526 | 4 | `public void section(String key, boolean open)` | One dashboard section opened or folded, as its header's click would. |
| 532 | 4 | `public void sectionsAll(boolean open)` | "Open all" or "close all", as the dashboard's links do. |
| 538 | 4 | `public void drawer(boolean open, boolean pinned)` | The drawer open or shut (the header's chip), pinned or not: the chip's look follows, the panel's nodes do not. |
| 544 | 4 | `public void inboxList(boolean open, String expandedKey)` | The inbox's list dropped down or put away, and which notice is unfolded ("" for none). |
| 550 | 7 | `public Node firstHoverInCityPanel()` | The first node of the city panel with a hover look given by code (a summary row), or null. |
| 558 | 11 | `private static Node firstHover(Node n)` |  |
| 571 | 6 | `public boolean focusInboxButton()` | The inbox list's action button (an unfolded notice's), given the focus; whether the scene has it. |
| 579 | 6 | `public boolean inboxHasFocus()` | Whether the focus is on a node inside the inbox list (the box itself is the scene's root here, where the draw puts the focus). |
| 587 | 4 | `public ScrollPane cityScroller()` | The city panel's scroller (kept: the same object from one draw to the next). |
| 593 | 3 | `public void forgetChrome()` | Every kept node of the chrome let go, as anotherCity() lets them go: the next draw makes them all new. |
| 598 | 3 | `public Node headerChild(int i)` | The header's i-th child (the clock, the money block, the row of tiles, the rating and inbox). |
| 603 | 3 | `public Node railChild(int i)` | The rail's i-th child (a destination's button, or the gap, the minimap, Menu). |
| 608 | 5 | `public static void hover(Node n, boolean on) throws ReflectiveOperationException` | The pointer put over a node, or taken off it, as the scene's own mouse handling does (Node.setHover). |

### CITY HISTORY (KP1): the page kept and built new, and the hands on its choices (lines 614-860)

| line | len | member | says |
|---:|---:|---|---|
| 631 | 4 | `private void historyStage()` | City History's stage: the screen's name (its scroller is filed under it), the first-visit preset (showHistoryMenu() hands it over, and the bench draws the page itself), the window's scroller the page's height reads, a... |
| 637 | 7 | `private void historyEnsure()` | ...without naming the screen on show: the real show methods name it themselves, and arriving is not being there. |
| 646 | 8 | `private void ensureWindow()` | The window a page reads: the stand-in scroller its height follows, and the stack a full screen goes in. |
| 656 | 6 | `public double drawHistoryKept()` | City History drawn kept from its pool (the first draw makes every node): the draw's own milliseconds. |
| 664 | 3 | `public int[] historyCounts()` | The nodes the last kept draw of City History made new and handed back: {made, kept}. |
| 669 | 3 | `public List<Node> historyTop()` | The page's top nodes (the frame and its scroller), kept or not: the same objects from one kept draw to the next. |
| 674 | 5 | `public List<Node> historyCharts()` | The charts the page holds, for scene and parent checks: the big chart, then the two pins' (those made). |
| 686 | 32 | `public double[] showFreshHistory() throws Exception` | City History built new in place of the kept page - its pool and the big chart's chips' pool swapped for empty ones, so every node is made again, as every redraw was before KP1 - and laid out: {its draw ms, CSS ms, lay... |
| 720 | 25 | `public void restoreKeptHistory() throws Exception` | The kept page back in the column with its pool, the charts back in the boxes that held them, and laid out. |
| 750 | 16 | `public void settle() throws Exception` | The page laid out again, as the next frames would, until a pass moves nothing (at most SETTLE_PASSES): a wrapped label's height settles over a pass or two, and the scroller's under the frame a pass after that. |
| 770 | 7 | `private List<ScrollPane> scrollersOnPage()` |  |
| 778 | 17 | `private void settlePasses() throws Exception` |  |
| 800 | 3 | `public String spoilHistory()` | One figure on City History made wrong: the first label whose words hold a digit gets a "9" in front (see spoil()). |
| 809 | 3 | `public void finishPageAnimations()` | Every animation a draw started on the page run to its end, as a pulse would have: a scene with no window has none. |
| 813 | 8 | `private static void finishOne(Node n)` |  |
| 823 | 7 | `private static void each(Node n, java.util.function.Consumer<Node> f)` | `f` on a node and everything under it, through the scroller's content and a label's graphic too. |
| 834 | 10 | `public int watchGraphics()` | Every graphic of a label on the page now watched for entering or leaving the scene: how many are watched. |
| 846 | 5 | `public int graphicMoves()` | How many times a watched graphic left or entered the scene since this was last asked. |
| 853 | 7 | `private<T extends Node> T first(Class<T> type)` | The first node of this class under the page, or null. |

### the hands on what reshapes the page (lines 861-1006)

| line | len | member | says |
|---:|---:|---|---|
| 864 | 5 | `public void historyPick(String key, boolean on)` | A line ticked or unticked, as its chip would (the next draw shows it). |
| 871 | 5 | `public void historyPickOnly(String...keys)` | Exactly these lines drawn, none hidden (what "clear all" and a preset leave). |
| 878 | 3 | `public static int historyPresets()` | How many presets there are. |
| 883 | 3 | `public void historyPreset(int i)` | The i-th preset pressed, as its chip would (the lines it names, none hidden). |
| 888 | 3 | `public String historyGroupOf(String key)` | The picker's group (by name) a line is in. |
| 893 | 3 | `public void historyGroup(String group, boolean open)` | A picker group opened or closed, as its heading would. |
| 898 | 6 | `public boolean historyType(String text)` | Typed into the picker's filter box, as a key would (it refills the groups in place); false when the page has no box. |
| 906 | 4 | `public void historyFold(String key, boolean open)` | A fold of the page opened or closed ("hard times", "goods"), as its toggle would. |
| 912 | 4 | `public String historyLog(boolean on)` | The big chart on a log scale or off, as the chip would; why it cannot be drawn, or null when it can. |
| 918 | 3 | `public void historyLayers(boolean on)` | Real GDP's layers on or off, as the chip would. |
| 923 | 7 | `public boolean historyLegendClick(int i)` | The legend chip i of the big chart clicked, as the pointer would (it hides or shows the line); false when it has none. |
| 932 | 4 | `public List<Node> historyLegend()` | The big chart's legend chips, as they are now. |
| 938 | 3 | `public static Object tooltipOf(Node n)` | The tooltip installed on a node (the object, to see it is the same one from month to month), or null. |
| 943 | 4 | `public List<Node> historyRanges()` | The big chart's range buttons, as they are now. |
| 949 | 3 | `public int historyHidden()` | How many lines the legend has switched off. |
| 954 | 3 | `public void historyWindow(double from, double to)` | The chart's window moved to these months, as a drag or an episode's row would. |
| 959 | 3 | `public void historyRange(int months)` | The chart's window on one of its ranges (a number of months, as ChartModel.RANGES gives them, or ChartModel.ALL), as a range button would. |
| 964 | 3 | `public int[] historyWindow()` | The window as it is: {first month shown, last month shown}. |
| 969 | 4 | `public int[] historyBounds()` | The history's ends: {first month, last month}. |
| 975 | 6 | `public double[] historyEpisodeSpan()` | The first named episode's span with a year either side, as moveChartTo(episode) would put the window, or null when the city has none. |
| 983 | 3 | `public void historyPin(String key)` | A line pinned, as its chip would (it writes the city's scratch preferences). |
| 988 | 3 | `public void historyUnpin(boolean left)` | A pin taken off, as its "unpin" would. |
| 993 | 3 | `public String historyPinned(boolean left)` | The line on a pin. |
| 998 | 3 | `public void historyFull(boolean on)` | The chart's full screen on or off, as its button would (the page is drawn by the caller). |
| 1003 | 3 | `public void historyBook(String said)` | The year book's card shown (with this said) or put away (null), without writing a book. |

### the pointer on the big chart (lines 1007-1035)

| line | len | member | says |
|---:|---:|---|---|
| 1010 | 7 | `public boolean historyHoverPlot()` | The pointer moved over the big chart's plot, as the toolkit would: a MOUSE_MOVED fired on the canvas; whether the chart now shows its crosshair. |
| 1019 | 4 | `public boolean historyHovering()` | Whether the big chart shows its crosshair. |
| 1024 | 4 | `private static void fireMouse(Node target, javafx.event.EventType<javafx.scene.input.MouseEvent> type, double x, double y, java...` |  |
| 1029 | 6 | `private static javafx.scene.input.MouseEvent mouse(javafx.event.EventType<javafx.scene.input.MouseEvent> type, double x, double...` |  |

### the real show methods, for what the clock decides (the count-up) (lines 1036-1108)

| line | len | member | says |
|---:|---:|---|---|
| 1039 | 3 | `public static double[] speedLadder()` | The speed ladder, as the clock steps along it. |
| 1044 | 3 | `public static double countUpTop()` | The fastest speed a figure still counts up at. |
| 1049 | 3 | `public void showPeople()` | People shown, as the rail's tab or a month landing shows it: the real show method (the chrome, then the page kept). |
| 1054 | 4 | `public void showHistory()` | City History shown, as the rail's tab or a month landing shows it. |
| 1060 | 3 | `public void forgetPeople()` | People forgotten, as another city opened would leave it: nothing kept, nothing just landed. |
| 1065 | 3 | `public void forgetHistory()` | City History forgotten, as another city opened would leave it. |
| 1070 | 4 | `public javafx.animation.Animation peopleCount()` | The count-up last started on LIVING HERE's figure on People, or null. |
| 1076 | 4 | `public javafx.animation.Animation pinCount()` | The count-up last started on the left pin's figure on City History, or null. |
| 1081 | 9 | `private Label livingHere()` |  |
| 1092 | 4 | `public boolean headcountMoved()` | Whether last month's headcount differs from this month's, as LIVING HERE's count-up reads them (it counts nothing from the same figure). |
| 1098 | 3 | `public void historyPinLeft(String key)` | The line on the left pin set directly (the preferences are not written): a line that moves most months, for the count-up. |
| 1103 | 5 | `public String[] pinFigures()` | The left pin's figure on the page now, and the figure the model gives it now: they differ when a month has landed that the page has not drawn. |

### INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2): the three pages kept and built new, (lines 1109-1645)

| line | len | member | says |
|---:|---:|---|---|
| 1120 | 20 | **type** `public final class Page` | One of the three pages on the bench: the name the window shows it under, the screen's own pool, how it is drawn from a pool (what the screen's show method works out first, then the page's code as a kept draw) and how ... |
| 1126 | 8 | `private Page(String name, String screen, java.util.function.Supplier<Kept.Pool> pool, java.util.function.ToDoubleFunction<Kept....` _(in KeptBench.Page)_ |  |
| 1136 | 3 | `public String name()` _(in KeptBench.Page)_ | What the harness's lines call the page. |
| 1142 | 5 | `private double timed(Kept.Pool pool, Runnable page)` | The draw's own milliseconds, for a page's code run as a kept draw from this pool. |
| 1149 | 12 | `public Page infrastructure()` | Infrastructure's tab: the page `infraPage` (and on Freight `freightDivision`) names, over its fold state. |
| 1163 | 13 | `public Page services()` | Services' tab: the sub-page `serviceArea` and `servicePage` name, over its fold state. |
| 1178 | 15 | `public Page land()` | The land office: its side and place, the best-N count and its fold as the screen has them; the map is the screen's own. |
| 1195 | 7 | `public Page landFunding(List<Integer> ids)` | The land office's funding page for these plots (Game.landNeedsFunding() holds for them). |
| 1204 | 7 | `public Page landFellShort(String paper, int bought, int asked)` | The page for when the city fell short: `bought` of `asked` plots, the loan `paper` taken (null: the vault's dollars). |
| 1213 | 6 | `public double drawKept(Page p)` | A page drawn kept from its screen's own pool (an arrival from another screen forgets it first, as keepMenu does): the draw's own milliseconds. |
| 1221 | 3 | `public void home(Page p)` | A page back on the page it opens on, nothing kept: the next draw makes every node. |
| 1232 | 1 | **type** `public record Sets(int count, int ignored, List<String> first)` | What one kept draw set on the nodes it handed back (KP2b/KP2c): style, text, graphic, padding, preferred size, disabling, constraints, and size/margin layout requests, including requests made by an equal raw JavaFX se... |
| 1244 | 3 | `public Sets redrawSets(Page p, java.util.Set<String> unmended)` | One more kept draw of a page, the city unchanged (no month between), with a listener on every node of the page before it: on its style; on a label's or a text's words and a label's graphic; on a region's padding and p... |
| 1252 | 51 | `public Sets redrawSets(Node root, Runnable draw, java.util.Set<String> unmended)` | ...the same for any root and any draw (UP7): the listeners go on `root` and everything under it (a label, a box and its children), `draw` runs - a screen's refresh, the header's, the speed's repaint - and they come off. |
| 1305 | 3 | `public Sets redrawSets(Page p)` | ...with nothing unmended. |
| 1310 | 40 | `public boolean[] drawEndState() throws Exception` | KP2c's draw-end fixture: equal constraints and a greyed button stay put; omissions reset once. |
| 1352 | 14 | `public boolean foreignVitalsCell()` | A non-limitCell maker reapplies its own style before vitalsBar on each draw (KP2b's review). |
| 1368 | 37 | `public boolean[] keptPageInteractions() throws Exception` | Real pages twice kept: retold pictures and controls must give their own optional state each draw. |
| 1406 | 4 | `private static boolean rowsHandlers(Node n)` |  |
| 1412 | 18 | `public int[] sizeAndMarginSets() throws Exception` | Equal raw JavaFX setters cause layout, and the same redraw through the screens' helpers does not. |
| 1432 | 8 | `public int comparisonRoots()` | Saved comparison roots must end with the restore; they otherwise hold whole retired pages. |
| 1442 | 5 | `public java.lang.ref.WeakReference<Pane> retiredPicture()` | An unattached picture counted by the bench: only the diagnostic map could keep it alive. |
| 1449 | 9 | `public java.lang.ref.WeakReference<Node> retiredChrome() throws Exception` | A rendered temporary header child retired by restore: a no-stage scene must drain its dirty list too. |
| 1460 | 4 | `public static void releaseElsewhere(Node n)` | A physical release caught by the scene while a disabled slider cannot hear its own handler. |
| 1468 | 11 | `private static void watchSets(Node n, String what, javafx.beans.value.ObservableValue<?> property, int[] count, java.util.Map<S...` |  |
| 1480 | 7 | `private static void watchList(Node n, String what, javafx.collections.ObservableList<?> list, int[] count, java.util.Map<String...` |  |
| 1488 | 12 | `private static void heardSet(Node n, String what, Object was, Object now, int[] count, java.util.Map<String, Object[]> sources,...` |  |
| 1505 | 12 | `private static String[] whereSet()` | Where the write that is being counted comes from: {the two nearest lines of the game's own code on the stack that are not the bench's, "Pieces.java:132 < Kept.java:61"; the file of the nearest that is not Kept's, the ... |
| 1519 | 12 | `private static String differ(Object was, Object now)` | What changed between two values, as the report writes it: from where they part, each cut. |
| 1533 | 7 | `private static String describeSet(Node n)` | A node as the report of redrawSets names it: its class, its words (cut) and its first style class. |
| 1547 | 19 | `public List<List<String>> berthRowGoesToNone() throws Exception` | A berth kind's row, as the Ports page draws it (SectorScreen.berthRow in the scope berthScope() names), drawn kept with two berths standing and then with none, and the same row built new with none (KP2b): the signatur... |
| 1568 | 4 | `public int[] counts(Page p)` | The nodes the last kept draw of the page made new and handed back: {made, kept}. |
| 1574 | 3 | `public List<Node> pageTop()` | The page's top nodes, kept or not: the same objects from one kept draw to the next. |
| 1587 | 10 | `public double[] showFresh(Page p) throws Exception` | The page built new in place of the kept one (an empty pool, every node new) and laid out, as every redraw was before KP2: {its draw ms, CSS ms, layout ms}. |
| 1599 | 8 | `public void restoreKeptPage() throws Exception` | The kept page back in the column, every box holding what it held, and laid out. |
| 1617 | 3 | `public void release()` | What a scene with no window never lets go: a scene is synchronised to its renderer by a pulse, and the nodes that changed wait in its dirty list (strongly held) until one comes - so every page built new and taken off ... |
| 1621 | 4 | `private void release(Scene...scenes)` |  |
| 1627 | 7 | `private static void boxesOf(Node n, java.util.Map<Pane, List<Node>> out)` | Every box (Pane) under a node and the children it holds now. |
| 1636 | 3 | `public String spoilPage()` | One figure on the page made wrong (see spoil()). |
| 1641 | 4 | `private static void fold(java.util.Set<String> open, String key, boolean on)` | A fold's key put in a set or taken out of it. |

### the hands on what reshapes the pages (lines 1646-1767)

| line | len | member | says |
|---:|---:|---|---|
| 1649 | 4 | `public void infraOpen(String page, String division)` | Infrastructure on one of its pages (and on Freight, a division), as its strip would (the show method normalises the two; the draw does not). |
| 1655 | 3 | `public void infraFold(String name, boolean open)` | One of Infrastructure's folds ("roads", "transit", "railway", "freight") opened or closed. |
| 1660 | 7 | `public static List<String[]> servicesPages()` | Every sub-page of Services as {system, page}, in the strips' order. |
| 1669 | 4 | `public void servicesOpen(String area, String page)` | Services on one sub-page, as the two strips would. |
| 1675 | 3 | `public void servicesFold(String key, boolean open)` | One of Services' folds ("health:sick", "books:safety"...) opened or closed. |
| 1680 | 4 | `public void landSide(int side)` | The land office's side (0 north to 3 west) picked, as its chip or the map would: no place on it yet. |
| 1686 | 3 | `public void landPlace(int place)` | The offer at a place (0 to 5) on the side shown picked, as its row would. |
| 1691 | 3 | `public int landSide()` | The side the office shows. |
| 1696 | 3 | `public void landCount(int n)` | How many offers "Buy the best N" is set to buy (the draw holds it inside what is listed). |
| 1701 | 3 | `public void landDetails(boolean open)` | The office's "details" fold opened or closed. |
| 1706 | 3 | `public void landPay(boolean fromVault)` | How the office pays: from the vault (true) or by converting cash (false), as its toggle sets it. |
| 1711 | 3 | `public boolean landPaidFromVault()` | Whether the office pays from the vault. |
| 1716 | 3 | `public int landListed()` | How many offers are listed. |
| 1721 | 8 | `public List<Integer> landFundingIds()` | The first count of the best offers the city cannot pay for as it stands, or null when it can pay for the whole shelf. |
| 1731 | 6 | `public String landBuy()` | The best offer bought through the model, as its row's Buy would (the receipt shows until the month turns); why not, or null when it was. |
| 1742 | 9 | `public int landBuyUntilShort(int limit)` | Offers bought, the best first through the model (Game.buyLandParcels(), as a row's Buy), until the city cannot pay for the best one as it stands, so that the funding page exists: how many it took, or -1 when the limit... |
| 1753 | 3 | `public int landWaiting()` | How many sectors wait on ground (the office's waiting card lists them). |
| 1758 | 4 | `public boolean landReceipt()` | Whether the land office holds a receipt now. |
| 1764 | 3 | `public boolean landLandmasses()` | Whether the city owns ground on a second landmass, so that the office lists its ground by landmass (a table kept since KP4: SectorScreen.oilTable, whose rows are scopes). |

### the controls (the rule: a control keeps the player's hand) (lines 1768-1871)

| line | len | member | says |
|---:|---:|---|---|
| 1771 | 4 | `public void showInfrastructure()` | Infrastructure shown, as the rail's tab or a month landing shows it: the real show method (the chrome, then the page kept). |
| 1777 | 3 | `public javafx.scene.control.Slider slider()` | The first slider on the page - Transit's fare - or null. |
| 1782 | 3 | `public static boolean held(Node n)` | Whether the player holds a node (Kept.held). |
| 1787 | 4 | `public static Label readingOf(javafx.scene.control.Slider s)` | The reading beside a slider, as the latest draw made it (the label the slider's listener writes). |
| 1793 | 4 | `public static double shownBy(javafx.scene.control.Slider s)` | What the dial's ladder says its thumb shows, as the latest draw built it (Ladder.shown()). |
| 1799 | 3 | `public static void press(Node n)` | A primary press on a node, as the pointer's. |
| 1804 | 3 | `public static void release(Node n)` | ...and the release (the node's own release handler runs: for the fare dial, it stages the thumb and redraws the screen). |
| 1809 | 3 | `public double stagedFare()` | The staged fare, or NaN when none is. |
| 1814 | 3 | `public void unstageFare()` | The fare put back as it is, as the card's "Leave it as it is" does (it drops the proposal). |
| 1819 | 3 | `public void stageFare(double value)` | The fare staged at a value, as a release on the dial stages it. |
| 1824 | 3 | `private javafx.scene.control.TextField filter()` | The filter box of City History as it is now, or null when the page has none. |
| 1833 | 16 | `public String historyFilterFocus(String text) throws ReflectiveOperationException` | Words typed into City History's filter box and the box given the focus: how it got it - "requestFocus" when the scene took it, "setFocused by reflection" when this scene has no window to give it (a scene with no stage... |
| 1851 | 7 | `public void historyFilterBlur() throws ReflectiveOperationException` | The focus taken off City History's filter box, as another click would. |
| 1860 | 4 | `public Object[] historyFilterState()` | City History's filter box: the node, its text, its caret and whether it has the focus. |
| 1866 | 5 | `public String historyFilterSet(String text)` | Kept.text on City History's filter box: the text it holds after. |

### BUILD AND THE CONSTRUCTION PAGE (KP3): the Build screen's pages and the construction page, kept and built new, (lines 1872-2330)

| line | len | member | says |
|---:|---:|---|---|
| 1878 | 6 | `public static List<String> buildPages()` | The pages the Build tab has: the Overview, then the categories in the strip's order. |
| 1886 | 4 | `public static boolean buildIsCity(String name)` | Whether a Build page is one of the city's five (rings, and a card on each), else the Overview or a market category. |
| 1892 | 5 | `public static List<String> buildRings(String category)` | The rings a city category has, by their labels in the page's order (none for the Overview and the market's). |
| 1906 | 3 | `public Page build(String name, String start, java.util.Map<String, Long> orders)` | The Build screen as BuildScreen has it: whichever page `buildCategory` names, the Overview or a category, set up as its show method sets it up and drawn by its draw; a page other than the one the pool last drew is the... |
| 1911 | 17 | `public Page build(String name, String start, java.util.Map<String, Long> orders, java.util.Set<String> pins)` | ...with the stat cards of these buildings pinned open. |
| 1930 | 3 | `public void buildOpen(String name)` | The Build screen on a page, as a tile or the strip's chip opens it (the next draw is the arrival). |
| 1935 | 3 | `public String buildPage()` | The page the Build screen is on. |
| 1940 | 3 | `public void buildRing(String category, int i)` | The ring at a place on a city category picked, as its card's click does (the click then draws the page again). |
| 1945 | 3 | `public void buildAuto(boolean on)` | Automatic building turned on or off through the model, as its switch does. |
| 1950 | 3 | `public boolean buildAuto()` | Whether automatic building is on. |
| 1955 | 5 | `public List<String> buildCardNames()` | The names of the buildings with a card on the page as the last draw laid it, in the page's order (the keyboard's list). |
| 1962 | 7 | `private List<javafx.scene.layout.StackPane> cardNodes()` | The card nodes on the page now, in the order the page laid them out. |
| 1971 | 7 | `private static<T extends Node> T find(Node root, Class<T> type, java.util.function.Predicate<T> test)` | The first node of a class under a root that passes a test, or null. |
| 1980 | 3 | `public int buildCards()` | How many cards the page now shows. |
| 1985 | 3 | `public boolean buildShowsNew(BuildingsTemplate t)` | Whether the Build screen shows NEW on a template's card now (R5): its one rule, which the card, the category's dot and the strip all ask. |
| 1990 | 3 | `public static boolean buildShowsNew(boolean modelNew, Integer firstDrawn, int month)` | The rule alone (R5): NEW while the model says so and the card was not first drawn in an earlier month than `month`. |
| 1995 | 3 | `public Integer buildNewSeen(BuildingsTemplate t)` | The month a template's card was first drawn in while it was NEW (R5), or null for one the screen has not drawn. |
| 2000 | 3 | `public List<BuildingsTemplate> buildShownNew()` | The templates that show NEW on the Build screen now (R5), in the templates' order. |
| 2005 | 7 | `public int buildNewChips()` | How many NEW chips the Build page shows (R5): the cards that say NEW in their heads. |
| 2014 | 4 | `public String buildNewStrip()` | The words of the strip under the Build page's head that says what is new on the page (R5), or null when there is none. |
| 2020 | 10 | `public List<String> buildNewDots()` | The category chips on the Build page's strip that carry the discovery's dot (R5): the buttons whose graphic is the pair of dots, one of them the discovery's violet ringed, by their names. |
| 2032 | 7 | `public int buildNewEdges()` | How many cards on the Build page have the discovery's violet for their edge (R5). |
| 2041 | 8 | `public boolean buildStep(int card, String glyph)` | A press on a card's stepper ("+", "+10", "+100", "−" or "↺"), as the pointer's: its order repriced and the order bar refilled in place, outside any draw. |
| 2051 | 3 | `public long buildOrdered(String building)` | The count a building has dialled up on its card. |
| 2056 | 6 | `public String buildCount(int card)` | The count on the face of a card's stepper, as the page shows it now; null when the page has no such card. |
| 2064 | 3 | `public boolean buildBarShows(String words)` | Whether the page shows a label with exactly these words (the order bar's line of the order, "3 × Walk-in Clinic"). |
| 2069 | 8 | `public boolean buildPin(int card)` | A click on a card's (i), as the pointer's: its stat card pinned open, or let go. |
| 2079 | 3 | `public int buildBarFills()` | How many times the order bar has been filled since the screen was made (BuildScreen.barFills). |
| 2084 | 3 | `public boolean buildPinned(String building)` | Whether the stat card of a building is pinned open. |
| 2095 | 30 | `public String hoverCover(Runnable redraw) throws Exception` | The pointer over the first card's (i): its stat cover shown by the (i)'s own mouse-entered, the page drawn kept again (`redraw`) and the cover looked at; then the pointer gone with its mouse-exited run, the page drawn... |
| 2127 | 8 | `public void showBuild()` | The Build screen shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 2139 | 11 | `private static Game.BuildResult refusalOf(String kind)` |  |
| 2152 | 10 | `private boolean namesRefusal(String kind, BuildingsTemplate t)` | Whether a building is of the kind that can meet this refusal at all (it names the page): a mine for ore, a plant or terminal for the sea... |
| 2169 | 13 | `public Object[] stoppedFor(String kind)` | A building and a count the model refuses for this reason (Game.buildRunStop(), the check an order passes before money), found by trying every building at a few counts: {the building, the count, true}. |
| 2184 | 23 | `public Page buildStopped(String kind, BuildingsTemplate t, int quantity)` | The page a build is stopped on, for this kind (STOPPED), this building and count, as it is drawn when the order came from the building's own category. |
| 2213 | 12 | `public Object[] fundingRun()` | A run the city cannot pay for, for the funding page: the first building and count (a few, the smallest first) such that the model lets some of it ahead and the part it funds costs more than the cash; {the building, th... |
| 2234 | 24 | `public Object[] fundingRunShort(double shortBy)` | A run the market cannot lend for, for the funding page with its shortfall (FB6, KBF1): fundingRun()'s building first and then the others, each at ten times the count until the model lets none of it ahead, the first wh... |
| 2266 | 23 | `public Object[] fundingRunCovered(double coverBy)` | The usual funding page's run, an offer covering the gap (REV-FB6-1): fundingRun()'s building first and then the others, each at ten times the count until the model lets none of it ahead, the first with a gap whose two... |
| 2294 | 4 | `public List<String> fundingShortWords(BuildingsTemplate t, int quantity)` | The funding page's shortfall lines for a run, as its answers draw them (BuildScreen.shortWords() over runOffers()): none when both offers cover it. |
| 2300 | 5 | `private static BuildScreen.RunFrom fundingFrom(BuildingsTemplate t)` | Where a run of one building is placed from, as buildFunding() draws it. |
| 2307 | 11 | `public Page buildFunding(BuildingsTemplate t, int quantity)` | The build funding page for a run of one order (the run must have a gap: fundingRun() finds one). |
| 2320 | 10 | `public Page buildFellShort(BuildingsTemplate built, int builtCount, BuildingsTemplate left, int leftCount, String paper)` | The page for when the loan went through and an order was still not built: `built` of one building and `left` of another, the loan `paper` ("20-year bond"). |

### the construction page (lines 2331-2392)

| line | len | member | says |
|---:|---:|---|---|
| 2334 | 3 | `public Page construction()` | The construction page, on the tab the screen is on; it opens on Sites with nothing picked and the find box empty. |
| 2339 | 13 | `public Page construction(String opensOn)` | ...opening on a tab ("Sites", "Timeline" or "Demolish"), with nothing picked and the find box empty. |
| 2354 | 4 | `public void constructionTab(String tab)` | The construction page on a tab ("Sites", "Timeline" or "Demolish"), as its tab chip opens it (the next draw is the page). |
| 2360 | 6 | `public void constructionPick(String name, boolean shell, int count)` | A building on the Demolish tab picked, as its row's click picks it, and how many of it to bring down. |
| 2368 | 3 | `public void constructionFilter(String filter)` | The screen's own find filter set, as a change that is not the player's typing (the box is then set by the next draw, never while it has the focus). |
| 2373 | 8 | `public String constructionPickable(boolean buyOut)` | The first building the city may demolish (buyOut false) or must buy out first (buyOut true) and has standing, by name; null when it has none. |
| 2383 | 4 | `public void showConstruction()` | The construction page shown through its real show method, on its tab (the chrome, then the page kept). |
| 2389 | 3 | `public int sitesOn()` | How many sites the city has on (the rows of the Sites tab and the bars of the timeline). |

### the first text field on a page: the yield box on the Overview, the find box on Demolish (lines 2393-2421)

| line | len | member | says |
|---:|---:|---|---|
| 2396 | 3 | `public String fieldFocus(String text) throws ReflectiveOperationException` | Text typed into the page's first text field and the box given the focus (see historyFilterFocus()): how it got the focus. |
| 2401 | 3 | `public void fieldBlur() throws ReflectiveOperationException` | The focus taken off the page's first text field. |
| 2406 | 3 | `public Object[] fieldState()` | The page's first text field: the node, its text, its caret and whether it has the focus; null when it has none. |
| 2411 | 5 | `public String fieldType(String text)` | Text typed into the page's first text field as keys would, its listener running (outside a draw); the text it holds after. |
| 2418 | 3 | `public String fieldSet(String text)` | Kept.text on the page's first text field: the text it holds after. |

### the pointer over what a hover styles (lines 2422-2549)

| line | len | member | says |
|---:|---:|---|---|
| 2436 | 49 | `public Node hoverTarget(String kind)` | The first node of a kind on the page, or null: "rowGoes" (City History's row that moves the chart), "door" (Pieces.door: words and a "›"), "limitCell" (a vitals cell that is a door), "chartCardHead" (a chart card's na... |
| 2492 | 20 | `public String hoverKept(String kind, Runnable redraw) throws Exception` | The pointer put over the first node of a kind, the page drawn kept again (`redraw`), and the node looked at; then the pointer taken off and the page drawn again. |
| 2519 | 21 | `public String hoverCrumb(Runnable redraw) throws Exception` | The pointer put over the crumb on a business's page ("Sectors ›", whose hover is its tooltip), the page drawn kept again (`redraw`), and the crumb looked at; then the pointer taken off and the page drawn again. |
| 2542 | 3 | `private Node crumb()` | The crumb on a business's page: the label that goes back to the list. |
| 2546 | 3 | `private static String clip(String s)` |  |

### THE BANK (KP5): the Overview and the six pages behind it, in both views, kept and built new, the hands on what (lines 2550-2613)

| line | len | member | says |
|---:|---:|---|---|
| 2556 | 3 | `public Page bank()` | The Bank tab on its Overview: nothing opened, the waterfall on this month, the Summary view. |
| 2565 | 9 | `public Page bank(String name, String area, String page, boolean statement)` | The Bank tab as a Page, as the screen draws it from its pool (the Overview when `area` is null, else the page `page` names behind it, in the Summary or the Statement view - which only Profit and Balance sheet have): i... |
| 2576 | 3 | `public static List<String> bankPages()` | The six pages behind the Overview, in the chip strip's order. |
| 2581 | 3 | `public static List<String> bankFolds()` | The keys of the six pages' "details" folds, in the pages' order. |
| 2586 | 6 | `public void bankOpen(String area, String page, boolean statement)` | The screen on its Overview (`area` null), or on a page behind it in a view, as a chip or the switch would put it (the next draw shows it). |
| 2594 | 3 | `public void bankLine(String key, boolean open)` | A fold ("details"), a statement line or a borrower's sectors opened or closed, by its key, as a click on it would. |
| 2599 | 3 | `public void bankYear(boolean year)` | The Profit page's waterfall on the last twelve months or on this month, as its chip would put it. |
| 2604 | 4 | `public void showBank()` | The Bank tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 2610 | 3 | `public void forgetBank()` | The Bank tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. |

### FINANCES (KP5): the hub and the six areas' pages, kept and built new, the hands on what reshapes them (an area and a (lines 2614-2733)

| line | len | member | says |
|---:|---:|---|---|
| 2621 | 3 | `public Page finances()` | The Finances tab on its hub: nothing opened, nothing folded open. |
| 2630 | 15 | `public Page finances(String name, String area, String page)` | The Finances tab as a Page, as the screen draws it from its pool (the hub when `area` is null, else the area and the page `page` names, or its first page when `page` is null): it opens with nothing folded open, no lin... |
| 2647 | 3 | `public static List<String> financeAreas()` | The six areas, in the hub's order. |
| 2652 | 3 | `public static List<String> financePages(String area)` | An area's pages, in its chip strip's order (the book's one page, Money's one, the fund's four). |
| 2657 | 3 | `public static List<String> financeFolds()` | The keys of the "details" folds on the area's pages: the bond market's two (every bond as tables, a bond's bids and asks). |
| 2665 | 6 | `public void financesOpen(String area, String page)` | The screen on its hub (`area` null), or on an area and a page as a chip or a door would put it (the next draw shows it): the fund on no security, no line's page and no Close it now open. |
| 2673 | 3 | `public List<ham.citybuildersim.Debt> financesPieces()` | The pieces of paper on the city's books, as the book lists them (the desk sorts them by when they fall due). |
| 2678 | 13 | `public void financesLine(ham.citybuildersim.Debt piece)` | A piece's own page opened under The book, as its desk row would (the next draw shows it); null puts the book back. |
| 2696 | 10 | `public void financesClose(ham.citybuildersim.Debt piece)` | Close it now opened on a piece, as its Close it now button would (the next draw shows it): its two boxes empty, and the back door to the line's page when that was open on the same piece. |
| 2708 | 4 | `public void financesTyped(int which, String text)` | What is typed in Close it now's two boxes (1 the face to buy back, 2 the most to pay), as the page keeps it: the next draw puts it in the box. |
| 2714 | 3 | `public javafx.scene.control.TextField[] financesCloseBoxes()` | Close it now's two boxes as the last draw made them (null before one has drawn them). |
| 2719 | 3 | `public void financesFold(String key, boolean open)` | A "details" fold on a Finances page opened or closed, by its key, as a click on it would. |
| 2724 | 4 | `public void showFinances()` | The Finances tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 2730 | 3 | `public void forgetFinances()` | The Finances tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. |

### Borrow, the bond market's book, the city's fund (KP5, slice C) (lines 2734-2815)

| line | len | member | says |
|---:|---:|---|---|
| 2737 | 5 | `public static List<String> borrowKinds()` | The kinds of paper Borrow offers, by key, in its tiles' order. |
| 2744 | 5 | `public static List<Integer> borrowTerms(String kind)` | The terms a kind is sold at (a bill's in months, a bond's in years). |
| 2751 | 3 | `public static List<String> borrowMoneys()` | The eight moneys the paper abroad may be asked in, by code. |
| 2760 | 11 | `public void financesBorrow(String kind, int term, String money, double ask, boolean callable, boolean hold)` | Borrow's choices as its tiles, columns, chips and switches would set them (the next draw shows them): the kind of paper, its term, the money it is asked in (abroad), the ask (0: none), whether a bond is asked callable... |
| 2773 | 6 | `public void financesAskTyped(String typed, String refused)` | What is typed in Borrow's ask box and not yet set, over the ask as it stands (null: nothing typed), and what the box said of an entry it could not read. |
| 2781 | 3 | `public javafx.scene.control.TextField financesAskBox()` | Borrow's ask box as the last draw made it (null before one has). |
| 2786 | 3 | `public void financesCap(boolean fill, double value)` | The highest yield the sale will take, as its chips would set it. |
| 2791 | 7 | `public List<Integer> bondIds()` | The numbers of the businesses' bonds outstanding, the largest first (the bond market's book names one). |
| 2800 | 7 | `public void financesBondBook(int id)` | A bond's book opened, as a click on its issuer's card would (the next draw shows it). |
| 2809 | 6 | `public List<String> fundSecurities()` | The securities the fund's Search lists with nothing typed, by lot key (the shares by value, then the bonds): the pages it may open. |

### the fund's words, read without a window (LF1c): a harness reads them on a played city (lines 2816-2949)

| line | len | member | says |
|---:|---:|---|---|
| 2819 | 8 | `public static List<String> fundActivityWords(Game g)` | What the fund's Activity page says on each line of its record, newest first, the rule's folded lines opened. |
| 2829 | 5 | `public static List<String[]> fundPositionLines(Game g, String key)` | The label and the figure on each line of a security's YOUR POSITION, by its lot key. |
| 2836 | 1 | `public static String fundHeroIncomeNote(Game g)` | What the INCOME LAST MONTH card says under its figure on this city's fund. |
| 2839 | 1 | `public static String fundIncomeNote(double transferLastMonth, double incomeLastMonth)` | ...and the note itself, by the transfer last month and the income: how much it covered, or with no transfer to cover what the income is made of. |
| 2842 | 1 | `public static String fundIncomeLabel(String kind)` | The label of a lot's income on its page, by the lot's kind (FundView.SHARE, RESCUE, BOND, PREFERRED, WARRANTS). |
| 2845 | 1 | `public static String fundWrittenOffWords(String name, boolean bond)` | The record's line for a security written off with what it was: a bond's face, or a company's shares. |
| 2848 | 1 | `public static String[] tradeValuationWords(double change, String shown)` | The Trade screen's words for a valuation change (the month's) and for what has been written off since founding: the chip, the sentence behind it, and the statement's note (LF1c). |
| 2849 | 1 | `public static String[] tradeForgivenWords(double forgiven, String shown)` |  |
| 2852 | 5 | `public List<String> fundHoldings()` | The lots the fund holds, by key. |
| 2859 | 6 | `public void fundPage(String page)` | The fund on one of its four pages (the next draw shows it): no security open. |
| 2867 | 9 | `public void fundOpen(String key, String from)` | A security's page opened by its lot key, as a row would (the next draw shows it), from the page named; the ticket for it fresh. |
| 2878 | 9 | `public void fundTicket(boolean buy, boolean byAmount, double amount, String priceKind, int steps, boolean review)` | The ticket on the security open set as its chips, steps and press would: buy or sell, by amount or by quantity, the figure, the price's kind and steps, and whether it is at its review. |
| 2889 | 3 | `public static List<String> fundPriceKinds()` | The ticket's price kinds, in the chips' words: fair, bid, ask, last and the player's own. |
| 2894 | 5 | `public void fundSearch(String query, String filter)` | Search's words and chip, as the box and the chips would set them (the next draw shows them). |
| 2901 | 3 | `public static List<String> fundFilters()` | Search's chips. |
| 2906 | 5 | `public void fundActivity(String group, int months)` | Activity's chip and how many months it shows. |
| 2913 | 3 | `public static List<String> fundGroups()` | Activity's chips. |
| 2918 | 3 | `public javafx.scene.control.TextField fundSearchBox()` | The fund's search box as the last draw made it (null before one has). |
| 2923 | 3 | `public void fundFold(String key, boolean open)` | A fold or an opened row of the fund, by its key ("fund:closed", "fund:life", "fund:issuer:...", "fund:small:...", "fund:act:..."), as a click on it would. |
| 2928 | 3 | `public static List<String> fundFolds()` | The keys of the fund's folds and opened rows that exist before anything is held: the closed lots' and the Rules & cash page's. |
| 2933 | 3 | `public void fundAsk(double ask)` | What the fund's PAY IN, DRAW OUT asks for, as its steps would set it. |
| 2941 | 3 | `public void fundOnShow(boolean here)` | Whether the fund's tab was on show before the next draw, as the show method tells it (FinancesScreen sets it): only then does the worth count on and a holding's payment pop when the month has moved, and (UserInterface... |
| 2946 | 3 | `public List<String> fundOpenRows()` | What the fund has opened so far (folds, grouped lines, bond issuers), for the harness to open every one of them. |

### the foreign default menu and the debt result menu (KP5, slice C) (lines 2950-2996)

| line | len | member | says |
|---:|---:|---|---|
| 2953 | 4 | `public Page foreignDefault()` | The foreign default menu: the page that asks twice before the city walks away from what it owes abroad. |
| 2968 | 13 | `public String financesBook(String kind, int term, double ask, boolean foreign, String money)` | Paper sold as the Borrow page's own button sells it, through the same calls (the quote struck on the city as it stands, then the booking the quote's card hands it to): the receipt's inputs are kept for the debt result... |
| 2983 | 6 | `public void financesBookedNothing(String summary)` | The receipt for nothing issued: no quote, the booking's words only (null: the page's own "Nothing issued."). |
| 2991 | 5 | `public Page debtResult()` | The debt result menu, drawn on what financesBook() last quoted and said. |

### THE MONEY SCREENS' HANDS (KP5, slice D): what KeptCheck's sections 55 to 68 reach that the slices' hands did not - the (lines 2997-3032)

| line | len | member | says |
|---:|---:|---|---|
| 3004 | 4 | `public ScrollPane pageScroller()` | The page's scroller (a Bank, Finances or fund page is a frame over a scroller), or null. |
| 3010 | 3 | `public void bankPick(String chip)` | A chip of the Bank tab picked, as a click on it would: "Overview" or one of the six pages, at its top (BankScreen.pick). |
| 3015 | 3 | `public void bankPickView(boolean statement)` | The Bank's Summary \| Statement switch picked, as a click on it would (BankScreen.pickView). |
| 3020 | 3 | `public void financesPick(String area, String page)` | A chip of the Finances tab picked, as a click on it would: the area's page at its top (FinancesScreen.open, no anchor). |
| 3028 | 4 | `public String financesSell(String kind, int term, double ask, boolean callable)` | Paper sold at home as the Borrow page's own button sells it (the same executeDebtLogic call, at the default highest yield): what the booking said. |

### the boxes, given the focus and pressed under (lines 3033-3132)

| line | len | member | says |
|---:|---:|---|---|
| 3036 | 7 | `public List<javafx.scene.control.TextField> textFields()` | Every text field on the page, in the page's order. |
| 3045 | 3 | `public javafx.scene.control.TextField yieldBox()` | The yield box of a cap's chips ("type a yield, 4.5": the rollover's on the hub, Borrow's at home) on the page now, or null. |
| 3053 | 15 | `public static String boxFocus(javafx.scene.control.TextField box, String text) throws ReflectiveOperationException` | Words typed into a box and the box given the focus (see historyFilterFocus()): how it got it - "requestFocus" when the scene took it, "setFocused by reflection" when this scene has no window to give it, or why it coul... |
| 3070 | 5 | `public static void boxBlur(javafx.scene.control.TextField box) throws ReflectiveOperationException` | The focus taken off a box, as another click would. |
| 3077 | 3 | `public static Object[] boxState(javafx.scene.control.TextField box)` | A box: the node, its text, its caret and whether it has the focus. |
| 3082 | 4 | `public static String boxType(javafx.scene.control.TextField box, String text)` | Text typed into a box as keys would, its listener running (outside a draw); the text it holds after. |
| 3088 | 4 | `public static String boxSet(javafx.scene.control.TextField box, String text)` | Kept.text on a box: the text it holds after. |
| 3097 | 7 | `public boolean pressChip(String words)` | A chip pressed on the page, as the pointer would press it: the first button or label with exactly these words that can be pressed (a chip strip's Button is fired, a step chip's Label is clicked). |
| 3106 | 5 | `public String chipWith(java.util.function.Predicate<String> test)` | The words of the first chip or step on the page that pass a test (its label), or null. |
| 3116 | 11 | `public int fundUnfold()` | Every foldable row of the fund's page clicked open, as the pointer would (a holding's bond issuer "▸ ...", the "and N more under ..." row, an activity line that groups): how many were opened. |
| 3129 | 3 | `public void fundFoldsClosed()` | Every fold and row the fund has opened closed again (its page drawn on the next draw). |

### the pictures, and how often they draw (lines 3133-3225)

| line | len | member | says |
|---:|---:|---|---|
| 3136 | 3 | `public static List<String> pictureKinds()` | The kinds of picture KeptCheck's section 64 watches. |
| 3140 | 14 | `private static boolean isPicture(String kind, Node n)` |  |
| 3156 | 7 | `public List<Pane> pictures(String kind)` | Every picture of a kind on the page now, in the page's order. |
| 3173 | 18 | `public int repaints(Pane picture)` | How many times a picture has drawn itself since the bench first asked about it (0 at that first asking): a chart counts its frames (TimeChart.paints); a picture that clears its children and builds them again when it d... |
| 3193 | 4 | `public static String figuresOf(Pane picture)` | What a bar was last told (RankBar): its figures, for comparing one month with the next; the picture's signature for any other. |
| 3199 | 5 | `public static String layoutReason(Pane picture)` | Why a picture is waiting for a layout: itself, or which of its parts, and the figures it was last told. |
| 3206 | 3 | `public static boolean layoutRequested(Pane picture)` | Whether a picture has asked to be laid out again (a bar told a figure that moved: RankBar.set). |
| 3214 | 11 | `public static String pictureSignature(Node picture)` | What a picture and everything in it show now, a line a node: two pictures with the same lines draw the same. |

### the animations a month starts (lines 3226-3347)

| line | len | member | says |
|---:|---:|---|---|
| 3229 | 8 | `public List<javafx.animation.Animation> pageAnimations()` | Every animation a kept draw has played (Kept.play) on a node of the page: the last on each node. |
| 3239 | 4 | `public boolean animating()` | Whether any animation of the page is running. |
| 3245 | 7 | `public int notAtRest()` | How many nodes of the page that were popped are not at rest (a scale other than 1). |
| 3254 | 4 | `public Node bankProfitFigure()` | The PROFIT figure on the page (the Overview's scorecard, or a page's status strip): the node under the word, or null. |
| 3259 | 5 | `private static boolean saysProfit(Node first)` |  |
| 3266 | 5 | `public Pieces.SegmentBar bankGauge()` | The Overview's capital gauge: the bar in the card that opens "CAPITAL", or null. |
| 3276 | 8 | `public boolean bankGaugeMoved()` | Whether the capital gauge would grow on the month that has just landed, by the screen's own reading: the measure that binds is the risk-weighted one, it is finite, and the ratio of the month before (the history's last... |
| 3286 | 11 | `public double[] gaugeNumbers()` | The gauge's figure now, the risk-weighted ratio of the month before it (the history's second last), and the scale the bar is drawn on: for a failure to say what it saw. |
| 3299 | 7 | `public List<Integer> bondIdsLasting()` | The numbers of the businesses' bonds outstanding, the ones that fall due last first (the largest first among those): books that outlast a long section. |
| 3308 | 4 | `public String fundWorth()` | The fund's worth as the hero says it (the page's one 28-point figure), or null. |
| 3314 | 3 | `public static String yieldText(Auction.Cap cap)` | The yield box's text for a cap, as a page built new shows it (CapChips.yieldText): 0.045 is "4.500" (KP2b). |
| 3319 | 3 | `public static Auction.Cap yieldCapOf(String typed)` | The cap a typed yield names, as Enter in the yield box sets it (CapChips.fromWords, in per cent): "4.5" is the cap at 4.5% (KP2b). |
| 3324 | 3 | `public Auction.Cap borrowCap()` | The highest yield Borrow's sale will take, as the page keeps it (KP2b). |
| 3329 | 3 | `public void financesCapDefault()` | The highest yield the sale will take put back to what Borrow opens with (fair and the tail). |
| 3334 | 7 | `public int paidLines()` | How many holdings the fund's page says paid something in the month just landed ("paid +D$26k this month"). |
| 3343 | 4 | `public void forgetMoney()` | The Bank tab forgotten and the Finances tab forgotten, as another city opened would leave them. |

### SECTORS (KP4): the list and a business's five pages, kept and built new, the hands on what reshapes them, the (lines 3348-3474)

| line | len | member | says |
|---:|---:|---|---|
| 3355 | 3 | `public Page sectors()` | The list the Sectors tab opens on: folded to its first row (Show more), nothing opened, the Summary view. |
| 3363 | 13 | `public Page sectors(String name, String key, String page, boolean statement)` | The Sectors tab as a Page, as the screen draws it from its pool (the list when `key` is null, else the business and its page, in the Summary or the Statement view): it opens with nothing folded open or noted, the stat... |
| 3378 | 3 | `public static List<String> sectorPages()` | The five pages of a business, in the strip's order. |
| 3383 | 5 | `public static List<String> freightDivisions()` | Freight's divisions by the words on their chips: All, Haulage, Rail, Ports. |
| 3390 | 6 | `public void sectorsOpen(String key, String page, boolean statement)` | The screen on the list (`key` null), or on a business's page in a view, as a card's click, a chip or the switch would put it (the next draw shows it). |
| 3398 | 3 | `public void sectorsMore(boolean more)` | The list's cards open to their second row (Show more) or folded to the first (Show less). |
| 3403 | 5 | `public void sectorsDivision(String label)` | Freight's Operations page on one of its divisions, by the words on its chip. |
| 3413 | 9 | `public void sectorsFold(String kind, String key, boolean open)` | One of the pages' folds opened or closed: "every" (the fold of every line, on a business's Operations page), "foods" (the goods a column folds into one row, on the shops' and the kitchens'), "book" (the order book, on... |
| 3424 | 6 | `public void sectorsNotes(String key, boolean open)` | Every note of a business's four statements opened or closed, as the toolbar's "open every note" would. |
| 3432 | 3 | `public void sectorsNote(String key, String table, int note, boolean open)` | One note of one statement of a business opened or closed, as a click on its line would. |
| 3442 | 15 | `public int sectorsNoteOf(String key)` | The first note of a business's income statement a click opens on lines this month, or 0 when none does: its line drawn (shown, and not nil in the statement's columns) and its note not empty, the statement's own test f... |
| 3459 | 4 | `public void sectorsColumns(boolean change, boolean share)` | The statements' change and common-size columns on or off, as the toolbar's two chips would. |
| 3465 | 4 | `public void showSectors()` | The Sectors tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 3471 | 3 | `public void forgetSectors()` | The Sectors tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. |

### the pictures (lines 3475-3507)

| line | len | member | says |
|---:|---:|---|---|
| 3478 | 3 | `public Pane refineryPicture()` | The refinery's picture on the page, or null. |
| 3483 | 3 | `public Pane industryPane()` | A materials industry's picture on the page, or null. |
| 3488 | 7 | `public List<Pane> legBars()` | Every leg's bar on the page. |
| 3497 | 5 | `public static Object painted(Node picture)` | What a picture last painted, by identity (null before a first paint): another object only where the month's view moved or the width did. |
| 3504 | 3 | `public static List<Node> partsOf(Pane picture)` | What a picture (or a bar) holds now, in its order. |

### the count-up and the sweep (lines 3508-3603)

| line | len | member | says |
|---:|---:|---|---|
| 3511 | 5 | `public Label sectorsKept()` | The KEPT figure on the page now: the list's first cell, or the open business's. |
| 3518 | 3 | `private static boolean isKept(Label l)` | Whether a label says KEPT, or "KEPT  ›" (the cell that is a door says its name and a chevron). |
| 3523 | 4 | `private static boolean saysKept(Node first)` | Whether a cell's first node is its "KEPT" label, or the row that holds it beside an (i). |
| 3529 | 11 | `public List<Label> sectorsCardFigures()` | The net income each card on the list shows, in the order the grid has them: its figure's label. |
| 3546 | 17 | `public String[][] sectorsWords(String key)` | The words the figures read this month, from the model as the screen reads it, with last month's beside: {the list's KEPT, then the open business's KEPT}, each as {last month's, this month's}, and the cards' as {last, ... |
| 3565 | 4 | `public static boolean counting(Node figure)` | Whether a count-up is running on a figure now (one was started and has not finished or been stopped). |
| 3571 | 3 | `public static javafx.animation.Animation countOf(Node figure)` | The count-up last started on a figure: the animation object, or null. |
| 3576 | 3 | `public boolean hasFall()` | Whether the page has an income waterfall. |
| 3581 | 4 | `public javafx.animation.Animation fallGrowth()` | The growth last started on the page's income waterfall (a new month that animates starts one), or null. |
| 3587 | 7 | `public javafx.animation.Animation ringSweep()` | The sweep last started on a ring of the page (the plant's), or null. |
| 3596 | 7 | `public boolean hasRing()` | Whether the page has a ring with an arc to sweep (a plant that runs). |

### the controls (the two dials of the oil reserve) (lines 3604-3776)

| line | len | member | says |
|---:|---:|---|---|
| 3607 | 7 | `public List<javafx.scene.control.Slider> sliders()` | Every slider on the page, in the page's order: the oil reserve's FILL dial first, then its RELEASE dial. |
| 3616 | 3 | `public double staged(String key)` | What a policy dial has staged under its key ("reserveFill", "reserveRelease"), or NaN when nothing is. |
| 3621 | 3 | `public void unstage(String key)` | A policy dial's staged value dropped. |
| 3632 | 3 | `public double[] passes() throws Exception` | The scene's CSS and layout, as a frame's pulse runs them (Scene.doCSSPass and doLayoutPass, which touch only what is dirty): {CSS ms, layout ms}. |
| 3636 | 22 | `private static double[] passes(Scene sc, javafx.scene.Parent root) throws Exception` |  |
| 3660 | 9 | `public int[] pixels()` | The page as pixels, ARGB, row by row: the menu column's snapshot, with its width and height first. |
| 3676 | 5 | `public List<String> signature()` | The page as it is now, a line a node in the order its code made them: the node's class; its words; its inline style; the values code set on it (the styleable ones whose origin is code); whether it has a click, a hover... |
| 3688 | 3 | `private static void sign(Node n, String path, List<String> out)` |  |
| 3697 | 30 | `private static void sign(Node n, String path, List<String> out, boolean unlaid)` | One node and what is under it. |
| 3734 | 12 | `private static String codeSet(Styleable n)` | Every styleable value a node has now, by property, where it is not the property's initial value: what code set and what its style made of it, both pages styled and laid out alike. |
| 3748 | 11 | `private static String shape(Node n)` | A shape's own geometry, which no style sets. |
| 3761 | 15 | `private static String pathOf(javafx.scene.shape.Path p)` | A path's elements as figures: each element's kind and its end point (and a curve's controls). |

### TRADE & THE WORLD (KP6, slice B): the six pages and a nation's page behind Partners, kept and built new, the hands on (lines 3777-3939)

| line | len | member | says |
|---:|---:|---|---|
| 3784 | 3 | `public Page trade()` | The Trade tab on its Overview: nothing folded open, the steps, goods rather than businesses, the exchange buying and asking nothing. |
| 3792 | 16 | `public Page trade(String name, String page)` | The Trade tab as a Page, as the screen draws it from its pool: the page `page` names (one of tradePages()), and on Partners the nation tradeNationPick() names - it opens with nothing folded open, the steps, goods, the... |
| 3810 | 8 | `public Page tradeNation(String name, int nation)` | A nation's page behind Partners (the number from 1: tradePartners() names some), as the table's link would open it. |
| 3820 | 3 | `public static List<String> tradePages()` | The six pages, in the strip's order. |
| 3825 | 10 | `public static List<String> tradeFolds(String page)` | The keys of the "details" folds and "more" lines a page of the tab has, outermost first (a fold inside a fold needs the one it is in open). |
| 3837 | 3 | `public static String tradeNationFold(int nation)` | The key of the fold of the rest of a nation's goods on its page. |
| 3842 | 6 | `public void tradeOpen(String page)` | The screen on a page, as a chip would put it (the next draw shows it): a nation's page only by tradeNationPick(). |
| 3850 | 5 | `public void tradeNationPick(int nation)` | The screen on a nation's page behind Partners, as a row's link would put it (the next draw shows it); 0 is the table. |
| 3857 | 6 | `public List<Integer> tradePartners(int limit)` | The nations the month's table ranks first, at most `limit`, by number: the ones a row, a strip card or a link opens. |
| 3865 | 6 | `public List<ham.citybuildersim.Good> tradeGoods()` | The goods the Partners board offers, those the city sold first. |
| 3873 | 3 | `public void tradeGood(ham.citybuildersim.Good good)` | The Partners board of a good, as its chip would pick it. |
| 3878 | 3 | `public void tradeSpan(boolean year)` | The Partners table of the year so far or of the month, as its chip would pick it. |
| 3883 | 3 | `public void tradeLine(String key, boolean open)` | A fold, or a "more" line, opened or closed by its key, as a click on it would. |
| 3888 | 3 | `public void tradeRiver(boolean river)` | The Month page on the river or on the steps, as its chip would put it. |
| 3893 | 3 | `public void tradeByBusiness(boolean business)` | What we trade by business or by good, as its chip would put it. |
| 3898 | 8 | `public void tradeBuying(boolean buying)` | The exchange buying or selling, as its toggle would put it (an ask the other way cleared). |
| 3908 | 3 | `public void tradeAsk(double thousands)` | The exchange asked for this much (thousands of the city's money; the card keeps it to what there is to deal). |
| 3913 | 4 | `public String[] tradePastBound()` | The words of the exchange's warning past the bound for the ask now made - title, line, what is behind its (i) - or null under the bound (XI2; pure). |
| 3919 | 3 | `public void tradeMoney(String code)` | The exchange dealing in one of the eight moneys by its code (null: the mix when buying, the largest holding when selling). |
| 3924 | 4 | `public void tradeDoor(String page, String target)` | A door of the tab to a page and a card on it (TradeScreen.open(page, target)): the real open, which draws the screen through its show method. |
| 3930 | 4 | `public void showTrade()` | The Trade tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 3936 | 3 | `public void forgetTrade()` | The Trade tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. |

### THE GOVERNMENT TAB (KP6, slice A): its four pages, kept and built new, the hands on what reshapes them (a page picked, a (lines 3940-4019)

| line | len | member | says |
|---:|---:|---|---|
| 3946 | 3 | `public Page government()` | The Government tab on its Overview: nothing folded open, no line opened. |
| 3954 | 8 | `public Page government(String name, String page)` | The Government tab as a Page, as the screen draws it from its pool, on the page `page` names (its first page when null): it opens with nothing folded open and no line opened. |
| 3964 | 3 | `public static List<String> governmentPages()` | The tab's four pages, in the chip strip's order. |
| 3969 | 3 | `public static List<String> governmentFolds()` | The keys of the folds the pages hold: the treasury's month, the paper, the output's months recorded, and the bridge's two columns' "N more". |
| 3974 | 9 | `public static List<String> governmentLines(String page)` | The lines of Revenue or Spending that open into who pays (the other pages have none). |
| 3985 | 3 | `public void governmentOpen(String page)` | The screen on the page `page` names, as a chip would put it (the next draw shows it). |
| 3990 | 3 | `public void governmentFold(String key, boolean open)` | A fold ("details" or a bridge column's "N more"), by its key, opened or closed, as a click on it would. |
| 3995 | 3 | `public void governmentLine(String page, String line, boolean open)` | A line of Revenue or Spending opened or closed into who pays, by its name, as a click on its row would. |
| 4000 | 3 | `public void governmentPick(String page)` | A chip of the Government tab picked, as a click on it would: the page, at its top (GovernmentScreen.open, no line). |
| 4005 | 3 | `public void governmentDoor(String page, String line)` | A door of the tab used, as its click would: the page opened with `line` opened and scrolled to (a budget line's name, or the Overview's BRIDGE or BALANCE). |
| 4010 | 4 | `public void showGovernment()` | The Government tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 4016 | 3 | `public void forgetGovernment()` | The Government tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. |

### POLICY AND ZONING (KP6, slice C): the hub and every page of the five areas, kept and built new, the hands on what res... (lines 4020-4197)

| line | len | member | says |
|---:|---:|---|---|
| 4026 | 3 | `public Page policy()` | The Policy tab on its hub: nothing opened, nothing staged. |
| 4035 | 9 | `public Page policy(String name, String area, String page)` | The Policy tab as a Page, as the screen draws it from its pool (the hub when `area` is null, else the area and the page `page` names, or the area's first page when `page` is null; a zone's page of Zoning is "zone:" an... |
| 4046 | 7 | `public static List<String[]> policyPages()` | Every fixed page of the five areas as {area, page}, in the tabs' order (Zoning's two; its zones' pages are policyZone()'s). |
| 4055 | 3 | `public static String policyZonePage(String sector)` | The page of the zone of a sector, as the area's tabs name it. |
| 4060 | 3 | `public static List<String> policySectors()` | Every sector's key, in the registry's order: each has a zone's page, zoned or not. |
| 4065 | 6 | `public void policyOpen(String area, String page)` | Policy on an area and page (`area` null: the hub), as a door or a tab would put it; the staged set dropped (the next draw shows it). |
| 4073 | 5 | `public void policyPick(String area, String page)` | A door of the tab pressed (PolicyScreen.open): the area and page at their top, through the real show method. |
| 4080 | 5 | `public void policyPickHub()` | The hub's door pressed (PolicyScreen.openHub). |
| 4087 | 3 | `public void policyLine(String key, boolean open)` | A fold, a payer's own dial, a course's own price or the folded zero payers (by its key) opened or closed, as a click on it would. |
| 4092 | 3 | `public java.util.Set<String> policyLines()` | The keys of the lines open now: what a page's folds hold. |
| 4097 | 3 | `public void policyStage(String key, double value)` | A change staged under a lever's key, as a release on its dial stages it (the next draw shows it). |
| 4102 | 3 | `public void policyUnstage(String key)` | A staged change taken back, as the tray's × does. |
| 4107 | 3 | `public void policyDrop()` | Everything staged dropped, as "Discard" or "Leave it as it is" does. |
| 4112 | 3 | `public java.util.Map<String, Double> policyStaged()` | What is staged now, by key, in the order it was staged. |
| 4117 | 3 | `public List<String> policyLevers()` | The keys of the dials the page on show has registered, in the order drawn: what the tray can name and apply. |
| 4122 | 4 | `public Node policyTray()` | The tray under the page, or null with nothing staged to show: the page's third top node. |
| 4128 | 7 | `public List<javafx.scene.control.Slider> policySliders()` | Every slider on the page, in the page's order (the dials': the first is the first card's). |
| 4137 | 7 | `public int policyArrows()` | How many "→" arrows the page shows: one a dial card's effect that has moved, so a card drawn at its thumb shows its after. |
| 4146 | 7 | `public List<VBox> policyDoors()` | The cards of the page that are doors - a card's ground, a hand and a click: the hub's area cards, a tax's card on Taxes (KP2b); not the vitals strip's cells (Pieces.limitCell), which have no ground. |
| 4155 | 3 | `public static String strictLine(Game g, double thumb)` | What HOW STRICT's line says for a thumb at a place on the dial (KP2b): the words under the dial at that step, as the screen's own function makes them. |
| 4160 | 3 | `public boolean pageSays(String words)` | Whether a label on the page says exactly these words (KP2b). |
| 4165 | 3 | `public static String policyZoneTaxKey(ham.citybuildersim.Zoning.Tax tax, String sector)` | The key a zone's tax dial stages under, as Policy's Taxes rows have it: the same dial, the sector's own offset (KP2b). |
| 4170 | 4 | `public boolean policyZone(String sector, double share)` | A sector zoned through the model (a share of the dry ground, at the going price), as the zone's page applies it; false while zoning is shut. |
| 4176 | 3 | `public void policyUnzone(String sector)` | A zone taken off through the model. |
| 4181 | 5 | `public List<String> policyZoned()` | The sectors with a zone now. |
| 4188 | 4 | `public void showPolicy()` | The Policy tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 4194 | 3 | `public void forgetPolicy()` | The Policy tab forgotten, as another city opened would leave it: nothing kept. |

### THE RESEARCH AREA AND CITY HISTORY'S FLAGS (R5, slice B1): the pure helpers the Found page and the History flags are ... (lines 4198-4202)

### the Overview, Funding and the schools' lines (R3b, slice B) (lines 4203-4326)

| line | len | member | says |
|---:|---:|---|---|
| 4206 | 3 | `public static String researchGrantKey(ham.citybuildersim.ResearchCatalog.Field f)` | The key a field's grant is staged under on Policy > Research > Funding. |
| 4211 | 3 | `public static String researchPrizeKey(ham.citybuildersim.ResearchCatalog.Field f)` | ...and its prize. |
| 4216 | 3 | `public static String researchPostsWords(List<ham.citybuildersim.ResearchView.SchoolPosts> posts)` | What a kind of school's card says of the research posts it carries (ResearchScreen.postsWords()), or null for none. |
| 4221 | 3 | `public static String researchSay(ham.citybuildersim.ResearchView.Card card)` | The sentence a Funding card ends in for this read of the field (ResearchScreen.sayWords()). |
| 4226 | 9 | `public boolean policyDoorClick(String title)` | A door card of the page (policyDoors()) that has a label with these words, clicked as a press on it would: false if there is none. |
| 4237 | 4 | `public String policyWhere()` | Where Policy is: the area and page the screen has open, "Research/Overview" (the hub: "/"). |
| 4243 | 3 | `public void policyApplyStaged()` | Everything staged applied through the tray's own path (PolicyScreen.applyStaged()), as Apply does; the next draw shows it. |
| 4248 | 5 | `public List<String> policyTrayWords()` | The tray's chips as the tray words them ("Engineering grant $1.2M → $1.4M"), in the order staged. |
| 4255 | 3 | `public static List<String> governmentSpendingNames()` | The names of the lines of the Government tab's Spending list, in order (GovernmentScreen.spendingNames()). |
| 4260 | 3 | `public List<Double> governmentSpendingAmounts()` | The amounts of those lines in this month's accounts, in the same order (GovernmentScreen.spendingAmounts()). |
| 4265 | 3 | `public static String[] governmentPlace(String line)` | Where a budget line of the Government tab is decided on Policy, {area, page}, or null (GovernmentScreen.policyPlace()). |
| 4270 | 3 | `public static int[] researchLevels(int[] months, int first, int last, double plotWidth)` | How high each dot of a timeline row stands (ResearchScreen.levels()) for the months given, at the width a plot is given: the dot and its gap are the screen's. |
| 4275 | 3 | `public static double researchSlot()` | What two dots must be apart not to stack, in pixels (the dot and its gap). |
| 4280 | 3 | `public static int[] researchTicks(int first, int last, double plotWidth)` | The months the timeline's year ticks stand at (ResearchScreen.yearTicks()), and the least width between two of them. |
| 4285 | 3 | `public static double researchTickRoom()` | The least width between two year ticks of the timeline, in pixels. |
| 4290 | 3 | `public static double researchX(int month, int first, int last, double plotWidth)` | Where a month stands across a plot (ResearchScreen.xOf()). |
| 4295 | 11 | `public List<double[]> researchDots()` | Every dot of the Found page's timeline, in the order drawn: {its left, top, width and height in its pane, then the pane's width and height}. |
| 4308 | 3 | `public static String flagCounts(int decisions, int discoveries)` | A flag's head counts, decisions and discoveries apart (TimeChart.flagCounts()). |
| 4313 | 3 | `public static String flagColour(String kind)` | The colour a flag of this kind is drawn in (TimeChart.flagColour()). |
| 4318 | 3 | `public static List<ham.citybuildersim.DecisionLog.Entry> decisionsInView(List<ham.citybuildersim.ChartModel.Flag> flags, int fr...` | The decisions of a window of flags, newest first, a discovery's left out (HistoryScreen.decisionsInView()). |
| 4323 | 3 | `public static int decisionsIn(ham.citybuildersim.ChartModel.Flag flag)` | How many decisions a flag holds, its discoveries left out (HistoryScreen.decisionsIn()). |

### THE HANDS THE KP6 HARNESS ADDS (KP6, slice D): what Trade counts up, and the width of its charts (lines 4327-4417)

| line | len | member | says |
|---:|---:|---|---|
| 4332 | 7 | `private static String headOf(VBox cell)` | The head word of a limit cell (Pieces.limitCell): its first child's words, whether that is a label or a row that starts with one. |
| 4341 | 8 | `public List<Label> tradeFigures()` | The two figures of Trade's strip that count up from last month's when a month lands: SOLD ABROAD's and BOUGHT ABROAD's, in that order (empty when the page has no such cells). |
| 4354 | 13 | `public String[][] tradeWords()` | The words the strip's two counted figures read, from the model as the screen reads them (TradeScreen.countFrom): {SOLD ABROAD, BOUGHT ABROAD}, each {last month's, this month's}; last month's is null while History hold... |
| 4369 | 7 | `public List<Pieces.MirrorRows> tradeBars()` | The mirror-row pictures on the page (the bars of what the city bought and sold abroad), in the page's order: the ones that grow out of their axis when a month lands. |
| 4378 | 8 | `public java.util.Map<Node, Tooltip> tooltips()` | Every tooltip on the page by the node that carries it, by identity: Kept.tip keeps a node's own tooltip from draw to draw, Tooltip.install on a draw's node makes a new one each time. |
| 4388 | 5 | `public java.util.Set<Node> nodesOnPage()` | Every node on the page, by identity. |
| 4395 | 7 | `public List<String> policyPayers(String tax)` | The keys of a tax's payers that show a row with a dial of their own (a payer's fold is "payer:" and its key), in the ranking's order: the first is the row at the top. |
| 4404 | 4 | `public String[] hunger()` | What WHO GOES WITHOUT (Pieces.whoGoesWithout) says now: {its figure's words, whether the door to the shops is "drawn" or "not drawn"}. |
| 4410 | 7 | `public List<double[]> chartWidths()` | Every time chart on the page with the width of the box it sits in: {the chart's width, the box's width}, in the page's order. |

### THE CLOCK ON EVERY RAIL SCREEN (KP7, slice D): the window's count of screens rebuilt in place, what the clock does after (lines 4418-4726)

| line | len | member | says |
|---:|---:|---|---|
| 4424 | 3 | `public int rebuiltInPlace()` | How many draws rebuilt the screen on show in place - the same screen drawn again with its page emptied - since the window was made (UserInterface.rebuiltInPlace). |
| 4429 | 3 | `public void clockRedraw()` | What the clock does after a month lands: the screen on show drawn again by its own redraw (UserInterface.redraw(), the call the clock's drawPaced() makes). |
| 4434 | 3 | `public String screenShowing()` | The name the screen on show gives the window: the one its show method passes to clearMenu() or keepMenu(). |
| 4443 | 11 | `public int[] rebuildProbe()` | The condition that moves rebuiltInPlace, caused: a page drawn the old way (clearMenu() and a page put in the menu column) twice under one name, then People shown through its real show method, an arrival from that name. |
| 4456 | 20 | `private Kept.Pool poolOnShow()` | The pool of the screen on show, by the name it gives the window, or null for a screen this does not know (a page drawn the old way, a menu). |
| 4481 | 4 | `public int[] drawnOnShow()` | The nodes the last kept draw of the screen on show made new and handed back: {made, kept}, or null where the screen on show draws through no pool this knows. |
| 4487 | 4 | `public void showLand()` | The land office shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 4493 | 4 | `public void showServices()` | The Services tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 4499 | 6 | `public Page withHome(Page p, String name, Runnable more)` | The page `p` under a name of the harness's own, with `more` run after its home as well: the harness's state on top of what the page opens on (a cell open, the measures column unfolded). |
| 4511 | 20 | `private String hoverLook(java.util.function.Supplier<Node> target, Runnable redraw) throws Exception` | The pointer put over the node `target` finds, the page drawn kept again (`redraw`), and the node looked at; then the pointer taken off and the page drawn again (the same as hoverKept(), for a node the kinds there do n... |
| 4539 | 29 | `public int[] canvasPixels()` | The page's canvases as one picture, in the order the page holds them, one under another ({width, height, then the pixels}, KeptCheck.alike()'s form; the empty part of a narrower canvas's row is nought): what no signat... |
| 4570 | 4 | `private Node rankingsLine()` | The first measure line on the Rankings tab's measures column that is not the one shown (the shown one is lit and has no hover): an HBox with a mouse-entered and a mouse-exited. |
| 4576 | 3 | `public String hoverRankingsLine(Runnable redraw) throws Exception` | The Rankings tab's measure line under the pointer: hoverKept()'s test, on the first line that is not lit (see hoverLook()). |
| 4581 | 3 | `private Pane rankingsRows()` | The table's picture (the rows are painted on canvases, with one region under them for the row the pointer is over), or null. |
| 4591 | 29 | `public String hoverRankingsRows(Runnable redraw) throws Exception` | The pointer moved over the first row of the Rankings table that lights under it, the page drawn kept again (`redraw`): the picture is the same node and the same row is lit, with no mouse-moved sent (a pointer that has... |
| 4626 | 22 | `public String rankingsRowsCursor(Runnable redraw) throws Exception` | The table's picture drawn kept once more, the city unchanged (KP2b): how many times its cursor was written by the draw, and the cursor after it. |
| 4650 | 4 | `private ScrollPane rankingsScroller(Pane rows)` | The scroller the table's picture sits in, or null. |
| 4663 | 51 | `public String hoverRankingsScroll(Runnable redraw) throws Exception` | The pointer over a row of the Rankings table while the page scrolls under it (KP2b): a wheel scrolls the page, the rows move under the pointer, and JavaFX sends the table no mouse-moved (it re-picks at a pulse and sen... |
| 4716 | 10 | `private static String bandNow(Pane rows, Region lit, double sceneY, boolean[] lights, double before)` | The band against the row under a pointer at this place in the scene: null when it is lit on that row (or not lit, where that row does not light), else the difference. |

### THE RANKINGS TAB (KP7, slice B): the tab as it opens, kept and built new, the hands on what reshapes it (a board, the... (lines 4727-4933)

| line | len | member | says |
|---:|---:|---|---|
| 4738 | 15 | `public Page rankings()` | The Rankings tab as a Page, as the screen draws it from its pool: it opens on GDP and the full list, Real world 2025 in the real US dollar, per year and at this month's rate, the first six measures pinned, nothing fol... |
| 4755 | 5 | `public static List<String> rankingsMeasures()` | The measures of the file, by their key, in the file's order: the ones the tab can show. |
| 4762 | 4 | `public static boolean rankingsSourced(String key)` | Whether the measure ranks on Real world 2025 against real places (false: only the player's cities rank on it). |
| 4768 | 3 | `public static List<String> rankingsBoards()` | The boards, by name: Real world 2025 and Your world. |
| 4773 | 3 | `public static List<String> rankingsMoneys()` | The moneys, by name: the city's, the game's US dollar and the real one. |
| 4778 | 3 | `public static List<String> rankingsPrices()` | The prices, by name: per year and static. |
| 4783 | 3 | `public static List<String> rankingsConverts()` | The conversions, by name: this month's rate, constant 2015 and PPP. |
| 4788 | 3 | `public static List<String> rankingsFilters()` | The filters of the table, by name: All, Nations, Cities, Your size and Your cities. |
| 4792 | 5 | `private static List<String> enumNames(Enum<?>[] values)` |  |
| 4799 | 3 | `public boolean rankingsHasNations()` | Whether the city has nations of its own, so that Your world is a board it can show (else the screen draws Real world 2025 in its place). |
| 4804 | 3 | `public void rankingsMeasure(String key)` | The measure shown, by its key, as a line on the measures column or a pinned rank on the card would show it. |
| 4809 | 4 | `public void rankingsBoard(String board)` | The board picked, by name, as its chip would (the money, the prices and the conversion as they are). |
| 4815 | 4 | `public void rankingsMoney(String money)` | The money picked, by name, as its chip would. |
| 4821 | 4 | `public void rankingsPrice(String prices)` | The prices picked, by name, as its chip would (they move only the saved cities' rows, and are locked in other cases). |
| 4827 | 4 | `public void rankingsConvert(String convert)` | The conversion picked, by name, as its chip would. |
| 4833 | 3 | `public void rankingsFilter(String filter)` | The filter picked, by name, as its chip would. |
| 4838 | 3 | `public void rankingsAround(boolean around)` | Full list or Around you, as its chip would put it. |
| 4843 | 3 | `public void rankingsAtAge(boolean atAge)` | Your cities at Now (false) or Your age (true), as its chip would put it. |
| 4848 | 3 | `public void rankingsBrowseAll(boolean all)` | The measures column folded to the pinned and the group shown (false) or opened on every group (true), as "Browse all" would put it. |
| 4853 | 4 | `public void rankingsPin(String key, boolean on)` | A measure pinned or unpinned, by its key, as the pin on its line would. |
| 4859 | 3 | `public void rankingsDrawer(String place)` | A place's drawer opened, by its id (a nation's or a region's in the file, "city", or a saved city's "saved:" and its slot), as its row or a neighbour on the card would; null closes it. |
| 4864 | 14 | `public List<String> rankingsDrawers()` | The ids a drawer can be opened on: the city, the file's first nation and first region, and the saved cities the screen has read. |
| 4883 | 5 | `public boolean rankingsSaveSlot10()` | The city saved into slot 10 of the folder it plays in, so that the Your cities board has a row to read. |
| 4890 | 3 | `public void rankingsRead()` | A read of the saved cities started on the screen's worker, as an entry to the tab starts one: its answer comes back through the FX thread (see rankingsReadLanded()). |
| 4895 | 4 | `public boolean rankingsReadLanded()` | Whether a read has landed: none is on the worker and the screen holds a snapshot. |
| 4901 | 4 | `public String rankingsSavedLeft()` | What the screen's snapshot left off the board, by why (SavedCities.left()), or "no snapshot" before a read has landed (FB6, KS1-Saved's trace). |
| 4907 | 4 | `public String rankingsReadState()` | The read's state on the screen, for a read that has not landed: on the worker or not, and a snapshot held or none. |
| 4913 | 4 | `public int rankingsSavedOnBoard()` | How many saved cities the screen's snapshot puts on the board (0 before the first read has landed). |
| 4919 | 3 | `public void rankingsUnread()` | The snapshot of the saved cities let go, as a tab not yet entered has it ("Your cities (reading...)" and no saved row). |
| 4924 | 4 | `public void showRankings()` | The Rankings tab shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 4930 | 3 | `public void forgetRankings()` | The Rankings tab forgotten, as another city opened would leave it: nothing kept, nothing just landed. |

### HOUSEHOLD MONEY (KP7, slice A): People's second page, kept and built new, the hands on what reshapes it (the view, a ... (lines 4934-5032)

| line | len | member | says |
|---:|---:|---|---|
| 4941 | 3 | `public Page householdMoney()` | Household money as it opens: no cell open, the view "One household", the city's month's statement folded. |
| 4946 | 11 | `public Page householdMoney(String name)` | ...under a name of the harness's own. |
| 4959 | 3 | `public static List<String> householdViews()` | The page's two views, in the strip's order. |
| 4964 | 3 | `public void householdView(String view)` | The view picked, as the strip's chip would put it (the next draw shows it). |
| 4973 | 14 | `public List<String> householdCells()` | The cells of the grid that can be opened on the city as it is, by the key the grid opens them under, in the grid's order: a family's cell with anybody in it ("tier:SHAPE", the tier's number), then the rows below the r... |
| 4989 | 4 | `public void householdCell(String key)` | A cell of the grid opened by its key (see householdCells()), or none ("" - the quiet books), as a click on it would (the next draw shows it). |
| 4995 | 3 | `public String householdOpenCell()` | The cell the grid has open now: a draw closes one whose households have gone, so this is the answer after a draw. |
| 5000 | 3 | `public void householdStatement(boolean open)` | The city's month's statement ("the month as a statement") opened or closed, as a click on its toggle would. |
| 5008 | 4 | `public void householdColumnWidth(double width)` | The menu column laid out `width` wide (its side padding takes the rest of WIDTH) or, for 0 or less, at WIDTH again: the books sit beside the grid from PeopleScreen.BESIDE and stack under it below that. |
| 5014 | 7 | `public Boolean householdBeside()` | Whether the open cell's books sit beside the grid on the page as laid out, or null when the page has no books pane. |
| 5023 | 4 | `public void showHousehold()` | Household money shown through its real show method, as the rail's tab or a month landing shows it (the chrome, then the page kept). |
| 5029 | 3 | `public void forgetHousehold()` | Household money forgotten, as another city opened would leave it: nothing kept (People's page too, which shares the screen's forget()). |

### THE DRAWER'S BAR, THE CONSTRUCTION TAB AND THE SPEED'S READING (UP7) (lines 5033-5240)

| line | len | member | says |
|---:|---:|---|---|
| 5046 | 7 | `public double drawFrameKept(boolean inCity, boolean open)` | The bar and the tab drawn kept, from their pools (the first draw makes every node): the draws' own milliseconds. |
| 5055 | 3 | `public void framePinned(boolean pinned)` | The drawer pinned or not, as its pin's click leaves it (the bar's look follows at the next draw). |
| 5060 | 3 | `public boolean framePinned()` | Whether the drawer is pinned now. |
| 5065 | 3 | `public boolean frameOpen()` | Whether the construction panel is open now, as the tab's click leaves it. |
| 5070 | 3 | `public int[] frameCounts()` | The nodes the last draws made new and handed back: {bar made, kept, tab made, kept}. |
| 5075 | 5 | `public List<Node> frameKids()` | The bar's boxed nodes and the tab's, in order: the same objects from one kept draw to the next. |
| 5082 | 3 | `public Node bar()` | The drawer's bar, made once. |
| 5087 | 3 | `public Node tab()` | The construction tab, made once. |
| 5092 | 3 | `public Label pinLabel()` | The bar's pin and its close, in order. |
| 5096 | 3 | `public Label closeLabel()` |  |
| 5101 | 3 | `public Label tabCount()` | The tab's count (the kinds of building on site). |
| 5109 | 10 | `public void showFreshFrame(boolean inCity, boolean open) throws Exception` | The bar and the tab built new in place of the kept ones - the pools swapped for empty ones, so every node is made again, as every redraw did before UP7 - and laid out. |
| 5121 | 10 | `public void restoreKeptFrame() throws Exception` | The kept nodes and pools back where they were, and laid out. |
| 5133 | 4 | `public double[] framePasses() throws Exception` | Both scenes' CSS and layout, as a frame's pulse runs them: {CSS ms, layout ms}, summed. |
| 5139 | 8 | `public List<String> frameSignature()` | The bar's signature, then the tab's, a line a node (see signature()); each box itself is signed too: its style, its tooltip, its handlers. |
| 5149 | 3 | `public int[][] framePixels()` | The bar's pixels and the tab's, each as pixels() gives them: {bar, tab}. |
| 5154 | 3 | `public String spoilFrame()` | The tab's count made wrong by hand: the first label with a digit gets a "9" in front (see spoil()); what it said, or null. |
| 5162 | 5 | `public Sets[] frameSets(boolean inCity, boolean open)` | A second draw of the bar and then of the tab, the city unchanged, each with a listener on every node of it (redrawSets): what they wrote. |
| 5169 | 3 | `public void pace(ClockPace pace)` | The header's pace swapped for this one: a ClockPace on a clock KeptCheck moves by hand puts the speed's reading in a known state. |
| 5174 | 3 | `public static double[] speedRungs()` | The speed ladder's rungs, in times real speed, lowest first: the index speed(int) takes. |
| 5179 | 3 | `public static String speedWords(int rung)` | A rung's words as the header writes them ("0.25×", "50×"). |
| 5184 | 3 | `public Label speedLabel()` | The header's speed reading, kept: the same label from one draw to the next. |
| 5189 | 3 | `public double speedWidth()` | The one width the reading holds, as the window measures it. |
| 5194 | 3 | `public void paintSpeed()` | The reading repainted in place, as the clock does between redraws. |
| 5199 | 3 | `public static double speedTextWidth(String text)` | A string's width in the reading's own font (the figures' face, 11 px), as the window measures the reading's widest text. |
| 5204 | 4 | `public boolean speedClipped()` | Whether the reading's words are cut short: the text its skin draws is not the text it was given. |
| 5213 | 12 | `public double[] speedOldWay(String text) throws Exception` | A label made as the reading was before UP7 - min width 34, the same style, this text - laid out alone on a scene of its own, as a row lays it out: {its width, its preferred height}. |
| 5227 | 3 | `public double speedHeight()` | The reading's preferred height, as laid out. |
| 5232 | 3 | `public Sets speedSets(Runnable draw)` | What the reading and the nodes round it wrote while `draw` ran, by redrawSets (the header's second draw, or paintSpeed()). |
| 5237 | 3 | `public double headerChildX(int i)` | The x of the header's i-th child, as laid out. |

### FB2: THE CAUSAL FIXTURES OF THE REPAIRS (KL1, KS1-UI, BF1-K) (lines 5241-5603)

| line | len | member | says |
|---:|---:|---|---|
| 5252 | 6 | `private Pane probeHost(Node n, double w, double h)` | A node on a scene of its own, themed as the bench's pages are, so that its CSS and layout can be run. |
| 5260 | 4 | `private Pane probeFixed(Region n, double w, double h)` | ...with the node left to be sized by hand: a plain Pane autosizes its managed children to their preferred sizes at every layout. |
| 5265 | 5 | `private static List<String> signLines(Node n)` |  |
| 5272 | 3 | `private static ham.citybuildersim.Buyback.Result ladderQuote(double scale, boolean taken)` | A quote of the buyback ladder: six slices of Buyback.Holder's four, their faces times `scale`, the face taken (when `taken`) too. |
| 5277 | 10 | `private static ham.citybuildersim.Buyback.Result ladderQuote(double scale, boolean taken, int only, double add)` | ...with `add` more face on slice `only` (none: -1). |
| 5295 | 29 | `public int[] ladderProbe() throws Exception` | KL1: a kept BuybackLadder told a quote and then the same quote with every face moved by 1.4e-8 of itself (what GW1's city moved them by in months 2 and 6 with nothing typed), with nothing taken and with part of it tak... |
| 5330 | 22 | `public List<List<String>> headerTileProbe() throws Exception` | KS1-UI: a header tile told the same words at widths that make it show its longest wording, then the next, then the shortest, and back (the RATE tile's "D$1,000,000,000.00 per US$" at the rate's ceiling is the longest ... |
| 5358 | 20 | `public List<List<String>> segmentBarProbe() throws Exception` | HW1's Your rate, mended under KS1-UI: a SegmentBar whose dial tick is named when the rate is away from the dial and not when it is near (FinancesScreen.realCard's `near ? |
| 5384 | 43 | `public int[][] partnersChartProbe(int levels) throws Exception` | BF1-K: Partners' chart told where the next buyers join, then other places (a fraction of a pixel on), then the first again: for each telling, how the kept chart's picture and a chart built new's, told the last, part -... |
| 5433 | 25 | `public List<List<String>> fundRowsProbe() throws Exception` | BF1-K: Fund Search's rows told securities whose price, move and held words come and go (a bond has a price and none held, a share held has both...) at the same places, as FP1's rows are kept in their place when the ma... |
| 5459 | 9 | `private static Node foundRows(FundScreen fs, Game g, String[][] state)` |  |
| 5470 | 1 | **type** `public record RowsProbe(List<List<String>> signatures, int[] made, int[] madeAgain)` | KS1-Gov's probe: what each state's draw made new and what its unchanged redraw made, and the page's signature, kept beside built new. |
| 5480 | 33 | `public RowsProbe governmentRowsProbe() throws Exception` | KS1-Gov: the pieces the Government Overview's lines come and go in - the ring's arcs and its key's rows (a slice a line), the bridge's steps (a step a line, the ones at nothing left out) and the treasury statement's r... |
| 5514 | 16 | `private static Node governmentRows(String[] lines, String[] steps)` |  |
| 5542 | 19 | `public int[] parkedCullProbe()` |  |
| 5563 | 2 | **type** `public record LadderQuantum(double quantum, double tinyPx, double bigPx, boolean tinyKept, boolean tinyEqua...` | KL1's quantum probe: what a face's drift does to a bar, against the picture's own quantum. |
| 5571 | 32 | `public LadderQuantum ladderQuantumProbe() throws Exception` | KL1: one holder's face moved so that its bar moves by a hundredth of a pixel (under FinancesScreen.BuybackLadder.PIXEL_QUANTUM: the ladder is the picture it was, the nodes it held) and by half a pixel (over it: it is ... |

