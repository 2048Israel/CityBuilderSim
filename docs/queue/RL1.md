# RL1 - the next batch in a lane starts at "ready", not at "shipped"

Queued 2026-10-27 from a side session with Jerus, on his word ("yes please,
do it as soon as humanly possible"; `docs/queue/GP1.md` has his words and
the figures). After GP1, which builds the ship-file wait this one reuses.

## Why

A lane's next batch waits (`sh tools/wait.sh --row PRED`) until its
predecessor's Queued row leaves origin's `QUEUE.md`, that is until the
predecessor has passed the gate. Every minute from ship file to merge (a
median of about 60 min; MS2 and CC1-S each waited 2-3 h on Jerus's word) is
paid again by the batch behind it.

## What RL1 does

1. **The successor starts when its predecessor's ship file is on origin**,
   from the predecessor's branch tip rather than from main. It reads, plans
   and builds on that base.
2. **When the predecessor merges**, the successor merges `origin/main` in.
   **When the predecessor is sent back**, the successor merges the fix from
   the predecessor's branch. The successor never gates before its
   predecessor is on main: `day.sh` calls it waiting, not ready.
3. **Three places assume a branch cut from main** (the independent review,
   2026-10-27). Each is made right:
   - `ship.sh` stops a branch that adds another id's ship file (header,
     step 5). The successor carries its predecessor's until the predecessor
     lands and the successor merges main.
   - `tools/pick.sh` diffs against `origin/main` by default
     (`tools/pick.sh:97-100`), so before the predecessor lands it counts the
     predecessor's whole diff as the successor's. Base it on the
     predecessor's tip.
   - The recorded reds are read at the merge-base with `origin/main`
     (`recorded_reds`, `batch-check.sh:265-272`). Reds Jerus approved at the
     predecessor's gate are not yet in main's `gate.txt`, so they show as
     `[NEW]` and the successor may chase them.
4. **One in, one out** (Jerus, 2026-10-27): RL1 removes about as many lines
   of script and rules as it adds. Its notes give the count.

## Scope and checks

Tooling and `docs/batch.md`, no game code; ships `--no-tag`. Checks:
`sh tools/pick-selftest.sh`, `sh tools/ship-selftest.sh`, and a dry run
with two throwaway branches, a predecessor and its successor, through
ready, sent back, and merged.

## Calls

- A successor whose predecessor is sent back with a fix that changes what
  the successor built on. Recommendation: merge the fix and carry on; stop
  and ask only if the fix removes something the successor uses.
