# FS1 - the tests that break when another batch moves the played city (a study)

Jerus, 2026-11-18, on why the queue stays near 70: a rule that a batch mends
all it finds "mends its own results in endless make batch, fail, redo, fail,
redo, fail, resulting in tons of redos and wasted gate time". Then, on this
study placed after SZ1 with no new standing rule: "Yes, queue it".

## Why (measured 2026-11-18 on main 79e336def)

Since 11-16, 34 ships deleted their 34 rows and their ship files added 45. Of
the 26 that read as defects, 11 are real bugs found in passing (QR1, MR1, OX2,
CL7, RB2, R3b-R, CPB2, DK1, DM4, HN1-A, HN1-B) and stay as they are. Twelve
are the suite's own fragility, and they turn a correct batch red at its gate:

- **A moved path** (7): HP1, HP2, PX2, SX2, KFP1, FF1, PK1. Main's code is
  right; the harness reads a played city at a picked month, and a batch that
  moves the playtest moves what it finds.
- **Under load** (5): KRT1, KBP1, KRW1, QW1, MQ1. Green alone, red beside a
  gate's JVMs or on the wall clock.

The FB ships removed 11 rows and added 11: each red is fixed one row at a
time, and the batch that fixes it finds the next. The standing rule is
already "a fixture must cause the condition" (`docs/contract.md` 1); these
harnesses were written before it held.

## What the study does

1. **Sort every harness** (`AllChecks.HARNESSES`, 221 files) by how each
   section gets its condition: (a) caused: a town built for it, the state set
   directly; (b) played: months run, on autopilot or by its own loop, and a
   picked month or a played city read; (c) clocked: a wall-clock wait or a
   reading that moves under load. The greps are a start, not the count: 22
   harnesses call `LongPlaytest.run`, `AUTOPILOT`, `AUTOBUILD` or `advise`;
   41 more use only its build helpers (`build`, `villageBuild`, `founding`);
   GoldStandardCheck, KeptCheck and FundCheck broke on a moved path with
   little of either.
2. **For (b), section by section:** whether it truly needs a played city
   (`LongPlaytest`'s own audit, the ensemble's kind: those stay, named, with
   why) or how it would cause its condition instead (a fixture town, the
   model's constants, the threshold caused from both sides).
3. **Group (b) into sweep batches**, each a queued row with its brief in
   `docs/queue/`, sized for one gate each (`docs/contract.md` 4), grouped by
   the fixture builder or area they share so no two in a group's lane meet
   (`docs/contract.md` 3). All after HL1, which changes `LongPlaytest` and 8
   harnesses.
4. **For (c), a call for Jerus**, with a recommendation: each harness's
   wait, and whether it runs alone, in the nightly, or on a clock-free wait.
   It changes what the gate runs, so it is his.
5. **Fold in the rows of the class**: the twelve above, and any others the
   sort finds (BQ1, LM1-R and KS1-rest read like it: "the fixture looks for a
   term market that clears instead of causing one"). Name each in the notes
   for the orchestrator to fold into the batch that takes it (`QUEUE.md`'s
   rules).

## Meanwhile, and the gate

- A red of class (b) or (c) on any batch's gate names **FS1** as its queued
  fix in its `## known-reds` row, which `docs/contract.md` 5.3 already allows
  ("in `## queued` or already on the queue"): no new row, no FB bundle. When
  FS1 ships, its batches take those rows over.
- Docs only: no game code, so `--fast` alone at the gate (`docs/contract.md`
  6), untagged. It reads, so it runs beside anything, HL1 included.
- Done means: every harness classed, with its sections; the (b) groups queued
  as rows with briefs; the (c) call written for Jerus; the folded rows named.
