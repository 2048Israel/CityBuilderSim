# KT1 - KeptCheck's run time: under 150 s, every section kept

Jerus, 2026-11-06, on the process write-up's bottlenecks ("Queue it", to "Queue
a batch to cut KeptCheck's run time"). The figures below were measured on the
PC that day.

**Why.** KeptCheck is the suite's longest harness: 408 s of a 2,751 s suite
(`times.tsv`, 11-06 08:32), then 550 s and 636 s later that day, about 15% of
every gate's suite. It is in `Timing.HARNESSES`, so the gate's hard timing step
runs all of it again (408 s of that step's ~941 s). A `--pick` that reaches it
costs ~24 min (XR5: 49 harnesses in 1,420 s, 60% of it KeptCheck and
CentralBankCheck). Its heap sits at ~1.95 of its 2 GB (KP2c's row), and it ran
out once (KP2b). It is not in `AllChecks.LONGEST_FIRST`, so a parallel run
starts it late.

1. **Find where the time goes.** Per-section seconds from one soft run on the
   PC: building cities, playing months, sweeping pages, or the collector at
   the heap's edge. The notes give the table.
2. **Cut it, premises kept.** Every section keeps its fixture, its condition
   caused, and its assertion against the model's constants (README, *The
   checks*). No section is dropped or sampled down to pass. Candidates, for
   the batch to judge: one built city shared by the sections that can share
   it; one sweep read by several sections; the heap's growth (KP2c finds what
   the earlier sections keep).
3. **The timing step.** KeptCheck's few timed figures (`KEPT_*_MS`) need not
   run the whole harness hard at every gate. Consider moving its `Timing`
   sections into a harness of their own in `Timing.HARNESSES`, and taking the
   rest out of that list. The budgets do not move.
4. **`AllChecks.LONGEST_FIRST`:** add what is still the longest afterwards.

**Done means:** KeptCheck, and any harness split from it, runs under 150 s in a
soft suite run on the PC; the same lines pass and fail as on main; the notes
give before and after. The gate's timing step is shorter by what moved out of
it.

**After KP2c**, which holds the same harness (its section 92 and its heap) and
the kept screens' code; this batch leaves that code alone.
