# PO1 - the path lines as nightly observations

Jerus, 2026-11-12, on the harness audit's second round (question box): of the
fixture lines that only hope the played city reaches their condition, "Nightly
observations" (not new fixtures, not mended as they flip). The path lines
print but never fail a gate, and every law stays asserted. A fault a new path
exposes shows at night. After HA1: it touches the same files
(`tools/nightly.sh`, the harnesses, and a soft/hard helper beside `Timing`).

## What the game wants

A batch whose change moves the played city no longer stops at the gate on a
harness that went looking for a city state and did not find it this time. The
nightly still reads every such line on main and says which merge moved it.

WHY: measured 2026-10-15..11-12. Re-running the same code changed a result 13
times outside the timing harnesses in four weeks: the game is deterministic.
A model change moving the city's path did it 465 times, in 132 harnesses.
Two readers went through the 54 harnesses with the most (357 events): about
230 were a fixture line that only hoped the city got there ("the city has a
piece of paper to close", "a surplus over the slice at month 72", a month
pinned by hand: ScaleCheck's FREE_FIXTURE_MONTHS re-pinned 8 times). About 99
were a batch changing the rule on purpose. About 43 were real faults a new
path exposed. No harness is randomness as a whole: the path lines are about
7% of those harnesses' 10,626 assertions, next to laws that hold on built
fixtures.

## What to build

1. **The helper.** A path line is a call through one helper, the way a clock
   goes through `Timing`. The suite and the gate run it soft: a line that
   misses prints `OBSERVED - not reached: <label>` and is not counted. The
   nightly runs it hard (`tools/nightly.sh`, beside GR1's timing). Every miss
   is a line in the night's report, with the merges since that line last
   held. A miss does not fail the night.
2. **What stands on a missed premise.** A premise line that misses skips the
   law lines that need it for that run. The skip prints which lines and why.
   A law line never passes on a state its premise did not reach. A law line
   that does run counts as now, at the gate too.
3. **Which lines.** `docs/queue/PO1-lines.tsv`: 469 lines in 29 harnesses, by
   harness, with the law each guards and the queued rows that patch them.
   Counts are estimates (+-30%), and line numbers are of 2026-11-12. Re-read
   each harness and mark only a line whose truth depends on the run's path:
   a share over a run's samples, a premise found in a played city, a figure
   at a pinned month. A line whose fixture causes its condition stays as it
   is.
4. **Main's known reds.** An entry in `docs/notes/known-reds.txt` whose line
   becomes an observation goes from that file in this batch.
5. **The queued patch rows.** About 28 Queued rows exist only to mend such a
   line (KS2, KBF1, KHT1, KL1, KS1-rest, LM1-R, BQ1, M6-F, CB2, HX1, RN1-F,
   WG1-R, LD3, DQ1, WG2-R and others in the tsv). The notes list each with
   what in it is now only an observation. They stay queued until Jerus
   reviews the list. Rows with a model half (DQ1, RT3, ...) keep that half
   in any case.

## Done means

- A fixture causes it: a premise made to miss (a slice of a harness with its
  condition held off) prints OBSERVED at the gate, exits 0, and skips its
  laws. The same run hard is listed in the nightly's report with the merge
  range.
- `AllChecks -f` on main lists no red that is a marked line.
- The notes give, per harness, the lines marked and the lines left as they
  were.

## One in, one out

The helper and its nightly report are new machinery. The known-reds entries
they retire, and the timing-like code they share with `Timing`, are the lines
out. The notes give `git diff --numstat origin/main` over those files.
