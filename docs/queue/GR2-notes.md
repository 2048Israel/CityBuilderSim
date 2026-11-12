# GR2 — known reds in the repository, and what runs where (2026-11-12)

## Built

`KnownReds` supplies the runner's line tags and the saved-log verdict used by
batch-check and ship. Exact gate records and five-column, harness-scoped
approval parts share it. Counts require a covered assertion and complete
coverage of that harness's assertions. All failing assertions reach the judge,
including assertions past the former forty-line display limit. Old tagged and
untagged logs remain readable. Rejudging retained evidence uses today's entries.

The gate files `## known-reds` alongside the other ship records. A mending
batch's entries are removed before the check, with scratch exclusions removing
their exact-record cover too. The merge deletes those entries. One-time named
approvals retain their existing command-line entry point. The merge body records
the parts actually used.

The picker selects ALL for a class over its fan-out and for suite-runner changes.
Changed harnesses select their borrowers and TimingCheck. The publisher runs a
full suite for game classes, resources, the POM and suite-runner changes. Other
changes run the selected harnesses and changed scripts' self-tests with the
compile, maps and Stale. Those partial gates preserve the whole-suite gate record.

The launcher retains commit identities, the origin, the selection, completed
checks, generated indexes and hashes. Reuse requires the same branch commit and check selection.
Main may advance through Markdown outside the ensemble and through the approval
file. The requested version is part of the check selection. Fast gates with zero
harnesses also reuse their completed evidence. Restored indexes carry the checked
source line numbers and version into the merge. A changed source tree, unlaunched evidence or an incomplete/cancelled run
gets fresh checks. Completed exit-4 evidence can pass once its unrelated reds
are entered. The retained-run directory can be named in advance through
`CBS_GATE_DIRECTORY`, and the ship file names it in a `gate-run` comment.

## Historical approvals and follow-ups

Read the LM1 and PW1 launchers and their bodies in the existing cbs-gates
folder, and the queued repairs. Seeded 68 scoped parts after removing count-only
arguments and duplicate entries. Unqualified historical strings were traced to
harness labels, including dynamic labels in KeptCheck, PlanCheck and MapCheck.
The broad historical "at today's prices" part belongs to PriceLevelCheck.
The historical "asks -1, unpainted frames 0" part belongs to ReplayCheck's
`movedChecks`, whose comparison requires zero worker asks.

The entries link to HX1, MQ1, KL1, DM2, DM3, PX1, BQ1, CB2, CL6, KS1,
KS2, RT3, BS1 and HS1. Exact gate records continue covering their other findings,
including OX1. These historical approvals predate GR2's process changes.

Two seeded findings needed queued rows, supplied by the ship file.

- SS1 follows SaveSlotCheck's held-open fixture, lines 457 and 465. It reads
  the process-wide `GameFiles.swapsThatWaited()` counter against its saved value
  plus one after the refused swap. Both approved counter assertions need a
  fixture that isolates and accounts for the refused write and any concurrent
  successful writes. Preserve the successful write, refused write and intact
  file assertions. Done means the two counter assertions hold on Windows under
  concurrent load. The storage code and fixture match main in GR2.
- GR2-R follows ReplayCheck's `movedChecks`, line 922. The historically approved
  negative worker-ask delta fails its zero-asks comparison while unpainted
  frames stay zero. Trace ReplayMoveProbe's counter sampling across a loaded
  glide and cause that case in a fixture. Done means worker-ask deltas stay
  valid and the loaded-glide assertion holds. The replay code matches main.

## Checks

- `./batch-check.sh --fast` passed the compile, maps, Stale and tree checks.
- `./batch-check.sh KnownRedsCheck` passed, including scoped matching,
  exact-record retirement, count guards, a late assertion past line forty,
  one-time qualified crash parts, literal pipes, saved-log agreement and GF1's
  owned-child timeout checks. The final source recheck exited 0.
- `tools/pick-selftest.sh` passed 17/17 cases, including the common judge's
  ALL selection, high fan-out classes and borrowed harness fixtures.
- `tools/ship-selftest.sh` passed 135/135 cases. Its new cases file and retire
  entries, preserve gate.txt for a harness-only merge, record complete used
  parts, run a full suite for game/runner edits, reuse the same commits, allow
  Markdown-only advancement, reject changed commits and working-folder
  evidence, rejudge a stopped run after its unrelated red is named, restore
  generated indexes and reuse both zero-harness fast paths.
- A separate script-only fixture passed both paths. Its companion ran once,
  and a failing companion stopped publication and restored the fixture tree.
  Evidence is `target/gr2-script-gate.log` and its ignored fixture driver.
- `./batch-check.sh --pick` selected ALL and exited 3. All 201 harnesses ran
  in 2,195 seconds, with 195 green and six known-red harnesses. These were
  MonetaryCheck, HealthCheck, PlanCheck, KeptCheck, DeathRecordCheck and
  LongPlaytest. Thirteen timing assertions were soft. Maps regenerated and
  Stale reported zero firm findings. The whole output is in
  `target/gr2-pick.log`, with the suite and every harness log preserved in
  `target/gr2-pick-checks/`. Every red was covered by the seeded file or the
  exact gate record. This run needed no additional approval entries.

The fresh-clone full dry run passed with exit 0. Its underlying suite exited 3,
with 201 harnesses run in 2,129 seconds, 195 green and the same six known-red
harnesses. Fourteen timing assertions were soft. The compile, regenerated maps,
Stale and tree checks passed. The clone returned to its original HEAD and clean
tree. No red-line overrides were supplied, and no additional entries were needed.
The kept checks and generated indexes passed their SHA-256 manifest verification.

The simulation and resources match main. The full suite includes LongPlaytest.
The ensemble is skipped under EN1 because this process batch leaves that model
and its trace unchanged.

## Retained launcher runs

The first launcher run was stopped by the builder to complete generated-index
restoration and zero-harness replay. Its origin refs were unchanged. Its log is
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GR2-20261112-01/ship.log`.
It was branch `b1dfaa9d4f24562f569e898e1e0e088c08465cbf` merged with main
`420c65dcebd0a0371d4d785145ef904af87a7883`, exit 130. Its stopped clone is retained.

Targeted fresh-clone fixtures then passed replay for full, docs-only and
script-only gates, including their generated indexes. Changing the requested
version refused the cached indexes and ran fresh checks. Evidence is
`target/gr2-reuse-paths.log`. The additional permanent self-test cases cover
restoring regenerated indexes and both zero-harness replay paths.

The completed run is
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GR2-20261112-02/ship.log`, with
branch `7170d8472dd17f4eafca8880de18f6451ae27cb7` merged with main
`420c65dcebd0a0371d4d785145ef904af87a7883`. Its `checks/` directory retains every
harness log, suite, timing figures and verdict. Its `generated/` directory retains
the checked indexes, with both trees sealed by `checks.sha256`. The launcher exited
0 and deleted its successful clone. `branch.commit`, `main.commit`, `selection`
and `exit` retain the identities and result alongside the log.

The final notes and readiness commits follow that tested source commit and
contain only this batch's records. GR2 reaches main by the brief's fresh
review and untagged merge.

## Process line count

The count covers all changed scripts, check machinery, the approval file and
hand-authored session/tree guidance. Generated indexes and this batch's notes
and ship file are excluded. It is `git diff --numstat origin/main` over the
files below.

690 lines added, 630 removed, +60 net.

```text
2	1	CLAUDE.md
15	152	batch-check.sh
40	49	docs/batch.md
73	0	docs/notes/known-reds.txt
34	35	docs/orchestrator.md
2	0	docs/tree.md
151	92	ship.sh
18	49	src/main/java/ham/citybuildersim/AllChecks.java
129	0	src/main/java/ham/citybuildersim/KnownReds.java
63	155	src/main/java/ham/citybuildersim/KnownRedsCheck.java
38	0	tools/gate-reuse.sh
14	3	tools/gate.sh
17	19	tools/pick-selftest.sh
7	65	tools/pick.sh
87	10	tools/ship-selftest.sh
```
