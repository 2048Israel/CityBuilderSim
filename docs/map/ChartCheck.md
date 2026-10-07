# ChartCheck.java - 899 lines · 27 methods · 0 constants · harnesses

`ham/citybuildersim/ChartCheck.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> The charts (0.7.23): the player's decisions as the model records them,
> and the arithmetic City History's charts are drawn by - the window, the
> ticks, the scales, the bands, the lanes and the flags, and the years under
> every other chart - held to its own rules without a screen.
> 
> WHY. Jerus asked for charts "just like yahoo finance", crisis labels that
> are clear, and the player's own decisions on the timeline. A decision is a
> flow the state cannot give back, so it is recorded where it is applied
> (DecisionLog) and saved; a chart is a window of months and a set of
> labels, and the arithmetic of both is ChartModel's, which needs no
> toolkit. The screen is checked by eye on the PC; everything it draws by
> is checked here.
> 
> What this has to prove:
>   1. THE DECISION LOG records each kind of decision once, at the month it
>      was made, with its label - a tax, a promise, the central bank, the
>      money, the paper, the bank, the fund and the queue - and nothing
>      that changes nothing; a city built and played by the fixture's hand
>      records nothing, ordinary build orders included; the founding's and
>      the load's own settings are not decisions; the rollover's issues are
>      not decisions either.
>   2. IT SURVIVES A SAVE AND A LOAD, entry for entry, and a format-28 save
>      loads with an empty log.
>   3. THE CHART'S SPANS ARE YEARBOOK'S: the bands are exactly recessions(),
>      each carrying the name of the recession that holds it, its depth the
>      year book's own; the episode lane is exactly episodes(), and no two
>      that overlap share a row; every kind says the rule that named it; and
>      (0.7.37) what is running is what the history has not closed, a
>      crisis before a watch, the newest first, the chronic last.
>   4. THE TICKS: every window, at every width, has years on its axis - on
>      January, every tick at least MONTH_LABEL_PX apart - and months only
>      when they fit; a value axis steps by one, two or five times a power
>      of ten and holds what is drawn, a per cent from zero; and the new
>      city's flat rate and flat price level no longer lie on each other.
>   5. PAN AND ZOOM CLAMP TO THE DATA: a drag stops at either end, a wheel at
>      MIN_SPAN and at the whole history, the month under the pointer stays
>      under it, and a window on the newest month follows it.
>   6. THE FLAGS: one a month, with the count; flags too close to part drawn
>      as one; and (0.7.37) a decision from the founding month, before the
>      history's first month, on the lane at that first month.
>   7. A YOUNG CITY'S WINDOW GROWS INTO ITS RANGE (after the docs pass): a
>      history shorter than the range is shown whole and then, as it grows,
>      is the last ten years - or whichever range was pressed - every
>      month, with that range still the one lit; a window moved by hand
>      keeps its own width, and only one the player zoomed out to
>      everything stays everything; a double-click puts it back on its
>      range.
>   8. A CHART DRAWS FROM WHAT IT WAS HANDED (0.7.50): the months and every
>      series fixed at handover, all as long as the months, so a month
>      the history records under a chart still on screen moves nothing it
>      reads - the stack's runs and reach, the hover's readout and the
>      redraw when the pointer leaves are those of the handover, and the
>      stack's arithmetic, handed the history's own growing list, still
>      reads nothing past either. The fixture is Jerus's 0.7.49 freeze:
>      the same month, under that version's walk, reads past the layers.
> 
> Every fixture causes its condition.

**Uses:** [ChartModel](ChartModel.md) (77), [DecisionLog](DecisionLog.md) (58), [YearBook](YearBook.md) (42), [Game](Game.md) (22), [GameFiles](GameFiles.md) (5), [ConstructionControl](ConstructionControl.md) (5), [HistorySave](HistorySave.md) (5), [Sectors](Sectors.md) (4), [CityCalendar](CityCalendar.md) (4), [GameVersion](GameVersion.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Rollover](Rollover.md) (2), [TaxPolicy](TaxPolicy.md) (2), [Founding](Founding.md) (1), [Sector](Sector.md) (1), [Debt](Debt.md) (1), [TreasuryFund](TreasuryFund.md) (1), [Health](Health.md) (1)

## Sections

| line | section |
|---:|---|
| 157 | 1. THE DECISION LOG |
| 307 | 2. A SAVE AND A LOAD |
| 345 | 3. THE SPANS ARE THE YEAR BOOK'S |
| 501 | 4. THE TICKS |
| 583 | 5. PAN AND ZOOM CLAMP |
| 632 | 6. THE FLAGS |
| 683 | 7. A YOUNG CITY'S WINDOW |
| 733 | 8. A CHART DRAWS FROM WHAT IT WAS HANDED |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |
| 78 | `static PrintStream out` |  |
| 79 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 825 | **type** `public class ChartCheck` | The charts (0.7.23): the player's decisions as the model records them, and the arithmetic City History's charts are drawn by - the window, the ticks, the scales, the bands, the lanes and the flags, and the years under... |
| 81 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 86 | 5 | `static void same(String label, Object actual, Object expected)` |  |
| 92 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 98 | 5 | `static void quietly(Runnable r)` |  |
| 104 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 116 | 21 | `static Game city(Path root, String name)` | A played city with a little of everything a decision touches: works, people, a bank, depots, every sector held so no planner orders anything, and on site a University and two Middle Schools of the city's own. |
| 138 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. THE DECISION LOG (lines 157-306)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 5 | `static List<String> labelsSince(DecisionLog log, int from)` | The entries made since `from`, by label. |
| 167 | 3 | `static void decides(Game g, String what, String kind, String label, Runnable hand)` | One decision: the hand, then exactly one new entry, at this month, of this kind, saying this. |
| 172 | 12 | `static void decides(Game g, String what, String kind, java.util.function.Supplier<String> saying, Runnable hand)` | ...with what it should say worked out after the hand, from what the hand did: a face, a clamped floor. |
| 186 | 6 | `static void changesNothing(Game g, String what, Runnable hand)` | ...and the same hand again, changing nothing, records nothing. |
| 194 | 4 | `static double newestFace(Game g)` | The face of the paper the city issued last: the newest debt on its books. |
| 199 | 107 | `static Game theLogRecordsEachKind(Path root)` |  |

### 2. A SAVE AND A LOAD (lines 307-344)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 35 | `static void theLogSurvivesASave(Path root, Game g) throws Exception` |  |

### 3. THE SPANS ARE THE YEAR BOOK'S (lines 345-500)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 14 | `static HistorySave built(int months, String[] series, double[]...values)` | A history of `months` months with these series, each as long as the axis. |
| 363 | 5 | `static double[] flat(int months, double v)` |  |
| 369 | 87 | `static void theSpansAreTheYearBooks()` |  |
| 465 | 35 | `static void whatIsRunning()` | What City History's RUNNING NOW reads (0.7.37): YearBook.running(). |

### 4. THE TICKS (lines 501-582)

| line | len | member | says |
|---:|---:|---|---|
| 503 | 79 | `static void theTicks()` |  |

### 5. PAN AND ZOOM CLAMP (lines 583-631)

| line | len | member | says |
|---:|---:|---|---|
| 585 | 46 | `static void panAndZoomClamp()` |  |

### 6. THE FLAGS (lines 632-682)

| line | len | member | says |
|---:|---:|---|---|
| 634 | 48 | `static void theFlags()` |  |

### 7. A YOUNG CITY'S WINDOW (lines 683-732)

| line | len | member | says |
|---:|---:|---|---|
| 685 | 47 | `static void aYoungCitysWindow()` |  |

### 8. A CHART DRAWS FROM WHAT IT WAS HANDED (lines 733-899)

| line | len | member | says |
|---:|---:|---|---|
| 742 | 22 | `static List<double[]> walk0749(List<Integer> months, double[][] layers, double lo, double hi)` | 0.7.49's TimeChart.drawStack(), its walk without the canvas: each layer's points {month, top, bottom} over the months it was handed, the way it read them - straight out of the arrays, by the list's index. |
| 766 | 7 | `static List<double[]> runs0750(List<Integer> months, double[][] layers, double lo, double hi)` | The same points as ChartModel.stackRuns() gives them, every layer's runs in the order the chart fills them. |
| 774 | 5 | `static boolean samePoints(List<double[]> a, List<double[]> b)` |  |
| 787 | 11 | `static double[] frame(List<Integer> months, double[][] layers, double[][] lines, ChartModel w, double pointer)` | What the chart does on every frame, from what it holds: the stack's runs and reach, each line read at every month in the window, and the hover's readout at the month nearest `pointer` - every line's and layer's value ... |
| 799 | 100 | `static void aChartDrawsFromItsSnapshot(Path root)` |  |

