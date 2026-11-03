# DM1 - the way kept clear for a walled-in street piece stays put ({next}, 2026-11-03) - HELD: the planner change regresses SlopeCheck 9

**Held, no ship file** (2026-11-03): `batch-check.sh --pick` is red in SlopeCheck 9 only (exit 4), and it is DM1's:
the 2000-month Medellin city's layout watcher flags `district (0,0), m608 to m609: 40 of its 210 boxes moved`
(13 houses, 4 studios, 8 stores, 1 grocery; counts unchanged), and BD1's unfixed planner is green there. Traced
(scratchpad, `DM1T`/`DM1W` lines): that month the district bought 320 plots (owned 6800 to 7120), the first pass's
street pieces re-formed (a piece born at box 117 became one born at box 42) and the way kept clear jumped to other
plots through boxes of rank 189, 203 and 204, which cost nothing under the dating ("they came after the wall") but
stand in the month before's final plan, since that plan's way was elsewhere; the old rule's cheapest way over every
building stayed where it was. The dating is right only while the pieces stay as they are; telling a new way from
the old one needs the journal (DM2). The planner change is therefore not shippable alone; see ★ 0.

FC1 found it (`docs/queue/DM1.md`): MapCheck 2h, on FC1's path, moved 12 and 23 boxes of the plan four months on
(0 without FC1), where "what stands never moves" (CL3). DM1 found the cause and mended it in `DistrictPlan`; three
checks that meet a wall event itself stay with FC1 off until DM2 (below). Sonnet 5.5 did the session whole.

## 1. The cause

FW11's way kept clear for a street piece the buildings wall in (`Builder.wallJoin`, the third way of
`joinFromSmall()`) was found from the plan as the buildings stood with nothing yet kept clear, so every month's
arrivals changed which way was cheapest: on MapCheck's played city (FC1 on, no Earth file) the way kept clear was 6
plots the month the wall came, 5 the month after, 20, then 22 four months on, as arrivals took the plots of the way
the plan before had kept clear (the plan before is not what the next is made from: the ways are found on a plan that
knows none). Each change moved the boxes first-fit gave those plots to the next lot, and the next lot's to the one
after: 27 of 610 boxes stood elsewhere four months on, none on a run. A cascade, not a defect of one placement.

## 2. What was changed

`DistrictPlan`, a section "THE WAY KEPT CLEAR, DATED BY WHEN THE PIECE WAS WALLED IN" in place of FW11's `wallJoin`:

- A piece is walled in at a time, and the way costs only the buildings that stood then: a building that came after
  the piece was walled in costs nothing, so the plan keeps the way clear from the start and it never lands there. The
  time is the later of the piece's birth (`pieceBorn`: the boxes placed before its latest cell was opened,
  `cellOpenBox`/`cellBorn`, filled where the events open a cell and where `buildArrivals` compacts the gone boxes
  out) and when the wall was complete (the latest arrival rank every way crosses a building of or before, found by
  a binary search over the ranks in `wallJoin`). `wallRank` is a building's place in the order the boxes were placed
  (`rankArrivals`; the first batch's all rank 0). `wallWalk` is the one breadth-first walk of FW11 with the cost
  counted per rank.
- A plan with no log (the counts alone) has every building at rank 0, so it costs what FW11's cost: PlanCheck 17 and
  18 are as they were.
- `DistrictPlan.wallWay` (new, public): the plots kept clear, sorted, for MapCheck 2h to ask that the way stays put.
  The plan is still a pure function of its Input; no save changes (the map recomputes its boxes from the input).

## 3. What was found, what was left

- **The way no longer drifts.** On the FC1-on city MapCheck 2h is cut from (no Earth file), the way kept clear is the
  same plots the month after and four months on, and 2h's "none of those boxes has moved" and its block-bought
  scenario are green.
- **Three checks stay red with FC1 on, and are genuine wall events, not drift:** 2d four months on (9 boxes moved: a
  second piece is born walled in at month 133 by houses that stand, and its way of 22 plots crosses standing boxes);
  2d "a yard that goes moves none of the buildings" (73: the wall the yard's ground cured is back when the yard goes,
  and the boxes it displaces cascade); 2h scenario 1's reserve month (76 moved for 71 lying: the reserve re-routes
  a way). They need the wall ways dated in a per-district journal in `CityMap`'s sidecar, as the runs' are
  (`FORMAT` bump, so saves): **DM2**, queued in the ship file, its brief `docs/queue/DM2.md`. DM1 ships the
  stabilisation alone because the orchestrator said so on 2026-11-03.
- MapCheck keeps `buildersShortOn` off through 2h, its comment narrowed to those three events, and 2h has a new
  assertion that every plot kept clear in the reserve's month is kept clear four months on.

## ★ calls

0. **The planner change regresses a real city (above).** Options: (a) DM1 ships without the planner change (MapCheck's
   narrowed comment only) and DM2, the journal, mends the drift and the three events at once, taking `wallRank`,
   `cellBorn` and `rankArrivals` from this branch's history; (b) ship the change and take SlopeCheck 9 red: no.
   **Recommendation: (a).**

1. **The new 2h assertion is vacuous on the gate's city.** With `earth-2.bin` and `world-1.bin` named, as the
   gate names them, the FC1-on city has no walled piece in 2h's district (0 plots kept clear in either scenario),
   and 2h's four months on are green with FC1 on, with or without DM1's fix: I ran MapCheck with FC1 on in 2h
   against BD1's unfixed `DistrictPlan` to see. The drift shows only on cities built without the Earth file, which
   is what a batch session's `batch-check.sh` runs (it does not name the files, so MapCheck there is red in 16 places
   even on BD1's tip: the second block's fixtures, 2d, the crossing). **Recommendation:** keep the assertion (it is true
   and costs nothing), and let DM2's first slice build a fixture that causes a wall with arrivals after it (PlanCheck
   18's `fieldBelt` with a `Log`, or PlanCheck 17's played city advanced by `LongPlaytest.run(g, 1)`), so the
   stability is asserted where the gate runs.
2. **A batch session's MapCheck is red without the Earth file** (the finding above). Not DM1's to mend; the
   orchestrator may want `batch-check.sh` to name `data/earth-2.bin` and `data/world-1.bin` as `tools/nightly.sh`
   does (`$MAIN/data/..` when they exist and `JDK_JAVA_OPTIONS` is empty). **Recommendation:** queue it as a
   process batch, one in and one out. I ran DM1's checks with `JDK_JAVA_OPTIONS` naming them.
3. **The default playtest's trace**: no model code changed (the map is not read by the model), so the ensemble is not
   run.

## Count

No process change: nothing under `tools/`, the scripts or the rules a session reads.
