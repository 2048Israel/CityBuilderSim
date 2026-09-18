# HistoryScreen.java - 1,427 lines · 32 methods · 6 constants · interface

`ham/citybuildersim/ui/HistoryScreen.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The Reports tab: the city as a shape over time.
> 
> One chart with lines overlaid, each series in its own units or all of them
> mapped onto 0-100 by their own range over the window, a legend that carries
> the real values, presets for the questions a player usually has, and the
> year book written out on request. The first screen split out of
> UserInterface (2026-09-18), chosen because it is the most self-contained:
> it reads the window's game and root, calls clearMenu() and scrolled(), and
> nothing else in the shell reads it but the rail.
> 
> The text is exactly what it was inside UserInterface - the two banners, THE
> HISTORY SCREEN and THE RECORD, and everything under them - with the shell's
> members reached through ui. Nothing was rewritten on the way. THE GOODS ROW
> followed later the same day: it had sat under the shell's THE STATEMENT
> banner, and this screen was the only thing that drew it.

**Uses:** [Palette](Palette.md) (56), [HistorySave](HistorySave.md) (19), [Crime](Crime.md) (6), [FamilyStructure](FamilyStructure.md) (6), [Equity](Equity.md) (5), [Good](Good.md) (3), [YearBook](YearBook.md) (3), [UserInterface](UserInterface.md) (2), [CityCalendar](CityCalendar.md) (2), [GameFiles](GameFiles.md) (1), [Currency](Currency.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 43 | THE HISTORY SCREEN |
| 273 | THE RECORD |
| 359 | · SEEDED ONCE, AND THEN LEFT ALONE. |
| 427 | · EVERY GOOD, ON ONE PAGE. |
| 487 | · (untitled) |
| 1371 | THE GOODS ROW |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 80 | `HistoryScreen.MAX_PLOT_POINTS` | `400` | Above this many points a line is bucket-averaged; see bucketSize(). |
| 92 | `HistoryScreen.TRACES` | `withTheCrime(withTheHouseholds(withTheMarket(new Trace[] { new Trace("gdp", "...` |  |
| 268 | `HistoryScreen.TRACE_COLOURS` | `{ "#5cb8ff", "#ff6b6b", "#5fd68a", "#ffb454", "#ce93d8", "#4dd0e1", "#d4e157"...` | Eight, then it wraps - and the legend swatch uses the same list. |
| 308 | `HistoryScreen.GRAPH` | `760` | How wide this one screen runs. |
| 313 | `HistoryScreen.PRESETS` | `{ new Preset("How it is going", "output, people, and what money costs", new S...` |  |
| 1369 | `HistoryScreen.TABLE_WIDTH` | `660` | How wide the paragraph above the buyback table wraps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 39 | `private final UserInterface ui` | The window this screen draws into: its game, its root, its clearMenu(). |
| 73 | `final java.util.LinkedHashSet<String> historyPicked` | What the player has ticked, and how far back they are looking. |
| 76 | `boolean historySeeded` | Whether the first-visit preset has been handed over; see showHistoryMenu. |
| 77 | `int historyWindow` |  |
| 474 | `String bookExportSaid` | What the last export did, kept so it survives the redraw. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 1392 | **type** `final class HistoryScreen` | The Reports tab: the city as a shape over time. |
| 41 | 1 | `HistoryScreen(UserInterface ui)` |  |

### THE HISTORY SCREEN (lines 43-272)

| line | len | member | says |
|---:|---:|---|---|
| 90 | 1 | **type** `record Trace(String key, String label, String group, String unit)` | One plottable line: where it comes from and how to read it. |
| 231 | 9 | `static Trace[] withTheCrime(Trace[] fixed)` | One series per household shape, appended after the market. |
| 241 | 9 | `static Trace[] withTheHouseholds(Trace[] fixed)` |  |
| 257 | 9 | `static Trace[] withTheMarket(Trace[] fixed)` | ...and a share price per company, one trace each, generated off the register's own list so a company added there is graphed here without anybody remembering to. |

### THE RECORD (lines 273-486)

| line | len | member | says |
|---:|---:|---|---|
| 311 | 1 | **type** `record Preset(String name, String blurb, String[] keys)` | A curated set of lines that belong on one chart. |
| 348 | 5 | `static String[] marketKeys()` | Every company's share price, for the market preset. |
| 354 | 113 | `void showHistoryMenu()` |  |
| 476 | 10 | `void writeTheBooks()` |  |

### (untitled) (lines 487-1370)

| line | len | member | says |
|---:|---:|---|---|
| 489 | 50 | `HBox historyVitals(HistorySave h)` |  |
| 541 | 20 | `VBox historyRange(HistorySave h)` | 10 years, 50 years, or the whole life of the city. |
| 571 | 3 | `int bucketSize(int points)` | How many months go into one drawn point. |
| 576 | 12 | `Label pickChip(String text, boolean on, Runnable act)` | A chip that is on or off, which is every control on this screen. |
| 596 | 9 | `String sharedUnit()` | True when every selected line is measured in the same thing. |
| 606 | 185 | `javafx.scene.chart.LineChart<Number, Number> historyChart(HistorySave h)` |  |
| 804 | 20 | `static String axisTick(String unit, double v, double step)` | One gridline's label: short enough to fit, honest about its unit. |
| 834 | 5 | `static String priceTick(double a, double step)` | A price on an axis: cents only when the gridlines are finer than a dollar. |
| 841 | 6 | `static String shortCash(double a)` | Dollars, abbreviated from a thousand up - an axis has no room for digits. |
| 849 | 5 | `static String shortCount(double a)` | People, homes, jobs - the same abbreviation without the dollar. |
| 856 | 4 | `static String trim(double a)` | One decimal at most, and none at all when it would read ".0". |
| 869 | 6 | `static double niceStep(double raw)` | One, two or five times a power of ten - the only steps a reader can add up. |
| 877 | 16 | `static String unitName(String unit)` | What the y-axis is measured in, when every line agrees. |
| 902 | 7 | `static double plotScale(String unit, double v)` | The stored value, in the units the axis is labelled in. |
| 919 | 11 | `void styleLine(javafx.scene.chart.XYChart.Series<Number, Number> line, String colour)` | Paints one line, now or as soon as it has a node. |
| 938 | 66 | `VBox historyReading(HistorySave h, String key, String colour)` | One line's reading: what it is now, and what it did. |
| 1013 | 18 | `String changeText(String unit, double first, double last)` | How far it moved, in terms the unit deserves. |
| 1033 | 31 | `VBox historyPresets()` | The questions a player actually has, each one a chart's worth of lines. |
| 1079 | 37 | `VBox historyPickerRows()` | The chips, one heading per group. |
| 1117 | 4 | `Trace traceFor(String key)` |  |
| 1134 | 14 | `static double[] trailingSum(double[] series, int window)` | A trailing total over the last `window` readings. |
| 1160 | 156 | `double[] historyValues(HistorySave h, String key)` | A series, aligned to the month axis, derived ones included. |
| 1317 | 5 | `double[] minus(double[] a, double[] b)` |  |
| 1324 | 36 | `String fmtUnit(String unit, double v)` | A value in the units it is actually kept in. |

### THE GOODS ROW (lines 1371-1427)

| line | len | member | says |
|---:|---:|---|---|
| 1387 | 40 | `HBox goodsRow(Good g)` | One good: what it is, what it costs here, and what happened to it. |

