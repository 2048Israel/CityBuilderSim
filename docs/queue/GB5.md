# GB5 - the gate box runs the harnesses that need a display

Queued 2026-11-20 by the orchestrator, from FS4's finding (docs/queue/FS4-notes.md): the gate box is
headless, so KeptCheck skips there (no JavaFX toolkit), and so would FS4's new KeptTimingCheck when the
nightly runs it hard. Since GB1 (untagged, 2026-11-20 08:39) every gate has run on the box, so no
gate since has judged KeptCheck, and the KEPT_*_MS budgets are judged only on the PC, where there is a
display. This is a gap in the gate, not in FS4.

## What it does

1. **Find every harness that skips without a display:** the ones that start the JavaFX toolkit (grep
   the harnesses for `Platform.startup`, the toolkit's skip line, and the "skip" they print), and list
   them with what each proves.
2. **Give the box a display** for the gate, the dry run and the nightly. The box (2026-11-20) has no
   Xvfb and no passwordless sudo, so there are two ways: JavaFX's Monocle headless platform (a
   dependency, nothing installed on the box), if it draws what KeptCheck measures; or Xvfb (`xvfb-run -a`
   around the box's suite and timing runs in `tools/gate-box.sh`), which needs Jerus to run one line on
   the box (`sudo apt install xvfb`). Try Monocle first; if it does not draw what KeptCheck measures,
   mail the orchestrator for the apt line. Say which, and why (★).
3. **Prove it:** a box run where KeptCheck and KeptTimingCheck run, not skip, and pass; the nightly's
   timing line counts their budgets. If any budget misses on the box's hardware, say by how much in the
   notes; the budgets do not move in this batch.

## Done

The box's gate and nightly run every display harness (none skips for want of a display), the suite's
summary says so, and a harness or the gate's own check fails if a display harness skips on the box.
Tools only: untagged, the default trace cannot move.

- **Files it expects to change:** `tools/gate-box.sh`, perhaps `tools/nightly.sh`, the box's setup
  notes (docs/orchestrator.md or the gate box's README).
- **Not beside:** another batch changing `tools/gate-box.sh`.
