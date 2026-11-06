# R5 - research: the screens ({next}, 2026-11-06)

## WHERE IT STANDS

**Building.** Branch `batch/R5` from `batch/R4`'s ship file (RL1, 00fa589a); R4 not yet on main. Slice A (the model the
screens read) is bf2f26a6; B1 (Policy › Research › Found, City History's flags) and B2 (equipment on the Investors page,
NEW on the build cards) drafting side by side. The default trace is R4's to the byte, so no ensemble (§4). No ship file
yet.

## 1. What R5 is, and where it splits

The brief is research-design §9 item 6, "R5, the screens: Found, equipment on sector pages, new-building cards,
History's flags", with §5.5, §6.2 and mockups 3-5, and what R1-R4 left it: R2 ★8 (the Sector page reads a piece's year,
history's; the screens take the month and the city's research) and R4 ★10 (a Research Lab on the player's build list
says what it is for). R3b (Policy › Research's Overview and Funding pages, the grant and prize cards) is queued apart
and "best beside R5": R5 keeps to its own screens and opens the Research area with Found alone (§5).

| slice | what | state |
|---|---|---|
| A | `ResearchView` (the timeline, the recipes in use, the equipment rows, a building's origin and NEW, the discovery flags), `ChartModel.flags(log, more)`, `Recipes.chosen()`, three history series (the researchers by field, the research money, the share fitted); `ResearchViewCheck` 1-10 and its `researchTown()`, the 1900 city the screens' KeptCheck sections share | bf2f26a6 |
| B1 | `ui/ResearchScreen` (Policy's sixth area, Found), `PolicyScreen`'s area, City History's discovery flags; KeptCheck 95 | drafting |
| B2 | `SectorScreen`'s ITS EQUIPMENT and the Fit pill by the month, `BuildScreen`'s NEW, origin and the lab's line, the category's dot and strip; KeptCheck 96-97 | drafting |

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

## 4. Checks

- **The trace.** The default playtest (2,405 months) traced on R4's tip (00fa589a, built apart) and on slice A
  (bf2f26a6), back to back: all eight CSVs byte-identical, the log but for "in 50s"/"in 64s" and the layout windows'
  "1.7 s"/"2.4 s". So no ensemble (`docs/batch.md` 6): a city founded the default way records none of the new series
  and reads none of the new views.
