# KP2c — The kept screens' sweep, the rest

Source scope: ui/ and KeptCheck, on batch/KP2c cut from main b9c93a989.
The queue row links directly to KP2b-notes §6; §§1–5 and WC1 were read.
No known-red entry names KP2c. Section 96 is unchanged for R2d.

## 1. The row, built

- UserInterface.scrolled keeps one PAGE_FOOT_ROOM, derived from PAGE_FOOT,
  and sets padding only when it differs. Services and Infrastructure use
  the shared FRAME_ROOM with the same guard. Services' door cards, including
  Infrastructure's traffic cards, choose their complete style before
  writing it once. NOT_KP2B and its ignored counter are gone.
- Kept.Grid stages column and row constraints like children. Equal complete
  specifications keep the grid's existing constraints; an omitted list is
  cleared at commit. All grid callers use Kept.columns/rows.
- Kept.give records assignment intent, including an unchanged assignment.
  At draw end Kept resets only omitted optional state and handlers. The rule
  is in Kept's rules list, and the kept callers use it. This removes the
  false enabled interval in a repeatedly greyed control.
- Preferred sizes and HBox margins compare before setting. Section 92 also
  counts preferred height, disable changes and constraint lists. Temporary
  observers on kept regions/boxes hear size calls and JavaFX margin layout
  requests, including equal raw setters that property listeners miss.
- Comparison restores clear saved roots, boxes and pools. Picture repaint
  counters have weak keys. Each no-stage scene drains its dirty list with
  a one-pixel snapshot after restoring a comparison, including the auxiliary
  header, rail and panel scenes.
- A slider keeps its physical press through disable/enable. A weak scene
  release filter hears release elsewhere while disabled; permanent scene
  departure releases it. A temporary departure during drawing/commit keeps
  the press if the node returns to the same scene before commit.
- Pieces.vitalsBar finishes a cell from another maker on each draw. Its
  skip requires the look limitCell supplied, so another maker's freshly
  supplied style cannot bypass the last-cell finish.

## 2. ★ Calls and threading

★ Guard the foreign-cell case rather than dropping the promise. A maker
that reapplies its own style must still get the bar's last-cell look; the
fixture checks the bar's own first-draw result on the same node's redraw.

★ Track assignments explicitly, including handlers: JavaFX emits no change
event for a same-value assignment, so a listener-only reset cannot tell an
unchanged assignment from an omission. Deferring all omitted state also
prevents disabled/style toggles in the middle of the draw.

★ Compare all geometry, growth, alignment and fill values of constraint
specifications, retaining the grid's actual objects when equal. The pages
can keep their one drawing path without a cache per specification.

★ Preserve a physical press and catch release at its scene. Clearing it on
disable loses the hand if the control is enabled again during that drag.
The real Policy fixture also exposed reparenting when its staged-choice
tray appears: a draw checks departure at commit, after its lists settle.
Scene handlers are weak and removed on scene change. Section 91 now sends
the physical release elsewhere while greyed; section 102 keeps the same
drag down through greyed and ungreyed draws, then checks release commits
the held value snapped with the slider's own min/block increment.

All drawing, state settlement, observers, snapshots and input stay on the
FX thread. No worker or model/money call is added or changed. Bank/Fund
changes are assignment tracking, guarded sizes/margins and staged grids.

## 3. Fixtures, red first

The new fixtures are section 102. Early build logs print 100 for them;
that provisional number was changed, with no fixture logic change, because
Research already owns 100–101. Research and R2d's section 96 remain intact.

`target/kp2c-before.log`: original ui implementations from b9c93a989 compiled
ahead of current classes, with compatibility delegates for the new APIs
and passive setter observation, no mends. The same CITY_MONTHS city uses
KeptCheck's going-rate minimum wage. Exit 174: ten draw-state, foreign-cell,
size, drag and retention assertions fail, and 164 section-92 pages repeat
writes. An unchanged old icon requests size twice; equal raw size and
margin setters are each heard once. Omissions still reset as required.

`target/kp2c-after.log`: every section-92 page zero. The remaining Policy
drag failure found its transient scene departure; the final fix preserves
the hold and release at the snapped value. `target/kp2c-unit.log`: all new
fixtures pass, exit 0, with zero saved comparison roots (89 before), both
retired-node references cleared and no unchanged icon layout request.

REV-KP2c-1 found unqualified setters in ActionButton.retell and
RowsPicture.set. Both now give their cursor/handlers explicitly. The new
real-page fixtures prove the initial four row handlers and hand cursor
exist, redraw twice kept, and check the same picture retains them; History
does the same for its action button. Exact Pieces, RankingsScreen and
UserInterface at 65e3e19e1 loaded ahead of current classes fail these two
assertions: `target/kp2c-review-before.log`, exit 2. The mended focused run
passes. The header's same private sparklines retain transparency in both.

The full raw-setter audit covered every kept page/helper. Three remaining
raw visibility setters are on private labels in Ladder's marks and Pieces'
picture; Kept hands back their owners. Sparkline has both ownership modes:
HeadlineTile owns one, while Pieces.sparkline pools one directly. Its
constructor and each retell now give transparency explicitly, before the
unchanged-series early return. Raw tooltip installs belong to picture-owned
parts or rebuilt menu cards; kept-node tooltips use Kept.tip. Styles are
retold whole and are not resetOmitted properties; section 92 counts every
screen with no exemption.

## 4. Checks, heap and default trace

- Unchanged-main whole KeptCheck at b9c93a989: ALL OK, exit 0,
  `target/kp2c-baseline.log`. A temporary PrintStream wrapper samples live
  heap after GC at section boundaries under AllChecks' 2048 MiB heap.
  Before: 185 MiB at section 30, 186 at 40, 615 at 50, about 890 at 60,
  1393 before 92 and 1552 before 93. KP2b's 1.95/1.97 GiB observations
  were on its older model; these are this main's measured before figures.
- Fresh final `./batch-check.sh --pick`, default pool and the required
  Earth/world files: exit 3; 40 harnesses run in 1497 s, 37 passed and
  MapCheck, KeptCheck and ResearchViewCheck had only registered reds.
  The picker names 33 filters; AllChecks also runs their substring matches.
  `target/kp2c-pick-final.log`, `target/checks/suite.txt` and harness logs.
  Stale: zero firm, 186 soft; 11 timing assertions soft, three path
  observations informational. No premise or budget was weakened.
- Whole KeptCheck: all KP2c laws pass; after release at its end,
  **1349.6 MiB of 2048**, leaving 698.4 MiB (34.1%) free. Its sole red
  is the registered KS1-UI header signature: the hidden FX label retains
  144x14 bounds while fresh is 0x0 (18 rungs). The registry already names
  hidden-header bounds; this run's unchanged-main baseline above passed.
  No signature rule is relaxed and no new coverage is added.
- Default LongPlaytest: All checks passed. Its full 486-line report equals
  FP1's retained main report at
  `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-FP1-qkbYBzgy/checks/LongPlaytest.log`.
  Only JDK-option notices, wall-clock prefixes, the elapsed banner and CL3
  runtime measurements are normalized. SHA256:
  `3deda6d14c92e4f03768e00dc2e520332698492262bc14b23960891cc6d7974c`.
  The default trace is unchanged. Main was not rerun; no ensemble ran.

Two earlier interrupted picks are discarded. The first Bash stop left its
native Java suite detached; exact verified native PIDs were then ended,
and a brief overlap had corrupted their output files. The final pick used
a fresh checks directory and unique log after the process inventory showed
those suites gone. Discarded logs remain in target/kp2c-interrupted-checks.
Only this batch's verified process trees were ended; other sessions and
MemWatch were left alone. No player save slot was written; save fixtures used scratch folders.

## 5. Review and launcher merge verdict

Both reviews are accepted, including the orchestrator's direct review of
the b10c2a681 Sparkline follow-up. XR7 and R2d are merged. The normal
launcher merge dry run passed at 18:08:47 local on 2026-11-15, gate exit 0
and check-suite exit 3. The retained receipt is below. Only these notes and
the ship file advance after the dry run; the provisional marker is removed
in the last readiness commit, by itself.

Main advanced to XR7, 8083aaf33, after the local pick. Merged it before the
launcher run. Only docs/dials.md, docs/harnesses.md and docs/map/README.md
conflicted; all three were regenerated from the merged source. The merged
`--fast` passes: compile, maps, zero firm Stale findings (186 soft).
XR7 intentionally changes world-peg traces and brings its own ensemble;
the local pre-merge report comparison above remains the UI-only evidence.
The launcher will verify the final combined model after R2d also ships.

REV-KP2c-2 reviewed the whole 8083aaf33..99356968c change: one finding,
otherwise SHIP. The initial transparency control covered header-owned
sparklines but missed Pieces.sparkline's directly pooled instance. Its
retell now gives the invariant too. Section 102 keeps the owned control
and adds the real Pieces factory with one pool, a first draw and two
unchanged redraws, at SPARK_WIDTH/SPARK_HEIGHT. Against exact UserInterface
at 99356968c, its first draw and owned control pass and the directly pooled
redraw alone fails (`target/kp2c-spark-before.log`, exit 1). On the mend all
focused laws pass (`target/kp2c-unit.log`, exit 0). The delta's --fast also
passes, zero firm/186 soft. The 40-harness pick above precedes this small
retell/fixture delta; the final whole launcher run verifies it in full.

The orchestrator gave the dry-run go at 17:26 on 2026-11-15 after R2d
shipped (main 32ec5a5d3). Merged that main before the launcher. The four
conflicts were generated indexes (dials, harnesses, KeptCheck's map and the
map index), all regenerated. All ui/ files and the complete own methods
greyedWhileHeld, doorsStyledOnce, nothingSet, kp2bSets and kp2cMends are
byte-identical across the merge. R2d's section 96 and descriptions came in
as authored; the source merge was automatic. Reported that audit to the
orchestrator before launch. Merged --fast passes: compile 49 s, maps,
zero firm/186 soft. The pushed provisional branch is now frozen for the
normal detached tools/gate.sh KP2c --dry-run, in the default pool.

## 6. Retained final launcher run

Normal route: sh tools/gate.sh KP2c --dry-run, detached launcher PID 28072,
after the orchestrator's go, default MG1 pool, no parallel override. No
push occurred while it ran. The guardian and retained receipt completed.

- Run: `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-KP2c-u9Mhwx0L`
  (Git Bash `/tmp/cbs-gate-KP2c-u9Mhwx0L`). `ship.log`, `checks/`,
  `checks.sha256`, `checks.complete`, `selection`, and both commit files
  are retained; the normal launcher cleaned its disposable clone.
- Branch of the run: `7a6f0bf08a331737229ce2d5bb6b9813d2a5c2f6`.
- Main of the run: `32ec5a5d395dbc18c794c7f4a93dd52392305d8e` (R2d).
- Selection: full gate plus the directly selected nightly
  CopperRefiningCheck and KeptCheck; publisher's candidate version 0.9.119.
- Gate exit 0; check stage 2203 s, exit 3. Suite: **194 harnesses in
  2126 s, 188 passed, six registered-red harnesses only**: DeathRecordCheck,
  InvestCheck, KeptCheck, MapCheck, MoneyDatesCheck, ResearchViewCheck.
  `unnamed.txt` is empty. Eleven timing assertions were soft and four path
  observations informational. No new known-red row or transfer is needed.
- Every section-102 law passes, including the directly pooled sparkline.
  After the full merged-model harness, live heap is **1347.7 MiB of
  2048**, leaving 700.3 MiB (34.2%) free. KeptCheck's sole failure is the
  same registered KS1-UI hidden-header signature line (18 rungs).
- Default LongPlaytest passes. Its entire normalized 486-line report is
  identical to R2d's final retained report at
  `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R2d-final2-20261115-163523/checks/LongPlaytest.log`.
  The exclusions are the same JDK notices, wall-clock prefixes and runtime
  measurements described above. The SHA256 remains
  `3deda6d14c92e4f03768e00dc2e520332698492262bc14b23960891cc6d7974c`.
  The production model diff against that main is empty: all source changes
  are ui/ and KeptCheck. No default-trace change and no ensemble.
- Independent `sha256sum -c checks.sha256` from the retained run exits 0
  (`/tmp/KP2c-receipt-sha.log`). Main was fetched after completion and is
  still the exact recorded main. The ship file names this run for reuse.

Every check used the required real files at
`C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin`
and `C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
There is no unfinished KP2c item or batch-owned red. The publisher owns the
version, queue removal, changelog and gate record; none is hand-edited here.
