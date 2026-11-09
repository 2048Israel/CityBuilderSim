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
  button under Settings, so it is on the title screen and the Esc menu alike.
- **The fonts:** a lead line (IBM Plex Sans and IBM Plex Mono, Copyright © 2017 IBM Corp., SIL Open Font License,
  Version 1.1), then `fonts/OFL.txt` in full, as the OFL asks of a redistribution.
- **The Bank of Canada's and the ECB's notice: none yet.** EP1 shipped (0.8.95) and neither the world's file nor
  the Earth's names either bank; the page shows each file in full, so a notice a file gains is on the page with it.
- **Each file's "ships beside the exe" line** now also says the Credits page shows it; `sources2.py`'s template with
  the Earth's, so a re-bake keeps it.

## The slice (one, Sonnet 5.5 xhigh; reviewed here)

- **The scroll** is Settings' own: `page == rootMenu ? scrolled(column) : column`. Read and confirmed: over the
  title backdrop `menuPage()` already returns a panel inside a ScrollPane, so the page scrolls on the title screen
  and in the Esc menu alike.
- **The text:** one Label per paragraph (`Credits.paragraphs`, cut on blank lines; one Label of 20 KB lays out
  slowly), `Palette.mono()` at 12px in `TEXT_MUTED`, wrapped at the statements' width (`STATEMENT - 16`), 8px
  between paragraphs, 14px in from the headings.
- **`CreditsCheck`** reads `Build EXE.bat`, `tools/` and `resources/fonts/` from the working directory, as
  `BrandingCheck` does; outside the repository those sections say so and are skipped. From the repository, as the
  suite and the gate run it, none is: 35 assertions in five sections, every ATTRIBUTION copy the bat makes matched
  to a section word for word and to its file beside the exe.
- **Review fix:** the byte-order-mark test was a literal invisible U+FEFF in the source; now the escape `"﻿"`.

## By eye

- The title screen: a Credits button under Settings. With a city open, the Esc menu: the same button.
- The page: CREDITS, one line of introduction, then the Earth, the nations and the fonts, each heading over its
  file in small mono type, wrapped; the fonts' notice above the licence. It scrolls to the end on both menus, and
  Back returns to the menu.

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

## The checks

- `--pick` (the data files named): exit 3. It ran all 187 harnesses, since `pom.xml` changed. The only reds
  are main's recorded ones (`docs/notes/gate.txt`): PriceLevelCheck, ScaleCheck, MapCheck and KeptCheck.
  KeptCheck's five failing lines are main's five, word for word and figure for figure. Four timing assertions
  were soft (SaveSlotCheck 1, MapCheck 1, KeptCheck 2). Maps regenerated, nothing changed; stale 0 firm.
- CreditsCheck: 36 ok, 0 FAIL.
- The review line: "needed - threading: ui/UserInterface.java (UI-wide)", a floor for the gate's stranger's
  review. The diff adds no threaded line (no synchronized, volatile, thread, executor, runLater, Atomic or
  Concurrent). The page reads three small files from the jar on the FX thread when it opens (about 40 KB).
