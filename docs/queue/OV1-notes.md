# OV1 - a well that opens the month two wear out keeps its own month ({next}, 2026-11-01)

Jerus, 2026-10-31: "Ship, OV1 next", with LD1 shipped over WellCheck 4's three red lines. The brief is OV1's row and
`docs/queue/LD1-notes.md` §7. A small fix batch, done by one session with no slices.

## 1. The fault

`Oil.view()` reads the wells standing against the stored vintages as one figure: it drops the oldest vintages by the
wells that fell and opens a vintage of the month for the wells that rose, from their difference alone. Two wells worn
out and one opened in a month is a fall of one, so one old vintage went and the fresh well was never recorded. The old
well went on standing in the book, lifting at its worn-out profile, and was retired again the month after, the fresh
well gone from the count with it. Latent on main; LD1's path meets it in WellCheck 4.

## 2. The fix, in `Game.retireWornOutWells()`

The vintages are struck twice around the retirements, so the two never net against each other:

- **before the loop**: a well that opened earlier in the month (the builders' completions come first) is a vintage of
  its month before the retirement drops the old ones;
- **after each kind's retirement** (`if (gone > 0)`): a well that opens later in the month is not netted against it.

**The brief's one line is half of it.** The line LD1's slice found, `if (gone > 0) wells.strikeVintages();`, mends
WellCheck 4 and leaves the other order red. WellCheck section 16 (new) causes that order on its own, five wells on
five sites with two a month short of their life, no order or hold in the way, and the vintages after the month read
`[(-261, 1), (-99, 2)]` under the line alone: the fresh well missing, one old well kept. Each strike alone, run on the
branch:

| strike | WellCheck 4 | WellCheck 16 |
|---|---|---|
| neither (main) | 3 red | 2 red |
| after only (the brief's line) | green | 2 red |
| before only | 3 red | green |
| both | green | green |

## 3. The checks

- **WellCheck** is green on the branch, all sixteen sections; 4's three lines are mended without a line changed.
- **OilCheck, OilViewCheck, ReadPathCheck and SaveFileCheck** pass. Nothing is saved that was not (the vintages are the
  Oil sector's extras already), so no format bump.
- **`./batch-check.sh --pick`** (WellCheck, TimingCheck, LongPlaytest; `Game.java` is left to the gate's suite) exits 0.
- **The default playtest's trace is main's**: LongPlaytest on main's classes (2f12df44) and on the branch, 4,001 months,
  differ in two lines only, its run time and the seconds the layout watch took. No ensemble ran.
- **The record**: `docs/notes/gate.txt` lists WellCheck's three lines as main's reds passed on Jerus's word; the gate's
  next record has none of them.
