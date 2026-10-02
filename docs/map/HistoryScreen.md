# HistoryScreen.java - 3,014 lines · 106 methods · 22 constants · interface

`ham/citybuildersim/ui/HistoryScreen.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [Palette](Palette.md) (153), [YearBook](YearBook.md) (54), [HistorySave](HistorySave.md) (33), [Icons](Icons.md) (23), [TimeChart](TimeChart.md) (23), [ChartModel](ChartModel.md) (19), [CityCalendar](CityCalendar.md) (14), [GamePrefs](GamePrefs.md) (9), [Good](Good.md) (9), [DecisionLog](DecisionLog.md) (7), [Crime](Crime.md) (6), [FamilyStructure](FamilyStructure.md) (6), [GoodsMarket](GoodsMarket.md) (6), [Equity](Equity.md) (5), [Sector](Sector.md) (5), [UserInterface](UserInterface.md) (3), [Sectors](Sectors.md) (3), [Pieces](Pieces.md) (3), [SectorScreen](SectorScreen.md) (3), [GameFiles](GameFiles.md) (2), [Currency](Currency.md) (2)

**Used by (8):** [BankScreen](BankScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [PolicyScreen](PolicyScreen.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 67 | THE HISTORY SCREEN |
| 499 | THE RECORD |
| 586 | THE PAGE, REDRAWN (0.7.5) |
| 623 | THE PAGE AT ITS WIDTH (0.7.37) |
| 679 | · SEEDED ONCE, AND THEN LEFT ALONE. |
| 869 | · the head |
| 1004 | · the strip |
| 1170 | · a chip on this page, and the thing pressed held still (0.7.5) |
| 1265 | THE PINS (0.7.5) |
| 1486 | GDP IN LAYERS (0.7.6) |
| 1587 | THE BIG CHART (0.7.5) |
| 1684 | THE CHART, REBUILT (0.7.23) |
| 1775 | FULL SCREEN (0.7.23) |
| 1960 | WHAT EACH LINE DID (0.7.37) |
| 2506 | HARD TIMES AND YOUR DECISIONS (0.7.37) |
| 2820 | PRICES THIS MONTH (0.7.37; EVERY GOOD, ON ONE PAGE before) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 173 | `HistoryScreen.TO_CHART` | `"chart", TO_HARD_TIMES = "hard times"` | The two places a draw can be asked to scroll to: the row above the big chart, and the hard times' heading. |
| 199 | `HistoryScreen.TRACES` | `withTheCrime(withTheHouseholds(withTheSectors(withTheMarket(new Trace[] { new...` |  |
| 537 | `HistoryScreen.PRESETS` | `{ new Preset("What money costs", "the borrowing rate, the price level, and ho...` |  |
| 798 | `HistoryScreen.FRAME_CHROME` | `160` | The frame's height before it is laid out, for the scroller's first guess at what is left of the stage. |
| 801 | `HistoryScreen.STAGE_REST` | `36` | What the stage keeps under the scroller once the frame is laid out - BankScreen's and Trade's. |
| 872 | `HistoryScreen.LEAD_INFO` | `"Every month the city has lived, and what it did.The big chart draws the line...` | The title's (i): the old lead, and how the big chart is read. |
| 878 | `HistoryScreen.YEAR_BOOK_INFO` | `"Writes the whole run out as plain text - every series the history " + "keeps...` | What "Write the year book" writes (the old SEND THIS RUN TO SOMEBODY paragraph). |
| 1280 | `HistoryScreen.SMALL_CHART` | `120` | How tall a pinned chart's plot is: 150 until 0.7.37, 120 since, so the big plot ends above the fold at 1,389 x 868 (D2). |
| 1519 | `HistoryScreen.LAYERED` | `"realGdp"` | The one line this page can draw in layers. |
| 1522 | `HistoryScreen.LAYERED_LINE` | `Palette.TEXT_HEAD` | What the GDP line is drawn in over the layers: the headings' ink, which no step of the blue ramp is near. |
| 1525 | `HistoryScreen.LAYER_NAMES` | `{ "consumption", "investment", "government", "net exports" }` | What each part is called on the key and in the crosshair, in YearBook.GDP_PARTS' order. |
| 1592 | `HistoryScreen.BIG_CHART` | `380` | How tall the big chart's plot is on the page; the lanes and the overview are under it. |
| 1595 | `HistoryScreen.CONTROLS` | `130` | Room kept at the right of the preset row for "clear all" and "log". |
| 2145 | `HistoryScreen.MOVE_MILLIS` | `600` | How long a figure takes to slide to a month's new reading, in milliseconds - the range bar's dot; the figure counts on over SectorScreen's own time. |
| 2324 | `HistoryScreen.AXES_INFO` | `"Lines measured in the same thing are drawn against each other on a real axis...` | PICK WHAT TO DRAW's (i): how the lines share axes. |
| 2524 | `HistoryScreen.HARD_TIMES_SHOWN` | `5` | At most this many hard times are listed in view; the rest are counted, and in the details. |
| 2527 | `HistoryScreen.DECISIONS_SHOWN` | `8` | At most this many decisions are listed in view. |
| 2530 | `HistoryScreen.KINDS` | `{ "recession", "depression", "slump", "epidemic", "financial", "currency", "i...` | The kinds of episode, in the order the details list them. |
| 2533 | `HistoryScreen.KIND_NAMES` | `{ "Recessions", "Depressions", "Slumps", "Epidemics", "Financial crises", "Cu...` | ...and what the details call each, in that order. |
| 2843 | `HistoryScreen.AT_END` | `1e-6` | How near an end of its band a price must be to be AT it - a rounding hair of the band (the market strikes an end exactly). |
| 2846 | `HistoryScreen.BAND_ROOM` | `1.25` | The scale a good's row is drawn on: the band from 0 to 1, and room past its ceiling for the month's trade. |
| 2849 | `HistoryScreen.GOODS_INFO` | `"What a unit costs here this month, against what the world pays for one and "...` | The section's (i). |

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
| 167 | `private final java.util.Map<String, Integer> episodeSeen` | The month each named episode was first listed, by kind and first month (0.7.37): an episode the list did not have at the previous draw is NEW for the month it appears. |
| 168 | `private int episodesSeenAt` |  |
| 171 | `private String scrollTarget` | Where the next draw scrolls the page to - the chart's top, or the hard times - once it is laid out; null: the scroll memory's. |
| 176 | `private javafx.scene.Node chartTop, hardTimesTop` | This draw's nodes a scroll can land on: the row above the big chart and the hard times' heading. |
| 184 | `private double pageWidth` | The page's inside width, last laid out (0.7.37; GRAPH's 760 until then): the big chart, the pins and the rows that wrap are drawn at it, and the canvases follow it when the window is resized. |
| 187 | `private javafx.scene.layout.FlowPane presetFlow` | The preset row's flow, so a resize can rewrap it. |
| 911 | `String bookExportSaid` | What the last export did, kept so it survives the redraw - the whole of it in a few lines (bookExportSaid), and its parts for the card. |
| 912 | `private String bookFolder` |  |
| 913 | `private final List<String> bookFiles` |  |
| 1173 | `private javafx.scene.control.ScrollPane page` | The page's scroller, kept so a click can be put back where it was after the rebuild. |
| 1176 | `private String pressed` | The chip last pressed, by name, and how far down the window it was; the next rebuild spends it. |
| 1177 | `private double pressedAt` |  |
| 1180 | `private final java.util.Map<String, javafx.scene.Node> named` | This build's chips by name, so the rebuild can find the one that was pressed. |
| 1183 | `private TextField filterField` | The picker's filter box, so a redraw can hand the focus back to it; see showHistoryMenu. |
| 1386 | `private final TimeChart[] smallCharts` | The two small charts' nodes, kept from one redraw to the next, as the big chart is: each follows the big chart's window as it is dragged, which a chart rebuilt every month could not (0.7.23). |
| 1714 | `private TimeChart bigChart` | The big chart, made once; see the banner. |
| 1717 | `private boolean chartFull` | Whether the big chart has the whole window (0.7.23); see FULL SCREEN. |
| 1792 | `private VBox fullPane` | The pane the chart is laid in over the whole window, and its clock line. |
| 1793 | `private Label fullClock` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 2955 | **type** `final class HistoryScreen` | City History: the city as a shape over time. |
| 65 | 1 | `HistoryScreen(UserInterface ui)` |  |

### THE HISTORY SCREEN (lines 67-498)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 1 | **type** `record Trace(String key, String label, String group, String unit)` | One plottable line: where it comes from and how to read it. |
| 372 | 9 | `static Trace[] withTheCrime(Trace[] fixed)` | One series per reason for crime, added to CRIME. |
| 383 | 9 | `static Trace[] withTheHouseholds(Trace[] fixed)` | One series per household shape, appended after the sectors. |
| 401 | 11 | `static Trace[] withTheMarket(Trace[] fixed)` | ...and a share price per company, one trace each, generated off the register's own list so a company added there is graphed here without anybody remembering to - and since 0.7.37 its fair value beside it, what the reg... |
| 420 | 11 | `static Trace[] withTheSectors(Trace[] fixed)` | ...and each business's month (0.7.37): its net income after tax and its posts filled, which the history has kept for every sector since 0.7.4 for the Sectors cards' sparklines and the picker never offered (City Histor... |
| 440 | 10 | `static String areaOf(Trace t)` | Which of the four areas a line belongs to, for its colour (0.7.23): money and policy blue, people and services teal, business and trade violet, building and land pink - the picker's groups mapped onto the rail's areas... |
| 452 | 8 | `static String groupArea(String group)` | A picker group's area: what its heading is coloured in. |
| 462 | 18 | `static String groupIcon(String group)` | A picker group's icon, beside its heading (0.7.37): the rail's or Build's for what the group is about. |
| 482 | 5 | `String[] pickedColours()` | Each picked line's colour, in the order picked: its area's, then the others (Palette.lineColours()). |
| 489 | 9 | `String colourOf(String key)` | One picked line's colour, or null if it is not picked. |

### THE RECORD (lines 499-585)

| line | len | member | says |
|---:|---:|---|---|
| 535 | 1 | **type** `record Preset(String name, String blurb, String[] keys)` | A curated set of lines that belong on one chart. |
| 580 | 5 | `static String[] marketKeys()` | Every company's share price, for the market preset. |

### THE PAGE, REDRAWN (0.7.5) (lines 586-622)

### THE PAGE AT ITS WIDTH (0.7.37) (lines 623-868)

| line | len | member | says |
|---:|---:|---|---|
| 658 | 138 | `void showHistoryMenu()` |  |
| 809 | 18 | `private void frameOver(VBox frame, VBox body)` | The fixed frame over a scrolling page, at the page's width; the page as tall as what is left under the frame as laid out (BankScreen's), its position kept from the bottom (THE PAGE, REDRAWN), and its width the scrolle... |
| 833 | 10 | `private void follow(double width)` | The page laid out at a new width (0.7.37, D1): the big chart, the two pins and the preset row follow it, without a redraw. |
| 845 | 11 | `private void scrollTo(javafx.scene.Node target)` | Scrolls the page so `target` is at its top, once laid out (InfrastructureScreen's and Trade's way). |
| 858 | 5 | `void moveChartTo(double from, double to)` | The chart moved to a stretch of months - an episode's, a decision's - and the page scrolled to it; nothing else changes (D6). |
| 865 | 3 | `void moveChartTo(YearBook.Episode e)` | ...an episode, with a year either side. |

### the head (lines 869-1003)

| line | len | member | says |
|---:|---:|---|---|
| 893 | 10 | `HBox head()` | The head: "City History" in the money blue with its (i), and at its right the page's one action, 0.7.34's button (D10) - it sat at the foot of the page under every good. |
| 920 | 30 | `void writeTheBooks()` | Six files since 0.7.16 - each book's text and its two tables as CSV - all in one folder, so the folder is named once and each book is a line of file names; anything that failed says so on a line of its own. |
| 956 | 47 | `HBox bookCard()` | The book's result as a card under the head (0.7.37): where it went, a line of file names a book in figures, what could not be written in the red, and a × that puts it away. |

### the strip (lines 1004-1169)

| line | len | member | says |
|---:|---:|---|---|
| 1007 | 1 | **type** `record Cell(String label, String value, String note, String tone, String where, boolean isNew)` | One cell of the strip, as words, so a probe reads them without drawing: its label, figure, note and tone, where its door goes (null: none), and whether it is NEW this month. |
| 1017 | 46 | `List<Cell> stripCells(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` | The strip's four (0.7.37, D3): THE CITY, its age and its record; HARD TIMES, every named episode since founding and how many touch the chart's window; RUNNING NOW, the trouble the history has not closed, the worst fir... |
| 1065 | 24 | `HBox strip(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` | The strip drawn: four limit cells, three of them doors - to the hard times, to what is running on the chart, to the decisions. |
| 1096 | 3 | `static String episodeTone(String kind)` | An episode's verdict colour, red for a crisis and amber for a watch: TimeChart.episodeColour()'s rule (YearBook.isSevere()), here so the strip's words are composed without the chart's class, which a probe without a to... |
| 1101 | 5 | `static String cityAge(int months)` | A city's age in the words a person uses: months under a year, years after. |
| 1108 | 3 | `static String months(int n)` | "1 month", "6 months", "2,213 months". |
| 1113 | 3 | `static boolean touches(YearBook.Episode e, int from, int to)` | Whether an episode touches the months from..to. |
| 1118 | 3 | `static String episodeKey(YearBook.Episode e)` | An episode's name for "new": its kind and its first month, which do not change as it runs. |
| 1128 | 6 | `void noteEpisodes(List<YearBook.Episode> episodes, int month)` | Notes the episodes this draw lists (0.7.37): one not listed before is NEW from the month it is first seen in. |
| 1136 | 4 | `boolean isNew(YearBook.Episode e)` | Whether an episode was first listed this month. |
| 1146 | 9 | `int[] shownIndices(HistorySave h)` | The window's first and last months as indices into the history's axis, {from, to} - what the pins, the readings and the log scale's check fold over (0.7.23; they read historyWindow's last N months before). |
| 1157 | 12 | `Label pickChip(String text, boolean on, Runnable act)` | A chip that is on or off, which is most of the controls on this screen. |

### a chip on this page, and the thing pressed held still (0.7.5) (lines 1170-1264)

| line | len | member | says |
|---:|---:|---|---|
| 1190 | 10 | `Label chip(String name, String text, boolean on, Runnable act)` | A chip on this page: pickChip, remembered by name, and held where it was on the screen when pressed. |
| 1202 | 6 | `Label stillChip(String text, boolean on, String why)` | A chip that only says something: lit or not, no act, and why on hover. |
| 1209 | 5 | `static void tip(javafx.scene.Node node, String text)` |  |
| 1229 | 18 | `private void holdInPlace(javafx.scene.Node node, double wasAt)` | Puts `node` back `wasAt` scene pixels down the window, once the page has been laid out. |
| 1256 | 8 | `List<String> pickedUnits()` | The units of the picked lines, each once, in the order they were picked. |

### THE PINS (0.7.5) (lines 1265-1485)

| line | len | member | says |
|---:|---:|---|---|
| 1287 | 9 | `String pinned(boolean left)` | The line on a small chart. |
| 1298 | 4 | `static boolean known(String key)` | Whether this page draws a line by that name. |
| 1317 | 13 | `void openOn(String...keys)` | City History opened on these lines - THE ONE DOOR IN (0.7.37, D11): a header tile's click (UserInterface.openHistory(), which marks it an arrival first), the Infrastructure tab's "The road over the years ›", the Gover... |
| 1336 | 7 | `void pin(String key)` | Pins a line. |
| 1345 | 7 | `String unpinned(boolean left)` | What unpinning a side would put back: its default, or the other default if that one is showing beside it. |
| 1353 | 6 | `void unpin(boolean left)` |  |
| 1364 | 11 | `GridPane pins(HistorySave h, List<YearBook.Band> bands, boolean fresh)` | The two pins as cards side by side (0.7.37; two bare charts at 374 wide until then), each half the page, on the big chart's window. |
| 1377 | 3 | `double pinChartWidth()` | A pin's chart: its card's half of the page, less the card's padding and edge. |
| 1389 | 1 | **type** `record PinHead(String key, String label, String figure, String move, String moveTip, double last, boolean r...` | One pin's head, as words (0.7.37): its line, its figure in the window's last month, and how far it moved over the window. |
| 1391 | 10 | `PinHead pinHead(HistorySave h, boolean left)` |  |
| 1409 | 76 | `VBox pinCard(HistorySave h, boolean left, List<YearBook.Band> bands, boolean fresh)` | One pin as a card (0.7.37): its head - the line's name, a click that draws it alone on the big chart (openOn(), which never pins), its figure, its move over the window in neutral ink, "unpin" and "layers" - and the li... |

### GDP IN LAYERS (0.7.6) (lines 1486-1586)

| line | len | member | says |
|---:|---:|---|---|
| 1528 | 3 | `boolean bigInLayers()` | Whether the big chart draws its one line in layers: real GDP picked alone, with the toggle on. |
| 1533 | 11 | `Label layersChip(String where)` | The chip that toggles the layers, on the small chart's head and on the big chart's reading. |
| 1551 | 6 | `TimeChart.Stack gdpStack(HistorySave h, String unit)` | GDP's four parts, a rolling year in founding money each - C, I, G, then NX - as a chart stacks them (0.7.23): C, I and G drawn from zero in the three steps of the ramp, net exports read on the crosshair, in the muted ... |
| 1563 | 23 | `VBox layersKey(double[][] layers, double wide)` | The key under a layered chart - a swatch per layer and one for the line - and the sentence that says how to read the gap. |

### THE BIG CHART (0.7.5) (lines 1587-1683)

| line | len | member | says |
|---:|---:|---|---|
| 1601 | 53 | `HBox chartControls(HistorySave h)` | The row above the big chart: the presets on the left, "clear all" and "log" on the right. |
| 1665 | 18 | `String logRefusal(HistorySave h)` | Why the log scale cannot draw what is picked, or null when it can. |

### THE CHART, REBUILT (0.7.23) (lines 1684-1774)

| line | len | member | says |
|---:|---:|---|---|
| 1720 | 49 | `TimeChart bigChart(HistorySave h, List<YearBook.Band> bands, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` | The big chart with this redraw's lines, bands, episodes and flags - the flags as its lane draws them (ChartModel.onAxis(), 0.7.37) - at the page's width. |
| 1771 | 3 | `static TimeChart.Axis axisFor(String unit)` | One value axis: its gridlines in the unit's own words (axisTick()), and from zero when a per cent. |

### FULL SCREEN (0.7.23) (lines 1775-1959)

| line | len | member | says |
|---:|---:|---|---|
| 1800 | 31 | `void setChartFull(boolean on, boolean redraw)` | Gives the big chart the whole window, or takes it back - and redraws the page, unless another screen is about to be drawn instead (the main menu, a rail button: UserInterface.leaveChartFullScreen()). |
| 1836 | 3 | `void leaving()` | The page is going: another screen is being drawn (UserInterface.clearMenu()). |
| 1841 | 6 | `private void fitFull()` | The chart sized to the pane: the whole width, and the plot whatever height the rest leaves. |
| 1849 | 4 | `private void refreshFullScreen()` | A redraw in full screen: the clock's line, and the chart's size if the window moved. |
| 1866 | 23 | `static String axisTick(String unit, double v, double step)` | One gridline's label: short enough to fit, honest about its unit. |
| 1899 | 5 | `static String priceTick(double a, double step)` | A price on an axis: cents only when the gridlines are finer than a dollar. |
| 1906 | 6 | `static String shortCash(double a)` | Dollars, abbreviated from a thousand up - an axis has no room for digits. |
| 1914 | 5 | `static String shortCount(double a)` | People, homes, jobs - the same abbreviation without the dollar. |
| 1921 | 4 | `static String trim(double a)` | One decimal at most, and none at all when it would read ".0". |
| 1927 | 16 | `static String unitName(String unit, Currency money)` | What the y-axis is measured in, when every line agrees - the rate in this city's own money (0.7.10). |
| 1952 | 7 | `static double plotScale(String unit, double v)` | The stored value, in the units the axis is labelled in. |

### WHAT EACH LINE DID (0.7.37) (lines 1960-2505)

| line | len | member | says |
|---:|---:|---|---|
| 1974 | 4 | **type** `record Span(double first, double last, double lo, double hi, int loAt, int hiAt)` | A line over the window: its first and last recorded values, its low and high and the indices of each; NaN where nothing is recorded. |
| 1976 | 1 | `boolean flat()` _(in HistoryScreen.Span)_ | Whether it never moved over the window. |
| 1980 | 12 | `static Span span(double[] all, int[] shown)` | One series over the window's indices {from, to} (shownIndices()). |
| 1994 | 2 | **type** `record Reading(String key, String label, String latest, String move, String caption, String rangeTip, doubl...` | One reading card, as words and figures (0.7.37): what the probe reads. |
| 1997 | 24 | `Reading reading(HistorySave h, String key, String axis)` |  |
| 2023 | 3 | `String shown(String unit, double v)` | A figure as this page's cards and pins write it: fmtUnit()'s, with a true minus (0.7.37) - the chart's own card keeps fmtUnit()'s. |
| 2033 | 7 | `String moveWords(String unit, double first, double last)` | How far a line moved, as this page writes it (0.7.37, D5): an up or a down arrow and changeText()'s figure without its sign - points for a rate, per cent for a quantity - in neutral ink; a move too small to show in th... |
| 2042 | 26 | `VBox readings(HistorySave h, boolean fresh)` | The section: its head, the cards three across, and the line that says when two lines share a colour. |
| 2076 | 67 | `VBox readingCard(HistorySave h, String key, String colour, String axis, boolean fresh)` | One line's card: its swatch, name and axis, "pin" or "pinned" (and "layers" for real GDP alone); its figure and its move; its range bar; the caption. |
| 2155 | 22 | `String changeText(String unit, double first, double last)` | How far it moved, in terms the unit deserves. |
| 2210 | 40 | `VBox historyPickerRows()` | The chips, one heading per group - each group closed until it is wanted, and a box over them that finds a line by name (0.7.5). |
| 2252 | 70 | `void fillPicker(VBox groups)` | The groups, as the filter and the player have left them. |
| 2328 | 4 | `Trace traceFor(String key)` |  |
| 2344 | 116 | `double[] historyValues(HistorySave h, String key)` | A series, aligned to the month axis, derived ones included. |
| 2461 | 5 | `double[] minus(double[] a, double[] b)` |  |
| 2468 | 37 | `String fmtUnit(String unit, double v)` | A value in the units it is actually kept in. |

### HARD TIMES AND YOUR DECISIONS (0.7.37) (lines 2506-2819)

| line | len | member | says |
|---:|---:|---|---|
| 2537 | 8 | `static String hardTimesInfo()` | The section's (i): the rules that name an episode, the year book's own. |
| 2552 | 15 | `List<YearBook.Episode> hardTimesInView(List<YearBook.Episode> episodes, int lastMonth)` | The hard times touching the window, as this page lists them (pure): those still running first, in RUNNING NOW's order, but a chronic one last of all; then those the history closed, the most recently ended first. |
| 2569 | 6 | `static List<DecisionLog.Entry> decisionsInView(List<ChartModel.Flag> flags, int from, int to)` | The decisions inside the window, newest first (pure): a founding-month one counts at the history's first month, where its flag stands. |
| 2577 | 4 | `static String spanWords(YearBook.Episode e, int lastMonth)` | "Jun 2192 – May 2197 · 60 months", or "Aug 2199 – now · 6 months" while it runs. |
| 2583 | 3 | `static String worstLine(YearBook.Episode e)` | "real output 13.78% below the year before at Feb 2193": the episode's worst, the year book's words. |
| 2588 | 5 | `String viewWords()` | What the window spans, for an empty list: "these 10 years", "these 18 months", or "the whole history". |
| 2594 | 56 | `VBox hardTimes(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` |  |
| 2652 | 10 | `HBox columnHead(String words, int count)` | A column's head: its words, and how many. |
| 2664 | 6 | `static Label quiet(String text)` | A quiet line: an empty list's words. |
| 2672 | 6 | `Label showAll()` | "show All ›": the whole history in view. |
| 2680 | 8 | `void rowGoes(javafx.scene.layout.Pane row, String tipText, Runnable go)` | A row that moves the chart: the pointer's hand, a ground under it, and the click. |
| 2694 | 26 | `HBox episodeRow(YearBook.Episode e, int lastMonth)` | One hard time: its kind's icon and tag in its colour (the chart's verdict colours, 0.7.23), its name, its span; under it its worst, in the year book's words. |
| 2722 | 17 | `HBox decisionRow(DecisionLog.Entry d)` | One decision: its icon in its flag's area colour, its month, its words - wrapping, never cut; "this month" when it is. |
| 2745 | 13 | `VBox fold(String key, String caption, java.util.function.Supplier<javafx.scene.Node> body)` | A fold on this page (Pieces.details()), its toggle held under the pointer as it opens or closes, a chip's way (D7, D8): the page under it changes length and the scroll memory keeps the bottom. |
| 2760 | 1 | **type** `record KindRow(String kind, String name, int count, int months, List<YearBook.Episode> episodes)` | One kind of episode in the details, as words (pure): its kind, how many, how long all told, and its episodes. |
| 2762 | 10 | `static List<KindRow> kindRows(List<YearBook.Episode> episodes)` |  |
| 2774 | 3 | `static String howLong(int months)` | "42 years" all told, or "9 months" under two years. |
| 2786 | 33 | `javafx.scene.Node byKind(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags)` | Every named episode since founding, by kind (0.7.37): a row a kind that has happened, its episodes on one scale of the history's months in their colour - each at least two pixels, its name, span and worst on its toolt... |

### PRICES THIS MONTH (0.7.37; EVERY GOOD, ON ONE PAGE before) (lines 2820-3014)

| line | len | member | says |
|---:|---:|---|---|
| 2855 | 3 | `GoodsMarket market(Good g)` | A good's market, or null. |
| 2860 | 4 | `Sector seller(Good g)` | The business that sells a seller-priced good: the sector that makes it, or null. |
| 2866 | 5 | `static String goodPrice(double thousands)` | A good's price, to the cent: "$296.67", "$11,454.16" - tiny ones to the place that shows them. |
| 2873 | 8 | `static String flowWords(GoodsMarket m)` | "imported 54,201", "exported 87,294", both, or null when nothing crossed; a fraction of a unit is "under 1" (a wagon set's worth read "imported 0"). |
| 2883 | 3 | `static String units(double v)` | A count of units that crossed: whole ones grouped, or "under 1". |
| 2888 | 3 | `static String some(int n)` | "1 at", "none at": a count in the summary's words. |
| 2898 | 17 | `String priceSummary()` | The line over the fold (pure): how many goods trade with the world and where in their bands the both-ways ones stand - at what the world charges, between, at what it pays - how many are open at one end, and how many t... |
| 2917 | 10 | `VBox prices()` | The section: its head with the (i), the line of counts, and every good behind "details". |
| 2929 | 1 | **type** `record GoodLine(Good good, String figure, String says, double at, String flow)` | One good as a row of the fold, as words (pure): what the probe reads. |
| 2932 | 25 | `List<GoodLine> goodLines()` | Every good, as the fold draws it: the both-ways goods with their place in the band, the rest with their words. |
| 2965 | 49 | `javafx.scene.Node goodsRows()` | The fold: the goods traded both ways on their bands, the world's floor and ceiling a rule through every row; then those open at one end and those their seller prices, in words. |

