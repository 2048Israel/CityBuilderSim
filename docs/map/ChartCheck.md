# ChartCheck.java - 722 lines · 22 methods · 0 constants · harnesses

`ham/citybuildersim/ChartCheck.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
> 
> Every fixture causes its condition.

**Uses:** [DecisionLog](DecisionLog.md) (58), [ChartModel](ChartModel.md) (58), [YearBook](YearBook.md) (39), [Game](Game.md) (21), [GameFiles](GameFiles.md) (5), [ConstructionControl](ConstructionControl.md) (5), [Sectors](Sectors.md) (4), [HistorySave](HistorySave.md) (4), [CityCalendar](CityCalendar.md) (4), [GameVersion](GameVersion.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Rollover](Rollover.md) (2), [TaxPolicy](TaxPolicy.md) (2), [Founding](Founding.md) (1), [Sector](Sector.md) (1), [Debt](Debt.md) (1), [TreasuryFund](TreasuryFund.md) (1)

## Sections

| line | section |
|---:|---|
| 148 | 1. THE DECISION LOG |
| 298 | 2. A SAVE AND A LOAD |
| 336 | 3. THE SPANS ARE THE YEAR BOOK'S |
| 491 | 4. THE TICKS |
| 573 | 5. PAN AND ZOOM CLAMP |
| 622 | 6. THE FLAGS |
| 673 | 7. A YOUNG CITY'S WINDOW |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static int fails` |  |
| 70 | `static PrintStream out` |  |
| 71 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 656 | **type** `public class ChartCheck` | The charts (0.7.23): the player's decisions as the model records them, and the arithmetic City History's charts are drawn by - the window, the ticks, the scales, the bands, the lanes and the flags, and the years under... |
| 73 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 78 | 5 | `static void same(String label, Object actual, Object expected)` |  |
| 84 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 90 | 5 | `static void quietly(Runnable r)` |  |
| 96 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 108 | 21 | `static Game city(Path root, String name)` | A played city with a little of everything a decision touches: works, people, a bank, depots, every sector held so no planner orders anything, and on site a University and two Middle Schools of the city's own. |
| 130 | 17 | `public static void main(String[] args) throws Exception` |  |

### 1. THE DECISION LOG (lines 148-297)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 5 | `static List<String> labelsSince(DecisionLog log, int from)` | The entries made since `from`, by label. |
| 158 | 3 | `static void decides(Game g, String what, String kind, String label, Runnable hand)` | One decision: the hand, then exactly one new entry, at this month, of this kind, saying this. |
| 163 | 12 | `static void decides(Game g, String what, String kind, java.util.function.Supplier<String> saying, Runnable hand)` | ...with what it should say worked out after the hand, from what the hand did: a face, a clamped floor. |
| 177 | 6 | `static void changesNothing(Game g, String what, Runnable hand)` | ...and the same hand again, changing nothing, records nothing. |
| 185 | 4 | `static double newestFace(Game g)` | The face of the paper the city issued last: the newest debt on its books. |
| 190 | 107 | `static Game theLogRecordsEachKind(Path root)` |  |

### 2. A SAVE AND A LOAD (lines 298-335)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 35 | `static void theLogSurvivesASave(Path root, Game g) throws Exception` |  |

### 3. THE SPANS ARE THE YEAR BOOK'S (lines 336-490)

| line | len | member | says |
|---:|---:|---|---|
| 339 | 14 | `static HistorySave built(int months, String[] series, double[]...values)` | A history of `months` months with these series, each as long as the axis. |
| 354 | 5 | `static double[] flat(int months, double v)` |  |
| 360 | 87 | `static void theSpansAreTheYearBooks()` |  |
| 455 | 35 | `static void whatIsRunning()` | What City History's RUNNING NOW reads (0.7.37): YearBook.running(). |

### 4. THE TICKS (lines 491-572)

| line | len | member | says |
|---:|---:|---|---|
| 493 | 79 | `static void theTicks()` |  |

### 5. PAN AND ZOOM CLAMP (lines 573-621)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 46 | `static void panAndZoomClamp()` |  |

### 6. THE FLAGS (lines 622-672)

| line | len | member | says |
|---:|---:|---|---|
| 624 | 48 | `static void theFlags()` |  |

### 7. A YOUNG CITY'S WINDOW (lines 673-722)

| line | len | member | says |
|---:|---:|---|---|
| 675 | 47 | `static void aYoungCitysWindow()` |  |

