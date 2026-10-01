# TimeChart.java - 1,343 lines · 67 methods · 17 constants · interface

`ham/citybuildersim/ui/TimeChart.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> A chart of months (0.7.23): lines over a window the player drags, wheels
> and ranges through, years on its axis, recessions named on their bands,
> the episodes on a lane under it, the player's decisions as flags, a
> crosshair that reads every line, and an overview of the whole history
> with the window on it - or, small, the same lines and years without the
> controls.
> 
> WHY. Jerus: "in teh graphs you should be able to pan the chart just like
> yahoo finance does if you get what i mean, and it actually have interactive
> graph, and like also the crisis labels and all". City History drew a
> JavaFX LineChart - a node per point, three chips for the window, a month
> number on the axis that nobody could read as a date, grey bands nothing
> named - and a LineChart has no pan, no wheel and one y-axis. This draws on
> a canvas: one pass a frame, every month of a four-thousand-month city
> without thinning it to keep the node count down, two axes of its own, and
> the bands, lanes and flags in the same coordinates as the lines.
> 
> NOTHING HERE IS ARITHMETIC ABOUT THE CITY. The window, the ticks, the
> scales, the lanes and the flags are ChartModel's, which ChartCheck holds
> to; the bands and episodes YearBook's; the decisions the DecisionLog's;
> each line's values the screen's, in its stored units, with the screen's
> own formatter to read them. This draws and listens.

**Uses:** [Palette](Palette.md) (88), [ChartModel](ChartModel.md) (57), [YearBook](YearBook.md) (32), [CityCalendar](CityCalendar.md) (16), [DecisionLog](DecisionLog.md) (3), [HistoryScreen](HistoryScreen.md) (2)

**Used by (3):** [HistoryScreen](HistoryScreen.md), [Pieces](Pieces.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 58 | · the dials |
| 105 | · what it is handed |
| 138 | · its nodes |
| 152 | · geometry |
| 157 | · interaction |
| 231 | WHAT IT DRAWS, HANDED OVER ON EVERY REBUILD |
| 338 | THE LEGEND AND THE RANGES |
| 390 | THE GEOMETRY |
| 430 | THE SCALES, FIT TO WHAT IS SHOWN |
| 511 | DRAWING |
| 883 | · the overview |
| 982 | WHAT IS UNDER THE POINTER, AND THE CARD |
| 1184 | LISTENING: THE DRAG, THE WHEEL, THE CLICK, THE OVERVIEW |
| 1316 | · small helpers |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `TimeChart.AXIS_W` | `64` | How wide a y-axis's labels are held. |
| 64 | `TimeChart.NO_AXIS_W` | `14` | The margin on a side with no axis. |
| 67 | `TimeChart.BAND_ROW` | `20` | The row above the plot that recessions' names sit in. |
| 70 | `TimeChart.TIME_ROW` | `20` | The row under the plot that the years sit in. |
| 73 | `TimeChart.EPISODE_ROW` | `18` | One row of the episode lane. |
| 76 | `TimeChart.FLAG_ROW` | `24` | The decision lane. |
| 79 | `TimeChart.OVERVIEW` | `52` | The overview strip under the lanes. |
| 82 | `TimeChart.HANDLE` | `7` | How close, in pixels, the pointer must be to the overview window's edge to take it. |
| 85 | `TimeChart.FLAG_GAP` | `16` | A flag closer than this many pixels to the first of the group before it is drawn in that group, as one flag with the count. |
| 88 | `TimeChart.RECESSION_SHADE` | `.10` | How strongly a recession is shaded: enough to see, not enough to read as a colour. |
| 91 | `TimeChart.DRAG_SLOP` | `3` | How far a press may wander and still be a click rather than a drag. |
| 94 | `TimeChart.SETTLE_MS` | `280` | How long the window must rest before the page under the chart is redrawn for it, in milliseconds. |
| 97 | `TimeChart.CARD_W` | `300` | The card's widest. |
| 100 | `TimeChart.NOTCH` | `40` | A wheel notch, in the pixels JavaFX reports it as. |
| 103 | `TimeChart.WHEEL_OWNER` | `"TimeChart.wheel"` | Which node owns the wheel: the window's page-scroll filter leaves a wheel over this alone (UserInterface). |
| 1155 | `TimeChart.CARD_DECISIONS` | `10` | At most this many decisions are listed on a flag's card; the rest are counted. |
| 1319 | `TimeChart.MEASURE` | `new javafx.scene.text.Text()` | One Text node, reused to measure a string's width in a font (textWidth()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 123 | `private final ChartModel window` |  |
| 124 | `private final Set<String> hidden` |  |
| 125 | `private final boolean main` |  |
| 127 | `private List<Integer> months` |  |
| 128 | `private List<Line> lines` |  |
| 129 | `private Axis left, right` |  |
| 130 | `private boolean squashed, log` |  |
| 131 | `private Stack stack` |  |
| 132 | `private List<YearBook.Band> bands` |  |
| 133 | `private List<YearBook.Episode> episodes` |  |
| 134 | `private int[] lanes` |  |
| 135 | `private List<ChartModel.Flag> flags` |  |
| 136 | `private String emptySays` |  |
| 140 | `private final Canvas plot` |  |
| 141 | `private final Canvas strip` |  |
| 142 | `private final Pane over` |  |
| 143 | `private final VBox card` |  |
| 144 | `private final FlowPane legend` |  |
| 145 | `private final HBox ranges` |  |
| 146 | `private final HBox lead` |  |
| 147 | `private final Label fullButton` |  |
| 148 | `private final Label spanSays` |  |
| 149 | `private final Label hint` |  |
| 150 | `private final HBox toolbar` |  |
| 154 | `private double width` |  |
| 155 | `private double plotLeft, plotRight, top` |  |
| 159 | `private double hoverX` |  |
| 160 | `private Object pinned` |  |
| 161 | `private double pinnedX` |  |
| 162 | `private double pressX` |  |
| 163 | `private double pressLo, pressHi, pressMonth` |  |
| 164 | `private boolean dragging` |  |
| 165 | `private int stripMode` |  |
| 166 | `private boolean full` |  |
| 167 | `private final List<Runnable> followers` |  |
| 168 | `private Runnable onSettled, onFullScreen` |  |
| 169 | `private final javafx.animation.PauseTransition settle` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 1288 | **type** `final class TimeChart extends VBox` | A chart of months (0.7.23): lines over a window the player drags, wheels and ranges through, years on its axis, recessions named on their bands, the episodes on a lane under it, the player's decisions as flags, a cros... |

### the dials (lines 58-104)

### what it is handed (lines 105-137)

| line | len | member | says |
|---:|---:|---|---|
| 113 | 2 | **type** `record Line(String key, String label, String colour, int side, double[] values, DoubleUnaryOperator toPlot,...` | One line: its key and name, its colour, the axis it reads against (0 left, 1 right), its values in their stored units aligned to the months, what turns a stored value into the axis's units, how a stored value is read ... |
| 117 | 1 | **type** `record Axis(java.util.function.BiFunction<Double, Double, String> tick, boolean fromZero)` | One value axis: what a gridline says, from the value and the step; and whether it starts at zero. |
| 120 | 2 | **type** `record Stack(double[][] layers, String[] names, String[] colours, DoubleUnaryOperator toPlot, DoubleFunctio...` | Layers stacked from zero under the first line (GDP in layers, 0.7.6): their values, names and colours. |

### its nodes (lines 138-151)

### geometry (lines 152-156)

### interaction (lines 157-230)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 53 | `TimeChart(ChartModel window, Set<String> hidden, boolean main)` |  |

### WHAT IT DRAWS, HANDED OVER ON EVERY REBUILD (lines 231-337)

| line | len | member | says |
|---:|---:|---|---|
| 240 | 25 | `void setData(List<Integer> months, List<Line> lines, Axis left, Axis right, boolean squashed, boolean log, Stack stack, List<Ye...` | Everything the chart draws. |
| 266 | 4 | `private Object sameBand(YearBook.Band b)` |  |
| 272 | 5 | `void setSize(double width, double plotHeight)` | The width the chart is drawn at, and the plot's height; the lanes and the overview take what they need. |
| 279 | 4 | `double heightBesidePlot()` | The height everything but the plot takes, for a caller fitting the chart to a space (full screen). |
| 285 | 1 | `void follow(TimeChart leader)` | Called with nothing when the window moves - a chart that shows the same window redraws. |
| 288 | 1 | `void onSettled(Runnable r)` | What runs once the window has rested: the screen's redraw, for the readings that follow the window. |
| 291 | 1 | `void onFullScreen(Runnable r)` | What the full-screen button does. |
| 294 | 1 | `boolean isDragging()` | True while a drag is in hand on a chart that is on screen: a month's redraw waits for the release. |
| 304 | 7 | `void letGo()` | Leaves the chart at rest (after the docs pass): no drag in hand, no overview edge held, no redraw waiting. |
| 313 | 1 | `HBox lead()` | The slot at the left of the toolbar: the title and the date, in full screen. |
| 316 | 21 | `void setFull(boolean full)` | Full screen or not: the button's words and icon. |

### THE LEGEND AND THE RANGES (lines 338-389)

| line | len | member | says |
|---:|---:|---|---|
| 342 | 24 | `private void buildLegend()` |  |
| 367 | 11 | `private void buildRanges()` |  |
| 380 | 9 | `private void paintRanges()` | The range button lit is the one the window still shows exactly; a pan or a wheel lights none. |

### THE GEOMETRY (lines 390-429)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 1 | `private int laneRows()` |  |
| 396 | 5 | `private boolean anyOnRight()` |  |
| 402 | 1 | `private double plotBottom()` |  |
| 403 | 1 | `private double episodesTop()` |  |
| 404 | 1 | `private double flagsTop()` |  |
| 405 | 1 | `private double canvasHeight()` |  |
| 407 | 16 | `private void layoutCanvases()` |  |
| 424 | 1 | `private double plotW()` |  |
| 426 | 1 | `private double xOf(double month)` |  |
| 428 | 1 | `private double monthAt(double x)` |  |

### THE SCALES, FIT TO WHAT IS SHOWN (lines 430-510)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 1 | `private boolean shown(Line l)` |  |
| 437 | 11 | `private double plotted(Line l, int i, double[] own)` | A line's value at index i in the axis's units, or NaN: through toPlot, the log, or its own low-to-high. |
| 450 | 5 | `private double[][] ownRanges()` | Each shown line's low and high in the window, for the squashed chart. |
| 457 | 36 | `private ChartModel.Scale[] scales(double[][] own)` | The two axes' scales for the window: {left, right}; null for a side with nothing on it. |
| 494 | 16 | `private double[] stackReach()` |  |

### DRAWING (lines 511-882)

| line | len | member | says |
|---:|---:|---|---|
| 516 | 66 | `void draw()` | Draws the chart, its overview and its card, and lights the range button the window still matches. |
| 583 | 27 | `private void drawBands(GraphicsContext g, double bottom)` |  |
| 611 | 35 | `private void drawStack(GraphicsContext g, ChartModel.Scale s, double bottom)` |  |
| 647 | 12 | `private void fillRun(GraphicsContext g, List<double[]> run)` |  |
| 660 | 5 | `private void clipToPlot(GraphicsContext g)` |  |
| 667 | 43 | `private boolean drawLines(GraphicsContext g, ChartModel.Scale[] scale, double[][] own, double bottom)` | The lines, each month a point, or each bucket of months one where they outnumber the pixels. |
| 711 | 25 | `private void drawAxes(GraphicsContext g, ChartModel.Scale[] scale, double bottom)` |  |
| 737 | 20 | `private void drawTime(GraphicsContext g, List<ChartModel.Tick> ticks, double bottom)` |  |
| 759 | 6 | `static String episodeColour(String kind)` | An episode's colour: a crisis or a depression is bad news, the rest are a watch - verdicts, which is what these are. |
| 766 | 33 | `private void drawEpisodes(GraphicsContext g)` |  |
| 801 | 7 | `static String flagColour(String kind)` | A flag's colour: the area its decision is about - money blue, people teal, building pink. |
| 809 | 3 | `private List<ChartModel.Cluster> clusters()` |  |
| 813 | 45 | `private void drawFlags(GraphicsContext g)` |  |
| 859 | 23 | `private void drawCrosshair(GraphicsContext g, ChartModel.Scale[] scale, double[][] own, double bottom)` |  |

### the overview (lines 883-981)

| line | len | member | says |
|---:|---:|---|---|
| 885 | 3 | `private double stripX(double month)` |  |
| 889 | 3 | `private double stripMonth(double x)` |  |
| 893 | 88 | `private void drawStrip()` |  |

### WHAT IS UNDER THE POINTER, AND THE CARD (lines 982-1183)

| line | len | member | says |
|---:|---:|---|---|
| 986 | 3 | `private boolean inPlot(double x, double y)` |  |
| 991 | 1 | `private Object hovered()` | The band, the episode or the flag under the pointer, or null. |
| 993 | 33 | `private Object itemAt(double x, double y)` |  |
| 1028 | 34 | `private void fillCard(ChartModel.Scale[] scale, double[][] own)` | Fills the card for what is pinned, or else what is under the pointer; hides it when there is nothing. |
| 1063 | 7 | `private static Label caption(String text, String colour)` |  |
| 1071 | 7 | `private static Label head(String text)` |  |
| 1079 | 19 | `private static HBox row(String colour, String name, String value)` |  |
| 1099 | 25 | `private void crosshairCard(YearBook.Band in)` |  |
| 1125 | 17 | `private void bandCard(YearBook.Band b)` |  |
| 1143 | 10 | `private void episodeCard(YearBook.Episode e)` |  |
| 1157 | 24 | `private void flagCard(ChartModel.Cluster c)` |  |
| 1182 | 1 | `private static String months(int n)` |  |

### LISTENING: THE DRAG, THE WHEEL, THE CLICK, THE OVERVIEW (lines 1184-1315)

| line | len | member | says |
|---:|---:|---|---|
| 1188 | 56 | `private void listen()` |  |
| 1245 | 13 | `private void wheel(ScrollEvent e)` |  |
| 1259 | 43 | `private void listenToStrip()` |  |
| 1304 | 5 | `private void moved(boolean settled)` | The window moved: this chart, the charts that follow it, and - once it rests - the page. |
| 1311 | 4 | `void unpin()` | Drops a pinned card: going into full screen and coming out of it, Esc's way out included (HistoryScreen.setChartFull()). |

### small helpers (lines 1316-1343)

| line | len | member | says |
|---:|---:|---|---|
| 1321 | 5 | `static double textWidth(String s, Font f)` |  |
| 1328 | 9 | `static String fit(String text, double room, Font f)` | The text, cut with an ellipsis to fit `room` pixels; empty when not even a few letters fit. |
| 1338 | 5 | `private static String trim(double v)` |  |

