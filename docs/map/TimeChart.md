# TimeChart.java - 1,370 lines · 70 methods · 18 constants · interface

`ham/citybuildersim/ui/TimeChart.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> A chart of months (0.7.23): lines over a window the player drags, wheels
> and ranges through, years on its axis, recessions named on their bands,
> the episodes on a lane under it, the player's decisions as flags, a
> crosshair that reads every line, and an overview of the whole history
> with the window on it - or, small, the same lines and years without the
> controls, and since 0.7.38 the decisions it is handed on a smaller lane.
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

**Uses:** [Palette](Palette.md) (88), [ChartModel](ChartModel.md) (58), [YearBook](YearBook.md) (33), [CityCalendar](CityCalendar.md) (16), [DecisionLog](DecisionLog.md) (4), [HistoryScreen](HistoryScreen.md) (2)

**Used by (9):** [BankScreen](BankScreen.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 58 | · the dials |
| 108 | · what it is handed |
| 141 | · its nodes |
| 155 | · geometry |
| 160 | · interaction |
| 235 | WHAT IT DRAWS, HANDED OVER ON EVERY REBUILD |
| 345 | THE LEGEND AND THE RANGES |
| 397 | THE GEOMETRY |
| 447 | THE SCALES, FIT TO WHAT IS SHOWN |
| 528 | DRAWING |
| 902 | · the overview |
| 1001 | WHAT IS UNDER THE POINTER, AND THE CARD |
| 1211 | LISTENING: THE DRAG, THE WHEEL, THE CLICK, THE OVERVIEW |
| 1343 | · small helpers |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `TimeChart.AXIS_W` | `64` | How wide a y-axis's labels are held. |
| 64 | `TimeChart.NO_AXIS_W` | `14` | The margin on a side with no axis. |
| 67 | `TimeChart.BAND_ROW` | `20` | The row above the plot that recessions' names sit in. |
| 70 | `TimeChart.TIME_ROW` | `20` | The row under the plot that the years sit in. |
| 73 | `TimeChart.EPISODE_ROW` | `18` | One row of the episode lane. |
| 76 | `TimeChart.FLAG_ROW` | `24` | The decision lane. |
| 79 | `TimeChart.SMALL_FLAG_ROW` | `18` | ...on a small chart handed decisions (0.7.38): the same lane, smaller. |
| 82 | `TimeChart.OVERVIEW` | `52` | The overview strip under the lanes. |
| 85 | `TimeChart.HANDLE` | `7` | How close, in pixels, the pointer must be to the overview window's edge to take it. |
| 88 | `TimeChart.FLAG_GAP` | `16` | A flag closer than this many pixels to the first of the group before it is drawn in that group, as one flag with the count. |
| 91 | `TimeChart.RECESSION_SHADE` | `.10` | How strongly a recession is shaded: enough to see, not enough to read as a colour. |
| 94 | `TimeChart.DRAG_SLOP` | `3` | How far a press may wander and still be a click rather than a drag. |
| 97 | `TimeChart.SETTLE_MS` | `280` | How long the window must rest before the page under the chart is redrawn for it, in milliseconds. |
| 100 | `TimeChart.CARD_W` | `300` | The card's widest. |
| 103 | `TimeChart.NOTCH` | `40` | A wheel notch, in the pixels JavaFX reports it as. |
| 106 | `TimeChart.WHEEL_OWNER` | `"TimeChart.wheel"` | Which node owns the wheel: the window's page-scroll filter leaves a wheel over this alone (UserInterface). |
| 1174 | `TimeChart.CARD_DECISIONS` | `10` | At most this many decisions are listed on a flag's card; the rest are counted. |
| 1346 | `TimeChart.MEASURE` | `new javafx.scene.text.Text()` | One Text node, reused to measure a string's width in a font (textWidth()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 126 | `private final ChartModel window` |  |
| 127 | `private final Set<String> hidden` |  |
| 128 | `private final boolean main` |  |
| 130 | `private List<Integer> months` |  |
| 131 | `private List<Line> lines` |  |
| 132 | `private Axis left, right` |  |
| 133 | `private boolean squashed, log` |  |
| 134 | `private Stack stack` |  |
| 135 | `private List<YearBook.Band> bands` |  |
| 136 | `private List<YearBook.Episode> episodes` |  |
| 137 | `private int[] lanes` |  |
| 138 | `private List<ChartModel.Flag> flags` |  |
| 139 | `private String emptySays` |  |
| 143 | `private final Canvas plot` |  |
| 144 | `private final Canvas strip` |  |
| 145 | `private final Pane over` |  |
| 146 | `private final VBox card` |  |
| 147 | `private final FlowPane legend` |  |
| 148 | `private final HBox ranges` |  |
| 149 | `private final HBox lead` |  |
| 150 | `private final Label fullButton` |  |
| 151 | `private final Label spanSays` |  |
| 152 | `private final Label hint` |  |
| 153 | `private final HBox toolbar` |  |
| 157 | `private double width` |  |
| 158 | `private double plotLeft, plotRight, top` |  |
| 162 | `private double hoverX` |  |
| 163 | `private Object pinned` |  |
| 164 | `private double pinnedX` |  |
| 165 | `private double pressX` |  |
| 166 | `private double pressLo, pressHi, pressMonth` |  |
| 167 | `private boolean dragging` |  |
| 168 | `private int stripMode` |  |
| 169 | `private boolean full` |  |
| 170 | `private final List<Runnable> followers` |  |
| 171 | `private Runnable onSettled, onFullScreen` |  |
| 172 | `private final javafx.animation.PauseTransition settle` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 1315 | **type** `final class TimeChart extends VBox` | A chart of months (0.7.23): lines over a window the player drags, wheels and ranges through, years on its axis, recessions named on their bands, the episodes on a lane under it, the player's decisions as flags, a cros... |

### the dials (lines 58-107)

### what it is handed (lines 108-140)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 2 | **type** `record Line(String key, String label, String colour, int side, double[] values, DoubleUnaryOperator toPlot,...` | One line: its key and name, its colour, the axis it reads against (0 left, 1 right), its values in their stored units aligned to the months, what turns a stored value into the axis's units, how a stored value is read ... |
| 120 | 1 | **type** `record Axis(java.util.function.BiFunction<Double, Double, String> tick, boolean fromZero)` | One value axis: what a gridline says, from the value and the step; and whether it starts at zero. |
| 123 | 2 | **type** `record Stack(double[][] layers, String[] names, String[] colours, DoubleUnaryOperator toPlot, DoubleFunctio...` | Layers stacked from zero under the first line (GDP in layers, 0.7.6): their values, names and colours. |

### its nodes (lines 141-154)

### geometry (lines 155-159)

### interaction (lines 160-234)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 53 | `TimeChart(ChartModel window, Set<String> hidden, boolean main)` | one draws the decision lane alone, and only when it is handed flags) |

### WHAT IT DRAWS, HANDED OVER ON EVERY REBUILD (lines 235-344)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 25 | `void setData(List<Integer> months, List<Line> lines, Axis left, Axis right, boolean squashed, boolean log, Stack stack, List<Ye...` | Everything the chart draws. |
| 270 | 4 | `private Object sameBand(YearBook.Band b)` |  |
| 276 | 5 | `void setSize(double width, double plotHeight)` | The width the chart is drawn at, and the plot's height; the lanes and the overview take what they need. |
| 283 | 4 | `double heightBesidePlot()` | The height everything but the plot takes, for a caller fitting the chart to a space (full screen). |
| 289 | 1 | `void follow(TimeChart leader)` | Called with nothing when the window moves - a chart that shows the same window redraws. |
| 292 | 1 | `void onSettled(Runnable r)` | What runs once the window has rested: the screen's redraw, for the readings that follow the window. |
| 295 | 1 | `void onFullScreen(Runnable r)` | What the full-screen button does. |
| 298 | 1 | `void withoutFullScreen()` | A big chart on another tab (0.7.35: Trade's rate over time) has no full-screen button: only City History lays a chart out full screen. |
| 301 | 1 | `boolean isDragging()` | True while a drag is in hand on a chart that is on screen: a month's redraw waits for the release. |
| 311 | 7 | `void letGo()` | Leaves the chart at rest (after the docs pass): no drag in hand, no overview edge held, no redraw waiting. |
| 320 | 1 | `HBox lead()` | The slot at the left of the toolbar: the title and the date, in full screen. |
| 323 | 21 | `void setFull(boolean full)` | Full screen or not: the button's words and icon. |

### THE LEGEND AND THE RANGES (lines 345-396)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 24 | `private void buildLegend()` |  |
| 374 | 11 | `private void buildRanges()` |  |
| 387 | 9 | `private void paintRanges()` | The range button lit is the one the window still shows exactly; a pan or a wheel lights none. |

### THE GEOMETRY (lines 397-446)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 1 | `private int laneRows()` |  |
| 403 | 5 | `private boolean anyOnRight()` |  |
| 409 | 1 | `private double plotBottom()` |  |
| 410 | 1 | `private double episodesTop()` |  |
| 411 | 1 | `private double flagsTop()` |  |
| 412 | 1 | `private double canvasHeight()` |  |
| 419 | 1 | `private boolean flagLane()` | Whether the decision lane is drawn: always on the big chart, and on a small one handed a decision to show (0.7.38: the Bank's rates and Finances' debt and rate were handed theirs and drew none). |
| 422 | 1 | `private double flagRow()` | The lane's height: the big chart's, or a small chart's. |
| 424 | 16 | `private void layoutCanvases()` |  |
| 441 | 1 | `private double plotW()` |  |
| 443 | 1 | `private double xOf(double month)` |  |
| 445 | 1 | `private double monthAt(double x)` |  |

### THE SCALES, FIT TO WHAT IS SHOWN (lines 447-527)

| line | len | member | says |
|---:|---:|---|---|
| 451 | 1 | `private boolean shown(Line l)` |  |
| 454 | 11 | `private double plotted(Line l, int i, double[] own)` | A line's value at index i in the axis's units, or NaN: through toPlot, the log, or its own low-to-high. |
| 467 | 5 | `private double[][] ownRanges()` | Each shown line's low and high in the window, for the squashed chart. |
| 474 | 36 | `private ChartModel.Scale[] scales(double[][] own)` | The two axes' scales for the window: {left, right}; null for a side with nothing on it. |
| 511 | 16 | `private double[] stackReach()` |  |

### DRAWING (lines 528-901)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 64 | `void draw()` | Draws the chart, its overview and its card, and lights the range button the window still matches. |
| 598 | 27 | `private void drawBands(GraphicsContext g, double bottom)` |  |
| 626 | 35 | `private void drawStack(GraphicsContext g, ChartModel.Scale s, double bottom)` |  |
| 662 | 12 | `private void fillRun(GraphicsContext g, List<double[]> run)` |  |
| 675 | 5 | `private void clipToPlot(GraphicsContext g)` |  |
| 682 | 43 | `private boolean drawLines(GraphicsContext g, ChartModel.Scale[] scale, double[][] own, double bottom)` | The lines, each month a point, or each bucket of months one where they outnumber the pixels. |
| 726 | 25 | `private void drawAxes(GraphicsContext g, ChartModel.Scale[] scale, double bottom)` |  |
| 752 | 20 | `private void drawTime(GraphicsContext g, List<ChartModel.Tick> ticks, double bottom)` |  |
| 779 | 3 | `public static String episodeColour(String kind)` | An episode's colour: a crisis or a depression is bad news, the rest are a watch - verdicts, which is what these are. |
| 783 | 33 | `private void drawEpisodes(GraphicsContext g)` |  |
| 818 | 7 | `static String flagColour(String kind)` | A flag's colour: the area its decision is about - money blue, people teal, building pink. |
| 826 | 3 | `private List<ChartModel.Cluster> clusters()` |  |
| 830 | 47 | `private void drawFlags(GraphicsContext g)` |  |
| 878 | 23 | `private void drawCrosshair(GraphicsContext g, ChartModel.Scale[] scale, double[][] own, double bottom)` |  |

### the overview (lines 902-1000)

| line | len | member | says |
|---:|---:|---|---|
| 904 | 3 | `private double stripX(double month)` |  |
| 908 | 3 | `private double stripMonth(double x)` |  |
| 912 | 88 | `private void drawStrip()` |  |

### WHAT IS UNDER THE POINTER, AND THE CARD (lines 1001-1210)

| line | len | member | says |
|---:|---:|---|---|
| 1005 | 3 | `private boolean inPlot(double x, double y)` |  |
| 1010 | 1 | `private Object hovered()` | The band, the episode or the flag under the pointer, or null. |
| 1012 | 33 | `private Object itemAt(double x, double y)` |  |
| 1047 | 34 | `private void fillCard(ChartModel.Scale[] scale, double[][] own)` | Fills the card for what is pinned, or else what is under the pointer; hides it when there is nothing. |
| 1082 | 7 | `private static Label caption(String text, String colour)` |  |
| 1090 | 7 | `private static Label head(String text)` |  |
| 1098 | 19 | `private static HBox row(String colour, String name, String value)` |  |
| 1118 | 25 | `private void crosshairCard(YearBook.Band in)` |  |
| 1144 | 17 | `private void bandCard(YearBook.Band b)` |  |
| 1162 | 10 | `private void episodeCard(YearBook.Episode e)` |  |
| 1176 | 32 | `private void flagCard(ChartModel.Cluster c)` |  |
| 1209 | 1 | `private static String months(int n)` |  |

### LISTENING: THE DRAG, THE WHEEL, THE CLICK, THE OVERVIEW (lines 1211-1342)

| line | len | member | says |
|---:|---:|---|---|
| 1215 | 56 | `private void listen()` |  |
| 1272 | 13 | `private void wheel(ScrollEvent e)` |  |
| 1286 | 43 | `private void listenToStrip()` |  |
| 1331 | 5 | `private void moved(boolean settled)` | The window moved: this chart, the charts that follow it, and - once it rests - the page. |
| 1338 | 4 | `void unpin()` | Drops a pinned card: going into full screen and coming out of it, Esc's way out included (HistoryScreen.setChartFull()). |

### small helpers (lines 1343-1370)

| line | len | member | says |
|---:|---:|---|---|
| 1348 | 5 | `static double textWidth(String s, Font f)` |  |
| 1355 | 9 | `static String fit(String text, double room, Font f)` | The text, cut with an ellipsis to fit `room` pixels; empty when not even a few letters fit. |
| 1365 | 5 | `private static String trim(double v)` |  |

