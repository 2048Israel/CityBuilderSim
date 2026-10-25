# ClockCheck.java - 455 lines · 17 methods · 5 constants · harnesses

`ham/citybuildersim/ClockCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The clock's pace (UP2): a redraw is paced by what it cost, a frame spends a budget on months and
> drops what it could not run, the header's speed says when the city falls short of it, and the
> header reads only the months it draws.
> 
> WHY THIS EXISTS. The timer ran every month that was due in a frame however long they took, drew
> the open screen at a fixed 120 ms whatever the draw cost, and the header rebuilt five whole
> histories to draw the last ten years of each. None of it showed while a month and a page were
> cheap; at month 4,000 it was a third of the FX thread and a slow stretch banked time to burst
> afterwards. This runs ClockPace on a clock it moves by hand, frame by frame as the timer runs
> them, with months and redraws of chosen cost, and the windowed reads against the whole-history
> reads they stand in for. No JavaFX toolkit: it runs in the suite in the cloud as on the PC.
> 
> What it has to prove:
>   1. a redraw costing 10, 60 and 200 ms is followed by the next no sooner than
>      max(REDRAW_EVERY, cost x REDRAW_SPACING) and is due once that has passed; redraws take at
>      most a third of the time; a redraw never costs the screen more than REDRAW_COST_MAX;
>   2. a frame runs as many months as are due while they have taken under MONTH_FRAME_MS, and
>      always the first; across 600 frames at 50x with months of mixed cost none is skipped or run
>      twice, and the time worth is the months run, owed and dropped;
>   3. a 2 s stall at 50x is worth FRAME_CLAMP, leaves at most one month owed, and is not paid
>      back in a burst afterwards, with months of 5 and of 50 ms;
>   4. the speed's reading is the rung while the city keeps up and until a window has run, both
>      figures once it falls more than SHORT_BY under, the rung again after restart() or a new
>      rung, and a slow rung (0.1x) is not read as a shortfall;
>   5. the header's five series, read the windowed way, are the last SPARK_MONTHS of the
>      whole-history read, value for value, on a young city, an older one and a history past
>      MONTHLY_KEPT (folded years), and so is the GDP tile's change line.

**Uses:** [ClockPace](ClockPace.md) (38), [YearBook](YearBook.md) (8), [HistorySave](HistorySave.md) (7), [UserInterface](UserInterface.md) (4), [GameFiles](GameFiles.md) (2), [LongPlaytest](LongPlaytest.md) (2), [Game](Game.md) (1)

## Sections

| line | section |
|---:|---|
| 119 | 1. a redraw is paced by what it cost |
| 186 | 2. a frame has a budget for months |
| 247 | 3. a stall is not paid back |
| 277 | 4. the speed says when it falls short |
| 347 | 5. the header reads the window it draws |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 46 | `ClockCheck.SPM` | `UserInterface.SECONDS_PER_MONTH` | Real seconds a month takes at 1x, as the window's clock has it. |
| 49 | `ClockCheck.TOP` | `50` | The top rung. |
| 50 | `ClockCheck.TOP_LABEL` | `"50×"` |  |
| 52 | `ClockCheck.FRAME` | `1.0 / 60` |  |
| 363 | `ClockCheck.KEYS` | `{ "population", "gdp", "priceIndex", "unemployment", "policyRate" }` | The header's five keys, and the whole-history read each one stands in for. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |
| 64 | `long nanos` |  |
| 65 | `final ClockPace pace` |  |
| 66 | `double speed` |  |
| 68 | `IntToDoubleFunction monthMs` | What month number m costs, in milliseconds. |
| 69 | `long last` |  |
| 70 | `int called, returned, badFrames` |  |
| 71 | `double elapsed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 420 | **type** `public class ClockCheck` | The clock's pace (UP2): a redraw is paced by what it cost, a frame spends a budget on months and drops what it could not run, the header's speed says when the city falls short of it, and the header reads only the mont... |
| 40 | 4 | `static void check(String label, boolean ok)` |  |
| 54 | 3 | `static boolean near(double a, double b, double eps)` |  |
| 63 | 44 | **type** `static final class Sim` | The timer's frames on a clock moved by hand: each step is one frame of FRAME seconds plus whatever the work of the frame before it took (the timer's dt is the gap between two pulses, and the work is in it), months and... |
| 73 | 3 | `void advance(double ms)` _(in ClockCheck.Sim)_ |  |
| 78 | 3 | `int step()` _(in ClockCheck.Sim)_ | One frame; dt is the gap since the last. |
| 82 | 17 | `int frame(double interval)` _(in ClockCheck.Sim)_ |  |
| 101 | 5 | `void redraw(double ms)` _(in ClockCheck.Sim)_ | A redraw that costs {@code ms}, begun and ended as the timer does. |
| 108 | 10 | `public static void main(String[] args) throws Exception` |  |

### 1. a redraw is paced by what it cost (lines 119-185)

| line | len | member | says |
|---:|---:|---|---|
| 121 | 64 | `static void redraws()` |  |

### 2. a frame has a budget for months (lines 186-246)

| line | len | member | says |
|---:|---:|---|---|
| 188 | 58 | `static void months()` |  |

### 3. a stall is not paid back (lines 247-276)

| line | len | member | says |
|---:|---:|---|---|
| 249 | 27 | `static void stall()` |  |

### 4. the speed says when it falls short (lines 277-346)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 67 | `static void reading()` |  |

### 5. the header reads the window it draws (lines 347-455)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 3 | `static boolean same(double a, double b)` |  |
| 354 | 7 | `static boolean isTailOf(double[] tail, double[] whole)` | Whether the tail is the last tail.length entries of the whole, value for value, NaN where NaN. |
| 365 | 3 | `static double[] whole(HistorySave h, String key)` |  |
| 370 | 41 | `static void headerReads(String what, HistorySave h, int...tails)` | The header's reads on one history: the five tiles, the change line, and every series' tail. |
| 412 | 31 | `static void windows()` |  |
| 445 | 10 | `static double foldedValue(String name, int month)` | A value for every series of a history grown by hand: positive, different by series and month, with a hole in the price index. |

