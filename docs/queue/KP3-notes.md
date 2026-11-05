# KP3 - Screens kept: Build and the construction page ({next}, 2026-11-05)

**WHERE IT STANDS** (2026-11-04, 23:30): the notes are written; slice A is next.

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

(Written as the slices land.)

## 5. What checks it

(Written with slice C.)

## 6. Found in review

## 7. Left
