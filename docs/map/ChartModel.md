# ChartModel.java - 715 lines · 49 methods · 11 constants · model

`ham/citybuildersim/ChartModel.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> What a time chart shows, as numbers: the window of months it looks at and
> how a drag, a wheel, a range button and the overview move it; the ticks on
> its two axes; and the bands, the episodes and the decisions it lays over
> the lines (0.7.23). Since 0.7.50 also what a chart draws from: the copy
> of the months and every series it keeps from the moment it is handed
> them, and the stack's runs and reach (WHAT A CHART DRAWS FROM).
> 
> WHY THIS EXISTS
> 
> Jerus, on the play-through: "in teh graphs you should be able to pan the
> chart just like yahoo finance does ... and like also the crisis labels and
> all, dont you think that it should be more clear". The chart he was looking
> at was a JavaFX LineChart on a window picked from three chips, with no year
> on its axis and grey bands nothing named. Pan and zoom are arithmetic on a
> window of months; year ticks are arithmetic on a calendar; which band is
> which and where a flag goes are lists. None of it needs a toolkit, and all
> of it can be wrong in a way a player only notices years into a city - a
> window that drifts past the newest month, an axis that loses its years at
> one zoom, a band named after the wrong recession. So it lives here, where
> ChartCheck can hold it to its arithmetic without a screen, and the
> interface (ui.TimeChart) draws what it says.
> 
> NOTHING HERE DECIDES WHAT HAPPENED. The bands are YearBook.recessions(),
> the spans YearBook.episodes() and the flags the DecisionLog's entries, as
> they are; this class only says where on the chart each one goes.

**Uses:** [DecisionLog](DecisionLog.md) (14), [CityCalendar](CityCalendar.md) (9), [PriceIndex](PriceIndex.md) (9), [YearBook](YearBook.md) (6), [HistorySave](HistorySave.md) (4)

**Used by (15):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [ChartCheck](ChartCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [TimeChart](TimeChart.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 72 | THE WINDOW |
| 221 | FROM MONTHS TO PIXELS, AND BACK |
| 266 | WHAT A CHART DRAWS FROM: ONE SNAPSHOT, FIXED WHEN IT IS HANDED OVER |
| 378 | THE TIME AXIS |
| 440 | A VALUE AXIS |
| 503 | WHAT IS LAID OVER THE LINES |
| 646 | · the basket's links (0.7.45) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `ChartModel.MIN_SPAN` | `6` | The fewest months the main chart can be zoomed down to: half a year, seven points. |
| 43 | `ChartModel.RANGES` | `{ 12, 60, 120, 600 }` | The range buttons, in months: one, five, ten and fifty years; "All" is the whole history. |
| 46 | `ChartModel.RANGE_NAMES` | `{ "1Y", "5Y", "10Y", "50Y", "All" }` | What the range buttons say, in RANGES' order, then the whole history's. |
| 49 | `ChartModel.ALL` | `Integer.MAX_VALUE` | The range that means the whole history. |
| 52 | `ChartModel.DEFAULT_RANGE` | `120` | The range a chart opens on: ten years, what City History drew before it had buttons. |
| 55 | `ChartModel.ZOOM_STEP` | `0.85` | What one notch of the wheel leaves in view, zooming in; zooming out is its inverse. |
| 58 | `ChartModel.YEAR_LABEL_PX` | `46` | The least room, in pixels, between two year labels: a "2141" and a gap. |
| 61 | `ChartModel.MONTH_LABEL_PX` | `36` | The least room between two month labels: a "Mar" and a gap. |
| 64 | `ChartModel.YEAR_STEPS` | `{ 1, 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000 }` | The year steps the axis may label, smallest first: every year, every second, every fifth... |
| 67 | `ChartModel.MONTH_STEPS` | `{ 1, 2, 3 }` | The month steps a zoomed-in axis may label between its years: monthly, two-monthly, quarterly - no coarser, or ten years across a wide screen would be half-years. |
| 70 | `ChartModel.AXIS_PAD` | `.06` | Headroom a value axis leaves above and below what is drawn, as a share of the range: a line at its extreme is not drawn along the frame. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `private double first, last` |  |
| 84 | `private double lo, hi` |  |
| 85 | `private int range` |  |
| 86 | `private boolean placed` |  |
| 96 | `private boolean touched` | Whether the player has moved the window by hand - a drag, a wheel, the overview - since the last range button or double-click. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 679 | **type** `public final class ChartModel` | What a time chart shows, as numbers: the window of months it looks at and how a drag, a wheel, a range button and the overview move it; the ticks on its two axes; and the bands, the episodes and the decisions it lays ... |

### THE WINDOW (lines 72-220)

| line | len | member | says |
|---:|---:|---|---|
| 106 | 24 | `public void setData(int firstMonth, int lastMonth)` | The months the data covers, first to last - every rebuild hands it the history's axis. |
| 132 | 12 | `public void showRange(int months)` | A range button: the last `months` months, ending at the newest; ALL is everything. |
| 146 | 1 | `public void reset()` | The double-click: back to the last range picked, ending at the newest month. |
| 149 | 7 | `public void pan(double months)` | A drag: the window moved by this many months, stopped at either end of the data. |
| 161 | 13 | `public void zoom(double factor, double anchorMonth)` | A wheel notch: the window `factor` times as wide, about the month under the pointer, which stays under it. |
| 176 | 7 | `public void setWindow(double from, double to)` | The overview's handles and its window: an explicit window, held to the data. |
| 185 | 15 | `private void clamp()` | Holds the window inside the data and at least MIN_SPAN wide (or the whole of a shorter history). |
| 201 | 1 | `public double lo()` |  |
| 202 | 1 | `public double hi()` |  |
| 203 | 1 | `public double span()` |  |
| 204 | 1 | `public double first()` |  |
| 205 | 1 | `public double last()` |  |
| 206 | 1 | `public int range()` |  |
| 209 | 1 | `public boolean onRange()` | Whether the window is its range - set by a range button or a double-click and not moved by hand since: the button the chart lights. |
| 212 | 1 | `public boolean showsAll()` | Whether the window shows the whole history. |
| 215 | 1 | `public int firstMonthShown()` | The first and last whole months inside the window. |
| 216 | 1 | `public int lastMonthShown()` |  |
| 219 | 1 | `public int monthsShown()` | How many months the window shows, its two ends included. |

### FROM MONTHS TO PIXELS, AND BACK (lines 221-265)

| line | len | member | says |
|---:|---:|---|---|
| 226 | 3 | `public static double x(double month, double lo, double hi, double left, double width)` | Where a month falls across a plot `width` wide starting at `left`, for a window lo..hi. |
| 231 | 3 | `public static double month(double x, double lo, double hi, double left, double width)` | ...and the month under a pixel. |
| 236 | 11 | `public static int nearest(List<Integer> months, double month)` | The index of the month on the axis nearest this one, or -1 for an empty axis. |
| 249 | 10 | `public static double[] reach(double[] values, List<Integer> months, double lo, double hi)` | The lowest and highest of a series inside the window, {low, high}; low above high when nothing is recorded there. |
| 261 | 4 | `public static int bucket(double months, double pixels)` | How many months one drawn point stands for, so a line has at most one point a pixel. |

### WHAT A CHART DRAWS FROM: ONE SNAPSHOT, FIXED WHEN IT IS HANDED OVER (lines 266-377)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 3 | `public static List<Integer> fixedMonths(List<Integer> months)` | The months a chart draws on, copied: the caller's list can grow afterwards without moving them. |
| 294 | 7 | `public static double[] aligned(double[] values, int n)` | A series as a chart keeps it: a copy exactly n long, cut where it is longer and NaN (not recorded) where it is shorter. |
| 303 | 6 | `public static double[][] aligned(double[][] series, int n)` | ...each of several (a stack's layers), every one its own copy; null stays null. |
| 311 | 3 | `public static double at(double[] values, int i)` | A series' value at index i, or NaN off either end: what a readout says of a month the series does not reach. |
| 323 | 28 | `public static List<List<double[]>> stackRuns(List<Integer> months, double[][] layers, int p, double lo, double hi, DoubleUnaryO...` | Layer p of a stack as the chart fills it: one run for each unbroken stretch of months within a month of lo..hi (so the fill reaches the plot's edges), each point {month, top, bottom} in plotted units - the layers unde... |
| 358 | 19 | `public static double[] stackReach(List<Integer> months, double[][] layers, int count, double lo, double hi, DoubleUnaryOperator...` | The lowest and highest the first `count` layers of a stack reach, summed from zero, inside lo..hi: {low, high} in plotted units, zero always between them - what the stack's axis has to hold. |

### THE TIME AXIS (lines 378-439)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 1 | **type** `public record Tick(double month, String label, boolean year)` | One tick on the time axis: the month it marks, its label, and whether it is a year's. |
| 392 | 47 | `public static List<Tick> timeTicks(double lo, double hi, double pixels)` | The ticks for a window lo..hi drawn `pixels` wide, in order. |

### A VALUE AXIS (lines 440-502)

| line | len | member | says |
|---:|---:|---|---|
| 454 | 14 | **type** `public record Scale(double low, double high, double step)` | A value axis: its bottom, its top and the step between its gridlines, in the axis's units. |
| 456 | 7 | `public List<Double> ticks()` _(in ChartModel.Scale)_ | The gridlines, bottom to top. |
| 464 | 3 | `public double y(double v, double bottom, double height)` _(in ChartModel.Scale)_ | Where a value falls on a plot `height` tall whose bottom is at `bottom` (pixels grow down). |
| 470 | 6 | `public static double niceStep(double raw)` | One, two or five times a power of ten, at least `raw`. |
| 482 | 12 | `public static Scale niceScale(double low, double high, int ticks, boolean fromZero)` | A nice scale over low..high, about `ticks` gridlines, from zero when asked and nothing is negative. |
| 496 | 6 | `public static Scale logScale(double lowLog, double highLog)` | A log axis over these LOGARITHMS: whole powers of ten, a gridline each. |

### WHAT IS LAID OVER THE LINES (lines 503-645)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 1 | `public static List<YearBook.Band> bands(HistorySave h)` | The recession bands, exactly YearBook.recessions(), each with its depth and the episode whose name it carries. |
| 511 | 1 | `public static List<YearBook.Episode> episodes(HistorySave h)` | The spans on the episode lane, exactly YearBook.episodes(), oldest first. |
| 518 | 12 | `public static int[] lanes(List<YearBook.Episode> episodes)` | Which row of the episode lane each episode sits on: the first row whose last span ended before this one began, so two that overlap - a treasury crisis inside a recession - never share a row. |
| 532 | 5 | `public static int laneCount(int[] rows)` | How many rows lanes() used. |
| 539 | 3 | **type** `public record Flag(int month, List<DecisionLog.Entry> entries)` | One flag on the decision lane: a month and everything decided in it, in the order it was decided. |
| 540 | 1 | `public int count()` _(in ChartModel.Flag)_ |  |
| 544 | 10 | `public static List<Flag> flags(DecisionLog log)` | The log's entries as flags: one a month, many decisions in one month one flag with a count; oldest first. |
| 561 | 12 | `public static List<Flag> flags(DecisionLog log, String kind)` | ...of one kind only (0.7.32): the Finances tab's chart of what the city owes and its rate carries the BORROWING decisions alone - an issue, a buyback, the rollover's setting, a default abroad. |
| 581 | 13 | `public static List<Flag> flagsOf(DecisionLog log, String...kinds)` | ...of several kinds, one flag a month (0.7.33): the Bank tab's rates carry the CENTRAL_BANK decisions that move the policy rate under them and the BANK ones - a rescue, the preferred offer - in one lane, so a month wi... |
| 603 | 10 | `public static List<Flag> onAxis(List<Flag> flags, int firstMonth)` | The flags as the lane can draw them (0.7.37): a flag from before the axis's first month - the founding month's, which the history does not record, so no window reaches it - is moved onto that first month and merged wi... |
| 615 | 7 | **type** `public record Cluster(int month, List<Flag> flags)` | Flags drawn as one: at the first one's month, so many months packed under one pixel read as one count. |
| 616 | 5 | `public int count()` _(in ChartModel.Cluster)_ |  |
| 629 | 16 | `public static List<Cluster> clusters(List<Flag> flags, double lo, double hi, double pixels, double gapPx)` | The flags inside the window, each month's one flag, and a flag that would sit within `gapPx` of the FIRST flag of the group before it drawn in that group, as one: the decision lane of a long city zoomed out is a row o... |

### the basket's links (0.7.45) (lines 646-715)

| line | len | member | says |
|---:|---:|---|---|
| 655 | 1 | **type** `public record Mark(int month, String words)` | One mark over the plot: its month and what it says. |
| 666 | 33 | `public static List<Mark> basketLinks(HistorySave h, PriceIndex px)` | The months the basket was linked, oldest first, each with what it says: every link the history's basketLinkedAt names (recorded since 0.7.45), at its own month while that month is on the axis, and the link in force (P... |
| 701 | 11 | `static String linkWords(double[] weights, double level, boolean first)` | What a link's mark says: the weights it struck and the level it opened at. |
| 714 | 1 | `public ChartModel()` | A fresh window: no data yet; the first setData() opens it on DEFAULT_RANGE. |

