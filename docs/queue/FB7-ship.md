<!-- readiness: provisional -->
<!-- gate-run: /tmp/cbs-gate-FB7-Gss1vBX6 -->
## changelog
### {next} — FB7, counters read under load — 2026-11-17, ready, see docs/queue/FB7-notes.md
Two counters that load could move are read so that it cannot; the model and the playtest are main's. SaveSlotCheck 7d's swap counter
and its seam are the whole program's: 7d now counts with the shared save writer settled and held, the seam answering only its own
thread, and asserts the writer wrote nothing meanwhile. With another job on the writer waiting for a file inside 7d, main went red in
36 of 40 runs, FB7 in none (SS1; the first red, RT1's, was the clock SS2 mended). ReplayCheck 5d's asks were getTaskCount() deltas,
which miss a job the thread has taken and not begun: a loaded glide that asked nothing could tally -1. The map's worker now counts each
job as it is handed in (MapView.Worker), a fixture causes the moment (the old count 2 to 1, the new one holding), and no frame's asks
may come out below nothing (GR2-R). Both harnesses green 4 of 4 as two copies beside a 20-thread CPU load; FB7's three known reds retire.
