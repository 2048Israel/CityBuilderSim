# KP4 - Screens kept: Sectors, the list and a business's five pages (0.9.21, 2026-11-05)

**WHERE IT STANDS** (2026-11-05, 15:57): ready again, see the ship file. The four slices are in and reviewed (A 0bae2c36, B bb19f39e, C cd9e58b0, D a8e560a0) with ★B's fix (594181fa); KP3 on main at 0.9.18, merged in (905e8468); `--pick` with the world files exit 3 and the full suite 170 of 174, every red main's recorded one (§5). The gate sent it back on 0.9.19 for one of 49's fixtures, mended (§6, "By the gate"); main at 0.9.19 merged in (3062d6b9) and `--pick` exit 3 on it. The gate's threading review (§4): SHIP + FOLLOW-UP, its three low items in KP2b's row.

The batch's record. The study's KP4 row (`docs/studies/spec-screens-in-place.md` §7) with §3 (the pattern and its
rules) and §6 (what checks it), and KP1's ★C (KP1-notes §6): Sectors' count-up held to 1x-10x when Sectors is kept.
Lane 2 (screens), after KP3, built on `origin/batch/KP3` at ddf79f1f once its ship file was on origin; `origin/main`
merged at once when KP3 landed (905e8468, 0.9.18). Opus wrote the briefs, reviewed each diff and committed; Sonnet 5.5
slices at xhigh wrote the code, one at a time. Nothing outside `ui/`, `KeptCheck` and one view class (`RefineryView`,
§6) changes, and no view class is read by the month: the default playtest's trace cannot move, so no ensemble (EN1).

## 1. What it builds

- **The Sectors tab kept, whole class** (`SectorScreen`, 6,373 lines, about 400 sites of `new`): the list (its head,
  its four figures, a card per business - a scope keyed by its sector - with its sparkline a kept `Canvas`, cleared
  and drawn, its investors' word and Show more's second row), and a business's frame and five pages, each page a
  scope keyed by the business, the page and the view (Summary or Statement), Freight's Operations by its division
  too, so another business, page, view or division shares no node with the last and opens at its top. Inside: the
  head, the five figures, the investors' line, the strip; Operations of every kind (the flow and the plant, how its
  tonnes move, the refinery, the materials industries, the oil industry and its reserve, Freight's All, Haulage, Rail
  and Ports, the shelf, what it charges), Income and its waterfall, the balance sheet, cash and credit, the four
  statement views, the Investors page. Rows that come and go are scopes keyed by what they show (a good, a unit, a
  well, a leg, a division, a bond, a building, a gate, a rule).
- **The pictures told their figures**: `RefineryPicture` and `IndustryPane` made once, handed the month's view by
  `set(...)`, which repaints only when the view or the width moved, the shapes from a `Kept.Pool` of the picture's
  own (a draw of another pool inside the page's, which `Kept.draw` supports); `LegBar` told its rows and scale,
  keeping its blocks while their count holds; the waterfall (`Pieces.Waterfall.stand()`, new) stands at the month's
  figures when it does not grow.
- **`Statement` and `StatementView`** kept whole (the money screens share them: Bank, Finances and the fund are not
  kept until KP5, and outside a draw every `Kept` call is `new`, so they are unchanged), and the helpers the pages
  reach: `Pieces`' `ring` (a sweep stopped where none is due), `pips`, `factGrid`; `SectorScreen`'s `oil*` table
  helpers, which make the land office's landmass table kept too.
- **The count-up held to 1x-10x** (KP1 ★C): the list's KEPT, each card's figure and a business's KEPT count from last
  month's only when `UserInterface.countsUp()` (paused, or at most `COUNT_UP_TOP`, 10x); otherwise the month's figures
  are set at once, a count still running from the month before is stopped (`stopCounting`), the plant's ring does not
  sweep and the waterfall stands. As on People and History.
- **`KeptBench` and `KeptCheck`**: the Sectors tab on the bench beside the other kept screens (`KeptBench.sectors()`
  and the hands a harness needs: a business, a page, a view, Show more, a division, a fold, a note, the columns) and
  `KeptCheck` 47-54, a section per part and the choices that reshape it, two dials (`KEPT_SECTORS_MS`,
  `KEPT_SECTOR_MS`) (§5).

## 2. The slices

| slice | what | commit |
|---|---|---|
| A | the wiring, the list, a business's frame, the screen's own pieces and the count-up rule, the statement line that opens, `Statement`, `StatementView` | 0bae2c36 |
| B | Income and its waterfall, Balance sheet, Cash & debt, the four statement views, Investors; `Pieces`' `pips`, `factGrid` | bb19f39e |
| C | Operations of every kind, the three pictures, the `oil*` helpers | cd9e58b0 |
| D | the bench and the harness, and the mends it found | a8e560a0 |
| Opus | `RefineryView`'s bands in order (★B) | 594181fa |

Each slice checked itself with a scratch smoke harness outside the repository (every sector's every page in both
views drawn kept over four months against the same page built new: A 651 pages, B 1,008, C 938 with a sea-town oil
city and a refinery town; 0 differing at the end of each), which slice D's sections now hold in `KeptCheck`.

## 3. The ★ calls, taken the recommended way

- **★A. Freight's Operations page keyed by its division**, beyond the study's "keyed by the business and the page":
  `openFreightDivision()` promises a division picked is drawn from the top, which a kept scroller would not do.
  *Recommended and taken.*
- **★B. `RefineryView`'s bands in the slots' order** (outside `ui/`, a view class no month reads): it iterated an
  `IdentityHashMap`, so the picture's boxes came out in another order on every call and the kept refinery picture
  painted itself again each draw. Three lines; RefineryViewCheck and RefineryCheck green. *Recommended and taken.*
- **★C. The reserve's cards keyed by whether they have a dial** (C's mend in `SectorScreen`): `Levers.dialCard` makes
  the dial's row and column in its own scope, so a card that loses its dial would take the dial's nodes. The same
  fix belongs in `Levers.dialCard` for every screen; *recommended:* a line in KP2b, which owns `Ladder` and the
  dial's other follow-ups (KP4-N).

## 4. The threading, for the gate's review

**One thread**, as KP1-KP3: the clock is an `AnimationTimer`; the month, every redraw, every handler and listener
below, every animation, and every repaint of a picture run on the FX thread. **Nothing in this batch runs off it.**
`Kept.drawing` is a static only that thread touches. What a reviewer weighs is what runs inside a draw and what a
kept node carries from one draw to the next.

- **The pool and the arrivals.** `SectorScreen.pool` serves the list and every business's page; `anotherCity()`
  forgets it (`forget()`). The list and every page share the screen name "showSectorMenu", so a move between them
  is a kept redraw under `keepMenu()`, not an arrival; what a move changes is the page's scope key (§1), so the old
  page's nodes - its scroller among them - are let go and the new page's are made new, at its top. `open()`,
  `backToList()` and `openSectorBooks()` drop the remembered inner scroll as before.
- **The frame over the scroller.** The scroller's height (bound to the frame's) and the page's width (bound to the
  scroller's viewport) are bound once per scroller, a marker in its properties, never unbound and bound again on a
  kept draw; the page reaches the window through `setPage()`.
- **The pictures' own pools.** `RefineryPicture` and `IndustryPane` repaint either inside the page's draw (`set(...)`
  with a moved view: a draw of the picture's pool nested in the page's, which saves and restores the outer draw) or
  in a layout pass when their width changes (the width listener, added once in the constructor: a draw of its own).
  Neither reads a figure captured at an earlier draw: `set(...)` stores the view the listener paints.
- **Animations.** Every count-up, the plant's sweep, the investors' line's flash and the waterfall's growth go through
  `Kept.play`, which stops the one the node was last given; the side that does not animate stops it too
  (`stopCounting`, `noFlash`, `Pieces.ring`, `Waterfall.stand()`), so a count past 10x never writes last month's
  figure over this one's.
- **Added once or set**: every click is a `setOnMouseClicked` set each draw (the hand-back clears it); every tooltip
  is `Kept.tip`, so a kept node keeps only this draw's (a `Tooltip.install` on a kept node would be uninstalled at the
  next draw's end, which is how Operations' chip tooltips were lost before slice C); hover looks are chosen from
  `isHover()` where the draw sets them.
- **The controls.** The reserve's FILL and RELEASE dials are `Ladder`s on `Levers.dialCard`, kept-safe since KP2
  (`Kept.held`, the value set only where it differs, the listener added once and returning inside a draw); their
  callbacks stage through `PolicyScreen` and redraw, reading the model when they fire.
- **The model** is read through the same getters and written only where the player's controls wrote it before.

## 5. What checks it

`KeptCheck` sections 47-54, after KP3's 36-46, on its city at `CITY_MONTHS` (198 assertions; 649 in all on 0.9.19's city, all pass;
452 before). They run last; their fixtures touch no earlier section: a sawmill put up through the model and Wood
Products held (`ensureWorks`, `holdSector`: the city sheds idle works), RefineryViewCheck's refinery town for the
refinery's picture, OilViewCheck's sea town (a platform, its wells, the Strategic Reserve) for the reserve's dials -
each a scratch city.

- **47 kept** (6): the list and a plain business's Operations page - the top nodes stay, nine in ten handed back,
  most months make none.
- **48 shows** (25): signatures equal to a page built new for `MONTHS` months, the same pixels at three, the spoil
  proof - the list, a plain business's Operations, its Income in both views, the Balance sheet as a statement.
- **49 reshaped** (106): Show more and less; every sector of `Sectors.KEYS` on its Operations page; a plain business,
  the shops, the landlords, a materials industry and Freight on all five pages in both views; Freight's four
  divisions; the fold of every line, the goods fold, the order book and a statement's notes opened and closed; the
  Statement view's change and common-size columns off and on - each redraw, and the one after a month lands, equal to
  a page built new.
- **50 the pictures** (18): `IndustryPane`, `LegBar` and, in the refinery town, `RefineryPicture` stay the nodes they
  were from month to month, repaint only where the month's view moved, keep nine in ten of their parts, paint nothing
  on a redraw in the same month, and equal a page built new.
- **51 the count-up only at 1x-10x** (6): at each rung of `UserInterface.SPEEDS` (and paused) a month that lands
  counts the list's KEPT and its cards', a business's KEPT, the waterfall and the plant's ring exactly at the rungs
  `countsUp()` allows (`COUNT_UP_TOP`); past it nothing counts, the page is the month's, and a count still running
  from the month before is stopped.
- **52 cost** (4): `KEPT_SECTORS_MS` 18, `KEPT_SECTOR_MS` 15 (the three business pages that weigh most).
- **53 the controls** (26): the reserve's FILL and RELEASE dials pressed and held across a month through the real
  show method (the same node, its thumb, its reading, its ladder's shown value), released and equal to a page built
  new.
- **54 the hover** (7): a list card, a door and the crumb under a still pointer keep their look across a kept redraw.

| dial | value | kept (median) | built new |
|---|---:|---|---|
| `KEPT_SECTORS_MS` | 18 | the list: five runs of the sections alone 8.3-9.2 ms (8.8); KeptCheck alone 6.9 | 23.8-26.0 ms; 21.5 |
| `KEPT_SECTOR_MS` | 15 | the shops' Operations, the heaviest: 5.1-7.8 ms (7.1); KeptCheck alone 7.2; Income as a statement 2.7, Wood Products' Operations 4.7 | 25.3-41.0 ms; 30.6 |

Each twice the median, rounded up (KP1's way); the gate judges them hard. Under `--pick` beside the other lane the
shops' page took 16.9 ms (SOFT, not counted); KeptCheck alone a minute later, 7.2. Broken by hand to see them caught,
one at a time and put back: `stopCounting` a no-op (51), a `Kept.tip` put back to `Tooltip.install` (48, 49, 54),
`Waterfall.stand()` a no-op (51), `RefineryPicture.set` repainting on every call (50). Not caught, by design: the
business page's scope key the same for both views - every part is set again at each draw, so the page is the same;
the key buys the nodes kept and the view's own scroller, not the page right.

Also green: RefineryViewCheck and RefineryCheck on ★B. On the merge with main at 0.9.18 (905e8468), with the world
files: `--pick` exit 3 (27 harnesses; MapCheck and LongPlaytest main's recorded reds, word for word; three timing
lines SOFT: MapCheck 2, KeptCheck's shops' page above) and the full suite, `AllChecks -f`, 170 of 174 in 35 minutes, its four reds all `[known]` by `docs/notes/gate.txt` and
none NEW: DenominationCheck's twins (OB1), PriceLevelCheck 8, MapCheck 2d and 2h, LongPlaytest's moved building
(DM2's); two timing lines SOFT (SaveSlotCheck, MapCheck). None reads a screen; KP4 changed nothing they run.
Sent back and mended (§6), on the merge with main at 0.9.19 (3062d6b9), with the world files: `--pick` exit 3 (27
harnesses; CentralBankCheck and MapCheck main's recorded reds by `docs/notes/gate.txt`; one timing line SOFT, MapCheck
1); KeptCheck ALL OK, 49's note now note 2 (the sales tax), 387 lines and then 407.

## 6. Found in review

- **By the slices:** a `Tooltip.install` on a kept node is uninstalled at the next draw's end (only `Kept.tip` marks
  a tooltip as the draw's), so every tooltip on the screen is `Kept.tip` (A, B, C). A node handed back by position
  keeps what its last user set and the next does not (a label's wrap or min width, a spring's grow): the screen's
  pieces drawn only sometimes are scopes of their own (A). A style built from the node's own (`setStyle(getStyle() +
  ...)`) grew every month on a kept node: the Investors' buildings grid and the oil platform's set it whole (B, C).
  The reserve's cards with and without a dial (★C). `RefineryView`'s identity-map order (★B).
- **By Opus:** slice C's smoke harness left two JVMs running after its report (a crashed first run's retry), stopped
  by hand; the refinery picture's repaint on every draw traced to `RefineryView` (★B) and mended there, not worked
  round in the picture. Every slice's diff read for `addListener`, `Tooltip.install`, `getStyle() +` and `new` left in
  a draw: none but records, `Insets`, `ColumnConstraints`, path elements and a picture's own parts.
- **By the harness (slice D's mends):** `billRow`, Freight's bill by division: a kept wrapped label that had words
  and is emptied keeps the wrapping width of the words it had (145 x 20 where a new empty label is 0 x 13), so a
  division that billed nothing is a row of its own scope, made new. Seen only on a 1,400-month city (49's every
  sector, Wood Products' Operations), not on the harness's 605-month one; green at 150, 600, 1,000 and 1,400 months.
  **For the gate's review**, the harness's own machinery: `againstNew` and `pageShows` now finish a page's
  animations before its layout pass rather than after (a count-up's last words and the waterfall's growth moved the
  layout after the last pass: about 50 false reds), the kept page and the page built new alike, so every earlier
  section compares the settled page as well (all green); `KeptBench.finishOne` runs an animation's on-finished
  handler too (the built tag's glow was left on); `firstDifference` names how many more lines part.
- **By the gate (sent back, 2026-11-05):** on the merge with 0.9.19 (RT1: every founded town sells one of its two
  founding Mixed Farms, so the harness city moved) 49's "note 1 opened on it adds to the page" was red, 387 lines and
  then 387, every comparison with a page built new green around it. Note 1 is the builders' revenue, which the screen
  does not draw when it comes down to one named part (no repairs billed that month): no caret, nothing to open. The
  fixture chose "the first note on a shown line", not one the statement opens. Mended in the fixture, the assertion as
  it was: `KeptBench.sectorsNoteOf` takes the first note whose line is drawn (shown, not nil in the columns) and whose
  note is not empty - the statement's own test for a caret, through `SectorScreen.incomeNotes`, the page's notes moved
  into a method of their own - and 49 asserts the premise first (a note that opens on lines exists).

## 7. Left

- **Not reached by a fixture** (KP4-N): the list's "Nothing recorded yet" and "no word yet" pages and a business with
  no month before (the harness city has months); the Ports division's berth-less row (`caption("")`), the twin of
  `billRow`'s empty wrapped label, which no city in the harness reaches; the land office's landmass table only
  through its rows (`drawOffice` needs a second landmass, which no scratch city has). Each is drawn by the same code as
  the kept pages around it.
- **`Levers.dialCard`'s dial or none** (★C): a card that loses its dial shifts onto the dial's nodes on every screen
  that draws one kept; the reserve's cards are keyed round it here. Recommended into KP2b, beside `Ladder`'s.
- **The money screens' half of the statements**: `Statement` and `StatementView` are kept, inert on Bank, Finances
  and the fund until KP5 keeps them.
