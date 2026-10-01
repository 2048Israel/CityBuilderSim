# ChartCheck.java - 652 lines · 21 methods · 0 constants · harnesses

`ham/citybuildersim/ChartCheck.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

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
>      that overlap share a row; every kind says the rule that named it.
>   4. THE TICKS: every window, at every width, has years on its axis - on
>      January, every tick at least MONTH_LABEL_PX apart - and months only
>      when they fit; a value axis steps by one, two or five times a power
>      of ten and holds what is drawn, a per cent from zero; and the new
>      city's flat rate and flat price level no longer lie on each other.
>   5. PAN AND ZOOM CLAMP TO THE DATA: a drag stops at either end, a wheel at
>      MIN_SPAN and at the whole history, the month under the pointer stays
>      under it, and a window on the newest month follows it.
>   6. THE FLAGS: one a month, with the count; flags too close to part drawn
>      as one.
>   7. A YOUNG CITY'S WINDOW GROWS INTO ITS RANGE (after the docs pass): a
>      history shorter than the range is shown whole and then, as it grows,
>      is the last ten years - or whichever range was pressed - every
>      month, with that range still the one lit; a window moved by hand
>      keeps its own width, and only one the player zoomed out to
>      everything stays everything; a double-click puts it back on its
>      range.
> 
> Every fixture causes its condition.

**Uses:** [DecisionLog](DecisionLog.md) (52), [ChartModel](ChartModel.md) (50), [Game](Game.md) (21), [YearBook](YearBook.md) (20), [GameFiles](GameFiles.md) (5), [ConstructionControl](ConstructionControl.md) (5), [Sectors](Sectors.md) (4), [CityCalendar](CityCalendar.md) (4), [GameVersion](GameVersion.md) (3), [HistorySave](HistorySave.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Rollover](Rollover.md) (2), [TaxPolicy](TaxPolicy.md) (2), [Founding](Founding.md) (1), [Sector](Sector.md) (1), [Debt](Debt.md) (1), [TreasuryFund](TreasuryFund.md) (1)

## Sections

| line | section |
|---:|---|
| 145 | 1. THE DECISION LOG |
| 295 | 2. A SAVE AND A LOAD |
| 333 | 3. THE SPANS ARE THE YEAR BOOK'S |
| 443 | 4. THE TICKS |
| 525 | 5. PAN AND ZOOM CLAMP |
| 574 | 6. THE FLAGS |
| 603 | 7. A YOUNG CITY'S WINDOW |

## Fields (state)

| line | field | says |
|---:|---|---|
| 66 | `static int fails` |  |
| 67 | `static PrintStream out` |  |
| 68 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 589 | **type** `public class ChartCheck` | The charts (0.7.23): the player's decisions as the model records them, and the arithmetic City History's charts are drawn by - the window, the ticks, the scales, the bands, the lanes and the flags, and the years under... |
| 70 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 75 | 5 | `static void same(String label, Object actual, Object expected)` |  |
| 81 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 87 | 5 | `static void quietly(Runnable r)` |  |
| 93 | 6 | `static BuildingsTemplate template(Game g, String name)` |  |
| 105 | 21 | `static Game city(Path root, String name)` | A played city with a little of everything a decision touches: works, people, a bank, depots, every sector held so no planner orders anything, and on site a University and two Middle Schools of the city's own. |
| 127 | 17 | `public static void main(String[] args) throws Exception` |  |

### 1. THE DECISION LOG (lines 145-294)

| line | len | member | says |
|---:|---:|---|---|
| 148 | 5 | `static List<String> labelsSince(DecisionLog log, int from)` | The entries made since `from`, by label. |
| 155 | 3 | `static void decides(Game g, String what, String kind, String label, Runnable hand)` | One decision: the hand, then exactly one new entry, at this month, of this kind, saying this. |
| 160 | 12 | `static void decides(Game g, String what, String kind, java.util.function.Supplier<String> saying, Runnable hand)` | ...with what it should say worked out after the hand, from what the hand did: a face, a clamped floor. |
| 174 | 6 | `static void changesNothing(Game g, String what, Runnable hand)` | ...and the same hand again, changing nothing, records nothing. |
| 182 | 4 | `static double newestFace(Game g)` | The face of the paper the city issued last: the newest debt on its books. |
| 187 | 107 | `static Game theLogRecordsEachKind(Path root)` |  |

### 2. A SAVE AND A LOAD (lines 295-332)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 35 | `static void theLogSurvivesASave(Path root, Game g) throws Exception` |  |

### 3. THE SPANS ARE THE YEAR BOOK'S (lines 333-442)

| line | len | member | says |
|---:|---:|---|---|
| 336 | 14 | `static HistorySave built(int months, String[] series, double[]...values)` | A history of `months` months with these series, each as long as the axis. |
| 351 | 5 | `static double[] flat(int months, double v)` |  |
| 357 | 85 | `static void theSpansAreTheYearBooks()` |  |

### 4. THE TICKS (lines 443-524)

| line | len | member | says |
|---:|---:|---|---|
| 445 | 79 | `static void theTicks()` |  |

### 5. PAN AND ZOOM CLAMP (lines 525-573)

| line | len | member | says |
|---:|---:|---|---|
| 527 | 46 | `static void panAndZoomClamp()` |  |

### 6. THE FLAGS (lines 574-602)

| line | len | member | says |
|---:|---:|---|---|
| 576 | 26 | `static void theFlags()` |  |

### 7. A YOUNG CITY'S WINDOW (lines 603-652)

| line | len | member | says |
|---:|---:|---|---|
| 605 | 47 | `static void aYoungCitysWindow()` |  |

