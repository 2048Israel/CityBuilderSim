# FL1 - a works making a flow good stops running at a loss

Queued 2026-10-30 by the orchestrator, from WN3's pending row WN3-F. Jerus,
2026-10-30, asked whether WN3 ships with this fault known or fixes it first:
"Ship, fix next (Recommended)". This is the fix, run beside HD2.

## The fault

WN3's notes have it (§5.10; on main once WN3 ships).
- **The cause.** WN3's order rule (THE ORDER) has a works order its inputs
  for what its month will make, but only for stockable goods. A works making
  a flow good keeps importing its input dearer than its output sells.
- **SlopeCheck's city** (red). From m834, Copper Refining imports 163,302 t
  of cathode at 347.18 and exports 160,728 t of rod at 314. The city's GDP is
  negative from m834 to m1837.
- **LandmassCheck's Rotterdam run** (red). The January re-solve moves
  cathode's import price from 79.8 to 120.1 and rod's export price from 97.5
  to 70.7. The works imports 10.1M of cathode and exports 5.3M of rod a month
  until it cuts back, from m962 to m968. GDP is -997k at m962.

## To do

- Prove the cause on the two fixtures first: why the rule covers stockable
  goods only, and what a flow-good works reads instead.
- Extend the rule so a works never buys an input to make an output it can
  only sell at a loss on the band's prices.
- SlopeCheck and LandmassCheck pass with their premises unmoved. Count how
  often the rule cuts an order in a real run.
- It is built on WN3: from `origin/batch/WN3` as its lane successor until WN3
  is on main, then from main.
- It is a model batch: an Opus session with Sonnet slices, the full suite,
  and the ensemble, since the trace moves wherever a flow good is made.
