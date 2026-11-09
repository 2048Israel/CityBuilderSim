# CR1 — a Credits page from the title screen ({next}, 2026-11-09)

The brief: `docs/queue/LC1-notes.md` §5. Built on main at 08f58a5f (LC2 merged). The session's compact window:
W=150000 (the orchestrator's env, not the project's 300k).

## The decisions

- **The page reads the files from the jar, one source each.** `pom.xml`'s resources carry
  `tools/earth/ATTRIBUTION-2.txt` and `tools/world/ATTRIBUTION.txt` into the jar as `credits/earth/ATTRIBUTION-2.txt`
  and `credits/world/ATTRIBUTION.txt`, beside the default `src/main/resources`: no copy in `resources/` to drift
  from the one `sources2.py` writes and LC2/LC3 edit. `mvn compile` (so `batch-check.sh`) copies them.
- **`Credits`** (flat package) is the pure model of what the page draws: the sections in order (the Earth, the
  nations, the fonts), each a title, its resource and its text in full, or marked missing with where the file is
  beside the exe. The screen, `showCreditsMenu()` in `UserInterface`, beside Settings; the main menu's "Credits"
  button between Settings and Quit, so it is on the title screen and the Esc menu alike.
- **The fonts:** a lead line (IBM Plex Sans and IBM Plex Mono, Copyright © 2017 IBM Corp., SIL Open Font License,
  Version 1.1), then `fonts/OFL.txt` in full, as the OFL asks of a redistribution.
- **The Bank of Canada's and the ECB's notice: none yet.** EP1 shipped (0.8.95) and neither the world's file nor
  the Earth's names either bank; the page shows each file in full, so a notice a file gains is on the page with it.
- **Each file's "ships beside the exe" line** now also says the Credits page shows it; `sources2.py`'s template with
  the Earth's, so a re-bake keeps it.

## The calls, each with a recommendation

- ★1 **The libraries' notices are not on the page.** The exe bundles Gson (Apache 2.0, whose §4(a) asks that a
  redistribution give its recipients a copy of the licence), OpenJFX (GPL v2 with the Classpath Exception) and the
  Java runtime (whose `legal/` folder jlink keeps by default; not looked at in a built exe). The brief named the data and the
  fonts only. **Recommended:** a small batch, CR1-L under Pending, adding a section "The software" with Gson's
  Apache 2.0 text and OpenJFX's notice, read from the jars the build already has.
- ★2 **The maker's own line.** The page credits the data and the type, not the game's author. **Recommended:** none
  until Jerus says how he wants to be named.

## Not touched

- `tools/earth/ATTRIBUTION.txt` (earth-1's, not shipped since earth-2) still says "the Credits page": it now exists,
  so the line is true; left alone.
- No process file changed (the scripts, the checks' machinery, the rules a session reads): no one-in-one-out count.
- No ensemble: nothing the month reads changed (a screen, a model the screen alone reads, the jar's resources), so
  the default playtest's trace cannot move.
