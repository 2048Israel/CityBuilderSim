# RL1 - the next batch in a lane starts at "ready", not at "shipped" (no tag, 2026-10-27)

The batch's record beside its brief, `RL1.md`. Tooling and `docs/`: no game
change, no version, no trace moved, no ensemble. Run whole on Sonnet 5.5 at
xhigh, Opus at the gate only (BP1 item 7). It was itself built on GP1's tip
(GP1 was at the gate behind MS3), the first case of what it adds.

## 1. What was there

A lane's next batch waited (`wait.sh --row PRED`) for its predecessor's Queued
row to leave `QUEUE.md`, that is for the predecessor's gate to finish: a median
of about 60 min from ship file to merge, paid again by the batch behind it. And
five places read "a branch" as "cut from main": `pick.sh` (the base), `ship.sh`
(the other-id ship-file stop), `day.sh` and `wait.sh --ship` (the ready test)
and `batch-check.sh` (the recorded reds at the merge-base).

## 2. What changed

1. **`tools/carries.sh [REF [OWN-BRANCH]]`** (new, 24 lines): the ids of origin's
   unmerged `batch/` branches whose tip is in REF's history (default HEAD), the
   nearest last, REF's own branch left out (a tip equal to it, or the branch
   named by it, by HEAD, or by the second argument: its origin ref may lag the
   commits not yet pushed). One `git for-each-ref --merged --no-merged` call.
   It is the one test of "my predecessor is not on main yet".
2. **`pick.sh`**: with no argument it diffs against the tip of the unmerged batch
   the tree is built on, else `origin/main` as before; its line names it
   ("since 1d5b9a3d, GP1's tip (not on main yet)": this batch's own `--pick`
   said so). `batch-check.sh --pick` follows, so a successor's pick is its own
   diff, not its predecessor's.
3. **`ship.sh`** stops a branch built on an unmerged batch, before the
   other-id ship-file stop (which a carried predecessor's file would trip with
   a less useful word): "batch/S is built on P, which is not on main yet: gate
   that first, then batch/S merges main". Once P is on main the successor ships,
   whether or not it has merged main (P's tip is then in main's history).
4. **`day.sh`** calls a branch with its ship file pushed that is built on an
   unmerged batch `waiting`, not `ready` (`--due` leaves it out); **`wait.sh
   --ship`** leaves it out of "ready to ship". Both ask `carries.sh`.
5. **`wait.sh --row`** also ends when a predecessor the branch is built on is
   ready again with commits HEAD lacks (sent back, mended, the ship file
   pushed last): `done - P ready again with commits HEAD lacks: git merge
   origin/batch/<id>`. That is the "sent back" case with no message from the
   orchestrator. `--row` now fetches the batch branches too (one `fetch_main`
   for both modes; the `ready_ships` loop became `ships ready|mended`).
6. **`batch-check.sh`**: a NEW red on a tree built on an unmerged batch says so
   under the red lines: a red the predecessor brought, approved at its gate,
   is in main's `gate.txt` only when it lands, so wait, merge main, run again
   before mending. The record is still read at the merge-base: reading the tip
   would bring back BP1's trouble (a red main later mended shows NEW), and
   the successor merges main at once when the predecessor lands, which makes
   the merge-base the tip.
7. **Out (one in, one out)**: `day.sh`'s old-style ready test (its own header
   said it goes when no pre-BP1 branch is left; none is, on origin today);
   **`ship.sh`'s old way** (a batch with no ship file shipping its own version
   and Queued row; every batch has written a ship file since BP1): a branch with
   no ship file now ships `--no-tag` or stops, said, and its selftest cases;
   `docs/batch.md`'s "The calls BP1 left" (28 lines, each call already a rule
   above it; the facts only it held, the pick fan-out dial and call 2, are in
   their rules), and wait.sh's `--ff-only` chained command.
8. **Docs**: `docs/batch.md` (The day: the lane's successor starts at the
   ship file; A batch session, 7: how it builds, waits and merges, and that
   its ship file goes last; the gate: a waiting branch goes after its
   predecessor), `docs/tree.md`.
9. **Selftests**: `pick-selftest.sh` 13 (3 new: against the predecessor's tip,
   once it is merged, and the branch's own lagging ref); `wait-selftest.sh` 24
   (was 14; the ready/sent back/mended/landed run of a predecessor and its
   successor, and `day.sh`'s waiting and ready); `ship-selftest.sh` 106 (was
   103: the old-way cases out, the stop on PS and PT in, PP shipped, PS then
   ships). Mutations: `wait.sh` without the carries test fails the waiting case,
   without the shared-history test fails the "not built on P2" case.

## 3. One in, one out

`git diff --numstat origin/batch/GP1` (GP1's tip, the base), over the scripts and
the rules, this batch's notes and ship file left out: **261 lines added, 182
removed.** With the three selftests left out (69 added, 22 removed) it is 192
added, 160 removed. Removed: `ship.sh` 54 (the old way, step 9's header
restated), `docs/batch.md` 56 (the calls section 28, the rest rewritten),
`day.sh` 20, `wait.sh` 19, `ship-selftest.sh` 17, `docs/tree.md` 7. Added:
`wait.sh` 41, `ship.sh` 42 (the stop, the headers), `docs/batch.md` 49,
`wait-selftest.sh` 33, `ship-selftest.sh` 26, `carries.sh` 24, `day.sh` 12,
`pick-selftest.sh` 10, `docs/tree.md` 13, `pick.sh` 7, `batch-check.sh` 4. It
does not come out even: the shared test and the proof of it (the selftests) are
new, and what else could go without Jerus's word did (`docs/batch.md`'s lessons
list is cited by letter from other files and stays).

## 4. Calls

- **The call** (the brief): a successor whose predecessor is sent back with a
  fix that changes what it built on. Recommendation: merge the fix and carry
  on; stop and ask only if the fix removes something it uses. **Taken: built as
  written** (`wait.sh --row` ends on the mended predecessor; `batch.md` 7 says
  merge, carry on, ask only on a removal).

Taken without a call, for the reader to overturn: the old way of `ship.sh` and
`day.sh` out (a branch with no ship file and no `--no-tag` now stops, said:
write the ship file, or ship it `--no-tag`); BP1's calls section folded into the
rules it served (call 2, the open one, is in The day, as it was); `--row` also
ends on a mended predecessor (the brief named the merge, not what wakes it).

## 5. The orchestrator starts a lane's successor

When `wait.sh --ship` names PRED (or `day.sh` shows it ready), the orchestrator
gates PRED and, in the same turn, starts the lane's next batch as a session whose
brief's first step is `git fetch origin && git checkout -b batch/<id>
origin/batch/PRED`. It then needs to do nothing else for that lane: the
successor waits in `wait.sh --row PRED`, merges main when PRED lands (or PRED's
fix when it is sent back), and pushes its ship file last, so `wait.sh --ship`
names it only when it can be gated. A successor that pushes its ship file early
shows as `waiting` in `day.sh` and stops at `ship.sh`.
