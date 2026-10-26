## changelog
### TM2 — the ship gate's timing waits for a quiet minute — 2026-10-25, ready, no tag (tooling), see docs/queue/TM2-notes.md
`--timing` started its hard run at the first look that found no other
batch's JVM, and a runner that starts JVMs one after another leaves gaps a
look can fall in. Now `tools/jvms.ps1 -Quiet`, which both scripts call on the
PC (the same loop in sh elsewhere), waits for four empty looks in a row, 15 s
apart; a busy look starts the count over; at 30 minutes it runs hard all the
same and says so. The game is said, not waited for (`CBS_QUIET_GAME=1` waits).
`Batch check.bat` refuses an unknown `--option`; `agent.py` times a harness
soft beside other runs. `tools/quiet-selftest.sh` (new) proves it against
real JVMs with gaps, 17 of 17; a real gate beside a loop of JVMs waited it out.
## pending
| TM2-C | TM2 (no tag): the gate's timing waits for a quiet minute. Left: ★1 the game is not waited for by default (`CBS_QUIET_GAME=1` waits; recommended: as built, since a red beside it is judged against main's run beside it); ★2 four looks 15 s apart is about 45 s, not a full minute (recommended: as built); a JVM shorter than a look can fall between two looks unseen; the sh loop off the PC tried only under stand-ins | his calls; the orchestrator may settle ★1 and ★2 as recommended | [TM2-notes](docs/queue/TM2-notes.md) |
