# R5 - research: the screens ({next}, 2026-11-06)

## WHERE IT STANDS

**Building.** Branch `batch/R5` from `batch/R4`'s ship file (RL1, 00fa589a); R4 not yet on main. Slice A (the model the
screens read) drafting; B1 (Policy › Research › Found, City History's flags) and B2 (equipment on the Investors page,
NEW on the build cards) after it. No ship file yet.

## 1. What R5 is, and where it splits

The brief is research-design §9 item 6, "R5, the screens: Found, equipment on sector pages, new-building cards,
History's flags", with §5.5, §6.2 and mockups 3-5, and what R1-R4 left it: R2 ★8 (the Sector page reads a piece's year,
history's; the screens take the month and the city's research) and R4 ★10 (a Research Lab on the player's build list
says what it is for). R3b (Policy › Research's Overview and Funding pages, the grant and prize cards) is queued apart
and "best beside R5": R5 keeps to its own screens and opens the Research area with Found alone (§5).

| slice | what | state |
|---|---|---|
| A | `ResearchView` (the timeline, the recipes in use, the equipment rows, a building's origin and NEW, the discovery flags), `ChartModel.flags(log, more)`, `Recipes.chosen()`, three history series (the researchers by field, the research money, the share fitted); `ResearchViewCheck` | drafting |
| B1 | `ui/ResearchScreen` (Policy's sixth area, Found), `PolicyScreen`'s area, City History's discovery flags; KeptCheck 95 | queued behind A |
| B2 | `SectorScreen`'s ITS EQUIPMENT and the Fit pill by the month, `BuildScreen`'s NEW, origin and the lab's line, the category's dot and strip; KeptCheck 96-97 | queued behind A |
