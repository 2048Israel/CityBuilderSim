# HistoryScreen.java - 3,374 lines · 124 methods · 25 constants · interface

`ham/citybuildersim/ui/HistoryScreen.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> City History: the city as a shape over time.
> 
> Since 0.7.37 the page is laid out at its own width, in Build's style
> (THE PAGE AT ITS WIDTH): a head with "Write the year book" in it, a strip
> about the city's life - its age, its hard times, what is running now and
> the decisions it has made - then two pinned charts as cards, the big
> chart, a card per line it draws, the hard times and decisions in view
> (each a click that moves the chart there), the picker, and the month's
> prices against the world's, folded.
> 
> The charts: two small pinned ones, then one big chart with lines
> overlaid - one unit on a real axis, two on two real axes, three or more
> mapped onto 0-100 by their own range over the window - with recessions
> shaded and named, the episodes on a lane under it and the player's
> decisions as flags; a legend that carries the real values, presets for
> the questions a player usually has, a picker folded into its groups, and
> the year book written out on request (the page as of 0.7.5 is under THE
> PAGE, REDRAWN; real GDP drawn in layers on a toggle since 0.7.6, under
> GDP IN LAYERS). Since 0.7.23 the charts are TimeCharts - dragged, wheeled
> and ranged like a market chart, with an overview and a full screen - over
> one window of months (THE CHART, REBUILT; FULL SCREEN). The first screen
> split out of UserInterface (2026-09-18), chosen because it is the most
> self-contained: it reads the window's game and root, calls clearMenu()
> and scrolled(), and nothing else in the shell reads it but the rail.
> 
> The text came over exactly as it was inside UserInterface - the two banners,
> THE HISTORY SCREEN and THE RECORD, and everything under them - with the
> shell's members reached through ui. Nothing was rewritten on the way. The
> goods table followed later the same day (it had sat under the shell's THE
> STATEMENT banner); since 0.7.37 it is PRICES THIS MONTH.

**Uses:** [Palette](Palette.md) (153), [Kept](Kept.md) (116), [YearBook](YearBook.md) (55), [HistorySave](HistorySave.md) (48), [TimeChart](TimeChart.md) (27), [Icons](Icons.md) (23), [ChartModel](ChartModel.md) (21), [PriceIndex](PriceIndex.md) (15), [CityCalendar](CityCalendar.md) (14), [DecisionLog](DecisionLog.md) (12), [Money](Money.md) (12), [Good](Good.md) (10), [GamePrefs](GamePrefs.md) (9), [Crime](Crime.md) (6), [FamilyStructure](FamilyStructure.md) (6), [GoodsMarket](GoodsMarket.md) (6), [Equity](Equity.md) (5), [Currency](Currency.md) (5), [Sector](Sector.md) (5), [Pieces](Pieces.md) (4), [UserInterface](UserInterface.md) (3), [Sectors](Sectors.md) (3), [SectorScreen](SectorScreen.md) (3), [ResearchView](ResearchView.md) (2), [GameFiles](GameFiles.md) (2), [LandManager](LandManager.md) (2)

**Used by (15):** [BankScreen](BankScreen.md), [ChartFitBench](ChartFitBench.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [KeptBench](KeptBench.md), [LandScreen](LandScreen.md), [MapView](MapView.md), [PolicyScreen](PolicyScreen.md), [RankingsScreen](RankingsScreen.md), [ResearchScreen](ResearchScreen.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [VaultTable](VaultTable.md)

## Sections

| line | section |
|---:|---|
| 67 | THE HISTORY SCREEN |
| 549 | THE RECORD |
| 645 | THE PAGE, REDRAWN (0.7.5) |
| 682 | THE PAGE AT ITS WIDTH (0.7.37) |
| 734 | · SEEDED ONCE, AND THEN LEFT ALONE. |
| 1017 | · the head |
| 1166 | · the strip |
| 1343 | · a chip on this page, and the thing pressed held still (0.7.5) |
| 1453 | THE PINS (0.7.5) |
| 1693 | GDP IN LAYERS (0.7.6) |
| 1794 | THE BIG CHART (0.7.5) |
| 1891 | THE CHART, REBUILT (0.7.23) |
| 2001 | FULL SCREEN (0.7.23) |
| 2226 | WHAT EACH LINE DID (0.7.37) |
| 2811 | HARD TIMES AND YOUR DECISIONS (0.7.37) |
| 3172 | PRICES THIS MONTH (0.7.37; EVERY GOOD, ON ONE PAGE before) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 182 | `HistoryScreen.TO_CHART` | `"chart", TO_HARD_TIMES = "hard times"` | The two places a draw can be asked to scroll to: the row above the big chart, and the hard times' heading. |
| 209 | `HistoryScreen.TRACES` | `withTheCrime(withTheHouseholds(withTheSectors(withTheMarket(new Trace[] { new...` |  |
| 587 | `HistoryScreen.PRESETS` | `{ new Preset("What money costs", "the borrowing rate, the price level, and ho...` |  |
| 888 | `HistoryScreen.FRAME_CHROME` | `160` | The frame's height before it is laid out, for the scroller's first guess at what is left of the stage. |
| 891 | `HistoryScreen.STAGE_REST` | `36` | What the stage keeps under the scroller once the frame is laid out - BankScreen's and Trade's. |
| 894 | `HistoryScreen.SLACK` | `2` | What a canvas leaves unused of the width it is given (0.7.40), so a pixel's rounding never makes it wider than what holds it. |
| 968 | `HistoryScreen.BOUND_TO_FRAME` | `"history.frame", BOUND_TO_SCROLLER = "history.scroller", FOLLOWS_VIEWPORT = "...` | Keys in a kept scroller's, body's and frame's properties for what is bound to them: the frame the scroller's height reads, the scroller the body's width reads, the viewport listener, the frame's height listener (KH1). |
| 1020 | `HistoryScreen.LEAD_INFO` | `"Every month the city has lived, and what it did.The big chart draws the line...` | The title's (i): the old lead, and how the big chart is read. |
| 1026 | `HistoryScreen.YEAR_BOOK_INFO` | `"Writes the whole run out as plain text - every series the history " + "keeps...` | What "Write the year book" writes (the old SEND THIS RUN TO SOMEBODY paragraph). |
| 1468 | `HistoryScreen.SMALL_CHART` | `120` | How tall a pinned chart's plot is: 150 until 0.7.37, 120 since, so the big plot ends above the fold at 1,389 x 868 (D2). |
| 1726 | `HistoryScreen.LAYERED` | `"realGdp"` | The one line this page can draw in layers. |
| 1729 | `HistoryScreen.LAYERED_LINE` | `Palette.TEXT_HEAD` | What the GDP line is drawn in over the layers: the headings' ink, which no step of the blue ramp is near. |
| 1732 | `HistoryScreen.LAYER_NAMES` | `{ "consumption", "investment", "government", "net exports" }` | What each part is called on the key and in the crosshair, in YearBook.GDP_PARTS' order. |
| 1799 | `HistoryScreen.BIG_CHART` | `380` | How tall the big chart's plot is on the page; the lanes and the overview are under it. |
| 1802 | `HistoryScreen.CONTROLS` | `130` | Room kept at the right of the preset row for "clear all" and "log". |
| 2080 | `HistoryScreen.FULL_PAD_TOP` | `14, FULL_PAD_SIDE = 22, FULL_PAD_BOTTOM = 12` | The full-screen pane's padding: above the chart, beside it on each side, and below it, in pixels; the chart gets the window less these. |
| 2425 | `HistoryScreen.MOVE_MILLIS` | `600` | How long a figure takes to slide to a month's new reading, in milliseconds - the range bar's dot; the figure counts on over SectorScreen's own time. |
| 2622 | `HistoryScreen.AXES_INFO` | `"Lines measured in the same thing are drawn against each other on a real axis...` | PICK WHAT TO DRAW's (i): how the lines share axes. |
| 2829 | `HistoryScreen.HARD_TIMES_SHOWN` | `5` | At most this many hard times are listed in view; the rest are counted, and in the details. |
| 2832 | `HistoryScreen.DECISIONS_SHOWN` | `8` | At most this many decisions are listed in view. |
| 2835 | `HistoryScreen.KINDS` | `{ "recession", "depression", "slump", "epidemic", "financial", "currency", "i...` | The kinds of episode, in the order the details list them. |
| 2838 | `HistoryScreen.KIND_NAMES` | `{ "Recessions", "Depressions", "Slumps", "Epidemics", "Financial crises", "Cu...` | ...and what the details call each, in that order. |
| 3195 | `HistoryScreen.AT_END` | `1e-6` | How near an end of its band a price must be to be AT it - a rounding hair of the band (the market strikes an end exactly). |
| 3198 | `HistoryScreen.BAND_ROOM` | `1.25` | The scale a good's row is drawn on: the band from 0 to 1, and room past its ceiling for the month's trade. |
| 3201 | `HistoryScreen.GOODS_INFO` | `"What a unit costs here this month, against what the world pays for one and "...` | The section's (i). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 63 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 100 | `final java.util.LinkedHashSet<String> historyPicked` | What the player has ticked, and how far back they are looking. |
| 103 | `boolean historySeeded` | Whether the first-visit preset has been handed over; see showHistoryMenu. |
| 113 | `final ChartModel chartWindow` | The window of months the charts show (0.7.23): the big chart's, which the two small ones follow - dragged, wheeled and ranged by the player, and following the newest month while its right edge is on it (ChartModel). |
| 116 | `final java.util.Set<String> hiddenLines` | The lines the big chart's legend has switched off, by key: still picked and read below, not drawn (0.7.23). |
| 123 | `boolean historyLog` | The big chart on a log scale, when what is picked allows it; see logRefusal(). |
| 132 | `boolean gdpLayers` | Real GDP drawn in layers - consumption, investment and government stacked from zero, the line over them (0.7.6) - on its small chart, and on the big one when it is picked alone. |
| 135 | `String historyFilter` | What is typed in the picker's filter box, kept so a month's redraw does not wipe it. |
| 142 | `final java.util.Map<String, Boolean> groupOpen` | Which of the picker's groups the player has opened or closed, by name. |
| 149 | `final java.util.Set<String> folds` | The folds this page keeps open - the hard times by kind, and every good's price - by name (0.7.37). |
| 156 | `private final java.util.Map<String, Double> shownBefore` | What each pin and each reading card last showed, by "pin:side:key" or "card:key" (0.7.37), so a month landing on the open page counts the figure on from it and slides the range bar's dot (SectorScreen.countUp()). |
| 159 | `private int drawnMonth` | The game month this page was last drawn at; -1 before its first draw. |
| 162 | `Kept.Pool pool` | The page's kept nodes (Kept; study UP4, KP1): the page is drawn over them on each redraw. |
| 176 | `private final java.util.Map<String, Integer> episodeSeen` | The month each named episode was first listed, by kind and first month (0.7.37): an episode the list did not have at the previous draw is NEW for the month it appears. |
| 177 | `private int episodesSeenAt` |  |
| 180 | `private String scrollTarget` | Where the next draw scrolls the page to - the chart's top, or the hard times - once it is laid out; null: the scroll memory's. |
| 185 | `private javafx.scene.Node chartTop, hardTimesTop` | This draw's nodes a scroll can land on: the row above the big chart and the hard times' heading. |
| 194 | `private double pageWidth` | The page's inside width, as the scroller gives it (0.7.37; GRAPH's 760 until then; read off the viewport, not the page, since 0.7.40): the big chart, the pins and the rows that wrap are drawn at it, and the canvases f... |
| 197 | `private javafx.scene.layout.FlowPane presetFlow` | The preset row's flow, so a resize can rewrap it. |
| 1059 | `String bookExportSaid` | What the last export did, kept so it survives the redraw - the whole of it in a few lines (bookExportSaid), and its parts for the card. |
| 1060 | `private String bookFolder` |  |
| 1061 | `private final List<String> bookFiles` |  |
| 1346 | `private javafx.scene.control.ScrollPane page` | The page's scroller, kept so a click can be put back where it was after the rebuild. |
| 1349 | `private String pressed` | The chip last pressed, by name, and how far down the window it was; the next rebuild spends it. |
| 1350 | `private double pressedAt` |  |
| 1353 | `private final java.util.Map<String, javafx.scene.Node> named` | This build's chips by name, so the rebuild can find the one that was pressed. |
| 1356 | `private TextField filterField` | The picker's filter box, so a redraw can hand the focus back to it; see showHistoryMenu. |
| 1581 | `private final TimeChart[] smallCharts` | The two small charts' nodes, kept from one redraw to the next, as the big chart is: each follows the big chart's window as it is dragged, which a chart rebuilt every month could not (0.7.23). |
| 1921 | `private TimeChart bigChart` | The big chart, made once; see the banner. |
| 1924 | `private boolean chartFull` | Whether the big chart has the whole window (0.7.23); see FULL SCREEN. |
| 2018 | `private VBox fullPane` | The pane the chart is laid in over the whole window, and its clock line. |
| 2019 | `private Label fullClock` |  |
| 2541 | `private VBox pickerGroups` | The picker's groups box, the one the last draw made, so the filter box's listener (added once) refills the right one. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 3315 | **type** `final class HistoryScreen` | City History: the city as a shape over time. |
| 65 | 1 | `HistoryScreen(UserInterface ui)` |  |

### THE HISTORY SCREEN (lines 67-548)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 4 | `void forget()` | A new city or a load: no month has just landed, and nothing of the last city's page is kept. |
| 207 | 1 | **type** `record Trace(String key, String label, String group, String unit)` | One plottable line: where it comes from and how to read it. |
| 422 | 9 | `static Trace[] withTheCrime(Trace[] fixed)` | One series per reason for crime, added to CRIME. |
| 433 | 9 | `static Trace[] withTheHouseholds(Trace[] fixed)` | One series per household shape, appended after the sectors. |
| 451 | 11 | `static Trace[] withTheMarket(Trace[] fixed)` | ...and a share price per company, one trace each, generated off the register's own list so a company added there is graphed here without anybody remembering to - and since 0.7.37 its fair value beside it, what the reg... |
| 470 | 11 | `static Trace[] withTheSectors(Trace[] fixed)` | ...and each business's month (0.7.37): its net income after tax and its posts filled, which the history has kept for every sector since 0.7.4 for the Sectors cards' sparklines and the picker never offered (City Histor... |
| 490 | 10 | `static String areaOf(Trace t)` | Which of the four areas a line belongs to, for its colour (0.7.23): money and policy blue, people and services teal, business and trade violet, building and land pink - the picker's groups mapped onto the rail's areas... |
| 502 | 8 | `static String groupArea(String group)` | A picker group's area: what its heading is coloured in. |
| 512 | 18 | `static String groupIcon(String group)` | A picker group's icon, beside its heading (0.7.37): the rail's or Build's for what the group is about. |
| 532 | 5 | `String[] pickedColours()` | Each picked line's colour, in the order picked: its area's, then the others (Palette.lineColours()). |
| 539 | 9 | `String colourOf(String key)` | One picked line's colour, or null if it is not picked. |

### THE RECORD (lines 549-644)

| line | len | member | says |
|---:|---:|---|---|
| 585 | 1 | **type** `record Preset(String name, String blurb, String[] keys)` | A curated set of lines that belong on one chart. |
| 639 | 5 | `static String[] marketKeys()` | Every company's share price, for the market preset. |

### THE PAGE, REDRAWN (0.7.5) (lines 645-681)

### THE PAGE AT ITS WIDTH (0.7.37) (lines 682-1016)

| line | len | member | says |
|---:|---:|---|---|
| 718 | 72 | `void showHistoryMenu()` |  |
| 796 | 90 | `void drawPage(boolean fresh)` | The page itself, put in the window through setPage(): what keepMenu runs as a kept draw from the pool, and what KeptBench runs kept and built new for KeptCheck. |
| 928 | 38 | `private void frameOver(VBox frame, VBox body)` | The fixed frame over a scrolling page, at the page's width; the page as tall as what is left under the frame as laid out (BankScreen's), its position kept from the bottom (THE PAGE, REDRAWN), and its width the scrolle... |
| 972 | 3 | `static double given(double viewport)` | The page's width for a viewport this wide: all of it, from 320 up to PAGE_WIDE. |
| 981 | 10 | `private void follow(double width)` | The page laid out at a new width (0.7.37, D1): the big chart, the two pins and the preset row follow it, without a redraw. |
| 993 | 11 | `private void scrollTo(javafx.scene.Node target)` | Scrolls the page so `target` is at its top, once laid out (InfrastructureScreen's and Trade's way). |
| 1006 | 5 | `void moveChartTo(double from, double to)` | The chart moved to a stretch of months - an episode's, a decision's - and the page scrolled to it; nothing else changes (D6). |
| 1013 | 3 | `void moveChartTo(YearBook.Episode e)` | ...an episode, with a year either side. |

### the head (lines 1017-1165)

| line | len | member | says |
|---:|---:|---|---|
| 1041 | 10 | `HBox head()` | The head: "City History" in the money blue with its (i), and at its right the page's one action, 0.7.34's button (D10) - it sat at the foot of the page under every good. |
| 1068 | 30 | `void writeTheBooks()` | Six files since 0.7.16 - each book's text and its two tables as CSV - all in one folder, so the folder is named once and each book is a line of file names; anything that failed says so on a line of its own. |
| 1104 | 61 | `HBox bookCard()` | The book's result as a card under the head (0.7.37): where it went, a line of file names a book in figures, what could not be written in the red, and a × that puts it away. |

### the strip (lines 1166-1342)

| line | len | member | says |
|---:|---:|---|---|
| 1169 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where, boolean isNew)` | One cell of the strip, as words, so a probe reads them without drawing: its label, figure, note and tone, where its door goes (null: none), and whether it is NEW this month. |
| 1179 | 46 | `List<Cell> stripCells(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` | The strip's four (0.7.37, D3): THE CITY, its age and its record; HARD TIMES, every named episode since founding and how many touch the chart's window; RUNNING NOW, the trouble the history has not closed, the worst fir... |
| 1227 | 27 | `HBox strip(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` | The strip drawn: four limit cells, three of them doors - to the hard times, to what is running on the chart, to the decisions. |
| 1261 | 3 | `static String episodeTone(String kind)` | An episode's verdict colour, red for a crisis and amber for a watch: TimeChart.episodeColour()'s rule (YearBook.isSevere()), here so the strip's words are composed without the chart's class, which a probe without a to... |
| 1266 | 5 | `static String cityAge(int months)` | A city's age in the words a person uses: months under a year, years after. |
| 1273 | 3 | `static String months(int n)` | "1 month", "6 months", "2,213 months". |
| 1278 | 3 | `static boolean touches(YearBook.Episode e, int from, int to)` | Whether an episode touches the months from..to. |
| 1283 | 3 | `static String episodeKey(YearBook.Episode e)` | An episode's name for "new": its kind and its first month, which do not change as it runs. |
| 1293 | 6 | `void noteEpisodes(List<YearBook.Episode> episodes, int month)` | Notes the episodes this draw lists (0.7.37): one not listed before is NEW from the month it is first seen in. |
| 1301 | 4 | `boolean isNew(YearBook.Episode e)` | Whether an episode was first listed this month. |
| 1311 | 9 | `int[] shownIndices(HistorySave h)` | The window's first and last months as indices into the history's axis, {from, to} - what the pins, the readings and the log scale's check fold over (0.7.23; they read historyWindow's last N months before). |
| 1322 | 3 | `Label pickChip(String text, boolean on, Runnable act)` | A chip that is on or off, which is most of the controls on this screen. |
| 1330 | 12 | `Label pickChip(String text, javafx.scene.Node graphic, boolean on, Runnable act)` | ...with a graphic: set where the label is made, so a kept chip handed its own icon again is not stripped of it and given it back (that takes the icon off the scene and puts it on). |

### a chip on this page, and the thing pressed held still (0.7.5) (lines 1343-1452)

| line | len | member | says |
|---:|---:|---|---|
| 1363 | 3 | `Label chip(String name, String text, boolean on, Runnable act)` | A chip on this page: pickChip, remembered by name, and held where it was on the screen when pressed. |
| 1368 | 10 | `Label chip(String name, String text, javafx.scene.Node graphic, boolean on, Runnable act)` | ...with a graphic (the picker's group heads' icons). |
| 1380 | 3 | `Label stillChip(String text, boolean on, String why)` | A chip that only says something: lit or not, no act, and why on hover. |
| 1385 | 6 | `Label stillChip(String text, javafx.scene.Node graphic, boolean on, String why)` | ...with a graphic. |
| 1393 | 3 | `static void tip(javafx.scene.Node node, String text)` | A tooltip on a node: on a kept node (inside a draw) the one it has, its words set; else a new one (Kept.tip). |
| 1398 | 4 | `private static void stopCounting(javafx.scene.Node figure)` | A figure on a kept label that is not counting on this draw has no count-up left running from the last. |
| 1417 | 18 | `private void holdInPlace(javafx.scene.Node node, double wasAt)` | Puts `node` back `wasAt` scene pixels down the window, once the page has been laid out. |
| 1444 | 8 | `List<String> pickedUnits()` | The units of the picked lines, each once, in the order they were picked. |

### THE PINS (0.7.5) (lines 1453-1692)

| line | len | member | says |
|---:|---:|---|---|
| 1475 | 9 | `String pinned(boolean left)` | The line on a small chart. |
| 1486 | 4 | `static boolean known(String key)` | Whether this page draws a line by that name. |
| 1505 | 13 | `void openOn(String...keys)` | City History opened on these lines - THE ONE DOOR IN (0.7.37, D11): a header tile's click (UserInterface.openHistory(), which marks it an arrival first), the Infrastructure tab's "The road over the years ›", the Gover... |
| 1524 | 7 | `void pin(String key)` | Pins a line. |
| 1533 | 7 | `String unpinned(boolean left)` | What unpinning a side would put back: its default, or the other default if that one is showing beside it. |
| 1541 | 6 | `void unpin(boolean left)` |  |
| 1552 | 13 | `GridPane pins(HistorySave h, List<YearBook.Band> bands, boolean fresh)` | The two pins as cards side by side (0.7.37; two bare charts at 374 wide until then), each half the page, on the big chart's window. |
| 1567 | 3 | `double pinChartWidth()` | A pin's chart: its card's half of the page, less the card's padding and edge - in whole pixels, less SLACK (0.7.40). |
| 1572 | 3 | `double bigChartWidth()` | The big chart's canvas: the page, less its padding and edge (TimeChart's 30) and SLACK, in whole pixels (0.7.40). |
| 1584 | 3 | `TimeChart[] charts()` | The charts the page holds, for KeptBench: the big chart, then the two pins' (null where not made yet). |
| 1589 | 1 | **type** `record PinHead(String key, String label, String figure, String move, String moveTip, double last, boolean r...` | One pin's head, as words (0.7.37): its line, its figure in the window's last month, and how far it moved over the window. |
| 1591 | 10 | `PinHead pinHead(HistorySave h, boolean left)` |  |
| 1609 | 83 | `VBox pinCard(HistorySave h, boolean left, List<YearBook.Band> bands, boolean fresh)` | One pin as a card (0.7.37): its head - the line's name, a click that draws it alone on the big chart (openOn(), which never pins), its figure, its move over the window in neutral ink, "unpin" and "layers" - and the li... |

### GDP IN LAYERS (0.7.6) (lines 1693-1793)

| line | len | member | says |
|---:|---:|---|---|
| 1735 | 3 | `boolean bigInLayers()` | Whether the big chart draws its one line in layers: real GDP picked alone, with the toggle on. |
| 1740 | 11 | `Label layersChip(String where)` | The chip that toggles the layers, on the small chart's head and on the big chart's reading. |
| 1758 | 6 | `TimeChart.Stack gdpStack(HistorySave h, String unit)` | GDP's four parts, a rolling year in founding money each - C, I, G, then NX - as a chart stacks them (0.7.23): C, I and G drawn from zero in the three steps of the ramp, net exports read on the crosshair, in the muted ... |
| 1770 | 23 | `VBox layersKey(double[][] layers, double wide)` | The key under a layered chart - a swatch per layer and one for the line - and the sentence that says how to read the gap. |

### THE BIG CHART (0.7.5) (lines 1794-1890)

| line | len | member | says |
|---:|---:|---|---|
| 1808 | 53 | `HBox chartControls(HistorySave h)` | The row above the big chart: the presets on the left, "clear all" and "log" on the right. |
| 1872 | 18 | `String logRefusal(HistorySave h)` | Why the log scale cannot draw what is picked, or null when it can. |

### THE CHART, REBUILT (0.7.23) (lines 1891-2000)

| line | len | member | says |
|---:|---:|---|---|
| 1927 | 53 | `TimeChart bigChart(HistorySave h, List<YearBook.Band> bands, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` | The big chart with this redraw's lines, bands, episodes and flags - the flags as its lane draws them (ChartModel.onAxis(), 0.7.37) - at the page's width. |
| 1988 | 7 | `boolean drawsAnIndex(List<String> keys)` | Whether the picked lines include a price index - the level, what people expect it to be, or a part of the basket - whose line answers to the basket's links: the big chart marks them only then (0.7.45; the UI spec's D12). |
| 1997 | 3 | `static TimeChart.Axis axisFor(String unit)` | One value axis: its gridlines in the unit's own words (axisTick()), and from zero when a per cent. |

### FULL SCREEN (0.7.23) (lines 2001-2225)

| line | len | member | says |
|---:|---:|---|---|
| 2026 | 28 | `void setChartFull(boolean on, boolean redraw)` | Gives the big chart the whole window, or takes it back - and redraws the page, unless another screen is about to be drawn instead (the main menu, a rail button: UserInterface.leaveChartFullScreen()). |
| 2059 | 3 | `void leaving()` | The page is going: another screen is being drawn (UserInterface.clearMenu()). |
| 2072 | 6 | `private void fitFull()` | The chart sized to the pane: the whole width, and the plot whatever height the rest leaves - read off the window (0.7.40), which the pane is laid over and nothing in it can move, not off the pane, which a chart a pixe... |
| 2083 | 6 | `static VBox newFullPane()` | The pane the chart is laid in over the whole window: the stage's colour, padded by FULL_PAD_*. |
| 2091 | 5 | `static Label newFullTitle()` | The page's title in the full-screen toolbar. |
| 2098 | 5 | `static Label newFullClock()` | The clock's line in the full-screen toolbar, empty until the screen says the date. |
| 2105 | 6 | `static void fitToWindow(TimeChart chart, double windowW, double windowH)` | The chart sized to a window of this size: the window less the pane's padding, the canvas SLACK short of that (nothing when the window has no room). |
| 2113 | 4 | `private void refreshFullScreen()` | A redraw in full screen: the clock's line, and the chart's size if the window moved. |
| 2130 | 23 | `static String axisTick(String unit, double v, double step)` | One gridline's label: short enough to fit, honest about its unit. |
| 2163 | 5 | `static String priceTick(double a, double step)` | A price on an axis: cents only when the gridlines are finer than a dollar. |
| 2170 | 6 | `static String shortCash(double a)` | Dollars, abbreviated from a thousand up - an axis has no room for digits. |
| 2178 | 5 | `static String shortCount(double a)` | People, homes, jobs - the same abbreviation without the dollar. |
| 2185 | 4 | `static String trim(double a)` | One decimal at most, and none at all when it would read ".0". |
| 2191 | 16 | `static String unitName(String unit, Currency money)` | What the y-axis is measured in, when every line agrees - the rate in this city's own money (0.7.10). |
| 2217 | 8 | `static double plotScale(String unit, double v)` | The stored value, in the units the axis is labelled in. |

### WHAT EACH LINE DID (0.7.37) (lines 2226-2810)

| line | len | member | says |
|---:|---:|---|---|
| 2240 | 4 | **type** `record Span(double first, double last, double lo, double hi, int loAt, int hiAt)` | A line over the window: its first and last recorded values, its low and high and the indices of each; NaN where nothing is recorded. |
| 2242 | 1 | `boolean flat()` _(in HistoryScreen.Span)_ | Whether it never moved over the window. |
| 2246 | 12 | `static Span span(double[] all, int[] shown)` | One series over the window's indices {from, to} (shownIndices()). |
| 2260 | 2 | **type** `record Reading(String key, String label, String latest, String move, String caption, String rangeTip, doubl...` | One reading card, as words and figures (0.7.37): what the probe reads. |
| 2263 | 24 | `Reading reading(HistorySave h, String key, String axis)` |  |
| 2289 | 3 | `String shown(String unit, double v)` | A figure as this page's cards and pins write it: fmtUnit()'s, with a true minus (0.7.37) - the chart's own card keeps fmtUnit()'s. |
| 2299 | 7 | `String moveWords(String unit, double first, double last)` | How far a line moved, as this page writes it (0.7.37, D5): an up or a down arrow and changeText()'s figure without its sign - points for a rate, per cent for a quantity - in neutral ink; a move too small to show in th... |
| 2308 | 29 | `VBox readings(HistorySave h, boolean fresh)` | The section: its head, the cards three across, and the line that says when two lines share a colour. |
| 2345 | 78 | `VBox readingCard(HistorySave h, String key, String colour, String axis, boolean fresh)` | One line's card: its swatch, name and axis, "pin" or "pinned" (and "layers" for real GDP alone); its figure and its move; its range bar; the caption. |
| 2435 | 22 | `String changeText(String unit, double first, double last)` | How far it moved, in terms the unit deserves. |
| 2490 | 49 | `VBox historyPickerRows()` | The chips, one heading per group - each group closed until it is wanted, and a box over them that finds a line by name (0.7.5). |
| 2544 | 29 | `void fillPicker(VBox groups)` | The groups, as the filter and the player have left them. |
| 2575 | 45 | `private void pickerGroup(VBox groups, String group, int size, int picked, List<Trace> matching, boolean finding)` | One group of the picker: its heading and how many of its lines are picked, and - open - its chips. |
| 2626 | 4 | `Trace traceFor(String key)` |  |
| 2647 | 117 | `double[] historyValues(HistorySave h, String key)` | A series, aligned to the month axis, derived ones included. |
| 2765 | 5 | `double[] minus(double[] a, double[] b)` |  |
| 2772 | 38 | `String fmtUnit(String unit, double v)` | A value in the units it is actually kept in. |

### HARD TIMES AND YOUR DECISIONS (0.7.37) (lines 2811-3171)

| line | len | member | says |
|---:|---:|---|---|
| 2842 | 8 | `static String hardTimesInfo()` | The section's (i): the rules that name an episode, the year book's own. |
| 2857 | 15 | `List<YearBook.Episode> hardTimesInView(List<YearBook.Episode> episodes, int lastMonth)` | The hard times touching the window, as this page lists them (pure): those still running first, in RUNNING NOW's order, but a chronic one last of all; then those the history closed, the most recently ended first. |
| 2877 | 3 | `static boolean isDecision(DecisionLog.Entry e)` | Whether a flag's entry is a decision of the player's: the flags carry the city's discoveries beside its decisions (R5; ResearchView.FLAG_KIND), and a discovery is not a decision, so every list and count of "your decis... |
| 2882 | 5 | `static int decisionsIn(ChartModel.Flag f)` | How many decisions a flag holds (its discoveries left out: isDecision()). |
| 2893 | 9 | `static List<DecisionLog.Entry> decisionsInView(List<ChartModel.Flag> flags, int from, int to)` | The decisions inside the window, newest first (pure): a founding-month one counts at the history's first month, where its flag stands. |
| 2904 | 4 | `static String spanWords(YearBook.Episode e, int lastMonth)` | "Jun 2192 – May 2197 · 60 months", or "Aug 2199 – now · 6 months" while it runs. |
| 2910 | 3 | `static String worstLine(YearBook.Episode e)` | "real output 13.78% below the year before at Feb 2193": the episode's worst, the year book's words. |
| 2915 | 5 | `String viewWords()` | What the window spans, for an empty list: "these 10 years", "these 18 months", or "the whole history". |
| 2921 | 73 | `VBox hardTimes(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` |  |
| 2996 | 3 | `static String decisionKey(DecisionLog.Entry d)` | A decision's name for its scope: its month, kind and words, which do not change as the log grows (an Entry has no equality of its own). |
| 3001 | 10 | `HBox columnHead(String words, int count)` | A column's head: its words, and how many. |
| 3013 | 6 | `static Label quiet(String text)` | A quiet line: an empty list's words. |
| 3021 | 6 | `Label showAll()` | "show All ›": the whole history in view. |
| 3029 | 10 | `void rowGoes(javafx.scene.layout.Pane row, String tipText, Runnable go)` | A row that moves the chart: the pointer's hand, a ground under it, and the click. |
| 3045 | 26 | `HBox episodeRow(YearBook.Episode e, int lastMonth)` | One hard time: its kind's icon and tag in its colour (the chart's verdict colours, 0.7.23), its name, its span; under it its worst, in the year book's words. |
| 3073 | 17 | `HBox decisionRow(DecisionLog.Entry d)` | One decision: its icon in its flag's area colour, its month, its words - wrapping, never cut; "this month" when it is. |
| 3096 | 13 | `VBox fold(String key, String caption, java.util.function.Supplier<javafx.scene.Node> body)` | A fold on this page (Pieces.details()), its toggle held under the pointer as it opens or closes, a chip's way (D7, D8): the page under it changes length and the scroll memory keeps the bottom. |
| 3111 | 1 | **type** `record KindRow(String kind, String name, int count, int months, List<YearBook.Episode> episodes)` | One kind of episode in the details, as words (pure): its kind, how many, how long all told, and its episodes. |
| 3113 | 10 | `static List<KindRow> kindRows(List<YearBook.Episode> episodes)` |  |
| 3125 | 3 | `static String howLong(int months)` | "42 years" all told, or "9 months" under two years. |
| 3137 | 34 | `javafx.scene.Node byKind(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` | Every named episode since founding, by kind (0.7.37): a row a kind that has happened, its episodes on one scale of the history's months in their colour - each at least two pixels, its name, span and worst on its toolt... |

### PRICES THIS MONTH (0.7.37; EVERY GOOD, ON ONE PAGE before) (lines 3172-3374)

| line | len | member | says |
|---:|---:|---|---|
| 3207 | 3 | `GoodsMarket market(Good g)` | A good's market, or null. |
| 3212 | 4 | `Sector seller(Good g)` | The business that sells a seller-priced good: the sector that makes it, or null. |
| 3218 | 5 | `static String goodPrice(double thousands)` | A good's price, to the cent: "$296.67", "$11,454.16" - tiny ones to the place that shows them. |
| 3225 | 8 | `static String flowWords(GoodsMarket m)` | "imported 54,201", "exported 87,294", both, or null when nothing crossed; a fraction of a unit is "under 1" (a wagon set's worth read "imported 0"). |
| 3235 | 3 | `static String units(double v)` | A count of units that crossed: whole ones grouped, or "under 1". |
| 3240 | 3 | `static String some(int n)` | "1 at", "none at": a count in the summary's words. |
| 3250 | 17 | `String priceSummary()` | The line over the fold (pure): how many goods trade with the world and where in their bands the both-ways ones stand - at what the world charges, between, at what it pays - how many are open at one end, and how many t... |
| 3269 | 11 | `VBox prices()` | The section: its head with the (i), the line of counts, and every good behind "details". |
| 3282 | 1 | **type** `record GoodLine(Good good, String figure, String says, double at, String flow)` | One good as a row of the fold, as words (pure): what the probe reads. |
| 3285 | 30 | `List<GoodLine> goodLines()` | Every good, as the fold draws it: the both-ways goods with their place in the band, the rest with their words. |
| 3325 | 49 | `javafx.scene.Node goodsRows()` | The fold: the goods traded both ways on their bands, the world's floor and ceiling a rule through every row; then those open at one end and those their seller prices, in words. |

