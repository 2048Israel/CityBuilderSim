## changelog
### {next} — GT2, a timing red main also misses re-runs nothing — 2026-11-07, ready, see docs/queue/GT2-notes.md
No player sees this one: the gate is shorter. Every gate since 11-05 had a timing red that main itself misses (SaveSlotCheck's
25 ms, main's medians 26-57 ms), and `ship.sh` timed each in three paired rounds of main's classes and the merge's, 20-80
minutes a gate (PD1's 78), to conclude it was not the batch's. Each gate now keeps the merged main's figure of every miss in
`docs/notes/gate.txt` (`# figure` lines: the merge's median over the rounds, else its hard figure); the next gate the same day
passes a miss whose recorded figure misses the budget too, with the merge's no more than 15% over it, with no round, and times
the rest as before. Replayed on 11-07's nine gates, four would have run no round and four would have run them without
SaveSlotCheck. `docs/orchestrator.md`, The gate 6, says so; `tools/ship-selftest.sh` has four cases for it, and its QUEUE.md
case, red since that file gained a second table on 11-02, compares with main before the merge. Process lines 104 in, 95 out.
