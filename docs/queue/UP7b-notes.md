# UP7b - the clock counts the stall its own redraw made; the pulse listener reads the pace field ({next}, 2026-11-07)

**WHERE IT STANDS** (2026-11-07): code in, committed (969f7c42) and pushed on `batch/UP7b`, built on `origin/batch/XI2` (RL1: its
lane's predecessor, ship file there, not on main yet). `--pick` on that tip: 7 of 186 harnesses, exit 3, main's recorded MapCheck and
KeptCheck reds only; two timing assertions soft. The ship file waits for XI2 to land on main and be merged in (`docs/batch.md` 7).

UP7's gate review, as the `QUEUE.md` row had it. Run on Sonnet whole (a small fix batch, `docs/batch.md` 3): no slice.

## 1. The two items

| item | how |
|---|---|
| a stall that keeps coming back stayed white | `ClockPace.frame()` counted every frame over its clamped share (UP7). A redraw costing c > FRAME_CLAMP, due every max(3c, 1.5 s), makes the frame after it a stall of c; the city loses c - FRAME_CLAMP of game time each cycle (c = 0.5 s: 0.27 s in 1.5 s, the city at 82% of the rung) and the window, counting the share alone, read the rung. Now `redrawStarted()` sets `afterRedraw`; the next `frame()` counts `share + min(dt - share, redrawCost)` seconds and clears it, so the stall the redraw is measured to have made is counted whole, and any stall past it (a minimised window after a cheap redraw) stays clamped, as a stall nobody made was in UP7. The brief said the raw dt; the redraw's measured cost (at most REDRAW_COST_MAX) is the raw dt bounded so a restored window cannot read "0×" for two seconds. `idle()` clears the flag: a redraw begun while the clock stood still is no part of the running time. |
| `pace::redrawEnded` binds the pace of the clock's start | `startClock()` adds the post-layout listener once, and `UserInterface.pace` is no longer final (KeptBench swaps it, UP7). `() -> pace.redrawEnded()` reads the field when the pulse fires. |

## 2. The threading, for the gate's review

Nothing new: the listener and `ClockPace.frame()`/`idle()`/`redrawStarted()`/`redrawEnded()` all run on the FX thread, as before; the
new field `afterRedraw` is read and written there alone. No saved field, no model change, no format bump. The default playtest never
draws the window, so its trace cannot move: no ensemble (EN1).

## 3. What checks it

- **ClockCheck 7** (new), on the hand-moved clock: (a) a redraw of 0.5 s whenever one is due, at 1x and 50x: the fixture measures the
  city at 82% of the rung (a cycle loses 18%, over SHORT_BY, from the model's own constants) and the reading is a speed reached at
  each of ~340 frames once a window has run (red on the old accounting at every such frame); (b) a redraw of 0.8 FRAME_CLAMP: the city
  at 100%, the reading the rung at every frame; (c) a 10 ms redraw then a 30 s stall: the rung throughout; (d) a redraw begun with
  the clock stopped, then a 1 s stall: the rung (red when `idle()` does not clear the flag).
- Both mutations run: the redraw's cost left out of `frame()` fails 7(a); `idle()` not clearing fails 7(d); the rest of the harness is
  green either way.
- **`--pick`** on the tip (5d10419a XI2 + this): exit 3, as above. Stale 0 firm.

## 4. For Jerus

No ★ calls. To look at, only if something seems off: with a heavy page open at 50x the header's reading turns amber when the redraws
themselves keep the city under nine tenths of the rung; before, it stayed white.
