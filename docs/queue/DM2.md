# DM2 - the wall ways dated: a journal of when each way was kept clear (left by DM1)

DM1 (2026-11-03) mended what moved standing boxes with no wall event behind it: FW11's way kept clear for a street
piece the buildings wall in was worked out again, from the whole log, on every plan, so each month's arrivals changed
it (6, 5, 20, 22 plots across four months of MapCheck 2h) and first-fit cascaded each change into moved boxes (12 and
23 on FC1's path). DM1 dates the way by when the piece was walled in (`DistrictPlan.Builder.wallJoin`, `pieceBorn`,
`wallWalk`): the buildings that came after cost nothing, so the way is the same the month after. What is left is the
wall *events*, where a way really does change, and first-fit follows with a cascade the plan cannot tell from a defect.
With FC1 on, three of MapCheck's checks are red for them, and `MapCheck.main` keeps `buildersShortOn` off through 2h
until DM2:

1. 2d, "...the arrivals after it go on with the yard's ground left as it is: every box stands four months on": 9
   boxes moved. A second piece is born walled in at month 133 by houses that stand; its way (22 plots) crosses
   standing boxes, which leave their places.
2. 2d, "a yard that goes moves none of the buildings": 73 moved. The yard's ground cured a wall; when the yard goes
   the wall is back, the way is kept clear again and the displaced boxes cascade.
3. 2h scenario 1, the reserve month, "...moves off it the boxes it lies across...and no others": 76 moved against 71
   lying. A reserve re-routes a way.

The cause of all three is that the plan is a pure function of its Input and the Input carries no memory of the ways:
the way is found on the log as the buildings stand. The mend is the one the runs got in FX7/MF1/UP5 (`Input.fixedLog`,
`fixedLogMonth`, `moveOff`): a per-district journal of the ways, each plot dated with the month it was first kept clear
(and the month it stopped), in `CityMap`'s sidecar; the plan keeps a dated way clear from its month and a standing
box displaced by it re-arrives where it fits without a cascade (`moveOff`), and a way that is cured (the yard goes)
is let go only for the boxes that come after.

It touches the saves: the journal is written and read in the sidecar (`CityMap.FORMAT` bump; `DataSave`/`HistorySave`
rules in CLAUDE.md, `ReadPathCheck`, `SaveFileCheck`, `MapCheck` 2h's sidecar round trip), and the month a way is
new has to be found where the map already dates the runs (the model does not read the map, so `docs/month-order.md`
should need no new statement; check it). So it is no small fix batch: Opus, with the usual slices.

Done when: MapCheck is green with FC1 on - its `BuildingManager.buildersShortOn = false` at the top of `main()` and
the `= true` after `reservesDated(city)` deleted, with their comment - the journal round trips in the sidecar, and
SlopeCheck 9 and the playtest's layout watcher (LD2-D too) stay green, and the default playtest's trace is unchanged (the map is not the model's).


## What DM1 found and left for DM2

1. **Dating from the replay alone does not work.** DM1 tried it (commit 77e40c87 on `batch/DM1`'s history, not shipped:
   `Builder.wallRank`, `rankArrivals()`, `cellOpenBox`/`cellBorn`, `pieceBorn`, `wallWalk`, `wallJoin`): a piece's birth
   from the box count when its latest cell opened, the wall's completion from a binary search over the arrival ranks,
   the buildings after that costing nothing. It stops the drift in MapCheck 2h on a city built without the Earth file
   (6, 5, 20, 22 plots to the same plots; 27 moved boxes to 0), but SlopeCheck 9's Medellin run (Earth and world
   files named) moves 40 boxes of district (0,0) at m608 to m609: the district bought 320 plots, its street pieces
   re-formed (a piece born at box 117 became one born at box 42), and the way jumped through boxes of rank 189, 203
   and 204 that stood in the month before's final plan, which cost nothing under the dating. The replay cannot tell a
   new way from an old one; the journal can. Reuse those pieces for dating the journal's entries; the walk costs
   buildings by rank.
2. **LD2-D, the same defect on LD2's path:** the playtest's "a building on the map moved", m3010 to m3013, 13 of
   district (-1,-2)'s 1127 boxes (7 Lime Kilns, 6 Rod Mills); it passed at LD2's gate on Jerus's "Ship, fix next".
   DM2 asks the playtest's layout watcher as well (LongPlaytest, THE MOVES), not only MapCheck.
3. **The environment:** a batch session's `batch-check.sh` builds cities without the Earth and world files, unlike the
   gate, so its MapCheck is red in 16 places on BD1's tip and its 2h shows a drift the gate's city does not have.
   Run MapCheck, SlopeCheck and the playtest with `-Dcbs.earth=<main checkout>/data/earth-2.bin -Dcbs.world=<...>/world-1.bin`
   (`JDK_JAVA_OPTIONS`, as the gate names them). With them, 2h's district has no walled piece, so 2h cannot show
   the drift; the first slice is a fixture that causes a wall with arrivals after it (PlanCheck 18's `fieldBelt` with a
   `Log`, or PlanCheck 17's played city advanced a few months with `LongPlaytest.run(g, 1)`), checked against the
   unfixed planner so it bites.
