# A batch session

A batch reads this checklist, the orchestrator reads `docs/orchestrator.md`,
and a slice reads its brief. `CLAUDE.md`'s standing rules bind all three.
WHY (BM1) split readers after process instructions grew. Incident history
and lessons a–f live in `docs/notes/batch-history.md`.

Work in a worktree on `batch/<id>`, cut from main. Fetch and run
`sh tools/batch-id.sh ID` before creating it.
WHY (GG1, PD1) a reused merged branch was pushed again.
Concurrent soft suites share `AllChecks.PARALLEL` slots, heap-bounded by
`HEAP_MB`. MG1 reserves free commit for another harness and running growth,
requires available RAM and averaged CPU within their dials, and reports holds.
Hard timing, plain output and `-Dcbs.parallel=1` run serially.
The cloud workbench also takes `batch-check.sh`'s one-JVM lock.
WHY (SP1, MG1) exhausted commitment and CPU contention stopped suites.
Speed is judged nightly with machine-local paired confirmation (GR1).

1. Read `docs/map/README.md` and the touched files' maps, grep
   `docs/harnesses.md`, and use `sh tools/pick.sh` to see the selection.
   Settle in the notes each Pending row that names this batch ("For ID").
2. Main's last gate supplies the baseline reds. While building, use
   `./batch-check.sh --fast` and named harnesses.
   WHY (BP1) a fresh baseline suite repeats the recorded gate.
3. Change model, save, then screen. Prove mechanics with a harness before
   checking the screen by eye. Codex builds whole batches (WC1), reviews the
   diff as a stranger and commits small changes.
4. Finish with `./batch-check.sh --pick`. It compiles, runs selected harnesses,
   regenerates maps and checks Stale/tree. Exit 0 passes, 3 has only known
   reds, 4 has NEW reds, 1 fails build/completeness, 2 is usage.
   `gate.txt` exact lines and `known-reds.txt` harness parts share one judge.
   Shared classes over `CBS_PICK_FANOUT` pick ALL, changed harnesses select
   their users and TimingCheck. WHY (GR2, WG2) shared fixtures hid 16 reds.
   For ALL, push a provisional ship file, then run
   `sh tools/gate.sh ID --dry-run` in its fresh clone, adding `--no-tag`
   for process batches. Publisher edits also test `--branch-gate --dry-run`.
   Keep the run directory and both commits in the notes. Other picks need
   exit 0 or 3. Run changed scripts' self-tests.
5. Put publication records in `docs/queue/ID-ship.md`, separate from notes.
   Leave VERSION, QUEUE, changelog and gate.txt to the publisher.
   WHY (BP1) concurrent builders wrote the same records and version.

   ```
   <!-- readiness: provisional -->
   ## changelog
   ### {next} — ID, title — date, ready, see docs/queue/ID-notes.md
   <one paragraph, at most 12 lines including its heading>
   ## pending
   | ID-X | what remains ({next}) | why | link |
   ## queued
   | <new id: tools/batch-id.sh> | what was decided | brief |
   ## known-reds
   Harness | part of the red line | queued fix | date | cause
   ```

   Only changelog is mandatory. Pending/queued sections hold needed rows.
   Trace unrelated NEW reds in notes and name their queued fixes. The judge
   removes a batch's own cover, including exact records. Transferring an
   entry needs a ship row naming the replacement fix and cause.
   WHY (GG1, KS1/DM2) re-filed reds passed locally and failed at the gate.
   Name a kept run above the sections with `<!-- gate-run: RUN_DIRECTORY -->`.
   It is rejudged when branch, main and selection qualify. Afterward only
   notes, ship and known-reds.txt may advance, with registry changes rejudged.
   Push the provisional marker before the dry run, remove it last for ready,
   and restore/push it first on a return. WHY (GG1, BS1/DM2) dry runs need
   records while day/wait and publication must exclude unfinished branches.
   Write UTF-8 through Git Bash. Headings have two em-dash separators.
   Process headings use `### ID — title — date, ready, no tag (process), see docs/queue/ID-notes.md`.
   Without a queue row the heading supplies the title.
   WHY (GG1, HA1/GDP1) lost bytes and bookkeeping titles reached the record.
   Only record files and ensemble names receive the next-version token.
   Added source containing it is refused, existing main comments stay.
   WHY (GG1, BS1) assertion labels shipped with an unfilled version token.
6. Run the 80-run ensemble once on the final model when the default trace
   changes beyond runtime, comparing with the newest versioned baseline.
   Otherwise say the trace was unchanged. Main is never rerun for comparison.
   Commit `baseline.md` as `docs/ensemble/<id>.md`, renamed by the publisher.
   Baselines from EN2 onward name their data files. WHY (EN2) worktrees lacked data.
   Only M1 and F1 stop shipping, other scores and changed verdicts go beside
   the baseline in the notes. WHY (EI1, HW1) T2's informational stop blocked shipping.
7. Wait through long jobs in the foreground after starting them in the
   background, using `tools/wait.sh --line LOG '^exit [0-9]' --for 9`
   with a 600000 ms Bash timeout, repeating until done.
   WHY (BP1) end a session only when done or blocked, so dispatch sees a result.
8. Ready means the pick passed, or ALL's launcher merge dry run passed,
   and the ship file was pushed last. Report ready and the ship file,
   any calls with recommendations, and exit/red lines as needed.
   WHY (GR2, WG2) shared changes need the whole merge verdict.
9. A project write-up is only for gameplay changes or calls left for Jerus.
10. Process changes remove about as many lines as they add. Count scripts,
    checks' machinery and session rules against origin/main, excluding the
    batch's notes and ship. Every added rule keeps its incident's one-line WHY.
    WHY (BM1) process grew from 309 to 724 lines in four days.
