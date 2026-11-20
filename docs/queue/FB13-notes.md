# FB13 - the island's words, the Government screen's coupons and the build card's licence line (2026-11-20)

The brief is `docs/queue/FB13.md` (AL1-W, CD9-G, R5-L); built on main at 919c1fa41 (0.9.162). One Sonnet session, no slices.
Screens only: no money, save or month line is touched, so the default trace does not move (section 5).

## 1. Findings and calls (the design as decided; the build follows)

- **R5-L stays in the bundle.** Licence fees exist since R2c and R2c-P: `LicenceFees.payable()`, `TERM_MONTHS`, `SHARE`, `forNew()`
  (a sector's new building's monthly rent on its own estimate), `ResearchCatalog.Entry.fee()` and `worldMonth()`. The line is drawn from them.
- **AL1-W: the words are `LandmassView`'s** (pure functions of `Landmasses`), so `IslandChip`, `LandScreen` and `Inbox` only pass what
  `BuildAdvice` says. `linkFor()` is the rule the builders read; it chooses the island and mutates (the bridge card's chosen landmass,
  restored; the survey that found no crossing, remembered 12 months; `Game`'s one-slot survey cache), so a screen reading it would make the
  model depend on which screen was open. ★1 The screens read the same rule through `BuildAdvice.islandLink(game, id)`: linkOf() with a
  memory of its own (`SCREEN_NO_CROSSING`) and kept per (month, landmasses' version, the land's stamp, the ports and bridges on each island).
