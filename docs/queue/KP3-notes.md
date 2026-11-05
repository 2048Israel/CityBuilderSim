# KP3 - Screens kept: Build and the construction page ({next}, 2026-11-05)

**WHERE IT STANDS** (2026-11-05, 00:35): slices A (3e46f63c) and B (114cd980) committed, each green on BuildMenuCheck, BuildCardCheck, RunWorksCheck, KeptCheck (B: ConstructionControlCheck too); slice C, the bench and the harness, running. Then `origin/main` merged once BA1 and KP2 are on it, `--pick`, the ship file.

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

(Written with slice C.)

## 6. Found in review

## 7. Left
