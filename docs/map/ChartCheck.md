# ChartCheck.java - 962 lines · 28 methods · 0 constants · harnesses

`ham/citybuildersim/ChartCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

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
>   9. A SERIES THAT CANNOT GO UNDER ZERO NEVER GETS AN AXIS FLOOR UNDER IT
>      (PT1): City History's population chart read -100 at the bottom with
>      the founding in view, because the scale pads below the series' low
>      and the founding's low is near 0. The fixture is that population,
>      from 0 and from a few, up to thousands, on a scale not asked to
> ... (3 more lines in the source)

**Uses:** [ChartModel](ChartModel.md) (92), [DecisionLog](DecisionLog.md) (58), [YearBook](YearBook.md) (42), [Game](Game.md) (24), [GameFiles](GameFiles.md) (5), [ConstructionControl](ConstructionControl.md) (5), [HistorySave](HistorySave.md) (5), [Sectors](Sectors.md) (4), [CityCalendar](CityCalendar.md) (4), [GameVersion](GameVersion.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Rollover](Rollover.md) (2), [TaxPolicy](TaxPolicy.md) (2), [Founding](Founding.md) (1), [Sector](Sector.md) (1), [TempDirs](TempDirs.md) (1), [Debt](Debt.md) (1), [TreasuryFund](TreasuryFund.md) (1), [SaveFileCheck](SaveFileCheck.md) (1), [Health](Health.md) (1)

## Sections

| line | section |
|---:|---|
| 164 | 1. THE DECISION LOG |
| 317 | 2. A SAVE AND A LOAD |
| 370 | 3. THE SPANS ARE THE YEAR BOOK'S |
| 526 | 4. THE TICKS |
| 608 | 5. PAN AND ZOOM CLAMP |
| 657 | 6. THE FLAGS |
| 708 | 7. A YOUNG CITY'S WINDOW |
| 758 | 8. A CHART DRAWS FROM WHAT IT WAS HANDED |
| 925 | 9. NO AXIS FLOOR UNDER A SERIES THAT STAYS OVER ZERO |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `static int fails` |  |
| 84 | `static PrintStream out` |  |
| 85 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 882 | **type** `public class ChartCheck` | The charts (0.7.23): the player's decisions as the model records them, and the arithmetic City History's charts are drawn by - the window, the ticks, the scales, the bands, the lanes and the flags, and the years under... |
| 87 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 92 | 5 | `static void same(String label, Object actual, Object expected)` |  |
| 98 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 104 | 5 | `static void quietly(Runnable r)` |  |
| 110 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 122 | 21 | `static Game city(Path root, String name)` | A played city with a little of everything a decision touches: works, people, a bank, depots, every sector held so no planner orders anything, and on site a University and two Middle Schools of the city's own. |
| 144 | 19 | `public static void main(String[] args) throws Exception` |  |

### 1. THE DECISION LOG (lines 164-316)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 5 | `static List<String> labelsSince(DecisionLog log, int from)` | The entries made since `from`, by label. |
| 174 | 3 | `static void decides(Game g, String what, String kind, String label, Runnable hand)` | One decision: the hand, then exactly one new entry, at this month, of this kind, saying this. |
| 179 | 12 | `static void decides(Game g, String what, String kind, java.util.function.Supplier<String> saying, Runnable hand)` | ...with what it should say worked out after the hand, from what the hand did: a face, a clamped floor. |
| 193 | 6 | `static void changesNothing(Game g, String what, Runnable hand)` | ...and the same hand again, changing nothing, records nothing. |
| 201 | 4 | `static double newestFace(Game g)` | The face of the paper the city issued last: the newest debt on its books. |
| 206 | 110 | `static Game theLogRecordsEachKind(Path root)` |  |

### 2. A SAVE AND A LOAD (lines 317-369)

| line | len | member | says |
|---:|---:|---|---|
| 319 | 50 | `static void theLogSurvivesASave(Path root, Game g) throws Exception` |  |

### 3. THE SPANS ARE THE YEAR BOOK'S (lines 370-525)

| line | len | member | says |
|---:|---:|---|---|
| 373 | 14 | `static HistorySave built(int months, String[] series, double[]...values)` | A history of `months` months with these series, each as long as the axis. |
| 388 | 5 | `static double[] flat(int months, double v)` |  |
| 394 | 87 | `static void theSpansAreTheYearBooks()` |  |
| 490 | 35 | `static void whatIsRunning()` | What City History's RUNNING NOW reads (0.7.37): YearBook.running(). |

### 4. THE TICKS (lines 526-607)

| line | len | member | says |
|---:|---:|---|---|
| 528 | 79 | `static void theTicks()` |  |

### 5. PAN AND ZOOM CLAMP (lines 608-656)

| line | len | member | says |
|---:|---:|---|---|
| 610 | 46 | `static void panAndZoomClamp()` |  |

### 6. THE FLAGS (lines 657-707)

| line | len | member | says |
|---:|---:|---|---|
| 659 | 48 | `static void theFlags()` |  |

### 7. A YOUNG CITY'S WINDOW (lines 708-757)

| line | len | member | says |
|---:|---:|---|---|
| 710 | 47 | `static void aYoungCitysWindow()` |  |

### 8. A CHART DRAWS FROM WHAT IT WAS HANDED (lines 758-924)

| line | len | member | says |
|---:|---:|---|---|
| 767 | 22 | `static List<double[]> walk0749(List<Integer> months, double[][] layers, double lo, double hi)` | 0.7.49's TimeChart.drawStack(), its walk without the canvas: each layer's points {month, top, bottom} over the months it was handed, the way it read them - straight out of the arrays, by the list's index. |
| 791 | 7 | `static List<double[]> runs0750(List<Integer> months, double[][] layers, double lo, double hi)` | The same points as ChartModel.stackRuns() gives them, every layer's runs in the order the chart fills them. |
| 799 | 5 | `static boolean samePoints(List<double[]> a, List<double[]> b)` |  |
| 812 | 11 | `static double[] frame(List<Integer> months, double[][] layers, double[][] lines, ChartModel w, double pointer)` | What the chart does on every frame, from what it holds: the stack's runs and reach, each line read at every month in the window, and the hover's readout at the month nearest `pointer` - every line's and layer's value ... |
| 824 | 100 | `static void aChartDrawsFromItsSnapshot(Path root)` |  |

### 9. NO AXIS FLOOR UNDER A SERIES THAT STAYS OVER ZERO (lines 925-962)

| line | len | member | says |
|---:|---:|---|---|
| 927 | 35 | `static void aSeriesThatCannotGoUnderZero()` |  |

