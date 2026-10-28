# A batch session

What a batch session reads and does, from its row in `QUEUE.md` to a pushed
branch. The orchestrator's part (the day, the gate, the PC, the Earth file,
the other assistants, the docs pass) is `docs/orchestrator.md`, which a batch
session does not read; a slice reads neither, its brief says what it needs.
`CLAUDE.md`'s standing rules bind every line. Each rule has a one-line WHY
naming its incident; the longer story and the lessons cited by letter (a-f)
are `docs/notes/batch-history.md`. Split by reader in BM1 (2026-10-28).

Each batch runs in a worktree of the orchestrator's clone, on a branch of its
own (`batch/<id>`) from `main`, or, as a lane's next batch, from its
predecessor's tip (7). Batches check side by side (TM1, 2026-10-18), so every
`batch-check.sh` run but `--timing` times soft: a figure over its budget says
SOFT and is not counted. Timing is judged hard only at the gate (BP1's review:
a run that began alone timed hard, and main's own misses on a slow day came
out as the batch's reds). The suite runs three harnesses at once (SP1,
`AllChecks.PARALLEL`, each JVM under `-Xmx` `AllChecks.HEAP_MB`, none started
while the commit charge is over `AllChecks.COMMIT_PCT`); a hard run, `-Dcbs.parallel=1`
in `JDK_JAVA_OPTIONS` (memory short) and the cloud workbench (two cores, one
JVM under the lock `batch-check.sh` takes) go one at a time.

1. Read `docs/map/README.md`, then the map of each file the change touches;
   grep `docs/harnesses.md` for what already checks the area, and `sh
   tools/pick.sh` says what `--pick` would run on the diff so far.
2. **No baseline suite** (BP1 item 3: main's reds are recorded by its last
   gate in `docs/notes/gate.txt`). While building: `./batch-check.sh --fast`,
   and named harnesses (`./batch-check.sh <names>`) as the work needs.
3. Change the model first, the save second, the screen last; a harness for the
   mechanic before the screen, which is checked by eye. The coding goes to
   Sonnet (`docs/deepseek.md`) in **fewer, larger slices** (BP1 item 6), each
   one a brief can pin down, and the diff is reviewed as a stranger's
   **without re-reading what the brief sent Sonnet to read**. A **small fix
   batch** - no money, saves or threading, a few hundred lines - runs on
   Sonnet as the whole session, Opus at the gate only (BP1 item 7). Every
   session and slice runs at xhigh or max (Jerus, 2026-10-24: a medium-effort
   orchestrator stalled a lane, lesson e). Commit small and often.
4. At the end, `./batch-check.sh --pick`: the compile, the harnesses that read
   what the diff changed (`tools/pick.sh`), the maps regenerated, Stale and
   the tree. **Exit 0** is green; **3**, nothing failed but main's recorded
   reds, word for word: green, for a batch; **4** prints the red lines that
   are not main's, the batch's to mend; 1 is anything else (the compile, the
   maps, Stale, line endings, a harness `AllChecks` does not register), 2 a
   mistake in the options. `AllChecks -q` and `-f` say `known red` or `NEW red`
   and tag each red line `[known]` or `[NEW]` (FW7: a known-red harness with a
   second failing line says NEW); `--judge <log>` judges a saved log again. A
   class most harnesses read (`Game.java`) is left to the gate's suite,
   `LongPlaytest` picked in its place (more than `CBS_PICK_FANOUT` readers); a
   `*Check.java` in the diff also picks `TimingCheck` (TM3: ET6's new harness
   compared times against its budgets directly, and only the gate's suite ran
   the rule that catches it). After a change to `tools/pick.sh`, `sh
   tools/pick-selftest.sh`. Commit the sources, `buildings.json` and `docs/`
   together.
5. **The records go in the ship file** (BP1 item 4, lesson f: batches side by
   side each took main + 1 and re-merged, and each wrote the same top of the
   changelog and `QUEUE.md`). A batch branch never edits `GameVersion.VERSION`,
   `QUEUE.md`, `docs/notes/changelog.md` or `docs/notes/gate.txt`, nor writes a
   paragraph above `VERSION` in `GameVersion`'s javadoc. Where its version
   would go it writes `{next}`, and its notes' title says `({next}, date)`.
   The notes (`docs/queue/<id>-notes.md`) and the records stay two files:
   `ship.sh`'s heading check reads the ship file alone (BM1). The ship file,
   `docs/queue/<id>-ship.md`:

   ```
   ## changelog
   ### {next} — ID, title — date, state, see docs/queue/ID-notes.md
   <one paragraph, at most 12 lines in all>
   ## pending
   | ID-C | ... ({next}) ... | ... | ... |
   ## queued
   | NEW | ... | ... |
   ```

   `## pending` and `## queued` only when it has rows for them; no other `## `
   heading. What the batch left undone goes in `## pending`, a batch it decides
   in `## queued`; `ship.sh` sets the version and files them at the merge
   (`docs/orchestrator.md`, The gate 4). **The ship file is pushed last**: on
   the branch it means "ready to ship". A branch the gate sends back first
   takes it off (`git rm docs/queue/<id>-ship.md`, pushed) and pushes it again
   last when mended; left on, `day.sh --due` calls it ready every hour. A
   branch with no ship file ships `--no-tag`, its records as it wrote them, as
   an assistant's does (RL1: the old way, with its own version and row, is
   gone); a batch whose change no player sees may write no `{next}` either.
   `ship.sh` stops a branch with no ship file and no `--no-tag`, `--no-tag`
   with a `VERSION` change, a branch carrying another id's ship file, and one
   built on a batch main lacks (7).
6. **The ensemble only when the trace moves** (EN1, Jerus 2026-10-20): a batch
   whose default playtest's trace is byte-identical to main's but for its
   run-time line skips the 80-run ensemble and its notes say so; one whose
   trace moved runs `tools/ensemble/ensemble.sh <label>` once, on its final
   model, against the newest `docs/ensemble/<version>.md` (main is never rerun
   for a baseline), and commits the run's `baseline.md` as
   `docs/ensemble/<id>.md`; `ship.sh` renames it by the version at the gate.
7. **A session ends a turn only when it is done or blocked** (BP1 item 2: the
   orchestrator is woken only by a finished report or a question it must
   answer). A wait of minutes (a suite, a playtest) is a background run and
   then a wait in the foreground, repeated until it says done, in a Bash call
   whose timeout is raised to 600000 ms (the default cuts it at two minutes),
   hence `--for 9`:

   ```
   mkdir -p target/checks; ./batch-check.sh --pick > target/checks/run.txt 2>&1; echo "exit $?" >> target/checks/run.txt   # in the background
   sh tools/wait.sh --line target/checks/run.txt '^exit [0-9]' --for 9                                                       # in the foreground, Bash timeout 600000
   ```

   **A lane's next batch is built on its predecessor** (RL1): `git fetch origin
   && git checkout -b batch/<id> origin/batch/PRED`, once PRED's ship file is on
   origin; that ship file stays as it is. `--pick` diffs against PRED's tip
   (`tools/carries.sh` names the unmerged batch a tree is built on). The
   session waits in one background `sh tools/wait.sh --row PRED`, which ends
   when PRED's row has left origin's `QUEUE.md` or PRED is ready again after a
   gate sent it back. PRED landed: `git fetch -q origin && git merge
   origin/main` at once (a conflict in the generated docs: main's side, `--pick`
   regenerates them). PRED mended: `git merge origin/batch/PRED`, carry on and
   wait again; ask only if the fix removes something this batch uses. **The
   ship file is pushed last, and only once PRED is on main and merged in**:
   until then `day.sh` calls the branch waiting, `wait.sh --ship` leaves it out
   and `ship.sh` stops it (it would ship PRED ungated). A NEW red under
   `--pick` while PRED is unmerged may be one Jerus approves at PRED's gate:
   merge main and run again before mending it.
8. **The report is "ready, see the ship file"** (BM1), then its ★ calls each
   with a recommendation (the orchestrator takes the recommended ones), and
   the checks' exit and red lines only if not green: not a second summary of
   what the ship file and the notes say. A session is ready when `--pick` exits
   0 or 3 and its ship file is pushed, last (a successor: once PRED is on main
   and merged in).
9. The write-up, in the project, if the batch changes how the game plays or
   leaves calls for Jerus; otherwise the changelog's paragraph is the record.
10. **One in, one out** (BM1; `CLAUDE.md`'s standing rules): a batch that
    changes the process (the scripts, the checks' machinery, the rules a
    session reads) removes about as many lines as it adds, and its notes give
    the count. A rule it adds keeps a one-line WHY naming its incident.
