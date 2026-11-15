# KP2c — The kept screens' sweep, the rest

Building on `batch/KP2c`, cut from main b9c93a98. The queue row links to
KP2b-notes §6; §§1–5 and WC1 read. No known-red entry names KP2c on this
starting tree. Source scope is ui/ and KeptCheck; section 96 is R2d's.

## What changed and why

- The window's scrolling-page padding, Services' and Infrastructure's frame
  padding compare their insets before setting them. Services' door cards,
  including Infrastructure's traffic cards, choose their complete style
  before writing it. Section 92 has no screen exemption left.
- A kept grid stages column and row constraints, as it stages children.
  Equivalent specifications keep the grid's own constraints and cause no
  list change. Constraints omitted by the drawing code go at commit.
- Optional node state and event handlers are explicitly given through
  `Kept.give`. The draw clears only omitted state, at its end. This includes
  unchanged assignments; JavaFX property listeners cannot detect those.
  The existing kept-page callers use the helper, with no model changes.
- Preferred sizes and HBox margins compare before setting. Section 92 now
  observes preferred height, disable changes and constraint lists. Its
  temporary layout observer hears JavaFX's actual equal-size/equal-margin
  layout requests in the kept parent boxes; control probes cause both.
- Comparison restores clear their saved roots/pools. Repaint counters have
  weak keys. Auxiliary no-stage scenes drain their dirty lists through a
  one-pixel snapshot after restore, as the page scene already did.
- A press is held through disable/enable until release. A weak scene release
  filter also hears release elsewhere while the slider is disabled; leaving
  the scene releases it. Section 91 now actually sends that missed release,
  preserving its stale-hold finding. Section 100 separately keeps the same
  drag down through a greyed draw and an ungreyed draw, then releases it and
  checks the committed thumb value.

## Calls

★ Guard the non-limitCell case in `Pieces.vitalsBar`, rather than dropping
the promise. A maker that supplies its own style on every draw must still
receive the last-cell look from the bar. The fixture reapplies that maker's
style on the same node and checks the bar's own first-draw result.

★ Track assignment intent explicitly, including handlers. A listener-only
reset would treat a same-value assignment as an omission and clear a greyed
control. Staging the reset also avoids a false enabled interval mid-draw.

★ Preserve a physical press through greying and catch release at its scene.
Clearing the press merely because the control is disabled loses ownership
when it is enabled again during the same drag. Scene handlers are weak and
removed when the node changes scene, so the remedy adds no scene-to-node
retention path. All drawing, assignment, reset and input work stays on the
FX thread; no worker is added.

## Checks and heap

In progress. The unchanged-main baseline is a normal whole KeptCheck with
GC/live-heap samples at section boundaries, under AllChecks' 2048 MiB heap,
soft timing and the required Earth/world files. Kept at
`target/kp2c-baseline.log`; its temporary launcher is `target/KP2cHeap.java`.

Before: section 30 185 MiB, section 40 186 MiB, section 50 615 MiB,
section 60 about 890 MiB, section 70 1044 MiB. Final sections and the same
after measurements will be recorded when complete. No budget or existing
retention floor is weakened. New fixtures are in section 100.

The default trace must remain unchanged: this batch changes screens only.
No ensemble is authorized or needed. Final pick and kept gate-run directory,
with branch and main commit, will be recorded after the orchestrator's go.

## Focused red/green evidence

The old implementations from b9c93a98 were compiled in
`target/kp2c-before` ahead of current classes, with compatibility delegates
for the new assignment/list APIs and passive setter observers, no mends.
The same section 100 and section 92 fixtures ran on the same city played
to CITY_MONTHS, with the going-rate minimum wage used by KeptCheck.
`target/kp2c-before.log`: 174 failed assertions (exit 174). Ten are section
100's old retention, toggling, foreign-cell, repeated-size and drag faults;
164 pages in section 92 had repeated writes. Equal raw size and margin
setters were each heard once, and the old icon redraw requested size twice.
The counter therefore measures the formerly invisible setter calls.

`target/kp2c-after.log`: all section 92 pages zero; the integration drag
still found a transient scene departure. `target/kp2c-unit.log`, on the
final code: all section 100 probes pass, exit 0. Saved comparison roots
fell from 89 to zero, both retired-node references cleared, and the same
unchanged icon redraw requested no layout. These are focused diagnostics;
the full harness, pick and launcher merge verdict remain required.

★ A draw that briefly reparents a held control keeps its press when the
control returns to the same scene before commit. The real Policy fixture
causes this by opening its staged-choice tray during the held, greyed draw.
A scene departure outside a draw, or one that remains at commit, releases
it. The release assertion uses the slider's own min/block increment to
snap the player's held thumb, as the Ladder does; no copied policy dial.

The baseline whole KeptCheck at b9c93a98 was ALL OK, exit 0, with live heap
1393 MiB before section 92 and 1552 MiB before section 93. These are lower
than KP2b's earlier 1.95/1.97 GiB observations on its older model, still
close to the 2048 MiB suite heap. A focused run's 103 MiB is not the full
harness's after measurement; that comes from the pick and gate below.

Source and fixtures at 65e3e19e1; fetched and merged origin/main (already
up to date at b9c93a989). The local selection is 33 harnesses, so the
required `./batch-check.sh --pick` is running once with the default pool.
The launcher merge dry run is the whole-suite run; no local ALL or
ensemble is run. Source changes remain strictly ui/ and KeptCheck.

The first pick was stopped at its own launcher PID 26396 after the full
harness found ActionButton.retell's unqualified `setCursor`: it had not
been migrated to `Kept.give`, so the new end reset took its hand cursor.
The existing History signatures caused and found the condition; no
signature or premise changed. The cursor assignment now records intent.
The stopped output is `target/kp2c-pick-stopped-KeptCheck.log`. Only that
verified process tree was ended; the next pick verifies the mended tree.

## Gate review follow-up and complete assignment audit
+
+REV-KP2c-1 found RowsPicture.set's four unqualified mouse setters, the same
+assignment-intent omission as ActionButton.retell. They now use Kept.give.
+Section 100 draws Rankings and History fresh then twice kept, proves the
+initial handlers/cursors exist, and checks the same row picture's four
+handlers and hand cursor and the same action button's hand remain.
+`target/kp2c-review-before.log` loads the exact Pieces, RankingsScreen and
+UserInterface classes at 65e3e19e1 ahead of current classes: these two
+assertions fail, exit 2. `target/kp2c-unit.log` on the mends: all pass,
+exit 0. The header's same sparklines retain mouse transparency in both.
+
+The complete raw-setter sweep covered every kept screen and helper. The
+remaining three raw visibility calls are on the private labels of Ladder's
+marks and Pieces' picture, and Sparkline's constructor transparency is on
+HeadlineTile's private child: none is handed back by Kept. Raw tooltip
+installs likewise belong to picture-owned shapes/cells or the rebuilt
+menu/saved-city cards. Kept-node tooltips go through Kept.tip. Styles are
+retold whole and never cleared by resetOmitted; section 92 counts every
+screen's writes, with no exemption. No other raw setter assigns a property
+resetOmitted clears on a handed-back node.
+
+The first Bash stop left its native Java launcher detached. Its verified
+AllChecks PID 10340 and children were then stopped explicitly. Two picks
+had briefly overlapped writers in target/checks, so both interrupted runs
+are discarded: NUL gaps in that report are output corruption, not an
+economic trace change or a usable verdict. The second native AllChecks
+40852, its parent batch-check 12964 and Bash launcher 41068 were all ended;
+the process inventory showed only XR7's different AllChecks afterward.
+The discarded checks directory is preserved under
+`target/kp2c-interrupted-checks`. The final pick starts with a fresh checks
+directory and a unique log, `target/kp2c-pick-final.log`.
+EOF
 git add src/main/java/ham/citybuildersim/ui/RankingsScreen.java src/main/java/ham/citybuildersim/ui/KeptBench.java src/main/java/ham/citybuildersim/KeptCheck.java docs/queue/KP2c-notes.md; git commit -m 'Keep Rankings row handlers and prove page interactions survive' -m 'The review found unqualified setters in RowsPicture.set. Draw the actual Rankings and History pages twice kept and check the same row handlers and hand cursors; the fixtures fail against 65e3e19e1. Audit every reset property and distinguish private picture children from handed-back nodes.' -m 'Written by Codex (GPT-6.1-Sol).'; git push origin batch/KP2c
