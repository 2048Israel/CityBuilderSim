# R5 - research: the screens ({next}, 2026-11-06)

## WHERE IT STANDS

**Built, waiting on R4.** Branch `batch/R5` from `batch/R4`'s ship file (RL1, 00fa589a); R4 is not on main yet, so
no ship file. Slice A is bf2f26a6, B2 4b4de81d, B1 9785404f. The default trace is R4's to the byte, so no ensemble.
`--pick` on R4's tip + R5: **exit 3**, main's recorded reds only (§3); the full suite running. When R4 lands: main
merged in, `--pick` again, the ship file pushed last.

## 1. What R5 is, and where it splits

The brief is research-design §9 item 6, "R5, the screens: Found, equipment on sector pages, new-building cards,
History's flags", with §5.5, §6.2 and mockups 3-5, and what R1-R4 left it: R2 ★8 (the Sector page reads a piece's year,
history's; the screens take the month and the city's research) and R4 ★10 (a Research Lab on the player's build list
says what it is for). R3b (Policy › Research's Overview and Funding pages, the grant and prize cards) is queued apart
and "best beside R5": R5 keeps to its own screens and opens the Research area with Found alone (§5).

| slice | what | state |
|---|---|---|
| A | `ResearchView` (the timeline, the recipes in use, the equipment rows, a building's origin and NEW, the discovery flags), `ChartModel.flags(log, more)`, `Recipes.chosen()`, three history series (the researchers by field, the research money, the share fitted); `ResearchViewCheck` 1-10 and its `researchTown()`, the 1900 city the screens' KeptCheck sections share | bf2f26a6 |
| B1 | `ui/ResearchScreen` (Policy's sixth area, Found), `PolicyScreen`'s area, City History's discovery flags; KeptCheck 95 | 9785404f |
| B2 | `SectorScreen`'s ITS EQUIPMENT and the Fit pill by the month, `BuildScreen`'s NEW, origin and the lab's line, the category's dot and strip; KeptCheck 96-97 | 4b4de81d |

All drafted by Sonnet 5.5 at xhigh, reviewed here. What the reviews found:

- **Slice A.** Every read is empty in a city founded the default way (R3 ★2: research runs in no other), so a played
  city's screens are as they were. The three series are kept in a map by name, as the world's moneys are (WN4), and
  only by a city founded the 1900 way: a default city's history file, year book and charts are unchanged, and JSON
  holds no NaN (the share fitted begins in the first month with equipment to count). The map goes everywhere the world's
  moneys go (the load, the cut, the fold, a reform: the money series scaled). A find counts as HERE only before
  history's month, as `Research.foundHere()` reads it. `researchTown()` is played, not edited: FittingCheck's village
  with a Pulp Mill, founded the 1900 way, PE1 restored as found in month 3, played to month 14 (about 2 s); section
  10 holds each thing it promises. Found in passing: no shipped entry is dated at or before month 1, so a 1900 city
  knows none "at the founding"; no non-default recipe's since is a catalogue code, so a recipe's origin is never a
  discovery yet.
- **Slice B2.** One rule says NEW (`BuildScreen.showsNew`), asked by the card, the category's dot and the strip, so the
  three never disagree; the card notes the month it is first drawn in, and the strip and dots drawn before it in the
  same draw still read that month as unseen. The edge stays the building colour while an order is pending. A piece
  known by a year after the founding says "from abroad" and its January, not "known since the founding". KeptCheck 97
  causes the seen rule apart from the model's year (the find restored so the furnace is five months old, not at its
  last NEW month) and holds both sides. A javadoc's "he" for the player mended here.
- **Slice B1.** The timeline is kept nodes, not a canvas: each dot a scope keyed by its code with its tooltip, placed
  when the pane is laid out (the one width a draw cannot know), the lines, ticks and rules on a canvas of the pane's
  own; dots closer than their width stack upward (`levels()`, pure, held in KeptCheck 95). City History's decisions
  list, its YOUR DECISIONS count and the hard times' decision ticks stay decisions (`isDecision()`). The lead said
  "knew 0 of 89" in every 1900 city (no entry is dated at or before month 1): mended here to say how many are still to
  come. `docs/tree.md` has the three new files; `CLAUDE.md`'s count of interface files is forty-four (Stale's finding).

## 2. The calls (★)

1. **★ The split with R3b.** Recommended and taken: R5 opens Policy's sixth area, Research (★R2), with its one page of
   its own, Found, and draws no hub card; R3b puts Overview and Funding in front of it and the hub's sixth card beside
   the five (§5). Nothing R5 draws is seen in a city founded the default way (every list is empty there), so a played
   city's screens move by one chip, Research, whose page says in two sentences why it is quiet.
2. **★ The series only in a city founded the 1900 way** (slice A): the researchers by field, the research money and
   the share fitted are recorded where research runs; a default city's history file and year book are as they were.
   Its schools' faculty still research there (R3 ★2), every researcher idle: the Found page draws no chart of them.
3. **★ NEW until seen or a year**: a building is NEW on its card for `ResearchView.NEW_MONTHS` (12) months after it
   became known, and from the month after the player first saw its card it is not; the seen state is the screen's and
   not saved (PolicyScreen's biting flags' rule), so a reload marks NEW again what became known within the year.
   Saving it would be a field for a mark; recommended as is.
4. **★ A lab's card says what a lab is for** (R4 ★10's recommendation, taken), rather than keeping the four labs off the
   player's list: a lab is an investors' building for a prize, and one ordered by hand with nothing to chase is shed
   after `Research.LAB_IDLE_MONTHS` months idle.
5. **★ No licence line** on a building's card or anywhere: licence fees are R2c's, not built; the card's line is in the
   ship file's `## pending` for R2c.
6. **★ Three verdicts, not four**: mockup 4's "won't fit (too few years left)" has no model behind it (a building has
   no age the fit reads); a sector's equipment says fitted, next (it pays) or not worth it yet, `EquipmentFit.Kind`'s.
7. **★ A discovery is a flag, not a decision**: City History's lane carries the discoveries beside the decisions in
   one flag a month (`ChartModel.flags(log, more)`), in the research violet; the decision log holds none, and the
   page's list of decisions stays decisions.

## 3. Checks

- **The trace.** The default playtest (2,405 months) traced on R4's tip (00fa589a, built apart) and on slice A
  (bf2f26a6), back to back: all eight CSVs byte-identical, the log but for "in 50s"/"in 64s" and the layout windows'
  "1.7 s"/"2.4 s". So no ensemble (`docs/batch.md` 6): a city founded the default way records none of the new series
  and reads none of the new views.
- **`--pick` on R4's tip + R5** (9785404f's tree): 27 of 182 harnesses for 55 files, the seven screens read by no
  harness but KeptCheck: **exit 3**, main's recorded reds only (DenominationCheck, ScaleCheck, KeptCheck's "a month's
  redraw after each choice hands back nine in ten" from section 49); timing SOFT. `ResearchViewCheck` 106 ok;
  KeptCheck 95-97, 43 + 23 + 27 assertions, ok.
- **The save** (`--pick`'s "review needed - saves: HistorySave.java"): one new field, `HistorySave.researchSeries`, a
  map by name as `worldMoney` is, written only by a city founded the 1900 way. No format bump (`docs/batch.md`'s rule):
  a history without it reads it empty (Gson leaves a missing key alone), and an older build ignores it. DataSave is
  untouched; the timeline, NEW and the flags are derived from what R3 already saves (each find's month).
- Not a process batch: no scripts, checks' machinery or rules changed (CLAUDE.md's one word is a count, 43 to 44).

## 4. For R3b, and what the two fit by

- **The area.** `ui/ResearchScreen` holds Policy › Research: `PAGES` is `{FOUND}` today; R3b adds "Overview" and
  "Funding" in front (`{OVERVIEW, FUNDING, FOUND}`), a page method each, and the area's first page becomes Overview,
  where a door to the area lands. `AREA_INFO` grows by the grants and prizes. The tab strip appears by itself once
  there are two pages (Wages' rule in `PolicyScreen.drawPage()`).
- **The hub's sixth card** is R3b's, with the Overview it opens (researchers at work, money a month, what is in reach).
- **Reads to use, not repeat**: `ResearchView.timeline()`, `count()`, `byField()` for "discoveries so far here and
  from abroad"; `HistorySave.researchersKey(f)`, `RESEARCH_SPEND` for anything over time; `Palette.DISCOVERY`,
  `Icons.FLASK`. Found's OVER TIME charts are the money and the researchers over time: the Overview need not draw them
  again.
- **What R3b needs that R5 does not add**: what is in reach per field and the sectors it touches (§6.2, ★R4:
  `Research.openProblems(f, month)` and each entry's `touches()`), idle researchers, the posts filled and the room,
  the previews; and after a load the month's researchers, idle and open counts read 0 until a month runs (R3-notes),
  which the Overview shows and Found does not.

## 5. What to look at (by eye, if an issue turns up)

On a city founded the 1900 way (only a harness founds one until R1d; `ResearchViewCheck.researchTown()` is the
quickest): Policy › Research › Found at the window's width (the timeline's stacked dots and the found-here line past
now; B1 saw the charts leave room at the right of each card, and the share-fitted chart's axis run to 150%, TimeChart's
own range, which has no cap to set); a sector's Investors page with ITS EQUIPMENT (Agriculture or Industry in the
town); Build › Industry with the Electric Arc Furnace NEW (the dot on the chip, the strip, the violet edge); City
History's lane, where the "you" label now also stands over the discovery flags. On a default city: Policy's head with
six chips, and Research's one quiet card. Found in passing, not mended: the inbox says "ahead of the world" where the
flags and the cards say "before the world" (`Inbox.takeResearch()` against `ResearchView.flagEntries()`).
