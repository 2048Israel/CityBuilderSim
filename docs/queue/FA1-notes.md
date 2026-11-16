# FA1 notes — frontage avenues beside diagonal highways

## Baseline

Today's main was `01402a54e58881a5283a84933e766a38b5f1a089` (0.9.120),
also this worktree's starting commit. No known-reds entry names FA1.
`./batch-check.sh --fast` passed. MF2DiagonalProbe compiled unchanged and
exited 1: 5,433 buildings, all 64 cells open, 439 highway plots; 6,936 strip
plots 73.3% used against 81.9% in the control (loss 8.5 points, margin 2).
All 52 reaching street pieces ended without frontage, zero frontage pieces.
Log: `target/fa1-main/probe.log` (local). The two original laws remain hard.

## Calls

★ Frontage uses the existing avenue layout and marks, with separate origins
and junction keys for additional arms. Discover it from replay's current
`fx`, never the final runs hidden behind future journal records. Cells
already open retain their streets and boxes; later frontage may take only
unoccupied plots. The map's saved sidecar shape is unchanged.

★ The orchestrator accepted the probe's laws over the header's suggested
lattice snapping (mail 20261115-204416-457-orchestrator-2f4f). The highway's
normal coordinates are 37,38; its verge spans 35..40. A snapped high-side
40,41 line leaves isolated plots in the probe's inner rings; 48,49 lies
outside them. The nearest outside staircases are 33,34 and 41,42, with
independent origins. They are new arms of CL4's tables; only heading bits,
never an arm index, enter the painter's existing avenue marks.

★ Small homes open frontage-facing cells before the square hinterland. An
avenue alone connected the streets but left estate wedges without small
buildings to fill them (75.8% strip use). Opening those cells for homes uses
94.9% against the unchanged control's 81.9%, including the existing rule
that keeps each smooth band's outer plots free. No change to tryPlace's
packing or to the fixture's mix, fill, margin or distance measurement.

★ PlanCheck's independent junction walker now reads every marked frontage
line with its own origin, using the existing feet and LATTICE floor. It
found 6 violations before the floor change (new low-side ring crossings a
step apart). The new-cell floor includes the new arms' ring feet and a
2-plot halo so opening the next cell cannot restore a refused shared-ring
foot: 0 violations after. Its premises remain hard in Figures too.

★ A later frontage never takes up the old grid's feet. It stops its new
band at conflicting crossings and occupied plots, and trims only its new
dangling tips. The fully open dated fixture preserves every old grid plot
and every old box beyond the highway's own verge: 0 lost streets, 0 such
boxes moved, 0 floor violations, 0 new dead ends. It lays 123 of 878
frontage-line plots and omits 755; its strip is 64.1% used. Highway-induced
move-off remains MF1's, 155 boxes in this fixture. The opposite diagonal
also keeps all plan laws, and a second dated replay is identical.

The orchestrator requested the same late fixture on unchanged main. Compiled
`01402a54e`'s DistrictPlan separately (`target/fa1-main-model`) and ran the
identical new section against those classes: 4,373/6,936 strip plots used,
63.0%, against FA1's 64.1%. The section now asserts the exact main numerator
with that source; late frontage improves rather than lowers this use. Log:
`target/fa1-main-model/section.log`.

Review caught an artificial two-frame cap on the original arms' step range:
the first full named PlanCheck exposed it in the existing far-origin
crescent fixture (section 14c). Removed the cap, restored the unlimited
founding rays and widened junction keys to longs (arm above 32 step bits).
The unchanged section 14, run alone, is green again. No fixture premise moved.

★ Retaining the grid applies when a neighbouring frontage cell opens too:
if an old foot becomes a junction only because of a new continuation, the
new continuation stops instead. The eight-month growth fixture opens four
further cells and keeps all old grid plots and boxes, with every plan law.
ROLE_JOIN paths are the planner's temporary joins and are rebuilt normally;
they are not a cell's retained grid. New frontage is subject to the same
water bridge-length law as the founding arms. Junction keys use 32 bits for
the step and 32 for the arm, avoiding the old four-arm key's bound.

The painter reads heading and corner bits, not an arm number; the mark
format is unchanged. Final review found that a new crossing away from the
founding origin also needs the second stroke restored (below). Changes are
confined to DistrictPlan, TileRaster, PlanCheck and MapCheck. No sidecar
fields or byte layout changed; no save or model code changed.

## Selected verification

Final `./batch-check.sh --pick`: 18 harnesses ran in 672 seconds, 17 green,
MapCheck alone known red (DM3's monthly map totals, BF1-M's raster network,
DM3-R's surface accounting); no new red. PlanCheck, LongPlaytest and both
new and unchanged geometry sections are green. Ten soft timing assertions,
two MapCheck path observations; default suite pool used, two CPU start
holds. Maps regenerated, Stale zero firm findings, LF checks clean. The
saved suite was rejudged separately: known red only (exit 3).

The model is unchanged; its eight default trace files will be captured by
the mandatory gate and compared with the orchestrator's current-main
reference (XR7-postmerge, current main 01402a54e). No ensemble unless that
comparison actually moves the trace.

## Main advanced during the first dry run

The first normal gate was of branch `dfb430660d0f91c23416d6425538ee0e43e83f57`
and main `01402a54e58881a5283a84933e766a38b5f1a089`, retained at
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-FA1-YE7FRJtp`. The orchestrator
shipped R0h while it ran and instructed cancellation. Stopped through
`tools/gate.sh --stop`: exit 130, not reusable. The launcher checked its
collected process tree exited; a subsequent Win32 query found neither the
old native gate root 43928 nor detached host 8180.

Merged new main `a3fb38c06de26cf44e462597e48278413b31300a` (0.9.121).
Only generated harness and file indexes conflicted; the merged fast check
rebuilt them, compiled and passed Stale. No FA1 implementation or fixture
changed. The new trace reference is the orchestrator's R0h run:
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R0h-NrRv6F33/traces/trace-*.csv`.

## The first completed gate and the painter mend

`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-FA1-BJZZNAHT`: branch
`d5af3da2b955a5807e2322a23530be39b5b3d3f9`, main
`a3fb38c06de26cf44e462597e48278413b31300a`. Gate exit 0; its suite took
1,906 seconds and exited 3, known red only in DeathRecordCheck, InvestCheck,
MapCheck, MoneyDatesCheck and ResearchViewCheck. All eight CSVs captured in
this gate (`target/FA1-gate-trace-R0h-*.csv`) are byte-identical to the
orchestrator's R0h reference; no ensemble. The copied reference and hashes
are local under `target/fa1-R0h-reference*`.

The existing MapCheck findings and its two path observations match R0h's
retained main log: the played-city raster has 561 road plots, two pieces,
the largest 494, one joinable piece; the other logged network figures match
too. They were not introduced by frontage. The gate finished before the
attempted launcher stop; that stop returned 'name an active run directory',
and killed nothing. This green run is superseded by the following source
mend and is not claimed for reuse.

★ Final painter review found that the first mark on a shared plot hides a
second diagonal at a new crossing away from the founding origin. The old
painter repairs that at the founding origin alone. A real-raster probe on
FA1's actual plan missed 5 of 24 centre-line samples. Permanent MapCheck 24
was committed first (`02d6e008`) and run with `c5bd5df2`'s painter compiled
separately: 92 of 384 centre-line samples missing, zero four-road signals,
exit 1. It causes both slopes, both staircase phases, both frontage sides,
4/8 pixels a plot and a crossing inside a tile or on four tiles' corner.

The mend infers the occluded step from the real same-heading step on each
side, restores its band and free outer corners, and lets the crossing's
normal road count choose its signal or roundabout. It uses the existing
surface, bridge, ownership and blocked-plot rules; no new origin or saved
mark is added. The permanent case now has 0 missing centre samples, 0
missing width samples, 32 four-road signals, identical overlapping tile
reads and identical redraws. Existing avenue section 16 passes unchanged.
An intermediate centre-only repair exposed 16 missing side samples; keeping
the original band-width law required restoring the outer corners too.

Final painter verification before its next gate: named `JunctionCheck`
passed, then its whole harness passed again after restoring the outer
corners; existing MapCheck 16 and new 24 passed together, including the
original origin, signals, roundabout, seam, width and screen laws. Final
`--fast` compiled, regenerated indexes and found zero firm Stale findings.
The strengthened permanent fixture on the original painter also reports
200 missing side samples; the final painter reports zero. Existing check
strings retain their original UTF-8 bytes (verified in the final diff).
The updated pick selects 28 harnesses, not ALL; review is not needed (no
money, saves or threading in the diff). The final selected run follows.

## Final integration after PD1b and R2e

The final pre-integration `--pick` selected 28 harness names and ran 33
harnesses in 847 seconds: 32 passed, MapCheck alone known red, exit 3.
Indexes regenerated, zero firm Stale findings and a clean tree. Source was
frozen at `7f947f3188ea77e1089e53da7deb4fc6826fbcb1` while the orchestrator
held the shared slot for PD1b and R2e. Both have now shipped; main is
`32869da2` (0.9.123). No standing instruction file changed in that merge.
Only generated dials, harness and file indexes conflicted; all regenerated.

As requested at go, reran MapCheck, PlanCheck and JunctionCheck on that
merged main (BridgeMapCheck is selected by the MapCheck name too): four
harnesses, 518 seconds, three green and MapCheck's existing three known
lines, exit 3. All new PlanCheck 20 and MapCheck 24 laws pass; no change to
any of FA1's four source files since the accepted mend. No budget moved.

The preceding completed gate's loaded-PC PlanCheck figures versus main's
retained R0h gate, in its two timed fixtures: median 25.77/14.06 ms against
17.21/13.37 ms, most 58.87/17.71 against 21.92/17.35. These are informational
runs under differing load, not a paired timing verdict; the project judges
speed nightly. The plan and screen budgets are unchanged.

The final gate follows this merge. Its eight trace files use a distinct
prefix so BJZZNAHT's evidence stays intact. The orchestrator confirmed
R2e's trace is identical to R0h's and named the same R0h reference again.

## The final dry run, and who wrote these records

The Codex builder stopped at its plan's end (02:15, the batch done but for
its records); a Claude session (FA1c, Sonnet 5.5) wrote this section and the
ship file below, with no JVM and no checks.

The final launcher dry run, `sh tools/gate.sh FA1 --dry-run`, was of branch
`69673c048f0d58294fa2e8136071ebff865d409b` (FA1 with PD1b and R2e merged) on
main `32869da2811788dd0c9c627ddc4b6452a1cc85e4` (0.9.123). Kept run:
`/tmp/cbs-gate-FA1-LlVP5m4q` (`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-FA1-LlVP5m4q`).
Gate exit 0, `unnamed.txt` empty, `checks.complete` present. Its ship.log:
28 of 209 harnesses picked, no review needed (no money, saves or threading),
records `{next}`=0.9.124, compile OK in 71 s, the suite finished in 2,187 s
with exit 3: known red only, five harnesses: DeathRecordCheck, InvestCheck,
MapCheck, MoneyDatesCheck, ResearchViewCheck. Their `checks/reds.txt` lines
are the ones already on record: MapCheck's three (DM3's monthly map totals,
the one-network and the surface-accounting lines), InvestCheck's older-save
contract, MoneyDatesCheck's exhausted-gold twin, DeathRecordCheck's layoff
orphan and ResearchViewCheck's sector filter; none is FA1's. Fourteen timing
assertions soft, three path assertions observed, five starts held for CPU
(none changed a result). Stale: 0 firm findings. The dry run left the tree
as it was.

Trace equality: the eight CSVs this run captured
(`target/FA1-final-trace-32869da2-{bank,bonds,borrow,build,house,labour,month,pop}.csv`)
are byte-identical to main's reference, the orchestrator's R0h run
(`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R0h-NrRv6F33/traces/trace-*.csv`);
confirmed here with `cmp`, eight of eight the same. No ensemble: the trace
did not move.

After this kept run only this file, `FA1-ship.md` and `known-reds.txt` may
advance; this commit changes only the first two, and a final commit alone
removes the provisional marker.
