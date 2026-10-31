# KP1 - Screens kept: City History ({next}, 2026-10-31)

The batch's record. The study's KP1 row (`docs/studies/spec-screens-in-place.md` §7) with §3 (the pattern and its
five rules) and §6 (what checks it), and Jerus's calls on UP4 (2026-10-23): ★2 "Queue KP1-KP7", ★1 "Default order is
fine" (History first), ★4 "Count up at 1x-10x only". Lane B's fourth and last batch of the night line, after LB1-LB3;
it does not use the Rankings batches' code. Opus wrote the two briefs, reviewed each diff and committed; two Sonnet
5.5 slices at xhigh wrote the conversion (A) and the bench and harness (B). Nothing outside `ui/` and `KeptCheck`
changed, so the default playtest's trace cannot move: no ensemble (EN1).

## 1. What is kept now

- **City History's page** is drawn through `UserInterface.keepMenu()` from `HistoryScreen.pool`:
  `showHistoryMenu()` keeps what it did before and after drawing (the drag guard, the filter's focus, the pressed
  chip held in place, the scroll target) and the page moved into `drawPage(fresh)`, which the bench can run kept or
  built new. Every node on it comes through `Kept`; none is made with `new`.
- **Each part is a scope keyed by what it shows**: the head, the year book's card and each of its lines, each strip
  cell (by label), each pin card (by side), the chart controls, the layers key, each reading card (by its line),
  each hard time (by `episodeKey`) and each decision (`decisionKey`: month, kind and words), each picker group (by
  name), the prices. A line picked, a hard time begun or a decision taken slides no other row onto its neighbour's
  nodes.
- **The charts stay in the scene.** The big chart and the two pins' charts were already made once and told their
  data by `setData()`; what lost them their crosshair every month was the page torn down around them (`TimeChart`
  clears its hover when its scene goes null). In kept boxes they never leave, so their window, zoom and crosshair
  hold across months. **The legend's chips and the range buttons** are kept too, in the chart's own pool
  (`TimeChart.chipPool`, a draw nested in the page's), each chip a scope keyed by its line: a chip under the pointer
  keeps its hover and its tooltip as the month lands.
- **The frame over the scroller**: `UserInterface.scrolled()` hands back its content box and scroller inside a kept
  draw (the from-bottom twin of the panels' `keptScroller`), and every binding and listener on them is made once per
  scroller, frame and body. Outside a draw it is unchanged for every other screen.
- **Pictures told their figures**: `Pieces.RangeBar` (`set()`; its dot's slide through `Kept.play`);
  `ActionButton` (`retell()`, the hand cursor the hand-back takes given back); `CardHead` a box that stages its
  children. `tag`, `door`, `details` and `chartCardHead` come through `Kept`; `door`'s click is set, not added.
- **A graphic handed where its label is made** (`Kept.label(text, graphic)`): `pageHead`'s swatch,
  `constructionHead`'s and `chipStrip`'s icons were stripped and given back each draw, which took them off the
  scene and restyled them - the People pilot's and UP2's code as well as History's (KeptCheck's new watch saw 50
  such moves with the old pairs, 0 now).

## 2. What is still rebuilt

- **`Pieces.ScaleRows`** inside History's two folds (every hard time by kind, every good's price), both closed by
  default: it builds every part in its constructor and five other screens use it. Left for KP2, where
  Infrastructure reaches it four times (★ B below).
- **The crosshair's card** (`TimeChart.crosshairCard`) is rebuilt on each pointer move, as before: not on the clock.
- **The full-screen pane** is made once, outside any draw, as before.

## 3. The count-up only at 1x-10x (★4)

`UserInterface.COUNT_UP_TOP` (10) and `countsUp()`: a month that lands while the clock runs faster than 10x sets
its figures at once; paused (a month stepped by hand) it counts at any speed. People's headcount and its month's
bars, and History's pins and reading cards (and the range bars' dots) obey it. A count still running when the clock
is sped past 10x is stopped at the next month, on both pages, so it cannot write the month before's figure over the
new one. Sectors' count-up is not on a kept page yet and is unchanged (★ C).

## 4. What checks it

`KeptCheck` sections 14-20, on its city at `CITY_MONTHS`, beside People's 1-5 (95 assertions, all pass):
14 the page kept (top nodes 25 of 25 months; first draw 366 nodes, 368 handed back a month, 24 of 25 months make
none; the three charts, the legend chips and the range buttons in the scene in the same boxes; no graphic moves);
15 the signature equals a page built new for `MONTHS` months; 16 the same pixels at three months, then the spoil
proof; 17 29 choices that reshape the page (picks, presets, groups, the filter, both folds, log, layers, a hidden
line, the window moved to an episode and a range, pin and unpin, full screen, the year book's card), each redraw
after them equal to a page built new; 18 a window put by hand stays across a month, the crosshair (a real
`MOUSE_MOVED` on the plot, read through `TimeChart.hovering()`) survives a kept redraw and a page built new clears
it, a hovered legend chip keeps its identity, hover and tooltip; 19 at each of the ladder's 9 rungs and paused, People's
headcount and a History pin count up exactly where `countsUp()` allows, and a running count is stopped past the top;
20 the kept month within the new dial `KEPT_HISTORY_MS` (13 ms: medians 5.0-8.2 ms over five runs on a loaded PC,
doubled; built new 20.6-26.3 ms). The bench (`KeptBench`'s History section) gives the window a stand-in
`menuScroller` and `windowStack`. The year book's card is faked by its said line only: no file is written.

## 5. Found in review

- By the slices: the three graphic pairs (§1); `ActionButton`'s cursor reset by the hand-back; a page built new needs
  several layout passes to settle, so the bench's `settle()` runs passes until one changes nothing.
- By Opus: a legend chip's click redrew the legend alone from the chart's pool, which let go of the range buttons
  (a draw lets go of what it does not ask for): it redraws both now. People had no stop for a count still running
  past 10x (History had one): `PeopleScreen.headcount()` and the stop added.

## 6. The ★ calls, taken the recommended way

- **A. The count-up rule on History too.** Jerus's answer was about People's LIVING HERE, but the reason (at 50x the
  600 ms count never ends and relays every frame) holds for every figure that counts on a month; History's pins and
  cards count the same way. Taken: both pages.
- **B. `ScaleRows` left for KP2.** Making it a picture touches five screens; History reaches it only inside two
  folds closed by default, Infrastructure four times on its pages. Taken: KP2 does it (pending KP1-N).
- **C. Sectors' count-up at 1x-10x in KP4**, when Sectors is converted: it is a one-line change there, and KP4 owns
  the screen (pending KP1-N).

## 7. For the eye

A kept from-bottom scroller restores its position only when it is new: a row above the view that changes height as a
month lands (a new hard time, a decision) while the player is scrolled down may shift the view a little; a click is
still held in place (`holdInPlace`). Pending KP1-C.
