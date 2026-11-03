# DM1 - the wall events found behind a reserve's moved boxes; the fix held for DM2 (0.9.8, 2026-11-03)

FC1 found it (`docs/queue/DM1.md`): MapCheck 2h, on FC1's path, moved 12 and 23 boxes of the plan four months on
(0 without FC1), where "what stands never moves" (CL3). DM1 found the cause, tried a planner fix, and found the fix
cannot ship without the journal DM2 builds. It ships a comment, DM2's brief and nothing else (Sonnet 5.5 whole, the
orchestrator's call, 2026-11-03).

## 1. The cause

FW11's way kept clear for a street piece the buildings wall in (`Builder.wallJoin`, the third way of
`joinFromSmall()`) is found from the plan as the buildings stand with nothing yet kept clear, so every month's
arrivals change which way is cheapest: on MapCheck's played city (FC1 on, no Earth file) the way kept clear was 6
plots the month the wall came, 5 the month after, 20, then 22 four months on, as arrivals took the plots of the way
the plan before had kept clear (the plan before is not what the next is made from: the ways are found on a plan that
knows none). Each change moves the boxes first-fit gave those plots to the next lot, and the next lot's to the one
after: 27 of 610 boxes stood elsewhere four months on, none on a run. A cascade, not a defect of one placement.

## 2. What was tried, and why it is not shipped

`DistrictPlan` with the way dated from the replay alone (commit 77e40c87 in this branch's history: a piece's birth
from the box count when its latest cell opened, the wall's completion from a binary search over the arrival ranks, the
buildings after that costing nothing). It does stop the drift on the city built without the Earth file (2h's
four-months-on and its block-bought scenario green with FC1 on), but `--pick` exits 4: SlopeCheck 9's Medellin city
(Earth and world files named) flags `district (0,0), m608 to m609: 40 of its 210 boxes moved`, where BD1's unfixed
planner is green. Traced: that month the district bought 320 plots, its street pieces re-formed (a piece born at box
117 became one born at box 42), and the dated way jumped through boxes of rank 189, 203 and 204 that stood in the
month before's final plan, which cost nothing under the dating; the old rule's cheapest way over every building stayed
where it was. A replay cannot tell a new way from an old one; only a journal can (DM2).

## 3. What ships

- MapCheck's comment on `buildersShortOn = false` says what keeps FC1 off through 2h: the wall events (2d four months
  on, 9 boxes; 2d's yard that goes, 73; 2h's reserve month, 76 moved for 71 lying) and, on a city built without the
  Earth file, the drift, with the pointer to DM2. No code changes: `DistrictPlan` and MapCheck's checks are BD1's.
- DM2 queued in the ship file; `docs/queue/DM2.md` carries the finding above, LD2's Pending row LD2-D (the playtest's
  "a building on the map moved" at m3010 to m3013, 13 of district (-1,-2)'s 1127 boxes), the reusable pieces
  (`wallRank`, `rankArrivals`, `cellBorn`), and the environment note below.

## ★ calls

1. **A batch session's checks build cities without the Earth and world files.** `batch-check.sh` does not name them
   (`tools/nightly.sh` does), so MapCheck there is red in 16 places even on BD1's tip, and 2h shows a drift the
   gate's city does not (with the files named, 2h's district has no walled piece, and 2h's four months on are green
   with FC1 on and the unfixed planner). I ran DM1's checks with `JDK_JAVA_OPTIONS` naming them. **Recommendation:**
   queue a process batch for `batch-check.sh` to name them as `nightly.sh` does (`$MAIN/data/..` when they exist and
   `JDK_JAVA_OPTIONS` is empty), one in and one out.
2. **The default playtest's trace** is unchanged (no code changed), so the ensemble is not run.

## Count

No process change: nothing under `tools/`, the scripts or the rules a session reads.
