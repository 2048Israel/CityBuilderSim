# HN1 - households housed before the matching homes finish

Queued by R0h's same-state main probe (2026-11-15), with the orchestrator's
instruction to queue a main-proven F1 finding separately from the influenza fix.

## The finding

The accepted R0h model's 80-run ensemble has one finding type: "households with
no home", AUTO2 seed 12 and ZERO_SLACK seed 12 at months 744-747. Those policies
share the same path at this date. From their valid month-743 checkpoint, main
6114b00f's complete model and R0h both leave 3.592771208418526,
7.848705090851581, 12.491581939075331 and 17.197984450481272 households unplaced,
then zero at 748. Homes rise from 3,044 to 3,107 at 748. The checkpoint has no
acute shares or influenza sickness. Both models match to the bit through all
five months in fingerprints, money pools, family state, rings, cohorts, Health
and causes. The captured run's complete CSV prefix matches the kept ensemble.

This is a latent main housing/placement fault reached by a valid played path.
Diagnose why the homes that match those households arrive after the shortage:
placement, forecast, building choice, queue and actual completion are candidates,
not a decided root cause. Preserve LongPlaytest's existing >1 unplaced-household
watch and the original seed, founding and policy. Do not move the fixture or
relax the floor to let the change through.

## The regression

Cause the original seed-12 city and the month-743 checkpoint. Play 744-748 with
its normal player and auto-builder, retaining the hard watch and recording how
many households remain unplaced and which home sizes actually finish. The
fixture must fail on 6114b00f. Assert the fix against the model's own housing
inputs and constants, not a changed premise or a door count that ignores sizes.

Evidence and the exact reproduction: [R0h notes](R0h-notes.md), "Same-state F1
probe". Kept baseline: [raw R0h ensemble](https://github.com/2048Israel/CityBuilderSim/blob/2929e0d36024d4a522c0d86c9c441201b1cc3d14/docs/ensemble/R0h.md). Scratch probe sources,
checkpoint, logs and state comparisons: target/r0h-homeless/ in R0h's worktree.
No housing model or watch was changed in R0h.
