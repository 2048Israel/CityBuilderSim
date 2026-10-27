# GP1 - the gate starts at once: no quiet minute, and a ready branch picked up when its ship file lands (no tag, 2026-10-27)

The batch's record beside its brief, `GP1.md`. It is tooling and `docs/`: no
game change, no version, no trace moved, no ensemble (nothing in the model, a
save or a screen was touched). Run whole on Sonnet 5.5 at xhigh, Opus at the
gate only (BP1 item 7).

## 1. What was there

- The gate's `--timing` waited for a quiet minute (TM2): four empty looks 15 s
  apart, up to 1,800 s, restarting when another lane's JVM turned up. With two
  lanes the other lane is often running checks, and since TM3 a timing red is
  judged side by side with main in the same minutes anyway, which is what the
  wait only approximated.
- A ready branch waited for the orchestrator's hourly :07 check-in (`day.sh
  --due`): from ship file to merge a median of about 60 min over 16 gates, of
  which `ship.sh` itself is 22-25.

## 2. What changed

1. **The quiet minute is out, all of it.** `quiet_minute` and its
   `CBS_TIMING_WAIT_ONLY` hook in `batch-check.sh`; the `-Quiet` mode, the
   `CBS_QUIET_*` dials and `CBS_JVMS_MARK` in `tools/jvms.ps1` (the script is
   now only the count, which the soft run's line and `tools/deepseek/agent.py`
   still use); the wait and the hook in `Batch check.bat`;
   `tools/quiet-selftest.sh` (231 lines); `CBS_TIMING_PAIRED`, which did nothing
   but skip the wait in a paired round, in `batch-check.sh`, `ship.sh` and
   `ship-selftest.sh` (its one assertion went; the six turns assertion beside it
   still holds the three rounds); TM2's prose in `docs/batch.md` (The gate 6 and
   7, On the PC), `ship.sh`'s header (step 9) and `docs/tree.md`. `--timing`
   compiles and runs at once.
2. **`sh tools/wait.sh --ship [--skip ID...] [--for MIN]`.** It fetches origin's
   `main` and `batch/*` (the deleted ones pruned) every 2 minutes, as `--row`
   does, and ends, in one line and exit 0, when an unmerged `batch/<id>` branch
   has `docs/queue/<id>-ship.md`: `wait      done - ready to ship: A B
   (./ship.sh batch/<id>; <sha>, <time>)`. A branch ready already ends it at
   once; a merged one (tip in main's history) and a deleted one do not count;
   the test is `day.sh`'s ready test (a ship file on the tip of an unmerged
   batch branch), so the two cannot disagree. `--skip` names the ids the
   orchestrator holds with the ship file on (a gate stopped for Jerus's word):
   without it the wait would end at once, every time. The rest is `--row`'s loop
   (the deadline, the 6 failed fetches, the one-line endings), shared, not
   copied. `docs/batch.md`, The day, says how it is armed and re-armed.
3. **`tools/wait-selftest.sh`** (new, 14 cases, about 20 s, no JVM): a bare
   `origin.git`, a `work` clone that pushes, an `orch` clone that waits. A
   batch branch with no ship file, another id's ship file, a ship file on a
   `chatgpt/` branch and a push to `main` do not end it; a wait started in the
   background, with a main push and a batch push meanwhile, is still waiting,
   and the ship file pushed ends it within seconds with exit 0, the id and the
   command; a branch already ready ends it at once; `--skip` takes one out; a
   merged branch and a deleted one are not ready; `--skip` without `--ship` is a
   usage error. Each of three mutations of `wait.sh` (no ship-file test, no
   merged test, no `--prune`) fails the cases that cover it.

## 3. One in, one out

`git diff --numstat origin/main` over the scripts and the rules, this batch's
notes and ship file left out: **239 lines added, 555 removed, 316 fewer.**
Removed: `quiet-selftest.sh` 231, `jvms.ps1` 140 (25 left), `batch-check.sh` 104,
`Batch check.bat` 26, `docs/batch.md` 19, `wait.sh` 18, `docs/tree.md` 11, three
in `ship.sh`, three in `ship-selftest.sh`. Added: `wait-selftest.sh` 86 (new),
`wait.sh` 60 (the `--ship` mode and its header), `docs/batch.md` 36 (the
ready-branch wait, 20 of them), `jvms.ps1` 25 (the count, as it was, and the
WHY), `batch-check.sh` 11, `docs/tree.md` 10, `Batch check.bat` 6.

## 4. Calls

- **★1 Does the quiet minute still earn a place** (say, the first hard run
  before any pairing)? Recommendation: remove it. **Taken: removed.** It cost
  up to 30 min of a 22-25 min gate and could not make a green run any greener;
  a red under load goes to the paired rounds, which share one load by design.
  The only place left to put it would be before the first hard run, and that run
  is judged by the same rounds if it is red.

Taken without a call, for the reader to overturn: `--skip` (the brief did not
name it; the ready branches on origin today, four of them held for different
reasons, show a bare wait would end at once); the poll is `--row`'s 2 minutes
(`CBS_WAIT_ROW_SECS`), so a gate starts within about two minutes of the push;
the wait is a `wait.sh` mode, not a `day.sh --due` loop, because `day.sh` fetches
tags and runs `merge-tree` for every ready branch on every look.

## 5. Left

- `TM2-C` in Pending (the quiet minute's two calls) is moot: ★1 (the game not
  waited for) and ★2 (four looks is about 45 s) were about a wait that is gone.
  The orchestrator may settle it as recommended (QUEUE.md's rules) and drop the
  row; a batch branch leaves `QUEUE.md` alone.
- RL1 builds on `--ship`: a lane's successor can wait for its predecessor's ship
  file on origin with the same loop.

## 6. Checks

`sh tools/wait-selftest.sh` 14 of 14 (and 12-13 of 14 under each of three
mutations of `wait.sh`). `sh tools/ship-selftest.sh` 103 of 103 (it was 104;
the case that counted `CBS_TIMING_PAIRED` runs went with the variable).
`./batch-check.sh --pick` exit 0: compile, no harness reads the diff, maps,
Stale 0 firm findings, a clean tree. `./batch-check.sh --timing PlanCheck` and
`Batch check.bat --timing PlanCheck` start at once (28 s and 19 s, with another
lane's JVM on the machine), and the bat's soft run still says who runs.
`wait.sh --row` and `--line` tried as before, against origin and a file.
