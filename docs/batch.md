# How a batch is done

How a batch goes from its row in `QUEUE.md` to a tag on `main`: the
orchestrator's day, a batch session's work on its branch, the gate that
`ship.sh` runs, and the sessions and rules around them. The orchestrator
and the batch sessions read it; a slice does not, since its brief says what
it needs. It came out of `CLAUDE.md` in BP1 (2026-10-24), so that a slice,
which loads `CLAUDE.md` whole, no longer loads this with it (Jerus,
2026-10-23: the batch process is "quite the slow and token heavy process").
`CLAUDE.md`'s standing rules bind every line here. `docs/queue/BP1.md` is
the brief, with the figures it was measured against.

## The day

**A fresh orchestrator each day** (BP1, Jerus 2026-10-23: "yes, all
recs"). The old one was about 500k tokens deep after two days, and every
wake-up - a session's turn end, a report, a background task - re-sent all
of it; a fresh one is about 70k. At the end of its day the orchestrator
CronDeletes its hourly check-in (below), then overwrites the PC memory note
`orchestrator-handoff.md`, at most 40 lines: its own session id, the lanes,
each running session's name and id, the gates waiting, Jerus's open
questions, and the screen-check session's id. The next day's orchestrator
archives the session that note names as orchestrator before it arms its
own check-in, then starts from the note, `sh tools/day.sh` and the top of
`docs/notes/changelog.md`, and reads nothing else until a session needs it.

`sh tools/day.sh` is the morning in one call, in about fifteen lines:
main's `GameVersion.VERSION` against the last version tag, main's recorded
reds (`docs/notes/gate.txt`), the next queued ids, the Pending ids, and
each unmerged `batch/` branch on origin as ready (its ship file pushed, or
an old-style branch's row out of its own `QUEUE.md`; with the conflicts a
merge would meet), working, stuck (no commit for a day) or off-queue.

**The hourly check-in** (lesson b). Each day the orchestrator creates a
CronCreate job at minute 7 of every hour (`7 * * * *`) whose prompt is: run
`sh tools/day.sh --due`, and if it prints nothing, end the turn at once
with no other call. `--due` prints only the ready and the stuck branches,
so the orchestrator acts on the hour only when it is quiet and batch work
is due: a branch to gate, a lane stuck. The job lives in the session that
made it, so each day's orchestrator arms its own, and yesterday's is
deleted, not left to lapse after seven days: left, it wakes yesterday's
whole context every hour, and two orchestrators may gate one branch.

**Two lanes, on files of their own**: the next two queued batches side by
side, as sessions, and never more than two at once. A lane's next batch
waits in the background for its predecessor's row to leave `QUEUE.md`
(`sh tools/wait.sh --row PRED`, below), then takes `origin/main`.

**A blocked batch goes under Pending at once** (lesson a): its Queued row
out and a Pending row in, saying why, in a queue commit pushed to `main`
the same turn, so the next row in its lane can start. Who returns it to
Queued when its block clears by itself is a call left to Jerus (the calls
below, 2); until he says, only his review moves a Pending row, save one the
orchestrator settles as the next paragraph says.

**The orchestrator answers what it safely can** (BP1 item 9, Jerus
2026-10-23). A session's question about the process, the code or a reading
of its brief it answers itself. It takes the recommended option on a
batch's ★ calls (Jerus: "dont most end up being rec? its only a few that
arent"), writes in the notes which it took and why, and the batch ships
without waiting on him; he can overturn one later. A Pending row that
waits only on calls with a recommendation it settles the same way (the
standing rule on the queue in `CLAUDE.md`). It sends Jerus only what is
his: a call with no clear recommendation, one that moves a standing rule or
a premise, one that spends money, an eye check of his.

## A batch session

Each batch runs in a worktree of the orchestrator's clone, on a branch of
its own from `main` (`batch/<id>`), as it has since the git era began
(2026-10-10). **Batches check side by side** (TM1, 2026-10-18): every
`batch-check.sh` run but `--timing` times soft - a timing figure over its
budget says SOFT and is not counted (`Timing`, `-Dcbs.timing=soft`, which
the script sets by itself) - and timing is judged hard only by `--timing`
at the ship gate, which runs the timing harnesses alone (BP1's review: a
run that began alone timed hard, and main's own misses on a slow day came
out as the batch's reds). The cloud workbench, with two cores, still runs
one JVM at a time under the shared lock (`batch-check.sh` takes it).

1. Read `docs/map/README.md`, then the map of each file the change
   touches; `docs/harnesses.md` (grep it) says what already checks the
   area, and `sh tools/pick.sh` what `--pick` would run on the diff so far.
2. **No baseline suite** (BP1 item 3). Main's reds are recorded by its last
   gate in `docs/notes/gate.txt`, so a batch never runs the whole suite to
   learn them. While building: `./batch-check.sh --fast`, and named
   harnesses (`./batch-check.sh <harness names>`) as the work needs them.
3. Change the model first, the save second, the screen last; a harness for
   the mechanic before the screen, because the screen is checked by eye.
   The coding goes to Sonnet (`docs/deepseek.md`) in **fewer, larger
   slices** (BP1 item 6), each one a brief can pin down, and the session
   reviews each diff as a stranger's **without re-reading what the brief
   sent Sonnet to read**. A **small fix batch** - no money, saves or
   threading, a few hundred lines - runs on Sonnet as the whole session,
   with Opus at the gate only (item 7). Commit to the branch, small and
   often.
4. At the end, `./batch-check.sh --pick`: the compile, the harnesses that
   read what the diff changed (`tools/pick.sh`), the maps regenerated,
   Stale and the tree. Exit 0 is green. Exit 3 means nothing failed but
   main's recorded reds, word for word: green, for a batch. Exit 4 prints
   the red lines that are not main's, and they are the batch's to mend.
   Since FW7 `AllChecks -q` and `-f` read the same record (`gate.txt`, or
   `-Dcbs.knownReds=<file>`) and say `known red` or `NEW red: N of M red
   lines` after a failed harness's time, and `-f` tags each red line
   `[known]` or `[NEW]`: a known-red harness with a second failing line says
   NEW. `./batch-check.sh --judge <log>` judges a saved `-f` log again
   without running anything.
   Exit 1 is anything else (the compile, the maps, Stale, line endings, a
   harness `AllChecks` does not register), and 2 a mistake in the options.
   A class most harnesses read (`Game.java` and the like) is left to the
   gate's suite, with `LongPlaytest` picked in its place (the calls below,
   1). A `*Check.java` in the diff, changed or added, also picks `TimingCheck`
   (TM3 item 8: ET6's new harness compared times against its budgets directly,
   and only the gate's full suite ran the rule that catches it); after a
   change to `tools/pick.sh`, `sh tools/pick-selftest.sh` (no JVM, seconds). Commit the sources, `buildings.json` and `docs/` together.
5. **The records go in the ship file** (BP1 item 4, lesson f). A batch
   branch never edits `GameVersion.VERSION`, `QUEUE.md`,
   `docs/notes/changelog.md` or `docs/notes/gate.txt`, and writes no
   paragraph above `VERSION` in `GameVersion`'s javadoc: the changelog is
   the one record (the calls below, 3). Where its version would go it
   writes `{next}`, and its notes' title says `({next}, date)`. Its records
   are one file, `docs/queue/<id>-ship.md`:

   ```
   ## changelog
   ### {next} — ID, title — date, state, see docs/queue/ID-notes.md
   <one paragraph, at most 12 lines in all>
   ## pending
   | ID-C | ... ({next}) ... | ... | ... |
   ## queued
   | NEW | ... | ... |
   ```

   `## pending` and `## queued` are there only when it has rows for them,
   and no other `## ` heading is allowed. What the batch left undone goes
   in `## pending`; a batch it decides goes in `## queued`, and reaches the
   queue in its merge. At the merge `ship.sh` sets `GameVersion.VERSION`,
   fills `{next}` in the ship file, the notes and the ensemble baseline
   (a `{next}` anywhere else is left, with a line saying so), puts the
   changelog block on top, deletes the batch's Queued row, puts its pending
   rows at the top of Pending and its queued rows at the end of Queued, and
   removes the ship file. A batch whose change no player sees may write no
   `{next}` at all and be shipped `--no-tag`, as the docs pass is. **The
   ship file is pushed last**: on the branch it means "ready to ship". A
   branch sent back from the gate first takes it off (`git rm
   docs/queue/<id>-ship.md`, pushed) and pushes it again last when mended;
   left on, `day.sh --due` calls the branch ready every hour. A branch with
   no ship file still ships the old way during the change-over, its records
   as it wrote them (BP1 itself ships so, `--no-tag`); `ship.sh` stops one
   whose `VERSION` main has tagged already (merge main and bump it, or
   write a ship file), `--no-tag` with a `VERSION` change, and any branch
   carrying another id's ship file.
6. **The ensemble only when the trace moves** (EN1, Jerus 2026-10-20): a
   batch whose default playtest's trace is byte-identical to main's but for
   its run-time line skips the 80-run ensemble and its notes say so; one
   whose trace moved runs `tools/ensemble/ensemble.sh <label>` once, on its
   final model, against the newest `docs/ensemble/<version>.md` (main is
   never rerun for a baseline), and commits the run's `baseline.md` as
   `docs/ensemble/<id>.md`, under the batch's id; `ship.sh` renames it by
   the version at the gate (`tools/ensemble/README.md`).
7. **A session ends a turn only when it is done or blocked** (BP1 item 2),
   so the orchestrator is woken only by a finished report or a question it
   must answer. A wait of minutes (a suite, a playtest) is a background run
   and then a wait in the foreground, repeated until it says done, in a
   Bash call whose timeout is raised to 600000 ms, the most it allows (the
   default cuts the call at two minutes), hence `--for 9`:

   ```
   mkdir -p target/checks; ./batch-check.sh --pick > target/checks/run.txt 2>&1; echo "exit $?" >> target/checks/run.txt   # in the background
   sh tools/wait.sh --line target/checks/run.txt '^exit [0-9]' --for 9                                                       # in the foreground, Bash timeout 600000
   ```

   A wait of hours, for its lane's predecessor, is one background
   `sh tools/wait.sh --row PRED`, which ends when PRED's row has left the
   Queued table of origin's `QUEUE.md`. When it says done the session
   takes main as it now is, before any work: `git merge -q --ff-only
   origin/main` (nothing is committed yet, and the worktree was cut hours
   before, so the predecessor's shared files would conflict at the gate).
8. **The report is 15 lines**: what it did, the checks' exit and any red
   lines, its ★ calls each with a recommendation, and its branch pushed
   with the ship file last. The first batch after BP1 also reports its token
   cost against BP1's figures (an Opus batch session 400k-770k, a
   stranger's review 190k-260k, the suite about three times a batch).
9. The write-up, in the project, if it changes how the game plays or
   leaves calls for Jerus; otherwise the changelog's paragraph is the
   record.

## The gate

The orchestrator gates each ready branch (`sh tools/day.sh` names them).

1. It reads the diff's stat (`git diff --stat origin/main...origin/batch/<id>`),
   the notes, and the review line of
   `sh tools/pick.sh origin/main origin/batch/<id>`. It checks that the
   batch did what its brief asked, the playtest's trace and, when the trace
   moved, the ensemble's score against the last baseline.
2. **A stranger's review only where money, saves or threading change**
   (BP1 item 5): the bank, bonds, the treasury and funds; `DataSave`,
   `HistorySave`, the load path, `SAVE_FORMAT`; threads and the UI-wide.
   Otherwise the stat and the notes are the review. The review line names
   these from the diff and is a floor: the orchestrator may add a
   stranger's review, never skip one it names (BP1 found each cost
   190k-260k).
3. It takes the batch's recommended ★ calls and says which and why in the
   notes, sending Jerus only his (The day, above).
4. Then `./ship.sh batch/<id>` does the rest:

   ```
   ./ship.sh [--dry-run] [--no-tag] [--version X.Y.Z] [--red-ok "his words" --red-line "<part of a line>" ...] <branch> [-m title] [-F body] [-- harness names]
   ```

   - `main` checked out, clean and current with origin, and its
     `GameVersion.VERSION` equal to the last version tag, or it stops
     (tag or mend `main` first);
   - the merge: a conflict only in the generated docs takes the branch's
     side and regenerates them below; any other conflict stops it;
   - the records from the ship file (above), before the checks, so that the
     compile, the suite and the maps see the new version. `{next}` is the
     last version tag's patch + 1, so batches built side by side no longer
     collide (lesson f);
   - the batch's one full suite (BP1 item 3), its timing soft, then the
     timing harnesses alone and hard;
   - `docs/notes/gate.txt` rewritten: the merge's title, the date, whether
     earth-2.bin was named, the suite's summary and the timing line, then
     the suite's red lines. Only `ship.sh` writes it;
   - the commit `Merge batch/<id>: <title>, <version>` with a `Gate:` line
     in its body, the tag, and one atomic push of `main` and the tag. A push
     refused because `main` moved takes the merge and the tag back: run
     `ship.sh` again, and it takes the next number from the tags it
     fetches.
5. **Reds** (the calls below, 4). Main's recorded reds pass, word for word,
   and the body says `Gate: known red only: <harnesses>`. A NEW red
   line ships only with `--red-ok "his words"` and a `--red-line` for each
   line he approved, his words and the lines in the body. **His words pass
   the lines they name and no others** (TM3, the review, 2026-10-27): the
   lines are known before his word, because he is asked after a stop with
   that stop's lines in hand, and a broad `--red-ok` once let a different
   flaky line go red in the rerun and pass under words given for another
   (CC1-S's gate). So `--red-ok` without a `--red-line` is refused at the
   start (exit 2, the usage); a red line passes only if it contains one of
   the `--red-line` parts, as written (a fixed string, the option repeated
   for more lines); a harness's own `exit N` and `N FAILED` lines pass with
   a named line of it, when no other line of that harness is left unnamed;
   any other red stops the ship as before, with its lines marked `NOT
   NAMED`. Both steps read it, the suite's and the timing step's (below).
   **Use:** after a stop, show Jerus the stop's red lines; for each he
   approves, copy a distinctive part of the line into `--red-line`, e.g.
   `./ship.sh --red-ok "ship it" --red-line "a month's FX-side map work at
   5B" --red-line "EarthTerrainCheck | exit 1" batch/X`, and run again.
   Any other
   red goes back to the batch as its failing lines (`target/checks/reds.txt`
   in the gate's clone), and its ship file comes off first (A batch
   session, 5).
6. **Timing** (lesson d; the calls below, 5; TM3, 2026-10-27). A timing red
   at the gate is judged side by side with main, not by one run of the
   merge's: a wall-clock budget measured once on a PC that also runs Jerus's
   game measures the load as much as the code (HY1's gate stopped on three
   map lines main itself misses). `ship.sh` takes the red line's figure from
   `target/checks/timing.tsv`, which every timing assertion writes through
   `Timing.verdict(ok, label)` (harness, label, measured, budget, unit,
   status, part), never from the label's prose, and times the red harnesses
   again with main's classes (a worktree of `main`) and the merge's **in
   turn, back to back, three rounds** (`CBS_TIMING_ROUNDS`, at least two; no
   quiet minute between them, the rounds are to share one load). The red
   counts against the batch only if its median figure is **more than 15%**
   over main's (`tools/timing-pair.awk` holds the arithmetic and the dial;
   `CBS_TIMING_MARGIN` moves it for a test). Otherwise it passes, named in
   the merge's `Gate-timing:` line with both medians, and in `gate.txt` only
   as a `#` line, so it never becomes a recorded red. A red that is over the
   margin, one with nothing to compare (main has no such assertion, a round
   did not run) and one that is no clock's (a failure behind no missed
   figure) is the batch's and stops - unless `--red-ok "his words"` and a
   `--red-line` name it (step 5: the lines he approved, no others), said in
   `Gate-timing:` with his words and the lines, and in `gate.txt` as `#`
   lines. A timing red that is not named stops even with `--red-ok`, and a
   named line records nothing. Wall-clock
   comparisons that are not a budget (MapCheck's "the view adds nothing a
   tile" compared two measured times) are `Timing` assertions too
   (TimingCheck 6 holds it): soft in the suite, judged here.
7. `--version X.Y.Z` only on Jerus's word. `--dry-run` runs everything, the
   gate included, prints what it would commit, tag and push, and keeps
   nothing. Harness names after `--` only with `--dry-run`: a partial suite
   never ships. A docs-only merge (every path ends in `.md`, none under
   `docs/ensemble/`) gates on `--fast`: no suite, no timing, `gate.txt`
   untouched. `ship.sh` runs from a copy of itself (TM3): the merge of a
   batch that changes `ship.sh` rewrites the file, and a shell reads its
   script as it goes. The new one runs from the next ship. After any change
   to `ship.sh` or `tools/timing-pair.awk`, `sh tools/ship-selftest.sh`
   runs it against a throwaway local origin with a stub `batch-check.sh`:
   no JVM, about two minutes. After any change to the timing wait
   (`tools/jvms.ps1`, the `--timing` of the two check scripts),
   `sh tools/quiet-selftest.sh` tries it against real JVMs that come and go.

## The screen-check session

BP1 item 10 (Jerus, 2026-10-23). When a batch needs a screen looked at -
renders, or the game on slots 7-10 - it asks the orchestrator, which starts
one session whose only job is that, or reuses the one running (its id is
in the hand-off note). It takes a list of what to look at, returns what it
saw and the images' paths in 15 lines, and edits no source. One at a time,
on Sonnet unless a check needs judgement, at xhigh or max like every
session. **Slots 7-9 are its to write, even over a city of his there**
(Jerus: "it can do whatever it wants with slots 7-9 even if i have a city
save there"), and slot 10 as before; slots 1-6 it never writes. That is
`CLAUDE.md`'s standing rule, for this session alone: every other session
keeps off 1-9, and a harness still uses `GameFiles.scratch` or `TempDirs`.

## On the PC

Claude Code, NetBeans beside it: the same steps. Build with NetBeans *Clean
and Build* or `mvn -q compile`. Sessions check with Git Bash's
`./batch-check.sh`, which alone has `--pick` and the exits 3 and 4;
`Batch check.bat` is Jerus's double-click (`--maps` to regenerate the maps
too; without it it writes nothing git tracks, because the PC's copy pulls
`main` and must stay clean). Batches check side by side here, and alongside
Jerus's game, which nobody waits on him to close: `batch-check.sh` times
every run soft but `--timing`, which waits for a quiet minute and then runs
the timing harnesses alone and hard. A quiet minute is four empty looks in a
row, 15 s apart (`tools/jvms.ps1 -Quiet`, and the same loop in sh over `ps`
off the PC); a look that finds another batch's JVM starts the count again,
because a runner that starts its JVMs one after another leaves a gap of a
second or two that one look can fall in (TM2). It waits 30 minutes at most,
then runs hard without the quiet minute and says so. The game is not waited
for: a line says it runs, and `ship.sh` judges a timing red beside it side
by side with main's classes, in the same minutes (TM3); `CBS_QUIET_GAME=1` on the gate's
command waits for it too. `Batch check.bat` still times soft only while another batch's
JVM or the game runs, and decides **once, at its start**, so a run of it that
began alone stays hard when another starts beside it (lesson d); its
`--timing` waits as above.

A batch shipped from the PC ships from a temp clone of `origin`, never this
folder: `%TEMP%\cbs-<id>` (the scratchpad's path is past Windows' 260
characters), with `JDK_JAVA_OPTIONS` naming the PC checkout's
`data\earth-2.bin` (lesson c), in Git Bash there:

```
JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin ./ship.sh batch/<id>
```

Once it has shipped, the session deletes the clone and every other
`%TEMP%\cbs-<id>*` folder it made (`rm -rf "$TEMP"/cbs-<id>*`, run from
outside them): 24 left behind held 5 GB by 2026-10-21 (TD1). The full suite
is

```
java -cp "target/classes;%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar" ham.citybuildersim.AllChecks -f
```

(about nine minutes alone, and BP1 found 15-22 minutes beside other runs;
`AllChecks money bank` runs two).

## The Earth file

The real Earth is earth-2.bin since 0.8.41 (ET1; lesson c), at
`data/earth-2.bin` beside the game (`EarthData.DATA_FILE`; `data/` is
git-ignored, and a batch's worktree has none). The full-file sections of
EarthCheck, EarthTerrainCheck, EarthFoundingCheck, EarthDepositsCheck,
GlobeCheck, GatewayCheck, LandCheck and MapCheck run only where
earth-2.bin is named (`-Dcbs.earth=<path>`, through `JDK_JAVA_OPTIONS` for
the suite) or lies at `data/earth-2.bin`, and say so in one line where it
does not; `batch-check.sh` adds a note when the Earth harnesses'
full-file sections did not run, and `gate.txt` says whether the gate named
the file.
`LongPlaytest --earth rotterdam` plays the run on the real Earth; its trace
is a baseline of its own.

## Other assistants

Since 2026-10-13: ChatGPT (the GitHub connector: reads and writes, runs
nothing) and Gemini (AI Studio: a shell, git, Java and Maven; it ran the
checks to the cent on 2026-10-13) work on branches `chatgpt/<topic>` and
`gemini/<topic>`, never `main`, by their briefs `openai-dontread.md` and
`gemini-notes.md`. A Claude session ships such a branch with
`./ship.sh --no-tag <branch>` after its review; what they report as run is
checked, not taken. The review goes to the `assistant-review` subagent
(`.claude/agents/`) or a fresh session, never a long conversation (RV1).
`ship.sh` runs `tools.Names` on their branches and prints the names their
text uses that the tree does not declare: a warning, not a gate, since a
passage about the past may name what existed then. DeepSeek is not one of
these: it has no branch and no brief of its own, and a Claude session
drives it (`docs/deepseek.md`).

Since BP1 they ship `--no-tag` and write no `{next}`: their branches take no
version.

## Which model

Jerus, 2026-09-21: the implementer and the docs pass run on Opus, whatever
the job's size. The orchestrating session keeps the gate.

Amended by BP1 item 7 (Jerus, 2026-10-23): a small fix batch - no money,
saves or threading, a few hundred lines - runs on Sonnet as the whole
session, Opus at the gate only. An Opus batch hands its coding to Sonnet
slices (`CLAUDE.md`, "Coding goes to Sonnet"), and the screen-check session
runs on Sonnet unless a check needs judgement. Lesson e (Jerus, 2026-10-24:
"anything on high effort or lower is banned"): every orchestrator, batch
session, review agent, Sonnet slice and screen-check session runs at xhigh
or max. A session cannot raise its own effort, so one started below xhigh
is handed to a fresh one at xhigh.

## The docs pass

By a fresh agent, at most once a day (2026-10-12), over everything merged
since the last one, on its own branch; `docs/docs-pass.md` is its brief,
`Docs pass.bat` runs it headless, `docs/docs-pass-agent.md` is it as a
subagent. It makes every comment, header, index and count agree with the
code, by a context that did not make the change. Its mechanical half is
`tools.Stale` (StaleCheck, in the suite); the pass is for what a tool
cannot judge. A batch is not done until it has run. A manual edition only
at a milestone or when Jerus asks.

Since BP1 it ships `--no-tag`, and may carry a ship file for its changelog
block in `docs/queue/`, named from its branch with each `/` a `-` (on
`docs/pass-2026-10-26`, `docs-pass-2026-10-26-ship.md`; `ship.sh` stops a
branch carrying any other)
and holding no `{next}`; a pass that changes only `.md` files gates on
`--fast`. A `{next}` on a batch branch is not stale: `ship.sh` fills it at
the gate. `Docs pass.bat` runs a bare `claude -p`, which takes the CLI's
default effort: check it before trusting a headless pass (lesson e).

## Before saying a change works

The gate's suite is green but for main's recorded reds (say which: they are
in `docs/notes/gate.txt` and the merge's body), the maps are regenerated,
the ensemble scored if the trace moved, the write-up exists, the docs pass
has run, and the batch's row is out of `QUEUE.md` (`ship.sh` takes it out
in the merge). A batch session calls itself ready when `--pick` exits 0 or
3 and its ship file is pushed, last.

## The calls BP1 left

Each ★ but 2 with its recommendation already built; until Jerus says
otherwise, the built way holds. Call 2 is his, since it moves the standing
rule on Pending.

1. A class most harnesses read (`Game.java` and the like, more than
   `CBS_PICK_FANOUT` readers) is left to the gate's full suite, with
   `LongPlaytest` run in its place, rather than the whole suite run in the
   batch. Recommended: item 3's "one full suite per batch, at the gate".
   `CBS_PICK_FANOUT=999` flips it; it reopens if more than one of the first
   four batches bounces at the gate on a harness so left.
2. Who returns a blocked batch to Queued when its block clears by itself.
   Recommended: the orchestrator, saying so in the queue commit. Not built:
   until he says, only his review moves a Pending row.
3. No more paragraphs in `GameVersion`'s javadoc: the changelog is the one
   record. Recommended.
4. Main's recorded reds pass the gate by default, word for word, and a new
   red only with `--red-ok`, his words and a `--red-line` naming it (TM3's
   review: his words pass the lines he approved, no others). Recommended: his
   own by-hand rule, made mechanical.
5. A timing red that main shows too passes and is named in the merge's body
   (lesson d). Since TM3 "shows too" is read side by side: the red harness
   is timed again with main's classes and the merge's in turn, and the red
   passes unless the merge's median is over main's by more than 15% (step 6
   of the gate). Recommended.

## The lessons of 2026-10-23/24

- (a) 2026-10-23/24: PT1-M4's reds held the Earth lane about six hours. A
  blocked batch now goes under Pending at once, so its lane continues.
- (b) 2026-10-24: batches finished at 11:25 and 13:53 sat unshipped until
  22:45, because the orchestrator is not woken after many automated turns
  in a row with no message from Jerus. Hence the hourly check-in.
- (c) The real Earth is earth-2.bin since 0.8.41 (ET1, 2026-10-24); a gate
  that day named earth-1.bin by mistake, and the Earth harnesses'
  full-file sections then tested the wrong file.
- (d) 2026-10-24: `batch-check.sh` decides soft or hard once, at its start,
  and the PC's speed can change after a restart. So a timing red at the
  gate is compared with main's own run the same day before it counts as
  the batch's (TM3, 2026-10-27: not minutes later, one run to one: the two are
  timed in turn, in the same minutes, and the median decides).
- (e) 2026-10-24, Jerus: "anything on high effort or lower is banned". A
  medium-effort orchestrator had stalled a lane, clashed versions and named
  the wrong Earth file.
- (f) 2026-10-23/24: batches built side by side each took main's version
  plus one, so their versions clashed and they had to re-merge. The
  version is now set at the gate.
