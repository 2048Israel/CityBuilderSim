## changelog
### GP1 — the gate starts at once: no quiet minute, a ready branch picked up when its ship file lands — 2026-10-27, ready, no tag (tooling), see docs/queue/GP1-notes.md
From ship file to merge a gate took a median of about 60 minutes, of which `ship.sh` is 22-25: the rest was the hourly check-in, and TM2's quiet minute (up to 30 minutes for no other batch's JVM) when the other lane was running.
The quiet minute is out, with all that served only it (`quiet_minute` and its hook, `jvms.ps1 -Quiet`, the `CBS_QUIET_*` dials, `Batch check.bat`'s wait, `quiet-selftest.sh`, `CBS_TIMING_PAIRED`): `--timing` runs at once, and a red under load is judged against main's own run in the same minutes (TM3).
`sh tools/wait.sh --ship [--skip ID...]` is one background wait that ends when an unmerged `batch/` branch on origin has its `docs/queue/<id>-ship.md` (a branch ready already ends it at once; a merged or deleted one does not count), so the orchestrator's gate starts within minutes of the push; the hourly check-in stays as the fallback.
`docs/batch.md` (The day) says how it is armed and re-armed after each gate. `tools/wait-selftest.sh` tries it against a throwaway origin, 14 of 14; `ship-selftest.sh` 103 of 103.
One in, one out: 239 lines of script and rules added, 555 removed.
