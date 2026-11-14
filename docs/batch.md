# A batch session

What a batch session reads and does, from its row in `QUEUE.md` to a pushed
branch. The orchestrator's part (the day, the gate, the PC, the Earth file,
the other assistants, the docs pass) is `docs/orchestrator.md`, which a batch
session does not read; a slice reads neither, its brief says what it needs.
`CLAUDE.md`'s standing rules bind every line. Each rule has a one-line WHY
naming its incident; the longer story and the lessons cited by letter (a-f)
are `docs/notes/batch-history.md`. Split by reader in BM1 (2026-10-28).

Each batch runs in a worktree of the orchestrator's clone, on a branch of its
own (`batch/<id>`) from `main`. Fetch first and run `sh tools/batch-id.sh ID`
before creating it: changelog or merged-branch evidence refuses a used ID.
WHY (GG1, PD1): pushing a reused merged branch fast-forwarded it. Batches check side by side (TM1). Every run but
`batch-check.sh --timing` times soft. Speed is judged nightly on main by
`tools/nightly.sh`, with machine-local figures and paired confirmation (GR1,
the gate's rounds took 33.7 hours without identifying a faulty batch).
The suite runs three harnesses at once (SP1, `AllChecks.PARALLEL`,
each JVM under `-Xmx` `AllChecks.HEAP_MB`, none started while the commit charge
is over `AllChecks.COMMIT_PCT`); a hard run, `-Dcbs.parallel=1` in
`JDK_JAVA_OPTIONS` (memory short) and the cloud workbench (two cores, one JVM
under `batch-check.sh`'s lock) go one at a time.

1. Read `docs/map/README.md`, then the map of each file the change touches;
   grep `docs/harnesses.md` for what already checks the area, and `sh
   tools/pick.sh` says what `--pick` would run on the diff so far.
2. **No baseline suite** (BP1 item 3: main's reds are recorded by its last
   gate in `docs/notes/gate.txt`). While building: `./batch-check.sh --fast`,
   and named harnesses (`./batch-check.sh <names>`) as the work needs.
3. Change the model first, the save second, the screen last; a harness for the
   mechanic before the screen, which is checked by eye. Codex builds whole
   batches (Jerus, WC1); review the diff as a stranger's. Commit small and often.
4. At the end, `./batch-check.sh --pick` compiles, runs the selected harnesses,
   regenerates maps, checks Stale and the tree. Exit 0 is green, 3 only known
   reds, 4 NEW reds, 1 a build or incomplete-check failure, 2 usage.
   `docs/notes/gate.txt` supplies exact lines, `docs/notes/known-reds.txt`
   harness-scoped parts. `AllChecks -f`, `--judge LOG` and the gate share one
   judge. A shared class over `CBS_PICK_FANOUT` selects `ALL`; changed harnesses
   select their users and TimingCheck. WHY (GR2, WG2) the builder missed reds
   in shared fixtures and 16 harnesses until the gate.
   When the pick is `ALL`, push the branch with a provisional ship file (5),
   then run `sh tools/gate.sh ID --dry-run` (`--no-tag` for process batches)
   in its fresh clone before readiness. A publisher change also tests its
   branch publisher with `--branch-gate --dry-run`.
   Keep its run directory and both commit identities in the notes. Other picks
   are ready on exit 0 or 3. Run changed scripts' self-tests as well.
5. **The records go in `docs/queue/ID-ship.md`**, separate from `ID-notes.md`.
   A branch keeps `GameVersion.VERSION`, `QUEUE.md`, `docs/notes/changelog.md`
   and `docs/notes/gate.txt` for the publisher. WHY (BP1) concurrent batches
   each wrote the same version and queue. The ship file has these sections,
   each optional except its changelog.

   ```
   ## changelog
   ### {next} — ID, title — date, ready, see docs/queue/ID-notes.md
   <one paragraph, at most 12 lines including its heading>
   ## pending
   | ID-X | what remains ({next}) | why | link |
   ## queued
   | NEW | what was decided | brief |
   ## known-reds
   Harness | part of the red line | queued fix | date | cause
   ```

   Pending and queued sections contain rows only when needed. Trace each NEW
   red unrelated to the batch in its notes and name its queued fix. The gate
   files these entries and removes entries whose fix is the batch being judged,
   including their cover in the exact record. A ship row with the same harness
   and part may transfer its own entry to another queued fix; state the cause.
   `batch-check.sh` applies both rules too. WHY (GG1, KS1/DM2): the builder
   saw green where the gate vetoed the re-filed red.
   A kept launcher run is named by `<!-- gate-run: RUN_DIRECTORY -->` above
   the sections. The gate rejudges it against current known reds when branch,
   main and selection qualify. After a kept run, only its notes, ship file and
   `docs/notes/known-reds.txt` may advance; the new registry is rejudged (GG1).
   Before the dry run, push the ship file with `<!-- readiness: provisional -->`.
   It supplies the version and records, while day/wait and real publication
   exclude it. Remove that line and push the ship file last when ready; put
   it back and push first on a return. WHY (GG1, BS1/DM2): versioned dry runs
   need records before readiness. Marker-free existing ship files stay ready.
   Write records as UTF-8 through Git Bash. Headings require two ` — `
   separators; a process batch uses `### ID — title — date, ready, no tag
   (process), see docs/queue/ID-notes.md`, a versioned one the example above.
   Without a queue row the heading supplies the title. WHY (GG1, HA1/GDP1):
   lost bytes and bookkeeping commit titles reached the record.
   `{next}` is filled only in the three record files and the ensemble's name;
   added source containing it is refused. WHY (GG1, BS1): assertion labels
   shipped with a literal version token. Existing main comments are left alone.
6. **The ensemble only when the trace moves** (EN1, Jerus 2026-10-20): a batch
   whose default playtest's trace is byte-identical to main's but for its
   run-time line skips the 80-run ensemble and its notes say so; one whose
   trace moved runs `tools/ensemble/ensemble.sh <label>` once, on its final
   model, against the newest `docs/ensemble/<version>.md`. Only M1 (money audit)
   and F1 (findings) stop shipping. All other criteria stay scored as information,
   beside the baseline with changed verdicts called out (EI1, HW1's T2 stop).
   Main is never rerun for a baseline. Commit the run's `baseline.md` as
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

8. The report is "ready, see the ship file", then any calls with recommendations
   and the exit and red lines if needed. Ready means the pick passed, or for
   `ALL` the launcher's full dry run of the merge passed, and the ship file
   was pushed last. WHY (GR2, WG2) a builder needs the whole shared-code verdict
   before its readiness marker. Its notes and ship file name the retained run.
9. The write-up, in the project, if the batch changes how the game plays or
   leaves calls for Jerus; otherwise the changelog's paragraph is the record.
10. **One in, one out** (BM1; `CLAUDE.md`'s standing rules): a batch that
    changes the process (the scripts, the checks' machinery, the rules a
    session reads) removes about as many lines as it adds, and its notes give
    the count. A rule it adds keeps a one-line WHY naming its incident.
