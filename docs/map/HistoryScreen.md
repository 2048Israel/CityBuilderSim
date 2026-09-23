# HistoryScreen.java - 2,481 lines · 66 methods · 15 constants · interface

`ham/citybuildersim/ui/HistoryScreen.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The Reports tab: the city as a shape over time.
> 
> Two small pinned charts, then one big chart with lines overlaid - one unit
> on a real axis, two on two real axes, three or more mapped onto 0-100 by
> their own range over the window - with recessions shaded and the named
> episodes marked under it; a legend that carries the real values, presets
> for the questions a player usually has, a picker folded into its groups,
> and the year book written out on request (the page as of 0.7.5 is under
> THE PAGE, REDRAWN; real GDP drawn in layers on a toggle since 0.7.6, under
> GDP IN LAYERS). The first screen split out of
> UserInterface (2026-09-18), chosen because it is the most self-contained:
> it reads the window's game and root, calls clearMenu() and scrolled(), and
> nothing else in the shell reads it but the rail.
> 
> The text came over exactly as it was inside UserInterface - the two banners,
> THE HISTORY SCREEN and THE RECORD, and everything under them - with the
> shell's members reached through ui. Nothing was rewritten on the way. THE
> GOODS ROW followed later the same day: it had sat under the shell's THE
> STATEMENT banner, and this screen was the only thing that drew it.

**Uses:** [Palette](Palette.md) (100), [HistorySave](HistorySave.md) (24), [YearBook](YearBook.md) (14), [GamePrefs](GamePrefs.md) (9), [Crime](Crime.md) (6), [FamilyStructure](FamilyStructure.md) (6), [Equity](Equity.md) (5), [CityCalendar](CityCalendar.md) (5), [Good](Good.md) (3), [UserInterface](UserInterface.md) (2), [GameFiles](GameFiles.md) (1), [Currency](Currency.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 53 | THE HISTORY SCREEN |
| 314 | THE RECORD |
| 404 | THE PAGE, REDRAWN (0.7.5) |
| 449 | · SEEDED ONCE, AND THEN LEFT ALONE. |
| 533 | · EVERY GOOD, ON ONE PAGE. |
| 613 | · (untitled) |
| 715 | · a chip on this page, and the thing pressed held still (0.7.5) |
| 810 | THE PINS (0.7.5) |
| 975 | GDP IN LAYERS (0.7.6) |
| 1186 | THE BIG CHART (0.7.5) |
| 1422 | · · the crosshair: a line at the month under the pointer, and what every line read there |
| 1508 | · · the named episodes: a tick under the chart where each began, and their names |
| 2425 | THE GOODS ROW |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 119 | `HistoryScreen.MAX_PLOT_POINTS` | `400` | Above this many points a line is bucket-averaged; see bucketSize(). |
| 131 | `HistoryScreen.TRACES` | `withTheCrime(withTheHouseholds(withTheMarket(new Trace[] { new Trace("gdp", "...` |  |
| 309 | `HistoryScreen.TRACE_COLOURS` | `{ "#5cb8ff", "#ff6b6b", "#5fd68a", "#ffb454", "#ce93d8", "#4dd0e1", "#d4e157"...` | Eight, then it wraps - and the legend swatch uses the same list. |
| 350 | `HistoryScreen.GRAPH` | `760` | How wide this one screen runs. |
| 355 | `HistoryScreen.PRESETS` | `{ new Preset("What money costs", "the borrowing rate, the price level, and ho...` |  |
| 825 | `HistoryScreen.SMALL_CHART` | `150` | How tall a pinned chart is. |
| 1010 | `HistoryScreen.LAYERED` | `"realGdp"` | The one line this page can draw in layers. |
| 1013 | `HistoryScreen.LAYERED_LINE` | `Palette.TEXT_HEAD` | What the GDP line is drawn in over the layers: the headings' ink, which no step of the blue ramp is near. |
| 1016 | `HistoryScreen.SMALL_Y_AXIS` | `56` | How wide a small chart's y-axis is held when a stack is drawn behind it, so the two plots line up. |
| 1019 | `HistoryScreen.LAYER_NAMES` | `{ "consumption", "investment", "government", "net exports" }` | What each part is called on the key and in the crosshair, in YearBook.GDP_PARTS' order. |
| 1191 | `HistoryScreen.BIG_CHART` | `380` | How tall the big chart is. |
| 1199 | `HistoryScreen.Y_AXIS` | `76` | How wide each y-axis is held when there are two. |
| 1202 | `HistoryScreen.RECESSION_SHADE` | `0.12` | How strongly a recession is shaded: enough to see, not enough to read as a colour. |
| 1205 | `HistoryScreen.CONTROLS` | `130` | Room kept at the right of the preset row for "clear all" and "log". |
| 2423 | `HistoryScreen.TABLE_WIDTH` | `660` | How wide the paragraph above the buyback table wraps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 86 | `final java.util.LinkedHashSet<String> historyPicked` | What the player has ticked, and how far back they are looking. |
| 89 | `boolean historySeeded` | Whether the first-visit preset has been handed over; see showHistoryMenu. |
| 90 | `int historyWindow` |  |
| 97 | `boolean historyLog` | The big chart on a log scale, when what is picked allows it; see logRefusal(). |
| 106 | `boolean gdpLayers` | Real GDP drawn in layers - consumption, investment and government stacked from zero, the line over them (0.7.6) - on its small chart, and on the big one when it is picked alone. |
| 109 | `String historyFilter` | What is typed in the picker's filter box, kept so a month's redraw does not wipe it. |
| 116 | `final java.util.Map<String, Boolean> groupOpen` | Which of the picker's groups the player has opened or closed, by name. |
| 600 | `String bookExportSaid` | What the last export did, kept so it survives the redraw. |
| 718 | `private javafx.scene.control.ScrollPane page` | The page's scroller, kept so a click can be put back where it was after the rebuild. |
| 721 | `private String pressed` | The chip last pressed, by name, and how far down the window it was; the next rebuild spends it. |
| 722 | `private double pressedAt` |  |
| 725 | `private final java.util.Map<String, javafx.scene.Node> named` | This build's chips by name, so the rebuild can find the one that was pressed. |
| 728 | `private TextField filterField` | The picker's filter box, so a redraw can hand the focus back to it; see showHistoryMenu. |
| 1754 | `private final List<int[]> bands` | Runs of months shaded as recession, {first, last}, and the shapes drawn for them. |
| 1755 | `private final List<javafx.scene.shape.Rectangle> bandShapes` |  |
| 1758 | `private final javafx.scene.shape.Line cursor` | The crosshair's line, over the lines once withCursor() has moved it there. |
| 1761 | `private final List<Integer> markMonths` | Ticks outside the chart - in a strip laid out under it - at the month each belongs to. |
| 1762 | `private final List<Region> marks` |  |
| 1763 | `private Pane markStrip` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 2436 | **type** `final class HistoryScreen` | The Reports tab: the city as a shape over time. |
| 51 | 1 | `HistoryScreen(UserInterface ui)` |  |

### THE HISTORY SCREEN (lines 53-313)

| line | len | member | says |
|---:|---:|---|---|
| 129 | 1 | **type** `record Trace(String key, String label, String group, String unit)` | One plottable line: where it comes from and how to read it. |
| 272 | 9 | `static Trace[] withTheCrime(Trace[] fixed)` | One series per household shape, appended after the market. |
| 282 | 9 | `static Trace[] withTheHouseholds(Trace[] fixed)` |  |
| 298 | 9 | `static Trace[] withTheMarket(Trace[] fixed)` | ...and a share price per company, one trace each, generated off the register's own list so a company added there is graphed here without anybody remembering to. |

### THE RECORD (lines 314-403)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 1 | **type** `record Preset(String name, String blurb, String[] keys)` | A curated set of lines that belong on one chart. |
| 398 | 5 | `static String[] marketKeys()` | Every company's share price, for the market preset. |

### THE PAGE, REDRAWN (0.7.5) (lines 404-612)

| line | len | member | says |
|---:|---:|---|---|
| 438 | 155 | `void showHistoryMenu()` |  |
| 602 | 10 | `void writeTheBooks()` |  |

### (untitled) (lines 613-714)

| line | len | member | says |
|---:|---:|---|---|
| 615 | 50 | `HBox historyVitals(HistorySave h)` |  |
| 667 | 20 | `VBox historyRange(HistorySave h)` | 10 years, 50 years, or the whole life of the city. |
| 697 | 3 | `int bucketSize(int points)` | How many months go into one drawn point. |
| 702 | 12 | `Label pickChip(String text, boolean on, Runnable act)` | A chip that is on or off, which is every control on this screen. |

### a chip on this page, and the thing pressed held still (0.7.5) (lines 715-809)

| line | len | member | says |
|---:|---:|---|---|
| 735 | 10 | `Label chip(String name, String text, boolean on, Runnable act)` | A chip on this page: pickChip, remembered by name, and held where it was on the screen when pressed. |
| 747 | 6 | `Label stillChip(String text, boolean on, String why)` | A chip that only says something: lit or not, no act, and why on hover. |
| 754 | 5 | `static void tip(javafx.scene.Node node, String text)` |  |
| 774 | 18 | `private void holdInPlace(javafx.scene.Node node, double wasAt)` | Puts `node` back `wasAt` scene pixels down the window, once the page has been laid out. |
| 801 | 8 | `List<String> pickedUnits()` | The units of the picked lines, each once, in the order they were picked. |

### THE PINS (0.7.5) (lines 810-974)

| line | len | member | says |
|---:|---:|---|---|
| 832 | 9 | `String pinned(boolean left)` | The line on a small chart. |
| 843 | 4 | `static boolean known(String key)` | Whether this page draws a line by that name. |
| 853 | 7 | `void pin(String key)` | Pins a line. |
| 862 | 7 | `String unpinned(boolean left)` | What unpinning a side would put back: its default, or the other default if that one is showing beside it. |
| 870 | 6 | `void unpin(boolean left)` |  |
| 878 | 9 | `HBox pinnedCharts(HistorySave h, List<int[]> recessions)` | The two small charts, side by side. |
| 889 | 85 | `VBox smallChart(HistorySave h, boolean left, double wide, List<int[]> recessions)` | One pinned line: its name and latest reading, then the line in its own units. |

### GDP IN LAYERS (0.7.6) (lines 975-1185)

| line | len | member | says |
|---:|---:|---|---|
| 1022 | 3 | `boolean bigInLayers()` | Whether the big chart draws its one line in layers: real GDP picked alone, with the toggle on. |
| 1027 | 11 | `Label layersChip(String where)` | The chip that toggles the layers, on the small chart's head and on the big chart's reading. |
| 1040 | 7 | `static double[][] layerValues(HistorySave h, int from, int bucket, int points)` | GDP's four parts, a rolling year in founding money each, averaged into the drawn points - C, I, G, then NX. |
| 1054 | 13 | `static double[] stackReach(double[][] layers, String unit)` | The lowest and highest the stack reaches, in the axis's units: from zero to its top, and below zero wherever a layer is negative (a month of run-down stock is negative investment). |
| 1074 | 50 | `javafx.scene.chart.StackedAreaChart<Number, Number> layersBehind(Plot front, List<Integer> months, int from, int[] at, double[]...` | The three layers as a chart to stand behind `front`: the same size, the same months, the axes there and invisible, the y-axis held to `axisWide` on both, no legend and no mouse. |
| 1126 | 9 | `static StackPane stacked(Plot front, javafx.scene.chart.StackedAreaChart<Number, Number> under, double wide, double tall)` | The stack behind, the line chart in front, in one pane of the chart's size. |
| 1142 | 16 | `void styleArea(javafx.scene.chart.XYChart.Series<Number, Number> layer, String colour)` | Paints one layer: its fill in its ramp step, a little translucent so the grid shows through, and its edge in the step itself. |
| 1164 | 21 | `VBox layersKey(double[][] layers, double wide)` | The key under a layered chart - a swatch per layer and one for the line - and the sentence that says how to read the gap. |

### THE BIG CHART (0.7.5) (lines 1186-2424)

| line | len | member | says |
|---:|---:|---|---|
| 1211 | 51 | `HBox chartControls(HistorySave h)` | The row above the big chart: the presets on the left, "clear all" and "log" on the right. |
| 1271 | 17 | `String logRefusal(HistorySave h, int from)` | Why the log scale cannot draw what is picked, or null when it can. |
| 1295 | 253 | `VBox historyChart(HistorySave h, List<int[]> recessions, List<YearBook.Episode> episodes)` | The big chart: the picked lines on one axis, two, or each its own low-to-high; the recessions shaded behind them; a crosshair that reads every line at the month under the pointer; and the named episodes marked under it. |
| 1550 | 8 | `String axisLabel(String unit, boolean log)` | What an axis is called: the line's own name when it carries one line, its unit when more. |
| 1568 | 19 | `static NumberAxis monthAxis(List<Integer> months, int from)` | The months of the window, first to last - and the same on every chart on the page. |
| 1598 | 105 | `static void rangeAxis(NumberAxis y, String unit, double low, double high, boolean log, String label, int ticks)` | Ranges a value axis over what is drawn on it, and labels it. |
| 1705 | 8 | `static int[] bucketMonths(List<Integer> months, int from, int bucket)` | The month each drawn point stands for - the middle of its bucket. |
| 1715 | 15 | `static double[] bucketed(double[] all, int from, int bucket, int points)` | A line averaged into those points; NaN where a bucket had nothing recorded. |
| 1732 | 7 | `static int nearest(int[] at, double month)` | The drawn point nearest a month. |
| 1751 | 105 | **type** `static final class Plot extends javafx.scene.chart.LineChart<Number, Number>` | A LineChart that also draws what its lines sit on and what points at them. |
| 1765 | 14 | `Plot(NumberAxis x, NumberAxis y, double width, double height)` _(in HistoryScreen.Plot)_ |  |
| 1780 | 1 | `NumberAxis yAxis()` _(in HistoryScreen.Plot)_ |  |
| 1783 | 10 | `void shade(List<int[]> runs)` _(in HistoryScreen.Plot)_ | Shades these runs of months, {first, last}, behind the lines. |
| 1795 | 4 | `void withCursor()` _(in HistoryScreen.Plot)_ | Moves the crosshair's line over the lines - call after they are added. |
| 1801 | 8 | `void showCursor(double month)` _(in HistoryScreen.Plot)_ | The crosshair at a month, in the plot's own coordinates; no layout pass needed. |
| 1810 | 1 | `void hideCursor()` _(in HistoryScreen.Plot)_ |  |
| 1816 | 6 | `void mark(Pane strip, int month, Region tick)` _(in HistoryScreen.Plot)_ | A tick at a month, in `strip` - a pane laid out beside this chart, under it - positioned whenever the chart lays out. |
| 1823 | 32 | `protected void layoutPlotChildren()` _(in HistoryScreen.Plot)_ |  |
| 1869 | 22 | `static String axisTick(String unit, double v, double step)` | One gridline's label: short enough to fit, honest about its unit. |
| 1901 | 5 | `static String priceTick(double a, double step)` | A price on an axis: cents only when the gridlines are finer than a dollar. |
| 1908 | 6 | `static String shortCash(double a)` | Dollars, abbreviated from a thousand up - an axis has no room for digits. |
| 1916 | 5 | `static String shortCount(double a)` | People, homes, jobs - the same abbreviation without the dollar. |
| 1923 | 4 | `static String trim(double a)` | One decimal at most, and none at all when it would read ".0". |
| 1936 | 6 | `static double niceStep(double raw)` | One, two or five times a power of ten - the only steps a reader can add up. |
| 1944 | 16 | `static String unitName(String unit)` | What the y-axis is measured in, when every line agrees. |
| 1969 | 7 | `static double plotScale(String unit, double v)` | The stored value, in the units the axis is labelled in. |
| 1986 | 11 | `void styleLine(javafx.scene.chart.XYChart.Series<Number, Number> line, String colour)` | Paints one line, now or as soon as it has a node. |
| 2005 | 79 | `VBox historyReading(HistorySave h, String key, String colour, String axis)` | One line's reading: what it is now, and what it did. |
| 2093 | 18 | `String changeText(String unit, double first, double last)` | How far it moved, in terms the unit deserves. |
| 2137 | 38 | `VBox historyPickerRows()` | The chips, one heading per group - each group closed until it is wanted, and a box over them that finds a line by name (0.7.5). |
| 2177 | 59 | `void fillPicker(VBox groups)` | The groups, as the filter and the player have left them. |
| 2237 | 4 | `Trace traceFor(String key)` |  |
| 2253 | 116 | `double[] historyValues(HistorySave h, String key)` | A series, aligned to the month axis, derived ones included. |
| 2370 | 5 | `double[] minus(double[] a, double[] b)` |  |
| 2377 | 37 | `String fmtUnit(String unit, double v)` | A value in the units it is actually kept in. |

### THE GOODS ROW (lines 2425-2481)

| line | len | member | says |
|---:|---:|---|---|
| 2441 | 40 | `HBox goodsRow(Good g)` | One good: what it is, what it costs here, and what happened to it. |

