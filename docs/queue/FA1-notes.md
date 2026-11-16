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

The painter already reads heading and corner bits, not an arm number or a
shared origin; its existing arbitrary-line avenue bands need no code change.
All implementation changes are confined to DistrictPlan and PlanCheck.
No sidecar fields or byte layout changed; no save or model code changed.

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
