# GB6: the PC as the gate, with the player that keeps its ground

Queued by LP-G1 (2026-11-23, the orchestrator's review: "queue a row in the ship file for the PC-as-gate
fallback"). The figures are in [LP-G1-notes.md §4 and §6](LP-G1-notes.md#6-what-it-costs-the-players-city-grows).

Since LP-G1 the test player looks at its ground across its skips, and its city is no longer held for land: the
default seed 0 ends at 5.7 million people where it ended at 1.3 million, seed 1 at 5.5 million. On the PC, alone:

- `LongPlaytest`, seed 0: 1,377 s (657 s before).
- `CopperRefiningCheck` section 11, the playtest's seed 1 in a child JVM: 1,647 s, inside its `ROD_CHILD_MINUTES`
  (28), but with sections 1-10 before it the harness passes `AllChecks.HARNESS_SECONDS` (1,740 s, "four times
  KeptCheck's usual 435 seconds", GF1) and is ended: a red on any gate run on the PC.

The box ran main's two in 160 s and 225 s at PW5's gate, so about twice that fits its limits (LP-G1's dry run on the
box and the 4,000-month playtest there, notes §9 and §7, say). A gate falls back to the PC when the box is down (GB1), so
on that day CopperRefiningCheck reds and LongPlaytest is near the limit under a full suite's load.

What is to be decided, for the gate and not for the model: a limit per harness where one is known to run long on the
PC (a hang guard for the rest stays four times KeptCheck's), section 11 played on the box only, or the PC's gate
naming the red as the box's absence. The playtest's player is not to be changed for it, and no check is to be
weakened: section 11's two lines pass on their merits (exit 0, Rod Mill 0).
