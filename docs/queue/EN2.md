# EN2 - the ensemble names the data files, as the gate does

Queued 2026-11-08 by the orchestrator, from RE2's call ★4 (RE2-notes §4, taken on its recommendation). A small
process batch: Sonnet whole (`docs/batch.md`, 3); one in, one out (`CLAUDE.md`).

**Why.** In a worktree `tools/ensemble/ensemble.sh` finds no `data/`, so its 80 runs, and every baseline in
`docs/ensemble/`, trade on the literals without the world market, while the gate (`ship.sh` through
`JDK_JAVA_OPTIONS`) and `tools/nightly.sh` name `data/earth-2.bin` and `data/world-1.bin`. A batch scored
like-for-like is scored on a city no player runs; RE2's posts read differently under the world market (RE2-notes,
§3 A2).

1. **Name them.** When `JDK_JAVA_OPTIONS` is unset, `ensemble.sh` adds `-Dcbs.earth=` and `-Dcbs.world=` for the
   main checkout's `data/earth-2.bin` and `data/world-1.bin` to `JAVA_OPTS` (line 104; it reaches the runner as
   `ENS_JAVA_OPTS`, line 71), each only if the file is there, as `nightly.sh` does at lines 59-62 with `$MAIN`.
   The run's `baseline.md` paragraph says which data files it named, or "none".
2. **One baseline under the new setting**, on main's tip, committed as `docs/ensemble/EN2.md`; its paragraph
   says it is the first scored with the data files, so a later batch scores against it, not 0.9.50's or RE2's.
3. **The rule's line**: `docs/orchestrator.md` or `docs/batch.md`, wherever the ensemble's baseline is named,
   one line that the baselines from EN2 on name the data files. Remove about as many lines as it adds.

Done when: a worktree's `ensemble.sh` run names both files (its baseline paragraph says so), the baseline is
committed, `sh tools/ship-selftest.sh` is not needed (no `ship.sh` change), and the line count is in the notes.
