# TimeChart.java - 1,626 lines · 90 methods · 20 constants · interface

`ham/citybuildersim/ui/TimeChart.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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
> to, and since 0.7.50 so are the copy of what it is handed that it draws
> from and the stack's runs and reach (setData(); ChartModel, WHAT A CHART
> DRAWS FROM); the bands and episodes YearBook's; the decisions the
> DecisionLog's; each line's values the screen's, in its stored units, with
> the screen's own formatter to read them. This draws and listens.

**Uses:** [Palette](Palette.md) (94), [ChartModel](ChartModel.md) (71), [YearBook](YearBook.md) (35), [CityCalendar](CityCalendar.md) (18), [Kept](Kept.md) (11), [DecisionLog](DecisionLog.md) (5), [HistoryScreen](HistoryScreen.md) (2), [UserInterface](UserInterface.md) (1), [GameLog](GameLog.md) (1)

**Used by (11):** [BankScreen](BankScreen.md), [ChartFitBench](ChartFitBench.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [GovernmentScreen](GovernmentScreen.md), [HistoryScreen](HistoryScreen.md), [KeptBench](KeptBench.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 62 | · the dials |
| 115 | · what it is handed |
| 185 | · its nodes |
| 208 | · geometry |
| 213 | · interaction |
| 314 | WHAT IT DRAWS, HANDED OVER ON EVERY REBUILD |
| 493 | THE LEGEND AND THE RANGES |
| 556 | THE GEOMETRY |
| 611 | THE SCALES, FIT TO WHAT IS SHOWN |
| 680 | DRAWING |
| 1147 | · the overview |
| 1246 | WHAT IS UNDER THE POINTER, AND THE CARD |
| 1467 | LISTENING: THE DRAG, THE WHEEL, THE CLICK, THE OVERVIEW |
| 1599 | · small helpers |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `TimeChart.AXIS_W` | `64` | How wide a y-axis's labels are held. |
| 68 | `TimeChart.NO_AXIS_W` | `14` | The margin on a side with no axis. |
| 71 | `TimeChart.BAND_ROW` | `20` | The row above the plot that recessions' names sit in. |
| 74 | `TimeChart.TIME_ROW` | `20` | The row under the plot that the years sit in. |
| 77 | `TimeChart.EPISODE_ROW` | `18` | One row of the episode lane. |
| 80 | `TimeChart.FLAG_ROW` | `24` | The decision lane. |
| 83 | `TimeChart.SMALL_FLAG_ROW` | `18` | ...on a small chart handed decisions (0.7.38): the same lane, smaller. |
| 86 | `TimeChart.OVERVIEW` | `52` | The overview strip under the lanes. |
| 89 | `TimeChart.HANDLE` | `7` | How close, in pixels, the pointer must be to the overview window's edge to take it. |
| 92 | `TimeChart.FLAG_GAP` | `16` | A flag closer than this many pixels to the first of the group before it is drawn in that group, as one flag with the count. |
| 95 | `TimeChart.RECESSION_SHADE` | `.10` | How strongly a recession is shaded: enough to see, not enough to read as a colour. |
| 98 | `TimeChart.DRAG_SLOP` | `3` | How far a press may wander and still be a click rather than a drag. |
| 101 | `TimeChart.SETTLE_MS` | `280` | How long the window must rest before the page under the chart is redrawn for it, in milliseconds. |
| 104 | `TimeChart.CARD_W` | `300` | The card's widest. |
| 107 | `TimeChart.NOTCH` | `40` | A wheel notch, in the pixels JavaFX reports it as. |
| 110 | `TimeChart.WHEEL_OWNER` | `"TimeChart.wheel"` | Which node owns the wheel: the window's page-scroll filter leaves a wheel over this alone (UserInterface). |
| 113 | `TimeChart.OVERHANG` | `2000` | How far above and below its own box the chart's clip reaches (0.7.40): it cuts the sides only. |
| 848 | `TimeChart.MARK_REACH` | `5` | How near a mark the pointer must be, in pixels, for the card to say it (0.7.45). |
| 1430 | `TimeChart.CARD_DECISIONS` | `10` | At most this many decisions are listed on a flag's card; the rest are counted. |
| 1602 | `TimeChart.MEASURE` | `new javafx.scene.text.Text()` | One Text node, reused to measure a string's width in a font (textWidth()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 160 | `private final ChartModel window` |  |
| 161 | `private final Set<String> hidden` |  |
| 162 | `private final boolean main` |  |
| 164 | `private List<Integer> months` |  |
| 172 | `private int yearly` | How many of the months, from the first, are folded years (0.7.55; HistorySave.yearlyPoints()): each drawn at its year's last month, as the history keeps it, and read in the crosshair as its year. |
| 173 | `private List<Line> lines` |  |
| 174 | `private Axis left, right` |  |
| 175 | `private boolean squashed, log` |  |
| 176 | `private Stack stack` |  |
| 177 | `private List<YearBook.Band> bands` |  |
| 178 | `private List<YearBook.Episode> episodes` |  |
| 179 | `private int[] lanes` |  |
| 180 | `private List<ChartModel.Flag> flags` |  |
| 182 | `private List<ChartModel.Mark> marks` | What the model did on its own that a line answers to (0.7.45: the basket's links), drawn over the plot on the big chart. |
| 183 | `private String emptySays` |  |
| 188 | `final Canvas plot` | Not private: KeptBench fires the pointer's moves on it (KeptCheck's crosshair). |
| 189 | `private final Canvas strip` |  |
| 190 | `private final Pane over` |  |
| 191 | `private final VBox card` |  |
| 193 | `final FlowPane legend` | Not private: KeptBench puts the kept chips back after a page is built new. |
| 194 | `final HBox ranges` |  |
| 195 | `private final HBox lead` |  |
| 196 | `private final Label fullButton` |  |
| 197 | `private final Label spanSays` |  |
| 198 | `private final Label hint` |  |
| 199 | `private final HBox toolbar` |  |
| 206 | `Kept.Pool chipPool` | The legend's chips and the range buttons, kept from one setData() to the next (study UP4, KP1): a chip under the pointer is not made again every month, so it keeps its hover and its tooltip. |
| 210 | `private double width` |  |
| 211 | `private double plotLeft, plotRight, top` |  |
| 215 | `private double hoverX` |  |
| 216 | `private Object pinned` |  |
| 217 | `private double pinnedX` |  |
| 218 | `private double pressX` |  |
| 219 | `private double pressLo, pressHi, pressMonth` |  |
| 220 | `private boolean dragging` |  |
| 221 | `private int stripMode` |  |
| 222 | `private boolean full` |  |
| 223 | `private final List<Runnable> followers` |  |
| 224 | `private Runnable onSettled, onFullScreen` |  |
| 225 | `private final javafx.animation.PauseTransition settle` |  |
| 707 | `private boolean faultLogged` | Whether this chart has written a fault to the log: once a chart, so a fault on every frame is one entry (0.7.50). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 1567 | **type** `final class TimeChart extends VBox` | A chart of months (0.7.23): lines over a window the player drags, wheels and ranges through, years on its axis, recessions named on their bands, the episodes on a lane under it, the player's decisions as flags, a cros... |

### the dials (lines 62-114)

### what it is handed (lines 115-184)

| line | len | member | says |
|---:|---:|---|---|
| 123 | 16 | **type** `record Line(String key, String label, String colour, int side, double[] values, DoubleUnaryOperator toPlot,...` | One line: its key and name, its colour, the axis it reads against (0 left, 1 right), its values in their stored units aligned to the months, what turns a stored value into the axis's units, how a stored value is read ... |
| 126 | 4 | `Line(String key, String label, String colour, int side, double[] values, DoubleUnaryOperator toPlot, DoubleFunction<String> rea...` _(in TimeChart.Line)_ | ...drawn solid, as every line was until 0.7.45. |
| 132 | 1 | `Line asDashed()` _(in TimeChart.Line)_ | The same line drawn dashed (0.7.45): what people expect beside what happened. |
| 135 | 3 | `Line alignedTo(int n)` _(in TimeChart.Line)_ | The same line with its own copy of its values, exactly n months long (0.7.50; ChartModel.aligned()). |
| 141 | 1 | **type** `record Axis(java.util.function.BiFunction<Double, Double, String> tick, boolean fromZero)` | One value axis: what a gridline says, from the value and the step; and whether it starts at zero. |
| 144 | 15 | **type** `record Stack(double[][] layers, String[] names, String[] colours, DoubleUnaryOperator toPlot, DoubleFunctio...` | Layers stacked from zero under the first line (GDP in layers, 0.7.6): their values, names and colours. |
| 147 | 5 | `Stack alignedTo(int n)` _(in TimeChart.Stack)_ | The same stack with its own copy of every layer, exactly n months long (0.7.50; ChartModel.aligned()). |
| 154 | 1 | `String name(int p)` _(in TimeChart.Stack)_ | Layer p's name, or nothing for a layer handed no name. |
| 157 | 1 | `String colour(int p)` _(in TimeChart.Stack)_ | Layer p's colour, or the muted ink for a layer handed none. |

### its nodes (lines 185-207)

### geometry (lines 208-212)

### interaction (lines 213-313)

| line | len | member | says |
|---:|---:|---|---|
| 234 | 79 | `TimeChart(ChartModel window, Set<String> hidden, boolean main)` | one draws the decision lane alone, and only when it is handed flags) |

### WHAT IT DRAWS, HANDED OVER ON EVERY REBUILD (lines 314-492)

| line | len | member | says |
|---:|---:|---|---|
| 330 | 5 | `void setData(List<Integer> months, List<Line> lines, Axis left, Axis right, boolean squashed, boolean log, Stack stack, List<Ye...` | Everything the chart draws. |
| 342 | 29 | `void setData(List<Integer> months, List<Line> lines, Axis left, Axis right, boolean squashed, boolean log, Stack stack, List<Ye...` | ...and the marks over the plot (0.7.45): the basket's links, which City History hands the big chart while it draws an index line. |
| 373 | 3 | `private static<T> List<T> fixed(List<T> list)` | A list as the chart keeps it: its own copy, so the caller's can change afterwards. |
| 377 | 4 | `private Object sameBand(YearBook.Band b)` |  |
| 383 | 5 | `void setSize(double width, double plotHeight)` | The width the chart is drawn at, and the plot's height; the lanes and the overview take what they need. |
| 403 | 5 | `double heightBesidePlot()` | The height everything but the plot takes, for a caller fitting the chart to a space (full screen): the chart's own preferred height at the width it is drawn at - the toolbar as its legend wraps there, the lanes, the o... |
| 414 | 8 | `void fitTo(double width, double height)` | The chart drawn this wide and, all of it, this high (FW10): the width first, which sets how the legend wraps; then what stands beside the plot, measured at that wrap; then the plot whatever the height leaves - and one... |
| 424 | 1 | `double plotHeight()` | The plot's height as the chart was last given it (setSize), for the harness that checks a fit. |
| 427 | 1 | `boolean hovering()` | Whether the pointer's crosshair is on the plot (what KeptCheck reads after a month is redrawn over the chart). |
| 430 | 5 | `int legendRows()` | How many rows the laid-out legend holds: the distinct layoutY of its visible chips (0 with none). |
| 437 | 1 | `void follow(TimeChart leader)` | Called with nothing when the window moves - a chart that shows the same window redraws. |
| 440 | 1 | `void onSettled(Runnable r)` | What runs once the window has rested: the screen's redraw, for the readings that follow the window. |
| 443 | 1 | `void onFullScreen(Runnable r)` | What the full-screen button does. |
| 446 | 1 | `void withoutFullScreen()` | A big chart on another tab (0.7.35: Trade's rate over time) has no full-screen button: only City History lays a chart out full screen. |
| 449 | 1 | `boolean isDragging()` | True while a drag is in hand on a chart that is on screen: a month's redraw waits for the release. |
| 459 | 7 | `void letGo()` | Leaves the chart at rest (after the docs pass): no drag in hand, no overview edge held, no redraw waiting. |
| 468 | 1 | `HBox lead()` | The slot at the left of the toolbar: the title and the date, in full screen. |
| 471 | 21 | `void setFull(boolean full)` | Full screen or not: the button's words and icon. |

### THE LEGEND AND THE RANGES (lines 493-555)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 30 | `private void buildLegend()` | The legend, a chip a line (KP1: kept). |
| 532 | 12 | `private void buildRanges()` |  |
| 546 | 9 | `private void paintRanges()` | The range button lit is the one the window still shows exactly; a pan or a wheel lights none. |

### THE GEOMETRY (lines 556-610)

| line | len | member | says |
|---:|---:|---|---|
| 560 | 1 | `private int laneRows()` |  |
| 562 | 5 | `private boolean anyOnRight()` |  |
| 568 | 1 | `private double plotBottom()` |  |
| 569 | 1 | `private double episodesTop()` |  |
| 570 | 1 | `private double flagsTop()` |  |
| 571 | 1 | `private double canvasHeight()` |  |
| 578 | 1 | `private boolean flagLane()` | Whether the decision lane is drawn: always on the big chart, and on a small one handed a decision to show (0.7.38: the Bank's rates and Finances' debt and rate were handed theirs and drew none). |
| 581 | 1 | `private double flagRow()` | The lane's height: the big chart's, or a small chart's. |
| 583 | 4 | `private void layoutCanvases()` |  |
| 589 | 15 | `private void geometry()` | The canvases' sizes and the legend's wrap length for the width and the plot's height, without the draw (fitTo() measures between the two). |
| 605 | 1 | `private double plotW()` |  |
| 607 | 1 | `private double xOf(double month)` |  |
| 609 | 1 | `private double monthAt(double x)` |  |

### THE SCALES, FIT TO WHAT IS SHOWN (lines 611-679)

| line | len | member | says |
|---:|---:|---|---|
| 615 | 1 | `private boolean shown(Line l)` |  |
| 618 | 11 | `private double plotted(Line l, int i, double[] own)` | A line's value at index i in the axis's units, or NaN: through toPlot, the log, or its own low-to-high. |
| 631 | 5 | `private double[][] ownRanges()` | Each shown line's low and high in the window, for the squashed chart. |
| 638 | 36 | `private ChartModel.Scale[] scales(double[][] own)` | The two axes' scales for the window: {left, right}; null for a side with nothing on it. |
| 676 | 3 | `private double[] stackReach()` | What the stack's first three layers reach in the window, summed from zero (ChartModel.stackReach()). |

### DRAWING (lines 680-1146)

| line | len | member | says |
|---:|---:|---|---|
| 698 | 7 | `void draw()` | Draws the chart, its overview and its card, and lights the range button the window still matches. |
| 710 | 5 | `private void fault(String what, RuntimeException e)` | A fault in this chart, written to the log with its stack trace the first time only. |
| 721 | 15 | `private void skipFrame(RuntimeException e)` | A frame that failed, skipped: the state a drawing step saved put back (each saves one at a time, and a restore with nothing saved does nothing), the canvases cleared, the card hidden, and the fault logged. |
| 742 | 9 | `private<T extends javafx.event.Event> javafx.event.EventHandler<T> guarded(javafx.event.EventHandler<T> h)` | A handler that cannot throw into JavaFX's event dispatch (0.7.50): what it throws is the chart's fault, logged once, and the event is dropped. |
| 753 | 65 | `private void paint()` | One frame: draw()'s, which catches what it throws. |
| 819 | 27 | `private void drawBands(GraphicsContext g, double bottom)` |  |
| 851 | 17 | `private void drawMarks(GraphicsContext g, double bottom)` | The marks in view: a dashed hairline in the muted ink through the plot, and its word at the plot's top (0.7.45). |
| 870 | 8 | `private ChartModel.Mark markAt(double x)` | The mark within MARK_REACH of the pointer, or null. |
| 879 | 19 | `private void drawStack(GraphicsContext g, ChartModel.Scale s, double bottom)` |  |
| 899 | 12 | `private void fillRun(GraphicsContext g, List<double[]> run)` |  |
| 912 | 5 | `private void clipToPlot(GraphicsContext g)` |  |
| 919 | 45 | `private boolean drawLines(GraphicsContext g, ChartModel.Scale[] scale, double[][] own, double bottom)` | The lines, each month a point, or each bucket of months one where they outnumber the pixels. |
| 965 | 25 | `private void drawAxes(GraphicsContext g, ChartModel.Scale[] scale, double bottom)` |  |
| 991 | 20 | `private void drawTime(GraphicsContext g, List<ChartModel.Tick> ticks, double bottom)` |  |
| 1018 | 3 | `public static String episodeColour(String kind)` | An episode's colour: a crisis or a depression is bad news, the rest are a watch - verdicts, which is what these are. |
| 1022 | 33 | `private void drawEpisodes(GraphicsContext g)` |  |
| 1062 | 8 | `static String flagColour(String kind)` | A flag's colour: the area its decision is about - money blue, people teal, building pink - and since 0.7.109 (batch Z2) zoning's in the ground's sand (Palette.ORE, the land office's colour for what the ground holds), ... |
| 1071 | 3 | `private List<ChartModel.Cluster> clusters()` |  |
| 1075 | 47 | `private void drawFlags(GraphicsContext g)` |  |
| 1123 | 23 | `private void drawCrosshair(GraphicsContext g, ChartModel.Scale[] scale, double[][] own, double bottom)` |  |

### the overview (lines 1147-1245)

| line | len | member | says |
|---:|---:|---|---|
| 1149 | 3 | `private double stripX(double month)` |  |
| 1153 | 3 | `private double stripMonth(double x)` |  |
| 1157 | 88 | `private void drawStrip()` |  |

### WHAT IS UNDER THE POINTER, AND THE CARD (lines 1246-1466)

| line | len | member | says |
|---:|---:|---|---|
| 1250 | 3 | `private boolean inPlot(double x, double y)` |  |
| 1255 | 1 | `private Object hovered()` | The band, the episode or the flag under the pointer, or null. |
| 1257 | 33 | `private Object itemAt(double x, double y)` |  |
| 1292 | 34 | `private void fillCard(ChartModel.Scale[] scale, double[][] own)` | Fills the card for what is pinned, or else what is under the pointer; hides it when there is nothing. |
| 1327 | 7 | `private static Label caption(String text, String colour)` |  |
| 1335 | 7 | `private static Label head(String text)` |  |
| 1343 | 19 | `private static HBox row(String colour, String name, String value)` |  |
| 1364 | 1 | `void setYearly(int years)` | The folded years at the front of the months this chart is handed (0.7.55; HistorySave.yearlyPoints()). |
| 1366 | 33 | `private void crosshairCard(YearBook.Band in)` |  |
| 1400 | 17 | `private void bandCard(YearBook.Band b)` |  |
| 1418 | 10 | `private void episodeCard(YearBook.Episode e)` |  |
| 1432 | 32 | `private void flagCard(ChartModel.Cluster c)` |  |
| 1465 | 1 | `private static String months(int n)` |  |

### LISTENING: THE DRAG, THE WHEEL, THE CLICK, THE OVERVIEW (lines 1467-1598)

| line | len | member | says |
|---:|---:|---|---|
| 1471 | 56 | `private void listen()` |  |
| 1528 | 13 | `private void wheel(ScrollEvent e)` |  |
| 1542 | 43 | `private void listenToStrip()` |  |
| 1587 | 5 | `private void moved(boolean settled)` | The window moved: this chart, the charts that follow it, and - once it rests - the page. |
| 1594 | 4 | `void unpin()` | Drops a pinned card: going into full screen and coming out of it, Esc's way out included (HistoryScreen.setChartFull()). |

### small helpers (lines 1599-1626)

| line | len | member | says |
|---:|---:|---|---|
| 1604 | 5 | `static double textWidth(String s, Font f)` |  |
| 1611 | 9 | `static String fit(String text, double room, Font f)` | The text, cut with an ellipsis to fit `room` pixels; empty when not even a few letters fit. |
| 1621 | 5 | `private static String trim(double v)` |  |

