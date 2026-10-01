# HistoryScreen.java - 2,030 lines · 54 methods · 10 constants · interface

`ham/citybuildersim/ui/HistoryScreen.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> The Reports tab: the city as a shape over time.
> 
> Two small pinned charts, then one big chart with lines overlaid - one unit
> on a real axis, two on two real axes, three or more mapped onto 0-100 by
> their own range over the window - with recessions shaded and named, the
> episodes on a lane under it and the player's decisions as flags; a
> legend that carries the real values, presets for the questions a player
> usually has, a picker folded into its groups, and the year book written
> out on request (the page as of 0.7.5 is under THE PAGE, REDRAWN; real GDP
> drawn in layers on a toggle since 0.7.6, under GDP IN LAYERS). Since
> 0.7.23 the charts are TimeCharts - dragged, wheeled and ranged like a
> market chart, with an overview and a full screen - over one window of
> months (THE CHART, REBUILT; FULL SCREEN). The first screen split out of
> UserInterface (2026-09-18), chosen because it is the most self-contained:
> it reads the window's game and root, calls clearMenu() and scrolled(), and
> nothing else in the shell reads it but the rail.
> 
> The text came over exactly as it was inside UserInterface - the two banners,
> THE HISTORY SCREEN and THE RECORD, and everything under them - with the
> shell's members reached through ui. Nothing was rewritten on the way. THE
> GOODS ROW followed later the same day: it had sat under the shell's THE
> STATEMENT banner, and this screen was the only thing that drew it.

**Uses:** [Palette](Palette.md) (105), [HistorySave](HistorySave.md) (24), [TimeChart](TimeChart.md) (18), [YearBook](YearBook.md) (14), [GamePrefs](GamePrefs.md) (9), [ChartModel](ChartModel.md) (7), [Crime](Crime.md) (6), [FamilyStructure](FamilyStructure.md) (6), [Equity](Equity.md) (5), [Good](Good.md) (3), [UserInterface](UserInterface.md) (2), [GameFiles](GameFiles.md) (2), [CityCalendar](CityCalendar.md) (2), [Currency](Currency.md) (2), [GoodsMarket](GoodsMarket.md) (1)

**Used by (2):** [TimeChart](TimeChart.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 55 | THE HISTORY SCREEN |
| 368 | THE RECORD |
| 458 | THE PAGE, REDRAWN (0.7.5) |
| 511 | · SEEDED ONCE, AND THEN LEFT ALONE. |
| 609 | · EVERY GOOD, ON ONE PAGE. |
| 722 | · (untitled) |
| 800 | · a chip on this page, and the thing pressed held still (0.7.5) |
| 895 | THE PINS (0.7.5) |
| 1069 | GDP IN LAYERS (0.7.6) |
| 1168 | THE BIG CHART (0.7.5) |
| 1265 | THE CHART, REBUILT (0.7.23) |
| 1355 | FULL SCREEN (0.7.23) |
| 1974 | THE GOODS ROW |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 142 | `HistoryScreen.TRACES` | `withTheCrime(withTheHouseholds(withTheMarket(new Trace[] { new Trace("gdp", "...` |  |
| 404 | `HistoryScreen.GRAPH` | `760` | How wide this one screen runs. |
| 409 | `HistoryScreen.PRESETS` | `{ new Preset("What money costs", "the borrowing rate, the price level, and ho...` |  |
| 910 | `HistoryScreen.SMALL_CHART` | `150` | How tall a pinned chart is. |
| 1102 | `HistoryScreen.LAYERED` | `"realGdp"` | The one line this page can draw in layers. |
| 1105 | `HistoryScreen.LAYERED_LINE` | `Palette.TEXT_HEAD` | What the GDP line is drawn in over the layers: the headings' ink, which no step of the blue ramp is near. |
| 1108 | `HistoryScreen.LAYER_NAMES` | `{ "consumption", "investment", "government", "net exports" }` | What each part is called on the key and in the crosshair, in YearBook.GDP_PARTS' order. |
| 1173 | `HistoryScreen.BIG_CHART` | `380` | How tall the big chart's plot is on the page; the lanes and the overview are under it. |
| 1176 | `HistoryScreen.CONTROLS` | `130` | Room kept at the right of the preset row for "clear all" and "log". |
| 1972 | `HistoryScreen.TABLE_WIDTH` | `660` | How wide the paragraph above the buyback table wraps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 88 | `final java.util.LinkedHashSet<String> historyPicked` | What the player has ticked, and how far back they are looking. |
| 91 | `boolean historySeeded` | Whether the first-visit preset has been handed over; see showHistoryMenu. |
| 101 | `final ChartModel chartWindow` | The window of months the charts show (0.7.23): the big chart's, which the two small ones follow - dragged, wheeled and ranged by the player, and following the newest month while its right edge is on it (ChartModel). |
| 104 | `final java.util.Set<String> hiddenLines` | The lines the big chart's legend has switched off, by key: still picked and read below, not drawn (0.7.23). |
| 111 | `boolean historyLog` | The big chart on a log scale, when what is picked allows it; see logRefusal(). |
| 120 | `boolean gdpLayers` | Real GDP drawn in layers - consumption, investment and government stacked from zero, the line over them (0.7.6) - on its small chart, and on the big one when it is picked alone. |
| 123 | `String historyFilter` | What is typed in the picker's filter box, kept so a month's redraw does not wipe it. |
| 130 | `final java.util.Map<String, Boolean> groupOpen` | Which of the picker's groups the player has opened or closed, by name. |
| 691 | `String bookExportSaid` | What the last export did, kept so it survives the redraw. |
| 803 | `private javafx.scene.control.ScrollPane page` | The page's scroller, kept so a click can be put back where it was after the rebuild. |
| 806 | `private String pressed` | The chip last pressed, by name, and how far down the window it was; the next rebuild spends it. |
| 807 | `private double pressedAt` |  |
| 810 | `private final java.util.Map<String, javafx.scene.Node> named` | This build's chips by name, so the rebuild can find the one that was pressed. |
| 813 | `private TextField filterField` | The picker's filter box, so a redraw can hand the focus back to it; see showHistoryMenu. |
| 978 | `private final TimeChart[] smallCharts` | The two small charts' nodes, kept from one redraw to the next, as the big chart is: each follows the big chart's window as it is dragged, which a chart rebuilt every month could not (0.7.23). |
| 1295 | `private TimeChart bigChart` | The big chart, made once; see the banner. |
| 1298 | `private boolean chartFull` | Whether the big chart has the whole window (0.7.23); see FULL SCREEN. |
| 1372 | `private VBox fullPane` | The pane the chart is laid in over the whole window, and its clock line. |
| 1373 | `private Label fullClock` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 1983 | **type** `final class HistoryScreen` | The Reports tab: the city as a shape over time. |
| 53 | 1 | `HistoryScreen(UserInterface ui)` |  |

### THE HISTORY SCREEN (lines 55-367)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 1 | **type** `record Trace(String key, String label, String group, String unit)` | One plottable line: where it comes from and how to read it. |
| 286 | 9 | `static Trace[] withTheCrime(Trace[] fixed)` | One series per household shape, appended after the market. |
| 296 | 9 | `static Trace[] withTheHouseholds(Trace[] fixed)` |  |
| 312 | 9 | `static Trace[] withTheMarket(Trace[] fixed)` | ...and a share price per company, one trace each, generated off the register's own list so a company added there is graphed here without anybody remembering to. |
| 330 | 9 | `static String areaOf(Trace t)` | Which of the four areas a line belongs to, for its colour (0.7.23): money and policy blue, people and services teal, business and trade violet, building and land pink - the picker's groups mapped onto the rail's areas... |
| 341 | 8 | `static String groupArea(String group)` | A picker group's area: what its heading is coloured in. |
| 351 | 5 | `String[] pickedColours()` | Each picked line's colour, in the order picked: its area's, then the others (Palette.lineColours()). |
| 358 | 9 | `String colourOf(String key)` | One picked line's colour, or null if it is not picked. |

### THE RECORD (lines 368-457)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 1 | **type** `record Preset(String name, String blurb, String[] keys)` | A curated set of lines that belong on one chart. |
| 452 | 5 | `static String[] marketKeys()` | Every company's share price, for the market preset. |

### THE PAGE, REDRAWN (0.7.5) (lines 458-721)

| line | len | member | says |
|---:|---:|---|---|
| 495 | 189 | `void showHistoryMenu()` |  |
| 698 | 23 | `void writeTheBooks()` | Six files since 0.7.16 - each book's text and its two tables as CSV - all in one folder, so the folder is named once and each book is a line of file names; anything that failed says so on a line of its own. |

### (untitled) (lines 722-799)

| line | len | member | says |
|---:|---:|---|---|
| 724 | 46 | `HBox historyVitals(HistorySave h)` |  |
| 776 | 9 | `int[] shownIndices(HistorySave h)` | The window's first and last months as indices into the history's axis, {from, to} - what the vitals and the readings fold over (0.7.23; they read historyWindow's last N months before). |
| 787 | 12 | `Label pickChip(String text, boolean on, Runnable act)` | A chip that is on or off, which is every control on this screen. |

### a chip on this page, and the thing pressed held still (0.7.5) (lines 800-894)

| line | len | member | says |
|---:|---:|---|---|
| 820 | 10 | `Label chip(String name, String text, boolean on, Runnable act)` | A chip on this page: pickChip, remembered by name, and held where it was on the screen when pressed. |
| 832 | 6 | `Label stillChip(String text, boolean on, String why)` | A chip that only says something: lit or not, no act, and why on hover. |
| 839 | 5 | `static void tip(javafx.scene.Node node, String text)` |  |
| 859 | 18 | `private void holdInPlace(javafx.scene.Node node, double wasAt)` | Puts `node` back `wasAt` scene pixels down the window, once the page has been laid out. |
| 886 | 8 | `List<String> pickedUnits()` | The units of the picked lines, each once, in the order they were picked. |

### THE PINS (0.7.5) (lines 895-1068)

| line | len | member | says |
|---:|---:|---|---|
| 917 | 9 | `String pinned(boolean left)` | The line on a small chart. |
| 928 | 4 | `static boolean known(String key)` | Whether this page draws a line by that name. |
| 938 | 7 | `void pin(String key)` | Pins a line. |
| 947 | 7 | `String unpinned(boolean left)` | What unpinning a side would put back: its default, or the other default if that one is showing beside it. |
| 955 | 6 | `void unpin(boolean left)` |  |
| 963 | 9 | `HBox pinnedCharts(HistorySave h, List<YearBook.Band> bands)` | The two small charts, side by side, on the big chart's window. |
| 981 | 87 | `VBox smallChart(HistorySave h, boolean left, double wide, List<YearBook.Band> bands)` | One pinned line: its name and latest reading, then the line in its own units, its years under it. |

### GDP IN LAYERS (0.7.6) (lines 1069-1167)

| line | len | member | says |
|---:|---:|---|---|
| 1111 | 3 | `boolean bigInLayers()` | Whether the big chart draws its one line in layers: real GDP picked alone, with the toggle on. |
| 1116 | 11 | `Label layersChip(String where)` | The chip that toggles the layers, on the small chart's head and on the big chart's reading. |
| 1134 | 6 | `TimeChart.Stack gdpStack(HistorySave h, String unit)` | GDP's four parts, a rolling year in founding money each - C, I, G, then NX - as a chart stacks them (0.7.23): C, I and G drawn from zero in the three steps of the ramp, net exports read on the crosshair, in the muted ... |
| 1146 | 21 | `VBox layersKey(double[][] layers, double wide)` | The key under a layered chart - a swatch per layer and one for the line - and the sentence that says how to read the gap. |

### THE BIG CHART (0.7.5) (lines 1168-1264)

| line | len | member | says |
|---:|---:|---|---|
| 1182 | 53 | `HBox chartControls(HistorySave h)` | The row above the big chart: the presets on the left, "clear all" and "log" on the right. |
| 1246 | 18 | `String logRefusal(HistorySave h)` | Why the log scale cannot draw what is picked, or null when it can. |

### THE CHART, REBUILT (0.7.23) (lines 1265-1354)

| line | len | member | says |
|---:|---:|---|---|
| 1301 | 48 | `TimeChart bigChart(HistorySave h, List<YearBook.Band> bands, List<YearBook.Episode> episodes)` | The big chart with this redraw's lines, bands, episodes and flags. |
| 1351 | 3 | `static TimeChart.Axis axisFor(String unit)` | One value axis: its gridlines in the unit's own words (axisTick()), and from zero when a per cent. |

### FULL SCREEN (0.7.23) (lines 1355-1973)

| line | len | member | says |
|---:|---:|---|---|
| 1380 | 31 | `void setChartFull(boolean on, boolean redraw)` | Gives the big chart the whole window, or takes it back - and redraws the page, unless another screen is about to be drawn instead (the main menu, a rail button: UserInterface.leaveChartFullScreen()). |
| 1416 | 3 | `void leaving()` | The page is going: another screen is being drawn (UserInterface.clearMenu()). |
| 1421 | 6 | `private void fitFull()` | The chart sized to the pane: the whole width, and the plot whatever height the rest leaves. |
| 1429 | 4 | `private void refreshFullScreen()` | A redraw in full screen: the clock's line, and the chart's size if the window moved. |
| 1446 | 23 | `static String axisTick(String unit, double v, double step)` | One gridline's label: short enough to fit, honest about its unit. |
| 1479 | 5 | `static String priceTick(double a, double step)` | A price on an axis: cents only when the gridlines are finer than a dollar. |
| 1486 | 6 | `static String shortCash(double a)` | Dollars, abbreviated from a thousand up - an axis has no room for digits. |
| 1494 | 5 | `static String shortCount(double a)` | People, homes, jobs - the same abbreviation without the dollar. |
| 1501 | 4 | `static String trim(double a)` | One decimal at most, and none at all when it would read ".0". |
| 1507 | 16 | `static String unitName(String unit, Currency money)` | What the y-axis is measured in, when every line agrees - the rate in this city's own money (0.7.10). |
| 1532 | 7 | `static double plotScale(String unit, double v)` | The stored value, in the units the axis is labelled in. |
| 1547 | 78 | `VBox historyReading(HistorySave h, String key, String colour, String axis)` | One line's reading: what it is now, and what it did. |
| 1634 | 18 | `String changeText(String unit, double first, double last)` | How far it moved, in terms the unit deserves. |
| 1678 | 38 | `VBox historyPickerRows()` | The chips, one heading per group - each group closed until it is wanted, and a box over them that finds a line by name (0.7.5). |
| 1718 | 67 | `void fillPicker(VBox groups)` | The groups, as the filter and the player have left them. |
| 1786 | 4 | `Trace traceFor(String key)` |  |
| 1802 | 116 | `double[] historyValues(HistorySave h, String key)` | A series, aligned to the month axis, derived ones included. |
| 1919 | 5 | `double[] minus(double[] a, double[] b)` |  |
| 1926 | 37 | `String fmtUnit(String unit, double v)` | A value in the units it is actually kept in. |

### THE GOODS ROW (lines 1974-2030)

| line | len | member | says |
|---:|---:|---|---|
| 1990 | 40 | `HBox goodsRow(Good g)` | One good: what it is, what it costs here, and what happened to it. |

