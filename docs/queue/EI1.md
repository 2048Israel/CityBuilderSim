# EI1 — the ensemble's outcome criteria as information

Jerus, 2026-11-13, after HW1 (every government service built when needed)
was held by the ensemble's T2 (final population at least 75% of the
baseline's) in SHOCK, HIGH and ZERO_HOT, three scenarios that end at
months 840, 720 and 402: "for once i think the ai is no longer good enough
to test this game. the game is too complex for ai too playtest". Asked
which checks to keep: "so based on that, we should only keep some of the
harnesses, and the timing stuff right". On the split proposed (rules a
fixture causes, saves and the money audit stay hard; timing is judged
nightly; readings of where the played city went and the ensemble's
outcomes against the last baseline become information): "yes queue it and
do it next".

## Why

The ensemble's outcome criteria ask whether a city came out the same as
last time, not whether it is better. A change whose payoff comes later
(AW1, HW1: build every work the city needs) reads as a failure when a
stressed scenario is scored at month 402. They still carry information
worth printing beside the baseline; they stop being a ship stop. PO1 does
the same for the harnesses' path lines; this batch does it for the
ensemble.

## The batch

1. **Hard criteria:** in every policy, the money audit (M1, months the
   audit failed) and the findings (F1, runs with a LongPlaytest finding).
   A batch whose trace moved ships only if both hold, as today.
2. **Information:** every other criterion (A1-A3, S1, H2, K1, K2, R1-R3,
   T1, T2, and any added later unless marked hard). They are still run,
   scored and printed against the newest baseline, with each verdict that
   changed called out, so that Jerus sees them. They never stop a batch.
3. `tools/ensemble/score.py` gives each criterion a class (hard or info)
   and prints the batch's verdict on the hard class alone; the baseline
   file shows the class column. A self-test covers a run that fails an
   information criterion and passes, and one that fails M1 and stops.
4. The rules that say otherwise change with it: `docs/batch.md` 6,
   `docs/orchestrator.md` ("The gate", step 1), `tools/ensemble/README.md`,
   and the brief template's ensemble line.
5. One in, one out (`CLAUDE.md`): the notes give the line count over the
   process files. No tag (process). No game code; the playtest does not
   move.

Expected files: `tools/ensemble/score.py`, `tools/ensemble/README.md`,
`docs/batch.md`, `docs/orchestrator.md`. GG1 also edits `docs/batch.md`:
EI1 goes first.
