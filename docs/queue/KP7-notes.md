# KP7 - Screens kept: the rest (Household money, Rankings), and the rebuild path retired from the clock ({next}, 2026-11-06)

**WHERE IT STANDS** (2026-11-06, 04:55): ready. The slices are in and reviewed (A bf5b25ad, B f7bf4f84, D 84465ec0
and cdf53266, with Opus's bb51ecfc, 85daebd8 and 3a87945a); main merged in with KP6 0.9.27 and XI1 0.9.26 (2681657f),
`--pick` exit 3 on it, main's recorded reds only (§5).

The batch's record. The study's KP7 row (`docs/studies/spec-screens-in-place.md` §7) with §3 (the pattern and its
rules, seven since KP2) and §6 (what checks it). Lane 2 (screens), the last KP batch, after KP6, built on
`origin/batch/KP6` at its ship file (4155ecf1). Opus wrote the briefs, reviewed each diff and committed; Sonnet 5.5
slices at xhigh wrote the code. Nothing outside `ui/`, `KeptCheck` and the prose (`CLAUDE.md`, `README.md`) changes,
and no view class: the default playtest's trace cannot move, so no ensemble (EN1).

## 1. What it builds

- **Household money** (People's second page, slice A): drawn over last month's nodes through `keepMenu()` and
  `PeopleScreen.drawHousehold()`, from a pool of its own (`moneyPool`: People and Household money are two screens to
  the window, so an arrival at either forgets the pool it draws from; `forget()` forgets both). Every card and section
  is a scope; the grid's rows are scopes keyed by what they are (a family shape, the tier row, a ledger's key below
  the rule, a section's caption) and each cell by its tier inside its row, so a shape or a row that comes or goes
  hands nobody's cell to a neighbour; the open cell's books are a scope keyed by the cell, so another cell opened is
  not drawn over the last one's nodes. `Books` (the grid with the books beside or under it) is a picture told its two
  children. The three `Tooltip.install`s are `Kept.tip`; the cells' styles are set once, whole (`moneyCell`), where
  they were built on `getStyle()`. The page was placed straight in the menu column and kept its place only by the
  window's hold and settle; it now keeps it because its nodes are never taken away.
- **The Rankings tab** (slice B; a rail tab since LB2, 2026-10-31, after the study, so not in its §7 list, but on the
  rail the clock redraws, and so "the rest"): drawn through `keepMenu()` and `RankingsScreen.drawPage()`; what only an
  entry does (the rank over the years worked out again, the read of the saved cities started) stays in the show
  method. The four controls, the measures column (a scope per measure, pinned or grouped), the table's filters and
  rows, the card, the drawer (keyed by the place it is open on) and the notes are scopes. `RowsPicture` (the table's
  canvases) and `RankLine` are pictures told their figures, painted again only when a row, the bars' scale or the open
  place moved; the hover row relights under a still pointer. The columns' rows (`across`, `pair`) are kept, so a
  width change moves the three boxes between them and makes no node. A chip strip's chips are taken out of the
  `FlowPane`'s staged list before the row takes them, or they would change parents at every draw's end.
- **The rebuild path retired from the clock** (Opus, bb51ecfc). With these two kept, every rail screen draws through
  `UserInterface.keepMenu()`, so the clock's redraw after a month runs the page's code over its own nodes and never
  empties the page, holds its height or settles the scroll. `clearMenu()` is now the menu's own screens' alone
  (`isGameMenu()`: the main menu, Save, Load, Settings, the founding screen, the replay list), which the clock never
  redraws because it stops on them, and two simulate-months pages nothing has opened since 2026-09-18 (left as they
  are: unreachable, not this batch's to delete). Their hold, settle and the scrollers' three attempts stay for them,
  and for a kept screen's new page under the same name (Build's categories, which forget the pool so the page arrives
  as it did when rebuilt). `UserInterface.rebuiltInPlace` counts each draw that rebuilt the screen on show in place;
  KeptCheck 88 holds the clock to none. `CLAUDE.md`'s "A screen section" and the README's clock paragraph now say a
  screen draws through `Kept`, where they said it "must restore its own scroll position".
- **The `Pieces` helpers no kept screen reached** (the study's KP7 row): `Pieces.bandMeter` and `Pieces.pnlLabel`,
  which nothing calls now, make their nodes through `Kept` (85daebd8). Every other helper in `Pieces`, `Statement`,
  `StatementView`, `Levers` and `Ladder` already did (a scan of each for a node made with `new` outside a picture's
  own parts found none); neither page's slice met an unconverted one.
- `UserInterface.anotherCity()` forgets the Rankings pool; Household money's goes with People's `forget()`.

## 2. The slices

| slice | what | commit | its smoke run (scratch city, outside the repository) |
|---|---|---|---|
| Opus | the rebuild path retired from the clock: the counter, the docs | bb51ecfc | - |
| D, part 1 | KeptCheck 88: the clock never rebuilds a rail screen | 84465ec0 | 66 assertions; the Bank put back on `clearMenu` by hand went red on three |
| A | Household money | bf5b25ad | 66 choices and 25 months, 0 differing; the fewest handed back 98.1%; the books stacked at 1,000 px, 15 choices |
| B | the Rankings tab | f7bf4f84 | 369 comparisons, 0 differing, 112 of them by pixels, all equal; the fewest handed back 98.4% |
| Opus | A's bench hands, the Rankings forget, the bench's three sections put together | 6102b6ef, 3aeb1b4c | - |
| Opus | `bandMeter`, `pnlLabel` | 85daebd8 | - |
| D, part 2 | KeptCheck 82-87, 89, 90; 88 with the two pages | cdf53266 | 358 assertions in 82-90 at month 3,405, 45 s; a wrap set on one side only in `cashCell` went red on 61, the table's relight removed on 89's |

A and B ran at once on two sets of files (A in a worktree of its own; B, refused writes outside this session's
worktree, in this one); D's part 1 ran beside them on the harness alone. Their diffs met only at `KeptBench`'s last
lines (three new sections one after another).

## 3. The ★ calls

None. The study's KP7 row names Household money; the Rankings tab is the one other rail screen the clock rebuilt, so
keeping it is what "every rail screen kept" needs, not a call.

## 4. The threading, for the gate's review

**One thread**, as KP1-KP6, but for one worker that was there before this batch: the clock is an `AnimationTimer`;
the month, every redraw, every handler and listener below and every repaint of a picture run on the FX thread.

- **Off the FX thread: the Rankings tab's saved cities (LB3, unchanged).** `SavedCities.refresh(files)` runs on
  `WORKER`, one daemon thread (`rankings-saved-cities`), once per entry to the tab and once for `openSaved()`; it is handed
  the `GameFiles` and the last snapshot, touches no node, no `Game` and none of the screen's state, and hands back a
  snapshot through `Platform.runLater(() -> arrived(...))`, which redraws the tab if it is still the screen on show -
  since KP7 a kept draw like the clock's. `saved` stays `volatile` (only its `private` went, for the bench's hands,
  which read it on the FX thread). Nothing else in the batch runs off it: no thread, executor or timer is added.
- **The pools and the arrivals.** Household money's `moneyPool` and Rankings' `pool`; `anotherCity()` forgets both.
  People and Household money are two screens, so a chip between them is an arrival.
- **Added once**: Household money's two listeners on the menu's scroller (`hookBooks`, once for the window's life,
  reading the `books` field when they fire, as before); Rankings' viewport-width listener, once per scroller (a marker
  in its properties), reading the latest draw's boxes from the screen's fields; the width listeners of `RowsPicture`
  and `RankLine`, in their constructors. Every click and the table picture's mouse handlers are `setOnXxx` set each
  draw; every tooltip a draw makes is `Kept.tip`; hover looks are chosen from `isHover()`.
- **What runs a pulse later**: Household money's `reveal()` (one `Platform.runLater`, one-shot on the click that
  opened a cell, as before; now after the kept draw, in the show method). Nothing else.
- **Animations**: none on either page.
- **The window**: `clearMenu()` gains one counter (`rebuiltInPlace`, written and read on the FX thread).
- **The model** is read through the same getters; the bench's slot-10 save writes only a scratch folder's slot 10 and
  refuses the real save folder.

## 5. What checks it

`KeptCheck` sections 82-90, after KP6's 69-81, on its city as it stands when it reaches them (358 assertions and 4
timed lines in slice D's run of them alone at month 3,405).

- **82-84 Household money**: the page kept (top nodes stay; 389 of 396 nodes handed back, 25 of 25 months making
  none); equal to a page built new for `MONTHS` months, the same pixels at three, the spoil proof; every one of the
  city's 53 cells opened (45 a family's, 8 below the rule) and none, both views, the statement open and closed, the books
  stacked at 1,000 px and beside again, a cell open across a month - each redraw and the next equal to a page built new,
  the fewest handed back 98.7%; through the real show method a redraw keeps the page.
- **85-87 the Rankings tab**: the same; at every reshaping choice the page's canvases are compared to the pixel as well
  as the page by signature (the table is painted on canvases the signature does not read; `KeptBench.canvasPixels()`,
  the canvases alone, after a whole page's snapshot at each choice failed in the full run, §6): each board, money, price and conversion, every fifth
  measure with the last and the first unsourced, each filter, Around you, Browse all, a pin, every drawer; then the
  scratch city saved into its own folder's slot 10, a read of the saved cities landing on the tab with one city on the
  board, and Your cities at Now and Your age, its prices, moneys and drawers (`saved:10` among them).
- **88 the clock never rebuilds a rail screen**: the condition caused first - a page drawn the old way twice under one
  name moves `rebuiltInPlace` by one, an arrival by none; then 17 screens (Build's Overview, a city page and a market
  page, the construction page, the land office, People, Household money, Services, Infrastructure, Sectors,
  Government, Finances and its fund, the Bank, Trade, Policy, City History, Rankings), each shown through its real
  show method and given three months with the screen's own redraw after each: the count unmoved, the page's top nodes
  the same nodes, the same screen on show, and nodes handed back by the redraw. The Bank's show method put back on
  `clearMenu()` by hand went red on three of its four lines.
- **89 the hover and the tooltips**: a Household money vitals cell keeps its hover look under a still pointer; a
  Rankings measure line keeps its own, and the table picture relights the row under a still pointer and clears it when
  the pointer goes; a node a month's redraw keeps keeps its own `Tooltip` on all six page variants.
- **90 the cost**, through `Timing` (soft beside the other lane, hard at the gate):

| dial | value | page | kept, five runs (median) | built new |
|---|---|---|---|---|
| `KEPT_HOUSEHOLD_MS` | 8 | as it opens; the first cell open | 2.7-3.1 ms (2.8); 3.6-7.0 (3.8) | 15.9-17.7; 19.2-29.7 ms |
| `KEPT_RANKINGS_MS` | 7 | as it opens; every measure open | 2.6-3.0 (2.8); 3.1-3.5 (3.3) | 9.0-10.9; 16.8-20.3 |

Each about twice the heavier page's median, rounded up (KP1's way), measured in five runs of slice D's sections at
months 3,405-3,987.

On the branch at 3a87945a (KP6's tip under it, main 0.9.25 below that), with the world files: `--pick` **exit 3**, 8
harnesses (BuildMenuCheck, ClockCheck, IslandNetCheck, KeptCheck, LandCheck, MapCheck, TimingCheck,
ZoningViewCheck), the two reds main's recorded: MapCheck (2d) and KeptCheck's one line, section 49's Construction
income page at 89.8% (KS1, queued). KeptCheck's KP7 sections ran at month 4,435: 82-84 72 assertions, 85-87 70, 88 74
(17 screens), 89 14, 90 four timed lines, all OK, no renderer error; 2,233 OK in all. KP7's timed lines, soft beside
the other lane: Household money 3.4 ms as it opens and 5.4 with a cell open, of 8 (built new 21.1 and 28.2); Rankings
3.2 and 3.6 of 7 (13.1 and 20.0). One soft line not KP7's: KP4's `KEPT_SECTORS_MS`, the Sectors list at 20.1 ms of 18
under that load. No full suite: the diff is `ui/`, `KeptCheck` and prose, and the eight picked are the harnesses that
read it; nothing outside `ui/` changed (the gate runs the suite).

On the merge with main (2681657f: KP6 0.9.27, XI1 0.9.26), with the world files: `--pick` **exit 3**, the same eight,
the same two reds, main's recorded (MapCheck 2d; KeptCheck 49 at 89.8%, gate.txt's line); 2,234 OK, no renderer
error, every KP7 line passes. Timed alone this time: Household money 3.0 ms as it opens and 3.6 with a cell open, of 8
(built new 19.3 and 22.9); Rankings 3.1 and 3.3 of 7 (15.7 and 16.5).

## 6. Found in review

- **By the slices:** A: a cell's style built on `getStyle()` after `gridCell` set it (now set once, whole); B: the chip
  strip's chips staged by two boxes (they would have changed parents at each draw's end), the columns' rows made new at
  every width change, a canvas only the pixels can see (the signature reads no canvas: a copy that never repainted was
  caught by the pixel comparison on every measure change).
- **By the full run** (`--pick`, 3a87945a's parent): slice D's 87 took a snapshot of the whole Rankings page four
  times a choice, the long list some 6,000 px tall; alone it passed, but late in the full KeptCheck, after every
  section's snapshots before it, the renderer's texture pool gave out - 219 `NullPointerException`s in `D3DTexture` on
  the render thread from 87's filters on and through 90, every one in making a snapshot's texture, so the pixel checks
  from there may have compared two failed pictures (they all passed). 87 now compares the canvases alone (each a tile of 40 rows) and the page by its
  signature; the whole page's pixels stay at 86's three months. A table that never repaints (B's mutant) still goes red
  on 58 lines.
- **By Opus:** each diff read for `new` left in a draw, `Tooltip.install`, `setTooltip`, `getStyle() +`,
  `addListener` and `runLater`: none but records, `Insets`, `ColumnConstraints`, a picture's own parts and the
  listeners above. The merge of A's bench hands after B's left a conflict marker and an unclosed method (6102b6ef,
  pushed before its compile was read); mended at 3aeb1b4c.

## 7. Left

- **Not reached by a fixture** (KP7-N): Rankings in a city with no nations of its own (KeptCheck's has them, so Your
  world's greyed chip is not drawn); a Household money vitals cell with a door this month (89 checks the hover on one
  without); Household money's empty states (nobody needing a basket, a household's month of nothing). Each is drawn by
  the same code as the kept pages around it.
- **KP5-N's chart paint** (a `TimeChart` paints on every `setData` and `setSize`, twice a kept draw; KP5's notes
  said "for KP7"): not taken. It is cost only, it touches every kept chart (History's first), and the KP5-N row in
  Pending keeps it.
- **The menu's own screens are still rebuilt** (Settings, Load, Save, the founding screen, the replay list): the clock
  never redraws them, so the rebuild path stays for their own clicks; keeping them is no batch's yet.
