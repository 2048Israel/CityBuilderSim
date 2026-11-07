# ClockPace.java - 297 lines · 19 methods · 7 constants · model

`ham/citybuildersim/ClockPace.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> The clock's pace (UP2): how much of each frame the months may take, how often the screen is
> redrawn and what the redraw cost, and what speed the city is really running at.
> 
> It holds the arithmetic of ui.UserInterface.startClock() and nothing else: pure Java, no JavaFX,
> with its time source injected so a harness can drive it with a clock it advances by hand
> (ClockCheck). The window's timer calls frame() once a frame, redrawStarted() and redrawEnded()
> around each redraw it makes, and asks redrawDue(), progress() and reached().
> 
> WHY THIS EXISTS. The timer ran every month that was due in a frame, however long they took (up
> to two and a half of them after a stalled frame at 50x), and redrew the open screen at a fixed
> 120 ms spacing that never looked at what a redraw cost. Neither cost mattered while a month and
> a page were cheap. At month 4,000 a People page took 55 ms to build and lay out, so at 50x
> redraws took nearly half of the FX thread, and a slow stretch stacked: the owed time was banked
> and paid back in a burst the frame after. So the redraw now waits three times what it cost (it
> takes at most a third of the thread), a frame stops running months when it has spent
> MONTH_FRAME_MS on them and drops the time it could not run instead of banking it, and the
> speed the header reads says when the city is running slower than the rung asked for (UP7: it
> reads the speed reached alone, in place of the rung, and counts the time a frame was worth
> rather than the time it took, so a stalled frame is not a shortfall).

**Used by (4):** [ClockCheck](ClockCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 105 | THE MONTHS A FRAME RUNS |
| 194 | HOW OFTEN THE SCREEN IS REDRAWN |
| 233 | THE SPEED THE CITY IS REALLY RUNNING AT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `ClockPace.REDRAW_EVERY` | `.12` | The least time between one redraw of the open screen and the next while the clock runs, in seconds (moved here from ui.UserInterface by UP2, its comment with it). |
| 51 | `ClockPace.REDRAW_SPACING` | `3` | How many times a redraw's cost the next one waits (UP2): 3, so redraws take at most a third of the thread. |
| 54 | `ClockPace.REDRAW_COST_MAX` | `.5` | The most a redraw is counted to have cost, in seconds (UP2): the pulse that ends the measure can be late (a minimised window), and a gap of minutes is a screen that never moves. |
| 57 | `ClockPace.MONTH_FRAME_MS` | `40` | The time one frame may spend running months, in milliseconds (UP2): the loop stops when the months run in it pass this, but the first month always runs. |
| 71 | `ClockPace.FRAME_CLAMP` | `.25` | The most real time one frame is worth to the game's clock, in seconds. |
| 74 | `ClockPace.SHORT_WINDOW` | `2` | The running time the speed achieved is read over, in seconds (UP2); until a full window has run the header reads the rung alone. |
| 77 | `ClockPace.SHORT_BY` | `.1` | How far under the rung the speed achieved must fall, as a share of the rung, before the header says so (UP2): more than a tenth. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `private final LongSupplier nanoTime` |  |
| 82 | `private double progress` | The months run in the month now under way and owed to the next, as a fraction; a month is due at 1. |
| 83 | `private double dropped` |  |
| 85 | `private double sinceRedraw` |  |
| 86 | `private double redrawCost` |  |
| 87 | `private double redrawGap` |  |
| 88 | `private long redrawBegan` |  |
| 89 | `private boolean redrawing` |  |
| 92 | `private final ArrayDeque<double[]> window` | The frames of the window: each one's real seconds and the months it advanced. |
| 93 | `private double windowSeconds, windowMonths` |  |
| 94 | `private double windowRung` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 266 | **type** `public final class ClockPace` | The clock's pace (UP2): how much of each frame the months may take, how often the screen is redrawn and what the redraw cost, and what speed the city is really running at. |
| 96 | 3 | `public ClockPace()` |  |
| 101 | 3 | `public ClockPace(LongSupplier nanoTime)` |  |

### THE MONTHS A FRAME RUNS (lines 105-193)

| line | len | member | says |
|---:|---:|---|---|
| 123 | 40 | `public int frame(double dt, double speed, double secondsPerMonth, BooleanSupplier month)` | One frame of the clock while it runs: adds the share of a month the frame is worth (clamped to FRAME_CLAMP), then runs {@code month} while a month is due and the months run in this frame have taken under MONTH_FRAME_M... |
| 165 | 3 | `public void idle(double dt)` | A frame of a clock that is not running (paused, or a menu or dialog up): only the redraw's time moves. |
| 170 | 3 | `public double progress()` | The share of the next month the clock has run, 0 up to 1 (1 while a month is owed that a slow frame could not run): what the date's day is read from. |
| 175 | 3 | `public double droppedMonths()` | The months of owed time that were dropped, not run, since the pace was made: a stalled frame past FRAME_CLAMP, what a slow frame could not fit, what a stop zeroed. |
| 180 | 5 | `public void stopped()` | The clock stopped itself or the city was closed: nothing is owed, and the speed's window starts again. |
| 187 | 6 | `public void restart()` | The speed's window starts again (the clock starts, the speed changes, another city opens); the progress stands. |

### HOW OFTEN THE SCREEN IS REDRAWN (lines 194-232)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 3 | `public boolean redrawDue()` | Whether the gap since the last redraw began has passed: a redraw that is pending may be drawn. |
| 204 | 5 | `public void redrawStarted()` | A redraw of the open screen begins: the time since the last starts again, and its cost is measured from here. |
| 211 | 3 | `public boolean redrawing()` | Whether a redraw began and its cost has not been taken (redrawEnded()). |
| 216 | 6 | `public void redrawEnded()` | The redraw that began is over - drawn, and CSS and layout done: its cost sets the gap before the next. |
| 224 | 3 | `public double redrawGap()` | The seconds the next redraw waits after the last one began: the larger of REDRAW_EVERY and what the last cost times REDRAW_SPACING. |
| 229 | 3 | `public double redrawCost()` | What the last redraw cost, in seconds (capped at REDRAW_COST_MAX); 0 before the first. |

### THE SPEED THE CITY IS REALLY RUNNING AT (lines 233-297)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 7 | `public String reached(String rungLabel, double rung)` | The speed the city has reached, as the header reads it between the speed's arrows in place of the rung (UP7), or null when the header reads the rung: the city keeps up (within SHORT_BY of the rung), a full SHORT_WINDO... |
| 259 | 4 | `public String reading(String rungLabel, double rung)` | What the header reads between the arrows: reached() when the city falls short, else the rung's own words. |
| 265 | 5 | `private static String reachedText(double achieved)` | A speed reached in words: whole from 1 up, one decimal under 1, then the times sign ("39×", "0.4×"). |
| 279 | 6 | `public static List<String> reachedTexts(double topRung)` | Every text reached() can return under this top rung (UP7), by the formatter reached() uses: the tenths from 0.0 up to 0.9 (a speed of .96 is written "1×", as the ladder writes 1x), and the whole numbers from 1 up to t... |
| 287 | 10 | `private void windowAdd(double seconds, double months)` | One frame into the window; the oldest frames go while what is left still spans SHORT_WINDOW. |

