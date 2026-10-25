# FW10 - the full-screen chart's foot stays on the screen (0.8.50, 2026-10-25)

The brief: `QUEUE.md`'s row, from `docs/notes/todo-archive.md` (FOUND ON THE
WAY IN 0.7.40 AND 0.7.41; `runs/ui21-notes.md` §8, not in the tree): City
History's big chart in full screen (P), with many lines picked, is a row taller
than the window and its foot is cut, because `TimeChart.heightBesidePlot()`
assumes a one-row toolbar. Measure the toolbar once the legend has wrapped and
size the plot from it; a check red on main; renders before and after. The
model is untouched.

## 1. What was there

`heightBesidePlot()` summed constants: the padding, a toolbar of 34 px, the
lanes, the overview, a caption of 18. `fitFull()` gave the plot the window's
height less that sum. Laid out (ChartFitCheck's bench, below), one toolbar row
is 27 px, and each row the legend wraps to adds about 29 (a chip and the
flow's gap). So the sum's 7 spare pixels hid a second row, and the foot was cut
from the third:

| window | 2 legend rows from | 3 rows from | on main, worst |
|---|---|---|---|
| 1280x720 | 4 lines | 7 lines | 16 lines, 6 rows: caption ends at 763 of 720 |
| 1600x900 | 5 lines | 9 lines | 9 lines: chart ends at 910 of 900 |
| 1920x1080 | 7 lines | 13 lines | 13 lines: past the window |
| 2560x1440 | 10 lines | not by 16 | 16 lines lay out 2 rows: inside |

With one row the chart stopped 22 px above the window's foot where the screen
means 14 (the pane's bottom padding and `SLACK`): 8 px of plot unused.

## 2. What changed

- **`TimeChart.heightBesidePlot()` is measured**: with the legend's wrap length
  set for the width and the CSS applied (when in a scene), it is the chart's
  own `prefHeight` at the width it is laid out at, less the plot's height:
  the toolbar as it wraps, the lanes, the overview, the caption, the border
  and the spacing, as JavaFX lays them.
- **`TimeChart.fitTo(width, height)`**: the width first (it sets the wrap),
  then the measure, then the plot whatever the height leaves; one draw.
  `layoutCanvases()` is now `geometry()` and `draw()`, so `fitTo` measures
  between them. `setSize()` and the page's sizing are as they were.
- **`HistoryScreen`**: the full-screen pane's padding is three dials
  (`FULL_PAD_TOP`, `FULL_PAD_SIDE`, `FULL_PAD_BOTTOM`, 14, 22 and 12 as
  before); `newFullPane()`, `newFullTitle()`, `newFullClock()` and
  `fitToWindow()` hold what `setChartFull()` and `fitFull()` did inline, so
  the screen and the check run one code. `fitToWindow()` calls `fitTo()`.

## 3. The check

`ChartFitCheck` (new, in `AllChecks` after `KeptCheck`, skipped like it where
JavaFX is not on the classpath) lays the full-screen chart out on a scene of
its own through `ui/ChartFitBench` - the screen's own pane, title, clock and
`fitToWindow()`, the theme's CSS - at 1280x720, 1600x900, 1920x1080 and
2560x1440, with 2 to 16 lines of City History-length names. Its sections:
1, the fixture wraps the legend (one row at 2 lines, two and three rows at
the counts it finds); 2, the caption's and the chart's bottoms are inside the
window at 2 lines, at the first counts that wrap to two and three rows, and at
16; 3, the chart ends where the screen means it to (the room below it within
`FULL_PAD_BOTTOM + SLACK` + 1 px) and the plot loses to a wrapped legend what
the toolbar grew (within 1 px); 4, the renders, with `-Dchartfit.out=DIR`.

Run against main's sizing (the bench and the screen's statics in, the old
`heightBesidePlot()` still in place): **exit 23**, section 2 red at every
3-row and 16-line count (e.g. `1280x720, 7 lines, 3 legend rows: the chart's
bottom is inside the window - chart bottom 730.0 against window 720.0`) and
section 3 red everywhere (one row: 22 px left below the chart where 14 is
meant; wrapped: the plot lost 0 where the toolbar grew 54). On the fix: exit 0.

## 4. The renders

`docs/queue/FW10/before/` and `after/`: three pairs of the fifteen the check
writes (1280x720 at 7 lines and 16, 1600x900 at 9), the others left out for
their size (9 MB in all). A "before" picture is taller than its window: the
scene's snapshot takes the chart as laid out, so the rows below 720 (or 900)
are the foot the screen cut. The "after" pictures are the window's size, the
overview strip and the caption on screen.

## 5. Checks

The model is untouched, so the playtest's trace is unchanged and there is no
ensemble. `./batch-check.sh --pick KeptCheck`, earth-2.bin named: compile OK,
`ChartFitCheck` and `KeptCheck` OK (`--pick` picked only ChartFitCheck; City
History is not a kept page, and KeptCheck was run to be sure), the maps
regenerated, Stale's one firm finding mended (CLAUDE.md's count of the
interface's files, thirty-three, now thirty-four with the bench), then
`--fast` clean. The README's harness count and `docs/tree.md` carry the new
harness and bench. `QUEUE.md` is left to `ship.sh`, which deletes the row at
the merge (BP1).

## 6. Calls

None. The page's own chart (not full screen) is sized as before; its legend
wraps over the page's height, which scrolls.
