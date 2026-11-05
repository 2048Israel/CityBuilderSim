# KP3 - Screens kept: Build and the construction page (0.9.18, 2026-11-05)

**WHERE IT STANDS** (2026-11-05, 09:57): ready, see the ship file. The three slices are in and reviewed (A 3e46f63c, B 114cd980, C 275893a2); main at 0.9.17 merged in (04956676); `--pick` with the world files exit 3 and the full suite 170 of 174, every red main's recorded one (§5). Its gate wants a threading review (§4).

The batch's record. The study's KP3 row (`docs/studies/spec-screens-in-place.md` §7) with §3 (the pattern and its
rules, seven since KP2) and §6 (what checks it). Lane 2 of the night line, after KP2, built on `origin/batch/KP2` at
14d2e38d (the orchestrator, 23:10: KP2's work pushed, its ship file waiting only for BA1); `origin/main` is merged
once BA1 and KP2 are on it, before the ship file. Opus writes the briefs, reviews each diff and commits; Sonnet 5.5
slices at xhigh write the code, one at a time (the PC carries the other lane's gate). Nothing outside `ui/` and
`KeptCheck` changes, so the default playtest's trace cannot move: no ensemble (EN1).

## 1. What it builds

- **The Build screen kept, whole class** (`BuildScreen`, 6,006 lines, 273 sites of `new`): the Overview (the city's
  job, what would help most, automatic building's card with its two dials, the open asks, the market's row), a city
  category opened on its needs and a market category in its groups (each with its order bar and the keys' caption),
  every building as the one card (its head, hero, bars, investors' line, needs, the materials' lines, the paving,
  the order line and stepper, the stats), the stat card and the receipt with its pulse, and the pages a build is
  stopped on (no deposit, coast, river, bridge, licence, land) and the run's funding and fell-short pages. One
  `Kept.Pool`; every page drawn through `UserInterface.keepMenu()`; a scope per page, per section, per building card
  (keyed by the building's name), per advice tile and per open ask.
- **The construction page kept** (`ConstructionScreen`, 1,144 lines, 83 sites): the builders' gauge, a row per
  site (a scope keyed by the site), the timeline (a picture told its figures), the Demolish tab and its find field
  by KP2's field rule.
- **The helpers they reach**, converted as KP1 and KP2 did theirs: the `Pieces` helpers not yet kept, `CapChips`
  (the yield box, by the field rule; inert on Bank, Finances and the fund until KP5 keeps them), and what KP2 left
  made new on its pages from this file (`verdict`, `arc`, `goAhead`: KP2-N's half that is KP3's).
- **The controls by KP2's rule**: automatic building's two `Ladder` dials (already kept-safe since KP2), the card's
  steppers and the order bar (refilled in place as a stepper moves, outside a draw, as KP2's dial card is), the
  cap chips' yield box and the construction page's find field (never set while focused).
- **`KeptBench` and `KeptCheck`**: Build's pages and the construction page beside the others, a section per page
  and the choices that reshape it, the controls held across a month, a `KEPT_*_MS` dial for each.

## 2. The slices

| slice | what | files |
|---|---|---|
| A | the card and what it reaches: the build menu, the shortcut, the card (paving, materials' lines, keyboard), the stat card and the receipt; the `Pieces` helpers and `CapChips` | `BuildScreen` (its first half and THE STAT CARD), `Pieces`, `CapChips` |
| B | the pages and the window: the Overview, automatic building, the two kinds of category with their order bars, the stopped and funding pages, the pool and `keepMenu`; the construction page whole | `BuildScreen` (the rest), `ConstructionScreen`, `UserInterface` (the wiring) |
| C | the bench and the harness, after A and B; mends where a page built new differs | `KeptBench`, `KeptCheck`, the pages it finds wrong |

A and B share `BuildScreen`, so they run one after the other; one slice at a time in any case tonight.

## 3. The ★ calls, taken the recommended way

- **★A. `CapChips` kept here, not in KP5.** Build's automatic-building card and its funding page draw it, and §7
  has the first batch that reaches a helper convert it. Kept, its yield box never leaves the scene, so the focus it
  restored by hand (`noteFocus()`, a `runLater` that also put the caret at the end) is not needed on a kept page and
  must not move the caret; on Bank, Finances and the fund, not kept until KP5, it works as before. *Recommended and
  taken:* the field rule is settled (KP2 ★A) and a helper half-converted is worse than one done.
- **★B. The order bar stays refilled in place.** A stepper's press reprices its card and refills the order bar
  outside a draw, with plain new nodes; the next draw hands back the bar's own. Redrawing the page on every press
  would cost a kept draw per click for nothing a player sees. *Recommended and taken:* KP2's dial card does the same.
- **★C. Every page of the screen kept**, the stopped and funding pages too: §7 says whole screen classes (KP2's ★C).
  *Recommended and taken.*

## 4. The threading, for the gate's review

**One thread**, as KP2: the clock is an `AnimationTimer`; the month, every redraw, every handler and listener below run
on the FX thread, and `Kept.drawing` is a static only that thread touches. What a reviewer weighs is what runs inside
a draw and what a kept node carries from one draw to the next.

- **The pools and the arrivals.** `BuildScreen.pool` serves every Build page, `ConstructionScreen.pool` its page;
  `anotherCity()` forgets both. `keepMenu()` forgets a pool when the screen name changes or the rail's own tab is
  pressed. The Overview and every category share the screen name "handleAllBuildingMenus", so `keepBuild()` forgets
  the pool when the page it draws is not the one the pool last drew (`drawnPage`): a move from the Overview to
  Residential, or between categories, takes keepMenu's arrival path (`clearMenu(screen, again, false)`), scroll and
  all, exactly as when every page was rebuilt; a month, a stepper, a ring or a pin on the same page is a kept redraw.
  The stopped pages, the funding page and the fell-short page have screen names of their own, so their arrival is
  keepMenu's own. Each page's draw is a `drawXxx(...)` wrapped in a scope keyed by the page.
- **The order bar** (★B). Inside a draw the card's `reprice` does not refill the bar (`Kept.drawing()`); the page
  calls `refreshOrderBar()` once, after the last card, in a scope of its own. A stepper pressed between draws reprices
  its card and refills the bar in place, outside any draw, with plain new nodes; the next draw hands back the bar's own
  and drops those.
- **What fires during a draw.** `Pieces.searchBox`'s text listener (the cap chips' yield box) and the construction
  page's find listener return at once while `Kept.drawing()`: a draw setting a box to what the page says is not a
  key typed. `Kept.text` never sets a box that has the focus. Automatic building's two dials are `Ladder`s, kept-safe
  since KP2 (`Kept.held`, the value set only where it differs, the listener added once and returning inside a draw);
  their callbacks read `ui.game.getAutoBuilder()` when they fire.
- **Added once** (`Kept.isNew`, or a marker in the node's properties): the search box's text listener (it calls the
  consumer of the latest draw, kept in the box's properties); the yield box's focus listener (reading the box and the
  `CapChips` fields when it fires); the find box's listener (reading `pickerList`, the latest draw's list); the wrap
  bindings of the strip and the card flows (`wrapToScroller`, a marker per scroller); the construction page's width
  binding (`isBound`). Every other handler is a `setOnXxx`, set every draw and cleared by the hand-back.
- **The focus.** `keepMenu()` leaves the focus on a `TextInputControl` (KP2 ★A); `CapChips.noteFocus()` and its
  `runLater` refocus stay for Bank, Finances and the fund, which still rebuild the box, and never refocus a box that
  has the focus nor move its caret (★A).
- **Animations.** The receipt's pulse goes through `Kept.play(dot, ...)`, which stops the one the dot was last given;
  `clearMenu()` still stops `ui.receiptPulse` on every redraw, kept or not, and a dot with no pulse due has its old
  one stopped and its opacity put back.
- **The model** is read through the same getters and written only where the player's controls wrote it before.

## 5. What checks it

`KeptCheck` sections 36-46, after KP2's 21-35, on its city at `CITY_MONTHS` (452 assertions in all, all pass; 276
before). The KP3 sections run last, so their fixture - six sites kept on through the model, the city borrowing on
Build's bond and buying ground where an order finds none (`ensureSites`, topped up after each month while the pages
are watched) - touches no earlier section.

- **36 Build kept** (6), on the Overview and on Healthcare with two orders dialled and a card's stats pinned: the top
  nodes stay, nine in ten handed back, most months make none; a kept draw fills the order bar exactly once.
- **37 shows** (16): signatures equal to a page built new for `MONTHS` months, the same pixels at three, the spoil
  proof - on the Overview, Healthcare and Homes.
- **38 reshaped** (43): fifteen pages in turn, thirteen rings on Education and Healthcare, orders dialled and cleared
  through the cards' own steppers, a card pinned and unpinned, automatic building on and off. **For the gate's
  review:** `Choice` gains `acts`, for a choice that makes the model act - automatic building turned on orders what
  the Overview advises, so the next month's advice tiles are other buildings (71-76% of the page handed back). For
  those two the share is printed, not asserted; the redraw's equality to a page built new is asserted as for every
  choice.
- **39 cost** (4): `KEPT_BUILD_MS` 12.
- **40-43 the construction page** (3, 15, 11, 2): Sites kept, shows (the Timeline and Demolish tabs as pictures too),
  reshaped (the three tabs, a pick, a buy-out pick, the find box typed and emptied), cost `KEPT_CONSTRUCTION_MS` 4.
- **44 the stopped and funding pages** (24): each an arrival, then a month later again, equal to a page built new.
  Caused through the model (`Game.buildRunStop`): no deposit (Iron Mine), no river (Hydro Station), no crossing
  (Highway Bridge), no licence (Engineering Services Office), no land (10,000 Studio Apartments); the funding page by
  1,000 Houses against the cash. Drawn for a building that only names the page: no coast (Desalination Plant; the
  city meets no such refusal) and the two fell-short pages (the model does not reach them in this city).
- **45 the controls** (36): automatic building's dial pressed and held across a month (the same node, its thumb, its
  reading), released and equal to a page built new; the cap chips' yield box and the construction page's find box
  with the focus keep node, text, caret and focus across a month through the real show methods; a stepper pressed
  between draws and the next kept draw equal to a page built new.
- **46 the hover** (13): the job tile, the market tile, a door, a ring card and the card's (i), whose stat cover shows
  while it is hovered and goes when the pointer does.

| dial | value | kept, five runs (median) | built new |
|---|---:|---|---|
| `KEPT_BUILD_MS` | 12 | Overview 3.8-6.3 ms (4.3); Healthcare with orders and a pin 3.7-9.0 ms (6.0) | 11.6-23.7; 15.0-30.0 ms |
| `KEPT_CONSTRUCTION_MS` | 4 | 1.4-1.9 ms (1.7) | 8.5-16.5 ms |

Each twice the median, rounded up (KP1's way); the gate judges them hard. Broken by hand to see them caught, one at a
time and put back: the cover set only on a new node (37, 38), the cover never set false (46), `Kept.held` ignored in
`Ladder` (45), the find box set while focused (45), the (i)'s click added as a handler every draw (38's pins), the
hover ignored (46), the order bar refilled inside a draw (36's fill count). Not caught, by design: a card's or a site
row's scope key made constant - the nodes hand back by position and every part is set again at each draw, so the
page is the same; the key buys the nodes kept, not the page right.

Also green on slice A, B and C's code: BuildMenuCheck, BuildCardCheck, RunWorksCheck, ConstructionControlCheck.
On the merge with main at 0.9.17 (04956676), with the world files: `--pick` exit 3 (23 harnesses; MapCheck and
LongPlaytest main's recorded reds), and the full suite, `AllChecks -f`, 170 of 174 in 39 minutes, its four reds all
`[known]` by `docs/notes/gate.txt` and none NEW: DenominationCheck's twins (OB1), PriceLevelCheck 8 (RT1, BS1),
MapCheck's moved boxes and LongPlaytest's finding (DM2's). None reads a screen; KP3 changed nothing they run.
Tonight's known-class reds, passed by name at the gates (the orchestrator, 00:40) and not chased here:
PriceLevelCheck 8 (RT1), ReplayCheck 5d's "asks -1" under load (CM1b), MapCheck 2h and SlopeCheck's moved building
(DM2's), ShortSellingCheck 7 (OH1).

## 6. Found in review

- **By the slices:** inside a draw a card's reprice no longer refills the order bar, which it did once per card; the
  page fills it once, after the last card (A, B). The construction page has three tabs, not two (Sites, Timeline,
  Demolish). `CapChips`' refocus put the caret at the end of a box that already had the focus. `Statement.
  reportSection` and `FinancesScreen.words`/`caption`, reached through `CapChips`, made new. A landed-on card already
  scrolls after any draw (`openOnBuilding`'s `runLater`); the construction page never scrolled to a site.
- **By Opus:** `Statement.reportSection`'s scope keyed by its heading, not a constant (two sections on one page would
  have run on in one scope). KP2 left "whatever `BuildScreen.verdict`, `arc` and `goAhead` make" for KP3: all three
  make no node (`verdict` is words, `arc` a figure, `goAhead` a test), so KP2-N's half that named this file has
  nothing left in it.
- **By the harness (slice C's mends):** the ↺ that clears an order, hidden with `showIf()`, kept the place it last
  stood (250,0 on a kept card, 0,0 on a new one): it is in the row only while there is an order. The empty, hidden
  order bar kept its last place and width (18,663, 1,420 wide): it rests at the origin, as a new one does.

## 7. Left

- **Not exercised by a fixture:** the Overview's open asks (an order whose bond sold short at the cap waiting for its
  money), the sea terminal's payback, the port and crossing choosers, the coast refusal and the fell-short pages as
  the model reaches them. Each is a scope or a page drawn by the same code as the rest. Pending KP3-N.
- **Still made new on a kept page:** the receipt's popup (made when the dot is clicked, outside a draw) and
  `Pieces.openPopover`'s contents, both on a click and not on the clock.
