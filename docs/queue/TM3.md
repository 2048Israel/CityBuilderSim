# TM3 - the gate's timing judged side by side: main against the batch, in the same minutes

Queued 2026-10-26 by the orchestrator, on Jerus's word.

## Jerus, 2026-10-26 (the requirement)

> question, if something doesnt really change map stuff, then if the
> mapcheck fails cause of timing its not really an issue no? or perhaps a
> margin of error if other stuff is running?

Asked whether to fix the gate with a paired comparison, he answered: "queue
the TM3 batch".

## Why

HY1 (the Hydro Station) touched nothing that MapCheck's timed steps run.
Its gate on 2026-10-26 still stopped on three MapCheck timing lines:
- the FX-side map work at 5B, a 59.9 ms median against its 58 ms budget;
- Rotterdam's far node at one stride over SCREEN_MS;
- a month's change at 10 billion over its 5 ms.

ship.sh then timed main by itself, minutes later and in a quieter moment.
Main passed, so the reds were called the batch's. Earlier the same day,
main's own run of the 5B line had read a 75.5 ms median. A wall-clock
budget measured once on a PC that is also running Jerus's game and other
sessions is a measure of the load as much as of the code. TM1 (soft timing
beside other runs) and TM2 (a quiet minute before the hard run) did not
close it, because the game is not waited for and the load changes between
the batch's run and main's.

## What TM3 does

1. **Main and the batch timed alternately.** When the merge's hard timing
   run has a red line, ship.sh times that harness again with main's classes
   and the merge's classes in turn: main, merge, main, merge, at least two
   rounds, back to back in the same minutes. TM1's soft wait and TM2's quiet
   minute stand as they are.
2. **A red counts against the batch only if the batch is slower than main**
   by more than a margin, at the median of the paired runs, on the red
   line's own measured figure. The margin is a dial, 15%, with its sentence
   in `Timing` (or wherever the gate's arithmetic lives). Otherwise the red
   passes:
   - named in the merge's `Gate:` line with the paired medians;
   - in `docs/notes/gate.txt` only as a `#` line, never as a recorded red.
3. **The figures read, not guessed.** Each timing assertion writes its
   measured figure and budget where the gate can read them: a line in a
   form `Timing` defines, or `target/checks/timing.tsv` with harness, label,
   measured, budget. The paired step reads those, never the label's prose.
4. **No budget moves.** A harness's budget and premise stay as they are
   (README, *The checks*). TM3 changes only how the gate judges a red.
5. **`--red-ok` reaches the timing step.** Today it covers only the suite
   step's new reds. A timing red that main does not share stops the ship
   whatever Jerus said, so MS2 (0.8.55, 2026-10-26) shipped on his "Ship it"
   through a local copy of `ship.sh` changed at that one stop. With
   `--red-ok "his words"`, the timing step's own reds pass too, and the gate's
   record and the merge's `Gate-timing:` line carry his words and the lines.
6. **Wall-clock comparisons are timing too.** MapCheck's "...and no more than
   section 5's at either size: the view adds nothing a tile" compares two
   measured times, but it is not a `Timing` assertion, so it stays hard in
   the suite step. Under Jerus's running game it went red on CC1-S's merge
   (0.8.56), which cannot reach it. It passed under a `--red-ok` given for
   another line, and ship.sh wrote it into `docs/notes/gate.txt` as a
   recorded red. The orchestrator took it out again so that it masks no later
   regression. Find every such comparison and make it timing (soft in the
   suite, judged side by side at the gate). A `--red-ok` names the lines it
   passes, never more.

## Scope and checks

- `ship.sh`, `batch-check.sh` (and `Batch check.bat` if it judges timing),
  `Timing`, the timing harnesses' reporting if a figure is not yet written,
  and `docs/batch.md`'s gate section (step 6 and "The calls BP1 left", 5).
- `sh tools/ship-selftest.sh` grows two cases, with stub figures:
  - a timing red where the batch is as fast as main: passes, named;
  - a timing red where the batch is 30% slower: stops.
- TimingCheck covers the figure's record and the margin's arithmetic.
- The first real gate after it ships says in its notes what the paired step
  read.
- No model change: the trace is byte-identical, and there is no ensemble.
