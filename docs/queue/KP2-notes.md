# KP2 - Screens kept: Land, Services and Infrastructure; the controls' rule; KP1's hover kept ({next}, 2026-11-04)

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

To be written from the code as reviewed (slice A).

## 5. What checks it

To be written (slice E).

## 6. Found in review

To be written.
