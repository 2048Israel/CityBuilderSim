# ClockPace.java - 259 lines · 16 methods · 7 constants · model

`ham/citybuildersim/ClockPace.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The clock's pace (UP2): how much of each frame the months may take, how often the screen is
> redrawn and what the redraw cost, and what speed the city is really running at.
> 
> It holds the arithmetic of ui.UserInterface.startClock() and nothing else: pure Java, no JavaFX,
> with its time source injected so a harness can drive it with a clock it advances by hand
> (ClockCheck). The window's timer calls frame() once a frame, redrawStarted() and redrawEnded()
> around each redraw it makes, and asks redrawDue(), progress() and reading().
> 
> WHY THIS EXISTS. The timer ran every month that was due in a frame, however long they took (up
> to two and a half of them after a stalled frame at 50x), and redrew the open screen at a fixed
> 120 ms spacing that never looked at what a redraw cost. Neither cost mattered while a month and
> a page were cheap. At month 4,000 a People page took 55 ms to build and lay out, so at 50x
> redraws took nearly half of the FX thread, and a slow stretch stacked: the owed time was banked
> and paid back in a burst the frame after. So the redraw now waits three times what it cost (it
> takes at most a third of the thread), a frame stops running months when it has spent
> MONTH_FRAME_MS on them and drops the time it could not run instead of banking it, and the
> speed the header reads says when the city is running slower than the rung asked for.

**Used by (2):** [ClockCheck](ClockCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 100 | THE MONTHS A FRAME RUNS |
| 186 | HOW OFTEN THE SCREEN IS REDRAWN |
| 225 | THE SPEED THE CITY IS REALLY RUNNING AT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `ClockPace.REDRAW_EVERY` | `.12` | The least time between one redraw of the open screen and the next while the clock runs, in seconds (moved here from ui.UserInterface by UP2, its comment with it). |
| 46 | `ClockPace.REDRAW_SPACING` | `3` | How many times a redraw's cost the next one waits (UP2): 3, so redraws take at most a third of the thread. |
| 49 | `ClockPace.REDRAW_COST_MAX` | `.5` | The most a redraw is counted to have cost, in seconds (UP2): the pulse that ends the measure can be late (a minimised window), and a gap of minutes is a screen that never moves. |
| 52 | `ClockPace.MONTH_FRAME_MS` | `40` | The time one frame may spend running months, in milliseconds (UP2): the loop stops when the months run in it pass this, but the first month always runs. |
| 66 | `ClockPace.FRAME_CLAMP` | `.25` | The most real time one frame is worth to the game's clock, in seconds. |
| 69 | `ClockPace.SHORT_WINDOW` | `2` | The running time the speed achieved is read over, in seconds (UP2); until a full window has run the header reads the rung alone. |
| 72 | `ClockPace.SHORT_BY` | `.1` | How far under the rung the speed achieved must fall, as a share of the rung, before the header says so (UP2): more than a tenth. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 74 | `private final LongSupplier nanoTime` |  |
| 77 | `private double progress` | The months run in the month now under way and owed to the next, as a fraction; a month is due at 1. |
| 78 | `private double dropped` |  |
| 80 | `private double sinceRedraw` |  |
| 81 | `private double redrawCost` |  |
| 82 | `private double redrawGap` |  |
| 83 | `private long redrawBegan` |  |
| 84 | `private boolean redrawing` |  |
| 87 | `private final ArrayDeque<double[]> window` | The frames of the window: each one's real seconds and the months it advanced. |
| 88 | `private double windowSeconds, windowMonths` |  |
| 89 | `private double windowRung` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 233 | **type** `public final class ClockPace` | The clock's pace (UP2): how much of each frame the months may take, how often the screen is redrawn and what the redraw cost, and what speed the city is really running at. |
| 91 | 3 | `public ClockPace()` |  |
| 96 | 3 | `public ClockPace(LongSupplier nanoTime)` |  |

### THE MONTHS A FRAME RUNS (lines 100-185)

| line | len | member | says |
|---:|---:|---|---|
| 118 | 37 | `public int frame(double dt, double speed, double secondsPerMonth, BooleanSupplier month)` | One frame of the clock while it runs: adds the share of a month the frame is worth (clamped to FRAME_CLAMP), then runs {@code month} while a month is due and the months run in this frame have taken under MONTH_FRAME_M... |
| 157 | 3 | `public void idle(double dt)` | A frame of a clock that is not running (paused, or a menu or dialog up): only the redraw's time moves. |
| 162 | 3 | `public double progress()` | The share of the next month the clock has run, 0 up to 1 (1 while a month is owed that a slow frame could not run): what the date's day is read from. |
| 167 | 3 | `public double droppedMonths()` | The months of owed time that were dropped, not run, since the pace was made: a stalled frame past FRAME_CLAMP, what a slow frame could not fit, what a stop zeroed. |
| 172 | 5 | `public void stopped()` | The clock stopped itself or the city was closed: nothing is owed, and the speed's window starts again. |
| 179 | 6 | `public void restart()` | The speed's window starts again (the clock starts, the speed changes, another city opens); the progress stands. |

### HOW OFTEN THE SCREEN IS REDRAWN (lines 186-224)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 3 | `public boolean redrawDue()` | Whether the gap since the last redraw began has passed: a redraw that is pending may be drawn. |
| 196 | 5 | `public void redrawStarted()` | A redraw of the open screen begins: the time since the last starts again, and its cost is measured from here. |
| 203 | 3 | `public boolean redrawing()` | Whether a redraw began and its cost has not been taken (redrawEnded()). |
| 208 | 6 | `public void redrawEnded()` | The redraw that began is over - drawn, and CSS and layout done: its cost sets the gap before the next. |
| 216 | 3 | `public double redrawGap()` | The seconds the next redraw waits after the last one began: the larger of REDRAW_EVERY and what the last cost times REDRAW_SPACING. |
| 221 | 3 | `public double redrawCost()` | What the last redraw cost, in seconds (capped at REDRAW_COST_MAX); 0 before the first. |

### THE SPEED THE CITY IS REALLY RUNNING AT (lines 225-259)

| line | len | member | says |
|---:|---:|---|---|
| 239 | 8 | `public String reading(String rungLabel, double rung)` | What the header reads between the speed's arrows: the rung alone - {@code rungLabel}, as the header writes it - while the city keeps up or until a full SHORT_WINDOW of running time has passed since the window began; a... |
| 249 | 10 | `private void windowAdd(double seconds, double months)` | One frame into the window; the oldest frames go while what is left still spans SHORT_WINDOW. |

