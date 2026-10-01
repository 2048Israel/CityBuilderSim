# ChartModel.java - 474 lines · 38 methods · 11 constants · model

`ham/citybuildersim/ChartModel.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> What a time chart shows, as numbers: the window of months it looks at and
> how a drag, a wheel, a range button and the overview move it; the ticks on
> its two axes; and the bands, the episodes and the decisions it lays over
> the lines (0.7.23).
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

**Uses:** [CityCalendar](CityCalendar.md) (9), [YearBook](YearBook.md) (6), [DecisionLog](DecisionLog.md) (5), [HistorySave](HistorySave.md) (2)

**Used by (4):** [ChartCheck](ChartCheck.md), [HistoryScreen](HistoryScreen.md), [Pieces](Pieces.md), [TimeChart](TimeChart.md)

## Sections

| line | section |
|---:|---|
| 69 | THE WINDOW |
| 218 | FROM MONTHS TO PIXELS, AND BACK |
| 263 | THE TIME AXIS |
| 325 | A VALUE AXIS |
| 388 | WHAT IS LAID OVER THE LINES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 37 | `ChartModel.MIN_SPAN` | `6` | The fewest months the main chart can be zoomed down to: half a year, seven points. |
| 40 | `ChartModel.RANGES` | `{ 12, 60, 120, 600 }` | The range buttons, in months: one, five, ten and fifty years; "All" is the whole history. |
| 43 | `ChartModel.RANGE_NAMES` | `{ "1Y", "5Y", "10Y", "50Y", "All" }` | What the range buttons say, in RANGES' order, then the whole history's. |
| 46 | `ChartModel.ALL` | `Integer.MAX_VALUE` | The range that means the whole history. |
| 49 | `ChartModel.DEFAULT_RANGE` | `120` | The range a chart opens on: ten years, what City History drew before it had buttons. |
| 52 | `ChartModel.ZOOM_STEP` | `0.85` | What one notch of the wheel leaves in view, zooming in; zooming out is its inverse. |
| 55 | `ChartModel.YEAR_LABEL_PX` | `46` | The least room, in pixels, between two year labels: a "2141" and a gap. |
| 58 | `ChartModel.MONTH_LABEL_PX` | `36` | The least room between two month labels: a "Mar" and a gap. |
| 61 | `ChartModel.YEAR_STEPS` | `{ 1, 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000 }` | The year steps the axis may label, smallest first: every year, every second, every fifth... |
| 64 | `ChartModel.MONTH_STEPS` | `{ 1, 2, 3 }` | The month steps a zoomed-in axis may label between its years: monthly, two-monthly, quarterly - no coarser, or ten years across a wide screen would be half-years. |
| 67 | `ChartModel.AXIS_PAD` | `.06` | Headroom a value axis leaves above and below what is drawn, as a share of the range: a line at its extreme is not drawn along the frame. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 80 | `private double first, last` |  |
| 81 | `private double lo, hi` |  |
| 82 | `private int range` |  |
| 83 | `private boolean placed` |  |
| 93 | `private boolean touched` | Whether the player has moved the window by hand - a drag, a wheel, the overview - since the last range button or double-click. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 441 | **type** `public final class ChartModel` | What a time chart shows, as numbers: the window of months it looks at and how a drag, a wheel, a range button and the overview move it; the ticks on its two axes; and the bands, the episodes and the decisions it lays ... |

### THE WINDOW (lines 69-217)

| line | len | member | says |
|---:|---:|---|---|
| 103 | 24 | `public void setData(int firstMonth, int lastMonth)` | The months the data covers, first to last - every rebuild hands it the history's axis. |
| 129 | 12 | `public void showRange(int months)` | A range button: the last `months` months, ending at the newest; ALL is everything. |
| 143 | 1 | `public void reset()` | The double-click: back to the last range picked, ending at the newest month. |
| 146 | 7 | `public void pan(double months)` | A drag: the window moved by this many months, stopped at either end of the data. |
| 158 | 13 | `public void zoom(double factor, double anchorMonth)` | A wheel notch: the window `factor` times as wide, about the month under the pointer, which stays under it. |
| 173 | 7 | `public void setWindow(double from, double to)` | The overview's handles and its window: an explicit window, held to the data. |
| 182 | 15 | `private void clamp()` | Holds the window inside the data and at least MIN_SPAN wide (or the whole of a shorter history). |
| 198 | 1 | `public double lo()` |  |
| 199 | 1 | `public double hi()` |  |
| 200 | 1 | `public double span()` |  |
| 201 | 1 | `public double first()` |  |
| 202 | 1 | `public double last()` |  |
| 203 | 1 | `public int range()` |  |
| 206 | 1 | `public boolean onRange()` | Whether the window is its range - set by a range button or a double-click and not moved by hand since: the button the chart lights. |
| 209 | 1 | `public boolean showsAll()` | Whether the window shows the whole history. |
| 212 | 1 | `public int firstMonthShown()` | The first and last whole months inside the window. |
| 213 | 1 | `public int lastMonthShown()` |  |
| 216 | 1 | `public int monthsShown()` | How many months the window shows, its two ends included. |

### FROM MONTHS TO PIXELS, AND BACK (lines 218-262)

| line | len | member | says |
|---:|---:|---|---|
| 223 | 3 | `public static double x(double month, double lo, double hi, double left, double width)` | Where a month falls across a plot `width` wide starting at `left`, for a window lo..hi. |
| 228 | 3 | `public static double month(double x, double lo, double hi, double left, double width)` | ...and the month under a pixel. |
| 233 | 11 | `public static int nearest(List<Integer> months, double month)` | The index of the month on the axis nearest this one, or -1 for an empty axis. |
| 246 | 10 | `public static double[] reach(double[] values, List<Integer> months, double lo, double hi)` | The lowest and highest of a series inside the window, {low, high}; low above high when nothing is recorded there. |
| 258 | 4 | `public static int bucket(double months, double pixels)` | How many months one drawn point stands for, so a line has at most one point a pixel. |

### THE TIME AXIS (lines 263-324)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 1 | **type** `public record Tick(double month, String label, boolean year)` | One tick on the time axis: the month it marks, its label, and whether it is a year's. |
| 277 | 47 | `public static List<Tick> timeTicks(double lo, double hi, double pixels)` | The ticks for a window lo..hi drawn `pixels` wide, in order. |

### A VALUE AXIS (lines 325-387)

| line | len | member | says |
|---:|---:|---|---|
| 339 | 14 | **type** `public record Scale(double low, double high, double step)` | A value axis: its bottom, its top and the step between its gridlines, in the axis's units. |
| 341 | 7 | `public List<Double> ticks()` _(in ChartModel.Scale)_ | The gridlines, bottom to top. |
| 349 | 3 | `public double y(double v, double bottom, double height)` _(in ChartModel.Scale)_ | Where a value falls on a plot `height` tall whose bottom is at `bottom` (pixels grow down). |
| 355 | 6 | `public static double niceStep(double raw)` | One, two or five times a power of ten, at least `raw`. |
| 367 | 12 | `public static Scale niceScale(double low, double high, int ticks, boolean fromZero)` | A nice scale over low..high, about `ticks` gridlines, from zero when asked and nothing is negative. |
| 381 | 6 | `public static Scale logScale(double lowLog, double highLog)` | A log axis over these LOGARITHMS: whole powers of ten, a gridline each. |

### WHAT IS LAID OVER THE LINES (lines 388-474)

| line | len | member | says |
|---:|---:|---|---|
| 393 | 1 | `public static List<YearBook.Band> bands(HistorySave h)` | The recession bands, exactly YearBook.recessions(), each with its depth and the episode whose name it carries. |
| 396 | 1 | `public static List<YearBook.Episode> episodes(HistorySave h)` | The spans on the episode lane, exactly YearBook.episodes(), oldest first. |
| 403 | 12 | `public static int[] lanes(List<YearBook.Episode> episodes)` | Which row of the episode lane each episode sits on: the first row whose last span ended before this one began, so two that overlap - a treasury crisis inside a recession - never share a row. |
| 417 | 5 | `public static int laneCount(int[] rows)` | How many rows lanes() used. |
| 424 | 3 | **type** `public record Flag(int month, List<DecisionLog.Entry> entries)` | One flag on the decision lane: a month and everything decided in it, in the order it was decided. |
| 425 | 1 | `public int count()` _(in ChartModel.Flag)_ |  |
| 429 | 10 | `public static List<Flag> flags(DecisionLog log)` | The log's entries as flags: one a month, many decisions in one month one flag with a count; oldest first. |
| 441 | 7 | **type** `public record Cluster(int month, List<Flag> flags)` | Flags drawn as one: at the first one's month, so many months packed under one pixel read as one count. |
| 442 | 5 | `public int count()` _(in ChartModel.Cluster)_ |  |
| 455 | 16 | `public static List<Cluster> clusters(List<Flag> flags, double lo, double hi, double pixels, double gapPx)` | The flags inside the window, each month's one flag, and a flag that would sit within `gapPx` of the FIRST flag of the group before it drawn in that group, as one: the decision lane of a long city zoomed out is a row o... |
| 473 | 1 | `public ChartModel()` | A fresh window: no data yet; the first setData() opens it on DEFAULT_RANGE. |

