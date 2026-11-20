# R2e-PE4 notes: the Cement Plant calibrated rotary-first

Builder: Claude (Opus), headless, 2026-11-20; branch `batch/R2e-PE4`, cut from
`origin/main` e2f399d7c (0.9.162), main merged again at e6b37b348 (FB13, SQ1b).
The requirement is QUEUE.md's R2e-PE4 row, R2e's "★ The rotary-first Cement
Plant calibration" paragraph (docs/queue/R2e-notes.md, its second review) and
R1d's ★13.

## The finding

R2d calibrated PE4 (Edison's long rotary kiln, June 1902) on the Cement Plant so
that *all missing together* gave the study's 30% coal per tonne of clinker
(research-1900 8.5; cement tonnes proxy clinker, R2d's mapping): R1c's shaft kilns
(4,690 t, x1.4 today's 3,350 t) plus a PE4 undo of 2,810 t made 7,500 t on 25,000 t
of cement. R2e then scoped E2's preheater after PE4, with its exact 7.0/3.3 wet
form when PE4 is present and E2 is not (7,106.06 t). But the rotary kiln is known
from 1900 and every 1900 plant fits it, so the state a 1900 plant actually runs is
rotary-on, PE4-off: 3,350 + 2,810 = 6,160 t. Learning PE4 then took it to
7,106.06 t, 946.06 t more: the long kiln was a loss, refused as a refit and as a
new plant's piece, and it earned no royalty, until E2 a half-century later
(R2e's controls asserted exactly that, PostwarRecipeCheck 9 and 10).

## What changed

- `buildings.json`, the Cement Plant's PE4 recipe change: `THERMAL_COAL` 2,810 to
  **4,150** (= 30% x 25,000 - 3,350). The PE4 source line, shared by the three
  templates, now says the 30% is the rotary kiln's state before PE4 and each
  undo is that less the modern coal; R1c's shaft kilns keep their 1,340 t over
  the rotary in every state.
- The Cement Plant's coal, a month, by state (rotary kiln fitted unless said):

  | state | before | now |
  |---|---|---|
  | PE4 and E2 unknown | 6,160 | **7,500** (30.0%) |
  | PE4, not E2 | 7,106.06 | 7,106.06 (the wet form, unchanged) |
  | E2 found first, PE4 not | 6,160 | **7,500** (E2 is scoped after PE4: no change) |
  | all known (today) | 3,350 | 3,350 |
  | shaft kilns, PE4 unknown | 7,500 | **8,840** |
  | shaft kilns, PE4, not E2 | 8,446.06 | 8,446.06 |
  | shaft kilns, all known | 4,690 | 4,690 |

  So in both learning orders every step saves on the one before: PE4 first,
  7,500 to 7,106.06 (394 t) to 3,350 (3,756 t); E2 first, 7,500 to 7,500 (the
  preheater waits for its long kiln) to 3,350. No refit is refused for a negative
  gain, and a fitted long kiln pays PE4's royalty (25%) on positive production.
- The Large Cement Plant (37,500 t of 125,000 before PE4) and the Lime Kiln
  (5,400 of 18,000) were already 30% before PE4 and every step there already
  saved: unchanged.
- `PostwarRecipeCheck` (R2e's harness), 228 assertions:
  - 7: the rotary state without PE4 is the study's 30%, and the all-missing shaft
    state keeps R1c's own penalty over it (replacing "all-missing keeps 30%");
    the wet form burns less than the 30% it follows.
  - 8: on both cement templates, in both learning orders, no learned kiln step
    is a loss and the long kiln's is a real coal saving (on the real fixture
    prices, coal priced).
  - 9: the 30% falls to the wet form; PE4's fitting gain is that real positive
    difference; with the kiln's cost nearly nothing (machinery at 1e-6) the refit
    and the new plant's long kiln both PAY, the refit's net gain the real gain
    less PE4's 25%; the new plant's appraisal carries PE4's royalty, 25% of the
    real gain at the works' rate; positive production pays the royalty (E2
    unknown, lime's kiln unfitted).
  - 10: a real new Cement Plant ordered in June 1902 completes with the rotary
    kiln *and* the long kiln (at the fixture's own machinery price), E2 off, and
    runs the wet form below the 30%.
  - 13 (new): a plant standing in May 1902 on its rotary kiln at the 30%, the
    long kiln not yet known; in PE4's month the long kiln is a paying refit; the
    works' month (EquipmentFit.choose, a plan that builds nothing) is to fit it;
    `Game.fit` takes the order; the crews fit it through the ordinary months (4
    depots, about 49 months for 4,616 points); the plant falls to the wet form;
    its production pays a royalty; the stack's bill is exactly 25% of the long
    kiln's real coal saving (measured by unfitting it) at the works' rate.

## The calls

1. ★ **Define PE4 against the rotary state** (the row's recommendation, R2e's
   and R1d's ★13), taken as recommended under Jerus's standing "do recommended"
   for a ★ call; the alternative, leaving the partial-state calibration, kept a
   1900 plant's long kiln a loss for fifty years.
2. ★ **"The study's 30%" is 30% of the clinker's weight** (research-1900 8.5 as
   R2d cites it: short kilns burn coal at 30% of clinker weight), so 7,500 t on
   25,000 t, cement proxying clinker as R2d mapped it. The brief's "30% more coal
   per tonne of clinker (T32/T34)" names E2's sources; the queue row and R2e's
   paragraph say "burns 30% coal/clinker", which is this.
3. ★ **The shaft kilns move with it** (7,500 to 8,840 t before PE4). The undo is
   template-wide (`RecipeChanges.compose` applies one share of each undo across
   the rotary and shaft parts of a fleet), and R1c's all-known shaft figure
   (4,690 t) stays, so the shaft state is the rotary state plus R1c's 1,340 t,
   in every state. The rotary kiln's own gain over the shaft kilns (1,340 t of
   coal and 20 unskilled posts, against 316 kW more draw) is unchanged, so no 1900 plant's rotary choice
   moves. The other way, PE4 scoped to the rotary share alone, would change
   `compose` and leave shaft kilns at 4,690 t before PE4, 2,810 t under the
   rotary's 7,500, so a 1900 plant would leave its rotary kiln off: outside the
   brief, and against R1c's 1900 rotary choice. Recommended and taken: this one.
4. ★ **Section 13's order is the works' choice against a plan that builds
   nothing, then `Game.fit`** (FittingCheck 5 and 6's path), not the free
   investment round: at the fixture's cement price (10,000, there for positive
   production) the works' plan builds a new plant, which outranks a fitting on
   earnings over cost, so a free round tests the plan and not the kiln. The
   fixture gives the works four times the quote in cash (its first run: "could
   not borrow the $1.6M it is short") and four Construction Depots with
   workers, as section 10 does.

## The checks

- **The old calibration, the same harness**: the branch's classes with
  e2f399d7c's `buildings.json` overlaid, run whole: **15 FAILED of 228**, every
  updated control: 7 (the 30% read 6,160; the wet form above it), 8 (Cement
  Plant/PE4, PE4 first: actual -1.84), 9 (all five: 6,160 to 7,106; gain
  -94,606; NOT_YET; appraisal 0; fee 0), 10 (both: long kiln left off, 6,160),
  13 (five: 6,160 the month before; NOT_YET; "nothing it wants"; fee 0; bill
  0). Log: `target/R2e-PE4-evidence/old-calibration.log` (not kept in git).
- **The branch**: PostwarRecipeCheck 228/228.
- **The default playtest's trace is main's, byte for byte**: one JVM each,
  `-Dplaytest.trace`, 2,405 months, main's classes at e2f399d7c and the
  branch's at 8ad13fa34 (both compiled in this worktree, each run from its own
  copy of the classes). All eight CSVs identical (sha256 in
  `target/R2e-PE4-evidence/trace-hashes.txt`); the report differs only by its
  run time and one object hash in a works-decision line (`LandParcel@...`). The
  default city knows all three steps and is founded fitted, so nothing there
  reads the partial states: no ensemble is owed.
- **Named harnesses**, one `./batch-check.sh` name at a time, on the tree with
  FB13 and SQ1b merged (e6b37b348): PostwarRecipeCheck 228, HistoricalRecipeCheck
  120, HistoricalLicenceCheck 80, RecipeCheck, EquipmentCheck, ResearchCheck,
  DiscoveryCheck, BuildingDataCheck, LicenceFeesCheck 43, FittingCheck,
  CementConcreteCheck, NewUnitCheck 63: all green. Forms1900Check exit 3: only
  main's two known reds (section 12's lamp oil, FS8, `docs/notes/known-reds.txt`).
  After FB14's merge (808b69b43), PostwarRecipeCheck 228/228 again.
- **Village1900Check, for R1g**: R1g's branch (03a239410) in a temporary
  worktree, run alone as it stands and then with this batch's `buildings.json`
  diff applied: both exit 0, all 35 assertions, and the whole 65-line output
  identical byte for byte. Nothing in its five years moved (its 1900 village's
  Cement & Concrete is held for money at a Lime Kiln, R1g's notes; whatever
  cement it makes, nothing the harness reads moved). For R1g's 1900 playtest when it is the
  default: a Cement Plant before June 1902 now burns 7,500 t a month (was
  6,160) and from PE4 a long kiln that saves 394 t a month is a refit it may
  order (it was a loss before). Logs: `target/R2e-PE4-evidence/village-r1g*.log`.
- `./batch-check.sh --fast` green on each merged tree (0 firm Stale).
- `sh tools/pick.sh origin/main HEAD`: ALL (buildings.json's reader,
  BuildingManager, has 109 readers); review line: no money, saves or threading.

## The dry run

`sh tools/gate.sh R2e-PE4 --dry-run` on the gate box, on the orchestrator's go:
run `/home/jerus/cbs/runs/cbs-gate-R2e-PE4-mS6El1Me` (copied home to
`C:\Users\Jerus\AppData\Local\Temp\cbs-gate-R2e-PE4-mS6El1Me`), of main
01d93949b and the branch 2a65ad4f6. **Exit 0**: the whole suite in 738 s, exit
3, known reds only (20 harnesses, every line in `docs/notes/gate.txt` or
`docs/notes/known-reds.txt`), `unnamed.txt` empty; "dry run: the tree is as it
was". Main moved to 4d3e18673 (SB1, `BuildingManager.retire()`) at 17:27 UTC,
during the run; not merged, since after a dry run only the notes and the ship
file change. Whether the gate reuses this run is the orchestrator's call.

## Left

Nothing for Jerus: the row's one call was recommended and is taken (★1). No
screen changed, so no eye check. The Pending row R2e-PE4 is this batch; no new
Pending, queued or known-red row.
