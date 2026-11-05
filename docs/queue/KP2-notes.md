# KP2 - Screens kept: Land, Services and Infrastructure; the controls' rule; KP1's hover kept ({next}, 2026-11-04)

**WHERE IT STANDS** (2026-11-04, late): the five slices are in and reviewed; KeptCheck passes whole (276), and the
24 harnesses KP2 picks against BA1's tip are green but for BA1's own two (LongPlaytest's layout ring, passed by name
at BA1's gate; CapitalFlowCheck's fixture, mended on BA1's branch after KP2 branched). Waiting for BA1 to land, then
`origin/main` merged, the picked harnesses again, and the ship file last.

The batch's record. The study's KP2 row (`docs/studies/spec-screens-in-place.md` §7) with §3 (the pattern and its
five rules) and §6 (what checks it); `QUEUE.md`'s row adds KP1's gate review (a kept row under a still pointer loses
its hover look) and `Pieces.ScaleRows` a picture (KP1-N, `docs/queue/KP1-notes.md` §6 B). Lane 2 of the night line,
after BA1, built on `origin/batch/BA1` at its ship file (the orchestrator, 21:17). Opus writes the briefs, reviews
each diff and commits; Sonnet 5.5 slices at xhigh write the code. Nothing outside `ui/` and `KeptCheck` changes, so
the default playtest's trace cannot move: no ensemble (EN1).

## 1. What it builds

- **The three screens kept**, whole classes, every page and sub-page: `InfrastructureScreen` (roads, transit, the
  freight band with its haulage and rail divisions, ports; `FlowCurve` a picture told its figures), `ServicesScreen`
  (health with its care pages, education with its pipeline, ladder and courses, utilities, safety, and the books),
  `LandScreen` (the office, the funding page and the page for when the city fell short; a scope per offer; the map is
  UP3's `MapView`, a node of its own the page holds in a field, and stays as it is). Each draws through
  `UserInterface.keepMenu()` from its own `Kept.Pool`, its page in a `drawPage(...)` the bench can run kept or built
  new, the frame over its scroller bound once as History's `frameOver()` is.
- **The `Pieces` and `Statement` helpers and pictures they reach**, converted as KP1 did its own: `ScaleRows` a
  picture told its rows (`set()`), with every other picture the three pages reach.
- **The controls' rule** (sliders and fields on a kept page, §3): a kept control keeps the player's hand on it.
  `Ladder` (the fare on Infrastructure; every Policy dial and two on Build use it, unkept until KP3/KP6) makes its
  slider through `Kept`; its value is set only where it differs and never while the player holds it; its listener is
  added once and reads the ladder of the latest draw when it fires; `Levers.dialCard` fills its effects at the value
  the ladder shows (the thumb, while held). A field's text is set only where it differs and never while it has the
  focus. The rule goes in `Kept`'s javadoc as its sixth and seventh.
- **KP1's hover kept**: a style a hover changes is chosen from `isHover()` where it is set (`HistoryScreen.rowGoes`,
  `Pieces.limitCell`, `door`, `chartCardHead`, and every hover on the three pages).
- **`KeptBench` and `KeptCheck`**: the three pages beside People and History, a section per page and its choices,
  the controls held across a month, the hover across a month, a `KEPT_*_MS` dial for each page.

## 2. The slices

| slice | what | files |
|---|---|---|
| A | the controls, the hover, the `Pieces`/`Statement` helpers and pictures the three pages reach | `Kept`, `Ladder`, `Levers`, `PolicyScreen` (applyBar), `UserInterface` (keepMenu's focus), `Pieces`, `Statement`, `HistoryScreen` (rowGoes) |
| B | Infrastructure kept | `InfrastructureScreen` |
| C | Services kept | `ServicesScreen` |
| D | Land kept | `LandScreen` |
| E | the bench and the harness, after A-D | `KeptBench`, `KeptCheck` |

A-D touch no file in common, so they run side by side, each in a worktree of its own; their diffs are applied here
after review. E finds what a page built new shows that the kept page does not, and mends the page it is in.

## 3. The ★ calls, taken the recommended way

- **★A. A field keeps the focus across a kept redraw.** `keepMenu()` moves the focus off a kept page at each redraw
  (the pilot's fix for a chip clicked keeping a focus ring). A text field that has it is the player typing: it keeps
  it, its text and caret untouched. No field is on KP2's three pages; History's filter is the one on a kept page now
  and restores its focus itself, which stays. *Recommended and taken:* the rule settles here, for KP3's, KP5's and
  KP6's fields. A slider keeps nothing of the focus: a drag goes on whoever has the focus.
- **★B. A slider held while a month lands shows the thumb.** The reading and the dial card's "what it would do"
  follow the thumb, as they do while it is dragged, and not the staged value a redraw would otherwise put back.
  *Recommended and taken:* the redraw must not move what the hand holds.
- **★C. Land's funding page and fell-short page kept too.** §7 says whole screen classes, so no screen is half kept.
  *Recommended and taken.*

## 4. The threading, for the gate's review

**One thread.** The clock is an `AnimationTimer`; the month, every redraw, every handler and listener below run on
the FX thread, and `Kept.drawing` is a static only that thread touches. Nothing in KP2 runs elsewhere, so what a
reviewer weighs is re-entrancy - what runs inside a draw - and what a kept node carries from one draw to the next.

- **A draw and what it calls.** `keepMenu()` -> `Kept.draw(pool, page)`. A draw of another pool inside it (the
  chrome's) saves and restores the outer (UP2); the page's `ui.scrolled()` hands back its scroller from the same
  draw. Kept's boxes set their real children after the page's code returns, with `drawing` null.
- **What fires during a draw.** `Ladder.build()` sets the kept slider's range and value only where they differ
  (`settle()`: widened, the value, then narrowed, so no passing range clamps it). That fires the slider's value
  listener, which returns at once while `Kept.drawing()`: a redraw never calls the dial card's `track`, never
  restages, never redraws inside a draw.
- **What fires between draws.** A drag: the listener, added once (`Kept.isNew`), reads the ladder of the LATEST
  build from the slider's properties (`LADDER`, `READING_LABEL`), sets that reading and calls that ladder's `track` -
  the dial card's `fill`, which empties the card's effects column and refills it with plain new rows (no draw is
  on). The next draw hands back the column's own rows; the drag's are dropped when it commits.
- **The press and the release.** `Kept.grip()` adds two event filters and a scene listener once in the node's life
  (a marker in its properties). The press filter sets `held` before the slider's own handlers move the thumb; the
  release filter clears it before the slider's `onMouseReleased` commits -> `PolicyScreen` stages -> `ui.redraw()` ->
  a kept draw, which is then free to set the value (the staged, snapped one). A node that leaves the scene mid-press
  hears no release, so the scene listener lets it go. A slider also counts as held while `isValueChanging()`.
- **A month landing mid-drag** (★B): the timer's tick -> the month -> `redraw` -> a kept draw in which
  `Kept.held(bar)` is true: the range and the thumb are left alone, the reading says the thumb's snapped value as
  the listener would, and `Levers.dialCard` fills its effects at `Ladder.shown()`, the thumb. The slider never
  leaves the scene, so the drag's next events reach the same node.
- **The focus** (★A): `keepMenu()` moves the focus off a kept page at each redraw, as before, unless the focus
  owner is a `TextInputControl`; `Kept.text()` never sets a field that has the focus.
- **Added once:** the Ladder's listener; `grip`'s filters and listener; each page's width binding (a marker per
  scroller, as KP1's `frameOver`); `FlowCurve`'s tooltips (its constructor). The `setOnXxx` handlers Kept clears on
  a node handed back are set by the pages every draw.
- **The model** is read through the same getters as before, and written only where the player's controls wrote it
  before (`PolicyScreen`'s staged set and its Apply).

## 5. What checks it

`KeptCheck` sections 21-35, on its city at `CITY_MONTHS`, after People's 1-5, the chrome's and History's 14-20
(276 assertions, all pass; 60 s through `batch-check.sh`). For each of Infrastructure (21-24), Services (25-28) and
the land office (29-32): the page kept on its home page for `MONTHS` months (top nodes stay, nine in ten handed back
- Infrastructure 187 of 187, Services 246 of 246 - most months make none); its signature equal to a page built new
every month, the same pixels at three, the spoil proof; every page, division, sub-page and fold in turn (a fold
opened must add lines), the redraw after it and the one after a month equal to a page built new; the kept month
within its dial. 33: the funding page (the city bought down through `Game.buyLandParcels` until it falls short) and
the fell-short page, with a bond and with the vault's dollars. 34: the fare's slider pressed is held, a month leaves
it the same node with the thumb, `Ladder.shown()` and the reading at the thumb; released, the fare staged at the
thumb and the page equal to one built new; a value set outside a draw reaches the latest draw's reading; History's
filter keeps its node, text, caret and focus across a month (the focus forced through `Node.setFocused`: the bench's
windowless scene says no window has it), and `Kept.text` leaves a focused field and sets one without. 35: History's
`rowGoes` row and `chartCardHead`, a `limitCell` with a click, Land's place row and ore card, Services' `doorCard`,
hovered, keep the style their own `onMouseEntered` sets across a kept draw, and the exit's once the hover is off
(History had no door that month: said, not checked). The slice broke four things by hand to see them caught -
a door's hover, the land row's hover, `Kept.held` ignored, Services' scope key made constant - and 35, 35, 34 and
27 failed on them.

| dial | value | kept, five runs (median) | built new |
|---|---:|---|---|
| `KEPT_INFRASTRUCTURE_MS` | 9 | 2.7-6.3 ms (4.4) | 10.2-19.1 ms |
| `KEPT_SERVICES_MS` | 13 | 4.8-10.5 ms (6.5) | 9.9-19.5 ms |
| `KEPT_LAND_MS` | 5 | 2.1-3.3 ms (2.4) | 9.1-16.0 ms |

Each is twice the median, rounded up (KP1's way). The harness's mends in the pages' code: `ScaleRows` makes afresh a
part whose words come or go (an emptied `Label` keeps its `Text`'s old bounds, which History's goods' fold showed:
a hidden tag 22 px tall kept against 17 built new); `CauseBar`'s hidden names and keys rest at the origin;
`ActionButton`'s line under its words exists only while it has some.

## 7. Left, and for the eye

- **Still made new each month on a kept page**, correct but not kept: the land office's landmass table's cells
  (`SectorScreen`'s oil helpers, KP4's file; only on a city with ground on two landmasses), and whatever
  `BuildScreen.verdict`, `arc` and `goAhead` make where the pages call them (KP3's file). Pending KP2-N.
- `ScaleRows`' hidden "empty" label and hidden rule lines are not laid out while hidden (the class of `CauseBar`'s
  mend); no fixture has shown them differ.
- Infrastructure's pictures part in one 13 x 13 box (126 pixels) at two of three months, inside `alike()`: an
  antialiased edge, as People's household tiles.
- For the eye: the fare dragged on Infrastructure while the clock runs at 50x - the thumb stays under the hand, the
  reading and "what it would do" follow it, the release stages it.

## 6. Found in review

- **By the slices:** a page opened from its chip or a vitals cell went to its top only because the rebuilt page
  had a new scroller; a kept one stays, so `open()` on Infrastructure (`toTop`) and `open()`/`openPage()` on Services
  (`atTop`) send it to the top after the draw. Services' `details()` takes its insides as a supplier: a closed fold
  builds nothing (it built every table every month). Services' `lane()` takes a list: a kept row and the flow
  would have taken each other's children. Land's `MapView` is made before the draw (`ensureMap()`), so its own
  icon is not in the hero's pool. `words()`/`figure()` on both screens set wrap and minimum width on both sides.
- **By Opus:** a gripped node let go with its scene (a stuck `held` would leave a slider never set again);
  Infrastructure's and Services' pools forgotten with the city in `anotherCity()`; Land's side chips stripped of
  their graphic by `chipStrip` and given it back every draw (off the scene and on again) - `chipStrip` takes the
  caller's graphics now, as KP1 made it take its icons.
- **By the harness:** before the new sections, KeptCheck's 94 held but one, History's goods' fold: a hidden part
  of the new `ScaleRows` picture kept last month's height (slice E's first mend, §5).
