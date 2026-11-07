# ClockCheck.java - 583 lines · 21 methods · 6 constants · harnesses

`ham/citybuildersim/ClockCheck.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

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
>   4. the speed's reading is the rung while the city keeps up and until a window has run, the
>      speed reached ALONE (UP7: "31×", never "50× · 31×") once it falls more than SHORT_BY
>      under, the rung again after restart() or a new rung, and a slow rung (0.1x) is not read
>      as a shortfall; reached() is null exactly when the reading is the rung;
>   5. the header's five series, read the windowed way, are the last SPARK_MONTHS of the
>      whole-history read, value for value, on a young city, an older one and a history past
>      MONTHLY_KEPT (folded years), and so is the GDP tile's change line;
>   6. a stalled frame is not a shortfall (UP7): after one frame of half a second, and one of
>      under FRAME_CLAMP, the reading is the rung at 1x and at 50x with cheap months, and a city
>      really short stays short across the same stall; a speed that prints as the rung's own
>      words ("2×" reached at 1.75 of a rung of 2) is not a shortfall, one that rounds apart is;
>      and every text reached anywhere above is one of ClockPace.reachedTexts().

**Uses:** [ClockPace](ClockPace.md) (63), [YearBook](YearBook.md) (8), [HistorySave](HistorySave.md) (7), [UserInterface](UserInterface.md) (4), [GameFiles](GameFiles.md) (2), [LongPlaytest](LongPlaytest.md) (2), [Game](Game.md) (1)

**Used by (1):** [KeptCheck](KeptCheck.md)

## Sections

| line | section |
|---:|---|
| 126 | 1. a redraw is paced by what it cost |
| 193 | 2. a frame has a budget for months |
| 254 | 3. a stall is not paid back |
| 284 | 4. the speed says when it falls short |
| 375 | 5. the header reads the window it draws |
| 484 | 6. a stalled frame is not a shortfall, and a speed that prints as the rung is not one |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 52 | `ClockCheck.SPM` | `UserInterface.SECONDS_PER_MONTH` | Real seconds a month takes at 1x, as the window's clock has it. |
| 55 | `ClockCheck.TOP` | `50` | The top rung. |
| 56 | `ClockCheck.TOP_LABEL` | `"50×"` |  |
| 58 | `ClockCheck.FRAME` | `1.0 / 60` |  |
| 287 | `ClockCheck.SEEN` | `new java.util.TreeSet<>()` | Every text the pace said it reached in this run: section 6 holds them against the list reachedTexts() gives. |
| 391 | `ClockCheck.KEYS` | `{ "population", "gdp", "priceIndex", "unemployment", "policyRate" }` | The header's five keys, and the whole-history read each one stands in for. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `static int fails` |  |
| 70 | `long nanos` |  |
| 71 | `final ClockPace pace` |  |
| 72 | `double speed` |  |
| 74 | `IntToDoubleFunction monthMs` | What month number m costs, in milliseconds. |
| 75 | `long last` |  |
| 76 | `int called, returned, badFrames` |  |
| 77 | `double elapsed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 542 | **type** `public class ClockCheck` | The clock's pace (UP2): a redraw is paced by what it cost, a frame spends a budget on months and drops what it could not run, the header's speed says when the city falls short of it, and the header reads only the mont... |
| 46 | 4 | `static void check(String label, boolean ok)` |  |
| 60 | 3 | `static boolean near(double a, double b, double eps)` |  |
| 69 | 44 | **type** `static final class Sim` | The timer's frames on a clock moved by hand: each step is one frame of FRAME seconds plus whatever the work of the frame before it took (the timer's dt is the gap between two pulses, and the work is in it), months and... |
| 79 | 3 | `void advance(double ms)` _(in ClockCheck.Sim)_ |  |
| 84 | 3 | `int step()` _(in ClockCheck.Sim)_ | One frame; dt is the gap since the last. |
| 88 | 17 | `int frame(double interval)` _(in ClockCheck.Sim)_ |  |
| 107 | 5 | `void redraw(double ms)` _(in ClockCheck.Sim)_ | A redraw that costs {@code ms}, begun and ended as the timer does. |
| 114 | 11 | `public static void main(String[] args) throws Exception` |  |

### 1. a redraw is paced by what it cost (lines 126-192)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 64 | `static void redraws()` |  |

### 2. a frame has a budget for months (lines 193-253)

| line | len | member | says |
|---:|---:|---|---|
| 195 | 58 | `static void months()` |  |

### 3. a stall is not paid back (lines 254-283)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 27 | `static void stall()` |  |

### 4. the speed says when it falls short (lines 284-374)

| line | len | member | says |
|---:|---:|---|---|
| 290 | 5 | `static String reachedOf(ClockPace pace, String label, double rung)` | The speed reached (ClockPace.reached), noted in SEEN when there is one. |
| 297 | 3 | `static boolean readsRung(ClockPace pace, String label, double rung)` | Whether the pace reads the rung's own words: reading() is the rung and reached() says nothing. |
| 302 | 4 | `static boolean readsReached(ClockPace pace, String label, double rung)` | Whether the pace reads a speed reached, alone: reading() is reached(), which is not the rung's words. |
| 307 | 66 | `static void reading()` |  |

### 5. the header reads the window it draws (lines 375-483)

| line | len | member | says |
|---:|---:|---|---|
| 377 | 3 | `static boolean same(double a, double b)` |  |
| 382 | 7 | `static boolean isTailOf(double[] tail, double[] whole)` | Whether the tail is the last tail.length entries of the whole, value for value, NaN where NaN. |
| 393 | 3 | `static double[] whole(HistorySave h, String key)` |  |
| 398 | 41 | `static void headerReads(String what, HistorySave h, int...tails)` | The header's reads on one history: the five tiles, the change line, and every series' tail. |
| 440 | 31 | `static void windows()` |  |
| 473 | 10 | `static double foldedValue(String name, int month)` | A value for every series of a history grown by hand: positive, different by series and month, with a hole in the price index. |

### 6. a stalled frame is not a shortfall, and a speed that prints as the rung is not one (lines 484-583)

| line | len | member | says |
|---:|---:|---|---|
| 486 | 97 | `static void stallAndPair()` |  |

