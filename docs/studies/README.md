# Design studies

A design study is read-only work done before a batch: it reads the code,
measures what is wrong, prototypes the fix outside the game and writes down
the decisions Samuel (Jerus) has to make. Since 2026-10-11 the studies live
here, each a note with its prototypes and boards in a folder beside it
(Samuel: "just put them on github no? thats where studies will be"). The
batches a study plans go in the project's `claude/todo.md`, and the batch
write-ups stay in the project as before.

The studies before this folder are in the claude.ai project "Civic Ledger:
Java Game", under `claude/` (`spec-earth.md`, `spec-land.md`,
`spec-roads-and-ports.md`, `research-design.md` and the others); the
project's `claude/index.md` maps them by subsystem.

| study | date | what | batches |
|---|---|---|---|
| [spec-globe.md](spec-globe.md) | 2026-10-11 | The Earth's map as a globe, ice and desert on the ground, panning with W, A, S and D, and a minimap with district pictures; prototypes in `globe/` and `minimap/` | E7 to E9, MM1 and MM2 |

The prototypes are design aids, not game code: Python with numpy and Pillow
(and `global-land-mask` for the globe's coasts), and one Java timing. Nothing
in the game or the harnesses reads this folder, and `tools.Stale` does not
scan it.
